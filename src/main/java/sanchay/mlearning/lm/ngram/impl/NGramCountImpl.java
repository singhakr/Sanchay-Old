/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram.impl;

import sanchay.mlearning.lm.ngram.NGramCounts;
import sanchay.mlearning.lm.ngram.NGramCount;
import java.util.ArrayList;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.factory.Factory;
import static sanchay.mlearning.lm.ngram.impl.NGramImpl.getIndices;
import static sanchay.mlearning.lm.ngram.impl.NGramImpl.getString;

/**
 *
 * @author anil
 */
public class NGramCountImpl implements NGramCount {
    protected List<Integer> indices;
    protected long freq;

    public static class NGramCountFactory implements Factory<NGramCount> {
        @Override
        public NGramCount createInstance() {
            return new NGramCountImpl();
        }

    }
    
    public static Factory getFactory()
    {
        return new NGramCountFactory();
    }
    
    public NGramCountImpl()
    {
        freq = 1;
    }

    public NGramCountImpl(long f)
    {
        freq = f;
    }

    @Override
    public List<Integer> getIndices()
    {
        return indices;
    }

    @Override
    public void setIndices(List<Integer> wdIndices)
    {
        indices = wdIndices;
    }

    @Override
    public String getString(NGramCounts ngramLM)
    {
        return getString(ngramLM, indices);
    }

    @Override
    public void setString(NGramCounts ngramLM, String s)
    {
        indices = getIndices(ngramLM, s, true);
    }
   
    @Override
    public long getFreq()
    {
        return freq;
    }

    @Override
    public void setFreq(long f)
    {
        freq = f;
    }

    @Override
    public Object clone()
    {
        try
        {
            NGramCountImpl obj = (NGramCountImpl) super.clone();
            return obj;
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(GlobalProperties.getIntlString("But_the_class_is_Cloneable!!!"));
        }
    }
    
    public static List<Integer> getIndices(NGramCounts ngramLM, String wds, boolean add)
    {        
        String parts[] = wds.split("@#&");
        
        List<Integer> indices = new ArrayList<Integer>(parts.length);
        
        for (int i = 0; i < parts.length; i++) {
            int wi = ngramLM.getVocabIndex().indexOf(parts[i], add);
            
            indices.add(wi);
        }
        
        return indices;
    }
    
    public static List<Integer> getIndicesPlain(NGramCounts ngramLM, String wdsPlain, boolean add)
    {
        String parts[] = wdsPlain.trim().split("[\\s+]");
        
        List<Integer> indices = new ArrayList<Integer>(parts.length);
        
        for (int i = 0; i < parts.length; i++) {
            int wi = ngramLM.getVocabIndex().indexOf(parts[i], add);
            
            indices.add(wi);
        }
        
        return indices;
    }

    public static String getString(NGramCounts ngramLM, List<Integer> wdIndices)
    {
        String str = "";
        
        int i = 0;
        for (Integer wi : wdIndices) {
            if(i == 0)
            {
                str = (String) ngramLM.getVocabIndex().get(wi);
            }
            else
            {
                str += "@#&" + ngramLM.getVocabIndex().get(wi);
            }
            
            i++;
        }
        
        return str;
    }

    public static String getPlainString(NGramCounts ngramLM, List<Integer> wdIndices)
    {
        String str = "";

        int i = 0;
        
        for (Integer wi : wdIndices) {
            if(i == 0)
            {
                str = (String) ngramLM.getVocabIndex().get(wi);
            }
            else
            {
                str += " " + ngramLM.getVocabIndex().get(wi);                
            }
            
            i++;
        }
        
        return str;
    }
        
    @Override
    public String toString()
    {
        return indices.toString();
//        return getString(indices);
    }
}
