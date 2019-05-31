package de.jan_br.neat.network;

import com.google.inject.Singleton;

import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class InnovationCounter {

  private AtomicInteger counter = new AtomicInteger(0);

  public int get(){
    return this.counter.getAndIncrement();
  }

}
