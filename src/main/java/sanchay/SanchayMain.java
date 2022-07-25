/*
 * SanchayMain.java
 *
 * Created on May 27, 2008, 12:39 AM
 * 
 * 
 */
/**
 * The SanchayMain class provides a GUI for all the Sanchay applications.
 * 
 */
package sanchay;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import sanchay.common.SanchayClientsStateData;
import sanchay.gui.clients.*;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import sanchay.common.types.ClientType;
import sanchay.common.types.SanchayType;
import sanchay.common.types.ServerType;
import sanchay.corpus.parallel.gui.ParallelSyntacticAnnotationWorkJPanel;
import sanchay.corpus.ssf.SSFStory;
import sanchay.gui.LoginJPanel;
import sanchay.gui.common.FileDisplayer;
import sanchay.gui.console.ConsoleJPanel;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.SanchayEvent;
import sanchay.gui.common.SanchayJDialog;
import sanchay.gui.common.SanchayLanguages;
import sanchay.help.HelpApp;
import sanchay.util.UtilityFunctions;
import sanchay.gui.clients.SanchayRemoteWorkJPanel;
import sanchay.servers.UserManagerRI;

/**
 *
 * @author  Anil Kumar Singh
 */
//@SpringBootApplication
//@EnableScheduling
public class SanchayMain extends javax.swing.JFrame implements WindowListener, SanchayMainEventListener {
    // This list is added to the  JList and subsequently modified to reflect the currently running programs
    private DefaultListModel inputList;
    int del = 0;
    boolean alreadyBeingRemoved = false;
    Hashtable poppedOut;
    Hashtable<String, Integer> ApplicationCount;
    HelpApp helpApp;

    ConsoleJPanel consoleJPanel;
    boolean consoleShown = false;

    private static boolean connected;
    private static boolean loggedIn;
    private String server;
    private String userName;

    private boolean allCommands[];
    private SanchayMainAction actions[];

//    private SanchayServerRI sanchayServerRI;
    private UserManagerRI userManagerRI;

//    private LoginJPanel loginJPanel;
    private static SanchayRemoteWorkJPanel remoteClientJPanel;

    protected DefaultComboBoxModel inputMethodModel;

    protected transient javax.swing.event.EventListenerList listenerListLocal = new javax.swing.event.EventListenerList();

    public static SanchayMain sanchayMain;
    public static final int DEFAULT_MODE = 1000;

    /** Creates new form SanchayMain */
    public SanchayMain() {

        //pre-intialisation
        //Call a seperate function if the list of init gets bigger!

        // This list is added to the  JList and subsequently modified to reflect the currently running programs
        inputList = new DefaultListModel();

        setTitle(GlobalProperties.getIntlString("Sanchay"));
        ImageIcon img = new ImageIcon(GlobalProperties.getHomeDirectory() + "/" + GlobalProperties.getIntlString("images/sanchay-logo.jpg"), GlobalProperties.getIntlString("Sanchay"));

        setIconImage(img.getImage());
        initComponents();

        updateJMenuItem.setText("Update"); // NOI18N
        updateJMenuItem.setToolTipText("Update your version of Sanchay"); // NOI18N

        File udFile = new File("no-update");

        if(udFile.isDirectory())
            updateJMenuItem.setEnabled(false);

        currentClientsJList.setCellRenderer(new SanchayClientListRenderer());

        poppedOut = new Hashtable();
        ApplicationCount = new Hashtable<String, Integer>();

        //post-intialisation
        //Call a seperate function if the list of init gets bigger!
//        runClientJComboBox.addItem("TextEditor");
//        runClientJComboBox.addItem("Protege");
        populateNewMenu();
        Enumeration enm = ClientType.elements();
        while (enm.hasMoreElements()) {

            SanchayType u = (SanchayType) enm.nextElement();
//            System.out.println("--->>" + u.getClassName());
            runClientJComboBox.addItem(u);
        }

        ContainerListener l2 = new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
//                System.out.println("---------Component Added!-----");
            }

            public void componentRemoved(ContainerEvent e) {
//                System.out.println("---------Component Removed!-----");
                refreshActiveApplicationList(e);
            }
        };

        activeJTabbedPane.addContainerListener(l2);

        //Setting the side menu invisible

    //currentClientsJPanel.setVisible(false);

