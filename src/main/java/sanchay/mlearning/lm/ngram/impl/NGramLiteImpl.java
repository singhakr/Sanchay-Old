/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram.impl;

import sanchay.factory.Factory;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramCount;
import sanchay.mlearning.lm.ngram.NGramLite;

/**
 *
 * @author anil
 */
public class NGramLiteImpl extends NGramCountImpl implements NGramLite {
    
    protected double prob;
    protected double backwt; // the backoff weight

    public static class NGramLiteFactory implements Factory<NGramLite> {
        @Override
        public NGramLite createInstance() {
            return new NGramLiteImpl();
        }

    }
    
    public static Factory getFactory()
    {
        return new NGramLiteFactory();
    }

    public NGramLiteImpl()
    {
        freq = 1;
        prob = 0.0;
        backwt = 0.0;
    }

    public NGramLiteImpl(long f, double p, double bw)
    {
        freq = f;
        prob = p;
        backwt = bw;
    }
    
    @Override
    public double getProb()
    {
        return prob;
    }

    @Override
    public void setProb(double p)
    {
        prob = p;
    }

    @Override
    public double getBackwt()
    {
        return backwt;
    }

    @Override
    public void setBackwt(double b)
    {
        backwt = b;
    }
    

    @Override
    public Object clone()
    {
        NGramLiteImpl obj = (NGramLiteImpl) super.clone();
        obj.prob = prob;
        obj.backwt = backwt;

        return obj;
    }
}
