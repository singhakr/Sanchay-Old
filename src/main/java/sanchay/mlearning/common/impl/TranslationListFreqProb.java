/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import java.util.Vector;
import sanchay.mlearning.common.MLFreqProb;

/**
 *
 * @author anil
 */
public class TranslationListFreqProb extends TranslationFreqProb implements MLFreqProb {

    public TranslationListFreqProb()
    {
        sourceObject = new Vector();
        targetObject = new Vector();
    }

    public TranslationListFreqProb(Object srcObj, Object tgtObj)
    {
        addObjects(srcObj, tgtObj);
    }

    public TranslationListFreqProb(int freq)
    {
        sourceObject = new Vector();
        targetObject = new Vector();

        frequency = freq;
    }

    public TranslationListFreqProb(Object srcObj, Object tgtObj, int freq)
    {
        addObjects(srcObj, tgtObj);
        
        frequency = freq;
    }

    public TranslationListFreqProb(int freq, double prob)
    {
        sourceObject = new Vector();
        targetObject = new Vector();

        frequency = freq;
        probability = prob;
    }

    public TranslationListFreqProb(Object srcObj, Object tgtObj, int freq, double prob)
    {
        addObjects(srcObj, tgtObj);

        frequency = freq;
        probability = prob;
    }

    public void addObjects(Object srcObj, Object tgtObj)
    {
        if(sourceObject == null)
            sourceObject = new Vector();

        if(targetObject == null)
            targetObject = new Vector();

        ((Vector) sourceObject).add(srcObj);
        ((Vector) targetObject).add(tgtObj);
    }

    public String getObjectString()
    {
        int count = ((Vector) sourceObject).size();

        String objectString = "";

        for (int i = 0; i < count; i++)
        {
            Object srcObj = ((Vector) sourceObject).get(i);
            Object tgtObj = ((Vector) targetObject).get(i);

            if(i == 0)
                objectString += srcObj + " became " + tgtObj + "\n";
            else
                objectString += "\t" + srcObj + " became " + tgtObj + "\n";
        }

        return objectString + "\n";
    }
}
