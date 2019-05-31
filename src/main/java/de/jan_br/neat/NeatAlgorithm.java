package de.jan_br.neat;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.jan_br.neat.network.*;
import de.jan_br.neat.util.RandomUtils;

import java.util.*;

@Singleton
public final class NeatAlgorithm {

  private final Injector injector;
  private final InnovationCounter innovationCounter;
  private final NeatAlgorithmConfiguration neatAlgorithmConfiguration;
  private Population population;
  private int currentGeneration;
  private Genome latestFitness;
  private boolean active = true;

  @Inject
  private NeatAlgorithm(
      Injector injector,
      InnovationCounter innovationCounter,
      NeatAlgorithmConfiguration neatAlgorithmConfiguration) {
    this.injector = injector;
    this.innovationCounter = innovationCounter;
    this.neatAlgorithmConfiguration = neatAlgorithmConfiguration;
    this.population = new Population(this.neatAlgorithmConfiguration);
  }

  public NeatAlgorithm init() {
    this.populate();
    while (this.active) {
      System.out.println("New generation");
      this.newGeneration();
      System.out.println("New generation finished");
      Genome best = this.latestFitness;
      this.neatAlgorithmConfiguration.getMainTask().accept(best::calculate);

      Set<Integer> hiddenNodes = new HashSet<>();
      int enabledConns = 0;

      for (Gene g : best.getGenes()) {
        if (g.isEnabled()) {
          enabledConns++;
        }

        {
          int node = g.getFrom();
          if (!best.isInputNode(node) && !best.isOutputNode(node)) {
            if (!hiddenNodes.contains(node)) {
              hiddenNodes.add(node);
            }
          }
        }
        {
          int node = g.getTo();
          if (!best.isInputNode(node) && !best.isOutputNode(node)) {
            if (!hiddenNodes.contains(node)) {
              hiddenNodes.add(node);
            }
          }
        }
      }

      System.out.println(
          "======================================= Mythan =======================================");
      System.out.println(
          "Solution was found with a fitness of "
              + best.getFitness()
              + " in generation "
              + this.currentGeneration);
      System.out.println(
          "The system had "
              + hiddenNodes.size()
              + " hidden units and "
              + enabledConns
              + " enabled connections");
      System.out.println(
          "======================================================================================");
    }
    return this;
  }

  public void newGeneration() {
    this.currentGeneration++;
    Population population = this.getPopulation();

    Map<Species, List<Genome>> bestPerforming = new HashMap<>();

    for (Species sp : population.getSpecies()) {
      bestPerforming.put(sp, sp.getBestPerforming());
    }

    float sum = 0;
    for (Species sp : population.getSpecies()) {
      sum += sp.getAverageFitness();
    }

    HashMap<Species, Genome> vips = new HashMap<>();
    Iterator<Species> it = population.getSpecies().iterator();
    while (it.hasNext()) {
      Species sp = it.next();

      List<Genome> best = bestPerforming.get(sp);
      if (best == null) throw new AssertionError();

      float remove =
          (float)
              Math.ceil(
                  best.size()
                      * this.neatAlgorithmConfiguration.getGenerationEliminationPercentage());
      int start = (int) (Math.floor(best.size() - remove) + 1);

      for (int i = start; i < best.size(); i++) {
        Genome bad = best.get(i);
        if (sp.getMembers().size() > 1) {
          sp.remove(bad);
        }
      }

      sp.setFailedGenerations(sp.getFailedGenerations() + 1);

      if (sp.getFailedGenerations() > 15) {
        if (sp.getMembers().size() > 1) {
          System.out.println("Species was removed, because it failed for 15 generations.");
          it.remove();
        }
        continue;
      }

      float totalSize = this.neatAlgorithmConfiguration.getPopulation();
      float breedsAllowed = (float) (Math.floor(sp.getAverageFitness() / sum * totalSize) - 1.0f);

      if (breedsAllowed < 1) {
        it.remove();
        continue;
      }

      Genome bestOfSpecies = best.get(0);
    }

    {
      int size = 0;
      for (Species sp : population.getSpecies()) {
        size += sp.getMembers().size();
      }
      System.out.println(
          "Building generation "
              + this.currentGeneration
              + "... Now "
              + population.getSpecies().size()
              + " species active (with a total size of "
              + size
              + ").");
    }

    if (population.getSpecies().isEmpty()) {
      throw new RuntimeException("All species died");
    }

    int populationSize = 0;

    Map<Species, Set<Genome>> oldMembers = new HashMap<>();
    for (Species sp : population.getSpecies()) {
      oldMembers.put(sp, new HashSet<>(sp.getMembers()));

      sp.getMembers().clear();

      Genome vip = vips.get(sp);
      if (vip != null) {
        sp.getMembers().add(vip);
        populationSize++;
      }
    }

    while (populationSize < this.neatAlgorithmConfiguration.getPopulation()) {
      Species randomSpecies = RandomUtils.randomItem(population.getSpecies());
      Set<Genome> oldMems = oldMembers.get(randomSpecies);

      if (oldMems != null) {
        if (RandomUtils.success(this.neatAlgorithmConfiguration.getBreedCrossChance())) {
          Genome father = RandomUtils.randomItem(oldMems);
          Genome mother = RandomUtils.randomItem(oldMems);

          Genome.crossAndAdd(father, mother);
        } else {
          Genome g = RandomUtils.randomItem(oldMems).clone();
          g.mutate();
          randomSpecies.getMembers().add(g);
        }
        populationSize++;
      }
    }

    population.getSpecies().removeIf(sp -> sp.getMembers().isEmpty());

    for (Species sp : population.getSpecies()) {
      sp.update();
    }

    this.latestFitness = population.getBestPerforming();

    System.out.println(
        "Best performing genome had fitness of "
            + this.latestFitness.getFitness()
            + " and was part of species "
            + this.latestFitness.getSpecies().getId()
            + " which has "
            + this.latestFitness.getSpecies().getMembers().size()
            + " members");
  }

  public NeatAlgorithmConfiguration getNeatAlgorithmConfiguration() {
    return neatAlgorithmConfiguration;
  }

  public InnovationCounter getInnovationCounter() {
    return innovationCounter;
  }

  public Population getPopulation() {
    return population;
  }

  private void populate() {
    Genome init = this.injector.getInstance(Genome.class);
    for (int i = 0; i < this.getNeatAlgorithmConfiguration().getPopulation(); i++) {
      Genome genome = init.clone();
      for (Gene gene : genome.getGenes()) {
        float dist = this.neatAlgorithmConfiguration.getMutationWeightChanceRandomRange();
        gene.setWeight(RandomUtils.randomValue(-dist, dist));
      }
      this.getPopulation().addGenome(genome);
    }
  }
}
