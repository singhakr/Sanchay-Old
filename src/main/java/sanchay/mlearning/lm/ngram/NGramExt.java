/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram;

/**
 *
 * @author Anil Kumar Singh
 */
public interface NGramExt extends NGram {
    public int getNormalizerIncrement();

    public void setNormalizerIncrement(int normalizerIncrement);
}
