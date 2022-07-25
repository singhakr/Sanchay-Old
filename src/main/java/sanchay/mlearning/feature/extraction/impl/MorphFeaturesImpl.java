/*
 * MorphFeaturesImpl.java
 *
 * Created on June 20, 2008, 4:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction.impl;

import java.lang.String.*;
import sanchay.mlearning.feature.extraction.*;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.corpus.ssf.impl.*;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.features.impl.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class MorphFeaturesImpl implements MorphFeatures {
    
    /** Creates a new instance of MorphFeaturesImpl */
    int  suffixLen = 4;
    int prefixLen = 4;
    
    public String getMorphFeature(SSFNode ssfNode, int feature)
    {
        
        String morphString;
        if(feature==MORPH_FEATURE_ROOT)
        {
          //  FeatureStructure fs = ((SSFPhrase) ssfNode).convertToMMNode().
        }
        else if(feature==MORPH_FEATURE_POS_TAG)
        {
             if(ssfNode.isLeaf()==true)
             {
             //      if
            }
             else
             {
                 
             }
        }
        else 
             return null;
             return null;
    }
    
    public String getRootWord(SSFNode ssfNode)
    {
        FeatureStructures fs;
        fs=ssfNode.getFeatureStructures();
        
        return fs.getAltFSValue(0).toString();
    }
    public boolean checkPOSTag(SSFNode ssfNode, String tag)
    {
       if(ssfNode.isLeaf()==true)
       {
           if(ssfNode.getPrevious().getName()==tag)
                return true;
       }
       else 
       {
           if(ssfNode.getName()==tag)
               return true;
           else
               return false;
       }
       
       return false;
    }
    public String getSuffix(SSFNode ssfNode)
    {
        return ssfNode.getName().substring(ssfNode.getName().length()- suffixLen,ssfNode.getName().length());
                
    }
    public String getPrefix(SSFNode ssfNode)
    {
        return ssfNode.getName().substring(0,prefixLen);
        
    }
    //public String getFeatureStructure(FeatureAttribute fattrib);
    
}
