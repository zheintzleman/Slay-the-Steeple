package app;

public class EventManager {
  public enum Event{
    ONCARDPLAY,    //
    ONDISCARD, //TODO: Note that this should probably only be called(?) when a card's effect discards this card
    ONEXHAUST,
    ONDRAW,
    ONTURNEND,
    ONPLAYERHURT
  }

  private Run run;
  private Combat combat;

  public EventManager(Run r){
    run = r;
    combat = null;
  }
  public EventManager(Combat c){
    run = c.getRun();
    combat = c;
  }
  public Combat getCombat(){ return combat; }
  public void setCombat(Combat c){ combat = c; }

  public void OnTurnEnd(){
    // Relics.turnStart(...)((...));
    // Statuses.turnStart(...)((...));
    // ^Or just make them all here since there's going to be a bunch of different events anyway?
    //TODO: Put some stuff from (Entity/Enemy).endTurn here?
    // Cards subscribed are taken care of in the combat.endTurn() method
  }

  public void OnPlayerHurt(int hpLoss){
    for(Card card : combat.getCardsInPlay()){
      for(CardEffect eff : card.getEffects()){
        if(eff.whenPlayed() == Event.ONPLAYERHURT){
          combat.playEffect(eff, card);
        }
      }
    }
  }
}
