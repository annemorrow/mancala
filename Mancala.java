import java.awt.Color;
import java.util.*;

public class Mancala {

  private Hole holeA;
  private Hole holeB;
  private Hole holeC;
  private Hole holeD;
  private Hole holeE;
  private Hole holeF;
  private Hole holeG; // first player's storage
  private Hole holeH;
  private Hole holeI;
  private Hole holeJ;
  private Hole holeK;
  private Hole holeL;
  private Hole holeM;
  private Hole holeN; // second player's storage
  
  private Player player1;
  private Player player2;
  private Player currentPlayer;
  private boolean gameOver;
  private Player winner;
  
  private Stack helperHand;
  
  private Mancala(String name1) {
    // set players (one is computer)
    player1 = new Player(name1);
    player2 = new Player("computer");
    initialSetup();
  }
  
  private Mancala(String name1, String name2) {
    // set players
    player1 = new Player(name1);
    player2 = new Player(name2);
    initialSetup();
  }

  private static class Marble {
    private Color marbleColor; // Sedgwick object
    private Marble() {
      // use Sedgwick random number to generate color
      float r = (float) StdRandom.uniform(0.0, 0.1);
      float g = (float) StdRandom.uniform(0.0, 0.9);
      float b = (float) StdRandom.uniform(0.0, 0.6);
      float a = (float) 0.5;
      marbleColor = new Color(r, g, b, a);
    }
  }
  
  private class Hole {
    private Stack marblesInHole;
    private boolean storage;  // end hole
    private Player whoseSide;  // whose side is it on so they can select it (or store in it)
    private Hole nextHole;
    
    private double centerx;
    private double centery;
    
    private Hole(boolean endHole, Player whichSide) {
      marblesInHole = new Stack();
      storage = endHole;
      whoseSide = whichSide;
    }
    
    private boolean containsPoint(double x, double y) {
      double dx = x - this.centerx;
      double dy = y - this.centery;
      double dsquared = dx * dx + dy * dy;
      return (dsquared < 2500);
    }
  }
  
  private class Player {
    private String name;
    private Stack marblesInHand;
    // Points or winning status
    
    private Player(String giveName) {
      name = giveName;
      marblesInHand = new Stack();
    }
  }
  
  private void holeStructure() {
    holeA = new Hole(false, player1);
    holeB = new Hole(false, player1);
    holeC = new Hole(false, player1);
    holeD = new Hole(false, player1);
    holeE = new Hole(false, player1);
    holeF = new Hole(false, player1);
    holeG = new Hole(true, player1);
    holeH = new Hole(false, player2);
    holeI = new Hole(false, player2);
    holeJ = new Hole(false, player2);
    holeK = new Hole(false, player2);
    holeL = new Hole(false, player2);
    holeM = new Hole(false, player2);
    holeN = new Hole(true, player2);
    holeA.nextHole = holeB;
    holeB.nextHole = holeC;
    holeC.nextHole = holeD;
    holeD.nextHole = holeE;
    holeE.nextHole = holeF;
    holeF.nextHole = holeG;
    holeG.nextHole = holeH;
    holeH.nextHole = holeI;
    holeI.nextHole = holeJ;
    holeJ.nextHole = holeK;
    holeK.nextHole = holeL;
    holeL.nextHole = holeM;
    holeM.nextHole = holeN;
    holeN.nextHole = holeA;
  }
  
  private void initialMarbles() {
    int numberOfFilledHoles = 0;
    Hole current = holeA;
    while (numberOfFilledHoles < 12) {
      if (current.storage) current = current.nextHole;
      fourInitialMarbles(current);
      current = current.nextHole;
      numberOfFilledHoles++;
    }
  }
  
  private void fourInitialMarbles(Hole hole) {
      for (int i = 0; i < 4; i++) {
        Marble freshMarble = new Marble();
        hole.marblesInHole.push(freshMarble);
      }
    }
  
  private void initialSetup() {
    helperHand = new Stack();
    gameOver = false;
    holeStructure();
    initialMarbles();
  }
  
  private Hole playerChoice(Player player) {
    while(true) {
      Hole choice = clickHole();
      if (choice.whoseSide == player) return choice;
    }
  }
  
  private Hole move(Player player, Hole choice) {
    // return last hole
    emptyHole(choice);
    player.marblesInHand = choice.marblesInHole;
    choice.marblesInHole = new Stack();
    Hole current = choice;
    while(!player.marblesInHand.isEmpty()) {
      current = current.nextHole;
      if (current.storage && current.whoseSide != player) current = current.nextHole;
      dropMarble(current, player.marblesInHand);
    }
    return current;
  }
  
  private boolean cycle(Player player, Hole start) { // returns true if player gets another turn
    Hole landingHole = move(player, start);
    if (landingHole.storage) {
      return true;
    } else if(landingHole.marblesInHole.size() == 1) {
      return false;
    } else {
      return cycle(player, landingHole);
    }
  }
  
  private void turn(Player player) {
    Hole choice = playerChoice(player);
    boolean anotherGo = cycle(player, choice);
    if(anotherGo) {
      turn(player);
    }
  }
  
