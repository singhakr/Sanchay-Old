package sanchay.mlearning.lm.ngram.impl;

import edu.cmu.sphinx.fst.semiring.LogSemiring;
import edu.cmu.sphinx.fst.semiring.Semiring;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramCount;
import sanchay.mlearning.lm.ngram.NGramCounts;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.properties.PropertyTokens;
import sanchay.text.spell.PhonemeFeatureModel;
import sanchay.text.spell.PhoneticModelOfScripts;
import sanchay.util.Pair;
import sanchay.util.UtilityFunctions;

/**
NGram Language Model
 */
public class NGramLMImpl<NG extends NGram> extends NGramLiteLMImpl<NG> implements NGramLM<NG>, Cloneable, Serializable  {

    // Hashtables with key-NGram pairs
    protected List<LinkedHashMap<List<Integer>, Long>> nGramTypes;
    protected List<LinkedHashMap<List<Integer>, Long>> nGramTokens;
    protected List<LinkedHashMap<List<Integer>, Integer>> KNDotCounts;
    protected LinkedHashMap<Integer, NGram> nGramUNKs;
    protected double freqWeight;
    protected double rarityWeight;
    protected LinkedHashMap<Pair<List<Integer>, List<Integer>>, Long> triggerPairs;
    protected int triggerPairWindowSize = 30;
    protected long triggerPairCount;
    protected String smoothingAlgo = GlobalProperties.getIntlString("Witten-Bell");
    protected long vocabulary = 1616;
    protected int goodTuringK = 5;
    protected double KNdelta = 0.2;

