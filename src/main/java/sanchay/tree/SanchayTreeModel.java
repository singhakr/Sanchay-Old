/*
 * SanchayTree.java
 *
 * Created on October 7, 2005, 6:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree;

import java.io.*;
import javax.swing.tree.*;

/**
 *
 *  @author Anil Kumar Singh
 */
public class SanchayTreeModel extends DefaultTreeModel
        implements Serializable, TreeModel
{
    
    /** Creates a new instance of SanchayTree */
    public SanchayTreeModel(TreeNode root) {
        super(root);
    }
    
    public SanchayTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }
}
