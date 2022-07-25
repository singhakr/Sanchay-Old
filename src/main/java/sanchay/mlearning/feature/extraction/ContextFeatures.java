/*
 * ContextFeatures.java
 *
 * Created on June 20, 2008, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction;

//import java.lang.String.*;

import java.util.List;
import sanchay.corpus.ssf.*;
/**
 *
 * @author Anil Kumar Singh
 */
public interface ContextFeatures {
    
    public static final int WINDOW_DIRECTION_LEFT = 0;
    public static final int WINDOW_DIRECTION_RIGHT = 1;
    public static final int WINDOW_DIRECTION_BOTH = 2;
    
    
    /** Creates a new instance of ContextFeatures */
    
    
    public List getwordContext(SSFSentence ssfSentence, int wordIndex, int windowSize,int minFreq, String str,int direction);
    public List gettagContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, int direction);
    public List getContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, int minfreq);
    public List getwordtagContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, String str, int direction);
    public List getContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, String str,int minFreq);
    /*
    public boolean isFirstWordOfSentence(String str);
    public boolean isLastWordOfSentence(String str);
    public String DynamicLeftTagFeature(SSFNode ssfNode);
    public String DynamicRightTagFeature(SSFNode ssfNode);
    */
    
}
