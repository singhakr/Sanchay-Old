/*
 * TreeAction.java
 *
 * Created on October 22, 2005, 6:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree.gui.action;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import sanchay.GlobalProperties;
import sanchay.tree.gui.*;

/**
 *
 *  @author Anil Kumar Singh
 */
public class TreeAction extends AbstractAction {
    protected int command;
    protected SanchayTreeJPanel currentTreeJPanel;
    
    public static final int UNDO = 0;
    public static final int REDO = 1;
    
    public static final int EDIT_TREE = 2;
    public static final int EDIT_NODE = 3;
    public static final int EDIT_LABEL = 4;
    public static final int EDIT_NODE_TEXT = 5;
    public static final int SELECT_FS = 6;

    public static final int SAVE_TREE = 7;
    public static final int SAVE_TREE_AS = 8;
    
    public static final int RESET_ALL = 9;
    public static final int CLEAR_ALL = 10;
    public static final int CLEAR_NODE = 11;
    
    public static final int ADD_CHILD = 12;
    public static final int INSERT_NODE = 13;

    public static final int JOIN_NODES = 14;
    public static final int SPLIT_NODE = 15;
    
    public static final int ADD_LAYER = 16;
    public static final int DEL_LAYER = 17;
    public static final int DEL_SUBTREE = 18;
    public static final int SHIFT_LEFT = 19;
    public static final int SHIFT_RIGHT = 20;

//    public static final int LOCAL_NODE_STATISTICS = 21;
    public static final int GLOBAL_NODE_STATISTICS = 21;

    public static final int COPY_NODE = 22;
    public static final int CUT_NODE = 23;
    public static final int PASTE_NODE = 24;

    public static final int EXPAND_ALL = 25;
    public static final int COLLAPSE_ALL = 26;

    public static final int TREE_PRINT = 27;

    public static final int VIEW_TREE = 28; // A 'real' tree-like view
    
    // Some more specialised actions
    public static final int VIEW_GDEPS = 29; // A 'real' tree-like view of group dependencies
    public static final int VIEW_LDEPS = 30; // A 'real' tree-like view of leaf (lexical) dependencies
    public static final int VIEW_PS = 31; // A 'real' tree-like view of phrase structure

    public static final int SELECT_INPUT_METHOD = 32;
    public static final int SHOW_KB_MAP = 33;

    public static final int SET_TAG_LEVELS = 34;

    public static final int SHOW_CONTROL_TABS = 35;

    // Total number of actions available
    public static final int _TOTAL_ACTIONS_ = 36;
    
    public static UndoManager undo;
    public static UndoAction undoAction;
    public static RedoAction redoAction;

    /**
     * @return the command
     */
    public int getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(int command) {
        this.command = command;
    }
    
