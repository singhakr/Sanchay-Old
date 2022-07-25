/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.morph.segment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.properties.KeyValueProperties;
import sanchay.table.SanchayTableModel;

/**
 *
 * @author ram
 */
public class MorphSegmenterV15  implements MorphSegmenterInterface{
    final int MAXCHAR = 65535;

    protected boolean flag;
    protected boolean modelLoaded;

    //reusing code from sanchay
    protected KeyValueProperties kvp;
    /*Change in input file
     * while morfessor expected freq and word in that order
     * sanchay has word and freq.
     * we will use sanchay's version which is word and later freq
     */
    //
    protected MorphInfoCollectionInterface morphInfoCollection;
    //following variables as they are in morphessor
    protected String dataFile;	// Input file
    protected double finishThresh;/* Minimum improvement in the cost per input word type
    that an epoch must produce in order for a new
    epoch to start. During an epoch all input words
    are processed once (not guaranteed if -savememory is in use).*/

    protected int randSeed;   // Random seed
    protected int maxSkip;    //Maximum number of skips when picking next word to process (only applies to -savememory).
    protected boolean saveMemory;     // Use less memory (default = off) ,note can cause wrong output for small wordlist
    protected boolean useGammaLenDistr;   // Whether to use a Gamma pdf prior for morph lengths
    protected double mostCommonMorphlen;  //Prior for most common morph length in lexicon
    protected double beta;    // Beta value in Gamma pdf
    protected boolean useZipFFreqDistr;	// Whether to use a Zipfian pdf prior morph morph frequencies
    protected double hapax;   // Prior for proportion of morphs that only occur once in the corpus.
    protected String modelFile;   // File from which an existing model is loaded
    protected boolean trace;	// Trace progress of program
    protected double log2coeff;// Coeff. for conversion to log2
    protected int nWordTokens;    // Number of word tokens in input data
    protected int nWordTypes;	    // Number of word types in input data
    protected int maxWordLen;	    // Longest word in the input

    //TODO portability regarding range of char
    protected double[] letterLogProb;    // Probability distr. of letters as estimated from input data.
    protected int nMorphTokens;	    // Total number of occurrences of all morph tokens in the corpus.
    protected int nMorphTypes;	    // Total number of morphs in the lexicon, which equals the number of morph types in the corpus.
    protected int logTokenSum;	    // Part of the coding length of the corpus
    protected int nVirtualMorphTypes;
    /* Number of different morphs (type count),
     * either virtual (=split) morphs or
     * real (=unsplit) morphs
     */
    /**
     * List containing words sorted by random
     */
    protected Vector randomWords;
    protected int nLetterTokens;	    // Number of letters in lexicon
    protected double corpusMorphCost;   // Code length of morph pointers in corpus
    protected double morphStringsCost;  // Code length of spelling out the morphs in the lexicon
    protected double lenDistrCost;	    // Cost of coding the morph lengths
    protected double freqDistrCost;	    // Cost of coding the morph frequencies
    protected double factorialNMorphTypes;// If free order in lexicon: Subtract this from cost
    protected double log2hapax; // Precalculated value
    /**
     * holds a morph and its logprob
     */
    protected MorphAndDouble morphLogProb;
    protected double logNMorphTokens;

    /**
     */
    public MorphSegmenterV15(){
        flag = false;
        commonInit();
    }

    protected void commonInit() {
        if (flag) {
            System.err.println("commonInit");
        }

        modelLoaded = false;
        //variables as they are in morfessor

        finishThresh = 0.005;
        randSeed = 0;
        maxSkip = 8;
        saveMemory = false;
        useGammaLenDistr = true;
        mostCommonMorphlen = 7.0;
        beta = 1.0;
        useZipFFreqDistr = false;
        hapax = 0.5;
        trace = false;

        randomWords = null;

    /*
     *at this point in morfessor he was reading all the words again and displaying the result using expandMorph()
     *here we will give a similar interface using getSegment() overloaded functions
     */
    }

