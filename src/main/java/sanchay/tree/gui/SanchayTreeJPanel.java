/*
 * SanchayJTreePanel.java
 *
 * Created on October 7, 2005, 6:39 PM
 */
package sanchay.tree.gui;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.tree.*;
import sanchay.corpus.ssf.gui.*;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.gui.clients.SanchayClient;
import sanchay.properties.PropertyTokens;
import sanchay.table.*;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.tree.*;
import sanchay.tree.gui.action.*;
import sanchay.gui.common.*;
import sanchay.util.*;
import sanchay.common.types.ClientType;
import sanchay.corpus.stats.CorpusStatisticsFactory;
import sanchay.table.gui.MultiLineCellRenderer;
import sanchay.table.gui.SanchayTableCellEditor;

/**
 *
 * @author  anil
 */
public class SanchayTreeJPanel extends javax.swing.JPanel implements SanchayClient, sanchay.gui.common.JPanelDialog
{
    protected ClientType clientType = ClientType.LANGUAGE_ENCODING_IDENTIFIER;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected SanchayMutableTreeNode rootNode;
    protected SanchayMutableTreeNode rootNodeCopy;
    protected JPanel treeNodeJPanel;
    
    protected SmartPopupMenu treeJPopupMenu = new SmartPopupMenu();

    protected Hashtable nodeLabelEditors; // DefsaultComboBoxModels
    protected int mode;
    protected String langEnc;
    protected String title = "";
    // Tree model types
    public static final int DEFAULT_MODE = 1000; // SanchayMutableTreeNode as the root
    public static final int SSF_MODE = 1001; // SSFPhrase as the root
    public static final int FS_MODE = 1002; // FeatureStructures as the root
    public static final int MM_TREE_MODE = 1003; // Modifier-Modified tree
    public static final int SANCHAY_CATEGORY_MODE = 1020; // SanchayCategoryNode as the root
    public static final int SANCHAY_CATEGORY_FS_MODE = 1021; // FeatureStructure as the root
    public static final int DSF_MODE = 1040; // DSFEntry as the root
    public static final int XML_MODE = 1041; // XMLTag as the root
    private boolean allCommands[];
    private TreeAction actions[];
    private boolean savingNode;
    private boolean nodeTextEditable;
    private boolean controlTabsShown;
    protected TreeNodeEditPopupListener popupListener;
    protected SanchayTableModel fsSchema;
    //undo helpers
    protected UndoManager undo;
    protected UndoableEditSupport support;

    protected boolean propbankMode;
    private boolean alignmentMode;

    /** Creates new form SanchayJTreePanel */
    public SanchayTreeJPanel(int appliedCommands[], int mode, String lang)
    {
        initComponents();

        parentComponent = this;

        this.mode = mode;
        langEnc = lang;
        setFonts();

        SanchayMutableTreeNode root = new SanchayMutableTreeNode(GlobalProperties.getIntlString("Root"));
        SanchayTreeModel sanchayTreeModel = new SanchayTreeModel(root);
        treeJTree.setModel(sanchayTreeModel);

        popupListener = new TreeNodeEditPopupListener(treeJPopupMenu, getFSSchema());
        treeJTree.addMouseListener(popupListener);

        prepareCommands(appliedCommands, mode);

        expandAll(null);

        undo = new UndoManager();
        TreeAction.undo = undo;
        support = new UndoableEditSupport(treeJTree);
        addUndoableEditListener(undo);
    }

    public SanchayTreeJPanel(SanchayMutableTreeNode root, TreeCellRenderer tcr, int appliedCommands[], int mode, int cols, String[] langs)
    {
        initComponents();

        this.mode = mode;

        if (langs.length == 1)
        {
            langEnc = langs[0];
        } else if (langs.length >= 2)
        {
            langEnc = langs[1];
        }

        setFonts();

        rootNode = root;

        try
        {
            rootNodeCopy = rootNode.getCopy();
        } catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_in_copying_node!"), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
        }

        SanchayTreeModel sanchayTreeModel = new SanchayTreeModel(rootNode);
        treeJTree.setModel(sanchayTreeModel);
        treeJTree.setCellRenderer(tcr);

        popupListener = new TreeNodeEditPopupListener(treeJPopupMenu, getFSSchema());
        popupListener.setJTree(treeJTree);
        popupListener.setSanchayTreeJPanel(this);
        treeJTree.addMouseListener(popupListener);

        prepareCommands(appliedCommands, mode);

        expandAll(null);

        undo = new UndoManager();
        TreeAction.undo = undo;
        support = new UndoableEditSupport(treeJTree);
        addUndoableEditListener(undo);

    }

    public SanchayTreeJPanel(SanchayMutableTreeNode root, TreeCellRenderer tcr, int appliedCommands[], int mode, String lang)
    {

        initComponents();

        this.mode = mode;
        langEnc = lang;
        setFonts();

        rootNode = root;

        try
        {
            rootNodeCopy = rootNode.getCopy();
        } catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_in_copying_node!"), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
        }

        SanchayTreeModel sanchayTreeModel = new SanchayTreeModel(rootNode);
        treeJTree.setModel(sanchayTreeModel);
        treeJTree.setCellRenderer(tcr);

        popupListener = new TreeNodeEditPopupListener(treeJPopupMenu, getFSSchema());
        popupListener.setJTree(treeJTree);
        popupListener.setSanchayTreeJPanel(this);
        treeJTree.addMouseListener(popupListener);

        prepareCommands(appliedCommands, mode);

        expandAll(null);

        undo = new UndoManager();
        TreeAction.undo = undo;
        support = new UndoableEditSupport(treeJTree);
        addUndoableEditListener(undo);
    }

    public SanchayTreeJPanel(SanchayMutableTreeNode root, TreeCellRenderer tcr,
            Hashtable labelEditors, int appliedCommands[], int mode, String lang)
    {
        this(root, tcr, appliedCommands, mode, lang);

        nodeLabelEditors = labelEditors;
        popupListener.setJTree(treeJTree);
        popupListener.setSanchayTreeJPanel(this);
        popupListener.setNodeLabelEditors(nodeLabelEditors);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainJSplitPane = new javax.swing.JSplitPane();
        topJPanel = new javax.swing.JPanel();
        senJPanel = new javax.swing.JPanel();
        senJLabel = new javax.swing.JLabel();
        senJScrollPane = new javax.swing.JScrollPane();
        senJTextArea = new javax.swing.JTextArea();
        annotationDisplayOptionJPanel = new javax.swing.JPanel();
        annotationDisplayOptionJComboBox = new javax.swing.JComboBox();
        treeJSplitPane = new javax.swing.JSplitPane();
        treeEditJPanel = new javax.swing.JPanel();
        treeJScrollJPane = new javax.swing.JScrollPane();
        treeJTree = new javax.swing.JTree();
        controlsJTabbedPane = new javax.swing.JTabbedPane();
        tagsTabJPanel = new javax.swing.JPanel();
        featuresTabJPanel = new javax.swing.JPanel();
        fsSelectionPanel = new javax.swing.JPanel();
        fsJComboBox = new javax.swing.JComboBox();
        fsEditJPanel = new javax.swing.JPanel();
        fsEditCommandsJPanel = new javax.swing.JPanel();
        removeFSJButton = new javax.swing.JButton();
        addFSJButton = new javax.swing.JButton();
        applyFSJButton = new javax.swing.JButton();
        commandsJScrollPane = new javax.swing.JScrollPane();
        commandsJPanel = new javax.swing.JPanel();
        bottomJPanel = new javax.swing.JPanel();
        textLabelEditJPanel = new javax.swing.JPanel();
        nodeLabelEditJPanel = new javax.swing.JPanel();
        nodeLabelJLabel = new javax.swing.JLabel();
        nodeLabelJComboBox = new javax.swing.JComboBox();
        nodeTextEditJPanel = new javax.swing.JPanel();
        nodeTextLabel = new javax.swing.JLabel();
        nodeTextJScrollPane = new javax.swing.JScrollPane();
        nodeTextJTextArea = new javax.swing.JTextArea();
        nodeEditJPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        mainJSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainJSplitPane.setResizeWeight(1.0);
        mainJSplitPane.setOneTouchExpandable(true);

        topJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Tree Edit"));
        topJPanel.setLayout(new java.awt.BorderLayout());

        senJPanel.setLayout(new java.awt.BorderLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        senJLabel.setText(bundle.getString("Sentence:_")); // NOI18N
        senJPanel.add(senJLabel, java.awt.BorderLayout.NORTH);

        senJTextArea.setEditable(false);
        senJTextArea.setColumns(20);
        senJTextArea.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        senJTextArea.setLineWrap(true);
        senJTextArea.setRows(2);
        senJTextArea.setWrapStyleWord(true);
        senJScrollPane.setViewportView(senJTextArea);

        senJPanel.add(senJScrollPane, java.awt.BorderLayout.CENTER);

        annotationDisplayOptionJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Raw", "Tagged", "Chunked" }));
        annotationDisplayOptionJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annotationDisplayOptionJComboBoxActionPerformed(evt);
            }
        });
        annotationDisplayOptionJPanel.add(annotationDisplayOptionJComboBox);

        senJPanel.add(annotationDisplayOptionJPanel, java.awt.BorderLayout.EAST);

        topJPanel.add(senJPanel, java.awt.BorderLayout.NORTH);

        treeJSplitPane.setResizeWeight(0.5);
        treeJSplitPane.setToolTipText("");
        treeJSplitPane.setOneTouchExpandable(true);
        treeJSplitPane.setPreferredSize(new java.awt.Dimension(800, 404));

        treeEditJPanel.setLayout(new javax.swing.BoxLayout(treeEditJPanel, javax.swing.BoxLayout.LINE_AXIS));

        treeJScrollJPane.setPreferredSize(new java.awt.Dimension(454, 404));

        treeJTree.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        treeJTree.setMaximumSize(new java.awt.Dimension(32767, 32767));
        treeJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeJTreeValueChanged(evt);
            }
        });
        treeJTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                treeJTreeKeyTyped(evt);
            }
        });
        treeJScrollJPane.setViewportView(treeJTree);

        treeEditJPanel.add(treeJScrollJPane);

        treeJSplitPane.setLeftComponent(treeEditJPanel);

        controlsJTabbedPane.setToolTipText("Mark tags of words and groups");
        controlsJTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                controlsJTabbedPaneStateChanged(evt);
            }
        });

        tagsTabJPanel.setLayout(new java.awt.GridLayout(0, 5, 3, 3));
        controlsJTabbedPane.addTab("Tags", tagsTabJPanel);

        featuresTabJPanel.setLayout(new java.awt.BorderLayout());

        fsSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Feature Structures"));
        fsSelectionPanel.setLayout(new java.awt.BorderLayout(4, 0));

        fsJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fsJComboBoxItemStateChanged(evt);
            }
        });
        fsJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fsJComboBoxActionPerformed(evt);
            }
        });
        fsSelectionPanel.add(fsJComboBox, java.awt.BorderLayout.CENTER);

        featuresTabJPanel.add(fsSelectionPanel, java.awt.BorderLayout.NORTH);

        fsEditJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributes"));
        fsEditJPanel.setLayout(new java.awt.GridLayout(0, 2, 3, 3));
        featuresTabJPanel.add(fsEditJPanel, java.awt.BorderLayout.CENTER);

        removeFSJButton.setMnemonic('y');
        removeFSJButton.setText("Remove");
        removeFSJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFSJButtonActionPerformed(evt);
            }
        });
        fsEditCommandsJPanel.add(removeFSJButton);

        addFSJButton.setMnemonic('y');
        addFSJButton.setText("Add");
        addFSJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFSJButtonActionPerformed(evt);
            }
        });
        fsEditCommandsJPanel.add(addFSJButton);

        applyFSJButton.setMnemonic('y');
        applyFSJButton.setText("Apply");
        applyFSJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyFSJButtonActionPerformed(evt);
            }
        });
        fsEditCommandsJPanel.add(applyFSJButton);

        featuresTabJPanel.add(fsEditCommandsJPanel, java.awt.BorderLayout.PAGE_END);

        controlsJTabbedPane.addTab("Attributes", featuresTabJPanel);

        commandsJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 2));
        commandsJScrollPane.setViewportView(commandsJPanel);

        controlsJTabbedPane.addTab("Commands", commandsJScrollPane);

        treeJSplitPane.setRightComponent(controlsJTabbedPane);

        topJPanel.add(treeJSplitPane, java.awt.BorderLayout.CENTER);

        mainJSplitPane.setTopComponent(topJPanel);

        bottomJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Node Edit", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10))); // NOI18N
        bottomJPanel.setPreferredSize(new java.awt.Dimension(300, 100));
        bottomJPanel.setLayout(new java.awt.BorderLayout(0, 4));

        textLabelEditJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        nodeLabelEditJPanel.setLayout(new java.awt.BorderLayout(4, 0));

        nodeLabelJLabel.setLabelFor(nodeLabelJComboBox);
        nodeLabelJLabel.setText(bundle.getString("Node_label:")); // NOI18N
        nodeLabelEditJPanel.add(nodeLabelJLabel, java.awt.BorderLayout.WEST);

        nodeLabelJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                nodeLabelJComboBoxItemStateChanged(evt);
            }
        });
        nodeLabelJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeLabelJComboBoxActionPerformed(evt);
            }
        });
        nodeLabelEditJPanel.add(nodeLabelJComboBox, java.awt.BorderLayout.CENTER);

        textLabelEditJPanel.add(nodeLabelEditJPanel);

        nodeTextEditJPanel.setLayout(new java.awt.BorderLayout(4, 0));

        nodeTextLabel.setLabelFor(nodeTextJTextArea);
        nodeTextLabel.setText(bundle.getString("Node_text:")); // NOI18N
        nodeTextEditJPanel.add(nodeTextLabel, java.awt.BorderLayout.WEST);

        nodeTextJTextArea.setColumns(20);
        nodeTextJTextArea.setEditable(false);
        nodeTextJTextArea.setLineWrap(true);
        nodeTextJTextArea.setRows(1);
        nodeTextJTextArea.setWrapStyleWord(true);
        nodeTextJScrollPane.setViewportView(nodeTextJTextArea);

        nodeTextEditJPanel.add(nodeTextJScrollPane, java.awt.BorderLayout.CENTER);

        textLabelEditJPanel.add(nodeTextEditJPanel);

        bottomJPanel.add(textLabelEditJPanel, java.awt.BorderLayout.NORTH);

        nodeEditJPanel.setLayout(new java.awt.BorderLayout());
        bottomJPanel.add(nodeEditJPanel, java.awt.BorderLayout.CENTER);

        mainJSplitPane.setBottomComponent(bottomJPanel);

        add(mainJSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void annotationDisplayOptionJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annotationDisplayOptionJComboBoxActionPerformed
// TODO add your handling code here:
        convertToHorizontalSentence();
    }//GEN-LAST:event_annotationDisplayOptionJComboBoxActionPerformed

    private void treeJTreeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeJTreeKeyTyped
