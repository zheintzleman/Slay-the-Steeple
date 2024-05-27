package app;
import java.util.*;

public class Run{
  private String[] screen;
  //TODO: Put these into App.java?
  //Odd number (v) (default w=199, h=50 -- change defaults in App.java.)
  public static final int SCREENWIDTH = App.settingsManager.screenWidth;
  public static final int SCREENHEIGHT = App.settingsManager.screenHeight;
  private int hp, maxHP;
  private int gold;
  private ArrayList<Card> deck;
  private EventManager eventManager;
  
  public Run(){
    hp = maxHP = 80;
    gold = 99;
    deck = new ArrayList<Card>();
    eventManager = new EventManager(this);
    App.ASSERT(new Card("Strike") != null);
    deck.add(new Card("Battle Trance"));
    deck.add(new Card("Battle Trance"));
    deck.add(new Card("Battle Trance"));
    deck.add(new Card("Battle Trance"));
    deck.add(new Card("Battle Trance"));
    deck.add(new Card("Battle Trance"));
    deck.add(new Card("Battle Trance"));
    deck.add(new Card("Armaments"));
    deck.add(new Card("Blood for Blood"));
    deck.add(new Card("Blood for Blood"));
    deck.add(new Card("Blood for Blood"));
    deck.add(new Card("Blood for Blood"));
    deck.add(new Card("Armaments"));
    deck.add(new Card("Armaments"));
    deck.add(new Card("Heavy Blade"));
    deck.add(new Card("Flex"));
    deck.add(new Card("Flex"));
    // TODO: Display deck in alphabetical order or smth?
    // Although this does show it in order obtained, actually.
    if(App.settingsManager.debug){
      System.out.println("W: " + SCREENWIDTH);
      System.out.println("H: " + SCREENHEIGHT);
      System.out.println("W: " + App.settingsManager.screenWidth);
      System.out.println("H: " + App.settingsManager.screenHeight);

      Str.println(Colors.hpRed + "HP Red ");
      Str.println(Colors.hpBarRed + "HP Bar Red ");
      Str.println(Colors.ICRed + "IC Red ");
      Str.println(Colors.darkRed + "Dark Red ");
      Str.println(Colors.slaverRed + "Slaver Red ");
      Str.println(Colors.atkIntArtRed + "AtkInt Red ");
      Str.println(Colors.energyCostRed + "Energy Cost Red ");
      
      Str.println(Colors.dexGreen + "Dex Green");
      Str.println(Colors.louseGreen + "Louse Green");
      Str.println(Colors.fatGGreen + "FG Green");
      Str.println(Colors.upgradeGreen + "Upgrade Green");

      // for(Card c : Card.availableCards()){
      //   Str.println(c.getName() + ": " + c.generateDescription() + c.getDescription()); //Doesn't work for flex
      //   App.ASSERT(c.getDescription().equals(c.generateDescription()));
      // }

    }
    constructScreen();
  }

  //Getters and Setters
  public void setScreen(String[] newScreen){ screen = newScreen; }
  public String[] getScreen(){ return screen; }
  public void setHP(int newHP){ hp = newHP; }
  public int getHP(){ return hp; }
  public void setMaxtHP(int newMaxHP){ maxHP = newMaxHP; }
  public int getMaxHP(){ return maxHP; }
  public int getGold(){ return gold; }
  public ArrayList<Card> getDeck(){ return deck; }
  public EventManager getEventManager(){ return eventManager; }

