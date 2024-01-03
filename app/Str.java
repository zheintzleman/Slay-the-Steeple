package app;
import java.util.*;
import java.util.function.Predicate;

/**Static string-management functions */
public class Str {
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ CUT FROM RUN.JAVA ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  /**Constructs and returns a String[] of a box with the (now wrapped) text within. Uses default sizes.
   * @Precondition No spaces adjacent to new lines in text; 
  */
  public static String[] makeTextBox(String text){
    return makeTextBox(text, Run.SCREENHEIGHT*3/4, 43, null);
  }
  /**Constructs and returns a String[] of a box with the (now wrapped) text within. Uses default height and the entered width. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in text; 
  */
  public static String[] makeTextBox(String text, int width){
    return makeTextBox(text, Run.SCREENHEIGHT*3/4, width, null); 
  }
  /**Constructs and returns a String[] of a box with the (now wrapped) text within. Uses default height and the entered width. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in text; 
  */
  public static String[] makeTextBox(String text, int height, int width){
    return makeTextBox(text, height, width, null); 
  }
  /**Constructs and returns a String[] of a box with the (now wrapped) text within. Uses entered height and width. Text wrapped with box of width-4.
   * @Precondition No spaces adjacent to new lines in text;
   * @param extraOut (Output) Replaces extraOut with a list of any lines of the wrapped text that overflow the text box
  */
  public static String[] makeTextBox(String text, int height, int width, ArrayList<String> extraOut){
    String[] box = new String[height];
    Arrays.fill(box, Colors.reset);
    String horizontalBorder = repeatStr("═", width-2);
    box[0] += "╔" + horizontalBorder + "╗";
    box[height-1] += "╚" + horizontalBorder + "╝";

    ArrayList<String> wrappedText = wrapText(text, width-4);
    for(int i=0; i<height-2; i++){
      if(i < wrappedText.size()){
        String line = wrappedText.get(i);
        box[i+1] += "║ " + line;
      }else{
        box[i+1] += "║ "; //If no more text in wrappedText
      }
      while(lengthIgnoringEscSeqs(box[i+1]) < width-2){ //Fill empty space so all rows are the same width
        box[i+1] += " ";
      }
      box[i+1] += Colors.reset + " ║";
    }
    
    if(extraOut == null){
      return box;
    }

    extraOut.clear();

    if(height-2 < wrappedText.size()){ //Too much text to fit
      extraOut.addAll(wrappedText.subList(height-2, wrappedText.size()));
      if(lengthIgnoringEscSeqs(extraOut.get(extraOut.size()-1)) == 0){
        extraOut.remove(extraOut.size()-1);
      }
    }

    return box;

  }

  /**Constructs and returns a String[] of a box with the (now wrapped and centered) text within. Uses default sizes.
  */
  public static String[] makeCenteredTextBox(String text){
    return makeCenteredTextBox(text, Run.SCREENHEIGHT*3/4, 43); 
  }
  /**Constructs and returns a String[] of a box with the (now wrapped and centered) text within. Uses entered height and width.
  */
  public static String[] makeCenteredTextBox(String text, int height, int width){
    String[] box = new String[height];
    Arrays.fill(box, Colors.reset);
    String horizontalBorder = new String(new char[width-2]).replace("\0", "═"); //String of [width-2] '═' characters
    box[0] += "╔" + horizontalBorder + "╗";
    box[height-1] += "╚" + horizontalBorder + "╝";

    ArrayList<String> wrappedText = wrapText(text, width-4);
    for(int i=0; i<height-2; i++){
      if(i < wrappedText.size()){     //If Still more lines in wrappedText
        String line = wrappedText.get(i); //Get next line
        while(lengthIgnoringEscSeqs(line) < width-4){ //Fill empty space so all rows are the same width
          line += " ";
        }
        line = centerText(line);
        box[i+1] += "║ " + line;
      }else{
        box[i+1] += "║ "; //If no more text in wrappedText
        for(int s=0; s<width-4; s++){
          box[i+1] += " ";
        }
      }
      box[i+1] += Colors.reset + " ║";
    }

    return box;
  }