//        popoutJPanel.setVisible(false);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH); //Maximizing the window
        mainJInternalFrame.setSize(new Dimension(300, 300));

        try {
            mainJInternalFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SanchayMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        initToolbar();
        
        currentClientsJPanel.setVisible(false);

        try {
            consoleJPanel = new ConsoleJPanel();
            consoleJPanelWrapper.add(consoleJPanel, BorderLayout.CENTER);
        } catch (IOException e) {
        }

        addEventListener(this);

        fillInputMethods();

        prepareCommands(null, DEFAULT_MODE);
        
        
        remoteClientJPanel = new SanchayRemoteWorkJPanel();

//        sshClientJPanel = (SanchaySSHClientJPanel) createNewApplication(ClientType.SANCHAY_SSH_CLIENT);
//        createNewApplication(ClientType.SANCHAY_SHELL);

        sanchayMain = this;

        this.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        componentJPopupMenu = new javax.swing.JPopupMenu();
        mainJDesktopPane = new javax.swing.JDesktopPane();
        mainJInternalFrame = new javax.swing.JInternalFrame();
        mainJSplitPane = new javax.swing.JSplitPane();
        currentClientsJPanel = new javax.swing.JPanel();
        runClientJPanel = new javax.swing.JPanel();
        runClientJComboBox = new javax.swing.JComboBox();
        start = new javax.swing.JButton();
        clientsJScrollPane = new javax.swing.JScrollPane();
        currentClientsJList = new javax.swing.JList();
        popoutJPanel = new javax.swing.JPanel();
        popoutJButton = new javax.swing.JButton();
        clientsjPanel = new javax.swing.JPanel();
        activeJTabbedPane = new javax.swing.JTabbedPane();
        consoleJPanelWrapper = new javax.swing.JPanel();
        statusJLabel = new javax.swing.JLabel();
        mainJToolBar = new javax.swing.JToolBar();
        leftToolbarJPanel = new javax.swing.JPanel();
        toolbarJSeparator1 = new javax.swing.JToolBar.Separator();
        rightToolbarJPanel = new javax.swing.JPanel();
        inputMethodJComboBox = new javax.swing.JComboBox();
        consoleJButton = new javax.swing.JButton();
        mainJMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newfile = new javax.swing.JMenu();
        exit = new javax.swing.JMenuItem();
        viewJMenu = new javax.swing.JMenu();
        popOutTab = new javax.swing.JMenuItem();
        showApplicationList = new javax.swing.JCheckBoxMenuItem();
        helpJMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutJMenuItem = new javax.swing.JMenuItem();
        aboutComponentJMenuItem = new javax.swing.JMenuItem();
        helpJMenuItem = new javax.swing.JMenuItem();
        updateJMenuItem = new javax.swing.JMenuItem();
        proxyJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        setTitle(bundle.getString("Sanchay")); // NOI18N
        setName(bundle.getString("SanchayJFrame")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        mainJInternalFrame.setVisible(true);

        mainJSplitPane.setDividerSize(2);

        currentClientsJPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        currentClientsJPanel.setLayout(new java.awt.BorderLayout());

        runClientJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Applications"));
        runClientJPanel.setLayout(new java.awt.BorderLayout());

        runClientJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runClientJComboBoxActionPerformed(evt);
            }
        });
        runClientJPanel.add(runClientJComboBox, java.awt.BorderLayout.CENTER);

        start.setText(bundle.getString("Start_Application")); // NOI18N
        start.setActionCommand(bundle.getString("Start_service_now_or")); // NOI18N
        start.setName(bundle.getString("stop")); // NOI18N
        start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startActionPerformed(evt);
            }
        });
        runClientJPanel.add(start, java.awt.BorderLayout.SOUTH);

        currentClientsJPanel.add(runClientJPanel, java.awt.BorderLayout.NORTH);

        currentClientsJList.setBorder(javax.swing.BorderFactory.createTitledBorder("Active Application(s)"));
        currentClientsJList.setModel(inputList);
        currentClientsJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                currentClientsJListValueChanged(evt);
            }
        });
        currentClientsJList.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                currentClientsJListComponentAdded(evt);
            }
        });
        clientsJScrollPane.setViewportView(currentClientsJList);

        currentClientsJPanel.add(clientsJScrollPane, java.awt.BorderLayout.CENTER);

        popoutJPanel.setLayout(new java.awt.GridLayout(1, 0, 4, 0));

        popoutJButton.setText(bundle.getString("Pop_Out")); // NOI18N
        popoutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popoutJButtonActionPerformed(evt);
            }
        });
        popoutJPanel.add(popoutJButton);

        currentClientsJPanel.add(popoutJPanel, java.awt.BorderLayout.SOUTH);

        mainJSplitPane.setLeftComponent(currentClientsJPanel);

        clientsjPanel.setLayout(new java.awt.CardLayout());

        activeJTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                activeJTabbedPaneStateChanged(evt);
            }
        });
        activeJTabbedPane.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                activeJTabbedPaneComponentAdded(evt);
            }
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                activeJTabbedPaneComponentRemoved(evt);
            }
        });
        clientsjPanel.add(activeJTabbedPane, "Clients");

        consoleJPanelWrapper.setLayout(new java.awt.BorderLayout());
        clientsjPanel.add(consoleJPanelWrapper, "Console");

        mainJSplitPane.setRightComponent(clientsjPanel);

        mainJInternalFrame.getContentPane().add(mainJSplitPane, java.awt.BorderLayout.CENTER);

        statusJLabel.setText(bundle.getString("_")); // NOI18N
        statusJLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mainJInternalFrame.getContentPane().add(statusJLabel, java.awt.BorderLayout.SOUTH);

        mainJToolBar.setRollover(true);

        leftToolbarJPanel.setLayout(new java.awt.GridLayout(1, 0, 3, 0));
        mainJToolBar.add(leftToolbarJPanel);
        mainJToolBar.add(toolbarJSeparator1);

        rightToolbarJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        inputMethodJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        inputMethodJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputMethodJComboBoxActionPerformed(evt);
            }
        });
        rightToolbarJPanel.add(inputMethodJComboBox);

        consoleJButton.setText("Console");
        consoleJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consoleJButtonActionPerformed(evt);
            }
        });
        rightToolbarJPanel.add(consoleJButton);

        mainJToolBar.add(rightToolbarJPanel);

        mainJInternalFrame.getContentPane().add(mainJToolBar, java.awt.BorderLayout.PAGE_START);

        mainJInternalFrame.setBounds(0, 0, 280, 328);
        mainJDesktopPane.add(mainJInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        getContentPane().add(mainJDesktopPane, java.awt.BorderLayout.CENTER);

        fileMenu.setText(bundle.getString("File")); // NOI18N

        newfile.setText(bundle.getString("New")); // NOI18N
        fileMenu.add(newfile);

        exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exit.setText(bundle.getString("Exit")); // NOI18N
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        fileMenu.add(exit);

        mainJMenuBar.add(fileMenu);

        viewJMenu.setText(bundle.getString("View")); // NOI18N

        popOutTab.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        popOutTab.setText(bundle.getString("Pop_Out_Tab")); // NOI18N
        popOutTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popOutTabSelected(evt);
            }
        });
        viewJMenu.add(popOutTab);

        showApplicationList.setSelected(true);
        showApplicationList.setText(bundle.getString("Show_Application_List")); // NOI18N
        showApplicationList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showApplicationListActionPerformed(evt);
            }
        });
        viewJMenu.add(showApplicationList);

        mainJMenuBar.add(viewJMenu);

        helpJMenu.setText(bundle.getString("Help")); // NOI18N

        aboutJMenuItem.setText(bundle.getString("About")); // NOI18N
        aboutJMenuItem.setToolTipText(bundle.getString("About_Sanchay")); // NOI18N
        aboutJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(aboutJMenuItem);

        aboutComponentJMenuItem.setText(bundle.getString("About_Applications")); // NOI18N
        aboutComponentJMenuItem.setToolTipText(bundle.getString("About_specific_applications")); // NOI18N
        aboutComponentJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutComponentJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(aboutComponentJMenuItem);

        helpJMenuItem.setText(bundle.getString("Help")); // NOI18N
        helpJMenuItem.setToolTipText(bundle.getString("Help_about_using_Sanchay")); // NOI18N
        helpJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(helpJMenuItem);

        updateJMenuItem.setText(bundle.getString("Help")); // NOI18N
        updateJMenuItem.setToolTipText(bundle.getString("Help_about_using_Sanchay")); // NOI18N
        updateJMenuItem.setEnabled(false);
        updateJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(updateJMenuItem);

        proxyJMenuItem.setText("Proxy Settings");
        proxyJMenuItem.setToolTipText("Specify the proxy settings");
        proxyJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proxyJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(proxyJMenuItem);

        mainJMenuBar.add(helpJMenu);

        setJMenuBar(mainJMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void activeJTabbedPaneComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_activeJTabbedPaneComponentRemoved
// TODO add your handling code here:
        applicationSelectionChanged();
    }//GEN-LAST:event_activeJTabbedPaneComponentRemoved

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
// TODO add your handling code here:
        SanchayClientsStateData.save();        
    }//GEN-LAST:event_formWindowClosed

