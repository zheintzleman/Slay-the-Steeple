package app;

import java.util.ArrayList;
import java.util.Collection;

/** The class that the deck, hand, drawPile, discardPile, and exhaustPile all implement. Identical
 * to an ArrayList of Cards, except that it does card tangibility checks before each addition (if
 * intangible, not adding the card.)
 * Everything else should be self-explanatory and/or boilerplate.
 * 
 * @see Combat
 */
public class CardList extends ArrayList<Card> {

  // Automatically calls the accessible no-args ArrayList<Card> constructor
  // (unless otherwise specified, like in CardList(int).)
  public CardList(){}
  public CardList(int capacity){
    super(capacity);
  }
  public CardList(Collection<? extends Card> c){
    this(c.size());
    addAll(c);
  }
  
  @Override
  public void add(int index, Card element) {
    if(element.INTANGIBLE){ return; }
    super.add(index, element);
  }

  @Override
  public boolean add(Card e) {
    if(e.INTANGIBLE){ return false; }
    return super.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends Card> c) {
    if (c instanceof CardList) {
      return super.addAll(c);
    }
    for(Card card : c){
      add(card);
    }
    return c.size() > 0;
  }

  /** Throws an UnsupportedOperationException.
   * It would have bad asymptotics unless I put a non-negligible amount of time into writing this
   * method, and I doubt I'd ever use this anyway. If I do, feel free to implement this.
   */
  @Override
  public boolean addAll(int index, Collection<? extends Card> c) {
    throw new UnsupportedOperationException("Not Yet Implemented for CardList.");
  }

  @Override
  public void addFirst(Card element) {
    if(element.INTANGIBLE){ return; }
    super.addFirst(element);
  }

  @Override
  public void addLast(Card element) {
    if(element.INTANGIBLE){ return; }
    super.addLast(element);
  }
}