    public void createModel(String dataFile) throws FileNotFoundException, IOException {
        this.dataFile = dataFile;

        morphInfoCollection = new MorphInfoCollectionUsingHashV2();

        //Learn a new segmentation model from data
        kvp = new KeyValueProperties();
        kvp.read(dataFile, "UTF-8");//2 exceptions possible
        Iterator itr = kvp.getPropertyKeys();
        String smorph;
        while (itr.hasNext()) {
            smorph = (String) itr.next();
            /*
            if(flag){ System.err.println("reading:"+smorph);}
             */
            //TODO the last occurance of a particular word in the list is only considered!!
            MorphInfoInterface morphInfo = new MorphInfo(Integer.parseInt(kvp.getPropertyValue(smorph)), 0, 0);
            Morph morph = morphInfoCollection.getMorphFromString(smorph);
            morphInfoCollection.addMorph(morph, morphInfo);
        /*if(flag){System.err.println("size of morphInfoCollection is "+morphInfoCollection.size());}*/
        }
        initModel();

        processWords();

        //no need to read again
        //KeyValueProperties kvp = new KeyValueProperties();
        //kvp.read(dataFile, "UTF-8");

        Iterator itr1 = kvp.getPropertyKeys();
        String word = null;
        while (itr1.hasNext()) {
            word = (String) itr1.next();
            System.out.print(getFreq(word) + "\t");
            Vector Morphenes = expandMorph(word);

            for (int i = 0; i < Morphenes.size(); i++) {
                System.out.print("\t" + (String) Morphenes.get(i));
            }
            System.out.print("\n");
        }
    }

    public String[] getSegmentStrings(String str) {
        if (flag) {
            System.err.println("getSegmentString");
        }
        Vector morphs = getSegments(str);
        String[] segments = new String[morphs.size()];
        for (int i = 0; i < morphs.size(); i++) {
            segments[i] = ((Morph) morphs.get(i)).getString();
        }
        return segments;
    }

    public Vector getSegments(String str) {
        if (flag) {
            System.err.println("getsegments");
        }

        return viterbiSegmentWord(str);
    }

