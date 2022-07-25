/*
 * TaggerMain.java
 *
 * Created on December 26, 2007, 12:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.annotation.common;

import sanchay.properties.KeyValueProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class TaggerMain {
    
    protected KeyValueProperties option;
    
    protected AutomaticTagger tagger;    
    protected TaggingEvaluation eval;
    
    /** Creates a new instance of TaggerMain */
    public TaggerMain() {
    }
    
    public KeyValueProperties getOptions()
    {
        return option;
    }
    
    public void setOptions(KeyValueProperties o)
    {
        option = o;
    }

    public void train() {
        tagger.train();
    }

    public void tag() {
        tagger.tag();
    }

    public void tagBatch() {
        tagger.tagBatch();
    } 

    public void evaluate() {
        eval.evaluate();
    } 

    public void evaluateBatch() {
        eval.evaluateBatch();
    } 
    
    public static void main(String[] args)
    {
        
    }    
}
