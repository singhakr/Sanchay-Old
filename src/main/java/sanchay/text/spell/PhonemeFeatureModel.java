/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.text.spell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.mlearning.lm.ngram.impl.NGramLMImpl;
import sanchay.properties.PropertyTokens;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class PhonemeFeatureModel {
    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected PhoneticModelOfScripts phoneticModelOfScripts;

    protected ConditionalProbabilities phonemeFeatureProbabilities;

    protected PropertyTokens featurePrecedence;

    protected NGramLM charNGramLMImpl;

    protected NGramLM phoneticFeatureNGramLM;
    protected NGramLM phoneticFeatureNGramLMTgt;

    protected InstantiatePhonemeGraph instantiatePhonemeGraph;
    protected String instantiatePhonemeGraphPath = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/NEWS09_dev_EnHi_974-hindi-hin.txt";

    public PhonemeFeatureModel()
    {
        try
        {
            phoneticModelOfScripts = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
                    GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
//		instantiatePhonemeGraph = new InstantiatePhonemeGraph(langEnc, charset, false);
//        phoneticFeatureNGramLM = instantiatePhonemeGraph.createPhonemeUnigrams(new File(instantiatePhonemeGraphPath));
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(PhonemeFeatureModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(PhonemeFeatureModel.class.getName()).log(Level.SEVERE, null, ex);
        }
//		instantiatePhonemeGraph = new InstantiatePhonemeGraph(langEnc, charset, false);
//        phoneticFeatureNGramLM = instantiatePhonemeGraph.createPhonemeUnigrams(new File(instantiatePhonemeGraphPath));
    }

    /**
     * @return the langEnc
     */
    public String getLangEnc()
    {
        return langEnc;
    }

    /**
     * @param langEnc the langEnc to set
     */
    public void setLangEnc(String langEnc)
    {
        this.langEnc = langEnc;
    }

    /**
     * @return the charset
     */
    public String getCharset()
    {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    /**
     * @return the phoneticModelOfScripts
     */
    public PhoneticModelOfScripts getPhoneticModelOfScripts()
    {
        return phoneticModelOfScripts;
    }

    /**
     * @param phoneticModelOfScripts the phoneticModelOfScripts to set
     */
    public void setPhoneticModelOfScripts(PhoneticModelOfScripts phoneticModelOfScripts)
    {
        this.phoneticModelOfScripts = phoneticModelOfScripts;
    }

    /**
     * @return the phonemeFeatureProbabilities
     */
    public ConditionalProbabilities getPhonemeFeatureProbabilities()
    {
        return phonemeFeatureProbabilities;
    }

    /**
     * @param phonemeFeatureProbabilities the phonemeFeatureProbabilities to set
     */
    public void setPhonemeFeatureProbabilities(ConditionalProbabilities phonemeFeatureProbabilities)
    {
        this.phonemeFeatureProbabilities = phonemeFeatureProbabilities;
    }

    /**
     * @return the featurePrecedence
     */
    public PropertyTokens getFeaturePrecedence()
    {
        return featurePrecedence;
    }

    /**
     * @param featurePrecedence the featurePrecedence to set
     */
    public void setFeaturePrecedence(PropertyTokens featurePrecedence)
    {
        this.featurePrecedence = featurePrecedence;
    }

    /**
     * @return the charNGramLMImpl
     */
    public NGramLM getCharNGramLMImpl()
    {
        return charNGramLMImpl;
    }

    /**
     * @param charNGramLMImpl the charNGramLMImpl to set
     */
    public void setCharNGramLMImpl(NGramLM charNGramLMImpl)
    {
        this.charNGramLMImpl = charNGramLMImpl;
    }

    /**
     * @return the phoneticFeatureNGramLM
     */
    public NGramLM getPhoneticFeatureNGramLM()
    {
        return phoneticFeatureNGramLM;
    }

    /**
     * @param phoneticFeatureNGramLM the phoneticFeatureNGramLM to set
     */
    public void setPhoneticFeatureNGramLM(NGramLM phoneticFeatureNGramLM)
    {
        this.phoneticFeatureNGramLM = phoneticFeatureNGramLM;
    }
    
    public double getPhonemeUniGramProb(String unigram)
    {
        double prob = 0.0;

        FeatureStructure fs = phoneticModelOfScripts.getFeatureStructure(unigram.charAt(0), langEnc);

        if(unigram.charAt(0) == '~')
            fs = phoneticModelOfScripts.getFeatureStructureForWordStart();
        else if(unigram.charAt(0) == '^')
            fs = phoneticModelOfScripts.getFeatureStructureForWordEnd();

        if(fs != null)
        {
            int acount = fs.countAttributes();

            for (int i = 0; i < acount; i++)
            {
                FeatureAttribute fa = fs.getAttribute(i);
                FeatureValue fv = fa.getAltValue(0);

                String valStr = (String) fv.getValue();

                String fvString = fa.getName() + "=" + valStr;

                NGram ng = (NGram) phoneticFeatureNGramLM.getNGram(fvString, 1);

                prob += ng.getProb();
//                prob *= ng.getProb();
            }
        }

        return prob;
    }

    public double getPhonemeBiGramProb(String bigram)
    {
        double prob = 0.0;

        String parts[] = bigram.split("@#&");

        char ch1 = parts[0].charAt(0);
        char ch2 = parts[1].charAt(0);
        
        FeatureStructure fs1 = phoneticModelOfScripts.getFeatureStructure(ch1, langEnc);

        if(ch1 == '~')
            fs1 = phoneticModelOfScripts.getFeatureStructureForWordStart();
        else if(ch1 == '^')
            fs1 = phoneticModelOfScripts.getFeatureStructureForWordEnd();

        FeatureStructure fs2 = phoneticModelOfScripts.getFeatureStructure(ch2, langEnc);

        if(ch2 == '~')
            fs2 = phoneticModelOfScripts.getFeatureStructureForWordStart();
        else if(ch2 == '^')
            fs2 = phoneticModelOfScripts.getFeatureStructureForWordEnd();

        if(fs1 != null)
        {
            int acount1 = fs1.countAttributes();

            for (int i = 0; i < acount1; i++)
            {
                FeatureAttribute fa1 = fs1.getAttribute(i);
                FeatureValue fv1 = fa1.getAltValue(0);

                String valStr1 = (String) fv1.getValue();

                String fvString1 = fa1.getName() + "=" + valStr1;

                if(fs2 != null)
                {
                    int acount2 = fs2.countAttributes();

                    for (int j = 0; j < acount2; j++)
                    {
                        FeatureAttribute fa2 = fs2.getAttribute(j);
                        FeatureValue fv2 = fa2.getAltValue(0);

                        String valStr2 = (String) fv2.getValue();

                        String fvString2 = fa2.getName() + "=" + valStr2;

                        String fvString = fvString1 + "@#&" + fvString2;

                        NGram ng = (NGram) phoneticFeatureNGramLM.getNGram(fvString, 2);

                        if(ng != null)
                            prob += ng.getProb();
//                            prob *= ng.getProb();
                        else
                        {
                            ng = (NGram) phoneticFeatureNGramLM.getNGram(fvString1, 1);

                            if(ng != null)
                               prob += Math.pow(ng.getProb(), 4.0);
//                               prob *= Math.pow(ng.getProb(), 4.0);
                        }
                    }
                }
            }
        }

        return prob;
    }

    public double getPhonemeTriGramProb(String trigram)
    {
        double prob = 0.0;

        String parts[] = trigram.split("@#&");

        char ch1 = parts[0].charAt(0);
        char ch2 = parts[1].charAt(0);
        char ch3 = parts[2].charAt(0);

        FeatureStructure fs1 = phoneticModelOfScripts.getFeatureStructure(ch1, langEnc);

        if(ch1 == '~')
            fs1 = phoneticModelOfScripts.getFeatureStructureForWordStart();
        else if(ch1 == '^')
            fs1 = phoneticModelOfScripts.getFeatureStructureForWordEnd();

        FeatureStructure fs2 = phoneticModelOfScripts.getFeatureStructure(ch2, langEnc);

//        if(ch2 == '~')
//            fs2 = phoneticModelOfScripts.getFeatureStructureForWordStart();
//        else if(ch2 == '^')
//            fs2 = phoneticModelOfScripts.getFeatureStructureForWordEnd();

        FeatureStructure fs3 = phoneticModelOfScripts.getFeatureStructure(ch3, langEnc);

        if(ch3 == '~')
            fs3 = phoneticModelOfScripts.getFeatureStructureForWordStart();
        else if(ch3 == '^')
            fs3 = phoneticModelOfScripts.getFeatureStructureForWordEnd();

        if(fs1 != null)
        {
            int acount1 = fs1.countAttributes();

            for (int i = 0; i < acount1; i++)
            {
                FeatureAttribute fa1 = fs1.getAttribute(i);
                FeatureValue fv1 = fa1.getAltValue(0);

                String valStr1 = (String) fv1.getValue();

                String fvString1 = fa1.getName() + "=" + valStr1;

                if(fs2 != null)
                {
                    int acount2 = fs2.countAttributes();

                    for (int j = 0; j < acount2; j++)
                    {
                        FeatureAttribute fa2 = fs2.getAttribute(j);
                        FeatureValue fv2 = fa2.getAltValue(0);

                        String valStr2 = (String) fv2.getValue();

                        String fvString2 = fa2.getName() + "=" + valStr2;

                        if(fs3 != null)
                        {
                            int acount3 = fs3.countAttributes();

                            for (int k = 0; k < acount3; k++)
                            {
                                FeatureAttribute fa3 = fs3.getAttribute(k);
                                FeatureValue fv3 = fa3.getAltValue(0);

                                String valStr3 = (String) fv3.getValue();

                                String fvString3 = fa3.getName() + "=" + valStr3;

                                String fvString = fvString1 + "@#&" + fvString2 + "@#&" + fvString3;

                                NGram ng = (NGram) phoneticFeatureNGramLM.getNGram(fvString, 3);

                                if(ng != null)
                                    prob += ng.getProb();
//                                    prob *= ng.getProb();
                                else
                                {
                                    ng = (NGram) phoneticFeatureNGramLM.getNGram(fvString1 + "@#&" + fvString2, 2);

                                    if(ng != null)
                                        prob += Math.pow(ng.getProb(), 2.0);
//                                        prob *= Math.pow(ng.getProb(), 2.0);
                                    else
                                    {
                                        ng = (NGram) phoneticFeatureNGramLM.getNGram(fvString1, 1);

                                        if(ng != null)
                                            prob += Math.pow(ng.getProb(), 8.0);
//                                            prob *= Math.pow(ng.getProb(), 8.0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return prob;
    }

    public double getFeatureBasedSequenceProbability(String sequence)
    {
        double prob = charNGramLMImpl.getPhonemeSequenceProb(sequence, this);

//        prob = prob * Math.pow((double) charNGramLMImpl.countNGrams(1), Math.log10((double) charNGramLMImpl.countNGrams(1)))
//                * Math.pow((double) charNGramLMImpl.countNGrams(2), Math.log10((double) charNGramLMImpl.countNGrams(2)))
//                * Math.pow((double) charNGramLMImpl.countNGrams(3), Math.log10((double) charNGramLMImpl.countNGrams(3)));

//        prob = prob * Math.pow((double) charNGramLMImpl.countNGrams(3), 3.0);

        return prob;
    }

    public double getPhonemicSequenceProbability(String sequence)
    {
        NGramLM charNGramLMImplSequence = new NGramLMImpl(null, "uchar", 3, GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));

        charNGramLMImplSequence.makeNGramLM(sequence.replaceAll(" ", ""));

        double prob = charNGramLMImpl.getSentenceProb(sequence);

        return prob;
    }

    public double getFeatureBasedDistributionalSimilarity(String sequence)
    {
        InstantiatePhonemeGraph instantiatePhonemeGraphSequence = new InstantiatePhonemeGraph(langEnc, charset, false);
        NGramLM phoneticFeatureNGramLMSequence = instantiatePhonemeGraphSequence.createPhonemeUnigrams(sequence.replaceAll(" ", ""));

        double sim = NGramLMImpl.getSimilarity(phoneticFeatureNGramLM, phoneticFeatureNGramLMSequence);

        return sim;
    }

    public double getPhonemicDistributionalSimilarity(String sequence)
    {
        NGramLM charNGramLMImplSequence = new NGramLMImpl(null, "uchar", 3, GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));

        charNGramLMImplSequence.makeNGramLM(sequence.replaceAll(" ", ""));

        double sim = NGramLMImpl.getSimilarity(charNGramLMImpl, charNGramLMImplSequence);

        return sim;
    }

    public double getFeatureBasedDistributionalDistance(String sequence)
    {
        InstantiatePhonemeGraph instantiatePhonemeGraphSequence = new InstantiatePhonemeGraph(langEnc, charset, false);
        NGramLM phoneticFeatureNGramLMSequence = instantiatePhonemeGraphSequence.createPhonemeUnigrams(sequence.replaceAll(" ", ""));

        double sim = NGramLMImpl.getDistance(phoneticFeatureNGramLM, phoneticFeatureNGramLMSequence);

        return sim;
    }

    public double getPhonemicDistributionalDistance(String sequence)
    {
        NGramLM charNGramLMImplSequence = new NGramLMImpl(null, "uchar", 3, GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));

        charNGramLMImplSequence.makeNGramLM(sequence.replaceAll(" ", ""));

        double sim = NGramLMImpl.getDistance(charNGramLMImpl, charNGramLMImplSequence);

        return sim;
    }

    public static void main(String[] args) {
     
    }
}
