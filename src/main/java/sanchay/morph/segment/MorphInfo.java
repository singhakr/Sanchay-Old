/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;

/**
 *
 * @author ram
 */
public class MorphInfo implements MorphInfoInterface{
    private int wordCount;
    private int morphCount;
    private int splitLocation;

    public void resetWordCount() {
        wordCount = 0;
    }


    /**
     * @return the wordCount
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * @param wordCount the wordCount to set
     */
    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    /**
     * @return the morphcount
     */
    public int getMorphCount() {
        return morphCount;
    }

    /**
     * @param morphcount the morphcount to set
     */
    public void setMorphCount(int morphCount) {
        this.morphCount = morphCount;
    }

    /**
     * @return the splitLocation
     */
    public int getSplitLocation() {
        return splitLocation;
    }

    /**
     * @param splitLocation the splitLocation to set
     */
    public void setSplitLocation(int splitLocation) {
        this.splitLocation = splitLocation;
    }

    public void setAll(int wordCount, int morphCount, int splitLocation) {
        setWordCount(wordCount);
        setMorphCount(morphCount);
        setSplitLocation(splitLocation);
    }

    public MorphInfo(int wordCount, int morphCount, int splitLocation) {
        setAll(wordCount,morphCount,splitLocation);
    }
    
    public MorphInfo() {
        setAll(0,0,0);
    }

}
