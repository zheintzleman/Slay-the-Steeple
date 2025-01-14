Recreated the Slay the Spire combat system in the Terminal using Java & its built-in libraries.
Intended for the VSCode Terminal with the settings included in vscode/settings.json. (Runs in any terminal, but graphics quality may differ depending on how it displays text and ANSI escape sequences; see below.)
Created by Zachary Heintzleman. Originally an AP Computer Science A final project, which I have since significantly expanded on. All code written completely by myself -- no AI or copy-pasting code from Stack Overflow without knowing what it does. Not currently using any libraries, either (excluding the base java package) -- all display methods, systems, file IO, etc. done by myself. Pixel art made by me as well, based directly on the real Slay the Spire art.
(Large text on intro and death screens the only thing not made by me -- see App.java.)

Utilizes all of the AP Java topics (Polymorphism, Exception Handling, etc.), along with more advanced features such as the Java Collection framework, Streams, File IO and Javadoc Comments.

With cheats enabled, use "/killall" (or /ka) to kill all enemies, and "/fixdeck" or "/deck" to replace your deck with one of every card. Rules and controls explained in the game; have fun!


To run in VSCode (or similar IDEs):
Open the IDE to the Slay-the-Steeple directory.
Select any of the .java files, and click the play button in the top-right corner.
Alternatively, open the integrated terminal using Ctrl+`, make it full-screen, and do the next option:

To run in a terminal:
From the Slay-the-Steeple directory, run:
java -cp class app/Main
To re-compile the source code (shouldn't be necessary), run:
javac -d class app/Main.java

If the screen is covered in a mess of ←[0m's, your terminal unfortunately does not support ANSI escape sequences by default. You can disable them in this project by typing the following commands in the game (entering a new line after each), but this removes all color and prevents the screen from clearing before each frame:
-Press enter
-Type esc
-Type colors
-Type false

Then zoom in/out (and adjust screen width/height in the in-game esc panel) as desired.
