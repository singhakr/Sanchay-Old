/*
 * AutomaticTagger.java
 *
 * Created on December 26, 2007, 12:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.annotation.common;

/**
 *
 * @author Anil Kumar Singh
 */
public interface AutomaticTagger {
    void train();
    void tag();
    void tagBatch();        
    void evaluate();
    void evaluateBatch();
}
