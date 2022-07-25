/*
 * OtherFeaturesImpl.java
 *
 * Created on June 20, 2008, 3:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction.impl;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.tree.SanchayMutableTreeNode;
/**
 *
 * @author Anil Kumar Singh
 */
public class OtherFeaturesImpl {
    
    /**
     * Creates a new instance of OtherFeaturesImpl
     */
   
    private SSFNode root ;
    private SSFSentence sent;
    private List<SanchayMutableTreeNode> children;
    
    public OtherFeaturesImpl() {
        
    }
    
    // Function to check whether the current node is the start of a sentence
    public boolean isSentenceStart(SSFNode node)
    {
        root = (SSFNode)node.getRoot();
        children = root.getAllLeaves();
        if (node.equals(children.get(0))) {
            return true;
        }
        else {
            return false;
        }
    }
    
    // Function to check whether the current node is a number or not
    public boolean isNumber(SSFNode node)
    {
        int length = node.getLexData().length();
        int i;
        char character;
        for (i=0;i<length;i++)
        {
            character = (char)node.getLexData().charAt(i);
            if ((int)character < 48 || (int)character > 57 ) {
                break;
            }
        }
        
        if (i==length) {
            return true;
        }
        else {
            return false;
        }
    }
    
    // Function to check whether the node is alphanumeric or not (consisting of numbers + alphabets only)
//    public boolean isAlphaNum(SSFNode node)
//    {
//        int length = node.getLexData().length();
//        int i;
//        char character;
//        for (i=0;i<length;i++)
//        {
//            character = (char)node.getLexData().charAt(i);
//            if ((int)character < 48 || (int)character > 57 )
//                break;
//        }
//        
//        if (i==length)
//            return true;
//        else 
//            return false;
//    }
    
//    public boolean isPunctuation(SSFNode node)
//    {
//        
//    }
    
    
    
    
    
    // Functions to return word classes "WC" and "BWC" -- refer Proceedings Pg 90

    public String wordClass(SSFNode node)
    {
        int length = node.getLexData().length();
        int i;
        String wc = "";
        char character;
        List punc = new ArrayList();
        punc.add("!");
        punc.add("\"");
        punc.add(".");
        punc.add("'");
        punc.add("?");
        punc.add("'");
        punc.add("`");
        punc.add("(");
        punc.add(")");
        punc.add("-");
        punc.add(":");
        punc.add(",");
        for (i=0;i<length;i++)
        {
            character = (char)node.getLexData().charAt(i);
            if (((int)character >= 65 && (int)character <= 90) || ((int)character >= 97 && (int)character <= 122) ) {
                wc = wc + "a";
            }
            else if ((int)character >= 48 && (int)character <= 57 ) {
                wc = wc + "0";
            }
            else if (punc.contains(character)) { 
                wc = wc + "p";
            }
            else {
                wc = wc + "-";
            }
        }
        
        return wc;
    }

    public String briefWordClass(SSFNode node)
    {
        String wc = wordClass(node);
        int length = wc.length();
        int i;
        String bwc = "";
        bwc = bwc + wc.charAt(0);
        char character;
        for (i=1;i<length;i++)
        {
            if (wc.charAt(i) == wc.charAt(i-1))
                 continue ;
            else
                bwc = bwc + wc.charAt(i);
        }
        
        return bwc;
    }
    
    
    
    // Function to check whether there are 4 consecutive numbers
    public boolean isFourNumbers(SSFNode node)
    {
        SSFNode node2,node3,node4;
        int ascii,i;
        int length = node.getLexData().length();
        char character;
        if (length != 4)
            return false;
        for (i=0;i<length;i++)
        {
            character = (char)node.getLexData().charAt(i);
            ascii = (int)character;
            if (ascii < 48 || ascii > 57 )
                break;   
        }
        if (i==length)
            return true;
        else
            return false;
    }
    
    public int characterCount(SSFNode node)
    {
        return node.getLexData().length();
    }
    
    
    