    public NGramLMImpl(File f, String type, int order, String cs, String lang, boolean sentenceBoundaries) {
        this(f, type, order, cs, lang);

        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramLMImpl(File f, String type, int order, String cs, String lang) {
        this(f, type, order);

        charset = cs;
        language = lang;
    }

    public NGramLMImpl(File f, String type, int order, Index vocabIndex, boolean sentenceBoundaries) {
        this(f, type, order, vocabIndex);
        
        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramLMImpl(File f, String type, int order, boolean sentenceBoundaries) {
        this(f, type, order);
        
        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramLMImpl(File f, String type, int order, Index vocabIndex) {        
        init(f, type, order);

        this.vocabIndex = vocabIndex;
    }

    public NGramLMImpl(File f, String type, int order) {
        init(f, type, order);
        
        this.vocabIndex = new HashIndex<String>();
    }
    
    private void init(File f, String type, int order)
    {
        factory = NGramImpl.getFactory();
        
        nGramType = type;
        nGramOrder = order;

        nGrams = new ArrayList(order);
        tempNGrams = new ArrayList(order);

        for (int i = 0; i < order; i++) {
            nGrams.add(new LinkedHashMap<List<Integer>, NG>(0, 10));
            tempNGrams.add(new LinkedHashMap<Integer, String>(0, 10));
        }

        tokenCount = new long[order];

        nGramLMFile = f;
        charset = "ISO-8859-1";
        language = "hin::utf8";

        triggerPairs = new LinkedHashMap<Pair<List<Integer>, List<Integer>>, Long>(0, 20);        
    }

    /**
     * @return Returns the freqWeight.
     */
    @Override
    public double getFreqWeight() {
        return freqWeight;
    }

    /**
     * @param freqWeight The freqWeight to set.
     */
    @Override
    public void setFreqWeight(double freqWeight) {
        this.freqWeight = freqWeight;
    }

    /**
     * @return Returns the rarityWeight.
     */
    @Override
    public double getRarityWeight() {
        return rarityWeight;
    }

    /**
     * @param rarityWeight The rarityWeight to set.
     */
    @Override
    public void setRarityWeight(double rarityWeight) {
        this.rarityWeight = rarityWeight;
    }

    private void updateTriggerPairs(LinkedList wndList, String wrd) {
        Iterator itr = wndList.iterator();

        while (itr.hasNext()) {
            String wndWrd = (String) itr.next();

            String key = wndWrd + "@#&" + wrd;
            
            List<Integer> wrdIndices = NGramImpl.getIndices(this, wrd, false);
            List<Integer> wndWrdIndices = NGramImpl.getIndices(this, wndWrd, false);
            
            Pair pkey = new Pair(wrdIndices, wndWrdIndices);

            Long freq = triggerPairs.get(pkey);
            
            if(freq == null)
            {
                triggerPairs.put(pkey, 1L);
            }
            else
            {
                triggerPairs.put(pkey, freq + 1L);                
            }
        }
    }

    @Override
    public long countTriggerPairs(boolean recalc) {
        if (recalc == false) {
            return triggerPairCount;
        }

        Iterator<Pair<List<Integer>, List<Integer>>> itr = triggerPairs.keySet().iterator();

        while (itr.hasNext()) {
            Pair<List<Integer>, List<Integer>> pkey = itr.next();
            
            triggerPairCount += triggerPairs.get(pkey);
        }

        return triggerPairCount;
    }

    @Override
    public void pruneTriggerPairs(long minFreq) {

        LinkedHashMap<Pair<List<Integer>, List<Integer>>, Long> prunedTriggerPairs
                = new LinkedHashMap<Pair<List<Integer>, List<Integer>>, Long>(1000, 1000);
        
        Iterator<Pair<List<Integer>, List<Integer>>> itr = triggerPairs.keySet().iterator();

        while (itr.hasNext()) {
            Pair<List<Integer>, List<Integer>> pkey = itr.next();

            Long freq = triggerPairs.get(pkey);

            if (freq >= minFreq) {
                prunedTriggerPairs.put(pkey, freq);
            }
        }
        
        triggerPairs = prunedTriggerPairs;
        
        System.gc();
    }

    @Override
    public long triggerPairFreq(String wrd1, String wrd2) {
        
        List<Integer> wrd1Indices = NGramImpl.getIndices(this, wrd1, false);
        List<Integer> wrd2Indices = NGramImpl.getIndices(this, wrd2, false);

        Pair pkey = new Pair(wrd1Indices, wrd2Indices);

        Long freq = triggerPairs.get(pkey);

        if (freq == null) {
            return 0;
        }

        return freq;
    }

    /**
     * @return NGramLM containing associated words (in terms of trigger pairs) as unigram.
     */
    @Override
    public NGramLM getTPWordModel(String wrd) {
//	Hashtable wrdModel = (Hashtable) triggerPairs.get(wrd1);
//	
//	if(wrdModel == null)
//	    return 0;
//	
//	Long freq = (Long) wrdModel.get(wrd2);
//	
//	if(freq == null)
//	    return 0;
//	
//	return freq.longValue();
        return null;
    }
    
    @Override
    public void readNGramLM() throws FileNotFoundException, IOException {
        clear();

        BufferedReader lnReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(nGramLMFile),getCharset()));

        String line;
        int gram = -1;
        int order = 0;
        String ngram = "";
        String pngram = ""; // probability
        String bowngram = ""; // backoff weight
        String fqngram = ""; // frequency
        String splitstr[];
        
        List<Integer> typeCounts = new ArrayList<Integer>();

        Pattern p = Pattern.compile("[\\s]+");

        while ((line = lnReader.readLine()) != null && !line.startsWith("\\end\\")) {
            if (line.startsWith("\\data\\")) {
                gram = 0;
//                System.out.println("0-grams");
            } else if (gram == 0 && line.startsWith("ngram")) {
                splitstr = p.split(line);
                
                String orderStr = splitstr[1];
                
                splitstr = orderStr.split("=");
                
                order = Integer.parseInt(splitstr[0]);
                
                typeCounts.add(Integer.parseInt(splitstr[1]));
                
            } else if (gram >= 0 && line.matches("\\\\[1-9]-grams:")) {
                gram++;
            } else if (gram >= 1) {
                splitstr = p.split(line);
                
                if(splitstr.length <= 1)
                {
                    continue;
                }

                NG ng = factory.createInstance();

                pngram = splitstr[0];

//                System.err.println(line);
                ng.setProb(Math.pow(10, Double.parseDouble(pngram)));
                
                ngram = "";

                for (int i = 1; i <= gram; i++) {
                    if(i == 1)
                    {
                        ngram = splitstr[i];
                    }
                    else
                    {
                        ngram += "@#&" + splitstr[i];
                    }
                }
                
                if(gram == order)
                {
                    if(splitstr.length > gram + 1)
                    {
                        fqngram = splitstr[gram + 1];
                        ng.setFreq(Long.parseLong(fqngram));
                    }
                }
                else if(gram < order)
                {
                    if(splitstr.length > gram + 1) // Standard ARPA format: backoff weight
                    {
                        bowngram = splitstr[gram + 1];

                        ng.setBackwt(Math.pow(10, Double.parseDouble(bowngram)));
                    }

                    if(splitstr.length > gram + 2) // Sanchay format: frequency
                    {
                        fqngram = splitstr[gram + 2];

                        ng.setFreq(Long.parseLong(fqngram));
                    }
                }

                addNGram(ngram, ng, gram);
            }
            
            nGramOrder = order;
        }
    }
//
//    @Override
//    public void readNGramLM() throws FileNotFoundException, IOException {
//        clear();
//
//        BufferedReader lnReader = new BufferedReader(
//                new InputStreamReader(new FileInputStream(nGramLMFile),getCharset()));
//
//        String line;
//        int gram = -1;
//        String wd1 = "";
//        String wd2 = "";
//        String wd3 = "";
//        String wd4 = "";
//        String pngram = ""; // probability
//        String bowngram = ""; // backoff weight
//        String fqngram = ""; // frequency
//        String splitstr[];
//
//        Pattern p = Pattern.compile("[\\s]");
//
//        while ((line = lnReader.readLine()) != null && !line.startsWith("\\end\\")) {
//            if (line.startsWith("\\data\\")) {
//                gram = 0;
////                System.out.println("0-grams");
//            } else if (gram == 0 && line.startsWith("\\1-grams:")) {
//                gram = 1;
////                System.out.println("1-grams");
//            } else if (gram == 1 && line.startsWith("\\2-grams:") == false) {
//                //splitstr = line.split("[\\t ]");
//                splitstr = p.split(line);
////              System.out.println(splitstr1[1]);
//                if (splitstr.length >= 3) {
//                    pngram = splitstr[0];
//                    wd1 = splitstr[1];
////                      System.out.println(wd1);
//                    bowngram = splitstr[2];
//
//                    NGram ng = new NGramImpl();
//                    ng.setProb(Double.parseDouble(pngram));
//                    ng.setBackwt(Double.parseDouble(bowngram));
//
//                    if (splitstr.length > 3) {
//                        fqngram = splitstr[3];
//                        ng.setFreq(Long.parseLong(fqngram));
//                    }
//
//                    addNGram(wd1, ng, 1);
//                }
//            } else if (gram == 1 && line.startsWith("\\2-grams:")) {
//                gram = 2;
////                System.out.println("2-grams");
//            } else if (gram == 2 && line.startsWith("\\3-grams:") == false) {
//                splitstr = p.split(line);
//                if (splitstr.length >= 4) {
//                    pngram = splitstr[0];
//                    wd1 = splitstr[1];
//                    wd2 = splitstr[2];
//                    bowngram = splitstr[3];
//
//                    NGram ng = new NGramImpl();
//                    ng.setProb(Double.parseDouble(pngram));
//                    ng.setBackwt(Double.parseDouble(bowngram));
//
//                    if (splitstr.length > 4) {
//                        fqngram = splitstr[4];
//                        ng.setFreq(Long.parseLong(fqngram));
//                    }
//
//                    addNGram((wd1 + "@#&" + wd2), ng, 2);
//                }
//            } else if (gram == 2 && line.startsWith("\\3-grams:")) {
//                gram = 3;
////                System.out.println("3-grams");
//            } else if (gram == 3) {
//                splitstr = p.split(line);
//                //System.out.println(line);
//
//                if (splitstr.length >= 5) {
//                    pngram = splitstr[0];
//                    wd1 = splitstr[1];
//                    wd2 = splitstr[2];
//                    wd3 = splitstr[3];
//                    bowngram = splitstr[4];
//
//                    NGram ng = new NGramImpl();
//                    ng.setProb(Double.parseDouble(pngram));
//                    ng.setBackwt(Double.parseDouble(bowngram));
//                    //System.out.println("AMB-->"+line);
//
//
//                    if (splitstr.length > 5) {
//                        fqngram = splitstr[5];
//                        ng.setFreq(Long.parseLong(fqngram));
//                    }
//
//                    addNGram((wd1 + "@#&" + wd2 + "@#&" + wd3), ng, 3);
//                }
//            } else if (gram == 3 && line.startsWith("\\4-grams:")) {
//                gram = 4;
////                System.out.println("3-grams");
//            } else if (gram == 4) {
//                splitstr = p.split(line);
//                //System.out.println(line);
//
//                if (splitstr.length >= 5) {
//                    pngram = splitstr[0];
//                    wd1 = splitstr[1];
//                    wd2 = splitstr[2];
//                    wd3 = splitstr[3];
//                    wd4 = splitstr[4];
////                  System.out.println(wd1 + " " + wd2 + " " + wd3);
//
//                    NGram ng = new NGramImpl();
//                    ng.setProb(Double.parseDouble(pngram));
//
//                    if (splitstr.length > 5) {
//                        fqngram = splitstr[5];
//                        ng.setFreq(Long.parseLong(fqngram));
//                    }
//
//                    addNGram((wd1 + "@#&" + wd2 + "@#&" + wd3 + "@#&" + wd4), ng, 3);
//                }
//            }
//        }
//    }

    @Override
    public void calcCountsNProbs() {
        calcTokenCount();
        calcMergedTokenCount();
        calcMergedTypeCount();
        
        calcMergedProbs();

//        calcProbs();
//        calcRelevanceProbs();
    //calcSmoothProbs();
    }

    @Override
    public void calcMergedProbs() {
        calcMergedTokenCount();

        for (int i = 1; i <= nGramOrder; i++) {
            calcMergedProbs(i);
        }
    }

    @Override
    public void calcMergedProbs(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        while (itr.hasNext()) {
            List<Integer> k = itr.next();
            NGram ng = getNGram(k, whichGram);

            ng.setProb(((double) ng.getFreq()) / ((double) mergedTokenCount));
        }
    }

    @Override
    public void calcSimpleProbs() {
        for (int i = 1; i <= nGramOrder; i++) {
            calcSimpleProbs(i);
        }
    }

    @Override
    public void calcSimpleProbs(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        calcTokenCount(whichGram);

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        while (itr.hasNext()) {
            List<Integer> k = itr.next();
            NGram ng = getNGram(k, whichGram);

            ng.setProb(((double) ng.getFreq()) / ((double) countTokens(whichGram)));
        }
    }

    @Override
    public void calcProbs() {
        for (int i = 1; i <= nGramOrder; i++) {
            calcProbs(i);
        }
    }

    @Override
    public void calcProbs(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        while (itr.hasNext()) {
            List<Integer> numerIndices = itr.next();
            
            String numer = NGramImpl.getString(this, numerIndices);

            int index = numer.lastIndexOf("@#&");
            double denom;
            String denomString = "";
            if (index >= 0) {
                denomString = numer.substring(0, index);
                NGram dg = getNGram(denomString, whichGram - 1);

                if (dg == null) {
                    continue;
                }

                denom = dg.getFreq();
            } else {
                denom = countTokens(whichGram);
            }

            NGram ng = getNGram(numerIndices, whichGram);

            ng.setProb(((double) ng.getFreq()) / denom);

        //System.out.println(numer+"--"+index+"--"+denomString+"--"+denom+"--"+((double) ng.getFreq()) / denom);
        }
    }

    @Override
    public void calcRelevanceProbs() {
        for (int i = 1; i <= nGramOrder; i++) {
            calcRelevanceProbs(i);
        }
    }

    @Override
    public void calcRelevanceProbs(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        while (itr.hasNext()) {
            List<Integer> k = itr.next();
            NGram ng = getNGram(k, whichGram);

            ng.setRelevanceProb(
                    (0.5) * (getFreqWeight() * ng.getProb() + getRarityWeight() * (1 / ((double) ng.getFreq()))));
        }
    }

//    public void calcSmoothProbs(String Algo, long vocabSize) {
//        smoothingAlgo = Algo;
//        vocabulary = vocabSize;
//        calcSmoothProbs();
//    }

    @Override
    public void calcSmoothProbs(String Algo, int kValue) {
        smoothingAlgo = Algo;
//        vocabulary = vocabSize;
        goodTuringK = kValue;
        calcSmoothProbs();
    }

//    public void calcSmoothProbs(String Algo, long vocabSize, int kValue) {
//        smoothingAlgo = Algo;
//        vocabulary = vocabSize;
//        goodTuringK = kValue;
//        calcSmoothProbs();
//    }

    @Override
    public void calcSmoothProbs() {

        if (smoothingAlgo == "Witten-Bell") {
            nGramTypes = new ArrayList(nGramOrder);
            nGramTokens = new ArrayList(nGramOrder);

            for (int i = 0; i < nGramOrder; i++) {
                nGramTypes.add(new LinkedHashMap<List<Integer>, Long>(0, 10));
                nGramTokens.add(new LinkedHashMap<List<Integer>, Long>(0, 10));
            }

            for (int i = 1; i <= nGramOrder; i++) {
                calcSmoothWittenBell(i);
            }
        } else if (smoothingAlgo == "Good-Turing") {
            for (int i = 1; i <= nGramOrder; i++) {
                calcSmoothGoodTuring(i);
            }
        }
    }

    @Override
    public void calcSmoothWittenBell(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }
        
        getVocabularySize();

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        String unkl = "UNKL";
        long totalNgrams = vocabulary;

        for (int i = 0; i < whichGram - 1; i++) {
            unkl += "@#&UNKL";
            totalNgrams *= vocabulary;
        }

        if (whichGram == 1) {
            long tokens = countTokens(1);
            long types = countTypes(1);
            long zeros = vocabulary - types;

            while (itr.hasNext()) {
                List<Integer> lexiconIndices = itr.next();
                NGram ng = getNGram(lexiconIndices, whichGram);

                ng.setProb(((double) ng.getFreq()) / (tokens + types));
            }

            addNGram(unkl, 1);
            NGram ng = getNGram(unkl, whichGram);

            if(zeros == 0)
            {
                ng.setProb(0.0);                
            }
            else
            {
                ng.setProb(((double) types) / (zeros * (tokens + types)));
            }
        } else {
            LinkedHashMap<List<Integer>, Long> ht = nGramTypes.get(whichGram - 1);
            LinkedHashMap<List<Integer>, Long> ht2 = nGramTokens.get(whichGram - 1);

            while (itr.hasNext()) {
                List<Integer> nGramLexIndices = itr.next();
                
                String nGramLex = NGramImpl.getString(this, nGramLexIndices);
                
                int index = nGramLex.lastIndexOf("@#&");
                String n1GramLex = nGramLex.substring(0, index);
                
                List<Integer> n1GramLexIndices = NGramImpl.getIndices(this, n1GramLex, true);
                
                NGram ng = getNGram(n1GramLexIndices, whichGram - 1);

                if (ht != null) {

                    if (!ht.containsKey(n1GramLexIndices)) {
                        ht.put(n1GramLexIndices, 1L);
                        ht2.put(n1GramLexIndices, ng.getFreq());
                    } else {
                        ht.put(n1GramLexIndices, ht.get(n1GramLexIndices) + 1L);
                        ht2.put(n1GramLexIndices, ht2.get(n1GramLexIndices) + ng.getFreq());
                    }
                }
            }

            Iterator<List<Integer>> itr2 = getNGramKeys(whichGram);

            while (itr2.hasNext()) {
                List<Integer> nGramLexIndices = itr2.next();
                String nGramLex = NGramImpl.getString(this, nGramLexIndices);

                int index = nGramLex.lastIndexOf("@#&");
                String n1GramLex = nGramLex.substring(0, index);

                NGram ng = getNGram(nGramLexIndices, whichGram);
                NGram n1g = getNGram(n1GramLex, whichGram - 1);

                List<Integer> n1GramLexIndices = NGramImpl.getIndices(this, n1GramLex, false);

                ng.setProb(((double) ng.getFreq()) / (n1g.getFreq() + ht.get(n1GramLexIndices)));

            }

            long zeros = totalNgrams - countTokens(whichGram);
//            // ??
//            long zeros = totalNgrams - countTypes(whichGram);
            
            addNGram(unkl, whichGram);
            NGram ng = getNGram(unkl, whichGram);

            ng.setProb(((double) 1) / (zeros));

            Iterator<List<Integer>> itr3 = ht.keySet().iterator();

            while (itr3.hasNext()) {

                List<Integer> keyIndices = itr3.next();
                String key = NGramImpl.getString(this, keyIndices);

                addNGram(key + "@#&" + "UNKL", whichGram);
                NGram ngU = getNGram(key + "@#&" + "UNKL", whichGram);

                Long types = ht.get(keyIndices);
                Long tokens = ht2.get(keyIndices);

                long zerosU = vocabulary - (long) types;

                //System.out.println(key+"\t"+types+"\t"+tokens+"\t"+zeros);
                if(zeros == 0)
                {
                    ngU.setProb(0.0);                
                }
                else
                {
                    ngU.setProb(((double) types) / (zeros * (types + tokens)));
                }
            }
        }
    }

