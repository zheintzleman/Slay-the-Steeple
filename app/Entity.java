package app;
import java.util.*;

public abstract class Entity{
  public static final String[] RECTANGLE = new String[] {"███████", "███████", "███████", "███████", "███████", "███████"};
  private String name;
  private int hp, maxHP, hpBarLength, block, startOfTurnBlock;
  private String[] art;
  private ArrayList<Status> statuses;
  private Combat combat;
  // While enemies are doing their intents, new statuses are applied to a copy of the entity
  // instead of to the entity itself. They then sync at the start of the next turn.
  public Entity endTurnCopy = null;

  public Entity(){
    name = "<Entity>";
    maxHP = (int)(Math.random()*10) + 45;
    hp = maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = new ArrayList<Status>();
  }
  public Entity(String name, Combat c){
    this.name = name;
    this.maxHP = (int)(Math.random()*10) + 45;
    this.hp = this.maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = new ArrayList<Status>();
    this.combat = c;
  }
  public Entity(String name, int hp, Combat c){
    this.name = name;
    this.maxHP = hp;
    this.hp = this.maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = new ArrayList<Status>();
    this.combat = c;
  }
  public Entity(String name, int hp, int hpBarLength, Combat c){
    this.name = name;
    this.maxHP = hp;
    this.hp = this.maxHP;
    this.hpBarLength = hpBarLength;
    startOfTurnBlock = block = 0;
    art = RECTANGLE;
    statuses = new ArrayList<Status>();
    this.combat = c;
  }
  public Entity(String name, int hp, String[] art, Combat c){
    this.name = name;
    this.maxHP = hp;
    this.hp = this.maxHP;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    this.art = art;
    statuses = new ArrayList<Status>();
    this.combat = c;
  }
  public Entity(String name, int hp, int maxHP, String[] art, Combat c){
    this.name = name;
    this.maxHP = maxHP;
    this.hp = hp;
    hpBarLength = 21;
    startOfTurnBlock = block = 0;
    this.art = art;
    statuses = new ArrayList<Status>();
    this.combat = c;
  }
  public Entity(String name, int hp, int hpBarLength, Combat c, String[] art){
    this.name = name;
    this.hp = this.maxHP = hp;
    this.hpBarLength = hpBarLength;
    startOfTurnBlock = block = 0;
    this.art = art;
    statuses = new ArrayList<Status>();
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

  
  /**Returns this entity's status with the specified name, or null if none is present.
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
    Status s = getStatus(name);
    if(s != null){
      // Status strength not 0:
      return s.getStrength();
    } else {
      return 0;
    }
  }
  /**Sets this entity's status (with the specified name) to have the given strength.
   * If status not present, creates one.
   * If `strength` == 0, removes status from the status list.
   * If during turn-end, sets the status in the entity copy instead.
   * @return The status being set (even if removed from list for being set to 0)
   * @return null, if setting a status already at 0 to 0.
  */
  public Status setStatusStrength(String name, int strength){
    if(isEndOfTurn()){
      // While enemies doing intents, want to edit their statuses instead.
      return endTurnCopy.setStatusStrength(name, strength); //TODO: Did this break things?
    }

    Status s = getStatus(name);
    // If status strength == 0; i.e. not currently present in the statuses list
    // (and not setting it to 0)
    if(strength != 0 && s == null){
      s = new Status(name, strength);
      statuses.add(s);
      return s;
    } else if(s == null){
      // Setting a non-present status to 0 -- doing nothing.
      return null;
    }
    if(strength == 0){
      statuses.remove(s);
    } else {
      s.setStrength(strength);
    }
    return s;
  }
  /**Adds strength to this entity's status (with the specified name.)
   * If status not present, creates one.
   * If new strength is 0, removes status from the status list.
   * If during turn-end, add status to the entity copy instead.
   * @return The status added to (even if removed from list for being set to 0)
   * @return null, if strength is 0.
  */
  public Status addStatusStrength(String name, int strength){
    if(strength == 0){ return null; }
    if(isEndOfTurn()){
      return endTurnCopy.addStatusStrength(name, strength);
    }

    Status s = getStatus(name);

    // If status strength == 0; i.e. not currently present in the statuses list
    if(s == null){
      s = new Status(name, strength);
      statuses.add(s);
      return s;
    }
    s.addStrength(strength);
    if(s.getStrength() == 0){
      statuses.remove(s);
    }
    return s;
  }
  /**Subtracts strength from this entity's status (with the specified name.)
   * If status not present, creates one.
   * If new strength is 0, removes status from the status list.
   * If during turn-end, subtracts status from the entity copy instead.
   * @NOTE Only deletes on == 0. If subtracting >1 at a time, make sure it can go
   * negative (or else that you deal with the negative case, or rewrite this function.)
   * @return The status subtracted from (even if removed from list for being set to 0)
   * @Precondition Not during turn-end, or being called by endTurnCopy (i.e. endTurnCopy == null)
  */
  public Status subtractStatusStrength(String name, int strength){
    return addStatusStrength(name, -strength);
  }
  /**Returns whether or not this enemy has the status in any strength, besides 0.
   * @NOTE Returns true for negative status strength.
  */
  public boolean hasStatus(String name){
    return getStatus(name) != null;
  }
  public boolean isEndOfTurn(){
    return endTurnCopy != null;
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
  /**Attacks the victim entity for the specified amount. Takes into account relevent status effects.
   * Takes from the victim's block, then its hp.
   * @param victim - The entity being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @return int - The amount of attack damage delt
  */
  public int attack(Entity victim, int damagePreCalculations){
    return attack(Collections.singletonList(victim), damagePreCalculations, 1);
  }
  /**Attacks the victims for the specified amount each. Takes into account relevent status effects.
   * Takes from the victims' block, then their hp. Doesn't update statuses until all attacks completed.
   * @param victims - The entities being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @return int - The total amount of attack damage delt
  */
  public int attack(List<? extends Entity> victims, int damagePreCalculations){
    return attack(victims, damagePreCalculations, 1);
  }
  /**Attacks the victim entity for the specified amount. Takes into account relevent status effects.
   * Takes from the victim's block, then its hp.
   * @param victim - The entity being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @param strMultiplier - Multiplies the effect of strength by this; default 1. Used for Heavy Blade, etc.
   * @return int - The amount of attack damage delt
  */
  public int attack(Entity victim, int damagePreCalculations, int strMultiplier){
    return attack(Collections.singletonList(victim), damagePreCalculations, strMultiplier);
  }
  /**Attacks the victims for the specified amount each. Takes into account relevent status effects.
   * Takes from the victims' block, then their hp. Doesn't update statuses until all attacks completed.
   * @param victims - The entities being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @param strMultiplier - Multiplies the effect of strength by this; default 1. Used for Heavy Blade, etc.
   * @return int - The total amount of attack damage delt
  */
  public int attack(List<? extends Entity> victims, int damagePreCalculations, int strMultiplier){ //TODO: Make into an event?
    int totalDmgDealt = 0;
    // To prevent ConcurrentModificationException's:
    List<? extends Entity> victimsCopy = List.copyOf(victims);
    for(Entity victim : victimsCopy){
      int dmg = calcAttackDamage(victim, damagePreCalculations, strMultiplier);
      int dmgDealt = victim.damage(dmg);
      totalDmgDealt += dmgDealt;
      if(dmgDealt > 0){
        if(victim.hasStatus("Curl Up")){ //Curl Up
          victim.addBlock(victim.getStatusStrength("Curl Up"));
          victim.setStatusStrength("Curl Up", 0);
        }
        if(victim.hasStatus("Angry")){
          victim.addStatusStrength("Strength", victim.getStatusStrength("Angry"));
          System.out.println("Victim Anger: " + victim.getStatusStrength("Angry"));
        }
      }
    }
    setStatusStrength("Vigor", 0);
    return totalDmgDealt;
  }
  /**Performs an attack that hits `victim` `times` times. See attack(List, int, int) for more details. */
  public int multiattack(int times, Entity victim, int damagePreCalculations){
    return attack(Collections.nCopies(times, victim), damagePreCalculations, 1);
  }

  
  public int calcAttackDamage(Entity victim, int damagePreCalculations, int strMultiplier){
    double dmg = calcAtkDmgFromThisStats(damagePreCalculations, strMultiplier);
    if(victim.getStatusStrength("Vulnerable") > 0){
      dmg *= 1.5;
    }
    return (int)(dmg + 0.00000001); //In case of floating point errors
  }

  /**Calculates the attack damage damagePreCalculations would deal, just taking into
   * account the statuses this entity has. Useful for getting #s that show up on cards
   */
  public int calcAtkDmgFromThisStats(int damagePreCalculations, int strMultiplier){
    double dmg = damagePreCalculations
                + this.getStatusStrength("Strength") * strMultiplier
                + this.getStatusStrength("Vigor");

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

  

  //For polymorphism
  public abstract void setSplitIntent();
  public abstract void die();
  /**Ends the turn of the entity. Will generally only be called on the player
  */
  public abstract void endTurn(Player player);
  /**Loops over the copy's statuses (i.e. the entity's statuses after end-of-turn status changes
   * such as entity intents), decreasing the strength of statuses that where:
   * The status is decreasing, and it is present both in the copy and in the original.
   * */
  public void updateCopysDecreasingStatuses(){
    for(int i=0; i<endTurnCopy.statuses.size(); i++){
      Status s = endTurnCopy.statuses.get(i);
      if(s.isDecreasing() && this.hasStatus(s.getName())){
        App.ASSERT(s.getStrength() != 0); //Used to be a condition above. Implied by existence in the statuses list.
        s.subtractStrength(1);
        if(s.getStrength() == 0){
          endTurnCopy.statuses.remove(i);
          i--;
        }
      }
    }
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

  /**Performs a deep copy of the status list. */
  public static ArrayList<Status> copyStatusList(Collection<Status> ogList){
    App.ASSERT(ogList != null);
    ArrayList<Status> newList = new ArrayList<Status>(ogList.size());

    for(Status s : ogList){
      newList.add(new Status(s));
    }
    return newList;
  }
}