    public String[] getSegmentStrings(String string, int granularity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Vector getSegments(String string, int granularity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Initialize model variables
     */
    public void initModel() {
        if (flag | true) {
            System.err.println("initModel");
        }

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

        }

        //Convert letter frequencies to negative logprobs

        double logNCorpusLetterTokens = Math.log(nCorpusLetterTokens);

        for (int i = 0; i < MAXCHAR; i++) {
            //TODO check if it works as required
            if (letterLogProb[i] != 0) {
                letterLogProb[i] = logNCorpusLetterTokens - Math.log(letterLogProb[i]);
            }
        }

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

    /** Process the words, i.e., optimize the model until convergence
     * of the overall logprob (or code length)
     */
    protected void processWords() {
        if (flag | true) {
            System.err.println("start of processwords as of now nWordTypes = " + nWordTypes + " nWordTokens = " + nWordTokens);
        }

        double oldCost = 0;
        double newCost = getTotalCost();
        Morph word;

        do {

            for (int i = 1; i <= nWordTypes; i++) {
                word = getNextRandomWord();//TODO returns null :(
                if (flag) {
                    System.err.println("processing:" + word.getString());
                }
                reSplitNode(word);
            }

            oldCost = newCost;
            newCost = getTotalCost(); //this will be surely lesser than oldCost,bcos of reSplitNode()

            if (flag |true) {
                System.err.println("\n\nwhilecondition " + newCost + " " + (oldCost - finishThresh * nWordTypes));
            }
        } while (newCost < oldCost - finishThresh * nWordTypes);//Iterate until no substantial improvement
        resetNextRandomWord();

        if (flag | true) {
            System.err.println("end of processwords");
        }
    }

    /**
     * Split a morph recursively
     * @param morph
     */
    protected void reSplitNode(Morph morph) {
        /*if (flag) {System.err.println("resplitnode");}*/

        // Remove the morph (and its subtree):
        MorphInfoInterface morphInfo = removeMorph(morph);
        int morphCount = morphInfo.getMorphCount();
        int wordCount = morphInfo.getWordCount();

        // Put it back as an unsplit morph
        increaseMorphCount(morph, morphCount);
        double minCost = getTotalCost();	   // Compute cost
        increaseMorphCount(morph, -morphCount); // Remove morph again

        // Try other segmentations
        int splitLocation = 0;
        int i;
        double cost;
        Morph prefix, suffix;

        for (i = 1; i <= morph.length() - 1; i++) {
            prefix = morphInfoCollection.getMorphFromString(morph.split(0, i));
            suffix = morphInfoCollection.getMorphFromString(morph.split(i));

            increaseMorphCount(prefix, morphCount);  // Add two submorphs
            increaseMorphCount(suffix, morphCount);  //

            cost = getTotalCost();                // Compute cost

            increaseMorphCount(prefix, -morphCount);  // And remove the
            increaseMorphCount(suffix, -morphCount); // submorphs

            /*if(flag){System.err.println("Morph:" + morph.getString() + " Prefix:" + prefix.getString() + " Suffix:" + suffix.getString() + " Cost:" + cost + " MinCost:" + minCost);}*/

            if (cost <= minCost) {
                minCost = cost;
                splitLocation = i;
            }
        }

        // Choose the segmentation (or no segmentation) with the lowest cost

        if (splitLocation != 0) {	// Virtual morph (a split morph)
            if (morphInfoCollection.getInfo(morph) != null) {
                nVirtualMorphTypes++;
            }
            morphInfoCollection.addMorph(morph, new MorphInfo(wordCount, morphCount, splitLocation));

            // Add submorphs as real (unsplit) morphs
            prefix = morphInfoCollection.getMorphFromString(morph.split(0, splitLocation));
            suffix = morphInfoCollection.getMorphFromString(morph.split(splitLocation));
            increaseMorphCount(prefix, morphCount);
            increaseMorphCount(suffix, morphCount);

            //Recursive split of the submorphs
            reSplitNode(prefix);
            reSplitNode(suffix);
        } else {	// Real morph (no split)
            morphInfoCollection.addMorph(morph, new MorphInfo(wordCount, 0, 0));
            increaseMorphCount(morph, morphCount);
        }
    }

    /**
     * Retrieve the overall logprob, or equivalently code length,
     * of the lexicon and the corpus. The negative of the logprobs
     * (which are negative or zero) are taken, i.e., all costs or
     * logprobs are non-negative in this program!
     * @return overallcost
     */
    protected double getTotalCost() {
        /*if (flag) {
        System.err.println("gettotalcost");
        }*/

        /*
         * Logprob (or code length) of the corpus, i.e., morph pointers
         * to entries in the lexicon.
         */

        /*if(flag) {System.err.println("g" + nMorphTokens + " " + nMorphTypes);}*/

        int nTotalMorphTokens = nMorphTokens;
        int nTotalMorphTypes = nMorphTypes;

        double lognTotalMorphTokens = Math.log(nTotalMorphTokens);

        // Code length of morphs:
        corpusMorphCost = nMorphTokens * lognTotalMorphTokens - logTokenSum;
        /*if(flag) System.err.print(" "+corpusMorphCost+" ");*/

        // Free order of morphs in lexicon
        // log n! approx = n * log(n - 1)
        factorialNMorphTypes = nMorphTypes * (1 - Math.log(nMorphTypes));

        // Enumerative morph frequency distribution (if not Zipfian)
        //
        if (!useZipFFreqDistr) {
            freqDistrCost = 0;
            if (nTotalMorphTokens > 2) {
                freqDistrCost += (nTotalMorphTokens - 1) * Math.log(nTotalMorphTokens - 2);
            /*if(flag){ System.err.print(" if1 "+freqDistrCost+" ");}*/
            }
            if (nTotalMorphTypes > 2) {
                freqDistrCost -= (nTotalMorphTypes - 1) * Math.log(nTotalMorphTypes - 2);
            /*if(flag){System.err.println(" if2 " + freqDistrCost + " ");}*/
            }
            if (nTotalMorphTokens - nTotalMorphTypes > 1) {
                freqDistrCost -= (nTotalMorphTokens - nTotalMorphTypes) * Math.log(nTotalMorphTokens - nTotalMorphTypes - 1);
            /*if(flag){System.err.println(" if3 " + freqDistrCost + " ");}*/
            }
        }

        // Overall cost
        /*if(flag){System.err.println(" "+log2coeff * (corpusMorphCost + morphStringsCost + lenDistrCost + freqDistrCost + factorialNMorphTypes));}*/
        /*if(flag){System.err.println("global (1) "+log2coeff+" (2) "+corpusMorphCost +" (3) "+ morphStringsCost+" (4) "+lenDistrCost+" (5) "+freqDistrCost+" (6) "+factorialNMorphTypes);}*/

        return log2coeff * (corpusMorphCost + morphStringsCost + lenDistrCost + freqDistrCost + factorialNMorphTypes);
    }

    /**
     * returns a list of which morphs a particular morph consists of.
     * @param morph
     * @return Morphs
     */
    protected Vector expandMorph(Morph morph) {
        if (flag) {
            System.err.println("expandMorph(" + morph.getString() + ")");
        }

        Vector morphs = new Vector();
        Morph prefix, suffix;

        MorphInfoInterface morphinfo = morphInfoCollection.getInfo(morph);

        if (morphinfo.getSplitLocation() != 0) {
            prefix = morphInfoCollection.getMorphFromString(morph.split(0, morphinfo.getSplitLocation()));
            suffix = morphInfoCollection.getMorphFromString(morph.split(morphinfo.getSplitLocation()));

            //TODO efficiency
            Vector temp = expandMorph(prefix);
            while (!temp.isEmpty()) {
                morphs.add(temp.firstElement());
                temp.removeElementAt(0);
            }
            temp = expandMorph(suffix);
            while (!temp.isEmpty()) {
                morphs.add(temp.firstElement());
                temp.removeElementAt(0);
            }
        } else {
            morphs.add(morph.getString());
        }

        return morphs;
    }

    protected Vector expandMorph(String str) {
        Morph morph = new Morph(str);
        return expandMorph(morph);
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
            }
        }
    }

