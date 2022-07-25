/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.tree.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.tree.gui.SanchayTreeJPanel;

/**
 *
 * @author anil
 */
public class AttributeValueActionListener implements ActionListener {

    protected SanchayTreeJPanel sanchayTreeJPanel;
    protected String name;
    protected FeatureStructure featureStructure;
    protected DefaultComboBoxModel dcbm;

    public AttributeValueActionListener(SanchayTreeJPanel sanchayTreeJPanel, String name, FeatureStructure featureStructure,
            DefaultComboBoxModel dcbm)
    {
        this.sanchayTreeJPanel = sanchayTreeJPanel;
        this.name = name;
        this.featureStructure = featureStructure;
        this.dcbm = dcbm;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the featureStructure
     */
    public FeatureStructure getFeatureStructure() {
        return featureStructure;
    }

    /**
     * @param featureStructure the featureStructure to set
     */
    public void setFeatureStructure(FeatureStructure featureStructure) {
        this.featureStructure = featureStructure;
    }

    /**
     * @return the dcbm
     */
    public DefaultComboBoxModel getDcbm() {
        return dcbm;
    }

    /**
     * @param dcbm the dcbm to set
     */
    public void setDcbm(DefaultComboBoxModel dcbm) {
        this.dcbm = dcbm;
    }

    public void actionPerformed(ActionEvent e) {
        String val = (String) getDcbm().getSelectedItem();

        if(val.equals("Other"))
        {
            val = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Please_enter_the_attribute_value"), "");

            if(dcbm.getIndexOf(val) == -1)
            {
                dcbm.addElement(val);
            }

            dcbm.setSelectedItem(val);
        }

//        TreePath currentSelection = jtree.getSelectionPath();
//
//        if (currentSelection != null)
//        {
//            SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());

//            if(currentNode.getFeatureStructures().countAltFSValues() == 1)
//            {
//                currentNode.setAttributeValue(name, val);
//            }
//            else
//            {
//                getFeatureStructure().setAttributeValue(getName(),val);
//                sanchayTreeJPanel.editTreeNode(null);
//            }

//            jtree.updateUI();
//            jtree.requestFocusInWindow();
//        }
    }
}
