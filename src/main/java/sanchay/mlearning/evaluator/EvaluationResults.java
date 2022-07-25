/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.evaluator;

import java.io.PrintStream;

/**
 *
 * @author Anil Kumar Singh
 */
public class EvaluationResults {
    protected double precision;
    protected double recall;
    protected double fMeasure;

    public EvaluationResults()
    {

    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getFMeasure() {
        return fMeasure;
    }

    public void setFMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public void print(PrintStream ps)
    {
        ps.println("Precision: " + precision);
        ps.println("Recall: " + recall);
        ps.println("F-Meeasure: " + fMeasure);
    }
}
