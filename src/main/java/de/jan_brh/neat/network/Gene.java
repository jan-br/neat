package de.jan_brh.neat.network;

public class Gene {

  private final Genome genome;
  private final GeneType geneType;
  private float charge = 0;
  private float bias = 1;

  protected Gene(Genome genome, GeneType geneType) {
    this.genome = genome;
    this.geneType = geneType;
  }

  public float getOutput() {
    return (float) (1 / (1 + Math.exp(-this.charge)));
  }

  public void input(float value) {
    this.charge += value;
    if (value > this.bias) {
      this.genome
          .getConnectionsToNextLayer(this)
          .forEach(geneConnection -> geneConnection.input(this.getOutput()));
    }
  }
}
