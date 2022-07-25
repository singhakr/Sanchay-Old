/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.util.query;

import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayJDialog;
import sanchay.properties.PropertyTokens;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author ambati
 */
public class SSFFindReplace {

    protected PropertyTokens posTagsPT;
    protected PropertyTokens phraseNamesPT;
    protected String langEnc;
    protected JFrame owner;
    protected JFrame owner1;
    protected SanchayTreeJPanel ssfPhraseJPanel;

    public void setDefaults(PropertyTokens tagsPT,PropertyTokens namesPT,String enc, JFrame frame,SanchayTreeJPanel panel)
    {
        posTagsPT = tagsPT;
        phraseNamesPT = namesPT;
        langEnc = enc;
        owner = frame;
        ssfPhraseJPanel = panel;
    }
            
    public SanchayTableModel getFindOptionsTable()
    {
        SanchayTableModel findOptionsTable = new SanchayTableModel(0, 4);

        SanchayTableJPanel findOptionsJPanel = SanchayTableJPanel.createFindOptionsTableJPanel(findOptionsTable, langEnc, false);

        JTable tableJTable = findOptionsJPanel.getJTable();

        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        Vector tagsVec = posTagsPT.getCopyOfTokens();
        tagsVec.addAll(phraseNamesPT.getCopyOfTokens());

        UtilityFunctions.makeExactMatchRegexes(tagsVec);

        DefaultComboBoxModel labelEditorModel = new DefaultComboBoxModel(tagsVec);
        labelEditorModel.insertElementAt("[.]*", 0);
        JComboBox labelEditor = new JComboBox();
        labelEditor.setModel(labelEditorModel);
        UtilityFunctions.setComponentFont(labelEditor, langEnc);

        TableColumn tagsCol = tableJTable.getColumn("Tag");
        labelEditor.setEditable(true);
        tagsCol.setCellEditor(new DefaultCellEditor(labelEditor));

        DefaultComboBoxModel textEditorModel = new DefaultComboBoxModel();
        textEditorModel.addElement("[.]*");
        JComboBox textEditor = new JComboBox();
        textEditor.setModel(textEditorModel);
        UtilityFunctions.setComponentFont(textEditor, langEnc);

        TableColumn textCol = tableJTable.getColumn("Text");
        textEditor.setEditable(true);
        textCol.setCellEditor(new DefaultCellEditor(textEditor));

        Vector attribNamesVec = UtilityFunctions.arrayToVector(fsProperties.getAllAttributes());

        UtilityFunctions.makeExactMatchRegexes(attribNamesVec);

        DefaultComboBoxModel attribNameEditorModel = new DefaultComboBoxModel(attribNamesVec);
        attribNameEditorModel.insertElementAt("[.]*", 0);
        JComboBox attribNameEditor = new JComboBox();
        attribNameEditor.setModel(attribNameEditorModel);
        UtilityFunctions.setComponentFont(attribNameEditor, langEnc);

        TableColumn attribNamesCol = tableJTable.getColumn("Attribute Name");
        attribNameEditor.setEditable(true);
        attribNamesCol.setCellEditor(new DefaultCellEditor(attribNameEditor));

        DefaultComboBoxModel attribValEditorModel = new DefaultComboBoxModel();

        int mcount = fsProperties.countMandatoryAttributes();

        for (int i = 0; i < mcount; i++)
        {
            String attribVals[] = fsProperties.getMandatoryAttributeValues(i);

            for (int j = 0; j < attribVals.length; j++)
            {
                String attribVal = attribVals[j];
                attribValEditorModel.addElement("^" + attribVal + "$");
            }
        }

        int ocount = fsProperties.countNonMandatoryAttributes();

        for (int i = 0; i < ocount; i++)
        {
            String attribVals[] = fsProperties.getNonMandatoryAttributeValues(i);

            for (int j = 0; j < attribVals.length; j++)
            {
                String attribVal = attribVals[j];
                attribValEditorModel.addElement("^" + attribVal + "$");
            }
        }

        JTree jtree = ssfPhraseJPanel.getJTree();

        findOptionsTable.addTableModelListener(new FindOptionsTableChangeListener(jtree, false));

        findOptionsTable.addRow();

        JComboBox attribValEditor = new JComboBox();
        attribValEditor.setModel(attribValEditorModel);
        attribValEditorModel.insertElementAt("[.]*", 0);
        UtilityFunctions.setComponentFont(attribValEditor, langEnc);

        TableColumn attribValsCol = tableJTable.getColumn("Attribute Value");
        attribValEditor.setEditable(true);
        attribValsCol.setCellEditor(new DefaultCellEditor(attribValEditor));

        SanchayJDialog findDialog = new SanchayJDialog(owner1, "Find", true, (JPanelDialog) findOptionsJPanel);
        findDialog.pack();

        UtilityFunctions.centre(findDialog);

        findDialog.setVisible(true);

        return findOptionsTable;
    }

