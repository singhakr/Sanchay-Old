/*
 * SyntacticAnnotationWorkJPanel.java
 *
 * Created on October 9, 2005, 9:51 PM
 */

package sanchay.corpus.ssf.gui;

import sanchay.corpus.ssf.SSFSentence;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.*;
import sanchay.SanchayMainEvent;
import sanchay.GlobalProperties;
import sanchay.GlobalUtils;
import sanchay.SanchayMain;
import sanchay.common.SanchayClientsStateData;

import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertiesManager;
import sanchay.properties.PropertyTokens;
import sanchay.properties.PropertiesTable;
import sanchay.common.types.*;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.impl.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.gui.*;
import sanchay.gui.clients.*;
import sanchay.gui.common.DialogFactory;
import sanchay.gui.common.FileSelectionJPanel;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayLanguages;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.tree.*;
import sanchay.tree.gui.*;
import sanchay.util.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.corpus.ssf.query.SSFQuery;
import sanchay.corpus.validation.SyntacticAnnotationValidationJPanel;
import sanchay.gui.common.FileDisplayer;
import sanchay.gui.common.SanchayJDialog;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.crf.CRFAnnotationMain;
import sanchay.server.dto.tree.impl.RemoteFileNode;
import sanchay.table.gui.DisplayEvent;
import sanchay.table.gui.FindEvent;
import sanchay.table.gui.FindEventListener;
import sanchay.table.gui.SanchayActionTableCellEditor;
import sanchay.table.gui.SanchayDefaultJTable;
import sanchay.text.editor.gui.*;
import sanchay.text.enc.conv.EncodingConverterUtils;
import sanchay.util.file.FileMonitor;
import sanchay.util.file.SanchayBackup;
import sanchay.util.gui.SanchayGUIUtils;
import sanchay.util.query.SSFFindReplace;

/**
 *
 * @author  anil
 */
public class SyntacticAnnotationWorkJPanel extends javax.swing.JPanel
        implements WindowListener, WorkJPanelInterface, sanchay.gui.common.JPanelDialog,
        AnnotationClient, FindEventListener, FileDisplayer, NavigatetoValidateEventListener {

    protected ClientType clientType = ClientType.SYNTACTIC_ANNOTATION;

    protected CorpusType corpusType = CorpusType.RAW;

    protected static KeyValueProperties stateKVProps;

    protected String textFile;
    protected String charset = sanchay.GlobalProperties.getIntlString("UTF-8");

    protected String title = sanchay.GlobalProperties.getIntlString("Untitled");
       
    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;
    
    protected SyntacticAnnotationTaskSetupJPanel taskSetupJPanel;
    protected CRFAnnotationMain annotationMain;
    
    protected PropertiesManager propman;
    protected PropertiesTable taskList;
    
    protected String workspace;
    
    protected String taskName;
    protected KeyValueProperties kvTaskProps;

    protected SelectTaskJPanel selectTaskJPanel;
    
    protected boolean standAloneMode;
    
    protected SSFStory ssfStory;
//    private SSFStory ssfUTF8Story;
    
    protected PropertyTokens posTagsPT;
    protected KeyValueProperties morphTagsKVP;
    protected PropertyTokens phraseNamesPT;
    protected PropertyTokens commentsPT;
    
    protected DefaultComboBoxModel positions;
    protected DefaultComboBoxModel comments;
    
    protected int currentPosition;
    protected int currentWPosition;
    protected SSFSentence sentence;
    protected SSFNode node;
    protected String nodeID;
//    private SSFSentence utf8Sentence;
    protected String comment;
    
    protected String langEnc;
    
    protected boolean utf8Shown;
    protected boolean newComboBoxElementAdded;
    protected boolean dirty;

    protected File selFiles[];
    protected LinkedHashMap<File, SSFStory> selStories;
    
//    private SSFPhraseJPanel ssfPhraseJPanel;
    protected SanchayTreeJPanel ssfPhraseJPanel;
    protected SanchayTreeViewerJPanel viewTreeJPanel;

    protected SanchayBackup sanchayBackup;
    protected long backupPeriod = (long) 100000;

    protected SSFPhrase mmRoot;
    
    protected LinkedHashMap cfgToMMTreeMapping;

    protected String ssfQueryString = "";

    protected boolean propbankMode;
    private boolean alignmentMode;
    
    private boolean workRemote = false;
    
    protected PropbankInfoJPanel extraInfoJPanel;

    protected SyntacticAnnotationValidationJPanel validationJPanel;

    private SanchayRemoteWorkJPanel sanchayRemoteWorkJPanel = null;
    protected File rootLocalFile = null;
    protected RemoteFileNode rootRemoteFileNode = null;
    
    private boolean connected = false;
    

    /**
     * Validation
     */
    protected SyntacticAnnotationValidator syntacticAnnotationValidator;

    public String inputDirforValidation;
    public int currententrybeingvalidated;
    public String[][] validationErrorsCells;
    private int totaltobeValidated;

    public static int FIND_LEXDATA = 0;
    public static int FIND_TEXT = 1;
    public static int FIND_LABEL = 2;
    public static int FIND_ATTRIB = 3;
    
    public static int FIND_TEXT_PER_LABEL = 4;
    public static int FIND_TEXT_PER_ATTRIB = 5;
    public static int FIND_LABEL_PER_TEXT = 6;
    public static int FIND_ATTRIB_PER_TEXT = 7;
    public static int FIND_ATTRIB_PER_LABEL = 8;
    
    public static boolean REPLACE;
    
    /** Creates new form SyntacticAnnotationWorkJPanel */
    public SyntacticAnnotationWorkJPanel() {
        super();

        initComponents();
        
        uploadJButton.setVisible(false);

        loadState(this);
        
        langEnc = stateKVProps.getPropertyValue("LangEnc");
        
        parentComponent = this;
        
        ssfStory = new SSFStoryImpl();
//        ssfUTF8Story = new SSFStoryImpl();
        
        nodeID = "";
        node = new SSFNode();

        utf8Shown = true;
        newComboBoxElementAdded = false;
        dirty = false;

        commentJPanel.setVisible(false);

        extraInfoJPanel = new PropbankInfoJPanel(this, langEnc);
        jTreeViewJPanel.add(extraInfoJPanel, BorderLayout.EAST);
        extraInfoJPanel.setVisible(false);

        secondCommandsJPanel.setVisible(false);
        thirdCommandsJPanel.setVisible(false);
        fourthCommandsJPanel.setVisible(false);
        fourthCommandsJPanel.setVisible(false);

        setLangEnc(langEnc);
    }
    
    public SyntacticAnnotationWorkJPanel(KeyValueProperties kvTaskProps) {
        this();

        langEnc = kvTaskProps.getPropertyValue("Language");

        setLangEnc(langEnc);
        
        this.kvTaskProps = kvTaskProps;
    }

    public SyntacticAnnotationWorkJPanel(boolean propbankMode) {
        this();

        this.propbankMode = propbankMode;

        propbankModeJCheckBox.setEnabled(false);
    }

    public SyntacticAnnotationWorkJPanel(KeyValueProperties kvTaskProps, boolean propbankMode) {
        this(kvTaskProps);

        this.propbankMode = propbankMode;

        langEnc = kvTaskProps.getPropertyValue("Language");

        setLangEnc(langEnc);
   }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        workJPanel = new javax.swing.JPanel();
        topJPanel = new javax.swing.JPanel();
        jTreeViewJPanel = new javax.swing.JPanel();
        dndViewJPanel = new javax.swing.JPanel();
        dndViewJSplitPane = new javax.swing.JSplitPane();
        dndLeftJPanel = new javax.swing.JPanel();
        dndRightJPanel = new javax.swing.JPanel();
        commentJPanel = new javax.swing.JPanel();
        commentJLabel = new javax.swing.JLabel();
        commentJComboBox = new javax.swing.JComboBox();
        commentJScrollPane = new javax.swing.JScrollPane();
        commentJTextArea = new javax.swing.JTextArea();
        bottomJPanel = new javax.swing.JPanel();
        upperNavJPanel = new javax.swing.JPanel();
        senNumJPanel = new javax.swing.JPanel();
        positionJLabel = new javax.swing.JLabel();
        positionJComboBox = new javax.swing.JComboBox();
        textEditCheckBox = new javax.swing.JCheckBox();
        nestedFSJCheckBox = new javax.swing.JCheckBox();
        queryJPanel = new javax.swing.JPanel();
        queryJTextField = new javax.swing.JTextField();
        queryCommandsJPanel = new javax.swing.JPanel();
        queryJButton = new javax.swing.JButton();
        queryInFilesJButton = new javax.swing.JButton();
        buttonsJPanel = new javax.swing.JPanel();
        hideBFormJCheckBox = new javax.swing.JCheckBox();
        propbankModeJCheckBox = new javax.swing.JCheckBox();
        zoomInJButton = new javax.swing.JButton();
        zoomOutJButton = new javax.swing.JButton();
        openJButton = new javax.swing.JButton();
        workRemoteJCheckBox = new javax.swing.JCheckBox();
        videoNavJPanel = new javax.swing.JPanel();
        firstJButton = new javax.swing.JButton();
        prevJButton = new javax.swing.JButton();
        nextJButton = new javax.swing.JButton();
        lastJButton = new javax.swing.JButton();
        commandsJPanel = new javax.swing.JPanel();
        mainCommandsJPanel = new javax.swing.JPanel();
        resetJButton = new javax.swing.JButton();
        statJButton = new javax.swing.JButton();
        saveAsJButton = new javax.swing.JButton();
        saveJButton = new javax.swing.JButton();
        uploadJButton = new javax.swing.JButton();
        statInFilesJButton = new javax.swing.JButton();
        moreJButton = new javax.swing.JButton();
        secondCommandsJPanel = new javax.swing.JPanel();
        clearAllJButton = new javax.swing.JButton();
        clearJButton = new javax.swing.JButton();
        resetAllJButton = new javax.swing.JButton();
        editTextJButton = new javax.swing.JButton();
        sentenceJoinJButton = new javax.swing.JButton();
        sentenceSplitJButton = new javax.swing.JButton();
        thirdCommandsJPanel = new javax.swing.JPanel();
        findJButton = new javax.swing.JButton();
        replaceJButton = new javax.swing.JButton();
        findInFilesJButton = new javax.swing.JButton();
        replaceInFilesJButton = new javax.swing.JButton();
        replaceBatchJButton = new javax.swing.JButton();
        replaceBatchInFilesJButton = new javax.swing.JButton();
        fourthCommandsJPanel = new javax.swing.JPanel();
        joinFilesJButton = new javax.swing.JButton();
        transferTagsJButton = new javax.swing.JButton();
        setMorphTagsJButton = new javax.swing.JButton();
        convertEncodingJButton = new javax.swing.JButton();
        validationJButton = new javax.swing.JButton();
        validationPrevJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        workJPanel.setLayout(new java.awt.BorderLayout(0, 4));

        topJPanel.setLayout(new java.awt.CardLayout());

        jTreeViewJPanel.setLayout(new java.awt.BorderLayout());
        topJPanel.add(jTreeViewJPanel, "JTreeView");

        dndViewJPanel.setLayout(new java.awt.BorderLayout());

        dndLeftJPanel.setLayout(new java.awt.BorderLayout());
        dndViewJSplitPane.setLeftComponent(dndLeftJPanel);

        dndRightJPanel.setLayout(new java.awt.BorderLayout());
        dndViewJSplitPane.setRightComponent(dndRightJPanel);

        dndViewJPanel.add(dndViewJSplitPane, java.awt.BorderLayout.CENTER);

        topJPanel.add(dndViewJPanel, "DNDView");

        workJPanel.add(topJPanel, java.awt.BorderLayout.CENTER);

        commentJPanel.setLayout(new java.awt.BorderLayout());

        commentJLabel.setLabelFor(commentJTextArea);
        commentJLabel.setText("Comment: "); // NOI18N
        commentJPanel.add(commentJLabel, java.awt.BorderLayout.WEST);

        commentJComboBox.setEditable(true);
        commentJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commentJComboBoxActionPerformed(evt);
            }
        });
        commentJPanel.add(commentJComboBox, java.awt.BorderLayout.CENTER);

        commentJScrollPane.setPreferredSize(new java.awt.Dimension(120, 45));

        commentJTextArea.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        commentJTextArea.setLineWrap(true);
        commentJTextArea.setRows(1);
        commentJTextArea.setWrapStyleWord(true);
        commentJScrollPane.setViewportView(commentJTextArea);

        commentJPanel.add(commentJScrollPane, java.awt.BorderLayout.SOUTH);

        workJPanel.add(commentJPanel, java.awt.BorderLayout.SOUTH);

        add(workJPanel, java.awt.BorderLayout.CENTER);

        bottomJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bottomJPanel.setLayout(new java.awt.BorderLayout(0, 4));

        upperNavJPanel.setLayout(new java.awt.BorderLayout());

        senNumJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        positionJLabel.setLabelFor(positionJComboBox);
        positionJLabel.setText("Go to: "); // NOI18N
        positionJLabel.setToolTipText("Go to a particular sentence number");
        senNumJPanel.add(positionJLabel);

        positionJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionJComboBoxActionPerformed(evt);
            }
        });
        senNumJPanel.add(positionJComboBox);

        textEditCheckBox.setText("Edit Text"); // NOI18N
        textEditCheckBox.setToolTipText("Enable editing of the words");
        textEditCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        textEditCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textEditCheckBoxActionPerformed(evt);
            }
        });
        senNumJPanel.add(textEditCheckBox);

        nestedFSJCheckBox.setText("Nested FS"); // NOI18N
        nestedFSJCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        nestedFSJCheckBox.setEnabled(false);
        nestedFSJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nestedFSJCheckBoxActionPerformed(evt);
            }
        });
        senNumJPanel.add(nestedFSJCheckBox);

        upperNavJPanel.add(senNumJPanel, java.awt.BorderLayout.WEST);

        queryJPanel.setLayout(new java.awt.BorderLayout());

        queryJTextField.setToolTipText("Enter the query to search for data in the document");
        queryJTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        queryJPanel.add(queryJTextField, java.awt.BorderLayout.CENTER);

        queryCommandsJPanel.setLayout(new java.awt.GridLayout(1, 0));

        queryJButton.setText("Query");
        queryJButton.setToolTipText("Search the document with a query");
        queryJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryJButtonActionPerformed(evt);
            }
        });
        queryCommandsJPanel.add(queryJButton);

        queryInFilesJButton.setText("Query (Files)");
        queryInFilesJButton.setToolTipText("Search multiple documents with a query");
        queryInFilesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryInFilesJButtonActionPerformed(evt);
            }
        });
        queryCommandsJPanel.add(queryInFilesJButton);

        queryJPanel.add(queryCommandsJPanel, java.awt.BorderLayout.EAST);

        upperNavJPanel.add(queryJPanel, java.awt.BorderLayout.CENTER);

        hideBFormJCheckBox.setText("Hide BForm"); // NOI18N
        hideBFormJCheckBox.setToolTipText("Hide the sentence display in bracker form at the top"); // NOI18N
        hideBFormJCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        hideBFormJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideBFormJCheckBoxActionPerformed(evt);
            }
        });
        buttonsJPanel.add(hideBFormJCheckBox);

        propbankModeJCheckBox.setText("Propbank"); // NOI18N
        propbankModeJCheckBox.setToolTipText("Select this for Propbank annotation"); // NOI18N
        propbankModeJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propbankModeJCheckBoxActionPerformed(evt);
            }
        });
        buttonsJPanel.add(propbankModeJCheckBox);

        zoomInJButton.setText("+"); // NOI18N
        zoomInJButton.setToolTipText("Zoom In"); // NOI18N
        zoomInJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInJButtonActionPerformed(evt);
            }
        });
        buttonsJPanel.add(zoomInJButton);

        zoomOutJButton.setText("-"); // NOI18N
        zoomOutJButton.setToolTipText("Zoom out"); // NOI18N
        zoomOutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutJButtonActionPerformed(evt);
            }
        });
        buttonsJPanel.add(zoomOutJButton);

        openJButton.setText("Open"); // NOI18N
        openJButton.setToolTipText("Open a file"); // NOI18N
        openJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openJButtonActionPerformed(evt);
            }
        });
        buttonsJPanel.add(openJButton);

        workRemoteJCheckBox.setSelected(true);
        workRemoteJCheckBox.setText("Work Remote");
        workRemoteJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workRemoteJCheckBoxActionPerformed(evt);
            }
        });
        buttonsJPanel.add(workRemoteJCheckBox);

        upperNavJPanel.add(buttonsJPanel, java.awt.BorderLayout.EAST);

        bottomJPanel.add(upperNavJPanel, java.awt.BorderLayout.NORTH);

        videoNavJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        firstJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        firstJButton.setText("First"); // NOI18N
        firstJButton.setToolTipText("Go ot the first sentence"); // NOI18N
        firstJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstJButtonActionPerformed(evt);
            }
        });
        videoNavJPanel.add(firstJButton);

        prevJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        prevJButton.setText("Previous"); // NOI18N
        prevJButton.setToolTipText("Go to the previous sentence"); // NOI18N
        prevJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevJButtonActionPerformed(evt);
            }
        });
        videoNavJPanel.add(prevJButton);

        nextJButton.setText("Next"); // NOI18N
        nextJButton.setToolTipText("Go to the next sentence"); // NOI18N
        nextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextJButtonActionPerformed(evt);
            }
        });
        videoNavJPanel.add(nextJButton);

        lastJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        lastJButton.setText("Last"); // NOI18N
        lastJButton.setToolTipText("Go to the last sentence"); // NOI18N
        lastJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastJButtonActionPerformed(evt);
            }
        });
        videoNavJPanel.add(lastJButton);

        bottomJPanel.add(videoNavJPanel, java.awt.BorderLayout.CENTER);

        commandsJPanel.setLayout(new javax.swing.BoxLayout(commandsJPanel, javax.swing.BoxLayout.Y_AXIS));

        mainCommandsJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        resetJButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        resetJButton.setText("Reset"); // NOI18N
        resetJButton.setToolTipText("Reset this sentence to the state last saved"); // NOI18N
        resetJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetJButtonActionPerformed(evt);
            }
        });
        mainCommandsJPanel.add(resetJButton);

        statJButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        statJButton.setText("Statistics"); // NOI18N
        statJButton.setToolTipText("See the basic statistics about this file"); // NOI18N
        statJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statJButtonActionPerformed(evt);
            }
        });
        mainCommandsJPanel.add(statJButton);

        saveAsJButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveAsJButton.setText("Save As ..."); // NOI18N
        saveAsJButton.setToolTipText("Save this file with a different name"); // NOI18N
        saveAsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsJButtonActionPerformed(evt);
            }
        });
        mainCommandsJPanel.add(saveAsJButton);

        saveJButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveJButton.setText("Save"); // NOI18N
        saveJButton.setToolTipText("Save this file"); // NOI18N
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        mainCommandsJPanel.add(saveJButton);

        uploadJButton.setText("Upload");
        uploadJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadJButtonActionPerformed(evt);
            }
        });
        mainCommandsJPanel.add(uploadJButton);

        statInFilesJButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        statInFilesJButton.setText("Statistics (Files)"); // NOI18N
        statInFilesJButton.setToolTipText("See the basic statistics about many files"); // NOI18N
        statInFilesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statInFilesJButtonActionPerformed(evt);
            }
        });
        mainCommandsJPanel.add(statInFilesJButton);

        moreJButton.setText("More..."); // NOI18N
        moreJButton.setToolTipText("Show (or hide) more commands"); // NOI18N
        moreJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreJButtonActionPerformed(evt);
            }
        });
        mainCommandsJPanel.add(moreJButton);

        commandsJPanel.add(mainCommandsJPanel);

        secondCommandsJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        clearAllJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        clearAllJButton.setText("Clear All"); // NOI18N
        clearAllJButton.setToolTipText("Clear annotation from all the sentences"); // NOI18N
        clearAllJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllJButtonActionPerformed(evt);
            }
        });
        secondCommandsJPanel.add(clearAllJButton);

        clearJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        clearJButton.setText("Clear"); // NOI18N
        clearJButton.setToolTipText("Clear annotation from this sentence"); // NOI18N
        clearJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJButtonActionPerformed(evt);
            }
        });
        secondCommandsJPanel.add(clearJButton);

        resetAllJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        resetAllJButton.setText("Reset All"); // NOI18N
        resetAllJButton.setToolTipText("Reset all the sentence to the state last saved"); // NOI18N
        resetAllJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetAllJButtonActionPerformed(evt);
            }
        });
        secondCommandsJPanel.add(resetAllJButton);

        editTextJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        editTextJButton.setText("Edit SSF Text"); // NOI18N
        editTextJButton.setToolTipText("Allow editing of text being annotated"); // NOI18N
        editTextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTextJButtonActionPerformed(evt);
            }
        });
        secondCommandsJPanel.add(editTextJButton);

        sentenceJoinJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        sentenceJoinJButton.setText("Join Sentence"); // NOI18N
        sentenceJoinJButton.setToolTipText("Join this sentence with the next one"); // NOI18N
        sentenceJoinJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sentenceJoinJButtonActionPerformed(evt);
            }
        });
        secondCommandsJPanel.add(sentenceJoinJButton);

        sentenceSplitJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        sentenceSplitJButton.setText("Split Sentence"); // NOI18N
        sentenceSplitJButton.setToolTipText("Split this sentence into two from the selected node"); // NOI18N
        sentenceSplitJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sentenceSplitJButtonActionPerformed(evt);
            }
        });
        secondCommandsJPanel.add(sentenceSplitJButton);

        commandsJPanel.add(secondCommandsJPanel);

        thirdCommandsJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        findJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        findJButton.setText("Find"); // NOI18N
        findJButton.setToolTipText("Find in this file"); // NOI18N
        findJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findJButtonActionPerformed(evt);
            }
        });
        thirdCommandsJPanel.add(findJButton);

        replaceJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        replaceJButton.setText("Replace"); // NOI18N
        replaceJButton.setToolTipText("Replace in this file"); // NOI18N
        replaceJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceJButtonActionPerformed(evt);
            }
        });
        thirdCommandsJPanel.add(replaceJButton);

        findInFilesJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        findInFilesJButton.setText("Find (Files)"); // NOI18N
        findInFilesJButton.setToolTipText("Find in many files"); // NOI18N
        findInFilesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findInFilesJButtonActionPerformed(evt);
            }
        });
        thirdCommandsJPanel.add(findInFilesJButton);

        replaceInFilesJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        replaceInFilesJButton.setText("Replace (Files)"); // NOI18N
        replaceInFilesJButton.setToolTipText("Replace in many files"); // NOI18N
        replaceInFilesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceInFilesJButtonActionPerformed(evt);
            }
        });
        thirdCommandsJPanel.add(replaceInFilesJButton);

        replaceBatchJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        replaceBatchJButton.setText("Batch Replace"); // NOI18N
        replaceBatchJButton.setToolTipText("Multiple replacements in this file"); // NOI18N
        replaceBatchJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceBatchJButtonActionPerformed(evt);
            }
        });
        thirdCommandsJPanel.add(replaceBatchJButton);

        replaceBatchInFilesJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        replaceBatchInFilesJButton.setText("Batch Replace (Files)"); // NOI18N
        replaceBatchInFilesJButton.setToolTipText("Multiple replacements in many files"); // NOI18N
        replaceBatchInFilesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceBatchInFilesJButtonActionPerformed(evt);
            }
        });
        thirdCommandsJPanel.add(replaceBatchInFilesJButton);

        commandsJPanel.add(thirdCommandsJPanel);

        fourthCommandsJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        joinFilesJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        joinFilesJButton.setText("Join Files"); // NOI18N
        joinFilesJButton.setToolTipText("Join many SSF files to form one large file"); // NOI18N
        joinFilesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinFilesJButtonActionPerformed(evt);
            }
        });
        fourthCommandsJPanel.add(joinFilesJButton);

        transferTagsJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        transferTagsJButton.setText("Transfer Tags"); // NOI18N
        transferTagsJButton.setToolTipText("Transfer tags from source filed to target files"); // NOI18N
        transferTagsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transferTagsJButtonActionPerformed(evt);
            }
        });
        fourthCommandsJPanel.add(transferTagsJButton);

        setMorphTagsJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        setMorphTagsJButton.setText("Set Morph Tags"); // NOI18N
        setMorphTagsJButton.setToolTipText("Set morph tags for the whole file, based on the POS tags"); // NOI18N
        setMorphTagsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setMorphTagsJButtonActionPerformed(evt);
            }
        });
        fourthCommandsJPanel.add(setMorphTagsJButton);

        convertEncodingJButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        convertEncodingJButton.setText("Convert Encoding"); // NOI18N
        convertEncodingJButton.setToolTipText("Convert encoding of text"); // NOI18N
        convertEncodingJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convertEncodingJButtonActionPerformed(evt);
            }
        });
        fourthCommandsJPanel.add(convertEncodingJButton);

        validationJButton.setText("Validate");
        validationJButton.setToolTipText("Validate one by one (2)");
        validationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationJButtonActionPerformed(evt);
            }
        });
        fourthCommandsJPanel.add(validationJButton);

        validationPrevJButton.setText("Validation Prev");
        validationPrevJButton.setToolTipText("Validation");
        validationPrevJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationPrevJButtonActionPerformed(evt);
            }
        });
        fourthCommandsJPanel.add(validationPrevJButton);

        commandsJPanel.add(fourthCommandsJPanel);

        bottomJPanel.add(commandsJPanel, java.awt.BorderLayout.SOUTH);

        add(bottomJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    
    private void dndRightJPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dndRightJPanelMousePressed
// TODO add your handling code here:
    }//GEN-LAST:event_dndRightJPanelMousePressed

    private void propbankModeJCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_propbankModeJCheckBoxActionPerformed
    {//GEN-HEADEREND:event_propbankModeJCheckBoxActionPerformed
// TODO add your handling code here:
        if(propbankModeJCheckBox.isSelected())
        {
            ((SSFTreeCellRendererNew) ssfPhraseJPanel.getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.PROPBANK_ANNOTATION);

//            ssfPhraseJPanel.getJPopupMenu().get

            queryJButton.setEnabled(false);
            queryInFilesJButton.setEnabled(false);

            ssfPhraseJPanel.showControlTabs(false);

            propbankMode = true;

            extraInfoJPanel.initWordNavigationList();
            extraInfoJPanel.initTagNavigationList();
            initPropbank();

            extraInfoJPanel.setVisible(true);
        }
        else
        {
            ((SSFTreeCellRendererNew) ssfPhraseJPanel.getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);

            queryJButton.setEnabled(true);
            queryInFilesJButton.setEnabled(true);

            ssfPhraseJPanel.showControlTabs(true);

            propbankMode = false;

            resetAll(evt);

            extraInfoJPanel.setVisible(false);
        }
//        switchDNDView();
}//GEN-LAST:event_propbankModeJCheckBoxActionPerformed
    
    private void hideBFormJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideBFormJCheckBoxActionPerformed
