package app;

import java.util.ArrayList;
import java.util.Collection;

/** The class that the deck, hand, drawPile, discardPile, and exhaustPile all implement. Identical
 * to an ArrayList of Cards, except that it does checks of a card's CANENTERPILES value before each
 * addition (if false, not adding the card.)
 * The end of the ArrayList represents the top of the pile, so, e.g., drawing is done by removing
 * the last/most-recently-added element.
 * 
 * Everything else should be self-explanatory and/or boilerplate.
 * 
 * Could implement dequeue, but that adds a lot of methods (=> refactoring overhead) that I'd never
 * use anyway.
 * 
 * @see Combat
 */
public class CardList extends ArrayList<Card> {

  // Automatically calls the accessible no-args ArrayList<Card> constructor
  // (unless otherwise specified, like in CardList(int).)
  public CardList() {}
  public CardList(int capacity) {
    super(capacity);
  }
  public CardList(Collection<? extends Card> c) {
    this(c.size());
    addAll(c);
  }
  
  @Override
  public void add(int index, Card element) {
    if(!element.CANENTERPILES){ return; }
    super.add(index, element);
  }

  @Override
  public boolean add(Card e) {
    if(!e.CANENTERPILES){ return false; }
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
    if(!element.CANENTERPILES){ return; }
    super.addFirst(element);
  }

  /** Identical to add(Card). */
  @Override
  public void addLast(Card element) {
    if(!element.CANENTERPILES){ return; }
    super.addLast(element);
  }

  /** The "addFirst"/"addLast" naming system can be confusing since the first card you draw
   * is the "last" elt in the list. Added these methods for clarity.
   * 
   * Identical to add(Card).
   */
  public void addTop(Card element) {
    addLast(element);
  }
  /** The "addFirst"/"addLast" naming system can be confusing since the first card you draw
   * is the "last" elt in the list. Added these methods for clarity.
   */
  public void addBottom(Card element) {
    addFirst(element);
  }
  /** The "removeFirst"/"removeLast" naming system can be confusing since the first card you draw
   * is the "last" elt in the list. Added these methods for clarity.
   */
  public Card removeTop() {
    return removeLast();
  }
  /** The "removeFirst"/"removeLast" naming system can be confusing since the first card you draw
   * is the "last" elt in the list. Added these methods for clarity.
   */
  public Card removeBottom() {
    return removeFirst();
  }
}
