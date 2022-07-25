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
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.tree.gui.SanchayTreeJPanel;

/**
 *
 * @author anil
 */
public class NodeFeatureReferenceTreeAction extends AbstractAction {
    JTree jtree;
    List namedNodes;

    SanchayTreeJPanel sanchayTreeJPanel;

     public NodeFeatureReferenceTreeAction(JTree jtree, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator, List namedNodes)
     {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        this.jtree = jtree;
        this.namedNodes = namedNodes;
    }

    public NodeFeatureReferenceTreeAction(JTree jtree, String text, List namedNodes)
    {
        super(text);

        this.jtree = jtree;
        this.namedNodes = namedNodes;
    }

    public NodeFeatureReferenceTreeAction(JTree jtree, String text, List namedNodes, SanchayTreeJPanel sanchayTreeJPanel)
    {
        this(jtree, text, namedNodes);

        this.sanchayTreeJPanel = sanchayTreeJPanel;
    }

    public void actionPerformed(ActionEvent e)
    {
        if(namedNodes == null || namedNodes.size() == 0)
            return;

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
//                 if(currentNode instanceof SSFPhrase)
//                 {
                SSFNode selectedValue = (SSFNode) JOptionPane.showInputDialog(null,
                    GlobalProperties.getIntlString("Select_the_referent_node"), GlobalProperties.getIntlString("Attibute_Referent"), JOptionPane.INFORMATION_MESSAGE, null,
                    namedNodes.toArray(), namedNodes.toArray()[0]);

                if(selectedValue == null)
                    return;

//                    String prevValue = ((SSFPhrase) currentNode).getAttributeValue((String) getValue(Action.NAME));
                String prevValue = currentNode.getAttributeValue((String) getValue(Action.NAME));

                if(prevValue != null && prevValue.equals("") == false)
                {
                    String parts[] = prevValue.split(":");

//                        ((SSFPhrase) currentNode).setAttributeValue((String) getValue(Action.NAME), parts[0] + ":" + selectedValue.getAttributeValue("name"));
                    currentNode.setAttributeValue((String) getValue(Action.NAME), parts[0] + ":" + selectedValue.getAttributeValue(GlobalProperties.getIntlString("name")));
//                        ((DefaultTreeModel) jtree.getModel()).reload(currentNode.getParent());
                    sanchayTreeJPanel.editTreeNode(null);
                    jtree.updateUI();
                }
//                 }
//                    jtree.updateUI();
//                }
        }
    }
}
