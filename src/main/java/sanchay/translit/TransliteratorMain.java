package sanchay.translit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.langenc.LangEncIdentifier;
import sanchay.ili.AlignOutput;
import sanchay.ili.AlignmentWordFreq;
import sanchay.ili.PhonemeMapping;
import sanchay.ili.RomanAnalyser;
import sanchay.text.adhoc.AksharSimilarity;

import com.sun.speech.freetts.lexicon.LetterToSound;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.GlobalProperties;
import sanchay.mlearning.mt.TranslationCandidateScores;
import sanchay.mlearning.mt.TranslationCandidates;
import sanchay.mlearning.mt.TransliterationCandidates;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.text.DictionaryFSTNode;
import sanchay.text.TextNormalizer;
import sanchay.text.spell.DictionaryFSTExt;

/**
 * Main Class for Transliteration. 
 * 
 * The input word is transliterated based on the following algorithm: 
 * 
 * 1. Based on the origin of the word,
 * 
 * 2. If its of Foreign origin & If the input word is present in the CMU Speech dictionary, 
 * 			its transliterated using the phoneme-based mapping approach
 * 
 * 3. If the input word is not present in the CMU Speech dictionary, use the CMU LTS dictionary for
 * 			 phoneme-based approach of transliteration
 * 
 * 4. If the word is of Indian origin, then its transliterated using mapping based on grammar rules
 *     
 * 4. Final list of possible transliteration candidates is returned in the form on an arraylist
 * 
 * @author Sethu
 *
 */
@Deprecated
public class TransliteratorMain {

    String charset = GlobalProperties.getIntlString("UTF-8");

    // Language encoding identification inputs
    final int scoreType = LangEncIdentifier.LOG_JOINT_PROB;
    final boolean useStoredModels = true;
    final boolean useWrdModels = false;
    final boolean inMemory = true;
    final int numAmbiguous = 5;
    final int charNGrams = 3000;
    final int wordNGrams = 500;
    final double wordNGramsWeight = 1.0;
    final int charNGramOrder = 4;
    final int wordNGramOrder = 3;
    public final static String DATA_FOLDER_PATH = GlobalProperties.getHomeDirectory() + "/";
    // Language encoding identification inputs
    public final static String LANG_ENC_TRAINPATH = DATA_FOLDER_PATH + "data/transliteration/Lang_enc_id/trainInfo";
    // Indian Origin words Mapping Grammar files' paths
    public final static String IL_GRAMMAR_TYPE_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/GrammarTypeRoman";
    public final static String IL_CONSONANT_MAP_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/ConsantMapRomanDevProperNames";
    public final static String IL_MATRA_MAP_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/MatraMapRomanDev";
    public final static String IL_VOWEL_MAP_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/VowelMapRomanDev";
    public final static String IL_AMBI_GRAMMAR_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/AmbiGrammarRoman";
    public final static String IL_INPUT_NAMES_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/IndNames.txt";
    public final static String IL_OUTPUT_MAPPED_NAMES_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/HindiNames.MAP";

    // Foreign Origin words Mapping files' paths
    public final static String ENG_CMU_GRAMMAR_TYPE_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Type";
    public final static String ENG_CMU_GRAMMAR_CONS_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Cons1";
    public final static String ENG_CMU_GRAMMAR_MATRA_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Matra";
    public final static String ENG_CMU_GRAMMAR_VOWEL_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Vowel";
    public final static String ENG_INPUT_NAMES_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/Transfer/TelCMU";
    public final static String ENG_OUTPUT_NAMES_MAPPED_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/Transfer/TelCMU.Mapped";

    // Dictionary(Wordlist) path
    //public final static String DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/wordList";
//    public final static String DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/WordLists/Hindi_wordList.txt";
//    public final static String DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/word-translation/word-lists/hindi-wordlist-translit.txt";
//    public final static String DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/word-translation/word-lists/hindi-wordlist-fire.txt";
//    public final static String DICTIONARY_FILEPATH = "/home/anil/Desktop/WordLists_pruned/Hindi_wordlist.txt";
//    public final static String DICTIONARY_FILEPATH = "data/word-translation/word-lists/hindi-wordlist-translit-new.txt";
//    public final static String DICTIONARY_FILEPATH = "data/word-translation/word-lists/hindi-wordlist-translit-news.txt";
    public final static String DICTIONARY_FILEPATH = "/home/anil/word-lists/marathi/complete/marathi-wordlist-sorted.txt";
    public final static String DICTIONARY_ENCODING = GlobalProperties.getIntlString("UTF-8");


