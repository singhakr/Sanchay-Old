/*
 * Created on Jul 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.langenc;

import sanchay.mlearning.lm.ngram.NGramLM;
import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.mlearning.lm.ngram.*;
import sanchay.mlearning.lm.ngram.impl.*;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LangEncIdentifier {
    
    /**
     * Constants defining the kind of identifier. 
     */
    
    public static final int STD_NGRAM_IDENTIFIER = 0;
    public static final int REL_PROB_IDENTIFIER = 1;
    public static final int FREQ_IDENTIFIER = 2;
    
    public static final int JOINT_PROB = 1;
    public static final int LOG_JOINT_PROB = 2;
    public static final int ABS_LOG_JOINT_PROB = 3;
    public static final int PROB_DIFF1 = 4;
    public static final int PROB_DIFF2 = 5;
    public static final int ABS_PROB_DIFF = 6;
    public static final int LOG_PROB_DIFF1 = 7;
    public static final int LOG_PROB_DIFF2 = 8;
    public static final int ABS_LOG_PROB_DIFF = 9;
    public static final int REL_ENTROPY1 = 10;
    public static final int REL_ENTROPY2 = 11;
    public static final int ABS_REL_ENTROPY1 = 12;
    public static final int ABS_REL_ENTROPY2 = 13;
    public static final int CROSS_ENTROPY1 = 14;
    public static final int CROSS_ENTROPY2 = 15;
    public static final int ABS_CROSS_ENTROPY1 = 16;
    public static final int ABS_CROSS_ENTROPY2 = 17;
    public static final int MUTUAL_CROSS_ENTROPY = 18;
    public static final int ABS_MUTUAL_CROSS_ENTROPY = 19;
    public static final int MUTUAL_REL_ENTROPY = 20;
    public static final int ABS_MUTUAL_REL_ENTROPY = 21;
    public static final int DEKANG_LIN = 22;
    public static final int ABS_DEKANG_LIN = 23;
    public static final int JIANG_CONRATH = 24;
    public static final int ABS_JIANG_CONRATH = 25;
    public static final int CAVNAR_TRENKLE = 26;

    protected int identifierType;
    protected int scoreType;

    protected int charNGramOrder;
    protected int wordNGramOrder;

    protected int charPruneRank;
    protected int wordPruneRank;
    protected int pruneFreq;

    protected int topLangEncs;
    protected int numAmbiguous;

    protected double wrdModelWeight;

    // Character n-gram models
    protected NGramLM trainingModel;
    protected NGramLM testModel;

    // Word n-gram models
    protected NGramLM wrdTrainingModel;
    protected NGramLM wrdTestModel;

    protected KeyValueProperties trainingDataPaths; //  path is the key, enc-lang is the value
    protected KeyValueProperties testingDataPaths;
    // Parallel vectors
    Vector paths = new Vector(0, 5);
    
    // In case of default identification, each entry will be a String (langEnc)
    // For multi-lingual identification, each entry will be a LinkedHashMap (enumerated langEncs and their scores/likelihoods)
    Vector actual_enclangs = new Vector(0, 5);
    Vector identified_enclangs = new Vector(0, 5);
    
    // Only for multi-lingual identification
    // Each entry will be a Hashtable (path as key) of LinkedHashMaps (words types as keys and LinkedHashMaps of top langEncs as values)
    Vector identified_wrdtyp_enclangs = new Vector(0, 5);
    // For one document/text
    LinkedHashMap wrdtyp_enclangs;

    // Parallel vectors
    // Each entry will be a Hashtable (path as key) of LinkedHashMaps (words token indices as keys and LinkedHashMaps of top langEncs as values)
    Vector identified_wrdtok_enclangs = new Vector(0, 5);
    // For one document/text
    LinkedHashMap wrdtok_enclangs;
    
    protected boolean storeTrainingLMs = true;
    protected boolean useStoredTrainingLMs;
    protected boolean useWordNGram;
    protected boolean inMemoryModels;
    protected boolean featureNGrams;
    
    protected long maxTrainingDataSize;
    protected long minTrainingDataSize;
    protected long totalTrainingDataSize;
    
    protected long maxTestingDataSize;
    protected long minTestingDataSize;
    protected long totalTestingDataSize;
    
    protected long maxTrainingDataSizeWrd;
    protected long minTrainingDataSizeWrd;
    protected long totalTrainingDataSizeWrd;
    
    protected long maxTestingDataSizeWrd;
    protected long minTestingDataSizeWrd;
    protected long totalTestingDataSizeWrd;
    
    // Temp
    protected Hashtable allScores;
    protected Hashtable trainingModels;
    protected Hashtable wrdTrainingModels;

    /**
     * 
     */
    public LangEncIdentifier() {
        super();
        // TODO Auto-generated constructor stub
        useStoredTrainingLMs = false;

        maxTrainingDataSize = -1;
        minTrainingDataSize = Long.MAX_VALUE;
        
        maxTestingDataSize = -1;
        minTestingDataSize = Long.MAX_VALUE;

        maxTrainingDataSizeWrd = -1;
        minTrainingDataSizeWrd = Long.MAX_VALUE;
        
        maxTestingDataSizeWrd = -1;
        minTestingDataSizeWrd = Long.MAX_VALUE;
    }

    public LangEncIdentifier(boolean useStoredTrainingLMs) {
        super();
        // TODO Auto-generated constructor stub
        this.useStoredTrainingLMs = useStoredTrainingLMs;

        maxTrainingDataSize = -1;
        minTrainingDataSize = Long.MAX_VALUE;
        
        maxTestingDataSize = -1;
        minTestingDataSize = Long.MAX_VALUE;

        maxTrainingDataSizeWrd = -1;
        minTrainingDataSizeWrd = Long.MAX_VALUE;
        
        maxTestingDataSizeWrd = -1;
        minTestingDataSizeWrd = Long.MAX_VALUE;
    }

    public LangEncIdentifier(String trndpaths, int type, int st, int charNGrams,
	    boolean useStoredTrainingLMs, boolean useWordNGram,
	    boolean inMemory, int numAmbiguous, int wordNGrams, double wordNGramsWeight,
	    int charNGramOrder, int wordNGramOrder, boolean featureNGrams) throws FileNotFoundException, IOException
    {
        this(trndpaths, type, st, charNGrams, useStoredTrainingLMs, useWordNGram,
	    inMemory, numAmbiguous, wordNGrams, wordNGramsWeight, charNGramOrder, wordNGramOrder);
        
        this.featureNGrams = featureNGrams;
    }

    public LangEncIdentifier(String trndpaths, int type, int st, int charNGrams,
	    boolean useStoredTrainingLMs, boolean useWordNGram,
	    boolean inMemory, int numAmbiguous, int wordNGrams, double wordNGramsWeight,
	    int charNGramOrder, int wordNGramOrder) throws FileNotFoundException, IOException
    {
        if(trndpaths != null && (new File(trndpaths)).exists())
        {
            trainingDataPaths = new KeyValueProperties();
            trainingDataPaths.read(trndpaths, GlobalProperties.getIntlString("UTF-8"));
//            trainingDataPaths.print(System.out);
        }

        this.charNGramOrder = charNGramOrder;
	this.wordNGramOrder = wordNGramOrder;

	charPruneRank = charNGrams;
	wordPruneRank = wordNGrams;
	pruneFreq = 3;
        identifierType = type;
        scoreType = st;
	wrdModelWeight = wordNGramsWeight;

        this.useStoredTrainingLMs = useStoredTrainingLMs;
        this.useWordNGram = useWordNGram;
	this.numAmbiguous = numAmbiguous;
	inMemoryModels = inMemory;

        maxTrainingDataSize = -1;
        minTrainingDataSize = Long.MAX_VALUE;

        maxTestingDataSize = -1;
        minTestingDataSize = Long.MAX_VALUE;

        maxTrainingDataSizeWrd = -1;
        minTrainingDataSizeWrd = Long.MAX_VALUE;

        maxTestingDataSizeWrd = -1;
        minTestingDataSizeWrd = Long.MAX_VALUE;
    }
    
    public int getIdentifierType()
    {
        return identifierType;
    }
    
    public void setIdentifierType(int type)
    {
        identifierType = type;
    }

    /**
     * @return the trainingDataPaths
     */
    public KeyValueProperties getTrainingDataPaths() {
        return trainingDataPaths;
    }

    /**
     * @param trainingDataPaths the trainingDataPaths to set
     */
    public void setTrainingDataPaths(KeyValueProperties trainingDataPaths) {
        this.trainingDataPaths = trainingDataPaths;

//        trainingDataPaths.print(System.out);
    }

    /**
     * @return the testingDataPaths
     */
    public KeyValueProperties getTestingDataPaths() {
        return testingDataPaths;
    }

    /**
     * @param testingDataPaths the testingDataPaths to set
     */
    public void setTestingDataPaths(KeyValueProperties testingDataPaths) {
        this.testingDataPaths = testingDataPaths;
    }

    /**
     * @return Returns the charPruneRank.
     */
    public int getCharPruneRank() {
        return charPruneRank;
    }

    /**
     * @return the storeTrainingLMs
     */
    public boolean isStoringTrainingLMs() {
        return storeTrainingLMs;
    }

    /**
     * @param storeTrainingLMs the storeTrainingLMs to set
     */
    public void setStoreTrainingLMs(boolean useStoredTrainingLMs) {
        this.storeTrainingLMs = storeTrainingLMs;
    }

    /**
     * @return the useStoredTrainingLMs
     */
    public boolean isUseStoredTrainingLMs() {
        return useStoredTrainingLMs;
    }

    /**
     * @param useStoredTrainingLMs the useStoredTrainingLMs to set
     */
    public void setUseStoredTrainingLMs(boolean useStoredTrainingLMs) {
        this.useStoredTrainingLMs = useStoredTrainingLMs;
    }

    /**
     * @return the inMemoryModels
     */
    public boolean isInMemoryModels() {
        return inMemoryModels;
    }

    /**
     * @param inMemoryModels the inMemoryModels to set
     */
    public void setInMemoryModels(boolean inMemoryModels) {
        this.inMemoryModels = inMemoryModels;
    }
    
    public void setCharPruneRank(int charPruneRank) {
        this.charPruneRank = charPruneRank;
    }

    public int getPruneFreq() {
        return pruneFreq;
    }
    
    public void setPruneFreq(int pruneFreq) {
        this.pruneFreq = pruneFreq;
    }
    
    public int countNGramLMs()
    {
        return getTrainingDataPaths().countProperties();
    }

    public Iterator getNGramLMKeys()
    {
        return getTrainingDataPaths().getPropertyKeys();
    }

    public String getNGramLM(String k /* key */)
    {
	return getTrainingDataPaths().getPropertyValue(k);
    }

    public int addNGramLM(String k /* key */, String lm /* file/directory path */)
    {
        getTrainingDataPaths().addProperty(k, lm);
        return countNGramLMs();
    }

    public String removeNGramLM(String k /* key */)
    {
        return getTrainingDataPaths().removeProperty(k);
    }

    public NGramLM makeNGramLM(String k /* key */, String type) throws FileNotFoundException, IOException
    {
	File nf = new File(getTrainingDataPaths().getPropertyValue(k));
	NGramLM nglm = null;
	
//	String parts[] = k.split("[::]");
//	
//	if(parts == null && parts.length != 2)
//	{
//	    System.out.println("Wrong language-encoding pair: " + k);
//	    return null;
//	}
	
	String charset = GlobalProperties.getIntlString("ISO-8859-1");
	
	if(type.equalsIgnoreCase("word"))
	{
//	    if(parts[1].equalsIgnoreCase("UTF-8") || parts[1].equalsIgnoreCase("UTF-8"))
		charset = GlobalProperties.getIntlString("UTF-8");
	}
	
	String lang = GlobalProperties.getIntlString("hin::utf8");

	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nglm = new NGramLMImpl(nf, type, charNGramOrder, charset, lang);
	else if(type.equalsIgnoreCase("word"))
	    nglm = new NGramLMImpl(nf, type, wordNGramOrder, charset, lang);

	nglm.makeNGramLM((File) null);

        if(featureNGrams)
            nglm = nglm.getCPMSFeaturesNGramLM(nglm.getNGramLMFile());
	
	long trainingDataSize = nglm.countTokens(1);
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	{
	    if(minTrainingDataSize > trainingDataSize)
		minTrainingDataSize = trainingDataSize;

	    if(maxTrainingDataSize < trainingDataSize)
		maxTrainingDataSize = trainingDataSize;
	    
	    totalTrainingDataSize += trainingDataSize;
	}
	else if(type.equalsIgnoreCase("word"))
	{
	    if(minTrainingDataSizeWrd > trainingDataSize)
		minTrainingDataSizeWrd = trainingDataSize;

	    if(maxTrainingDataSizeWrd < trainingDataSize)
		maxTrainingDataSizeWrd = trainingDataSize;
	    
	    totalTrainingDataSizeWrd += trainingDataSize;
	}
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nglm.pruneByRankAndMerge(charPruneRank, 0);
	else if(type.equalsIgnoreCase("word"))
	    nglm.pruneByRankAndMerge(wordPruneRank, 0);

        storeNGramLM(nglm, k, type);
        storeNGramLMArpa(nglm, k, type);
	
	return nglm;	    
    }

    public NGramLM loadNGramLM(String k /* key */, String type) throws FileNotFoundException, IOException, ClassNotFoundException
    {
	File nf = null;
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".nglmc");
	else if(type.equalsIgnoreCase("word"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".nglmw");

	NGramLM nGramLM = null;
	
	if(nf != null && nf.exists())
	    nGramLM = NGramLMImpl.loadNGramLMBinary(nf);

	// Just for filling ranks
	nGramLM.pruneByRankAndMerge(-1, 0);
	
	return nGramLM;
    }

    public NGramLM loadNGramLMArpa(String k /* key */, String type) throws FileNotFoundException, IOException, ClassNotFoundException
    {
	File nf = null;
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".arpac");
	else if(type.equalsIgnoreCase("word"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".arpaw");

	String charset = GlobalProperties.getIntlString("ISO-8859-1");
	
	if(type.equalsIgnoreCase("word"))
	{
//	    if(parts[1].equalsIgnoreCase("UTF-8") || parts[1].equalsIgnoreCase("UTF-8"))
		charset = GlobalProperties.getIntlString("UTF-8");
	}

	String lang = "hin::utf8";
	
	NGramLM nGramLM = null;

	if(nf != null && nf.exists())
	{
	    if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
		nGramLM = NGramLMImpl.loadNGramLMArpa(nf, type, charNGramOrder, charset, lang);
	    else if(type.equalsIgnoreCase("word"))
		nGramLM = NGramLMImpl.loadNGramLMArpa(nf, type, wordNGramOrder, charset, lang);
	}
	
	// Just for filling ranks
	nGramLM.pruneByRankAndMerge(-1, 0);
	
	return nGramLM;
    }

    public void storeNGramLM(NGramLM nglm, String k /* key */, String type) throws FileNotFoundException, IOException
    {
        if(storeTrainingLMs == false)
            return;

	File nf = null;
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".nglmc");
	else if(type.equalsIgnoreCase("word"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".nglmw");

	if(nf != null)
	    NGramLMImpl.storeNGramLM(nglm, nf);
    }

    public void storeNGramLMArpa(NGramLM nglm, String k /* key */, String type) throws FileNotFoundException, IOException
    {
        if(storeTrainingLMs == false)
            return;

	File nf = null;
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".arpac");
	else if(type.equalsIgnoreCase("word"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".arpaw");

	String charset = GlobalProperties.getIntlString("ISO-8859-1");
	
	if(type.equalsIgnoreCase("word"))
	{
//	    if(parts[1].equalsIgnoreCase("UTF-8") || parts[1].equalsIgnoreCase("UTF-8"))
		charset = GlobalProperties.getIntlString("UTF-8");
	}

	if(nf != null)
	    NGramLMImpl.storeNGramLMArpa(nglm, nf, charset, true);
    }

    public boolean nGramLMArpaExists(String k /* key */, String type)
    {
	File nf = null;
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".arpac");
	else if(type.equalsIgnoreCase("word"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".arpaw");

	if(nf != null && nf.exists())
	    return true;
	
	return false;
    }

    public boolean nGramLMExists(String k /* key */, String type)
    {
	File nf = null;
	
	if(type.equalsIgnoreCase("char") || type.equalsIgnoreCase("uchar"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".nglmc");
	else if(type.equalsIgnoreCase("word"))
	    nf = new File(getTrainingDataPaths().getPropertyValue(k) + ".nglmw");

	if(nf != null && nf.exists())
	    return true;
	
	return false;
    }
    
    /**
     * Training is done for pairs of language and encoding. These pairs and their
     * corresponding directory names (which have the training files/directories)
     * are read from a properties file. The left column has the language and encoding
     * names separated by double colon (::). The right column gives the path. 
     */
    public void train()
    {
//        System.out.println("Starting training...");
	if(isInMemoryModels())
	{
	   trainingModels = new Hashtable(getTrainingDataPaths().countProperties());

	    if(useWordNGram)
		wrdTrainingModels = new Hashtable(getTrainingDataPaths().countProperties());
	}
	
        Iterator enm = getTrainingDataPaths().getPropertyKeys();
        
        while(enm.hasNext())
        {
            String k = (String) enm.next();
            String v = getTrainingDataPaths().getPropertyValue(k);
            
            boolean nglmExists = nGramLMExists(k, "uchar"); //char
            
            if(featureNGrams)
                nglmExists = nGramLMExists(k, "uchar");
                
            boolean wnglmExists = nGramLMExists(k, "word");
            
	    try {
		if(isUseStoredTrainingLMs() == false || nglmExists == false)
		{
                    if(featureNGrams)
                        trainingModel = makeNGramLM(k, "uchar");
                    else
                        trainingModel = makeNGramLM(k, "uchar");//char

                    if(featureNGrams)
                        trainingModel = trainingModel.getCPMSFeaturesNGramLM(trainingModel.getNGramLMFile());
		}
		else if(isUseStoredTrainingLMs() && nglmExists && isInMemoryModels())
                {
                    if(featureNGrams)
                        trainingModel = loadNGramLM(k, "uchar");
                    else
                        trainingModel = loadNGramLM(k, "uchar");//char
//		    trainingModel = loadNGramLMArpa(k, "char");
                }

		if(useWordNGram && (isUseStoredTrainingLMs() == false || wnglmExists == false))
		{
		    wrdTrainingModel = makeNGramLM(k, "word");
		}
		else if(useWordNGram && (isUseStoredTrainingLMs() && nglmExists && isInMemoryModels()))
		    wrdTrainingModel = loadNGramLM(k, "word");
//		    wrdTrainingModel = loadNGramLMArpa(k, "word");
		
		if(isInMemoryModels())
		{
		    trainingModels.put(k, trainingModel);
		    
		    if(useWordNGram)
			wrdTrainingModels.put(k, wrdTrainingModel);
		}
	    } catch (ClassNotFoundException ex) {
		    ex.printStackTrace();
	    }  catch (FileNotFoundException ex) {
		    ex.printStackTrace();
	    } catch (IOException ex) {
		    ex.printStackTrace();
	    }
        }
    }

    public void evaluate(String tstdpaths) throws FileNotFoundException, IOException
    {
//        System.out.println("Starting evaluation...");

        setTestingDataPaths(new KeyValueProperties());
        getTestingDataPaths().read(tstdpaths, GlobalProperties.getIntlString("UTF-8"));
 
//        System.out.println("Testing data paths:");
//        testingDataPaths.print(System.out);

        // Parallel vectors
        paths = new Vector(0, 5);
        actual_enclangs = new Vector(0, 5);
        identified_enclangs = new Vector(0, 5);

        int oldSize = -1;
        int newSize = -1;
        Iterator enm = getTestingDataPaths().getPropertyKeys();
	
	allScores = new Hashtable(0, 50);
        
        while(enm.hasNext())
        {
            String k = (String) enm.next();
            String v = getTestingDataPaths().getPropertyValue(k);
            
            oldSize = paths.size();

            File file = new File(k);
            identifyBatch(file, false);
            
            newSize = paths.size();
            
            for(int i = 0; i < (newSize - oldSize); i++)
            {
                actual_enclangs.add(v);
            }
        }
        
        if(paths.size() != actual_enclangs.size() || actual_enclangs.size() != identified_enclangs.size()
                || identified_enclangs.size() != paths.size())
        {
            System.out.println(GlobalProperties.getIntlString("Error_in_evaluate_method."));
            System.exit(1);
        }
        
        int correct = 0;

        System.out.println(GlobalProperties.getIntlString("paths:_") + paths.size());
        System.out.println("actual_enclangs: " + actual_enclangs.size());
        System.out.println("identified_enclangs: " + identified_enclangs.size());
        
        Hashtable errors = new Hashtable(paths.size() / 4);
        Hashtable errorCounts = new Hashtable(paths.size() / 4);
        Hashtable errorPaths = new Hashtable(paths.size() / 4);
        Hashtable errorInfo = new Hashtable(paths.size() / 4);
        
        for(int i = 0; i < paths.size(); i++)
        {
           // System.out.print("LD::" + actual_enclangs.get(i) + "::");
           // System.out.print("LD::" + identified_enclangs.get(i));
            
            if(((String) actual_enclangs.get(i)).equals((String) identified_enclangs.get(i)))
            {
//                System.out.println("Found a correct one.");
                correct++;
            }
            else
            {
                if(errors.get(actual_enclangs.get(i)) == null)
                {
                    errors.put(actual_enclangs.get(i), identified_enclangs.get(i));
                    errorCounts.put(actual_enclangs.get(i), new Integer(1));
                }
                else
                {
                    Integer oldCount = (Integer) errorCounts.get(actual_enclangs.get(i));
                    Integer newCount = new Integer(oldCount.intValue() + 1);
                    errorCounts.put(actual_enclangs.get(i), newCount);
                }
		
		String errinf = "\tScore_with_actual_encoding_(" + actual_enclangs.get(i) + ") :"+ ((LinkedHashMap) allScores.get(paths.get(i))).get(actual_enclangs.get(i));
		errinf += "\n\tScore_with_identified_encoding_(" + identified_enclangs.get(i) + ") :" + ((LinkedHashMap) allScores.get(paths.get(i))).get(identified_enclangs.get(i));

                errorPaths.put(paths.get(i), identified_enclangs.get(i));
                errorInfo.put(paths.get(i), errinf);
            }
            
//            System.out.println(paths.get(i) + "\t" + actual_enclangs.get(i) + "\t" + identified_enclangs.get(i));
        }
        
        double precision = ( ((double) correct) / ((double) paths.size()) ) * 100;
        
        System.out.println("Correct: " + correct);
        System.out.println("Total: " + paths.size());
        System.out.println("Precision: " + precision);
        
        System.out.println("Errors: ");
        Enumeration errenm = errors.keys();
        
        while(errenm.hasMoreElements())
        {
            String errkey = (String) errenm.nextElement();
            String errval = (String) errors.get(errkey);
            Integer errCount = (Integer) errorCounts.get(errkey);

            System.out.println(errkey + " identified as " + errval + " " + errCount + " times.");
        }

        System.out.println("Error details: ");
        errenm = errorPaths.keys();
        
        while(errenm.hasMoreElements())
        {
            String errkey = (String) errenm.nextElement();
            String errval = (String) errorPaths.get(errkey);
            String errinf = (String) errorInfo.get(errkey);

            System.out.println(errkey + " got identified as " + errval + ".");
            System.out.println(errinf);
        }

        System.out.println("----------------------------------------------------");

	if(isUseStoredTrainingLMs() == false)
	{
	    System.out.println("Minimum training data size: " + minTrainingDataSize + " characters.");
	    System.out.println("Maximum training data size: " + maxTrainingDataSize + " characters.\n");
	    System.out.println("Average training data size: " + totalTrainingDataSize/getTrainingDataPaths().countProperties() + " characters.");
	}

	System.out.println("Minimum test data size: " + minTestingDataSize + " characters.");
        System.out.println("Maximum test data size: " + maxTestingDataSize + " characters.");
	System.out.println("Average test data size: " + totalTestingDataSize/paths.size() + " characters.");

	System.out.println("----------------------------------------------------");
	
	if(useWordNGram)
	{
	    if(isUseStoredTrainingLMs() == false)
	    {
		System.out.println("Minimum training data size: " + minTrainingDataSizeWrd + " words.");
		System.out.println("Maximum training data size: " + maxTrainingDataSizeWrd + " words.\n");
		System.out.println("Average training data size: " + totalTrainingDataSizeWrd/getTrainingDataPaths().countProperties() + " words.");
	    }

	    System.out.println("Minimum test data size: " + minTestingDataSizeWrd + " words.");
	    System.out.println("Maximum test data size: " + maxTestingDataSizeWrd + " words.");
	    System.out.println("Average test data size: " + totalTestingDataSizeWrd/paths.size() + " words.");
	    System.out.println("----------------------------------------------------");
	}

	System.err.println("*** Precision: " + precision + " ****");
	
	// Print all scores for errors:
//	System.out.println("\nAll scores:\n");
//	enm = errorPaths.keys();
//        while(enm.hasMoreElements())
//        {
//            String test = (String) enm.nextElement();
//            Hashtable scores = (Hashtable) allScores.get(test);
//	    
//	    Enumeration enm1 = scores.keys();
//	    while(enm1.hasMoreElements())
//	    {
//		String model = (String) enm1.nextElement();
//		Double score = (Double) scores.get(model);
//		
//		System.out.println("Test: " + test + ", Model: " + model + ", score = " + score);
//	    }
//	}
    }

    public void evaluateLineWise(String tstdpaths) throws FileNotFoundException, IOException
    {
        System.out.println("Starting evaluation...");
        
        train();

        setTestingDataPaths(new KeyValueProperties());
        getTestingDataPaths().read(tstdpaths, "UTF-8");

        Iterator enm = getTestingDataPaths().getPropertyKeys();
        
        while(enm.hasNext())
        {
            String k = (String) enm.next();
            String v = getTestingDataPaths().getPropertyValue(k);

            System.out.println("-----------------------------");
            System.out.println("\t" + k + "...");
            System.out.println("\t" + v + "...");
            
            int correct = 0;
            
            try 
            {
                correct = 0;
                
                PropertyTokens wrds = new PropertyTokens(k, "UTF-8");
                File f = new File(k);
                
                String ofile = f.getParentFile().getParentFile().getParent() + "/output/" + f.getParentFile().getName() + "/" + f.getName();
                KeyValueProperties output = new KeyValueProperties();

                int count = wrds.countTokens();
                
                for(int i = 0; i < count; i++)
                {
                    String wrd = wrds.getToken(i);
                    String vEval = getBestLangEnc(identify(wrd));
                    
                    output.addProperty(wrd, vEval);
                    
                    if(v.equalsIgnoreCase(vEval))
                        correct++;                    
                }
                
                output.save(ofile, "UTF-8");
                
                double precision = ((double) correct)/((double) count);
                
                System.out.println("\tPrecision: " + precision);            
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void evaluateMultilingual(String tstdpaths) throws FileNotFoundException, IOException
    {
	
    }
    
    private void identifyBatch(File f, boolean multi) throws FileNotFoundException, IOException
    {
        if(f.isFile() == true)
        {
	    String ans = null;
	    
	    if(multi)
		ans = getBestLangEnc(identifyMultilingual(f));
	    else
		ans = getBestLangEnc(identify(f));

        System.out.println(f.getAbsolutePath() + "\t" + ans);

	    paths.add(f.getAbsolutePath());
            identified_enclangs.add(ans);
        }
        else
        {
            if(f.isDirectory() == true)
            {
                File files[] = f.listFiles();

                for(int i = 0; i < files.length; i++)
                {
		    identifyBatch(files[i], multi);
                }
            }
        }
    }
    
    protected String getBestLangEnc(LinkedHashMap topLEs)
    {
	Set keys = topLEs.keySet();
	Iterator itr = keys.iterator();
	
	if(itr != null && topLEs.size() > 0)
	{
	    return (String) itr.next();
	}
	
	return null;
    }

    public LinkedHashMap identify(String testString)
    {
        LinkedHashMap bestModels = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER)
        {
            if(featureNGrams)
                testModel = new NGramLMImpl(null, "uchar", charNGramOrder);
            else
                testModel = new NGramLMImpl(null, "uchar", charNGramOrder);//char
	    
	    if(useWordNGram)
		wrdTestModel = new NGramLMImpl(null, "word", wordNGramOrder, "UTF-8", "hin::utf8");

	    testModel.makeNGramLM(testString);

            if(featureNGrams)
                testModel = testModel.getCPMSFeaturesNGramLM(testModel.getNGramLMFile());
            
	    testModel.pruneByRankAndMerge(-1, 0);

	    bestModels = identify(testModel, wrdTestModel, null);
        }

	return bestModels;
    }
    
    /**
     * 
     * @param A test File.
     * @return A String containing the names of the language and the encoding,
     * separated by double colon (::).
     */
    public LinkedHashMap identify(File f) throws FileNotFoundException, IOException
    {
//        System.out.println("Identifying " + f.getAbsolutePath() + "...");
        LinkedHashMap bestModels = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER)
        {
            if(featureNGrams)
                testModel = new NGramLMImpl(f, "uchar", charNGramOrder);
            else
                testModel = new NGramLMImpl(f, "uchar", charNGramOrder);//char
	    
	    if(useWordNGram)
		wrdTestModel = new NGramLMImpl(f, "word", wordNGramOrder, GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));

            try
            {
                testModel.makeNGramLM((File) null);

                if(featureNGrams)
                    testModel = testModel.getCPMSFeaturesNGramLM(testModel.getNGramLMFile());
                
		testModel.pruneByRankAndMerge(-1, 0);
		
		bestModels = identify(testModel, wrdTestModel, f);
            }
            catch(IOException e) 
            {
                System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
            }
        }

	return bestModels;
    }

    /**
     * @return LinkedHashMap with top t langEncs and their scrores
     */
    public LinkedHashMap identify(NGramLM testModel, NGramLM wrdTestModel, File testFile /* Just for evaluation: allScores */)
    {
//        System.out.println("Identifying " + f.getAbsolutePath() + "...");
        String modelKey = "";
        
        if(identifierType != STD_NGRAM_IDENTIFIER)
        {
	    long testingDataSize = testModel.countTokens(1);

	    if(minTestingDataSize > testingDataSize)
		minTestingDataSize = testingDataSize;

	    if(maxTestingDataSize < testingDataSize)
		maxTestingDataSize = testingDataSize;

	    totalTestingDataSize += testingDataSize;

	    if(useWordNGram)
	    {
		long testingDataSizeWrd = wrdTestModel.countTokens(1);

		if(minTestingDataSizeWrd > testingDataSizeWrd)
		    minTestingDataSizeWrd = testingDataSizeWrd;

		if(maxTestingDataSizeWrd < testingDataSizeWrd)
		    maxTestingDataSizeWrd = testingDataSizeWrd;

		totalTestingDataSizeWrd += testingDataSizeWrd;
	    }
        }

        LinkedHashMap modelScores = new LinkedHashMap(this.countNGramLMs());
        LinkedHashMap wrdModelScores = new LinkedHashMap(this.countNGramLMs());
	
        if(allScores != null)
            allScores.put(testFile.getAbsolutePath(), modelScores);

        Iterator enm = getNGramLMKeys();
	
	long trainingDataSize = 0;
	long trainingDataSizeWrd = 0;

        while(enm.hasNext())
        {
            modelKey = (String) enm.next();
	    
	    try {
		if(isInMemoryModels())
		    trainingModel = (NGramLM) trainingModels.get(modelKey);
		else
                {
                    if(featureNGrams)
                        trainingModel = loadNGramLM(modelKey, "uchar");
                    else
                        trainingModel = loadNGramLM(modelKey, "uchar");//char
//		    trainingModel = loadNGramLMArpa(modelKey, "char");
                }
				    
		if(useWordNGram)
		{
		    if(isInMemoryModels())
			wrdTrainingModel = (NGramLM) wrdTrainingModels.get(modelKey);
		    else
			wrdTrainingModel = loadNGramLM(modelKey, GlobalProperties.getIntlString("word"));
//			wrdTrainingModel = loadNGramLMArpa(modelKey, "word");
		}
	    } catch (FileNotFoundException ex) {
		ex.printStackTrace();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    } catch (ClassNotFoundException ex) {
		ex.printStackTrace();
	    }

            double modelScore = 0.0;
            double wrdModelScore = 0.0;
	    
	    double tst = 0.0;
	    double trn = 0.0;
	    double jnt = 0.0;

	    double wtst = 0.0;
	    double wtrn = 0.0;
	    double wjnt = 0.0;

            if(identifierType == FREQ_IDENTIFIER || identifierType == REL_PROB_IDENTIFIER)
            {
                for(int j = 1; j <= trainingModel.getNGramOrder(); j++)
                {
                    Iterator<List<Integer>> testItr = testModel.getNGramKeys(j);

                    while(testItr.hasNext())
                    {
                        List<Integer> testNGram = testItr.next();
                        NGram testNg = (NGram) testModel.getNGram(testNGram, j);
                        NGram trainNg = (NGram) trainingModel.getNGram(testNGram, j);

                        if(trainNg != null)
                        {
                            // System.out.println("Matched NGram: " + ((NGram) sortedNGrams.get(i)).getString() + "\t" + ((NGram) sortedTestNGrams.get(k)).getString());
			    if(scoreType == DEKANG_LIN || scoreType == JIANG_CONRATH
				    || scoreType == ABS_DEKANG_LIN || scoreType == ABS_JIANG_CONRATH)
			    {
				tst += Math.log(testNg.getProb());
				trn += Math.log(trainNg.getProb());
				jnt += calcMatchScore(trainNg, testNg, identifierType, LOG_JOINT_PROB);
			    }
			    else
				modelScore += calcMatchScore(trainNg, testNg, identifierType, scoreType);
                        }
                    }
                }

		if(useWordNGram)
		{
		    Iterator<List<Integer>> testItr = wrdTestModel.getNGramKeys(1);

		    while(testItr.hasNext())
		    {
			List<Integer> testNGram = testItr.next();
			NGram testNg = (NGram) wrdTestModel.getNGram(testNGram, 1);
			NGram trainNg = (NGram) wrdTrainingModel.getNGram(testNGram, 1);

			if(testNGram.equals("") == false && trainNg != null)
			{
			    // System.out.println("Matched NGram: " + ((NGram) sortedNGrams.get(i)).getString() + "\t" + ((NGram) sortedTestNGrams.get(k)).getString());
			    if(scoreType == DEKANG_LIN || scoreType == JIANG_CONRATH
				    || scoreType == ABS_DEKANG_LIN || scoreType == ABS_JIANG_CONRATH)
			    {
				wtst += Math.log(testNg.getProb());
				wtrn += Math.log(trainNg.getProb());
				wjnt += calcMatchScore(trainNg, testNg, identifierType, LOG_JOINT_PROB);
			    }
			    else
				wrdModelScore += calcMatchScore(trainNg, testNg, identifierType, scoreType);
			}
		    }
		    
		    // For debugging
		    if(wrdModelScore != 0.0)
			;
		}
            }

	    if(scoreType == DEKANG_LIN)
	    {
		modelScore = (2.0 * jnt) / (tst + trn);

		if(useWordNGram)
		    wrdModelScore = (2.0 * wjnt) / (wtst + wtrn);
	    }
	    else if(scoreType == JIANG_CONRATH)
	    {
		modelScore = -1.0 * ( (2.0 * jnt) - (tst + trn) );

		if(useWordNGram)
		    wrdModelScore = -1.0 * ( (2.0 * wjnt) - (wtst + wtrn) );
	    }

            modelScores.put(modelKey, new Double(modelScore));
	    
	    if(useWordNGram)
		wrdModelScores.put(modelKey, new Double(wrdModelScore));
        }

        // Return the best model
	boolean takeMax = false;
	    
	if(scoreType == ABS_CROSS_ENTROPY1 || scoreType == ABS_CROSS_ENTROPY2 || scoreType == ABS_MUTUAL_CROSS_ENTROPY
		|| scoreType == DEKANG_LIN || scoreType == JIANG_CONRATH
		|| scoreType == ABS_DEKANG_LIN || scoreType == ABS_JIANG_CONRATH
		|| scoreType == ABS_LOG_PROB_DIFF || scoreType == ABS_PROB_DIFF
		|| scoreType == LOG_PROB_DIFF2 || scoreType == CAVNAR_TRENKLE
		|| scoreType == REL_ENTROPY1 || scoreType == REL_ENTROPY2
		|| scoreType == MUTUAL_REL_ENTROPY || scoreType == ABS_MUTUAL_REL_ENTROPY
	)
	    takeMax = true;
	else if(scoreType == PROB_DIFF1 || scoreType == PROB_DIFF2
		|| scoreType == LOG_PROB_DIFF1
		|| scoreType == CROSS_ENTROPY1 || scoreType == CROSS_ENTROPY2
		|| scoreType == ABS_REL_ENTROPY1 || scoreType == ABS_REL_ENTROPY2
		|| scoreType == MUTUAL_CROSS_ENTROPY
	)
	    takeMax = false;

        LinkedHashMap bestModels = new LinkedHashMap(modelScores.size());
	
	Vector sortedScores = new Vector(modelScores.size());

        enm = modelScores.keySet().iterator();

	while(enm.hasNext())
	{
	    modelKey = (String) enm.next();
	    Double modelScore = (Double) modelScores.get(modelKey);
	    ModelScore ms = new ModelScore(modelKey, modelScore);
	    sortedScores.add(ms);
	}
	
	if(takeMax)
	    Collections.sort(sortedScores, new Comparator() {
		public int compare(Object o1, Object o2) {
		    return ( (int) ((ModelScore) o2).modelScore.doubleValue() - (int) ((ModelScore) o1).modelScore.doubleValue() );
		}
	    });
	else
	    Collections.sort(sortedScores, new Comparator() {
		public int compare(Object o1, Object o2) {
		    return ( (int) ((ModelScore) o1).modelScore.doubleValue() - (int) ((ModelScore) o2).modelScore.doubleValue() );
		}
	    });
	
	if(useWordNGram)
	{
	    int count = Math.min(numAmbiguous, modelScores.size());
	    for (int i = 0; i < count; i++)
	    {
		modelKey = ((ModelScore) sortedScores.get(i)).modelKey;
		Double modelScore = ((ModelScore) sortedScores.get(i)).modelScore;
		Double wrdModelScore = (Double) wrdModelScores.get(modelKey);

		double s = modelScore.doubleValue();
		
		if(wrdModelScore.doubleValue() != 0.0)
		    s += wrdModelWeight * wrdModelScore.doubleValue();
		
		modelScores.put(modelKey, new Double(s));

    //            System.out.println("\t" + modelKey + "\t" + modelScore);
	    }

	    // Redo sorting with word model scores
	    sortedScores.removeAllElements();
	    enm = modelScores.keySet().iterator();

	    while(enm.hasNext())
	    {
		modelKey = (String) enm.next();
		Double modelScore = (Double) modelScores.get(modelKey);
		ModelScore ms = new ModelScore(modelKey, modelScore);
		sortedScores.add(ms);
	    }

	    if(takeMax)
		Collections.sort(sortedScores, new Comparator() {
		    public int compare(Object o1, Object o2) {
			return ( (int) ((ModelScore) o2).modelScore.doubleValue() - (int) ((ModelScore) o1).modelScore.doubleValue() );
		    }
		});
	    else
		Collections.sort(sortedScores, new Comparator() {
		    public int compare(Object o1, Object o2) {
			return ( (int) ((ModelScore) o1).modelScore.doubleValue() - (int) ((ModelScore) o2).modelScore.doubleValue() );
		    }
		});
	}
	    
	for (int i = 0; i < sortedScores.size(); i++)
	{
	    ModelScore ms = (ModelScore) sortedScores.get(i);
	    bestModels.put(ms.modelKey, ms.modelScore);
	}

//        System.out.println("bestModel: " + bestModel);
        return bestModels;
    }

    /**
     * @return Strings from text as keys and langEncs as values.
     */
    public LinkedHashMap identifyMultilingual(String testString)
    {
        String bestModel = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER)
        {
        }

//	return bestModel;
	return null;
    }

    /**
     * Some points about generalised multilingual identification:<br>
     * - Not possible to use techniques like maximum entropy because there is unlikely to be any data for training<br>
     * - Limited ambiguity hypothesis: There are likely to be only two or three languages in a document is most cases<br>
     * - Single word problem: There are likely to be single (isolated) words in a language different from the surrounding words<br>
     *   which means sophisticated context based techniques are unlikely to work<br>
     * - The above also means that we have to operate at the character (or byte) level, rather than word or word sequence level<br>
     * - There can be unresolvable ambiguity because the same word may belong to many languages and those languages<br>
     *   might be using the same or almost same encoding<br>
     * - Different techniques can be applied depending on whether we assume that single isolated words will occur in the<br>
     *   the document or whether there will only be long sequences in the same language<br>
     * - If we assume the presence of single isolated words, the problem essentially reduces to identifying each word separately<br>
     * -- We can take the context into account to a limited extent, using a threshold and weighted sum<br>
     * - Otherwise, the problem is of determining the points at which language changes, along with monolingual identification<br>
     * -- This can be done by calculating sudden drops in probabilities of word n-grams based on the character based n-grams model for the current sequence whose langauge we have previously determined<br>
     * -- One problem here is to find the language of the first sequence<br>
     * - We can also have a dominant language assumption, which will affect other things above<br>
     * - We could also try to guess dynamically whether the single/isolated word case holds or not<br>
     * <br>
     * <br>
     * There are four steps, each can be considered a different problem:<br>
     * 1. Enumerating the languages the document is guessed to be in<br>
     * 2. Finding the top t most likely languages for each word<br>
     * 3. Based on the languages of the preceding and succeeding word(s), finalising the most likely (one) language for each word<br>
     * 4. Marking up portions of the text with their guessed languages<br>
     * <br>
     * Evaluation: There can five measures, correspoding the above sub-problems:<br>
     * 1. Precision of language enumeration<br>
     * 2. Precision of single word identification (isolated)<br>
     * 3. Precision of single word identification (context based)<br>
     * 4. Precision of markup<br>
     * <br>
     * @return Strings from text as keys and langEncs as values.
     */
    public LinkedHashMap identifyMultilingual(File testFile)
    {
        String bestModel = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER)
        {
        }

