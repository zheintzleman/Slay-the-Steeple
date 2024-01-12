package enemyfiles;
import java.util.*;

import app.Combat;
import app.Entity;
import app.Intent;
import app.Status;

public class Enemy extends Entity{
  boolean isElite;
  int middleX;

  public Enemy(){
    super();
  }
  public Enemy(String name, Combat c){
    super(name, c);
  }
  public Enemy(String name, int hp, Combat c){
    super(name, hp, c);
  }
  public Enemy(String name, int hp, boolean isElite, Combat c){
    super(name, hp, c);
    this.isElite = isElite;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX, Combat c){
    super(name, hp, c);
    this.isElite = isElite;
    this.middleX = middleX;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX, int hpBarLength, Combat c){
    super(name, hp, hpBarLength, c);
    this.isElite = isElite;
    this.middleX = middleX;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX, int hpBarLength, String[] art, Combat c){
    super(name, hp, hpBarLength, c, art);
    this.isElite = isElite;
    this.middleX = middleX;
  }
  public Enemy(String name, int hp, boolean isElite, int middleX, String[] art, Combat c){
    super(name, hp, art, c);
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
  public void doIntent(Entity p, Enemy copy){
    System.out.println("Basic Enemy Intent");
  }
  /**Sets the enemy's next intent. (Should always be overridden)
  */
  public void setNextIntent(){}

  //Not @Override
  /**Does necessary actions for the end of the enemy's turn
  */
  public void endTurn(Entity player){
    Enemy enemyCopy = new Enemy(this);
    //Creating a copy enemy and changing its statuses instead of the real entity's. (Still attacking with the real entity.) Then, syncs the copy's statuses to the real entity at the end of this method.
    System.out.println("About to do intent");
    this.doIntent(player, enemyCopy);
    for(int i=0; i<enemyCopy.getStatuses().size(); i++){
      Status s = enemyCopy.getStatuses().get(i);
      if(s.isDecreasing() && s.getStrength() > 0){
        s.subtractStrength(1);
      }
    }
    enemyCopy.addStatusStrength("Strength", this.getStatusStrength("Ritual")); //New rituals don't count until the end of the turn
    this.setBlock(0);
    this.setStatuses(enemyCopy.getStatuses());
    this.setNextIntent();
  }

  
  @Override
  public void die(){
    Combat combat = this.getCombat();
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