    public final static String SPELL_CHECKER_DICT_FORWARD_PATH = DATA_FOLDER_PATH + "data/transliteration/spell-checker-dict.forward";
    public final static String SPELL_CHECKER_DICT_REVERSE_PATH = DATA_FOLDER_PATH + "data/transliteration/spell-checker-dict.reverse";
    public final static String SIM_LIST_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/ILIEngSimList.sim";

    // Fuzzy Mapped output filepath
    public final static String FUZZY_IND_MAPFILE_PATH_PREFIX = DATA_FOLDER_PATH + "data/transliteration/HindiNames.FUZZY";
    public final static String FUZZY_ENG_MAPFILE_PATH_PREFIX = DATA_FOLDER_PATH + "data/transliteration/Transfer/TelCMU.FUZZY";

    // Ranking related files paths
    public final static String RANKING_IND_FILE_PATH = DATA_FOLDER_PATH + "data/transliteration/HindiNames.FUZZY_0";
    public final static String RANKING_ENG_FILE_PATH = DATA_FOLDER_PATH + "data/transliteration/Transfer/TelCMU.FUZZY_0";
    public final static String RANKED_IND_OUTPUT_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/HindiNames.RANKS";
    public final static String RANKED_ENG_OUTPUT_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/Transfer/TelCMU.RANKS";

    // CMU Speech dictionary filepath
    public final static String CMU_SPEECH_DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/cmudict.0.7a";
    LangEncIdentifier langEncIdentifier = null;
    RomanAnalyser IndAnl = null;
//    DictionaryFST dict = null;
    protected DictionaryFSTExt dictionaryFST = null;
    AlignmentWordFreq tree = null;
    AksharSimilarity aks = null;
    PhonemeMapping phonemeMapping = null;
    LetterToSound letterToSound = null;
    TextNormalizer textNormalizer;

    int pruneTopN = 10;

//    protected InstantiatePhonemeGraph instantiatePhonemeGraphIndian;
//    protected InstantiatePhonemeGraph instantiatePhonemeGraphForeign;
//    protected NGramLM phoneticFeatureNGramLMIndian;
//    protected NGramLM phoneticFeatureNGramLMForeign;
//
//    protected InstantiatePhonemeGraph instantiatePhonemeGraphTest;
//    protected NGramLM phoneticFeatureNGramLMTest;

    /**
     * Default Constructor
     * @throws FileNotFoundException
     * @throws IOException
     */
    public TransliteratorMain() throws FileNotFoundException, IOException {

        //Instantiate & train the Language Encoding Identifier
        langEncIdentifier = new LangEncIdentifier(LANG_ENC_TRAINPATH, LangEncIdentifier.FREQ_IDENTIFIER, scoreType,
                charNGrams, useStoredModels, useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight, charNGramOrder,
                wordNGramOrder);
        langEncIdentifier.train();

//		instantiatePhonemeGraphIndian = new InstantiatePhonemeGraph("UTF-8", "UTF-8", false);
//		instantiatePhonemeGraphForeign = new InstantiatePhonemeGraph("UTF-8", "UTF-8", false);
//
//        phoneticFeatureNGramLMIndian = instantiatePhonemeGraphIndian.createPhonemeUnigrams("data/translit/Lang_enc_id/Indian");
//        phoneticFeatureNGramLMForeign = instantiatePhonemeGraphForeign.createPhonemeUnigrams("data/translit/Lang_enc_id/Foreign");

        // Make a Indian Word Analyser Read Grammar Files
        IndAnl = new RomanAnalyser();
        IndAnl.readType(IL_GRAMMAR_TYPE_ROMAN_PATH);
        IndAnl.readCons(IL_CONSONANT_MAP_ROMAN_PATH);
        IndAnl.readMatra(IL_MATRA_MAP_ROMAN_PATH);
        IndAnl.readVowel(IL_VOWEL_MAP_ROMAN_PATH);
        IndAnl.readAmbi(IL_AMBI_GRAMMAR_ROMAN_PATH);

        // Instantiate the PhoneMapping class(for foreign-origin words)
        phonemeMapping = new PhonemeMapping();
        phonemeMapping.readType(ENG_CMU_GRAMMAR_TYPE_PATH);
        phonemeMapping.readCons(ENG_CMU_GRAMMAR_CONS_PATH);
        phonemeMapping.readMatra(ENG_CMU_GRAMMAR_MATRA_PATH);
        phonemeMapping.readVowel(ENG_CMU_GRAMMAR_VOWEL_PATH);

        //To load the CMU Speech Dictionary
        phonemeMapping.readCmuSpeech(CMU_SPEECH_DICTIONARY_FILEPATH);

        // Instantiate the Target Dictionary/WordList
//        dictionaryFST = new DictionaryFSTExt(DICTIONARY_FILEPATH, DICTIONARY_ENCODING, SPELL_CHECKER_DICT_FORWARD_PATH,
//                SPELL_CHECKER_DICT_REVERSE_PATH);
        dictionaryFST = new DictionaryFSTExt(DICTIONARY_FILEPATH, DICTIONARY_ENCODING, SPELL_CHECKER_DICT_FORWARD_PATH, SPELL_CHECKER_DICT_REVERSE_PATH, true, 0);

        // Instantiate AlignmentWordfreq
        tree = new AlignmentWordFreq();

        // Instantiate AksharSimilarity
        aks = new AksharSimilarity(25);
        aks.readSimilarityList(SIM_LIST_FILEPATH);

        // Assign Similarity List
        tree.getSimList(aks);

        // Load the Dictionary
        tree.readHinFreq(DICTIONARY_FILEPATH);
    }

