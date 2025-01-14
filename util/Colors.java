package util;

import app.App;

/** A collection of public static final String's (mostly text-color escape codes), for legibility
 * purposes.
 * 
 * @see Str
 * @see App
 */
public abstract class Colors {
  public static final String reset = "Ø\u001B[0mÁ";
  public static final String clearScreen = "Ø\033[H\033[2JÁ";
  public static final String deckBrown = "Ø\033[38;2;81;54;26mÁ";
  // public static final String blackOnDeckBrown = "Ø\033[48;2;81;54;26m\033[30mÁ";
  public static final String whiteOnDeckBrown = "Ø\033[48;2;81;54;26m\033[37mÁ";
  public static final String energyCounterRed = "Ø\033[38;2;95;0;0mÁ"; //Prev: "Ø\033[38;5;52mÁ"
  public static final String energyCounterRedBold = "Ø\033[38;2;95;0;0;1mÁ";
  // public static final String veryLightRedOnEnergyCounterRed = "Ø\033[91m\033[48;5;52mÁ";
  public static final String whiteOnEnergyCounterRed = "Ø\033[37m\033[48;5;52mÁ";
  // public static final String oldMagenta = "Ø\033[38;5;126mÁ";
  public static final String magenta = "Ø\033[38;2;210;106;185mÁ";
  // public static final String darkMagenta = "Ø\033[38;2;89;22;74mÁ";
  public static final String headerBrown = "Ø\033[38;2;152;118;84mÁ";
  public static final String darkMagentaBoldOnHeaderBrown = "Ø\033[48;2;152;118;84m\033[38;2;89;22;74;1mÁ";
  // public static final String magentaOnDeckBrown = "Ø\033[38;2;210;106;185m\033[48;2;81;54;26mÁ";
  public static final String energyCounterRedOnHeaderBrown = "Ø\033[38;2;95;0;0;1m\033[48;2;152;118;84mÁ";
  public static final String hpBarRed = "Ø\033[38;2;175;0;0mÁ";
  public static final String backgroundHPBarRed = "Ø\033[48;2;175;0;0mÁ";
  // public static final String lightRed = "Ø\033[31mÁ";
  public static final String lightGray = "Ø\033[38;2;145;145;145mÁ";
  public static final String blueGray = "Ø\033[38;2;140;145;160mÁ";
  public static final String gray = "Ø\033[1;90mÁ";
  // public static final String backgroundGray = "Ø\033[1;100mÁ";
  public static final String energyCostRed = "Ø\033[38;2;210;106;106mÁ";
  public static final String energyCostRedBold = "Ø\033[38;2;210;106;106mÁ";
  public static final String blockBlue = "Ø\033[38;5;33mÁ";
  // public static final String blockBlueOnHeaderBrown = "Ø\033[38;5;33m\033[48;2;152;118;84mÁ";
  public static final String backgroundBlockBlue = "Ø\033[48;5;33mÁ";
  public static final String hpTextRedOnWhite = "Ø\u001B[38;5;88;1m\033[47mÁ";
  //public static final String backgroundHPRed = "Ø\u001B[48;5;88;1mÁ";
  public static final String white = "Ø\033[37mÁ";
  // public static final String whiteOnWhite = "Ø\033[37m\033[47mÁ";
  public static final String whiteBold = "Ø\033[37;1mÁ";
  public static final String backgroundWhite = "Ø\033[47mÁ";
  public static final String whiteOnGray = "Ø\033[37m\033[1;100mÁ";
  // public static final String grayOnWhite = "Ø\033[1;90m\033[47mÁ";
  // vCurrently ~looks like black. Want to keep it that way or actually white now?
  public static final String maxWhiteOnBlockBlue = "Ø\033[38;2;255;255;255m\033[48;5;33mÁ";
  // public static final String blockBlueOnWhite = "Ø\033[38;5;33m\033[47mÁ";
  // public static final String endTurnButton = "Ø\033[38;5;23mÁ";
  // public static final String whiteOnEndTurnButton = "Ø\033[37m\033[48;5;23mÁ";
  // public static final String magentaOnEndTurnButton = "Ø\033[48;2;152;118;84m\033[48;5;23mÁ";
  public static final String magentaOnGearBlue = "Ø\033[38;2;210;106;185m\033[48;2;40;95;105mÁ";
  public static final String gearBlueOnHeaderBrown = "Ø\033[38;2;40;95;105m\033[48;2;152;118;84mÁ";
  public static final String atkIntArtRed = "Ø\033[38;5;88mÁ";
  public static final String dbfIntArtGrn = "Ø\033[38;5;119mÁ";
  public static final String bufIntArtTop = "Ø\033[38;2;33;174;235mÁ";
  public static final String bufIntArtMid = "Ø\033[38;2;30;199;250mÁ";
  public static final String bufIntArtBtm = "Ø\033[38;2;36;248;255mÁ";
  public static final String escIntArtRed = "Ø\033[38;5;52mÁ";
  public static final String vulnRed = "Ø\033[38;5;196mÁ";
  public static final String lightGreen = "Ø\033[38;5;156mÁ";
  public static final String lightBlue = "Ø\033[38;5;116mÁ";
  public static final String dexGreen = "Ø\033[38;5;119mÁ";
  public static final String upgradeGreen = "Ø\033[38;2;126;215;42mÁ";
  public static final String descNumBlue = "Ø\033[38;2;26;147;219mÁ";
  public static final String lightYellow = "Ø\033[38;5;228mÁ";
  public static final String tan = "Ø\033[38;5;179mÁ";
  public static final String tanOnICRed = "Ø\033[38;5;179m\033[48;5;88mÁ";
  public static final String ICRed = "Ø\033[38;5;88mÁ";
  public static final String ICRedOnBrown = "Ø\033[38;5;88m\033[48;2;81;54;26mÁ";
  public static final String brown = "Ø\033[38;2;81;54;26mÁ";
  public static final String lightBrown = "Ø\033[38;2;92;85;73mÁ";
  public static final String brownOnLightBrown = "Ø\033[38;2;81;54;26m\033[48;2;92;85;73mÁ";
  public static final String tanOnLightBrown = "Ø\033[38;5;179m\033[48;2;92;85;73mÁ";
  // Note: gray split meanings above
  public static final String tanOnGray = "Ø\033[38;5;179m\033[1;100mÁ";
  public static final String brownOnGray = "Ø\033[38;2;81;54;26m\033[1;100mÁ";
  public static final String tanOnBrown = "Ø\033[38;5;179m\033[48;2;81;54;26mÁ";
  // public static final String yellow = "Ø\033[38;5;220mÁ";
  public static final String tanOnYellow = "Ø\033[38;5;179m\033[48;5;220mÁ";
  public static final String gold = "Ø\033[38;5;208mÁ";
  public static final String goldDisplayOnHeaderBrown = "Ø\033[38;2;77;41;0;1m\033[48;2;152;118;84mÁ";
  public static final String spikeSlimeColor = "Ø\033[38;5;66mÁ";
  public static final String slaverBlue = "Ø\033[38;5;25mÁ";
  public static final String slaverRed = "Ø\033[38;2;148;57;85mÁ";
  public static final String cultistBlue = "Ø\033[38;2;62;106;141mÁ";
  public static final String fatGGreen = "Ø\033[38;2;198;94;72mÁ";
  public static final String wizardPurple = "Ø\033[38;5;61mÁ";
  public static final String madGRed = "Ø\033[38;5;160mÁ";
  public static final String shieldGPink = "Ø\033[38;5;219mÁ";
  public static final String sneakyGPurple = "Ø\033[38;2;160;62;108mÁ";
  public static final String louseGreen = "Ø\033[38;2;155;182;86mÁ";
  public static final String louseRed = "Ø\033[38;2;215;100;109mÁ";
  public static final String jawWormBlue = "Ø\033[38;2;67;138;152mÁ";
  // Note: gray split meanings above
  public static final String jawWormBlueOnGray = "Ø\033[38;2;67;138;152m\033[100mÁ";
  public static final String exhGray  = "Ø\033[38;5;236mÁ";
  public static final String whiteOnExhGray  = "Ø\033[37m\033[48;5;236mÁ";
  public static final String looterBlue = "Ø\033[38;2;65;103;145mÁ";
  public static final String basicBlue = "Ø\033[38;2;38;66;77mÁ";
  public static final String vigorOrange = "Ø\033[38;5;214mÁ";
  public static final String darkEmbracePurple = "Ø\033[38;2;235;160;240mÁ";
  public static final String purple = "Ø\033[38;2;200;35;186mÁ";
  public static final String demonFormRed = "Ø\033[38;2;252;72;58mÁ";
  public static final String doubleTapBlue = "Ø\033[38;2;31;67;116mÁ";
  public static final String blue = "Ø\033[38;2;100;90;220mÁ";
  public static final String rareYellow = "Ø\033[38;2;204;176;65mÁ";
  public static final String uncommonBlue = "Ø\033[38;2;56;167;186mÁ";


