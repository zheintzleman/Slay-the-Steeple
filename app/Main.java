package app;

import java.util.*;

import util.Colors;
import util.Str;

/**
 * Slay the Spire in the Console - Java                             <p></p>
 * 5/10/22 - 5/25/22 (Alpha release 0.1.0 -- APCSA final)           <p></p>
 * 2/13/23 - 8/28/23                                                <p></p>
 * (Throughout Breaks...)                                           <p></p>
 * - 8/27/24 (Beta release 0.2.0)                                   <p></p>
 * - 1/14/25 (Release 1.0.0 !)
 * 
 * @author Zachary Heintzleman
 * @see Run
 * @see README.md
 * @see todo.txt
 */
class Main {
  public static Scanner scan = new Scanner(System.in);

  public static void main(String[] args){
    Str.println("\nLoading...");

    System.out.print("If blank, assertions disabled: ");
    assert testAssert();
    System.out.println();

    printTitleScreen();
    scan.nextLine();
    Run.r.play();

    //Probably bring back when/if making a home screen, etc.:
    /*
    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
    */

    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
  }

  private static void printTitleScreen(){
    if(SettingsManager.sm.debug){
      Str.println("CARDS: " + App.CARDS);
      
      Str.println(Colors.hpBarRed + "HP Bar Red ");
      Str.println(Colors.ICRed + "IC Red ");
      Str.println(Colors.vulnRed + "Vuln Red ");
      Str.println(Colors.slaverRed + "Slaver Red ");
      Str.println(Colors.atkIntArtRed + "AtkInt Red ");
      Str.println(Colors.energyCostRed + "Energy Cost Red ");
      Str.println(Colors.demonFormRed + "Demon Form Red ");
      
      Str.println(Colors.dexGreen + "Dex Green ");
      Str.println(Colors.louseGreen + "Louse Green ");
      Str.println(Colors.fatGGreen + "FG Green ");
      Str.println(Colors.upgradeGreen + "Upgrade Green ");
      
      Str.println(Colors.wizardPurple + "Wizard Purple ");
      Str.println(Colors.sneakyGPurple + "Sneaky G Purple ");
      Str.println(Colors.darkEmbracePurple + "Dark Embrace Purple ");
      Str.println(Colors.purple + "Purple ");
      
      Str.println(Colors.descNumBlue + "descNumBlue ");
      Str.println(Colors.doubleTapBlue + "doubleTapBlue ");
      Str.println(Colors.lightBlue + "lightBlue ");
      Str.println(Colors.blockBlue + "blockBlue ");
      // Exists more blues
    } else {
      Str.println(Colors.clearScreen);
    }
    
    Str.println(App.TITLE);
    Str.println("\n" + App.INSTRUCTIONS + "Press " + Colors.magenta + "Enter" + Colors.reset + " to continue\n");
  }

  private static boolean testAssert(){
    // Note: Can change whether assertions are enabled in launch.json.
    System.out.println("Assertions Enabled");
    return true;
  }
}
