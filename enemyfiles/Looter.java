package enemyfiles;
import java.util.*;

import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class Looter extends Enemy{

  private Intent intent;
  public static final Intent MUG = new Intent("Mug", 10);
  public static final Intent LUNGE = new Intent("Tackle", 12);
  public static final Intent SMOKEBOMB = new Intent("Smoke Bomb", IntentType.DEFEND);
  public static final Intent ESCAPE = new Intent("Escape", IntentType.ESCAPE);
  private int patternNum;
  private int goldStolen;
  public static final String[] art = Colors.fillColor(new String[] {"       ▄█▄     ", "   ▄  ▀████    ", "   ▀▄    ███   ", "▀▀▄▄██▄▄████   ", "     ▀▀▀▀████  ", "      ▄██████  ", "     ██▀  ██ ", "     ▀█   ██   ", "      ▀    █   "}, Colors.slaverBlue);


  public Looter(int middleX, Combat c){
    super("Looter", (int)(Math.random()*5)+44, false, middleX, art, c);
    this.addStatusStrength("Thievery", 15);
    intent = MUG;
    patternNum = 1;
    goldStolen = 0;
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player, Enemy copy){
    if(intent == MUG){
      this.attack(player, 10);
      goldStolen += this.getCombat().getRun().loseGold(15);
    }else if(intent == LUNGE){
      this.attack(player, 12);
      goldStolen += this.getCombat().getRun().loseGold(15);
    }else if(intent == SMOKEBOMB){
      this.blockAfterTurn(6);
    }else if(intent == ESCAPE){
      ArrayList<Enemy> enemiesToUpdate = this.getCombat().getEnemiesToUpdate();
      int thisIndex = -1;
      for(int i=0; i<enemiesToUpdate.size(); i++){
        Enemy e = enemiesToUpdate.get(i);
        if(e == this){
          thisIndex = i;
        }
      }
      enemiesToUpdate.remove(thisIndex);
      if(enemiesToUpdate.size() == 0){
        this.getCombat().endCombat();
      }
    }
    patternNum++;
  }

  public void setNextIntent(){
    if(patternNum == 2){
      intent = MUG;
      return;
    }
    
    if(patternNum == 3){
      double rn = Math.random();
      if(rn < 0.5){
        intent = LUNGE;
      }else{
        intent = SMOKEBOMB;
      }
      return;
    }
    
    if(intent == LUNGE){
      intent = SMOKEBOMB;
      return;
    }

    if(intent == SMOKEBOMB){
      intent = ESCAPE;
      return;
    }
  }

  @Override
  public void die(){
    this.getCombat().getRun().addGold(goldStolen);
    super.die();
  }
}