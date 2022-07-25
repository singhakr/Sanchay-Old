/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WordAlignmentInterfaceJPanel.java
 *
 * Created on 15 Nov, 2009, 12:38:58 PM
 */
package sanchay.corpus.parallel.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.MutableTreeNode;
import sanchay.GlobalProperties;
import sanchay.common.SanchayClientsStateData;
import sanchay.common.types.ClientType;
import sanchay.corpus.parallel.Alignable;
import sanchay.corpus.parallel.AlignmentBlock;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayLanguages;
import sanchay.gui.scroll.Rule;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.RowEditorModel;
import sanchay.table.gui.RowRendererModel;
import sanchay.table.gui.SanchayJTable;
import sanchay.table.gui.TreeViewerTableCellRenderer;
import sanchay.tree.SanchayEdges;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class WordAlignmentInterfaceJPanel extends javax.swing.JPanel
        implements WindowListener, JPanelDialog, sanchay.gui.clients.SanchayClient, ItemListener, AlignmentEventListener {

    protected ClientType clientType = ClientType.WORD_ALIGNMENT_INTERFACE;
    protected static KeyValueProperties stateKVProps;
    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;
    protected String title = "";
    protected String srcLangEnc = "eng::utf8";
    protected String tgtLangEnc = "hin::utf8";
    protected String srcCharset = "UTF-8";
    protected String tgtCharset = "UTF-8";
    protected String gizaCharset = "UTF-8";
    protected String srcFilePath = GlobalProperties.resolveRelativePath("data/parallel-corpus/eng-1.txt");
    protected String tgtFilePath = GlobalProperties.resolveRelativePath("data/parallel-corpus/hin-1.txt");
    protected String gizaFilePath = GlobalProperties.resolveRelativePath("data/parallel-corpus/en-hi.A3.final");
    protected String srcTagFilePath = GlobalProperties.resolveRelativePath("workspace/syn-annotation/phrase-names.txt");
    protected String tgtTagFilePath = GlobalProperties.resolveRelativePath("workspace/syn-annotation/phrase-names.txt");
    protected String srcLangauge;
    protected String srcEncoding;
    protected String tgtLangauge;
    protected String tgtEncoding;
    protected DefaultComboBoxModel srcLangauges;
    protected DefaultComboBoxModel srcEncodings;
    protected DefaultComboBoxModel tgtLangauges;
    protected DefaultComboBoxModel tgtEncodings;
    protected PropertyTokens srcTextPT;
    protected PropertyTokens tgtTextPT;
    protected PropertyTokens srcTagsPT;
    protected PropertyTokens tgtTagsPT;
    protected DefaultComboBoxModel srcTags;
    protected DefaultComboBoxModel tgtTags;
    protected SSFStory srcSSFStory;
    protected SSFStory tgtSSFStory;
    protected AlignmentBlock alignmentBlock;
    protected AlignmentUnit alignmentUnit;
    protected SanchayTableModel alignmentModel;
    protected SanchayJTable alignmentJTable;
    protected RowRendererModel cellRendererModel;
    protected RowEditorModel cellEditorModel;
    protected DefaultTableCellRenderer srcCellRenderer;
    protected DefaultTableCellRenderer tgtCellRenderer;
    protected TableCellEditor srcCellEditor;
    protected TableCellEditor tgtCellEditor;
    protected Rule rowView;
    protected DefaultComboBoxModel positions;
    protected boolean dirty;
    protected SSFSentence srcSentence;
    protected SSFSentence tgtSentence;
    protected int currentPositionSrc;
    protected int currentPositionTgt;
    protected int currentAlignmentPosition;
    protected boolean langEncFilled;

    /** Creates new form WordAlignmentInterfaceJPanel */
    public WordAlignmentInterfaceJPanel() {
        initComponents();

        srcLangauges = new DefaultComboBoxModel();
        srcEncodings = new DefaultComboBoxModel();

        tgtLangauges = new DefaultComboBoxModel();
        tgtEncodings = new DefaultComboBoxModel();

        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());

        if (stateKVProps.getPropertyValue("srcLangEnc") != null && stateKVProps.getPropertyValue("srcLangEnc").equals("") == false) {
            srcLangEnc = stateKVProps.getPropertyValue("srcLangEnc");
        }

        if (stateKVProps.getPropertyValue("tgtLangEnc") != null && stateKVProps.getPropertyValue("tgtLangEnc").equals("") == false) {
            tgtLangEnc = stateKVProps.getPropertyValue("tgtLangEnc");
        }

        srcLangauge = SanchayLanguages.getLanguageName(srcLangEnc);
        srcEncoding = SanchayLanguages.getEncodingName(srcLangEnc);

        SanchayLanguages.fillLanguages(srcLangauges);
        SanchayLanguages.fillEncodings(srcEncodings, SanchayLanguages.getLanguageCode(srcLangauge));

        srcLanguageJComboBox.setModel(srcLangauges);
        srcEncodingJComboBox.setModel(srcEncodings);

        srcLanguageJComboBox.setSelectedItem(srcLangauge);
        srcEncodingJComboBox.setSelectedItem(srcEncoding);

        tgtLangauge = SanchayLanguages.getLanguageName(tgtLangEnc);
        tgtEncoding = SanchayLanguages.getEncodingName(tgtLangEnc);

        SanchayLanguages.fillLanguages(tgtLangauges);
        SanchayLanguages.fillEncodings(tgtEncodings, SanchayLanguages.getLanguageCode(tgtLangauge));

        tgtLanguageJComboBox.setModel(tgtLangauges);
        tgtEncodingJComboBox.setModel(tgtEncodings);

        tgtLanguageJComboBox.setSelectedItem(tgtLangauge);
        tgtEncodingJComboBox.setSelectedItem(tgtEncoding);

        alignmentBlock = new AlignmentBlock(AlignmentBlock.PHRASE_ALIGNMENT_MODE);
        alignmentUnit = new AlignmentUnit();

        alignmentModel = new SanchayTableModel(4, 4);

        cellEditorModel = new RowEditorModel();
        cellRendererModel = new RowRendererModel();

        alignmentModel.addRow();
        alignmentModel.addRow();
        alignmentModel.addRow();
        alignmentModel.addRow();

        prepareAlignment();

        langEncFilled = true;

//        navigationJPanel.setVisible(false);

        srcFileJTextField.setText(srcFilePath);
        tgtFileJTextField.setText(tgtFilePath);

        loadState(this);

        loadData();
        loadSrcTags();
        loadTgtTags();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topJPanel = new javax.swing.JPanel();
        srcLangEncJPanel = new javax.swing.JPanel();
        srcLanguageJPanel = new javax.swing.JPanel();
        srcLanguageJLabel = new javax.swing.JLabel();
        srcLanguageJComboBox = new javax.swing.JComboBox();
        srcEncodingJPanel = new javax.swing.JPanel();
        srcEncodingJLabel = new javax.swing.JLabel();
        srcEncodingJComboBox = new javax.swing.JComboBox();
        tgtLangEncJPanel = new javax.swing.JPanel();
        tgtLanguageJPanel = new javax.swing.JPanel();
        tgtLanguageJLabel = new javax.swing.JLabel();
        tgtLanguageJComboBox = new javax.swing.JComboBox();
        tgtEncodingJPanel = new javax.swing.JPanel();
        tgtEncodingJLabel = new javax.swing.JLabel();
        tgtEncodingJComboBox = new javax.swing.JComboBox();
        filesJPanel = new javax.swing.JPanel();
        srcFileJPanel = new javax.swing.JPanel();
        srcFileJLabel = new javax.swing.JLabel();
        srcFileJTextField = new javax.swing.JTextField();
        srcFileJButton = new javax.swing.JButton();
        tgtFileJPanel = new javax.swing.JPanel();
        tgtFileJLabel = new javax.swing.JLabel();
        tgtFileJTextField = new javax.swing.JTextField();
        tgtFileJButton = new javax.swing.JButton();
        tagFilesJPanel = new javax.swing.JPanel();
        srcTagFileJPanel = new javax.swing.JPanel();
        srcTagFileJLabel = new javax.swing.JLabel();
        srcTagFileJTextField = new javax.swing.JTextField();
        srcTagFileJButton = new javax.swing.JButton();
        tgtTagFileJPanel = new javax.swing.JPanel();
        tgtTagFileJLabel = new javax.swing.JLabel();
        tgtTagFileJTextField = new javax.swing.JTextField();
        tgtTagFileJButton = new javax.swing.JButton();
        mainJPanel = new javax.swing.JPanel();
        bottomJPanel = new javax.swing.JPanel();
        optionsJPanel = new javax.swing.JPanel();
        senNumJPanel = new javax.swing.JPanel();
        positionJLabel = new javax.swing.JLabel();
        positionJComboBox = new javax.swing.JComboBox();
        tagsJPanel = new javax.swing.JPanel();
        srcTagsJLabel = new javax.swing.JLabel();
        srcTagsJComboBox = new javax.swing.JComboBox();
        tgtTagsJLabel = new javax.swing.JLabel();
        tgtTagsJComboBox = new javax.swing.JComboBox();
        loadJPanel = new javax.swing.JPanel();
        clearJButton = new javax.swing.JButton();
        resetJButton = new javax.swing.JButton();
        loadGIZAJButton = new javax.swing.JButton();
        loadJButton = new javax.swing.JButton();
        saveGIZAJButton = new javax.swing.JButton();
        saveGIZAAsJButton = new javax.swing.JButton();
        saveJButton = new javax.swing.JButton();
        ungroupJButton = new javax.swing.JButton();
        groupJButton = new javax.swing.JButton();
        navigationJPanel = new javax.swing.JPanel();
        firstJButton = new javax.swing.JButton();
        previousJButton = new javax.swing.JButton();
        nextJButton = new javax.swing.JButton();
        lastJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        topJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        srcLangEncJPanel.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        srcLanguageJPanel.setLayout(new java.awt.BorderLayout());

        srcLanguageJLabel.setText("Source Language: ");
        srcLanguageJPanel.add(srcLanguageJLabel, java.awt.BorderLayout.WEST);

        srcLanguageJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                srcLanguageJComboBoxActionPerformed(evt);
            }
        });
        srcLanguageJPanel.add(srcLanguageJComboBox, java.awt.BorderLayout.CENTER);

        srcLangEncJPanel.add(srcLanguageJPanel);

        srcEncodingJPanel.setLayout(new java.awt.BorderLayout());

        srcEncodingJLabel.setText("Source Encoding:  ");
        srcEncodingJPanel.add(srcEncodingJLabel, java.awt.BorderLayout.WEST);

        srcEncodingJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                srcEncodingJComboBoxActionPerformed(evt);
            }
        });
        srcEncodingJPanel.add(srcEncodingJComboBox, java.awt.BorderLayout.CENTER);

        srcLangEncJPanel.add(srcEncodingJPanel);

        topJPanel.add(srcLangEncJPanel);

        tgtLangEncJPanel.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        tgtLanguageJPanel.setLayout(new java.awt.BorderLayout());

        tgtLanguageJLabel.setText("Target Language: ");
        tgtLanguageJPanel.add(tgtLanguageJLabel, java.awt.BorderLayout.WEST);

        tgtLanguageJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgtLanguageJComboBoxActionPerformed(evt);
            }
        });
        tgtLanguageJPanel.add(tgtLanguageJComboBox, java.awt.BorderLayout.CENTER);

        tgtLangEncJPanel.add(tgtLanguageJPanel);

        tgtEncodingJPanel.setLayout(new java.awt.BorderLayout());

        tgtEncodingJLabel.setText("Target Encoding:  ");
        tgtEncodingJPanel.add(tgtEncodingJLabel, java.awt.BorderLayout.WEST);

        tgtEncodingJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgtEncodingJComboBoxActionPerformed(evt);
            }
        });
        tgtEncodingJPanel.add(tgtEncodingJComboBox, java.awt.BorderLayout.CENTER);

        tgtLangEncJPanel.add(tgtEncodingJPanel);

        topJPanel.add(tgtLangEncJPanel);

        filesJPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        srcFileJPanel.setLayout(new java.awt.BorderLayout());

        srcFileJLabel.setText("Source File:");
        srcFileJLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        srcFileJPanel.add(srcFileJLabel, java.awt.BorderLayout.WEST);

        srcFileJTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        srcFileJTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                srcFileJTextFieldCaretUpdate(evt);
            }
        });
        srcFileJPanel.add(srcFileJTextField, java.awt.BorderLayout.CENTER);

        srcFileJButton.setText("Browse");
        srcFileJButton.setToolTipText("Browse to the source file");
        srcFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                srcFileJButtonActionPerformed(evt);
            }
        });
        srcFileJPanel.add(srcFileJButton, java.awt.BorderLayout.EAST);

        filesJPanel.add(srcFileJPanel);

        tgtFileJPanel.setLayout(new java.awt.BorderLayout());

        tgtFileJLabel.setText("Target File:");
        tgtFileJLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        tgtFileJPanel.add(tgtFileJLabel, java.awt.BorderLayout.WEST);

        tgtFileJTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        tgtFileJTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tgtFileJTextFieldCaretUpdate(evt);
            }
        });
        tgtFileJPanel.add(tgtFileJTextField, java.awt.BorderLayout.CENTER);

        tgtFileJButton.setText("Browse");
        tgtFileJButton.setToolTipText("Browse to the target file");
        tgtFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgtFileJButtonActionPerformed(evt);
            }
        });
        tgtFileJPanel.add(tgtFileJButton, java.awt.BorderLayout.EAST);

        filesJPanel.add(tgtFileJPanel);

        topJPanel.add(filesJPanel);

        tagFilesJPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        srcTagFileJPanel.setLayout(new java.awt.BorderLayout());

        srcTagFileJLabel.setText("Source Tag File:");
        srcTagFileJLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        srcTagFileJPanel.add(srcTagFileJLabel, java.awt.BorderLayout.WEST);

        srcTagFileJTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        srcTagFileJPanel.add(srcTagFileJTextField, java.awt.BorderLayout.CENTER);

        srcTagFileJButton.setText("Browse");
        srcTagFileJButton.setToolTipText("Browse to the source file");
        srcTagFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                srcTagFileJButtonActionPerformed(evt);
            }
        });
        srcTagFileJPanel.add(srcTagFileJButton, java.awt.BorderLayout.EAST);

        tagFilesJPanel.add(srcTagFileJPanel);

        tgtTagFileJPanel.setLayout(new java.awt.BorderLayout());

        tgtTagFileJLabel.setText("Target Tag File:");
        tgtTagFileJLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        tgtTagFileJPanel.add(tgtTagFileJLabel, java.awt.BorderLayout.WEST);

        tgtTagFileJTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        tgtTagFileJPanel.add(tgtTagFileJTextField, java.awt.BorderLayout.CENTER);

        tgtTagFileJButton.setText("Browse");
        tgtTagFileJButton.setToolTipText("Browse to the target file");
        tgtTagFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgtTagFileJButtonActionPerformed(evt);
            }
        });
        tgtTagFileJPanel.add(tgtTagFileJButton, java.awt.BorderLayout.EAST);

        tagFilesJPanel.add(tgtTagFileJPanel);

        topJPanel.add(tagFilesJPanel);

        add(topJPanel, java.awt.BorderLayout.NORTH);

        mainJPanel.setLayout(new java.awt.BorderLayout());
        add(mainJPanel, java.awt.BorderLayout.CENTER);

        bottomJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        optionsJPanel.setLayout(new java.awt.BorderLayout());

        senNumJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        positionJLabel.setText("Go to: "); // NOI18N
        positionJLabel.setToolTipText("Go to sentence number");
        senNumJPanel.add(positionJLabel);

        positionJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionJComboBoxActionPerformed(evt);
            }
        });
        senNumJPanel.add(positionJComboBox);

        optionsJPanel.add(senNumJPanel, java.awt.BorderLayout.WEST);

        srcTagsJLabel.setText("Source Tags:");
        srcTagsJLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        tagsJPanel.add(srcTagsJLabel);

        srcTagsJComboBox.setPreferredSize(new java.awt.Dimension(100, 24));
        srcTagsJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                srcTagsJComboBoxActionPerformed(evt);
            }
        });
        tagsJPanel.add(srcTagsJComboBox);

        tgtTagsJLabel.setText("Target Tags:");
        tgtTagsJLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        tagsJPanel.add(tgtTagsJLabel);

        tgtTagsJComboBox.setPreferredSize(new java.awt.Dimension(100, 24));
        tgtTagsJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgtTagsJComboBoxActionPerformed(evt);
            }
        });
        tagsJPanel.add(tgtTagsJComboBox);

        optionsJPanel.add(tagsJPanel, java.awt.BorderLayout.CENTER);

        loadJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        clearJButton.setMnemonic('C');
        clearJButton.setText("Clear");
        clearJButton.setToolTipText("Clear alignments");
        clearJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(clearJButton);

        resetJButton.setMnemonic('R');
        resetJButton.setText("Reset");
        resetJButton.setToolTipText("Reset alignments");
        resetJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(resetJButton);

        loadGIZAJButton.setMnemonic('G');
        loadGIZAJButton.setText("Load GIZA");
        loadGIZAJButton.setToolTipText("Load data in the GIZA format");
        loadGIZAJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadGIZAJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(loadGIZAJButton);

        loadJButton.setMnemonic('O');
        loadJButton.setText("Load");
        loadJButton.setToolTipText("Load the data");
        loadJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(loadJButton);

        saveGIZAJButton.setMnemonic('Z');
        saveGIZAJButton.setText("Save GIZA");
        saveGIZAJButton.setToolTipText("Save data in the GIZA format");
        saveGIZAJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGIZAJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(saveGIZAJButton);

        saveGIZAAsJButton.setMnemonic('Z');
        saveGIZAAsJButton.setText("Save GIZA As");
        saveGIZAAsJButton.setToolTipText("Save data in the GIZA format");
        saveGIZAAsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGIZAAsJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(saveGIZAAsJButton);

        saveJButton.setMnemonic('S');
        saveJButton.setText("Save");
        saveJButton.setToolTipText("Save the alignment");
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(saveJButton);

        ungroupJButton.setMnemonic('U');
        ungroupJButton.setText("Ungroup");
        ungroupJButton.setToolTipText("Ungroup words");
        ungroupJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ungroupJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(ungroupJButton);

        groupJButton.setMnemonic('G');
        groupJButton.setText("Group");
        groupJButton.setToolTipText("Group words");
        groupJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(groupJButton);

        optionsJPanel.add(loadJPanel, java.awt.BorderLayout.EAST);

        bottomJPanel.add(optionsJPanel);

        navigationJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        firstJButton.setMnemonic('F');
        firstJButton.setText("First");
        firstJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstJButtonActionPerformed(evt);
            }
        });
        navigationJPanel.add(firstJButton);

        previousJButton.setMnemonic('P');
        previousJButton.setText("Previous");
        previousJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousJButtonActionPerformed(evt);
            }
        });
        navigationJPanel.add(previousJButton);

        nextJButton.setMnemonic('N');
        nextJButton.setText("Next");
        nextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextJButtonActionPerformed(evt);
            }
        });
        navigationJPanel.add(nextJButton);

        lastJButton.setMnemonic('L');
        lastJButton.setText("Last");
        lastJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastJButtonActionPerformed(evt);
            }
        });
        navigationJPanel.add(lastJButton);

        bottomJPanel.add(navigationJPanel);

        add(bottomJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void srcLanguageJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcLanguageJComboBoxActionPerformed
    {//GEN-HEADEREND:event_srcLanguageJComboBoxActionPerformed
        // TODO add your handling code here:
        if (langEncFilled == false) {
            return;
        }

        srcLangauge = (String) srcLanguageJComboBox.getSelectedItem();
        SanchayLanguages.fillEncodings(srcEncodings, SanchayLanguages.getLanguageCode(srcLangauge));
        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());

        if (srcLangauge != null) {
            srcLangEnc = SanchayLanguages.getLangEncCode(srcLangauge, srcEncoding);
            stateKVProps.addProperty("srcLangEnc", srcLangEnc);

//            UtilityFunctions.setComponentFont(srcTextJTextPane, srcLangEnc);
        }
}//GEN-LAST:event_srcLanguageJComboBoxActionPerformed

    private void srcEncodingJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcEncodingJComboBoxActionPerformed
    {//GEN-HEADEREND:event_srcEncodingJComboBoxActionPerformed
        // TODO add your handling code here:
        if (langEncFilled == false) {
            return;
        }

        srcEncoding = (String) srcEncodingJComboBox.getSelectedItem();
        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());

        if (srcEncoding != null) {
            srcLangEnc = SanchayLanguages.getLangEncCode(srcLangauge, srcEncoding);
            stateKVProps.addProperty("srcEangEnc", srcLangEnc);

//            UtilityFunctions.setComponentFont(srcTextJTextPane, srcLangEnc);
        }
}//GEN-LAST:event_srcEncodingJComboBoxActionPerformed

    private void tgtLanguageJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtLanguageJComboBoxActionPerformed
    {//GEN-HEADEREND:event_tgtLanguageJComboBoxActionPerformed
        // TODO add your handling code here:
        if (langEncFilled == false) {
            return;
        }

        tgtLangauge = (String) tgtLanguageJComboBox.getSelectedItem();
        SanchayLanguages.fillEncodings(tgtEncodings, SanchayLanguages.getLanguageCode(tgtLangauge));
        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());

        if (tgtLangauge != null) {
            tgtLangEnc = SanchayLanguages.getLangEncCode(tgtLangauge, tgtEncoding);
            stateKVProps.addProperty("tgtLangEnc", tgtLangEnc);

//            UtilityFunctions.setComponentFont(tgtTextJTextPane, tgtLangEnc);
        }
}//GEN-LAST:event_tgtLanguageJComboBoxActionPerformed

    private void tgtEncodingJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtEncodingJComboBoxActionPerformed
    {//GEN-HEADEREND:event_tgtEncodingJComboBoxActionPerformed
        // TODO add your handling code here:
        if (langEncFilled == false) {
            return;
        }

        tgtEncoding = (String) tgtEncodingJComboBox.getSelectedItem();
        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());

        if (tgtEncoding != null) {
            tgtLangEnc = SanchayLanguages.getLangEncCode(tgtLangauge, tgtEncoding);
            stateKVProps.addProperty("tgtLangEnc", tgtLangEnc);

//            UtilityFunctions.setComponentFont(tgtTextJTextPane, tgtLangEnc);
        }
}//GEN-LAST:event_tgtEncodingJComboBoxActionPerformed

    private void srcFileJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcFileJButtonActionPerformed
    {//GEN-HEADEREND:event_srcFileJButtonActionPerformed
        // TODO add your handling code here:
        String path = null;

        if (stateKVProps == null) {
            stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());
        }

        if (srcFilePath != null) {
            File sfile = new File(srcFilePath);

            if (sfile.exists() && sfile.getParentFile() != null) {
                path = sfile.getParent();
            } else {
                path = stateKVProps.getPropertyValue("CurrentSrcDir");
            }
        } else {
            path = stateKVProps.getPropertyValue("CurrentSrcDir");
        }

        JFileChooser chooser = null;

        if (path != null) {
            chooser = new JFileChooser(path);
        } else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            srcFilePath = chooser.getSelectedFile().getAbsolutePath();

            srcFileJTextField.setText(srcFilePath);
            stateKVProps.addProperty("CurrentSrcDir", chooser.getSelectedFile().getParent());
        }
}//GEN-LAST:event_srcFileJButtonActionPerformed

    private void tgtFileJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtFileJButtonActionPerformed
    {//GEN-HEADEREND:event_tgtFileJButtonActionPerformed
        // TODO add your handling code here:
        String path = null;

        if (stateKVProps == null) {
            stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());
        }

        if (tgtFilePath != null) {
            File tfile = new File(tgtFilePath);

            if (tfile.exists() && tfile.getParentFile() != null) {
                path = tfile.getParent();
            } else {
                path = stateKVProps.getPropertyValue("CurrentTgtDir");
            }
        } else {
            path = stateKVProps.getPropertyValue("CurrentTgtDir");
        }

        JFileChooser chooser = null;

        if (path != null) {
            chooser = new JFileChooser(path);
        } else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            tgtFilePath = chooser.getSelectedFile().getAbsolutePath();

            tgtFileJTextField.setText(tgtFilePath);
            stateKVProps.addProperty("CurrentTgtDir", chooser.getSelectedFile().getParent());
        }
}//GEN-LAST:event_tgtFileJButtonActionPerformed

    private void loadJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadJButtonActionPerformed
    {//GEN-HEADEREND:event_loadJButtonActionPerformed
        // TODO add your handling code here:
        loadData();
}//GEN-LAST:event_loadJButtonActionPerformed

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveJButtonActionPerformed
    {//GEN-HEADEREND:event_saveJButtonActionPerformed
        // TODO add your handling code here:
        saveData();
}//GEN-LAST:event_saveJButtonActionPerformed

    private void firstJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_firstJButtonActionPerformed
    {//GEN-HEADEREND:event_firstJButtonActionPerformed
        // TODO add your handling code here:
        setCurrentPosition(0, 0);
}//GEN-LAST:event_firstJButtonActionPerformed

    private void previousJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_previousJButtonActionPerformed
    {//GEN-HEADEREND:event_previousJButtonActionPerformed
        // TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        int cp = 0;

        try {
            cp = Integer.parseInt(pos);

            if(srcSentence.getAlignedObject(currentAlignmentPosition - 1) == null)
                setCurrentPosition(cp - 2, 0);
            else
                setCurrentPosition(cp - 1, currentAlignmentPosition - 1);
        } catch (NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
}//GEN-LAST:event_previousJButtonActionPerformed

    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextJButtonActionPerformed
    {//GEN-HEADEREND:event_nextJButtonActionPerformed
        // TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        int cp = 0;

        try {
            cp = Integer.parseInt(pos);

            if(srcSentence.getAlignedObject(currentAlignmentPosition + 1) == null)
                setCurrentPosition(cp, 0);
            else
                setCurrentPosition(cp - 1, currentAlignmentPosition + 1);
        } catch (NumberFormatException e) {
            displayCurrentPosition();
//            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        }
}//GEN-LAST:event_nextJButtonActionPerformed

    private void lastJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lastJButtonActionPerformed
    {//GEN-HEADEREND:event_lastJButtonActionPerformed
        // TODO add your handling code here:
        setCurrentPosition(srcSSFStory.countSentences() - 1, -1);
}//GEN-LAST:event_lastJButtonActionPerformed

    private void positionJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_positionJComboBoxActionPerformed
    {//GEN-HEADEREND:event_positionJComboBoxActionPerformed
        // TODO add your handling code here:
        String pos = (String) positionJComboBox.getSelectedItem();

        int cp = 0;

        try {
            cp = Integer.parseInt(pos);

            setCurrentPosition(cp - 1, 0);
        } catch (NumberFormatException e) {
            displayCurrentPosition();
            //            JOptionPane.showMessageDialog(this, "Wrong sentence number: " + pos, "Error", JOptionPane.ERROR_MESSAGE);
            //            e.printStackTrace();
        }
}//GEN-LAST:event_positionJComboBoxActionPerformed

    private void srcTagFileJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcTagFileJButtonActionPerformed
    {//GEN-HEADEREND:event_srcTagFileJButtonActionPerformed
        // TODO add your handling code here:
        String path = null;

        if (stateKVProps == null) {
            stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());
        }

        if (srcTagFilePath != null) {
            File tfile = new File(srcTagFilePath);

            if (tfile.exists() && tfile.getParentFile() != null) {
                path = tfile.getParent();
            } else {
                path = stateKVProps.getPropertyValue("CurrentSrcTagDir");
            }
        } else {
            path = stateKVProps.getPropertyValue("CurrentSrcTagDir");
        }

        JFileChooser chooser = null;

        if (path != null) {
            chooser = new JFileChooser(path);
        } else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            srcTagFilePath = chooser.getSelectedFile().getAbsolutePath();

            loadSrcTags();
        }
}//GEN-LAST:event_srcTagFileJButtonActionPerformed

    private void tgtTagFileJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtTagFileJButtonActionPerformed
    {//GEN-HEADEREND:event_tgtTagFileJButtonActionPerformed
        // TODO add your handling code here:
        String path = null;

        if (stateKVProps == null) {
            stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());
        }

        if (tgtTagFilePath != null) {
            File tfile = new File(tgtTagFilePath);

            if (tfile.exists() && tfile.getParentFile() != null) {
                path = tfile.getParent();
            } else {
                path = stateKVProps.getPropertyValue("CurrentTgtTagDir");
            }
        } else {
            path = stateKVProps.getPropertyValue("CurrentTgtTagDir");
        }

        JFileChooser chooser = null;

        if (path != null) {
            chooser = new JFileChooser(path);
        } else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            tgtTagFilePath = chooser.getSelectedFile().getAbsolutePath();

            loadTgtTags();
        }
}//GEN-LAST:event_tgtTagFileJButtonActionPerformed

    private void groupJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_groupJButtonActionPerformed
    {//GEN-HEADEREND:event_groupJButtonActionPerformed
        // TODO add your handling code here:
        groupNodes();
}//GEN-LAST:event_groupJButtonActionPerformed

    private void ungroupJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ungroupJButtonActionPerformed
    {//GEN-HEADEREND:event_ungroupJButtonActionPerformed
        // TODO add your handling code here:
        ungroupNodes();
}//GEN-LAST:event_ungroupJButtonActionPerformed

    private void srcTagsJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcTagsJComboBoxActionPerformed
    {//GEN-HEADEREND:event_srcTagsJComboBoxActionPerformed
        // TODO add your handling code here:
        setTag();
    }//GEN-LAST:event_srcTagsJComboBoxActionPerformed

    private void tgtTagsJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtTagsJComboBoxActionPerformed
    {//GEN-HEADEREND:event_tgtTagsJComboBoxActionPerformed
        // TODO add your handling code here:
        setTag();
    }//GEN-LAST:event_tgtTagsJComboBoxActionPerformed

    private void clearJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clearJButtonActionPerformed
    {//GEN-HEADEREND:event_clearJButtonActionPerformed
        // TODO add your handling code here:
        clear();
}//GEN-LAST:event_clearJButtonActionPerformed

    private void resetJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetJButtonActionPerformed
    {//GEN-HEADEREND:event_resetJButtonActionPerformed
        // TODO add your handling code here:
        reset();
}//GEN-LAST:event_resetJButtonActionPerformed

    private void srcFileJTextFieldCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_srcFileJTextFieldCaretUpdate
    {//GEN-HEADEREND:event_srcFileJTextFieldCaretUpdate
        // TODO add your handling code here:
        srcFilePath = srcFileJTextField.getText();
    }//GEN-LAST:event_srcFileJTextFieldCaretUpdate

    private void tgtFileJTextFieldCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_tgtFileJTextFieldCaretUpdate
    {//GEN-HEADEREND:event_tgtFileJTextFieldCaretUpdate
        // TODO add your handling code here:
        tgtFilePath = tgtFileJTextField.getText();
    }//GEN-LAST:event_tgtFileJTextFieldCaretUpdate

    private void loadGIZAJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadGIZAJButtonActionPerformed
    {//GEN-HEADEREND:event_loadGIZAJButtonActionPerformed
        // TODO add your handling code here:
        String path = null;

        if (stateKVProps == null) {
            stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());
        }

        if (gizaFilePath != null) {
            File sfile = new File(gizaFilePath);

            if (sfile.exists() && sfile.getParentFile() != null) {
                path = sfile.getParent();
            } else {
                path = stateKVProps.getPropertyValue("GIZADir");
            }
        } else {
            path = stateKVProps.getPropertyValue("GIZADir");
        }

        JFileChooser chooser = null;

        if (path != null) {
            chooser = new JFileChooser(path);
        } else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            gizaFilePath = chooser.getSelectedFile().getAbsolutePath();

