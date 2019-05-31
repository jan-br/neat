package de.jan_br.neat;

import java.util.function.Consumer;
import java.util.function.Function;

public final class NeatAlgorithmConfiguration {

  private final Function<Number, Float> activationFunction;
  private final float breedCrossChance;
  private final float distanceExcessWeight;
  private final float distanceDisjointWeight;
  private final float distanceWeightsWeight;
  private final float geneDisableChance;
  private final float generationEliminationPercentage;
  private final int inputs;
  private final Consumer<NeatInputAdapter> mainTask;
  private final float mutationWeightChance;
  private final float mutationWeightMaxDisturbance;
  private final float mutationWeightRandomChance;
  private final float mutationWeightChanceRandomRange;
  private final float mutationNewNodeChance;
  private final float mutationNewConnectionChance;
  private final int outputs;
  private final int population;
  private final float speciesCompatibilityDistance;
  private final Function<NeatInputAdapter, Float> trainingTask;

  public NeatAlgorithmConfiguration(
      Function<Number, Float> activationFunction,
      float breedCrossChance,
      float distanceExcessWeight,
      float distanceDisjointWeight,
      float distanceWeightsWeight,
      float geneDisableChance,
      float generationEliminationPercentage,
      int inputs,
      Consumer<NeatInputAdapter> mainTask,
      float mutationWeightChance,
      float mutationWeightMaxDisturbance,
      float mutationWeightRandomChance,
      float mutationWeightChanceRandomRange,
      float mutationNewNodeChance,
      float mutationNewConnectionChance,
      int outputs,
      int population,
      float speciesCompatibilityDistance,
      Function<NeatInputAdapter, Float> trainingTask) {
    this.activationFunction = activationFunction;
    this.breedCrossChance = breedCrossChance;
    this.distanceExcessWeight = distanceExcessWeight;
    this.distanceDisjointWeight = distanceDisjointWeight;
    this.distanceWeightsWeight = distanceWeightsWeight;
    this.geneDisableChance = geneDisableChance;
    this.generationEliminationPercentage = generationEliminationPercentage;
    this.inputs = inputs;
    this.mainTask = mainTask;
    this.mutationWeightChance = mutationWeightChance;
    this.mutationWeightMaxDisturbance = mutationWeightMaxDisturbance;
    this.mutationWeightRandomChance = mutationWeightRandomChance;
    this.mutationWeightChanceRandomRange = mutationWeightChanceRandomRange;
    this.mutationNewNodeChance = mutationNewNodeChance;
    this.mutationNewConnectionChance = mutationNewConnectionChance;
    this.outputs = outputs;
    this.population = population;
    this.speciesCompatibilityDistance = speciesCompatibilityDistance;
    this.trainingTask = trainingTask;
  }

  public Function<Number, Float> getActivationFunction() {
    return activationFunction;
  }

  public float getBreedCrossChance() {
    return breedCrossChance;
  }

  public float getDistanceExcessWeight() {
    return distanceExcessWeight;
  }

  public float getDistanceDisjointWeight() {
    return distanceDisjointWeight;
  }

  public float getDistanceWeightsWeight() {
    return distanceWeightsWeight;
  }

  public float getGeneDisableChance() {
    return geneDisableChance;
  }

  public float getGenerationEliminationPercentage() {
    return generationEliminationPercentage;
  }

  public int getInputs() {
    return inputs;
  }

  public Consumer<NeatInputAdapter> getMainTask() {
    return mainTask;
  }

  public float getMutationWeightChance() {
    return mutationWeightChance;
  }

  public float getMutationWeightMaxDisturbance() {
    return mutationWeightMaxDisturbance;
  }

  public float getMutationWeightRandomChance() {
    return mutationWeightRandomChance;
  }

  public float getMutationWeightChanceRandomRange() {
    return mutationWeightChanceRandomRange;
  }

  public float getMutationNewNodeChance() {
    return mutationNewNodeChance;
  }

  public float getMutationNewConnectionChance() {
    return mutationNewConnectionChance;
  }

  public int getOutputs() {
    return outputs;
  }

  public int getPopulation() {
    return population;
  }

  public float getSpeciesCompatibilityDistance() {
    return speciesCompatibilityDistance;
  }

  public Function<NeatInputAdapter, Float> getTrainingTask() {
    return trainingTask;
  }
}
