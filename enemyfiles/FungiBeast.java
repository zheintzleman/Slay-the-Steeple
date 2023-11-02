package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class FungiBeast extends Enemy{
  private Intent intent;
  public static final Intent BITE = new Intent("Bite", 6); 
  public static final Intent GROW = new Intent("Grow", IntentType.BUFF);
  private int bitesInARow = 0;
  public static final String[] art = Colors.fillColor(new String[] {"     ▄▄▄▄        ", "    ██████       ", "▄█▄ ▄ █   ▄▄  ▄▄▄", "███████▄▄███▄▄█ █", " █▀▀█████████ ▀  █", "  ▀  ▀▀▀▀▀  ▀▄   "}, Colors.gray);
  

  public FungiBeast(int middleX, Combat c){
    super("Fungi Beast", (int)(Math.random()*7)+22, false, middleX, art, c);
    this.setStatusStrength("Spore Cloud", 2); //3-7 SporeCloud
    setNextIntent();
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player, Enemy copy){
    if(intent == BITE){
      this.attack(player, 6);
    }else if(intent == GROW){
      copy.addStatusStrengthDuringEndOfTurn("Strength", 3);
    }
  }

  public void setNextIntent(){
    boolean decided = false;
    double probBite = .6;
    while(!decided){
      double rng = Math.random();
      if(rng < probBite){
        if(bitesInARow < 2){
          intent = BITE;
          bitesInARow++;
          decided = true;
        }
      }else{
        if(intent != GROW){
          intent = GROW;
          bitesInARow = 0;
          decided = true;
        }
      }
    }
  }
  
}