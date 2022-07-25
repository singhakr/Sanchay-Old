/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package sanchay.db.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import sanchay.common.types.ClientType;
import sanchay.db.DBConnection;
import sanchay.db.DatabaseUtils;
import sanchay.gui.clients.SanchayClient;
import sanchay.gui.common.DialogFactory;
import sanchay.gui.common.JPanelDialog;
import sanchay.table.SanchayTableModel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.util.UtilityFunctions;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author User
 */
public class SanchayDatabaseEditorJPanel  extends javax.swing.JPanel implements WindowListener, JPanelDialog, SanchayClient  {

    protected ClientType clientType = ClientType.DATABASe_EDITOR;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected String title = "Sanchay Database Editor";
    
    protected String langEnc;
    
    protected DBConnection connection;

    private ConnectToDBJPanel connectToDBJPanel;

    private SanchayTableModel currentMetaTableModel;
    private SanchayTableJPanel sanchayMetaTableJPanel;
    
    private SanchayTableModel currentTableModel;
    private SanchayTableJPanel sanchayTableJPanel;
    
    private String currentTableName;
    
    /**
     * Creates new form SanchayDatabaseEditor
     */
    public SanchayDatabaseEditorJPanel() {
        initComponents();
        
        connectToDBJPanel = (ConnectToDBJPanel) DialogFactory.createJPanel(ConnectToDBJPanel.class);
        
        mainJSplitPane.setLeftComponent(connectToDBJPanel);

        createSchemaTable();

        createDataTable();
                
        connectToDBJPanel.setSanchayDatabaseEditorJPanel(this);
        
        connection = connectToDBJPanel.getConnection();
        
        sanchayMetaTableJPanel.getJTable().repaint();
        
        sanchayMetaTableJPanel.setVisible(false);
        sanchayMetaTableJPanel.setVisible(true);
        
        sanchayTableJPanel.setVisible(true);
        
        topButtonsJPanel.setVisible(true);
        bottomButtonsJPanel.setVisible(true);
        
        setVisible(false);
        setVisible(true);
    }
    
    private void createSchemaTable()
    {
        currentMetaTableModel = new SanchayTableModel(0, DatabaseUtils.NUM_SCHEMA_COLUMNS);
        
        currentMetaTableModel.setName(currentTableName);

        sanchayMetaTableJPanel = SanchayTableJPanel.createDatabaseSchemaTableJPanel(currentMetaTableModel, langEnc, false);

        sanchayMetaTableJPanel.setMode(SanchayTableJPanel.DATABASE_SCHEMA_MODE);

        JTable tableJTable = sanchayMetaTableJPanel.getJTable();
        Field[] fields = java.sql.Types.class.getFields();
        String[] types = new String[fields.length];
        
        for (int i = 0; i < fields.length; i++) {
            String parts[] = fields[i].toString().split("\\.");
            types[i] = parts[parts.length - 1];
        }
//        Vector tagsVec = new Vector();
        
//        tagsVec.add("One");
//        tagsVec.add("Two");
//        tagsVec.add("Three");
        
        DefaultComboBoxModel labelEditorModel = new DefaultComboBoxModel(types);
//        labelEditorModel.insertElementAt("[.]*", 0);
        JComboBox labelEditor = new JComboBox();
        labelEditor.setModel(labelEditorModel);
        UtilityFunctions.setComponentFont(labelEditor, "hin::utf8");

        TableColumn tagsCol = tableJTable.getColumn("Data Type");
        labelEditor.setEditable(true);
        tagsCol.setCellEditor(new DefaultCellEditor(labelEditor));

//        JCheckBox checkBox = new JCheckBox();
//        tagsCol = tableJTable.getColumn("Primary Key");
//        tagsCol.setCellEditor(new DefaultCellEditor(checkBox));
//
//        checkBox = new JCheckBox();
//        tagsCol = tableJTable.getColumn("Not NULL");
//        tagsCol.setCellEditor(new DefaultCellEditor(checkBox));
//
//        checkBox = new JCheckBox();
//        tagsCol = tableJTable.getColumn("Unique");
//        tagsCol.setCellEditor(new DefaultCellEditor(checkBox));
//
//        checkBox = new JCheckBox();
//        tagsCol = tableJTable.getColumn("Check");
//        tagsCol.setCellEditor(new DefaultCellEditor(checkBox));        
        
        Object values[] = new Object[]{"", "", new Boolean(false), new Boolean(false), new Boolean(false), "", new Boolean(false)};
        
        currentMetaTableModel.addRow(values);
        
        sanchayMetaTableJPanel.setConnection(connection, false, true);

        if(topJPanel.getComponentCount() == 3)
        {
            topJPanel.remove(1);
        }

        topJPanel.add(sanchayMetaTableJPanel, BorderLayout.CENTER, 1);
        
        setVisible(false);
        setVisible(true);
    }
    
