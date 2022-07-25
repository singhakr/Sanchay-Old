/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.mlearning.lm.ngram.impl.NGramLMImpl;
import sanchay.properties.PropertyTokens;
import sanchay.text.DictionaryFST;
import sanchay.text.DictionaryFSTNode;
import sanchay.text.TextNormalizer;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.text.enc.conv.WX2UTF8;
import sanchay.text.spell.InstantiatePhonemeGraph;
import sanchay.translit.TransliteratorMain;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class MLTransliterator {

    protected String srcLangEnc = GlobalProperties.getIntlString("eng::utf8");
    protected String tgtLangEnc = GlobalProperties.getIntlString("hin::utf8");

    protected String srcCharset = GlobalProperties.getIntlString("ISO-8859-1");
    protected String tgtCharset = GlobalProperties.getIntlString("UTF-8");

    protected String srcPath = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/translit-test-data-hindi.txt";
//    protected String srcPath = "/home/anil/tmp/feature_based_code/neList.txt";
    protected String tgtPath = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/translit-test-data-hindi-results.xml";
    protected String wrdOriginPath = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/translit-word-origin.txt";
    
    protected PhraseTranslationTable phraseTranslationTable;

    protected String phraseTranslationTablePath;
    protected String phraseTranslationTableCharset = GlobalProperties.getIntlString("ISO-8859-1");

    protected TextNormalizer textNormalizer;

    protected WordSegmentationGenerator wordSegmentationGenerator;

    protected SanchayEncodingConverter sanchayEncodingConverter;

    protected String charNGramLMImplPath = GlobalProperties.getHomeDirectory() + "/" + "data/word-translation/word-lists/hindi-wordlist-translit.txt";
    protected NGramLMImpl charNGramLMImpl;

    protected InstantiatePhonemeGraph instantiatePhonemeGraph;
    protected String instantiatePhonemeGraphPath = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/NEWS09_dev_EnHi_974-hindi-hin.txt";
    protected NGramLM phoneticFeatureNGramLM;

    protected boolean useStoreCharNGramLM = true;
    protected boolean batchMode = true;
    protected boolean phoneticNGram = false;

    protected PropertyTokens srcWords;

    protected TransliteratorMain transliteratorMain;

    protected int method = LDATM;

    PropertyTokens tgtSMTWords;
    LinkedHashMap smtCandidatesMap;

    public static final int DATM = 0;
    public static final int EDATM = 1;
    public static final int LDATM = 2;
    public static final int SMTFSM = 3;

    public MLTransliterator(String inputFile, String outputFile)
    {
        this();
        
        srcPath = inputFile;
        tgtPath = outputFile;

        batchMode = true;
    }

    public MLTransliterator()
    {        
		instantiatePhonemeGraph = new InstantiatePhonemeGraph(tgtLangEnc, tgtCharset, false);

        phraseTranslationTable = new PhraseTranslationTable();
        
        textNormalizer = new TextNormalizer(getTgtLangEnc(), getTgtCharset(), "", "", false);

        charNGramLMImpl = new NGramLMImpl(new File(charNGramLMImplPath), "uchar", 3, GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
        
        try {
            transliteratorMain = new TransliteratorMain(textNormalizer);
            transliteratorMain.loadPhoneticModelOfScripts();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init()
    {
        if(useStoreCharNGramLM == false)
        {
            try {
                charNGramLMImpl.makeNGramLM(new File(charNGramLMImplPath));

                charNGramLMImpl.calcBackoff();

                NGramLMImpl.storeNGramLM(charNGramLMImpl, new File(charNGramLMImplPath + ".nglmc"));
                NGramLMImpl.storeNGramLMArpa(charNGramLMImpl, new File(charNGramLMImplPath + ".arpac"), tgtCharset, true);

                if(phoneticNGram)
                    phoneticFeatureNGramLM = instantiatePhonemeGraph.createPhonemeUnigrams(new File(instantiatePhonemeGraphPath));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try {
                NGramLMImpl.loadNGramLMBinary(new File(charNGramLMImplPath + ".nglmc"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if(batchMode)
        {
            try {
//                srcWords = new PropertyTokens(srcPath, srcCharset);
                srcWords = new PropertyTokens(srcPath, tgtCharset);

                if(method == SMTFSM)
                {
                    tgtSMTWords = new PropertyTokens("/home/anil/tmp/feature_based_code/eng-10-best-neList-one-line.utf", GlobalProperties.getIntlString("UTF-8"));
//                    tgtSMTWords = new PropertyTokens("/home/anil/tmp/feature_based_code/NEWS-smt-test-10-best.utf.txt", "UTF-8");
                    smtCandidatesMap = new LinkedHashMap();

//                    for(int l = 0;l < tgtSMTWords.countTokens(); l++)
//                    {
//                        String line = tgtSMTWords.getToken(l);
//                        String srcTkns[] = line.split("\\s\\|\\|\\|\\s");
//
//                        String sword = srcWords.getToken(Integer.parseInt(srcTkns[0]));
//
//                        TransliterationCandidates smtCandidates = (TransliterationCandidates) smtCandidatesMap.get(sword);
//
//                        if(smtCandidates == null)
//                        {
//                            smtCandidates = new TransliterationCandidates();
//                            smtCandidatesMap.put(sword, smtCandidates);
//                        }
//
//                        smtCandidates.addTranslationCandidateScores(srcTkns[1], new TranslationCandidateScores());
//
//                    }

                    // One line format for NEs
                    for(int l = 0; l < tgtSMTWords.countTokens(); l++)
                    {
                        String line = tgtSMTWords.getToken(l);
                        String parts[] = line.split("[ ]");

                        String sword = parts[0];

                        for(int k = 1; k < parts.length; k++)
                        {
                            TransliterationCandidates smtCandidates = (TransliterationCandidates) smtCandidatesMap.get(sword);

                            if(smtCandidates == null)
                            {
                                smtCandidates = new TransliterationCandidates();
                                smtCandidatesMap.put(sword, smtCandidates);
                            }

                            smtCandidates.addTranslationCandidateScores(parts[k], new TranslationCandidateScores());
                        }
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        wordSegmentationGenerator = new WordSegmentationGenerator();
        sanchayEncodingConverter = new WX2UTF8(tgtLangEnc);
        
        try {
                phraseTranslationTable.readTranslationTable(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/phrase-table.0-0", srcCharset);
                phraseTranslationTable.pruneAndSortPhraseTranslationScores(3);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the srcLangEnc
     */
    public String getSrcLangEnc() {
        return srcLangEnc;
    }

    /**
     * @param srcLangEnc the srcLangEnc to set
     */
    public void setSrcLangEnc(String srcLangEnc) {
        this.srcLangEnc = srcLangEnc;
    }

    /**
     * @return the tgtLangEnc
     */
    public String getTgtLangEnc() {
        return tgtLangEnc;
    }

    /**
     * @param tgtLangEnc the tgtLangEnc to set
     */
    public void setTgtLangEnc(String tgtLangEnc) {
        this.tgtLangEnc = tgtLangEnc;
    }

    /**
     * @return the srcCharset
     */
    public String getSrcCharset() {
        return srcCharset;
    }

    /**
     * @param srcCharset the srcCharset to set
     */
    public void setSrcCharset(String srcCharset) {
        this.srcCharset = srcCharset;
    }

    /**
     * @return the tgtCharset
     */
    public String getTgtCharset() {
        return tgtCharset;
    }

    /**
     * @param tgtCharset the tgtCharset to set
     */
    public void setTgtCharset(String tgtCharset) {
        this.tgtCharset = tgtCharset;
    }

    /**
     * @return the srcPath
     */
    public String getSrcPath() {
        return srcPath;
    }

    /**
     * @param srcPath the srcPath to set
     */
    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    /**
     * @return the tgtPath
     */
    public String getTgtPath() {
        return tgtPath;
    }

    /**
     * @param tgtPath the tgtPath to set
     */
    public void setTgtPath(String tgtPath) {
        this.tgtPath = tgtPath;
    }

    /**
     * @return the method
     */
    public int getMethod()
    {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(int method)
    {
        this.method = method;
    }

    /**
     * @return the wordSegmentationGenerator
     */
    public WordSegmentationGenerator getWordSegmentationGenerator() {
        return wordSegmentationGenerator;
    }

    /**
     * @param wordSegmentationGenerator the wordSegmentationGenerator to set
     */
    public void setWordSegmentationGenerator(WordSegmentationGenerator wordSegmentationGenerator) {
        this.wordSegmentationGenerator = wordSegmentationGenerator;
    }

    /**
     * @return the phraseTranslationTable
     */
    public PhraseTranslationTable getPhraseTranslationTable() {
        return phraseTranslationTable;
    }

    /**
     * @param phraseTranslationTable the phraseTranslationTable to set
     */
    public void setPhraseTranslationTable(PhraseTranslationTable phraseTranslationTable) {
        this.phraseTranslationTable = phraseTranslationTable;
    }

    /**
     * @return the phraseTranslationTablePath
     */
    public String getPhraseTranslationTablePath() {
        return phraseTranslationTablePath;
    }

    /**
     * @param phraseTranslationTablePath the phraseTranslationTablePath to set
     */
    public void setPhraseTranslationTablePath(String phraseTranslationTablePath) {
        this.phraseTranslationTablePath = phraseTranslationTablePath;
    }

    /**
     * @return the phraseTranslationTableCharset
     */
    public String getPhraseTranslationTableCharset() {
        return phraseTranslationTableCharset;
    }

    /**
     * @param phraseTranslationTableCharset the phraseTranslationTableCharset to set
     */
    public void setPhraseTranslationTableCharset(String phraseTranslationTableCharset) {
        this.phraseTranslationTableCharset = phraseTranslationTableCharset;
    }

    /**
     * @return the textNormalizer
     */
    public TextNormalizer getTextNormalizer() {
        return textNormalizer;
    }

    /**
     * @param textNormalizer the textNormalizer to set
     */
    public void setTextNormalizer(TextNormalizer textNormalizer) {
        this.textNormalizer = textNormalizer;
    }

    public WordSegmentations generateAndPruneSegmentations(String srcWrd)
    {
        wordSegmentationGenerator.clear();
        
        Vector partitions = wordSegmentationGenerator.partition("", srcWrd);

        WordSegmentations wordSegmentations = new WordSegmentations(srcWrd, partitions);

        LinkedHashMap remove = new LinkedHashMap(100, 100);
        
        Iterator sgmItr = wordSegmentations.getSegmentations();

        while (sgmItr.hasNext()) {
            String segmentation = (String) sgmItr.next();

            String[] sengments = segmentation.split(GlobalProperties.getIntlString("_"));
            for (int i = 0; i < sengments.length; i++) {
                if (phraseTranslationTable.getTgtPhrases(UtilityFunctions.getSpacedOutString(sengments[i])) == null || sengments[i].length() > 3) {
                    remove.put(segmentation, new Integer(1));
                    break;
                }
            }
        }

        Iterator itr = remove.keySet().iterator();

        while(itr.hasNext())
        {
            String k = (String) itr.next();

            wordSegmentations.removeSegmentationScores(k);
        }

//        wordSegmentations.sortSegmentations(phraseTranslationTable);

//        PrintStream ps = null;
//
//        try {
//            ps = new PrintStream(srcPath + "-segment-pruned.txt", srcCharset);
//            segmentedCandidates.printSegmentations(ps);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            ps.close();
//        }

        System.gc();
        
        return wordSegmentations;
    }

    public TranslationCandidates generateTransliterationCandidatesDATM(String srcWrd, boolean onlyTheFirst)
    {
        TranslationCandidates translationCandidates = null;

        try {
            translationCandidates = transliteratorMain.transliterateNew(srcWrd);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(GlobalProperties.getIntlString("Final_Transliteration_candidates:"));
        translationCandidates.printTranslationCandidates(System.out, onlyTheFirst);

        return translationCandidates;
    }

    public TranslationCandidates generateTransliterationCandidatesDATM(String srcWrd, boolean prune, boolean onlyTheFirst)
    {
        TranslationCandidates translationCandidates = transliteratorMain.getTransliterationCandidates(srcWrd, true);

        Iterator itr = translationCandidates.getTranslationCandidates();

        while(itr.hasNext())
        {
            String candidate = (String) itr.next();

//            Pattern p = Pattern.compile(srcWrd, Pattern.UNICODE_CASE | Pattern.CANON_EQ);

//            candidate = candidate.replaceAll("(?dum)[\\?]", "\u094D");

            TranslationCandidateScores candidateScores = translationCandidates.getTranslationCandidateScores(candidate);
            candidateScores.setTranslationScore(1.0);
        }

//        translationCandidates = convertEncodingOfCandidates(translationCandidates);

        translationCandidates.removeInvalidWords(textNormalizer);

        if(prune)
            translationCandidates = pruneAndSortTranslationCandidates(translationCandidates);

//        translationCandidates.printTranslationCandidates(System.out);

        return translationCandidates;
    }

    public TranslationCandidates generateTransliterationCandidatesLDATM(String srcWrd, boolean prune, boolean onlyTheFirst)
    {
        srcWrd = preprocessWord(srcWrd);
        
        TransliterationCandidates translationCandidates = (TransliterationCandidates) transliteratorMain.transliterateLDATM(srcWrd, true);
//      candidates.printTranslationCandidates(ps, false);

        System.out.println(GlobalProperties.getIntlString("Final_Transliteration_candidates:"));
        translationCandidates.printTranslationCandidates(System.out, onlyTheFirst);

        return translationCandidates;
    }

    public TranslationCandidates getTransliterationCandidatesSMTAFSM(String srcWrd, boolean prune, boolean onlyTheFirst)
    {
        TransliterationCandidates translationCandidates = new TransliterationCandidates();

        TranslationCandidates smtCandidates = (TranslationCandidates) smtCandidatesMap.get(srcWrd);

        String firstSMTCnd = smtCandidates.getFirstCandidate();

        TranslationCandidates fsmCandidates = transliteratorMain.doFuzzyMatch(smtCandidates);

        Iterator itr = fsmCandidates.getTranslationCandidates();

        boolean noCloseMatch = true;

        while(itr.hasNext())
        {
            String fsmCandidate = (String) itr.next();

            TranslationCandidateScores scores = fsmCandidates.getTranslationCandidateScores(fsmCandidate);

            if(scores.getTranslationScore() < 10.0)
            {
                noCloseMatch = false;
            }

            translationCandidates.addTranslationCandidateScores(fsmCandidate, scores);
        }

        if(noCloseMatch)
            translationCandidates.addTranslationCandidateScores(firstSMTCnd, new TranslationCandidateScores(0.0));

        translationCandidates.normalizeScores(true, true);

        if(prune)
            translationCandidates.pruneAndSortTranslationCandidates(true);

        System.out.println(GlobalProperties.getIntlString("Final_Transliteration_candidates:"));
        translationCandidates.printTranslationCandidates(System.out, onlyTheFirst);

        return translationCandidates;
    }

    public TranslationCandidates getTransliterationCandidatesSMTLFSM(String srcWrd, boolean prune, boolean onlyTheFirst)
    {
        TransliterationCandidates translationCandidates = new TransliterationCandidates();

        DictionaryFST dict = transliteratorMain.getDictionaryFST();

        TranslationCandidates smtCandidates = (TranslationCandidates) smtCandidatesMap.get(srcWrd);

        Iterator itr = smtCandidates.getTranslationCandidates();

        boolean noCloseMatch = true;
        String firstSMTCnd = "";

        int count = 0;

        while(itr.hasNext())
        {
            String smtCnd = (String) itr.next();

            if(count == 0)
                firstSMTCnd = smtCnd;

            count++;

            LinkedHashMap<DictionaryFSTNode,Double> nearestMatches = dict.getNearestWords(smtCnd, 10, false);

            Iterator mIitr = nearestMatches.keySet().iterator();

            while(mIitr.hasNext())
            {
                DictionaryFSTNode fstNode = (DictionaryFSTNode) mIitr.next();

                Double d = nearestMatches.get(fstNode);

                if(d.doubleValue() < 0.20)
                {
                    noCloseMatch = false;
                }

                String cstr = ((DictionaryFSTNode) fstNode).getWordString();

                TranslationCandidateScores scores = new TranslationCandidateScores(d.doubleValue());
                TranslationCandidateScores oldScores = translationCandidates.getTranslationCandidateScores(cstr);

                if(oldScores == null || oldScores.getTranslationScore() > scores.getTranslationScore())
                {
                    translationCandidates.addTranslationCandidateScores(fstNode.getWordString(), scores);
                }
            }
        }

        if(noCloseMatch)
            translationCandidates.addTranslationCandidateScores(firstSMTCnd, new TranslationCandidateScores(0.0));

        itr = smtCandidates.getTranslationCandidates();

        int smtCount = smtCandidates.countTranslationCandidates();
        int fsmCount = translationCandidates.countTranslationCandidates();

        for (int i = 0; itr.hasNext() && i < (smtCount - fsmCount); i++)
        {
            String smtCandidate = (String) itr.next();

            if(smtCandidate.equals(firstSMTCnd) == false)
            {
                if(translationCandidates.getTranslationCandidateScores(smtCandidate) == null)
                    translationCandidates.addTranslationCandidateScores(smtCandidate, new TranslationCandidateScores(0.15 + (i * 0.1)));
            }
        }

        translationCandidates.normalizeScores(true, true);

        if(prune)
            translationCandidates.pruneAndSortTranslationCandidates(true);

        System.out.println(GlobalProperties.getIntlString("Final_Transliteration_candidates:"));
        translationCandidates.printTranslationCandidates(System.out, onlyTheFirst);

        return translationCandidates;
    }

    public TranslationCandidates generateTransliterationCandidatesEDATM(String srcWrd, boolean prune, boolean onlyTheFirst)
    {
        srcWrd = preprocessWord(srcWrd);

        TranslationCandidates translationCandidatesDATM = generateTransliterationCandidatesDATM(srcWrd, false);

        ((TransliterationCandidates) translationCandidatesDATM).scoreTransliterationCandidates(charNGramLMImpl, false, sanchayEncodingConverter, /*phoneticNGram */ phoneticNGram, phoneticFeatureNGramLM, false);
        translationCandidatesDATM.normalizeScores(true, true);

        translationCandidatesDATM.pruneAndSortTranslationCandidates(true);

        System.out.println(GlobalProperties.getIntlString("Candidates_from_DATM:"));
        translationCandidatesDATM.printTranslationCandidates(System.out, onlyTheFirst);

        TranslationCandidates translationCandidates = transliteratorMain.doFuzzyMatch(translationCandidatesDATM);

        translationCandidates.mergeCandidates(translationCandidatesDATM, 10);

        System.out.println(GlobalProperties.getIntlString("Final_Transliteration_candidates:"));
        translationCandidates.printTranslationCandidates(System.out, onlyTheFirst);

        return translationCandidates;
    }

    // Segment = Phrase
    public TranslationCandidates generateTransliterationCandidates(String srcWrd, boolean onlyTheFirst)
    {
        srcWrd = preprocessWord(srcWrd);

        TranslationCandidates translationCandidatesDATM = generateTransliterationCandidatesDATM(srcWrd, false);

        ((TransliterationCandidates) translationCandidatesDATM).scoreTransliterationCandidates(charNGramLMImpl, false, sanchayEncodingConverter, /*phoneticNGram */ phoneticNGram, phoneticFeatureNGramLM, false);
        translationCandidatesDATM.normalizeScores(true, false);

        System.out.println(GlobalProperties.getIntlString("Candidates_from_DATM:"));
        translationCandidatesDATM.printTranslationCandidates(System.out, onlyTheFirst);

        TranslationCandidates translationCandidates = new TransliterationCandidates();

        WordSegmentations wordSegmentations = generateAndPruneSegmentations(srcWrd);

        System.out.println(GlobalProperties.getIntlString("Segmentation:"));

        wordSegmentations.printSegmentations(System.out);

        Iterator sgmItr = wordSegmentations.getSegmentations();

        int pruneSize = 1000;
        double check = 0.01;

        while (sgmItr.hasNext())
        {
            System.gc();
            
            String segmentation = (String) sgmItr.next();
            String[] segments = segmentation.split(" ");

            LinkedHashMap prevCandidates = new LinkedHashMap(0, 100);
            LinkedHashMap candidates = new LinkedHashMap(0, 100);
            
            for (int i = 0; i < segments.length; i++)
            {
                Iterator tgtItr = phraseTranslationTable.getTgtPhrases(UtilityFunctions.getSpacedOutString(segments[i]));

                while(tgtItr.hasNext())
                {
                    String tgtPhrase = (String) tgtItr.next();
                    PhraseTranslationScores phraseTranslationScores = phraseTranslationTable.getPhraseTranslationScores(UtilityFunctions.getSpacedOutString(segments[i]), tgtPhrase);

                    if(i == 0)
                    {
                        TranslationCandidateScores candidateScores = new TranslationCandidateScores();

                        candidateScores.setTranslationScore(phraseTranslationScores.getTranlsationScore() * phraseTranslationScores.getRevTranlsationScore());

                        candidates.put(tgtPhrase, candidateScores);
                    }
                    else
                    {
                        // Partial candidates
                        Iterator cndItr = prevCandidates.keySet().iterator();

                        while(cndItr.hasNext())
                        {
                            String candidate = (String) cndItr.next();
                            TranslationCandidateScores prevCandidateScores = (TranslationCandidateScores) prevCandidates.get(candidate);

                            String prevTgtPhrase = candidate;

                            TranslationCandidateScores candidateScores = new TranslationCandidateScores();

                            double d = prevCandidateScores.getTranslationScore()
                                    * phraseTranslationScores.getTranlsationScore()
                                    * phraseTranslationScores.getRevTranlsationScore();

                            candidateScores.setTranslationScore(d);

                            String convCandidatePart = prevTgtPhrase + " " + tgtPhrase;
                            convCandidatePart = convCandidatePart.replaceAll(" ", "");
                            convCandidatePart = sanchayEncodingConverter.convert(convCandidatePart);

                            boolean isPossiblyValid = textNormalizer.isPossiblyValidWord(convCandidatePart);
                            boolean isPossiblyValidStart = textNormalizer.isPossiblyValidWordStart(convCandidatePart);
                            boolean hasNonZeroLMProb = true;

                            double lmProb = charNGramLMImpl.getSentenceProb(UtilityFunctions.getSpacedOutString(convCandidatePart));

                            if(lmProb <= 1.0E-4 * ((double) (i + 1)) * ((double) (i + 1)) )
                                hasNonZeroLMProb = false;

                            if(i == segments.length - 1)
                            {
                                if(isPossiblyValid && hasNonZeroLMProb)
                                    translationCandidates.addTranslationCandidateScores(prevTgtPhrase + " " + tgtPhrase, candidateScores);
//                                else
//                                    System.out.println("Word not added:" + convCandidatePart);
                            }
                            else
                            {
                                d *= (i + 1) * (i + 1);

                                if(d > check)
                                {
                                    if(i > 1)
                                    {
                                        if(isPossiblyValidStart && hasNonZeroLMProb)
                                            candidates.put(prevTgtPhrase + " " + tgtPhrase, candidateScores);
//                                        else
//                                            System.out.println("Part not added:" + convCandidatePart);
                                    }
                                    else
                                        candidates.put(prevTgtPhrase + " " + tgtPhrase, candidateScores);
                                }
//                                else
//                                    System.out.println("Part not added:" + convCandidatePart);
                            }
                        }
                    }
                }

                prevCandidates.clear();
//                System.gc();

                prevCandidates = candidates;
                candidates = new LinkedHashMap(0, 100);
            }
        }

        translationCandidates = convertEncodingOfCandidates(translationCandidates);
        ((TransliterationCandidates) translationCandidates).scoreTransliterationCandidates(charNGramLMImpl, false, sanchayEncodingConverter, /*phoneticNGram*/ phoneticNGram, phoneticFeatureNGramLM, false);
        translationCandidates.normalizeScores(true, false);

//        System.out.println("MLTransliteration candidates:");
//        translationCandidates.printTranslationCandidates(System.out);

//        translationCandidates translationCandidatesDATM = generateTransliterationCandidatesDATM(srcWrd, false);
        translationCandidates.addTranslationCandidates(translationCandidatesDATM, false);
        translationCandidates.normalizeScores(true, true);

//        // For negative scores
//        translationCandidates = translationCandidates.getCandidatesInReverseOrder();

        translationCandidates = pruneAndSortTranslationCandidates(translationCandidates);

        System.out.println(GlobalProperties.getIntlString("Final_Transliteration_candidates:"));
        translationCandidates.printTranslationCandidates(System.out, onlyTheFirst);

//        if(phoneticNGram)
//        {
//            translationCandidates.scoreTransliterationCandidates(charNGramLMImpl, false, sanchayEncodingConverter, /*phoneticNGram*/ true, phoneticFeatureNGramLM, true);
//            translationCandidates.normalizeScores(true, true);
//            translationCandidates.pruneAndSortTranslationCandidates(false);
//
//            System.out.println("Final Transliteration candidates after PNG:");
//            translationCandidates.printTranslationCandidates(System.out);
//        }

        return translationCandidates;
    }
    
//    public translationCandidates generateTransliterationCandidates(String srcWrd)
//    {
//        System.gc();
//
//        translationCandidates translationCandidates = new TransliterationCandidates();
//
//        WordSegmentations wordSegmentations = generateAndPruneSegmentations(srcWrd);
//
//        System.out.println("Segmentation:");
//
//        wordSegmentations.printSegmentations(System.out);
//
//        Hashtable localTransliterationCandidates = new Hashtable(0, 100);
//        Hashtable localTransliterationCandidatesScores = new Hashtable(0, 100);
//
//        Iterator sgmItr = wordSegmentations.getSegmentations();
//
//        int pruneSize = 100;
//        double check = 0.0001;
//
//        while (sgmItr.hasNext())
//        {
//            String segmentation = (String) sgmItr.next();
//            String[] segments = segmentation.split(" ");
//
//            String srcSeq = "";
//            String prevSrcSeq = "";
//
//            TranslationCandidateScores srcSeqScores = new TranslationCandidateScores();
//            TranslationCandidateScores prevSrcSeqScores = new TranslationCandidateScores();
//
//            for (int i = 0; i < segments.length; i++)
//            {
//                if(i < segments.length - 1)
//                    srcSeq += segments[i] + " ";
//                else
//                {
//                    srcSeq += segments[i];
////                        System.out.println(srcSeq);
////                        translationCandidates.sortTranslationCandidates(srcSeq, phraseTranslationTable);
//                }
//
//                Hashtable tgtSeqs  = (Hashtable) localTransliterationCandidates.get(srcSeq);
//                Hashtable tgtSeqsScores  = (Hashtable) localTransliterationCandidatesScores.get(srcSeq);
//
//                if(tgtSeqs == null)
//                {
//                    tgtSeqs = new Hashtable(0, 100);
//                    localTransliterationCandidates.put(srcSeq, tgtSeqs);
//
//                    tgtSeqsScores = new Hashtable(0, 100);
//                    localTransliterationCandidatesScores.put(srcSeq, tgtSeqsScores);
//                }
//
//                Iterator tgtItr = phraseTranslationTable.getTgtPhrases(UtilityFunctions.getSpaceOutString(segments[i]));
//
//                while (tgtItr != null && tgtItr.hasNext())
//                {
//                    String tgtPhrase = (String) tgtItr.next();
//                    String tgtPhraseConverted = sanchayEncodingConverter.convert(tgtPhrase.replaceAll(" ", ""));
//
//                    PhraseTranslationScores phraseTranslationScores = phraseTranslationTable.getPhraseTranslationScores(UtilityFunctions.getSpaceOutString(segments[i]), tgtPhrase);
//
////                        if(phraseTranslationScores.getTranlsationScore() < 0.001)
////                            continue;
//
//                    Hashtable prevTgtSeqs = (Hashtable) localTransliterationCandidates.get(prevSrcSeq);
//                    Hashtable prevTgtSeqsScores = (Hashtable) localTransliterationCandidatesScores.get(prevSrcSeq);
//
//                    if(prevTgtSeqs == null)
//                    {
//                        prevTgtSeqs = new Hashtable(0, 100);
//                        localTransliterationCandidates.put(prevSrcSeq, prevTgtSeqs);
//
//                        prevTgtSeqsScores = new Hashtable(0, 100);
//                        localTransliterationCandidates.put(prevSrcSeq, prevTgtSeqsScores);
//                    }
//
//                    Enumeration prevTgtEnm = prevTgtSeqs.keys();
//
//                    if(prevTgtSeqs.size() > 0)
//                    {
//                        while(prevTgtEnm.hasMoreElements())
//                        {
//                            String prevTgtSeq = (String) prevTgtEnm.nextElement();
//
//                            prevSrcSeqScores = (TranslationCandidateScores) prevTgtSeqsScores.get(prevTgtSeq);
//                            srcSeqScores = new TranslationCandidateScores();
//
//                            tgtSeqs.put(prevTgtSeq + " " + tgtPhrase, new Integer(1));
//
//                            double d = prevSrcSeqScores.getTranslationScore()
//                                    * phraseTranslationScores.getTranlsationScore()
//                                    * phraseTranslationScores.getRevTranlsationScore();
//
////                            if(d < check)
////                            {
////                                prevTgtSeqs.remove(prevTgtSeq);
////                                prevTgtSeqsScores.remove(prevSrcSeq);
////                                continue;
////                            }
//
//                            srcSeqScores.setTranslationScore(d);
////                                    * charNGramLMImpl.getNGram(tgtPhraseConverted, tgtPhraseConverted.length()).getProb());
//                            tgtSeqsScores.put(prevTgtSeq + " " + tgtPhrase, srcSeqScores);
//
//                            if(i == segments.length - 1)
//                            {
//                                translationCandidates.addTranslationCandidateScores(prevTgtSeq + " " + tgtPhrase, srcSeqScores);
////                                    System.out.println("\t" + prevTgtSeq + " " + tgtPhrase);
////
////                                if(translationCandidates.countTranslationCandidates() > pruneSize)
////                                {
////                                    translationCandidates.sortTranslationCandidates(charNGramLMImpl, true, sanchayEncodingConverter);
////                                    System.gc();
////                                }
//                            }
//                        }
//                    }
//                    else
//                    {
//                        tgtSeqs.put(tgtPhrase, new Integer(1));
//
//                        srcSeqScores = new TranslationCandidateScores();
//
//                        srcSeqScores.setTranslationScore(phraseTranslationScores.getTranlsationScore()
//                                * phraseTranslationScores.getRevTranlsationScore());
////                                * charNGramLMImpl.getNGram(tgtPhraseConverted, tgtPhraseConverted.length()).getProb());
//                        tgtSeqsScores.put(tgtPhrase, srcSeqScores);
//
//                        if(i == segments.length - 1)
//                        {
//                            translationCandidates.addTranslationCandidateScores(tgtPhrase, srcSeqScores);
////                                System.out.println("\t" + tgtPhrase);
//                        }
//                    }
//
////                    System.gc();
//                }
//
////                tgtSeqs.remove(prevSrcSeq);
////                tgtSeqsScores.remove(prevSrcSeq);
//
//                localTransliterationCandidates.remove(prevSrcSeq);
//                localTransliterationCandidatesScores.remove(prevSrcSeq);
//
//                prevSrcSeq = srcSeq;
//                prevSrcSeqScores = srcSeqScores;
//
////                System.gc();
//            }
//        }
//
//        System.out.println("Transliteration candidates:");
//
//        translationCandidates = convertEncodingOfCandidates(translationCandidates);
//        translationCandidates = pruneAndSortTranslationCandidates(translationCandidates);
//
//        translationCandidates.printTranslationCandidates(System.out);
//
//        return translationCandidates;
//    }

    public TranslationCandidates convertEncodingOfCandidates(TranslationCandidates translationCandidates)
    {
        TranslationCandidates translationCandidatesConverted = new TransliterationCandidates();
        
        Iterator cndItr = translationCandidates.getTranslationCandidates();

        while(cndItr.hasNext())
        {
            String candidate = (String) cndItr.next();

            TranslationCandidateScores scores = translationCandidates.getTranslationCandidateScores(candidate);

            String convertedCandidate = candidate.replaceAll(" ", "");
            convertedCandidate = sanchayEncodingConverter.convert(convertedCandidate);

            if(textNormalizer.isPossiblyValidWord(convertedCandidate) == false)
                continue;

            translationCandidatesConverted.addTranslationCandidateScores(convertedCandidate, scores);
        }

        translationCandidates = translationCandidatesConverted;

//        PrintStream ps = null;
//
//        try {
//            ps = new PrintStream(srcPath + "-transliteration-candidates-utf8.txt", tgtCharset);
//            transliterationCandidates.printTransliterationCandidates(ps);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            ps.close();
//        }

        return translationCandidatesConverted;
    }

    public TranslationCandidates pruneAndSortTranslationCandidates(TranslationCandidates translationCandidates)
    {
        Vector sorted = translationCandidates.pruneAndSortTranslationCandidates(true);

//        PrintStream ps = null;
//
//        try {
//            ps = new PrintStream(srcPath + "-transliteration-candidates-sorted.txt", tgtCharset);
//            transliterationCandidates.printTransliterationCandidates(ps);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            ps.close();
//        }

        return translationCandidates;
    }

    protected String preprocessWord(String srcWrd)
    {
        srcWrd = srcWrd.replaceAll("'", "a");
        srcWrd = srcWrd.replaceAll("-", "a");

        return srcWrd;
    }

    protected String postprocessTransliterationCandidate(String candidate)
    {
        candidate = textNormalizer.normalizeWord(candidate);

        return candidate;
    }

    // Only for the batch mode
    public void transliterate(boolean onlyTheFirst)
    {
        int scount = srcWords.countTokens();

        PrintStream ps = null;

//        if(phoneticNGram)
//            tgtPath += "-png.txt";

        try {
            ps = new PrintStream(tgtPath, tgtCharset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }

        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransliterationCorpus CorpusID = \"NEWS2009-Testing-EnHi-1000\" SourceLang = \"English\" TargetLang = \"Hindi\" CorpusType = \"Testing\" CorpusSize = \"1000\" CorpusFormat = \"UTF8\">");
        
        for (int i = 1; i <= scount; i++)
        {
            String srcWrd = srcWords.getToken(i - 1);

            if(onlyTheFirst)
            {
                ps.print(srcWrd + "\t");
            }
            else
            {
                ps.print("<Name ID=\"" + i +"\"><SourceName>" + srcWrd + "</SourceName>");
            }

            TranslationCandidates translationCandidates = generateTransliterationCandidates(srcWrd, onlyTheFirst);

            Iterator itr = translationCandidates.getTranslationCandidates();

            if(onlyTheFirst)
            {
                if(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println(translationCandidate);
                }
                else
                    ps.println("");
            }
            else
            {
                int j = 1;

                while(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println("<TargetName ID=\"" + j + "\">" + translationCandidate + "</TargetName>");

                    j++;
                }

                for (int k = j; k <= 10; k++) {
                    ps.println("<TargetName ID=\"" + k + "\">" + GlobalProperties.getIntlString("Don't_know") + "</TargetName>");
                }

                ps.println(GlobalProperties.getIntlString("</Name>"));
            }
        }

        if(onlyTheFirst)
        {
            ps.println("</TransliterationCorpus>");

            ps.close();
        }
    }

    // Only for the batch mode
    public void transliterateDATM(boolean onlyTheFirst)
    {
        int scount = srcWords.countTokens();

        PrintStream ps = null;
        PrintStream psWrdOrigin = null;

//        if(phoneticNGram)
//            tgtPath += "-png.txt";

        try {
            ps = new PrintStream(tgtPath, tgtCharset);
            psWrdOrigin = new PrintStream(wrdOriginPath, tgtCharset);

            if(onlyTheFirst == false)
                ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransliterationCorpus CorpusID = \"NEWS2009-Testing-EnHi-1000\" SourceLang = \"English\" TargetLang = \"Hindi\" CorpusType = \"Testing\" CorpusSize = \"1000\" CorpusFormat = \"UTF8\">");

            for (int i = 1; i <= scount; i++)
            {
                String srcWrd = srcWords.getToken(i - 1);

                if(onlyTheFirst)
                {
                    ps.print(srcWrd + "\t");
                }
                else
                {
                    ps.print("<Name ID=\"" + i +"\"><SourceName>" + srcWrd + "</SourceName>");
                }

                TranslationCandidates translationCandidates = transliteratorMain.transliterateNew(srcWrd, null, psWrdOrigin);

                Iterator itr = translationCandidates.getTranslationCandidates();

                if(onlyTheFirst)
                {
                    if(itr.hasNext())
                    {
                        String translationCandidate = (String) itr.next();
                        ps.println(translationCandidate);
                    }
                    else
                        ps.println("");
                }
                else
                {
                    int j = 1;

                    while(itr.hasNext())
                    {
                        String translationCandidate = (String) itr.next();
                        ps.println("<TargetName ID=\"" + j + "\">" + translationCandidate + "</TargetName>");

                        j++;
                    }

                    for (int k = j; k <= 10; k++) {
                        ps.println("<TargetName ID=\"" + k + "\">" + GlobalProperties.getIntlString("Don't_know") + "</TargetName>");
                    }

                    ps.println("</Name>");
                }
            }

            if(onlyTheFirst == false)
            {
                ps.println("</TransliterationCorpus>");

                ps.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ps.close();
        }
    }

    public void transliterateSMTFSM(boolean onlyTheFirst, boolean aksharBased)
    {
        int scount = srcWords.countTokens();

        PrintStream ps = null;

//        if(phoneticNGram)
//            tgtPath += "-png.txt";

        try {
            ps = new PrintStream(tgtPath, tgtCharset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(onlyTheFirst == false)
            ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransliterationCorpus CorpusID = \"NEWS2009-Testing-EnHi-1000\" SourceLang = \"English\" TargetLang = \"Hindi\" CorpusType = \"Testing\" CorpusSize = \"1000\" CorpusFormat = \"UTF8\">");

        for (int i = 1; i <= scount; i++)
        {
            String srcWrd = srcWords.getToken(i - 1);

             if(onlyTheFirst)
            {
                ps.print(srcWrd + "\t");
            }
            else
            {
                ps.print("<Name ID=\"" + i +"\"><SourceName>" + srcWrd + "</SourceName>");
            }

            TranslationCandidates translationCandidates = null;

            if(aksharBased)
            {
                translationCandidates = getTransliterationCandidatesSMTAFSM(srcWrd, true, onlyTheFirst);
            }
            else
            {
                translationCandidates = getTransliterationCandidatesSMTLFSM(srcWrd, true, onlyTheFirst);
            }

            Iterator itr = translationCandidates.getTranslationCandidates();

            if(onlyTheFirst)
            {
                if(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println(translationCandidate);
                }
                else
                    ps.println("");
            }
            else
            {
                int j = 1;

                while(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println("<TargetName ID=\"" + j + "\">" + translationCandidate + "</TargetName>");

                    j++;
                }

                for (int k = j; k <= 10; k++) {
                    ps.println("<TargetName ID=\"" + k + "\">" + GlobalProperties.getIntlString("Don't_know") + "</TargetName>");
                }

                ps.println("</Name>");
            }
        }

        if(onlyTheFirst == false)
        {
            ps.println("</TransliterationCorpus>");

            ps.close();
        }
    }

    public void transliterateLDATM(boolean onlyTheFirst)
    {
        int scount = srcWords.countTokens();

        PrintStream ps = null;

//        if(phoneticNGram)
//            tgtPath += "-png.txt";

        try {
            ps = new PrintStream(tgtPath, tgtCharset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(onlyTheFirst == false)
            ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransliterationCorpus CorpusID = \"NEWS2009-Testing-EnHi-1000\" SourceLang = \"English\" TargetLang = \"Hindi\" CorpusType = \"Testing\" CorpusSize = \"1000\" CorpusFormat = \"UTF8\">");

        for (int i = 1; i <= scount; i++)
        {
            String srcWrd = srcWords.getToken(i - 1);

             if(onlyTheFirst)
            {
                ps.print(srcWrd + "\t");
            }
            else
            {
                ps.print("<Name ID=\"" + i +"\"><SourceName>" + srcWrd + "</SourceName>");
            }

            TranslationCandidates translationCandidates = generateTransliterationCandidatesLDATM(srcWrd, false, onlyTheFirst);

            Iterator itr = translationCandidates.getTranslationCandidates();

            if(onlyTheFirst)
            {
                if(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println(translationCandidate);
                }
                else
                    ps.println("");
            }
            else
            {
                int j = 1;

                while(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println("<TargetName ID=\"" + j + "\">" + translationCandidate + "</TargetName>");

                    j++;
                }

                for (int k = j; k <= 10; k++) {
                    ps.println("<TargetName ID=\"" + k + "\">" + GlobalProperties.getIntlString("Don't_know") + "</TargetName>");
                }

                ps.println("</Name>");
            }
        }

        if(onlyTheFirst == false)
        {
            ps.println("</TransliterationCorpus>");

            ps.close();
        }
    }

    public void transliterateEDATM(boolean onlyTheFirst)
    {
        int scount = srcWords.countTokens();

        PrintStream ps = null;

//        if(phoneticNGram)
//            tgtPath += "-png.txt";

        try {
            ps = new PrintStream(tgtPath, tgtCharset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(onlyTheFirst == false)
            ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransliterationCorpus CorpusID = \"NEWS2009-Testing-EnHi-1000\" SourceLang = \"English\" TargetLang = \"Hindi\" CorpusType = \"Testing\" CorpusSize = \"1000\" CorpusFormat = \"UTF8\">");

        for (int i = 1; i <= scount; i++)
        {
            String srcWrd = srcWords.getToken(i - 1);

             if(onlyTheFirst)
            {
                ps.print(srcWrd + "\t");
            }
            else
            {
                ps.print("<Name ID=\"" + i +"\"><SourceName>" + srcWrd + GlobalProperties.getIntlString("</SourceName>"));
            }

            TranslationCandidates translationCandidates = generateTransliterationCandidatesEDATM(srcWrd, false, onlyTheFirst);

            Iterator itr = translationCandidates.getTranslationCandidates();

            if(onlyTheFirst)
            {
                if(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println(translationCandidate);
                }
                else
                    ps.println("");
            }
            else
            {
                int j = 1;

                while(itr.hasNext())
                {
                    String translationCandidate = (String) itr.next();
                    ps.println("<TargetName ID=\"" + j + "\">" + translationCandidate + "</TargetName>");

                    j++;
                }

                for (int k = j; k <= 10; k++) {
                    ps.println("<TargetName ID=\"" + k + "\">" + GlobalProperties.getIntlString("Don't_know") + "</TargetName>");
                }

                ps.println("</Name>");
            }
        }

        if(onlyTheFirst == false)
        {
            ps.println("</TransliterationCorpus>");

            ps.close();
        }
    }

    private static void usage()
    {
        System.err.println(GlobalProperties.getIntlString("Usage:_java_MLTransliterator_[-m_method]_[-i_input_type]_[-f_only_the_first]_<input_file_or_string>_[<output_file_or_string>]"));
        System.err.println( GlobalProperties.getIntlString("English_to_Hindi_transliteration_tool_that_can_be_used_with_the_following_options:"));
        System.err.println(GlobalProperties.getIntlString("\t-m_Specifies_the_method_of_transliteration_(d_for_DATM,_l_for_LDATM_e_for_EDATM,_r_for_SMT_+_AFSM_and_s_for_SMT_+_LFSM))"));
        System.err.println(GlobalProperties.getIntlString("\t-i_Specifies_the_type_of_input_(s_for_string,_f_for_file)"));
        System.err.println(GlobalProperties.getIntlString("\t-f_For_only_one_candidate_as_the_output"));
        System.err.println(GlobalProperties.getIntlString("Example:"));
        System.err.println("\t java MLTransliterator -m d -i f -f input.txt output.txt");
        System.err.println(GlobalProperties.getIntlString("Note_that_if_you_don't_give_-f_as_the_option,_then_the_output_file_will_be_an_XML_file_in_the_ACL-09_Shared_Task_format."));
        System.err.println(GlobalProperties.getIntlString("These_options_will_change_in_the_future."));
        System.exit(1);
    }

    public static void main(String args[])
    {
//        System.err.println("Total options: " + args.length);
        
//        if (args.length == 0){
//          usage();
//        }

        int ai=0;

        String method = "s";
        String inputType = "f";
        boolean onlyTheFirst = false;
        
//        while (args[ai].startsWith("-")) {
//
////            System.err.println("Option "+ ai + ": " + args[ai]);
//
//            if (args[ai].equals("-m"))
//            {
//              ai++;
//              if (ai < args.length) {
//                method = args[ai++];
//              }
//              else {
//                usage();
//              }
//            }
//            else if (args[ai].equals("-i"))
//            {
//              ai++;
//              if (ai < args.length) {
//                inputType = args[ai++];
//              }
//              else {
//                usage();
//              }
//            }
//            else if (args[ai].equals("-f"))
//            {
//                ai++;
//
//                if (ai < args.length)
//                   onlyTheFirst = true;
//            }
//            else {
//              System.err.println("Unknown option "+args[ai]);
//              usage();
//            }
//        }

        //        mlTransliterator.transliterate();


        if(inputType.equalsIgnoreCase("s"))
        {
            MLTransliterator mlTransliterator = new MLTransliterator();

            if(method.equalsIgnoreCase("d"))
            {
                mlTransliterator.setMethod(DATM);
                mlTransliterator.init();
                mlTransliterator.generateTransliterationCandidatesDATM(args[ai++], onlyTheFirst);
            }
            else if(method.equalsIgnoreCase("l"))
            {
                mlTransliterator.setMethod(LDATM);
                mlTransliterator.init();
                mlTransliterator.generateTransliterationCandidatesLDATM(args[ai++], true, onlyTheFirst);
            }
            else if(method.equalsIgnoreCase("e"))
            {
                mlTransliterator.setMethod(EDATM);
                mlTransliterator.init();
                mlTransliterator.generateTransliterationCandidatesEDATM(args[ai++], true, onlyTheFirst);
            }
            else if(method.equalsIgnoreCase("r"))
            {
                mlTransliterator.setMethod(SMTFSM);
                mlTransliterator.init();
                mlTransliterator.getTransliterationCandidatesSMTAFSM(args[ai++], true, onlyTheFirst);
            }
            else if(method.equalsIgnoreCase("s"))
            {
                mlTransliterator.setMethod(SMTFSM);
                mlTransliterator.init();
                mlTransliterator.getTransliterationCandidatesSMTLFSM(args[ai++], true, onlyTheFirst);
            }
            else
                usage();
        }
//        else if(inputType.equalsIgnoreCase("f") && ai + 1 < args.length)
        else if(inputType.equalsIgnoreCase("f"))
        {
//            MLTransliterator mlTransliterator = new MLTransliterator(args[ai++], args[ai++]);
            MLTransliterator mlTransliterator = new MLTransliterator("/home/anil/tmp/feature_based_code/eng-neList.txt", "/home/anil/tmp/feature_based_code/eng-10-best-neList-transliterated-marathi.xml");

            if(method.equalsIgnoreCase("d"))
            {
                mlTransliterator.setMethod(DATM);
                mlTransliterator.init();
                mlTransliterator.transliterateDATM(onlyTheFirst);
            }
            else if(method.equalsIgnoreCase("l"))
            {
                mlTransliterator.setMethod(LDATM);
                mlTransliterator.init();
                mlTransliterator.transliterateLDATM(onlyTheFirst);
            }
            else if(method.equalsIgnoreCase("e"))
            {
                mlTransliterator.setMethod(EDATM);
                mlTransliterator.init();
                mlTransliterator.transliterateEDATM(onlyTheFirst);
            }
            else if(method.equalsIgnoreCase("r"))
            {
                mlTransliterator.setMethod(SMTFSM);
                mlTransliterator.init();
                mlTransliterator.transliterateSMTFSM(onlyTheFirst, true);
            }
            else if(method.equalsIgnoreCase("s"))
            {
                mlTransliterator.setMethod(SMTFSM);
                mlTransliterator.init();
                mlTransliterator.transliterateSMTFSM(onlyTheFirst, false);
            }
            else
                usage();
        }
        else
          usage();

//        mlTransliterator.transliterateEDATM();
//        mlTransliterator.generateTransliterationCandidates("Kandarpa");
//        mlTransliterator.generateTransliterationCandidates("Kaamalwala");
//        mlTransliterator.generateTransliterationCandidates("Madhulika");
//        mlTransliterator.generateTransliterationCandidates("Bharat");
//        mlTransliterator.generateTransliterationCandidates("Chandrama");
//        mlTransliterator.generateTransliterationCandidates("Raja");
//        mlTransliterator.generateTransliterationCandidates("Nisha");
//        mlTransliterator.generateTransliterationCandidatesEDATM("Singh", false);
//
//        UtilityFunctions.printVector(pruned, System.out);
    }
}
