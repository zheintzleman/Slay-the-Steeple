package app;
import java.util.*;

/** Class for cards that the player (usually) plays on their turn, that make up the deck, etc.
 * Also contains related public enums and private classes.
 * 
 * @see Combat
 * @see CardEffect
 * @see Combat Combat.playCard()
 * @see App App.loadCards()
 * @see Card.Rarity
 * @see Card.Color
 * @see Card.CardData
 * @see Card.Description
 */
public class Card {
  public static final int CARDWIDTH = 21; //Odd
  public static final int CARDHEIGHT = 14;
  
  /** Card rarity; determines frequency a card shows up */
  public enum Rarity{
    BASIC,
    COMMON,
    UNCOMMON,
    RARE,
  }
  /** Card color (i.e. character) */
  public enum Color{
    IRONCLAD,
    SILENT,
    DEFECT,
    WATCHER,
    NEUTRAL
  }
  /** Anything that could change from being upgraded.
   * Card stores two of these -- one for base values, and one for upgraded values.
   */
  private class CardData {
    private Description description;
    private int baseEnergyCost;
    private int energyCost; //Could be an Integer to include null for the X cost cards
    private boolean isTargeted;
    private ArrayList<CardEffect> effects; //Implemented in Combat.java

    public CardData(){}
    public CardData(CardData old, Card c){
      this.description = new Description(old.description);
      this.baseEnergyCost = old.baseEnergyCost;
      this.isTargeted = old.isTargeted;
      if(old.effects != null){
        this.effects = new ArrayList<CardEffect>();
        for(CardEffect eff : old.effects){
          this.effects.add(new CardEffect(eff, c));
        }
      }
    }
  }
  /** Represents the description of a card (i.e. the part that shows on-screen.)
   * Stored with <> tags around numbers that change color based on the current combat environment
   * (e.g. attack numbers being green when doing more than base damage.)
   * 
   * @see App App.loadCards()
   */
  private class Description {
    private String codedDescription;
    //If having performance issues, could maybe store the WStatuses desc as well, and update it less

