/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.text.spell.PhonemeFeatureModel;

/**
 *
 * @author anil
 */
public class NGramLMImplTest {
    
    private NGramLM instance;
    private NGramLM instanceSentenceBoundaries;

    private String string = "a possible reconstruction of the vina is given here .\n"
            + "the vina of the region resembles the yantra .\n"
            + "the possible reconstruction of the yantra is not given here .";
    
    private NGramLM instanceStr;
    private NGramLM instanceSentenceBoundariesStr;

    private File file = new File("data/ngram/ngramlm-test-sample.txt");
    private int order = 4;
    
    public NGramLMImplTest() {
        instance = new NGramLMImpl(file, "word", order);
        instanceSentenceBoundaries = new NGramLMImpl(file, "word", order, true);

        instanceStr = new NGramLMImpl(file, "word", order);
        instanceSentenceBoundariesStr = new NGramLMImpl(file, "word", order, true);
        
        try {
            instance.makeNGramLM(file);
            instanceSentenceBoundaries.makeNGramLM(file);

            instanceStr.makeNGramLM(string);
            instanceSentenceBoundariesStr.makeNGramLM(string);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

//    /**
//     * Test of getCharset method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetCharset() {
//        String result = instance.getCharset();
//        assertNotNull(result);
//        assertTrue(Charset.isSupported(result));
//        System.out.println("getCharset: " + result);
//    }
//
//    /**
//     * Test of calcMergedTokenCount method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcMergedTokenCount() {
//        System.out.println("calcMergedTokenCount");
//        long expResult = 102L;
//        long result = instance.calcMergedTokenCount();
//        assertEquals(expResult, result);
//
//        expResult = 126L;
//        result = instanceSentenceBoundaries.calcMergedTokenCount();
//        assertEquals(expResult, result);
//
//        // For compilation with strings instead of files //
//        expResult = 102L;
//        result = instanceStr.calcMergedTokenCount();
//        assertEquals(expResult, result);
//
//        expResult = 126L;
//        result = instanceSentenceBoundariesStr.calcMergedTokenCount();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of calcTokenCount method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcTokenCount_0args() {
//        System.out.println("calcTokenCount");
//        long expResult = 21L;
//        long result = instance.calcTokenCount();
//        assertEquals(expResult, result);
//
//        expResult = 27L;
//        result = instanceSentenceBoundaries.calcTokenCount();
//        assertEquals(expResult, result);
//
//        // For compilation with strings instead of files //
//        expResult = 21L;
//        result = instanceStr.calcTokenCount();
//        assertEquals(expResult, result);
//
//        expResult = 27L;
//        result = instanceSentenceBoundariesStr.calcTokenCount();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of calcTokenCount method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcTokenCount_int() {
//        System.out.println("calcTokenCount");
//        int whichGram = 0;
//        long expResult = -1L;
//        long result = instance.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 1;
//        expResult = 30L;
//        result = instance.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 27L;
//        result = instance.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 24L;
//        result = instance.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 21L;
//        result = instance.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//        
//
//        // With sentence boundaries marked
//        whichGram = 1;
//        expResult = 36L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 33L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 30L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 27L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//        
//        // For compilation with strings instead of files //
//        whichGram = 1;
//        expResult = 30L;
//        result = instanceStr.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 27L;
//        result = instanceStr.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 24L;
//        result = instanceStr.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 21L;
//        result = instanceStr.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//        
//        // With sentence boundaries marked
//        whichGram = 1;
//        expResult = 36L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 33L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 30L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 27L;
//        result = instanceSentenceBoundaries.calcTokenCount(whichGram);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of calcMergedTypeCount method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcMergedTypeCount() {
//        System.out.println("calcMergedTypeCount");
//        long expResult = 74L;
//        long result = instance.calcMergedTypeCount();
//        assertEquals(expResult, result);
//
//        expResult = 89L;
//        result = instanceSentenceBoundaries.calcMergedTypeCount();
//        assertEquals(expResult, result);
//
//        // For compilation with strings instead of files //
//        expResult = 74L;
//        result = instanceStr.calcMergedTypeCount();
//        assertEquals(expResult, result);
//
//        expResult = 89L;
//        result = instanceSentenceBoundariesStr.calcMergedTypeCount();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of countTokens method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCountTokens() {
//        System.out.println("countTokens");
//        int whichGram = 4;
//        long expResult = 21L;
//        instance.calcTokenCount();
//        long result = instance.countTokens(whichGram);
//        assertEquals(expResult, result);
//        
//        // With sentence boundaries marked
//        expResult = 27L;
//        instanceSentenceBoundaries.calcTokenCount();
//        result = instanceSentenceBoundaries.countTokens(whichGram);
//        assertEquals(expResult, result);
//
//        // For compilation with strings instead of files //
//        expResult = 21L;
//        instanceStr.calcTokenCount();
//        result = instanceStr.countTokens(whichGram);
//        assertEquals(expResult, result);
//        
//        // With sentence boundaries marked
//        expResult = 27L;
//        instanceSentenceBoundariesStr.calcTokenCount();
//        result = instanceSentenceBoundariesStr.countTokens(whichGram);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of getNGramLMFile method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetNGramLMFile() {
//        System.out.println("getNGramLMFile");
//        File result = instance.getNGramLMFile();
//        assertTrue(result.exists());
//    }
//
//    /**
//     * Test of setNGramLMFile method, of class NGramLMImpl.
//     */
//    @Test
//    public void testSetNGramLMFile() {
//        System.out.println("setNGramLMFile");
//        instance.setNGramLMFile(file);
//
//        File result = instance.getNGramLMFile();
//        assertTrue(result.exists());
//    }
//
//    /**
//     * Test of countNGrams method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCountNGrams() {
//        System.out.println("countNGrams");
//        int whichGram = 0;
//        long expResult = -1L;
//        long result = instance.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 1;
//        expResult = 14L;
//        result = instance.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 19L;
//        result = instance.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 21L;
//        result = instance.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 20L;
//        result = instance.countTypes(whichGram);
//        assertEquals(expResult, result);
//        
//
//        // With sentence boundaries marked
//        whichGram = 1;
//        expResult = 16L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 22L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 26L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 25L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//        
//        // For compilation with strings instead of files //
//        whichGram = 1;
//        expResult = 14L;
//        result = instanceStr.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 19L;
//        result = instanceStr.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 21L;
//        result = instanceStr.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 20L;
//        result = instanceStr.countTypes(whichGram);
//        assertEquals(expResult, result);
//        
//        // With sentence boundaries marked
//        whichGram = 1;
//        expResult = 16L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 2;
//        expResult = 22L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 3;
//        expResult = 26L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//
//        whichGram = 4;
//        expResult = 25L;
//        result = instanceSentenceBoundaries.countTypes(whichGram);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of hasNGram method, of class NGramLMImpl.
//     */
//    @Test
//    public void testHasNGram() {
//        System.out.println("hasNGram");
//
//        String ngramKey = "the";
//        int whichGram = 1;
//        boolean expResult = true;
//        boolean result = instance.hasNGram(ngramKey, whichGram);
//        assertEquals(expResult, result);
//
//        ngramKey = "reconstruction@#&of@#&the";
//        whichGram = 3;
//        expResult = true;
//        result = instance.hasNGram(ngramKey, whichGram);
//        assertEquals(expResult, result);
//
//        ngramKey = "reconstruction@#&the@#&vina";
//        whichGram = 3;
//        expResult = false;
//        result = instance.hasNGram(ngramKey, whichGram);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of hasNGramPlain method, of class NGramLMImpl.
//     */
//    @Test
//    public void testHasNGramPlain() {
//        System.out.println("hasNGram");
//
//        String ngramKey = "the";
//        int whichGram = 1;
//        boolean expResult = true;
//        boolean result = instance.hasNGramPlain(ngramKey, whichGram);
//        assertEquals(expResult, result);
//
//        ngramKey = "reconstruction of the";
//        whichGram = 3;
//        expResult = true;
//        result = instance.hasNGramPlain(ngramKey, whichGram);
//        assertEquals(expResult, result);
//
//        ngramKey = "reconstruction the vina";
//        whichGram = 3;
//        expResult = false;
//        result = instance.hasNGramPlain(ngramKey, whichGram);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of getNGram method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetNGram_ArrayList_int() {
//        System.out.println("getNGram");
////        String ng = "reconstruction of the";
////        List<Integer> wdIndices = NGramImpl.getIndicesPlain(ng, false);
////        int whichGram = 3;
////        NGram expResult = instance.getNGram(ng, whichGram);
////        NGram result = instance.getNGram(wdIndices, whichGram);
////        assertNull(expResult);
////        
////        ng = ng.replaceAll(" ", "@#&");
////        
////        expResult = instance.getNGram(ng, whichGram);
////        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of readNGramLM method, of class NGramLMImpl.
//     */
//    @Test
//    public void testReadNGramLM_0args() throws Exception {
//        System.out.println("readNGramLM");
//        NGramLMImpl newInstanceSentenceBoundaries = new NGramLMImpl(file, "word", order, true);
//        newInstanceSentenceBoundaries.readNGramLM(file, "UTF-8");
//    }
//
//    /**
//     * Test of calcCountsNProbs method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcCountsNProbs() {
//        System.out.println("calcCountsNProbs");
//        instance.calcCountsNProbs();
//
//        NGram ng = (NGram) instance.getNGram("the", 1);
//        
//        assertEquals((0.2), ng.getProb());
//
//        ng = (NGram) instanceSentenceBoundaries.getNGram("the", 1);
//        
//        assertEquals((1.0/6.0), ng.getProb());
//
//        ng = (NGram) instance.getNGramPlain("the vina", 2);
//        
//        assertEquals((2.0/6.0), ng.getProb());
//
//        ng = (NGram) instanceSentenceBoundaries.getNGramPlain("the vina", 2);
//        
//        assertEquals((2.0/6.0), ng.getProb());
//
//        ng = (NGram) instanceSentenceBoundaries.getNGramPlain("<s> the", 2);
//        
//        assertEquals((2.0/3.0), ng.getProb());
//    }
//
//    /**
//     * Test of calcMergedProbs method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcMergedProbs_0args() {
//        System.out.println("calcMergedProbs");
//        NGramLMImpl newInstance = new NGramLMImpl(file, "word", order);
//        NGramLMImpl newInstanceSentenceBoundaries = new NGramLMImpl(file, "word", order, true);
//        
//        try {
//            newInstance.makeNGramLM(file);
//            newInstanceSentenceBoundaries.makeNGramLM(file);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        newInstance.calcMergedProbs();
//        newInstanceSentenceBoundaries.calcMergedProbs();
//
//        NGram ng = (NGram) newInstance.getNGram("the", 1);
//        
//        assertEquals((6.0/102.0), ng.getProb());
//
//        ng = (NGram) newInstanceSentenceBoundaries.getNGram("the", 1);
//        
//        assertEquals((6.0/126.0), ng.getProb());
//
//        ng = (NGram) newInstance.getNGramPlain("the vina", 2);
//        
//        assertEquals((2.0/102.0), ng.getProb());
//
//        ng = (NGram) newInstanceSentenceBoundaries.getNGramPlain("the vina", 2);
//        
//        assertEquals((2.0/126.0), ng.getProb());
//
//        ng = (NGram) newInstanceSentenceBoundaries.getNGramPlain("<s> the", 2);
//        
//        assertEquals((2.0/126.0), ng.getProb());
//    }
//
//    /**
//     * Test of calcSimpleProbs method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcSimpleProbs_0args() {
//        System.out.println("calcSimpleProbs");
//        NGramLMImpl newInstance = new NGramLMImpl(file, "word", order);
//        NGramLMImpl newInstanceSentenceBoundaries = new NGramLMImpl(file, "word", order, true);
//        
//        try {
//            newInstance.makeNGramLM(file);
//            newInstanceSentenceBoundaries.makeNGramLM(file);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        newInstance.calcSimpleProbs();
//        newInstanceSentenceBoundaries.calcSimpleProbs();
//
//        NGram ng = (NGram) newInstance.getNGram("the", 1);
//        
//        assertEquals((0.2), ng.getProb());
//
//        ng = (NGram) newInstanceSentenceBoundaries.getNGram("the", 1);
//        
//        assertEquals((1.0/6.0), ng.getProb());
//
//        ng = (NGram) newInstance.getNGramPlain("the vina", 2);
//        
//        assertEquals((2.0/27.0), ng.getProb());
//
//        ng = (NGram) newInstanceSentenceBoundaries.getNGramPlain("the vina", 2);
//        
//        assertEquals((2.0/33.0), ng.getProb());
//
//        ng = (NGram) newInstanceSentenceBoundaries.getNGramPlain("<s> the", 2);
//        
//        assertEquals((2.0/33.0), ng.getProb());
//    }
//
//    /**
//     * Test of calcSmoothProbs method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcSmoothProbs_3args() {
//        System.out.println("calcSmoothProbs");
//        String Algo = "Witten-Bell";
////        long vocabSize = 0L;
//        int kValue = 5;
//        instanceSentenceBoundaries.calcSmoothProbs(Algo, kValue);
//        
//        try {
//            instanceSentenceBoundaries.saveNGramLM(file + ".lm", "UTF-8", true);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(NGramLMImplTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    /**
//     * Test of removeUNKL method, of class NGramLMImpl.
//     */
//    @Test
//    public void testRemoveUNKL() {
//        System.out.println("removeUNKL");
//        int whichGram = 0;
//        NGramLMImpl instance = null;
//        instance.removeUNKL(whichGram);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calcBackoff method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcBackoff() {
//        System.out.println("calcBackoff");
//        NGramLMImpl instance = null;
//        instance.calcBackoff();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calcSmoothKneserNey method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCalcSmoothKneserNey() {
//        System.out.println("calcSmoothKneserNey");
//        double delta = 0.0;
//        NGramLMImpl instance = null;
//        instance.calcSmoothKneserNey(delta);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSmoothKneserNeyProb method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetSmoothKneserNeyProb() {
//        System.out.println("getSmoothKneserNeyProb");
//        String nGramKey = "";
//        int whichGram = 0;
//        NGramLMImpl instance = null;
//        double expResult = 0.0;
//        double result = instance.getSmoothKneserNeyProb(nGramKey, whichGram);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of cleanNGramLM method, of class NGramLMImpl.
//     */
//    @Test
//    public void testCleanNGramLM() {
//        System.out.println("cleanNGramLM");
//        NGramLMImpl instance = null;
//        instance.cleanNGramLM();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of saveNGramLMBinary method, of class NGramLMImpl.
//     */
//    @Test
//    public void testWriteNGramLM() {
//        System.out.println("writeNGramLM");
//        PrintStream ps = null;
//        NGramLMImpl instance = null;
//        instance.writeNGramLM(ps, true);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of normalizeNGramProbs method, of class NGramLMImpl.
//     */
//    @Test
//    public void testNormalizeNGramProbs_0args() {
//        System.out.println("normalizeNGramProbs");
//        NGramLMImpl instance = null;
//        instance.normalizeNGramProbs();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSentenceProb method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetSentenceProb() {
//        System.out.println("getSentenceProb");
//        String sentence = "";
//        NGramLMImpl instance = null;
//        double expResult = 0.0;
//        double result = instance.getSentenceProb(sentence);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPhonemeSequenceProb method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetPhonemeSequenceProb() {
//        System.out.println("getPhonemeSequenceProb");
//        String sequence = "";
//        PhonemeFeatureModel phonemeFeatureModel = null;
//        NGramLMImpl instance = null;
//        double expResult = 0.0;
//        double result = instance.getPhonemeSequenceProb(sequence, phonemeFeatureModel);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of sort method, of class NGramLMImpl.
//     */
//    @Test
//    public void testSort_int() {
//        System.out.println("sort");
//        int sortOrder = 0;
//        NGramLMImpl instance = null;
//        instance.sort(sortOrder);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of clone method, of class NGramLMImpl.
//     */
//    @Test
//    public void testClone() {
//        System.out.println("clone");
//        NGramLMImpl instance = null;
//        Object expResult = null;
//        Object result = instance.clone();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of pruneByFrequency method, of class NGramLMImpl.
//     */
//    @Test
//    public void testPruneByFrequency() {
////        System.out.println("pruneByFrequency");
////        int minFreq = 0;
////        int whichGram = 0;
////        NGramLMImpl instance = null;
////        instance.pruneByFrequency(minFreq, whichGram);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of pruneByRankAndMerge method, of class NGramLMImpl.
//     */
//    @Test
//    public void testPruneByRankAndMerge() {
//        System.out.println("pruneByRankAndMerge");
//        int rank = 0;
//        int whichGram = 0;
//        NGramLMImpl instance = null;
//        instance.pruneByRankAndMerge(rank, whichGram);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of pruneByRank method, of class NGramLMImpl.
//     */
//    @Test
//    public void testPruneByRank() {
//        System.out.println("pruneByRank");
//        int rank = 0;
//        int whichGram = 0;
//        NGramLMImpl instance = null;
//        instance.pruneByRank(rank, whichGram);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of fillRanks method, of class NGramLMImpl.
//     */
//    @Test
//    public void testFillRanks() {
//        System.out.println("fillRanks");
//        int whichGram = 0;
//        NGramLMImpl instance = null;
//        instance.fillRanks(whichGram);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAllNgrams method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetAllNgrams() {
//        System.out.println("getAllNgrams");
//        NGramLMImpl instance = null;
//        LinkedHashMap expResult = null;
//        LinkedHashMap result = instance.getAllNgrams();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of storeNGramLM method, of class NGramLMImpl.
//     */
//    @Test
//    public void testStoreNGramLM() throws Exception {
//        System.out.println("storeNGramLM");
//        NGramLM nglm = null;
//        File file = null;
//        NGramLMImpl.storeNGramLM(nglm, file);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of storeNGramLMArpa method, of class NGramLMImpl.
//     */
//    @Test
//    public void testStoreNGramLMArpa() throws Exception {
//        System.out.println("storeNGramLMArpa");
//        NGramLM nglm = null;
//        File file = null;
//        String cs = "";
//        NGramLMImpl.storeNGramLMArpa(nglm, file, cs, true);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of makeTriggerPairs method, of class NGramLMImpl.
//     */
//    @Test
//    public void testMakeTriggerPairs() {
//        System.out.println("makeTriggerPairs");
//        NGramLMImpl instance = null;
//        instance.makeTriggerPairs();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCPMSFeaturesNGramLM method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetCPMSFeaturesNGramLM() {
//        System.out.println("getCPMSFeaturesNGramLM");
//        File f = null;
//        NGramLMImpl instance = null;
//        NGramLM expResult = null;
//        NGramLM result = instance.getCPMSFeaturesNGramLM(f);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findNGram method, of class NGramLMImpl.
//     */
//    @Test
//    public void testFindNGram() {
//        System.out.println("findNGram");
//        String ngram = "";
//        int order = 0;
//        int minFreq = 0;
//        int maxFreq = 0;
//        NGramLMImpl instance = null;
////        Hashtable expResult = null;
////        Hashtable result = instance.findNGram(ngram, order, minFreq, maxFreq);
////        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findNGramFile method, of class NGramLMImpl.
//     */
//    @Test
//    public void testFindNGramFile() {
//        System.out.println("findNGramFile");
//        String ngram = "";
//        int order = 0;
//        int minFreq = 0;
//        int maxFreq = 0;
//        NGramLMImpl instance = null;
////        Hashtable expResult = null;
////        Hashtable result = instance.findNGramFile(ngram, order, minFreq, maxFreq);
////        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of fCheckNGramFile method, of class NGramLMImpl.
//     */
//    @Test
//    public void testFCheckNGramFile() {
//        System.out.println("fCheckNGramFile");
//        String ngram = "";
//        int order = 0;
//        int minFreq = 0;
//        int maxFreq = 0;
//        NGramLMImpl instance = null;
//        boolean expResult = false;
//        boolean result = instance.fCheckNGramFile(ngram, order, minFreq, maxFreq);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of fCheckNGram method, of class NGramLMImpl.
//     */
//    @Test
//    public void testFCheckNGram() {
//        System.out.println("fCheckNGram");
//        String ngram = "";
//        int order = 0;
//        int minFreq = 0;
//        int maxFreq = 0;
//        NGramLMImpl instance = null;
//        boolean expResult = false;
//        boolean result = instance.fCheckNGram(ngram, order, minFreq, maxFreq);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSimilarity method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetSimilarity() {
//        System.out.println("getSimilarity");
//        NGramLM nGramLM1 = null;
//        NGramLM nGramLM2 = null;
//        double expResult = 0.0;
//        double result = NGramLMImpl.getSimilarity(nGramLM1, nGramLM2);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCumulativeFrequencies method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetCumulativeFrequencies() {
//        System.out.println("getCumulativeFrequencies");
//        int whichGram = 0;
//        NGramLMImpl instance = null;
//        LinkedHashMap expResult = null;
//        LinkedHashMap result = instance.getCumulativeFrequencies(whichGram);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getQuartile method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetQuartile() {
//        System.out.println("getQuartile");
//        String wds = "";
//        int whichGram = 0;
//        NGramLMImpl instance = null;
//        long expResult = 0L;
//        long result = instance.getQuartile(wds);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of percentNGramsInQuartile method, of class NGramLMImpl.
//     */
//    @Test
//    public void testPercentNGramsInQuartile_int() {
//        System.out.println("percentNGramsInQuartile");
////        int whichGram = 0;
////        NGramLMImpl instance = null;
////        List expResult = null;
////        List result = instance.percentNGramsInQuartile(whichGram);
////        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of percentNGramsInQuartile method, of class NGramLMImpl.
//     */
//    @Test
//    public void testPercentNGramsInQuartile_NGramLM_int() {
//        System.out.println("percentNGramsInQuartile");
////        NGramLM nglm = null;
////        int whichGram = 0;
////        NGramLMImpl instance = null;
////        List expResult = null;
////        List result = instance.percentNGramsInQuartile(nglm, whichGram);
////        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDistance method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetDistance() {
//        System.out.println("getDistance");
//        NGramLM nGramLM1 = null;
//        NGramLM nGramLM2 = null;
//        double expResult = 0.0;
//        double result = NGramLMImpl.getDistance(nGramLM1, nGramLM2);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCommonNGramCount method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetCommonNGramCount() {
//        System.out.println("getCommonNGramCount");
//        NGramLM nGramLM1 = null;
//        NGramLM nGramLM2 = null;
//        int expResult = 0;
//        int result = NGramLMImpl.getCommonNGramCount(nGramLM1, nGramLM2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCommonNGramEditDistance method, of class NGramLMImpl.
//     */
//    @Test
//    public void testGetCommonNGramEditDistance() {
//        System.out.println("getCommonNGramEditDistance");
//        NGramLM nGramLM1 = null;
//        NGramLM nGramLM2 = null;
//        int expResult = 0;
//        int result = NGramLMImpl.getCommonNGramEditDistance(nGramLM1, nGramLM2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of main method, of class NGramLMImpl.
//     */
//    @Test
//    public void testMain() {
//        System.out.println("main");
//        String[] args = null;
//        NGramLMImpl.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
