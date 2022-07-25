/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

/**
 *
 * @author Anil Kumar Singh
 */
public interface Hypothesis {

    /**
     * @return the cost
     */
    double getCost();

    /**
     * @return the LHS
     */
    Object getLHS();

    /**
     * @return the RHS
     */
    Object getRHS();

    /**
     * @param cost the cost to set
     */
    void setCost(double cost);

    /**
     * @param LHS the LHS to set
     */
    void setLHS(Object LHS);

    /**
     * @param RHS the RHS to set
     */
    void setRHS(Object RHS);

}
