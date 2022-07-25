/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.simple.data.*;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Sentence {
    java.util.ResourceBundle bundle = GlobalProperties.getResourceBundle(); // NOI18N

    /**
     * 
     */
    public Sentence() {
        super();
        // TODO Auto-generated constructor stub
    }

    public int countWords()
    {
        return -1;
    }

    public int getWord(int num)
    {
        return -1;
    }

    public int setWord(int ind /* sentence index */, int wd /* table index */)
    {
        return -1;
    }
    
    public void insertWord(int ind /* sentence index */, int wd /* table index */)
    {

    }

    public Vector getWords(WordTypeTable wttbl)
    {
        return null;
    }

    public String getSentenceString(WordTypeTable wttbl)
    {
        return null;
    }

    public void readString(String s) throws Exception
    {
    }

    public String makeString()
    {
        return null;
    }

    public void print(PrintStream ps)
    {
        
    }

    public Sentence getCopy() throws Exception
    {
        return null;
    }

    public static void main(String[] args) {
    }
}
