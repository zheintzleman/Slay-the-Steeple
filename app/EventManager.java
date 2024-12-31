package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Singleton class to provide easy access to various different points in the program, such as
 * when a card is exhausted, when the turn ends, or when the player is hurt. Just contains
 * appropritely-named methods which are called at the respective point in the code.
 * Most such methods play all card/status effects associated with a specific event, so helper
 * functions have been created to do so automatically.
 * 
 * @see Effect Effect / CardEffect / StatusEffect
 * @see Combat
 */
public class EventManager {
  public enum Event{
    ONCARDPLAY,
    ONDISCARD,
    ONDISCARDED,
    ONEXHAUST,
    ONEXHAUSTED,
    ONDRAW,
    ONDRAWN,
    ONTURNEND,
    ONLOSEHP,
    ONPLAYERLOSEHP,
    ONATKDMGDEALT
  }

  /** Singleton EventManager Instance */
  public static final EventManager em = new EventManager();
  private EventManager(){
    if(em != null){
      throw new IllegalStateException("Instantiating second EventManager object.");
    }
  }
  public static final List<Event> BANNED_CARD_EFFECTS =
    List.of(Event.ONDRAW, Event.ONDISCARD, Event.ONEXHAUST, Event.ONLOSEHP, Event.ONATKDMGDEALT);
  public static final List<Event> BANNED_STATUS_EFFECTS =
    List.of(Event.ONDRAWN, Event.ONDISCARDED, Event.ONEXHAUSTED);


  public void OnExhaust(Card exhausted){
    playCardEffects(Event.ONEXHAUSTED, exhausted);
    playStatusEffects(Event.ONEXHAUST);
  }

  public void OnTurnEnd(){
    playStatusEffects(Event.ONTURNEND);

    // Calls ONTURNEND card effects from cards in hand; discards them if appropriate.
    // Each card discarded immediately after "play", for canon continuity.
    @SuppressWarnings("unchecked")
    ArrayList<Card> hand = (ArrayList<Card>) Combat.c.getHand().clone();
    for(Card card : hand){
      // Tracks whether the card has been removed from hand already or if it needs to be discarded still.
      boolean shouldDiscard = true;

      for(CardEffect eff : card.getEffects()){
        if(eff.whenPlayed() == Event.ONTURNEND){
          //Plays any ONTURNEND card effects
          //If the effect returns false (card should not discard), shouldDiscard set to false.
          shouldDiscard = Combat.c.playEffect(eff) && shouldDiscard;
        }
      }
      if(shouldDiscard){
        Combat.c.discard(card, false);
      }
    }
  }

  public void OnLoseHP(Entity victim, int hpLoss){
    // Note: If entity killed, hpLoss parameter not appropriately decreased.
    if(victim == Combat.c.getPlayer()){
      OnPlayerLoseHP(hpLoss);
    }
    playStatusEffects(Event.ONLOSEHP, Collections.singletonList(victim));
  }

  private void OnPlayerLoseHP(int hpLoss){
    playCardEffects(Event.ONPLAYERLOSEHP, Combat.c.getCardsInPlay());
    playStatusEffects(Event.ONPLAYERLOSEHP);
  }

  public void OnAttack(Entity attacker, Entity victim, int damage){
    if(victim.hasStatus("Flame Barrier")){
      attacker.damage(victim.getStatusStrength("Flame Barrier"));
    }

    if(damage > 0){
      OnAtkDmgDealt(victim, damage);
    }
  }

  public void OnAtkDmgDealt(Entity victim, int damage){
    if(victim.hasStatus("Curl Up")){ //Curl Up
      victim.block(victim.getStatusStrength("Curl Up"), false);
      victim.setStatusStrength("Curl Up", 0);
    }
    if(victim.hasStatus("Angry")){
      victim.addStatusStrength("Strength", victim.getStatusStrength("Angry"));
    }
  }

  public void OnAttackFinished(Entity attacker){
    //todo: If we never add anything else to this event, can change it so the attack() function
    //Just directly reduces vigor -- the entity copy system will make it so the statuses don't
    //apply until after all of the attacks are done.
    attacker.setStatusStrength("Vigor", 0);
  }

  /** Activates when a card is discarded "unnaturally", i.e. not from just playing it, but from
   * another effect such as Gambling Chip or Acrobatics. Only calls effects on the discarded card.
   * @param c The card discarded
   */
  public void OnDiscard(Card c){
    playCardEffects(Event.ONDISCARDED, c);
    playStatusEffects(Event.ONDISCARD);
  }

  public void OnDraw(Card c){
    playCardEffects(Event.ONDRAWN, c);
    playStatusEffects(Event.ONDRAW);
    Player player = Combat.c.getPlayer();

    if(c.isStatus()){
      int evolve = player.getStatusStrength("Evolve");
      for(int i=0; i<evolve; i++){
        Combat.c.drawCard();
      }
    }
    if(c.isStatus() || c.isCurse()){
      int fireBreathing = player.getStatusStrength("Fire Breathing");
      for(Entity enemy : Combat.c.getEnemies()){
        enemy.damage(fireBreathing);
      }
    }
  }


  
  /** Plays all status effects (on all entities) that were initialized with the respective event;
   * e.g. with (OnTurnEnd) for event == Event.ONTURNEND.
   * @param event The Event enum to call
   */
  private void playStatusEffects(Event event){
    playStatusEffects(event, Combat.c.getEntities());
  }
  /** Plays all status effects that were initialized with the respective event;
   * e.g. with (OnTurnEnd) for event == Event.ONTURNEND.
   * @param event The Event enum to call
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
  /** Plays all card effects (on the given card) that were initialized with the respective event;
   * e.g. with (OnTurnEnd) for event == Event.ONTURNEND.
   * @param event The Event enum to call
   */
  private void playCardEffects(Event event, Card c){
    playCardEffects(event, Collections.singletonList(c));
  }
  /** Plays all card effects (on the given cards) that were initialized with the respective event;
   * e.g. with (OnTurnEnd) for event == Event.ONTURNEND.
   * @param event The Event enum to call
   * @param cards The list of cards to call on
   */
  private void playCardEffects(Event event, List<? extends Card> cards){
    for(Card card : cards){
      for(CardEffect eff : card.getEffects()){
        if(eff.whenPlayed() == event){
          Combat.c.playEffect(eff);
        }
      }
    }
  }
  
  // Along with editing these functions for card/relic/w/e effects, can edit:
  // - Entity.calcAttackDamage / Entity.calcAtkDmgFromThisStats
  // - Combat.c.playCard / Combat.c.playEffect
  // - Card.Description constructor
}
