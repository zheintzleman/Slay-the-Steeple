package app;

import java.util.ArrayList;
import java.util.List;

import util.Colors;
import util.Str;

/** The effects/powers that show up under the player's & enemies' name and HP bar.
 * E.g. Strength, Weakened, Artifact, etc.
 * Contains a List of StatusEffects to be activated during the appropriate event. Or, for some
 * statuses, just if-blocks in the respective method (usually in Combat.java) instead of using a
 * creating a whole EventManager.Event for it.
 * 
 * @see StatusEffect
 * @see App App.loadStatuses()
 * @see Entity
 */
public class Status {
  private String name, description, image;
  private int strength;
  private boolean decreasing, hasStrength;
  final private List<StatusEffect> effects;

  public Status(){
    name = "<Status>";
    strength = 1;
    image = "▓";
    decreasing = false;
    hasStrength = true;
    description = "<desc>";
    effects = new ArrayList<>();
  }
  public Status(Status s){
    name = s.getName();
    strength = s.getStrength();
    image = s.getImage();
    decreasing = s.isDecreasing();
    hasStrength = s.hasStrength();
    description = s.getDescription();
    // For each effect: create a new (otherwise identical) StatusEffect that points to this status.
    effects = s.getEffects().stream()
                .map((StatusEffect eff) -> new StatusEffect(eff, this))
                .toList();
  }
  /** Initializes the described status, with default strength of 1. */
  public Status(String name){
    this(getStatus(name));
    strength = 1;
  }
  /** Initializes the described status with the given strength. */
  public Status(String name, int strength){
    this(getStatus(name));
    this.strength = strength;
  }
  //For App.STATUSES:
  public Status(String name, String image, boolean decreasing, boolean hasStrength, String desc){
    this.name = name;
    this.strength = 0;
    this.image = image;
    this.decreasing = decreasing;
    this.hasStrength = hasStrength;
    description = desc;
    effects = new ArrayList<>();
  }
  //For App.STATUSES:
  public Status(String name, String image, boolean decreasing, boolean hasStrength, String desc, List<String> effects){
    this(name, image, decreasing, hasStrength, desc);
    for(String eff : effects){
      // If ends w/ <str>, delete it and mark that 
      if(eff.endsWith(" <str>")){
        this.effects.add(new StatusEffect(eff, this, true));
        // TODO: Stretch goal: make plurals singular if <str> == 1 (e.g.in Dark Embrace)
      } else {
        this.effects.add(new StatusEffect(eff, this));
      }
    }
  }

  //Getters and Setters
  public String getName(){ return name; }
  public void setName(String newName){ name = newName; }
  public int getStrength(){ return strength; }
  public String getImage(){ return image; }
  public void setImage(String newImage){ image = newImage; }
  public boolean isDecreasing(){ return decreasing; }
  public void setDecreasing(boolean newDec){ decreasing = newDec; }
  public String getDescription(){ return description; }
  public boolean hasStrength(){ return hasStrength; }
  public void setHasStrength(boolean newHS){ hasStrength = newHS; }
  public List<StatusEffect> getEffects(){ return effects; }
  
  public void setStrength(int newStrength){
    if(hasStrength){
      strength = newStrength;
      return;
    }
    strength = newStrength > 0 ? 1 : 0;
  }
  //setStrength deals with the (!hasStrength) behavior
  public void addStrength(int extraStr){
    setStrength(strength + extraStr);
  }
  public void subtractStrength(int lessStr){
    setStrength(strength - lessStr);
  }
  
  /** Constructs and returns a String of the correctly colored and formatted status description
  */
  public String getDescriptionFormatted(){
    return getDescriptionFormatted(strength);
  }
  /** Constructs and returns a String of the correctly colored and formatted status description
  */
  public String getDescriptionFormatted(int str){
    String desc = description;
    boolean hasStr = desc.indexOf("<str>") != -1;
    while(hasStr){
      String start = desc.substring(0, desc.indexOf("<str>"));
      String middle = Colors.descNumBlue + str + Colors.reset;
      String end = desc.substring(desc.indexOf("<str>")+5);
      desc = start + middle + end;
      hasStr = desc.indexOf("<str>") != -1;
    }
    return desc;
  }
  
  /** Returns the (Usually 1 or 2 char long) image for this specific status effect.
  */
  public String getDisplay(){
    String str = image;
    if(strength != 1 || hasStrength){
      str += Colors.lightGray + strength;
    }
    return str + Colors.reset;
  }
  
  /** Returns the status with the entered name from the set of available statuses.
   * @Postcondition - Returns a status in App.STATUSES -- doesn't return null
  */
  private static Status getStatus(String name){
    Status status = App.STATUSSET.get(name);

    //Exception case:
    if(status == null){
      System.out.println("Status \"" + name + "\" not found. Status list:");
      for(Status s : App.STATUSSET.values()){
        Str.println("S: " + s.getName());
      }
      throw new IllegalArgumentException("Status \"" + name + "\" not in App.STATUSES.");
    }

    return status;
  }

  /** Initializes a copy of this status. Same as `new Status(this)`. */
  public Status clone(){
    return new Status(this);
  }
  public String toString(){
    return getDisplay() + " - " + getName() + ": " + getDescriptionFormatted();
  }
}