  /**Wraps the entered text to be an arraylist of String each less long than the specified width. Breaks at spaces and at each new line
   * @Precondition No spaces adjacent to new lines in theText; 
   * This condition may not be needed anymore after some refactoring, but I'm not sure.
  */
  public static ArrayList<String> wrapText(String theText, int width){
    ArrayList<String> wrappedText = new ArrayList<String>();
    String text = theText;
    while(!text.equals("")){                                          //While String text is not empty:
      if(lengthIgnoringEscSeqs(text) <= width && (text.indexOf("\n") == -1)){
        wrappedText.add(text);                                                    //If text fits in one line & there's no "\n"s, add it to the last line and return
        return wrappedText;
      }
      //4 possibilities of text: short/long(ie. need to wrap), "\n" present in next width+1 chars/not
      //^shortNot (above)
      String line;
      boolean wordBreak = false;
      if(lengthIgnoringEscSeqs(text) <= width){ //short(Present):
        line = text.substring(0, text.indexOf("\n"));
      }else{                                  //long:
        line = substringIgnoringEscSequences(text, 0, width+1);          //line = Next [width+1] characters
        if(line.indexOf('\n') >= 0){         //longPresent
          line = line.substring(0, line.indexOf('\n'));
        }else if(line.indexOf(' ') >= 0){    //longNot
          line = line.substring(0, line.lastIndexOf(' '));
        }else{                                  //longNot; no (\n's or) spaces
          line = substringIgnoringEscSequences(line, 0, width-1) + "-";
          wordBreak = true;
        }
        //Used to be:
        // line = line.substring(0, minPositiveOf(line.lastIndexOf(" "), line.indexOf("\n")));        //Go up to final " " or first "\n" //(always cuts off at least 1 character)
      }
      wrappedText.add(line);
      int indexToCutOffAt = line.length() + 1;
      if(wordBreak){ indexToCutOffAt -= 2; }
      text = text.substring(indexToCutOffAt);
    }
    return wrappedText;
  }


  public static String removeEscSeqs(String input){
    return input.replaceAll("\u00D8([^\u00D8\u00C1]*)\u00C1", "");
  }
  public static boolean equalsSkipEscSeqs(String A, String B){
    return removeEscSeqs(A).equals(removeEscSeqs(B));
  }
  public static boolean equalsIgnoreCaseSkipEscSeqs(String A, String B){
    return removeEscSeqs(A).equalsIgnoreCase(removeEscSeqs(B));
  }

  /**Adds one String over the other at the specified index. Skips escape sequences that are marked with the O and A characters.
  *@Precondition Escape sequences in mainStr are preceded by 'Ø' and succeeded by 'Á'. The characters 'Ø' and 'Á' are not used in mainStr outside of escape sequences.
  */
  public static String addStringsSkipEscSequences(String mainStr, int indexToAdd, String toAdd){
    int col = 0;
    boolean inEscSequence = false;

    for(int i=0; i<mainStr.length(); i++){
      if(mainStr.charAt(i) == 'Ø'){ //Start of esc sequence
        inEscSequence = true;
        continue;
      }
      if(mainStr.charAt(i) == 'Á'){ //End of esc sequence
        inEscSequence = false;
        continue;
      }
      if(col == indexToAdd){
        return substringIgnoringEscSequences(mainStr, 0, col) + toAdd + substringIgnoringEscSequences(mainStr, col + lengthIgnoringEscSeqs(toAdd));
      }
      if(!inEscSequence){
        col++;
      }
    }

    String message = "index " + indexToAdd + " out of bounds for mainStr length " + lengthIgnoringEscSeqs(mainStr) + " and toAdd length " + lengthIgnoringEscSeqs(toAdd);
    throw new IndexOutOfBoundsException(message);
  }

