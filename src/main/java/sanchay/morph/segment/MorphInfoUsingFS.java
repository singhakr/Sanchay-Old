/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;



import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.features.impl.FeatureAttributeImpl;
import sanchay.corpus.ssf.features.impl.FeatureValueImpl;

/**
 *
 * @author ram
 */
public class MorphInfoUsingFS {

    private FeatureStructure fs;

    public void resetWordCount() {
        fs.clear();
    }

    /**
     * @return the wordCount
     */
    public int getWordCount() {
        FeatureValue fv = fs.getAttribute("wordCount").getAltValue(0);
        return Integer.parseInt(fv.toString());
    }

    /**
     * @param wordCount the wordCount to set
     */
    public void setWordCount(int wordCount) {
        FeatureAttribute fa = new FeatureAttributeImpl();
        FeatureValue fv = new FeatureValueImpl();
        fv.setValue(wordCount);
        fa.setName("wordCount");
        fa.addAltValue(fv);

        fs.addAttribute(fa);
    }

    /**
     * @return the morphcount
     */
    public int getMorphCount() {
        FeatureValue fv = fs.getAttribute("morphCount").getAltValue(0);
        return Integer.parseInt(fv.toString());
    }

    /**
     * @param morphcount the morphcount to set
     */
    public void setMorphCount(int morphCount) {
        FeatureAttribute fa = new FeatureAttributeImpl();
        FeatureValue fv = new FeatureValueImpl();
        fv.setValue(morphCount);
        fa.setName("morphCount");
        fa.addAltValue(fv);

        fs.addAttribute(fa);
    }

    /**
     * @return the splitLocation
     */
    public int getSplitLocation() {
        FeatureValue fv = fs.getAttribute("splitLocation").getAltValue(0);
        return Integer.parseInt(fv.toString());
    }

    /**
     * @param splitLocation the splitLocation to set
     */
    public void setSplitLocation(int splitLocation) {
        FeatureAttribute fa = new FeatureAttributeImpl();
        FeatureValue fv = new FeatureValueImpl();
        fv.setValue(splitLocation);
        fa.setName("splitLocation");
        fa.addAltValue(fv);

        fs.addAttribute(fa);
    }

    public void setAll(int wordCount, int morphCount, int splitLocation) {
        setWordCount(wordCount);
        setMorphCount(wordCount);
        setSplitLocation(wordCount);
    }

    public MorphInfoUsingFS(int wordCount, int morphCount, int splitLocation) {
        setAll(wordCount, morphCount, splitLocation);
    }

    public MorphInfoUsingFS() {
        setAll(0, 0, 0);
    }

}