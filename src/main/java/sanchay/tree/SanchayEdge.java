/*
 * SSFEdge.java
 *
 */

package sanchay.tree;

import java.awt.Color;
import java.awt.Stroke;

public class SanchayEdge {

    public Object node1;
    public int row1;
    public int col1;

    public Object node2;
    public int row2;
    public int col2;
    
    protected String label;
    protected Color color;
    protected Stroke stroke;

    protected boolean curved;
    protected boolean isTriangle;

    public SanchayEdge(Object node1, int row1, int col1, Object node2, int row2, int col2)
    {
        this.node1 = node1;
        this.row1 = row1;
        this.col1 = col1;

        this.node2 = node2;
        this.row2 = row2;
        this.col2 = col2;
    }

    public int[] getCells()
    {
	return new int[] {row1, col1, row2, col2};
    }

    public void setCells(int[] cls)
    {
	row1 = cls[0];
	col1 = cls[1];
	row2 = cls[2];
	col2 = cls[3];
    }

    public int[] getStartCell()
    {
	return new int[] {row1, col1};
    }

    public int[] getEndCell()
    {
	return new int[] {row2, col2};
    }

    public Object getStartNode()
    {
	return node1;
    }

    public Object getEndNode()
    {
	return node2;
    }
    
    public String getLabel()
    {
	return label;
    }
    
    public void setLabel(String l)
    {
	label = l;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the stroke
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * @param stroke the stroke to set
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public boolean isCurved()
    {
        return curved;
    }

    public void isCurved(boolean c)
    {
        curved = c;
    }

    public boolean isTriangle()
    {
        return isTriangle;
    }

    public void isTriangle(boolean isTriangle)
    {
        this.isTriangle = isTriangle;
    }
}
