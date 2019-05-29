package de.jan_brh.neat;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import de.jan_brh.neat.network.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class NeatAlgorithm {

  private final double c1 = 1, c2 = 1, c3 = 1;
  private final float mutationRate;
  private final float addConnectionRate;
  private final float addNodeRate;
  private final Collection<Species> species = new CopyOnWriteArrayList<>();
  private final Collection<Genome> genomes = new CopyOnWriteArrayList<>();
  private final IncrementalCounter geneConnectionCounter = new IncrementalCounter();
  private final Random random = new Random();
  private final Function<NeatInputAdapter, Float> neatTask;
  private final Runnable clearAction;
  private final Runnable initAction;
  private final Function<Float, Float> activationFunction;
  private final int inputs;
  private final int outputs;
  private final int generationSize;
  private boolean running;

  protected NeatAlgorithm(
      float mutationRate,
      float addConnectionRate,
      float addNodeRate,
      Function<NeatInputAdapter, Float> neatTask,
      Runnable clearAction,
      Runnable initAction,
      Function<Float, Float> activationFunction,
      int inputs,
      int outputs,
      int generationSize) {
    this.mutationRate = mutationRate;
    this.addConnectionRate = addConnectionRate;
    this.addNodeRate = addNodeRate;
    this.neatTask = neatTask;
    this.clearAction = clearAction;
    this.initAction = initAction;
    this.activationFunction = activationFunction;
    this.inputs = inputs;
    this.outputs = outputs;
    this.generationSize = generationSize;
  }

  public void start() {
    if (this.running) throw new IllegalStateException("Algorithm is already active.");
    this.running = true;
    ListeningExecutorService listeningExecutorService =
        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    listeningExecutorService.execute(
        () -> {
          this.initAction.run();

          for (int i = 0; i < this.generationSize; i++) {
            this.genomes.add(createEmptyGenome());
          }
          for (Genome genome : genomes) {
            for (Gene input : genome.getInputs()) {
              for (Gene output : genome.getOutputs()) {
                genome.addConnection(input, output, 1);
              }
            }
          }

          while (this.running) {
            Map<Genome, Float> scores = new HashMap<>();

            try {
              for (Genome genome : this.genomes) {
                System.out.println("Genome has input genes... " + genome.getInputs().size());
                Float score = this.neatTask.apply(genome::process);
                scores.put(genome, score);
                Thread.sleep(500);
                System.out.println("Finish iteration.. Cleaning up for next run..");
                this.clearAction.run();
              }
              System.out.println("Finished generation... reached scores " + scores.values());
              System.out.println("Speciate...");
              System.out.println("Preparing next run...");
              prepareNextRun();
              //              this.speciate();
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        });
  }

  private void prepareNextRun() {
    Collection<Genome> genomes = new LinkedList<>();
    System.out.println("Genome Size " + this.genomes.size());
    Genome fittestGenome =
        this.genomes.stream()
            .max((o1, o2) -> Float.compare(o1.getLatestScore(), o2.getLatestScore()))
            .get();
    System.out.println("Printing fittest genome..");
    GenomePrinter.printGenome(fittestGenome, "fittest.png");

    while (genomes.size() < this.generationSize - 1) {
      Genome p1 = this.getRandomGenomeBiasedAdjustedFitness();
      Genome p2 = this.getRandomGenomeBiasedAdjustedFitness();

      Genome child;
      if (p1.getLatestScore() >= p2.getLatestScore()) {
        child = this.crossOver(p1, p2);
      } else {
        child = this.crossOver(p2, p1);
      }
      if (random.nextFloat() < this.mutationRate) {
        child.mutation();
      }
      if (random.nextFloat() < this.addConnectionRate) {
        child.addConnectionMutation(10);
      }
      if (random.nextFloat() < this.addNodeRate) {
        child.addNodeMutation();
      }
      genomes.add(child);
    }
    genomes.add(fittestGenome.clone());
    this.genomes.clear();
    this.genomes.addAll(genomes);
  }

  private Genome getRandomGenomeBiasedAdjustedFitness() {
    double completeWeight =
        0.0; // sum of probablities of selecting each genome - selection is more probable for
    // genomes with higher fitness
    for (Genome genome : this.getGenomes()) {
      completeWeight += genome.getLatestScore();
    }
    double r = Math.random() * completeWeight;
    double countWeight = 0.0;
    for (Genome genome : this.getGenomes()) {
      System.out.println("DEBUG");
      countWeight += genome.getLatestScore();
      if (countWeight >= r) {
        return genome;
      }
    }
    throw new RuntimeException(
        "Couldn't find a genome... Number is genomes in selected species is "
            + this.generationSize
            + ", and the total adjusted fitness is "
            + completeWeight);
  }

  private void speciate() {
    this.species.clear();
    this.genomes.forEach(genome -> genome.setSpecies(null));
    this.genomes.forEach(genome -> genome.setSpecies(this.getSpecies(genome)));
  }

  public Species getSpecies(Genome genome) {
    for (Species species : this.species) {
      List<Genome> genomesBySpecies = getGenomesBySpecies(species);
      if (!genomesBySpecies.isEmpty()) {
        if (Math.abs(genomesBySpecies.get(0).distance(genome)) > 0.8) {
          return species;
        }
      }
    }
    System.out.println("Create new species...");
    Species species = new Species();
    this.species.add(species);
    return species;
  }

  public List<Genome> getGenomesBySpecies(Species species) {
    return this.genomes.stream()
        .filter(genome -> genome.getSpecies() != null && genome.getSpecies().equals(species))
        .collect(Collectors.toList());
  }

  public Collection<Genome> getGenomes() {
    return genomes;
  }

  public IncrementalCounter getGeneConnectionCounter() {
    return geneConnectionCounter;
  }

  public void stop() {
    if (!this.running) throw new IllegalStateException("Algorithm is already inactive.");
    this.running = false;
  }

  public Genome createEmptyGenome() {
    Genome genome = new Genome(this, this.geneConnectionCounter, this.activationFunction);
    for (int j = 0; j < this.inputs; j++) {
      genome.addGene(GeneType.INPUT);
    }
    for (int j = 0; j < this.outputs; j++) {
      genome.addGene(GeneType.OUTPUT);
    }
    return genome;
  }

  private GeneConnection getConnection(GeneConnection con) {
    GeneConnection c = new GeneConnection(0, con.getFrom(), con.getTo(), con.getWeight());
    c.setEnabled(con.isEnabled());
    return c;
  }

  public Genome crossOver(Genome parent1, Genome parent2) {
    Genome child = new Genome(this, geneConnectionCounter, activationFunction);

    for (Gene parent1Node : parent1.getGenes()) {
      child.addGene(parent1Node.clone(child));
    }

    for (GeneConnection parent1Node : parent1.getConnections()) {
      if (parent2.getConnections().stream()
          .anyMatch(
              search -> parent1Node.getInnovation() == search.getInnovation())) { // matching gene

        GeneConnection parent2Node = parent2.getConnections().stream()
            .filter(search -> search.getInnovation() == parent1Node.getInnovation())
            .findAny()
            .get();
        GeneConnection childConGene =
            this.random.nextBoolean()
                ? new GeneConnection(
                    parent1Node.getInnovation(),
                    child.getGene(parent1Node.getFrom().getInnovation()),
                    child.getGene(parent1Node.getTo().getInnovation()),
                    parent1Node.getWeight(),
                    parent1Node.isEnabled())
                :
                new GeneConnection(
                    parent2Node.getInnovation(),
                    child.getGene(parent2Node.getFrom().getInnovation()),
                    child.getGene(parent2Node.getTo().getInnovation()),
                    parent2Node.getWeight(),
                    parent2Node.isEnabled());

        child.addConnection(childConGene);
      } else { // disjoint or excess gene
        child.addConnection(child.getGene(parent1Node.getFrom().getInnovation()), child.getGene(parent1Node.getTo().getInnovation()), parent1Node.getWeight());
      }
    }

    return child;
  }

  public double getC1() {
    return c1;
  }

  public double getC2() {
    return c2;
  }

  public double getC3() {
    return c3;
  }
}
