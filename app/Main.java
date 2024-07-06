package app;

import java.util.*;

/**
 * Slay the Spire in the Console - Java                             <p></p>
 * 5/10/22 - 5/25/22 (Alpha release 0.1.0 -- APCSA final)           <p></p>
 * 2/13/23 - 8/28/23                                                <p></p>
 * 12/22/23 - 1/13/24                                               <p></p>
 * 5/22/24 - (...)
 * 
 * @author Zachary Heintzleman
 * @see Run
 * @see README.md
 * @see todo.txt
 */
class Main {
  public static Scanner scan = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("\033[H\033[2JLoading...");

    // final String[] colors = new String[] {Colors.reset, Colors.deckBrown, Colors.blackOnDeckBrown, Colors.whiteOnDeckBrown, Colors.energyCounterRed, Colors.veryLightRedOnenergyCounterRed, Colors.whiteOnenergyCounterRed, Colors.oldMagenta, Colors.magenta, Colors.headerBrown, Colors.magentaOnHeaderBrown, Colors.hpRedOnHeaderBrown, Colors.hpBarRed, Colors.backgroundHPBarRed, Colors.lightRed, Colors.gray, Colors.backgroundGray, Colors.energyCostRed, Colors.energyCostRedBold, Colors.blockBlue, Colors.backgroundBlockBlue, Colors.hpRed, Colors.white, Colors.whiteOnWhite, Colors.whiteBold, Colors.backgroundWhite, Colors.whiteOnGray, Colors.grayOnWhite, Colors.whiteOnBlockBlue, Colors.blockBlueOnWhite, Colors.endTurnButton, Colors.whiteOnEndTurnButton, Colors.magentaOnEndTurnButton, Colors.magentaOnGearBlue, Colors.gearBlueOnHeaderBrown, Colors.atkIntArtRed, Colors.dbfIntArtGrn, Colors.bufIntArtTop, Colors.bufIntArtMid, Colors.bufIntArtBtm, Colors.escIntArtRed, Colors.vulnRed, Colors.lightGreen, Colors.lightBlue, Colors.dexGreen, Colors.upgradeGreen, Colors.descNumBlue, Colors.lightYellow, Colors.tan, Colors.tanOnICRed, Colors.ICRed, Colors.ICRedOnBrown, Colors.brown, Colors.lightBrown, Colors.brownOnLightBrown, Colors.tanOnLightBrown, Colors.tanOnGray, Colors.brownOnGray, Colors.tanOnBrown, Colors.yellow, Colors.tanOnYellow, Colors.gold, Colors.goldOnHeaderBrown, Colors.spikeSlimeColor, Colors.slaverBlue, Colors.slaverRed, Colors.cultistBlue, Colors.fatGGreen, Colors.wizardPurple, Colors.madGRed, Colors.shieldGPink, Colors.sneakyGColor, Colors.louseGreen, Colors.louseRed, Colors.jawWormBlue, Colors.jawWormBlueOnGray, Colors.exhGray , Colors.whiteOnExhGray , Colors.looterBlue, Colors.basicBlue, Colors.vigorOrange, Colors.darkEmbracePurple};
    // final String[] names = new String[] {"reset", "deckBrown", "blackOnDeckBrown", "whiteOnDeckBrown", "energyCounterRed", "veryLightRedOnenergyCounterRed", "whiteOnenergyCounterRed", "oldMagenta", "magenta", "headerBrown", "magentaOnHeaderBrown", "hpRedOnHeaderBrown", "hpBarRed", "backgroundHPBarRed", "lightRed", "gray", "backgroundGray", "energyCostRed", "energyCostRedBold", "blockBlue", "backgroundBlockBlue", "hpRed", "white", "whiteOnWhite", "whiteBold", "backgroundWhite", "whiteOnGray", "grayOnWhite", "whiteOnBlockBlue", "blockBlueOnWhite", "endTurnButton", "whiteOnEndTurnButton", "magentaOnEndTurnButton", "magentaOnGearBlue", "gearBlueOnHeaderBrown", "atkIntArtRed", "dbfIntArtGrn", "bufIntArtTop", "bufIntArtMid", "bufIntArtBtm", "escIntArtRed", "vulnRed", "lightGreen", "lightBlue", "dexGreen", "upgradeGreen", "descNumBlue", "lightYellow", "tan", "tanOnICRed", "ICRed", "ICRedOnBrown", "brown", "lightBrown", "brownOnLightBrown", "tanOnLightBrown", "tanOnGray", "brownOnGray", "tanOnBrown", "yellow", "tanOnYellow", "gold", "goldOnHeaderBrown", "spikeSlimeColor", "slaverBlue", "slaverRed", "cultistBlue", "fatGGreen", "wizardPurple", "madGRed", "shieldGPink", "sneakyGColor", "louseGreen", "louseRed", "jawWormBlue", "jawWormBlueOnGray", "exhGray ", "whiteOnExhGray ", "looterBlue", "basicBlue", "vigorOrange", "darkEmbracePurple"};
    // System.out.println(colors.length + "" + names.length);
    // for(int i = 0; i<colors.length; i++){
    //   Str.print(Colors.reset + colors[i] + names[i] + "▀█▄, ");
    // }
    // System.err.println("\n");

    run();

    //Probably bring back when/if making a home screen or smth:
    /*
    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
    */

    scan.nextLine();
    scan.close();
    Str.println(Colors.clearScreen);
  }

  private static void run(){
    //Title Screen
    if(!SettingsManager.sm.debug){
      Str.println(Colors.clearScreen);
    } else {
      Str.println("CARDS: " + App.CARDS.values());
    }
    
    Str.println(App.TITLE);
    Str.println("\n" + App.INSTRUCTIONS + "Press " + Colors.magenta + "Enter" + Colors.reset + " to continue\n");
    
    Main.scan.nextLine();
    
    Run.r.play();
  }
}
