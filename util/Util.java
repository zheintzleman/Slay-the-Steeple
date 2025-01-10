package util;

import java.util.List;

public abstract class Util {
  /** Returns a random element of the given list. Does not modify the list.
   */
  public static <T> T randElt(List<T> list){
    final int rng = (int) (Math.random() * list.size());
    return list.get(rng);
  }
}