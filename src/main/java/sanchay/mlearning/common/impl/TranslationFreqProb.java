/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import sanchay.mlearning.common.MLFreqProb;

/**
 *
 * @author anil
 */
public class TranslationFreqProb extends DefaultMLFreqProb implements MLFreqProb {

    protected Object sourceObject;
    protected Object targetObject;

    public TranslationFreqProb()
    {

    }

    public TranslationFreqProb(Object srcObj, Object tgtObj)
    {
        sourceObject = srcObj;
        targetObject = tgtObj;
    }

    public TranslationFreqProb(int freq)
    {
        frequency = freq;
    }

    public TranslationFreqProb(Object srcObj, Object tgtObj, int freq)
    {
        sourceObject = srcObj;
        targetObject = tgtObj;

        frequency = freq;
    }

    public TranslationFreqProb(int freq, double prob)
    {
        frequency = freq;
        probability = prob;
    }

    public TranslationFreqProb(Object srcObj, Object tgtObj, int freq, double prob)
    {
        sourceObject = srcObj;
        targetObject = tgtObj;
        
        frequency = freq;
        probability = prob;
    }

    /**
     * @return the sourceObject
     */
    public Object getSourceObject()
    {
        return sourceObject;
    }

    /**
     * @param sourceObject the sourceObject to set
     */
    public void setSourceObject(Object sourceObject)
    {
        this.sourceObject = sourceObject;
    }

    /**
     * @return the targetObject
     */
    public Object getTargetObject()
    {
        return targetObject;
    }

    /**
     * @param targetObject the targetObject to set
     */
    public void setTargetObject(Object targetObject)
    {
        this.targetObject = targetObject;
    }

}
