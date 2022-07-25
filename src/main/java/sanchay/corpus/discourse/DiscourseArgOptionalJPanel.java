/*
 * DiscourseArgOptionalJPanel.java
 *
 * Created on June 11, 2008, 10:17 AM
 */
package sanchay.corpus.discourse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.gui.SSFTreeCellRendererNew;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.common.FileDisplayer;
import sanchay.gui.common.PopupListener;
import sanchay.gui.common.SanchayLanguages;
import sanchay.properties.KeyValueProperties;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.DisplayEvent;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.text.editor.gui.TextEditorJPanel;
import sanchay.tree.SanchayTreeModel;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.util.UtilityFunctions;
import sanchay.util.file.FileMonitor;
import sanchay.util.file.SanchayBackup;

/**
 *
 * @author  RAKESH
 */
public class DiscourseArgOptionalJPanel extends javax.swing.JPanel implements WindowListener, FileDisplayer {
    //added by Rakesh
    public int MODE;
    public static final int EXPLICIT_MODE = 0;
    public static final int IMPLICIT_MODE = 1;
    public static final int ALTLEX_MODE = 2;
    public static final int ENTREL_MODE = 3;
    public static final int NOREL_MODE = 4;
    public static final int DEFAULT_MODE = 0;
    private String mode = GlobalProperties.getIntlString("Explicit");
    //
    private JFrame owner;
    private JDialog dialog;
    JDialog addInfoDialog;
    javax.swing.JButton okButton;
    javax.swing.JButton cancelButton;
    String langEnc = GlobalProperties.getIntlString("hin::utf8");
    String title = GlobalProperties.getIntlString("Untitled");
    AbstractDocument doc;
    private static String statePropFile = GlobalProperties.resolveRelativePath("props/state/discourse-annotation-state.txt");
    private static KeyValueProperties stateKVProps;
    protected boolean dirty;
    private SanchayBackup sanchayBackup;
    private long backupPeriod = (long) 10000;
    SanchayTableModel connectiveInfoTableModel;
    SanchayTableJPanel connectiveInfoTableJPanel;
    JTree connectiveJTree;
    JTree additionalInfoConJTree;
    JTree additionalInfoArgSupJTree;
    SanchayTreeJPanel additionalInfoConTreeJPanel;
    SanchayTreeJPanel additionalInfoArgSupTreeJPanel;
    SanchayTreeJPanel connectiveInfoTreeJPanel;
    TextEditorJPanel textEditorJPanel;
    JTextArea textJTextArea;
    JTextArea logJTextArea;
    SSFStory discourseInfo;
    SSFStory additionalInfoCon;
    SSFStory additionalInfoArgSup;
    SSFPhrase rootNode;    //undo helpers
    protected UndoAction undoAction;
    protected RedoAction redoAction;
    protected UndoManager undo = new UndoManager();
    private boolean commands[];
    private Action actions[];
    private boolean arg1Commands[];
    private Action arg1Actions[];
    private boolean arg2Commands[];
    private Action arg2Actions[];
    private boolean moreCommands[];
    private Action moreActions[];
    private boolean commandButtonsShown;
    protected boolean moreCommandsShown;
    int appliedCommands[];
    int appliedMoreCommands[];
    JDialog discourseConnectiveInfoDialog;    //Added by Nrapesh
    protected boolean boolmenuItem[] = new boolean[DiscourseAnnotationInterfaceAction._MORE_ACTIONS_];
    private SSFPhrase connectiveJRoot;                        //root of the tree containing the instances of connective
    private SanchayTreeModel connectiveJTreeModel;             //tree model for the tree containing the instances of connective
    private SanchayTreeModel additionalInfoConTreeModel;        //tree model for the tree containing the additional information for connectives
    private SanchayTreeModel additionalInfoArgSupTreeModel;     //tree model for the tree containing the additional information for supplements and arguements
    private Toolkit toolkit = Toolkit.getDefaultToolkit();      //the defualt toolkit
    private JMenuItem menuItem[];                               //menuitem containing the options for the connective tree
    private String fileinitopened = null;                         //containing the name of the file opened

    //Added by Kinshul
    private DefaultHighlighter.DefaultHighlightPainter painter;   //painter for highlighting 
    private SSFNode Lastselect;                                  //storefor the last selected node from the tree     
    private SSFNode lastSelect;                                //stores the last selected node from the tree
    private AnnotationInfo info;                                  //stores the root of the data structure
    private AnnotationInfo child;
    private String tpath;                                     //stores the string representing the path in the optional tree
    private SSFPhrase argRoot;                                    //root of the optional tags tree
    private DefaultTreeModel model1;                                    //root of the optional information tree for arguments
    private DefaultTreeModel model2;                             //root of the optional information tree for the supplements
    private DefaultTreeModel model3;                             //root of the optional information tree for the connectives
    //added by Kinshul till here
    private String textFile;
    private String charset;
    AnnotationInfo larg;
    AnnotationInfo rarg;
    DiscourseAnnotationJPanel mainJPanel;

    /** Creates new form DiscourseArgOptionalJPanel */
    public DiscourseArgOptionalJPanel(String lancEnc, TextEditorJPanel editorJPanel, DiscourseAnnotationJPanel mainJPanel) {

        this.mainJPanel = mainJPanel;

        this.langEnc = lancEnc;
        textEditorJPanel = editorJPanel;
        textFile = textEditorJPanel.getDisplayedFile(null);
        charset = textEditorJPanel.getCharset(null);
        doc = textEditorJPanel.getDocument();
        title = textEditorJPanel.getTitle();
        textJTextArea = textEditorJPanel.textJTextArea;
        logJTextArea = textEditorJPanel.logJTextArea;

        initComponents();

        //  initExtraComponents();

        loadState(this);

        //Added by Kinshul
        tpath = null;
        lastSelect = null;
        Lastselect = null;
        info = new AnnotationInfo("Story");
        //Added by Nrapesh
        fileinitopened = null;

        connectiveInfoTableModel = new SanchayTableModel(new String[]{"Feature", "Value"}, 0);

        initConnectiveInfoTable();

        undoAction = new UndoAction();
        redoAction = new RedoAction();

        InputMap inputMap = textJTextArea.getInputMap();

        //Ctrl-z to undo
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        inputMap.put(key, undoAction);

        //Ctrl-y to undo
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
        inputMap.put(key, redoAction);

        connectiveInfoTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(connectiveInfoTableModel, langEnc);

        connectiveInfoJPanel.add(connectiveInfoTableJPanel, BorderLayout.CENTER);

        //Making Panel for additional information for connectives

        try {
            additionalInfoCon = new SSFStoryImpl();
            additionalInfoCon.readFile(GlobalProperties.getHomeDirectory() + "/" + "tmp/NewOptionalInformationForConnectives.txt", GlobalProperties.getIntlString("UTF-8"));
            SSFSentence sentence = additionalInfoCon.getSentence(0);
//          rootNode = new SSFPhrase("0", "", "SSF", "");
            rootNode = sentence.getRoot();
        } catch (Exception ex) {
            Logger.getLogger(DiscourseConnectiveInfoJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        String langCode = SanchayLanguages.getLanguageCodeFromLECode(langEnc);
        String langCode = langEnc;
        additionalInfoConTreeJPanel = SanchayTreeJPanel.createSSFDisplayJPanel(rootNode, 3, new String[] {langCode, langCode, langCode});

        additionalInfoConTreeModel = additionalInfoConTreeJPanel.getModel();
        additionalInfoConJTree = (JTree) additionalInfoConTreeJPanel.getJTree();

//      new SanchayTreeJPanel(null, SanchayTreeJPanel.XML_MODE, lancEnc);

        additionalInfoConJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                //additionalInfoConJTreeValueChanged(evt);
                addInfotreeValueChanged(evt);
            }
        });

