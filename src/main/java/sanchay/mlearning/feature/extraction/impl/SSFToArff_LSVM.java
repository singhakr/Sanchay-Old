/*
 * SSFToSVMImpl.java
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
import java.util.HashMap;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import sanchay.GlobalProperties;
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
import sanchay.tree.SanchayMutableTreeNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class SSFToArff_LSVM extends WindowFeaturesImpl{
    
    /** Creates a new instance of SSFToSVMImpl */
    public SSFToArff_LSVM() {
    }
    
    public SSFToArff_LSVM(String path, PrintStream ps, boolean whetherTest)
    {
        printFeatureToSSFDir(path, ps, whetherTest);
    }
    
    HashMap hMap;
    static int staticInt;
    public void printFeatureTrain(SSFStory story, PrintStream ps) {
        if(staticInt == 0)
        {
            hMap = new HashMap();

            hMap.put("B-NEP", 1);
            hMap.put("I-NEP", 2);
            hMap.put("O-NEP", 3);
            hMap.put("B-NETE", 4);
            hMap.put("I-NETE", 5);
            hMap.put("O-NETE", 6);
            hMap.put("B-NETO", 7);
            hMap.put("I-NETO", 8);
            hMap.put("O-NETO", 9);
            hMap.put("B-NETI", 10);
            hMap.put("I-NETI", 11);
            hMap.put("O-NETI", 12);
            hMap.put("B-NED", 13);
            hMap.put("I-NED", 14);
            hMap.put("O-NED", 15);
            hMap.put("B-NEL", 16);
            hMap.put("I-NEL", 17);
            hMap.put("O-NEL", 18);
            hMap.put("B-NEN", 19);
            hMap.put("I-NEN", 20);
            hMap.put("O-NEN", 21);
            hMap.put("B-NET", 22);
            hMap.put("I-NET", 23);
            hMap.put("O-NET", 24);
            hMap.put("B-NEO", 25);
            hMap.put("I-NEO", 26);
            hMap.put("O-NEO", 27);
            hMap.put("B-NEM", 28);
            hMap.put("I-NEM", 29);
            hMap.put("O-NEM", 30);
            hMap.put("B-NEA", 31);
            hMap.put("I-NEA", 32);
            hMap.put("O-NEA", 33);
            hMap.put("B-NETP",34);
            hMap.put("I-NETP",35);
            hMap.put("O-NETP", 36);
            hMap.put("B-NOT", 37);
            hMap.put("I-NOT", 38);
            hMap.put("O-NOT", 39);
            staticInt++;
        }
        int countNE = 0;
        int scount = story.countSentences();
        String subst = "nil";

        int prefixNum = 3;
        int suffixNum = 3;

        

        for (int i = 0; i < scount; i++) {
            int flagSentence = 0;
            SSFSentence sen = story.getSentence(i);

            List chunkVector = getChunkInformation(sen);//this is the chunk information of the word of a sentence

            List<SanchayMutableTreeNode> words = ((SSFPhrase) sen.getRoot()).getAllLeaves();

            int ccount = words.size();

            String classWord = new String();
            for (int j = 0; j < ccount; j++)
            {
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
                
                String lexdata = ((SSFNode) words.get(j)).getLexData();

                List wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);

                int wcount = wordFeatures.size();
                // to Print the word window
                String s = ((SSFNode) words.get(j)).getLexData().replaceAll("'", "");
                 ps.print(GlobalProperties.getIntlString("'") + ((SSFNode) words.get(j)).getLexData() + GlobalProperties.getIntlString("',"));
                for (int k = 0; k < wcount; k++)
                {
                        String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
                            ps.print(GlobalProperties.getIntlString("'") + temp + GlobalProperties.getIntlString("',"));
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
                                 ps.print(GlobalProperties.getIntlString("'") + ((String) charFeatures.get(n)) + GlobalProperties.getIntlString("',"));

                //this is to  print the chunk attributes of the given window of the word       
                List chunkFeatures = getChunkAttributeWindow(chunkVector, j , 3 , WINDOW_DIRECTION_BOTH);

                for (int k = 0; k < chunkFeatures.size(); k++) {
                     ps.print(GlobalProperties.getIntlString("'") + (String) chunkFeatures.get(k) + GlobalProperties.getIntlString("',"));
                }

                ps.print(GlobalProperties.getIntlString("'") + (String) chunkVector.get(j) + GlobalProperties.getIntlString("',"));


               List parentChunks = getParentChunkAttribute((SSFNode)words.get(j));
               int pcount = parentChunks.size();
               for (int k = 0; k < pcount; k++) {
                     ps.print(GlobalProperties.getIntlString("'") + (String) parentChunks.get(k) + GlobalProperties.getIntlString("',"));
                }
                ps.print(GlobalProperties.getIntlString("'") + hMap.get(classWord) + GlobalProperties.getIntlString("'"));
                ps.println();
            }
            countNE++;
        }
    }
    
    public void printFeatureTest(SSFStory story, PrintStream ps) {
        if(staticInt == 0)
        {
            hMap = new HashMap();

            hMap.put(GlobalProperties.getIntlString("B-NEP"), 1);
            hMap.put(GlobalProperties.getIntlString("I-NEP"), 2);
            hMap.put(GlobalProperties.getIntlString("O-NEP"), 3);
            hMap.put(GlobalProperties.getIntlString("B-NETE"), 4);
            hMap.put(GlobalProperties.getIntlString("I-NETE"), 5);
            hMap.put(GlobalProperties.getIntlString("O-NETE"), 6);
            hMap.put(GlobalProperties.getIntlString("B-NETO"), 7);
            hMap.put(GlobalProperties.getIntlString("I-NETO"), 8);
            hMap.put(GlobalProperties.getIntlString("O-NETO"), 9);
            hMap.put(GlobalProperties.getIntlString("B-NETI"), 10);
            hMap.put(GlobalProperties.getIntlString("I-NETI"), 11);
            hMap.put(GlobalProperties.getIntlString("O-NETI"), 12);
            hMap.put(GlobalProperties.getIntlString("B-NED"), 13);
            hMap.put(GlobalProperties.getIntlString("I-NED"), 14);
            hMap.put(GlobalProperties.getIntlString("O-NED"), 15);
            hMap.put(GlobalProperties.getIntlString("B-NEL"), 16);
            hMap.put(GlobalProperties.getIntlString("I-NEL"), 17);
            hMap.put(GlobalProperties.getIntlString("O-NEL"), 18);
            hMap.put(GlobalProperties.getIntlString("B-NEN"), 19);
            hMap.put(GlobalProperties.getIntlString("I-NEN"), 20);
            hMap.put(GlobalProperties.getIntlString("O-NEN"), 21);
            hMap.put(GlobalProperties.getIntlString("B-NET"), 22);
            hMap.put(GlobalProperties.getIntlString("I-NET"), 23);
            hMap.put(GlobalProperties.getIntlString("O-NET"), 24);
            hMap.put(GlobalProperties.getIntlString("B-NEO"), 25);
            hMap.put(GlobalProperties.getIntlString("I-NEO"), 26);
            hMap.put(GlobalProperties.getIntlString("O-NEO"), 27);
            hMap.put(GlobalProperties.getIntlString("B-NEM"), 28);
            hMap.put(GlobalProperties.getIntlString("I-NEM"), 29);
            hMap.put(GlobalProperties.getIntlString("O-NEM"), 30);
            hMap.put(GlobalProperties.getIntlString("B-NEA"), 31);
            hMap.put(GlobalProperties.getIntlString("I-NEA"), 32);
            hMap.put(GlobalProperties.getIntlString("O-NEA"), 33);
            hMap.put(GlobalProperties.getIntlString("B-NETP"),34);
            hMap.put(GlobalProperties.getIntlString("I-NETP"),35);
            hMap.put(GlobalProperties.getIntlString("O-NETP"), 36);
            hMap.put(GlobalProperties.getIntlString("B-NOT"), 37);
            hMap.put(GlobalProperties.getIntlString("I-NOT"), 38);
            hMap.put(GlobalProperties.getIntlString("O-NOT"), 39);
            staticInt++;
        }
        int countNE = 0;
        int scount = story.countSentences();
        String subst = GlobalProperties.getIntlString("nil");

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
                 classWord = (String) chunkVector.get(j)+ GlobalProperties.getIntlString("-");

                SSFNode parent = (SSFNode) ((SSFNode) words.get(j)).getParent();

                FeatureStructures fs = parent.getFeatureStructures();
                if(fs != null && fs.countAltFSValues() > 0)
                {
                    FeatureStructure fs1 = fs.getAltFSValue(0);
                    FeatureAttribute fa =  fs1.getAttribute(GlobalProperties.getIntlString("ne"));
                    if(fa != null && fa.countAltValues() > 0)
                    {
                        String wordFeature = (String) fa.getAltValue(0).getValue();

                        if(wordFeature != null && (isValidSymbol(wordFeature) == true) ){
                            classWord += wordFeature;
                        }
                        else
                            classWord += GlobalProperties.getIntlString("NOT");

                        if(wordFeature != null && (isContainingNE(wordFeature) == true))
                            flagSentence = 1;
                    }
                    else
                    {
                        classWord += GlobalProperties.getIntlString("NOT");
                    }
                }                   
                 
                else
                {
                    classWord += GlobalProperties.getIntlString("NOT");
                }
                
                ps.print(GlobalProperties.getIntlString("'") + hMap.get(classWord) + GlobalProperties.getIntlString("_"));
                String lexdata = ((SSFNode) words.get(j)).getLexData();

                List wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);

                int wcount = wordFeatures.size();
                // to Print the word window
                 ps.print(GlobalProperties.getIntlString("'") + ((SSFNode) words.get(j)).getLexData());
                for (int k = 0; k < wcount; k++)
                {
                        String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
                            ps.print(GlobalProperties.getIntlString("'") + temp);
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
                                 ps.print(GlobalProperties.getIntlString("'") + ((String) charFeatures.get(n)));

                //this is to  print the chunk attributes of the given window of the word       
//                Vector chunkFeatures = getChunkAttributeWindow(chunkVector, j , 3 , WINDOW_DIRECTION_BOTH);
//
//                for (int k = 0; k < chunkFeatures.size(); k++) {
//                     ps.print("'" + (String) chunkFeatures.get(k));
//                }
//
//                ps.print("'" + (String) chunkVector.get(j));
//
//
//               Vector parentChunks = getParentChunkAttribute((SSFNode)words.get(j));
//               int pcount = parentChunks.size();
//               for (int k = 0; k < pcount; k++) {
//                     ps.print("'" + (String) parentChunks.get(k));
//                }
                ps.println();
            }
            countNE++;
        }
    }
    
    public static void main(String[] args) throws Exception {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();
        SSFStory testStory = new SSFStoryImpl();
        SSFStory trainStory = new SSFStoryImpl();
        PrintStream psLibsvm;
        SSFToArff_LSVM obj;
        boolean whetherTest=false;

        whetherTest = Boolean.parseBoolean(JOptionPane.showInputDialog(GlobalProperties.getIntlString("Enter_0_for_Train_1_for_Test,_Then_enter_any_file_in_the_input_directory_and_then_enter_output_file")));

        JFileChooser jfc = new JFileChooser();
        
        jfc.showOpenDialog(null);
        
        String inputDir = jfc.getSelectedFile().getAbsolutePath();

        jfc.showOpenDialog(null);
        
        String outputFile = jfc.getSelectedFile().getAbsolutePath();
        
        int index = inputDir.lastIndexOf('/');
        System.out.println(GlobalProperties.getIntlString("\n\n\n\n")+whetherTest+GlobalProperties.getIntlString("\n\n\n\n"));
        inputDir = inputDir.substring(0, index);

        try {
            psLibsvm = new PrintStream(outputFile, GlobalProperties.getIntlString("UTF-8"));
            obj = new SSFToArff_LSVM(inputDir, psLibsvm, whetherTest);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
