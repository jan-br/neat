package de.jan_brh.neat.network;

public class GeneConnection {

  private Gene from;
  private Gene to;
  private float weight;
  private boolean enabled = true;

  public GeneConnection(Gene from, Gene to, float weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  public void input(float value) {
    this.to.input(value * this.weight);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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
}
