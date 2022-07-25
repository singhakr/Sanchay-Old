/*
 * CRF2SSF.java
 *
 * Created on September 10, 2008, 7:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.formats.converters;

import sanchay.mlearning.crf.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.impl.SSFCorpusImpl;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.mlearning.crf.DefaultCRFData.DefaultCRFDocument;
import sanchay.properties.KeyValueProperties;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author Anil Kumar Singh
 */
public class CRF2SSF extends DefaultMLCorpusConverter implements MLCorpusConverter {

//    protected SSFCorpus ssfCorpus;
//    protected DefaultCRFData crfCorpus;
    
    /** Creates a new instance of CRF2SSF */
    public CRF2SSF() {
        super();
    }
    
    public void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature)
    {
        super.init(ssfPath, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultCRFData();
        
         try {            
            ((DefaultCRFData) mlCorpus).readTagged(mlPath, charset);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus = new SSFCorpusImpl(charset);        
    }

    public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature)
    {
        super.init(ssfFiles, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultCRFData();

         try {
            ((DefaultCRFData) mlCorpus).readTagged(mlPath, charset);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus = new SSFCorpusImpl(charset);
    }
    
    protected void convertToTagFormat(boolean featureType)
    {
        int scount = ((DefaultCRFData) mlCorpus).countDocuments();
        DefaultCRFDocument doc = null;

        for(int i = 0; (i < scount) || scount == 0; i++)
        {
            int docStartIndex = 0;
            int docEndIndex = 0;

            if(scount == 0)
                docEndIndex = ((DefaultCRFData) mlCorpus).countDataSequences() - 1;
            else
            {
                doc = ((DefaultCRFData) mlCorpus).getDocument(i);
            
                docStartIndex = doc.getSequenceStart();
                docEndIndex = doc.getSequenceEnd();
            }
            
            SSFStory ssfStory = new SSFStoryImpl();
            /*Modified by Anil Kumar Singh*/
            for (int j = docStartIndex; j <= docEndIndex; j++)
            {
                DefaultDataSequence dataSequence = (DefaultDataSequence) ((DefaultCRFData) mlCorpus).getDataSequence(j);
                
                int icount = dataSequence.length();
                
                SSFSentence ssfSentence = new SSFSentenceImpl();
                SSFPhrase root = null;
                
                try {   
                    root = new SSFPhrase("0", "((", "SSF", "");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                ssfSentence.setRoot(root);
                
                for (int k = 0; k < icount; k++)
                {
                    SSFNode word = null;
 
                    if(dataSequence.x(k) instanceof SSFNode)
                    {
                        word = (SSFNode) dataSequence.x(k);
                    }
                    else
                    {
                        try {
                            word = new SSFLexItem("0", (String) dataSequence.x(k), "", "");
                        } catch (Exception ex) {
                            Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    int labelIndex = dataSequence.y(k);
                    
                    String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);
                    
                    SSFLexItem lexItem = null;
                    FeatureStructures fss = word.getFeatureStructures();

                    if(featureType)
                    {
                        if(fss == null)
                            fss = new FeatureStructuresImpl();

                        fss.setAttributeValue(labelFeature, label);

                        label = word.getName();
                    }
                    
                    try {   
                        lexItem = new SSFLexItem("0", word.getLexData(), label, fss);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    /*Removed by Anil Kumar Singh*/
//                    if(lexItem.getLexData().length() == 2 && lexItem.getLexData().charAt(0) == '_'
//                        && UtilityFunctions.isPunctuation("" + lexItem.getLexData().charAt(1)))
//                    {
//                        lexItem.setLexData("" + lexItem.getLexData().charAt(1));
//                        
//                    }
                    /*Added by Anil Kumar Singh*/
                    if(lexItem.getLexData().length() > 1 && lexItem.getLexData().charAt(0) == '_')
                    {
                        lexItem.setLexData(lexItem.getLexData().substring(1));
                    }
                    root.addChild(lexItem);                    
                }
    
                ssfStory.addSentence(ssfSentence);
            }

            if(scount == 0)
            {
                try {
                    ssfStory.save(ssfPath, charset);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                break;
            }
            else
                addStory(doc, ssfStory);
        }
    }
    
    protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries)
    {
        int scount = ((DefaultCRFData) mlCorpus).countDocuments();

        for(int i = 0; i < scount; i++)
        {
            DefaultCRFDocument doc = ((DefaultCRFData) mlCorpus).getDocument(i);
            
            int docStartIndex = doc.getSequenceStart();
            int docEndIndex = doc.getSequenceEnd();
            
            SSFStory ssfStory = new SSFStoryImpl();
            
            for (int j = docStartIndex; j < docEndIndex; j++)
            {
                ChunkedDataSequence dataSequence = (ChunkedDataSequence) ((DefaultCRFData) mlCorpus).getDataSequence(j);
                
                int icount = dataSequence.length();
                
                SSFSentence ssfSentence = new SSFSentenceImpl();
                SSFPhrase root = null;
                Vector words = new Vector(icount/2, icount/2);
                
                try {   
                    root = new SSFPhrase("0", "((", "SSF", "");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                ssfSentence.setRoot(root);
                SSFPhrase chunk = null;

                for (int k = 0; k < icount; k++)
                {
                    SSFNode node = (SSFNode) dataSequence.x(k);
                    
                    int labelIndex = dataSequence.y(k);
                    
                    String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);

                    FeatureStructures fss = node.getFeatureStructures();

                    if(preserveChunkBoundaries)
                    {
                        if(featureType)
                        {
                            if(label.equals("O") == false)
                            {
                                if(fss == null)
                                    fss = new FeatureStructuresImpl();

                                fss.setAttributeValue(labelFeature, label);
                            }
                        }
                        else
                            node.setName(label);

                        root.addChild(node);
                    }
                    else
                    {
                        SSFLexItem lexItem = null;

                        try {
                            lexItem = new SSFLexItem("0", node.getLexData(), node.getName(), fss);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        words.add(lexItem);
                    }
                }

                if(preserveChunkBoundaries == false)
                {
                    chunk = null;
                    int prevLabelType = MLClassLabels.OUTSIDE;

                    for (int k = 0; k < icount; k++)
                    {
                        SSFNode node = (SSFNode) dataSequence.x(k);
                        SSFNode word = (SSFNode) words.get(k);

                        int labelIndex = dataSequence.y(k);
//                        int segmentEnd = dataSequence.getSegmentEnd(k);
//
//                        String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);
//
//                        if(segmentEnd != -1)
//                        {
//                            if(chunk != null)
//                                root.addChild(chunk);
//
//                            FeatureStructures fss = new FeatureStructuresImpl();
//                            String name = "";
//
//                            if(featureType)
//                                fss.setAttributeValue(labelFeature, label);
//                            else
//                                name = label;
//
//                            try {
//                                chunk = new SSFPhrase("0", word.getLexData(), name, fss);
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//
//                        chunk.addChild(node);

                        String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);
                        String baseLabel = "";

                        if(label.equals("O") == false)
                        {
                            String parts[] = label.split("-");
                            baseLabel = parts[1];
                        }

                        FeatureStructures fss = new FeatureStructuresImpl();

                        if(featureType == true)
                        {
                            if(label.equals("O") == false)
                            {
                                fss.setAttributeValue(labelFeature, baseLabel);
                            }

                            baseLabel = word.getName();
                        }

                        if(labels.isOutside(labelIndex))
                        {
                            root.addChild(word);
                            chunk = null;
                        }
                        else if(labels.isBeginning(labelIndex))
                        {
                            if(chunk != null)
                                root.addChild(chunk);

//                            if(chunk == null)
//                            {
                                try {
                                    chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
//                            }
                            
                            chunk.addChild(node);
                        }
//                        else if(labels.isSingle(labelIndex))
//                        {
//                            if(chunk != null)
//                                root.addChild(chunk);
//
//                            try {
//                                chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//
//                            chunk.addChild(node);
//                            root.addChild(chunk);
//                            chunk = null;
//                        }
//                        else if(labels.isEnd(labelIndex))
//                        {
//                            if(chunk == null)
//                            {
//                                try {
//                                    chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
//                                } catch (Exception ex) {
//                                    ex.printStackTrace();
//                                }
//                            }
//
//                            chunk.addChild(node);
//                            root.addChild(chunk);
//                            chunk = null;
//                        }
                        else if(labels.isIntermediate(labelIndex))
                        {
                            if(chunk == null)
                            {
                                try {
                                    chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            chunk.addChild(node);

                            if(k == icount -1)
                                root.addChild(chunk);
                        }

                        prevLabelType = labelIndex;
                    }
                }

                ssfStory.addSentence(ssfSentence);
            }
            
            addStory(doc, ssfStory);
        }
    }
//
//    protected void convertToChunkAttributeFormat()
//    {
//        int scount = mlCorpus.countDocuments();
//        DefaultCRFDocument doc = null;
//
//        for(int i = 0; (i < scount) || scount == 0; i++)
//        {
//            int docStartIndex = 0;
//            int docEndIndex = 0;
//
//            if(scount == 0)
//                docEndIndex = mlCorpus.countDataSequences() - 1;
//            else
//            {
//                doc = mlCorpus.getDocument(i);
//
//                docStartIndex = doc.getSequenceStart();
//                docEndIndex = doc.getSequenceEnd();
//            }
//
//            SSFStory ssfStory = new SSFStoryImpl();
//
//            for (int j = docStartIndex; j < docEndIndex; j++)
//            {
//                DefaultDataSequence dataSequence = (DefaultDataSequence) mlCorpus.getDataSequence(j);
//
//                int icount = dataSequence.length();
//
//                SSFSentence ssfSentence = new SSFSentenceImpl();
//                SSFPhrase root = null;
//
//                try {
//                    root = new SSFPhrase("0", "((", "SSF", "");
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//                ssfSentence.setRoot(root);
//
//                for (int k = 0; k < icount; k++)
//                {
//                    SSFNode word = (SSFNode) dataSequence.x(k);
//
//                    int labelIndex = dataSequence.y(k);
//
//                    String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);
//
//                    SSFLexItem lexItem = null;
//                    FeatureStructures fss = word.getFeatureStructures();
//
//                    if(featureType)
//                    {
//                        if(fss == null)
//                            fss = new FeatureStructuresImpl();
//
//                        fss.setAttributeValue(labelFeature, label);
//
//                        label = word.getName();
//                    }
//
//                    try {
//                        lexItem = new SSFLexItem("0", word.getLexData(), label, fss);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//
//                    root.addChild(lexItem);
//                }
//
//                ssfStory.addSentence(ssfSentence);
//            }
//
//            if(scount == 0)
//            {
//                try {
//                    ssfStory.save(ssfPath, charset);
//                } catch (FileNotFoundException ex) {
//                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//                break;
//            }
//            else
//                addStory(doc, ssfStory);
//        }
//    }
    
    protected void addStory(DefaultCRFDocument doc, SSFStory ssfStory)
    {    
        String opath = doc.getOutputPath();
        System.out.println("outputPath = " + opath);
        try {

            File ofile = new File(opath);
            File outputFile = new File(outputPath);
            System.out.println(outputFile.exists() + "" + outputFile.isDirectory() );
            if(outputFile.exists() && outputFile.isDirectory())
            {
                outputFile = new File(outputFile, ofile.getName());

                opath = outputFile.getAbsolutePath();
            }

            ssfStory.save(opath, charset);
            System.out.println(opath + " File saved");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus.addStory(outputPath, null);
    }

    public static void main(String argv[])
    {
        CRF2SSF converter = new CRF2SSF();
        converter.init("tmp/reference-ssf.txt",
                "tmp/crf-ml.txt",
                "UTF-8", "tmp/reference-ssf.txt", "ne");
        try {
            KeyValueProperties int2Labels = new KeyValueProperties("tmp/int2labels.txt", "UTF-8");
            
            MLClassLabels labels = new MLClassLabels();
            labels.setRevLabels(int2Labels);
            
            converter.setLabels(labels);
        
            converter.convertToTagFormat(false);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
//        crfAnnotation.annotate();
    }
}