    public Description(String codedDescription){
      this.codedDescription = codedDescription;
    }
    public Description(Description prev){
      this.codedDescription = prev.codedDescription;
    }
    public Description(ArrayList<CardEffect> effects){
      codedDescription = "";
      for(CardEffect eff : effects) {
        String effectPower = "" + eff.getPower(); //Used to be basePower instead of eff.getPower().
        String primary = eff.getPrimary();
        String secondary = eff.getSecondary();

        switch(primary){
          case "Attack":
            codedDescription += "Deal <atk>" + effectPower + "<endatk> damage.\n"; //Maybe make some applyStatuses()/w/e method in Combat or smth
            break;
          case "AtkAll": //Uses shortened word to be separate from "Attack" (& for brevity)
            codedDescription += "Deal <atk>" + effectPower + "<endatk> damage to ALL enemies.\n";
            break;
          case "AtkRandom":
            codedDescription += "Deal <atk>" + effectPower + "<endatk> damage to a random enemy.\n";
          case "SearingBlow":
            codedDescription += "Deal <atk>" + searingBlowDamage() + "<endatk> damage.\nCan be upgraded any number of times.\n";
            break;
          case "Block":
            codedDescription += "Gain <blk>" + effectPower + "<endblk> block.\n";
            break;
          case "Apply":
            codedDescription += "Apply " + effectPower + " " + secondary + ".\n";
            break;
          case "AppAll":
            codedDescription += "Apply " + effectPower + " " + secondary + " to ALL enemies.\n";
            break;
          case "Exhaust":
            switch(secondary){
              case "Choose1FromHand":
                codedDescription += "Exhaust 1 card.\n";
                break;
              default:
                codedDescription += "Exhaust.\n";
                break;
            }
            break;
          case "Draw":
            codedDescription += "Draw " + effectPower + (effectPower.equals("1") ? " card.\n" : " cards.\n");
            break;
          case "Upgrade":
            switch(secondary){
              case "Choose1FromHand":
                codedDescription += "Upgrade a card in your hand for the rest of combat.\n";
                break;
              case "Hand":
                codedDescription += "Upgrade ALL cards in your hand for the rest of combat.\n";
                break;
              default:
                codedDescription += "[Upgrade; Enter text in Card.java].\n";
                break;
            }
            break;
          case "PutOnDrawPile":
            switch(secondary){
              case "Choose1FromDisc":
                codedDescription += "Put a card from your discard pile on top of your draw pile.\n";
                break;
              case "Choose1FromHand":
                codedDescription += "Put a card from your hand onto the top of your draw pile.\n";
                break;
              default:
                codedDescription += "[PutOnDrawPile; Enter text in Card.java].\n";
                break;
            }
            break;
            // if(secondary.equals("Choose1FromDisc")){
            //   codedDescription += "Put a card from your discard pile on top of your draw pile.\n";
            //   break;
            // }else{
            //   codedDescription += "[PutOnDrawPile; Enter text in Card.java]\n";
            //   break;
            // }
          case "Anger":
            codedDescription += "Add a copy of this card into your discard pile.\n";
            break;
          case "Clash":
            codedDescription += "Can only be played if every card in your hand is an Attack.\n";
            break;
          case "BodySlam":
            codedDescription += "Deal damage equal to your block.\n"; //todo: Add "(deals X damage)"?
            break;
          case "Havoc":
            codedDescription += "Play the top card of your draw pile and Exhaust it.\n";
            break;
          case "Unplayable":
            codedDescription += "Unplayable.\n";
            break;
          case "Ethereal":
            codedDescription += "Ethereal.\n";
            break;
          case "HeavyAttack":
            codedDescription += "Deal <atk>14<endatk> damage.\nStrength affects this card " + effectPower + " times.\n";
            break;
          case "GainToDraw":
            codedDescription += "Shuffle a " + secondary + " into your draw pile.\n";
            break;
          case "LoseHP":
            codedDescription += "Lose " + effectPower + " HP.\n";
            break;
          case "GainEnergy":
            codedDescription += "Gain " + effectPower + " energy.\n";
          default:
            break;
        }
        App.ASSERT(codedDescription.endsWith(".\n") || codedDescription.isEmpty());
      }
    }
    //End Description Constructors

    public String getCodedDescription(){
      return codedDescription;
    }
    public String getBaseDescription(){
      String res = codedDescription;
      res = res.replaceAll("<atk>", "");
      res = res.replaceAll("<blk>", "");
      res = res.replaceAll("<endatk>", "");
      res = res.replaceAll("<endblk>", "");
      return res;
    }
    public String getBaseDescriptionWONLs(){
      String res = getBaseDescription().replace("\n", " ");
      if(res.isEmpty()){ return res; }
      return res.substring(0, res.length()-1);
    }
    /**
     * Returns the card's description, with atk & blk amounts changed based on
     * the current statuses & other combat information. Does not change
     * underlying card description.
     * @param combat
     * @param strMultiplier The amount of times to multiply strength by (for
     *  heavy blade.) By default 1.
     * @return The formatted card description
     */
    public String getDescriptionWStatuses(Combat combat, int strMultiplier){
      String res = codedDescription;
      while(res.contains("<atk>")){ //Updates the attack #s
        int index = res.indexOf("<atk>");
        int endIndex = res.indexOf("<endatk>");
        App.ASSERT(endIndex != -1);

        int baseDamage = Integer.parseInt(res, index + 5, endIndex, 10);
        int newDamage = combat.getPlayer().calcAtkDmgFromThisStats(baseDamage, strMultiplier); //todo: Display the full damage for each enemy below that enemy?
        String color = "";
        if(newDamage < baseDamage){ color = Colors.energyCostRed; }
        if(newDamage > baseDamage){ color = Colors.upgradeGreen; }
        res = res.substring(0, index) + color + newDamage + Colors.reset + res.substring(endIndex + 8);
      }
      while(res.contains("<blk>")){ //Updates the block #s
        int index = res.indexOf("<blk>");
        int endIndex = res.indexOf("<endblk>");
        App.ASSERT(endIndex != -1);

        int baseBlock = Integer.parseInt(res, index + 5, endIndex, 10);
        int newBlock = combat.getPlayer().calcBlockAmount(baseBlock);
        String color = "";
        if(newBlock < baseBlock){ color = Colors.energyCostRed; }
        if(newBlock > baseBlock){ color = Colors.upgradeGreen; }
        res = res.substring(0, index) + color + newBlock + Colors.reset + res.substring(endIndex + 8);
      }

      return res;
    }
  }