    @Override
    public void calcSmoothGoodTuring(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }
        
        getVocabularySize();

        double[] countArray;
        countArray = new double[goodTuringK + 2];
        String unkl = "UNKL";

        countArray[0] = vocabulary;

        for (int i = 0; i < whichGram - 1; i++) {
            countArray[0] *= vocabulary;
            unkl += "@#&UNKL";
        }

        countArray[0] -= countTokens(whichGram);

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        Index hashIndex = new HashIndex();

        while (itr.hasNext()) {
            List<Integer> nGramLexIndices = itr.next();
            NGram ng = getNGram(nGramLexIndices, whichGram);

            long freq = ng.getFreq();
            if (freq <= goodTuringK + 1) {
                countArray[(int) freq] += 1;
            }

        }

        double commonTerm = (double) ((goodTuringK + 1) * countArray[goodTuringK + 1]) / countArray[1];

        //System.out.println((goodTuringK+1) +"\t" + countArray[1] + "\t" + countArray[goodTuringK+1]);

        for (int i = 0; i <= goodTuringK; i++) {
            //System.out.println((goodTuringK+1) +"\tcount:\t" + countArray[1] + "\t" + countArray[goodTuringK+1]);
            //System.out.println(i+ "\tcount:\t" + countArray[i] + "\t" + commonTerm);
            countArray[i] = ((double) ((double) (i + 1) * countArray[i + 1] / countArray[i]) - (i * commonTerm)) / (1 - commonTerm);
        }

