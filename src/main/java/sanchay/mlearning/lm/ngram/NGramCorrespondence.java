/*
 * Created on Jul 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.mlearning.lm.ngram;

import java.io.PrintStream;
import java.util.Enumeration;

import sanchay.corpus.parallel.impl.AlignedCorpusImpl;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface NGramCorrespondence {
    public int getNGramOrder();

    public void setNGramOrder(int o);

    public long countNGrams(short whichGram);

    public Enumeration getNGramKeys(int whichGram);

    public long getNGramFreq(String wds, int whichGram);

    public long addNGram(String wds, long ngf, int whichGram);

    public long removeNGram(String wds, int whichGram);

    public int train(AlignedCorpusImpl ac);

    public void clear();

    public void print(int whichGram, PrintStream ps);

    public void print(PrintStream ps);
}