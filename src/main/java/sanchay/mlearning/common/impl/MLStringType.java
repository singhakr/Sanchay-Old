/*
 * MLStringType.java
 *
 * Created on June 29, 2006, 1:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import sanchay.mlearning.common.MLType;

/**
 *
 * @author Anil Kumar Singh
 */
public class MLStringType implements MLType
{
    protected String type;
    
    /** Creates a new instance of MLStringType */
    public MLStringType(String t)
    {
	type = t;
    }

    public String getFrequency()
    {
	return type;
    }

    public void setFrequency(String t)
    {
	type = t;
    }

    public boolean equals(Object obj) {
        return type.equals(((MLStringType) obj).type);
    }

    public String toString() {
        return type;
    }

    public int hashCode() {
        return type.hashCode();
    }
}
