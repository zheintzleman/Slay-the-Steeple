package app;
import java.util.*;

import enemyfiles.Enemy;
import util.Colors;

/** Used to store an entity's hp and maxHP. Using an object for this to sync
 * the Player's HP with the Run's.
 */
class EntityHealth {
  int hp;
  int maxHP;

  public EntityHealth(int hp, int maxHP){
    this.hp = hp;
    this.maxHP = maxHP;
  }

  /** Increases hp the specified amount, up to its max health.
  */
  public void heal(int amt){
    hp += amt;
    if(hp > maxHP){
      hp = maxHP;
    }
  }
  /** Increases maxHP and hp by the specified amount.
  */
  public void raiseMaxHP(int amt){
    maxHP += amt;
    heal(amt);
  }
}

/** Represents any entity in a combat -- either the player or an enemy. Contains methods for most
 * basic actions an entity can perform, such as attacking, dying, changing statuses, etc.
 * 
 * @see Enemy
 * @see Player
 * @see AbstractEntity
 * @see Status
 */
public abstract class Entity {
  /** Default Entity Image */
  public static final String[] RECTANGLE = new String[] {"███████", "███████", "███████", "███████", "███████", "███████"};
  private String name;
  private EntityHealth health;
  private int hpBarLength, block;
  private boolean isDead = false;
  private String[] art;
  // Could instead use a LinkedHashMap, especially given most of the use cases are with searching
  // for a specific element. The lists are relatively short, though (and this project is not
  // especially computationally demanding), so asymptotics aren't as important as clarity.
  private ArrayList<Status> statuses;
  /// While non-null (iff statusHolds > 0), new statuses are applied
  /// to a copy of the entity instead of to the entity itself. They then sync at the start of the
  /// next turn. Originally just for during "end-of-turn" (i.e. while enemies are doing their
  /// intents), but have since generalized the functionality.
  private Entity copy = null;
  /// While blockHolds > 0, new block is instead added into this ArrayList. When blockHolds goes
  /// back to 0, they are then all applied one after the other.
  /// Not just using an int, since cards like Juggernaut need to count the individual block gains.
  private ArrayList<Integer> heldBlock;
  // Similar to above, but for block. While >0, new block is stored in heldBlock until resumed.
  // Each call to holdThisBlock() adds one, and each call to resumeThisBlock removes one.
  private int blockHolds = 0;
  private int statusHolds = 0;

  public Entity(){
    name = "<Entity>";
    final int hp = (int)(Math.random()*10) + 45;
    health = new EntityHealth(hp, hp);
    hpBarLength = 21;
    block = 0;
    art = RECTANGLE;
    heldBlock = new ArrayList<Integer>();
    statuses = new ArrayList<Status>();
  }
  public Entity(String name){
    this.name = name;
    final int hp = (int)(Math.random()*10) + 45;
    health = new EntityHealth(hp, hp);
    hpBarLength = 21;
    block = 0;
    art = RECTANGLE;
    heldBlock = new ArrayList<Integer>();
    statuses = new ArrayList<Status>();
  }
  public Entity(String name, int hp){
    this.name = name;
    health = new EntityHealth(hp, hp);
    hpBarLength = 21;
    block = 0;
    art = RECTANGLE;
    heldBlock = new ArrayList<Integer>();
    statuses = new ArrayList<Status>();
  }
  public Entity(String name, int hp, int hpBarLength){
    this.name = name;
    health = new EntityHealth(hp, hp);
    this.hpBarLength = hpBarLength;
    block = 0;
    art = RECTANGLE;
    heldBlock = new ArrayList<Integer>();
    statuses = new ArrayList<Status>();
  }
  public Entity(String name, int hp, String[] art){
    this.name = name;
    health = new EntityHealth(hp, hp);
    hpBarLength = 21;
    block = 0;
    this.art = art;
    heldBlock = new ArrayList<Integer>();
    statuses = new ArrayList<Status>();
  }
  public Entity(String name, String[] art, EntityHealth healthObj){
    this.name = name;
    health = healthObj;
    hpBarLength = 21;
    block = 0;
    this.art = art;
    heldBlock = new ArrayList<Integer>();
    statuses = new ArrayList<Status>();
  }
  public Entity(String name, int hp, int hpBarLength, String[] art){
    this.name = name;
    health = new EntityHealth(hp, hp);
    this.hpBarLength = hpBarLength;
    block = 0;
    this.art = art;
    heldBlock = new ArrayList<Integer>();
    statuses = new ArrayList<Status>();
  }
  public Entity(Entity e){
    this.name = e.name;
    health = new EntityHealth(e.health.hp, e.health.maxHP);
    this.hpBarLength = e.hpBarLength;
    this.block = e.block;
    this.heldBlock = new ArrayList<Integer>();
    this.art = e.art.clone();
    this.statuses = copyStatusList(e.statuses);
  }
  