  /**Adds one String over the other at the specified index. Skips escape sequences that are marked with the O and A characters. String added will start with color and ends with colorReset.
  *@Precondition Escape sequences in mainStr are preceded by 'Ø' and succeeded by 'Á'. The characters 'Ø' and 'Á' are not used in mainStr outside of escape sequences.
  */
  public static String addStringsSkipEscSequences(String mainStr, int indexToAdd, String toAdd, String color, String colorReset){
    int col = 0;
    boolean inEscSequence = false;

    for(int i=0; i<mainStr.length(); i++){
      if(mainStr.charAt(i) == 'Ø'){ //Start of esc sequence
        inEscSequence = true;
        continue;
      }
      if(mainStr.charAt(i) == 'Á'){ //End of esc sequence
        inEscSequence = false;
        continue;
      }
      if(col == indexToAdd){
        return substringIgnoringEscSequences(mainStr, 0, col) + color + toAdd + colorReset + substringIgnoringEscSequences(mainStr, col + lengthIgnoringEscSeqs(toAdd));
      }
      if(!inEscSequence){
        col++;
      }
    }

    String message = "index " + indexToAdd + " out of bounds for mainStr length " + lengthIgnoringEscSeqs(mainStr) + " and toAdd length " + lengthIgnoringEscSeqs(toAdd);
    throw new IndexOutOfBoundsException(message);
  }
  /**Adds one String over the other at the specified index. Skips escape sequences that are marked with the O and A characters. String added will start with color and ends with colorReset, them also being added appropriately around spaces
  *@Precondition Escape sequences in mainStr are preceded by 'Ø' and succeeded by 'Á'. The characters 'Ø' and 'Á' are not used in mainStr outside of escape sequences.
  */
  /*
  public static String addStringsTransparent(String mainStr, int indexToAdd, String toAdd, String color, String colorReset){
    int col = 0;
    boolean inEscSequence = false;

    for(int i=0; i<mainStr.length(); i++){
      if(mainStr.charAt(i) == 'Ø'){ //Start of esc sequence
        inEscSequence = true;
        continue;
      }
      if(mainStr.charAt(i) == 'Á'){ //End of esc sequence
        inEscSequence = false;
        continue;
      }
      if(col == indexToAdd+1){
        String toAddTransparent = toAdd;
        int nextSpaceIndex = toAddTransparent.indexOf(' ');
        //While there are any spaces in the string
        while(nextSpaceIndex != -1){
          String newChar = colorReset + mainStr.charAt(nextSpaceIndex)//Yeah this'll need an indexOfIgnoringEscSequences and I realized I don't actually need it soo..
          toAddTransparent = toAddTransparent.substring(0, nextSpaceIndex) + colorReset + toAddTransparent.substring(nextSpaceIndex);
          //While this string of spaces has not been fully replaced
          do{
            
          }while(toAddTransparent.charAt(nextSpaceIndex+1) == ' ');
        }
        return substringIgnoringEscSequences(mainStr, 0, col) + color + toAdd + colorReset + substringIgnoringEscSequences(mainStr, col + lengthIgnoringEscSeqs(toAdd));
      }
      if(!inEscSequence){
        col++;
      }
    }

    String message = "index " + indexToAdd + " out of bounds for mainStr length " + lengthIgnoringEscSeqs(mainStr) + " and toAdd length " + lengthIgnoringEscSeqs(toAdd);
    throw new IndexOutOfBoundsException(message);
  }*/
  