    public SanchayTableModel getReplaceOptionsTable()
    {
        SanchayTableModel findOptionsTable = new SanchayTableModel(0, 8);

        SanchayTableJPanel findOptionsJPanel = SanchayTableJPanel.createFindOptionsTableJPanel(findOptionsTable, langEnc, true);

        JTable tableJTable = findOptionsJPanel.getJTable();

        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        Vector tagsVec = posTagsPT.getCopyOfTokens();
        tagsVec.addAll(phraseNamesPT.getCopyOfTokens());

        UtilityFunctions.makeExactMatchRegexes(tagsVec);

        DefaultComboBoxModel labelEditorModel = new DefaultComboBoxModel(tagsVec);
        labelEditorModel.insertElementAt("[.]*", 0);
        JComboBox labelEditor = new JComboBox();
        labelEditor.setModel(labelEditorModel);
        UtilityFunctions.setComponentFont(labelEditor, langEnc);

        TableColumn tagsCol = tableJTable.getColumn("Tag");
        labelEditor.setEditable(true);
        tagsCol.setCellEditor(new DefaultCellEditor(labelEditor));

        Vector tagsVecNew = new Vector();
        tagsVecNew.addAll(tagsVec);
        UtilityFunctions.backFromExactMatchRegex(tagsVecNew);

        labelEditorModel = new DefaultComboBoxModel(tagsVecNew);
        TableColumn newTagsCol = tableJTable.getColumn("New Tag");
        labelEditor = new JComboBox();
        labelEditor.setEditable(true);
        labelEditor.setModel(labelEditorModel);
        newTagsCol.setCellEditor(new DefaultCellEditor(labelEditor));
        UtilityFunctions.setComponentFont(labelEditor, langEnc);

        DefaultComboBoxModel textEditorModel = new DefaultComboBoxModel();
        textEditorModel.addElement("[.]*");
        JComboBox textEditor = new JComboBox();
        textEditor.setModel(textEditorModel);
        UtilityFunctions.setComponentFont(textEditor, langEnc);

        TableColumn textCol = tableJTable.getColumn("Text");
        textEditor.setEditable(true);
        textCol.setCellEditor(new DefaultCellEditor(textEditor));

        TableColumn newTextCol = tableJTable.getColumn("New Text");
        newTextCol.setCellEditor(new DefaultCellEditor(textEditor));

        Vector attribNamesVec = UtilityFunctions.arrayToVector(fsProperties.getAllAttributes());

        UtilityFunctions.makeExactMatchRegexes(attribNamesVec);

        DefaultComboBoxModel attribNameEditorModel = new DefaultComboBoxModel(attribNamesVec);
        attribNameEditorModel.insertElementAt("[.]*", 0);
        JComboBox attribNameEditor = new JComboBox();
        attribNameEditor.setModel(attribNameEditorModel);
        UtilityFunctions.setComponentFont(attribNameEditor, langEnc);

        TableColumn attribNamesCol = tableJTable.getColumn("Attribute Name");
        attribNameEditor.setEditable(true);
        attribNamesCol.setCellEditor(new DefaultCellEditor(attribNameEditor));

        Vector attribNamesVecNew = new Vector();
        attribNamesVecNew.addAll(attribNamesVec);
        UtilityFunctions.backFromExactMatchRegex(attribNamesVecNew);

        attribNameEditorModel = new DefaultComboBoxModel(attribNamesVecNew);
        TableColumn newAttribNamesCol = tableJTable.getColumn("New Name");
        attribNameEditor = new JComboBox();
        UtilityFunctions.setComponentFont(attribNameEditor, langEnc);
        attribNameEditor.setEditable(true);
        attribNameEditor.setModel(attribNameEditorModel);
        newAttribNamesCol.setCellEditor(new DefaultCellEditor(attribNameEditor));

        JCheckBox createAttribEditor = new JCheckBox();
        createAttribEditor.setSelected(false);

        TableColumn createAttribCol = tableJTable.getColumn("Create Attribute");
        createAttribCol.setCellEditor(new DefaultCellEditor(createAttribEditor));

        DefaultComboBoxModel attribValEditorModel = new DefaultComboBoxModel();
        DefaultComboBoxModel attribValEditorModelNew = new DefaultComboBoxModel();

        int mcount = fsProperties.countMandatoryAttributes();

        for (int i = 0; i < mcount; i++)
        {
            String attribVals[] = fsProperties.getMandatoryAttributeValues(i);

            for (int j = 0; j < attribVals.length; j++)
            {
                String attribVal = attribVals[j];
                attribValEditorModel.addElement("^" + attribVal + "$");
                attribValEditorModelNew.addElement(attribVal);
            }
        }

        int ocount = fsProperties.countNonMandatoryAttributes();

        for (int i = 0; i < ocount; i++)
        {
            String attribVals[] = fsProperties.getNonMandatoryAttributeValues(i);

            for (int j = 0; j < attribVals.length; j++)
            {
                String attribVal = attribVals[j];
                attribValEditorModel.addElement("^" + attribVal + "$");
                attribValEditorModelNew.addElement(attribVal);
            }
        }

        JTree jtree = ssfPhraseJPanel.getJTree();

        findOptionsTable.addTableModelListener(new FindOptionsTableChangeListener(jtree, true));

        findOptionsTable.addRow();

        JComboBox attribValEditor = new JComboBox();
        attribValEditor.setModel(attribValEditorModel);
        attribValEditorModel.insertElementAt("[.]*", 0);
        attribValEditorModelNew.insertElementAt("[.]*", 0);
        UtilityFunctions.setComponentFont(attribValEditor, langEnc);

        TableColumn attribValsCol = tableJTable.getColumn("Attribute Value");
        attribValEditor.setEditable(true);
        attribValsCol.setCellEditor(new DefaultCellEditor(attribValEditor));

        attribValEditor = new JComboBox();
        attribValEditor.setEditable(true);
        attribValEditor.setModel(attribValEditorModelNew);
        UtilityFunctions.setComponentFont(attribValEditor, langEnc);

        TableColumn newAttribValsCol = tableJTable.getColumn("New Value");
        newAttribValsCol.setCellEditor(new DefaultCellEditor(attribValEditor));

        SanchayJDialog findDialog = new SanchayJDialog(owner1, "Replace", true, (JPanelDialog) findOptionsJPanel);
        findDialog.pack();
                
        UtilityFunctions.maxmize(findDialog);
        UtilityFunctions.centre(findDialog);

        findDialog.setVisible(true);

        return findOptionsTable;
    }

