/*
 * GazetteerFeatures.java
 *
 * Created on June 20, 2008, 11:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction;

import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author Anil Kumar Singh
 */
public interface GazetteerFeatures {
    
    /** Creates a new instance of GazeteerFeature */
    boolean isGazeteerWord(SSFNode node);
    boolean matchesWithGazeteer(SSFNode node);
    boolean matchesWithRegex(SSFNode node);
}
