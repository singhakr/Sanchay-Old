/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.formats.converters;

import sanchay.mlearning.svm.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
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
import sanchay.formats.converters.CRF2SSF;
import sanchay.mlearning.crf.DefaultDataSequence;
import sanchay.mlearning.svm.DefaultSVMData.DefaultSVMDocument;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author anil
 */

public class SVM2SSF extends DefaultMLCorpusConverter implements MLCorpusConverter
{
    protected String outputPrefix = ".out.txt";
    
    public SVM2SSF()
    {
        super();
    }
    
    public void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature)
    {
        super.init(ssfPath, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultSVMData();
        
         try {

            ((DefaultSVMData) mlCorpus).readSvmTagged(ssfPath, mlPath, charset);
        
        } catch (Exception ex) {
            Logger.getLogger(SVM2SSF.class.getName()).log(Level.SEVERE, null, ex);
        }

        ssfCorpus = new SSFCorpusImpl(charset);        
    }
    
    
 /*   public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature)
    {
        super.init(ssfFiles, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultSVMData();

         try {
            ((DefaultSVMData) mlCorpus).readSvmTagged(mlPath, charset);
        } catch (FcorpusileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus = new SSFCorpusImpl(charset);
    }*/
    
    protected void convertToTagFormat(boolean featureType)
    {
       int scount = ((DefaultSVMData) mlCorpus).countDocuments(); 
       
       SSFStory ssfStory = new SSFStoryImpl();
       
       DefaultSVMDocument doc = null;
       
       for(int i = 0; (i < scount) || scount == 0; i++)
        {
           int docStartIndex = 0;
            
           int docEndIndex = 0;
           
           if(scount == 0)
                docEndIndex = ((DefaultSVMData) mlCorpus).countDataSequences() ;
          /* else
            {
                doc = ((DefaultSVMData) mlCorpus).getDocument(i);
            
                docStartIndex = doc.getSequenceStart();
                docEndIndex = doc.getSequenceEnd();
               
            }*/
           for (int j = docStartIndex; j < docEndIndex; j++)
            {
               SSFSentence ssfSentence = new SSFSentenceImpl();
               
               SSFPhrase root = null;
               
               int icount;
               
               
               DefaultDataSequence dataSequence = (DefaultDataSequence) ((DefaultSVMData) mlCorpus).getDataSequence(j);
               
               icount = dataSequence.length();
               
               try {   
                    root = new SSFPhrase("0", "((", "SSF", "");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
               
               ssfSentence.setRoot(root);
               
               //System.out.println(" ==>"+ icount);
               for (int k = 0; k < icount; k++)
                {
                   SSFNode word = (SSFNode) dataSequence.x(k);
                   
                   int labelIndex = dataSequence.y(k);
                   
                   String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);
                   
                   SSFLexItem lexItem = null;
                   FeatureStructures fss = word.getFeatureStructures();

                  /* if(featureType)
                   {
                        if(fss == null)
                            fss = new FeatureStructuresImpl();

                        fss.setAttributeValue(labelFeature, label);

                        label = word.getName();
                   }*/
                   
                   try {   
                        lexItem = new SSFLexItem("0", word.getLexData(), label, fss);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                   
                   //System.out.println("== "+word.getLexData()+" "+ label);
                   root.addChild(lexItem);
                   
                }
               
               ssfStory.addSentence(ssfSentence);
            
            }
           
           if(scount == 0)
            {
                try {
                    ssfStory.save(outputPath, charset);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            }
            //else
            //    addStory(doc, ssfStory);
        
            
        }
       
    }    
    
     protected void convertToChunkFormatBIESO(boolean featureType, boolean preserveChunkBoundaries)
    {
        int scount = ((DefaultSVMData) mlCorpus).countDocuments();
        
        for(int i = 0; ( i < scount || scount == 0 ); i++)
        {
            System.out.println(GlobalProperties.getIntlString("_Convert_to_chunk_format"));
            int docStartIndex = 0;
            int docEndIndex = 0;
            
            if( scount ==0 ){
             docEndIndex = ((DefaultSVMData)mlCorpus).countDataSequences();   
                
            }else{   
            DefaultSVMDocument doc = ((DefaultSVMData) mlCorpus).getDocument(0);
            
            docStartIndex = doc.getSequenceStart();
            docEndIndex = doc.getSequenceEnd();
            }
            SSFStory ssfStory = new SSFStoryImpl();
            
            for (int j = docStartIndex; j < docEndIndex; j++)
            {
                DefaultDataSequence dataSequence = (DefaultDataSequence) ((DefaultSVMData) mlCorpus).getDataSequence(j);
                
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

                    System.out.println(" Word-"+node.getLexData()+" label-"+label);
                    if(preserveChunkBoundaries)
                    {
                       /* if(featureType)
                        {
                            if(label.equals("O") == false)
                            {
                                if(fss == null)
                                    fss = new FeatureStructuresImpl();

                                fss.setAttributeValue(labelFeature, label);
                            }
                        }
                        else*/
                            node.setName(label+"-"+node.getName());
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
                    String prevLabel = new String();
                    icount = dataSequence.length();
                    for (int k = 0; k < icount; k++)
                    {
                        SSFNode node = (SSFNode) dataSequence.x(k);
                        SSFNode word = (SSFNode) words.get(k);

                        int labelIndex = dataSequence.y(k);
                        
                        String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);
                        String baseLabel = "";
                        
                        if(label.equals("O") == false)
                        {
                            String parts[] = label.split("-");
                            baseLabel = parts[1];
                            label = parts[0];
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
                        
                        if(label.equals("O"))
                        {
                            root.addChild(word);
                            chunk = null;
                        }
                        else if(label.equals("B"))
                        {
                            
                            if(chunk != null || prevLabel.equals("B"))
                                root.addChild(chunk);
                                try {
                                    chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                              
                            chunk.addChild(node);
                        }else if(label.equals("I"))
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
                            
                        }else if(label.equals("E"))
                        {
                            chunk.addChild(node);
                            root.addChild(chunk);
                        }
                         prevLabelType = labelIndex;
                         prevLabel = label;
                    }
                }
                
                ssfStory.addSentence(ssfSentence); 
                
                
            }
             if(scount == 0)
            {
                try {
                    ssfStory.save(outputPath, charset);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            }
            //else
            //    addStory(doc, ssfStory);

        }                                                                                        
    }
    
      protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries)
    {
        int scount = ((DefaultSVMData) mlCorpus).countDocuments();
        
        
        
        for(int i = 0; ( i < scount || scount == 0 ); i++)
        {
            System.out.println(GlobalProperties.getIntlString("_Convert_to_chunk_format"));
            int docStartIndex = 0;
            int docEndIndex = 0;
            
            if( scount ==0 ){
             docEndIndex = ((DefaultSVMData)mlCorpus).countDataSequences();   
                
            }else{   
            DefaultSVMDocument doc = ((DefaultSVMData) mlCorpus).getDocument(0);
            
            docStartIndex = doc.getSequenceStart();
            docEndIndex = doc.getSequenceEnd();
            }
            SSFStory ssfStory = new SSFStoryImpl();
            
            for (int j = docStartIndex; j < docEndIndex; j++)
            {
                DefaultDataSequence dataSequence = (DefaultDataSequence) ((DefaultSVMData) mlCorpus).getDataSequence(j);
                
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
                       /* if(featureType)
                        {
                            if(label.equals("O") == false)
                            {
                                if(fss == null)
                                    fss = new FeatureStructuresImpl();

                                fss.setAttributeValue(labelFeature, label);
                            }
                        }
                        else
                            node.setName(label+"-"+node.getName());
                            root.addChild(node);*/
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
                
                System.out.println(GlobalProperties.getIntlString("_Words_In_current_sentence-")+words.size()+" "+words);
                
                if(preserveChunkBoundaries == false)
                {
                    chunk = null;
                    //int prevLabelType = MLClassLabels.OUTSIDE;
                    String prevLabel = new String();
                    icount = dataSequence.length();
                    String prevBaseLabel = "";
                    int flag = 0;
                    
                    for (int k = 0; k < icount; k++) {
                        SSFNode node = (SSFNode) dataSequence.x(k);
                        SSFNode word = (SSFNode) words.get(k);
                        flag = 0;
                        
                        
                        int labelIndex = dataSequence.y(k);

                        String label = labels.getInt2LabelMapping().getPropertyValue("" + labelIndex);
                        String baseLabel = "";
                        
                        //System.out.println(" Word-"+node.getLexData()+" label-"+label);
                        
                        
                        if (label.equals("O") == false) {
                            String parts[] = label.split("-");
                            baseLabel = parts[1];                               //CHUNK-TAG    
                            label = parts[0];                                   //BIO
                        }

                        FeatureStructures fss = new FeatureStructuresImpl();
                        if (featureType == true) {
                            if (label.equals("O") == false) {
                                fss.setAttributeValue(labelFeature, baseLabel);
                            }
                            baseLabel = word.getName();
                        }
                        
                        
                        if (label.equals("O")) {
                            flag = 1;
                            root.addChild(word);
                            chunk = null;                                                     
                        } else if (label.equals("B")) {

                            if (chunk != null || prevLabel.equals("B")) {                                
                                root.addChild(chunk);
                            }
                            
                            try {
                                chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            flag = 1;
                            chunk.addChild(node);
                        } else if (label.equals("I")) {                            
                            
                            if (chunk == null) {
                                try {
                                    chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }else if( !prevBaseLabel.equals(baseLabel) && prevLabel.equals("I") ){
                                 
                                root.addChild(chunk);
                                try {
                                    chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }                                                                
                            }
                            flag = 1;
                            chunk.addChild(node);
                        }
                        //prevLabelType = labelIndex;
                        
                        if (flag == 0) {
                            if (chunk == null) {
                                try {
                                    chunk = new SSFPhrase("0", word.getLexData(), baseLabel, fss);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                chunk.addChild(word);
                            }
                        }
                        
                        prevLabel = label;
                        prevBaseLabel = baseLabel;
                        
                        if( k == (icount -1) && chunk != null )
                        {
                            root.addChild(chunk);
                        }
                    }
                    
                }
                ssfSentence.setRoot(root);
                ssfStory.addSentence(ssfSentence); 
                
                
            }
             if(scount == 0)
            {
                try {
                    ssfStory.save(outputPath, charset);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CRF2SSF.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            }
            //else
            //    addStory(doc, ssfStory);

        }                                                                                        
    }
       
    protected void addStory(DefaultSVMDocument doc, SSFStory ssfStory)
    {    
        String opath = doc.getOutputPath();

        try {

            File ofile = new File(opath);
            File outputFile = new File(outputPath);

            if(outputFile.exists() && outputFile.isDirectory())
            {
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
    
    public static void main (String args[]) throws FileNotFoundException, IOException
    {
//        SVM2SSF svm2ssf = new SVM2SSF();
//        svm2ssf.init("/media/disk/Users/53590/Desktop/IIIT H/CORPUS/shallow-parsing-annotated-data/SmallFile.txt",
//                "/media/disk/Users/53590/Desktop/IIIT H/test/svm/SmallFileOP.txt",
//                GlobalProperties.getIntlString("UTF-8"),
//                "/media/disk/Users/53590/Desktop/IIIT H/test/svm/abc.txt",
//                "ne");
//        svm2ssf.labels.getInt2LabelMapping().read("/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/SVM_Pos-tagging/17-06-labels.txt",
//                GlobalProperties.getIntlString("UTF-8"));
//        svm2ssf.labels.setRevLabels( svm2ssf.labels.getInt2LabelMapping().getReverse());
//        
//        svm2ssf.convert(CHUNK_FORMAT);
        
        SVM2SSF converter = new SVM2SSF();
        converter.init("data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.out.txt",
                "data/automatic-annotation/pos-tagging/learntModels/smallStoryTagged", "UTF-8", "tmp/svm-ssf.txt", "ne");
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
    }
}