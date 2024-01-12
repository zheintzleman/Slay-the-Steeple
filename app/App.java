package app;

import java.util.*;

import app.Card.Color;
import app.Card.Rarity;

import java.io.*;

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

  public static SettingsManager settingsManager = new SettingsManager(SETTINGS_PATH);
  // public static ArrayList<Card> CARD_LIST = loadAvailableCards(CARD_LIST_PATH);
  public static ArrayList<Card> CARD_LIST = loadCardList();
  
  public App(){

  }

  public void run(){
    // //Load the available cards list:
    // updateAvailableCardsFile(CARD_LIST_PATH); //TODO: Remove these 3 lines (<, ^, v) <-???
    // ASSERT(CARD_LIST != null);
    Str.println("LIST: " + CARD_LIST); //Remove
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
  // so for now at least I'm just constructing the arraylist each time.

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

  public static ArrayList<Card> loadCardList(){
    ArrayList<Card> cardList = new ArrayList<Card>();
    // From Above:

    //For encoding card effects (copied from CardEffect.java:)
    //Primary: First word of input data.
    //Secondary: Rest of input data, apart from terminal integer
    //Power: Terminal integer, or 1 if last word not an integer
    //WhenPlayed: Defaults to ONPLAY; begin with "(OnDiscard) "/"(OnTurnEnd) "/etc. to change.
    //e.g. Stores "Lorem Ipsum Dolor 4" as: P = "Lorem", S = "Ipsum Dolor", p = 4,
    // or "Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1.
    // or "(OnExhaust) Lorem Ipsum 4 Dolor" as: P = "Lorem", S = "Ipsum 4 Dolor", p = 1, WP = ONEXHAUST
    cardList.add(new Card("Burn", "Unplayable.\nAt the end of your turn, take 2 damage.\n", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "(OnTurnEnd) DmgPlayer 2")),
                      "Unplayable.\nAt the end of your turn, take 4 damage.\n", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "(OnTurnEnd) DmgPlayer 4")), Rarity.COMMON, Color.NEUTRAL)); //TODO: Something that makes burn discard even w/ retain? Or just hard-code that in?
    // Statuses can't be upgraded by default;^ Burn is a hardcoded exception
    cardList.add(new Card("Dazed", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "Ethereal")), new ArrayList<String>(), Rarity.COMMON, Color.NEUTRAL));
    cardList.add(new Card("Slimed", "Status", 1, false, new ArrayList<String>(Arrays.asList("Exhaust")), new ArrayList<String>(), Rarity.COMMON, Color.NEUTRAL));
    cardList.add(new Card("Void", "Unplayable.\nEthereal.\nWhenever this card is drawn, lose 1 Energy\n", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable", "Ethereal", "(OnDraw) ChangeEnergy -1")),
                      "", -1, false, new ArrayList<String>(Arrays.asList()), Rarity.COMMON, Color.NEUTRAL));
    cardList.add(new Card("Wound", "Status", -1, false, new ArrayList<String>(Arrays.asList("Unplayable")), new ArrayList<String>(), Rarity.COMMON, Color.NEUTRAL));

    cardList.add(new Card("Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 6")), //TODO: Should these arraylists really just be arrays?
                      new ArrayList<String>(Arrays.asList("Attack 9")), Rarity.BASIC, Color.IRONCLAD));
    cardList.add(new Card("Defend", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 5")),
                      new ArrayList<String>(Arrays.asList("Block 8")), Rarity.BASIC, Color.IRONCLAD)); 
    cardList.add(new Card("Bash", "Attack", 2, true, new ArrayList<String>(Arrays.asList("Attack 8", "Apply Vulnerable 2")),
                      new ArrayList<String>(Arrays.asList("Attack 10", "Apply Vulnerable 3")), Rarity.BASIC, Color.IRONCLAD));
    cardList.add(new Card("Anger", "Attack", 0, true, new ArrayList<String>(Arrays.asList("Attack 6", "Anger")),
                      new ArrayList<String>(Arrays.asList("Attack 8", "Anger")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Armaments", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 5", "Upgrade Choose1FromHand")),
                      new ArrayList<String>(Arrays.asList("Block 5", "Upgrade Hand")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Searing Blow", "Attack", 2, true, new ArrayList<String>(Arrays.asList("SearingBlow")),
                      new ArrayList<String>(Arrays.asList("SearingBlow")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Clash", "Attack", 0, true, new ArrayList<String>(Arrays.asList("Clash", "Attack 14")),
                      new ArrayList<String>(Arrays.asList("Clash", "Attack 18")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Body Slam", "Attack", 1, true, new ArrayList<String>(Arrays.asList("BodySlam")),
                      0, true, new ArrayList<String>(Arrays.asList("BodySlam")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Cleave", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkAll 8")),
                      new ArrayList<String>(Arrays.asList("AtkAll 11")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Clothesline", "Attack", 2, true, new ArrayList<String>(Arrays.asList("Attack 12", "Apply Weak 2")),
                      new ArrayList<String>(Arrays.asList("Attack 14", "Apply Weak 3")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Flex", "Gain 2 Strength.\nAt the end of this turn, lose 2 Strength.\n", "Skill", 0, false, new ArrayList<String>(Arrays.asList("AppPlayer Strength 2", "AppPlayer Strength Down 2")),
                                      "Gain 4 Strength.\nAt the end of this turn, lose 4 Strength.\n", 0, false, new ArrayList<String>(Arrays.asList("AppPlayer Strength 4", "AppPlayer Strength Down 4")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Havoc", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Havoc")),
                      0, false, new ArrayList<String>(Arrays.asList("Havoc")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Headbutt", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 9", "PutOnDrawPile Choose1FromDisc")),
                      new ArrayList<String>(Arrays.asList("Attack 12", "PutOnDrawPile Choose1FromDisc")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Heavy Blade", "Attack", 2, true, new ArrayList<String>(Arrays.asList("HeavyAttack 3")),
                      new ArrayList<String>(Arrays.asList("HeavyAttack 5")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Iron Wave", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Block 5", "Attack 5")),
                      new ArrayList<String>(Arrays.asList("Block 7", "Attack 7")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Pommel Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 9", "Draw 1")), //TODO: rn in Card, energy cost displays right next to the name (adj. chars)
                      new ArrayList<String>(Arrays.asList("Attack 10", "Draw 2")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Shrug It Off", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 8", "Draw 1")),
                      new ArrayList<String>(Arrays.asList("Block 11", "Draw 1")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Sword Boomerang", "Deal ØatkÁ3ØendatkÁ damage to a random enemy 3 times.\n", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkRandom 3", "AtkRandom 3", "AtkRandom 3")),
                      "Deal ØatkÁ3ØendatkÁ damage to a random enemy 4 times.\n", 1, false, new ArrayList<String>(Arrays.asList("AtkRandom 3", "AtkRandom 3", "AtkRandom 3", "AtkRandom 3")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Thunderclap", "Attack", 1, false, new ArrayList<String>(Arrays.asList("AtkAll 4", "AppAll Vulnerable 1")),
                      new ArrayList<String>(Arrays.asList("AtkAll 7", "AppAll Vulnerable 1")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("True Grit", "Gain 7 block.\nExhaust a random card in your hand.\n", "Skill", 1, false, new ArrayList<String>(Arrays.asList("Block 7", "Exhaust RandHand")),
                      "Gain 9 block.\nExhaust a card in your hand.\n", 1, false, new ArrayList<String>(Arrays.asList("Block 9", "Exhaust Choose1FromHand")), Rarity.COMMON, Color.IRONCLAD));
    // cardList.add(new Card("Twin Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 5", "Attack 5")),
                      // new ArrayList<String>(Arrays.asList("Attack 7", "Attack 7")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Twin Strike", "Deal ØatkÁ5ØendatkÁ damage twice.\n", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 5", "Attack 5")),
                      "Deal ØatkÁ7ØendatkÁ damage twice.\n", 1, true, new ArrayList<String>(Arrays.asList("Attack 7", "Attack 7")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Warcry", "Skill", 0, false, new ArrayList<String>(Arrays.asList("Draw 1", "PutOnDrawPile Choose1FromHand", "Exhaust")),
                      new ArrayList<String>(Arrays.asList("Draw 2", "PutOnDrawPile Choose1FromHand", "Exhaust")), Rarity.COMMON, Color.IRONCLAD));
    // cardList.add(new Card("Sentinel", "Skill", 1, false, new ArrayList<String>(Arrays.asList("(OnExhaust) Block 5")),
    //                   new ArrayList<String>(Arrays.asList("(OnExhaust) Block 8")), Rarity.UNCOMMON)); //TODO: Remove/update to be correct eventually (just for testing rn)
    cardList.add(new Card("Wild Strike", "Attack", 1, true, new ArrayList<String>(Arrays.asList("Attack 12", "GainToDraw Wound")),
                      new ArrayList<String>(Arrays.asList("Attack 17", "GainToDraw Wound")), Rarity.COMMON, Color.IRONCLAD));
    cardList.add(new Card("Battle Trance", "Skill", 0, false, new ArrayList<String>(Arrays.asList("Draw 3", "AppPlayer No Draw")),
                      new ArrayList<String>(Arrays.asList("Draw 4", "AppPlayer No Draw")), Rarity.UNCOMMON, Color.IRONCLAD));
    // cardList.add(new Card("Blood for Blood", "Attack", 4, true, ,
    // 3, true, , Rarity.UNCOMMON, Color.IRONCLAD));
    cardList.add(new Card("Blood for Blood", "Costs 1 less energy for each time you lose HP this combat.\nDeal ØatkÁ18ØendatkÁ damage.\n", "Attack", 4, true, new ArrayList<String>(Arrays.asList("(OnPlayerHurt) ChangeCost -1", "Attack 18")),
                      "Costs 1 less energy for each time you lose HP this combat.\nDeal ØatkÁ22ØendatkÁ damage.\n", 3, true, new ArrayList<String>(Arrays.asList("(OnPlayerHurt) ChangeCost -1", "Attack 22")), Rarity.UNCOMMON, Color.IRONCLAD));
    // Make an event system/class(calls relevant relic/status/we/ functions) (/smth)
    // TODO: Make the display of how much damage heavy blade will do accurate (probably using the events/(OnX)'s)
    // Change the cards' (OnDiscard)/etc. into an EventManager enum?
    // ^Could even then make cards' effects all go through one of these? (Like normally through (CardPlayed) or smth?)
    //  ^Is that useful though?
    // Deal w/ hand size issue
    // ^Could make, eg, right 4(?) cards show only half the card (overlap) & typing ">" changes that to the left 4(?)?
    // Make sure all card draw in Combat.java uses the drawCard() method (if applicable)
    // Unstackable statuses shouldn't gain multiple levels, right? (Playing two battle trances in one turn)
    // ^Could change how "Apply" works, how addstatstrength works, how Status power works in general, idk
    // Battle trance text; b4b's text & functionality

    return cardList;
  }


  public static void ASSERT(boolean condition){
    if(/*settingsManager.debug == true && */!condition){ //TODO: Optimize if having performace issues.
      throw new AssertionError();
    }
  }
}