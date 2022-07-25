/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram;

import java.util.List;
import sanchay.factory.Factory;

/**
 *
 * @author anil
 */
public interface NGramCount {

    long getFreq();

    List<Integer> getIndices();

    String getString(NGramCounts ngramLM);

    void setFreq(long f);

    void setIndices(List<Integer> wdIndices);

    void setString(NGramCounts ngramLM, String s);    
    
    Object clone();
}
