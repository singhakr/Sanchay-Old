/*
 * SSF2CRF.java
 *
 * Created on September 9, 2008, 1:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.formats.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFCorpusImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.mlearning.crf.ChunkedDataSequence;
import sanchay.mlearning.crf.DefaultCRFData;
import sanchay.mlearning.crf.DefaultDataSequence;

/**
 *
 * @author Anil Kumar Singh
 */
public class SSF2CRF extends DefaultMLCorpusConverter implements MLCorpusConverter {
    
//    protected SSFCorpus ssfCorpus;
//    protected DefaultCRFData crfCorpus;
    
    protected String outputPrefix = ".out.txt";
    
    /** Creates a new instance of SSF2CRF */
    public SSF2CRF()
    {
        super();
    }
    
    @Override
    public void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature)
    {
        super.init(ssfPath, mlPath, cs, opath, labelFeature);
        
        ssfCorpus = new SSFCorpusImpl(charset);
        
        try {            
            ssfCorpus.read(new File(ssfPath));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        mlCorpus = new DefaultCRFData();
    }

    @Override
    public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature)
    {
        super.init(ssfFiles, mlPath, cs, opath, labelFeature);

        ssfCorpus = new SSFCorpusImpl(charset);

        try {
            ssfCorpus.read(ssfFiles);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mlCorpus = new DefaultCRFData();
    }
    
    @Override
    protected void convertToTagFormat(boolean featureType)
    {
        Enumeration enm = ssfCorpus.getStoryKeys();

        int docIndex = 0;
        
        while(enm.hasMoreElements())
        {
            String storyPath = (String) enm.nextElement();
            
            SSFStory ssfStory = new SSFStoryImpl();
            
            try {                                
                ssfStory.readFile(storyPath, charset);
                
                if(SimpleStoryImpl.getCorpusType(storyPath, charset) != CorpusType.RAW)
                {
                    String tmpFile = GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp-tmp-tmp.abc.xyz";
                    
                    ssfStory.save(tmpFile, charset);                    
        
                    ssfStory = new SSFStoryImpl();

                    ssfStory.readFile(tmpFile, charset);
                }                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            SSFStory ssfStory = ssfCorpus.getStory(storyPath);
            
            int scount = ssfStory.countSentences();
            
            for (int i = 0; i < scount; i++)
            {
                SSFSentence ssfSentence = ssfStory.getSentence(i);
                
                DefaultDataSequence mlSentence = new DefaultDataSequence();
                
                SSFPhrase root = ssfSentence.getRoot();
                
                List words = root.getAllLeaves();
                
                int wcount = words.size();
                
                for (int j = 0; j < wcount; j++)
                {
                    SSFNode word = (SSFNode) words.get(j);
                    String label = word.getName();

                    if(featureType)
                    {
                        label = word.getAttributeValue(labelFeature);

                        if(label == null) {
                            label = MLClassLabels.UNDEFINED_LABEL;
                        }
                    }

                    String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);
                    
                    int labelIndex = -1;

                    if(labelIndexStr == null)
                    {
                        labelIndex = labels.getLabel2IntMapping().countProperties();
                        labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                        labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
                    }
                    else
                    {
                        labelIndex = Integer.parseInt(labelIndexStr);
                    }
                    
                    if(word.getLexData().length() > 1) {
                        word.setLexData(word.getLexData().replaceAll(":", ""));
                    }
                    
                    mlSentence.add_x(word);
                    mlSentence.set_y(j, labelIndex);
                }
                
                ((DefaultCRFData) mlCorpus).add(mlSentence);
            }
            
            ((DefaultCRFData) mlCorpus).addDocumentBoundaries(docIndex, docIndex + scount, storyPath, storyPath + outputPrefix);
            
            docIndex += scount;
        }
        
        try {   
            ((DefaultCRFData) mlCorpus).saveTagged(mlPath, charset);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries)
    {
        Enumeration enm = ssfCorpus.getStoryKeys();

        int docIndex = 0;

        while(enm.hasMoreElements())
        {
            String storyPath = (String) enm.nextElement();

            SSFStory ssfStory = new SSFStoryImpl();

            try {
                ssfStory.readFile(storyPath, charset);
                
                if(SimpleStoryImpl.getCorpusType(storyPath, charset) != CorpusType.RAW)
                {
                    String tmpFile = GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp-tmp-tmp.abc.xyz";
                    
                    ssfStory.save(tmpFile, charset);                    
        
                    ssfStory = new SSFStoryImpl();

                    ssfStory.readFile(tmpFile, charset);
                }                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            SSFStory ssfStory = ssfCorpus.getStory(storyPath);

            int scount = ssfStory.countSentences();

            for (int i = 0; i < scount; i++)
            {
                SSFSentence ssfSentence = ssfStory.getSentence(i);

                ChunkedDataSequence mlSentence = new ChunkedDataSequence();

                SSFPhrase root = ssfSentence.getRoot();

                int ccount = root.countChildren();

                int wrdPos = 0;
                int prevWrdPos = 0;

                for (int j = 0; j < ccount; j++)
                {
                    SSFNode node = root.getChild(j);
                    String label = node.getName();

                    if(featureType)
                    {
                        label = node.getAttributeValue(labelFeature);

                        if(label == null) {
                            label = MLClassLabels.UNDEFINED_LABEL;
                        }
                    }

                    if(preserveChunkBoundaries)
                    {
                        String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);

                        int labelIndex = -1;

                        if(labelIndexStr == null)
                        {
                            labelIndex = labels.getLabel2IntMapping().countProperties();
                            labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                            labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
                        }
                        else
                        {
                            labelIndex = Integer.parseInt(labelIndexStr);
                        }
                    
                        if(node.getLexData().length() > 1) {
                            node.setLexData(node.getLexData().replaceAll(":", ""));
                        }

                        mlSentence.add_x(node);
                        mlSentence.set_y(j, labelIndex);
                    }
                    else
                    {
                        int wcount = node.countChildren();
                        int labelIndex = -1;

                        if(wcount <= 0)
                        {
                            label = "O";

                            String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);

                            labelIndex = -1;

                            if(labelIndexStr == null)
                            {
                                labelIndex = labels.getLabel2IntMapping().countProperties();
                                labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                                labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
                            }
                            else
                            {
                                labelIndex = Integer.parseInt(labelIndexStr);
                            }
                    
                            if(node.getLexData().length() > 1) {
                                node.setLexData(node.getLexData().replaceAll(":", ""));
                            }

                            mlSentence.add_x(node);
                            mlSentence.set_y(wrdPos, labelIndex);

                            mlSentence.addChunkBoundaries(wrdPos, wrdPos + 1, node);
                            mlSentence.setSegment(wrdPos, wrdPos++, labelIndex);
                        }
                        else
                        {
                            for (int k = 0; k < wcount; k++)
                            {
                                String innerLabel = label;
                                SSFNode innerNode = ((SSFPhrase) node).getChild(k);
    
                                if(k == 0)
                                {
//                                    if(wcount == 1)
//                                        innerLabel = "S-" + innerLabel;
//                                    else
                                        innerLabel = "B-" + innerLabel;
                                }
//                                else if(k == wcount - 1)
//                                {
//                                    innerLabel = "E-" + innerLabel;
//                                }
                                else {
                                    innerLabel = "I-" + innerLabel;
                                }

                                String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(innerLabel);

                                labelIndex = -1;

                                if(labelIndexStr == null)
                                {
                                    labelIndex = labels.getLabel2IntMapping().countProperties();
                                    labels.getLabel2IntMapping().addProperty(innerLabel, "" + labelIndex);
                                    labels.getInt2LabelMapping().addProperty("" + labelIndex, innerLabel);
                                }
                                else
                                {
                                    labelIndex = Integer.parseInt(labelIndexStr);
                                }
//                                String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);
//
//                                labelIndex = -1;
//
//                                if(labelIndexStr == null)
//                                {
//                                    labelIndex = labels.getLabel2IntMapping().countProperties();
//                                    labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
//                                    labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
//                                }
//                                else
//                                {
//                                    labelIndex = Integer.parseInt(labelIndexStr);
//                                }
                    
                                if(innerNode.getLexData().length() > 1) {
                                    innerNode.setLexData(innerNode.getLexData().replaceAll(":", ""));
                                }

                                mlSentence.add_x(innerNode);
                                mlSentence.set_y(wrdPos++, labelIndex);
                            }

//                            wrdPos += node.countChildren();

                            mlSentence.addChunkBoundaries(prevWrdPos, wrdPos, node);
//                            mlSentence.setSegment(prevWrdPos, wrdPos - 1, labelIndex);

                            prevWrdPos = wrdPos;
                        }
                    }
                }

                ((DefaultCRFData) mlCorpus).add(mlSentence);
            }

            ((DefaultCRFData) mlCorpus).addDocumentBoundaries(docIndex, docIndex + scount, storyPath, storyPath + outputPrefix);

            docIndex += scount;
        }

        try {
            ((DefaultCRFData) mlCorpus).saveTagged(mlPath, charset);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

//    protected void convertToChunkAttributeFormat()
//    {
//        Enumeration enm = ssfCorpus.getStoryKeys();
//
//        int docIndex = 0;
//
//        while(enm.hasMoreElements())
//        {
//            String storyPath = (String) enm.nextElement();
//
//            SSFStory ssfStory = new SSFStoryImpl();
//
//            try {
//                ssfStory.readFile(storyPath, charset);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
////            SSFStory ssfStory = ssfCorpus.getStory(storyPath);
//
//            int scount = ssfStory.countSentences();
//
//            for (int i = 0; i < scount; i++)
//            {
//                SSFSentence ssfSentence = ssfStory.getSentence(i);
//
//                DefaultDataSequence mlSentence = new DefaultDataSequence();
//
//                SSFPhrase root = ssfSentence.getRoot();
//
//                int ccount = root.countChildren();
//
//                for (int j = 0; j < ccount; j++)
//                {
//                    SSFNode node = (SSFNode) root.getChild(j);
//
//                    String label = node.getAttributeValue(labelFeature);
//
//                    if(label == null)
//                        label = MLClassLabels.UNDEFINED_LABEL;
//
//                    String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);
//
//                    int labelIndex = -1;
//
//                    if(labelIndexStr == null)
//                    {
//                        labelIndex = labels.getLabel2IntMapping().countProperties();
//                        labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
//                        labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
//                    }
//                    else
//                    {
//                        labelIndex = Integer.parseInt(labelIndexStr);
//                    }
//
//                    mlSentence.add_x(node);
//                    mlSentence.set_y(j, labelIndex);
//                }
//
//                mlCorpus.add(mlSentence);
//            }
//
//            mlCorpus.addDocumentBoundaries(docIndex, docIndex + scount, storyPath, storyPath + outputPrefix);
//
//            docIndex += scount;
//        }
//
//        try {
//            mlCorpus.saveTagged(mlPath, charset);
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

    public static void main(String argv[])
    {
        SSF2CRF converter = new SSF2CRF();
        
//        converter.init("data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.out.txt",
//                "tmp/crf-ml.txt", "UTF-8", "tmp/crf-ml.txt", "ne");
        converter.init("tmp/testData",
                "tmp/crf-ml.txt", "UTF-8", "tmp/crf-ml.txt", "ne");
        
//        converter.init(
//                new File[] {
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Aparna/bhojpuri data Aparna.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Archana/bhoj3/bhjpuri2.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Archana/bhoj3/bhjpuri3.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Archana/bhoj3/bhjpuri4.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Archana/bhoj3/bhjpuri5.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Archana/bhoj3/bhjpuri-new.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Chandani/file1.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Chandani/file2.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Chandani/file3.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Chandani/file4.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Chandani/file5.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Chandani/file6.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Chandani/file7.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Deepak/Bhoj-15.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Deepak/to-send.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Deepak/deepak.txt.bak"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Khushbu/Bhoj-7.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Khushbu/bhoj1.1.txt"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Satyam/Bhoj-4.txt-Lenovo"),
//                    new File("/home/anil/projects/nlp-tools/data/tagged file Bhojpuri/Satyam/Bhoj-10.txt-Satyam.bak")
//                },
//                "/home/anil/projects/nlp-tools/pos-tagger/crf/crf-ml.txt", "UTF-8", "/home/anil/projects/nlp-tools/pos-tagger/crf/crf-ml.txt", "ne");
        
        converter.convertToTagFormat(false);
        
        try {
            converter.getLabels().getInt2LabelMapping().save("tmp/int2labels.txt", "UTF-8");
//            converter.getLabels().getInt2LabelMapping().save("/home/anil/projects/nlp-tools/pos-tagger/crf/int2labels.txt", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2CRF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSF2CRF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
