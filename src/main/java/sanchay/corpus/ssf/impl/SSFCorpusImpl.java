/*
 * Created on Aug 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.impl;

import java.io.*;
import java.util.*;

import sanchay.corpus.*;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.*;
import sanchay.properties.KeyValueProperties;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SSFCorpusImpl extends Corpus
        implements SSFCorpus {

    /**
     * 
     */
    
    protected Hashtable stories;
    protected KeyValueProperties properties;

    protected AlignmentUnit<SSFCorpus> alignmentUnit;
   
    public SSFCorpusImpl(String charset) {
        super(charset);
        // TODO Auto-generated constructor stub
        stories = new Hashtable(5, 5);
    }

    public SSFCorpusImpl(String pf /*props file*/, String charset)  throws FileNotFoundException, IOException {
        super(pf, charset);
        // TODO Auto-generated constructor stub
        properties = new KeyValueProperties(pf, charset);
        stories = new Hashtable(5, 5);
    }
	
    public KeyValueProperties getProperties()
    {
            return properties;
    }

    public void setProperties(KeyValueProperties p)
    {
            properties = p;
    }
    
    public int countStories()
    {
        return stories.size();
    }
    
    public Enumeration getStoryKeys()
    {
        return stories.keys();
    }
    
    public SSFStory getStory(String p)
    {
        return (SSFStory) stories.get(p);
    }
    
    public int addStory(String p, SSFStory s)
    {
        if(s == null)
            s = new SSFStoryImpl();
        
        stories.put(p, s);
        return stories.size();
    }
    
    public String removeStory(String p)
    {
        return (String) stories.remove(p);
    }

    // Will just read the paths, not the actual stories
    public void read() throws FileNotFoundException, IOException
    {
        File f = new File(path);
        read(f);
    }

    public void read(File f[]) throws FileNotFoundException, IOException
    {
        for(int i = 0; i < f.length; i++)
        {
            read(f[i]);
        }
    }

    public void read(File f) throws FileNotFoundException, IOException
    {
        if(f == null) { f = new File(path); }

        if(f.isDirectory() == true)
        {
            File files[] = f.listFiles();
            
            for(int i = 0; i < files.length; i++)
            {
                read(files[i]);
            }
        }
        else if(f.isFile() == true)
        {
            String p = f.getAbsolutePath();
            addStory(p, null);
        }
    }
    
    public void readStory(File f) throws Exception, FileNotFoundException, IOException
    {
        if(f == null) { f = new File(path); }

        if(f.isDirectory() == true)
        {
            File files[] = f.listFiles();
            
            for(int i = 0; i < files.length; i++)
            {
                readStory(files[i]);
            }
        }
        else if(f.isFile() == true)
        {
            String p = f.getAbsolutePath();
            SSFStoryImpl s = new SSFStoryImpl();
            s.readFile(p, charset);
            addStory(p, s);
        }
    }
    
    public void readStories(File[] fs)
    		throws Exception, FileNotFoundException, IOException
    {
        for (int i = 0; i < fs.length; i++)
        {
            String p = fs[i].getAbsolutePath();
            SSFStoryImpl s = new SSFStoryImpl();
            s.readFile(p, charset);
            addStory(p, s);            
        }
    }
    
    public void clear()
    {
        path = "";
        stories.clear();
        properties.clear();
    }
	
    public void print(PrintStream ps)
    {

    }

    public Corpus getCopy()
    {
        return null;
    }

    public AlignmentUnit getAlignmentUnit()
    {
        return alignmentUnit;
    }
    
    public void setAlignmentUnit(AlignmentUnit alignmentUnit)
    {
        this.alignmentUnit = alignmentUnit;
    }

    public SSFCorpus getAlignedObject(String alignmentKey)
    {
        return alignmentUnit.getAlignedObject(alignmentKey);
    }
    
    public List<SSFCorpus> getAlignedObjects()
    {
        return alignmentUnit.getAlignedObjects();
    }

    public SSFCorpus getFirstAlignedObject()
    {
        return alignmentUnit.getFirstAlignedObject();
    }

    public SSFCorpus getAlignedObject(int i)
    {
        return alignmentUnit.getAlignedObject(i);
    }

    public SSFCorpus getLastAlignedObject()
    {
        return alignmentUnit.getLastAlignedObject();
    }

    public static void main(String[] args) {
    }
}
