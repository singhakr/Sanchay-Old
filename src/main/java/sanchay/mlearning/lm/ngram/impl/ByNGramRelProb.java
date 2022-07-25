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
public class ByNGramRelProb implements Comparator {


    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
	public int compare(Object one, Object two)
	{
            if(one instanceof Map.Entry)
            {
		return ( (int) ((NGram) ((Map.Entry) one).getValue()).getRelevanceProb()
                        - (int) ((NGram) ((Map.Entry) two).getValue()).getRelevanceProb());
            }

	    return Double.compare((double) ((NGram) one).getRelevanceProb(), (double) ((NGram) two).getRelevanceProb());
    }

    public static void main(String[] args) {
    }
}
