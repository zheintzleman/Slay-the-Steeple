package app;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import app.EventManager.Event;
import enemyfiles.*;
import util.CardList;
import util.Colors;
import util.Str;
import util.Util;

class CombatOverException extends Exception {}

/** A semi-singleton (i.e. only 1 at a time, but can be replaced by a new one) class representing
 * the current combat -- the current fight with an enemy/group of enemies.
 * Ties together many other classes, and contains methods for any situation that could occur
 * during a combat (excluding some methods that have been moved to EventManager as events.)
 * 
 * @see Run
 * @see Entity Entity / Enemy / Player
 * @see Card
 * @see Effect Effect / CardEffect / StatusEffect
 * @see EventManager
 */
public class Combat {
  private Player player;
  private ArrayList<Enemy> enemies;
  /** All changes to the enemy list while the enemies perform their intents go here instead of the main
    * enemies list. This is then synced back to the main enemies list after all intents are done. */
  private ArrayList<Enemy> enemiesToUpdate;
  private CardList drawPile, discardPile, exhaustPile, hand;
  private int energy;
  private final int baseEnergy;
  /** Combat stops running when set to true. Value immediately set upon player/last enemy dying. */
  private boolean combatOver;
  // For when too many cards in hand to fully print all of them
  private boolean condenseLeftHalfOfHand;
  //The highest row (of the screen) in which the hand cards are printed
  private final int topRowOfCards;
  /** The number of times Combust has been played this combat */
  private int combusts = 0;
  /** A reference to EventManager.er */
  private final EventManager eventManager;
  /** Semi-singleton Combat instance (only ever exists 1, but not final) */
  public static Combat c;
  
  public Combat(EntityHealth playerHealthObj){
    c = this;
    eventManager = EventManager.em;
    player = new Player("Ironclad", Colors.IRONCLADIMG2, playerHealthObj);
    enemies = new ArrayList<Enemy>();
    baseEnergy = 3;
    energy = -1;
    combatOver = false;
    condenseLeftHalfOfHand = false;
    topRowOfCards = Run.SCREENHEIGHT - Card.CARDHEIGHT;
    
    drawPile = new CardList();
    for(Card c : Run.r.getDeck()){
      drawPile.add(new Card(c));    //Makes draw pile contain a copy of each card in the deck.
    }
    Collections.shuffle(drawPile);
    discardPile = new CardList();
    exhaustPile = new CardList();
    hand = new CardList();

    // X pos (col) of the first enemy; offset between enemies.
    // If >2 enemies, used for ensuring same gap between them all.
    int e1X, gap;
    String combatName = pickCombat();
    switch(combatName){
      case "Jaw Worm":
        enemies.add(new JawWorm(Run.SCREENWIDTH*5/7));
        break;
      case "Two Louses":
        enemies.add(createLouse(Run.SCREENWIDTH*2/3));
        enemies.add(createLouse(Run.SCREENWIDTH*4/5));
        break;
      case "Cultist":
        enemies.add(new Cultist(Run.SCREENWIDTH*5/7));
        break;
      case "Small and Med Slime":
      case "Small and Medium Slime":
        enemies.add(createSlime(Run.SCREENWIDTH*2/3, 0));
        enemies.add(createSlime(Run.SCREENWIDTH*4/5, 1));
        break;
      case "Gremlin Gang":
        constructGremlinGang();
        break;
      case "Large Slime":
        enemies.add(createSlime(Run.SCREENWIDTH*5/7, 2));
        break;
      case "Lots of Slimes":
        e1X = Run.SCREENWIDTH*11/20;
        gap = Run.SCREENWIDTH*2/20;
        enemies.add(new SpikeSlimeSmall(e1X));
        enemies.add(new SpikeSlimeSmall(e1X + gap));
        enemies.add(new SpikeSlimeSmall(e1X + 2*gap));
        enemies.add(new AcidSlimeSmall(e1X + 3*gap));
        enemies.add(new AcidSlimeSmall(e1X + 4*gap));
        shuffleEnemies();
        break;
      case "Blue Slaver":
        enemies.add(new BlueSlaver(Run.SCREENWIDTH*5/7));
        break;
      case "Red Slaver":
        enemies.add(new RedSlaver(Run.SCREENWIDTH*5/7));
        break;
      case "Three Louses":
        e1X = Run.SCREENWIDTH*60/100;
        gap = Run.SCREENWIDTH*12/100;
        enemies.add(createLouse(e1X));
        enemies.add(createLouse(e1X + gap));
        enemies.add(createLouse(e1X + 2*gap));
        break;
      case "Two Fungi Beasts":
        enemies.add(new FungiBeast(Run.SCREENWIDTH*2/3));
        enemies.add(new FungiBeast(Run.SCREENWIDTH*4/5));
        break;
      case "Looter":
        enemies.add(new Looter(Run.SCREENWIDTH*5/7));
        break;
      case "Exordium Thugs":
      case "Thugs":
        constructThugs();
        break;
      case "Exordium Wildlife":
      case "Wildlife":
        constructWildlife();
        break;
    }
  }

  //Getters and Setters
  public ArrayList<Card> getDrawPile(){ return drawPile; }
  public ArrayList<Card> getDiscardPile(){ return discardPile; }
  public ArrayList<Card> getExhaustPile(){ return exhaustPile; }
  public ArrayList<Card> getHand(){ return hand; }
  public ArrayList<Enemy> getEnemies(){ return enemies; }
  public ArrayList<Enemy> getEnemiesToUpdate(){ return enemiesToUpdate; }
  public void setEnemiesToUpdate(ArrayList<Enemy> newETU){ enemiesToUpdate = newETU; }
  public Player getPlayer(){ return player; }


  public static String pickCombat(){
    // Random string from list
    List<String> combatTypes = List.of("Cultist", "Jaw Worm", "Two Louses",
      "Small and Med Slime", "Gremlin Gang", "Large Slime", "Lots of Slimes", "Blue Slaver",
      "Red Slaver", "Three Louses", "Two Fungi Beasts", "Exordium Thugs", "Exordium Wildlife",
      "Looter");
    return Util.randElt(combatTypes);
  }