  //Getters and Setters
  public String getName(){ return name; }
  public void setName(String newName){ name = newName; }
  public int getHP(){ return health.hp; }
  public void setHP(int newHP){ health.hp = newHP; }
  public int getMaxHP(){ return health.maxHP; }
  public void setMaxHP(int newMaxHP){ health.maxHP = newMaxHP; }
  public int getBlock(){ return block; }
  public void setBlock(int newBlock){ block = newBlock; }
  public boolean isDead(){ return isDead; }
  public String[] getArt(){ return art; }
  public void setArt(String[] newArt){ art = newArt; }
  public int getHPBarLength(){ return hpBarLength; }
  public void setHPBarLength(int newHPBarLength){ hpBarLength = newHPBarLength; }
  public ArrayList<Status> getStatuses(){ return statuses; } 
  public void setStatuses(ArrayList<Status> newStatus){ statuses = newStatus; }
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
  

  /** For each entity currently in play:
   * Increases the # of status & block holds by 1.
   * All new status effects and block will go to the clone until resumed once
   * for each hold.
   * @Note Only affects calls to the block function, not other changes to block
   * amount (such as being attacked and losing block.)
   * @Note Using the numHolds system so that things can be freely held then
   * later resumed (e.g. at the beginning/end of a function) without worrying
   * about whether there could already be a hold. E.g. killing fungi beasts
   * using Combust would otherwise throw exceptions whether or not the
   * EventManager.playStatusEffects method holds.
   */
  public static void hold(){
    // Calls holdThis functions on the enemies and player.
    holdBlock();
    holdStatuses();
  }
  /** For each entity currently in play:
   * Decreases the # of status & block holds by 1. If now 0:
   * Merges the statuses and/or block that have been applied since, and sets
   * `copy` back to null. (i.e. applies all attempted status changes and block
   * since calling hold.)
   * (If only one of statuses/block holds reaches 0, only resumes that; i.e.
   * if the status holds reach 0 but block reaches 1, only the statuses
   * resumed) (obviously)
   */
  public static void resume(){
    // Calls resumeThis functions on the enemies and player.
    resumeBlock();
    resumeStatuses();
  }
  /** For each entity currently in play:
   * Increases the # of status holds by 1.
   * All new block will not be applied until resumeThisBlock is called once for
   * each hold.
   * @Note Only affects calls to the block function, not other changes to block
   * amount (such as being attacked.)
   * @Postcondition All entities' block held.
   */
  public static void holdBlock(){
    Combat.c.getEntities().stream()
      .forEach(Entity::holdThisBlock);
  }
  /** For each entity currently in play:
   * Decreases the # of block holds by 1. If now 0:
   * Applies the block that has been held.
   */
  public static void resumeBlock(){
    // See resumeStatuses() for why we don't assert blockHeld() here.
    Combat.c.getEntities().stream()
      .forEach(Entity::resumeThisBlock);
  }
  /** For each entity currently in play:
   * Increases the # of status holds by 1. If was just 0:
   * Initializes `copy` to a clone of `this`. All status changes will go to
   * the clone until resumeThisStatuses() is called once for each hold.
   * @Postcondition All entity's statuses held.
   */
  public static void holdStatuses(){
    Combat.c.getEntities().stream()
      .forEach(Entity::holdThisStatuses);
  }
  /** For each entity currently in play:
   * Decreases the # of status holds by 1. If now 0:
   * Merges the status changes that have been applied since holding them.
   * Does nothing if this entity not currently held
   */
  public static void resumeStatuses(){
    // Not asserting statusesHeld(), since a new enemy could have been created
    // since holdStatuses(), and they are created w/ statusesHeld() false
    // (i.e. copy == null.)
    Combat.c.getEntities().stream()
      .forEach(Entity::resumeThisStatuses);
  }
  private void holdThisStatuses(){
    if(statusHolds == 0){
      copy = new AbstractEntity(this);
    }
    statusHolds++;
    
    App.ASSERT(statusesHeld());
  }
  private void resumeThisStatuses(){
    if(statusesHeld()){
      // Decrease the # of holds by 1. If now 0, actually resume it.
      statusHolds--;
      if(statusHolds == 0){
        setStatuses(copy.getStatuses());
        copy = null;
      }
    }
  }
  private void holdThisBlock(){
    blockHolds++;
    App.ASSERT(blockHeld());
  }
  private void resumeThisBlock(){
    if(blockHeld()){
      // Decrease the # of holds by 1. If now 0, actually resume it.
      blockHolds--;
      if(blockHolds == 0){
        for(int block : heldBlock){
          addBlock(block);
        }
        heldBlock.clear();
      }
    }
  }
  /** Returns true iff this entity is currently in a status && block hold.
   * (e.g.)
   * Returns false if called on the copy. See `copy` description for more
   * details.
   */
  public boolean fullyHeld(){
    return statusesHeld() && blockHeld();
  }
  /** Returns whether or not the statuses are currently being applied to the
   * copy (i.e. being held.)
   */
  public boolean statusesHeld(){
    return statusHolds > 0;
  }
  /** Returns whether new block is added to the heldBlock variable (i.e. block
   * is being held.)
   * If false, block is added straight to the block variable.
   */
  public boolean blockHeld(){
    return blockHolds > 0;
  }
  
  
  /** Returns this entity's status with the specified name, or null if none is present.
  */
  public Status getStatus(String name){
    for(Status s : statuses){
      if(s.getName().equals(name)){
        return s;
      }
    }
    return null;
  }
  /** Returns this entity's strength of the status with the specified name.
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
  /** Sets this entity's status (with the specified name) to have the given strength.
   * If status not present, creates one.
   * If `strength` == 0, removes status from the status list.
   * If during turn-end, sets the status in the entity copy instead.
   * @return The status being set (even if removed from list for being set to 0)
   * @return null, if setting a status already at 0 to 0.
   */
  public Status setStatusStrength(String name, int strength){
    if(statusesHeld()){
      // While enemies doing intents, want to edit their statuses instead.
      return copy.setStatusStrength(name, strength);
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
  /** Adds strength to this entity's status (with the specified name.)
   * If status not present, creates one.
   * If new strength is 0, removes status from the status list.
   * If during turn-end, add status to the entity copy instead.
   * @return The status added to (even if removed from list for being set to 0)
   * @return null, if strength is 0.
  */
  public Status addStatusStrength(String name, int strength){
    if(strength == 0){ return null; }
    if(statusesHeld()){
      return copy.addStatusStrength(name, strength);
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
  /** Subtracts strength from this entity's status (with the specified name.)
   * If status not present, creates one.
   * If new strength is 0, removes status from the status list.
   * If during turn-end, subtracts status from the entity copy instead.
   * @NOTE Only deletes on == 0. If subtracting >1 at a time, make sure it can go
   * negative (or else that you deal with the negative case, or rewrite this function.)
   * @return The status subtracted from (even if removed from list for being set to 0)
   * @Precondition Not during turn-end, or being called by `copy` (i.e. copy == null)
   */
  public Status subtractStatusStrength(String name, int strength){
    return addStatusStrength(name, -strength);
  }
  /** Returns whether or not this enemy has the status in any strength, besides 0.
   * @NOTE Returns true for negative status strength.
   */
  public boolean hasStatus(String name){
    return getStatus(name) != null;
  }

  
  /** Damages the entity the specified amount; taking from its block then its hp. If hp is 0 or less, it dies.
   * @return int - the amount of damage delt
   */
  public int damage(int dmg, boolean fromCard){
    if(block >= dmg){
      block -= dmg;
      return 0;
    }else{
      dmg -= block;
      block = 0;
      return subtractHP(dmg, fromCard);
    }
  }
  /** Subtracts the entity's hp the specified amount. If hp is 0 or less, it dies.
   * @return int - the amount of hp actually lost (e.g. if it has 1HP left.)
   */
  public int subtractHP(int dmg, boolean fromCard){
    EventManager.em.OnLoseHP(this, dmg, fromCard);
    health.hp -= dmg;
    if(health.hp <= 0){
      // Subtract off the overkill:
      dmg += health.hp;
      health.hp = 0;
      this.die();
    }
    if(this.hasStatus("Split") && health.hp <= (health.maxHP/2)){
      this.setSplitIntent();
    }
    return dmg;
  }
  /** Increases the entity's health the specified amount, up to its max health.
  */
  public void heal(int amt){
    health.heal(amt);
  }
  /** Increases the entity's max HP and health the specified amount.
  */
  public void raiseMaxHP(int amt){
    health.raiseMaxHP(amt);
  }
  /** Increases the entity's block the specified amount, up to 999.
   * Does not account for frail/dexterity/etc., or for block being held.
   */
  private void addBlock(int blk){
    block += blk;
    block = Integer.min(block, 999);
    EventManager.em.OnGainBlock(this, blk);
  }
  /** Gives the entity the appropriate amount of block, taking into account relevent status effects.
   * @param blockPreCalculations - The amount of defence the card/intent does (ie. 5 for an unupgraded Defend)
   * @param fromPlayingACard - Whether or not a card is causing the block (from directly playing it). Used to
   * determine, e.g., whether to account for frail.
   */
  public void block(int blockPreCalculations, boolean fromPlayingACard){
    int blk = calcBlockAmount(blockPreCalculations, fromPlayingACard);
    if(blockHeld()){
      heldBlock.add(blk);
    } else {
      addBlock(blk);
    }
  }
  /** Gives the receiving entity the appropriate amount of block, taking into
   * account relevent status effects (on this entity).
   * @param receiver - The entity to receive the block
   * @param blockPreCalculations - The amount of defence the card/intent does
   * (ie. 5 for an unupgraded Defend)
   *
   * @Precondition Not being called from or to the player.
   */
  public void giveBlock(Entity receiver, int blockPreCalculations){
    App.ASSERT(receiver instanceof Enemy);

    int blk = calcBlockAmount(blockPreCalculations, false);
    if(receiver.blockHeld()){
      receiver.heldBlock.add(blk);
    } else {
      receiver.addBlock(blk);
    }
  }
  /** Calculates the amount a block card with the entered amount would block for, taking into account relevent status effects.
   * @param blockPreCalculations - The amount the base card would say (ie. 5 for an unupgraded Defend)
   * @param fromCard - Whether or not a card is causing the block. Used to determine, e.g., whether to account for frail.
   * @return int - The amount of block that would be gained by playing such a card, taking into account dexterity and frail.
   */
  public int calcBlockAmount(int blockPreCalculations, boolean fromCard){
    double blk = blockPreCalculations;
    if(fromCard){
      blk += this.getStatusStrength("Dexterity");
      if(this.getStatusStrength("Frail") > 0){
        blk *= 0.75;
      }
    }
    return (int)(blk + 0.00000001); //In case of floating point errors
  }
  /** Attacks the victim entity for the specified amount. Takes into account relevent status effects.
   * Takes from the victim's block, then its hp.
   * @param victim - The entity being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @return int - The amount of attack damage delt
   */
  public int attack(Entity victim, int damagePreCalculations){
    return attack(Collections.singletonList(victim), damagePreCalculations, 1);
  }
  /** Attacks the victims for the specified amount each. Takes into account relevent status effects.
   * Takes from the victims' block, then their hp. Doesn't update statuses until all attacks completed.
   * @param victims - The entities being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @return int - The total amount of attack damage delt
   */
  public int attack(List<? extends Entity> victims, int damagePreCalculations){
    return attack(victims, damagePreCalculations, 1);
  }
  /** Attacks the victim entity for the specified amount. Takes into account relevent status effects.
   * Takes from the victim's block, then its hp.
   * @param victim - The entity being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @param strMultiplier - Multiplies the effect of strength by this; default 1. Used for Heavy Blade, etc.
   * @return int - The amount of attack damage delt
   */
  public int attack(Entity victim, int damagePreCalculations, int strMultiplier){
    return attack(Collections.singletonList(victim), damagePreCalculations, strMultiplier);
  }
  /** Attacks the victims for the specified amount each. Takes into account relevent status effects.
   * Takes from the victims' block, then their hp.
   * @param victims - The entities being attacked by the entity calling this method
   * @param damagePreCalculations - The amount of damage the base card does (ie. 6 for an unupgraded Strike)
   * @param strMultiplier - Multiplies the effect of strength by this; default 1. Used for Heavy Blade, etc.
   * @return int - The total amount of attack damage delt
   */
  public int attack(List<? extends Entity> victims, int damagePreCalculations, int strMultiplier){
    int totalDmgDealt = 0;
    // To prevent ConcurrentModificationException's:
    List<? extends Entity> victimsCopy = List.copyOf(victims);
    for(Entity victim : victimsCopy){
      int dmg = calcAttackDamage(victim, damagePreCalculations, strMultiplier);
      int dmgDealt = victim.damage(dmg, this instanceof Player);
      totalDmgDealt += dmgDealt;
      EventManager.em.OnAttack(this, victim, dmgDealt);
    }
    return totalDmgDealt;
  }
  /** Performs an attack that hits `victim` `times` times. See attack(List, int, int) for more details.
   */
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

  /** Calculates the attack damage damagePreCalculations would deal, just taking into
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

  /** Meant to be overridden w/ a super() call. */
  public void die(){
    isDead = true;
  }

  //For polymorphism
  public abstract void setSplitIntent();
  /** Ends the turn of the entity.
  */
  public abstract void endTurn(Player player);
  /** Loops over the copy's statuses (i.e. the entity's statuses after end-of-turn status changes
   * such as entity intents), decreasing the strength of statuses where:
   * The status is decreasing, and it is present both in the copy and in the original.
   */
  public void updateDecreasingStatuses(){
    for(int i=0; i<copy.statuses.size(); i++){
      Status s = copy.statuses.get(i);
      if(s.isDecreasing() && this.hasStatus(s.getName())){
        App.ASSERT(s.getStrength() != 0); //Used to be a condition above. Implied by existence in the statuses list.
        s.subtractStrength(1);
        if(s.getStrength() == 0){
          copy.statuses.remove(i);
          i--;
        }
      }
    }
  }



  /** Constructs and returns the HPBar String of this entity
  */
  public String getHPBar(){
    double p = ((double)health.hp / health.maxHP); //proportion of hp remaining
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

  /** Adds strength to this entity's with the specified name.
  */
  private String getHPTextColor(boolean pastRedSection, boolean blueHPBar){
    String textColor = "";
    if(blueHPBar){
      if(pastRedSection){
        textColor += Colors.hpTextRedOnWhite;
      }else{
        textColor += Colors.energyCounterRedBold + Colors.backgroundBlockBlue;
      }
    }else{    //Red hp bar
      if(pastRedSection){
        textColor += Colors.hpTextRedOnWhite;
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
    final int hp = health.hp;
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

  /** Performs a deep copy of the status list. */
  public static ArrayList<Status> copyStatusList(Collection<Status> ogList){
    App.ASSERT(ogList != null);
    ArrayList<Status> newList = new ArrayList<Status>(ogList.size());

    for(Status s : ogList){
      newList.add(new Status(s));
    }
    return newList;
  }
}