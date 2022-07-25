/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

/**
 *
 * @author anil
 */
public class TranslationCandidateScores {
    protected double translationScore;

    public TranslationCandidateScores()
    {

    }

    public TranslationCandidateScores(double initScore)
    {
        translationScore = initScore;
    }

    /**
     * @return the translationScore
     */
    public double getTranslationScore() {
        return translationScore;
    }

    /**
     * @param translationScore the translationScore to set
     */
    public void setTranslationScore(double translationScore) {
        this.translationScore = translationScore;
    }

    public String toString()
    {
        return "" + translationScore;
    }
}
