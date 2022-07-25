/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.align.sequence;

/**
 *
 * @author anil
 */
public class SequenceCell {
   private SequenceCell prevSequenceCell;
   private int score;
   private int row;
   private int col;

   public SequenceCell(int row, int col) {
      this.row = row;
      this.col = col;
   }

   /**
    * @param score
    *           the score to set
    */
   public void setScore(int score) {
      this.score = score;
   }

   /**
    * @return the score
    */
   public int getScore() {
      return score;
   }

   /**
    * @param prevSequenceCell
    *           the prevSequenceCell to set
    */
   public void setPrevCell(SequenceCell prevSequenceCell) {
      this.prevSequenceCell = prevSequenceCell;
   }

   /**
    * @return the row
    */
   public int getRow() {
      return row;
   }

   /**
    * @return the col
    */
   public int getCol() {
      return col;
   }

   /**
    * @return the prevSequenceCell
    */
   public SequenceCell getPrevCell() {
      return prevSequenceCell;
   }

   /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "SequenceCell(" + row + ", " + col + "): score=" + score + ", prevSequenceCell="
            + prevSequenceCell + "]";
   }
}