    private void createDataTable()
    {
        currentTableModel = new SanchayTableModel(1, 1);
        
        currentMetaTableModel.setName(currentTableName);

//        SanchayTableModel findOptionsTable = new SanchayTableModel(0, DatabaseUtils.NUM_SCHEMA_COLUMNS);
//        SanchayTableJPanel findOptionsJPanel = SanchayTableJPanel.createFindOptionsTableJPanel(findOptionsTable, langEnc, false);
//  
//        currentMetaTableModel = findOptionsTable;
//        sanchayMetaTableJPanel = findOptionsJPanel;
        
        
        sanchayTableJPanel = SanchayTableJPanel.createDatabaseTableJPanel(currentTableModel, langEnc, false);    
//        sanchayTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(currentTableModel, langEnc);    
//        sanchayTableJPanel = new SanchayTableJPanel(new SanchayTableModel(1, 1), false, null, SanchayTableJPanel.DEFAULT_MODE, GlobalProperties.getIntlString("hin::utf8"));
//        sanchayTableJPanel = SanchayTableJPanel.createTableDisplayJPanel(currentTableModel, langEnc);

//        DatabaseUtils.createDatabaseSchemaTableModel(sanchayMetaTableJPanel.getJTable(), langEnc);
//        DatabaseUtils.createDatabaseSchemaTableModel(sanchayTableJPanel.getJTable(), langEnc);

        sanchayTableJPanel.setMode(SanchayTableJPanel.DATABASE_MODE);        
        sanchayTableJPanel.setConnection(connection, true, false);

        if(bottomJPanel.getComponentCount() == 3)
        {
            bottomJPanel.remove(1);
        }

        bottomJPanel.add(sanchayTableJPanel, BorderLayout.CENTER, 1);
        
        setVisible(false);
        setVisible(true);
    }
    
    public void createNewTable()
    {
        if(connection != null)
        {
            String tableName = connectToDBJPanel.getTableName();
            
            if(tableName == null || tableName.equals(""))
            {
                JOptionPane.showMessageDialog(this, "Please enter the table name on the left.", "Error", JOptionPane.ERROR_MESSAGE);            

                return;
            }

            int result = JOptionPane.showConfirmDialog(this,"Do you want to save the *schema* table?", "Save Schema Table?",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE);

            if(result == JOptionPane.YES_OPTION){
                saveSchemaTable();
            }
            
            result = JOptionPane.showConfirmDialog(this,"Do you want to save the *data* table?", "Save Datat Table?",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE);

            if(result == JOptionPane.YES_OPTION){
                saveDataTable();
            }
            
            currentTableName = tableName;
            
            topJLabel.setText("Table Schema: " + currentTableName);
            bottomJLabel.setText("Table Data: " + currentTableName);
            
//            JOptionPane.showMessageDialog(this, "Please fill in the table\nschema for the new table.");
            
            createSchemaTable();
//            createDataTable();
        }
    }
    
    private void saveSchemaTable()
    {
        sanchayMetaTableJPanel.saveTable(new EventObject(sanchayMetaTableJPanel.getJTable()));
    }
    
    private void saveDataTable()
    {
        sanchayTableJPanel.saveTable(new EventObject(sanchayTableJPanel.getJTable()));
    }
    
    public void clearTables()
    {
        createSchemaTable();
        createDataTable();
        
        setVisible(false);
        setVisible(true);
    }
    
