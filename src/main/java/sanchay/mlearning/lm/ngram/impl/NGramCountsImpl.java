/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram.impl;

import edu.berkeley.nlp.lm.NgramLanguageModel;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.GlobalProperties;
import sanchay.factory.Factory;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramCount;
import sanchay.mlearning.lm.ngram.NGramCounts;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class NGramCountsImpl<NG extends NGramCount> implements NGramCounts<NG> {

    protected String nGramType; // char or word or object
    protected int nGramOrder;
    protected String language;
    protected File nGramLMFile; // in ARPA format, or raw text file
    protected String charset;
    
    protected List<LinkedHashMap<List<Integer>, NG>> nGrams;
    protected List<LinkedHashMap<Integer, String>> tempNGrams;

    protected long mergedTypeCount;
    protected long tokenCount[];
    protected long mergedTokenCount;

    protected long vocabulary = 1616;
    
    protected boolean sentenceBoundaries = true;

    protected Index<String> vocabIndex;
    
    protected Factory<NG> factory;
    
    protected NGramCountsImpl()
    {
    }

    public NGramCountsImpl(File f, String type, int order, String cs, String lang, boolean sentenceBoundaries) {
        this(f, type, order, cs, lang);

        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramCountsImpl(File f, String type, int order, String cs, String lang) {
        this(f, type, order);

        charset = cs;
        language = lang;
    }

    public NGramCountsImpl(File f, String type, int order, Index vocabIndex, boolean sentenceBoundaries) {
        this(f, type, order, vocabIndex);
        
        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramCountsImpl(File f, String type, int order, boolean sentenceBoundaries) {
        this(f, type, order);
        
        this.sentenceBoundaries = sentenceBoundaries;
    }

    public NGramCountsImpl(File f, String type, int order, Index vocabIndex) {
        init(f, type, order);

        this.vocabIndex = vocabIndex;
    }

    public NGramCountsImpl(File f, String type, int order) {
        init(f, type, order);
        
        this.vocabIndex = new HashIndex<String>();
    }
    
    private void init(File f, String type, int order)
    {
        factory = NGramCountImpl.getFactory();
        
        nGramType = type;
        nGramOrder = order;

        nGrams = new ArrayList(order);
        tempNGrams = new ArrayList(order);

        for (int i = 0; i < order; i++) {
            nGrams.add(new LinkedHashMap<List<Integer>, NG>(0, 10));
            tempNGrams.add(new LinkedHashMap<Integer, String>(0, 10));
        }

        tokenCount = new long[order];

        nGramLMFile = f;
        charset = "ISO-8859-1";
        language = "hin::utf8";
    }
    

    /**
     * @return the charset
     */
    @Override
    public String getCharset()
    {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    @Override
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    /**
     * @return Returns the nGramType.
     */
    @Override
    public String getNGramType() {
        return nGramType;
    }

    /**
     * @param gramType The nGramType to set.
     */
    @Override
    public void setNGramType(String gramType) {
        nGramType = gramType;
    }
    
    @Override
    public long getVocabularySize()
    {
//        vocabulary = nGrams.get(0).size();
        vocabulary = vocabIndex.size();
        
        return vocabulary;
    }
    
    @Override
    public final Index getVocabIndex()
    {
        return vocabIndex;
    }

    @Override
    public long calcMergedTokenCount() {
        
        mergedTokenCount = 0;
        
        for (int i = 1; i <= nGramOrder; i++) {
            mergedTokenCount += calcTokenCount(i);
        }

        return mergedTokenCount;
    }

    @Override
    public long calcTokenCount() {
        for (int i = 1; i <= nGramOrder; i++) {
            calcTokenCount(i);
        }
        
        return tokenCount[nGramOrder - 1];
    }

    /**
     * @return Calculates and returns the tokenCount.
     */
    @Override
    public long calcTokenCount(int whichGram) {
        if (whichGram < 1 || whichGram > nGramOrder) {
            return -1;
        }

        tokenCount[whichGram - 1] = 0;

        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        while (itr.hasNext()) {
            List<Integer> k = itr.next();
            NG ng = getNGram(k, whichGram);

            tokenCount[whichGram - 1] += ng.getFreq();
        }

        return tokenCount[whichGram - 1];
    }

    @Override
    public long calcMergedTypeCount() {
        
        mergedTypeCount = 0;
        
        for (int i = 1; i <= nGramOrder; i++) {
            mergedTypeCount += countTypes(i);
        }

        return mergedTypeCount;
    }

    /**
     * @return Returns the tokenCount.
     */
    @Override
    public long countTokens(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return -1;
        }

        return tokenCount[whichGram - 1];
    }

    @Override
    public int getNGramOrder() {
        return nGramOrder;
    }

    @Override
    public void setNGramOrder(int o) {
        nGramOrder = o;
    }

    @Override
    public File getNGramLMFile() {
        return nGramLMFile;
    }

    @Override
    public void setNGramLMFile(File f) {
        nGramLMFile = f;
    }

    @Override
    public long countTypes(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return -1;
        }

        return nGrams.get(whichGram - 1).size();
    }

    @Override
    public Iterator<List<Integer>> getNGramKeys(int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return null;
        }

        return nGrams.get(whichGram - 1).keySet().iterator();
    }

    @Override
    public boolean hasNGram(String ngramKey, int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return false;
        }

        List<Integer> wdIndices = NGramCountImpl.getIndices(this, ngramKey, false);

        LinkedHashMap<List<Integer>, NG> ngmap = nGrams.get(whichGram - 1);

        return ngmap == null ? null : ngmap.containsKey(wdIndices);
    }

    @Override
    public boolean hasNGramPlain(String ngramKey, int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return false;
        }

        List<Integer> wdIndices = NGramImpl.getIndicesPlain(this, ngramKey, false);

        LinkedHashMap<List<Integer>, NG> ngmap = nGrams.get(whichGram - 1);

        return ngmap == null ? null : ngmap.containsKey(wdIndices);
    }

    @Override
    public NG getNGram(List<Integer> wdIndices, int whichGram)
    {
        if (whichGram > nGramOrder || whichGram < 1) {
            return null;
        }

        LinkedHashMap<List<Integer>, NG> ngmap = nGrams.get(whichGram - 1);
        
        if(ngmap == null) {
            return null;
        }
        
        return ngmap.get(wdIndices);
    }

    @Override
    public NG getNGram(String wds, int whichGram) {
        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, false);

        return getNGram(wdIndices, whichGram);
    }

    /**
     *
     * @param wds
     * @param whichGram
     * @return
     */
    @Override
    public NG getNGramPlain(String wds, int whichGram) {
        List<Integer> wdIndices = NGramImpl.getIndicesPlain(this, wds, false);

        return getNGram(wdIndices, whichGram);
    }

    @Override
    public void addNGram(String wds, NG ng, int whichGram)
    {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        if(nGrams.size() == whichGram - 1)
        {
            nGrams.add(new LinkedHashMap<List<Integer>, NG>());
        }

        LinkedHashMap<List<Integer>, NG> ngmap = nGrams.get(whichGram - 1);
        
        if(ngmap == null) {
            return;
        }

        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, true);

        ng.setIndices(wdIndices);
        ngmap.put(wdIndices, ng);        
    }

    @Override
    public NG addNGram(List<Integer> wdIndices, int whichGram)
    {
        if (whichGram > nGramOrder || whichGram < 1) {
            return null;
        }

        LinkedHashMap<List<Integer>, NG> ngmap = nGrams.get(whichGram - 1);
        
        if(ngmap == null) {
            return null;
        }

        NG ng = null;

        if ((ng = ngmap.get(wdIndices)) == null) {
            ng = factory.createInstance();
            ng.setIndices(wdIndices);
            ngmap.put(wdIndices, ng);
        } else {
            ng.setFreq(ng.getFreq() + 1);
        }
                
        return ng;
    }

    @Override
    public NG addNGram(String wds, int whichGram) {
        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, true);

        return addNGram(wdIndices, whichGram);
    }

    @Override
    public NG removeNGram(List<Integer> wdIndices, int whichGram)
    {
        if (whichGram > nGramOrder || whichGram < 1) {
            return null;
        }

        LinkedHashMap<List<Integer>, NG> ngmap = nGrams.get(whichGram - 1);
        
        if(ngmap == null)
            return null;
        
        return ngmap.remove(wdIndices);
    }

    @Override
    public NG removeNGram(String wds, int whichGram) {
        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, false);
        
        return removeNGram(wdIndices, whichGram);
    }

    @Override
    public void readNGramLM(File f, String cs) throws FileNotFoundException,
            IOException
    {
        charset = cs;
        readNGramLM(f);
    }

    @Override
    public void readNGramLM(File f) throws FileNotFoundException, IOException {
        setNGramLMFile(f);
        readNGramLM();
    }

    @Override
    public void computeNGramLM(String unigram, int count) {
        addNGram(unigram, 1);
        LinkedHashMap<Integer, String> ht = tempNGrams.get(0);
        ht.put(1, unigram);

        if (count == 1) {
            for (int j = count; j <= nGramOrder; j++) {
                LinkedHashMap<Integer, String> tempht = tempNGrams.get(j - 1);
                tempht.put(1, unigram);
            }

        } else if (count > 1 && count <= nGramOrder) {
            for (int j = 2; j <= count; j++) {
                LinkedHashMap<Integer, String> tempht = tempNGrams.get(j - 1);
                for (int k = j; k > 1; k--) {
                    tempht.put(k, tempht.get(k - 1) + "@#&" + unigram);
                }
                tempht.put(1, unigram);
                addNGram((String) tempht.get(j), j);
            //System.out.println(count + "-*&" + (String)tempht.get(j) + "***" + j);
            }
            for (int j = count + 1; j <= nGramOrder; j++) {
                LinkedHashMap<Integer, String> tempht = tempNGrams.get(j - 1);
                for (int k = count; k > 1; k--) {
                    tempht.put(k, tempht.get(k - 1) + "@#&" + unigram);
                }
                tempht.put(1, unigram);
            }
        } else {
            for (int j = 2; j <= nGramOrder; j++) {
                LinkedHashMap<Integer, String> tempht = tempNGrams.get(j - 1);
                for (int k = j; k > 1; k--) {
                    tempht.put(k, tempht.get(k - 1) + "@#&" + unigram);
                }
                tempht.put(1, unigram);
                addNGram((String) tempht.get(j), j);
            //System.out.println(count + "-*&" + (String)tempht.get(j) + "***" + j);
            }
        }
    }

    @Override
    public void makeNGramLM(File f, String cs) throws FileNotFoundException, IOException
    {
        charset = cs;
        
        makeNGramLM(f);
    }

    @Override
    public void makeNGramLM(File f) throws FileNotFoundException,
            IOException {
//	System.out.println("File: " + f.getAbsolutePath());
//	System.out.println("Type: " + nGramType);

        if (f == null) {
            f = this.getNGramLMFile();
        }

//        System.out.println(GlobalProperties.getIntlString("Making_NGramLM_") + f.getAbsolutePath() + "...");

        if (f.isDirectory() == true) {
            File files[] = f.listFiles();

            for (int i = 0; i < files.length; i++) {
                makeNGramLM(files[i]);
            }
        } else if (f.isFile() == true) {

            byte ch[] = new byte[1];
            int ich = -1;
            int count = 0;
//	    String chstr = "";

            String unigram = "";

            if (nGramType.equalsIgnoreCase("char")) {
                FileInputStream fis = new FileInputStream(f);

                while ((fis.read(ch)) != -1) {
                    ich = (int) ch[0];
                    ich += 127;

//                    chstr = new String(ch, "ISO-8859-1");
                    //	    		    unigram = chstr;
                    unigram = Integer.toString(ich);
                    count = count + 1;
                    computeNGramLM(unigram, count);
                }
            } else if (nGramType.equalsIgnoreCase("uchar")) {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(f),getCharset()));

                String line = "";
                String wrdBegin = "~";
                String wrdEnd = "^";

                while ((line = inReader.readLine()) != null) {
                    line = line.trim();

                    while (line.contains("  ")) {
                        line = line.replaceAll("  ", " ");
                    }

                    String wrds[] = line.split("[\\s]");

                    line = "";

                    for (int i = 0; i < wrds.length; i++) {
                        line += wrdBegin + wrds[i] + wrdEnd;
                    }

                    for (int i = 0; i < line.length(); i++) {
                        char cch = line.charAt(i);

                        unigram = Character.toString(cch);
                        count = count + 1;
                        computeNGramLM(unigram, count);
                    }
                }
            } else if (nGramType.equalsIgnoreCase("word")) {
                String ifile = f.getAbsolutePath();
                String ofile = ifile.replaceFirst("/", "");
                ofile = ofile.replaceAll("/", "_");
                String tfile = ofile + ".tmp";

                Pattern p = Pattern.compile("[\\s]");

//		System.out.println("Temp Word NGram file: " + tfile);
//		System.out.println("Out Word NGram file: " + ofile);

//		UtilityFunctions.naiivePreprocessing(f.getAbsolutePath(), charset, tfile, charset, language);
//		UtilityFunctions.trimSpaces(tfile, charset, ofile, charset);

                f = new File(ifile);

                BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(f),getCharset()));

                String line = "";
