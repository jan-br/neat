package de.jan_br.neat.network;

import de.jan_br.neat.NeatAlgorithmConfiguration;
import de.jan_br.neat.util.RandomUtils;

import java.util.*;

public class Species {

  private static int speciesCount = 0;
  private final NeatAlgorithmConfiguration neatAlgorithmConfiguration;
  private final int id = speciesCount++;
  private Genome representative;
  private final Set<Genome> members = new HashSet<>();
  private float highestFitness = 0;
  private int failedGenerations = 0;

  public Species(NeatAlgorithmConfiguration neatAlgorithmConfiguration, Genome representative) {
		this.neatAlgorithmConfiguration = neatAlgorithmConfiguration;
		this.representative = representative;
    representative.setSpecies(this);
  }

  public int getId() {
    return id;
  }

  public float getHighestFitness() {
    return highestFitness;
  }

  public void setHighestFitness(float highestFitness) {
    this.highestFitness = highestFitness;
    this.failedGenerations = 0;
  }

  public int getFailedGenerations() {
    return failedGenerations;
  }

  public void setFailedGenerations(int failedGenerations) {
    this.failedGenerations = failedGenerations;
  }

  public Genome getRepresentative() {
    return representative;
  }

  public void setRepresentative(Genome representative) {
    this.representative = representative;
  }

  public boolean isCompatible(Genome genome) {
    return Genome.distance(this.representative, genome)
        <= this.neatAlgorithmConfiguration.getSpeciesCompatibilityDistance();
  }

  public float getAverageFitness() {
    float total = 0;
    float counter = 0;
    for (Genome g : this.members) {
      total += g.getFitness();
      counter++;
    }
    return total / counter;
  }

  public List<Genome> getBestPerforming() {
    List<Genome> bestPerforming = new ArrayList<>();

    bestPerforming.addAll(this.members);
    Collections.sort(bestPerforming, new Genome.GenomeSorter());
    return bestPerforming;
  }

  public void remove(Genome g) {
    this.members.remove(g);
  }

  public Set<Genome> getMembers() {
    return members;
  }

  public void update() {
    this.setRepresentative(RandomUtils.randomItem(this.getMembers()));
  }
}
