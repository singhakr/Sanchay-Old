/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.svm;

import iitb.crf.DataIter;
import iitb.crf.DataSequence;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.crf.DefaultDataSequence;

/**
 *
 * @author anil
 */
public class DefaultSVMData implements DataIter {

    protected List data;
    protected Iterator iterator;
    protected List documents;

    public DefaultSVMData() {
        data = new ArrayList();
        documents = new ArrayList();
    }

    @Override
    public void startScan() {
        iterator = data.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public DataSequence next() {
        DataSequence seq = (DataSequence) iterator.next();

        if (seq == null) {
            iterator = null;
        }

        return seq;
    }

    public DataSequence getDataSequence(int index) {
        return (DataSequence) data.get(index);
    }

    public void add(DataSequence seq) {
        data.add(seq);
    }

    public void addDocumentBoundaries(int startIndex, int endIndex, String inputPath, String outputPath) {
        DefaultSVMDocument doc = new DefaultSVMDocument(startIndex, endIndex, inputPath, outputPath);

        documents.add(doc);
    }

    public DefaultSVMDocument removeDocumentBoundaries(int index) {
        return (DefaultSVMDocument) documents.remove(index);
    }

    public DefaultSVMDocument getDocument(int index) {
        return (DefaultSVMDocument) documents.get(index);
    }

    public int countDocuments() {
        return documents.size();
    }

    public int countDataSequences() {
        return data.size();
    }

    public DefaultSVMData getCRFText(int index) {
        DefaultSVMDocument doc = (DefaultSVMDocument) documents.get(index);

        return (DefaultSVMData) getDefaultSVMData(doc.getSequenceStart(), doc.getSequenceEnd() - doc.getSequenceStart());
    }

    public DefaultSVMData getDefaultSVMData(int startSequenceNum, int windowSize) {
        if (startSequenceNum < 0 || startSequenceNum >= documents.size()
                || (startSequenceNum + windowSize) > documents.size()) {
            return null;
        }

        DefaultSVMData doc = new DefaultSVMData();

        for (int i = 0; i < windowSize; i++) {
            doc.add((DataSequence) data.get(startSequenceNum + i));
        }

        return doc;
    }

    public void readRaw(String path, String cs) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;

        if (cs != null && cs.equals("") == false) {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        } else {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        }

        String line = "";
        DefaultDataSequence defaultDataSequence = null;

        while ((line = inReader.readLine()) != null) {
            String parts[] = line.split("[ ]");

            defaultDataSequence = new DefaultDataSequence();

            for (int i = 0; i < parts.length; i++) {
                defaultDataSequence.add_x(parts[i]);
            }

            data.add(defaultDataSequence);
        }
    }

    public void readSSFRaw(String path, String cs) throws FileNotFoundException, IOException, Exception {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++) {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++) {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
            }

