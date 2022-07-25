/*
 * SanchayNode.java
 *
 * Created on October 7, 2005, 6:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package sanchay.tree;

import java.io.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import sanchay.corpus.ssf.tree.SSFNode;

import sanchay.table.gui.*;

/**
 *
 *  @author Anil Kumar Singh
 */
public class SanchayMutableTreeNode extends DefaultMutableTreeNode
        implements Cloneable, MutableTreeNode, Serializable {

    public static final int CHUNK_MODE = 0;
    public static final int FS_MODE = 1;
    public static final int DEPENDENCY_RELATIONS_MODE = 2;
    public static final int PHRASE_STRUCTURE_MODE = 3;
    public static final int DICT_FST_MODE = 4;
//    protected int requiredColumnCount;
    protected int rowIndex;
    protected int columnIndex;

    /** Creates a new instance of SanchayNode */
    public SanchayMutableTreeNode() {
        super();

//	requiredColumnCount = -1;
        rowIndex = -1;
        columnIndex = -1;
    }

    public SanchayMutableTreeNode(Object userObject) {
        super(userObject);

//	requiredColumnCount = -1;
        rowIndex = -1;
        columnIndex = -1;
    }

    public SanchayMutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);

//	requiredColumnCount = -1;
        rowIndex = -1;
        columnIndex = -1;
    }

    public SanchayMutableTreeNode getCopy() throws Exception {
        return null;
    }

    public List<SanchayMutableTreeNode> getAllLeaves() // &get_leaves( [$tree] )  -> @leaf_nodes;
    {
        List<SanchayMutableTreeNode> leaves = new ArrayList<SanchayMutableTreeNode>();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            SanchayMutableTreeNode node = (SanchayMutableTreeNode) getChildAt(i);

            if (node.isLeaf()) {
                leaves.add(node);
            } else {
                leaves.addAll(((SanchayMutableTreeNode) getChildAt(i)).getAllLeaves());
            }
        }

        return leaves;
    }

    public boolean isDeep() {
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            if (getChildAt(i).getChildCount() > 0) {
                return true;
            }
        }

        return false;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int i) {
        rowIndex = i;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int i) {
        columnIndex = i;
    }

    public SanchayMutableTreeNode getLeftNode()
    {
        SanchayMutableTreeNode prevNode = (SanchayMutableTreeNode) getPreviousSibling();

        if(prevNode != null)
            return prevNode;

        SanchayMutableTreeNode prevParent = (SanchayMutableTreeNode) ((SanchayMutableTreeNode) parent).getPreviousSibling();

        if(parent == null || prevParent == null)
            return null;

        return (SanchayMutableTreeNode) prevParent.getLastChild();
    }

    public SanchayMutableTreeNode getRightNode()
    {
        SanchayMutableTreeNode nextNode = (SanchayMutableTreeNode) getNextSibling();

        if(nextNode != null)
            return nextNode;

        SanchayMutableTreeNode nextParent = (SanchayMutableTreeNode) ((SanchayMutableTreeNode) parent).getNextSibling();

        if(parent == null || nextParent == null)
            return null;

        return (SanchayMutableTreeNode) nextParent.getFirstChild();
    }

    public void shiftLeft()
    {
        SanchayMutableTreeNode leftNode = getLeftNode();
        
        if(leftNode == null || leftNode == getPreviousSibling())
            return;

        parent.remove(this);

        ((SanchayMutableTreeNode) leftNode.getParent()).add(this);
    }

    public void shiftRight()
    {
        SanchayMutableTreeNode rightNode = getRightNode();

        if(rightNode == null || rightNode == getNextSibling())
            return;

        parent.remove(this);

        ((SanchayMutableTreeNode) rightNode.getParent()).insert(this, 0);
    }

//    public int getRequiredColumnCount()
//    {
//	return requiredColumnCount;
//    }
//     
//    public void clearRequiredColumnCounts()
//    {
//	requiredColumnCount = -1;
//	
//	int chcount = getChildCount();
//
//	for (int i = 0; i < chcount; i++)
//	{
//	    SanchayMutableTreeNode child = (SanchayMutableTreeNode) getChildAt(i);
//	    child.clearRequiredColumnCounts();
//	}
//    }
//   
//    public void calculateRequiredColumnCounts()
//    {
//	if(isLeaf() == false)
//	{
//	    Vector leaves = getAllLeaves();
//	    int lcount = leaves.size();
//
//	    if(lcount%2 == 0)
//		lcount++;
//
//	    if(lcount > getRequiredColumnCount())
//	    {
//		requiredColumnCount = lcount;
//		
//		SanchayMutableTreeNode parent = (SanchayMutableTreeNode) getParent();
//		
//		while(parent != null)
//		{
//		    parent.requiredColumnCount++;
//		    parent = (SanchayMutableTreeNode) parent.getParent();
//		}
//	    }
//
//	    int chcount = getChildCount();
//	    
//	    for (int i = 0; i < chcount; i++)
//	    {
//		SanchayMutableTreeNode child = (SanchayMutableTreeNode) getChildAt(i);
//		child.calculateRequiredColumnCounts();
//	    }
//	}
//	else
//	{
//	    requiredColumnCount = 1;
//	}
//    }
    public void setValuesInTable(DefaultTableModel tbl, int mode, boolean reverse) {
        tbl.setValueAt(this, rowIndex, columnIndex);

        if (isLeaf() == false) {
            int chcount = getChildCount();

            for (int i = 0; i < chcount; i++) {
                SanchayMutableTreeNode child = (SanchayMutableTreeNode) getChildAt(i);
                child.setValuesInTable(tbl, mode, reverse);
            }
        }
    }

    public void setValuesInTable(DefaultTableModel tbl, int mode) {
        tbl.setValueAt(this, rowIndex, columnIndex);

        if (isLeaf() == false) {
            int chcount = getChildCount();

            for (int i = 0; i < chcount; i++) {
                SanchayMutableTreeNode child = (SanchayMutableTreeNode) getChildAt(i);
                child.setValuesInTable(tbl, mode);
            }
        }
    }

    public void fillTreeEdges(SanchayJTable jtbl, int mode) {
//	if(requiredColumnCount == -1 || rowIndex == -1 || columnIndex == -1)
//	    return;

        if (isLeaf() == false) {
            int chcount = getChildCount();

            for (int i = 0; i < chcount; i++) {
                SanchayMutableTreeNode child = (SanchayMutableTreeNode) getChildAt(i);
                jtbl.addEdge(new SanchayEdge(this, rowIndex, columnIndex, child, child.rowIndex, child.columnIndex));

                jtbl.setCellObject(rowIndex, columnIndex, child);
                child.fillTreeEdges(jtbl, mode);
            }
        }
    }

    public void fillGraphEdges(SanchayJTable jtbl, int mode) {
//        if (isLeaf() == false) {
//            int chcount = getChildCount();
//
//            for (int i = 0; i < chcount; i++) {
//                SanchayMutableTreeNode child = (SanchayMutableTreeNode) getChildAt(i);
//                jtbl.addEdge(new SanchayEdge(this, rowIndex, columnIndex, child, child.rowIndex, child.columnIndex));
//
//                jtbl.setCellObject(rowIndex, columnIndex, child);
//                child.fillTreeEdges(jtbl, mode);
//            }
//        }

    }
}
