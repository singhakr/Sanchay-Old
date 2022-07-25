/*
 * PhoneticModelOfScripts.java
 *
 * Created on March 30, 2006, 8:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package sanchay.text.spell;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.common.types.*;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.speech.common.*;
import sanchay.speech.decoder.isolated.*;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.MultiKeyValueProperties;
import sanchay.properties.PropertiesManager;
import sanchay.properties.PropertiesTable;
import sanchay.properties.PropertyTokens;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class PhoneticModelOfScripts {

    protected String language;
    protected PropertiesManager propman;
    protected PropertyTokens featureList;
    protected Hashtable featureStructures;
    protected Hashtable langEncfeatureStructures;
    protected Hashtable featureVectors;
    protected Hashtable langEncFeatureVectors;
    protected Vector wordList;
    
    private NGramLM ngramLM;
    
    public static final double INFINITE = 99999999.0;

    // The distance constants for different levels of phonetic matching
    public static final double LOWEST = 1.0;

    // Weights
    // For vowels
    public static final double VLEVEL6 = 0.5; // diphthong
    public static final double VLEVEL5 = 0.75; // length
    public static final double VLEVEL4 = 1.0; // svar1 and svar2
    public static final double VLEVEL3 = 1.25; // maatraa
    public static final double VLEVEL2 = 1.5; // height

    // For consonants
    public static final double CLEVEL7 = 2.0; // devanagari or dravidian or bangla
    public static final double CLEVEL6 = 2.0; // hard
    public static final double CLEVEL5 = 3.0; // aspirated
    public static final double CLEVEL4 = 4.0; // voiced
    public static final double CLEVEL3 = 5.0; // prayatna
    public static final double CLEVEL2 = 6.0; // sthaan
    public static final double HIGHEST = 15.0; // type: level1
    protected PrintStream logPS;

    /** Creates a new instance of PhoneticModelOfScripts */
    public PhoneticModelOfScripts(String propFile, String cs, String lang, Vector wrdList)
            throws FileNotFoundException, IOException {
        language = lang;

        wordList = wrdList;

        propman = new PropertiesManager(propFile, cs);
        prepareFeatureVectors();

        try {
            logPS = new PrintStream(GlobalProperties.getHomeDirectory() + "/" + "tmp/spell-checker-log.txt", GlobalProperties.getIntlString("UTF-8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    public PhoneticModelOfScripts(String propFile, String cs, String lang)
            throws FileNotFoundException, IOException {
        language = lang;
        propman = new PropertiesManager(propFile, cs);
        prepareFeatureVectors();

        try {
            logPS = new PrintStream(GlobalProperties.getHomeDirectory() + "/" + "tmp/spell-checker-log.txt", GlobalProperties.getIntlString("UTF-8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    public PhoneticModelOfScripts(PropertiesManager pm, String lang) {
        language = lang;
        propman = pm;
        prepareFeatureVectors();


        try {
            logPS = new PrintStream(GlobalProperties.getHomeDirectory() + "/" + "tmp/spell-checker-log.txt", GlobalProperties.getIntlString("UTF-8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return the ngramLM
     */
    public NGramLM getNgramLM() {
        return ngramLM;
    }

    /**
     * @param ngramLM the ngramLM to set
     */
    public void setNgramLM(NGramLM ngramLM) {
        this.ngramLM = ngramLM;
    }

    public PropertiesManager getPropertiesManager() {
        return propman;
    }

    public PropertyTokens getFeatureList() {
        return featureList;
    }

    protected void prepareFeatureVectors() {
        featureList = (PropertyTokens) propman.getPropertyContainer(GlobalProperties.getIntlString("feature-list"), PropertyType.PROPERTY_TOKENS);

        KeyValueProperties iscPhoneticFeatures = (KeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("isc-phonetic-features"), PropertyType.KEY_VALUE_PROPERTIES);

        MultiKeyValueProperties phoneticFeatureCodes = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-codes"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);
        MultiKeyValueProperties phoneticFeatureValues = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-values"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);

        KeyValueProperties phoneticFeatureWeights = (KeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-weights"), PropertyType.KEY_VALUE_PROPERTIES);

        Iterator enm = iscPhoneticFeatures.getPropertyKeys();

        featureStructures = new Hashtable(iscPhoneticFeatures.countProperties());

        // Preparing feature structures
        while (enm.hasNext()) {
            String iscCode = (String) enm.next();
            String phoneticFeatures = (String) iscPhoneticFeatures.getPropertyValue(iscCode);

            FeatureStructure fs = new FeatureStructureImpl();
            try {
                fs.readString(phoneticFeatures);
                featureStructures.put(iscCode, fs);
//		fs.print(System.out);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Preparing feature vectors
        featureVectors = new Hashtable(iscPhoneticFeatures.countProperties());
        langEncFeatureVectors = new Hashtable(iscPhoneticFeatures.countProperties());
        langEncfeatureStructures = new Hashtable(iscPhoneticFeatures.countProperties());

        enm = iscPhoneticFeatures.getPropertyKeys();

        while (enm.hasNext()) {
            String iscCode = (String) enm.next();
            FeatureStructure fs = (FeatureStructure) featureStructures.get(iscCode);

            int fcount = featureList.countTokens();

            Double featureVector[] = new Double[fcount];

            for (int i = 0; i < fcount; i++) {
                String feature = featureList.getToken(i);
                FeatureAttribute attrib = fs.getAttribute(feature);

                if (attrib == null) {
                    featureVector[i] = new Double(3.0);
                } else {
                    String fval = (String) attrib.getAltValue(0).getValue();

                    LinkedHashMap fcodes = phoneticFeatureCodes.getMultiPropertiesMap();
                    KeyValueProperties ckvp = (KeyValueProperties) fcodes.get(feature);

                    String fcode = ckvp.getPropertyValue(fval);

                    LinkedHashMap fvalues = phoneticFeatureValues.getMultiPropertiesMap();
                    KeyValueProperties vkvp = (KeyValueProperties) fvalues.get(feature);

                    String fv = vkvp.getPropertyValue(fcode);
                    String fweight = phoneticFeatureWeights.getPropertyValue(feature);

                    featureVector[i] = new Double(Double.parseDouble(fv) * Double.parseDouble(fweight));
                }
            }

            featureVectors.put(iscCode, featureVector);
        }

        translateFVToLangEnc();
    }

    protected void translateFVToLangEnc() {
        PropertiesTable iscUTF8Map = (PropertiesTable) propman.getPropertyContainer(GlobalProperties.getIntlString("isc-utf8-map"), PropertyType.PROPERTY_TABLE);

        int rcount = iscUTF8Map.getRowCount();

        for (int i = 0; i < rcount; i++) {
            String iscCode = (String) iscUTF8Map.getValueAt(i, 0);

            if (language.equalsIgnoreCase(GlobalProperties.getIntlString("ISCII"))) {
                langEncFeatureVectors.put(new Character(iscCode.charAt(0)), featureVectors.get(iscCode));
                langEncfeatureStructures.put(new Character(iscCode.charAt(0)), featureStructures.get(iscCode));
            } else {
                String langEncStr = (String) iscUTF8Map.getValue(0, iscCode, language);

//		System.out.println(iscCode + " " + langEncStr);

                if (langEncStr != null && langEncStr.equals("") == false) {
                    langEncFeatureVectors.put(new Character(langEncStr.charAt(0)), featureVectors.get(iscCode));
                    langEncfeatureStructures.put(new Character(langEncStr.charAt(0)), featureStructures.get(iscCode));
                }
            }
        }
    }

    protected void loadWordList() throws FileNotFoundException, IOException {
        PropertiesTable wordLists = (PropertiesTable) propman.getPropertyContainer(GlobalProperties.getIntlString("word-lists"), PropertyType.PROPERTY_TABLE);

        String wordListFile = (String) wordLists.getValue(0, language, GlobalProperties.getIntlString("word-list"));
        String wordListCharset = (String) wordLists.getValue(0, language, GlobalProperties.getIntlString("Charset"));

        wordList = (new PropertyTokens(wordListFile, wordListCharset)).getCopyOfTokens();
    }

    public Vector getWordList() {
        if (wordList != null) {
            return wordList;
        } else {
            try {
                loadWordList();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return wordList;
    }

    public int countWords() {
        return getWordList().size();
    }

    public String getWord(int ind) {
        Object obj = wordList.get(ind);

        if (obj instanceof NGram) {
            return ((NGram) obj).getString(ngramLM);
        }

        String wrd = (String) wordList.get(ind);

        String parts[] = wrd.split("[\t]");

        return parts[0];
    }

    public Double[] getFeatureVector(char c) {
        return (Double[]) langEncFeatureVectors.get(new Character(c));
    }

    public FeatureStructure getFeatureStructureForWordStart()
    {
        FeatureStructure fs = new FeatureStructureImpl();

        PropertyTokens featureList = getFeatureList();

        int count = featureList.countTokens();

        for (int i = 0; i < count; i++)
        {
            String feature = featureList.getToken(i);

            fs.addAttribute(feature, "START");
        }

        return fs;
    }

    public FeatureStructure getFeatureStructureForWordEnd()
    {
        FeatureStructure fs = new FeatureStructureImpl();

        PropertyTokens featureList = getFeatureList();

        int count = featureList.countTokens();

        for (int i = 0; i < count; i++)
        {
            String feature = featureList.getToken(i);

            fs.addAttribute(feature, "END");
        }

        return fs;
    }

    public FeatureStructure getFeatureStructure(char c, String langEnc) {
        if (langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("ISCII"))) {
            return (FeatureStructure) featureStructures.get(new Character(c));
        }

        return (FeatureStructure) langEncfeatureStructures.get(new Character(c));
    }

    public double getSurfaceSimilarity(String wrd1, String wrd2) {
        IsolatedRecog isolatedRecog = new IsolatedRecog();

        isolatedRecog.addModel(getTrellisString(wrd2));

        isolatedRecog.setData(getTrellisString(wrd1));

        isolatedRecog.alignAll();

        IsoTrellisPath bestPaths[] = isolatedRecog.bestAlignments(1);

        if(bestPaths == null)
            return Double.MAX_VALUE;

        return bestPaths[0].getCost();
    }

    public double getSymmetricScaledSurfaceSimilarity(String wrd1, String wrd2)
    {
        int avgLength = (wrd1.length() + wrd1.length()) / 2;

        double avgLengthD = ((double) avgLength) == 0.0 ? 1.0 : ((double) avgLength);

        double fssim1 = getSurfaceSimilarity(wrd1, wrd2);
        double fssim2 = getSurfaceSimilarity(wrd2, wrd1);

        String rwrd1 = UtilityFunctions.reverseString(wrd1);
        String rwrd2 = UtilityFunctions.reverseString(wrd2);

        double rssim1 = getSurfaceSimilarity(rwrd1, rwrd2);
        double rssim2 = getSurfaceSimilarity(rwrd2, rwrd1);

        return (fssim1 + fssim2 + rssim1 + rssim2) / (4 * avgLengthD);
    }

    public String[] getNearestWordsSequential(String wrd, int nearest, PrintStream ps) {
        IsolatedRecog isolatedRecog = new IsolatedRecog();

        int wcount = countWords();
        int lengthTolerance = 4;

        for (int k = 0; k < wcount; k++) {
            String mwrd = getWord(k);

            if (mwrd.length() > wrd.length() - lengthTolerance && mwrd.length() < wrd.length() + lengthTolerance)
                isolatedRecog.addModel(getTrellisString(mwrd));
        }

        isolatedRecog.setData(getTrellisString(wrd));

        isolatedRecog.alignAll();

        int moreNearest = 7 * nearest;
        IsoTrellisPath bestPaths[] = isolatedRecog.bestAlignments(moreNearest);

        String bestWrds[] = new String[nearest];

        int j = 0;
        boolean printIt = true;

        for (int i = 0; i < moreNearest && j < nearest; i++) {
            String bestWrd = getWord(bestPaths[i].getModelIndex());

            if (bestWrd.length() <= 0) {
                continue;
            }

//	    System.out.println("***" + bestWrd + "***");

//	    if( (bestWrd.charAt(0) == wrd.charAt(0) || bestWrd.charAt(bestWrd.length() - 1) == wrd.charAt(wrd.length() - 1))
//            if (bestWrd.length() > wrd.length() - lengthTolerance && bestWrd.length() < wrd.length() + lengthTolerance)
//            {
                if (j == 0) {
                    if (bestPaths[i].getCost() >= 0.0) {
                        printIt = true;
                        ps.println(wrd + ":");
                    } else {
                        printIt = false;
                    }
                }

                printIt = true;
                bestWrds[j] = getWord(bestPaths[i].getModelIndex());

                if (printIt) {
                    ps.println("\t" + bestWrds[j] + "\t" + bestPaths[i].getCost());
                }

                j++;
//            }
        }

        return bestWrds;
    }

    public TrellisString getTrellisString(String wrd) {
        TrellisString trellisString = new TrellisString();

        for (int i = 0; i < wrd.length(); i++) {
            char c = wrd.charAt(i);
            Double[] fv = getFeatureVector(c);

            Feature f = new Feature();

//	    f.setFeatures(fv);

            f.setFeatures(new PhoneticCharacter(new Character(c), this, logPS));

            StringNode snode = new StringNode(f);
            snode.setIndex(i);
            trellisString.addNode(snode);
        }

        return trellisString;
    }

    public void matchWords(String inFile, String ics, String outFile, String ocs) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;

        if (ics != null && ics.equals("") == false) {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ics));
        } else {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
        }

        String line = "";
        PrintStream ps = new PrintStream(outFile, ocs);
        int nearest = 5;

        while ((line = inReader.readLine()) != null) {
            if (line.equals("") == false) {
                String parts[] = line.split("[\t]");
                System.out.println("WORD - " + parts[0]); //
//                ps.print(parts[0] + " ");
                String bestWrds[] = getNearestWordsSequential(parts[0], nearest, ps);
                
//                for (int i=0;i<nearest;i++)
//                {
//                    System.out.println(bestWrds[i]);
//                }
            }
        }
    }

    /** Distance based on equality of character, if equal, otherwise on phonetic features with different priorities:<br>
     * 1. type<br>
     * <br>
     * If vowel:<br>
     * 2. height<br>
     * 3. maatraa<br>
     * 4. svar1 and (or?) svar2<br>
     * 5. length<br>
     * 6. diphthong<br>
     * <br>
     * If consonant:<br>
     * 2. sthaan<br>
     * 3. prayatna<br>
     * 4. voiced<br>
     * 5. aspirated<br>
     * 6. hard<br>
     * 7. devanagari or dravidian or bangla<br>
     * <br>
     * If two character match exactly, the distance is 0. If not, compare at the
     * above mentioned phonetic levels and return the distance accordingly. Start
     * from the HIGHEST and as phonetic features match, the distance will decrease.<br>
     */
    public double getDistance(Character c1, Character c2) {
        double distance = 0.0;

        MultiKeyValueProperties phoneticFeatureCodes = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-codes"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);
        MultiKeyValueProperties phoneticFeatureValues = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-values"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);

        FeatureStructure fs1 = (FeatureStructure) langEncfeatureStructures.get(c1);
        FeatureStructure fs2 = (FeatureStructure) langEncfeatureStructures.get(c2);

        if (c1.equals(c2)) {
            return distance;
        }

        if ((fs1 == null && fs2 != null) || (fs1 != null && fs2 == null)) {
            return HIGHEST;
        }

        // We don't know what to do
        if (fs1 == null && fs2 == null) {
            return (HIGHEST - LOWEST) / 2;
        }

        // Level-1
        // If there is an FS, there has to be a 'type'
        String feature = GlobalProperties.getIntlString("type");
        distance = getDistanceHelper(feature, fs1, fs2, true, HIGHEST);

        if (distance > 0.0) {
            return HIGHEST;
        }

        // Now both must be non-null and have the same value for type (after Level-1)
        FeatureAttribute attrib1 = fs1.getAttribute(feature);
        FeatureAttribute attrib2 = fs2.getAttribute(feature);

        String fval1 = (String) attrib1.getAltValue(0).getValue();
        String fval2 = (String) attrib2.getAltValue(0).getValue();

        double tmpDistance = 0.0;

        // If vowel
        if (fval1.equals(GlobalProperties.getIntlString("v"))) {
            // Level-2
            feature = GlobalProperties.getIntlString("height");
            distance += getDistanceHelper(feature, fs1, fs2, false, VLEVEL2);

            // Level-3
            feature = GlobalProperties.getIntlString("maatraa");
            distance += getDistanceHelper(feature, fs1, fs2, true, VLEVEL3);

            // Level-4
            feature = GlobalProperties.getIntlString("svar1");
            tmpDistance = getDistanceHelper(feature, fs1, fs2, false, VLEVEL4);

            feature = GlobalProperties.getIntlString("svar2");
            tmpDistance += getDistanceHelper(feature, fs1, fs2, false, VLEVEL4);

            distance += tmpDistance / 2;

            // Level-5
            feature = GlobalProperties.getIntlString("length");
            distance += getDistanceHelper(feature, fs1, fs2, false, VLEVEL5);

            // Level-6
            feature = GlobalProperties.getIntlString("diphthong");
            distance += getDistanceHelper(feature, fs1, fs2, true, VLEVEL4);

            // Level-7
            feature = GlobalProperties.getIntlString("diphthong");
            distance += getDistanceHelper(feature, fs1, fs2, true, VLEVEL4);
        } // If consonant
        else if (fval1.equals(GlobalProperties.getIntlString("c"))) {
            // Level-2
            feature = GlobalProperties.getIntlString("sthaan");
            distance += getDistanceHelper(feature, fs1, fs2, false, CLEVEL2);

            // Level-3
            feature = GlobalProperties.getIntlString("prayatna");
            distance += getDistanceHelper(feature, fs1, fs2, false, CLEVEL3);

            // Level-4
            feature = GlobalProperties.getIntlString("voiced");
            distance += getDistanceHelper(feature, fs1, fs2, true, CLEVEL4);

            // Level-5
            feature = GlobalProperties.getIntlString("aspirated");
            distance += getDistanceHelper(feature, fs1, fs2, true, CLEVEL5);

            // Level-6
            feature = GlobalProperties.getIntlString("hard");
            distance += getDistanceHelper(feature, fs1, fs2, true, CLEVEL6);

            // Level-7
            feature = GlobalProperties.getIntlString("devanagari");
            distance += getDistanceHelper(feature, fs1, fs2, true, CLEVEL7);

            feature = GlobalProperties.getIntlString("dravidian");
            distance += getDistanceHelper(feature, fs1, fs2, true, CLEVEL7);

            feature = GlobalProperties.getIntlString("bangla");
            distance += getDistanceHelper(feature, fs1, fs2, true, CLEVEL7);
        }
        // if fs1.

        // start transform operations
        feature = GlobalProperties.getIntlString("id");
        distance = distance * getTranformCost(feature, fs1, fs2, 0.30);

        feature = GlobalProperties.getIntlString("transform");
        distance = distance * getTranformCost(feature, fs1, fs2, 0.30);


        // For Punjabi only
        if (fval1.equals(GlobalProperties.getIntlString("h"))) {
            distance = .5;
        }

        return distance;
    }

    protected double getTranformCost(String feature, FeatureStructure fs1, FeatureStructure fs2, double multiplyFactor) {
        FeatureAttribute attrib1 = fs1.getAttribute(feature);
        FeatureAttribute attrib2 = fs2.getAttribute(feature);

        if ((attrib1 == null && attrib2 != null) || (attrib1 != null && attrib2 == null)) {
            return 1;
        }

        // We don't know what to do
        if (attrib1 == null && attrib2 == null) {
            return 1;
        }

        MultiKeyValueProperties phoneticFeatureCodes = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-codes"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);
        MultiKeyValueProperties phoneticFeatureValues = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-values"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);

        String fval1 = (String) attrib1.getAltValue(0).getValue();
        String fval2 = (String) attrib2.getAltValue(0).getValue();

        if (fval1.equals(fval2) == true) {
            return (multiplyFactor);
        }


        return 1;
    }

    // To calculate the distance for one feature
    protected double getDistanceHelper(String feature, FeatureStructure fs1, FeatureStructure fs2,
            boolean boolType, double distanceWeight) {
        FeatureAttribute attrib1 = fs1.getAttribute(feature);
        FeatureAttribute attrib2 = fs2.getAttribute(feature);

        if ((attrib1 == null && attrib2 != null) || (attrib1 != null && attrib2 == null)) {
            return distanceWeight * LOWEST;
        }

        // We don't know what to do
        if (attrib1 == null && attrib2 == null) {
            return (distanceWeight * LOWEST) / 2;
        }

        MultiKeyValueProperties phoneticFeatureCodes = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-codes"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);
        MultiKeyValueProperties phoneticFeatureValues = (MultiKeyValueProperties) propman.getPropertyContainer(GlobalProperties.getIntlString("phonetic-feature-values"), PropertyType.MULTI_KEY_VALUE_PROPERTIES);

        String fval1 = (String) attrib1.getAltValue(0).getValue();
        String fval2 = (String) attrib2.getAltValue(0).getValue();

        LinkedHashMap fcodes = phoneticFeatureCodes.getMultiPropertiesMap();
        KeyValueProperties ckvp = (KeyValueProperties) fcodes.get(feature);

        String fcode1 = ckvp.getPropertyValue(fval1);
        String fcode2 = ckvp.getPropertyValue(fval2);

        LinkedHashMap fvalues = phoneticFeatureValues.getMultiPropertiesMap();
        KeyValueProperties vkvp = (KeyValueProperties) fvalues.get(feature);

        String fv1 = vkvp.getPropertyValue(fcode1);
        String fv2 = vkvp.getPropertyValue(fcode2);

        if (boolType) {
            if (fval1.equals(fval2) == false) {
                return distanceWeight * LOWEST;
            }
        } else {
            if (fval1.equals(fval2) == false) {
                return (distanceWeight * Math.abs(Double.parseDouble(fv1) - Double.parseDouble(fv2)));
            }
        }

        return 0.0;
    }

    public Vector<String> getFeatureValueStrings(char c, String langEnc)
    {
        Vector<String> fvStrings = new Vector<String>();

        FeatureStructure fs = getFeatureStructure(c, langEnc);

        if(fs != null)
        {
            int acount = fs.countAttributes();

            for (int i = 0; i < acount; i++)
            {
                FeatureAttribute fa = fs.getAttribute(i);
                FeatureValue fv = fa.getAltValue(0);

                String valStr = (String) fv.getValue();

                fvStrings.add(fa.getName() + "=" + valStr);
            }
        }

        return fvStrings;
    }

    public static boolean areFeaturesCompatible(String fvString1, String fvString2)
    {
        fvString1 = fvString1.replaceAll("<", "");
        fvString1 = fvString1.replaceAll(">", "");

        fvString2 = fvString2.replaceAll("<", "");
        fvString2 = fvString2.replaceAll(">", "");

        String parts1[] = fvString1.split("=");
        String parts2[] = fvString2.split("=");

        if(parts1 == null || parts2 == null ||  parts1.length != 2 || parts2.length != 2)
            return false;

        if(parts1[0].equalsIgnoreCase(parts2[0]))
            return true;

        return false;
    }

    public static boolean areFeaturesEquivalent(String fvString1, String fvString2)
    {
        fvString1 = fvString1.replaceAll("<", "");
        fvString1 = fvString1.replaceAll(">", "");

        fvString2 = fvString2.replaceAll("<", "");
        fvString2 = fvString2.replaceAll(">", "");

        String parts1[] = fvString1.split("=");
        String parts2[] = fvString2.split("=");

        if(parts1 == null || parts2 == null ||  parts1.length != 2 || parts2.length != 2)
            return false;

        if(parts1[0].equalsIgnoreCase(parts2[0]) && parts1[1].equalsIgnoreCase(parts2[1]))
            return true;

        return false;
    }

    public static void main(String[] args) {
        try {
            PhoneticModelOfScripts cpf = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
                    GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
//	    cpf.getPropertiesManager().print(new PrintStream("/home/anil/tmp/propman.txt", "UTF-8"));
            //cpf.

//            cpf.matchWords("tmp/hindi-word-list-test.txt", "UTF-8", "tmp/spell-checker-out-test.txt", "UTF-8");
//	    Character a1 = new Character('à¤¬');
//	    Character a2 = new Character('à¤µ');
//	    System.out.println (cpf.getDistance( a1 , a2 ));

            String wrd1 = "राजस्थान";
            String wrd2 = "राजिस्थान";

            double ssim = cpf.getSurfaceSimilarity(wrd1, wrd2);
            
            System.out.println("SSim: " + wrd1 + " : " + wrd2 + " = " + ssim);

            wrd1 = "सजाना";
            wrd2 = "नवाना";

            ssim = cpf.getSurfaceSimilarity(wrd1, wrd2);

            System.out.println("SSim: " + wrd1 + " : " + wrd2 + " = " + ssim);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
