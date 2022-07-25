/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SanchayTreeDrawingJPanel.java
 *
 * Created on 10 Dec, 2009, 8:21:38 PM
 */
package sanchay.tree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;
import sanchay.GlobalProperties;
import sanchay.common.SanchayClientsStateData;
import sanchay.common.types.ClientType;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.scroll.Rule;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.Cell;
import sanchay.table.gui.SanchayJTable;
import sanchay.table.gui.TreeDrawingTableCellRenderer;
import sanchay.table.gui.TreeDrawingTableEditor;
import sanchay.table.gui.TreeViewerTableCellRenderer;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.FileFilterImpl;
import sanchay.util.ImageUtil;
import sanchay.util.PrintUtilities;
import sanchay.util.UtilityFunctions;
import sanchay.common.types.ClientType;

/**
 *
 * @author anil
 */
public class SanchayTreeDrawingJPanel extends javax.swing.JPanel
        implements JPanelDialog, sanchay.gui.clients.SanchayClient, ItemListener, TreeDrawingEventListener, WindowListener
{
    protected ClientType clientType = ClientType.TREE_EDITOR;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected static KeyValueProperties stateKVProps;

    protected String textFile = "Untitled";
    protected String charset = sanchay.GlobalProperties.getIntlString("UTF-8");

    protected SSFStory ssfStory;

    protected String langEnc = "hin::utf8";

    protected String title = "";
    protected String curDir = System.getProperty("user.home");
    protected Rule rowView;
    protected SanchayJTable tableJTable;
    protected SanchayMutableTreeNode rootNode;
    protected int mode;
    protected boolean reverse;
    protected boolean rtl = false;
    protected boolean up = false;
    protected boolean collapsed = false;
    protected LinkedHashMap cfgToMMTreeMapping;
    protected TreeDrawingNodeEditPopupListener popupListener;
    protected SanchayTableModel fsSchema;

    protected PropertyTokens posTagsPT;
    protected PropertyTokens phraseNamesPT;

    protected Hashtable nodeLabelEditors;
    
    // Command buttions
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JButton printJButton;
    public javax.swing.JButton openJButton;
    public javax.swing.JButton saveJButton;
    public javax.swing.JButton saveAsImageJButton;
    public javax.swing.JButton saveAsEPSJButton;
    public javax.swing.JButton saveAsTextJButton;
    public javax.swing.JButton zoomInJButton;
    public javax.swing.JButton zoomOutJButton;
    
    public static final int REFRESH_TREE_ACTION = 0;

    /** Creates new form SanchayTreeDrawingJPanel */
    public SanchayTreeDrawingJPanel()
    {
        initComponents();
    }

    public SanchayTreeDrawingJPanel(SanchayMutableTreeNode root, int mode, String lang, boolean rev) {
        super();

        loadState(this);

        collapsed = true;

        initComponents();

        readProps();

        rowView = new Rule(Rule.VERTICAL, true);

        //Set up the scroll pane.
        tableJTable = new SanchayJTable(new SanchayTableModel(), SanchayJTable.TREE_MODE, root, false);
        tableJTable.addEventListener(this);
        JScrollPane tableScrollPane = new JScrollPane(tableJTable);
        tableScrollPane.setPreferredSize(new Dimension(300, 250));
        tableScrollPane.setViewportBorder(
                BorderFactory.createLineBorder(Color.black));

        //Put it in this panel.
        treeJPanel.add(tableScrollPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        reverse = rev;

        langEnc = lang;

        rootNode = root;

        this.mode = mode;

//        tableJScrollPane.setViewportView(tableJTable);

//        Graphics g = tableJScrollPane.getGraphics();

        UtilityFunctions.setComponentFont(tableJTable, langEnc);
//        tableJTable.setFont(new java.awt.Font("Dialog", 1, 14));

        tableJTable.setBackground(Color.WHITE);
        tableJTable.setRowHeight(120);
        tableJTable.getColumnModel().setColumnMargin(10);
        tableJTable.setShowHorizontalLines(false);
        tableJTable.setShowVerticalLines(false);
//        tableJTable.setIntercellSpacing(new java.awt.Dimension(5, 50));

        tableJTable.setCellSelectionEnabled(true);
        tableJTable.firePropertyChange("cellSelectionEnabled", false, true);
        tableJTable.setRowSelectionAllowed(true);
        tableJTable.firePropertyChange("rowSelectionAllowed", false, true);
        tableJTable.setColumnSelectionAllowed(true);
        tableJTable.firePropertyChange("columnSelectionAllowed", false, true);

//        tableJTable.setDragEnabled(false);
//        tableJTable.setDropTarget(null);

        JTextField ed = new JTextField();
        ed.setEditable(true);
        ed.setHorizontalAlignment(JTextField.CENTER);

//        DefaultCellEditor dced = new DefaultCellEditor(ed);
//        tableJTable.setDefaultEditor(String.class, dced);

        TreeDrawingTableEditor ced = new TreeDrawingTableEditor(langEnc);
        tableJTable.setDefaultEditor(String.class, ced);
        tableJTable.setDefaultEditor(SSFLexItem.class, ced);

        int lcount = rootNode.getAllLeaves().size();

        SanchayTableModel sanchayTableModel = new SanchayTableModel(0, lcount);
        tableJTable.setModel(sanchayTableModel);

        positionTree(rootNode, 0, lcount/2);

        if(reverse)
            rootNode.setValuesInTable(sanchayTableModel, mode, true);
        else
            rootNode.setValuesInTable(sanchayTableModel, mode);

        tableJTable.clearEdges();

        UtilityFunctions.fitColumnsToContent(tableJTable);

        rootNode.fillTreeEdges(tableJTable, mode);
        rootNode.fillGraphEdges(tableJTable, mode);

        TreeDrawingTableCellRenderer renderer = new TreeDrawingTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        if(!(mode == SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || mode == SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE))
            renderer.setToolTipText(GlobalProperties.getIntlString("Click_for_node_data"));

        if(sanchayTableModel.getColumnCount() > 0)
            tableJTable.setDefaultRenderer(sanchayTableModel.getColumnClass(0), renderer);

        tableJTable.setTableHeader(null);
        tableJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        refreshTree();

        init();
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    private void readProps()
    {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

        try {
            fsp.readDefaultProps();
            ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), "UTF-8"); //throws java.io.FileNotFoundException;

            FeatureStructuresImpl.setFSProperties(fsp);
            SSFNode.setSSFProperties(ssfp);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void positionTree(SanchayMutableTreeNode node, int level, int pos) {
        SanchayTableModel sanchayTableModel = (SanchayTableModel) tableJTable.getModel();

        if(mode == SSFPhrase.DEPENDENCY_RELATIONS_MODE) {
            // Skipping the root SSF node
            if(sanchayTableModel.getRowCount() == level - 1)
                sanchayTableModel.addRow();
        } else {
            if(sanchayTableModel.getRowCount() == level)
                sanchayTableModel.addRow();
        }

        if(node.isLeaf() == false) {
            int ccount = node.getAllLeaves().size();

            int chcount = node.getChildCount();

            int beg = pos - ccount/2;
            int chpos = 0;

            for (int i = 0; i < chcount; i++) {
                SanchayMutableTreeNode chnode = (SanchayMutableTreeNode) node.getChildAt(i);

                if(chnode.isLeaf() == false) {
                    int chccount = chnode.getAllLeaves().size();

                    chpos = beg + chccount/2;
                    beg += chccount;
                } else {
                    chpos = beg;
                    beg++;
                }

                positionTree(chnode, level + 1, chpos);
            }
        }

        if(mode == SSFPhrase.DEPENDENCY_RELATIONS_MODE)
            node.setRowIndex(level - 1);
        else
            node.setRowIndex(level);

        node.setColumnIndex(pos);
    }

    public SanchayMutableTreeNode getRoot() {
        return rootNode;
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

        if(size.height < screenSize.height - h1) {
            int h = rect.height;

            rect.y = (screenSize.height - h - h1)/2;
            rect.height = h + h1;
        } else {
            rect.y = h1/2;
            rect.height = size.height - h1;
        }

        if(size.width < screenSize.width - w1) {
            int w = rect.width;

            rect.x = (screenSize.width - w - w1)/2;
            rect.width = w + w1;
        } else {
            rect.x = w1/2;
            rect.width = size.width - w1;
        }

        dialog.setBounds(rect);
    }

    public void initPopupMenu()
    {
        popupListener = new TreeDrawingNodeEditPopupListener(tableJTable, treeDrawingJPopupMenu, nodeLabelEditors, getFSSchema());
        tableJTable.addMouseListener(popupListener);
    }

    private void zoomOutJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        if(tableJTable.getRowHeight() > 2)
            tableJTable.setRowHeight(tableJTable.getRowHeight() - 2);

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

    private void printJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
	PrintUtilities.printComponent(tableJTable);
    }

    private void openJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String path = null;

        if(stateKVProps == null)
            stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.TREE_EDITOR.toString());

        if(textFile != null)
        {
            File sfile = new File(textFile);

            if(sfile.exists() && sfile.getParentFile() != null)
                path = sfile.getParent();
            else
                path = stateKVProps.getPropertyValue("CurrentSrcDir");
        }
        else
            path = stateKVProps.getPropertyValue("CurrentSrcDir");

        JFileChooser chooser = null;

        if(path != null)
            chooser = new JFileChooser(path);
        else
            chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(this);

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            textFile = chooser.getSelectedFile().getAbsolutePath();

            stateKVProps.addProperty("CurrentSrcDir", chooser.getSelectedFile().getParent());

            ssfStory = new SSFStoryImpl();

            try
            {
                ssfStory.readFile(textFile, charset);
                
                if(ssfStory.countSentences() == 0)
                    return;

                SSFSentence sentence = ssfStory.getSentence(0);

                SSFPhrase rnode = sentence.getRoot();

                if(rnode != null)
                {
                    SSFProperties ssfp = SSFNode.getSSFProperties();

                    String rootName = ssfp.getProperties().getPropertyValueForPrint("rootName");

                    if(rnode.getName().equals(rootName) && rnode.countChildren() == 1)
                        rnode = (SSFPhrase) rnode.getChild(0);

                    rootNode = rnode;
                    
                    refreshTree();
                }
                
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex)
            {
                Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {

        if(rootNode == null)
            return;

        if(ssfStory == null)
        {
            ssfStory = new SSFStoryImpl();
            SSFSentence sentence = new SSFSentenceImpl();

            ssfStory.addSentence(sentence);

            sentence.setRoot((SSFPhrase) rootNode);
        }

        File tfile = new File(textFile);

        if(textFile.equals("Untitled"))
            saveAsTextJButtonActionPerformed(evt);
        else
        {
            try
            {
                ssfStory.save(textFile, charset);
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex)
            {
                Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveAsImageJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        try
        {
            JFileChooser chooser = new JFileChooser();

            javax.swing.filechooser.FileFilter filter = new FileFilterImpl(ImageUtil.getImageReaderList(ImageUtil.IMAGE_READERS));
            chooser.addChoosableFileFilter(filter);

            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                String imgFile = chooser.getSelectedFile().getAbsolutePath();

                BufferedImage image = new BufferedImage(tableJTable.getWidth(), tableJTable.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                tableJTable.paintAll(g2d);

                if (imgFile == null) return;

		int offset = imgFile.lastIndexOf( "." );
		String type = offset == -1 ? "jpg" : imgFile.substring(offset + 1);

                File file = new File( imgFile );

		ImageIO.write(image, UtilityFunctions.getExtension(file), file);
            }
        }
        catch (FileNotFoundException ex)
        {
            JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void saveAsEPSJButtonActionPerformed(java.awt.event.ActionEvent evt) {
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();

        javax.swing.filechooser.FileFilter filter = new FileFilterImpl("eps", GlobalProperties.getIntlString("Encapsulated_PostScript"));
        chooser.addChoosableFileFilter(filter);

        int returnVal = chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            String imgFile = chooser.getSelectedFile().getAbsolutePath();

            FileOutputStream finalImage;

            try
            {
                finalImage = new FileOutputStream(imgFile);

//                BufferedImage image = new BufferedImage(tableJTable.getWidth(), tableJTable.getHeight(), BufferedImage.TYPE_INT_RGB);
                EpsGraphics2D g = new EpsGraphics2D(GlobalProperties.getIntlString("Title"), finalImage, 0, 0, tableJTable.getWidth(), tableJTable.getHeight());
                tableJTable.paint(g);
                g.flush();
                g.close();
                finalImage.close();
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(SanchayTreeViewerJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
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
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                textFile = chooser.getSelectedFile().getAbsolutePath();

                sen.save(textFile, charset);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void expandJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(collapsed == false || mode != SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || cfgToMMTreeMapping == null)
            return;

        ((SSFPhrase) rootNode).expandMMTree(cfgToMMTreeMapping);

        refreshTree();

        collapsed = false;
    }

    private void collapseJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(collapsed || mode != SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || cfgToMMTreeMapping == null)
            return;

        ((SSFPhrase) rootNode).collapseLexicalItemsDeep();

        refreshTree();

        collapsed = true;
    }

    private void init()
    {
        commandsJPanel = new javax.swing.JPanel();
        zoomInJButton = new javax.swing.JButton();
        zoomOutJButton = new javax.swing.JButton();
        openJButton = new javax.swing.JButton();
        saveJButton = new javax.swing.JButton();
        saveAsTextJButton = new javax.swing.JButton();
        saveAsImageJButton = new javax.swing.JButton();
        saveAsEPSJButton = new javax.swing.JButton();
        printJButton = new javax.swing.JButton();

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

        openJButton.setText(GlobalProperties.getIntlString("Open"));
        openJButton.setToolTipText("Open a tree file");
        openJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(openJButton);

        saveJButton.setText(GlobalProperties.getIntlString("Save"));
        saveJButton.setToolTipText(GlobalProperties.getIntlString("Save_Annotated_Tree"));
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(saveJButton);

        saveAsImageJButton.setText(GlobalProperties.getIntlString("Save_As_Image"));
        saveAsImageJButton.setToolTipText(GlobalProperties.getIntlString("Save_As_Image"));
        saveAsImageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsImageJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(saveAsImageJButton);

        saveAsEPSJButton.setText(GlobalProperties.getIntlString("Save_As_EPS"));
        saveAsEPSJButton.setToolTipText(GlobalProperties.getIntlString("Save_As_EPS"));
        saveAsEPSJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsEPSJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(saveAsEPSJButton);

        saveAsTextJButton.setText(GlobalProperties.getIntlString("Save_as_Text"));
        saveAsTextJButton.setToolTipText(GlobalProperties.getIntlString("Save_as_Text"));
        saveAsTextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsTextJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(saveAsTextJButton);

        printJButton.setText(GlobalProperties.getIntlString("Print"));
        printJButton.setToolTipText(GlobalProperties.getIntlString("Print"));
        printJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printJButtonActionPerformed(evt);
            }
        });

        commandsJPanel.add(printJButton);

        add(commandsJPanel, BorderLayout.SOUTH);

        try
        {
            phraseNamesPT = new PropertyTokens(GlobalProperties.getHomeDirectory() + "/workspace/syn-annotation/non-terminals.txt", "UTF-8");
            posTagsPT = new PropertyTokens(GlobalProperties.getHomeDirectory() + "/workspace/syn-annotation/terminals.txt", "UTF-8");
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        nodeLabelEditors = new Hashtable(2);
        nodeLabelEditors.put(GlobalProperties.getIntlString("PhraseNames"), new DefaultComboBoxModel(phraseNamesPT.getCopyOfTokens()));
        nodeLabelEditors.put(GlobalProperties.getIntlString("POSTags"), new DefaultComboBoxModel(posTagsPT.getCopyOfTokens()));

        fsSchema = getFSSchema();

        initPopupMenu();
    }

    public SanchayTableModel getFSSchema()
    {
        if (fsSchema == null)
        {
            try
            {
                fsSchema = new SanchayTableModel(GlobalProperties.resolveRelativePath("props/feature-schema.txt"),
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

    public void refreshTree()
    {
        JTextField ed = new JTextField();
        ed.setEditable(true);
        ed.setHorizontalAlignment(JTextField.CENTER);

//        DefaultCellEditor dced = new DefaultCellEditor(ed);
//        tableJTable.setDefaultEditor(String.class, dced);
        
        TreeDrawingTableEditor ced = new TreeDrawingTableEditor(langEnc);
        tableJTable.setDefaultEditor(String.class, ced);
        tableJTable.setDefaultEditor(SSFLexItem.class, ced);

        tableJTable.clearCellObjects();

        int lcount = rootNode.getAllLeaves().size();
        SanchayTableModel sanchayTableModel = new SanchayTableModel(0, lcount);
        tableJTable.setModel(sanchayTableModel);

        positionTree(rootNode, 0, lcount/2);

        if(reverse)
            rootNode.setValuesInTable(sanchayTableModel, mode, true);
        else
            rootNode.setValuesInTable(sanchayTableModel, mode);

        tableJTable.clearEdges();

        UtilityFunctions.fitColumnsToContent(tableJTable);

        rootNode.fillTreeEdges(tableJTable, mode);
        rootNode.fillGraphEdges(tableJTable, mode);

        TreeViewerTableCellRenderer renderer = new TreeViewerTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        if(!(mode == SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE || mode == SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE))
            renderer.setToolTipText(GlobalProperties.getIntlString("Click_for_node_data"));

        if(sanchayTableModel.getColumnCount() > 0)
            tableJTable.setDefaultRenderer(sanchayTableModel.getColumnClass(0), renderer);

        tableJTable.setTableHeader(null);
        tableJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        tableJTable.requestFocusInWindow();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        treeDrawingJPopupMenu = new javax.swing.JPopupMenu();
        treeJPanel = new javax.swing.JPanel();
        treeCommandsJPanel = new javax.swing.JPanel();
        addNonTerminalJButton = new javax.swing.JButton();
        addTerminalJButton = new javax.swing.JButton();
        addTriangleJButton = new javax.swing.JButton();
        addBinaryJButton = new javax.swing.JButton();
        addTernaryJButton = new javax.swing.JButton();
        addAdjunctJButton = new javax.swing.JButton();
        addXBarJButton = new javax.swing.JButton();
        addFeatureJButton = new javax.swing.JButton();
        moveLeftJButton = new javax.swing.JButton();
        moveRightJButton = new javax.swing.JButton();
        deleteNodeJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout(4, 4));

        treeJPanel.setBackground(new java.awt.Color(255, 255, 255));
        treeJPanel.setLayout(new java.awt.BorderLayout());
        add(treeJPanel, java.awt.BorderLayout.CENTER);

        treeCommandsJPanel.setPreferredSize(new java.awt.Dimension(150, 300));
        treeCommandsJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 3));

        addNonTerminalJButton.setMnemonic('N');
        addNonTerminalJButton.setText("Non-Terminal");
        addNonTerminalJButton.setToolTipText("Add a non-terminal node");
        addNonTerminalJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNonTerminalJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addNonTerminalJButton);

        addTerminalJButton.setMnemonic('T');
        addTerminalJButton.setText("Terminal");
        addTerminalJButton.setToolTipText("Add a terminal node");
        addTerminalJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTerminalJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addTerminalJButton);

        addTriangleJButton.setMnemonic('G');
        addTriangleJButton.setText("Triangle");
        addTriangleJButton.setToolTipText("Add a compressed (triangle) node");
        addTriangleJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTriangleJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addTriangleJButton);

        addBinaryJButton.setMnemonic('B');
        addBinaryJButton.setText("Binary");
        addBinaryJButton.setToolTipText("Add a node with binary branching");
        addBinaryJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBinaryJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addBinaryJButton);

        addTernaryJButton.setMnemonic('R');
        addTernaryJButton.setText("Ternary");
        addTernaryJButton.setToolTipText("Add a node with ternary branching");
        addTernaryJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTernaryJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addTernaryJButton);

        addAdjunctJButton.setMnemonic('A');
        addAdjunctJButton.setText("Adjunct");
        addAdjunctJButton.setToolTipText("Add a parent node adjunct");
        addAdjunctJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAdjunctJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addAdjunctJButton);

        addXBarJButton.setMnemonic('X');
        addXBarJButton.setText("X-Bar");
        addXBarJButton.setToolTipText("Add a node with X-bar structure");
        addXBarJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addXBarJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addXBarJButton);

        addFeatureJButton.setMnemonic('F');
        addFeatureJButton.setText("Feature");
        addFeatureJButton.setToolTipText("Add a feature to a node");
        addFeatureJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFeatureJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(addFeatureJButton);

        moveLeftJButton.setMnemonic('L');
        moveLeftJButton.setText("Move Left");
        moveLeftJButton.setToolTipText("Move the node to the left");
        moveLeftJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLeftJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(moveLeftJButton);

        moveRightJButton.setMnemonic('O');
        moveRightJButton.setText("Move Right");
        moveRightJButton.setToolTipText("Move the node to the right");
        moveRightJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveRightJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(moveRightJButton);

        deleteNodeJButton.setMnemonic('D');
        deleteNodeJButton.setText("Delete");
        deleteNodeJButton.setToolTipText("Delete a node");
        deleteNodeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNodeJButtonActionPerformed(evt);
            }
        });
        treeCommandsJPanel.add(deleteNodeJButton);

        add(treeCommandsJPanel, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void addNonTerminalJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addNonTerminalJButtonActionPerformed
    {//GEN-HEADEREND:event_addNonTerminalJButtonActionPerformed
        // TODO add your handling code here:
        addNonTerminal(evt);
    }//GEN-LAST:event_addNonTerminalJButtonActionPerformed

    private void addTerminalJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addTerminalJButtonActionPerformed
    {//GEN-HEADEREND:event_addTerminalJButtonActionPerformed
        // TODO add your handling code here:
        addTerminal(evt);
    }//GEN-LAST:event_addTerminalJButtonActionPerformed

    private void addTriangleJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addTriangleJButtonActionPerformed
    {//GEN-HEADEREND:event_addTriangleJButtonActionPerformed
        // TODO add your handling code here:
        addTriangle(evt);
}//GEN-LAST:event_addTriangleJButtonActionPerformed

    private void addBinaryJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addBinaryJButtonActionPerformed
    {//GEN-HEADEREND:event_addBinaryJButtonActionPerformed
        // TODO add your handling code here:
        addBinary(evt);
    }//GEN-LAST:event_addBinaryJButtonActionPerformed

    private void addTernaryJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addTernaryJButtonActionPerformed
    {//GEN-HEADEREND:event_addTernaryJButtonActionPerformed
        // TODO add your handling code here:
        addTernary(evt);
    }//GEN-LAST:event_addTernaryJButtonActionPerformed

    private void addAdjunctJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addAdjunctJButtonActionPerformed
    {//GEN-HEADEREND:event_addAdjunctJButtonActionPerformed
        // TODO add your handling code here:
        addAdjunct(evt);
    }//GEN-LAST:event_addAdjunctJButtonActionPerformed

    private void addXBarJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addXBarJButtonActionPerformed
    {//GEN-HEADEREND:event_addXBarJButtonActionPerformed
        // TODO add your handling code here:
        addXBar(evt);
    }//GEN-LAST:event_addXBarJButtonActionPerformed

    private void addFeatureJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addFeatureJButtonActionPerformed
    {//GEN-HEADEREND:event_addFeatureJButtonActionPerformed
        // TODO add your handling code here:
        addFeature(evt);
    }//GEN-LAST:event_addFeatureJButtonActionPerformed

    private void deleteNodeJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteNodeJButtonActionPerformed
    {//GEN-HEADEREND:event_deleteNodeJButtonActionPerformed
        // TODO add your handling code here:
        deleteNode(evt);
    }//GEN-LAST:event_deleteNodeJButtonActionPerformed

    private void moveLeftJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveLeftJButtonActionPerformed
    {//GEN-HEADEREND:event_moveLeftJButtonActionPerformed
        // TODO add your handling code here:
        moveLeft(evt);
}//GEN-LAST:event_moveLeftJButtonActionPerformed

    private void moveRightJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveRightJButtonActionPerformed
    {//GEN-HEADEREND:event_moveRightJButtonActionPerformed
        // TODO add your handling code here:
        moveRight(evt);
}//GEN-LAST:event_moveRightJButtonActionPerformed

    private SSFNode getSelectedNode()
    {
        if(rootNode.getChildCount() == 0)
            return (SSFNode) rootNode;

        int r = tableJTable.getSelectedRow();
        int c = tableJTable.getSelectedColumn();

        if(r == -1 || c == -1)
            return null;

        Object cellObject = tableJTable.getCellObject(r, c);

        if(cellObject != null && cellObject instanceof SSFNode)
            return ((SSFNode) cellObject);

        return null;
    }

    private void addNonTerminal(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null)
            return;

        SSFPhrase nonTerminal = null;

        try
        {
            nonTerminal = new SSFPhrase("0", "", "X", "");

            selNode.add(nonTerminal);
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void addTerminal(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null)
            return;

        SSFLexItem terminal = null;

        try
        {
            terminal = new SSFLexItem("0", "term", "", "");

            selNode.add(terminal);
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void addTriangle(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null)
            return;

        SSFLexItem triangle = null;

        try
        {
            triangle = new SSFLexItem("0", "term", "", "");
            triangle.isTriangle(true);

            selNode.add(triangle);
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void addBinary(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null)
            return;

        SSFPhrase binarySubtree = null;
        SSFPhrase nonTerminalLeft = null;
        SSFPhrase nonTerminalRight = null;

        try
        {
            binarySubtree = new SSFPhrase("0", "", "XP", "");
            nonTerminalLeft = new SSFPhrase("0", "", "X", "");
            nonTerminalRight = new SSFPhrase("0", "", "Y", "");

            binarySubtree.add(nonTerminalLeft);
            binarySubtree.add(nonTerminalRight);

            selNode.add(binarySubtree);
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void addTernary(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null)
            return;

        SSFPhrase ternarySubtree = null;
        SSFPhrase nonTerminalLeft = null;
        SSFPhrase nonTerminalCentre = null;
        SSFPhrase nonTerminalRight = null;

        try
        {
            ternarySubtree = new SSFPhrase("0", "", "XP", "");
            nonTerminalLeft = new SSFPhrase("0", "", "X", "");
            nonTerminalCentre = new SSFPhrase("0", "", "Y", "");
            nonTerminalRight = new SSFPhrase("0", "", "Z", "");

            ternarySubtree.add(nonTerminalLeft);
            ternarySubtree.add(nonTerminalCentre);
            ternarySubtree.add(nonTerminalRight);

            selNode.add(ternarySubtree);
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void addAdjunct(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null || selNode == rootNode)
            return;

        SSFPhrase parentNode = (SSFPhrase) selNode.getParent();
        SSFPhrase nonTerminalLeft = null;

        try
        {
            nonTerminalLeft = new SSFPhrase("0", "", "∅", "");

            int snInd = parentNode.findChild(selNode);

            parentNode.insert(nonTerminalLeft, snInd);
            
            parentNode.formPhrase(snInd, 2);

            SSFPhrase adjunctNode = (SSFPhrase) parentNode.getChild(snInd);
            adjunctNode.setName("X");
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void addXBar(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null)
            return;

        SSFPhrase xbarSubtree = null;
        SSFPhrase xbarNode = null;
        SSFPhrase xbarSpec = null;
        SSFPhrase nonTerminalLeft = null;
        SSFPhrase nonTerminalRight = null;

        try
        {
            xbarSubtree = new SSFPhrase("0", "", "XP", "");
            xbarNode = new SSFPhrase("0", "", "X'", "");
            xbarSpec = new SSFPhrase("0", "", "Spec", "");
            nonTerminalLeft = new SSFPhrase("0", "", "X", "");
            nonTerminalRight = new SSFPhrase("0", "", "Comp", "");

            xbarSubtree.add(xbarSpec);
            xbarSubtree.add(xbarNode);

            xbarNode.add(nonTerminalLeft);
            xbarNode.add(nonTerminalRight);

            xbarSubtree.add(xbarNode);

            selNode.add(xbarSubtree);
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void addFeature(ActionEvent evt)
    {
        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
    }

    private void deleteNode(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null || selNode == rootNode)
            return;

        SSFPhrase parentNode = (SSFPhrase) selNode.getParent();

        parentNode.remove(selNode);

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(parentNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void moveLeft(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null || selNode == rootNode)
            return;

        SSFPhrase parentNode = (SSFPhrase) selNode.getParent();

        int curPos = parentNode.findChild(selNode);

        if(curPos == 0)
            return;

        parentNode.remove(selNode);

        parentNode.insert(selNode, curPos - 1);

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    private void moveRight(ActionEvent evt)
    {
        SSFNode selNode = getSelectedNode();

        if(selNode == null || selNode == rootNode)
            return;

        SSFPhrase parentNode = (SSFPhrase) selNode.getParent();

        int curPos = parentNode.findChild(selNode);

        if(curPos == parentNode.countChildren() - 1)
            return;

        parentNode.remove(selNode);

        parentNode.insert(selNode, curPos + 1);

        tableJTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));

        Cell c = tableJTable.findCellObject(selNode);

        if(c == null)
            c = tableJTable.findCellObject(rootNode);

        if(c != null)
            tableJTable.changeSelection(c.row, c.column, false, false);
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
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

    @Override
    public void treeChanged(TreeViewerEvent evt)
    {
        refreshTree();
    }

    public String getLangEnc()
    {
        return langEnc;
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

    public void setDialog(JDialog dialog)
    {
        this.dialog = dialog;
    }

    public String getTitle()
    {
        return title;
    }

    public JMenuBar getJMenuBar()
    {
        return null;
    }

    public JPopupMenu getJPopupMenu()
    {
        return null;
    }

    public JToolBar getJToolBar()
    {
        return null;
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        saveState(this);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
   }

    private static void saveState(SanchayTreeDrawingJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.TREE_EDITOR.toString());

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");

        if(currentDir == null)
            currentDir = ".";

        File file = null;

        if(editorInstance.textFile != null) {
            file = new File(editorInstance.textFile);

            if(file.exists()) {
                currentDir = file.getParent();
            }
        }

        stateKVProps.addProperty("CurrentDir", currentDir);
        stateKVProps.addProperty("LangEnc", editorInstance.getLangEnc());

        SanchayClientsStateData.save();
    }

    private static void loadState(SanchayTreeDrawingJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.TREE_EDITOR.toString());

        editorInstance.curDir = stateKVProps.getPropertyValue("CurrentDir");
        String langEnc = stateKVProps.getPropertyValue("LangEnc");

        if(langEnc == null) {
            langEnc = sanchay.GlobalProperties.getIntlString("hin::utf8");
            stateKVProps.addProperty("LangEnc", langEnc);
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Sanchay Tree Creator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        SSFPhrase root = null;
        
        try
        {
            root = new SSFPhrase("0", "", "S", "");
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        SanchayTreeDrawingJPanel newContentPane = new SanchayTreeDrawingJPanel(root, SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE, "eng::utf8", false);
        newContentPane.setOwner(frame);

        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();

        int inset = 35;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 5);

        frame.setVisible(true);

        newContentPane.requestFocusInWindow();
    }

    public static void main(String[] args)
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {

            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAdjunctJButton;
    private javax.swing.JButton addBinaryJButton;
    private javax.swing.JButton addFeatureJButton;
    private javax.swing.JButton addNonTerminalJButton;
    private javax.swing.JButton addTerminalJButton;
    private javax.swing.JButton addTernaryJButton;
    private javax.swing.JButton addTriangleJButton;
    private javax.swing.JButton addXBarJButton;
    private javax.swing.JButton deleteNodeJButton;
    private javax.swing.JButton moveLeftJButton;
    private javax.swing.JButton moveRightJButton;
    private javax.swing.JPanel treeCommandsJPanel;
    private javax.swing.JPopupMenu treeDrawingJPopupMenu;
    private javax.swing.JPanel treeJPanel;
    // End of variables declaration//GEN-END:variables
}
