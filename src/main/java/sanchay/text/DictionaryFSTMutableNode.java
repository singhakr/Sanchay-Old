/*
 * DictionaryFSTMutableNode.java
 *
 * Created on April 6, 2006, 9:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text;

import java.io.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;

import sanchay.tree.*;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class DictionaryFSTMutableNode extends SanchayMutableTreeNode
        implements MutableTreeNode, Serializable
{
    protected String label;
    
    /** Creates a new instance of DictionaryFSTMutableNode */
    public DictionaryFSTMutableNode(DictionaryFSTNode node, String lbl)
    {
	super();
        setUserObject(node);
	addNode(this);
        label = lbl;
    }

    public DictionaryFSTMutableNode(DictionaryFSTNode node)
    {
	super();
        setUserObject(node);
	addNode(this);
    }
    
    public DictionaryFSTNode getDictionaryFSTNode()
    {
        return (DictionaryFSTNode) getUserObject();
    }
    
    public String getLabel()
    {
        if(label != null && label.equals("") == false)
            return label;
        
        return ((DictionaryFSTNode) getUserObject()).getString();
    }
    
    public void addNode(DictionaryFSTMutableNode node)
    {
//	if(node.getString() == null || node.getString().equals(""))
//	    setUserObject(node.ge);
//	else
//	    setUserObject(node.getString());
	DictionaryFSTNode dictionaryFSTNode = getDictionaryFSTNode();
        
	Enumeration enm = dictionaryFSTNode.getChildren();
	
	if(enm == null)
	    return;
	
	while(enm.hasMoreElements())
	{
	    String key = (String) enm.nextElement();
	    DictionaryFSTNode child = dictionaryFSTNode.getChild(key);

	    DictionaryFSTMutableNode mchild = new DictionaryFSTMutableNode(child);
	    add(mchild);
	}
    }
    
    public String toString()
    {
        if(getDictionaryFSTNode().isReverse())
            return UtilityFunctions.reverseString(getLabel());
        
        return getLabel();
    }
}