    private class FindOptionsTableChangeListener implements TableModelListener {

        private boolean replace;
        private JTree jtree;

        public FindOptionsTableChangeListener(JTree jtree, boolean replace)
        {
            this.jtree = jtree;
            this.replace = replace;
        }

        public void tableChanged(TableModelEvent e) {

            TreePath currentSelection = jtree.getSelectionPath();

            String tag = "[.]*";
            String text = "[.]*";

            if (currentSelection != null)
            {
                SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());

                if(currentNode.getName().equals("") == false)
                    tag = "^" + currentNode.getName() + "$";

                if(currentNode.getLexData().equals("") == false)
                    text = "^" + currentNode.getLexData() + "$";
            }

            int row = e.getFirstRow();
            int column = e.getColumn();

            SanchayTableModel model = (SanchayTableModel)e.getSource();

            int ccount = model.getColumnCount();

            if(e.getType() == TableModelEvent.INSERT)
            {
                for (int i = 0; i < ccount; i++)
                {
                    String colName = model.getColumnName(i);

                    if(colName.equals("Create Attribute"))
                        model.setValueAt(new Boolean(false), row, i);
                    else if(colName.equals("Tag"))
                        model.setValueAt(tag, row, i);
                    else if(colName.equals("Text"))
                        model.setValueAt(text, row, i);
                    else
                        model.setValueAt("[.]*", row, i);

                    if(replace)
                    {
                        if(colName.equals("New Tag"))
                            model.setValueAt(UtilityFunctions.backFromExactMatchRegex(tag), row, i);
                        else if(colName.equals("New Text"))
                            model.setValueAt(UtilityFunctions.backFromExactMatchRegex(text), row, i);
                    }
                }
            }
            else if(e.getType() == TableModelEvent.UPDATE && replace)
            {
                String colName = model.getColumnName(column);

                if(colName.equals("Tag"))
                {
                    tag = (String) model.getValueAt(row, column);
                    int newTagColIndex = model.getColumnIndex("New Tag");
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(tag), row, newTagColIndex);
                }
                else if(colName.equals("Text"))
                {
                    text = (String) model.getValueAt(row, column);
                    int newTextColIndex = model.getColumnIndex("New Text");
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(text), row, newTextColIndex);
                }
                else if(colName.equals("Attribute Name"))
                {
                    String attrib = (String) model.getValueAt(row, column);
                    int newAttribColIndex = model.getColumnIndex("New Name");
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(attrib), row, newAttribColIndex);
                }
                else if(colName.equals("Attribute Value"))
                {
                    String val = (String) model.getValueAt(row, column);
                    int newValColIndex = model.getColumnIndex("New Value");
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(val), row, newValColIndex);
                }
            }
       }
    }
}
