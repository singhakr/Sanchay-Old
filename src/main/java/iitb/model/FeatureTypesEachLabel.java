package iitb.model;

import iitb.crf.DataSequence;

/**
 * This can be used as a wrapper around a FeatureTypes class that wants to
 * generate a feature for each label. 
 */
public class FeatureTypesEachLabel extends FeatureTypes {
	protected FeatureTypes single;

	int numStates;

	int stateId;

	FeatureImpl featureImpl;
	
	boolean optimize = false;

	public FeatureTypesEachLabel(FeatureGenImpl fgen, FeatureTypes single) {
		super(fgen);
		numStates = model.numStates();
		this.single = single;
		featureImpl = new FeatureImpl();
		thisTypeId = single.thisTypeId;
	}
	protected void nextFeature() {
	    single.next(featureImpl);
	}
	boolean advance() {
		stateId++;
		if (stateId < numStates)
			return true;
		if (single.hasNext()) {
		    nextFeature();
			stateId = 0;
		} 
		return stateId < numStates;
	}

	public boolean startScanFeaturesAt(iitb.crf.DataSequence data, int prevPos,
			int pos) {
		stateId = numStates;
		single.startScanFeaturesAt(data, prevPos, pos);
		return advance();
	}

	public boolean startScanFeaturesAt(iitb.crf.DataSequence data,
			int pos) {
		stateId = numStates;
		single.startScanFeaturesAt(data, pos);
		return advance();
	}

	public boolean hasNext() {
		return (stateId < numStates);
	}
	protected FeatureImpl getFeature() {return featureImpl;}
	public void next(iitb.model.FeatureImpl f) {
		f.copy(getFeature());
		f.yend = stateId;
		single.setFeatureIdentifier(featureImpl.strId.id * numStates + stateId,
				stateId, featureImpl.strId.name, f);
		advance();
	}
	
	/* (non-Javadoc)
	 * @see iitb.Model.FeatureTypes#requiresTraining()
	 */
	public boolean requiresTraining() {
		return single.requiresTraining();
	}
	/* (non-Javadoc)
	 * @see iitb.Model.FeatureTypes#train(iitb.CRF.DataSequence, int)
	 */
	public void train(DataSequence data, int pos) {
		single.train(data, pos);
	}
	 public boolean fixedTransitionFeatures() {
	        return single.fixedTransitionFeatures();
	 }
};
	