        Iterator<List<Integer>> itr2 = getNGramKeys(whichGram);

        if (whichGram == 1) {
            while (itr2.hasNext()) {

                List<Integer> lexiconIndices = itr2.next();

                NGram ng = getNGram(lexiconIndices, whichGram);
                long freq = ng.getFreq();
                
                if (freq <= goodTuringK) {
                    ng.setProb((double) countArray[(int) freq] / countTypes(whichGram));
                //ng.setProb((double)countArray[(int)freq]);
                }
            }
            addNGram(unkl, whichGram);
            NGram ng = getNGram(unkl, whichGram);
            ng.setProb((double) countArray[0] / countTypes(whichGram));

        } else {

            while (itr2.hasNext()) {
                List<Integer> nGramLexIndices = itr2.next();

                String nGramLex = NGramImpl.getString(this, nGramLexIndices);
                
                int index = nGramLex.lastIndexOf("@#&");
                String n1GramLex = nGramLex.substring(0, index);

                NGram ng = getNGram(nGramLex, whichGram);
                NGram n1g = getNGram(n1GramLex, whichGram - 1);

                long freq = ng.getFreq();
                if (freq <= goodTuringK) {
                    //ng.setProb((double)countArray[(int)freq]);
                    ng.setProb((double) countArray[(int) freq] / (n1g.getFreq()));
                }

                if (!hashIndex.contains(n1GramLex)) {
                    hashIndex.add(n1GramLex);
                }
            }

            Iterator<String> itr3 = hashIndex.iterator();
            while (itr3.hasNext()) {
                String n1GramLex = (String) itr3.next();
                addNGram(n1GramLex + "@#&" + "UNKL", whichGram);
                NGram ng = getNGram(n1GramLex + "@#&" + "UNKL", whichGram);
                NGram n1g = getNGram(n1GramLex, whichGram - 1);

                ng.setProb((double) countArray[0] / n1g.getFreq());

            }

            int index = unkl.lastIndexOf("@#&");
            String n1GramLex = unkl.substring(0, index);
            addNGram(unkl, whichGram);
            NGram ng = getNGram(unkl, whichGram);
            NGram n1g = getNGram(n1GramLex, whichGram - 1);
            ng.setProb((double) countArray[0] / n1g.getFreq());
        }

    }

    @Override
    public void removeUNKL(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        Iterator<List<Integer>> itr1 = getNGramKeys(whichGram);
        
        Index remIndex = new HashIndex();

        while (itr1.hasNext()) {
            List<Integer> nGramLexIndices = itr1.next();
            
            String nGramLex = NGramImpl.getString(this, nGramLexIndices);
            
            if (nGramLex.matches(".*UNKL")) {
                remIndex.add(nGramLex);
//                removeNGram(nGramLex, whichGram);
            }
        }
        
        Iterator<String> itr2 = remIndex.iterator();
        
        while(itr2.hasNext())
        {
            removeNGram(itr2.next(), whichGram);
        }
    }

    @Override
    public void calcBackoff() {
        for (int whichGram = nGramOrder; whichGram > 1; whichGram--) {
            removeUNKL(whichGram);
        }

        for (int whichGram = nGramOrder; whichGram > 1; whichGram--) {
            LinkedHashMap<List<Integer>, double[]> backOffHash = new LinkedHashMap<List<Integer>, double[]>(0, 10);

            Iterator<List<Integer>> itr = getNGramKeys(whichGram - 1);

            while (itr.hasNext()) {
                double[] value;
                value = new double[2];
                value[0] = 0.0;
                value[1] = 0.0;
                List<Integer> n1GramLex = itr.next();
                backOffHash.put(n1GramLex, value);
            }

            Iterator<List<Integer>> itr1 = getNGramKeys(whichGram);

            while (itr1.hasNext()) {
                List<Integer> nGramLexIndices = itr1.next();
                
                String nGramLex = NGramImpl.getString(this, nGramLexIndices);

                if (!nGramLex.matches(".*UNKL")) {
                    int Lindex = nGramLex.lastIndexOf("@#&");
                    int Findex = nGramLex.indexOf("#&");
                    String numer = nGramLex.substring(0, Lindex);
                    
                    List<Integer> numerIndices = NGramImpl.getIndices(this, numer, false);
                    
                    String denom = nGramLex.substring(Findex + 2);

                    List<Integer> denomIndices = NGramImpl.getIndices(this, denom, false);
                    //System.out.println(nGramLex + "::" + numer + "::" + denom);
                    NGram ng = getNGram(nGramLexIndices, whichGram);
                    NGram n1g = getNGram(denomIndices, whichGram - 1);

                    double[] n1GramValue;
                    n1GramValue = new double[2];

                    n1GramValue = (double[]) backOffHash.get(numerIndices);

                    //System.out.println(numer + "::" + Double.toString(n1GramValue[0]) + "::" +Double.toString(n1GramValue[1]));
                    //System.out.println(numer + "::" + Double.toString(ng.getProb()) + "::" +Double.toString(n1g.getProb()));

                    n1GramValue[0] += ng.getProb();
                    n1GramValue[1] += n1g.getProb();
                    
                    backOffHash.put(numerIndices, n1GramValue);
                //System.out.println(numer + "::" + Double.toString(n1GramValue[0]) + "::" +Double.toString(n1GramValue[1]));
                }
            }

            Iterator<List<Integer>> itr2 = getNGramKeys(whichGram - 1);

            while (itr2.hasNext()) {
                List<Integer> n1GramLexIndices = itr2.next();
                
                NGram n1g = getNGram(n1GramLexIndices, whichGram - 1);

                double[] n1GramValue;
                n1GramValue = new double[2];
                n1GramValue = (double[]) backOffHash.get(n1GramLexIndices);
                double backOffWt = (1 - n1GramValue[0]) / (1 - n1GramValue[1]);
                //System.out.println(n1GramLex + "::" + Double.toString(n1GramValue[0]) + "::" +Double.toString(n1GramValue[1]));
                n1g.setBackwt(backOffWt);
            }
        }
    }

    private void calcDotCounts() {

        if (nGramOrder >= 2) {
            Iterator<List<Integer>> itr = getNGramKeys(2);
            LinkedHashMap<List<Integer>, Integer> ht = KNDotCounts.get(0);

            while (itr.hasNext()) {
                List<Integer> nGramLexIndices = itr.next();
                
                String nGramLex = NGramImpl.getString(this, nGramLexIndices);
                
                int Lindex = nGramLex.lastIndexOf("@#&");
                String lastLex = nGramLex.substring(Lindex + 3);
                
                List<Integer> lastLexIndices = NGramImpl.getIndices(this, lastLex, false);

                if (!ht.containsKey(lastLexIndices)) {
                    ht.put(lastLexIndices, 1);
                } else {
                    ht.put(lastLexIndices, ((Integer) ht.get(lastLexIndices)) + 1);
                }
            }
        }

        for (int whichGram = 2; whichGram <= nGramOrder; whichGram++) {
            Iterator<List<Integer>> itr = getNGramKeys(whichGram);
            LinkedHashMap<List<Integer>, Integer> ht = KNDotCounts.get(whichGram - 1);

            while (itr.hasNext()) {
                List<Integer> nGramLexIndices = itr.next();
                
                String nGramLex = NGramImpl.getString(this, nGramLexIndices);

                int Lindex = nGramLex.lastIndexOf("@#&");
                String n1GramLex = nGramLex.substring(0, Lindex);

                List<Integer> n1GramLexIndices = NGramImpl.getIndices(this, n1GramLex, false);

                if (!ht.containsKey(n1GramLexIndices)) {
                    ht.put(n1GramLexIndices, 1);
                } else {
                    ht.put(n1GramLexIndices, ht.get(n1GramLexIndices) + 1);
                }
            }
        }
    }

    @Override
    public void calcSmoothKneserNey(double delta) {
        KNdelta = delta;
        KNDotCounts = new ArrayList<LinkedHashMap<List<Integer>, Integer>>(nGramOrder);

        for (int i = 0; i <= nGramOrder; i++) {
            KNDotCounts.add(new LinkedHashMap<List<Integer>, Integer>(0, 10));
        }

        calcDotCounts();

        if (nGramOrder >= 1) {
            Iterator<List<Integer>> itr = getNGramKeys(1);
            LinkedHashMap<List<Integer>, Integer> ht = KNDotCounts.get(0);

            while (itr.hasNext()) {
                List<Integer> nGramLexIndices = itr.next();
                NGram ng = getNGram(nGramLexIndices, 1);
                
                Integer numer = (Integer) ht.get(nGramLexIndices);
                
                if(numer == null) {
                    continue;
                }

                Long denom = countTokens(2);

                ng.setProb((double) numer / denom);
            }
        }

        for (int whichGram = 2; whichGram <= nGramOrder; whichGram++) {
            LinkedHashMap<List<Integer>, Integer> ht = KNDotCounts.get(whichGram - 1);
            Iterator<List<Integer>> itr = getNGramKeys(whichGram);
            while (itr.hasNext()) {
                List<Integer> nGramLexIndices = itr.next();

                String nGramLex = NGramImpl.getString(this, nGramLexIndices);
                
                int Lindex = nGramLex.lastIndexOf("@#&");
                int Findex = nGramLex.indexOf("#&");
                String n1GramLex = nGramLex.substring(0, Lindex);
                String numer = nGramLex.substring(Findex + 2);

                List<Integer> n1GramLexIndices = NGramImpl.getIndices(this, n1GramLex, false);
                
                NGram ng = getNGram(nGramLex, whichGram);
                NGram n1g = getNGram(n1GramLex, whichGram - 1);
                NGram numg = getNGram(numer, whichGram - 1);

                double firstTerm = (double) (ng.getFreq() - KNdelta) / (n1g.getFreq());
                double secondTerm = (double) (KNdelta * (Integer) ht.get(n1GramLexIndices)) / (n1g.getFreq());
                ng.setProb(firstTerm + secondTerm * numg.getProb());
            }
        }
    }

    @Override
    public double getSmoothKneserNeyProb(String nGramKey, int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return 0;
        }

        if (hasNGram(nGramKey, whichGram)) {
            NGram ng = getNGram(nGramKey, whichGram);
            return ng.getProb();
        } else {
            boolean NBREAK = true;
            while (NBREAK) {
                int Lindex = nGramKey.lastIndexOf("@#&");
                int Findex = nGramKey.indexOf("#&");
                String n1GramLex = nGramKey.substring(0, Lindex);

                List<Integer> n1GramLexIndices = NGramImpl.getIndices(this, n1GramLex, false);

                String numer = nGramKey.substring(Findex + 2);
                if (hasNGram(numer, whichGram - 1)) {
                    NGram n1g = getNGram(n1GramLex, whichGram - 1);
                    NGram numg = getNGram(numer, whichGram - 1);
                    LinkedHashMap<List<Integer>, Integer> ht = KNDotCounts.get(whichGram - 1);
                    double secondTerm = (double) (KNdelta * (Integer) ht.get(n1GramLexIndices)) / (n1g.getFreq());
                    NBREAK = false;
                    return secondTerm * numg.getProb();
                } else if (whichGram - 1 == 1) {
                    return 1;
                } else {
                    nGramKey = numer;
                    whichGram--;
                }
            }
        }
        return 0;
    }

    @Override
    public void cleanNGramLM() {
        // It will take care of the invalid words in the corpus.
    }

    @Override
    public void printNGram(String wds, int whichGram, PrintStream ps, boolean printFrequency) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }
        
