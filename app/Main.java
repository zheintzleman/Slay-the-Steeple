package app;
//Slay the Spire in the Console - Java
//Zachary Heintzleman (solo)
//5/10/22 - 5/25/22
//2/13/23 - 8/28/23
//12/22/23 - (...)

import java.util.*;

class Main {
  public static Scanner scan = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("\033[H\033[2JLoading...");

    Entity E = new Player("Test", 1, 1, new String[] {"1","2","3"}, new Combat(new Run()));
    E.die();

    App game = new App();
    game.run();

    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
  }
}
