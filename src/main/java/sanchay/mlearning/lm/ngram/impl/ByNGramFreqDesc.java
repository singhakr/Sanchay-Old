/*
 * ByNGramFreqDesc.java
 *
 * Created on April 9, 2006, 5:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram.impl;

import sanchay.mlearning.lm.ngram.*;

import java.util.Comparator;
import java.util.Map;


/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

//  compare() Inconsistent with equals()
public class ByNGramFreqDesc implements Comparator {

    /**
     * 
     */
	public int compare(Object one, Object two)
	{
            if(one instanceof Map.Entry)
            {
		return ( (int) ((NGram) ((Map.Entry) two).getValue()).getFreq()
                        - (int) ((NGram) ((Map.Entry) one).getValue()).getFreq() );
            }
            
            return ( (int) ((NGram) two).getFreq() - (int) ((NGram) one).getFreq() );
	}
}
