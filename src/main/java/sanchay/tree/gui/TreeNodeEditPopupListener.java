/*
 * TreeNodeEditPopupListener.java
 *
 * Created on October 13, 2008, 12:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructureImpl;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.table.SanchayTableModel;
import sanchay.tree.gui.action.NodeEditingTreeAction;
import sanchay.tree.gui.action.NodeFeatureDeleteTreeAction;
import sanchay.tree.gui.action.NodeFeatureNameTreeAction;
import sanchay.tree.gui.action.NodeFeatureReferenceTreeAction;
import sanchay.tree.gui.action.NodeFeatureValueTreeAction;
import sanchay.tree.gui.action.NodeMandatoryFeatureValueTreeAction;

/**
 *
 * @author eklavya
 */
public class TreeNodeEditPopupListener extends MouseAdapter {
     protected JPopupMenu popup;
     protected JMenu nodeEditingJMenu;
     protected JMenu featureNameEditingJMenu;
     protected JMenu featureValueEditingJMenu;
     protected JMenu mandatoryFeatureValueEditingJMenu;
     protected JMenu featureReferentEditingJMenu;
     protected JMenu featureDeleteJMenu;
     
     protected JTree jtree;
     protected Hashtable nodeLabelEditors;
     protected SanchayTableModel fsSchema;

     protected String[] featureValueStrings;
     
     protected SanchayTreeJPanel sanchayTreeJPanel;
     protected List namedNodes;
     protected List attribNames;

     protected boolean propbankMode;
   
    /** Creates a new instance of TreeNodeEditPopupListener */
     public TreeNodeEditPopupListener(JTree jtree, JPopupMenu pm, Hashtable nodeLabelEditors, SanchayTableModel fsSchema) {
        popup = pm;
        this.jtree = jtree;
        this.nodeLabelEditors = nodeLabelEditors;
        
        this.fsSchema = fsSchema;

        nodeEditingJMenu = new JMenu(GlobalProperties.getIntlString("Node_Name"));
        featureNameEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Name"));
        featureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Value"));

        mandatoryFeatureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Mandatory_Attribute_Value"));
        featureReferentEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Referent"));
        featureDeleteJMenu = new JMenu(GlobalProperties.getIntlString("Delete_Attribute"));
    }

     public TreeNodeEditPopupListener(JPopupMenu pm, SanchayTableModel fsSchema) {
        popup = pm;

        this.fsSchema = fsSchema;

        nodeEditingJMenu = new JMenu(GlobalProperties.getIntlString("Node_Name"));
        featureNameEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Name"));
        featureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Value"));
        mandatoryFeatureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Mandatory_Attribute_Value"));
        featureReferentEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Referent"));
        featureDeleteJMenu = new JMenu(GlobalProperties.getIntlString("Delete_Attribute"));
     }

    public void setPropbankMode(boolean m)
    {
        propbankMode = m;
    }

