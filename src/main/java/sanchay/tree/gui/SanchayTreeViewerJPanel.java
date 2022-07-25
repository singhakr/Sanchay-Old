/*
 * SanchayTreeViewerJPanel.java
 *
 * Created on February 4, 2006, 3:52 PM
 */
package sanchay.tree.gui;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultTreeModel;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.impl.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.gui.common.SanchayLanguages;
import sanchay.gui.scroll.Rule;
import sanchay.table.*;
import sanchay.table.gui.*;
import sanchay.tree.*;
import sanchay.util.FileFilterImpl;
import sanchay.util.ImageUtil;
import sanchay.util.PrintUtilities;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author  anil
 */
public class SanchayTreeViewerJPanel extends javax.swing.JPanel implements ItemListener, TreeViewerEventListener {

//    private Rule columnView;
    protected Rule rowView;
//    private JToggleButton isMetric;
    protected SanchayJTable tableJTable;
    protected SanchayMutableTreeNode chunkRootNode;
    protected SanchayMutableTreeNode rootNode;
    protected Vector phrases;
    protected Vector cellCounts;
    protected SanchayTreeJPanel sanchayTreeJPanel;
    protected String language;
    protected JFrame owner;
    protected JDialog dialog;
    protected int mode;
    protected boolean reverse;
    protected boolean leafDependencies = false;
    protected boolean rtl = false;
    protected boolean up = false;
    protected boolean collapsed = false;
    protected LinkedHashMap cfgToDepTreeMapping;
    protected TreeViewNodeEditPopupListener popupListener;
    protected SanchayTableModel fsSchema;
    // Command buttions
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JButton leafDepsJButton;
    public javax.swing.JButton printJButton;
    public javax.swing.JButton saveJButton;
    public javax.swing.JButton saveAsJButton;
    public javax.swing.JButton textDirJButton;
    public javax.swing.JButton treeOrientationJButton;
    public javax.swing.JButton zoomInJButton;
    public javax.swing.JButton zoomOutJButton;
    public javax.swing.JButton expandJButton;
    public javax.swing.JButton collapseJButton;

    public static final int REFRESH_TREE_ACTION = 0;

    public static final int LTR_HORIZONTAL = 0;
    public static final int LTR_VERTICAL = 1;
    
    public static final int _HORIZONTAL = 2;
    public static final int RTL_VERTICAL = 3;

    /** Creates new form SanchayTreeViewerJPanel */
    public SanchayTreeViewerJPanel(SanchayMutableTreeNode chunkRoot, SanchayMutableTreeNode mmRoot, LinkedHashMap cfgToDepTreeMapping, int mode, String lang, boolean rev, boolean leafDeps) {
        this(chunkRoot, mmRoot, cfgToDepTreeMapping, mode, lang, rev);

        leafDependencies = leafDeps;
    }

    public SanchayTreeViewerJPanel(SanchayMutableTreeNode chunkRoot, SanchayMutableTreeNode mmRoot, LinkedHashMap cfgToDepTreeMapping, int mode, String lang, boolean rev) {
        this(mmRoot, mode, lang, rev);
        chunkRootNode = chunkRoot;

        this.cfgToDepTreeMapping = cfgToDepTreeMapping;

        tableJTable.setCFG2DepTreeMap(cfgToDepTreeMapping);
    }

    public SanchayTreeViewerJPanel(SanchayMutableTreeNode root, int mode, String lang, boolean rev) {
        super();

        collapsed = true;

        initComponents();

//        setLayout(new BorderLayout(this, BoxLayout.PAGE_AXIS));

        //Create the row and column headers.
//        columnView = new Rule(Rule.HORIZONTAL, true);
        rowView = new Rule(Rule.VERTICAL, true);

//        columnView.setPreferredWidth(320);
//        rowView.setPreferredHeight(480);

        //Create the corners.
//        JPanel buttonCorner = new JPanel(); //use FlowLayout
//        isMetric = new JToggleButton("cm", true);
//        isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
//        isMetric.setMargin(new Insets(2,2,2,2));
//        isMetric.addItemListener(this);
//        buttonCorner.add(isMetric); 

        //Set up the scroll pane.
        tableJTable = new SanchayJTable(new SanchayTableModel(), SanchayJTable.TREE_MODE, root, true);
        tableJTable.addEventListener(this);
        tableJTable.prepareCommands();
        JScrollPane tableScrollPane = new JScrollPane(tableJTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 450));
//        tableScrollPane.setMinimumSize(new Dimension(300, 350));
//        tableJTable.setPreferredScrollableViewportSize(new Dimension(300, 350));
        tableScrollPane.setViewportBorder(
                BorderFactory.createLineBorder(Color.black));

//        tableScrollPane.setColumnHeaderView(columnView);
//        tableScrollPane.setRowHeaderView(rowView);

