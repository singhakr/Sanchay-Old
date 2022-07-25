/*
 * SyntacticAnnotationTaskJPanel.java
 *
 * Created on October 9, 2005, 9:51 PM
 */

package sanchay.corpus.ssf.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Frame;
import java.io.*;
import java.util.*;
import javax.swing.*;
import sanchay.GlobalProperties;
import sanchay.SanchayMain;
import sanchay.common.SanchayClientsStateData;
import sanchay.common.types.ClientType;
import sanchay.common.types.CorpusType;
import sanchay.common.types.PropertyType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;

import sanchay.gui.*;
import sanchay.gui.clients.AnnotationClient;
import sanchay.gui.clients.SanchayRemoteWorkJPanel;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayLanguages;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertiesManager;
import sanchay.properties.PropertiesTable;
import sanchay.text.editor.gui.TextEditorJPanel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author  anil
 */
public class SyntacticAnnotationTaskSetupJPanel extends javax.swing.JPanel
        implements TaskSetupJPanelInterface, JPanelDialog
{
    private JFrame owner;
    private JDialog dialog;
    
    private SyntacticAnnotationWorkJPanel workJPanel;

    private JDialog workDialog;
    
    private String langauge;
    private String encoding;

    private DefaultComboBoxModel langauges;
    private DefaultComboBoxModel encodings;

    private String taskName;
    private String taskPropFile;

    private String ssfPropFile;
    private String fsPropFile;
    private String mfFile; // mandatory features
    private String nmfFile; // non-mandatory features
    private String psaFile; // phrase structure features
    private String dsaFile; // dependency features
    private String sraFile; // semantic features
    private String ssfCorpusStoryFile;
    private String ssfCorpusStoryUTF8File;

    private String posTagsFile;
    private String morphTagsFile;
    private String phraseNamesFile;
    
    private KeyValueProperties taskKVP;
    private DefaultComboBoxModel taskList;
    
    private boolean newTask;
    private boolean standAloneMode;
    private boolean moreOptionsShown = true;
    
    /** Creates new form SyntacticAnnotationTaskJPanel */
    public SyntacticAnnotationTaskSetupJPanel() {
                 
        taskKVP = new KeyValueProperties();
        newTask = true;
	standAloneMode = false;

        initComponents();
	
	langauges = new DefaultComboBoxModel();
	encodings = new DefaultComboBoxModel();

        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());
        String langEnc = stateKVProps.getPropertyValue("LangEnc");

	setDefaults();

        langauge = SanchayLanguages.getLanguageName(langEnc);
        encoding = SanchayLanguages.getEncodingName(langEnc);

	SanchayLanguages.fillLanguages(langauges);
	SanchayLanguages.fillEncodings(encodings, SanchayLanguages.getLanguageCode(langauge));
	
	languageJComboBox.setModel(langauges);
	encodingJComboBox.setModel(encodings);

        languageJComboBox.setSelectedItem(langauge);
	encodingJComboBox.setSelectedItem(encoding);

        showMoreOptions(null);
    }

    public SyntacticAnnotationTaskSetupJPanel(String ssfFile, boolean standAloneMode)
    {
	this(true);
	
	ssfCorpusStoryFile = ssfFile;
	ssfCorpusJTextField.setText(ssfFile);
	generateTaskNameAndPropFile();
    }

    public SyntacticAnnotationTaskSetupJPanel(boolean standAloneMode, SyntacticAnnotationWorkJPanel workJPanel) {
        this(standAloneMode);
        
        this.workJPanel = workJPanel;
    }

    public SyntacticAnnotationTaskSetupJPanel(boolean standAloneMode) {
        taskKVP = new KeyValueProperties();
        newTask = true;
	this.standAloneMode = standAloneMode;
 
        initComponents();
	
	langauges = new DefaultComboBoxModel();
	encodings = new DefaultComboBoxModel();

        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());
        String langEnc = stateKVProps.getPropertyValue("LangEnc");

	setDefaults();

        langauge = SanchayLanguages.getLanguageName(langEnc);
        encoding = SanchayLanguages.getEncodingName(langEnc);

	SanchayLanguages.fillLanguages(langauges);
	SanchayLanguages.fillEncodings(encodings, SanchayLanguages.getLanguageCode(langauge));

	languageJComboBox.setModel(langauges);
	encodingJComboBox.setModel(encodings);

        languageJComboBox.setSelectedItem(langauge);
	encodingJComboBox.setSelectedItem(encoding);

        showMoreOptions(null);
    }

    public SyntacticAnnotationTaskSetupJPanel(KeyValueProperties kvp) {
        taskKVP = kvp;
        newTask = false;
	standAloneMode = false;
        
        initComponents();

	langauges = new DefaultComboBoxModel();
	encodings = new DefaultComboBoxModel();


        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());
        String langEnc = stateKVProps.getPropertyValue("LangEnc");

        langauge = SanchayLanguages.getLanguageName(langEnc);
        encoding = SanchayLanguages.getEncodingName(langEnc);

	SanchayLanguages.fillLanguages(langauges);
	SanchayLanguages.fillEncodings(encodings, SanchayLanguages.getLanguageCode(langauge));

	languageJComboBox.setModel(langauges);
	encodingJComboBox.setModel(encodings);

        languageJComboBox.setSelectedItem(langauge);
	encodingJComboBox.setSelectedItem(encoding);

	configure();
        taskNameJTextField.setEditable(false);
        propJTextField.setEditable(false);

        showMoreOptions(null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainJPanel = new javax.swing.JPanel();
        languageJPanel = new javax.swing.JPanel();
        languageJLabel = new javax.swing.JLabel();
        languageJComboBox = new javax.swing.JComboBox();
        encodingJPanel = new javax.swing.JPanel();
        encodingJLabel = new javax.swing.JLabel();
        encodingJComboBox = new javax.swing.JComboBox();
        ssfCorpusJPanel = new javax.swing.JPanel();
        ssfCorpusJLabel = new javax.swing.JLabel();
        ssfCorpusJTextField = new javax.swing.JTextField();
        ssfCorpusJButton = new javax.swing.JButton();
        booleanOptionsJPanel = new javax.swing.JPanel();
        saveTaskFileJCheckBox = new javax.swing.JCheckBox();
        auxJPanel = new javax.swing.JPanel();
        ssfCorpusUTF8JPanel = new javax.swing.JPanel();
        ssfCorpusUTF8JLabel = new javax.swing.JLabel();
        ssfCorpusUTF8JTextField = new javax.swing.JTextField();
        ssfCorpusUTF8JButton = new javax.swing.JButton();
        taskJPanel = new javax.swing.JPanel();
        taskNameJLabel = new javax.swing.JLabel();
        taskNameJTextField = new javax.swing.JTextField();
        propJPanel = new javax.swing.JPanel();
        propJLabel = new javax.swing.JLabel();
        propJTextField = new javax.swing.JTextField();
        propertiesJButton = new javax.swing.JButton();
        ssfPropsJPanel = new javax.swing.JPanel();
        ssfPropsJLabel = new javax.swing.JLabel();
        ssfPropsJTextField = new javax.swing.JTextField();
        ssfPropsJButton = new javax.swing.JButton();
        mfeaturesJPanel = new javax.swing.JPanel();
        mfeaturesJLabel = new javax.swing.JLabel();
        mfeaturesJTextField = new javax.swing.JTextField();
        mfeaturesJButton = new javax.swing.JButton();
        nmfeaturesJPanel = new javax.swing.JPanel();
        nmfeaturesJLabel = new javax.swing.JLabel();
        nmfeaturesJTextField = new javax.swing.JTextField();
        nmfeaturesJButton = new javax.swing.JButton();
        fsPropsJPanel = new javax.swing.JPanel();
        fsPropsJLabel = new javax.swing.JLabel();
        fsPropsJTextField = new javax.swing.JTextField();
        fsPropsJButton = new javax.swing.JButton();
        psFeaturesJPanel = new javax.swing.JPanel();
        psFeaturesJLabel = new javax.swing.JLabel();
        psFeaturesJTextField = new javax.swing.JTextField();
        psFeaturesJButton = new javax.swing.JButton();
        depFeaturesJPanel = new javax.swing.JPanel();
        depFeaturesJLabel = new javax.swing.JLabel();
        depFeaturesJTextField = new javax.swing.JTextField();
        depFeaturesJButton = new javax.swing.JButton();
        semFeaturesJPanel = new javax.swing.JPanel();
        semFeaturesJLabel = new javax.swing.JLabel();
        semFeaturesJTextField = new javax.swing.JTextField();
        semFeaturesJButton = new javax.swing.JButton();
        phraseNamesJPanel = new javax.swing.JPanel();
        phraseNamesJLabel = new javax.swing.JLabel();
        phraseNamesJTextField = new javax.swing.JTextField();
        phraseNamesJButton = new javax.swing.JButton();
        posTagsJPanel = new javax.swing.JPanel();
        posTagsJLabel = new javax.swing.JLabel();
        posTagsJTextField = new javax.swing.JTextField();
        posTagsJButton = new javax.swing.JButton();
        morphTagsJPanel = new javax.swing.JPanel();
        morphTagsJLabel = new javax.swing.JLabel();
        morphTagsJTextField = new javax.swing.JTextField();
        morphTagsJButton = new javax.swing.JButton();
        commandsJPanel = new javax.swing.JPanel();
        moreOptionsJButton = new javax.swing.JButton();
        OKJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        mainJPanel.setLayout(new javax.swing.BoxLayout(mainJPanel, javax.swing.BoxLayout.Y_AXIS));

        languageJPanel.setLayout(new java.awt.BorderLayout());

        languageJLabel.setLabelFor(languageJComboBox);
        languageJLabel.setText("Language: ");
        languageJPanel.add(languageJLabel, java.awt.BorderLayout.WEST);

        languageJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageJComboBoxActionPerformed(evt);
            }
        });
        languageJPanel.add(languageJComboBox, java.awt.BorderLayout.CENTER);

        mainJPanel.add(languageJPanel);

        encodingJPanel.setLayout(new java.awt.BorderLayout());

        encodingJLabel.setLabelFor(encodingJComboBox);
        encodingJLabel.setText("Encoding:  ");
        encodingJPanel.add(encodingJLabel, java.awt.BorderLayout.WEST);

        encodingJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encodingJComboBoxActionPerformed(evt);
            }
        });
        encodingJPanel.add(encodingJComboBox, java.awt.BorderLayout.CENTER);

        mainJPanel.add(encodingJPanel);

        ssfCorpusJPanel.setLayout(new java.awt.BorderLayout());

        ssfCorpusJLabel.setLabelFor(ssfCorpusJTextField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        ssfCorpusJLabel.setText(bundle.getString("SSF_corpus_story_file:")); // NOI18N
        ssfCorpusJPanel.add(ssfCorpusJLabel, java.awt.BorderLayout.NORTH);

        ssfCorpusJTextField.setText(bundle.getString("SSF_corpus_story_file")); // NOI18N
        ssfCorpusJTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ssfCorpusJTextFieldCaretUpdate(evt);
            }
        });
        ssfCorpusJPanel.add(ssfCorpusJTextField, java.awt.BorderLayout.CENTER);

        ssfCorpusJButton.setText(bundle.getString("Browse")); // NOI18N
        ssfCorpusJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ssfCorpusJButtonActionPerformed(evt);
            }
        });
        ssfCorpusJPanel.add(ssfCorpusJButton, java.awt.BorderLayout.EAST);

        mainJPanel.add(ssfCorpusJPanel);

        booleanOptionsJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        saveTaskFileJCheckBox.setText(bundle.getString("Save_task_properties_file")); // NOI18N
        saveTaskFileJCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        booleanOptionsJPanel.add(saveTaskFileJCheckBox);

        mainJPanel.add(booleanOptionsJPanel);

        add(mainJPanel, java.awt.BorderLayout.NORTH);

        auxJPanel.setLayout(new javax.swing.BoxLayout(auxJPanel, javax.swing.BoxLayout.Y_AXIS));

        ssfCorpusUTF8JPanel.setLayout(new java.awt.BorderLayout());

        ssfCorpusUTF8JLabel.setLabelFor(ssfCorpusUTF8JTextField);
        ssfCorpusUTF8JLabel.setText(bundle.getString("SSF_corpus_story_file_(UTF8):")); // NOI18N
        ssfCorpusUTF8JPanel.add(ssfCorpusUTF8JLabel, java.awt.BorderLayout.NORTH);

        ssfCorpusUTF8JTextField.setText(bundle.getString("SSF_corpus_story_file_(UTF8)")); // NOI18N
        ssfCorpusUTF8JTextField.setEnabled(false);
        ssfCorpusUTF8JPanel.add(ssfCorpusUTF8JTextField, java.awt.BorderLayout.CENTER);

        ssfCorpusUTF8JButton.setText(bundle.getString("Browse")); // NOI18N
        ssfCorpusUTF8JButton.setEnabled(false);
        ssfCorpusUTF8JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ssfCorpusUTF8JButtonActionPerformed(evt);
            }
        });
        ssfCorpusUTF8JPanel.add(ssfCorpusUTF8JButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(ssfCorpusUTF8JPanel);

        taskJPanel.setLayout(new java.awt.BorderLayout());

        taskNameJLabel.setLabelFor(taskNameJTextField);
        taskNameJLabel.setText(bundle.getString("Task_name:")); // NOI18N
        taskJPanel.add(taskNameJLabel, java.awt.BorderLayout.NORTH);

        taskNameJTextField.setText(bundle.getString("Task_name")); // NOI18N
        taskJPanel.add(taskNameJTextField, java.awt.BorderLayout.CENTER);

        auxJPanel.add(taskJPanel);

        propJPanel.setLayout(new java.awt.BorderLayout());

        propJLabel.setLabelFor(propJTextField);
        propJLabel.setText(bundle.getString("Task_properties_file:")); // NOI18N
        propJPanel.add(propJLabel, java.awt.BorderLayout.NORTH);

        propJTextField.setText(bundle.getString("Task_properties_file")); // NOI18N
        propJPanel.add(propJTextField, java.awt.BorderLayout.CENTER);

        propertiesJButton.setText(bundle.getString("Browse")); // NOI18N
        propertiesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertiesJButtonActionPerformed(evt);
            }
        });
        propJPanel.add(propertiesJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(propJPanel);

        ssfPropsJPanel.setLayout(new java.awt.BorderLayout());

        ssfPropsJLabel.setLabelFor(ssfPropsJTextField);
        ssfPropsJLabel.setText(bundle.getString("SSF_properties_file:")); // NOI18N
        ssfPropsJPanel.add(ssfPropsJLabel, java.awt.BorderLayout.NORTH);

        ssfPropsJTextField.setText(bundle.getString("SSF_properties_file")); // NOI18N
        ssfPropsJPanel.add(ssfPropsJTextField, java.awt.BorderLayout.CENTER);

        ssfPropsJButton.setText(bundle.getString("Browse")); // NOI18N
        ssfPropsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ssfPropsJButtonActionPerformed(evt);
            }
        });
        ssfPropsJPanel.add(ssfPropsJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(ssfPropsJPanel);

        mfeaturesJPanel.setLayout(new java.awt.BorderLayout());

        mfeaturesJLabel.setLabelFor(mfeaturesJTextField);
        mfeaturesJLabel.setText(bundle.getString("Mandatory_features_file:")); // NOI18N
        mfeaturesJPanel.add(mfeaturesJLabel, java.awt.BorderLayout.NORTH);

        mfeaturesJTextField.setText(bundle.getString("Mandatory_features_file")); // NOI18N
        mfeaturesJPanel.add(mfeaturesJTextField, java.awt.BorderLayout.CENTER);

        mfeaturesJButton.setText(bundle.getString("Browse")); // NOI18N
        mfeaturesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mfeaturesJButtonActionPerformed(evt);
            }
        });
        mfeaturesJPanel.add(mfeaturesJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(mfeaturesJPanel);

        nmfeaturesJPanel.setLayout(new java.awt.BorderLayout());

        nmfeaturesJLabel.setLabelFor(mfeaturesJTextField);
        nmfeaturesJLabel.setText(bundle.getString("Non-Mandatory_features_file:")); // NOI18N
        nmfeaturesJPanel.add(nmfeaturesJLabel, java.awt.BorderLayout.NORTH);

        nmfeaturesJTextField.setText(bundle.getString("Non-Mandatory_features_file")); // NOI18N
        nmfeaturesJPanel.add(nmfeaturesJTextField, java.awt.BorderLayout.CENTER);

        nmfeaturesJButton.setText(bundle.getString("Browse")); // NOI18N
        nmfeaturesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nmfeaturesJButtonActionPerformed(evt);
            }
        });
        nmfeaturesJPanel.add(nmfeaturesJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(nmfeaturesJPanel);

        fsPropsJPanel.setLayout(new java.awt.BorderLayout());

        fsPropsJLabel.setLabelFor(fsPropsJTextField);
        fsPropsJLabel.setText(bundle.getString("Feature_structure_properties_file:")); // NOI18N
        fsPropsJPanel.add(fsPropsJLabel, java.awt.BorderLayout.NORTH);

        fsPropsJTextField.setText(bundle.getString("Feature_structure_properties_file")); // NOI18N
        fsPropsJPanel.add(fsPropsJTextField, java.awt.BorderLayout.CENTER);

        fsPropsJButton.setText(bundle.getString("Browse")); // NOI18N
        fsPropsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fsPropsJButtonActionPerformed(evt);
            }
        });
        fsPropsJPanel.add(fsPropsJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(fsPropsJPanel);

        psFeaturesJPanel.setLayout(new java.awt.BorderLayout());

        psFeaturesJLabel.setLabelFor(mfeaturesJTextField);
        psFeaturesJLabel.setText(bundle.getString("Phrase_structure_attributes_file:")); // NOI18N
        psFeaturesJPanel.add(psFeaturesJLabel, java.awt.BorderLayout.NORTH);

        psFeaturesJTextField.setText(bundle.getString("Phrase_structure_attributes_file")); // NOI18N
        psFeaturesJPanel.add(psFeaturesJTextField, java.awt.BorderLayout.CENTER);

        psFeaturesJButton.setText(bundle.getString("Browse")); // NOI18N
        psFeaturesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                psFeaturesJButtonActionPerformed(evt);
            }
        });
        psFeaturesJPanel.add(psFeaturesJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(psFeaturesJPanel);

        depFeaturesJPanel.setLayout(new java.awt.BorderLayout());

        depFeaturesJLabel.setLabelFor(mfeaturesJTextField);
        depFeaturesJLabel.setText(bundle.getString("Dependency_attributes_file:")); // NOI18N
        depFeaturesJPanel.add(depFeaturesJLabel, java.awt.BorderLayout.NORTH);

        depFeaturesJTextField.setText(bundle.getString("Dependency_attributes_file")); // NOI18N
        depFeaturesJPanel.add(depFeaturesJTextField, java.awt.BorderLayout.CENTER);

        depFeaturesJButton.setText(bundle.getString("Browse")); // NOI18N
        depFeaturesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depFeaturesJButtonActionPerformed(evt);
            }
        });
        depFeaturesJPanel.add(depFeaturesJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(depFeaturesJPanel);

        semFeaturesJPanel.setLayout(new java.awt.BorderLayout());

        semFeaturesJLabel.setLabelFor(mfeaturesJTextField);
        semFeaturesJLabel.setText(bundle.getString("Semantic_attributes_file:")); // NOI18N
        semFeaturesJPanel.add(semFeaturesJLabel, java.awt.BorderLayout.NORTH);

        semFeaturesJTextField.setText(bundle.getString("Semantic_attributes_file")); // NOI18N
        semFeaturesJPanel.add(semFeaturesJTextField, java.awt.BorderLayout.CENTER);

        semFeaturesJButton.setText(bundle.getString("Browse")); // NOI18N
        semFeaturesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                semFeaturesJButtonActionPerformed(evt);
            }
        });
        semFeaturesJPanel.add(semFeaturesJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(semFeaturesJPanel);

        phraseNamesJPanel.setLayout(new java.awt.BorderLayout());

        phraseNamesJLabel.setLabelFor(phraseNamesJTextField);
        phraseNamesJLabel.setText(bundle.getString("Phrase_names_file:")); // NOI18N
        phraseNamesJPanel.add(phraseNamesJLabel, java.awt.BorderLayout.NORTH);

        phraseNamesJTextField.setText(bundle.getString("Phrase_names_file")); // NOI18N
        phraseNamesJPanel.add(phraseNamesJTextField, java.awt.BorderLayout.CENTER);

        phraseNamesJButton.setText(bundle.getString("Browse")); // NOI18N
        phraseNamesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phraseNamesJButtonActionPerformed(evt);
            }
        });
        phraseNamesJPanel.add(phraseNamesJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(phraseNamesJPanel);

        posTagsJPanel.setLayout(new java.awt.BorderLayout());

        posTagsJLabel.setLabelFor(posTagsJTextField);
        posTagsJLabel.setText(bundle.getString("POS_tags_file:")); // NOI18N
        posTagsJPanel.add(posTagsJLabel, java.awt.BorderLayout.NORTH);

        posTagsJTextField.setText(bundle.getString("POS_tags_file")); // NOI18N
        posTagsJPanel.add(posTagsJTextField, java.awt.BorderLayout.CENTER);

        posTagsJButton.setText(bundle.getString("Browse")); // NOI18N
        posTagsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                posTagsJButtonActionPerformed(evt);
            }
        });
        posTagsJPanel.add(posTagsJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(posTagsJPanel);

        morphTagsJPanel.setLayout(new java.awt.BorderLayout());

        morphTagsJLabel.setLabelFor(morphTagsJTextField);
        morphTagsJLabel.setText(bundle.getString("Morph_tags_file:")); // NOI18N
        morphTagsJPanel.add(morphTagsJLabel, java.awt.BorderLayout.NORTH);

        morphTagsJTextField.setText(bundle.getString("Morph_tags_file")); // NOI18N
        morphTagsJPanel.add(morphTagsJTextField, java.awt.BorderLayout.CENTER);

        morphTagsJButton.setText(bundle.getString("Browse")); // NOI18N
        morphTagsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                morphTagsJButtonActionPerformed(evt);
            }
        });
        morphTagsJPanel.add(morphTagsJButton, java.awt.BorderLayout.EAST);

        auxJPanel.add(morphTagsJPanel);

        add(auxJPanel, java.awt.BorderLayout.CENTER);

        moreOptionsJButton.setText(bundle.getString("More_Options")); // NOI18N
        moreOptionsJButton.setToolTipText(bundle.getString("Show_or_hide_other_options")); // NOI18N
        moreOptionsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreOptionsJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(moreOptionsJButton);

        OKJButton.setText(bundle.getString("OK")); // NOI18N
        OKJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(OKJButton);

        cancelJButton.setText("Cancel");
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(cancelJButton);

        add(commandsJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void languageJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_languageJComboBoxActionPerformed
    {//GEN-HEADEREND:event_languageJComboBoxActionPerformed
// TODO add your handling code here:
	langauge = (String) languageJComboBox.getSelectedItem();
	SanchayLanguages.fillEncodings(encodings, SanchayLanguages.getLanguageCode(langauge));
        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());

        if(langauge != null)
            stateKVProps.addProperty("LangEnc", SanchayLanguages.getLangEncCode(langauge, encoding));

        setDefaults(langauge);
    }//GEN-LAST:event_languageJComboBoxActionPerformed

    private void ssfCorpusJTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ssfCorpusJTextFieldCaretUpdate
