/*
 * DiscourseArgOptionalJPanel.java
 *
 * Created on June 11, 2008, 10:17 AM
 */
package sanchay.corpus.span;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.JFileChooser;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.gui.SSFTreeCellRendererNew;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;

import sanchay.gui.common.FileDisplayer;
import sanchay.gui.common.PopupListener;
import sanchay.properties.KeyValueProperties;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.text.editor.gui.TextEditorJPanel;
import sanchay.tree.SanchayTreeModel;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.util.UtilityFunctions;
import sanchay.util.file.FileMonitor;
import sanchay.util.file.SanchayBackup;
import sanchay.gui.common.SanchayLanguages;
import sanchay.corpus.span.SpanAnnotationParentNode;
import sanchay.properties.PropertiesManager;
import sanchay.common.types.PropertyType;
import sanchay.properties.PropertyTokens;
import sanchay.table.gui.DisplayEvent;

/**
 *This is the DiscourseArgOptionalJpanel.
 * Added By Sundeep Kumar Mishra
 * Added By Shrikant Baronia
 * 
 */
public class SpanArgOptionalJPanel extends javax.swing.JPanel implements WindowListener, FileDisplayer {
    
    
    
    private JFrame owner;
    JFrame keyframe;
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
    SSFPhrase rootNode;    
    protected UndoAction undoAction;
    protected RedoAction redoAction;
    protected UndoManager undo = new UndoManager();
    private boolean commands[];
    private Action actions[];
   
    private boolean commandButtonsShown;
    
    int appliedCommands[]=null;
    int appliedMoreCommands[];
    JDialog discourseConnectiveInfoDialog;    
    protected boolean boolmenuItem[] = new boolean[15];
    
    private SSFPhrase connectiveJRoot;                        //root of the tree containing the instances of connective
    private SanchayTreeModel connectiveJTreeModel;             //tree model for the tree containing the instances of connective

    private Toolkit toolkit = Toolkit.getDefaultToolkit();      //the defualt toolkit
    private JMenuItem menuItem[];                               //menuitem containing the options for the connective tree
    private String fileinitopened = null;                         //containing the name of the file opened

   
    private DefaultHighlighter.DefaultHighlightPainter painter;   //painter for highlighting 
    private SSFNode Lastselect;                                  //storefor the last selected node from the tree     
    private SSFNode lastSelect;                                //stores the last selected node from the tree
    private SpanAnnotationParentNode info;                                  //stores the root of the data structure
    private SpanAnnotationParentNode child;
    private String tpath;                                     //stores the string representing the path in the optional tree
    private SpanAnnotationParentNode argRoot;                                    //root of the optional tags tree
    private DefaultTreeModel model1;                                    //root of the optional information tree for arguments
    private DefaultTreeModel model2;                             //root of the optional information tree for the supplements
    private DefaultTreeModel model3;                             //root of the optional information tree for the connectives
   
    private String textFile;
    private String charset;
    SpanAnnotationLeafNode larg;
    SpanAnnotationLeafNode rarg;
    SpanAnnotationJPanel mainJPanel;
    int Global_Flag=0;
    SpanAnnotationParentNode Global_node;
    int Global_keys=0,Global_tokens=0,Global_connective=0;

    /** Creates new form DiscourseArgOptionalJPanel */
    public SpanArgOptionalJPanel(String lancEnc, TextEditorJPanel editorJPanel, SpanAnnotationJPanel mainJPanel) {

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

       

        loadState(this);

       
        tpath = null;
        lastSelect = null;
        Lastselect = null;
        info = new SpanAnnotationParentNode(GlobalProperties.getIntlString("Story"));
     
        fileinitopened = null;

        connectiveInfoTableModel = new SanchayTableModel(new String[]{GlobalProperties.getIntlString("Feature"), GlobalProperties.getIntlString("Value")}, 0);

      

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
        String langCode = langEnc;
                //Making panel for connectives and its instances
        try {
            connectiveJRoot = new SSFPhrase("0", "", GlobalProperties.getIntlString("Story"), "", GlobalProperties.getIntlString("Story"));
        } catch (Exception ex) {
            Logger.getLogger(SpanArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        connectiveInfoTreeJPanel = SanchayTreeJPanel.createSSFDisplayJPanel(connectiveJRoot, 3, new String[] {langCode, langCode, langCode});

        connectiveJTreeModel = connectiveInfoTreeJPanel.getModel();
        connectiveJTree = connectiveInfoTreeJPanel.getJTree();

        connectiveJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {

            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                connectiveJTreeValueChanged(evt);
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

        moreMenu.setText("More...");
        annotationJPopupMenu.add(moreMenu);

        arg1Menu.setText("Arguement1..");
        annotationJPopupMenu.add(arg1Menu);

        arg2Menu.setText("Arguement2..");
        annotationJPopupMenu.add(arg2Menu);

        setLayout(new java.awt.BorderLayout());

        lowerJPanel.setLayout(new java.awt.BorderLayout());

        connnectiveJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        connectiveJLabel.setLabelFor(connectiveJTextField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
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

    
    
    
    
    
    private void initDocument()
    {
        textEditorJPanel.initDocument();
        

        int noofchild=connectiveJRoot.getChildCount();
        
        for(int i=0;i<noofchild;i++){
                SSFNode childNode =(SSFNode)connectiveJRoot.getChildAt(0);
                connectiveJRoot.removeChild(0);

        }
        info=new SpanAnnotationParentNode(GlobalProperties.getIntlString("Story"));
        fileinitopened=textFile; 
        xmlcheck(textFile);  
        
        connectiveJTreeModel.reload();
    }

    
    
    
    
    
    
    
    
private void findConnectiveJButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                      
                                                    
    
    
//GEN-FIRST:event_findConnectiveJButtonActionPerformed
                                                    
    
    
    String story=connectiveJTextField.getText();
     connectiveJTextField.setText(null);
        String connective=story;
        SSFNode connectiveJChild;
        SpanAnnotationParentNode node;
        SpanAnnotationParentNode instance;
        SpanAnnotationParentNode con;
        int pos[];
        int count = connectiveJTreeModel.getChildCount(connectiveJRoot), j;

        try {
            connectiveJChild = new SpanAnnotationParentNode("0", "", connective, "", connective);
        } catch (Exception ex) {
            Logger.getLogger(SpanArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex); 
        }

        for (j = 0; j < count; j++) {
            DefaultMutableTreeNode temp = (DefaultMutableTreeNode) connectiveJTreeModel.getChild(connectiveJRoot, j);
            String tmp1 = temp.toString();//.toLowerCase();
   
            if (tmp1.compareTo(connective) == 0) { 
      
                con = info.getChild(j);
               
                break;
            }
        }
        String conn=connective;
        if (j >= count) {
            if (connective.isEmpty() == false) {
           
                connectiveJChild =addObject(connectiveJRoot, conn, true);
                node = new SpanAnnotationParentNode(conn);
                node.setParent(info);
                info.addChild1(node);
                
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
                            addObject((SSFPhrase)connectiveJChild,ch,false);
                            instance = new SpanAnnotationParentNode(ch);
                            instance.setPos(pos);
                            instance.setParent(node);
                            node.addChild1(instance);

                        }
                    } else {
                        i++;
                    }

                }

            }
            
        }
        
        connectiveJTreeModel.reload();
    
 
    
    
    
   
}//GEN-LAST:event_findConnectiveJButtonActionPerformed

private void connectiveJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectiveJTextFieldActionPerformed

}//GEN-LAST:event_connectiveJTextFieldActionPerformed

private void caseJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_caseJCheckBoxActionPerformed

}//GEN-LAST:event_caseJCheckBoxActionPerformed