  /**Adds the String[] over the other starting at the specified indeces. Skips escape sequences that are marked with the O and A characters.
  *@Precondition Escape sequences in mainArr are preceded by 'Ø' and succeeded by 'Á'. The characters 'Ø' and 'Á' are not used in mainStr outside of escape sequences.
  */
  public static String[] addStringArraysSkipEscSequences(String[] mainArray, int topRow, int startCol, String[] newArray){
    String[] combined = mainArray.clone();
    for(int r=topRow; r<topRow+newArray.length; r++){
      String newBit = newArray[r-topRow]; //~~~debugging
      String originalRow = combined[r]; //~~~debugging
      combined[r] = addStringsSkipEscSequences(originalRow, startCol, newBit);
    }
    return combined;
  }
  /**Adds the String[] over the other starting at the specified indeces. Skips escape sequences that are marked with the O and A characters. color and colorReset are added at the start and end of each string, respectively
  *@Precondition Escape sequences in mainArr are preceded by 'Ø' and succeeded by 'Á'. The characters 'Ø' and 'Á' are not used in mainStr outside of escape sequences.
  */
  public static String[] addStringArraysSkipEscSequences(String[] mainArray, int topRow, int startCol, String[] newArray, String color, String colorReset){
    String[] combined = mainArray.clone();
    for(int r=topRow; r<topRow+newArray.length; r++){
      combined[r] = addStringsSkipEscSequences(combined[r], startCol, newArray[r-topRow], color, colorReset);
    }
    return combined;
  }
  /**Adds the String[] over the other starting at the specified indeces, not covering mainArray using spaces. Skips escape sequences that are marked with the O and A characters. color and colorReset are added at the start and end of each string, respectively, and at the end and start of any string of spaces (in newArray)
  *@Precondition Escape sequences in mainArr are preceded by 'Ø' and succeeded by 'Á'. The characters 'Ø' and 'Á' are not used in mainStr outside of escape sequences.
  */
  public static String[] addStringArraysTransparent(String[] mainArray, int topRow, int startCol, String[] newArray, String color, String colorReset){
    String[] combined = mainArray.clone();
    for(int r=topRow; r<topRow+newArray.length; r++){
      combined[r] = addStringsSkipEscSequences(combined[r], startCol, newArray[r-topRow], color, colorReset);
    }
    return combined;
  }

  /**Returns the length of the string ignoring esc sequences that have been marked with the O and A chars
  */
  public static int lengthIgnoringEscSeqs(String str){ 
    int length = 0;
    boolean inEscSequence = false;

    for(int i=0; i<str.length(); i++){
      if(str.charAt(i) == 'Ø'){ //Start of esc sequence
        inEscSequence = true;
        continue;
      }
      if(str.charAt(i) == 'Á'){ //End of esc sequence
        inEscSequence = false;
        continue;
      }
      if(!inEscSequence){
        length++;
      }
    }
    return length;
  }

  /**Returns the substring of the string starting at the entered index ignoring esc sequences that have been marked with the O and A chars
  */
  public static String substringIgnoringEscSequences(String str, int start){
    int col = 0;
    boolean inEscSequence = false;

    for(int i=0; i<str.length(); i++){
      if(str.charAt(i) == 'Ø'){ //Start of esc sequence
        inEscSequence = true;
        continue;
      }
      if(str.charAt(i) == 'Á'){ //End of esc sequence
        inEscSequence = false;
        continue;
      }
      if(!inEscSequence){
        if(col == start){
          if(i!=0 && str.charAt(i-1)=='Á'){
            while(!(str.charAt(i)=='Ø' && str.charAt(i-1)!='Á')){
              //Backs up start index so that it includes esc sequences on the border
              i--;
              if(i==0){
                break;
              }
            }
          }
          return str.substring(i);
        }
        col++;
      }
    }
    return "";
  }
  /**Returns the substring of the string from the start index to the end index, ignoring esc sequences that have been marked with the O and A chars
  */
  public static String substringIgnoringEscSequences(String str, int start, int end){
    //Traverse ignoring esc sequences until col==start. Save that actual index (i), and then traverse ignoring esc sequences until col==end. Return substring between actual indexes
    
    if(end < start){
      System.out.println("sIES: End < Start");
    }
    if(end == lengthIgnoringEscSeqs(str)){
      return substringIgnoringEscSequences(str, start);
    }
    
    int col = 0;
    boolean inEscSequence = false;
    int actualStartIndex = 0;
    int actualEndIndex;

    for(int i=0; i<str.length(); i++){
      if(str.charAt(i) == 'Ø'){ //Start of esc sequence
        inEscSequence = true;
        continue;
      }
      if(str.charAt(i) == 'Á'){ //End of esc sequence
        inEscSequence = false;
        continue;
      }
      if(!inEscSequence){                                               //if not in escape sequence:
        if(col == start){
          actualStartIndex = i;                                               //If col is equal to start, save i
        }else if(col == end){
          actualEndIndex = i;
          if(actualStartIndex!=0 && str.charAt(actualStartIndex-1)=='Á'){
            while(!(str.charAt(actualStartIndex)=='Ø' && str.charAt(actualStartIndex-1)!='Á')){
              //Backs up start index so that it includes esc sequences on the border
              actualStartIndex--;
              if(actualStartIndex == 0){
                break;
              }
            }
          }
          return str.substring(actualStartIndex, actualEndIndex);             //If col is equal to end, return the substring from the start to the end
        }
        col++;                                                                //Increment col
      }
    }
    //TODO: Remove the part of that (v) comment about it being recently added (eventually)
    App.ASSERT(false); //<Recently added condition; shouldn't be returning in this failure case I believe.
    return "";
  }
  