//	return bestModel;
	return null;
    }

    /**
     * Uses <br>
     * @return langEncs (Strings) keys and scores as values, sorted by scores (best first).
     */
    public LinkedHashMap enumerateLangEncs(File testFile)
    {
        String bestModel = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER)
        {
        }

//	return bestModel;
	return null;
    }

    public LinkedHashMap identifyWordTypes(File testFile, int topLangEncs) throws FileNotFoundException, IOException
    {
        BufferedReader lnReader = null;
        
        lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), GlobalProperties.getIntlString("UTF-8")));

        String line;
        String splitstr[] = null;
        
        wrdtyp_enclangs = new LinkedHashMap(0, 5);
        
        while((line = lnReader.readLine()) != null && line.equals("") == false)
        {
            splitstr = line.split("[\\s]");
            
            for (int i = 0; i < splitstr.length; i++)
            {
                if(wrdtyp_enclangs.get(splitstr[i]) == null)
                {
                    wrdtyp_enclangs.put(splitstr[i], identifyWord(splitstr[i], topLangEncs));
                }
            }
        }
        
        return wrdtyp_enclangs;
    }

    public LinkedHashMap identifyWordTokens(File testFile, int topLangEncs) throws FileNotFoundException, IOException
    {
        BufferedReader lnReader = null;
        
        lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), GlobalProperties.getIntlString("UTF-8")));

        String line;
        String splitstr[] = null;
        
        wrdtyp_enclangs = new LinkedHashMap(0, 5);
        
        while((line = lnReader.readLine()) != null && line.equals("") == false)
        {
            splitstr = line.split("[\\s]");
            
            for (int i = 0; i < splitstr.length; i++)
            {
                if(wrdtyp_enclangs.get(splitstr[i]) == null)
                {
                    wrdtyp_enclangs.put(splitstr[i], identify(splitstr[i]));
                }
            }
        }
        
        return wrdtyp_enclangs;
    }
    
    /**
     * Returns a Vector of LinkedHashMaps, where each LinkedHashMap has langEnc as the key and score as the value. <br>
     * Sorted (optionally top t) langEncs (by score) are returned.
     */
    public Vector identifyWords(String[] words, int topLangEncs)
    {
	Vector langEncsList = new Vector(words.length);
	
	for (int i = 0; i < words.length; i++)
	{
	    LinkedHashMap langEncs = identifyWord(words[i], topLangEncs);
	    langEncsList.add(langEncs);
	}
	
	return langEncsList;
    }

    public LinkedHashMap identifyWord(String word, int topLangEncs)
    {
        LinkedHashMap langEncs = identify(word);

        Set keys = langEncs.keySet();
        Iterator itr = keys.iterator();

        int j = 0;
        LinkedHashMap retLangEncs = new LinkedHashMap(topLangEncs);

        while(j < topLangEncs && itr.hasNext())
        {
            String le = (String) itr.next();

            retLangEncs.put(le, langEncs.get(le));
            j++;
        }
	
	return retLangEncs;
    }
    
    private static double calcMatchScore(NGram trainNG, NGram testNG, int idtype, int stype)
    {
        double score = 0.0;

        if(idtype == REL_PROB_IDENTIFIER)
        {
//			case STD_NGRAM_IDENTIFIER:
        //  Sequence probability; score not needed

//            if(stype == JOINT_PROB)
//                score = trainNG.getRelevanceProb() * testNG.getRelevanceProb();
//            else if(stype == REL_ENTROPY)
//                score = testNG.getRelevanceProb() * ( Math.log( testNG.getRelevanceProb() / trainNG.getRelevanceProb() ) );
        }
        else if(idtype == FREQ_IDENTIFIER)
        {
//          System.out.println("\t\t*trainNG.getProb(): " + trainNG.getProb());
//          System.out.println("\t\t*testNG.getProb(): " + testNG.getProb());

            if(stype == JOINT_PROB)
		score = testNG.getProb() * trainNG.getProb();
            else if(stype == LOG_JOINT_PROB)
		score = Math.log(testNG.getProb()) + Math.log(trainNG.getProb());
            else if(stype == ABS_LOG_JOINT_PROB)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, LOG_JOINT_PROB) );
            else if(stype == PROB_DIFF1)
		score = trainNG.getProb() - testNG.getProb();
            else if(stype == PROB_DIFF2)
		score = testNG.getProb() - trainNG.getProb();
            else if(stype == ABS_PROB_DIFF)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, PROB_DIFF1) );
            else if(stype == LOG_PROB_DIFF1)
		score = Math.log(trainNG.getProb()) - Math.log(testNG.getProb());
            else if(stype == LOG_PROB_DIFF2)
		score = Math.log(testNG.getProb()) - Math.log(trainNG.getProb());
            else if(stype == ABS_LOG_PROB_DIFF)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, LOG_PROB_DIFF1) );
	    else if(stype == CROSS_ENTROPY1)
		score = testNG.getProb() * Math.log(trainNG.getProb());
            else if(stype == CROSS_ENTROPY2)
		score = trainNG.getProb() * Math.log(testNG.getProb());
	    else if(stype == ABS_CROSS_ENTROPY1)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, CROSS_ENTROPY1) );
            else if(stype == ABS_CROSS_ENTROPY2)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, CROSS_ENTROPY2) );
            else if(stype == REL_ENTROPY1)