        //Making panel for additional information for Args/Sups
        try {
            additionalInfoArgSup = new SSFStoryImpl();
            additionalInfoArgSup.readFile(GlobalProperties.getHomeDirectory() + "/" + "tmp/Optional Information for ArgSup.txt", GlobalProperties.getIntlString("UTF-8"));
            SSFSentence sentence = additionalInfoArgSup.getSentence(0);
//            rootNode = new SSFPhrase("0", "", "SSF", "");
            rootNode = sentence.getRoot();
        } catch (Exception ex) {
            Logger.getLogger(DiscourseConnectiveInfoJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        additionalInfoArgSupTreeJPanel = SanchayTreeJPanel.createSSFDisplayJPanel(rootNode, 3, new String[] {langCode, langCode, langCode});
//      new SanchayTreeJPanel(null, SanchayTreeJPanel.XML_MODE, lancEnc);

        //added by Rakesh
        additionalInfoArgSupTreeModel = additionalInfoArgSupTreeJPanel.getModel();
        additionalInfoArgSupJTree = (JTree) additionalInfoArgSupTreeJPanel.getJTree();

        additionalInfoArgSupJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                //additionalInfoArgSupJTreeValueChanged(evt);
                addInfotreeValueChanged(evt);
            }
        });

        //Making panel for connectives and its instances
        try {
            connectiveJRoot = new SSFPhrase("0", "", "Story", "", "Story");
        } catch (Exception ex) {
            Logger.getLogger(DiscourseArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        connectiveInfoTreeJPanel = SanchayTreeJPanel.createSSFDisplayJPanel(connectiveJRoot, 3, new String[] {langCode, langCode, langCode});
//      new SanchayTreeJPanel(null, SanchayTreeJPanel.XML_MODE, lancEnc);
        connectiveJTreeModel = connectiveInfoTreeJPanel.getModel();
        connectiveJTree = connectiveInfoTreeJPanel.getJTree();

        connectiveJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                connectiveJTreeValueChanged(evt);
            }
        });

        connectiveJTree.addMouseListener(new java.awt.event.MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
            public void mousePressed(MouseEvent e) {
                connectiveJTreeMouseEvent(e);
            }
            public void mouseReleased(MouseEvent e) {
                connectiveJTreeMouseEvent(e);
            }
        });

        add(connectiveInfoTreeJPanel, BorderLayout.CENTER);

        okButton = new JButton(GlobalProperties.getIntlString("Ok"));
        cancelButton = new JButton(GlobalProperties.getIntlString("Cancel"));

        okButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        prepareCommands(appliedCommands, appliedMoreCommands);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        annotationJPopupMenu = new javax.swing.JPopupMenu();
        moreMenu = new javax.swing.JMenu();
        arg1Menu = new javax.swing.JMenu();
        arg2Menu = new javax.swing.JMenu();
        lowerJPanel = new javax.swing.JPanel();
        connnectiveJPanel = new javax.swing.JPanel();
        connectiveJLabel = new javax.swing.JLabel();
        connectiveJTextField = new javax.swing.JTextField();
        findConnectiveJButton = new javax.swing.JButton();
        caseJCheckBox = new javax.swing.JCheckBox();
        freezeJButton = new javax.swing.JButton();
        connectiveInfoJPanel = new javax.swing.JPanel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        moreMenu.setText(bundle.getString("More...")); // NOI18N
        annotationJPopupMenu.add(moreMenu);

        arg1Menu.setText(GlobalProperties.getIntlString("Arguement1.."));
        annotationJPopupMenu.add(arg1Menu);

        arg2Menu.setText(GlobalProperties.getIntlString("Arguement2.."));
        annotationJPopupMenu.add(arg2Menu);

        setLayout(new java.awt.BorderLayout());

        lowerJPanel.setLayout(new java.awt.BorderLayout());

        connnectiveJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        connectiveJLabel.setLabelFor(connectiveJTextField);
        connectiveJLabel.setText(bundle.getString("Connective")); // NOI18N
        connnectiveJPanel.add(connectiveJLabel);

        connectiveJTextField.setColumns(10);
        connectiveJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectiveJTextFieldActionPerformed(evt);
            }
        });
        connnectiveJPanel.add(connectiveJTextField);

        findConnectiveJButton.setText(bundle.getString("Find")); // NOI18N
        findConnectiveJButton.setToolTipText(bundle.getString("Find_the_instances_of_connective")); // NOI18N
        findConnectiveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findConnectiveJButtonActionPerformed(evt);
            }
        });
        connnectiveJPanel.add(findConnectiveJButton);

        caseJCheckBox.setSelected(true);
        caseJCheckBox.setText(bundle.getString("Match_Case")); // NOI18N
        caseJCheckBox.setToolTipText(bundle.getString("Case_matching")); // NOI18N
        caseJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caseJCheckBoxActionPerformed(evt);
            }
        });
        connnectiveJPanel.add(caseJCheckBox);

        freezeJButton.setText(bundle.getString("Freeze")); // NOI18N
        freezeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freezeJButtonActionPerformed(evt);
            }
        });
        connnectiveJPanel.add(freezeJButton);

        lowerJPanel.add(connnectiveJPanel, java.awt.BorderLayout.NORTH);

        connectiveInfoJPanel.setLayout(new java.awt.BorderLayout());
        lowerJPanel.add(connectiveInfoJPanel, java.awt.BorderLayout.CENTER);

        add(lowerJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

private void findConnectiveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findConnectiveJButtonActionPerformed
// TODO add your handling code here:
    AnnotationInfo node;
    AnnotationInfo instance;
    AnnotationInfo con;
    int pos[];


    if (textEditorJPanel.textJTextArea.isEditable() == true) {
        JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Text_can_only_be_processed_if_the_text_has_been_frozen._Freeze_the_text_to_proceed"), GlobalProperties.getIntlString("Attention"), JOptionPane.INFORMATION_MESSAGE);
    } else {

        String connective = connectiveJTextField.getText();
        String conn = connective;
        if (caseJCheckBox.isSelected() == false) {
            connective = connective.toLowerCase();
        }
        connectiveJTextField.setText(null);
        SSFNode connectiveJChild;
        int count = connectiveJTreeModel.getChildCount(connectiveJRoot), j;

        try {
            connectiveJChild = new SSFPhrase("0", "", connective, "", connective);
        } catch (Exception ex) {
            Logger.getLogger(DiscourseArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (j = 0; j < count; j++) {
            DefaultMutableTreeNode temp = (DefaultMutableTreeNode) connectiveJTreeModel.getChild(connectiveJRoot, j);
            String tmp1 = temp.toString();//.toLowerCase();
            if (tmp1.compareTo(connective) == 0) {
                con = info.getChild(j);
                //  if(con.getType().compareTo(node.getType())==0)
                break;
            }
        }

        if (j >= count) {
            if (connective.isEmpty() == false) {
                connectiveJChild = addObject(connectiveJRoot, conn, true);
                node = new AnnotationInfo(conn);
                node.setParent(info);
                info.addChild(node);
                //Added by Rakesh
                node.setType(mode);
                //
                connectiveJTextField.setText(null);
                String st = textEditorJPanel.textJTextArea.getText(), ch;
                if (caseJCheckBox.isSelected() == false) {
                    st = st.toLowerCase();
                }
                int l = st.length(), n = connective.length(), counter = 0, i = 0, k = 0, index = 0;
                while (i < l) {
                    if ((i == 0 || st.charAt(i - 1) == ' ' || st.charAt(i - 1) == '.' || st.charAt(i - 1) == '(' || st.charAt(i - 1) == ')' || st.charAt(i - 1) == ',' || st.charAt(i - 1) == '!' || st.charAt(i - 1) == '|' || st.charAt(i - 1) == '+' || st.charAt(i - 1) == '/' || st.charAt(i - 1) == '*' || st.charAt(i - 1) == '-' || st.charAt(i - 1) == '=' || st.charAt(i - 1) == ')' || st.charAt(i - 1) == '{' || st.charAt(i - 1) == '}' || st.charAt(i - 1) == '[' || st.charAt(i - 1) == ']' || st.charAt(i - 1) == ':' || st.charAt(i - 1) == ';' || st.charAt(i - 1) == '"' || st.charAt(i - 1) == '\'' || st.charAt(i - 1) == '\n' || st.charAt(i - 1) == ')') && st.charAt(i) == connective.charAt(0)) {
                        index = i;
                        int flag = 1;
                        for (k = 0; k < n; k++) {
                            if (i >= l || connective.charAt(k) != st.charAt(i)) {
                                flag = 0;
                                break;
                            }
                            i++;
                        }
                        if (flag == 1 && (i == l || st.charAt(i) == ' ' || st.charAt(i) == ',' || st.charAt(i) == '.' || st.charAt(i) == '(' || st.charAt(i) == ')' || st.charAt(i) == '|' || st.charAt(i) == '!' || st.charAt(i) == ';' || st.charAt(i) == ':' || st.charAt(i) == '?' || st.charAt(i) == '>' || st.charAt(i) == '<' || st.charAt(i) == '{' || st.charAt(i) == '}' || st.charAt(i) == '\n' || st.charAt(i) == '[' || st.charAt(i) == ']' || st.charAt(i) == '*' || st.charAt(i) == '-' || st.charAt(i) == '+' || st.charAt(i) == '/' || st.charAt(i) == '"' || st.charAt(i) == '\'')) {
                            pos = new int[2];
                            pos[0] = index;
                            pos[1] = i;
                            counter++;
                            ch = new String();
                            ch = "Instance" + String.valueOf(counter);
                            addObject((SSFPhrase) connectiveJChild, ch, false);
                            instance = new AnnotationInfo(ch);
                            instance.setPos(pos);
                            instance.setParent(node);
                            
                            try {
                                instance.setHead(textJTextArea.getText(index, i - index));
                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                            
                            instance.setType("Explicit");
                            node.addChild(instance);
                        //   connectiveJTree.setInvokesStopCellEditing(true);

                        }
                    } else {
                        i++;
                    }

                }

                connectiveJTreeModel.reload();
                if (counter == 0) {
                    DefaultTreeModel treeModel = (DefaultTreeModel) connectiveJTree.getModel();
                    connectiveJRoot.removeChild(j);
                    connectiveJTreeModel.reload();
//					treeModel.removeNodeFromParent(connectiveJChild);
                    int num = info.NumOfChildren();
                    info.removeChild(num - 1);
                    JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("No_instance_found.The_connective_will_not_be_added"), "Ooops!!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("The_given_connective_is_already_present"), "Attention", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}//GEN-LAST:event_findConnectiveJButtonActionPerformed

private void connectiveJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectiveJTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_connectiveJTextFieldActionPerformed

private void caseJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_caseJCheckBoxActionPerformed
// TODO add your handling code here:
    if (caseJCheckBox.isSelected()) {
        logJTextArea.append(GlobalProperties.getIntlString("Match_Case_Selected\n"));
    } else {
        logJTextArea.append(GlobalProperties.getIntlString("Match_case_deselected\n"));
    }
}//GEN-LAST:event_caseJCheckBoxActionPerformed

private void freezeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freezeJButtonActionPerformed
// TODO add your handling code here:
    freeze(evt);
}//GEN-LAST:event_freezeJButtonActionPerformed

     //added by Kinshul
//action performed when a new node is selected in the connectiveJTree
    private void connectiveJTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {                                             
        Highlighter h=textJTextArea.getHighlighter();
	h.removeAllHighlights();
        
        //Added by Rakesh
        String str = new String();
        String selection = new String();
        
	if(textJTextArea.isEditable() == false)
	{
		//added by nrapesh
            TreePath currentSelection =connectiveJTree.getSelectionPath();
            if(currentSelection==null)
		{
			for(int i=0; i < DiscourseAnnotationInterfaceAction._MORE_ACTIONS_; i++){
				boolmenuItem[i]=false;
				moreActions[i].setEnabled(boolmenuItem[i]);
			}
		}
		//added by Kinshul
		else
		{
			String story=textJTextArea.getText();
			int x=0,y=0;
			SSFPhrase parent=(SSFPhrase)connectiveJTree.getLastSelectedPathComponent();
                        TreeNode temp1= (javax.swing.tree.TreeNode)connectiveJTree.getLastSelectedPathComponent();
			lastSelect= (SSFNode) parent;
			Lastselect= (SSFNode) temp1;
			TreeNode temp2;
			y=parent.getLevel();
			if(parent.isRoot()==false)
			{
				temp2=temp1.getParent();
				x=temp2.getIndex(temp1);
			}

			if(parent.isRoot()==true && y==0)
			{
				for(int i=0;i<DiscourseAnnotationInterfaceAction._MORE_ACTIONS_;i++)
				{
					boolmenuItem[i]=false;
					moreActions[i].setEnabled(boolmenuItem[i]);
				}
				//do nothing if the node is a root node
                            
                            //Added by Rakesh
                            selection = "Root";
                            addInfoTable(str,selection);

			}
			if(parent.isLeaf()==false && parent.isRoot()==false && y==1)
			{
				boolmenuItem[0]=true;
				moreActions[0].setEnabled(boolmenuItem[0]);
				for(int i=1;i < DiscourseAnnotationInterfaceAction._MORE_ACTIONS_;i++)
				{
					boolmenuItem[i]=false;
					moreActions[i].setEnabled(boolmenuItem[i]);
				}
				
				//newly added code
				AnnotationInfo node=info.getChild(x);
                                logJTextArea.append(GlobalProperties.getIntlString("Connective_Node_:_")+node.getName()+"\n");
                                logJTextArea.append(GlobalProperties.getIntlString("Type_:_")+node.getType()+"\n");
				AnnotationInfo childe;
				for(int i=0;i<node.NumOfChildren();i++)
				{
					childe=node.getChild(i);
					int a[]=childe.getPos(),b[]=childe.getsupInfo();
					if(a!=null)
					{
						this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
						for(int j=0;j<a.length;j=j+2)
						{

							try
							{
								h.addHighlight(a[j],a[j+1],this.painter);
                                                                if(i==0)
                                                                    textJTextArea.setCaretPosition(a[j]);
							}catch(BadLocationException ble)
							{
								logJTextArea.append(GlobalProperties.getIntlString("Error:_An_exception_occured_while_trying_to_highlight_text\n"));
							}
						}
					}
					if(b!=null)
				{
						this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE);
						logJTextArea.append(GlobalProperties.getIntlString("Suplement_:_"));
						for(int j=0;j<b.length;j=j+2)
						{
							logJTextArea.append(story.substring(b[j],b[j+1]));
							try
							{
								h.addHighlight(b[j],b[j+1],this.painter);
                                                                
  							}catch(BadLocationException ble)
							{
								logJTextArea.append(GlobalProperties.getIntlString("Error:_An_exception_occured_while_trying_to_highlight_text\n"));
							}
						}
						logJTextArea.append(GlobalProperties.getIntlString("\n"));
                                                String temp=childe.getoptInfo();
                                                if(temp!=null)
                                                {
                                                   logJTextArea.append(GlobalProperties.getIntlString("Optional_Tags_:_")+temp+GlobalProperties.getIntlString("\n"));
                                                }
					}

				}
                                genMenu(node, 1);
                              selection = "Connective";
                            addInfoTable(str,selection);  

			}
			if(parent.isLeaf()==true && parent.isRoot()==false && y==2)
			{
				temp2=temp1.getParent();
				TreeNode temp3=temp2.getParent();
				int p=temp3.getIndex(temp2);
				AnnotationInfo node=info.getChild(p);
				AnnotationInfo childe=node.getChild(x);
				highlight(childe);
                                genMenu(childe, 2);
//				AnnotationInfo larg=null,rarg=null,opt1,opt2;
//				for(int i=0;i<child.NumOfChildren();i++)
//				{
//					AnnotationInfo temp=child.getChild(i);
//					if(temp.getFlag()==0)
//						larg=temp;
//					if(temp.getFlag()==1)
//						rarg=temp;
//				}        
//				boolmenuItem[0]=false;
//				menuItem[0].setEnabled(boolmenuItem[0]);
//				if(larg==null)
//				{
//					boolmenuItem[1]=false;
//					menuItem[1].setEnabled(boolmenuItem[1]);
//					boolmenuItem[3]=false;
//					menuItem[3].setEnabled(boolmenuItem[3]);
//				}
//				else
//				{
//					boolmenuItem[1]=true;
//					menuItem[1].setEnabled(boolmenuItem[1]);
//					if(larg.getsupInfo()==null)
//					{
//						boolmenuItem[3]=false;
//						menuItem[3].setEnabled(boolmenuItem[3]);
//					}
//					else
//					{
//						boolmenuItem[3]=true;
//						menuItem[3].setEnabled(boolmenuItem[3]);
//					}
//				}
//				if(rarg==null)
//				{
//					boolmenuItem[2]=false;
//					menuItem[2].setEnabled(boolmenuItem[2]);
//					boolmenuItem[4]=false;
//					menuItem[4].setEnabled(boolmenuItem[4]);
//				}
//				else
//				{
//					boolmenuItem[2]=true;
//					menuItem[2].setEnabled(boolmenuItem[2]);
//					if(rarg.getsupInfo()==null)
//					{
//						boolmenuItem[4]=false;
//						menuItem[4].setEnabled(boolmenuItem[4]);
//					}
//					else
//					{
//						boolmenuItem[4]=true;
//						menuItem[4].setEnabled(boolmenuItem[4]);
//					}
//				}
                        }
                }
        }
	else
	{
		JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Please_freeze_the_text_inorder_to_do_any_processing_on_it."),"Attention!",JOptionPane.INFORMATION_MESSAGE);
	}

    }

//added till here
    private void connectiveJTreeMouseEvent(MouseEvent e)
    {
	if(textJTextArea.isEditable() == false)
	{
            TreePath currentSelection =connectiveJTree.getSelectionPath();

            if(currentSelection != null)
            {
                TreeNode node = (javax.swing.tree.TreeNode)connectiveJTree.getLastSelectedPathComponent();
                int x=0,y=0;
                SSFPhrase parent=(SSFPhrase)connectiveJTree.getLastSelectedPathComponent();
                TreeNode temp1= (javax.swing.tree.TreeNode)connectiveJTree.getLastSelectedPathComponent();
                lastSelect= (SSFNode) parent;
                Lastselect= (SSFNode) temp1;
                TreeNode temp2;
                y=parent.getLevel();
                if(parent.isRoot()==false)
                {
                        temp2=temp1.getParent();
                        x=temp2.getIndex(temp1);
                }

                if(parent.isRoot()==true && y==0)
                {
                        for(int i=0;i<DiscourseAnnotationInterfaceAction._MORE_ACTIONS_;i++)
                        {
                                boolmenuItem[i]=false;
                                moreActions[i].setEnabled(boolmenuItem[i]);
                        }
                }
                if(parent.isLeaf()==false && parent.isRoot()==false && y==1)
                {
                        boolmenuItem[0]=true;
                        moreActions[0].setEnabled(boolmenuItem[0]);
                        for(int i=1;i < DiscourseAnnotationInterfaceAction._MORE_ACTIONS_;i++)
                        {
                                boolmenuItem[i]=false;
                                moreActions[i].setEnabled(boolmenuItem[i]);
                        }

                        AnnotationInfo infoNode = info.getChild(x);                        
                        genMenu(infoNode, 1);
                }
                if(parent.isLeaf()==true && parent.isRoot()==false && y==2)
                {
                        temp2=temp1.getParent();
                        TreeNode temp3=temp2.getParent();
                        int p=temp3.getIndex(temp2);
                        AnnotationInfo infoNode = info.getChild(p);
                        AnnotationInfo childe=infoNode.getChild(x);
                        genMenu(childe, 2);
                }
            }
        }
    }

//added by Kinshul 
//action performed when a user selects a node in any optional tags trees
    private void addInfotreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {                                   
        TreePath Path=evt.getPath();
      //  System.out.println(tpath);
        
//        JOptionPane.showMessageDialog(this,Path.toString(),"Path",JOptionPane.INFORMATION_MESSAGE);
        String temp1=Path.toString();
        int l=temp1.length();
        String temp2=temp1.replace(',',':');
        String temp3=temp2.substring(l-6,l);
        if(temp3.compareTo("Other]")==0)
        {
           String temp4=temp2.substring(0,l-6);
           String inputValue = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Please_input_a_value_for_")+temp4);
           if(inputValue==null)
           {
               tpath=null;
           }
           tpath=temp4+inputValue+"]";
        }
        else

        
        {
            tpath=temp2;
        }
        logJTextArea.append(tpath+"\n");
  //      JOptionPane.showMessageDialog(this,tpath,"Path",JOptionPane.INFORMATION_MESSAGE);
        return;
    }                                  


    private void prepareCommands(int appliedCommands[], int appliedMoreCommands[])
    {
	commands = new boolean[DiscourseAnnotationInterfaceAction._BASIC_ACTIONS_];
	actions = new Action[DiscourseAnnotationInterfaceAction._BASIC_ACTIONS_];
        
        arg1Commands = new boolean[DiscourseAnnotationInterfaceAction._ARG1_ACTIONS];
	arg1Actions = new Action[DiscourseAnnotationInterfaceAction._ARG1_ACTIONS];
        
        arg2Commands = new boolean[DiscourseAnnotationInterfaceAction._ARG2_ACTIONS];
	arg2Actions = new Action[DiscourseAnnotationInterfaceAction._ARG2_ACTIONS];

        moreCommands = new boolean[DiscourseAnnotationInterfaceAction._MORE_ACTIONS_];
	moreActions = new Action[DiscourseAnnotationInterfaceAction._MORE_ACTIONS_];

        // Basic action commands
	for(int i = 0; i < commands.length; i++)
	{
	    commands[i] = true;
	    actions[i] = DiscourseAnnotationInterfaceAction.createAction(this, i);
	}
        
       
//        Font btnFont = new Font("Dialog", Font.PLAIN, 11);
//        Font btnFont = getFont().deriveFont(Font.PLAIN, 10);
	
	if(appliedCommands != null)
	{
	    for(int i = 0; i < commands.length; i++)
		commands[i] = false;
	    
	    for(int i = 0; i < appliedCommands.length; i++)
	    {
		int cmd = appliedCommands[i];
		commands[cmd] = true;
		
		JMenuItem mi = new JMenuItem();
		mi.setAction(actions[cmd]);
		annotationJPopupMenu.add(mi);
		
		JButton jb = new JButton(actions[cmd]);
		jb.setAction(actions[cmd]);
//                jb.setFont(btnFont);
//		mainCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
	    }
	    
//	    ((GridLayout) mainCommandsJPanel.getLayout()).setColumns(appliedCommands.length);
//	    ((GridLayout) mainCommandsJPanel.getLayout()).setHgap(4);
	}
	else
	{
	    for(int i = 0; i < commands.length; i++)
	    {
		
                JMenuItem mi = new JMenuItem();
		mi.setAction(actions[i]);
		annotationJPopupMenu.add(mi);
		
		JButton jb = new JButton(actions[i]);
		jb.setAction(actions[i]);
//		mainCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
	    }
	    
            
//	    ((GridLayout) mainCommandsJPanel.getLayout()).setColumns(commands.length);
//	    ((GridLayout) mainCommandsJPanel.getLayout()).setHgap(4);
	}

        // Arg1 action commands
        for(int i = 0; i < arg1Commands.length; i++)
	{
	    arg1Commands[i] = true;
	    arg1Actions[i] = DiscourseAnnotationInterfaceAction.createarg1Action(this, i);
	}
        
        for(int i = 0; i < arg1Commands.length; i++)
	{
		//moreActions[i].setEnabled(false);
                JMenuItem mi = new JMenuItem();
		mi.setAction(arg1Actions[i]);
		arg1Menu.add(mi);
                                
		JButton jb = new JButton(arg1Actions[i]);
		jb.setAction(arg1Actions[i]);
//		moreCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
	}
	    
        
         for(int i = 0; i < arg2Commands.length; i++)
	{
	    arg2Commands[i] = true;
	    arg2Actions[i] = DiscourseAnnotationInterfaceAction.createarg2Action(this, i);
	}
        
        for(int i = 0; i < arg2Commands.length; i++)
	{
		//moreActions[i].setEnabled(false);
                JMenuItem mi = new JMenuItem();
		mi.setAction(arg2Actions[i]);
		arg2Menu.add(mi);
                                
		JButton jb = new JButton(arg2Actions[i]);
		jb.setAction(arg2Actions[i]);
//		moreCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
	}
//	    ((GridLayout) moreCommandsJPanel.getLayout()).setColumns(moreCommands.length);
//	    ((GridLayout) moreCommandsJPanel.getLayout()).setHgap(4);
	
        // More action commands
	for(int i = 0; i < moreCommands.length; i++)
	{
	    moreCommands[i] = true;
	    moreActions[i] = DiscourseAnnotationInterfaceAction.createMoreAction(this, i);
	}
	
	if(appliedMoreCommands != null)
	{
	    for(int i = 0; i < moreCommands.length; i++)
		moreCommands[i] = false;
	    
	    for(int i = 0; i < appliedMoreCommands.length; i++)
	    {
		int cmd = appliedMoreCommands[i];
		moreCommands[cmd] = true;
		
		JMenuItem mi = new JMenuItem();
		mi.setAction(moreActions[cmd]);
		moreMenu.add(mi);
		
		JButton jb = new JButton(moreActions[cmd]);
		jb.setAction(moreActions[cmd]);
//                jb.setFont(btnFont);
//		moreCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
	    }
	    
//	    ((GridLayout) moreCommandsJPanel.getLayout()).setColumns(appliedCommands.length);
//	    ((GridLayout) moreCommandsJPanel.getLayout()).setHgap(4);
	}
	else
	{
	    for(int i = 0; i < moreCommands.length; i++)
	    {
		//moreActions[i].setEnabled(false);
                JMenuItem mi = new JMenuItem();
		mi.setAction(moreActions[i]);
		moreMenu.add(mi);
                                
		JButton jb = new JButton(moreActions[i]);
		jb.setAction(moreActions[i]);
//		moreCommandsJPanel.add(jb);
                
                UtilityFunctions.decreaseFontSize(jb, 3);
	    }
	    
//	    ((GridLayout) moreCommandsJPanel.getLayout()).setColumns(moreCommands.length);
//	    ((GridLayout) moreCommandsJPanel.getLayout()).setHgap(4);
	}
        
        annotationJPopupMenu.add(arg1Menu);
        annotationJPopupMenu.add(arg2Menu);
        annotationJPopupMenu.add(moreMenu);

        MouseListener popupListener = new PopupListener(annotationJPopupMenu);
	textJTextArea.addMouseListener(popupListener);

        JPopupMenu treePopupMenu = connectiveInfoTreeJPanel.getJPopupMenu();
        
        for(int i = 0; i < moreCommands.length; i++)
        {
            //moreActions[i].setEnabled(false);
            JMenuItem mi = new JMenuItem();
            mi.setAction(moreActions[i]);
            
            treePopupMenu.add(mi);
        }
                
//        showCommandButtons(true);
    }

//    public SanchayTreeJPanel getTreeJPanel()
//    {
//        return sanchayTreeJPanel;
//    }
//
//    public JPopupMenu getPopupMenu()
//    {
//        return sanchayTreeJPanel.getJPopupMenu();
//        //return annotationJPopupMenu;
//    }
//
//    public JMenu getMorePopupMenu()
//    {
////        return (JMenu) sanchayTreeJPanel.getJPopupMenu().getComponent(0);
//        return moreMenu;
//    }
//    
//    public JPanel getConnectiveInfoJPanel()
//    {
//        return connectiveInfoJPanel;
//    }
//    
    public void setTextEditorJPanel(TextEditorJPanel textEditorJPanel)
    {
        this.textEditorJPanel = textEditorJPanel;
        textJTextArea = textEditorJPanel.textJTextArea;
        logJTextArea = textEditorJPanel.logJTextArea;
    }
//    
//    public void setInfo(AnnotationInfo info, AnnotationInfo child)
//    {
//        this.info = info;
//        this.child = child;
//    }

    //added by Rakesh
    private void initConnectiveInfoTable()
    {         
        String connectiveInfo[] = new String[]{"Connective", "Part of Connective", "Type", "Head", "Connective Info", "AttribSpan",
                "Arguement 1", "Part of Arg1", "Arguement 2", "Part of Arg2", "Arguement 1 Info", "AttribSpanArg1", "Arguement 2 Info", "AttribSpanArg2",
                "Supplement 1", "Supplement 2"};
//                "Source", "SourceArg1", "SourceArg2", "Factuality", "FactualityArg1", "FactualityArg2", "Polarity", "PolarityArg1", "PolarityArg2",
//                "Option 1", "Option 2", "Supplement 1 Info", "Supplement 2 Info", "Type"};

        Object connectiveInfoValues[] = new String[]{"","","","","", "", "", "", "", "", "", "", "", "", "", ""};
//                "", "", "", "", "","","","","","","","","",""};
        
        for (int i = 0; i < connectiveInfo.length; i++) {
            String infor = connectiveInfo[i];
            Object infoValue = connectiveInfoValues[i];

            connectiveInfoTableModel.addRow();
            connectiveInfoTableModel.setValueAt(infor, i, 0);
            connectiveInfoTableModel.setValueAt(infoValue, i, 1);
        }
    }
    
    //Added by Rakesh
    private void addInfoTable(String str, String selectedoption)
    {
        if(str == null || str.equals("null"))
            str = "";
        
        if(selectedoption.compareTo("Root")==0 || selectedoption.compareTo("Connective")==0 )
        {
            connectiveInfoTableModel.setValueAt(str, 0, 1);
            connectiveInfoTableModel.setValueAt(str, 1, 1);
            connectiveInfoTableModel.setValueAt(str, 2, 1);
            connectiveInfoTableModel.setValueAt(str, 3, 1);
            connectiveInfoTableModel.setValueAt(str, 4, 1);
            connectiveInfoTableModel.setValueAt(str, 5, 1);
            connectiveInfoTableModel.setValueAt(str, 6, 1);
            connectiveInfoTableModel.setValueAt(str, 7, 1);
            connectiveInfoTableModel.setValueAt(str, 8, 1);
            connectiveInfoTableModel.setValueAt(str, 8, 1);
            connectiveInfoTableModel.setValueAt(str, 9, 1);
            connectiveInfoTableModel.setValueAt(str, 10, 1);
            connectiveInfoTableModel.setValueAt(str, 11, 1);
            connectiveInfoTableModel.setValueAt(str, 12, 1);
            connectiveInfoTableModel.setValueAt(str, 13, 1);
            connectiveInfoTableModel.setValueAt(str, 14, 1);
            connectiveInfoTableModel.setValueAt(str, 15, 1);
//            connectiveInfoTableModel.setValueAt(str, 16, 1);
//            connectiveInfoTableModel.setValueAt(str, 17, 1);
//            connectiveInfoTableModel.setValueAt(str, 18, 1);
//            connectiveInfoTableModel.setValueAt(str, 19, 1);
//            connectiveInfoTableModel.setValueAt(str, 20, 1);
//            connectiveInfoTableModel.setValueAt(str, 21, 1);
//            connectiveInfoTableModel.setValueAt(str, 22, 1);
//            connectiveInfoTableModel.setValueAt(str, 23, 1);
//            connectiveInfoTableModel.setValueAt(str, 24, 1);
//            connectiveInfoTableModel.setValueAt(str, 25, 1);
//            connectiveInfoTableModel.setValueAt(str, 26, 1);
//            connectiveInfoTableModel.setValueAt(str, 27, 1);
        }

        if(selectedoption.compareTo("Connective")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 0, 1);
        }
        if(selectedoption.compareTo("PartCon")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 1, 1);
        }
        if(selectedoption.compareTo("ConType")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 2, 1);
        }
        if(selectedoption.compareTo("HeadCon")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 3, 1);
        }
        if(selectedoption.compareTo("ConInfo")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 4, 1);
        }
        if(selectedoption.compareTo("AttribSpan")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 5, 1);
        }
        if(selectedoption.compareTo("Arg1")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 6, 1);
        }
        if(selectedoption.compareTo("PartArg1")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 7, 1);
        }
        if(selectedoption.compareTo("Arg2")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 8, 1);
        }
        if(selectedoption.compareTo("PartArg2")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 9, 1);
        }
        if(selectedoption.compareTo("Arg1Info")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 10, 1);
        }
        if(selectedoption.compareTo("AttribSpanArg1")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 11, 1);
        }
        if(selectedoption.compareTo("Arg2Info")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 12, 1);
        }
        if(selectedoption.compareTo("AttribSpanArg2")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 13, 1);
        }