  /**Plays the run
  */
  public void play(){
    while(true){ //Runs until hp <= 0, which has a break statement below
      Combat c;
      double rn = Math.random();
      double chance = 1.0/14;
      if(rn < chance){
      c = new Combat(this, "Cultist");
      }else if(rn < 2*chance){
      c = new Combat(this, "Jaw Worm");
      }else if(rn < 3*chance){
      c = new Combat(this, "Two Louses");
      }else if(rn < 4*chance){
      c = new Combat(this, "Small and Med Slime");
      }else if(rn < 5*chance){
      c = new Combat(this, "Gremlin Gang");
      }else if(rn < 6*chance){
      c = new Combat(this, "Large Slime");
      }else if(rn < 7*chance){
      c = new Combat(this, "Lots of Slimes");
      }else if(rn < 8*chance){
      c = new Combat(this, "Blue Slaver");
      }else if(rn < 9*chance){
      c = new Combat(this, "Red Slaver");
      }else if(rn < 10*chance){
      c = new Combat(this, "Three Louses");
      }else if(rn < 11*chance){
      c = new Combat(this, "Two Fungi Beasts");
      }else if(rn < 12*chance){
      c = new Combat(this, "Exordium Thugs");
      }else if(rn < 13*chance){
      c = new Combat(this, "Exordium Wildlife");
      }else{
      c = new Combat(this, "Looter");
      }
      eventManager.setCombat(c);
      EventManager E = c.getEventManager();
      App.ASSERT(c.getEventManager() == eventManager);
      int goldStolen = c.runCombat();
      if(hp <= 0){
        break; //Death mechanic (temporary?) //global bool var for death?
        //todo: make it (prob in Combat) so that enemies don't show their next intent when you die?)
      }
      combatRewards(goldStolen); //todo: move elsewhere if relevant?
      Main.scan.nextLine();
    }
    Str.println(Colors.clearScreen);
    System.out.println(App.GAME_OVER);
    Str.println("You got " + Colors.gold + gold + Colors.reset + " gold");
  }

  private enum RewardType{
    GOLD,
    CARD,
    POTION
  }

  private class CombatReward{
    String img;
    RewardType type;
    int data; //Stores data relevant to the reward; e.g. Gold amount, etc.
    private String grayBar37 = Colors.fillColor(Str.repeatChar('█', 37), Colors.gray);

    private CombatReward(String text, RewardType type){
      this.type = type;

      String endOfMiddleRow = Colors.fillColor(Str.repeatChar('█', 37 - (Str.lengthIgnoringEscSeqs(text)+1)), Colors.gray);
      String middleRow = Colors.gray + "█" + Colors.whiteOnGray + text + Colors.reset + endOfMiddleRow;
      img = grayBar37 + "\n" + middleRow + "\n" + grayBar37 + "\n";
    }
    private CombatReward(String text, RewardType type, int data){
      this.type = type;
      this.data = data;

      String endOfMiddleRow = Colors.fillColor(Str.repeatChar('█', 37 - (Str.lengthIgnoringEscSeqs(text)+1)), Colors.gray);
      String middleRow = Colors.gray + "█" + Colors.whiteOnGray + text + Colors.reset + endOfMiddleRow;
      img = grayBar37 + "\n" + middleRow + "\n" + grayBar37 + "\n";
    }

    private String getImg(){ return img; }

    private void execute(){
      switch (type) {
        case GOLD:
          addGold(data);
          reloadScreenHeader();
          break;
        case CARD:
          //Make a whole card reward method and popup and stuff.
          //(And impl. cards and everything)(Big Task!)
          break;
        //TODO: Add potions here obv.
        default:
          break;
      }
    }
  }