// TODO add your handling code here:
	generateTaskNameAndPropFile();	
    }//GEN-LAST:event_ssfCorpusJTextFieldCaretUpdate

    private void mfeaturesJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mfeaturesJButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           mfFile = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateFileName(mfFile).equals("") == false)
           {
               mfeaturesJTextField.setText(mfFile);
           }
        }
    }//GEN-LAST:event_mfeaturesJButtonActionPerformed

    private void posTagsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_posTagsJButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           posTagsFile = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateFileName(posTagsFile).equals("") == false)
           {
               posTagsJTextField.setText(posTagsFile);
           }
        }
    }//GEN-LAST:event_posTagsJButtonActionPerformed

    private void phraseNamesJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phraseNamesJButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           phraseNamesFile = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateFileName(phraseNamesFile).equals("") == false)
           {
               phraseNamesJTextField.setText(phraseNamesFile);
           }
        }
    }//GEN-LAST:event_phraseNamesJButtonActionPerformed

    private void OKJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKJButtonActionPerformed
// TODO add your handling code here:
        boolean valid = true;
	
        langauge = (String) languageJComboBox.getSelectedItem();
        encoding = (String) encodingJComboBox.getSelectedItem();

        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());
        stateKVProps.addProperty("LangEnc", SanchayLanguages.getLangEncCode(langauge, encoding));
        
        taskPropFile = propJTextField.getText();
        ssfPropFile = ssfPropsJTextField.getText();
        fsPropFile = fsPropsJTextField.getText();
        mfFile = mfeaturesJTextField.getText();
        nmfFile = nmfeaturesJTextField.getText();
        psaFile = psFeaturesJTextField.getText();
        dsaFile = depFeaturesJTextField.getText();
        sraFile = semFeaturesJTextField.getText();

        posTagsFile = posTagsJTextField.getText();
        morphTagsFile = morphTagsJTextField.getText();
        phraseNamesFile = phraseNamesJTextField.getText();;
        
        ssfCorpusStoryFile = ssfCorpusJTextField.getText();
        ssfCorpusStoryUTF8File = ssfCorpusUTF8JTextField.getText();

        if((new File(ssfCorpusStoryFile)).exists() == false)
        {
			JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Either_you_did_not_select_a_file_or_the_file_does_not_exist."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);

            return;
        }
            
        if(validateTaskPropsFileName() == false || validateFileName(ssfPropFile).equals("") == true
                || validateFileName(mfFile).equals("") == true
                || validateFileName(nmfFile).equals("") == true
                || validateFileName(psaFile).equals("") == true
                || validateFileName(dsaFile).equals("") == true
                || validateFileName(sraFile).equals("") == true
                || validateFileName(fsPropFile).equals("") == true
                || validateFileName(ssfCorpusStoryFile).equals("") == true 
                /*|| validateFileName(ssfCorpusStoryUTF8File).equals("") == true*/
                || validateTaskName() == false)
        {
            valid = false;
        }
                
        if(valid == true)
        {
	    if(standAloneMode == false)
	    {
		AnnotationClient owner = (AnnotationClient) getOwner();
		PropertiesManager pm = owner.getPropertiesManager();

		String taskName = taskNameJTextField.getText();

		PropertiesTable tasks = (PropertiesTable) pm.getPropertyContainer("tasks", PropertyType.PROPERTY_TABLE);

		if(newTask == true)
		{
		    taskList.addElement(taskName);

		    String cols[] = {taskName, taskPropFile, GlobalProperties.getIntlString("UTF-8")};
		    tasks.addRow(cols);

		    try {
			pm.savePropertyContainer("tasks", PropertyType.PROPERTY_TABLE);
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error"), GlobalProperties.getIntlString("Could_not_save_task_list."), JOptionPane.ERROR_MESSAGE);
		    }
		}
		else
		{
		    taskName = (String) taskList.getSelectedItem();
		    Vector vec = tasks.getValues("TaskName", taskName, new String[]{"TaskKVPropFile"});

		    if(vec.size() != 1)
			JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error"), GlobalProperties.getIntlString("Could_not_save_task_information."), JOptionPane.ERROR_MESSAGE);

		    taskPropFile = (String) ((Vector) vec.get(0)).get(0);
		}
	    }
	    else
	    {
		if((new File(taskPropFile)).exists())
		{
		    KeyValueProperties kvp = new KeyValueProperties();

		    try {
			kvp.read(taskPropFile, GlobalProperties.getIntlString("UTF-8"));
		    } catch (FileNotFoundException ex) {
			ex.printStackTrace();
		    } catch (IOException ex) {
			ex.printStackTrace();
		    }

		    taskKVP.addProperty("CurrentPosition", kvp.getPropertyValue("CurrentPosition"));
		}
		else
		    taskKVP.addProperty("CurrentPosition", "1");
	    }

            taskKVP.addProperty("Language", SanchayLanguages.getLangEncCode(langauge, encoding));
            taskKVP.addProperty("TaskName", taskName);
            taskKVP.addProperty("TaskPropFile", taskPropFile);
            taskKVP.addProperty("TaskPropCharset", GlobalProperties.getIntlString("UTF-8"));
            taskKVP.addProperty("SSFPropFile", ssfPropFile);
            taskKVP.addProperty("SSFPropCharset", GlobalProperties.getIntlString("UTF-8"));
            taskKVP.addProperty("FSPropFile", fsPropFile);
            taskKVP.addProperty("FSPropCharset", GlobalProperties.getIntlString("UTF-8"));
            taskKVP.addProperty("MFeaturesFile", mfFile);
            taskKVP.addProperty("OFeaturesFile", nmfFile);
            taskKVP.addProperty("PAttributesFile", psaFile);
            taskKVP.addProperty("DAttributesFile", dsaFile);
            taskKVP.addProperty("SAttributesFile", sraFile);
            taskKVP.addProperty("MFeaturesCharset", GlobalProperties.getIntlString("UTF-8"));
            taskKVP.addProperty("SSFCorpusStoryFile", ssfCorpusStoryFile);
            taskKVP.addProperty("SSFCorpusCharset", GlobalProperties.getIntlString("UTF-8"));
            taskKVP.addProperty("SSFCorpusStoryUTF8File", ssfCorpusStoryUTF8File);

            taskKVP.addProperty("POSTagsFile", posTagsFile);
            taskKVP.addProperty("MorphTagsFile", morphTagsFile);
            taskKVP.addProperty("POSTagsCharset", GlobalProperties.getIntlString("UTF-8"));

            taskKVP.addProperty("PhraseNamesFile", phraseNamesFile);
            taskKVP.addProperty("PhraseNamesCharset", GlobalProperties.getIntlString("UTF-8"));
            
            if(newTask == true && standAloneMode == false)
                taskKVP.addProperty("CurrentPosition", "1");

	    if(saveTaskFileJCheckBox.isSelected())
	    {
		try {
			taskKVP.save(taskPropFile, GlobalProperties.getIntlString("UTF-8"));
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error"), GlobalProperties.getIntlString("Could_not_save_task_information."), JOptionPane.ERROR_MESSAGE);
		}
	    }
        }
        
	if(standAloneMode == false)
	    dialog.setVisible(false);
	else
	{
	    showWorkDialog();
	}
        
        SanchayClientsStateData.save();            
    }//GEN-LAST:event_OKJButtonActionPerformed

    private void ssfCorpusUTF8JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ssfCorpusUTF8JButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           ssfCorpusStoryUTF8File = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateFileName(ssfCorpusStoryUTF8File).equals("") == false)
           {
               ssfCorpusUTF8JTextField.setText(ssfCorpusStoryUTF8File);
           }
        }
    }//GEN-LAST:event_ssfCorpusUTF8JButtonActionPerformed

    private void ssfCorpusJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ssfCorpusJButtonActionPerformed
