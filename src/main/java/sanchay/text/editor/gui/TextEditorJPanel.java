/*
 * TextEditorJPanel.java
 *
 * Created on February 2, 2006, 11:57 PM
 */

package sanchay.text.editor.gui;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.swing.JTextComponentSpellChecker;

import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputContext;
import java.awt.Frame;
import java.io.*;
import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;

import javax.swing.undo.*;
import sanchay.GlobalProperties;
import sanchay.common.SanchayClientsStateData;
import sanchay.corpus.simple.impl.*;

import sanchay.common.types.*;
import sanchay.corpus.ssf.gui.SyntacticAnnotationTaskSetupJPanel;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.gui.clients.SanchayClient;
import sanchay.gui.common.*;
import sanchay.langenc.LangEncIdentifier;
import sanchay.properties.KeyValueProperties;
import sanchay.table.gui.DisplayEvent;
import sanchay.text.ScriptStats;
import sanchay.text.enc.conv.EncodingConverterUtils;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.text.spell.SanchaySpellDictionaryHashMap;
import sanchay.util.*;
import sanchay.tree.*;
import sanchay.util.file.FileMonitor;
import sanchay.util.file.SanchayBackup;


/**
 *
 * @author  anil
 */
public class TextEditorJPanel extends javax.swing.JPanel implements WindowListener, FileDisplayer, SanchayClient , JPanelDialog {

    protected ClientType clientType = ClientType.SANCHAY_EDITOR;

    protected static String cmlBegFilePath = GlobalProperties.resolveRelativePath("props/xml/cml-beg.txt");
    protected static String cmlEndFilePath = GlobalProperties.resolveRelativePath("props/xml/cml-end.txt");
    
    protected static KeyValueProperties stateKVProps;
    
    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;
    
    protected FileExplorerJPanel fileExplorerJPanel;
    
    protected LangEncIdentifier encLangIdentifier;
    
    protected AbstractDocument doc;

    public void setLangEnc(String langEnc) {
        this.langEnc = langEnc;

        UtilityFunctions.setComponentFont(textJTextArea, langEnc);
    }

    protected String langEnc;
    
    protected String textFile;
    protected String charset;
    
    public static int DEFAULT_MODE = 0;
    public static int MINIMAL_MODE = 1;
    
    protected String title;
    
    protected int currentMode;
    
    protected boolean commands[];
    protected Action actions[];
    
    protected boolean moreCommands[];
    protected Action moreActions[];

    private DisplayFileTask displayFileTask;
    
    protected boolean commandButtonsShown;
    
    //undo helpers
    protected UndoAction undoAction;
    protected RedoAction redoAction;
    protected UndoManager undo = new UndoManager();
    
    protected JDialog findReplaceDialog;
    protected JDialog ssfEditorDialog;
    
    protected boolean moreCommandsShown;
    protected boolean dirty;
    protected boolean toBeValidatedBeforeSave;
    
    protected SanchayBackup sanchayBackup;
    protected long backupPeriod = (long) 10000;
    
    protected MouseListener popupListener = null;

//    private static final String dictionaryFile = GlobalProperties.getHomeDirectory() + "/" + "data/spell-checker-dictionaries/eng/english.0";
    private static final String dictionaryFile = GlobalProperties.getHomeDirectory() + "/" + "data/spell-checker-dictionaries/hin/word-list-std-hin.txt";
//    private static final String englishPhonetic = GlobalProperties.getHomeDirectory() + "/" + "data/spell-checker-dictionaries/eng/phonet.en";
    protected SpellDictionary dictionary;

    protected boolean editable = true;
    // For auto spell check
    JTextComponentSpellChecker sc;

    /** Creates new form TextEditorJPanel */
    public TextEditorJPanel(String lang) {
        this(GlobalProperties.getIntlString("Untitled"), lang, GlobalProperties.getIntlString("UTF-8"));
    }
    
    public TextEditorJPanel(String lang, int appliedCommands[], int appliedMoreCommands[], int mode) {
        this(GlobalProperties.getIntlString("Untitled"), lang, GlobalProperties.getIntlString("UTF-8"));
        
        popupListener = new PopupListener(editorJPopupMenu);
        textJTextArea.addMouseListener(popupListener);
        
        prepareCommands(appliedCommands, appliedMoreCommands, mode);
    }
    
    public TextEditorJPanel(String lang, String cs) {
        this(GlobalProperties.getIntlString("Untitled"), lang, cs);
    }
    
    public TextEditorJPanel(String lang, String cs, int appliedCommands[], int appliedMoreCommands[], int mode) {
        this(GlobalProperties.getIntlString("Untitled"), lang, cs);
        
        popupListener = new PopupListener(editorJPopupMenu);
        textJTextArea.addMouseListener(popupListener);

        if(mode == MINIMAL_MODE) {
            appliedCommands = new int[12];
            appliedCommands[0] = TextEditorAction.SAVE;
            appliedCommands[1] = TextEditorAction.SAVE_AS;
            appliedCommands[2] = TextEditorAction.UNDO;
            appliedCommands[3] = TextEditorAction.REDO;
            appliedCommands[4] = TextEditorAction.CUT;
            appliedCommands[5] = TextEditorAction.COPY;
            appliedCommands[6] = TextEditorAction.PASTE;
            appliedCommands[7] = TextEditorAction.FIND;
            appliedCommands[8] = TextEditorAction.REPLACE;
            appliedCommands[9] = TextEditorAction.GOTO;
            appliedCommands[10] = TextEditorAction.SELECT_FONT;
            appliedCommands[11] = TextEditorAction.SHOW_MORE_COMMANDS;

            appliedMoreCommands = new int[5];

            appliedMoreCommands[0] = TextEditorAction.REVERT;
            appliedMoreCommands[1] = TextEditorAction.SELECT_INPUT_METHOD;
            appliedMoreCommands[2] = TextEditorAction.SHOW_KB_MAP;
            appliedMoreCommands[3] = TextEditorAction.PRINT;
            appliedMoreCommands[4] = TextEditorAction.SHOW_COMMAND_BUTTONS;

            prepareCommands(appliedCommands, appliedMoreCommands, mode);

            showCommandButtons(false);
            mainJSplitPane.getLeftComponent().setVisible(false);
        } else
            prepareCommands(appliedCommands, appliedMoreCommands, mode);

        textJTextArea.setRequestFocusEnabled(true);
    }
    
    public TextEditorJPanel(String path, String lang, String cs) {

        langEnc = lang;
        
        initComponents();

        UtilityFunctions.setComponentFont(textJTextArea, langEnc);

        loadState(this);

        parentComponent = this;
        
        fileExplorerJPanel = new FileExplorerJPanel(this, new File("/"));

        leftJTabbedPane.addTab(GlobalProperties.getIntlString("Folders"), fileExplorerJPanel);
    	mainJSplitPane.setLeftComponent(leftJTabbedPane);

//        mainJSplitPane.setLeftComponent(fileExplorerJPanel);
        fileExplorerJPanel.createTree("/");
//	getContentPane().validate();
        
        moreCommandsShown = false;
        moreCommandsJPanel.setVisible(false);
        
        title = GlobalProperties.getIntlString("Untitled");
        currentMode = DEFAULT_MODE;
        langEnc = lang;
        
        initDocument();
        
        undoAction = new UndoAction();
//        undoJButton.setAction(undoAction);
        
        redoAction = new RedoAction();
//        redoJButton.setAction(redoAction);
        
        InputMap inputMap = textJTextArea.getInputMap();
        
        //Ctrl-z to undo
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        inputMap.put(key, undoAction);
        
        //Ctrl-y to undo
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
        inputMap.put(key, redoAction);
        
        textFile = path;
        charset = cs;

        initSpell();
        
        displayFile(textFile, charset, null);

        textJTextArea.setRequestFocusEnabled(true);
    }
    
