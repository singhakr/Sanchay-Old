/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.tree.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.SanchayJTable;

/**
 *
 * @author anil
 */
public class TreeDrawingNodeEditPopupListener extends MouseAdapter
{

    protected JPopupMenu popup;

    protected JMenu nodeEditingJMenu;
    protected JMenu featureNameEditingJMenu;
    protected JMenu featureValueEditingJMenu;
    protected JMenu mandatoryFeatureValueEditingJMenu;
    protected JMenu featureReferentEditingJMenu;
    protected JMenu featureDeleteJMenu;

    protected SanchayJTable sanchayJTable;

    protected Hashtable nodeLabelEditors;

    protected SanchayTableModel fsSchema;

    protected String[] featureValueStrings;

    protected SanchayTreeDrawingJPanel sanchayTreeDrawingJPanel;

    protected List namedNodes;
    protected List attribNames;
    
    protected Point point;

    public TreeDrawingNodeEditPopupListener(SanchayJTable sanchayJTable, JPopupMenu pm, Hashtable nodeLabelEditors, SanchayTableModel fsSchema)
    {
        popup = pm;
        this.sanchayJTable = sanchayJTable;
        this.nodeLabelEditors = nodeLabelEditors;

        this.fsSchema = fsSchema;

        nodeEditingJMenu = new JMenu(GlobalProperties.getIntlString("Node_Name"));
        featureNameEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Name"));
        featureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Value"));
        mandatoryFeatureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Mandatory_Attribute_Value"));
        featureReferentEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Referent"));
        featureDeleteJMenu = new JMenu(GlobalProperties.getIntlString("Delete_Attribute"));
    }

    public TreeDrawingNodeEditPopupListener(JPopupMenu pm, SanchayTableModel fsSchema)
    {
        popup = pm;

        this.fsSchema = fsSchema;

        nodeEditingJMenu = new JMenu(GlobalProperties.getIntlString("Node_Name"));
        featureNameEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Name"));
        featureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Value"));
        mandatoryFeatureValueEditingJMenu = new JMenu(GlobalProperties.getIntlString("Mandatory_Attribute_Value"));
        featureReferentEditingJMenu = new JMenu(GlobalProperties.getIntlString("Attribute_Referent"));
        featureDeleteJMenu = new JMenu(GlobalProperties.getIntlString("Delete_Attribute"));
    }

    private void initMenu()
    {
        if (nodeEditingJMenu != null)
        {
            nodeEditingJMenu.setVisible(false);
            nodeEditingJMenu.removeAll();
        }

        if(featureValueEditingJMenu != null)
        {
            featureValueEditingJMenu.setVisible(false);
            featureValueEditingJMenu.removeAll();
        }

        if(mandatoryFeatureValueEditingJMenu != null)
        {
            mandatoryFeatureValueEditingJMenu.setVisible(false);
            mandatoryFeatureValueEditingJMenu.removeAll();
        }

        if(featureReferentEditingJMenu != null)
        {
            featureReferentEditingJMenu.setVisible(false);
            featureReferentEditingJMenu.removeAll();
        }

        if(featureDeleteJMenu != null)
        {
            featureDeleteJMenu.setVisible(false);
            featureDeleteJMenu.removeAll();
        }

        popup.validate();
    }

    public void setJTable(SanchayJTable sanchayJTable)
    {
        this.sanchayJTable = sanchayJTable;
    }

    public void setSanchayTreeJPanel(SanchayTreeDrawingJPanel sanchayTreeDrawingJPanel)
    {
        this.sanchayTreeDrawingJPanel = sanchayTreeDrawingJPanel;
    }

    public void setNodeLabelEditors(Hashtable nodeLabelEditors)
    {
        this.nodeLabelEditors = nodeLabelEditors;
    }

    public void setNodeEditingJMenu(JMenu pm)
    {
        this.nodeEditingJMenu = pm;
    }

    public void mouseClicked(MouseEvent e)
    {
//        showPopup(e);
    }

