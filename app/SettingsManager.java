package app;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/** Singleton instance that stores and provides basic utility functions for settings.
 * A from-scratch rewrite of the previous system, which was a somewhat ugly mess I had copy-pasted
 * from Stack Overflow when I didn't understand anything about file IO.
 * 
 * @see App App.DEFAULT_SCREEN_X
 * @see Run Run.xSettings.
 */
public class SettingsManager {
  private final Path path;
  public String name;
  public int screenWidth;
  public int screenHeight;
  public boolean debug;
  public boolean cheats;
  public boolean includeANSI;
  // On adding more settings, update the below functions & the xSettings methods in Run.java.
  
  /** Singleton SettingsManager Instance */
  public static final SettingsManager sm = new SettingsManager(App.SETTINGS_PATH);

  private SettingsManager(String pathName){
    if(sm != null){
      throw new IllegalStateException("Instantiating second SettingsManager object.");
    }

    path = Paths.get(pathName);
    load();
  }

  /** Rewrites the App.SETTINGS_PATH file to contain the current settings. */
  public void save(){
    try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path.toFile()))){
      os.writeObject(name);
      os.writeInt(screenWidth);
      os.writeInt(screenHeight);
      os.writeBoolean(debug);
      os.writeBoolean(cheats);
      os.writeBoolean(includeANSI);
    } catch(IOException e){
      System.out.println("Error saving settings data. Press enter to continue anyway.");
      e.printStackTrace();
      Main.scan.nextLine();
    }
  }

  /** Reads settings from App.SETTINGS_PATH into the singleton instance's public variables. */
  private void load(){
    try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(path.toFile()))){
      name = (String) is.readObject();
      screenWidth = is.readInt();
      screenHeight = is.readInt();
      debug = is.readBoolean();
      cheats = is.readBoolean();
      includeANSI = is.readBoolean();
    } catch(IOException | ClassNotFoundException e){
      System.out.println("Error loading settings data.");
      System.out.println("Press enter to reset to default settings." + 
                         " Or type \"error\" to see the error message.");
      while(Main.scan.nextLine().equalsIgnoreCase("error")){
        e.printStackTrace();
        System.out.println("Press enter to reset to default settings." + 
                           " Or type \"error\" to see the error message.");
      }
      resetToDefaults();
    }
  }

  /** Resets the settings to their default values, and resets the file accordingly. */
  public void resetToDefaults(){
    name = "Default";
    screenWidth = App.DEFAULT_SCREEN_WIDTH;
    screenHeight = App.DEFAULT_SCREEN_HEIGHT;
    debug = false;
    cheats = true;
    includeANSI = true;
    save();
  }
}