    public TextEditorJPanel(String path, String lang, String cs, int appliedCommands[], int appliedMoreCommands[], int mode) {
        this(path, lang, cs);
        
        popupListener = new PopupListener(editorJPopupMenu);
        textJTextArea.addMouseListener(popupListener);
        
        if(mode == MINIMAL_MODE) {
            appliedCommands = new int[12];
            appliedCommands[0] = TextEditorAction.SAVE;
            appliedCommands[1] = TextEditorAction.SAVE_AS;
            appliedCommands[2] = TextEditorAction.UNDO;
            appliedCommands[3] = TextEditorAction.REDO;
            appliedCommands[4] = TextEditorAction.CUT;
            appliedCommands[5] = TextEditorAction.COPY;
            appliedCommands[6] = TextEditorAction.PASTE;
            appliedCommands[7] = TextEditorAction.FIND;
            appliedCommands[8] = TextEditorAction.REPLACE;
            appliedCommands[9] = TextEditorAction.GOTO;
            appliedCommands[10] = TextEditorAction.SELECT_FONT;
            appliedCommands[11] = TextEditorAction.SHOW_MORE_COMMANDS;
            
            appliedMoreCommands = new int[5];
            
            appliedMoreCommands[0] = TextEditorAction.REVERT;
            appliedMoreCommands[1] = TextEditorAction.SELECT_INPUT_METHOD;
            appliedMoreCommands[2] = TextEditorAction.SHOW_KB_MAP;
            appliedMoreCommands[3] = TextEditorAction.PRINT;
            appliedMoreCommands[4] = TextEditorAction.SHOW_COMMAND_BUTTONS;
            
            prepareCommands(appliedCommands, appliedMoreCommands, mode);
            
            showCommandButtons(false);
            mainJSplitPane.getLeftComponent().setVisible(false);
        } else
            prepareCommands(appliedCommands, appliedMoreCommands, mode);

        textJTextArea.setRequestFocusEnabled(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorJPopupMenu = new javax.swing.JPopupMenu();
        moreMenu = new javax.swing.JMenu();
        mainJSplitPane = new javax.swing.JSplitPane();
        leftJTabbedPane = new javax.swing.JTabbedPane();
        textJSplitPane = new javax.swing.JSplitPane();
        textJScrollPane = new javax.swing.JScrollPane();
        textJTextArea = new javax.swing.JTextArea();
        logJScrollPane = new javax.swing.JScrollPane();
        logJTextArea = new javax.swing.JTextArea();
        bottomJPanel = new javax.swing.JPanel();
        statusJPanel = new javax.swing.JPanel();
        lineJLabel = new javax.swing.JLabel();
        commandsJPanel = new javax.swing.JPanel();
        mainCommandsJPanel = new javax.swing.JPanel();
        moreCommandsJPanel = new javax.swing.JPanel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        moreMenu.setText(bundle.getString("More...")); // NOI18N
        editorJPopupMenu.add(moreMenu);

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        mainJSplitPane.setResizeWeight(0.2);
        mainJSplitPane.setOneTouchExpandable(true);

        leftJTabbedPane.setMinimumSize(new java.awt.Dimension(25, 25));
        mainJSplitPane.setLeftComponent(leftJTabbedPane);

        textJSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        textJSplitPane.setResizeWeight(1.0);
        textJSplitPane.setOneTouchExpandable(true);

        textJTextArea.setLineWrap(true);
        textJTextArea.setWrapStyleWord(true);
        textJTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textJTextAreaKeyTyped(evt);
            }
        });
        textJTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                textJTextAreaCaretUpdate(evt);
            }
        });
        textJScrollPane.setViewportView(textJTextArea);

        textJSplitPane.setTopComponent(textJScrollPane);

        logJTextArea.setColumns(20);
        logJTextArea.setEditable(false);
        logJTextArea.setFont(new java.awt.Font("Dialog", 1, 12));
        logJTextArea.setRows(5);
        logJScrollPane.setViewportView(logJTextArea);

        textJSplitPane.setBottomComponent(logJScrollPane);

        mainJSplitPane.setRightComponent(textJSplitPane);

        add(mainJSplitPane, java.awt.BorderLayout.CENTER);

        bottomJPanel.setLayout(new java.awt.BorderLayout());

        statusJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lineJLabel.setText(bundle.getString("Line")); // NOI18N
        statusJPanel.add(lineJLabel);

        bottomJPanel.add(statusJPanel, java.awt.BorderLayout.NORTH);

        commandsJPanel.setLayout(new java.awt.BorderLayout());

        mainCommandsJPanel.setLayout(new java.awt.GridLayout(1, 0));
        commandsJPanel.add(mainCommandsJPanel, java.awt.BorderLayout.NORTH);

        moreCommandsJPanel.setLayout(new java.awt.GridLayout(1, 0));
        commandsJPanel.add(moreCommandsJPanel, java.awt.BorderLayout.CENTER);

        bottomJPanel.add(commandsJPanel, java.awt.BorderLayout.CENTER);

        add(bottomJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    
    private void textJTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textJTextAreaKeyTyped
// TODO add your handling code here:
        if(evt.getModifiers() == 0) {
            logJTextArea.setText("");
            dirty = true;
        }
    }//GEN-LAST:event_textJTextAreaKeyTyped
    
    private void textJTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_textJTextAreaCaretUpdate
