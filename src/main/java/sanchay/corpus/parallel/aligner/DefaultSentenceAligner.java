/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel.aligner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.corpus.parallel.APCProperties;
import sanchay.corpus.simple.SimpleCorpus;
import sanchay.corpus.simple.SimpleSentence;
import sanchay.corpus.simple.impl.SimpleCorpusImpl;
import sanchay.corpus.ssf.SSFStory;
import sanchay.mlearning.mt.TranslationCandidates;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.speech.common.StringNode;
import sanchay.speech.common.TrellisString;
import sanchay.speech.decoder.isolated.IsolatedTrellisAligner;
import sanchay.table.SanchayTableModel;

/**
 *
 * @author anil
 */
public class DefaultSentenceAligner implements SentenceAligner {

    protected String srcCharset = "UTF-8";
    protected String tgtCharset = "UTF-8";
    protected String outCharset = "UTF-8";

    protected String srcCorpusPath = GlobalProperties.resolveRelativePath("data/parallel-corpus/eng-2.txt");
    protected String tgtCorpusPath = GlobalProperties.resolveRelativePath("data/parallel-corpus/hin-2.txt");

    protected String alignmentsOutputPath = GlobalProperties.resolveRelativePath("data/parallel-corpus/sen-alignments-2.txt");

    protected PropertyTokens srcTextPT;
    protected PropertyTokens tgtTextPT;

    protected KeyValueProperties alignmentsKVP;

    protected SimpleCorpus srcCorpus;
    protected SimpleCorpus tgtCorpus;

    protected APCProperties apcProperties;

    protected TranslationCandidates alignmentCandidates;

    protected IsolatedTrellisAligner isolatedTrellisAligner;

    protected int pruneSize = 50;

    protected int mode = 0;

    public DefaultSentenceAligner()
    {
        isolatedTrellisAligner = new IsolatedTrellisAligner();
    }

    /**
     * @return the srcCharset
     */
    @Override
    public String getSrcCharset()
    {
        return srcCharset;
    }

    /**
     * @param srcCharset the srcCharset to set
     */
    @Override
    public void setSrcCharset(String srcCharset)
    {
        this.srcCharset = srcCharset;
    }

    /**
     * @return the tgtCharset
     */
    @Override
    public String getTgtCharset()
    {
        return tgtCharset;
    }

    /**
     * @param tgtCharset the tgtCharset to set
     */
    @Override
    public void setTgtCharset(String tgtCharset)
    {
        this.tgtCharset = tgtCharset;
    }

    /**
     * @return the outCharset
     */
    @Override
    public String getOutCharset()
    {
        return outCharset;
    }

    /**
     * @param outCharset the outCharset to set
     */
    @Override
    public void setOutCharset(String outCharset)
    {
        this.outCharset = outCharset;
    }

    /**
     * @return the srcCorpusPath
     */
    @Override
    public String getSrcCorpusPath()
    {
        return srcCorpusPath;
    }

    /**
     * @param srcCorpusPath the srcCorpusPath to set
     */
    @Override
    public void setSrcCorpusPath(String srcCorpusPath)
    {
        this.srcCorpusPath = srcCorpusPath;
    }

    /**
     * @return the tgtCorpusPath
     */
    @Override
    public String getTgtCorpusPath()
    {
        return tgtCorpusPath;
    }

    /**
     * @param tgtCorpusPath the tgtCorpusPath to set
     */
    @Override
    public void setTgtCorpusPath(String tgtCorpusPath)
    {
        this.tgtCorpusPath = tgtCorpusPath;
    }

    /**
     * @return the alignmentsOutputPath
     */
    @Override
    public String getAlignmentsOutputPath()
    {
        return alignmentsOutputPath;
    }

    /**
     * @param alignmentsOutputPath the alignmentsOutputPath to set
     */
    @Override
    public void setAlignmentsOutputPath(String alignmentsOutputPath)
    {
        this.alignmentsOutputPath = alignmentsOutputPath;
    }

    /**
     * @return the srcTextPT
     */
    @Override
    public PropertyTokens getSrcTextPT()
    {
        return srcTextPT;
    }

    /**
     * @param srcTextPT the srcTextPT to set
     */
    @Override
    public void setSrcTextPT(PropertyTokens srcTextPT)
    {
        this.srcTextPT = srcTextPT;
    }

    /**
     * @return the tgtTextPT
     */
    @Override
    public PropertyTokens getTgtTextPT()
    {
        return tgtTextPT;
    }

    /**
     * @param tgtTextPT the tgtTextPT to set
     */
    @Override
    public void setTgtTextPT(PropertyTokens tgtTextPT)
    {
        this.tgtTextPT = tgtTextPT;
    }

    /**
     * @return the alignmentsKVP
     */
    @Override
    public KeyValueProperties getAlignmentsKVP()
    {
        return alignmentsKVP;
    }

