/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FramesetJPanel.java
 *
 * Created on 13 Sep, 2009, 3:33:33 PM
 */

package sanchay.propbank.gui;

import java.awt.Event;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.im.InputContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.common.SanchayClientsStateData;
import sanchay.common.types.ClientType;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.gui.clients.SanchayClient;
import sanchay.gui.common.FileDisplayer;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.PopupListener;
import sanchay.gui.common.SanchayLanguages;
import sanchay.propbank.Frameset;
import sanchay.propbank.FramesetPredicate;
import sanchay.properties.KeyValueProperties;
import sanchay.table.gui.DisplayEvent;
import sanchay.util.UtilityFunctions;
import sanchay.util.file.FileMonitor;
import sanchay.util.file.SanchayBackup;
import sanchay.xml.XMLUtils;
import sanchay.common.types.ClientType;

/**
 *
 * @author anil
 */
public class FramesetJPanel extends javax.swing.JPanel implements WindowListener, FileDisplayer, SanchayClient , JPanelDialog {

    protected ClientType clientType = ClientType.FRAMESET_EDITOR;

    protected static KeyValueProperties stateKVProps;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected String title = GlobalProperties.getIntlString("Frameset_Editor");

    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected String file = GlobalProperties.getIntlString("Untitled");
    
    protected String dtd = "./data/propbank/resource/frameset/frameset.dtd";
    protected String attibVals = "./data/propbank/resource/frameset/attrib-vals.txt";

    protected Frameset frameset;

    protected boolean dirty;

    protected SanchayBackup sanchayBackup;
    protected long backupPeriod = (long) 1000000;

    protected boolean commands[];
    protected Action actions[];

    protected MouseListener popupListener;

    /** Creates new form FramesetJPanel */
    public FramesetJPanel(Frameset frameset, String langEnc) {
        loadState(this);

        this.frameset = frameset;
        this.langEnc = langEnc;

        initComponents();

        popupListener = new PopupListener(editorJPopupMenu);
        addMouseListener(popupListener);
        framesetNoteJTextArea.addMouseListener(popupListener);
        framesetPredicateJTabbedPane.addMouseListener(popupListener);

        prepareCommands(null);

        init();
    }

    public FramesetJPanel(Frameset frameset, String file, String dtd, String langEnc) {
        loadState(this);

        this.frameset = frameset;
        this.langEnc = langEnc;
        this.file = file;
        this.dtd = dtd;

        initComponents();

        popupListener = new PopupListener(editorJPopupMenu);
        addMouseListener(popupListener);
        framesetNoteJTextArea.addMouseListener(popupListener);
        framesetPredicateJTabbedPane.addMouseListener(popupListener);

        prepareCommands(null);

        displayFile(dtd, charset, null);

        init();
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    public void init()
    {
        Frameset.readDTD(dtd, charset);
        Frameset.readAttributeValues(attibVals, charset);

        UtilityFunctions.setComponentFont(framesetNoteJTextArea, langEnc);

        String notes = frameset.getNote();

        if(notes != null)
            framesetNoteJTextArea.setText(notes);

        int count = frameset.countPredicates();

        for (int i = 0; i < count; i++)
        {
            FramesetPredicate predicate = frameset.getPredicate(i);
            FeatureStructure fs = predicate.getAttributes();

            if(fs != null)
            {
                FeatureValue fv = fs.getAttributeValue(GlobalProperties.getIntlString("lemma"));
                String lemma = "";

                if(fv != null)
                {
                    lemma = fv.getValue().toString();
                    FramesetPredicateJPanel framesetPredicateJPanel = new FramesetPredicateJPanel(langEnc);

                    if(owner != null)
                        framesetPredicateJPanel.setOwner(owner);
                    else if(dialog != null)
                        framesetPredicateJPanel.setDialog(dialog);

                    framesetPredicateJTabbedPane.add(lemma, framesetPredicateJPanel);

                    framesetPredicateJPanel.addPopupListener(popupListener);

                    framesetPredicateJPanel.setPredicate(predicate);

                    framesetPredicateJPanel.init();
                }
            }
        }
    }

    public String getDTDFile(EventObject e)
    {
        return dtd;
    }

    private static void loadState(FramesetJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.FRAMESET_EDITOR.toString());

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");

        if(currentDir == null)
        {
            currentDir = ".";
            stateKVProps.addProperty("CurrentDir", currentDir);
        }
    }