        //Set the corners.
        //In theory, to support internationalization you would change
        //UPPER_LEFT_CORNER to UPPER_LEADING_CORNER,
        //LOWER_LEFT_CORNER to LOWER_LEADING_CORNER, and
        //UPPER_RIGHT_CORNER to UPPER_TRAILING_CORNER.  In practice,
        //bug #4467063 makes that impossible (in 1.4, at least).
//        tableScrollPane.setCorner(JScrollPane.UPPER_LEADING_CORNER,
//                                    buttonCorner);
//        tableScrollPane.setCorner(JScrollPane.LOWER_LEADING_CORNER,
//                                    new Corner());
//        tableScrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER,
//                                    new Corner());

        //Put it in this panel.
        add(tableScrollPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        reverse = rev;

        language = lang;

        rootNode = root;
        phrases = new Vector(0, 3);
        cellCounts = new Vector(0, 3);

        this.mode = mode;

//        tableJScrollPane.setViewportView(tableJTable);

//        Graphics g = tableJScrollPane.getGraphics();

        UtilityFunctions.setComponentFont(tableJTable, language);
        rtl = SanchayLanguages.isRTL(lang);
//        tableJTable.setFont(new java.awt.Font("Dialog", 1, 14));

        tableJTable.setRowHeight(120);
        tableJTable.getColumnModel().setColumnMargin(10);
        tableJTable.setShowHorizontalLines(false);
        tableJTable.setShowVerticalLines(false);
//        tableJTable.setIntercellSpacing(new java.awt.Dimension(5, 50));

//        tableJTable.setCellSelectionEnabled(true);
        tableJTable.firePropertyChange("cellSelectionEnabled", false, true);
        tableJTable.setRowSelectionAllowed(true);
        tableJTable.firePropertyChange("rowSelectionAllowed", false, true);
        tableJTable.setColumnSelectionAllowed(true);
        tableJTable.firePropertyChange("columnSelectionAllowed", false, true);

        JTextField ed = new JTextField();
        ed.setEditable(true);
        ed.setHorizontalAlignment(JTextField.CENTER);
        DefaultCellEditor dced = new DefaultCellEditor(ed);
        tableJTable.setDefaultEditor(String.class, dced);

        int lcount = rootNode.getAllLeaves().size();
        SanchayTableModel sanchayTableModel = new SanchayTableModel(0, lcount);
        tableJTable.setModel(sanchayTableModel);

        positionTree(rootNode, 0, lcount / 2);

        if (reverse) {
            rootNode.setValuesInTable(sanchayTableModel, mode, true);
        } else {
            rootNode.setValuesInTable(sanchayTableModel, mode);
        }

        tableJTable.clearEdges();

        UtilityFunctions.fitColumnsToContent(tableJTable);
        tableJTable.doLayout();

        rootNode.fillTreeEdges(tableJTable, mode);
        rootNode.fillGraphEdges(tableJTable, mode);

        TreeViewerTableCellRenderer renderer = new TreeViewerTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        if (!(mode == SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || mode == SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE)) {
            renderer.setToolTipText(GlobalProperties.getIntlString("Click_for_node_data"));
        }

        if (sanchayTableModel.getColumnCount() > 0) {
            tableJTable.setDefaultRenderer(sanchayTableModel.getColumnClass(0), renderer);
        }

        tableJTable.setTableHeader(null);
        tableJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        refreshTree();

        init();
    }

