package iitb.crf;

import sanchay.mlearning.crf.ChunkedDataSequence;

/**
 *
 * @author Sunita Sarawagi
 *
 */ 
public class NestedCRF extends CRF {
    protected FeatureGeneratorNested featureGenNested;
    protected transient NestedViterbi nestedViterbi;

    public NestedCRF(int numLabels, FeatureGeneratorNested fgen, String arg) {
	super(numLabels,fgen,arg);
	featureGenNested = fgen;
	nestedViterbi = new NestedViterbi(this,1);
    }

    public NestedCRF(int numLabels, FeatureGeneratorNested fgen, java.util.Properties configOptions) {
	super(numLabels,fgen,configOptions);
	featureGenNested = fgen;
	nestedViterbi = new NestedViterbi(this,1);
    }

    protected Trainer getTrainer() {
	if (params.trainerType.startsWith("SegmentCollins"))
	    return new NestedCollinsTrainer(params);
	return new NestedTrainer(params);
    }

    protected Viterbi getViterbi(int beamsize) {
    	return new NestedViterbi(this,beamsize);
    }

    public void apply(DataSequence dataSeq) {
        /*Removed by Pankaj Soni*/
    	//apply((SegmentDataSequence)dataSeq);
        /*Added by Pankaj Soni*/
      SegmentDataSequence seq = new ChunkedDataSequence(dataSeq);
    	apply(seq);
    }

    public void apply(SegmentDataSequence dataSeq) {
        if (nestedViterbi==null)
            nestedViterbi = new NestedViterbi(this,1);
	if (params.debugLvl > 2) 
	    Util.printDbg("NestedCRF: Applying on " + dataSeq);
	nestedViterbi.bestLabelSequence(dataSeq,lambda);
    }
};
