/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RichTextEditorJPanel.java
 *
 * Created on 7 Apr, 2009, 6:27:20 PM
 */

package sanchay.text.editor.gui;

import sanchay.text.editor.print.SanchayPageableEditorKit;
import sanchay.text.editor.print.SanchayPaginationPrinter;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.swing.JTextComponentSpellChecker;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.im.InputContext;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import sanchay.GlobalProperties;
import sanchay.common.SanchayClientsStateData;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.gui.SyntacticAnnotationTaskSetupJPanel;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.gui.clients.SanchayClient;
import sanchay.gui.common.FileDisplayer;
import sanchay.gui.common.FileExplorerJPanel;
import sanchay.gui.common.FontChooser;
import sanchay.gui.common.IntegratedResourceAccessorJPanel;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayLanguages;
import sanchay.langenc.LangEncIdentifier;
import sanchay.properties.KeyValueProperties;
import sanchay.text.enc.conv.EncodingConverterUtils;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.text.spell.SanchaySpellDictionaryHashMap;
import sanchay.tree.FileNode;
import sanchay.util.UtilityFunctions;
import sanchay.util.file.FileMonitor;
import sanchay.util.file.SanchayBackup;
import sanchay.gui.common.PopupListener;
import sanchay.table.gui.DisplayEvent;
import sanchay.text.SanchayBatchDocument;
import sanchay.util.DocumentRenderer;
import sanchay.common.types.ClientType;

/**
 *
 * @author anil
 */
public class RichTextEditorJPanel extends javax.swing.JPanel implements WindowListener, FileDisplayer, SanchayClient , JPanelDialog {

    protected ClientType clientType = ClientType.SANCHAY_RTF_EDITOR;

    protected static String cmlBegFilePath = GlobalProperties.resolveRelativePath("props/xml/cml-beg.txt");
    protected static String cmlEndFilePath = GlobalProperties.resolveRelativePath("props/xml/cml-end.txt");

    protected static KeyValueProperties stateKVProps;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected FileExplorerJPanel fileExplorerJPanel;

    protected LangEncIdentifier encLangIdentifier;

    protected AbstractDocument doc;
    SimpleAttributeSet styleAtrributes;

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
    protected long backupPeriod = (long) 1000000;

    private static final String dictionaryFile = GlobalProperties.getHomeDirectory() + "/" + "data/spell-checker-dictionaries/eng/english.0";
//    private static final String englishPhonetic = GlobalProperties.getHomeDirectory() + "/" + "data/spell-checker-dictionaries/eng/phonet.en";
    protected SpellDictionary dictionary;

    protected SanchayPageableEditorKit editorKit;
    protected SanchayPaginationPrinter sanchayPaginationPrinter;
    // For auto spell check
    protected JTextComponentSpellChecker sc;

    /** Creates new form RichTextEditorJPanel */
    public RichTextEditorJPanel(String lang) {
        this(GlobalProperties.getIntlString("Untitled"), lang, GlobalProperties.getIntlString("UTF-8"));
    }

    public RichTextEditorJPanel(String lang, int appliedCommands[], int appliedMoreCommands[], int mode) {
        this(GlobalProperties.getIntlString("Untitled"), lang, GlobalProperties.getIntlString("UTF-8"));

        MouseListener popupListener = new PopupListener(editorJPopupMenu);
        editorJTextPane.addMouseListener(popupListener);

        prepareCommands(appliedCommands, appliedMoreCommands, mode);
    }

    public RichTextEditorJPanel(String lang, String cs) {
        this(GlobalProperties.getIntlString("Untitled"), lang, cs);
    }

    public RichTextEditorJPanel(String lang, String cs, int appliedCommands[], int appliedMoreCommands[], int mode) {
        this(GlobalProperties.getIntlString("Untitled"), lang, cs);

        MouseListener popupListener = new PopupListener(editorJPopupMenu);
        editorJTextPane.addMouseListener(popupListener);

        if(mode == MINIMAL_MODE) {
            appliedCommands = new int[12];
            appliedCommands[0] = RichTextEditorAction.SAVE;
            appliedCommands[1] = RichTextEditorAction.SAVE_AS;
            appliedCommands[2] = RichTextEditorAction.UNDO;
            appliedCommands[3] = RichTextEditorAction.REDO;
            appliedCommands[4] = RichTextEditorAction.CUT;
            appliedCommands[5] = RichTextEditorAction.COPY;
            appliedCommands[6] = RichTextEditorAction.PASTE;
            appliedCommands[7] = RichTextEditorAction.FIND;
            appliedCommands[8] = RichTextEditorAction.REPLACE;
            appliedCommands[9] = RichTextEditorAction.GOTO;
            appliedCommands[10] = RichTextEditorAction.SELECT_FONT;
            appliedCommands[11] = RichTextEditorAction.SHOW_MORE_COMMANDS;

            appliedMoreCommands = new int[5];

            appliedMoreCommands[0] = RichTextEditorAction.REVERT;
            appliedMoreCommands[1] = RichTextEditorAction.SELECT_INPUT_METHOD;
            appliedMoreCommands[2] = RichTextEditorAction.SHOW_KB_MAP;
            appliedMoreCommands[3] = RichTextEditorAction.PRINT;
            appliedMoreCommands[4] = RichTextEditorAction.SHOW_COMMAND_BUTTONS;

            prepareCommands(appliedCommands, appliedMoreCommands, mode);

            showCommandButtons(false);
            mainJSplitPane.getLeftComponent().setVisible(false);
        } else
            prepareCommands(appliedCommands, appliedMoreCommands, mode);
        
        editorJTextPane.setRequestFocusEnabled(true);
    }

