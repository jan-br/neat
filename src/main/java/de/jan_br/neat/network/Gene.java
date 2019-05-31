package de.jan_br.neat.network;

public class Gene implements Cloneable {

  private int innovationNumber;
  private final int from;
  private final int to;
  private float weight;
  private boolean enabled;

  public Gene(int innovationNumber, int from, int to, float weight, boolean enabled) {
    this.innovationNumber = innovationNumber;
    this.from = from;
    this.to = to;
    this.weight = weight;
    this.enabled = enabled;
  }

  public int getInnovationNumber() {
    return innovationNumber;
  }

  public void setInnovationNumber(int innovationNumber) {
    this.innovationNumber = innovationNumber;
  }

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public float getWeight() {
    return weight;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  protected Gene clone() {
    return new Gene(innovationNumber, from, to, weight, enabled);
  }

  @Override
  public String toString() {
    return "Gene [innovationNumber=" + innovationNumber + ", from=" + from + ", to=" + to + ", weight=" + weight + ", enabled=" + enabled + "]";
  }
}
