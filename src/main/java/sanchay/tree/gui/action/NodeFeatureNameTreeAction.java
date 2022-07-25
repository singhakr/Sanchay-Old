/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.tree.gui.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.tree.gui.SanchayTreeJPanel;

/**
 *
 * @author anil
 */
public class NodeFeatureNameTreeAction extends AbstractAction {

    JTree jtree;
    SanchayTreeJPanel sanchayTreeJPanel;

     public NodeFeatureNameTreeAction(JTree jtree, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator)
     {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        this.jtree = jtree;
    }

    public NodeFeatureNameTreeAction(JTree jtree, String text)
    {
        super(text);

        this.jtree = jtree;
    }

    public NodeFeatureNameTreeAction(JTree jtree, String text, SanchayTreeJPanel sanchayTreeJPanel)
    {
        this(jtree, text);

        this.sanchayTreeJPanel = sanchayTreeJPanel;
    }

    public void actionPerformed(ActionEvent e)
    {
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());

    //                if(mode == SanchayTreeJPanel.DEFAULT_MODE)
    //                {
    ////                    nodeLabelJComboBox.setEnabled(false);
    //                }
    //                else if(mode == SanchayTreeJPanel.SSF_MODE)
    //                {
    //                    if(currentNode instanceof SSFPhrase)
    //                    {
    //                        ((SSFPhrase) currentNode).setAttributeValue((String) getValue(Action.NAME), "?");
                    currentNode.setAttributeValue((String) getValue(Action.NAME), "?");
    //                        ((DefaultTreeModel) jtree.getModel()).reload(currentNode.getParent());
                    sanchayTreeJPanel.editTreeNode(null);
                    jtree.updateUI();
    //                    }
    //                    jtree.updateUI();
    //                }
        }
    }
}
