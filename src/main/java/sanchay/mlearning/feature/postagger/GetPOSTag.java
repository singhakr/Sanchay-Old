/*
 * getPOSTag.java
 *
 * Created on September 5, 2008, 12:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.postagger;

import java.util.List;
import sanchay.corpus.ssf.SSFSentence;


/**
 *
 * @author Anil Kumar Singh
 */
public interface GetPOSTag {
    
    public static final int WINDOW_DIRECTION_LEFT = 0;
    public static final int WINDOW_DIRECTION_RIGHT = 1;
    public static final int WINDOW_DIRECTION_BOTH = 2;
    
    public List getPOSTag(SSFSentence ssfSentence, int wordIndex, int windowSize, int direction , int bool);
    
}