    public void setConnection(DBConnection connection)
    {
        this.connection = connection;
        
        sanchayMetaTableJPanel.setConnection(connection, false, true);
        sanchayTableJPanel.setConnection(connection, true, false);        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainJSplitPane = new javax.swing.JSplitPane();
        innerJSplitPane = new javax.swing.JSplitPane();
        topJPanel = new javax.swing.JPanel();
        topJLabel = new javax.swing.JLabel();
        topButtonsJPanel = new javax.swing.JPanel();
        loadSchemaJButton = new javax.swing.JButton();
        CreateSchemaJButton = new javax.swing.JButton();
        addColumnsJButton = new javax.swing.JButton();
        bottomJPanel = new javax.swing.JPanel();
        bottomJLabel = new javax.swing.JLabel();
        bottomButtonsJPanel = new javax.swing.JPanel();
        loadDataJButton = new javax.swing.JButton();
        saveDataJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        innerJSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        topJPanel.setLayout(new java.awt.BorderLayout());

        topJLabel.setText("Table Schema");
        topJPanel.add(topJLabel, java.awt.BorderLayout.NORTH);

        loadSchemaJButton.setText("Load Schema");
        loadSchemaJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSchemaJButtonActionPerformed(evt);
            }
        });
        topButtonsJPanel.add(loadSchemaJButton);

        CreateSchemaJButton.setText("Create Schema");
        CreateSchemaJButton.setToolTipText("Create new schema. Be careful!");
        CreateSchemaJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateSchemaJButtonActionPerformed(evt);
            }
        });
        topButtonsJPanel.add(CreateSchemaJButton);

        addColumnsJButton.setText("Add Columns");
        addColumnsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addColumnsJButtonActionPerformed(evt);
            }
        });
        topButtonsJPanel.add(addColumnsJButton);

        topJPanel.add(topButtonsJPanel, java.awt.BorderLayout.SOUTH);

        innerJSplitPane.setTopComponent(topJPanel);
        topJPanel.getAccessibleContext().setAccessibleName("");

        bottomJPanel.setLayout(new java.awt.BorderLayout());

        bottomJLabel.setText("Table Data");
        bottomJPanel.add(bottomJLabel, java.awt.BorderLayout.NORTH);

        loadDataJButton.setText("Load Data");
        loadDataJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDataJButtonActionPerformed(evt);
            }
        });
        bottomButtonsJPanel.add(loadDataJButton);

        saveDataJButton.setActionCommand("Save Data");
        saveDataJButton.setLabel("Save Data");
        saveDataJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDataJButtonActionPerformed(evt);
            }
        });
        bottomButtonsJPanel.add(saveDataJButton);

        bottomJPanel.add(bottomButtonsJPanel, java.awt.BorderLayout.SOUTH);

        innerJSplitPane.setBottomComponent(bottomJPanel);

        mainJSplitPane.setRightComponent(innerJSplitPane);

        add(mainJSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addColumnsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addColumnsJButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addColumnsJButtonActionPerformed

    private void loadDataJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDataJButtonActionPerformed
        // TODO add your handling code here:
        currentTableName = connectToDBJPanel.getTableName();
        
        currentTableModel.clearData();
        
        try {
            
            currentTableModel.getTableContents(connection, currentTableName);
                        
        } catch (SQLException ex) {
            Logger.getLogger(SanchayDatabaseEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        currentTableModel.fireTableStructureChanged();
        currentTableModel.fireTableDataChanged();
        
        sanchayTableJPanel.setVisible(false);
        sanchayTableJPanel.setVisible(true);
        
    }//GEN-LAST:event_loadDataJButtonActionPerformed

    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
        
        sanchayMetaTableJPanel.setTableName(currentTableName);
        sanchayTableJPanel.setTableName(currentTableName);        
    }

    private void saveDataJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDataJButtonActionPerformed
        // TODO add your handling code here:
        
        currentTableName = connectToDBJPanel.getTableName();
        
        setCurrentTableName(currentTableName);

        if(currentTableName == null || currentTableName.equals(""))
        {
            JOptionPane.showMessageDialog(this, "Please enter the table name on the left.", "Error", JOptionPane.ERROR_MESSAGE);            

            return;
        }

        saveDataTable();
    }//GEN-LAST:event_saveDataJButtonActionPerformed

    private void loadSchemaJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSchemaJButtonActionPerformed
        // TODO add your handling code here:
        currentTableName = connectToDBJPanel.getTableName();
        
        createSchemaTable();
        
        try {
            currentMetaTableModel.loadTableSchema(currentTableName);
            
            sanchayMetaTableJPanel.setVisible(false);
            sanchayMetaTableJPanel.setVisible(true);
            
            createDataTable();

            sanchayTableJPanel.setVisible(false);
            sanchayTableJPanel.setVisible(true);
            
        } catch (SQLException ex) {
            Logger.getLogger(SanchayDatabaseEditorJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_loadSchemaJButtonActionPerformed

    private void CreateSchemaJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateSchemaJButtonActionPerformed
        // TODO add your handling code here:

        String tableName = connectToDBJPanel.getTableName();

        if(tableName == null || tableName.equals(""))
        {
            JOptionPane.showMessageDialog(this, "Please enter the table name on the left.", "Error", JOptionPane.ERROR_MESSAGE);            

            return;
        }

        saveSchemaTable();
    }//GEN-LAST:event_CreateSchemaJButtonActionPerformed

    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Sanchay Database Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SanchayDatabaseEditorJPanel newContentPane = new SanchayDatabaseEditorJPanel();

        
        newContentPane.owner = frame;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
//            frame.addWindowListener(newContentPane);

//            newContentPane.setTitle(newContentPane.getTitle());

            //Display the window.
        frame.pack();

        int inset = 5;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*9);

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
    private javax.swing.JButton CreateSchemaJButton;
    private javax.swing.JButton addColumnsJButton;
    private javax.swing.JPanel bottomButtonsJPanel;
    private javax.swing.JLabel bottomJLabel;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JSplitPane innerJSplitPane;
    private javax.swing.JButton loadDataJButton;
    private javax.swing.JButton loadSchemaJButton;
    private javax.swing.JSplitPane mainJSplitPane;
    private javax.swing.JButton saveDataJButton;
    private javax.swing.JPanel topButtonsJPanel;
    private javax.swing.JLabel topJLabel;
    private javax.swing.JPanel topJPanel;
    // End of variables declaration//GEN-END:variables


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

    public JToolBar getJToolBar() {
        return null;
    }

    public JPopupMenu getJPopupMenu()
    {
        return null;
    }    
    
    public void setDialog(JDialog dialog)
    {
        this.dialog = dialog;
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
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
}
