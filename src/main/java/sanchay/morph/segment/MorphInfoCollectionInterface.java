/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;
/**
 *
 * @author ram
 */
public interface MorphInfoCollectionInterface  {//will rpobably be implemented using a simple hash like in morfessor
    /**
     * you must create it before inserting!!
      * adds a new morph and morphinfo pair
      * can be used to edit an already existing morph also
      * @param morph
      * @param morphInfo
      */
    public void addMorph(Morph morph,MorphInfoInterface morphInfo);
    /**
     * removes a morph
     * @param morph
     */
    public void removeMorph(Morph morph);
    /**
     * return the morphinfo regrading the morph
     * return null if not present
     * @param morph
     * @return
     */
    public MorphInfoInterface getInfo(Morph morph);
    /**
     * can be used to enumerate over all morphs
     * @return
     */
   // public Enumeration keys();
    /*
     * gives next morph,morphInfo pair
     * @param morph
     * @param morphInfo
     * @return 
     */
    //public boolean next(Morph morph,MorphInfoInterface morphInfo);
    /**
     * 
     * @return
     */
    public Morph next();
    /**
     * resets so that next() again continues from start
     */
    public void resetNext();

    /**
     * create if necessary
     * doesnot add the morph to its collection automatically!!
     * @param str
     * @return
     */
    public Morph getMorphFromString(String str);

    /**
     * returns the number of morphs
     * @return
     */
    public int size();
}
