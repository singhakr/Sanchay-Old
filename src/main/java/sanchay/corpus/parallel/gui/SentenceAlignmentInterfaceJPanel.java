/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SentenceAlignmentInterfaceJPanel.java
 *
 * Created on 3 Nov, 2009, 11:22:12 PM
 */

package sanchay.corpus.parallel.gui;

import com.googlecode.starrating.StarRating;
import com.googlecode.starrating.StarTableCellEditor;
import com.googlecode.starrating.StarTableCellRenderer;
import com.googlecode.starrating.demo.StarComboRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import sanchay.GlobalProperties;
import sanchay.common.SanchayClientsStateData;
import sanchay.common.types.ClientType;
import sanchay.corpus.parallel.AlignmentBlock;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.parallel.aligner.DefaultSentenceAligner;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayLanguages;
import sanchay.gui.scroll.Rule;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.MultiLineCellEditor;
import sanchay.table.gui.MultiLineCellRenderer;
import sanchay.table.gui.SanchayJTable;
import sanchay.tree.SanchayEdges;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class SentenceAlignmentInterfaceJPanel extends javax.swing.JPanel
        implements WindowListener, JPanelDialog, sanchay.gui.clients.SanchayClient, ItemListener, AlignmentEventListener {

    protected ClientType clientType = ClientType.SENTENCE_ALIGNMENT_INTERFACE;

    protected static KeyValueProperties stateKVProps;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected String title = "";

    protected String srcLangEnc = "eng::utf8";
    protected String tgtLangEnc = "hin::utf8";

    protected String srcCharset = "UTF-8";
    protected String tgtCharset = "UTF-8";

    protected String srcFilePath = GlobalProperties.resolveRelativePath("data/parallel-corpus/eng-1.txt");
    protected String tgtFilePath = GlobalProperties.resolveRelativePath("data/parallel-corpus/hin-1.txt");

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

    protected SSFStory srcSSFStory;
    protected SSFStory tgtSSFStory;

    protected AlignmentBlock alignmentBlock;
    protected AlignmentUnit alignmentUnit;

    protected SanchayTableModel alignmentModel;
    protected SanchayJTable alignmentJTable;

    protected MultiLineCellRenderer srcCellRenderer;
    protected MultiLineCellRenderer tgtCellRenderer;

    protected MultiLineCellEditor srcCellEditor;
    protected MultiLineCellEditor tgtCellEditor;

    protected Rule rowView;

    protected boolean langEncFilled;
    protected boolean sentenceAligner;

    protected DefaultSentenceAligner aligner;
    protected DefaultComboBoxModel starModel;
    
    protected StarRating srating;

    /** Creates new form SentenceAlignmentInterfaceJPanel */
    public SentenceAlignmentInterfaceJPanel(boolean sentenceAligner) {
        this.sentenceAligner = sentenceAligner;
        
        srating = new StarRating();
        
        createStarModel();
        initComponents();
        
        if(sentenceAligner)
        {
            aligner = new DefaultSentenceAligner();
        }
        else
        {
            runSenAlignJButton.setVisible(false);
        }

        srcLangauges = new DefaultComboBoxModel();
        srcEncodings = new DefaultComboBoxModel();

        tgtLangauges = new DefaultComboBoxModel();
        tgtEncodings = new DefaultComboBoxModel();

        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SENTENCE_ALIGNMENT_INTERFACE.toString());

        if(stateKVProps.getPropertyValue("srcLangEnc") != null && stateKVProps.getPropertyValue("srcLangEnc").equals("") == false) {
            srcLangEnc = stateKVProps.getPropertyValue("srcLangEnc");
        }

        if(stateKVProps.getPropertyValue("tgtLangEnc") != null && stateKVProps.getPropertyValue("tgtLangEnc").equals("") == false) {
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

//        srcTextJScrollPane.getVerticalScrollBar().setModel(tgtTextJScrollPane.getVerticalScrollBar().getModel());

        alignmentBlock = new AlignmentBlock(AlignmentBlock.SENTENCE_ALIGNMENT_MODE);
        alignmentUnit = new AlignmentUnit();

        alignmentModel = new SanchayTableModel(4, 4);

        alignmentModel.addRow();
        alignmentModel.addRow();
        alignmentModel.addRow();
        alignmentModel.addRow();

        prepareAlignment();

        srcFileJTextField.setText(srcFilePath);
        tgtFileJTextField.setText(tgtFilePath);

        loadState();
        loadData();

        langEncFilled = true;

        navigationJPanel.setVisible(false);        
        batchSizeJComboBox.setVisible(false);
        batchSizeJLabel.setVisible(false);
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
        mainJPanel = new javax.swing.JPanel();
        bottomJPanel = new javax.swing.JPanel();
        optionsJPanel = new javax.swing.JPanel();
        batchSizeJPanel = new javax.swing.JPanel();
        batchSizeJLabel = new javax.swing.JLabel();
        batchSizeJComboBox = new javax.swing.JComboBox();
        checkbox_enabled = new javax.swing.JCheckBox();
        checkbox_label = new javax.swing.JCheckBox();
        combo_maxRate = new javax.swing.JComboBox();
        combo_starImage = new javax.swing.JComboBox();
        loadJPanel = new javax.swing.JPanel();
        runSenAlignJButton = new javax.swing.JButton();
        clearJButton = new javax.swing.JButton();
        resetJButton = new javax.swing.JButton();
        loadJButton = new javax.swing.JButton();
        alignJSeparator = new javax.swing.JSeparator();
        cleanupJButton = new javax.swing.JButton();
        saveJButton = new javax.swing.JButton();
        navigationJPanel = new javax.swing.JPanel();
        firstJButton = new javax.swing.JButton();
        previousJButton = new javax.swing.JButton();
        nextJButton = new javax.swing.JButton();
        lastJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout(0, 5));

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

        add(topJPanel, java.awt.BorderLayout.NORTH);

        mainJPanel.setLayout(new java.awt.BorderLayout());
        add(mainJPanel, java.awt.BorderLayout.CENTER);

        bottomJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        optionsJPanel.setLayout(new java.awt.BorderLayout());

        batchSizeJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        batchSizeJLabel.setText("Batch Size:");
        batchSizeJLabel.setToolTipText("Batch size to be displayed in one page for alignment");
        batchSizeJPanel.add(batchSizeJLabel);

        batchSizeJComboBox.setEnabled(false);
        batchSizeJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batchSizeJComboBoxActionPerformed(evt);
            }
        });
        batchSizeJPanel.add(batchSizeJComboBox);

        checkbox_enabled.setSelected(srating.isRatingEnabled());
        checkbox_enabled.setText("enabled");
        checkbox_enabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkbox_enabledActionPerformed(evt);
            }
        });
        batchSizeJPanel.add(checkbox_enabled);

        checkbox_label.setSelected(srating.isValueLabelVisible());
        checkbox_label.setText("Show label");
        checkbox_label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkbox_labelActionPerformed(evt);
            }
        });
        batchSizeJPanel.add(checkbox_label);

        combo_maxRate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3", "4", "5", "6", "7", "8", "9", "10", "15" }));
        combo_maxRate.setToolTipText("Select the maximum rate");
        combo_maxRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_maxRateActionPerformed(evt);
            }
        });
        batchSizeJPanel.add(combo_maxRate);

        combo_starImage.setModel(starModel);
        combo_starImage.setRenderer(new StarComboRenderer());
        combo_starImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_starImageActionPerformed(evt);
            }
        });
        batchSizeJPanel.add(combo_starImage);

        optionsJPanel.add(batchSizeJPanel, java.awt.BorderLayout.CENTER);

        loadJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        runSenAlignJButton.setMnemonic('A');
        runSenAlignJButton.setText("Auto Align");
        runSenAlignJButton.setToolTipText("Run Sentence Aligner to automatically align the sentences, which can then be manually corrected");
        runSenAlignJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runSenAlignJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(runSenAlignJButton);

        clearJButton.setMnemonic('E');
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

        loadJButton.setMnemonic('O');
        loadJButton.setText("Load");
        loadJButton.setToolTipText("Load the data");
        loadJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(loadJButton);

        alignJSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        alignJSeparator.setPreferredSize(new java.awt.Dimension(3, 20));
        loadJPanel.add(alignJSeparator);

        cleanupJButton.setMnemonic('C');
        cleanupJButton.setText("Cleanup");
        cleanupJButton.setToolTipText("Cleanup the alignments (remove empty rows pairs etc.)");
        cleanupJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanupJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(cleanupJButton);

        saveJButton.setMnemonic('S');
        saveJButton.setText("Save");
        saveJButton.setToolTipText("Save the alignment");
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        loadJPanel.add(saveJButton);

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

    private void srcFileJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcFileJButtonActionPerformed
    {//GEN-HEADEREND:event_srcFileJButtonActionPerformed
        // TODO add your handling code here:
        String path;

        if(srcFilePath != null) {
            File sfile = new File(srcFilePath);

            if(sfile.exists() && sfile.getParentFile() != null) {
                path = sfile.getParent();
            }
            else {
                path = stateKVProps.getPropertyValue("CurrentDir");
            }
        }
        else {
            path = stateKVProps.getPropertyValue("CurrentDir");
        }

        JFileChooser chooser;

        if(path != null) {
            chooser = new JFileChooser(path);
        }
        else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           srcFilePath = chooser.getSelectedFile().getAbsolutePath();

           srcFileJTextField.setText(srcFilePath);
           stateKVProps.addProperty("CurrentDir", chooser.getSelectedFile().getParent());
        }
    }//GEN-LAST:event_srcFileJButtonActionPerformed

    private void tgtFileJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtFileJButtonActionPerformed
    {//GEN-HEADEREND:event_tgtFileJButtonActionPerformed
        // TODO add your handling code here:
        String path;

        if(tgtFilePath != null) {
            File tfile = new File(tgtFilePath);

            if(tfile.exists() && tfile.getParentFile() != null) {
                path = tfile.getParent();
            }
            else {
                path = stateKVProps.getPropertyValue("CurrentDir");
            }
        }
        else {
            path = stateKVProps.getPropertyValue("CurrentDir");
        }

        JFileChooser chooser;

        if(path != null) {
            chooser = new JFileChooser(path);
        }
        else {
            chooser = new JFileChooser();
        }

        int returnVal = chooser.showOpenDialog(this);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
           tgtFilePath = chooser.getSelectedFile().getAbsolutePath();

           tgtFileJTextField.setText(tgtFilePath);
           stateKVProps.addProperty("CurrentDir", chooser.getSelectedFile().getParent());
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
    }//GEN-LAST:event_firstJButtonActionPerformed

    private void previousJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_previousJButtonActionPerformed
    {//GEN-HEADEREND:event_previousJButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_previousJButtonActionPerformed

    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextJButtonActionPerformed
    {//GEN-HEADEREND:event_nextJButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nextJButtonActionPerformed

    private void lastJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lastJButtonActionPerformed
    {//GEN-HEADEREND:event_lastJButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lastJButtonActionPerformed

    private void batchSizeJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_batchSizeJComboBoxActionPerformed
    {//GEN-HEADEREND:event_batchSizeJComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_batchSizeJComboBoxActionPerformed

    private void srcLanguageJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcLanguageJComboBoxActionPerformed
    {//GEN-HEADEREND:event_srcLanguageJComboBoxActionPerformed
        // TODO add your handling code here:
        if(langEncFilled == false) {
            return;
        }
        
        srcLangauge = (String) srcLanguageJComboBox.getSelectedItem();
        SanchayLanguages.fillEncodings(srcEncodings, SanchayLanguages.getLanguageCode(srcLangauge));

        if(srcLangauge != null)
        {
            srcLangEnc = SanchayLanguages.getLangEncCode(srcLangauge, srcEncoding);
            stateKVProps.addProperty("srcLangEnc", srcLangEnc);

            UtilityFunctions.setComponentFont(srcCellRenderer, srcLangEnc);
        }
}//GEN-LAST:event_srcLanguageJComboBoxActionPerformed

    private void srcEncodingJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_srcEncodingJComboBoxActionPerformed
    {//GEN-HEADEREND:event_srcEncodingJComboBoxActionPerformed
        // TODO add your handling code here:
        if(langEncFilled == false) {
            return;
        }

        srcEncoding = (String) srcEncodingJComboBox.getSelectedItem();

        if(srcEncoding != null)
        {
            srcLangEnc = SanchayLanguages.getLangEncCode(srcLangauge, srcEncoding);
            stateKVProps.addProperty("srcEangEnc", srcLangEnc);

            UtilityFunctions.setComponentFont(srcCellRenderer, srcLangEnc);
        }
}//GEN-LAST:event_srcEncodingJComboBoxActionPerformed

    private void tgtLanguageJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtLanguageJComboBoxActionPerformed
    {//GEN-HEADEREND:event_tgtLanguageJComboBoxActionPerformed
        // TODO add your handling code here:
        if(langEncFilled == false) {
            return;
        }

        tgtLangauge = (String) tgtLanguageJComboBox.getSelectedItem();
        SanchayLanguages.fillEncodings(tgtEncodings, SanchayLanguages.getLanguageCode(tgtLangauge));

        if(tgtLangauge != null)
        {
            tgtLangEnc = SanchayLanguages.getLangEncCode(tgtLangauge, tgtEncoding);
            stateKVProps.addProperty("tgtLangEnc", tgtLangEnc);

            UtilityFunctions.setComponentFont(tgtCellRenderer, tgtLangEnc);
        }
}//GEN-LAST:event_tgtLanguageJComboBoxActionPerformed

    private void tgtEncodingJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgtEncodingJComboBoxActionPerformed
    {//GEN-HEADEREND:event_tgtEncodingJComboBoxActionPerformed
        // TODO add your handling code here:
        if(langEncFilled == false) {
            return;
        }

        tgtEncoding = (String) tgtEncodingJComboBox.getSelectedItem();

        if(tgtEncoding != null)
        {
            tgtLangEnc = SanchayLanguages.getLangEncCode(tgtLangauge, tgtEncoding);
            stateKVProps.addProperty("tgtLangEnc", tgtLangEnc);

            UtilityFunctions.setComponentFont(tgtCellRenderer, tgtLangEnc);
        }
}//GEN-LAST:event_tgtEncodingJComboBoxActionPerformed

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

    private void runSenAlignJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_runSenAlignJButtonActionPerformed
    {//GEN-HEADEREND:event_runSenAlignJButtonActionPerformed
        // TODO add your handling code here:
        clear();

        saveData();

        aligner.alignSSFStories(srcSSFStory, tgtSSFStory);

        srcSSFStory.saveAlignments();
        tgtSSFStory.saveAlignments();

        prepareAlignment();
}//GEN-LAST:event_runSenAlignJButtonActionPerformed

    private void cleanupJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanupJButtonActionPerformed
        // TODO add your handling code here:
        saveData();
        loadData();
    }//GEN-LAST:event_cleanupJButtonActionPerformed

    private void checkbox_enabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkbox_enabledActionPerformed
        srating.setEnabled(checkbox_enabled.isSelected());
    }//GEN-LAST:event_checkbox_enabledActionPerformed

    private void checkbox_labelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkbox_labelActionPerformed
        srating.setValueLabelVisible(checkbox_label.isSelected());
    }//GEN-LAST:event_checkbox_labelActionPerformed

    private void combo_maxRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_maxRateActionPerformed
        srating.setMaxRate(Integer.parseInt((String) combo_maxRate.getSelectedItem()));
        srating.validate();
        srating.repaint();
        this.validate();
        this.repaint();
    }//GEN-LAST:event_combo_maxRateActionPerformed

    private void combo_starImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_starImageActionPerformed
        ImageIcon im = (ImageIcon) combo_starImage.getSelectedItem();
        srating.changeStarImage(im);
        srating.validate();
        srating.repaint();
        this.validate();
        this.repaint();
    }//GEN-LAST:event_combo_starImageActionPerformed

    private void createStarModel() {
        starModel = new DefaultComboBoxModel();
        ImageIcon euro = new ImageIcon(getClass().getResource("/com/googlecode/starrating/images/euro.png"));
        euro.setDescription("Euro");
        ImageIcon face = new ImageIcon(getClass().getResource("/com/googlecode/starrating/images/face.png"));
        face.setDescription("Face");
        ImageIcon note = new ImageIcon(getClass().getResource("/com/googlecode/starrating/images/note.png"));
        note.setDescription("Note");
        ImageIcon star = new ImageIcon(getClass().getResource("/com/googlecode/starrating/images/star.png"));
        star.setDescription("Star");
        ImageIcon whiteStar = new ImageIcon(getClass().getResource("/com/googlecode/starrating/images/whiteStar.png"));
        whiteStar.setDescription("whiteStar");
        ImageIcon[] icons = {euro, face, note, star, whiteStar};
        starModel = new DefaultComboBoxModel(icons);
    }
    
    private void initStarModel()
    {
//        createStarModel();
//        StarTableCellRenderer rend = new StarTableCellRenderer(srating, true, false);
//        StarTableCellEditor ed = new StarTableCellEditor(srating, true);
        StarTableCellRenderer rend = new StarTableCellRenderer(true, false);
        StarTableCellEditor ed = new StarTableCellEditor(true);
        alignmentJTable.getColumnModel().getColumn(1).setCellRenderer(rend);
        alignmentJTable.getColumnModel().getColumn(1).setCellEditor(ed);
//        alignmentJTable.setRowHeight(22);

        /**
         * Adds a CellEditorListener to the CellEditor to receive changes in the cells value
         */
        ed.addCellEditorListener(new CellEditorListener() {

            @Override
          public void editingStopped(ChangeEvent e) {
            StarTableCellEditor s = (StarTableCellEditor) e.getSource();
//            System.out.println("CellEditorListener notified (Editing stopped), value: " + s.getCellEditorValue());
          }

            @Override
          public void editingCanceled(ChangeEvent e) {
            StarTableCellEditor s = (StarTableCellEditor) e.getSource();
//            System.out.println("CellEditorListener notified (Editing Cancelled), value: " + s.getCellEditorValue());
          }
        });
        
        srating.setRate(2.5);
        srating.changeStarImage(new ImageIcon(StarRating.class.getResource(StarRating.EURO_IMAGE)));
        
        /**
         * Adds a PropertyChangeListener to the StarRating and listens for property
         * {@link StarRating#RATE_CHANGED} change.
         */
        srating.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
          public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(StarRating.RATE_CHANGED)) {
//              System.out.println("PropertyChangeListener notified (property: " + StarRating.RATE_CHANGED + "), value: " + evt.getNewValue());
            }
          }
        });
    }        
    
    @Override
    public ClientType getClientType()
    {
        return clientType;
    }

    private void clear()
    {
        srcSSFStory.clearAlignments();
        tgtSSFStory.clearAlignments();

        srcSSFStory.saveAlignments();
        tgtSSFStory.saveAlignments();

        prepareAlignment();
    }

    private void reset()
    {
        loadData();
    }

    private void prepareAlignment()
    {
        if(srcSSFStory == null) {
            srcSSFStory = new SSFStoryImpl();
        }
        if(tgtSSFStory == null) {
            tgtSSFStory = new SSFStoryImpl();
        }

        alignmentBlock.prepareAlignment(AlignmentBlock.SENTENCE_ALIGNMENT_MODE, srcSSFStory, tgtSSFStory);

        prepareAlignmentTable();
    }

    public void prepareAlignmentTable()
    {
        alignmentModel = alignmentBlock.getAlignmentTable();

        mainJPanel.removeAll();

        alignmentJTable = new SanchayJTable(alignmentModel, SanchayJTable.ALIGNMENT_MODE, alignmentBlock, true);
        alignmentJTable.addEventListener(this);
        alignmentJTable.prepareCommands();

        JScrollPane tableScrollPane = new JScrollPane(alignmentJTable);
//        tableScrollPane.setPreferredSize(new Dimension(300, 250));
        tableScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));

        mainJPanel.add(tableScrollPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        UtilityFunctions.setComponentFont(alignmentJTable, tgtLangEnc);
//        tableJTable.setFont(new java.awt.Font("Dialog", 1, 14));

        alignmentJTable.setRowHeight(80);
        alignmentJTable.getColumnModel().setColumnMargin(50);
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

//        JTextField ed = new JTextField();
//        ed.setEditable(true);
//        ed.setHorizontalAlignment(JTextField.CENTER);
//        DefaultCellEditor dced = new DefaultCellEditor(ed);
//        alignmentJTable.setDefaultEditor(String.class, dced);

        srcCellEditor = new MultiLineCellEditor(srcLangEnc);
        tgtCellEditor = new MultiLineCellEditor(tgtLangEnc);

        srcCellRenderer = new MultiLineCellRenderer(srcLangEnc, 3, 150);
        srcCellRenderer.setToolTipText("Drag and drop for alignment");

        tgtCellRenderer = new MultiLineCellRenderer(tgtLangEnc, 3, 150);
        tgtCellRenderer.setToolTipText("Drag and drop for alignment");

        if(alignmentModel.getColumnCount() > 1)
        {
            alignmentJTable.getColumnModel().getColumn(0).setCellEditor(srcCellEditor);
            alignmentJTable.getColumnModel().getColumn(2).setCellEditor(srcCellEditor);

            alignmentJTable.getColumnModel().getColumn(0).setCellRenderer(srcCellRenderer);
            alignmentJTable.getColumnModel().getColumn(2).setCellRenderer(srcCellRenderer);

            initStarModel();
        }

//        alignmentJTable.setTableHeader(null);

        SanchayEdges edges = alignmentBlock.getEdges();

        if(edges != null) {
            alignmentJTable.setEdges(edges);
        }

        UtilityFunctions.fitColumnsToContent(alignmentJTable);
        UtilityFunctions.widthToFit(alignmentJTable, SanchayJTable.ALIGNMENT_MODE);

        setVisible(false);
        setVisible(true);
    }

    public void refreshAlignments(boolean recreate)
    {
        Rectangle rect = alignmentJTable.getVisibleRect();

//        int r = alignmentJTable.scSelectedRow();
//        int c = alignmentJTable.getSelectedColumn();

        alignmentBlock.synchronizeIndices(true);
        prepareAlignmentTable();

//        alignmentJTable.setVisible(false);
//        alignmentJTable.setVisible(true);
        alignmentJTable.requestFocusInWindow();
        alignmentJTable.updateUI();
        alignmentJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//        alignmentJTable.changeSelection(r, c, false, false);
        alignmentJTable.scrollRectToVisible(rect);
    }

    private void loadData()
    {
        srcTextPT = new PropertyTokens();
        tgtTextPT = new PropertyTokens();

        srcSSFStory = new SSFStoryImpl();
        tgtSSFStory = new SSFStoryImpl();

        srcFilePath = srcFileJTextField.getText();
        tgtFilePath = tgtFileJTextField.getText();

        File sfile = new File(srcFilePath);
        File tfile = new File(tgtFilePath);

        if(sfile.canWrite() && tfile.canWrite())
        {
            try
            {
                srcSSFStory.readFile(srcFilePath, srcCharset);
                tgtSSFStory.readFile(tgtFilePath, srcCharset);
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex)
            {
                Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        srcSSFStory.loadAlignments(tgtSSFStory, 0);
        tgtSSFStory.loadAlignments(srcSSFStory, 2);

        prepareAlignment();
    }

    protected void saveData()
    {
        srcSSFStory.saveAlignments();
        tgtSSFStory.saveAlignments();

        try
        {
            srcSSFStory.save(srcFilePath, srcCharset);
            tgtSSFStory.save(tgtFilePath, tgtCharset);

            srcTextPT.save(srcFilePath + ".pt.txt", srcCharset);
            tgtTextPT.save(tgtFilePath + ".pt.txt", tgtCharset);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SentenceAlignmentInterfaceJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveState(SentenceAlignmentInterfaceJPanel editorInstance) {

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");

        if(currentDir == null) {
            currentDir = ".";
        }

        File file;

        if(editorInstance.srcFilePath != null) {
            file = new File(editorInstance.srcFilePath);

            if(file.exists()) {
                currentDir = file.getParent();
            }
        }

        stateKVProps.addProperty("CurrentDir", currentDir);
        stateKVProps.addProperty("srcLangEnc", editorInstance.getSrcLangEnc());
        stateKVProps.addProperty("tgtLangEnc", editorInstance.getTgtLangEnc());

        SanchayClientsStateData.save();
    }

    private static void loadState() {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SENTENCE_ALIGNMENT_INTERFACE.toString());

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");

        if(currentDir == null) {
            currentDir = GlobalProperties.getWorkspaceDirectory();
            stateKVProps.addProperty("currentDir", currentDir);
        }

        String srcLangEnc = stateKVProps.getPropertyValue("srcLangEnc");
        String tgtLangEnc = stateKVProps.getPropertyValue("tgtLangEnc");

        if(srcLangEnc == null) {
            srcLangEnc = sanchay.GlobalProperties.getIntlString("eng::utf8");
            stateKVProps.addProperty("srcLangEnc", srcLangEnc);
        }

        if(tgtLangEnc == null) {
            tgtLangEnc = sanchay.GlobalProperties.getIntlString("hin::utf8");
            stateKVProps.addProperty("tgtLangEnc", tgtLangEnc);
        }
    }

    public String getSrcLangEnc()
    {
        return srcLangEnc;
    }

    public String getTgtLangEnc()
    {
        return tgtLangEnc;
    }

    @Override
    public String getLangEnc()
    {
        return srcLangEnc;
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
    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
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

    public JToolBar getJToolBar() {
        return null;
    }
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        saveState(this);
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

    @Override
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

    @Override
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
        JFrame frame = new JFrame("Sanchay Setence Alignment JPanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
    	SentenceAlignmentInterfaceJPanel newContentPane = new SentenceAlignmentInterfaceJPanel(true);
        newContentPane.setOwner(frame);

        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();

        int inset = 35;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset,
		screenSize.width  - inset*2,
		screenSize.height - inset*5);

	frame.setVisible(true);

        newContentPane.requestFocusInWindow();
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator alignJSeparator;
    private javax.swing.JComboBox batchSizeJComboBox;
    private javax.swing.JLabel batchSizeJLabel;
    private javax.swing.JPanel batchSizeJPanel;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JCheckBox checkbox_enabled;
    private javax.swing.JCheckBox checkbox_label;
    private javax.swing.JButton cleanupJButton;
    private javax.swing.JButton clearJButton;
    private javax.swing.JComboBox combo_maxRate;
    private javax.swing.JComboBox combo_starImage;
    private javax.swing.JPanel filesJPanel;
    private javax.swing.JButton firstJButton;
    private javax.swing.JButton lastJButton;
    private javax.swing.JButton loadJButton;
    private javax.swing.JPanel loadJPanel;
    private javax.swing.JPanel mainJPanel;
    private javax.swing.JPanel navigationJPanel;
    private javax.swing.JButton nextJButton;
    private javax.swing.JPanel optionsJPanel;
    private javax.swing.JButton previousJButton;
    private javax.swing.JButton resetJButton;
    private javax.swing.JButton runSenAlignJButton;
    private javax.swing.JButton saveJButton;
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
    private javax.swing.JPanel topJPanel;
    // End of variables declaration//GEN-END:variables

}
