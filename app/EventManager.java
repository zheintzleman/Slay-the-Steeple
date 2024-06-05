package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    playStatusEffects(Event.ONTURNEND);

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
          shouldDiscard = shouldDiscard && Combat.c.playEffect(eff);
        }
      }
      if(shouldDiscard){
        Combat.c.discardCardFromHand(card);
      }
    }
  }

  public void OnLoseHP(Entity victim, int hpLoss){
    // Note: If entity killed, hpLoss parameter not appropriately decreased.
    if(victim == Combat.c.getPlayer()){
      OnPlayerHurt(hpLoss);
    }
    //TODO: playStatusEffects(Event.ONLOSEHP, Collections.singletonList(victim));
  }

  private void OnPlayerHurt(int hpLoss){
    for(Card card : Combat.c.getCardsInPlay()){
      for(CardEffect eff : card.getEffects()){
        if(eff.whenPlayed() == Event.ONPLAYERHURT){
          Combat.c.playEffect(eff);
        }
      }
    }
    playStatusEffects(Event.ONPLAYERHURT);
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

  
  /**Plays all status effects (on all entities) that were initialized with the respective event;
   * e.g. with (OnTurnEnd) for event == Event.ONTURNEND.
   * @param event The Event enum elt. to call
   */
  private void playStatusEffects(Event event){
    playStatusEffects(event, Combat.c.getEntities());
  }
  /**Plays all status effects that were initialized with the respective event;
   * e.g. with (OnTurnEnd) for event == Event.ONTURNEND.
   * @param event The Event enum elt. to call
   * @param entities The list of entities to call on
   */
  private void playStatusEffects(Event event, List<? extends Entity> entities){
    for(Entity entity : entities){
      if(entity.isDead()) continue;
      for(Status stat : entity.getStatuses()){
        for(Effect eff : stat.getEffects()){
          if(eff.whenPlayed() == event){
            Combat.c.playEffect(eff);
          }
        }
      }
    }
  }

  // TODO: Make a global function? Or does the need for int hpLoss/etc. make that not worth it?
  
  // Along with editing these functions for card/relic/w/e effects, can edit:
  // - Entity.calcAttackDamage / Entity.calcAtkDmgFromThisStats
  // - Combat.c.playCard / Combat.c.playEffect
  // - Card.Description constructor
}
