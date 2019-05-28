package de.jan_brh.neat.network;

public final class IncrementalCounter {

  private int count = 0;

  public int get(){
    count++;
    return count;
  }

}
