/*
 * NERecognizer.java
 *
 * Created on December 14, 2007, 9:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.annotation.chunking;

import sanchay.annotation.common.AutomaticTagger;

/**
 *
 * @author Anil Kumar Singh
 */
public interface Chunker extends AutomaticTagger {    
    void train();
    void tag();
    void tagBatch();    
}
