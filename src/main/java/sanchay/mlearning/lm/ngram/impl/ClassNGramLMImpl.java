/*
 * ClassNGramLM.java
 *
 * Created on October 15, 2005, 3:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram.impl;

import sanchay.mlearning.lm.ngram.NGramLM;
import java.io.*;

import sanchay.mlearning.lm.ngram.*;
import sanchay.properties.*;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public class ClassNGramLMImpl implements ClassNGramLM {
    
    protected NGramLM classNGramLM;
    protected NGramLM classlessNGramLM;
    protected NGramLM classBasedNGramLM;
    
    /** Creates a new instance of ClassNGramLM */
	
    public ClassNGramLMImpl()
    {
    }
    
    public NGramLM getClassNGramLM()
    {
        return classNGramLM;
    }

    public void setClassNGramLM(NGramLM lm)
    {
        classNGramLM = lm;
    }

    public NGramLM getClasslessNGramLM()
    {
        return classlessNGramLM;
    }

    public void setClasslessNGramLM(NGramLM lm)
    {
        classlessNGramLM = lm;
    }

    public NGramLM getClassBasedNGramLM()
    {
        return classBasedNGramLM;
    }

    public void setClassBasedNGramLM(NGramLM lm)
    {
        classBasedNGramLM = lm;
    }
    
    public double getClassBasedSentenceProb1(String sentence)
    {
        return 0.0;
    }

    public double getClassBasedSentenceProb2(String sentence)
    {
        return 0.0;
    }

    public double getClassBasedSentenceProb3(String sentence)
    {
        return 0.0;
    }
}