    /**
     * @param alignmentsKVP the alignmentsKVP to set
     */
    @Override
    public void setAlignmentsKVP(KeyValueProperties alignmentsKVP)
    {
        this.alignmentsKVP = alignmentsKVP;
    }

    /**
     * @return the srcCorpus
     */
    @Override
    public SimpleCorpus getSrcCorpus()
    {
        return srcCorpus;
    }

    /**
     * @param srcCorpus the srcCorpus to set
     */
    @Override
    public void setSrcCorpus(SimpleCorpus srcCorpus)
    {
        this.srcCorpus = srcCorpus;
    }

    /**
     * @return the tgtCorpus
     */
    @Override
    public SimpleCorpus getTgtCorpus()
    {
        return tgtCorpus;
    }

    /**
     * @param tgtCorpus the tgtCorpus to set
     */
    @Override
    public void setTgtCorpus(SimpleCorpus tgtCorpus)
    {
        this.tgtCorpus = tgtCorpus;
    }

    /**
     * @return the alignmentCandidates
     */
    @Override
    public TranslationCandidates getAlignmentCandidates()
    {
        return alignmentCandidates;
    }

    /**
     * @param alignmentCandidates the alignmentCandidates to set
     */
    @Override
    public void setAlignmentCandidates(TranslationCandidates alignmentCandidates)
    {
        this.alignmentCandidates = alignmentCandidates;
    }

    /**
     * @return the pruneSize
     */
    public int getPruneSize()
    {
        return pruneSize;
    }

    /**
     * @param pruneSize the pruneSize to set
     */
    public void setPruneSize(int pruneSize)
    {
        this.pruneSize = pruneSize;
    }

    /**
     * @return the mode
     */
    @Override
    public int getMode()
    {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    @Override
    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public APCProperties getAPCProperties()
	{
		return apcProperties;
	}

	public void setAPCProperties(APCProperties apcprop)
	{
		apcProperties = apcprop;
	}

    @Override
    public void readCorpora() throws FileNotFoundException, IOException
    {
        srcCorpus = new SimpleCorpusImpl(GlobalProperties.getHomeDirectory() + "/workspace/minimal_src_propertymanager.txt", "UTF-8");
        tgtCorpus = new SimpleCorpusImpl(GlobalProperties.getHomeDirectory() + "/workspace/minimal_src_propertymanager.txt", "UTF-8");

        srcCorpus.read(new File(srcCorpusPath), srcCharset);
        tgtCorpus.read(new File(tgtCorpusPath), tgtCharset);

        apcProperties = new APCProperties(srcCorpusPath, tgtCorpusPath);

        apcProperties.readProperties(null);

        apcProperties.setSrcCorpus(srcCorpus);
        apcProperties.setTgtCorpus(tgtCorpus);

        apcProperties.setSurfaceSimilarityThreshold(2.5);
        apcProperties.setSurfaceSimilarityWordLengthRatio(0.75);
        apcProperties.setPhoneticModelOfScripts(isolatedTrellisAligner.getPhoneticModelOfScripts());
//        apcProperties.setTransliterationCandidatesGenerator(isolatedTrellisAligner.getTransliterationCandidatesGenerator());

        isolatedTrellisAligner.setAPCProperties(apcProperties);
        isolatedTrellisAligner.setWordTypeTables(srcCorpus.getWordTypeTable(), tgtCorpus.getWordTypeTable());
    }

    @Override
    public void readCorpora(String srcPath, String srcCS, String tgtPath, String tgtCS) throws FileNotFoundException, IOException
    {
        srcCorpusPath = srcPath;
        srcCharset = srcCS;
        tgtCorpusPath = tgtPath;
        tgtCharset = tgtCS;

        readCorpora();
    }

    @Override
    public void saveAlignments() throws FileNotFoundException, IOException
    {

    }

    @Override
    public void saveAlignments(String outPath, String outCS) throws FileNotFoundException, IOException
    {
        alignmentsOutputPath = outPath;

        saveAlignments();
    }

    @Override
    public void calculateCounts()
    {
        SanchayTableModel srcWTTable = srcCorpus.getWordTypeTable();
        SanchayTableModel tgtWTTable = tgtCorpus.getWordTypeTable();

        int scount = srcCorpus.countSentences();

        int srcCharCountMax = 0;
        int srcWrdCountMax = 0;
        int srcSignatureMax = 0;
        int srcWSLMax = 0;

        for (int i = 0; i < scount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) srcCorpus.getSentence(i);

            sentence.calculateSignature(srcWTTable);

            int charCount = sentence.getSentenceLength();

            if(charCount > srcCharCountMax)
                srcCharCountMax = charCount;

            int wrdCount = sentence.countWords();

            if(wrdCount > srcWrdCountMax)
                srcWrdCountMax = wrdCount;

            int signature = sentence.getSignature();

            if(signature > srcSignatureMax)
                srcSignatureMax = signature;
        }

        apcProperties.setSMaxCharcnt(srcCharCountMax);
        apcProperties.setSMaxWrdcnt(srcWrdCountMax);
        apcProperties.setSMaxSignature(srcSignatureMax);

        for (int i = 0; i < scount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) srcCorpus.getSentence(i);

            sentence.setWeightedLength(apcProperties, 's');
        }

