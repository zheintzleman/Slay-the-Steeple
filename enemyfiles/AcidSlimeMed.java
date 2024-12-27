package enemyfiles;

import java.util.*;

import app.Card;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.Intent.IntentType;

public class AcidSlimeMed extends Enemy{

  private Intent intent;
  public static final Intent TACKLE = new Intent("Tackle", 10);
  public static final Intent CORROSIVESPIT = new Intent("Corrosive Spit", IntentType.ATTACK, IntentType.DEBUFF, 7);
  public static final Intent LICK = new Intent("Lick", IntentType.DEBUFF);
  private int spitsInARow = 0;
  public static final String[] art = Colors.fillColor(new String[] {"      ▄▄▄▄▄▄       ", "    ▄████████▄     ", "  ▄████████████▄   ", "▄████████████████▄ ", "███████████████████", " ▀▀▀██████████▀▀▀  "}, Colors.lightGreen);


  public AcidSlimeMed(int middleX){
    super(" Acid Slime (M)", (int)(Math.random()*5)+28, false, middleX, 17, art);
    setNextIntent();
  }
  public AcidSlimeMed(AcidSlimeLarge lSlime, int middleX){
    super(" Acid Slime (M)", lSlime.getHP(), false, middleX, 17, art);
    setNextIntent();
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  @Override
  public void doIntent(Entity player){
    if(intent == TACKLE){
      attack(player, 10);
    }else if(intent == CORROSIVESPIT){
      attack(player, 7);
      ArrayList<Card> disc = Combat.c.getDiscardPile();
      disc.add(Card.getCard("Slimed"));
    }else if(intent == LICK){
      player.addStatusStrength("Weak", 1);
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
}