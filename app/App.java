/**Contains and initializes various program-wide constants and data structures. */

package app;

import java.util.*;

import app.Card.Color;
import app.Card.Rarity;

public class App {
  public static final String SETTINGS_PATH = "app\\settings.dat"; // Changed STS 2.0+\\ to app\\
  public static final String CARD_LIST_PATH = "app\\cardList1.dat";// Changed STS 2.0+\\ to app\\
  public static final String INSTRUCTIONS = "Instructions:\n\n"
                                          + "Interact with the game by typing commands in the terminal. "
                                          + "You can look at these instructions again mid-game by typing \"help\" or \"instructions\". "
                                          + "Specific actions have their respective commands written near them in " + Colors.magenta + "magenta" + Colors.reset + ". "
                                          + "Along with the commands shown on screen, you can type the following:\n\n\"E\", \"end\", or \"end turn\" to end your turn,\n"
                                          + "\"Stat\" or \"status\" to see all entities' status effects, and\n"
                                          + "For convenience, you can refer to draw, discard, and exhaust piles as \"draw\", \"disc\", and \"exh\" respectively.\n\n"
                                          + "Some screens have additional information written below the screen, so check there if you're confused.\n\n"
                                          + "Each combat drops 10-20 " + Colors.gold + "gold" + Colors.reset + ". Try to get as much as possible before dying!\n\n";
  public static final String TITLE = Colors.headerBrown + "\n   ▄▄▄▄▄   █    ██  ▀▄    ▄        ▄▄▄▄▀ ▄  █ ▄███▄          ▄▄▄▄▄   █ ▄▄  ▄█ █▄▄▄▄ ▄███▄   \n"
                                                          + "  █     ▀▄ █    █ █   █  █      ▀▀▀ █   █   █ █▀   ▀        █     ▀▄ █   █ ██ █  ▄▀ █▀   ▀  \n"
                                                          + "▄  ▀▀▀▀▄   █    █▄▄█   ▀█           █   ██▀▀█ ██▄▄        ▄  ▀▀▀▀▄   █▀▀▀  ██ █▀▀▌  ██▄▄    \n"
                                                          + " ▀▄▄▄▄▀    ███▄ █  █   █           █    █   █ █▄   ▄▀      ▀▄▄▄▄▀    █     ▐█ █  █  █▄   ▄▀ \n"
                                                          + "               ▀   █ ▄▀           ▀        █  ▀███▀                   █     ▐   █   ▀███▀   \n"
                                                          + "                  █                       ▀                            ▀       ▀            \n"
                                                          + "                 ▀                                                                          \n" + Colors.reset;
  public static final String GAME_OVER = "  ▄████  ▄▄▄       ███▄ ▄███▓▓█████     ▒█████   ██▒   █▓▓█████  ██▀███  \n" +
                                        " ██▒ ▀█▒▒████▄    ▓██▒▀█▀ ██▒▓█   ▀    ▒██▒  ██▒▓██░   █▒▓█   ▀ ▓██ ▒ ██▒\n" +
                                        "▒██░▄▄▄░▒██  ▀█▄  ▓██    ▓██░▒███      ▒██░  ██▒ ▓██  █▒░▒███   ▓██ ░▄█ ▒\n" +
                                        "░▓█  ██▓░██▄▄▄▄██ ▒██    ▒██ ▒▓█  ▄    ▒██   ██░  ▒██ █░░▒▓█  ▄ ▒██▀▀█▄  \n" +
                                        "░▒▓███▀▒ ▓█   ▓██▒▒██▒   ░██▒░▒████▒   ░ ████▓▒░   ▒▀█░  ░▒████▒░██▓ ▒██▒\n" +
                                        " ░▒   ▒  ▒▒   ▓▒█░░ ▒░   ░  ░░░ ▒░ ░   ░ ▒░▒░▒░    ░ ▐░  ░░ ▒░ ░░ ▒▓ ░▒▓░\n" +
                                        "  ░   ░   ▒   ▒▒ ░░  ░      ░ ░ ░  ░     ░ ▒ ▒░    ░ ░░   ░ ░  ░  ░▒ ░ ▒░\n" +
                                        "░ ░   ░   ░   ▒   ░      ░      ░      ░ ░ ░ ▒       ░░     ░     ░░   ░ \n" +
                                        "      ░       ░  ░       ░      ░  ░       ░ ░        ░     ░  ░   ░     \n" +
                                        "                                                     ░                   \n";
  public static final int POPUP_WIDTH = 43;
  public static final int POPUP_HEIGHT = 30;
  public static final int DEFAULT_SCREEN_WIDTH = 199;
  public static final int DEFAULT_SCREEN_HEIGHT = 50;
  public static final int MIN_SCREEN_WIDTH = 10;
  public static final int MIN_SCREEN_HEIGHT = 10;
  // TODO: Check for realistic values of MSW/MSH^

