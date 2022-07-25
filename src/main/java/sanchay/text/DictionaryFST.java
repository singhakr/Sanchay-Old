/*
 * DictionaryFST.java
 *
 * Created on April 3, 2006, 7:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text;

import java.util.*;
import java.io.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import sanchay.GlobalProperties;
import sanchay.gui.common.SanchayLanguages;
import sanchay.mlearning.common.ModelScore;
import sanchay.text.adhoc.PreProcessRules;
import sanchay.text.spell.PhoneticModelOfScripts;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.tree.gui.SanchayTreeViewerJPanel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class DictionaryFST implements Serializable {
    
    protected DictionaryFSTNode forwardFST;
    protected DictionaryFSTNode backwardFST;
    
    protected String langEnc;
    
    protected String dictFile;
    protected String charset;
    
    protected String forwardFSTFile;
    protected String backwardFSTFile;
    
    protected boolean isLetterBased = false;
    protected boolean compress = false;

    protected PhoneticModelOfScripts phoneticModelOfScripts;
    
    protected int minFrequency = 0;
    protected long wordCount;
    protected float totalScoreCutoff = 20.0f;
    protected float scaledScoreCutoff = 10.0f;
//    protected float totalScoreCutoff = 30.0f;
//    protected float scaledScoreCutoff = 15.0f;
//    protected float totalScoreCutoff = 20.0f;
//    protected float scaledScoreCutoff = 10.0f;
    protected float MAX_PENALTY = 20.0f;
//    protected float MAX_PENALTY = 35.0f;
    protected float horzArcPenalty = (float) MAX_PENALTY;
    protected float vertArcPenalty = (float) MAX_PENALTY;
    
    public static final Character rootChar = new Character('\u0000');
    public static final Character terminalChar = new Character('\u0000');

    public static final int DIAGONAL_ARC = 0;
    public static final int VERTICAL_ARC = 1;
    public static final int HORIZONTAL_ARC = 2;
    
    // Instance of AksharData for managing Akshars
    public AksharData data;

    public int leaves = 0;
    public Vector featureList = new Vector();
    
    /** Creates a new instance of DictionaryFST */
    public DictionaryFST() throws FileNotFoundException, IOException, ClassNotFoundException {
        super();

        dictFile = "";
        charset = GlobalProperties.getIntlString("UTF-8");

        forwardFSTFile = GlobalProperties.getHomeDirectory() + "/" + "tmp/dict.forward";
        backwardFSTFile = GlobalProperties.getHomeDirectory() + "/" + "tmp/dict.backward";

 //       read();
    }

    public DictionaryFST(String cs)
    {
        super();
        charset = cs;
    }

    public DictionaryFST(String inFile, String cs, String ffile, String bfile)
    throws FileNotFoundException, IOException {
        super();
        
        dictFile = inFile;
        charset = cs;
        forwardFSTFile = ffile;
        backwardFSTFile = bfile;
        
        //compile(inFile, cs, ffile, bfile);
        compileAkshar(inFile, cs, ffile, bfile);
    }
    
    public DictionaryFST(String ffile, String bfile)
    throws FileNotFoundException, IOException, ClassNotFoundException {
        super();
        
        dictFile = "";
        charset = GlobalProperties.getIntlString("UTF-8");
        
        forwardFSTFile = ffile;
        backwardFSTFile = bfile;
        
        read();
    }
    
    public DictionaryFST(String inFile, String cs, String ffile, String bfile, boolean letterBased)
    throws FileNotFoundException, IOException {
        super();
        
        dictFile = inFile;
        charset = cs;
        forwardFSTFile = ffile;
        backwardFSTFile = bfile;
        
        if(letterBased) {
            compile(inFile, cs, ffile, bfile);
            isLetterBased = letterBased;
        } else
            compileAkshar(inFile, cs, ffile, bfile);
    }
    
    public DictionaryFST(String inFile, String cs, String ffile, String bfile, boolean letterBased, int minFreq)
    throws FileNotFoundException, IOException {
        super();
        
        dictFile = inFile;
        charset = cs;
        forwardFSTFile = ffile;
        backwardFSTFile = bfile;
        
        minFrequency = minFreq;
        
        if(letterBased) {
            compile(inFile, cs, ffile, bfile);
            isLetterBased = letterBased;
        } else
            compileAkshar(inFile, cs, ffile, bfile);
    }
    
    public DictionaryFST(String inFile, String cs, String ffile, String bfile, String langEnc, boolean isAkshar, boolean isJodo)
        throws FileNotFoundException, IOException {
        super();
        
        dictFile = inFile;
        charset = cs;
        forwardFSTFile = ffile;
        backwardFSTFile = bfile;
        
        this.langEnc = langEnc;
        
        if (isAkshar==true && isJodo==true) {
            //compile(inFile, cs, ffile, bfile);
            compileAksharJodo(inFile, cs, ffile, bfile);
        } else {
            compile(inFile, cs, ffile, bfile);
        }
        
        //compileAkshar(inFile, cs, ffile, bfile);
    }

    /**
     * @return the totalScoreCutoff
     */
    public float getTotalScoreCutoff()
    {
        return totalScoreCutoff;
    }

    /**
     * @param totalScoreCutoff the totalScoreCutoff to set
     */
    public void setTotalScoreCutoff(float totalScoreCutoff)
    {
        this.totalScoreCutoff = totalScoreCutoff;
    }

    /**
     * @return the scaledScoreCutoff
     */
    public float getScaledScoreCutoff()
    {
        return scaledScoreCutoff;
    }

    /**
     * @param scaledScoreCutoff the scaledScoreCutoff to set
     */
    public void setScaledScoreCutoff(float scaledScoreCutoff)
    {
        this.scaledScoreCutoff = scaledScoreCutoff;
    }

    /**
     * @return the phoneticModelOfScripts
     */
    public PhoneticModelOfScripts getPhoneticModelOfScripts()
    {
        return phoneticModelOfScripts;
    }

    /**
     * @param phoneticModelOfScripts the phoneticModelOfScripts to set
     */
    public void setPhoneticModelOfScripts(PhoneticModelOfScripts phoneticModelOfScripts)
    {
        this.phoneticModelOfScripts = phoneticModelOfScripts;
    }
    
    public void loadPhoneticModelOfScripts()
    {
        try {
            phoneticModelOfScripts = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
                    GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void compile(String inFile, String cs, String ffile, String bfile)
    throws FileNotFoundException, IOException {
        forwardFST = new DictionaryFSTNode(false, true);
        backwardFST = new DictionaryFSTNode(true, true);
        
        File ifile = new File(ffile);
        
        if(ifile.exists()) {
            try {
                read();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            BufferedReader inReader = null;
            
            if(cs != null && cs.equals("") == false)
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
            else
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            
            String line = "";
            
            while((line = inReader.readLine()) != null) {
                if(line.contains("::") == false) {
                    String parts[] = line.split("\t");
                    String wd = parts[0];
                    String freqStr = "";
                    
                    if(parts.length == 2 && parts[1] != null && parts[1].equals("") == false)
                           freqStr = parts[1];
                    else
                    {
                        String msg = "Error in the file " + inFile;
                        msg += "\nThe format required is a text file with two tab_separated columns.";
                        msg += "\nThe first column should have a_word and the second its frequency.";

                        System.err.println(msg);

                        throw new IOException(msg);
                    }


                    long freq = Long.parseLong(freqStr);
                    
                    if(freq < minFrequency)
                        continue;
                    
                    String fs = null;
                    
                    if(parts.length == 3) {
                        fs = parts[2];
                    }
                    
                    forwardFST.compile(wd, null, freq, fs);
                    backwardFST.compile(UtilityFunctions.reverseString(wd), null, freq, fs);
                    
                    wordCount++;
                    if (wordCount % 1000 == 0) {
                        System.out.println(wordCount);
                    }
                }
            }

            if(compress)
                compressStrings();            
            
            //PrintStream temp = new PrintStream("/home/sanchay/tmp/FST/punj1");
            //printForwardFST(temp);
            write();
        }
    }

    public void compile(LinkedHashMap<String, Long> words, LinkedHashMap<String, String> fss)  throws FileNotFoundException, IOException {
        forwardFST = new DictionaryFSTNode(false);
        backwardFST = new DictionaryFSTNode(true);

        Iterator<String> itr = words.keySet().iterator();

        while(itr.hasNext())
        {
            String word = itr.next();
            long freq = words.get(word).longValue();
            String fs = "";

            if(fss != null && fss.get(word) != null)
                fs = fss.get(word);

            forwardFST.compile(word, null, freq, fs);
            backwardFST.compile(UtilityFunctions.reverseString(word), null, freq, fs);

            wordCount++;
            if (wordCount % 1000 == 0) {
                System.out.println(wordCount);
            }
        }

        if(compress)
            compressStrings();
    }

    public void compileAkshar(LinkedHashMap<String, Long> words, LinkedHashMap<String, String> fss)  throws FileNotFoundException, IOException {
        forwardFST = new DictionaryFSTNode(false);
        backwardFST = new DictionaryFSTNode(true);

        // To add Akshar Information
        data = new AksharData(langEnc);
        data.readScript(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/devType.txt");
        data.makeGrammar();

        PreProcessRules aRule= new PreProcessRules();
        aRule.readRules(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/preProcessAll");

        Iterator<String> itr = words.keySet().iterator();

        while(itr.hasNext())
        {
            String word = itr.next();
            long freq = words.get(word).longValue();
            String fs = "";

            if(fss != null && fss.get(word) != null)
                fs = fss.get(word);

            forwardFST.compileAkshar(aRule.transferString(word), data, null, freq, fs);
//                            }
            //backwardFST.compile(UtilityFunctions.reverseString(line));

            wordCount++;
            if (wordCount % 1000 == 0) {
                System.out.println(wordCount);
            }
        }
    }
    
    public void compileAkshar(String inFile, String cs, String ffile, String bfile)  throws FileNotFoundException, IOException {
        
        File ifile = new File(ffile);
        
        if(ifile.exists()) {
            try {
                read();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            BufferedReader inReader = null;
            
            if(cs != null && cs.equals("") == false)
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
            else
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            
            String line = "";
            LinkedHashMap<String, Long> words = new LinkedHashMap<String, Long>();
            LinkedHashMap<String, String> fss = new LinkedHashMap<String, String>();
            
            while((line = inReader.readLine()) != null) {
                // /*
                if(line.contains("::") == false) {
                    String parts[] = line.split("\t");
                    String a = parts[0];
                    String freqStr = parts[1];
                    long freq = Long.parseLong(freqStr);
                    
                    if(freq < minFrequency)
                        continue;
                    
                            /*
                            // ARBIT FUNCTION FOR iNEWS
                            String[] splitline = line.split(" ");
                            String a = "";
                            if (splitline.length == 2)
                            {
                                    a = splitline[1];
                            }
                            // */
//                            if (a.matches("।|,|.|\"|//|...")==false)
//                            {
                    String fs = null;

                    words.put(a, freq);
                    
                    if(parts.length == 3) {
                        fs = parts[2];
                        fss.put(a, fs);
                    }
                }
            }

            compileAkshar(words, fss);
            
            //compressStrings();
            
            write();
        }
        
        //PrintStream temp = new PrintStream("/home/sanchay/tmp/FST/punj");
        //printForwardFST(temp);
//		data.getAksharSimilarityList();
        data.readSimilarityList(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/SimilarityListDict");
        //data.writeSimilarityList("/home/sanchay/tmp/SimilarityListDict1");
    }
    
    public void compileAksharJodo(String inFile, String cs, String ffile, String bfile)  throws FileNotFoundException, IOException {
        forwardFST = new DictionaryFSTNode(false);
        backwardFST = new DictionaryFSTNode(true);
        
        // To add Akshar Information
        data = new AksharData(langEnc);
        
        String devType = GlobalProperties.getHomeDirectory() + "/" + "data/joRo/devType-" + SanchayLanguages.getLanguageCodeFromLECode(langEnc) + ".txt";
        
        data.readScript(devType);
        data.makeGrammarJodo();
        
        BufferedReader inReader = null;
        
        if(cs != null && cs.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
        
        String line = "";
        
        PreProcessRules aRule= new PreProcessRules();
        aRule.readRules(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/preProcessAll");
        
//        PrintStream ps = new PrintStream(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.txt", "UTF-8");
        
        while((line = inReader.readLine()) != null) {
            
//            ps.println(line);
            
            if(line.equals("") || line.matches("^[ ]+$"))
                continue;
            // /*
            
            String parts[] = line.split(" ");
            
            if(parts.length < 2)
                continue;
                
            String a = parts[1];
            a = a.trim();
                        /*
                        // ARBIT FUNCTION FOR iNEWS
                        String[] splitline = line.split(" ");
                        String a = "";
                        if (splitline.length == 2)
                        {
                                a = splitline[1];
                        }
                        // */
//			if (a.matches("।|,|.|\"|//|...")==false)
            {
                String wordCompile = aRule.transferString(a);
                forwardFST.compileAkshar(wordCompile, data, null);
            }
            //backwordFST.compile(UtilityFunctions.reverseString(line));
            
            wordCount++;
            if (wordCount % 1000 == 0) {
                System.out.println(wordCount);
            }
        }
        
        //compressStrings();
        
//        write();
        //PrintStream temp = new PrintStream("/home/sanchay/tmp/FST/punj");
        //printForwardFST(temp);
//		data.getAksharSimilarityList();
        data.readSimilarityList(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/SimilarityListDict");
        //data.writeSimilarityList("/home/sanchay/tmp/SimilarityListDict1");
    }

    public void compileFC(Vector phnSeq1, DictionaryFSTNode forFST, DictionaryFSTNode backFST, String fs)
    throws FileNotFoundException, IOException {

            Vector phnSeq1_rev = new Vector();
            for(int j = 0;j<phnSeq1.size();j++)
                phnSeq1_rev.add(j, phnSeq1.elementAt(phnSeq1.size()-j-1));

            for (int i = 0;i<phnSeq1.size();i++){
                String wd = phnSeq1.get(i).toString();
                String wd_rev = phnSeq1_rev.get(i).toString();


                    String wd_arr[] = wd.split(" ");
                    String wd_rev_arr[] = wd_rev.split(" ");
                    Vector vec = new Vector();
                    Vector vec_rev= new Vector();
                    for(int j = 0;j<wd_arr.length;j++)
                    {
                        vec.add(j,wd_arr[j]);
                        vec_rev.add(j,wd_rev_arr[j]);
                    }
                    forFST.compileFC(vec, null, fs);
//                    System.out.println("fst -- "+forwardFST.fst.toString());
                    backFST.compileFC(vec_rev, null, fs);
                 }

            if(compress)
            {
                forFST.compressStrings();
                backFST.compressStrings();
            }

    }

    public void compareFC(Vector phnSeq, DictionaryFSTNode forFST, DictionaryFSTNode backFST, String fs)
    {
//        System.out.println(forwardFST.countChildren());
        Vector phnSeq_rev = new Vector();
        for(int j = 0;j<phnSeq.size();j++)
            phnSeq_rev.add(j, phnSeq.elementAt(phnSeq.size()-j-1));
        for(int i = 0;i<phnSeq.size();i++)
        {
            String wd = phnSeq.get(i).toString();
            String wd_rev = phnSeq_rev.get(i).toString();
            String wd_arr[] = wd.split(" ");
            String wd_rev_arr[] = wd_rev.split(" ");
            Vector vec = new Vector();
            Vector vec_rev= new Vector();
            for(int j = 0;j<wd_arr.length;j++)
            {
                vec.add(j,wd_arr[j]);
                vec_rev.add(j,wd_rev_arr[j]);
            }
            Vector tempVec = new Vector();
            tempVec = (Vector)vec.clone();
            if(hasSequence(forFST, tempVec)){
                removeSimilarSequence(forFST, vec);
            }
            else
            {
                try
                {
                    compileFC(vec,forFST,backFST,fs);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            Vector tempVec_rev = new Vector();
            tempVec_rev = (Vector)vec_rev.clone();
            if (hasSequence(backFST, tempVec_rev))
            	removeSimilarSequence(backFST, vec_rev);
            else
            {
                 try
                 {
                    compileFC(vec_rev,forFST,backFST,fs);
                 }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void removeSimilarSequence(DictionaryFSTNode dictNode, Vector phnSeq)
    {
        String str = phnSeq.get(0).toString();
        Enumeration children = dictNode.getChildren();
        for(int i=0;i<dictNode.countChildren();i++)
        {
            DictionaryFSTNode nextChild = dictNode.getChild(children.nextElement().toString());
            String nextString = nextChild.getString();
//            System.out.println(str+" -- "+nextString);
            if(str.equalsIgnoreCase(nextString))
            {
                phnSeq.remove(0);
//                System.out.println("I'm coming here!");
                if(nextChild.isLeaf())
                {
                    while(nextChild.isLeaf())
                    {

                        DictionaryFSTNode temp = nextChild.parent;
//                        System.out.print(nextChild.getString()+" ");
                        if(temp!=null)
                        {
                            String tmpStr = nextChild.getString();
                            temp.fst.remove(tmpStr);
                            nextChild = null;
//                          System.out.println("see -- "+nextChild.getString());
                            nextChild = temp;
                        }
                        else
                            break;
                    }
//                    featureList.clear();
//                    traverseTree(dictNode);
//                    System.out.println(" one sequence removed! ");
                    return;
                }
                if(!phnSeq.isEmpty())
                    removeSimilarSequence(nextChild,phnSeq);
                else
                    return;
            }
        }
        return;
    }

    public boolean hasSequence(DictionaryFSTNode dictNode, String string)
    {
        Enumeration children = dictNode.getChildren();

        for(int i=0;i<dictNode.countChildren();i++)
        {
            String seq = string;
            DictionaryFSTNode nextChild = dictNode.getChild(children.nextElement().toString());
            String nextString = nextChild.getString();

            if((seq.charAt(0) + "").equalsIgnoreCase(nextString))
            {
                seq = seq.substring(1);

                if(nextChild.isWordEnd() && seq.isEmpty())
                    return true;
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                {
                    return hasSequence(nextChild, seq);
                }
                else
                    return false;
            }
            else if(nextString.startsWith(seq) && nextString.length() > 0)
            {
                seq = seq.substring(nextString.length());

                if(nextChild.isWordEnd() && seq.isEmpty())
                    return true;
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                    return hasSequence(nextChild, seq);
                else
                    return false;
            }
        }

        return false;
    }

    public DictionaryFSTNode getMatchingNode(DictionaryFSTNode dictNode, String string)
    {
        Enumeration children = dictNode.getChildren();

        for(int i=0;i<dictNode.countChildren();i++)
        {
            String seq = string;
            
            DictionaryFSTNode nextChild = dictNode.getChild(children.nextElement().toString());
            String nextString = nextChild.getString();

            if((seq.charAt(0) + "").equalsIgnoreCase(nextString))
            {
                seq = seq.substring(1);

                if(nextChild.isWordEnd() && seq.isEmpty())
                    return nextChild;
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                    return getMatchingNode(nextChild, seq);
                else
                    return null;
            }
            else if(nextString.startsWith(seq) && nextString.length() > 0)
            {
                seq = seq.substring(nextString.length());

                if(nextChild.isWordEnd() && seq.isEmpty())
                    return nextChild;
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                    return getMatchingNode(nextChild, seq);
                else
                    return null;
            }
        }

        return null;
    }

    public boolean hasSubSequence(DictionaryFSTNode dictNode, String string)
    {
        Enumeration children = dictNode.getChildren();

        for(int i=0;i<dictNode.countChildren();i++)
        {
            String seq = string;

            DictionaryFSTNode nextChild = dictNode.getChild(children.nextElement().toString());
            String nextString = nextChild.getString();

            if((seq.charAt(0) + "").equalsIgnoreCase(nextString))
            {
                seq = seq.substring(1);

                if(seq.isEmpty())
                    return true;
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                    return hasSubSequence(nextChild, seq);
                else
                    return false;
            }
            else if(nextString.startsWith(seq) && nextString.length() > 0)
            {
                seq = seq.substring(nextString.length());

                if(seq.isEmpty())
                    return true;
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                    return hasSubSequence(nextChild, seq);
                else
                    return false;
            }
        }

        return false;
    }

    public void getMatchingNodes(DictionaryFSTNode dictNode, String string, Vector<DictionaryFSTNode> matchingNodes)
    {
        Enumeration children = dictNode.getChildren();

        for(int i=0;i<dictNode.countChildren();i++)
        {
            String seq = string;
            
            DictionaryFSTNode nextChild = dictNode.getChild(children.nextElement().toString());
            String nextString = nextChild.getString();

            if((seq.charAt(0) + "").equalsIgnoreCase(nextString))
            {
                seq = seq.substring(1);

                if(seq.isEmpty())
                {
                    if(nextChild.isLeaf())
                        matchingNodes.add(nextChild);
                    else
                    {
                        Vector<DictionaryFSTNode> nodes = new Vector<DictionaryFSTNode>();
                        nextChild.getAllWords(nodes);
                        matchingNodes.addAll(nodes);
                    }
                    
                    continue;
                }
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                    getMatchingNodes(nextChild, seq, matchingNodes);
                else
                    continue;
            }
            else if(nextString.startsWith(seq) && nextString.length() > 0)
            {
                seq = seq.substring(nextString.length());

                if(seq.isEmpty())
                {
                    if(nextChild.isLeaf())
                        matchingNodes.add(nextChild);
                    else
                    {
                        Vector<DictionaryFSTNode> nodes = new Vector<DictionaryFSTNode>();
                        nextChild.getAllWords(nodes);
                        matchingNodes.addAll(nodes);
                    }

                    continue;
                }
                else if(!(nextChild.isLeaf() || seq.isEmpty()))
                    getMatchingNodes(nextChild, seq, matchingNodes);
                else
                    continue;
            }
        }

        return;
    }

    public boolean hasSequence(DictionaryFSTNode dictNode, Vector sequence)
    {

//        System.out.println("Received vector: "+phnSeq.toString()+" size: "+phnSeq.size()+" children: "+dictNode.countChildren()+" str-- "+dictNode.getString());

        Enumeration children = dictNode.getChildren();
        for(int i=0;i<dictNode.countChildren();i++)
        {
            String str = sequence.get(0).toString();
            Vector phnSeq = sequence;
            
            DictionaryFSTNode nextChild = dictNode.getChild(children.nextElement().toString());
            
            String nextString = nextChild.getString();

            if(str.equalsIgnoreCase(nextString))
            {
                phnSeq.remove(0);

                if(nextChild.isWordEnd() && phnSeq.isEmpty())
                    return true;
                else if(!(nextChild.isLeaf() || phnSeq.isEmpty()))
                    return hasSequence(nextChild,phnSeq);
                else
                    return false;
            }
        }
        return false;
    }
    
   public void traverseTree(DictionaryFSTNode dictNode)
    {
//        System.out.println("no of leaves -- "+dictNode.leaves);

        if (dictNode.isLeaf())
        {
            leaves++;
            String features = dictNode.getString();
            DictionaryFSTNode temp = dictNode;
            while(temp.parent!=null)
            {
                temp = temp.parent;
                if(temp.parent!=null)
                    features = temp.getString()+" "+features;
//                System.out.println("feature "+features+" added!");
            }

            featureList.add(features);
        }
        Enumeration enm = dictNode.getChildren();
        for(int i = 0;i<dictNode.countChildren();i++)
        {
            DictionaryFSTNode temp = dictNode.getChild(enm.nextElement().toString());
            traverseTree(temp);
        }

    }
    
    public DictionaryFSTNode getRoot() {
        return forwardFST;
    }
    
    public DictionaryFSTNode getRoot(boolean rev) {
        if(rev)
            return backwardFST;
        
        return forwardFST;
    }
    
    public AksharData geAksharData() {
        return data;
    }
    
    public boolean isLetterBased() {
        return isLetterBased;
    }
    
    public void read() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(forwardFSTFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        isLetterBased = ois.readBoolean();
        wordCount = ois.readLong();
        
        forwardFST = new DictionaryFSTNode(false);
        forwardFST.loadObject(ois, null);
//	forwardFST = (DictionaryFSTNode) ois.readObject();
        
        ois.close();
        
        fis = new FileInputStream(backwardFSTFile);
        ois = new ObjectInputStream(fis);
        
        backwardFST = new DictionaryFSTNode(true);
        backwardFST.loadObject(ois, null);
//	backwardFST = (DictionaryFSTNode) ois.readObject();
        
        ois.close();
        
        // This is to Init AksharData file which would not be present if its read
        data = new AksharData(langEnc);
        data.readScript(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/devType.txt");
        data.readSimilarityList(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/SimilarityListDict");
        data.makeGrammar();
        
    }
    
    public void write() throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(forwardFSTFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        
        oos.writeBoolean(isLetterBased);
        oos.writeLong(wordCount);
        
        forwardFST.storeObject(oos);
//	oos.writeObject(forwardFST);
        
        oos.close();
        
        fos = new FileOutputStream(backwardFSTFile);
        oos = new ObjectOutputStream(fos);
        
        backwardFST.storeObject(oos);
//	oos.writeObject(backwardFST);
        
        oos.close();
    }

//    public LinkedHashMap getNearestWords(String str, int nearest, boolean reverse)
//    {
//        if(reverse)
//            return backwardFST.getNearestWords(str, nearest);
//
//        return forwardFST.getNearestWords(str, nearest);
//    }

    public LinkedHashMap<DictionaryFSTNode,Double> getNearestWords(String str, int nearest, boolean reverse)
    {
        LinkedHashMap<DictionaryFSTNode,Double> nearestMatches = getNearestWords(str, false);

        nearestMatches = UtilityFunctions.getTopNElements(nearestMatches, nearest);

        return nearestMatches;
    }

    public LinkedHashMap<DictionaryFSTNode,Double> getNearestWords(String str, boolean reverse)
    {
        LinkedHashMap<DictionaryFSTNode,Double> nearestMatches = new LinkedHashMap<DictionaryFSTNode,Double>(0, 50);
        
        DictionaryFSTNode rootNode = forwardFST;

        if(reverse)
            rootNode = backwardFST;

//        rootNode.expandStrings();

        SimilarityTraversalData similarityTraversalData = new SimilarityTraversalData(str, nearestMatches, (short) str.length(), 0.0f, (char) 0);

        getNearestWords(rootNode, nearestMatches, similarityTraversalData);

        LinkedHashMap<DictionaryFSTNode,Double> sortedNearestMatches = ModelScore.sortElementsByScores(nearestMatches, true);

        return sortedNearestMatches;

//        rootNode.compressStrings();
    }

    protected void getNearestWords(DictionaryFSTNode dictNode, LinkedHashMap<DictionaryFSTNode,Double> nearestMatches,
            SimilarityTraversalData similarityTraversalData)
    {
        if(similarityTraversalData.score > getTotalScoreCutoff())
            return;

        if(nearestMatches == null)
            nearestMatches = new LinkedHashMap<DictionaryFSTNode,Double>(0, 10);

        Enumeration children = dictNode.getChildren();

        for(int i=0;i<dictNode.countChildren();i++)
        {
            String seq = similarityTraversalData.str;
            
            DictionaryFSTNode nextChild = dictNode.getChild(children.nextElement().toString());
            String nextString = nextChild.getString();

//            if(nextChild.getWordString().equals("कर"))
//            {
//                int stop = 1;
//            }
//
//            if(nextChild.getWordString().equals("करता"))
//            {
//                int stop = 1;
//            }

            float minCost = (float) MAX_PENALTY * 2.0f;
            int bestArc = DIAGONAL_ARC;

            // Diagonal arc
            char diagSearchChar = 0;

            if(seq.isEmpty() == false)
                diagSearchChar = seq.charAt(0);
            
            char dictChar = nextString.charAt(0);

            float diagCost = (float) getPhoneticModelOfScripts().getDistance(diagSearchChar, dictChar);

            if(diagCost < minCost)
                minCost = diagCost;

            // Vertical arc
            float vertCost = (float) MAX_PENALTY * 2.0f;

            char vertSearchChar = 0;
            
            if(dictNode.getParent() != null)
            {
//                char vertSearchChar = nextString.charAt(1);
//                searchStringChar = seq.charAt(1);
                vertSearchChar = similarityTraversalData.prevChar;
            }

            vertCost = (float) getPhoneticModelOfScripts().getDistance(vertSearchChar, dictChar);

            if(vertCost < minCost)
            {
                minCost = vertCost;
                bestArc = VERTICAL_ARC;
            }

            // Horizontal arc
            float horzCost = (float) MAX_PENALTY * 2.0f;

            char horzSearchChar = 0;

            if(seq.length() > 1)
            {
//                char horzSearchChar = dictNode.getString().charAt(0);
                horzSearchChar = seq.charAt(1);
            }

            horzCost = (float) getPhoneticModelOfScripts().getDistance(horzSearchChar, dictChar);

            if(horzCost < minCost)
            {
                minCost = horzCost;
                bestArc = HORIZONTAL_ARC;
            }

            int level = nextChild.getLevel();

            boolean prefix = false;

            float maxLength =  Math.max( (float) level, (float) similarityTraversalData.length);
            float lengthDiff =  Math.abs( (float) level - (float) similarityTraversalData.length);
            float scoreNow = similarityTraversalData.score + minCost;

            if((float) level > (float) similarityTraversalData.length)
                prefix = true;

            if(bestArc == DIAGONAL_ARC)
            {
                if(seq.isEmpty() == false)
                    seq = seq.substring(1);
                else if(nextChild.isLeaf() == false)
                {
                    scoreNow += MAX_PENALTY;
                }

//                float scoreNow = (similarityTraversalData.score + minCost)/ (float) (similarityTraversalData.level + 1);

                float scaledScore = scoreNow;
                
//                if(seq.isEmpty() == false && nextChild.isLeaf())
//                {
//                    scaledScore = (scoreNow + (float) MAX_PENALTY * (float) seq.length()) / maxLength;
//                }
//                else
//                    scaledScore = scoreNow / maxLength;

                if(prefix)
                    scaledScore = (scoreNow + (lengthDiff * MAX_PENALTY/4.0f)) / maxLength;
                else
                    scaledScore = (scoreNow + (lengthDiff * MAX_PENALTY)) / maxLength;

                if(nextChild.isWordEnd() && scaledScore <= getScaledScoreCutoff())
                {
                    nearestMatches.put(nextChild, new Double(scaledScore));
                }

//                if(seq.isEmpty())
//                    continue;

                if(nextChild.isLeaf() == false)
                {
                    SimilarityTraversalData nextSimilarityTraversalData = new SimilarityTraversalData(seq, nearestMatches, similarityTraversalData.length,
                            scoreNow, diagSearchChar);

                    getNearestWords(nextChild, nearestMatches, nextSimilarityTraversalData);
                }
            }

            if(bestArc == VERTICAL_ARC)
            {
//                seq = seq.substring(1);
//                float scoreNow = (similarityTraversalData.score + minCost + vertArcPenalty)/ (float) (similarityTraversalData.level + 1);
                if(seq.isEmpty() == false)
                    scoreNow += vertArcPenalty;
                else if(nextChild.isLeaf() == false)
                    scoreNow += vertArcPenalty + MAX_PENALTY;
                
                float scaledScore = scoreNow;

//                if(seq.isEmpty() == false && nextChild.isLeaf())
//                {
//                    scaledScore = (scoreNow + (float) MAX_PENALTY * (float) seq.length()) / maxLength;
//                }
//                else
//                    scaledScore = scoreNow / maxLength;
                if(prefix)
                    scaledScore = (scoreNow + (lengthDiff * MAX_PENALTY/4.0f)) / maxLength;
                else
                    scaledScore = (scoreNow + (lengthDiff * MAX_PENALTY)) / maxLength;

                if(nextChild.isWordEnd() && scaledScore <= getScaledScoreCutoff())
                {
                    nearestMatches.put(nextChild, new Double(scaledScore));
                }

//                if(seq.isEmpty())
//                    continue;

                if(nextChild.isLeaf() == false)
                {
                    SimilarityTraversalData nextSimilarityTraversalData = new SimilarityTraversalData(seq, nearestMatches, similarityTraversalData.length,
                            scoreNow, vertSearchChar);

                    getNearestWords(nextChild, nearestMatches, nextSimilarityTraversalData);
                }
            }

            if(bestArc == HORIZONTAL_ARC)
            {
                if(seq.length() > 2)
                {
                    seq = seq.substring(2);
                    scoreNow += horzArcPenalty;
                }
                else if(nextChild.isLeaf() == false)
                    scoreNow += horzArcPenalty + MAX_PENALTY;

//                float scoreNow = (similarityTraversalData.score + minCost + horzArcPenalty)/ (float) (similarityTraversalData.level + 1);

                float scaledScore = scoreNow;

//                if(seq.isEmpty() == false && nextChild.isLeaf())
//                {
//                    scaledScore = (scoreNow + (float) MAX_PENALTY * (float) seq.length()) / maxLength;
//                }
//                else
//                    scaledScore = scoreNow / maxLength;
                if(prefix)
                    scaledScore = (scoreNow + (lengthDiff * MAX_PENALTY/4.0f)) / maxLength;
                else
                    scaledScore = (scoreNow + (lengthDiff * MAX_PENALTY)) / maxLength;

                if(nextChild.isWordEnd() && scaledScore <= getScaledScoreCutoff())
                {
                    nearestMatches.put(nextChild, new Double(scaledScore));
                }

//                if(seq.isEmpty())
//                    continue;

                if(nextChild.isLeaf() == false)
                {
                    SimilarityTraversalData nextSimilarityTraversalData = new SimilarityTraversalData(seq, nearestMatches, similarityTraversalData.length,
                            scoreNow, horzSearchChar);

                    getNearestWords(nextChild, nearestMatches, nextSimilarityTraversalData);
                }
            }

//            if((seq.charAt(0) + "").equalsIgnoreCase(nextString))
//            {
//                seq = seq.substring(1);
//
//                if(seq.isEmpty() && nextChild.isLeaf())
//                {
//                    matchingNodes.put(nextChild, new Double(similarityTraversalData.score));
//                    return;
//                }
//                else if(!(nextChild.isLeaf() || seq.isEmpty()))
//                    getNearestWords(nextChild, seq, matchingNodes, similarityTraversalData);
//                else
//                    return;
//            }
//            else if(nextString.startsWith(seq) && nextString.length() > 0)
//            {
//                seq = seq.substring(nextString.length());
//
//                if(seq.isEmpty() && nextChild.isLeaf())
//                {
//                    matchingNodes.put(nextChild, new Double(similarityTraversalData.score));
//                    return;
//                }
//                else if(!(nextChild.isLeaf() || seq.isEmpty()))
//                    getNearestWords(nextChild, seq, matchingNodes, similarityTraversalData);
//                else
//                    return;
//            }
        }

        return;
    }
    
    public void showTreeView(boolean reverse) {
        showTreeView(reverse, GlobalProperties.getIntlString("hin::utf8"));
    }
    
    public void showTreeView(boolean reverse, String langEnc) {
        JDialog realTreeDialog = null;
        
        JDialog dialog = null;
        JFrame owner = null;
        
        if(dialog != null)
            realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Tree_Viewer"), true);
        else
            realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Tree_Viewer"), true);
        
        SanchayTreeViewerJPanel realTreeJPanel = null;
        
        if(reverse)
            realTreeJPanel = new SanchayTreeViewerJPanel(new DictionaryFSTMutableNode(backwardFST), SanchayMutableTreeNode.DICT_FST_MODE, langEnc, true);
        else
            realTreeJPanel = new SanchayTreeViewerJPanel(new DictionaryFSTMutableNode(forwardFST), SanchayMutableTreeNode.DICT_FST_MODE, langEnc);
        
        realTreeJPanel.setDialog(realTreeDialog);
        realTreeJPanel.setColumnClasses();
        
        realTreeDialog.add(realTreeJPanel);
        realTreeJPanel.sizeToFit();
        
        realTreeDialog.setVisible(true);
    }
    
    public void showTreeJPanel(boolean reverse) {
        showTreeJPanel(reverse, GlobalProperties.getIntlString("hin::utf8"));
    }
    
    public void showTreeJPanel(boolean reverse, String langEnc) {
        JDialog realTreeDialog = null;
        
        JDialog dialog = null;
        JFrame owner = null;
        
        if(dialog != null)
            realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Tree_Viewer"), true);
        else
            realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Tree_Viewer"), true);
        
        SanchayTreeJPanel realTreeJPanel = null;
        
        if(reverse)
            realTreeJPanel = SanchayTreeJPanel.createDefaultTreeJPanel(new DictionaryFSTMutableNode(backwardFST), langEnc);
        else
            realTreeJPanel = SanchayTreeJPanel.createDefaultTreeJPanel(new DictionaryFSTMutableNode(forwardFST), langEnc);
        
        realTreeJPanel.setDialog(realTreeDialog);
        
        realTreeDialog.add(realTreeJPanel);
        realTreeDialog.setBounds(80, 30, 900, 700);
        
        realTreeDialog.setVisible(true);
        realTreeJPanel.treeJTree.collapseRow(0);
    }
    
    public void save(String inFile, String cs, String ffile, String bfile)
    throws FileNotFoundException, IOException {
        dictFile = inFile;
        charset = cs;
        forwardFSTFile = ffile;
        backwardFSTFile = bfile;
        
        write();
        
        PrintStream ps = new PrintStream(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.txt", charset);
        
        printTable(System.out);
        printTable(ps);
    }
    
    public void printTable(PrintStream ps) {
        forwardFST.printTable(ps);
        backwardFST.printTable(ps);
    }
    
    public void print(PrintStream ps) {
        forwardFST.print(ps, 0);
        backwardFST.print(ps, 0);
    }
    
    public void printForwardFST(PrintStream ps) {
        forwardFST.print(ps, 0);
    }
    
    public void printBackwardFST(PrintStream ps) {
        backwardFST.print(ps, 0);
    }
    
    public void compressStrings() {
        forwardFST.compressStrings();
        backwardFST.compressStrings();
    }

    public void expandStrings() {
        forwardFST.expandStrings();
        backwardFST.expandStrings();
    }
    
    // Direct comparison while traversing the trie
    public String[] getNearestWords(String wrd, int nearest, PrintStream ps) {
        String[] bestWrds = null;
        
        
        
        return bestWrds;
    }
    
    public static String[] getAksharWord(String word, AksharData data) {
        String[] aksharArray = new String[15];
        int i=0;
        
        while (word.length() > 0 && i < 15 ) {
            aksharArray[i] =  data.getAkshar(word);
            word = word.substring(aksharArray[i].length());
            i++;
        }
        
        return aksharArray;
    }

    protected class SimilarityTraversalData
    {

        public String str;
        public LinkedHashMap nearestMatches;
        public short length;
        public float score;
        public char prevChar;

        public SimilarityTraversalData(String str, LinkedHashMap nearestMatches, short length, float score, char prevChar)
        {
            this.str = str;
            this.nearestMatches = nearestMatches;
            this.length = length;
            this.score = score;
            this.prevChar = prevChar;
        }
    }
    
    public static void main(String[] args) {
        try {
//	    DictionaryFST dictionaryFST = new DictionaryFST("props/spell-checker/hindi-word-types-ciil-2.txt", "UTF-8",
//		    "/home/anil/tmp/spell-checker-dict.forward", "/home/anil/tmp/spell-checker-dict.reverse");
//	    DictionaryFST dictionaryFST = new DictionaryFST("/home/anil/tmp/spell-checker-dict-1.txt", "UTF-8",
//		    "/home/anil/tmp/spell-checker-dict.forward", "/home/anil/tmp/spell-checker-dict.reverse");
            
//	    DictionaryFST dictionaryFSTh1 = new DictionaryFST("/home/sanchay/tmp/FST/hindi5k", "UTF-8", "/home/sanchay/tmp/FST/hindi5k.for", "/home/sanchay/tmp/FST/hindi5k.rev");
//	    DictionaryFST dictionaryFSTh2 = new DictionaryFST("/home/sanchay/tmp/FST/hindi25k", "UTF-8", "/home/sanchay/tmp/FST/hindi25k.for", "/home/sanchay/tmp/FST/hindi25k.rev");
//	    DictionaryFST dictionaryFSTh3 = new DictionaryFST("/home/sanchay/tmp/FST/hindi50k", "UTF-8", "/home/sanchay/tmp/FST/hindi50k.for", "/home/sanchay/tmp/FST/hindi50k.rev");
//	    DictionaryFST dictionaryFSTh4 = new DictionaryFST("/home/sanchay/tmp/FST/hindi75k", "UTF-8", "/home/sanchay/tmp/FST/hindi75k.for", "/home/sanchay/tmp/FST/hindi75k.rev");
            
//	    DictionaryFST dictionaryFSTh5 = new DictionaryFST("/home/sanchay/tmp/FST/hindi100k", "UTF-8", "/home/sanchay/tmp/FST/hindi100k.for", "/home/sanchay/tmp/FST/hindi100k.rev");
//
//	    DictionaryFST dictionaryFSTta1 = new DictionaryFST("/home/sanchay/tmp/FST/tamil5k", "UTF-8", "/home/sanchay/tmp/FST/tamil5k.for", "/home/sanchay/tmp/FST/tamil5k.rev");
//	    DictionaryFST dictionaryFSTta2 = new DictionaryFST("/home/sanchay/tmp/FST/tamil25k", "UTF-8", "/home/sanchay/tmp/FST/tamil25k.for", "/home/sanchay/tmp/FST/tamil25k.rev");
//	    DictionaryFST dictionaryFSTta3 = new DictionaryFST("/home/sanchay/tmp/FST/tamil50k", "UTF-8", "/home/sanchay/tmp/FST/tamil50k.for", "/home/sanchay/tmp/FST/tamil50k.rev");
//	    DictionaryFST dictionaryFSTta4 = new DictionaryFST("/home/sanchay/tmp/FST/tamil75k", "UTF-8", "/home/sanchay/tmp/FST/tamil75k.for", "/home/sanchay/tmp/FST/tamil75k.rev");
//	    DictionaryFST dictionaryFSTta5 = new DictionaryFST("/home/sanchay/tmp/FST/tamil100k", "UTF-8", "/home/sanchay/tmp/FST/tamil100k.for", "/home/sanchay/tmp/FST/tamil100k.rev");
            
            
//	    DictionaryFST dictionaryFSTh1 = new DictionaryFST("/home/sanchay/tmp/FST/hindi5k", "UTF-8", "/home/sanchay/tmp/FST/hindi5kAks.for", "/home/sanchay/tmp/FST/hindi5k.rev");
//	    DictionaryFST dictionaryFSTh2 = new DictionaryFST("/home/sanchay/tmp/FST/hindi25k", "UTF-8", "/home/sanchay/tmp/FST/hindi25kAks.for", "/home/sanchay/tmp/FST/hindi25k.rev");
//	    DictionaryFST dictionaryFSTh3 = new DictionaryFST("/home/sanchay/tmp/FST/hindi50k", "UTF-8", "/home/sanchay/tmp/FST/hindi50kAks.for", "/home/sanchay/tmp/FST/hindi50k.rev");
//	    DictionaryFST dictionaryFSTh4 = new DictionaryFST("/home/sanchay/tmp/FST/hindi75k", "UTF-8", "/home/sanchay/tmp/FST/hindi75kAks.for", "/home/sanchay/tmp/FST/hindi75k.rev");
            
//	    DictionaryFST dictionaryFSTh5 = new DictionaryFST("/home/sanchay/tmp/FST/hindi100k", "UTF-8", "/home/sanchay/tmp/FST/hindi100kAks.for", "/home/sanchay/tmp/FST/hindi100k.rev");
//
//	    DictionaryFST dictionaryFSTta1 = new DictionaryFST("/home/sanchay/tmp/FST/tamil5k", "UTF-8", "/home/sanchay/tmp/FST/tamil5kAks.for", "/home/sanchay/tmp/FST/tamil5k.rev");
//	    DictionaryFST dictionaryFSTta2 = new DictionaryFST("/home/sanchay/tmp/FST/tamil25k", "UTF-8", "/home/sanchay/tmp/FST/tamil25kAks.for", "/home/sanchay/tmp/FST/tamil25k.rev");
//	    DictionaryFST dictionaryFSTta3 = new DictionaryFST("/home/sanchay/tmp/FST/tamil50k", "UTF-8", "/home/sanchay/tmp/FST/tamil50kAks.for", "/home/sanchay/tmp/FST/tamil50k.rev");
//	    DictionaryFST dictionaryFSTta4 = new DictionaryFST("/home/sanchay/tmp/FST/tamil75k", "UTF-8", "/home/sanchay/tmp/FST/tamil75kAks.for", "/home/sanchay/tmp/FST/tamil75k.rev");
//	    DictionaryFST dictionaryFSTta5 = new DictionaryFST("/home/sanchay/tmp/FST/tamil100k", "UTF-8", "/home/sanchay/tmp/FST/tamil100kAks.for", "/home/sanchay/tmp/FST/tamil100k.rev");
/*
                DictionaryFST dictionaryFST = new DictionaryFST("/home/sanchay/tmp/FST/CL/25k", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/test.for", "/home/sanchay/tmp/FST/rand.rev");
 
                ///*
                DictionaryFST dictionaryFST1 = new DictionaryFST("/home/sanchay/tmp/FST/CL/hindi", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/hindiAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST2 = new DictionaryFST("/home/sanchay/tmp/FST/CL/oriya", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/oriyaAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST3 = new DictionaryFST("/home/sanchay/tmp/FST/CL/tamil", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/tamilAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST4 = new DictionaryFST("/home/sanchay/tmp/FST/CL/punjabi", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/punjabiAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST5 = new DictionaryFST("/home/sanchay/tmp/FST/CL/malayalam", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/malayalamAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST6 = new DictionaryFST("/home/sanchay/tmp/FST/CL/bengali", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/bengaliAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST7 = new DictionaryFST("/home/sanchay/tmp/FST/CL/assami", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/assamiAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST8 = new DictionaryFST("/home/sanchay/tmp/FST/CL/telugu", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/teluguAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST9 = new DictionaryFST("/home/sanchay/tmp/FST/CL/kannada", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/kannadaAks.for", "/home/sanchay/tmp/FST/rand.rev");
                DictionaryFST dictionaryFST10 = new DictionaryFST("/home/sanchay/tmp/FST/CL/marathi", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/marathiAks.for", "/home/sanchay/tmp/FST/rand.rev");
//		DictionaryFST dictionaryFST11 = new DictionaryFST("/home/sanchay/tmp/FST/CL/mahabharat", "UTF-8", "/home/sanchay/tmp/FST/Cross Results/mahabharatAks.for", "/home/sanchay/tmp/FST/rand.rev");
 
                //*/
            
            DictionaryFST dictionaryFST = new DictionaryFST(GlobalProperties.getHomeDirectory() + "/data/transliteration/Transfer/hq/test.Hin1", GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getHomeDirectory() + "/" + "tmp/spell-checker-dict.forward", GlobalProperties.getHomeDirectory() + "/" + "tmp/spell-checker-dict.reverse");
//		DictionaryFST dictionaryFST = new DictionaryFST("/home/sanchay/tmp/FST/hindi.for", "/home/sanchay/tmp/FST/hindi.rev");
            System.out.println(GlobalProperties.getIntlString("Dictionary_loaded."));
            
//	    dictionaryFST.write();
            //dictionaryFST.read();
            System.out.println(GlobalProperties.getIntlString("Dictionary_Read."));
//	    dictionaryFST.showTreeView();
            dictionaryFST.showTreeJPanel(false);
            dictionaryFST.print(System.out);
            
//	    PrintStream ps = new PrintStream("/home/anil/tmp/spell-checker-dict.forward.txt", "UTF-8");
//	    dictionaryFST.printForwardFST(ps);
            
//	    ps = new PrintStream("/home/anil/tmp/spell-checker-dict.reverse.txt", "UTF-8");
//	    dictionaryFST.printBackwardFST(ps);
        }
//	catch (ClassNotFoundException ex)
//	{
//	    ex.printStackTrace();
//	}
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
}
