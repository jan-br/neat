package de.jan_brh.neat.network;

public class Gene {

  private final Genome genome;
  private final GeneType geneType;
  private float charge = 0;
  private float bias = 1;
  private final int id;

  protected Gene(Genome genome, GeneType geneType, int id) {
    this.genome = genome;
    this.geneType = geneType;
    this.id = id;
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

  public int getId() {
    return id;
  }
}
