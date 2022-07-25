/*
 * EMDataCorpusImpl.java
 *
 * Created on June 27, 2006, 6:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import sanchay.GlobalProperties;
import sanchay.mlearning.common.MLCorpus;
import sanchay.mlearning.common.MLFrequency;
import sanchay.mlearning.common.MLType;

/**
 *
 * @author Anil Kumar Singh
 */
public abstract class MLAbstractCorpus implements MLCorpus
{
    protected LinkedHashMap data;
    
    /** Creates a new instance of EMDataCorpusImpl */
    public MLAbstractCorpus()
    {
    }

    public long getTypeCount() {
	return data.size();
    }

    public MLFrequency getTokenCount() {
	return null;
    }

    public MLFrequency getTypeFrequency(MLType type) {
	return (MLFrequency) data.get(type);
    }

    public long addType(MLType type, MLFrequency freq) {
	data.put(type, freq);
	return data.size();
    }

    public long removeType(MLType type) {
	data.remove(type);
	return data.size();
    }

    public Iterator getTypes() {
	return data.keySet().iterator();
    }

    public Iterator getTypeFrequencies() {
	return data.values().iterator();
    }

    public void print(PrintStream ps)
    {
	Iterator itr = getTypes();
	
	ps.println(GlobalProperties.getIntlString("Type_Frequencies:"));
	
	while(itr.hasNext())
	{
	    MLType tp = (MLType) itr.next();
	    ps.println("\t" + tp + "\t" + getTypeFrequency(tp));
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
