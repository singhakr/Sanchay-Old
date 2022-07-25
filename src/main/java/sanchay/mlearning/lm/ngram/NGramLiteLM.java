/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author anil
 */
public interface NGramLiteLM <NG extends NGramLite> extends NGramCounts<NG> {

    @Override
    LinkedHashMap<List<Integer>, Long> getCumulativeFrequencies(int whichGram);

    @Override
    List<LinkedHashMap<List<Integer>, Long>> getCumulativeFrequenciesList();
    
}
