package util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Util {
  /** Returns a random element of the given list. Does not modify the list.
   * @return A random element of the list, or null if the list is empty.
   */
  public static <T> T randElt(List<T> list){
    if(list.isEmpty()){ return null; }
    final int rng = (int) (Math.random() * list.size());
    return list.get(rng);
  }
  /** Creates a (shallow) copy of the collection, containing only the elements
   * for which p is true.
   */
  public static <T> ArrayList<T> filter(List<T> list, Predicate<T> p){
    ArrayList<T> copy = new ArrayList<T>(list);
    copy.removeIf(p.negate());
    return copy;
  }
}