    public RichTextEditorJPanel(String path, String lang, String cs) {
        loadState(this);

        initComponents();

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
        UtilityFunctions.setComponentFont(editorJTextPane, langEnc);

        initDocument();

        undoAction = new UndoAction();
//        undoJButton.setAction(undoAction);

        redoAction = new RedoAction();
//        redoJButton.setAction(redoAction);

        InputMap inputMap = editorJTextPane.getInputMap();

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

        editorJTextPane.setRequestFocusEnabled(true);
    }

    public RichTextEditorJPanel(String path, String lang, String cs, int appliedCommands[], int appliedMoreCommands[], int mode) {
        this(path, lang, cs);

        MouseListener popupListener = new PopupListener(editorJPopupMenu);
        editorJTextPane.addMouseListener(popupListener);

        if(mode == MINIMAL_MODE) {
            appliedCommands = new int[12];
            appliedCommands[0] = RichTextEditorAction.SAVE;
            appliedCommands[1] = RichTextEditorAction.SAVE_AS;
            appliedCommands[2] = RichTextEditorAction.UNDO;
            appliedCommands[3] = RichTextEditorAction.REDO;
            appliedCommands[4] = RichTextEditorAction.CUT;
            appliedCommands[5] = RichTextEditorAction.COPY;
            appliedCommands[6] = RichTextEditorAction.PASTE;
            appliedCommands[7] = RichTextEditorAction.FIND;
            appliedCommands[8] = RichTextEditorAction.REPLACE;
            appliedCommands[9] = RichTextEditorAction.GOTO;
            appliedCommands[10] = RichTextEditorAction.SELECT_FONT;
            appliedCommands[11] = RichTextEditorAction.SHOW_MORE_COMMANDS;

            appliedMoreCommands = new int[5];

            appliedMoreCommands[0] = RichTextEditorAction.REVERT;
            appliedMoreCommands[1] = RichTextEditorAction.SELECT_INPUT_METHOD;
            appliedMoreCommands[2] = RichTextEditorAction.SHOW_KB_MAP;
            appliedMoreCommands[3] = RichTextEditorAction.PRINT;
            appliedMoreCommands[4] = RichTextEditorAction.SHOW_COMMAND_BUTTONS;

            prepareCommands(appliedCommands, appliedMoreCommands, mode);

            showCommandButtons(false);
            mainJSplitPane.getLeftComponent().setVisible(false);
        } else
            prepareCommands(appliedCommands, appliedMoreCommands, mode);

        editorJTextPane.setRequestFocusEnabled(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorJPopupMenu = new javax.swing.JPopupMenu();
        moreMenu = new javax.swing.JMenu();
        mainJSplitPane = new javax.swing.JSplitPane();
        leftJTabbedPane = new javax.swing.JTabbedPane();
        textJSplitPane = new javax.swing.JSplitPane();
        editorJScrollPane = new javax.swing.JScrollPane();
        editorJTextPane = new javax.swing.JTextPane();
        logJScrollPane = new javax.swing.JScrollPane();
        logJTextPane = new javax.swing.JTextPane();
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

        editorJScrollPane.setViewportView(editorJTextPane);

        textJSplitPane.setTopComponent(editorJScrollPane);

        logJScrollPane.setViewportView(logJTextPane);

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

    private void formFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_formFocusGained
    {//GEN-HEADEREND:event_formFocusGained
        // TODO add your handling code here:
        editorJTextPane.requestFocusInWindow();
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

        try {
//          dictionary = new SanchaySpellDictionaryHashMap(dictFile, phonetFile, charset);
          dictionary = new SanchaySpellDictionaryHashMap(dictFile, charset);
          //dictionary = new SpellDictionaryDisk(dictFile, phonetFile, true);
          //dictionary = new GenericSpellDictionary(dictFile, phonetFile);
        } catch (Exception ex) {
          ex.printStackTrace();
        }

    // For auto spell check (will need JEditorPane)
        sc = new JTextComponentSpellChecker(dictionary);
        sc.startAutoSpellCheck(editorJTextPane);
}

    private void prepareCommands(int appliedCommands[], int appliedMoreCommands[], int mode) {
        commands = new boolean[RichTextEditorAction._BASIC_ACTIONS_];
        actions = new Action[RichTextEditorAction._BASIC_ACTIONS_];

        moreCommands = new boolean[RichTextEditorAction._MORE_ACTIONS_];
        moreActions = new Action[RichTextEditorAction._MORE_ACTIONS_];

        // Basic action commands
        for(int i = 0; i < commands.length; i++) {
            commands[i] = true;
            actions[i] = RichTextEditorAction.createAction(this, i);
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
            moreActions[i] = RichTextEditorAction.createMoreAction(this, i);
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

        Action[] editorActions = editorKit.getActions();

        JMenu menu=null;
        int menuCount = 0;
        for(int i=0;i<editorActions.length;i++){
            if(i%20==0){
                menu = new JMenu(GlobalProperties.getIntlString("Actions_")+menuCount);
                menuCount++;
                moreMenu.add(menu);
            }

            menu.add(editorActions[i]);
        }

        commandButtonsShown = true;

        InputMap inputMap = editorJTextPane.getInputMap();

        //Shift+Ctrl+p to wrap the current line in a paragraph tag
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.MAKE_PARAGRAPH]);

        //Shift+Ctrl+h to wrap the current line in a heading tag
        key = KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.MAKE_HEADING]);

