package enemyfiles;
import app.Colors;
import app.Entity;
import app.Intent;
import app.Intent.IntentType;

public class Cultist extends Enemy{
  private Intent intent;
  public static final Intent INCANTATION = new Intent("Incantation", IntentType.BUFF);
  public static final Intent DARKSTRIKE = new Intent("Dark Strike", 6);
  public static final String[] art = Colors.fillColor(new String[] {" ▄▄              ", " ▄ █           ▀▄", " ▀█▀ ▄█▄      █▄█", "  ▀██████▄▄▄████ ", "   ▀▀▀ ████▀█████", "      ▄████    ▀ ", "      █████▄ ▄   ", "       █ █▄ ▀    "}, Colors.cultistBlue);
  

  public Cultist(int middleX){
    super("Cultist", (int)(Math.random()*5)+40, false, middleX, art);
    intent = INCANTATION; //Always starts w/ Incantaion
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player){
    if(intent == DARKSTRIKE){
      System.out.println("Attacking player for 6");
      attack(player, 6);
    }else if(intent == INCANTATION){
      addStatusStrength("Ritual", 3);
    }
  }

  public void setNextIntent(){
    intent = DARKSTRIKE;
  }
  
}