            data.add(defaultDataSequence);
        }
    }

    public void readTagged(String path, String cs) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;

        if (cs != null && cs.equals("") == false) {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        } else {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        }

        String line = "";
        DefaultDataSequence defaultDataSequence = new DefaultDataSequence();

        int i = 0;

        while ((line = inReader.readLine()) != null) {
            if (line.equals("")) {
                if (defaultDataSequence != null) {
                    data.add(defaultDataSequence);
                }

                i = 0;
                defaultDataSequence = new DefaultDataSequence();
            }

            String parts[] = line.split("[ ]");

            //if(parts.length >= 2)
            {
                String label = parts[parts.length - 1];
                label = label.substring(1);

                String instance = "";

                for (int j = 0; j < parts.length - 1; j++) {
                    instance += parts[j];
                }
                SSFNode ssfNode = new SSFNode();
                ssfNode.setLexData(line);

                defaultDataSequence.add_x(ssfNode);
                defaultDataSequence.set_y(i++, 0);
            }
        }

    }

    public void readSSFPOS(String path, String cs) throws FileNotFoundException, IOException, Exception {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++) {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++) {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
            }

            data.add(defaultDataSequence);
        }
    }

    public void readSSFChunk(String path, String cs) throws FileNotFoundException, IOException, Exception {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++) {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++) {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
            }

            data.add(defaultDataSequence);
        }
    }

    public void readSSFFeature(String path, String cs, String featureName) throws FileNotFoundException, IOException, Exception {
        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.readFile(path, cs);

        int scount = ssfStory.countSentences();

        DefaultDataSequence defaultDataSequence = null;

        for (int i = 0; i < scount; i++) {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            int ccount = root.countChildren();

            defaultDataSequence = new DefaultDataSequence();

            for (int j = 0; j < ccount; j++) {
                SSFNode ssfNode = root.getChild(j);
                defaultDataSequence.add_x(ssfNode);
            }

            data.add(defaultDataSequence);
        }
    }

    public void saveRaw(String path, String cs) throws FileNotFoundException, IOException {
        PrintStream ps = new PrintStream(path, cs);
        printRaw(ps);
    }

    public void printRaw(PrintStream ps) {
        DefaultDataSequence testRecord = null;

        startScan();

        while (hasNext()) {
            testRecord = (DefaultDataSequence) next();

            testRecord.printRaw(ps);
        }
    }

    /*
     * This routine was the wierd one, check it out before you use.
     */
    public void readSvmTagged(String ssfPath, String mlPath, String cs) throws FileNotFoundException, IOException, Exception {
        BufferedReader inReader = null;

        SSFStoryImpl ssfStory = new SSFStoryImpl();

        SSFSentence sen = new SSFSentenceImpl();

        SSFPhrase root = new SSFPhrase();

        List leaves = new ArrayList();

        String line = "";

        //DefaultDataSequence defaultDataSequence = new DefaultDataSequence();

        int i = 0, j = 0, wordCount = 0;
        
        if (cs != null && cs.equals("") == false) {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(mlPath), cs));
            ssfStory.readFile(ssfPath, cs);
        } else {
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(mlPath)));
            ssfStory.readFile(ssfPath);
        }
        DefaultDataSequence defaultDataSequence = new DefaultDataSequence();

        int scount = ssfStory.countSentences();
        for (i = 0; i < scount; i++) {
            defaultDataSequence = new DefaultDataSequence();

            wordCount = 0;

            sen = ssfStory.getSentence(i);

            root = sen.getRoot();

            leaves = root.getAllLeaves();

            int wcount = leaves.size();

            for (j = 0; j < wcount; j++) {
                if ((line = inReader.readLine()) != null) {
                    String parts[] = line.split("[ ]");

                    String subParts[] = new String[3];

                    subParts = parts[0].split("[.]");

                    SSFNode ssfNode = new SSFNode();

                    ssfNode = (SSFNode) leaves.get(j);

                    //System.out.println(" parts"+parts[0]+"   subParts"+subParts[0]);

                    int label = Integer.parseInt(subParts[0]);

                    //System.out.println( ssfNode+"  "+label);

                    defaultDataSequence.add_x(ssfNode);
                    //System.out.println( "Word -"+ ssfNode.getLexData()+" POS-"+ssfNode.getName()+"count - "+ wordCount + "Sentence Count-"+ i + " WordCount-"+ j);
                    defaultDataSequence.set_y(wordCount++, label);

                } else {
                    System.err.println("The SSF File and MLfile do not match.");
                }
            }
            data.add(defaultDataSequence);
        }



        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
                
         for (j = 0; j < parts.length - 1; j++)
         {
         instance += "_" + parts[j];
         }
                
         defaultDataSequence.add_x(instance);
         defaultDataSequence.set_y(i++, Integer.parseInt(label) - 1);
         }
         }    */
    }

    public void saveTagged(String path, String cs) throws FileNotFoundException, IOException {
        PrintStream ps = new PrintStream(path, cs);
        printTagged(ps);
    }

    public void printTagged(PrintStream ps) {
        DefaultDataSequence testRecord = null;

        startScan();

        while (hasNext()) {
            testRecord = (DefaultDataSequence) next();

            testRecord.printTagged(ps);
        }
    }

    public class DefaultSVMDocument {

        int startSequence;
        int endSequence;
        String inputPath;
        String outputPath;

        public DefaultSVMDocument() {
        }

        public DefaultSVMDocument(int startIndex, int endIndex, String inputPath, String outputPath) {
            this.startSequence = startIndex;
            this.endSequence = endIndex;
            this.inputPath = inputPath;
            this.outputPath = outputPath;
        }

        public int getSequenceStart() {
            return startSequence;
        }

        public void setSequenceStart(int s) {
            startSequence = s;
        }

        public int getSequenceEnd() {
            return endSequence;
        }

        public void setSequenceEnd(int e) {
            endSequence = e;
        }

        public String getInputPath() {
            return inputPath;
        }

        public void setInputPath(String p) {
            inputPath = p;
        }

        public String getOutputPath() {
            return outputPath;
        }

        public void setOutputPath(String p) {
            outputPath = p;
        }
    }
}
