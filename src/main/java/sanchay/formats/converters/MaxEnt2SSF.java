/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.formats.converters;

import sanchay.mlearning.maxent.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.formats.converters.CRF2SSF;
import static sanchay.mlearning.common.MLCorpusConverter.CHUNK_FORMAT;
import sanchay.mlearning.crf.DefaultDataSequence;
import sanchay.mlearning.maxent.DefaultMaxEntData.DefaultMaxEntDocument;

/**
 *
 * @author Anil Kumar Singh
 */
public class MaxEnt2SSF extends DefaultMLCorpusConverter implements MLCorpusConverter
{

      public MaxEnt2SSF() {
        super();
    }

    public void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature,int format)
    {
        super.init(ssfPath, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultMaxEntData();

         try {
             switch(format){
                 case TAG_FORMAT:
                   ((DefaultMaxEntData) mlCorpus).readTagged(mlPath, charset);
                   break;

                 case CHUNK_FORMAT:
                     ((DefaultMaxEntData) mlCorpus).readSSFChunk(mlPath, charset);
                     break;
             }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        ssfCorpus = new SSFCorpusImpl(charset);
    }

    public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature,int format)
    {
        super.init(ssfFiles, mlPath, cs, opath, labelFeature);

        mlCorpus = new DefaultMaxEntData();

         try {
            ((DefaultMaxEntData) mlCorpus).readTagged(mlPath, charset);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ssfCorpus = new SSFCorpusImpl(charset);
    }

    protected void convertToTagFormat(boolean featureType)
    {
        int scount = ((DefaultMaxEntData) mlCorpus).countDocuments();
        DefaultMaxEntDocument doc = null;

        SSFStory ssfStory = new SSFStoryImpl();

        for(int i = 0; (i < scount) || scount == 0; i++)
        {
            int docStartIndex = 0;
            int docEndIndex = 0;

            if(scount == 0)
                docEndIndex = ((DefaultMaxEntData) mlCorpus).countDataSequences() - 1;
            else
            {
                doc = ((DefaultMaxEntData) mlCorpus).getDocument(i);

                docStartIndex = doc.getSequenceStart();
                docEndIndex = doc.getSequenceEnd();
            }

            for (int j = docStartIndex; j <= docEndIndex; j++)
            {
                DefaultDataSequence dataSequence = (DefaultDataSequence) ((DefaultMaxEntData) mlCorpus).getDataSequence(j);

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
                    SSFNode word = (SSFNode) dataSequence.x(k);

                    String label = new String();

                    SSFLexItem lexItem = null;

                    FeatureStructures fss = word.getFeatureStructures();

                    if(featureType)
                    {
                        if(fss == null)
                            fss = new FeatureStructuresImpl();

                        fss.setAttributeValue(labelFeature, label);
                    }

                    label = word.getName();

                    try {
                        lexItem = new SSFLexItem("0", word.getLexData(), label, fss);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

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
            else
                addStory(doc, ssfStory);
        }

    }
    protected void addStory(DefaultMaxEntDocument doc, SSFStory ssfStory)
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


     protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries)
    {
        int scount = ((DefaultMaxEntData) mlCorpus).countDocuments();

        DefaultMaxEntDocument doc = null;

        for(int i = 0; (i < scount)||scount == 0; i++)
        {
            int docStartIndex = 0;
            int docEndIndex = 0;

            if(scount == 0)
                docEndIndex = ((DefaultMaxEntData) mlCorpus).countDataSequences() - 1;
            else
            {
                doc = ((DefaultMaxEntData) mlCorpus).getDocument(i);

                docStartIndex = doc.getSequenceStart();
                docEndIndex = doc.getSequenceEnd();
            }

            SSFStory ssfStory = new SSFStoryImpl();

            for (int j = docStartIndex; j <= docEndIndex; j++)
            {
                DefaultDataSequence dataSequence = (DefaultDataSequence) ((DefaultMaxEntData) mlCorpus).getDataSequence(j);

                int icount = dataSequence.length();
                System.out.println("icount---"+icount);

                SSFSentence ssfSentence = new SSFSentenceImpl();
                SSFPhrase root = null;
//                Vector words = new Vector(icount/2, icount/2);

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

                    String label = new String();
                    label = node.getName();

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

                    }
                }


                ssfStory.addSentence(ssfSentence);
            }
            if(scount == 0)
            {

                System.out.println(GlobalProperties.getIntlString("last")+outputPath+"      "+charset);
                try {
                    ssfStory.save(outputPath, charset);
                    System.out.println(GlobalProperties.getIntlString("finish"));
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


    public static void main(String argv[])
    {
//        MaxEnt2SSF maxEnt2SSF = new MaxEnt2SSF();
//        maxEnt2SSF.init("C:\\Users\\Sourabh\\Desktop\\Sanchay data\\taggedoutputmaxent.out.utf8",
//                "C:\\Users\\Sourabh\\Desktop\\Sanchay data\\sourabh.utf8",
//                GlobalProperties.getIntlString("UTF-8"),
//                "C:\\Users\\Sourabh\\Desktop\\Sanchay data\\sourabh.out.utf8",
//                "ne",CHUNK_FORMAT);
//        maxEnt2SSF.convertToChunkFormat(false,true);

        MaxEnt2SSF converter = new MaxEnt2SSF();
        converter.init("data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.out.txt",
                "tmp/maxent-ml.txt", "UTF-8", "tmp/maxent-ssf.txt", "ne", TAG_FORMAT);
        
        converter.convertToTagFormat(false);
    }
}
