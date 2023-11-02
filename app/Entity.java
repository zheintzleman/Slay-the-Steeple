package app;
import java.util.*;

public class Entity{
  private String name;
  private int hp, maxHP, hpBarLength, block, startOfTurnBlock;
  private String[] art;
  public static final String[] RECTANGLE = new String[] {"███████", "███████", "███████", "███████", "███████", "███████"};
  private ArrayList<Status> statuses;
  private Combat combat;

  public Entity(){
    name = "<Entity>";
    maxHP = (int)(Math.random()*10) + 45;
    hp = maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = copyStatusList(Statuses.allStatuses);
  }
  public Entity(String name, Combat c){
    this.name = name;
    this.maxHP = (int)(Math.random()*10) + 45;
    this.hp = this.maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = copyStatusList(Statuses.allStatuses);
    this.combat = c;
  }
  public Entity(String name, int hp, Combat c){
    this.name = name;
    this.maxHP = hp;
    this.hp = this.maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = copyStatusList(Statuses.allStatuses);
    this.combat = c;
  }
  public Entity(String name, int hp, int hpBarLength, Combat c){
    this.name = name;
    this.maxHP = hp;
    this.hp = this.maxHP;
    this.hpBarLength = hpBarLength;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = copyStatusList(Statuses.allStatuses);
    this.combat = c;
  }
  public Entity(String name, int hp, String[] art, Combat c){
    this.name = name;
    this.maxHP = hp;
    this.hp = this.maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    this.art = art;
    statuses = copyStatusList(Statuses.allStatuses);
    this.combat = c;
  }
  public Entity(String name, int hp, int maxHP, String[] art, Combat c){
    this.name = name;
    this.maxHP = maxHP;
    this.hp = hp;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    this.art = art;
    statuses = copyStatusList(Statuses.allStatuses);
    this.combat = c;
  }
  public Entity(String name, int hp, int hpBarLength, Combat c, String[] art){
    this.name = name;
    this.hp = this.maxHP = hp;
    this.hpBarLength = hpBarLength;
    startOfTurnBlock = block = 0;
    this.art = art;
    statuses = copyStatusList(Statuses.allStatuses);
    this.combat = c;
  }
  public Entity(Entity e){
    this.name = e.name;
    this.maxHP = e.maxHP;
    this.hp = e.hp;
    this.hpBarLength = e.hpBarLength;
    this.block = e.block;
    startOfTurnBlock = 0;
    this.art = e.art.clone();
    this.statuses = copyStatusList(e.statuses);
    this.combat = e.combat;
  }
  
  //Getters and Setters
  public String getName(){ return name; }
  public void setName(String newName){ name = newName; }
  public int getHP(){ return hp; }
  public void setHP(int newHP){ hp = newHP; }
  public int getMaxHP(){ return maxHP; }
  public void setMaxHP(int newMaxHP){ maxHP = newMaxHP; }
  public int getStartOfTurnBlock(){ return startOfTurnBlock; }
  public void setStartOfTurnBlock(int newBlock){ startOfTurnBlock = newBlock; }
  public int getBlock(){ return block; }
  public void setBlock(int newBlock){ block = newBlock; }
  public String[] getArt(){ return art; }
  public void setArt(String[] newArt){ art = newArt; }
  public int getHPBarLength(){ return hpBarLength; }
  public void setHPBarLength(int newHPBarLength){ hpBarLength = newHPBarLength; }
  public ArrayList<Status> getStatuses(){ return statuses; } 
  public void setStatuses(ArrayList<Status> newStatus){ statuses = newStatus; }
  public Combat getCombat(){ return combat; }
  public void setCombat(Combat newCombat){ combat = newCombat; }
  public int getVuln(){ return this.getStatusStrength("Vulnerable"); }
  public void setVuln(int newVuln){ setStatusStrength("Vulnerable", newVuln); }
  public int getWeak(){ return this.getStatusStrength("Weak"); }
  public void setWeak(int newWeak){ setStatusStrength("Weak", newWeak); }
  public int getFrail(){ return this.getStatusStrength("Frail"); }
  public void setFrail(int newFrail){ setStatusStrength("Frail", newFrail); }
  public int getStrength(){ return this.getStatusStrength("Strength"); }
  public void setStrength(int newStrength){ setStatusStrength("Strength", newStrength); }
  public int getDexterity(){ return this.getStatusStrength("Dexterity"); }
  public void setDexterity(int newDexterity){ setStatusStrength("Dexterity", newDexterity); }

  
  /**Returns this entity's status with the specified name.
  */
  public Status getStatus(String name){
    for(Status s : statuses){
      if(s.getName().equals(name)){
        return s;
      }
    }
    return null;
  }
  
