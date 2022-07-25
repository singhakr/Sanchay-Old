/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.weka;

import iitb.crf.DataIter;
import iitb.crf.DataSequence;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.crf.DefaultDataSequence;

/**
 *
 * @author H Umesh
 */
public class DefaultArffData implements DataIter {

    protected Vector data;
    protected Iterator iterator;
    protected Vector documents;

    public DefaultArffData() {
        data = new Vector();
        documents = new Vector();
    }

    public void startScan() {
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
        DefaultArffDocument doc = new DefaultArffDocument(startIndex, endIndex, inputPath, outputPath);
        
        documents.add(doc);
    }

    public DefaultArffDocument removeDocumentBoundaries(int index)
    {
        return (DefaultArffDocument) documents.remove(index);
    }

    public DefaultArffDocument getDocument(int index)
    {
        return (DefaultArffDocument) documents.get(index);
    }
    
    public int countDocuments()
    {
        return documents.size();
    }

    public int countDataSequences()
    {
        return data.size();
    }
    
    
        public DefaultArffData getCRFText(int index)
    {
        DefaultArffDocument doc = (DefaultArffDocument) documents.get(index);
        
        return (DefaultArffData) getDefaultArffData(doc.getSequenceStart(), doc.getSequenceEnd() - doc.getSequenceStart());
    }
    
        public DefaultArffData getDefaultArffData(int startSequenceNum, int windowSize)
    {
        if(startSequenceNum < 0 || startSequenceNum >= documents.size()
                || (startSequenceNum + windowSize) > documents.size())
        return null;

        DefaultArffData doc = new DefaultArffData();

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
                
    public void saveArffTagged(String path, String cs, String keys[], HashMap attributes) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(path, cs);
        ps.println("%\n%SSF Annotation Using weka\n%");
        ps.println("@RELATION\tSSF_ANNOTATION");
        for( int i = 0 ; i < keys.length ; i++ )
        {
            if( attributes.get(keys[i]) instanceof MLClassLabels )
            {
               int count  = 1;
               String parts[] =  keys[i].split(" "); 
               
               if( parts.length > 1)
                   count = Integer.parseInt(parts[1]);
               
               if( count == 1 )
               {
                   MLClassLabels tempLabels = (MLClassLabels) attributes.get(keys[i]);
                   ps.print("@ATTRIBUTE\t" + parts[0]+ "\t{" + tempLabels.getInt2LabelMapping().getPropertyValue("" + 0));
                   
                   for (int j = 1; j < tempLabels.getInt2LabelMapping().countProperties(); j++) {
                       ps.print("," + tempLabels.getInt2LabelMapping().getPropertyValue("" + j));
                   }
                   ps.println("}");
               }else{
                   for (int k = 0; k < count; k++) {
                       MLClassLabels tempLabels = (MLClassLabels) attributes.get(keys[i]);
                       ps.print("@ATTRIBUTE\t" + parts[0]+"_" + k + "\t{" + tempLabels.getInt2LabelMapping().getPropertyValue("" + 0));
                       for (int j = 1; j < tempLabels.getInt2LabelMapping().countProperties(); j++) {
                           ps.print("," + tempLabels.getInt2LabelMapping().getPropertyValue("" + j));
                       }
                       ps.println("}");
                   }
               }                                                                
            }else if ( attributes.get(keys[i]) instanceof String )
            {
                int count = 1;
                
                
                String parts[] = keys[i].split("[ ]");

                if (parts.length > 1) {
                    count = Integer.parseInt(parts[1]);
                }
                
                
                if( count == 1 )
                {
                    ps.println("@ATTRIBUTE " + parts[0] + " " + attributes.get(keys[i]));
                }else{
                    for( int l = 0 ; l < count ; l++ )
                    {
                       //System.out.println(" Count-"+l);
                      
                       ps.println("@ATTRIBUTE\t" + parts[0] + "_"+l + "\t" + attributes.get(keys[i])); 
                    }
                }
            }
           
        }
        /*
         *Save the attibutes first here. 
         */
        ps.println("@DATA");
        
        printArffTagged(ps);
        
    }
     public void printArffTagged(PrintStream ps)
    {
        DefaultDataSequence testRecord = null;
        
        startScan();

        while (hasNext()) {
            testRecord = (DefaultDataSequence) next();

            for (int i = 0; i < testRecord.length(); i++) {
                ps.println(testRecord.x(i));
            }
        }
    }
     
        private Vector parse(String line) {
        StringTokenizer tkn = new StringTokenizer(line, ",");

       
        String temp = new String();
        temp = tkn.nextToken();

        String first = new String();
        if (temp.length() > 1) {
            //first = temp.substring(1, temp.length() - 1);
            first = temp.substring(0);
        }


        String second = new String();
        while (tkn.hasMoreElements()) {
            second = tkn.nextToken();
        }

        Vector v = new Vector();
        v.insertElementAt(first, 0);
        String[] arr = second.split("-");

        if (arr.length >= 2) {
            v.insertElementAt(arr[0], 1);
            v.insertElementAt(arr[1], 2);
            if (second.length() < 2) {
                v.insertElementAt("NotPresentInFile", 2);
            } else {
                v.insertElementAt(second.substring(2), 3);
            }
        } else {
            v.insertElementAt(second.substring(0), 1);
            if (second.length() < 2) {
                v.insertElementAt("NotPresentInFile", 2);
            } else {
                v.insertElementAt(second.substring(2), 2);
            }
        }
        return v;
    }
     
     
     public void readArffTagged(String path, String cs) throws FileNotFoundException, IOException {
     
         System.out.println("ajklshdnkjas");
         
        DefaultArffData ats = new DefaultArffData();
        BufferedReader inReader = null;

        if (cs != null && cs.equals("") == false) {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        } else {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        }

        String line = "";


        int flagRead = 0;

        while ((line = inReader.readLine()) != null) {
            DefaultDataSequence defaultDataSequence = new DefaultDataSequence();
            /*if (line.equals("")) {
                if (defaultDataSequence != null) {
                    data.add(defaultDataSequence);
                }

                defaultDataSequence = new DefaultDataSequence();
            }*/
            try{
            if (line.substring(0, 5).equalsIgnoreCase("@DATA")) {
                flagRead = 1;
                continue;
            }
            }catch(StringIndexOutOfBoundsException ex){
                
            }

            if (flagRead == 0) {
                continue;
            }

            Vector list = ats.parse(line);

            defaultDataSequence.add_x(list);
            data.add(defaultDataSequence);
            System.out.println(" In Default Arff Data readArffTagged() "+list.elementAt(0)+" "+list.elementAt(1)+"  DataSeq, list-"+defaultDataSequence.x(0));
            
        }
        
    }
     
     
    
    
        public class DefaultArffDocument
    {
        int startSequence;
        int endSequence;
        
        String inputPath;
        String outputPath;
        
        public DefaultArffDocument()
        {
            
        }

        public DefaultArffDocument(int startIndex, int endIndex, String inputPath, String outputPath)
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
