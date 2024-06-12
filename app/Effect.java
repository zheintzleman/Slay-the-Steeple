/**Generic class for effects that cards/statuses/etc. might have during the game.
 * Usually stored as List<Effect>, with each card/status/etc. do at least one effect. 
 * When the effect goes off is also specifies -- defaults to "OnPlay", which is only available
 * for cards.
 * Primary: First word of input data.
 * Secondary: Rest of input data, apart from terminal integer
 * Power: Terminal integer, or 1 if last word not an integer
 * WhenPlayed: Defaults to ONPLAY; begin with "(OnDiscard) "/"(OnTurnEnd) "/etc. to change.
 * e.g. Stores "Lorem Ipsum Dolor 4" as: P = "Lorem", S = "Ipsum Dolor", p = 4,
 *  or "Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1
 *  or "(OnExhaust) Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1, WP = ONEXHAUST
 * 
 * See Combat.playCard(), Combat.playEff(), and EventManager.
 */

package app;

import java.io.Serializable;

import app.EventManager.Event;

public abstract class Effect implements Serializable {
  public static final String[] ATTACK_PRIMARIES = new String[] {"Attack", "AtkAll", "BodySlam", "SearingBlow", "HeavyAttack", "AtkRandom"}; //Can remove
  public static final String[] DEFENSE_PRIMARIES = new String[] {"Blk"};
  // Primaries that affect game state outside of the current combat (i.e. that matter even after the combat ends.)
  public static final String[] RUN_STATE_PRIMARIES = new String[] {}; //TODO: Fill out as I add them.

  private String primary;
  private String secondary;
  private int power;
  private Event whenPlayed;

  public Effect(String data){
    String str = data;
    whenPlayed = Event.ONCARDPLAY;

    if(str.startsWith("(")){
      String[] halves = str.split("[\\(\\)] ", 2);
      App.ASSERT(halves.length == 2);
      String enumName = halves[0].substring(1).toUpperCase();
      whenPlayed = Event.valueOf(enumName);
      str = halves[1];
    }
    if(str.equals("Ethereal")){
      whenPlayed = Event.ONTURNEND;
    }

    int lastSpaceIndex = str.lastIndexOf(" ");

    try{
      power = Integer.parseInt(str.substring(lastSpaceIndex+1));
      str = str.substring(0, lastSpaceIndex);
    }catch(NumberFormatException e){
      if(str.endsWith(" <str>")){
        str = str.substring(0, str.length()-6);
      }
      power = 1;
    }
    
    int spaceIndex = str.indexOf(" ");
    if(spaceIndex == -1){
      primary = str;
      secondary = "";
    }else{
      primary = str.substring(0, spaceIndex);
      secondary = str.substring(spaceIndex+1);
    }
  }
  public Effect(Effect prev){
    primary = prev.primary;
    secondary = prev.secondary;
    power = prev.power;
    whenPlayed = prev.whenPlayed;
  }

  //Getters and setters
  public String getPrimary(){ return primary; }
  public void setPrimary(String primary){ this.primary = primary; }
  public String getSecondary(){ return secondary; }
  public void setSecondary(String secondary){ this.secondary = secondary; }
  public int getPower(){ return power; }
  public void setPower(int power) { this.power = power; }
  public Event whenPlayed(){ return whenPlayed; }

  public boolean isAttack(){ //TODO: Why not just make this check if the card is an attack card?
    for(String s : ATTACK_PRIMARIES){
      if(primary.equals(s)){
        return true;
      }
    }
    return false;
  }
  public boolean isDefense(){
    for(String s : DEFENSE_PRIMARIES){
      if(primary.equals(s)){
        return true;
      }
    }
    return false;
  }
  public boolean affectsRunState(){
    for(String s : DEFENSE_PRIMARIES){
      if(primary.equals(s)){
        return true;
      }
    }
    return false;
  }
  /**
   * Converts the secondary (all text between the first word & an optional last number)
   * to an Integer. If not an integer, returns defaultVal.
   * @return The secondary as an integer, or defaultVal if n/a.
   */
  public int getIntFromSecondary(int defaultVal){
    try{
      return Integer.parseInt(secondary);
    } catch (NumberFormatException e) {
      return defaultVal;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass()) //TODO: Does this automatically make CardEffects and StatusEffects not equal? Should -- if not, would need to do funky stuff w/ power below.
      return false;
    Effect other = (Effect) obj;

    return primary.equals(other.primary)
        && secondary.equals(other.secondary)
        && power == other.power;
  }
}