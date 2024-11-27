Recreating Slay the Spire in the Terminal using entire Java.
Intended for the VSCode Terminal with the settings included in vscode/settings.json
Created by Zachary Heintzleman. Originally an AP Computer Science A final project, which I have
since expanded on. All code written completely by myself -- no AI or copy-pasting code from Stack
Overflow without knowing what it does. Not currently using any libraries, either (excluding the base java package) -- all display methods, ANSI escape sequences, file IO, etc. done by me for practice purposes. Pixel art made by me as well, based directly on the real Slay the Spire art.
(Large text on intro and death screens the only thing not made by me -- see App.java.)

Utilizes all of the AP Java topics (Polymorphism, Exception Handling,
etc.), along with more advanced features such as the Java Collection framework, Streams, File IO,
and Javadoc Comments.


To run in VSCode (or similar IDEs):
Open the IDE to the Slay-the-Spire directory.
Select any of the .java files, and click the play button in the top-right corner.

To run in a terminal:
From the Slay-the-Steeple directory, run:
$ javac -d tmp app/Main.java
$ java -cp tmp app/Main

If the screen is covered in a mess of ←[0m's, your terminal unfortunately does not support ANSI escape sequences by default. you can disable them via the following commands (entering a new line after each), but this removes all color, and prevents the screen from clearing before each frame:
-Press enter
-Type esc
-Type colors
-Type false

Then zoom in/out (and adjust screen width/height in the esc panel) as desired.