    public SanchayTreeViewerJPanel(SanchayMutableTreeNode root, int mode, String lang) {
        super();

        initComponents();

//        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        //Create the row and column headers.
//        columnView = new Rule(Rule.HORIZONTAL, true);
        rowView = new Rule(Rule.VERTICAL, true);
//
//        columnView.setPreferredWidth(320);
//        rowView.setPreferredHeight(480);

        //Create the corners.
//        JPanel buttonCorner = new JPanel(); //use FlowLayout
//        isMetric = new JToggleButton("cm", true);
//        isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
//        isMetric.setMargin(new Insets(2,2,2,2));
//        isMetric.addItemListener(this);
//        buttonCorner.add(isMetric); 

        //Set up the scroll pane.
//        tableJTable = new SanchayJTable(new SanchayTableModel(), columnView.getIncrement());
        tableJTable = new SanchayJTable(new SanchayTableModel(), SanchayJTable.TREE_MODE, true);
        tableJTable.prepareCommands();
        JScrollPane tableScrollPane = new JScrollPane(tableJTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 450));
//        tableScrollPane.setMinimumSize(new Dimension(300, 350));
//        tableJTable.setPreferredScrollableViewportSize(new Dimension(300, 350));
        tableScrollPane.setViewportBorder(
                BorderFactory.createLineBorder(Color.black));

//        tableScrollPane.setColumnHeaderView(columnView);
//        tableScrollPane.setRowHeaderView(rowView);

        //Set the corners.
        //In theory, to support internationalization you would change
        //UPPER_LEFT_CORNER to UPPER_LEADING_CORNER,
        //LOWER_LEFT_CORNER to LOWER_LEADING_CORNER, and
        //UPPER_RIGHT_CORNER to UPPER_TRAILING_CORNER.  In practice,
        //bug #4467063 makes that impossible (in 1.4, at least).
//        tableScrollPane.setCorner(JScrollPane.UPPER_LEADING_CORNER,
//                                    buttonCorner);
//        tableScrollPane.setCorner(JScrollPane.LOWER_LEADING_CORNER,
//                                    new Corner());
//        tableScrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER,
//                                    new Corner());

        //Put it in this panel.
        add(tableScrollPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        language = lang;

        rootNode = root;
        phrases = new Vector(0, 3);
        cellCounts = new Vector(0, 3);

        this.mode = mode;

//        Graphics g = tableJScrollPane.getGraphics();

        UtilityFunctions.setComponentFont(tableJTable, language);
        rtl = SanchayLanguages.isRTL(lang);
//        tableJTable.setFont(new java.awt.Font("Dialog", 1, 14));

        tableJTable.setRowHeight(120);
        tableJTable.getColumnModel().setColumnMargin(10);
        tableJTable.setShowHorizontalLines(false);
        tableJTable.setShowVerticalLines(false);
//        tableJTable.setIntercellSpacing(new java.awt.Dimension(5, 50));

//        tableJTable.setCellSelectionEnabled(true);
        tableJTable.firePropertyChange("cellSelectionEnabled", false, true);
        tableJTable.setRowSelectionAllowed(true);
        tableJTable.firePropertyChange("rowSelectionAllowed", false, true);
        tableJTable.setColumnSelectionAllowed(true);
        tableJTable.firePropertyChange("columnSelectionAllowed", false, true);

        JTextField ed = new JTextField();
        ed.setEditable(false);
        ed.setHorizontalAlignment(JTextField.CENTER);
        DefaultCellEditor dced = new DefaultCellEditor(ed);
        tableJTable.setDefaultEditor(String.class, dced);

        int lcount = rootNode.getAllLeaves().size();
        SanchayTableModel sanchayTableModel = new SanchayTableModel(0, lcount);
        tableJTable.setModel(sanchayTableModel);

        positionTree(rootNode, 0, lcount / 2);

        rootNode.setValuesInTable(sanchayTableModel, mode);

        tableJTable.clearEdges();

        UtilityFunctions.fitColumnsToContent(tableJTable);
        tableJTable.doLayout();

        rootNode.fillTreeEdges(tableJTable, mode);
        rootNode.fillGraphEdges(tableJTable, mode);

        TreeViewerTableCellRenderer renderer = new TreeViewerTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        if (!(mode == SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || mode == SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE)) {
            renderer.setToolTipText(GlobalProperties.getIntlString("Click_for_node_data"));
        }

        if (sanchayTableModel.getColumnCount() > 0) {
            tableJTable.setDefaultRenderer(sanchayTableModel.getColumnClass(0), renderer);
        }

        tableJTable.setTableHeader(null);
        tableJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        refreshTree();

        init();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        treeViewerJPopupMenu = new javax.swing.JPopupMenu();

        setLayout(new java.awt.BorderLayout(4, 4));
    }// </editor-fold>//GEN-END:initComponents

