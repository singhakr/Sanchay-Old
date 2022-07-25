/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.tree.gui.action;

import java.awt.event.ActionEvent;
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
public class NodeMandatoryFeatureValueTreeAction extends AbstractAction {
    JTree jtree;
    String fname;

    SanchayTreeJPanel sanchayTreeJPanel;

     public NodeMandatoryFeatureValueTreeAction(JTree jtree, String fname, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator)
     {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        this.jtree = jtree;
        this.fname = fname;
    }

    public NodeMandatoryFeatureValueTreeAction(JTree jtree, String fname, String text)
    {
        super(text);

        this.jtree = jtree;
        this.fname = fname;
    }

    public NodeMandatoryFeatureValueTreeAction(JTree jtree, String fname, String text, SanchayTreeJPanel sanchayTreeJPanel)
    {
        this(jtree, fname, text);

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
                String val = (String) getValue(Action.NAME);

                if(val.equalsIgnoreCase(GlobalProperties.getIntlString("other")))
                {
                    val = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Please_enter_the_attribute_value"), "");
                }

                if(val != null)
                {
                    currentNode.setAttributeValue(fname, val);
                    sanchayTreeJPanel.editTreeNode(null);
                    jtree.updateUI();
                }
//                }
        }
    }
}
