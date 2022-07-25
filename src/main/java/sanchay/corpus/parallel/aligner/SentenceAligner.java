/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel.aligner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import sanchay.corpus.simple.SimpleCorpus;
import sanchay.mlearning.mt.TranslationCandidates;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;

/**
 *
 * @author anil
 */
public interface SentenceAligner {

    void alignAll();

    void calculateCounts();

    void calculateScores();

    /**
     * @return the alignmentCandidates
     */
    TranslationCandidates getAlignmentCandidates();

    /**
     * @return the alignmentsKVP
     */
    KeyValueProperties getAlignmentsKVP();

    /**
     * @return the alignmentsOutputPath
     */
    String getAlignmentsOutputPath();

    /**
     * @return the mode
     */
    int getMode();

    /**
     * @return the outCharset
     */
    String getOutCharset();

    /**
     * @return the srcCharset
     */
    String getSrcCharset();

    /**
     * @return the srcCorpus
     */
    SimpleCorpus getSrcCorpus();

    /**
     * @return the srcCorpusPath
     */
    String getSrcCorpusPath();

    /**
     * @return the srcTextPT
     */
    PropertyTokens getSrcTextPT();

    /**
     * @return the tgtCharset
     */
    String getTgtCharset();

    /**
     * @return the tgtCorpus
     */
    SimpleCorpus getTgtCorpus();

    /**
     * @return the tgtCorpusPath
     */
    String getTgtCorpusPath();

    /**
     * @return the tgtTextPT
     */
    PropertyTokens getTgtTextPT();

    void readCorpora() throws FileNotFoundException, IOException;

    void readCorpora(String srcPath, String srcCS, String tgtPath, String tgtCS) throws FileNotFoundException, IOException;

    void saveAlignments() throws FileNotFoundException, IOException;

    void saveAlignments(String outPath, String outCS) throws FileNotFoundException, IOException;

    /**
     * @param alignmentCandidates the alignmentCandidates to set
     */
    void setAlignmentCandidates(TranslationCandidates alignmentCandidates);

    /**
     * @param alignmentsKVP the alignmentsKVP to set
     */
    void setAlignmentsKVP(KeyValueProperties alignmentsKVP);

    /**
     * @param alignmentsOutputPath the alignmentsOutputPath to set
     */
    void setAlignmentsOutputPath(String alignmentsOutputPath);

    /**
     * @param mode the mode to set
     */
    void setMode(int mode);

    /**
     * @param outCharset the outCharset to set
     */
    void setOutCharset(String outCharset);

    /**
     * @param srcCharset the srcCharset to set
     */
    void setSrcCharset(String srcCharset);

    /**
     * @param srcCorpus the srcCorpus to set
     */
    void setSrcCorpus(SimpleCorpus srcCorpus);

    /**
     * @param srcCorpusPath the srcCorpusPath to set
     */
    void setSrcCorpusPath(String srcCorpusPath);

    /**
     * @param srcTextPT the srcTextPT to set
     */
    void setSrcTextPT(PropertyTokens srcTextPT);

    /**
     * @param tgtCharset the tgtCharset to set
     */
    void setTgtCharset(String tgtCharset);

    /**
     * @param tgtCorpus the tgtCorpus to set
     */
    void setTgtCorpus(SimpleCorpus tgtCorpus);

    /**
     * @param tgtCorpusPath the tgtCorpusPath to set
     */
    void setTgtCorpusPath(String tgtCorpusPath);

    /**
     * @param tgtTextPT the tgtTextPT to set
     */
    void setTgtTextPT(PropertyTokens tgtTextPT);

    void printCounts(PrintStream ps);

}