        //Shift+Ctrl+s to wrap the current line in a sentence tag
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.MAKE_SENTENCE]);

        //Shift+Ctrl+g to wrap the current line in a segment tag
        key = KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.MAKE_SEGMENT]);

        //Ctrl+s to save
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
        inputMap.put(key, actions[RichTextEditorAction.SAVE]);

        //Ctrl+o to open
        key = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK);
        inputMap.put(key, actions[RichTextEditorAction.OPEN]);

        //Ctrl+w to close
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK);
        inputMap.put(key, actions[RichTextEditorAction.CLOSE]);

        //Ctrl+f to find
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, actions[RichTextEditorAction.FIND]);

        //Ctrl+Shift+f to select font
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, actions[RichTextEditorAction.SELECT_FONT]);

        //Ctrl+r to replace
        key = KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK);
        inputMap.put(key, actions[RichTextEditorAction.REPLACE]);

        //Ctrl++ (actually Ctrl+=) to increase font size
        key = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Event.CTRL_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.INCREASE_FONT_SIZE]);

        //Ctrl+- to decrease font size
        key = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.DECREASE_FONT_SIZE]);

        //Ctrl+i to select input method
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.SELECT_INPUT_METHOD]);

        //Ctrl+Shift+i to switch input method
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.SWITCH_INPUT_METHOD]);

        //Ctrl+Shift+L to select language
        key = KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.SELECT_LANGUAGE]);

        //Ctrl+Shift+E to select encoding
        key = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, moreActions[RichTextEditorAction.SELECT_ENCODING]);
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
        doc = new SanchayBatchDocument();
        editorJTextPane.setDocument(doc);

        styleAtrributes = new SimpleAttributeSet();

        Font font = SanchayLanguages.getDefaultLangEncFont(langEnc);

        StyleConstants.setForeground(styleAtrributes, Color.BLUE);
        StyleConstants.setFontFamily(styleAtrributes, font.getFamily());
        StyleConstants.setFontSize(styleAtrributes, font.getSize());
        StyleConstants.setBold(styleAtrributes, font.isBold());
        StyleConstants.setItalic(styleAtrributes, font.isItalic());

        //Start watching for undoable edits
        doc.addUndoableEditListener(new LocalUndoableEditListener());
        doc.addDocumentListener(new LocalDocumentListener());

        editorKit = new SanchayPageableEditorKit();
        editorJTextPane.setEditorKit(editorKit);
