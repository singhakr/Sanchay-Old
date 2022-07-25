/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.morph.segment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.properties.KeyValueProperties;
import sanchay.table.SanchayTableModel;
import sanchay.text.spell.InstantiatePhonemeGraph;

/**
 *
 * @author ram
 */
public class MorphSegmenterV2 extends MorphSegmenterV15 {

    protected InstantiatePhonemeGraph ipg;
    double morphStringsCostFeat;
    FeatureLogProb featureLogProb = null;

    /**
     * Initialize model variables
     */
    @Override
    public void initModel() {
        if (flag | true) {
            System.err.println("initModel");
        }
        //start sanchay
        long nCorpusFeatureTokens = 0;//TODO chk for possible overflow
        Vector vec = null, innerVec = null;
        ipg = new InstantiatePhonemeGraph("hin::utf8", "UTF-8", false);
        featureLogProb = new FeatureLogProb();
        //end sanchay



        //global variables!!!
        log2coeff = 1 / Math.log(2);// Coeff. for conversion to log2
        nWordTokens = 0;	    // Number of word tokens in input data
        nWordTypes = 0;	    // Number of word types in input data
        maxWordLen = 0;	    // Longest word in the input
        letterLogProb = new double[MAXCHAR];   // Probability distr. of letters as estimated from input data.
        nMorphTokens = 0;	    // Total number of occurrences of all morph tokens in the corpus.
        nMorphTypes = 0;	    // Total number of morphs in the lexicon, which equals the number of morph types in the corpus.
        logTokenSum = 0;	    // Part of the coding length of the corpus
        nVirtualMorphTypes = 0;
        /* Number of different morphs (type count),
         * either virtual (=split) morphs or
         * real (=unsplit) morphs
         */

        nLetterTokens = 0;		// Number of letters in lexicon
        corpusMorphCost = 0;   	// Code length of morph pointers in corpus
        morphStringsCost = 0;  	// Code length of spelling out the morphs in the lexicon
        lenDistrCost = 0;	    	// Cost of coding the morph lengths
        freqDistrCost = 0;	    	// Cost of coding the morph frequencies
        factorialNMorphTypes = 0;	// If free order in lexicon: Subtract this from cost

        log2hapax = Math.log(1 - hapax) / Math.log(2); // Precalculated value

        //Local Variables
        String word;
        int wordCount, wLen;
        char letter;
        int nCorpusLetterTokens = 0;	// Number of letters in input

        MorphInfoInterface tempInfo = null;
        Morph tempMorph = null;
        Enumeration e;

        while ((tempMorph = morphInfoCollection.next()) != null) {
            tempInfo = morphInfoCollection.getInfo(tempMorph);
            wordCount = tempInfo.getWordCount();
            word = tempMorph.getString();

            // Update word counts
            nWordTokens += wordCount;
            nWordTypes++;

            // Update maximum word length
            wLen = word.length();
            if (wLen > maxWordLen) {
                maxWordLen = wLen;
            }

            // Compute frequencies (i.e., counts) of each letter in the data

            //TODO what does "" mean in pearl,how does it help this??
            if (!useGammaLenDistr) {
                word = word + " "; 	//"*" // Append word break
                wLen++;
            }

            for (int i = 0; i < word.length(); i++) {
                /*
                 *The variable name letterlogprob is misleading at this
                 *stage, when it contains counts of occurrences of letters:
                 */
                //$letterlogprob{$letter} += WordCount;
                letter = word.charAt(i);
                letterLogProb[letter] += wordCount;
            }
            // Total number of letters in the corpus, including word breaks
            nCorpusLetterTokens += wordCount * wLen;

            //start sanchay
            //doin the same for featurelogprob
            if(flag |true){System.err.println(word);}
            vec = ipg.createPhonemeSequence2(word);
            if(flag |true){System.err.println(vec.size());}

            for (int i = 0; i < vec.size(); i++) {
                innerVec = (Vector) vec.get(i);
                for (int j = 0; j < innerVec.size(); j++) {
                    String tempS = (String) innerVec.get(j);
                    double tempD = featureLogProb.get(tempS);
                    featureLogProb.put(tempS, tempD + 1);
                }
            }

            nCorpusFeatureTokens += vec.size() * wLen;//TODO chk if i am right about this
        //end sanchay
        }

        //Convert letter frequencies to negative logprobs

        double logNCorpusLetterTokens = Math.log(nCorpusLetterTokens);

        for (int i = 0; i < MAXCHAR; i++) {
            if (letterLogProb[i] != 0) {
                letterLogProb[i] = logNCorpusLetterTokens - Math.log(letterLogProb[i]);
            }
        }

        //start sanchay
        //doin the same for featurelogprob
        double logNCorpusFeatureTokens = Math.log(nCorpusFeatureTokens);
        String tempFeat = null;
        Double tempD;
        while ((tempFeat = featureLogProb.next()) != null) {
            if ((tempD = featureLogProb.get(tempFeat)) != 0) {
                featureLogProb.put(tempFeat, logNCorpusFeatureTokens - Math.log(tempD));
            }
        }
        //end sanchay

        /*
         * From now on, morphInfoCollection contains the following information:
         * morphInfoCollection is a hash, whose keys are morph strings, and the
         * morphInfo for each morph
         * The word count is the number of occurrences of the word in the
         * input data (if this morph occurs as an entire word).
         * The morph count tells how many times the particular morph exists
         * in the segmentation of the input data.
         * If the split location is zero, the morph is not split, i.e., it
         * really exists as a morph in the morph set.
         * If the split location is greater than zero, the morph has been split
         * after the character indicated by the split location (through
         * recursive splitting).
         * All morphs have morph counts, even if they are split. This
         * makes it possible to trace how greater chunks have been split into
         * smaller chunks, which enables us to resplit badly split morphs.
         */

        while ((tempMorph = morphInfoCollection.next()) != null) {
            tempInfo = morphInfoCollection.getInfo(tempMorph);

            morphInfoCollection.removeMorph(tempMorph);//remove

            increaseMorphCount(tempMorph, tempInfo.getWordCount());	// Update morph count

            //tempInfo.setWordCount(wordCount);//already set
            tempInfo.setMorphCount(tempInfo.getWordCount());
            tempInfo.setSplitLocation(0);//TODO already set or not??

            morphInfoCollection.addMorph(tempMorph, tempInfo);// Update word count
        }
    }

