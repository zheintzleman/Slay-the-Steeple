package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class GreenLouse extends Enemy{
  private Intent intent;
  public static final Intent BITE = new Intent("Bite", IntentType.ATTACK);
  public static final Intent SPITWEB = new Intent("Spit Web", IntentType.DEBUFF);
  private Intent bite;
  private int bitesInARow = 0;
  private int spitWebsInARow = 0;
  private int biteDamage;
  public static final String[] art = Colors.fillColor(new String[] {" ▄        ", " █████▄▄ ", "█████████"}, Colors.louseGreen);
  

  public GreenLouse(int middleX, Combat c){
    super("Green Louse", (int)(Math.random()*7)+11, false, middleX, 17, art, c);
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
    }else if(intent == SPITWEB){
      player.addStatusStrengthDuringEndOfTurn("Weak", 2);
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
          spitWebsInARow = 0;
          decided = true;
        }
      }else{
        if(spitWebsInARow < 2){
          intent = SPITWEB;
          spitWebsInARow++;
          bitesInARow = 0;
          decided = true;
        }
      }
    }
  }
  
}