  public static final String[] IRONCLADIMG0 = Colors.fillColor(new String[] {"        ▄▄▄   ",
                                                                             "       ▄███▄  ",
                                                                            "▀▀▀▀▀▀▀▀▀████  ",
                                                                             "       ▄███▄  ",
                                                                             "       ██▀ ▀██ ",
                                                                             "       █▀    ▀█"}, Colors.atkIntArtRed); 

  public static final String[] IRONCLADIMG2 = new String[] {"                    " + fillColor("▄▄▄", tan) + reset + "       " + "          ",
                                                            "                   " + gray + "█" + fillColor("██▄█", tanOnYellow) + reset + "      " + "          ",
                                                            "                  " + tan + "▄" + tanOnGray + "▄" + brownOnGray + "▄" + fillColor("▀▀", tanOnBrown) + reset + brown + "▄" + tan + "▄" + reset + "     " + "          ",
                                                            fillColor("█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄", gray) + reset + fillColor("▀▀█", tanOnLightBrown) + fillColor("██", brown) + brownOnLightBrown + "▀" + tanOnLightBrown + "▀" + reset + fillColor("██", tan) + "    " + "          ",
                                                            fillColor(" ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀", gray) + reset + fillColor("▀█", lightBrown) + fillColor("▄▄▄", brownOnGray) + fillColor("▄▄", brownOnLightBrown) + reset + fillColor("██", lightBrown) + "    " + "          ",
                                                            "                  " + fillColor("▄████▄ ", brown) + reset + lightBrown + "▀    " + "          ",
                                                            "                  " + fillColor("██▄▄▄█", ICRedOnBrown) + reset + ICRed + "▄     " + "          ",
                                                            fillColor("                ▄█████████    ", ICRed) + "          ",
                                                            "              " + tan + "▄" + tanOnICRed + "▄" + fillColor("██▀    ▀███", reset + ICRed) + reset + tan + "▄  " + "          ",
                                                            "              " + tan + "▀" + fillColor("▀▀", tanOnLightBrown) + reset + tan + "▀       " +  fillColor("█▀▀", tanOnLightBrown) + reset + "  " + "          ",
                                                            fillColor("               ██          ▀█▄", lightBrown) + "          ", fillColor("              ▀▀           ▄█▀", lightBrown) + "          "};
    