// TODO add your handling code here:
        displayEditInfo();
    }//GEN-LAST:event_textJTextAreaCaretUpdate

    private void formFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_formFocusGained
    {//GEN-HEADEREND:event_formFocusGained
        // TODO add your handling code here:
        textJTextArea.setRequestFocusEnabled(true);
        textJTextArea.requestFocusInWindow();
    }//GEN-LAST:event_formFocusGained

    public ClientType getClientType()
    {
        return clientType;
    }

    private void initSpell()
    {
        // INIT DICTIONARY
        File dictFile = null;
        File phonetFile = null;

        dictFile = new File(dictionaryFile);

        if(dictFile.exists())
        {
            try {
    //          dictionary = new SanchaySpellDictionaryHashMap(dictFile, phonetFile, charset);
              dictionary = new SanchaySpellDictionaryHashMap(dictFile, charset);
              //dictionary = new SpellDictionaryDisk(dictFile, phonetFile, true);
              //dictionary = new GenericSpellDictionary(dictFile, phonetFile);
            } catch (Exception ex) {
              ex.printStackTrace();
            }
        }

    // For auto spell check (will need JEditorPane)
//        sc = new JTextComponentSpellChecker(dictionary);
//        sc.startAutoSpellCheck(textJTextArea);
}

    private void prepareCommands(int appliedCommands[], int appliedMoreCommands[], int mode) {
        commands = new boolean[TextEditorAction._BASIC_ACTIONS_];
        actions = new Action[TextEditorAction._BASIC_ACTIONS_];
        
        moreCommands = new boolean[TextEditorAction._MORE_ACTIONS_];
        moreActions = new Action[TextEditorAction._MORE_ACTIONS_];
        
        // Basic action commands
        for(int i = 0; i < commands.length; i++) {
            commands[i] = true;
            actions[i] = TextEditorAction.createAction(this, i);
        }
        
//        Font btnFont = new Font("Dialog", Font.PLAIN, 11);
//        Font btnFont = getFont().deriveFont(Font.PLAIN, 10);
        
        if(appliedCommands != null) {
            for(int i = 0; i < commands.length; i++)
                commands[i] = false;
            
            for(int i = 0; i < appliedCommands.length; i++) {
                int cmd = appliedCommands[i];
                commands[cmd] = true;
                
                JMenuItem mi = new JMenuItem();
                mi.setAction(actions[cmd]);
                editorJPopupMenu.add(mi);
                
                JButton jb = new JButton(actions[cmd]);
                jb.setAction(actions[cmd]);
//                jb.setFont(btnFont);
                mainCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
            }
            
            ((GridLayout) mainCommandsJPanel.getLayout()).setColumns(appliedCommands.length);
            ((GridLayout) mainCommandsJPanel.getLayout()).setHgap(4);
        } else {
            for(int i = 0; i < commands.length; i++) {
                JMenuItem mi = new JMenuItem();
                mi.setAction(actions[i]);
                editorJPopupMenu.add(mi);
                
                JButton jb = new JButton(actions[i]);
                jb.setAction(actions[i]);
                mainCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
            }
            
            ((GridLayout) mainCommandsJPanel.getLayout()).setColumns(commands.length);
            ((GridLayout) mainCommandsJPanel.getLayout()).setHgap(4);
        }
        
        // More action commands
        for(int i = 0; i < moreCommands.length; i++) {
            moreCommands[i] = true;
            moreActions[i] = TextEditorAction.createMoreAction(this, i);
        }
        
        if(appliedMoreCommands != null) {
            for(int i = 0; i < moreCommands.length; i++)
                moreCommands[i] = false;
            
            for(int i = 0; i < appliedMoreCommands.length; i++) {
                int cmd = appliedMoreCommands[i];
                moreCommands[cmd] = true;
                
                JMenuItem mi = new JMenuItem();
                mi.setAction(moreActions[cmd]);
                moreMenu.add(mi);
                
                JButton jb = new JButton(moreActions[cmd]);
                jb.setAction(moreActions[cmd]);
//                jb.setFont(btnFont);
                moreCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
            }
            
            ((GridLayout) moreCommandsJPanel.getLayout()).setColumns(appliedCommands.length);
            ((GridLayout) moreCommandsJPanel.getLayout()).setHgap(4);
        } else {
            for(int i = 0; i < moreCommands.length; i++) {
                JMenuItem mi = new JMenuItem();
                mi.setAction(moreActions[i]);
                moreMenu.add(mi);
                
                JButton jb = new JButton(moreActions[i]);
                jb.setAction(moreActions[i]);
                moreCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
            }
            
            ((GridLayout) moreCommandsJPanel.getLayout()).setColumns(moreCommands.length);
            ((GridLayout) moreCommandsJPanel.getLayout()).setHgap(4);
        }
        
        commandButtonsShown = true;
        
        InputMap inputMap = textJTextArea.getInputMap();
        
        //Shift+Ctrl+p to wrap the current line in a paragraph tag
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[TextEditorAction.MAKE_PARAGRAPH]);
        
        //Shift+Ctrl+h to wrap the current line in a heading tag
        key = KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[TextEditorAction.MAKE_HEADING]);
        
        //Shift+Ctrl+s to wrap the current line in a sentence tag
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[TextEditorAction.MAKE_SENTENCE]);
        
        //Shift+Ctrl+g to wrap the current line in a segment tag
        key = KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[TextEditorAction.MAKE_SEGMENT]);
        
        //Ctrl+s to save
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
        inputMap.put(key, actions[TextEditorAction.SAVE]);
        
        //Ctrl+o to open
        key = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK);
        inputMap.put(key, actions[TextEditorAction.OPEN]);
        
        //Ctrl+w to close
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK);
        inputMap.put(key, actions[TextEditorAction.CLOSE]);
        
        //Ctrl+f to find
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, actions[TextEditorAction.FIND]);
        
        //Ctrl+Shift+f to select font
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, actions[TextEditorAction.SELECT_FONT]);
        
        //Ctrl+r to replace
        key = KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK);
        inputMap.put(key, actions[TextEditorAction.REPLACE]);
        
        //Ctrl++ (actually Ctrl+=) to increase font size
        key = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Event.CTRL_MASK);
        inputMap.put(key, moreActions[TextEditorAction.INCREASE_FONT_SIZE]);
        
        //Ctrl+- to decrease font size
        key = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK);
        inputMap.put(key, moreActions[TextEditorAction.DECREASE_FONT_SIZE]);
        
        //Ctrl+i to select input method
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        inputMap.put(key, moreActions[TextEditorAction.SELECT_INPUT_METHOD]);
        
        //Ctrl+Shift+i to switch input method
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[TextEditorAction.SWITCH_INPUT_METHOD]);
        
        //Ctrl+Shift+L to select language
        key = KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[TextEditorAction.SELECT_LANGUAGE]);
        
        //Ctrl+Shift+E to select encoding
        key = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[TextEditorAction.SELECT_ENCODING]);
    }
    
    public void showCommandButtons(EventObject e) {
        if(commandButtonsShown) {
            showCommandButtons(false);
            commandButtonsShown = false;
        } else {
            showCommandButtons(true);
            commandButtonsShown = true;
        }
    }
    
    public void initDocument() {
        textFile = GlobalProperties.getIntlString("Untitled");
        setTitle(title);
        doc = new PlainDocument();
        textJTextArea.setDocument(doc);
        
        //Start watching for undoable edits
        doc.addUndoableEditListener(new LocalUndoableEditListener());
        doc.addDocumentListener(new LocalDocumentListener());
    }

    public JTabbedPane getLeftJTabbedPane()
    {
        return leftJTabbedPane;
    }

    public void collapseLeftJTabbedPane()
    {
        mainJSplitPane.setDividerLocation(0);
    }

    public void addJPanelToLeftTabbedPanel(String title, JPanel panel)
    {
        leftJTabbedPane.addTab(title, panel);
    }
    
    public UndoAction getUndoAction() {
        return undoAction;
    }
    
    public RedoAction getRedoAction() {
        return redoAction;
    }
    
    public void validateBeforeSave(boolean v) {
        toBeValidatedBeforeSave = v;
    }
    
    public void showCommandButtons(boolean b) {
        commandsJPanel.setVisible(b);
    }
    
    public boolean validateCorpusType(EventObject e) {
        boolean validated = true;
        
        CorpusType corpusType = CorpusType.RAW;
        PlainDocument doc = (PlainDocument) textJTextArea.getDocument();
        Element root = doc.getDefaultRootElement();
        int count = root.getElementCount();
        String lines[] = new String[count];
        
        if(dirty) {
            for (int i = 0; i < count; i++) {
                Element line = root.getElement(i);
                int length = line.getEndOffset() - line.getStartOffset();
                
                try{
                    String text = doc.getText(line.getStartOffset(), length);
                    lines[i] = text;
                } catch(BadLocationException ex){
                    ex.printStackTrace();
                }
            }
//	    String text = textJTextArea.getText();
//	    lines = text.split("[\n]");
            corpusType = SimpleStoryImpl.getCorpusType(lines);
        } else
            corpusType = SimpleStoryImpl.getCorpusType(textFile, charset);
        
        if(corpusType == null)
            logJTextArea.setText(GlobalProperties.getIntlString("Error:_Incorrect_corpus_type."));
        else {
            if(corpusType == CorpusType.RAW)
                logJTextArea.setText(GlobalProperties.getIntlString("Corpus_type_validated:_") + corpusType.toString());
            else if(corpusType == CorpusType.SSF_FORMAT) {
                Vector errorLog = new Vector(0, 5);
                
                if(dirty)
                    validated = SSFStoryImpl.validateSSF(lines, errorLog);
                else
                    validated = SSFStoryImpl.validateSSF(textFile, charset, errorLog);
                
                if(validated == true) {
                    logJTextArea.setText(GlobalProperties.getIntlString("Corpus_type_validated:_") + corpusType.toString());
                } else {
                    logJTextArea.setText("");
                    
                    for(int i = 0; i < errorLog.size(); i++) {
                        logJTextArea.append((String) errorLog.get(i));
                    }
                }
            }
        }
        
        return validated;
    }
    
    public boolean validateCorpusType(File file, EventObject e) {
        boolean validated = true;
        
        CorpusType corpusType = SimpleStoryImpl.getCorpusType(file, charset);
        
        if(corpusType == null)
            logJTextArea.append(GlobalProperties.getIntlString("Error:_Incorrect_corpus_type_for_file:_") + file.getAbsolutePath() + "\n");
        else {
            if(corpusType == CorpusType.RAW)
                logJTextArea.append(GlobalProperties.getIntlString("Corpus_type_of_file_") + file.getAbsolutePath() + GlobalProperties.getIntlString("_validated:_") + corpusType.toString() + "\n");
            else if(corpusType == CorpusType.SSF_FORMAT) {
                Vector errorLog = new Vector(0, 5);
                
                validated = SSFStoryImpl.validateSSF(textFile, charset, errorLog);
                
                if(validated == true) {
                    logJTextArea.append(GlobalProperties.getIntlString("Corpus_type_of_file_") + file.getAbsolutePath() + GlobalProperties.getIntlString("_validated:_") + corpusType.toString() + "\n");
                } else {
                    logJTextArea.append("***************************************\n");
                    
                    logJTextArea.append(GlobalProperties.getIntlString("Corpus_type_of_file_") + file.getAbsolutePath() + GlobalProperties.getIntlString("_NOT_validated:_") + corpusType.toString() + "\n");
                    
                    for(int i = 0; i < errorLog.size(); i++) {
                        logJTextArea.append((String) errorLog.get(i));
                    }
                    
                    logJTextArea.append("***************************************\n");
                }
            }
        }
        
        return validated;
    }
    
    public void revert(EventObject e) {
        if((new File(textFile)).exists()) {
            int retVal = JOptionPane.showConfirmDialog(parentComponent, "Are you sure you want to revert to the previously saved\nversion? Any new changes will be lost.", GlobalProperties.getIntlString("Reverting"), JOptionPane.YES_NO_OPTION);
            
            if(retVal == JOptionPane.NO_OPTION)
                return;
            
            displayFile(textFile, charset, e);
        }
    }
    
    public void preprocess(EventObject e) {
        File f = new File(textFile);
        
        if(f.exists()) {
            CorpusType corpusType = SimpleStoryImpl.getCorpusType(textFile, charset);
            
            if(corpusType != CorpusType.RAW) {
                JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("The_file_is_not_of_raw_corpus_type."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            } else {
                int retVal = JOptionPane.showConfirmDialog(parentComponent, "Are you sure you want to proceed? The preprocessed\nfile will replace the current file.", GlobalProperties.getIntlString("Preprocessing"), JOptionPane.YES_NO_OPTION);
                
                if(retVal == JOptionPane.NO_OPTION)
                    return;
                
                if(dirty) {
                    retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("Do_you_want_to_save_the_currently_open_file?"), GlobalProperties.getIntlString("Preprocessing"), JOptionPane.YES_NO_OPTION);
                    
                    if(retVal == JOptionPane.YES_OPTION)
                        save(e);
                }
                
                try {
                    File tmpFile = new File(textFile + ".tmp");
                    
                    UtilityFunctions.naiivePreprocessing(textFile, charset, tmpFile.getAbsolutePath(), charset, GlobalProperties.getIntlString("hin::utf8"));
                    UtilityFunctions.trimSpaces(tmpFile.getAbsolutePath(), charset, textFile, charset);
                    
                    tmpFile.delete();
                    
                    displayFile(textFile, charset, e);
                    
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public boolean save(EventObject e) {
        int pos = textJTextArea.getCaretPosition();
        
        if(toBeValidatedBeforeSave) {
            if(validateCorpusType(e) == false) {
                JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Unable_to_save._The_file_format_could_not_be_validated."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        if(textFile.equals(GlobalProperties.getIntlString("Untitled")) == false || (new File(textFile)).exists()) {
            PrintStream ps = null;
            
            try {
                sanchayBackup.backup(textFile);
                ps = new PrintStream(textFile, charset);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            print(ps);
            setTitle(textFile);
            
            logJTextArea.setText(textFile + GlobalProperties.getIntlString("_saved."));
            
            dirty = false;
            
            if(currentMode == MINIMAL_MODE && dialog != null) {
                dialog.setVisible(false);
            }
        } else
            saveAs(e);
        
        textJTextArea.setCaretPosition(pos);
        
        return true;
    }
    
    public void saveAs(EventObject e) {
        try {
            int pos = textJTextArea.getCaretPosition();
            
            String path = null;
            
//	    System.out.println("Current path: " + textFile);
            
            if(textFile != null && !textFile.equals("") && !textFile.equals(GlobalProperties.getIntlString("Untitled"))) {
                File tfile = new File(textFile);
                
                if(tfile.exists()) {
                    path = tfile.getParentFile().getAbsolutePath();
                }
                else
                    path = stateKVProps.getPropertyValue(GlobalProperties.getIntlString("CurrentDir"));
            }
            else
                path = stateKVProps.getPropertyValue(GlobalProperties.getIntlString("CurrentDir"));
            
//	    System.out.println("Current directory: " + path);
            
            JFileChooser chooser = null;
            
            if(path != null)
                chooser = new JFileChooser(path);
            else
                chooser = new JFileChooser();
            
            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                textFile = chooser.getSelectedFile().getAbsolutePath();
//		charset = JOptionPane.showInputDialog(parentComponent, "Please enter the charset:", "UTF-8");
                
                if((new File(textFile)).exists())
                {
                    int retVal = JOptionPane.showConfirmDialog(parentComponent, "The file " + textFile + " already exists.\n\nDo you want to overwrite it?", GlobalProperties.getIntlString("Overwrite_File?"), JOptionPane.YES_NO_OPTION);

                    if(retVal == JOptionPane.NO_OPTION)
                        return;
                }
        
                PrintStream ps = new PrintStream(textFile, charset);
                print(ps);
                setTitle(textFile);
                
                if(sanchayBackup != null)
                    FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);
                
                sanchayBackup = new SanchayBackup();
                FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
                logJTextArea.setText(GlobalProperties.getIntlString("File_") + textFile + GlobalProperties.getIntlString("_backed_up."));
                
                dirty = false;
            }
            
            textJTextArea.setCaretPosition(pos);
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_opening_file."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public void open(EventObject e) {
//        Cursor cursor = null;
//        
//        if(owner != null)
//        {
//            cursor = getParent().getCursor();
//            owner.setCursor(Cursor.WAIT_CURSOR);
////            owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        }        
//        
        closeFile(e);
        
        try {
            String path = null;
            
            if(textFile != null) {
                File tfile = new File(textFile);
                
                if(tfile.exists() && tfile.getParentFile() != null)
                    path = tfile.getParent();
                else
                    path = stateKVProps.getPropertyValue(GlobalProperties.getIntlString("CurrentDir"));
            }
            else
                path = stateKVProps.getPropertyValue(GlobalProperties.getIntlString("CurrentDir"));
            
            JFileChooser chooser = null;
            
            if(path != null)
                chooser = new JFileChooser(path);
            else
                chooser = new JFileChooser();
            
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                textFile = chooser.getSelectedFile().getAbsolutePath();
//		charset = JOptionPane.showInputDialog(parentComponent, "Please enter the charset:", "UTF-8");
                
                displayFile(textFile, charset, e);
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_opening_file."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

//        if(owner != null)
//        {
//            setCursor(cursor);
//        }        
    }

    public void displayFileInBackground(File file, String charset, EventObject e) {
        (displayFileTask = new DisplayFileTask(this, file, charset, e)).execute();
    }
    
    public void displayFile(File file, String charset, EventObject e) {
        if(file.isFile() == false || file.exists() == false)
            return;

        Cursor cursor = null;
        
        if(owner != null)
        {
//            cursor = getParent().getCursor();
            cursor = owner.getCursor();
//            owner.setCursor(Cursor.WAIT_CURSOR);
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }        
        
        try{
            //	textJTextArea.setText("");
            
            initDocument();
            
            textFile = file.getAbsolutePath();
            this.charset = charset;
            
            BufferedReader lnReader = null;
            
            try {
                if(!charset.equals(""))
                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), charset));
                else
                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), GlobalProperties.getIntlString("UTF-8")));
                
                sanchayBackup = new SanchayBackup();
                FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
//                logJTextArea.setText("File " + textFile + " backed up.");
                
                String line = "";
                
                Element root = doc.getDefaultRootElement();
                while((line = lnReader.readLine()) != null) {
                    doc.insertString(root.getEndOffset() - 1, line + "\n", null);
                    //textJTextArea.append(line + "\n");
                }
                
//		if(textJTextArea.getText().length() > 0)
                if(doc.getLength() > 0)
                    textJTextArea.setCaretPosition(0);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            dirty = false;
            
            undo.discardAllEdits();
            undoAction.updateUndoState();
            redoAction.updateRedoState();
            
            setTitle(textFile);
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }

        if(owner != null && cursor != null)
        {
            owner.setCursor(cursor);
        }        
        
        saveState(this);
    }
    
    public void displayFile(String path, String charset, EventObject e) {
        if(path == null || path.equals(""))
            return;
        
        displayFile(new File(path), charset, e);
//        displayFileInBackground(new File(path), charset, e);
    }

    public void displayFile(EventObject e)
    {
        if(e instanceof DisplayEvent)
        {
            DisplayEvent de = (DisplayEvent) e;
            displayFile(de.getFilePath(), de.getCharset(), e);
        }
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String t) {
        title = (new File(t)).getName();
        
        String fullTitle = GlobalProperties.getIntlString("Sanchay:_") + ClientType.SANCHAY_EDITOR.toString() + GlobalProperties.getIntlString(":_") + title;
        
        if(dialog != null)
            dialog.setTitle(fullTitle);
        else if(owner != null)
            owner.setTitle(fullTitle);
        
//        if(dialog != null)
//            dialog.setTitle(title + "-" + textFile);
//        else if(owner != null)
//            owner.setTitle(title + "-" + textFile);
    }
    
    public void print(PrintStream ps) {
        PlainDocument doc = (PlainDocument) textJTextArea.getDocument();
        
//        ps.print(textJTextArea.getText());
        try{
            ps.print(doc.getText(0, doc.getLength()));
        } catch(BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean closeFile(EventObject e) {
        if(dirty) {
            int retVal = -1;
            
//            if(dialog != null)
//                retVal = JOptionPane.showConfirmDialog(dialog, "The file " + textFile + " has been modified.\n\nDo you want to save the file?", "Closing File", JOptionPane.YES_NO_OPTION);
//            else
                retVal = JOptionPane.showConfirmDialog(parentComponent, "The file " + textFile + " has been modified.\n\nDo you want to save the file?", GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);
            
            if(retVal == JOptionPane.NO_OPTION) {
                initDocument();
                return false;
            } else {
                save(e);
                initDocument();
                return true;
            }
        } else
            initDocument();
        
        if(sanchayBackup != null)
            FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);
        
        return true;
    }
    
    public void gotoLine(int index) {
        PlainDocument docvalidate = (PlainDocument) textJTextArea.getDocument();
        Element root = doc.getDefaultRootElement();
        
        Element line = root.getElement(index);
        
        textJTextArea.setCaretPosition(line.getStartOffset());
    }
    
    public void textStats(EventObject e) {
//	boolean multiple = true;
//
//	TreePath currentSelection[] = fileExplorerJPanel.getJTree().getSelectionPaths();
//
//	if(currentSelection == null || currentSelection.length <= 1)
//	    multiple = false;
//
//	if(multiple)
//	{
//	    int count = currentSelection.length;
//
//            File files[] = new File[count];
//
//	    for(int i = 0; i < count; i++)
//	    {
//		FileNode currentNode = (FileNode) (currentSelection[i].getLastPathComponent());
//		File selFile = (File) currentNode.getUserObject();
//
//                files[i] = selFile;
//
//                textStats(files);
//	    }
//	}
//	else
//	    textStats();
    }
    
//    public void textStats(File[] files)
//    {
//        SanchayTableModel textStats = null;
//
//        try {
//            textStats = UtilityFunctions.getTextStatsBatch(files, charset);
//        }
//        catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
//        catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        if(dialog != null)
//            DialogFactory.showTableDialog(dialog, "Text Statistics", true, textStats, langEncCode);
//        else
//            DialogFactory.showTableDialog(owner, "Text Statistics", true, textStats, langEncCode);
//    }
//
//    public void textStats(File file)
//    {
//        SanchayTableModel textStats = null;
//
//        try {
//            textStats = UtilityFunctions.getTextStatsBatch(file, charset);
//        }
//        catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
//        catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        if(dialog != null)
//            DialogFactory.showTableDialog(dialog, "Text Statistics", true, textStats, langEncCode);
//        else
//            DialogFactory.showTableDialog(owner, "Text Statistics", true, textStats, langEncCode);
//    }
//
//    public void textStats()
//    {
//        SanchayTableModel textStats = null;
//
//        textStats = UtilityFunctions.getTextStats(textJTextArea.getText());
//
//        if(dialog != null)
//            DialogFactory.showTableDialog(dialog, "Text Statistics", true, textStats, langEncCode);
//        else
//            DialogFactory.showTableDialog(owner, "Text Statistics", true, textStats, langEncCode);
//    }
    
    public void textStatsInFiles(EventObject e) {
        
    }

    public Frame getOwner() {
        return owner;
    }

    public void setOwner(Frame frame) {
        owner = (JFrame) frame;
        owner.addWindowListener(this);
    }

    public void setParentComponent(Component parentComponent)
    {
        this.parentComponent = parentComponent;
    }
    
    public void setDialog(JDialog d) {
        dialog = d;
    }
    
    public String switchLanguage(EventObject e) {
        return switchLanguageStatic(this);
    }
    
    public static String switchLanguageStatic(Component parent) {
        boolean switching = false;
        
        if(parent instanceof TextEditorJPanel)
            switching = true;
        
        if(switching) {
            if(((TextEditorJPanel) parent).dirty) {
                int retVal = JOptionPane.showConfirmDialog(parent, "The current file will be closed. If you haven't saved it,\nthe data may be lost. Do you want to continue?", GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);
                
                if(retVal == JOptionPane.NO_OPTION)
                    return null;
            }
            
            ((TextEditorJPanel) parent).textJTextArea.setText("");
            ((TextEditorJPanel) parent).logJTextArea.setText("");
        }
        
        Vector allLanguages = new Vector(0, 3);
        
        Iterator enm = SanchayLanguages.getAllLanguages().getPropertyKeys();
        
        while(enm.hasNext()) {
            String key = (String) enm.next();
            allLanguages.add(key);
        }
        
        Object langs[] = allLanguages.toArray();
        
        Arrays.sort(langs);
        
        String initLang = GlobalProperties.getIntlString("Hindi");
        
        if(switching)
            initLang = SanchayLanguages.getLanguageName( ((TextEditorJPanel) parent).langEnc );
        
        String selectedLanguage = (String) JOptionPane.showInputDialog(parent,
                GlobalProperties.getIntlString("Select_the_language"), GlobalProperties.getIntlString("Language"), JOptionPane.INFORMATION_MESSAGE, null,
                langs, initLang);
        
        if(switching) {
            String langCode = SanchayLanguages.getLangEncCode(selectedLanguage);
            ((TextEditorJPanel) parent).langEnc = langCode;
            
            UtilityFunctions.setComponentFont(((TextEditorJPanel) parent).textJTextArea, langCode);
            UtilityFunctions.setComponentFont(((TextEditorJPanel) parent).logJTextArea, langCode);
            
            ((TextEditorJPanel) parent).setTitle(((TextEditorJPanel) parent).getTitle());
        }
        
        return selectedLanguage;
    }
    
    public String switchEncoding(EventObject e) {
        return switchEncodingStatic(this, langEnc, e);
    }
    
    protected static String switchEncodingStatic(Component parent, String leCode, EventObject e) {
        boolean switching = false;
        
        if(parent instanceof TextEditorJPanel)
            switching = true;
        
        if(switching) {
            if(((TextEditorJPanel) parent).dirty) {
                int retVal = JOptionPane.showConfirmDialog(parent, "The current file will be closed. If you haven't saved it,\nthe data may be lost. Do you want to continue?", GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);
                
                if(retVal == JOptionPane.NO_OPTION)
                    return null;
            }
            
            ((TextEditorJPanel) parent).textJTextArea.setText("");
            ((TextEditorJPanel) parent).logJTextArea.setText("");
        }
        
        String lCode = SanchayLanguages.getLanguageCodeFromLECode(leCode);
        Vector allEncodings = SanchayLanguages.getEncodings(lCode).getCopyOfTokens();
        
        Object encs[] = allEncodings.toArray();
        
        Object encNames[] = new Object[encs.length];
        
        for (int i = 0; i < encNames.length; i++) {
            encNames[i] = SanchayLanguages.getEncodingName((String) encs[i]);
        }
        
        Arrays.sort(encs);
        
        String initEnc = GlobalProperties.getIntlString("UTF-8");
        
        if(switching)
            initEnc = SanchayLanguages.getEncodingName( ((TextEditorJPanel) parent).langEnc );
        
        String currentLanguage = SanchayLanguages.getLanguageName( ((TextEditorJPanel) parent).langEnc );
        
        String selectedEncoding = (String) JOptionPane.showInputDialog(parent,
                GlobalProperties.getIntlString("Select_the_encoding"), GlobalProperties.getIntlString("Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                encNames, initEnc);
        
        if(switching) {
            leCode = SanchayLanguages.getLangEncCode(currentLanguage, selectedEncoding);
            ((TextEditorJPanel) parent).langEnc = leCode;
            
            UtilityFunctions.setComponentFont(((TextEditorJPanel) parent).textJTextArea, leCode);
            UtilityFunctions.setComponentFont(((TextEditorJPanel) parent).logJTextArea, leCode);
            
            ((TextEditorJPanel) parent).setTitle(((TextEditorJPanel) parent).getTitle());
        }
        
        return selectedEncoding;
    }
    
    public void setMode(int mode) {
//	GridLayout gl = ((GridLayout) mainCommandsJPanel.getLayout());
        
//	if(currentMode == DEFAULT_MODE && mode == MINIMAL_MODE)
//	{
//	    mainCommandsJPanel.remove(openJButton);
//	    mainCommandsJPanel.remove(switchLanguageJButton);
//	    mainCommandsJPanel.remove(replaceJButton);
//
//	    mainCommandsJPanel.add(revertJButton);
//
//	    mainCommandsJPanel.remove(moreJButton);
////	    gl.setRows(6);
//	}
//	else if(currentMode == MINIMAL_MODE && mode == DEFAULT_MODE)
//	{
////	    gl.setRows(10);
//
//	    mainCommandsJPanel.add(openJButton, 0);
//	    mainCommandsJPanel.add(switchLanguageJButton, 1);
//	    mainCommandsJPanel.add(replaceJButton);
//	    mainCommandsJPanel.add(moreJButton);
//	}
        
        currentMode = mode;
    }
    
    private void displayEditInfo() {
        PlainDocument doc = (PlainDocument) textJTextArea.getDocument();
        Element root = doc.getDefaultRootElement();
        
        int caretPos = textJTextArea.getCaretPosition();
        
        int currentLine = root.getElementIndex(caretPos);
        int totalLines = root.getElementCount();
        
        lineJLabel.setText(GlobalProperties.getIntlString("Line:_") + (currentLine + 1) + "/" + totalLines);
    }
    
    //This one listens for edits that can be undone.
    protected class LocalUndoableEditListener
            implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }
    
    //And this one listens for any changes to the document.
    protected class LocalDocumentListener
            implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            dirty = true;
            localDisplayEditInfo(e);
        }
        
        public void removeUpdate(DocumentEvent e) {
            dirty = true;
            localDisplayEditInfo(e);
        }
        
        public void changedUpdate(DocumentEvent e) {
            dirty = true;
            localDisplayEditInfo(e);
        }
        
        private void localDisplayEditInfo(DocumentEvent e) {
            PlainDocument doc = (PlainDocument) textJTextArea.getDocument();
            Element root = doc.getDefaultRootElement();
            
            int caretPos = textJTextArea.getCaretPosition();
            
            int currentLine = root.getElementIndex(caretPos);
            int totalLines = root.getElementCount();
            
//	    Element currentElement = root.getElement(currentLine);
            
            lineJLabel.setText(GlobalProperties.getIntlString("Line:_") + (currentLine + 1) + "/" + totalLines);
            
//            Document document = (Document)e.getDocument();
//            int changeLength = e.getLength();
//            changeLog.append(e.getType().toString() + ": " +
//                changeLength + " character" +
//                ((changeLength == 1) ? ". " : "s. ") +
//                " Text length = " + document.getLength() +
//                "." + newline);
        }
    }
    
    class UndoAction extends AbstractAction {
        public UndoAction() {
            super(GlobalProperties.getIntlString("Undo"));
            setEnabled(false);
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                System.out.println(GlobalProperties.getIntlString("Unable_to_undo:_") + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }
        
        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, GlobalProperties.getIntlString("Undo"));
            }
        }
    }
    
    class RedoAction extends AbstractAction {
        public RedoAction() {
            super(GlobalProperties.getIntlString("Redo"));
            setEnabled(false);
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println(GlobalProperties.getIntlString("Unable_to_redo:_") + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }
        
        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, GlobalProperties.getIntlString("Redo"));
            }
        }
    }
    
    private void showFindDialog(boolean replaceMode, EventObject e) {
        findReplaceDialog = null;
        
        if(dialog != null)
            findReplaceDialog = new JDialog(dialog, GlobalProperties.getIntlString("Find"), false);
        else
            findReplaceDialog = new JDialog(owner, GlobalProperties.getIntlString("Find"), false);
        
        IntegratedResourceAccessorJPanel findReplaceJPanel = new IntegratedResourceAccessorJPanel(replaceMode, false, this);
        
        findReplaceJPanel.setOwner(this.getOwner());
        findReplaceJPanel.setDialog(findReplaceDialog);
        
        findReplaceDialog.add(findReplaceJPanel);
        
        findReplaceDialog.pack();
        
        findReplaceDialog.setBounds(300, 200, findReplaceDialog.getBounds().width, findReplaceDialog.getBounds().height);
        
        findReplaceDialog.setVisible(true);
    }
    
    private void showSSFEditorDialog(EventObject e) {
        save(e);
        
        ssfEditorDialog = null;
        
        if(dialog != null)
            ssfEditorDialog = new JDialog(dialog, GlobalProperties.getIntlString("SSF_Edit_Task"), false);
        else
            ssfEditorDialog = new JDialog(owner, GlobalProperties.getIntlString("SSF_Edit_Task"), false);
        
        SyntacticAnnotationTaskSetupJPanel newContentPane = new SyntacticAnnotationTaskSetupJPanel(textFile, true);
        
        newContentPane.setOwner(this.getOwner());
        newContentPane.setDialog(ssfEditorDialog);
        
        ssfEditorDialog.add(newContentPane);
        
        ssfEditorDialog.pack();
        UtilityFunctions.centre(ssfEditorDialog);
        
        ssfEditorDialog.setVisible(true);
        
        revert(e);
    }
    
    public String getDisplayedFile(EventObject e) {
        return textFile;
    }
    
    public String getCharset(EventObject e) {
        return charset;
    }
    
    public String getLangEnc() {
        return langEnc;
    }
    
    public String getText() {
        return textJTextArea.getText();
    }
    
    public void setText(String text) {
        textJTextArea.setText(text);
        
        undo.discardAllEdits();
        undoAction.updateUndoState();
        redoAction.updateRedoState();
        
        textJTextArea.setCaretPosition(0);
    }
    
    public void cut(EventObject e) {
        textJTextArea.cut();
    }
    
    public void copy(EventObject e) {
        textJTextArea.copy();
    }
    
    public void paste(EventObject e) {
        textJTextArea.paste();
    }
    
    public void find(EventObject e) {
        if(findReplaceDialog == null || findReplaceDialog.isVisible() == false)
            showFindDialog(false, e);
    }
    
    public void replace(EventObject e) {
        if(findReplaceDialog == null || findReplaceDialog.isVisible() == false)
            showFindDialog(true, e);
    }
    
    public void gotoLine(EventObject e) {
        PlainDocument doc = (PlainDocument) textJTextArea.getDocument();
        Element root = doc.getDefaultRootElement();
        
        int index = textJTextArea.getCaretPosition();
        index = root.getElementIndex(index);
        String initLine = "" + index;
        
        int count = root.getElementCount();
        String lines[] = new String[count];
        
        for (int i = 0; i < count; i++)
            lines[i] = "" + (i + 1);
        
        String selectedLine = (String) JOptionPane.showInputDialog(parentComponent,
                GlobalProperties.getIntlString("Go_to_line"), GlobalProperties.getIntlString("Go_To"), JOptionPane.INFORMATION_MESSAGE, null,
                lines, initLine);
        
        if(selectedLine != null) {
            gotoLine(Integer.parseInt(selectedLine) - 1);
            textJTextArea.requestFocusInWindow();
        }
    }
    
    public void selectInputMethod(EventObject e) {
        String im = SanchayLanguages.selectInputMethod(this);
        
        if(owner != null)
            SanchayLanguages.changeInputMethod(owner, im);
        else if(dialog != null)
            SanchayLanguages.changeInputMethod(dialog, im);
    }
    
    public void switchInputMethod(EventObject e) {
        String im = SanchayLanguages.switchtInputMethod(this);
        
        if(im != null)
            SanchayLanguages.changeInputMethod(textJTextArea, im);
    }
    
    public void showKBMap(EventObject e) {
        SanchayLanguages.showKBMap(this);
    }
    
    public void selectFont(EventObject e) {
        Font presentFont = textJTextArea.getFont();
        FontChooser chooser = null;
        
        Frame own = getOwner();
        
        if(own != null && own instanceof JFrame)
        {
            chooser = new FontChooser(own, langEnc, presentFont, textJTextArea.getForeground(), false);
        }
        else if(dialog != null)
        {
            chooser = new FontChooser(dialog, langEnc, presentFont, textJTextArea.getForeground(), false);
        }            
        else
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_opening_font_selector:_null_JFrame."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
        }
            