  /**Gives the popup with rewards for a normal combat
  */
  void combatRewards(int goldStolenBack){
    //Constants:
    String header = Colors.lightBrown + "Rewards!" + Colors.reset + "\n\n";
    ArrayList<CombatReward> rewards = new ArrayList<CombatReward>();

    //Gold stolen back
    if(goldStolenBack > 0){
      String goldBackText = goldStolenBack + " Gold (Stolen Back)";
      CombatReward goldBackReward = new CombatReward(goldBackText, RewardType.GOLD, goldStolenBack);
      rewards.add(goldBackReward);
    }

    //Gold Reward
    int goldAmt = (int)(Math.random()*11) + 10; //Will have to change this for later w/ elites probably.
    String goldText = goldAmt + " Gold"; //TODO: Add a symbol here, too.
    CombatReward goldReward = new CombatReward(goldText, RewardType.GOLD, goldAmt);
    rewards.add(goldReward);

    //Card Reward
    String cardRewardText = Colors.fillColor("░█", Colors.grayOnWhite) + Colors.whiteOnGray + "Add a card to your deck"; //TODO: make this display properly
    CombatReward cardReward = new CombatReward(cardRewardText, RewardType.CARD);
    rewards.add(cardReward);
    
    //todo: add potions
    //(Big thing too!)

    hp += 6; //todo move to some endOfCombat function? Ie that takes into account relics
    if(hp > maxHP){ hp = maxHP; }

    //Add any additional rewards here
    int selectedIndex = 0;

    while(rewards.size() > 0){
      String textToPopup = header; //Used to say '+ Str.concatArrayListWNL(rewards)'
      //Construct the popup text:
      for(int i=0; i < rewards.size(); i++){
        CombatReward r = rewards.get(i);
        //Change its color
        String img = r.getImg();
        if(i == selectedIndex){
          //Changing gray to blue
          while(img.indexOf(Colors.gray) != -1) {
            int nextIndex = img.indexOf(Colors.gray);
            img = img.substring(0, nextIndex) + Colors.blockBlue + img.substring(nextIndex + Colors.gray.length()); //TODO: Check colors; currently using block blue, but could change it (eg to light blue.)
          }
          //Changing gray on white to blue on white
          while(img.indexOf(Colors.grayOnWhite) != -1) {
            int nextIndex = img.indexOf(Colors.grayOnWhite);
            img = img.substring(0, nextIndex) + Colors.blockBlueOnWhite + img.substring(nextIndex + Colors.grayOnWhite.length());
          }
          //Changing white on gray to white on blue
          while(img.indexOf(Colors.whiteOnGray) != -1) {
            int nextIndex = img.indexOf(Colors.whiteOnGray);
            img = img.substring(0, nextIndex) + Colors.whiteOnBlockBlue + img.substring(nextIndex + Colors.whiteOnGray.length());
          }
        }
        //Concatenate its image to the popup text
        textToPopup += img + "\n";
      }
      
      //Display the popup:
      //Effectively 'popup(textToPopup);', except I can actually look at what the input is:
      int startCol = App.settingsManager.screenWidth/2 - App.POPUP_WIDTH/2; // == 78
      String[] screenWithAddition = Str.addStringArraysSkipEscSequences(screen, 6, startCol, Str.makeTextBox(textToPopup, App.POPUP_HEIGHT , App.POPUP_WIDTH));
      display(screenWithAddition); //Same as calling displayScreenWithAddition with the above params, but can pass this screen into input below (v)
      //TODO: Print info message
      Str.println("<Just press enter to collect reward; navigate with q (up) and z (down)>");

      //Take in commands:
      String input = input(screenWithAddition);
      switch(input.toLowerCase()){
        case "0":
        case "q":
          if(selectedIndex > 0) selectedIndex--;
          break;
        case "1":
        case "z":
          if(selectedIndex < rewards.size()-1) selectedIndex++;
          break;
        case "":
          rewards.get(selectedIndex).execute();
          rewards.remove(selectedIndex);
          if(selectedIndex >= rewards.size()){ selectedIndex = rewards.size()-1; }
          break;
        default:
          break;
      }
      // displayScreenWithAddition(Str.makeTextBox(textToPopup, 30 , 43), 6, 78);
    }
  }

  /**Reduces the player's gold value by the entered amount.
   * @return int - the amount of gold actually taken away.
  */
  public int loseGold(int amount){
    if(amount > gold){
      amount = gold;
    }
    gold -= amount;
    return amount;
  }
  public void addGold(int amount){
    gold += amount;
  }

  /**Displays the screen. Same as calling display(screen);
  */
  public void display(){
    display(screen);
  }
  
  /**Displays the entered String array.
  */
  public static void display(String[] arr){
    if(!App.settingsManager.debug)
      System.out.println(Colors.clearScreen);

    for(String str : arr){
      Str.println(str);
    }
  }

