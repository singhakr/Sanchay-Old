/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.speech.decoder.isolated;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.corpus.parallel.APCProperties;
import sanchay.corpus.ssf.SSFStory;
import sanchay.matrix.SparseMatrixDouble;
import sanchay.mlearning.mt.NumberTranslator;
import sanchay.mlearning.mt.SMTTransliterator;
import sanchay.speech.common.TrellisString;
import sanchay.speech.common.TwoByTwoBead;
import sanchay.table.SanchayTableModel;
import sanchay.text.DictionaryFSTNode;
import sanchay.text.spell.DictionaryFSTExt;
import sanchay.text.spell.PhoneticModelOfScripts;
import sanchay.util.MathUtilFunctions;

/**
 *
 * @author anil
 */
public class IsolatedTrellisAligner {

	protected TrellisString srcString;
	protected TrellisString tgtString;

    protected SanchayTableModel srcWTTable;
    protected SanchayTableModel tgtWTTable;

    protected SparseMatrixDouble alignmentCosts;
    
    protected SparseMatrixDouble pathx;
    protected SparseMatrixDouble pathy;

    protected SparseMatrixDouble lengthScores;
    protected SparseMatrixDouble surfaceMatchCounts;

    protected Vector<TwoByTwoBead> alignments;

    protected APCProperties apcProperties;

//    protected TransliterationCandidateGenerator transliterationCandidatesGenerator;
//    protected TransliteratorMain transliteratorMain;
    protected SMTTransliterator transliterator;
    protected NumberTranslator numberTranslator;
    protected PhoneticModelOfScripts phoneticModelOfScripts;
//    protected TextNormalizer textNormalizer;
    protected DictionaryFSTExt dictionaryFST;

    protected int beamSize = 30;

    protected int pruneSize = 50;

    public IsolatedTrellisAligner()
	{
        loadPhoneticModelOfScripts();

//        try
//        {
//            textNormalizer = new TextNormalizer(GlobalProperties.getIntlString("hin::utf8"), GlobalProperties.getIntlString("UTF-8"), "", "", false);
            numberTranslator = new NumberTranslator("eng::utf8", "UTF-8", "hin::utf8", "UTF-8");
//            transliterationCandidatesGenerator = new TransliterationCandidateGenerator(textNormalizer);
//
//            transliterationCandidatesGenerator.loadLM();
//            transliterationCandidatesGenerator.readMappings();
//            transliterationCandidatesGenerator.loadSpeechDict();
//        } catch (FileNotFoundException ex)
//        {
//            Logger.getLogger(IsolatedTrellisAligner.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex)
//        {
//            Logger.getLogger(IsolatedTrellisAligner.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex)
//        {
//            Logger.getLogger(IsolatedTrellisAligner.class.getName()).log(Level.SEVERE, null, ex);
//        }

//        TranslationCandidates candidates = transliterationCandidatesGenerator.getTransliterationCandidates("book", true, false);
//        candidates.printTranslationCandidates(System.out, false);
    }

	public IsolatedTrellisAligner(SanchayTableModel srcWTTable, SanchayTableModel tgtWTTable)
	{
        this();

        setWordTypeTables(srcWTTable, tgtWTTable);
	}