  private static Mancala getPlayerInfo() {
    Mancala game = new Mancala("Player 1", "Player 2");
    StdOut.println("How many players? Type 1 or 2");
    int num = StdIn.readInt();
    if (num == 1) {
      StdOut.println("What is your name?");
      String name = StdIn.readString();
      game = new Mancala(name);
    }
    else if (num == 2) {
      StdOut.println("What is the first player's name?");
      String name1 = StdIn.readString();
      StdOut.println("What is the second player's name?");
      String name2 = StdIn.readString();
      game = new Mancala(name1, name2);
    }
    else {
      StdOut.println("Please choose an appropriate number of players.");
      getPlayerInfo();
    }
    return game;
  }
  
  
  private Hole clickHole() {
    double x = 0.0;
    double y = 0.0;
    while(true) {
      if (StdDraw.mousePressed()) {
        x = StdDraw.mouseX();
        y = StdDraw.mouseY();
        Hole current = holeA;
        for (int i = 0; i < 14; i++) {
          if (current.containsPoint(x, y) && !current.storage) return current;
          current = current.nextHole;
        }
      }
    }
  }
  
  private void clickHoleTest() {
    Hole clicked = clickHole();
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.circle(clicked.centerx, clicked.centery, 50.0);
  }
  
  private void dropMarble(Hole hole, Stack held) {  // what if set from other equal method
    Marble marble = (Marble) held.pop();
    hole.marblesInHole.push(marble);
    dropMarble(hole, marble); // visualize
  }
  
  private void switchPlayer() {
    if (currentPlayer == player1) currentPlayer = player2;
    else currentPlayer = player1;
    switchPlayerDisplay();
  }
  
  private boolean wins(Player player) {
    Hole current;
    if (player == player1) {
      current = holeA;
    } else {
      current = holeH;
    }
    for (int i = 0; i < 6; i++) {
      if (current.marblesInHole.size() > 0) return false;
      current = current.nextHole;
    }
    return true;
  }
  
  /* DISPLAY METHODS BELOW */
  
  private void switchPlayerDisplay() {
    if (currentPlayer == player1) {
      StdDraw.setPenColor(StdDraw.WHITE);
      StdDraw.filledRectangle(500.0, 375.0, 25.0, 25.0);
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(500.0, 25.0, player1.name);
    } else {
      StdDraw.setPenColor(StdDraw.WHITE);
      StdDraw.filledRectangle(500.0, 25.0, 25.0, 25.0);
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(500.0, 375.0, player2.name);
    }
    StdDraw.show();
  }
  
  private void drawBoard() {
    StdDraw.setCanvasSize(1000, 400);
    Double bottomRowHeight = 100.0;
    Double topRowHeight = 300.0;
    Double holeRadius = 50.0;
    StdDraw.setXscale(0.0, 1000);
    StdDraw.setYscale(0.0, 400);
    double leftjustify = 100.0;
    double spacing = 110.0;
    holeN.centerx = leftjustify;
    holeN.centery = 300.0;
    // center bottom row of holes and draw
    Hole current = holeA;
    for (int i = 1; i <= 6; i++) {
      current.centerx = leftjustify + i * spacing;
      current.centery = bottomRowHeight;
      StdDraw.circle(current.centerx, current.centery, holeRadius);
      current = current.nextHole;
    }
    // center top row of holes and draw
    current = holeH;
    for (int j = 6; j>=1; j--) {
      current.centerx = leftjustify + j * spacing;
      current.centery = topRowHeight;
      StdDraw.circle(current.centerx, current.centery, holeRadius);
      current = current.nextHole;
    }
    // center and draw storage holes
    StdDraw.setPenColor(StdDraw.BLACK);
    holeG.centerx = leftjustify + 7 * spacing;
    holeG.centery = 200.0;
    StdDraw.ellipse(holeG.centerx, holeG.centery, 50.0, 150.0);
    StdDraw.show();
    holeN.centerx = leftjustify;
    holeN.centery = 200.0;
    StdDraw.ellipse(holeN.centerx, holeN.centery, 50.0, 150.0);
    
    visibleMarbles();
  }
  
  private void emptyHole(Hole hole) {
    if (!hole.storage) {
      StdDraw.setPenColor(StdDraw.WHITE);
      StdDraw.filledCircle(hole.centerx, hole.centery, 50.0);
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.circle(hole.centerx, hole.centery, 50.0);
    }
  }
  
  private void dropMarble(Hole hole, Marble marble) {
    double centerx = StdRandom.uniform(-30.0, 30.0) + hole.centerx;
    double centery = StdRandom.uniform(-30.0, 30.0) + hole.centery;
    // change color to marble
    StdDraw.setPenColor(marble.marbleColor);
    StdDraw.filledCircle(centerx, centery, 10.0);
    StdDraw.show(200);
  }
  
  private void visibleMarbles() {
    Hole current = holeA;
    for (int holesShown = 0; holesShown < 14; holesShown++) {
      visibleMarbles(current);
      current = current.nextHole;
    }
  }
  
  private void visibleMarbles(Hole hole) {
    while(!hole.marblesInHole.isEmpty()) {
      helperHand.push(hole.marblesInHole.pop());
    }
    while(!helperHand.isEmpty()) {
      Marble marble = (Marble) helperHand.pop();
      dropMarble(hole, marble);
      hole.marblesInHole.push(marble);
    }
  }
  
  public static void main(String[] args) {
    // Mancala game = getPlayerInfo();
    Mancala game = new Mancala("Anne", "Zak");
    game.initialSetup();
    game.drawBoard();
    game.currentPlayer = game.player1;
    game.switchPlayerDisplay();
    while(!game.gameOver) {
      game.turn(game.currentPlayer);
      if (game.wins(game.currentPlayer)) {
        StdOut.println(game.currentPlayer.name + "wins!");
        game.gameOver = true;
        StdDraw.text(500.0, 200.0, game.currentPlayer.name + " wins!");
        StdDraw.show();
      } else {
        game.switchPlayer();
      }
    }
  }

}