private void runClientJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runClientJComboBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_runClientJComboBoxActionPerformed

private void startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startActionPerformed
    ClientType ctype = (ClientType) runClientJComboBox.getSelectedItem();
    createNewApplication(ctype);
    applicationSelectionChanged();
}//GEN-LAST:event_startActionPerformed

private void popoutJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popoutJButtonActionPerformed
    //popoutSelectedApplication();
    popoutSelectedApplication();

//    JPanelDialog pnl = (JPanelDialog) inputList.get(currentClientsJList.getSelectedIndex());
//
//    //String name = inputList.get(currentClientsJList.getSelectedIndex())
//
//    JPanel temp= (JPanel)inputList.get(currentClientsJList.getSelectedIndex());
//
//    String title= temp.getName();
////    System.out.println("Popout name is :"+title);
//
//    SanchayJDialog dlg = new SanchayJDialog((Frame) getOwner(), title, false, pnl);
//    dlg.addWindowListener(this);
//
//    poppedOut.put(pnl, dlg);
//    dlg.setVisible(true);//*/
}//GEN-LAST:event_popoutJButtonActionPerformed

private void aboutJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJMenuItemActionPerformed
    AboutDialog about = new AboutDialog(this, false);
    UtilityFunctions.centre(about);
    about.setVisible(true);
}//GEN-LAST:event_aboutJMenuItemActionPerformed

private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
    SanchayClientsStateData.save();
    System.exit(0);
}//GEN-LAST:event_exitActionPerformed

private void popOutTabSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popOutTabSelected
// TODO add your handling code here:
    popoutSelectedApplication();
}//GEN-LAST:event_popOutTabSelected

private void showApplicationListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showApplicationListActionPerformed
    if (showApplicationList.isSelected()) {
//        System.out.println("Show the list");
        currentClientsJPanel.setVisible(true);
        mainJSplitPane.setLeftComponent(currentClientsJPanel);
        mainJSplitPane.validate();
        mainJSplitPane.updateUI();
    } else {
//        System.out.println("Hide the list");
        currentClientsJPanel.setVisible(false);
    }
}//GEN-LAST:event_showApplicationListActionPerformed

private void currentClientsJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_currentClientsJListValueChanged
// TODO add your handling code here:

//    System.out.println("Does this msg come everytime i change selection?");


    //Component c = (Component) inputList.get(currentClientsJList.getAnchorSelectionIndex());
    applicationSelectionChanged();

    SanchayClientsStateData.save();    
}//GEN-LAST:event_currentClientsJListValueChanged

private void activeJTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_activeJTabbedPaneStateChanged
// TODO add your handling code here:
//    System.out.println("read focussed??");
    //Component c= ActiveJTabbedPane.getSelectedComponent();


    //Component c=ActiveJTabbedPane.getSelectedComponent();

    //currentClientsJList.get
    //currentClientsJList.setSelectedIndex(del);

    //currentClientsJList.set
    currentClientsJList.setSelectedValue(activeJTabbedPane.getSelectedComponent(), true);

    applicationSelectionChanged();
}//GEN-LAST:event_activeJTabbedPaneStateChanged

private void activeJTabbedPaneComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_activeJTabbedPaneComponentAdded
// TODO add your handling code here:
//    System.out.println("++++ new stab added??");
    //if(ActiveJTabbedPane.getComponentCount()!=0)
    //  currentClientsJList.setSelectedValue(ActiveJTabbedPane.getSelectedComponent(), true);
//    System.out.println("-----"+ActiveJTabbedPane.getSelectedComponent()+"index:"+ ActiveJTabbedPane.getSelectedIndex());
    applicationSelectionChanged();
}//GEN-LAST:event_activeJTabbedPaneComponentAdded

private void currentClientsJListComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_currentClientsJListComponentAdded
// TODO add your handling code here:
//    System.out.println("++++++++++A component added to list and size is: "+inputList.getSize());
    currentClientsJList.setSelectedIndex(inputList.getSize() - 1);
}//GEN-LAST:event_currentClientsJListComponentAdded

private void helpJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJMenuItemActionPerformed
    // TODO add your handling code here:
    helpApp = new HelpApp(GlobalProperties.getIntlString("Sanchay_HelpSet"), GlobalProperties.getIntlString("Sanchay_HelpSet.hs"));
}//GEN-LAST:event_helpJMenuItemActionPerformed

private void aboutComponentJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutComponentJMenuItemActionPerformed
    // TODO add your handling code here:
    SubApplicationDescriptionJDialog showthis = new SubApplicationDescriptionJDialog(this, true);
    UtilityFunctions.centre(showthis);
    showthis.setVisible(true);
}//GEN-LAST:event_aboutComponentJMenuItemActionPerformed

