package enemyfiles;
import app.Colors;
import app.Combat;
import app.Entity;
import app.Intent;
import app.IntentType;

public class RedSlaver extends Enemy{
  private Intent intent;
  public static final Intent STAB = new Intent("Stab", 13);
  public static final Intent SCRAPE = new Intent("Scrape", IntentType.ATTACK, IntentType.DEBUFF, 8);
  public static final Intent ENTANGLE = new Intent("Entangle", IntentType.DEBUFF);
  private int numStabsInARow = 0;
  private int numScrapesInARow = 0;
  boolean hasPlayedEntangled = false;
  private int patternNum = 0; 
  public static final String[] art = Colors.fillColor(new String[] {"        ▄▄▄        ", "        ██████▄    ", "         ▀█████▄   ", "          ██████   ", "          █▀█████  ", "▄█▄█▄█▄▄▄▄██▄█████▄▄", " ▀ ▀ ▀      ████▀  ", "            ▀█▀█   ", "            ▄█▀▀   "}, Colors.slaverRed);
  

  public RedSlaver(int middleX, Combat c){
    super("Red Slaver", (int)(Math.random()*5)+46, false, middleX, art, c);
    intent = STAB;
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player, Enemy copy){
    if(intent == STAB){
      this.attack(player, 12);
    }else if(intent == SCRAPE){
      this.attack(player, 7);
      player.addStatusStrengthDuringEndOfTurn("Weak", 1);
    }else if(intent == ENTANGLE){
      player.addStatusStrengthDuringEndOfTurn("Entangled", 1);
    }
    patternNum++;
  }

  public void setNextIntent(){
    if(hasPlayedEntangled){
      setIntentIfHasPlayedEntangled();
    }else{
      setIntentIfHasNotPlayedEntangled();
    }
  }

  public void setIntentIfHasPlayedEntangled(){
    //55% chance of Scrape and 45% chance of Stab
    boolean decided = false;
    double probStab = .45;
    while(!decided){
      double rng = Math.random();
      if(rng < probStab){
        if(numStabsInARow < 2){
          intent = STAB;
          numStabsInARow++;
          numScrapesInARow = 0;
          decided = true;
        }
      }else{
        if(numScrapesInARow < 2){
          intent = SCRAPE;
          numScrapesInARow++;
          numStabsInARow = 0;
          decided = true;
        }
      }
    }
  }
  public void setIntentIfHasNotPlayedEntangled(){
    //25% chance of doing entangle each turn
    if(Math.random() < 0.25){
      intent = ENTANGLE;
      hasPlayedEntangled = true;
      return;
    }
    
    //Else: does stab, scrape, scrape, repeat
    if(patternNum%3 == 0){
      intent = STAB;
      //Don't need to update in-a-row ints here
      return;
    }

    intent = SCRAPE;
  }
  
}