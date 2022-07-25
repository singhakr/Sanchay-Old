/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram;

/**
 *
 * @author Anil Kumar Singh
 */
public interface NGramLMExt<NG extends NGram> extends NGramLM<NG> {
    public long addNGram(String wds, int whichGram, int normIncrement);

    public long addNGram(String wds, NG ng, int whichGram, int normIncrement);

    public long addNGram(String wds, int whichGram,long freq, int normIncrement);
}