    protected MorphInfoInterface removeMorph(Morph morph) {
        /*if (flag) {
        System.err.println("removeMorph(" + morph.getString() + ")");
        }*/
        MorphInfoInterface morphInfo = null;
        if (morphInfoCollection.getInfo(morph) == null) {//cannot remove morph if its not there!!
            //TODO throw an exception
            System.err.println("die Error ($me): Assertion failed (removemorph).\n");
            int a = 9 / 0;
        } else {
            morphInfo = morphInfoCollection.getInfo(morph);
            /*if(flag){System.err.println("morph: " + morph.getString() + " SplitLoct: " + morphInfo.getSplitLocation());}*/
            increaseMorphCount(morph, -(morphInfo.getMorphCount()));//this statement removes the morph

        }
        return morphInfo;
    }

    protected Morph getNextRandomWord() {
        /*    if (flag) {
        System.err.println("getNextRandomWord");
        }
         */
        int i, j, wordCount;
        Morph morph = null;
        MorphInfoInterface morphInfo = null;

        if (saveMemory) {
            /*
             * Save memory. Pick words from the %morphinfo hash by random.
             *
             * First, skip a random number of items, so that the next word is not
             * deterministically chosen.
             *
             * Note that %morphinfo IS modified between calls to 'each', which
             * means that the same item may show up many times before the hash
             * is consumed. But that should not be a problem for us.
             */
            double skip = (int) ((Math.random() * maxSkip));//will give us a random number in [0,8)
            /*if(true){System.err.println(maxSkip+" "+skip);}*/
            for (i = 1; i <= skip && ((morph = morphInfoCollection.next()) != null); i++) {
                morphInfoCollection.next();
            }

            // Then pick the next item that is really a word

            wordCount = 0;
            while (wordCount == 0) {
                if ((morph = morphInfoCollection.next()) != null) {
                    if ((morphInfo = morphInfoCollection.getInfo(morph)) != null) {
                        wordCount = morphInfo.getWordCount();
                    }
                }
                if (flag) {
                    System.err.println("while");
                }
            }

            return morph;
        }

        // Else don't save memory. This means that we can copy up the words
        // in the %morphinfo hash into a randomly sorted wordlist.

        if (randomWords == null) {
            randomWords = new Vector();
        }

        if (randomWords.isEmpty()) {
            // randomWords was empty. Generate a new wordlist by random.
            // First collect the words into a list:

            while ((morph = morphInfoCollection.next()) != null) {
                morphInfo = morphInfoCollection.getInfo(morph);
                wordCount = morphInfo.getWordCount();
                if (wordCount != 0) {
                    // Only accept words, not morphs
                    randomWords.add(morph);
                } else if (flag) {
                    System.err.println("\nelif morph " + morph.getString() + " wordCount = " + wordCount + " morphCount " + morphInfo.getMorphCount() + " splitLocation " + morphInfo.getSplitLocation());
                }
            }

            /*if(flag){System.err.println("\nb4 random");
            for (i = randomWords.size() - 1; i >= 0; i--) {
            System.err.println("getnext:" + ((Morph) randomWords.elementAt(i)).getString());
            }}*/

            // Sort the words into random order
            for (i = randomWords.size() - 1; i >= 0; i--) {
                // $j = Random value [0, 1, .., $i]
                j = (int) (Math.random() * (i + 1));
                j = j % (i + 1);//$j = sprintf("%d", rand($i + 1));
                Object tmp = randomWords.elementAt(i);
                randomWords.set(i, randomWords.elementAt(j));
                randomWords.set(j, tmp);
            }

        /*if(flag){System.err.println("\nafter random");
        for (i = randomWords.size() - 1; i >= 0; i--) {
        System.err.println("getnext:" + ((Morph) randomWords.elementAt(i)).getString());
        }}*/

        /*if(flag){System.err.print("\nrandomised\n\n");}*/
        }

        // Return the next word in the word list sorted by random
        //return shift @randomWords;
        int morphCollSize = morphInfoCollection.size();

        if (flag) {
            System.err.println("size of randword is " + randomWords.size() + " size of morphColl is " + morphCollSize);
        }

        Morph firstElement = null;
        if (randomWords.size() != 0) {
            firstElement = (Morph) randomWords.firstElement();
            randomWords.removeElementAt(0);
        }
        /*if (flag){System.err.println(" returning "+firstElement.getString());}*/


        return firstElement;
    }

