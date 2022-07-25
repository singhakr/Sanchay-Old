package iitb.model;
import iitb.crf.DataSequence;
import iitb.model.*;

/**
 *
 * @author Sunita Sarawagi
 *
 */ 

public class UnknownFeature extends FeatureTypes {
    private static final long serialVersionUID = 6122L;
	int stateId;
    WordsInTrainExt dict;
    public UnknownFeature(FeatureGenImpl m, WordsInTrainExt d) {
	super(m);
	dict = d;
    }
    public boolean startScanFeaturesAt(DataSequence data, int prevPos, int pos) {
	if (dict.count(data.x(pos).toString()) > WordFeatures.RARE_THRESHOLD+1) {
	    stateId = model.numStates();
	    return false;
	} else {
	    stateId = 0;
	    return true;
	}
    }
    public boolean hasNext() {
	return (stateId < model.numStates());
    }
    public void next(FeatureImpl f) {
	setFeatureIdentifier(stateId,stateId,"U",f);
	f.yend = stateId;
	f.ystart = -1;
	f.val = 1;
	stateId++;
    }
    
	/* (non-Javadoc)
	 * @see iitb.Model.FeatureTypes#maxFeatureId()
	 */
	public int maxFeatureId() {
		return model.numStates();
	}
};