    public TransliteratorMain(TextNormalizer textNormalizer) throws FileNotFoundException, IOException {
        this();

        this.textNormalizer = textNormalizer;
    }

    public TransliteratorMain(TextNormalizer textNormalizer, DictionaryFSTExt dictionaryFST) throws FileNotFoundException, IOException {
        langEncIdentifier = new LangEncIdentifier(LANG_ENC_TRAINPATH, LangEncIdentifier.FREQ_IDENTIFIER, scoreType,
                charNGrams, useStoredModels, useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight, charNGramOrder,
                wordNGramOrder);
        langEncIdentifier.train();

        // Make a Indian Word Analyser Read Grammar Files
        IndAnl = new RomanAnalyser();
        IndAnl.readType(IL_GRAMMAR_TYPE_ROMAN_PATH);
        IndAnl.readCons(IL_CONSONANT_MAP_ROMAN_PATH);
        IndAnl.readMatra(IL_MATRA_MAP_ROMAN_PATH);
        IndAnl.readVowel(IL_VOWEL_MAP_ROMAN_PATH);
        IndAnl.readAmbi(IL_AMBI_GRAMMAR_ROMAN_PATH);

        // Instantiate the PhoneMapping class(for foreign-origin words)
        phonemeMapping = new PhonemeMapping();
        phonemeMapping.readType(ENG_CMU_GRAMMAR_TYPE_PATH);
        phonemeMapping.readCons(ENG_CMU_GRAMMAR_CONS_PATH);
        phonemeMapping.readMatra(ENG_CMU_GRAMMAR_MATRA_PATH);
        phonemeMapping.readVowel(ENG_CMU_GRAMMAR_VOWEL_PATH);

        //To load the CMU Speech Dictionary
        phonemeMapping.readCmuSpeech(CMU_SPEECH_DICTIONARY_FILEPATH);

        this.dictionaryFST = dictionaryFST;

        // Instantiate AlignmentWordfreq
        tree = new AlignmentWordFreq();

        // Instantiate AksharSimilarity
        aks = new AksharSimilarity(25);
        aks.readSimilarityList(SIM_LIST_FILEPATH);

        // Assign Similarity List
        tree.getSimList(aks);

        // Load the Dictionary
        tree.readHinFreq(DICTIONARY_FILEPATH);

        this.textNormalizer = textNormalizer;
    }

    /**
     * @return the dictionaryFST
     */
    public//    DictionaryFST dict = null;
    DictionaryFSTExt getDictionaryFST()
    {
        return dictionaryFST;
    }

