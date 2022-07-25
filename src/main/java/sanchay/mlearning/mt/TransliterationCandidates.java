/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.util.Iterator;
import sanchay.GlobalProperties;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.mlearning.lm.ngram.impl.NGramLMImpl;
import sanchay.properties.PropertyTokens;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.text.spell.InstantiatePhonemeGraph;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class TransliterationCandidates extends TranslationCandidates {


    public PropertyTokens getTransliterationCandidatesPTForDATM()
    {
        PropertyTokens pt = new PropertyTokens(countTranslationCandidates());

        Iterator cndItr = getTranslationCandidates();

        while(cndItr.hasNext())
        {
            String candidate = (String) cndItr.next();
            pt.addToken(srcWrd + "\t" + candidate);
        }

        return pt;
    }
    
    public void scoreTransliterationCandidates(NGramLM charGramLMImpl, boolean interim,
            SanchayEncodingConverter sanchayEncodingConverter, boolean phoneticNGram, NGramLM phoneticFeatureNGramLM, boolean fresh)
    {
        Iterator cndItr = getTranslationCandidates();

        while (cndItr.hasNext()) {
            String candidate = (String) cndItr.next();
            TranslationCandidateScores candidateScores = getTranslationCandidateScores(candidate);

            InstantiatePhonemeGraph candidatePhonemeGraph = new InstantiatePhonemeGraph(langEnc, GlobalProperties.getIntlString("UTF-8"), false);

            double s = candidateScores.getTranslationScore();

            if(fresh && phoneticNGram)
                s = 0.0;
            else if(fresh)
                s = 1.0;

//            if(s == 0.0)
//                s = 1.0;

//            s *= charGramLMImpl.getSentenceProb(UtilityFunctions.getSpaceOutString(candidate)) * candidate.length();

            if(interim)
            {
                String candidateConverted = sanchayEncodingConverter.convert(candidate.replaceAll(" ", ""));
                s *= charGramLMImpl.getSentenceProb(UtilityFunctions.getSpacedOutString(candidateConverted)) * candidateConverted.length();
            }
            else
            {
                if(phoneticNGram)
                {
//                     Vector sequences = candidatePhonemeGraph.createPhonemeSequence(candidate);
//
//                     int seqcount = sequences.size();
//
//                     for(int i = 0; i < seqcount; i++)
//                     {
//                         String seq = (String) sequences.get(i);
//
//                         double p = phoneticFeatureNGramLM.getSentenceProb(seq);
//
////                         s += p;
//
//                         if(p != 0.0)
//                            s += Math.log(p);
//                     }
//
//                     System.out.println("Total score: " + candidate + ": " + s);
//                     System.out.println("\tSequences: " + seqcount);
//
////                     s = Math.exp(s / 100000.0) * ((double) seqcount);
//                     s = Math.exp(s / 100000.0) / ((double) Math.log(seqcount));
//
//                     s = (s * Math.exp(candidate.length()));
//
////                     s = (s * candidate.length()) / (double) seqcount;
//                     System.out.println("\tNormalized score: " + s);

                    // Distributional similarity
                    NGramLM phoneticFeatureNGramLMCandidate = candidatePhonemeGraph.createPhonemeUnigrams(candidate);
                    double psim = NGramLMImpl.getSimilarity(phoneticFeatureNGramLM, phoneticFeatureNGramLMCandidate);
//                    psim = -1.0 * Math.pow(1.6, (double) candidate.length()) * Math.log(-1.0 * (1 / psim));
//                    psim = -1.0 * Math.log(-1.0 * (1 / psim)) / Math.log((double) candidate.length());
                    psim = (-1.0 * psim) / (double) candidate.length();
                    s *= psim;
                }
                else
                {
                    double sp = charGramLMImpl.getSentenceProb(UtilityFunctions.getSpacedOutString(candidate)) * candidate.length();

//                    if(sp == Double.NaN || sp == Double.POSITIVE_INFINITY || sp == Double.NEGATIVE_INFINITY)
//                        sp = 0.0;

                    s *= sp;
                }
            }

            candidateScores.setTranslationScore(s);
        }
    }

}