    public void setSanchayTreeJPanel(SanchayTreeJPanel sanchayTreeJPanel) {
        this.sanchayTreeJPanel = sanchayTreeJPanel;
    }

    public SanchayTableModel getFSSchema() {
        if (fsSchema == null && sanchayTreeJPanel != null) {
            fsSchema = sanchayTreeJPanel.getFSSchema();
        }

        return fsSchema;
    }

    public void initPopupMenu() {
        if (sanchayTreeJPanel != null) {
            popupListener = new TreeViewNodeEditPopupListener(tableJTable, treeViewerJPopupMenu, sanchayTreeJPanel.getNodeLabelEditors(), getFSSchema());
            tableJTable.addMouseListener(popupListener);
        }
    }

    private void zoomOutJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        if (tableJTable.getRowHeight() > 2) {
            tableJTable.setRowHeight(tableJTable.getRowHeight() - 2);
        }

        UtilityFunctions.decreaseFontSize(tableJTable);
        UtilityFunctions.fitColumnsToContent(tableJTable);
        tableJTable.doLayout();
//        sizeToFit();
    }

    private void zoomInJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        tableJTable.setRowHeight(tableJTable.getRowHeight() + 2);
        tableJTable.setSize(tableJTable.getWidth() + 4, tableJTable.getHeight());

        UtilityFunctions.increaseFontSize(tableJTable);
        UtilityFunctions.fitColumnsToContent(tableJTable);
        tableJTable.doLayout();