//        if(selectedoption.compareTo("SourceCon")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 14, 1);
//        }
//        if(selectedoption.compareTo("SourceArg1")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 15, 1);
//        }
//        if(selectedoption.compareTo("SourceArg2")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 16, 1);
//        }
//        if(selectedoption.compareTo("FactualityCon")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 17, 1);
//        }
//        if(selectedoption.compareTo("FactualityArg1")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 18, 1);
//        }
//        if(selectedoption.compareTo("FactualityArg2")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 19, 1);
//        }
//        if(selectedoption.compareTo("PolarityCon")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 20, 1);
//        }
//        if(selectedoption.compareTo("PolarityArg1")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 21, 1);
//        }
//        if(selectedoption.compareTo("PolarityArg2")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 22, 1);
//        }
        
        if(selectedoption.compareTo("RemoveArg1")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 6, 1);
            connectiveInfoTableModel.setValueAt(str, 7, 1);        
            connectiveInfoTableModel.setValueAt(str, 10, 1);
            connectiveInfoTableModel.setValueAt(str, 11, 1);        
            connectiveInfoTableModel.setValueAt(str, 14, 1);        
        }
                
        if(selectedoption.compareTo("RemoveArg2")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 8, 1);
            connectiveInfoTableModel.setValueAt(str, 9, 1);
            connectiveInfoTableModel.setValueAt(str, 12, 1);
            connectiveInfoTableModel.setValueAt(str, 13, 1);
            connectiveInfoTableModel.setValueAt(str, 15, 1);        
       }
        
        if(selectedoption.compareTo("Sup1")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 14, 1);
        }
        
        if(selectedoption.compareTo("RemoveSup1")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 14, 1);
        }
        
        if(selectedoption.compareTo("Sup2")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 15, 1);
        }
        
        if(selectedoption.compareTo("RemoveSup2")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 15, 1);
        }        
        if(selectedoption.compareTo("RemoveConInfo")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 4, 1);
        }
        
        
        if(selectedoption.compareTo("RemoveArg1Info")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 10, 1);
        }
        
        
        if(selectedoption.compareTo("RemoveArg2Info")==0)
        {
            connectiveInfoTableModel.setValueAt(str, 12, 1);
        }
        
//        if(selectedoption.compareTo("Sup1")==0)
//        {
//            connectiveInfoTableModel.setValueAt(str, 11, 1);
//        }
//        
//        if(selectedoption.compareTo("RemoveSup1")==0)
//        {
//        connectiveInfoTableModel.setValueAt(str, 11, 1);
//        }
//        
//        if(selectedoption.compareTo("Sup2")==0)
//        {
//        connectiveInfoTableModel.setValueAt(str, 12, 1);
//        }
//        if(selectedoption.compareTo("RemoveSup2")==0)
//        {
//        connectiveInfoTableModel.setValueAt(str, 12, 1);
//        }
    }   
    
    public void showWindow(String selectedoption)
    {
        JTabbedPane jTabbedPane = textEditorJPanel.getLeftJTabbedPane();
        
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        JPanel buttonJPanel = new JPanel(new GridLayout(1, 0, 4, 0));
        
        buttonJPanel.add(okButton);
        buttonJPanel.add(cancelButton);       
                
        int i = jTabbedPane.getTabCount();
        
        if(i==2)
            jTabbedPane.removeTabAt(i-1);

        if(selectedoption.compareTo("ConInfo")==0)
        {
            panel.add(additionalInfoConTreeJPanel, BorderLayout.CENTER);
            panel.add(buttonJPanel, BorderLayout.SOUTH);

            textEditorJPanel.addJPanelToLeftTabbedPanel("Add Info for Connectives", panel);        
            addInfoDialog = new JDialog(getOwner(), "Add Info for Connectives", true);
            
           
        }
        else
        {
            panel.add(additionalInfoArgSupTreeJPanel, BorderLayout.CENTER);
            panel.add(buttonJPanel, BorderLayout.SOUTH);

            textEditorJPanel.addJPanelToLeftTabbedPanel("Add Info for Arg/Sup", panel);        
            addInfoDialog = new JDialog(getOwner(), "Add Info for Arg/Sup", true);
            
            
        }            
               
        jTabbedPane.setSelectedComponent(panel);

        addInfoDialog.add(panel);
        addInfoDialog.pack();

//        UtilityFunctions.centre(addInfoDialog);

        addInfoDialog.setVisible(true);
        
         return;
    }
    
    public void removeWindow()
    {
//        JTabbedPane jTabbedPane = textEditorJPanel.getLeftJTabbedPane();
//        
//        if(jTabbedPane.getTabCount() > 0 )
//            jTabbedPane.removeTabAt(1);
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)
    {  
        if(addInfoDialog != null)
            addInfoDialog.setVisible(false);
        
        tpath=null;
        removeWindow();
        
        addInfoDialog = null;
    }                                             

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)
    {          
        String path = tpath;
        
        if(addInfoDialog != null)
            addInfoDialog.setVisible(false);
        
       removeWindow();
       
       addInfoDialog = null;
    }    
    //till here
    public void freeze(EventObject e)
    {
        if(textEditorJPanel.textJTextArea.isEditable()==true)
		{
			textEditorJPanel.textJTextArea.setEditable(false);
			//added by Kinshul            
			textEditorJPanel.textJTextArea.setBackground(Color.lightGray);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "The text is already forzen. You can now process it","Attention",JOptionPane.INFORMATION_MESSAGE);
		}
		//added till here
    }
    
    //added by Kinshul
        //highlights text in the text area according to the node selected in the connectiveJTree
    public void highlight(AnnotationInfo child)
    {
        
       // AnnotationInfo object is passed here
        
           //Added by Rakesh
           String arguement1 = new String();
           String suplement1 = new String();
           String arguement2 = new String();
           String suplement2 = new String();
           String part = new String();
           String selection = new String();
           //
           
           
                String story=textJTextArea.getText();
                Highlighter h=textJTextArea.getHighlighter();
		AnnotationInfo larg=null,rarg=null,opt1,opt2;
                String name = child.getName();
                
                selection = "Connective";
                addInfoTable(child.getParent().getName(), selection);    
                
                logJTextArea.append("\nConnective Node : "+child.getName()+"\n");
		for(int i=0;i<child.NumOfChildren();i++)
		{
			AnnotationInfo temp=child.getChild(i);
			if(temp.getFlag()==0)
				larg=temp;
			if(temp.getFlag()==1)
				rarg=temp;
		}
		int a[]=child.getPos();
		if(a!=null)
		{
			this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
			for(int j=0;j<a.length;j=j+2)
			{
				try
				{
					h.addHighlight(a[j],a[j+1],this.painter);
                                        textJTextArea.setCaretPosition(a[j]);
				}catch(BadLocationException ble)
				{
					logJTextArea.append("Error: An exception occured while trying to highlight text\n");
				}
			}
                        
		}
                
               //added by Rakesh
                int p[]=child.getPart();
		if(p!=null)
		{
                        
			this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
			for(int j=0;j<p.length;j=j+2)
			{
                             part = part + story.substring(p[j],p[j+1]);
				try
				{
					h.addHighlight(p[j],p[j+1],this.painter);
                                        textJTextArea.setCaretPosition(p[j]);
				}catch(BadLocationException ble)
				{
					logJTextArea.append("Error: An exception occured while trying to highlight text\n");
				}
			}
                     selection = "PartCon";
                    addInfoTable(part, selection);    
		}
                else
                {
                    selection = "PartCon";
                    addInfoTable("", selection); 
                }
                
                selection = "ConType";
                String type=child.getType();
                addInfoTable(type, selection);
                
                if((type.compareTo("null")==0)|| (type.compareTo("Explicit")==0) || (type.compareTo("Implicit")==0) || (type.compareTo("AltLex")==0))
                    for(int i = 0; i < actions.length; i++)
                        actions[i].setEnabled(true); 
                else
                {
//                    actions[15].setEnabled(false);
//                    actions[16].setEnabled(false);
//                    actions[18].setEnabled(false);
//                    actions[19].setEnabled(false);
//                    actions[20].setEnabled(false);
//                    actions[21].setEnabled(false);
//                    actions[22].setEnabled(false);
//                    actions[23].setEnabled(false);
//                    actions[24].setEnabled(false);
//                    actions[25].setEnabled(false);
//                    actions[26].setEnabled(false);
//                    actions[27].setEnabled(false);
                }
                    
                
//                if(child.getSource()!= null)
//                {
//                   logJTextArea.append("Source of connective : "+child.getSource()+"\n");
//                    selection = "SourceCon";
//                    addInfoTable(child.getSource(), selection); 
//                    
//                }
//                else
//                {
//                    selection = "SourceCon";
//                    addInfoTable("", selection);
//                }
//                if(child.getFactuality()!= null)
//                {
//                   logJTextArea.append("Factuality for connective : "+child.getFactuality()+"\n");
//                    selection = "FactualityCon";
//                    addInfoTable(child.getFactuality(), selection); 
//                    
//                }
//                else
//                {
//                    selection = "FactualityCon";
//                    addInfoTable("", selection);
//                }
//                if(child.getPolarity()!= null)
//                {
//                   logJTextArea.append("Polarity for connective: "+child.getPolarity()+"\n");
//                    selection = "PolarityCon";
//                    addInfoTable(child.getPolarity(), selection); 
//                    
//                }
//                else
//                {
//                    selection = "PolarityCon";
//                    addInfoTable("", selection);
//                }
                if(child.getHead()!= null)
                {
                   logJTextArea.append("Connective Head : "+child.getHead()+"\n");
                    selection = "HeadCon";
                    addInfoTable(child.getHead(), selection); 
                    
                }
                else
                {
                    selection = "HeadCon";
                    addInfoTable(child.getParent().getName(), selection);
                }
                
                //till here
                
                if(child.getoptInfo()!=null)
                {
                    logJTextArea.append("Connective Optional tags : "+child.getoptInfo()+"\n");
                    
                    //Added by Rakesh    
                    selection = "ConInfo";
                    addInfoTable(child.getoptInfo(), selection);
                  //
                
                    int attribSpan[] = child.getAttribSpan();
                    if(attribSpan != null)
                    {
                        String attribSpanStr = "";
                        
                        try {
                            attribSpanStr = textJTextArea.getText(attribSpan[0], attribSpan[1] - attribSpan[0]);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                        
                        selection = "AttribSpan";
                        addInfoTable(attribSpanStr, selection);    
                    }
                }
                else
                {
                   selection = "ConInfo";
                   addInfoTable("", selection);
                   
                    selection = "AttribSpan";
                    addInfoTable("", selection);    
                }
                
                
		if(larg!=null)
		{
			int b[]=larg.getPos(),c[]=larg.getsupInfo(),p1[]=larg.getPart();
			if(b!=null)
                        {
                               logJTextArea.append("Argument 1 : ");
				this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
				for(int j=0;j<b.length;j=j+2)
				{
                                        logJTextArea.append(story.substring(b[j],b[j+1]));
                                        //Added by Rakesh
                                        arguement1 = arguement1 + story.substring(b[j],b[j+1]);
                                        //
					try
					{
						h.addHighlight(b[j],b[j+1],this.painter);
					}catch(BadLocationException ble)
					{
						logJTextArea.append("Error: An exception occured while trying to highlight text\n");
					}
				}
                                
                                //Added by Rakesh
                                selection = "Arg1";
                                addInfoTable(arguement1, selection);
                               //
                                
                                logJTextArea.append("\n");
                                String temp=larg.getoptInfo();
                                if(temp!=null)
                                {
                                    int index=temp.indexOf("|");
                                    if(index==-1)
                                    {
                                       logJTextArea.append("Optional Tags for Argument 1 : "+temp+"\n");
                                      
                                       selection = "Arg1Info";
                                       addInfoTable(temp, selection);
                                       
                                        int attribSpan[] = larg.getAttribSpan();
                                        if(attribSpan != null)
                                        {
                                            String attribSpanStr = "";

                                            try {
                                                attribSpanStr = textJTextArea.getText(attribSpan[0], attribSpan[1] - attribSpan[0]);
                                            } catch (BadLocationException ex) {
                                                ex.printStackTrace();
                                            }

                                            selection = "AttribSpanArg1";
                                            addInfoTable(attribSpanStr, selection);    
                                        }
                                        
                                       selection = "Sup1";
                                       addInfoTable("", selection);
                                    }
                                    else
                                    {
                                        if(index==0)
                                        {
                                            logJTextArea.append("Optional Tags for Supplement 1 : "+temp.substring(1)+"\n");
                                            selection = "Arg1Info";
                                            addInfoTable(" ", selection);
                                            
                                            selection = "AttribSpanArg1";
                                            addInfoTable("", selection);    
                                       
                                           selection = "Sup1";
                                           addInfoTable(temp.substring(1), selection);
                                        }
                                        else
                                        {
                                          logJTextArea.append("Optional Tags for Argument 1 : "+temp.substring(0,index)+"\nOptional Tags for Supplement 1 : "+temp.substring(index+1)+"\n");
                                          
                                          selection = "Arg1Info";
                                          addInfoTable(temp.substring(0,index), selection);
                                                                                   
                                          selection = "AttribSpanArg1";
                                          addInfoTable("", selection);    
                                       
                                          selection = "Sup1";
                                          addInfoTable(temp.substring(index+1), selection);
                                        }
                                    }
                                    
                                }
                                else
                                {
                                    selection = "Arg1Info";
                                    addInfoTable(" ", selection);
                                            
                                    selection = "AttribSpanArg1";
                                    addInfoTable("", selection);    
                                       
                                    selection = "Sup1";
                                    addInfoTable(" ", selection);
                                }
			}
                        else//Added by Rakesh
                        {
                                selection = "Arg1";
                                addInfoTable("", selection);
                        }
			if(c!=null)
			{
                                logJTextArea.append(GlobalProperties.getIntlString("Suplement_for_argument_1_:_"));
				this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.MAGENTA);
                                
                               
                                
				for(int j=0;j<c.length;j=j+2)
				{
                                        logJTextArea.append(story.substring(c[j],c[j+1]));
                                        //Added by Rakesh
                                        suplement1 = suplement1 + story.substring(c[j],c[j+1]);
                                        //
					try
					{
						h.addHighlight(c[j],c[j+1],this.painter);
					}catch(BadLocationException ble)
					{
						logJTextArea.append("Error: An exception occured while trying to highlight text\n");
					}
				}
                                //Added by Rakesh
                                selection = "Sup1";
                                addInfoTable(suplement1, selection);
                               //
                                
                                logJTextArea.append("\n");
			}
                        else//Added by Rakesh
                        {
                         selection = "Sup1";
                         addInfoTable("", selection);
                       }
                        //part
                        if(p1!=null)
			{
                                String part1 = new String();
                                logJTextArea.append(GlobalProperties.getIntlString("Part_for_argument_1_:_"));
				this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
                        	for(int j=0;j<p1.length;j=j+2)
				{
                                        logJTextArea.append(story.substring(p1[j],p1[j+1]));
                                        //Added by Rakesh
                                        part1 = part1 + story.substring(p1[j],p1[j+1]);
                                        //
					try
					{
						h.addHighlight(p1[j],p1[j+1],this.painter);
					}catch(BadLocationException ble)
					{
						logJTextArea.append("Error: An exception occured while trying to highlight text\n");
					}
				}
                                //Added by Rakesh
                                selection = GlobalProperties.getIntlString("PartArg1");
                                addInfoTable(part1, selection);
                               //
                                
                                logJTextArea.append("\n");
			}
                        else//Added by Rakesh
                        {
                         selection = "PartArg1";
                         addInfoTable("", selection);
                        }
//                        selection = "SourceArg1";
//                        addInfoTable(larg.getSource(), selection);
//                        
//                        selection = "FactualityArg1";
//                        addInfoTable(larg.getFactuality(),selection);
//                        
//                        selection = "PolarityArg1";
//                        addInfoTable(larg.getPolarity(),selection);
		}
                
                else
                {
                    selection = "Arg1";
                    addInfoTable("", selection);
                                
                    selection = "PartArg1";
                    addInfoTable("", selection);
                    
                    selection = "Sup1";
                    addInfoTable("", selection);
                    
                    selection = "Arg1Info";
                    addInfoTable("", selection);
                    
                    selection = "AttribSpanArg1";
                    addInfoTable("", selection);
                                       
                    selection = "Sup1";
                    addInfoTable("", selection);
                    
//                    selection = "SourceArg1";
//                    addInfoTable("", selection);
//                    
//                    selection = "FactualityArg1";
//                    addInfoTable("", selection);
//                    
//                    selection = "PolarityArg1";
//                    addInfoTable("", selection);
                    
                }
                //
                
		if(rarg!=null)
		{
			int d[]=rarg.getPos(),e[]=rarg.getsupInfo(),p2[]=rarg.getPart();
			if(d!=null)
			{
                                logJTextArea.append(GlobalProperties.getIntlString("Argument_2_:_"));
				this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
				for(int j=0;j<d.length;j=j+2)
				{
                                        logJTextArea.append(story.substring(d[j],d[j+1]));
                                       //Added by Rakesh
                                        arguement2 = arguement2 + story.substring(d[j],d[j+1]);
					try
					{
						h.addHighlight(d[j],d[j+1],this.painter);
					}catch(BadLocationException ble)
					{
						logJTextArea.append(GlobalProperties.getIntlString("Error:_An_exception_occured_while_trying_to_highlight_text\n"));
					}
				}
                                
                                 //Added by Rakesh
                               selection = "Arg2";
                                addInfoTable(arguement2, selection);
                               //
                                
                                logJTextArea.append("\n");
                                String temp=rarg.getoptInfo();
                                if(temp!=null)
                                {
                                    int index=temp.indexOf("|");
                                    if(index==-1)
                                    {
                                       logJTextArea.append(GlobalProperties.getIntlString("Optional_Tags_for_Argument_2_:_")+temp+"\n");
                                       selection = "Arg2Info";
                                       addInfoTable(temp, selection);
                                       
                                        int attribSpan[] = rarg.getAttribSpan();
                                        if(attribSpan != null)
                                        {
                                            String attribSpanStr = "";

                                            try {
                                                attribSpanStr = textJTextArea.getText(attribSpan[0], attribSpan[1] - attribSpan[0]);
                                            } catch (BadLocationException ex) {
                                                ex.printStackTrace();
                                            }

                                            selection = "AttribSpanArg2";
                                            addInfoTable(attribSpanStr, selection);    
                                        }
                                    
                                       selection = "Sup2";
                                       addInfoTable("", selection);
                                    }
                                    else
                                    {
                                        if(index==0)
                                        {
                                            logJTextArea.append(GlobalProperties.getIntlString("Optional_Tags_for_Supplement_2_:_")+temp.substring(1)+"\n");
                                            
                                            selection = "Arg2Info";
                                            addInfoTable("", selection);
                                            
                                            selection = "AttribSpanArg2";
                                            addInfoTable("", selection);    
                                    
                                             selection = "Sup2";
                                             addInfoTable(temp.substring(1), selection);
                                        }
                                        else
                                        {
                                            logJTextArea.append(GlobalProperties.getIntlString("Optional_Tags_for_Argument_2_:_")+temp.substring(0,index)+GlobalProperties.getIntlString("\nOptional_Tags_for_Supplement_2_:_")+temp.substring(index+1)+"\n");
                                            
                                            selection = "Arg2Info";
                                            addInfoTable(temp.substring(0,index), selection);
                                                                           
                                            selection = "AttribSpanArg2";
                                            addInfoTable("", selection);    
                                            
                                           selection = "Sup2";
                                           addInfoTable(temp.substring(index+1), selection);
                                        }
                                    }    
                                }
                                else
                                {
                                    selection = "Arg2Info";
                                    addInfoTable("", selection);
                                            
                                    selection = "AttribSpanArg2";
                                    addInfoTable("", selection);    
                                       
                                    selection = "Sup2";
                                    addInfoTable("", selection);
                                }
			}
                        else
                        {
                            //Added by Rakesh
                               selection = "Arg2";
                               addInfoTable("", selection);
                               //
                        }
			if(e!=null)
			{
                               logJTextArea.append(GlobalProperties.getIntlString("Suplementary_Information_for_argument_2_:_"));
				this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
				for(int j=0;j<e.length;j=j+2)
				{
                                        logJTextArea.append(story.substring(e[j],e[j+1]));
                                        //Added by Rakesh
                                        suplement2 = suplement2 + story.substring(e[j],e[j+1]);
					try
					{
						h.addHighlight(e[j],e[j+1],this.painter);
					}catch(BadLocationException ble)
					{
						logJTextArea.append(GlobalProperties.getIntlString("Error:_An_exception_occured_while_trying_to_highlight_text\n"));
					}
				}
                                
                                //Added by Rakesh
                               selection = "Sup2";
                                addInfoTable(suplement2, selection);
                               //
                                logJTextArea.append("\n");
			}
                        else 
                        {
                            //Added by Rakesh
                              selection = "Sup2";
                              addInfoTable("", selection);
                        }
                        //part
                        if(p2!=null)
			{
                                String part2 = new String();
                                logJTextArea.append(GlobalProperties.getIntlString("Part_for_argument_2_:_"));
				this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE);
				for(int j=0;j<p2.length;j=j+2)
				{
                                        logJTextArea.append(story.substring(p2[j],p2[j+1]));
                                        //Added by Rakesh
                                        part2 = part2 + story.substring(p2[j],p2[j+1]);
					try
					{
						h.addHighlight(p2[j],p2[j+1],this.painter);
					}catch(BadLocationException ble)
					{
						logJTextArea.append(GlobalProperties.getIntlString("Error:_An_exception_occured_while_trying_to_highlight_text\n"));
					}
				}
                                
                                //Added by Rakesh
                                selection = "PartArg2";
                                addInfoTable(part2, selection);
                               //
                                logJTextArea.append("\n");
			}
                        else 
                        {
                            //Added by Rakesh
                              selection = "PartArg2";
                              addInfoTable("", selection);
                        }
                        //
//                        selection = "SourceArg2";
//                        addInfoTable(rarg.getSource(), selection);
//                        
//                        selection = "FactualityArg2";
//                        addInfoTable(rarg.getFactuality(),selection);
//                        
//                        selection = "PolarityArg2";
//                        addInfoTable(rarg.getPolarity(),selection);
		}
                else
                {
                    selection = "Arg2";
                    addInfoTable("", selection);
                                
                    selection = "PartArg2";
                    addInfoTable("", selection); 
                    
                    selection = "Sup2";
                    addInfoTable("", selection); 
                    
                    selection = "Arg2Info";
                    addInfoTable("", selection);
                     
                    selection = "AttribSpanArg2";
                    addInfoTable("", selection);
                                      
                    selection = "Sup2";
                    addInfoTable("", selection);
                   
//                    selection = "SourceArg2";
//                    addInfoTable("", selection);
//                    
//                    selection = "FactualityArg2";
//                    addInfoTable("", selection);
//                    
//                    selection = "PolarityArg2";
//                    addInfoTable("", selection);
                                
                    
                }
	}

    
    public void newFile(EventObject e)
    {
        textEditorJPanel.newFile(e);
        
      //  textJTextArea.setEditable(true);
        textJTextArea.setBackground(Color.WHITE);
        int noofchild=connectiveJRoot.getChildCount();
              //addedhere
        for(int i=0;i<noofchild;i++)
        {
                        SSFPhrase kid=(SSFPhrase)connectiveJRoot.getChildAt(0);
                        connectiveJTreeModel.removeNodeFromParent((SSFPhrase)kid);
        }
        info=new AnnotationInfo("Story");
        
        String selection = "Root";
        addInfoTable("", selection);
    }
    
    public String getTitle()
    {
            return textEditorJPanel.getTitle();
    }

    public void setTitle(String t)
    {
            title = t;
            textEditorJPanel.setTitle(t);
    }
    
    public void open(EventObject e)
    {
//        textEditorJPanel.open(e);       
	closeFile(e);

	try
	{
		String path = null;

		if(textFile != null)
		{
			File tfile = new File(textFile);

			if(tfile.exists() && tfile.getParentFile() != null)
				path = tfile.getParentFile().getAbsolutePath();
			else
				path = stateKVProps.getPropertyValue("CurrentDir");
		}

		JFileChooser chooser = null;

		if(path != null)
			chooser = new JFileChooser(path);
		else
			chooser = new JFileChooser();

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			textFile = chooser.getSelectedFile().getAbsolutePath();
			//		charset = JOptionPane.showInputDialog(this, "Please enter the charset:", "UTF-8");

			displayFile(textFile, charset, e);
		}
	}
	catch(Exception ex)
	{
		JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error_is_in_this_opening_file."), "Error", JOptionPane.ERROR_MESSAGE);
		ex.printStackTrace();
	}   
    }
    
    private void initDocument()
    {
        textEditorJPanel.initDocument();
        
      //addedhere
        int noofchild=connectiveJRoot.getChildCount();
        
        for(int i=0;i<noofchild;i++){
                SSFNode childNode =(SSFNode)connectiveJRoot.getChildAt(0);
                connectiveJRoot.removeChild(0);
//                connectiveJTreeModel.removeNodeFromParent((MutableTreeNode)childNode);
        }
        info=new AnnotationInfo("Story");
        fileinitopened=textFile; //added by nrapesh
        xmlcheck(textFile);     //added by nrapesh        
        
        connectiveJTreeModel.reload();
    }
    
    public void displayFile(File file, String charset, EventObject e)
    {
            if(file.isFile() == false || file.exists() == false)
                    return;

            try{
                    //	textJTextArea.setText("");

                    initDocument();
                    doc = textEditorJPanel.getDocument();
                    
                    textFile = file.getAbsolutePath();
//                    int noofchild=connectiveJRoot.getChildCount();
//                  //addedhere
//                    for(int i=0;i<noofchild;i++){
//                            SSFNode childNode =(SSFNode)connectiveJRoot.getChildAt(0);
//                            connectiveJTreeModel.removeNodeFromParent((MutableTreeNode)childNode);
//                    }
//                    info=new AnnotationInfo("Story");
//                    fileinitopened=textFile; //added by nrapesh
//                    xmlcheck(textFile);     //added by nrapesh
                    this.charset = charset;

                    BufferedReader lnReader = null;

                    try
                    {
                            if(!charset.equals(""))
                                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), charset));
                            else
                                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), GlobalProperties.getIntlString("UTF-8")));

                            sanchayBackup = new SanchayBackup();
                            FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
                            logJTextArea.append("File " + textFile + " backed up.\n");

                            String line = "";

                            Element root = doc.getDefaultRootElement();
                            while((line = lnReader.readLine()) != null)
                            {
                                    doc.insertString(root.getEndOffset() - 1, line + "\n", null);
                                    //textJTextArea.append(line + "\n");
                            }

                            //		if(textJTextArea.getText().length() > 0)
                            if(doc.getLength() > 0)
                                    textJTextArea.setCaretPosition(0);
                    }
                    catch (UnsupportedEncodingException ex)
                    {
                            ex.printStackTrace();
                    } catch (FileNotFoundException ex)
                    {
                            ex.printStackTrace();
                    } catch (IOException ex)
                    {
                            ex.printStackTrace();
                    }

                    dirty = false;

                    undo.discardAllEdits();
                    undoAction.updateUndoState();
                    redoAction.updateRedoState();

                    textEditorJPanel.setTitle(title);
            }
            catch(BadLocationException ex)
            {
                    ex.printStackTrace();
            }
            textJTextArea.setEditable(false);                //added by nrapesh
            textJTextArea.setBackground(Color.LIGHT_GRAY);   //added by nrapesh  
    }

    public void displayFile(String path, String charset, EventObject e)
    {
            if(path == null || path.equals(""))
                    return;

            displayFile(new File(path), charset, e);
    }

    public void displayFile(EventObject e)
    {
        if(e instanceof DisplayEvent)
        {
            DisplayEvent de = (DisplayEvent) e;
            displayFile(de.getFilePath(), de.getCharset(), e);
        }
    }
    
    public boolean isDirty()
    {
        return (dirty && textEditorJPanel.isDirty());
    }

    private static void saveState(DiscourseArgOptionalJPanel editorInstance)
    {
            File tf = new File(editorInstance.textFile);
            String cd = "";

            if(tf.exists())
            {
                    cd = tf.getParent();    
                    stateKVProps.addProperty("CurrentDir", cd);
            }

            try {
                    stateKVProps.save();
            } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
            } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
            }
    }

    private static void loadState(DiscourseArgOptionalJPanel editorInstance)
    {
            if(stateKVProps == null)
            {
                    File sf = new File(statePropFile);

                    if(sf.exists() == false)

                            try {
                                    sf.createNewFile();
                            } catch (IOException ex) {
                                    ex.printStackTrace();
                            }

                    try {
                            stateKVProps = new KeyValueProperties(statePropFile, GlobalProperties.getIntlString("UTF-8"));
                    } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                    } catch (IOException ex) {
                            ex.printStackTrace();
                    }
            }
    }
    
    public boolean closeFile(EventObject e)
    {
        textFile = "";
        
       // textJTextArea.setEditable(true);
        textJTextArea.setBackground(Color.WHITE);
        
        String selectedoption = GlobalProperties.getIntlString("Connective");
        addInfoTable(GlobalProperties.getIntlString("_"), selectedoption);
        
        
	if(dirty)
	{
		int retVal = -1;

                retVal = JOptionPane.showConfirmDialog(this, GlobalProperties.getIntlString("The_file_") + textFile + GlobalProperties.getIntlString("_has_been_modified.\n\nDo_you_want_to_save_the_file?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

		if(retVal == JOptionPane.NO_OPTION)
		{
			initDocument();
			return false;
		}
		else
		{
			save(e);
			initDocument();
			return true;
		}
	}
	else
		initDocument();

	if(sanchayBackup != null)
		FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);

	return true;
    }
    
    public boolean save(EventObject e)
    {
	int pos = textJTextArea.getCaretPosition();

	if(textFile.equals(GlobalProperties.getIntlString("Untitled")) == false || (new File(textFile)).exists())
	{            
		PrintStream ps = null;

		try
		{
			ps = new PrintStream(textFile, charset);
		} catch (UnsupportedEncodingException ex)
		{
			ex.printStackTrace();
		} catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
		}

		textEditorJPanel.print(ps);
		setTitle(title);

		logJTextArea.append(textFile + GlobalProperties.getIntlString("_saved\n."));

		dirty = false;
                finalwork(fileinitopened);
	}
	else
		saveAs(e);

	textJTextArea.setCaretPosition(pos);

	return true;
    }
    
    public boolean saveAs(EventObject e)
    {
	try
	{
		int pos = textJTextArea.getCaretPosition();

		String path = null;

		//	    System.out.println("Current path: " + textFile);

		if(textFile != null && !textFile.equals("") && !textFile.equals(GlobalProperties.getIntlString("Untitled")))
		{
			File tfile = new File(textFile);

			if(tfile.exists())
			{
				path = tfile.getParentFile().getAbsolutePath();
			}
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
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			textFile = chooser.getSelectedFile().getAbsolutePath();
			//		charset = JOptionPane.showInputDialog(this, "Please enter the charset:", "UTF-8");

			PrintStream ps = new PrintStream(textFile, charset);
			textEditorJPanel.print(ps);
			setTitle(title);

			FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);

			sanchayBackup = new SanchayBackup();
			FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
			logJTextArea.append(GlobalProperties.getIntlString("File_") + textFile + GlobalProperties.getIntlString("_backed_up."));

			dirty = false;
                        finalwork(textFile);//added by nrapesh
		}

		textJTextArea.setCaretPosition(pos);
	}
	catch(Exception ex)
	{
		JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error_opening_file."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
		ex.printStackTrace();
	}
        
        return true;
    }
    
    
    //added by Kinshul
    //generates the right click menu for the conenctiveJTree Area
	private void genMenu(AnnotationInfo child, int level) 
	{
		AnnotationInfo larg=null,rarg=null,opt1,opt2;
		for(int i=0;i<child.NumOfChildren();i++)
		{
			AnnotationInfo temp=child.getChild(i);
			if(temp.getFlag()==0)
				larg=temp;
			if(temp.getFlag()==1)
				rarg=temp;
		} 
//                moreCommands = new boolean[DiscourseAnnotationInterfaceAction._MORE_ACTIONS_];
//	        moreActions = new Action[DiscourseAnnotationInterfaceAction._MORE_ACTIONS_];

                if(level == 1)
                {
                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_CONNECTIVE] = true;
                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_CONNECTIVE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_CONNECTIVE]);

                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INSTANCE] = false;
                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INSTANCE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INSTANCE]);
                }
                else if(level == 2)
                {
                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_CONNECTIVE] = false;
                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_CONNECTIVE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_CONNECTIVE]);

                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INSTANCE] = true;
                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INSTANCE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INSTANCE]);
                