// TODO add your handling code here:
        if(hideBFormJCheckBox.isSelected())
            ssfPhraseJPanel.showSentence(false);
        else
            ssfPhraseJPanel.showSentence(true);
    }//GEN-LAST:event_hideBFormJCheckBoxActionPerformed
    
    private void transferTagsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transferTagsJButtonActionPerformed
// TODO add your handling code here:
        transferTags(evt);
    }//GEN-LAST:event_transferTagsJButtonActionPerformed
    
    private void openJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openJButtonActionPerformed
// TODO add your handling code here:
        workRemote = workRemoteJCheckBox.isSelected();
        
        if(workRemote)
        {
            openRemoteFileMode();
        }
        else if(propbankMode)
        {
            if(GlobalProperties.getClientModes().getPropertyValue("PROPBANK_ANNOTATION").equals("FILE_MODE"))
                openFileMode();
            else if(GlobalProperties.getClientModes().getPropertyValue("PROPBANK_ANNOTATION").equals("TASK_MODE"))
                openTaskMode();
        }
        else
        {
            if(GlobalProperties.getClientModes().getPropertyValue("SYNTACTIC_ANNOTATION").equals("FILE_MODE"))
                openFileMode();
            else if(GlobalProperties.getClientModes().getPropertyValue("SYNTACTIC_ANNOTATION").equals("TASK_MODE"))
                openTaskMode();
        }
    }//GEN-LAST:event_openJButtonActionPerformed
    
    private void joinFilesJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinFilesJButtonActionPerformed
// TODO add your handling code here:
        joinFiles(evt);
    }//GEN-LAST:event_joinFilesJButtonActionPerformed
    
    private void statInFilesJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statInFilesJButtonActionPerformed
// TODO add your handling code here:
        storeCurrentPosition();
        showStatisticsInFiles(evt);
    }//GEN-LAST:event_statInFilesJButtonActionPerformed
    
    private void statJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statJButtonActionPerformed
// TODO add your handling code here:
        storeCurrentPosition();
        showStatistics(evt);
    }//GEN-LAST:event_statJButtonActionPerformed
                                                                            
    private void replaceInFilesJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_replaceInFilesJButtonActionPerformed
    {//GEN-HEADEREND:event_replaceInFilesJButtonActionPerformed
// TODO add your handling code here:
        replace(evt, true);
}//GEN-LAST:event_replaceInFilesJButtonActionPerformed
        
    private void findInFilesJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_findInFilesJButtonActionPerformed
    {//GEN-HEADEREND:event_findInFilesJButtonActionPerformed
// TODO add your handling code here:
        find(evt, true);
}//GEN-LAST:event_findInFilesJButtonActionPerformed
                        
    private void replaceJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_replaceJButtonActionPerformed
    {//GEN-HEADEREND:event_replaceJButtonActionPerformed
// TODO add your handling code here:
        replace(evt, false);
}//GEN-LAST:event_replaceJButtonActionPerformed
            
    private void findJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_findJButtonActionPerformed
    {//GEN-HEADEREND:event_findJButtonActionPerformed
// TODO add your handling code here:
        find(evt, false);
}//GEN-LAST:event_findJButtonActionPerformed
    
    private void zoomOutJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoomOutJButtonActionPerformed
    {//GEN-HEADEREND:event_zoomOutJButtonActionPerformed
// TODO add your handling code here:
//	SanchayLanguages.decreaseFontSize();
        ssfPhraseJPanel.decreaseFontSizes();
        setVisible(false);
        setVisible(true);
        SanchayMutableTreeNode root = (SanchayMutableTreeNode) ssfPhraseJPanel.getJTree().getModel().getRoot();
        ((SanchayTreeModel) ssfPhraseJPanel.getJTree().getModel()).nodeChanged(root);
        ssfPhraseJPanel.expandAll(null);
//	ssfPhraseJPanel.getJTree().repaint();
//	ssfPhraseJPanel.getJTree().setVisible(false);
//	ssfPhraseJPanel.getJTree().setVisible(true);
    }//GEN-LAST:event_zoomOutJButtonActionPerformed
    
    private void zoomInJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoomInJButtonActionPerformed
    {//GEN-HEADEREND:event_zoomInJButtonActionPerformed
// TODO add your handling code here:
//	SanchayLanguages.increaseFontSize();
        ssfPhraseJPanel.increaseFontSizes();
        setVisible(false);
        setVisible(true);
        SanchayMutableTreeNode root = (SanchayMutableTreeNode) ssfPhraseJPanel.getJTree().getModel().getRoot();
        ((SanchayTreeModel) ssfPhraseJPanel.getJTree().getModel()).nodeStructureChanged(root);
        ssfPhraseJPanel.expandAll(null);
//	ssfPhraseJPanel.getJTree().repaint();
//	ssfPhraseJPanel.getJTree().setVisible(false);
//	ssfPhraseJPanel.getJTree().setVisible(true);
    }//GEN-LAST:event_zoomInJButtonActionPerformed
    
    private void sentenceSplitJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sentenceSplitJButtonActionPerformed
    {//GEN-HEADEREND:event_sentenceSplitJButtonActionPerformed
// TODO add your handling code here:
        splitSentence(evt);
    }//GEN-LAST:event_sentenceSplitJButtonActionPerformed
    
    private void sentenceJoinJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sentenceJoinJButtonActionPerformed
    {//GEN-HEADEREND:event_sentenceJoinJButtonActionPerformed
// TODO add your handling code here:
        joinSentence(evt);
    }//GEN-LAST:event_sentenceJoinJButtonActionPerformed
    
    private void clearAllJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllJButtonActionPerformed
// TODO add your handling code here:
        clearAll(evt);
    }//GEN-LAST:event_clearAllJButtonActionPerformed
    
    private void editTextJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTextJButtonActionPerformed
// TODO add your handling code here:
        editSSFText(evt);
    }//GEN-LAST:event_editTextJButtonActionPerformed
    
    private void textEditCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_textEditCheckBoxActionPerformed
    {//GEN-HEADEREND:event_textEditCheckBoxActionPerformed
// TODO add your handling code here:
        if(textEditCheckBox.isSelected())
            ssfPhraseJPanel.setNodeTextEditable(true);
//	    ssfPhraseJPanel.getJTree().setEditable(true);
        else
            ssfPhraseJPanel.setNodeTextEditable(false);
//	    ssfPhraseJPanel.getJTree().setEditable(false);
    }//GEN-LAST:event_textEditCheckBoxActionPerformed
    
    private void moreJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moreJButtonActionPerformed
    {//GEN-HEADEREND:event_moreJButtonActionPerformed
// TODO add your handling code here:
        showMoreButtons(evt);
    }//GEN-LAST:event_moreJButtonActionPerformed
    
    private void saveAsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsJButtonActionPerformed
// TODO add your handling code here:
        saveAs(evt);
    }//GEN-LAST:event_saveAsJButtonActionPerformed
    
    private void nestedFSJCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nestedFSJCheckBoxActionPerformed
    {//GEN-HEADEREND:event_nestedFSJCheckBoxActionPerformed
// TODO add your handling code here:
        if(sentence.getRoot().allowsNestedFS() == true)
            sentence.getRoot().allowNestedFS(false);
        else
            sentence.getRoot().allowNestedFS(true);
        
        ssfPhraseJPanel.editTreeNode(null);
    }//GEN-LAST:event_nestedFSJCheckBoxActionPerformed
    
    private void commentJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commentJComboBoxActionPerformed
// TODO add your handling code here:
        if(newComboBoxElementAdded == true) {
            newComboBoxElementAdded = false;
            return;
        }
        
        String selItem = (String) commentJComboBox.getSelectedItem();
        if(UtilityFunctions.addItemToJCoboBox(commentJComboBox, selItem) == true)
            newComboBoxElementAdded = true;
        
        String cmt = commentJTextArea.getText();
        if(cmt.equals("") == true)
            commentJTextArea.setText(selItem);
        else
            commentJTextArea.setText(cmt + ".\n" + selItem);
    }//GEN-LAST:event_commentJComboBoxActionPerformed
    
    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJButtonActionPerformed
// TODO add your handling code here:        
        if(workRemote && connected)
        {
            uploadJButtonActionPerformed(null);
        }
        else
        {
            save(evt);            
        }
    }//GEN-LAST:event_saveJButtonActionPerformed
    
    private void resetAllJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAllJButtonActionPerformed
// TODO add your handling code here:
        resetAll(evt);
    }//GEN-LAST:event_resetAllJButtonActionPerformed
    
    private void clearJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearJButtonActionPerformed
// TODO add your handling code here:
        clear(evt);
    }//GEN-LAST:event_clearJButtonActionPerformed
    
    private void resetJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetJButtonActionPerformed
// TODO add your handling code here:
        reset(evt);
    }//GEN-LAST:event_resetJButtonActionPerformed
    
    private void lastJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastJButtonActionPerformed
// TODO add your handling code here:
        if(propbankMode)
            setCurrentPosition(ssfStory.countSentences() - 1, extraInfoJPanel.getWPos2SPosMap().countProperties() - 1);
        else
            setCurrentPosition(ssfStory.countSentences() - 1, -1);
    }//GEN-LAST:event_lastJButtonActionPerformed
    
    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextJButtonActionPerformed
// TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        if(Integer.parseInt(pos) == positions.getSize())
            return;

        int cp = 0;
        int cwp = -1;

        try {
            if(propbankMode)
            {
                cwp = Integer.parseInt(pos);
                cp = Integer.parseInt(extraInfoJPanel.getWPos2SPosMap().getPropertyValue(pos));
            }
            else
                cp = Integer.parseInt(pos);
            
            setCurrentPosition(cp, cwp);
        } catch(NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
    }//GEN-LAST:event_nextJButtonActionPerformed
    
    private void prevJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevJButtonActionPerformed
// TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        if(Integer.parseInt(pos) == 1)
            return;

        int cp = 0;
        int cwp = -1;

        try {
            if(propbankMode)
            {
                cwp = Integer.parseInt(pos);
                cp = Integer.parseInt(extraInfoJPanel.getWPos2SPosMap().getPropertyValue(pos));
            }
            else
                cp = Integer.parseInt(pos);

            setCurrentPosition(cp - 2, cwp - 2);
        } catch(NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
    }//GEN-LAST:event_prevJButtonActionPerformed
    
    private void firstJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstJButtonActionPerformed
// TODO add your handling code here:
        setCurrentPosition(0, 0);
    }//GEN-LAST:event_firstJButtonActionPerformed
    
    private void positionJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionJComboBoxActionPerformed
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
            else
                cp = Integer.parseInt(pos);

            setCurrentPosition(cp - 1, cwp - 1);
        } catch(NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
    }//GEN-LAST:event_positionJComboBoxActionPerformed

    private void setMorphTagsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setMorphTagsJButtonActionPerformed
        // TODO add your handling code here:
        if(ssfStory != null)
        {
            ssfStory.setMorphTags(morphTagsKVP);
            resetCurrentPosition();
            ssfPhraseJPanel.setVisible(false);
            ssfPhraseJPanel.setVisible(true);
        }
}//GEN-LAST:event_setMorphTagsJButtonActionPerformed

    private void replaceBatchJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_replaceBatchJButtonActionPerformed
    {//GEN-HEADEREND:event_replaceBatchJButtonActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_replaceBatchJButtonActionPerformed

    private void replaceBatchInFilesJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_replaceBatchInFilesJButtonActionPerformed
    {//GEN-HEADEREND:event_replaceBatchInFilesJButtonActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_replaceBatchInFilesJButtonActionPerformed

    private void queryJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_queryJButtonActionPerformed
    {//GEN-HEADEREND:event_queryJButtonActionPerformed
        // TODO add your handling code here:
        query(evt, false);
    }//GEN-LAST:event_queryJButtonActionPerformed

    private void queryInFilesJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_queryInFilesJButtonActionPerformed
    {//GEN-HEADEREND:event_queryInFilesJButtonActionPerformed
        // TODO add your handling code here:
        query(evt, true);
}//GEN-LAST:event_queryInFilesJButtonActionPerformed

    private void convertEncodingJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertEncodingJButtonActionPerformed
        // TODO add your handling code here:
        convertEncoding(evt);
    }//GEN-LAST:event_convertEncodingJButtonActionPerformed

    private void validationPrevJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validationPrevJButtonActionPerformed
        // TODO add your handling code here:
        JDialog taskDialog = new JDialog(this.owner, "Choose Input Directory/File", true);

        ValidationToolJPanel validationToolJPanel = new ValidationToolJPanel();

     //   validationToolJPanel.setOwner(this.owner);
        validationToolJPanel.setDialog(taskDialog);
        taskDialog.add(validationToolJPanel);
        taskDialog.setBounds(480, 250, 400, 100);
        taskDialog.setVisible(true);


        String inputDirectoryforValidation = validationToolJPanel.getInputDirectory();
        inputDirforValidation = inputDirectoryforValidation;
        if(!inputDirectoryforValidation.equals(""))
        {
        try {
//String cmd = "./run-validation_checks.sh " + inputDirectoryforValidation; // this is the command to execute in the Unix shell
            String cmd = "./validation_tool/val-prev/run-validation_checks.sh ./validation_tool/val-prev " + inputDirectoryforValidation; // this is the command to execute in the Unix shell
            System.out.println(cmd);
            // create a process for the shell
//            String inpsplit[] = inputDirectoryforValidation.split(" ");
//            int inpsplitcount = 1;
//            if(inpsplit.length>0){
//                inputDirectoryforValidation= inpsplit[0];
//                while(inpsplitcount<inpsplit.length)
//                {
//                    inputDirectoryforValidation = inputDirectoryforValidation+"\\ " + inpsplit[inpsplitcount];
//                    inpsplitcount++;
//                }
//            }
//            System.out.println("PATH:"+inputDirectoryforValidation);
            ProcessBuilder pb = new ProcessBuilder("bash","-c",cmd);
            pb.redirectErrorStream(true); // use this to capture messages sent to stderr
            Process shell = pb.start();
            InputStream shellIn = shell.getInputStream(); // this captures the output from the command
            // at this point you can process the output issued by the command
            // for instance, this reads the output and writes it to System.out:
            int ti;
            char tic ;
            while((ti=shellIn.read())!=-1)
            {
                tic = (char) ti;
            System.err.print(tic);

            }
                try {
                    // this captures the output from the command
                    int shellExitStatus = shell.waitFor(); // wait for the shell to finish and get the return code
                } catch (InterruptedException ex) {
                    Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            int c;
            while ((c = shellIn.read()) != -1) {System.out.write(c);}
            // close the stream
            try {shellIn.close();} catch (IOException ignoreMe) {}
        } catch (IOException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        String text="";
        try {
            text = UtilityFunctions.getTextFromFile("./validation_tool/val-prev/Annotation_Errors.txt", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        String lines[] = text.split("\n");

        //System.out.println(lines.length);
        String cells[][] = new String[lines.length][4];
        for (int i = 0; i < lines.length; i++) {
            String split[] = lines[i].split("\t");
            cells[i][0] = split[0];
            cells[i][1] = split[1];
            cells[i][2] = split[2];
            cells[i][3] = split[3];
            //System.out.println(cells[i][0]+ " " + cells[i][1] + " " + cells[i][2]);
            }

        SanchayTableModel tablemodel = new SanchayTableModel(cells, new String[]{"(1)", "(2)", "(3)", "Comments"});
        showValidationResults(tablemodel, "hin::utf8", 1);
        }
    }//GEN-LAST:event_validationPrevJButtonActionPerformed

    private void PREVvalidationOneByOneJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:

        JDialog taskDialog = new JDialog(this.owner, "Choose Input Directory/File", true);

        ValidationToolJPanel validationToolJPanel = new ValidationToolJPanel();
     //   validationToolJPanel.setOwner(this.owner);
        validationToolJPanel.setDialog(taskDialog);
        taskDialog.add(validationToolJPanel);
        taskDialog.setBounds(480, 250, 400, 100);
        taskDialog.setVisible(true);

        String inputDirectoryforValidation = validationToolJPanel.getInputDirectory();
        inputDirforValidation = inputDirectoryforValidation;

        if(!inputDirectoryforValidation.equals(""))
        {

        try {
//String cmd = "./run-validation_checks.sh " + inputDirectoryforValidation; // this is the command to execute in the Unix shell
            String cmd = "./validation_tool/run-validation_checks.sh ./validation_tool " + inputDirectoryforValidation; // this is the command to execute in the Unix shell
            System.out.println(cmd);
            // create a process for the shell
//            String inpsplit[] = inputDirectoryforValidation.split(" ");
//            int inpsplitcount = 1;
//            if(inpsplit.length>0){
//                inputDirectoryforValidation= inpsplit[0];
//                while(inpsplitcount<inpsplit.length)
//                {
//                    inputDirectoryforValidation = inputDirectoryforValidation+"\\ " + inpsplit[inpsplitcount];
//                    inpsplitcount++;
//                }
//            }
//            System.out.println("PATH:"+inputDirectoryforValidation);
            ProcessBuilder pb = new ProcessBuilder("bash","-c",cmd);
            pb.redirectErrorStream(true); // use this to capture messages sent to stderr
            Process shell = pb.start();
            InputStream shellIn = shell.getInputStream(); // this captures the output from the command
            // at this point you can process the output issued by the command
            // for instance, this reads the output and writes it to System.out:
            int ti;
            char tic ;
            while((ti=shellIn.read())!=-1)
            {
                tic = (char) ti;
            System.err.print(tic);

            }
                try {
                    // this captures the output from the command
                    int shellExitStatus = shell.waitFor(); // wait for the shell to finish and get the return code
                } catch (InterruptedException ex) {
                    Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            int c;
            while ((c = shellIn.read()) != -1) {System.out.write(c);}
            // close the stream
            try {shellIn.close();} catch (IOException ignoreMe) {}
        } catch (IOException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        String text=null;
        try {
            text = UtilityFunctions.getTextFromFile("./validation_tool/Annotation_Errors.txt", "UTF-8");
            System.out.println("HEEH"+text);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(text!=null && !text.equals("")){
            String lines[] = text.split("\n");
            int tobeValidated = lines.length;

            if(tobeValidated==0){
            return;
            }
            currententrybeingvalidated = 0;
            totaltobeValidated = lines.length;
            //System.out.println(lines.length);
            validationErrorsCells = new String[lines.length][4];
            for (int i = 0; i < lines.length; i++) {
                String split[] = lines[i].split("\t");
                if(split.length==4){
                validationErrorsCells[i][0] = split[0];
                validationErrorsCells[i][1] = split[1];
                validationErrorsCells[i][2] = split[2];
                validationErrorsCells[i][3] = split[3];
                }
            }

            String split[] = validationErrorsCells[0][2].toString().split("::");
            String fileID="",sentenceId="",nodeID="";

            if (split.length == 3) {

                    fileID = split[split.length - 1];
                    sentenceId = split[split.length - 2];
                    nodeID = split[0];

            }

            File selFile = new File(inputDirforValidation + "/" + fileID);
            String path = inputDirforValidation + "/" + fileID;
            File cfile = new File(path);

            if (cfile.exists() == false) {
                return;
            }

            KeyValueProperties kvTaskProps1 = new KeyValueProperties();
            String taskName = cfile.getName();

            String taskPropFile = "task-" + taskName;
            File tpfile = new File(cfile.getParent(), taskPropFile);
            taskPropFile = tpfile.getAbsolutePath();

            kvTaskProps1.addProperty("Language", langEnc);
            kvTaskProps1.addProperty("TaskName", taskName);
            kvTaskProps1.addProperty("TaskPropFile", "task-" + taskName);
            kvTaskProps1.addProperty("TaskPropCharset", "UTF-8");
            kvTaskProps1.addProperty("SSFPropFile", "props/ssf-props.txt");
            kvTaskProps1.addProperty("SSFPropCharset", "UTF-8");
            kvTaskProps1.addProperty("FSPropFile", "props/fs-props.txt");
            kvTaskProps1.addProperty("FSPropCharset", "UTF-8");
            kvTaskProps1.addProperty("MFeaturesFile", "props/fs-mandatory-attribs.txt");
            kvTaskProps1.addProperty("MFeaturesCharset", "UTF-8");
            kvTaskProps1.addProperty("OFeaturesFile", "props/fs-other-attribs.txt");
            kvTaskProps1.addProperty("PAttributesFile", "props/ps-attribs.txt");
            kvTaskProps1.addProperty("DAttributesFile", "props/dep-attribs.txt");
            kvTaskProps1.addProperty("SAttributesFile", "props/sem-attribs.txt");
            kvTaskProps1.addProperty("SSFCorpusStoryFile", selFile.getAbsolutePath());
            kvTaskProps1.addProperty("SSFCorpusCharset", charset);
            kvTaskProps1.addProperty("SSFCorpusStoryUTF8File", selFile.getAbsolutePath());

            kvTaskProps1.addProperty("POSTagsFile", "workspace/syn-annotation/pos-tags.txt");
            kvTaskProps1.addProperty("MorphTagsFile", "workspace/syn-annotation/morph-tags.txt");
            kvTaskProps1.addProperty("POSTagsCharset", "UTF-8");

            kvTaskProps1.addProperty("PhraseNamesFile", "workspace/syn-annotation/phrase-names.txt");
            kvTaskProps1.addProperty("PhraseNamesCharset", "UTF-8");

            kvTaskProps1.addProperty("CurrentPosition", sentenceId);

            SyntacticAnnotationWorkJPanel navigatedPanel;

            if (this.owner instanceof sanchay.SanchayMain) {

    //            navigatedPanel = new SyntacticAnnotationWorkJPanel(kvTaskProps1);
    //            navigatedPanel.setOwner(this.owner);
    //            ((SanchayMain) this.owner).addApplicationToTabbedPane(navigatedPanel, "Syntactic Annotation", false);
    //            navigatedPanel.configure();
    //            navigatedPanel.setCurrentPosition(Integer.parseInt(sentenceId)-1, 0);

                this.kvTaskProps = kvTaskProps1;
                this.configure();
                this.setCurrentPosition(Integer.parseInt(sentenceId)-1, 0);

                List<SSFNode> nodes = this.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
                SSFPhrase tempRoot = this.sentence.getRoot();

                searchinFSandhighlight(tempRoot, nodeID, this);
            }
            else{

                navigatedPanel = this;
                this.kvTaskProps = kvTaskProps1;

                this.setTaskName(kvTaskProps1.getPropertyValue("TaskName"));
                this.configure();

                List<SSFNode> nodes = this.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
                SSFPhrase tempRoot = this.sentence.getRoot();

                searchinFSandhighlight(tempRoot, nodeID, this);
            }

            this.ssfPhraseJPanel.treeJTree.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent evt) {
                      NavigateToNextInValidation(evt);

                }
            });
            }
        } 
    }
    
    private SanchayTableModel getValTableModel() {

        String curDir = stateKVProps.getPropertyValue("CurrentDir");

        if(textFile != null && !textFile.equals("") && !textFile.equals(sanchay.GlobalProperties.getIntlString("Untitled"))) {
            File tfile = new File(textFile);

            if(tfile.exists()) {
                curDir = tfile.getParentFile().getAbsolutePath();
            }
        }
/*
        validationJPanel = new SyntacticAnnotationValidationJPanel(this);
        validationJPanel.setOwner(owner);
        validationJPanel.setVisible(true);
        
        //SanchayJDialog openDialog = new SanchayJDialog(owner, "Validation Tool", true, (JPanelDialog) validationJPanel);
*/
        JDialog validationDialog = new JDialog(this.owner, "Validation Tool", true);

        validationJPanel = new SyntacticAnnotationValidationJPanel();

     //   validationToolJPanel.setOwner(this.owner);
        validationJPanel.setDialog(validationDialog);
        validationDialog.add(validationJPanel);
        //taskDialog.setBounds(validationJPanel.getBounds());
        validationDialog.pack();
        UtilityFunctions.centre(validationDialog);
        validationDialog.setVisible(true);

        selStories = validationJPanel.getSelFilesMap();
        System.out.println(selStories);
        
        return validationJPanel.getTableModel();
        
        /*
        SanchayJDialog fsDialog = (SanchayJDialog) DialogFactory.showFileSelectionDialog(owner, sanchay.GlobalProperties.getIntlString("FileSelectionJPanel"), true, new File(curDir));

        FileSelectionJPanel fsPanel = (FileSelectionJPanel) fsDialog.getJPanel();

        return fsPanel.getSelectedFiles();
        
        taskSetupJPanel = new SyntacticAnnotationTaskSetupJPanel(true, this);
        taskSetupJPanel.setOwner(owner);

        SanchayJDialog openDialog = new SanchayJDialog(owner, sanchay.GlobalProperties.getIntlString("Sanchay_Syntactic_Annotation_Task_Setup"), true, (JPanelDialog) taskSetupJPanel);
        openDialog.pack();

        UtilityFunctions.centre(openDialog);

        openDialog.setVisible(true);

         * */

        
    }

    private void validateFiles()
    {
        storeCurrentPosition();

        //SanchayTableModel matches = syntacticAnnotationValidator.validate(ssfStory, validationOption);
        SanchayTableModel matches = getValTableModel();

        displayCurrentPosition();

        if(matches == null)
        {
            JOptionPane.showMessageDialog(this, "No errrors found.", "Validation Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        //matches.trimRows(false);
//        if(matches.getColumnCount()==6)
//            matches.setColumnIdentifiers(new String[]{"Sentence", "Matched Node", "Context", "Referred Node", "File", "Comment"});

        if(matches.getColumnCount()==6)
            matches.setColumnIdentifiers(new String[]{"Rule", "Matched Node", "Context", "Referred Node", "File", "Comment"});

        matches.insertRow(0);
        matches.setValueAt("Matches:", 0, 0);

        if(matches.getRowCount() > 1)
            matches.insertRow(1);

        showValSearchResults(matches, langEnc, 1);
    }

    private void validationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validationJButtonActionPerformed
        // TODO add your handling code here:
        validateFiles();
    }//GEN-LAST:event_validationJButtonActionPerformed

    private void workRemoteJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workRemoteJCheckBoxActionPerformed
        // TODO add your handling code here:
        workRemote = workRemoteJCheckBox.isSelected();
    }//GEN-LAST:event_workRemoteJCheckBoxActionPerformed

    private void uploadJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadJButtonActionPerformed
        // TODO add your handling code here:
        save(evt);

        String localPath = kvTaskProps.getPropertyValue("SSFCorpusStoryFile");
        
        sanchayRemoteWorkJPanel.uploadFile(localPath, evt);
    }//GEN-LAST:event_uploadJButtonActionPerformed

    private void showValidationResults(SanchayTableModel matches,String langEnc, int tcount) {

//        if(matches == null) {
//            JOptionPane.showMessageDialog(this, "No match found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }

        int rcount = matches.getRowCount();

//        if(rcount == 0) {
//            JOptionPane.showMessageDialog(this, "No match found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }

        matches.insertRow(0);
        matches.insertRow(0);

        matches.setValueAt("Total : ", 0, 0);
        matches.setValueAt(Integer.toString(tcount), 0, 1);

        SanchayJDialog check = DialogFactory.showTableDialog(dialog, "Validation Results @ " + inputDirforValidation, false, matches, langEnc, SanchayTableJPanel.DEFAULT_MODE);

        SanchayTableJPanel tableJpanel = (SanchayTableJPanel) check.getJPanel();

        SanchayActionTableCellEditor editor = new SanchayActionTableCellEditor(this, langEnc, "Sentence", SanchayActionTableCellEditor.VALIDATION_MODE);
        tableJpanel.getJTable().setDefaultEditor(String.class, editor);
        ((SanchayDefaultJTable) tableJpanel.getJTable()).addEventListener(this);
    }


    public void NavigateToNextInValidation(java.awt.event.KeyEvent evt)
    {
        if(evt.getKeyChar()=='p')
        {
            System.out.println("Yeah");

            currententrybeingvalidated = currententrybeingvalidated+1;

            if(currententrybeingvalidated==totaltobeValidated)
            {
                //ALERT!!??
            }
            String split[] = validationErrorsCells[currententrybeingvalidated][2].toString().split("::");
            String fileID="",sentenceId="",nodeID="";


            System.out.println(currententrybeingvalidated);
            if (split.length == 3) {

                fileID = split[split.length - 1];
                sentenceId = split[split.length - 2];
                nodeID = split[0];

        }

        System.out.println(nodeID + " " + sentenceId + " " + fileID);
        File selFile = new File(inputDirforValidation + "/" + fileID);
        String path = inputDirforValidation + "/" + fileID;
        File cfile = new File(path);

        if (cfile.exists() == false) {
            return;
        }

        KeyValueProperties kvTaskProps1 = new KeyValueProperties();
        String taskName = cfile.getName();

        String taskPropFile = "task-" + taskName;
        File tpfile = new File(cfile.getParent(), taskPropFile);
        taskPropFile = tpfile.getAbsolutePath();

        kvTaskProps1.addProperty("Language", langEnc);
        kvTaskProps1.addProperty("TaskName", taskName);
        kvTaskProps1.addProperty("TaskPropFile", "task-" + taskName);
        kvTaskProps1.addProperty("TaskPropCharset", "UTF-8");
        kvTaskProps1.addProperty("SSFPropFile", "props/ssf-props.txt");
        kvTaskProps1.addProperty("SSFPropCharset", "UTF-8");
        kvTaskProps1.addProperty("FSPropFile", "props/fs-props.txt");
        kvTaskProps1.addProperty("FSPropCharset", "UTF-8");
        kvTaskProps1.addProperty("MFeaturesFile", "props/fs-mandatory-attribs.txt");
        kvTaskProps1.addProperty("MFeaturesCharset", "UTF-8");
        kvTaskProps1.addProperty("OFeaturesFile", "props/fs-other-attribs.txt");
        kvTaskProps1.addProperty("PAttributesFile", "props/ps-attribs.txt");
        kvTaskProps1.addProperty("DAttributesFile", "props/dep-attribs.txt");
        kvTaskProps1.addProperty("SAttributesFile", "props/sem-attribs.txt");
        kvTaskProps1.addProperty("SSFCorpusStoryFile", selFile.getAbsolutePath());
        kvTaskProps1.addProperty("SSFCorpusCharset", charset);
        kvTaskProps1.addProperty("SSFCorpusStoryUTF8File", selFile.getAbsolutePath());

        kvTaskProps1.addProperty("POSTagsFile", "workspace/syn-annotation/pos-tags.txt");
        kvTaskProps1.addProperty("MorphTagsFile", "workspace/syn-annotation/morph-tags.txt");
        kvTaskProps1.addProperty("POSTagsCharset", "UTF-8");

        kvTaskProps1.addProperty("PhraseNamesFile", "workspace/syn-annotation/phrase-names.txt");
        kvTaskProps1.addProperty("PhraseNamesCharset", "UTF-8");

        kvTaskProps1.addProperty("CurrentPosition", sentenceId);

        SyntacticAnnotationWorkJPanel navigatedPanel;

        if (this.owner instanceof sanchay.SanchayMain) {

            this.kvTaskProps = kvTaskProps1;
            this.configure();
            this.setCurrentPosition(Integer.parseInt(sentenceId)-1, 0);

            List<SSFNode> nodes = this.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
            SSFPhrase tempRoot = this.sentence.getRoot();

            searchinFSandhighlight(tempRoot, nodeID, this);
        }
        else{

            navigatedPanel = this;
            this.kvTaskProps = kvTaskProps1;

            this.setTaskName(kvTaskProps1.getPropertyValue("TaskName"));
            this.configure();

            List<SSFNode> nodes = this.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
            SSFPhrase tempRoot = this.sentence.getRoot();

            searchinFSandhighlight(tempRoot, nodeID, this);

        }
        this.ssfPhraseJPanel.treeJTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                  NavigateToNextInValidation(evt);

            }
        });

        }


    }

     public void NavigateToNextInValidationSimple(java.awt.event.KeyEvent evt)
    {
        if(evt.getKeyChar()=='p')
        {
            System.out.println("Yeah");

            currententrybeingvalidated = currententrybeingvalidated+1;

            if(currententrybeingvalidated==totaltobeValidated)
            {
                //ALERT!!??
            }
            String split[] = validationErrorsCells[currententrybeingvalidated][2].toString().split("::");
            String fileID="",sentenceId="",nodeID="";


            System.out.println(currententrybeingvalidated);
            if (split.length == 3) {

                fileID = split[split.length - 1];
                sentenceId = split[split.length - 2];
                nodeID = split[0];

        }
           System.out.println(nodeID + " " + sentenceId + " " + fileID);
        File selFile = new File(inputDirforValidation + "/" + fileID);
        String path = inputDirforValidation + "/" + fileID;
        File cfile = new File(path);

        if (cfile.exists() == false) {
            return;
        }

        KeyValueProperties kvTaskProps1 = new KeyValueProperties();
        String taskName = cfile.getName();

        String taskPropFile = "task-" + taskName;
        File tpfile = new File(cfile.getParent(), taskPropFile);
        taskPropFile = tpfile.getAbsolutePath();

        kvTaskProps1.addProperty("Language", langEnc);
        kvTaskProps1.addProperty("TaskName", taskName);
        kvTaskProps1.addProperty("TaskPropFile", "task-" + taskName);
        kvTaskProps1.addProperty("TaskPropCharset", "UTF-8");
        kvTaskProps1.addProperty("SSFPropFile", "props/ssf-props.txt");
        kvTaskProps1.addProperty("SSFPropCharset", "UTF-8");
        kvTaskProps1.addProperty("FSPropFile", "props/fs-props.txt");
        kvTaskProps1.addProperty("FSPropCharset", "UTF-8");
        kvTaskProps1.addProperty("MFeaturesFile", "props/fs-mandatory-attribs.txt");
        kvTaskProps1.addProperty("MFeaturesCharset", "UTF-8");
        kvTaskProps1.addProperty("OFeaturesFile", "props/fs-other-attribs.txt");
        kvTaskProps1.addProperty("PAttributesFile", "props/ps-attribs.txt");
        kvTaskProps1.addProperty("DAttributesFile", "props/dep-attribs.txt");
        kvTaskProps1.addProperty("SAttributesFile", "props/sem-attribs.txt");
        kvTaskProps1.addProperty("SSFCorpusStoryFile", selFile.getAbsolutePath());
        kvTaskProps1.addProperty("SSFCorpusCharset", charset);
        kvTaskProps1.addProperty("SSFCorpusStoryUTF8File", selFile.getAbsolutePath());

        kvTaskProps1.addProperty("POSTagsFile", "workspace/syn-annotation/pos-tags.txt");
        kvTaskProps1.addProperty("MorphTagsFile", "workspace/syn-annotation/morph-tags.txt");
        kvTaskProps1.addProperty("POSTagsCharset", "UTF-8");

        kvTaskProps1.addProperty("PhraseNamesFile", "workspace/syn-annotation/phrase-names.txt");
        kvTaskProps1.addProperty("PhraseNamesCharset", "UTF-8");

        kvTaskProps1.addProperty("CurrentPosition", sentenceId);

        SyntacticAnnotationWorkJPanel navigatedPanel;

        if (this.owner instanceof sanchay.SanchayMain) {

            this.kvTaskProps = kvTaskProps1;
            this.configure();
            this.setCurrentPosition(Integer.parseInt(sentenceId)-1, 0);

            List<SSFNode> nodes = this.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
            SSFPhrase tempRoot = this.sentence.getRoot();

            searchinFSandhighlightSimple(tempRoot, nodeID, this);
        }
        else{

            navigatedPanel = this;
            this.kvTaskProps = kvTaskProps1;

            this.setTaskName(kvTaskProps1.getPropertyValue("TaskName"));
            this.configure();

            List<SSFNode> nodes = this.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
            SSFPhrase tempRoot = this.sentence.getRoot();

            searchinFSandhighlightSimple(tempRoot, nodeID, this);

        }
        this.ssfPhraseJPanel.treeJTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                  NavigateToNextInValidationSimple(evt);

            }
        });

        }


    }



    public void searchinFSandhighlight(SSFNode root, String search, SyntacticAnnotationWorkJPanel navigatedPanel ) {

        if(search.equals(""))
        {

            jTreeViewJPanel.removeAll();
                dndLeftJPanel.removeAll();
                //dndLeftJPanel.remove(ssfPhraseJPanel);

                dndLeftJPanel.add(ssfPhraseJPanel, BorderLayout.CENTER);
                ssfPhraseJPanel.showControlTabs(false);
    //            cfgToMMTreeMapping = new LinkedHashMap(0, 10);
                mmRoot = sentence.getRoot().convertToGDepNode(cfgToMMTreeMapping);

//            try {
//                if(mmRoot == null)
//                    viewTreeJPanel = new SanchayTreeViewerJPanel(sentence.getRoot(), sentence.getRoot().getCopy(), cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_STRUCTURE_MODE, langEnc, false);
//                else
//                    viewTreeJPanel = new SanchayTreeViewerJPanel(sentence.getRoot(), mmRoot, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_STRUCTURE_MODE, langEnc, false);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

                viewTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) sentence.getRoot(), SanchayMutableTreeNode.CHUNK_MODE, langEnc);

                viewTreeJPanel.setSanchayTreeJPanel(ssfPhraseJPanel);
                viewTreeJPanel.initPopupMenu();
                dndRightJPanel.removeAll();
                dndRightJPanel.add(viewTreeJPanel, BorderLayout.CENTER);
                String selectinJtable = viewTreeJPanel.getCellPosition((SanchayMutableTreeNode) ((SSFPhrase) root));
                System.err.println(selectinJtable+"SEARCHNIL"+root);

//                System.err.println(viewTreeJPanel.tableJTable.findCellObject(root).column+"  "+viewTreeJPanel.tableJTable.findCellObject(root).row);
                System.err.println(viewTreeJPanel.getValueAt(1,1));

                if (selectinJtable != null) {
                String[] selectSplit = selectinJtable.split("::");

                viewTreeJPanel.changeSelection(Integer.parseInt(selectSplit[0]),Integer.parseInt( selectSplit[1]), true, false);
                }
            //viewTreeJPanel.tableJTable.changeSelection(1,2, true, false);
                ((CardLayout) topJPanel.getLayout()).show(topJPanel, "DNDView");
                topJPanel.setVisible(false);
                topJPanel.setVisible(true);