//        editorKit.setHeader(editorKit.createHeader());
//        editorKit.setFooter(editorKit.createFooter());
        PageFormat pf = new PageFormat();
        pf.setPaper(new Paper());
        sanchayPaginationPrinter = new SanchayPaginationPrinter(pf, editorJTextPane);
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
        SanchayBatchDocument doc = (SanchayBatchDocument) editorJTextPane.getDocument();
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
//	    String text = editorJTextPane.getText();
//	    lines = text.split("[\n]");
            corpusType = SimpleStoryImpl.getCorpusType(lines);
        } else
            corpusType = SimpleStoryImpl.getCorpusType(textFile, charset);

        if(corpusType == null)
            logJTextPane.setText(GlobalProperties.getIntlString("Error:_Incorrect_corpus_type."));
        else {
            if(corpusType == CorpusType.RAW)
                logJTextPane.setText(GlobalProperties.getIntlString("Corpus_type_validated:_") + corpusType.toString());
            else if(corpusType == CorpusType.SSF_FORMAT) {
                Vector errorLog = new Vector(0, 5);

                if(dirty)
                    validated = SSFStoryImpl.validateSSF(lines, errorLog);
                else
                    validated = SSFStoryImpl.validateSSF(textFile, charset, errorLog);

                if(validated == true) {
                    logJTextPane.setText(GlobalProperties.getIntlString("Corpus_type_validated:_") + corpusType.toString());
                } else {
                    logJTextPane.setText("");

                    for(int i = 0; i < errorLog.size(); i++) {
                        appendToJEditorPane(logJTextPane, (String) errorLog.get(i));
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
            appendToJEditorPane(logJTextPane, GlobalProperties.getIntlString("Error:_Incorrect_corpus_type_for_file:_") + file.getAbsolutePath() + "\n");
        else {
            if(corpusType == CorpusType.RAW)
                appendToJEditorPane(logJTextPane, GlobalProperties.getIntlString("Corpus_type_of_file_") + file.getAbsolutePath() + GlobalProperties.getIntlString("_validated:_") + corpusType.toString() + "\n");
            else if(corpusType == CorpusType.SSF_FORMAT) {
                Vector errorLog = new Vector(0, 5);

                validated = SSFStoryImpl.validateSSF(textFile, charset, errorLog);

                if(validated == true) {
                    appendToJEditorPane(logJTextPane, GlobalProperties.getIntlString("Corpus_type_of_file_") + file.getAbsolutePath() + GlobalProperties.getIntlString("_validated:_") + corpusType.toString() + "\n");
                } else {
                    appendToJEditorPane(logJTextPane, "***************************************\n");

                    appendToJEditorPane(logJTextPane, GlobalProperties.getIntlString("Corpus_type_of_file_") + file.getAbsolutePath() + GlobalProperties.getIntlString("_NOT_validated:_") + corpusType.toString() + "\n");

                    for(int i = 0; i < errorLog.size(); i++) {
                        appendToJEditorPane(logJTextPane, (String) errorLog.get(i));
                    }

                    appendToJEditorPane(logJTextPane, "***************************************\n");
                }
            }
        }

        return validated;
    }

    public void revert(EventObject e) {
        if((new File(textFile)).exists()) {
            int retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("Are_you_sure_you_want_to_revert_to_the_previously_saved\nversion?_Any_new_changes_will_be_lost."), GlobalProperties.getIntlString("Reverting"), JOptionPane.YES_NO_OPTION);

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
                int retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("Are_you_sure_you_want_to_proceed?_The_preprocessed\nfile_will_replace_the_current_file."), GlobalProperties.getIntlString("Preprocessing"), JOptionPane.YES_NO_OPTION);

                if(retVal == JOptionPane.NO_OPTION)
                    return;

                if(dirty) {
                    retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("Do_you_want_to_save_the_currently_open_file?"), GlobalProperties.getIntlString("Preprocessing"), JOptionPane.YES_NO_OPTION);

                    if(retVal == JOptionPane.YES_OPTION)
                        save(e);
                }

                try {
                    File tmpFile = new File(textFile + GlobalProperties.getIntlString(".tmp"));

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
        int pos = editorJTextPane.getCaretPosition();

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

            if(textFile.endsWith(".rtf"))
            {
                try {
                    editorKit.write(ps, doc, 0, doc.getLength());
                } catch (IOException ex) {
                    Logger.getLogger(RichTextEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadLocationException ex) {
                    Logger.getLogger(RichTextEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
                print(ps);

            setTitle(textFile);

            logJTextPane.setText(textFile + GlobalProperties.getIntlString("_saved."));

            dirty = false;

            if(currentMode == MINIMAL_MODE && dialog != null) {
                dialog.setVisible(false);
            }
        } else
            saveAs(e);

        editorJTextPane.setCaretPosition(pos);

        return true;
    }

    public void saveAs(EventObject e) {
        try {
            int pos = editorJTextPane.getCaretPosition();

            String path = null;

//	    System.out.println("Current path: " + textFile);

            if(textFile != null && !textFile.equals("") && !textFile.equals(GlobalProperties.getIntlString("Untitled"))) {
                File tfile = new File(textFile);

                if(tfile.exists()) {
                    path = tfile.getParentFile().getAbsolutePath();
                }
                else
                    path = stateKVProps.getPropertyValue("CurrentDir");
            }
            else
                path = stateKVProps.getPropertyValue("CurrentDir");

//	    System.out.println("Current directory: " + path);

            JFileChooser chooser = null;

            if(path != null)
                chooser = new JFileChooser(path);
            else
                chooser = new JFileChooser();

            int returnVal = chooser.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                textFile = chooser.getSelectedFile().getAbsolutePath();

                if((new File(textFile)).exists())
                {
                    int retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("The_file_") + textFile + GlobalProperties.getIntlString("_already_exists.\n\nDo_you_want_to_overwrite_it?"), GlobalProperties.getIntlString("Overwrite_File?"), JOptionPane.YES_NO_OPTION);

                    if(retVal == JOptionPane.NO_OPTION)
                        return;
                }
//		charset = JOptionPane.showInputDialog(parentComponent, "Please enter the charset:", "UTF-8");

                PrintStream ps = new PrintStream(textFile, charset);

                if(textFile.endsWith(".rtf"))
                {
                    try {
                        editorKit.write(ps, doc, 0, doc.getLength());
                    } catch (IOException ex) {
                        Logger.getLogger(RichTextEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(RichTextEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                    print(ps);

                setTitle(textFile);

                if(sanchayBackup != null)
                    FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);

                sanchayBackup = new SanchayBackup();
                FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
                logJTextPane.setText(GlobalProperties.getIntlString("File_") + textFile + GlobalProperties.getIntlString("_backed_up."));

                dirty = false;
            }

            editorJTextPane.setCaretPosition(pos);
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

    public void displayFile(File file, String charset, EventObject e) {
        if(file.isFile() == false || file.exists() == false)
            return;

        Cursor cursor = null;

        if(owner != null)
        {
//            cursor = getParent().getCursor();
            cursor = owner.getCursor();
            owner.setCursor(Cursor.WAIT_CURSOR);
//            owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        try{
            //	editorJTextPane.setText("");

            initDocument();

            textFile = file.getAbsolutePath();
            this.charset = charset;

            BufferedReader lnReader = null;

            if(textFile.endsWith(".rtf") || textFile.endsWith(".RTF"))
            {
                if(!charset.equals(""))
                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), charset));
                else
                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), GlobalProperties.getIntlString("UTF-8")));

                Document blank = new SanchayBatchDocument();
                editorJTextPane.setDocument(blank);

                editorKit.read(lnReader, doc, 0);

                editorJTextPane.setDocument(doc);
            }
            else
            {
                try {
                    if(!charset.equals(""))
                        lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), charset));
                    else
                        lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), GlobalProperties.getIntlString("UTF-8")));

                    sanchayBackup = new SanchayBackup();
                    FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
    //                logJTextPane.setText("File " + textFile + " backed up.");

                    String text = "";
                    String line = "";

                    long start = System.currentTimeMillis();

                    Document blank = new SanchayBatchDocument();
                    editorJTextPane.setDocument(blank);

                    Element root = doc.getDefaultRootElement();
                    while((line = lnReader.readLine()) != null) {
    //                    text += line + "\n";
                        ((SanchayBatchDocument) doc).appendBatchString(line, null);
                        ((SanchayBatchDocument) doc).appendBatchLineFeed(styleAtrributes);

    //                    append(line, color);
    //                    doc.insertString(root.getEndOffset() - 1, line + "\n", null);
                        //editorJTextPane.append(line + "\n");
                    }

                    ((SanchayBatchDocument) doc).processBatchUpdates(0);

                    System.out.println(GlobalProperties.getIntlString("Time_to_update_=_") +
                    (System.currentTimeMillis() - start));
                    System.out.println(GlobalProperties.getIntlString("Text_size_=_") + doc.getLength());

                    editorJTextPane.setDocument(doc);

    //		if(editorJTextPane.getText().length() > 0)
                    if(doc.getLength() > 0)
                        editorJTextPane.setCaretPosition(0);

                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            dirty = false;

            undo.discardAllEdits();
            undoAction.updateUndoState();
            redoAction.updateRedoState();

            setTitle(textFile);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        if(owner != null && cursor != null)
        {
            owner.setCursor(cursor);
        }

        saveState(this);
    }

    public void displayFileInBackground(File file, String charset, EventObject e) {
        (displayFileTask = new DisplayFileTask(this, file, charset, e)).execute();
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

        String fullTitle = GlobalProperties.getIntlString("Sanchay:_") + ClientType.SANCHAY_EDITOR.toString() + ": " + title;

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
        SanchayBatchDocument doc = (SanchayBatchDocument) editorJTextPane.getDocument();

//        ps.print(editorJTextPane.getText());
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
        SanchayBatchDocument docvalidate = (SanchayBatchDocument) editorJTextPane.getDocument();
        Element root = doc.getDefaultRootElement();

        Element line = root.getElement(index);

        editorJTextPane.setCaretPosition(line.getStartOffset());
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
//        textStats = UtilityFunctions.getTextStats(editorJTextPane.getText());
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

        if(parent instanceof RichTextEditorJPanel)
            switching = true;

        if(switching) {
            if(((RichTextEditorJPanel) parent).dirty) {
                int retVal = JOptionPane.showConfirmDialog(parent, GlobalProperties.getIntlString("The_current_file_will_be_closed._If_you_haven't_saved_it,\nthe_data_may_be_lost._Do_you_want_to_continue?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

                if(retVal == JOptionPane.NO_OPTION)
                    return null;
            }

            ((RichTextEditorJPanel) parent).editorJTextPane.setText("");
            ((RichTextEditorJPanel) parent).logJTextPane.setText("");
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
            initLang = SanchayLanguages.getLanguageName( ((RichTextEditorJPanel) parent).langEnc );

        String selectedLanguage = (String) JOptionPane.showInputDialog(parent,
                GlobalProperties.getIntlString("Select_the_language"), GlobalProperties.getIntlString("Language"), JOptionPane.INFORMATION_MESSAGE, null,
                langs, initLang);

        if(switching && selectedLanguage != null && selectedLanguage.equals("") == false) {
            String langCode = SanchayLanguages.getLangEncCode(selectedLanguage);
            ((RichTextEditorJPanel) parent).langEnc = langCode;

            UtilityFunctions.setComponentFont(((RichTextEditorJPanel) parent).editorJTextPane, langCode);
            UtilityFunctions.setComponentFont(((RichTextEditorJPanel) parent).logJTextPane, langCode);

            ((RichTextEditorJPanel) parent).setTitle(((RichTextEditorJPanel) parent).getTitle());
        }

        return selectedLanguage;
    }

    public String switchEncoding(EventObject e) {
        return switchEncodingStatic(this, langEnc, e);
    }

    protected static String switchEncodingStatic(Component parent, String leCode, EventObject e) {
        boolean switching = false;

        if(parent instanceof RichTextEditorJPanel)
            switching = true;

        if(switching) {
            if(((RichTextEditorJPanel) parent).dirty) {
                int retVal = JOptionPane.showConfirmDialog(parent, GlobalProperties.getIntlString("The_current_file_will_be_closed._If_you_haven't_saved_it,\nthe_data_may_be_lost._Do_you_want_to_continue?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

                if(retVal == JOptionPane.NO_OPTION)
                    return null;
            }

            ((RichTextEditorJPanel) parent).editorJTextPane.setText("");
            ((RichTextEditorJPanel) parent).logJTextPane.setText("");
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
            initEnc = SanchayLanguages.getEncodingName( ((RichTextEditorJPanel) parent).langEnc );

        String currentLanguage = SanchayLanguages.getLanguageName( ((RichTextEditorJPanel) parent).langEnc );

        String selectedEncoding = (String) JOptionPane.showInputDialog(parent,
                GlobalProperties.getIntlString("Select_the_encoding"), GlobalProperties.getIntlString("Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                encNames, initEnc);

        if(switching && selectedEncoding != null && selectedEncoding.equals("") == false) {
            leCode = SanchayLanguages.getLangEncCode(currentLanguage, selectedEncoding);
            ((RichTextEditorJPanel) parent).langEnc = leCode;

            UtilityFunctions.setComponentFont(((RichTextEditorJPanel) parent).editorJTextPane, leCode);
            UtilityFunctions.setComponentFont(((RichTextEditorJPanel) parent).logJTextPane, leCode);

            ((RichTextEditorJPanel) parent).setTitle(((RichTextEditorJPanel) parent).getTitle());
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
        SanchayBatchDocument doc = (SanchayBatchDocument) editorJTextPane.getDocument();
        Element root = doc.getDefaultRootElement();

        int caretPos = editorJTextPane.getCaretPosition();

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
            SanchayBatchDocument doc = (SanchayBatchDocument) editorJTextPane.getDocument();
            Element root = doc.getDefaultRootElement();

            int caretPos = editorJTextPane.getCaretPosition();

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

    public JEditorPane getEditorPane()
    {
        return editorJTextPane;
    }

    public String getDisplayedFile(EventObject e) {
        return textFile;
    }

    public String getCharset(EventObject e) {
        return charset;
    }

    public String getLanguage() {
        return langEnc;
    }

    public String getText() {
        return editorJTextPane.getText();
    }

    public void setText(String text) {
        editorJTextPane.setText(text);

        undo.discardAllEdits();
        undoAction.updateUndoState();
        redoAction.updateRedoState();

        editorJTextPane.setCaretPosition(0);
    }

    public void cut(EventObject e) {
        editorJTextPane.cut();
    }

    public void copy(EventObject e) {
        editorJTextPane.copy();
    }

    public void paste(EventObject e) {
        editorJTextPane.paste();
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
        SanchayBatchDocument doc = (SanchayBatchDocument) editorJTextPane.getDocument();
        Element root = doc.getDefaultRootElement();

        int index = editorJTextPane.getCaretPosition();
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
            editorJTextPane.requestFocusInWindow();
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
            SanchayLanguages.changeInputMethod(editorJTextPane, im);
    }

    public void showKBMap(EventObject e) {
        SanchayLanguages.showKBMap(this);
    }

    public Font getSelectedFont(EventObject e) {
        Font presentFont = editorJTextPane.getFont();
        FontChooser chooser = null;

        Frame own = getOwner();

        if(own != null && own instanceof JFrame)
        {
            chooser = new FontChooser(own, langEnc, presentFont, editorJTextPane.getForeground(), false);
        }
        else if(dialog != null)
        {
            chooser = new FontChooser(dialog, langEnc, presentFont, editorJTextPane.getForeground(), false);
        }
        else
        {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_opening_font_selector:_null_JFrame."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
        }

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

            return newFont;
        }

        return presentFont;
    }

    public void selectFont(EventObject e) {
        Font presentFont = editorJTextPane.getFont();
        FontChooser chooser = null;

        Frame own = getOwner();

        if(own != null && own instanceof JFrame)
        {
            chooser = new FontChooser(own, langEnc, presentFont, editorJTextPane.getForeground(), false);
        }
        else if(dialog != null)
        {
            chooser = new FontChooser(dialog, langEnc, presentFont, editorJTextPane.getForeground(), false);
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

            editorJTextPane.setFont(newFont);
            logJTextPane.setFont(newFont);

            editorJTextPane.setForeground(chooser.getNewColor());
        }
    }

    public void spellCheck(EventObject e)
    {
      Thread t = new SpellThread();
      t.start();
    }

    public void toCML(EventObject e) {
//        String cmlBeg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<document>\n";
//        String cmlEnd = "\n</document>\n";
//
//        File cmlBegFile = new File(cmlBegFilePath);
//        File cmlEndFile = new File(cmlEndFilePath);
//
//        if(cmlBegFile.isFile() && cmlEndFile.isFile()) {
//            try {
//                cmlBeg = UtilityFunctions.getTextFromFile(cmlBegFilePath, charset);
//                cmlEnd = UtilityFunctions.getTextFromFile(cmlEndFilePath, charset);
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        editorJTextPane.insert(cmlBeg, 0);
//
//        int lineCount = editorJTextPane.getLineCount();
//
//        int lastPos = 0;
//
//        try {
//            lastPos = editorJTextPane.getLineEndOffset(lineCount - 1);
//        } catch (BadLocationException ex) {
//            ex.printStackTrace();
//        }
//
//        editorJTextPane.insert(cmlEnd, lastPos);
    }

    public void makeSelTagged(EventObject e, String tag) {
//        String tagStart = "<" + tag + " id=\"\">";
//        String tagEnd = "</" + tag + ">";
//
//        String lineSep = System.getProperty("line.separator");
//
//        int selStart = editorJTextPane.getSelectionStart();
//        int selEnd = editorJTextPane.getSelectionEnd();
//
//        String firstChar = "";
//        String lastChar = "";
//
//        try {
//            firstChar = editorJTextPane.getText(selStart, 1);
//            lastChar = editorJTextPane.getText(selEnd, 1);
//        } catch (BadLocationException ex) {
//            ex.printStackTrace();
//        }
//
//        if(lastChar.equals("\n") || lastChar.equals(lineSep))
//            lineSep = "";
//
////        if(selStart > 0 && (firstChar.equals("\n") || firstChar.equals(lineSep)))
////            editorJTextPane.insert(tagStart, selStart);
////        else
//        editorJTextPane.insert(lineSep + tagStart, selStart);
//
//        if(selStart > 0 && (lastChar.equals("\n") || lastChar.equals(lineSep)))
//            editorJTextPane.insert(tagEnd + lineSep, selEnd + tagStart.length() - 1);
//        else {
//            if(lastChar.equals(" ")) {
//                editorJTextPane.replaceRange("", selEnd + tagStart.length() + 1, selEnd + tagStart.length() + 2);
//                editorJTextPane.insert(tagEnd + lineSep, selEnd + tagStart.length() + 1);
//            } else
//                editorJTextPane.insert(tagEnd + lineSep, selEnd + tagStart.length());
//        }
//
//        editorJTextPane.setSelectionStart(selEnd);
//        editorJTextPane.setSelectionEnd(selEnd);
//        editorJTextPane.setCaretPosition(selEnd + tagStart.length() + tagEnd.length() + 1);
    }

    public void makeTagged(EventObject e, String tag) {
//        String tagStart = "<" + tag + " id=\"\">";
//        String tagEnd = "</" + tag + ">";
//
//        int line = UtilityFunctions.getCurrentLine(editorJTextPane);
//        int spos = 0;
//        int epos = 0;
//
//        try {
//            spos = editorJTextPane.getLineStartOffset(line);
//            epos = editorJTextPane.getLineEndOffset(line);
//        } catch (BadLocationException ex) {
//            ex.printStackTrace();
//        }
//
//        editorJTextPane.insert(tagStart, spos);
//        editorJTextPane.insert(tagEnd, epos + tagStart.length() - 1);
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
            logJTextPane.setText("");

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

                SanchayBatchDocument doc = (SanchayBatchDocument) editorJTextPane.getDocument();

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

            logJTextPane.setText(langEnc);

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
//        editorKit.print(editorJTextPane, sanchayPaginationPrinter);
//        SanchayPreviewDialog dlg=new SanchayPreviewDialog(owner, editorJTextPane);
//        dlg.setVisible(true);
//        SanchayEditorPanePrinter pnl=new SanchayEditorPanePrinter(editorJTextPane, new Paper(), new Insets(18,18,18,18));
//        pnl.print();

        DocumentRenderer documentRenderer = new DocumentRenderer();
        documentRenderer.print(editorJTextPane);
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
        UtilityFunctions.increaseFontSize(editorJTextPane);
    }

    public void decreaseFontSize(EventObject e) {
        UtilityFunctions.decreaseFontSize(editorJTextPane);
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
            String convText = converter.convert(editorJTextPane.getText());
            editorJTextPane.setText(convText);
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

            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Please_check_if_the_conversion_has_been_completed_correctly..."), GlobalProperties.getIntlString("Conversion_Complete"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public AbstractDocument getDocument() {
        return doc;
    }

    private static void saveState(RichTextEditorJPanel editorInstance) {
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

        SanchayClientsStateData.save();
    }

    private static void loadState(RichTextEditorJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.SANCHAY_EDITOR.toString());

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");

        if(currentDir == null)
        {
            currentDir = ".";
            stateKVProps.addProperty("CurrentDir", currentDir);
        }
    }

    public void hideLeftAndBottomPanels() {
        mainJSplitPane.getLeftComponent().setVisible(false);
        logJScrollPane.setVisible(false);
    }

    public String getLangEnc()
    {
        return langEnc;
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

    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame(GlobalProperties.getIntlString("Sanchay_Text_Editor"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        InputContext imc = frame.getInputContext();
//        System.out.println("Input method selected: " + imc.selectInputMethod(new Locale("hi", "in", "Inscript")));

        String selectedLanguage = RichTextEditorJPanel.switchLanguageStatic(frame);

        //Create and set up the content pane.
        RichTextEditorJPanel newContentPane = null;

        if(selectedLanguage == null || selectedLanguage .equals("") == true)
            newContentPane = new RichTextEditorJPanel(GlobalProperties.getIntlString("hin::utf8"));
        else {
            newContentPane = new RichTextEditorJPanel(SanchayLanguages.getLangEncCode(selectedLanguage), null, null, DEFAULT_MODE);
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
        sc.spellCheck(editorJTextPane);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

    protected void append(String s, AttributeSet attributes) {
        try {
          doc.insertString(doc.getLength(), s, attributes);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
        
  public static void appendToJEditorPane(JEditorPane editPane, String appendText)
  {
        String origText = editPane.getText();
        editPane.setText(origText + appendText);
  }
  
    private class DisplayFileTask extends SwingWorker<Void, Void> {

        RichTextEditorJPanel textEditorJPanel;
        File file;
        String charset;
        EventObject e;

        public DisplayFileTask(RichTextEditorJPanel textEditorJPanel, File file, String charset, EventObject e)
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
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JPanel commandsJPanel;
    private javax.swing.JPopupMenu editorJPopupMenu;
    private javax.swing.JScrollPane editorJScrollPane;
    private javax.swing.JTextPane editorJTextPane;
    private javax.swing.JTabbedPane leftJTabbedPane;
    private javax.swing.JLabel lineJLabel;
    private javax.swing.JScrollPane logJScrollPane;
    private javax.swing.JTextPane logJTextPane;
    private javax.swing.JPanel mainCommandsJPanel;
    private javax.swing.JSplitPane mainJSplitPane;
    private javax.swing.JPanel moreCommandsJPanel;
    private javax.swing.JMenu moreMenu;
    private javax.swing.JPanel statusJPanel;
    private javax.swing.JSplitPane textJSplitPane;
    // End of variables declaration//GEN-END:variables

}
