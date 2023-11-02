package enemyfiles;
import java.util.*;

import app.Card;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class SpikeSlimeMed extends Enemy{

  private Intent intent;
  public static final Intent FLAMETACKLE = new Intent("Flame Tackle", IntentType.ATTACK, IntentType.DEBUFF, 8);
  public static final Intent LICK = new Intent("Lick", IntentType.DEBUFF);
  private int FTsInARow = 0;
  private int licksInARow = 0;
  private Combat combat;
  public static final String[] art = Colors.fillColor(new String[] {"      ▄▄▄▄▄▄       ",  "    ██████████     ", "  ▄████████████▄   ", "▄████████████████▄ ", "███████████████████", " ▀▀▀██████████▀▀▀  "}, Colors.spikeSlimeColor);


  public SpikeSlimeMed(int middleX, Combat c){
    super("Spike Slime (M)", (int)(Math.random()*5)+28, false, middleX, 17, art, c);
    combat = c;
    setNextIntent();
  }
  public SpikeSlimeMed(SpikeSlimeLarge lSlime, int middleX){
    super("Spike Slime (M)", lSlime.getHP(), false, middleX, 17, art, lSlime.getCombat());
    combat = lSlime.getCombat();
    setNextIntent();
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player, Enemy copy){
    if(intent == FLAMETACKLE){
      this.attack(player, 8);
      ArrayList<Card> disc = combat.getDiscardPile();
      disc.add(Card.getCard("Slimed"));
    }else if(intent == LICK){
      player.addStatusStrengthDuringEndOfTurn("Frail", 1);
    }
  }

  public void setNextIntent(){
    boolean decided = false;
    double probTackle = .3;
    while(!decided){
      double rng = Math.random();
      if(rng < probTackle){
        if(FTsInARow < 2){
          intent = FLAMETACKLE;
          FTsInARow++;
          licksInARow = 0;
          decided = true;
        }
      }else{
        if(licksInARow < 2){
          intent = LICK;
          licksInARow++;
          FTsInARow = 0;
          decided = true;
        }
      }
    }
  }
}