  //~~~~~~~~~~~~~~~~~~~~~  Card Class Begins  ~~~~~~~~~~~~~~~~~~~~~

  private String name, type;
  private int energyCost, upgrades;
  private Rarity rarity;
  private Color color;
  private CardData data = new CardData();
  private CardData upData = new CardData(); //Data for the upgraded version of the card
  

  public Card(){
    name = "";
    type = "Skill";
    upgrades = 0;
    data.description = new Description("");
    data.baseEnergyCost = energyCost = -1;
    data.isTargeted = false;
    rarity = Rarity.COMMON;
    color = Color.NEUTRAL;
    data.effects = new ArrayList<CardEffect>();
    upData = new CardData(data, this);
  }
  public Card(Card old){
    name = old.name;
    type = old.type;
    upgrades = 0;
    energyCost = old.energyCost;
    rarity = old.rarity;
    color = old.color;
    data = new CardData(old.data, this);
    upData = new CardData(old.upData, this);
  }
  public Card(String name){
    this(getCard(name));
  }
  private Card(String name, String type, int energyCost, boolean targeted, List<String> effects, Rarity rarity, Color color) {
    this.name = name;
    this.type = type;
    upgrades = 0;
    this.rarity = rarity;
    this.color = color;
    data.baseEnergyCost = this.energyCost = energyCost;
    data.isTargeted = targeted;
    data.effects = new ArrayList<CardEffect>();
    for(String str : effects){
      data.effects.add(new CardEffect(str, this));
    }
    data.description = new Description(data.effects);
  }
  public Card(String name, String type, int energyCost, boolean targeted, List<String> effects,
              List<String> upEffects, Rarity rarity, Color color){
    this(name, type, energyCost, targeted, effects, rarity, color);
    upData.baseEnergyCost = energyCost;
    upData.isTargeted = targeted;
    upData.effects = new ArrayList<CardEffect>();
    for(String str : upEffects){
      upData.effects.add(new CardEffect(str, this));
    }
    upData.description = new Description(upData.effects);
  }
  public Card(String name, String type, int energyCost, boolean targeted, List<String> effects,
              int upCost, boolean upTargeted, List<String> upEffects, Rarity rarity, Color color){
    this(name, type, energyCost, targeted, effects, rarity, color);
    upData.baseEnergyCost = upCost;
    upData.isTargeted = upTargeted;
    upData.effects = new ArrayList<CardEffect>();
    for(String str : upEffects){
      upData.effects.add(new CardEffect(str, this));
    }
    upData.description = new Description(upData.effects);
  }
  /** If card's description can be affected by str/dex, include the <atk> / <endatk> / <blk> / <endblk> around the number(s)
   */
  public Card(String name, String description, String type, int energyCost, boolean targeted, List<String> effects,
              String upDescription, int upCost, boolean upTargeted, List<String> upEffects, Rarity rarity, Color color){
    this(name, type, energyCost, targeted, effects, rarity, color);
    data.description = new Description(description);

    if(!(name.equals("Twin Strike")
      || name.equals("Sword Boomerang")
      || name.equals("Blood for Blood")
      || name.equals("Dropkick"))){ //<-Whitelist
      // To get your attention. Read the above Javadoc comment.
      for(CardEffect eff : data.effects){
        App.ASSERT(!eff.isAttack() && !eff.isDefense());
      }
    }

    upData.baseEnergyCost = upCost;
    upData.isTargeted = upTargeted;
    upData.effects = new ArrayList<CardEffect>();
    for(String str : upEffects){
      upData.effects.add(new CardEffect(str, this));
    }
    upData.description = new Description(upDescription);

    App.ASSERT((description.endsWith(".\n") || description.isEmpty())
            && (upDescription.endsWith(".\n") || upDescription.isEmpty()));
  }

