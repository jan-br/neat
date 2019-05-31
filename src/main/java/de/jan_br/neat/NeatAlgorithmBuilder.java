package de.jan_br.neat;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import de.jan_br.neat.network.Genome;
import de.jan_br.neat.network.DefaultGenomeFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class NeatAlgorithmBuilder {

  private Injector injector;
  private Class<? extends Provider<Genome>> genomeProvider = DefaultGenomeFactory.class;
  private Function<Number, Float> activationFunction =
      x -> (float) (1f / (1f + Math.exp(-x.floatValue())));
  private float breedCrossChance = 0.75f;
  private float distanceExcessWeight = 1.0f;
  private float distanceDisjointWeight = 1.0f;
  private float distanceWeightsWeight = 0.4f;
  private float geneDisableChance = 0.75f;
  private float generationEliminationPercentage = 0.9f;
  private int inputs;
  private Consumer<NeatInputAdapter> mainTask;
  private float mutationWeightChance = 0.8f;
  private float mutationWeightMaxDisturbance = 0.25f;
  private float mutationWeightRandomChance = 0.1f;
  private float mutationWeightChanceRandomRange = 5.0f;
  private float mutationNewNodeChance = 0.03f;
  private float mutationNewConnectionChance = 0.05f;
  private int outputs;
  private int population = 5;
  private float speciesCompatibilityDistance = 0.8f;
  private Function<NeatInputAdapter, Float> trainingTask;

  private NeatAlgorithmBuilder() {}

  private NeatAlgorithmConfiguration getNeatAlgorithmConfiguration() {
    return new NeatAlgorithmConfiguration(
        this.activationFunction,
        this.breedCrossChance,
        this.distanceExcessWeight,
        this.distanceDisjointWeight,
        this.distanceWeightsWeight,
        this.geneDisableChance,
        this.generationEliminationPercentage,
        this.inputs,
        this.mainTask,
        this.mutationWeightChance,
        this.mutationWeightMaxDisturbance,
        this.mutationWeightRandomChance,
        this.mutationWeightChanceRandomRange,
        this.mutationNewNodeChance,
        this.mutationNewConnectionChance,
        this.outputs,
        this.population,
        this.speciesCompatibilityDistance,
        this.trainingTask);
  }

  public Injector getInjector() {
    return injector;
  }

  public NeatAlgorithmBuilder setInjector(Injector injector) {
    this.injector = injector;
    return this;
  }

  public Class<? extends Provider<Genome>> getGenomeProvider() {
    return genomeProvider;
  }

  public NeatAlgorithmBuilder setGenomeProvider(Class<? extends Provider<Genome>> genomeProvider) {
    this.genomeProvider = genomeProvider;
    return this;
  }

  public Function<Number, Float> getActivationFunction() {
    return activationFunction;
  }

  public NeatAlgorithmBuilder setActivationFunction(Function<Number, Float> activationFunction) {
    this.activationFunction = activationFunction;
    return this;
  }

  public float getBreedCrossChance() {
    return breedCrossChance;
  }

  public NeatAlgorithmBuilder setBreedCrossChance(float breedCrossChance) {
    this.breedCrossChance = breedCrossChance;
    return this;
  }

  public float getDistanceExcessWeight() {
    return distanceExcessWeight;
  }

  public NeatAlgorithmBuilder setDistanceExcessWeight(float distanceExcessWeight) {
    this.distanceExcessWeight = distanceExcessWeight;
    return this;
  }

  public float getDistanceDisjointWeight() {
    return distanceDisjointWeight;
  }

  public NeatAlgorithmBuilder setDistanceDisjointWeight(float distanceDisjointWeight) {
    this.distanceDisjointWeight = distanceDisjointWeight;
    return this;
  }

  public float getDistanceWeightsWeight() {
    return distanceWeightsWeight;
  }

  public NeatAlgorithmBuilder setDistanceWeightsWeight(float distanceWeightsWeight) {
    this.distanceWeightsWeight = distanceWeightsWeight;
    return this;
  }

  public float getGeneDisableChance() {
    return geneDisableChance;
  }

  public NeatAlgorithmBuilder setGeneDisableChance(float geneDisableChance) {
    this.geneDisableChance = geneDisableChance;
    return this;
  }

  public float getGenerationEliminationPercentage() {
    return generationEliminationPercentage;
  }

  public NeatAlgorithmBuilder setGenerationEliminationPercentage(float generationEliminationPercentage) {
    this.generationEliminationPercentage = generationEliminationPercentage;
    return this;
  }

  public int getInputs() {
    return inputs;
  }

  public NeatAlgorithmBuilder setInputs(int inputs) {
    this.inputs = inputs;
    return this;
  }

  public Consumer<NeatInputAdapter> getMainTask() {
    return mainTask;
  }

  public NeatAlgorithmBuilder setMainTask(Consumer<NeatInputAdapter> mainTask) {
    this.mainTask = mainTask;
    return this;
  }

  public float getMutationWeightChance() {
    return mutationWeightChance;
  }

  public NeatAlgorithmBuilder setMutationWeightChance(float mutationWeightChance) {
    this.mutationWeightChance = mutationWeightChance;
    return this;
  }

  public float getMutationWeightMaxDisturbance() {
    return mutationWeightMaxDisturbance;
  }

  public NeatAlgorithmBuilder setMutationWeightMaxDisturbance(float mutationWeightMaxDisturbance) {
    this.mutationWeightMaxDisturbance = mutationWeightMaxDisturbance;
    return this;
  }

  public float getMutationWeightRandomChance() {
    return mutationWeightRandomChance;
  }

  public NeatAlgorithmBuilder setMutationWeightRandomChance(float mutationWeightRandomChance) {
    this.mutationWeightRandomChance = mutationWeightRandomChance;
    return this;
  }

  public float getMutationWeightChanceRandomRange() {
    return mutationWeightChanceRandomRange;
  }

  public NeatAlgorithmBuilder setMutationWeightChanceRandomRange(float mutationWeightChanceRandomRange) {
    this.mutationWeightChanceRandomRange = mutationWeightChanceRandomRange;
    return this;
  }

  public float getMutationNewNodeChance() {
    return mutationNewNodeChance;
  }

  public NeatAlgorithmBuilder setMutationNewNodeChance(float mutationNewNodeChance) {
    this.mutationNewNodeChance = mutationNewNodeChance;
    return this;
  }

  public float getMutationNewConnectionChance() {
    return mutationNewConnectionChance;
  }

  public NeatAlgorithmBuilder setMutationNewConnectionChance(float mutationNewConnectionChance) {
    this.mutationNewConnectionChance = mutationNewConnectionChance;
    return this;
  }

  public int getOutputs() {
    return outputs;
  }

  public NeatAlgorithmBuilder setOutputs(int outputs) {
    this.outputs = outputs;
    return this;
  }

  public int getPopulation() {
    return population;
  }

  public NeatAlgorithmBuilder setPopulation(int population) {
    this.population = population;
    return this;
  }

  public float getSpeciesCompatibilityDistance() {
    return speciesCompatibilityDistance;
  }

  public NeatAlgorithmBuilder setSpeciesCompatibilityDistance(float speciesCompatibilityDistance) {
    this.speciesCompatibilityDistance = speciesCompatibilityDistance;
    return this;
  }

  public Function<NeatInputAdapter, Float> getTrainingTask() {
    return trainingTask;
  }

  public NeatAlgorithmBuilder setTrainingTask(Function<NeatInputAdapter, Float> trainingTask) {
    this.trainingTask = trainingTask;
    return this;
  }

  public NeatAlgorithm build() {
    AbstractModule abstractModule =
        new AbstractModule() {
          protected void configure() {
            this.bind(NeatAlgorithmConfiguration.class).toInstance(getNeatAlgorithmConfiguration());
            this.bind(Genome.class).toProvider(genomeProvider);
          }
        };

    if (injector == null) {
      injector = Guice.createInjector(abstractModule);
    } else {
      injector = injector.createChildInjector(abstractModule);
    }
    return injector.getInstance(NeatAlgorithm.class);
  }

  public static NeatAlgorithmBuilder newBuilder() {
    return new NeatAlgorithmBuilder();
  }
}