// TODO add your handling code here:
	String path = null;
        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());

        if(ssfCorpusStoryFile != null) {
            File sfile = new File(ssfCorpusStoryFile);

            if(sfile.exists() && sfile.getParentFile() != null)
                path = sfile.getParent();
            else
                path = stateKVProps.getPropertyValue("CurrentDir");
        }
        else
            path = stateKVProps.getPropertyValue("CurrentDir");

	JFileChooser chooser = null;

	if(path != null)
	    chooser = new JFileChooser(path);
	else
	    chooser = new JFileChooser();
	    
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           ssfCorpusStoryFile = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateFileName(ssfCorpusStoryFile).equals("") == false)
           {
               ssfCorpusJTextField.setText(ssfCorpusStoryFile);
	       generateTaskNameAndPropFile();
               stateKVProps.addProperty("CurrentDir", chooser.getSelectedFile().getParent());
//	       
//	       File cfile = new File(ssfCorpusStoryFile);
//	       
//	       if(standAloneMode || taskName.equals(""))
//	       {
//		    taskName = cfile.getName();
//		    taskNameJTextField.setText(taskName);
//	       }
//	       
//	       if(standAloneMode || taskPropFile.equals(""))
//	       {
//		    taskPropFile = "task-" + taskName;
//		    File tpfile = new File(cfile.getParent(), taskPropFile);
//		    taskPropFile = tpfile.getAbsolutePath();
//		    propJTextField.setText(taskPropFile);
//	       }
           }
        }
    }//GEN-LAST:event_ssfCorpusJButtonActionPerformed

    private void fsPropsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fsPropsJButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           fsPropFile = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateFileName(fsPropFile).equals("") == false)
           {
               fsPropsJTextField.setText(fsPropFile);
           }
        }
    }//GEN-LAST:event_fsPropsJButtonActionPerformed

    private void ssfPropsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ssfPropsJButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           ssfPropFile = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateFileName(ssfPropFile).equals("") == false)
           {
               ssfPropsJTextField.setText(ssfPropFile);
           }
        }
    }//GEN-LAST:event_ssfPropsJButtonActionPerformed

    private void propertiesJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertiesJButtonActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           taskPropFile = chooser.getSelectedFile().getAbsolutePath();
           
           if(validateTaskPropsFileName() == true)
           {
               propJTextField.setText(taskPropFile); 
           }
        }
    }//GEN-LAST:event_propertiesJButtonActionPerformed

    private void morphTagsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_morphTagsJButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           morphTagsFile = chooser.getSelectedFile().getAbsolutePath();

           if(validateFileName(morphTagsFile).equals("") == false)
           {
               posTagsJTextField.setText(morphTagsFile);
           }
        }
}//GEN-LAST:event_morphTagsJButtonActionPerformed

    private void depFeaturesJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depFeaturesJButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           dsaFile = chooser.getSelectedFile().getAbsolutePath();

           if(validateFileName(dsaFile).equals("") == false)
           {
               depFeaturesJTextField.setText(dsaFile);
           }
        }
}//GEN-LAST:event_depFeaturesJButtonActionPerformed

    private void psFeaturesJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_psFeaturesJButtonActionPerformed
    {//GEN-HEADEREND:event_psFeaturesJButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           psaFile = chooser.getSelectedFile().getAbsolutePath();

           if(validateFileName(psaFile).equals("") == false)
           {
               psFeaturesJTextField.setText(psaFile);
           }
        }
}//GEN-LAST:event_psFeaturesJButtonActionPerformed

    private void nmfeaturesJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nmfeaturesJButtonActionPerformed
    {//GEN-HEADEREND:event_nmfeaturesJButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           nmfFile = chooser.getSelectedFile().getAbsolutePath();

           if(validateFileName(nmfFile).equals("") == false)
           {
               nmfeaturesJTextField.setText(nmfFile);
           }
        }
}//GEN-LAST:event_nmfeaturesJButtonActionPerformed

    private void semFeaturesJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_semFeaturesJButtonActionPerformed
    {//GEN-HEADEREND:event_semFeaturesJButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           sraFile = chooser.getSelectedFile().getAbsolutePath();

           if(validateFileName(sraFile).equals("") == false)
           {
               semFeaturesJTextField.setText(sraFile);
           }
        }
}//GEN-LAST:event_semFeaturesJButtonActionPerformed

    private void moreOptionsJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moreOptionsJButtonActionPerformed
    {//GEN-HEADEREND:event_moreOptionsJButtonActionPerformed
        // TODO add your handling code here:
        showMoreOptions(evt);
    }//GEN-LAST:event_moreOptionsJButtonActionPerformed

    private void encodingJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_encodingJComboBoxActionPerformed
    {//GEN-HEADEREND:event_encodingJComboBoxActionPerformed
        // TODO add your handling code here:
	encoding = (String) encodingJComboBox.getSelectedItem();
        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SYNTACTIC_ANNOTATION.toString());

        if(encoding != null)
            stateKVProps.addProperty("LangEnc", SanchayLanguages.getLangEncCode(langauge, encoding));
    }//GEN-LAST:event_encodingJComboBoxActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        // TODO add your handling code here:
        closeDialog();
    }//GEN-LAST:event_cancelJButtonActionPerformed

    public void closeDialog()
    {
        this.setVisible(false);
        dialog.dispose();        
    }
    
    public void showMoreOptions(java.awt.event.ActionEvent e)
    {
        if(moreOptionsShown == true)
        {
            moreOptionsShown = false;

            auxJPanel.setVisible(false);

            remove(mainJPanel);
            remove(auxJPanel);
            remove(commandsJPanel);

            add(mainJPanel, BorderLayout.CENTER);
            add(commandsJPanel, BorderLayout.SOUTH);

            moreOptionsJButton.setText(GlobalProperties.getIntlString("More_Options"));
        }
        else
        {
            moreOptionsShown = true;
            
            auxJPanel.setVisible(true);

            remove(mainJPanel);
            remove(auxJPanel);
            remove(commandsJPanel);

            add(mainJPanel, BorderLayout.NORTH);
            add(auxJPanel, BorderLayout.CENTER);
            add(commandsJPanel, BorderLayout.SOUTH);

            moreOptionsJButton.setText(GlobalProperties.getIntlString("Less_Options"));
        }

        if(dialog != null)
        {
            dialog.pack();
            UtilityFunctions.centre(dialog);
        }
    }


    private void generateTaskNameAndPropFile()
    {
	String ssfFileStr = ssfCorpusJTextField.getText();

	File cfile = new File(ssfFileStr);
	
	if(cfile.exists() == false)
	    return;

	if(standAloneMode || taskName.equals(""))
	{
	    taskName = cfile.getName();
	    taskNameJTextField.setText(taskName);
	}

	if(standAloneMode || taskPropFile.equals(""))
	{
	    taskPropFile = "task-" + taskName;
	    File tpfile = new File(cfile.getParent(), taskPropFile);
	    taskPropFile = tpfile.getAbsolutePath();
	    propJTextField.setText(taskPropFile);
	}
    }
    
    private boolean validateTaskName()
    {
	if(AnnotationClient.class.isInstance(getOwner()) == false)
	    return true;

	AnnotationClient owner = (AnnotationClient) getOwner();
        PropertiesManager pm = owner.getPropertiesManager();
        
        String taskName = taskNameJTextField.getText();
        
        PropertiesTable tasks = (PropertiesTable) pm.getPropertyContainer("tasks", PropertyType.PROPERTY_TABLE);
        Vector rows = tasks.getRows("TaskName", taskName);
        
        if(rows.size() == 1)
        {
            JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Task_name_alresdy_exists:_") + taskName, GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private boolean validateTaskPropsFileName()
    {
	if(AnnotationClient.class.isInstance(getOwner()) == false)
	    return true;

        AnnotationClient owner = (AnnotationClient) getOwner();
        String ws = owner.getWorkspace();
        
        if(taskPropFile.startsWith(File.separator) == true)
        {
            if(taskPropFile.startsWith(ws) == false)
            {
                JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Files_should_be_in_the_workspace_directory:_") + ws, GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
//            else
//            {
//                taskPropFile = taskPropFile.substring(ws.length());
//                taskPropFile.replaceFirst(ws, "");
//            }
        }
        
        return true;
    }
    
    private void configure()
    {
        taskPropFile = taskKVP.getPropertyValue("TaskPropFile");
        
        ssfPropFile = taskKVP.getPropertyValue("SSFPropFile");
        fsPropFile = taskKVP.getPropertyValue("FSPropFile");
        mfFile = taskKVP.getPropertyValue("MFeaturesFile");
        nmfFile = taskKVP.getPropertyValue("OFeaturesFile");
        psaFile = taskKVP.getPropertyValue("PAttributesFile");
        dsaFile = taskKVP.getPropertyValue("DAttributesFile");
        sraFile = taskKVP.getPropertyValue("SAttributesFile");
        ssfCorpusStoryFile = taskKVP.getPropertyValue("SSFCorpusStoryFile");
        ssfCorpusStoryUTF8File = taskKVP.getPropertyValue("SSFCorpusStoryUTF8File");

        posTagsFile = taskKVP.getPropertyValue("POSTagsFile");
        morphTagsFile = taskKVP.getPropertyValue("MorphTagsFile");
        phraseNamesFile = taskKVP.getPropertyValue("PhraseNamesFile");

        propJTextField.setText(taskPropFile);
        ssfPropsJTextField.setText(ssfPropFile);
        fsPropsJTextField.setText(fsPropFile);
        mfeaturesJTextField.setText(mfFile);
        mfeaturesJTextField.setText(nmfFile);
        psFeaturesJTextField.setText(psaFile);
        depFeaturesJTextField.setText(dsaFile);
        semFeaturesJTextField.setText(sraFile);
        ssfCorpusJTextField.setText(ssfCorpusStoryFile);
        ssfCorpusUTF8JTextField.setText(ssfCorpusStoryUTF8File);
        
        posTagsJTextField.setText(posTagsFile);
        morphTagsJTextField.setText(morphTagsFile);
        phraseNamesJTextField.setText(phraseNamesFile);
    }

    private String validateFileName(String fn)
    {
	if(AnnotationClient.class.isInstance(getOwner()) == false)
	    return fn;

        AnnotationClient owner = (AnnotationClient) getOwner();
        String ws = owner.getWorkspace();
        
        if(fn.startsWith(File.separator) == true)
        {
            if(fn.startsWith(ws) == false)
            {
                JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Files_should_be_in_the_workspace_directory:_") + ws, GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return "";
            }
//            else
//            {
//                fn = taskPropFile.substring(ws.length());
//                fn.replaceFirst(ws, "");
//            }
        }
        
        return fn;
    }

    public void setDefaults(String lang)
    {
        langauge = lang;
	encoding = GlobalProperties.getIntlString("UTF-8");

	taskName = "";
        taskPropFile = "";

        ssfPropFile = GlobalProperties.resolveRelativePath("props/ssf-props.txt");
        fsPropFile = GlobalProperties.resolveRelativePath("props/fs-props.txt");
        mfFile = GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt");
        nmfFile = GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt");
        psaFile = GlobalProperties.resolveRelativePath("props/ps-attribs.txt");
        dsaFile = GlobalProperties.resolveRelativePath("props/dep-attribs.txt");
        sraFile = GlobalProperties.resolveRelativePath("props/sem-attribs.txt");

        ssfCorpusStoryFile = "";
        ssfCorpusStoryUTF8File = "";

        morphTagsFile = GlobalProperties.resolveRelativePath("workspace/syn-annotation/morph-tags.txt");

//        posTagsFile = GlobalProperties.resolveRelativePath("workspace/syn-annotation/pos-tags.txt");
//        phraseNamesFile = GlobalProperties.resolveRelativePath("workspace/syn-annotation/phrase-names.txt");

        posTagsFile = GlobalProperties.resolveRelativePath(SSFNode.getPOSTagsPath("workspace/syn-annotation", SanchayLanguages.getLangEncCode(langauge, encoding)));
        phraseNamesFile = GlobalProperties.resolveRelativePath(SSFNode.getPhraseNamesPath("workspace/syn-annotation", SanchayLanguages.getLangEncCode(langauge, encoding)));

        languageJComboBox.setSelectedItem(langauge);
        encodingJComboBox.setSelectedItem(encoding);

    	taskNameJTextField.setText(taskName);
        propJTextField.setText(taskPropFile);

        ssfPropsJTextField.setText(ssfPropFile);
        fsPropsJTextField.setText(fsPropFile);
        mfeaturesJTextField.setText(mfFile);
        nmfeaturesJTextField.setText(nmfFile);
        psFeaturesJTextField.setText(psaFile);
        depFeaturesJTextField.setText(dsaFile);
        semFeaturesJTextField.setText(sraFile);

        ssfCorpusJTextField.setText(ssfCorpusStoryFile);
        ssfCorpusUTF8JTextField.setText(ssfCorpusStoryUTF8File);

        posTagsJTextField.setText(posTagsFile);
        morphTagsJTextField.setText(morphTagsFile);
        phraseNamesJTextField.setText(phraseNamesFile);
    }
    
    public void setDefaults()
    {
	langauge = "Hindi";

        setDefaults(langauge);
    }
    
    public Frame getOwner()
    {
        return owner;
    }
    
    public void setOwner(Frame f)
    {
        owner = (JFrame) f;
    }
    
    public void setDialog(JDialog d)
    {
        dialog = d;

        if(dialog != null && dialog.getRootPane() != null)
            dialog.getRootPane().setDefaultButton(OKJButton);
    }
    
    public void setTaskList(DefaultComboBoxModel l)
    {
        taskList = l;
    }
    
    public KeyValueProperties getTaskProps()
    {
	return taskKVP;
    }
    
    private void showWorkDialog()
    {
        if(workJPanel != null)
        {
            workJPanel.init();
            workJPanel.setTaskProps(taskKVP);
            workJPanel.setOwner(this.getOwner());
            workJPanel.setTaskName(taskName);
            workJPanel.configure();
            
            dialog.setVisible(false);
            
            return;
        }
        
	workDialog = null;
	
	if(dialog != null)
	    workDialog = new JDialog(dialog, taskName, true);
	else
	    workDialog = new JDialog(owner, taskName, true);
        
//	SyntacticAnnotationWorkJPanel workJPanelLocal = new SyntacticAnnotationWorkJPanel(taskKVP);
	workJPanel = new SyntacticAnnotationWorkJPanel(taskKVP);

        workJPanel.setOwner(this.getOwner());
        workJPanel.setDialog(workDialog);
        workJPanel.setTaskName(taskName);
        workJPanel.configure();
        
        workDialog.add(workJPanel);
	
        int inset = 5;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        workDialog.setBounds(inset, inset,
		screenSize.width  - inset*2,
		screenSize.height - inset*9);

	workDialog.setVisible(true);
	
//	((AnnotationClient) owner).setWorkJPanel(workJPanel);
    }
    
    public static KeyValueProperties getDefaultTaskKVP()
    {
        KeyValueProperties taskKVP = new KeyValueProperties();
        
        taskKVP.addProperty("Language", "hin::utf8");
        taskKVP.addProperty("TaskName", "Some Task");
        taskKVP.addProperty("TaskPropFile", "");
        taskKVP.addProperty("TaskPropCharset", "UTF-8");
        taskKVP.addProperty("SSFPropFile", "props/ssf-props.txt");
        taskKVP.addProperty("SSFPropCharset", "UTF-8");
        taskKVP.addProperty("FSPropFile", "props/fs-props.txt");
        taskKVP.addProperty("FSPropCharset", "UTF-8");
        taskKVP.addProperty("MFeaturesFile", "props/fs-mandatory-attribs.txt");
        taskKVP.addProperty("OFeaturesFile", "props/fs-other-attribs.txt");
        taskKVP.addProperty("PAttributesFile", "props/ps-attribs.txt");
        taskKVP.addProperty("DAttributesFile", "props/dep-attribs.txt");
        taskKVP.addProperty("SAttributesFile", "props/sem-attribs.txt");
        taskKVP.addProperty("MFeaturesCharset", "UTF-8");
        taskKVP.addProperty("SSFCorpusStoryFile", "");
        taskKVP.addProperty("SSFCorpusCharset", "UTF-8");
        taskKVP.addProperty("SSFCorpusStoryUTF8File", "");

        taskKVP.addProperty("POSTagsFile", "workspace/syn-annotation/pos-tags.txt");
        taskKVP.addProperty("MorphTagsFile", "workspace/syn-annotation/morph-tags.txt");
        taskKVP.addProperty("POSTagsCharset", "UTF-8");

        taskKVP.addProperty("PhraseNamesFile", "workspace/syn-annotation/phrase-names.txt");
        taskKVP.addProperty("PhraseNamesCharset", "UTF-8");

        taskKVP.addProperty("CurrentPosition", "1");

        return taskKVP;
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
	
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame(GlobalProperties.getIntlString("Sanchay_Syntactic_Annotation"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	SyntacticAnnotationTaskSetupJPanel newContentPane = new SyntacticAnnotationTaskSetupJPanel(true);
        
        newContentPane.setOwner(frame);
	
	newContentPane.owner = frame;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
//        frame.pack();
	
        int xinset = 280;
        int yinset = 140;
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton OKJButton;
    public javax.swing.JPanel auxJPanel;
    public javax.swing.JPanel booleanOptionsJPanel;
    public javax.swing.JButton cancelJButton;
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JButton depFeaturesJButton;
    public javax.swing.JLabel depFeaturesJLabel;
    public javax.swing.JPanel depFeaturesJPanel;
    public javax.swing.JTextField depFeaturesJTextField;
    public javax.swing.JComboBox encodingJComboBox;
    public javax.swing.JLabel encodingJLabel;
    public javax.swing.JPanel encodingJPanel;
    public javax.swing.JButton fsPropsJButton;
    public javax.swing.JLabel fsPropsJLabel;
    public javax.swing.JPanel fsPropsJPanel;
    public javax.swing.JTextField fsPropsJTextField;
    public javax.swing.JComboBox languageJComboBox;
    public javax.swing.JLabel languageJLabel;
    public javax.swing.JPanel languageJPanel;
    public javax.swing.JPanel mainJPanel;
    public javax.swing.JButton mfeaturesJButton;
    public javax.swing.JLabel mfeaturesJLabel;
    public javax.swing.JPanel mfeaturesJPanel;
    public javax.swing.JTextField mfeaturesJTextField;
    public javax.swing.JButton moreOptionsJButton;
    public javax.swing.JButton morphTagsJButton;
    public javax.swing.JLabel morphTagsJLabel;
    public javax.swing.JPanel morphTagsJPanel;
    public javax.swing.JTextField morphTagsJTextField;
    public javax.swing.JButton nmfeaturesJButton;
    public javax.swing.JLabel nmfeaturesJLabel;
    public javax.swing.JPanel nmfeaturesJPanel;
    public javax.swing.JTextField nmfeaturesJTextField;
    public javax.swing.JButton phraseNamesJButton;
    public javax.swing.JLabel phraseNamesJLabel;
    public javax.swing.JPanel phraseNamesJPanel;
    public javax.swing.JTextField phraseNamesJTextField;
    public javax.swing.JButton posTagsJButton;
    public javax.swing.JLabel posTagsJLabel;
    public javax.swing.JPanel posTagsJPanel;
    public javax.swing.JTextField posTagsJTextField;
    public javax.swing.JLabel propJLabel;
    public javax.swing.JPanel propJPanel;
    public javax.swing.JTextField propJTextField;
    public javax.swing.JButton propertiesJButton;
    public javax.swing.JButton psFeaturesJButton;
    public javax.swing.JLabel psFeaturesJLabel;
    public javax.swing.JPanel psFeaturesJPanel;
    public javax.swing.JTextField psFeaturesJTextField;
    public javax.swing.JCheckBox saveTaskFileJCheckBox;
    public javax.swing.JButton semFeaturesJButton;
    public javax.swing.JLabel semFeaturesJLabel;
    public javax.swing.JPanel semFeaturesJPanel;
    public javax.swing.JTextField semFeaturesJTextField;
    public javax.swing.JButton ssfCorpusJButton;
    public javax.swing.JLabel ssfCorpusJLabel;
    public javax.swing.JPanel ssfCorpusJPanel;
    public javax.swing.JTextField ssfCorpusJTextField;
    public javax.swing.JButton ssfCorpusUTF8JButton;
    public javax.swing.JLabel ssfCorpusUTF8JLabel;
    public javax.swing.JPanel ssfCorpusUTF8JPanel;
    public javax.swing.JTextField ssfCorpusUTF8JTextField;
    public javax.swing.JButton ssfPropsJButton;
    public javax.swing.JLabel ssfPropsJLabel;
    public javax.swing.JPanel ssfPropsJPanel;
    public javax.swing.JTextField ssfPropsJTextField;
    public javax.swing.JPanel taskJPanel;
    public javax.swing.JLabel taskNameJLabel;
    public javax.swing.JTextField taskNameJTextField;
    // End of variables declaration//GEN-END:variables
    
}