//            srcFileJTextField.setText(gizaFilePath);
            stateKVProps.addProperty("GIZADir", chooser.getSelectedFile().getParent());
        }

        srcFilePath = gizaFilePath + "-src.txt";
        tgtFilePath = gizaFilePath + "-tgt.txt";

        srcFileJTextField.setText(srcFilePath);
        tgtFileJTextField.setText(tgtFilePath);

        loadGIZAData(gizaFilePath, gizaCharset);
}//GEN-LAST:event_loadGIZAJButtonActionPerformed

    private void saveGIZAJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGIZAJButtonActionPerformed
        // TODO add your handling code here:
        saveGIZAFormat(evt);
    }//GEN-LAST:event_saveGIZAJButtonActionPerformed

    private void saveGIZAAsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGIZAAsJButtonActionPerformed
        // TODO add your handling code here:
        saveAsGIZAFormat(evt);
    }//GEN-LAST:event_saveGIZAAsJButtonActionPerformed

    public ClientType getClientType() {
        return clientType;
    }

    private void clear() {
        srcSentence.clearAlignments();
        tgtSentence.clearAlignments();

        srcSentence.saveAlignments();
        tgtSentence.saveAlignments();

        prepareAlignment();
    }

    private void reset() {
        dirty = false;
        displayCurrentPosition();
    }

    private void loadSrcTags() {
        srcTagFileJTextField.setText(srcTagFilePath);

        String parentDir = (new File(srcTagFilePath)).getParent();

        stateKVProps.addProperty("CurrentSrcTagDir", parentDir);

        try {
            srcTagsPT = new PropertyTokens(srcTagFilePath, srcCharset);
            Vector tagsVec = srcTagsPT.getCopyOfTokens();

            srcTags = new DefaultComboBoxModel(tagsVec);
            srcTagsJComboBox.setModel(srcTags);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTgtTags() {
        String parentDir = (new File(tgtTagFilePath)).getParent();

        tgtTagFileJTextField.setText(tgtTagFilePath);
        stateKVProps.addProperty("CurrentTgtTagDir", parentDir);

        try {
            tgtTagsPT = new PropertyTokens(tgtTagFilePath, tgtCharset);
            Vector tagsVec = tgtTagsPT.getCopyOfTokens();

            tgtTags = new DefaultComboBoxModel(tagsVec);
            tgtTagsJComboBox.setModel(tgtTags);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private SSFNode getSelectedNode() {
        int row = alignmentJTable.getSelectedRow();
        int col = alignmentJTable.getSelectedColumn();

        if (row == 1) {
            return null;
        }

        AlignmentUnit aunit = (AlignmentUnit) alignmentModel.getValueAt(row, col);

        SSFNode node = (SSFNode) aunit.getAlignmentObject();

        return node;
    }

    private void setTag() {
        SSFNode node = getSelectedNode();

        if (node == null) {
            return;
        }

        int row = alignmentJTable.getSelectedRow();

        String tag = "";

        if (row == 0) {
            tag = (String) srcTags.getSelectedItem();
        } else if (row == 2) {
            tag = (String) tgtTags.getSelectedItem();
        }

        node.setName(tag);

        refreshAlignments(true);
    }

    private void groupNodes() {
        int cols[] = alignmentJTable.getSelectedColumns();

        if (cols == null || UtilityFunctions.areConsequentNumbers(cols) == false) {
            return;
        }

        Arrays.sort(cols);

        SSFNode node = getSelectedNode();

        if (node == null) {
            return;
        }

        SSFPhrase parent = (SSFPhrase) node.getParent();

        try {
            parent.formPhrase(cols[0], cols.length);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        storeCurrentPosition();

        displayCurrentPosition();
    }

    private void ungroupNodes() {
        SSFNode node = getSelectedNode();

        if (node == null) {
            return;
        }

        if (((SSFNode) node).isLeafNode() == true) {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("The_node_you_have_selected_is_a_lexical_item,_not_a_phrase."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        MutableTreeNode parent = (MutableTreeNode) (node.getParent());

        if (parent != null) {
            ((SSFNode) node).removeLayer();
        }

        storeCurrentPosition();

        displayCurrentPosition();
    }

    public void setCurrentPosition(int cp, int cap) {
        int slSize = srcSSFStory.countSentences();

        boolean change = false;
        
        if (cp >= 0 && cp < slSize) {
            if (cp != currentPositionSrc) {
                storeCurrentPosition();
                currentPositionSrc = cp;
                currentAlignmentPosition = 0;
            }
            else if(cap != currentAlignmentPosition)
            {
                storeCurrentPosition();
                currentAlignmentPosition = cap;
            }

            displayCurrentPosition();
        }
    }
    
    private void storeCurrentPosition() {
        if (srcSentence != null && dirty == true) {

            srcSentence.saveAlignments();
            tgtSentence.saveAlignments();

            srcSSFStory.modifySentence(srcSentence, currentPositionSrc);

            SSFSentenceImpl srcStorySentence = (SSFSentenceImpl) srcSSFStory.getSentence(currentPositionSrc);
            SSFSentence tgtStorySentence = srcStorySentence.getAlignedObject(currentAlignmentPosition);
            
            if(tgtStorySentence == null)
                currentPositionTgt = currentPositionSrc;
            else
                currentPositionTgt = tgtSSFStory.findSentenceIndex(tgtStorySentence.getId());
//                currentPositionTgt = tgtSSFStory.findSentence(tgtStorySentence);
            
            tgtSSFStory.modifySentence(tgtSentence, currentPositionTgt);
            
            dirty = false;
        }
    }

    private void displayCurrentPosition() {

        if (dirty == false) {
            try {
                SSFSentenceImpl srcStorySentence = (SSFSentenceImpl) srcSSFStory.getSentence(currentPositionSrc);
                srcSentence = (SSFSentence) srcStorySentence.getCopy();

//                SSFSentenceImpl tgtStorySentence = (SSFSentenceImpl) tgtSSFStory.getSentence(currentPositionSrc);
//                tgtSentence = (SSFSentence) tgtStorySentence.getCopy();

                SSFSentence tgtStorySentence = srcStorySentence.getAlignedObject(currentAlignmentPosition);
                                
                if(tgtStorySentence == null)
                {
                    // For the case when one-to-one sentence aligned and extracted in order before starting this interface
                    // Otherwise just to be ignored
                    tgtStorySentence = (SSFSentenceImpl) tgtSSFStory.getSentence(currentPositionSrc);                    
                    currentPositionTgt = currentPositionSrc;
                }
                else
                    currentPositionTgt = tgtSSFStory.findSentenceIndex(tgtStorySentence.getId());
//                    currentPositionTgt = tgtSSFStory.findSentence(tgtStorySentence);
                
                tgtSentence = (SSFSentence) tgtStorySentence.getCopy();

                srcSentence.loadAlignments(tgtStorySentence, 0);
//                srcSentence.loadAlignments(tgtSentence, 0);
                tgtSentence.loadAlignments(srcSentence, 2);

                dirty = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if(currentAlignmentPosition == 0)
        {
            String currentPositionString = Integer.toString(currentPositionSrc + 1);

            positionJComboBox.setSelectedItem(currentPositionString);
        }

        prepareAlignment();
    }

    protected void prepareAlignment() {
        if (srcSentence == null) {
            srcSentence = new SSFSentenceImpl();
        }
        if (tgtSentence == null) {
            tgtSentence = new SSFSentenceImpl();
        }

        alignmentBlock.prepareAlignment(AlignmentBlock.PHRASE_ALIGNMENT_MODE, srcSentence, tgtSentence);
        alignmentModel = alignmentBlock.getAlignmentTable();

        mainJPanel.removeAll();

        alignmentJTable = new SanchayJTable(alignmentModel, SanchayJTable.ALIGNMENT_MODE, alignmentBlock, true);
        alignmentJTable.addEventListener(this);
        alignmentJTable.setEditor(this);
        alignmentJTable.prepareCommands();

        JScrollPane tableScrollPane = new JScrollPane(alignmentJTable);
//        tableScrollPane.setPreferredSize(new Dimension(300, 250));
        tableScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));

        mainJPanel.add(tableScrollPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        UtilityFunctions.setComponentFont(alignmentJTable, tgtLangEnc);
//        tableJTable.setFont(new java.awt.Font("Dialog", 1, 14));

        alignmentJTable.setRowHeight(80);
        alignmentJTable.getColumnModel().setColumnMargin(15);
        alignmentJTable.setShowHorizontalLines(false);
        alignmentJTable.setShowVerticalLines(false);
//        tableJTable.setIntercellSpacing(new java.awt.Dimension(5, 50));

        alignmentJTable.setCellSelectionEnabled(true);
        alignmentJTable.firePropertyChange("cellSelectionEnabled", false, true);
        alignmentJTable.setRowSelectionAllowed(true);
        alignmentJTable.firePropertyChange("rowSelectionAllowed", false, true);
        alignmentJTable.setColumnSelectionAllowed(true);
        alignmentJTable.firePropertyChange("columnSelectionAllowed", false, true);

        alignmentJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        if (alignmentModel.getRowCount() > 1) {
//            srcCellEditor = new SanchayTableCellEditor(srcLangEnc);
//            tgtCellEditor = new SanchayTableCellEditor(tgtLangEnc);

            srcCellRenderer = new TreeViewerTableCellRenderer();
            srcCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            srcCellRenderer.setToolTipText("Drag and drop for alignment");
            UtilityFunctions.setComponentFont(srcCellRenderer, srcLangEnc);

            tgtCellRenderer = new TreeViewerTableCellRenderer();
            tgtCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            tgtCellRenderer.setToolTipText("Drag and drop for alignment");
            UtilityFunctions.setComponentFont(tgtCellRenderer, tgtLangEnc);

//            cellEditorModel.addRendererForRow(0, srcCellEditor);
//            cellEditorModel.addRendererForRow(2, tgtCellEditor);

            cellRendererModel.addRendererForRow(0, srcCellRenderer);
            cellRendererModel.addRendererForRow(2, tgtCellRenderer);

            alignmentJTable.setRowEditorModel(cellEditorModel);
            alignmentJTable.setRowRendererRendererModel(cellRendererModel);

            alignmentJTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        }

//        alignmentJTable.setTableHeader(null);

        SanchayEdges edges = alignmentBlock.getEdges();

        if (edges != null) {
            alignmentJTable.setEdges(edges);
        }

        UtilityFunctions.fitColumnsToContent(alignmentJTable);

        setVisible(false);
        setVisible(true);
    }

    public void refreshAlignments(boolean recreate) {
        alignmentJTable.updateUI();
        alignmentJTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        dirty = true;
        storeCurrentPosition();

//        if(recreate)
//            prepareAlignment();
//        setVisible(false);
//        setVisible(true);
    }

    protected void loadData() {
        dirty = false;

        srcTextPT = new PropertyTokens();
        tgtTextPT = new PropertyTokens();

        srcSSFStory = new SSFStoryImpl();
        tgtSSFStory = new SSFStoryImpl();

        if (!UtilityFunctions.fileExists(srcFilePath) || !UtilityFunctions.fileExists(tgtFilePath)) {
            JOptionPane.showMessageDialog(this, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File sfile = new File(srcFilePath);
        File tfile = new File(tgtFilePath);

        if (sfile.canWrite() && tfile.canWrite()) {
            try {
                srcSSFStory.readFile(srcFilePath, srcCharset);
                tgtSSFStory.readFile(tgtFilePath, srcCharset);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // Loading sentence alignments
        srcSSFStory.loadAlignments(tgtSSFStory, 0);
        tgtSSFStory.loadAlignments(srcSSFStory, 2);

        if (srcSSFStory != null) {
            int senCount = srcSSFStory.countSentences();

            if (senCount > 0) {
                Vector pvec = new Vector(senCount);

                for (int i = 1; i <= senCount; i++) {
                    pvec.add(Integer.toString(i));
                }

                positions = new DefaultComboBoxModel(pvec);
                positionJComboBox.setModel(positions);
            }

            setCurrentPosition(0, 0);

            displayCurrentPosition();

//            prepareAlignment();
        }
    }

    protected void saveData() {
        storeCurrentPosition();

        try {
            srcSSFStory.save(srcFilePath, srcCharset);
            tgtSSFStory.save(tgtFilePath, tgtCharset);

            srcTextPT.save(srcFilePath + ".pt.txt", srcCharset);
            tgtTextPT.save(tgtFilePath + ".pt.txt", tgtCharset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void loadGIZAData(String gizaFilePath, String cs) {
        dirty = false;

        srcSSFStory = new SSFStoryImpl();
        tgtSSFStory = new SSFStoryImpl();

        SSFStoryImpl.loadGIZAData(gizaFilePath, cs, srcSSFStory, tgtSSFStory);

        if (srcSSFStory != null) {
            int senCount = srcSSFStory.countSentences();

            if (senCount > 0) {
                Vector pvec = new Vector(senCount);

                for (int i = 1; i <= senCount; i++) {
                    pvec.add(Integer.toString(i));
                }

                positions = new DefaultComboBoxModel(pvec);
                positionJComboBox.setModel(positions);
            }
        }

        setCurrentPosition(0, 0);

        displayCurrentPosition();
    }

    public void saveAsGIZAFormat(ActionEvent evt) {
        String path = null;

        if (stateKVProps == null) {
            stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());
        }

        if (gizaFilePath != null) {
            File sfile = new File(gizaFilePath);

            if (sfile.exists() && sfile.getParentFile() != null) {
                path = sfile.getParent();
            } else {
                path = stateKVProps.getPropertyValue("GIZADir");
            }
        } else {
            path = stateKVProps.getPropertyValue("GIZADir");
        }

        JFileChooser chooser = null;

        if (path != null) {
            chooser = new JFileChooser(path);
        } else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            gizaFilePath = chooser.getSelectedFile().getAbsolutePath();

            stateKVProps.addProperty("GIZADir", chooser.getSelectedFile().getParent());

            saveGIZAFormat(evt);
        }
    }

    public void saveGIZAFormat(ActionEvent evt) {
        SSFSentence srcSen = null;
        SSFSentence tgtSen = null;

        int scount = srcSSFStory.countSentences();
        int tcount = tgtSSFStory.countSentences();

        if (scount != tcount) {
            JOptionPane.showMessageDialog(this, "Wrong sentence counts: source " + scount + "  and target " + tcount + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PrintStream ps = null;

        try {
            ps = new PrintStream(gizaFilePath, tgtCharset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WordAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < scount; i++) {
            srcSen = srcSSFStory.getSentence(i);
            tgtSen = tgtSSFStory.getSentence(i);

            srcSen.loadAlignments(tgtSen, 0);
            tgtSen.loadAlignments(srcSen, 2);

            int srcAUCount = srcSen.getRoot().countChildren();
            int tgtAUCount = tgtSen.getRoot().countChildren();

            ps.println("# Sentence pair (" + (i + 1) + ") source length " + srcAUCount + " target length " + tgtAUCount + " alignment score : ");

            for (int j = 0; j < srcAUCount; j++) {
                SSFNode node = srcSen.getRoot().getChild(j);

                ps.print(node.getLexData() + " ");
            }

            ps.println("");

            String nullString = "NULL ({ ";
            String tgtString = "";

            LinkedHashMap<Integer, Integer> nonNullMap = new LinkedHashMap<Integer, Integer>();

            for (int j = 0; j < tgtAUCount; j++) {
                SSFNode node = tgtSen.getRoot().getChild(j);

                tgtString += node.getLexData() + " ({ ";


                AlignmentUnit aunit = node.getAlignmentUnit();

                if (aunit.countAlignedUnits() == 0)
                {
                }
                else
                {
                    Iterator<String> itr = aunit.getAlignedUnitKeys();

                    while(itr.hasNext())
                    {
                        String key = itr.next();

                        AlignmentUnit alignedUnit = aunit.getAlignedUnit(key);

                        tgtString += (alignedUnit.getIndex() + 1) + " ";

                        nonNullMap.put(alignedUnit.getIndex(), j);
                    }
                }

                tgtString += "}) ";
            }

            tgtString = tgtString.replaceAll("\\(\\{\\}\\)", "({ })");

            for (int j = 0; j < srcAUCount; j++) {

                if(nonNullMap.get(j) == null)
                {
                    nullString += (j + 1) + " ";
                }
            }

            nullString += "}) ";

            ps.println(nullString + tgtString);
        }

        ps.close();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JButton clearJButton;
    private javax.swing.JPanel filesJPanel;
    private javax.swing.JButton firstJButton;
    private javax.swing.JButton groupJButton;
    private javax.swing.JButton lastJButton;
    private javax.swing.JButton loadGIZAJButton;
    private javax.swing.JButton loadJButton;
    private javax.swing.JPanel loadJPanel;
    private javax.swing.JPanel mainJPanel;
    private javax.swing.JPanel navigationJPanel;
    private javax.swing.JButton nextJButton;
    private javax.swing.JPanel optionsJPanel;
    private javax.swing.JComboBox positionJComboBox;
    private javax.swing.JLabel positionJLabel;
    private javax.swing.JButton previousJButton;
    private javax.swing.JButton resetJButton;
    private javax.swing.JButton saveGIZAAsJButton;
    private javax.swing.JButton saveGIZAJButton;
    private javax.swing.JButton saveJButton;
    private javax.swing.JPanel senNumJPanel;
    private javax.swing.JComboBox srcEncodingJComboBox;
    private javax.swing.JLabel srcEncodingJLabel;
    private javax.swing.JPanel srcEncodingJPanel;
    private javax.swing.JButton srcFileJButton;
    private javax.swing.JLabel srcFileJLabel;
    private javax.swing.JPanel srcFileJPanel;
    private javax.swing.JTextField srcFileJTextField;
    private javax.swing.JPanel srcLangEncJPanel;
    private javax.swing.JComboBox srcLanguageJComboBox;
    private javax.swing.JLabel srcLanguageJLabel;
    private javax.swing.JPanel srcLanguageJPanel;
    private javax.swing.JButton srcTagFileJButton;
    private javax.swing.JLabel srcTagFileJLabel;
    private javax.swing.JPanel srcTagFileJPanel;
    private javax.swing.JTextField srcTagFileJTextField;
    private javax.swing.JComboBox srcTagsJComboBox;
    private javax.swing.JLabel srcTagsJLabel;
    private javax.swing.JPanel tagFilesJPanel;
    private javax.swing.JPanel tagsJPanel;
    private javax.swing.JComboBox tgtEncodingJComboBox;
    private javax.swing.JLabel tgtEncodingJLabel;
    private javax.swing.JPanel tgtEncodingJPanel;
    private javax.swing.JButton tgtFileJButton;
    private javax.swing.JLabel tgtFileJLabel;
    private javax.swing.JPanel tgtFileJPanel;
    private javax.swing.JTextField tgtFileJTextField;
    private javax.swing.JPanel tgtLangEncJPanel;
    private javax.swing.JComboBox tgtLanguageJComboBox;
    private javax.swing.JLabel tgtLanguageJLabel;
    private javax.swing.JPanel tgtLanguageJPanel;
    private javax.swing.JButton tgtTagFileJButton;
    private javax.swing.JLabel tgtTagFileJLabel;
    private javax.swing.JPanel tgtTagFileJPanel;
    private javax.swing.JTextField tgtTagFileJTextField;
    private javax.swing.JComboBox tgtTagsJComboBox;
    private javax.swing.JLabel tgtTagsJLabel;
    private javax.swing.JPanel topJPanel;
    private javax.swing.JButton ungroupJButton;
    // End of variables declaration//GEN-END:variables

    private static void saveState(WordAlignmentInterfaceJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());

        String currentSrcDir = stateKVProps.getPropertyValue("CurrentSrcDir");

        if (currentSrcDir == null) {
            currentSrcDir = ".";
        }

        File file = null;

        if (editorInstance.srcFilePath != null) {
            file = new File(editorInstance.srcFilePath);

            if (file.exists()) {
                currentSrcDir = file.getParent();
            }
        }

        String currentTgtDir = stateKVProps.getPropertyValue("CurrentTgtDir");

        if (currentTgtDir == null) {
            currentTgtDir = ".";
        }

        file = null;

        if (editorInstance.tgtFilePath != null) {
            file = new File(editorInstance.tgtFilePath);

            if (file.exists()) {
                currentTgtDir = file.getParent();
            }
        }

        String currentGizaDir = stateKVProps.getPropertyValue("GIZADir");

        if (currentGizaDir == null) {
            currentGizaDir = ".";
        }

        file = null;

        if (editorInstance.gizaFilePath != null) {
            file = new File(editorInstance.gizaFilePath);

            if (file.exists()) {
                currentGizaDir = file.getParent();
            }
        }

        stateKVProps.addProperty("CurrentSrcDir", currentTgtDir);
        stateKVProps.addProperty("CurrentTgtDir", currentTgtDir);
        stateKVProps.addProperty("GIZADir", currentGizaDir);
        stateKVProps.addProperty("SrcLangEnc", editorInstance.getSrcLangEnc());
        stateKVProps.addProperty("TgtLangEnc", editorInstance.getTgtLangEnc());

        SanchayClientsStateData.save();
    }

    private static void loadState(WordAlignmentInterfaceJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.WORD_ALIGNMENT_INTERFACE.toString());

        String currentSrcDir = stateKVProps.getPropertyValue("CurrentSrcDir");
        String currentTgtDir = stateKVProps.getPropertyValue("CurrentTgtDir");
        String gizaDir = stateKVProps.getPropertyValue("GIZADir");

        String srcLangEnc = stateKVProps.getPropertyValue("SrcLangEnc");
        String tgtLangEnc = stateKVProps.getPropertyValue("TgtLangEnc");

        if (currentSrcDir == null) {
            currentSrcDir = ".";
            stateKVProps.addProperty("CurrentSrcDir", currentSrcDir);
        }

        if (currentTgtDir == null) {
            currentTgtDir = ".";
            stateKVProps.addProperty("CurrentTgtDir", currentTgtDir);
        }

        if (gizaDir == null) {
            gizaDir = ".";
            stateKVProps.addProperty("GIZADir", gizaDir);
        }

        if (srcLangEnc == null) {
            srcLangEnc = sanchay.GlobalProperties.getIntlString("eng::utf8");
            stateKVProps.addProperty("SrcLangEnc", srcLangEnc);
        }

        if (tgtLangEnc == null) {
            tgtLangEnc = sanchay.GlobalProperties.getIntlString("hin::utf8");
            stateKVProps.addProperty("TgtLangEnc", tgtLangEnc);
        }
    }

    public String getSrcLangEnc() {
        return srcLangEnc;
    }

    public String getTgtLangEnc() {
        return tgtLangEnc;
    }

    public String getLangEnc() {
        return srcLangEnc;
    }

    public Frame getOwner() {
        return owner;
    }

    public void setOwner(Frame frame) {
        owner = (JFrame) frame;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public String getTitle() {
        return title;
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
        alignmentJTable.setMaxUnitIncrement(rowView.getIncrement());
    }

    public void alignmentChanged(AlignmentEvent evt) {
        refreshAlignments(true);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Sanchay Shell");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        WordAlignmentInterfaceJPanel newContentPane = new WordAlignmentInterfaceJPanel();
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

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });
    }
}