    protected void resetNextRandomWord() {
        if (flag) {
            System.err.println("resetnextrandomword");
        }
        if (saveMemory) {
            // Reset the pointer to the first item in %morphinfo
            morphInfoCollection.resetNext();
        } else {
            // Clear randomly sorted word list
            if (randomWords != null) {//TODO chk dis
                randomWords.clear();
                randomWords = null;//TODO is this necessary?
            }
        }
    }

    public void loadModel(String modelFile) throws FileNotFoundException, IOException {
        this.modelFile = modelFile;

        if (flag) {
            System.err.println("loadModel");
        }

        double count;
        String word = null;
        Vector morphs = new Vector();
        Morph morph = new Morph();  // Local variables
        String s = null;

        morphLogProb = new MorphAndDouble();	  // Global variable
        nMorphTokens = 0;	  // -"-
        nMorphTypes = 0;     // -"-
        logNMorphTokens = 0; // -"-

        SanchayTableModel stm = new SanchayTableModel(modelFile, "UTF-8");
        int nRow = stm.getRowCount();
        if (flag && false) {
            System.err.println("Row Count::" + stm.getRowCount());
            stm.print(System.err);
        }
        for (int i = 0; i < nRow; i++) {
            if (flag && false) {
                System.err.print("i: " + i);
            }
            // SegmRowented word preceded by a word count
            s = (String) stm.getValueAt(i, 0);
            count = Integer.parseInt(s);
            for (int j = 0;; j++) {
                if (flag && false) {
                    System.err.print(" j:" + j);
                }
                s = (String) stm.getValueAt(i, j);
                if (s == null) {
                    break;
                }
                morph.fromString(s);//TODO assumes that string is unique so ,i am not using new Morph() every time

                /*
                 *Count the number of occurrences of every morph type
                 *The variable name for morphLogProb is misleading at
                 *this stage. It actually contains morph counts:
                 */
                morphLogProb.put(morph, morphLogProb.get(morph) + count);//put will automatically add if necessary
                nMorphTokens += count;
            }
            if (flag && false) {
                System.err.print("\n");
            }
        }
        // Precompute morph logprobs, i.e., convert counts to negative logprobs

        logNMorphTokens = Math.log(nMorphTokens);
        while ((morph = morphLogProb.next()) != null) {
            count = morphLogProb.get(morph);
            morphLogProb.put(morph, logNMorphTokens - Math.log(count));
            nMorphTypes++;
        }
    }

