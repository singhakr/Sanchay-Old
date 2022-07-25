/*
 * EMDataModelImpl.java
 *
 * Created on June 27, 2006, 6:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import sanchay.GlobalProperties;
import sanchay.mlearning.common.MLModel;
import sanchay.mlearning.common.MLProbability;
import sanchay.mlearning.common.MLType;

/**
 *
 * @author Anil Kumar Singh
 */
public abstract class MLAbstractModel implements MLModel
{
    protected LinkedHashMap data;
    
    /** Creates a new instance of EMDataModelImpl */
    public MLAbstractModel()
    {
    }

    public long getTypeCount() {
	return data.size();
    }

    public MLProbability getTotalProbability() {
	return null;
    }

    public MLProbability getTypeProbability(MLType type) {
	return (MLProbability) data.get(type);
    }

    public long addType(MLType type, MLProbability prob) {
	data.put(type, prob);
	return data.size();
    }

    public long removeType(MLType type) {
	data.remove(type);
	return data.size();
    }

    public Iterator getTypes() {
	return data.keySet().iterator();
    }

    public Iterator getTypeProbabilities() {
	return data.values().iterator();
    }

    public void print(PrintStream ps)
    {
	Iterator itr = getTypes();
	
	ps.println(GlobalProperties.getIntlString("Type_Probabilities:"));
	
	while(itr.hasNext())
	{
	    MLType tp = (MLType) itr.next();
	    ps.println("\t" + tp + "\t" + getTypeProbability(tp));
	}
    }
    
    public void initializeComplete(int initType, int emType)
    {
	
    }
    
    public void initializeAnalysisPart(int initType, int emType)
    {
	
    }
    
    public void initializeIncomplete(int initType, int emType)
    {
	
    }
}
