package enemyfiles;
import java.util.*;

import app.Combat;
import app.Entity;
import app.EventManager;
import app.Player;

public abstract class Enemy extends Entity{
  boolean isElite;
  int middleX;

  public Enemy(){
    super();
  }
  public Enemy(String name){
    super(name);
  }
  public Enemy(String name, int hp){
    super(name, hp);
  }
  public Enemy(String name, int hp, boolean isElite){
    super(name, hp);
    this.isElite = isElite;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX){
    super(name, hp);
    this.isElite = isElite;
    this.middleX = middleX;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX, int hpBarLength){
    super(name, hp, hpBarLength);
    this.isElite = isElite;
    this.middleX = middleX;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX, int hpBarLength, String[] art){
    super(name, hp, hpBarLength, art);
    this.isElite = isElite;
    this.middleX = middleX;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX, String[] art){
    super(name, hp, art);
    this.isElite = isElite;
    this.middleX = middleX;
  }
  public Enemy(Enemy e){
    super(e);
    this.isElite = e.isElite;
    this.middleX = e.middleX;
  }

  //Getters and Setters
  public abstract Intent getIntent();
  public int getMiddleX(){ return middleX; }
  public void setMiddleX(int newX){ middleX = newX; }



  /** Performs the enemy's intent. */
  public abstract void doIntent(Entity p);
  /** Sets the enemy's next intent. (Should always be overridden) */
  public abstract void setNextIntent();
  
  @Override
  public void setSplitIntent() {
    // Not an abstract method so that non-slime enemy classes can all inherit
    // this same method.
    throw new UnsupportedOperationException("Calling setSplitIntent function on non-large-slime Enemy.");
  }

  /**Does necessary actions for the end of the enemy's turn, including their intents.
  */
  @Override
  public void endTurn(Player player){
    //Creating a copy enemy and changing its statuses instead of the real entity's. (Still attacking with the real entity.) Then, syncs the copy's statuses to the real entity at the end of this method.
    System.out.println("About to do intent");
    this.doIntent(player);
    updateDecreasingStatuses(); //Updates said statuses in copy.
    addStatusStrength("Strength", this.getStatusStrength("Ritual")); //Only counts rituals that were there already.
    this.setBlock(0); //Could add the whole "start of turn block" system to be dealt with with the enemy copy system instead.
    this.setNextIntent();
  }

  
  @Override
  public void die(){
    super.die();
    assert isDead();
    
    Combat combat = Combat.c;
    Entity player = combat.getPlayer();

    if(this.hasStatus("Spore Cloud")){
      int scAmount = this.getStatusStrength("Spore Cloud");
      player.addStatusStrength("Vulnerable", scAmount);
    }
    
    ArrayList<Enemy> enemies = combat.getEnemies();
    int thisIndex = -1;
    for(int i=0; i<enemies.size(); i++){
      Enemy e = enemies.get(i);
      if(e == this){
        thisIndex = i;
      }
    }
    enemies.remove(thisIndex);

    if(enemies.size() == 0){
      combat.endCombat();
    }
  }

  @Override
  public int attack(List<? extends Entity> victims, int damagePreCalculations, int strMultiplier) {
    int retVal = super.attack(victims, damagePreCalculations, strMultiplier);
    EventManager.em.OnAttackFinished(this);
    return retVal;
  }
  
  // @Override
  // /**Subtracts the entity's hp the specified amount. If hp is 0 or less, it dies.
  //  * Calls OnPlayerHurt Event.
  // */
  // public void subtractHP(int dmg){
  //   super.subtractHP(dmg);
  //   int hp = super.getHP();
  //   int maxHP = super.getMaxHP();
  //   if(this.hasStatus("Split") && hp <= (maxHP/2)){
  //     this.setSplitIntent();
  //   }
  // }
  
}