  public static final SettingsManager settingsManager = new SettingsManager(SETTINGS_PATH);
  // Note: Switched to HashMaps in refactoring; changes not updated in loadAvailableCards.
  // public static HashMap<String, Card> CARDS = loadAvailableCards(CARD_LIST_PATH);
  // Can use Card.getCard(String) and Status.getStatus(String) to easily access w/ null-checking:
  public static final HashMap<String, Card> CARDS = loadCards();
  public static final HashMap<String, Status> STATUSES = loadStatuses();
  // TODO: try w/ final^
  
  public App(){

  }

  public void run(){
    // //Load the available cards list:
    // updateAvailableCardsFile(CARD_LIST_PATH); //TODO: Remove these 3 lines (<, ^, v) <-???
    // ASSERT(CARD_LIST != null);
    Str.println("CARDS: " + CARDS); //Remove
    // ArrayList<Card> REMOVE = loadAvailableCards(CARD_LIST_PATH);
    // System.out.println(REMOVE);
    //TODO: Add more assertions (just like in general)

    //Title
    if(!settingsManager.debug)
      Str.println(Colors.clearScreen);
    Str.println(TITLE);
       
    Str.println("\n" + INSTRUCTIONS + "Press " + Colors.magenta + "Enter" + Colors.reset + " to continue\n");
    
    Main.scan.nextLine();
    
    Run g = new Run();
    g.play();
    //Probably bring back when/if making a home screen or smth:
    /*
    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
    */
  }

  // Previously would save the arraylist of cards to a file and read from it each run. It was too buggy, though,
  // so for now at least I'm just constructing the ~~arraylist~~HashSet each time.

  /**Loads the available cards list from the entered file name
  */
  // public static ArrayList<Card> loadAvailableCards(String pathname){
  //   try {
  // 		FileInputStream fi = new FileInputStream(new File(pathname));
  //     ObjectInputStream oi = new ObjectInputStream(fi);

  //     Object r = oi.readObject();
  //     ArrayList<Card> res = (ArrayList<Card>) r;
    
  //     oi.close();
  //     fi.close();
  //     return res;
      
	// 	} catch (IOException | ClassNotFoundException e) {
	// 		e.printStackTrace();
  //     settingsManager.debug = true; //TODO: Is this meant to be long-term? (ie save() or smth?)
  //     return new ArrayList<Card>();
	// 	}
  // }

  /**Updates the available cards list in the entered file name
   * UNCOMMENT IN main(); IF UPDATED (>= ONCE)
  */
  // private static void updateAvailableCardsFile(String pathname){
  //   try{
  //     FileOutputStream f = new FileOutputStream(new File(pathname));
  //     ObjectOutputStream o = new ObjectOutputStream(f);

  //     ArrayList<Card> cardList = new ArrayList<Card>();

  //     //Name, desc, energy cost, isTargeted, effects
  //     // Write objects to file
  