    private static void saveState(FramesetJPanel editorInstance) {
        stateKVProps = SanchayClientsStateData.getSateData().getPropertiesValue(ClientType.FRAMESET_EDITOR.toString());

        String currentDir = stateKVProps.getPropertyValue("CurrentDir");

        if(currentDir == null)
            currentDir = ".";

        File file = null;

        if(editorInstance.file != null)
        {
            file = new File(editorInstance.file);

            if(file.exists())
            {
                currentDir = file.getParent();
            }
        }

        stateKVProps.addProperty("CurrentDir", currentDir);

        SanchayClientsStateData.save();
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
        framesetNoteJPanel = new javax.swing.JPanel();
        framesetNoteJScrollPane = new javax.swing.JScrollPane();
        framesetNoteJTextArea = new javax.swing.JTextArea();
        framesetPredicateJTabbedPane = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());

        framesetNoteJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Frameset note"));
        framesetNoteJPanel.setLayout(new java.awt.BorderLayout());

        framesetNoteJTextArea.setColumns(20);
        framesetNoteJTextArea.setRows(2);
        framesetNoteJTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                framesetNoteJTextAreaCaretUpdate(evt);
            }
        });
        framesetNoteJScrollPane.setViewportView(framesetNoteJTextArea);

        framesetNoteJPanel.add(framesetNoteJScrollPane, java.awt.BorderLayout.CENTER);

        add(framesetNoteJPanel, java.awt.BorderLayout.NORTH);
        add(framesetPredicateJTabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void framesetNoteJTextAreaCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_framesetNoteJTextAreaCaretUpdate
    {//GEN-HEADEREND:event_framesetNoteJTextAreaCaretUpdate
        // TODO add your handling code here:
        frameset.setNote(framesetNoteJTextArea.getText());
    }//GEN-LAST:event_framesetNoteJTextAreaCaretUpdate


    private void prepareCommands(int appliedCommands[]) {
        commands = new boolean[FramesetAction._ACTIONS_];
        actions = new Action[FramesetAction._ACTIONS_];

        // Basic action commands
        for(int i = 0; i < commands.length; i++) {
            commands[i] = true;
            actions[i] = FramesetAction.createAction(this, i);
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
            }
        } else {
            for(int i = 0; i < commands.length; i++) {
                JMenuItem mi = new JMenuItem();
                mi.setAction(actions[i]);
                editorJPopupMenu.add(mi);
            }
        }

        InputMap inputMap = getInputMap();

        //Ctrl+s to save
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
        inputMap.put(key, actions[FramesetAction.SAVE]);

        //Ctrl+o to open
        key = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK);
        inputMap.put(key, actions[FramesetAction.OPEN]);

        //Ctrl+w to close
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK);
        inputMap.put(key, actions[FramesetAction.CLOSE]);

        //Ctrl+i to select input method
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        inputMap.put(key, actions[FramesetAction.SELECT_INPUT_METHOD]);

        //Ctrl+Shift+L to select language
        key = KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, actions[FramesetAction.SELECT_LANGUAGE]);

        //Ctrl+Shift+E to select encoding
        key = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK | Event.SHIFT_MASK);
        inputMap.put(key, actions[FramesetAction.SELECT_ENCODING]);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu editorJPopupMenu;
    private javax.swing.JPanel framesetNoteJPanel;
    private javax.swing.JScrollPane framesetNoteJScrollPane;
    private javax.swing.JTextArea framesetNoteJTextArea;
    private javax.swing.JTabbedPane framesetPredicateJTabbedPane;
    // End of variables declaration//GEN-END:variables

    public void initDocument() {
        frameset = new Frameset();

        file = GlobalProperties.getIntlString("Untitled");
        setTitle(title);

        framesetNoteJTextArea.setText("");
        framesetPredicateJTabbedPane.removeAll();
    }

    public void newFile(EventObject e) {
        closeFile(e);
    }

    public boolean closeFile(EventObject e) {
        if(dirty) {
            int retVal = -1;

//            if(dialog != null)
//                retVal = JOptionPane.showConfirmDialog(dialog, "The file " + file + " has been modified.\n\nDo you want to save the file?", "Closing File", JOptionPane.YES_NO_OPTION);
//            else
                retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("The_file_") + file + GlobalProperties.getIntlString("_has_been_modified.\n\nDo_you_want_to_save_the_file?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

            if(retVal == JOptionPane.NO_OPTION) {
                initDocument();

                init();
                return false;
            } else {
                save(e);

                initDocument();

                init();
                return true;
            }
        } else
        {
            initDocument();

            init();
        }

        if(sanchayBackup != null)
            FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, file);

        return true;
    }

    public void addPredicate(EventObject e) {
        FramesetPredicate predicate = new FramesetPredicate();

        frameset.addPredicate(predicate);

        FramesetPredicateJPanel framesetPredicateJPanel = new FramesetPredicateJPanel(langEnc);

        if(owner != null)
            framesetPredicateJPanel.setOwner(owner);
        else if(dialog != null)
            framesetPredicateJPanel.setDialog(dialog);

        framesetPredicateJPanel.setPredicate(predicate);
        framesetPredicateJPanel.init();

        framesetPredicateJPanel.addPopupListener(popupListener);

        framesetPredicateJTabbedPane.add(GlobalProperties.getIntlString("predicate"), framesetPredicateJPanel);
    }

    public void editPredicate(EventObject e) {
        if(framesetPredicateJTabbedPane.getTabCount() == 0)
            return;

        FramesetPredicateJPanel framesetPredicateJPanel = (FramesetPredicateJPanel) framesetPredicateJTabbedPane.getSelectedComponent();
        FramesetPredicate predicate = framesetPredicateJPanel.getPredicate();

        int index = framesetPredicateJTabbedPane.indexOfComponent(framesetPredicateJPanel);

        String predicateLemma = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Please_enter_the_predicate_lemma"), "");

        if(predicateLemma != null && predicateLemma.equals("") == false)
        {
            predicate.getAttributes().addAttribute(GlobalProperties.getIntlString("lemma"), predicateLemma);
            framesetPredicateJTabbedPane.setTitleAt(index, predicateLemma);
        }
    }

    public void removePredicate(EventObject e) {
        FramesetPredicateJPanel framesetPredicateJPanel = (FramesetPredicateJPanel) framesetPredicateJTabbedPane.getSelectedComponent();
        FramesetPredicate predicate = framesetPredicateJPanel.getPredicate();
        frameset.removePredicate(predicate);
        framesetPredicateJTabbedPane.remove(framesetPredicateJPanel);
    }

    public void addRoleset(EventObject e) {
        FramesetPredicateJPanel framesetPredicateJPanel = (FramesetPredicateJPanel) framesetPredicateJTabbedPane.getSelectedComponent();
        framesetPredicateJPanel.addRoleset(e);
    }

    public void editRoleset(EventObject e) {
        FramesetPredicateJPanel framesetPredicateJPanel = (FramesetPredicateJPanel) framesetPredicateJTabbedPane.getSelectedComponent();
        framesetPredicateJPanel.editRoleset(e);
    }

    public void removeRoleset(EventObject e) {
        FramesetPredicateJPanel framesetPredicateJPanel = (FramesetPredicateJPanel) framesetPredicateJTabbedPane.getSelectedComponent();
        framesetPredicateJPanel.removeRoleset(e);
    }

    public void addRole(EventObject e) {
        FramesetPredicateJPanel framesetPredicateJPanel = (FramesetPredicateJPanel) framesetPredicateJTabbedPane.getSelectedComponent();
        framesetPredicateJPanel.addRole(e);
    }

    public void editExamples(EventObject e) {
        FramesetPredicateJPanel framesetPredicateJPanel = (FramesetPredicateJPanel) framesetPredicateJTabbedPane.getSelectedComponent();

        if(owner != null)
            framesetPredicateJPanel.setOwner(owner);
        else if(dialog != null)
            framesetPredicateJPanel.setDialog(dialog);

        framesetPredicateJPanel.editExamples(e);
    }

    public String switchLanguage(EventObject e) {
        return switchLanguageStatic(this);
    }

    public static String switchLanguageStatic(Component parent) {
        boolean switching = false;

        if(parent instanceof FramesetJPanel)
            switching = true;

        if(switching) {
            if(((FramesetJPanel) parent).dirty) {
                int retVal = JOptionPane.showConfirmDialog(parent, GlobalProperties.getIntlString("The_current_file_will_be_closed._If_you_haven't_saved_it,\nthe_data_may_be_lost._Do_you_want_to_continue?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

                if(retVal == JOptionPane.NO_OPTION)
                    return null;
            }

            ((FramesetJPanel) parent).framesetPredicateJTabbedPane.removeAll();
            ((FramesetJPanel) parent).frameset = new Frameset();
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
            initLang = SanchayLanguages.getLanguageName( ((FramesetJPanel) parent).langEnc );

        String selectedLanguage = (String) JOptionPane.showInputDialog(parent,
                GlobalProperties.getIntlString("Select_the_language"), GlobalProperties.getIntlString("Language"), JOptionPane.INFORMATION_MESSAGE, null,
                langs, initLang);

        if(switching && selectedLanguage != null && selectedLanguage.equals("") == false) {
            String langCode = SanchayLanguages.getLangEncCode(selectedLanguage);
            ((FramesetJPanel) parent).langEnc = langCode;

            UtilityFunctions.setComponentFont(((FramesetJPanel) parent).framesetNoteJTextArea, langCode);

            ((FramesetJPanel) parent).setTitle(((FramesetJPanel) parent).getTitle());
        }

        return selectedLanguage;
    }

    public String switchEncoding(EventObject e) {
        return switchEncodingStatic(this, langEnc, e);
    }

    protected static String switchEncodingStatic(Component parent, String leCode, EventObject e) {
        boolean switching = false;

        if(parent instanceof FramesetJPanel)
            switching = true;

        if(switching) {
            if(((FramesetJPanel) parent).dirty) {
                int retVal = JOptionPane.showConfirmDialog(parent, GlobalProperties.getIntlString("The_current_file_will_be_closed._If_you_haven't_saved_it,\nthe_data_may_be_lost._Do_you_want_to_continue?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

                if(retVal == JOptionPane.NO_OPTION)
                    return null;
            }

            ((FramesetJPanel) parent).framesetNoteJTextArea.setText("");
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
            initEnc = SanchayLanguages.getEncodingName( ((FramesetJPanel) parent).langEnc );

        String currentLanguage = SanchayLanguages.getLanguageName( ((FramesetJPanel) parent).langEnc );

        String selectedEncoding = (String) JOptionPane.showInputDialog(parent,
                GlobalProperties.getIntlString("Select_the_encoding"), GlobalProperties.getIntlString("Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                encNames, initEnc);

        if(switching && selectedEncoding != null && selectedEncoding.equals("") == false) {
            leCode = SanchayLanguages.getLangEncCode(currentLanguage, selectedEncoding);
            ((FramesetJPanel) parent).langEnc = leCode;

            UtilityFunctions.setComponentFont(((FramesetJPanel) parent).framesetNoteJTextArea, leCode);

            ((FramesetJPanel) parent).setTitle(((FramesetJPanel) parent).getTitle());
        }

        return selectedEncoding;
    }

    public void selectInputMethod(EventObject e) {
        String im = SanchayLanguages.selectInputMethod(this);

        if(owner != null)
            SanchayLanguages.changeInputMethod(owner, im);
        else if(dialog != null)
            SanchayLanguages.changeInputMethod(dialog, im);
    }

    public void showKBMap(EventObject e) {
        SanchayLanguages.showKBMap(this);
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

        //Create and set up the content pane.
        FramesetJPanel newContentPane = new FramesetJPanel(new Frameset(), GlobalProperties.getIntlString("hin::utf8"));

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

    @Override
    public void windowOpened(WindowEvent e)
    {
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
        saveState(this);
        closeFile(e);
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
    }

    @Override
    public void windowIconified(WindowEvent e)
    {
    }

    @Override
    public void windowDeiconified(WindowEvent e)
    {
    }

    @Override
    public void windowActivated(WindowEvent e)
    {
    }

    @Override
    public void windowDeactivated(WindowEvent e)
    {
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

            if(file != null) {
                File tfile = new File(file);

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
                file = chooser.getSelectedFile().getAbsolutePath();
//		charset = JOptionPane.showInputDialog(parentComponent, "Please enter the charset:", "UTF-8");

                displayFile(file, charset, e);
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

    public boolean save(EventObject e) {

        if(file.equals(GlobalProperties.getIntlString("Untitled")) == false || (new File(file)).exists()) {
            PrintStream ps = null;

            try {
                SanchayBackup.backup(file);

                String msg = frameset.validate(file, charset);

                if(msg == null)
                    frameset.save(file, charset);
                else
                {
                    JOptionPane.showMessageDialog(parentComponent, msg, GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    return false;
                }

            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            setTitle(file);

            dirty = false;

            if(dialog != null) {
                dialog.setVisible(false);
            }
        } else
            saveAs(e);

        return true;
    }

    public void saveAs(EventObject e) {
        try {
            String path = null;

//	    System.out.println("Current path: " + file);

            if(file != null && !file.equals("") && !file.equals(GlobalProperties.getIntlString("Untitled"))) {
                File tfile = new File(file);

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
                file = chooser.getSelectedFile().getAbsolutePath();

                if((new File(file)).exists())
                {
                    int retVal = JOptionPane.showConfirmDialog(parentComponent, GlobalProperties.getIntlString("The_file_") + file + GlobalProperties.getIntlString("_already_exists.\n\nDo_you_want_to_overwrite_it?"), GlobalProperties.getIntlString("Overwrite_File?"), JOptionPane.YES_NO_OPTION);

                    if(retVal == JOptionPane.NO_OPTION)
                        return;
                }
//		charset = JOptionPane.showInputDialog(parentComponent, "Please enter the charset:", "UTF-8");

                String msg = frameset.validate(file, charset);

                if(msg == null)
                    frameset.save(file, charset);
                else
                {
                    JOptionPane.showMessageDialog(parentComponent, msg, GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                setTitle(file);

                if(sanchayBackup != null)
                    FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, file);

                sanchayBackup = new SanchayBackup();
                FileMonitor.getInstance().addFileChangeListener(sanchayBackup, file, backupPeriod);

                dirty = false;
            }

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(parentComponent, GlobalProperties.getIntlString("Error_opening_file."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void print(PrintStream ps) {

//        ps.print(editorJTextPane.getText());
        ps.print("");
    }

    public void displayFile(String path, String charset, EventObject e) {
        if(path == null || path.equals(""))
            return;

        displayFile(new File(path), charset, e);
    }


    public void displayFile(File fileObj, String charset, EventObject e) {
        if(fileObj.isFile() == false || fileObj.exists() == false)
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
 
            initDocument();

            file = fileObj.getAbsolutePath();
            this.charset = charset;

            Element rootNode = null;

            try {
                rootNode = XMLUtils.parseW3CXML(file, charset);

                if(rootNode != null)
                {
                    frameset = new Frameset();
                    frameset.readXML(rootNode);

                    init();
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            sanchayBackup = new SanchayBackup();
            FileMonitor.getInstance().addFileChangeListener(sanchayBackup, file, backupPeriod);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        dirty = false;

        setTitle(file);
        
        if(owner != null && cursor != null)
        {
            owner.setCursor(cursor);
        }

        saveState(this);
    }

    public void displayFile(EventObject e)
    {
        if(e instanceof DisplayEvent)
        {
            DisplayEvent de = (DisplayEvent) e;
            displayFile(de.getFilePath(), de.getCharset(), e);
        }
    }

    @Override
    public String getDisplayedFile(EventObject e)
    {
        return file;
    }

    @Override
    public String getCharset(EventObject e)
    {
        return charset;
    }

    @Override
    public String getLangEnc()
    {
        return langEnc;
    }

    @Override
    public Frame getOwner()
    {
        return owner;
    }

    @Override
    public void setOwner(Frame frame)
    {
        owner = (JFrame) frame;
    }

    @Override
    public void setParentComponent(Component parentComponent)
    {
        this.parentComponent = parentComponent;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = (new File(title)).getName();

        String fullTitle = GlobalProperties.getIntlString("Sanchay:_") + ClientType.SANCHAY_EDITOR.toString() + ": " + title;

        if(dialog != null)
            dialog.setTitle(fullTitle);
        else if(owner != null)
            owner.setTitle(fullTitle);
    }

    @Override
    public JMenuBar getJMenuBar()
    {
        return null;
    }

    @Override
    public JPopupMenu getJPopupMenu()
    {
        return null;
    }

    @Override
    public JToolBar getJToolBar()
    {
        return null;
    }

    @Override
    public void setDialog(JDialog dialog)
    {
        this.dialog = dialog;
    }
}
