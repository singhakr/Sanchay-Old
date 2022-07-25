/*
 * WindowFeaturesImpl.java
 *
 * Created on June 20, 2008, 3:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.tree.SanchayMutableTreeNode;
/**
 *
 * @author Anil Kumar Singh
 */

public class SSFRefiner {
    
    /** Creates a new instance of WindowFeaturesImpl */
    public SSFRefiner() {
    }
    
    /**
     * Returns SSFLexItem in the vector
     */
    public boolean isContainingNE(String string)
    {
        
       String symbolArr[] = {"NEP","NETE","NETO","NETI","NED","NEL","NEN","NET","NEO","NEM","NEA","NETP"};
       int flag = 0;
        for (int i = 0; i < symbolArr.length ; i++) {
            String string1 = symbolArr[i];
            
        
           if(string.equals(string1) == true)
           {
               flag = 1;
                 return true;
             }
       }
          return false;
    }

    public void printFeatureToSSFDir(String inDir) {
        File inDirFile = new File(inDir);
        
        if(inDirFile.exists() && inDirFile.isDirectory())
        {
            File files[] = inDirFile.listFiles();
            
            for (int i = 0; i < files.length; i++) {
                
                if(files[i].isFile())
                {
                    SSFStory inStory = new SSFStoryImpl();

                    try {
                        inStory.readFile(files[i].getAbsolutePath(), GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
                        if(i==0)
                            System.out.println(inStory.getSentence(0).convertToRawText());
                        printFeatureToArff(inStory);
                        
                        if(i==0)
                            System.out.println(inStory.getSentence(0).convertToRawText());
                        inStory.save(files[i].getAbsolutePath(), GlobalProperties.getIntlString("UTF-8"));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }                
            }
        }
    }

    public int printFeatureToArff(SSFStory story) {
        int countNE = 0;
        int scount = story.countSentences();
        String subst = "?";
        
        int prefixNum = 3;
        int suffixNum = 3;
       
        //this section prints the header information in the arff file 
        int tmpp=0;
        for (int i = 0; i < scount; i++) {
            int flagSentence = 0;
            SSFSentence sen = story.getSentence(i);
            /*if(i==0)
            System.out.println(sen.convertToRawText());*/
            
            List<SanchayMutableTreeNode> words = ((SSFPhrase) sen.getRoot()).getAllLeaves();
            
            int ccount = words.size();

            
            for (int j = 0; j < ccount; j++)
            {
               String lexdata = ((SSFNode) words.get(j)).getLexData();
                
                       
                       
                SSFNode parent = (SSFNode) ((SSFNode) words.get(j)).getParent();
                
                FeatureStructures fs = parent.getFeatureStructures();
                if(fs != null && fs.countAltFSValues() > 0)
                {
                    FeatureStructure fs1 = fs.getAltFSValue(0);
                    FeatureAttribute fa =  fs1.getAttribute("ne");
                    if(fa != null && fa.countAltValues() > 0)
                    {
                        String wordFeature = (String) fa.getAltValue(0).getValue();

                        if(wordFeature != null && (isContainingNE(wordFeature) == true)) {
                            flagSentence = 1;
                            break;
                        }
                    }
                }
            }
            
            if(flagSentence == 0)
            {
                story.removeSentence(i);
                scount--;
                i--;
            }
        }
        return countNE;
    }
    
       public static void main(String[] args) throws Exception {
        
        SSFRefiner ssfRefiner = new SSFRefiner();
        ssfRefiner.printFeatureToSSFDir("/home1/sanchay/automatic-annotation/training-hindi-refined");
    }
  }

