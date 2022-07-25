/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram;

import edu.stanford.nlp.util.Index;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.phramer.v1.decoder.lm.NgramLanguageModel;

/**
 *
 * @author anil
 */
public interface NGramCounts<NG extends NGramCount> {

    void addNGram(String wds, NG ng, int whichGram);

    NG addNGram(List<Integer> wdIndices, int whichGram);

    NG addNGram(String wds, int whichGram);

    void calcCountsNProbs();

    long calcMergedTokenCount();

    long calcMergedTypeCount();

    long calcTokenCount();

    /**
     * @return Calculates and returns the tokenCount.
     */
    long calcTokenCount(int whichGram);

    void clear();

    void computeNGramLM(String unigram, int count);

    /**
     * @return Returns the tokenCount.
     */
    long countTokens(int whichGram);

    long countTypes(int whichGram);

    boolean fCheckNGram(String ngram, int order, int minFreq, int maxFreq);

    boolean fCheckNGramFile(String ngram, int order, int minFreq, int maxFreq);

    LinkedHashMap<List<Integer>, NG> findNGram(String ngram, int order, int minFreq, int maxFreq);

    LinkedHashMap<Integer, LinkedHashMap<List<Integer>, NG>> findNGramFile(String ngram, int order, int minFreq, int maxFreq);

    LinkedHashMap<String, NG> getAllNgrams();

    /**
     * @return the charset
     */
    String getCharset();

    LinkedHashMap<List<Integer>, Long> getCumulativeFrequencies(int whichGram);

    List<LinkedHashMap<List<Integer>, Long>> getCumulativeFrequenciesList();

    NG getNGram(List<Integer> wdIndices, int whichGram);

    NG getNGram(String wds, int whichGram);

    Iterator<List<Integer>> getNGramKeys(int whichGram);

    File getNGramLMFile();

    int getNGramOrder();

    /**
     *
     * @param wds
     * @param whichGram
     * @return
     */
    NG getNGramPlain(String wds, int whichGram);

    /**
     * @return Returns the nGramType.
     */
    String getNGramType();

    long getQuartile(String wds);

    Index getVocabIndex();

    long getVocabularySize();

    boolean hasNGram(String ngramKey, int whichGram);

    boolean hasNGramPlain(String ngramKey, int whichGram);

    void makeNGramLM(File f, String cs) throws FileNotFoundException, IOException;

    void makeNGramLM(File f) throws FileNotFoundException, IOException;

    // Ad-hoc repetition of code
    void makeNGramLM(String s);

    void printNGram(List<Integer> wdIndices, int whichGram, PrintStream ps, boolean printFrequency);

    void printNGram(String wds, int whichGram, PrintStream ps, boolean printFrequency);

    void printNGrams(int whichGram, PrintStream ps, boolean printFrequency);

    void pruneByFrequency(int minFreq, int whichGram);

    void readNGramLM(File f, String cs) throws FileNotFoundException, IOException;

    void readNGramLM(File f) throws FileNotFoundException, IOException;

    void readNGramLM() throws FileNotFoundException, IOException;

    NG removeNGram(List<Integer> wdIndices, int whichGram);

    NG removeNGram(String wds, int whichGram);

    void saveNGramLM(String f, String cs, boolean printFrequency) throws FileNotFoundException, UnsupportedEncodingException, IOException;

    /**
     * @param charset the charset to set
     */
    void setCharset(String charset);

    void setNGramLMFile(File f);

    void setNGramOrder(int o);

    /**
     * @param gramType The nGramType to set.
     */
    void setNGramType(String gramType);

    void sort();

    void sort(int sortOrder);

    List<NG> sort(int sortOrder, int whichGram);

    void writeNGramLM(PrintStream ps, boolean printFrequency);
}
