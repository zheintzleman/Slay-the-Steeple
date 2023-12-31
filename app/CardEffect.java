package app;

import java.io.Serializable;

public class CardEffect implements Serializable {
  public static final String[] ATTACK_PRIMARIES = new String[] {"Attack", "AtkAll", "BodySlam", "SearingBlow"}; //Can remove
  public static final String[] DEFENSE_PRIMARIES = new String[] {"Blk"};
  // Primaries that affect game state outside of the current combat (i.e. that matter even after the combat ends.)
  public static final String[] RUN_STATE_PRIMARIES = new String[] {}; //TODO: Fill out as I add them.

  private String primary;
  private String secondary;
  private int power; //Remove basepower?

  //Primary: First word of input data.
  //Secondary: Rest of input data, apart from terminal integer
  //Power: Terminal integer, or 0 if last word not an integer
  //e.g. Stores "Lorem Ipsum Dolor 4" as: P = "Lorem", S = "Ipsum Dolor", p = 4,
  // or "Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 0.
  public CardEffect(String data){
    power = 0;
    String str = data;
    int lastSpaceIndex = str.lastIndexOf(" ");

    try{
      power = Integer.parseInt(str.substring(lastSpaceIndex+1));
      str = str.substring(0, lastSpaceIndex);
    }catch(NumberFormatException e){}
    
    int spaceIndex = str.indexOf(" ");
    if(spaceIndex == -1){
      primary = str;
      secondary = "";
    }else{
      primary = str.substring(0, spaceIndex);
      secondary = str.substring(spaceIndex+1);
    }
  }
  public CardEffect(String primary, String secondary, int power){
    this.primary = primary;
    this.secondary = secondary;
    this.power = power;
  }

  //Getters and setters
  public String getPrimary(){ return primary; }
  public void setPrimary(String primary){ this.primary = primary; }
  public String getSecondary(){ return secondary; }
  public void setSecondary(String secondary){ this.secondary = secondary; }
  public int getPower(){ return power; }
  public void setPower(int power) { this.power = power; }

  public boolean isAttack(){
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CardEffect other = (CardEffect) obj;

    return primary.equals(other.primary)
        && secondary.equals(other.secondary)
        && power == other.power;
  }
}