    public void setWordTypeTables(SanchayTableModel srcWTTable, SanchayTableModel tgtWTTable)
    {
        this.srcWTTable = srcWTTable;
        this.tgtWTTable = tgtWTTable;
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
     * @return the beamSize
     */
    public int getBeamSize()
    {
        return beamSize;
    }

    /**
     * @param beamSize the beamSize to set
     */
    public void setBeamSize(int beamSize)
    {
        this.beamSize = beamSize;
    }

    public TrellisString getSrcTrellisString()
    {
        return srcString;
    }

    public void setSrcTrellisString(TrellisString ts)
    {
        srcString = ts;
    }

    public TrellisString getTgtTrellisString()
    {
        return tgtString;
    }

    public void setTgtTrellisString(TrellisString ts)
    {
        tgtString = ts;
    }

    public APCProperties getAPCProperties()
	{
        return apcProperties;
    }

    public void setAPCProperties(APCProperties apcProps)
	{
        apcProperties = apcProps;
    }

//    /**
//     * @return the transliterationCandidatesGenerator
//     */
//    public TransliterationCandidateGenerator getTransliterationCandidatesGenerator()
//    {
//        return transliterationCandidatesGenerator;
//    }

    /**
     * @return the phoneticModelOfScripts
     */
    public PhoneticModelOfScripts getPhoneticModelOfScripts()
    {
        return phoneticModelOfScripts;
    }

    public void loadPhoneticModelOfScripts()
    {
        try {
            phoneticModelOfScripts = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"), GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void calculateScores()
    {
        calculateLengthScores();
        findSurfaceMatches();
    }

    protected void calculateLengthScores()
    {        
        int maxSrcLength = apcProperties.getSMaxWrdcnt();
        int maxTgtLength = apcProperties.getTMaxWrdcnt();

        int lengthLimit = maxSrcLength + maxTgtLength + 3;

        lengthScores = new SparseMatrixDouble(lengthLimit, lengthLimit, 0.0);

        double r = apcProperties.getMeanWrdcntRatio();

        double mean = 0.0;
        double logNumer = 0.0;
        double logDenom = 0.0;
        double pLogTgtGivenSrc = 0.0;

        double distance = 0.0;

        for (int i = 0; i < lengthLimit; i++)
        {
            for (int j = 0; j < lengthLimit; j++)
            {
                mean = i * r;
                logNumer = Math.log(mean) * j;
                logDenom = (double) MathUtilFunctions.logFactorial(j);

                pLogTgtGivenSrc = logNumer - logDenom - mean;

                distance = Math.exp(pLogTgtGivenSrc);

                if(distance == 0.0 || Double.isNaN(distance))
                    lengthScores.put(i, j, Double.MAX_VALUE);
                else
                {
                    distance = 1.0/distance;
                    lengthScores.put(i, j, distance);
                }
            }
        }

        apcProperties.setLengthScores(lengthScores);
    }

    protected void findSurfaceMatches()
    {
        int srcWrdTypeCount = srcWTTable.getRowCount();
        int tgtWrdTypeCount = tgtWTTable.getRowCount();

        LinkedHashMap<String, Long> words = new LinkedHashMap<String, Long>();

        for (int i = 0; i < tgtWrdTypeCount; i++)
        {
            words.put((String) tgtWTTable.getValueAt(i, 0), new Long(1));
        }

        try
        {
            dictionaryFST = new DictionaryFSTExt();

//            dictionaryFST.compileAkshar(words, null);
            dictionaryFST.compile(words, null);

            dictionaryFST.setTotalScoreCutoff(50.0f);
            dictionaryFST.setScaledScoreCutoff(20.0f);
            dictionaryFST.setPhoneticModelOfScripts(phoneticModelOfScripts);
//
//            transliteratorMain = new TransliteratorMain(textNormalizer, dictionaryFST);
            transliterator = new SMTTransliterator("-s", "eng::utf8", "hin::utf8", null, true, false, null);

            surfaceMatchCounts = new SparseMatrixDouble(srcWrdTypeCount, tgtWrdTypeCount, 0.0);

//            for (int i = 0; i < srcWrdTypeCount; i++)
//            {
//                String srcWord = (String) srcWTTable.getValueAt(i, 0);
//
//                boolean exactMatch = false;
//
//                for (int j = 0; j < tgtWrdTypeCount; j++)
//                {
//                    String tgtWord = (String) tgtWTTable.getValueAt(j, 0);
//
//                    if(srcWord.equals(tgtWord))
//                    {
//                        double mcount = surfaceMatchCounts.get(i, j);
//
//                        if(mcount == 0.0)
//                            surfaceMatchCounts.put(i, j, 1.0);
//                        else
//                            surfaceMatchCounts.put(i, j, mcount + 1.0);
//
//                        exactMatch = true;
//                        break;
//                    }
//                    else
//                    {
//                        String translatedNumber = numberTranslator.translateNumberWord(srcWord);
//
//                        if(translatedNumber != null)
//                        {
//                            if(translatedNumber.equals(tgtWord))
//                            {
//                                double mcount = surfaceMatchCounts.get(i, j);
//
//                                if(mcount == 0.0)
//                                    surfaceMatchCounts.put(i, j, 1.0);
//                                else
//                                    surfaceMatchCounts.put(i, j, mcount + 1.0);
//
//                                exactMatch = true;
//                                break;
//                            }
//                        }
//                    }
//                }
//
//                if(exactMatch == false && srcWord.contains("\"") == false
//                         && srcWord.contains("(") == false && srcWord.contains(")") == false
//                         && srcWord.contains("[") == false && srcWord.contains("]") == false
//                         && srcWord.contains("{") == false && srcWord.contains("}") == false)
//                {
////                    TranslationCandidates candidates = transliteratorMain.transliterateNew(srcWord);
//
////                    String transliteration = candidates.getFirstCandidate();
//                    String transliteration = transliterator.transliterate(srcWord);
//
//                    for (int j = 0; j < tgtWrdTypeCount; j++)
//                    {
//                        String tgtWord = (String) tgtWTTable.getValueAt(j, 0);
//
//                        if(transliteration.length() > 2 && tgtWord.length() > 2)
//                        {
////                            double distance = phoneticModelOfScripts.getSymmetricScaledSurfaceSimilarity(tgtWord, transliteration);
//                            LinkedHashMap<DictionaryFSTNode,Double> nearestMatches = dictionaryFST.getNearestWords(transliteration, 20, false);
//
//                            if(!nearestMatches.isEmpty())
//                            {
//                                Iterator<DictionaryFSTNode> itr = nearestMatches.keySet().iterator();
//
//                                DictionaryFSTNode node = itr.next();
//
//                                double distance = nearestMatches.get(node).doubleValue();
//
//                                if(distance < apcProperties.getSurfaceSimilarityThreshold())
//                                {
//                                    double mcount = surfaceMatchCounts.get(i, j);
//
//                                    if(mcount == 0.0)
//                                        surfaceMatchCounts.put(i, j, 1.0);
//                                    else
//                                        surfaceMatchCounts.put(i, j, mcount + 1.0);
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            apcProperties.setSurfaceMatchCounts(surfaceMatchCounts);
           
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(IsolatedTrellisAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(IsolatedTrellisAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(IsolatedTrellisAligner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int alignAll()
	{
        int scount = srcString.countNodes();
        int tcount = tgtString.countNodes();

        int N = Math.max(scount, tcount) + 1;

        alignmentCosts = new SparseMatrixDouble(N, N, 0.0);
        
        pathx = new SparseMatrixDouble(N, N, 0.0);
        pathy = new SparseMatrixDouble(N, N, 0.0);

        double subCost, delCost, insCost, conCost, expCost, beaCost, minCost;

        for (int j = 0; j <= tcount; j++)
        {
            for (int i = Math.max(0, j - beamSize); i <= Math.min(scount, j + beamSize); i++)
//            for (int i = 0; i <= scount; i++)
            {
                if(i > 0 && j > 0)
                {
                    TwoByTwoBead subBead = new TwoByTwoBead();

                    subBead.srcNodeOne = srcString.getNode(i - 1);
                    subBead.srcNodeTwo = null;
                    subBead.tgtNodeOne = tgtString.getNode(j - 1);
                    subBead.tgtNodeTwo = null;

                    TwoByTwoBead.getSubstitutionCost(subBead);

                    subCost = alignmentCosts.get(i - 1, j - 1) + subBead.alignmentCost;
                }
                else
                    subCost = Double.MAX_VALUE;

                if(i > 0)
                {
                    TwoByTwoBead delBead = new TwoByTwoBead();

                    delBead.srcNodeOne = srcString.getNode(i - 1);
                    delBead.srcNodeTwo = null;
                    delBead.tgtNodeOne = null;
                    delBead.tgtNodeTwo = null;

                    TwoByTwoBead.getDeletionCost(delBead);

                    delCost = alignmentCosts.get(i - 1, j) + delBead.alignmentCost;
                }
                else
                    delCost = Double.MAX_VALUE;

                if(j > 0)
                {
                    TwoByTwoBead insBead = new TwoByTwoBead();

                    insBead.srcNodeOne = null;
                    insBead.srcNodeTwo = null;
                    insBead.tgtNodeOne = tgtString.getNode(j - 1);
                    insBead.tgtNodeTwo = null;

                    TwoByTwoBead.getInsertionCost(insBead);

                    insCost = alignmentCosts.get(i, j - 1) + insBead.alignmentCost;
                }
                else
                    insCost = Double.MAX_VALUE;

                if(i > 1 && j > 0)
                {
                    TwoByTwoBead conBead = new TwoByTwoBead();

                    conBead.srcNodeOne = srcString.getNode(i - 2);
                    conBead.srcNodeTwo = srcString.getNode(i - 1);
                    conBead.tgtNodeOne = tgtString.getNode(j - 1);
                    conBead.tgtNodeTwo = null;

                    TwoByTwoBead.getContractionCost(conBead);

                    conCost = alignmentCosts.get(i - 2, j - 1) + conBead.alignmentCost;
                }
                else
                    conCost = Double.MAX_VALUE;

                if(i > 0 && j > 1)
                {
                    TwoByTwoBead expBead = new TwoByTwoBead();

                    expBead.srcNodeOne = srcString.getNode(i - 1);
                    expBead.srcNodeTwo = null;
                    expBead.tgtNodeOne = tgtString.getNode(j - 2);
                    expBead.tgtNodeTwo = tgtString.getNode(j - 1);

                    TwoByTwoBead.getExpansionCost(expBead);

                    expCost = alignmentCosts.get(i - 1, j - 2) + expBead.alignmentCost;
                }
                else
                    expCost = Double.MAX_VALUE;

                if(i > 1 && j > 1)
                {
                    TwoByTwoBead beaBead = new TwoByTwoBead();

                    beaBead.srcNodeOne = srcString.getNode(i - 2);
                    beaBead.srcNodeTwo = srcString.getNode(i - 1);
                    beaBead.tgtNodeOne = tgtString.getNode(j - 2);
                    beaBead.tgtNodeTwo = tgtString.getNode(j - 1);

                    TwoByTwoBead.getBeadMatchCost(beaBead);

                    beaCost = alignmentCosts.get(i - 2, j - 2) + beaBead.alignmentCost;
                }
                else
                    beaCost = Double.MAX_VALUE;

                minCost = subCost;

                if(delCost < minCost)
                    minCost = delCost;

                if(insCost < minCost)
                    minCost = insCost;

                if(conCost < minCost)
                    minCost = conCost;

                if(expCost < minCost)
                    minCost = expCost;

                if(beaCost < minCost)
                    minCost = beaCost;

                if(minCost == Double.MAX_VALUE)
                    alignmentCosts.put(i, j, 0.0);
                else if(minCost == subCost)
                {
                    alignmentCosts.put(i, j, subCost);
                    pathx.put(i, j, i - 1);
                    pathy.put(i, j, j - 1);
                }
                else if(minCost == delCost)
                {
                    alignmentCosts.put(i, j, delCost);
                    pathx.put(i, j, i - 1);
                    pathy.put(i, j, j);
                }
                else if(minCost == insCost)
                {
                    alignmentCosts.put(i, j, insCost);
                    pathx.put(i, j, i);
                    pathy.put(i, j, j - 1);
                }
                else if(minCost == conCost)
                {
                    alignmentCosts.put(i, j, conCost);
                    pathx.put(i, j, i - 2);
                    pathy.put(i, j, j - 1);
                }
                else if(minCost == expCost)
                {
                    alignmentCosts.put(i, j, expCost);
                    pathx.put(i, j, i - 1);
                    pathy.put(i, j, j - 2);
                }
                else if(minCost == beaCost)
                {
                    alignmentCosts.put(i, j, beaCost);
                    pathx.put(i, j, i - 2);
                    pathy.put(i, j, j - 2);
                }
            }
        }

        alignments = new Vector<TwoByTwoBead>(N);

        int oi = 0, oj = 0, di = 0, dj = 0;

        for(int i = scount, j = tcount; i > 0 || j > 0; i = oi, j = oj)
        {
            oi = (int) pathx.get(i, j);
            oj = (int) pathy.get(i, j);
            di = i - oi;
            dj = j - oj;

            if(di == 1 && dj == 1)
            {
                TwoByTwoBead subBead = new TwoByTwoBead();

                subBead.srcNodeOne = srcString.getNode(i - 1);
                subBead.srcNodeTwo = null;
                subBead.tgtNodeOne = tgtString.getNode(j - 1);
                subBead.tgtNodeTwo = null;
                subBead.alignmentCost = alignmentCosts.get(i, j) - alignmentCosts.get(i - 1, j - 1);

                alignments.add(subBead);
            }
            else if(di == 1 && dj == 0)
            {
                TwoByTwoBead delBead = new TwoByTwoBead();

                delBead.srcNodeOne = srcString.getNode(i - 1);
                delBead.srcNodeTwo = null;
                delBead.tgtNodeOne = null;
                delBead.tgtNodeTwo = null;
                delBead.alignmentCost = alignmentCosts.get(i, j) - alignmentCosts.get(i - 1, j);

                alignments.add(delBead);
            }
            else if(di == 0 && dj == 1)
            {
                TwoByTwoBead insBead = new TwoByTwoBead();

                insBead.srcNodeOne = null;
                insBead.srcNodeTwo = null;
                insBead.tgtNodeOne = tgtString.getNode(j - 1);
                insBead.tgtNodeTwo = null;
                insBead.alignmentCost = alignmentCosts.get(i, j) - alignmentCosts.get(i, j - 1);

                alignments.add(insBead);
            }
            else if(dj == 1)
            {
                TwoByTwoBead conBead = new TwoByTwoBead();

                conBead.srcNodeOne = srcString.getNode(i - 2);
                conBead.srcNodeTwo = srcString.getNode(i - 1);
                conBead.tgtNodeOne = tgtString.getNode(j - 1);
                conBead.tgtNodeTwo = null;
                conBead.alignmentCost = alignmentCosts.get(i, j) - alignmentCosts.get(i - 2, j - 1);

                alignments.add(conBead);
            }
            else if(di == 1)
            {
                TwoByTwoBead expBead = new TwoByTwoBead();

                expBead.srcNodeOne = srcString.getNode(i - 1);
                expBead.srcNodeTwo = null;
                expBead.tgtNodeOne = tgtString.getNode(j - 2);
                expBead.tgtNodeTwo = tgtString.getNode(j - 1);
                expBead.alignmentCost = alignmentCosts.get(i, j) - alignmentCosts.get(i - 1, j - 2);

                alignments.add(expBead);
            }
            else if(di == 1 && dj == 1)
            {
                TwoByTwoBead beaBead = new TwoByTwoBead();

                beaBead.srcNodeOne = srcString.getNode(i - 2);
                beaBead.srcNodeTwo = srcString.getNode(i - 1);
                beaBead.tgtNodeOne = tgtString.getNode(j - 2);
                beaBead.tgtNodeTwo = tgtString.getNode(j - 1);
                beaBead.alignmentCost = alignmentCosts.get(i, j) - alignmentCosts.get(i - 2, j - 2);

                alignments.add(beaBead);
            }
        }

		return alignments.size();
	}

    public void printAlignments(PrintStream ps)
    {
        if(alignments == null)
            return;

        int count = alignments.size();

        for (int i = count - 1; i >= 0; i--)
        {
            TwoByTwoBead bead = alignments.get(i);

            if(bead.srcNodeOne != null && bead.tgtNodeOne != null && bead.srcNodeTwo != null && bead.tgtNodeTwo != null)
                ps.println((bead.srcNodeOne.getIndex() + 1) + ", " + (bead.srcNodeTwo.getIndex() + 1)
                        + " : " + (bead.tgtNodeOne.getIndex() + 1) + ", " + (bead.tgtNodeTwo.getIndex() + 1));
            else if(bead.srcNodeOne != null && bead.tgtNodeOne != null && bead.srcNodeTwo != null)
                ps.println((bead.srcNodeOne.getIndex() + 1) + ", " + (bead.srcNodeTwo.getIndex() + 1)
                        + " : " + (bead.tgtNodeOne.getIndex() + 1));
            else if(bead.srcNodeOne != null && bead.tgtNodeOne != null && bead.tgtNodeTwo != null)
                ps.println((bead.srcNodeOne.getIndex() + 1)
                        + " : " + (bead.tgtNodeOne.getIndex() + 1) + ", " + (bead.tgtNodeTwo.getIndex() + 1));
            else if(bead.srcNodeOne != null && bead.tgtNodeOne != null)
                ps.println((bead.srcNodeOne.getIndex() + 1) + " : " + (bead.tgtNodeOne.getIndex() + 1));
            else if(bead.srcNodeOne != null)
                ps.println((bead.srcNodeOne.getIndex() + 1) + " : None");
            else if(bead.tgtNodeOne != null)
                ps.println("None : " + (bead.tgtNodeOne.getIndex() + 1));
        }
    }

    public void markAlignments(SSFStory srcStory, SSFStory tgtStory)
    {
        if(alignments == null)
            return;

        int count = alignments.size();

        for (int i = count - 1; i >= 0; i--)
        {
            TwoByTwoBead bead = alignments.get(i);

            if(bead.srcNodeOne != null && bead.tgtNodeOne != null && bead.srcNodeTwo != null && bead.tgtNodeTwo != null)
            {
                srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit());
                srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(tgtStory.getSentence(bead.tgtNodeTwo.getIndex()).getAlignmentUnit());

                tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit());
                tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(srcStory.getSentence(bead.srcNodeTwo.getIndex()).getAlignmentUnit());
            }
            else if(bead.srcNodeOne != null && bead.tgtNodeOne != null && bead.srcNodeTwo != null)
            {
                srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit());

                tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit());
                tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(srcStory.getSentence(bead.srcNodeTwo.getIndex()).getAlignmentUnit());
            }
            else if(bead.srcNodeOne != null && bead.tgtNodeOne != null && bead.tgtNodeTwo != null)
            {
                srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit());
                srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(tgtStory.getSentence(bead.tgtNodeTwo.getIndex()).getAlignmentUnit());

                tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit());
            }
            else if(bead.srcNodeOne != null && bead.tgtNodeOne != null)
            {
                srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit());

                tgtStory.getSentence(bead.tgtNodeOne.getIndex()).getAlignmentUnit().addAlignedUnit(srcStory.getSentence(bead.srcNodeOne.getIndex()).getAlignmentUnit());
            }
        }
    }

	public void clear()
	{
	}
}
