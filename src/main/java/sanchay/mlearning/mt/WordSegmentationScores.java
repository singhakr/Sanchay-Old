/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

/**
 *
 * @author anil
 */
public class WordSegmentationScores {
    protected double segmentationScore;

    public WordSegmentationScores()
    {
        
    }

    /**
     * @return the segmentationScore
     */
    public double getSegmentationScore() {
        return segmentationScore;
    }

    /**
     * @param segmentationScore the segmentationScore to set
     */
    public void setSegmentationScore(double segmentationScore) {
        this.segmentationScore = segmentationScore;
    }

    public String toString()
    {
        return "" + segmentationScore;
    }
}
