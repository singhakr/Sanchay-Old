/*
 * MLModel.java
 *
 * Created on June 30, 2006, 11:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

import java.io.PrintStream;
import java.util.Iterator;

/**
 *
 * @author Anil Kumar Singh
 */
public interface MLModel
{
    long getTypeCount();

    MLProbability getTotalProbability();

    Iterator getTypes();

    Iterator getTypeProbabilities();

    MLProbability getTypeProbability(MLType type);

    long addType(MLType type, MLProbability prob);

    long removeType(MLType type);

    void print(PrintStream ps);
    
    public void initializeComplete(int initType, int emType);
    
    public void initializeAnalysisPart(int initType, int emType);
    
    public void initializeIncomplete(int initType, int emType);
}
