/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
//import ml.options.OptionSet;
//import ml.options.Options;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.table.SanchayTableModel;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class TextStats {
    
    protected SSFStory text;
    
    public TextStats(String textPath, String cs)
    {
        text = new SSFStoryImpl();
        try {
            text.readFile(textPath, cs);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextStats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextStats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TextStats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the text
     */
    public SSFStory getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(SSFStory text) {
        this.text = text;
    }

    public double getSentenceLengthStdDev()
    {
        double sd = 0.0;
        double m = text.getAvgSentenceLength();
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            double length = sen.getRoot().getAllLeaves().size();
            
            sd += (length - m) * (length - m);
        }
        
        sd /= (double) count;
        
        sd = Math.sqrt(sd);
        
        return sd;
    }

    public double getTokenLengthStdDev()
    {
        double sd = 0.0;
        double m = text.getAvgTokenLength();
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            List<SanchayMutableTreeNode> leaves = sen.getRoot().getAllLeaves();
            
            int lcount = leaves.size();
            
            for (int j = 0; j < lcount; j++) {
            
                double length = ((SSFLexItem) leaves.get(j)).getLexData().length();

                sd += (length - m) * (length - m);                
            }
        }
        
        sd /= (double) count;
        
        sd = Math.sqrt(sd);
        
        return sd;
    }

    public double getTextEntropyLexical()
    {
        double entropy = 0.0;
        
        LinkedHashMap<String, Integer> wordFreq = text.getWordFreq();

        int wcount = UtilityFunctions.getTotalValue(wordFreq);
        
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            String word = entry.getKey();
            Integer freq = entry.getValue();
            double prob =  (double) freq / (double) wcount;
            
            entropy += prob * Math.log(prob);
        }
        
        return -1 * entropy;
    }

    public double getAvgSentenceEntropyLexical()
    {
        double entropy = 0.0;
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            entropy += sen.getEntropyLexical();
        }
        
        entropy /= (double) count;
        
        return entropy;
    }

    public double getSentenceEntropyLexicalStdDev()
    {
        double sd = 0.0;
        double m = getAvgSentenceEntropyLexical();
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            double entropy = sen.getEntropyLexical();
            
            sd += (entropy - m) * (entropy - m);
        }
        
        sd /= (double) count;

        sd = Math.sqrt(sd);
        
        return sd;
    }

    public double getTextEntropyPOS()
    {
        double entropy = 0.0;
        
        LinkedHashMap<String, Integer> posTagFreq = text.getPOSTagFreq();
        
        int pcount = UtilityFunctions.getTotalValue(posTagFreq);
        
        System.out.println("POS tags: " + posTagFreq.size());
        System.out.println("Total POS tag freq: " + pcount);
        
        for (Map.Entry<String, Integer> entry : posTagFreq.entrySet()) {
            String pos = entry.getKey();
            Integer freq = entry.getValue();
            double prob =  (double) freq / (double) pcount;

            System.out.println("Freq of tag " + pos + ": " + freq);
            System.out.println("Prob of tag " + pos + ": " + prob);
            
            entropy += prob * Math.log(prob);
        }
        
        return -1 * entropy;
    }

    public double getAvgSentenceEntropyPOS()
    {
        double entropy = 0.0;
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            System.out.println("POS entropy, sentence " + (i + 1) + " : " + sen.getEntropyPOS());
            
            entropy += sen.getEntropyPOS();
        }
        
        entropy /= (double) count;
        
        return entropy;
    }

    public double getSentenceEntropyPOSStdDev()
    {
        double sd = 0.0;
        double m = getAvgSentenceEntropyPOS();
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            double entropy = sen.getEntropyPOS();
            
            sd += (entropy - m) * (entropy - m);
        }
        
        sd /= (double) count;

        sd = Math.sqrt(sd);
        
        return sd;
    }

    public double getTextEntropyLexicalPOS()
    {
        double entropy = 0.0;
        
        LinkedHashMap<String, Integer> wordTagFreq = text.getWordTagPairFreq();
        
        int wtcount = UtilityFunctions.getTotalValue(wordTagFreq);
        
        for (Map.Entry<String, Integer> entry : wordTagFreq.entrySet()) {
            String pos = entry.getKey();
            Integer freq = entry.getValue();
            double prob =  (double) freq / (double) wtcount;
            
            entropy += prob * Math.log(prob);
        }
        
        return -1 * entropy;
    }

    public double getAvgSentenceEntropyLexicalPOS()
    {
        double entropy = 0.0;
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            entropy += sen.getEntropyLexicalPOS();
        }
        
        entropy /= (double) count;
        
        return entropy;
    }

    public double getSentenceEntropyLexicalPOSStdDev()
    {
        double sd = 0.0;
        double m = getAvgSentenceEntropyLexicalPOS();
        
        int count = text.countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = text.getSentence(i);
            
            double entropy = sen.getEntropyLexicalPOS();
            
            sd += (entropy - m) * (entropy - m);
        }
        
        sd /= (double) count;

        sd = Math.sqrt(sd);
        
        return sd;
    }

    public double getTextScore(String scoreFile, String cs)
    {
        double entropy = 0.0;
        
        return entropy;
    }

    public double getAvgSentenceScore(String scoreFile, String cs)
    {
        double entropy = 0.0;
        
        return entropy;
    }

    public static List<String> getAllScoreNames()
    {
        List<String> scoreNames = new ArrayList<String>();
        
        scoreNames.add("SentenceCount");
        scoreNames.add("TokenCount");
        scoreNames.add("TypeCount");
        scoreNames.add("AvgSentenceLength");
        scoreNames.add("SentenceLengthStdDev");
        scoreNames.add("AvgTokenLength");
        scoreNames.add("TokenLengthStdDev");
//        scoreNames.add("AvgNounCount");
//        scoreNames.add("AvgModifierCount");
//        scoreNames.add("AvgVerbCount");
//        scoreNames.add("AvgPrepCount");
//        scoreNames.add("AvgFnWrdCount");
//        scoreNames.add("AvgWHWrdCount");
//        scoreNames.add("AvgNumWrdCount");
//        scoreNames.add("AvgNounCount");
//        scoreNames.add("AvgNounCount");
        scoreNames.add("TextEntropy");
        scoreNames.add("AvgSentenceEntropy");
        scoreNames.add("SentenceEntropyStdDev");
        scoreNames.add("TextPOSEntropy");
        scoreNames.add("AvgPOSSentenceEntropy");
        scoreNames.add("POSSentenceEntropyStdDev");
        scoreNames.add("TextLexicalPOSEntropy");
        scoreNames.add("AvgLexicalPOSSentenceEntropy");
        scoreNames.add("LexicalPOSSentenceEntropyStdDev");
        
        return scoreNames;
        
    }

    public List getAllScores()
    {
        DecimalFormat dc = new DecimalFormat("0.0000");
        
        List scores = new ArrayList();
        
        int sentenceCount = text.countSentences();
        
        scores.add(sentenceCount);
        
        int tokenCount = text.countWords();
        
        scores.add(tokenCount);
        
        LinkedHashMap<String, Integer> wordFreq = text.getWordFreq();
        
        scores.add(wordFreq.size());

        scores.add(dc.format(text.getAvgSentenceLength()));
        scores.add(dc.format(getSentenceLengthStdDev()));
        scores.add(dc.format(text.getAvgTokenLength()));
        scores.add(dc.format(getTokenLengthStdDev()));
        
        scores.add(dc.format(getTextEntropyLexical()));
        scores.add(dc.format(getAvgSentenceEntropyLexical()));
        scores.add(dc.format(getSentenceEntropyLexicalStdDev()));
        
        scores.add(dc.format(getTextEntropyPOS()));
        scores.add(dc.format(getAvgSentenceEntropyPOS()));
        scores.add(dc.format(getSentenceEntropyPOSStdDev()));
        
        scores.add(dc.format(getTextEntropyLexicalPOS()));
        scores.add(dc.format(getAvgSentenceEntropyLexicalPOS()));
        scores.add(dc.format(getSentenceEntropyLexicalPOSStdDev()));
        
        return scores;
    }
    
    public void calculateScores(String outFilePath, String cs)
    {
    
    }
    
    public void saveScores(String outFilePath, String cs)
    {
//        TextStats textStats = new TextStats();
    
    }
    
    public static void calculateScores(List<String> inFilePaths,
            String outFilePath, String cs) throws FileNotFoundException, UnsupportedEncodingException
    {
        SanchayTableModel scoresTable =  new SanchayTableModel(17, 5);

        List<String> scoreNames = getAllScoreNames();

        int i = 0;
        for (String scoreName : scoreNames) {

            scoresTable.setValueAt(scoreName, ++i, 0);
        }
//
//        ps.println();
        
        int count = inFilePaths.size();
        
        for (i = 0; i < count; i++) {
            String path = inFilePaths.get(i);
            
            scoresTable.setValueAt(path, 0, (i + 1));
            
            TextStats textStats = new TextStats(path, cs);
            
            List<Object> scores = textStats.getAllScores();
            
            int j = 0;
            
            for (Object score : scores) {
                scoresTable.setValueAt(score, ++j, (i + 1));
            }
        }
        
        scoresTable.save(outFilePath, cs);
    }
    
    public static void main(String args[])
    {
//        Options opt = new Options(args, 2);
//        
//        opt.addSet("bset", 1, 100).addOption("b").addOption("c", Options.Multiplicity.ZERO_OR_ONE);
//
//        opt.addOptionAllSets("v", Options.Multiplicity.ZERO_OR_ONE);
//
//        opt.getSet("bset").addOption("e", Options.Separator.EQUALS, Options.Multiplicity.ZERO_OR_ONE);
//        opt.getSet("bset").addOption("o", Options.Separator.EQUALS, Options.Multiplicity.ZERO_OR_ONE);
//
//        String cs = "UTF-8";
//        String outFilePath = "";
//
//        List<String> inFilePaths = null;
//        
//        OptionSet set = opt.getMatchingSet();        
//
//        if(set.getSetName().equals("bset"))
//        {
//            if (set.isSet("e")) {
//              // React to option -a
////                System.out.println("e is set");
//                cs = set.getOption("e").getResultValue(0);
//            }
//
//            if (set.isSet("o")) {
//              // React to option -a
////                System.out.println("o is set");
//                outFilePath = set.getOption("o").getResultValue(0);
//            }
//
//            inFilePaths = set.getData();
//        }
//        
//        for (String string : inFilePaths) {
//            System.out.println("File: " + string);
//        }
//      
//        try {
//            calculateScores(inFilePaths, outFilePath, cs);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(TextStats.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(TextStats.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
