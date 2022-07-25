/*
 * ModelScoreEx.java
 *
 * Created on January 25, 2009, 9:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

/**
 *
 * @author Anil Kumar Singh
 */
public class ModelScoreEx<K, S extends Comparable, O extends Object> extends ModelScore<K, S> {
    
    public O modelObject;
    
    /** Creates a new instance of ModelScoreEx */
    public ModelScoreEx(K modelKey, S modelScore, O modelObject) {
        super(modelKey, modelScore);
        
        this.modelObject = modelObject;
    }

    public String toString()
    {
        return modelKey + " ||| " + modelObject + " ||| " + modelScore;
    }
}
