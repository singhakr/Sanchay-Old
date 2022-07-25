/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

/**
 *
 * @author anil
 */
public class PhraseTranslationScores {
    protected double tranlsationScore;
    protected double revTranlsationScore;

//    protected double lexicalWeight;
//    protected double phraseWeight;

    /**
     * @return the tranlsationScore
     */
    public double getTranlsationScore() {
        return tranlsationScore;
    }

    /**
     * @param tranlsationScore the tranlsationScore to set
     */
    public void setTranlsationScore(double tranlsationScore) {
        this.tranlsationScore = tranlsationScore;
    }

    /**
     * @return the revTranlsationScore
     */
    public double getRevTranlsationScore() {
        return revTranlsationScore;
    }

    /**
     * @param revTranlsationScore the revTranlsationScore to set
     */
    public void setRevTranlsationScore(double revTranlsationScore) {
        this.revTranlsationScore = revTranlsationScore;
    }

    /**
     * @return the lexicalWeight
     */
//    public double getLexicalWeight() {
//        return lexicalWeight;
//    }
//
//    /**
//     * @param lexicalWeight the lexicalWeight to set
//     */
//    public void setLexicalWeight(double lexicalWeight) {
//        this.lexicalWeight = lexicalWeight;
//    }
//
//    /**
//     * @return the phraseWeight
//     */
//    public double getPhraseWeight() {
//        return phraseWeight;
//    }
//
//    /**
//     * @param phraseWeight the phraseWeight to set
//     */
//    public void setPhraseWeight(double phraseWeight) {
//        this.phraseWeight = phraseWeight;
//    }

    public String toString()
    {
        return " () " + " ||| " + " () " + " ||| "
                        + getTranlsationScore() + " "+ getRevTranlsationScore() + " " + 0.0 + " " + 0.0;
    }
}
