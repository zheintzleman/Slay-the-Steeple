package app;
import java.util.*;
import java.util.function.Predicate;

import util.CardList;
import util.Colors;
import util.Str;
import util.Util;

/** Singleton class representing the current run, along with most/all screen-related methods
 * and Settings Screen implementation.
 * 
 * @see Main
 * @see Combat
 * @see Str
 * @see SettingsManager
 */
public class Run {
  public static final int SCREENWIDTH = SettingsManager.sm.screenWidth;
  public static final int SCREENHEIGHT = SettingsManager.sm.screenHeight;
  private final List<String> easyPool = new ArrayList<>(List.of("Cultist", "Jaw Worm",
      "Two Louses", "Small and Med Slime"));
  private final List<String> hardPool = new ArrayList<>(List.of("Gremlin Gang",
      "Large Slime", "Lots of Slimes", "Blue Slaver", "Red Slaver", "Three Louses",
      "Two Fungi Beasts", "Exordium Thugs", "Exordium Wildlife", "Looter"));
  private String prevCombat = null;
  private String[] screen;
  private EntityHealth health;
  private int gold, goldStolenBack, floor;
  /// Starts at 5% & decreases by 1% per non-rare. Decreases the chance of
  /// seeing a rare card by this amount (rolling over into decreasing uncommon
  /// chance if greater than rare percentage.)
  private double rareCardOffset = 0.05;
  private CardList deck;
  /** Singleton run instance */
  public static final Run r = new Run();
  
