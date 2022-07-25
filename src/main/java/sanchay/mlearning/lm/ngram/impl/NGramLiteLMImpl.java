/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram.impl;

import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import sanchay.factory.Factory;
import sanchay.mlearning.lm.ngram.NGramLite;
import sanchay.mlearning.lm.ngram.NGramLiteLM;

/**
 *
 * @author anil
 */
public class NGramLiteLMImpl <NG extends NGramLite> extends NGramCountsImpl<NG>
        implements NGramLiteLM<NG>
{
    
    protected NGramLiteLMImpl()
    {
    }
    
    public NGramLiteLMImpl(File f, String type, int order, String cs, String lang, boolean sentenceBoundaries) {
        this(f, type, order, cs, lang);

        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramLiteLMImpl(File f, String type, int order, String cs, String lang) {
        this(f, type, order);

        charset = cs;
        language = lang;
    }

    public NGramLiteLMImpl(File f, String type, int order, Index vocabIndex, boolean sentenceBoundaries) {
        this(f, type, order, vocabIndex);
        
        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramLiteLMImpl(File f, String type, int order, boolean sentenceBoundaries) {
        this(f, type, order);
        
        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramLiteLMImpl(File f, String type, int order, Index vocabIndex) {
        init(f, type, order);

        this.vocabIndex = vocabIndex;
    }

    public NGramLiteLMImpl(File f, String type, int order) {
        init(f, type, order);
        
        this.vocabIndex = new HashIndex<String>();
    }
    
    private void init(File f, String type, int order)
    {
        factory = NGramLiteImpl.getFactory();
        
        nGramType = type;
        nGramOrder = order;

        nGrams = new ArrayList(order);

        for (int i = 0; i < order; i++) {
            nGrams.add(new LinkedHashMap<List<Integer>, NG>(0, 10));
        }

        tokenCount = new long[order];

        nGramLMFile = f;
        charset = "ISO-8859-1";
        language = "hin::utf8";
    }
    
}