       /**
     * Increase counts for a morph and all its submorphs
     * @param morph
     * @param deltaCount
     */
    protected void increaseMorphCount(Morph morph, int deltaCount) {
        /*if (flag) {System.err.println("increasemorphcount"); }*/

        MorphInfoInterface morphInfo, tempMorphInfo = null;
        int wordCount, morphCount, splitLocation;

        if ((morphInfo = morphInfoCollection.getInfo(morph)) != null) {
            wordCount = morphInfo.getWordCount();
            morphCount = morphInfo.getMorphCount();
            splitLocation = morphInfo.getSplitLocation();
        } else {
            wordCount = morphCount = splitLocation = 0;
        }

        int newMorphCount = morphCount + deltaCount;

        if (newMorphCount < 0) {
            //TODO throw an exception
            System.err.println("die Error ($me): Assertion failed (increasemorphcount): Negative morph count." + morph.getString() + " " + morphCount + " " + deltaCount);
            int i = 9 / 0;
            return;
        }

        if (newMorphCount == 0) {
            morphInfoCollection.removeMorph(morph);
            nVirtualMorphTypes--;
        } else {

            if (morphInfo != null) {//DONE optimization if its already there
                morphInfo.setAll(wordCount, newMorphCount, splitLocation);
            } else {
                tempMorphInfo = new MorphInfo(wordCount, newMorphCount, splitLocation);
                morphInfoCollection.addMorph(morph, tempMorphInfo);
            }

            if (morphCount == 0) {
                nVirtualMorphTypes++;
            }
        }

        if (splitLocation != 0) {
            // Recursively propagate new count to submorphs
            try {
                Morph prefix = morphInfoCollection.getMorphFromString(morph.split(0, splitLocation));
                Morph suffix = morphInfoCollection.getMorphFromString(morph.split(splitLocation));
                increaseMorphCount(prefix, deltaCount);
                increaseMorphCount(suffix, deltaCount);
            } catch (Exception e) {
                int a = 0 / 9;
            }
        } else {
            // This morph has no submorphs, i.e., it is a real morph with associative cost (coding length)

            nMorphTokens += deltaCount;

            // Decrease old count
            if (morphCount > 0) {

                double logMorphCount = Math.log(morphCount);

                // Morph pointers in corpus
                logTokenSum -= morphCount * logMorphCount;

                // Zipfian length cost
                if (useZipFFreqDistr) {
                    freqDistrCost -= -Math.log(Math.pow(morphCount, log2hapax) - Math.pow(morphCount + 1, log2hapax));
                }
            }

            // Increase new count
            if (newMorphCount > 0) {

                double lognewMorphCount = Math.log(newMorphCount);

                // Morph pointers in corpus
                logTokenSum += newMorphCount * lognewMorphCount;

                // Zipfian length cost
                if (useZipFFreqDistr) {
                    freqDistrCost +=
                            -Math.log(Math.pow(newMorphCount, log2hapax) - Math.pow(newMorphCount + 1, log2hapax));
                }

            }

            if (((morphCount == 0) && (newMorphCount > 0)) || // A morph type was
                    ((newMorphCount == 0) && (morphCount > 0))) { // added or removed
                // from the lexicon
                int sign = 0;
                if (newMorphCount == 0) {
                    sign = -1;	// Morph type removed
                } else {
                    sign = 1;	// Morph type added
                }

                nMorphTypes += sign;

                // Add or remove letters from the lexicon

                int morphLen = morph.length();

                // Encode morph length...

                /*
                if (useGammaLenDistr) {
                // Gamma length distribution
                lenDistrCost += sign*loggammapdf[$morphlen];
                }
                 */
                if (!useGammaLenDistr) {
                    // End-of-morph character used instead of
                    // explicit length distribution
                    lenDistrCost += sign * letterLogProb[' '];//"*"
                    morphLen++;
                }

                // Number of letters/characters in lexicon:
                nLetterTokens += sign * morphLen;
                // ... and add or remove the letters
                String smorph = morph.getString();
                for (int i = 0; i < smorph.length(); i++) {
                    char letter = smorph.charAt(i);
                    morphStringsCost += sign * letterLogProb[letter];
                /*if(flag) { System.err.println("g"+morphStringsCost);}*/
                }

                //start sanchay
                //doin for features
                Vector vec = ipg.createPhonemeSequence2(smorph);
                Vector innerVec = null;
                for(int i=0;i<vec.size();i++){
                    innerVec = (Vector) vec.get(i);
                    for(int j=0;j<innerVec.size();i++){
                        morphStringsCostFeat += sign * featureLogProb.get((String) innerVec.get(j));
                    }
                }
                //end sanchay

            }
        }
    }