//        sizeToFit();
    }

    private void leafDepsJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        allowLeafDependencies(evt);
    }

    private void printJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        PrintUtilities.printComponent(tableJTable);
    }

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (mode == SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE) {
            SSFPhrase ssfChunkRoot = (SSFPhrase) chunkRootNode;
            SSFPhrase ssfMMRoot = (SSFPhrase) rootNode;
//            ssfChunkRoot.copyAttributesMM2Chunk((SSFPhrase) ssfMMRoot.getChild(0), cfgToMMTreeMapping);
//            ssfChunkRoot.copyDataMM2Chunk((SSFPhrase) ssfMMRoot.getChild(0), cfgToMMTreeMapping);
            ssfChunkRoot.copyDataDep2Chunk((SSFPhrase) ssfMMRoot, cfgToDepTreeMapping, leafDependencies);

            if (sanchayTreeJPanel != null) {
                ((DefaultTreeModel) sanchayTreeJPanel.getJTree().getModel()).reload();
//                sanchayTreeJPanel.getJTree().expandRow(1);
                sanchayTreeJPanel.collapseAll(null);
                sanchayTreeJPanel.expandAll(null);
            }

            collapsed = true;
            //      ssfChunkRoot.copyAttributesMM2Chunk((SSFPhrase) ssfMMRoot.getChild(0));
        }
    }

    private void saveAsJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String saveAs = (String) JOptionPane.showInputDialog(this,
                "Select the type of file you want to save as...", "File Type", JOptionPane.INFORMATION_MESSAGE, null,
                new String[] {"Image", "EPS", "Text"}, "EPS");

        if(saveAs.equals("Image"))
            saveAsImageJButtonActionPerformed(evt);
        else if(saveAs.equals("EPS"))
            saveAsEPSJButtonActionPerformed(evt);
        else if(saveAs.equals("Text"))
            saveAsTextJButtonActionPerformed(evt);
    }

    private void saveAsImageJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        try {             
            JFileChooser chooser = new JFileChooser();

            javax.swing.filechooser.FileFilter filter = new FileFilterImpl(ImageUtil.getImageReaderList(ImageUtil.IMAGE_READERS));
            chooser.addChoosableFileFilter(filter);

            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String imgFile = chooser.getSelectedFile().getAbsolutePath();

                BufferedImage image = new BufferedImage(tableJTable.getWidth(), tableJTable.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                tableJTable.paintAll(g2d);

                if (imgFile == null) {
                    return;
                }

                int offset = imgFile.lastIndexOf(".");
                String type = offset == -1 ? "jpg" : imgFile.substring(offset + 1);

                File file = new File(imgFile);

                ImageIO.write(image, UtilityFunctions.getExtension(file), file);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveAsEPSJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();

        javax.swing.filechooser.FileFilter filter = new FileFilterImpl("eps", GlobalProperties.getIntlString("Encapsulated_PostScript"));
        chooser.addChoosableFileFilter(filter);

        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String imgFile = chooser.getSelectedFile().getAbsolutePath();

            FileOutputStream finalImage;

            try {
                finalImage = new FileOutputStream(imgFile);

//                BufferedImage image = new BufferedImage(tableJTable.getWidth(), tableJTable.getHeight(), BufferedImage.TYPE_INT_RGB);
                EpsGraphics2D g = new EpsGraphics2D(GlobalProperties.getIntlString("Title"), finalImage, 0, 0, tableJTable.getWidth(), tableJTable.getHeight());
                tableJTable.paint(g);
                g.flush();
                g.close();
                finalImage.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SanchayTreeViewerJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SanchayTreeViewerJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveAsTextJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        SSFSentenceImpl sen = new SSFSentenceImpl();
        sen.setRoot((SSFPhrase) rootNode);

        try {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String treeFile = chooser.getSelectedFile().getAbsolutePath();

                String charset = JOptionPane.showInputDialog(this, GlobalProperties.getIntlString("Please_enter_the_charset:"), GlobalProperties.getIntlString("UTF-8"));
                sen.save(treeFile, charset);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setTextDirectionJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setTextDirection(evt, true);
    }

    private void setTreeOrientationJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setTreeOrientation(evt, true);
    }

    private void expandJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (collapsed == false || mode != SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || cfgToDepTreeMapping == null) {
            return;
        }

        ((SSFPhrase) rootNode).expandMMTree(cfgToDepTreeMapping);

        refreshTree();

        collapsed = false;
    }

    private void collapseJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (collapsed || mode != SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || cfgToDepTreeMapping == null) {
            return;
        }

        ((SSFPhrase) rootNode).collapseLexicalItemsDeep();

        refreshTree();

        collapsed = true;
    }

    private void init() {
        commandsJPanel = new javax.swing.JPanel();
        zoomInJButton = new javax.swing.JButton();
        zoomOutJButton = new javax.swing.JButton();
        saveJButton = new javax.swing.JButton();
        saveAsJButton = new javax.swing.JButton();
        textDirJButton = new javax.swing.JButton();
        treeOrientationJButton = new javax.swing.JButton();
        printJButton = new javax.swing.JButton();
        leafDepsJButton = new javax.swing.JButton();
        expandJButton = new javax.swing.JButton();
        collapseJButton = new javax.swing.JButton();

        zoomInJButton.setText("+");
        zoomInJButton.setToolTipText(GlobalProperties.getIntlString("Zoom_In"));
        zoomInJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(zoomInJButton);

        zoomOutJButton.setText("-");
        zoomOutJButton.setToolTipText(GlobalProperties.getIntlString("Zoom_Out"));
        zoomOutJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(zoomOutJButton);

        leafDepsJButton.setText("LDeps");
        leafDepsJButton.setToolTipText("Allow leaf level (lexical) dependencies");
        leafDepsJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leafDepsJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(leafDepsJButton);

        leafDepsJButton.setVisible(false);

        expandJButton.setText(GlobalProperties.getIntlString("Expand"));
        expandJButton.setToolTipText(GlobalProperties.getIntlString("Expand_the_lexical_items"));
        expandJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(expandJButton);

        collapseJButton.setText(GlobalProperties.getIntlString("Collapse"));
        collapseJButton.setToolTipText(GlobalProperties.getIntlString("Collapse_the_lexical_items"));
        collapseJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collapseJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(collapseJButton);

        saveJButton.setText(GlobalProperties.getIntlString("Save"));
        saveJButton.setToolTipText(GlobalProperties.getIntlString("Save_Annotated_Tree"));
        saveJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(saveJButton);

        saveAsJButton.setText("Save As");
        saveAsJButton.setToolTipText("Save the tree in a particular format");
        saveAsJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(saveAsJButton);

        if(rtl)
        {
            textDirJButton.setText("LTR");
            textDirJButton.setToolTipText("Set Left to Right text direction");
        }
        else
        {
            textDirJButton.setText("RTL");
            textDirJButton.setToolTipText("Set Right to Left text direction");
        }
        
        textDirJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTextDirectionJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(textDirJButton);

        if(up)
        {
            treeOrientationJButton.setText("Down");
        }
        else
        {
            treeOrientationJButton.setText("Up");
        }
        
        treeOrientationJButton.setToolTipText("Set the tree orientation (up or down)");
        treeOrientationJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTreeOrientationJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(treeOrientationJButton);

        treeOrientationJButton.setVisible(false);

        printJButton.setText(GlobalProperties.getIntlString("Print"));
        printJButton.setToolTipText(GlobalProperties.getIntlString("Print"));
        printJButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(printJButton);

        add(commandsJPanel, BorderLayout.SOUTH);
    }

    public void refreshTree() {
        JTextField ed = new JTextField();
        ed.setEditable(true);
        ed.setHorizontalAlignment(JTextField.CENTER);
        DefaultCellEditor dced = new DefaultCellEditor(ed);
        tableJTable.setDefaultEditor(String.class, dced);
        tableJTable.clearCellObjects();

        int lcount = rootNode.getAllLeaves().size();
        SanchayTableModel sanchayTableModel = new SanchayTableModel(0, lcount);
        tableJTable.setModel(sanchayTableModel);

        positionTree(rootNode, 0, lcount / 2);

        if (reverse) {
            rootNode.setValuesInTable(sanchayTableModel, mode, true);
        } else {
            rootNode.setValuesInTable(sanchayTableModel, mode);
        }

        tableJTable.clearEdges();

        UtilityFunctions.fitColumnsToContent(tableJTable);

        rootNode.fillTreeEdges(tableJTable, mode);
        rootNode.fillGraphEdges(tableJTable, mode);

        TreeViewerTableCellRenderer renderer = new TreeViewerTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        if (!(mode == SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || mode == SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE)) {
            renderer.setToolTipText(GlobalProperties.getIntlString("Click_for_node_data"));
        }

        if (sanchayTableModel.getColumnCount() > 0) {
            tableJTable.setDefaultRenderer(sanchayTableModel.getColumnClass(0), renderer);
        }

        tableJTable.setTableHeader(null);
        tableJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    }

    public void setColumnClasses() {
        if (mode == SanchayMutableTreeNode.DICT_FST_MODE) {
            int ccount = tableJTable.getModel().getColumnCount();

            for (int i = 0; i < ccount; i++) {
                if (owner != null) {
                    SanchayActionTableCellEditor editor = new SanchayActionTableCellEditor(owner, language, GlobalProperties.getIntlString("Affix_Editor"), SanchayActionTableCellEditor.DICTIONARY_FST_MODE);
                    tableJTable.getColumnModel().getColumn(i).setCellEditor(editor);
                } else if (dialog != null) {
                    SanchayActionTableCellEditor editor = new SanchayActionTableCellEditor(dialog, language, GlobalProperties.getIntlString("Affix_Editor"), SanchayActionTableCellEditor.DICTIONARY_FST_MODE);
                    tableJTable.getColumnModel().getColumn(i).setCellEditor(editor);
                }
            }
        }
    }

    private void positionTree(SanchayMutableTreeNode node, int level, int pos) {
        SanchayTableModel sanchayTableModel = (SanchayTableModel) tableJTable.getModel();

        if (mode == SSFPhrase.DEPENDENCY_RELATIONS_MODE) {
            // Skipping the root SSF node
            if (sanchayTableModel.getRowCount() == level - 1) {
                sanchayTableModel.addRow();
            }
        } else {
            if (sanchayTableModel.getRowCount() == level) {
                sanchayTableModel.addRow();
            }
        }

        if (node.isLeaf() == false) {
            int ccount = node.getAllLeaves().size();

            int chcount = node.getChildCount();

            int beg = pos - ccount / 2;
            int chpos = 0;

            for (int i = 0; i < chcount; i++) {
                SanchayMutableTreeNode chnode = (SanchayMutableTreeNode) node.getChildAt(i);

                if (chnode.isLeaf() == false) {
                    int chccount = chnode.getAllLeaves().size();

                    chpos = beg + chccount / 2;
                    beg += chccount;
                } else {
                    chpos = beg;
                    beg++;
                }

                positionTree(chnode, level + 1, chpos);
            }
        }

        if (mode == SSFPhrase.DEPENDENCY_RELATIONS_MODE) {
            node.setRowIndex(level - 1);
        } else {
            node.setRowIndex(level);
        }

        if(rtl == false)
            node.setColumnIndex(pos);
        else
        {
            int colCount = sanchayTableModel.getColumnCount();
            node.setColumnIndex(colCount - pos - 1);
        }
    }

    public SanchayMutableTreeNode getRoot() {
        return rootNode;
    }

    public JFrame getOwner() {
        return owner;
    }

    public void setOwner(JFrame f) {
        owner = f;
    }

    public void setDialog(JDialog d) {
        dialog = d;
    }

    public TableCellEditor getDefaultNodeEditor(Class cls) {
        return tableJTable.getDefaultEditor(cls);
    }

    public void setDefaultNodeEditor(Class cls, TableCellEditor edtr) {
        tableJTable.setDefaultEditor(cls, edtr);
    }

    public void sizeToFit() {
        Dimension size = tableJTable.getPreferredSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Rectangle rect = new Rectangle(size);

        int h1 = 50;
        int w1 = 10;

        if (size.height < screenSize.height - h1) {
            int h = rect.height;

            rect.y = (screenSize.height - h - h1) / 2;
            rect.height = h + h1;
        } else {
            rect.y = h1 / 2;
            rect.height = size.height - h1;
        }

        if (size.width < screenSize.width - w1) {
            int w = rect.width;

            rect.x = (screenSize.width - w - w1) / 2;
            rect.width = w + w1;
        } else {
            rect.x = w1 / 2;
            rect.width = size.width - w1;
        }

        dialog.setBounds(rect);
    }