//                moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG1]=true;
//                moreActions[DiscourseAnnotationInterfaceAction.REMOVE_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG1]);
//                
//                moreCommands[11]=true;
//                moreActions[11].setEnabled(moreCommands[11]);
               
//                boolmenuItem[0]=false;
//		menuItem[0].setEnabled(boolmenuItem[0]);
                    if(child.getoptInfo()==null)
                    {    
    //                    boolmenuItem[5]=false;
    //                    menuItem[5].setEnabled(boolmenuItem[5]);
                          moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_CONNECTIVE]=false;
                          moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_CONNECTIVE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_CONNECTIVE]);
                    }
                    else
                    {
    //                    boolmenuItem[5]=true;
    //                    menuItem[5].setEnabled(boolmenuItem[5]);
                          moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_CONNECTIVE]=true;
                          moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_CONNECTIVE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_CONNECTIVE]);
                    }
                    if(child.getPart()==null)
                    {    
    //                    boolmenuItem[5]=false;
    //                    menuItem[5].setEnabled(boolmenuItem[5]);
                          moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_CONNECTIVE]=false;
                          moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_CONNECTIVE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_CONNECTIVE]);
                    }
                    else
                    {
    //                    boolmenuItem[5]=true;
    //                    menuItem[5].setEnabled(boolmenuItem[5]);
                          moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_CONNECTIVE]=true;
                          moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_CONNECTIVE].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_CONNECTIVE]);
                    }
                    if(larg==null)
                    {
    //			boolmenuItem[1]=false;
    //			menuItem[1].setEnabled(boolmenuItem[1]);
    //			boolmenuItem[3]=false;
    //			menuItem[3].setEnabled(boolmenuItem[3]);
    //                        boolmenuItem[6]=false;
    //                        menuItem[6].setEnabled(boolmenuItem[6]);
    //                        boolmenuItem[8]=false;
    //                        menuItem[8].setEnabled(boolmenuItem[8]);
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG1]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG1]);
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]);
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]);
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1]);
    //                        moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_]=false;
    //                        moreActions[13].setEnabled(moreCommands[13]);
                    }
                    else
                    {
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG1]=true;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG1]);

                            if(larg.getsupInfo()==null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]);
                            }
                            if(larg.getsupInfo()!=null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]);
                            }
                            if(larg.getPart()==null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1]);
                            }
                            if(larg.getPart()!=null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG1]);
                            }
                            String temp=larg.getoptInfo();
                            if(temp==null)
                            {
                                moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]=false;
                                moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]);
                                moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]=false;
                                moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]);
                            }
                            else
                            {
                                int index=temp.indexOf("|");
                                if(index==0)
                                {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]);
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]);
                                }
                                if(index==-1)
                                {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]);
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]);
                                }
                                if(index!=0 && index!=-1)
                                {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG1]);
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP1].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP1]);
                                }
                            }
                    }
                    if(rarg==null)
                    {
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG2]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG2]);
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]);
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2]);
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]=false;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]);
    //                        moreCommands[14]=false;
    //			moreActions[14].setEnabled(moreCommands[14]);
                    }
                    else
                    {
                            moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG2]=true;
                            moreActions[DiscourseAnnotationInterfaceAction.REMOVE_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_ARG2]);
                            if(rarg.getsupInfo()==null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]);
                            }
                            if(rarg.getsupInfo()!=null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]);
                            }
                            if(rarg.getPart()==null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2]);
                            }
                            if(rarg.getPart()!=null)
                            {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_PART_ARG2]);
                            }
                            String temp=rarg.getoptInfo();
                            if(temp==null)
                            {
                                moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]=false;
                                moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]);
                                moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]=false;
                                moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]);
                            }
                            else
                            {
                                int index=temp.indexOf("|");
                                if(index==0)
                                {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]);
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]);
                                }
                                if(index==-1)
                                {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]);
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]=false;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]);
                                }
                                if(index!=0 && index!=-1)
                                {
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_INFO_ARG2]);
                                    moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]=true;
                                    moreActions[DiscourseAnnotationInterfaceAction.REMOVE_SUP2].setEnabled(moreCommands[DiscourseAnnotationInterfaceAction.REMOVE_SUP2]);
                                }
                            }
                    }
                }
	}
    
    
    
    public void setHead(EventObject e)
    {
       String selectedoption = new String(GlobalProperties.getIntlString("HeadCon"));
        
        if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
        
        Object[] possibilities = {"then", "तव","Other"};
        String initLine = "then";
        String selection = (String) JOptionPane.showInputDialog(getOwner(),
			GlobalProperties.getIntlString("Enter_the_head_for_connective"), "Head", JOptionPane.INFORMATION_MESSAGE, null,
			possibilities, initLine);
     //   System.out.println(selection);
        if(selection.compareTo("Other")==0)
        {
            selection = (String) JOptionPane.showInputDialog(getOwner(),
                    GlobalProperties.getIntlString("Enter_head"),"Extra Head",JOptionPane.INFORMATION_MESSAGE,null,null,"");
        }
        
        TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
        AnnotationInfo instance = node.getChild(y);
        instance.setHead(selection);
       // System.out.println(instance.getHead());
        
        addInfoTable(selection,selectedoption);
        
        
    }
    
    
    public void partofConnective(EventObject e)
    {
        String selectedoption = new String("PartCon");
        if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String story=textJTextArea.getText();
	int index=textJTextArea.getSelectionStart();
        int off=textJTextArea.getSelectionEnd(),l=off-index;
 //       textEditorJPanel.logJTextArea.append("Start : "+String.valueOf(index)+" End : "+String.valueOf(off)+"\n");
	String name=lastSelect.getUserObject().toString();
	TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
	name=node.getName();
	AnnotationInfo instance=node.getChild(y);
	Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	if(instance.getPart()==null)
	{
		
		int a[]={index,index+l};
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			textEditorJPanel.logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		instance.setPart(a);
		
	}
	else
	{
		int b[]=instance.getPart();
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		int len=b.length;
		int c[]=new int[len+2];
		c[len]=index;
		c[len+1]=index+l;
		for(int i=0;i<b.length;i++)
			c[i]=b[i];
		instance.setPart(c);
              
	}
	logJTextArea.append(GlobalProperties.getIntlString("Finally_selected_arg1_:_"));
	String str = new String();
        for(int i=0;i<instance.getPart().length;i=i+2)
	{
		logJTextArea.append(story.substring(instance.getPart()[i],instance.getPart()[i+1]));
                str = str + story.substring(instance.getPart()[i],instance.getPart()[i+1]);
	}
                logJTextArea.append("\n");
                System.out.println(str);
//        genMenu(instance);
        addInfoTable(str,selectedoption);
    }
    
    //Added by Rakesh
    public void setconType(EventObject e)
    {
        Object[] typePossibilities = {"Explicit","Implicit", "AltLex","EntRel","NoRel"};
        String initType = "Explicit";
        
        String selectedoption = (String) JOptionPane.showInputDialog(getOwner(),
                GlobalProperties.getIntlString("Enter_the_connective_type"), "Type", JOptionPane.INFORMATION_MESSAGE, null,
                typePossibilities, initType);        
        
        if(selectedoption == null)
            return;
        
        if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
        
        String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
        
        AnnotationInfo node;
	AnnotationInfo instance = new AnnotationInfo();
        AnnotationInfo con = new AnnotationInfo();
        int n;
        String name = new String();

        String possibilities[] = {"if","then","other"};
        String initLine = "if";
        String selection = "";
        
        if(selectedoption.equals("Implicit"))
        {        
            selection = (String) JOptionPane.showInputDialog(getOwner(),
                            GlobalProperties.getIntlString("Enter_the_connective"), "Connective", JOptionPane.INFORMATION_MESSAGE, null,
                            possibilities, initLine);
        
            if(selection == null)
                return;

            if(selection.compareTo("other")==0)
            {
                selection = (String) JOptionPane.showInputDialog(getOwner(),
                        GlobalProperties.getIntlString("Enter_Connective"),"Connective",JOptionPane.INFORMATION_MESSAGE,null,null,"");
            }
        }
        else if(selectedoption.equals("Explicit") || selectedoption.equals("AltLex"))
            selection = text;
        else
            selection = selectedoption;
        
        SSFNode connectiveJChild;
        DefaultMutableTreeNode temp = new DefaultMutableTreeNode();
        
        int count=connectiveJTreeModel.getChildCount(connectiveJRoot),j;
   
        for(j=0;j<count;j++)
	{
            temp=(DefaultMutableTreeNode)connectiveJTreeModel.getChild(connectiveJRoot,j);
            String tmp1=temp.toString();//.toLowerCase();

            if(tmp1.compareTo(selection)==0)
            {
                 con = info.getChild(j);
                //  if(con.getType().compareTo(node.getType())==0)
                break;
            }
	}
        
       if(j<count)
       {
          n = con.NumOfChildren()+1;
          name = "Instance"+n;
          instance = new AnnotationInfo(name);
          instance.setType(selectedoption);
          con.addChild(instance);
          instance.setParent(con);
          
          addObject((SSFPhrase)temp,name,true);
          
          
        }
       else if(j>=count)
       {
            connectiveJChild=addObject(connectiveJRoot,selection,true);

            node=new AnnotationInfo(selection);
            
            node.setParent(info);
            info.addChild(node); 
            
            name = "Instance1";
            instance = new AnnotationInfo(name);
            instance.setType(selectedoption);
            node.addChild(instance);
            instance.setParent(node);
          
            addObject((SSFPhrase)connectiveJChild,name,true);
           // connectiveJTreeModel.reload();
         
       }  
      
        connectiveJTreeModel.reload();
	String story=textJTextArea.getText();
        int index=textJTextArea.getSelectionStart(),off=textJTextArea.getSelectionEnd();
        int l=off-index;
        Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
        AnnotationInfo rarg=null;
        rarg=new AnnotationInfo("Arg2",1);
        int a[] = {index,index+l};
	int b[] = {index, index + 1};  //For implicit mode we store in place of position the offset of 1st character of arg2
        
        if(selectedoption.equalsIgnoreCase("Implicit"))
            instance.setPos(b);
        else
            instance.setPos(a); //and its sentence no(now storing 0).	
        
        try
        {
            if(selectedoption.equalsIgnoreCase("Implicit"))
                h.addHighlight(index, index + 1, this.painter);
            else
                h.addHighlight(index, off, this.painter);
        }
        catch(BadLocationException ble)
        {
                logJTextArea.append("Error : Could not highlight text\n");
        }
        
        if(selectedoption.equalsIgnoreCase("Implicit"))
        {
            rarg.setPos(a);
            rarg.setParent(instance);
            instance.addChild(rarg);        
        }
        else if(selectedoption.equalsIgnoreCase("Explicit") == false)
        {
            rarg.setPos(a);
            rarg.setParent(instance);
            instance.addChild(rarg);        
        }
    }
    //Added by Rakesh
