package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class AcidSlimeSmall extends Enemy{

  private Intent intent;
  public static final Intent TACKLE = new Intent("Tackle", 3);
  public static final Intent LICK = new Intent("Lick", IntentType.DEBUFF);
  public static final String[] art = Colors.fillColor(new String[] {"   ▄▄▄   ", " ▄█████▄ ", "▀███████▀"}, Colors.lightGreen);


  public AcidSlimeSmall(int middleX, Combat c){
    super("Acid Slime (S)", (int)(Math.random()*5)+8, false, middleX, 11, art, c);
    if(Math.random() < 0.5){
      intent = TACKLE;
    }else{
      intent = LICK;
    }
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  @Override
  public void doIntent(Entity player, Enemy copy){
    if(intent == TACKLE){
      this.attack(player, 3);
    }else if(intent == LICK){
      player.addStatusStrengthDuringEndOfTurn("Weak", 1);
    }
  }

  @Override
  public void setNextIntent(){
    //Alternate between the two moves
    if(intent == TACKLE){
      intent = LICK;
    }else{
      intent = TACKLE;
    }
  }
}