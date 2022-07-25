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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.mlearning.feature.extraction.*;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.corpus.ssf.tree.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class WindowFeaturesImpl implements WindowFeatures {

    /**
     * Creates a new instance of WindowFeaturesImpl
     */
    public WindowFeaturesImpl() {
    }

    /**
     * Returns SSFLexItem in the vector
     */
    @Override
    public List getWordWindow(SSFSentence ssfSentence, int wordIndex, int windowSize, int direction, int bool) {
        int i;
        int count;
        SSFNode rootNode;
        String NULL = "?";
        List wordString = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        count = childVector.size();


        if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if (wordIndex >= windowSize) {
                for (i = 0; i < windowSize; i++) {
                    SSFNode feature = (SSFNode) childVector.get(wordIndex - (i + 1));
                    wordString.add(feature);
                }
            } else {
                for (i = 0; i < wordIndex; i++) {
                    SSFNode feature = (SSFNode) childVector.get(wordIndex - (i + 1));
                    wordString.add(feature);
                }

                while (i < windowSize) {
                    SSFNode temp = new SSFNode();
                    temp.setLexData(NULL);
                    temp.setName(NULL);


                    wordString.add(temp);
                    i++;
                }
            }
        }

        if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if (count - (wordIndex + 1) > windowSize) {
                for (i = 0; i < windowSize; i++) {
                    SSFNode feature = (SSFNode) childVector.get((wordIndex + 1) + i);
                    wordString.add(feature);

                }
            } else {
                for (i = 0; i < count - (wordIndex + 1); i++) {
                    SSFNode feature = (SSFNode) childVector.get((wordIndex + 1) + i);
                    wordString.add(feature);
                }
                while (i < windowSize) {

                    SSFNode temp = new SSFNode();
                    temp.setLexData(NULL);
                    temp.setName(NULL);

                    wordString.add(temp);
                    i++;
                }
            }
        }

        return wordString;
    }

    @Override
    public List getCharWindow(SSFNode ssfNode, int charIndex, int windowSize, int direction) {
        List charString = new ArrayList();//make the size general
        String temp;
        int strLen;
        temp = ssfNode.getLexData();
        strLen = temp.length();
        int i;

        if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if (charIndex >= windowSize) {
                String charFeature = temp.substring(charIndex - windowSize, charIndex);
                charString.add(charFeature);
            } else {
                String charFeature = temp.substring(0, charIndex);
                charString.add(charFeature);
            }
        }

        if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if (strLen - charIndex >= windowSize) {
                String charFeature = temp.substring(charIndex, charIndex + windowSize);
                charString.add(charFeature);
            } else {
                String charFeature = temp.substring(charIndex, strLen);
                charString.add(charFeature);
            }
        }



        return charString;
    }

    @Override
    public List getChunkWindow(SSFSentence ssfSentence, int wordIndex, int windowSize, int direction) {


        int i;
        int counter = 0;
        SSFNode rootNode;
        List wordString = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        List chunkFeatures = new ArrayList();
        SSFNode chunkFeature = (SSFNode) ((SSFNode) childVector.get(wordIndex)).getParent();

        if (chunkFeature != null && chunkFeature.getParent() != null) {
            if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
                while ((chunkFeature = chunkFeature.getPrevious()) != null && counter < windowSize) {
                    if (chunkFeature.isLeafNode() == false) {
                        chunkFeatures.add(chunkFeature);
                        counter++;
                    }
                }

            }
        }

        chunkFeature = (SSFNode) ((SSFNode) childVector.get(wordIndex)).getParent();

        if (chunkFeature != null && chunkFeature.getParent() != null) {
            if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
                while ((chunkFeature = chunkFeature.getNext()) != null && counter < windowSize) {
                    if (chunkFeature.isLeafNode() == false) {
                        chunkFeatures.add(chunkFeature);
                        counter++;
                    }
                }
            }
        }

        return chunkFeatures;
    }
    // this function returns the chunk information of a word and like whethe this is a 
    //chunk or not and if chunk then chunk middle or chunk end or chunk beginning or just one word chunk

    public List getChunkInformation(SSFSentence ssfSentence) {
        int i;
        int count;
        SSFNode rootNode;
        List string = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        List chunkPosition;
        childVector = rootNode.getAllLeaves();
        count = childVector.size();
        List status = new ArrayList();
        for (int j = 0; j < childVector.size(); j++) {

            SSFNode temp = (SSFNode) childVector.get(j);
            if (temp.getParent().getParent() == null) {
                status.add(WindowFeatures.CHUNK_BOUNDARY_O);///set that it is a not a chunk
            } else if (((SSFNode) temp.getNextSibling()) != null && ((SSFNode) temp.getPreviousSibling()) != null) {
                if (((SSFNode) temp.getNextSibling()).getParent() == ((SSFNode) temp.getPreviousSibling()).getParent()) {
                    status.add(WindowFeatures.CHUNK_BOUNDARY_I);//set that it is a middle word of the chunk

                }

            } else if (((SSFNode) temp.getNextSibling()) != null && ((SSFNode) temp.getPreviousSibling()) == null) {

                if (((SSFNode) temp.getNextSibling()).getParent() == temp.getParent()) {
                    status.add(WindowFeatures.CHUNK_BOUNDARY_B);//set that its a start node of a chunk

                }
            } else if (((SSFNode) temp.getNextSibling()) == null && ((SSFNode) temp.getPreviousSibling()) != null) {

                if (((SSFNode) temp.getPreviousSibling()).getParent() == temp.getParent()) {
                    status.add(WindowFeatures.CHUNK_BOUNDARY_B);//set that its a start node of a chunk

                }
            } else {
                status.add(WindowFeatures.CHUNK_BOUNDARY_B);// set that it is  a single word chunk
            }
        }

        return status;
    }

    public List getChunkAttributeWindow(List status, int wordIndex, int windowSize, int direction) {
        int i;
        int count = status.size();
        List chunkAttributes = new ArrayList();

        if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if (wordIndex >= windowSize) {
                for (i = 0; i < windowSize; i++) {
                    String Attribute = (String) status.get(i);
                    chunkAttributes.add(Attribute);
                }
            } else {
                for (i = 0; i < wordIndex; i++) {
                    String Attribute = (String) status.get(i);
                    chunkAttributes.add(Attribute);
                }
                while (i < windowSize) {
                    String Attribute = "?";
                    chunkAttributes.add(Attribute); // here u have to set the null values 
                    i++;
                }


            }
        }

        if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if (count - (wordIndex + 1) > windowSize) {
                for (i = 0; i < windowSize; i++) {
                    String Attribute = (String) status.get(i);
                    chunkAttributes.add(Attribute);
                }
            } else {
                for (i = 0; i < count - (wordIndex + 1); i++) {
                    String Attribute = (String) status.get(i);
                    chunkAttributes.add(Attribute);
                }
                while (i < windowSize) {
                    String Attribute = "?";
                    chunkAttributes.add(Attribute); // here u have to set the null values 
                    i++;
                }
            }
        }

        return chunkAttributes;
    }

    public List getParentChunkAttribute(SSFNode ssfNode) {
        List parentStatus = new ArrayList();
        SSFNode temp = (SSFNode) ssfNode.getParent();


        if (temp.getParent() != null) {


            if (((SSFNode) temp.getNextSibling()) != null && ((SSFNode) temp.getPreviousSibling()) != null) {
                if (((SSFNode) temp.getNextSibling()).getParent() == ((SSFNode) temp.getPreviousSibling()).getParent()) {
                    parentStatus.add(WindowFeatures.CHUNK_BOUNDARY_I);//set that it is a middle word of the chunk

                }

            } else if (((SSFNode) temp.getNextSibling()) != null && ((SSFNode) temp.getPreviousSibling()) == null) {

                if (((SSFNode) temp.getNextSibling()).getParent() == temp.getParent()) {
                    parentStatus.add(WindowFeatures.CHUNK_BOUNDARY_B);//set that its a start node of a chunk

                }
            } else if (((SSFNode) temp.getNextSibling()) == null && ((SSFNode) temp.getPreviousSibling()) != null) {

                if (((SSFNode) temp.getPreviousSibling()).getParent() == temp.getParent()) {
                    parentStatus.add(WindowFeatures.CHUNK_BOUNDARY_B);//set that it is end of the chunk
                }

            } else if (((SSFNode) temp.getNextSibling()) == null && ((SSFNode) temp.getPreviousSibling()) == null) {
                parentStatus.add(WindowFeatures.CHUNK_BOUNDARY_B);
            }

        } else {
            parentStatus.add(WindowFeatures.CHUNK_BOUNDARY_O);
        }

        return parentStatus;
    }

    /**
     * Returns Strings in the vector
     *
     * here we pass the feature as a String which we want to retrieve
     */
    @Override
    public List getWordFeatureWindow(SSFSentence ssfSentence, int wordIndex, int windowSize, int direction, String attribName) {
        int i;
        int count;
        SSFNode rootNode;
        List string = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        count = childVector.size();


        if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if (wordIndex >= windowSize) {
                for (i = 0; i < windowSize; i++) {
                    FeatureStructures fs = ((SSFNode) childVector.get(wordIndex - i)).getFeatureStructures();

                    if (fs != null) {
                        FeatureStructure fs1 = fs.getAltFSValue(0);
                        FeatureAttribute fa = fs1.getAttribute(attribName);
                        if (fa != null) {
                            String wordFeature = (String) fa.getAltValue(0).getValue();
                            if (wordFeature != null) {
                                string.add(wordFeature);
                            }
                        }
                    }
                }
            } else {
                for (i = 0; i < wordIndex; i++) {

                    FeatureStructures fs = ((SSFNode) childVector.get(wordIndex - i)).getFeatureStructures();
                    if (fs != null) {
                        FeatureStructure fs1 = fs.getAltFSValue(0);
                        FeatureAttribute fa = fs1.getAttribute(attribName);
                        if (fa != null) {
                            String wordFeature = (String) fa.getAltValue(0).getValue();
                            if (wordFeature != null) {
                                string.add(wordFeature);
                            }
                        }
                    }
                }
            }
        }

        if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if (count - wordIndex >= windowSize) {
                for (i = 0; i < windowSize; i++) {


                    FeatureStructures fs = ((SSFNode) childVector.get(wordIndex + i)).getFeatureStructures();
                    if (fs != null) {
                        FeatureStructure fs1 = fs.getAltFSValue(0);
                        FeatureAttribute fa = fs1.getAttribute(attribName);
                        if (fa != null) {
                            String wordFeature = (String) fa.getAltValue(0).getValue();

                            if (wordFeature != null) {
                                string.add(wordFeature);
                            }
                        }
                    }
                }
            } else {
                for (i = 0; i < count - wordIndex; i++) {

                    FeatureStructures fs = ((SSFNode) childVector.get(wordIndex + i)).getFeatureStructures();
                    if (fs != null) {
                        FeatureStructure fs1 = fs.getAltFSValue(0);

                        FeatureAttribute fa = fs1.getAttribute(attribName);
                        if (fa != null) {
                            String wordFeature = (String) fa.getAltValue(0).getValue();
                            if (wordFeature != null) {
                                string.add(wordFeature);
                            }
                        }
                    }
                }
            }
        }

        return string;
    }

    @Override
    public List getChunkFeatureWindow(SSFSentence ssfSentence, int wordIndex, int windowSize, int direction, String attribName) {



        int i;
        int counter = 0;
        SSFNode rootNode;
        List chunkattribString = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        List chunkFeatures = new ArrayList();
        SSFNode chunkFeature = (SSFNode) ((SSFNode) childVector.get(wordIndex)).getParent();

        if (chunkFeature != null && chunkFeature.getParent() != null) {
            if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
                while ((chunkFeature = chunkFeature.getPrevious()) != null && counter < windowSize) {
                    if (chunkFeature.isLeafNode() == false) {
                        FeatureStructures fs = chunkFeature.getFeatureStructures();
                        if (fs != null) {
                            FeatureStructure fs1 = fs.getAltFSValue(0);
                            FeatureAttribute fa = fs1.getAttribute(attribName);
                            if (fa != null) {
                                String chunkAttrib = (String) fa.getAltValue(0).getValue();
                                if (chunkAttrib != null) {
                                    chunkattribString.add(chunkAttrib);
                                }
                            }
                        }
                        counter++;
                    }
                }

            }
        }

        chunkFeature = (SSFNode) ((SSFNode) childVector.get(wordIndex)).getParent();

        if (chunkFeature != null && chunkFeature.getParent() != null) {
            if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
                while ((chunkFeature = chunkFeature.getNext()) != null && counter < windowSize) {
                    if (chunkFeature.isLeafNode() == false) {
                        FeatureStructures fs = chunkFeature.getFeatureStructures();
                        if (fs != null) {
                            FeatureStructure fs1 = fs.getAltFSValue(0);
                            FeatureAttribute fa = fs1.getAttribute(attribName);
                            if (fa != null) {
                                String chunkAttrib = (String) fa.getAltValue(0).getValue();
                                if (chunkAttrib != null) {
                                    chunkattribString.add(chunkAttrib);
                                }
                            }
                        }
                        counter++;
                    }
                }
            }
        }

        return chunkattribString;
    }

    public boolean isValidSymbol(String string) {

        String symbolArr[] = {"NEP", "NETE", "NETO", "NETI", "NED", "NEL", "NEN", "NET", "NEO", "NEM", "NEA", "NETP", "NOT"};
        int flag = 0;
        for (int i = 0; i < symbolArr.length; i++) {
            String string1 = symbolArr[i];
            if (string.equals(string1) == true) {
                flag = 1;
                return true;
            }
        }
        return false;
    }

    public boolean isContainingNE(String string) {

        String symbolArr[] = {"NEP", "NETE", "NETO", "NETI", "NED", "NEL", "NEN", "NET", "NEO", "NEM", "NEA", "NETP"};
        int flag = 0;
        for (int i = 0; i < symbolArr.length; i++) {
            String string1 = symbolArr[i];


            if (string.equals(string1) == true) {
                flag = 1;
                return true;
            }
        }
        return false;
    }

    public void printFeatureToSSFDir(String inDir, PrintStream ps, boolean whetherTest) {
        File inDirFile = new File(inDir);

        if (inDirFile.exists() && inDirFile.isDirectory()) {
            File files[] = inDirFile.listFiles();

            for (int i = 0; i < files.length; i++) {

                if (files[i].isFile()) {
                    FSProperties fsp = new FSProperties();
                    SSFProperties ssfp = new SSFProperties();
                    SSFStory inStory = new SSFStoryImpl();

                    try {
                        fsp.readDefaultProps();
                        ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"),
                                GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;

                        FeatureStructuresImpl.setFSProperties(fsp);
                        SSFNode.setSSFProperties(ssfp);
                        //System.out.println(files[i].getAbsolutePath());
                        inStory.readFile(files[i].getAbsolutePath(), GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;

                        if (whetherTest) {
                            System.out.println(GlobalProperties.getIntlString("Test"));
                            printFeatureTest(inStory, ps);
                        } else {
                            printFeatureTrain(inStory, ps);
                        }

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

    public void printFeatureTrain(SSFStory story, PrintStream ps) {
        System.out.println(GlobalProperties.getIntlString("In_Parent_Print_Train_Feature"));
    }

    public void printFeatureTest(SSFStory story, PrintStream ps) {
        System.out.println(GlobalProperties.getIntlString("In_Parent_Print_Feature"));
    }
//    public void printFeatureToYamchaDir(String inDir, PrintStream ps) {
//        File inDirFile = new File(inDir);
//        
////        int prefixNum = 3;
////        int suffixNum = 3;
////        
////        
////           String str = "@RELATION namedentity";
////        ps.println(str);
////        for (int i = 0; i < 6; i++) {
////            str = "@ATTRIBUTE   wordfeature"+i+"    string";
////            ps.println(str);
////            
////        }
////        ps.println("@ATTRIBUTE  word    string");
////        
////        
////        /*
////        for (int i = 0; i < 6; i++) {
////            str = "@ATTRIBUTE   tagfeature"+i+"    string";
////            ps.println(str);
////            
////        }*/
////        
////        for (int i = 0; i < (prefixNum + suffixNum); i++) {
////            str = "@ATTRIBUTE   charwindowFeature"+i+"    string";
////            ps.println(str);
////            
////        }
////        
////        
////        for (int i = 0; i < 6; i++) {
////            str = "@ATTRIBUTE   chunkfeature"+i+"    string";
////            ps.println(str);
////            
////        }
////        
////         ps.println("@ATTRIBUTE  wordsChunk    string");
////        
////         
////         ps.println("@ATTRIBUTE  parentChunk    string");
////         
////        ps.print("@ATTRIBUTE class"+"   {B-NEP,I-NEP,O-NEP,B-NETE,I-NETE,O-NETE,B-NETO,I-NETO,O-NETO,B-NETI,I-NETI,O-NETI,B-NED,I-NED,O-NED,B-NEL,I-NEL,O-NEL,B-NEN,I-NEN,O-NEN,B-NET,I-NET,O-NET,B-NEO,I-NEO,O-NEO,B-NEM,I-NEM,O-NEM,B-NEA,I-NEA,O-NEA,B-NETP,I-NETP,O-NETP,B-NOT,I-NOT,O-NOT}");
////        ps.println();
////        ps.println("@DATA");
//        
//       int countNE = 0;
//        
//        if(inDirFile.exists() && inDirFile.isDirectory())
//        {
//            File files[] = inDirFile.listFiles();
//            
//            for (int i = 0; i < files.length; i++) {
//                
//                if(files[i].isFile())
//                {
//                    FSProperties fsp = new FSProperties();
//                    SSFProperties ssfp = new SSFProperties();
//                    SSFStory inStory = new SSFStoryImpl();
//
//                    try {
//                        fsp.read("props/fs-mandatory-attribs.txt", "props/fs-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;
//                        ssfp.read("props/ssf-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;
//
//                        FeatureStructuresImpl.setFSProperties(fsp);
//                        SSFNode.setSSFProperties(ssfp);
//                        System.out.println(files[i].getAbsolutePath());
//                        inStory.readFile(files[i].getAbsolutePath(), "UTF-8"); //throws java.io.FileNotFoundException;
//
//                       countNE += printFeatureToYamcha(inStory, ps);
//
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }                
//            }
//        }
//      
//        System.out.println("\n\n\n\n\n\n\n\n\n\n\n" + countNE + "\n\n\n\n\n\n\n\n\n\n");
//
//    }
//        
//    
//     public int printFeatureToYamcha(SSFStory story, PrintStream ps) {
//        int countNE = 0;
//        int scount = story.countSentences();
//        String subst = "__nil__";
//
//        int prefixNum = 3;
//        int suffixNum = 3;
//
//        //this section prints the header information in the arff file 
//
//        for (int i = 0; i < scount; i++) {
//            int flagSentence = 0;
//            SSFSentence sen = story.getSentence(i);
//
//            Vector chunkVector = getChunkInformation(sen);//this is the chunk information of the word of a sentence
//
//            Vector words = ((SSFPhrase) sen.getRoot()).getAllLeaves();
//
//            int ccount = words.size();
//
////            for (int j = 0; j < ccount; j++){
////               String lexdata = ((SSFNode) words.get(j)).getLexData();
////               lexdata = lexdata.replaceAll("'", "?");
////               ((SSFNode) words.get(j)).setLexData(lexdata);
////            }
//
//            for (int j = 0; j < ccount; j++)
//            {
//               String lexdata = ((SSFNode) words.get(j)).getLexData();
//
//                Vector wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);
//
//                int wcount = wordFeatures.size();
//                // to Print the word window
//                 ps.print(((SSFNode) words.get(j)).getLexData() + " ");
////                for (int k = 0; k < wcount; k++)
////                {
////                        String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
////                            ps.print(temp);
////                }
//
//                       Vector charFeatures = new Vector();
//
//                       for (int m = 1; m <= prefixNum; m++) {
//                           if(m > lexdata.length())
//                               charFeatures.add("__nil__");
//                           else
//                               charFeatures.add(lexdata.substring(0, m));
//                       }
//
//                       for (int n = 1; n <= suffixNum; n++) {
//                           if(n > lexdata.length())
//                               charFeatures.add("__nil__");
//                           else
//                               charFeatures.add(lexdata.substring(lexdata.length() - n, lexdata.length()));
//                       }
//
//                       for (int n = 0; n < charFeatures.size() ; n++)
//                                 ps.print(((String) charFeatures.get(n)) + " ");
//
//                //this is to  print the chunk attributes of the given window of the word       
////                Vector chunkFeatures = getChunkAttributeWindow(chunkVector, j , 3 , WINDOW_DIRECTION_BOTH);
////
////                for (int k = 0; k < chunkFeatures.size(); k++) {
////                     ps.print( "'" + (String) chunkFeatures.get(k)+ "'"+ ",");
////                }
////
//                ps.print((String) chunkVector.get(j) + " ");
//
//
//               Vector parentChunks = getParentChunkAttribute((SSFNode)words.get(j));
//               int pcount = parentChunks.size();
//               for (int k = 0; k < pcount; k++) {
//                     ps.print((String) parentChunks.get(k)+ " ");
//                }
//
//
//
//                 ps.print((String) chunkVector.get(j)+ "-");
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
//                            ps.println(wordFeature);
//                        }
//                        else
//                            ps.println("NOT");
//
//                        if(wordFeature != null && (isContainingNE(wordFeature) == true))
//                            flagSentence = 1;
//                    }
//                    else
//                    {
//                        ps.println("NOT");
//                    }
//
//                }
//                else
//                {
//                    ps.println("NOT");
//                }
//            }
//            
//            ps.println();
//
//            countNE++;
//        }
//        return countNE;
//    }
//
//     public int printFeatureToMaxent(SSFStory story, PrintStream ps) {
//        int countNE = 0;
//        int scount = story.countSentences();
//        String subst = "__nil__";
//
//        int prefixNum = 3;
//        int suffixNum = 3;
//
//        //this section prints the header information in the arff file 
//
//        for (int i = 0; i < scount; i++) {
//            int flagSentence = 0;
//            SSFSentence sen = story.getSentence(i);
//
//            Vector chunkVector = getChunkInformation(sen);//this is the chunk information of the word of a sentence
//
//            Vector words = ((SSFPhrase) sen.getRoot()).getAllLeaves();
//
//            int ccount = words.size();
//
////            for (int j = 0; j < ccount; j++){
////               String lexdata = ((SSFNode) words.get(j)).getLexData();
////               lexdata = lexdata.replaceAll("'", "?");
////               ((SSFNode) words.get(j)).setLexData(lexdata);
////            }
//
//            for (int j = 0; j < ccount; j++)
//            {
//               String lexdata = ((SSFNode) words.get(j)).getLexData();
//
//                Vector wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);
//
//                int wcount = wordFeatures.size();
//                // to Print the word window
//                 ps.print(((SSFNode) words.get(j)).getLexData() + " ");
//                for (int k = 0; k < wcount; k++)
//                {
//                        String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
//                        if(temp.equalsIgnoreCase("?"))
//                            temp = "__nil__";
//                            ps.print(temp + " ");
//                }
//
//                       Vector charFeatures = new Vector();
//
//                       for (int m = 1; m <= prefixNum; m++) {
//                           if(m > lexdata.length())
//                               charFeatures.add("__nil__");
//                           else
//                               charFeatures.add(lexdata.substring(0, m));
//                       }
//
//                       for (int n = 1; n <= suffixNum; n++) {
//                           if(n > lexdata.length())
//                               charFeatures.add("__nil__");
//                           else
//                               charFeatures.add(lexdata.substring(lexdata.length() - n, lexdata.length()));
//                       }
//
//                       for (int n = 0; n < charFeatures.size() ; n++)
//                       {
//                            ps.print(charFeatures.get(n) + " ");
//                       }
//
//                //this is to  print the chunk attributes of the given window of the word       
//                Vector chunkFeatures = getChunkAttributeWindow(chunkVector, j , 3 , WINDOW_DIRECTION_BOTH);
//
//                for (int k = 0; k < chunkFeatures.size(); k++) {
//                    String tmp = new String();
//                    tmp = (String) chunkFeatures.get(k);
//                    if(tmp.equalsIgnoreCase("?"))
//                        tmp="__nil__";
//                     ps.print(tmp + " ");
//                }
//
//                ps.print((String) chunkVector.get(j) + " ");
//
//
//               Vector parentChunks = getParentChunkAttribute((SSFNode)words.get(j));
//               int pcount = parentChunks.size();
//               for (int k = 0; k < pcount; k++) {
//                     ps.print((String) parentChunks.get(k)+ " ");
//                }
//
//
//                //System.out.println(ps.toString());
//                //System.exit(0);
////                ps.print((String) chunkVector.get(j)+ "-");
////
////                SSFNode parent = (SSFNode) ((SSFNode) words.get(j)).getParent();
////
////                FeatureStructures fs = parent.getFeatureStructures();
////                if(fs != null && fs.countAltFSValues() > 0)
////                {
////                    FeatureStructure fs1 = fs.getAltFSValue(0);
////                    FeatureAttribute fa =  fs1.getAttribute("ne");
////                    if(fa != null && fa.countAltValues() > 0)
////                    {
////                        String wordFeature = (String) fa.getAltValue(0).getValue();
////
////                        if(wordFeature != null && (isValidSymbol(wordFeature) == true) ){
////                            ps.println(wordFeature);
////                        }
////                        else
////                            ps.println("NOT");
////
////                        if(wordFeature != null && (isContainingNE(wordFeature) == true))
////                            flagSentence = 1;
////                    }
////                    else
////                    {
////                        ps.println("NOT");
////                    }
////
////                }
////                else
////                {
////                    ps.println("NOT");
////                }
////               
//            ps.println("?");
//            }
//
//            countNE++;
//        }
//        return countNE;
//    }
//
//      public void printFeatureToArffTest(SSFStory story, PrintStream ps) {
//        int scount = story.countSentences();
//        String subst =  "?";
//        
//        int prefixNum = 3;
//        int suffixNum = 3;
//        
//        
//       
//        //this section prints the header information in the arff file 
//        
//           
//           String str = "@RELATION namedentity";
//        ps.println(str);
//        for (int i = 0; i < 6; i++) {
//            str = "@ATTRIBUTE   wordfeature"+i+"    string";
//            ps.println(str);
//            
//        }
//        ps.println("@ATTRIBUTE  word    string");
//        
//        
//        /*
//        for (int i = 0; i < 6; i++) {
//            str = "@ATTRIBUTE   tagfeature"+i+"    string";
//            ps.println(str);
//            
//        }*/
//        
//        for (int i = 0; i < (prefixNum + suffixNum); i++) {
//            str = "@ATTRIBUTE   charwindowFeature"+i+"    string";
//            ps.println(str);
//            
//        }
//        
//        
//        for (int i = 0; i < 6; i++) {
//            str = "@ATTRIBUTE   chunkfeature"+i+"    string";
//            ps.println(str);
//            
//        }
//        
//         ps.println("@ATTRIBUTE  wordsChunk    string");
//         
//         ps.println("@ATTRIBUTE  parentChunk    string");
//         
//        ps.print("@ATTRIBUTE class"+"   {B-NEP,I-NEP,O-NEP,B-NETE,I-NETE,O-NETE,B-NETO,I-NETO,O-NETO,B-NETI,I-NETI,O-NETI,B-NED,I-NED,O-NED,B-NEL,I-NEL,O-NEL,B-NEN,I-NEN,O-NEN,B-NET,I-NET,O-NET,B-NEO,I-NEO,O-NEO,B-NEM,I-NEM,O-NEM,B-NEA,I-NEA,O-NEA,B-NETP,I-NETP,O-NETP,B-NOT,I-NOT,O-NOT}");
//        ps.println();
//        ps.println("@DATA");
//        PrintStream psSentenceCount = null;
//        try {
//            psSentenceCount = new PrintStream("/home1/sanchay/automatic-annotation/test-data--count-of-sentences");
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
//        //This marks the end of my code - Ashish
//        int sentenceCount=1;
//        for (int i = 0; i < scount; i++) {
//            SSFSentence sen = story.getSentence(i);
//                
//            //This is My Code - Ashish
//            //System.out.println(sentenceCount + "\n");
//            String strRawSentence = sen.convertToRawText();
//            psSentenceCount.println(sentenceCount);
//            
//            StringTokenizer tmpTkn = new StringTokenizer(strRawSentence, " ");
//            sentenceCount+=tmpTkn.countTokens();
//            
//            //This marks the end of my code - Ashish
//            
//            Vector chunkVector = getChunkInformation(sen);//this is the chunk information of the word of a sentence
//            
//            Vector words = ((SSFPhrase) sen.getRoot()).getAllLeaves();
//            
//            int ccount = words.size();
//
//            for (int j = 0; j < ccount; j++)
//            {
//               String lexdata = ((SSFNode) words.get(j)).getLexData();
//               lexdata = lexdata.replaceAll("'", "?");
//               ((SSFNode) words.get(j)).setLexData(lexdata);
//            }
//            
//            for (int j = 0; j < ccount; j++)
//            {
//               String lexdata = ((SSFNode) words.get(j)).getLexData();
//                
//                Vector wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);
//               
//                int wcount = wordFeatures.size();
//                // to Print the word window
//                for (int k = 0; k < wcount; k++)
//                {
//                        String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
////                        if(temp == "'" || temp == "," || temp == "`")
////                            ps.print( "'" + subst + "'"+ ",");
////                        else    
//                            ps.print( "'" + temp + "'"+ ",");
//                }
//                
//                //this line prints the particular word
//                       ps.print( "'" + ((SSFNode) words.get(j)).getLexData() + "'"+ ","); 
//                       
//
///*
//                //to print the tag window
//                for (int k = 0; k < wcount; k++){
//                                ps.print( "'" + ((SSFNode) wordFeatures.get(k)).getName() + "'"+ ",");
//                }
//                //this section to print the character window of the word
// */
//                       
////                       for (int k = 0; k < (((SSFNode) words.get(j)).getLexData()).length(); k++) {
////                           
////                          charFeatures = getCharWindow(((SSFNode)words.get(j)), k, 3, WINDOW_DIRECTION_BOTH);
////                       }
//                       
//                       //this is to print the character window of the sentence 
//                       
//                       Vector charFeatures = new Vector();
//                       
//                       for (int m = 1; m <= prefixNum; m++) {
//                           if(m > lexdata.length())
//                               charFeatures.add("?");
//                           else
//                               charFeatures.add(lexdata.substring(0, m));                           
//                       }
//                       
//                       for (int n = 1; n <= suffixNum; n++) {
//                           if(n > lexdata.length())
//                               charFeatures.add("?");
//                           else
//                               charFeatures.add(lexdata.substring(lexdata.length() - n, lexdata.length()));                           
//                       }
//                       
//                       for (int n = 0; n < charFeatures.size() ; n++) {
//                           
//                                 ps.print( "'" + ((String) charFeatures.get(n)) + "'"+ ",");
//
//                           
//                       }
//                       
//                //this is to  print the chunk attributes of the given window of the word       
//                Vector chunkFeatures = getChunkAttributeWindow(chunkVector, j , 3 , WINDOW_DIRECTION_BOTH);
//                
//                for (int k = 0; k < chunkFeatures.size(); k++) {
//                     ps.print( "'" + (String) chunkFeatures.get(k)+ "'"+ ",");
//                }
//                
//                ps.print( "'" + (String) chunkVector.get(j)+ "'"+ ",");
//                
//                
//               Vector parentChunks = getParentChunkAttribute((SSFNode)words.get(j));
//               int pcount = parentChunks.size();
//               for (int k = 0; k < pcount; k++) {
//                     ps.print( "'" + (String) parentChunks.get(k)+ "'"+ ",");
//                }
//                
//                
//                
//                 ps.print(  (String) chunkVector.get(j)+ "-");
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
//                            ps.println(wordFeature);
//                        }
//                        else
//                            ps.println("NOT");
//                    }
//                    else
//                    {
//                        ps.println("NOT");
//                    }
//
//                }
//                else
//                {
//                    ps.println("NOT");
//                }
//            }
//        }
//    }
//    
//    public void printFeature(SSFStory story) {
//        
//        int scount = story.countSentences();
//        //String s = new String();
//        
//        for (int i = 0; i < scount; i++) {
//            SSFSentence sen = story.getSentence(i);
//            
//            System.out.println("Features for sentence-" + (i+1) + ":\n");                    
//            
//            int ccount = sen.getRoot().countChildren();
//            
//            for (int j = 0; j < ccount; j++)
//            {
//                System.out.println("\tFeatures for word-" + (j+1) + ":\n");                    
//    
//                Vector wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH , WORD_FLAG);
//                
//                
//                
//                for (int k = 0; k < wordFeatures.size(); k++)
//                {
//                    System.out.println("\t\t" +((SSFNode) wordFeatures.get(k)).getName());
//                   /* s = ((SSFNode) wordFeatures.get(k)).getName() + " , ";
//                    ps.println(s);*/
//                    
//                }    
//                
//            }
//            /*
//             *this code part returns the word feature of the window   
//             *
//             *
//             */
//            
//            
//            for (int j = 0; j < ccount; j++)
//            {
//                System.out.println("\t Atribute Features for word-" + (j+1) + ":\n");                    
//    
//                Vector attribFeatures = getWordFeatureWindow(sen, j, 3, WINDOW_DIRECTION_BOTH , "ne");
//                
//                for (int k = 0; k < attribFeatures.size(); k++)
//                {
//                    System.out.println("\t\t" +( attribFeatures.get(k)));                    
//                }                
//            }
//            
//            
//          //this part returns the chunk wndow of the word.
//            
//        /*  for (int j = 0; j < ccount; j++)
//            {
//                System.out.println("\t chunk Window Features for word-" + (j+1) + ":\n");                    
//    
//                Vector chunkFeatures = getChunkWindow(sen, j, 3, WINDOW_DIRECTION_RIGHT);
//                
//                for (int k = 0; k < chunkFeatures.size(); k++)
//                {
//                    System.out.println("\t\t" +( (SSFNode) chunkFeatures.get(k)).makeRawSentence());                    
//                }                
//            }
//
//         */   
//            
//            
//            /*
//             *This code returns the tag of  chunk window in a vector
//             *
//             */
//           /* 
//                   for (int j = 0; j < ccount; j++)
//            {
//                System.out.println("\t chunk Window Features for word-" + (j+1) + ":\n");                    
//    
//                Vector chunkFeatures = getChunkWindow(sen, j, 3, WINDOW_DIRECTION_RIGHT);
//                
//                for (int k = 0; k < chunkFeatures.size(); k++)
//                {
//                    System.out.println("\t\t" +( (SSFPhrase) chunkFeatures.get(k)).getName());                    
//                }                
//            }*/
//            
//            /*
//             *
//             *this code returns the attribute feature for chunk
//             */
//            
//           /*
//              
//            for (int j = 0; j < ccount; j++)
//            {
//                System.out.println("\t Atribute Features for chunk-" + (j+1) + ":\n");                    
//    
//                Vector chunkattribFeatures = getChunkFeatureWindow(sen, j, 3, WINDOW_DIRECTION_BOTH , "ne");
//                
//                for (int k = 0; k < chunkattribFeatures.size(); k++)
//                {
//                    System.out.println("\t\t" +( chunkattribFeatures.get(k)));                    
//                }                
//            }
//      
//            */
//            
//            
//        // this code part return the character features by iterating on the each word of the sentence and then 
//            // on each character of the word
//                Vector childVector = sen.getRoot().getAllLeaves();
//                
//                for (int m = 0; m < childVector.size(); m++) {
//            
//             System.out.println("charFeatures for word-" + (i+1) + ":\n");
//         
//             for (int j = 0; j < ((String) ((SSFNode)childVector.get(m)).getLexData()).length() ; j++)
//            {
//                System.out.println("\t charFeatures for char-" + (j+1) + "in the word - " + (i+1) + ":\n");                    
//    
//                Vector charFeatures = getCharWindow((SSFNode)childVector.get(m), j, 3, WINDOW_DIRECTION_BOTH);
//                
//                for (int k = 0; k < charFeatures.size(); k++)
//                {
//                    System.out.println("\t\t" + charFeatures.get(k));                    
//                }                
//            }
// 
//         }
//               
//        }
//    }
//    
//    
//    @SuppressWarnings("empty-statement")
//    void newPrint(SSFStory story, PrintStream ps)
//    {
//        
//        
//         
//        int scount = story.countSentences();;
//        
//        
//           for (int i = 0; i < scount; i++) {
//            SSFSentence sen = story.getSentence(i);
//            Vector chunkVector = getChunkInformation(sen);
//            Vector words = ((SSFPhrase) sen.getRoot()).getAllLeaves();
//            int ccount = words.size();
//            
//            for (int j = 0; j < ccount; j++)
//            {
//                Vector wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);
//               
//                int wcount = wordFeatures.size();
//                // to Print the word window
//                for (int k = 0; k < wcount; k++)
//                {
//                
//                       ps.print( "'" + ((SSFNode) wordFeatures.get(k)).getLexData() + "'"+ ",");
//                }
//                
//
//                //this line prints the particular word
//                       ps.print( "'" + ((SSFNode) words.get(j)).getLexData() + "'"+ ",");
//                       
//               // this code is to print the chunk information of the window 
//               
//                Vector chunkFeatures = getChunkAttributeWindow(chunkVector, j , 3 , WINDOW_DIRECTION_BOTH);
//                for (int k = 0; k < chunkFeatures.size(); k++) {
//                     ps.print( "'" + (String) chunkFeatures.get(k)+ "'"+ ",");
//                }
//                
//                ps.print( "'" + (String) chunkVector.get(j)+ "'"+ ",");
//                
//                // this code is to print the parent's chunk information
//                Vector parentChunks = getParentChunkAttribute((SSFNode)words.get(j));
//                 int pcount = parentChunks.size();
//                    for (int k = 0; k < pcount; k++) {
//                     ps.print( "'" + (String)parentChunks.get(k)+ "'"+ ",");
//                }
//                
//                
//                
//                    ps.print((String) chunkVector.get(j)+ "-");
//                 
//                 SSFNode parent1 =  (SSFNode) ((SSFNode) words.get(j)).getParent();                            
//                FeatureStructures fs = parent1.getFeatureStructures();
//                if(fs != null && fs.countAltFSValues() > 0 )
//                {
//                    FeatureStructure fs1 = fs.getAltFSValue(0);
//                    FeatureAttribute fa =  fs1.getAttribute("ne");
//                    if(fa != null && fa.countAltValues() > 0 )
//                    {
//                        String wordFeature = (String) fa.getAltValue(0).getValue();
//
//                        if(wordFeature != null){
//                            ps.print(wordFeature);
//                            ps.println();
//                        }
//                        else
//                            ps.println("NOT");
//                    }
//                    else
//                    {
//                        ps.println("NOT");
//                    }
//                
//                }
//                else
//                {
//                    ps.println("NOT");
//                }
//            
//            }
//          }
//        }
//    
//       public void printFeatureToArffBatch(String inFilePath, String outFilePath) throws FileNotFoundException, IOException {
//        File inFile = new File(inFilePath);
//        File outFile = new File(outFilePath);
//        
//        if(inFile.isFile() == true)
//        {
//            System.out.println("Converting file " + inFile.getAbsolutePath());
//            
//            FSProperties fsp = new FSProperties();
//            SSFProperties ssfp = new SSFProperties();
//           // SSFStory story = new SSFStoryImpl();
//            SSFStory trainStory = new SSFStoryImpl();
//
//            try {
//                fsp.read("props/fs-mandatory-attribs.txt", "props/fs-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;
//                ssfp.read("props/ssf-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;
//
//                FeatureStructuresImpl.setFSProperties(fsp);
//                SSFNode.setSSFProperties(ssfp);
//
//                trainStory.readFile(inFilePath, "UTF-8"); //throws java.io.FileNotFoundException;
//
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            
//            if((new File(outFilePath)).isDirectory())
//            {
//                File odir = new File(outFilePath);
//
//                if(odir.exists() == false)
//                {
//                    odir.mkdir();
//                }
//
//                odir = new File(odir, inFile.getParentFile().getName() + "-" + inFile.getName());
//
//                PrintStream outPS = new PrintStream(odir.getAbsolutePath(), "UTF-8");                
//                
//                printFeatureToArff(trainStory, outPS);
//                printFeatureToYamcha(trainStory, outPS);
//            }
//            else
//            {
//                System.out.println("Converting file " + inFile.getAbsolutePath());
//
//                PrintStream outPS = new PrintStream(outFilePath, "UTF-8");                
//                printFeatureToArff(trainStory, outPS);
//            }
//        }
//        else
//        {
//            if(inFile.isDirectory() == true)
//            {
//                File files[] = inFile.listFiles();
//
//                for(int i = 0; i < files.length; i++)
//                {
//                    printFeatureToArffBatch(files[i].getAbsolutePath(), outFilePath);
//                }
//            }
//        }
//    }
//
//       public static void main(String[] args) throws Exception {
//        
//        WindowFeaturesImpl win = new WindowFeaturesImpl();
//        FSProperties fsp = new FSProperties();
//        SSFProperties ssfp = new SSFProperties();
//        SSFStory testStory = new SSFStoryImpl();
//        SSFStory trainStory = new SSFStoryImpl();
//        
//        
//        try {
//            //PrintStream ps = new PrintStream("/home1/sanchay/automatic-annotation/training-hindi.arff", "UTF-8");
//            PrintStream psYamcha = new PrintStream("/home1/sanchay/automatic-annotation/testing-hindi-yamcha", "UTF-8");        
//            PrintStream psMaxent = new PrintStream("/home1/sanchay/automatic-annotation/maxent/train-on-whole-data/training-hindi-maxent.test", "UTF-8");
//            PrintStream ps1 = new PrintStream("/home1/sanchay/automatic-annotation/testing-hindi.arff", "UTF-8");
//            testStory.readFile("/home1/sanchay/automatic-annotation/test-data-hindi.txt");
//            win.printFeatureToYamchaDir("/home1/sanchay/automatic-annotation/testing-hindi", psYamcha);
//            win.printFeatureToArffTest(testStory, ps1);
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace();
//        }
////        SSFStory testStory = new SSFStoryImpl();
//        
////        t
////            fsp.read("props\\fs-mandatory-attribs.txt", "pro`ps\\fs-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;
////            ssfp.read("props\\ssf-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;
////            
////            FeatureStructuresImpl.setFSProperties(fsp);
////            SSFNode.setSSFProperties(ssfp);
////            
////            trainStory.readFile("res\\113.priti.utf8"); //throws java.io.FileNotFoundException;
////            testStory.readFile("res\\test-data-hindi.txt");
//////            story.print(System.out);
////            
////          // win.printFeature(story);
////           PrintStream ps = new PrintStream("C:\\Users\\User\\Desktop\\training-hindi.arff", "UTF-8");
////           PrintStream ps1 = new PrintStream("C:\\Users\\User\\Desktop\\testing-hindi.arff", "UTF-8"); 
////           win.printFeatureToArff(trainStory, ps);
////           win.printFeatureToArff(testStory, ps1);
////          //  win.newPrint(trainStory,System.out);
////            
////        } catch (FileNotFoundException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        } catch (IOException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        } catch (Exception e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
//    }
}
