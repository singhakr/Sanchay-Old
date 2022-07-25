/*
 * ClassNGramLM.java
 *
 * Created on October 15, 2005, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public interface ClassNGramLM {
    
    public NGramLM getClassNGramLM();

    public void setClassNGramLM(NGramLM lm);
    
    public NGramLM getClasslessNGramLM();

    public void setClasslessNGramLM(NGramLM lm);

    public NGramLM getClassBasedNGramLM();

    public void setClassBasedNGramLM(NGramLM lm);
    
    public double getClassBasedSentenceProb1(String sentence);

    public double getClassBasedSentenceProb2(String sentence);

    public double getClassBasedSentenceProb3(String sentence);
}
