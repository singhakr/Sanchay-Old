/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import sanchay.corpus.Sentence;
import sanchay.properties.PropertiesManager;
import sanchay.table.SanchayTableModel;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SimpleCorpus {
	SanchayTableModel getWordTypeTable();

	void setWordTypeTable();

	PropertiesManager getPropsManager();

	int setPropsManager(String propsfilename, String charset);

	int countSentences();

    int countTokens(boolean recalculate);

	Sentence getSentence(int num);

	int addSentence(Sentence s);

	int insertSentence(int index, Sentence s);

	Sentence removeSentence(int num);

	int read(File file, String charset)throws FileNotFoundException, IOException;

    int readSegments(File file, String charset, int segmentSize)throws FileNotFoundException, IOException;

    int readOverlappingSegments(File file, String charset, int segmentSize)throws FileNotFoundException, IOException;

    void print(PrintStream ps);

}
