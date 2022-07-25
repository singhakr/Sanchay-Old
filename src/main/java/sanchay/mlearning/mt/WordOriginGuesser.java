/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.properties.PropertyTokens;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.mlearning.lm.ngram.impl.NGramLMImpl;
import sanchay.text.spell.InstantiatePhonemeGraph;
import sanchay.text.spell.PhonemeFeatureModel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class WordOriginGuesser {

    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected LinkedHashMap<String, PhonemeFeatureModel> phonemeFeatureModels;

    protected PropertyTokens wordListPT;

    public static final int PHONEMIC_DISTRIBUTIONAL_SIMILARITY = 0;
    public static final int PHONEMIC_DISTRIBUTIONAL_DISTANCE = 1;
    public static final int PHONEMIC_SEQUENCE_PROBABILITY = 2;
    public static final int FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY = 3;
    public static final int FEATURE_BASED_DISTRIBUTIONAL_DISTANCE = 4;
    public static final int FEATURE_BASED_SEQUENCE_PROBABILITY = 5;

    public WordOriginGuesser() {
        phonemeFeatureModels = new LinkedHashMap<String, PhonemeFeatureModel>();
    }

    public void compilePhonemeFeatureModels()
    {
//        compilePhonemeFeatureModel("Sanskrit", "data/cognate-classification/training/Sanskrit-training.txt");
//        compilePhonemeFeatureModel("Persian", "data/cognate-classification/training/Persian-training.txt");
//        compilePhonemeFeatureModel("English", "data/cognate-classification/training/English-training.txt");

//        compilePhonemeFeatureModel("Sanskrit", "data/cognate-classification/wordlist-sets-word-origin/Sanskrit-Training.txt");
//        compilePhonemeFeatureModel("Persian", "data/cognate-classification/wordlist-sets-word-origin/Persian-Training.txt");
//        compilePhonemeFeatureModel("English", "data/cognate-classification/wordlist-sets-word-origin/English-Training.txt");

        compilePhonemeFeatureModel("Sanskrit", "data/cognate-classification/wordlist-sets-word-origin/Indian/set_tatsam_train5-BE.txt");
        compilePhonemeFeatureModel("Persian", "data/cognate-classification/wordlist-sets-word-origin/Indian/set_pers_train5-BE.txt");
        compilePhonemeFeatureModel("English", "data/cognate-classification/wordlist-sets-word-origin/Foreign/set_eng_train5-BE.txt");
    }

    public void compilePhonemeFeatureModel(String modelName, String instantiatePhonemeGraphPath)
    {
        InstantiatePhonemeGraph instantiatePhonemeGraph = new InstantiatePhonemeGraph(langEnc, charset, false);
        NGramLM phoneticFeatureNGramLM = instantiatePhonemeGraph.createPhonemeUnigrams(new File(instantiatePhonemeGraphPath));

        PhonemeFeatureModel phonemeFeatureModel = new PhonemeFeatureModel();
        phonemeFeatureModel.setPhoneticFeatureNGramLM(phoneticFeatureNGramLM);

        NGramLM charNGramLMImpl = new NGramLMImpl(new File(instantiatePhonemeGraphPath), "uchar", 3, GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));

        try
        {
            charNGramLMImpl.makeNGramLM(new File(instantiatePhonemeGraphPath));
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(WordOriginGuesser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(WordOriginGuesser.class.getName()).log(Level.SEVERE, null, ex);
        }

//        charNGramLMImpl.calcSmoothProbs("Witten-Bell", (long) 100);
//        charNGramLMImpl.calcSmoothKneserNey(0.5);

//        charNGramLMImpl.calcBackoff();

        phonemeFeatureModel.setCharNGramLMImpl(charNGramLMImpl);

        phonemeFeatureModels.put(modelName, phonemeFeatureModel);
    }

    public void loadPhonemeFeatureModels()
    {

    }

    public void guessWordOriginFile(String correcOrigin, int mode)
    {
        System.out.println("**************************************************");

        if(mode == FEATURE_BASED_DISTRIBUTIONAL_DISTANCE)
            System.out.println("FEATURE_BASED_DISTRIBUTIONAL_DISTANCE");
        else if(mode == PHONEMIC_DISTRIBUTIONAL_DISTANCE)
            System.out.println("PHONEMIC_DISTRIBUTIONAL_DISTANCE");
        else if(mode == FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY)
            System.out.println("FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY");
        else if(mode == PHONEMIC_DISTRIBUTIONAL_SIMILARITY)
            System.out.println("PHONEMIC_DISTRIBUTIONAL_SIMILARITY");
        else if(mode == FEATURE_BASED_SEQUENCE_PROBABILITY)
            System.out.println("FEATURE_BASED_SEQUENCE_PROBABILITY");
        else if(mode == PHONEMIC_SEQUENCE_PROBABILITY)
            System.out.println("PHONEMIC_SEQUENCE_PROBABILITY");

        int correctCount = 0;

        int count = wordListPT.countTokens();

        for (int i = 0; i < count; i++)
        {
            String word = wordListPT.getToken(i);

            String origin = guessWordOrigin(word, mode);

            System.out.println(word + "::" + origin);

            if(origin.equalsIgnoreCase(correcOrigin))
                correctCount++;
        }

        double precision = ((double) correctCount) / ((double) count);

        System.out.println("Precision for " + correcOrigin + ": " + precision * 100.0 + "%");
        System.out.println("**************************************************");
    }

    public String guessWordOrigin(String word, int mode)
    {
        PhonemeFeatureModel sanskritPhonemeFeatureModel = phonemeFeatureModels.get("Sanskrit");
        PhonemeFeatureModel persianPhonemeFeatureModel = phonemeFeatureModels.get("Persian");
        PhonemeFeatureModel englishPhonemeFeatureModel = phonemeFeatureModels.get("English");

        if(mode == FEATURE_BASED_SEQUENCE_PROBABILITY || mode == PHONEMIC_SEQUENCE_PROBABILITY)
        {
            double probSanskrit = 0.0;
            double probPersian = 0.0;
            double probEnglish = 0.0;

            if(mode == FEATURE_BASED_SEQUENCE_PROBABILITY)
            {
                probSanskrit = sanskritPhonemeFeatureModel.getFeatureBasedSequenceProbability(UtilityFunctions.getSpacedOutString(word));
                probPersian = persianPhonemeFeatureModel.getFeatureBasedSequenceProbability(UtilityFunctions.getSpacedOutString(word));
                probEnglish = englishPhonemeFeatureModel.getFeatureBasedSequenceProbability(UtilityFunctions.getSpacedOutString(word));
            }
            else if(mode == PHONEMIC_SEQUENCE_PROBABILITY)
            {
                probSanskrit = sanskritPhonemeFeatureModel.getPhonemicSequenceProbability(UtilityFunctions.getSpacedOutString(word));
                probPersian = persianPhonemeFeatureModel.getPhonemicSequenceProbability(UtilityFunctions.getSpacedOutString(word));
                probEnglish = englishPhonemeFeatureModel.getPhonemicSequenceProbability(UtilityFunctions.getSpacedOutString(word));
            }

            System.out.println("Sanskrit" + "\t" + probSanskrit);
            System.out.println("Persian" + "\t" + probPersian);
            System.out.println("English" + "\t" + probEnglish);

            if(probSanskrit >= probPersian && probSanskrit >= probEnglish)
                return "Sanskrit";

            if(probPersian >= probSanskrit && probPersian >= probEnglish)
                return "Persian";

            if(probEnglish >= probSanskrit && probEnglish >= probPersian)
                return "English";
        }
        else if(mode == FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY || mode == PHONEMIC_DISTRIBUTIONAL_SIMILARITY)
        {
            double simSanskrit = 0.0;
            double simPersian = 0.0;
            double simEnglish = 0.0;

            if(mode == FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY)
            {
                simSanskrit = sanskritPhonemeFeatureModel.getFeatureBasedDistributionalSimilarity(UtilityFunctions.getSpacedOutString(word));
                simPersian = persianPhonemeFeatureModel.getFeatureBasedDistributionalSimilarity(UtilityFunctions.getSpacedOutString(word));
                simEnglish = englishPhonemeFeatureModel.getFeatureBasedDistributionalSimilarity(UtilityFunctions.getSpacedOutString(word));
            }
            else if(mode == PHONEMIC_DISTRIBUTIONAL_SIMILARITY)
            {
                simSanskrit = sanskritPhonemeFeatureModel.getPhonemicDistributionalSimilarity(UtilityFunctions.getSpacedOutString(word));
                simPersian = persianPhonemeFeatureModel.getPhonemicDistributionalSimilarity(UtilityFunctions.getSpacedOutString(word));
                simEnglish = englishPhonemeFeatureModel.getPhonemicDistributionalSimilarity(UtilityFunctions.getSpacedOutString(word));
            }

            System.out.println("Sanskrit" + "\t" + simSanskrit);
            System.out.println("Persian" + "\t" + simPersian);
            System.out.println("English" + "\t" + simEnglish);

            if(simSanskrit <= simPersian && simSanskrit <= simEnglish)
                return "Sanskrit";

            if(simPersian <= simSanskrit && simPersian <= simEnglish)
                return "Persian";

            if(simEnglish <= simSanskrit && simEnglish <= simPersian)
                return "English";
        }
        else if(mode == FEATURE_BASED_DISTRIBUTIONAL_DISTANCE || mode == PHONEMIC_DISTRIBUTIONAL_DISTANCE)
        {
            double diffSanskrit = 0.0;
            double diffPersian = 0.0;
            double diffEnglish = 0.0;

            if(mode == FEATURE_BASED_DISTRIBUTIONAL_DISTANCE)
            {
                diffSanskrit = sanskritPhonemeFeatureModel.getFeatureBasedDistributionalDistance(UtilityFunctions.getSpacedOutString(word));
                diffPersian = persianPhonemeFeatureModel.getFeatureBasedDistributionalDistance(UtilityFunctions.getSpacedOutString(word));
                diffEnglish = englishPhonemeFeatureModel.getFeatureBasedDistributionalDistance(UtilityFunctions.getSpacedOutString(word));
            }
            else if(mode == PHONEMIC_DISTRIBUTIONAL_DISTANCE)
            {
                diffSanskrit = sanskritPhonemeFeatureModel.getPhonemicDistributionalDistance(UtilityFunctions.getSpacedOutString(word));
                diffPersian = persianPhonemeFeatureModel.getPhonemicDistributionalDistance(UtilityFunctions.getSpacedOutString(word));
                diffEnglish = englishPhonemeFeatureModel.getPhonemicDistributionalDistance(UtilityFunctions.getSpacedOutString(word));
            }

            System.out.println("Sanskrit" + "\t" + diffSanskrit);
            System.out.println("Persian" + "\t" + diffPersian);
            System.out.println("English" + "\t" + diffEnglish);

//            if(diffSanskrit > diffPersian && diffSanskrit > diffEnglish)
            if(diffSanskrit <= diffPersian && diffSanskrit <= diffEnglish)
                return "Sanskrit";

//            if(diffPersian > diffSanskrit && diffPersian > diffEnglish)
            if(diffPersian <= diffSanskrit && diffPersian <= diffEnglish)
                return "Persian";

//            if(diffEnglish > diffSanskrit && diffEnglish > diffPersian)
            if(diffEnglish <= diffSanskrit && diffEnglish <= diffPersian)
                return "English";
        }

        return "Don't Know";
    }

    public void readWordList(String path, String cs)
    {
        wordListPT = new PropertyTokens();

        try
        {
            wordListPT.read(path, cs);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(WordOriginGuesser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(WordOriginGuesser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        WordOriginGuesser wordOriginGuesser = new WordOriginGuesser();

        wordOriginGuesser.compilePhonemeFeatureModels();

//        String origin = wordOriginGuesser.guessWordOrigin("पोस्टमॉडर्न");
//        System.out.println(origin);
//
//        origin = wordOriginGuesser.guessWordOrigin("कलाकृति");
//        System.out.println(origin);
//
//        origin = wordOriginGuesser.guessWordOrigin("बेपनाह");
//        System.out.println(origin);

//        wordOriginGuesser.readWordList("data/cognate-classification/testing/3/0-hin-eval.txt", "UTF-8");
//        wordOriginGuesser.guessWordOriginFile("Sanskrit", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_DISTANCE);
//
//        wordOriginGuesser.readWordList("data/cognate-classification/testing/3/1-hin-eval.txt", "UTF-8");
//        wordOriginGuesser.guessWordOriginFile("Persian", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_DISTANCE);
//
//        wordOriginGuesser.readWordList("data/cognate-classification/testing/3/2-hin-eval.txt", "UTF-8");
//        wordOriginGuesser.guessWordOriginFile("English", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_DISTANCE);

        wordOriginGuesser.readWordList("data/cognate-classification/wordlist-sets-word-origin/Indian/set_tatsam_test5-BE.txt", "UTF-8");
        wordOriginGuesser.guessWordOriginFile("Sanskrit", WordOriginGuesser.FEATURE_BASED_DISTRIBUTIONAL_DISTANCE);
        wordOriginGuesser.guessWordOriginFile("Sanskrit", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_DISTANCE);
//        wordOriginGuesser.guessWordOriginFile("Sanskrit", WordOriginGuesser.FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY);
//        wordOriginGuesser.guessWordOriginFile("Sanskrit", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_SIMILARITY);
//        wordOriginGuesser.guessWordOriginFile("Sanskrit", WordOriginGuesser.FEATURE_BASED_SEQUENCE_PROBABILITY);
//        wordOriginGuesser.guessWordOriginFile("Sanskrit", WordOriginGuesser.PHONEMIC_SEQUENCE_PROBABILITY);

        wordOriginGuesser.readWordList("data/cognate-classification/wordlist-sets-word-origin/Indian/set_pers_test5-BE.txt", "UTF-8");
        wordOriginGuesser.guessWordOriginFile("Persian", WordOriginGuesser.FEATURE_BASED_DISTRIBUTIONAL_DISTANCE);
        wordOriginGuesser.guessWordOriginFile("Persian", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_DISTANCE);
//        wordOriginGuesser.guessWordOriginFile("Persian", WordOriginGuesser.FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY);
//        wordOriginGuesser.guessWordOriginFile("Persian", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_SIMILARITY);
//        wordOriginGuesser.guessWordOriginFile("Persian", WordOriginGuesser.FEATURE_BASED_SEQUENCE_PROBABILITY);
//        wordOriginGuesser.guessWordOriginFile("Persian", WordOriginGuesser.PHONEMIC_SEQUENCE_PROBABILITY);

        wordOriginGuesser.readWordList("data/cognate-classification/wordlist-sets-word-origin/Foreign/set_eng_test5-BE.txt", "UTF-8");
        wordOriginGuesser.guessWordOriginFile("English", WordOriginGuesser.FEATURE_BASED_DISTRIBUTIONAL_DISTANCE);
        wordOriginGuesser.guessWordOriginFile("English", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_DISTANCE);
//        wordOriginGuesser.guessWordOriginFile("English", WordOriginGuesser.FEATURE_BASED_DISTRIBUTIONAL_SIMILARITY);
//        wordOriginGuesser.guessWordOriginFile("English", WordOriginGuesser.PHONEMIC_DISTRIBUTIONAL_SIMILARITY);
//        wordOriginGuesser.guessWordOriginFile("English", WordOriginGuesser.FEATURE_BASED_SEQUENCE_PROBABILITY);
//        wordOriginGuesser.guessWordOriginFile("English", WordOriginGuesser.PHONEMIC_SEQUENCE_PROBABILITY);
    }
}