  /**Returns this entity's strength of the status with the specified name.
  */
  public int getStatusStrength(String name){
    for(Status s : statuses){
      if(s.getName().equals(name)){
        return s.getStrength();
      }
    }
    return -1; 
  }
  /**Sets this entity's strength of the status with the specified name.
  */
  public void setStatusStrength(String name, int strength){
    for(int i=0; i<statuses.size(); i++){
      if(statuses.get(i).getName().equals(name)){
        statuses.get(i).setStrength(strength);
      }
    }
    return;
  }
  /**Adds strength to this entity's status with the specified name.
  */
  public void addStatusStrength(String name, int strength){
    for(int i=0; i<statuses.size(); i++){
      Status s = statuses.get(i);
      if(s.getName().equals(name)){
        s.addStrength(strength);
      }
    }
    return;
  }
  /**Subtracts strength from this entity's status with the specified name.
  */
  public void subtractStatusStrength(String name, int strength){
    for(int i=0; i<statuses.size(); i++){
      if(statuses.get(i).getName().equals(name)){
        statuses.get(i).subtractStrength(strength);
      }
    }
    return;
  }
  /**Returns whether or not this enemy has the status in any strength besides 0.
  */
  public boolean hasStatus(String name){
    return getStatusStrength(name) != 0;
  }
  /**Adds strength to this entity's with the specified name. If the strength was 0 and the status will decrease at the end of the turn, adds 1 to its strength to counteract that.
  */
  public void addStatusStrengthDuringEndOfTurn(String name, int strength){
    for(int i=0; i<statuses.size(); i++){
      Status s = statuses.get(i);
      if(s.getName().equals(name)){
        //So that it's not immediately removed:
        if(s.isDecreasing() && s.getStrength() == 0){
          strength++;
        }
        s.addStrength(strength);
      }
    }
    return;
  }


  
  /**Damages the entity the specified amount; taking from its block then its hp. If hp is 0 or less, it dies.
  *@return int - the amount of damage delt
  */
  public int damage(int dmg){
    if(block >= dmg){
      block -= dmg;
      return 0;
    }else{
      dmg -= block;
      block = 0;
      subtractHP(dmg);
      return dmg;
    }
  }
  /**Subtracts the entity's hp the specified amount. If hp is 0 or less, it dies.
  */
  public void subtractHP(int dmg){
    hp -= dmg;
    if(hp <= 0){
      hp = 0;
      this.die();
    }
    if(this.hasStatus("Split") && hp <= (maxHP/2)){
      this.setSplitIntent();
    }
  }
  /**Increases the entity's health the specified amount, up to its max health.
  */
  public void heal(int heal){
    hp += heal;
    if(hp > maxHP){
      hp = maxHP;
    }
  }
  /**Increases the entity's block the specified amount, up to 999. Does not account for frail or dexterity.
  */
  public void addBlock(int blk){
    block += blk;
    if(block > 999){
      block = 999;
    }
  }
  /**Increases the entity's start of turn block the specified amount. Does not account for frail or dexterity.
  */
  public void addStartOfTurnBlock(int blk){
    startOfTurnBlock += blk;
  }
  /**Gives the entity the appropriate amount of block, taking into account relevent status effects.
  *@param blockPreCalculations - The amount of defence the card/intent does (ie. 5 for an unupgraded Defend)
  */
  public void block(int blockPreCalculations){
    int blk = calcBlockAmount(blockPreCalculations);
    this.addBlock(blk);
  }
  /**Gives the receiving entity the appropriate amount of block, taking into account relevent status effects (largely, if not entirely, on this entity).
  *@param receiver - The entity to receive the block
  *@param blockPreCalculations - The amount of defence the card/intent does (ie. 5 for an unupgraded Defend)
  */
  public void giveBlock(Entity receiver, int blockPreCalculations){
    int blk = calcBlockAmount(blockPreCalculations);
    receiver.addBlock(blk);
  }
  /**Calculates the amount a block card with the entered amount would block for, taking into account relevent status effects.
  *@param blockPreCalculations - The amount the base card would say (ie. 5 for an unupgraded Defend)
  *@return int - The amount of block that would be gained by playing such a card, taking into account dexterity and frail.
  */
  public int calcBlockAmount(int blockPreCalculations){
    double blk = blockPreCalculations + this.getStatusStrength("Dexterity");
    if(this.getStatusStrength("Frail") > 0){
      blk *= 0.75;
    }
    return (int)(blk + 0.00000001); //In case of floating point errors
  }
  /**Increases the startOfTurnBlock variable according to relevent status effects
  *@param blockPreCalculations - The amount of defence the card/intent does (ie. 5 for an unupgraded Defend)
  */
  public void blockAfterTurn(int blockPreCalculations){
    int blk = calcBlockAmount(blockPreCalculations);
    addStartOfTurnBlock(blk);
  }
  /**Increases the receiving entity's startOfTurnBlock variable according to relevent status effects (largely, if not entirely, on this entity)
  *@param receiver - The entity to receive the future block
  *@param blockPreCalculations - The amount of defence the card/intent does (ie. 5 for an unupgraded Defend)
  */
  public void giveBlockDuringEndOfTurn(Entity receiver, int blockPreCalculations){
    int blk = calcBlockAmount(blockPreCalculations);
    receiver.addStartOfTurnBlock(blk);
  }
  /**Attacks the victim entity for the specified amount. Takes into account relevent status effects. Takes from the victims block, then its hp.
  *@param victim - The entity being attacked by the entity calling this method
  *@param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
  *@return int - The amount of attack damage delt
  */
  public int attack(Entity victim, int damagePreCalculations){
    int dmg = calcAttackDamage(victim, damagePreCalculations);
    int dmgDelt = victim.damage(dmg);
    if(dmgDelt > 0){              //OnTakingAttackDamage:
      if(victim.hasStatus("Curl Up")){ //Curl Up
      victim.addBlock(victim.getStatusStrength("Curl Up"));
      victim.setStatusStrength("Curl Up", 0);
      }
      if(victim.hasStatus("Angry")){
        victim.addStatusStrength("Strength", victim.getStatusStrength("Angry"));
        System.out.println("Victim Anger: " + victim.getStatusStrength("Angry"));
      }
    }
    return dmgDelt;
  }
  
