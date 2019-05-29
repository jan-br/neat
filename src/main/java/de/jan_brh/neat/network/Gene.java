package de.jan_brh.neat.network;

import java.util.function.Function;

public class Gene implements Cloneable {

  private final int innovation;
  private final Genome genome;
  private final GeneType geneType;
  private float charge = 0;
  private float bias = 0.5f;

  protected Gene(GeneType geneType, int innovation, Genome genome) {
    this.genome = genome;
    this.geneType = geneType;
    this.innovation = innovation;
  }

  public float getOutput() {
    return this.genome.getActivationFunction().apply(this.charge);
  }

  public void input(float value) {
    this.charge += value;
    if (value > this.bias) {
      this.genome
          .getConnectionsToNextLayer(this)
          .forEach(geneConnection -> geneConnection.input(this.getOutput()));
      this.charge = 0;
    }
  }

  public GeneType getGeneType() {
    return geneType;
  }

  public int getInnovation() {
    return innovation;
  }

  public Gene clone(Genome genome) {
    Gene gene = new Gene(this.geneType, this.innovation, genome);
    gene.bias = this.bias;
    return gene;
  }
}