//		LinkedList windowList = (LinkedList) Collections.synchronizedList(new LinkedList());
                LinkedList windowList = new LinkedList();


                while ((line = inReader.readLine()) != null) {
                    line = line.trim();
                    
                    // Sentence/line based calculation
                    count = 0;                    
                    
                    if(sentenceBoundaries)
                    {
                        line = "<s> " + line + " </s>";
                    }
                    
//		    System.out.println(line);

                    String wrds[] = p.split(line);

                    if (wrds == null || wrds.length == 0) {
                        continue;
                    }

                    for (int i = 0; i < wrds.length; i++) {
                        wrds[i] = wrds[i].trim();

                        if (wrds[i].equals("")) {
                            continue;
                        }

                        unigram = wrds[i];
                        count = count + 1;
                        computeNGramLM(unigram, count);
                    //addNGram(unigram, 1);

//			if(windowList.size() >= triggerPairWindowSize)
//			    windowList.removeFirst();
//
//			windowList.add(unigram);
//			
//			updateTriggerPairs(windowList, unigram);

//			System.out.println(unigram);
                    }
                }

                (new File(tfile)).delete();
                (new File(ofile)).delete();
            }
        }

        calcCountsNProbs();
//	countTriggerPairs(true);
    }

    // Ad-hoc repetition of code
    @Override
    public void makeNGramLM(String s) {
        int ich = -1;
        int count = 0;
//	    String chstr = "";

        String unigram = "";

        if (nGramType.equalsIgnoreCase("char")) {
            byte inbytes[] = s.getBytes();

            for (int i = 0; i < inbytes.length; i++) {
                ich = (int) inbytes[i];
                ich += 127;
//                    chstr = new String(ch, "ISO-8859-1");
//	    		    unigram = chstr;
                unigram = Integer.toString(ich);
                count = count + 1;
                computeNGramLM(unigram, count);
            }
        } else if (nGramType.equalsIgnoreCase("uchar")) {
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
//                    chstr = new String(ch, "ISO-8859-1");
//	    		    unigram = chstr;
                unigram = Character.toString(ch);
                count = count + 1;
                computeNGramLM(unigram, count);
            }
        } else if (nGramType.equalsIgnoreCase("word")) {
            Pattern p = Pattern.compile("[\\s]");

            Pattern pn = Pattern.compile("[\\n]");
            
//		LinkedList windowList = (LinkedList) Collections.synchronizedList(new LinkedList());
            LinkedList windowList = new LinkedList();

            s = s.trim();
//		    System.out.println(line);

            String lines[] = pn.split(s);

            for (int i = 0; i < lines.length; i++) {
                String string = lines[i];

                // Sentence/line based calculation
                count = 0;                    

                if(sentenceBoundaries)
                {
                    string = "<s> " + string + " </s>";
                }

                String wrds[] = p.split(string);

                if (wrds == null || wrds.length == 0) {
                    return;
                }

                for (int j = 0; j < wrds.length; j++) {
                    wrds[j] = wrds[j].trim();

                    if (wrds[j].equals("")) {
                        continue;
                    }

                    unigram = wrds[j];
                    count = count + 1;
                    computeNGramLM(unigram, count);
                }
            }
        }

        calcCountsNProbs();