  public int calcAttackDamage(Entity victim, int damagePreCalculations){
    double dmg = calcAtkDmgFromThisStats(damagePreCalculations);
    if(victim.getStatusStrength("Vulnerable") > 0){
      dmg *= 1.5;
    }
    return (int)(dmg + 0.00000001); //In case of floating point errors
  }

  /**Calculates the attack damage damagePreCalculations would deal, just taking into
   * account the statuses this entity has. Useful for getting #s that show up on cards
   */
  public int calcAtkDmgFromThisStats(int damagePreCalculations){
    double dmg = damagePreCalculations + this.getStatusStrength("Strength");
    if(this.getStatusStrength("Weak") > 0){
      dmg *= 0.75;
    }
    return (int)(dmg + 0.00000001); //In case of floating point errors
  }

  public void loseVulnerable(int amount){
    subtractStatusStrength("Vulnerable", amount);
    if(getStatusStrength("Vulnerable") < 0){
      setStatusStrength("Vulnerable", 0);
    }
  }
  public void loseWeak(int amount){
    subtractStatusStrength("Weak", amount);
    if(getStatusStrength("Weak") < 0){
      setStatusStrength("Weak", 0);
    }
  }
  public void loseFrail(int amount){
    subtractStatusStrength("Frail", amount);
    if(getStatusStrength("Frail") < 0){
      setStatusStrength("Frail", 0);
    }
  }

