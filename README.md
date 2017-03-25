# ttr-board-generator
A playable implementation of the board game Ticket to Ride, which has the option to generate a novel board with each play.


# Playing the Game
To run the jar file located in desktop/build/libs/desktop-1.0.jar, use java -jar <file>

To import project to Intellij, select Import Project, and select the gradle.build file. Then on the next prompt, uncheck "Create separate module per source set"

To run from Intellij, run the desktop/src/com/csc570/rsmith/desktop/DesktopLauncher.java class with its main method.

To edit GA parameters, the core/src/com/csc570/rsmith/boardgenerator/genetic/GeneticAlgorithmDriver.java file contains constants that can be edited.

To save and import seeds for boards, the core/src/com/csc570/rsmith/graphics/TTRMainScreen.java file contains the method "getNewBoard()". Modifying this (by uncommenting code that is present for importing files or the code in runGA() for saving the seeds) will allow you to import and export seeds.
