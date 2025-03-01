// Name: Brandon Han
// USC NetID: hanbrand
// CS 455 PA3
// Spring 2024

import java.util.Random;

/** 
   MineField
      Class with locations of mines for a minesweeper game.
      This class is mutable, because we sometimes need to change it once it's created.
      Mutators: populateMineField, resetEmpty
      Includes convenience method to tell the number of mines adjacent to a location.
 */
public class MineField {

    /** minefield must not be null and is a 2D boolean array representing the presence (true or false) of mines.
     * Both numRows and numCols must be > 0 and rectangular as it represents dimensions of the minefield.
     * numMines must be >= 0 and represents the total number of mines on the minefield.
     */

    // <put instance variables here>
   private boolean[][] minefield;
   private int numRows;
   private int numCols;
   private int numMines;
   
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in
      the array such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice
      versa.  numMines() for this minefield will corresponds to the number of 'true' values in 
      mineData.
      @param mineData  the data for the mines; must have at least one row and one col,
                       and must be rectangular (i.e., every row is the same length)
    */
   public MineField(boolean[][] mineData) {
       // Initialize minefield with dimensions from mineData
       numRows = mineData.length; // Extract numRows from the array
       numCols = mineData[0].length; // Extracts the numCols based on the first row in the array.
       minefield = new boolean[numRows][numCols];

       // Iterate through the entire array and populate minefield as defined by mineData
       numMines = 0;
       for (int row = 0; row < numRows; row++) {
           for (int col = 0; col < numCols; col++) {
               minefield[row][col]= mineData[row][col];
               if (mineData[row][col]) {
                   numMines++;  // If mineData contains a mine/true, increment counter
               }
           }
       }
   }
   
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a 
      MineField, numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
   public MineField(int numRows, int numCols, int numMines) {
      this.numRows = numRows;
      this.numCols = numCols;
      this.numMines = numMines;
      this.minefield = new boolean[numRows][numCols];
   }
   

   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on
      the minefield, ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col) and numMines() < (1/3 * numRows() * numCols())
    */
   public void populateMineField(int row, int col) {
       // Remove any current mines on the minefield
       resetEmpty();

       // Randomly place mines until number of desired mines is reached
       int placedMines = 0;
       Random random = new Random(); // Create random number generator to select random locations for mines within minefield dimensions
       while (placedMines < numMines) {
           int rowLoc = random.nextInt(minefield.length);
           int colLoc = random.nextInt(minefield[0].length);

           // Do not place mine if it is first square clicked or if there is a mine present already
           if (rowLoc == row && colLoc == col || minefield[rowLoc][colLoc]) {
               continue;
           }

           minefield[rowLoc][colLoc] = true;
           placedMines++;
       }
   }

   
   
   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or
      numCols().  Thus, after this call, the actual number of mines in the minefield does not match
      numMines().  
      Note: This is the state a minefield created with the three-arg constructor is in at the 
      beginning of a game.
    */
   public void resetEmpty() {
       // Iterate through the entire array and set all values to false/empty squares
      for (int row = 0; row < numRows; row++) {
          for (int col = 0; col < numCols; col++) {
              minefield[row][col] = false;
          }
      }
   }

   
  /**
     Returns the number of mines adjacent to the specified location (not counting a possible 
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
   public int numAdjacentMines(int row, int col) {
       int count = 0;

       // Loop through blocks surrounding (-1 and +1 col and row) specified location at (row, col)
       for (int i = row - 1; i <= row + 1; i++) {
           for (int j = col - 1; j <= col + 1; j++) {
               // Increment mine count everywhere other than the specified location
               if (inRange(i,j)) { // First, confirm that the location is within the array range
                   if (!(i == row && j == col)) { // Skip the specified/provided location
                       if (minefield[i][j]) {
                           count++;
                       }
                   }
               }
           }
       }
       return count;
   }
   
   
   /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
   public boolean inRange(int row, int col) {
       // Check if field location is within defined rows
       boolean isValidRow = row >= 0 && row < minefield.length;
       // Check if field location is within defined cols
       boolean isValidCol = col >= 0 && col < minefield[0].length;

       if (isValidRow && isValidCol) {
           return true;
       }
       else {
           return false;
       }
   }
   
   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */  
   public int numRows() {
      return numRows;
   }
   
   
   /**
      Returns the number of columns in the field.
      @return number of columns in the field
   */    
   public int numCols() {
      return numCols;
   }
   
   
   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
   public boolean hasMine(int row, int col) {
      return minefield[row][col];
   }
   
   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg
      constructor, some of the time this value does not match the actual number of mines currently
      on the field.  See doc for that constructor, resetEmpty, and populateMineField for more
      details.
      @return number of mines
    */
   public int numMines() {
       return numMines;
   }
   
   // <put private methods here>
}

