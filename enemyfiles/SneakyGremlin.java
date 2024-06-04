package enemyfiles;
import app.Colors;
import app.Entity;
import app.Intent;

public class SneakyGremlin extends Enemy{

  private Intent intent;
  public static final Intent PUNCTURE = new Intent("Puncture", 9);
public static final String[] art = Colors.fillColor(new String[] {"    ▄▄   ▄▄  ", "     ▀███▀   ", "▄     ███    ", " ▀▄▄  ▄███▄  ", "   ▀▀▀▀████  ", "      ▄▀ █   "}, Colors.sneakyGColor);


  public SneakyGremlin(int middleX){
    super("Sneaky Gremlin", (int)(Math.random()*5)+10, false, middleX, 11, art);
    intent = PUNCTURE;
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player){
    if(intent == PUNCTURE){
      attack(player, 9);
    }
  }

  public void setNextIntent(){
    intent = PUNCTURE;
  }
}