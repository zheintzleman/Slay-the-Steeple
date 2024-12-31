package enemyfiles;
import java.util.*;

import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.Intent.IntentType;

public class ShieldGremlin extends Enemy{

  private Intent intent;
  public static final Intent SHIELDBASH = new Intent("Shield Bash", 6);
  public static final Intent PROTECT = new Intent("Protect", IntentType.DEFEND);
  private ArrayList<Enemy> allies; //List of the other gremlins/enemies it is with
  public static final String[] art = Colors.fillColor(new String[] {"   ▄▄▄▄▀ ", "█ ▀ ██   ", "▀█▄ ▄█▄  ", " ▀█████▄ ", " ▄█████  ", "   ████  ", "   █ ▀█▄ ", "   █  █▀▀"}, Colors.shieldGPink);

  
  public ShieldGremlin(int middleX){
    super("Shield Gremlin", (int)(Math.random()*4)+12, false, middleX, 11, art);
    intent = PROTECT;
    allies = Combat.c.getEnemies();
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }


  public int getIndexInAllies(){
    for(int i=0; i<allies.size(); i++){
      if(allies.get(i) == this){
        return i;
      }
    }
    throw new IndexOutOfBoundsException("Did not find this enemy in list of enemies.");
  }

  
  public void doIntent(Entity player){
    if(intent == SHIELDBASH){
      attack(player, 6);
    }else if(intent == PROTECT){
      int rn = (int)(Math.random()*allies.size());
      while(rn == getIndexInAllies()){
        rn = (int)(Math.random()*allies.size());
      }
      Enemy target = allies.get(rn);
      giveBlock(target, 7);
    }
  }


  public void setNextIntent(){
    if(Combat.c.getEnemies().size() == 1){
      intent = SHIELDBASH;
    }
    intent = PROTECT;
  }
}