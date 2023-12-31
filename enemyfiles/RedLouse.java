package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class RedLouse extends Enemy{
  private Intent intent;
  public static final Intent BITE = new Intent("Bite", IntentType.ATTACK);
  public static final Intent GROW = new Intent("Grow", IntentType.BUFF);
  private Intent bite;
  private int bitesInARow = 0;
  private int growsInARow = 0;
  private int biteDamage;
  public static final String[] art = Colors.fillColor(new String[] {" ▄▀▀▀     ", " █████▄▄ ", "█████████"}, Colors.louseRed);
  

  public RedLouse(int middleX, Combat c){
    super("Red Louse", (int)(Math.random()*6)+10, false, middleX, 17, art, c);
    this.setStatusStrength("Curl Up", (int)(Math.random()*5)+3); //3-7 CurlUp
    biteDamage = (int)(Math.random()*2) + 5;
    bite = new Intent(BITE);
    bite.setDamage(biteDamage);
    setNextIntent();
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player, Enemy copy){
    if(intent == bite){
      this.attack(player, biteDamage);
    }else if(intent == GROW){
      copy.addStatusStrengthDuringEndOfTurn("Strength", 3);
    }
  }

  public void setNextIntent(){
    boolean decided = false;
    double probBite = .75;
    while(!decided){
      double rng = Math.random();
      if(rng < probBite){
        if(bitesInARow < 2){
          intent = bite;
          bitesInARow++;
          growsInARow = 0;
          decided = true;
        }
      }else{
        if(growsInARow < 2){
          intent = GROW;
          growsInARow++;
          bitesInARow = 0;
          decided = true;
        }
      }
    }
  }
  
}