package app;
import java.util.*;
import java.util.function.Predicate;

/** Singleton class representing the current run, along with most/all screen-related methods
 * and Settings Screen implementation.
 * 
 * @see Main
 * @see Combat
 * @see Str
 * @see SettingsManager
 */
public class Run {
  private String[] screen;
  public static final int SCREENWIDTH = SettingsManager.sm.screenWidth;
  public static final int SCREENHEIGHT = SettingsManager.sm.screenHeight;
  private int hp, maxHP;
  private int gold;
  private ArrayList<Card> deck;
  /** Singleton run instance */
  public static final Run r = new Run();
  
  private Run(){
    if(r != null){
      throw new IllegalStateException("Instantiating second Run object.");
    }
    hp = maxHP = 80;
    gold = 99;
    generateStartingDeck();

    if(SettingsManager.sm.debug){
      System.out.println("W: " + SCREENWIDTH);
      System.out.println("H: " + SCREENHEIGHT);
      System.out.println("W: " + SettingsManager.sm.screenWidth);
      System.out.println("H: " + SettingsManager.sm.screenHeight);
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

  /** Initialize & populate the deck
  */
  private void generateStartingDeck(){
    final Collection<Card> CARDS = App.CARDS;

    deck = new ArrayList<>(CARDS.size());
    // for(Card c : CARDS){
    //   deck.add(new Card(c));
    // }
    deck.add(new Card("Rage"));
    deck.add(new Card("Pummel"));
    deck.add(new Card("Rampage"));
    deck.add(new Card("Inflame"));
    deck.add(new Card("Armaments"));
  }

  /** Plays the run
  */
  public void play(){
    while(true){ //Runs until hp <= 0, which has a break statement below
      Combat c = new Combat();
      int goldStolen = c.runCombat();
      if(hp <= 0){
        break; //Current death mechanic //global bool var for death?
      }
      combatRewards(goldStolen); //todo: move elsewhere if relevant?
      Main.scan.nextLine();
    }
    if(!SettingsManager.sm.debug)
      Str.println(Colors.clearScreen);
    System.out.println(App.GAME_OVER);
    Str.println("You got " + Colors.gold + gold + Colors.reset + " gold");
  }

  private enum RewardType{
    GOLD,
    CARD,
    POTION
  }

  private class CombatReward {
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
      switch(type){
        case GOLD:
          addGold(data);
          reloadScreenHeader();
          break;
        case CARD:
          //Make a whole card reward method and popup and stuff.
          //(And impl. cards and everything)(Big Task!)
          break;
        //Add potions here obv.
        default:
          break;
      }
    }
  }

  /** Displays the (interactable) popup with rewards for a normal combat
  */
  void combatRewards(int goldStolenBack){
    //Constants:
    final int popupHeight = App.POPUP_HEIGHT;
    final int popupWidth = App.POPUP_WIDTH; //Included for ease of editing later.
    final String header = Str.header("Rewards!", popupWidth, "") + '\n';
    ArrayList<CombatReward> rewards = new ArrayList<CombatReward>();

    //Gold stolen back
    if(goldStolenBack > 0){
      String goldBackText = "$ " + goldStolenBack + " Gold (Stolen Back)";
      CombatReward goldBackReward = new CombatReward(goldBackText, RewardType.GOLD, goldStolenBack);
      rewards.add(goldBackReward);
    }

    //Gold Reward
    int goldAmt = (int)(Math.random()*11) + 10; //Will have to change this for later w/ elites probably.
    String goldText = "$ " + goldAmt + " Gold";
    CombatReward goldReward = new CombatReward(goldText, RewardType.GOLD, goldAmt);
    rewards.add(goldReward);

    //Card Reward
    CombatReward cardReward = new CombatReward(Colors.whiteOnGray + "▓ Add a card to your deck", RewardType.CARD);
    rewards.add(cardReward);

    //TODO: Potions:

    hp += 6;
    if(hp > maxHP){ hp = maxHP; }

    //Add any additional rewards here
    int selectedIndex = 0;

    while(rewards.size() > 0){
      String textToPopup = header; //Used to say '+ Str.concatArrayListWNL(rewards)'
      //Construct the popup text:
      for(int i=0; i < rewards.size(); i++){
        CombatReward r = rewards.get(i);
        //Change the selected reward's color
        String img = r.getImg();
        if(i == selectedIndex){
          //Changing gray to blue
          while(img.contains(Colors.gray)) {
            int nextIndex = img.indexOf(Colors.gray);
            img = img.substring(0, nextIndex) + Colors.blockBlue + img.substring(nextIndex + Colors.gray.length());
          }
          //Changing white on gray to white on blue
          while(img.indexOf(Colors.whiteOnGray) != -1) {
            int nextIndex = img.indexOf(Colors.whiteOnGray);
            img = img.substring(0, nextIndex) + Colors.maxWhiteOnBlockBlue + img.substring(nextIndex + Colors.whiteOnGray.length());
          }
        }
        //Concatenate its image to the popup text
        textToPopup += img + "\n";
      }
      
      //Display the popup:
      //Effectively 'popup(textToPopup);', except I can actually look at what the input is:
      int startCol = SettingsManager.sm.screenWidth/2 - popupWidth/2; // == 78
      String[] screenWithAddition = Str.addStringArraysSkipEscSequences(screen, 6, startCol, Str.makeTextBox(textToPopup, popupHeight , popupWidth));
      display(screenWithAddition); //Same as calling displayScreenWithAddition with the above params, but can pass this screen into input below (v)
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

  /** Reduces the player's gold value by the entered amount.
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

  /** Displays the screen. Same as calling display(screen);
  */
  public void display(){
    display(screen);
  }
  
  /** Displays the entered String array.
  */
  public static void display(String[] arr){
    if(!SettingsManager.sm.debug)
      Str.println(Colors.clearScreen);

    for(String str : arr){
      Str.println(str);
    }
  }

  /** Displays the screen with an addition on top of it.
  */
  public void displayScreenWithAddition(String[] newArray, int topRow, int startCol){
    String[] combinedScreen = Str.addStringArraysSkipEscSequences(screen, topRow, startCol, newArray);
    display(combinedScreen);
  }
  
  /** Constructs the screen String[] and adds/sets basic run-wide values to the screen such as hp and the deck
  */
  private void constructScreen(){
    screen = new String[SCREENHEIGHT];
    String lineOfSpaces = Str.repeatChar(' ', SCREENWIDTH);

    //Fills the screen with 3 lines of gray Blocks then the rest (27) lines of spaces
    reloadScreenHeader();
    Arrays.fill(screen, 5, SCREENHEIGHT, Colors.reset + lineOfSpaces + Colors.reset);
  }

  /** Sets basic run-wide values such as hp and the deck. Doesn't construct a new array.
  */
  public void reloadScreenHeader(){
    String lineOfBlocks = Str.repeatChar('█', SCREENWIDTH);

    //Fills the screen with 3 lines of gray Blocks then the rest (27) lines of spaces
    Arrays.fill(screen, 0, 5, Colors.headerBrown + lineOfBlocks + Colors.reset);
    
    String hpText =  "HP: " + this.hp;
    addToScreen(2, SCREENWIDTH/2 - Str.lengthIgnoringEscSeqs(hpText) -2, hpText, Colors.energyCounterRedOnHeaderBrown, Colors.reset + Colors.headerBrown);
    addToScreen(2, SCREENWIDTH/2 +1, "Gold: " + this.gold, Colors.goldDisplayOnHeaderBrown, Colors.reset + Colors.headerBrown);
    
    
    //Deck (top right)
    String[] deckDisplay = Combat.square(3, 5, deck.size(), Colors.deckBrown, Colors.whiteOnDeckBrown);
    addToScreen(1, SCREENWIDTH-16, deckDisplay , Colors.reset + Colors.deckBrown, Colors.reset + Colors.headerBrown);
    addToScreen(0, SCREENWIDTH-16, "Deck-D", Colors.darkMagentaBoldOnHeaderBrown, Colors.headerBrown);

    //Settings Gear
    String[] gearDisplay = {"▀▄█▄▀", "█" + Colors.magentaOnGearBlue + "Esc" + Colors.gearBlueOnHeaderBrown + "█", "▄▀█▀▄"};
    addToScreen(1, SCREENWIDTH - 7, gearDisplay, Colors.gearBlueOnHeaderBrown, Colors.reset + Colors.headerBrown);
  }

  /** Resets the screen String[] and adds/sets basic run-wide values such as hp and the deck. Doesn't construct a new array.
  */
  public void reloadScreen(){
    reloadScreenHeader();

    String lineOfSpaces = Str.repeatChar(' ', SCREENWIDTH);
    Arrays.fill(screen, 5, SCREENHEIGHT, Colors.reset + lineOfSpaces);
  }

  /** Adds the specified string to the screen array at the row and column
  */
  public void addToScreen(int row, int startCol, String str){
    screen[row] = Str.addStringsSkipEscSequences(screen[row], startCol, str); 
  }
  /** Adds the specified string to the screen array at the specified rown and column. Starts with color and ends with colorReset.
  */
  public void addToScreen(int row, int startCol, String str, String color, String colorReset){
    screen[row] = Str.addStringsSkipEscSequences(screen[row], startCol, str, color, colorReset);
  }

  /** Adds the specified string[] to the screen array at the specified rown and column.
  */
  public void addToScreen(int topRow, int startCol, String[] strings){
    for(int r=topRow; r<topRow+strings.length; r++){
      screen[r] = Str.addStringsSkipEscSequences(screen[r], startCol, strings[r-topRow]);
    }
  }
  /** Adds the specified string to the screen array at the specified rown and column. Starts with color and ends with colorReset.
  */
  public void addToScreen(int topRow, int startCol, String[] strings, String color, String colorReset){
    for(int r=topRow; r<topRow+strings.length; r++){
      screen[r] = Str.addStringsSkipEscSequences(screen[r], startCol, strings[r-topRow], color, colorReset);
    }
  }

  /** Gets user input. If input is one of the registered codes, follows the code and repeats until any other input is entered.
   * @return The first non-code value inputted by the user
  */
  public String input(){
    return input(screen);
  }

  /** Gets user input. If input is one of the registered codes, follows the code and repeats until any other input is entered.
   * @param prevScreen The screen to display before returning
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
        case "set":
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
    //todo: add color to true/false?
    
    boolean exit = false;
    while(!exit){
      displaySettings();
      Str.print("<Press enter to exit, or type the name of a setting to change it>\n");
      String input = Main.scan.nextLine();
      switch(input.toLowerCase()){
        case "name":
          Str.print("Enter the new name (Just press enter to cancel:)\n");
          String s = Main.scan.nextLine();
          if(!s.equals("")){
            SettingsManager.sm.name = s;
            SettingsManager.sm.save();
          }
          break;
        case "screen width":
        case "width":
          Str.println("Enter the new screen width, or enter 1-4 for default width options. (Just press enter to cancel:)");
          int width = getIntWPred(w -> (1<=w && w<=4) || (171<=w && w<=499 && w%2 == 1), "New width must be odd, and between than 171 and 499.");
          
          if(1 <= width && width <= 4){
            width = 155 + 22*width;
          }
          SettingsManager.sm.screenWidth = width;
          SettingsManager.sm.save();
          break;
        case "screen height":
        case "height":
          //TODO: add a way to reset these to default?
          //TODO: Eventually place actually reasonable restrictions on these (+width must be odd.) //TODO: also make these actually work. (currently mostly referring to the FINAL var. Can just tell user they have to restart the game to see changes here?)
          //TODO: Make getIntWPred return an Optional?
          Str.println("Enter the new screen height (Just press enter to cancel:)");
          SettingsManager.sm.screenHeight = getIntWPred(h -> h >= 50, "New height must be at least 50.");
          SettingsManager.sm.save();
          break;
        case "debug mode":
        case "debug":
          Str.println("Enter what to change debug to (Just press enter to cancel:)");
          try{
            SettingsManager.sm.debug = parseBoolInput();
            SettingsManager.sm.save();
          } catch(NumberFormatException E){}
          break;
        case "ansi":
        case "includeansi":
        case "include ansi":
        case "color":
        case "colors":
          Str.println("Enter what to change colors to (Just press enter to cancel:)");
          try{
            SettingsManager.sm.includeANSI = parseBoolInput();
            SettingsManager.sm.save();
          } catch(NumberFormatException E){}
          break;
        case "cheats":
        case "cheat":
          Str.println("Enter what to change cheats to (Just press enter to cancel:)");
          try{
            SettingsManager.sm.cheats = parseBoolInput();
            SettingsManager.sm.save();
          } catch(NumberFormatException E){}
          break;
        case "reset":
        case "default":
        case "defaults":
          Str.println("Are you sure you want to reset all settings to their default values? (y/n)");
          input = Main.scan.nextLine().toLowerCase();
          if(input.equals("y") || input.equals("yes")){
            SettingsManager.sm.resetToDefaults();
          }
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

  private void displaySettings(){
    final int popupHeight = (SCREENHEIGHT*4/5 + 6 <= SCREENHEIGHT) ? SCREENHEIGHT*4/5 : SCREENHEIGHT - 6;
    final int popupWidth = SCREENWIDTH*22/25;
    final String settingsText = Colors.magenta + Str.header("Settings:", popupWidth, "") + 
                                "Settings saved between runs.\n" + 
                                "To change a setting, type the name of the setting and follow the given prompts.\n" + 
                                "Type " + Colors.magenta + "reset" + Colors.reset + " to reset all settings to" +
                                " their default values.\n" + 
                                "Restart the program for some settings to take effect.\n\n" + 
               Colors.magenta + "Name: " + Colors.reset + SettingsManager.sm.name + "\n" + 
               Colors.magenta + "Screen Width: " + Colors.reset + SettingsManager.sm.screenWidth + "\n" + 
               Colors.magenta + "Screen Height: " + Colors.reset + SettingsManager.sm.screenHeight + "\n" + 
               Colors.magenta + "Debug Mode: " + Colors.reset + SettingsManager.sm.debug + "\n" + 
               Colors.magenta + "Colors: " + Colors.reset + SettingsManager.sm.includeANSI + "\n" + 
               Colors.magenta + "Cheats: " + Colors.reset + SettingsManager.sm.cheats + "\n\n" + 
       " " + Colors.basicBlue + Str.repeatStr("═", popupWidth - 6) + Colors.reset + "\n" + 
               Colors.magenta + Str.header("Instructions:", popupWidth, "") + 
                                App.INSTRUCTIONS_TEXT;
    
    displayScreenWithAddition(Str.makeTextBox(settingsText, popupHeight, popupWidth), 5, SCREENWIDTH*3/50);
  }

  /**
   * Repeatedly gets input from the user until they enter an int that passes p.
   * @param p A predicate (ideally a lambda expression) on an integer input.
   * @param errMsg A string to print each time the predicate fails
   * @return The first inputted int which passes p
   */
  private int getIntWPred(Predicate<Integer> p, String errMsg){
    int inputInt;

    while(true){
      try {
        inputInt = Integer.parseInt(Main.scan.nextLine());
      } catch(NumberFormatException E) {
        System.out.println("Please enter an integer");
        continue;
      }
      if(p.test(inputInt)){
        return inputInt;
      }
      Str.println(errMsg);
    }
  }

  private boolean parseBoolInput() throws NumberFormatException{
    String s = Main.scan.nextLine();
    switch(s.toLowerCase()){
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

  /** Displays the screen with a text box of the entered text.
   * Player presses enter to close it. Text wrapped with box of h=30, w=43.
   * @Precondition No spaces adjacent to new lines in text; 
   */
  public void popup(String text){
    popup(text, 30, 43, 6, 78);
  }
  /** Displays the screen with a text box of the entered text.
   * Player presses enter to close it. Text wrapped with box of h=30, w=43.
   * @Precondition No spaces adjacent to new lines in text; 
   */
  public void popup(String text, String[] prevScreen){
    popup(text, 30, 43, 6, 78, prevScreen);
  }
  /** Displays the screen with a text box of the entered text, with the specific width.
   * Player presses enter to close it. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in theText; 
   * @Precondition width <= SCREENWIDTH;
   */
  public void popup(String text, int width){
    App.ASSERT(width <= SCREENWIDTH);
    
    popup(text, 30, width, 6, (SCREENWIDTH-width)/2);
  }
  /** Displays the screen with a text box of the entered text, with the specific width.
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
  /** Displays the screen with a text box of the entered text, with the specific width.
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
  /** Displays the screen with a text box of the entered text, with the specific width.
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
  /** Displays the screen with a text box of the entered text.
   * Player presses gives input other than q/z to close it. Text wrapped with box of h=30, w=43.
   * @Precondition No spaces adjacent to new lines in text; 
   */
  public String popupInput(String text, String popupPrompt){
    return popupInput(text, popupPrompt, 30, 43, 6, 78, screen);
  }
  /** Displays the screen with a text box of the entered text, with the specific width.
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
      switch(input.toLowerCase()){
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