    public void printOtherFeatures( SSFStory ssfStory, PrintStream ps) {
        SSFSentence ssfSentence;
        int senCount = ssfStory.countSentences();
        for (int i = 0; i < senCount; i++) {
            ssfSentence = ssfStory.getSentence(i);
            List<SanchayMutableTreeNode> POSTag =  ((SSFNode)ssfSentence.getRoot()).getAllLeaves();
            int nodeCount = POSTag.size();
            for (int j = 0; j < nodeCount; j++) {
                boolean isNumber;
                isNumber = isNumber((SSFNode)POSTag.get(j));
                System.out.println( ((SSFNode)POSTag.get(j)).getLexData() + GlobalProperties.getIntlString("_this_value_indicae_number(1)_or_not(0)_") + isNumber);
                ps.println( ((SSFNode)POSTag.get(j)).getLexData() + GlobalProperties.getIntlString("_this_value_indicae_number(1)_or_not(0)_") + isNumber);
                boolean isSentenceStart = isSentenceStart((SSFNode)POSTag.get(j));
                System.out.println( ((SSFNode)POSTag.get(j)).getLexData() + GlobalProperties.getIntlString("_this_value_indicae_whether__start(1)_or_not(0)_") +  isSentenceStart);
                boolean isFour = isFourNumbers((SSFNode)POSTag.get(j));
                ps.println(((SSFNode)POSTag.get(j)).getLexData() + GlobalProperties.getIntlString("_this_value_indicae_whether__start(1)_or_not(0)_") +  isSentenceStart);
                System.out.println( ((SSFNode)POSTag.get(j)).getLexData() + GlobalProperties.getIntlString("_this_value_indicae_whether__fourNoss(1)_or_not(0)_") +  isFour);
                ps.println(((SSFNode)POSTag.get(j)).getLexData() + GlobalProperties.getIntlString("_this_value_indicae_whether__fourNoss(1)_or_not(0)_") +  isFour);
                int len = characterCount((SSFNode)POSTag.get(j));
                System.out.println( ((SSFNode)POSTag.get(j)).getLexData() + " length =" +  len);
                ps.println(((SSFNode)POSTag.get(j)).getLexData() + " length =" +  len);
                String wordClass = wordClass((SSFNode)POSTag.get(j));
                System.out.println( ((SSFNode)POSTag.get(j)).getLexData() + " wc = " +  wordClass);
                ps.println(((SSFNode)POSTag.get(j)).getLexData() + " wc = " +  wordClass);
                String briefWordClass = briefWordClass((SSFNode)POSTag.get(j));
                System.out.println( ((SSFNode)POSTag.get(j)).getLexData() + " bwc = " +  briefWordClass);
                ps.println( ((SSFNode)POSTag.get(j)).getLexData() + " bwc = " +  briefWordClass);
                
                
                
            }
        }
    }
               
     public static void main(String[] args) throws Exception {
         
         
         OtherFeaturesImpl win = new OtherFeaturesImpl();
//        FSProperties fsp = new FSProperties();
//        SSFProperties ssfp = new SSFProperties();
         SSFStory testStory = new SSFStoryImpl();
//        SSFStory trainStory = new SSFStoryImpl();
//
         try {
//            PrintStream ps = new PrintStream("E:/Project @IIIT/training-hindi.arff", "UTF-8");
                PrintStream ps1 = new PrintStream("/home1/sanchay/automatic-annotation/file/experiment/other-features.txt", GlobalProperties.getIntlString("UTF-8"));
             testStory.readFile("/home1/sanchay/automatic-annotation/file/301.nitu.utf8");
//         win.printFeatureToArffDir("E:/Project @IIIT/training-hindi", ps);
             win.printOtherFeatures(testStory,ps1);
         } catch (FileNotFoundException ex) {
             ex.printStackTrace();
         } catch (UnsupportedEncodingException ex) {
             ex.printStackTrace();
         }
         
         
     }
     
     
     
}
