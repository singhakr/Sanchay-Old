/*
 * Created on Jul 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.simple.data;

import java.io.PrintStream;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface WordType {
    // WordType flags
    public static final short ANY_WORD = 0; // all kinds of words

    public static final short CORPUS_WORD = 1;

    public static final short SUBSTITUTE_WORD = 2;

    public static final short HYPERNYM_WORD = 3;

    public static final short CUT_OFF_WORD = 4;

    public static final short NUMERIC = 5;

    public static final short PHONETIC = 6;

    // Sorting orders
    public static final int SORT_BY_SRC_WRD = 0;

    public static final int SORT_BY_TGT_MNG = 1;

    public static final int SORT_BY_TAG = 2;

    public static final int SORT_BY_EQWRD = 3;

    public static final int SORT_BY_FREQ = 4;

    public static final int SORT_BY_REVERSE_FREQ = 5;

    public static final int SORT_BY_SIG = 6;

    public static final int SORT_BY_CUT_OFF_FREQ = 7;

    public String getWord();

    public void setWord(String w);

    public String getTag();

    public void setTag(String t);

    /*public String getMeaning()
     {
     return meaning;
     }

     public void setMeaning(String w)
     {
     meaning = w;
     }*/public int getEqWord();

    public void setEqWord(int w);

    public int countAllSbtWords();

    public int getWordSbtAll(int num);

    public void setWordSbtAll(int wd);// table index

    public int getWordSig();

    public void setWordSig(int s);

    public long getFreq();

    public void setFreq(long f);

    public long getCutOffFreq();

    public void setCutOffFreq(long f);

    public boolean getIsCorpusWord();

    public void setIsCorpusWord(boolean flag);

    public boolean getIsSubstituteWord();

    public void setIsSubstituteWord(boolean flag);

    public boolean getIsHypernymWord();

    public void setIsHypernymWord(boolean flag);

    public boolean getIsNumeric();

    public void setIsNumeric(boolean flag);

    public boolean getIsPhonetic();

    public void setIsPhonetic(boolean flag);

    public int print(PrintStream ps);
}