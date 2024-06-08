package app;

import app.EventManager.Event;

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