  //     //For encoding card effects (copied from CardEffect.java:)
  //     //Primary: First word of input data.
  //     //Secondary: Rest of input data, apart from terminal integer
  //     //Power: Terminal integer, or 0 if last word not an integer
  //     //e.g. Stores "Lorem Ipsum Dolor 4" as: P = "Lorem", S = "Ipsum Dolor", p = 4,
  //     // or "Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 0.
  //     cardList.add(new Card("Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 6")), //TODO: Should these arraylists really just be arrays?
  //                       new ArrayList<String>(Arrays.asList("Attack 9")), Rarity.BASIC));
  //     cardList.add(new Card("Defend", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 5")),
  //                       new ArrayList<String>(Arrays.asList("Block 8")), Rarity.BASIC)); 
  //     cardList.add(new Card("Bash", "Attack", 2, true, new ArrayList<String>(Arrays.asList("Attack 8", "Apply Vulnerable 2")),
  //                       new ArrayList<String>(Arrays.asList("Attack 10", "Apply Vulnerable 3")), Rarity.BASIC));
  //     cardList.add(new Card("Slimed", "Status", 1, false, new ArrayList<String>(Arrays.asList("Exhaust")), new ArrayList<String>(), Rarity.NULL));
  //     cardList.add(new Card("Anger", "Attack", 0, true, new ArrayList<String>(Arrays.asList("Attack 6", "Anger")),
  //                       new ArrayList<String>(Arrays.asList("Attack 8", "Anger")), Rarity.COMMON));
  //     cardList.add(new Card("Armaments", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 5", "Upgrade Choose1FromHand")),
  //                       new ArrayList<String>(Arrays.asList("Block 5", "Upgrade Hand")), Rarity.COMMON));
  //     cardList.add(new Card("Searing Blow", "Attack", 2, true, new ArrayList<String>(Arrays.asList("SearingBlow")),
  //                       new ArrayList<String>(Arrays.asList("SearingBlow")), Rarity.COMMON));
  //     cardList.add(new Card("Clash", "Attack", 0, true, new ArrayList<String>(Arrays.asList("Clash", "Attack 14")),
  //                       new ArrayList<String>(Arrays.asList("Clash", "Attack 18")), Rarity.COMMON));
  //     cardList.add(new Card("Body Slam", "Attack", 1, true, new ArrayList<String>(Arrays.asList("BodySlam")),
  //                       0, true, new ArrayList<String>(Arrays.asList("BodySlam")), Rarity.COMMON));
  //     cardList.add(new Card("Cleave", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkAll 8")),
  //                       new ArrayList<String>(Arrays.asList("AtkAll 11")), Rarity.COMMON));
  //     //10
  //     cardList.add(new Card("Clothesline", "Attack", 2, true, new ArrayList<String>(Arrays.asList("Attack 12", "Apply Weak 2")),
  //                       new ArrayList<String>(Arrays.asList("Attack 14", "Apply Weak 3")), Rarity.COMMON));
  //     cardList.add(new Card("Flex", "Gain 2 Strength.\nAt the end of this turn, lose 2 Strength.\n", "Skill", 0, false, new ArrayList<String>(Arrays.asList("AppPlayer Strength 2", "AppPlayer Strength Down 2 UseStatDesc")),
  //                                       "Gain 4 Strength.\nAt the end of this turn, lose 4 Strength.\n", 0, false, new ArrayList<String>(Arrays.asList("AppPlayer Strength 4", "AppPlayer Strength Down 4 UsesStatusDesc")), Rarity.COMMON));
  //     cardList.add(new Card("Havoc", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Havoc")),
  //                       0, false, new ArrayList<String>(Arrays.asList("Havoc")), Rarity.COMMON));
  //     cardList.add(new Card("Headbutt", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 9", "PutOnDrawPile Choose1FromDisc")),
  //                       new ArrayList<String>(Arrays.asList("Attack 12", "PutOnDrawPile Choose1FromDisc")), Rarity.COMMON));
  //     cardList.add(new Card("Heavy Blade", "Attack", 2, true, new ArrayList<String>(Arrays.asList("Heavy 3", "Attack 14")),
  //                       new ArrayList<String>(Arrays.asList("Heavy 5", "Attack 14")), Rarity.COMMON));
  //     cardList.add(new Card("Iron Wave", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Block 5", "Attack 5")),
  //                       new ArrayList<String>(Arrays.asList("Block 7", "Attack 7")), Rarity.COMMON));
  //     cardList.add(new Card("Pommel Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 9", "Draw 1")), //TODO: rn in Card, energy cost displays right next to the name (adj. chars)
  //                       new ArrayList<String>(Arrays.asList("Attack 10", "Draw 2")), Rarity.COMMON));
  //     cardList.add(new Card("Shrug It Off", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 8", "Draw 1")),
  //                       new ArrayList<String>(Arrays.asList("Block 11", "Draw 1")), Rarity.COMMON));
  //     cardList.add(new Card("Sword Boomerang", "Deal 3 damage to a random enemy 3 times.", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkRandom 3", "AtkRandom 3", "AtkRandom 3")),
  //                       "Deal 3 damage to a random enemy 4 times.\n", 1, false, new ArrayList<String>(Arrays.asList("AtkRandom 3", "AtkRandom 3", "AtkRandom 3", "AtkRandom 3")), Rarity.COMMON));
  //     cardList.add(new Card("Thunderclap", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkAll 4", "AppAll Vulnerable 1")),
  //                       new ArrayList<String>(Arrays.asList("AtkAll 7", "AppAll Vulnerable 1")), Rarity.COMMON));
  //     cardList.add(new Card("True Grit", "Exhaust a random card in your hand.", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 7", "Exhaust RandHand")),
  //                 "Exhaust a card in your hand.", 1, false, new ArrayList<String>(Arrays.asList("Block 9", "Exhaust Choose1FromHand")), Rarity.COMMON));
  //     // cardList.add(new Card("Twin Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 5", "Attack 5")),
  //                       // new ArrayList<String>(Arrays.asList("Attack 7", "Attack 7")), Rarity.COMMON));
  //     cardList.add(new Card("Twin Strike", "Deal 5 damage twice.", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 5", "Attack 5")),
  //                       "Deal 7 damage twice.", 1, true, new ArrayList<String>(Arrays.asList("Attack 5", "Attack 5")), Rarity.COMMON));
      
