/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import com.sun.speech.freetts.lexicon.LetterToSound;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.GlobalProperties;
import sanchay.ili.PhonemeMapping;
import sanchay.ili.RomanAnalyser;
import sanchay.langenc.LangEncIdentifier;
import sanchay.text.DictionaryFST;
import sanchay.text.DictionaryFSTNode;
import sanchay.text.TextNormalizer;
import sanchay.text.spell.DictionaryFSTExt;

/**
 *
 * @author anil
 */
public class TransliterationCandidateGenerator {
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

    public final static String DATA_FOLDER_PATH = "./";
    // Language encoding identification inputs
    public final static String LANG_ENC_TRAINPATH = DATA_FOLDER_PATH + "data/transliteration/Lang_enc_id/trainInfo";
    // Indian Origin words Mapping Grammar files' paths
    public final static String IL_GRAMMAR_TYPE_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/GrammarTypeRoman";
    public final static String IL_CONSONANT_MAP_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/ConsantMapRomanDevProperNames";
    public final static String IL_MATRA_MAP_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/MatraMapRomanDev";
    public final static String IL_VOWEL_MAP_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/VowelMapRomanDev";
    public final static String IL_AMBI_GRAMMAR_ROMAN_PATH = DATA_FOLDER_PATH + "data/transliteration/AmbiGrammarRoman";
//    public final static String IL_INPUT_NAMES_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/IndNames.txt";
//    public final static String IL_OUTPUT_MAPPED_NAMES_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/HindiNames.MAP";

    // Foreign Origin words Mapping files' paths
    public final static String ENG_CMU_GRAMMAR_TYPE_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Type";
    public final static String ENG_CMU_GRAMMAR_CONS_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Cons1";
    public final static String ENG_CMU_GRAMMAR_MATRA_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Matra";
    public final static String ENG_CMU_GRAMMAR_VOWEL_PATH = DATA_FOLDER_PATH + "data/transliteration/ILI_Eng/CMU_Grammar_Vowel";
//    public final static String ENG_INPUT_NAMES_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/Transfer/TelCMU";
//    public final static String ENG_OUTPUT_NAMES_MAPPED_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/Transfer/TelCMU.Mapped";

    // Dictionary(Wordlist) path
    //public final static String DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/wordList";
//    public final static String DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/WordLists/Hindi_wordList.txt";
    public final static String DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/word-translation/word-lists/hindi-wordlist-translit.txt";
    public final static String DICTIONARY_ENCODING = "UTF-8";
    public final static String SPELL_CHECKER_DICT_FORWARD_PATH = DATA_FOLDER_PATH + "data/transliteration/spell-checker-dict.forward";
    public final static String SPELL_CHECKER_DICT_REVERSE_PATH = DATA_FOLDER_PATH + "data/transliteration/spell-checker-dict.reverse";

    public final static String CMU_SPEECH_DICTIONARY_FILEPATH = DATA_FOLDER_PATH + "data/transliteration/cmudict.0.7a";

    protected LangEncIdentifier langEncIdentifier = null;
    protected RomanAnalyser IndAnl = null;
    protected DictionaryFST targetDictionary = null;

    protected PhonemeMapping phonemeMapping = null;
    protected LetterToSound letterToSound = null;
    protected TextNormalizer textNormalizer;

    int pruneTopN = 10;

    public TransliterationCandidateGenerator() {
        
    }

    public TransliterationCandidateGenerator(TextNormalizer textNormalizer) throws FileNotFoundException, IOException {
        this();

        this.textNormalizer = textNormalizer;
    }

    public TransliterationCandidateGenerator(TextNormalizer textNormalizer, DictionaryFST dict) throws FileNotFoundException, IOException {
        this();

        this.textNormalizer = textNormalizer;
        this.targetDictionary = dict;
    }

    /**
     * @return the targetDictionary
     */
    public DictionaryFST getTargetDictionary()
    {
        return targetDictionary;
    }

    /**
     * @param targetDictionary the targetDictionary to set
     */
    public void setTargetDictionary(DictionaryFST targetDictionary)
    {
        this.targetDictionary = targetDictionary;
    }

    public void init() throws FileNotFoundException, IOException
    {
        loadLM();

        readMappings();

        loadSpeechDict();

        loadTargetDict();
    }

    public void loadLM() throws FileNotFoundException, IOException
    {
        //Instantiate & train the Language Encoding Identifier
        langEncIdentifier = new LangEncIdentifier(LANG_ENC_TRAINPATH, LangEncIdentifier.FREQ_IDENTIFIER, scoreType,
                charNGrams, useStoredModels, useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight, charNGramOrder,
                wordNGramOrder);
        langEncIdentifier.train();
    }

    public void readMappings() throws FileNotFoundException, IOException
    {
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
    }

    public void loadSpeechDict() throws FileNotFoundException, IOException
    {
        //To load the CMU Speech Dictionary
        phonemeMapping.readCmuSpeech(CMU_SPEECH_DICTIONARY_FILEPATH);
    }

    public void loadTargetDict() throws FileNotFoundException, IOException
    {
        // Instantiate the Target Dictionary/WordList
        setTargetDictionary(new DictionaryFSTExt(DICTIONARY_FILEPATH, DICTIONARY_ENCODING, SPELL_CHECKER_DICT_FORWARD_PATH, SPELL_CHECKER_DICT_REVERSE_PATH, true, 0));
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

    public TranslationCandidates getTransliterationCandidates(String srcWrd, boolean useSpeechDict, boolean mergeAll) {
        TranslationCandidates translationCandidates = new TransliterationCandidates();

        // Identify the Language and encoding of the input string
        String langEncoding = getLanguageEncoding(srcWrd);

        Vector imaps = null;
        Vector fmaps = null;

        if ("Indian".equalsIgnoreCase(langEncoding)) {
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

        if ("Indian".equalsIgnoreCase(langEncoding) || mergeAll) {
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
        }

        if ("Indian".equalsIgnoreCase(langEncoding) == false || mergeAll)
        {
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

    public TranslationCandidates doFuzzyMatch(TranslationCandidates translationCandidates, int nearest)
    {
        Iterator itr = translationCandidates.getTranslationCandidates();

        TranslationCandidates matchedTranslationCandidates = new TransliterationCandidates();

        while(itr.hasNext())
        {
            String candidate = (String) itr.next();

            LinkedHashMap<DictionaryFSTNode,Double> nearestMatches = getTargetDictionary().getNearestWords(candidate, nearest, false);

            Iterator fmItr = nearestMatches.keySet().iterator();

            while(fmItr.hasNext())
            {
                DictionaryFSTNode node = (DictionaryFSTNode) fmItr.next();
                
                Double matchScore = nearestMatches.get(node);

                TranslationCandidateScores matchedTranslationCandidatesScores = matchedTranslationCandidates.getTranslationCandidateScores(node.getWordString());

                if(matchedTranslationCandidatesScores == null)
                {
                    matchedTranslationCandidatesScores = new TranslationCandidateScores(matchScore.doubleValue());
                    matchedTranslationCandidates.addTranslationCandidateScores(node.getWordString(), matchedTranslationCandidatesScores);
                }
                else
                {
                    matchedTranslationCandidatesScores.setTranslationScore(Math.min(matchedTranslationCandidatesScores.getTranslationScore(), matchScore.doubleValue()));
                }
            }
        }

        return matchedTranslationCandidates;
    }
}
