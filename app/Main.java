package app;
//Slay the Spire in the Console - Java
//Zachary Heintzleman (solo)
//5/10/22 - 5/25/22
//2/13/23 - 8/28/23
//12/22/23 - 1/13/24
//5/22/24 - (...)

import java.io.*;
import java.nio.file.*;
import java.util.*;

class Main {
  public static Scanner scan = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("\033[H\033[2JLoading...");

    Path path = Paths.get("testfilename");
    // Card c;
    try{
      ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path.toFile()));
      // if(!path.toFile().exists())
      //   Files.createFile(path);
      // os.writeObject("Hello World.");
      os.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
    // try {
    //   ObjectInputStream is = new ObjectInputStream(new FileInputStream(path.toFile()));
    //   System.out.println(is.readObject());
    //   is.close();
    // } catch(Exception e) {
    //   e.printStackTrace();
    // }

    App.a.run();

    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
  }
}
