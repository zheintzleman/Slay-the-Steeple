package app;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Card implements Serializable {
	private static final long serialVersionUID = 1L;
  // private static final ArrayList<Card> availableCards = App.CARD_LIST; //Wasn't working as well
  public static final int CARDWIDTH = 21; //Odd
  public static final int CARDHEIGHT = 14;
  // public static final String[] TARGETEDCARDEFFECTS = new String[] {"Attack", "Apply"}; //List of card effects that trigger the playCard() method to prompt the player for a target
  
  public enum Rarity{
    BASIC,
    COMMON,
    UNCOMMON,
    RARE,
    NULL //TODO: Remove? Used to have for statuses but they're now switched to Common Rarity.
  }
  public enum Class{ //(Card Color)
    IRONCLAD,
    SILENT,
    DEFECT,
    WATCHER,
    NEUTRAL
  }
  //Anything that could change from being upgraded
  private class CardData implements Serializable {
    private Description description;
    private int energyCost; //Could be an Integer to include null for the X cost cards
    private boolean isTargeted;
    private ArrayList<CardEffect> effects; //Implemented in Combat.java

    public CardData(){}
    public CardData(CardData old){
      this.description = old.description;
      this.energyCost = old.energyCost;
      this.isTargeted = old.isTargeted;
      if(old.effects != null){
        this.effects = new ArrayList<CardEffect>(old.effects);
      }
    }
  }
  private class Description implements Serializable {
    private String codedDescription; //todo: Make final?
    //If having performance issues, could maybe store the WStatuses desc as well, and update it less

    public Description(String codedDescription){
      this.codedDescription = codedDescription;
    }
    public Description(ArrayList<CardEffect> effects){
      codedDescription = "";
      for(CardEffect eff : effects) {
        String effectPower = "" + eff.getPower(); //Used to be basePower instead of eff.getPower().
        String primary = eff.getPrimary();
        String secondary = eff.getSecondary();

        switch(primary){
          case "Attack":
            codedDescription += "Deal ØatkÁ" + effectPower + "ØendatkÁ damage.\n"; //Maybe make some applyStatuses()/w/e method in Combat or smth
            break;
          case "AtkAll": //Uses shortened word to be separate from "Attack" (& for brevity)
            codedDescription += "Deal ØatkÁ" + effectPower + "ØendatkÁ damage to ALL enemies.\n";
            break;
          case "AtkRandom":
            codedDescription += "Deal ØatkÁ" + effectPower + "ØendatkÁ damage to a random enemy."; //TODO: If eg 3 atks in a row, causes error if second kills the last enemy.
          case "SearingBlow":
            codedDescription += "Deal ØatkÁ" + searingBlowDamage() + "ØatkÁ damage.\nCan be upgraded any number of times.\n";
            break;
          case "Block":
            codedDescription += "Gain ØblkÁ" + effectPower + "ØendblkÁ block.\n";
            break;
          case "Apply":
            codedDescription += "Apply " + effectPower + " " + secondary + ".\n";
            break;
          case "AppAll":
            codedDescription += "Apply " + effectPower + " " + secondary + " to ALL enemies.\n";
            break;
          case "Exhaust":
            codedDescription += "Exhaust.\n";
            break;
          case "Draw":
            codedDescription += "Draw " + effectPower + (effectPower.equals("1") ? " card.\n" : " cards.\n");
            break;
          case "Upgrade":
            if(secondary.equals("Choose1FromHand")){
              codedDescription += "Upgrade a card in your hand for the rest of combat.\n";
              break;
            }else if(secondary.equals("Hand")){
              codedDescription += "Upgrade ALL cards in your hand for the rest of combat.\n";
              break;
            }else{
              codedDescription += "[Upgrade; Enter text in Card.java]\n";
              break;
            }
          case "PutOnDrawPile":
            switch(secondary){
              case "Choose1FromDisc":
                codedDescription += "Put a card from your discard pile on top of your draw pile.\n";
                break;
              case "Choose1FromHand":
                codedDescription += "Put a card from your hand onto the top of your draw pile.\n";
                break;
              default:
                codedDescription += "[PutOnDrawPile; Enter text in Card.java]\n";
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
          default:
            break;
        }
      }
    }
    //End Description Constructors

    public String getCodedDescription(){
      return codedDescription;
    }
    public String getBaseDescription(){
      String res = codedDescription;
      res = res.replaceAll("ØatkÁ", "");
      res = res.replaceAll("ØblkÁ", "");
      res = res.replaceAll("ØendatkÁ", "");
      res = res.replaceAll("ØendblkÁ", "");
      return res;
    }
    public String getBaseDescriptionWONLs(){
      String res = getBaseDescription().replace("\n", " ");
      return res.substring(0, res.length()-1);
    }
    //Takes into account the statuses of the player
    public String getDescriptionWStatuses(Combat combat){
      String res = codedDescription;
      while(res.contains("ØatkÁ")){ //Updates the attack #s
        int index = res.indexOf("ØatkÁ");
        int endIndex = res.indexOf("ØendatkÁ");
        App.ASSERT(endIndex != -1);

        int baseDamage = Integer.parseInt(res, index + 5, endIndex, 10);
        int newDamage = combat.getPlayer().calcAtkDmgFromThisStats(baseDamage); //todo: Display the full damage for each enemy below that enemy?
        String color = "";
        if(newDamage < baseDamage){ color = Colors.hpBarRed; }
        if(newDamage > baseDamage){ color = Colors.upgradeGreen; }
        res = res.substring(0, index) + color + newDamage + Colors.reset + res.substring(endIndex + 8);
      }
      while(res.contains("ØblkÁ")){ //Updates the block #s
        int index = res.indexOf("ØblkÁ");
        int endIndex = res.indexOf("ØendblkÁ");
        App.ASSERT(endIndex != -1);

        int baseBlock = Integer.parseInt(res, index + 5, endIndex, 10);
        int newBlock = combat.getPlayer().calcBlockAmount(baseBlock);
        String color = "";
        if(newBlock < baseBlock){ color = Colors.hpBarRed; }
        if(newBlock > baseBlock){ color = Colors.upgradeGreen; }
        res = res.substring(0, index) + color + newBlock + Colors.reset + res.substring(endIndex + 8);
      }

      return res;
    }
  }

  //~~~~~~~~~~~~~~~~~~~~~  Card Class Begins  ~~~~~~~~~~~~~~~~~~~~~

  private String name, type;
  private int upgrades;
  private Rarity rarity;
  private CardData data = new CardData();
  private CardData upData = new CardData(); //Data for the upgraded version of the card
  

  public Card(){
    name = "";
    type = "Skill";
    upgrades = 0;
    data.description = new Description("");
    data.energyCost = -1;
    data.isTargeted = false;
    rarity = Rarity.NULL;
    data.effects = new ArrayList<CardEffect>();
    upData = new CardData(data);
  }
  public Card(Card old){
    name = old.name;
    type = old.type;
    upgrades = 0;
    rarity = old.rarity;
    data = new CardData(old.data);
    upData = new CardData(old.upData);
  }
  public Card(String name){
    this(getCard(name));
  }
  private Card(String name, String type, int energyCost, boolean targeted, ArrayList<String> effects, Rarity rarity){
    this.name = name;
    this.type = type;
    upgrades = 0;
    this.rarity = rarity;
    data.energyCost = energyCost;
    data.isTargeted = targeted;
    data.effects = new ArrayList<CardEffect>();
    for(String str : effects){
      data.effects.add(new CardEffect(str));
    }
    data.description = new Description(data.effects);
  }
  public Card(String name, String type, int energyCost, boolean targeted, ArrayList<String> effects, ArrayList<String> upEffects, Rarity rarity){
    this(name, type, energyCost, targeted, effects, rarity);
    upData.energyCost = energyCost;
    upData.isTargeted = targeted;
    upData.effects = new ArrayList<CardEffect>();
    for(String str : upEffects){
      upData.effects.add(new CardEffect(str));
    }
    upData.description = new Description(upData.effects);
  }
  public Card(String name, String type, int energyCost, boolean targeted, ArrayList<String> effects,
              int upCost, boolean upTargeted, ArrayList<String> upEffects, Rarity rarity){
    this(name, type, energyCost, targeted, effects, rarity);
    upData.energyCost = upCost;
    upData.isTargeted = upTargeted;
    upData.effects = new ArrayList<CardEffect>();
    for(String str : upEffects){
      upData.effects.add(new CardEffect(str));
    }
    upData.description = new Description(upData.effects);
  }
  /**If card's description can be affected by str/dex, include the ØatkÁ / ØendatkÁ / ØblkÁ / ØendblkÁ around the number(s)
   */
  public Card(String name, String description, String type, int energyCost, boolean targeted, ArrayList<String> effects,
              String upDescription, int upCost, boolean upTargeted, ArrayList<String> upEffects, Rarity rarity){
    this(name, type, energyCost, targeted, effects, rarity);
    data.description = new Description(description);

    if(!name.equals("Twin Strike")){ //<-Whitelist
      // To get your attention. Read the above comment^.
      for(CardEffect eff : data.effects){
        App.ASSERT(!eff.isAttack() && !eff.isDefense());
      }
    }

    upData.energyCost = upCost;
    upData.isTargeted = upTargeted;
    upData.effects = new ArrayList<CardEffect>();
    for(String str : upEffects){
      upData.effects.add(new CardEffect(str));
    }
    upData.description = new Description(upDescription);
  }

  //Getters and setters
  // public static ArrayList<Card> availableCards(){ return availableCards; } //TODO: Remove?
  public String getName(){ return name; }
  public void setName(String newName){ name = newName; }
  public String getType(){ return type; }
  public void setType(String newType){ type = newType; }
  public int getEnergyCost(){ return data.energyCost; }
  public void setEnergyCost(int newCost){ data.energyCost = newCost; }
  public int getUpgrades(){ return upgrades; }
  public void setUpgrades(int newUpgrades){ upgrades = newUpgrades; }
  public boolean isTargeted(){ return data.isTargeted; }
  public void setIsTargeted(boolean newIsTargeted){ data.isTargeted = newIsTargeted; }
  public ArrayList<CardEffect> getEffects(){ return data.effects; }
  public void setEffects(ArrayList<CardEffect> newEffects){ data.effects = newEffects; }

  public String getDescription(){ return data.description.getBaseDescription(); }
  /**Description replacing \n characters with spaces (removes the terminal \n character w/o replacing it.) */
  public String getDescriptionWONLs(){ return data.description.getBaseDescriptionWONLs(); }
  public Description getDescriptionObject(){ return data.description; }
  /**Takes into account the statuses of the player */
  public String getDescriptionWStatuses(Combat c){ return data.description.getDescriptionWStatuses(c); }

  
  @Override
  public String toString(){
    return Colors.gray + (data.energyCost < 0 ? "" : "(" + Colors.energyCostRed + data.energyCost + Colors.gray + ") ")
    + Colors.reset + name + colorEveryWordBySpaces(" - " + getDescriptionWONLs(), Colors.gray) + "\n" + Colors.reset;
  }

  /**Returns whether or not this Card has the entered effect
  *@param effect - The effect to search this Card for
  *@return boolean - true if the entered effect is in this Card's list of effects. False otherwise.
  */
  public boolean hasEffect(String effect){
    for(CardEffect eff : data.effects){
      if(eff.getPrimary().equals(effect) && eff.getSecondary().isEmpty()){
        return true;
      }
    }
    return false;
  }
  /**Returns whether or not this Card has an effect which contains the entered String
  *@param effectType - The String to search this Card's effects for
  *@return boolean - true if the entered String is in this Card's list of effects. False otherwise.
  */
  public boolean hasEffectWith(String primary){
    for(CardEffect eff : data.effects){
      if(primary.equals(eff.getPrimary())){
        return true;
      }
    }
    return false;
  }

  /**Returns whether any of the card's effects have isAttack();
   * effectively shows whether or not the card is affected by strength
  */
  public boolean hasAttackEffect(){
    for(CardEffect eff : data.effects){
      if(eff.isAttack())
        return true;
    }
    return false;
  }
  /**Returns whether any of the card's effects have isDefense();
   * effectively shows whether or not the card is affected by dexterity
  */
  public boolean hasDefenseEffect(){
    for(CardEffect eff : data.effects){
      if(eff.isDefense())
        return true;
    }
    return false;
  }

  // /**Generates the card's description using its data.effects list //todo: Remove this comment block after a bit
  //  */
  // public String generateDescription(){
  //   return generateDescription(data.effects, true);
  // }
  // /**Generates the card's description using the entered effects list
  //  */
  // public String generateDescription(ArrayList<CardEffect> effects){
  //   return generateDescription(effects, true);
  // }
  // /**Generates the card's description using the entered effects list
  //  */
  // public String generateDescription(ArrayList<CardEffect> effects, boolean useBasePower){
  //   String desc = "";
  //   if(effects == null){ return ""; }

  //   for(CardEffect eff : effects){
  //     desc += eff.generateDescription(upgrades, useBasePower);
  //   }

  //   return desc;
  // }

  /**Returns the image of the card that will be displayed on the screen
   * using the card's base description (not accounting for statuses).
  */
  public String[] getImage(){
    return getImageWStatuses(null);
  }
  /**Returns the image of the card that will be displayed on the screen.
   * Uses base descriptions if combat == null, otherwise accounts for player statuses.
  */
  public String[] getImageWStatuses(Combat combat){ //TODO: Enemy images are displaying weirdly; louses have 3 spaces on the left & 5 on the right, while blue slaver is 1 & 0 respectively.
    String text = "";
    // text += name + "\n";
    Str.println(Str.concatArrayListWNL(Str.wrapText(name, CARDWIDTH-8))); // TODO: REMOVE
    text += Str.concatArrayListWNL(Str.wrapText(name, CARDWIDTH-8));
    text += "\n " + (combat == null ? getDescription() : getDescriptionWStatuses(combat)) + "\n";
    
    String[] image = Str.makeCenteredTextBox(text, CARDHEIGHT, CARDWIDTH); //Can make them up to 18 wide with width = 200; Up to 12 wide iirc with width = 150. 12 can work to fit long texts but the cards are really vertical.

    String energyCostString = data.energyCost < 0 ? "" : "" + data.energyCost;
    Str.println("Before:" + image[1]); //debug
    image[1] = Str.addStringsSkipEscSequences(image[1], 2, energyCostString, Colors.energyCostRedBold, Colors.reset);
    //debug:
    Str.println("After:" + image[1]);
    Str.println("Substring:" + Str.substringIgnoringEscSequences(image[1], 0, 2) + "<");
    
    return image;
  }

  public boolean isUpgradable(){
    if(name.equals("Burn")){
      return true;
    }
    if(type.equals("Status") || type.equals("Curse")){
      return false;
    }
    if(upgrades == 0){
      return true;
    }
    return hasEffect("SearingBlow");
  }

  /**Attempts to upgrade the card if isUpgradable(), applying all necessary changes. */
  public void upgrade(){
    if(!isUpgradable()){
      return;
    }

    Str.println("Upgrades:" + upgrades);
      
    upgrades++;
    if(upgrades == 1) {
      name += "+";            //Change Name
      name = Colors.fillColor(name, Colors.upgradeGreen);
      CardData temp = data;   //Swap data and upData
      data = upData;
      upData = temp;
    }else{ //More than 1 upgrade (Searing Blow later upgrades:)
      App.ASSERT(name.lastIndexOf("+") != -1);
      name = name.substring(0, name.lastIndexOf("+")+1) + upgrades;                   //Changes the Name
      data.description = new Description(data.effects);
    }

  }
  
  /**Calculates the damage searing blow would do using the # of upgrades on the card.*/
  public int searingBlowDamage(){
    return upgrades*(upgrades+7)/2 + 12;
  }

  /**Puts the specified color after each space and '\n' in the text.
  *@Precondition - Text doesn't start with a space; two spaces aren't in a row.
  */
  public static String colorEveryWordBySpaces(String theText, String color){ //TODO: Used once and in a way that violates the precondition lol
    // String[] wordsBySpaces = theText.split(" ");
    // String text = "";
    // for(String str : wordsBySpaces){
    //   text += " " + color + str;
    // }
    // return text.substring(1); //To remove the starting " " //TODO: remove commented part
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



  /**Returns the card with the entered name from the list of available cards.
  */
  public static Card getCard(String name){
    for(Card c : App.CARD_LIST){
      if(name.equals(c.getName())){ //TODO: Make more efficient with hash sets with strings as keys?
        return c;
      }
    }

    //Shouldn't get here
    for(Card c : App.CARD_LIST){
      Str.println("C: " + c.name);
    }

    throw new RuntimeException("Card \"" + name + "\" not in App.CARD_LIST list.");
  }
  
  
}