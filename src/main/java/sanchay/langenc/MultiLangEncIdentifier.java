/*
 * MultiEncLangIdentifier.java
 *
 * Created on January 27, 2007, 5:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.langenc;

import sanchay.mlearning.lm.ngram.NGramLM;
import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;
import sanchay.mlearning.lm.ngram.*;
import sanchay.mlearning.lm.ngram.impl.*;
import sanchay.properties.KeyValueProperties;
import sanchay.util.UTFIscii;


public class MultiLangEncIdentifier {
    
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
    
    private int identifierType;
    private int scoreType;
    
    private int charNGramOrder;
    private int wordNGramOrder;
    
    private int charPruneRank;
    private int wordPruneRank;
    private int pruneFreq;
    
    private boolean useTmodel = true;
    private int topLangEncs; // For monolingual document/string
    private int enumLangEncs; // For multilingual document/string
    private int numAmbiguous;
    
    private double wrdModelWeight;
    
    // Character n-gram models
    private NGramLM trainingModel;
    private NGramLM knTmodel;
    private NGramLM testModel;
    
    // Word n-gram models
    private NGramLM wrdTrainingModel;
    private NGramLM wrdTestModel;
    
    private KeyValueProperties trainingDataPaths; // enc-lang is the key, path is the value
    private KeyValueProperties testingDataPaths; //  path is the key, enc-lang is the value
    
    // Parallel vectors
    Vector paths = new Vector(0, 5);
    
    // In case of default identification, each entry will be a String (langEnc)
    // For multi-lingual identification, each entry will be a LinkedHashMap (enumerated langEncs and their scores/likelihoods)
    Vector actual_enclangs = new Vector(0, 5);
    Vector identified_enclangs = new Vector(0, 5);
    Vector actual_word_enclags = new Vector(0,5);
    Vector Tokens = new Vector(0,5);
    
    
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
    
    boolean useStoredTrainingLMs;
    boolean useStoredUniqueNGrams;
    boolean useWordNGram;
    boolean inMemoryModels;
    
    long maxTrainingDataSize;
    long minTrainingDataSize;
    long totalTrainingDataSize;
    
    long maxTestingDataSize;
    long minTestingDataSize;
    long totalTestingDataSize;
    
    long maxTrainingDataSizeWrd;
    long minTrainingDataSizeWrd;
    long totalTrainingDataSizeWrd;
    
    long maxTestingDataSizeWrd;
    long minTestingDataSizeWrd;
    long totalTestingDataSizeWrd;
    
    // Temp
    Hashtable allScores;
    Hashtable trainingModels;
    Hashtable enumeratedTrainingModels;
    Hashtable wrdTrainingModels;
    Hashtable enumeratedWrdTrainingModels;
    //LinkedHashMap NoLanc;
    Hashtable NoLanc ;// = new Hashtable(0,5);
    Hashtable toplanc = new Hashtable(0,5);
    LinkedHashMap Confusion = new LinkedHashMap(0,5);
    
    String uniqueNGramsFile;
    LinkedHashMap<List<Integer>, List<Integer>> uniqueNGrams;
    //Hashtable Idlncs = new Hashtable(0,5);
    LinkedHashMap Idlncs = new LinkedHashMap(0,5);
    // LinkedHashMap toplanc = new LinkedHashMap();
    public int correct1 ;
    public int nofile ;
    public double precision1;
    boolean Tag = false;
    public int WordTagCnt;
    public int TotWrds;
    public double wrdPrecition;
    public int Pk;
    public int qk;
    public int km;
    /**
     *
     */
    public MultiLangEncIdentifier() {
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
    
    public MultiLangEncIdentifier(boolean useStoredTrainingLMs) {
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
    
//     fill lencs
//
//    // Sort
//    Arrays.sort(lencs);
//
//    // Creating
//    KeyValueProperties kvp = new KeyValueProperties();
//    kvp.addProperty(key, val);
//
//    kvp.save("file", "UTF-8");
//
//    // Reading
//    kvp.read("file", "UTF-8");
//
//    Enumeration enm = kvp.getPropertyKeys();
//    String val = kvp.getPropertyValue(key)
//
//    String lencs = new int[2];
    
    public MultiLangEncIdentifier(String trndpaths, int type, int st, int charNGrams,
            boolean useStoredTrainingLMs, boolean useWordNGram,
            boolean inMemory, int numAmbiguous, int wordNGrams, double wordNGramsWeight,
            int charNGramOrder, int wordNGramOrder) throws FileNotFoundException, IOException {
        trainingDataPaths = new KeyValueProperties();
        trainingDataPaths.read(trndpaths, GlobalProperties.getIntlString("UTF-8"));
        
//        System.out.println("Training data paths:");
        trainingDataPaths.print(System.out);
        
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
        
        topLangEncs = 4;
        enumLangEncs =4;
        int correct1 = 0;
        int nofile = 0;
        double precision1 = 0;
        int WordTagCnt = 0;
        int TotWrds = 0;
        double wrdPrecition = 0.0;
        
        uniqueNGramsFile = GlobalProperties.resolveRelativePath("props/uniqueNGrams");
        uniqueNGrams = new LinkedHashMap<List<Integer>, List<Integer>>(0, 5);
        useStoredUniqueNGrams = false;
    }
    
    public int getIdentifierType() {
        return identifierType;
    }
    
    public void setIdentifierType(int type) {
        identifierType = type;
    }
    
    /**
     * @return Returns the charPruneRank.
     */
    public int getCharPruneRank() {
        return charPruneRank;
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
    
    public int countNGramLMs() {
        return trainingDataPaths.countProperties();
    }
    
    public Enumeration getNGramLMKeys() {
        //return trainingDataPaths.getPropertyKeys();
        
        if(useTmodel == false)
            return enumeratedTrainingModels.keys();
        else
            return trainingModels.keys();
    }
    
    public String getNGramLMPath(String k /* key */) {
        return trainingDataPaths.getPropertyValue(k);
    }
    
    public NGramLM getNGramLM(String k /* key */) {
        
        if(useTmodel == false)
            return (NGramLM) enumeratedTrainingModels.get(k);
        else
            return (NGramLM) trainingModels.get(k);
    }
    
    public NGramLM getNGramLM(String k /* key */, boolean all) {
        
        if(all)
            return (NGramLM) trainingModels.get(k);
        
        return (NGramLM) enumeratedTrainingModels.get(k);
    }
    
    public int addNGramLM(String k /* key */, String lm /* file/directory path */) {
        trainingDataPaths.addProperty(k, lm);
        return countNGramLMs();
    }
    
    public String removeNGramLM(String k /* key */) {
        return trainingDataPaths.removeProperty(k);
    }
    
    public NGramLM makeNGramLM(String k /* key */, String type) throws FileNotFoundException, IOException {
        File nf = new File(trainingDataPaths.getPropertyValue(k));
        NGramLM nglm = null;
        
//	String parts[] = k.split("[::]");
//
//	if(parts == null && parts.length != 2)
//	{
//	    System.out.println("Wrong language-encoding pair: " + k);
//	    return null;
//	}
        
        String charset = GlobalProperties.getIntlString("ISO-8859-1");
        
        if(type.equalsIgnoreCase("word")) {
//	    if(parts[1].equalsIgnoreCase("UTF-8") || parts[1].equalsIgnoreCase("UTF-8"))
            charset = GlobalProperties.getIntlString("UTF-8");
        }
        
        String lang = GlobalProperties.getIntlString("hin::utf8");
        
        if(type.equalsIgnoreCase("char"))
            nglm = new NGramLMImpl(nf, type, charNGramOrder, charset, lang);
        else if(type.equalsIgnoreCase("word"))
            nglm = new NGramLMImpl(nf, type, wordNGramOrder, charset, lang);
        
        nglm.makeNGramLM((File) null);
        
        long trainingDataSize = nglm.countTokens(1);
        
        if(type.equalsIgnoreCase("char")) {
            if(minTrainingDataSize > trainingDataSize)
                minTrainingDataSize = trainingDataSize;
            
            if(maxTrainingDataSize < trainingDataSize)
                maxTrainingDataSize = trainingDataSize;
            
            totalTrainingDataSize += trainingDataSize;
        } else if(type.equalsIgnoreCase("word")) {
            if(minTrainingDataSizeWrd > trainingDataSize)
                minTrainingDataSizeWrd = trainingDataSize;
            
            if(maxTrainingDataSizeWrd < trainingDataSize)
                maxTrainingDataSizeWrd = trainingDataSize;
            
            totalTrainingDataSizeWrd += trainingDataSize;
        }
        
        if(type.equalsIgnoreCase("char"))
            nglm.pruneByRank(charPruneRank, 0);
        else if(type.equalsIgnoreCase("word"))
            nglm.pruneByRank(wordPruneRank, 0);
        
        storeNGramLM(nglm, k, type);
//	storeNGramLMArpa(nglm, k, type);
        
        return nglm;
    }
    
    public NGramLM loadNGramLM(String k /* key */, String type) throws FileNotFoundException, IOException, ClassNotFoundException {
        File nf = null;
        
        if(type.equalsIgnoreCase("char"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".nglmc");
        else if(type.equalsIgnoreCase("word"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".nglmw");
        
        NGramLM nGramLM = null;
        
        if(nf != null && nf.exists())
            nGramLM = NGramLMImpl.loadNGramLMBinary(nf);
        
        // Just for filling ranks
        nGramLM.pruneByRank(-1, 0);
        
        return nGramLM;
    }
    
    public NGramLM loadNGramLMArpa(String k /* key */, String type) throws FileNotFoundException, IOException, ClassNotFoundException {
        File nf = null;
        
        if(type.equalsIgnoreCase("char"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".arpac");
        else if(type.equalsIgnoreCase("word"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".arpaw");
        
        String charset = GlobalProperties.getIntlString("ISO-8859-1");
        
        if(type.equalsIgnoreCase("word")) {
//	    if(parts[1].equalsIgnoreCase("UTF-8") || parts[1].equalsIgnoreCase("UTF-8"))
            charset = GlobalProperties.getIntlString("UTF-8");
        }
        
        String lang = GlobalProperties.getIntlString("hin::utf8");
        
        NGramLM nGramLM = null;
        
        if(nf != null && nf.exists()) {
            if(type.equalsIgnoreCase("char"))
                nGramLM = NGramLMImpl.loadNGramLMArpa(nf, type, charNGramOrder, charset, lang);
            else if(type.equalsIgnoreCase("word"))
                nGramLM = NGramLMImpl.loadNGramLMArpa(nf, type, wordNGramOrder, charset, lang);
        }
        
        // Just for filling ranks
        nGramLM.pruneByRank(-1, 0);
        
        return nGramLM;
    }
    
    public void storeNGramLM(NGramLM nglm, String k /* key */, String type) throws FileNotFoundException, IOException {
        File nf = null;
        
        if(type.equalsIgnoreCase("char"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".nglmc");
        else if(type.equalsIgnoreCase("word"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".nglmw");
        
        if(nf != null)
            NGramLMImpl.storeNGramLM(nglm, nf);
    }
    
    public void storeNGramLMArpa(NGramLM nglm, String k /* key */, String type) throws FileNotFoundException, IOException {
        File nf = null;
        
        if(type.equalsIgnoreCase("char"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".arpac");
        else if(type.equalsIgnoreCase("word"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".arpaw");
        
        String charset = GlobalProperties.getIntlString("ISO-8859-1");
        
        if(type.equalsIgnoreCase("word")) {
//	    if(parts[1].equalsIgnoreCase("UTF-8") || parts[1].equalsIgnoreCase("UTF-8"))
            charset = GlobalProperties.getIntlString("UTF-8");
        }
        
        if(nf != null)
            NGramLMImpl.storeNGramLMArpa(nglm, nf, charset, true);
    }
    
    public void loadUniqueNGrams() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(uniqueNGramsFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        uniqueNGrams = (LinkedHashMap<List<Integer>, List<Integer>>) ois.readObject();
        ois.close();
        
        Iterator<List<Integer>> itr = uniqueNGrams.keySet().iterator();
        
        int count = 0;
        while(itr.hasNext()) {
            List<Integer> key = itr.next();
            List<Integer> val = uniqueNGrams.get(key);
            
            count++;
//            System.out.println(key + "\t" + val);
        }
        
        System.out.println(GlobalProperties.getIntlString("Unique_count:_") + count);
    }
    
    public void storeUniqueNGrams() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream(uniqueNGramsFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        
        oos.writeObject(uniqueNGrams);
        oos.close();
    }
    
    public boolean nGramLMArpaExists(String k /* key */, String type) {
        File nf = null;
        
        if(type.equalsIgnoreCase("char"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".arpac");
        else if(type.equalsIgnoreCase("word"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".arpaw");
        
        if(nf != null && nf.exists())
            return true;
        
        return false;
    }
    
    public boolean nGramLMExists(String k /* key */, String type) {
        File nf = null;
        
        if(type.equalsIgnoreCase("char"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".nglmc");
        else if(type.equalsIgnoreCase("word"))
            nf = new File(trainingDataPaths.getPropertyValue(k) + ".nglmw");
        
        if(nf != null && nf.exists())
            return true;
        
        return false;
    }
    
    private void addUniqueNGrams(NGramLM trgModel, String le, Hashtable nonUnique) {
        int ord = trgModel.getNGramOrder();
        
        List<Integer> leIndices = NGramImpl.getIndices(trgModel, le, false);
        
        for (int i = 0; i < ord; i++) {
            Iterator<List<Integer>> itr = trgModel.getNGramKeys(i);
            
            if(itr != null) {
                while(itr.hasNext()) {
                    List<Integer> key = itr.next();
                    NGram ng = (NGram) trgModel.getNGram(key, i);
                    
                    if(nonUnique.containsKey(key)) {
                        continue;
                    } else if(uniqueNGrams.containsKey(key)) {
                        uniqueNGrams.remove(key);
                        nonUnique.put(key, le);
                    } else {
                        uniqueNGrams.put(key, leIndices);
                    }
                }
            }
        }
    }
    
    private int hasUniqueNGrams(NGramLM tstModel) {
        int ord = tstModel.getNGramOrder();
        
        int uniqueFound = 0;
        
        for (int i = 0; i < ord; i++) {
            Iterator<List<Integer>> itr = tstModel.getNGramKeys(i);
            
            if(itr != null) {
                while(itr.hasNext()) {
                    List<Integer> key = itr.next();
                    
                    if(uniqueNGrams.containsKey(key))
                        uniqueFound++;
                }
            }
        }
        
        return uniqueFound;
    }
    
    private String getLEByUniqueNGrams(NGramLM tstModel) {
        int ord = tstModel.getNGramOrder();
        
        int uniqueFound = 0;
        String le = null;
        
        for (int i = 0; i < ord; i++) {
            Iterator<List<Integer>> itr = tstModel.getNGramKeys(i);
            
            if(itr != null) {
                while(itr.hasNext()) {
                    List<Integer> key = itr.next();
                    List<Integer> val = uniqueNGrams.get(key);
                    
                    String valString = NGramImpl.getString(tstModel, val);
                    
                    if(uniqueNGrams.containsKey(key)) {
                        if(le == null || val.equals(le)) {
                            le = valString;
                            uniqueFound++;
                        } else
                            return null;
                    }
                }
            }
        }
        
        return le;
    }
    
    /**
     * Training is done for pairs of language and encoding. These pairs and their
     * corresponding directory names (which have the training files/directories)
     * are read from a properties file. The left column has the language and encoding
     * names separated by double colon (::). The right column gives the path.
     */
    public void train() {
//        System.out.println("Starting training...");
        
        Hashtable nonUnique = new Hashtable(0, 10);
        
        if(inMemoryModels) {
            trainingModels = new Hashtable(trainingDataPaths.countProperties());
            enumeratedTrainingModels = new Hashtable(topLangEncs);
            
            if(useWordNGram) {
                wrdTrainingModels = new Hashtable(trainingDataPaths.countProperties());
                enumeratedWrdTrainingModels = new Hashtable(topLangEncs);
            }
        }
        
        Iterator enm = trainingDataPaths.getPropertyKeys();
        
        while(enm.hasNext()) {
            String k = (String) enm.next();
            String v = trainingDataPaths.getPropertyValue(k);
            
            boolean nglmExists = nGramLMExists(k, "char");
            boolean wnglmExists = nGramLMExists(k, "word");
            
            try {
                if(useStoredTrainingLMs == false || nglmExists == false) {
                    trainingModel = makeNGramLM(k, "char");
                } else if(useStoredTrainingLMs && nglmExists && inMemoryModels)
                    trainingModel = loadNGramLM(k, "char");
//		    trainingModel = loadNGramLMArpa(k, "char");
                
                if(useStoredUniqueNGrams == false) {
                    addUniqueNGrams(trainingModel, k, nonUnique);
                }
                
                if(useWordNGram && (useStoredTrainingLMs == false || wnglmExists == false)) {
                    wrdTrainingModel = makeNGramLM(k, "word");
                } else if(useWordNGram && (useStoredTrainingLMs && nglmExists && inMemoryModels))
                    wrdTrainingModel = loadNGramLM(k, "word");
//		    wrdTrainingModel = loadNGramLMArpa(k, "word");
                
                if(inMemoryModels) {
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
        
        try {
            if(useStoredUniqueNGrams == false)
                storeUniqueNGrams();
            else
                loadUniqueNGrams();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void evaluate(String tstdpaths) throws FileNotFoundException, IOException {
//        System.out.println("Starting evaluation...");
        
        testingDataPaths = new KeyValueProperties();
        testingDataPaths.read(tstdpaths, GlobalProperties.getIntlString("UTF-8"));
        
//        System.out.println("Testing data paths:");
//        testingDataPaths.print(System.out);
        
        // Parallel vectors
        paths = new Vector(0, 5);
        actual_enclangs = new Vector(0, 5);
        identified_enclangs = new Vector(0, 5);
        
        int oldSize = -1;
        int newSize = -1;
        Iterator enm = testingDataPaths.getPropertyKeys();
        
        allScores = new Hashtable(0, 50);
        
        while(enm.hasNext()) {
            String k = (String) enm.next();
            String v = testingDataPaths.getPropertyValue(k);
            
            oldSize = paths.size();
            
            File file = new File(k);
            identifyBatch(file, false);
            newSize = paths.size();
            
            for(int i = 0; i < (newSize - oldSize); i++) {
                actual_enclangs.add(v);
            }
        }
// Till here we did training and calculated the encodings for the testing Data.
        
        
        //Calculation of PRecition starts Here
        if(paths.size() != actual_enclangs.size() || actual_enclangs.size() != identified_enclangs.size()
        || identified_enclangs.size() != paths.size()) {
            System.out.println("Error in evaluate method.");
            System.exit(1);
        }
        
        int correct = 0;
        
        System.out.println("paths: " + paths.size());
        System.out.println("actual_enclangs: " + actual_enclangs.size());
        System.out.println("identified_enclangs: " + identified_enclangs.size());
        
        Hashtable errors = new Hashtable(paths.size() / 4);
        Hashtable errorCounts = new Hashtable(paths.size() / 4);
        Hashtable errorPaths = new Hashtable(paths.size() / 4);
        Hashtable errorInfo = new Hashtable(paths.size() / 4);
        
        for(int i = 0; i < paths.size(); i++) {
            if(((String) actual_enclangs.get(i)).equals((String) identified_enclangs.get(i))) {
//                System.out.println("Found a correct one.");
                correct++;
            } else {
                if(errors.get(actual_enclangs.get(i)) == null) {
                    errors.put(actual_enclangs.get(i), identified_enclangs.get(i));
                    errorCounts.put(actual_enclangs.get(i), new Integer(1));
                } else {
                    Integer oldCount = (Integer) errorCounts.get(actual_enclangs.get(i));
                    Integer newCount = new Integer(oldCount.intValue() + 1);
                    errorCounts.put(actual_enclangs.get(i), newCount);
                }
                
                String errinf = GlobalProperties.getIntlString("\tScore_with_actual_encoding_(") + actual_enclangs.get(i) + ") :"+ ((Hashtable) allScores.get(paths.get(i))).get(actual_enclangs.get(i));
                errinf += GlobalProperties.getIntlString("\n\tScore_with_identified_encoding_(") + identified_enclangs.get(i) + ") :" + ((Hashtable) allScores.get(paths.get(i))).get(identified_enclangs.get(i));
                
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
        
        while(errenm.hasMoreElements()) {
            String errkey = (String) errenm.nextElement();
            String errval = (String) errors.get(errkey);
            Integer errCount = (Integer) errorCounts.get(errkey);
            
            System.out.println(errkey + " identified as " + errval + " " + errCount + " times.");
        }
        
        System.out.println("Error details: ");
        errenm = errorPaths.keys();
        
        while(errenm.hasMoreElements()) {
            String errkey = (String) errenm.nextElement();
            String errval = (String) errorPaths.get(errkey);
            String errinf = (String) errorInfo.get(errkey);
            
            System.out.println(errkey + " got identified as " + errval + ".");
            System.out.println(errinf);
        }
        
        System.out.println("----------------------------------------------------");
        
        if(useStoredTrainingLMs == false) {
            System.out.println("Minimum training data size: " + minTrainingDataSize + " characters.");
            System.out.println("Maximum training data size: " + maxTrainingDataSize + " characters.\n");
            System.out.println("Average training data size: " + totalTrainingDataSize/trainingDataPaths.countProperties() + " characters.");
        }
        
        System.out.println("Minimum test data size: " + minTestingDataSize + " characters.");
        System.out.println("Maximum test data size: " + maxTestingDataSize + " characters.");
        System.out.println("Average test data size: " + totalTestingDataSize/paths.size() + " characters.");
        
        System.out.println("----------------------------------------------------");
        
        if(useWordNGram) {
            if(useStoredTrainingLMs == false) {
                System.out.println("Minimum training data size: " + minTrainingDataSizeWrd + " words.");
                System.out.println("Maximum training data size: " + maxTrainingDataSizeWrd + " words.\n");
                System.out.println("Average training data size: " + totalTrainingDataSizeWrd/trainingDataPaths.countProperties() + " words.");
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
    
    public void evaluateMultilingual(String tstdpaths) throws FileNotFoundException, IOException {
        testingDataPaths = new KeyValueProperties();
        testingDataPaths.read(tstdpaths, "UTF-8");
        //    identifyBatch(new File(tstdpaths), true);
        Iterator enm = testingDataPaths.getPropertyKeys();
        while(enm.hasNext()) {
            String k = (String) enm.next();
            String v = testingDataPaths.getPropertyValue(k);
            File file = new File(k);
            identifyBatch(file, true);
            
        }
        
        wrdPrecition = (double) (( ((double) WordTagCnt) / ((double) TotWrds ) ) * 100);
        nofile *= 2;
        precision1 = (double) (( ((double) correct1) / ((double) nofile ) ) * 100);
//          System.out.println("Correct  :   "+ correct1 + "\n");
//          System.out.println("Denominator  :   "+ nofile + "\n");
        System.out.println("precision :  " + precision1+"\n");
          System.out.println("Maatched Words :   "+WordTagCnt  + "\n");
          System.out.println("Total Words:  " + TotWrds+ "\n");
          System.out.println(" Word Precition precision :  " + wrdPrecition);
        correct1 = 0;
        nofile = 0;
        
        Set keysw = Confusion.keySet();
        Iterator itrw = keysw.iterator();
        while(itrw.hasNext()) {
            String Wrdw = (String) itrw.next();
            //confusabbe Pair****     //     System.out.println("Confusable Pair : " + Wrdw + "\t " + Confusion.get(Wrdw));
        }
        
        
        
    }
    
    private void identifyBatch(File f, boolean multi) throws FileNotFoundException, IOException {
        if(f.isFile() == true) {
            String ans = null;
            nofile++;
            System.out.println("file" + f);
            //  System.out.println("No.of files"+nofile);
            if(multi) {
                //ans = getBestLangEnc(identifyMultilingual(f));
                identifyMultilingual(f); //modified
            } else {
                ans = getBestLangEnc(identify(f));
                
                paths.add(f.getAbsolutePath());
                identified_enclangs.add(ans);
            }
        } else {
            if(f.isDirectory() == true) {
                File files[] = f.listFiles();
                
                for(int i = 0; i < files.length; i++) {
                    identifyBatch(files[i], multi);
                }
            }
        }
    }
    
    protected String getBestLangEnc(LinkedHashMap topLEs) {
        Set keys = topLEs.keySet();
        Iterator itr = keys.iterator();
        
        if(itr != null && topLEs.size() > 0) {
            return (String) itr.next();
        }
        
        return null;
    }
    
    public LinkedHashMap identify(String testString) {
        LinkedHashMap bestModels = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER) {
            testModel = new NGramLMImpl(null, "char", charNGramOrder);
            
            if(useWordNGram)
                wrdTestModel = new NGramLMImpl(null, "word", wordNGramOrder, "UTF-8", "hin::utf8");
            
            testModel.makeNGramLM(testString);
            testModel.pruneByRank(-1, 0);
            bestModels = identify(testModel, wrdTestModel, null);
            if(useTmodel==true) {
                String lacs = (String) getLEByUniqueNGrams(testModel);
                if(lacs !=  null) {
                    if(Idlncs.containsKey(lacs)) {
                        Integer m = (Integer) Idlncs.get(lacs);
                        m = new Integer(m.intValue() + 1);
                        Idlncs.remove(lacs);
                        Idlncs.put(lacs , m);
                    } else {
                        Idlncs.put(lacs , 1);
                        //    System.out.println("Language Suggested ; " + lacs +"\n ");
                    }
                }
            }
        }
        
        return bestModels;
    }
    
    /**
     *
     * @param A test File.
     * @return A String containing the names of the language and the encoding,
     * separated by double colon (::).
     */
    public LinkedHashMap identify(File f) throws FileNotFoundException, IOException {
//        System.out.println("Identifying " + f.getAbsolutePath() + "...");
        LinkedHashMap bestModels = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER) {
            testModel = new NGramLMImpl(f, "char", charNGramOrder);
            
            if(useWordNGram)
                wrdTestModel = new NGramLMImpl(f, "word", wordNGramOrder, GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
            
            try {
                testModel.makeNGramLM((File) null);
                testModel.pruneByRank(-1, 0);
                
                bestModels = identify(testModel, wrdTestModel, f);
            } catch(IOException e) {
                System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
            }
        }
        
        return bestModels;
    }
    
    /**
     * @return LinkedHashMap with top t langEncs and their scrores
     */
    public LinkedHashMap identify(NGramLM testModel, NGramLM wrdTestModel, File testFile /* Just for evaluation: allScores */) {
//        System.out.println("Identifying " + f.getAbsolutePath() + "...");
        String modelKey = "";
        
        if(identifierType != STD_NGRAM_IDENTIFIER) {
            long testingDataSize = testModel.countTokens(1);
            
            if(minTestingDataSize > testingDataSize)
                minTestingDataSize = testingDataSize;
            
            if(maxTestingDataSize < testingDataSize)
                maxTestingDataSize = testingDataSize;
            
            totalTestingDataSize += testingDataSize;
            
            if(useWordNGram) {
                long testingDataSizeWrd = wrdTestModel.countTokens(1);
                
                if(minTestingDataSizeWrd > testingDataSizeWrd)
                    minTestingDataSizeWrd = testingDataSizeWrd;
                
                if(maxTestingDataSizeWrd < testingDataSizeWrd)
                    maxTestingDataSizeWrd = testingDataSizeWrd;
                
                totalTestingDataSizeWrd += testingDataSizeWrd;
            }
        }
        
        Hashtable modelScores = new Hashtable(this.countNGramLMs());
        Hashtable wrdModelScores = new Hashtable(this.countNGramLMs());
        
        if(allScores != null)
            allScores.put(testFile.getAbsolutePath(), modelScores);
        
        Enumeration enm = getNGramLMKeys();
        
        long trainingDataSize = 0;
        long trainingDataSizeWrd = 0;
        
        while(enm.hasMoreElements()) {
            modelKey = (String) enm.nextElement();
            
            try {
                if(inMemoryModels) {
                    trainingModel = getNGramLM(modelKey);
                } else {
                    trainingModel = loadNGramLM(modelKey, "char");
//		    trainingModel = loadNGramLMArpa(modelKey, "char");
                }
                if(useWordNGram) {
                    if(inMemoryModels)
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
            
            double modelScore = 0.0;
            double wrdModelScore = 0.0;
            
            double tst = 0.0;
            double trn = 0.0;
            double jnt = 0.0;
            
            double wtst = 0.0;
            double wtrn = 0.0;
            double wjnt = 0.0;
            
            if(identifierType == FREQ_IDENTIFIER || identifierType == REL_PROB_IDENTIFIER) {
                for(int j = 1; j <= trainingModel.getNGramOrder(); j++) {
                    Iterator<List<Integer>> testItr = testModel.getNGramKeys(j);
                    
                    while(testItr.hasNext()) {
                        List<Integer> testNGram = testItr.next();
                        NGram testNg = (NGram) testModel.getNGram(testNGram, j);
                        NGram trainNg = (NGram) trainingModel.getNGram(testNGram, j);
                        
                        if(trainNg != null) {
                            // System.out.println("Matched NGram: " + ((NGram) sortedNGrams.get(i)).getString() + "\t" + ((NGram) sortedTestNGrams.get(k)).getString());
                            if(scoreType == DEKANG_LIN || scoreType == JIANG_CONRATH
                                    || scoreType == ABS_DEKANG_LIN || scoreType == ABS_JIANG_CONRATH) {
                                tst += Math.log(testNg.getProb());
                                trn += Math.log(trainNg.getProb());
                                jnt += calcMatchScore(trainNg, testNg, identifierType, LOG_JOINT_PROB);
                            } else
                                modelScore += calcMatchScore(trainNg, testNg, identifierType, scoreType);
                        }
                    }
                }
                
                if(useWordNGram) {
                    Iterator<List<Integer>> testItr = wrdTestModel.getNGramKeys(1);
                    
                    while(testItr.hasNext()) {
                        List<Integer> testNGram = testItr.next();
                        NGram testNg = (NGram) wrdTestModel.getNGram(testNGram, 1);
                        NGram trainNg = (NGram) wrdTrainingModel.getNGram(testNGram, 1);
                        
                        if(testNGram.equals("") == false && trainNg != null) {
                            // System.out.println("Matched NGram: " + ((NGram) sortedNGrams.get(i)).getString() + "\t" + ((NGram) sortedTestNGrams.get(k)).getString());
                            if(scoreType == DEKANG_LIN || scoreType == JIANG_CONRATH
                                    || scoreType == ABS_DEKANG_LIN || scoreType == ABS_JIANG_CONRATH) {
                                wtst += Math.log(testNg.getProb());
                                wtrn += Math.log(trainNg.getProb());
                                wjnt += calcMatchScore(trainNg, testNg, identifierType, LOG_JOINT_PROB);
                            } else
                                wrdModelScore += calcMatchScore(trainNg, testNg, identifierType, scoreType);
                        }
                    }
                    
                    // For debugging
                    if(wrdModelScore != 0.0)
                        ;
                }
            }
            
            if(scoreType == DEKANG_LIN) {
                modelScore = (2.0 * jnt) / (tst + trn);
                
                if(useWordNGram)
                    wrdModelScore = (2.0 * wjnt) / (wtst + wtrn);
            } else if(scoreType == JIANG_CONRATH) {
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
        
        enm = modelScores.keys();
        
        while(enm.hasMoreElements()) {
            modelKey = (String) enm.nextElement();
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
            
            if(useWordNGram) {
                int count = Math.min(numAmbiguous, modelScores.size());
                for (int i = 0; i < count; i++) {
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
                enm = modelScores.keys();
                
                while(enm.hasMoreElements()) {
                    modelKey = (String) enm.nextElement();
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
            
            for (int i = 0; i < sortedScores.size(); i++) {
                ModelScore ms = (ModelScore) sortedScores.get(i);
                bestModels.put(ms.modelKey, ms.modelScore);
            }
            
//        System.out.println("bestModel: " + bestModel);
            return bestModels;
    }
    
    /**
     * @return Strings from text as keys and langEncs as values.
     */
    public LinkedHashMap identifyMultilingual(String testString) {
        String bestModel = null;
        
        if(identifierType != STD_NGRAM_IDENTIFIER) {
            String words[] = testString.split("[ ]");
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
    public LinkedHashMap identifyMultilingual(File testFile) {
        String bestModel = null;
        //  System.out.println("I am in Multi Lingual File");
        if(identifierType != STD_NGRAM_IDENTIFIER) {
            // LinkedHashMap TopenumLangEncs =   enumerateLangEncs(testFile);
            int c = 0;
            
            enumerateLangEncs(testFile);
            useTmodel = false;
            enumerateLangEncs(testFile);
            //       enumerateLangEncs(testFile);
            //           enumerateLangEncs(testFile);
//            enumerateLangEncs(testFile);
//            enumerateLangEncs(testFile);
            Tag = true;
            enumeratedTrainingModels.clear();
            Set ke = NoLanc.keySet();
            Iterator it = ke.iterator();
            while(it.hasNext()) {
                
                
                String Wrdl = (String) it.next();
                knTmodel = getNGramLM(Wrdl, true);
                enumeratedTrainingModels.put(Wrdl, knTmodel);
            }
            
            
            
            enumerateLangEncs(testFile);
//          enumerateLangEncs(testFile);
            Tag = false;
            useTmodel = true;
            actual_word_enclags.clear();
//            Enumeration enm1 = Idlncs.keys();
//            while(enm1.hasMoreElements())
//                {
//                    String key = (String) enm1.nextElement();
//                    Integer val = (Integer) Idlncs.get(key)
            // System.out.println("Unique Ngram Languages : "+key + "\t" + "Value : " + val);
//                }
            Idlncs.clear();
            int k =0;
            Set keysl = toplanc.keySet();
            Iterator itrl = keysl.iterator();
            int flg = 0;
            while(itrl.hasNext()) {
                String Wrdl = (String) itrl.next();
                k++;
                if(NoLanc.containsKey(Wrdl)) {
                    correct1++;
                    flg++;
                    //System.out.println("Top list Lnguage :  " + Wrdl+"\n" + "Score : " + toplanc.get(Wrdl));
                }
                System.out.println(GlobalProperties.getIntlString("Top_list_Lnguage_:__") + Wrdl + "\t" + GlobalProperties.getIntlString("Score_:_") + toplanc.get(Wrdl));
                
            }
//                if(flg != 2)
//                {
//                    System.out.println("Wrong Here **************** \n");
//                }
//                if(flg == 2)
//                {
//                        Set keysw = toplanc.keySet();
//                        Iterator itrw = keysw.iterator();
//                         while(itrw.hasNext()) {
//                                String Wrdw = (String) itrw.next();
//                                if(Wrdw != null)
//                                Confusion.put(Wrdw , itrw.next());
//                         }
//                }
//                if(flg == 0)
//                {
//                   System.out.println("Both Languages are wrong\n");
//                }
//                flg = 0;
//
            
            
            Set keyst = NoLanc.keySet();
            Iterator itrt = keyst.iterator();
            while(itrt.hasNext() ) {
                String Wrdt = (String) itrt.next();
                System.out.println(GlobalProperties.getIntlString("Actual__Languages_") + Wrdt+"\n")   ;
            }
            //    System.out.println("Correct: " + correct1 + "\n" );
            //   System.out.println("Denominator  : " + nofile);
            NoLanc.clear();
            toplanc.clear();
            enumeratedTrainingModels.clear();
            enumLangEncs = 4;
            if(useWordNGram)
                enumeratedWrdTrainingModels.clear();
        }
        
//	return bestModel;
        return null;
    }
    
    /**
     * Uses <br>
     * @return langEncs (Strings) keys and scores as values, sorted by scores (best first).
     */
    public LinkedHashMap enumerateLangEncs(File testFile) {
        // System.out.println("I am in Multi Lingual Enumeration File");
        int km = 1;
        String bestModel = null;
        LinkedHashMap wrd_enctypes ;
        
        // LinkedHashMap  Lanec_Scores ;
        LinkedHashMap Lanec_Scores = new LinkedHashMap();
        LinkedHashMap Lanec_New_Scores = new LinkedHashMap();
        //   LinkedHashMap toplanc = new LinkedHashMap();
        // Lanec_Scores = new LinkedHashMap(0, 5);
        actual_word_enclags.clear();
        try {
            if(identifierType != STD_NGRAM_IDENTIFIER) {
                if(Tag == false) {
                    
                    if(useTmodel == true) {
                        //enumLangEncs--;
                        wrd_enctypes = identifyWordTypes(testFile , topLangEncs);
                        // wrd_enctypes = identifyWordTypes(testFile , 3);
                    } else {
                        enumLangEncs = enumLangEncs-1;
                        //System.out.println("Secound Iteration : "+ enumLangEncs);
                        //  wrd_enctypes = identifyWordTypes(testFile , topLangEncs);
                        wrd_enctypes = identifyWordTypes(testFile , enumLangEncs);
                        //             if( enumLangEncs !=4)
                        //enumLangEncs = enumLangEncs-1;
                        //wrd_enctypes = identifyWordTypes(testFile , 3);
                    }
                    
                    int corcnt = 0;
//        Enumeration enm1 = Idlncs.keys();
//        while(enm1.hasMoreElements())
//        {
//            String key = (String) enm1.nextElement();
//            Integer val = (Integer) Idlncs.get(key);
//  //          System.out.println("Unique Ngram Languages : "+key + "\t" + "Value : " + val);
//        }
                    //       Idlncs.clear();
                    //**************************************************************************************************************************
                    boolean takeMax = false;
                    if(useTmodel == true) {
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
                        
                        Vector sortedScrs = new Vector(Idlncs.size());
                        Set ks1 = Idlncs.keySet();
                        Iterator itrN = ks1.iterator();
                        while(itrN.hasNext()) {
                            String modelKey = (String) itrN.next();
                            Integer modelRank = (Integer) Idlncs.get(modelKey);
                            // Double modelRank = (Double) Lanec_Scores.get(modelKey);
                            ModelRank mr = new ModelRank(modelKey, modelRank);
                            // ModelScore mr = new ModelScore(modelKey, modelRank);
                            sortedScrs.add(mr);
                        }
//                toplanc.clear();
//                Set keysl =  Lanec_Scores.keySet();
//                Iterator itrl = keysl.iterator();
//                while(itrl.hasNext()) {
//                    String Wrdl = (String) itrl.next();
//                    toplanc.put(Wrdl , 0);
//
//                }
                        
                        //Lanec_Scores.clear();
                        
                        if(takeMax)
                            Collections.sort(sortedScrs, new Comparator() {
                                public int compare(Object o1, Object o2) {
                                    //   return ( (int) ((ModelRank) o2).modelRank.intValue() - (int) ((ModelRank) o1).modelRank.intValue() );
                                    return ( (int) ((ModelScore) o2).modelScore.doubleValue() - (int) ((ModelScore) o1).modelScore.doubleValue() );
                                }
                            });
                        else
                            Collections.sort(sortedScrs, new Comparator() {
                                public int compare(Object o1, Object o2) {
                                    return ( (int) ((ModelRank) o1).modelRank.intValue() - (int) ((ModelRank) o2).modelRank.intValue() );
                                    // return ( (int) ((ModelScore) o1).modelScore.doubleValue() - (int) ((ModelScore) o2).modelScore.doubleValue() );
                                }
                            });
                            Idlncs.clear();
                            for (int i = 0; i < sortedScrs.size(); i++) {
                                //ModelScore mr = (ModelScore) sortedScores.get(i);
                                ModelRank mr = (ModelRank) sortedScrs.get(i);
                                Idlncs.put(mr.modelKey, mr.modelRank);
                                //     Idlncs.put(mr.modelKey, mr.modelScore);
                            }
                            
                            
                            //Enumeration enm1 = Idlncs.keys();
                            Vector val1 = new Vector(0, 5);
//        Vector val1 = new  vector(0 ,5);
                            Set enm1 = Idlncs.keySet();
                            Iterator itrk = enm1.iterator();
                            while(itrk.hasNext()) {
                                String key = (String) itrk.next();
                                Integer val = (Integer) Idlncs.get(key);
                                val1.add(val);
                                //   System.out.println("Unique Ngram Languages : "+key + "\t" + "Value : " + val);
                            }
                            
                            int in = val1.size();
                            Integer ma = (Integer) val1.get((in-1));
//        System.out.println("Max Value  :  " + ma + "\n");
                            
                            Set enm2 = Idlncs.keySet();
                            Iterator itrp = enm2.iterator();
                            while(itrp.hasNext()) {
                                String keyp = (String) itrp.next();
                                Integer valp = (Integer) Idlncs.get(keyp);
                                Double mods = (Double) (( ((double) valp) / ((double) ma ) ));
                                mods += 1;
                                // System.out.println("value after devision :" + mods+"\n");
                                //Idlncs.remove(keyp);
                                Idlncs.put(keyp , mods);
                                //         wrdPrecition = (double) (( ((double) WordTagCnt) / ((double) TotWrds ) ) * 100);
                                //  System.out.println("Values after modification : "+keyp + "\t" + "ValueP : " + mods);
                            }
                            
//        Set enm3 = Idlncs.keySet();
//        Iterator itrm = enm3.iterator();
//        while(itrm.hasNext())
//        {
//            String keym = (String) itrm.next();
//            Double valm = (Double) Idlncs.get(keym);
//           // val1.add(val);
//            System.out.println("Unique Ngram Languages : "+keym + "\t" + "Value : " + valm);
//        }
                            
                            
                            
                    }
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
//***************************************************************************************************************************
                    Set keys = wrd_enctypes.keySet();
                    Iterator itr = keys.iterator();
                    //    int v = 0;
                    while(itr.hasNext()) {
                        String Wrd = (String) itr.next();
                        LinkedHashMap Lncs = (LinkedHashMap) wrd_enctypes.get( Wrd );
                        
                        // String k1 = (String) wrd_enctypes.get( Wrd );
                        //System.out.println("Word is : " + Wrd + "\n");
                        
                        Set Enc =  Lncs.keySet();
                        Iterator itr1 = Enc.iterator();
                        
                        int max = 3000;
                        
                        int rank = 0;
                        while(itr1.hasNext()) {
                            
                            rank++;
                            
                            String Lan_enc = (String) itr1.next();
                            if(Lanec_Scores.get(Lan_enc) == null) {
                                
                                Double m = 0.0;
                                double penalty = 0.0;
                                if(rank !=0) {
                                    if (rank == 1)
                                        penalty = (double) Math.pow(2,14);
                                    else if(rank ==2)
                                        penalty = (double) Math.pow(2,6);
                                    else if(rank == 3)
                                        penalty = (double) Math.pow(2,0);
                                    else if(rank == 4)
                                        penalty = (double)Math.pow(2,0);
                                    else if(rank == 5)
                                        penalty = (double) Math.pow(2,0);
                                    
                                    //  penalty = Math.pow(Pk , (km - (Math.pow(qk , Math.pow(rank ,1)) )));
                                    
                                    
                                    Double k = (Double) Lncs.get(Lan_enc);
                                    Double un = (Double) Idlncs.get(Lan_enc);
                                    if(un !=null) {
                                        //System.out.println("K , UN :"+ k + un + "\n");
                                        Double mul =  (Double)(un.doubleValue() * k.doubleValue());
                                        //Double f = new Double(k) ;
                                        // System.out.println("Rank : "+ rank +"Language: "+Lan_enc + "Score : " +Lncs.get(Lan_enc) +"\n" );
                                        m =  new Double( penalty * mul );
                                        //System.out.println("Language :"+Lan_enc + "\tScore : " +m +"\n");
                                    } else {
                                        m =  new Double( penalty * k );
                                    }
                                    
                                    //System.out.println("Language :"+Lan_enc + "\tScore : " +m +"\n");
                                    //Lanec_Scores.put(Lan_enc , new Integer (sc.intValue() + (10-rank)));
                                    Lanec_Scores.put( Lan_enc ,  m) ;
                                }
//end added code
                                
                                
                                
                                
                                
                                
                            } else {
                                
                                double penalty = 0.0;
                                Double m = 0.0;
                                
                                if(rank !=0) {
                                    if (rank == 1)
                                        penalty = (double) Math.pow(2,14);
                                    else if(rank ==2)
                                        penalty = (double) Math.pow(2,6);
                                    else if(rank == 3)
                                        penalty = (double) Math.pow(2,0);
                                    else if(rank == 4)
                                        penalty = (double)Math.pow(2,0);
                                    else if(rank == 5)
                                        penalty = (double) Math.pow(2,0);
//                                penalty = Math.pow(Pk , (km - (Math.pow(qk , Math.pow(rank , 1)) )));
                                    Double curVal = (Double) Lanec_Scores.get(Lan_enc);
                                    Double k = (Double) Lncs.get(Lan_enc);
                                    Double un = (Double) Idlncs.get(Lan_enc);
                                    if(un !=null) {
                                        
                                        //Double mul = new Double ( un.doubleValue() * k.doubleValue());
                                        Double mul = new Double( un.doubleValue() + k.doubleValue());
                                        //Double mul = new Double ( ((1/3)*(un.doubleValue())) + ((k.doubleValue()*(1/3))));
                                        //Double f = new Double(k) ;
                                        // System.out.println("Rank : "+ rank + "Language: "+Lan_enc + "\tScore : " +Lncs.get(Lan_enc) +"\n" );
                                        //Double m =  new Double ( Double.valueOf(penalty) * k.doubleValue());
                                        m =  new Double( penalty * mul);
                                    } else {
                                        m =  new Double( penalty * k );
                                    }
                                    //System.out.println("Language :"+Lan_enc + "\tScore : " +m +"\n");
                                    //Lanec_Scores.put(Lan_enc , new Integer (sc.intValue() + (10-rank)));
                                    Lanec_Scores.put( Lan_enc ,  new Double(m + curVal) ) ;
                                }
                                
                            }
                        }
                        //           v++;
                    }
                    //  boolean takeMax = false;
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
                    Vector sortedScores = new Vector(Lanec_Scores.size());
                    Set ks = Lanec_Scores.keySet();
                    itr = ks.iterator();
                    while(itr.hasNext()) {
                        String modelKey = (String) itr.next();
                        //  Integer modelRank = (Integer) Lanec_Scores.get(modelKey);
                        Double modelRank = (Double) Lanec_Scores.get(modelKey);
                        // ModelRank mr = new ModelRank(modelKey, modelRank);
                        ModelScore mr = new ModelScore(modelKey, modelRank);
                        sortedScores.add(mr);
                    }
                    Lanec_Scores.clear();
                    if(takeMax)
                        Collections.sort(sortedScores, new Comparator() {
                            public int compare(Object o1, Object o2) {
                                //   return ( (int) ((ModelRank) o2).modelRank.intValue() - (int) ((ModelRank) o1).modelRank.intValue() );
                                return ( (int) ((ModelScore) o2).modelScore.doubleValue() - (int) ((ModelScore) o1).modelScore.doubleValue() );
                            }
                        });
                    else
                        Collections.sort(sortedScores, new Comparator() {
                            public int compare(Object o1, Object o2) {
                                //  return ( (int) ((ModelRank) o1).modelRank.intValue() - (int) ((ModelRank) o2).modelRank.intValue() );
                                return ( (int) ((ModelScore) o1).modelScore.doubleValue() - (int) ((ModelScore) o2).modelScore.doubleValue() );
                            }
                        });
                        
                        for (int i = 0; i < sortedScores.size(); i++) {
                            ModelScore mr = (ModelScore) sortedScores.get(i);
                            //    ModelRank mr = (ModelRank) sortedScores.get(i);
                            //Lanec_Scores.put(mr.modelKey, mr.modelRank);
                            Lanec_Scores.put(mr.modelKey, mr.modelScore);
                        }
                        ks = Lanec_Scores.keySet();
                        itr = ks.iterator();
                        //System.out.print("file: "+ testFile + "\n" );
                        if(useTmodel == true) {
//            System.out.println("Firest Iteration \n************************************************************************************************");
                            while(itr.hasNext() && (km <= topLangEncs)) {
                                String errkey = (String) itr.next();
                                enumeratedTrainingModels.put(errkey, getNGramLM(errkey));
                                if(km<=topLangEncs)
                                    toplanc.put(errkey , 0);
                                km++;
                                Double  errval = (Double) Lanec_Scores.get(errkey);
//                            System.out.println("\n"+ " Language Enc----- "+errkey + "\t Score----\t"+ errval);
                                
                            }
                        } else {
                            toplanc.clear();
                            //      System.out.println("---------------------------------------------------------------------------------------------------");
                            while(itr.hasNext() && (km <= enumLangEncs)) {
                                
                                //   System.out.println("No of languages under consideration : " + enumLangEncs +"\n");
                                String errkey = (String) itr.next();
                                enumeratedTrainingModels.put(errkey, getNGramLM(errkey));
                                // Integer errval = (Integer) Lanec_Scores.get(errkey);
                                Double  errval = (Double) Lanec_Scores.get(errkey);
//                      System.out.println("\n"+ " Language Enc----- "+errkey + "\t Score----\t"+ errval);
                                //                 if(km<=enumLangEncs)
                                toplanc.put(errkey , Lanec_Scores.get(errkey));
                                km++;
                            }
                            if(itr.hasNext()) {
                                enumeratedTrainingModels.remove(itr.next());
                            }
                        }
                        
                } else {
                    
                    wrd_enctypes = identifyWordTypes(testFile , enumLangEncs);
                    int corcnt = 0;
                    
                    int v = 0;
                    
                    for (int i = 0; i < actual_word_enclags.size(); i++) {
                        
                        if(actual_word_enclags.get(i).equals(Tokens.get(i))) {
                            corcnt++;
                        }
                        
                    }
                    WordTagCnt += corcnt;
                    TotWrds += actual_word_enclags.size();
                    
//                    System.out.println("No of matches :" + corcnt);
//                    System.out.println("Total Words : " + actual_word_enclags.size());
//                    double p1 = (double) ((double)corcnt / (double)actual_word_enclags.size() ) * 100 ;
//                    System.out.println("Precition word : " + p1);
//                    System.out.println("==============================================================================");

                    Tokens.clear();
                }
                
            }
        } catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            
        }
//	return bestModel;
        // return Lanec_Scores ;
        return null;
    }
    
    public static String[] splitByBytes(String str) {
        byte strBytes[] = str.getBytes();
        
        int ich = -1;
        
        byte wtBytes[] = new byte[200];
        String wt = new String();
        String wtypes[] = null;
        
        for (int i = 0; i < strBytes.length; i++) {
            ich = (int) strBytes[i];
            
            if(ich == ' ') {
                
            }
        }
        
        return wtypes;
    }
    
    public LinkedHashMap identifyWordTypes(File testFile, int topLangEncs) throws FileNotFoundException, IOException {
        
        BufferedReader lnReaderISO = null;
        BufferedReader lnReaderUTF = null;
        
        lnReaderISO = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), GlobalProperties.getIntlString("ISO-8859-1")));
        lnReaderUTF = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), GlobalProperties.getIntlString("UTF-8")));
        
        String lineISO;
        String lineUTF;
        
        String splitstrISO[] = null;
        String splitstrUTF[] = null;
        
        String splitstr[] = null;
        
        wrdtyp_enclangs = new LinkedHashMap(0, 5);
        NoLanc =  new Hashtable(0,5);
        
//        while((line = lnReader.readLine()) != null && line.equals("") == false)
//        {
//            splitstr = line.split("[\\s]");
//
//            for (int i = 0; i < splitstr.length; i++)
//            {
//                if(wrdtyp_enclangs.get(splitstr[i]) == null)
//                {
//                    wrdtyp_enclangs.put(splitstr[i], identifyWord(splitstr[i], topLangEncs));
//
//                }
//            }
//        }
        int cnt =0;
        int lineCount = 0;
//     actual_word_enclags.clear();
//      if(actual_word_enclags.isEmpty())
//      {
//        System.out.println("Array is empty\n");
//      }
        while
                (
                ((lineISO = lnReaderISO.readLine()) != null && lineISO.equals("") == false)
                && ((lineUTF = lnReaderUTF.readLine()) != null && lineUTF.equals("") == false)
                ) {
            splitstrISO = lineISO.split("[\\t]");
            splitstrUTF = lineUTF.split("[\\t]");
//        splitstr = splitByBytes(line);
            
            if(UTFIscii.isUTF8(splitstrUTF[0]) == true) {
                splitstr = splitstrUTF;
            } else {
                splitstr = splitstrISO;
            }
            
            if(Tag == false) {
                
                if(wrdtyp_enclangs.get(splitstr[0]) == null && splitstr[0].length() > 5) {
                    
                    NoLanc.put(splitstr[1],1);
                    cnt++;
                    //actual_word_enclags.add(splitstr[1]);
                    wrdtyp_enclangs.put(splitstr[0], identifyWord(splitstr[0], topLangEncs));
                }
            } else {
                lineCount++;
                
                if(wrdtyp_enclangs.get(splitstr[0]) == null ) { // Commnet IF for Calculating Token  Precition .
                    
                    NoLanc.put(splitstr[1],1);
                    actual_word_enclags.add(splitstr[1]);
                    wrdtyp_enclangs.put(splitstr[0], identifyWord(splitstr[0], topLangEncs));
                    LinkedHashMap kl = identifyWord(splitstr[0], topLangEncs);
                    Set ket = kl.keySet();
                    Iterator itrt = ket.iterator();
                    String Lt = (String) itrt.next();
                    Tokens.add(Lt);
                }
            }
        }
        
//    System.out.println("COunt > 5 :" + cnt);
//    System.out.println("Lines:" + lineCount);
        
        
        //System.out.println("Dude I am in Identify Word Type 2 " );
        
        // return wrdtyp_enclangs;
        return wrdtyp_enclangs;
    }
    
    
    
    public LinkedHashMap identifyWordTokens(File testFile, int topLangEncs) throws FileNotFoundException, IOException {
        
        BufferedReader lnReaderISO = null;
        BufferedReader lnReaderUTF = null;
        
        lnReaderISO = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), GlobalProperties.getIntlString("ISO-8859-1")));
        lnReaderUTF = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), GlobalProperties.getIntlString("UTF-8")));
        
        String lineISO;
        String lineUTF;
        
        String splitstrISO[] = null;
        String splitstrUTF[] = null;
        
        String splitstr[] = null;
        
        wrdtyp_enclangs = new LinkedHashMap(0, 5);
        NoLanc = new Hashtable(0, 5);
        
        while
        (
                (lineISO = lnReaderISO.readLine()) != null && lineISO.equals("") == false
                && (lineUTF = lnReaderUTF.readLine()) != null && lineUTF.equals("") == false
        )
        {
            splitstrISO = lineISO.split("[\\t]");
            splitstrUTF = lineUTF.split("[\\t]");
            
            if(UTFIscii.isUTF8(splitstrUTF[0]) == true) {
                splitstr = splitstrUTF;
            } else {
                splitstr = splitstrISO;
            }
            
            for (int i = 0; i < splitstr.length; i++) {
                if(wrdtyp_enclangs.get(splitstr[i]) == null) {
                    
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
    public Vector identifyWords(String[] words, int topLangEncs) {
        Vector langEncsList = new Vector(words.length);
        
        for (int i = 0; i < words.length; i++) {
            LinkedHashMap langEncs = identifyWord(words[i], topLangEncs);
            langEncsList.add(langEncs);
        }
        
        return langEncsList;
    }
    
    public LinkedHashMap identifyWord(String word, int topLangEncs) {
        LinkedHashMap langEncs = identify(word);
        
        Set keys = langEncs.keySet();
        Iterator itr = keys.iterator();
        
        int j = 0;
        LinkedHashMap retLangEncs = new LinkedHashMap(topLangEncs);
        
        while(j < topLangEncs && itr.hasNext()) {
            String le = (String) itr.next();
            
            retLangEncs.put(le, langEncs.get(le));
            //System.out.println( word+"\t \t encodings \t "+ le + " \t Score" + langEncs.get(le));
            j++;
        }
        
        return retLangEncs;
    }
    
    private static double calcMatchScore(NGram trainNG, NGram testNG, int idtype, int stype) {
        double score = 0.0;
        
        if(idtype == REL_PROB_IDENTIFIER) {
//			case STD_NGRAM_IDENTIFIER:
            //  Sequence probability; score not needed
            
//            if(stype == JOINT_PROB)
//                score = trainNG.getRelevanceProb() * testNG.getRelevanceProb();
//            else if(stype == REL_ENTROPY)
//                score = testNG.getRelevanceProb() * ( Math.log( testNG.getRelevanceProb() / trainNG.getRelevanceProb() ) );
        } else if(idtype == FREQ_IDENTIFIER) {
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
    
    public static String getScoreTypeString(int stype) {
        switch(stype) {
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
                return "LOG_PRO B_DIFF1";
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
    
    public static int getScoreType(String stypeString) {
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
    
    public static void main(String[] args) {
        try {
            int scoreType = MultiLangEncIdentifier.MUTUAL_CROSS_ENTROPY;
            
            if(args.length > 0)
                scoreType = MultiLangEncIdentifier.getScoreType(args[0]);
            
            String trainPath = GlobalProperties.resolveRelativePath("props/enc-lang-identify-train.txt");
//	    String trainPath = "/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-train.txt";
            
            if(args.length > 1)
                trainPath = args[1];
            
            // String testPath = "/home/jagadeesh/enc-lang-identifier/testing-multilingual/CO2/Bengali-UTF8_Russian-Windows-1251/0.2-1.0/test-3" +"";
            // String testPath = "/home/jagadeesh/enc-lang-identifier/testing-multilingual/CO2/English-ASCII_Danish-ISO-8859-1" + "";
            String testPath = GlobalProperties.resolveRelativePath("props/Test.txt");
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
            
            int charNGrams = 1000;
            
            if(args.length > 7)
                charNGrams = Integer.parseInt(args[7]);
            
            int wordNGrams = 500;
            
            if(args.length > 8)
                wordNGrams = Integer.parseInt(args[8]);
            
            double wordNGramsWeight = 1.0;
            
            if(args.length > 9)
                wordNGramsWeight = Integer.parseInt(args[9]);
            
            int charNGramOrder = 5;
            
            if(args.length > 10)
                charNGramOrder = Integer.parseInt(args[10]);
            
            int wordNGramOrder = 3;
            
            if(args.length > 11)
                wordNGramOrder = Integer.parseInt(args[11]);
            
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
            System.err.println("\tscoreType: " + MultiLangEncIdentifier.getScoreTypeString(scoreType));
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
            
            MultiLangEncIdentifier idfr = new MultiLangEncIdentifier(trainPath,
                    MultiLangEncIdentifier.FREQ_IDENTIFIER, scoreType, charNGrams, useStoredModels,
                    useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight,
                    charNGramOrder, wordNGramOrder);
//            EncLangIdentifier idfr = new EncLangIdentifier("/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-train.txt", EncLangIdentifier.FREQ_IDENTIFIER, EncLangIdentifier.JOINT_PROB, 1000, false);
            
            idfr.train();
            //idfr.evaluate(testPath);
            
            
//          idfr.qk = 4;
//           // penalty = Math.pow(Pk , (km - (Math.pow(qk , rank)) ));
//            idfr.km = 33000;
//          for(idfr.Pk = 2 ; idfr.Pk<=4 ; idfr.Pk++)
//          {
//                for(idfr.qk = 2 ; idfr.qk<=3; idfr.qk++)
//                {
//                    System.out.println("Km\t"+ idfr.km + "\tQK "+idfr.qk + "\tPK "+idfr.Pk);
            idfr.evaluateMultilingual(testPath);
//                }
//          }
            
//            idfr.evaluate("/home/anil/myproj/sanchay/eclipse/Sanchay/props/topic-identify-test.txt");
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    class ModelScore {
        public String modelKey;
        public Double modelScore;
        
        public ModelScore(String modelKey, Double modelScore) {
            this.modelKey = modelKey;
            this.modelScore = modelScore;
        }
    };
    
    class ModelRank {
        public String modelKey;
        public Integer modelRank;
        public ModelRank(String modelKey, Integer modelRank) {
            this.modelKey = modelKey;
            this.modelRank = modelRank;
        }
    };
    
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

