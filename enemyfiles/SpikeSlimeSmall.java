package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;

public class SpikeSlimeSmall extends Enemy{

  private Intent intent;
  public static final Intent TACKLE = new Intent("Tackle", 5);
  public static final String[] art = Colors.fillColor(new String[] {"   ▄█▄   ", " ▄█████▄ ", "▀███████▀"}, Colors.spikeSlimeColor);


  public SpikeSlimeSmall(int middleX, Combat c){
    super("Spike Slime (S)", (int)(Math.random()*5)+10, false, middleX, 11, art, c);
    intent = TACKLE;
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player, Enemy copy){
    if(intent == TACKLE){
      this.attack(player, 5);
    }
  }

  public void setNextIntent(){
    intent = TACKLE;
  }
}