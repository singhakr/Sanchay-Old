/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.lattice;

import sanchay.tree.SanchayEdges;

/**
 *
 * @author Anil Kumar Singh
 */
public class SanchayPolygon {
    protected SanchayEdges edges;

    public SanchayPolygon()
    {

    }

    /**
     * @return the edges
     */
    public SanchayEdges getEdges()
    {
        return edges;
    }

    /**
     * @param edges the edges to set
     */
    public void setEdges(SanchayEdges edges)
    {
        this.edges = edges;
    }
}
