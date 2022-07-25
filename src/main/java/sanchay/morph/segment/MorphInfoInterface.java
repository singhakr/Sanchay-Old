/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.morph.segment;

/**
 *
 * @author ram
 */
public interface MorphInfoInterface {

    public void resetWordCount();

    public void setAll(int wordCount, int morphCount, int splitLocation);
    
    public int getWordCount();

    public void setWordCount(int wordCount);

    public int getMorphCount();

    public void setMorphCount(int morphCount);

    public int getSplitLocation();

    public void setSplitLocation(int splitLocation);
}