  public static final String[] IRONCLADIMG1 = new String[] {"                    " + fillColor("▄▄▄", tan) + reset + "       " + "          ",
                                                            "                   " + gray + "█" + fillColor("██▄█", tanOnYellow) + reset + "      " + "          ",
                                                            "                  " + tan + "▄" + tanOnGray + "▄" + brownOnGray + "▄" + fillColor("▀▀", tanOnBrown) + reset + brown + "▄" + tan + "▄" + reset + "     " + "          ",
                                                            fillColor("█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄", gray) + fillColor("███", tan) + fillColor("██", brown) + tanOnBrown + "▄" + reset + fillColor("███", tan) + "    " + "          ",
                                                            fillColor(" ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀",gray) + reset + fillColor("▀█", tan) + fillColor("▄▄▄▄", brownOnGray) + tanOnBrown + "▀" + reset + fillColor("██", tan) + "    " + "          ",
                                                            "                  " + fillColor("▄████▄ ", brown) + reset + tan + "▀    " + "          ",
                                                            "                  " + fillColor("██▄▄▄█", ICRedOnBrown) + reset + ICRed + "▄     " + "          ",
                                                            fillColor("                ▄█████████    ", ICRed) + "          ",
                                                            "              " + tan + "▄" + tanOnICRed + "▄" + fillColor("██▀    ▀███", reset + ICRed) + reset + tan + "▄  " + "          ",
                                                            fillColor("              ▀██▀       ███  ", tan) + "          ",
                                                            fillColor("               ██          ▀█▄", tan) + "          ",
                                                            fillColor("              ▀▀           ▄█▀", tan) + "          "};



  /** Adds the color String before every character in str.
  * @Precondition - str must not have any escape sequences (of any type)
  */
  public static String fillColor(String str, String color){
    String coloredString = "";
    for(int i=str.length()-1; i>=0; i--){
      coloredString = color + str.substring(i, i+1) + coloredString;
    }
    return coloredString;
  }
  /** Adds the color String before every character in every string in the array.
  * @Precondition - arr must not have any escape sequences (of any type)
  */
  public static String[] fillColor(String[] arr, String color){
    String[] coloredArr = new String[arr.length];
    for(int i=0; i<coloredArr.length; i++){
      coloredArr[i] = fillColor(arr[i], color);
    }
    return coloredArr;
  }
}
