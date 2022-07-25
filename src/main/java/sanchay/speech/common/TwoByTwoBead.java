/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.speech.common;

/**
 *
 * @author anil
 */
public class TwoByTwoBead {

    public StringNode srcNodeOne;
    public StringNode srcNodeTwo;
    public StringNode tgtNodeOne;
    public StringNode tgtNodeTwo;

    public int beadType = 0;
    public double alignmentCost = 0.0;

    public static final int SUBSTITUTION = 0;
    public static final int DELETION = 1;
    public static final int INSERTION = 2;
    public static final int CONTRACTION = 3;
    public static final int EXPANSION = 4;
    public static final int BEAD_MATCH = 5;

    public static double PENALTY01A = 1.0;
    public static double PENALTY12A = 1.0;
    public static double PENALTY22A = 1.0;

    public static double PENALTY01B = 1.0;
    public static double PENALTY12B = 1.0;
    public static double PENALTY22B = 1.0;

    public static TwoByTwoBead getAlignmentBead(StringNode srcNodeOne, StringNode srcNodeTwo,
            StringNode tgtNodeOne, StringNode tgtNodeTwo)
    {
        TwoByTwoBead alignmentBead = null;
        
        TwoByTwoBead subBead = new TwoByTwoBead();

        subBead.srcNodeOne = srcNodeOne;
        subBead.srcNodeTwo = null;
        subBead.tgtNodeOne = tgtNodeOne;
        subBead.tgtNodeTwo = null;

        getSubstitutionCost(subBead);

        double minCost = subBead.alignmentCost;
        alignmentBead = subBead;
        alignmentBead.beadType = SUBSTITUTION;

        TwoByTwoBead delBead = new TwoByTwoBead();

        delBead.srcNodeOne = srcNodeOne;
        delBead.srcNodeTwo = null;
        delBead.tgtNodeOne = null;
        delBead.tgtNodeTwo = null;

        getDeletionCost(delBead);

        if(delBead.alignmentCost < minCost)
        {
            minCost = delBead.alignmentCost;
            alignmentBead = delBead;
            alignmentBead.beadType = DELETION;
        }

        TwoByTwoBead insBead = new TwoByTwoBead();

        insBead.srcNodeOne = null;
        insBead.srcNodeTwo = null;
        insBead.tgtNodeOne = tgtNodeOne;
        insBead.tgtNodeTwo = null;

        getInsertionCost(insBead);

        if(insBead.alignmentCost < minCost)
        {
            minCost = insBead.alignmentCost;
            alignmentBead = insBead;
            alignmentBead.beadType = INSERTION;
        }

        TwoByTwoBead conBead = new TwoByTwoBead();

        conBead.srcNodeOne = srcNodeOne;
        conBead.srcNodeTwo = srcNodeTwo;
        conBead.tgtNodeOne = tgtNodeOne;
        conBead.tgtNodeTwo = null;

        getContractionCost(conBead);

        if(conBead.alignmentCost < minCost)
        {
            minCost = conBead.alignmentCost;
            alignmentBead = conBead;
            alignmentBead.beadType = CONTRACTION;
        }

        TwoByTwoBead expBead = new TwoByTwoBead();

        expBead.srcNodeOne = srcNodeOne;
        expBead.srcNodeTwo = srcNodeTwo;
        expBead.tgtNodeOne = null;
        expBead.tgtNodeTwo = tgtNodeTwo;

        getExpansionCost(expBead);

        if(expBead.alignmentCost < minCost)
        {
            minCost = expBead.alignmentCost;
            alignmentBead = expBead;
            alignmentBead.beadType = EXPANSION;
        }

        TwoByTwoBead beaBead = new TwoByTwoBead();

        beaBead.srcNodeOne = srcNodeOne;
        beaBead.srcNodeTwo = srcNodeTwo;
        beaBead.tgtNodeOne = tgtNodeOne;
        beaBead.tgtNodeTwo = tgtNodeTwo;

        getBeadMatchCost(beaBead);

        if(beaBead.alignmentCost < minCost)
        {
            minCost = beaBead.alignmentCost;
            alignmentBead = beaBead;
            alignmentBead.beadType = BEAD_MATCH;
        }

        return alignmentBead;
    }

    public static void getSubstitutionCost(TwoByTwoBead bead)
    {
        bead.alignmentCost = bead.srcNodeOne.matchScore(bead.tgtNodeOne);
    }

    public static void getDeletionCost(TwoByTwoBead bead)
    {
        double d = RecogUtils.getDistance(bead.srcNodeOne.getFeature(), null);
        bead.alignmentCost = d * 2.0;
    }

    public static void getInsertionCost(TwoByTwoBead bead)
    {
        double d = RecogUtils.getDistance(null, bead.tgtNodeOne.getFeature());
        bead.alignmentCost = d * 2.0;
    }

    public static void getContractionCost(TwoByTwoBead bead)
    {
        double d = bead.tgtNodeOne.matchScore(bead.srcNodeOne, bead.srcNodeTwo, true);
        bead.alignmentCost =  d * 4.0;
    }

    public static void getExpansionCost(TwoByTwoBead bead)
    {
        double d = bead.srcNodeOne.matchScore(bead.tgtNodeOne, bead.tgtNodeTwo, false);
        bead.alignmentCost =  d * 4.0;
    }

    public static void getBeadMatchCost(TwoByTwoBead bead)
    {
        double d = bead.srcNodeOne.matchScore(bead.srcNodeTwo, bead.tgtNodeOne, bead.tgtNodeTwo);
        bead.alignmentCost =  d * 4.0;
    }
}
