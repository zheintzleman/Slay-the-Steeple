package enemyfiles;
import app.Entity;
import enemyfiles.Intent.IntentType;
import util.Colors;

public class JawWorm extends Enemy{
  private Intent intent;
  public static final Intent CHOMP = new Intent("Chomp", 11);
  public static final Intent THRASH = new Intent("Thrash", IntentType.ATTACK, IntentType.DEFEND, 7);
  public static final Intent BELLOW = new Intent("Bellow", IntentType.DEFEND, IntentType.BUFF);
  private int numThrashesInARow = 0;
  public static final String[] art = new String[] {Colors.fillColor("      ▄▄▄       ", Colors.jawWormBlue),
                                                   Colors.fillColor("  ▄▄██████▄     ", Colors.jawWormBlue),
                                                   Colors.fillColor("▄███████████▄   ", Colors.jawWormBlue),
                                                   Colors.fillColor("▄▄▄▄▄▄", Colors.jawWormBlueOnGray) + Colors.reset + Colors.fillColor("███████▄  ", Colors.jawWormBlue) + Colors.reset,
                                                   Colors.fillColor("   ▀▀▀▀▀▀█▀ ▀▀▀▀", Colors.jawWormBlue)};
  

  public JawWorm(int middleX){
    super("Jaw Worm", (int)(Math.random()*5)+40, false, middleX, art);
    intent = CHOMP; //Always starts w/ Chomp
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player){
    if(intent == CHOMP){
      attack(player, 11);
    }else if(intent == THRASH){
      attack(player, 7);
      block(5, false);
    }else if(intent == BELLOW){
      block(6, false);
      addStatusStrength("Strength", 3);
    }
  }

  public void setNextIntent(){
    boolean decided = false;
    double probChomp = .25;
    double probThrash = .3;
    //double probBellow = .45;
    while(!decided){
      double rng = Math.random();
      if(rng < probChomp){
        if(intent != CHOMP){
          intent = CHOMP;
          numThrashesInARow = 0;
          decided = true;
        }
      }else if(rng < probChomp + probThrash){
        if(numThrashesInARow < 2){
          intent = THRASH;
          numThrashesInARow++;
          decided = true;
        }
      }else{
        if(intent != BELLOW){
          intent = BELLOW;
          numThrashesInARow = 0;
          decided = true;
        }
      }
    }
  }
  
}