private void consoleJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_consoleJButtonActionPerformed
{//GEN-HEADEREND:event_consoleJButtonActionPerformed
    // TODO add your handling code here:
    showConsole();
}//GEN-LAST:event_consoleJButtonActionPerformed

private void inputMethodJComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inputMethodJComboBoxActionPerformed
{//GEN-HEADEREND:event_inputMethodJComboBoxActionPerformed
    // TODO add your handling code here:
    String localeName = (String) inputMethodJComboBox.getSelectedItem();

    LinkedHashMap installedLocales = SanchayLanguages.getAllInputMethods();

    Locale l = (Locale) installedLocales.get(localeName);

    getInputContext().selectInputMethod(l);
}//GEN-LAST:event_inputMethodJComboBoxActionPerformed

private void updateJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateJMenuItemActionPerformed
    // TODO add your handling code here:
//    UpdateJPanel updateJPanel = new UpdateJPanel();
//    updateJPanel.setOwner(this);
//
//    SanchayJDialog updateJDialog = null;
//
//    updateJDialog = new SanchayJDialog(this, "Update Sanchay", true, (JPanelDialog) updateJPanel);
//
//    updateJDialog.pack();
//
//    updateJDialog.setBounds(0, 0, 600, 300);
//
//    UtilityFunctions.centre(updateJDialog);
//
//    updateJDialog.setVisible(true);
}//GEN-LAST:event_updateJMenuItemActionPerformed

private void proxyJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proxyJMenuItemActionPerformed
    // TODO add your handling code here:

    String proxyType = (String) JOptionPane.showInputDialog(this,
            "Select the proxy type", "Proxy Type", JOptionPane.INFORMATION_MESSAGE, null,
            new String[]{"HTTP", "FTP", "SOCKET", "ALL", "NONE"}, "HTTP");

    Properties systemSettings = System.getProperties();

    if(proxyType.equals("NONE"))
    {
        systemSettings.put("http.proxyHost", "null");
        systemSettings.put("ftp.proxyHost", "null");
        systemSettings.put("socks.proxyHost", "null");

        return;
    }

    String proxyHost = JOptionPane.showInputDialog(this, "Please enter the proxy host address:", systemSettings.getProperty("http.proxyHost"));
    String proxyPort = JOptionPane.showInputDialog(this, "Please enter the proxy port number:", systemSettings.getProperty("http.proxyPort"));

    if(proxyHost == null || proxyPort == null)
        return;

    try
    {
        if(proxyType.equals("HTTP") || proxyType.equals("ALL"))
        {
            systemSettings.put("http.proxyHost", proxyHost);
            systemSettings.put("http.proxyPort", proxyPort);
        }
        else if(proxyType.equals("FTP") || proxyType.equals("ALL"))
        {
            systemSettings.put("ftp.proxyHost", proxyHost);
            systemSettings.put("ftp.proxyPort", proxyPort);
        }
        else if(proxyType.equals("SOCKET") || proxyType.equals("ALL"))
        {
            systemSettings.put("socks.proxyHost", proxyHost);
            systemSettings.put("socks.proxyPort", proxyPort);
        }

        System.setProperties(systemSettings);
    }
    catch(Exception ex)
    {
        JOptionPane.showMessageDialog(this, "Invalid address or port", sanchay.GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
//        ex.printStackTrace();
    }
    
}//GEN-LAST:event_proxyJMenuItemActionPerformed

    public static SanchayMain getSanchayMain()
    {
        return sanchayMain;
    }

    public SanchayClient getSanchayClient(ClientType clientType, String file)
    {
        int ccount = inputList.getSize();

        for (int i = 0; i < ccount; i++)
        {
            SanchayClient client = (SanchayClient) inputList.elementAt(i);

            if(client.getClientType().equals(clientType) && client instanceof FileDisplayer)
            {
                String fstring = ((FileDisplayer) client).getDisplayedFile(null);

                if(fstring != null)
                {
                    File f = new File(fstring);

                    if(f.exists() && f.getAbsolutePath().equals(file))
                        return client;
                }
            }
        }

        return null;
    }

    protected void fillInputMethods()
    {
        inputMethodModel = new DefaultComboBoxModel();

        LinkedHashMap installedLocales = SanchayLanguages.getAllInputMethods();
        
        Object installedLocaleNames[] = installedLocales.keySet().toArray();
        
        Arrays.sort(installedLocaleNames);

        for (int i = 0; i < installedLocaleNames.length; i++)
        {
            String localeName = (String) installedLocaleNames[i];

            inputMethodModel.addElement(localeName);
        }

        inputMethodJComboBox.setModel(inputMethodModel);

        inputMethodJComboBox.setSelectedItem("System input method");
    }

    private void prepareCommands(int appliedCommands[], int mode)
    {
        allCommands = new boolean[SanchayMainAction._TOTAL_ACTIONS_];
        actions = new SanchayMainAction[SanchayMainAction._TOTAL_ACTIONS_];

        for(int i = 0; i < allCommands.length; i++)
        {
            allCommands[i] = true;
	    actions[i] = SanchayMainAction.createAction(this, i);
        }

        if(appliedCommands != null)
        {
            for(int i = 0; i < allCommands.length; i++)
                allCommands[i] = false;

            for(int i = 0; i < appliedCommands.length; i++)
            {
                int cmd = appliedCommands[i];
                allCommands[cmd] = true;
            }

            for(int i = 0; i < allCommands.length; i++)
            {
                if(allCommands[i] == true)
                {
                    JMenuItem mi = new JMenuItem();
                    mi.setAction(actions[i]);
                    int j = fileMenu.getMenuComponentCount();
//                    fileMenu.add(mi);
                    fileMenu.insert(mi, j-1);
                }
            }
        }
        else
        {
            for(int i = 0; i < allCommands.length; i++)
            {
                JMenuItem mi = new JMenuItem();
                mi.setAction(actions[i]);
                    int j = fileMenu.getMenuComponentCount();
//                fileMenu.add(mi);
                fileMenu.insert(mi, j-1);
            }
        }
    }

