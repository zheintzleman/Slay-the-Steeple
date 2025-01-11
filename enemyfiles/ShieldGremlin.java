package enemyfiles;
import java.util.*;

import app.Combat;
import app.Entity;
import enemyfiles.Intent.IntentType;
import util.Colors;
import util.Util;

public class ShieldGremlin extends Enemy{

  private Intent intent;
  public static final Intent SHIELDBASH = new Intent("Shield Bash", 6);
  public static final Intent PROTECT = new Intent("Protect", IntentType.DEFEND);
  public static final String[] art = Colors.fillColor(new String[] {"   ▄▄▄▄▀ ", "█ ▀ ██   ", "▀█▄ ▄█▄  ", " ▀█████▄ ", " ▄█████  ", "   ████  ", "   █ ▀█▄ ", "   █  █▀▀"}, Colors.shieldGPink);

  
  public ShieldGremlin(int middleX){
    super("Shield Gremlin", (int)(Math.random()*4)+12, false, middleX, 11, art);
    intent = PROTECT;
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }
  
  
  public void doIntent(Entity player){
    if(intent == SHIELDBASH){
      attack(player, 6);
    }else if(intent == PROTECT){
      List<Enemy> allies = new ArrayList<>(Combat.c.getEnemies());
      allies.remove(this);
      Enemy target = Util.randElt(allies);
      if(target != null){
        giveBlock(target, 7);
      }
    }
  }

  public void setNextIntent(){
    if(Combat.c.getEnemies().size() == 1){
      intent = SHIELDBASH;
    }
    intent = PROTECT;
  }
}