//    public void source(EventObject e,String selectedoption )
//    {
//      //selectedoption = new String("SourceCon");
//        
//        if(textJTextArea.isEditable()==true)
//	{
//		JOptionPane.showMessageDialog(this,"In order to process the text you need to freeze it","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return; 
//	}
//	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
//	{
//		JOptionPane.showMessageDialog(this,"You must choose an instance of a connective in order to set its arguments","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return;
//	}
//        
//        TreeNode parent=Lastselect.getParent();
//	TreeNode root=parent.getParent();
//	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
//	AnnotationInfo node=info.getChild(x);
//        AnnotationInfo instance = node.getChild(y);
//        
//        for(int i=0;i<instance.NumOfChildren();i++)
//	{
//		AnnotationInfo temp=instance.getChild(i);
//		if(temp.getFlag()==0)
//	          larg=temp;
//                else
//                  rarg=temp;
//	}
//	
//	if(larg==null && selectedoption.compareTo("SourceArg1")==0)
//	{
//		JOptionPane.showMessageDialog(this,"First set argument in order to set information of source about it","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return;
//	}
//        if(rarg==null && selectedoption.compareTo("SourceArg2")==0)
//        {
//            JOptionPane.showMessageDialog(this,"First set argument in order to set information of source about it","Attention",JOptionPane.INFORMATION_MESSAGE);
//	    return;
//        }
//        
//        String[] possibilities;
//        if(selectedoption.equals("SourceArg1") || selectedoption.equals("SourceArg2"))
//        {
//             possibilities = new String[] {"Writer", "Other","Inherited"};
//        }
//        else
//        {
//             possibilities = new String[] {"Writer", "Other"};
//        }
//        String initLine = "Writer";
//        String selection = (String) JOptionPane.showInputDialog(getOwner(),
//			"Enter the source", "Source", JOptionPane.INFORMATION_MESSAGE, null,
//			possibilities, initLine);
//   
//        if(selectedoption.compareTo("SourceCon")==0)
//        instance.setSource(selection);
//        
//        else if(selectedoption.compareTo("SourceArg1")==0)
//             //instance.setarg1Source(selection);
//        {
//            larg.setSource(selection);
//            System.out.print(larg.getSource());
//        }
//        
//     
//         else if(selectedoption.compareTo("SourceArg2")==0)
//       // instance.setarg2Source(selection);
//         {
//             rarg.setSource(selection);
//             System.out.print(rarg.getSource());
//         }
//        
//        addInfoTable(selection,selectedoption);
//        
//        
//    }
//    //Added by Rakesh
//    public void factuality(EventObject e, String selectedoption)
//    {
//       //selectedoption = new String("FactualityCon");
//        
//        if(textJTextArea.isEditable()==true)
//	{
//		JOptionPane.showMessageDialog(this,"In order to process the text you need to freeze it","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return; 
//	}
//	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
//	{
//		JOptionPane.showMessageDialog(this,"You must choose an instance of a connective in order to set its arguments","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return;
//	}
//        TreeNode parent=Lastselect.getParent();
//	TreeNode root=parent.getParent();
//	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
//	AnnotationInfo node=info.getChild(x);
//        AnnotationInfo instance = node.getChild(y);
//        
//        for(int i=0;i<instance.NumOfChildren();i++)
//	{
//		AnnotationInfo temp=instance.getChild(i);
//		if(temp.getFlag()==0)
//	          larg=temp;
//                else
//                  rarg=temp;
//	}
//	
//	if(larg==null && selectedoption.compareTo("FactualityArg1")==0)
//	{
//		JOptionPane.showMessageDialog(this,"First set argument in order to set information of source about it","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return;
//	}
//        if(rarg==null && selectedoption.compareTo("FactualityArg2")==0)
//        {
//            JOptionPane.showMessageDialog(this,"First set argument in order to set information of source about it","Attention",JOptionPane.INFORMATION_MESSAGE);
//	    return;
//        }
//        
//        String[] possibilities;
//        
//        if(selectedoption.equals("FactualityArg1") || selectedoption.equals("FactualityArg2"))
//        {
//             possibilities = new String[] {"Factual", "Non-Factual","Inherited"};
//        }
//        else
//        {
//             possibilities = new String[] {"Factual", "Non-Factual"};
//        }
//        
//       // Object[] possibilities = {"Factual", "Non-Factual","Inherited"};
//        String initLine = "Factual";
//        String selection = (String) JOptionPane.showInputDialog(getOwner(),
//			"Enter the factuality ", "Factuality", JOptionPane.INFORMATION_MESSAGE, null,possibilities, initLine);
//        
//        if(selectedoption.compareTo("FactualityCon")==0)
//        instance.setFactuality(selection);
//        
//        else if(selectedoption.compareTo("FactualityArg1")==0)
//      //  instance.setarg1Factuality(selection);
//        {
//            larg.setFactuality(selection);
//            System.out.print(larg.getFactuality());
//        }
//        
//         else if(selectedoption.compareTo("FactualityArg2")==0)
//       // instance.setarg2Factuality(selection);
//         {
//             rarg.setFactuality(selection);
//             System.out.print(rarg.getFactuality());
//         }
//        
//        addInfoTable(selection,selectedoption);
//        
//        
//    }
//    //Added by Rakesh
//    public void polarity(EventObject e,String selectedoption)
//    {
//        //selectedoption = new String("PolarityCon");
//        
//        if(textJTextArea.isEditable()==true)
//	{
//		JOptionPane.showMessageDialog(this,"In order to process the text you need to freeze it","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return; 
//	}
//	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
//	{
//		JOptionPane.showMessageDialog(this,"You must choose an instance of a connective in order to set its arguments","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return;
//	}
//        TreeNode parent=Lastselect.getParent();
//	TreeNode root=parent.getParent();
//	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
//	AnnotationInfo node=info.getChild(x);
//        AnnotationInfo instance = node.getChild(y);
//        
//        for(int i=0;i<instance.NumOfChildren();i++)
//	{
//		AnnotationInfo temp=instance.getChild(i);
//		if(temp.getFlag()==0)
//	          larg=temp;
//                else
//                  rarg=temp;
//	}
//	
//	if(larg==null && selectedoption.compareTo("PolarityArg1")==0)
//	{
//		JOptionPane.showMessageDialog(this,"First set argument in order to set information of source about it","Attention",JOptionPane.INFORMATION_MESSAGE);
//		return;
//	}
//        if(rarg==null && selectedoption.compareTo("PolarityArg2")==0)
//        {
//            JOptionPane.showMessageDialog(this,"First set argument in order to set information of source about it","Attention",JOptionPane.INFORMATION_MESSAGE);
//	    return;
//        }
//        
//        Object[] possibilities = {"Positive", "Negative"};
//        String initLine = "Positive";
//        String selection = (String) JOptionPane.showInputDialog(getOwner(),
//			"Enter the polarity", "Polarity", JOptionPane.INFORMATION_MESSAGE, null,
//			possibilities, initLine);
// 
//        if(selectedoption.compareTo("PolarityCon")==0)
//        instance.setPolarity(selection);
//        
//        else if(selectedoption.compareTo("PolarityArg1")==0)
//        //instance.setarg1Polarity(selection);
//        {
//            larg.setPolarity(selection);
//            System.out.print(larg.getPolarity());
//        }
//      
//        else if(selectedoption.compareTo("PolarityArg2")==0)
//       // instance.setarg2Polarity(selection);
//        {
//            rarg.setPolarity(selection);
//            System.out.print(larg.getPolarity());
//        }
//        
//        addInfoTable(selection,selectedoption);
//        
//        
//    }
       
    public void setArg1(EventObject e)
    {
        String selectedoption = new String("Arg1");
        if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String story=textJTextArea.getText();
	int index=textJTextArea.getSelectionStart();
        int off=textJTextArea.getSelectionEnd(),l=off-index;
 //       textEditorJPanel.logJTextArea.append("Start : "+String.valueOf(index)+" End : "+String.valueOf(off)+"\n");
	String name=lastSelect.getUserObject().toString();
	TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
	name=node.getName();
	AnnotationInfo instance=node.getChild(y);
	AnnotationInfo larg=null;
	for(int i=0;i<instance.NumOfChildren();i++)
	{
		AnnotationInfo temp=instance.getChild(i);
		if(temp.getFlag()==0)
		{
			larg=temp;
			break;
		}
	}
	Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
	if(larg==null)
	{
		larg=new AnnotationInfo("Arg1",0);
		int a[]={index,index+l};
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			textEditorJPanel.logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		larg.setPos(a);
		larg.setParent(instance);
		instance.addChild(larg);
	}
	else
	{
		int b[]=larg.getPos();
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		int len=b.length;
		int c[]=new int[len+2];
		c[len]=index;
		c[len+1]=index+l;
		for(int i=0;i<b.length;i++)
			c[i]=b[i];
		larg.setPos(c);
	}

    logJTextArea.append(GlobalProperties.getIntlString("Finally_selected_arg1_:_"));
	String str = new String();
        for(int i=0;i<larg.getPos().length;i=i+2)
	{
		logJTextArea.append(story.substring(larg.getPos()[i],larg.getPos()[i+1]));
                str = str + story.substring(larg.getPos()[i],larg.getPos()[i+1]);
	}
                logJTextArea.append("\n");
             //   System.out.println(str);
//        genMenu(instance);
        addInfoTable(str,selectedoption);
    }
    //Added by Rakesh
    public void partofArg1(EventObject e)
    {
        String selectedoption = new String("PartArg1");
        if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String story=textJTextArea.getText();
	int index=textJTextArea.getSelectionStart();
        int off=textJTextArea.getSelectionEnd(),l=off-index;
 //       textEditorJPanel.logJTextArea.append("Start : "+String.valueOf(index)+" End : "+String.valueOf(off)+"\n");
	String name=lastSelect.getUserObject().toString();
	TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
	name=node.getName();
	AnnotationInfo instance=node.getChild(y);
	AnnotationInfo larg=null;
	for(int i=0;i<instance.NumOfChildren();i++)
	{
		AnnotationInfo temp=instance.getChild(i);
		if(temp.getFlag()==0)
		{
			larg=temp;
			break;
		}
	}
	Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
	if(larg==null)
	{
            JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("You_must_set_Arg1_in_order_to_set_its_part"),"Alert",JOptionPane.WARNING_MESSAGE);
        }
        else
        {
            int p[] = larg.getPart();
         if(p==null)
             {
                int a[]={index,index+l};
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			textEditorJPanel.logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		larg.setPart(a);
             }
            else
            {
		int b[]=larg.getPart();
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		int len=b.length;
		int c[]=new int[len+2];
		c[len]=index;
		c[len+1]=index+l;
		for(int i=0;i<b.length;i++)
			c[i]=b[i];
		larg.setPart(c);
            }   
        }          
	logJTextArea.append(GlobalProperties.getIntlString("Finally_selected_part_of_arg1_:_"));
	String str = new String();
        for(int i=0;i<larg.getPart().length;i=i+2)
	{
		logJTextArea.append(story.substring(larg.getPart()[i],larg.getPart()[i+1]));
                str = str + story.substring(larg.getPart()[i],larg.getPart()[i+1]);
	}
                logJTextArea.append("\n");
             //   System.out.println(str);
//        genMenu(instance);
        addInfoTable(str,selectedoption);
    }
    
    public void setArg2(EventObject e)
    {
        String selectedoption = new String("Arg2");
         if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String story=textJTextArea.getText();
        int index=textJTextArea.getSelectionStart(),off=textJTextArea.getSelectionEnd();
        int l=off-index;
	String name=lastSelect.getUserObject().toString();
//	logJTextArea.append("Selected Component Name : "+ name+"\n");
	TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
	name=node.getName();
//	logJTextArea.append("Parent Component Name : "+ name+"\n");
	AnnotationInfo instance=node.getChild(y);
	AnnotationInfo rarg=null;
	for(int i=0;i<instance.NumOfChildren();i++)
	{
		AnnotationInfo temp=instance.getChild(i);
		if(temp.getFlag()==1)
		{
			rarg=temp;
			break;
		}
	}
	Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
	if(rarg==null)
	{
		rarg=new AnnotationInfo("Arg2",1);
		int a[]={index,index+l};
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error_:_Could_not_highlight_text\n"));
		}
		rarg.setPos(a);
		rarg.setParent(instance);
		instance.addChild(rarg);
	}
	else
	{
		int b[]=rarg.getPos();
		int len=b.length;
		int c[]=new int[len+2];
		c[len]=index;
		c[len+1]=index+l;
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error_:_Could_not_highlight_text\n"));
		}
		for(int i=0;i<b.length;i++)
			c[i]=b[i];
		rarg.setPos(c);
	}
	logJTextArea.append(GlobalProperties.getIntlString("Finally_selected_arg2_:_"));
        String str = new String();
	for(int i=0;i<rarg.getPos().length;i=i+2)
	{
		logJTextArea.append(story.substring(rarg.getPos()[i],rarg.getPos()[i+1]));
                str = str + story.substring(rarg.getPos()[i],rarg.getPos()[i+1]);
	}
	logJTextArea.append("\n");
//        genMenu(instance);
        addInfoTable(str, selectedoption);
         
    }
    //Added by Rakesh
    public void partofArg2(EventObject e)
    {
         String selectedoption = new String("PartArg2");
         if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String story=textJTextArea.getText();
        int index=textJTextArea.getSelectionStart(),off=textJTextArea.getSelectionEnd();
        int l=off-index;
	String name=lastSelect.getUserObject().toString();
//	logJTextArea.append("Selected Component Name : "+ name+"\n");
	TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
	name=node.getName();
//	logJTextArea.append("Parent Component Name : "+ name+"\n");
	AnnotationInfo instance=node.getChild(y);
	AnnotationInfo rarg=null;
	for(int i=0;i<instance.NumOfChildren();i++)
	{
		AnnotationInfo temp=instance.getChild(i);
		if(temp.getFlag()==1)
		{
			rarg=temp;
			break;
		}
	}
	Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE);
	if(rarg==null)
	{
	  JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("You_must_set_Arg2_in_order_to_set_its_part"),"Alert",JOptionPane.WARNING_MESSAGE);
        }
        else
        {
            int p[]=rarg.getPart();
            if(p==null)
            {  
		int a[]={index,index+l};
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error_:_Could_not_highlight_text\n"));
		}
		rarg.setPart(a);
		
	   }
	      else
	   {
		int b[]=rarg.getPart();
		int len=b.length;
		int c[]=new int[len+2];
		c[len]=index;
		c[len+1]=index+l;
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error_:_Could_not_highlight_text\n"));
		}
		for(int i=0;i<b.length;i++)
			c[i]=b[i];
		rarg.setPart(c);
	 }
        }
	logJTextArea.append(GlobalProperties.getIntlString("Finally_selected_part_of_arg2_:_"));
        String str = new String();
	for(int i=0;i<rarg.getPart().length;i=i+2)
	{
		logJTextArea.append(story.substring(rarg.getPart()[i],rarg.getPart()[i+1]));
                str = str + story.substring(rarg.getPart()[i],rarg.getPart()[i+1]);
	}
	logJTextArea.append("\n");
