package app;
import java.util.*;

public class Statuses{
  //Name, image, isDecreasing, hasStrength, description
  public static final Status Vulnerable = new Status("Vulnerable", Colors.darkRed + "V", true, true, "Takes 50% more damage from attacks for the next <str> turn(s).");
  public static final Status Weak = new Status("Weak", Colors.lightGreen + "W", true, true, "Deals 25% less attack damage for the next <str> turn(s).");
  public static final Status Frail = new Status("Frail", Colors.lightBlue + "F", true, true, "Block gained from cards is reduced by 25% for the next <str> turn(s).");
  public static final Status Strength = new Status("Strength", Colors.hpBarRed + "S", false, true, "Increases attack damage by <str> (per hit)");
  public static final Status StrengthDown = new Status("Strength Down", Colors.lightYellow + "s", false, true, "At the end of your turn, lose <str> Strength.");
  public static final Status Dexterity = new Status("Dexterity", Colors.dexGreen + "D", false, true, "Increases block gained from cards by <str>.");
  public static final Status DexterityDown = new Status("Dexterity Down", Colors.lightYellow + "d", false, true, "At the end of your turn, lose <str> Dexterity.");
  public static final Status CurlUp = new Status("Curl Up", Colors.lightBlue + "C", false, true, "Gains <str> block upon first receiving attack damage."); //TODO: See how this interacts w/, eg, Twin Strike
  public static final Status Ritual = new Status("Ritual", Colors.lightBlue + "R", false, true, "Gains <str> Strength at the end of each turn.");
  public static final Status Angry = new Status("Angry", Colors.lightYellow + "A", false, true, "Gains <str> Strength when this receives attack damage.");
  public static final Status Split = new Status("Split", Colors.lightGreen + "S", false, false, "When at half HP or below, this splits into two smaller slimes with its current HP."); //TODO: Make display the amount of HP needed for splitting (or just add max HP to the HP bar)
  public static final Status Entangled = new Status("Entangled", Colors.white + "E", true, false, "You may not play any attacks this turn");
  public static final Status SporeCloud = new Status("Spore Cloud", Colors.lightYellow + "S", false, true, "On death, applies <str> Vulnerable to the player.");
  public static final Status Thievery = new Status("Thievery", Colors.lightYellow + "T", false, true, "<str> Gold is stolen with every attack. Total Gold stolen is returned if the enemy is killed.");
  //Add new statuses in list below, too

  public static ArrayList<Status> allStatuses;


  public static void loadStatuses(){
    allStatuses = new ArrayList<Status>();
    allStatuses.add(Vulnerable);
    allStatuses.add(Weak);
    allStatuses.add(Frail);
    allStatuses.add(Strength);
    allStatuses.add(StrengthDown);
    allStatuses.add(Dexterity);
    allStatuses.add(DexterityDown);
    allStatuses.add(CurlUp);
    allStatuses.add(Ritual);
    allStatuses.add(Angry);
    allStatuses.add(Split);
    allStatuses.add(Entangled);
    allStatuses.add(SporeCloud);
    allStatuses.add(Thievery);
  }

  public static Status getStatus(String statusName){
    for(Status s : allStatuses){
      if(s.getName().equals(statusName)){
        return s;
      }
    }
    return null;
  }
}