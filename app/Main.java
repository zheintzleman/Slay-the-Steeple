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
    System.out.println("\033[H\033[2JLoading...");

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
    if(!SettingsManager.sm.debug){
      Str.println(Colors.clearScreen);
    } else {
      Str.println("CARDS: " + App.CARDS.values());
    }
    
    Str.println(App.TITLE);
    Str.println("\n" + App.INSTRUCTIONS + "Press " + Colors.magenta + "Enter" + Colors.reset + " to continue\n");
  }
}
