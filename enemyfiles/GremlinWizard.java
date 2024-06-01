package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class GremlinWizard extends Enemy{

  private Intent intent;
  public static final Intent CHARGING = new Intent("Charging", IntentType.UNKNOWN);
  public static final Intent ULTIMATEBLAST = new Intent("Puncture", 25);
  int patternNum;
public static final String[] art = Colors.fillColor(new String[] {"      █▄▀", " ▄▄  ▀██ ", "▀▄ █▄▄██ ", "   █  ██ ", "   █ █ █ "}, Colors.wizardPurple);


  public GremlinWizard(int middleX, Combat c){
    super("Gremlin Wizard", (int)(Math.random()*3)+23, false, middleX, 13, art, c);
    intent = CHARGING;
    patternNum = 2;
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player){
    if(intent == CHARGING){
      //Does nothing
    }else if(intent == ULTIMATEBLAST){
      attack(player, 25);
    }
    patternNum++;
  }

  public void setNextIntent(){
    if(patternNum%4 == 0){
      //Third turn and every 4 turns after that
      intent = ULTIMATEBLAST;
    }else{
      intent = CHARGING;
    }
  }
}