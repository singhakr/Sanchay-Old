/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SanchayCharmapJPanel.java
 *
 * Created on 13 Nov, 2009, 7:38:29 PM
 */

package sanchay.gui.common;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.Character.UnicodeBlock;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import sanchay.common.SanchayClientsStateData;
import sanchay.common.types.ClientType;
import sanchay.properties.KeyValueProperties;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class SanchayCharmapJPanel extends javax.swing.JPanel
        implements WindowListener, JPanelDialog, sanchay.gui.clients.SanchayClient {

    protected ClientType clientType = ClientType.SANCHAY_CHARMAP;

    protected static KeyValueProperties stateKVProps;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected String title = "";

    protected String langEnc = "eng::utf8";
    protected String charset = "UTF-8";

    protected DefaultComboBoxModel langauges;
    protected DefaultComboBoxModel encodings;

    protected DefaultComboBoxModel fonts;
    protected DefaultComboBoxModel fontSizes;

    protected DefaultComboBoxModel scripts;
    protected DefaultComboBoxModel unicodeBlocks;

    protected String text = "";

    public static final int BY_SCRIPTS = 0;
    public static final int BY_UNICODE_BLOCKS = 1;
    public static final int BY_FONTS = 2;

    protected int mode = BY_SCRIPTS;

    /** Creates new form SanchayCharmapJPanel */
    public SanchayCharmapJPanel() {
        initComponents();

        init();
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    protected void init()
    {
        langauges = new DefaultComboBoxModel();

        SanchayLanguages.fillAllLanguages(langauges);
        languageJComboBox.setModel(langauges);
        
        encodings = new DefaultComboBoxModel();

        fonts = new DefaultComboBoxModel();

        SanchayLanguages.fillFonts(fonts, langEnc, true);
        fontJComboBox.setModel(fonts);

        fontSizes = new DefaultComboBoxModel();

        UtilityFunctions.fillComboBoxIntegers(fontSizes, 4, 200, 2);
        fontSizeJComboBox.setModel(fontSizes);

        scripts = new DefaultComboBoxModel();
        SanchayLanguages.fillAllScripts(scripts);

        unicodeBlocks = new DefaultComboBoxModel();
        SanchayLanguages.fillAllUnicodeBlocks(unicodeBlocks);

        charBlockJPanel.setVisible(false);
        findNavJPanel.setVisible(false);

        setList(mode);
        loadChars();
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    public void setFontSize(float fsize)
    {
        Font prevFont = getCharFont();

        Font fnt = prevFont.deriveFont(fsize);

        setFont(fnt);
    }

    public void setFontStyle(int fstyle)
    {
        Font prevFont = getCharFont();

        Font fnt = prevFont.deriveFont(fstyle);

        setFont(fnt);
    }

    public Font getCharFont()
    {
        Font fnt = null;

        if(charTableJPanel == null)
        {
            fnt = getFont();
            return fnt;
        }

        int count = charTableJPanel.getComponentCount();

        if(count == 0)
        {
            fnt = getFont();
            return fnt;
        }

        fnt = charTableJPanel.getComponent(0).getFont();

        return fnt;
    }

    public void setFont(Font fnt)
    {
        if(charTableJPanel == null)
        {
            super.setFont(fnt);
            return;
        }

        int count = charTableJPanel.getComponentCount();

        for (int i = 0; i < count; i++)
        {
            SanchayCharDisplayPanel sanchayCharDisplayPanel = (SanchayCharDisplayPanel) charTableJPanel.getComponent(i);

            sanchayCharDisplayPanel.setFont(fnt);
        }

        setFontHelper(fnt);
    }

    protected void setFontHelper(Font fnt)
    {
        if(textJTextField != null)
        {
            Font prevFont = textJTextField.getFont();
            fnt = fnt.deriveFont(prevFont.getStyle(), prevFont.getSize());
            textJTextField.setFont(fnt);
        }

        if(searchJTextField != null)
        {
            Font prevFont = searchJTextField.getFont();
            fnt = fnt.deriveFont(prevFont.getStyle(), prevFont.getSize());
            searchJTextField.setFont(fnt);
        }
    }

    protected void loadChars()
    {
        charTableJPanel.removeAll();

        if(mode == BY_SCRIPTS)
            loadScript();
        else if(mode == BY_UNICODE_BLOCKS)
            loadUnicodeBlock();
        else if(mode == BY_FONTS)
            loadFont();
    }

    protected void loadScript()
    {
        String selFontName = (String) fontJComboBox.getSelectedItem();

        String scriptName = (String) listJList.getSelectedValue();

        if(scriptName == null || scriptName.equals(""))
            return;

        Font selFont = new Font(selFontName, Font.PLAIN, 24);

        Font font = SanchayLanguages.getFontFor(SanchayLanguages.getUnicodeBlockForScript(scriptName));

        if(font != null)
            selFont = font.deriveFont(Font.PLAIN, 24);
        else
            font = SanchayLanguages.getDefaultLangEncFont("hin::utf8");

        if(selFont == null)
            selFont = SanchayLanguages.getDefaultLangEncFont("hin::utf8");

        setFontHelper(font);

        Vector<Character> chars = SanchayLanguages.getSupportedCharacters(scriptName);

        int count = chars.size();

        for (int i = 0; i < count; i++)
        {
            Character ch = chars.get(i);

            SanchayCharDisplayPanel sanchayCharDisplayPanel = new SanchayCharDisplayPanel();

            sanchayCharDisplayPanel.setFont(selFont);

            sanchayCharDisplayPanel.setCharacter(ch.charValue());
            sanchayCharDisplayPanel.setTextComponent(textJTextField);
            sanchayCharDisplayPanel.setCharSummaryComponent(statusJLabel);
            sanchayCharDisplayPanel.setCharDetailsComponent(charDetailsJTextPane);

            charTableJPanel.add(sanchayCharDisplayPanel);
        }

        charTableJPanel.setVisible(false);
        charTableJPanel.setVisible(true);
    }

    protected void loadUnicodeBlock()
    {
        String selFontName = (String) fontJComboBox.getSelectedItem();
        
        Character.UnicodeBlock selBlock = (UnicodeBlock) listJList.getSelectedValue();

        if(selBlock == null)
            return;

        Font selFont = new Font(selFontName, Font.PLAIN, 24);

        Font font = SanchayLanguages.getFontFor(selBlock);

        if(font != null)
            selFont = font.deriveFont(Font.PLAIN, 24);
        else
            font = SanchayLanguages.getDefaultLangEncFont("hin::utf8");

        if(selFont == null)
            selFont = SanchayLanguages.getDefaultLangEncFont("hin::utf8");

        setFontHelper(font);

        Vector<Character> chars = SanchayLanguages.getSupportedCharacters(selBlock);

        int count = chars.size();

        for (int i = 0; i < count; i++)
        {
            Character ch = chars.get(i);

            SanchayCharDisplayPanel sanchayCharDisplayPanel = new SanchayCharDisplayPanel();

            sanchayCharDisplayPanel.setFont(selFont);

            sanchayCharDisplayPanel.setCharacter(ch.charValue());
            sanchayCharDisplayPanel.setTextComponent(textJTextField);
            sanchayCharDisplayPanel.setCharSummaryComponent(statusJLabel);
            sanchayCharDisplayPanel.setCharDetailsComponent(charDetailsJTextPane);

            charTableJPanel.add(sanchayCharDisplayPanel);
        }

        charTableJPanel.setVisible(false);
        charTableJPanel.setVisible(true);
    }

    protected void loadFont()
    {
        String selFontName = (String) listJList.getSelectedValue();

        Font selFont = new Font(selFontName, Font.PLAIN, 24);

        setFontHelper(selFont);

        Vector<Character> chars = SanchayLanguages.getSupportedCharacters(selFont);

        int count = chars.size();

        for (int i = 0; i < count; i++)
        {
            Character ch = chars.get(i);

            SanchayCharDisplayPanel sanchayCharDisplayPanel = new SanchayCharDisplayPanel();

            sanchayCharDisplayPanel.setFont(selFont);

            sanchayCharDisplayPanel.setCharacter(ch.charValue());
            sanchayCharDisplayPanel.setTextComponent(textJTextField);
            sanchayCharDisplayPanel.setCharSummaryComponent(statusJLabel);
            sanchayCharDisplayPanel.setCharDetailsComponent(charDetailsJTextPane);

            charTableJPanel.add(sanchayCharDisplayPanel);
        }

        charTableJPanel.setVisible(false);
        charTableJPanel.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listTypeButtonGroup = new javax.swing.ButtonGroup();
        topJPanel = new javax.swing.JPanel();
        langEncJPanel = new javax.swing.JPanel();
        fontJPanel = new javax.swing.JPanel();
        fontJLabel = new javax.swing.JLabel();
        fontJComboBox = new javax.swing.JComboBox();
        languageJPanel = new javax.swing.JPanel();
        languageJLabel = new javax.swing.JLabel();
        languageJComboBox = new javax.swing.JComboBox();
        encodingJPanel = new javax.swing.JPanel();
        encodingJLabel = new javax.swing.JLabel();
        encodingJComboBox = new javax.swing.JComboBox();
        optionsPanel = new javax.swing.JPanel();
        fontSizeJComboBox = new javax.swing.JComboBox();
        boldJCheckBox = new javax.swing.JCheckBox();
        italicJCheckBox = new javax.swing.JCheckBox();
        zoomInJButton = new javax.swing.JButton();
        zoomOutJButton = new javax.swing.JButton();
        byScriptsJRadioButton = new javax.swing.JRadioButton();
        byUnicodeBlocksJRadioButton = new javax.swing.JRadioButton();
        byFontsJRadioButton = new javax.swing.JRadioButton();
        mainJPanel = new javax.swing.JPanel();
        mainJSplitPane = new javax.swing.JSplitPane();
        listJPanel = new javax.swing.JPanel();
        listJScrollPane = new javax.swing.JScrollPane();
        listJList = new javax.swing.JList();
        charJTabbedPane = new javax.swing.JTabbedPane();
        charTableJScrollPane = new javax.swing.JScrollPane();
        charTableJPanel = new javax.swing.JPanel();
        charDetailsJPanel = new javax.swing.JPanel();
        charDetailsJScrollPane = new javax.swing.JScrollPane();
        charDetailsJTextPane = new javax.swing.JTextPane();
        textJPanel = new javax.swing.JPanel();
        textJLabel = new javax.swing.JLabel();
        textJTextField = new javax.swing.JTextField();
        commandsJPanel = new javax.swing.JPanel();
        clearJButton = new javax.swing.JButton();
        copyJButton = new javax.swing.JButton();
        bottomJPanel = new javax.swing.JPanel();
        charBlockJPanel = new javax.swing.JPanel();
        prevCharJButton = new javax.swing.JButton();
        nextCharJButton = new javax.swing.JButton();
        prevBlockJButton = new javax.swing.JButton();
        nextBlockJButton = new javax.swing.JButton();
        findNavJPanel = new javax.swing.JPanel();
        findPrevJButton = new javax.swing.JButton();
        findNextJButton = new javax.swing.JButton();
        statusJPanel = new javax.swing.JPanel();
        statusJLabel = new javax.swing.JLabel();
        searchJPanel = new javax.swing.JPanel();
        searchJLabel = new javax.swing.JLabel();
        searchJTextField = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        topJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        langEncJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        langEncJPanel.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        fontJPanel.setLayout(new java.awt.BorderLayout());

        fontJLabel.setLabelFor(fontJComboBox);
        fontJLabel.setText("Font: ");
        fontJLabel.setDisplayedMnemonicIndex(0);
        fontJPanel.add(fontJLabel, java.awt.BorderLayout.WEST);

        fontJComboBox.setPreferredSize(new java.awt.Dimension(150, 24));
        fontJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontJComboBoxActionPerformed(evt);
            }
        });
        fontJPanel.add(fontJComboBox, java.awt.BorderLayout.CENTER);

        langEncJPanel.add(fontJPanel);

        languageJPanel.setLayout(new java.awt.BorderLayout());

        languageJLabel.setLabelFor(languageJComboBox);
        languageJLabel.setText("Language: ");
        languageJLabel.setDisplayedMnemonicIndex(0);
        languageJLabel.setPreferredSize(new java.awt.Dimension(70, 15));
        languageJPanel.add(languageJLabel, java.awt.BorderLayout.WEST);

        languageJComboBox.setEnabled(false);
        languageJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageJComboBoxActionPerformed(evt);
            }
        });
        languageJPanel.add(languageJComboBox, java.awt.BorderLayout.CENTER);

        langEncJPanel.add(languageJPanel);

        encodingJPanel.setLayout(new java.awt.BorderLayout());

        encodingJLabel.setLabelFor(encodingJComboBox);
        encodingJLabel.setText("Encoding:  ");
        encodingJLabel.setDisplayedMnemonicIndex(0);
        encodingJLabel.setPreferredSize(new java.awt.Dimension(70, 15));
        encodingJPanel.add(encodingJLabel, java.awt.BorderLayout.WEST);

        encodingJComboBox.setEnabled(false);
        encodingJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encodingJComboBoxActionPerformed(evt);
            }
        });
        encodingJPanel.add(encodingJComboBox, java.awt.BorderLayout.CENTER);

        langEncJPanel.add(encodingJPanel);

        topJPanel.add(langEncJPanel);

        optionsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        optionsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        fontSizeJComboBox.setPreferredSize(new java.awt.Dimension(60, 24));
        fontSizeJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontSizeJComboBoxActionPerformed(evt);
            }
        });
        optionsPanel.add(fontSizeJComboBox);

        boldJCheckBox.setText("Bold");
        boldJCheckBox.setDisplayedMnemonicIndex(0);
        boldJCheckBox.setFocusable(false);
        boldJCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        boldJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boldJCheckBoxActionPerformed(evt);
            }
        });
        optionsPanel.add(boldJCheckBox);

        italicJCheckBox.setText("Italic");
        italicJCheckBox.setDisplayedMnemonicIndex(0);
        italicJCheckBox.setFocusable(false);
        italicJCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        italicJCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        italicJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                italicJCheckBoxActionPerformed(evt);
            }
        });
        optionsPanel.add(italicJCheckBox);

        zoomInJButton.setText("+");
        zoomInJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInJButtonActionPerformed(evt);
            }
        });
        optionsPanel.add(zoomInJButton);

        zoomOutJButton.setText("-");
        zoomOutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutJButtonActionPerformed(evt);
            }
        });
        optionsPanel.add(zoomOutJButton);

        listTypeButtonGroup.add(byScriptsJRadioButton);
        byScriptsJRadioButton.setSelected(true);
        byScriptsJRadioButton.setText("Scripts");
        byScriptsJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                byScriptsJRadioButtonActionPerformed(evt);
            }
        });
        optionsPanel.add(byScriptsJRadioButton);

        listTypeButtonGroup.add(byUnicodeBlocksJRadioButton);
        byUnicodeBlocksJRadioButton.setText("Unicode Blocks");
        byUnicodeBlocksJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                byUnicodeBlocksJRadioButtonActionPerformed(evt);
            }
        });
        optionsPanel.add(byUnicodeBlocksJRadioButton);

        listTypeButtonGroup.add(byFontsJRadioButton);
        byFontsJRadioButton.setText("Fonts");
        byFontsJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                byFontsJRadioButtonActionPerformed(evt);
            }
        });
        optionsPanel.add(byFontsJRadioButton);

        topJPanel.add(optionsPanel);

        add(topJPanel, java.awt.BorderLayout.NORTH);

        mainJPanel.setLayout(new java.awt.BorderLayout());

        mainJSplitPane.setOneTouchExpandable(true);

        listJPanel.setLayout(new java.awt.BorderLayout());

        listJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listJListValueChanged(evt);
            }
        });
        listJScrollPane.setViewportView(listJList);

        listJPanel.add(listJScrollPane, java.awt.BorderLayout.CENTER);

        mainJSplitPane.setLeftComponent(listJPanel);

        charTableJPanel.setLayout(new java.awt.GridLayout(0, 10, 4, 4));
        charTableJScrollPane.setViewportView(charTableJPanel);

        charJTabbedPane.addTab("Characters Table", charTableJScrollPane);

        charDetailsJPanel.setLayout(new java.awt.BorderLayout());

        charDetailsJTextPane.setEditable(false);
        charDetailsJScrollPane.setViewportView(charDetailsJTextPane);

        charDetailsJPanel.add(charDetailsJScrollPane, java.awt.BorderLayout.CENTER);

        charJTabbedPane.addTab("Character Details", charDetailsJPanel);

        mainJSplitPane.setRightComponent(charJTabbedPane);

        mainJPanel.add(mainJSplitPane, java.awt.BorderLayout.CENTER);

        textJPanel.setLayout(new java.awt.BorderLayout(3, 0));

        textJLabel.setText("Text to copy: ");
        textJPanel.add(textJLabel, java.awt.BorderLayout.WEST);
        textJPanel.add(textJTextField, java.awt.BorderLayout.CENTER);

        commandsJPanel.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        clearJButton.setText("Clear");
        clearJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(clearJButton);

        copyJButton.setText("Copy");
        copyJButton.setDisplayedMnemonicIndex(0);
        copyJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(copyJButton);

        textJPanel.add(commandsJPanel, java.awt.BorderLayout.EAST);

        mainJPanel.add(textJPanel, java.awt.BorderLayout.SOUTH);

        add(mainJPanel, java.awt.BorderLayout.CENTER);

        bottomJPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        charBlockJPanel.setLayout(new java.awt.GridLayout(1, 0));

        prevCharJButton.setText("Previous Char");
        charBlockJPanel.add(prevCharJButton);

        nextCharJButton.setText("Next Char");
        charBlockJPanel.add(nextCharJButton);

        prevBlockJButton.setText("Previous Block");
        charBlockJPanel.add(prevBlockJButton);

        nextBlockJButton.setText("Next Block");
        charBlockJPanel.add(nextBlockJButton);

        bottomJPanel.add(charBlockJPanel);

        findNavJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        findPrevJButton.setText("Find Previous");
        findNavJPanel.add(findPrevJButton);

        findNextJButton.setText("Find Next");
        findNavJPanel.add(findNextJButton);

        bottomJPanel.add(findNavJPanel);

        statusJPanel.setLayout(new java.awt.BorderLayout());

        statusJLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusJPanel.add(statusJLabel, java.awt.BorderLayout.CENTER);

        searchJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        searchJPanel.setLayout(new java.awt.BorderLayout());

        searchJLabel.setText("Search: ");
        searchJPanel.add(searchJLabel, java.awt.BorderLayout.WEST);

        searchJTextField.setEditable(false);
        searchJTextField.setPreferredSize(new java.awt.Dimension(100, 19));
        searchJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchJTextFieldActionPerformed(evt);
            }
        });
        searchJPanel.add(searchJTextField, java.awt.BorderLayout.CENTER);

        statusJPanel.add(searchJPanel, java.awt.BorderLayout.EAST);

        bottomJPanel.add(statusJPanel);

        add(bottomJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void languageJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_languageJComboBoxActionPerformed
    {//GEN-HEADEREND:event_languageJComboBoxActionPerformed
        // TODO add your handling code here:
//        if(langEncFilled == false)
//            return;
//
//        srcLangauge = (String) srcLanguageJComboBox.getSelectedItem();
//        SanchayLanguages.fillEncodings(srcEncodings, SanchayLanguages.getLanguageCode(srcLangauge));
//        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SENTENCE_ALIGNMENT_INTERFACE.toString());
//
//        if(srcLangauge != null)
//        {
//            srcLangEnc = SanchayLanguages.getLangEncCode(srcLangauge, srcEncoding);
//            stateKVProps.addProperty("srcLangEnc", srcLangEnc);
//
//            UtilityFunctions.setComponentFont(srcTextJTextPane, srcLangEnc);
//        }
}//GEN-LAST:event_languageJComboBoxActionPerformed

    private void encodingJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_encodingJComboBoxActionPerformed
    {//GEN-HEADEREND:event_encodingJComboBoxActionPerformed
        // TODO add your handling code here:
//        if(langEncFilled == false)
//            return;
//
//        srcEncoding = (String) srcEncodingJComboBox.getSelectedItem();
//        KeyValueProperties stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SENTENCE_ALIGNMENT_INTERFACE.toString());
//
//        if(srcEncoding != null)
//        {
//            srcLangEnc = SanchayLanguages.getLangEncCode(srcLangauge, srcEncoding);
//            stateKVProps.addProperty("srcEangEnc", srcLangEnc);
//
//            UtilityFunctions.setComponentFont(srcTextJTextPane, srcLangEnc);
//        }
}//GEN-LAST:event_encodingJComboBoxActionPerformed

    private void fontJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fontJComboBoxActionPerformed
    {//GEN-HEADEREND:event_fontJComboBoxActionPerformed
        // TODO add your handling code here:
        String selFontName = (String) fontJComboBox.getSelectedItem();

        Font selFont = new Font(selFontName, Font.PLAIN, 24);

        setFont(selFont);
}//GEN-LAST:event_fontJComboBoxActionPerformed

    private void searchJTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_searchJTextFieldActionPerformed
    {//GEN-HEADEREND:event_searchJTextFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_searchJTextFieldActionPerformed

    private void byScriptsJRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_byScriptsJRadioButtonActionPerformed
    {//GEN-HEADEREND:event_byScriptsJRadioButtonActionPerformed
        // TODO add your handling code here:
        mode = BY_SCRIPTS;
        setList(mode);
    }//GEN-LAST:event_byScriptsJRadioButtonActionPerformed

    private void byUnicodeBlocksJRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_byUnicodeBlocksJRadioButtonActionPerformed
    {//GEN-HEADEREND:event_byUnicodeBlocksJRadioButtonActionPerformed
        // TODO add your handling code here:
        mode = BY_UNICODE_BLOCKS;
        setList(mode);
    }//GEN-LAST:event_byUnicodeBlocksJRadioButtonActionPerformed

    private void byFontsJRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_byFontsJRadioButtonActionPerformed
    {//GEN-HEADEREND:event_byFontsJRadioButtonActionPerformed
        // TODO add your handling code here:
        mode = BY_FONTS;
        setList(mode);
    }//GEN-LAST:event_byFontsJRadioButtonActionPerformed

    private void listJListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_listJListValueChanged
    {//GEN-HEADEREND:event_listJListValueChanged
        // TODO add your handling code here:
        loadChars();
    }//GEN-LAST:event_listJListValueChanged

    private void clearJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clearJButtonActionPerformed
    {//GEN-HEADEREND:event_clearJButtonActionPerformed
        // TODO add your handling code here:
        text = "";
        textJTextField.setText(text);
    }//GEN-LAST:event_clearJButtonActionPerformed

    private void copyJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyJButtonActionPerformed
    {//GEN-HEADEREND:event_copyJButtonActionPerformed
        // TODO add your handling code here:
        text = textJTextField.getText();
        SanchayStringDataTransfer stringTransfer = new SanchayStringDataTransfer();
        stringTransfer.setClipboardContents(text);
    }//GEN-LAST:event_copyJButtonActionPerformed

    private void fontSizeJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fontSizeJComboBoxActionPerformed
    {//GEN-HEADEREND:event_fontSizeJComboBoxActionPerformed
        // TODO add your handling code here:
        Integer sizeInt = (Integer) fontSizeJComboBox.getSelectedItem();

        setFontSize((float) sizeInt.intValue());
    }//GEN-LAST:event_fontSizeJComboBoxActionPerformed

    private void boldJCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_boldJCheckBoxActionPerformed
    {//GEN-HEADEREND:event_boldJCheckBoxActionPerformed
        // TODO add your handling code here:
        if(boldJCheckBox.isSelected() && italicJCheckBox.isSelected())
            setFontStyle(Font.BOLD | Font.ITALIC);
        else if(boldJCheckBox.isSelected())
            setFontStyle(Font.BOLD);
        else if(italicJCheckBox.isSelected())
            setFontStyle(Font.ITALIC);
        else
            setFontStyle(Font.PLAIN);
    }//GEN-LAST:event_boldJCheckBoxActionPerformed

    private void italicJCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_italicJCheckBoxActionPerformed
    {//GEN-HEADEREND:event_italicJCheckBoxActionPerformed
        // TODO add your handling code here:
        if(boldJCheckBox.isSelected() && italicJCheckBox.isSelected())
            setFontStyle(Font.BOLD | Font.ITALIC);
        else if(boldJCheckBox.isSelected())
            setFontStyle(Font.BOLD);
        else if(italicJCheckBox.isSelected())
            setFontStyle(Font.ITALIC);
        else
            setFontStyle(Font.PLAIN);
    }//GEN-LAST:event_italicJCheckBoxActionPerformed

    private void zoomInJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoomInJButtonActionPerformed
    {//GEN-HEADEREND:event_zoomInJButtonActionPerformed
        // TODO add your handling code here:
        Font prevFont = getCharFont();

        Font fnt = prevFont.deriveFont(prevFont.getStyle(), prevFont.getSize() + 2);

        setFont(fnt);
    }//GEN-LAST:event_zoomInJButtonActionPerformed

    private void zoomOutJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoomOutJButtonActionPerformed
    {//GEN-HEADEREND:event_zoomOutJButtonActionPerformed
        // TODO add your handling code here:
        Font prevFont = getCharFont();

        Font fnt = prevFont.deriveFont(prevFont.getStyle(), prevFont.getSize() - 2);

        setFont(fnt);
    }//GEN-LAST:event_zoomOutJButtonActionPerformed

    protected void setList(int m)
    {
        if(m == BY_SCRIPTS)
        {
            listJList.setModel(scripts);
            fontJComboBox.setEnabled(true);
        }
        else if(m == BY_UNICODE_BLOCKS)
        {
            listJList.setModel(unicodeBlocks);
            fontJComboBox.setEnabled(true);
        }
        else if(m == BY_FONTS)
        {
            listJList.setModel(fonts);
            fontJComboBox.setEnabled(false);
        }
    }

    private static void saveState(SanchayCharmapJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SANCHAY_CHARMAP.toString());

        stateKVProps.addProperty("langEnc", editorInstance.getLangEnc());

        SanchayClientsStateData.save();
    }

    private static void loadState(SanchayCharmapJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SANCHAY_CHARMAP.toString());

        String langEnc = stateKVProps.getPropertyValue("langEnc");

        if(langEnc == null) {
            langEnc = sanchay.GlobalProperties.getIntlString("eng::utf8");
            stateKVProps.addProperty("langEnc", langEnc);
        }
    }

    public String getLangEnc()
    {
        return langEnc;
    }

    public Frame getOwner() {
        return owner;
    }

    public void setOwner(Frame frame) {
        owner = (JFrame) frame;
    }

    public void setParentComponent(Component parentComponent)
    {
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
    	SanchayCharmapJPanel newContentPane = new SanchayCharmapJPanel();
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
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox boldJCheckBox;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JRadioButton byFontsJRadioButton;
    private javax.swing.JRadioButton byScriptsJRadioButton;
    private javax.swing.JRadioButton byUnicodeBlocksJRadioButton;
    private javax.swing.JPanel charBlockJPanel;
    private javax.swing.JPanel charDetailsJPanel;
    private javax.swing.JScrollPane charDetailsJScrollPane;
    private javax.swing.JTextPane charDetailsJTextPane;
    private javax.swing.JTabbedPane charJTabbedPane;
    private javax.swing.JPanel charTableJPanel;
    private javax.swing.JScrollPane charTableJScrollPane;
    private javax.swing.JButton clearJButton;
    private javax.swing.JPanel commandsJPanel;
    private javax.swing.JButton copyJButton;
    private javax.swing.JComboBox encodingJComboBox;
    private javax.swing.JLabel encodingJLabel;
    private javax.swing.JPanel encodingJPanel;
    private javax.swing.JPanel findNavJPanel;
    private javax.swing.JButton findNextJButton;
    private javax.swing.JButton findPrevJButton;
    private javax.swing.JComboBox fontJComboBox;
    private javax.swing.JLabel fontJLabel;
    private javax.swing.JPanel fontJPanel;
    private javax.swing.JComboBox fontSizeJComboBox;
    private javax.swing.JCheckBox italicJCheckBox;
    private javax.swing.JPanel langEncJPanel;
    private javax.swing.JComboBox languageJComboBox;
    private javax.swing.JLabel languageJLabel;
    private javax.swing.JPanel languageJPanel;
    private javax.swing.JList listJList;
    private javax.swing.JPanel listJPanel;
    private javax.swing.JScrollPane listJScrollPane;
    private javax.swing.ButtonGroup listTypeButtonGroup;
    private javax.swing.JPanel mainJPanel;
    private javax.swing.JSplitPane mainJSplitPane;
    private javax.swing.JButton nextBlockJButton;
    private javax.swing.JButton nextCharJButton;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JButton prevBlockJButton;
    private javax.swing.JButton prevCharJButton;
    private javax.swing.JLabel searchJLabel;
    private javax.swing.JPanel searchJPanel;
    private javax.swing.JTextField searchJTextField;
    private javax.swing.JLabel statusJLabel;
    private javax.swing.JPanel statusJPanel;
    private javax.swing.JLabel textJLabel;
    private javax.swing.JPanel textJPanel;
    private javax.swing.JTextField textJTextField;
    private javax.swing.JPanel topJPanel;
    private javax.swing.JButton zoomInJButton;
    private javax.swing.JButton zoomOutJButton;
    // End of variables declaration//GEN-END:variables

}
