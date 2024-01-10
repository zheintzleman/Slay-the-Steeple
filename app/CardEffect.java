package app;

import java.io.Serializable;

public class CardEffect implements Serializable {
  public static final String[] ATTACK_PRIMARIES = new String[] {"Attack", "AtkAll", "BodySlam", "SearingBlow", "HeavyAttack", "AtkRandom"}; //Can remove
  public static final String[] DEFENSE_PRIMARIES = new String[] {"Blk"};
  // Primaries that affect game state outside of the current combat (i.e. that matter even after the combat ends.)
  public static final String[] RUN_STATE_PRIMARIES = new String[] {}; //TODO: Fill out as I add them.

  public enum PlayEvent{
    ONPLAY,
    ONDISCARD, //TODO: Note that this should probably only be called(?) when a card's effect discards this card
    ONEXHAUST,
    ONTURNEND,
    ONDRAW,
    ONPLAYERHURT
  }

  private String primary;
  private String secondary;
  private int power;
  private PlayEvent whenPlayed;

  //Primary: First word of input data.
  //Secondary: Rest of input data, apart from terminal integer
  //Power: Terminal integer, or 1 if last word not an integer
  //WhenPlayed: Defaults to ONPLAY; begin with "(OnDiscard) "/"(OnTurnEnd) "/etc. to change.
  //e.g. Stores "Lorem Ipsum Dolor 4" as: P = "Lorem", S = "Ipsum Dolor", p = 4,
  // or "Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1
  // or "(OnExhaust) Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1, WP = ONEXHAUST
  public CardEffect(String data){
    power = 1;
    String str = data;
    whenPlayed = PlayEvent.ONPLAY;

    if(str.startsWith("(OnExhaust) ")){
      whenPlayed = PlayEvent.ONEXHAUST;
      str = str.substring("(OnExhaust) ".length());
    } else if(str.startsWith("(OnDiscard) ")){
      whenPlayed = PlayEvent.ONDISCARD;
      str = str.substring("(OnDiscard) ".length());
    } else if(str.startsWith("(OnTurnEnd) ")){
      whenPlayed = PlayEvent.ONTURNEND;
      str = str.substring("(OnTurnEnd) ".length());
    } else if(str.startsWith("(OnDraw) ")){
      whenPlayed = PlayEvent.ONDRAW;
      str = str.substring("(OnDraw) ".length());
    } else if(str.startsWith("(OnPlayerHurt) ")){
      whenPlayed = PlayEvent.ONPLAYERHURT;
      str = str.substring("(OnPlayerHurt) ".length());
    }
    if(str.equals("Ethereal")){
      whenPlayed = PlayEvent.ONTURNEND;
    }

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
  public PlayEvent whenPlayed(){ return whenPlayed; }

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