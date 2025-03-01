// Name: Brandon Han
// USC NetID: hanbrand
// CS 455 PA3
// Spring 2024


/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because
  it's what the user can see about the minefield). Client can call getStatus(row, col) for any 
  square.  It actually has data about the whole current state of the game, including the underlying
  mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), 
  isGameOver().  It also has mutators related to actions the player could do (resetGameDisplay(),
  cycleGuess(), uncover()), and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms the Model for the
  game application, whereas GameBoardPanel is the View and Controller in the MVC design pattern.  It
  contains the MineField that it's partially displaying.  That MineField can be accessed
  (or modified) from outside this class via the getMineField accessor.  
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus values [0,8] mentioned in comments below) are the
   // possible states of one location (a "square") in the visible field (all are values that can be
   // returned by public method getStatus(row, col)).
   
   // The following are the covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // The following are the uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this opened square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already
                                          // (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of
                                                  // losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused
                                                 // you to lose)
   // ----------------------------------------------------------   

   /** Representation Invariant:
    *  mineField array must not be null and represents the underlying minefield associated with this visiblefield.
    *  squareStatus must not be null and must always be in a valid state (covered, mine-guess, question, exploded mine,
    *  unopened) as defined by the constants above.
    */

   // <put instance variables here>
   private MineField mineField;
   private int[][] squareStatus;


   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the locations covered, no mines guessed, and the game not
      over.
      @param mineField  the minefield to use for for this VisibleField
    */
   public VisibleField(MineField mineField) {
      this.mineField = mineField;

      // Initialize squareStatus array to match dimensions of the mineField array
      squareStatus = new int[mineField.numRows()][mineField.numCols()];

      // Reset the states of all squares to COVERED
      resetGameDisplay();
   }
   
   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */     
   public void resetGameDisplay() {
      // Iterate through all the array locations and set squareStatus to COVERED.
      for (int row = 0; row < mineField.numRows(); row++) {
         for (int col = 0; col < mineField.numCols(); col++) {
            squareStatus[row][col] = COVERED;
         }
      }
   }
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
   public MineField getMineField() {
      return mineField;
   }
   
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the
            beginning of the class for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      return squareStatus[row][col];
   }

   
   /**
      Returns the the number of mines left to guess.  This has nothing to do with whether the mines
      guessed are correct or not.  Just gives the user an indication of how many more mines the user
      might want to guess.  This value will be negative if they have guessed more than the number of
      mines in the minefield.     
      @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      int mineGuesses = 0;

      // Iterate through all array positions and checks if any squares are marked as MINE_GUESS. If so, increment counter.
      for (int row = 0; row < mineField.numRows(); row++) {
         for (int col = 0; col < mineField.numCols(); col++) {
            if (squareStatus[row][col] == MINE_GUESS) {
               mineGuesses++;
            }
         }
      }
      return mineField.numMines() - mineGuesses;
   }
 
   
   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on
      a COVERED square changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to
      QUESTION;  call on a QUESTION square changes it to COVERED again; call on an uncovered square
      has no effect.  
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {

      // Update squareStatus and cycle through the possible square states/statuses as defined by the game rules.
      // If square is COVERED, cycle to MINE_GUESS. If MINE_GUESS, then cycle to QUESTION. If QUESTION, cycle back to COVERED
      if (squareStatus[row][col] == COVERED) {
         squareStatus[row][col] = MINE_GUESS;
      } else if (squareStatus[row][col] == MINE_GUESS) {
         squareStatus[row][col] = QUESTION;
      } else if (squareStatus[row][col] == QUESTION) {
         squareStatus[row][col] = COVERED;
      }
   }

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in the
      neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form (possibly along with
      parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {
      // If square location clicked has a mine, set squareStatus to EXPLODED_MINE and return false.
      if (mineField.hasMine(row, col)) {
         squareStatus[row][col] = EXPLODED_MINE;
         return false;
      }

      // Uncover the square clicked, and if applicable, all other surrounding squares with no adjacent mines.
      uncoverSurrounding(row, col);
      checkAndMarkAllMinesIfWon(); // Check if the mine clicked is the last remaining non-mine square.
      return true;
   }
 
   
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game has ended
    */
   public boolean isGameOver() {
      boolean mineExploded = false;
      int mineSquares = 0;

      // Iterate through all array locations to check if any squareStatus is EXPLODED_MINE. If so, game is over.
      // Also iterate through all array locations to count covered squares, mine guesses, and questions.
      // If count matches the number of mines, then all mine locations have been discovered and game is over.
      for (int row = 0; row < mineField.numRows(); row++) {
         for (int col = 0; col < mineField.numCols(); col++) {
            if (squareStatus[row][col] == EXPLODED_MINE) {
               mineExploded = true;
            } else if (squareStatus[row][col] == COVERED || squareStatus[row][col] == MINE_GUESS || squareStatus[row][col] == QUESTION) {
               mineSquares++;
            }
         }
      }
      return mineExploded || (mineSquares == mineField.numMines()); // Game is over if mine exploded or all mines found.
   }
 
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      // If squareStatus is not COVERED, MINE_GUESS, or QUESTION, the value must be > -1 (i.e. not covered)
      if (squareStatus[row][col] > COVERED) {
         return true;
      }
      else {
         return false;
      }
   }
   
 
   // <put private methods here>

   /** Uncovers the square selected only if the square is in a valid range and only if it is still in the COVERED state.
    * Uncover the square selected by changing it's state to represent the number of adjacent mines.
    * If there are no adjacent mines then continue to uncover surrounding squares.
    * @param row of the square
    * @param col of the square
    */
   private void uncoverSurrounding(int row, int col) {
      // Check of the square is in range. If square is not in range or not covered, don't do anything.
      if (!mineField.inRange(row,col) || squareStatus[row][col] != COVERED) {
         return;
      }

      // Uncover the squareStatus and show the number of adjacent mines.
      int adjacentMines = mineField.numAdjacentMines(row, col);
      squareStatus[row][col] = adjacentMines;

      // If there are no adjacent mines, continue to uncover neighboring squares (within -1 & +1 rows and columns)
      if (adjacentMines == 0) {
         for (int surrRow = -1; surrRow <= 1; surrRow++) {
            for (int surrCol = -1; surrCol <= 1; surrCol++) {
               if (surrRow != 0 || surrCol != 0) { // Avoid uncovering the square selected/clicked a second time.
                  uncoverSurrounding(row + surrRow, col +surrCol);
               }
            }
         }
      }
   }

   /** Checks if the game is won by checking if all non-mine squares have been uncovered or marked as MINE_GUESS or
    * marked as QUESTION.
    */
   private void checkAndMarkAllMinesIfWon() {
      boolean won = true;

      // Iterate through entire array
      for (int row = 0; row < mineField.numRows(); row++) {
         for (int col = 0; col < mineField.numCols(); col++) {
            // Checks for a non-mine cell that is still covered. If so the game is not won.
            if (!mineField.hasMine(row, col) && !isUncovered(row, col)) {
               won = false;
               break;
            }
         }
      }

      // If won, mark all un-guessed/covered mines as MINE_GUESS
      if (won) {
         for (int row = 0; row < mineField.numRows(); row++) {
            for (int col = 0; col < mineField.numCols(); col++) {
               if (mineField.hasMine(row, col) && squareStatus[row][col] == COVERED) {
                  squareStatus[row][col] = MINE_GUESS;
               }
            }
         }
      }
   }

}