    /**
     * @param dictionaryFST the dictionaryFST to set
     */
    public void setDictionaryFST(DictionaryFSTExt dictionaryFST)
    {
        this.dictionaryFST = dictionaryFST;
    }

    /**
     * Main method for transliteration
     * @param inputStr
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List transliterate(String inputStr, PrintStream ps, PrintStream ps1) throws FileNotFoundException,
            IOException {

        String langEncoding = null;
        List translitCandidatesList = null;

        // Identify the Language and encoding of the input string
        langEncoding = getLanguageEncoding(inputStr);
//		langEncoding = "Indian";
        ps1.println(GlobalProperties.getIntlString("Lang_Encoding_::_") + inputStr + " :: " + langEncoding);

        if (GlobalProperties.getIntlString("Indian").equalsIgnoreCase(langEncoding)) {
            // Do the normal mapping
            translitCandidatesList = adhocIndTranslit(inputStr);

        } else {
            Hashtable cmuSpeechDict = phonemeMapping.getCMUSpeechDictionary();

            if (cmuSpeechDict.containsKey(inputStr.toUpperCase())) {
                //Do Phoneme-based mapping
                translitCandidatesList = doPhoneticMapping(inputStr, true);
            } else {
                //Do Phoneme-based mapping using the CMU LTS dictionary
                translitCandidatesList = doPhoneticMapping(inputStr, false);
            }

        }

        //System.out.println("FINAL OUTUPUT : \n");

        int size = translitCandidatesList.size();
        for (int i = 0; i < size; i++) {
            //System.out.println(translitCandidatesList.get(i));
            if (i > 0) {

                ps.print("|");

//                            System.out.print("|");

            }

            ps.print(translitCandidatesList.get(i));
        }
        ps.println("");
        return translitCandidatesList;
    }

    public TranslationCandidates transliterateNew(String inputStr, PrintStream ps, PrintStream ps1) throws FileNotFoundException, IOException {

//        instantiatePhonemeGraphTest = new InstantiatePhonemeGraph("UTF-8", "UTF-8", false);
//        phoneticFeatureNGramLMTest = instantiatePhonemeGraphIndian.createPhonemeUnigrams(inputStr);

        TranslationCandidates translationCandidates = new TransliterationCandidates();

        String langEncoding = null;
        List translitCandidatesList = null;

        // Identify the Language and encoding of the input string
        langEncoding = getLanguageEncoding(inputStr);
//		langEncoding = "Indian";

        if(ps1 != null)
            ps1.println(GlobalProperties.getIntlString("Lang_Encoding_::_") + inputStr + " :: " + langEncoding);

        if (GlobalProperties.getIntlString("Indian").equalsIgnoreCase(langEncoding)) {
            // Do the normal mapping
            translitCandidatesList = adhocIndTranslit(inputStr);

        } else {
            Hashtable cmuSpeechDict = phonemeMapping.getCMUSpeechDictionary();

            if (cmuSpeechDict.containsKey(inputStr.toUpperCase())) {
                //Do Phoneme-based mapping
                translitCandidatesList = doPhoneticMapping(inputStr, true);
            } else {
                //Do Phoneme-based mapping using the CMU LTS dictionary
                translitCandidatesList = doPhoneticMapping(inputStr, false);
            }

        }

        //System.out.println("FINAL OUTUPUT : \n");

        int size = translitCandidatesList.size();
        
        if(ps != null)
        {
            for (int i = 0; i < size; i++) {
                //System.out.println(translitCandidatesList.get(i));
                if (i > 0) {
                    ps.print("|");
    //                            System.out.print("|");
                }

                ps.print(translitCandidatesList.get(i));
            }
            ps.println("");
        }

        for (int i = 0; i < size; i++)
        {
            String transliteration = (String) translitCandidatesList.get(i);
            translationCandidates.addTranslationCandidateScores(transliteration, new TranslationCandidateScores());
        }

        return translationCandidates;
    }

    public TranslationCandidates transliterateNew(String inputStr) throws FileNotFoundException, IOException {
        return transliterateNew(inputStr, null, null);
    }

    /**
     * Method to return the Language encoding of the given input word
     *
     * @param inputStr
     * @return
     */
    public String getLanguageEncoding(String inputStr) {
        String langEncoding = null;

        try {
            //Captialize the inputString as the Source training files were in Capitals.
            inputStr = inputStr.toUpperCase();

            LinkedHashMap topLangEncs = langEncIdentifier.identify(inputStr);
            Iterator<String> itr = topLangEncs.keySet().iterator();

            if (itr != null && topLangEncs.size() > 0) {
                langEncoding = itr.next();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        double simIndian = NGramLMImpl.getSimilarity(phoneticFeatureNGramLMIndian, phoneticFeatureNGramLMTest);
//        double simForeign = NGramLMImpl.getSimilarity(phoneticFeatureNGramLMForeign, phoneticFeatureNGramLMTest);
//
//        if(simIndian < simForeign)
//            langEncoding = "Indian";
//        else
//            langEncoding = "Foreign";

        return langEncoding;
    }

    /**
     * Method to invoke the Mapping and Fuzzy search for Indian Origin words
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List adhocIndTranslit(String inputStr) throws FileNotFoundException,
            IOException {

        List translitCandidatesList = null;
        PrintStream ps = new PrintStream(IL_OUTPUT_MAPPED_NAMES_FILEPATH, charset);

        // IndAnl.genMapList(IL_INPUT_NAMES_FILEPATH, IL_OUTPUT_MAPPED_NAMES_FILEPATH);

        { // RomanAnalyzer.genMapList() Method invocation is replaced by this code block
            Vector maps = IndAnl.romanAnlyz(inputStr);

            for (int i = 0; i < maps.size(); i++) {
                ps.println(inputStr + "\t" + maps.get(i).toString() + "\n");
            }
            ps.close();
        }

        translitCandidatesList = doFuzzyMatch(IL_OUTPUT_MAPPED_NAMES_FILEPATH,
                FUZZY_IND_MAPFILE_PATH_PREFIX, RANKING_IND_FILE_PATH,
                RANKED_IND_OUTPUT_FILEPATH);

        return translitCandidatesList;
    }

    /**
     * Method for Phonetic mapping for the words of Foreign origin
     *
     * @param inpWord
     * @param useLTSRules - Parameter to check if the word is in CMU Speech dictionary
     * @throws IOException
     */
    public List doPhoneticMapping(String inputStr, boolean useSpeechDict) throws IOException {

        List translitCandidatesList = null;
        String phonemes = null;

        PrintStream ps = new PrintStream(ENG_OUTPUT_NAMES_MAPPED_FILEPATH, charset);

//		phonemeMapping.genMapList(ENG_INPUT_NAMES_FILEPATH,	ENG_OUTPUT_NAMES_MAPPED_FILEPATH);

        { // PhonemeMapping.getMapping() Method invocation is replaced by this code block

            if (useSpeechDict) {
                phonemes = (String) phonemeMapping.getCMUSpeechDictionary().get(inputStr.toUpperCase());
            } else {
                phonemes = phonemeMapping.getPhonemes(inputStr);
            }
            Vector maps = phonemeMapping.getMapping(phonemes);

            for (int i = 0; i < maps.size(); i++) {
                ps.println(inputStr + "\t" + maps.get(i).toString() + "\n");
//				System.out.println(inputStr + "\t" + maps.get(i).toString() + "\n");
            }
            ps.close();
        }

        translitCandidatesList = doFuzzyMatch(ENG_OUTPUT_NAMES_MAPPED_FILEPATH,
                FUZZY_ENG_MAPFILE_PATH_PREFIX, RANKING_ENG_FILE_PATH,
                RANKED_ENG_OUTPUT_FILEPATH);

        return translitCandidatesList;
    }

    /**
     * Method to the Fuzzy Match and returns the final transliteration candidates
     * @param MappedCandidatesFilePa
     * @param FuzzyMapFilePathPrefix
     * @param MappedFileForRanking
     * @param rankedOutputFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List doFuzzyMatch(String MappedCandidatesFile,
            String FuzzyMapFilePathPrefix, String MappedFileForRanking,
            String rankedOutputFile) throws FileNotFoundException, IOException {

        List finalCandidates = new ArrayList();

        // Fuzzy Search for The correct word in the Corpus
        tree.genSimilarity(MappedCandidatesFile, 750000000, pruneTopN,
                FuzzyMapFilePathPrefix,getDictionaryFST());

        // Ranking of the possible Candidates.
        AlignOutput RankSystem = new AlignOutput();
        RankSystem.readMap(MappedFileForRanking, charset);
        RankSystem.genRanks(rankedOutputFile, charset);

        FileInputStream fis = new FileInputStream(rankedOutputFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, charset));
        String readLine = null;
        String[] words = null;

        while ((readLine = reader.readLine()) != null) {
            words = readLine.split(" ");
            finalCandidates.add(words[1]);
        }

        return finalCandidates;
    }

    public LinkedHashMap<String,Double> doFuzzyMatchScores(String MappedCandidatesFile,
            String FuzzyMapFilePathPrefix, String MappedFileForRanking,
            String rankedOutputFile) throws FileNotFoundException, IOException {

        LinkedHashMap<String,Double> finalCandidates = new LinkedHashMap<String,Double>();

        // Fuzzy Search for The correct word in the Corpus
        tree.genSimilarity(MappedCandidatesFile, 750000000, pruneTopN,
                FuzzyMapFilePathPrefix,getDictionaryFST());

        // Ranking of the possible Candidates.
        AlignOutput RankSystem = new AlignOutput();
        RankSystem.readMap(MappedFileForRanking, charset);
        RankSystem.genRanks(rankedOutputFile, charset);

        FileInputStream fis = new FileInputStream(rankedOutputFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, charset));
        String readLine = null;
        String[] words = null;

        while ((readLine = reader.readLine()) != null) {
            words = readLine.split(" ");

            Double oldScore = finalCandidates.get(words[1]);
            Double score = Double.parseDouble(words[2]);

            if(oldScore == null || oldScore.doubleValue() > score.doubleValue())
                finalCandidates.put(words[1], score);
        }

        return finalCandidates;
    }

    public TranslationCandidates doFuzzyMatch(TranslationCandidates translationCandidates)
    {
        PropertyTokens tmap = ((TransliterationCandidates) translationCandidates).getTransliterationCandidatesPTForDATM();

        LinkedHashMap<String,Double> translitCandidates = null;
        try {
            tmap.save(ENG_OUTPUT_NAMES_MAPPED_FILEPATH, charset);

            translitCandidates = doFuzzyMatchScores(ENG_OUTPUT_NAMES_MAPPED_FILEPATH,
                FUZZY_ENG_MAPFILE_PATH_PREFIX, RANKING_ENG_FILE_PATH,
                RANKED_ENG_OUTPUT_FILEPATH);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TransliteratorMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TransliteratorMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TransliteratorMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        TranslationCandidates matchedTranslationCandidates = new TransliterationCandidates();

        Iterator itr = translitCandidates.keySet().iterator();

        while(itr.hasNext())
        {
            String candidate = (String) itr.next();

            Double score = translitCandidates.get(candidate);
            
            matchedTranslationCandidates.addTranslationCandidateScores(candidate, new TranslationCandidateScores(score.doubleValue()));
        }

        return matchedTranslationCandidates;
    }

    public TranslationCandidates getTransliterationCandidates(String srcWrd, boolean useSpeechDict) {
        TranslationCandidates translationCandidates = new TransliterationCandidates();

        // Identify the Language and encoding of the input string
        String langEncoding = getLanguageEncoding(srcWrd);

        Vector imaps = null;
        Vector fmaps = null;

        if (GlobalProperties.getIntlString("Indian").equalsIgnoreCase(langEncoding)) {
            imaps = IndAnl.romanAnlyz(srcWrd);
        } else {
            String phonemes = null;

            if (useSpeechDict) {
                phonemes = (String) phonemeMapping.getCMUSpeechDictionary().get(srcWrd.toUpperCase());

                if (phonemes == null) {
                    phonemes = phonemeMapping.getPhonemes(srcWrd);
                }
            } else {
                phonemes = phonemeMapping.getPhonemes(srcWrd);
            }


//            if(phonemes == null)
//                System.out.println(srcWrd);

            fmaps = phonemeMapping.getMapping(phonemes);
        }

        Pattern p = Pattern.compile("(?dum)[\\?]");

        int stop = 1000;

        if (GlobalProperties.getIntlString("Indian").equalsIgnoreCase(langEncoding)) {
            int mcount = imaps.size();

            for (int i = 0; i < mcount; i++) {
                String candidate = (String) imaps.get(i);

                Matcher m = p.matcher(candidate);

                //            candidate = candidate.replaceAll("\\?", "");
                if (m.find()) {
                    int stopCount = 0;

                    p = Pattern.compile("(?dum)[\\?]");
                    m = p.matcher(candidate);

                    String candidate1 = candidate;

                    while (m.find() && stopCount < stop) {
                        candidate1 = candidate1.replaceFirst("(?dum)[\\?]", "\u094D");
                        String candidate1a = candidate1.replaceAll("(?dum)[\\?]", "");

                        if (textNormalizer.isPossiblyValidWord(candidate1a)) {
                            translationCandidates.addTranslationCandidateScores(candidate1a.replaceAll("(?dum)[\\?]", ""), new TranslationCandidateScores());
                        }

                        p = Pattern.compile("(?dum)[\\?]");
                        m = p.matcher(candidate1);

                        stopCount++;
                    }

                    p = Pattern.compile("(?dum)[\\?]");
                    m = p.matcher(candidate);

                    stopCount = 0;

                    String candidate2 = candidate;

                    while (m.find() && stopCount < stop) {
                        candidate2 = candidate2.replaceFirst("(?dum)[\\?]", "");
                        String candidate2a = candidate2.replaceAll("(?dum)[\\?]", "\u094D");

                        if (textNormalizer.isPossiblyValidWord(candidate2a)) {
                            translationCandidates.addTranslationCandidateScores(candidate2a.replaceAll("(?dum)[\\?]", ""), new TranslationCandidateScores());
                        }

                        p = Pattern.compile("(?dum)[\\?]");
                        m = p.matcher(candidate2);

                        stopCount++;
                    }
                } else if (textNormalizer.isPossiblyValidWord(candidate)) {
                    translationCandidates.addTranslationCandidateScores(candidate, new TranslationCandidateScores());
                }
            }
        } else {
            int mcount = fmaps.size();

            for (int i = 0; i < mcount; i++) {
                String candidate = (String) fmaps.get(i);

                Matcher m = p.matcher(candidate);

                //            candidate = candidate.replaceAll("\\?", "");
                String candidate1 = candidate;

                if (m.find()) {
                    int stopCount = 0;

                    while (m.find() && stopCount < stop) {
                        candidate1 = candidate1.replaceFirst("(?dum)[\\?]", "\u094D");
                        String candidate1a = candidate1.replaceAll("(?dum)[\\?]", "");

                        if (textNormalizer.isPossiblyValidWord(candidate1a)) {
                            translationCandidates.addTranslationCandidateScores(candidate1a.replaceAll("(?dum)[\\?]", ""), new TranslationCandidateScores());
                        }

                        p = Pattern.compile("(?dum)[\\?]");
                        m = p.matcher(candidate1);

                        stopCount++;
                    }

                    stopCount = 0;

                    String candidate2 = candidate;

                    while (m.find() && stopCount < stop) {
                        candidate2 = candidate2.replaceFirst("(?dum)[\\?]", "");
                        String candidate2a = candidate2.replaceAll("(?dum)[\\?]", "\u094D");

                        if (textNormalizer.isPossiblyValidWord(candidate2a)) {
                            translationCandidates.addTranslationCandidateScores(candidate2a.replaceAll("(?dum)[\\?]", ""), new TranslationCandidateScores());
                        }

                        p = Pattern.compile("(?dum)[\\?]");
                        m = p.matcher(candidate2);

                        stopCount++;
                    }
                } else if (textNormalizer.isPossiblyValidWord(candidate)) {
                    translationCandidates.addTranslationCandidateScores(candidate, new TranslationCandidateScores());
                }
            }
        }

        return translationCandidates;
    }

    public TransliterationCandidates transliterateLDATM(String inputStr, boolean useSpeechDict) {
        TranslationCandidates translationCandidates = getTransliterationCandidates(inputStr, useSpeechDict);

//        System.out.println("********************");
//
//        translationCandidates.printTranslationCandidates(System.out, false);
//
//        System.out.println("********************");

        TransliterationCandidates translationCandidatesRet = new TransliterationCandidates();
        
        Iterator cndItr = translationCandidates.getTranslationCandidates();

        while(cndItr.hasNext())
        {
            String candidate = (String) cndItr.next();

            LinkedHashMap<DictionaryFSTNode, Double> matchedWords = getDictionaryFST().getNearestWords(candidate, 20, false);

            Iterator itr = matchedWords.keySet().iterator();

            while(itr.hasNext())
            {
                Object fstNode = itr.next();
                Double d = matchedWords.get(fstNode);

                String cstr = ((DictionaryFSTNode) fstNode).getWordString();

//                System.out.println(candidate + "\t" + cstr + "\t" + d);

                TranslationCandidateScores scores = new TranslationCandidateScores(d.doubleValue());

                TranslationCandidateScores oldScores = translationCandidatesRet.getTranslationCandidateScores(cstr);

                if(oldScores == null || oldScores.getTranslationScore() > scores.getTranslationScore())
                    translationCandidatesRet.addTranslationCandidateScores(cstr, scores);
            }
        }

        translationCandidatesRet.normalizeScores(true, true);
        translationCandidatesRet.pruneAndSortTranslationCandidates(true);

        return translationCandidatesRet;
    }

    public void loadPhoneticModelOfScripts()
    {
        getDictionaryFST().loadPhoneticModelOfScripts();
    }

    public static void main(String[] args) {
        //String inputWord = "sharma";
        String cs = GlobalProperties.getIntlString("UTF-8");

        try {
//            TransliteratorMain transliterator = new TransliteratorMain();
            String inFile = "/home/anil/tmp/feature_based_code/50english_queries-unique-unks.txt";
            String outFile = "/home/anil/tmp/feature_based_code/50english_queries-unique-unks-transliterated.txt";
            KeyValueProperties ikvp = new KeyValueProperties();
            PrintStream ps = new PrintStream(outFile, cs);

            ikvp.read(inFile, cs);
//            String inFile = DATA_FOLDER_PATH + "data/translit/input-1.txt";
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

//            PrintStream ps = new PrintStream(inFile + "_tl.txt", cs);

//            PrintStream ps1 = new PrintStream(inFile + "_lang.txt", cs);



            //String inputWord = "CONGRESS";

//            String inputWord = "prakash";

            TextNormalizer textNormalizer = new TextNormalizer(GlobalProperties.getIntlString("hin::utf8"), GlobalProperties.getIntlString("UTF-8"), "", "", false);
            TransliteratorMain transliterator = new TransliteratorMain(textNormalizer);
            transliterator.loadPhoneticModelOfScripts();

            Iterator itr = ikvp.getPropertyKeys();

            while(itr.hasNext())
            {
//                String inputWord = "rajasthan";
                String inputWord = (String) itr.next();

                inputWord = inputWord.replaceAll("'", "");
                inputWord = inputWord.replaceAll("-", "");
                inputWord = inputWord.replaceAll("\\.", "");

//                if(inputWord.length() > 10)
//                    continue;
    //            transliterator.transliterateNew(inputWord, ps, ps1);

                TransliterationCandidates candidates = (TransliterationCandidates) transliterator.transliterateLDATM(inputWord, true);
//                candidates.printTranslationCandidates(ps, false);

                Iterator inItr = candidates.getTranslationCandidates();

                while(inItr.hasNext())
                {
                    String cnd = (String) inItr.next();
                    TranslationCandidateScores score = candidates.getTranslationCandidateScores(cnd);

                    ps.println(inputWord + "\t" + cnd + "\t" + score.getTranslationScore());
                }
            }

            ps.close();

            //TransliteratorMain transliterator = new TransliteratorMain();

//            while ((inputWord = br.readLine()) != null) {
//                ps.print(inputWord + "\t");
//
//                ps1.print(inputWord + "\t");
//
//                // System.out.print(inputWord+"\t");
//
//                transliterator.transliterate(inputWord, ps, ps1);
//
//            }
        /*transliterator.doFuzzyMatch(IL_OUTPUT_MAPPED_NAMES_FILEPATH,
        FUZZY_IND_MAPFILE_PATH_PREFIX, RANKING_IND_FILE_PATH,
        RANKED_IND_OUTPUT_FILEPATH);
         */
        //transliterator.transliterate(inputWord);
        //transliterator.adhocIndTranslit("");


//			ArrayList list = transliterator.doPhoneticMapping(inputWord,true);
//			
//			for (Object object : list) {
//				System.out.println(object);
//			}

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
