/*
 * MLStringAnalysis.java
 *
 * Created on June 29, 2006, 1:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import sanchay.mlearning.common.MLAnalysis;

/**
 *
 * @author Anil Kumar Singh
 */
public class MLStringAnalysis implements MLAnalysis
{
    protected String analysis;
    
    /** Creates a new instance of MLStringAnalysis */
    public MLStringAnalysis(String a)
    {
	analysis = a;
    }

    public String getAnalysis()
    {
	return analysis;
    }

    public void setAnalysis(String a)
    {
	analysis = a;
    }

    public boolean equals(Object obj) {
        return analysis.equals(((MLStringAnalysis) obj).analysis);
    }

    public String toString() {
        return analysis;
    }

    public int hashCode() {
        return analysis.hashCode();
    }
}
