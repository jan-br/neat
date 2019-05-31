package de.jan_br.neat.util;

import com.google.common.base.Preconditions;

import java.util.TreeMap;
import java.util.function.Function;

public class FunctionalTreeMap<K, V> extends TreeMap<K, V> implements FunctionalMap<K, V> {

  private final Function<V, K> function;

  public FunctionalTreeMap(Function<V, K> function){
    Preconditions.checkNotNull(function, "Convert function must not be null.");
    this.function = function;
  }

  public void put(V value){
    Preconditions.checkNotNull(value, "Inserted value must not be null.");
    K key = this.function.apply(value);
    Preconditions.checkNotNull(key, "Resultating key must not be null.");
    this.put(key, value);
  }


}
