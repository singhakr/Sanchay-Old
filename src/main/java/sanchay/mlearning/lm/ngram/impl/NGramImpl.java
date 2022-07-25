package sanchay.mlearning.lm.ngram.impl;

import java.io.Serializable;
import sanchay.factory.Factory;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramCount;

/**
	The string containing words of the ngram will be the key for a Hashtable.
	NGram objects will be stored in the Hashtable.
	The Hashtable will be stored in the NGramLM object.
*/

public class NGramImpl extends NGramLiteImpl implements NGram, Serializable, Cloneable
{
    protected long rank;
    protected int weight; // weight giving the number of templates from which this ngram can be generated

    protected double relevanceProb;

    public static class NGramFactory implements Factory<NGram> {
        @Override
        public NGram createInstance() {
            return new NGramImpl();
        }
    }
    
    public static Factory getFactory()
    {
        return new NGramFactory();
    }

    public NGramImpl()
    {
        freq = 1;
        prob = 0.0;
        backwt = 0.0;
        weight = 1;

        relevanceProb = 0.0;
    }

    public NGramImpl(long f, double p, double bw, int w)
    {
        freq = f;
        prob = p;
        backwt = bw;
        weight = w;
    }

    @Override
    public long getRank()
    {
	return rank;
    }

    @Override
    public void setRank(long r)
    {
	rank = r;
    }

    @Override
    public int getWeight()
    {
        return weight;
    }

    @Override
    public void setWeight(int w)
    {
        weight = w;
    }
	
    /**
     * @return Returns the relevanceProb.
     */
    @Override
    public double getRelevanceProb()
    {
        return relevanceProb;
    }
    
    /**
     * @param relevanceProb The relevanceProb to set.
     */
    @Override
    public void setRelevanceProb(double relevanceProb)
    {
        this.relevanceProb = relevanceProb;
    }

    @Override
    public Object clone()
    {
        NGramImpl obj = (NGramImpl) super.clone();
        obj.weight = weight;

        return obj;
    }
    
    @Override
    public boolean equals(Object ng)
    {
        return false;
//        return ((NGram)ng).getString().equals(getString());
    }
}
