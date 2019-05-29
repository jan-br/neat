package de.jan_brh.neat.network;

public class GeneConnection {

  private final int innovation;
  private Gene from;
  private Gene to;
  private float weight;
  private boolean enabled;

  public GeneConnection(int innovation, Gene from, Gene to, float weight) {
    this(innovation, from, to, weight, true);
  }

  public GeneConnection(int innovation, Gene from, Gene to, float weight, boolean enabled) {
    this.innovation = innovation;
    this.from = from;
    this.to = to;
    this.weight = weight;
    this.enabled = enabled;
  }

  public void input(float value) {
    this.to.input(value * this.weight);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public float getWeight() {
    return weight;
  }

  public Gene getFrom() {
    return from;
  }

  public Gene getTo() {
    return to;
  }

  public int getInnovation() {
    return innovation;
  }
}
