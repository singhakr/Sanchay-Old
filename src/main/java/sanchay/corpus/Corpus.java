/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus;

import java.io.*;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Corpus {
    
    protected String path;
    protected String charset;

    protected int tokenCount;

    /**
     * 
     */
    public Corpus()
    {
        
    }
    
    public Corpus(String charset) {
        super();
        // TODO Auto-generated constructor stub
        
        this.charset = charset;
    }

    public Corpus(String path, String charset) {
        super();
        // TODO Auto-generated constructor stub

        this.path = path;
        this.charset = charset;
    }

    public String getPath()
    {
        return path;
    }
    
    public void setPath(String p)
    {
        path = p;
    }

    public String getCharset()
    {
        return charset;
    }

    public void setCharset(String cs)
    {
        charset = cs;
    }
    
    public int countSentences()
    {
        return -1;
    }

    public int countTokens(boolean recalculate)
    {
        return -1;
    }
    
    public Sentence getSentence(int num)
    {
        return null;
    }

    public int addSentence(Sentence s)
    {
        return -1;
    }

    public int insertSentence(int index, Sentence s)
    {
        return -1;
    }

    public Sentence removeSentence(int num)
    {
        return null;
    }

    public void print(PrintStream ps)
    {
        
    }
    
    public Corpus getCopy()
    {
        return null;
    }

    public static void main(String[] args) {
    }
}