// TODO add your handling code here:
        int c = evt.getKeyCode();
        char ch = evt.getKeyChar();
        int mods = evt.getModifiers();

        if (c != java.awt.event.KeyEvent.CHAR_UNDEFINED)
        {
            initNodeLabel(c, ch, mods);
        }
    }//GEN-LAST:event_treeJTreeKeyTyped

    private void treeJTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_treeJTreeValueChanged
    {//GEN-HEADEREND:event_treeJTreeValueChanged
        // TODO add your handling code here:
        if (savingNode == true)
        {
            return;
        }

        JTree jtree = getJTree();

        TreePath prevSelection = evt.getOldLeadSelectionPath();
        TreePath newSelection = evt.getNewLeadSelectionPath();

        if (jtree.getSelectionCount() > 1)
        {
            nodeTextJTextArea.setEditable(false);
            nodeTextJTextArea.setText("");
            removeTreeNodeJPanel();
            return;
        }

        if (prevSelection != null && treeNodeJPanel != null && jtree.getSelectionCount() <= 1)
        {
            SanchayMutableTreeNode prevNode = (SanchayMutableTreeNode) (prevSelection.getLastPathComponent());
            saveTreeNode(prevNode);
            saveTreeNodeText(prevNode);

            savingNode = true;

            SanchayMutableTreeNode parent = (SanchayMutableTreeNode) (prevNode.getParent());
            getModel().reload(parent);
            expandAll(null);

            jtree.setSelectionPath(newSelection);

            savingNode = false;

//	    setVisible(false);
//	    setVisible(true);
        }

        editTreeNode(evt);
        editTreeNodeText(evt);
    }//GEN-LAST:event_treeJTreeValueChanged

    private void nodeLabelJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nodeLabelJComboBoxActionPerformed
    {//GEN-HEADEREND:event_nodeLabelJComboBoxActionPerformed
// TODO add your handling code here:
        JTree jtree = getJTree();

        TreePath selections[] = jtree.getSelectionPaths();
        saveTreeNodeLabel(evt);
        setVisible(false);
        setVisible(true);

        if (mode == SSF_MODE)
        {
            convertToHorizontalSentence();
        }

        jtree.setSelectionPaths(selections);
    }//GEN-LAST:event_nodeLabelJComboBoxActionPerformed

    private void nodeLabelJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_nodeLabelJComboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_nodeLabelJComboBoxItemStateChanged

    private void controlsJTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_controlsJTabbedPaneStateChanged
        // TODO add your handling code here:
        validate();
    }//GEN-LAST:event_controlsJTabbedPaneStateChanged

    private void fsJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fsJComboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_fsJComboBoxItemStateChanged

    private void fsJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fsJComboBoxActionPerformed
        // TODO add your handling code here:
         selectFSS2Edit();
    }//GEN-LAST:event_fsJComboBoxActionPerformed

    private void applyFSJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyFSJButtonActionPerformed
        // TODO add your handling code here:
        saveEditedFSS();
    }//GEN-LAST:event_applyFSJButtonActionPerformed

    private void addFSJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFSJButtonActionPerformed
        // TODO add your handling code here:
        addFeatureStructure(evt);
    }//GEN-LAST:event_addFSJButtonActionPerformed

    private void removeFSJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFSJButtonActionPerformed
        // TODO add your handling code here:
        removeFeatureStructure(evt);
    }//GEN-LAST:event_removeFSJButtonActionPerformed

    public TreeAction[] getActions()
    {
        return actions;
    }

    public int[] getCommands()
    {
        int commands[] = new int[actions.length];

        for (int i = 0; i < commands.length; i++)
        {
            commands[i] = actions[i].getCommand();

        }

        return commands;
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    public void prepareCommands(int appliedCommands[], int mode)
    {
//        treeJPopupMenu.removeAll();
//        commandsJPanel.removeAll();
//        treeJPopupMenu.setLayout(null);
//        commandsJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 2));

        allCommands = new boolean[TreeAction._TOTAL_ACTIONS_];
        actions = new TreeAction[TreeAction._TOTAL_ACTIONS_];

        for (int i = 0; i < allCommands.length; i++)
        {
            allCommands[i] = true;
            actions[i] = TreeAction.createAction(this, i);
        }

        if (appliedCommands != null)
        {
            for (int i = 0; i < allCommands.length; i++)
            {
                allCommands[i] = false;
            }

            for (int i = 0; i < appliedCommands.length; i++)
            {
                int cmd = appliedCommands[i];
                allCommands[cmd] = true;

                JMenuItem mi = new JMenuItem();
                mi.setAction(actions[cmd]);
                treeJPopupMenu.add(mi);

                JButton jb = new JButton(actions[cmd]);
                jb.setAction(actions[cmd]);
                Font font = jb.getFont();
                jb.setFont(font.deriveFont(10));
                commandsJPanel.add(jb);
            }

            ((GridLayout) commandsJPanel.getLayout()).setRows(appliedCommands.length);
            ((GridLayout) commandsJPanel.getLayout()).setVgap(4);
        } else
        {
            for (int i = 0; i < allCommands.length; i++)
            {
                JMenuItem mi = new JMenuItem();
                mi.setAction(actions[i]);
                treeJPopupMenu.add(mi);

                JButton jb = new JButton(actions[i]);
                jb.setAction(actions[i]);
                commandsJPanel.add(jb);
            }

            ((GridLayout) commandsJPanel.getLayout()).setRows(allCommands.length);
            ((GridLayout) commandsJPanel.getLayout()).setVgap(4);
        }

        treeJPopupMenu.setInvoker(treeJTree);

        controlTabsShown = true;
    }

    public void setFontSizes(int size)
    {
        ((MultiLingualTreeCellRenderer)  treeJTree.getCellRenderer()).setFontSize(size);
        UtilityFunctions.setComponentFont(senJTextArea, langEnc, size);
        UtilityFunctions.setComponentFont(nodeTextJTextArea, langEnc, size);
    }

    private void setFonts()
    {
//	UtilityFunctions.setComponentFont(this, language);
//	UtilityFunctions.setComponentFont(treeJTree, language);
        UtilityFunctions.setComponentFont(senJTextArea, langEnc);
        UtilityFunctions.setComponentFont(nodeTextJTextArea, langEnc);
    }

    public void increaseFontSizes()
    {
        ((MultiLingualTreeCellRenderer)  treeJTree.getCellRenderer()).increaseFontSize();
        UtilityFunctions.increaseFontSize(senJTextArea);
        UtilityFunctions.increaseFontSize(nodeTextJTextArea);
        
        JComponent comp = (JComponent) fsEditJPanel.getComponent(1);

        if(comp != null)
        {
            UtilityFunctions.increaseFontSize(comp);
        }
        
        comp = (JComponent) fsEditJPanel.getComponent(7);

        if(comp != null)
        {
            UtilityFunctions.increaseFontSize(comp);
        }
    }

    public void decreaseFontSizes()
    {
        ((MultiLingualTreeCellRenderer)  treeJTree.getCellRenderer()).decreaseFontSize();
        UtilityFunctions.decreaseFontSize(senJTextArea);
        UtilityFunctions.decreaseFontSize(nodeTextJTextArea);
        
        JComponent comp = (JComponent) fsEditJPanel.getComponent(1);

        if(comp != null)
        {
            UtilityFunctions.decreaseFontSize(comp);
        }
        
        comp = (JComponent) fsEditJPanel.getComponent(7);

        if(comp != null)
        {
            UtilityFunctions.decreaseFontSize(comp);
        }
    }

    private void removeTreeNodeJPanel()
    {
        if (nodeEditJPanel.getComponentCount() > 0)
        {
            nodeEditJPanel.remove(0);
            treeNodeJPanel = null;
        }
    }

    public void initTreeJPanel()
    {
        if (mode == SSF_MODE)
        {
//	    senJScrollPane.remove(senJTextArea);
//	    JTextArea ta = new JTextArea();
//	    ta.setFont(SanchayLanguages.getLangEncFont(language));
//	    ta.setText( ((SSFNode) treeJTree.getModel().getRoot()).convertToBracketForm(1));
//	    senJScrollPane.setViewportView(ta);
//	    senJTextArea.setFont(SanchayLanguages.getLangEncFont("tel::utf8"));
            convertToHorizontalSentence();
        }
    }

    private void initNodeLabel(int c, char ch, int mods)
    {
        if (nodeLabelEditors == null)
        {
            return;
        }

        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            ComboBoxModel nodeLabelEditor = null;

            if (currentNode instanceof SSFPhrase)
            {
                nodeLabelEditor = (ComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("PhraseNames"));
            } else
            {
                nodeLabelEditor = (ComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("POSTags"));
            }

            if (mode == SSF_MODE)
            {
                int newSelIndex = 0;

                String cStr = new String(new char[]
                        {
                            ch
                        });

                if (ch == '<' || ch == '>')
                {
                    String prevName = ((SSFNode) currentNode).getName();
                    int ind = UtilityFunctions.findIndexOfEqualObject(nodeLabelEditor, prevName);

                    if (ind >= -1 && ind < nodeLabelJComboBox.getItemCount() - 1 && ch == '>')
                    {
                        newSelIndex = ind + 1;
                    } else if ((ind == -1 || ind >= 1) && ind < nodeLabelJComboBox.getItemCount() && ch == '<')
                    {
                        newSelIndex = ind - 1;
                    }

                    String newName = (String) nodeLabelJComboBox.getItemAt(newSelIndex);
                    ((SSFNode) currentNode).setName(newName);
                    nodeLabelJComboBox.setSelectedItem(newSelIndex);
                    jtree.updateUI();
                } //        else if(c >= java.awt.event.KeyEvent.VK_A && c <= java.awt.event.KeyEvent.VK_Z)
                else if (Character.isLetter((int) ch))
                {
                    String prevName = ((SSFNode) currentNode).getName();

                    if (prevName.startsWith(cStr.toUpperCase()) || prevName.startsWith(cStr.toLowerCase()))
                    {
                        newSelIndex = UtilityFunctions.findIndexOfEqualObject(nodeLabelEditor, prevName);
                        String newName = (String) nodeLabelJComboBox.getItemAt(newSelIndex + 1);
                        ((SSFNode) currentNode).setName(newName);

                        nodeLabelJComboBox.setModel(nodeLabelEditor);
                        nodeLabelJComboBox.setEnabled(true);
                        nodeLabelJComboBox.setSelectedItem(newSelIndex + 1);

                        jtree.updateUI();
                    } else
                    {
                        int count = nodeLabelJComboBox.getItemCount();

                        for (int i = 0; i < count; i++)
                        {
                            String lbl = (String) nodeLabelJComboBox.getItemAt(i);

                            if (lbl.startsWith(cStr.toUpperCase()) || lbl.startsWith(cStr.toLowerCase()))
                            {
                                newSelIndex = i;

                                String newName = (String) nodeLabelJComboBox.getItemAt(newSelIndex);
                                ((SSFNode) currentNode).setName(newName);

                                nodeLabelJComboBox.setModel(nodeLabelEditor);
                                nodeLabelJComboBox.setEnabled(true);
                                nodeLabelJComboBox.setSelectedItem(newSelIndex);

                                jtree.updateUI();

                                break;
                            }
                        }
                    }
                }
            }
        }

//	if(jtree.isEditable() == false)
//	    return;

        treeJTree.requestFocusInWindow();
    }

    public Frame getOwner()
    {
        return owner;
    }

    public void setOwner(Frame frame)
    {
        owner = (JFrame) frame;
    }

    public void setParentComponent(Component parentComponent)
    {
        this.parentComponent = parentComponent;
    }

    public void setDialog(JDialog d)
    {
        dialog = d;
    }

    public SanchayTableModel getFSSchema()
    {
        if (fsSchema == null)
        {
            try
            {
                fsSchema = new SanchayTableModel(GlobalProperties.resolveRelativePath("props/fs-schema.txt"),
                        GlobalProperties.getIntlString("UTF-8"));
            } catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return fsSchema;
    }

    public void setModel(SanchayTreeModel tm)
    {
        treeJTree.setModel(tm);
    }

    public SanchayTreeModel getModel()
    {
        return (SanchayTreeModel) treeJTree.getModel();
    }

    public JTree getJTree()
    {
        return treeJTree;
    }

    public void showControlTabs(boolean b)
    {
        controlsJTabbedPane.setVisible(b);
    }

    public void showSentence(boolean b)
    {
        senJPanel.setVisible(b);
    }

    public void showNodeEditor(boolean b)
    {
        bottomJPanel.setVisible(b);
    }

    public boolean getNodeTextEditable()
    {
        return nodeTextEditable;
    }

    public void setNodeTextEditable(boolean b)
    {
        nodeTextEditable = b;
    }

    public void saveTreeNode(SanchayMutableTreeNode node)
    {
        if (treeNodeJPanel == null)
        {
            return;
        }

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
            if (node.getClass().equals(SSFPhrase.class) || node.getClass().equals(SSFLexItem.class))
            {
                if (treeNodeJPanel.getClass().equals(SanchayTableJPanel.class))
                {
                    FeatureStructures fss = ((SSFNode) node).getFeatureStructures();

                    if (fss == null)
                    {
                        fss = new FeatureStructuresImpl();
                        fss.setToEmpty();
                    }

                    if (fss.countAltFSValues() <= 0)
                    {
                        fss.addAltFSValue(new FeatureStructureImpl());
                    }

                    SanchayTableModel ft = ((SanchayTableJPanel) treeNodeJPanel).getModel();
                    fss.getAltFSValue(0).setFeatureTable(ft);
                }
            }
        } else if (mode == FS_MODE)
        {
        }
    }

    public void saveTreeNodeText(SanchayMutableTreeNode node)
    {
        if (nodeTextEditable == false)
        {
            return;
        }

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
            if (node.getClass().equals(SSFLexItem.class))
            {
                String nodeText = nodeTextJTextArea.getText();

                nodeText = nodeText.replaceAll("\n", "");

                if (nodeText.equals("") == false)
                {
                    ((SSFNode) node).setLexData(nodeText);
                }
            }
        } else if (mode == FS_MODE)
        {
        }
    }

    public void saveTreeNodeLabel(EventObject evt)
    {
        JTree jtree = getJTree();

//	if(jtree.isEditable() == false)
//	    return;

        TreePath currentSelection = jtree.getSelectionPath();

        String label = (String) nodeLabelJComboBox.getSelectedItem();

        if (label == null)
        {
            label = "";
        }

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                if (currentNode.getClass().equals(SSFPhrase.class) || currentNode.getClass().equals(SSFLexItem.class))
                {
                    // Edit phrase name or POS tag
                    ((SSFNode) currentNode).setName(label);
                }
            } else if (mode == FS_MODE)
            {
                if (currentNode.getClass().equals(FeatureAttributeImpl.class))
                {
                    // Edit attribute name
                    ((FeatureAttribute) currentNode).setName(label);
                } else if (currentNode.getClass().equals(FeatureStructureImpl.class))
                {
                    // Edit FS name
                    ((FeatureStructure) currentNode).setName(label);
                } else if (currentNode.getClass().equals(FeatureValueImpl.class))
                {
                    // Edit attribute value
                    ((FeatureValue) currentNode).setValue(label);
                }
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent,"Select a node.","Error", JOptionPane.ERROR_MESSAGE);

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    /**
     * Action implementations which are called from Actions
     */
    public void addTreeNode(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                // Add a NULL SSFLexItem node
//		if(((SSFNode) currentNode).isLeafNode() == true)
//		{
//		    JOptionPane.showMessageDialog(parentComponent, "Please select a phrase, not a lexical item.","Error", JOptionPane.ERROR_MESSAGE);
//		    return;
//		}

                MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
                if (parent != null)
                {
                    try
                    {
                        FeatureStructuresImpl fss = new FeatureStructuresImpl();
                        fss.setToEmpty();
                        SSFLexItem lex = new SSFLexItem("", GlobalProperties.getIntlString("NULL"), GlobalProperties.getIntlString("NULL"), fss);
                        lex.clearFeatureStructures();
                        ((SSFNode) currentNode).add(lex);
                        getModel().reload(parent);
                        expandAll(null);
                        convertToHorizontalSentence();
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            } else if (mode == FS_MODE)
            {
                if (currentNode.getClass().equals(FeatureStructuresImpl.class))
                {
                    // Add FS
                    FeatureStructureImpl fs = new FeatureStructureImpl();
                    fs.setName(GlobalProperties.getIntlString("fs"));
                    ((FeatureStructures) rootNodeCopy).addAltFSValue(fs);

                    getModel().reload();
                    expandAll(null);
                } else if (currentNode.getClass().equals(FeatureStructureImpl.class))
                {
                    // Add attribute
                    FeatureStructureImpl fs = (FeatureStructureImpl) currentNode;
                    SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();

                    FeatureAttributeImpl fa = new FeatureAttributeImpl();
                    fa.setName(GlobalProperties.getIntlString("New_Attribute"));
                    fs.addAttribute(fa);

                    getModel().reload(parent);
                    expandAll(null);
                } else if (currentNode.getClass().equals(FeatureAttributeImpl.class))
                {
                    // Add attribute value
                    FeatureAttributeImpl fa = (FeatureAttributeImpl) currentNode;

                    if (FeatureStructuresImpl.getFSProperties().isMandatory(fa.getName()) == false)
                    {
                        Object[] possibleValues =
                        {
                            GlobalProperties.getIntlString("Feature_Value"), GlobalProperties.getIntlString("Feature_Structure")
                        };
                        Object selectedValue = JOptionPane.showInputDialog(parentComponent,
                                GlobalProperties.getIntlString("Select_the_type_of_value"), GlobalProperties.getIntlString("Add_Value"), JOptionPane.INFORMATION_MESSAGE, null,
                                possibleValues, possibleValues[0]);

                        if (selectedValue == null || selectedValue.equals("") == true)
                        {
                            return;
                        }

                        SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();

                        if (selectedValue.equals(GlobalProperties.getIntlString("Feature_Value")))
                        {
                            String inputValue = JOptionPane.showInputDialog(parentComponent, GlobalProperties.getIntlString("Type_the_attribute_value:"), "");

                            if (inputValue == null || inputValue.equals("") == true)
                            {
                                return;
                            }

                            FeatureValue fv = new FeatureValueImpl(inputValue);
                            fa.addAltValue(fv);
                        } else if (selectedValue.equals(GlobalProperties.getIntlString("Feature_Structure")))
                        {
                            FeatureStructureImpl fv = new FeatureStructureImpl("");
                            fa.addAltValue(fv);
                        }

                        getModel().reload(parent);
                        expandAll(null);
                    } else
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Mandatory_attributes_cannot_have_more_than_one_values."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent, "Select a node.","Error", JOptionPane.ERROR_MESSAGE);

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void copyTreeNode(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                String str = ((SSFNode) currentNode).getName();

                FeatureStructures ifs = ((SSFNode) currentNode).getFeatureStructures();

                if (ifs != null)
                {
                    str += "\t" + ifs.makeString();
                }

                SSFNodeTransfer nodeTransfer = new SSFNodeTransfer();
                nodeTransfer.setClipboardContents(str);
            } else if (mode == FS_MODE)
            {
            }
        }
//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void cutTreeNode(EventObject e)
    {
        JTree jtree = getJTree();

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
        } else if (mode == FS_MODE)
        {
        }

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void deleteSubTree(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                if ((currentNode.getClass().equals(SSFLexItem.class) && ((SSFNode) currentNode).getLexData().equals(GlobalProperties.getIntlString("NULL"))) || (currentNode.getClass().equals(SSFPhrase.class) && currentNode.getChildCount() == 0) || (currentNode.getChildCount() == 1 && ((SSFNode) currentNode.getChildAt(0)).getLexData().equals(GlobalProperties.getIntlString("NULL"))))
                {
                    SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();
                    parent.remove(currentNode);

                    getModel().reload(parent);
                    expandAll(null);
                    convertToHorizontalSentence();
                }
            } else if (mode == FS_MODE)
            {
                if (currentNode.getClass().equals(FeatureStructuresImpl.class))
                {
                    // Delete FS
                    SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();
                    parent.remove(currentNode);

                    getModel().reload(parent);
                    expandAll(null);
                } else if (currentNode.getClass().equals(FeatureStructureImpl.class))
                {
                    // Delete attribute
                    FeatureAttributeImpl fa = (FeatureAttributeImpl) currentNode;

                    if (FeatureStructuresImpl.getFSProperties().isMandatory(fa.getName()) == false)
                    {
                        SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();
                        parent.remove(currentNode);

                        getModel().reload(parent);
                        expandAll(null);
                    } else
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Mandatory_attribute_cannot_be_deleted."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    }
                } else if (currentNode.getClass().equals(FeatureAttributeImpl.class))
                {
                    // Delete attribute value
                    SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();
                    FeatureAttribute fa = (FeatureAttributeImpl) parent;

                    if (FeatureStructuresImpl.getFSProperties().isMandatory(fa.getName()) == false)
                    {
                        parent.remove(currentNode);

                        getModel().reload(parent);
                        expandAll(null);
                    } else
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("The_value_of_a_mandatory_attribute_cannot_be_deleted."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent, "Select a node.","Error", JOptionPane.ERROR_MESSAGE);

        jtree.requestFocusInWindow();
    }

    public void addTreeNodeLayer(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection[] = jtree.getSelectionPaths();

        if (currentSelection == null || currentSelection.length <= 0)
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("You_did_not_select_nodes."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
            SSFNode firstNode = (SSFNode) (currentSelection[0].getLastPathComponent());
            SSFPhrase parent = (SSFPhrase) (firstNode.getParent());

            int fromChild = parent.findChild(firstNode);
            int count = currentSelection.length;

            Integer indices[] = new Integer[count];

            // Selected nodes should be at the same level
            for (int i = 0; i < count; i++)
            {
                if (count > 1 && currentSelection[0].getParentPath().equals(currentSelection[i].getParentPath()) == false)
                {
                    JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Wrong_selection_for_forming_a_phrase."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SSFNode node = (SSFNode) (currentSelection[i].getLastPathComponent());
                indices[i] = Integer.valueOf(parent.findChild(node));
            }

            Arrays.sort(indices);

            if (count > 1)
            {
                for (int i = 1; i < count; i++)
                {
                    if (indices[i].intValue() != indices[i - 1].intValue() + 1)
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Wrong_selection_for_forming_a_phrase."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            try
            {
                parent.formPhrase(indices[0].intValue(), currentSelection.length);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            getModel().reload(parent);
            expandAll(null);
            convertToHorizontalSentence();
        } else if (mode == FS_MODE)
        {
        }

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void deleteTreeNodeLayer(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                // Remove phrase
                if (((SSFNode) currentNode).isLeafNode() == true)
                {
                    JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("The_node_you_have_selected_is_a_lexical_item,_not_a_phrase."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
                if (parent != null)
                {
                    ((SSFNode) currentNode).removeLayer();
                    getModel().reload(parent);
                    expandAll(null);
                    convertToHorizontalSentence();
                }
            } else if (mode == FS_MODE)
            {
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent, "Select a node.","Error", JOptionPane.ERROR_MESSAGE);

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void shiftNodeLeft(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();

                ((SSFNode) currentNode).shiftLeft();

                getModel().reload(parent);
                expandAll(null);
                convertToHorizontalSentence();
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent, "Select a node.","Error", JOptionPane.ERROR_MESSAGE);

        jtree.requestFocusInWindow();
    }

    public void shiftNodeRight(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();

                ((SSFNode) currentNode).shiftRight();

                getModel().reload(parent);
                expandAll(null);
                convertToHorizontalSentence();
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent, "Select a node.","Error", JOptionPane.ERROR_MESSAGE);

        jtree.requestFocusInWindow();
    }

    public void globalNodeStats(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                sanchay.corpus.stats.CorpusStatistics corpusStatistics = CorpusStatisticsFactory.getGlobalCorpusStatisticsReference();

                SanchayTableModel statsTable = new SanchayTableModel(new String[]{"Type", "Data"}, 4);
                
                SSFNode ssfNode = (SSFNode) currentNode;

                String depAttribs[] = FSProperties.getDependencyTreeAttributes();

                String lexData = ssfNode.getLexData();
                String tag = ssfNode.getName();
                
                String attribs[] = ssfNode.getOneOfAttributeValues(depAttribs);

                statsTable.setValueAt("Tags for Word", 0, 0);
                statsTable.setValueAt(corpusStatistics.getTagsForWord(lexData), 0, 1);

                statsTable.setValueAt("Words for Tag", 1, 0);
                statsTable.setValueAt(corpusStatistics.getWordsForTag(tag), 1, 1);

                if(attribs != null)
                {
                    statsTable.setValueAt("Chunks for Relation", 2, 0);
                    statsTable.setValueAt(corpusStatistics.getChunksForRelation(attribs[0] + "=" + attribs[1].split(":")[0]), 2, 1);

                    statsTable.setValueAt("Relations for Chunk", 3, 0);
                    statsTable.setValueAt(corpusStatistics.getRelationsForChunk(ssfNode.makeRawSentence()), 3, 1);
                }

                SanchayJDialog statsDialog = null;

                SanchayTableJPanel statsJPanel = SanchayTableJPanel.createTableDisplayJPanel(statsTable, langEnc, true);

                statsJPanel.getJTable().setRowHeight(80);
                statsJPanel.getJTable().setDefaultRenderer(String.class, new MultiLineCellRenderer(langEnc, 3, 100));
                statsJPanel.getJTable().setDefaultEditor(String.class, new SanchayTableCellEditor(langEnc, SanchayTableCellEditor.MULTIPLE_ROW));

                if(owner != null)
                    statsDialog = new SanchayJDialog(owner, "Node Statistics", false, statsJPanel);
                else if(dialog != null)
                    statsDialog = new SanchayJDialog(dialog, "Node Statistics", false, statsJPanel);

                UtilityFunctions.maxmize(statsDialog);
                UtilityFunctions.fitColumnsToContent(statsJPanel.getJTable());
                statsDialog.setVisible(true);
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent, "Select a node.","Error", JOptionPane.ERROR_MESSAGE);

        jtree.requestFocusInWindow();
    }

    public void editTree(EventObject e)
    {
        JTree jt = getJTree();

        Action a = ((AbstractButton) e.getSource()).getAction();

        if (jt.isEditing() == true)
        {
            jt.getCellEditor().stopCellEditing();
        }

        if (jt.isEditable())
        {
            a.putValue(Action.NAME, GlobalProperties.getIntlString("Edit_On"));
            jt.setEditable(false);
        }
        else
        {
            a.putValue(Action.NAME, GlobalProperties.getIntlString("Edit_Off"));
            jt.setEditable(true);
        }

        jt.requestFocusInWindow();
    }

    public void editTreeNode(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            // Edit the node label
            editTreeNodeLabel(e);
            editTreeNodeText(e);

            removeTreeNodeJPanel();

            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                // Edit FS
                FeatureStructures fss = ((SSFNode) currentNode).getFeatureStructures();

                if (fss == null)
                {
                    fss = new FeatureStructuresImpl();
                    fss.setToEmpty();
                }

                if (fss.countAltFSValues() <= 0)
                {
                    fss.addAltFSValue(new FeatureStructureImpl());
                }

                MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
                if (parent != null)
                {
                    if (fss.isDeep() == true || ((SSFNode) getJTree().getModel().getRoot()).allowsNestedFS() == true)
                    {
                        treeNodeJPanel = createSSFFeatureStructureJPanel((SanchayMutableTreeNode) fss, langEnc);
                        nodeEditJPanel.add(treeNodeJPanel, java.awt.BorderLayout.CENTER);
                    } else
                    {
                        getFSSchema();

                        SanchayTableModel sanchayTableModel = fss.getAltFSValue(0).getFeatureTable();

                        sanchayTableModel.setSchema(fsSchema);

//			treeNodeJPanel = SanchayTableJPanel.createFeatureTableJPanel(sanchayTableModel, language);
                        treeNodeJPanel = SanchayTableJPanel.createFeatureTableJPanel(sanchayTableModel, GlobalProperties.getIntlString("eng"));

                        nodeEditJPanel.add(treeNodeJPanel, java.awt.BorderLayout.CENTER);
                    }

//		    getModel().reload(parent);
//		    expandAll(null);
                    setVisible(false);
                    setVisible(true);
                }
            } else if (mode == FS_MODE)
            {
                // Edit attribute value
                if (currentNode.getClass().equals(FeatureValueImpl.class))
                {
                    FeatureValueImpl fv = (FeatureValueImpl) currentNode;

                    SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();
                    String inputValue = JOptionPane.showInputDialog(parentComponent, GlobalProperties.getIntlString("Type_the_attribute_value:"), fv.getValue());

                    if (inputValue == null || inputValue.equals("") == true)
                    {
                        return;
                    }

                    fv.setValue(inputValue);

                    getModel().reload(parent);
                    expandAll(null);
                } else if (currentNode.getClass().equals(FeatureAttributeImpl.class))
                {
                    FeatureAttributeImpl fa = (FeatureAttributeImpl) currentNode;

                    if (fa.countAltValues() <= 0)
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Add_a_Feature_Value_first."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (fa.countAltValues() > 1)
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Select_an_Attribute_Value."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    FeatureValue fv = fa.getAltValue(0);

                    if (fv.getClass().equals(FeatureValueImpl.class))
                    {
                        SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();
                        String inputValue = JOptionPane.showInputDialog(parentComponent, GlobalProperties.getIntlString("Type_the_attribute_value:"), fv.getValue());

                        if (inputValue == null || inputValue.equals("") == true)
                        {
                            return;
                        }

                        fv.setValue(inputValue);

                        getModel().reload(parent);
                        expandAll(null);
                    } else
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Select_an_Attribute_Value."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent, "Select a node.", "Error", JOptionPane.ERROR_MESSAGE);

//	jtree.setSelectionPath(currentSelection);

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void editTreeNodeText(EventObject e)
    {
        if (nodeTextEditable == false)
        {
            return;
        }

        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        nodeTextJTextArea.setEditable(false);
        nodeTextJTextArea.setText("");

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                String nodeText = "";

                if (currentNode.getClass().equals(SSFPhrase.class))
                {
                } else if (currentNode.getClass().equals(SSFLexItem.class))
                {
                    nodeText = ((SSFNode) currentNode).getLexData();
                    nodeTextJTextArea.setEditable(true);
                    nodeTextJTextArea.setText(nodeText);
                }
            } else if (mode == FS_MODE)
            {
            }
        }

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void editTreeNodeLabel(EventObject e)
    {
        if (nodeLabelEditors == null)
        {
            return;
        }

        JTree jtree = getJTree();

//	if(jtree.isEditable() == false)
//	    return;

        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
                nodeLabelJComboBox.setEnabled(false);
            } else if (mode == SSF_MODE)
            {
                if (currentNode.getClass().equals(SSFPhrase.class))
                {
                    // Edit phrase name
                    DefaultComboBoxModel labelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("PhraseNames"));
//		    System.out.println("PhraseNames: " + labelEditor.getSize());
                    nodeLabelJComboBox.setModel(labelEditor);
                    nodeLabelJComboBox.setEnabled(true);
                    nodeLabelJComboBox.setSelectedItem(((SSFNode) currentNode).getName());

                    tagsTabJPanel.removeAll();

                    int lcount = labelEditor.getSize();

                    for (int i = 0; i < lcount; i++) {

                        JButton btn = new JButton();

                        Action act = new NodeEditingTreeAction(jtree, (String) labelEditor.getElementAt(i));

                        btn.setAction(act);

                        tagsTabJPanel.add(btn);
                    }

                    FeatureStructures cnfss = ((SSFNode) currentNode).getFeatureStructures();

                    if(cnfss != null)
                    {
                        fsJComboBox.setModel(UtilityFunctions.getComboBoxModel(cnfss));

                        if(cnfss.countAltFSValues() == 0)
                        {
                            FeatureStructure ifs = new FeatureStructureImpl();

                            ifs.addMandatoryAttributes();

                            cnfss.addAltFSValue(ifs);
                        }

                        fsEditJPanel.removeAll();

                        UtilityFunctions.fillMandatoryFSEditJPanel(this, cnfss.getAltFSValue(0), fsEditJPanel, langEnc);

                        selectFSS2Edit();
                    }

                } else if (currentNode.getClass().equals(SSFLexItem.class))
                {
                    // Edit POS tag
                    DefaultComboBoxModel labelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("POSTags"));
//		    System.out.println("POSTags: " + labelEditor.getSize());
                    nodeLabelJComboBox.setModel(labelEditor);
                    nodeLabelJComboBox.setEnabled(true);
//		    labelJComboBox.setSelectedItem(((SSFNode) currentNode));
                    nodeLabelJComboBox.setSelectedItem(((SSFNode) currentNode).getName());

                    tagsTabJPanel.removeAll();

                    int lcount = labelEditor.getSize();

                    for (int i = 0; i < lcount; i++) {

                        JButton btn = new JButton();

                        Action act = new NodeEditingTreeAction(jtree, (String) labelEditor.getElementAt(i));

                        btn.setAction(act);

                        tagsTabJPanel.add(btn);
                    }

                    FeatureStructures cnfss = ((SSFNode) currentNode).getFeatureStructures();

                    if(cnfss != null)
                    {
                        fsJComboBox.setModel(UtilityFunctions.getComboBoxModel(cnfss));

                        if(cnfss.countAltFSValues() == 0)
                        {
                            FeatureStructure ifs = new FeatureStructureImpl();

                            ifs.addMandatoryAttributes();

                            cnfss.addAltFSValue(ifs);
                        }

                        fsEditJPanel.removeAll();

                        UtilityFunctions.fillMandatoryFSEditJPanel(this, cnfss.getAltFSValue(0), fsEditJPanel, langEnc);

                        selectFSS2Edit();
                    }

                } else
                {
                    nodeLabelJComboBox.setEnabled(false);
                }
            } else if (mode == FS_MODE)
            {
                if (currentNode.getClass().equals(FeatureAttributeImpl.class))
                {
                    // Edit attribute name
                    DefaultComboBoxModel labelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("AttributeNames"));
                    nodeLabelJComboBox.setModel(labelEditor);
                    nodeLabelJComboBox.setEnabled(true);
                } else if (currentNode.getClass().equals(FeatureStructureImpl.class))
                {
                    // Edit FS name
                    DefaultComboBoxModel labelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("FSNames"));
                    nodeLabelJComboBox.setModel(labelEditor);
                    nodeLabelJComboBox.setEnabled(true);
                } else if (currentNode.getClass().equals(FeatureValueImpl.class))
                {
                    // Edit attribute value
                    DefaultComboBoxModel labelEditor = (DefaultComboBoxModel) nodeLabelEditors.get(GlobalProperties.getIntlString("AttributeValues"));
                    nodeLabelJComboBox.setModel(labelEditor);
                    nodeLabelJComboBox.setEnabled(true);
                } else
                {
                    nodeLabelJComboBox.setEnabled(false);
                }
            }
        }
//	else
//	    JOptionPane.showMessageDialog(parentComponent,"Select a node.","Error", JOptionPane.ERROR_MESSAGE);

//	jtree.setSelectionPath(currentSelection);

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    private void selectFSS2Edit()
    {
        FeatureStructure fsToEdit = (FeatureStructure) fsJComboBox.getSelectedItem();

        if(fsToEdit == null)
            return;

        if(fsToEdit.hasMandatoryAttribs() == false)
            fsToEdit.addMandatoryAttributes();

        int count = fsEditJPanel.getComponentCount();

        for (int i = 0; i < count; i++) {
            Component jc = fsEditJPanel.getComponent(i++);

            if(jc instanceof JLabel)
            {
                JComboBox jcb = (JComboBox) fsEditJPanel.getComponent(i);
                DefaultComboBoxModel dcbm = (DefaultComboBoxModel) jcb.getModel();

                ActionListener actionListeners[] = jcb.getActionListeners();

                for (int j = 0; j < actionListeners.length; j++) {
                    ActionListener actionListener = actionListeners[j];

                    if(actionListener instanceof AttributeValueActionListener)
                    {
                        ((AttributeValueActionListener) actionListener).setFeatureStructure(fsToEdit);
                        ((AttributeValueActionListener) actionListener).setDcbm(dcbm);
                    }
                }

                String name = ((JLabel) jc).getText();

                String val = (String) fsToEdit.getAttributeValue(name).getValue();

                if(dcbm.getIndexOf(val) == -1)
                    dcbm.addElement(val);

                if(dcbm.getIndexOf("") == -1)
                    dcbm.addElement("");

                if(dcbm.getIndexOf("Other") == -1)
                    dcbm.addElement("Other");

                jcb.setSelectedItem(val);
            }
        }
    }

    private void saveEditedFSS()
    {
        FeatureStructure fsToEdit = (FeatureStructure) fsJComboBox.getSelectedItem();

        int count = fsEditJPanel.getComponentCount();

        for (int i = 0; i < count; i++) {
            Component jc = fsEditJPanel.getComponent(i++);

            if(jc instanceof JLabel)
            {
                JComboBox jcb = (JComboBox) fsEditJPanel.getComponent(i);
                DefaultComboBoxModel dcbm = (DefaultComboBoxModel) jcb.getModel();

                String name = ((JLabel) jc).getText();

                String val = (String) dcbm.getSelectedItem();

                fsToEdit.setAttributeValue(name, val);
            }
        }
        
        editTreeNode(null);

        JTree jtree = getJTree();

        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void addFeatureStructure(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                FeatureStructures ifss = ((SSFNode) currentNode).getFeatureStructures();

                if (ifss == null)
                {
                    ifss = new FeatureStructuresImpl();
                    ((SSFNode) currentNode).setFeatureStructures(ifss);
                }

                FeatureStructure ifs = new FeatureStructureImpl();

                ifs.addMandatoryAttributes();

                ifss.addAltFSValue(ifs);

                fsJComboBox.setModel(UtilityFunctions.getComboBoxModel(ifss));

                fsJComboBox.setSelectedItem(ifs);

                fsEditJPanel.removeAll();

                UtilityFunctions.fillMandatoryFSEditJPanel(this, ifs, fsEditJPanel, langEnc);

                selectFSS2Edit();

            } else if (mode == FS_MODE)
            {
            }
        }
//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void removeFeatureStructure(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                FeatureStructures ifss = ((SSFNode) currentNode).getFeatureStructures();

                if (ifss != null)
                {
                    FeatureStructure ifs = (FeatureStructure) fsJComboBox.getSelectedItem();

                    FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();
                    int mcount = fsProperties.countMandatoryAttributes();

                    if(ifs.countAttributes() > mcount)
                    {
                        ifs.hasMandatoryAttribs(false);
                    }
                    else
                    {
                        int fsIndex = ifss.findAltFSValue(ifs);

                        ifss.removeAltFSValue(fsIndex);

                        fsJComboBox.removeItem(ifs);
                    }

                    selectFSS2Edit();
                }
            } else if (mode == FS_MODE)
            {
            }
        }
//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void selectNodeFS(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                FeatureStructures fss = ((SSFNode) currentNode).getFeatureStructures();

                if (fss == null)
                {
                    return;
                }

                int count = fss.countAltFSValues();

                String fsStrings[] = new String[count];

                for (int i = 0; i < count; i++)
                {
                    FeatureStructure fs = fss.getAltFSValue(i);
                    fsStrings[i] = fs.makeString();
                }

                for (int i = 0; i < count; i++)
                {
                    fss.removeAltFSValue(0);
                }

                String selectedFSString = (String) JOptionPane.showInputDialog(this,
                        GlobalProperties.getIntlString("Select_the_feature_structure"), GlobalProperties.getIntlString("Feature_Structures"), JOptionPane.INFORMATION_MESSAGE, null,
                        fsStrings, fsStrings[0]);

                if (selectedFSString == null || selectedFSString.equals(""))
                {
                    return;
                }

                FeatureStructure fs = new FeatureStructureImpl();

                try
                {
                    fs.readString(selectedFSString);
                } catch (Exception ex)
                {
                    Logger.getLogger(SanchayTreeJPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

                fss.addAltFSValue(fs);
            } else if (mode == FS_MODE)
            {
            }
        }

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void insertTreeNode(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                // Insert a NULL SSFLexItem node
//		if(((SSFNode) currentNode).isLeafNode() == false)
//		{
//		    JOptionPane.showMessageDialog(parentComponent, "Please select a lexical item, not a phrase.","Error", JOptionPane.ERROR_MESSAGE);
//		    return;
//		}

                MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
                if (parent != null)
                {
                    try
                    {
                        FeatureStructuresImpl fss = new FeatureStructuresImpl();
                        fss.setToEmpty();
                        SSFLexItem lex = new SSFLexItem("", GlobalProperties.getIntlString("NULL"), GlobalProperties.getIntlString("NULL"), fss);
                        lex.clearFeatureStructures();

                        if(parent.getIndex(currentNode) == parent.getChildCount() - 1)
                        {
                            int retVal = JOptionPane.showConfirmDialog(parentComponent, "Do you want to do add after the current node?", "Insert Node", JOptionPane.YES_NO_OPTION);

                            if(retVal == JOptionPane.YES_OPTION)
                                ((SSFPhrase) parent).addChild(lex);
                            else
                                ((SSFNode) parent).insert(lex, parent.getIndex(currentNode));
                        }
                        else
                            ((SSFNode) parent).insert(lex, parent.getIndex(currentNode));
                        
                        getModel().reload();
                        expandAll(null);
                        convertToHorizontalSentence();
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            } else if (mode == FS_MODE)
            {
            }
        }

        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void joinTreeNodes(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection[] = jtree.getSelectionPaths();

        if (currentSelection == null || currentSelection.length <= 0)
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("You_did_not_select_nodes."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
            int retVal = JOptionPane.showConfirmDialog(parentComponent, "The_selected_word_will_be_broken_into_two.\nAre_you_sure_you_want_to_do_this?", GlobalProperties.getIntlString("Joining_Words"), JOptionPane.YES_NO_OPTION);

            if (retVal == JOptionPane.NO_OPTION)
            {
                return;
            }

            SSFNode firstNode = (SSFNode) (currentSelection[0].getLastPathComponent());
            SSFPhrase parent = (SSFPhrase) (firstNode.getParent());

            int fromChild = parent.findChild(firstNode);
            int count = currentSelection.length;

            Integer indices[] = new Integer[count];

            // Selected nodes should be at the same level
            for (int i = 0; i < count; i++)
            {
                if (count > 1 && currentSelection[0].getParentPath().equals(currentSelection[i].getParentPath()) == false)
                {
                    JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Wrong_selection_for_joining_a_node."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SSFNode node = (SSFNode) (currentSelection[i].getLastPathComponent());
                indices[i] = Integer.valueOf(parent.findChild(node));
            }

            Arrays.sort(indices);

            if (count > 1)
            {
                for (int i = 1; i < count; i++)
                {
                    if (indices[i].intValue() != indices[i - 1].intValue() + 1)
                    {
                        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Wrong_selection_for_joining_a_node."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (((SSFPhrase) parent).joinNodes(indices[0].intValue(), currentSelection.length) == false)
            {
                JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Wrong_selection_for_joining_a_node."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            }

            getModel().reload(parent);
            expandAll(null);
            convertToHorizontalSentence();
        } else if (mode == FS_MODE)
        {
        }

//        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void splitTreeNode(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                int retVal = JOptionPane.showConfirmDialog(parentComponent, "The selected word will be split into two.\nAre you sure you want to do this?", GlobalProperties.getIntlString("Splitting_Words"), JOptionPane.YES_NO_OPTION);

                if (retVal == JOptionPane.NO_OPTION)
                {
                    return;
                }

                SanchayMutableTreeNode parent = (SanchayMutableTreeNode) currentNode.getParent();

                if (((SSFPhrase) parent).splitLexItem(((SSFPhrase) parent).findChild((SSFNode) currentNode)) == false)
                {
                    JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Wrong_selection_for_splitting_a_node."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                }

                getModel().reload(parent);
                expandAll(null);
                convertToHorizontalSentence();
            } else if (mode == FS_MODE)
            {
            }
        }

        jtree.requestFocusInWindow();
    }

    public void pasteTreeNode(EventObject e)
    {
        JTree jtree = getJTree();
        TreePath currentSelection = jtree.getSelectionPath();

        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());
            SSFPhrase parent = (SSFPhrase) currentNode.getParent();

            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                SSFNodeTransfer ssfNodeTransfer = new SSFNodeTransfer();

                String str = ssfNodeTransfer.getClipboardContents();

                String parts[] = str.split("[\t]");

                if (parts != null)
                {
                    if (parts[0] != null && parts[0].equals("") == false)
                    {
                        int retVal = JOptionPane.showConfirmDialog(parentComponent, "Do you want to copy the tag too?", "Paste Node", JOptionPane.YES_NO_OPTION);

                        if(retVal == JOptionPane.YES_OPTION)
                            ((SSFNode) currentNode).setName(parts[0]);
                    }

                    if (parts.length > 1 && parts[1] != null && parts[1].equals("") == false)
                    {
                        FeatureStructures fss = ((SSFNode) currentNode).getFeatureStructures();
                        String lex = ((SSFNode) currentNode).getLexData();
                        String name = ((SSFNode) currentNode).getAttributeValue("name");

                        if (fss == null)
                        {
                            fss = new FeatureStructuresImpl();
                            ((SSFNode) currentNode).setFeatureStructures(fss);
                        } else
                        {
                            lex = ((SSFNode) currentNode).getAttributeValue(GlobalProperties.getIntlString("lex"));
                        }

                        int count = fss.countAltFSValues();

                        for (int i = 0; i < count; i++)
                        {
                            fss.removeAltFSValue(0);
                        }

                        FeatureStructure fs = new FeatureStructureImpl();

                        try
                        {
                            fs.readString(parts[1]);
                        } catch (Exception ex)
                        {
                            Logger.getLogger(SanchayTreeJPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        fss.addAltFSValue(fs);

                        if (lex != null && lex.equals("") == false)
                        {
                            ((SSFNode) currentNode).setAttributeValue(GlobalProperties.getIntlString("lex"), lex);
                        }

                        if (name != null && name.equals("") == false)
                        {
                            ((SSFNode) currentNode).setAttributeValue("name", name);
                        }

                        editTreeNode(e);
                    }
                }
            } else if (mode == FS_MODE)
            {
            }
        }

        jtree.updateUI();
        jtree.requestFocusInWindow();
    }

    public void saveTree(EventObject e)
    {
        JTree jtree = getJTree();

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
        } else if (mode == FS_MODE)
        {
            try
            {
                ((FeatureStructures) rootNode).readString(((FeatureStructures) rootNodeCopy).makeString());
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            dialog.setVisible(false);
        }

        jtree.requestFocusInWindow();
    }

    public void saveTreeAs(EventObject e)
    {
        JTree jtree = getJTree();

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
            SSFSentenceImpl sen = new SSFSentenceImpl();
            sen.setRoot((SSFPhrase) jtree.getModel().getRoot());

            try
            {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    String treeFile = chooser.getSelectedFile().getAbsolutePath();

                    String charset = JOptionPane.showInputDialog(parentComponent, GlobalProperties.getIntlString("Please_enter_the_charset:"), GlobalProperties.getIntlString("UTF-8"));
                    sen.save(treeFile, charset);
                }
            } catch (FileNotFoundException ex)
            {
                JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        } else if (mode == FS_MODE)
        {
        }

        jtree.requestFocusInWindow();
    }

    public void treeCollapseAll(EventObject e)
    {
        expandAll(e);
    }

    public void treeExpandAll(EventObject e)
    {
        collapseAll(e);
    }

    public void treeNodeClear(EventObject e)
    {
        JTree jtree = getJTree();

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
        } else if (mode == FS_MODE)
        {
            ((FeatureStructuresImpl) rootNodeCopy).setToEmpty();

            getModel().reload();
            expandAll(null);
        }

        jtree.requestFocusInWindow();
    }

    public void treeResetAll(EventObject e)
    {
        JTree jtree = getJTree();

        if (mode == DEFAULT_MODE)
        {
        } else if (mode == SSF_MODE)
        {
        } else if (mode == FS_MODE)
        {
            try
            {
                rootNodeCopy = (FeatureStructuresImpl) ((FeatureStructuresImpl) rootNode).getCopy();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            getModel().setRoot(rootNodeCopy);
            expandAll(null);
        }

        jtree.requestFocusInWindow();
    }

    public void printTree(EventObject e)
    {
        JTree jtree = getJTree();
        PrintUtilities.printComponent(jtree);

        jtree.requestFocusInWindow();
    }

    public void viewTree(EventObject e)
    {
        JTree jtree = getJTree();
        MutableTreeNode rnode = (MutableTreeNode) jtree.getModel().getRoot();

        if (rnode != null)
        {
            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                JDialog realTreeDialog = null;

                if (dialog != null)
                {
                    realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Tree_Viewer"), false);
                } else
                {
                    realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Tree_Viewer"), false);
                }

                SanchayTreeViewerJPanel realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, SanchayMutableTreeNode.CHUNK_MODE, langEnc);
//                SanchayTreeViewerJPanel realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE, langEnc);
                realTreeJPanel.setDialog(realTreeDialog);

                realTreeDialog.add(realTreeJPanel);
                realTreeJPanel.sizeToFit();

                realTreeDialog.setVisible(true);
            } else if (mode == FS_MODE)
            {
            }
        } else
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Unable_to_construct_a_tree_view._Incomplete_or_incorrect_annotation."),
                    GlobalProperties.getIntlString("Error:_Tree_Viewer"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        jtree.requestFocusInWindow();
    }

    public void viewGroupDependencies(EventObject e)
    {
        JTree jtree = getJTree();
        MutableTreeNode rnode = (MutableTreeNode) jtree.getModel().getRoot();

        if (rnode != null)
        {
            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                LinkedHashMap cfgToMMTreeMapping = new LinkedHashMap(0, 10);
                SSFPhrase mmt = ((SSFPhrase) rnode).convertToGDepNode(cfgToMMTreeMapping);

                SanchayTreeViewerJPanel realTreeJPanel = null;

                try {
                    if(mmt == null)
                        realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, ((SSFPhrase) rnode).getCopy(), cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE, langEnc, false);
                    else
                        realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, mmt, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE, langEnc, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                realTreeJPanel.setSanchayTreeJPanel(this);
                realTreeJPanel.initPopupMenu();

//                if (mmt == null)
//                {
//                    JOptionPane.showMessageDialog(parentComponent, "Unable to construct a tree view of a modifier-modified tree.\nIncomplete or incorrect annotation.",
//                            "Error: Tree Viewer", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }

                JDialog realTreeDialog = null;

                if (dialog != null)
                {
                    realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Dependency_Structure_(Click_on_the_Expand_Button_to_Edit_the_Tree)"), false);
                } else
                {
                    realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Dependency_Structure_(Click_on_the_Expand_Button_to_Edit_the_Tree)"), false);
                }

//                SanchayTreeViewerJPanel realTreeJPanel = new SanchayTreeViewerJPanel((SanchayMutableTreeNode) rnode, mmt, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_STRUCTURE_MODE, langEnc, false);
                realTreeJPanel.setDialog(realTreeDialog);

                realTreeDialog.add(realTreeJPanel);
                realTreeDialog.setMinimumSize(new Dimension(400, 450));
//      realTreeDialog.setBounds(80, 30, 900, 700);
                realTreeJPanel.sizeToFit();
                UtilityFunctions.centre(realTreeDialog);

                realTreeDialog.setVisible(true);

                getModel().reload();
                expandAll(null);
            } else if (mode == FS_MODE)
            {
            }
        } else
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Unable_to_construct_a_tree_view._Incomplete_or_incorrect_annotation."),
                    GlobalProperties.getIntlString("Error:_Tree_Viewer"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        jtree.requestFocusInWindow();
    }

    public void viewLeafDependencies(EventObject e)
    {
        JTree jtree = getJTree();
        MutableTreeNode rnode = (MutableTreeNode) jtree.getModel().getRoot();

        if (rnode != null)
        {
            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                LinkedHashMap cfgToMMTreeMapping = new LinkedHashMap(0, 10);
                SSFPhrase mmt = ((SSFPhrase) rnode).convertToLDepNode(cfgToMMTreeMapping);

//                if(mmt.hasLexicalDependencies("drel") == false)
//                    mmt.flattenChunks();

                SanchayTreeViewerJPanel realTreeJPanel = null;

                try {
                    if(mmt == null)
                        realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, ((SSFPhrase) rnode).getCopy(), cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE, langEnc, false, true);
                    else
                        realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, mmt, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE, langEnc, false, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                realTreeJPanel.allowLeafDependencies(null);
                realTreeJPanel.setSanchayTreeJPanel(this);
                realTreeJPanel.initPopupMenu();

//                if (mmt == null)
//                {
//                    JOptionPane.showMessageDialog(parentComponent, "Unable to construct a tree view of a modifier-modified tree.\nIncomplete or incorrect annotation.",
//                            "Error: Tree Viewer", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }

                JDialog realTreeDialog = null;

                if (dialog != null)
                {
                    realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Dependency_Structure_(Click_on_the_Expand_Button_to_Edit_the_Tree)"), false);
                } else
                {
                    realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Dependency_Structure_(Click_on_the_Expand_Button_to_Edit_the_Tree)"), false);
                }

//                SanchayTreeViewerJPanel realTreeJPanel = new SanchayTreeViewerJPanel((SanchayMutableTreeNode) rnode, mmt, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_STRUCTURE_MODE, langEnc, false);
                realTreeJPanel.setDialog(realTreeDialog);

                realTreeDialog.add(realTreeJPanel);
                realTreeDialog.setMinimumSize(new Dimension(400, 450));
//      realTreeDialog.setBounds(80, 30, 900, 700);
                realTreeJPanel.sizeToFit();
                UtilityFunctions.centre(realTreeDialog);

                realTreeDialog.setVisible(true);

                getModel().reload();
                expandAll(null);
            } else if (mode == FS_MODE)
            {
            }
        } else
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Unable_to_construct_a_tree_view._Incomplete_or_incorrect_annotation."),
                    GlobalProperties.getIntlString("Error:_Tree_Viewer"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        jtree.requestFocusInWindow();
    }

    public void viewPS(EventObject e)
    {
        JTree jtree = getJTree();
        MutableTreeNode rnode = (MutableTreeNode) jtree.getModel().getRoot();

        if (rnode != null)
        {
            if (mode == DEFAULT_MODE)
            {
            } else if (mode == SSF_MODE)
            {
                LinkedHashMap cfgToPSTreeMapping = new LinkedHashMap(0, 10);
                SSFPhrase pst = ((SSFPhrase) rnode).convertToPSNode(cfgToPSTreeMapping);

                SanchayTreeViewerJPanel realTreeJPanel = null;

                try {
                    if(pst == null)
                        realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, ((SSFPhrase) rnode).getCopy(), cfgToPSTreeMapping, SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE, langEnc, false);
                    else
                        realTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) rnode, pst, cfgToPSTreeMapping, SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE, langEnc, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                realTreeJPanel.setSanchayTreeJPanel(this);
                realTreeJPanel.initPopupMenu();

//                if (mmt == null)
//                {
//                    JOptionPane.showMessageDialog(parentComponent, "Unable to construct a tree view of a modifier-modified tree.\nIncomplete or incorrect annotation.",
//                            "Error: Tree Viewer", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }

                JDialog realTreeDialog = null;

                if (dialog != null)
                {
                    realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Phrase_Structure"), true);
                } else
                {
                    realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Phrase_Structure"), true);
                }

//                SanchayTreeViewerJPanel realTreeJPanel = new SanchayTreeViewerJPanel((SanchayMutableTreeNode) rnode, mmt, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_STRUCTURE_MODE, langEnc, false);
                realTreeJPanel.setDialog(realTreeDialog);

                realTreeDialog.add(realTreeJPanel);
//      realTreeDialog.setBounds(80, 30, 900, 700);
                realTreeJPanel.sizeToFit();

                realTreeDialog.setVisible(true);

                getModel().reload();
                expandAll(null);
            } else if (mode == FS_MODE)
            {
            }
        } else
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Unable_to_construct_a_tree_view._Incomplete_or_incorrect_annotation."),
                    GlobalProperties.getIntlString("Error:_Tree_Viewer"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        jtree.requestFocusInWindow();
    }

    public void selectInputMethod(EventObject e)
    {
        String im = SanchayLanguages.selectInputMethod(this);

        if (owner != null)
        {
            SanchayLanguages.changeInputMethod(owner, im);
        } else if (dialog != null)
        {
            SanchayLanguages.changeInputMethod(dialog, im);
        }
    }

    public void showKBMap(EventObject e)
    {
        SanchayLanguages.showKBMap(this);
    }

    public void setTagLevels(EventObject e)
    {
        SanchayJDialog tagLevelsDialog = (SanchayJDialog) DialogFactory.showDialog(HierarchicalTagsJPanel.class, owner, "Select tag levels", true, langEnc, "UTF-8");

        HierarchicalTagsJPanel tagLevelsPanel = (HierarchicalTagsJPanel) tagLevelsDialog.getJPanel();
       
    }

    public void showControlTabs(EventObject e)
    {
        if (controlsJTabbedPane.isVisible())
        {
            showControlTabs(false);
            controlTabsShown = false;
        } else
        {
            showControlTabs(true);
            controlTabsShown = true;
        }
    }

    public void showBracketForm(boolean show)
    {
        senJPanel.setVisible(show);
    }

    public void convertToHorizontalSentence()
    {
        JTree jtree = getJTree();

        String annotationDisplayOption = getAnnotationDisplayOption();

        if (annotationDisplayOption.equalsIgnoreCase(GlobalProperties.getIntlString("Raw")))
        {
            senJTextArea.setRows(2);
            senJTextArea.setText(((SSFNode) jtree.getModel().getRoot()).makeRawSentence());
            senJTextArea.setCaretPosition(0);
            setVisible(false);
            setVisible(true);
        } else if (annotationDisplayOption.equalsIgnoreCase(GlobalProperties.getIntlString("Tagged")))
        {
            senJTextArea.setRows(3);
            senJTextArea.setText(((SSFNode) jtree.getModel().getRoot()).convertToPOSTagged());
            senJTextArea.setCaretPosition(0);
            setVisible(false);
            setVisible(true);
        } else if (annotationDisplayOption.equalsIgnoreCase(GlobalProperties.getIntlString("Chunked")))
        {
            senJTextArea.setRows(3);
            senJTextArea.setText(((SSFNode) jtree.getModel().getRoot()).convertToBracketForm(1));
            senJTextArea.setCaretPosition(0);
            setVisible(false);
            setVisible(true);
        }

        jtree.requestFocusInWindow();
    }

    // Helper functions
    public void expandAll(EventObject e)
    {
        treeJTree.setScrollsOnExpand(true);
        for (int i = 0; i < treeJTree.getRowCount(); i++)
        {
            treeJTree.expandRow(i);
        }

        treeJTree.requestFocusInWindow();
    }

    public String getAnnotationDisplayOption()
    {
        return (String) annotationDisplayOptionJComboBox.getSelectedItem();
    }

    public void collapseAll(EventObject e)
    {
        if (treeJTree.getRowCount() > 0)
        {
            treeJTree.collapseRow(0);
        }

        treeJTree.requestFocusInWindow();
    }

    public Hashtable getNodeLabelEditors()
    {
        return nodeLabelEditors;
    }

    public static SanchayTreeJPanel createDefaultTreeJPanel(SanchayMutableTreeNode root, String lang)
    {
        SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
                new DefaultTreeCellRenderer(), null, SanchayTreeJPanel.SSF_MODE, lang);
//		new SSFTreeCellRenderer(lang), null, SanchayTreeJPanel.SSF_MODE, lang);

        treeNodeJPanel.showControlTabs(false);
        treeNodeJPanel.showSentence(false);
        treeNodeJPanel.showNodeEditor(false);

        treeNodeJPanel.mainJSplitPane.setDividerSize(0);

        return treeNodeJPanel;
    }

    public static SanchayTreeJPanel createSSFDisplayJPanel(SanchayMutableTreeNode root, int cols, String[] langs)
    {
        int cmds[] = new int[5];

        cmds[0] = TreeAction.VIEW_TREE;
        cmds[1] = TreeAction.VIEW_GDEPS;
        cmds[2] = TreeAction.VIEW_LDEPS;
        cmds[3] = TreeAction.SAVE_TREE_AS;
        cmds[4] = TreeAction.TREE_PRINT;

        TreeCellRenderer tcr = new SSFTreeCellRendererNew(cols, langs, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);

//	SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
//	new SSFTreeCellRenderer(lang), cmds, SanchayTreeJPanel.SSF_MODE, lang);

        SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root, tcr, cmds, SanchayTreeJPanel.SSF_MODE, cols, langs);

        treeNodeJPanel.showControlTabs(false);
        treeNodeJPanel.showSentence(false);
        treeNodeJPanel.showNodeEditor(false);

        treeNodeJPanel.mainJSplitPane.setDividerSize(0);

        return treeNodeJPanel;
    }

    public static SanchayTreeJPanel createSSFDisplayJPanel(SanchayMutableTreeNode root, String lang)
    {
        int cmds[] = new int[5];

        cmds[0] = TreeAction.VIEW_TREE;
        cmds[1] = TreeAction.VIEW_GDEPS;
        cmds[2] = TreeAction.VIEW_LDEPS;
        cmds[3] = TreeAction.SAVE_TREE_AS;
        cmds[4] = TreeAction.TREE_PRINT;

        TreeCellRenderer tcr = new SSFTreeCellRendererNew(3, new String[]
                {
                    GlobalProperties.getIntlString("eng"), lang, GlobalProperties.getIntlString("eng")
                }, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);

//	SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
//	new SSFTreeCellRenderer(lang), cmds, SanchayTreeJPanel.SSF_MODE, lang);

        SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
                tcr, cmds, SanchayTreeJPanel.SSF_MODE, lang);

        treeNodeJPanel.showControlTabs(false);
        treeNodeJPanel.showSentence(false);
        treeNodeJPanel.showNodeEditor(false);

        treeNodeJPanel.mainJSplitPane.setDividerSize(0);

        return treeNodeJPanel;
    }

    public static int[] getSSFPhraseJPanelCommands()
    {
        int cmds[] = new int[24];

        cmds[0] = TreeAction.ADD_LAYER;
        cmds[1] = TreeAction.DEL_LAYER;
        cmds[2] = TreeAction.SHIFT_LEFT;
        cmds[3] = TreeAction.SHIFT_RIGHT;
        cmds[4] = TreeAction.JOIN_NODES;
        cmds[5] = TreeAction.SPLIT_NODE;
        cmds[6] = TreeAction.EDIT_NODE_TEXT;
        cmds[7] = TreeAction.SELECT_FS;
        cmds[8] = TreeAction.VIEW_TREE;
        cmds[9] = TreeAction.VIEW_GDEPS;
        cmds[10] = TreeAction.VIEW_LDEPS;
        cmds[11] = TreeAction.GLOBAL_NODE_STATISTICS;
//        cmds[8] = TreeAction.VIEW_PS;
        cmds[12] = TreeAction.SAVE_TREE_AS;
        cmds[13] = TreeAction.INSERT_NODE;
        cmds[14] = TreeAction.DEL_SUBTREE;
        cmds[15] = TreeAction.TREE_PRINT;
        cmds[16] = TreeAction.UNDO;
        cmds[17] = TreeAction.REDO;
        cmds[18] = TreeAction.SELECT_INPUT_METHOD;
        cmds[19] = TreeAction.SHOW_KB_MAP;
        cmds[20] = TreeAction.COPY_NODE;
        cmds[21] = TreeAction.PASTE_NODE;
        cmds[22] = TreeAction.SET_TAG_LEVELS;
        cmds[23] = TreeAction.SHOW_CONTROL_TABS;

        return cmds;
    }

    public static SanchayTreeJPanel createSSFCompareJPanel(SanchayMutableTreeNode root,
            PropertyTokens phraseNamesPT, PropertyTokens posTagsPT, String lang)
    {
        int[] cmds = getSSFPhraseJPanelCommands();

        Hashtable labelEditors = new Hashtable(2);
        labelEditors.put(GlobalProperties.getIntlString("PhraseNames"), new DefaultComboBoxModel(phraseNamesPT.getCopyOfTokens()));
        labelEditors.put(GlobalProperties.getIntlString("POSTags"), new DefaultComboBoxModel(posTagsPT.getCopyOfTokens()));

        TreeCellRenderer tcr = new SSFTreeCellRendererNew(3, new String[]
                {
                    GlobalProperties.getIntlString("eng"), lang, GlobalProperties.getIntlString("eng")
                }, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);

//	SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
//	new SSFTreeCellRenderer(lang), labelEditors, cmds, SanchayTreeJPanel.SSF_MODE, lang);

        SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
                tcr, labelEditors, cmds, SanchayTreeJPanel.SSF_MODE, lang);

        treeNodeJPanel.showControlTabs(false);
        treeNodeJPanel.showNodeEditor(false);
        treeNodeJPanel.showSentence(false);

        return treeNodeJPanel;
    }

    public static SanchayTreeJPanel createSSFPhraseJPanel(SanchayMutableTreeNode root,
            PropertyTokens phraseNamesPT, PropertyTokens posTagsPT, String lang)
    {
        int[] cmds = getSSFPhraseJPanelCommands();

        Hashtable labelEditors = new Hashtable(2);
        labelEditors.put(GlobalProperties.getIntlString("PhraseNames"), new DefaultComboBoxModel(phraseNamesPT.getCopyOfTokens()));
        labelEditors.put(GlobalProperties.getIntlString("POSTags"), new DefaultComboBoxModel(posTagsPT.getCopyOfTokens()));

        TreeCellRenderer tcr = new SSFTreeCellRendererNew(3, new String[]
                {
//                    GlobalProperties.getIntlString("eng"), lang, GlobalProperties.getIntlString("eng")
                    GlobalProperties.getIntlString("eng"), lang, lang
                }, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);

//	SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
//	new SSFTreeCellRenderer(lang), labelEditors, cmds, SanchayTreeJPanel.SSF_MODE, lang);

        SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(root,
                tcr, labelEditors, cmds, SanchayTreeJPanel.SSF_MODE, lang);

        return treeNodeJPanel;
    }

    public static SanchayTreeJPanel createSSFFeatureStructureJPanel(SanchayMutableTreeNode fss, String lang)
    {
        int cmds[] = new int[8];
        cmds[0] = TreeAction.ADD_CHILD;
        cmds[1] = TreeAction.DEL_SUBTREE;
        cmds[2] = TreeAction.EDIT_LABEL;
        cmds[3] = TreeAction.EXPAND_ALL;
        cmds[4] = TreeAction.COLLAPSE_ALL;
        cmds[5] = TreeAction.TREE_PRINT;
        cmds[6] = TreeAction.UNDO;
        cmds[7] = TreeAction.REDO;

        Hashtable labelEditors = new Hashtable(3);
        labelEditors.put(GlobalProperties.getIntlString("AttributeNames"), new DefaultComboBoxModel());
        labelEditors.put(GlobalProperties.getIntlString("FSNames"), new DefaultComboBoxModel());
        labelEditors.put(GlobalProperties.getIntlString("AttributeValues"), new DefaultComboBoxModel());

        SanchayTreeJPanel treeNodeJPanel = new SanchayTreeJPanel(fss,
                new FSTreeCellRenderer(lang), labelEditors, cmds, SanchayTreeJPanel.FS_MODE, lang);

        return treeNodeJPanel;
    }

    public static SanchayTreeJPanel createCategoryJPanel()
    {
        return null;
    }

    public static SanchayTreeJPanel createCategoryFSJPanel()
    {
        return null;
    }

    public static SanchayTreeJPanel createDSFJPanel()
    {
        return null;
    }

    public static SanchayTreeJPanel createXMLJPanel()
    {
        return null;
    }

    // Undo support
    public void addUndoableEditListener(UndoableEditListener l)
    {
        support.addUndoableEditListener(l);
    }

    public void removeUndoableEditListener(UndoableEditListener l)
    {
        support.removeUndoableEditListener(l);
    }

    public void collapsePath(TreePath path)
    {
        boolean wasExpanded = treeJTree.isExpanded(path);

        treeJTree.collapsePath(path);

        boolean isExpanded = treeJTree.isExpanded(path);
        if (isExpanded != wasExpanded)
        {
            support.postEdit(new CollapseEdit(path));
        }
    }

    public void expandPath(TreePath path)
    {
        boolean wasExpanded = treeJTree.isExpanded(path);

        treeJTree.expandPath(path);

        boolean isExpanded = treeJTree.isExpanded(path);
        if (isExpanded != wasExpanded)
        {
            support.postEdit(new ExpandEdit(path));
        }
    }

    public void undoExpansion(TreePath path)
    {
        treeJTree.collapsePath(path);
    }

    public void undoCollapse(TreePath path)
    {
        treeJTree.expandPath(path);
    }

    public String getCurrentTreeEditTab()
    {
        int tabIndex = controlsJTabbedPane.getSelectedIndex();

        if(tabIndex == -1)
            return "Tag";

        return controlsJTabbedPane.getTitleAt(tabIndex);
    }

    public void setCurrentTreeEditTab(String title)
    {
        int tabIndex = controlsJTabbedPane.indexOfTab(title);

        if(tabIndex != -1)
            controlsJTabbedPane.setSelectedIndex(tabIndex);
    }

    private class CollapseEdit extends AbstractUndoableEdit
    {

        public CollapseEdit(TreePath path)
        {
            this.path = path;
        }

        public void undo() throws CannotUndoException
        {
            super.undo();
            SanchayTreeJPanel.this.undoCollapse(path);
        }

        public void redo() throws CannotRedoException
        {
            super.redo();
            SanchayTreeJPanel.this.undoExpansion(path);
        }

        public String getPresentationName()
        {
            return GlobalProperties.getIntlString("node_collapse");
        }
        private TreePath path;
    }

    private class ExpandEdit extends AbstractUndoableEdit
    {

        public ExpandEdit(TreePath path)
        {
            this.path = path;
        }

        public void undo() throws CannotUndoException
        {
            super.undo();
            SanchayTreeJPanel.this.undoExpansion(path);
        }

        public void redo() throws CannotRedoException
        {
            super.redo();
            SanchayTreeJPanel.this.undoCollapse(path);
        }

        public String getPresentationName()
        {
            return GlobalProperties.getIntlString("node_expansion");
        }
        private TreePath path;
    }

    public void setPropbankMode(boolean m)
    {
        propbankMode = m;

        popupListener.setPropbankMode(m);
    }

    public String getTitle()
    {
        return title;
    }

    public String getLangEnc()
    {
        return langEnc;
    }

    public JPopupMenu getJPopupMenu()
    {
        return treeJPopupMenu;
    }

    public JMenuBar getJMenuBar()
    {
        return null;
    }

    public JToolBar getJToolBar()
    {
        return null;
    }
    
    public boolean getAlignmentMode()
    {
        return alignmentMode;
    }
    
    public void setAlignmentMode(boolean am)
    {
        alignmentMode = am;

        treeJTree.setDragEnabled(am);
        treeJTree.setDropMode(DropMode.ON_OR_INSERT); 
        treeJTree.getSelectionModel().setSelectionMode(  
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);  
        treeJTree.setTransferHandler(new SyntacticAlignmentTransferHandler(this));
    }
    
    public TransferHandler getTransferHandler()
    {
        return treeJTree.getTransferHandler();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton addFSJButton;
    public javax.swing.JComboBox annotationDisplayOptionJComboBox;
    public javax.swing.JPanel annotationDisplayOptionJPanel;
    public javax.swing.JButton applyFSJButton;
    public javax.swing.JPanel bottomJPanel;
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JScrollPane commandsJScrollPane;
    public javax.swing.JTabbedPane controlsJTabbedPane;
    public javax.swing.JPanel featuresTabJPanel;
    public javax.swing.JPanel fsEditCommandsJPanel;
    public javax.swing.JPanel fsEditJPanel;
    public javax.swing.JComboBox fsJComboBox;
    public javax.swing.JPanel fsSelectionPanel;
    public javax.swing.JSplitPane mainJSplitPane;
    public javax.swing.JPanel nodeEditJPanel;
    public javax.swing.JPanel nodeLabelEditJPanel;
    public javax.swing.JComboBox nodeLabelJComboBox;
    public javax.swing.JLabel nodeLabelJLabel;
    public javax.swing.JPanel nodeTextEditJPanel;
    public javax.swing.JScrollPane nodeTextJScrollPane;
    public javax.swing.JTextArea nodeTextJTextArea;
    public javax.swing.JLabel nodeTextLabel;
    public javax.swing.JButton removeFSJButton;
    public javax.swing.JLabel senJLabel;
    public javax.swing.JPanel senJPanel;
    public javax.swing.JScrollPane senJScrollPane;
    public javax.swing.JTextArea senJTextArea;
    public javax.swing.JPanel tagsTabJPanel;
    public javax.swing.JPanel textLabelEditJPanel;
    public javax.swing.JPanel topJPanel;
    public javax.swing.JPanel treeEditJPanel;
    public javax.swing.JScrollPane treeJScrollJPane;
    public javax.swing.JSplitPane treeJSplitPane;
    public javax.swing.JTree treeJTree;
    // End of variables declaration//GEN-END:variables
}
