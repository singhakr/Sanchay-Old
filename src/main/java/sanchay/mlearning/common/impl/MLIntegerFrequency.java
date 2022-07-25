/*
 * MLIntegerFrequency.java
 *
 * Created on June 29, 2006, 1:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import sanchay.mlearning.common.MLFrequency;

/**
 *
 * @author Anil Kumar Singh
 */
public class MLIntegerFrequency implements MLFrequency
{
     protected Integer frequency;
   
    /** Creates a new instance of MLIntegerFrequency */
    public MLIntegerFrequency(int freq)
    {
	frequency = new Integer(freq);
    }

    public Integer getFrequency()
    {
	return frequency;
    }

    public void setFrequency(Integer freq)
    {
	frequency = freq;
    }

    public String toString()
    {
        return frequency.toString();
    }
}
