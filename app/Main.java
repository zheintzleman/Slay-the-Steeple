package app;

import java.util.*;

/**
 * Slay the Spire in the Console - Java
 * Zachary Heintzleman (solo)
 * 5/10/22 - 5/25/22
 * 2/13/23 - 8/28/23
 * 12/22/23 - 1/13/24
 * 5/22/24 - (...)
 */
class Main {
  public static Scanner scan = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("\033[H\033[2JLoading...");

    run();

    //Probably bring back when/if making a home screen or smth:
    /*
    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
    */

    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
  }

  private static void run(){
    //Title Screen
    if(!SettingsManager.sm.debug){
      Str.println(Colors.clearScreen);
    } else {
      Str.println("CARDS: " + App.CARDS.values());
    }
    
    Str.println(App.TITLE);
    Str.println("\n" + App.INSTRUCTIONS + "Press " + Colors.magenta + "Enter" + Colors.reset + " to continue\n");
    
    Main.scan.nextLine();
    
    Run.r.play();
  }
}