  /**Ends the turn of the entity. Will generally only be called on the player
  */
  public void endTurn(){
    this.block = 0;
    for(int i=0; i<statuses.size(); i++){
      Status s = statuses.get(i);
      if(s.isDecreasing() && s.getStrength() > 0){
        s.subtractStrength(1);
      }
    }
    this.subtractStatusStrength("Strength", getStatusStrength("Strength Down"));
    this.setStatusStrength("Strength Down", 0);
    this.subtractStatusStrength("Dexterity", getStatusStrength("Dexterity Down"));
    this.setStatusStrength("Dexterity Down", 0);
  }



  /**Constructs and returns the HPBar String of this entity
  */
  public String getHPBar(){
    double p = ((double)hp / maxHP); //proportion of hp remaining
    int numRedBars = (int)(hpBarLength*p);
    int middleIndex = hpBarLength/2;
    boolean blueHPBar = block > 0;
    
    //Construct hpBar String
    String hpBar = "";
    boolean pastRedSection = false;
    for(int i=0; i<hpBarLength; i++){     //Traverse bar
      String thisColor;
      String thisChar;
      if(i == numRedBars){    //Start of white ones
        pastRedSection = true;
      }
      if(i >= middleIndex-1 && i <= middleIndex+1){ //Middle three digits
        thisColor = getHPTextColor(pastRedSection, blueHPBar);
        thisChar = getHPTextChar(i-middleIndex);
      }else{
        thisColor = getHPBarColor(pastRedSection, blueHPBar);
        thisChar = "█";
      }
      hpBar += thisColor + thisChar;
    }
    return hpBar;
  }

  /**Adds strength to this entity's with the specified name.
  */
  private String getHPTextColor(boolean pastRedSection, boolean blueHPBar){
    String textColor = "";
    if(blueHPBar){
      textColor += Colors.hpRed;
      if(pastRedSection){
        textColor += Colors.backgroundWhite;
      }else{
        textColor += Colors.backgroundBlockBlue;
      }
    }else{    //Red hp bar
      if(pastRedSection){
        textColor += Colors.hpRed + Colors.backgroundWhite;
      }else{
        textColor += Colors.whiteBold + Colors.backgroundHPBarRed;
      }
    }


    return textColor;
  }

  private String getHPBarColor(boolean pastRedSection, boolean blueHPBar){
    if(pastRedSection){
      return Colors.whiteBold; //Was plain white before; the left part of the bar was slightly darker
    }
    if(blueHPBar){
      return Colors.blockBlue;
    }
    return Colors.hpBarRed;
  }

  private String getHPTextChar(int position){
    //position should be between -1 and 1 == (i-middleIndex)
    int ch = -1;
    if(hp < 10){
      switch(position){
        case -1:
          return " ";
        case 0:
          ch = hp;
          break;
        case 1:
          return " ";
      }
    }else if(hp < 100){
      switch(position){
        case -1:
          ch = hp/10;
          break;
        case 0:
          return " ";
        case 1:
          ch = hp%10;
          break;
      }
    }else if(hp > 99){
      switch(position){
        case -1:
          ch = hp/100;
          break;
        case 0:
          ch = (hp/10)%10;
          break;
        case 1:
          ch = hp%10;
          break;
      }
    }

    String str = "" + ch;
    return str;
  }

  

  public void die(){
    //For the player only (Entity is overridden):
    combat.endCombat();
  }
  

  //For polymorphism
  public void setSplitIntent(){ }


  public static ArrayList<Status> copyStatusList(ArrayList<Status> ogList){
    ArrayList<Status> newList = new ArrayList<Status>();
    for(Status s : ogList){
      Status newStatus = new Status(s);
      newList.add(newStatus);
    }
    return newList;
  }
}