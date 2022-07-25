/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram;

import edu.cmu.sphinx.fst.semiring.Semiring;
import java.io.File;
import java.io.Serializable;
import sanchay.text.spell.PhonemeFeatureModel;

/**
 *
 * @author Anil Kumar Singh
 */
public interface NGramLM<NG extends NGram> extends NGramLiteLM<NG>, Cloneable, Serializable {

    void calcBackoff();

    void calcMergedProbs();

    void calcMergedProbs(int whichGram);

    void calcProbs();

    void calcProbs(int whichGram);

    void calcRelevanceProbs();

    void calcRelevanceProbs(int whichGram);

    void calcSimpleProbs();

    void calcSimpleProbs(int whichGram);

    void calcSmoothGoodTuring(int whichGram);

    void calcSmoothKneserNey(double delta);

    void calcSmoothProbs(String Algo, int kValue);

    void calcSmoothProbs();

    void calcSmoothWittenBell(int whichGram);

    void cleanNGramLM();

    Object clone();


    long countTriggerPairs(boolean recalc);

    void fillRanks(int whichGram);

    NGramLM getCPMSFeaturesNGramLM(File f);

    /**
     * @return Returns the freqWeight.
     */
    double getFreqWeight();

    // When the model is char ngram model
    double getPhonemeSequenceProb(String sequence, PhonemeFeatureModel phonemeFeatureModel);

    /**
     * @return Returns the rarityWeight.
     */
    double getRarityWeight();

    double getSentenceProb(String sentence);

    float getSentencePosteriorProb(String sentence, Semiring semiring, int whichGram);

    double getSmoothKneserNeyProb(String nGramKey, int whichGram);

    /**
     * @return NGramLM containing associated words (in terms of trigger pairs) as unigram.
     */
    NGramLM getTPWordModel(String wrd);

    void makeTriggerPairs();

    void normalizeNGramProbs(int whichGram);

    void normalizeNGramProbs();

    void pruneByRank(int rank, int whichGram);

    void pruneByRankAndMerge(int rank, int whichGram);

    void pruneTriggerPairs(long minFreq);

    void removeUNKL(int whichGram);

    /**
     * @param freqWeight The freqWeight to set.
     */
    void setFreqWeight(double freqWeight);

    /**
     * @param rarityWeight The rarityWeight to set.
     */
    void setRarityWeight(double rarityWeight);

    long triggerPairFreq(String wrd1, String wrd2);    
}
