/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.context;

import sanchay.context.impl.FunctionalContextElement;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author anil
 */
public class ComplexContextElement extends FunctionalContextElementImpl implements FunctionalContextElement {

    protected LinkedHashMap<Integer,String> contextFeatures;
    protected LinkedHashMap<Integer,Long> contextFeaturesFrequencies;

    public ComplexContextElement() {

        contextFeatures = new LinkedHashMap<Integer,String>();
        contextFeaturesFrequencies = new LinkedHashMap<Integer,Long>();
    }

    public int countContextFeature()
    {
        return contextFeatures.size();
    }

    public Iterator getContextFeatureKeys()
    {
        return contextFeatures.keySet().iterator();
    }

    public String getContextFeature(Integer k)
    {
        return contextFeatures.get(k);
    }

    public int addContextFeature(Integer k, String v)
    {
        String feature = contextFeatures.get(k);

        if(feature == null)
        {
            contextFeatures.put(k, v);
            contextFeaturesFrequencies.put(k, new Long(1));
        }
        else
        {
            Long featureFreq = contextFeaturesFrequencies.get(k);
            contextFeaturesFrequencies.put(k, new Long(featureFreq.longValue() + 1));
        }

        return contextFeatures.size();
    }

    public int addAllContextFeatures(ComplexContextElement cce)
    {
        Iterator itr = cce.getContextFeatureKeys();

        while(itr.hasNext())
        {
            Integer featureKey = (Integer) itr.next();
            String feature = (String) cce.getContextFeature(featureKey);

            addContextFeature(featureKey, feature);
        }

        return contextFeatures.size();
    }

    public int removeContextFeature(Integer k)
    {
        String feature = contextFeatures.get(k);

        if(feature == null)
            return contextFeatures.size();

        Long featureFreq = contextFeaturesFrequencies.get(k);

        if(featureFreq.longValue() == 1)
        {
            contextFeatures.remove(k);
            contextFeaturesFrequencies.remove(k);
        }
        else
        {
            contextFeaturesFrequencies.put(k, new Long(featureFreq.longValue() - 1));
        }

        return contextFeatures.size();
    }
}
