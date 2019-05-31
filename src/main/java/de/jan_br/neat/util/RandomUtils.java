package de.jan_br.neat.util;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtils {

  private static final Random random = new Random();

  public static <T> T randomItem(T[] array) {
    Preconditions.checkArgument(array.length > 0, "Array must not be empty.");
    return array[random.nextInt(array.length)];
  }

  public static <T> T randomItem(Set<T> set) {
    Preconditions.checkArgument(!set.isEmpty(), "Set must not be empty.");

    int item = random.nextInt(set.size());
    int count = 0;
    for (T t : set) {
      if (count == item) return t;
      count++;
    }
    throw new AssertionError();
  }

  public static <T> T randomItem(List<T> list) {
    Preconditions.checkArgument(!list.isEmpty(), "List must not be empty.");
    return list.get(random.nextInt(list.size()));
  }

  public static boolean success(float chance) {
    return random.nextDouble() <= chance;
  }

  public static float randomValue(float min, float max) {
    Preconditions.checkArgument(min < max, "min has to be smaller than max.");
    return min + (max - min) * random.nextFloat();
  }
}
