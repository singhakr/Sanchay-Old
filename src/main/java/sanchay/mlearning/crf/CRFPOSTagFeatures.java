/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.crf;

import iitb.crf.DataSequence;
import iitb.model.FeatureGenImpl;
import iitb.model.FeatureImpl;
import iitb.model.FeatureTypes;
import iitb.model.WordsInTrainExt;
import java.io.Serializable;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class CRFPOSTagFeatures extends FeatureTypes implements Serializable {

    int stateId;
    int statePos;
    Object token;
    int tokenId;
    WordsInTrainExt dict;
    int _numWordStatePairs;
    public static int RARE_THRESHOLD = 0;

    public CRFPOSTagFeatures(FeatureGenImpl m, WordsInTrainExt d) {
        super(m);
        dict = d;
    }

    private void nextStateId() {
        stateId = dict.nextStateWithWord(token, stateId);
        statePos++;
    }

    public boolean startScanFeaturesAt(DataSequence data, int prevPos, int pos) {
        stateId = -1;
        String str = ((SSFNode) data.x(pos)).getName();
        if (dict.count(str) > RARE_THRESHOLD) {
            token = str;
            tokenId = dict.getIndex(token);
            statePos = -1;
            nextStateId();
            return true;
        }
        return false;
    }

    public boolean hasNext() {
        return (stateId != -1);
    }

    public void next(FeatureImpl f) {
        if (featureCollectMode()) {
            setFeatureIdentifier(tokenId * model.numStates() + stateId, stateId, "W_" + token, f);
        } else {
            setFeatureIdentifier(tokenId * model.numStates() + stateId, stateId, token, f);
        }
        f.yend = stateId;
        f.ystart = -1;
        f.val = 1;
        nextStateId();
    }
    /* (non-Javadoc)
     * @see iitb.Model.FeatureTypes#maxFeatureId()
     */

    public int maxFeatureId() {
        return dict.dictionaryLength() * model.numStates();
    }
}
