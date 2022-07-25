/*
 * AutomaticTagger.java
 *
 * Created on December 26, 2007, 12:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.annotation.common;

/**
 *
 * @author Anil Kumar Singh
 */
public abstract class AutomaticTaggerImpl implements AutomaticTagger {
    
    TaggingPreprocessing preprocessor;
    TaggingPostprocessing postprocessor;
    
    /** Creates a new instance of AutomaticTagger */
    public AutomaticTaggerImpl() {
    }
    
    public void train() {
    }

    public void tag() {
    }

    public void tagBatch() {
        
    }
}
