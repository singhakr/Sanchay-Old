/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.tree.gui.action;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.tree.gui.SanchayTreeJPanel;

/**
 *
 * @author anil
 */
public class NodeFeatureDeleteTreeAction extends AbstractAction {
    JTree jtree;

    protected List attribNames;

    SanchayTreeJPanel sanchayTreeJPanel;

     public NodeFeatureDeleteTreeAction(JTree jtree, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator)
     {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        this.jtree = jtree;
    }

    public NodeFeatureDeleteTreeAction(JTree jtree, String text)
    {
        super(text);

        this.jtree = jtree;
    }

    public NodeFeatureDeleteTreeAction(JTree jtree, String text, List attribNames, SanchayTreeJPanel sanchayTreeJPanel)
    {
        this(jtree, text);

        this.attribNames = attribNames;
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
//                 if(currentNode instanceof SSFPhrase && attribNames.size() > 0)
             if(attribNames.size() > 0)
             {
                 if(FeatureStructuresImpl.getFSProperties().isMandatory(Action.NAME) == false)
                 {
//                       ((SSFPhrase) currentNode).getFeatureStructures().getAltFSValue(0).removeAttribute((String) getValue(Action.NAME));
                   currentNode.getFeatureStructures().getAltFSValue(0).removeAttribute((String) getValue(Action.NAME));
//                        ((DefaultTreeModel) jtree.getModel()).reload(currentNode.getParent());
                    sanchayTreeJPanel.editTreeNode(null);
                    jtree.updateUI();
                 }
             }
//                    jtree.updateUI();
//                }
        }
    }
}
