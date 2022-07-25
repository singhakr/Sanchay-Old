/*
 * SSFToMaxentImpl.java
 *
 * Created on September 3, 2008, 6:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction.impl;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import javax.swing.*;
import sanchay.GlobalProperties;
import sanchay.tree.SanchayMutableTreeNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class SSFToMaxentImpl extends WindowFeaturesImpl{
    
    /** Creates a new instance of SSFToMaxentImpl */
    public SSFToMaxentImpl() {
    }
    
        public SSFToMaxentImpl(String path, PrintStream ps, boolean whetherTest)
        {
            printFeatureToSSFDir(path, ps, whetherTest);
        }

    public void printFeatureTrain(SSFStory story, PrintStream ps) {
        int countNE = 0;
        int scount = story.countSentences();
        String subst = "nil";

        int prefixNum = 3;
        int suffixNum = 3;

        //this section prints the header information in the arff file 

        for (int i = 0; i < scount; i++) {
            int flagSentence = 0;
            SSFSentence sen = story.getSentence(i);

            List chunkVector = getChunkInformation(sen);//this is the chunk information of the word of a sentence

            List<SanchayMutableTreeNode> words = ((SSFPhrase) sen.getRoot()).getAllLeaves();

            int ccount = words.size();

            String classWord = new String();
            for (int j = 0; j < ccount; j++)
            {
                String lexdata = ((SSFNode) words.get(j)).getLexData();

                List wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);

                int wcount = wordFeatures.size();
                // to Print the word window
                
                 ps.print(((SSFNode) words.get(j)).getLexData() + " ");
                for (int k = 0; k < wcount; k++)
                {
                    String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
                    ps.print(temp + " ");
                }

                       List charFeatures = new ArrayList();

                       for (int m = 1; m <= prefixNum; m++) {
                           if(m > lexdata.length())
                               charFeatures.add(subst);
                           else
                               charFeatures.add(lexdata.substring(0, m));
                       }

                       for (int n = 1; n <= suffixNum; n++) {
                           if(n > lexdata.length())
                               charFeatures.add(subst);
                           else
                               charFeatures.add(lexdata.substring(lexdata.length() - n, lexdata.length()));
                       }

                       for (int n = 0; n < charFeatures.size() ; n++)
                                 ps.print(((String) charFeatures.get(n)) + " ");

                 classWord = (String) chunkVector.get(j)+ "-";

                SSFNode parent = (SSFNode) ((SSFNode) words.get(j)).getParent();

                FeatureStructures fs = parent.getFeatureStructures();
                if(fs != null && fs.countAltFSValues() > 0)
                {
                    FeatureStructure fs1 = fs.getAltFSValue(0);
                    FeatureAttribute fa =  fs1.getAttribute("ne");
                    if(fa != null && fa.countAltValues() > 0)
                    {
                        String wordFeature = (String) fa.getAltValue(0).getValue();

                        if(wordFeature != null && (isValidSymbol(wordFeature) == true) ){
                            classWord += wordFeature;
                        }
                        else
                            classWord += "NOT";

                        if(wordFeature != null && (isContainingNE(wordFeature) == true))
                            flagSentence = 1;
                    }
                    else
                    {
                        classWord += "NOT";
                    }
                }                   
                 
                else
                {
                    classWord += "NOT";
                }
                
                ps.println(classWord);
            }
            ps.println();
            countNE++;
        }
    }
    
    public void printFeatureTest(SSFStory story, PrintStream ps) {

        int countNE = 0;
        int scount = story.countSentences();
        String subst = "nil";

        int prefixNum = 3;
        int suffixNum = 3;


        for (int i = 0; i < scount; i++) {
            int flagSentence = 0;
            SSFSentence sen = story.getSentence(i);

            List chunkVector = getChunkInformation(sen);
            //this is the chunk information of the word of a sentence

            List<SanchayMutableTreeNode> words = ((SSFPhrase) sen.getRoot()).getAllLeaves();

            int ccount = words.size();

            String classWord = new String();
            for (int j = 0; j < ccount; j++)
            {
                String lexdata = ((SSFNode) words.get(j)).getLexData();

                List wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);

                int wcount = wordFeatures.size();
                // to Print the word window
                
                //System.out.println(wcount);
                 System.out.println(((SSFNode) words.get(j)).getLexData() + " ");
                 ps.print(((SSFNode) words.get(j)).getLexData() + " ");
                for (int k = 0; k < wcount; k++)
                {
                    String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
                    ps.print(temp + " ");
                }

                       List charFeatures = new ArrayList();

                       for (int m = 1; m <= prefixNum; m++) {
                           if(m > lexdata.length())
                               charFeatures.add(subst);
                           else
                               charFeatures.add(lexdata.substring(0, m));
                       }

                       for (int n = 1; n <= suffixNum; n++) {
                           if(n > lexdata.length())
                               charFeatures.add(subst);
                           else
                               charFeatures.add(lexdata.substring(lexdata.length() - n, lexdata.length()));
                       }

                       for (int n = 0; n < charFeatures.size() ; n++)
                                 ps.print(((String) charFeatures.get(n)) + " ");
                
                ps.println("?");

//
//               Vector parentChunks = getParentChunkAttribute((SSFNode)words.get(j));
//               int pcount = parentChunks.size();
//               for (int k = 0; k < pcount; k++) {
//                     ps.print((String) parentChunks.get(k) + " ");
//                }
//                       
//                 classWord = (String) chunkVector.get(j)+ "-";
//
//                SSFNode parent = (SSFNode) ((SSFNode) words.get(j)).getParent();
//
//                FeatureStructures fs = parent.getFeatureStructures();
//                if(fs != null && fs.countAltFSValues() > 0)
//                {
//                    FeatureStructure fs1 = fs.getAltFSValue(0);
//                    FeatureAttribute fa =  fs1.getAttribute("ne");
//                    if(fa != null && fa.countAltValues() > 0)
//                    {
//                        String wordFeature = (String) fa.getAltValue(0).getValue();
//
//                        if(wordFeature != null && (isValidSymbol(wordFeature) == true) ){
//                            classWord += wordFeature;
//                        }
//                        else
//                            classWord += "NOT";
//
//                        if(wordFeature != null && (isContainingNE(wordFeature) == true))
//                            flagSentence = 1;
//                    }
//                    else
//                    {
//                        classWord += "NOT";
//                    }
//                }                   
//                 
//                else
//                {
//                    classWord += "NOT";
//                }
//                
//                ps.println(classWord);
            }
            
            ps.println();
            countNE++;
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();
        SSFStory testStory = new SSFStoryImpl();
        SSFStory trainStory = new SSFStoryImpl();
        PrintStream psLibsvm;
        SSFToMaxentImpl obj;
        boolean whetherTest=false;

        whetherTest = Boolean.parseBoolean(JOptionPane.showInputDialog(GlobalProperties.getIntlString("Enter_0_for_Train_1_for_Test,_Then_enter_any_file_in_the_input_directory_and_then_enter_output_file")));

        JFileChooser jfc = new JFileChooser();
        
        jfc.showOpenDialog(null);
        
        String inputDir = jfc.getSelectedFile().getAbsolutePath();

        jfc.showOpenDialog(null);
        
        String outputFile = jfc.getSelectedFile().getAbsolutePath();
        
        int index = inputDir.lastIndexOf('/');
        //System.out.println(index);
        inputDir = inputDir.substring(0, index);
        //System.out.println(inputDir);
        try {
            psLibsvm = new PrintStream(outputFile, GlobalProperties.getIntlString("UTF-8"));
            obj = new SSFToMaxentImpl(inputDir, psLibsvm, whetherTest);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