  /**Displays the screen with an addition on top of it.
  */
  public void displayScreenWithAddition(String[] newArray, int topRow, int startCol){
    String[] combinedScreen = Str.addStringArraysSkipEscSequences(screen, topRow, startCol, newArray);
    display(combinedScreen);
  }
  
  /**Constructs the screen String[] and adds/sets basic run-wide values to the screen such as hp and the deck
  */
  private void constructScreen(){
    screen = new String[SCREENHEIGHT];
    String lineOfSpaces = Str.repeatChar(' ', SCREENWIDTH);

    //Fills the screen with 3 lines of gray Blocks then the rest (27) lines of spaces
    reloadScreenHeader();
    Arrays.fill(screen, 5, SCREENHEIGHT, Colors.reset + lineOfSpaces + Colors.reset);
  }

  /**Sets basic run-wide values such as hp and the deck. Doesn't construct a new array.
  */
  public void reloadScreenHeader(){
    String lineOfBlocks = Str.repeatChar('█', SCREENWIDTH);

    //Fills the screen with 3 lines of gray Blocks then the rest (27) lines of spaces
    Arrays.fill(screen, 0, 5, Colors.headerBrown + lineOfBlocks + Colors.reset);
    
    String hpText =  "HP: " + this.hp;
    addToScreen(2, SCREENWIDTH/2 - Str.lengthIgnoringEscSeqs(hpText) -2, hpText, Colors.hpRedOnHeaderBrown, Colors.reset + Colors.headerBrown);
    addToScreen(2, SCREENWIDTH/2 +1, "Gold: " + this.gold, Colors.goldOnHeaderBrown, Colors.reset + Colors.headerBrown);
    
    
    //Deck (top right)
    String[] deckDisplay = Combat.square(3, 5, deck.size(), Colors.deckBrown, Colors.whiteOnDeckBrown);
    addToScreen(1, SCREENWIDTH-16, deckDisplay , Colors.reset + Colors.deckBrown, Colors.reset + Colors.headerBrown);
    addToScreen(0, SCREENWIDTH-16, "Deck-D", Colors.magentaBoldOnHeaderBrown, Colors.headerBrown);

    //Settings Gear
    //todo: Change the blue color?
    String[] gearDisplay = {"▀▄█▄▀", "██" + Colors.magentaBoldOnBlockBlue + "S" + Colors.blockBlueOnHeaderBrown + "██", "▄▀█▀▄"};
    addToScreen(1, SCREENWIDTH - 7, gearDisplay, Colors.blockBlueOnHeaderBrown, Colors.reset + Colors.headerBrown);
  }

  /**Resets the screen String[] and adds/sets basic run-wide values such as hp and the deck. Doesn't construct a new array.
  */
  public void reloadScreen(){
    reloadScreenHeader();

    String lineOfSpaces = Str.repeatChar(' ', SCREENWIDTH);
    Arrays.fill(screen, 5, SCREENHEIGHT, Colors.reset + lineOfSpaces);
  }

  /**Adds the specified string to the screen array at the row and column
  */
  public void addToScreen(int row, int startCol, String str){
    screen[row] = Str.addStringsSkipEscSequences(screen[row], startCol, str); 
  }
  /**Adds the specified string to the screen array at the specified rown and column. Starts with color and ends with colorReset.
  */
  public void addToScreen(int row, int startCol, String str, String color, String colorReset){
    screen[row] = Str.addStringsSkipEscSequences(screen[row], startCol, str, color, colorReset);
  }

  /**Adds the specified string[] to the screen array at the specified rown and column.
  */
  public void addToScreen(int topRow, int startCol, String[] strings){
    for(int r=topRow; r<topRow+strings.length; r++){
      screen[r] = Str.addStringsSkipEscSequences(screen[r], startCol, strings[r-topRow]);
    }
  }
  /**Adds the specified string to the screen array at the specified rown and column. Starts with color and ends with colorReset.
  */
  public void addToScreen(int topRow, int startCol, String[] strings, String color, String colorReset){
    for(int r=topRow; r<topRow+strings.length; r++){
      screen[r] = Str.addStringsSkipEscSequences(screen[r], startCol, strings[r-topRow], color, colorReset);
    }
  }

