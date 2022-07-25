/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.evaluator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
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
public class ChunkingEvaluation1 {

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

    public ChunkingEvaluation1() {
    }

    public ChunkingEvaluation1(String referenceDataPath, String testDataPath, String resultsPath, String charset) {
        this.referenceDataPath = referenceDataPath;
        this.testDataPath = testDataPath;
        this.resultsPath = resultsPath;
        this.charset = charset;

        init();
    }

    private void init() {
        setReferenceDataDocument(new SSFStoryImpl());
        setTestDataDocument(new SSFStoryImpl());

        try {
            getReferenceDataDocument().readFile(getReferenceDataPath(), getCharset());
            getTestDataDocument().readFile(getTestDataPath(), getCharset());
            resultsPS = new PrintStream(resultsPath, charset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChunkingEvaluation1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChunkingEvaluation1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ChunkingEvaluation1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void finish() {
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

    public EvaluationResults evaluate(int mode) throws Exception {
        switch (mode) {
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

    public EvaluationResults evaluatePOSTagging() {
        int rscount = referenceDataDocument.countSentences();
        int tscount = testDataDocument.countSentences();

        if (rscount != tscount) {
            System.err.println("Error: Number of sentences not the same.");
            return null;
        }

        resultsPS.println("**********************************************");

        int totalDiffCount = 0;
        int totalTokenCount = 0;

        int numDiff[];
        for (int i = 0; i < rscount; i++) {
            SSFSentence refSentence = referenceDataDocument.getSentence(i);
            SSFSentence testSentence = testDataDocument.getSentence(i);

            SSFPhrase refRoot = refSentence.getRoot();
            SSFPhrase testRoot = testSentence.getRoot();

            numDiff = refRoot.getDifferentPOSTags(testRoot);

            // resultsPS.println("Reference file: " + referenceDataPath);
            // resultsPS.println("Test file: " + testDataPath);
            // resultsPS.println("Evaluation mode: " + "POS Tagging");

            totalTokenCount += refRoot.getAllLeaves().size();

            if (numDiff != null) {
                resultsPS.println("\tNumber of different tags in sentence " + (i + 1) + ": " + numDiff.length);
                totalDiffCount += numDiff.length;
            }
        }


        EvaluationResults evaluationResults = new EvaluationResults();

        evaluationResults.setPrecision((1.0 - (double) totalDiffCount / (double) totalTokenCount) * 100.0);

        resultsPS.println("**********************************************");

        return evaluationResults;
    }

    public EvaluationResults evaluateChunking() throws Exception {

        SSFSentence refSentence;

        SSFPhrase refSentenceRoot;

        SSFNode refSSFNode;

        List refLeaves = new ArrayList();

        char BIOTagRef;



        SSFSentence testSentence;

        SSFPhrase testSentenceRoot;

        SSFNode testSSFNode;

        List testLeaves = new ArrayList();

        char BIOtagtest;

        int errCount = 0;

        int refSentenceCount = referenceDataDocument.countSentences();  //System.out.println(" Number of Setences in Story 1="+refSentenceCount);

        int refWordCount1 = referenceDataDocument.countWords();                  //System.out.println(" Number of Words in Story 1="+refWordCount1);

        int testSentenceCount = testDataDocument.countSentences();          //System.out.println(" Number of Setences in Story 2="+testSentenceCount);

        int i = 0, j = 0;


        for (i = 0; i < refSentenceCount; i++) {
            refSentence = referenceDataDocument.getSentence(i);
            refSentenceRoot = refSentence.getRoot();
            refLeaves = refSentenceRoot.getAllLeaves();


            testSentence = testDataDocument.getSentence(i);
            testSentenceRoot = testSentence.getRoot();
            testLeaves = testSentenceRoot.getAllLeaves();

            for (j = 0; j < refLeaves.size(); j++) {
                refSSFNode = (SSFNode) refLeaves.get(j);

                BIOTagRef = getBIOTag(refSSFNode.getId());
                //System.out.println(" id-"+refSSFNode.getId()+" name:-"+refSSFNode.getName()+" LexData:-"+refSSFNode.getLexData());

                testSSFNode = (SSFNode) testLeaves.get(j);

                BIOtagtest = getBIOTag(testSSFNode.getId());
                //System.out.println(" id-"+testSSFNode.getId()+" name:-"+testSSFNode.getName()+" LexData:-"+testSSFNode.getLexData());
                //System.out.println("Tag1-"+BIOTagRef+" Tag2-"+BIOtagtest);

                if (BIOTagRef != BIOtagtest) {
                    errCount++;
                }

            }

            //System.out.println("**************************************************");
        }
        resultsPS.println("Total number of chunking errors-" + errCount);
        EvaluationResults evaluationResults = new EvaluationResults();

        return evaluationResults;
    }

    private char getBIOTag(String id) {

        String strAfterLastDot = new String();

        int IDNumber;

        strAfterLastDot = id.substring(id.lastIndexOf(".") + 1, id.length());

        try {
            IDNumber = Integer.parseInt(strAfterLastDot);

        } catch (NumberFormatException ex) {
            return 'O';
        }

        //System.out.print(" Tag id-"+IDNumber);

        if (IDNumber == 1) {
            return 'B';
        }


        if (IDNumber > 1) {
            return 'I';
        }


        return 'O';

    }

    public EvaluationResults evaluateNestedChunking() {
        return null;
    }

    public EvaluationResults evaluateSyntacticAnnotation() {
        return null;
    }

    public EvaluationResults evaluateSemanticTagging() {
        return null;
    }

    public static void main(String args[]) throws Exception {
        ChunkingEvaluation1 ssfEvaluation = new ChunkingEvaluation1("/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/chunking/testing/testing-1-utf8-ssf.txt.out_old.txt",
                "/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/chunking/testing/testing-1-utf8-ssf.txt.out_old2.txt",
                "/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/chunking/testing/results.txt",
                "UTF-8");

        ssfEvaluation.evaluate(CHUNKING_TAGGING_MODE);
        ssfEvaluation.finish();
    }
}