//
//    public tableJTable getSanchayJTable()
//    {
//        return tableJTable;
//    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            //Turn it to metric.
//            rowView.setIsMetric(true);
//            columnView.setIsMetric(true);
        } else {
            //Turn it to inches.
//            rowView.setIsMetric(false);
//            columnView.setIsMetric(false);
        }
        tableJTable.setMaxUnitIncrement(rowView.getIncrement());
    }

    public void treeChanged(TreeViewerEvent evt) {
        refreshTree();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPopupMenu treeViewerJPopupMenu;
    // End of variables declaration//GEN-END:variables

    public String getCellPosition(Object obj) {
        return tableJTable.getCellPosition(obj);
    }

    public Object getValueAt(int row, int column) {
        return tableJTable.getValueAt(row, column);
    }

    public void changeSelection(int row, int column, boolean toggle, boolean extend) {
        tableJTable.changeSelection(row, column, toggle, extend);
    }

    public SanchayEdges getEdges() {
        return tableJTable.getEdges();
    }

    public void allowLeafDependencies(ActionEvent evt)
    {
        if (leafDepsJButton.getText().equals("LDeps"))
        {
            leafDepsJButton.setText("GDeps");
            leafDepsJButton.setToolTipText("Don't allow leaf level (lexical) dependencies, only group (chunk level) allowed");

//            expandJButtonActionPerformed(evt);

            expandJButton.setVisible(false);
            collapseJButton.setVisible(false);

            tableJTable.allowsLeafDependencies(true);

            leafDependencies = true;
        }
        else
        {
            leafDepsJButton.setText("LDeps");
            leafDepsJButton.setToolTipText("Allow leaf level (lexical) dependencies");

//            collapseJButtonActionPerformed(evt);

            expandJButton.setVisible(true);
            collapseJButton.setVisible(true);

            tableJTable.allowsLeafDependencies(false);

            leafDependencies = false;
        }
    }

    public boolean allowsLeafDependencies()
    {
        return leafDependencies;
    }

    public boolean isRTL()
    {
        return rtl;
    }

    private void setTextDirection(ActionEvent evt, boolean refresh)
    {
        if (textDirJButton.getText().equals("RTL"))
        {
            textDirJButton.setText("LTR");
            textDirJButton.setToolTipText("Left to Right text direction");

            rtl = true;
        }
        else
        {
            textDirJButton.setText("RTL");
            textDirJButton.setToolTipText("Right to Left text direction");

            rtl = false;
        }

        if(refresh)
            refreshTree();
    }

    public boolean isUp()
    {
        return up;
    }

    private void setTreeOrientation(ActionEvent evt, boolean refresh)
    {
        if (treeOrientationJButton.getText().equals("Up"))
        {
            textDirJButton.setText("Down");
            textDirJButton.setToolTipText("Set the tree orientation downwards");

            up = true;
        }
        else
        {
            textDirJButton.setText("Up");
            textDirJButton.setToolTipText("Set the tree orientation upwards");

            up = false;
        }

        if(refresh)
            refreshTree();
    }
}