  //Originally was: //also made the same change to col++; in the other sIES method //TODO: remove eventually
  // /**Returns the substring of the string from the start index to the end index, ignoring esc sequences that have been marked with the O and A chars
  // */
  // public static String substringIgnoringEscSequences(String str, int start, int end){
  //   //Traverse ignoring esc sequences until col==start. Save that actual index (i), and then traverse ignoring esc sequences until col==end. Return substring between actual indexes
    
  //   if(end < start){
  //     System.out.println("sIES: End < Start");
  //   }
  //   int col = 0;
  //   boolean inEscSequence = false;
  //   int actualStartIndex = 0;
  //   int actualEndIndex;

  //   for(int i=0; i<str.length(); i++){
  //     if(str.charAt(i) == 'Ø'){ //Start of esc sequence
  //       inEscSequence = true;
  //       continue;
  //     }
  //     if(str.charAt(i) == 'Á'){ //End of esc sequence
  //       inEscSequence = false;
  //       continue;
  //     }
  //     if(!inEscSequence){                                               //if not in escape sequence:
  //       col++;                                                                //Increment col
  //       if(col == start){
  //         actualStartIndex = i;                                               //If col is equal to start, save i
  //       }else if(col == end){
  //         actualEndIndex = i;
  //         if(actualStartIndex!=0 && str.charAt(actualStartIndex-1)=='Á'){
  //           while(!(str.charAt(actualStartIndex)=='Ø' && str.charAt(actualStartIndex-1)!='Á')){
  //             //Backs up start index so that it includes esc sequences on the border
  //             actualEndIndex--;
  //             Str.println("ASI: " + actualStartIndex);
  //             if(actualStartIndex == 0){
  //               break;
  //             }
  //           }
  //         }
  //         return str.substring(actualStartIndex, actualEndIndex);             //If col is equal to end, return the substring from the start to the end
  //       }
  //     }
  //   }
  //   return "";
  // }