    protected int getFreq(String str) {
        if (flag) {
            System.err.println("getFreq");
        }
        Morph tempMorph = new Morph(str);
        return morphInfoCollection.getInfo(tempMorph).getWordCount();
    }

    protected Vector viterbiSegmentWord(String sWord) {
        Vector morphSeq = new Vector();

        if (flag) {
            System.err.println("viterbiSegmentWord(" + sWord + ")");
        }

        Morph word = new Morph(sWord);

        if(word == null || word.getString() == null)
            return morphSeq;

        int T = word.getString().length();//TODO chk

        double badLikelihood = (T + 1) * logNMorphTokens;
        double pseudoInfiniteCost = (T + 1) * badLikelihood;
        int t, l;
        double logp = 0;
        Morph morph = new Morph();
        Vector delta = new Vector();
        Vector psi = new Vector();
        double bestDelta;
        int bestL;
        double currDelta;
        Double d;

        // Viterbi segmentation
        delta.add(0.0);
        psi.add(0.0);
        for (t = 1; t <= T; t++) {
            bestDelta = pseudoInfiniteCost;
            bestL = 0;

            for (l = 1; l <= t; l++) {
                if (flag) {
                    System.err.print(" substr:(" + (t - l) + "," + t + ") =" + word.getString().substring(t - l, t) + " ");
                }
                //         substr EXPR OFFSET LENGHT
                //$morph = substr($word, $t - $l, $l);
                morph = new Morph(word.getString().substring(t - l, t));

                if (morphLogProb.containsKey(morph)) {
                    d = morphLogProb.get(morph);
                    if (flag) {
                        System.err.print("if");
                    }
                    logp = d;
                } else if (l == 1) {
                    // The morph was not defined but it was only one letter long.
                    // Accept it with a bad likelihood.
                    if (flag) {
                        System.err.print("else if");
                    }
                    logp = badLikelihood;
                } else {
                    // The morph was not defined: Don't accept!
                    //TODO chk next L_LOOP;
                    if (flag) {
                        System.err.print("else\n");
                    }
                    continue;
                }
                currDelta = (Double) delta.get(t - l) + logp;
                if (currDelta < bestDelta) {
                    if (flag) {
                        System.err.print(" updating bestL from " + bestL + " to " + l + " bestDelta from " + bestDelta + " to " + currDelta);
                    }
                    bestL = l;
                    bestDelta = currDelta;
                }
                if (flag) {
                    System.err.print("\n");
                }
            }
            delta.add(new Double(bestDelta));//delta[t] = bestDelta
            psi.add(new Integer(bestL));//psi[t] = bestL
        }

        // Trace back
        t = T;
        while (psi.get(t) != null && t != 0) {
            if (flag) {
                System.err.print("t:" + t);
            }
            //unshift @morphseq, ($word, t - psi[t], psi[t]);
            int start = t - (Integer) psi.get(t);
            int end = t;
            if (start >= 0 && start <= end) {
                if (flag) {
                    System.err.print("*" + word.getString().substring(start, end));
                }
                String s = word.getString().substring(start, end);
                morphSeq.add(0, s);
            }
            if (flag) {
                System.err.print("\n");
            }
            t -= (Integer) psi.get(t);
        }
        return morphSeq;
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
             MorphSegmenterInterface ms1 = new MorphSegmenterV15();
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
        MorphSegmenterInterface ms2 = new MorphSegmenterV15();
        ms2.loadModel(modelFile);

        KeyValueProperties kvp2 = new KeyValueProperties();
        kvp2.read(dataFile, "UTF-8");//2 exceptions possible
        Enumeration e2 = kvp2.getPropertyKeys();
        while (e2.hasMoreElements()) {
        word = (String) e2.nextElement();
        Vector Morphenes = ms2.getSegments(word);

        System.out.print(word+" =");
        for (int i = 0; i < Morphenes.size(); i++) {
        System.out.print(" "+(String) Morphenes.get(i));
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


