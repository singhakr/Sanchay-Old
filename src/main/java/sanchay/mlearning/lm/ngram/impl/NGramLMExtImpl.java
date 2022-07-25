/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram.impl;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import sanchay.factory.Factory;
import sanchay.mlearning.lm.ngram.NGramExt;
import sanchay.mlearning.lm.ngram.NGramLMExt;

/**
 *
 * @author Anil Kumar Singh
 */
public class NGramLMExtImpl<NG extends NGramExt> extends NGramLMImpl<NG> implements NGramLMExt<NG> {

    private Factory<NG> factory = NGramExtImpl.getFactory();

    public NGramLMExtImpl(File f, String type, int order, String cs, String lang)
    {
    	this(f, type, order);
    }

    public NGramLMExtImpl(File f, String type, int order)
    {
        super(f, type, order);
    }

    @Override
    public long addNGram(String wds, int whichGram, int normIncrement)
    {
        if(whichGram > nGramOrder || whichGram < 1) { return -1; }

        NGramExt oldng = null;
        int wt = 0;
        LinkedHashMap<List<Integer>, NG> ht = nGrams.get(whichGram - 1);
        
        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, true);

        if(ht != null)
        {
            if( (oldng = (NGramExt) ht.get(wdIndices) ) == null)
            {
                NG ng = factory.createInstance();
                ng.setString(this, wds);
                ng.setNormalizerIncrement(normIncrement);
                ht.put(wdIndices, ng);
            }
            else
            {
                oldng.setFreq(oldng.getFreq() + 1);
                oldng.setNormalizerIncrement(oldng.getNormalizerIncrement()  + normIncrement);
            }

            return ht.size();
        }

        return 0;
    }

    public long addNGram(String wds, NG ng, int whichGram, int normIncrement)
    {
        if(whichGram > nGramOrder || whichGram < 1) { return -1; }

        NG oldng = null;
        int wt = 0;
        LinkedHashMap<List<Integer>, NG> ht = nGrams.get(whichGram - 1);

        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, true);

        if(ht != null)
        {
            if( (oldng = ht.get(wdIndices) ) == null)
            {
                ng.setString(this, wds);
                ht.put(wdIndices, ng);
                ng.setNormalizerIncrement(normIncrement);
            }
            else
            {
                wt = oldng.getWeight();
                oldng.setWeight(wt + 1);
                oldng.setNormalizerIncrement(oldng.getNormalizerIncrement() + normIncrement);
            }

            return ht.size();
        }

        return 0;
    }

    public long addNGram(String wds, int whichGram,long freq, int normIncrement)
    {
    	if(whichGram > nGramOrder || whichGram < 1) { return -1; }

        NG oldng = null;
        int wt = 0;
        LinkedHashMap<List<Integer>, NG> ht = nGrams.get(whichGram - 1);

        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, true);

        if(ht != null)
        {
            if( (oldng = ht.get(wdIndices) ) == null)
            {
                NG ng = factory.createInstance();
                ng.setString(this, wds);
                ng.setFreq(freq);
                ng.setNormalizerIncrement(normIncrement);
                ht.put(wdIndices, ng);
            }
            else
            {
                oldng.setFreq(oldng.getFreq() + freq);
                oldng.setNormalizerIncrement(oldng.getNormalizerIncrement()  + normIncrement);
            }

            return ht.size();
        }

        return 0;
    }

    public void calcProbs(int whichGram)
    {
        if(whichGram > nGramOrder || whichGram < 1) { return; }

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        while(itr.hasNext())
        {
            List<Integer> numer = itr.next();
            
            String numerString = NGramImpl.getString(this, numer);

            int index=numerString.lastIndexOf("@#&");
            double denom;
            String denomString="";
            if(index >=0 )
            {
                denomString = numerString.substring(0,index);
                NGramExt dg = (NGramExt) getNGram(denomString,whichGram-1);

                if(dg == null)
                    continue;

                denom = (double) dg.getFreq();
            }
            else
            {
//                  NGramExt dg = (NGramExt) getNGram(numer,whichGram - 1);
                denom = countTokens(whichGram);
//                  denom = (double) dg.getFreq();
            }

            NGramExt ng = (NGramExt) getNGram(numer, whichGram);

//            if(index >= 0)
                ng.setProb( ((double) ng.getFreq()) / ( denom ) );
//                ng.setProb( ((double) ng.getFreq()) / ( denom * ng.getNormalizerIncrement()) );
//                ng.setProb( ((double) ng.getFreq()) / ( denom ) );
//            else
//                ng.setProb( ((double) ng.getFreq()) / denom );

            //System.out.println(numer+"--"+index+"--"+denomString+"--"+denom+"--"+((double) ng.getFreq()) / denom);
        }
    }
}