  /**Gets user input. If input is one of the registered codes, follows the code and repeats until any other input is entered.
   * @return The first non-code value inputted by the user
  */
  public String input(){
    return input(screen);
  }

  /**Gets user input. If input is one of the registered codes, follows the code and repeats until any other input is entered.
   * @return The first non-code value inputted by the user
  */
  public String input(String[] prevScreen){
    while(true){
      String input = Main.scan.nextLine();
      switch(input.toLowerCase()){
        case "d":
        case "deck":
          String str = "";
          for(Card c : deck){
            str += c.toString() + "\n";
          }
          popup("Your whole deck:\n" + str, prevScreen);
          break;
        case "i":
        case "h":
        case "inst":
        case "help":
        case "instructions":
          popup(App.INSTRUCTIONS, prevScreen);
          break;
        case "s":
        case "settings":
        case "esc":
        case "escape":
          openSettings();
          display(prevScreen);
          break; //TODO: Make some system so that you can go back to the previous screen after opening a menu like this (eg input(String[] S) or something)
        default:
          return input;
      }
    }
  }
  
  public void openSettings(){
    //todo: move this all to some function in App?
    //If SCREENHEIGHT*4/5 will overflow, replaces height of popup with SCREENHEIGHT - 6:
    int popupHeight = (SCREENHEIGHT*4/5 + 6 <= SCREENHEIGHT) ? SCREENHEIGHT*4/5 : SCREENHEIGHT - 6;
    int popupWidth = SCREENWIDTH*22/25;
    //todo: add color to true/false?
    //Displays a popup of the settings.
    // String[] arr = Str.makeTextBox(settingsText, popupHeight, popupWidth);
    // displayScreenWithAddition(arr, 5, SCREENWIDTH*3/50);
    // for(String s : arr){
    //   Str.println(s);
    // }

    // popup(settingsText, popupHeight, popupWidth, 5, SCREENWIDTH*3/50); //todo: finish this popup //Probably make it into its own function (in main? some gamemanager or overall function class or smth? A screen/interface class? that would probably be good.) (Plus remember that you were doing the displays & stuff for combat rewards before this)
    
    boolean exit = false;
    while(!exit){
      String settingsText = Colors.magenta + Str.repeatChar(' ', (popupWidth-4-9)/2) + "Settings:\n" + 
                            Str.repeatChar(' ', (popupWidth-4-11)/2) + "───────────\n" + 
                            "Settings saved to the device.\n" + 
                            "To change a setting, type the name of the setting and follow the given prompts.\n\n" + 
            Colors.magenta + "Name: " + Colors.reset + App.settingsManager.name + "\n" + 
            Colors.magenta + "Screen Width: " + Colors.reset + App.settingsManager.screenWidth + "\n" + 
            Colors.magenta + "Screen Height: " + Colors.reset + App.settingsManager.screenHeight + "\n" + 
            Colors.magenta + "Cheats: " + Colors.reset + App.settingsManager.cheats + "\n" + 
            Colors.magenta + "Debug Mode: " + Colors.reset + App.settingsManager.debug + "\n\n" + 
          Colors.basicBlue + Str.repeatStr("═", popupWidth - 6);
      
      displayScreenWithAddition(Str.makeTextBox(settingsText, popupHeight, popupWidth), 5, SCREENWIDTH*3/50);
      Str.print("<Press enter to exit, or type the name of a setting to change it>\n");
      String input = Main.scan.nextLine();
      switch (input.toLowerCase()) {
        case "name":
          Str.print("Enter the new name (Just press enter to cancel:)\n");
          String s = Main.scan.nextLine();
          if(!s.equals("")){
            App.settingsManager.name = s;
            App.settingsManager.save();
          }
          break;
        case "screen width":
        case "width":
          Str.println("Enter the new screen width, or enter 1-4 for default width options. (Just press enter to cancel:)");
          try{
            int width = Integer.parseInt(Main.scan.nextLine());
            if (1 <= width && width <= 4) {
              width = 155 + 22*width;
            }
            App.settingsManager.screenWidth = width;
            App.settingsManager.save();
          } catch (NumberFormatException E) {}
          break;
        case "screen height":
        case "height":
          Str.println("Enter the new screen height (Just press enter to cancel:)");
          try{
            int h = Integer.parseInt(Main.scan.nextLine()); //TODO: add a way to reset these to default?
            // if(h >= 10) //TODO: Eventually place actually reasonable restrictions on these (+width must be odd.) //TODO: also make these actually work. (currently mostly referring to the FINAL var. Can just tell user they have to restart the game to see changes here?)
            //   App.settingsManager.screenHeight = h;
            // else
            //   Str.println("New height must be at least 10.");
            App.settingsManager.save();
          } catch (NumberFormatException E) {}
          break;
        case "debug mode":
        case "debug":
          Str.println("Enter what to change debug to (Just press enter to cancel:)");
          try{
            App.settingsManager.debug = parseBoolInput();
            App.settingsManager.save();
          } catch (NumberFormatException E){}
          break;
        case "cheats":
        case "cheat":
          Str.println("Enter what to change debug to (Just press enter to cancel:)");
          try{
            App.settingsManager.cheats = parseBoolInput();
            App.settingsManager.save();
          } catch (NumberFormatException E){}
          break;
        case "":
          exit = true;
          break;
        default:
          break;
      }
    }
    // displayScreenWithAddition(Str.makeTextBox(settingsText, popupHeight, popupWidth), 5, SCREENWIDTH*3/50);
  }

