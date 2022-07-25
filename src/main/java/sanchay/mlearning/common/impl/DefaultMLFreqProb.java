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
public class DefaultMLFreqProb implements MLFreqProb
{
    protected int frequency;
    protected double probability;

    public DefaultMLFreqProb()
    {

    }

    public DefaultMLFreqProb(int freq)
    {
        frequency = freq;
    }

    public DefaultMLFreqProb(int freq, double prob)
    {
        frequency = freq;
        probability = prob;
    }

    /**
     * @return the frequency
     */
    public int getFrequency()
    {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }

    /**
     * @return the probability
     */
    public double getProbability()
    {
        return probability;
    }

    /**
     * @param probability the probability to set
     */
    public void setProbability(double probability)
    {
        this.probability = probability;
    }
}
