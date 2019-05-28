package de.jan_brh.neat.network;

public class GeneConnection {

  private Gene from;
  private Gene to;
  private float weight = 1;

  public void input(float value) {
    this.to.input(value * this.weight);
  }

  public Gene getFrom() {
    return from;
  }

  public Gene getTo() {
    return to;
  }
}
