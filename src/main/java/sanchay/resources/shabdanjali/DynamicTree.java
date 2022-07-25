/* DynamicTree.java
 * Class which creates a DynamicTree.
 *
 * */

package sanchay.resources.shabdanjali;

import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.TreeNode;
//import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.util.*;
import sanchay.GlobalProperties;
import sanchay.tree.*;

/**
 *
 *  @author Bharat Ram Ambati
 */


public class DynamicTree extends JPanel {
	protected SanchayMutableTreeNode rootNode;
	protected SanchayTreeModel treeModel;
	protected JTree tree;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();

	public DynamicTree() {
		super(new GridLayout(1,0));

		rootNode = new SanchayMutableTreeNode(GlobalProperties.getIntlString("Root_Node"));
		treeModel = new SanchayTreeModel(rootNode);

		tree = new JTree(treeModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode
			(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);

		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}

	/**Sets the name of the ROOT node*/
	public void setRootName(String word) {
		rootNode.setUserObject(GlobalProperties.getIntlString("Word:_") + word);
	}
	/** Remove all nodes except the root node. */
	public void clear() {
		rootNode.removeAllChildren();
		treeModel.reload();
	}

	/** Saves the tee */
	public List<DictEntry> save() {
		List<DictEntry> entryList = new ArrayList<DictEntry>();
		String word,pos,mean,egsentence;

		word = rootNode.getUserObject().toString();

		int poschildCount = rootNode.getChildCount();
		SanchayMutableTreeNode egNode,meanNode,posNode;
		for (int i=0; i<poschildCount; i++) {

			posNode = (SanchayMutableTreeNode) rootNode.getChildAt(i);
			pos = posNode.getUserObject().toString();
			pos = pos.substring(pos.indexOf(" ")+1);

			DictEntry dictEntry = new DictEntry();
			dictEntry.addWord(word);
			dictEntry.addPOS(pos);

			int meanChildCount = posNode.getChildCount();
			for (int j=0; j<meanChildCount; j++) {
				meanNode = (SanchayMutableTreeNode) posNode.getChildAt(j);
				mean = meanNode.getUserObject().toString();
				mean = mean.substring(mean.indexOf(" ")+1);
				dictEntry.addMeaning(mean);
				int egChildCount = meanNode.getChildCount();
				for (int k=0; k<egChildCount; k++) {
					egNode = (SanchayMutableTreeNode) meanNode.getChildAt(k);
					egsentence = egNode.getUserObject().toString();
					egsentence = egsentence.substring(egsentence.indexOf(" ")+1);
					dictEntry.addExample(egsentence);
				}
			}
			entryList.add(dictEntry);
		}
		return entryList;
	}


	/** Expands the tree */
	public void expandall() {
		TreePath rootPath = new TreePath(rootNode.getPath());
		int poschildCount = rootNode.getChildCount();
		SanchayMutableTreeNode currentNode,parentNode;
		expand(rootPath);
		for (int i=0; i<poschildCount; i++) {
			parentNode = (SanchayMutableTreeNode) rootNode.getChildAt(i);
			TreePath parentPath = new TreePath(parentNode.getPath());
			expand(parentPath);
			int meanChildCount = parentNode.getChildCount();
			for (int j=0; j<meanChildCount; j++) {
				currentNode = (SanchayMutableTreeNode) parentNode.getChildAt(j);
				TreePath childPath = new TreePath(currentNode.getPath());
				expand(childPath);
			}
		}
	}

	/** Expands the current node*/
	public void expand(TreePath path) {
		tree.expandPath(path);
	}

	/** Remove the currently selected node. */
	public void removeCurrentNode() {
		TreePath currentSelection = tree.getSelectionPath();
		System.out.println(GlobalProperties.getIntlString("Path_is_") + currentSelection);
		if (currentSelection != null) {
			SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode)
				(currentSelection.getLastPathComponent());
			MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
			if (parent != null) {
				treeModel.removeNodeFromParent(currentNode);
				return;
			}
		} 

		// Either there was no selection, or the root was selected.
		toolkit.beep();
	}

	/** Add child to the currently selected node. */
	public SanchayMutableTreeNode addObject(Object child) {
		SanchayMutableTreeNode parentNode = null;
		TreePath parentPath = tree.getSelectionPath();

		if (parentPath == null) {
			parentNode = rootNode;
		} else {
			parentNode = (SanchayMutableTreeNode)
				(parentPath.getLastPathComponent());
		}

		return addObject(parentNode, child, true);
	}

	public SanchayMutableTreeNode addObject(SanchayMutableTreeNode parent,
			Object child) {
		return addObject(parent, child, false);
	}

	public SanchayMutableTreeNode addObject(SanchayMutableTreeNode parent,
			Object child, 
			boolean shouldBeVisible) {
		SanchayMutableTreeNode childNode = 
			new SanchayMutableTreeNode(child);

		if (parent == null) {
			parent = rootNode;
		}

		//It is key to invoke this on the TreeModel, and NOT SanchayMutableTreeNode
		treeModel.insertNodeInto(childNode, parent, 
				parent.getChildCount());

		//Make sure the user can see the lovely new node.
		if (shouldBeVisible) {
			tree.scrollPathToVisible(new TreePath(childNode.getPath()));
		}
		return childNode;
	}

	class MyTreeModelListener implements TreeModelListener {
		public void treeNodesChanged(TreeModelEvent e) {
			SanchayMutableTreeNode node;
			node = (SanchayMutableTreeNode)(e.getTreePath().getLastPathComponent());

			/*
			 * If the event lists children, then the changed
			 * node is the child of the node we've already
			 * gotten.  Otherwise, the changed node and the
			 * specified node are the same.
			 */

			int index = e.getChildIndices()[0];
			node = (SanchayMutableTreeNode)(node.getChildAt(index));

			System.out.println(GlobalProperties.getIntlString("The_user_has_finished_editing_the_node."));
			System.out.println(GlobalProperties.getIntlString("New_value:_") + node.getUserObject());
		}
		public void treeNodesInserted(TreeModelEvent e) {
		}
		public void treeNodesRemoved(TreeModelEvent e) {
		}
		public void treeStructureChanged(TreeModelEvent e) {
		}
	}
}
