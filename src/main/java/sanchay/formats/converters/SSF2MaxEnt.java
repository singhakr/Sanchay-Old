/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.formats.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFCorpusImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.tree.SanchayMutableTreeNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class SSF2MaxEnt extends DefaultMLCorpusConverter implements MLCorpusConverter
{
    protected PrintStream outputFile ;
    protected String outputPrefix = ".out.txt";
    String chunkName = null;

    /** Creates a new instance of SSF2MaxEnt */
    public SSF2MaxEnt()
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
            outputFile = new PrintStream(opath);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

      //  mlCorpus = new DefaultMaxEntData();
    }

    @Override
    public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature)
    {
        super.init(ssfFiles, mlPath, cs, opath, labelFeature);

        ssfCorpus = new SSFCorpusImpl(charset);

        try {
            ssfCorpus.read(ssfFiles);
            outputFile = new PrintStream(opath);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    //    mlCorpus = new DefaultMaxEntData();
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

                //DefaultDataSequence mlSentence = new DefaultDataSequence();

                SSFPhrase root = ssfSentence.getRoot();

                List<SanchayMutableTreeNode> words = root.getAllLeaves();

                int wcount = words.size();

                for (int j = 0; j < wcount; j++)
                {
                    SSFNode word = (SSFNode) words.get(j);
                    String label = word.getName();
                    String lexData = word.getLexData();
                    outputFile.print(lexData+"_"+label+" ");

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
                }
                outputFile.println();
            }
        }
    }

    protected void convertToChunkFormat(boolean featureType)
    {
        Enumeration enm = ssfCorpus.getStoryKeys();

      //  int docIndex = 0;

        SSFSentence ssfSentence;

        SSFPhrase ssfPhrase;

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

            int i=0;

            for ( i = 0 ; i < scount ; i++ ) {

            ssfSentence  =  ssfStory.getSentence ( i ) ;

            ssfPhrase   =   ssfSentence.getRoot ( ) ;

            getBIOTag ( ssfPhrase ) ;

            outputFile.println();

            }
        }
    }


    private void getBIOTag ( SSFPhrase SentenceRoot )
    {
        SSFNode child   =  new SSFNode ( ) ;

        int level = 0 ,i ;

        int childrenCount = SentenceRoot.countChildren();

        for ( i = 0 ; i< childrenCount ; i++ )
        {
            child  =  SentenceRoot.getChild ( i ) ;

            level  =  child.getLevel ( ) ;

            String label = "UNK";

            if( level==1) {
                chunkName = (SentenceRoot.getChild(i)).getName() ;
            }
         //   System.out.println(chunkName);

            if (     !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   &&   level  !=  1 ) {

                outputFile.print(child.getLexData()+" "+child.getName());
                if ( i == 0 )
                {
                    label = "B-" + chunkName;
                    outputFile.println(" B-"+chunkName);
                }
                else
                {
                    label = "I-" + chunkName;
                    outputFile.println(" I-"+chunkName);
                }
                outputFile.println();
            }

            if(      !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   &&   level ==  1  ) {

                outputFile.print(child.getLexData()+" "+child.getName());
                outputFile.println(" O-"+chunkName);
                outputFile.println();
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

            /*********************Recursive call in case of nested chunking**********************/
            if(   child.getLexData().equals("")    ||    child.getLexData().equals("((")   ) {
                getBIOTag((SSFPhrase)child)  ;
            }

        }
    }

    public static void main(String [] args)
    {
//        SSF2MaxEnt ssf2Maxent = new SSF2MaxEnt();
//        ssf2Maxent.init("C:\\Users\\Sourabh\\Desktop\\Sanchay data\\shallow-parsing-training-data\\240k-hindi-wx.ssf.clean-8-bform.isc.utf8",
//                "C:\\Users\\Sourabh\\Desktop\\Sanchay data\\shallow-parsing-training-data\\240k-hindi-wx.ssf.clean-8-bform.isc.out.utf8",
//                GlobalProperties.getIntlString("UTF-8"), "C:\\Users\\Sourabh\\Desktop\\Sanchay data\\shallow-parsing-training-data\\240k-hindi-wx.ssf.clean-8-bform.isc.out.utf8", "ne");
//        ssf2Maxent.convertToChunkFormat(false);

        SSF2MaxEnt converter = new SSF2MaxEnt();
        converter.init("data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.out.txt",
                "tmp/maxent-ml.txt", "UTF-8", "tmp/maxent-ml.txt", "ne");

        converter.convertToTagFormat(false);
    }
}
