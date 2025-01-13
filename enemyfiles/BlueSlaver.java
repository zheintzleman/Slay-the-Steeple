package enemyfiles;
import app.Entity;
import enemyfiles.Intent.IntentType;
import util.Colors;

public class BlueSlaver extends Enemy{
  private Intent intent;
  public static final Intent STAB = new Intent("Stab", 12);
  public static final Intent RAKE = new Intent("Rake", IntentType.ATTACK, IntentType.DEBUFF, 7);
  private int numStabsInARow = 0;
  private int numRakesInARow = 0;
  public static final String[] art = Colors.fillColor(new String[] {"        ▄▄▄         ", "        ██████▄     ", "         ▀█████▄    ", "          ██████    ", "          █▀█████   ", "▄▀▀▀▀█▄▄▄▄██▄█████▄▄", " ▀          ████▀   ", "            ▀█▀█    ", "            ▄█▀▀    "}, Colors.slaverBlue);
  

  public BlueSlaver(int middleX){
    super("Blue Slaver", (int)(Math.random()*5)+46, false, middleX, art);
    setNextIntent();
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player){
    if(intent == STAB){
      attack(player, 12);
    }else if(intent == RAKE){
      attack(player, 7);
      player.addStatusStrength("Weak", 1);
    }
  }

  public void setNextIntent(){
    boolean decided = false;
    double probStab = .6;
    while(!decided){
      double rng = Math.random();
      if(rng < probStab){
        if(numStabsInARow < 2){
          intent = STAB;
          numStabsInARow++;
          numRakesInARow = 0;
          decided = true;
        }
      }else{
        if(numRakesInARow < 2){
          intent = RAKE;
          numRakesInARow++;
          numStabsInARow = 0;
          decided = true;
        }
      }
    }
  }
  
}