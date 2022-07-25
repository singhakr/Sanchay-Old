/*
 * Created on Aug 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import sanchay.corpus.*;
import sanchay.corpus.ssf.SSFCorpusCollection;
import sanchay.properties.KeyValueProperties;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SSFCorpusCollectionImpl extends CorpusCollection
        implements SSFCorpusCollection {

    /**
     * 
     */

    private KeyValueProperties properties;
    private KeyValueProperties corporaPaths;

    public SSFCorpusCollectionImpl() {
        super();
        // TODO Auto-generated constructor stub
    }

    public SSFCorpusCollectionImpl(String pf /*props file*/, String cf /*paths file*/, String charset)  throws FileNotFoundException, IOException {
        super();
        // TODO Auto-generated constructor stub
        properties = new KeyValueProperties(pf, charset);
        corporaPaths = new KeyValueProperties(cf, charset);
    }
	
    public KeyValueProperties getCorporaPaths()
    {
        return properties;
    }

    public void setCorporaPaths(KeyValueProperties p)
    {
        properties = p;
    }

    public KeyValueProperties getProperties()
    {
        return properties;
    }

    public void setProperties(KeyValueProperties p)
    {
        properties = p;
    }
    
    public void clear()
    {
        properties.clear();
        corporaPaths.clear();
    }
	
    public void print(PrintStream ps)
    {

    }

    public CorpusCollection getCopy()
    {
        return null;
    }

    public static void main(String[] args) {
    }
}