//	countTriggerPairs(true);
    }

    @Override
    public void readNGramLM() throws FileNotFoundException, IOException {
        clear();

        BufferedReader lnReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(nGramLMFile),getCharset()));

        String line;
        int gram = -1;
        int order = 0;
        String ngram = "";
        String pngram = ""; // probability
        String bowngram = ""; // backoff weight
        String fqngram = ""; // frequency
        String splitstr[];
        
        List<Integer> typeCounts = new ArrayList<Integer>();

        Pattern p = Pattern.compile("[\\s]+");

        while ((line = lnReader.readLine()) != null && !line.startsWith("\\end\\")) {
            if (line.startsWith("\\data\\")) {
                gram = 0;
//                System.out.println("0-grams");
            } else if (gram == 0 && line.startsWith("ngram")) {
                splitstr = p.split(line);
                
                String orderStr = splitstr[1];
                
                splitstr = orderStr.split("=");
                
                order = Integer.parseInt(splitstr[0]);
                
                typeCounts.add(Integer.parseInt(splitstr[1]));
                
            } else if (gram >= 0 && line.matches("\\\\[1-9]-grams:")) {
                gram++;
            } else if (gram >= 1) {
                splitstr = p.split(line);
                
                if(splitstr.length <= 1)
                {
                    continue;
                }

                NG ng = factory.createInstance();

                pngram = splitstr[0];

//                System.err.println(line);
//                ng.setProb(Math.pow(10, Double.parseDouble(pngram)));
                
                ngram = "";

                for (int i = 1; i <= gram; i++) {
                    if(i == 1)
                    {
                        ngram = splitstr[i];
                    }
                    else
                    {
                        ngram += "@#&" + splitstr[i];
                    }
                }
                
                if(gram == order)
                {
                    if(splitstr.length > gram + 1)
                    {
                        fqngram = splitstr[gram + 1];
                        ng.setFreq(Long.parseLong(fqngram));
                    }
                }
                else if(gram < order)
                {
                    if(splitstr.length > gram + 1) // Standard ARPA format: backoff weight
                    {
//                        bowngram = splitstr[gram + 1];
//
//                        ng.setBackwt(Math.pow(10, Double.parseDouble(bowngram)));
                    }

                    if(splitstr.length > gram + 2) // Sanchay format: frequency
                    {
                        fqngram = splitstr[gram + 2];

                        ng.setFreq(Long.parseLong(fqngram));
                    }
                }

                addNGram(ngram, ng, gram);
            }
            
            nGramOrder = order;
        }
    }

    @Override
    public void calcCountsNProbs() {
        calcTokenCount();
        calcMergedTokenCount();
        calcMergedTypeCount();
    }

    @Override
    public void printNGram(List<Integer> wdIndices, int whichGram,
                PrintStream ps, boolean printFrequency) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }
        
        String wds = NGramImpl.getString(this, wdIndices);
        
        printNGram(wds, whichGram, ps, printFrequency);
    }

    @Override
    public void printNGram(String wds, int whichGram, PrintStream ps, boolean printFrequency) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }
        
