package enemyfiles;
import java.util.*;

import app.Combat;
import app.Entity;
import app.EventManager;
import app.Intent;
import app.Player;

public class Enemy extends Entity{
  // Would be an abstract class, but we make copies of the enemies for end-turn status purposes,
  // and the copies are of type Enemy.
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
  public Intent getIntent(){ return null; }
  public int getMiddleX(){ return middleX; }
  public void setMiddleX(int newX){ middleX = newX; }



  /**Performs the enemy's intent. (Should always be overridden)
  */
  public void doIntent(Entity p){
    System.out.println("Basic Enemy Intent");
  }
  /**Sets the enemy's next intent. (Should always be overridden)
  */
  public void setNextIntent(){}
  
  @Override
  public void setSplitIntent() {
    throw new RuntimeException("Calling setSplitIntent function on non-large-slime Enemy.");
  }

  /**Does necessary actions for the end of the enemy's turn
  */
  @Override
  public void endTurn(Player player){
    //Creating a copy enemy and changing its statuses instead of the real entity's. (Still attacking with the real entity.) Then, syncs the copy's statuses to the real entity at the end of this method.
    System.out.println("About to do intent");
    this.doIntent(player);
    updateCopysDecreasingStatuses(); //Updates said statuses in endTurnCopy.
    endTurnCopy.addStatusStrength("Strength", this.getStatusStrength("Ritual")); //Only counts rituals that were there already.
    this.setBlock(0); //Could add the whole "start of turn block" system to be dealt with with the enemy copy system instead.
    this.setStatuses(endTurnCopy.getStatuses());
    this.setNextIntent();
  }

  
  @Override
  public void die(){
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
    EventManager.er.OnAttackFinished(this);
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