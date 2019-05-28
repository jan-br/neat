package de.jan_brh.neat.network;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Genome {

  private final Collection<Gene> genes = new CopyOnWriteArrayList<>();
  private final Collection<GeneConnection> geneConnections = new CopyOnWriteArrayList<>();
  private final IncrementalCounter geneCounter = new IncrementalCounter();
  private final IncrementalCounter geneConnectionCounter = new IncrementalCounter();
  private final Function<Float, Float> activationFunction;

  public Genome(Function<Float, Float> activationFunction) {
    this.activationFunction = activationFunction;
  }

  public Genome addGene(GeneType type) {
    Gene gene = new Gene(this, type, this.geneCounter.get());
    System.out.println("Added Gene " + gene.getId() + " with type " + type);
    this.genes.add(gene);
    return this;
  }

  public Genome addConnection(Gene from, Gene to, int weight) {
    System.out.println(
        "Added Connection from " + from.getId() + " to " + to.getId() + " with " + weight);
    this.geneConnections.add(new GeneConnection(from, to, weight));
    return this;
  }

  public Collection<GeneConnection> getConnectionToFrontLayer(Gene gene) {
    return this.geneConnections.stream()
        .filter(geneConnection -> geneConnection.getTo().equals(gene))
        .sorted(Comparator.comparingInt(connection -> connection.getFrom().getId()))
        .collect(Collectors.toList());
  }

  public Collection<GeneConnection> getConnectionsToNextLayer(Gene gene) {
    return this.geneConnections.stream()
        .filter(geneConnection -> geneConnection.getFrom().equals(gene))
        .sorted(Comparator.comparingInt(connection -> connection.getTo().getId()))
        .collect(Collectors.toList());
  }

  public Collection<Gene> getGenes() {
    return genes;
  }

  public Collection<Gene> getInputs() {
    return this.genes.stream()
        .filter(gene -> gene.getGeneType() == GeneType.INPUT)
        .sorted(Comparator.comparingInt(Gene::getId))
        .collect(Collectors.toList());
  }

  public Collection<Gene> getOutputs() {
    return this.genes.stream()
        .filter(gene -> gene.getGeneType() == GeneType.OUTPUT)
        .sorted(Comparator.comparingInt(Gene::getId))
        .collect(Collectors.toList());
  }

  public Collection<GeneConnection> getGeneConnections() {
    return geneConnections;
  }

  public synchronized float[] process(float[] input) {
    Collection<Gene> inputs = this.getInputs();
    if (input.length != inputs.size()) {
      new IllegalArgumentException("Input array must have a length of " + inputs.size())
          .printStackTrace();
      System.exit(1);
    }
    int counter = 0;
    for (Gene gene : inputs) {
      gene.input(input[counter++]);
    }
    Collection<Gene> outputs = getOutputs();
    float[] result = new float[outputs.size()];
    counter = 0;
    for (Gene output : outputs) {
      result[counter++] = output.getOutput();
    }
    return result;
  }

  public Function<Float, Float> getActivationFunction() {
    return activationFunction;
  }
}