  /** An ArrayList of all cards in the hand, draw, disc, and exh piles */
  public ArrayList<Card> getCardsInPlay(){
    ArrayList<Card> list = new ArrayList<Card>();
    list.addAll(hand);
    list.addAll(drawPile);
    list.addAll(discardPile);
    list.addAll(exhaustPile);
    return list;
  }
  public ArrayList<Entity> getEntities(){
    ArrayList<Entity> list = new ArrayList<Entity>();
    list.add(player);
    list.addAll(enemies);
    return list;
  }


  /** Adds the enemy to the arraylist of enemies
  */
  public void addEnemy(Enemy e){
    enemies.add(e);
  }
  /** Adds the enemy to the arraylist of enemies at the specified index
  */
  public void addEnemy(int index, Enemy e){
    enemies.add(index, e);
  }
  /** Removes the enemy from the arraylist of enemies
  */
  public void removeEnemy(Enemy e){
    for(int i=0; i<enemies.size(); i++){
      if(enemies.get(i) == e){
        enemies.remove(i);
        return;
      }
    }
  }
  /** Removes the enemy at the specified index from the arraylist of enemies
  */
  public Enemy removeEnemy(int index){
    return enemies.remove(index);
  }
  

  /** Performs the combat. Sets things up, then performs an infinite loop
   * of having the player take an action (i.e. playing a card. Could be
   * expanded to playing potions, etc. in the future.) "Returns" by the
   * checkCombatEnd() method throwing a CombatOverException.
   * @return The amount of gold stolen (by Mugger/Looter)
  */
  public void runCombat() throws CombatOverException {
    boolean turnOver;
    final int HANDSIZE = 5;
    
    // Put all innate cards on top of the deck:
    for(int i=0; i < drawPile.size(); i++){
      if(drawPile.get(i).hasEffect("Innate")){
        Card card = drawPile.remove(i);
        drawPile.addTop(card);
      }
    }

    while(true){
      //startOfTurn
      energy = baseEnergy;
      //draw new hand
      for(int i=0; i < HANDSIZE; i++){
        drawCard();
      }

      //Sync enemy lists (exists so order doesn't mess things up)
      enemiesToUpdate = new ArrayList<Enemy>(enemies);

      eventManager.OnTurnStart();
      checkCombatEnd();
      
      turnOver = false;
      while(!turnOver){
        //duringTurn
        display();
        turnOver = doNextAction();
        checkCombatEnd();
      }
      //endOfTurn

      //Discards Hand & OnTurnEnd Events
      endTurn();
      //Ends player & enemy turns
      endEntityTurns();
      
      checkCombatEnd();
    }
  }

  /** Inputs from the player the next action and performs it, updating the display as well.
  * @return boolean - Returns true if the action should end the turn, false otherwise
  */
  public boolean doNextAction(){
    boolean doneAction = false;
    
    while(!doneAction){
      String input = input();
      try{
        playCardFromHand(Integer.parseInt(input));
        doneAction = true;
        continue;
      } catch(NumberFormatException e){}
      
      if(input.indexOf("Play")==0 || input.indexOf("play")==0){
        try{
          playCardFromHand(Integer.parseInt(input.substring(5)));
          doneAction = true;
        } catch(NumberFormatException e){
          System.out.println("Invalid input. To play a card, type \u001B[35mPlay #\u001B[0m, or just \u001B[35m#\u001B[0m, where # is the card's position in your hand."); //TODO: Using esc codes< (Move to Colors.java)
        }
      }else if(input.equalsIgnoreCase("e") || input.equalsIgnoreCase("end") || input.equalsIgnoreCase("end turn")){
        doneAction = true;
        return true;
      }else if(SettingsManager.sm.cheats && (input.equalsIgnoreCase("/killall")
                                          || input.equalsIgnoreCase("/ka"))){
        while(enemies.size() > 0){
          enemies.get(0).die();
        }
        doneAction = true;
        return true;
      }
    }
    return false; //False if combat is not over, true if it is
  }

  /** If combat over, throws a CombatOverException that cancels everything else that would have
   * happened during this combat, immediately going to the rewards screen.
   */
  private void checkCombatEnd() throws CombatOverException {
    if(combatOver){
      // Makes the enemy disappear in the combat rewards screen:
      setUpCombatDisplay();
      throw new CombatOverException();
    }
  }

  /** Ends the turn, discarding the hand and calling OnTurnEnd events */
  public void endTurn(){
    eventManager.OnTurnEnd();
  }

  public void endEntityTurns(){
    enemiesToUpdate = new ArrayList<Enemy>(enemies);
    Entity.hold();
    
    for(Enemy e : enemies){
      e.endTurn(player);
    }
    enemies = enemiesToUpdate;

    player.endTurn(player);
    Entity.resume();
  }

