/*
 * DefaultCRFData.java
 *
 * Created on September 7, 2008, 11:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import iitb.crf.DataIter;
import iitb.crf.DataSequence;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;

/**
 *
 * @author Anil Kumar Singh
 */
public class DefaultCRFData implements DataIter {

    protected Vector data;
    protected Iterator iterator;
    
    protected Vector documents;
    
    /** Creates a new instance of DefaultCRFData */
    public DefaultCRFData()
    {
        data = new Vector(0, 10); 
        documents = new Vector();
    }

    public void startScan()
    {
        iterator = data.iterator();
    }

    public boolean hasNext()
    {
        return iterator.hasNext();
    }

    public DataSequence next()
    {
        DataSequence seq = (DataSequence) iterator.next();
        
        if(seq == null)
            iterator = null;
        
        return seq;
    }   

    public DataSequence getDataSequence(int index)
    {
        return (DataSequence) data.get(index);
    }   

    public void add(DataSequence seq)
    {
        data.add(seq);
    }

    public void addDocumentBoundaries(int startIndex, int endIndex, String inputPath, String outputPath)
    {
        DefaultCRFDocument doc = new DefaultCRFDocument(startIndex, endIndex, inputPath, outputPath);
        
        documents.add(doc);
    }

    public DefaultCRFDocument removeDocumentBoundaries(int index)
    {
        return (DefaultCRFDocument) documents.remove(index);
    }

    public DefaultCRFDocument getDocument(int index)
    {
        return (DefaultCRFDocument) documents.get(index);
    }
    
    public int countDocuments()
    {
        return documents.size();
    }

    public int countDataSequences()
    {
        return data.size();
    }
    
    public DefaultCRFData getCRFText(int index)
    {
        DefaultCRFDocument doc = (DefaultCRFDocument) documents.get(index);
        
        return (DefaultCRFData) getDefaultCRFData(doc.getSequenceStart(), doc.getSequenceEnd() - doc.getSequenceStart());
    }

    public DefaultCRFData getDefaultCRFData(int startSequenceNum, int windowSize)
    {
        if(startSequenceNum < 0 || startSequenceNum >= documents.size()
                || (startSequenceNum + windowSize) > documents.size())
        return null;

        DefaultCRFData doc = new DefaultCRFData();

        for(int i = 0; i < windowSize; i++)
            doc.add((DataSequence) data.get(startSequenceNum + i));

        return doc;
    }
   
    public void readRaw(String path, String cs) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;

        if(cs != null && cs.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

        String line = "";
        DefaultDataSequence defaultDataSequence = null;

        while((line = inReader.readLine()) != null)
        {
            String parts[] = line.split("[ ]");
            
            defaultDataSequence = new DefaultDataSequence();
            
            for (int i = 0; i < parts.length; i++)
            {
                defaultDataSequence.add_x(parts[i]);
            }
            
            data.add(defaultDataSequence);
        }       
    }

    public void readSSFRaw(String path, String cs) throws FileNotFoundException, IOException, Exception
    {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++)
        {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++)
            {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
           }

           data.add(defaultDataSequence);
        }
    }

    public void readSSFPOS(String path, String cs) throws FileNotFoundException, IOException, Exception
    {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++)
        {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++)
            {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
           }

           data.add(defaultDataSequence);
        }
    }

    public void readSSFChunk(String path, String cs) throws FileNotFoundException, IOException, Exception
    {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++)
        {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++)
            {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
           }

           data.add(defaultDataSequence);
        }
    }

    public void readSSFFeature(String path, String cs, String featureName) throws FileNotFoundException, IOException, Exception
    {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++)
        {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++)
            {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
           }

           data.add(defaultDataSequence);
        }
    }
    
    public void saveRaw(String path, String cs) throws FileNotFoundException, IOException
    {
        PrintStream ps = new PrintStream(path, cs);
        printRaw(ps);
    }
    
    public void printRaw(PrintStream ps)
    {
        DefaultDataSequence testRecord = null;
        
        startScan();
        
        while(hasNext())
        {
            testRecord = (DefaultDataSequence) next();

            testRecord.printRaw(ps);
        }
    }    
    
    public void readTagged(String path, String cs) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;

        if(cs != null && cs.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

        String line = "";
        DefaultDataSequence defaultDataSequence = new DefaultDataSequence();

        int i = 0;
        
        while((line = inReader.readLine()) != null)
        {
            if(line.equals(""))
            {
                if(defaultDataSequence != null)
                    data.add(defaultDataSequence);
        
                i = 0;
                defaultDataSequence = new DefaultDataSequence();                
            }
            
            String parts[] = line.split("[ ]");
            
            if(parts.length >= 2)
            {
                String label = parts[parts.length - 1];
                label = label.substring(1);

                String instance = "";
                
                for (int j = 0; j < parts.length - 1; j++)
                {
                    instance += "_" + parts[j];
                }
                
                defaultDataSequence.add_x(instance);
                defaultDataSequence.set_y(i++, Integer.parseInt(label) - 1);
            }
        }    
    }
    
    public void saveTagged(String path, String cs) throws FileNotFoundException, IOException
    {
        PrintStream ps = new PrintStream(path, cs);
        printTagged(ps);
    }
    
    public void printTagged(PrintStream ps)
    {
        DefaultDataSequence testRecord = null;
        
        startScan();
        
        while(hasNext())
        {
            testRecord = (DefaultDataSequence) next();

            testRecord.printTagged(ps);
        }
    }
    
    public class DefaultCRFDocument
    {
        int startSequence;
        int endSequence;
        
        String inputPath;
        String outputPath;
        
        public DefaultCRFDocument()
        {
            
        }

        public DefaultCRFDocument(int startIndex, int endIndex, String inputPath, String outputPath)
        {
            this.startSequence = startIndex;
            this.endSequence = endIndex;
            this.inputPath = inputPath;
            this.outputPath = outputPath;
        }
        
        public int getSequenceStart()
        {
            return startSequence;
        }

        public void setSequenceStart(int s)
        {
            startSequence = s;
        }
        
        public int getSequenceEnd()
        {
            return endSequence;
        }

        public void setSequenceEnd(int e)
        {
            endSequence = e;
        }
        
        public String getInputPath()
        {
            return inputPath;
        }

        public void setInputPath(String p)
        {
            inputPath = p;
        }
        
        public String getOutputPath()
        {
            return outputPath;
        }

        public void setOutputPath(String p)
        {
            outputPath = p;
        }
    }
}