    /** Creates a new instance of TreeAction */
    public TreeAction(SanchayTreeJPanel treeJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);
        
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentTreeJPanel = treeJPanel;
    }

    public TreeAction(SanchayTreeJPanel treeJPanel, String text) {
        super(text);

        currentTreeJPanel = treeJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
    
    }
    
    public static TreeAction createAction(SanchayTreeJPanel jpanel, int mode)
    {
	TreeAction act = null;
	
	switch(mode)
	{
	    case UNDO:
		act = new UndoAction(jpanel, GlobalProperties.getIntlString("Undo"));
		
		act.setEnabled(false);
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Undo_an_action."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
		undoAction = (UndoAction) act;
        act.setCommand(UNDO);
		return act;

	    case REDO:
		act = new RedoAction(jpanel, GlobalProperties.getIntlString("Redo"));
		
		act.setEnabled(false);
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Redo_an_action."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		redoAction = (RedoAction) act;
        act.setCommand(REDO);
		return act;

	    case EDIT_TREE:
		String lbl = "";
		if(jpanel.getJTree().isEditable())
		    lbl = GlobalProperties.getIntlString("Edit_Off");
		else
		    lbl = GlobalProperties.getIntlString("Edit_On");

		act = new TreeAction(jpanel, lbl) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.editTree(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Toggle_edit_mode."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
        act.setCommand(EDIT_TREE);
		return act;
		
	    case EDIT_LABEL:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Edit_Label")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.editTreeNodeLabel(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_a_node's_label."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
        act.setCommand(EDIT_LABEL);
		return act;
		
	    case EDIT_NODE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Edit_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.editTreeNode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_a_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
        act.setCommand(EDIT_NODE);
		return act;
		
	    case EDIT_NODE_TEXT:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Edit_Node_Text")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.editTreeNodeText(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_node_text."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
        act.setCommand(EDIT_NODE_TEXT);
		return act;

	    case SELECT_FS:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Select_FS")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.selectNodeFS(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_one_of_the_feature_structures_that_is_to_be_retained."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
        act.setCommand(SELECT_FS);
		return act;
		
	    case SAVE_TREE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Save_Tree")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.saveTree(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_tree."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
        act.setCommand(SAVE_TREE);
		return act;
		
	    case SAVE_TREE_AS:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Save_Tree_As...")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.saveTreeAs(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_tree_as..."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
        act.setCommand(SAVE_TREE_AS);
		return act;
		
	    case RESET_ALL:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Reset_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.treeResetAll(e);
		    }
		};
		
        act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Reset_everything."));
        act.setCommand(RESET_ALL);
		return act;
		
	    case CLEAR_ALL:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Clear_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.treeNodeClear(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Clear_everything."));
        act.setCommand(CLEAR_ALL);
		return act;
		
	    case CLEAR_NODE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Clear_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.treeNodeClear(e);
		    }
		};
		
        act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Clear_a_node."));
        act.setCommand(CLEAR_NODE);
		return act;
		
	    case ADD_CHILD:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Add_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.addTreeNode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_a_child_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
        act.setCommand(ADD_CHILD);
		return act;
		
	    case INSERT_NODE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Insert_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.insertTreeNode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Insert_a_node_before_the_selected_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
        act.setCommand(INSERT_NODE);
		return act;

	    case JOIN_NODES:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Join_Nodes")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.joinTreeNodes(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Join_the_selected_nodes_into_one_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_J));
        act.setCommand(JOIN_NODES);
		return act;

	    case SPLIT_NODE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Split_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.splitTreeNode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Split_the_selected_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
        act.setCommand(SPLIT_NODE);
		return act;

	    case ADD_LAYER:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Add_Layer")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.addTreeNodeLayer(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Join_the_selected_nodes_under_a_new_parent."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
        act.setCommand(ADD_LAYER);
		return act;

	    case DEL_LAYER:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Delete_Layer")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.deleteTreeNodeLayer(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Delete_a_node_and_move_up_it's_children."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
        act.setCommand(DEL_LAYER);
		return act;

	    case DEL_SUBTREE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Delete_Subtree")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.deleteSubTree(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Delete_a_subtree."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
        act.setCommand(DEL_SUBTREE);
		return act;

	    case SHIFT_LEFT:
		act = new TreeAction(jpanel, "Shift Left") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.shiftNodeLeft(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "Shift the node to the previous phrase");
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
        act.setCommand(SHIFT_LEFT);
		return act;

	    case SHIFT_RIGHT:
		act = new TreeAction(jpanel, "Shift Right") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.shiftNodeRight(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "Shift the node to the next phrase");
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
        act.setCommand(SHIFT_RIGHT);
		return act;