    /**
     * Retrieve the overall logprob, or equivalently code length,
     * of the lexicon and the corpus. The negative of the logprobs
     * (which are negative or zero) are taken, i.e., all costs or
     * logprobs are non-negative in this program!
     * @return overallcost
     */
    @Override
    protected double getTotalCost(){
        return morphStringsCostFeat + super.getTotalCost();
    }

    /**
     * tester
     * @param arg
     */
    public static void main(String arg[]) {
        String dataFile = null, modelFile = null, word = null;
        boolean small = true;

        try {
            //example 1: create Model
            MorphSegmenterInterface ms1 = new MorphSegmenterV2();
            dataFile = "data/morph-segmentation/samplemodeldata.hin";
//            dataFile = "data/morph-segmentation/samplemodeldatasmallwords.hin";
            ms1.createModel(dataFile);

            Vector Morphenes = ms1.getSegments(word);

            System.out.print(word+" =");

            for (int i = 0; i < Morphenes.size(); i++)
            {
                System.out.print(" "+(String) Morphenes.get(i));
            }

  //*/
/*
            //example2 after loading model
            dataFile = "data/morphsegmentation/testdata.hin";
            modelFile = "data/morphsegmentation/samplemodel.hin";
            MorphSegmenterInterface ms2 = new MorphSegmenterV2();
            ms2.loadModel(modelFile);

            KeyValueProperties kvp2 = new KeyValueProperties();
            kvp2.read(dataFile, "UTF-8");//2 exceptions possible
            Enumeration e2 = kvp2.getPropertyKeys();
            while (e2.hasMoreElements()) {
                word = (String) e2.nextElement();
                Vector Morphenes = ms2.getSegments(word);

                System.out.print(word + " =");
                for (int i = 0; i < Morphenes.size(); i++) {
                    System.out.print(" " + (String) Morphenes.get(i));
                }
                System.out.print("\n");
            }
             //*/

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MorphSegmenterV15.class.getName()).log(Level.SEVERE, null, ex);
            System.err.print("FileNotFound");
        } catch (IOException ex) {
            Logger.getLogger(MorphSegmenterV15.class.getName()).log(Level.SEVERE, null, ex);
            System.err.print("IOException");
        }

    }
}
