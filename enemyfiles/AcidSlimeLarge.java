package enemyfiles;
import java.util.*;

import app.Card;
import app.Combat;
import app.Entity;
import enemyfiles.Intent.IntentType;
import util.Colors;

public class AcidSlimeLarge extends Enemy{

  private Intent intent;
  public static final Intent TACKLE = new Intent("Tackle", 16);
  public static final Intent CORROSIVESPIT = new Intent("Corrosive Spit", IntentType.ATTACK, IntentType.DEBUFF, 11);
  public static final Intent LICK = new Intent("Lick", IntentType.DEBUFF);
  public static final Intent SPLIT = new Intent("Split", IntentType.UNKNOWN);
  private int spitsInARow = 0;
  public static final String[] art = Colors.fillColor(new String[] {"             ▄▄▄▄▄▄▄▄▄▄                  ", "          ▄██████████████▄               ", "        ▄██████████████████▄             ", "     ▄▄███████████████████████▄▄         ", "   ▄█████████████████████████████▄▄      ", "▄████████████████████████████████████▄   ", "█████████████████████████████████████▀   ", "▀█████████████████████████████████▀  ▄██▄", "   ▀▀▀██████▀▀▀▀▀▀█████████████▀▀         ", "                   ▀▀▀███▀▀              "}, Colors.lightGreen);
    


  public AcidSlimeLarge(int middleX){
    super(" Acid Slime (L)", (int)(Math.random()*5)+65, false, middleX, 41, art);
    setStatusStrength("Split", 1);
    setNextIntent();
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  @Override
  public void doIntent(Entity player){
    if(intent == TACKLE){
      attack(player, 16);
    }else if(intent == CORROSIVESPIT){
      attack(player, 11);
      ArrayList<Card> disc = Combat.c.getDiscardPile();
      disc.add(Card.getCard("Slimed"));
      disc.add(Card.getCard("Slimed"));
    }else if(intent == LICK){
      player.addStatusStrength("Weak", 2);
    }else if(intent == SPLIT){
      ArrayList<Enemy> enemiesToUpdate = Combat.c.getEnemiesToUpdate();
      int thisIndex = -1;
      for(int i=0; i<enemiesToUpdate.size(); i++){
        Enemy e = enemiesToUpdate.get(i);
        if(e == this){
          thisIndex = i;
        }
      }
      enemiesToUpdate.remove(thisIndex);
      enemiesToUpdate.add(thisIndex, new AcidSlimeMed(this, this.getMiddleX() + 11));
      enemiesToUpdate.add(thisIndex, new AcidSlimeMed(this, this.getMiddleX() - 11));
    }
  }

  @Override
  public void setNextIntent(){
    boolean decided = false;
    double probTackle = .4;
    double probSpit = .3;
    while(!decided){
      double rng = Math.random();
      if(rng < probTackle){
        if(intent != TACKLE){
          intent = TACKLE;
          spitsInARow = 0;
          decided = true;
        }
      }else if(rng < probTackle + probSpit){
        if(spitsInARow < 2){
          intent = CORROSIVESPIT;
          spitsInARow++;
          decided = true;
        }
      }else{
        if(intent != LICK){
          intent = LICK;
          spitsInARow = 0;
          decided = true;
        }
      }
    }
  }
  
  public void setSplitIntent(){
    intent = SPLIT;
  }
}