        for (int i = 0; i < scount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) srcCorpus.getSentence(i);

            int wsl = sentence.getWeightedLength();

            if(wsl > srcWSLMax)
                srcWSLMax = wsl;
        }

        apcProperties.setSMaxWSL(srcWSLMax);

        int tcount = tgtCorpus.countSentences();

        int tgtCharCountMax = 0;
        int tgtWrdCountMax = 0;
        int tgtSignatureMax = 0;
        int tgtWSLMax = 0;

        for (int i = 0; i < tcount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) tgtCorpus.getSentence(i);

            sentence.calculateSignature(tgtWTTable);

            int charCount = sentence.getSentenceLength();

            if(charCount > tgtCharCountMax)
                tgtCharCountMax = charCount;

            int wrdCount = sentence.countWords();

            if(wrdCount > tgtWrdCountMax)
                tgtWrdCountMax = wrdCount;

            int signature = sentence.getSignature();

            if(signature > tgtSignatureMax)
                tgtSignatureMax = signature;

            int wsl = sentence.getWeightedLength();

            if(wsl > tgtWSLMax)
                tgtWSLMax = wsl;
        }

        apcProperties.setTMaxCharcnt(tgtCharCountMax);
        apcProperties.setTMaxWrdcnt(tgtWrdCountMax);
        apcProperties.setTMaxSignature(tgtSignatureMax);
        apcProperties.setTMaxWSL(tgtWSLMax);

        for (int i = 0; i < tcount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) tgtCorpus.getSentence(i);

            sentence.setWeightedLength(apcProperties, 't');
        }

        for (int i = 0; i < tcount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) tgtCorpus.getSentence(i);

            int wsl = sentence.getWeightedLength();

            if(wsl > tgtWSLMax)
                tgtWSLMax = wsl;
        }

        apcProperties.setTMaxWSL(tgtWSLMax);
    }

    protected void calculateMeans()
    {
        int scount = srcCorpus.countSentences();
        int tcount = tgtCorpus.countSentences();

        double srcCharCountSum = 0.0;
        double srcWrdCountSum = 0.0;
        double srcSignatureSum = 0.0;
        double srcWSLSum = 0.0;

        double tgtCharCountSum = 0.0;
        double tgtWrdCountSum = 0.0;
        double tgtSignatureSum = 0.0;
        double tgtWSLSum = 0.0;

        for (int i = 0; i < scount; i++)
        {
            SimpleSentence srcSentence = (SimpleSentence) srcCorpus.getSentence(i);

            int srcCharCount = srcSentence.getSentenceLength();
            int srcWrdCount = srcSentence.countWords();
            int srcSignature = srcSentence.getSignature();
            int srcWSL = srcSentence.getWeightedLength();

            srcCharCountSum += srcCharCount;
            srcWrdCountSum += srcWrdCount;
            srcSignatureSum += srcSignature;
            srcWSLSum += srcWSL;
        }

        for (int i = 0; i < tcount; i++)
        {
            SimpleSentence tgtSentence = (SimpleSentence) tgtCorpus.getSentence(i);

            int tgtCharCount = tgtSentence.getSentenceLength();
            int tgtWrdCount = tgtSentence.countWords();
            int tgtSignature = tgtSentence.getSignature();
            int tgtWSL = tgtSentence.getWeightedLength();

            tgtCharCountSum += tgtCharCount;
            tgtWrdCountSum += tgtWrdCount;
            tgtSignatureSum += tgtSignature;
            tgtWSLSum += tgtWSL;
        }

        double srcCharCountMean = srcCharCountSum / (double) srcCorpus.countSentences();
        double srcWrdCountMean = srcWrdCountSum / (double) srcCorpus.countSentences();
        double srcSignatureMean = srcSignatureSum / (double) srcCorpus.countSentences();
        double srcWSLMean = srcWSLSum / (double) srcCorpus.countSentences();

        double tgtCharCountMean = tgtCharCountSum / (double) tgtCorpus.countSentences();
        double tgtWrdCountMean = tgtWrdCountSum / (double) tgtCorpus.countSentences();
        double tgtSignatureMean = tgtSignatureSum / (double) tgtCorpus.countSentences();
        double tgtWSLMean = tgtWSLSum / (double) tgtCorpus.countSentences();

        apcProperties.setMeanCharcntRatio(tgtCharCountMean / srcCharCountMean);
        apcProperties.setMeanWrdcntRatio(tgtWrdCountMean / srcWrdCountMean);
        apcProperties.setMeanSignatureRatio(tgtSignatureMean / srcSignatureMean);
        apcProperties.setMeanWSLRatio(tgtWSLMean / srcWSLMean);
    }

    public void prepareTrellisAligner()
    {
        int scount = srcCorpus.countSentences();

        TrellisString srcTrellisString = new TrellisString();

        for (int i = 0; i < scount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) srcCorpus.getSentence(i);

            AlignmentFeature feature = new AlignmentFeature();

            feature.setFeatures(sentence);
            
            feature.setAPCProperties(apcProperties);

            StringNode stringNode = new StringNode(feature);

            feature.setIndex(i);
            stringNode.setIndex(i);

            srcTrellisString.addNode(stringNode);
        }

        isolatedTrellisAligner.setSrcTrellisString(srcTrellisString);

        int tcount = tgtCorpus.countSentences();

        TrellisString tgtTrellisString = new TrellisString();

        for (int i = 0; i < tcount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) tgtCorpus.getSentence(i);

            AlignmentFeature feature = new AlignmentFeature();

            feature.setFeatures(sentence);

            feature.setAPCProperties(apcProperties);

            StringNode stringNode = new StringNode(feature);

            feature.setIndex(i);
            stringNode.setIndex(i);

            tgtTrellisString.addNode(stringNode);
        }

        isolatedTrellisAligner.setTgtTrellisString(tgtTrellisString);
    }

    @Override
    public void printCounts(PrintStream ps)
    {
        SanchayTableModel srcWTTable = srcCorpus.getWordTypeTable();
        SanchayTableModel tgtWTTable = tgtCorpus.getWordTypeTable();

        int scount = srcCorpus.countSentences();

        for (int i = 0; i < scount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) srcCorpus.getSentence(i);
            sentence.printCounts(srcWTTable, ps);
        }

        ps.println("Src Max Character Count: " + apcProperties.getSMaxCharcnt());
        ps.println("Src Max Word Count: " + apcProperties.getSMaxWrdcnt());
        ps.println("Src Max Signature: " + apcProperties.getSMaxSignature());

        int tcount = tgtCorpus.countSentences();

        for (int i = 0; i < tcount; i++)
        {
            SimpleSentence sentence = (SimpleSentence) tgtCorpus.getSentence(i);
            sentence.printCounts(tgtWTTable, ps);
        }

        ps.println("Tgt Max Character Count: " + apcProperties.getTMaxCharcnt());
        ps.println("Tgt Max Word Count: " + apcProperties.getTMaxWrdcnt());
        ps.println("Tgt Max Signature: " + apcProperties.getTMaxSignature());
    }

    @Override
    public void calculateScores()
    {
        isolatedTrellisAligner.calculateScores();
    }

    @Override
    public void alignAll()
    {
        isolatedTrellisAligner.alignAll();
    }

    public static double getSentencePairScore(SimpleSentence srcSen, SimpleSentence tgtSen)
    {
        return 0.0;
    }

    public void printAlignments(PrintStream ps)
    {
        isolatedTrellisAligner.printAlignments(ps);
    }

    public void alignSSFStories(SSFStory srcStory, SSFStory tgtStory)
    {
        srcCorpusPath = srcStory.getSSFFile() + ".raw.txt";
        tgtCorpusPath = tgtStory.getSSFFile() + ".raw.txt";

        srcCharset = srcStory.getCharset();
        tgtCharset = tgtStory.getCharset();

        try
        {
            srcStory.saveRawText(srcCorpusPath, srcCharset);
            tgtStory.saveRawText(tgtCorpusPath, tgtCharset);
            
            readCorpora();

            calculateCounts();
            calculateMeans();
            calculateScores();

            prepareTrellisAligner();

            alignAll();

            printAlignments(System.out);

            isolatedTrellisAligner.markAlignments(srcStory, tgtStory);

        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(DefaultSentenceAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(DefaultSentenceAligner.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }

    public static void main(String args[])
	{
        DefaultSentenceAligner aligner = new DefaultSentenceAligner();
        
        try
        {
            aligner.readCorpora();

//            aligner.getSrcCorpus().print(System.out);
//            aligner.getTgtCorpus().print(System.out);

            aligner.calculateCounts();
            aligner.calculateMeans();
            aligner.calculateScores();

//            aligner.printCounts(System.out);

            aligner.prepareTrellisAligner();

            aligner.alignAll();

            aligner.printAlignments(System.out);
            
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(DefaultSentenceAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(DefaultSentenceAligner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
