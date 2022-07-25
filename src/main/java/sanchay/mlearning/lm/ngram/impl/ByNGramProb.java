/*
 * Created on Jul 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.mlearning.lm.ngram.impl;

import java.util.Comparator;
import java.util.Map;

import sanchay.mlearning.lm.ngram.NGram;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ByNGramProb implements Comparator {

    /**
     * 
     */

	public int compare(Object one, Object two)
	{
            if(one instanceof Map.Entry)
            {
		return ( (int) ((NGram) ((Map.Entry) one).getValue()).getProb()
                        - (int) ((NGram) ((Map.Entry) two).getValue()).getProb());
            }

            return Double.compare((double) ((NGram) one).getProb(), (double) ((NGram) two).getProb());
	}
}
