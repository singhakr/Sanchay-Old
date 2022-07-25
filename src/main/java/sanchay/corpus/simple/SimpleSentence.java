/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.simple;

import java.io.PrintStream;
import java.util.Vector;
import sanchay.corpus.Sentence;
import sanchay.corpus.parallel.APCData;
import sanchay.corpus.parallel.APCProperties;
import sanchay.table.SanchayTableModel;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SimpleSentence {

	int countWords();

	int getWord(int num);

	int setWord(int ind /* sentence index */, int wd /* table index */);

    void insertWord(int ind /* sentence index */, int wd /* table index */);

	int countSbtWords();

	int getWordSbt(int num);

	int setWordSbt(int ind , int wd ); // sentence index ,  table index

	void calculateSentenceLength(SanchayTableModel wttbl);

    int getSentenceLength();

    void setSentenceLength(int senlen);

	void calculateSignature(SanchayTableModel wttbl);

    int getSignature();

	void setSignature(int sg);

	void setWeightedLength(APCProperties apcpro, char type);

	void setWeightedLength(char type);

    int getWeightedLength();

    Vector getWords(SanchayTableModel wttbl);

    String getSentenceString(SanchayTableModel wttbl);

    String getWordString(int i, SanchayTableModel wttbl);

    double getCommonWords(Sentence sen, APCData apcdata);

    double getCommonHypernyms(Sentence sen, APCData apcdata);

	double get_Phntc_Num_Match(Sentence sen, APCData apcdata);

	double getCommonSynonyms(Sentence sen, APCData apcdata);

    String removeVowels(String word, String lang);

    void print(PrintStream ps);

    void printCounts(SanchayTableModel wtTable, PrintStream ps);
}