//                score = trainNG.getProb() * (Math.log(trainNG.getProb()) / testNG.getProb());
                score = trainNG.getProb() * (Math.log(trainNG.getProb()) / Math.log(testNG.getProb()));
//                score = trainNG.getProb() * calcMatchScore(trainNG, testNG, idtype, LOG_PROB_DIFF1);
            else if(stype == REL_ENTROPY2)
//                score = testNG.getProb() * (Math.log(testNG.getProb()) / trainNG.getProb());
                score = testNG.getProb() * (Math.log(testNG.getProb()) / Math.log(trainNG.getProb()));
//                score = testNG.getProb() * calcMatchScore(testNG, trainNG, idtype, LOG_PROB_DIFF1);
            else if(stype == ABS_REL_ENTROPY1)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, REL_ENTROPY1) );
            else if(stype == ABS_REL_ENTROPY2)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, REL_ENTROPY2) );
            else if(stype == MUTUAL_REL_ENTROPY)
                score = calcMatchScore(trainNG, testNG, idtype, REL_ENTROPY1) + calcMatchScore(trainNG, testNG, idtype, REL_ENTROPY2);
            else if(stype == ABS_MUTUAL_REL_ENTROPY)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, MUTUAL_REL_ENTROPY) );
            else if(stype == MUTUAL_CROSS_ENTROPY)
                score = calcMatchScore(trainNG, testNG, idtype, CROSS_ENTROPY1) + calcMatchScore(trainNG, testNG, idtype, CROSS_ENTROPY2);
            else if(stype == ABS_MUTUAL_CROSS_ENTROPY)
		score = Math.abs( calcMatchScore(trainNG, testNG, idtype, MUTUAL_CROSS_ENTROPY) );
            else if(stype == CAVNAR_TRENKLE)
		score = (double) Math.abs( trainNG.getRank() - testNG.getRank() );
