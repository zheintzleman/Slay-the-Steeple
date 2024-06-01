package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;

public class MadGremlin extends Enemy{

  private Intent intent;
  public static final Intent SCRATCH = new Intent("Scratch", 4);
public static final String[] art = Colors.fillColor(new String[] {"  ▄  █     ", "   ▀██ ▄█▄ ", " ▄  ██▄▄▄▀ ", "▀▀▀▀▀██   ", "   ▄█▀█    ", "   █  █    ", "   ▀  █    "}, Colors.madGRed);


  public MadGremlin(int middleX, Combat c){
    super("Mad Gremlin", (int)(Math.random()*5)+20, false, middleX, 13, art, c);
    intent = SCRATCH;
    addStatusStrength("Angry", 1);
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player){
    if(intent == SCRATCH){
      attack(player, 4);
    }
  }

  public void setNextIntent(){
    intent = SCRATCH;
  }
}