/*/*




/**
    * @param args the command line arguments
    */
    public static void main(String args[]) {

//        Class myClass = SanchayMain.class;
//        URL url = myClass.getResource("SanchayMain.class");
//
//        String path = url.getPath();
//
//        int ind = path.lastIndexOf("Sanchay/");
//
//        path = path.substring(0, ind - 1);
//
//        System.out.println(path);
//        SpringApplication.run(SanchayMain.class);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                SpringApplication.run(SanchayMain.class);
                new SanchayMain().setVisible(true);
            }
        });
    }

    protected void initToolbar()
    {
        Enumeration enm = ClientType.elements();
         while (enm.hasMoreElements()) {
            ClientType ctype = (ClientType) enm.nextElement();
            String clientName = ctype.toString();

            SanchayMainAction act = new SanchayMainAction(this, clientName, ctype.getCode());

            JButton btn = new JButton();

            btn.setAction(act);
            btn.setText(ctype.getCode());
//            btn.setBackground(Color.BLUE);
            btn.setBorder(new EtchedBorder());
            btn.setToolTipText(clientName);

            leftToolbarJPanel.add(btn);
//            mainJToolBar.add(btn);
        }
    }
    
    // Methods I defined :
    
    
    public void refreshActiveApplicationList(ContainerEvent e) {
        // Note: Doesn't support multiple redundant active applications in the list as of now.
        
        if(alreadyBeingRemoved || inputList.getSize() < 1)
        {
            return;
        }        
        
        Component c = e.getChild();        
        inputList.removeElement(c);
        
        currentClientsJList.revalidate();
        currentClientsJList.updateUI();
        
//        currentClientsJList.validate();
                
//        System.out.println("Refreshing the Active components list!");
        
//        int activeAppliactionCount = inputList.getSize();
//        System.out.println("Number of elements in the active list: "+ activeAppliactionCount);
        
//        
//        if(inputList.getSize()<1)
//        {
//            return;
//        }
        
//        for(int i = 0; i < activeAppliactionCount; i++)
//        {
////            System.out.println("The element being accessed :  "+i);
//            
//            //Object obj=inputList.get(i);
//                        
//            Component c = (Component) inputList.get(i);
//                        
//            //c.isShowing();
////            if(!c.isValid())
//            if(activeJTabbedPane.indexOfTabComponent(c) == -1)
//            {
////                    System.out.println("An element removed from list!");
//                inputList.remove(i);
//                currentClientsJList.revalidate();
//                
//                currentClientsJList.updateUI();
//                break;                
//            }                
//        }
        
        //checking if the loop broke down due to the existance of invalid component
        
        /*
         if(i<activeAppliactionCount)
        {
            Component c=(Component)inputList.get(i);
            if(!c.isValid())
            {
                inputList.remove(i);
                currentClientsJList.revalidate();
                
                currentClientsJList.updateUI();
                
            }
        }
        
        */        
    }

    public void stopApplication()
    {
        //Clear selection

        // setlist()

        int selectedIndex = currentClientsJList.getAnchorSelectionIndex();
    //    System.out.println("The selected index is:" + selectedIndex);

    //    System.out.println("Components left in the list are---:" + inputList.getSize());
        if (inputList.getSize() == 0 || selectedIndex == -1) {
            JFrame frame = new JFrame();
            if (inputList.getSize() == 0) {
                JOptionPane.showMessageDialog(mainJDesktopPane, GlobalProperties.getIntlString("No_active_application_to_stop!"), GlobalProperties.getIntlString("No_active_application!"), JOptionPane.WARNING_MESSAGE);
            } else if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(mainJDesktopPane, GlobalProperties.getIntlString("Please_select_an_application_from_the_active_applications_list_before_trying_to_stop_it."), GlobalProperties.getIntlString("No_application_selected!"), JOptionPane.WARNING_MESSAGE);            //JOptionPane.showMessageDialog(frame, "Eggs are not supposed to be green.");
            }

            return;
        }






        //currentClientsJList.getan

        //currentClientsJList.getParent()






        //Component Parent=(Component)
        //      inputList.get(selectedIndex).getParent();




    //    System.out.println("currently removing:"+ currentClientsJList.getComponent(selectedIndex));

        //currentClientsJList.getComponents()
    //    System.out.println("Index of the selected item is:" + selectedIndex);

        Component c = (Component) inputList.get(selectedIndex);


        alreadyBeingRemoved = true;

        activeJTabbedPane.remove(c);

        System.gc();
        //ActiveJTabbedPane.remove(selectedIndex);

        //ActiveJTabbedPane.invalidate();
        //ActiveJTabbedPane.validate();

        //clientJPanel.getUI();
        //clientJPanel.repaint();
        //ActiveJTabbedPane.updateUI();

        //clientJPanel.up
    //


        //clientJPanel.remove()




        //currentClientsJList.remove(selectedIndex);

        //currentClientsJList.r


        //Removing from this point

    //    currentClientsJList.invalidate();
    //    currentClientsJList.validate();
    //    currentClientsJList.updateUI();
    //
    //    jScrollPane1.invalidate();
    //    jScrollPane1.validate();
    //
    //    currentClientsJPanel.invalidate();
    //    currentClientsJPanel.validate();
    //
    //    currentClientsJPanel.revalidate();
    //
    //
    //    currentClientsJPanel.updateUI();


        //last point of removal
        //runClientJPanel.invalidate();
        //runClientJPanel.validate();

        //runClientJPanel.revalidate();




        // getContentPane().invalidate();
        // getContentPane().validate();
        //sampleJList.setSelectedIndex(index);
        //sampleJList.ensureIndexIsVisible(index);



        //  clientJPanel.setVisible(false);
        //clientJPanel.setVisible(true);


        //currentClientsJList.se

        //currentClientsJList.invalidate();
        //currentClientsJList.validate();
        //currentClientsJList.setVisibleRowCount(selectedIndex);
    //    System.out.println("The number of comp. left are" + currentClientsJList.getComponentCount());
        //clientJPanel.setVisible(false);
        //clientJPanel.setVisible(true);




        //currentClientsJList.updateUI();

        //currentClientsJList.invalidate();
        //currentClientsJList.validate();


        //jScrollPane1.invalidate();
        //jScrollPane1.validate();

        //currentClientsJPanel.invalidate();
        //currentClientsJPanel.validate();


        //Finally removing the component



        inputList.remove(selectedIndex);
        alreadyBeingRemoved = false;


    //May Not required as of now as a listner triggers  the process of removal.


    }

    public void openTab(SanchayMainEvent evt)
    {
        if(evt.getClientType() != null)
            createNewApplication(evt.getClientType());
    }

    public void displayFile(SanchayMainEvent evt)
    {
        SanchayClient client = null;

        if(evt.getClientType().equals(ClientType.SYNTACTIC_ANNOTATION) && evt.getDisplayObject() != null)
            client = getSanchayClient(evt.getClientType(), ((SSFStory) evt.getDisplayObject()).getSSFFile());
        else
            client = getSanchayClient(evt.getClientType(), evt.getFilePath());

        if(client == null)
        {
            if((evt.getClientType() != null && evt.getFilePath() != null && evt.getCharset() != null)
                || (evt.getClientType() != null && evt.getDisplayObject() != null))
            {
                SanchayClient sanchayClient = createNewApplication(evt.getClientType());

                if(sanchayClient instanceof FileDisplayer)
                {
                    ((FileDisplayer) sanchayClient).displayFile(evt);
                    activeJTabbedPane.setSelectedComponent((Component) sanchayClient);
                }
            }
        }
        else if(client instanceof FileDisplayer)
        {
            ((FileDisplayer) client).displayFile(evt);
            activeJTabbedPane.setSelectedComponent((Component) client);
        }
    }
    
    public SanchayClient createNewApplication(ClientType ctype)
    {
        if(ctype.equals(ClientType.SANCHAY_SSH_CLIENT) && remoteClientJPanel != null)
        {
//            JOptionPane.showMessageDialog(this, "SSH Client is already open.");
            JOptionPane.showMessageDialog(this, "Remote Client can only be accessed from withing an application.");
            
//            activeJTabbedPane.setSelectedIndex(0);

            return remoteClientJPanel;
        }
        
        Cursor cursor = getCursor();
        //setCursor(Cursor.WAIT_CURSOR);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    //    setCursor(new Cursor((Cursor.WAIT_CURSOR)));

        JPanel clientJPanel = ClientType.createSanchayClient(ctype);
        
        if(clientJPanel == null)
        {
            JOptionPane.showMessageDialog(this, "This option is not currently available.");
            setCursor(cursor);
            return null;
        }
        
        ((SanchayClient) clientJPanel).setOwner(this);
        ((SanchayClient) clientJPanel).setParentComponent(mainJDesktopPane);

        String applicationName = ctype.toString();

        addApplicationToTabbedPane(clientJPanel, applicationName, false);
        setCursor(cursor);
        
        // Ad-hoc code for Parallel Syntactic Annotation interface
        // Move it to SanchayClient and respective implementations
        if(ctype.equals(ClientType.PARALLEL_CORPUS_MARKUP))
        {
            ((ParallelSyntacticAnnotationWorkJPanel) clientJPanel).configure();
        }

        return ((SanchayClient) clientJPanel);
    }
    
    public void addApplicationToTabbedPane(JPanel clientJPanel, String applicationName, boolean popIn)
    {
        if (ApplicationCount.containsKey(applicationName) && popIn == false) {
            int count = ApplicationCount.get(applicationName);
            count++;
            ApplicationCount.put(applicationName, count);
            applicationName = applicationName + count;
        } else {
            ApplicationCount.put(applicationName, 1);
        }

        clientJPanel.setName(applicationName);
        
//        componentJPopupMenu = ((SanchayClient) clientJPanel).getJPopupMenu();
//
//        if(componentJPopupMenu!=null)
//        {
//            for (int i = 0; i < componentJPopupMenu.getComponentCount(); i++) {
//                componentJMenu.add(componentJPopupMenu.getComponent(i));
//            }
//        }
        
        activeJTabbedPane.add(clientJPanel);

        JLabel label=new JLabel(applicationName);
        ButtonTabComponent b= new ButtonTabComponent(activeJTabbedPane,label);

        int insertIndex=(activeJTabbedPane.getComponentCount()-1);

        if(insertIndex>0)
            insertIndex--;
    
        activeJTabbedPane.setTabComponentAt(insertIndex,b);
        
        activeJTabbedPane.hasFocus();
        
        activeJTabbedPane.setSelectedIndex(insertIndex);
        
        del++;

        inputList.addElement(clientJPanel);
        
        applicationSelectionChanged();
        
        currentClientsJList.validate();
        currentClientsJList.updateUI();      
        
        activeJTabbedPane.setRequestFocusEnabled(true);
        clientJPanel.requestFocusInWindow();
    }
    
    private void applicationSelectionChanged()
    {
        int selectedIndex = currentClientsJList.getSelectedIndex();
        if (selectedIndex >= 0 && currentClientsJList.getModel().getSize() > 0) {
            Component c = (Component) inputList.get(selectedIndex);
    //        if(activeJTabbedPane.indexOfTabComponent(c) != -1)
    //        {
            if (c.isValid())
            {
                if(activeJTabbedPane.getTabCount() > 0)                
                    activeJTabbedPane.setSelectedComponent(c);
            }
            else if(SanchayClient.class.isAssignableFrom(c.getClass()))
            {
                String title = ((SanchayClient) c).getTitle();
                title = GlobalProperties.getIntlString("Sanchay:_") + ClientType.findFromClassName(c.getClass().getName()) + GlobalProperties.getIntlString(":_") + title;
                setTitle(title);
            }
        }                
    }
    
    private void popoutSelectedApplication()
    {
        if(inputList.size() <= 0)
            return;

        JPanelDialog pnl = (JPanelDialog) inputList.get(currentClientsJList.getSelectedIndex());
    
        //String name = inputList.get(currentClientsJList.getSelectedIndex())

        JPanel temp= (JPanel)inputList.get(currentClientsJList.getSelectedIndex());

        String applicationName = ClientType.findFromClassName(temp.getClass().getName()).toString();

        if (ApplicationCount.containsKey(applicationName)) {
            int count = ApplicationCount.get(applicationName);
            count--;
            ApplicationCount.put(applicationName, count);

            if(count > 0)
                applicationName = applicationName + count;
        }

        String title= temp.getName();
//        System.out.println("Popout name is :"+title);

        alreadyBeingRemoved = true;
        inputList.removeElement(temp);

        SanchayJDialog dlg = new SanchayJDialog((Frame) getOwner(), title, false, pnl);
        dlg.addWindowListener(this);

        UtilityFunctions.maxmize(dlg);
//        UtilityFunctions.maxmize(dlg);


//        inputList.removeElement(pnl);
        poppedOut.put(pnl, dlg);
        dlg.setVisible(true);
    }
    
    private void populateNewMenu()
    {
        Enumeration enm = ClientType.elements();
         while (enm.hasMoreElements()) {
            String menuText = enm.nextElement().toString();
            
            SanchayMainAction act = new SanchayMainAction(this, menuText);
            
            newfile.add(act);
        }
    }

    public void showConsole()
    {
        if(consoleShown == false)
        {
            ((CardLayout) clientsjPanel.getLayout()).show(clientsjPanel, "Console");
            consoleShown = true;
            consoleJButton.setText("Hide Console");
        }
        else
        {
            ((CardLayout) clientsjPanel.getLayout()).show(clientsjPanel, "Clients");
            consoleShown = false;
            consoleJButton.setText("Show Console");
        }
    }

    public void connectRemote(ActionEvent e)
    {
//        connected = remoteClientJPanel.connectRemote();
//        loggedIn = connected;
//        JDialog loginDialog = new JDialog(this, GlobalProperties.getIntlString("Connect_Remote"), true);
////        JDialog loginDialog = new JDialog(this, GlobalProperties.getIntlString("Login_to_Sanchay"), true);
//
//        sshClientJPanel = new SanchaySSHClientJPanel();
//
//        sshClientJPanel.setOwner(this);
//        sshClientJPanel.setDialog(loginDialog);
//
//        loginDialog.add(sshClientJPanel);
//        loginDialog.setBounds(280, 300, 500, 160);
//
//        loginDialog.setVisible(true);
//
//        if(sshClientJPanel.isLoggedIn())
//        {
////            closeAllClients(null);
////            actions[SanchayMainAction.LOGIN].setEnabled(false);
//            actions[SanchayMainAction.LOGOUT].setEnabled(true);
////            actions[SanchayMainAction.RUN_CLIENT].setEnabled(true);
//
//            loggedIn = true;
//            userName = sshClientJPanel.getUserName();
//            statusJLabel.setText(GlobalProperties.getIntlString("Logged_in_") + userName);
//        }
//
//        
////	if(loggedIn)
////	    sanchayLogout(null);
//////	else
//////            closeAllClients(null);
////
////        server = JOptionPane.showInputDialog(this, GlobalProperties.getIntlString("Please_enter_the_server_address:"), server);
////
////        System.out.println(GlobalProperties.getIntlString("Trying_to_connect_to_") + server + "...");
//
////	String location = "rmi://" + server + "/SanchayServer";
////	try {
////		sanchayServerRI = (SanchayServerRI) Naming.lookup(location);
////	} catch (MalformedURLException ex) {
////		ex.printStackTrace();
////	} catch (RemoteException ex) {
////		ex.printStackTrace();
////	} catch (NotBoundException ex) {
////		ex.printStackTrace();
////	}
////        sanchayServerRI = (SanchayServerRI) ServerType.getNewServer(ServerType.SANCHAY_SERVER, server, true);
////        userManagerRI = (UserManagerServerRI) ServerType.getNewServer(ServerType.USER_MANAGER, server, true);
////
////        String result = GlobalProperties.getIntlString("Failed_to_connect_to_") + server;
//
////	if(sanchayServerRI == null || userManagerRI == null)
////	if(userManagerRI == null)
////	{
////	    disconnectRemote(null);
////            statusJLabel.setText(result);
////	    return;
////	}
////
////        try
////        {
//////            result = (String) sanchayServerRI.checkConnection();
////            result = (String) userManagerRI.checkConnection();
////            statusJLabel.setText(result + GlobalProperties.getIntlString("_to_") + server);
////
////            connected = true;
////            actions[SanchayMainAction.CONNECT_REMOTE].setEnabled(false);
////            actions[SanchayMainAction.DISCONNECT_REMOTE].setEnabled(true);
//////            actions[SanchayMainAction.LOGIN].setEnabled(true);
////            actions[SanchayMainAction.LOGOUT].setEnabled(false);
////
////        } catch (Exception ex) {
////	    disconnectRemote(null);
////            statusJLabel.setText(result);
////            ex.printStackTrace();
////	}
    }

    public void disconnectRemote(ActionEvent e)
    {
	if(loggedIn)
	    sanchayLogout(null);
//	else
//            closeAllClients(null);

//        sanchayServerRI = (SanchayServerRI) ServerType.getNewServer(ServerType.SANCHAY_SERVER, "", false);
        userManagerRI = (UserManagerRI) ServerType.getNewServer(ServerType.USER_MANAGER, "", false);

        connected = false;
        loggedIn = false;

        actions[SanchayMainAction.CONNECT_REMOTE].setEnabled(true);
        actions[SanchayMainAction.DISCONNECT_REMOTE].setEnabled(false);
//        actions[SanchayMainAction.LOGIN].setEnabled(true);
        actions[SanchayMainAction.LOGOUT].setEnabled(false);

        statusJLabel.setText(GlobalProperties.getIntlString("Disconnected_from_") + server);
    }

