package app;

import java.util.ArrayList;

public class EventManager {
  public enum Event{
    ONCARDPLAY,    //
    ONDISCARD, //TODO: Note that this should probably only be called(?) when a(ny) card's effect discards this card
    ONEXHAUST,
    ONDRAW,
    ONTURNEND,
    ONPLAYERHURT,
    ONATKDMGDEALT
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
    // Cards subscribed are taken care of in the combat.endTurn() method, when discarded

    // Calls ONTURNEND card effects from cards in hand; discards them if appropriate.
    // Each card discarded immediately after "play", for canon continuity.
    ArrayList<Card> hand = combat.getHand();
    while(hand.size() != 0){
      Card card = hand.get(0);
      // Tracks whether the card has been removed from hand already or if it needs to be discarded still.
      // todo: Just check whether or not it's still in hand?
      boolean shouldDiscard = true;

      for(CardEffect eff : card.getEffects()){
        if(eff.whenPlayed() == Event.ONTURNEND){
          //Plays any ONTURNEND card effects
          //If the effect returns false (card should not discard), shouldDiscard set to false.
          shouldDiscard = shouldDiscard && combat.playEffect(eff, card);
        }
      }
      if(shouldDiscard){
        combat.discardCardFromHand(card);
      }
    }
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

  // TODO: Make a global function? Or does the need for int hpLoss/etc. make that not worth it?
  
  // Along with editing these functions for card/relic/w/e effects, can edit:
  // - Entity.calcAttackDamage / Entity.calcAtkDmgFromThisStats
  // - Combat.playCard / Combat.playEffect
  // - Card.Description constructor
}
