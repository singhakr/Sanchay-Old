/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.evaluator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.*;

/**
 *
 * @author H Umesh
 */
public class ChunkingEvaluation2 {
    protected String referenceDataPath;
    protected String testDataPath;
    protected String resultsPath;
    protected String charset;
    
    protected SSFStory referenceDataDocument;
    protected SSFStory testDataDocument;
    
    protected PrintStream resultsPS;

    public static final int POS_TAGGING_MODE = 0;
    public static final int CHUNKING_TAGGING_MODE = 1;
    public static final int NESTED_CHNKING_MODE = 2;
    public static final int SYNTACTIC_ANNOTATION_MODE = 3;
    public static final int SEMANTIC_TAGGING_MODE = 4;

    public ChunkingEvaluation2()
    {
        
    }

    public ChunkingEvaluation2(String referenceDataPath, String testDataPath, String resultsPath, String charset)
    {
        this.referenceDataPath = referenceDataPath;
        this.testDataPath = testDataPath;
        this.resultsPath = resultsPath;
        this.charset = charset;        
        
        init();
    }
    
    protected void init()
    {
        setReferenceDataDocument(new SSFStoryImpl());
        setTestDataDocument(new SSFStoryImpl());
        
        try {
            getReferenceDataDocument().readFile(getReferenceDataPath(), getCharset());
            getTestDataDocument().readFile(getTestDataPath(), getCharset());
            resultsPS = new PrintStream(resultsPath, charset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Evaluation2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Evaluation2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Evaluation2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void finish()
    {
        resultsPS.close();
    }

    public String getReferenceDataPath() {
        return referenceDataPath;
    }

    public void setReferenceDataPath(String referenceDataPath) {
        this.referenceDataPath = referenceDataPath;
    }

    public String getTestDataPath() {
        return testDataPath;
    }

    public void setTestDataPath(String testDataPath) {
        this.testDataPath = testDataPath;
    }

    public String getResultsPath() {
        return resultsPath;
    }

    public void setResultsPath(String resultsPath) {
        this.resultsPath = resultsPath;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public SSFStory getReferenceDataDocument() {
        return referenceDataDocument;
    }

    public void setReferenceDataDocument(SSFStory referenceDataDocument) {
        this.referenceDataDocument = referenceDataDocument;
    }

    public SSFStory getTestDataDocument() {
        return testDataDocument;
    }

    public void setTestDataDocument(SSFStory testDataDocument) {
        this.testDataDocument = testDataDocument;
    }
    
    public EvaluationResults evaluate(int mode) throws Exception
    {
        switch(mode)
        {
            case POS_TAGGING_MODE:
                return evaluatePOSTagging();
            case CHUNKING_TAGGING_MODE:
                return evaluateChunking();
            case NESTED_CHNKING_MODE:
                return evaluateNestedChunking();
            case SYNTACTIC_ANNOTATION_MODE:
                return evaluateSyntacticAnnotation();
            case SEMANTIC_TAGGING_MODE:
                return evaluateSemanticTagging();
            default:
                return evaluateChunking();
        }
    }
    
    public EvaluationResults evaluatePOSTagging()
    {
        int rscount = referenceDataDocument.countSentences();
        int tscount = testDataDocument.countSentences();
        
        if(rscount != tscount)
        {
            System.err.println("Error: Number of sentences not the same.");
            return null;
        }
            
        resultsPS.println("**********************************************");

        int totalDiffCount = 0;
        int totalTokenCount = 0;
        
        int numDiff[];
        for(int i = 0; i < rscount; i++)
        {
            SSFSentence refSentence = referenceDataDocument.getSentence(i);
            SSFSentence testSentence = testDataDocument.getSentence(i);

            SSFPhrase refRoot = refSentence.getRoot();
            SSFPhrase testRoot = testSentence.getRoot();
            
            numDiff = refRoot.getDifferentPOSTags(testRoot);

           // resultsPS.println("Reference file: " + referenceDataPath);
           // resultsPS.println("Test file: " + testDataPath);
           // resultsPS.println("Evaluation mode: " + "POS Tagging");
        
            totalTokenCount += refRoot.getAllLeaves().size();
            
            if(numDiff != null)
            {
                resultsPS.println("\tNumber of different tags in sentence " + (i + 1) + ": " + numDiff.length);            
                totalDiffCount += numDiff.length;
            }
        }
                
        
        EvaluationResults evaluationResults  =  new EvaluationResults();
        
        evaluationResults.setPrecision((1.0 - (double)totalDiffCount/(double)totalTokenCount) * 100.0);

        resultsPS.println("**********************************************");
         
        return evaluationResults;
    }
    
    public EvaluationResults evaluateChunking () throws Exception
    {
        
        SSFSentence refSentence;
        
        SSFPhrase refSentenceRoot;
        
        Vector refBIOTags=new Vector();
        
        
        SSFSentence testSentence;
        
        SSFPhrase testSentenceRoot;
        
        Vector testBIOTags=new Vector();
        
        
        int errCount=0;
        
        int  refSentenceCount  =  referenceDataDocument.countSentences ( ) ;
        int  wordCount  =  referenceDataDocument.countWords ( ) ;
        int  testSentenceCount  =  testDataDocument.countSentences ( ) ;
               
        
        int  i = 0, j = 0 ;
        
        if(refSentenceCount != testSentenceCount)
        {
            System.err.println("Error: Number of sentences not the same.");
            return null;
        }
        
        resultsPS.println("**********************************************");
        
        for ( i = 0 ; i < refSentenceCount ; i++ ) 
        {
        
            refSentence       =   referenceDataDocument.getSentence ( i ) ;
            
            refSentenceRoot   =   refSentence.getRoot ( ) ;
            
            refBIOTags        =   getBIOTag ( refSentenceRoot ) ;
            
            
            testSentence      =   testDataDocument.getSentence ( i ) ;
            
            testSentenceRoot  =   testSentence.getRoot ( ) ;
            
            testBIOTags       =   getBIOTag ( testSentenceRoot ) ;
            
            
            if ( refBIOTags.size ( )  !=  testBIOTags.size ( ) ) 
            {
            
                System.err.println ( "Error: Number of words in each sentence do not match." ) ;
                
                return null;
            }
            
            for ( j = 0;  j<  refBIOTags.size ( ) ; j++ )
            {
                
                if ( refBIOTags.elementAt ( j )   !=   testBIOTags.elementAt ( j ) )
                {
                    errCount++;
                }
           
            }
        
        
        }
        
        System.out.println("Number of errors-"+errCount);
        
        resultsPS.println("Number of errors-"+errCount);
        resultsPS.println("**********************************************");
        
        EvaluationResults evaluation=new EvaluationResults();
        
        evaluation.setPrecision( 1.0 - (double)errCount/(double)wordCount);
        
        return evaluation;
   
    }
    
    
    /***********  Returns a vector containing BIO tags for each word in the sentence  ***********/
    private Vector getBIOTag ( SSFPhrase SentenceRoot )
    {
        SSFNode child   =  new SSFNode ( ) ; 
        
        Vector sentenceTags = new Vector ( ) ;
        
        Vector childTags  =  new Vector ( ) ;
        
        int level = 0 ;
        
        int  i , j ;
        int childrenCount=SentenceRoot.countChildren();
        
        for ( i = 0 ; i< childrenCount ; i++ ) 
        {
            child  =  SentenceRoot.getChild ( i ) ;
            
            level  =  child.getLevel ( ) ;
            //System.out.println("LexData-"+child.getLexData()+" Name-"+child.getName()+" ID-"+child.getId()+" Level-"+child.getLevel());
           
            if (     !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   &&   level  !=  1 ) 
            {
                if ( i == 0 ) 
                {
                    sentenceTags.add('B');
                }
                else
                {
                    sentenceTags.add('I');
                }
            }
            
            if(      !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   &&   level ==  1  )
            {
                sentenceTags.add('O');
            }
            
            
            /*********************Recursive call in case of nested chunking**********************/
            if(   child.getLexData().equals("")    ||    child.getLexData().equals("((")   )
            {
                childTags   =  getBIOTag((SSFPhrase)child)  ;
                
                for( j = 0 ;  j<  childTags.size ( ) ;  j++  )
                {
                    sentenceTags.add(childTags.elementAt ( j ) );
                }
                
            }
        }
        
       return sentenceTags;
    } 
     
    public EvaluationResults evaluateNestedChunking()
    {
        return null;
    }
    
    public EvaluationResults evaluateSyntacticAnnotation()
    {
        return null;
    }
    
    public EvaluationResults evaluateSemanticTagging()
    {
        return null;        
    }
    
    public static void main(String args[]) throws Exception
    {
       ChunkingEvaluation2 ssfEvaluation = new ChunkingEvaluation2("/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/chunking/testing/testing-1-utf8-ssf.txt.out_old.txt",
                "/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/chunking/testing/testing-1-utf8-ssf.txt.out_old2.txt",
                "/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/chunking/testing/results.txt",
                "UTF-8");
        
        ssfEvaluation.evaluate(CHUNKING_TAGGING_MODE);
        ssfEvaluation.finish();
    }
}