//    public void sanchayLogin(ActionEvent e)
//    {
//        JDialog loginDialog = new JDialog(this, GlobalProperties.getIntlString("Login_to_Sanchay"), true);
//
//        loginJPanel = new LoginJPanel(userManagerRI, userName);
//
//        loginJPanel.setOwner(this);
//        loginJPanel.setDialog(loginDialog);
//
//        loginDialog.add(loginJPanel);
//        loginDialog.setBounds(280, 300, 500, 160);
//
//        loginDialog.setVisible(true);
//
//        if(loginJPanel.isLoggedIn())
//        {
////            closeAllClients(null);
////            actions[SanchayMainAction.LOGIN].setEnabled(false);
//            actions[SanchayMainAction.LOGOUT].setEnabled(true);
////            actions[SanchayMainAction.RUN_CLIENT].setEnabled(true);
//
//            loggedIn = true;
//            userName = loginJPanel.getUserName();
//            statusJLabel.setText(GlobalProperties.getIntlString("Logged_in_") + userName);
//        }
//    }

    public void sanchayLogout(ActionEvent e)
    {
//        closeAllClients(null);

	try {
	    userManagerRI.logout(userName);
	} catch (RemoteException ex) {
	    ex.printStackTrace();
	}

//        actions[SanchayMainAction.LOGIN].setEnabled(true);
        actions[SanchayMainAction.LOGOUT].setEnabled(false);

        loggedIn = false;
        statusJLabel.setText(GlobalProperties.getIntlString("Logged_off_") + userName);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutComponentJMenuItem;
    private javax.swing.JTabbedPane activeJTabbedPane;
    private javax.swing.JScrollPane clientsJScrollPane;
    private javax.swing.JPanel clientsjPanel;
    private javax.swing.JPopupMenu componentJPopupMenu;
    private javax.swing.JButton consoleJButton;
    private javax.swing.JPanel consoleJPanelWrapper;
    private javax.swing.JList currentClientsJList;
    private javax.swing.JPanel currentClientsJPanel;
    private javax.swing.JMenuItem exit;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpJMenu;
    private javax.swing.JMenuItem helpJMenuItem;
    private javax.swing.JComboBox inputMethodJComboBox;
    private javax.swing.JPanel leftToolbarJPanel;
    private javax.swing.JDesktopPane mainJDesktopPane;
    private javax.swing.JInternalFrame mainJInternalFrame;
    private javax.swing.JMenuBar mainJMenuBar;
    private javax.swing.JSplitPane mainJSplitPane;
    private javax.swing.JToolBar mainJToolBar;
    private javax.swing.JMenu newfile;
    private javax.swing.JMenuItem popOutTab;
    private javax.swing.JButton popoutJButton;
    private javax.swing.JPanel popoutJPanel;
    private javax.swing.JMenuItem proxyJMenuItem;
    private javax.swing.JPanel rightToolbarJPanel;
    private javax.swing.JComboBox runClientJComboBox;
    private javax.swing.JPanel runClientJPanel;
    private javax.swing.JCheckBoxMenuItem showApplicationList;
    private javax.swing.JButton start;
    private javax.swing.JLabel statusJLabel;
    private javax.swing.JToolBar.Separator toolbarJSeparator1;
    private javax.swing.JMenuItem updateJMenuItem;
    private javax.swing.JMenu viewJMenu;
    // End of variables declaration//GEN-END:variables

    public void windowOpened(WindowEvent e) {
         alreadyBeingRemoved = false;
   }

    public void windowClosing(WindowEvent e) {
        SanchayJDialog dlg = (SanchayJDialog) e.getSource();
        JPanelDialog pnl = (JPanelDialog) dlg.getJPanel();
        
        JPanel clientJPanel =dlg.getJPanel();
        
        String applicationName = ClientType.findFromClassName(pnl.getClass().getName()).toString();
        addApplicationToTabbedPane(clientJPanel, applicationName /*applicationName*/, true);

        poppedOut.remove(clientJPanel);
                         /* 
          JLabel label=new JLabel(ClientType.findFromClassName(pnl.getClass().getName()).toString());
        
        String applicationName = ClientType.findFromClassName(pnl.getClass().getName()).toString();
        ButtonTabComponent b= new ButtonTabComponent(ActiveJTabbedPane,label);

        addApplicationToTabbedPane(JPanel clientJPanel,applicationName); 
        */
        
        
        // Below-- Commented part of june 16 
        
        /*
                
        JLabel label=new JLabel(ClientType.findFromClassName(pnl.getClass().getName()).toString());
        ButtonTabComponent b= new ButtonTabComponent(ActiveJTabbedPane,label);

        ActiveJTabbedPane.setTabComponentAt(1, b);

        del++;

        inputList.addElement(pnl);

        currentClientsJList.validate();
        currentClientsJList.updateUI(); 
         
         */
        
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

    public void addEventListener(EventListener listener)
    {
        listenerListLocal.add(EventListener.class, listener);
    }

    // This methods allows classes to unregister for MyEvents
    public void removeEventListener(EventListener listener)
    {
        listenerListLocal.remove(EventListener.class, listener);
    }

    public void fireEvent(SanchayEvent evt)
    {
        Object[] listeners = listenerListLocal.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] instanceof SanchayMainEventListener
                    && evt instanceof SanchayMainEvent)
            {
                if(evt.getEventID() == SanchayMainEvent.OPEN_TAB)
                    ((SanchayMainEventListener) listeners[i]).openTab((SanchayMainEvent) evt);
                else if(evt.getEventID() == SanchayMainEvent.DISPLAY_FILE)
                    ((SanchayMainEventListener) listeners[i]).displayFile((SanchayMainEvent) evt);
            }
        }
    }

    public ClientType getClientType()
    {
        return null;
    }
    
    public static SanchayRemoteWorkJPanel getSSHClientJPanel()
    {
        return remoteClientJPanel;
    }
    
    public static boolean ifConnected()
    {
        return connected;
    }
    
    public static boolean ifLoggedIn()
    {
        return loggedIn;
    }
        
    public static void setConnected(boolean c)
    {
        connected = c;
    }
        
    public static void setLoggedIn(boolean l)
    {
        loggedIn = l;
    }
}
