/*
 * NERecognizerMain.java
 *
 * Created on December 14, 2007, 9:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.annotation.pos;

import sanchay.properties.KeyValueProperties;
import sanchay.annotation.common.TaggerMain;

/**
 *
 * @author Anil Kumar Singh
 */
public class POSTaggerMain extends TaggerMain {
    
    /** Creates a new instance of NERecognizerMain */
    public POSTaggerMain() {
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
        tagger.evaluate();
    } 

    public void evaluateBatch() {
        tagger.evaluateBatch();
    } 
    
    public static void main(String[] args)
    {
        
    }
}
