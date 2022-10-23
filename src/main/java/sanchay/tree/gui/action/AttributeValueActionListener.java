/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.tree.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.gui.common.SanchayJOptionPane;
import sanchay.gui.common.SanchayLanguages;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class AttributeValueActionListener implements ActionListener {

    protected SanchayTreeJPanel sanchayTreeJPanel;
    protected String name;
    protected FeatureStructure featureStructure;
    protected DefaultComboBoxModel dcbm;
    
    private String langEnc = "hin::utf8";

    public AttributeValueActionListener(SanchayTreeJPanel sanchayTreeJPanel, String name, FeatureStructure featureStructure,
            DefaultComboBoxModel dcbm, String langEnc)
    {
        this.sanchayTreeJPanel = sanchayTreeJPanel;
        this.name = name;
        this.featureStructure = featureStructure;
        this.dcbm = dcbm;
        
        this.langEnc = langEnc;
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
            JTextField inputField = new JTextField("");
            
            Locale locale = sanchayTreeJPanel.getLocale();
            
            SanchayLanguages.changeInputMethod(inputField, locale);
//            inputField.getInputContext().

            UtilityFunctions.setComponentFont(inputField, langEnc);
            
            int result = JOptionPane.showConfirmDialog(sanchayTreeJPanel, inputField, GlobalProperties.getIntlString("Please_enter_the_attribute_value"), JOptionPane.PLAIN_MESSAGE);
//            val = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Please_enter_the_attribute_value"), inputField);
//            val = SanchayJOptionPane.showInternalInputDialog(sanchayTreeJPanel, GlobalProperties.getIntlString("Please_enter_the_attribute_value"), langEnc);
            
            if (result == JOptionPane.OK_OPTION) {
                String inputValue = inputField.getText();

                if(dcbm.getIndexOf(inputValue) == -1)
                {
                    dcbm.addElement(inputValue);
                }

                dcbm.setSelectedItem(inputValue);
            }

//            if(dcbm.getIndexOf(val) == -1)
//            {
//                dcbm.addElement(val);
//            }
//
//            dcbm.setSelectedItem(val);
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
