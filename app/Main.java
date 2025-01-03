package app;

import java.util.*;

/**
 * Slay the Spire in the Console - Java                             <p></p>
 * 5/10/22 - 5/25/22 (Alpha release 0.1.0 -- APCSA final)           <p></p>
 * 2/13/23 - 8/28/23                                                <p></p>
 * 12/22/23 - 1/13/24                                               <p></p>
 * 5/22/24 - 7/5/24                                                 <p></p>
 * 8/26/24 - 8/27/24 (Beta release 0.2.0)
 * 
 * @author Zachary Heintzleman
 * @see Run
 * @see README.md
 * @see todo.txt
 */
class Main {
  public static Scanner scan = new Scanner(System.in);

  public static void main(String[] args){
    Str.println(Colors.clearScreen + "Loading...");

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
      
      Str.println(Colors.dexGreen + "Dex Green");
      Str.println(Colors.louseGreen + "Louse Green");
      Str.println(Colors.fatGGreen + "FG Green");
      Str.println(Colors.upgradeGreen + "Upgrade Green");
      
      Str.println(Colors.wizardPurple + "Wizard Purple ");
      Str.println(Colors.sneakyGPurple + "Sneaky G Purple ");
      Str.println(Colors.darkEmbracePurple + "Dark Embrace Purple ");
      Str.println(Colors.purple + "Purple ");
    } else {
      Str.println(Colors.clearScreen);
    }
    
    Str.println(App.TITLE);
    Str.println("\n" + App.INSTRUCTIONS + "Press " + Colors.magenta + "Enter" + Colors.reset + " to continue\n");
  }
}
