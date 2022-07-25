/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram.impl;

import java.util.Comparator;
import java.util.Map;
import sanchay.mlearning.lm.ngram.NGram;

/**
 *
 * @author Anil Kumar Singh
 */
public class ByNGramProbDescending implements Comparator {
	public int compare(Object one, Object two)
	{
            if(one instanceof Map.Entry)
            {
		return ( (int) ((NGram) ((Map.Entry) two).getValue()).getProb()
                        - (int) ((NGram) ((Map.Entry) one).getValue()).getProb());
            }

	    return Double.compare((double) ((NGram) two).getProb(), (double) ((NGram) one).getProb());
	}
}
