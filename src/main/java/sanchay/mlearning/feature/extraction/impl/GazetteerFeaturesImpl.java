/*
 * GazetteerFeaturesImpl.java
 *
 * Created on June 21, 2008, 12:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction.impl;


import sanchay.mlearning.feature.extraction.*;
import sanchay.corpus.ssf.tree.*;
/**
 *
 * @author Anil Kumar Singh
 */
public class GazetteerFeaturesImpl implements GazetteerFeatures
{
 
    
    public boolean isGazeteerWord(SSFNode node)
    {
        return true;
    
    }
    public boolean matchesWithGazeteer(SSFNode node)
    {
        return true;
    }
   
    public boolean matchesWithRegex(SSFNode node)
    {
        return true;
    }
}