    private void initMenu()
    {
//        sanchayTreeJPanel.prepareCommands(SanchayTreeJPanel.getSSFPhraseJPanelCommands(), SanchayTreeJPanel.DEFAULT_MODE);

//        if(popup.getComponentZOrder(nodeEditingJMenu) != -1)
        if(nodeEditingJMenu != null)
        {
            nodeEditingJMenu.setVisible(false);
            nodeEditingJMenu.removeAll();
        }
//            popup.remove(nodeEditingJMenu);

//        if(popup.getComponentZOrder(featureNameEditingJMenu) != -1)
        if(featureNameEditingJMenu != null)
        {
            featureNameEditingJMenu.setVisible(false);
            featureNameEditingJMenu.removeAll();
        }
//            popup.remove(featureNameEditingJMenu);

//        if(popup.getComponentZOrder(featureValueEditingJMenu) != -1)
        if(featureValueEditingJMenu != null)
        {
            featureValueEditingJMenu.setVisible(false);
            featureValueEditingJMenu.removeAll();
        }
//            popup.remove(featureValueEditingJMenu);

//        if(popup.getComponentZOrder(mandatoryFeatureValueEditingJMenu) != -1)
        if(mandatoryFeatureValueEditingJMenu != null)
        {
            mandatoryFeatureValueEditingJMenu.setVisible(false);
            mandatoryFeatureValueEditingJMenu.removeAll();
        }
//            popup.remove(mandatoryFeatureValueEditingJMenu);

//        if(popup.getComponentZOrder(featureReferentEditingJMenu) != -1)
        if(featureReferentEditingJMenu != null)
        {
            featureReferentEditingJMenu.setVisible(false);
            featureReferentEditingJMenu.removeAll();
        }
//            popup.remove(featureReferentEditingJMenu);

//        if(popup.getComponentZOrder(featureDeleteJMenu) != -1)
        if(featureDeleteJMenu != null)
        {
            featureDeleteJMenu.setVisible(false);
            featureDeleteJMenu.removeAll();
        }
//            popup.remove(featureDeleteJMenu);

        popup.validate();

//        nodeEditingJMenu = new JMenu("Node Name");
//        featureNameEditingJMenu = new JMenu("Attribute Name");
//        featureValueEditingJMenu = new JMenu("Attribute Value");
//        featureReferentEditingJMenu = new JMenu("Attribute Referent");
//        featureDeleteJMenu = new JMenu("Delete Attribute");
    }
     
    public void setJTree(JTree jtree)
    {
        this.jtree = jtree;        
    }
     
    public void setSanchayTreeJPanel(SanchayTreeJPanel sanchayTreeJPanel)
    {
        this.sanchayTreeJPanel = sanchayTreeJPanel;        
    }
     
    public void setNodeLabelEditors(Hashtable nodeLabelEditors)
    {
        this.nodeLabelEditors = nodeLabelEditors;
    }
     
    public void setNodeEditingJMenu(JMenu pm)
    {
        this.nodeEditingJMenu = pm;        
    }