//	    case LOCAL_NODE_STATISTICS:
//		act = new TreeAction(jpanel, "Local Node Statistics") {
//		    public void actionPerformed(ActionEvent e) {
//			this.currentTreeJPanel.globalNodeStats(e);
//		    }
//		};
//
//		act.putValue(SHORT_DESCRIPTION, "View local statistics for the selected node.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
//                act.setCommand(LOCAL_NODE_STATISTICS);
//		return act;

	    case GLOBAL_NODE_STATISTICS:
		act = new TreeAction(jpanel, "Global Node Statistics") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.globalNodeStats(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "View global (stored) statistics for the selected node.");
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
                act.setCommand(GLOBAL_NODE_STATISTICS);
		return act;

	    case COPY_NODE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Copy_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.copyTreeNode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Copy_a_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
        act.setCommand(COPY_NODE);
		return act;

	    case CUT_NODE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Cut_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.cutTreeNode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Cut_a_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
        act.setCommand(CUT_NODE);
		return act;

	    case PASTE_NODE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Paste_Node")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.pasteTreeNode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Paste_a_node."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
        act.setCommand(PASTE_NODE);
		return act;

	    case EXPAND_ALL:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Expand_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.expandAll(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Expand_the_tree."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
        act.setCommand(EXPAND_ALL);
		return act;

	    case COLLAPSE_ALL:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Collapse_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.collapseAll(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Collapse_the_tree."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
        act.setCommand(COLLAPSE_ALL);
		return act;

	    case TREE_PRINT:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Print")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.printTree(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Print_the_tree."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
        act.setCommand(TREE_PRINT);
		return act;

	    case VIEW_TREE:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("View_Tree")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.viewTree(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("See_a_tree-like_view."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_W));
        act.setCommand(VIEW_TREE);
		return act;

	    case VIEW_GDEPS:
		act = new TreeAction(jpanel, "Group Dependencies") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.viewGroupDependencies(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, "View the group (chunk) dependencies");
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
        act.setCommand(VIEW_GDEPS);
		return act;

	    case VIEW_LDEPS:
		act = new TreeAction(jpanel, "Leaf Dependencies") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.viewLeafDependencies(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "View the leaf (lexical) depedencies");
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
        act.setCommand(VIEW_LDEPS);
		return act;

        case VIEW_PS:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("View_Phrase_Structure")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.viewPS(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("View_the_phrase_structure."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
        act.setCommand(VIEW_PS);
		return act;

	    case SELECT_INPUT_METHOD:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Input_Method")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.selectInputMethod(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_input_method."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
        act.setCommand(SELECT_INPUT_METHOD);
		return act;

	    case SHOW_KB_MAP:
		act = new TreeAction(jpanel, GlobalProperties.getIntlString("Show_Keyboard")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.showKBMap(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_the_keyboard_map."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
        act.setCommand(SHOW_KB_MAP);
		return act;                

	    case SET_TAG_LEVELS:
		act = new TreeAction(jpanel, "Hierachical Tags") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.setTagLevels(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "Set Hierachical tag levels");
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
                act.setCommand(SET_TAG_LEVELS);
		return act;

	    case SHOW_CONTROL_TABS:
		act = new TreeAction(jpanel, "Show Control Tabs") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTreeJPanel.showControlTabs(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, "Show or hide the editing control tabs.");
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
                act.setCommand(SHOW_CONTROL_TABS);
		return act;
	}
	
	return act;
    }

   static class UndoAction extends TreeAction {
       
       protected UndoManager undo;
       
       public UndoAction(SanchayTreeJPanel treeJPanel, String text)
       {
	   super(treeJPanel, text);
       }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                System.out.println(GlobalProperties.getIntlString("Unable_to_undo:_") + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            TreeAction.redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, GlobalProperties.getIntlString("Undo"));
            }
        }
    }

    static class RedoAction extends TreeAction {
       
       protected UndoManager undo;
       
	public RedoAction(SanchayTreeJPanel treeJPanel, String text)
	{
	   super(treeJPanel, text);
	}
	
        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println(GlobalProperties.getIntlString("Unable_to_redo:_") + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            TreeAction.undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, GlobalProperties.getIntlString("Redo"));
            }
        }
    }
}
