/*
 * SSFToArffImpl.java
 *
 * Created on September 5, 2008, 12:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package sanchay.formats.converters;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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
import sanchay.mlearning.feature.extraction.impl.WindowFeaturesImpl;

/**
 *
 * @author sanchay
 */
public class SSFToArffImpl extends WindowFeaturesImpl {

    static int printArffHeaders;

    /**
     * Creates a new instance of SSFToArffImpl
     */
    public SSFToArffImpl(String inputDir, PrintStream psArff, boolean whetherTest) {
        printFeatureToSSFDir(inputDir, psArff, whetherTest);
    }

    @Override
    public void printFeatureTrain(SSFStory story, PrintStream ps) {
        int scount = story.countSentences();
        String subst = "?";

        int prefixNum = 3;
        int suffixNum = 3;



        //this section prints the header information in the arff file 


        if (printArffHeaders == 0) {
            String str = "@RELATION namedentity";
            ps.println(str);
            for (int i = 0; i < 6; i++) {
                str = "@ATTRIBUTE   wordfeature" + i + "    string";
                ps.println(str);

            }
            ps.println("@ATTRIBUTE  word    string");


            /*
             for (int i = 0; i < 6; i++) {
             str = "@ATTRIBUTE   tagfeature"+i+"    string";
             ps.println(str);

             }*/

            for (int i = 0; i < (prefixNum + suffixNum); i++) {
                str = "@ATTRIBUTE   charwindowFeature" + i + "    string";
                ps.println(str);

            }


            for (int i = 0; i < 6; i++) {
                str = "@ATTRIBUTE   chunkfeature" + i + "    string";
                ps.println(str);

            }

            ps.println("@ATTRIBUTE  wordsChunk    string");

            ps.println("@ATTRIBUTE  parentChunk    string");

            ps.print("@ATTRIBUTE class" + "   {B-NEP,I-NEP,O-NEP,B-NETE,I-NETE,O-NETE,B-NETO,I-NETO,O-NETO,B-NETI,I-NETI,O-NETI,B-NED,I-NED,O-NED,B-NEL,I-NEL,O-NEL,B-NEN,I-NEN,O-NEN,B-NET,I-NET,O-NET,B-NEO,I-NEO,O-NEO,B-NEM,I-NEM,O-NEM,B-NEA,I-NEA,O-NEA,B-NETP,I-NETP,O-NETP,B-NOT,I-NOT,O-NOT}");
            ps.println();
            ps.println("@DATA");
            printArffHeaders = 1;
        }

        //This marks the end of my code - Ashish
        int sentenceCount = 1;
        for (int i = 0; i < scount; i++) {
            SSFSentence sen = story.getSentence(i);
            List chunkVector = getChunkInformation(sen);//this is the chunk information of the word of a sentence

            List words = ((SSFPhrase) sen.getRoot()).getAllLeaves();

            int ccount = words.size();

            for (int j = 0; j < ccount; j++) {
                String lexdata = ((SSFNode) words.get(j)).getLexData();
                lexdata = lexdata.replaceAll("'", "?");
                ((SSFNode) words.get(j)).setLexData(lexdata);
            }

            for (int j = 0; j < ccount; j++) {

                String lexdata = ((SSFNode) words.get(j)).getLexData();

                List wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);

                int wcount = wordFeatures.size();
                // to Print the word window
                for (int k = 0; k < wcount; k++) {
                    String temp = ((SSFNode) wordFeatures.get(k)).getLexData();
                    ps.print("'" + temp + "'" + ",");
                }

                //this line prints the particular word
                ps.print("'" + ((SSFNode) words.get(j)).getLexData() + "'" + ",");



                //this is to print the character window of the sentence 

                List charFeatures = new ArrayList();

                for (int m = 1; m <= prefixNum; m++) {
                    if (m > lexdata.length()) {
                        charFeatures.add("?");
                    } else {
                        charFeatures.add(lexdata.substring(0, m));
                    }
                }

                for (int n = 1; n <= suffixNum; n++) {
                    if (n > lexdata.length()) {
                        charFeatures.add("?");
                    } else {
                        charFeatures.add(lexdata.substring(lexdata.length() - n, lexdata.length()));
                    }
                }

                for (int n = 0; n < charFeatures.size(); n++) {

                    ps.print("'" + ((String) charFeatures.get(n)) + "'" + ",");


                }
                ps.print((String) chunkVector.get(j) + "-");

                SSFNode parent = (SSFNode) ((SSFNode) words.get(j)).getParent();

                FeatureStructures fs = parent.getFeatureStructures();
                if (fs != null && fs.countAltFSValues() > 0) {
                    FeatureStructure fs1 = fs.getAltFSValue(0);
                    FeatureAttribute fa = fs1.getAttribute("ne");
                    if (fa != null && fa.countAltValues() > 0) {
                        String wordFeature = (String) fa.getAltValue(0).getValue();

                        if (wordFeature != null && (isValidSymbol(wordFeature) == true)) {
                            ps.println(wordFeature);
                        } else {
                            ps.println("NOT");
                        }
                    } else {
                        ps.println("NOT");
                    }

                } else {
                    ps.println("NOT");
                }
            }
        }
    }

    @Override
    public void printFeatureTest(SSFStory story, PrintStream ps) {
        int scount = story.countSentences();
        String subst = "?";

        int prefixNum = 3;
        int suffixNum = 3;



        //this section prints the header information in the arff file 


        if (printArffHeaders == 0) {
            String str = "@RELATION namedentity";
            ps.println(str);
            for (int i = 0; i < 6; i++) {
                str = "@ATTRIBUTE   wordfeature" + i + "    string";
                ps.println(str);

            }
            ps.println("@ATTRIBUTE  word    string");

            for (int i = 0; i < (prefixNum + suffixNum); i++) {
                str = "@ATTRIBUTE   charwindowFeature" + i + "    string";
                ps.println(str);

            }


            for (int i = 0; i < 6; i++) {
                str = "@ATTRIBUTE   chunkfeature" + i + "    string";
                ps.println(str);

            }

            ps.println("@ATTRIBUTE  wordsChunk    string");

            ps.println("@ATTRIBUTE  parentChunk    string");

            ps.print("@ATTRIBUTE class" + "   {B-NEP,I-NEP,O-NEP,B-NETE,I-NETE,O-NETE,B-NETO,I-NETO,O-NETO,B-NETI,I-NETI,O-NETI,B-NED,I-NED,O-NED,B-NEL,I-NEL,O-NEL,B-NEN,I-NEN,O-NEN,B-NET,I-NET,O-NET,B-NEO,I-NEO,O-NEO,B-NEM,I-NEM,O-NEM,B-NEA,I-NEA,O-NEA,B-NETP,I-NETP,O-NETP,B-NOT,I-NOT,O-NOT}");
            ps.println();
            ps.println("@DATA");
            printArffHeaders = 1;
        }

        PrintStream psSentenceCount = null;

        try {
            psSentenceCount = new PrintStream("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/Blank");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        //This marks the end of my code - Ashish
        int sentenceCount = 1;
        for (int i = 0; i < scount; i++) {
            SSFSentence sen = story.getSentence(i);

            //This is My Code - Ashish
            //System.out.println(sentenceCount + "\n");
            String strRawSentence = sen.convertToRawText();
            psSentenceCount.println(sentenceCount);

            StringTokenizer tmpTkn = new StringTokenizer(strRawSentence, " ");
            sentenceCount += tmpTkn.countTokens();

            //This marks the end of my code - Ashish

            List chunkVector = getChunkInformation(sen);//this is the chunk information of the word of a sentence

            List words = ((SSFPhrase) sen.getRoot()).getAllLeaves();

            int ccount = words.size();

            for (int j = 0; j < ccount; j++) {
                String lexdata = ((SSFNode) words.get(j)).getLexData();
                lexdata = lexdata.replaceAll("'", "?");
                ((SSFNode) words.get(j)).setLexData(lexdata);
            }

            for (int j = 0; j < ccount; j++) {
                String lexdata = ((SSFNode) words.get(j)).getLexData();

                List wordFeatures = getWordWindow(sen, j, 3, WINDOW_DIRECTION_BOTH, 1);

                int wcount = wordFeatures.size();
                // to Print the word window
                for (int k = 0; k < wcount; k++) {
                    String temp = ((SSFNode) wordFeatures.get(k)).getLexData();

                    ps.print("'" + temp + "'" + ",");
                }

                //this line prints the particular word
                ps.print("'" + ((SSFNode) words.get(j)).getLexData() + "'" + ",");




                //this is to print the character window of the sentence 

                List charFeatures = new ArrayList();

                for (int m = 1; m <= prefixNum; m++) {
                    if (m > lexdata.length()) {
                        charFeatures.add("?");
                    } else {
                        charFeatures.add(lexdata.substring(0, m));
                    }
                }

                for (int n = 1; n <= suffixNum; n++) {
                    if (n > lexdata.length()) {
                        charFeatures.add("?");
                    } else {
                        charFeatures.add(lexdata.substring(lexdata.length() - n, lexdata.length()));
                    }
                }

                for (int n = 0; n < charFeatures.size(); n++) {

                    ps.print("'" + ((String) charFeatures.get(n)) + "'" + ",");


                }

                ps.print((String) chunkVector.get(j) + "-");

                SSFNode parent = (SSFNode) ((SSFNode) words.get(j)).getParent();

                FeatureStructures fs = parent.getFeatureStructures();
                if (fs != null && fs.countAltFSValues() > 0) {
                    FeatureStructure fs1 = fs.getAltFSValue(0);
                    FeatureAttribute fa = fs1.getAttribute("ne");
                    if (fa != null && fa.countAltValues() > 0) {
                        String wordFeature = (String) fa.getAltValue(0).getValue();

                        if (wordFeature != null && (isValidSymbol(wordFeature) == true)) {
                            ps.println(wordFeature);
                        } else {
                            ps.println("NOT");
                        }
                    } else {
                        ps.println("NOT");
                    }

                } else {
                    ps.println("NOT");
                }
            }
        }
    }

    public static void main(String args[]) {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();
        SSFStory testStory = new SSFStoryImpl();
        SSFStory trainStory = new SSFStoryImpl();
        PrintStream psArff;
        SSFToArffImpl obj;
        boolean whetherTest = false;


        String whetherTestString = JOptionPane.showInputDialog("Enter 0 for Train 1 for Test, Then enter any file in the input directory and then enter output file");

        if (whetherTestString.equalsIgnoreCase("1")) {
            whetherTest = true;
        }



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
            System.out.println(whetherTest);
            psArff = new PrintStream(outputFile, GlobalProperties.getIntlString("UTF-8"));
            obj = new SSFToArffImpl(inputDir, psArff, whetherTest);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
