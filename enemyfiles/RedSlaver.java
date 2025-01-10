package enemyfiles;
import app.Entity;
import enemyfiles.Intent.IntentType;
import util.Colors;

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
  

  public RedSlaver(int middleX){
    super("Red Slaver", (int)(Math.random()*5)+46, false, middleX, art);
    intent = STAB;
  }

  //Getters and Setters
  public Intent getIntent(){ return intent; }
  public String getIntentName(){ return intent.getName(); }

  
  public void doIntent(Entity player){
    if(intent == STAB){
      attack(player, 12);
    }else if(intent == SCRAPE){
      attack(player, 7);
      player.addStatusStrength("Weak", 1);
    }else if(intent == ENTANGLE){
      player.addStatusStrength("Entangled", 1);
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