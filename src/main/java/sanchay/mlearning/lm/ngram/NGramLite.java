/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.lm.ngram;

/**
 *
 * @author anil
 */
public interface NGramLite extends NGramCount {

    double getBackwt();

    double getProb();

    void setBackwt(double b);

    void setProb(double p);
    
}
