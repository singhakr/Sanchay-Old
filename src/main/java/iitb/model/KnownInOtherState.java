package iitb.model;

import iitb.crf.DataSequence;
import iitb.model.*;
/**
 *
 * @author Sunita Sarawagi
 *
 */ 

public class KnownInOtherState extends FeatureTypes {
    int stateId;
    WordsInTrainExt dict;
    float wordFreq;
    int wordPos;
    public KnownInOtherState(FeatureGenImpl m, WordsInTrainExt d) {
	super(m);
	dict = d;
    }
    void nextStateId() {
	for (stateId++; (stateId < model.numStates()); stateId++)
	    if (dict.count(wordPos,stateId) == 0)
		return;
    }
    public boolean startScanFeaturesAt(DataSequence data, int prevPos, int pos) {
	if (dict.count(data.x(pos).toString()) <= WordFeatures.RARE_THRESHOLD+1) {
	    stateId = model.numStates();
	    return false;
	} else {
	    wordPos = dict.getIndex(data.x(pos).toString());
	    stateId = -1;
	    nextStateId();
	    wordFreq = (float)Math.log((double)dict.count(data.x(pos).toString())/dict.totalCount());
	    return true;
	}
    }
    public boolean hasNext() {
	return (stateId < model.numStates());
    }
    public void next(FeatureImpl f) {
	setFeatureIdentifier(stateId,stateId,"K",f);
	f.yend = stateId;
	f.ystart = -1;
	f.val = wordFreq;
	nextStateId();
    }
};
    