  /** Sets the screen to accurate values/images
  */
  public void setUpCombatDisplay(){
    Run.r.reloadScreen();
    int playerMidX = Run.SCREENWIDTH*2/7; //The x (column) value of the center of the player's display
    int entityBottomY = Run.SCREENHEIGHT/2; //The y (row) value of the row just below each entity's display
    final int energySquareX = 24; // = Run.SCREENWIDTH/2 - (Run.SCREENWIDTH/Card.CARDWIDTH - 1) * ((Card.CARDWIDTH + 1)/2) + Card.CARDWIDTH/2 + 3;
    
    //Player img
    String[] playerArt = player.getArt();
    int playerArtWidth = Str.lengthIgnoringEscSeqs(playerArt[0]);
    Run.r.addToScreen(entityBottomY-playerArt.length, playerMidX-(playerArtWidth/2), playerArt, "", Colors.reset);
    String hpBar = player.getHPBar();
    if(player.getBlock() > 0){    //adds blocknumber to left
      String middleRow = square(1, 3, player.getBlock(), Colors.blockBlue, Colors.whiteBold + Colors.backgroundBlockBlue)[0];
      String topRow, bottomRow;
      topRow = bottomRow = "";
      for(int i=0; i<Str.lengthIgnoringEscSeqs(middleRow); i++){
        topRow += "▄";
        bottomRow += "▀";
      }
      int leftIndex = playerMidX - (player.getHPBarLength()-1)/2 - Str.lengthIgnoringEscSeqs(middleRow) -1;
      Run.r.addToScreen(entityBottomY, leftIndex, new String[] {topRow, middleRow, bottomRow}, Colors.reset+Colors.blockBlue, Colors.reset);
    }
    //Player hp bar
    Run.r.addToScreen(entityBottomY+1, playerMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), hpBar, "", Colors.reset);
    //Player name
    Run.r.addToScreen(entityBottomY+3, playerMidX-(Str.lengthIgnoringEscSeqs(player.getName())/2), player.getName(), Colors.reset, "");
    //Player Statuses
    String statuses = "";
    for(Status status : player.getStatuses()){
      // if(status.getStrength() != 0){
        statuses += status.getDisplay() + " ";
      // }
    }
    Run.r.addToScreen(entityBottomY+2, playerMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), statuses);
    for(Enemy enemy : enemies){
      //Enemy img
      String[] enemyArt = enemy.getArt();
      int enemyMidX = enemy.getMiddleX();
      Run.r.addToScreen(entityBottomY-enemyArt.length, enemyMidX-(Str.lengthIgnoringEscSeqs(enemyArt[0])/2), enemyArt, "", Colors.reset);
      hpBar = enemy.getHPBar();
      if(enemy.getBlock() > 0){    //adds blocknumber to left
        String middleRow = square(1, 3, enemy.getBlock(), Colors.blockBlue, Colors.whiteBold + Colors.backgroundBlockBlue)[0];
        String topRow, bottomRow;
        topRow = bottomRow = "";
        for(int i=0; i<Str.lengthIgnoringEscSeqs(middleRow); i++){
          topRow += "▄";
          bottomRow += "▀";
        }
        int leftIndex = enemyMidX - (enemy.getHPBarLength()-1)/2 - Str.lengthIgnoringEscSeqs(middleRow) -1;
        Run.r.addToScreen(entityBottomY, leftIndex, new String[] {topRow, middleRow, bottomRow}, Colors.reset+Colors.blockBlue, Colors.reset);
      }
      //Enemy hp bar
      Run.r.addToScreen(entityBottomY+1, enemyMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), hpBar, "", Colors.reset);
      //Enemy name
      Run.r.addToScreen(entityBottomY+3, enemyMidX-(Str.lengthIgnoringEscSeqs(enemy.getName())/2), enemy.getName(), Colors.reset, "");
      //Enemy intent
      String[] intentImage = enemy.getIntent().getImage(enemy, player);
      Run.r.addToScreen(entityBottomY-enemyArt.length-intentImage.length-1, enemyMidX-(Str.lengthIgnoringEscSeqs(intentImage[0])/2), intentImage);
      //Enemy Statuses
      statuses = ""; //Declared above
      for(Status status : enemy.getStatuses()){
        App.ASSERT(status.getStrength() != 0);
        statuses += status.getDisplay() + " ";
      }
      Run.r.addToScreen(entityBottomY+2, enemyMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), statuses);
    }
    //Draw and Discard Piles
    String[] drawPileDisplay = square(3, 5, drawPile.size(), Colors.deckBrown, Colors.whiteOnDeckBrown);
    Run.r.addToScreen(Run.SCREENHEIGHT -4, 1, drawPileDisplay , Colors.reset + Colors.deckBrown, Colors.reset);
    Run.r.addToScreen(Run.SCREENHEIGHT -6, 1, new String[] {"Draw", "Pile"}, Colors.magenta, Colors.reset);
    String[] discardPileDisplay = square(3, 5, discardPile.size(), Colors.deckBrown, Colors.whiteOnDeckBrown);
    Run.r.addToScreen(Run.SCREENHEIGHT -4, Run.SCREENWIDTH -6, discardPileDisplay , Colors.reset + Colors.deckBrown, Colors.reset);
    Run.r.addToScreen(Run.SCREENHEIGHT -6, Run.SCREENWIDTH -8, new String[] {"Discard", "   Pile"}, Colors.magenta, Colors.reset);
    String[] exhaustPileDisplay = square(3, 5, exhaustPile.size(), Colors.exhGray, Colors.whiteOnExhGray);
    Run.r.addToScreen(Run.SCREENHEIGHT -10, Run.SCREENWIDTH -6, exhaustPileDisplay , Colors.reset + Colors.exhGray, Colors.reset);
    Run.r.addToScreen(Run.SCREENHEIGHT -12, Run.SCREENWIDTH -8, new String[] {"Exhaust", "   Pile"}, Colors.magenta, Colors.reset);

    addHandToScreen(hand);

    //Energy counter:
    String[] energyBlock = square(3, 7, energy, Colors.energyCounterRed, Colors.whiteOnEnergyCounterRed);
    Run.r.addToScreen(topRowOfCards-3, energySquareX, energyBlock, Colors.reset + Colors.energyCounterRed, Colors.reset);
  }

  /** Reloads and displays the screen
  */
  public void display(){
    setUpCombatDisplay(); //Actually just save the screen as a variable like in Run, if I'm having speed issues
    Run.r.display();
  }
  /** Displays the screen, replacing the hand with `displayHand`
  */
  public void display(CardList displayHand){
    CardList oldHand = hand;
    hand = displayHand;
    display();
    hand = oldHand;
  }

  /** Draws a card from the draw pile. Shuffles the discard into the draw pile if necessary.
   * If can't draw, doesn't.
  */
  public Card drawCard(){
    if(!canDraw()){
      return null;
    }
    if(hand.size() == 0 || hand.size() == 1){
      // For display purposes:
      condenseLeftHalfOfHand = false;
    }
    
    //Moves cards from discard to draw pile if necessary
    if(drawPile.size() == 0){
      // Swap draw & discard piles:
      CardList temp = drawPile;
      drawPile = discardPile;
      discardPile = temp;
      Collections.shuffle(drawPile);
    }
    
    Card topCard = drawPile.removeTop();
    hand.add(topCard);
    EventManager.em.OnDraw(topCard);
    return topCard;
  }

  public boolean canDraw(){
    if(hand.size() >= 10){
      //Hand limit (Hand is full)
      return false;
    }
    if(drawPile.size() == 0 && discardPile.size() == 0){
      //No cards to draw
      return false;
    }
    if(player.hasStatus("No Draw")){
      return false;
    }

    return true;
  }

  /** Adds the card to the hand, or the discard if the hand is full.
   * If !CANENTERPILES, does nothing.
   */
  public void gainToHand(Card card){
    // Assert card not currently in play:
    App.ASSERT(!getCardsInPlay().contains(card));
    if(hand.size() < 10){
      hand.add(card);
    } else {
      discardPile.add(card);
    }
  }

  /** Removes card from all piles and adds it to the exhaust pile, if not in
   * the exhaust pile already and CANENTERPILES true.
   */
  public void exhaust(Card c){
    if(exhaustPile.contains(c)){ return; }

    removeFromAllPiles(c);
    c.setCosts0ThisTurn(false);
    if(exhaustPile.add(c)){
      EventManager.em.OnExhaust(c);
    }
  }
  /** Removes the card from all piles & adds it to the discard pile (if CANENTERPILES.)
   * @param c The card to discard
   * @param callOnDiscard Whether or not to call the OnDiscard method, i.e. whether or not the card
   * discarded naturally (false) vs. if it was discarded by some effect like another card.
  */
  public void discard(Card c, boolean callOnDiscard){
    removeFromAllPiles(c);
    c.setCosts0ThisTurn(false);
    if(discardPile.add(c) && callOnDiscard){
      EventManager.em.OnDiscard(c);
    }
  }

  public void removeFromAllPiles(Card card){
    exhaustPile.remove(card);
    drawPile.remove(card);
    hand.remove(card);
    discardPile.remove(card);
  }

  public boolean isInAnyPiles(Card card){
    return exhaustPile.contains(card)
        || drawPile.contains(card)
        || hand.contains(card)
        || discardPile.contains(card);
  }

  /** Attemps to play the card at the selected index in hand.
  * @param index - The index in hand of the card being played
  */
  public void playCardFromHand(int num){
    if(num%10 == 0){
      num += 10;
    }

    int index = num-1;
    Card card;

    //Gets and assigns the target card:
    try{
      card = hand.get(index);
    }catch(IndexOutOfBoundsException e){
      return;
    }

    playCardFromHand(card);
    
    return;
  }

  /** Attemps to play the card; getting the target, reducing energy, and
   * discarding afterwards as appropriate.
   */
  public void playCardFromHand(Card card){
    Enemy target = null;
    // For X-cost cards:
    Integer X = null;

    if(!cardPlayable(card)
    || (!card.ISXCOST && card.getEnergyCost() > energy)){
      return;
    }

    if(card.isTargeted()){
      int targetIndex = getTarget();
      if(targetIndex == -1){ return; }
      target = enemies.get(targetIndex-1);
    }

    if(card.ISXCOST){
      X = energy;
      energy = 0;
    } else {
      // If not unplayable or x-cost, lose energy equal to the card's cost:
      energy -= card.getEnergyCost();
    }

    hand.remove(card);

    playCard(card, target, X);

    // Double Tap duplication:
    final int DOUBLETAPS = player.getStatusStrength("Double Tap");
    if(card.isAttack() && DOUBLETAPS > 0){
      //Decrease its strength by 1 (no supported easy way to do that as of now)
      player.setStatusStrength("Double Tap", DOUBLETAPS - 1);
      Card clone = new Card(card, false);
      playCard(clone, target, X);
    }
    
    if(card.isPower()){
      removeFromAllPiles(card);
    } else if(player.hasStatus("Corruption") && card.isSkill()
           && !exhaustPile.contains(card)){
      exhaust(card);
    } else if(!isInAnyPiles(card)){
      discard(card, false);
    }
  }

  /** Plays the effects of the card, with the given (nullable) target and
   * X-value. Does not reduce energy, discard, etc.
   * 
   * @param target If null, targets a random enemy.
   * @param X For X-cost cards. If null, just uses current energy. (Not an
   * option for normal playFromHand b/c this function is called after you lose
   * the energy, so `energy` is usually at 0 when this is called.)
   * 
   * @see Effect
   * @see Eff
   */
  private void playCard(Card card, Enemy target, Integer X){
    if(!cardPlayable(card)){
      return;
    }
    // Don't know if this is necessary, but it makes reasoning about later logic (specifically the
    // possible null return value of randElt) easier.
    if(combatOver){
      return;
    }
    // If null, target a random enemy:
    if(target == null && card.isTargeted()){
      target = Util.randElt(enemies);
    }
    // In case first Double-Tapped card kills enemy. This should do nothing, but
    // just in case that would cause a bug later:
    if(target != null && !enemies.contains(target)){
      System.out.println("Target not in Enemies list.");
      return;
    }
    if(X == null){
      X = energy;
    }
    
    Entity.holdBlock();
    
    //Doing the card's effect(s):
    ArrayList<CardEffect> cardEffects = card.getEffects();
    for(CardEffect eff : cardEffects){
      if(combatOver && !eff.affectsRunState()){
        continue;
      }
      if(eff.whenPlayed() != Event.ONCARDPLAY){
        continue;
      }
      Eff primary = eff.getPrimary();
      String secondary = eff.getSecondary();
      int power = eff.getPower();
      
      switch(primary){
        case Eff.SearingBlow:
          power = card.searingBlowDamage();
          //Fallthrough
        case Eff.Attack:
          if(enemies.contains(target))
            player.attack(target, power);
          break;
        case Eff.HeavyAttack: //Alternatively, could add code into the "gain pwr from strength" code to add more if card has some heavy property. Maybe even an (OnStrUse) or smth.
          if(enemies.contains(target)){
            int basePower = 14; //Base is 14 whether upgraded or not. If ever updated, can use secondary as an int
            player.attack(target, basePower, power);
          }
          break;
        case Eff.BodySlam:
          if(enemies.contains(target))
            player.attack(target, player.getBlock());
          break;
        case Eff.Dropkick:
          if(enemies.contains(target))
            player.attack(target, player.getBlock());
          if(target.hasStatus("Vulnerable")){
            energy++;
            drawCard();
          }
          break;
        case Eff.AtkAll: //Uses shortened word to be separate from "Attack" (& for brevity)
          player.attack(enemies, power);
          break;
        case Eff.Reap:
          final int dmg = player.attack(enemies, power);
          player.heal(dmg);
          break;
        case Eff.Whirlwind:
          for(int i=0; i<X; i++){
            player.attack(enemies, power);
          }
          break;
        case Eff.SpotWeakness:
          if(target.getIntent().isAttack()){
            player.addStatusStrength("Strength", power);
          }
          break;
        case Eff.Apply:
          target.addStatusStrength(secondary, power);
          break;
        case Eff.Unapply:
          target.subtractStatusStrength(secondary, power);
          break;
        case Eff.Feed:
          if(target.isDead()){
            player.raiseMaxHP(power);
          }
          break;
        case Eff.FiendFire:
          while(hand.size() != 0){
            exhaust(hand.get(0));
            if(enemies.contains(target)){
              player.attack(target, power);
            }
          }
          break;
        default:
          // Other effects that are included in the playEffect function
          playEffect(eff);
      }
    }

    if(card.isAttack()){
      eventManager.OnAttackFinished(player);
    }
    
    Entity.resumeBlock();
  }

  public boolean cardPlayable(Card card){
    if(card.ISUNPLAYABLE
    || player.hasStatus("Entangled") && card.isAttack()){
      return false;
    }
    if(card.hasEffect("Clash")){
      for(Card c : hand){
        if(!c.isAttack()){
          return false;
        }
      }
    }
    return true;
  }
  /** Prompts the user for the target and returns their response.
   * Returns -1 if card play is cancelled (no possible target selected)
   */
  public int getTarget(){
    if(enemies.size() == 1){
      return 1;
    }
    String input = input("Which enemy do you want to play this card on? (1-" + enemies.size() + "; type nothing to cancel)\n");
    int val = -1;
    try{
      val = Integer.parseInt(input);
    }catch(NumberFormatException e){}
    if(val > 0 && val <= enemies.size()){
      return val;
    }
    return -1;
  }

  /** Plays an effect that does not target a specific enemy.
   * 
   * @Precondition eff does not target an enemy.
   * @see Effect
   * @see Eff
   */
  public void playEffect(Effect eff){
    Eff primary = eff.getPrimary();
    String secondary = eff.getSecondary();
    int power = eff.getPower();
    Card card = (eff instanceof CardEffect) ? ((CardEffect) eff).getCard() : null;

    switch(primary){
      case Eff.Block:
        player.block(power, eff instanceof CardEffect);
        break;
      case Eff.Entrench:
        player.setBlock(2 * player.getBlock());
        break;
      case Eff.AtkRandom:
        if(enemies.size() == 0) break; //Guard Clause
        Enemy target = Util.randElt(enemies);
        player.attack(target, power);
        break;
      case Eff.AppPlayer:
        player.addStatusStrength(secondary, power);
        break;
      case Eff.ClearStatusPlayer:
        player.setStatusStrength(secondary, 0);
        break;
      case Eff.AppAll:
        for(Enemy enemy : enemies){
          enemy.addStatusStrength(secondary, power);
        }
        break;
      // From a card (For Rupture)
      case Eff.DmgPlayerC:
        player.damage(power, true);
        break;
      // Not from a card
      case Eff.DmgPlayerNC:
        player.damage(power, false);
        break;
      // From a card (For Rupture)
      case Eff.LoseHPC:
        player.subtractHP(power, true);
        break;
      // Not from a card
      case Eff.LoseHPNC:
        player.subtractHP(power, false);
        break;
      case Eff.Ethereal: // The only thing Ethereal changes is when it is activated (OnTurnEnd); the effect is the same.
      case Eff.Exhaust:
        for(Card c : cardTargets(secondary, card)){
          exhaust(c);
          // if(c == card){ shouldDiscard = false; }
        }
        break;
      case Eff.Draw:
        for(int i=0; i < power; i++){
          drawCard();
        }
        break;
      case Eff.Upgrade:
        for(Card c : cardTargets(secondary, card)){
          c.upgrade();
        }
        break;
      case Eff.PutOnDrawPile:
        for(Card c : cardTargets(secondary, card)){
          removeFromAllPiles(c);
          drawPile.addTop(c);
          // if(c == card){ shouldDiscard = false; } //Don't discard if we moved card onto draw pile
        }
        break;
      case Eff.CopyToHand:
        for(Card c : cardTargets(secondary, card)){
          for(int i=0; i < power; i++){
            gainToHand(new Card(c));
          }
        }
        break;
      case Eff.CopyToHandFree:
        for(Card c : cardTargets(secondary, card)){
          for(int i=0; i < power; i++){
            Card copy = new Card(c);
            copy.setCosts0ThisTurn(true);
            gainToHand(copy);
          }
        }
        break;
      case Eff.GainToDraw:
        for(int i=0; i < power; i++){
          Card c = new Card(secondary);
          drawPile.add(c);
        }
        Collections.shuffle(drawPile);
        break;
      case Eff.GainToDisc:
        for(int i=0; i < power; i++){
          Card c = new Card(secondary);
          discardPile.add(c);
        }
        break;
      case Eff.Anger:
        // Works, since cards with CANENTERPILES don't enter the discard from this anyway
        // (which is what we want; e.g. Double Tap-ing an Anger.)
        discardPile.add(new Card(card));
        break;
      case Eff.Rampage:
        App.ASSERT(card != null);
        //(int must be final to use in a lambda)
        final int rampagePower = power;
        //Increase the damage of this card's attacks:
        Consumer<CardEffect> increaseDamage = (CardEffect e) -> {
          if(e.getPrimary() == Eff.Attack){
            e.setPower(e.getPower() + rampagePower);
          }
        };
        card.getEffects().stream().forEach(increaseDamage);
        card.getUpEffects().stream().forEach(increaseDamage);
        card.reloadDescription();
        break;
      case Eff.ExhaustNonattacks:
        for(int i=0; i < hand.size();){
          Card c = hand.get(i);
          if(!c.isAttack()){
            exhaust(c);
            player.block(power, true);
          } else {
            i++;
          }
        }
        break;
      case Eff.Havoc: {
          if(drawPile.size() == 0){
            break;
          }
          Card c = drawPile.removeTop();
          
          int startRow = SettingsManager.sm.screenHeight/2 - c.getImage().length/2;
          int startCol = SettingsManager.sm.screenWidth/2
                       - Str.lengthIgnoringEscSeqs(c.getImage()[0])/2;
          // setUpCombatDisplay();
          Run.r.displayScreenWithAddition(c.getImage(), startRow, startCol);
          Str.print("Playing and Exhausting " + c.getName() + ". (Press enter)");
          Main.scan.nextLine();
          
          playCard(c, null, null);
          if(!c.isPower()){
            exhaust(c);
          }
        }
        break;
      case Eff.LimitBreak:
        player.addStatusStrength("Strength", player.getStatusStrength("Strength"));
        break;
      case Eff.LoseEnergy:
        power = -power;
        //FALLTHRU
      case Eff.GainEnergy:
        energy += power;
        break;
      case Eff.AddCost:
        App.ASSERT(card != null);
        card.setBaseEnergyCost(card.getBaseEnergyCost() + power);
        break;
      case Eff.IncrCombustCnt:
        combusts++;
        break;
      case Eff.Combust:
        final int combustDamage = power;
        player.subtractHP(combusts, true);
        List.copyOf(enemies).forEach(e -> e.damage(combustDamage, true));
        break;
      case Eff.Exhume:
        for(Card c : cardTargets("Choose1FromExhaust", card)){
          exhaustPile.remove(c);
          gainToHand(c);
        }
        break;
      case Eff.Clash:
      case Eff.Unplayable:
      case Eff.Innate:
        break; //Relevant code is earlier; included to avoid error.
      default:
        throw new UnsupportedOperationException("Invalid effect \"" + primary + "\", from card: " + card);
    }
  }

  /** Returns an array of the card(s) represented by the expression in secondary.
   * Secondary can be one of the keywords in the below switch statement, or the name of a card.
   * If a card name, returns a new clone of that card (else returns direct reference(s) to the relevant card(s).)
   * @param secondary The second word of the card effect, the meaning of which will be converted to an array
   * @param current The card being currently played
   */
  public Card[] cardTargets(String secondary, Card current){
    switch(secondary){
      case "":
      case "This":
        return new Card[] {current};
      case "TopFromDeck":
        return new Card[] {drawPile.get(0)};
      case "RandAtk":
        ArrayList<Card> attacks = new ArrayList<>(App.CARDS);
        attacks.removeIf(Predicate.not(Card::isAttack));
        return new Card[]{Util.randElt(attacks)};
      case "Hand":
        return hand.toArray(new Card[0]);
      case "RandHand":
        if(hand.size() <= 1) return hand.toArray(new Card[0]); //If hand just 0 or 1 elts, return it
        return new Card[]{Util.randElt(hand)};
      case "Choose1FromHand":
        if(hand.size() <= 1) return hand.toArray(new Card[0]); //If hand just 0 or 1 elts, return it
        display();
        while(true){
          try{
            String input = input("Select the position of a card in your hand: ");
            Card c = hand.get(Integer.parseInt(input)-1);
            return new Card[] {c};
          } catch(NumberFormatException | IndexOutOfBoundsException e){}
        }
        //No break since returns above
      case "Choose1AtkOrPwrFromHand":
        Predicate<Card> notAtkOrPwr = card -> { return !(card.isAttack() || card.isPower()); };
        CardList filteredHand = new CardList(hand);
        filteredHand.removeIf(notAtkOrPwr);
        if(filteredHand.size() <= 1) return filteredHand.toArray(new Card[0]);

        display(filteredHand);
        while(true){
          try{
            String input = input("Select the position of a card: ");
            Card c = filteredHand.get(Integer.parseInt(input)-1);
            return new Card[] {c};
          } catch(NumberFormatException | IndexOutOfBoundsException e){}
        }
        //No break since returns above
      case "Choose1FromDisc":
        if(discardPile.isEmpty()) return new Card[]{}; //Empty selection if discard pile empty
        display();
        while(true){
          try{
            String str = "Your discard pile:\n"; //Text to popup
            for(Card discardCard : discardPile){
              str += discardCard.toString() + "\n"; //Constructing the discard pile text
            }
            Card c = null;
            while(c == null){ //Until they enter the name of a card in their discard pile
              String input = Run.r.popupInput(str, "Select the name of a card in your discard pile");
              for(Card discardCard : discardPile){
                if(Str.equalsIgnoreCaseSkipEscSeqs(input, discardCard.getName())){ //Checks if input is a card in the discard pile
                  c = discardCard;
                }
              }
            }
            return new Card[] {c};
          } catch(NumberFormatException | IndexOutOfBoundsException e){}
        }
        //No break since returns above
      case "Choose1FromExhaust":
        if(exhaustPile.isEmpty()) return new Card[]{}; //Empty selection if exhaust pile empty
        display();
        while(true){
          try{
            String str = "Your exhaust pile:\n"; //Text to popup
            for(Card exhaustCard : exhaustPile){
              str += exhaustCard.toString() + "\n"; //Constructing the exhaust pile text
            }
            Card c = null;
            while(c == null){ //Until they enter the name of a card in their exhaust pile
              String input = Run.r.popupInput(str, "Select the name of a card in your exhaust pile");
              for(Card exhaustCard : exhaustPile){
                if(Str.equalsIgnoreCaseSkipEscSeqs(input, exhaustCard.getName())){ //Checks if input is a card in the exhaust pile
                  c = exhaustCard;
                }
              }
            }
            return new Card[] {c};
          } catch(NumberFormatException | IndexOutOfBoundsException e){}
        }
        //No break since returns above
      default:
        try{
          return new Card[] {new Card(Card.getCard(secondary))};
        } catch (NoSuchElementException e) {
          throw new UnsupportedOperationException(secondary + " not a valid card Target.");
        }
    }
  }

  public String input(){
    return input("");
  }
  /** Prompts the user for an input until they enter something that is not one of the popup commands. For each that is a command, does that command.
  */
  public String input(String prompt){
    while(true){
      Str.print(prompt);
      String input = Run.r.input();
      String str;
      
      switch(input.toLowerCase()){
        case "draw":
        case "a":
          ArrayList<Card> sortedDrawPile = sortedList(drawPile);
          str = "Draw pile, sorted alphabetically:\n";
          for(Card c : sortedDrawPile){
            str += c.toString() + "\n";
          }
          Run.r.popup(str);
          break;
        case "disc":
        case "discard":
        case "s":
          str = "Discard pile:\n";
          for(Card c : discardPile){
            str += c.toString() + "\n";
          }
          Run.r.popup(str);
          break;
        case "exh":
        case "exhaust":
        case "x":
          str = "Exhausted Cards:\n";
          for(Card c : exhaustPile){
            str += c.toString() + "\n";
          }
          Run.r.popup(str);
          break;
        case "stat":
        case "stats":
        case "status":
        case "t":
          String statuses = "Player Statuses:\n";
          for(Status s : player.getStatuses()){
            statuses += s.toString() + "\n";
          }
          int i=0;
          for(Enemy enemy : enemies){
            statuses += Colors.reset + "\nEnemy " + (++i) + " Statuses:\n";
            for(Status s : enemy.getStatuses()){
              statuses += s.toString() + "\n";
            }
          }
          Run.r.popup(statuses);
          break;
        case ">":
        case "c":
          condenseLeftHalfOfHand = true;
          display();
          break;
        case "<":
        case "z":
          condenseLeftHalfOfHand = false;
          display();
          break;
        default:
          return input;
      }
    }
    
  }
  
  /** Adds the player's hand to the screen.
   * Updates CardEffects and uses eff.power description instead of eff.basePower description
  */
  public void addHandToScreen(ArrayList<Card> cards){
    int numCards = cards.size();
    if(numCards > 10){
      System.out.println("Hand size > 10");
    }
    if(numCards == 0){
      return;
    }

    //Cards:
    // Explanation: Starting from the middle column, each additional card added to the hand pushes the leftmost ("starting")
    // column of the hand (CARDWIDTH+1)/2 chars left (+1 for the empty space between.)
    // Since the first card doesn't add an empty space, it only shifts by CARDWIDTH/2, so we add 1 to mCP1 to compensate.
    final int middleColPlus1 = Run.SCREENWIDTH/2 + 1;
    final int offsetPerCard = (Card.CARDWIDTH + 1)/2;
    //# of cards that can nicely fit on screen
    final int cardsThatFit = Run.SCREENWIDTH/Card.CARDWIDTH - 1; //8
    final int leftmostCol = middleColPlus1 - Integer.min(numCards, cardsThatFit) * offsetPerCard;

    if(numCards <= cardsThatFit){
      // If there's enough room on screen for all the cards
      addHandCardsToScreen(cards, leftmostCol, 1);
      // Resets so the right part gets condensed if the player draws back up to >8.
      // This also resets when drawing a new hand, and is changed by entering < or >.
      condenseLeftHalfOfHand = false;
    } else {
      final int extraCards = numCards - cardsThatFit;
      final int numCardsSquished = extraCards*2;
      final int numCardsPrintedNormally = numCards - numCardsSquished;
      int nextCol;
      final int squishedWidth = (Card.CARDWIDTH + 1)/2;
      if(condenseLeftHalfOfHand){
        // Cuts off the last CARDWIDTH/2 chars from the images of the rightmost cards. //11 cols wide
        nextCol = addHandCardsToScreen(cards.subList(0, numCardsSquished), leftmostCol, 1, 0, 0, squishedWidth);
        addHandCardsToScreen(cards.subList(numCardsSquished, numCards), nextCol, numCardsSquished+1);
        Run.r.addToScreen(topRowOfCards-1, leftmostCol-1 + squishedWidth*numCardsSquished/2, "<", Colors.magenta, Colors.reset);
      } else {
        nextCol = addHandCardsToScreen(cards.subList(0, numCardsPrintedNormally), leftmostCol, 1);
        // Cut off the first CARDWIDTH/2 chars from the images of the rightmost cards. //11 cols wide
        addHandCardsToScreen(cards.subList(numCardsPrintedNormally, numCards), nextCol, numCardsPrintedNormally+1, 0, squishedWidth-1, Card.CARDWIDTH);
        Run.r.addToScreen(topRowOfCards-1, nextCol-2 + squishedWidth*numCardsSquished/2, ">", Colors.magenta, Colors.reset);
      }
    }
  }

  /**
   * Starting at column leftmostCol, adds the list of cards from right to left (recursively.)
   * @return The column to the right of the rightmost printed card.
   */
  int addHandCardsToScreen(List<Card> cards, int leftmostCol, int cardIndex){
    return addHandCardsToScreen(cards, leftmostCol, cardIndex, 1, 0, Card.CARDWIDTH);
  }
  /**
   * Starting at column leftmostCol, adds the list of cards from right to left (recursively.)
   * @param cutoff Which column of the cards' images to start printing at; cuts off the
   * the first `cutoff` (non-esc seq) characters of the images before adding to screen.
   * @return The column to the right of the rightmost printed card.
   */
  int addHandCardsToScreen(List<Card> cards, int leftmostCol, int cardIndex, int gap, int startCutoff, int endCutoff){
    if(cards.size() == 0) {
      return leftmostCol-1;
    } else if(cardIndex == 10){
      cardIndex = 0;
    }
    // int numCards = cards.size();
    String[] cardArt0 = cards.get(0).getImage(true);
    // Map substringIgnoringEscSequences to each string of cardArt0
    if(startCutoff > 0 || endCutoff < Card.CARDWIDTH){
      for(int i=0; i < cardArt0.length; i++) {
        cardArt0[i] = Str.substringIgnoringEscSequences(cardArt0[i], startCutoff, endCutoff);
      }
      // cardArt0 = Stream.of(cardArt0).map(s -> Str.substringIgnoringEscSequences(s, startCutoff, endCutoff)).toArray(String[]::new); //Stream Implementation
    } else {
      Run.r.addToScreen(topRowOfCards-1, leftmostCol + Card.CARDWIDTH/2, "" + cardIndex, Colors.magenta, Colors.reset);
    }

    Run.r.addToScreen(topRowOfCards, leftmostCol, cardArt0);
    return addHandCardsToScreen(cards.subList(1, cards.size()), leftmostCol + endCutoff - startCutoff + gap, cardIndex+1, gap, startCutoff, endCutoff);
  }

  /** Creates a box of ascii characters with a number in the center (ie the energy counter box). color is added only after the number in the middle line. textColor is the color of the number in the middle line.
   * valInCenter must be in [0,99]
  */
  public static String[] square(int height, int minWidth, int valInCenter, String color, String textColor){
    String[] square = new String[height];
    if(valInCenter <= 9){
        int numToLeft = (minWidth-1)/2;
        String halfOfRow = "";
        for(int i=0; i<numToLeft; i++){
            halfOfRow += "█";
        }
        String middleRow = (halfOfRow + textColor + valInCenter + Colors.reset + color + halfOfRow + "█").substring(0, minWidth + (textColor+Colors.reset+color).length());
        String fullRow = "";
        for(int i=0; i<minWidth; i++){
            fullRow += "█";
        }
        for(int i=0; i<height; i++){
            if(i==height/2){
                square[i] = middleRow;
            }else{
                square[i] = fullRow;
            }
        }
    }else{ //Val in center > 9
        int numToLeftOfVal = (minWidth-1)/2;
        String halfOfRow = "";
        for(int i=0; i<numToLeftOfVal; i++){
            halfOfRow += "█";
        }
        String middleRow = (halfOfRow + textColor + valInCenter + Colors.reset + color + halfOfRow + "█").substring(0, minWidth + 1 + (textColor+Colors.reset+color).length());
        String fullRow = "";
        for(int i=0; i<minWidth+1; i++){
            fullRow += "█";
        }
        for(int i=0; i<height; i++){
            if(i==height/2){
                square[i] = middleRow;
            }else{
                square[i] = fullRow;
            }
        }
    }
    return square;
  }

  /** Creates a box of ascii characters with a text in the center (ie the energy counter box). color is added only after the text in the middle line. textColor is the color of the text in the middle line.
  */
  public static String[] square(int height, int width, String textInCenter, String color, String textColor){
    String[] square = new String[height];
    String otherRows = "";
    String middleRow = "";
    for(int r=0; r<height; r++){
      if(r == height/2){
        int numBlocks = width - Str.lengthIgnoringEscSeqs(textInCenter);
        for(int i=0; i<(numBlocks+1)/2; i++){
          middleRow += "█";
        }
        middleRow += textColor + textInCenter + color;
        for(int i=0; i<numBlocks/2; i++){
          middleRow += "█";
        }
        square[r] = middleRow;
      }else{
        for(int i=0; i<width; i++){
          otherRows += "█";
        }
        square[r] = otherRows;
      }
    }

    return square;
  }


  
  /** Returns a new ArrayList<Card> which containes the contents of the parameter ArrayList<Card> sorted alphabetically by name.
  * @return ArrayList<Card> - A new ArrayList<Card> of the sorted parameter list values
  * @param list - The list to make a sorted version of
  */
  public static ArrayList<Card> sortedList(ArrayList<Card> list){
    ArrayList<Card> sortedList = new ArrayList<Card>(list);
    sortedList.sort((Card a, Card b) -> a.getName().compareTo(b.getName()));

    return sortedList;
  }

  /** Constructs and returns a louse of a random color
  */
  public Enemy createLouse(int midX){
    if(Math.random() < 0.5){
      return new RedLouse(midX);
    }
    return new GreenLouse(midX);
  }
  /** Constructs and returns a slime of the specified size and a random type
  * @Precondition - 0 >= size >= 2
  */
  public Enemy createSlime(int midX, int size){
    boolean isAcidSlime = Math.random() < 0.5; //50-50 chance
    if(isAcidSlime){ //Acid Slime:
      switch(size){
        case 0: //Small
          return new AcidSlimeSmall(midX);
        case 1: //Medium
          return new AcidSlimeMed(midX);
        case 2:
          return new AcidSlimeLarge(midX);
      }
    }else{ //Spike Slime:
      switch(size){
        case 0: //Small
          return new SpikeSlimeSmall(midX);
        case 1: //Medium
          return new SpikeSlimeMed(midX);
        case 2:
          return new SpikeSlimeLarge(midX);
      }
    }
    return null;
  }

  /** Constructs the enemies for Gremlin Gang combat and adds them to the enemy list
  */
  public void constructGremlinGang(){
    List<String> gremlins = Arrays.asList("Fat", "Fat", "Sneaky", "Sneaky", "Mad", "Mad", "Shield", "Wizard");
    Collections.shuffle(gremlins);
    //Remove four:
    gremlins = gremlins.subList(0, 4);
    
    //Construct remaining four:
    int midX = Run.SCREENWIDTH*6/10;
    int gap = Run.SCREENWIDTH*1/10 + 1;
    for(int i=0; i<gremlins.size(); i++){
      String str = gremlins.get(i);
      switch(str){
        case "Fat":
          enemies.add(new FatGremlin(midX));
          break;
        case "Sneaky":
          enemies.add(new SneakyGremlin(midX));
          break;
        case "Mad":
          enemies.add(new MadGremlin(midX));
          break;
        case "Shield":
          enemies.add(new ShieldGremlin(midX));
          break;
        case "Wizard":
          enemies.add(new GremlinWizard(midX));
          break;
      }
      midX += gap;
    }
  }

  /** Constructs the enemies for Exordium Thugs combat and adds them to the enemy list
  */
  public void constructThugs(){
    double rn = Math.random();
    if(rn < 0.5){
      enemies.add(createLouse(Run.SCREENWIDTH*2/3));
    }else{
      enemies.add(createSlime(Run.SCREENWIDTH*2/3, 1));
    }
    rn = Math.random();
    if(rn<0.25){
      enemies.add(new Looter(Run.SCREENWIDTH*4/5));
    }else if(rn<0.5){
      enemies.add(new Cultist(Run.SCREENWIDTH*4/5));
    }else if(rn<0.75){
      enemies.add(new BlueSlaver(Run.SCREENWIDTH*4/5));
    }else{
      enemies.add(new RedSlaver(Run.SCREENWIDTH*4/5));
    }
  }

  /** Constructs the enemies for Exordium Wildlife combat and adds them to the enemy list
  */
  public void constructWildlife(){
    double rn = Math.random();
    if(rn < 0.5){
      enemies.add(new FungiBeast(Run.SCREENWIDTH*2/3));
    }else{
      enemies.add(new JawWorm(Run.SCREENWIDTH*2/3));
    }
    rn = Math.random();
    if(rn < 0.5){
      enemies.add(createLouse(Run.SCREENWIDTH*4/5));
    }else{
      enemies.add(createSlime(Run.SCREENWIDTH*4/5, 1));
    }
  }

  /** Randomizes the list of enemies and their positions.
  */
  public void shuffleEnemies(){
    ArrayList<Integer> positions = new ArrayList<Integer>();
    for(Enemy e : enemies){
      positions.add(e.getMiddleX());
    }
    Collections.shuffle(enemies);
    for(int i=0; i<enemies.size(); i++){
      Enemy e = enemies.get(i);
      e.setMiddleX(positions.get(i));
    }
  }

  /** Sets combatOver to true
  */
  public void endCombat(){
    combatOver = true;
  }
}