  /**Returns the last character in string, skipping escape sequences. Returns '\0' if str.isEmpty(). */
  public static char lastCharIgnoringEscSeqs(String str){
    if(str.isEmpty()){ return '\0'; }

    char lastChar = str.charAt(str.length()-1);
    while(lastChar == 'Á'){
      int LCIndex = str.lastIndexOf('Ø') - 1;
      str = str.substring(0, LCIndex+1);
      lastChar = str.charAt(str.length()-1);
    }
    return lastChar;
  }

  
  /**Evens spaces on either side of the text. If uneven, puts the extra space on the right side unless the string ends with a comma or period (then the space is added to the left)
  */
  public static String centerText(String string){
    String line = string;
    String text = line.trim();
    int whiteSpace = line.length() - text.length();
    String centeredText = "";
    for(int i=0; i<whiteSpace/2; i++){ //Half(rounded down) of the spaces go to start
      centeredText += " ";
    }
    centeredText += text;
    for(int i=0; i<(whiteSpace+1)/2; i++){ //Half(rounded up) of the spaces go to end
      centeredText += " ";
    }
    // String lastChar = substringIgnoringEscSequences(text, lengthIgnoringEscSeqs(text) -1);
    char lastChar = lastCharIgnoringEscSeqs(text);
    if((lastChar == ',' || lastChar == '.') && whiteSpace%2 == 1){
      centeredText = " " + centeredText.substring(0, centeredText.length()-1);
    } //^If ends with a "," or a "." and whitespace is odd, moves the extra space to the start of the string
    return centeredText;
  }

  /**Short for Concatenate ArrayList
   * @return The Strings in list concatenated (index 0 first)
   */
  public static String concatArrayList(ArrayList<String> list){
    String res = "";
    for(String s : list){
      res += s;
    }
    return res;
  }

  /**Short for Concatenate ArrayList with New Line(s)
   * @return The Strings in list concatenated (index 0 first) with a '\n' character between them
   * (includes a '\n' after the final element)
   */
  public static String concatArrayListWNL(ArrayList<String> list){
    String res = "";
    for(String s : list){
      res += s + '\n';
    }
    return res;
  }

  /**Calls System.out.print() on the string, removing all special O and A characters
  */
  public static void print(String string){
    String str = string;
    //Remove Øs
    int indexOfO = str.indexOf('Ø');
    while (indexOfO != -1){
      str = str.substring(0,indexOfO) + str.substring(indexOfO+1);
      indexOfO = str.indexOf('Ø');
    }
    //Remove Ás
    int indexOfA = str.indexOf('Á');
    while (indexOfA != -1){
      str = str.substring(0,indexOfA) + str.substring(indexOfA+1);
      indexOfA = str.indexOf('Á');
    }
    //Print
    System.out.print(str);
  }
  /**Calls System.out.println() on the string, removing all special O and A characters
  */
  public static void println(String string){
    String str = string;
    //Remove Øs
    int indexOfO = str.indexOf('Ø');
    while (indexOfO != -1){
      str = str.substring(0,indexOfO) + str.substring(indexOfO+1);
      indexOfO = str.indexOf('Ø');
    }
    //Remove Ás
    int indexOfA = str.indexOf('Á');
    while (indexOfA != -1){
      str = str.substring(0,indexOfA) + str.substring(indexOfA+1);
      indexOfA = str.indexOf('Á');
    }
    //Print
    System.out.println(str);
  }

  /**Returns a string composed of toRepeat repeated numRepeats times
  */
  public static String repeatChar(char toRepeat, int numRepeats){
    String str = "";
    for(int i=0; i < numRepeats; i++){
      str += toRepeat;
    }
    return str;
  }
  /**Returns a string composed of toRepeat repeated numRepeats times
  */
  public static String repeatStr(String toRepeat, int numRepeats){
    String str = "";
    for(int i=0; i < numRepeats; i++){
      str += toRepeat;
    }
    return str;
  }
  
  /**Returns the lower of the two values
  */
  public static int minOf(int a, int b){
    //idk why some people hate these things they're really nice imo -Z
    //I guess some people haven't had to learn new things in a while and aren't used to it. And this is a pretty simple example, it could get much worse.
    return (a<b)? a : b;
  }
  /**Returns the lower positive number of the two values. If neither are positive, returns -1.
  */
  public static int minPositiveOf(int a, int b){
    if(a<0 && b<0){ //Both negative
      return -1;
    }
    if(a<0 || b<0){ //One negative
      return (a>b)? a : b; //Max of two
    }
    return (a<b)? a : b; //Min of two
  }
}