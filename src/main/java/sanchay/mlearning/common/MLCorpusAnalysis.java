/*
 * EMCompleteDataCorpus.java
 *
 * Created on June 27, 2006, 12:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

import java.util.Iterator;

/**
 *
 * @author Anil Kumar Singh
 */
public interface MLCorpusAnalysis extends MLCorpus
{
    long getAnalysisCount(MLType type);

    MLFrequency getAnalysisFrequency(MLType type, MLAnalysis analysis);
    long addAnalysis(MLType type, MLAnalysis analysis, MLFrequency freq);
    long removeAnalysis(MLType type, MLAnalysis analysis);
    
    Iterator getAnalyses();
    Iterator getAnalysisFrequencies();
    
    Iterator getAnalyses(MLType type);
    Iterator getAnalysisFrequencies(MLType type);
}
