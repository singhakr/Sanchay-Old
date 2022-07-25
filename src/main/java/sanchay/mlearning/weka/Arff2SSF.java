/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.weka;

import java.io.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.corpus.ssf.impl.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.mlearning.crf.DefaultDataSequence;
import sanchay.mlearning.weka.DefaultArffData.DefaultArffDocument;

/**
 *
 * @author Sourabh
 */
public class Arff2SSF extends DefaultMLCorpusConverter implements MLCorpusConverter {

    protected String outputPrefix = ".out.txt";
    protected ArffExtractFeatures arffExtractFeatures;

    public Arff2SSF() {
        super();
    }

    public void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature) {
        super.init(ssfPath, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultArffData();


        try {

            ((DefaultArffData) mlCorpus).readArffTagged(mlPath, charset);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus = new SSFCorpusImpl(charset);
    }

    public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature, int format) {
        super.init(ssfFiles, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultArffData();

        try {
            ((DefaultArffData) mlCorpus).readArffTagged(mlPath, charset);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus = new SSFCorpusImpl(charset);
    }

    protected void convertToTagFormat(boolean featureType) {

        int scount = ((DefaultArffData) mlCorpus).countDocuments();

        DefaultArffDocument doc = null;

        SSFStoryImpl ssfTestStory = new SSFStoryImpl();
        try {
            ssfTestStory.readFile(ssfPath);
        } catch (Exception ex) {
            Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
        }

        int sentenceNumber = 0;


        SSFStory ssfStory = new SSFStoryImpl();

        ssfStory.setId("1");

        SSFNode ssfNode = null;
        SSFPhrase ssfPhrase = null;


        int sentenceCountWordNumer = 0;


        int sentenceId = 1;

        for (int i = 0; (i < scount) || scount == 0; i++) {
            int docStartIndex = 0;
            int docEndIndex = 0;

            if (scount == 0) {
                docEndIndex = ((DefaultArffData) mlCorpus).countDataSequences() - 1;
            } else {
                doc = ((DefaultArffData) mlCorpus).getDocument(i);

                docStartIndex = doc.getSequenceStart();
                docEndIndex = doc.getSequenceEnd();
            }

            int k = 0;
            DefaultDataSequence dataSequence = new DefaultDataSequence();
            SSFPhrase root = null;
            SSFSentence ssfSentence = null;
            SSFSentenceImpl ssfTestSentence = new SSFSentenceImpl();
            SSFPhrase ssfTestPhrase = new SSFPhrase();

            ssfTestSentence = (SSFSentenceImpl) ssfTestStory.getSentence(sentenceNumber);
            sentenceNumber++;
            ssfTestPhrase = ssfTestSentence.getRoot();

            sentenceCountWordNumer += ssfTestPhrase.getAllLeaves().size();

            ssfSentence = new SSFSentenceImpl();

            for (int j = docStartIndex; j <= docEndIndex; j++) {
                dataSequence = (DefaultDataSequence) ((DefaultArffData) mlCorpus).getDataSequence(j);

                Vector list = (Vector) dataSequence.x(0);

                if (sentenceCountWordNumer != 0 && sentenceCountWordNumer == k) {//.equalsIgnoreCase(k + "")) {

                    try {
                        ssfSentence = new SSFSentenceImpl();
                        root = new SSFPhrase("0", "((", "SSF", "");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    ssfSentence.setRoot(root);

                    ssfTestSentence = (SSFSentenceImpl) ssfTestStory.getSentence(sentenceNumber);
                    sentenceNumber++;
                    ssfTestPhrase = ssfTestSentence.getRoot();

                    sentenceCountWordNumer += ssfTestPhrase.getAllLeaves().size();
                    if (ssfNode != null) {
                        root.addChild(ssfPhrase);
                    }
                    if (k != 1) {
                        ssfSentence.setId(sentenceId + "");
                        sentenceId++;
                        ssfStory.addSentence(ssfSentence);
                    }

                    try {
                        ssfPhrase = new SSFPhrase("0", "((", "", "");

                        FeatureStructures fss = new FeatureStructuresImpl();
                        FeatureStructure fs = new FeatureStructureImpl();
                        FeatureAttribute fa = new FeatureAttributeImpl();
                        FeatureValue fv = new FeatureValueImpl();

                        fa.setName("ne");
                        fv.setValue((String) list.elementAt(2));
                        fa.addAltValue(fv);
                        fs.addAttribute(fa);

                        fss.addAltFSValue(fs);

                        ssfPhrase.setFeatureStructures(fss);

                        ssfNode = new SSFLexItem("0", (String) list.elementAt(0), (String) list.elementAt(1), "");

                        ssfPhrase.addChild(ssfNode);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    if (ssfPhrase == null) {
                        try {
                            ssfPhrase = new SSFPhrase("0", "((", "", "");

                            FeatureStructures fss = new FeatureStructuresImpl();
                            FeatureStructure fs = new FeatureStructureImpl();
                            FeatureAttribute fa = new FeatureAttributeImpl();
                            FeatureValue fv = new FeatureValueImpl();

                            fa.setName("ne");
                            fv.setValue((String) list.elementAt(2));
                            fa.addAltValue(fv);
                            fs.addAttribute(fa);

                            fss.addAltFSValue(fs);

                            ssfPhrase.setFeatureStructures(fss);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    try {
                        ssfNode = new SSFLexItem("0", (String) list.elementAt(0), (String) list.elementAt(1), "");

                        ssfPhrase.addChild(ssfNode);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                k++;

            }


            try {

                ssfSentence = new SSFSentenceImpl();
                root = new SSFPhrase("0", "((", "SSF", "");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ssfSentence.setRoot(root);
            if (ssfNode != null) {
                root.addChild(ssfPhrase);
            }
            ssfSentence.setId(sentenceId + "");
            sentenceId++;
            ssfStory.addSentence(ssfSentence);
            if (scount == 0) {

                try {
                    ssfStory.save(outputPath, charset);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            } else {
                addStory(doc, ssfStory);
            }
        }
    }

    protected void addStory(DefaultArffDocument doc, SSFStory ssfStory) {
        String opath = doc.getOutputPath();

        try {

            File ofile = new File(opath);
            File outputFile = new File(outputPath);

            if (outputFile.exists() && outputFile.isDirectory()) {
                outputFile = new File(outputFile, ofile.getName());

                opath = outputFile.getAbsolutePath();
            }

            ssfStory.save(opath, charset);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus.addStory(outputPath, null);
    }

    protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries) {

        int scount = ((DefaultArffData) mlCorpus).countDocuments();

        DefaultArffDocument doc = null;
        SSFStoryImpl ssfTestStory = new SSFStoryImpl();
        try {
            ssfTestStory.readFile(ssfPath);
        } catch (Exception ex) {
            Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
        }

        int sentenceNumber = 0;
        int sentenceCountWordNumer = 0;
        SSFNode ssfNode = null;
        SSFPhrase ssfPhrase = null;
        for (int i = 0; (i < scount) || scount == 0; i++) {
            int docStartIndex = 0;
            int docEndIndex = 0;

            if (scount == 0) {
                docEndIndex = ((DefaultArffData) mlCorpus).countDataSequences() - 1;
            } else {
                doc = ((DefaultArffData) mlCorpus).getDocument(i);

                docStartIndex = doc.getSequenceStart();
                docEndIndex = doc.getSequenceEnd();
            }

            SSFStory ssfStory = new SSFStoryImpl();

            SSFPhrase root = null;

            ssfStory.setId("1");
            SSFSentence ssfSentence = new SSFSentenceImpl();
            SSFSentenceImpl ssfTestSentence = new SSFSentenceImpl();
            SSFPhrase ssfTestPhrase = new SSFPhrase();
            //  try {
            ssfTestSentence = (SSFSentenceImpl) ssfTestStory.getSentence(sentenceNumber);
            sentenceNumber++;
            ssfTestPhrase = ssfTestSentence.getRoot();
            //  sentenceCountWordNumer = readerSentenceCount.readLine();
            sentenceCountWordNumer += ssfTestPhrase.getAllLeaves().size();
            /*  try {
            sentenceCountWordNumer = readerSentenceCount.readLine();
            } catch (IOException ex) {
            Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            int sentenceId = 1;
            try {
                root = new SSFPhrase("0", "((", "SSF", "");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ssfSentence.setRoot(root);
            int k = 0;
            for (int j = docStartIndex; j <= docEndIndex; j++) {
                DefaultDataSequence dataSequence = (DefaultDataSequence) ((DefaultArffData) mlCorpus).getDataSequence(j);


                if (sentenceCountWordNumer != 0 && sentenceCountWordNumer == k) {
                    if (k != 1) {
                        if (ssfNode != null) {
                            root.addChild(ssfPhrase);
                            ssfNode = null;
                        }
                        ssfSentence.setId(sentenceId + "");
                        sentenceId++;
                        ssfStory.addSentence(ssfSentence);
                    }
                    try {
                        ssfSentence = new SSFSentenceImpl();
                        root = new SSFPhrase("0", "((", "SSF", "");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    ssfSentence.setRoot(root);
                    ssfTestSentence = (SSFSentenceImpl) ssfTestStory.getSentence(sentenceNumber);
                    sentenceNumber++;
                    ssfTestPhrase = ssfTestSentence.getRoot();
                    //  sentenceCountWordNumer = readerSentenceCount.readLine();
                    sentenceCountWordNumer += ssfTestPhrase.getAllLeaves().size();
                /*        try {
                sentenceCountWordNumer = readerSentenceCount.readLine();
                } catch (IOException ex) {
                Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                }
                k++;
                Vector list = (Vector) dataSequence.x(0);
                System.out.println(list.elementAt(1).toString());
                if (list.elementAt(1).toString().equalsIgnoreCase("B")) {

                    if (ssfNode != null) {
                        root.addChild(ssfPhrase);
                    }
                    try {
                        ssfPhrase = new SSFPhrase("0", "((", list.elementAt(2).toString(), "");

                        FeatureStructures fss = new FeatureStructuresImpl();
                        FeatureStructure fs = new FeatureStructureImpl();
                        FeatureAttribute fa = new FeatureAttributeImpl();
                        FeatureValue fv = new FeatureValueImpl();

                        fa.setName("ne");
                        fv.setValue((String) list.elementAt(2));
                        fa.addAltValue(fv);
                        fs.addAttribute(fa);

                        fss.addAltFSValue(fs);

                        ssfPhrase.setFeatureStructures(fss);

                        ssfNode = new SSFLexItem("0", (String) list.elementAt(0), "", "");
                        ssfPhrase.addChild(ssfNode);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                if (list.elementAt(1).toString().equalsIgnoreCase("I")) {
                    if (ssfPhrase == null) {
                        try {
                            ssfPhrase = new SSFPhrase("0", "((", list.elementAt(2).toString(), "");

                            FeatureStructures fss = new FeatureStructuresImpl();
                            FeatureStructure fs = new FeatureStructureImpl();
                            FeatureAttribute fa = new FeatureAttributeImpl();
                            FeatureValue fv = new FeatureValueImpl();

                            fa.setName("ne");
                            fv.setValue((String) list.elementAt(2));
                            fa.addAltValue(fv);
                            fs.addAttribute(fa);

                            fss.addAltFSValue(fs);

                            ssfPhrase.setFeatureStructures(fss);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    try {
                        ssfNode = new SSFLexItem("0", (String) list.elementAt(0), "", "");
                        ssfPhrase.addChild(ssfNode);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                if (list.elementAt(1).toString().equalsIgnoreCase("O")) {
                    if (ssfNode != null) {
                        root.addChild(ssfPhrase);
                    }

                    try {
                        ssfNode = new SSFLexItem("0", (String) list.elementAt(0), "", "");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    root.addChild(ssfNode);
                    ssfNode = null;
                }

            }
            if (ssfNode != null) {
                System.out.println("Phrase added");
                root.addChild(ssfPhrase);
            }
            ssfSentence.setId(sentenceId + "");
            ssfStory.addSentence(ssfSentence);
            if (scount == 0) {

                try {
                    ssfStory.save(outputPath, charset);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Arff2SSF.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            } else {
                addStory(doc, ssfStory);
            }
        }

    }

    public static void main(String argv[]) {
        Arff2SSF arff2SSF2 = new Arff2SSF();
        arff2SSF2.init("C:\\Users\\Sourabh\\Desktop\\Sanchay data\\weka data\\SmallFile2.txt.out.txt",
                "C:\\Users\\Sourabh\\Desktop\\Sanchay data\\weka data\\output.arff",
                "UTF-8",
                "C:\\Users\\Sourabh\\Desktop\\Sanchay data\\weka data\\arfftossf.utf8",
                "ne");
        arff2SSF2.convertToTagFormat(false);
    }
}