  //Getters and setters
  public String getName(){ return name; }
  public void setName(String newName){ name = newName; }
  public String getType(){ return type; }
  public void setType(String newType){ type = newType; }
  public int getBaseEnergyCost(){ return data.baseEnergyCost; }
  public void setBaseEnergyCost(int newCost){
    data.baseEnergyCost = newCost >= 0 ? newCost : 0;
  }
  public int getEnergyCost(){ return energyCost; } //TODO: Factor in e.g. corruption & stuff?
  public void setEnergyCost(int newCost){
    energyCost = newCost >= 0 ? newCost : 0;
  }
  public int getUpgrades(){ return upgrades; }
  public void setUpgrades(int newUpgrades){ upgrades = newUpgrades; }
  public boolean isTargeted(){ return data.isTargeted; }
  public void setIsTargeted(boolean newIsTargeted){ data.isTargeted = newIsTargeted; }
  public ArrayList<CardEffect> getEffects(){ return data.effects; }
  public void setEffects(ArrayList<CardEffect> newEffects){ data.effects = newEffects; }

  public String getDescription(){ return data.description.getBaseDescription(); }
  /** Description replacing \n characters with spaces (removes the terminal \n character w/o replacing it.) */
  public String getDescriptionWONLs(){ return data.description.getBaseDescriptionWONLs(); }
  public Description getDescriptionObject(){ return data.description; }
  /** Takes into account the statuses of the player */
  public String getDescriptionWStatuses(Combat c){
    int strMultiplier = 1;
    for(CardEffect eff : getEffects()){
      if(eff.getPrimary().equals("HeavyAttack")){
        strMultiplier = eff.getPower();
      }
    }
    return data.description.getDescriptionWStatuses(c, strMultiplier);
  }

  public boolean isAttack(){
    return type.equals("Attack");
  }
  public boolean isSkill(){
    return type.equals("Skill");
  }
  public boolean isPower(){
    return type.equals("Power");
  }
  
  @Override
  public String toString(){
    // String energyCostColor = getEnergyCost() > getBaseEnergyCost() ? Colors.energyCostRed :
    //                          getEnergyCost() == getBaseEnergyCost() ? Colors.reset :
    //                                                                  Colors.upgradeGreen;
    return Colors.lightGray + (energyCost < 0 ? "" : "(" + Colors.energyCostRed + energyCost + Colors.lightGray + ") ")
    + Colors.reset + name + colorEveryWordBySpaces(" - " + getDescriptionWONLs(), Colors.lightGray) + "\n" + Colors.reset;
  }

  /** Returns whether or not this Card has the entered effect
  * @param effect - The effect to search this Card for
  * @return boolean - true if the entered effect is in this Card's list of effects. False otherwise.
  */
  public boolean hasEffect(String effect){
    for(CardEffect eff : data.effects){
      if(effect.equals(eff.getPrimary()) && eff.getSecondary().isEmpty()){
        return true;
      }
    }
    return false;
  }
  /** Returns whether or not this Card has an effect which contains the entered String
  * @param effectType - The String to search this Card's effects for
  * @return boolean - true if the entered String is in this Card's list of effects. False otherwise.
  */
  public boolean hasEffectWith(String primary){
    for(CardEffect eff : data.effects){
      if(primary.equals(eff.getPrimary())){
        return true;
      }
    }
    return false;
  }

  /** Returns whether any of the card's effects have isAttack();
   * effectively shows whether or not the card is affected by strength
  */
  public boolean hasAttackEffect(){
    for(CardEffect eff : data.effects){
      if(eff.isAttack())
        return true;
    }
    return false;
  }
  /** Returns whether any of the card's effects have isDefense();
   * effectively shows whether or not the card is affected by dexterity
  */
  public boolean hasDefenseEffect(){
    for(CardEffect eff : data.effects){
      if(eff.isDefense())
        return true;
    }
    return false;
  }


