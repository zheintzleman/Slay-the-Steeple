package app;

import app.EventManager.Event;

/** An extension of the Effect class, specifically for effects made by cards.
 * Used for what cards do when played, as well as other events they might do (such as Blood for
 * Blood decreasing in cost on player hurt.)
 * Stores a reference to the card holding this effect.
 * Unable to instantiate effects from the respective blacklist in EventManager.
 * 
 * @see Effect
 * @see Card
 * @see EventManager EventManager.BANNED_CARD_EFFECTS
 */
public class CardEffect extends Effect {
  private Card card;

  public CardEffect(String data, Card card){
    super(data);
    this.card = card;
    App.ASSERT(!isBanned(whenPlayed()));
  }
  public CardEffect(CardEffect eff, Card card){
    super(eff);
    this.card = card;
    App.ASSERT(!isBanned(whenPlayed()));
  }
  public Card getCard() { return card; }
  public void setCard(Card card) { this.card = card; }

  private static boolean isBanned(Event playEvent){
    return EventManager.BANNED_CARD_EFFECTS.contains(playEvent);
  }
}