  // TODO: Make all of these switch statements switch on "str.toLowerCase()" instead.

  private boolean parseBoolInput() throws NumberFormatException{
    String s = Main.scan.nextLine();
    switch (s.toLowerCase()) {
      case "true":
      case "1":
      case "y":
      case "on":
        return true;
      case "false":
      case "0":
      case "n":
      case "off":
        return false;
      default:
        throw new NumberFormatException();
    }
  }

  /**Displays the screen with a text box of the entered text.
   * Player presses enter to close it. Text wrapped with box of h=30, w=43.
   * @Precondition No spaces adjacent to new lines in text; 
   */
  public void popup(String text){
    popup(text, 30, 43, 6, 78);
  }
  /**Displays the screen with a text box of the entered text.
   * Player presses enter to close it. Text wrapped with box of h=30, w=43.
   * @Precondition No spaces adjacent to new lines in text; 
   */
  public void popup(String text, String[] prevScreen){
    popup(text, 30, 43, 6, 78, prevScreen);
  }
  /**Displays the screen with a text box of the entered text, with the specific width.
   * Player presses enter to close it. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in theText; 
   * @Precondition width <= SCREENWIDTH;
   */
  public void popup(String text, int width){
    App.ASSERT(width <= SCREENWIDTH);
    
    popup(text, 30, width, 6, (SCREENWIDTH-width)/2);
  }
  /**Displays the screen with a text box of the entered text, with the specific width.
   * Player presses enter to close it. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in theText; 
   * @Precondition width <= SCREENWIDTH;
   * @Precondition height <= SCREENHEIGHT;
   */
  public void popup(String text, int height, int width){
    App.ASSERT(width <= SCREENWIDTH);
    App.ASSERT(height <= SCREENHEIGHT);

    popup(text, height, width, (SCREENHEIGHT-height)/2, (SCREENWIDTH-width)/2);
  }
  /**Displays the screen with a text box of the entered text, with the specific width.
   * Player presses enter to close it. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in theText; 
   * @Precondition 5 <= height;
   * @Precondition 5 <= width;
   * @Precondition 0 <= startRow;
   * @Precondition 0 <= startCol;
   * @Precondition startRow + width <= SCREENWIDTH;
   * @Precondition startCol + height <= SCREENHEIGHT;
   * (Not 100% sure it works for 0; Not sure the exact minimim height/width.)
   */
  public void popup(String text, int height, int width, int startRow, int startCol){
    popup(text, height, width, startRow, startCol, screen);
  }
  /**Displays the screen with a text box of the entered text, with the specific width.
   * Player presses enter to close it and call display(prevScreen), returning their input
   * if it wasn't a q or z. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in theText; 
   * @Precondition 5 <= height;
   * @Precondition 5 <= width;
   * @Precondition 0 <= startRow;
   * @Precondition 0 <= startCol;
   * @Precondition startRow + width <= SCREENWIDTH;
   * @Precondition startCol + height <= SCREENHEIGHT;
   * (Not 100% sure it works for 0; Not sure the exact minimim height/width.)
   */
  public void popup(String text, int height, int width, int startRow, int startCol, String[] prevScreen){
    popupInput(text, "Press enter to exit this popup",
               height, width, startRow, startCol, prevScreen);
  }
  /**Displays the screen with a text box of the entered text.
   * Player presses gives input other than q/z to close it. Text wrapped with box of h=30, w=43.
   * @Precondition No spaces adjacent to new lines in text; 
   */
  public String popupInput(String text, String popupPrompt){
    return popupInput(text, popupPrompt, 30, 43, 6, 78, screen);
  }
  /**Displays the screen with a text box of the entered text, with the specific width.
   * Player presses enter to close it and call display(prevScreen), returning their input
   * if it wasn't a q or z. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in theText; 
   * @Precondition 5 <= height;
   * @Precondition 5 <= width;
   * @Precondition 0 <= startRow;
   * @Precondition 0 <= startCol;
   * @Precondition startRow + width <= SCREENWIDTH;
   * @Precondition startCol + height <= SCREENHEIGHT;
   * (Not 100% sure it works for 0; Not sure the exact minimim height/width.)
   */
  public String popupInput(String text, String popupPrompt, int height, int width, int startRow, int startCol, String[] prevScreen){
    App.ASSERT(5 <= startRow);
    App.ASSERT(5 <= startCol);
    App.ASSERT(0 <= startRow);
    App.ASSERT(0 <= startCol);
    App.ASSERT(startCol + width <= SCREENWIDTH);
    App.ASSERT(startRow + height <= SCREENHEIGHT);

    ArrayList<String> extra = new ArrayList<String>();
    String[] box = Str.makeTextBox(text, height, width, extra);
    displayScreenWithAddition(box, startRow, startCol);
    if(extra.isEmpty()){ //No extra text
      Str.print("<" + popupPrompt + "> ");
      String input = Main.scan.nextLine();
      display(prevScreen);
      return input;
    }
    
    //Text too long to fit in textBox
    ArrayList<String[]> pages = new ArrayList<String[]>();
    int pageIndex = 0;
    pages.add(box); //Page 0
    while(!extra.isEmpty()){
      String prevPgLastLine = Str.substringIgnoringEscSequences(box[box.length-2], 2, Str.lengthIgnoringEscSeqs(box[box.length-2]) - 2) + "\n";
      text = prevPgLastLine + Str.concatArrayListWNL(extra); //Include last line of prev. page
      box = Str.makeTextBox(text, height, width, extra);
      pages.add(box); //Other pages
    }

    while(true){
      Str.print("<" + popupPrompt + "; navigate with q (up) and z (down)> ");
      String input = Main.scan.nextLine();
      switch (input.toLowerCase()) {
        case "q":
          if(pageIndex > 0) pageIndex--;
          break;
        case "z":
          if(pageIndex < pages.size()-1) pageIndex++;
          break;
        default:
          display(prevScreen);
          return input;
      }
      Str.println("pI: " + pageIndex);
      displayScreenWithAddition(pages.get(pageIndex), startRow, startCol);
    }
  }
}