  /** Returns the image of the card that will be displayed on the screen
   * using the card's base description (not accounting for statuses).
  */
  public String[] getImage(){
    return getImageWStatuses(null);
  }
  /** Returns the image of the card that will be displayed on the screen.
   * Uses base descriptions if combat == null, otherwise accounts for player statuses.
  */
  public String[] getImageWStatuses(Combat combat){
    String text = "";
    text += Str.concatArrayListWNL(Str.wrapText(name, CARDWIDTH-7));
    text += "\n " + (combat == null ? getDescription() : getDescriptionWStatuses(combat)) + "\n";
    
    String[] image = Str.makeCenteredTextBox(text, CARDHEIGHT, CARDWIDTH); //Can make them up to 18 wide with width = 200; Up to 12 wide iirc with width = 150. 12 can work to fit long texts but the cards are really vertical.

    String energyCostString = energyCost < 0 ? "" : "" + energyCost;
    image[1] = Str.addStringsSkipEscSequences(image[1], 2, energyCostString, Colors.energyCostRedBold, Colors.reset);
    
    return image;
  }

  public boolean isUpgradable(){
    // if(name.equals("Burn")){
    //   return true;
    // }
    if(type.equals("Status") || type.equals("Curse")){
      return false;
    }
    if(upgrades == 0){
      return true;
    }
    return hasEffect("SearingBlow");
  }

  /** Attempts to upgrade the card if isUpgradable(), applying all necessary changes. */
  public void upgrade(){
    if(!isUpgradable()){
      return;
    }

    Str.println("Upgrades:" + upgrades);
      
    upgrades++;
    if(upgrades == 1){
      //Name:
      name += "+";
      name = Colors.fillColor(name, Colors.upgradeGreen);
      //Cost:=
      changeCostOnUpgrade();
      //Data:
      CardData temp = data;   //Swap data and upData
      data = upData;
      upData = temp;
    }else{ //More than 1 upgrade (Searing Blow later upgrades:)
      App.ASSERT(name.lastIndexOf("+") != -1);
      name = name.substring(0, name.lastIndexOf("+")+1) + upgrades;                   //Changes the Name's #
      data.description = new Description(data.effects);
    }
  }

  public void changeCostOnUpgrade(){
    //If card costs 0
    if(energyCost < 0 || data.baseEnergyCost == upData.baseEnergyCost){
      return;
    }
    //This one card functions differently for some reason. As far as I can tell the
    //original devs just added a bunch of hard coded exceptions for it.
    if(Str.equalsSkipEscSeqs(name, "Blood for Blood")
    || Str.equalsSkipEscSeqs(name, "Blood for Blood+")){
      setEnergyCost(energyCost-1);
      return;
    }

    energyCost = upData.baseEnergyCost;
  }
  
  /** Calculates the damage searing blow would do using the # of upgrades on the card.*/
  public int searingBlowDamage(){
    return upgrades*(upgrades+7)/2 + 12;
  }

  /** Puts the specified color after each space and '\n' in the text.
  * @Precondition - Two spaces aren't in a row.
  */
  public static String colorEveryWordBySpaces(String theText, String color){
    String text = theText;
    for(int i=0; i < text.length(); i++){
      char c = text.charAt(i);
      if(c == ' ' || c == '\n'){
        text = text.substring(0, i+1) + color + text.substring(i+1);
        i += color.length();
      }
    }
    return text;
  }



  /** Returns the card with the entered name from the set of available cards.
   * @Postcondition - Returns a card in App.CARDS -- doesn't return null
  */
  public static Card getCard(String name){
    Card card = App.CARDS.get(name);

    if(card == null){
      System.out.println("Card \"" + name + "\" not found. Card list:");
      for(Card c : App.CARDS.values()){
        Str.println("C: " + c.name);
      }
      throw new NoSuchElementException("Card \"" + name + "\" not in App.CARDS.");
    }

    return card;
  }
  
  
}