/*
 * SSFCompareJPanel.java
 *
 * Created on January 23, 2006, 5:46 PM
 */

package sanchay.corpus.ssf.gui;

//import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.impl.SSFCorpusAnalyzer;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertiesTable;
import sanchay.properties.PropertyTokens;
import sanchay.common.types.ClientType;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.JTextAreaCellEditor;
import sanchay.gui.SelectTaskJPanel;
import sanchay.gui.WorkJPanelInterface;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayJDialog;
import sanchay.properties.PropertiesManager;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.tree.gui.SanchayTreeViewerJPanel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author  anil
 */
public class SSFCorpusAnalyzerJPanel extends javax.swing.JPanel
        implements WindowListener, WorkJPanelInterface, sanchay.gui.common.JPanelDialog,
        sanchay.gui.clients.AnnotationClient {

    protected ClientType clientType = ClientType.SYNTACTIC_ANNOTATION;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;
    
    protected String langEnc;

    protected String title = "";

    protected String workspace;

    protected SelectTaskJPanel selectTaskJPanel;

    protected PropertiesManager propman;
    protected PropertiesTable taskList;
    
    protected String taskName;
    protected String taskNames[];
    protected KeyValueProperties kvTaskProps[];
    
    protected SSFStory ssfStory[];
//    private SSFStory ssfUTF8Story[];
    
    protected PropertyTokens commentsPT[];
    
    protected DefaultComboBoxModel positions;
    
    protected int currentPosition;
    protected int currentWPosition;
    
    protected int currentTaskIndex;
    protected int currentCompareType;
    
    protected SanchayTableJPanel summaryTableJPanel;
    protected SanchayTreeJPanel sanchayTreeJPanels[];
    protected JPanel sanchayTreeJPanel;
    protected SanchayTableJPanel sanchayTableJPanel;

    protected PropertyTokens posTagsPT;
    protected KeyValueProperties morphTagsKVP;
    protected PropertyTokens phraseNamesPT;
    
    protected int ssfPhrase[]; // indices of different trees
    protected SSFCorpusAnalyzer ssfPhraseComparison;

    protected boolean propbankMode;
    protected PropbankInfoJPanel extraInfoJPanel;
    
    /** Creates new form SSFCompareJPanel */
    public SSFCorpusAnalyzerJPanel() {
        initComponents();

        parentComponent = this;
        
        Action act = new AbstractAction(GlobalProperties.getIntlString("Show_Detail")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        
        act.putValue(Action.SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_detailed_comparison."));
        act.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
        detailJButton.setAction(act);
        
        currentTaskIndex = 0;
        currentCompareType = 0;
        
        langEnc = GlobalProperties.getIntlString("hin::utf8");
        
        try {
            propman = new PropertiesManager(GlobalProperties.getHomeDirectory() + "/" + "workspace/syn-annotation/server-props.txt", GlobalProperties.getIntlString("UTF-8"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSFCorpusAnalyzerJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSFCorpusAnalyzerJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public SSFCorpusAnalyzerJPanel(boolean propbankMode) {
        this();
        
        this.propbankMode = propbankMode;

        if(propbankMode)
        {
            extraInfoJPanel = new PropbankInfoJPanel(this, langEnc);
            comparisonTreeJPanel.add(extraInfoJPanel, BorderLayout.EAST);
        }
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainJSplitPane = new javax.swing.JSplitPane();
        summaryJPanel = new javax.swing.JPanel();
        detailJPanel = new javax.swing.JPanel();
        comparisonTreeJPanel = new javax.swing.JPanel();
        compareOptionsJPanel = new javax.swing.JPanel();
        compareLayoutJPanel = new javax.swing.JPanel();
        compareLayoutJLabel = new javax.swing.JLabel();
        compareLayoutJComboBox = new javax.swing.JComboBox();
        diffTasksJPanel = new javax.swing.JPanel();
        selecTaskJLabel = new javax.swing.JLabel();
        selecTaskJComboBox = new javax.swing.JComboBox();
        treeJPanel = new javax.swing.JPanel();
        comparisonTableJPanel = new javax.swing.JPanel();
        bottomJPanel = new javax.swing.JPanel();
        positionJPanel = new javax.swing.JPanel();
        positionLeftJPanel = new javax.swing.JPanel();
        senNumJPanel = new javax.swing.JPanel();
        positionJLabel = new javax.swing.JLabel();
        positionJComboBox = new javax.swing.JComboBox();
        positionRightJPanel = new javax.swing.JPanel();
        openJButton = new javax.swing.JButton();
        navigateJPanel = new javax.swing.JPanel();
        firstJButton = new javax.swing.JButton();
        prevJButton = new javax.swing.JButton();
        nextJButton = new javax.swing.JButton();
        lastJButton = new javax.swing.JButton();
        commandsJPanel = new javax.swing.JPanel();
        saveJButton = new javax.swing.JButton();
        detailJButton = new javax.swing.JButton();
        openLeftJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        mainJSplitPane.setDividerLocation(170);
        mainJSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainJSplitPane.setMinimumSize(new java.awt.Dimension(201, 150));
        mainJSplitPane.setOneTouchExpandable(true);

        summaryJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Comparison Summary"));
        summaryJPanel.setLayout(new java.awt.BorderLayout(0, 2));
        mainJSplitPane.setTopComponent(summaryJPanel);

        detailJPanel.setLayout(new java.awt.CardLayout());

        comparisonTreeJPanel.setLayout(new java.awt.BorderLayout());

        compareOptionsJPanel.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        compareLayoutJPanel.setLayout(new java.awt.BorderLayout(2, 0));

        compareLayoutJLabel.setLabelFor(compareLayoutJComboBox);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        compareLayoutJLabel.setText(bundle.getString("Layout:")); // NOI18N
        compareLayoutJPanel.add(compareLayoutJLabel, java.awt.BorderLayout.WEST);

        compareLayoutJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Horizontally stacked", "Vertically stacked", "One by one" }));
        compareLayoutJComboBox.setEnabled(false);
        compareLayoutJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compareLayoutJComboBoxActionPerformed(evt);
            }
        });
        compareLayoutJPanel.add(compareLayoutJComboBox, java.awt.BorderLayout.CENTER);

        compareOptionsJPanel.add(compareLayoutJPanel);

        diffTasksJPanel.setLayout(new java.awt.BorderLayout(2, 0));

        selecTaskJLabel.setLabelFor(selecTaskJComboBox);
        selecTaskJLabel.setText(bundle.getString("Task:")); // NOI18N
        diffTasksJPanel.add(selecTaskJLabel, java.awt.BorderLayout.WEST);

        selecTaskJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selecTaskJComboBoxActionPerformed(evt);
            }
        });
        diffTasksJPanel.add(selecTaskJComboBox, java.awt.BorderLayout.CENTER);

        compareOptionsJPanel.add(diffTasksJPanel);

        comparisonTreeJPanel.add(compareOptionsJPanel, java.awt.BorderLayout.NORTH);

        treeJPanel.setLayout(new java.awt.BorderLayout());
        comparisonTreeJPanel.add(treeJPanel, java.awt.BorderLayout.CENTER);

        detailJPanel.add(comparisonTreeJPanel, "treeCard");

        comparisonTableJPanel.setLayout(new java.awt.BorderLayout());
        detailJPanel.add(comparisonTableJPanel, "tableCard");

        mainJSplitPane.setBottomComponent(detailJPanel);

        add(mainJSplitPane, java.awt.BorderLayout.CENTER);

        bottomJPanel.setLayout(new java.awt.BorderLayout(0, 2));

        positionJPanel.setPreferredSize(new java.awt.Dimension(195, 25));
        positionJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        positionLeftJPanel.setLayout(new java.awt.BorderLayout());

        senNumJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        positionJLabel.setText(bundle.getString("Go_to_sentence_number:")); // NOI18N
        senNumJPanel.add(positionJLabel);

        positionJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionJComboBoxActionPerformed(evt);
            }
        });
        senNumJPanel.add(positionJComboBox);

        positionLeftJPanel.add(senNumJPanel, java.awt.BorderLayout.CENTER);

        positionJPanel.add(positionLeftJPanel);

        positionRightJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        openJButton.setText(bundle.getString("Open")); // NOI18N
        openJButton.setToolTipText(bundle.getString("Open_the_task_for_comparison")); // NOI18N
        openJButton.setEnabled(false);
        openJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openJButtonActionPerformed(evt);
            }
        });
        positionRightJPanel.add(openJButton);

        positionJPanel.add(positionRightJPanel);

        bottomJPanel.add(positionJPanel, java.awt.BorderLayout.NORTH);

        navigateJPanel.setLayout(new java.awt.GridLayout(1, 4, 4, 0));

        firstJButton.setText(bundle.getString("First")); // NOI18N
        firstJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstJButtonActionPerformed(evt);
            }
        });
        navigateJPanel.add(firstJButton);

        prevJButton.setText(bundle.getString("Previous")); // NOI18N
        prevJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevJButtonActionPerformed(evt);
            }
        });
        navigateJPanel.add(prevJButton);

        nextJButton.setText(bundle.getString("Next")); // NOI18N
        nextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextJButtonActionPerformed(evt);
            }
        });
        navigateJPanel.add(nextJButton);

        lastJButton.setText(bundle.getString("Last")); // NOI18N
        lastJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastJButtonActionPerformed(evt);
            }
        });
        navigateJPanel.add(lastJButton);

        bottomJPanel.add(navigateJPanel, java.awt.BorderLayout.CENTER);

        commandsJPanel.setLayout(new java.awt.GridLayout(1, 2, 4, 0));

        saveJButton.setText(bundle.getString("Save")); // NOI18N
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(saveJButton);

        detailJButton.setText(bundle.getString("Detail")); // NOI18N
        detailJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(detailJButton);

        openLeftJButton.setText("Open"); // NOI18N
        openLeftJButton.setToolTipText("Open a file"); // NOI18N
        openLeftJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openLeftJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(openLeftJButton);

        bottomJPanel.add(commandsJPanel, java.awt.BorderLayout.SOUTH);

        add(bottomJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void openJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openJButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_openJButtonActionPerformed
    
    private void compareLayoutJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_compareLayoutJComboBoxActionPerformed
    {//GEN-HEADEREND:event_compareLayoutJComboBoxActionPerformed
// TODO add your handling code here:
        displayDetailTrees();
        
        detailJPanel.setVisible(false);
        detailJPanel.setVisible(true);
    }//GEN-LAST:event_compareLayoutJComboBoxActionPerformed
    
    private void selecTaskJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selecTaskJComboBoxActionPerformed
    {//GEN-HEADEREND:event_selecTaskJComboBoxActionPerformed
// TODO add your handling code here:
        currentTaskIndex = selecTaskJComboBox.getSelectedIndex();
        
//        if(treeJPanel.getComponentCount() > 0)
//            treeJPanel.removeAll();
        
        if(compareLayoutJComboBox.getSelectedItem().equals(GlobalProperties.getIntlString("One_by_one")) == true) {
            currentTaskIndex = selecTaskJComboBox.getSelectedIndex();

            if(treeJPanel.getComponentCount() > 0) {
                treeJPanel.removeAll();
            }
            
            JPanel jp = fillDetailTreeJPanel(currentTaskIndex);
            
            if(jp == null)
            {
                treeJPanel.add(sanchayTreeJPanel, BorderLayout.CENTER);

                if(propbankMode) {
                    extraInfoJPanel.setSSFPhraseJPanel((SanchayTreeJPanel) sanchayTreeJPanel);
                }
            }
            else
            {
                treeJPanel.add(jp, BorderLayout.CENTER);

                if(propbankMode) {
                    extraInfoJPanel.setSSFPhraseJPanel((SanchayTreeJPanel) jp);
                }
            }
            
            detailJPanel.setVisible(false);
            detailJPanel.setVisible(true);
        }
        else
        {
            if(propbankMode) {
                extraInfoJPanel.setSSFPhraseJPanel((SanchayTreeJPanel) sanchayTreeJPanels[currentTaskIndex]);
            }
        }
    }//GEN-LAST:event_selecTaskJComboBoxActionPerformed
    
    private void detailJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_detailJButtonActionPerformed
    {//GEN-HEADEREND:event_detailJButtonActionPerformed
// TODO add your handling code here:
        displayDetail();
    }//GEN-LAST:event_detailJButtonActionPerformed
    
    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveJButtonActionPerformed
    {//GEN-HEADEREND:event_saveJButtonActionPerformed
// TODO add your handling code here:
        String story = kvTaskProps[0].getPropertyValue("SSFCorpusStoryFile");

        try {
            ssfStory[currentTaskIndex].save(story, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSFCorpusAnalyzerJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSFCorpusAnalyzerJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveJButtonActionPerformed
    
    private void lastJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lastJButtonActionPerformed
    {//GEN-HEADEREND:event_lastJButtonActionPerformed
// TODO add your handling code here:
        if(propbankMode) {
            setCurrentPosition(ssfStory[0].countSentences() - 1, extraInfoJPanel.getWPos2SPosMap().countProperties() - 1);
        }
        else {
            setCurrentPosition(ssfStory[0].countSentences() - 1, -1);
        }
    }//GEN-LAST:event_lastJButtonActionPerformed
    
    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextJButtonActionPerformed
    {//GEN-HEADEREND:event_nextJButtonActionPerformed
// TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        if(Integer.parseInt(pos) == positions.getSize()) {
            return;
        }

        int cp = 0;
        int cwp = -1;

        try {
            if(propbankMode)
            {
                cwp = Integer.parseInt(pos);
                cp = Integer.parseInt(extraInfoJPanel.getWPos2SPosMap().getPropertyValue(pos));
            }
            else {
                cp = Integer.parseInt(pos);
            }

            setCurrentPosition(cp, cwp);
        } catch(NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
    }//GEN-LAST:event_nextJButtonActionPerformed
    
    private void prevJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_prevJButtonActionPerformed
    {//GEN-HEADEREND:event_prevJButtonActionPerformed
// TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        if(Integer.parseInt(pos) == 1) {
            return;
        }

        int cp = 0;
        int cwp = -1;

        try {
            if(propbankMode)
            {
                cwp = Integer.parseInt(pos);
                cp = Integer.parseInt(extraInfoJPanel.getWPos2SPosMap().getPropertyValue(pos));
            }
            else {
                cp = Integer.parseInt(pos);
            }

            setCurrentPosition(cp - 2, cwp - 2);
        } catch(NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
    }//GEN-LAST:event_prevJButtonActionPerformed
    
    private void firstJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_firstJButtonActionPerformed
    {//GEN-HEADEREND:event_firstJButtonActionPerformed
// TODO add your handling code here:
        setCurrentPosition(0, 0);
    }//GEN-LAST:event_firstJButtonActionPerformed
    
    private void positionJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_positionJComboBoxActionPerformed
    {//GEN-HEADEREND:event_positionJComboBoxActionPerformed
// TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        int cp = 0;
        int cwp = -1;

        try {
            if(propbankMode)
            {
                cwp = Integer.parseInt(pos);
                cp = Integer.parseInt(extraInfoJPanel.getWPos2SPosMap().getPropertyValue(pos));
            }
            else {
                cp = Integer.parseInt(pos);
            }

            setCurrentPosition(cp - 1, cwp - 1);
        } catch(NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
    }//GEN-LAST:event_positionJComboBoxActionPerformed

    private void openLeftJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openLeftJButtonActionPerformed
        // TODO add your handling code here:
        openLeft();
    }//GEN-LAST:event_openLeftJButtonActionPerformed

    public ClientType getClientType()
    {
        return clientType;
    }

    public void openLeft()
    {
        if(taskList == null)
        {
            taskList = new PropertiesTable();
            
            taskList.addRow(new String[] {"Task-1",
                "tmp/task-1.txt",
                "UTF-8",
                "User1",
                "User2",
                "Adjudicator1",
                "Adjudicator2"});
        }
    }

    public void initPropbank()
    {
        if(propbankMode == false) {
            return;
        }

        extraInfoJPanel.setWPos2SPosMap(new KeyValueProperties());

//        initNavigationList();

        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);

        int count = ssfStory[0].countSentences();

        Vector<String> pvec = new Vector<String>(count);

        int wpos = 0;

        for(int i = 0; i < count; i++) {
            SSFSentence lsentence = ssfStory[0].getSentence(i);

            SSFPhrase root = lsentence.getRoot();

            List<SSFNode> nodesTag = root.getNodesForName(extraInfoJPanel.getNavigationTag());
            List<SSFNode> nodesStem = root.getNodesForAttribVal("lex", extraInfoJPanel.getNavigationWord(), true);

            List<SSFNode> nodes = (List) UtilityFunctions.getIntersection(nodesTag, nodesStem);

            if(nodes == null || nodes.isEmpty()) {
                continue;
            }

            int ncount = nodes.size();

            for (int j = 0; j < ncount; j++)
            {
                wpos++;

                extraInfoJPanel.getWPos2SPosMap().addProperty("" + wpos, "" + (i + 1));
                pvec.add("" + wpos);
            }
        }

        if(extraInfoJPanel.getWPos2SPosMap().countProperties() == 0)
        {
            JOptionPane.showMessageDialog(this, "There was no word for the given stem and the tag.", "No Match Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        extraInfoJPanel.prepareSPos2WPosMap();

        positions = new DefaultComboBoxModel(pvec);
        positionJComboBox.setModel(positions);

        displayCurrentPosition();

        owner.setCursor(cursor);
    }

    private void highlightWord() {
        if(ssfPhraseComparison == null) {
            return;
        }

        String wposString = (currentWPosition + 1) + "";
        String posString = (currentPosition + 1) + "";

        boolean beg = false;

        int back = 1;

        while(beg == false)
        {
            String bposString = extraInfoJPanel.getWPos2SPosMap().getPropertyValue((currentWPosition + 1 - back) + "");

            if(bposString == null || bposString.equals(posString) == false) {
                beg = true;
            }
            else {
                back++;
            }
        }

        int wIndex = back - 1;

        int tcount = ssfPhraseComparison.countNodes();

        for (int i = 0; i < tcount; i++)
        {
            SSFPhrase root = ssfPhraseComparison.getNode(i);

            root.clearHighlights();

            List<SSFNode> nodesTag = root.getNodesForName(extraInfoJPanel.getNavigationTag());
            List<SSFNode> nodesStem = root.getNodesForAttribVal("lex", extraInfoJPanel.getNavigationWord(), true);

            List<SSFNode> nodes = (List) UtilityFunctions.getIntersection(nodesTag, nodesStem);

            if(nodes == null || nodes.isEmpty()) {
                return;
            }

            SSFNode currentNode = (SSFNode) nodes.get(wIndex);

            TreePath currentPath = new TreePath(currentNode.getPath());

            SanchayTreeJPanel ssfPhraseJPanel = sanchayTreeJPanels[i];

            ssfPhraseJPanel.getJTree().setSelectionPath(currentPath);
            ssfPhraseJPanel.getJTree().scrollPathToVisible(currentPath);

            currentNode.isHighlighted(true);
        }

//        editWordInfoExamples();
    }
    
    private int getTreeMode(int compare_type) {
        if(compare_type == SSFCorpusAnalyzer.MM_TREES) {
            return SanchayTreeJPanel.MM_TREE_MODE;
        }
        
        return SanchayTreeJPanel.SSF_MODE;
    }

    public void setCurrentPosition(int cp)
    {
        
    }
    
    public void setCurrentPosition(int cp, int cwp) {
        int slSize = ssfStory[0].countSentences();

        if(cp >= 0 && cp < slSize) {
            if(cp != currentPosition) {
                currentPosition = cp;
            }

            if(propbankMode && cwp != currentWPosition) {
                currentWPosition = cwp;
            }

            displayCurrentPosition();
        }
    }
    
    private void displayCurrentPosition() {
        String currentPositionString = Integer.toString(currentPosition + 1);

        if(propbankMode)
        {
//            currentWPosition = Integer.parseInt(wpos2sposMap.getPropertyValue(currentPositionString)) - 1;
            String currentWPositionString = Integer.toString(currentWPosition + 1);
            positionJComboBox.setSelectedItem(currentWPositionString);

            highlightWord();
        }
        else {
            positionJComboBox.setSelectedItem(currentPositionString);
        }
        
        compare();
    }
    
    private void displayDetail() {
        int cct = summaryTableJPanel.getJTable().getSelectedRow();
        
        if(cct >= 0) {
            currentCompareType = cct;
        }
        
        SanchayTableModel comparisonTable = null;
        
        switch(currentCompareType) {
            case SSFCorpusAnalyzer.OVERALL_ANNOTATION:
                compareLayoutJComboBox.setEnabled(true);
                selecTaskJComboBox.setEnabled(true);
                
                ssfPhrase = ssfPhraseComparison.compareOverall();
                displayDetailTrees();
                break;
            case SSFCorpusAnalyzer.POS_TAGS:
                compareLayoutJComboBox.setEnabled(false);
                selecTaskJComboBox.setEnabled(false);
                
                comparisonTable = ssfPhraseComparison.comparePOSTags();
                sanchayTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(comparisonTable, langEnc);
                
                if(comparisonTableJPanel.getComponentCount() == 1) {
                    comparisonTableJPanel.remove(0);
                }
                
                comparisonTableJPanel.add(sanchayTableJPanel, BorderLayout.CENTER);
                ((CardLayout) detailJPanel.getLayout()).show(detailJPanel, "tableCard");
                break;
            case SSFCorpusAnalyzer.CHUNKS:
                compareLayoutJComboBox.setEnabled(false);
                selecTaskJComboBox.setEnabled(false);
                
                comparisonTable = ssfPhraseComparison.compareChunks();
                sanchayTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(comparisonTable, langEnc);
                
                if(comparisonTableJPanel.getComponentCount() == 1) {
                    comparisonTableJPanel.remove(0);
                }
                
                comparisonTableJPanel.add(sanchayTableJPanel, BorderLayout.CENTER);
                ((CardLayout) detailJPanel.getLayout()).show(detailJPanel, "tableCard");
                break;
            case SSFCorpusAnalyzer.MM_TREES:
                compareLayoutJComboBox.setEnabled(true);
                selecTaskJComboBox.setEnabled(false);
                
                ssfPhrase = ssfPhraseComparison.compareMMTrees();
                displayDetailTrees();
                break;
            case SSFCorpusAnalyzer.LEXITEM_FEATURE_TABLE:
                compareLayoutJComboBox.setEnabled(false);
                selecTaskJComboBox.setEnabled(false);
                
                comparisonTable = ssfPhraseComparison.compareLexItemFeatures();
                sanchayTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(comparisonTable, langEnc);
                
                if(comparisonTableJPanel.getComponentCount() == 1) {
                    comparisonTableJPanel.remove(0);
                }
                
                comparisonTableJPanel.add(sanchayTableJPanel, BorderLayout.CENTER);
                ((CardLayout) detailJPanel.getLayout()).show(detailJPanel, "tableCard");
                break;
            case SSFCorpusAnalyzer.CHUNK_FEATURE_TABLE:
                compareLayoutJComboBox.setEnabled(false);
                selecTaskJComboBox.setEnabled(false);
                
                comparisonTable = ssfPhraseComparison.compareChunkFeatures();
                sanchayTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(comparisonTable, langEnc);
                
                if(comparisonTableJPanel.getComponentCount() == 1) {
                    comparisonTableJPanel.remove(0);
                }
                
                comparisonTableJPanel.add(sanchayTableJPanel, BorderLayout.CENTER);
                ((CardLayout) detailJPanel.getLayout()).show(detailJPanel, "tableCard");
                break;
            case SSFCorpusAnalyzer.COMMENTS:
                compareLayoutJComboBox.setEnabled(false);
                selecTaskJComboBox.setEnabled(false);
                
                comparisonTable = ssfPhraseComparison.compareComments();
                int cls[] = new int[comparisonTable.getColumnCount() - 1];
                sanchayTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(comparisonTable, langEnc);
                
                TableCellEditor ce = new JTextAreaCellEditor();
                JTable jt = sanchayTableJPanel.getJTable();
//		jt.setDefaultEditor()
                
                jt.setRowHeight(50);
                
                for(int i = 1; i < comparisonTable.getColumnCount(); i++) {
                    cls[i - 1] = i;
                }
                
                comparisonTable.setEditableColumns(cls);
                
                for(int i = 1; i < comparisonTable.getColumnCount(); i++) {
                    jt.getColumn(jt.getColumnName(i)).setCellEditor(ce);
                }
                
                if(comparisonTableJPanel.getComponentCount() == 1) {
                    comparisonTableJPanel.remove(0);
                }
                
                comparisonTableJPanel.add(sanchayTableJPanel, BorderLayout.CENTER);
                ((CardLayout) detailJPanel.getLayout()).show(detailJPanel, "tableCard");
                break;
        }
        
        detailJPanel.setVisible(false);
        detailJPanel.setVisible(true);
    }
    
    private JPanel fillDetailTreeJPanel(int taskInd) {
        SSFPhrase tree = null;
        
        if(currentCompareType == SSFCorpusAnalyzer.OVERALL_ANNOTATION) {
            tree = ssfPhraseComparison.getNode(taskInd);
        }
        else if(currentCompareType == SSFCorpusAnalyzer.MM_TREES) {
            tree = ssfPhraseComparison.getMMTree(taskInd);
        }
        
        if(tree == null) {
            JPanel jp = new JPanel(new BorderLayout());
            jp.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            JScrollPane jsp = new JScrollPane();
            JTextArea jta = new JTextArea();
            jta.setLineWrap(true);
            jta.setText(GlobalProperties.getIntlString("Unable_to_construct_tree._Incomplete_or_incorrect_annotation."));
            jsp.setViewportView(jta);
            jp.add(jsp, BorderLayout.CENTER);
            
            // There is no tree. Error.
            return jp;
        }
        
        // Fill
        
        if(getTreeMode(currentCompareType) == SanchayTreeJPanel.MM_TREE_MODE) {
            sanchayTreeJPanel = new SanchayTreeViewerJPanel(tree, SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE, langEnc);
        } else
        {
            sanchayTreeJPanel = SanchayTreeJPanel.createSSFCompareJPanel(tree, phraseNamesPT, posTagsPT, langEnc);

            ((SanchayTreeJPanel) sanchayTreeJPanel).setPropbankMode(propbankMode);

            ((SanchayTreeJPanel) sanchayTreeJPanel).showControlTabs(false);
        }

        sanchayTreeJPanel.setBorder(new TitledBorder(taskNames[taskInd]));
        
        // Null means successful!
        return null;
    }
    
    private void displayDetailTrees() {
        if(treeJPanel.getComponentCount() > 0) {
            treeJPanel.removeAll();
        }
        
        if(compareLayoutJComboBox.getSelectedItem().equals(GlobalProperties.getIntlString("One_by_one")) == true
                || taskNames.length == 1) {
//            selecTaskJComboBox.setEnabled(true);
            currentTaskIndex = selecTaskJComboBox.getSelectedIndex();
            
            JPanel jp = fillDetailTreeJPanel(currentTaskIndex);
            
            if(jp == null)
            {
                treeJPanel.add(sanchayTreeJPanel, BorderLayout.CENTER);
                sanchayTreeJPanels[0] = (SanchayTreeJPanel) sanchayTreeJPanel;
            }
            else
            {
                treeJPanel.add(jp, BorderLayout.CENTER);
                sanchayTreeJPanels[0] = (SanchayTreeJPanel) jp;
            }

            if(propbankMode)
            {
                ((SSFTreeCellRendererNew) sanchayTreeJPanels[0].getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.PROPBANK_ANNOTATION);
                sanchayTreeJPanels[0].showControlTabs(false);
            }
            else {
                    ((SSFTreeCellRendererNew) sanchayTreeJPanels[0].getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);
            }

        } else if(compareLayoutJComboBox.getSelectedItem().equals(GlobalProperties.getIntlString("Vertically_stacked")) == true) {
            JSplitPane prevjsp = null;
            JSplitPane jsp = new JSplitPane();
            JSplitPane treeJSplitPane = jsp;
            jsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
            
            int divlocation = (treeJPanel.getSize().height - treeJPanel.getInsets().bottom
                    - treeJSplitPane.getInsets().bottom ) / taskNames.length - treeJSplitPane.getDividerSize();
//            int divlocation = (1/taskNames.length);
            
            jsp.setDividerLocation(divlocation);
            jsp.setResizeWeight(1/taskNames.length);
            
            for (int i = 0; i < taskNames.length; i++) {
                JPanel jp = fillDetailTreeJPanel(i);

                if(jp == null) {
                    sanchayTreeJPanels[i] = (SanchayTreeJPanel) sanchayTreeJPanel;
                }
                else {
                    sanchayTreeJPanels[i] = (SanchayTreeJPanel) jp;
                }

                if(propbankMode)
                {
                    ((SSFTreeCellRendererNew) sanchayTreeJPanels[i].getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.PROPBANK_ANNOTATION);
                    sanchayTreeJPanels[i].showControlTabs(false);
                }
                else {
                    ((SSFTreeCellRendererNew) sanchayTreeJPanels[i].getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);
                }
                
                if(i == 0) {
                    if(jp == null)
                    {
                        jsp.setTopComponent(sanchayTreeJPanel);
                    }
                    else
                    {
                        jsp.setTopComponent(jp);
                    }
                } else if(i == taskNames.length - 1) {
                    if(jp == null) {
                        jsp.setBottomComponent(sanchayTreeJPanel);
                    }
                    else {
                        jsp.setBottomComponent(jp);
                    }
                } else {
                    jsp = new JSplitPane();
                    jsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    jsp.setDividerLocation(divlocation);
                    jsp.setResizeWeight(1/taskNames.length);
                    
                    if(jp == null) {
                        jsp.setTopComponent(sanchayTreeJPanel);
                    }
                    else {
                        jsp.setTopComponent(jp);
                    }
                    
                    prevjsp.setBottomComponent(jsp);
                }
                
                prevjsp = jsp;
            }
            
            treeJPanel.add(treeJSplitPane, BorderLayout.CENTER);
//            selecTaskJComboBox.setEnabled(false);
        } else if(compareLayoutJComboBox.getSelectedItem().equals(GlobalProperties.getIntlString("Horizontally_stacked")) == true) {
            JSplitPane prevjsp = null;
            JSplitPane jsp = new JSplitPane();
            JSplitPane treeJSplitPane = jsp;
            jsp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            
            int divlocation = (treeJPanel.getSize().width - treeJPanel.getInsets().right
                    - treeJSplitPane.getInsets().right ) / taskNames.length - treeJSplitPane.getDividerSize();
//            int divlocation = (1/taskNames.length);
            
            jsp.setDividerLocation(divlocation);
            jsp.setResizeWeight(1/taskNames.length);
            
            for (int i = 0; i < taskNames.length; i++) {
                JPanel jp = fillDetailTreeJPanel(i);

                if(jp == null) {
                    sanchayTreeJPanels[i] = (SanchayTreeJPanel) sanchayTreeJPanel;
                }
                else {
                    sanchayTreeJPanels[i] = (SanchayTreeJPanel) jp;
                }

                if(propbankMode)
                {
                    ((SSFTreeCellRendererNew) sanchayTreeJPanels[i].getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.PROPBANK_ANNOTATION);
                    sanchayTreeJPanels[i].showControlTabs(false);
                }
                else {
                    ((SSFTreeCellRendererNew) sanchayTreeJPanels[i].getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);
                }
                
                if(i == 0) {
                    if(jp == null) {
                        jsp.setLeftComponent(sanchayTreeJPanel);
                    }
                    else {
                        jsp.setLeftComponent(jp);
                    }
                } else if(i == taskNames.length - 1) {
                    if(jp == null) {
                        jsp.setRightComponent(sanchayTreeJPanel);
                    }
                    else {
                        jsp.setRightComponent(jp);
                    }
                } else {
                    jsp = new JSplitPane();
                    jsp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
                    jsp.setDividerLocation(divlocation);
                    jsp.setResizeWeight(1/taskNames.length);
                    
                    if(jp == null) {
                        jsp.setLeftComponent(sanchayTreeJPanel);
                    }
                    else {
                        jsp.setLeftComponent(jp);
                    }
                    
                    prevjsp.setRightComponent(jsp);
                }
                
                prevjsp = jsp;
            }
            
            treeJPanel.add(treeJSplitPane, BorderLayout.CENTER);
//            selecTaskJComboBox.setEnabled(false);
        }
        
        ((CardLayout) detailJPanel.getLayout()).show(detailJPanel, "treeCard");
    }
    
    // Compare annotations for the current sentence
    public void compare() {
        SSFPhrase nodes[] = new SSFPhrase[taskNames.length];
        String comments[] = new String[taskNames.length];
        
        for (int i = 0; i < taskNames.length; i++) {
            nodes[i] = ssfStory[i].getSentence(currentPosition).getRoot();
            comments[i] = commentsPT[i].getToken(currentPosition);
        }
        
        ssfPhraseComparison = new SSFCorpusAnalyzer(nodes, taskNames, comments);
        
        SanchayTableModel summaryComparisonTable = ssfPhraseComparison.comparisonSummary();
        summaryTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(summaryComparisonTable, langEnc);
        
        if(summaryJPanel.getComponentCount() > 0) {
            summaryJPanel.remove(0);
        }
        
        summaryJPanel.add(summaryTableJPanel, BorderLayout.CENTER);
        
        summaryTableJPanel.getJTable().addRowSelectionInterval(currentCompareType, currentCompareType);
        
        displayDetail();
    }

    public SelectTaskJPanel getSelectTaskJPanel()
    {
        return selectTaskJPanel;
    }

    public void setSelectTaskJPanel(SelectTaskJPanel selectTaskJPanel)
    {
        this.selectTaskJPanel = selectTaskJPanel;
    }

    @Override
    public String getLangEnc()
    {
        return langEnc;
    }
    
    @Override
    public Frame getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Frame frame) {
        owner = (JFrame) frame;
    }

    @Override
    public void setParentComponent(Component parentComponent)
    {
        this.parentComponent = parentComponent;
    }
    
    @Override
    public void setDialog(JDialog d) {
        dialog = d;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public JMenuBar getJMenuBar() {
        return null;
    }
    
    @Override
    public JPopupMenu getJPopupMenu() {
        return null;
    }
    
    @Override
    public JToolBar getJToolBar() {
        return null;
    }
    
    @Override
    public void setTaskName(String tn) {
        taskName = tn;
    }
    
    public void setTaskNames(String tn[]) {
        if(tn == null) {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Tasks_not_selected."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        String users[] = SelectTaskJPanel.getTaskUsers(taskList, taskName);
        
        taskNames = new String[]{taskName + "-" + users[0], taskName + "-" + users[1]};
        kvTaskProps = new KeyValueProperties[taskNames.length];
        
        ssfStory = new SSFStory[taskNames.length];
        //ssfUTF8Story = new SSFStory[tn.length];
        
        commentsPT = new PropertyTokens[taskNames.length];
        
        DefaultComboBoxModel cm = new DefaultComboBoxModel(taskNames);
        selecTaskJComboBox.setModel(cm);
    }
    
    @Override
    public void configure() {
        if(taskNames == null) {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Tasks_not_selected."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            setVisible(false);
            return;
        }
        
        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);
        
//        AnnotationClient owner = (AnnotationClient) getOwner();
//        PropertiesManager pm = owner.getPropertiesManager();
//        PropertiesTable tasks = (PropertiesTable) pm.getPropertyContainer("tasks", PropertyType.PROPERTY_TABLE);
        
//        PropertiesTable tasks = owner.getTaskList();

        sanchayTreeJPanels = new SanchayTreeJPanel[taskNames.length];
        
        Vector rows = taskList.getRows("TaskName", taskName);

        if(rows.size() != 1) {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Not_a_valid_task_name:_") + taskName, GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            dialog.setVisible(false);
        }

        String cols[] = {"TaskKVPropFile", "TaskKVPropCharset"};
        Vector vals = taskList.getValues("TaskName", taskName, cols);

        vals = (Vector) vals.get(0);
        String taskKVPropFile = (String) vals.get(0);
        String taskKVPropCharset = (String) vals.get(1);
       
        for(int t = 0; t < taskNames.length; t++) {
            ssfStory[t] = new SSFStoryImpl();
            
            try {
                kvTaskProps[t] = new KeyValueProperties(taskKVPropFile, taskKVPropCharset);

                if(t == 0)
                {
                    posTagsPT = new PropertyTokens(kvTaskProps[t].getPropertyValue("POSTagsFile"), kvTaskProps[t].getPropertyValue("POSTagsCharset"));
                    morphTagsKVP = new KeyValueProperties(kvTaskProps[t].getPropertyValue("MorphTagsFile"), kvTaskProps[t].getPropertyValue("POSTagsCharset"));
                    phraseNamesPT = new PropertyTokens(kvTaskProps[t].getPropertyValue("PhraseNamesFile"), kvTaskProps[t].getPropertyValue("PhraseNamesCharset"));
                }
                
                String story = kvTaskProps[t].getPropertyValue("SSFCorpusStoryFile");
                //            String storyUTF8 = kvTaskProps.getPropertyValue("SSFCorpusStoryUTF8File");
                
                String ssfp = kvTaskProps[t].getPropertyValue("SSFPropFile");
                String fsm = kvTaskProps[t].getPropertyValue("MFeaturesFile");
                String fso = kvTaskProps[t].getPropertyValue("OFeaturesFile");
                String fsps = kvTaskProps[t].getPropertyValue("PAttributesFile");
                String fsd = kvTaskProps[t].getPropertyValue("DAttributesFile");
                String fss = kvTaskProps[t].getPropertyValue("SAttributesFile");
                String fsp = kvTaskProps[t].getPropertyValue("FSPropFile");
                
                if(FeatureStructuresImpl.getFSProperties() == null) {
                    FSProperties fsProps = new FSProperties();
                    fsProps.read(fsm, fso, fsp, fsps, fsd, fss, GlobalProperties.getIntlString("UTF-8"));
                    FeatureStructuresImpl.setFSProperties(fsProps);
                }
                
                if(SSFNode.getSSFProperties() == null) {
                    SSFProperties ssfProps = new SSFProperties();
                    ssfProps.read(ssfp, GlobalProperties.getIntlString("UTF-8"));
                    SSFNode.setSSFProperties(ssfProps);
                }
                
                langEnc = kvTaskProps[t].getPropertyValue("Language");
                
                ssfStory[t].readFile(story);
                //            ssfUTF8Story[t].readFile(storyUTF8, ssfProps, fsProps)
                
                int senCount = ssfStory[t].countSentences();
                if(senCount > 0) {
                    try {
                        commentsPT[t] = new PropertyTokens(kvTaskProps[t].getPropertyValue("TaskPropFile") + ".comments", GlobalProperties.getIntlString("UTF-8"));
                    } catch(FileNotFoundException e) {
                        commentsPT[t] = new PropertyTokens(senCount);
                        
                        for(int i = 1; i <= senCount; i++) {
                            commentsPT[t].addToken("");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_in_task_properties_for_the_task:_") + taskNames[t], GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception e) {
                JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_in_task_properties_for_the_task:_") + taskNames[t], GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        if(propbankMode)
        {
            extraInfoJPanel.initWordNavigationList();
            extraInfoJPanel.initTagNavigationList();
            initPropbank();

            extraInfoJPanel.fillArgActions();
            PropbankInfoJPanel.loadShortcuts(this);
            
            if(extraInfoJPanel.getSPos2WPosMap() == null)
            {
                if(owner != null) {
                    ((JFrame) owner).setCursor(cursor);
                } else if(parentComponent != null) {
                    parentComponent.setCursor(cursor);
                }

                return;
            }

            extraInfoJPanel.setVisible(true);
        }
        else
        {
            // Filling sentence positions
            int senCount = ssfStory[0].countSentences();
            Vector pvec = new Vector(senCount);

            for(int i = 1; i <= senCount; i++) {
                pvec.add(Integer.toString(i));
            }

            positions = new DefaultComboBoxModel(pvec);
            positionJComboBox.setModel(positions);
        }

        selecTaskJComboBox.setSelectedIndex(taskNames.length - 1);

        setCurrentPosition(0, 0);
        
        ((JFrame) owner).setCursor(cursor);
    }
    
    @Override
    public void convertToXML(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        PrintStream ps = new PrintStream(f, charset);
        printXML(ps);
    }
    
    @Override
    public void printXML(PrintStream ps) {
    }
    
    @Override
    public String getXML(int pos) {
        return null;
    }

    @Override
    public void clear() {
    }

    public void configure(String pmPath, String charSet) {
    }

    @Override
    public PropertiesManager getPropertiesManager() {
        return propman;
    }

    @Override
    public PropertiesTable getTaskList() {
        return taskList;
    }

    @Override
    public void setTaskList(PropertiesTable tl) {
        taskList = tl;
    }

    @Override
    public String getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(String p) throws Exception {
        workspace = p;
    }

    @Override
    public void setWorkJPanel(JPanel wjp)
    {

    }
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
//        saveState(this);
        PropbankInfoJPanel.saveShortcuts();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
   }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel bottomJPanel;
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JComboBox compareLayoutJComboBox;
    public javax.swing.JLabel compareLayoutJLabel;
    public javax.swing.JPanel compareLayoutJPanel;
    public javax.swing.JPanel compareOptionsJPanel;
    public javax.swing.JPanel comparisonTableJPanel;
    public javax.swing.JPanel comparisonTreeJPanel;
    public javax.swing.JButton detailJButton;
    public javax.swing.JPanel detailJPanel;
    public javax.swing.JPanel diffTasksJPanel;
    public javax.swing.JButton firstJButton;
    public javax.swing.JButton lastJButton;
    public javax.swing.JSplitPane mainJSplitPane;
    public javax.swing.JPanel navigateJPanel;
    public javax.swing.JButton nextJButton;
    public javax.swing.JButton openJButton;
    public javax.swing.JButton openLeftJButton;
    public javax.swing.JComboBox positionJComboBox;
    public javax.swing.JLabel positionJLabel;
    public javax.swing.JPanel positionJPanel;
    public javax.swing.JPanel positionLeftJPanel;
    public javax.swing.JPanel positionRightJPanel;
    public javax.swing.JButton prevJButton;
    public javax.swing.JButton saveJButton;
    public javax.swing.JComboBox selecTaskJComboBox;
    public javax.swing.JLabel selecTaskJLabel;
    public javax.swing.JPanel senNumJPanel;
    public javax.swing.JPanel summaryJPanel;
    public javax.swing.JPanel treeJPanel;
    // End of variables declaration//GEN-END:variables
    
}