//              System.out.println("\t\t*Returned score: " + score);
        }
//        else // default
//        {
//            if(stype == JOINT_PROB)
//                score = trainNG.getRelevanceProb() * testNG.getProb();
//            else if(stype == REL_ENTROPY)
//                score = testNG.getProb() * ( Math.log( testNG.getProb() / trainNG.getProb() ) );
//        }
        
//      System.out.println("\t\tReturned score: " + score);
        return score;
    }
    
    public static String getScoreTypeString(int stype)
    {
	switch(stype)
	{
	    case JOINT_PROB:
		return "JOINT_PROB";
	    case LOG_JOINT_PROB:
		return "LOG_JOINT_PROB";
	    case ABS_LOG_JOINT_PROB:
		return "ABS_LOG_JOINT_PROB";
	    case PROB_DIFF1:
		return "PROB_DIFF1";
	    case PROB_DIFF2:
		return "PROB_DIFF2";
	    case ABS_PROB_DIFF:
		return "ABS_PROB_DIFF";
	    case LOG_PROB_DIFF1:
		return "LOG_PROB_DIFF1";
	    case LOG_PROB_DIFF2:
		return "LOG_PROB_DIFF2";
	    case ABS_LOG_PROB_DIFF:
		return "ABS_LOG_PROB_DIFF";
	    case CROSS_ENTROPY1:
		return "CROSS_ENTROPY1";
	    case CROSS_ENTROPY2:
		return "CROSS_ENTROPY2";
	    case ABS_CROSS_ENTROPY1:
		return "ABS_CROSS_ENTROPY1";
	    case ABS_CROSS_ENTROPY2:
		return "ABS_CROSS_ENTROPY2";
	    case REL_ENTROPY1:
		return "REL_ENTROPY1";
	    case REL_ENTROPY2:
		return "REL_ENTROPY2";
	    case ABS_REL_ENTROPY1:
		return "ABS_REL_ENTROPY1";
	    case ABS_REL_ENTROPY2:
		return "ABS_REL_ENTROPY2";
	    case MUTUAL_REL_ENTROPY:
		return "MUTUAL_REL_ENTROPY";
	    case ABS_MUTUAL_REL_ENTROPY:
		return "ABS_MUTUAL_REL_ENTROPY";
	    case MUTUAL_CROSS_ENTROPY:
		return "MUTUAL_CROSS_ENTROPY";
	    case DEKANG_LIN:
		return "DEKANG_LIN";
	    case ABS_DEKANG_LIN:
		return "ABS_DEKANG_LIN";
	    case JIANG_CONRATH:
		return "JIANG_CONRATH";
	    case ABS_JIANG_CONRATH:
		return "ABS_JIANG_CONRATH";
	    case CAVNAR_TRENKLE:
		return "CAVNAR_TRENKLE";
	}
	
	return null;
    }
    
    public static int getScoreType(String stypeString)
    {
	int stype = 0;
	
	if(stypeString.equalsIgnoreCase("JOINT_PROB"))
	    return JOINT_PROB;
	else if(stypeString.equalsIgnoreCase("LOG_JOINT_PROB"))
	    return LOG_JOINT_PROB;
	else if(stypeString.equalsIgnoreCase("ABS_LOG_JOINT_PROB"))
	    return ABS_LOG_JOINT_PROB;
	else if(stypeString.equalsIgnoreCase("PROB_DIFF1"))
	    return PROB_DIFF1;
	else if(stypeString.equalsIgnoreCase("PROB_DIFF2"))
	    return PROB_DIFF2;
	else if(stypeString.equalsIgnoreCase("ABS_PROB_DIFF"))
	    return ABS_PROB_DIFF;
	else if(stypeString.equalsIgnoreCase("LOG_PROB_DIFF1"))
	    return LOG_PROB_DIFF1;
	else if(stypeString.equalsIgnoreCase("LOG_PROB_DIFF2"))
	    return LOG_PROB_DIFF2;
	else if(stypeString.equalsIgnoreCase("ABS_LOG_PROB_DIFF"))
	    return ABS_LOG_PROB_DIFF;
	else if(stypeString.equalsIgnoreCase("CROSS_ENTROPY1"))
	    return CROSS_ENTROPY1;
	else if(stypeString.equalsIgnoreCase("CROSS_ENTROPY2"))
	    return CROSS_ENTROPY2;
	else if(stypeString.equalsIgnoreCase("ABS_CROSS_ENTROPY1"))
	    return ABS_CROSS_ENTROPY1;
	else if(stypeString.equalsIgnoreCase("ABS_CROSS_ENTROPY2"))
	    return ABS_CROSS_ENTROPY2;
	else if(stypeString.equalsIgnoreCase("REL_ENTROPY1"))
	    return REL_ENTROPY1;
	else if(stypeString.equalsIgnoreCase("REL_ENTROPY2"))
	    return REL_ENTROPY2;
	else if(stypeString.equalsIgnoreCase("ABS_REL_ENTROPY1"))
	    return ABS_REL_ENTROPY1;
	else if(stypeString.equalsIgnoreCase("ABS_REL_ENTROPY2"))
	    return ABS_REL_ENTROPY2;
	else if(stypeString.equalsIgnoreCase("MUTUAL_REL_ENTROPY"))
	    return MUTUAL_REL_ENTROPY;
	else if(stypeString.equalsIgnoreCase("ABS_MUTUAL_REL_ENTROPY"))
	    return ABS_MUTUAL_REL_ENTROPY;
	else if(stypeString.equalsIgnoreCase("MUTUAL_CROSS_ENTROPY"))
	    return MUTUAL_CROSS_ENTROPY;
	else if(stypeString.equalsIgnoreCase("ABS_MUTUAL_CROSS_ENTROPY"))
	    return ABS_MUTUAL_CROSS_ENTROPY;
	else if(stypeString.equalsIgnoreCase("DEKANG_LIN"))
	    return DEKANG_LIN;
	else if(stypeString.equalsIgnoreCase("ABS_DEKANG_LIN"))
	    return ABS_DEKANG_LIN;
	else if(stypeString.equalsIgnoreCase("JIANG_CONRATH"))
	    return JIANG_CONRATH;
	else if(stypeString.equalsIgnoreCase("ABS_JIANG_CONRATH"))
	    return ABS_JIANG_CONRATH;
	else if(stypeString.equalsIgnoreCase("CAVNAR_TRENKLE"))
	    return CAVNAR_TRENKLE;
	
	return stype;
    }

    public void calcLangDistance()
    {
    	Iterator enm = getNGramLMKeys();
    	String modelKey = "";
    	
    	while(enm.hasNext())
        {
            modelKey = (String) enm.next();
            String splitstrs [] = modelKey.split("-");
	    try {
		if(isInMemoryModels())
		    trainingModel = (NGramLM) trainingModels.get(modelKey);
		else
                {
                    if(featureNGrams)
                        trainingModel = loadNGramLM(modelKey, "uchar");
                    else
                        trainingModel = loadNGramLM(modelKey, "uchar");//char
//		    trainingModel = loadNGramLMArpa(modelKey, "char");
                }
				    
		if(useWordNGram)
		{
		    if(isInMemoryModels())
			wrdTrainingModel = (NGramLM) wrdTrainingModels.get(modelKey);
		    else
			wrdTrainingModel = loadNGramLM(modelKey, "word");
//			wrdTrainingModel = loadNGramLMArpa(modelKey, "word");
		}
	    } catch (FileNotFoundException ex) {
		ex.printStackTrace();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    } catch (ClassNotFoundException ex) {
		ex.printStackTrace();
	    }
	    LinkedHashMap list = identify(trainingModel,null,null);
	    Iterator itr = list.keySet().iterator();
//	    System.out.println(modelKey +"\t");
        while (itr.hasNext())
        {
            Object o = itr.next();
//            System.out.print(modelKey + "_" +o.toString());
            Double score = (Double) list.get(o);
            //double norm_score = score.doubleValue() / max;
            if(o.toString().equalsIgnoreCase(modelKey) == false)
            {
            	System.out.print(modelKey +"\t"+o.toString()+"->" + score + "\t"+Math.exp(score)+"\n");
            }
        }
        }
    }
    
    public void calcLangDistance(String tstdpaths)  throws FileNotFoundException, IOException
    {
//      System.out.println("Starting evaluation...");

        setTestingDataPaths(new KeyValueProperties());
        getTestingDataPaths().read(tstdpaths, GlobalProperties.getIntlString("UTF-8"));
 
//        System.out.println("Testing data paths:");
//        testingDataPaths.print(System.out);

        Iterator enm = getTestingDataPaths().getPropertyKeys();

        while(enm.hasNext())
        {
            String k = (String) enm.next();
            String v = getTestingDataPaths().getPropertyValue(k);

            File file = new File(k);
            LinkedHashMap list =  identify(file);
            Iterator itr = list.keySet().iterator();
            
            //Object od = itr.next();
            //System.out.print(v + "_" +od.toString());
            //Double scoreMax = (Double) list.get(od);
            //double max = scoreMax.doubleValue();
            while (itr.hasNext())
            {
                Object o = itr.next();
                System.out.print(v + "_" +o.toString());
                Double score = (Double) list.get(o);
                //double norm_score = score.doubleValue() / max;
                System.out.print("->" + score + "\n");
            }
        }
    }

    public static void main(String[] args) {
        try {
	    int scoreType = LangEncIdentifier.MUTUAL_CROSS_ENTROPY;
	    
	    if(args.length > 0)
		scoreType = LangEncIdentifier.getScoreType(args[0]);
	    
//	    String trainPath = GlobalProperties.resolveRelativePath("data/cognate-classification/cognate-classification-hindi-train.txt");
	    String trainPath = GlobalProperties.resolveRelativePath("data/enc-lang-identifier/enc-lang-identify-train-small.txt");

	    if(args.length > 1)
		trainPath = args[1];

//	    String testPath = GlobalProperties.resolveRelativePath("data/cognate-classification/cognate-classification-hindi-test.txt");
	    String testPath = GlobalProperties.resolveRelativePath("data/enc-lang-identifier/enc-lang-identify-test-small.txt");
//	    String testPath = GlobalProperties.resolveRelativePath("data/enc-lang-identifier/testing/Hindi-UTF8");

	    if(args.length > 2)
		testPath = args[2];
	    
	    boolean useStoredModels = false;

	    if(args.length > 3)
		useStoredModels = Boolean.parseBoolean(args[3]);
	    
	    boolean useWrdModels = false;

	    if(args.length > 4)
		useWrdModels = Boolean.parseBoolean(args[4]);
	    
	    boolean inMemory = true;

	    if(args.length > 5)
		inMemory = Boolean.parseBoolean(args[5]);
	    
	    int numAmbiguous = 5;

	    if(args.length > 6)
		numAmbiguous = Integer.parseInt(args[6]);
	    
	    int charNGrams = 3000;

	    if(args.length > 7)
		charNGrams = Integer.parseInt(args[7]);
	    
	    int wordNGrams = 500;

	    if(args.length > 8)
		wordNGrams = Integer.parseInt(args[8]);
	    
	    double wordNGramsWeight = 1.0;

	    if(args.length > 9)
		wordNGramsWeight = Double.parseDouble(args[9]);

	    int charNGramOrder = 7;

	    if(args.length > 10)
		charNGramOrder = Integer.parseInt(args[10]);
	    
	    int wordNGramOrder = 3;

	    if(args.length > 11)
		wordNGramOrder = Integer.parseInt(args[11]);
	    
	    boolean featureNGrams = false;

	    if(args.length > 12)
		featureNGrams = Boolean.parseBoolean(args[12]);
	    
	    /*
	     * Best configs (for MCE with all test data):
	     * 1. 100% for byte n-grams 1000, without word unigrams (without Norwegian)
	     * 2. 99.876 for byte n-grams 1000, without word unigrams (with Norwegian)
	     * 2. 99.876 for byte n-grams 1000, with 500 word unigrams, 5 numAmbiguous, weight 2.0) (with Norwegian)
	     *
	     *
	     *
	     */
	    
	    System.err.println(GlobalProperties.getIntlString("Options:"));
	    System.err.println("\tscoreType: " + LangEncIdentifier.getScoreTypeString(scoreType));
	    System.err.println("\ttrainPath: " + trainPath);
	    System.err.println("\ttestPath: " + testPath);
	    System.err.println("\tuseStoredModels: " + useStoredModels);
	    System.err.println("\tuseWrdModels: " + useWrdModels);
	    System.err.println("\tinMemory: " + inMemory);
	    System.err.println("\tnumAmbiguous: " + numAmbiguous);
	    System.err.println("\tcharNGrams: " + charNGrams);
	    System.err.println("\twordNGrams: " + wordNGrams);
	    System.err.println("\twordNGramsWeight: " + wordNGramsWeight);
	    System.err.println("\tcharNGramOrder: " + charNGramOrder);
	    System.err.println("\twordNGramOrder: " + wordNGramOrder);
	    System.err.println("-------------------------------------");
		
            LangEncIdentifier idfr = new LangEncIdentifier(trainPath,
		    LangEncIdentifier.FREQ_IDENTIFIER, scoreType, charNGrams, useStoredModels,
		    useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight,
		    charNGramOrder, wordNGramOrder);
//            LangEncIdentifier idfr = new LangEncIdentifier(trainPath,
//		    LangEncIdentifier.FREQ_IDENTIFIER, scoreType, charNGrams, useStoredModels,
//		    useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight,
//		    charNGramOrder, wordNGramOrder, featureNGrams);
//            EncLangIdentifier idfr = new EncLangIdentifier(GlobalProperties.resolveRelativePath("props/topic-identify-train.txt"), EncLangIdentifier.FREQ_IDENTIFIER, EncLangIdentifier.JOINT_PROB, 1000, false);

            idfr.train();
            idfr.evaluate(testPath);
//            idfr.identifyBatch(new File(testPath), false);
//            idfr.evaluateLineWise(testPath);
            ////////////////////////////////
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
//    public static void main(String[] args) {
//        try {
//	    int scoreType = LangEncIdentifier.MUTUAL_CROSS_ENTROPY;
//	    
//	    if(args.length > 0)
//		scoreType = LangEncIdentifier.getScoreType(args[0]);
//	    
//	    String trainPath = "/home/eklavya/docs/papers/ner-name-structure/CharNER/Hindi/trainInfoNorm";
////	    String trainPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-train.txt";
//
//	    if(args.length > 1)
//		trainPath = args[1];
//
//	    String testPath = "/home/eklavya/docs/papers/ner-name-structure/CharNER/Hindi/trainInfoNorm";
////	    String testPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-test.txt";
//
//	    if(args.length > 2)
//		testPath = args[2];
//	    
//	    boolean useStoredModels = false;
//
//	    if(args.length > 3)
//		useStoredModels = Boolean.parseBoolean(args[3]);
//	    
//	    boolean useWrdModels = false;
//
//	    if(args.length > 4)
//		useWrdModels = Boolean.parseBoolean(args[4]);
//	    
//	    boolean inMemory = true;
//
//	    if(args.length > 5)
//		inMemory = Boolean.parseBoolean(args[5]);
//	    
//	    int numAmbiguous = 5;
//
//	    if(args.length > 6)
//		numAmbiguous = Integer.parseInt(args[6]);
//	    
//	    int charNGrams = 3500;
//
//	    if(args.length > 7)
//		charNGrams = Integer.parseInt(args[7]);
//	    
//	    int wordNGrams = 500;
//
//	    if(args.length > 8)
//		wordNGrams = Integer.parseInt(args[8]);
//	    
//	    double wordNGramsWeight = 1.0;
//
//	    if(args.length > 9)
//		wordNGramsWeight = Integer.parseInt(args[9]);
//
//	    int charNGramOrder = 5;
//
//	    if(args.length > 10)
//		charNGramOrder = Integer.parseInt(args[10]);
//	    
//	    int wordNGramOrder = 3;
//
//	    if(args.length > 11)
//		wordNGramOrder = Integer.parseInt(args[11]);
//	    
//	    boolean featureNGrams = true;
//
//	    if(args.length > 12)
//		featureNGrams = Boolean.parseBoolean(args[12]);
//	    
//	    /*
//	     * Best configs (for MCE with all test data):
//	     * 1. 100% for byte n-grams 1000, without word unigrams (without Norwegian)
//	     * 2. 99.876 for byte n-grams 1000, without word unigrams (with Norwegian)
//	     * 2. 99.876 for byte n-grams 1000, with 500 word unigrams, 5 numAmbiguous, weight 2.0) (with Norwegian)
//	     *
//	     *
//	     *
//	     */
//	    
//	    System.err.println("Options:");
//	    System.err.println("\tscoreType: " + LangEncIdentifier.getScoreTypeString(scoreType));
//	    System.err.println("\ttrainPath: " + trainPath);
//	    System.err.println("\ttestPath: " + testPath);
//	    System.err.println("\tuseStoredModels: " + useStoredModels);
//	    System.err.println("\tuseWrdModels: " + useWrdModels);
//	    System.err.println("\tinMemory: " + inMemory);
//	    System.err.println("\tnumAmbiguous: " + numAmbiguous);
//	    System.err.println("\tcharNGrams: " + charNGrams);
//	    System.err.println("\twordNGrams: " + wordNGrams);
//	    System.err.println("\twordNGramsWeight: " + wordNGramsWeight);
//	    System.err.println("\tcharNGramOrder: " + charNGramOrder);
//	    System.err.println("\twordNGramOrder: " + wordNGramOrder);
//	    System.err.println("-------------------------------------");
//		
//            LangEncIdentifier idfr = new LangEncIdentifier(trainPath,
//		    LangEncIdentifier.FREQ_IDENTIFIER, scoreType, charNGrams, useStoredModels,
//		    useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight,
//		    charNGramOrder, wordNGramOrder, featureNGrams);
////            EncLangIdentifier idfr = new EncLangIdentifier("/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-train.txt", EncLangIdentifier.FREQ_IDENTIFIER, EncLangIdentifier.JOINT_PROB, 1000, false);
//
//            idfr.train();
//            System.out.println("Trained");
//           
//            LinkedHashMap a = idfr.identify("#SURANA$");
//            /*
//            System.out.println(a.toString());
//            a = idfr.identify("#KAILASH$");
//            System.out.println(a.toString());
//            a = idfr.identify("#JOE$");
//            a = idfr.identify("#IMPETEOUSLY$");
//            System.out.println(a.toString());
//            a = idfr.identify("#RAMESHWARAM$");
//            System.out.println(a.toString());
//            a = idfr.identify("#RAMSAY$");
//            System.out.println(a.toString());
//            a = idfr.identify("#STATION$");
//            System.out.println(a.toString());
//            a = idfr.identify("#GEORGE$");
//            System.out.println(a.toString());
//            a = idfr.identify("#AISHWARYA$");
//            System.out.println(a.toString());
//            a = idfr.identify("#COMPUTER$");
//            System.out.println(a.toString());
//
//            a = idfr.identify("#BANARJEE$");
//            System.out.println(a.toString());
//            a = idfr.identify("#BAGHDAD$");
//            System.out.println(a.toString());
//            a = idfr.identify("#LAHORE$");
//            System.out.println(a.toString());
//            a = idfr.identify("#BANGALORE$");
//            System.out.println(a.toString());
//            a = idfr.identify("#BOMBAY$");
//            System.out.println(a.toString());
//            */
//            //////////////////////////////// TRANSLITERATE PAPER
//            int lineCount = 0;
//        		try 
//        		{
//        			BufferedReader lnReader = null;
//        			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/eklavya/docs/papers/ner-name-structure/CharNER/Hindi/NEP.utf8.test"), "UTF-8"));
//        			String line;
//        			//FileWriter writer= new FileWriter ( new File(outFile));
//        			//list = new Vector();
//        			while((line = lnReader.readLine()) != null)
//        			{
//        				lineCount ++;
//        				String wordId = line;
//        				//wordId = "#" + wordId.toUpperCase() + "$";
//        				a = idfr.identify(wordId);
//        				System.out.println(a.toString() + "\t" + wordId);
//        				
//        			}
//        		}
//        		catch (Exception ex)
//        		{
//        			ex.printStackTrace();
//        		}
//        		
//
//            ////////////////////////////////
//            
//            
////            idfr.evaluate(testPath);
//            
////            idfr.evaluate("/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-test.txt");
//            
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    
//    public static void main(String[] args) {
//        try {
//	    int scoreType = LangEncIdentifier.MUTUAL_CROSS_ENTROPY;
//	    
//	    if(args.length > 0)
//		scoreType = LangEncIdentifier.getScoreType(args[0]);
//	    
//	    String trainPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/enc-lang-identify-diachronic-train.txt";
////	    String trainPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/enc-lang-identify-train.txt";
////	    String trainPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-train.txt";
//
//	    if(args.length > 1)
//		trainPath = args[1];
//
//	    String testPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/enc-lang-identify-diachronic-test.txt";
////	    String testPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/enc-lang-identify-language-distance.txt";
////	    String testPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-test.txt";
//
//	    if(args.length > 2)
//		testPath = args[2];
//	    
//	    boolean useStoredModels = false;
//
//	    if(args.length > 3)
//		useStoredModels = Boolean.parseBoolean(args[3]);
//	    
//	    boolean useWrdModels = false;
//
//	    if(args.length > 4)
//		useWrdModels = Boolean.parseBoolean(args[4]);
//	    
//	    boolean inMemory = true;
//
//	    if(args.length > 5)
//		inMemory = Boolean.parseBoolean(args[5]);
//	    
//	    int numAmbiguous = 5;
//
//	    if(args.length > 6)
//		numAmbiguous = Integer.parseInt(args[6]);
//	    
//	    int charNGrams = 2000;
//
//	    if(args.length > 7)
//		charNGrams = Integer.parseInt(args[7]);
//	    
//	    int wordNGrams = 500;
//
//	    if(args.length > 8)
//		wordNGrams = Integer.parseInt(args[8]);
//	    
//	    double wordNGramsWeight = 1.0;
//
//	    if(args.length > 9)
//		wordNGramsWeight = Integer.parseInt(args[9]);
//
//	    int charNGramOrder = 5;
//
//	    if(args.length > 10)
//		charNGramOrder = Integer.parseInt(args[10]);
//	    
//	    int wordNGramOrder = 3;
//
//	    if(args.length > 11)
//		wordNGramOrder = Integer.parseInt(args[11]);
//	    
//	    /*
//	     * Best configs (for MCE with all test data):
//	     * 1. 100% for byte n-grams 1000, without word unigrams (without Norwegian)
//	     * 2. 99.876 for byte n-grams 1000, without word unigrams (with Norwegian)
//	     * 2. 99.876 for byte n-grams 1000, with 500 word unigrams, 5 numAmbiguous, weight 2.0) (with Norwegian)
//	     *
//	     *
//	     *
//	     */
//	    
//	    System.err.println("Options:");
//	    System.err.println("\tscoreType: " + LangEncIdentifier.getScoreTypeString(scoreType));
//	    System.err.println("\ttrainPath: " + trainPath);
//	    System.err.println("\ttestPath: " + testPath);
//	    System.err.println("\tuseStoredModels: " + useStoredModels);
//	    System.err.println("\tuseWrdModels: " + useWrdModels);
//	    System.err.println("\tinMemory: " + inMemory);
//	    System.err.println("\tnumAmbiguous: " + numAmbiguous);
//	    System.err.println("\tcharNGrams: " + charNGrams);
//	    System.err.println("\twordNGrams: " + wordNGrams);
//	    System.err.println("\twordNGramsWeight: " + wordNGramsWeight);
//	    System.err.println("\tcharNGramOrder: " + charNGramOrder);
//	    System.err.println("\twordNGramOrder: " + wordNGramOrder);
//	    System.err.println("-------------------------------------");
//		
//            LangEncIdentifier idfr = new LangEncIdentifier(trainPath,
//		    LangEncIdentifier.FREQ_IDENTIFIER, scoreType, charNGrams, useStoredModels,
//		    useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight,
//		    charNGramOrder, wordNGramOrder);
////            EncLangIdentifier idfr = new EncLangIdentifier("/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-train.txt", EncLangIdentifier.FREQ_IDENTIFIER, EncLangIdentifier.JOINT_PROB, 1000, false);
//
//            idfr.train();
//            
//            //idfr.evaluate(testPath);
//            idfr.calcLangDistance(testPath);
////            idfr.evaluate("/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-test.txt");
//            
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    
    class ModelScore
    {
	public String modelKey;
	public Double modelScore;
	
	public ModelScore(String modelKey, Double modelScore)
	{
	    this.modelKey = modelKey;
	    this.modelScore = modelScore;
	}
    }
    
/*    class MatchScoreArgs
    {	
        public int i; // Train model index
//        public int j; // Train model order
        public int k; // Test model index
//        public int l; // Test model order
        
        public Vector trainModel; // Sorted NGrams for order j
        public Vector testModel; // Sorted NGrams for order l
    }*/
}