//        DecimalFormat df = new DecimalFormat("0.0000000");
        DecimalFormat df = new DecimalFormat("#.#######");
        
        List<Integer> wdIndices = NGramImpl.getIndices(this, wds, false);
        
        NG ng = getNGram(wdIndices, whichGram);

        wds = wds.replaceAll("@#&", " ");

//        ps.print(ng.getProb() + " " + wds + " ");
        // Print logprobs
//        ps.print(df.format(Math.log10(ng.getProb())) + "\t" + wds);
        ps.print(0 + "\t" + wds);

        if (whichGram == getNGramOrder() && printFrequency) {
            ps.println("\t" + ng.getFreq());
        } else {
            if(printFrequency)
            {
//                if(ng.getBackwt() != 0.0)
//                {
//                    ps.println("\t" + df.format(Math.log10(ng.getBackwt())) + "\t" + ng.getFreq());                
//                }
//                else
//                {
                    ps.println("\t" + "-Infinity" + "\t" + ng.getFreq());                                    
//                }
            }
            else
            {
//                if(ng.getBackwt() != 0.0)
//                {
//                    ps.println("\t" + df.format(Math.log10(ng.getBackwt())));                
//                }                
//                else
//                {
                    ps.println("\t-Infinity");
//                }
            }
//            ps.println("\t" + Double.toString(ng.getBackwt()) + " " + ng.getFreq() + "\t" + ng.getProb());
        }
    }

    @Override
    public void printNGrams(int whichGram, PrintStream ps, boolean printFrequency) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return;
        }

        System.out.println(whichGram);
        Iterator<List<Integer>> itr = getNGramKeys(whichGram);

        while (itr.hasNext()) {
            List<Integer> key = itr.next();
            printNGram(key, whichGram, ps, printFrequency);
        }
    }

    @Override
    public void saveNGramLM(String f, String cs, boolean printFrequency)
            throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        PrintStream ps = new PrintStream(f, cs);
        
        writeNGramLM(ps, printFrequency);
        
        ps.close();
    }

    @Override
    public void writeNGramLM(PrintStream ps, boolean printFrequency) {
        ps.println("\\data\\");
        ps.println("");

        for (int i = 1; i <= nGramOrder; i++) {
            ps.println("ngram " + i + "=" + nGrams.get(i-1).size());            
        }

        ps.println("");

        for (int i = 1; i <= nGramOrder; i++) {
            ps.println("\\" + i + "-grams:");
            printNGrams(i, ps, printFrequency);
            ps.println("");
        }

        ps.println("\\end\\");
    }

    @Override
    public void sort()
    {
        sort(NGram.SORT_BY_FREQ);
    }

    @Override
    public void sort(int sortOrder)
    {
        for (int i = 1; i <= getNGramOrder(); i++)
        {
            LinkedHashMap<List<Integer>, NG> ngrams = nGrams.get(i - 1);
            
            switch (sortOrder) {
                case NGram.SORT_BY_FREQ:
                default:
                    ngrams = (LinkedHashMap<List<Integer>, NG>) UtilityFunctions.sort(ngrams, new ByNGramFreq());
                    nGrams.set(i - 1, ngrams);
                    break;

                case NGram.SORT_BY_FREQ_DESC:
                    ngrams = (LinkedHashMap<List<Integer>, NG>) UtilityFunctions.sort(ngrams, new ByNGramFreqDesc());
                    nGrams.set(i - 1, ngrams);
                    break;
            }
        }        
    }

    @Override
    public List<NG> sort(int sortOrder, int whichGram) {
        if (whichGram > nGramOrder || whichGram < 1) {
            return null;
        }

        LinkedHashMap<List<Integer>, NG> ht = nGrams.get(whichGram - 1);
        List<NG> sorted = new ArrayList<NG>(ht.values());

        switch (sortOrder) {
            case NGram.SORT_BY_FREQ:
            default:
                Collections.sort(sorted, new ByNGramFreq());
                break;

            case NGram.SORT_BY_FREQ_DESC:
                Collections.sort(sorted, new ByNGramFreqDesc());
                break;
        }

        return sorted;
    }

    @Override
    public void clear() {
        for (int i = 0; i < nGramOrder; i++) {
            nGrams.get(i).clear();
        }
    }

    @Override
    public Object clone() {
        try {
            NGramCountsImpl obj = (NGramCountsImpl) super.clone();
            obj.nGramOrder = nGramOrder;

            obj.nGramLMFile = nGramLMFile;

            // Implementation is currently limited to only 4-grams
            obj.nGrams = (List) ((ArrayList) nGrams).clone();

            for (int i = 0; i < nGramOrder; i++) {
                LinkedHashMap<List<Integer>, NG> oldht = nGrams.get(i);
                LinkedHashMap<List<Integer>, NG> ht = (LinkedHashMap<List<Integer>, NG>) oldht.clone();

                Iterator<List<Integer>> enm = ht.keySet().iterator();

                while (enm.hasNext()) {
                    List<Integer> key = enm.next();
                    NG ng = (NG) oldht.get(key);
                    ht.put(key, (NG) ng.clone());
                }

                obj.nGrams.set(i, ht);
            }

            return obj;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(GlobalProperties.getIntlString("But_the_class_is_Cloneable!!!"));
        }
    }

    @Override
    public void pruneByFrequency(int minFreq, int whichGram) {
        if (minFreq <= 0 || nGramOrder <= 0) {
            return;
        }

        int pruned = 0;

        if (whichGram <= 0 || whichGram > nGramOrder) {
            LinkedHashMap<List<Integer>, NG> oldht = nGrams.get(0);
            List<NG> sortedNGrams = new ArrayList<NG>(oldht.size());

            // Prune ngrams of all orders
            for (int i = 1; i <= nGramOrder; i++) {
                List<NG> sortedNGramsTmp = sort(NGram.SORT_BY_FREQ_DESC, i);
                sortedNGrams.addAll(sortedNGramsTmp);
            }

            Collections.sort(sortedNGrams, new ByNGramFreqDesc());

            int count = sortedNGrams.size();

            int i = 0;
            for (; i < count; i++) {
                NG ng = sortedNGrams.get(i);

                if (ng.getFreq() < minFreq) {
                    break;
                }
            }

            for (; i < sortedNGrams.size(); i++) {
                for (int j = 1; j <= nGramOrder; j++) {
                    NG ng = sortedNGrams.get(i);
                    String key = ng.getString(this);

                    if (removeNGram(key, j) != null) {
                        pruned++;
                        j = nGramOrder;
                    }
                }
            }
        } else {
            // Prune ngrams of a specific order
            List<NG> sortedNGrams = sort(NGram.SORT_BY_FREQ, whichGram);

            int count = sortedNGrams.size();

            int i = 0;
            for (; i < count; i++) {
                NG ng = sortedNGrams.get(i);

                if (ng.getFreq() < minFreq) {
                    break;
                }
            }

            for (; i < sortedNGrams.size(); i++) {
                NG ng = sortedNGrams.get(i);
                String key = ng.getString(this);
                removeNGram(key, whichGram);
                pruned++;
            }
        }

        System.out.println(GlobalProperties.getIntlString("\tPruned:_") + pruned);
    }

    @Override
    public LinkedHashMap<String, NG> getAllNgrams() {
        int size = 0;

        for (int i = 1; i <= nGramOrder; i++) {
            size += countTypes(i);
        }

        List<NG> allngsVec = new ArrayList<NG>(size);

        for (int i = 1; i <= nGramOrder; i++) {
            Iterator<List<Integer>> itr = getNGramKeys(i);
            while (itr.hasNext()) {
                List<Integer> key = itr.next();
                NG ng = getNGram(key, i);
                allngsVec.add(ng);
            }
        }

        Collections.sort(allngsVec, new ByNGramFreqDesc());

        LinkedHashMap<String, NG> allngs = new LinkedHashMap<String, NG>(size);

        int count = allngsVec.size();

        for (int i = 0; i < count; i++)
        {
            allngs.put(allngsVec.get(i).getString(this), allngsVec.get(i));
        }

        return allngs;
    }

    public static NGramLM loadNGramLMBinary(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);

        NGramLM nglm = (NGramLM) ois.readObject();
        ois.close();

        return nglm;
    }

    public static NGramLM saveNGramLMBinary(NGramLM nglm, File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(nglm);
        oos.close();

        return nglm;
    }

    public static NGramLM loadNGramLMArpa(File file, String type, int order, String cs, String lang) throws FileNotFoundException, IOException, ClassNotFoundException {
        NGramLM nglm = new NGramLMImpl(file, type, order, cs, lang);

        try {
            nglm.readNGramLM(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
        }

        return nglm;
    }

    public static void storeNGramLM(NGramLM nglm, File file) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(nglm);
        oos.close();
    }

    public static void storeNGramLMArpa(NGramLM nglm, File file, String cs, boolean printFrequency) throws FileNotFoundException, IOException {
        PrintStream ps = null;

        try {
            ps = new PrintStream(file, cs);
        } catch (IOException e) {
            System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
        }

        nglm.writeNGramLM(ps, printFrequency);
    }

    @Override
    public LinkedHashMap<List<Integer>, NG> findNGram(String ngram, int order, int minFreq, int maxFreq) {
        LinkedHashMap<List<Integer>, NG> matchNgrams = new LinkedHashMap<List<Integer>, NG>(0, 20);
        Iterator<List<Integer>> enm = getNGramKeys(order);
        Pattern p = Pattern.compile(ngram);
        while (enm.hasNext()) {
            List<Integer> key = enm.next();
            
            String keyStr = NGramImpl.getString(this, key);
            
            Matcher m = p.matcher(keyStr);
            if (m.find()) {
                NG ng = getNGram(key, order);
                //System.out.println(ng.getString() +"\t"+ ng.getFreq() +"\t"+ ng.getProb());
                if (minFreq != -1 && maxFreq != -1) {
                    if ((ng.getFreq() >= minFreq) && (ng.getFreq() <= maxFreq)) {
                        matchNgrams.put(key, ng);
                    }
                } else if (minFreq == -1 && maxFreq != -1) {
                    if (ng.getFreq() <= maxFreq) {
                        matchNgrams.put(key, ng);
                    }
                } else if (minFreq != -1 && maxFreq == -1) {
                    if (ng.getFreq() >= minFreq) {
                        matchNgrams.put(key, ng);
                    }
                } else {
                    matchNgrams.put(key, ng);
                }
            }
        }
        return matchNgrams;
    }

    @Override
    public LinkedHashMap<Integer, LinkedHashMap<List<Integer>, NG>> findNGramFile(String ngram, int order, int minFreq, int maxFreq) {
        LinkedHashMap<List<Integer>, NG> matchNgrams = new LinkedHashMap<List<Integer>, NG>(0, 20);

        LinkedHashMap<Integer, LinkedHashMap<List<Integer>, NG>> fNGrams = new LinkedHashMap<Integer, LinkedHashMap<List<Integer>, NG>>(0, 20);

        if (order == -1) {
            for (int i = 1; i <= nGramOrder; i++) {
                matchNgrams = findNGram(ngram, i, minFreq, maxFreq);
                fNGrams.put(i, matchNgrams);
            }
        } else if (order <= nGramOrder) {
            matchNgrams = findNGram(ngram, order, minFreq, maxFreq);
            fNGrams.put(order, matchNgrams);
        } else {
            System.out.println(GlobalProperties.getIntlString("Given_order_is_greater_than_the_order_of_the_file"));
        }

        return fNGrams;
    }

    @Override
    public boolean fCheckNGramFile(String ngram, int order, int minFreq, int maxFreq) {
        if (order == -1) {
            for (int i = 1; i <= nGramOrder; i++) {
                if ((fCheckNGram(ngram, i, minFreq, maxFreq)) == true) {
                    return true;
                }
            }
        } else if (order <= nGramOrder) {
            if ((fCheckNGram(ngram, order, minFreq, maxFreq)) == true) {
                return true;
            }
        } else {
            System.out.println(GlobalProperties.getIntlString("Given_order_is_greater_than_the_order_of_the_file"));
        }

        return false;
    }

    @Override
    public boolean fCheckNGram(String ngram, int order, int minFreq, int maxFreq) {
        Iterator<List<Integer>> itr = getNGramKeys(order);
        Pattern p = Pattern.compile(ngram);
        while (itr.hasNext()) {
            List<Integer> key = itr.next();
            
            String keyStr = NGramImpl.getString(this, key);
            
            Matcher m = p.matcher(keyStr);
            if (m.find()) {
                NG ng = getNGram(key, order);
                //System.out.println(ng.getString() +"\t"+ ng.getFreq() +"\t"+ ng.getProb());
                if (minFreq != -1 && maxFreq != -1) {
                    if ((ng.getFreq() >= minFreq) && (ng.getFreq() <= maxFreq)) {
                        return true;
                    }
                } else if (minFreq == -1 && maxFreq != -1) {
                    if (ng.getFreq() <= maxFreq) {
                        return true;
                    }
                } else if (minFreq != -1 && maxFreq == -1) {
                    if (ng.getFreq() >= minFreq) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<LinkedHashMap<List<Integer>, Long>> getCumulativeFrequenciesList()
    {
        List<LinkedHashMap<List<Integer>, Long>> cumFreqsList = new ArrayList<LinkedHashMap<List<Integer>, Long>>();
    
        int order = getNGramOrder();
        
        for (int i = 1; i <= order; i++) {
            LinkedHashMap<List<Integer>, Long> cumFreqs = getCumulativeFrequencies(i);

            cumFreqsList.add(cumFreqs);
        }
        
        return cumFreqsList;
    }

    @Override
    public LinkedHashMap<List<Integer>, Long> getCumulativeFrequencies(int whichGram)
    {
        LinkedHashMap<List<Integer>, Long> cumFreqs = new LinkedHashMap<List<Integer>, Long>();

//        List<NGram> ngrams = sort(NGram.SORT_BY_FREQ, whichGram);

        LinkedHashMap<List<Integer>, NG> ngrams = nGrams.get(whichGram - 1);
        
        int count = ngrams.size();
        
        long cumFreq = 0;
        
        Iterator<List<Integer>> itr = ngrams.keySet().iterator();

        while(itr.hasNext()){
            List<Integer> key = itr.next();
            
            NG ng = ngrams.get(key);
            
            cumFreq += ng.getFreq();
            
            cumFreqs.put(ng.getIndices(), cumFreq);
        }
        
        return cumFreqs;
    }
    
    @Override
    public long getQuartile(String wds)
    {        
        List<Integer> ngIndices = NGramImpl.getIndices(this, wds, true);
        
        LinkedHashMap<List<Integer>, Long> cumFreqs = getCumulativeFrequencies(ngIndices.size());
        
        long N = (Long) UtilityFunctions.getLastElement(cumFreqs);
        
        long cumFreq = cumFreqs.get(ngIndices);

        long q1 = 1 * N / 4;
        long q2 = 2 * N / 4;
        long q3 = 3 * N / 4;
        
        if(cumFreq <= q1)
        {
            return 1;
        }

        if(cumFreq > q1 && cumFreq <= q2)
        {
            return 2;
        }

        if(cumFreq > q2 && cumFreq <= q3)
        {
            return 3;
        }

        if(cumFreq > q3)
        {
            return 4;
        }
        
        return 0;
    }
    
    public static List<List<Double>> percentNGramsInQuartile(NGramCounts nglmSmall,
            List<LinkedHashMap<List<Integer>, Long>> cumFreqsList, List<Long> topCumFreqList)
    {
        int order = cumFreqsList.size();
        
        order = Math.min(order, nglmSmall.getNGramOrder());
        
        List<List<Double>> pqList = new ArrayList<List<Double>>();
        
        for (int i = 1; i <= order; i++) {
            List<Double> pq = percentNGramsInQuartile(nglmSmall, i,
                    cumFreqsList.get(i - 1), topCumFreqList.get(i - 1));

            pqList.add(pq);
        }
        
        return pqList;
    }
    
    public static List<Double> percentNGramsInQuartile(NGramCounts nglmSmall,
            int whichGram, LinkedHashMap<List<Integer>, Long> cumFreqs, long topCumFreq)
    {
        long count = nglmSmall.countTypes(whichGram);

        if(topCumFreq <= 0)
        {
            topCumFreq = (Long) UtilityFunctions.getLastElement(cumFreqs);
        }

        long q1 = 1 * topCumFreq / 4;
        long q2 = 2 * topCumFreq / 4;
        long q3 = 3 * topCumFreq / 4;
        
        List<Double> qp = new ArrayList<Double>();
        
        qp.add(0.0);
        qp.add(0.0);
        qp.add(0.0);
        qp.add(0.0);
        qp.add(0.0);
        
        Iterator<List<Integer>> itr = nglmSmall.getNGramKeys(whichGram);
        
        while(itr.hasNext())
        {
            List<Integer> ngKey = itr.next();
            
//            NGram ng = nglm.getNGram(ngKey, whichGram);
            
            Long cumFreq = cumFreqs.get(ngKey);
            
            if(cumFreq == null)
            {
                qp.set(0, qp.get(0) + 1.0);                
            }
            else if(cumFreq <= q1)
            {
                qp.set(1, qp.get(1) + 1.0);
            }
            else if(cumFreq > q1 && cumFreq <= q2)
            {
                qp.set(2, qp.get(2) + 1.0);
            }
            else if(cumFreq > q2 && cumFreq <= q3)
            {
                qp.set(3, qp.get(3) + 1.0);
            }
            else if(cumFreq > q3)
            {
                qp.set(4, qp.get(4) + 1.0);
            }            
        }

        qp.set(0, (qp.get(0)/ count) * 100.0);
        qp.set(1, (qp.get(1)/ count) * 100.0);
        qp.set(2, (qp.get(2)/ count) * 100.0);
        qp.set(3, (qp.get(3)/ count) * 100.0);
        qp.set(4, (qp.get(4)/ count) * 100.0);
    
        return qp;        
        
    }

    public static <NG extends NGramCount> int getCommonNGramCount(NGramCounts<NG> nGramLM1, NGramCounts<NG> nGramLM2) {
        int count = 0;

        if (nGramLM1 == null || nGramLM2 == null) {
            return count;
        }

        for (int j = 1; j <= nGramLM1.getNGramOrder(); j++) {
            Iterator<List<Integer>> itr2=  nGramLM2.getNGramKeys(j);

            while (itr2.hasNext()) {
                List<Integer> nGram2Key = itr2.next();
                NG nGram2 = nGramLM2.getNGram(nGram2Key, j);
                NG nGram1 = nGramLM1.getNGram(nGram2Key, j);

                if (nGram1 != null) {
                    if(nGram1.getString(nGramLM1).equals(nGram2.getString(nGramLM2)))
                        count++;
                }
            }
        }

        return count;
    }
    
        
    public static <T, NG extends NGramCount> NGramCounts<NG> makeCountsFromProbs(NgramLanguageModel<T> berkeleyLM, int uptoOrder)
    {
        NGramCounts<NG> nGramCounts = new NGramCountsImpl<NG>();
        
        int order = Math.min(uptoOrder, berkeleyLM.getLmOrder());

        if(order <= 0)
        {
            order = berkeleyLM.getLmOrder();
        }
        
        nGramCounts.setNGramOrder(order);
        
//        for (int i = 0; i < order; i++) {
//            berkeleyLM.
//        }
        
        return nGramCounts;
    }


    public static <NG extends NGramCount> int getCommonNGramEditDistance(NGramCounts<NG> nGramLM1, NGramCounts<NG> nGramLM2) {
        int distance = 0;
//
//        if (nGramLM1 == null || nGramLM2 == null) {
//            return Integer.MAX_VALUE;
//        }
//
//        for (int j = 1; j <= nGramLM1.getNGramOrder(); j++) {
//            Enumeration enm2 = nGramLM2.getNGramKeys(j);
//
//            while (enm2.hasMoreElements()) {
//                String nGram2Key = (String) enm2.nextElement();
//                NGram nGram2 = nGramLM2.getNGram(nGram2Key, j);
//                NGram nGram1 = nGramLM1.getNGram(nGram2Key, j);
//
//                if (nGram1 != null) {
//                    distance += SimilarityMeasures.levenshteinDistance(nGram1.getString(), nGram2.getString(), 0);
//                }
//            }
//        }

        return distance;
    }
    
    public static void main(String[] args)
    {
        try {
            NGramLM nGramCounts = new NGramLMImpl(null, "word", 7);
            
            nGramCounts.makeNGramLM("All evil starts with 15 volts . What does All good starts with .");
            File f = new File("tmp/tmp.nglm");

            NGramCountsImpl.storeNGramLM(nGramCounts, f);

            f = new File("tmp/tmp.nglm.arpa");

            NGramLMImpl.storeNGramLMArpa(nGramCounts, f, "UTF-8", true);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NGramCountsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NGramCountsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
