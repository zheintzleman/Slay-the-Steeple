package app;
import java.util.*;

import enemyfiles.*;

public class Combat{
  private Entity player;
  private ArrayList<Enemy> enemies;
  private ArrayList<Enemy> enemiesToUpdate;
  private ArrayList<Card> drawPile, discardPile, exhaustPile, hand;
  private int energy, baseEnergy;
  boolean combatOver;
  int topRowOfCards;
  private Run thisRun; //To access data about this specific run
  public static final String[] IRONCLADIMG = Colors.fillColor(new String[] {"        ▄▄▄   ", "       ▄███▄  ", "▀▀▀▀▀▀▀▀▀████  ", "       ▄███▄  ", "       ██▀ ▀██ ", "       █▀    ▀█"}, Colors.atkIntArtRed); 
  
  public Combat(Run run){
    thisRun = run;
    player = new Entity("Ironclad", run.getHP(), run.getMaxHP(), IRONCLADIMG, this);
    enemies = new ArrayList<Enemy>();
    //Defaults to a Jaw Worm combat
    enemies.add(new JawWorm(Run.SCREENWIDTH*5/7, this));   
    
    baseEnergy = 3;
    energy = -1;
    combatOver = false;
    topRowOfCards = Run.SCREENHEIGHT - Card.CARDHEIGHT;
    
    drawPile = new ArrayList<Card>(run.getDeck());
    Collections.shuffle(drawPile);
    discardPile = new ArrayList<Card>();
    exhaustPile = new ArrayList<Card>();
    hand = new ArrayList<Card>();
  }
  public Combat(Run run, String combatType){
    thisRun = run;
    player = new Entity("Ironclad", run.getHP(), run.getMaxHP(), Colors.IRONCLADIMG2, this);
    enemies = new ArrayList<Enemy>();
    baseEnergy = 3;
    energy = -1;
    combatOver = false;
    topRowOfCards = Run.SCREENHEIGHT - Card.CARDHEIGHT;
    
    drawPile = new ArrayList<Card>();
    for(Card c : run.getDeck()){
      drawPile.add(new Card(c));    //Makes draw pile contain a copy of each card in the deck.
    }
    Collections.shuffle(drawPile);
    discardPile = new ArrayList<Card>();
    exhaustPile = new ArrayList<Card>();
    hand = new ArrayList<Card>();  

    switch(combatType){
      case "Jaw Worm":
        enemies.add(new JawWorm(Run.SCREENWIDTH*5/7, this));
        break;
      case "Two Louses":
        enemies.add(createLouse(Run.SCREENWIDTH*2/3));
        enemies.add(createLouse(Run.SCREENWIDTH*4/5));
        break;
      case "Cultist":
        enemies.add(new Cultist(Run.SCREENWIDTH*5/7, this));
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
        enemies.add(new SpikeSlimeSmall(Run.SCREENWIDTH*11/20, this));
        enemies.add(new SpikeSlimeSmall(Run.SCREENWIDTH*13/20, this));
        enemies.add(new SpikeSlimeSmall(Run.SCREENWIDTH*15/20, this));
        enemies.add(new AcidSlimeSmall(Run.SCREENWIDTH*17/20, this));
        enemies.add(new AcidSlimeSmall(Run.SCREENWIDTH*19/20, this));
        shuffleEnemies();
        break;
      case "Blue Slaver":
        enemies.add(new BlueSlaver(Run.SCREENWIDTH*5/7, this));
        break;
      case "Red Slaver":
        enemies.add(new RedSlaver(Run.SCREENWIDTH*5/7, this));
        break;
      case "Three Louses":
        enemies.add(createLouse(Run.SCREENWIDTH*6/10));
        enemies.add(createLouse(Run.SCREENWIDTH*72/100));
        enemies.add(createLouse(Run.SCREENWIDTH*84/100));
        break;
      case "Two Fungi Beasts":
        enemies.add(new FungiBeast(Run.SCREENWIDTH*2/3, this));
        enemies.add(new FungiBeast(Run.SCREENWIDTH*4/5, this));
        break;
      case "Looter":
        enemies.add(new Looter(Run.SCREENWIDTH*5/7, this));
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
  public ArrayList<Enemy> getEnemies(){ return enemies; }
  public ArrayList<Enemy> getEnemiesToUpdate(){ return enemiesToUpdate; }
  public void setEnemiesToUpdate(ArrayList<Enemy> newETU){ enemiesToUpdate = newETU; }
  public Entity getPlayer(){ return player; }
  public Run getRun(){ return thisRun; }


  /**Adds the enemy to the arraylist of enemies
  */
  public void addEnemy(Enemy e){
    enemies.add(e);
  }
  /**Adds the enemy to the arraylist of enemies at the specified index
  */
  public void addEnemy(int index, Enemy e){
    enemies.add(index, e);
  }
  /**Removes the enemy from the arraylist of enemies
  */
  public void removeEnemy(Enemy e){
    for(int i=0; i<enemies.size(); i++){
      if(enemies.get(i) == e){
        enemies.remove(i);
        return;
      }
    }
  }
  /**Removes the enemy at the specified index from the arraylist of enemies
  */
  public Enemy removeEnemy(int index){
    return enemies.remove(index);
  }
  

  /**Performs the combat.
   * @return The amount of gold stolen (by Mugger/Looter)
  */
  public int runCombat(){
    boolean turnOver = false;
    while(!combatOver){
      //startOfTurn
      energy = baseEnergy;
      for(Enemy e : enemies){
        e.setStartOfTurnBlock(0);
      }
      //draw new hand
      for(int i=0; i<5; i++){
        drawCard();
      }

      //Sync enemy lists (exists so order doesn't mess things up)
      enemiesToUpdate = new ArrayList<Enemy>(enemies);
      
      turnOver = false;
      while(!turnOver){
        //duringTurn
        display();
        turnOver = doNextAction();
      }
      //endOfTurn
      //discard hand
      while(hand.size() != 0){
        discardCardFromHand(0);
      }

      //Enemy turn ends
      enemiesToUpdate = new ArrayList<Enemy>(enemies);
      for(Enemy e : enemies){
        e.endTurn(player);
      }
      for(Enemy e : enemies){
        e.block(e.getStartOfTurnBlock());
      }
      enemies = enemiesToUpdate;
      
      player.endTurn();
      thisRun.setHP(player.getHP());
    }
    //TODO: Make gold stolen be updated here (both remove the gold from the player and add # to res)
      //(Make it related to the theivery/w/e status too btw)
    display();
    return 0;   //TODO: change (^)
  }

  /**Inputs from the player the next action and performs it, updating the display as well.
  *@return boolean - Returns true if the action should end the turn, false otherwise
  */
  public boolean doNextAction(){
    boolean doneAction = false;
    
    while(!doneAction){
      String input = input();
      try{
        playCard(Integer.parseInt(input));
        doneAction = true;
        continue;
      } catch(NumberFormatException e){}
      
      if(input.indexOf("Play")==0 || input.indexOf("play")==0){
        try{
          playCard(Integer.parseInt(input.substring(5)));
          doneAction = true;
        } catch(NumberFormatException e){
          System.out.println("Invalid input. To play a card, type \u001B[35mPlay #\u001B[0m, or just \u001B[35m#\u001B[0m, where # is the card's position in your hand.");
        }
      }else if(input.equalsIgnoreCase("e") || input.equalsIgnoreCase("end") || input.equalsIgnoreCase("end turn")){
        doneAction = true;
        return true;
      }else if(App.settingsManager.debug && (input.equalsIgnoreCase("/killall")
                                          || input.equalsIgnoreCase("/ka"))){
        while(enemies.size() > 0){
          enemies.get(0).die();
        }
        doneAction = true;
        return true;
      }
    }
    return combatOver; //False if combat is not over, true if it is
  }

  /**Sets the screen to accurate values/images
  */
  public void setUpCombatDisplay(){
    thisRun.reloadScreen();
    int playerMidX = Run.SCREENWIDTH*2/7; //The x (column) value of the center of the player's display
    int entityBottomY = Run.SCREENHEIGHT/2; //The y (row) value of the row just below each entity's display
    
    //Player img
    String[] playerArt = player.getArt();
    thisRun.addToScreen(entityBottomY-playerArt.length, playerMidX-(Str.lengthIgnoringEscSeqs(playerArt[0])/2), playerArt, Colors.lightRed, Colors.reset);
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
      thisRun.addToScreen(entityBottomY, leftIndex, new String[] {topRow, middleRow, bottomRow}, Colors.reset+Colors.blockBlue, Colors.reset);
    }
    //Player hp bar
    thisRun.addToScreen(entityBottomY+1, playerMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), hpBar, "", Colors.reset);
    //Player name
    thisRun.addToScreen(entityBottomY+3, playerMidX-(Str.lengthIgnoringEscSeqs(player.getName())/2), player.getName(), Colors.reset, "");
    //Player Statuses
    String statuses = "";
    for(Status status : player.getStatuses()){
      if(status.getStrength() != 0){
        statuses += status.getDisplay() + " ";
      }
    }
    thisRun.addToScreen(entityBottomY+2, playerMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), statuses);
    for(Enemy enemy : enemies){
      //Enemy img
      String[] enemyArt = enemy.getArt();
      int enemyMidX = enemy.getMiddleX();
      thisRun.addToScreen(entityBottomY-enemyArt.length, enemyMidX-(Str.lengthIgnoringEscSeqs(enemyArt[0])/2), enemyArt, "", Colors.reset);
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
        thisRun.addToScreen(entityBottomY, leftIndex, new String[] {topRow, middleRow, bottomRow}, Colors.reset+Colors.blockBlue, Colors.reset);
      }
      //Enemy hp bar
      thisRun.addToScreen(entityBottomY+1, enemyMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), hpBar, "", Colors.reset);
      //Enemy name
      thisRun.addToScreen(entityBottomY+3, enemyMidX-(Str.lengthIgnoringEscSeqs(enemy.getName())/2), enemy.getName(), Colors.reset, "");
      //Enemy intent
      String[] intentImage = enemy.getIntent().getImage(enemy, player);
      thisRun.addToScreen(entityBottomY-enemyArt.length-intentImage.length-1, enemyMidX-(Str.lengthIgnoringEscSeqs(intentImage[0])/2), intentImage);
      //Enemy Statuses
      statuses = ""; //Declared above
      for(Status status : enemy.getStatuses()){
        if(status.getStrength() != 0){
          statuses += status.getDisplay() + " ";
        }
      }
      thisRun.addToScreen(entityBottomY+2, enemyMidX-(Str.lengthIgnoringEscSeqs(hpBar)/2), statuses);
    }
    //Draw and Discard Piles
    String[] drawPileDisplay = square(3, 5, drawPile.size(), Colors.deckBrown, Colors.whiteOnDeckBrown);
    thisRun.addToScreen(Run.SCREENHEIGHT -4, 1, drawPileDisplay , Colors.reset + Colors.deckBrown, Colors.reset);
    thisRun.addToScreen(Run.SCREENHEIGHT -6, 1, new String[] {"Draw", "Pile"}, Colors.magenta, Colors.reset);
    String[] discardPileDisplay = square(3, 5, discardPile.size(), Colors.deckBrown, Colors.whiteOnDeckBrown);
    thisRun.addToScreen(Run.SCREENHEIGHT -4, Run.SCREENWIDTH -6, discardPileDisplay , Colors.reset + Colors.deckBrown, Colors.reset);
    thisRun.addToScreen(Run.SCREENHEIGHT -6, Run.SCREENWIDTH -8, new String[] {"Discard", "   Pile"}, Colors.magenta, Colors.reset);
    String[] exhaustPileDisplay = square(3, 5, exhaustPile.size(), Colors.exhGray, Colors.whiteOnExhGray);
    thisRun.addToScreen(Run.SCREENHEIGHT -10, Run.SCREENWIDTH -6, exhaustPileDisplay , Colors.reset + Colors.exhGray, Colors.reset);
    thisRun.addToScreen(Run.SCREENHEIGHT -12, Run.SCREENWIDTH -8, new String[] {"Exhaust", "   Pile"}, Colors.magenta, Colors.reset);

    //Hand
    // for(Card c : hand){
    //   for(CardEffect eff : c.getEffects()){
    //     if(eff.isAttack()){
    //       eff.setPower(player.calcAtkDmgFromThisStats(eff.getBasePower()));
    //     }
    //     if(eff.isDefense()){
    //       eff.setPower(player.calcBlockAmount(eff.getBasePower()));
    //     }
    //   }
    //   c.generateDescription();
    // }
    addHandToScreen(hand);

    //Energy counter:
    String[] energyBlock = square(3, 7, energy, Colors.energyDisplayRed, Colors.whiteOnEnergyDisplayRed);
    thisRun.addToScreen(topRowOfCards-3, Run.SCREENWIDTH/8, energyBlock, Colors.reset + Colors.energyDisplayRed, Colors.reset);
    
  }

  /**Reloads and displays the screen
  */
  public void display(){
    setUpCombatDisplay(); //Actually just save the screen as a variable like in Run, if I'm having speed issues
    thisRun.display();
  }
  

  /**Draws a card from the draw pile. Shuffles the discard into the draw pile if necessary.
  */
  private Card drawCard(){ //TODO: Put something in to make newly drawn card pop out (ie white instead of gray or smth?) so it's easier to read?
    if(hand.size() >= 10){
      //Hand limit (Hand is full)
      return null;
    }
    if(drawPile.size() == 0 && discardPile.size() == 0){
      //No cards to draw
      return null;
    }
    
    //Moves cards from discard to draw pile if necessary
    if(drawPile.size() == 0){
      while(discardPile.size() > 0){
        drawPile.add(discardPile.remove(0));
      }
      Collections.shuffle(drawPile);
    }
    
    Card topCard = drawPile.remove(0);
    hand.add(topCard);
    return topCard;
  }

  /**Removes card from all piles and adds it to the exhaust pile */
  public void exhaust(Card card){
    removeFromAllPiles(card);
    exhaustPile.add(card);
  }

  public void removeFromAllPiles(Card card){
    exhaustPile.remove(card);
    drawPile.remove(card);
    hand.remove(card);
    discardPile.remove(card);
  }

  /**Attemps to play the card at the selected index in hand.
  *@param index - The index in hand of the card being played
  *@return boolean - Whether or not a card was played. Returns false if index is not valid, if player has too little energy, or if card is `unplayable`.
  */
  public boolean playCard(int num){
    int index = num-1;
    Card card;

    try{        //Gets and assigns the target if neccessary
      card = hand.get(index);
    }catch(IndexOutOfBoundsException e){
      return false;
    }

    hand.remove(index);
    boolean cardPlayed = playCard(card);
    if(!cardPlayed){
      hand.add(index, card);
    }
    return(cardPlayed);
  }

  /**Attemps to play the card.
  *@return boolean - Whether or not the card was played. Returns false if player has too little energy, or if card is `unplayable`.
  */
  public boolean playCard(Card card){
    Entity target = null;
    boolean shouldDiscard = true;

    //Technical Things:

    if(card.getEnergyCost() > this.energy){
      return false;
    }
    if(player.hasStatus("Entangled") && card.getType().equals("Attack")){
      return false;
    }
    if(card.hasEffect("Clash")){
      for(Card c : hand){
        if(!c.getType().equals("Attack")){
          return false;
        }
      }
    }

    if(card.isTargeted()){
      int targetIndex = getTarget();
      if(targetIndex == -1){
        return false;
      }
      target = enemies.get(targetIndex-1);
    }
    
    energy -= card.getEnergyCost();

    //Doing the card's effect(s):
    
    ArrayList<CardEffect> cardEffects = card.getEffects();
    for(CardEffect eff : cardEffects){
      String firstWord = eff.getPrimary();
      String otherWords = eff.getSecondary();
      int power = eff.getPower();
      
      switch(firstWord){
        case "SearingBlow":
          power = card.searingBlowDamage();
          //Fallthrough
        case "Attack":
          if(enemies.contains(target))
            player.attack(target, power);
          break;
        case "BodySlam":
          if(enemies.contains(target))
            player.attack(target, player.getBlock());
          break;
        case "AtkAll": //Uses shortened word to be separate from "Attack" (& for brevity)
          for(Object enemy : enemies.toArray()){
            player.attack((Enemy)enemy, power);
          }
          break;
        case "AtkRandom":
          if(enemies.size() == 0) break; //Guard Clause
          int rng = (int) (Math.random() * enemies.size());
          target = enemies.get(rng);
          player.attack(target, power);
          break;
        case "Block":
          player.block(power);
          break;
        case "AppPlayer":
          target = player;
          //Fallthrough
        case "Apply":
          target.addStatusStrength(otherWords, power);
          break;
        case "AppAll":
          for(Enemy enemy : enemies){
            enemy.addStatusStrength(otherWords, power);
          }
          break;
        case "Exhaust":
          for(Card c : cardTargets(otherWords, card)){
            exhaust(c);
            if(c == card){
              shouldDiscard = false;
            }
          }
          break;
        case "Draw":
          for(int i=0; i < power; i++){
            drawCard();
          }
          break;
        case "Upgrade":
          for (Card c : cardTargets(otherWords, card)) {
            c.upgrade();
          }
          break;
        case "PutOnDrawPile":
          Str.println("Second Word:" + otherWords + ":");
          for(Card c : cardTargets(otherWords, card)){
            removeFromAllPiles(c);
            drawPile.add(0, c);
          }
          break;
        case "Heavy":
          power += player.getStatusStrength("Strength") * (power - 1);
        case "Anger":
          discardPile.add(new Card(card));
          break;
        case "Clash":
          break; //Relevant code is above; included to avoid error.
        case "Havoc":
          if(drawPile.size() == 0){
            break;
          }
          Card c = drawPile.remove(0);
          display();
          
          int startRow = App.settingsManager.screenHeight/2 - c.getImage().length/2;
          int startCol = App.settingsManager.screenWidth/2 - c.getImage()[0].length()/2;
          thisRun.displayScreenWithAddition(c.getImage(), startRow, startCol);
          Str.print("Playing and Exhausting " + c.getName() + ". (Press enter)");
          Main.scan.nextLine();
          
          energy += c.getEnergyCost();
          playCard(c);
          exhaust(c);
          break;
        default:
          System.out.println("(Error) Cannot compile card data: " + card + ", firstWord: " + firstWord);
      }
    }

    if(shouldDiscard){
      discardPile.add(card);
    }
    
    return true;
  }

  /**Prompts the user for the target and returns their response. Returns -1 if card play is cancelled (no possible target selected)
  */
  public int getTarget(){
    if(enemies.size() == 1){
      return 1;
    }
    System.out.println("Which enemy do you want to play this card on? (1-" + enemies.size() + "; type c to cancel)");
    String input = input();
    int val = -1;
    try{
      val = Integer.parseInt(input);
    }catch(NumberFormatException e){}
    if(val > 0 && val <= enemies.size()){
      return val;
    }
    return -1;
  }

  /**Returns an arraylist of the card represented by the expression in otherWords
   * @param otherWords The second word of the card effect, the meaning of which will be converted to an arraylist
   * @param current The card being currently played
   */
  public Card[] cardTargets(String otherWords, Card current){ //TODO: finish
    switch (otherWords) {
      case "":
      case "This":
        return new Card[] {current};
      case "TopFromDeck":
        return new Card[] {drawPile.get(0)};
      case "Hand":
        return (Card[]) hand.toArray(new Card[0]);
      case "RandHand":
        if(hand.isEmpty()) return new Card[]{}; //Empty selection if hand empty
        if(hand.size() == 1) return new Card[]{hand.get(0)}; //If only 1 option, select that option
        int rng = (int) (Math.random() * hand.size());
        return new Card[]{hand.get(rng)};
      case "Choose1FromHand": //todo: finish // it's finished already, right?
        if(hand.isEmpty()) return new Card[]{}; //Empty selection if hand empty
        if(hand.size() == 1) return new Card[]{hand.get(0)}; //If only 1 option, select that option
        display(); //TODO: necessary?
        while(true){
          try{
            String input = input("Select the position of a card in your hand: "); //todo: make all of these kinds of text popups into on-screen things like the card rewards screen?
            Card c = hand.get(Integer.parseInt(input)-1);
            return new Card[] {c};
          } catch(NumberFormatException | IndexOutOfBoundsException e){}
        }
        //No break since returns above
      case "Choose1FromDisc": //todo: finish
        if(discardPile.isEmpty()) return new Card[]{}; //Empty selection if discard pile empty
        display(); //TODO: necessary?
        while(true){
          try{
            String str = "Your discard pile:\n";
            for(Card discardCard : discardPile){
              str += discardCard.toString() + "\n"; //Constructing the discard pile text
            }
            Card c = null;
            while(c == null){ //Until they enter the name of a card in their discard pile
              String input = thisRun.popupInput(str, "Select the name of a card in your discard pile"); //todo: make all of these kinds of text popups into on-screen things like the card rewards screen?
              for(Card discardCard : discardPile){
                if(input.equalsIgnoreCase(discardCard.getName())){ //Checks if input is a card in the discard pile //TODO: make work w/ coloring the name; also just now I typed "flex" when (a green) "flex+" was available and it kinda broke
                  c = discardCard;
                }
              }
            }
            return new Card[] {c};
          } catch(NumberFormatException | IndexOutOfBoundsException e){}
        }
        //No break since returns above
      default:
        break;
    }
    return null;
  }

  public String input(){
    return input("");
  }

  /**Prompts the user for an input until they enter something that is not one of the popup commands. For each that is a command, does that command.
  */
  public String input(String prompt){
    while(true){
      Str.print(prompt); //TODO: Call this version of input more, where I'm already effectively using it?
      String input = thisRun.input();
      
      if(input.equalsIgnoreCase("draw")){
        ArrayList<Card> sortedDrawPile = sortedList(drawPile);
        String str = "Draw pile, sorted alphabetically:\n";
        for(Card c : sortedDrawPile){
          str += c.toString() + "\n";
        }
        thisRun.popup(str);
      }else if(input.equalsIgnoreCase("disc") || input.equalsIgnoreCase("discard")){
        String str = "Discard pile:\n";
        for(Card c : discardPile){
          str += c.toString() + "\n";
        }
        thisRun.popup(str);
      }else if(input.equalsIgnoreCase("exh") || input.equalsIgnoreCase("exhaust")){
        String str = "Exhausted Cards:\n";
        for(Card c : exhaustPile){
          str += c.toString() + "\n";
        }
        thisRun.popup(str);
      }else if(input.equalsIgnoreCase("status") || input.equalsIgnoreCase("stat")){
        String statuses = "Player Statuses:\n"; //TODO: Add some color to these headers
        for(Status s : player.getStatuses()){
          if(s.getStrength() != 0){
            statuses += s.getDisplay() + " - " + s.getName() + ": " + s.getDescriptionFormatted() + "\n";
          }
        }
        int i=0;
        for(Enemy enemy : enemies){
          statuses += Colors.reset + "\nEnemy " + (++i) + " Statuses:\n"; 
          for(Status s : enemy.getStatuses()){
            if(s.getStrength() != 0){
              statuses += s.getDisplay() + " - " + s.getName() + ": " + s.getDescriptionFormatted() + "\n";
            }
          }
        }
        thisRun.popup(statuses);
      }else{
        return input;
      }
    }
    
  }
  
  /**Adds the player's hand to the screen.
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
    int rowAboveCards = topRowOfCards -1;

    //Odd and even numbers and even numbers have different things going on in the center, so I found it easier to just have them go separately
    if(numCards%2 == 0){  //Even num of cards
      //#s above cards:
      int firstLabelCol = Run.SCREENWIDTH/2 - (int)((Card.CARDWIDTH+1)*((numCards-1)/2.0)); //Middle col - width*(numOfCardsToTheLeft-0.5)
      for(int i=0; i<numCards; i++){
        String iAsString = "" + (i+1);
        System.out.println(iAsString);
        thisRun.addToScreen(rowAboveCards, (firstLabelCol + i*(Card.CARDWIDTH+1)), iAsString, Colors.magenta, Colors.reset);
      }
      addEvenHandToScreen(cards);
    }else{               //Odd num of cards
      //#s above cards:
      int firstLabelCol = Run.SCREENWIDTH/2 - (Card.CARDWIDTH+1)*((numCards-1)/2); //Middle col - width*numOfCardsToTheLeft
      for(int i=0; i<numCards; i++){
        String iAsString = "" + (i+1);
        System.out.println(iAsString);
        thisRun.addToScreen(rowAboveCards, (firstLabelCol + i*(Card.CARDWIDTH+1)), iAsString, Colors.magenta, Colors.reset);
      }
      addOddHandToScreen(cards);
    }
  }

  /**Adds the hand to the screen if they hae an even number of cards in their hand.
  */
  private void addEvenHandToScreen(ArrayList<Card> cards){
    int numCards = cards.size();
    String[] cardArt0 = cards.get(0).getImageWStatuses(this);
    String[] cardArtLast = cards.get(cards.size()-1).getImageWStatuses(this);
    int middleRightCardStartingCol = Run.SCREENWIDTH/2 + 1;

    if(numCards == 2){
      thisRun.addToScreen(topRowOfCards, middleRightCardStartingCol-(Card.CARDWIDTH+1), cardArt0);
      thisRun.addToScreen(topRowOfCards, middleRightCardStartingCol, cardArtLast);
      return;
    }

    int numExtraCardsToEachSide = (numCards-2)/2; //Num of cards on each side of the middle cards
    
    int leftCardStartingCol = middleRightCardStartingCol - (Card.CARDWIDTH+1)*(numExtraCardsToEachSide+1);
    int rightCardStartingCol = middleRightCardStartingCol + (Card.CARDWIDTH+1)*numExtraCardsToEachSide;
    thisRun.addToScreen(topRowOfCards, leftCardStartingCol, cardArt0);
    thisRun.addToScreen(topRowOfCards, rightCardStartingCol, cardArtLast);

    ArrayList<Card> subHand = new ArrayList<Card>(cards.subList(1, cards.size()-1));
    addEvenHandToScreen(subHand);
  }

  /**Adds the hand to the screen if there are an odd number of cards in their hand
  */
  private void addOddHandToScreen(ArrayList<Card> cards){
    int numCards = cards.size();
    String[] cardArt0 = cards.get(0).getImageWStatuses(this);
    int middleCardStartingCol = Run.SCREENWIDTH/2 - (Card.CARDWIDTH/2);

    if(numCards == 1){
      thisRun.addToScreen(topRowOfCards, middleCardStartingCol, cardArt0);
      return;
    }

    String[] cardArtLast = cards.get(cards.size()-1).getImageWStatuses(this);
    int numExtraCardsToEachSide = (numCards-1)/2; //Num of cards on each side of the middle card
    
    int displacement = (Card.CARDWIDTH+1)*numExtraCardsToEachSide;
    int leftCardStartingCol = middleCardStartingCol - displacement;
    int rightCardStartingCol = middleCardStartingCol + displacement;
    thisRun.addToScreen(topRowOfCards, leftCardStartingCol, cardArt0);
    thisRun.addToScreen(topRowOfCards, rightCardStartingCol, cardArtLast);
    
    ArrayList<Card> subHand = new ArrayList<Card>(cards.subList(1, cards.size()-1));
    addOddHandToScreen(subHand);
  }


  /**Creates a box of ascii characters with a number in the center (ie the energy counter box). color is added only after the number in the middle line. textColor is the color of the number in the middle line.
   * valInCenter must be in [0,99]
  */
  public static String[] square(int height, int minWidth, int valInCenter, String color, String textColor){
    String[] square = new String[height];
    if(valInCenter < 9){
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

  /**Creates a box of ascii characters with a text in the center (ie the energy counter box). color is added only after the text in the middle line. textColor is the color of the text in the middle line.
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




  
  /**Returns a new ArrayList<Card> which containes the contents of the parameter ArrayList<Card> sorted alphabetically by name.
  *@return ArrayList<Card> - A new ArrayList<Card> of the sorted parameter list values
  *@param list - The list to make a sorted version of
  */
  public static ArrayList<Card> sortedList(ArrayList<Card> list){
    //Insertion Sort
    ArrayList<Card> sortedList = new ArrayList<Card>(list);
    for(int i=1; i<sortedList.size(); i++){
      String name = sortedList.get(i).getName();
      for(int j=0; j<i; j++){
        if(name.compareTo(sortedList.get(j).getName()) <= 0){
          sortedList.add(j, sortedList.remove(i));
          break;
        }
      }
    }

    return sortedList;
  }

  /**Discards the card at the specified index from hand.
  */
  public void discardCardFromHand(int index){
    Card c = hand.remove(index);
    discardPile.add(0, c);
  }

  /**Constructs and returns a louse of a random color
  */
  public Enemy createLouse(int midX){
    if(Math.random() < 0.5){
      return new RedLouse(midX, this);
    }
    return new GreenLouse(midX, this);
  }
  /**Constructs and returns a slime of the specified size and a random type
  *@Precondition - 0 >= size >= 2
  */
  public Enemy createSlime(int midX, int size){
    boolean isAcidSlime = Math.random() < 0.5; //50-50 chance
    if(isAcidSlime){ //Acid Slime:
      switch(size){
        case 0: //Small
          return new AcidSlimeSmall(midX, this);
        case 1: //Medium
          return new AcidSlimeMed(midX, this);
        case 2:
          return new AcidSlimeLarge(midX, this);
      }
    }else{ //Spike Slime:
      switch(size){
        case 0: //Small
          return new SpikeSlimeSmall(midX, this);
        case 1: //Medium
          return new SpikeSlimeMed(midX, this);
        case 2:
          return new SpikeSlimeLarge(midX, this);
      }
    }
    return null;
  }

  /**Constructs the enemies for Gremlin Gang combat and adds them to the enemy list
  */
  public void constructGremlinGang(){
    ArrayList<String> gremlins = new ArrayList<String>();
    gremlins.add("Fat");
    gremlins.add("Fat");
    gremlins.add("Sneaky");
    gremlins.add("Sneaky");
    gremlins.add("Mad");
    gremlins.add("Mad");
    gremlins.add("Shield");
    gremlins.add("Wizard");
    Collections.shuffle(gremlins);
    //Remove four:
    for(int i=0; i<4; i++){
      gremlins.remove(0);
    }
    
    //Construct remaining four:
    for(int i=0; i<gremlins.size(); i++){
      String str = gremlins.get(i);
      int midX = Run.SCREENWIDTH*(i+6)/10;
      switch(str){
        case "Fat":
          enemies.add(new FatGremlin(midX, this));
          break;
        case "Sneaky":
          enemies.add(new SneakyGremlin(midX, this));
          break;
        case "Mad":
          enemies.add(new MadGremlin(midX, this));
          break;
        case "Shield":
          enemies.add(new ShieldGremlin(midX, this));
          break;
        case "Wizard":
          enemies.add(new GremlinWizard(midX, this));
          break;
      }
    }
  }

  /**Constructs the enemies for Exordium Thugs combat and adds them to the enemy list
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
      enemies.add(new Looter(Run.SCREENWIDTH*4/5, this));
    }else if(rn<0.5){
      enemies.add(new Cultist(Run.SCREENWIDTH*4/5, this));
    }else if(rn<0.75){
      enemies.add(new BlueSlaver(Run.SCREENWIDTH*4/5, this));
    }else{
      enemies.add(new RedSlaver(Run.SCREENWIDTH*4/5, this));
    }
  }

  /**Constructs the enemies for Exordium Wildlife combat and adds them to the enemy list
  */
  public void constructWildlife(){
    double rn = Math.random();
    if(rn < 0.5){
      enemies.add(new FungiBeast(Run.SCREENWIDTH*2/3, this));
    }else{
      enemies.add(new JawWorm(Run.SCREENWIDTH*2/3, this));
    }
    rn = Math.random();
    if(rn < 0.5){
      enemies.add(createLouse(Run.SCREENWIDTH*4/5));
    }else{
      enemies.add(createSlime(Run.SCREENWIDTH*4/5, 1));
    }
  }

  /**Randomizes the list of enemies and their positions.
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

  /**Sets combatOver to true
  */
  public void endCombat(){
    combatOver = true;
  }
}