    public void mouseClicked(MouseEvent e) {
//        showPopup(e);
    }

    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        if(File.separator.equalsIgnoreCase("\\"))
            showPopup(e);
    }

    private void showPopup(MouseEvent e) {

        if(propbankMode)
            return;

        initMenu();
        
        if (e.isPopupTrigger())
        {
            TreePath paths[] = jtree.getSelectionPaths();
            
            if(jtree != null && nodeLabelEditors != null && (paths == null || paths.length == 1))
            {
                Point p = e.getPoint();
                TreePath currentSelection = jtree.getPathForLocation(e.getX(), e.getY());

                try
                {
                    if (currentSelection != null)
                    {
                        jtree.cancelEditing();
                        jtree.setSelectionPath(currentSelection);

                        SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());
                        DefaultComboBoxModel nodeLabelEditor = null;

//                        if(currentNode instanceof SSFPhrase)
//                        {
//                            nodeLabelEditor = (DefaultComboBoxModel) nodeLabelEditors.get("PhraseNames");
                            
                            String featureNames = (String) fsSchema.getValue(GlobalProperties.getIntlString("ColumnName"), GlobalProperties.getIntlString("Feature"), GlobalProperties.getIntlString("EnumValues"));
                            String featureValues = (String) fsSchema.getValue(GlobalProperties.getIntlString("ColumnName"), GlobalProperties.getIntlString("Value"), GlobalProperties.getIntlString("EnumValues"));
                            
                            String featureNameStrings[] = featureNames.split("::");
//                            featureValueStrings = featureValues.split("::");
                            
//                            String featureValueStringsOther[] = new String[featureValueStrings.length + 1];
//
//                            featureValueStringsOther[0] = "Other";
//
//                            for (int i = 1; i < featureValueStringsOther.length; i++)
//                            {
//                                featureValueStringsOther[i] = featureValueStrings[i - 1];
//                            }
                            
//                            featureValueStrings = featureValueStringsOther;

                            namedNodes = ((SSFPhrase) currentNode.getRoot()).getNodesForAttrib(GlobalProperties.getIntlString("name"), true);
                           
                            int count = featureNameStrings.length;

                            for (int i = 0; i < count; i++)
                            {
                                featureNameEditingJMenu.add(new JMenuItem(new NodeFeatureNameTreeAction(jtree, featureNameStrings[i], sanchayTreeJPanel)));
//                                featureValueEditingJMenu.add(new JMenuItem(new NodeFeatureValueTreeAction(jtree, featureNameStrings[i], featureValueStrings)));
                                featureReferentEditingJMenu.add(new JMenuItem(new NodeFeatureReferenceTreeAction(jtree, featureNameStrings[i], namedNodes, sanchayTreeJPanel)));
                            }                            
                            
                            attribNames = currentNode.getAttributeNames();

                            if(attribNames != null)
                            {
                                count = attribNames.size();

                                for (int i = 0; i < count; i++)
                                {
                                    if(FeatureStructuresImpl.getFSProperties().isMandatory((String) attribNames.get(i)) == false) {
                                        featureDeleteJMenu.add(new JMenuItem(new NodeFeatureDeleteTreeAction(jtree, (String) attribNames.get(i), attribNames, sanchayTreeJPanel)));
                                    }
                                }
                            }
//                        }
//                          else
//                            nodeLabelEditor = (DefaultComboBoxModel) nodeLabelEditors.get("POSTags");
                        if(currentNode instanceof SSFPhrase) {
                            nodeLabelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("PhraseNames"));
                        }
                          else {
                            nodeLabelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("POSTags"));
                        }
                        
                        // If multiple tags are given, show them as options in the node label menu, instead of all tags
                        
                        String tag = currentNode.getName();
                        
                        String tags[] = tag.split("/");
                        
                        if(tags.length > 1)
                        {
                            for (int i = 0; i < tags.length; i++)
                            {
                                String nodeLabel = (String) tags[i];            
                                nodeEditingJMenu.add(new JMenuItem(new NodeEditingTreeAction(jtree, nodeLabel)));
                            }                            
                        }
                        else
                        {
                            count = nodeLabelEditor.getSize();

                            for (int i = 0; i < count; i++)
                            {
                                String nodeLabel = (String) nodeLabelEditor.getElementAt(i);            
                                nodeEditingJMenu.add(new JMenuItem(new NodeEditingTreeAction(jtree, nodeLabel)));
                            }
                        }

                        FeatureStructures fss = currentNode.getFeatureStructures();

                        if(fss == null)
                        {
                            fss = new FeatureStructuresImpl();
                            FeatureStructure fs = new FeatureStructureImpl();
                            fss.addAltFSValue(fs);

                            currentNode.setFeatureStructures(fss);
                        }

                        // For mandatory (e.g. morph) features

//                        if(fss != null && fss.countAltFSValues() == 1 && fss.getAltFSValue(0).hasMandatoryAttribs() == true)
//                        if(fss.countAltFSValues() == 1 && fss.getAltFSValue(0).hasMandatoryAttribs() == true)
                        if(fss.countAltFSValues() == 1)
                        {
                            FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();
                            int mcount = fsProperties.countMandatoryAttributes();

                            for (int i = 1; i < mcount; i++)
                            {
                                String fname = fsProperties.getMandatoryAttribute(i);
                                String fvalue = fsProperties.getMandatoryAttributeValue(i);

                                JMenu mfMenu = new JMenu(fname);

                                String vals[] = fvalue.split("::");

                                for (int j = 0; j < vals.length; j++)
                                {
                                    mfMenu.add(new JMenuItem(new NodeMandatoryFeatureValueTreeAction(jtree, fname, vals[j], sanchayTreeJPanel)));
                                }

                                mandatoryFeatureValueEditingJMenu.add(mfMenu);
                            }
                        }

                        // For non-mandatory (e.g. dependency) features                        
//                        if(fss != null && fss.countAltFSValues() == 1)
                        if(fss.countAltFSValues() == 1)
                        {
                            FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();
                            int mcount = fsProperties.countNonMandatoryAttributes();

                            for (int i = 0; i < mcount; i++)
                            {
                                String fname = fsProperties.getNonMandatoryAttribute(i);
                                String fvalue = fsProperties.getNonMandatoryAttributeValue(i);

                                JMenu mfMenu = new JMenu(fname);

                                String vals[] = fvalue.split("::");

                                int maxSize = 25;
                                JMenu moreMenu1 = new JMenu(GlobalProperties.getIntlString("More"));
                                JMenu moreMenu2 = new JMenu(GlobalProperties.getIntlString("More"));
                                JMenu moreMenu3 = new JMenu(GlobalProperties.getIntlString("More"));

                                for (int j = 0; j < vals.length; j++)
                                {
                                    if(j < maxSize)
                                        mfMenu.add(new JMenuItem(new NodeFeatureValueTreeAction(jtree, fname, vals[j], sanchayTreeJPanel)));
                                    else if(j < 2 * maxSize)
                                    {
                                        moreMenu1.add(new JMenuItem(new NodeFeatureValueTreeAction(jtree, fname, vals[j], sanchayTreeJPanel)));
                                    }
                                    else if(j < 3 * maxSize)
                                    {
                                        moreMenu2.add(new JMenuItem(new NodeFeatureValueTreeAction(jtree, fname, vals[j], sanchayTreeJPanel)));
                                    }
                                    else if(j < 4 * maxSize)
                                    {
                                        moreMenu3.add(new JMenuItem(new NodeFeatureValueTreeAction(jtree, fname, vals[j], sanchayTreeJPanel)));
                                    }

                                    if(moreMenu1.getItemCount() > 0)
                                        mfMenu.add(moreMenu1);
                                    if(moreMenu2.getItemCount() > 0)
                                        mfMenu.add(moreMenu2);
                                    if(moreMenu3.getItemCount() > 0)
                                        mfMenu.add(moreMenu3);
                                }

                                featureValueEditingJMenu.add(mfMenu);
                            }
                        }

                        // Adding things to the menu
                        if(featureDeleteJMenu.getMenuComponentCount() > 0)
                        {
                            popup.insert(featureDeleteJMenu, 0);
                            featureDeleteJMenu.setVisible(true);
                        }

                        if(featureReferentEditingJMenu.getMenuComponentCount() > 0)
                        {
                            popup.insert(featureReferentEditingJMenu, 0);
                            featureReferentEditingJMenu.setVisible(true);
                        }

                        if(mandatoryFeatureValueEditingJMenu.getMenuComponentCount() > 0)
                        {
                            popup.insert(mandatoryFeatureValueEditingJMenu, 0);
                            mandatoryFeatureValueEditingJMenu.setVisible(true);
                        }

                        if(featureValueEditingJMenu.getMenuComponentCount() > 0)
                        {
                            popup.insert(featureValueEditingJMenu, 0);
                            featureValueEditingJMenu.setVisible(true);
                        }

                        if(featureNameEditingJMenu.getMenuComponentCount() > 0)
                        {
                            popup.insert(featureNameEditingJMenu, 0);
                            featureNameEditingJMenu.setVisible(true);
                        }

                        popup.insert(nodeEditingJMenu, 0);
                        nodeEditingJMenu.setVisible(true);

                        popup.validate();

//                        popup.add(featureDeleteJMenu);
//                        popup.add(featureReferentEditingJMenu);
//                        popup.add(featureValueEditingJMenu);
//                        popup.add(featureNameEditingJMenu);
//                        popup.add(nodeEditingJMenu);
//                        e.consume();
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            popup.show(e.getComponent(), e.getX(), e.getY());
            popup.setVisible(false);
            popup.setVisible(true);
        }
    }    
}
