/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

/**
 *
 * @author Anil Kumar Singh
 */
public class DefaultHypothesis implements Hypothesis {

    protected Object LHS;
    protected Object RHS;

    protected double cost;

    public DefaultHypothesis(Object LHS, Object RHS, double cost)
    {
        this.LHS = LHS;
        this.RHS = RHS;

        this.cost = cost;
    }

    /**
     * @return the LHS
     */
    public Object getLHS()
    {
        return LHS;
    }

    /**
     * @param LHS the LHS to set
     */
    public void setLHS(Object LHS)
    {
        this.LHS = LHS;
    }

    /**
     * @return the RHS
     */
    public Object getRHS()
    {
        return RHS;
    }

    /**
     * @param RHS the RHS to set
     */
    public void setRHS(Object RHS)
    {
        this.RHS = RHS;
    }

    /**
     * @return the cost
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }
}
