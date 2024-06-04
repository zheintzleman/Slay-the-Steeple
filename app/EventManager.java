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

  /** Singleton EventManager Instance */
  public static final EventManager em = new EventManager();

  private EventManager(){
  }

  public void OnTurnEnd(){
    // Relics.turnStart(...)((...));
    // Status.turnStart(...)((...));
    // ^Or just make them all here since there's going to be a bunch of different events anyway?
    //TODO: Put some stuff from (Entity/Enemy).endTurn here?
    // Cards subscribed are taken care of in the Combat.c.endTurn() method, when discarded

    // Calls ONTURNEND card effects from cards in hand; discards them if appropriate.
    // Each card discarded immediately after "play", for canon continuity.
    ArrayList<Card> hand = Combat.c.getHand();
    while(hand.size() != 0){
      Card card = hand.get(0);
      // Tracks whether the card has been removed from hand already or if it needs to be discarded still.
      // todo: Just check whether or not it's still in hand?
      boolean shouldDiscard = true;

      for(CardEffect eff : card.getEffects()){
        if(eff.whenPlayed() == Event.ONTURNEND){
          //Plays any ONTURNEND card effects
          //If the effect returns false (card should not discard), shouldDiscard set to false.
          shouldDiscard = shouldDiscard && Combat.c.playEffect(eff, card);
        }
      }
      if(shouldDiscard){
        Combat.c.discardCardFromHand(card);
      }
    }
  }

  public void OnPlayerHurt(int hpLoss){
    for(Card card : Combat.c.getCardsInPlay()){
      for(CardEffect eff : card.getEffects()){
        if(eff.whenPlayed() == Event.ONPLAYERHURT){
          Combat.c.playEffect(eff, card);
        }
      }
    }
  }

  public void OnAtkDmgDealt(Entity victim, int damage){
    if(victim.hasStatus("Curl Up")){ //Curl Up
      victim.addBlock(victim.getStatusStrength("Curl Up"));
      victim.setStatusStrength("Curl Up", 0);
    }
    if(victim.hasStatus("Angry")){
      victim.addStatusStrength("Strength", victim.getStatusStrength("Angry"));
      System.out.println("Victim Anger: " + victim.getStatusStrength("Angry"));
    }
  }

  public void OnAttackFinished(Entity attacker){
    //todo: If we never add anything else to this event, can change it so the attack() function
    //Just directly reduces vigor -- the entity copy system will make it so the statuses don't
    //apply until after all of the attacks are done.
    attacker.setStatusStrength("Vigor", 0);
  }

  // TODO: Make a global function? Or does the need for int hpLoss/etc. make that not worth it?
  
  // Along with editing these functions for card/relic/w/e effects, can edit:
  // - Entity.calcAttackDamage / Entity.calcAtkDmgFromThisStats
  // - Combat.c.playCard / Combat.c.playEffect
  // - Card.Description constructor
}