//        genMenu(instance);
        addInfoTable(str, selectedoption);
    }
    public void setOption1(EventObject e)
    {
        
       String selectedoption = new String("Sup1");
        if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	//logJTextArea.append("Selected text : " + text + "\n");
	String story=textJTextArea.getText();
	int index=textJTextArea.getSelectionStart(),off=textEditorJPanel.textJTextArea.getSelectionEnd();
        int l=off-index;
	String name=lastSelect.getUserObject().toString();
	TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
	name=node.getName();
	AnnotationInfo instance=node.getChild(y);
	AnnotationInfo larg=null;
	for(int i=0;i<instance.NumOfChildren();i++)
	{
		AnnotationInfo temp=instance.getChild(i);
		if(temp.getFlag()==0)
		{
			larg=temp;
			break;
		}
	}
	Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.MAGENTA);
	if(larg==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("First_set_argument_1_in_order_to_set_suplementary_information_about_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	else
	{
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		int a[]=larg.getsupInfo();
		if(a==null)
		{
			a=new int[2];
			a[0]=index;
			a[1]=index+l;
			larg.setsupInfo(a);
		}
		else
		{
			int len=a.length;
			int b[]=new int[len+2];
			for(int k=0;k<len;k++)
			{
				b[k]=a[k];
			}
			b[len]=index;
			b[len+1]=index+l;
			larg.setsupInfo(b);
		}
                
                String str = new String();
                logJTextArea.append("Sup1:");
                for(int i=0;i<larg.getsupInfo().length;i=i+2)
                {
		logJTextArea.append(story.substring(larg.getsupInfo()[i],larg.getsupInfo()[i+1]));
                str = str + story.substring(larg.getsupInfo()[i],larg.getsupInfo()[i+1]);
                }
                
                addInfoTable(str, selectedoption);
	}
//        genMenu(instance); 
         
    }

    public void setOption2(EventObject e)
    {
       String selectedoption = new String("Sup2");
        if(textJTextArea.isEditable()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return; 
	}
	if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	String text=textJTextArea.getSelectedText();
	if(text==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	//logJTextArea.append("Selected text : " + text + "\n");
	String story=textJTextArea.getText();
	int index=textJTextArea.getSelectionStart(),off=textEditorJPanel.textJTextArea.getSelectionEnd();
        int l=off-index;
	String name=lastSelect.getUserObject().toString();
	TreeNode parent=Lastselect.getParent();
	TreeNode root=parent.getParent();
	int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
	AnnotationInfo node=info.getChild(x);
	name=node.getName();
	AnnotationInfo instance=node.getChild(y);
	AnnotationInfo rarg=null;
	for(int i=0;i<instance.NumOfChildren();i++)
	{
		AnnotationInfo temp=instance.getChild(i);
		if(temp.getFlag()==1)
		{
			rarg=temp;
			break;
		}
	}
	Highlighter h=textJTextArea.getHighlighter();
	this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
	if(rarg==null)
	{
		JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("First_set_argument_2_in_order_to_set_suplementary_information_about_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
		return;
	}
	else
	{
		try
		{
			h.addHighlight(index,index+l,this.painter);
		}catch(BadLocationException ble)
		{
			logJTextArea.append(GlobalProperties.getIntlString("Error:_Could_Highlight_text\n"));
		}
		int a[]=rarg.getsupInfo();
		if(a==null)
		{
			a=new int[2];
			a[0]=index;
			a[1]=index+l;
			rarg.setsupInfo(a);
		}
		else
		{
			int len=a.length;
			int b[]=new int[len+2];
			for(int k=0;k<len;k++)
			{
				b[k]=a[k];
			}
			b[len]=index;
			b[len+1]=index+l;
			rarg.setsupInfo(b);
		}
                String str = new String();
                
                logJTextArea.append("Sup2:");
                
                for(int i=0;i<rarg.getsupInfo().length;i=i+2)
                {
		logJTextArea.append(story.substring(rarg.getsupInfo()[i],rarg.getsupInfo()[i+1]));
                str = str + story.substring(rarg.getsupInfo()[i],rarg.getsupInfo()[i+1]);
                }
                
                addInfoTable(str, selectedoption);
	}
//        genMenu(instance);
        
    }

    public void addInfoConnective(EventObject e)

    {
        
       String selectedoption = new String("ConInfo");
        if(lastSelect==null)
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("This_button_sets_optional_tags_for_an_instance_of_a_connective.\nSelect_an_instance_of_a_connective_first"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
       //Added by Rakesh
        
       
        if(lastSelect.isLeaf()==true)
        {
            TreeNode parent=Lastselect.getParent();
            TreeNode root=parent.getParent();
            int x=root.getIndex(parent);
            int y=parent.getIndex(Lastselect);
            AnnotationInfo conn=info.getChild(x);
            AnnotationInfo instance=conn.getChild(y);
          // jDialog1.setVisible(true);
            showWindow(selectedoption);
            
           
            if(tpath!=null)
            {
                logJTextArea.append(GlobalProperties.getIntlString("Set_Optional_tags_for_connective_:_")+tpath+"\n");
                instance.setoptInfo(tpath);

                String story=textJTextArea.getText();
                int index=textJTextArea.getSelectionStart();
                int off=textEditorJPanel.textJTextArea.getSelectionEnd();

                instance.setAttribSpan(new int[] {index, off});
                
                String str = new String(tpath);
                addInfoTable(str, selectedoption);
                
                selectedoption = "AttribSpan";
                str = textJTextArea.getSelectedText();
                addInfoTable(str, selectedoption);

//                genMenu(instance);                
            }
            
            
          
           
        }
        else
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("Optional_Innformation_can_only_be_set_for_an_instance_of_a_connective_or_its_arguments_and_supplements.\nHence_select_a_relevant_node_from_the_tree."),"Attention",JOptionPane.INFORMATION_MESSAGE);
        }
        
      
          tpath=null;
    }

    public void addInfoArg1(EventObject e)
    {
        String selectedoption = new String("Arg1Info");
          if(lastSelect==null)
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("This_button_sets_optional_tags_for_the_first_argument_of_an_instance_of_a_connective.\nSelect_an_instance_of_a_connective_first"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        
        
        if(lastSelect.isLeaf()==true)
        {
            TreeNode parent=Lastselect.getParent();
            TreeNode root=parent.getParent();
            int x=root.getIndex(parent);
            int y=parent.getIndex(Lastselect);
            AnnotationInfo conn=info.getChild(x);
            AnnotationInfo instance=conn.getChild(y),larg=null;
            
            
            
            for(int i=0;i<instance.NumOfChildren();i++)
            {
                AnnotationInfo temp=instance.getChild(i);
                if(temp.getFlag()==0)
                {
                    larg=temp;
                    
            System.out.println("rakesh");
            System.out.println(larg.getsupInfo());
                    break;
                }
            }
            if(larg==null)
            {
                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("There_is_no_argument_1_for_this_instance_of_the_connective.\nFirst_set_the_argument_1_inorder_to_set_optional_information_for_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            }   
            else
            {
//                jDialog2.setVisible(true);
                 showWindow(selectedoption); 
                if(tpath!=null)
                {
                    logJTextArea.append(GlobalProperties.getIntlString("Set_Optional_Information_for_argument_1_:_")+tpath+"\n");
                    String curr=larg.getoptInfo();
                    if(curr!=null)
                    {
                        int index=curr.indexOf("|");
                        if(index==-1)
                            larg.setoptInfo(tpath);
                        else
                        {
                            String temp=curr.substring(index);
                            temp=tpath+temp;
                            larg.setoptInfo(temp);
                        }
                    }
                    else
                        larg.setoptInfo(tpath);

                    String story=textJTextArea.getText();
                    int index=textJTextArea.getSelectionStart();
                    int off=textEditorJPanel.textJTextArea.getSelectionEnd();

                    larg.setAttribSpan(new int[] {index, off});
                    
                    String str = new String(tpath);
                    addInfoTable(str, selectedoption);
                    
                    selectedoption = "AttribSpanArg1";
                    str = textJTextArea.getSelectedText();
                    addInfoTable(str, selectedoption);
                }
                else
                    larg.setoptInfo(null);                
            } 
//            genMenu(instance);
        }
        else
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("Optional_Innformation_can_only_be_set_for_an_instance_of_a_connective_or_its_arguments_and_supplements.\nHence_select_a_relevant_node_from_the_tree."),"Attention",JOptionPane.INFORMATION_MESSAGE);
        }
          
         tpath=null;
    }

    public void addInfoArg2(EventObject e)
    {
        String selectedoption = new String("Arg2Info");
          
         if(lastSelect==null)
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("This_button_sets_optional_tags_for_the_second_argument_of_an_instance_of_a_connective.\nSelect_an_instance_of_a_connective_first"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(lastSelect.isLeaf()==true)
        {
            TreeNode parent=Lastselect.getParent();
            TreeNode root=parent.getParent();
            int x=root.getIndex(parent);
            int y=parent.getIndex(Lastselect);
            AnnotationInfo conn=info.getChild(x);
            AnnotationInfo instance=conn.getChild(y),rarg=null;
            for(int i=0;i<instance.NumOfChildren();i++)
            {
                AnnotationInfo temp=instance.getChild(i);
                if(temp.getFlag()==1)
                {
                    rarg=temp;
                    break;
                }
            }
            if(rarg==null)
            {
                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("There_is_no_argument_2_for_this_instance_of_the_connective.\nFirst_set_the_argument_2_inorder_to_set_optional_information_for_it"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
              //  jDialog1.setVisible(true);
                showWindow(selectedoption);
                
                if(tpath!=null)
                {
                   logJTextArea.append(GlobalProperties.getIntlString("Set_Optional_Information_for_argument_2_:_")+tpath+"\n");
                    String curr=rarg.getoptInfo();
                    if(curr!=null)
                    {
                        int index=curr.indexOf("|");
                        if(index==-1)
                            rarg.setoptInfo(tpath);
                        else
                        {
                            String temp=curr.substring(index);
                            temp=tpath+temp;
                            rarg.setoptInfo(temp);
                        }
                    }
                    else
                        rarg.setoptInfo(tpath);
  
                    String story=textJTextArea.getText();
                    int index=textJTextArea.getSelectionStart();
                    int off=textEditorJPanel.textJTextArea.getSelectionEnd();

                    rarg.setAttribSpan(new int[] {index, off});
                  
                   String str = new String(tpath);
                   addInfoTable(str, selectedoption);
                    
                    selectedoption = "AttribSpanArg2";
                    str = textJTextArea.getSelectedText();
                    addInfoTable(str, selectedoption);
                }
                else
                    rarg.setoptInfo(null);
          }
//            genMenu(instance);
        }
        else
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("Optional_Innformation_can_only_be_set_for_an_instance_of_a_connective_or_its_arguments_and_supplements.\nHence_select_a_relevant_node_from_the_tree."),"Attention",JOptionPane.INFORMATION_MESSAGE);
        }
        
        tpath=null;
        
    }

    public void addSup1(EventObject e)
    {
        
         String selectedoption = new String("Sup1");
         if(lastSelect==null)
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("This_button_sets_optional_tags_for_the_supplement_of_the_first_argument_of_an_instance_of_a_connective.\nSelect_an_instance_of_a_connective_first"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         
        if(lastSelect.isLeaf()==true)
        {
            TreeNode parent=Lastselect.getParent();
            TreeNode root=parent.getParent();
            int x=root.getIndex(parent);
            int y=parent.getIndex(Lastselect);
            AnnotationInfo conn=info.getChild(x);
            AnnotationInfo instance=conn.getChild(y),larg=null;
            for(int i=0;i<instance.NumOfChildren();i++)
            {
                AnnotationInfo temp=instance.getChild(i);
                if(temp.getFlag()==0)
                {
                    larg=temp;
                    break;
                }
            }
            if(larg==null)
            {
                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("There_is_no_argument_1_for_this_instance_of_the_connective.\nFirst_set_the_argument_1,_then_its_supplement_in_order_to_set_optional_information_for_the_latter"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            }   
            else
            {
             //   jDialog2.setVisible(true);
                 showWindow(selectedoption); 
                if(tpath!=null)
                {
                    String temp=larg.getoptInfo();
                    if(temp!=null)
                    {
                        int index=temp.indexOf("|");
                        if(index==-1)
                            temp=temp+"|"+tpath;
                        else
                            temp=temp.substring(0,index+1) + tpath;
                    }
                    else
                        temp="|"+tpath;
                    larg.setoptInfo(temp);
                    logJTextArea.append(GlobalProperties.getIntlString("Set_Optional_Information_for_Supplement_1_:_")+tpath+"\n");
     
                 String str = new String(tpath);
                 addInfoTable(str, selectedoption);
                }
                
                 
            }
//            genMenu(instance);
        }
        else
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("Optional_Innformation_can_only_be_set_for_an_instance_of_a_connective_or_its_arguments_and_supplements.\nHence_select_a_relevant_node_from_the_tree."),"Attention",JOptionPane.INFORMATION_MESSAGE);
        } 
         
         tpath=null;
    }

    public void addSup2(EventObject e)
    {
        String selectedoption = new String("Sup2");
         if(lastSelect==null)
        {
            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("This_button_sets_optional_tags_for_the_supplement_for_the_second_argument_of_an_instance_of_a_connective.\nSelect_an_instance_of_a_connective_first"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         
        if(lastSelect.isLeaf()==true)
        {
            TreeNode parent=Lastselect.getParent();
            TreeNode root=parent.getParent();
            int x=root.getIndex(parent);
            int y=parent.getIndex(Lastselect);
            AnnotationInfo conn=info.getChild(x);
            AnnotationInfo instance=conn.getChild(y),rarg=null;
            for(int i=0;i<instance.NumOfChildren();i++)
            {
                AnnotationInfo temp=instance.getChild(i);
                if(temp.getFlag()==1)
                {
                    rarg=temp;
                    break;
                }
            }
            if(rarg==null)
            {
                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("There_is_no_argument_2_for_this_instance_of_the_connective.\nFirst_set_the_argument_2,_then_its_supplement_in_order_to_set_optional_information_for_the_latter"),"Attention",JOptionPane.INFORMATION_MESSAGE);
            }   
            else
            {
             //   jDialog2.setVisible(true);
                showWindow(selectedoption); 
                if(tpath!=null)
                {
                    String temp=rarg.getoptInfo();
                    if(temp!=null)
                    {
                        int index=temp.indexOf("|");
                        if(index==-1)
                            temp=temp+"|"+tpath;
                        else
                            temp=temp.substring(0,index+1)+tpath;
                    }
                    else
                        temp="|"+tpath;
                    rarg.setoptInfo(temp);
                  logJTextArea.append(GlobalProperties.getIntlString("Set_Optional_Information_for_Suplement_2_:_")+tpath+"\n");
                 
                String str = new String(tpath);
                addInfoTable(str, selectedoption);
                }
          
            }
//            genMenu(instance);
        }
        else
        {
            JOptionPane.showMessageDialog(this ,GlobalProperties.getIntlString("Optional_Innformation_can_only_be_set_for_an_instance_of_a_connective_or_its_arguments_and_supplements.\nHence_select_a_relevant_node_from_the_tree."),"Attention",JOptionPane.INFORMATION_MESSAGE);
        }
  
        tpath=null;
    }

    //Added by Rakesh
    public void connectiveType(EventObject e)
    {
        Object[] possibilities = {"Explicit","Implicit", "AltLex","EntRel","NoRel"};
        String initLine = "Explicit";
        String selection = new String();
        TreeNode parent=Lastselect.getParent();
        TreeNode root=parent.getParent();
        int x=root.getIndex(parent);
        int y=parent.getIndex(Lastselect);
        AnnotationInfo conn=info.getChild(x);
        AnnotationInfo instance=conn.getChild(y);
        String orgtype = instance.getType();
        
        try{
                selection = (String) JOptionPane.showInputDialog(getOwner(),
			"Enter the connective type", "Type", JOptionPane.INFORMATION_MESSAGE, null,
			possibilities, initLine);
        
                if((selection.compareTo("null")==0)|| (selection.compareTo("Explicit")==0) || (selection.compareTo("Implicit")==0) || (selection.compareTo("AltLex")==0))
                        for(int i = 0; i < actions.length; i++)
                        actions[i].setEnabled(true); 
                else
                {
//                    actions[15].setEnabled(false);
//                    actions[16].setEnabled(false);
//                    actions[18].setEnabled(false);
//                    actions[19].setEnabled(false);
//                    actions[20].setEnabled(false);
//                    actions[21].setEnabled(false);
//                    actions[22].setEnabled(false);
//                    actions[23].setEnabled(false);
//                    actions[24].setEnabled(false);
//                    actions[25].setEnabled(false);
//                    actions[26].setEnabled(false);
//                    actions[27].setEnabled(false);
                    
                    
                }
                instance.setType(selection);
                addInfoTable(selection, "ConType");
        }catch(Exception ex)
        {
            instance.setType(orgtype);
            
        }
   
    }
    //till here
    public void removeConnective(EventObject e)
    {
        Removenode("RemoveConnective");
    }
    
    public void removeArg1(EventObject e)
    {
        String selection = new String("RemoveArg1");
        Highlighter h= textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
               // if(selectedoption.compareTo("RemoveArg1")==0)
		//{
                        
			TreeNode parent=Lastselect.getParent();
			TreeNode root=parent.getParent();
			int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
			AnnotationInfo node=info.getChild(x);
			AnnotationInfo current=node.getChild(y);
			for(int i=0;i<current.NumOfChildren();i++)
			{
				AnnotationInfo temp=current.getChild(i);
				if(temp.getFlag()==0)
				{
					current.removeChild(i);
					break;
				}
			}
                        h.removeAllHighlights();
                        highlight(current);
//                        genMenu(current);
                       
                        //Added by Rakesh
                        
                        addInfoTable(" ", selection);
                        //
                        
                        toolkit.beep();  
		//}
    }

    public void removeArg2(EventObject e)
    {
        
        String selection = new String("RemoveArg2");
        Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
                //if(selectedoption.compareTo("RemoveArg2")==0)
		//{
			TreeNode parent=Lastselect.getParent();
			TreeNode root=parent.getParent();
			int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
			AnnotationInfo node=info.getChild(x);
			AnnotationInfo current=node.getChild(y);
			for(int i=0;i<current.NumOfChildren();i++)
			{
				AnnotationInfo temp=current.getChild(i);
				if(temp.getFlag()==1)
				{
					current.removeChild(i);
					break;
				}
			}
                        h.removeAllHighlights();
                        highlight(current);
//                        genMenu(current);
                        
                        
                        addInfoTable(" ", selection);
                        
                         toolkit.beep();  
		//}
    }

    public void removeOption1(EventObject e)
    {
        
        String selection = new String("RemoveSup1");
        Highlighter h= textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
                
               // if(selectedoption.compareTo("RemoveSup1")==0)
		//{
			TreeNode parent=Lastselect.getParent();
			TreeNode root=parent.getParent();
			int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
			AnnotationInfo node=info.getChild(x);
			AnnotationInfo current=node.getChild(y);
			for(int i=0;i<current.NumOfChildren();i++)
			{
				AnnotationInfo temp=current.getChild(i);
				if(temp.getFlag()==0)
				{
					temp.setsupInfo(null);
					break;
				}
			}
                        h.removeAllHighlights();
                        highlight(current);
//                        genMenu(current);
                        
                       
                        addInfoTable(" ", selection);
		//}
                         toolkit.beep();  
    }

    public void removeOption2(EventObject e)
    {
        String selection = new String("RemoveSup2");
        Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
                
               // if(selectedoption.compareTo("RemoveSup2")==0)
		//{
			TreeNode parent=Lastselect.getParent();
			TreeNode root=parent.getParent();
			int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
			AnnotationInfo node=info.getChild(x);
			AnnotationInfo current=node.getChild(y);
			for(int i=0;i<current.NumOfChildren();i++)
			{
				AnnotationInfo temp=current.getChild(i);
				if(temp.getFlag()==1)
				{
					temp.setsupInfo(null);
					break;
				}
			}
                        h.removeAllHighlights();
                        highlight(current);
//                        genMenu(current);
                        
                        
                        
		//}
                       
                        addInfoTable(" ", selection);
                         toolkit.beep();  
    }
    //Added by Rakesh
    public void removepartArg1(EventObject e)
    {
        String selection = new String("PartArg1");
        Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
                
               	TreeNode parent=Lastselect.getParent();
			TreeNode root=parent.getParent();
			int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
			AnnotationInfo node=info.getChild(x);
			AnnotationInfo current=node.getChild(y);
			for(int i=0;i<current.NumOfChildren();i++)
			{
				AnnotationInfo temp=current.getChild(i);
				if(temp.getFlag()==0)
				{
					temp.setPart(null);
					break;
				}
			}
                        h.removeAllHighlights();
                        highlight(current);
//                        genMenu(current);
                        addInfoTable("", selection);
                         toolkit.beep();  
    }
//Added by Rakesh
public void removepartArg2(EventObject e)
    {
        String selection = new String("PartArg2");
        Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
                
               // if(selectedoption.compareTo("RemoveSup2")==0)
		//{
			TreeNode parent=Lastselect.getParent();
			TreeNode root=parent.getParent();
			int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
			AnnotationInfo node=info.getChild(x);
			AnnotationInfo current=node.getChild(y);
			for(int i=0;i<current.NumOfChildren();i++)
			{
				AnnotationInfo temp=current.getChild(i);
				if(temp.getFlag()==1)
				{
					temp.setPart(null);
					break;
				}
			}
                        h.removeAllHighlights();
                        highlight(current);
//                        genMenu(current);
                        
                        
                        
		//}
                       
                        addInfoTable(" ", selection);
                         toolkit.beep();  
    }

    public void removeInfoConnective(EventObject e)
    {
        
                String selection = new String("RemoveConInfo");
                
                
                Highlighter h = textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
		
                
               // if(selectedoption.compareTo("RemoveOptforConn")==0){
                    TreeNode parent=Lastselect.getParent();
                    TreeNode root=parent.getParent();
                    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
                    AnnotationInfo node=info.getChild(x);
                    AnnotationInfo current=node.getChild(y);
                    current.setoptInfo(null);
                    current.setAttribSpan(null);
//                    genMenu(current);
             //   }
                    
                   
                    addInfoTable(" ", selection);
                    
                    selection = "AttribSpan";
                    addInfoTable(" ", selection);
                    
                     toolkit.beep();  
    }

    public void removeInfoArg1(EventObject e)  
    {
        String selection = new String("RemoveArg1Info");
        
        Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
               //  if(selectedoption.compareTo("RemoveOptforArg1")==0){
                    TreeNode parent=Lastselect.getParent();
                    TreeNode root=parent.getParent();
                    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
                    AnnotationInfo node=info.getChild(x);
                    AnnotationInfo current=node.getChild(y),larg=null;
                    for(int i=0;i<current.NumOfChildren();i++)
                    {
                        AnnotationInfo temp=current.getChild(i);
                        if(temp.getFlag()==0)
                        {
                            larg=temp;
                            break;
                        }
                    }
                    if(larg!=null)
                    {
                        String opt=larg.getoptInfo();
                        if(opt!=null)
                        {
                            int index=opt.indexOf("|");
                            if(index==-1)
                            {
                                larg.setoptInfo(null);
                                larg.setAttribSpan(null);
                            }
                            else
                            {
                                if(index!=0)
                                {
                                    String t=opt.substring(index);
                                    larg.setoptInfo(t);
                                }
                            }
                        }
                    }
//                    genMenu(current);
                     toolkit.beep();  
               // }
                     addInfoTable(" ", selection);
                    
                    selection = "AttribSpanArg1";
                    addInfoTable(" ", selection);
    }

    public void removeInfoArg2(EventObject e)
    {
        String selection = new String("RemoveArg2Info");
        
        Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
        //        if(selectedoption.compareTo("RemoveOptforArg2")==0){
                    TreeNode parent=Lastselect.getParent();
                    TreeNode root=parent.getParent();
                    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
                    AnnotationInfo node=info.getChild(x);
                    AnnotationInfo current=node.getChild(y),rarg=null;
                    for(int i=0;i<current.NumOfChildren();i++)
                    {
                        AnnotationInfo temp=current.getChild(i);
                        if(temp.getFlag()==1)
                        {
                            rarg=temp;
                            break;
                        }
                    }
                    if(rarg!=null)
                    {
                        String opt=rarg.getoptInfo();
                        if(opt!=null)
                        {
                            int index=opt.indexOf("|");
                            if(index==-1)
                            {
                                rarg.setoptInfo(null);
                                rarg.setAttribSpan(null);
                            }
                            else
                            {
                                if(index!=0)
                                {
                                    String t=opt.substring(index);
                                    rarg.setoptInfo(t);
                                }
                            }
                        }
                    }
//                    genMenu(current);
        //        }
                 toolkit.beep();  
                     
                selection = "AttribSpanArg2";
                addInfoTable(" ", selection);
                
                addInfoTable(" ", selection);
    }

    public void removeInfoSup1(EventObject e)
    {
        String selection = new String("RemoveSup1");
        
        Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
                
              //  if(selectedoption.compareTo("RemoveOptforSup1")==0){
                    TreeNode parent=Lastselect.getParent();
                    TreeNode root=parent.getParent();
                    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
                    AnnotationInfo node=info.getChild(x);
                    AnnotationInfo current=node.getChild(y),larg=null;
                    for(int i=0;i<current.NumOfChildren();i++)
                    {
                        AnnotationInfo temp=current.getChild(i);
                        if(temp.getFlag()==0)
                        {
                            larg=temp;
                            break;
                        }
                    }
                    if(larg!=null)
                    {
                        String opt=larg.getoptInfo();
                        if(opt!=null)
                        {
                            int index=opt.indexOf("|");
                            if(index!=-1)
                            {
                                if(index==0)
                                {
                                    larg.setoptInfo(null);
                                }
                                else
                                    larg.setoptInfo(opt.substring(0,index));
                            }
                        }
                    }
//                    genMenu(current);
                     toolkit.beep();  
                     
                     addInfoTable(" ", selection);
   // }
    }

    public void removeInfoSup2(EventObject e)
    {
                String selection = new String("RemoveSup2");
                Highlighter h=textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
                
             //   if(selectedoption.compareTo("RemoveOptforSup2")==0){
                    TreeNode parent=Lastselect.getParent();
                    TreeNode root=parent.getParent();
                    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
                    AnnotationInfo node=info.getChild(x);
                    AnnotationInfo current=node.getChild(y),rarg=null;
                    for(int i=0;i<current.NumOfChildren();i++)
                    {
                        AnnotationInfo temp=current.getChild(i);
                        if(temp.getFlag()==1)
                        {
                            rarg=temp;
                            break;
                        }
                    }
                    if(rarg!=null)
                    {
                        String opt=rarg.getoptInfo();
                        if(opt!=null)
                        {
                            int index=opt.indexOf("|");
                            if(index!=-1)
                            {
                                if(index==0)
                                {
                                    rarg.setoptInfo(null);
                                }
                                else
                                    rarg.setoptInfo(opt.substring(0,index));
                            }
                        }
                    }
//                    genMenu(current);
            //    }
           toolkit.beep(); 
           
           addInfoTable(" ", selection);
    }
    
    public void removepartConnective(EventObject e)
    {
        String selection = new String("PartCon");
                
                
                Highlighter h = textJTextArea.getHighlighter();
		JMenuItem source =(JMenuItem)e.getSource();
		String selectedoption=source.getText();
		logJTextArea.setText(selectedoption);
		
                int[] a = new int[0];
               // if(selectedoption.compareTo("RemoveOptforConn")==0){
                    TreeNode parent=Lastselect.getParent();
                    TreeNode root=parent.getParent();
                    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
                    AnnotationInfo node=info.getChild(x);
                    AnnotationInfo current=node.getChild(y);
                    
                    int i = current.setPart(null);
                    System.out.print("rakesh"+i);
                    h.removeAllHighlights();
                    highlight(current);
//                    genMenu(current);
             //   }
                    
                   
                    addInfoTable("", selection);
                     toolkit.beep();  
    }
    
    public String switchLanguage(EventObject e)
    {
        return switchLanguageStatic(this);
    }
    
    public static String switchLanguageStatic(Component parent)
    {
	boolean switching = false;

	if(parent instanceof DiscourseArgOptionalJPanel)
	    switching = true;

	if(switching)
	{
	    if(((DiscourseArgOptionalJPanel) parent).dirty)    
	    {
		int retVal = JOptionPane.showConfirmDialog(parent, GlobalProperties.getIntlString("The_current_file_will_be_closed._If_you_haven't_saved_it,\nthe_data_may_be_lost._Do_you_want_to_continue?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

		if(retVal == JOptionPane.NO_OPTION)
		    return null;
	    }

	   ((DiscourseArgOptionalJPanel) parent).textEditorJPanel.setText("");
	   ((DiscourseArgOptionalJPanel) parent).logJTextArea.setText("");
	}
	
	Vector allLanguages = new Vector(0, 3);

	Iterator enm = SanchayLanguages.getAllLanguages().getPropertyKeys();
	
	while(enm.hasNext())
	{
	    String key = (String) enm.next();
	    allLanguages.add(key);
	}
	
	Object langs[] = allLanguages.toArray();
	
	Arrays.sort(langs);
	
	String initLang = "Hindi";
	
	if(switching)
	    initLang = SanchayLanguages.getLanguageName( ((DiscourseArgOptionalJPanel) parent).langEnc );

	String selectedLanguage = (String) JOptionPane.showInputDialog(parent,
		GlobalProperties.getIntlString("Select_the_language"), "Language", JOptionPane.INFORMATION_MESSAGE, null,
		langs, initLang);

	if(switching)
	{
	    String langCode = SanchayLanguages.getLangEncCode(selectedLanguage);
	    ((DiscourseArgOptionalJPanel) parent).langEnc = langCode;

	    UtilityFunctions.setComponentFont(((DiscourseArgOptionalJPanel) parent).textJTextArea, langCode);
	    UtilityFunctions.setComponentFont(((DiscourseArgOptionalJPanel) parent).logJTextArea, langCode);

	    UtilityFunctions.setComponentFont(((DiscourseArgOptionalJPanel) parent).connectiveInfoTreeJPanel.treeJTree, langCode);
            ((DiscourseArgOptionalJPanel) parent).connectiveInfoTreeJPanel.treeJTree.setCellRenderer(new SSFTreeCellRendererNew(3, new String[] {langCode, langCode, langCode}, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION));
            
	    UtilityFunctions.setComponentFont(((DiscourseArgOptionalJPanel) parent).additionalInfoArgSupTreeJPanel, langCode);
            ((DiscourseArgOptionalJPanel) parent).additionalInfoArgSupTreeJPanel.treeJTree.setCellRenderer(new SSFTreeCellRendererNew(3, new String[] {langCode, langCode, langCode}, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION));
            
	    UtilityFunctions.setComponentFont(((DiscourseArgOptionalJPanel) parent).additionalInfoConTreeJPanel, langCode);
            ((DiscourseArgOptionalJPanel) parent).additionalInfoConTreeJPanel.treeJTree.setCellRenderer(new SSFTreeCellRendererNew(3, new String[] {langCode, langCode, langCode}, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION));

            UtilityFunctions.setComponentFont(((DiscourseArgOptionalJPanel) parent).connectiveInfoTableJPanel.getJTable(), langCode);

            UtilityFunctions.setComponentFont(((DiscourseArgOptionalJPanel) parent).connectiveJTextField, langCode);
            
	    ((DiscourseArgOptionalJPanel) parent).setTitle(((DiscourseArgOptionalJPanel) parent).getTitle());
            ((DiscourseArgOptionalJPanel) parent).textFile = GlobalProperties.getIntlString("Untitled");
	}
	
	return selectedLanguage;
    }

    public void selectInputMethod(EventObject e)
    {
        String im = SanchayLanguages.selectInputMethod(this);
                
        if(owner != null)
            SanchayLanguages.changeInputMethod(owner, im);
        else if(dialog != null)
            SanchayLanguages.changeInputMethod(dialog, im);
    }
    
    //added by Kinshul (for building tree from xml data file)
    private void xmlToTree(AnnotationInfo root,String filename)
        {
            //clear ur data structure
            File file = new File(filename);
		try {
			DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(file);
                        AnnotationInfo conn;
			org.w3c.dom.NodeList nodes = doc.getElementsByTagName("connective");
			for (int i = 0; i < nodes.getLength(); i++) 
                        {
				org.w3c.dom.Element element = (org.w3c.dom.Element) nodes.item(i);
				org.w3c.dom.NodeList title = element.getElementsByTagName("name");
				org.w3c.dom.Element line = (org.w3c.dom.Element) title.item(0);
			//	System.out.println("name: " + getCharacterDataFromElement(line));
                                conn=new AnnotationInfo(getCharacterDataFromElement(line));
				org.w3c.dom.NodeList opt = element.getElementsByTagName("opt");
				line = (org.w3c.dom.Element) opt.item(0);
                                conn.setoptInfo(getCharacterDataFromElement(line));
                              
                                conn.setParent(root);
                                AnnotationInfo inst;
                                org.w3c.dom.NodeList instance=element.getElementsByTagName("instance");
                                for(int j=0;j<instance.getLength();j++)
                                {
                                    org.w3c.dom.Element element1 =(org.w3c.dom.Element) instance.item(j);
                                    org.w3c.dom.NodeList number=element1.getElementsByTagName("number");
                                    org.w3c.dom.Element line1 =(org.w3c.dom.Element)number.item(0);
                                    String name="Instance"+getCharacterDataFromElement(line1);
                                    inst=new AnnotationInfo(name);
                                    org.w3c.dom.NodeList partcon=element1.getElementsByTagName("part");
                                    line1 =(org.w3c.dom.Element)partcon.item(0);
                                    String part=getCharacterDataFromElement(line1);
                                    org.w3c.dom.NodeList type=element1.getElementsByTagName("type");
                                    line1 =(org.w3c.dom.Element)type.item(0);
                                    String typecon=getCharacterDataFromElement(line1);
                                    inst.setType(typecon);
//                                    org.w3c.dom.NodeList source=element1.getElementsByTagName("source");
//                                    line1 =(org.w3c.dom.Element)source.item(0);
//                                    String sourcecon=getCharacterDataFromElement(line1);
//                                    inst.setSource(sourcecon);
//                                    org.w3c.dom.NodeList factuality=element1.getElementsByTagName("factuality");
//                                    line1 =(org.w3c.dom.Element)factuality.item(0);
//                                    String factualitycon=getCharacterDataFromElement(line1);
//                                    inst.setFactuality(factualitycon);
//                                    org.w3c.dom.NodeList polarity=element1.getElementsByTagName("polarity");
//                                    line1 =(org.w3c.dom.Element)polarity.item(0);
//                                    String polaritycon=getCharacterDataFromElement(line1);
//                                    inst.setPolarity(polaritycon);
                                    
                                    org.w3c.dom.NodeList head=element1.getElementsByTagName("head");
                                    line1 =(org.w3c.dom.Element)head.item(0);
                                    String headCon =getCharacterDataFromElement(line1);
                                    inst.setHead(headCon);
                                    org.w3c.dom.NodeList position=element1.getElementsByTagName("position");
                                    line1 =(org.w3c.dom.Element)position.item(0);
                                    String pos=getCharacterDataFromElement(line1);
                                    
                                    
                                    int a[],num=0,l=0,in=0,k;
                                   String str = inst.getType();
//                                   if(str.compareTo("Implicit")!=0 || str.compareTo("AltLex")!=0 || str.compareTo("NoRel")!=0) 
                                   if(str.compareTo("Implicit")!=0 || str.compareTo("NoRel")!=0) 
                                  {
                                   
                                    for( k=0;k<pos.length();k++)
                                        if(pos.charAt(k)==' ')
                                            num++;
                                    a=new int[num];
                                     k=0;
                                    if((num%2)!=0)
                                    {
                                        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                        return;
                                    }
                            
                                  while(l>=0 && l<pos.length())
                                    {
                                        in=pos.indexOf(32,l);
                                        a[k]=Integer.valueOf(pos.substring(l,in)).intValue();
                                        k++;
                                        l=in+1;
                                    }
                                    if(k!=num)
                                    {
                                        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                        return;
                                    }
                                   inst.setPos(a);
                                   }else{
                                        inst.setPos(null);
                                        }  
                             //part      
                                   if(part.compareTo("null")!=0)
                                    {
                                        
                                        l=0;in=0;num=0;
                                        for(k=0;k<part.length();k++)
                                        {
                                            if(part.charAt(k)==' ')
                                                num++;
                                        }
                                        a=new int[num];
                                        k=0;
                                        if((num%2)!=0)
                                        {
                                            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                        }
                                        while(l>=0 && l<part.length())
                                        {
                                            in=part.indexOf(32,l);
                                            a[k]=Integer.valueOf(part.substring(l,in)).intValue();
                                            l=in+1;
                                            k++;
                                        }
                                        if(k!=num)
                                        {
                                            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                        }
                                        inst.setPart(a);
                                   }
                                   else{
                                       inst.setPart(null);
                                   }
                                //   
                                    inst.setParent(conn);
                                    org.w3c.dom.NodeList optnode=element1.getElementsByTagName("opt");
                                    line1 =(org.w3c.dom.Element)optnode.item(0);
                                    String temp=getCharacterDataFromElement(line1);
                                    if(temp.compareTo("null")==0)
                                        inst.setoptInfo(null);
                                    else
                                    {
                                        inst.setoptInfo(temp);
                                            
                                        org.w3c.dom.NodeList attribSpan =element1.getElementsByTagName(GlobalProperties.getIntlString("AttribSpan"));
                                        
                                        if(attribSpan != null)
                                        {
                                            line1 =(org.w3c.dom.Element) attribSpan.item(0);
                                            String attribSpanPos = getCharacterDataFromElement(line1);

                                            String posStrs[] = attribSpanPos.split("[ ]");                                    
                                            int posInt[] = new int[] {Integer.parseInt(posStrs[0]), Integer.parseInt(posStrs[1])};
                                            inst.setAttribSpan(posInt);
                                        }
                                    }

                                    org.w3c.dom.NodeList arg1=element1.getElementsByTagName("arg1");
                                    org.w3c.dom.Element element2=(org.w3c.dom.Element) arg1.item(0);
                                    org.w3c.dom.NodeList positionarg1=element2.getElementsByTagName("position");
                                    org.w3c.dom.Element line2 =(org.w3c.dom.Element)positionarg1.item(0);
                                    pos=getCharacterDataFromElement(line2); 
                                    org.w3c.dom.NodeList part1=element2.getElementsByTagName("part1");
                                    line2 =(org.w3c.dom.Element)part1.item(0);
                                    part=getCharacterDataFromElement(line2); 
                                    org.w3c.dom.NodeList sup1=element2.getElementsByTagName("sup1");
                                    line2 =(org.w3c.dom.Element)sup1.item(0);
                                    String sup=getCharacterDataFromElement(line2);
                                    org.w3c.dom.NodeList optarg1=element2.getElementsByTagName("opt");
                                    line2=(org.w3c.dom.Element)optarg1.item(0); 
                                    String option=getCharacterDataFromElement(line2); 
                                    
//                                    org.w3c.dom.NodeList sourcearg1=element2.getElementsByTagName("source");
//                                    line2 =(org.w3c.dom.Element)sourcearg1.item(0);
//                                    String source1=getCharacterDataFromElement(line2);
//                                    org.w3c.dom.NodeList factualityarg1=element2.getElementsByTagName("factuality");
//                                    line2 =(org.w3c.dom.Element)factualityarg1.item(0);
//                                    String factuality1=getCharacterDataFromElement(line2);
//                                    org.w3c.dom.NodeList polarityarg1=element2.getElementsByTagName("polarity");
//                                    line2 =(org.w3c.dom.Element)polarityarg1.item(0);
//                                    String polarity1=getCharacterDataFromElement(line2);
                                    if(pos.compareTo("null")!=0)
                                    {
                                        AnnotationInfo larg=new AnnotationInfo("Arg1",0);
                                        l=0;in=0;num=0;
                                        for(k=0;k<pos.length();k++)
                                        {
                                            if(pos.charAt(k)==' ')
                                                num++;
                                        }
                                        a=new int[num];
                                        k=0;
                                        if((num%2)!=0)
                                        {
                                            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                        }
                                        while(l>=0 && l<pos.length())
                                        {
                                            in=pos.indexOf(32,l);
                                            a[k]=Integer.valueOf(pos.substring(l,in)).intValue();
                                            l=in+1;
                                            k++;
                                        }
                                        if(k!=num)
                                        {
                                            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                        }
                                        larg.setPos(a);
                                        larg.setParent(inst);
//                                        larg.setSource(source1);
//                                        larg.setFactuality(factuality1);
//                                        larg.setPolarity(polarity1);
                                        if(sup.compareTo("null")!=0)
                                        {
                                            l=0;in=0;num=0;
                                            for(k=0;k<sup.length();k++)
                                            {
                                                if(sup.charAt(k)==' ')
                                                    num++;
                                            }
                                            int b[]=new int[num];
                                            if((num%2)!=0)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            k=0;
                                            while(l>=0 && l<sup.length())
                                            {
                                                in=sup.indexOf(32,l);
                                                b[k]=Integer.valueOf(sup.substring(l,in)).intValue();
                                                k++;
                                                l=in+1;
                                            }
                                            if(k!=num)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            larg.setsupInfo(b);
                                        }
                                        else
                                        {
                                            larg.setsupInfo(null);
                                        }
                                        //part
                                        if(part.compareTo("null")!=0)
                                        {
                                            l=0;in=0;num=0;
                                            for(k=0;k<part.length();k++)
                                            {
                                                if(part.charAt(k)==' ')
                                                    num++;
                                            }
                                            int p[]=new int[num];
                                            if((num%2)!=0)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            k=0;
                                            while(l>=0 && l<part.length())
                                            {
                                                in=part.indexOf(32,l);
                                                p[k]=Integer.valueOf(part.substring(l,in)).intValue();
                                                k++;
                                                l=in+1;
                                            }
                                            if(k!=num)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            larg.setPart(p);
                                            System.out.print(p);
                                        }
                                        else
                                        {
                                            larg.setPart(null);
                                        }
                                        //
                                        if(option.compareTo("null")==0)
                                            larg.setoptInfo(null);
                                        else
                                        {
                                            larg.setoptInfo(option);
                                            
                                            org.w3c.dom.NodeList attribSpan =element1.getElementsByTagName("AttribSpan");
                                            
                                            if(attribSpan != null)
                                            {
                                                line1 =(org.w3c.dom.Element) attribSpan.item(0);
                                                String attribSpanPos = getCharacterDataFromElement(line1);

                                                String posStrs[] = attribSpanPos.split("[ ]");                                    
                                                int posInt[] = new int[] {Integer.parseInt(posStrs[0]), Integer.parseInt(posStrs[1])};
                                                larg.setAttribSpan(posInt);
                                            }
                                        }
                                        inst.addChild(larg);
                                    }
                                    org.w3c.dom.NodeList arg2=element1.getElementsByTagName("arg2");
                                    element2=(org.w3c.dom.Element) arg2.item(0);
                                    org.w3c.dom.NodeList positionarg2=element2.getElementsByTagName("position");
                                    line2 =(org.w3c.dom.Element)positionarg2.item(0);
                                    pos=getCharacterDataFromElement(line2);
                                    org.w3c.dom.NodeList part2=element2.getElementsByTagName("part2");
                                    line2 =(org.w3c.dom.Element)part2.item(0);
                                    part=getCharacterDataFromElement(line2);
                                    org.w3c.dom.NodeList sup2=element2.getElementsByTagName("sup2");
                                    line2 =(org.w3c.dom.Element)sup2.item(0);
                                    sup=getCharacterDataFromElement(line2);
                                    org.w3c.dom.NodeList optarg2=element2.getElementsByTagName("opt");
                                    line2=(org.w3c.dom.Element)optarg2.item(0);
                                    option=getCharacterDataFromElement(line2);
                                    
//                                    org.w3c.dom.NodeList sourcearg2=element2.getElementsByTagName("source");
//                                    line2 =(org.w3c.dom.Element)sourcearg2.item(0);
//                                    String source2=getCharacterDataFromElement(line2);
//                                    org.w3c.dom.NodeList factualityarg2=element2.getElementsByTagName("factuality");
//                                    line2 =(org.w3c.dom.Element)factualityarg2.item(0);
//                                    String factuality2=getCharacterDataFromElement(line2);
//                                    org.w3c.dom.NodeList polarityarg2=element2.getElementsByTagName("polarity");
//                                    line2 =(org.w3c.dom.Element)polarityarg2.item(0);
//                                    String polarity2=getCharacterDataFromElement(line2);
                                    if(pos.compareTo("null")!=0)
                                    {
                                        AnnotationInfo rarg=new AnnotationInfo("Arg2",1);
                                        l=0;in=0;num=0;
                                        for(k=0;k<pos.length();k++)
                                        {
                                            if(pos.charAt(k)==' ')
                                                num++;
                                        }
                                        a=new int[num];
                                        k=0;
                                        if((num%2)!=0)
                                        {
                                            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                        }
                                        while(l>=0 && l<pos.length())
                                        {
                                            in=pos.indexOf(32,l);
                                            a[k]=Integer.valueOf(pos.substring(l,in)).intValue();
                                            l=in+1;
                                            k++;
                                        }
                                        if(k!=num)
                                        {
                                            JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                        }
                                        rarg.setPos(a);
                                        rarg.setParent(inst);
//                                        rarg.setSource(source2);
//                                        rarg.setFactuality(factuality2);
//                                        rarg.setPolarity(polarity2);
                                        if(sup.compareTo("null")!=0)
                                        {
                                            l=0;in=0;num=0;
                                            for(k=0;k<sup.length();k++)
                                            {
                                                if(sup.charAt(k)==' ')
                                                    num++;
                                            }
                                            int b[]=new int[num];
                                            k=0;
                                            if((num%2)!=0)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            while(l>=0 && l<sup.length())
                                            {
                                                in=sup.indexOf(32,l);
                                                b[k]=Integer.valueOf(sup.substring(l,in)).intValue();
                                                k++;
                                                l=in+1;
                                            }
                                            if(k!=num)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            rarg.setsupInfo(b);
                                        }
                                        else
                                        {
                                            rarg.setsupInfo(null);
                                        }
                                        //part
                                        if(part.compareTo("null")!=0)
                                        {
                                            l=0;in=0;num=0;
                                            for(k=0;k<part.length();k++)
                                            {
                                                if(part.charAt(k)==' ')
                                                    num++;
                                            }
                                            int p[]=new int[num];
                                            k=0;
                                            if((num%2)!=0)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            while(l>=0 && l<part.length())
                                            {
                                                in=part.indexOf(32,l);
                                                p[k]=Integer.valueOf(part.substring(l,in)).intValue();
                                                k++;
                                                l=in+1;
                                            }
                                            if(k!=num)
                                            {
                                                JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("The_data_in_xml_file_is_corrupted"),GlobalProperties.getIntlString("Fatal_Error"),JOptionPane.INFORMATION_MESSAGE);
                                                return;
                                            }
                                            rarg.setPart(p);
                                             System.out.print(p);
                                        }
                                        else
                                        {
                                            rarg.setPart(null);
                                        }
                                        //
                                        if(option.compareTo("null")==0)
                                            rarg.setoptInfo(null);
                                        else
                                        {
                                            rarg.setoptInfo(option);
                                            
                                            org.w3c.dom.NodeList attribSpan =element2.getElementsByTagName("AttribSpan");
                                            
                                            if(attribSpan != null)
                                            {
                                                line1 =(org.w3c.dom.Element) attribSpan.item(0);
                                                String attribSpanPos = getCharacterDataFromElement(line1);

                                                String posStrs[] = attribSpanPos.split("[ ]");                                    
                                                int posInt[] = new int[] {Integer.parseInt(posStrs[0]), Integer.parseInt(posStrs[1])};
                                                rarg.setAttribSpan(posInt);
                                            }
                                        }
                                        //rarg.setoptInfo(option);
                                        inst.addChild(rarg);
                                    }
                                    conn.addChild(inst);
                                }
                                root.addChild(conn);
                        }
                        
		}
		catch (Exception e) {
			e.printStackTrace();
		}
            buildTree(root);
        }
  
        //added by nrapesh  
//gets the data from the xml file
    public static String getCharacterDataFromElement(org.w3c.dom.Element e) {
		org.w3c.dom.Node child = e.getFirstChild();
		if (child instanceof org.w3c.dom.CharacterData) {
			org.w3c.dom.CharacterData cd = (org.w3c.dom.CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

    //added by Kinshul
//builds the connective tree using information from the data structure
    private void buildTree(AnnotationInfo info)
    {
            SSFPhrase connectiveJChild = null;
            for(int i=0;i<info.NumOfChildren();i++)
            {
                AnnotationInfo connective=info.getChild(i);
                String conn=connective.getName();
                try {
                    connectiveJChild = new SSFPhrase("0", "", conn, "", conn);
                } catch (Exception ex) {
                    Logger.getLogger(DiscourseArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                connectiveJRoot.addChild(connectiveJChild);
           //     connectiveJChild=addObject(connectiveJRoot,conn,true);
                SSFPhrase instance;
                for(int j=0;j<connective.NumOfChildren();j++)
                {
                    AnnotationInfo inst=connective.getChild(j);
                    String name=inst.getName();
                    addObject(connectiveJChild,name,false);
                }
                
               //connectiveInfoTreeJPanel.expandAll(null);
//                connectiveJTree.setVisible(false);
//                connectiveJTree.setVisible(true);
            }
        }
    private void writeToFile(String fileName) //Added by Kinshul and nrapesh (for creating xml data file)
    {
            try
            {
                BufferedWriter out = new BufferedWriter(new PrintWriter(fileName, charset));
                out.write("<?xml version=\"1.0\"?>\n");
                out.write("<story>\n");
                AnnotationInfo child,node,leaf,larg,rarg;
                for(int i=0;i<info.NumOfChildren();i++)
                {
                    out.write("\t<connective>\n");
                    child=info.getChild(i);
                    out.write("\t\t<name>"+child.getName()+"</name>\n");
                    out.write("\t\t<opt>"+child.getoptInfo()+"</opt>\n");
                   for(int j=0;j<child.NumOfChildren();j++)
                    {
                        node=child.getChild(j);
                        out.write("\t\t<instance>\n");
                        out.write("\t\t\t<number>"+String.valueOf(j+1)+"</number>\n");
                        String pos="";
                        String part="";
                        int a[]=node.getPos();
                        if(a==null)
                            pos=null;
                       if(a!=null)
                       {
                           for(int k=0;k<a.length;k++)
                           pos=pos+String.valueOf(a[k])+" ";
                       }
                        int p[]=node.getPart();
                        if(p==null)
                            part=null;
                       if(p!=null)
                       {
                           for(int k=0;k<p.length;k++)
                           part=part+String.valueOf(p[k])+" ";
                       }
                        String type = node.getType();
                        out.write("\t\t\t<part>"+part+"</part>\n");
                        out.write("\t\t\t<type>"+type+"</type>\n");
                        
                        int attribSpan[] = node.getAttribSpan();

                        if(attribSpan != null)
                        {
                            out.write("\t\t\t<AttribSpan>"+ attribSpan[0] + " "  + attribSpan[1] +"</AttribSpan>\n");                            
                        }
//                        out.write("\t\t\t<source>"+node.getSource()+"</source>\n");
//                        out.write("\t\t\t<factuality>"+node.getFactuality()+"</factuality>\n");
//                        out.write("\t\t\t<polarity>"+node.getPolarity()+"</polarity>\n");   
                        out.write("\t\t\t<head>"+node.getHead()+"</head>\n");
                        out.write("\t\t\t<position>"+pos+"</position>\n");
                        out.write("\t\t\t<opt>"+node.getoptInfo()+"</opt>\n");
                        out.write("\t\t\t<arg1>\n");
                        larg=null;
                        rarg=null;
                        for(int k=0;k<node.NumOfChildren();k++)
                        {
                            leaf=node.getChild(k);
                            if(leaf.getFlag()==0)
                                larg=leaf;
                            if(leaf.getFlag()==1)
                                rarg=leaf;
                        }
                        pos="";
                        part="";
                        String opt,sup,source,factuality,polarity;
                        if(larg==null)
                        {
                            pos=null;
                            part=null;
                            opt=null;
                            sup=null;
                            source=null;
                            factuality=null;
                            polarity=null;
                        }
                        else
                        {
                            int b[]=larg.getPos();
                            for(int k=0;k<b.length;k++)
                                pos=pos+String.valueOf(b[k])+" ";
                            int c[]=larg.getsupInfo();
                            //for supplements
                            sup="";
                            if(c==null)
                                sup=null;
                            else
                            {
                                for(int k=0;k<c.length;k++)
                                   sup=sup+String.valueOf(c[k])+" "; 
                            }
                            //for disjoint arg
                            p=larg.getPart();
                             if(p==null)
                                 part=null;
                             else
                            {
                              for(int k=0;k<p.length;k++)
                                   part=part+String.valueOf(p[k])+" ";    
                            }
                            opt=larg.getoptInfo();
//                            source=larg.getSource();
//                            factuality=larg.getFactuality();
//                            polarity=larg.getPolarity();
                        }
                        out.write("\t\t\t\t<position>"+pos+"</position>\n");
                        out.write("\t\t\t\t<part1>"+part+"</part1>\n");
                        out.write("\t\t\t\t<sup1>"+sup+"</sup1>\n");
                        out.write("\t\t\t\t<opt>"+opt+"</opt>\n");
                        
                        if(larg != null)
                        {
                            attribSpan = larg.getAttribSpan();
                            
                            if(larg != null && attribSpan != null);
                                out.write("\t\t\t<AttribSpan>"+ attribSpan[0] + " "  + attribSpan[1] +"</AttribSpan>\n");                            
                        }
//                        out.write("\t\t\t\t<source>"+source+"</source>\n");
//                        out.write("\t\t\t\t<factuality>"+factuality+"</factuality>\n");
//                        out.write("\t\t\t\t<polarity>"+polarity+"</polarity>\n");
                        out.write("\t\t\t</arg1>\n");
                        out.write("\t\t\t<arg2>\n");
                        pos="";
                        part="";
                        if(rarg==null)
                        {
                            pos=null;
                            part=null;
                            opt=null;
                            sup=null;
                            source=null;
                            factuality=null;
                            polarity=null;
                            
                        }
                        else
                        {
                            int b[]=rarg.getPos();
                            for(int k=0;k<b.length;k++)
                                pos=pos+String.valueOf(b[k])+" ";
                            int c[]=rarg.getsupInfo();
                            sup="";
                            if(c==null)
                                sup=null;
                            else
                            {
                                for(int k=0;k<c.length;k++)
                                   sup=sup+String.valueOf(c[k])+" "; 
                            }
                            p=rarg.getPart();
                             if(p==null)
                                 part=null;
                             else
                            {
                              for(int k=0;k<p.length;k++)
                                   part=part+String.valueOf(p[k])+" ";    
                            }
                            opt=rarg.getoptInfo();
//                            source=rarg.getSource();
//                            factuality=rarg.getFactuality();
//                            polarity=rarg.getPolarity();
                        }
                        out.write("\t\t\t\t<position>"+pos+"</position>\n");
                        out.write("\t\t\t\t<part2>"+part+"</part2>\n");
                        out.write("\t\t\t\t<sup2>"+sup+"</sup2>\n");
                        out.write("\t\t\t\t<opt>"+opt+"</opt>\n");
                        
                        if(rarg != null)
                        {
                            attribSpan = rarg.getAttribSpan();
                            
                            if(attribSpan != null)
                                out.write("\t\t\t<AttribSpan>"+ attribSpan[0] + " "  + attribSpan[1] +"</AttribSpan>\n");                            
                        }
//                        out.write("\t\t\t\t<source>"+source+"</source>\n");
//                        out.write("\t\t\t\t<factuality>"+factuality+"</factuality>\n");
//                        out.write("\t\t\t\t<polarity>"+polarity+"</polarity>\n");
                        out.write("\t\t\t</arg2>\n");
                        out.write("\t\t</instance>\n");
                    }
                    out.write("\t</connective>\n");
                }
                out.write("</story>");
                out.close();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    
    //added by nrapesh
 //checks if the above opened file has been already annotated or not by checking for the xml file
    private void xmlcheck(String textFile)
    {
        if(textFile.equals(""))
            return;
        
        int checkos=0;
        File entryfile=new File(textFile);
        String entryfilepath=entryfile.getAbsolutePath();
        int lastindex =entryfilepath.lastIndexOf("/");
        //added to check for windows
        if(lastindex==-1)
        {
            lastindex=entryfilepath.lastIndexOf("\\");
            checkos=1;
        }
        String entryfiledir=entryfilepath.substring(0,lastindex);
        String xmlfile;
        int xmlindex=entryfilepath.lastIndexOf(".");
        if(xmlindex!=-1)
            xmlfile=entryfilepath.substring(lastindex+1,xmlindex)+".xml";
        else
            xmlfile=entryfilepath.substring(lastindex+1)+".xml";
        File directory = new File(entryfiledir);
        String[] chld = directory.list();
        boolean done=false;
        if(chld == null)
        {
            System.out.println(GlobalProperties.getIntlString("Specified_directory_does_not_exist_or_is_not_a_directory."));
            System.exit(0);
        }
        else
        {
            for(int i = 0; i < chld.length; i++)
            {
                String fileName = chld[i];
                if(fileName.compareTo(xmlfile)==0)
                {
                    done=true;
                    //System.out.println(fileName);
                }
            }
            if(checkos==0)
            {   
                xmlfile=entryfiledir+"/"+xmlfile;
            }
            if(checkos==1)
            {
                xmlfile=entryfiledir+"\\"+xmlfile;
            }
            if(done==true)
            {
                xmlToTree(info,xmlfile);
            }
        }
        
    }
    //added by nrapesh
    
     private void finalwork(String textfile)
    {
        try
        {
            int checkos=0;
            File entryfile=new File(textFile);
           fileinitopened=textFile;
            String entryfilepath=entryfile.getAbsolutePath();
            int lastindex=entryfilepath.lastIndexOf("/");
            if(lastindex==-1){
                lastindex=entryfilepath.lastIndexOf("\\");
                checkos=1;
            }
            String entryfiledir=entryfilepath.substring(0,lastindex);
            String xmlfile;
            int xmlindex=entryfilepath.lastIndexOf(".");
            if(xmlindex!=-1)
                    xmlfile=entryfilepath.substring(lastindex+1,xmlindex)+".xml";
            else
                    xmlfile=entryfilepath.substring(lastindex+1)+".xml";
            File directory = new File(entryfiledir);
            String[] chld = directory.list();
            boolean done=false;
            if(chld == null)
            {
                    System.out.println(GlobalProperties.getIntlString("Specified_directory_does_not_exist_or_is_not_a_directory."));
                    System.exit(0);
            }
            else
            {
                    for(int i = 0; i < chld.length; i++)
                    {
                         String fileName = chld[i];
                         if(fileName.compareTo(xmlfile)==0)
                         {
                             done=true;
                         }
                                
                     }
            }
            if(checkos==0)
            {
                xmlfile=entryfiledir+"/"+xmlfile;
            }
            if(checkos==1){
                xmlfile=entryfiledir+"\\"+xmlfile;
            }
            File toremove=new File(xmlfile);
            if(done==true)
            {
                  toremove.delete();

            }
            boolean check=toremove.createNewFile();
            if(check==true)
            {
            }
            else
            {
                textEditorJPanel.logJTextArea.append(GlobalProperties.getIntlString("Could_not_create_xml_data_file._Check_if_you_have_write_permissions_to_the_directory_where_the_text_file_is_saved\n"));
            }
            writeToFile(xmlfile);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    } 

     //added by nrapesh
        //removes a connective node from the connective
     private void Removenode(String selectedoption){
		TreePath currentSelection =connectiveJTree.getSelectionPath();
		if (currentSelection!= null) 
		{
			SSFPhrase selectednode=(SSFPhrase)connectiveJTree.getLastSelectedPathComponent();
			DefaultTreeModel treeModel=(DefaultTreeModel)connectiveJTree.getModel();
			selectedoption=selectednode.toString();
			if(selectednode.isRoot()==true)
			{
				JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("RootNode_cannot_be_Removed."),GlobalProperties.getIntlString("Attention!"),JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				SSFPhrase parent=(SSFPhrase)selectednode.getParent();
				String name=parent.getName();
				if(name.compareTo(GlobalProperties.getIntlString("Story"))==0)
				{
					int x=parent.getIndex(selectednode);
					info.removeChild(x);
                                        parent.removeChild(x);
                                      //  treeModel.removeNodeFromParent(selectednode);
                                        treeModel.reload();
				}
				else
				{
					//JOptionPane.showMessageDialog(this,"LeafNode cannot be Removed.","Attention!",JOptionPane.INFORMATION_MESSAGE);
                                    int x=0,y=0;
                                   
                                   // TreeNode temp1= (javax.swing.tree.TreeNode)connectiveJTree.getLastSelectedPathComponent();
//			            lastSelect= (SSFNode) parent;
//			            Lastselect= (SSFNode) selectednode;
			            TreeNode temp2;
			            y=parent.getLevel();
			            
                                    temp2=selectednode.getParent();
				    x=temp2.getIndex(selectednode);
			            TreeNode temp3=temp2.getParent();
			            int p=temp3.getIndex(temp2);
				    AnnotationInfo node=info.getChild(p);
                                    SSFPhrase root = (SSFPhrase)temp3;
                                    if(node.NumOfChildren()==1)
                                    {
                                        node.removeChild(x);
                                        parent.removeChild(x);
                                       // treeModel.reload();
                                        
                                        info.removeChild(p);
                                        root.removeChild(p);
				        treeModel.reload();
                                    }
                                    else
                                    {
                                      node.removeChild(x);
                                      parent.removeChild(x);
				      treeModel.reload();
                                    }
                                    
                                    String selection = "Root";
                                    addInfoTable("", selection);
                                }

			}

		}
	}
     
     private void Removeinfo(String selectedoption){
	}

     public void connectiveJTreeMouseClicked(java.awt.event.MouseEvent evt){

	}

//Added by Nrapesh
  private SSFPhrase addObject(SSFPhrase connectiveJParent,String connective,boolean ShouldBeVisible){
	SSFPhrase connectiveJChild = null;
        try {
            connectiveJChild = new SSFPhrase("0", "", connective, "", connective);
        } catch (Exception ex) {
            Logger.getLogger(DiscourseArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
	if(connectiveJParent==null){
            try {
                connectiveJRoot = new SSFPhrase("0", "", GlobalProperties.getIntlString("Story"), "", GlobalProperties.getIntlString("Story"));
            } catch (Exception ex) {
                Logger.getLogger(DiscourseArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            connectiveJParent=connectiveJRoot;
	}
        connectiveJParent.add(connectiveJChild);
//	connectiveJTreeModel.insertNodeInto(connectiveJChild,connectiveJParent,connectiveJParent.getChildCount());
	if (ShouldBeVisible) {
		connectiveJTree.scrollPathToVisible(new TreePath(connectiveJChild.getPath()));
	}
	return connectiveJChild;
}

    public Frame getOwner() {
        return owner;
    }

    public void setOwner(Frame f) {
        owner = (JFrame) f;
    }

    public void setDialog(JDialog d) {
        dialog = d;
    }    
    public String getDisplayedFile(EventObject e) {
        return textFile;
    }

    public String getCharset(EventObject e) {
        return charset;
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

  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu annotationJPopupMenu;
    private javax.swing.JMenu arg1Menu;
    private javax.swing.JMenu arg2Menu;
    public javax.swing.JCheckBox caseJCheckBox;
    private javax.swing.JPanel connectiveInfoJPanel;
    private javax.swing.JLabel connectiveJLabel;
    private javax.swing.JTextField connectiveJTextField;
    private javax.swing.JPanel connnectiveJPanel;
    private javax.swing.JButton findConnectiveJButton;
    private javax.swing.JButton freezeJButton;
    private javax.swing.JPanel lowerJPanel;
    private javax.swing.JMenu moreMenu;
    // End of variables declaration//GEN-END:variables
}