private void freezeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freezeJButtonActionPerformed
// TODO add your handling code here:
    freeze(evt);
}//GEN-LAST:event_freezeJButtonActionPerformed

//action performed when a new node is selected in the connectiveJTree
private void connectiveJTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {   
    
    
        Highlighter h=textJTextArea.getHighlighter();
	h.removeAllHighlights();
        

        String str = new String();
        String selection = new String();
    
    
        int numrows=connectiveInfoTableModel.getRowCount();
        for(int i=numrows-1;i>=0;i--)
            connectiveInfoTableModel.removeRow(i);
    
        
	if(textJTextArea.isEditable() == false)
	{
		
            TreePath currentSelection =connectiveJTree.getSelectionPath();
              if(currentSelection==null)
		{
		}
            
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
                            selection = "Root";
                            addInfoTable(str,selection);

			}
			if(parent.isLeaf()==false && parent.isRoot()==false && y==1)
			{
				
				
				SpanAnnotationParentNode node=info.getChild(x);
				SpanAnnotationParentNode childe;
				for(int i=0;i<node.NumOfChildren();i++)
				{
					childe=node.getChild(i);
					int a[]=childe.getPos(),b[]=null;
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
						logJTextArea.append("\n");
                                                String temp;
                                                temp="hi";
                                                if(temp!=null)
                                                {
                                                   logJTextArea.append(GlobalProperties.getIntlString("Optional_Tags_:_")+temp+"\n");
                                                }
					}

				}
                              selection = GlobalProperties.getIntlString("Connective");
                            addInfoTable(str,selection);  
                            genMenuconnective(node);

			}
			if(parent.isLeaf()==true && parent.isRoot()==false && y==2)
			{
				temp2=temp1.getParent();
				TreeNode temp3=temp2.getParent();
				int p=temp3.getIndex(temp2);
				SpanAnnotationParentNode node=info.getChild(p);
				SpanAnnotationParentNode childe=node.getChild(x);
				highlight(childe);
                                genMenu(childe);
                        }
                }
        }
	else
	{
		JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Please_freeze_the_text_inorder_to_do_any_processing_on_it."),GlobalProperties.getIntlString("Attention!"),JOptionPane.INFORMATION_MESSAGE);
	}

}


        
        
        
        
    public void setTextEditorJPanel(TextEditorJPanel textEditorJPanel)
    {
        this.textEditorJPanel = textEditorJPanel;
        textJTextArea = textEditorJPanel.textJTextArea;
        logJTextArea = textEditorJPanel.logJTextArea;
    }
    
    
    public void setArg1(EventObject e)
    {  
        String selectedoption = new String("Arg1");
        if(textJTextArea.isEditable()==true)
    {
        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),GlobalProperties.getIntlString("Attention"),JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
    {
        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),GlobalProperties.getIntlString("Attention"),JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    String text=textJTextArea.getSelectedText();
    if(text==null)
    {
        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),GlobalProperties.getIntlString("Attention"),JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    String story=textJTextArea.getText();
    int index=textJTextArea.getSelectionStart();
        int off=textJTextArea.getSelectionEnd(),l=off-index;

    String name=lastSelect.getUserObject().toString();
       
    TreeNode parent=Lastselect.getParent();
    TreeNode root=parent.getParent();
    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
    SpanAnnotationParentNode node=info.getChild(x);
    name=node.getProp("name",PropertyType.KEY_VALUE_PROPERTIES);
       

    
    SpanAnnotationParentNode instance=node.getChild(y);
      
    Highlighter h=textJTextArea.getHighlighter();
    this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
    
    PropertiesManager propman=Global_node.getPropman();
    PropertiesManager arg1_propman=new PropertiesManager();
    arg1_propman=(PropertiesManager)propman.getPropertyContainer("arg1-props",PropertyType.PROPERTIES_MANAGER);
    KeyValueProperties kvp=(KeyValueProperties)arg1_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
    Iterator enu=kvp.getPropertyKeys();
    String key1=(String)enu.next();
    kvp.addProperty(key1," ");    
    arg1_propman.addPropertyContainer("keyvalue-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
    propman.addPropertyContainer("arg1-props",arg1_propman,PropertyType.PROPERTIES_MANAGER);
    Global_node.setPropman(propman);
    
    int a[]={index,index+l};
        try
        {
            h.addHighlight(index,index+l,this.painter);
        }catch(BadLocationException ble)
        {
            textEditorJPanel.logJTextArea.append(GlobalProperties.getIntlString("Error:_Couldn't_Highlight_text\n"));
        }
        
    
        genMenu(instance);
        addInfoTable(text,selectedoption);
        
    }
    
public void setArg2(EventObject e)
    {
        
        String selectedoption = new String("Arg2");
        if(textJTextArea.isEditable()==true)
    {
        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("In_order_to_process_the_text_you_need_to_freeze_it"),GlobalProperties.getIntlString("Attention"),JOptionPane.INFORMATION_MESSAGE);
        return; 
    }
    if(lastSelect==null || lastSelect.isLeaf()==false || lastSelect.isRoot()==true)
    {
        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("You_must_choose_an_instance_of_a_connective_in_order_to_set_its_arguments"),GlobalProperties.getIntlString("Attention"),JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    String text=textJTextArea.getSelectedText();
    if(text==null)
    {
        JOptionPane.showMessageDialog(this,GlobalProperties.getIntlString("No_text_selected"),GlobalProperties.getIntlString("Attention"),JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    String story=textJTextArea.getText();
    int index=textJTextArea.getSelectionStart();
        int off=textJTextArea.getSelectionEnd(),l=off-index;

    String name=lastSelect.getUserObject().toString();
        
    TreeNode parent=Lastselect.getParent();
    TreeNode root=parent.getParent();
    int x=root.getIndex(parent),y=parent.getIndex(Lastselect);
    SpanAnnotationParentNode node=info.getChild(x);
    name=node.getProp("name",PropertyType.KEY_VALUE_PROPERTIES);
        

        
    SpanAnnotationParentNode instance=node.getChild(y);
       
    Highlighter h=textJTextArea.getHighlighter();
    this.painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
        
    PropertiesManager propman=Global_node.getPropman();
    PropertiesManager arg1_propman=new PropertiesManager();
    arg1_propman=(PropertiesManager)propman.getPropertyContainer("arg2-props",PropertyType.PROPERTIES_MANAGER);
    KeyValueProperties kvp=(KeyValueProperties)arg1_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
    Iterator enu=kvp.getPropertyKeys();
    String key1=(String)enu.next();
    kvp.addProperty(key1," ");    
    arg1_propman.addPropertyContainer("keyvalue-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
    propman.addPropertyContainer("arg2-props",arg1_propman,PropertyType.PROPERTIES_MANAGER);
    Global_node.setPropman(propman);
    
    

        int a[]={index,index+l};
        try
        {
            h.addHighlight(index,index+l,this.painter);
        }catch(BadLocationException ble)
        {
            textEditorJPanel.logJTextArea.append(GlobalProperties.getIntlString("Error:_Couldn't_Highlight_text\n"));
        }
        genMenu(instance);
        addInfoTable(text,selectedoption);
    }
    
    
    public void removeArg1(ActionEvent e)
    {
         PropertiesManager propman=Global_node.getPropman();
        PropertiesManager arg1_propman=new PropertiesManager();
        arg1_propman=(PropertiesManager)propman.getPropertyContainer("arg1-props",PropertyType.PROPERTIES_MANAGER);
        
        KeyValueProperties kvp=(KeyValueProperties)arg1_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        Iterator enu=kvp.getPropertyKeys();
        while(enu.hasNext())
        {   String key=(String)enu.next();
            removeInfoTable("arg1 "+key);
            kvp.addProperty(key,"");
        }
        PropertyTokens arg1_pt=new PropertyTokens();
        arg1_pt=(PropertyTokens)arg1_propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
        int arg1_no_tokens=arg1_pt.countTokens();
        int arg1_index1=0;
        while(arg1_index1<arg1_no_tokens)
        {   String token=arg1_pt.getToken(arg1_index1);
            removeInfoTable("arg1 "+token);
            arg1_index1=arg1_index1+1;
        }
        removeInfoTable("Arg1");
            arg1_propman.addPropertyContainer("keyvalue-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
            propman.addPropertyContainer("arg1-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
            Global_node.setPropman(propman);
            genMenu(Global_node);
    }
    public void removeArg2(ActionEvent e)
    {
         PropertiesManager propman=Global_node.getPropman();
            PropertiesManager arg2_propman=new PropertiesManager();
        arg2_propman=(PropertiesManager)propman.getPropertyContainer("arg2-props",PropertyType.PROPERTIES_MANAGER);
        
        KeyValueProperties kvp=(KeyValueProperties)arg2_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        Iterator enu=kvp.getPropertyKeys();
        while(enu.hasNext())
        {
            String key=(String)enu.next();
            removeInfoTable("arg2 "+key);
            kvp.addProperty(key,"");
        }
        PropertyTokens arg2_pt=new PropertyTokens();
        arg2_pt=(PropertyTokens)arg2_propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
        int arg2_no_tokens=arg2_pt.countTokens();
        int arg2_index1=0;
        while(arg2_index1<arg2_no_tokens)
        {   String token=arg2_pt.getToken(arg2_index1);
            removeInfoTable("arg2 "+token);
             arg2_index1=arg2_index1+1;
        }
        removeInfoTable("Arg2");
            arg2_propman.addPropertyContainer(GlobalProperties.getIntlString("keyvalue-props"),kvp,PropertyType.KEY_VALUE_PROPERTIES);
            propman.addPropertyContainer(GlobalProperties.getIntlString("arg2-props"),kvp,PropertyType.KEY_VALUE_PROPERTIES);
            Global_node.setPropman(propman);
            genMenu(Global_node);
    }
    public void removeConnective(ActionEvent e)
    {
        
                String selectedoption=GlobalProperties.getIntlString("RemoveConnective");
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

                                int x=parent.getIndex(selectednode);
					
					info.removeChild1(x);
                                        parent.removeChild(x);

                                        treeModel.reload();
  
                                    String selection = GlobalProperties.getIntlString("Root");
                                    addInfoTable("", selection);
			}
	}  
                
    }
    public void removeInstance(ActionEvent e)
    {
        String selectedoption=GlobalProperties.getIntlString("RemoveConnective");
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
				String name=parent.toString();
				if(name.compareTo(GlobalProperties.getIntlString("Story"))==0)
				{

					int x=parent.getIndex(selectednode);
					info.removeChild1(x);
                                        parent.removeChild(x);

                                        treeModel.reload();
				}
				else
				{

                                    int x=0,y=0;

			            TreeNode temp2;
			            y=parent.getLevel();
			            
                                    temp2=selectednode.getParent();
				    x=temp2.getIndex(selectednode);
			            TreeNode temp3=temp2.getParent();
			            int p=temp3.getIndex(temp2);
				    SpanAnnotationParentNode node=info.getChild(p);
                                    SSFPhrase root = (SSFPhrase)temp3;
                                    if(node.NumOfChildren()==1)
                                    {
                                        node.removeChild1(x);
                                        parent.removeChild(x);

                                        
                                        info.removeChild1(p);
                                        root.removeChild(p);
				        treeModel.reload();
                                    }
                                    else
                                    {
                                      node.removeChild1(x);
                                      parent.removeChild(x);
				      treeModel.reload();
                                    }
                                    
                                    String selection = GlobalProperties.getIntlString("Root");
                                    addInfoTable("", selection);
                                }

			}

	}      
        if(Global_Flag==1)
        {
            for(int i=0;i<Global_keys+Global_tokens+3;i++)
                        annotationJPopupMenu.remove(annotationJPopupMenu.getComponent(8));
            
            Global_Flag=0;
        }
    }
    
    public void genMenuconnective(SpanAnnotationParentNode instance)
    {
        if(Global_connective==1)
        {
            annotationJPopupMenu.remove(annotationJPopupMenu.getComponent(8));
            Global_connective=0;
        }
        if(Global_Flag==1)
        {
            for(int i=0;i<Global_keys+Global_tokens+3;i++)
                        annotationJPopupMenu.remove(annotationJPopupMenu.getComponent(8));
            Global_Flag=0;
        }
       javax.swing.JMenuItem remove_conn = new javax.swing.JMenuItem();
        remove_conn.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e){
                       removeConnective(e);
        } 
         });
        remove_conn.setText(GlobalProperties.getIntlString("remove_Connective"));
        annotationJPopupMenu.add(remove_conn);
        Global_connective=1;
    }
    public void genMenu(SpanAnnotationParentNode instance)
    {
               // AnnotationInfo object is passed here
        javax.swing.JMenu remove=new javax.swing.JMenu(); 
        if(Global_connective==1)
        {
            annotationJPopupMenu.remove(annotationJPopupMenu.getComponent(8));
            Global_connective=0;
        }
        Global_node=instance;
         int sum=Global_keys+Global_tokens+3;

        if(Global_Flag==1)
        {
            for(int i=0;i<Global_keys+Global_tokens+3;i++)
                        annotationJPopupMenu.remove(annotationJPopupMenu.getComponent(8));
        }
         
        PropertiesManager propman=Global_node.getPropman();
        KeyValueProperties kvp=new KeyValueProperties();
        kvp=(KeyValueProperties)propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        Iterator enu=kvp.getPropertyKeys();
        int key_count=0;
        
        while(enu.hasNext())
        {
            key_count++;
            String keyname=(String)enu.next();
            String value=kvp.getPropertyValue(keyname);
            addInfoTable(value,keyname);
            javax.swing.JMenuItem keyMenu = new javax.swing.JMenuItem();
        keyMenu.addActionListener(new java.awt.event.ActionListener(){
		    public void actionPerformed(ActionEvent e) {

        PropertiesManager propman=Global_node.getPropman();
        KeyValueProperties kvp=new KeyValueProperties();
        kvp=(KeyValueProperties)propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        String keyname=e.getActionCommand();
        String value=kvp.getPropertyValue(keyname);
        
        String  input = (String)JOptionPane.showInputDialog(null,GlobalProperties.getIntlString("Edit_the_value:"), GlobalProperties.getIntlString("Value_of_the_Property"),
        JOptionPane.PLAIN_MESSAGE,null,null,value);
        
            
        if(input!=null)
        {
        kvp.addProperty(keyname,input);
        propman.addPropertyContainer("keyvalue-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
        Global_node.setPropman(propman);
        addInfoTable(input,keyname);
        }

		    }
        });
        
        keyMenu.setText(keyname);
        annotationJPopupMenu.add(keyMenu);
        }
        Global_keys=key_count;
        
        Global_tokens=0;
        
        PropertyTokens pt=new PropertyTokens();
        pt=(PropertyTokens)propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
        int no_tokens=pt.countTokens();
        int index1=0;
        Global_tokens=no_tokens;

        while(index1<no_tokens)
        {   
            String token=pt.getToken(index1);

            addInfoTable("",token);
            javax.swing.JMenuItem tokenMenu = new javax.swing.JMenuItem();
            tokenMenu.addActionListener(new java.awt.event.ActionListener(){
		    public void actionPerformed(ActionEvent e) {

            PropertiesManager propman=Global_node.getPropman();
            PropertyTokens pt=new PropertyTokens();
            pt=(PropertyTokens)propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
            String tokenname=e.getActionCommand();
            
            String  input = (String)JOptionPane.showInputDialog(null,GlobalProperties.getIntlString("Edit_the_value:"), GlobalProperties.getIntlString("Value_of_the_Property"),
            JOptionPane.PLAIN_MESSAGE,null,null,null);
        
            if(input!=null)
               {
                addInfoTable(input,tokenname);
                }

		    }
                });
                index1++;
        tokenMenu.setText(token);
        annotationJPopupMenu.add(tokenMenu);
       
        }
        
        PropertiesManager arg1_propman=new PropertiesManager();
        arg1_propman=(PropertiesManager)propman.getPropertyContainer("arg1-props",PropertyType.PROPERTIES_MANAGER);
        kvp=(KeyValueProperties)arg1_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        
        if(kvp.isEmpty()==0){
        javax.swing.JMenu argMenu1 = new javax.swing.JMenu();
        
        arg1_propman=(PropertiesManager)propman.getPropertyContainer("arg1-props",PropertyType.PROPERTIES_MANAGER);
        kvp=(KeyValueProperties)arg1_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        
        enu=kvp.getPropertyKeys();
        while(enu.hasNext())
        {   
            String keyname=(String)enu.next();
            javax.swing.JMenuItem keyMenu = new javax.swing.JMenuItem();
            String arg1_keyvalue1=kvp.getPropertyValue(keyname);
            addInfoTable(arg1_keyvalue1,"arg1 "+keyname);
            keyMenu.addActionListener(new java.awt.event.ActionListener(){
		    public void actionPerformed(ActionEvent e) {

        PropertiesManager propman=Global_node.getPropman();
        KeyValueProperties kvp=new KeyValueProperties();
        PropertiesManager arg1_propman=new PropertiesManager();
        arg1_propman=(PropertiesManager)propman.getPropertyContainer("arg1-props",PropertyType.PROPERTIES_MANAGER);
        kvp=(KeyValueProperties)arg1_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        String keyname=e.getActionCommand();
        String value=kvp.getPropertyValue(keyname);
        
        String  input = (String)JOptionPane.showInputDialog(null,GlobalProperties.getIntlString("Edit_the_value:"), GlobalProperties.getIntlString("Value_of_the_Property"),
        JOptionPane.PLAIN_MESSAGE,null,null,value);
        
            
        if(input!=null)
        {       addInfoTable(input,"arg1 "+keyname);
                kvp.addProperty(keyname,input);
                arg1_propman.addPropertyContainer("keyvalue-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
                propman.addPropertyContainer("arg1-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
                Global_node.setPropman(propman);
        }

		    }
        });
        
        keyMenu.setText(keyname);
        argMenu1.add(keyMenu);
        }

            
        PropertyTokens arg1_pt=new PropertyTokens();
        arg1_pt=(PropertyTokens)arg1_propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
        int arg1_no_tokens=arg1_pt.countTokens();
        int arg1_index1=0;

        while(arg1_index1<arg1_no_tokens)
        {   String token=arg1_pt.getToken(arg1_index1);

            addInfoTable("","arg1 "+token);
            javax.swing.JMenuItem tokenMenu = new javax.swing.JMenuItem();
            tokenMenu.addActionListener(new java.awt.event.ActionListener(){
		    public void actionPerformed(ActionEvent e) {

            PropertiesManager propman=Global_node.getPropman();
            PropertiesManager arg1_propman=new PropertiesManager();
            arg1_propman=(PropertiesManager)propman.getPropertyContainer("arg1-props",PropertyType.PROPERTIES_MANAGER);
            PropertyTokens pt=new PropertyTokens();
            pt=(PropertyTokens)arg1_propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
            String tokenname=e.getActionCommand();
            
            String  input = (String)JOptionPane.showInputDialog(null,GlobalProperties.getIntlString("Edit_the_value:"), GlobalProperties.getIntlString("Value_of_the_Property"),
            JOptionPane.PLAIN_MESSAGE,null,null,null);
        
            
            if(input!=null)
               {    addInfoTable(input,"arg1 "+ tokenname);
                }

		    }
                });
                arg1_index1++;
        tokenMenu.setText(token);
        annotationJPopupMenu.add(tokenMenu);
        tokenMenu.setText(token);
        argMenu1.add(tokenMenu);
        }
        
        argMenu1.setText(GlobalProperties.getIntlString("Arguement1"));
        annotationJPopupMenu.add(argMenu1);
        
        javax.swing.JMenuItem remove_arg1 = new javax.swing.JMenuItem();
        remove_arg1.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e){
                  removeArg1(e);
        } 
         });
        remove_arg1.setText(GlobalProperties.getIntlString("remove_Argument_1"));
        remove.add(remove_arg1);
    }
         else
     {  
            javax.swing.JMenuItem setarg1 = new javax.swing.JMenuItem();
            setarg1.addActionListener(new java.awt.event.ActionListener(){
                public void actionPerformed(ActionEvent e){
                    setArg1(e);
                }
            });
            setarg1.setText(GlobalProperties.getIntlString("set_Argument_1"));
            annotationJPopupMenu.add(setarg1);
            
          
     }
        
        
        javax.swing.JMenu  argMenu2= new javax.swing.JMenu();
        PropertiesManager arg2_propman=new PropertiesManager();
        arg2_propman=(PropertiesManager)propman.getPropertyContainer("arg2-props",PropertyType.PROPERTIES_MANAGER);
        kvp=(KeyValueProperties)arg2_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        if(kvp.isEmpty()==0)
        {
        arg2_propman=new PropertiesManager();
        arg2_propman=(PropertiesManager)propman.getPropertyContainer("arg2-props",PropertyType.PROPERTIES_MANAGER);
        kvp=(KeyValueProperties)arg2_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        enu=kvp.getPropertyKeys();
        while(enu.hasNext())
        {
            String keyname=(String)enu.next();
            javax.swing.JMenuItem keyMenu = new javax.swing.JMenuItem();
            String arg2_keyvalue1=kvp.getPropertyValue(keyname);
            addInfoTable(arg2_keyvalue1,"arg2 "+keyname);
            keyMenu.addActionListener(new java.awt.event.ActionListener(){
		    public void actionPerformed(ActionEvent e) {
        
        PropertiesManager propman=Global_node.getPropman();
        KeyValueProperties kvp=new KeyValueProperties();
        PropertiesManager arg2_propman=new PropertiesManager();
        arg2_propman=(PropertiesManager)propman.getPropertyContainer("arg2-props",PropertyType.PROPERTIES_MANAGER);
        kvp=(KeyValueProperties)arg2_propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        String keyname=e.getActionCommand();
        String value=kvp.getPropertyValue(keyname);
        
        String  input = (String)JOptionPane.showInputDialog(null,GlobalProperties.getIntlString("Edit_the_value:"), "Value of the Property",
        JOptionPane.PLAIN_MESSAGE,null,null,value);
        
            
        if(input!=null)
        {       addInfoTable(input,"arg2 "+keyname);
                kvp.addProperty(keyname,input);
                arg2_propman.addPropertyContainer("keyvalue-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
                propman.addPropertyContainer("arg2-props",kvp,PropertyType.KEY_VALUE_PROPERTIES);
                Global_node.setPropman(propman);
        }
   
		    }
        });
        keyMenu.setText(keyname);
        argMenu2.add(keyMenu);
        }      
        
        PropertyTokens arg2_pt=new PropertyTokens();
        arg2_pt=(PropertyTokens)arg2_propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
        int arg2_no_tokens=arg2_pt.countTokens();
        int arg2_index1=0;
   
        while(arg2_index1<arg2_no_tokens)
        {   String token=arg2_pt.getToken(arg2_index1);
   
            addInfoTable("","arg2 "+token);
            javax.swing.JMenuItem tokenMenu = new javax.swing.JMenuItem();
            tokenMenu.addActionListener(new java.awt.event.ActionListener(){
		    public void actionPerformed(ActionEvent e) {
   
            PropertiesManager propman=Global_node.getPropman();
            PropertiesManager arg2_propman=new PropertiesManager();
            arg2_propman=(PropertiesManager)propman.getPropertyContainer("arg2-props",PropertyType.PROPERTIES_MANAGER);
            PropertyTokens pt=new PropertyTokens();
            pt=(PropertyTokens)arg2_propman.getPropertyContainer(GlobalProperties.getIntlString("tokenvalue-props"),PropertyType.PROPERTY_TOKENS);
            String tokenname=e.getActionCommand();
            
            String  input = (String)JOptionPane.showInputDialog(null,GlobalProperties.getIntlString("Edit_the_value:"), GlobalProperties.getIntlString("Value_of_the_Property"),
            JOptionPane.PLAIN_MESSAGE,null,null,null);
            
            
            if(input!=null)
               {       addInfoTable(input,"arg2 "+tokenname);
                }
 
		    }
                });
                arg2_index1++;
        tokenMenu.setText(token);
        annotationJPopupMenu.add(tokenMenu);
        tokenMenu.setText(token);
        argMenu2.add(tokenMenu);
        }
        argMenu2.setText(GlobalProperties.getIntlString("Arguement2"));
        annotationJPopupMenu.add(argMenu2);
        
         javax.swing.JMenuItem remove_arg2 = new javax.swing.JMenuItem();
        remove_arg2.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e){
                       removeArg2(e);
        } 
         });
        remove_arg2.setText(GlobalProperties.getIntlString("remove_Argument_2"));
        remove.add(remove_arg2);
        }
        else
        { 
            javax.swing.JMenuItem setarg2 = new javax.swing.JMenuItem();
            setarg2.addActionListener(new java.awt.event.ActionListener(){
                public void actionPerformed(ActionEvent e){
                    setArg2(e);
                }
            });
            setarg2.setText(GlobalProperties.getIntlString("set_Argument_2"));
            annotationJPopupMenu.add(setarg2);
           
            
        }
        
       
        
        javax.swing.JMenuItem remove_instance = new javax.swing.JMenuItem();
        remove_instance.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(ActionEvent e){
                       removeInstance(e);
        } 
         });
         remove_instance.setText(GlobalProperties.getIntlString("remove_Instance"));
        remove.add(remove_instance);
        remove.setText(GlobalProperties.getIntlString("remove"));
        annotationJPopupMenu.add(remove);

        
        Global_Flag=1;
        
        
    }

    
    
     public void highlight(SpanAnnotationParentNode child)
    {
                String story=textJTextArea.getText();
                Highlighter h=textJTextArea.getHighlighter();
		SpanAnnotationParentNode larg=null,rarg=null,opt1,opt2;
                String name = child.getProp("name",PropertyType.KEY_VALUE_PROPERTIES);
                logJTextArea.append(GlobalProperties.getIntlString("\nConnective_Node_:_")+child.getProp(GlobalProperties.getIntlString("name"),PropertyType.KEY_VALUE_PROPERTIES)+"\n");
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
					logJTextArea.append(GlobalProperties.getIntlString("Error:_An_exception_occured_while_trying_to_highlight_text\n"));
				}
			}
                        
		}
                
	}
    
    
    
    
  
    
    private void prepareCommands(int appliedCommands[], int appliedMoreCommands[])
    {
	commands = new boolean[SpanAnnotationInterfaceAction._BASIC_ACTIONS_];
	actions = new Action[SpanAnnotationInterfaceAction._BASIC_ACTIONS_];
        


        // Basic action commands
	for(int i = 0; i < commands.length; i++)
	{
	    commands[i] = true;
	    actions[i] = SpanAnnotationInterfaceAction.createAction(this, i);
	}
        
       
	
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
                
                UtilityFunctions.decreaseFontSize(jb, 3);
	    }
	    
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

                
                UtilityFunctions.decreaseFontSize(jb, 3);
	    }
	    
            
	}

        MouseListener popupListener = new PopupListener(annotationJPopupMenu);
	textJTextArea.addMouseListener(popupListener);

        JPopupMenu treePopupMenu = connectiveInfoTreeJPanel.getJPopupMenu();
        

                

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

        if(selectedoption.compareTo(GlobalProperties.getIntlString("ConInfo"))==0)
        {
            panel.add(additionalInfoConTreeJPanel, BorderLayout.CENTER);
            panel.add(buttonJPanel, BorderLayout.SOUTH);

            textEditorJPanel.addJPanelToLeftTabbedPanel(GlobalProperties.getIntlString("Add_Info_for_Connectives"), panel);
            addInfoDialog = new JDialog(getOwner(), GlobalProperties.getIntlString("Add_Info_for_Connectives"), true);
            
           
        }
        else
        {
            panel.add(additionalInfoArgSupTreeJPanel, BorderLayout.CENTER);
            panel.add(buttonJPanel, BorderLayout.SOUTH);

            textEditorJPanel.addJPanelToLeftTabbedPanel(GlobalProperties.getIntlString("Add_Info_for_Arg/Sup"), panel);
            addInfoDialog = new JDialog(getOwner(), GlobalProperties.getIntlString("Add_Info_for_Arg/Sup"), true);
            
            
        }            
               
        jTabbedPane.setSelectedComponent(panel);

        addInfoDialog.add(panel);
        addInfoDialog.pack();



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

    public void freeze(EventObject e)
    {
        if(textEditorJPanel.textJTextArea.isEditable()==true)
		{
			textEditorJPanel.textJTextArea.setEditable(false);

			textEditorJPanel.textJTextArea.setBackground(Color.lightGray);
		}
		else
		{
			JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("The_text_is_already_forzen._You_can_now_process_it"),GlobalProperties.getIntlString("Attention"),JOptionPane.INFORMATION_MESSAGE);
		}

    }
    
    public void newFile(EventObject e)
    {
        textEditorJPanel.newFile(e);
        

        textJTextArea.setBackground(Color.WHITE);
        int noofchild=connectiveJRoot.getChildCount();

        for(int i=0;i<noofchild;i++)
        {
                        SSFPhrase kid=(SSFPhrase)connectiveJRoot.getChildAt(0);
                        connectiveJTreeModel.removeNodeFromParent((SSFPhrase)kid);
        }
        info=new SpanAnnotationParentNode(GlobalProperties.getIntlString("Story"));
        
        String selection = GlobalProperties.getIntlString("Root");
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
				path = stateKVProps.getPropertyValue(GlobalProperties.getIntlString("CurrentDir"));
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


			displayFile(textFile, charset, e);
		}
	}
	catch(Exception ex)
	{
		JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Error_is_in_this_opening_file."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
		ex.printStackTrace();
	}   
    }
    
   
    
    public void displayFile(File file, String charset, EventObject e)
    {
            if(file.isFile() == false || file.exists() == false)
                    return;

            try{


                    initDocument();
                    doc = textEditorJPanel.getDocument();
                    
                    textFile = file.getAbsolutePath();
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
                            logJTextArea.append(GlobalProperties.getIntlString("File_") + textFile + GlobalProperties.getIntlString("_backed_up.\n"));

                            String line = "";

                            Element root = doc.getDefaultRootElement();
                            while((line = lnReader.readLine()) != null)
                            {
                                    doc.insertString(root.getEndOffset() - 1, line + "\n", null);

                            }


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
            textJTextArea.setEditable(false);                
            textJTextArea.setBackground(Color.LIGHT_GRAY);    
    }
    
    
      
    private void xmlcheck(String textFile)
    {
        if(textFile.equals(""))
            return;
        
        int checkos=0;
        File entryfile=new File(textFile);
        String entryfilepath=entryfile.getAbsolutePath();
        int lastindex =entryfilepath.lastIndexOf("/");

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
    
      public static String getCharacterDataFromElement(org.w3c.dom.Element e) {
		org.w3c.dom.Node child = e.getFirstChild();
		if (child instanceof org.w3c.dom.CharacterData) {
			org.w3c.dom.CharacterData cd = (org.w3c.dom.CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
    
    private void xmlToTree(SpanAnnotationParentNode root,String filename)
    {
                File file = new File(filename);
		try {
                    DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(file);
                        SpanAnnotationParentNode conn;
			org.w3c.dom.NodeList nodes = doc.getElementsByTagName("connective");
                        SSFPhrase connectiveJChild = null;
                  
                        for(int i=0;i<nodes.getLength();i++)
                        {
                            org.w3c.dom.Element element = (org.w3c.dom.Element) nodes.item(i);
                            org.w3c.dom.NodeList title = element.getElementsByTagName("name");
                            org.w3c.dom.Element line = (org.w3c.dom.Element) title.item(0);
                            conn=new SpanAnnotationParentNode(getCharacterDataFromElement(line));
                            String conntext=getCharacterDataFromElement(line);
                            
                            conn.setParent(root);
                             try {
                                        connectiveJChild = new SSFPhrase("0", "", conntext, "", conntext);
                    
                
                             } catch (Exception ex) {
                    Logger.getLogger(SpanArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
                } 
                           connectiveJRoot.addChild(connectiveJChild);
                            
                            
                            
                            
                            SpanAnnotationParentNode inst;
                            org.w3c.dom.NodeList instance=element.getElementsByTagName("instance");
                            for(int j=0;j<instance.getLength();j++)
                            {
                                
                                 org.w3c.dom.Element element1 =(org.w3c.dom.Element) instance.item(j);
                                 org.w3c.dom.NodeList number=element1.getElementsByTagName("number");
                                 org.w3c.dom.Element line1 =(org.w3c.dom.Element)number.item(0);
                                 String name="Instance"+getCharacterDataFromElement(line1);
                                 inst=new SpanAnnotationParentNode(name);
                               
                                 addObject(connectiveJChild,name,false);
                                 
                                 org.w3c.dom.NodeList position=element1.getElementsByTagName("position");
                                 line1 =(org.w3c.dom.Element)position.item(0);
                                 String pos=getCharacterDataFromElement(line1);
                                 
                                  int a[],num=0,l=0,in=0,k;
                                  if(pos.length()>0)
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
                                  }
                                  else
                                      inst.setPos(null);
                                  
                                  
                                  org.w3c.dom.NodeList prop=element1.getElementsByTagName("PropertiesManager");
                                  org.w3c.dom.Element propkey=(org.w3c.dom.Element)prop.item(0);
                                  
                                  PropertiesManager propman=new PropertiesManager();
                               
                                  
                                  
                                      org.w3c.dom.Element kvp=(org.w3c.dom.Element) prop.item(0);
                                      org.w3c.dom.NodeList attributekey=kvp.getElementsByTagName("KeyValueProperties");
                                      int numkeytags=attributekey.getLength()-6;
                                      org.w3c.dom.Element elementkey=(org.w3c.dom.Element)attributekey.item(numkeytags-1);
                                      KeyValueProperties keyvalue=new KeyValueProperties();
                                      keyvalue.readXML(elementkey);
                                      
                                      
                                      org.w3c.dom.Element tkn=(org.w3c.dom.Element) prop.item(0);
                                      org.w3c.dom.NodeList attributetkn=tkn.getElementsByTagName("PropertyTokens");
                                      org.w3c.dom.Element elementtoken=(org.w3c.dom.Element)attributetkn.item(1);
                                      PropertyTokens tokenvalue=new PropertyTokens();
                                      tokenvalue.readXML(elementtoken);
                                      
                                      propman.addPropertyContainer("keyvalue-props", keyvalue, PropertyType.KEY_VALUE_PROPERTIES);
                                      propman.addPropertyContainer("tokenvalue-props", tokenvalue,PropertyType.PROPERTY_TOKENS);
                                    
                                  PropertiesManager arg1prop=new PropertiesManager();
                               
                                        org.w3c.dom.Element arg1kvp=(org.w3c.dom.Element) prop.item(4);
                                        org.w3c.dom.NodeList arg1attributekey=arg1kvp.getElementsByTagName("KeyValueProperties");
                                        org.w3c.dom.Element arg1elementkey=(org.w3c.dom.Element)arg1attributekey.item(2);
                                        KeyValueProperties arg1keyvalue=new KeyValueProperties();
                                        arg1keyvalue.readXML(arg1elementkey);
                                      
                                       
                                      
                                      
                                 
                                   
                                      org.w3c.dom.Element arg1tkn=(org.w3c.dom.Element) prop.item(4);
                                      org.w3c.dom.NodeList arg1attributetkn=arg1tkn.getElementsByTagName("PropertyTokens");
                                      org.w3c.dom.Element arg1elementtoken=(org.w3c.dom.Element)arg1attributetkn.item(1);
                                      PropertyTokens arg1tokenvalue=new PropertyTokens();
                                      arg1tokenvalue.readXML(arg1elementtoken);
                                    
                                      arg1prop.addPropertyContainer("keyvalue-props", arg1keyvalue, PropertyType.KEY_VALUE_PROPERTIES);
                                      arg1prop.addPropertyContainer("tokenvalue-props", arg1tokenvalue, PropertyType.PROPERTY_TOKENS);
                                  
                                      propman.addPropertyContainer("arg1-props", arg1prop,PropertyType.PROPERTIES_MANAGER);
                                      
                                      
                                      
                                       PropertiesManager arg2prop=new PropertiesManager();
                               
                                        org.w3c.dom.Element arg2kvp=(org.w3c.dom.Element) prop.item(2);
                                        org.w3c.dom.NodeList arg2attributekey=arg2kvp.getElementsByTagName("KeyValueProperties");
                                        org.w3c.dom.Element arg2elementkey=(org.w3c.dom.Element)arg2attributekey.item(2);
                                        KeyValueProperties arg2keyvalue=new KeyValueProperties();
                                        arg2keyvalue.readXML(arg2elementkey);
                                      
                                       
                                      
                                      
                                 
                                   
                                      org.w3c.dom.Element arg2tkn=(org.w3c.dom.Element) prop.item(2);
                                      org.w3c.dom.NodeList arg2attributetkn=arg2tkn.getElementsByTagName("PropertyTokens");
                                      org.w3c.dom.Element arg2elementtoken=(org.w3c.dom.Element)arg2attributetkn.item(1);
                                      PropertyTokens arg2tokenvalue=new PropertyTokens();
                                      arg2tokenvalue.readXML(arg2elementtoken);
                                    
                                      arg2prop.addPropertyContainer("keyvalue-props", arg2keyvalue, PropertyType.KEY_VALUE_PROPERTIES);
                                      arg2prop.addPropertyContainer("tokenvalue-props", arg2tokenvalue, PropertyType.PROPERTY_TOKENS);
                                  
                                      propman.addPropertyContainer("arg2-props", arg2prop,PropertyType.PROPERTIES_MANAGER);
                                      
                                      
                                      
                                      
                                      
                                      
                                  inst.setPropman(propman);
                                 
                                 
              
                                 inst.setParent(conn);
                                 conn.addChild1(inst);
                            }
                            root.addChild1(conn);
                                    
                        }
                    
                }
                catch(Exception ecp)
                {
                    System.out.println(ecp);
                }
        
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

    private static void saveState(SpanArgOptionalJPanel editorInstance)
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

    private static void loadState(SpanArgOptionalJPanel editorInstance)
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
        
        if(Global_connective==1)
        {
            annotationJPopupMenu.remove(annotationJPopupMenu.getComponent(8));
            Global_connective=0;
        }
        if(Global_Flag==1)
        {
            for(int i=0;i<Global_keys+Global_tokens+3;i++)
                        annotationJPopupMenu.remove(annotationJPopupMenu.getComponent(8));
            Global_Flag=0;
        }
        
        
        
    
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



		JFileChooser chooser = null;

		if(path != null)
			chooser = new JFileChooser(path);
		else
			chooser = new JFileChooser();

		int returnVal = chooser.showSaveDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			textFile = chooser.getSelectedFile().getAbsolutePath();


			PrintStream ps = new PrintStream(textFile, charset);
			textEditorJPanel.print(ps);
			setTitle(title);

			FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);

			sanchayBackup = new SanchayBackup();
			FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
			logJTextArea.append(GlobalProperties.getIntlString("File_") + textFile + GlobalProperties.getIntlString("_backed_up."));

			dirty = false;
                        finalwork(textFile);
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
       
       
       
          
    private void writeToFile(String fileName) 
    {
            try
            {
                BufferedWriter out = new BufferedWriter(new PrintWriter(fileName, charset));
                out.write("<?xml version=\"1.0\"?>\n");
                out.write("<story>\n");
                SpanAnnotationParentNode child,node,leaf,larg,rarg;
                for(int i=0;i<info.NumOfChildren();i++)
                {
                    out.write(GlobalProperties.getIntlString("\t<connective>\n"));
                    child=info.getChild(i);
                    
                    out.write(GlobalProperties.getIntlString("\t\t<name>")+child.getProp("name",PropertyType.KEY_VALUE_PROPERTIES)+"</name>\n");
                   for(int j=0;j<child.NumOfChildren();j++)
                    {
                        node=child.getChild(j);
                        out.write("\t\t<instance>\n");
                        out.write(GlobalProperties.getIntlString("\t\t\t<number>")+String.valueOf(j+1)+"</number>\n");
                        String pos="";
                        int a[]=node.getPos();
                        if(a==null)
                            pos=null;
                       if(a!=null)
                       {
                           for(int k=0;k<a.length;k++)
                           pos=pos+String.valueOf(a[k])+" ";
                       }
                       out.write("\t\t\t<position>"+pos+"</position>\n");
                       
                        PropertiesManager propman=node.getPropman();
                        String text=propman.getXML();
                        out.write("\t\t\t\t"+text);
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
       
       
       
       
       
       
    
    public String switchLanguage(EventObject e)
    {
        return switchLanguageStatic(this);
    }
    
    public static String switchLanguageStatic(Component parent)
    {
	boolean switching = false;

	if(parent instanceof SpanArgOptionalJPanel)
	    switching = true;

	if(switching)
	{
	    if(((SpanArgOptionalJPanel) parent).dirty)    
	    {
		int retVal = JOptionPane.showConfirmDialog(parent, GlobalProperties.getIntlString("The_current_file_will_be_closed._If_you_haven't_saved_it,\nthe_data_may_be_lost._Do_you_want_to_continue?"), GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);

		if(retVal == JOptionPane.NO_OPTION)
		    return null;
	    }

	   ((SpanArgOptionalJPanel) parent).textEditorJPanel.setText("");
	   ((SpanArgOptionalJPanel) parent).logJTextArea.setText("");
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
	
	String initLang = GlobalProperties.getIntlString("Hindi");
	
	if(switching)
	    initLang = SanchayLanguages.getLanguageName( ((SpanArgOptionalJPanel) parent).langEnc );

	String selectedLanguage = (String) JOptionPane.showInputDialog(parent,
		GlobalProperties.getIntlString("Select_the_language"), GlobalProperties.getIntlString("Language"), JOptionPane.INFORMATION_MESSAGE, null,
		langs, initLang);

	if(switching)
	{
	    String langCode = SanchayLanguages.getLangEncCode(selectedLanguage);
	    ((SpanArgOptionalJPanel) parent).langEnc = langCode;

	    UtilityFunctions.setComponentFont(((SpanArgOptionalJPanel) parent).textJTextArea, langCode);
	    UtilityFunctions.setComponentFont(((SpanArgOptionalJPanel) parent).logJTextArea, langCode);

	    UtilityFunctions.setComponentFont(((SpanArgOptionalJPanel) parent).connectiveInfoTreeJPanel.treeJTree, langCode);
            ((SpanArgOptionalJPanel) parent).connectiveInfoTreeJPanel.treeJTree.setCellRenderer(new SSFTreeCellRendererNew(3, new String[] {langCode, langCode, langCode}, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION));
            
	    UtilityFunctions.setComponentFont(((SpanArgOptionalJPanel) parent).additionalInfoArgSupTreeJPanel, langCode);
            ((SpanArgOptionalJPanel) parent).additionalInfoArgSupTreeJPanel.treeJTree.setCellRenderer(new SSFTreeCellRendererNew(3, new String[] {langCode, langCode, langCode}, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION));
            
	    UtilityFunctions.setComponentFont(((SpanArgOptionalJPanel) parent).additionalInfoConTreeJPanel, langCode);
            ((SpanArgOptionalJPanel) parent).additionalInfoConTreeJPanel.treeJTree.setCellRenderer(new SSFTreeCellRendererNew(3, new String[] {langCode, langCode, langCode}, SSFTreeCellRendererNew.SYNTACTIC_ANNOTATION));

            UtilityFunctions.setComponentFont(((SpanArgOptionalJPanel) parent).connectiveInfoTableJPanel.getJTable(), langCode);

            UtilityFunctions.setComponentFont(((SpanArgOptionalJPanel) parent).connectiveJTextField, langCode);
            
	    ((SpanArgOptionalJPanel) parent).setTitle(((SpanArgOptionalJPanel) parent).getTitle());
            ((SpanArgOptionalJPanel) parent).textFile = GlobalProperties.getIntlString("Untitled");
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
    
   
    
     private void Removeinfo(String selectedoption){
	}

     public void connectiveJTreeMouseClicked(java.awt.event.MouseEvent evt){

	}


  private SSFPhrase addObject(SSFPhrase connectiveJParent,String connective,boolean ShouldBeVisible){
	SSFPhrase connectiveJChild = null;
        try {
            connectiveJChild = new SSFPhrase("0", "", connective, "", connective);
        } catch (Exception ex) {
            Logger.getLogger(SpanArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(connectiveJParent==null){
            try {
                connectiveJRoot = new SSFPhrase("0", "", GlobalProperties.getIntlString("Story"), "", GlobalProperties.getIntlString("Story"));
            } catch (Exception ex) {
                Logger.getLogger(SpanArgOptionalJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            connectiveJParent=connectiveJRoot;
        }
        connectiveJParent.add(connectiveJChild);
	
	if (ShouldBeVisible) {
		connectiveJTree.scrollPathToVisible(new TreePath(connectiveJChild.getPath()));
	}
	return connectiveJChild;
}
  
   private void addInfoTable(String str, String selectedoption)
    {
        if(str == null || str.equals("null"))
            str = "";
        
        if(selectedoption.compareTo(GlobalProperties.getIntlString("Root"))==0 || selectedoption.compareTo(GlobalProperties.getIntlString("Connective"))==0 )
        {
        connectiveInfoTableModel.addRow();
        connectiveInfoTableModel.setValueAt(selectedoption,connectiveInfoTableModel.getRowCount()-1,0);
        connectiveInfoTableModel.setValueAt(str,connectiveInfoTableModel.getRowCount()-1, 1);
        }
        else
        {
        
        
            int findflag=0;
             int numRows=connectiveInfoTableModel.getRowCount();
             for(int i=0;i<numRows;i++)
             {
                 if(connectiveInfoTableModel.getValueAt(i, 0).equals(selectedoption))
                 {
                     connectiveInfoTableModel.setValueAt(str, i, 1);
                     findflag=1;
                     break;
                     
                 }
             }
             if(findflag==0)
             {
                connectiveInfoTableModel.addRow();
                connectiveInfoTableModel.setValueAt(selectedoption,connectiveInfoTableModel.getRowCount()-1, 0);
                connectiveInfoTableModel.setValueAt(str,connectiveInfoTableModel.getRowCount()-1, 1);
             }
        }
        
    } 
  
   private void removeInfoTable(String selectedoption)
    {
            int numRows=connectiveInfoTableModel.getRowCount();

            for(int i=0;i<numRows;i++)
             { 

                 if(connectiveInfoTableModel.getValueAt(i, 0).equals(selectedoption))
                 {    connectiveInfoTableModel.removeRows(0,selectedoption);

                    break;
                 }
             }
        
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