  //     //Remember to update NUMCARDS in loadAvailableCards, for each addition here.
  //     //^Probably not necessary anymore.
    
  //     o.writeObject(cardList);

  //     o.close();
  //     f.close();
  //   } catch (IOException e) {
	// 		e.printStackTrace();
  //     settingsManager.debug = true;
	// 	}
  // }

  public static HashMap<String, Card> loadCards(){
    HashMap<String, Card> cards = new HashMap<String, Card>();
    // From Above:

    //For encoding card effects (copied from CardEffect.java:)
    //Primary: First word of input data.
    //Secondary: Rest of input data, apart from terminal integer
    //Power: Terminal integer, or 1 if last word not an integer
    //WhenPlayed: Defaults to ONPLAY; begin with "(OnDiscard) "/"(OnTurnEnd) "/etc. to change.
    //e.g. Stores "Lorem Ipsum Dolor 4" as: P = "Lorem", S = "Ipsum Dolor", p = 4,
    // or "Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1.
    // or "(OnExhaust) Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1, WP = ONEXHAUST
    cards.put("Burn", new Card("Burn", "Unplayable.\nAt the end of your turn, take 2 damage.\n", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "(OnTurnEnd) DmgPlayer 2")),
                      "Unplayable.\nAt the end of your turn, take 4 damage.\n", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "(OnTurnEnd) DmgPlayer 4")), Rarity.COMMON, Color.NEUTRAL)); //TODO: Something that makes burn discard even w/ retain? Or just hard-code that in?
    // Statuses can't be upgraded by default (incl. by apotheosis, etc.) So Burn+ is effectively its own card.
    cards.put("Burn+", new Card(Colors.upgradeGreen + "Burn+", "Unplayable.\nAt the end of your turn, take 4 damage.\n", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "(OnTurnEnd) DmgPlayer 4")),
                      "", -1, false, new ArrayList<String>(Arrays.asList()), Rarity.COMMON, Color.NEUTRAL));
    cards.put("Dazed", new Card("Dazed", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "Ethereal")), new ArrayList<String>(), Rarity.COMMON, Color.NEUTRAL));
    cards.put("Slimed", new Card("Slimed", "Status", 1, false, new ArrayList<String>(Arrays.asList("Exhaust")), new ArrayList<String>(), Rarity.COMMON, Color.NEUTRAL));
    cards.put("Void", new Card("Void", "Unplayable.\nEthereal.\nWhenever this card is drawn, lose 1 Energy\n", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "Ethereal", "(OnDraw) ChangeEnergy -1")),
                      "", -1, false, new ArrayList<String>(Arrays.asList()), Rarity.COMMON, Color.NEUTRAL));
    cards.put("Wound", new Card("Wound", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable")), new ArrayList<String>(), Rarity.COMMON, Color.NEUTRAL));

    cards.put("Strike", new Card("Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 6")), //TODO: Should these arraylists really just be arrays?
                      new ArrayList<String>(Arrays.asList("Attack 9")), Rarity.BASIC, Color.IRONCLAD));
    cards.put("Defend", new Card("Defend", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 5")),
                      new ArrayList<String>(Arrays.asList("Block 8")), Rarity.BASIC, Color.IRONCLAD)); 
    cards.put("Bash", new Card("Bash", "Attack", 2, true, new ArrayList<String>(Arrays.asList("Attack 8", "Apply Vulnerable 2")),
                      new ArrayList<String>(Arrays.asList("Attack 10", "Apply Vulnerable 3")), Rarity.BASIC, Color.IRONCLAD));
    cards.put("Anger", new Card("Anger", "Attack", 0, true, new ArrayList<String>(Arrays.asList("Attack 6", "Anger")),
                      new ArrayList<String>(Arrays.asList("Attack 8", "Anger")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Armaments", new Card("Armaments", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 5", "Upgrade Choose1FromHand")),
                      new ArrayList<String>(Arrays.asList("Block 5", "Upgrade Hand")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Searing Blow", new Card("Searing Blow", "Attack", 2, true, new ArrayList<String>(Arrays.asList("SearingBlow")),
                      new ArrayList<String>(Arrays.asList("SearingBlow")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Clash", new Card("Clash", "Attack", 0, true, new ArrayList<String>(Arrays.asList("Clash", "Attack 14")),
                      new ArrayList<String>(Arrays.asList("Clash", "Attack 18")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Body Slam", new Card("Body Slam", "Attack", 1, true, new ArrayList<String>(Arrays.asList("BodySlam")),
                      0, true, new ArrayList<String>(Arrays.asList("BodySlam")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Cleave", new Card("Cleave", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkAll 8")),
                      new ArrayList<String>(Arrays.asList("AtkAll 11")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Clothesline", new Card("Clothesline", "Attack", 2, true, new ArrayList<String>(Arrays.asList("Attack 12", "Apply Weak 2")),
                      new ArrayList<String>(Arrays.asList("Attack 14", "Apply Weak 3")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Flex", new Card("Flex", "Gain 2 Strength.\nAt the end of this turn, lose 2 Strength.\n", "Skill", 0, false, new ArrayList<String>(Arrays.asList("AppPlayer Strength 2", "AppPlayer Strength Down 2")),
                                      "Gain 4 Strength.\nAt the end of this turn, lose 4 Strength.\n", 0, false, new ArrayList<String>(Arrays.asList("AppPlayer Strength 4", "AppPlayer Strength Down 4")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Havoc", new Card("Havoc", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Havoc")),
                      0, false, new ArrayList<String>(Arrays.asList("Havoc")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Headbutt", new Card("Headbutt", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 9", "PutOnDrawPile Choose1FromDisc")),
                      new ArrayList<String>(Arrays.asList("Attack 12", "PutOnDrawPile Choose1FromDisc")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Heavy Blade", new Card("Heavy Blade", "Attack", 2, true, new ArrayList<String>(Arrays.asList("HeavyAttack 3")),
                      new ArrayList<String>(Arrays.asList("HeavyAttack 5")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Iron Wave", new Card("Iron Wave", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Block 5", "Attack 5")),
                      new ArrayList<String>(Arrays.asList("Block 7", "Attack 7")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Pommel Strike", new Card("Pommel Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 9", "Draw 1")), //TODO: rn in Card, energy cost displays right next to the name (adj. chars)
                      new ArrayList<String>(Arrays.asList("Attack 10", "Draw 2")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Shrug It Off", new Card("Shrug It Off", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 8", "Draw 1")),
                      new ArrayList<String>(Arrays.asList("Block 11", "Draw 1")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Sword Boomerang", new Card("Sword Boomerang", "Deal ØatkÁ3ØendatkÁ damage to a random enemy 3 times.\n", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkRandom 3", "AtkRandom 3", "AtkRandom 3")),
                      "Deal ØatkÁ3ØendatkÁ damage to a random enemy 4 times.\n", 1, false, new ArrayList<String>(Arrays.asList("AtkRandom 3", "AtkRandom 3", "AtkRandom 3", "AtkRandom 3")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Thunderclap", new Card("Thunderclap", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkAll 4", "AppAll Vulnerable 1")),
                      new ArrayList<String>(Arrays.asList("AtkAll 7", "AppAll Vulnerable 1")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("True Grit", new Card("True Grit", "Gain 7 block.\nExhaust a random card in your hand.\n", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 7", "Exhaust RandHand")),
                      "Gain 9 block.\nExhaust a card in your hand.\n", 1, false, new ArrayList<String>(Arrays.asList("Block 9", "Exhaust Choose1FromHand")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Twin Strike", new Card("Twin Strike", "Deal ØatkÁ5ØendatkÁ damage twice.\n", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 5", "Attack 5")),
                      "Deal ØatkÁ7ØendatkÁ damage twice.\n", 1, true, new ArrayList<String>(Arrays.asList("Attack 7", "Attack 7")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Warcry", new Card("Warcry", "Skill", 0, false, new ArrayList<String>(Arrays.asList("Draw 1", "PutOnDrawPile Choose1FromHand", "Exhaust")),
                      new ArrayList<String>(Arrays.asList("Draw 2", "PutOnDrawPile Choose1FromHand", "Exhaust")), Rarity.COMMON, Color.IRONCLAD));
    // cards.put("Sentinel", new Card("Sentinel", "Skill", 1, false, new ArrayList<String>(Arrays.asList("(OnExhaust) Block 5")),
    //                   new ArrayList<String>(Arrays.asList("(OnExhaust) Block 8")), Rarity.UNCOMMON)); //TODO: Remove/update to be correct eventually (just for testing rn)
    cards.put("Wild Strike", new Card("Wild Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 12", "GainToDraw Wound")),
                      new ArrayList<String>(Arrays.asList("Attack 17", "GainToDraw Wound")), Rarity.COMMON, Color.IRONCLAD));
    cards.put("Battle Trance", new Card("Battle Trance", "Draw 3 cards.\nYou cannot draw additional cards this turn.\n", "Skill", 0, false, new ArrayList<String>(Arrays.asList("Draw 3", "AppPlayer No Draw")),
                      "Draw 4 cards.\nYou cannot draw additional cards this turn.\n", 0, false, new ArrayList<String>(Arrays.asList("Draw 4", "AppPlayer No Draw")), Rarity.UNCOMMON, Color.IRONCLAD));
    cards.put("Blood for Blood", new Card("Blood for Blood", "Costs 1 less energy for each time you lose HP this combat.\nDeal ØatkÁ18ØendatkÁ damage.\n", "Attack", 4, true, new ArrayList<String>(Arrays.asList("(OnPlayerHurt) ChangeCost -1", "Attack 18")),
                      "Costs 1 less energy for each time you lose HP this combat.\nDeal ØatkÁ22ØendatkÁ damage.\n", 3, true, new ArrayList<String>(Arrays.asList("(OnPlayerHurt) ChangeCost -1", "Attack 22")), Rarity.UNCOMMON, Color.IRONCLAD));
    // Make it so the robber(s) don't "drop gold" when they run away.
    // Make Jaw Worm art wider?
    // Try to make the screen width/etc. update in real time by not using Run>SCREENX & using SettingsManager.x instead?
    // Give credit for making the opening logo (to that website)
    // Make statuses show up in the order attained?
    // Make some of those arraylists into hashsets
    // In the statuses list (& possibly any other lists, too), change the section headers to be some different color (besides just white)
    // Is there benefit to having a screen/interface class?
    // Stretch goal: Allow for a screen width of 155

    // TODO: Remove:
    for (Card c : cards.values()) {
      ASSERT(cards.get(c.getName()) == c || Str.equalsSkipEscSeqs(c.getName(), "Burn+"));
    }
    System.out.println("Oof");

    return cards;
  }
  
  public static HashMap<String, Status> loadStatuses(){
    HashMap<String, Status> statuses = new HashMap<String, Status>();
    statuses = new HashMap<String, Status>();
    
    //Name, image, isDecreasing, hasStrength, description
    statuses.put("Vulnerable", new Status("Vulnerable", Colors.darkRed + "V", true, true, "Takes 50% more damage from attacks for the next <str> turn(s)."));
    statuses.put("Weak", new Status("Weak", Colors.lightGreen + "W", true, true, "Deals 25% less attack damage for the next <str> turn(s)."));
    statuses.put("Frail", new Status("Frail", Colors.lightBlue + "F", true, true, "Block gained from cards is reduced by 25% for the next <str> turn(s)."));
    //TODO: Cap at 999 (dex too)
    statuses.put("Strength", new Status("Strength", Colors.hpBarRed + "S", false, true, "Increases attack damage by <str> (per hit)"));
    statuses.put("StrengthDown", new Status("Strength Down", Colors.lightYellow + "s", false, true, "At the end of your turn, lose <str> Strength."));
    statuses.put("Dexterity", new Status("Dexterity", Colors.dexGreen + "D", false, true, "Increases block gained from cards by <str>."));
    statuses.put("DexterityDown", new Status("Dexterity Down", Colors.lightYellow + "d", false, true, "At the end of your turn, lose <str> Dexterity."));
    //TODO: See how this interacts w/, eg, Twin Strike
    statuses.put("CurlUp", new Status("Curl Up", Colors.lightBlue + "C", false, true, "Gains <str> block upon first receiving attack damage."));
    statuses.put("Ritual", new Status("Ritual", Colors.lightBlue + "R", false, true, "Gains <str> Strength at the end of each turn."));
    statuses.put("Angry", new Status("Angry", Colors.lightYellow + "A", false, true, "Gains <str> Strength when this receives attack damage."));
    //TODO: Make display the amount of HP needed for splitting (or just add max HP to the HP bar)
    statuses.put("Split", new Status("Split", Colors.lightGreen + "S", false, false, "When at half HP or below, this splits into two smaller slimes with its current HP."));
    statuses.put("Entangled", new Status("Entangled", Colors.white + "E", true, false, "You may not play any attacks this turn"));
    statuses.put("SporeCloud", new Status("Spore Cloud", Colors.lightYellow + "S", false, true, "On death, applies <str> Vulnerable to the player."));
    statuses.put("Thievery", new Status("Thievery", Colors.lightYellow + "T", false, true, "<str> Gold is stolen with every attack. Total Gold stolen is returned if the enemy is killed."));
    statuses.put("Vigor", new Status("Vigor", Colors.vigorOrange + "v", false, true, "Your next Attack deals <str> additional damage."));
    statuses.put("NoDraw", new Status("No Draw", Colors.lightBlue + "N", true, false, "You may not draw any more cards this turn."));
    return statuses;
  }


  public static void ASSERT(boolean condition){
    if(/*settingsManager.debug == true && */!condition){ //TODO: Optimize if having performace issues.
      throw new AssertionError();
    }
  }
}