//	    int xinset = 300;
//	    int yinset = 130;
//	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//	    chooser.setBounds(xinset, yinset,
//		    screenSize.width  - xinset*2,
//		    screenSize.height - yinset*2);
            
        boolean enableFontFamilies = true;

        if(langEnc.equals(GlobalProperties.getIntlString("eng::utf8")) || langEnc.equals(GlobalProperties.getIntlString("eng")))
            enableFontFamilies = true;

        chooser.enableFontFamilies(enableFontFamilies);

        chooser.pack();
        UtilityFunctions.centre(chooser);
        chooser.setVisible(true);

        Font selFont = chooser.getNewFont();
        // If we got a real font choice, then update our go button
        if (selFont != null) {
            Font newFont = selFont;
//		Font newFont = null;
//
//		if(enableFontFamilies)
//		    newFont = new Font(selFont.getFamily(), selFont.getStyle(), selFont.getSize());
//		else
//		    newFont = presentFont.deriveFont(selFont.getStyle(), selFont.getSize());

            textJTextArea.setFont(newFont);
            logJTextArea.setFont(newFont);

            textJTextArea.setForeground(chooser.getNewColor());
        }
    }

    public void spellCheck(EventObject e)
    {
      Thread t = new SpellThread();
      t.start();
    }
    
    public void toCML(EventObject e) {
        String cmlBeg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<document>\n";
        String cmlEnd = "\n</document>\n";
        
        File cmlBegFile = new File(cmlBegFilePath);
        File cmlEndFile = new File(cmlEndFilePath);
        
        if(cmlBegFile.isFile() && cmlEndFile.isFile()) {
            try {
                cmlBeg = UtilityFunctions.getTextFromFile(cmlBegFilePath, charset);
                cmlEnd = UtilityFunctions.getTextFromFile(cmlEndFilePath, charset);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        textJTextArea.insert(cmlBeg, 0);
        
        int lineCount = textJTextArea.getLineCount();
        
        int lastPos = 0;
        
        try {
            lastPos = textJTextArea.getLineEndOffset(lineCount - 1);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        
        textJTextArea.insert(cmlEnd, lastPos);
    }
    
    public void makeSelTagged(EventObject e, String tag) {
        String tagStart = "<" + tag + " id=\"\">";
        String tagEnd = "</" + tag + ">";
        
        String lineSep = System.getProperty(GlobalProperties.getIntlString("line.separator"));
        
        int selStart = textJTextArea.getSelectionStart();
        int selEnd = textJTextArea.getSelectionEnd();
        
        String firstChar = "";
        String lastChar = "";
        
        try {
            firstChar = textJTextArea.getText(selStart, 1);
            lastChar = textJTextArea.getText(selEnd, 1);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        
        if(lastChar.equals("\n") || lastChar.equals(lineSep))
            lineSep = "";
        
//        if(selStart > 0 && (firstChar.equals("\n") || firstChar.equals(lineSep)))
//            textJTextArea.insert(tagStart, selStart);
//        else
        textJTextArea.insert(lineSep + tagStart, selStart);
        
        if(selStart > 0 && (lastChar.equals("\n") || lastChar.equals(lineSep)))
            textJTextArea.insert(tagEnd + lineSep, selEnd + tagStart.length() - 1);
        else {
            if(lastChar.equals(" ")) {
                textJTextArea.replaceRange("", selEnd + tagStart.length() + 1, selEnd + tagStart.length() + 2);
                textJTextArea.insert(tagEnd + lineSep, selEnd + tagStart.length() + 1);
            } else
                textJTextArea.insert(tagEnd + lineSep, selEnd + tagStart.length());
        }
        
        textJTextArea.setSelectionStart(selEnd);
        textJTextArea.setSelectionEnd(selEnd);
        textJTextArea.setCaretPosition(selEnd + tagStart.length() + tagEnd.length() + 1);
    }
    
    public void makeTagged(EventObject e, String tag) {
        String tagStart = "<" + tag + " id=\"\">";
        String tagEnd = "</" + tag + ">";
        
        int line = UtilityFunctions.getCurrentLine(textJTextArea);
        int spos = 0;
        int epos = 0;
        
        try {
            spos = textJTextArea.getLineStartOffset(line);
            epos = textJTextArea.getLineEndOffset(line);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        
        textJTextArea.insert(tagStart, spos);
        textJTextArea.insert(tagEnd, epos + tagStart.length() - 1);
    }
    
    public void newFile(EventObject e) {
        closeFile(e);
    }
    
    public void validate(EventObject e) {
        boolean multiple = true;
        
        TreePath currentSelection[] = fileExplorerJPanel.getJTree().getSelectionPaths();
        
        if(currentSelection == null || currentSelection.length <= 1)
            multiple = false;
        
        if(multiple) {
            logJTextArea.setText("");
            
            int count = currentSelection.length;
            
            for(int i = 0; i < count; i++) {
                FileNode currentNode = (FileNode) (currentSelection[i].getLastPathComponent());
                File selFile = (File) currentNode.getUserObject();
                
                if(selFile != null && selFile.exists() && selFile.isFile())
                    validateCorpusType(selFile, e);
            }
        } else
            validateCorpusType(e);
    }
    
    public void identifyLE(EventObject e) {
        String tpath = GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp-tmp-tmp-1-2-3.xyz.abc";
        
        String cs = GlobalProperties.getIntlString("ISO-8859-1");
        
        if(dirty) {
            PrintStream ps = null;
            
            try {
                
                PlainDocument doc = (PlainDocument) textJTextArea.getDocument();
                
                String text = doc.getText(0, doc.getLength());

                if(UtilityFunctions.isPossiblyUTF8String(text))
                    cs = GlobalProperties.getIntlString("UTF-8");

                ps = new PrintStream(tpath, cs);

                ps.println();
                
                ps.close();
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }  catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        } else
            tpath = textFile;
        
        try {
            File tfile = new File(tpath);
            
            if(encLangIdentifier == null) {
                try {
                    encLangIdentifier = new LangEncIdentifier(GlobalProperties.resolveRelativePath("props/enc-lang-identify-models.txt"),
                        LangEncIdentifier.FREQ_IDENTIFIER, LangEncIdentifier.MUTUAL_CROSS_ENTROPY, 3000, true,
                        false, true, 5, 5, 1.0, 7, 3);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                encLangIdentifier.train();
            }
            
            LinkedHashMap LEs = ((LinkedHashMap) encLangIdentifier.identify(tfile));
            Iterator itr = LEs.keySet().iterator();
            String langEnc = (String) itr.next();
            
            logJTextArea.setText(langEnc);
            
            if(dirty)
                tfile.delete();
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void editResource(EventObject e) {
        showSSFEditorDialog(e);
    }
    
    public void print(EventObject e) {
        DocumentRenderer documentRenderer = new DocumentRenderer();
        documentRenderer.print((PlainDocument) doc);
    }
    
    public void moreCommands(EventObject e) {
        if(moreCommandsShown == true) {
            moreCommandsShown = false;
            moreCommandsJPanel.setVisible(false);
        } else {
            moreCommandsShown = true;
            moreCommandsJPanel.setVisible(true);
        }
    }
    
    public void increaseFontSize(EventObject e) {
        UtilityFunctions.increaseFontSize(textJTextArea);
    }
    
    public void decreaseFontSize(EventObject e) {
        UtilityFunctions.decreaseFontSize(textJTextArea);
    }
    
    public void convertEncoding(EventObject e) {
        String initLangEnc = GlobalProperties.getIntlString("hin::utf8");
        String fromEncoding = (String) JOptionPane.showInputDialog(parentComponent,
                GlobalProperties.getIntlString("Select_the_source_language-encoding"), GlobalProperties.getIntlString("Language_and_Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                EncodingConverterUtils.getFromEncodings(), initLangEnc);
        
        String toEncoding = (String) JOptionPane.showInputDialog(parentComponent,
                GlobalProperties.getIntlString("Select_the_target_language-encoding"), GlobalProperties.getIntlString("Language_and_Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                EncodingConverterUtils.getToEncodings(), initLangEnc);
        
        SanchayEncodingConverter converter = EncodingConverterUtils.createEncodingConverter(fromEncoding, toEncoding);
        
        int retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("Do_you_want_to_save_the_currently_open_file?"), GlobalProperties.getIntlString("Converting_the_Encoding"), JOptionPane.YES_NO_OPTION);
        
        if(retVal == JOptionPane.YES_OPTION) {
            save(e);
            
            String oldTextFile = textFile;
            
            saveAs(e);
            
            try {
                converter.convert(new File(oldTextFile), new File(textFile));
                displayFile(textFile, GlobalProperties.getIntlString("UTF-8"), e);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            String convText = converter.convert(textJTextArea.getText());
            textJTextArea.setText(convText);
        }
    }
    
    public void convertEncodingBatch(EventObject e) {
        String initLangEnc = GlobalProperties.getIntlString("hin::utf8");
        String fromEncoding = (String) JOptionPane.showInputDialog(parentComponent,
                GlobalProperties.getIntlString("Select_the_source_language-encoding"), GlobalProperties.getIntlString("Language_and_Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                EncodingConverterUtils.getFromEncodings(), initLangEnc);
        
        String toEncoding = (String) JOptionPane.showInputDialog(parentComponent,
                GlobalProperties.getIntlString("Select_the_target_language-encoding"), GlobalProperties.getIntlString("Language_and_Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                EncodingConverterUtils.getToEncodings(), initLangEnc);
        
        SanchayEncodingConverter converter = EncodingConverterUtils.createEncodingConverter(fromEncoding, toEncoding);
        
        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Please_select_the_source_file(s)_or_directory_to_be_converted."), GlobalProperties.getIntlString("Source_Files"), JOptionPane.INFORMATION_MESSAGE);
        JFileChooser chooserSrc = new JFileChooser();
        int returnValSrc = chooserSrc.showOpenDialog(this);
        
        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Please_select_the_target_file(s)_or_directory_to_be_converted."), GlobalProperties.getIntlString("Source_Files"), JOptionPane.INFORMATION_MESSAGE);
        JFileChooser chooserTgt = new JFileChooser();
        int returnValTgt = chooserTgt.showOpenDialog(this);
        
        if(returnValSrc == JFileChooser.APPROVE_OPTION && returnValTgt == JFileChooser.APPROVE_OPTION) {
            try {
                converter.convertBatch(chooserSrc.getSelectedFile(), chooserTgt.getSelectedFile());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            JOptionPane.showMessageDialog(parentComponent, "Please check if the conversion has been completed_correctly...", GlobalProperties.getIntlString("Conversion_Complete"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void htmlToText(EventObject e) {
        try
        {
            String text = getText();
            File tmpFileIn = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.in.xyz.abc");
            File tmpFileOut = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.out.xyz.abc");
            UtilityFunctions.saveTextToFile(text, tmpFileIn.getAbsolutePath(), charset);
            UtilityFunctions.convertCharset(charset, charset, tmpFileIn.getAbsolutePath(), tmpFileOut.getAbsolutePath(), true);
            String convText = UtilityFunctions.getTextFromFile(tmpFileOut.getAbsolutePath(), charset);
            tmpFileIn.delete();
            tmpFileOut.delete();
            textJTextArea.setText(convText);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(TextEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(TextEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void scriptStats(EventObject e) {

        JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Please_select_the_file."), GlobalProperties.getIntlString("Source_File"), JOptionPane.INFORMATION_MESSAGE);
        JFileChooser chooser = new JFileChooser();
        int returnValTgt = chooser.showOpenDialog(this);

        if(returnValTgt == JFileChooser.APPROVE_OPTION) {

            closeFile(e);

            ScriptStats scriptStats = new ScriptStats(chooser.getSelectedFile().getAbsolutePath(), langEnc, charset);

            scriptStats.computeStats();
            String statString = scriptStats.printStats(System.out);

            textJTextArea.setText(statString);
        }
    }


    public boolean isDirty() {
        return dirty;
    }

    public AbstractDocument getDocument() {
        return doc;
    }
    
    public void setEditable(boolean edit)
    {
        editable = edit;
        textJTextArea.setEditable(edit);
        logJTextArea.setEditable(edit);
        
        textJTextArea.removeMouseListener(popupListener);
        
        popupListener = null;
        
//        editorJPopupMenu.setVisible(edit);
//        editorJPopupMenu.removeMouseListener(popupListener);
    }
    
    private static void saveState(TextEditorJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SANCHAY_EDITOR.toString());

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");
        
        if(currentDir == null)
            currentDir = ".";
        
        File file = null;

        if(editorInstance.textFile != null)
        {
            file = new File(editorInstance.textFile);

            if(file.exists())
            {
                currentDir = file.getParent();
            }
        }

        stateKVProps.addProperty("CurrentDir", currentDir);

        Font font = editorInstance.textJTextArea.getFont();

        stateKVProps.addProperty("fontSize", "" + font.getSize());

        SanchayClientsStateData.save();
    }
    
    private static void loadState(TextEditorJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SANCHAY_EDITOR.toString());

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");
        
        if(currentDir == null)
        {
            currentDir = ".";
            stateKVProps.addProperty("CurrentDir", currentDir);
        }

        String fontSize = stateKVProps.getPropertyValue("fontSize");

        Font font = editorInstance.textJTextArea.getFont();

        if(fontSize == null)
        {
            fontSize = "" + font.getSize();
            stateKVProps.addProperty("fontSize", fontSize);
        }
        else
        {
            font = font.deriveFont(Float.parseFloat(fontSize));
            editorInstance.textJTextArea.setFont(font);
        }
    }
    
    public void hideLeftAndBottomPanels() {
        mainJSplitPane.getLeftComponent().setVisible(false);
        logJScrollPane.setVisible(false);
    }
    
    public JMenuBar getJMenuBar() {
        return null;
    }
    
    public JToolBar getJToolBar() {
        return null;
    }
    
    public JPopupMenu getJPopupMenu() {
        return null;
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
        JFrame frame = new JFrame(GlobalProperties.getIntlString("Sanchay_Text_Editor"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        InputContext imc = frame.getInputContext();
//        System.out.println("Input method selected: " + imc.selectInputMethod(new Locale("hi", "in", "Inscript")));
        
        String selectedLanguage = TextEditorJPanel.switchLanguageStatic(frame);
        
        //Create and set up the content pane.
        TextEditorJPanel newContentPane = null;
        
        if(selectedLanguage == null || selectedLanguage .equals("") == true)
            newContentPane = new TextEditorJPanel(GlobalProperties.getIntlString("hin::utf8"));
        else {
            newContentPane = new TextEditorJPanel(SanchayLanguages.getLangEncCode(selectedLanguage), null, null, DEFAULT_MODE);
        }
        
        newContentPane.owner = frame;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.addWindowListener(newContentPane);
        
//        newContentPane.showCommandButtons(false);
        
        // Tautology in code: can be useful, but only sometimes
        newContentPane.setTitle(newContentPane.getTitle());
        
        //Display the window.
        frame.pack();
        
        int inset = 5;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*9);
        
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
    
    // Shift to a separate class later
    public void windowOpened(WindowEvent e) {
    }
    
    public void windowClosing(WindowEvent e) {
        saveState(this);
        closeFile(e);
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

  private class SpellThread extends Thread {

    public void run() {
      try {
        JTextComponentSpellChecker sc = new JTextComponentSpellChecker(dictionary);
        sc.spellCheck(textJTextArea);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
    private class DisplayFileTask extends SwingWorker<Void, Void> {

        TextEditorJPanel textEditorJPanel;
        File file;
        String charset;
        EventObject e;

        public DisplayFileTask(TextEditorJPanel textEditorJPanel, File file, String charset, EventObject e)
        {
            this.textEditorJPanel = textEditorJPanel;
            this.file = file;
            this.charset = charset;
            this.e = e;
        }

        @Override
        protected Void doInBackground() {
            textEditorJPanel.displayFile(file, charset, e);
//            while (!isCancelled()) {
//                total++;
//                if (random.nextBoolean()) {
//                    heads++;
//                }
////                publish(new FlipPair(heads, total));
//            }
            return null;
        }

//        @Override
//        protected void process(List<FlipPair> pairs) {
//            FlipPair pair = pairs.get(pairs.size() - 1);
//            headsText.setText(String.format("%d", pair.heads));
//            totalText.setText(String.format("%d", pair.total));
//            devText.setText(String.format("%.10g",
//                    ((double) pair.heads)/((double) pair.total) - 0.5));
//        }
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel bottomJPanel;
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JPopupMenu editorJPopupMenu;
    public javax.swing.JTabbedPane leftJTabbedPane;
    public javax.swing.JLabel lineJLabel;
    public javax.swing.JScrollPane logJScrollPane;
    public javax.swing.JTextArea logJTextArea;
    public javax.swing.JPanel mainCommandsJPanel;
    public javax.swing.JSplitPane mainJSplitPane;
    public javax.swing.JPanel moreCommandsJPanel;
    public javax.swing.JMenu moreMenu;
    public javax.swing.JPanel statusJPanel;
    public javax.swing.JScrollPane textJScrollPane;
    public javax.swing.JSplitPane textJSplitPane;
    public javax.swing.JTextArea textJTextArea;
    // End of variables declaration//GEN-END:variables
    
}
