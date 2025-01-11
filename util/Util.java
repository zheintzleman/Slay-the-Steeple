package util;

import java.util.List;

public abstract class Util {
  /** Returns a random element of the given list. Does not modify the list.
   * @return A random element of the list, or null if the list is empty.
   */
  public static <T> T randElt(List<T> list){
    if(list.isEmpty()){ return null; }
    final int rng = (int) (Math.random() * list.size());
    return list.get(rng);
  }
}
