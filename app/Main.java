package app;
//Slay the Spire in the Console - in Java
//Zachary Heintzleman (solo)
//5/10/22 - 5/25/22
//2/13/23 - 8/28/23

import java.util.*;

class Main {
  public static Scanner scan = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("\033[H\033[2JLoading...");
    App game = new App();
    game.run();

    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
  }
}
