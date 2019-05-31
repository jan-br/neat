package de.jan_br.neat.util;

import java.util.Map;

public interface FunctionalMap<K, V> extends Map<K, V> {

  void put(V value);

}