//        DecimalFormat df = new DecimalFormat("0.0000000");
        DecimalFormat df = new DecimalFormat("#.#######");
        
        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, false);
        
        NGram ng = getNGram(wdIndices, whichGram);

        wds = wds.replaceAll("@#&", " ");

//        ps.print(ng.getProb() + " " + wds + " ");
        // Print logprobs
        ps.print(df.format(Math.log10(ng.getProb())) + "\t" + wds);

        if (whichGram == getNGramOrder() && printFrequency) {
            ps.println("\t" + ng.getFreq());
        } else {
            if(printFrequency)
            {
                if(ng.getBackwt() != 0.0)
                {
                    ps.println("\t" + df.format(Math.log10(ng.getBackwt())) + "\t" + ng.getFreq());                
                }
                else
                {
                    ps.println("\t" + "-Infinity" + "\t" + ng.getFreq());                                    
                }
            }
            else
            {
                if(ng.getBackwt() != 0.0)
                {
                    ps.println("\t" + df.format(Math.log10(ng.getBackwt())));                
                }                
                else
                {
                    ps.println("\t-Infinity");
                }
            }
//            ps.println("\t" + Double.toString(ng.getBackwt()) + " " + ng.getFreq() + "\t" + ng.getProb());
        }
    }

    @Override
    public void normalizeNGramProbs(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        double prob = 0.0;
        double probsum = 0.0;
        int wt = 0;

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);
        while (itr.hasNext()) {
            List<Integer> key = itr.next();
            NGram ng = getNGram(key, whichGram);
            wt = ng.getWeight();
            probsum = +wt * ng.getProb();
        }

        itr = getNGramKeys(whichGram);
        while (itr.hasNext()) {
            List<Integer> key = itr.next();
            NGram ng = getNGram(key, whichGram);
            prob = ng.getProb();
            wt = ng.getWeight();
            prob = (double) (wt * prob) / probsum;
            ng.setProb(prob);
        }
    }

    @Override
    public void normalizeNGramProbs() {
        for (int i = 1; i <= nGramOrder; i++) {
            normalizeNGramProbs(i);
        }
    }

    @Override
    public double getSentenceProb(String sentence) {
        int N = getNGramOrder();

        Pattern p = Pattern.compile("[\\s]");

        String[] splitstr = p.split(sentence);
        double total_prob = 0;

        for (int k = 0; k < splitstr.length; k++) {
            String wrdk = splitstr[k];
            if (k == 0) {
                NGram ng = getNGram(wrdk, 1);
                if (ng != null) {
                    total_prob = ng.getProb();
                }
            } else if (k == 1) {
                String wrdk_min_one = splitstr[k - 1];
                String wrd = wrdk_min_one + "@#&" + wrdk;
                NGram ng = getNGram(wrd, 2);

                if (ng != null) {
                    total_prob *= ng.getProb();
                } else {
                    ng = getNGram(wrdk_min_one, 1);
                    if (ng != null) {
//                        total_prob *= ng.getProb() * ng.getBackwt();
                        total_prob *= Math.pow(ng.getProb(), 4.0);
                    }
                }
            } else {
                String wrdk_min_one = splitstr[k - 1];
                String wrdk_min_two = splitstr[k - 2];

                String wrd = wrdk_min_two + "@#&" + wrdk_min_one + "@#&" + wrdk;
                NGram ng = getNGram(wrd, 3);

                if (ng != null) {
                    total_prob *= ng.getProb();
                } else {
                    wrd = wrdk_min_two + "@#&" + wrdk_min_one;
                    ng = getNGram(wrd, 2);

                    if (ng != null) {
//                        total_prob *= ng.getProb() * ng.getBackwt();
                        total_prob *= Math.pow(ng.getProb(), 2.0);
                    } else {
                        wrd = wrdk_min_two;
                        ng = getNGram(wrd, 1);
                        if (ng != null) {
//                            total_prob *= ng.getProb() * ng.getBackwt();
                            total_prob *= Math.pow(ng.getProb(), 8.0);
                        }
                    }
                }
            }
        }

        return total_prob;
    }

    @Override
    public float getSentencePosteriorProb(String sentence, Semiring semiring, int whichGram)
    {
        float prob = semiring.one();
        
        String words[] = sentence.split("[\\s+]");
        
        String ngString = "";
        
        for (int i = whichGram; i <= words.length; i++) {
            
            for (int j = (i - whichGram + 1); j <= i; j++) {
                ngString += " " + words[j - 1];                
            }
            
            NGram ng = getNGramPlain(ngString, whichGram);
            
            if(ng != null)
            {
                float ngprob = new Double(ng.getProb()).floatValue();
                
                if(semiring instanceof LogSemiring)
                {
                    ngprob = new Double(Math.log(ngprob)).floatValue();
                }
                
                prob = semiring.times(prob, ngprob);
            }

            ngString = "";
        }

        return prob;
    }

    // When the model is char ngram model
    @Override
    public double getPhonemeSequenceProb(String sequence, PhonemeFeatureModel phonemeFeatureModel) {
        int N = getNGramOrder();

        Pattern p = Pattern.compile("[\\s]");

        String[] splitstr = p.split(sequence);
        double total_prob = 0;

        for (int k = 0; k < splitstr.length; k++) {
            String wrdk = splitstr[k];
            if (k == 0) {
                NGram ng = getNGram(wrdk, 1);
                if (ng != null) {
//                    total_prob = ng.getProb();
                    total_prob = phonemeFeatureModel.getPhonemeUniGramProb(wrdk);
                }
            } else if (k == 1) {
                String wrdk_min_one = splitstr[k - 1];
                String wrd = wrdk_min_one + "@#&" + wrdk;
                NGram ng = getNGram(wrd, 2);

                if (ng != null) {
//                    total_prob *= ng.getProb();
                  total_prob = phonemeFeatureModel.getPhonemeBiGramProb(wrd);
                } else {
                    ng = getNGram(wrdk_min_one, 1);
                    if (ng != null) {
//                        total_prob *= ng.getProb() * ng.getBackwt();
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrdk_min_one), 3.0);
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrdk_min_one), 2.0);
                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrdk_min_one), 4.0);
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrdk_min_one), 5.0);
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrdk_min_one), 4.5);
                    }
                }
            } else {
                String wrdk_min_one = splitstr[k - 1];
                String wrdk_min_two = splitstr[k - 2];

                String wrd = wrdk_min_two + "@#&" + wrdk_min_one + "@#&" + wrdk;
                NGram ng = getNGram(wrd, 3);

                if (ng != null) {
//                    total_prob *= ng.getProb();
                    total_prob *= phonemeFeatureModel.getPhonemeTriGramProb(wrd);
                } else {
                    wrd = wrdk_min_two + "@#&" + wrdk_min_one;
                    ng = getNGram(wrd, 2);

                    if (ng != null) {
//                        total_prob *= ng.getProb() * ng.getBackwt();
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeBiGramProb(wrd), 4.5);
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeBiGramProb(wrd), 3.0);
                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeBiGramProb(wrd), 2.0);
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeBiGramProb(wrd), 7.5);
//                        total_prob *= Math.pow(phonemeFeatureModel.getPhonemeBiGramProb(wrd), 6.5);
                    } else {
                        wrd = wrdk_min_two;
                        ng = getNGram(wrd, 1);
                        if (ng != null) {
//                            total_prob *= ng.getProb() * ng.getBackwt();
//                            total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrd), 6.0);
//                            total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrd), 4.0);
                            total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrd), 8.0);