    public void mousePressed(MouseEvent e)
    {
        point = e.getPoint();
        showPopup(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        point = e.getPoint();

        if (File.separator.equalsIgnoreCase("\\"))
        {
            showPopup(e);
        }
    }

    private void showPopup(MouseEvent e)
    {
        point = e.getPoint();

        initMenu();

        if (e.isPopupTrigger())
        {
            Point p = e.getPoint();
            int r = sanchayJTable.rowAtPoint(p);
            int c = sanchayJTable.columnAtPoint(p);

            SSFNode currentNode = (SSFNode) sanchayJTable.getCellObject(r, c);

            if (currentNode != null && nodeLabelEditors != null)
            {
                try
                {
                    DefaultComboBoxModel nodeLabelEditor = null;

                    FeatureStructures fss = currentNode.getFeatureStructures();

                    if (currentNode instanceof SSFPhrase)
                    {
                        nodeLabelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("PhraseNames"));

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

                        if(fss != null)
                        {
                            for (int i = 0; i < count; i++)
                            {
                                // Name attribute editing not allowed
    //                            featureNameEditingJMenu.add(new JMenuItem(new NodeFeatureNameTreeAction(sanchayJTable, featureNameStrings[i])));
    //                                featureValueEditingJMenu.add(new JMenuItem(new NodeFeatureValueTreeAction(jtree, featureNameStrings[i], featureValueStrings)));
                                if (featureNameStrings[i].equals(GlobalProperties.getIntlString("name")) == false)
                                {
                                    featureReferentEditingJMenu.add(new JMenuItem(new NodeFeatureReferenceTreeAction(sanchayJTable, featureNameStrings[i], namedNodes)));
                                }
                            }
                        }

                        attribNames = currentNode.getAttributeNames();

                        if(attribNames != null)
                        {
                            count = attribNames.size();

                            for (int i = 0; i < count; i++)
                            {
                                if (FeatureStructuresImpl.getFSProperties().isMandatory((String) attribNames.get(i)) == false)
                                {
                                    if (((String) attribNames.get(i)).equals(GlobalProperties.getIntlString("name")) == false)
                                    {
                                        featureDeleteJMenu.add(new JMenuItem(new NodeFeatureDeleteTreeAction(sanchayJTable, (String) attribNames.get(i))));
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        nodeLabelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("POSTags"));
                    }

                    // If multiple tags are given, show them as options in the node label menu, instead of all tags

                    String tag = currentNode.getName();

                    String tags[] = tag.split("/");

                    if (tags.length > 1)
                    {
                        for (int i = 0; i < tags.length; i++)
                        {
                            String nodeLabel = (String) tags[i];
                            nodeEditingJMenu.add(new JMenuItem(new NodeEditingTreeAction(sanchayJTable, nodeLabel)));
                        }
                    } else
                    {
                        int count = nodeLabelEditor.getSize();

                        for (int i = 0; i < count; i++)
                        {
                            String nodeLabel = (String) nodeLabelEditor.getElementAt(i);
                            nodeEditingJMenu.add(new JMenuItem(new NodeEditingTreeAction(sanchayJTable, nodeLabel)));
                        }
                    }

                    // For mandatory (e.g. morph) features
//                    if (fss != null && fss.countAltFSValues() == 1 && fss.getAltFSValue(0).hasMandatoryAttribs() == true)
//                    {
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
                                mfMenu.add(new JMenuItem(new NodeMandatoryFeatureValueTreeAction(sanchayJTable, fname, vals[j])));
                            }

                            mandatoryFeatureValueEditingJMenu.add(mfMenu);
                        }
//                    }

                    // For non-mandatory (e.g. dependency) features
//                    if (fss != null && fss.countAltFSValues() == 1)
//                    {
                        fsProperties = FeatureStructuresImpl.getFSProperties();
                        mcount = fsProperties.countNonMandatoryAttributes();

                        for (int i = 0; i < mcount; i++)
                        {
                            String fname = fsProperties.getNonMandatoryAttribute(i);

                            if (fname.equals(GlobalProperties.getIntlString("name")) == false)
                            {
                                String fvalue = fsProperties.getNonMandatoryAttributeValue(i);

                                JMenu mfMenu = new JMenu(fname);

                                String vals[] = fvalue.split("::");

                                int maxSize = 25;
                                JMenu moreMenu1 = new JMenu(GlobalProperties.getIntlString("More"));
                                JMenu moreMenu2 = new JMenu(GlobalProperties.getIntlString("More"));
                                JMenu moreMenu3 = new JMenu(GlobalProperties.getIntlString("More"));

                                for (int j = 0; j < vals.length; j++)
                                {
                                    if (j < maxSize)
                                    {
                                        mfMenu.add(new JMenuItem(new NodeFeatureValueTreeAction(sanchayJTable, fname, vals[j])));
                                    } else if (j < 2 * maxSize)
                                    {
                                        moreMenu1.add(new JMenuItem(new NodeFeatureValueTreeAction(sanchayJTable, fname, vals[j])));
                                    } else if (j < 3 * maxSize)
                                    {
                                        moreMenu2.add(new JMenuItem(new NodeFeatureValueTreeAction(sanchayJTable, fname, vals[j])));
                                    } else if (j < 4 * maxSize)
                                    {
                                        moreMenu3.add(new JMenuItem(new NodeFeatureValueTreeAction(sanchayJTable, fname, vals[j])));
                                    }

                                    if (moreMenu1.getItemCount() > 0)
                                    {
                                        mfMenu.add(moreMenu1);
                                    }
                                    if (moreMenu2.getItemCount() > 0)
                                    {
                                        mfMenu.add(moreMenu2);
                                    }
                                    if (moreMenu3.getItemCount() > 0)
                                    {
                                        mfMenu.add(moreMenu3);
                                    }
//                                    mfMenu.add(new JMenuItem(new NodeFeatureValueTreeAction(sanchayJTable, fname, vals[j])));
                                }

                                featureValueEditingJMenu.add(mfMenu);
                            }
                        }
//                    }

                    // Adding things to the menu
                    if (featureDeleteJMenu.getMenuComponentCount() > 0)
                    {
                        popup.insert(featureDeleteJMenu, 0);
                        featureDeleteJMenu.setVisible(true);
                    }

                    if (featureReferentEditingJMenu.getMenuComponentCount() > 0)
                    {
                        popup.insert(featureReferentEditingJMenu, 0);
                        featureReferentEditingJMenu.setVisible(true);
                    }

                    if (mandatoryFeatureValueEditingJMenu.getMenuComponentCount() > 0)
                    {
                        popup.insert(mandatoryFeatureValueEditingJMenu, 0);
                        mandatoryFeatureValueEditingJMenu.setVisible(true);
                    }

                    if (featureValueEditingJMenu.getMenuComponentCount() > 0)
                    {
                        popup.insert(featureValueEditingJMenu, 0);
                        featureValueEditingJMenu.setVisible(true);
                    }

                    if (featureNameEditingJMenu.getMenuComponentCount() > 0)
                    {
                        popup.insert(featureNameEditingJMenu, 0);
                        featureNameEditingJMenu.setVisible(true);
                    }

                    popup.insert(nodeEditingJMenu, 0);
                    nodeEditingJMenu.setVisible(true);

                    popup.validate();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            popup.show(e.getComponent(), e.getX(), e.getY());
            popup.setVisible(false);
            popup.setVisible(true);
        }
    }

    protected class NodeEditingTreeAction extends AbstractAction {
        SanchayJTable sanchayJTable;

         public NodeEditingTreeAction(SanchayJTable sanchayJTable, String text, ImageIcon icon,
                          String desc, Integer mnemonic, KeyStroke acclerator)
         {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, acclerator);

            this.sanchayJTable = sanchayJTable;
        }

        public NodeEditingTreeAction(SanchayJTable sanchayJTable, String text)
        {
            super(text);

            this.sanchayJTable = sanchayJTable;
        }

        public void actionPerformed(ActionEvent e)
        {
            int row = sanchayJTable.rowAtPoint(point);
            int col = sanchayJTable.columnAtPoint(point);

            Object currentSelection = sanchayJTable.getCellObject(row, col);

            if (currentSelection != null)
            {
                SSFNode currentNode = (SSFNode) currentSelection;

//                if(mode == SanchayTreeJPanel.DEFAULT_MODE)
//                {
////                    nodeLabelJComboBox.setEnabled(false);
//                }
//                else if(mode == SanchayTreeJPanel.SSF_MODE)
//                {
                    currentNode.setName((String) getValue(Action.NAME));
                    sanchayJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
//                    jtree.updateUI();
//                }
            }
        }
    }

    protected class NodeFeatureNameTreeAction extends AbstractAction {
        SanchayJTable sanchayJTable;

         public NodeFeatureNameTreeAction(SanchayJTable sanchayJTable, String text, ImageIcon icon,
                          String desc, Integer mnemonic, KeyStroke acclerator)
         {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, acclerator);

            this.sanchayJTable = sanchayJTable;
        }

        public NodeFeatureNameTreeAction(SanchayJTable sanchayJTable, String text)
        {
            super(text);

            this.sanchayJTable = sanchayJTable;
        }

        public void actionPerformed(ActionEvent e)
        {
            int row = sanchayJTable.rowAtPoint(point);
            int col = sanchayJTable.columnAtPoint(point);

            Object currentSelection = sanchayJTable.getCellObject(row, col);

            if (currentSelection != null)
            {
                SSFNode currentNode = (SSFNode) currentSelection;

//                if(mode == SanchayTreeJPanel.DEFAULT_MODE)
//                {
////                    nodeLabelJComboBox.setEnabled(false);
//                }
//                else if(mode == SanchayTreeJPanel.SSF_MODE)
//                {
                    if(currentNode instanceof SSFPhrase)
                    {
                        ((SSFPhrase) currentNode).setAttributeValue((String) getValue(Action.NAME), "?");
//                        ((DefaultTreeModel) jtree.getModel()).reload(currentNode.getParent());
                        sanchayJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
                    }
//                    jtree.updateUI();
//                }
            }
        }
    }

    protected class NodeFeatureValueTreeAction extends AbstractAction {
        SanchayJTable sanchayJTable;
        String fname;

         public NodeFeatureValueTreeAction(SanchayJTable sanchayJTable, String fname, String text, ImageIcon icon,
                          String desc, Integer mnemonic, KeyStroke acclerator)
         {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, acclerator);

            this.sanchayJTable = sanchayJTable;
            this.fname = fname;
        }

        public NodeFeatureValueTreeAction(SanchayJTable sanchayJTable, String fname, String text)
        {
            super(text);

            this.sanchayJTable = sanchayJTable;
            this.fname = fname;
        }

        public void actionPerformed(ActionEvent e)
        {
            int row = sanchayJTable.rowAtPoint(point);
            int col = sanchayJTable.columnAtPoint(point);

            Object currentSelection = sanchayJTable.getCellObject(row, col);

            if (currentSelection != null)
            {
                SSFNode currentNode = (SSFNode) currentSelection;

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
                        sanchayJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
                    }
//                }
            }
        }
    }

    protected class NodeMandatoryFeatureValueTreeAction extends AbstractAction {
        SanchayJTable sanchayJTable;
        String fname;

         public NodeMandatoryFeatureValueTreeAction(SanchayJTable sanchayJTable, String fname, String text, ImageIcon icon,
                          String desc, Integer mnemonic, KeyStroke acclerator)
         {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, acclerator);

            this.sanchayJTable = sanchayJTable;
            this.fname = fname;
        }

        public NodeMandatoryFeatureValueTreeAction(SanchayJTable sanchayJTable, String fname, String text)
        {
            super(text);

            this.sanchayJTable = sanchayJTable;
            this.fname = fname;
        }

        public void actionPerformed(ActionEvent e)
        {
            int row = sanchayJTable.rowAtPoint(point);
            int col = sanchayJTable.columnAtPoint(point);

            Object currentSelection = sanchayJTable.getCellObject(row, col);

            if (currentSelection != null)
            {
                SSFNode currentNode = (SSFNode) currentSelection;

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
                        sanchayJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
                    }
//                }
            }
        }
    }

    protected class NodeFeatureReferenceTreeAction extends AbstractAction {
        SanchayJTable sanchayJTable;
        List namedNodes;

         public NodeFeatureReferenceTreeAction(SanchayJTable sanchayJTable, String text, ImageIcon icon,
                          String desc, Integer mnemonic, KeyStroke acclerator, List namedNodes)
         {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, acclerator);

            this.sanchayJTable = sanchayJTable;
            this.namedNodes = namedNodes;
        }

        public NodeFeatureReferenceTreeAction(SanchayJTable sanchayJTable, String text, List namedNodes)
        {
            super(text);

            this.sanchayJTable = sanchayJTable;
            this.namedNodes = namedNodes;
        }

        public void actionPerformed(ActionEvent e)
        {
            if(namedNodes == null || namedNodes.size() == 0)
                return;

            int row = sanchayJTable.rowAtPoint(point);
            int col = sanchayJTable.columnAtPoint(point);

            Object currentSelection = sanchayJTable.getCellObject(row, col);

            if (currentSelection != null)
            {
                SSFNode currentNode = (SSFNode) currentSelection;

//                if(mode == SanchayTreeJPanel.DEFAULT_MODE)
//                {
////                    nodeLabelJComboBox.setEnabled(false);
//                }
//                else if(mode == SanchayTreeJPanel.SSF_MODE)
//                {
                 if(currentNode instanceof SSFPhrase)
                 {
                    SSFNode selectedValue = (SSFNode) JOptionPane.showInputDialog(null,
                        GlobalProperties.getIntlString("Select_the_referent_node"), GlobalProperties.getIntlString("Attibute_Referent"), JOptionPane.INFORMATION_MESSAGE, null,
                        namedNodes.toArray(), namedNodes.toArray()[0]);

                    if(selectedValue == null)
                        return;

                    String prevValue = ((SSFPhrase) currentNode).getAttributeValue((String) getValue(Action.NAME));

                    if(prevValue != null & prevValue.equals("") == false)
                    {
                        String parts[] = prevValue.split(":");

                        ((SSFPhrase) currentNode).setAttributeValue((String) getValue(Action.NAME), parts[0] + ":" + selectedValue.getAttributeValue(GlobalProperties.getIntlString("name")));
    //                        ((DefaultTreeModel) jtree.getModel()).reload(currentNode.getParent());
                        sanchayJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
                    }
                 }
//                    jtree.updateUI();
//                }
            }
        }
    }

    protected class NodeFeatureDeleteTreeAction extends AbstractAction {
        SanchayJTable sanchayJTable;

         public NodeFeatureDeleteTreeAction(SanchayJTable sanchayJTable, String text, ImageIcon icon,
                          String desc, Integer mnemonic, KeyStroke acclerator)
         {
            super(text, icon);

            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, acclerator);

            this.sanchayJTable = sanchayJTable;
        }

        public NodeFeatureDeleteTreeAction(SanchayJTable sanchayJTable, String text)
        {
            super(text);

            this.sanchayJTable = sanchayJTable;
        }

        public void actionPerformed(ActionEvent e)
        {
            int row = sanchayJTable.rowAtPoint(point);
            int col = sanchayJTable.columnAtPoint(point);

            Object currentSelection = sanchayJTable.getCellObject(row, col);

            if (currentSelection != null)
            {
                SSFNode currentNode = (SSFNode) currentSelection;

//                if(mode == SanchayTreeJPanel.DEFAULT_MODE)
//                {
////                    nodeLabelJComboBox.setEnabled(false);
//                }
//                else if(mode == SanchayTreeJPanel.SSF_MODE)
//                {
                 if(currentNode instanceof SSFPhrase && attribNames.size() > 0)
                 {
                     if(FeatureStructuresImpl.getFSProperties().isMandatory(Action.NAME) == false)
                     {
                       ((SSFPhrase) currentNode).getFeatureStructures().getAltFSValue(0).removeAttribute((String) getValue(Action.NAME));
    //                        ((DefaultTreeModel) jtree.getModel()).reload(currentNode.getParent());
                        sanchayJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
                     }
                 }
//                    jtree.updateUI();
//                }
            }
        }
    }
}