  private Run(){
    if(r != null){
      throw new IllegalStateException("Instantiating second Run object.");
    }
    health = new EntityHealth(80, 80);
    gold = 99;
    goldStolenBack = 0;
    floor = 1;
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
  public int getGold(){ return gold; }
  public int getFloor(){ return floor; }
  public ArrayList<Card> getDeck(){ return deck; }
  // No HP methods on purpose; Just change the Player's HP, and it syncs back
  // up after the combat's over.

  /** Initialize & populate the deck
  */
  private void generateStartingDeck(){
    deck = new CardList();
    deck.add(new Card("Strike"));
    deck.add(new Card("Strike"));
    deck.add(new Card("Strike"));
    deck.add(new Card("Strike"));
    deck.add(new Card("Strike"));
    deck.add(new Card("Defend"));
    deck.add(new Card("Defend"));
    deck.add(new Card("Defend"));
    deck.add(new Card("Defend"));
    deck.add(new Card("Bash"));
  }

  /** Picks a combat from the relevant pool; removes and returns this string.
   * Removes the previous combat, then adds it back, in order to not repeat
   * the same combat twice in a row.
   */
  public String pickCombat(){
    String res;
    if(floor < 4){
      boolean removed = easyPool.remove(prevCombat);
      res = Util.randElt(easyPool);
      if(removed){ easyPool.add(prevCombat); }
    } else {
      boolean removed = hardPool.remove(prevCombat);
      res = Util.randElt(hardPool);
      if(removed){ hardPool.add(prevCombat); }
    }
    return res;
  }

  /** Plays the run.
  */
  public void play(){
    while(true){ //Runs until hp <= 0, which has a break statement below
      String combatName = pickCombat();
      Combat c = new Combat(health, combatName);

      try {
        c.runCombat();
      } catch (CombatOverException e) {}

      if(health.hp <= 0){
        break; //Current death mechanic //global bool var for death?
      }
      combatRewards();
      goldStolenBack = 0;
      prevCombat = combatName;
      floor++;
    }

    if(!SettingsManager.sm.debug)
      Str.println(Colors.clearScreen);
    System.out.println(App.GAME_OVER);
    Str.println("You got " + Colors.gold + gold + Colors.reset + " gold");
  }

  private abstract class CombatReward {
    public String img;
    private final String grayBar37 = Colors.fillColor(Str.repeatChar('█', 37), Colors.gray);

    private CombatReward(String text){
      String endOfMiddleRow = Colors.fillColor(Str.repeatChar('█', 37 - (Str.lengthIgnoringEscSeqs(text)+1)), Colors.gray);
      String middleRow = Colors.gray + "█" + Colors.whiteOnGray + text + Colors.reset + endOfMiddleRow;
      img = " " + grayBar37 + "\n " + middleRow + "\n " + grayBar37 + "\n";
    }

    /** Executes the reward's effect
     * @return Whether the reward was executed (false if card reward skipped.)
     */
    abstract boolean execute();
  }
  private class GoldReward extends CombatReward {
    int goldAmt;
    private GoldReward(String text, int goldAmt){
      super(text);
      this.goldAmt = goldAmt;
    }
    boolean execute(){
      addGold(goldAmt);
      return true;
    }
  }
  private class CardReward extends CombatReward {
    private Card[] options;
    private CardReward(String text){
      super(text);
      this.options = new Card[]
      {generateRewardCard(), generateRewardCard(), generateRewardCard()};
    }
    boolean execute(){
      return cardReward(options);
    }
  }

  /** Displays the (interactable) popup with rewards for a normal combat
  */
  private void combatRewards(){
    //Constants:
    final int popupHeight = App.POPUP_HEIGHT;
    final int popupWidth = App.POPUP_WIDTH; //Included for ease of editing later.
    final ArrayList<CombatReward> rewards = new ArrayList<CombatReward>();

    //Gold stolen back
    if(goldStolenBack > 0){
      String goldBackText = "$ " + goldStolenBack + " Gold (Stolen Back)";
      CombatReward goldBackReward = new GoldReward(goldBackText, goldStolenBack);
      rewards.add(goldBackReward);
    }

    //Gold Reward
    int goldAmt = (int)(Math.random()*11) + 10; //Will have to change this for later w/ elites probably.
    String goldText = "$ " + goldAmt + " Gold";
    CombatReward goldReward = new GoldReward(goldText, goldAmt);
    rewards.add(goldReward);

    //Card Reward
    CombatReward cardReward = new CardReward(Colors.whiteOnGray + "▓ Add a card to your deck");
    rewards.add(cardReward);

    //TODO: Potions:

    health.hp += 6;
    if(health.hp > health.maxHP){ health.hp = health.maxHP; }

    //Add any additional rewards here
    int selectedIndex = 0;

    while(rewards.size() > 0){
      //Construct the popup text:
      String textToPopup = rewardsText(rewards, selectedIndex, popupWidth);
      
      reloadScreenHeader();
      //Display the popup:
      //Effectively 'popup(textToPopup);', except I can actually look at what the input is:
      int startCol = SettingsManager.sm.screenWidth/2 - popupWidth/2; // == 78
      String[] box = Str.makeTextBox(textToPopup, popupHeight, popupWidth);
      String[] screenWithAddition = Str.addStringArraysSkipEscSequences(screen, 6, startCol, box);
      display(screenWithAddition); //Same as calling displayScreenWithAddition with the above params, but can pass this screen into input below (v)
      Str.println("<Just press enter to collect reward; navigate with q (up) and z (down)>");

      //Take in commands:
      String input = input(screenWithAddition);
      switch(input.toLowerCase()){
        case "q":
          if(selectedIndex > 0) selectedIndex--;
          break;
        case "z":
          if(selectedIndex < rewards.size()-1) selectedIndex++;
          break;
        case "":
          boolean executed = rewards.get(selectedIndex).execute();
          if(executed){
            rewards.remove(selectedIndex);
          }
          if(selectedIndex >= rewards.size()){ selectedIndex = rewards.size()-1; }
          break;
        case "0":
          return;
        default:
          break;
      }
      // displayScreenWithAddition(Str.makeTextBox(textToPopup, 30 , 43), 6, 78);
    }
    // Note: This region only reached if user accepts all rewards (e.g. doesn't skip the cards.)
  }

  private String rewardsText(List<CombatReward> rewards, int selectedIndex, final int popupWidth){
    final String header = Str.header("Rewards!", popupWidth, "") + '\n';
    String textToPopup = header; //Used to say '+ Str.concatArrayListWNL(rewards)'
    for(int i=0; i < rewards.size(); i++){
      CombatReward r = rewards.get(i);
      String img = r.img;
      //Change the selected reward's color
      if(i == selectedIndex){
        img = img.replace(Colors.gray, Colors.blockBlue);
        img = img.replace(Colors.whiteOnGray, Colors.maxWhiteOnBlockBlue);
      }
      //Concatenate its image to the popup text
      textToPopup += img + "\n";
    }

    String skipTxt = (Colors.magenta + "0" + Colors.reset + " - Skip");
    skipTxt += Str.repeatChar(' ', popupWidth - 4 - "0 - Skip".length());
    skipTxt = Str.centerText(skipTxt);
    textToPopup += skipTxt;

    return textToPopup;
  }

  /** Runs the card reward process with the given three cards.
   * 
   * @return Whether a card was selected (false if skipped).
   * @Precondition opts.length == 3
   */
  private boolean cardReward(Card[] opts){
    final int BOXWIDTH = Card.CARDWIDTH * 3 + 12;
    final int BOXHEIGHT = Card.CARDHEIGHT + 8;
    String boxText = "Choose a Card:\n\n"
        + Colors.magenta + "1" + Str.repeatChar(' ', Card.CARDWIDTH + 1)
        + Colors.magenta + "2" + Str.repeatChar(' ', Card.CARDWIDTH + 1)
        + Colors.magenta + "3"
        + Str.repeatChar('\n', Card.CARDHEIGHT + 2)
        + Colors.magenta + "0" + Colors.reset + " - Skip";

    // Display popup box:
    String[] box = Str.makeCenteredTextBox(boxText, BOXHEIGHT, BOXWIDTH);
    box = Str.addStringArraysSkipEscSequences(box, 4, 4, opts[0].getImage());
    box = Str.addStringArraysSkipEscSequences(box, 4, 6 + Card.CARDWIDTH, opts[1].getImage());
    box = Str.addStringArraysSkipEscSequences(box, 4, 8 + 2*Card.CARDWIDTH, opts[2].getImage());
    displayScreenWithAddition(box, (SCREENHEIGHT - BOXHEIGHT) / 2, (SCREENWIDTH - BOXWIDTH) / 2);

    // Get user input:
    int input = getIntWPred((Integer i) -> { return i >= 0 && i < 4; },
        "Please enter a number from 0-4.");
    switch(input){
      case 0:
        return false;
      default:
        deck.add(opts[input - 1]);
        return true;
    }
  }

  /** Selects a random card from the available cards, based off of the current
   * rare probability. Updates this probability (rareCardOffset) accordingly.
   * @return A newly-instantiated clone of the card selected.
   */
  private Card generateRewardCard(){
    final double rareProb = 0.03;
    /// Not uncommon cutoff, but the probability itself.
    final double uncommonProb = 0.37;
    final double rng = Math.random() + rareCardOffset;
    ArrayList<Card> cardPool;

    if(rng < rareProb){
      // Generate Rare
      cardPool = App.RARE_CARDS;
      rareCardOffset = 0.05;
    } else if(rng < rareProb + uncommonProb){
      // Generate Uncommon
      cardPool = App.UNCOMMON_CARDS;
      rareCardOffset -= 0.01;
    } else {
      // Generate Common
      cardPool = App.COMMON_CARDS;
      rareCardOffset -= 0.01;
    }

    // Max of +40% (i.e. minus -40%).
    rareCardOffset = Math.max(rareCardOffset, -0.40);
    
    return new Card(Util.randElt(cardPool));
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
  /** For Looter/Mugger; On death, they call this function to add X amount of
   * gold to the additional combat reward.
   */
  public void stealGoldBack(int amount){
    goldStolenBack += amount;
  }

  /** Displays the screen. Same as calling display(screen);
  */
  public void display(){
    reloadScreenHeader();
    display(screen);
  }
  
  /** Displays the entered String array.
  */
  public void display(String[] arr){
    if(!SettingsManager.sm.debug)
      Str.println(Colors.clearScreen);

    for(String str : arr){
      Str.println(str);
    }
  }

  /** Displays the screen with an addition on top of it.
  */
  public void displayScreenWithAddition(String[] newArray, int topRow, int startCol){
    reloadScreenHeader();
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
    
    String hpText =  "HP: " + health.hp;
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
        case "w":
        case "screen width":
        case "width":
          Str.println("Enter the new screen width, or enter 1-3 for default width options, or 4 for extra-wide mode. (Just press enter to cancel:)");
          int width = getIntWPred(w -> (1<=w && w<=4) || (177<=w && w<=499 && w%2 == 1), "New width must be odd, and between than 177 and 499.");
          
          if(1 <= width && width <= 4){
            width = 155 + 22*width;
          }
          SettingsManager.sm.screenWidth = width;
          SettingsManager.sm.save();
          break;
        case "h":
        case "screen height":
        case "height":
          //TODO: add a way to reset these to default?
          //TODO: Eventually place actually reasonable restrictions on these (+width must be odd.) //TODO: also make these actually work. (currently mostly referring to the FINAL var. Can just tell user they have to restart the game to see changes here?)
          //TODO: Make getIntWPred return an Optional?
          Str.println("Enter the new screen height (Just press enter to cancel:)");
          SettingsManager.sm.screenHeight = getIntWPred(h -> h >= 50 && h <= 200, "New height must be between 50 and 200.");
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
        System.out.println("Please enter a number");
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
      case "y":
      case "yes":
      case "on":
        return true;
      case "false":
      case "n":
      case "no":
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