//above I added later april

        }
        else{
        int len = root.getChildCount();
        JDialog realTreeDialog;

        for (int i =0; i <len; i++)
        {
            SSFNode node = (SSFNode) root.getChildAt(i);
            String fs = node.getFeatureStructures().getAttributeValueString("name");
            if(fs.equals(search))
            {
                TreePath currentPath = new TreePath(node.getPath());
                navigatedPanel.ssfPhraseJPanel.getJTree().setSelectionPath(currentPath);
                navigatedPanel.ssfPhraseJPanel.getJTree().scrollPathToVisible(currentPath);
                node.isHighlighted(true);
                // below I added later april

                jTreeViewJPanel.removeAll();
                dndLeftJPanel.removeAll();
                //dndLeftJPanel.remove(ssfPhraseJPanel);

                dndLeftJPanel.add(ssfPhraseJPanel, BorderLayout.CENTER);
                ssfPhraseJPanel.showControlTabs(false);
    //            cfgToMMTreeMapping = new LinkedHashMap(0, 10);
//                mmRoot = sentence.getRoot().convertToMMNode(cfgToMMTreeMapping);
//
//            try {
//                if(mmRoot == null)
//                    viewTreeJPanel = new SanchayTreeViewerJPanel(sentence.getRoot(), sentence.getRoot().getCopy(), cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_STRUCTURE_MODE, langEnc, false);
//                else
//                    viewTreeJPanel = new SanchayTreeViewerJPanel(sentence.getRoot(), mmRoot, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_STRUCTURE_MODE, langEnc, false);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

                viewTreeJPanel = new SanchayTreeViewerJPanel((SSFPhrase) sentence.getRoot(), SanchayMutableTreeNode.CHUNK_MODE, langEnc);

                viewTreeJPanel.setSanchayTreeJPanel(ssfPhraseJPanel);
                viewTreeJPanel.initPopupMenu();
                dndRightJPanel.removeAll();
                dndRightJPanel.add(viewTreeJPanel, BorderLayout.CENTER);


                SanchayMutableTreeNode node1 = (SanchayMutableTreeNode) node;

                String selectinJtable = viewTreeJPanel.getCellPosition(node1);

                System.err.println(selectinJtable+"ting"+node1);

                 SanchayEdges sanchayEdges = viewTreeJPanel.getEdges();

                for (int ed = 0; ed < sanchayEdges.countEdges(); ed++) {
                SanchayEdge sanchayEdge = sanchayEdges.getEdge(ed);
                        if(sanchayEdge.getStartNode().equals(node1))
                        {
                            int cell[] = sanchayEdge.getStartCell();
                            System.err.println(cell[0]+" gy "+cell[1]);
                            viewTreeJPanel.changeSelection(cell[0],cell[1], true, false);
                            break;
                        }

                }

//                if (selectinJtable != null) {
//                String[] selectSplit = selectinJtable.split("::");

//                }
//                viewTreeJPanel.tableJTable.changeSelection(1,2, true, false);
//                viewTreeJPanel.tableJTable.getSelectedCellObject();




                ((CardLayout) topJPanel.getLayout()).show(topJPanel, "DNDView");
                topJPanel.setVisible(false);
                topJPanel.setVisible(true);
//above I added later april


            }
            else
                searchinFSandhighlight(node, search, navigatedPanel);

        }
        }

    }

    private void searchinFSandhighlightSimple(SSFNode root, String search, SyntacticAnnotationWorkJPanel navigatedPanel) {

        if(search.equals(""))
        {

        }
        else{

       // System.out.println(" "+search);
        int len = root.getChildCount();
        JDialog realTreeDialog;

        for (int i =0; i <len; i++)
        {
            SSFNode node = (SSFNode) root.getChildAt(i);
            String fs = node.getFeatureStructures().getAttributeValueString("name");
            if(fs.equals(search))
            {
                TreePath currentPath = new TreePath(node.getPath());
                navigatedPanel.ssfPhraseJPanel.getJTree().setSelectionPath(currentPath);
                navigatedPanel.ssfPhraseJPanel.getJTree().scrollPathToVisible(currentPath);
                node.isHighlighted(true);
            }
            else
                searchinFSandhighlightSimple(node, search, navigatedPanel);

        }
        }


    }

    public void navigateToSentenceToValidate(NavigatetoValidateEvent evt) {

                String loc[] = evt.getLocationStringArray();
        String nodeID = loc[0];
        String sentenceId = loc[1];
        String fileID = loc[2];
        //System.out.println(nodeID + " " + sentenceId + " " + fileID);
        File selFile = new File(inputDirforValidation + "/" + fileID);


        String path = inputDirforValidation + "/" + fileID;
        File cfile = new File(path);

        if (cfile.exists() == false) {
            return;
        }

        KeyValueProperties kvTaskProps1 = new KeyValueProperties();
        String taskName = cfile.getName();

        String taskPropFile = "task-" + taskName;
        File tpfile = new File(cfile.getParent(), taskPropFile);
        taskPropFile = tpfile.getAbsolutePath();

        kvTaskProps1.addProperty("Language", langEnc);
        kvTaskProps1.addProperty("TaskName", taskName);
        kvTaskProps1.addProperty("TaskPropFile", "task-" + taskName);
        kvTaskProps1.addProperty("TaskPropCharset", "UTF-8");
        kvTaskProps1.addProperty("SSFPropFile", "props/ssf-props.txt");
        kvTaskProps1.addProperty("SSFPropCharset", "UTF-8");
        kvTaskProps1.addProperty("FSPropFile", "props/fs-props.txt");
        kvTaskProps1.addProperty("FSPropCharset", "UTF-8");
        kvTaskProps1.addProperty("MFeaturesFile", "props/fs-mandatory-attribs.txt");
        kvTaskProps1.addProperty("MFeaturesCharset", "UTF-8");
        kvTaskProps1.addProperty("OFeaturesFile", "props/fs-other-attribs.txt");
        kvTaskProps1.addProperty("PAttributesFile", "props/ps-attribs.txt");
        kvTaskProps1.addProperty("DAttributesFile", "props/dep-attribs.txt");
        kvTaskProps1.addProperty("SAttributesFile", "props/sem-attribs.txt");
        kvTaskProps1.addProperty("SSFCorpusStoryFile", selFile.getAbsolutePath());
        kvTaskProps1.addProperty("SSFCorpusCharset", charset);
        kvTaskProps1.addProperty("SSFCorpusStoryUTF8File", selFile.getAbsolutePath());

        kvTaskProps1.addProperty("POSTagsFile", "workspace/syn-annotation/pos-tags.txt");
        kvTaskProps1.addProperty("MorphTagsFile", "workspace/syn-annotation/morph-tags.txt");
        kvTaskProps1.addProperty("POSTagsCharset", "UTF-8");

        kvTaskProps1.addProperty("PhraseNamesFile", "workspace/syn-annotation/phrase-names.txt");
        kvTaskProps1.addProperty("PhraseNamesCharset", "UTF-8");

        kvTaskProps1.addProperty("CurrentPosition", sentenceId);

        if (this.owner instanceof SanchayMain) {

            SyntacticAnnotationWorkJPanel navigatedPanel = new SyntacticAnnotationWorkJPanel(kvTaskProps1);
            navigatedPanel.setOwner(this.owner);

            ((SanchayMain) this.owner).addApplicationToTabbedPane(navigatedPanel, "Syntactic Annotation", false);
            navigatedPanel.configure();
            navigatedPanel.setCurrentPosition(Integer.parseInt(sentenceId)-1, 0);
            List<SSFNode> nodes = navigatedPanel.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
            SSFPhrase tempRoot = navigatedPanel.sentence.getRoot();


                searchinFSandhighlightSimple(tempRoot, nodeID, navigatedPanel);

        }
        else{

            this.kvTaskProps = kvTaskProps1;

            this.setTaskName(kvTaskProps1.getPropertyValue("TaskName"));
            this.configure();
            this.setCurrentPosition(Integer.parseInt(sentenceId)-1, 0);
            List<SSFNode> nodes = this.sentence.getRoot().getNodesForFS("name='"+nodeID+"'");
            SSFPhrase tempRoot = this.sentence.getRoot();


                searchinFSandhighlightSimple(tempRoot, nodeID, this);


        }




    }

    public ClientType getClientType()
    {
        return clientType;
    }

    public void openFileMode()
    {
        taskSetupJPanel = new SyntacticAnnotationTaskSetupJPanel(true, this);
        taskSetupJPanel.setOwner(owner);
        SanchayJDialog openDialog = new SanchayJDialog(owner, sanchay.GlobalProperties.getIntlString("Sanchay_Syntactic_Annotation_Task_Setup"), true, (JPanelDialog) taskSetupJPanel);
        openDialog.pack();

        UtilityFunctions.centre(openDialog);

        openDialog.setVisible(true);
    }
    public void openTaskMode()
    {
        try {
            propman = new PropertiesManager(GlobalProperties.getHomeDirectory() + "/" + "workspace/syn-annotation/server-props.txt", GlobalProperties.getIntlString("UTF-8"));
            propman.print(System.out);
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

        if(propbankMode)
            selectTaskJPanel = new SelectTaskJPanel(SelectTaskJPanel.PROPBANK_ANNOTATION_TASK, this);
        else
            selectTaskJPanel = new SelectTaskJPanel(SelectTaskJPanel.SYNTACTIC_ANNOTATION_TASK, this);

        selectTaskJPanel.setOwner(owner);
        selectTaskJPanel.setAnnotationClient(this);
        SanchayJDialog dialog = new SanchayJDialog(owner, "Sanchay Syntactic Annotation Task Setup", true, (JPanelDialog) selectTaskJPanel);
	dialog.pack();

	UtilityFunctions.centre(dialog);

        dialog.setVisible(true);
    }
    public void openRemoteFileMode()
    {
        if(sanchayRemoteWorkJPanel == null) {
            sanchayRemoteWorkJPanel = SanchayMain.getSSHClientJPanel();
        }

        sanchayRemoteWorkJPanel.setWorkJPanel(this);

        sanchayRemoteWorkJPanel.setOwner(owner);
        SanchayJDialog openDialog = new SanchayJDialog(owner, sanchay.GlobalProperties.getIntlString("Sanchay_SSH_Client"), true, (JPanelDialog) sanchayRemoteWorkJPanel);
//            openDialog.pack();
        SanchayGUIUtils.maximizeDialog(openDialog);

        UtilityFunctions.centre(openDialog);

        openDialog.setVisible(true);
    }

    public void claimTask()
    {
        if(SelectTaskJPanel.isOwnTask(taskList, taskName))
            return;

        String user = System.getProperty("user.name");

        String u1 = (String) taskList.getValue("TaskName", taskName, "User1");
        String u2 = (String) taskList.getValue("TaskName", taskName, "User1");

        int colInd = -1;
        int rowInd = -1;

        if(u1.equalsIgnoreCase("NONE"))
        {
            colInd = taskList.findColumn("User1");
            rowInd = ((Integer) taskList.getRowIndices("TaskName", taskName).get(0)).intValue();
        }
        else
        {
            colInd = taskList.findColumn("User2");
            rowInd = ((Integer) taskList.getRowIndices("TaskName", taskName).get(0)).intValue();
        }
        
        taskList.setValueAt(user, rowInd, colInd);
        
        try {
            taskList.save();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void init() {
        dirty = false;
        sentence = null;
        ssfStory = new SSFStoryImpl();
    }

    public void initPropbank()
    {
        if(propbankMode == false)
            return;

        extraInfoJPanel.setWPos2SPosMap(new KeyValueProperties());

//        int ret = JOptionPane.showConfirmDialog(this, sanchay.GlobalProperties.getIntlString("Are_you_sure_you_want_to_shift_to_the_frameset_mode?"), sanchay.GlobalProperties.getIntlString("Frameset_Mode"), JOptionPane.YES_NO_OPTION);
//
//        if(ret == JOptionPane.NO_OPTION)
//            return;

        if((!propbankMode && GlobalProperties.getClientModes().getPropertyValue("SYNTACTIC_ANNOTATION").equals("FILE_MODE"))
                || (propbankMode && GlobalProperties.getClientModes().getPropertyValue("PROPBANK_ANNOTATION").equals("FILE_MODE")))
        {
            int ret = JOptionPane.showConfirmDialog(this, "Would you like to save the document before proceeding?", sanchay.GlobalProperties.getIntlString("Save?"), JOptionPane.YES_NO_OPTION);

            if(ret == JOptionPane.YES_OPTION)
            {
                save(null);
            }
        }

//        initNavigationList();

        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);

        int count = ssfStory.countSentences();

        Vector pvec = new Vector(count);

        int wpos = 0;

        for(int i = 0; i < count; i++) {
            SSFSentence lsentence = ssfStory.getSentence(i);

            SSFPhrase root = lsentence.getRoot();

            List<SSFNode> nodesTag = root.getNodesForName(extraInfoJPanel.getNavigationTag());
            List<SSFNode> nodesStem = root.getNodesForAttribVal("lex", extraInfoJPanel.getNavigationWord(), true);

            Vector nodes = (Vector) UtilityFunctions.getIntersection(nodesTag, nodesStem);

            if(nodes == null || nodes.size() == 0)
                continue;

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

    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }
    
    private void switchDNDView() {
        if(propbankModeJCheckBox.isSelected()) {
            jTreeViewJPanel.removeAll();
            dndLeftJPanel.add(ssfPhraseJPanel, BorderLayout.CENTER);
            ssfPhraseJPanel.showControlTabs(false);

            cfgToMMTreeMapping = new LinkedHashMap(0, 10);
            
            mmRoot = sentence.getRoot().convertToGDepNode(cfgToMMTreeMapping);
            
            try {
                if(mmRoot == null)
                    viewTreeJPanel = new SanchayTreeViewerJPanel(sentence.getRoot(), sentence.getRoot().getCopy(), cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE, langEnc, false);
                else
                    viewTreeJPanel = new SanchayTreeViewerJPanel(sentence.getRoot(), mmRoot, cfgToMMTreeMapping, SanchayMutableTreeNode.DEPENDENCY_RELATIONS_MODE, langEnc, false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            viewTreeJPanel.setSanchayTreeJPanel(ssfPhraseJPanel);
            viewTreeJPanel.initPopupMenu();
            
            dndRightJPanel.removeAll();
            dndRightJPanel.add(viewTreeJPanel, BorderLayout.CENTER);
            
            ((CardLayout) topJPanel.getLayout()).show(topJPanel, "DNDView");
            topJPanel.setVisible(false);
            topJPanel.setVisible(true);
        } else {
            dndLeftJPanel.removeAll();
            dndRightJPanel.removeAll();
            jTreeViewJPanel.removeAll();
            jTreeViewJPanel.add(ssfPhraseJPanel, BorderLayout.CENTER);
            
            ((CardLayout) topJPanel.getLayout()).show(topJPanel, "JTreeView");
        }
    }

    private void query(ActionEvent e, boolean inFiles)
    {
//        ssfStory.clearHighlights();

        ssfQueryString = queryJTextField.getText();

        if(ssfQueryString.equals(""))
            return;

        storeCurrentPosition();

        SSFQuery ssfQuery = new SSFQuery(ssfQueryString);

        try
        {
            ssfQuery.parseQuery();
        } catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this, "Error in parsing the query: " + ssfQuery, sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SanchayTableModel matches = null;

        if(inFiles)
            matches = queryInFiles(ssfQuery);

        else
            matches = ssfQuery.query(ssfStory, ssfQuery);

        ssfQuery.send();

        displayCurrentPosition();

        if(matches == null)
        {
            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("No_match_found."), sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        matches.insertRow(0);
        matches.setValueAt("Matches:", 0, 0);

        if(matches.getRowCount() > 1)
            matches.insertRow(1);

        if(inFiles == false)
            showSearchResults(matches, langEnc, 1);
    }

    private SanchayTableModel queryInFiles(SSFQuery ssfQuery)
    {
        SanchayTableModel matches = null;

        selFiles = getSelectedFiles();
        selStories = new LinkedHashMap<File, SSFStory>();

        if(selFiles != null && selFiles.length > 1)
        {
            for (int i = 0; i < selFiles.length; i++) {
                File file = (File) selFiles[i];

                SSFStory story = new SSFStoryImpl();

                selStories.put(file, story);
                story.setSSFFile(file.getAbsolutePath());

                String cs = kvTaskProps.getPropertyValue("SSFCorpusCharset");

                try {
                    story.readFile(file.getAbsolutePath(), cs);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            matches = ssfQuery.queryInFiles(ssfQuery, selStories,"");
        }
        else {
            matches = ssfQuery.query(ssfStory, ssfQuery);
        }

        if(selFiles.length > 1)
             showSearchResults(matches, langEnc, selFiles.length);
        else
             showSearchResults(matches, langEnc, 1);

        return matches;
    }
    
    private void showSearchResults(SanchayTableModel matches, String langEnc, int tcount) {

        if(matches == null) {
            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("No_match_found."), sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int rcount = matches.getRowCount();

        if(rcount == 0) {
            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("No_match_found."), sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        matches.insertRow(0);
        matches.insertRow(0);
        
        matches.setValueAt(sanchay.GlobalProperties.getIntlString("Total_:_"), 0, 0);
        matches.setValueAt(Integer.toString(tcount), 0, 1);
        
        SanchayJDialog resultsJDialog = null;

        if(owner != null)
         resultsJDialog =DialogFactory.showTableDialog(dialog, sanchay.GlobalProperties.getIntlString("Search_Results"), false, matches, langEnc, SanchayTableJPanel.DEFAULT_MODE);
        else if(dialog != null)
         resultsJDialog =DialogFactory.showTableDialog(dialog, sanchay.GlobalProperties.getIntlString("Search_Results"), false, matches, langEnc, SanchayTableJPanel.DEFAULT_MODE);

        SanchayTableJPanel resultsJPanel = (SanchayTableJPanel) resultsJDialog.getJPanel();

        resultsJPanel.getJTable().getColumnModel().getColumn(0).setCellEditor(new SanchayActionTableCellEditor(this, langEnc, "Search Results", SanchayActionTableCellEditor.FIND_AND_NAVIGATE));
        //resultsJPanel.getJTable().setDefaultEditor(String.class,new SanchayActionTableCellEditor(this, langEnc, "Search Results", SanchayActionTableCellEditor.FIND_AND_NAVIGATE));

        /*
        SanchayJDialog check = DialogFactory.showTableDialog(dialog, "Validation Results @ " + inputDirforValidation, false, matches, langEnc, SanchayTableJPanel.DEFAULT_MODE);

        SanchayTableJPanel tableJpanel = (SanchayTableJPanel) check.getJPanel();

        SanchayActionTableCellEditor editor = new SanchayActionTableCellEditor(this, langEnc, "Sentence", SanchayActionTableCellEditor.VALIDATION_MODE);
        tableJpanel.getJTable().setDefaultEditor(String.class, editor);
        ((SanchayDefaultJTable) tableJpanel.getJTable()).addEventListener(this);
         * */


        //if(tcount > 1)
        //    resultsJPanel.getJTable().getColumnModel().getColumn(matches.getColumnCount() - 1).setCellEditor(new SanchayActionTableCellEditor(this, langEnc, "Search Results", SanchayActionTableCellEditor.FIND_AND_NAVIGATE));

        UtilityFunctions.fitColumnsToContent(resultsJPanel.getJTable());

        ((SanchayDefaultJTable) resultsJPanel.getJTable()).addEventListener(this);
    }
    
    private void showValSearchResults(SanchayTableModel matches, String langEnc, int tcount) {

        if(matches == null) {
            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("No_match_found."), sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int rcount = matches.getRowCount();

        if(rcount == 0) {
            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("No_match_found."), sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        matches.insertRow(0);
        matches.insertRow(0);

        matches.setValueAt(sanchay.GlobalProperties.getIntlString("Total_:_"), 0, 0);
        matches.setValueAt(Integer.toString(tcount), 0, 1);

        SanchayJDialog resultsJDialog = null;

        if(owner != null)
         resultsJDialog =DialogFactory.showTableDialog(dialog, sanchay.GlobalProperties.getIntlString("Search_Results"), false, matches, langEnc, SanchayTableJPanel.DEFAULT_MODE);
        else if(dialog != null)
         resultsJDialog =DialogFactory.showTableDialog(dialog, sanchay.GlobalProperties.getIntlString("Search_Results"), false, matches, langEnc, SanchayTableJPanel.DEFAULT_MODE);

        SanchayTableJPanel resultsJPanel = (SanchayTableJPanel) resultsJDialog.getJPanel();

        resultsJPanel.getJTable().getColumnModel().getColumn(0).setCellEditor(new SanchayActionTableCellEditor(this, langEnc, "Search Results", SanchayActionTableCellEditor.FIND_AND_NAVIGATE));
        //resultsJPanel.getJTable().setDefaultEditor(String.class,new SanchayActionTableCellEditor(this, langEnc, "Search Results", SanchayActionTableCellEditor.FIND_AND_NAVIGATE));

        /*
        SanchayJDialog check = DialogFactory.showTableDialog(dialog, "Validation Results @ " + inputDirforValidation, false, matches, langEnc, SanchayTableJPanel.DEFAULT_MODE);

        SanchayTableJPanel tableJpanel = (SanchayTableJPanel) check.getJPanel();

        SanchayActionTableCellEditor editor = new SanchayActionTableCellEditor(this, langEnc, "Sentence", SanchayActionTableCellEditor.VALIDATION_MODE);
        tableJpanel.getJTable().setDefaultEditor(String.class, editor);
        ((SanchayDefaultJTable) tableJpanel.getJTable()).addEventListener(this);
         * */


        //if(tcount > 1)
        //    resultsJPanel.getJTable().getColumnModel().getColumn(matches.getColumnCount() - 1).setCellEditor(new SanchayActionTableCellEditor(this, langEnc, "Search Results", SanchayActionTableCellEditor.FIND_AND_NAVIGATE));

        UtilityFunctions.fitColumnsToContent(resultsJPanel.getJTable());

        ((SanchayDefaultJTable) resultsJPanel.getJTable()).addEventListener(this);
    }

    private File[] getSelectedFiles() {

        String curDir = stateKVProps.getPropertyValue("CurrentDir");

        if(textFile != null && !textFile.equals("") && !textFile.equals(sanchay.GlobalProperties.getIntlString("Untitled"))) {
            File tfile = new File(textFile);

            if(tfile.exists()) {
                curDir = tfile.getParentFile().getAbsolutePath();
            }
        }

        SanchayJDialog fsDialog = (SanchayJDialog) DialogFactory.showFileSelectionDialog(owner, sanchay.GlobalProperties.getIntlString("FileSelectionJPanel"), true, new File(curDir));
        
        FileSelectionJPanel fsPanel = (FileSelectionJPanel) fsDialog.getJPanel();
        
        return fsPanel.getSelectedFilesExt();
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

        TableColumn tagsCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Tag"));
        labelEditor.setEditable(true);
        tagsCol.setCellEditor(new DefaultCellEditor(labelEditor));

        DefaultComboBoxModel textEditorModel = new DefaultComboBoxModel();
        textEditorModel.addElement("[.]*");
        JComboBox textEditor = new JComboBox();
        textEditor.setModel(textEditorModel);
        UtilityFunctions.setComponentFont(textEditor, langEnc);

        TableColumn textCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Text"));
        textEditor.setEditable(true);
        textCol.setCellEditor(new DefaultCellEditor(textEditor));
        
        Vector attribNamesVec = UtilityFunctions.arrayToVector(fsProperties.getAllAttributes());

        UtilityFunctions.makeExactMatchRegexes(attribNamesVec);

        DefaultComboBoxModel attribNameEditorModel = new DefaultComboBoxModel(attribNamesVec);
        attribNameEditorModel.insertElementAt("[.]*", 0);
        JComboBox attribNameEditor = new JComboBox();
        attribNameEditor.setModel(attribNameEditorModel);
        UtilityFunctions.setComponentFont(attribNameEditor, langEnc);

        TableColumn attribNamesCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Attribute_Name"));
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

        TableColumn attribValsCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Attribute_Value"));
        attribValEditor.setEditable(true);
        attribValsCol.setCellEditor(new DefaultCellEditor(attribValEditor));

        SanchayJDialog findDialog = new SanchayJDialog(owner, sanchay.GlobalProperties.getIntlString("Find"), true, (JPanelDialog) findOptionsJPanel);
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

        TableColumn tagsCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Tag"));
        labelEditor.setEditable(true);
        tagsCol.setCellEditor(new DefaultCellEditor(labelEditor));

        Vector tagsVecNew = new Vector();
        tagsVecNew.addAll(tagsVec);
        UtilityFunctions.backFromExactMatchRegex(tagsVecNew);

        labelEditorModel = new DefaultComboBoxModel(tagsVecNew);
        TableColumn newTagsCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("New_Tag"));
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

        TableColumn textCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Text"));
        textEditor.setEditable(true);
        textCol.setCellEditor(new DefaultCellEditor(textEditor));

        TableColumn newTextCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("New_Text"));
        newTextCol.setCellEditor(new DefaultCellEditor(textEditor));

        Vector attribNamesVec = UtilityFunctions.arrayToVector(fsProperties.getAllAttributes());

        UtilityFunctions.makeExactMatchRegexes(attribNamesVec);

        DefaultComboBoxModel attribNameEditorModel = new DefaultComboBoxModel(attribNamesVec);
        attribNameEditorModel.insertElementAt("[.]*", 0);
        JComboBox attribNameEditor = new JComboBox();
        attribNameEditor.setModel(attribNameEditorModel);
        UtilityFunctions.setComponentFont(attribNameEditor, langEnc);

        TableColumn attribNamesCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Attribute_Name"));
        attribNameEditor.setEditable(true);
        attribNamesCol.setCellEditor(new DefaultCellEditor(attribNameEditor));

        Vector attribNamesVecNew = new Vector();
        attribNamesVecNew.addAll(attribNamesVec);
        UtilityFunctions.backFromExactMatchRegex(attribNamesVecNew);

        attribNameEditorModel = new DefaultComboBoxModel(attribNamesVecNew);
        TableColumn newAttribNamesCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("New_Name"));
        attribNameEditor = new JComboBox();
        UtilityFunctions.setComponentFont(attribNameEditor, langEnc);
        attribNameEditor.setEditable(true);
        attribNameEditor.setModel(attribNameEditorModel);
        newAttribNamesCol.setCellEditor(new DefaultCellEditor(attribNameEditor));

        JCheckBox createAttribEditor = new JCheckBox();
        createAttribEditor.setSelected(false);

        TableColumn createAttribCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Create_Attribute"));
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

        TableColumn attribValsCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("Attribute_Value"));
        attribValEditor.setEditable(true);
        attribValsCol.setCellEditor(new DefaultCellEditor(attribValEditor));

        attribValEditor = new JComboBox();
        attribValEditor.setEditable(true);
        attribValEditor.setModel(attribValEditorModelNew);
        UtilityFunctions.setComponentFont(attribValEditor, langEnc);

        TableColumn newAttribValsCol = tableJTable.getColumn(sanchay.GlobalProperties.getIntlString("New_Value"));
        newAttribValsCol.setCellEditor(new DefaultCellEditor(attribValEditor));

        SanchayJDialog findDialog = new SanchayJDialog(owner, sanchay.GlobalProperties.getIntlString("Replace"), true, (JPanelDialog) findOptionsJPanel);
        findDialog.pack();

        UtilityFunctions.maxmize(findDialog);
        UtilityFunctions.centre(findDialog);

        findDialog.setVisible(true);

        return findOptionsTable;
    }

//    public void find(ActionEvent e, boolean inFiles)
//    {
//        TreePath currentSelection = ssfPhraseJPanel.getJTree().getSelectionPath();
//
//        String nlabel = "";
//        String ntext = "";
//        String attrib = "";
//        String val = "";
//
//        SanchayTableModel findOptionsTable = getFindOptionsTable();
//
//        if(findOptionsTable.isToBeSaved() == false)
//            return;
//
//        if (currentSelection != null) {
//            SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());
//            nlabel = currentNode.getName();
//            ntext = currentNode.getLexData();
//        }
//
//        SanchayTableModel batchMatches = null;
//
//        int rcount = findOptionsTable.getRowCount();
//
//        Vector matchesVec = new Vector();
//
//        for (int i = 0; i < rcount; i++)
//        {
//            nlabel = (String) findOptionsTable.getValueAt(i, 0);
//            ntext = (String) findOptionsTable.getValueAt(i, 1);
//            attrib = (String) findOptionsTable.getValueAt(i, 2);
//            val = (String) findOptionsTable.getValueAt(i, 3);
//
//            if(
//                    (nlabel == null || nlabel.equals(""))
//                    && (ntext == null || ntext.equals(""))
//                    && (attrib == null || attrib.equals(""))
//                    && (val == null || val.equals(""))
//              )
//                continue;
//
//            if(nlabel == null)
//                nlabel = "[.]*";
//            if(ntext == null)
//                ntext = "[.]*";
//            if(attrib == null)
//                attrib = "[.]*";
//            if(val == null)
//                val = "[.]*";
//
//            SanchayTableModel matches = null;
//
//            if(inFiles)
//                matches = findInFiles(nlabel, ntext, attrib, val,
//                    null, null, null, null, false, false);
//            else
//                matches = find(ssfStory, nlabel, ntext, attrib, val,
//                    null, null, null, null, false, false);
//
//            if(matches == null)
//                continue;
//
//            matches.insertRow(0);
//            matches.setValueAt(sanchay.GlobalProperties.getIntlString("Matches_-_") + (i + 1), 0, 0);
//
//            if(matches.getRowCount() > 1)
//                matches.insertRow(1);
//
//            if(i > 0)
//                matches.insertRow(0);
//
//            matchesVec.add(matches);
//        }
//
//        if(matchesVec != null && matchesVec.size() > 0)
//            batchMatches = SanchayTableModel.mergeRows(matchesVec);
//
////        showSearchResults(batchMatches, langEnc, 1);
//
//        if(inFiles == false)
//            showSearchResults(batchMatches, langEnc, 1);
//    }

    public void find(ActionEvent e, boolean inFiles)
    {
        TreePath currentSelection = ssfPhraseJPanel.getJTree().getSelectionPath();

        String nlabel = "";
        String ntext = "";
        String attrib = "";
        String val = "";

        SSFFindReplace findReplaceTable = new SSFFindReplace();

        findReplaceTable.setDefaults(posTagsPT,phraseNamesPT,langEnc, owner, ssfPhraseJPanel);

        SanchayTableModel findOptionsTable = findReplaceTable.getFindOptionsTable();

        //SanchayTableModel findOptionsTable = getFindOptionsTable();

        if(findOptionsTable.isToBeSaved() == false)
            return;

        if (currentSelection != null) {
            SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());
            nlabel = currentNode.getName();
            ntext = currentNode.getLexData();
        }

        SanchayTableModel batchMatches = null;

        int rcount = findOptionsTable.getRowCount();

        Vector matchesVec = new Vector();

        for (int i = 0; i < rcount; i++)
        {
            nlabel = (String) findOptionsTable.getValueAt(i, 0);
            ntext = (String) findOptionsTable.getValueAt(i, 1);
            attrib = (String) findOptionsTable.getValueAt(i, 2);
            val = (String) findOptionsTable.getValueAt(i, 3);

            if(
                    (nlabel == null || nlabel.equals(""))
                    && (ntext == null || ntext.equals(""))
                    && (attrib == null || attrib.equals(""))
                    && (val == null || val.equals(""))
              )
                continue;

            if(nlabel == null)
                nlabel = "[.]*";
            if(ntext == null)
                ntext = "[.]*";
            if(attrib == null)
                attrib = "[.]*";
            if(val == null)
                val = "[.]*";

            SanchayTableModel matches = null;

            if(inFiles)
                matches = findInFiles(nlabel, ntext, attrib, val,
                    null, null, null, null, false, false);
            else
                matches = find(ssfStory, nlabel, ntext, attrib, val,
                    null, null, null, null, false, false);

            if(matches == null)
                continue;

            matches.insertRow(0);
            matches.setValueAt("Matches - " + (i + 1), 0, 0);

            if(matches.getRowCount() > 1)
                matches.insertRow(1);

            if(i > 0)
                matches.insertRow(0);

            matchesVec.add(matches);
        }

        if(matchesVec != null && matchesVec.size() > 0)
            batchMatches = SanchayTableModel.mergeRows(matchesVec);

//        showSearchResults(batchMatches, langEnc, 1);

        if(inFiles == false)
            showSearchResults(batchMatches, langEnc, 1);
    }

    public void contextFind(ActionEvent e, boolean inFiles)
    {
        TreePath currentSelection = ssfPhraseJPanel.getJTree().getSelectionPath();

        String nlabel = "";
        String ntext = "";
        String attrib = "";
        String val = "";

        SSFFindReplace findReplaceTable = new SSFFindReplace();

        findReplaceTable.setDefaults(posTagsPT,phraseNamesPT,langEnc, owner, ssfPhraseJPanel);

        SanchayTableModel findOptionsTable = findReplaceTable.getFindOptionsTable();

        //SanchayTableModel findOptionsTable = getFindOptionsTable();

        if(findOptionsTable.isToBeSaved() == false)
            return;

        if (currentSelection != null) {
            SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());
            nlabel = currentNode.getName();
            ntext = currentNode.getLexData();
        }

        SanchayTableModel batchMatches = null;

        int rcount = findOptionsTable.getRowCount();

        Vector matchesVec = new Vector();

        for (int i = 0; i < rcount; i++)
        {
            nlabel = (String) findOptionsTable.getValueAt(i, 0);
            ntext = (String) findOptionsTable.getValueAt(i, 1);
            attrib = (String) findOptionsTable.getValueAt(i, 2);
            val = (String) findOptionsTable.getValueAt(i, 3);

            if(
                    (nlabel == null || nlabel.equals(""))
                    && (ntext == null || ntext.equals(""))
                    && (attrib == null || attrib.equals(""))
                    && (val == null || val.equals(""))
              )
                continue;

            if(nlabel == null)
                nlabel = "[.]*";
            if(ntext == null)
                ntext = "[.]*";
            if(attrib == null)
                attrib = "[.]*";
            if(val == null)
                val = "[.]*";

            SanchayTableModel matches = null;

            if(inFiles)
                matches = findInFiles(nlabel, ntext, attrib, val,
                    null, null, null, null, false, false);
            else
                matches = find(ssfStory, nlabel, ntext, attrib, val,
                    null, null, null, null, false, false);

            if(matches == null)
                continue;

            matches.insertRow(0);
            matches.setValueAt("Matches - " + (i + 1), 0, 0);

            if(matches.getRowCount() > 1)
                matches.insertRow(1);

            if(i > 0)
                matches.insertRow(0);

            matchesVec.add(matches);
        }

        if(matchesVec != null && matchesVec.size() > 0)
            batchMatches = SanchayTableModel.mergeRows(matchesVec);

//        showSearchResults(batchMatches, langEnc, 1);

        if(inFiles == false)
            showSearchResults(batchMatches, langEnc, 1);
    }

    public void replace(ActionEvent e, boolean inFiles)
    {
        TreePath currentSelection = ssfPhraseJPanel.getJTree().getSelectionPath();

        String nlabel = "";
        String ntext = "";
        String attrib = "";
        String val = "";
        String nlabelReplace = "";
        String ntextReplace = "";
        String attribReplace = "";
        String valReplace = "";
        boolean createAttrib = false;

        SanchayTableModel findOptionsTable = getReplaceOptionsTable();

        if(findOptionsTable.isToBeSaved() == false)
            return;

        if (currentSelection != null) {
            SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());
            nlabel = currentNode.getName();
            ntext = currentNode.getLexData();
        }

        SanchayTableModel batchMatches = null;

        int rcount = findOptionsTable.getRowCount();

        Vector matchesVec = new Vector();

        for (int i = 0; i < rcount; i++)
        {
            nlabel = (String) findOptionsTable.getValueAt(i, 0);
            nlabelReplace = (String) findOptionsTable.getValueAt(i, 1);
            ntext = (String) findOptionsTable.getValueAt(i, 2);
            ntextReplace = (String) findOptionsTable.getValueAt(i, 3);
            attrib = (String) findOptionsTable.getValueAt(i, 4);
            attribReplace = (String) findOptionsTable.getValueAt(i, 5);
            createAttrib = ((Boolean) findOptionsTable.getValueAt(i, 6)).booleanValue();
            val = (String) findOptionsTable.getValueAt(i, 7);
            valReplace = (String) findOptionsTable.getValueAt(i, 8);

            if(
                    (nlabel == null || nlabel.equals(""))
                    && (ntext == null || ntext.equals(""))
                    && (attrib == null || attrib.equals(""))
                    && (val == null || val.equals(""))
              )
                continue;

            if(nlabel == null)
                nlabel = "[.]*";
            if(ntext == null)
                ntext = "[.]*";
            if(attrib == null)
                attrib = "[.]*";
            if(val == null)
                val = "[.]*";

//            SanchayTableModel matches = find(ssfStory, nlabel, ntext, attrib, val,
//                    nlabelReplace, ntextReplace, attribReplace, valReplace, true, createAttrib);

            SanchayTableModel matches = null;

            if(inFiles)
                matches = findInFiles(nlabel, ntext, attrib, val,
                    nlabelReplace, ntextReplace, attribReplace, valReplace, true, createAttrib);
            else
                matches = find(ssfStory, nlabel, ntext, attrib, val,
                    nlabelReplace, ntextReplace, attribReplace, valReplace, true, createAttrib);

            if(matches == null)
                continue;

            matches.insertRow(0);
            matches.setValueAt(sanchay.GlobalProperties.getIntlString("Matches_-_") + (i + 1), 0, 0);

            if(matches.getRowCount() > 1)
                matches.insertRow(1);

            if(i > 0)
                matches.insertRow(0);

            matchesVec.add(matches);
        }

        if(matchesVec != null && matchesVec.size() > 0)
            batchMatches = SanchayTableModel.mergeRows(matchesVec);

        if(inFiles == false)
            showSearchResults(batchMatches, langEnc, 1);
    }

    private SanchayTableModel find(SSFStory story, String nlabel, String ntext, String attrib, String val,
            String nlabelReplace, String ntextReplace, String attribReplace, String valReplace, boolean replace, boolean createAttrib) {
        int count = story.countSentences();

        Vector matchedNodes = new Vector(20, 20);
        Vector senNums = new Vector(20, 20);

        for (int i = 0; i < count; i++) {
            SSFSentence sen = story.getSentence(i);

            SSFPhrase root = sen.getRoot();

            List<SSFNode> senNodes = null;
            List<SSFNode> senNodesForLabel = null;

            if(nlabel.equals("[.]*") == false)
            {
                senNodesForLabel = root.getNodesForName(nlabel);

                if(senNodesForLabel != null && senNodesForLabel.size() > 0)
                    senNodes = senNodesForLabel;
            }

            List<SSFNode> senNodesForText = null;

            if(ntext.equals("[.]*") == false)
            {
//                senNodesForText = root.getNodesForLexData(ntext);
                senNodesForText = root.getNodesForText(ntext);

                if(senNodesForText != null)
                {
                    senNodes = (List) UtilityFunctions.getIntersection(senNodes, senNodesForText);
                }

                if(nlabel.equals("[.]*") == true && senNodes == null
                        && (senNodesForText != null && senNodesForText.size() > 0))
                    senNodes = senNodesForText;
            }

            List<SSFNode> senNodesForAttrib = null;

            if(attrib.equals("[.]*") == false && val.equals("[.]*"))
            {
                senNodesForAttrib = root.getNodesForAttrib(attrib, false);

                if(senNodesForAttrib != null && createAttrib == false)
                {
                    senNodes = (List) UtilityFunctions.getIntersection(senNodes, senNodesForAttrib);
                }

                if((nlabel.equals("[.]*") == true && ntext.equals("[.]*") == true)
                        && senNodes == null && (senNodesForAttrib != null && senNodesForAttrib.size() > 0))
                    senNodes = senNodesForAttrib;
            }

            List<SSFNode> senNodesForVal = null;

            if(val.equals("[.]*") == false)
            {
                senNodesForVal = root.getNodesForAttribVal(attrib, val, false);

                if(senNodesForVal != null)
                {
                    senNodes = (List) UtilityFunctions.getIntersection(senNodes, senNodesForVal);
                }

//                if((nlabel.equals("[.]*") == true && ntext.equals("[.]*") == true && attrib.equals("[.]*") == true)
                if((nlabel.equals("[.]*") == true && ntext.equals("[.]*") == true)
                        && senNodes == null && (senNodesForVal != null && senNodesForVal.size() > 0))
                    senNodes = senNodesForVal;
            }

            if(senNodes != null)
            {
                if(replace)
                {
                    int repCount = senNodes.size();

                    for (int j = 0; j < repCount; j++)
                    {
                        SSFNode mnode = (SSFNode) senNodes.get(j);

                        if(UtilityFunctions.backFromExactMatchRegex(nlabel).equals(nlabelReplace) == false)
                        {
                            mnode.replaceNames(nlabel, nlabelReplace);
                        }

                        if(UtilityFunctions.backFromExactMatchRegex(ntext).equals(ntextReplace) == false)
                        {
                            mnode.replaceLexData(ntext, ntextReplace);
                        }

                        if(UtilityFunctions.backFromExactMatchRegex(attrib).equals(attribReplace) == false || val.equals(valReplace) == false)
                        {
                            mnode.replaceAttribVal(attrib, val, attribReplace, valReplace, createAttrib);
                        }
                    }
                }

                int senCount = senNodes.size();

                for (int j = 0; j < senCount; j++) {
                    senNums.add(Integer.valueOf(i));
                }

                matchedNodes.addAll(senNodes);
            }
        }

        if(matchedNodes == null || matchedNodes.size() == 0)
            return null;

        count = matchedNodes.size();

        String cells[][] = new String[count][3];

        for (int i = 0; i < count; i++) {
            SSFNode n = (SSFNode) matchedNodes.get(i);

            SSFNode pnode = (SSFNode) n.getParent();

            if(pnode != null)
            {
                cells[i][0] = (String) Integer.toString(((Integer) senNums.get(i)).intValue() + 1);
                cells[i][1] = n.convertToBracketForm(1);
//                    cells[i][2] = ((SSFNode) n.getParent()).convertToBracketForm(1);
                cells[i][2] = pnode.makeRawSentence();
            }
        }

        SanchayTableModel matches = new SanchayTableModel(cells, new String[] {sanchay.GlobalProperties.getIntlString("Sentence"), sanchay.GlobalProperties.getIntlString("Matched_Text"), sanchay.GlobalProperties.getIntlString("Context")});

        if(count > 0)
            resetCurrentPosition();

        return matches;
    }

    private SanchayTableModel findInFiles(String nlabel, String ntext, String attrib, String val,
            String nlabelReplace, String ntextReplace, String attribReplace, String valReplace, boolean replace, boolean createAttrib) {
        SanchayTableModel matches = null;

        selFiles = getSelectedFiles();

        String cs = kvTaskProps.getPropertyValue("SSFCorpusCharset");

        if(selFiles != null && selFiles.length > 1)
            selStories = SSFStoryImpl.readStories((File[]) selFiles, cs);

        if(selFiles != null && selFiles.length > 1)
        {
            int ccount = 0;

            for (int i = 0; i < selFiles.length; i++) {
                File file = (File) selFiles[i];

                SSFStory story = selStories.get(file);
//
//                SSFStory story = new SSFStoryImpl();
//
//                selStories.put(file, story);
//                story.setSSFFile(file.getAbsolutePath());
//
//                String cs = ;
//
//                try {
//                    story.readFile(file.getAbsolutePath(), cs);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }

                if(i == 0) {
                    matches = find(story, nlabel, ntext, attrib, val,
                            nlabelReplace, ntextReplace, attribReplace, valReplace, replace, createAttrib);

                    if(matches == null)
                        continue;

                    matches.addColumn(sanchay.GlobalProperties.getIntlString("File"));

                    int rcount = matches.getRowCount();
                    ccount = matches.getColumnCount();

                    for (int j = 0; j < rcount; j++) {
                        matches.setValueAt(file, j, ccount - 1);
                    }
                }
                else
                {
                    SanchayTableModel fileMatches = null;

                    fileMatches = find(story, nlabel, ntext, attrib, val,
                            nlabelReplace, ntextReplace, attribReplace, valReplace, replace, createAttrib);

                    if(fileMatches == null)
                        continue;

                    if(matches == null)
                    {
                        matches = fileMatches;

                        matches.addColumn(sanchay.GlobalProperties.getIntlString("File"));

                        int rcount = matches.getRowCount();
                        ccount = matches.getColumnCount();

                        for (int j = 0; j < rcount; j++) {
                            matches.setValueAt(file, j, ccount - 1);
                        }
                    }
                    else
                    {
                        int rcount = matches.getRowCount();
                        int frcount = fileMatches.getRowCount();

                        for (int j = 0; j < frcount; j++) {
                            Vector rowData = fileMatches.getRow(j);
                            rowData.add("");

                            matches.addRow(rowData);
                            matches.setValueAt(file, rcount + j, ccount - 1);
                        }

                        matches.insertRow(rcount);
                    }
                }

                if(replace)
                {
                    try
                    {
                        story.save(file.getAbsolutePath(), cs);
                    } catch (FileNotFoundException ex)
                    {
                        Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedEncodingException ex)
                    {
                        Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        else {
            matches = find(ssfStory, nlabel, ntext, attrib, val,
                            nlabelReplace, ntextReplace, attribReplace, valReplace, replace, createAttrib);
        }

        if(selFiles.length > 1)
             showSearchResults(matches, langEnc, selFiles.length);
        else
             showSearchResults(matches, langEnc, 1);

        return matches;
    }

    public void replaceBatch(ActionEvent evt)
    {

    }

    public void replaceBatchInFiles(ActionEvent evt)
    {

    }

    private SanchayTableModel getStats(SSFStory story) {
        String cells[][] = new String[1][4];
        
        cells[0][0] = Integer.toString(story.countSentences());
        cells[0][1] = Integer.toString(story.countChunks());
        cells[0][2] = Integer.toString(story.countWords());
        cells[0][3] = Integer.toString(story.countCharacters());
        
        SanchayTableModel matches = new SanchayTableModel(cells, new String[] {sanchay.GlobalProperties.getIntlString("Sentences"), sanchay.GlobalProperties.getIntlString("Chunks"), sanchay.GlobalProperties.getIntlString("Words"), sanchay.GlobalProperties.getIntlString("Characters")});
        
        return matches;
    }

//    public void showStatisticsInFiles(ActionEvent e) {
//        getStatsInFiles();
//    }
    
    private SanchayTableModel getStatsInFiles() {
        SanchayTableModel matches = null;
        
        selFiles = getSelectedFiles();
        selStories = new LinkedHashMap<File, SSFStory>();

        int sentences = 0;
        int chunks = 0;
        int words = 0;
        int characters = 0;
        
        if(selFiles != null && selFiles.length > 1) {
            int ccount = 0;
            
            for (int i = 0; i < selFiles.length; i++) {
                File file = (File) selFiles[i];
                
                SSFStory story = new SSFStoryImpl();

                selStories.put(file, story);
                story.setSSFFile(file.getAbsolutePath());

                String charset = kvTaskProps.getPropertyValue("SSFCorpusCharset");
                
                try {
                    story.readFile(file.getAbsolutePath(), charset);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                if(i == 0) {
                    matches = getStats(story);
                    
                    matches.addColumn(sanchay.GlobalProperties.getIntlString("File"));
                    
                    int rcount = matches.getRowCount();
                    ccount = matches.getColumnCount();
                    
                    for (int j = 0; j < rcount; j++) {
                        matches.setValueAt(file, j, ccount - 1);
                    }
                    
                    sentences = story.countSentences();
                    chunks = story.countChunks();
                    words = story.countWords();
                    characters = story.countCharacters();
                } else {
                    SanchayTableModel fileMatches = getStats(story);
                    
                    int rcount = matches.getRowCount();
                    int frcount = fileMatches.getRowCount();
                    
                    for (int j = 0; j < frcount; j++) {
                        Vector rowData = fileMatches.getRow(j);
                        rowData.add("");
                        
                        matches.addRow(rowData);
                        matches.setValueAt(file, rcount + j, ccount - 1);
                    }
                    
                    sentences += story.countSentences();
                    chunks += story.countChunks();
                    words += story.countWords();
                    characters += story.countCharacters();
                    
                    matches.insertRow(rcount);
                }
            }
            
            matches.insertRow(0);
            matches.insertRow(0);
            
            matches.setValueAt(Integer.toString(sentences), 0, 0);
            matches.setValueAt(Integer.toString(chunks), 0, 1);
            matches.setValueAt(Integer.toString(words), 0, 2);
            matches.setValueAt(Integer.toString(characters), 0, 3);
            matches.setValueAt(sanchay.GlobalProperties.getIntlString("Total_(All_Files)"), 0, 4);
        } else {
            matches = getStats(ssfStory);
        }
        
        if(selFiles.length > 1)
            showSearchResults(matches, langEnc, selFiles.length);
        else
            showSearchResults(matches, langEnc, 1);
        
        return matches;
    }

    public void setCurrentPosition(int cp) {

    }
    
    public void setCurrentPosition(int cp, int cwp) {
        int slSize = ssfStory.countSentences();
        if(cp >= 0 && cp < slSize) {
            if(cp != currentPosition) {
                storeCurrentPosition();
                currentPosition = cp;
            }

            if(propbankMode && cwp != currentWPosition)
                currentWPosition = cwp;
            
            displayCurrentPosition();
        }
    }
    
    public void setCurrentPosition(String nid, int cp, int cwp) {
        nodeID = nid;
        int slSize = ssfStory.countSentences();
        if(cp >= 0 && cp < slSize) {
            if(cp != currentPosition) {
                storeCurrentPosition();
                currentPosition = cp;
            }

            if(propbankMode && cwp != currentWPosition)
                currentWPosition = cwp;

            displayCurrentPosition();

            SSFNode inode = sentence.getRoot().getNodeForId(nid);

            if(inode != null)
            {
                inode.isHighlighted(true);

                TreePath tpath = new TreePath(inode.getPath());

//                ssfPhraseJPanel.getJTree().se;
                ssfPhraseJPanel.getJTree().scrollPathToVisible(tpath);
            }
        }
    }

    private void storeCurrentPosition() {
        if(sentence != null && dirty == true) {
            JTree jtree = ssfPhraseJPanel.getJTree();
            TreePath currentSelection = jtree.getSelectionPath();
            
            if (currentSelection != null) {
                SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());
                ssfPhraseJPanel.saveTreeNode(currentNode);
            }
            
            ssfStory.modifySentence(sentence, currentPosition);
            commentsPT.modifyToken(commentJTextArea.getText(), currentPosition);
            dirty = false;
        }
    }
    
    private void displayCurrentPosition() {

        if(dirty == false) {
            try {
                SSFSentenceImpl storySentence = (SSFSentenceImpl) ssfStory.getSentence(currentPosition);
                sentence = (SSFSentence) storySentence.getCopy();
                dirty = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        comment = (String) commentsPT.getToken(currentPosition);
        commentJTextArea.setText(comment);
        
        ssfPhraseJPanel.getModel().setRoot(sentence.getRoot());
        ssfPhraseJPanel.expandAll(null);
        
        String currentPositionString = Integer.toString(currentPosition + 1);

        if(propbankMode)
        {
//            currentWPosition = Integer.parseInt(wpos2sposMap.getPropertyValue(currentPositionString)) - 1;
            String currentWPositionString = Integer.toString(currentWPosition + 1);
            positionJComboBox.setSelectedItem(currentWPositionString);

            highlightWord();

            JTree jtree = ssfPhraseJPanel.getJTree();
            TreePath currentSelection = jtree.getSelectionPath();

            if (currentSelection != null) {
                SSFNode currentNode = (SSFNode) (currentSelection.getLastPathComponent());
                String wxLexData = extraInfoJPanel.getEncodingConverter().convert(currentNode.getLexData());
                extraInfoJPanel.removeAllWordStems();
                extraInfoJPanel.addWordStem(wxLexData);
                extraInfoJPanel.selectWordStem(wxLexData);

                extraInfoJPanel.displayWordInfo(wxLexData);
            }

            ssfPhraseJPanel.getJTree().updateUI();
            ssfPhraseJPanel.getJTree().updateUI();
        }
        else if(positionJComboBox.getSelectedItem().equals(currentPositionString) == false)
            positionJComboBox.setSelectedItem(currentPositionString);
        

        if(nodeID.isEmpty())
        {
            node=null;
        }
        else
        {
                SSFPhrase root = sentence.getRoot();
                node = root.getNodeForAttribVal("name", nodeID, true);
        }

        //System.out.println("node is: "+nodeID+" "+node);

        if(node!=null)
        {
            ssfStory.clearHighlights();
            TreePath currentPath = new TreePath(node.getPath());
            ssfPhraseJPanel.getJTree().setSelectionPath(currentPath);
            ssfPhraseJPanel.getJTree().scrollPathToVisible(currentPath);
            node.isHighlighted(true);
        }

        ssfPhraseJPanel.initTreeJPanel();


//        switchDNDView();
        
        // Moved to TreeJPanel
        //senJTextArea.setText(sentence.getRoot().convertToBracketForm(1));
//      toggleTgtUTF8(utf8Shown);
    }

    private void highlightWord() {
        String wposString = (currentWPosition + 1) + "";
        String posString = (currentPosition + 1) + "";

        SSFPhrase root = sentence.getRoot();

        root.clearHighlights();

        List<SSFNode> nodesTag = root.getNodesForName(extraInfoJPanel.getNavigationTag());
        List<SSFNode> nodesStem = root.getNodesForAttribVal("lex", extraInfoJPanel.getNavigationWord(), true);

        List<SSFNode> nodes = (List) UtilityFunctions.getIntersection(nodesTag, nodesStem);

        if(nodes == null || nodes.size() == 0)
            return;

        boolean beg = false;

        int back = 1;

        while(beg == false)
        {
            String bposString = extraInfoJPanel.getWPos2SPosMap().getPropertyValue((currentWPosition + 1 - back) + "");

            if(bposString == null || bposString.equals(posString) == false)
                beg = true;
            else
                back++;
        }

        int wIndex = back - 1;

        SSFNode currentNode = (SSFNode) nodes.get(wIndex);

        TreePath currentPath = new TreePath(currentNode.getPath());
        ssfPhraseJPanel.getJTree().setSelectionPath(currentPath);
        ssfPhraseJPanel.getJTree().scrollPathToVisible(currentPath);

        currentNode.isHighlighted(true);

//        editWordInfoExamples();
    }

    private void clearCurrentPosition(long annotationLevelsFlag) {
        if(UtilityFunctions.flagOn(annotationLevelsFlag, SSFCorpus.COMMENTS))
            commentsPT.modifyToken("", currentPosition);
        
//        sentence.getRoot().flatten();
        sentence.clearAnnotation(annotationLevelsFlag);
    }
    
    private void clearPosition(int pos) {
        int count = ssfStory.countSentences();
        
        if(pos >= 0 && pos < count) {
            commentsPT.modifyToken("", pos);
            SSFSentence sen = (SSFSentenceImpl) ssfStory.getSentence(pos);
            sen.getRoot().flatten();
        }
    }
    
    private void resetCurrentPosition() {
        try {
            SSFSentenceImpl storySentence = (SSFSentenceImpl) ssfStory.getSentence(currentPosition);
            sentence = (SSFSentence) storySentence.getCopy();
            dirty = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        displayCurrentPosition();
    }
    
    private void resetAll() {
        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);
        
        int count = ssfStory.countSentences();
        
        for(int i = 0; i < count; i++) {
            commentsPT.modifyToken("", i);
        }
        
        while(ssfStory.countSentences() > 0)
            ssfStory.removeSentence(0);
        
        configure();
        
        owner.setCursor(cursor);
        
        dirty = false;
    }
    
    private void clearAll() {
        Cursor cursor = owner.getCursor();
//        owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        owner.setCursor(Cursor.WAIT_CURSOR);
        
        int count = ssfStory.countSentences();
        
        for(int i = 0; i < count; i++)
            clearPosition(i);
        
        owner.setCursor(cursor);
    }
    
    public SSFStory getSSFStory() {
        return ssfStory;
    }
    
    public void setTaskProps(KeyValueProperties kvTaskProps) {
        this.kvTaskProps = kvTaskProps;
    }

    public void editSSFText(ActionEvent e) {
        save(null);
        
        if(editSSFText(true) == true)
            resetAll();    
    }
    
    public boolean editSSFText(boolean validateBeforeSave) {
        boolean validated = true;
        
        JDialog editTextDialog = new JDialog(this.getOwner(), sanchay.GlobalProperties.getIntlString("Edit_Text"), true);
        
        textFile = kvTaskProps.getPropertyValue("SSFCorpusStoryFile");
        charset = kvTaskProps.getPropertyValue("SSFCorpusCharset");
        
        if(SimpleStoryImpl.getCorpusType(textFile, sanchay.GlobalProperties.getIntlString("UTF-8")) == CorpusType.RAW
                && (SanchayLanguages.getLanguageCodeFromLECode(langEnc).equals("urd") == false
                && SanchayLanguages.getLanguageCodeFromLECode(langEnc).equals("kas") == false)) {
            File tmpFile = new File(textFile + sanchay.GlobalProperties.getIntlString(".tmp"));

            tmpFile.delete();
            
            try {
                UtilityFunctions.naiivePreprocessing(textFile, charset, tmpFile.getAbsolutePath(), charset, sanchay.GlobalProperties.getIntlString("hin::utf8"));
//                UtilityFunctions.moveFile(tmpFile.getAbsolutePath(), charset, textFile, charset);
                UtilityFunctions.trimSpaces(tmpFile.getAbsolutePath(), charset, textFile, charset);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            tmpFile.delete();
        }
        
        TextEditorJPanel editTextJPanel = new TextEditorJPanel(textFile, langEnc, charset, null, null, TextEditorJPanel.MINIMAL_MODE);
//	editTextJPanel.setMode(TextEditorJPanel.MINIMAL_MODE);
        editTextJPanel.validateBeforeSave(validateBeforeSave);
        
        editTextJPanel.setOwner(this.getOwner());
        editTextJPanel.setDialog(editTextDialog);
        
        editTextDialog.add(editTextJPanel);
        editTextDialog.setBounds(130, 30, 800, 700);
        
        editTextDialog.setVisible(true);
        
        if(editTextJPanel.isDirty() && validateBeforeSave) {
            validated = editTextJPanel.validateCorpusType(null);
        }
        
        return validated;
    }
    
    public void showStatistics(ActionEvent e)
    {
//        SanchayTableModel matches = getStats(ssfStory);
//
//        showSearchResults(matches, langEnc, 1);

        SanchayJDialog resultsJDialog = null;

        if(owner != null)
             resultsJDialog = DialogFactory.showDialog(CorpusStatisticsJPanel.class, owner, "Statistics", false);
        else if(dialog != null)
             resultsJDialog = DialogFactory.showDialog(CorpusStatisticsJPanel.class, dialog, "Statistics", false);

        UtilityFunctions.maxmize(resultsJDialog);

        CorpusStatisticsJPanel corpusStatisticsJPanel = (CorpusStatisticsJPanel) resultsJDialog.getJPanel();

        corpusStatisticsJPanel.initStats(langEnc, ssfStory);
    }

    public void showStatisticsInFiles(ActionEvent e)
    {
//        SanchayTableModel matches = getStats(ssfStory);
//
//        showSearchResults(matches, langEnc, 1);
        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);

        SanchayJDialog resultsJDialog = null;

        if(owner != null)
             resultsJDialog = DialogFactory.showDialog(CorpusStatisticsJPanel.class, owner, "Statistics", false);
        else if(dialog != null)
             resultsJDialog = DialogFactory.showDialog(CorpusStatisticsJPanel.class, dialog, "Statistics", false);

        UtilityFunctions.maxmize(resultsJDialog);

        selFiles = getSelectedFiles();

        CorpusStatisticsJPanel corpusStatisticsJPanel = (CorpusStatisticsJPanel) resultsJDialog.getJPanel();

        String cs = kvTaskProps.getPropertyValue("SSFCorpusCharset");

        selStories = SSFStoryImpl.readStories((File[]) selFiles, cs);

        corpusStatisticsJPanel.initStats(langEnc, selStories);

        owner.setCursor(cursor);
    }
    
    public void showComments(ActionEvent e)
    {
        if(commentJPanel.isVisible())
            commentJPanel.setVisible(false);
        else
            commentJPanel.setVisible(true);
    }
    
    public void joinSentence(ActionEvent e)
    {
        if(currentPosition == ssfStory.countSentences() - 1) {
            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("A_sentence_can_only_be_joined_with_the_next_sentence._You_are_at_the_last_sentence."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SSFSentence currentSentence = ssfStory.getSentence(currentPosition);
        SSFSentence nextSentence = ssfStory.getSentence(currentPosition + 1);
        
        SSFPhrase currentRoot = currentSentence.getRoot();
        SSFPhrase nextRoot = nextSentence.getRoot();
        
        String currentComment = commentsPT.getToken(currentPosition);
        String nextComment = commentsPT.getToken(currentPosition + 1);
        
        currentComment = currentComment + sanchay.GlobalProperties.getIntlString("._") + nextComment;
        commentsPT.modifyToken(currentComment, currentPosition);
        commentsPT.removeToken(currentPosition + 1);
        
        currentRoot.concat(nextRoot);
        
        ssfStory.removeSentence(currentPosition + 1);
        
        ssfStory.reallocateSentenceIDs();
        
        positionJComboBox.removeItemAt(positionJComboBox.getModel().getSize() - 1);
        
        resetCurrentPosition();
        
//	save();
    }

    public void splitSentence(ActionEvent e)
    {
        TreePath currentSelection = ssfPhraseJPanel.getJTree().getSelectionPath();
        
        if (currentSelection != null)
        {
            SanchayMutableTreeNode currentNode = (SanchayMutableTreeNode) (currentSelection.getLastPathComponent());
            
            if((currentNode.getParent() != null && currentNode.getParent().getParent() == null) == false) {
                JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("Wrong_selection_for_splitting_a_sentence."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            
            int splitAt = parent.getIndex(currentNode);
            
            SSFSentence currentSentence = ssfStory.getSentence(currentPosition);
            
            SSFSentence nextSentence = new SSFSentenceImpl();

            SSFProperties ssfp = SSFNode.getSSFProperties();

            String rootName = ssfp.getProperties().getPropertyValueForPrint("rootName");
            String chunkStart = ssfp.getProperties().getPropertyValueForPrint("chunkStart");
            
            try {
                SSFPhrase nextRoot = currentSentence.getRoot().splitPhrase(splitAt);
                nextRoot.setId("0");
                nextRoot.setLexData(chunkStart);
                nextRoot.setName(rootName);
                nextSentence.setRoot(nextRoot);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            ssfStory.insertSentence(nextSentence, currentPosition + 1);
            commentsPT.insertToken("", currentPosition + 1);
            
            ssfStory.reallocateNodeIDs();
            ssfStory.reallocateSentenceIDs();
            
            int count = positionJComboBox.getModel().getSize();
            String lastPos = "" + (count + 1);
            positionJComboBox.addItem(lastPos);
            
            resetCurrentPosition();
            
//            save(null);
        }
    }

    public void joinFiles(ActionEvent e)
    {
        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);
        
        selFiles = getSelectedFiles();
        selStories = new LinkedHashMap<File, SSFStory>();
        
        SSFStory joinedStory = new SSFStoryImpl();
        
        String charset = kvTaskProps.getPropertyValue("SSFCorpusCharset");
        
        if(selFiles != null && selFiles.length > 1) {
            int ccount = 0;
            
            for (int i = 0; i < selFiles.length; i++) {
                File file = (File) selFiles[i];
                
                SSFStory story = new SSFStoryImpl();
                selStories.put(file, story);
                story.setSSFFile(file.getAbsolutePath());
                
                try {
                    story.readFile(file.getAbsolutePath(), charset);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                joinedStory.addSentences(story);
            }
        } else {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_select_more_than_one_files_to_be_joined."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
        }
        
        String joinedStoryFile = "";
        
        try {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                joinedStoryFile = chooser.getSelectedFile().getAbsolutePath();
                
                String scharset = JOptionPane.showInputDialog(this, sanchay.GlobalProperties.getIntlString("Please_enter_the_charset:"), sanchay.GlobalProperties.getIntlString("UTF-8"));
                
                ((SSFStoryImpl) joinedStory).save(joinedStoryFile, scharset);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_saving_the_joined_file."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        owner.setCursor(cursor);
        JOptionPane.showMessageDialog(parentComponent, selFiles.length + 
                "files joined into one file:\n" + joinedStoryFile, sanchay.GlobalProperties.getIntlString("Joined_Files"), JOptionPane.INFORMATION_MESSAGE);
    }

    public void transferTags(ActionEvent e)
    {
        JOptionPane.showMessageDialog(parentComponent, "The number of source and target files and of the sentences\n"
                + "and the words in them should be the same.\n"
                + "Also, the corresponding files should be selected in the same order.\n"
                + "You will first be asked to select the source files,\n"
                + "and then the target files. The tags will be transferred from\n"
                + "the source files to the target files. Please make\n"
                + "sure that files are selected properly, otherwise\n"
                + "there may be data loss.", sanchay.GlobalProperties.getIntlString("Caution"), JOptionPane.INFORMATION_MESSAGE);
        
        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);
        
        File srcFiles[] = getSelectedFiles();
        
        File tgtFiles[] = getSelectedFiles();
        
        if(srcFiles.length != tgtFiles.length) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("The_number_of_source_and_target_files_and_of_the_sentences_and_the_words_in_them_should_be_the_same."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String charset = kvTaskProps.getPropertyValue("SSFCorpusCharset");
        
        if(srcFiles != null && tgtFiles.length > 0 && tgtFiles != null && tgtFiles.length > 0) {
            
            for (int i = 0; i < srcFiles.length; i++) {
                File srcFile = (File) srcFiles[i];
                File tgtFile = (File) tgtFiles[i];
                
                SSFStory srcStory = new SSFStoryImpl();
                SSFStory tgtStory = new SSFStoryImpl();
                
                try {
                    srcStory.readFile(srcFile.getAbsolutePath(), charset);
                    tgtStory.readFile(tgtFile.getAbsolutePath(), charset);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                SSFTextImpl.transferTags(tgtStory, srcStory);
                
                try {
                    tgtStory.save(tgtFile.getAbsolutePath(), charset);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        owner.setCursor(cursor);        
    }

    public void showMoreButtons(ActionEvent e)
    {
        if(secondCommandsJPanel.isVisible()) {
            secondCommandsJPanel.setVisible(false);
            thirdCommandsJPanel.setVisible(false);
            fourthCommandsJPanel.setVisible(false);
            fourthCommandsJPanel.setVisible(false);
        } else {
            secondCommandsJPanel.setVisible(true);
            thirdCommandsJPanel.setVisible(true);
            fourthCommandsJPanel.setVisible(true);
            fourthCommandsJPanel.setVisible(true);
        }
    }

    public void clear(ActionEvent e)
    {
        int ret = JOptionPane.showConfirmDialog(this, sanchay.GlobalProperties.getIntlString("Are_you_sure_you_want_to_clear_some_annotation_this_sentence?"), sanchay.GlobalProperties.getIntlString("Clear_Annotation_in_Sentence?"), JOptionPane.YES_NO_OPTION);
        
        if(ret == JOptionPane.NO_OPTION)
            return;
        
        JDialog alDialog = null;
        
        if(owner != null)
            alDialog = SSFAnnotationLevelsJPanel.showDialog(owner, sanchay.GlobalProperties.getIntlString("SSF_Annotation_Levels"), true);
        else if(dialog != null)
            alDialog = SSFAnnotationLevelsJPanel.showDialog(dialog, sanchay.GlobalProperties.getIntlString("SSF_Annotation_Levels"), true);
        
        long annotationLevelsFlag = ((SSFAnnotationLevelsJPanel.LocalDialog) alDialog).getAnnotationLevelsFlag();
        
        clearCurrentPosition(annotationLevelsFlag);
        
        displayCurrentPosition();
    }

    public void clearAll(ActionEvent e)
    {
        int ret = JOptionPane.showConfirmDialog(this, sanchay.GlobalProperties.getIntlString("Are_you_sure_you_want_to_clear_some_annotation_this_task?"), sanchay.GlobalProperties.getIntlString("Clear_Annotation_in_Task?"), JOptionPane.YES_NO_OPTION);
        
        if(ret == JOptionPane.NO_OPTION)
            return;
        
        JDialog alDialog = null;
        
        if(dialog != null)
            alDialog = SSFAnnotationLevelsJPanel.showDialog(dialog, sanchay.GlobalProperties.getIntlString("SSF_Annotation_Levels"), true);
        else if(owner != null)
            alDialog = SSFAnnotationLevelsJPanel.showDialog(owner, sanchay.GlobalProperties.getIntlString("SSF_Annotation_Levels"), true);
        
        long annotationLevelsFlag = ((SSFAnnotationLevelsJPanel.LocalDialog) alDialog).getAnnotationLevelsFlag();
        
        if(annotationLevelsFlag == SSFCorpus.NONE)
            return;
        
        if(UtilityFunctions.flagOn(annotationLevelsFlag, SSFCorpus.COMMENTS)) {
            for (int i = 0; i < commentsPT.countTokens(); i++)
                commentsPT.modifyToken("", i);
        }
        
        ssfStory.clearAnnotation(annotationLevelsFlag);
        resetCurrentPosition();
    }
    
    public void reset(ActionEvent e)
    {
        resetCurrentPosition();
    }
    
    public void resetAll(ActionEvent e)
    {
        int ret = JOptionPane.showConfirmDialog(this, sanchay.GlobalProperties.getIntlString("Are_you_sure_you_want_to_reset_everything?"), sanchay.GlobalProperties.getIntlString("Reset_All"), JOptionPane.YES_NO_OPTION);
        
        if(ret == JOptionPane.NO_OPTION)
            return;
        
        resetAll();
    }

    public void toggleTgtUTF8(boolean f) {
        if(f == true) // show UTF8
        {
            utf8Shown = true;
        } else // hide
        {
            utf8Shown = false;
        }
    }
    
    public Frame getOwner() {
        return owner;
    }
    
    public void setOwner(Frame frame) {
        owner = (JFrame) frame;
        owner.addWindowListener(this);
    }
    
    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    public void setDialog(JDialog d) {
        dialog = d;
    }
    
    public JMenuBar getJMenuBar() {
        return null;
    }
    
    public JPopupMenu getJPopupMenu() {
        return null;
    }
    
    public JToolBar getJToolBar() {
        return null;
    }
    
    public void setTaskName(String tn) {
        taskName = tn;
    }

    public void configure(SSFStory story)
    {
        Cursor cursor = null;

        if(owner != null) {
            owner.getCursor();
            owner.setCursor(Cursor.WAIT_CURSOR);
        } else if(parentComponent != null) {
            parentComponent.getCursor();
            parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        String taskKVPropFile = "";
        String taskKVPropCharset = "";

        try {
            String ssfp = kvTaskProps.getPropertyValue("SSFPropFile");

            String fsm = kvTaskProps.getPropertyValue("MFeaturesFile");
            String fso = kvTaskProps.getPropertyValue("OFeaturesFile");
            String fsps = kvTaskProps.getPropertyValue("PAttributesFile");
            String fsd = kvTaskProps.getPropertyValue("DAttributesFile");
            String fss = kvTaskProps.getPropertyValue("SAttributesFile");
            String fsp = kvTaskProps.getPropertyValue("FSPropFile");

            FSProperties fsProps = new FSProperties();
            SSFProperties ssfProps = new SSFProperties();

            fsProps.read(fsm, fso, fsp, fsps, fsd, fss, sanchay.GlobalProperties.getIntlString("UTF-8"));
            ssfProps.read(ssfp, sanchay.GlobalProperties.getIntlString("UTF-8"));

            SSFNode.setSSFProperties(ssfProps);

            langEnc = kvTaskProps.getPropertyValue("Language");

            textFile = kvTaskProps.getPropertyValue("SSFCorpusStoryFile");
            title = (new File(textFile)).getName();

            if(dialog != null)
            {
                dialog.setTitle(sanchay.GlobalProperties.getIntlString("Sanchay:_") + ClientType.SYNTACTIC_ANNOTATION.toString() + ": " + title);
            }
            else if(owner != null)
            {
                owner.setTitle(sanchay.GlobalProperties.getIntlString("Sanchay:_") + ClientType.SYNTACTIC_ANNOTATION.toString() + ": " + title);
            }

//            String storyUTF8 = kvTaskProps.getPropertyValue("SSFCorpusStoryUTF8File");

//            ssfStory.readFile(textFile);
//            ssfUTF8Story.readFile(storyUTF8, ssfProps, fsProps)

            if(sanchayBackup != null)
                FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);

            sanchayBackup = new SanchayBackup();
            FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);

            morphTagsKVP = new KeyValueProperties(kvTaskProps.getPropertyValue("MorphTagsFile"), kvTaskProps.getPropertyValue("POSTagsCharset"));

//            posTagsPT = new PropertyTokens(kvTaskProps.getPropertyValue("POSTagsFile"), kvTaskProps.getPropertyValue("POSTagsCharset"));
//            phraseNamesPT = new PropertyTokens(kvTaskProps.getPropertyValue("PhraseNamesFile"), kvTaskProps.getPropertyValue("PhraseNamesCharset"));

            posTagsPT = HierarchicalTagsJPanel.getTags(HierarchicalTagsJPanel.POS_TAGS, corpusType, kvTaskProps.getPropertyValue("POSTagsFile"), langEnc, kvTaskProps.getPropertyValue("POSTagsCharset"));
            phraseNamesPT = HierarchicalTagsJPanel.getTags(HierarchicalTagsJPanel.PHRASE_NAMES, corpusType, kvTaskProps.getPropertyValue("PhraseNamesFile"), langEnc, kvTaskProps.getPropertyValue("PhraseNamesCharset"));

            syntacticAnnotationValidator = new SyntacticAnnotationValidator(posTagsPT, morphTagsKVP, phraseNamesPT);

            int senCount = ssfStory.countSentences();
            if(senCount > 0) {
                if(ssfPhraseJPanel != null)
                    jTreeViewJPanel.remove(ssfPhraseJPanel);

                //            ssfPhraseJPanel = new SancSFPhraseJPanel(new SSFPhrase(), false);
                ssfPhraseJPanel = SanchayTreeJPanel.createSSFPhraseJPanel(ssfStory.getSentence(0).getRoot(), phraseNamesPT, posTagsPT, langEnc);

                ssfPhraseJPanel.setFontSizes(Integer.parseInt(stateKVProps.getPropertyValue("CurrentFontSize")));

                if(propbankMode)
                {
                    ssfPhraseJPanel.showNodeEditor(false);
                    ssfPhraseJPanel.setPropbankMode(propbankMode);
                }
                
                if(alignmentMode)
                {
                    ssfPhraseJPanel.setAlignmentMode(true);
                }

                ssfPhraseJPanel.setOwner(getOwner());
                ssfPhraseJPanel.setDialog(dialog);

                ssfPhraseJPanel.getJTree().addTreeSelectionListener(new TreeSelectionListener() {

                    @Override
                    public void valueChanged(TreeSelectionEvent e)
                    {
//                        editWordInfoExamples();
                    }
                });

                jTreeViewJPanel.add(ssfPhraseJPanel, java.awt.BorderLayout.CENTER);

                //	    ssfPhraseJPanel.setPOSTags(posTagsPT.getCopyOfTokens());
                //	    ssfPhraseJPanel.setPhraseNames(phraseNamesPT.getCopyOfTokens());

//                topJPanel.add(ssfPhraseJPanel, java.awt.BorderLayout.CENTER);
                currentPosition = 0;

                File commentsFile = new File(kvTaskProps.getPropertyValue("TaskPropFile") + ".comments");

                if(commentsFile.exists()) {
                    try {
                        commentsPT = new PropertyTokens(commentsFile.getAbsolutePath(), sanchay.GlobalProperties.getIntlString("UTF-8"));

                        if(senCount != commentsPT.countTokens()) {
                            commentsPT = new PropertyTokens(senCount);

                            for(int i = 1; i <= senCount; i++) {
                                commentsPT.addToken("");
                            }
                        }
                    } catch(FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    commentsPT = new PropertyTokens(senCount);

                    for(int i = 1; i <= senCount; i++) {
                        commentsPT.addToken("");
                    }
                }

                Vector pvec = new Vector(senCount);

                for(int i = 1; i <= senCount; i++) {
                    pvec.add(Integer.toString(i));
                }

                positions = new DefaultComboBoxModel(pvec);
                positionJComboBox.setModel(positions);

                comments = new DefaultComboBoxModel(commentsPT.getTypes());
                comments.removeElement("");
                commentJComboBox.setModel(comments);

                extraInfoJPanel.initWordNavigationList();
                extraInfoJPanel.initTagNavigationList();
                initPropbank();

                extraInfoJPanel.fillArgActions();
                extraInfoJPanel.setSSFPhraseJPanel(ssfPhraseJPanel);
                PropbankInfoJPanel.loadShortcuts(this);

                String pos = kvTaskProps.getPropertyValue("CurrentPosition");

                int cp = 0;
                int cwp = 0;

                try {
                    if(propbankMode)
                    {
                        if(extraInfoJPanel.getSPos2WPosMap() == null)
                        {
                            if(owner != null) {
                                ((JFrame) owner).setCursor(cursor);
                            } else if(parentComponent != null) {
                                parentComponent.setCursor(cursor);
                            }

                            return;
                        }
                        
                        ((SSFTreeCellRendererNew) ssfPhraseJPanel.getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.PROPBANK_ANNOTATION);

                        queryJButton.setEnabled(false);
                        queryInFilesJButton.setEnabled(false);

                        ssfPhraseJPanel.showControlTabs(false);

                        extraInfoJPanel.setVisible(true);

                        cwp = Integer.parseInt(extraInfoJPanel.getSPos2WPosMap().getPropertyValue(pos));
                        cp = Integer.parseInt(pos);
                    }
                    else
                    {
                        ((SSFTreeCellRendererNew) ssfPhraseJPanel.getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);

                        queryJButton.setEnabled(true);
                        queryInFilesJButton.setEnabled(true);

                        ssfPhraseJPanel.showControlTabs(true);

                        extraInfoJPanel.setVisible(false);

                        cp = Integer.parseInt(pos);
                    }

                    setCurrentPosition(cp - 1, cwp - 1);
                    comment = commentsPT.getToken(cp - 1);
                } catch(NumberFormatException e) {
                    setCurrentPosition(0, 0);
                    comment = commentsPT.getToken(0);
                }

            } else {
                JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_in_task_properties_for_the_task:_") + taskName + sanchay.GlobalProperties.getIntlString("._Unable_to_load_any_sentences."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_in_task_properties_for_the_task:_") + taskName, sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        ssfPhraseJPanel.setCurrentTreeEditTab(stateKVProps.getPropertyValue("CurrentTreeEditTab"));

        if(owner != null) {
            ((JFrame) owner).setCursor(cursor);
        } else if(parentComponent != null) {
            parentComponent.setCursor(cursor);
        }
    }
    
    public void configure()
    {
        Cursor cursor = null;
        
        if(owner != null) {
            owner.getCursor();
            owner.setCursor(Cursor.WAIT_CURSOR);
        } else if(parentComponent != null) {
            parentComponent.getCursor();
            parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        
        String taskKVPropFile = "";
        String taskKVPropCharset = "";
        
        if(kvTaskProps == null) {
//            AnnotationClient owner = (AnnotationClient) getOwner();
//            propman = owner.getPropertiesManager();
            
//            taskList = owner.getTaskList();
            taskList = selectTaskJPanel.getTaskList();
            Vector rows = taskList.getRows(sanchay.GlobalProperties.getIntlString("TaskName"), taskName);
            
            if(rows.size() != 1) {
                JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Not_a_valid_task_name:_") + taskName, sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                dialog.setVisible(false);
            }
            
            String cols[] = {"TaskKVPropFile", "TaskKVPropCharset"};
            Vector vals = taskList.getValues("TaskName", taskName, cols);
            
            vals = (Vector) vals.get(0);
            taskKVPropFile = (String) vals.get(0);
            taskKVPropCharset = (String) vals.get(1);
            
            try {
                kvTaskProps = new KeyValueProperties(taskKVPropFile, taskKVPropCharset);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        try {
            String ssfp = kvTaskProps.getPropertyValue("SSFPropFile");
            
            String fsm = kvTaskProps.getPropertyValue("MFeaturesFile");
            String fso = kvTaskProps.getPropertyValue("OFeaturesFile");
            String fsps = kvTaskProps.getPropertyValue("PAttributesFile");
            String fsd = kvTaskProps.getPropertyValue("DAttributesFile");
            String fss = kvTaskProps.getPropertyValue("SAttributesFile");
            String fsp = kvTaskProps.getPropertyValue("FSPropFile");
            
            FSProperties fsProps = new FSProperties();
            SSFProperties ssfProps = new SSFProperties();
            
            fsProps.read(fsm, fso, fsp, fsps, fsd, fss, sanchay.GlobalProperties.getIntlString("UTF-8"));
            ssfProps.read(ssfp, sanchay.GlobalProperties.getIntlString("UTF-8"));
            
            SSFNode.setSSFProperties(ssfProps);
            
            langEnc = kvTaskProps.getPropertyValue("Language");

            setLangEnc(langEnc);
            
            textFile = kvTaskProps.getPropertyValue("SSFCorpusStoryFile");

            corpusType = SimpleStoryImpl.getCorpusType(textFile, sanchay.GlobalProperties.getIntlString("UTF-8"));
            
            if(corpusType == CorpusType.RAW) {
                JOptionPane.showMessageDialog(parentComponent, "The task you have selected contains raw text. You\nnow have to make sure that each sentence is on\na separate line to ensure correct annotation.", sanchay.GlobalProperties.getIntlString("Raw_Text"), JOptionPane.INFORMATION_MESSAGE);
                
                editSSFText(false);
                
                if(langEnc.equals("hin::utf8")) {
                    int ret = JOptionPane.showConfirmDialog(this, sanchay.GlobalProperties.getIntlString("Would_like_the_text_to_be_POS_tagged?"), sanchay.GlobalProperties.getIntlString("Perform_POS_Tagging?"), JOptionPane.YES_NO_OPTION);
                    
                    if(ret == JOptionPane.YES_OPTION)
                        annotate(MLCorpusConverter.TAG_FORMAT);
                    
                    ret = JOptionPane.showConfirmDialog(this, sanchay.GlobalProperties.getIntlString("Would_like_the_text_to_be_chunked?"), sanchay.GlobalProperties.getIntlString("Perform_Chunking?"), JOptionPane.YES_NO_OPTION);
                    
                    if(ret == JOptionPane.YES_OPTION)
                        annotate(MLCorpusConverter.CHUNK_FORMAT);

                    corpusType = CorpusType.SSF_FORMAT;
                }
            }

            GlobalUtils.setClientCorpusMode(this, corpusType);
            
//            String storyUTF8 = kvTaskProps.getPropertyValue("SSFCorpusStoryUTF8File");

            String user = System.getProperty("user.name");

            if((!propbankMode && GlobalProperties.getClientModes().getPropertyValue("SYNTACTIC_ANNOTATION").equals("TASK_MODE"))
                    || (propbankMode && GlobalProperties.getClientModes().getPropertyValue("PROPBANK_ANNOTATION").equals("TASK_MODE")))
            {
                String userTextFile = textFile + "-" + user;

                if((new File(userTextFile)).exists())
                {
                    textFile = userTextFile;
                }
            }

            title = (new File(textFile)).getName();

            if(dialog != null)
            {
                dialog.setTitle(sanchay.GlobalProperties.getIntlString("Sanchay:_") + ClientType.SYNTACTIC_ANNOTATION.toString() + ": " + title);
            }
            else if(owner != null)
            {
                owner.setTitle(sanchay.GlobalProperties.getIntlString("Sanchay:_") + ClientType.SYNTACTIC_ANNOTATION.toString() + ": " + title);
            }
            
            ssfStory.readFile(textFile);
//            ssfUTF8Story.readFile(storyUTF8, ssfProps, fsProps)
            
            if(sanchayBackup != null)
                FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);

            sanchayBackup = new SanchayBackup();
            FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);

            morphTagsKVP = new KeyValueProperties(kvTaskProps.getPropertyValue("MorphTagsFile"), kvTaskProps.getPropertyValue("POSTagsCharset"));
//            posTagsPT = new PropertyTokens(kvTaskProps.getPropertyValue("POSTagsFile"), kvTaskProps.getPropertyValue("POSTagsCharset"));
//            phraseNamesPT = new PropertyTokens(kvTaskProps.getPropertyValue("PhraseNamesFile"), kvTaskProps.getPropertyValue("PhraseNamesCharset"));

            posTagsPT = HierarchicalTagsJPanel.getTags(HierarchicalTagsJPanel.POS_TAGS, corpusType, kvTaskProps.getPropertyValue("POSTagsFile"), langEnc, kvTaskProps.getPropertyValue("POSTagsCharset"));
            phraseNamesPT = HierarchicalTagsJPanel.getTags(HierarchicalTagsJPanel.PHRASE_NAMES, corpusType, kvTaskProps.getPropertyValue("PhraseNamesFile"), langEnc, kvTaskProps.getPropertyValue("PhraseNamesCharset"));

            syntacticAnnotationValidator = new SyntacticAnnotationValidator(posTagsPT, morphTagsKVP, phraseNamesPT);
            
            int senCount = ssfStory.countSentences();
            if(senCount > 0) {
                if(ssfPhraseJPanel != null)
                    jTreeViewJPanel.remove(ssfPhraseJPanel);
                
                //            ssfPhraseJPanel = new SancSFPhraseJPanel(new SSFPhrase(), false);
                ssfPhraseJPanel = SanchayTreeJPanel.createSSFPhraseJPanel(ssfStory.getSentence(0).getRoot(), phraseNamesPT, posTagsPT, langEnc);

                ssfPhraseJPanel.setFontSizes(Integer.parseInt(stateKVProps.getPropertyValue("CurrentFontSize")));

                if(propbankMode)
                {
                    ssfPhraseJPanel.showNodeEditor(false);
                    ssfPhraseJPanel.setPropbankMode(propbankMode);
                }
                
                if(alignmentMode)
                {
                    ssfPhraseJPanel.setAlignmentMode(true);
                }

                ssfPhraseJPanel.setOwner(getOwner());
                ssfPhraseJPanel.setDialog(dialog);

                ssfPhraseJPanel.getJTree().addTreeSelectionListener(new TreeSelectionListener() {

                    @Override
                    public void valueChanged(TreeSelectionEvent e)
                    {
//                        editWordInfoExamples();
                    }
                });
                
                jTreeViewJPanel.add(ssfPhraseJPanel, java.awt.BorderLayout.CENTER);
                
                //	    ssfPhraseJPanel.setPOSTags(posTagsPT.getCopyOfTokens());
                //	    ssfPhraseJPanel.setPhraseNames(phraseNamesPT.getCopyOfTokens());
                
//                topJPanel.add(ssfPhraseJPanel, java.awt.BorderLayout.CENTER);
                currentPosition = 1;
                
                File commentsFile = new File(kvTaskProps.getPropertyValue("TaskPropFile") + ".comments");
                
                if(commentsFile.exists()) {
                    try {
                        commentsPT = new PropertyTokens(commentsFile.getAbsolutePath(), sanchay.GlobalProperties.getIntlString("UTF-8"));
                        
                        if(senCount != commentsPT.countTokens()) {
                            commentsPT = new PropertyTokens(senCount);
                            
                            for(int i = 1; i <= senCount; i++) {
                                commentsPT.addToken("");
                            }
                        }
                    } catch(FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    commentsPT = new PropertyTokens(senCount);
                    
                    for(int i = 1; i <= senCount; i++) {
                        commentsPT.addToken("");
                    }
                }
                
                Vector pvec = new Vector(senCount);
                
                for(int i = 1; i <= senCount; i++) {
                    pvec.add(Integer.toString(i));
                }
                
                positions = new DefaultComboBoxModel(pvec);
                positionJComboBox.setModel(positions);
                
                comments = new DefaultComboBoxModel(commentsPT.getTypes());
                comments.removeElement("");
                commentJComboBox.setModel(comments);

                extraInfoJPanel.initWordNavigationList();
                extraInfoJPanel.initTagNavigationList();
                initPropbank();

                extraInfoJPanel.fillArgActions();
                extraInfoJPanel.setSSFPhraseJPanel(ssfPhraseJPanel);
                PropbankInfoJPanel.loadShortcuts(this);
                
                String pos = kvTaskProps.getPropertyValue("CurrentPosition");

                int cp = 0;
                int cwp = 0;

                try {
                    if(propbankMode)
                    {
                        if(extraInfoJPanel.getSPos2WPosMap() == null)
                        {
                            if(owner != null) {
                                ((JFrame) owner).setCursor(cursor);
                            } else if(parentComponent != null) {
                                parentComponent.setCursor(cursor);
                            }

                            return;
                        }

                        ((SSFTreeCellRendererNew) ssfPhraseJPanel.getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.PROPBANK_ANNOTATION);

                        queryJButton.setEnabled(false);
                        queryInFilesJButton.setEnabled(false);

                        ssfPhraseJPanel.showControlTabs(false);

                        extraInfoJPanel.setVisible(true);

                        propbankModeJCheckBox.setSelected(true);

                        cwp = Integer.parseInt(extraInfoJPanel.getSPos2WPosMap().getPropertyValue(pos));
                        cp = Integer.parseInt(pos);
                    }
                    else
                    {
                        ((SSFTreeCellRendererNew) ssfPhraseJPanel.getJTree().getCellRenderer()).setMode(SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION);

                        queryJButton.setEnabled(true);
                        queryInFilesJButton.setEnabled(true);

                        ssfPhraseJPanel.showControlTabs(true);

                        extraInfoJPanel.setVisible(false);

                        cp = Integer.parseInt(pos);
                    }

                    setCurrentPosition(cp - 1, cwp - 1);
                    comment = commentsPT.getToken(cp - 1);
                } catch(NumberFormatException e) {
                    setCurrentPosition(0, 0);
                    comment = commentsPT.getToken(0);
                }                
                
            } else {
                JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_in_task_properties_for_the_task:_") + taskName + sanchay.GlobalProperties.getIntlString("._Unable_to_load_any_sentences."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_in_task_properties_for_the_task:_") + taskName, sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        ssfPhraseJPanel.setCurrentTreeEditTab(stateKVProps.getPropertyValue("CurrentTreeEditTab"));
        
        if(owner != null) {
            ((JFrame) owner).setCursor(cursor);
        } else if(parentComponent != null) {
            parentComponent.setCursor(cursor);
        }
    }
    
    public void convertToXML(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        PrintStream ps = new PrintStream(f, charset);
        printXML(ps);
    }
    
    public void printXML(PrintStream ps) {
    }
    
    public String getXML(int pos) {
        return null;
    }
    
    public void save(ActionEvent e) {

        if((propbankMode && GlobalProperties.getClientModes().getPropertyValue("PROPBANK_ANNOTATION").equals("TASK_MODE"))
                || (!propbankMode && GlobalProperties.getClientModes().getPropertyValue("SYNTACTIC_ANNOTATION").equals("TASK_MODE")))
        {
            if(SelectTaskJPanel.isOwnTask(taskList, taskName) == false)
            {
                int retVal = JOptionPane.showConfirmDialog(parentComponent, "Do you want to claim this task?",
                        "Claim Task", JOptionPane.YES_NO_OPTION);

                if(retVal == JOptionPane.YES_OPTION)
                {
                    claimTask();
                }
                else
                {
                    JOptionPane.showMessageDialog(parentComponent, "You can save this task only if you have claimed it first.", sanchay.GlobalProperties.getIntlString("Error"),
                            JOptionPane.ERROR_MESSAGE);
                    
                    return;
                }
            }
        }

        Cursor cursor = getParent().getCursor();
        getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        storeCurrentPosition();
        
        try {
            kvTaskProps.addProperty("CurrentPosition", Integer.toString(currentPosition + 1));
            kvTaskProps.save(kvTaskProps.getPropertyValue("TaskPropFile"), kvTaskProps.getPropertyValue("TaskPropCharset"));
            
            int count = ssfStory.countSentences();
            for(int i = 0; i < count; i++) {
                String cmt = commentsPT.getToken(i);
                String spstr[] = cmt.split("[\n]");
                
                cmt = "";
                for(int j = 0; j < spstr.length; j++) {
                    if(j < spstr.length - 1)
                        cmt += spstr[j] + " ";
                    else
                        cmt += spstr[j];
                }
                
                commentsPT.modifyToken(cmt, i);
            }
            
            commentsPT.save(kvTaskProps.getPropertyValue("TaskPropFile") + ".comments", sanchay.GlobalProperties.getIntlString("UTF-8"));

            SanchayBackup.backup(kvTaskProps.getPropertyValue("SSFCorpusStoryFile"));

            String user = System.getProperty("user.name");

            if((propbankMode && GlobalProperties.getClientModes().getPropertyValue("PROPBANK_ANNOTATION").equals("TASK_MODE"))
                    || (!propbankMode && GlobalProperties.getClientModes().getPropertyValue("SYNTACTIC_ANNOTATION").equals("TASK_MODE")))
                ssfStory.save(kvTaskProps.getPropertyValue("SSFCorpusStoryFile") + "-" + user,
                    kvTaskProps.getPropertyValue("SSFCorpusCharset"));
            else
                ssfStory.save(kvTaskProps.getPropertyValue("SSFCorpusStoryFile"),
                    kvTaskProps.getPropertyValue("SSFCorpusCharset"));
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_while_saving."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        
        // May be needed
//        setCurrentPosition(currentPosition + 1);
        setCurrentPosition(currentPosition, currentWPosition);
        
//        dialog.setVisible(false);
        
        getParent().setCursor(cursor);
    }

    public void saveAs(ActionEvent e)
    {
        if(propbankMode)
        {
            JOptionPane.showMessageDialog(parentComponent, "Not available in the task mode.", sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        Enumeration enm = CorpusType.elements();
        Vector corpusTypes = new Vector(CorpusType.size());
        
        while(enm.hasMoreElements()) {
            CorpusType ctype = (CorpusType) enm.nextElement();
            corpusTypes.add(ctype);
        }
        
        CorpusType selectedCorpusType = (CorpusType) JOptionPane.showInputDialog(this,
                sanchay.GlobalProperties.getIntlString("Select_corpus_type_for_saving"), sanchay.GlobalProperties.getIntlString("Corpus_Type"), JOptionPane.INFORMATION_MESSAGE, null,
                corpusTypes.toArray(), CorpusType.SSF_FORMAT);
        
        saveAs(selectedCorpusType);        
    }
    
    public void saveAs(CorpusType ctype) {
        String curDir = null;
        
        if(kvTaskProps == null)
            return;
        
        String textFileAs = kvTaskProps.getPropertyValue("SSFCorpusStoryFile");
        
        if(textFileAs != null && !textFileAs.equals("") && !textFileAs.equals(sanchay.GlobalProperties.getIntlString("Untitled"))) {
            File tfile = new File(textFileAs);
            
            if(tfile.exists()) {
                curDir = tfile.getParentFile().getAbsolutePath();
            }
        } else
            curDir = stateKVProps.getPropertyValue("CurrentDir");

        if(ctype == null)
            return;

        if(ctype.equals(CorpusType.RAW))
            saveRawTextAs(curDir);
        else if(ctype.equals(CorpusType.POS_TAGGED))
            savePOSTaggedAs(curDir);
        else if(ctype.equals(CorpusType.CHUNKED))
            saveBracketFormAs(curDir);
        else if(ctype.equals(CorpusType.SSF_FORMAT))
        {
            textFileAs = saveSSFAs(curDir);
            kvTaskProps.addProperty("SSFCorpusStoryFile", textFileAs);
            title = (new File(textFileAs)).getName();

            if(dialog != null)
            {
                dialog.setTitle(sanchay.GlobalProperties.getIntlString("Sanchay:_") + ClientType.SYNTACTIC_ANNOTATION.toString() + ": " + title);
            }
            else if(owner != null)
            {
                owner.setTitle(sanchay.GlobalProperties.getIntlString("Sanchay:_") + ClientType.SYNTACTIC_ANNOTATION.toString() + ": " + title);
            }
        }
        else if(ctype.equals(CorpusType.XML_FORMAT)) {
            saveXMLAs(curDir);
//            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("XML_corpus_type_not_yet_implemented."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public String saveSSFAs(String curDir) {
        String storyFile = textFile;
        
        try {
            JFileChooser chooser = null;
            
            if(curDir != null)
                chooser = new JFileChooser(curDir);
            else
                chooser = new JFileChooser();
            
            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                storyFile = chooser.getSelectedFile().getAbsolutePath();
                
                String charset = JOptionPane.showInputDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_enter_the_charset:"), sanchay.GlobalProperties.getIntlString("UTF-8"));
                
                ((SSFStoryImpl) ssfStory).save(storyFile, charset);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return storyFile;
    }
    
    public String saveBracketFormAs(String curDir) {
        String storyFile = textFile;

        try {
            JFileChooser chooser = null;
            
            if(curDir != null)
                chooser = new JFileChooser(curDir);
            else
                chooser = new JFileChooser();
            
            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                storyFile = chooser.getSelectedFile().getAbsolutePath();
                
                String charset = JOptionPane.showInputDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_enter_the_charset:"), sanchay.GlobalProperties.getIntlString("UTF-8"));
                
                String spaces = JOptionPane.showInputDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_enter_the_number_of_spaces_after_the_chunk_end:"), "5");
                
                ((SSFStoryImpl) ssfStory).saveBracketForm(storyFile, charset, Integer.parseInt(spaces));
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return storyFile;
    }
    
    public String savePOSTaggedAs(String curDir) {
        String storyFile = textFile;

        try {
            JFileChooser chooser = null;
            
            if(curDir != null)
                chooser = new JFileChooser(curDir);
            else
                chooser = new JFileChooser();
            
            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                storyFile = chooser.getSelectedFile().getAbsolutePath();
                
                String charset = JOptionPane.showInputDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_enter_the_charset:"), sanchay.GlobalProperties.getIntlString("UTF-8"));
                
                ((SSFStoryImpl) ssfStory).savePOSTagged(storyFile, charset);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return storyFile;
    }
    
    public String saveRawTextAs(String curDir) {
        String storyFile = textFile;

        try {
            JFileChooser chooser = null;
            
            if(curDir != null)
                chooser = new JFileChooser(curDir);
            else
                chooser = new JFileChooser();
            
            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                storyFile = chooser.getSelectedFile().getAbsolutePath();
                
                String charset = JOptionPane.showInputDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_enter_the_charset:"), sanchay.GlobalProperties.getIntlString("UTF-8"));
                
                ((SSFStoryImpl) ssfStory).saveRawText(storyFile, charset);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return storyFile;
    }

    public String saveXMLAs(String curDir) {
        String storyFile = textFile;

        try {
            JFileChooser chooser = null;

            if(curDir != null)
                chooser = new JFileChooser(curDir);
            else
                chooser = new JFileChooser();

            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                storyFile = chooser.getSelectedFile().getAbsolutePath();

                String charset = JOptionPane.showInputDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_enter_the_charset:"), sanchay.GlobalProperties.getIntlString("UTF-8"));

                ((SSFStoryImpl) ssfStory).saveXML(storyFile, charset);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Error_resetting_from_file._Perhaps_the_file_name_and_the_charset_are_not_defined."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return storyFile;
    }

    protected void openInit(String path, String langEnc)
    {
        this.langEnc = langEnc;

        kvTaskProps = new KeyValueProperties();

        File cfile = new File(path);

        if(cfile.exists() == false)
            return;

        taskName = cfile.getName();

        String taskPropFile = "task-" + taskName;
        File tpfile = new File(cfile.getParent(), taskPropFile);
        taskPropFile = tpfile.getAbsolutePath();

        kvTaskProps.addProperty("Language", langEnc);
        kvTaskProps.addProperty("TaskName", taskName);
        kvTaskProps.addProperty("TaskPropFile", taskPropFile);
        kvTaskProps.addProperty("TaskPropCharset", sanchay.GlobalProperties.getIntlString("UTF-8"));
        kvTaskProps.addProperty("SSFPropFile", GlobalProperties.resolveRelativePath("props/ssf-props.txt"));
        kvTaskProps.addProperty("SSFPropCharset", sanchay.GlobalProperties.getIntlString("UTF-8"));
        kvTaskProps.addProperty("FSPropFile", GlobalProperties.resolveRelativePath("props/fs-props.txt"));
        kvTaskProps.addProperty("FSPropCharset", sanchay.GlobalProperties.getIntlString("UTF-8"));
        kvTaskProps.addProperty("MFeaturesFile", GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"));
        kvTaskProps.addProperty("MFeaturesCharset", sanchay.GlobalProperties.getIntlString("UTF-8"));
        kvTaskProps.addProperty("OFeaturesFile", GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"));
        kvTaskProps.addProperty("PAttributesFile", GlobalProperties.resolveRelativePath("props/ps-attribs.txt"));
        kvTaskProps.addProperty("DAttributesFile", GlobalProperties.resolveRelativePath("props/dep-attribs.txt"));
        kvTaskProps.addProperty("SAttributesFile", GlobalProperties.resolveRelativePath("props/sem-attribs.txt"));
        kvTaskProps.addProperty("SSFCorpusStoryFile", GlobalProperties.resolveRelativePath(path));
        kvTaskProps.addProperty("SSFCorpusCharset", charset);
        kvTaskProps.addProperty("SSFCorpusStoryUTF8File", GlobalProperties.resolveRelativePath(path));

        kvTaskProps.addProperty("POSTagsFile", GlobalProperties.resolveRelativePath("workspace/syn-annotation/pos-tags.txt"));
        kvTaskProps.addProperty("MorphTagsFile", GlobalProperties.resolveRelativePath("workspace/syn-annotation/morph-tags.txt"));
        kvTaskProps.addProperty("POSTagsCharset", sanchay.GlobalProperties.getIntlString("UTF-8"));

        kvTaskProps.addProperty("PhraseNamesFile", GlobalProperties.resolveRelativePath("workspace/syn-annotation/phrase-names.txt"));
        kvTaskProps.addProperty("PhraseNamesCharset", sanchay.GlobalProperties.getIntlString("UTF-8"));

        kvTaskProps.addProperty("CurrentPosition", "1");
    }
    
    public void openFile(String path, String langEnc, String charset) {

        openInit(path, langEnc);
        
        configure();
    }

    public void convertEncoding(ActionEvent e) {
        String initLangEnc = GlobalProperties.getIntlString("hin::utf8");
        String fromEncoding = (String) JOptionPane.showInputDialog(parentComponent,
                GlobalProperties.getIntlString("Select_the_source_language-encoding"), GlobalProperties.getIntlString("Language_and_Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                EncodingConverterUtils.getFromEncodings(), initLangEnc);

        String toEncoding = (String) JOptionPane.showInputDialog(parentComponent,
                GlobalProperties.getIntlString("Select_the_target_language-encoding"), GlobalProperties.getIntlString("Language_and_Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                EncodingConverterUtils.getToEncodings(), initLangEnc);

        SSFStoryConvertEncoding  ssfStoryConvertEncoding = new  SSFStoryConvertEncoding();
        ssfStoryConvertEncoding.init(fromEncoding, toEncoding);

        if(fromEncoding == null || toEncoding == null)
            return;

        int retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("Do_you_want_to_save_the_currently_open_file?"), GlobalProperties.getIntlString("Converting_the_Encoding"), JOptionPane.YES_NO_OPTION);

        if(retVal == JOptionPane.YES_OPTION) {
            save(e);

            String oldTextFile = textFile;

            saveAs(e);

            try {
                ssfStoryConvertEncoding.convertEncoding(ssfStory, "NULL");
                resetCurrentPosition();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            
            try {
                ssfStoryConvertEncoding.convertEncoding(ssfStory, "NULL");
                resetCurrentPosition();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void showBottomJPanel(boolean s) {
        bottomJPanel.setVisible(s);
    }
    
    public void showCommandButtons(boolean s) {
        commandsJPanel.setVisible(s);
    }
    
    public void showCommentJPanel(boolean s) {
        commentJPanel.setVisible(s);
    }
    
    public void showNestedFSButton(boolean s) {
        nestedFSJCheckBox.setVisible(s);
    }
    
    public SanchayTreeJPanel getSanchayTreeJPanel() {
        return ssfPhraseJPanel;
    }
    
    public void annotate(int type) {
        Cursor cursor = owner.getCursor();
        owner.setCursor(Cursor.WAIT_CURSOR);
        
        textFile = kvTaskProps.getPropertyValue("SSFCorpusStoryFile");
        charset = kvTaskProps.getPropertyValue("SSFCorpusCharset");
        
        annotationMain = new CRFAnnotationMain(type);
        
        String baseDir = "";
        
        if(type == MLCorpusConverter.TAG_FORMAT) {
            baseDir = GlobalProperties.resolveRelativePath("data/automatic-annotation/pos-tagging/learntModels");
        } else if(type == MLCorpusConverter.CHUNK_FORMAT) {
            baseDir = GlobalProperties.resolveRelativePath("data/automatic-annotation/chunking/learntModels");
        } else if(type == MLCorpusConverter.CHUNK_FEATURE_FORMAT) {
            baseDir = GlobalProperties.resolveRelativePath("data/automatic-annotation/ner/learntModels");
        }
        if(baseDir == null || baseDir.equals("") || (new File(baseDir)).exists() == false) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Path_given_for_the_training_model_doesn't_exist"), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            owner.setCursor(cursor);
            return;
        }
        
        annotationMain.setFormatType(type);
        
        annotationMain.setCharset(charset);
        annotationMain.setLabelFeature("ne");
        
        annotationMain.setBaseDir(baseDir);
        
        try {
            annotationMain.load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentComponent, sanchay.GlobalProperties.getIntlString("Please_either_load_a_trained_model_or_train_a_new_model."), sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            owner.setCursor(cursor);
        }
        
        annotationMain.setMLTrainPath(baseDir + "/" + "crf-train.txt");
        annotationMain.setMLTestPath(baseDir + sanchay.GlobalProperties.getIntlString("/") + "crf-test.txt");
        annotationMain.setMLTaggedPath(baseDir + "/" + "crf-tagged.txt");
        
        File storyFile = new File(textFile);
        File taggedFile = new File(textFile + "-crf-tagged.txt");
        
        annotationMain.setTestDataPath(textFile);
        annotationMain.setTaggedDataPath(taggedFile.getAbsolutePath());
        annotationMain.test();
        
        try {
            UtilityFunctions.moveFile(textFile + ".out.txt", charset, textFile, charset);
            taggedFile.delete();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        owner.setCursor(cursor);
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        //Create and set up the window.
        JFrame frame = new JFrame(sanchay.GlobalProperties.getIntlString("Sanchay_Syntactic_Annotation"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
//        JDialog taskDialog = new JDialog(frame, sanchay.GlobalProperties.getIntlString("Task_Panel"), true);
//
//        SyntacticAnnotationTaskSetupJPanel taskJPanel = new SyntacticAnnotationTaskSetupJPanel(true);
//
//        taskJPanel.setOwner(frame);
//        taskJPanel.setDialog(taskDialog);
//
//        taskDialog.add(taskJPanel);
//        taskDialog.setBounds(280, 160, 500, 440);
//
//        taskDialog.setVisible(true);
//
//        KeyValueProperties taskKVP = taskJPanel.getTaskProps();
        
        //Create and set up the content pane.
        SyntacticAnnotationWorkJPanel newContentPane = null;
        
//        newContentPane = new SyntacticAnnotationWorkJPanel(taskKVP);
        newContentPane = new SyntacticAnnotationWorkJPanel();
        
        newContentPane.setOwner(frame);
//        newContentPane.setTaskName(taskKVP.getPropertyValue("TaskName"));
//        newContentPane.configure();
        
        newContentPane.owner = frame;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        //Display the window.
        frame.pack();
        
        int xinset = 40;
        int yinset = 60;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(xinset, yinset,
                screenSize.width  - xinset*2,
                screenSize.height - yinset*2);
        
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public String getLangEnc()
    {
        return langEnc;
    }

    public void setLangEnc(String langEnc)
    {
        UtilityFunctions.setComponentFont(queryJTextField, langEnc);
    }

    public String getTitle() {
        return title;
    }
    
    public void clear() {
    }
    
    public void configure(String pmPath, String charSet) {
    }
    
    public PropertiesManager getPropertiesManager() {
        return propman;
    }
    
    public String getWorkspace() {
        return workspace;
    }
    
    public PropertiesTable getTaskList() {
        return taskList;
    }
    
    public void setTaskList(PropertiesTable tl) {
        taskList = tl;
    }
    
    public void setWorkspace(String p) throws Exception {
        workspace = p;
    }
    
    public void setWorkJPanel(JPanel wjp) {
    }

    public String getCurrentTreeEditTab()
    {
        if(ssfPhraseJPanel == null)
            return "Tags";
        
        return ssfPhraseJPanel.getCurrentTreeEditTab();
    }

    public void setCurrentTreeEditTab(String title)
    {
        ssfPhraseJPanel.setCurrentTreeEditTab(title);
    }
    
    private static void saveState(SyntacticAnnotationWorkJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());
        
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

        String currentFontSize = stateKVProps.getPropertyValue("CurrentFontSize");

        if(currentFontSize == null)
            currentFontSize = "" + SanchayLanguages.getDefaultLangEncFont(editorInstance.getLangEnc()).getSize();
        
        stateKVProps.addProperty("CurrentDir", currentDir);
        stateKVProps.addProperty("CurrentFontSize", currentFontSize);
        stateKVProps.addProperty("LangEnc", editorInstance.getLangEnc());
        
        if(editorInstance.getCurrentTreeEditTab() == null)
        {
            stateKVProps.addProperty("CurrentTreeEditTab", "Tags");            
        }
        else
        {
            stateKVProps.addProperty("CurrentTreeEditTab", editorInstance.getCurrentTreeEditTab());
        }
        
        SanchayClientsStateData.save();
    }
    
    private static void loadState(SyntacticAnnotationWorkJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());
        
        String currentDir = stateKVProps.getPropertyValue("CurrentDir");
        String currentFontSize = stateKVProps.getPropertyValue("CurrentFontSize");
        String langEnc = stateKVProps.getPropertyValue("LangEnc");

        String currentTreeEditTab = stateKVProps.getPropertyValue("CurrentTreeEditTab");
        
        if(langEnc == null) {
            langEnc = sanchay.GlobalProperties.getIntlString("hin::utf8");
            stateKVProps.addProperty("LangEnc", langEnc);
        }

        if(currentFontSize == null)
            stateKVProps.addProperty("CurrentFontSize", ""  + SanchayLanguages.getDefaultLangEncFont(editorInstance.getLangEnc()).getSize());

        stateKVProps.addProperty("CurrentTreeEditTab", currentTreeEditTab);
    }

    public SSFStory getSSFSelectedStory(File file)
    {
        System.out.println("File is:"+file);
        return selStories.get(file);
    }

    public void findAndNavigate(FindEvent evt) {
        if(evt.getFilePath() == null || evt.getCharset() == null)
            setCurrentPosition(evt.getNodeID(), evt.getSentenceID(), 0);
        else if(evt.getFilePath().equals(textFile))
            setCurrentPosition(evt.getNodeID(), evt.getSentenceID(), 0);
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        saveState(this);
        PropbankInfoJPanel.saveShortcuts();
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

    @Override
    public boolean closeFile(EventObject e)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void displayStory(SSFStory story, EventObject e)
    {
        if(story == null)
            return;

        if(ssfStory.getSSFFile() != null)
        {
            File oldFile = new File(ssfStory.getSSFFile());
            File newFile = new File(story.getSSFFile());

            if(newFile.equals(oldFile))
                return;
        }

        ssfStory = story;

        openInit(ssfStory.getSSFFile(), langEnc);

        textFile = ssfStory.getSSFFile();

        configure(story);
    }

    @Override
    public void displayFile(String path, String charset, EventObject e)
    {
        displayFile(new File(path), charset, e);
    }

    @Override
    public void displayFile(File file, String charset, EventObject e)
    {
        if(file == null || !file.canRead() || charset == null)
            return;

        openFile(file.getAbsolutePath(), langEnc, charset);
    }

    public void displayFile(EventObject e)
    {
        if(e instanceof DisplayEvent)
        {
            DisplayEvent de = (DisplayEvent) e;
            displayFile(de.getFilePath(), de.getCharset(), e);
        }
        else if(e instanceof SanchayMainEvent)
        {
            SanchayMainEvent sme = (SanchayMainEvent) e;

            if(sme.getFilePath() != null)
                displayFile(sme.getFilePath(), sme.getCharset(), e);
            else if(sme.getDisplayObject() != null)
                displayStory((SSFStory) sme.getDisplayObject(), e);
        }
    }

    @Override
    public String getDisplayedFile(EventObject e)
    {
        return textFile;
    }

    @Override
    public String getCharset(EventObject e)
    {
        return charset;
    }
    
    public boolean getAlignmentMode()
    {
        return alignmentMode;
    }
    
    public void setAlignmentMode(boolean am)
    {
        alignmentMode = am;
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

                    if(colName.equals(sanchay.GlobalProperties.getIntlString("Create_Attribute")))
                        model.setValueAt(Boolean.FALSE, row, i);
                    else if(colName.equals(sanchay.GlobalProperties.getIntlString("Tag")))
                        model.setValueAt(tag, row, i);
                    else if(colName.equals(sanchay.GlobalProperties.getIntlString("Text")))
                        model.setValueAt(text, row, i);
                    else
                        model.setValueAt("[.]*", row, i);

                    if(replace)
                    {
                        if(colName.equals(sanchay.GlobalProperties.getIntlString("New_Tag")))
                            model.setValueAt(UtilityFunctions.backFromExactMatchRegex(tag), row, i);
                        else if(colName.equals(sanchay.GlobalProperties.getIntlString("New_Text")))
                            model.setValueAt(UtilityFunctions.backFromExactMatchRegex(text), row, i);
                    }
                }
            }
            else if(e.getType() == TableModelEvent.UPDATE && replace)
            {
                String colName = model.getColumnName(column);

                if(colName.equals(sanchay.GlobalProperties.getIntlString("Tag")))
                {
                    tag = (String) model.getValueAt(row, column);
                    int newTagColIndex = model.getColumnIndex(sanchay.GlobalProperties.getIntlString("New_Tag"));
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(tag), row, newTagColIndex);
                }
                else if(colName.equals(sanchay.GlobalProperties.getIntlString("Text")))
                {
                    text = (String) model.getValueAt(row, column);
                    int newTextColIndex = model.getColumnIndex(sanchay.GlobalProperties.getIntlString("New_Text"));
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(text), row, newTextColIndex);
                }
                else if(colName.equals(sanchay.GlobalProperties.getIntlString("Attribute_Name")))
                {
                    String attrib = (String) model.getValueAt(row, column);
                    int newAttribColIndex = model.getColumnIndex(sanchay.GlobalProperties.getIntlString("New_Name"));
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(attrib), row, newAttribColIndex);
                }
                else if(colName.equals(sanchay.GlobalProperties.getIntlString("Attribute_Value")))
                {
                    String val = (String) model.getValueAt(row, column);
                    int newValColIndex = model.getColumnIndex(sanchay.GlobalProperties.getIntlString("New_Value"));
                    model.setValueAt(UtilityFunctions.backFromExactMatchRegex(val), row, newValColIndex);
                }
            }
       }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel bottomJPanel;
    public javax.swing.JPanel buttonsJPanel;
    public javax.swing.JButton clearAllJButton;
    public javax.swing.JButton clearJButton;
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JComboBox commentJComboBox;
    public javax.swing.JLabel commentJLabel;
    public javax.swing.JPanel commentJPanel;
    public javax.swing.JScrollPane commentJScrollPane;
    public javax.swing.JTextArea commentJTextArea;
    public javax.swing.JButton convertEncodingJButton;
    public javax.swing.JPanel dndLeftJPanel;
    public javax.swing.JPanel dndRightJPanel;
    public javax.swing.JPanel dndViewJPanel;
    public javax.swing.JSplitPane dndViewJSplitPane;
    public javax.swing.JButton editTextJButton;
    public javax.swing.JButton findInFilesJButton;
    public javax.swing.JButton findJButton;
    public javax.swing.JButton firstJButton;
    public javax.swing.JPanel fourthCommandsJPanel;
    public javax.swing.JCheckBox hideBFormJCheckBox;
    public javax.swing.JPanel jTreeViewJPanel;
    public javax.swing.JButton joinFilesJButton;
    public javax.swing.JButton lastJButton;
    public javax.swing.JPanel mainCommandsJPanel;
    public javax.swing.JButton moreJButton;
    public javax.swing.JCheckBox nestedFSJCheckBox;
    public javax.swing.JButton nextJButton;
    public javax.swing.JButton openJButton;
    public javax.swing.JComboBox positionJComboBox;
    public javax.swing.JLabel positionJLabel;
    public javax.swing.JButton prevJButton;
    public javax.swing.JCheckBox propbankModeJCheckBox;
    public javax.swing.JPanel queryCommandsJPanel;
    public javax.swing.JButton queryInFilesJButton;
    public javax.swing.JButton queryJButton;
    public javax.swing.JPanel queryJPanel;
    public javax.swing.JTextField queryJTextField;
    public javax.swing.JButton replaceBatchInFilesJButton;
    public javax.swing.JButton replaceBatchJButton;
    public javax.swing.JButton replaceInFilesJButton;
    public javax.swing.JButton replaceJButton;
    public javax.swing.JButton resetAllJButton;
    public javax.swing.JButton resetJButton;
    public javax.swing.JButton saveAsJButton;
    public javax.swing.JButton saveJButton;
    public javax.swing.JPanel secondCommandsJPanel;
    public javax.swing.JPanel senNumJPanel;
    public javax.swing.JButton sentenceJoinJButton;
    public javax.swing.JButton sentenceSplitJButton;
    public javax.swing.JButton setMorphTagsJButton;
    public javax.swing.JButton statInFilesJButton;
    public javax.swing.JButton statJButton;
    public javax.swing.JCheckBox textEditCheckBox;
    public javax.swing.JPanel thirdCommandsJPanel;
    public javax.swing.JPanel topJPanel;
    public javax.swing.JButton transferTagsJButton;
    public javax.swing.JButton uploadJButton;
    public javax.swing.JPanel upperNavJPanel;
    public javax.swing.JButton validationJButton;
    public javax.swing.JButton validationPrevJButton;
    public javax.swing.JPanel videoNavJPanel;
    public javax.swing.JPanel workJPanel;
    public javax.swing.JCheckBox workRemoteJCheckBox;
    public javax.swing.JButton zoomInJButton;
    public javax.swing.JButton zoomOutJButton;
    // End of variables declaration//GEN-END:variables
    
}
