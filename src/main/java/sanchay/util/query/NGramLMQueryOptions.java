/*
 * NGramLMQueryOptions.java
 *
 * Created on October 4, 2008, 1:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util.query;

/**
 *
 * @author eklavya
 */
public class NGramLMQueryOptions {

    public int order;
    public int minFreq;
    public int maxFreq;
    //public int minProb;
    //public int maxProb;
    
    /** Creates a new instance of NGramLMQueryOptions */
    public NGramLMQueryOptions() {
        order = -1;
        minFreq = -1;
        maxFreq = -1;
        //minProb = -1;
        //maxProb = -1;

    }
    
}
