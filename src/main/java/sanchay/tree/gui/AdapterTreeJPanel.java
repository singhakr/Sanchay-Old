/*
 * AdapterTreeJPanel.java
 *
 * Created on May 16, 2006, 5:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import sanchay.GlobalProperties;
import sanchay.gui.common.*;
import sanchay.server.dto.tree.ExplorableTreeNode;

/**
 *
 * @author anil
 */
public abstract class AdapterTreeJPanel extends javax.swing.JPanel {
    
//    protected final JTree tree = new JTree(createTreeModel());
    protected final JTree tree = new JTree();
//      protected JTree tree = new JTree();
////    protected JTree tree = null;
    protected TreeModel model;
    
    public abstract TreeModel createTreeModel();
//    public abstract TreeModel createTreeModel(File rootFile);
//    public abstract TreeModel createTreeModel(ChannelSftp.LsEntry remoteRootFile);
    
    public void createTree(String startPath) {
//	final JTree tree = new JTree(createTreeModel());
	JScrollPane scrollPane = new JScrollPane(tree);
	
	if(getLayout() != null && (getLayout() instanceof BorderLayout) == false)
	    setLayout(new BorderLayout());

	add(scrollPane, BorderLayout.CENTER);
	
	tree.addTreeExpansionListener(new TreeExpansionListener() {
	    
	    public void treeCollapsed(TreeExpansionEvent e) {
		// don't care about collapse events
	    }
	    
	    public void treeExpanded(TreeExpansionEvent e) {
		UpdateStatus updateThread;
		TreePath path = e.getPath();
		ExplorableTreeNode node = (ExplorableTreeNode) path.getLastPathComponent();
		
		if( ! node.isExplored()) {
		    model = (DefaultTreeModel)tree.getModel();
		    GJApp.updateStatus(GlobalProperties.getIntlString("Exploring_..."));
		    
		    UpdateStatus us = new UpdateStatus();
		    us.start();
		    
		    node.explore();
		    ((DefaultTreeModel) model).nodeStructureChanged(node);
		}
	    }
	    
	    class UpdateStatus extends Thread {
		public void run() {
		    try { Thread.currentThread().sleep(450); } catch(InterruptedException e) { }
		    
		    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    GJApp.updateStatus(" ");
			}
		    });
		}
	    }
	});
    }
    
    public TreeModel getTreeModel()
    {
	return model;
    }
    
    public JTree getJTree()
    {
	return tree;
    }
}

class ThreeDPanel extends Panel
{
    public void paint(Graphics g) {
	Dimension sz = getSize();
	g.setColor(Color.lightGray);
	g.draw3DRect(0, 0, sz.width-1, sz.height-1, true);
    }
}
