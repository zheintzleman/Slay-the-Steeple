package app;

public class CardEffect extends Effect {
  private Card card;

  public CardEffect(String data, Card card){
    super(data);
    this.card = card;
  }
  public Card getCard() { return card; }
  public void setCard(Card card) { this.card = card; }
}
