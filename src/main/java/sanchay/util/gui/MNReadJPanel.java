/*
 * MNReadJPanel.java
 *
 * Created on June 10, 2006, 1:00 PM
 */

package sanchay.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.table.TableCellEditor;
import sanchay.GlobalProperties;
import sanchay.gui.JTextAreaCellEditor;
import sanchay.gui.common.FontChooser;
import sanchay.gui.common.JPanelDialog;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.util.UtilityFunctions;
import sanchay.common.types.ClientType;

/**
 *
 * @author  anil
 */
public class MNReadJPanel extends javax.swing.JPanel implements JPanelDialog, sanchay.gui.clients.SanchayClient {

    protected ClientType clientType = ClientType.LANGUAGE_ENCODING_IDENTIFIER;
    
    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected SanchayTableModel sentencesTable;
    protected SanchayTableJPanel tableJPanel;
    
    protected String langEnc;
    protected String title = "";

    protected TableCellEditor tableCellEditor = new JTextAreaCellEditor();
    
    /** Creates new form MNReadJPanel */
    public MNReadJPanel() {
	initComponents();

        parentComponent = this;
	
	Vector colNames = new Vector(4);
	colNames.add(GlobalProperties.getIntlString("Sentence"));
	colNames.add(GlobalProperties.getIntlString("Characters"));
	colNames.add(GlobalProperties.getIntlString("Width"));
	colNames.add(GlobalProperties.getIntlString("Acceptable"));
	
	sentencesTable = new SanchayTableModel(colNames, 3);
	tableJPanel = SanchayTableJPanel.createTableDisplayJPanel(sentencesTable, GlobalProperties.getIntlString("hin::utf8"));

	sentencesTableJPanel.add(tableJPanel, BorderLayout.CENTER);
	
	tableCellEditor = new JTextAreaCellEditor();
	tableJPanel.getJTable().getColumn(tableJPanel.getJTable().getColumnName(0)).setCellEditor(tableCellEditor);
	    
	tableJPanel.getJTable().setRowHeight(40);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sentencesJLabel = new javax.swing.JLabel();
        sentencesTableJPanel = new javax.swing.JPanel();
        commandsJPanel = new javax.swing.JPanel();
        clearJButton = new javax.swing.JButton();
        fontJButton = new javax.swing.JButton();
        okJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout(4, 4));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        sentencesJLabel.setText(bundle.getString("Sentences_to_be_checked:")); // NOI18N
        add(sentencesJLabel, java.awt.BorderLayout.NORTH);

        sentencesTableJPanel.setLayout(new java.awt.BorderLayout());
        add(sentencesTableJPanel, java.awt.BorderLayout.CENTER);

        commandsJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        clearJButton.setText(bundle.getString("Clear")); // NOI18N
        clearJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(clearJButton);

        fontJButton.setText(bundle.getString("Font")); // NOI18N
        fontJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(fontJButton);

        okJButton.setText(bundle.getString("OK")); // NOI18N
        okJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(okJButton);

        add(commandsJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void okJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okJButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_okJButtonActionPerformed

    private void fontJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontJButtonActionPerformed
// TODO add your handling code here:
	Font presentFont = ((JTextAreaCellEditor) tableCellEditor).getFont();
	FontChooser chooser = null;
	
	if(dialog != null)
	    chooser = new FontChooser(dialog, GlobalProperties.getIntlString("eng::utf8"), presentFont, getForeground(), false);
	else
	    chooser = new FontChooser(owner, GlobalProperties.getIntlString("eng::utf8"), presentFont, getForeground(), false);

//	    int xinset = 300;
//	    int yinset = 130;
//	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//	    chooser.setBounds(xinset, yinset,
//		    screenSize.width  - xinset*2,
//		    screenSize.height - yinset*2);

//	boolean enableFontFamilies = true;
//
//	if(language.equals("eng::utf8") || language.equals("eng"))
//	    enableFontFamilies = true;
//
//	chooser.enableFontFamilies(enableFontFamilies);

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

	    ((JTextAreaCellEditor) tableCellEditor).setFont(newFont);
	    
	    tableJPanel.getJTable().setFont(newFont);

//	    textJTextArea.setForeground(chooser.getNewColor());
	}
    }//GEN-LAST:event_fontJButtonActionPerformed

    private void clearJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearJButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_clearJButtonActionPerformed

    public ClientType getClientType()
    {
        return clientType;
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

    public void setDialog(JDialog dialog) {
	this.dialog = dialog;
    }
    
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame(GlobalProperties.getIntlString("Sanchay_XML_Editor"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
//	DialogFactory.showDialog(MNReadJPanel.class, frame, "MNRead", true);

//	String selectedLanguage = TextEditorJPanel.switchLanguage(frame);

        //Create and set up the content pane.
	MNReadJPanel newContentPane = null;

//	if(selectedLanguage == null || selectedLanguage .equals("") == true)
	    newContentPane = new MNReadJPanel();
//	else
//	{
//	    newContentPane = new SanchayXMLJPanel(SanchayLanguages.getLangEncCode(selectedLanguage));
//	}
	
	newContentPane.owner = frame;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
//	frame.addWindowListener(newContentPane);

//	// Tautology in code: can be useful, but only sometimes
//	newContentPane.setTitle(newContentPane.getTitle());

        //Display the window.
        frame.pack();
	UtilityFunctions.centre(frame);
	
//        int inset = 5;
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        frame.setBounds(inset, inset,
//		screenSize.width  - inset*2,
//		screenSize.height - inset*9);

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
    protected javax.swing.JButton clearJButton;
    protected javax.swing.JPanel commandsJPanel;
    protected javax.swing.JButton fontJButton;
    protected javax.swing.JButton okJButton;
    protected javax.swing.JLabel sentencesJLabel;
    protected javax.swing.JPanel sentencesTableJPanel;
    // End of variables declaration//GEN-END:variables
}
