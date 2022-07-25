/*
 * Created on Jul 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.mlearning.lm.ngram;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface NGram extends NGramLite {
    
    public static final short SORT_BY_PROB = 0;
    public static final short SORT_BY_FREQ = 1;
    public static final short SORT_BY_FREQ_DESC = 2;
    public static final short SORT_BY_REL_PROB = 3;

    long getRank();

    void setRank(long r);

    int getWeight();

    void setWeight(int w);

    double getRelevanceProb();

    void setRelevanceProb(double relevanceProb);
}