package app;
import enemyfiles.Enemy;

/** Contains all information needed to display the intent above an enemy's head.
 * Implementation left to the enemy's class.
 * 
 * @see Enemy
 */
public class Intent{
  public static final String[] defIntArt = Colors.fillColor(new String[] {"▄ ▄ ▄ ▄", "███████", "▀█████▀", "  ▀█▀  "}, Colors.blockBlue); // "▄▂▄▆▄▂▄"
  public static final String[] atkIntArt = Colors.fillColor(new String[] { "▄▄     ", "▀██▄ ▄ ", "  ▀██▀ ", "  ▀▀ ▀▄"}, Colors.atkIntArtRed);
  public static final String[] dbfIntArt = Colors.fillColor(new String[] {" ▄▄▄▄▄ ", "█ ▄▄ ▀█", "█ ▀ █ █", " ▀▀▀ ▄▀"}, Colors.dbfIntArtGrn);
  public static final String[] bufIntArt = new String[] {"       ", Colors.fillColor(" ▄ █▄ ▄", Colors.bufIntArtTop), Colors.bufIntArtTop + " ▀" + Colors.fillColor("████ ", Colors.bufIntArtMid), "  " + Colors.bufIntArtMid + "▀" + Colors.fillColor("█▀  ", Colors.bufIntArtBtm)};
  public static final String[] unkIntArt = Colors.fillColor(new String[] {"       ", "▄▀▄ ▄▀▄", " ▄▀  ▄▀", " ▄   ▄ "}, Colors.lightYellow);
  public static final String[] escIntArt = Colors.fillColor(new String[] {"  ▄▄   ", "▄▀  ▀  ", "█   ▄▄▄", " ▀▄▄▄▀█"}, Colors.escIntArtRed);
  public static final String[] blnkIntArt = new String[] {"       ", "       ", "       ", "       "};
  public static enum IntentType{
    ATTACK,
    DEFEND,
    BUFF,
    DEBUFF,
    ESCAPE,
    SLEEPING,
    STUNNED,
    UNKNOWN,
    BLANK
  }

  private String intentName;
  private IntentType type;
  private IntentType secondaryType;
  private int damage;
  private int multiattack = 1;

  public Intent(){
    intentName = "<Intent>";
    type = IntentType.BLANK;
  }
  public Intent(String name){
    intentName = name;
    type = IntentType.BLANK;
  }
  public Intent(IntentType intentType){
    intentName = "<Intent>";
    type = intentType;
  }
  public Intent(String name, IntentType type){
    intentName = name;
    this.type = type;
  }
  public Intent(String name, IntentType primaryType, IntentType secondaryType){
    intentName = name;
    type = primaryType;
    this.secondaryType = secondaryType;
  }
  public Intent(String name, int damage){
    intentName = name;
    type = IntentType.ATTACK;
    this.damage = damage;
  }
  public Intent(String name, int damage, int numOfAttacks){
    intentName = name;
    type = IntentType.BLANK;
    this.damage = damage;
    multiattack = numOfAttacks;
  }
  public Intent(String name, IntentType primaryType, IntentType secondaryType, int damage){
    intentName = name;
    type = primaryType;
    this.secondaryType = secondaryType;
    this.damage = damage;
  }
  public Intent(String name, IntentType primaryType, IntentType secondaryType, int damage, int numOfAttacks){
    intentName = name;
    type = primaryType;
    this.secondaryType = secondaryType;
    this.damage = damage;
    multiattack = numOfAttacks;
  }
  public Intent(Intent old){
    intentName = old.intentName;
    type = old.type;
    secondaryType = old.secondaryType;
    damage = old.damage;
    multiattack = old.multiattack;
  }
  
  //Getters and Setters
  public String getName(){ return intentName; }
  public int getDamage(){ return damage; }
  public void setDamage(int newDmg){ damage = newDmg; }

  /** Returns the Intent's pixel art as a String[]
  */
  public String[] getImage(Enemy enemy, Entity player){
    //Update damage counter thingy
    int calculatedDamage = -1; //Only equals -1 here b/c of init errors
    if(type == IntentType.ATTACK || secondaryType == IntentType.ATTACK){
      int strMultiplier = 1; //Amount of times to add strength to atk (i.e. for Heavy Blade)
      calculatedDamage = enemy.calcAttackDamage(player, damage, strMultiplier);
    }
    //Create Images:
    String[] img;
    switch(type){
      case ATTACK:
        img = new String[atkIntArt.length+1];
        for(int i=0; i<atkIntArt.length; i++){
          img[i] = atkIntArt[i];
        }
        String lastLine;
        if(multiattack == 1){
          lastLine = (calculatedDamage + "          ").substring(0, Str.lengthIgnoringEscSeqs(atkIntArt[0]));
        }else{
          lastLine = (calculatedDamage + "x" + multiattack + "        ").substring(0, Str.lengthIgnoringEscSeqs(atkIntArt[0]));
        }
      img[img.length - 1] = Colors.reset + Str.centerText(lastLine);
        break;
      case DEFEND:
        img = defIntArt.clone();
        break;
      case DEBUFF:
        img = dbfIntArt.clone();
        break;
      case UNKNOWN:
        img = unkIntArt.clone();
        break;
      case ESCAPE:
        img = escIntArt.clone();
        break;
      case BUFF:
        img = bufIntArt.clone();
        break;
      case BLANK:
        img = blnkIntArt.clone();
        break;
      default: 
        img = new String[4];
    }
    if(secondaryType != null){
      String[] img2;
      switch(secondaryType){
        case ATTACK:
          img2 = new String[atkIntArt.length+1];
          for(int i=0; i<atkIntArt.length; i++){
            img2[i] = atkIntArt[i];
          }
          String lastLine;
          if(multiattack == 1){
            lastLine = (calculatedDamage + "          ").substring(0, Str.lengthIgnoringEscSeqs(atkIntArt[0]));
          }else{
            lastLine = (calculatedDamage + "x" + multiattack + "        ").substring(0, Str.lengthIgnoringEscSeqs(atkIntArt[0]));
          }
          img2[img2.length - 1] = Colors.reset + Str.centerText(lastLine);
          break;
        case DEFEND:
          img2 = defIntArt.clone();
          break;
        case DEBUFF:
          img2 = dbfIntArt.clone();
          break;
        case BUFF:
          img2 = bufIntArt.clone();
          break;
        case UNKNOWN:
          img2 = unkIntArt.clone();
          break;
        case ESCAPE:
          img2 = escIntArt.clone();
          break;
        case BLANK:
          img2 = blnkIntArt.clone();
          break;
        default: 
          img2 = new String[4];
      }
      for(int i=0; i<img2.length; i++){ //if anything except atk can go first, all of them will probably need to be the same length
        img[i] += " " + img2[i];
      }
    }
    return img;
  }

}