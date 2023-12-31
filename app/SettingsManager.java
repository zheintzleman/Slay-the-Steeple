package app;
import java.io.*;

public class SettingsManager{
  public String pathname;
  public boolean debug;
  public String name;
  public int screenWidth;
  public int screenHeight;
  public boolean cheats;

  public SettingsManager(String pathname){
    this.pathname = pathname;

    try {
  		FileInputStream fi = new FileInputStream(new File(pathname));
      ObjectInputStream oi = new ObjectInputStream(fi);
      //To reset to default settings:
      // debug = true;
      // name = "Default";
      // screenWidth = 199;
      // screenHeight = 50;
      // cheats = true;
      // save();

      // Read objects
      debug = (Boolean) oi.readObject(); //Capitalize Boolean?
      name = (String) oi.readObject();
      screenWidth = (Integer) oi.readObject();
      screenHeight = (Integer) oi.readObject();
      cheats = (Boolean) oi.readObject();
      if(screenHeight <= 10){
        screenHeight = 50;
      }
      if(screenWidth <= 10){
        screenWidth = 199;
      }

      oi.close();
      fi.close();
      
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
      debug = true;
      name = "Default";
      screenWidth = 199;
      screenHeight = 50;
      cheats = true;
      save();
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
  }

  public void save(){
    try{
      FileOutputStream f = new FileOutputStream(new File(pathname));
      ObjectOutputStream o = new ObjectOutputStream(f);

      // Write objects to file
      o.writeObject((Boolean) debug); //Capitalize Boolean?
      o.writeObject(name);
      o.writeObject((Integer) screenWidth);
      o.writeObject((Integer) screenHeight);
      o.writeObject((Boolean) cheats);
    
      o.close();
      f.close();
    } catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
    }
  }

}