//                            total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrd), 9.0);
//                            total_prob *= Math.pow(phonemeFeatureModel.getPhonemeUniGramProb(wrd), 8.5);
                        }
                    }
                }
            }
        }

        return total_prob;
    }

    @Override
    public void sort(int sortOrder)
    {
        for (int i = 1; i <= getNGramOrder(); i++)
        {
            LinkedHashMap<List<Integer>, NG> ngrams = nGrams.get(i - 1);
            
            switch (sortOrder) {
                case NGram.SORT_BY_FREQ:
                    ngrams = (LinkedHashMap<List<Integer>, NG>) UtilityFunctions.sort(ngrams, new ByNGramFreq());
                    nGrams.set(i - 1, ngrams);
                    break;

                case NGram.SORT_BY_FREQ_DESC:
                    ngrams = (LinkedHashMap<List<Integer>, NG>) UtilityFunctions.sort(ngrams, new ByNGramFreqDesc());
                    nGrams.set(i - 1, ngrams);
                    break;

                case NGram.SORT_BY_PROB:
                    ngrams = (LinkedHashMap<List<Integer>, NG>) UtilityFunctions.sort(ngrams, new ByNGramProb());
                    nGrams.set(i - 1, ngrams);
                    break;

                default:
                    ngrams = (LinkedHashMap<List<Integer>, NG>) UtilityFunctions.sort(ngrams, new ByNGramProb());
                    nGrams.set(i - 1, ngrams);
            }
        }        
    }

    @Override
    public List<NG> sort(int sortOrder, int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return null;
        }

        LinkedHashMap<List<Integer>, NG> ht = nGrams.get(whichGram - 1);
        List<NG> sorted = new ArrayList<NG>(ht.values());

        switch (sortOrder) {
            case NGram.SORT_BY_FREQ:
                Collections.sort(sorted, new ByNGramFreq());
                break;

            case NGram.SORT_BY_FREQ_DESC:
                Collections.sort(sorted, new ByNGramFreqDesc());
                break;

            case NGram.SORT_BY_PROB:
                Collections.sort(sorted, new ByNGramProb());
                break;

            default:
                Collections.sort(sorted, new ByNGramProb());
        }

        return sorted;
    }

    @Override
    public void clear() {
        for (int i = 0; i < nGramOrder; i++) {
            nGrams.get(i).clear();
            
            if(KNDotCounts != null)
            {
                KNDotCounts.clear();
            }

            if(nGramTokens != null)
            {
                nGramTokens.clear();
            }

            if(nGramTypes != null)
            {
                nGramTypes.clear();
            }
        }
    }

    @Override
    public Object clone() {
        NGramLMImpl obj = (NGramLMImpl) super.clone();
        obj.nGramOrder = nGramOrder;

        obj.nGramLMFile = nGramLMFile;

        // Implementation is currently limited to only 4-grams
        obj.nGrams = (List) ((ArrayList) nGrams).clone();

        for (int i = 0; i < nGramOrder; i++) {
            LinkedHashMap<List<Integer>, NG> oldht = nGrams.get(i);
            LinkedHashMap<List<Integer>, NG> ht = (LinkedHashMap<List<Integer>, NG>) oldht.clone();

            Iterator<List<Integer>> enm = ht.keySet().iterator();

            while (enm.hasNext()) {
                List<Integer> key = enm.next();
                NG ng = (NG) oldht.get(key);
                ht.put(key, (NG) ng.clone());
            }

            obj.nGrams.set(i, ht);
        }

        return obj;
    }

    @Override
    public void pruneByRankAndMerge(int rank, int whichGram) {
        pruneByRank(rank, whichGram);
        calcMergedProbs();
//        calcProbs();
    }

    @Override
    public void pruneByRank(int rank, int whichGram) {
        if (nGramOrder <= 0) {
            return;
        }

        int pruned = 0;

        if (whichGram <= 0 || whichGram > nGramOrder) {
            LinkedHashMap<List<Integer>, NG> oldht = nGrams.get(0);
            List<NG> sortedNGrams = new ArrayList<NG>(oldht.size());

            // Prune ngrams of all orders
            for (int i = 1; i <= nGramOrder; i++) {
                List<NG> sortedNGramsTmp = sort(NGram.SORT_BY_FREQ, i);
                sortedNGrams.addAll(sortedNGramsTmp);
            }

            Collections.sort(sortedNGrams, new ByNGramFreq());

            // Fill in the ranks
            int count = sortedNGrams.size();
            for (int i = 0; i < count; i++) {
                NGram ng = (NGram) sortedNGrams.get(i);
                ng.setRank(count - i);
            }

            // No pruning, only ranking
            if (rank == -1) {
                rank = sortedNGrams.size();
            }

            int extra = sortedNGrams.size() - rank;

            for (int i = 0; i < extra; i++) {
                NGram ng = (NGram) sortedNGrams.get(i);
                String key = ng.getString(this);

                for (int j = 1; j <= nGramOrder; j++) {
                    if (removeNGram(key, j) != null) {
                        pruned++;
                        j = nGramOrder;
                    }
                }
            }
        } else {
            // Prune ngrams of a specific order
            List<NG> sortedNGrams = sort(NGram.SORT_BY_FREQ, whichGram);

            // Fill in the ranks
            int count = sortedNGrams.size();
            for (int i = 0; i < count; i++) {
                NG ng = sortedNGrams.get(i);
                ng.setRank(count - i);
            }

            // No pruning, only ranking
            if (rank == -1) {
                rank = sortedNGrams.size();
            }

            int extra = sortedNGrams.size() - rank;

            for (int i = 0; i < extra; i++) {
                NGram ng = (NGram) sortedNGrams.get(i);
                String key = ng.getString(this);
                removeNGram(key, whichGram);
                pruned++;
            }
        }

//        System.out.println("\tPruned: " + pruned);
    }

    @Override
    public void fillRanks(int whichGram) {
    }

    @Override
    public void makeTriggerPairs() {
    }

    @Override
    public NGramLM getCPMSFeaturesNGramLM(File f) {
        if (nGramType.equalsIgnoreCase("uchar") == false) {
            System.out.println(GlobalProperties.getIntlString("The_NGramLM_should_be_of_type_\"uchar\"."));
            return null;
        }

        NGramLM cpmsFeaturesNGramLM = new NGramLMImpl(f, "uchar", 5);

        PhoneticModelOfScripts cpms = null;

        try {
            cpms = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
                    GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));

            cpms.setNgramLM(cpmsFeaturesNGramLM);
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        cpms.getFeatureVector();
        for (int i = 1; i <= nGramOrder; i++) {
            Iterator<List<Integer>> itr = getNGramKeys(i);
            while (itr.hasNext()) {
                List<Integer> key = itr.next();
                NGram ng = getNGram(key, i);

                String ngStr = ng.getString(this);

//                System.out.println(ngStr);

                String charStrs[] = ngStr.split("@#&");

                PropertyTokens featureList = cpms.getFeatureList();

                int fcount = featureList.countTokens();

                for (int j = 0; j < fcount; j++) {
                    String feature = featureList.getToken(j);

//                    System.out.println(feature);

                    String fngStr = "";

                    for (int k = 0; k < charStrs.length; k++) {
                        String chStr = charStrs[k];
//                        System.out.println("\t" + chStr);

                        String fvStr = "NULL";

                        if (chStr.equals("") == false) {
                            char ch = chStr.charAt(0);

                            FeatureStructure fs = cpms.getFeatureStructure(ch, language);

                            if (fs != null) {
//                                System.out.println(fs.makeString());

                                FeatureAttribute fa = fs.getAttribute(feature);

                                FeatureValue fv = null;

                                if (fa != null) {
                                    fv = fa.getAltValue(0);

                                    if (fv != null) {
                                        fvStr = (String) fv.getValue();
                                    }
                                }
                            }
                        }

                        fvStr = feature + "=" + fvStr;

                        if (k == charStrs.length - 1) {
                            fngStr += fvStr;
                        } else {
                            fngStr += fvStr + "@#&";
                        }
                    }

                    if (fngStr.equals("") == false) {
                        cpmsFeaturesNGramLM.addNGram(fngStr, i);
                    }
                }
            }
        }

        cpmsFeaturesNGramLM.calcCountsNProbs();


        return cpmsFeaturesNGramLM;
    }

    public static <NG extends NGram> double getSimilarity(NGramLM<NG> nGramLM1, NGramLM<NG> nGramLM2) {
        double sim = 0.0;
        double norm = 0.0;

        if (nGramLM1 == null || nGramLM2 == null) {
            return sim;
        }

        for (int j = 1; j <= nGramLM1.getNGramOrder(); j++) {
            Iterator<List<Integer>> itr2 = nGramLM2.getNGramKeys(j);

            while (itr2.hasNext()) {
                List<Integer> nGram2Key = itr2.next();
                NG nGram2 = nGramLM2.getNGram(nGram2Key, j);
                NG nGram1 = nGramLM1.getNGram(nGram2Key, j);

                if (nGram1 != null) {
                    sim += nGram1.getProb() * Math.log(nGram1.getProb()) + nGram2.getProb() * Math.log(nGram2.getProb());
//                    sim += Math.log(nGram1.getProb()) + Math.log(nGram2.getProb());
                }
            }
        }

        return sim;
    }

    public static <NG extends NGram> double getDistance(NGramLM<NG> nGramLM1, NGramLM<NG> nGramLM2) {
//        double sim = getSimilarity(nGramLM1, nGramLM2);
//
//        double selfSim = getSimilarity(nGramLM1, nGramLM1) + getSimilarity(nGramLM2, nGramLM2) / 2.0;
//
//        double diff = selfSim - sim;
//
//        return diff;

        int count = 0;

        if (nGramLM1 == null || nGramLM2 == null)
        {
            return count;
        }

        for (int j = 1; j <= nGramLM1.getNGramOrder(); j++)
        {
            Iterator<List<Integer>> itr2 = nGramLM2.getNGramKeys(j);

            while (itr2.hasNext())
            {
                List<Integer> nGram2Key = itr2.next();
                NGram nGram2 = nGramLM2.getNGram(nGram2Key, j);
                NGram nGram1 = nGramLM1.getNGram(nGram2Key, j);

                if (nGram1 == null)
                    count++;
//                    count += nGram2.getFreq();
            }
        }

        return (double) count;
    }

    public static int getNumNGrams(NGramLM nglm, double probThreshold, int whichGram)
    {
        if (whichGram > nglm.getNGramOrder() || whichGram < 1) {
            return 0;
        }

        int numNGrams = 0;

        Iterator<List<Integer>> itr = nglm.getNGramKeys(whichGram);
        while (itr.hasNext()) {
            List<Integer> key = itr.next();
            NGram ng = (NGram) nglm.getNGram(key, whichGram);
            
            if(ng.getProb() >= probThreshold)
            {
                numNGrams++;
            }
        }        
        
        return numNGrams;
    }

    public static void main(String args[]) {
        File file = new File("data/ngram/ngramlm-test-sample.txt.lm");
//        File file = new File("/extra/work/questimate/tmp/corrections_en2fr.data.src.fte.utf8.lc");
//        File file1 = new File("/extra/work/questimate/tmp/en-test.txt");
//          File file = new File("/home/anil/tmp/tmp.utf8");
//          File file = new File("/var/ftp/anil/documents/literature/new/mahabharat");
//            File file = new File("C:/Documents and Settings/eklavya/My Documents/marathi-ciil/marathi-utf8");
//          File file = new File("/home/anil/ftp/anil/ltrc/resources/stable/ciil-corpus/raw/Hindi/raw-ver-0.2/hindi-raw-ver-0.2-preprocessed-wx/12.naval.utf8.isc.wx");
//          File file = new File("/home/anil/exec/sphinx4-old/tests/decoder/spell.arpa");
//          File file = new File("/home/anil/tmp/ngram.txt");
//          nglm.clear();
        NGramCounts<NGramCount> nglm = new NGramCountsImpl(file, "word", 3);
        
//            File fout = new File("C:/Documents and Settings/eklavya/My Documents/marathi-ciil/word-list-ciil-mar.txt");
        String ofile = "data/ngram/ngramlm-test-sample.txt.lm";
//        String ofile = "data/ngram/ngramlm-test-sample.lm.wb.txt";
//        
        File fout = new File(ofile);
//            File cpmsFout = new File("props/spell-checker/names-trial.cpms");

        try {
		nglm.readNGramLM(file);
//            nglm = NGramLMImpl.loadNGramLMBinary(new File(file.getAbsolutePath() + ".lm.bin"));
//            nglm.makeNGramLM(file);
//            nglm.makeNGramLM(file, "ISO-8859-1");
//            nglm.saveNGramLM(file.getAbsolutePath() + ".lm", "ISO-8859-1", true);
//            NGramLMImpl.saveNGramLMBinary(nglm, new File(file.getAbsolutePath() + ".lm.bin"));

            NGramLM<NGram> nglm1 = new NGramLMImpl(file, "word", 3, nglm.getVocabIndex(), false);
//            NGramCounts<NGramCount> nglm1 = new NGramCountsImpl(file, "word", 3, nglm.getVocabIndex());
            
            nglm1.makeNGramLM("the vina resembles the yantra in one more sense .");
            
            nglm1.calcSimpleProbs();
            
            System.out.println("the : " + nglm1.getSentencePosteriorProb("the vina resembles the yantra in one", new LogSemiring(), 3));
            System.out.println("the vina: " + nglm1.getSentencePosteriorProb("in one more sense .", new LogSemiring(), 3));
            
//            SSFStory story = new SSFStoryImpl();
//            
//            story.readFile(file1.getAbsolutePath(), "UTF-8");
            
            nglm.sort();

            List<LinkedHashMap<List<Integer>, Long>> cumFreqsList = nglm.getCumulativeFrequenciesList();
            List<Long> topCumFreqList = new ArrayList<Long>();

            for (int i = 0; i < cumFreqsList.size(); i++) {
                topCumFreqList.add((Long) UtilityFunctions.getLastElement(cumFreqsList.get(i)));
            }        
                    
            List<List<Double>> pcGrams = NGramCountsImpl.percentNGramsInQuartile(nglm1,
                    cumFreqsList, topCumFreqList);
            
           System.out.println("Percent 1-grams not found: " + pcGrams.get(0).get(0));
            System.out.println("Percent 1-grams quartile 1: " + pcGrams.get(0).get(1));
            System.out.println("Percent 1-grams quartile 2: " + pcGrams.get(0).get(2));
            System.out.println("Percent 1-grams quartile 3: " + pcGrams.get(0).get(3));
            System.out.println("Percent 1-grams quartile 4: " + pcGrams.get(0).get(4));
            
//            cumFreqs = nglm.getCumulativeFrequencies(2);
//            topCumFreq = (Long) UtilityFunctions.getLastElement(cumFreqs);
//                    
//            pcGrams = NGramLMImpl.percentNGramsInQuartile(nglm, nglm1, 2, cumFreqs, topCumFreq);
            
            System.out.println("Percent 2-grams not found: " + pcGrams.get(1).get(0));
            System.out.println("Percent 2-grams quartile 1: " + pcGrams.get(1).get(1));
            System.out.println("Percent 2-grams quartile 2: " + pcGrams.get(1).get(2));
            System.out.println("Percent 2-grams quartile 3: " + pcGrams.get(1).get(3));
            System.out.println("Percent 2-grams quartile 4: " + pcGrams.get(1).get(4));
            
//            cumFreqs = nglm.getCumulativeFrequencies(3);
//            topCumFreq = (Long) UtilityFunctions.getLastElement(cumFreqs);
//                    
//            pcGrams = NGramLMImpl.percentNGramsInQuartile(nglm, nglm1, 3, cumFreqs, topCumFreq);
            
            System.out.println("Percent 3-grams not found: " + pcGrams.get(2).get(0));
            System.out.println("Percent 3-grams quartile 1: " + pcGrams.get(2).get(1));
            System.out.println("Percent 3-grams quartile 2: " + pcGrams.get(2).get(2));
            System.out.println("Percent 3-grams quartile 3: " + pcGrams.get(2).get(3));
            System.out.println("Percent 3-grams quartile 4: " + pcGrams.get(2).get(4));
           
//            nglm.calcSmoothProbs("Witten-Bell", 5);
//            nglm.calcSmoothKneserNey(0.2);
//            nglm.calcBackoff();
//            nglm.saveNGramLM(fout.getAbsolutePath(), "UTF-8");
            
//            nglm1.readNGramLM(fout, "UTF-8");
//            nglm1.saveNGramLM(fout.getAbsolutePath() + ".out", "UTF-8", false);
            //nglm.calcSmoothProbs("Witten-Bell",100);
            //nglm.calcSmoothProbs("Good-Turing",100,4);
            //nglm.calcSmoothProbs("Good-Turing",100,2);
            //nglm.calcBackoff();
//            nglm.calcSmoothKneserNey(0.5);
//		nglm.pruneByRank(50, 0);
//		NGramLMImpl.storeNGramLM(nglm, new File("/home/anil/tmp/ngram.nglm"));
//              ps = new PrintStream("/home/anil/tmp/mahabharat-unigram.txt", "UTF8");
//                cpmsPs = new PrintStream(cpmsFout, "UTF8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException_Exception!");
        } catch (Exception e) {
            e.printStackTrace();
        }

//          NGramLM cpmsNglm = nglm.getCPMSFeaturesNGramLM(fout);

//          nglm.saveNGramLMBinary(System.out);
//        nglm.saveNGramLMBinary(ps);
//          cpmsNglm.saveNGramLMBinary(cpmsPs);
//          System.out.println(nglm.sentenceProb("MOVE LEFT FIVE METERS"));
//        try {
//            File fout2 = new File("/home/ambati/study/4-1/anilsir-project/Sanchay-18-08-08/data/bharat/sampleoutput.txt");
//            nglm.readNGramLM(fout2);
//            String ngram = "saB.*";
//            int order = -1;
//            int minFreq = 2, maxFreq = -1;
//            Hashtable matchNgrams = new Hashtable(0, 20);
//            matchNgrams = nglm.findNGramFile(ngram, order, minFreq, maxFreq);
//            nglm.printfNGramFile(matchNgrams);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
//        }
    }
}
