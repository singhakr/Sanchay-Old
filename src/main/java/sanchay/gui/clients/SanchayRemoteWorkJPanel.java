/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package sanchay.gui.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import org.apache.commons.io.FileUtils;
import sanchay.GlobalProperties;
import sanchay.SanchayMain;
import sanchay.common.types.ClientType;
import sanchay.corpus.ssf.gui.SyntacticAnnotationTaskSetupJPanel;
import sanchay.corpus.ssf.gui.SyntacticAnnotationWorkJPanel;
import sanchay.gui.common.FileDisplayer;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.common.RemoteFileExplorerJPanel;
import sanchay.properties.KeyValueProperties;
import sanchay.server.dao.auth.model.domain.SanchayRole;
import sanchay.server.dto.auth.model.domain.*;
import sanchay.server.dto.model.files.RemoteSftpFile;
import sanchay.server.dto.model.files.RemoteFile;
import sanchay.server.dto.tree.impl.RemoteFileNode;
import sanchay.servers.AuthenticationSessionRI;
import sanchay.servers.RMIFileSystemRI;
import sanchay.servers.clients.SanchaySpringRestClient;
import sanchay.table.gui.DisplayEvent;
import sanchay.text.editor.gui.TextEditorJPanel;
import sanchay.servers.utils.JSchSSHUtils;
import sanchay.servers.impl.SanchayMainServer;
import sanchay.servers.utils.RMIUtils;
import sanchay.servers.UserManagerRI;
import sanchay.servers.ResourceManagerRI;
import sanchay.servers.SanchayLauncherSessionRI;
import sanchay.servers.impl.AuthorizationException;
import sanchay.servers.impl.SessionException;
import sanchay.servers.AuthenticationEntryRI;
import sanchay.servers.SanchayServerLauncherRI;
import sanchay.servers.clients.SanchaySpringClientUtils;

/**
 *
 * @author User
 */
public class SanchayRemoteWorkJPanel extends javax.swing.JPanel
        implements WindowListener, FileDisplayer, SanchayClient , JPanelDialog
{
    protected ClientType clientType = null;

    private static final String LAUNCHER_LOGIN = "LauncherLogin";
    private static final String LAUNCHER_SESSION = "LauncherSession";

    private static final String AUTH_LOGIN = "AuthorizationLogin";
    private static final String AUTH_SESSION = "AuthorizationSession";

    protected Component parentComponent;

    private String userName;
    private String password;
    private String server;
    
    private JFrame owner;
    private JDialog dialog;
    protected String langEnc = sanchay.GlobalProperties.getIntlString("hin::utf8");
    
    protected String textFile;
    protected String charset = "UTF-8";
    
    public static int REMOTE_MODE = 0;
    public static int LOCAL_MODE = 1;
    
    protected String title;
    
    protected int currentMode;

    private SanchaySpringRestClient sanchaySpringRestClient;
    private SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo;
    private SanchayUserDTO currentUser;

    private SanchayServerLauncherRI sanchayServerLauncher;

    private AuthenticationEntryRI authenticationEntry;

    private SanchayLauncherSessionRI sanchayLauncherSession;
    private AuthenticationSessionRI authenticationSession;
    
    private UUID launcherSessionId;
    private UUID authenticationSessionId;

    private SanchayMainServer sanchayMainServer;
//    private AuthenticationSeverRI authenticationSever;
    private UserManagerRI userManagerRI;
    private ResourceManagerRI resourceManagerRI;
    private RMIFileSystemRI rmiFileSystem = null;
    
    private Registry registry = null;
  
    protected RemoteFileExplorerJPanel remoteFileExplorerJPanel;
    
    protected RemoteFileNode rootRemoteFileNode = null;
    
    protected FileDisplayer displayFileTextJPanel = null;

    protected JSch jsch = null;
    protected Session session = null;
    protected ChannelSftp channelSftp = null;
    
    
    private int connectionMode = RemoteFileNode.SPRING_MODE;
    
    private boolean connected = false;
    private boolean loggedIn = false;
    
    private Serializable sessionId;
    
    private SyntacticAnnotationWorkJPanel workJPanel;

    private JDialog workDialog;

    private String taskName = null;
    private String taskPath = null;
    private KeyValueProperties taskKVP = null;
    
    private KeyValueProperties serverKVP = null;
    /**
     *
     */
    
//    ShellConnectionStream connectionStream;

    /**
     * Creates new form SSHJPanel
     */
//    public SanchaySSHClientJPanel(UserManagerServerRI umri, String un) {
    public SanchayRemoteWorkJPanel() {
        initComponents();

        usersListJList.setModel(new DefaultListModel<String>());
        rolesJList.setModel(new DefaultListModel<String>());
        organisationsListJList.setModel(new DefaultListModel<String>());
        organisationsJList.setModel(new DefaultListModel<String>());
        languagesListJList.setModel(new DefaultListModel<String>());
        languagesJList.setModel(new DefaultListModel<String>());
        annotationLevelsJList.setModel(new DefaultListModel<String>());
        rolesAssignedJList.setModel(new DefaultListModel<String>());
        languagesAssignedJList.setModel(new DefaultListModel<String>());
        organisationsAssignedJList.setModel(new DefaultListModel<String>());
        annotationLevelsJAssignedJList.setModel(new DefaultListModel<String>());

        selectRoleUserJComboBox1.setModel(new DefaultComboBoxModel<String>());
        currentRoleJComboBox.setModel(new DefaultComboBoxModel<String>());
        selectOrganisationUserJComboBox.setModel(new DefaultComboBoxModel<String>());
        currentUserOrganisationJComboBox.setModel(new DefaultComboBoxModel<String>());
        selectLanguageUserJComboBox.setModel(new DefaultComboBoxModel<String>());
        currentUserLanguageJComboBox.setModel(new DefaultComboBoxModel<String>());
        selectLevelUserJComboBox.setModel(new DefaultComboBoxModel<String>());
        currentAnnotationLevelJComboBox.setModel(new DefaultComboBoxModel<String>());

        try {
            sanchaySpringRestClient = SanchaySpringClientUtils.getSanchaySpringRestClientInstance();
            
            annotationManagementUpdateInfo = SanchayAnnotationManagementUpdateInfo.builder().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        connectionModeJComboBoxActionPerformed(new ActionEvent(this, (int) ActionEvent.KEY_EVENT_MASK, ""));

//        userManagerRI = umri;
//        userName = un;

        parentComponent = this;

        displayFileTextJPanel = new TextEditorJPanel(langEnc, charset, null, null, TextEditorJPanel.MINIMAL_MODE);
//        editTextJPanel = new TextEditorJPanel(findReplaceOptions.language, findReplaceOptions.charset);
//		editTextJPanel.setMode(TextEditorJPanel.MINIMAL_MODE);
        ((TextEditorJPanel)displayFileTextJPanel).setVisible(true);
        
        ((TextEditorJPanel)displayFileTextJPanel).setEditable(false);
        
    //System.out.println("Extract -->"+inFile);
//        if(textJScrollPane.getComponentCount() > 0)
//            textJScrollPane.removeAll();
        if(fileDisplayJPanel.getComponentCount() > 0)
            fileDisplayJPanel.removeAll();

        fileDisplayJPanel.add(((TextEditorJPanel)displayFileTextJPanel), java.awt.BorderLayout.CENTER);
//        textJScrollPane.add(displayFileTextJPanel, java.awt.BorderLayout.CENTER);
        fileDisplayJPanel.setVisible(false);
        fileDisplayJPanel.setVisible(true);

        try {
            //        if(un != null && un.equals("") == false)
//            userJTextField.setText(un);

            serverKVP = new KeyValueProperties("props/ssh-server-props.txt", "UTF-8");            

            serverJTextField.setText(serverKVP.getPropertyValue("HOSTNAME"));
            userJTextField.setText(serverKVP.getPropertyValue("USERNAME"));
            passwordJTextField.setText(serverKVP.getPropertyValue("PASSWORD"));
            
        } catch (IOException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modeJButtonGroup = new javax.swing.ButtonGroup();
        topJPanel = new javax.swing.JPanel();
        titleJPanel = new javax.swing.JPanel();
        titleJLabel = new javax.swing.JLabel();
        connectJPanel = new javax.swing.JPanel();
        serverJPanel = new javax.swing.JPanel();
        serverJLabel = new javax.swing.JLabel();
        serverJTextField = new javax.swing.JTextField();
        userJPanel = new javax.swing.JPanel();
        userJLabel = new javax.swing.JLabel();
        userJTextField = new javax.swing.JTextField();
        passwordJPanel = new javax.swing.JPanel();
        passwordJLabel = new javax.swing.JLabel();
        passwordJTextField = new javax.swing.JPasswordField();
        optionsJPanel = new javax.swing.JPanel();
        connectionModeJComboBox = new javax.swing.JComboBox<>();
        annotationManagementJCheckBox = new javax.swing.JCheckBox();
        mainJSplitPane = new javax.swing.JSplitPane();
        leftJTabbedPane = new javax.swing.JTabbedPane();
        foldersJPanel = new javax.swing.JPanel();
        rightJTabbedPane = new javax.swing.JTabbedPane();
        fileDisplayJPanel = new javax.swing.JPanel();
        organisationsJPanel = new javax.swing.JPanel();
        addOrganisationJPanel = new javax.swing.JPanel();
        organisationsShortNameJPanel = new javax.swing.JPanel();
        organisationShortNameLabel = new javax.swing.JLabel();
        organisationShortNameJTextField = new javax.swing.JTextField();
        organisationsLongNameJPanel = new javax.swing.JPanel();
        organisationLongNameLabel = new javax.swing.JLabel();
        organisationLongNameJTextField = new javax.swing.JTextField();
        createOrganisationJButton = new javax.swing.JButton();
        updateOrganisationJButton = new javax.swing.JButton();
        organisationDetailsJPanel = new javax.swing.JPanel();
        organisationListJScrollPane = new javax.swing.JScrollPane();
        organisationsListJList = new javax.swing.JList<>();
        organisationButtonJPanel = new javax.swing.JPanel();
        organisationButtonLayoutJPanel = new javax.swing.JPanel();
        deleteOrganisationJButton = new javax.swing.JButton();
        languagesJPanel = new javax.swing.JPanel();
        addLangugageJPanel = new javax.swing.JPanel();
        languageNameJPanel = new javax.swing.JPanel();
        languageNameLabel = new javax.swing.JLabel();
        languageNameJTextField = new javax.swing.JTextField();
        createLanguageJButton = new javax.swing.JButton();
        updateLanguageJButton = new javax.swing.JButton();
        languageDetailsJPanel = new javax.swing.JPanel();
        languageListJScrollPane = new javax.swing.JScrollPane();
        languagesListJList = new javax.swing.JList<>();
        languageButtonJPanel = new javax.swing.JPanel();
        languageButtonLayoutJPanel = new javax.swing.JPanel();
        deleteLanguageJButton = new javax.swing.JButton();
        usersJPanel = new javax.swing.JPanel();
        addUserJPanel = new javax.swing.JPanel();
        usernameJPanel = new javax.swing.JPanel();
        usernameLabel = new javax.swing.JLabel();
        usernameJTextField = new javax.swing.JTextField();
        passwordJPanel1 = new javax.swing.JPanel();
        passwordLabel = new javax.swing.JLabel();
        passwordJTextField1 = new javax.swing.JPasswordField();
        emailJPanel = new javax.swing.JPanel();
        emailLabel = new javax.swing.JLabel();
        emailJTextField = new javax.swing.JTextField();
        firstNameJPanel = new javax.swing.JPanel();
        firstNameLabel = new javax.swing.JLabel();
        firstNameJTextField = new javax.swing.JTextField();
        lastNameJPanel = new javax.swing.JPanel();
        lastNameLabel = new javax.swing.JLabel();
        lastNameJTextField = new javax.swing.JTextField();
        userAddButtonsJPanel = new javax.swing.JPanel();
        createUserJButton = new javax.swing.JButton();
        updateUserJButton = new javax.swing.JButton();
        userDetailsJPanel = new javax.swing.JPanel();
        userListJScrollPane = new javax.swing.JScrollPane();
        usersListJList = new javax.swing.JList<>();
        userButtonJPanel = new javax.swing.JPanel();
        languageButtonLayoutJPanel1 = new javax.swing.JPanel();
        userEnableJCheckBox1 = new javax.swing.JCheckBox();
        deleteUserJButton = new javax.swing.JButton();
        rolesJPanel = new javax.swing.JPanel();
        currentRoleJPanel = new javax.swing.JPanel();
        selectRoleUserJLabel = new javax.swing.JLabel();
        selectRoleUserJComboBox1 = new javax.swing.JComboBox<>();
        currentRoleJLabel = new javax.swing.JLabel();
        currentRoleJComboBox = new javax.swing.JComboBox<>();
        rolesPanel = new javax.swing.JPanel();
        rolesAssignedScrollPane = new javax.swing.JScrollPane();
        rolesAssignedJList = new javax.swing.JList<>();
        rolesButtonsPanel = new javax.swing.JPanel();
        addRoleJButton = new javax.swing.JButton();
        removeRoleJButton = new javax.swing.JButton();
        rolesScrollPane = new javax.swing.JScrollPane();
        rolesJList = new javax.swing.JList<>();
        userOrganisationJPanel = new javax.swing.JPanel();
        currentUserOrganisationJPanel = new javax.swing.JPanel();
        selectOrganisationUserJLabel = new javax.swing.JLabel();
        selectOrganisationUserJComboBox = new javax.swing.JComboBox<>();
        currentUserOrganisationJLabel = new javax.swing.JLabel();
        currentUserOrganisationJComboBox = new javax.swing.JComboBox<>();
        userOrganisationsPanel = new javax.swing.JPanel();
        organisationsAssignedScrollPane = new javax.swing.JScrollPane();
        organisationsAssignedJList = new javax.swing.JList<>();
        userOrganisationsButtonsPanel = new javax.swing.JPanel();
        addOrganisationJButton = new javax.swing.JButton();
        removeOrganisationJButton = new javax.swing.JButton();
        organisationsScrollPane = new javax.swing.JScrollPane();
        organisationsJList = new javax.swing.JList<>();
        userLanguagesJPanel = new javax.swing.JPanel();
        currentUserLangaugeJPanel = new javax.swing.JPanel();
        selectLanguageUserJLabel = new javax.swing.JLabel();
        selectLanguageUserJComboBox = new javax.swing.JComboBox<>();
        currentUserLanguageJLabel = new javax.swing.JLabel();
        currentUserLanguageJComboBox = new javax.swing.JComboBox<>();
        languagesAssignedJPanel = new javax.swing.JPanel();
        languagesAssignedScrollPane = new javax.swing.JScrollPane();
        languagesAssignedJList = new javax.swing.JList<>();
        languagesButtonsPanel = new javax.swing.JPanel();
        addLanguageJButton = new javax.swing.JButton();
        removeLanguageJButton = new javax.swing.JButton();
        languagesScrollPane = new javax.swing.JScrollPane();
        languagesJList = new javax.swing.JList<>();
        annotationLevelsJPanel = new javax.swing.JPanel();
        currentAnnotationLevelJPanel = new javax.swing.JPanel();
        selectLevelUserJLabel = new javax.swing.JLabel();
        selectLevelUserJComboBox = new javax.swing.JComboBox<>();
        currentAnnotationLevelJLabel = new javax.swing.JLabel();
        currentAnnotationLevelJComboBox = new javax.swing.JComboBox<>();
        userAnnotationLevelsJPanel = new javax.swing.JPanel();
        annotationLevelsAssignedJScrollPane = new javax.swing.JScrollPane();
        annotationLevelsJAssignedJList = new javax.swing.JList<>();
        annotationLevelsJButtonsPanel = new javax.swing.JPanel();
        addAnnotationLevelsJButton = new javax.swing.JButton();
        removeAnnotationLevelsJButton = new javax.swing.JButton();
        annotationLevelsScrollPane = new javax.swing.JScrollPane();
        annotationLevelsJList = new javax.swing.JList<>();
        bottomJPanel = new javax.swing.JPanel();
        connectJButton = new javax.swing.JButton();
        openRemoteJButton = new javax.swing.JButton();
        saveAnnotationManagementInfoJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        topJPanel.setLayout(new java.awt.BorderLayout());

        titleJPanel.setLayout(new java.awt.BorderLayout());

        titleJLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        titleJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleJLabel.setText("Server Panel");
        titleJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titleJPanel.add(titleJLabel, java.awt.BorderLayout.CENTER);

        topJPanel.add(titleJPanel, java.awt.BorderLayout.NORTH);
        titleJPanel.getAccessibleContext().setAccessibleName("");

        connectJPanel.setLayout(new java.awt.BorderLayout());

        serverJPanel.setLayout(new java.awt.BorderLayout());

        serverJLabel.setText("Server address: ");
        serverJPanel.add(serverJLabel, java.awt.BorderLayout.WEST);

        serverJTextField.setText("Host URL or IP");
        serverJPanel.add(serverJTextField, java.awt.BorderLayout.CENTER);

        connectJPanel.add(serverJPanel, java.awt.BorderLayout.NORTH);

        userJPanel.setLayout(new java.awt.BorderLayout());

        userJLabel.setText("User name:       ");
        userJPanel.add(userJLabel, java.awt.BorderLayout.WEST);

        userJTextField.setText("User Name");
        userJPanel.add(userJTextField, java.awt.BorderLayout.CENTER);

        connectJPanel.add(userJPanel, java.awt.BorderLayout.CENTER);

        passwordJPanel.setLayout(new java.awt.BorderLayout());

        passwordJLabel.setText("Password:         ");
        passwordJPanel.add(passwordJLabel, java.awt.BorderLayout.WEST);
        passwordJLabel.getAccessibleContext().setAccessibleName("Password:");

        passwordJTextField.setText("jPasswordField1");
        passwordJPanel.add(passwordJTextField, java.awt.BorderLayout.CENTER);

        connectJPanel.add(passwordJPanel, java.awt.BorderLayout.SOUTH);

        topJPanel.add(connectJPanel, java.awt.BorderLayout.CENTER);

        optionsJPanel.setLayout(new java.awt.GridLayout(3, 0));

        connectionModeJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Spring Mode", "RMI Mode", "SFTP Mode" }));
        connectionModeJComboBox.setEnabled(false);
        connectionModeJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionModeJComboBoxActionPerformed(evt);
            }
        });
        optionsJPanel.add(connectionModeJComboBox);

        annotationManagementJCheckBox.setText("Manage Users etc.");
        annotationManagementJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annotationManagementJCheckBoxActionPerformed(evt);
            }
        });
        optionsJPanel.add(annotationManagementJCheckBox);

        topJPanel.add(optionsJPanel, java.awt.BorderLayout.EAST);

        add(topJPanel, java.awt.BorderLayout.NORTH);

        mainJSplitPane.setResizeWeight(0.2);
        mainJSplitPane.setOneTouchExpandable(true);

        leftJTabbedPane.setMinimumSize(new java.awt.Dimension(25, 25));
        leftJTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                leftJTabbedPaneStateChanged(evt);
            }
        });

        foldersJPanel.setLayout(new java.awt.BorderLayout());
        leftJTabbedPane.addTab("Folders", foldersJPanel);

        mainJSplitPane.setLeftComponent(leftJTabbedPane);

        fileDisplayJPanel.setLayout(new java.awt.BorderLayout());
        rightJTabbedPane.addTab("File Display", fileDisplayJPanel);

        organisationsJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        organisationsJPanel.setLayout(new java.awt.BorderLayout());

        addOrganisationJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addOrganisationJPanel.setLayout(new java.awt.GridLayout(0, 1));

        organisationsShortNameJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        organisationShortNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        organisationShortNameLabel.setText("Short Name: ");
        organisationShortNameLabel.setPreferredSize(new java.awt.Dimension(130, 14));
        organisationsShortNameJPanel.add(organisationShortNameLabel);

        organisationShortNameJTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        organisationsShortNameJPanel.add(organisationShortNameJTextField);

        addOrganisationJPanel.add(organisationsShortNameJPanel);

        organisationsLongNameJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        organisationLongNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        organisationLongNameLabel.setText("Full Name: ");
        organisationLongNameLabel.setPreferredSize(new java.awt.Dimension(130, 14));
        organisationsLongNameJPanel.add(organisationLongNameLabel);

        organisationLongNameJTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        organisationsLongNameJPanel.add(organisationLongNameJTextField);

        createOrganisationJButton.setText("Add");
        createOrganisationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createOrganisationJButtonActionPerformed(evt);
            }
        });
        organisationsLongNameJPanel.add(createOrganisationJButton);

        updateOrganisationJButton.setText("Update");
        updateOrganisationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateOrganisationJButtonActionPerformed(evt);
            }
        });
        organisationsLongNameJPanel.add(updateOrganisationJButton);

        addOrganisationJPanel.add(organisationsLongNameJPanel);

        organisationsJPanel.add(addOrganisationJPanel, java.awt.BorderLayout.NORTH);

        organisationDetailsJPanel.setLayout(new java.awt.BorderLayout());

        organisationsListJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                organisationsListJListValueChanged(evt);
            }
        });
        organisationListJScrollPane.setViewportView(organisationsListJList);

        organisationDetailsJPanel.add(organisationListJScrollPane, java.awt.BorderLayout.CENTER);

        organisationsJPanel.add(organisationDetailsJPanel, java.awt.BorderLayout.CENTER);

        organisationButtonJPanel.setLayout(new java.awt.BorderLayout());

        deleteOrganisationJButton.setText("Delete");
        deleteOrganisationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOrganisationJButtonActionPerformed(evt);
            }
        });
        organisationButtonLayoutJPanel.add(deleteOrganisationJButton);

        organisationButtonJPanel.add(organisationButtonLayoutJPanel, java.awt.BorderLayout.LINE_END);

        organisationsJPanel.add(organisationButtonJPanel, java.awt.BorderLayout.SOUTH);

        rightJTabbedPane.addTab("Organisations", organisationsJPanel);

        languagesJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        languagesJPanel.setLayout(new java.awt.BorderLayout());

        addLangugageJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addLangugageJPanel.setLayout(new java.awt.GridLayout(0, 1));

        languageNameJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        languageNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        languageNameLabel.setText("Language Name: ");
        languageNameLabel.setPreferredSize(new java.awt.Dimension(130, 14));
        languageNameJPanel.add(languageNameLabel);

        languageNameJTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        languageNameJPanel.add(languageNameJTextField);

        createLanguageJButton.setText("Add");
        createLanguageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createLanguageJButtonActionPerformed(evt);
            }
        });
        languageNameJPanel.add(createLanguageJButton);

        updateLanguageJButton.setText("Update");
        updateLanguageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateLanguageJButtonActionPerformed(evt);
            }
        });
        languageNameJPanel.add(updateLanguageJButton);

        addLangugageJPanel.add(languageNameJPanel);

        languagesJPanel.add(addLangugageJPanel, java.awt.BorderLayout.NORTH);

        languageDetailsJPanel.setLayout(new java.awt.BorderLayout());

        languagesListJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                languagesListJListValueChanged(evt);
            }
        });
        languageListJScrollPane.setViewportView(languagesListJList);

        languageDetailsJPanel.add(languageListJScrollPane, java.awt.BorderLayout.CENTER);

        languagesJPanel.add(languageDetailsJPanel, java.awt.BorderLayout.CENTER);

        languageButtonJPanel.setLayout(new java.awt.BorderLayout());

        deleteLanguageJButton.setText("Delete");
        deleteLanguageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteLanguageJButtonActionPerformed(evt);
            }
        });
        languageButtonLayoutJPanel.add(deleteLanguageJButton);

        languageButtonJPanel.add(languageButtonLayoutJPanel, java.awt.BorderLayout.LINE_END);

        languagesJPanel.add(languageButtonJPanel, java.awt.BorderLayout.SOUTH);

        rightJTabbedPane.addTab("Languages", languagesJPanel);

        usersJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        usersJPanel.setLayout(new java.awt.BorderLayout());

        addUserJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addUserJPanel.setLayout(new java.awt.GridLayout(0, 1));

        usernameJPanel.setLayout(new java.awt.BorderLayout());

        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        usernameLabel.setText("Username: ");
        usernameLabel.setPreferredSize(new java.awt.Dimension(85, 14));
        usernameJPanel.add(usernameLabel, java.awt.BorderLayout.WEST);

        usernameJTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        usernameJPanel.add(usernameJTextField, java.awt.BorderLayout.CENTER);

        addUserJPanel.add(usernameJPanel);

        passwordJPanel1.setLayout(new java.awt.BorderLayout());

        passwordLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        passwordLabel.setText("Password: ");
        passwordLabel.setPreferredSize(new java.awt.Dimension(85, 14));
        passwordJPanel1.add(passwordLabel, java.awt.BorderLayout.WEST);

        passwordJTextField1.setText("jPasswordField1");
        passwordJPanel1.add(passwordJTextField1, java.awt.BorderLayout.CENTER);

        addUserJPanel.add(passwordJPanel1);

        emailJPanel.setLayout(new java.awt.BorderLayout());

        emailLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        emailLabel.setText("E-mail: ");
        emailLabel.setPreferredSize(new java.awt.Dimension(85, 14));
        emailJPanel.add(emailLabel, java.awt.BorderLayout.WEST);

        emailJTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        emailJPanel.add(emailJTextField, java.awt.BorderLayout.CENTER);

        addUserJPanel.add(emailJPanel);

        firstNameJPanel.setLayout(new java.awt.BorderLayout());

        firstNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstNameLabel.setText("First Name: ");
        firstNameLabel.setPreferredSize(new java.awt.Dimension(85, 14));
        firstNameJPanel.add(firstNameLabel, java.awt.BorderLayout.WEST);

        firstNameJTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        firstNameJPanel.add(firstNameJTextField, java.awt.BorderLayout.CENTER);

        addUserJPanel.add(firstNameJPanel);

        lastNameJPanel.setLayout(new java.awt.BorderLayout());

        lastNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lastNameLabel.setText("Last Name: ");
        lastNameLabel.setPreferredSize(new java.awt.Dimension(85, 14));
        lastNameJPanel.add(lastNameLabel, java.awt.BorderLayout.WEST);

        lastNameJTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        lastNameJPanel.add(lastNameJTextField, java.awt.BorderLayout.CENTER);

        addUserJPanel.add(lastNameJPanel);

        userAddButtonsJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        createUserJButton.setText("Add");
        createUserJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createUserJButtonActionPerformed(evt);
            }
        });
        userAddButtonsJPanel.add(createUserJButton);

        updateUserJButton.setText("Update");
        updateUserJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateUserJButtonActionPerformed(evt);
            }
        });
        userAddButtonsJPanel.add(updateUserJButton);

        addUserJPanel.add(userAddButtonsJPanel);

        usersJPanel.add(addUserJPanel, java.awt.BorderLayout.NORTH);

        userDetailsJPanel.setLayout(new java.awt.BorderLayout());

        usersListJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                usersListJListValueChanged(evt);
            }
        });
        userListJScrollPane.setViewportView(usersListJList);

        userDetailsJPanel.add(userListJScrollPane, java.awt.BorderLayout.CENTER);

        usersJPanel.add(userDetailsJPanel, java.awt.BorderLayout.CENTER);

        userButtonJPanel.setLayout(new java.awt.BorderLayout());

        userEnableJCheckBox1.setText("Enable");
        userEnableJCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                userEnableJCheckBox1ItemStateChanged(evt);
            }
        });
        userEnableJCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userEnableJCheckBox1ActionPerformed(evt);
            }
        });
        languageButtonLayoutJPanel1.add(userEnableJCheckBox1);

        deleteUserJButton.setText("Delete");
        deleteUserJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteUserJButtonActionPerformed(evt);
            }
        });
        languageButtonLayoutJPanel1.add(deleteUserJButton);

        userButtonJPanel.add(languageButtonLayoutJPanel1, java.awt.BorderLayout.LINE_END);

        usersJPanel.add(userButtonJPanel, java.awt.BorderLayout.SOUTH);

        rightJTabbedPane.addTab("Users", usersJPanel);

        rolesJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Roles"));
        rolesJPanel.setLayout(new java.awt.BorderLayout());

        currentRoleJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectRoleUserJLabel.setText("User: ");
        currentRoleJPanel.add(selectRoleUserJLabel);

        selectRoleUserJComboBox1.setEditable(true);
        selectRoleUserJComboBox1.setPreferredSize(new java.awt.Dimension(150, 22));
        selectRoleUserJComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectRoleUserJComboBox1ItemStateChanged(evt);
            }
        });
        selectRoleUserJComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectRoleUserJComboBox1ActionPerformed(evt);
            }
        });
        currentRoleJPanel.add(selectRoleUserJComboBox1);

        currentRoleJLabel.setText("Current Role: ");
        currentRoleJPanel.add(currentRoleJLabel);

        currentRoleJComboBox.setPreferredSize(new java.awt.Dimension(200, 22));
        currentRoleJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                currentRoleJComboBoxItemStateChanged(evt);
            }
        });
        currentRoleJPanel.add(currentRoleJComboBox);

        rolesJPanel.add(currentRoleJPanel, java.awt.BorderLayout.NORTH);

        rolesPanel.setLayout(new java.awt.GridLayout(1, 0));

        rolesAssignedScrollPane.setPreferredSize(new java.awt.Dimension(200, 70));

        rolesAssignedScrollPane.setViewportView(rolesAssignedJList);

        rolesPanel.add(rolesAssignedScrollPane);

        addRoleJButton.setText("<< Add Role");
        addRoleJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRoleJButtonActionPerformed(evt);
            }
        });

        removeRoleJButton.setText("Remove Role >>");
        removeRoleJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRoleJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rolesButtonsPanelLayout = new javax.swing.GroupLayout(rolesButtonsPanel);
        rolesButtonsPanel.setLayout(rolesButtonsPanelLayout);
        rolesButtonsPanelLayout.setHorizontalGroup(
            rolesButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rolesButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(87, Short.MAX_VALUE)
                .addGroup(rolesButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(removeRoleJButton)
                    .addComponent(addRoleJButton))
                .addGap(44, 44, 44))
        );

        rolesButtonsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addRoleJButton, removeRoleJButton});

        rolesButtonsPanelLayout.setVerticalGroup(
            rolesButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rolesButtonsPanelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(addRoleJButton)
                .addGap(7, 7, 7)
                .addComponent(removeRoleJButton)
                .addContainerGap(144, Short.MAX_VALUE))
        );

        rolesPanel.add(rolesButtonsPanel);

        rolesScrollPane.setPreferredSize(new java.awt.Dimension(200, 70));

        rolesScrollPane.setViewportView(rolesJList);

        rolesPanel.add(rolesScrollPane);

        rolesJPanel.add(rolesPanel, java.awt.BorderLayout.CENTER);

        rightJTabbedPane.addTab("User Roles", rolesJPanel);

        userOrganisationJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Roles"));
        userOrganisationJPanel.setLayout(new java.awt.BorderLayout());

        currentUserOrganisationJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectOrganisationUserJLabel.setText("User: ");
        currentUserOrganisationJPanel.add(selectOrganisationUserJLabel);

        selectOrganisationUserJComboBox.setEditable(true);
        selectOrganisationUserJComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
        selectOrganisationUserJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectOrganisationUserJComboBoxItemStateChanged(evt);
            }
        });
        currentUserOrganisationJPanel.add(selectOrganisationUserJComboBox);

        currentUserOrganisationJLabel.setText("Current Organisation: ");
        currentUserOrganisationJPanel.add(currentUserOrganisationJLabel);

        currentUserOrganisationJComboBox.setPreferredSize(new java.awt.Dimension(200, 22));
        currentUserOrganisationJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                currentUserOrganisationJComboBoxItemStateChanged(evt);
            }
        });
        currentUserOrganisationJPanel.add(currentUserOrganisationJComboBox);

        userOrganisationJPanel.add(currentUserOrganisationJPanel, java.awt.BorderLayout.NORTH);

        userOrganisationsPanel.setLayout(new java.awt.GridLayout(1, 0));

        organisationsAssignedScrollPane.setPreferredSize(new java.awt.Dimension(200, 130));

        organisationsAssignedScrollPane.setViewportView(organisationsAssignedJList);

        userOrganisationsPanel.add(organisationsAssignedScrollPane);

        addOrganisationJButton.setText("<< Add Organisation");
        addOrganisationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrganisationJButtonActionPerformed(evt);
            }
        });

        removeOrganisationJButton.setText("Remove Organisation >>");
        removeOrganisationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeOrganisationJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout userOrganisationsButtonsPanelLayout = new javax.swing.GroupLayout(userOrganisationsButtonsPanel);
        userOrganisationsButtonsPanel.setLayout(userOrganisationsButtonsPanelLayout);
        userOrganisationsButtonsPanelLayout.setHorizontalGroup(
            userOrganisationsButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userOrganisationsButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(67, Short.MAX_VALUE)
                .addGroup(userOrganisationsButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(addOrganisationJButton)
                    .addComponent(removeOrganisationJButton))
                .addGap(24, 24, 24))
        );

        userOrganisationsButtonsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addOrganisationJButton, removeOrganisationJButton});

        userOrganisationsButtonsPanelLayout.setVerticalGroup(
            userOrganisationsButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userOrganisationsButtonsPanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(addOrganisationJButton)
                .addGap(7, 7, 7)
                .addComponent(removeOrganisationJButton)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        userOrganisationsPanel.add(userOrganisationsButtonsPanel);

        organisationsScrollPane.setPreferredSize(new java.awt.Dimension(200, 130));

        organisationsJList.setPreferredSize(new java.awt.Dimension(200, 80));
        organisationsScrollPane.setViewportView(organisationsJList);

        userOrganisationsPanel.add(organisationsScrollPane);

        userOrganisationJPanel.add(userOrganisationsPanel, java.awt.BorderLayout.CENTER);

        rightJTabbedPane.addTab("User Organisations", userOrganisationJPanel);

        userLanguagesJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Roles"));
        userLanguagesJPanel.setLayout(new java.awt.BorderLayout());

        currentUserLangaugeJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectLanguageUserJLabel.setText("User: ");
        currentUserLangaugeJPanel.add(selectLanguageUserJLabel);

        selectLanguageUserJComboBox.setEditable(true);
        selectLanguageUserJComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
        selectLanguageUserJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectLanguageUserJComboBoxItemStateChanged(evt);
            }
        });
        currentUserLangaugeJPanel.add(selectLanguageUserJComboBox);

        currentUserLanguageJLabel.setText("Current Language: ");
        currentUserLangaugeJPanel.add(currentUserLanguageJLabel);

        currentUserLanguageJComboBox.setPreferredSize(new java.awt.Dimension(200, 22));
        currentUserLanguageJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                currentUserLanguageJComboBoxItemStateChanged(evt);
            }
        });
        currentUserLangaugeJPanel.add(currentUserLanguageJComboBox);

        userLanguagesJPanel.add(currentUserLangaugeJPanel, java.awt.BorderLayout.NORTH);

        languagesAssignedJPanel.setLayout(new java.awt.GridLayout(1, 0));

        languagesAssignedScrollPane.setPreferredSize(new java.awt.Dimension(200, 70));

        languagesAssignedScrollPane.setViewportView(languagesAssignedJList);

        languagesAssignedJPanel.add(languagesAssignedScrollPane);

        addLanguageJButton.setText("<< Add Language");
        addLanguageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLanguageJButtonActionPerformed(evt);
            }
        });

        removeLanguageJButton.setText("Remove Language >>");
        removeLanguageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLanguageJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout languagesButtonsPanelLayout = new javax.swing.GroupLayout(languagesButtonsPanel);
        languagesButtonsPanel.setLayout(languagesButtonsPanelLayout);
        languagesButtonsPanelLayout.setHorizontalGroup(
            languagesButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, languagesButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(74, Short.MAX_VALUE)
                .addGroup(languagesButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(removeLanguageJButton)
                    .addComponent(addLanguageJButton))
                .addGap(31, 31, 31))
        );

        languagesButtonsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addLanguageJButton, removeLanguageJButton});

        languagesButtonsPanelLayout.setVerticalGroup(
            languagesButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(languagesButtonsPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(addLanguageJButton)
                .addGap(7, 7, 7)
                .addComponent(removeLanguageJButton)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        languagesAssignedJPanel.add(languagesButtonsPanel);

        languagesScrollPane.setPreferredSize(new java.awt.Dimension(200, 70));

        languagesScrollPane.setViewportView(languagesJList);

        languagesAssignedJPanel.add(languagesScrollPane);

        userLanguagesJPanel.add(languagesAssignedJPanel, java.awt.BorderLayout.CENTER);

        rightJTabbedPane.addTab("User Languages", userLanguagesJPanel);

        annotationLevelsJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Roles"));
        annotationLevelsJPanel.setLayout(new java.awt.BorderLayout());

        currentAnnotationLevelJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectLevelUserJLabel.setText("User: ");
        currentAnnotationLevelJPanel.add(selectLevelUserJLabel);

        selectLevelUserJComboBox.setEditable(true);
        selectLevelUserJComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
        selectLevelUserJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectLevelUserJComboBoxItemStateChanged(evt);
            }
        });
        currentAnnotationLevelJPanel.add(selectLevelUserJComboBox);

        currentAnnotationLevelJLabel.setText("Current Level: ");
        currentAnnotationLevelJPanel.add(currentAnnotationLevelJLabel);

        currentAnnotationLevelJComboBox.setPreferredSize(new java.awt.Dimension(200, 22));
        currentAnnotationLevelJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                currentAnnotationLevelJComboBoxItemStateChanged(evt);
            }
        });
        currentAnnotationLevelJPanel.add(currentAnnotationLevelJComboBox);

        annotationLevelsJPanel.add(currentAnnotationLevelJPanel, java.awt.BorderLayout.NORTH);

        userAnnotationLevelsJPanel.setLayout(new java.awt.GridLayout(1, 0));

        annotationLevelsAssignedJScrollPane.setPreferredSize(new java.awt.Dimension(200, 70));

        annotationLevelsAssignedJScrollPane.setViewportView(annotationLevelsJAssignedJList);

        userAnnotationLevelsJPanel.add(annotationLevelsAssignedJScrollPane);

        addAnnotationLevelsJButton.setText("<< Add Level");
        addAnnotationLevelsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAnnotationLevelsJButtonActionPerformed(evt);
            }
        });

        removeAnnotationLevelsJButton.setText("Remove Level >>");
        removeAnnotationLevelsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAnnotationLevelsJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout annotationLevelsJButtonsPanelLayout = new javax.swing.GroupLayout(annotationLevelsJButtonsPanel);
        annotationLevelsJButtonsPanel.setLayout(annotationLevelsJButtonsPanelLayout);
        annotationLevelsJButtonsPanelLayout.setHorizontalGroup(
            annotationLevelsJButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, annotationLevelsJButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(85, Short.MAX_VALUE)
                .addGroup(annotationLevelsJButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(addAnnotationLevelsJButton)
                    .addComponent(removeAnnotationLevelsJButton))
                .addGap(42, 42, 42))
        );

        annotationLevelsJButtonsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addAnnotationLevelsJButton, removeAnnotationLevelsJButton});

        annotationLevelsJButtonsPanelLayout.setVerticalGroup(
            annotationLevelsJButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(annotationLevelsJButtonsPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(addAnnotationLevelsJButton)
                .addGap(7, 7, 7)
                .addComponent(removeAnnotationLevelsJButton)
                .addContainerGap(154, Short.MAX_VALUE))
        );

        userAnnotationLevelsJPanel.add(annotationLevelsJButtonsPanel);

        annotationLevelsScrollPane.setPreferredSize(new java.awt.Dimension(200, 70));

        annotationLevelsScrollPane.setViewportView(annotationLevelsJList);

        userAnnotationLevelsJPanel.add(annotationLevelsScrollPane);

        annotationLevelsJPanel.add(userAnnotationLevelsJPanel, java.awt.BorderLayout.CENTER);

        rightJTabbedPane.addTab("Annotation Levels", annotationLevelsJPanel);

        mainJSplitPane.setRightComponent(rightJTabbedPane);

        add(mainJSplitPane, java.awt.BorderLayout.CENTER);

        bottomJPanel.setLayout(new java.awt.GridLayout(1, 0));

        connectJButton.setText("Connect");
        connectJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectJButtonActionPerformed(evt);
            }
        });
        bottomJPanel.add(connectJButton);

        openRemoteJButton.setText("Open Remote");
        openRemoteJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRemoteJButtonActionPerformed(evt);
            }
        });
        bottomJPanel.add(openRemoteJButton);

        saveAnnotationManagementInfoJButton.setText("Save Annotation Management Data");
        saveAnnotationManagementInfoJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAnnotationManagementInfoJButtonActionPerformed(evt);
            }
        });
        bottomJPanel.add(saveAnnotationManagementInfoJButton);

        cancelJButton.setText("Cancel");
        bottomJPanel.add(cancelJButton);

        add(bottomJPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void connectJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectJButtonActionPerformed
        // TODO add your handling code here:        
        if(connectionMode == RemoteFileNode.RMI_MODE)
            connectRemoteRMI();
        else if (connectionMode == RemoteFileNode.SFTP_MODE)
            connectRemoteSftp();
        else if (connectionMode == RemoteFileNode.SPRING_MODE) {
            try {
                connectSpringWeb();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    
//        connectionStream = ShellConnectionStream.builder().build();
//
//        connectionStream.setHost(serverJTextField.getText());
//        connectionStream.setUsername(userJTextField.getText());
//        connectionStream.setPassword(passwordJTextField.getText());
//
//        Session session = null;
//        ChannelExec channel = null;
//        String command = "ls";
//        
//        try {                        
////            session.setConfig("StrictHostKeyChecking", "no");
//            
//            boolean connected  = connectionStream.connect();
//
//            session = connectionStream.getSession();
//
//            channel = (ChannelExec) session.openChannel("exec");
//            channel.setCommand(command);
//            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
//            channel.setOutputStream(responseStream);
//            channel.connect();
//        
//            String responseString = new String(responseStream.toByteArray());
//            System.out.println(responseString);
//            
//        } catch (JSchException ex) {
//            Logger.getLogger(SSHJPanel.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (session != null) {
//                session.disconnect();
//            }
//            if (channel != null) {
//                channel.disconnect();
//        }
//    }
        
    }//GEN-LAST:event_connectJButtonActionPerformed

    private void openRemoteJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRemoteJButtonActionPerformed
        // TODO add your handling code here:            

        if(connectionMode == RemoteFileNode.RMI_MODE || connectionMode == RemoteFileNode.SFTP_MODE) {
            if (!connected || sessionId == null) {
                JOptionPane.showMessageDialog(this, "Please connect to the remote server.", GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if(connectionMode == RemoteFileNode.SPRING_MODE) {
            if (!connected || currentUser == null) {
                JOptionPane.showMessageDialog(this, "Please connect to the remote server.", GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

//        if(remoteFileExplorerJPanel.getCurrentSelectedNodePath() != null)
//        {
//            JOptionPane.showMessageDialog(this, "Please select a file by double click\nto open remote file.", GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);            
//            return;
//        }
        
        boolean success = downloadRemoteFile();

        if(!success)
        {
            JOptionPane.showMessageDialog(this, "Please select a file by double click\nto open remote file.", GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);            
            return;
        }

        if(connectionMode == RemoteFileNode.RMI_MODE)
        {
            taskName = rootRemoteFileNode.getRemoteRMIFile().getFileName();
//            taskPath = rootRemoteFileNode.getRemoteRMIFile().getRelativePath();
            taskPath = rootRemoteFileNode.getRemoteRMIFile().getAbsolutePathOnClient();
        }
        else if(connectionMode == RemoteFileNode.SPRING_MODE)
        {
            taskName = rootRemoteFileNode.getRemoteRMIFile().getFileName();
//            taskPath = rootRemoteFileNode.getRemoteRMIFile().getRelativePath();
            taskPath = rootRemoteFileNode.getRemoteRMIFile().getAbsolutePathOnClient();
        }
        else if(connectionMode == RemoteFileNode.SFTP_MODE)
        {
            taskName = rootRemoteFileNode.getRemoteSftpFile().getLsEntry().getFilename();
            taskPath = rootRemoteFileNode.getRemoteSftpFile().getPath();
        }

        showWorkDialog();        
    }//GEN-LAST:event_openRemoteJButtonActionPerformed

    private void leftJTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_leftJTabbedPaneStateChanged
        // TODO add your handling code here:
        int indexSelected = leftJTabbedPane.getSelectedIndex();

        if(indexSelected >= 0 && rightJTabbedPane.getComponentCount() > 0)
        {
            rightJTabbedPane.setSelectedIndex(indexSelected);
        }
    }//GEN-LAST:event_leftJTabbedPaneStateChanged

    private void connectionModeJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectionModeJComboBoxActionPerformed
        // TODO add your handling code here:
        String modeString = (String) connectionModeJComboBox.getSelectedItem();
        
        if(modeString.equals("Spring Mode"))
        {
            connectionMode = RemoteFileNode.SPRING_MODE;
        }
        else if(modeString.equals("RMI Mode"))
        {
            connectionMode = RemoteFileNode.RMI_MODE;
        }
        else if(modeString.equals("SFTP Mode"))
        {
            connectionMode = RemoteFileNode.SFTP_MODE;
        }
    }//GEN-LAST:event_connectionModeJComboBoxActionPerformed

    private void annotationManagementJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annotationManagementJCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_annotationManagementJCheckBoxActionPerformed

    private void organisationsListJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_organisationsListJListValueChanged
        // TODO add your handling code here:
        String orgDetails = organisationsListJList.getSelectedValue();
        
        if(orgDetails != null)
        {
            String orgShortName = orgDetails.split(" <=> ")[0];

            Map<String, SanchayOrganisationDTO> sanchayOrganisationMap = null;
//            try {
                sanchayOrganisationMap = annotationManagementUpdateInfo.getAllOrganisations();
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }

            SanchayOrganisationDTO sanchayOrganisation = sanchayOrganisationMap.get(orgShortName);

            organisationShortNameJTextField.setText(sanchayOrganisation.getName());
            organisationLongNameJTextField.setText(sanchayOrganisation.getLongName());
        }
    }//GEN-LAST:event_organisationsListJListValueChanged

    private void languagesListJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_languagesListJListValueChanged
        // TODO add your handling code here:
        String languageName = languagesListJList.getSelectedValue();

        if(languageName != null)
        {
            Map<String, SanchayResourceLanguageDTO> languageMap = null;
//            try {
//                languageMap = sanchaySpringRestClient.getAllLanguages();
                languageMap = annotationManagementUpdateInfo.getAllLanguages();
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }

            SanchayResourceLanguageDTO sanchayResourceLanguage = languageMap.get(languageName);

            languageNameJTextField.setText(sanchayResourceLanguage.getName());
        }
    }//GEN-LAST:event_languagesListJListValueChanged

    private void usersListJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_usersListJListValueChanged
        // TODO add your handling code here:
        if(usersListJList.getSelectedValue() != null)
        {
            String username = usersListJList.getSelectedValue().split("; ")[0];

            if(username != null && username.equals("") == false)
            {
                Map<String, SanchayUserDTO> userMap = null;
//                try {
//                    userMap = sanchaySpringRestClient.getAllUsers();
                    userMap = annotationManagementUpdateInfo.getAllUsers();
//                } catch (JsonProcessingException e) {
//                    throw new RuntimeException(e);
//                }

                SanchayUserDTO sanchayUser = userMap.get(username);

                if(sanchayUser != null) {
                    usernameJTextField.setText(sanchayUser.getUsername());
                    passwordJTextField1.setText("");
                    emailJTextField.setText(sanchayUser.getEmailAddress());
                    firstNameJTextField.setText(sanchayUser.getFirstName());
                    lastNameJTextField.setText(sanchayUser.getLastName());
                }
            }
        }
    }//GEN-LAST:event_usersListJListValueChanged

    private void selectRoleUserJComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectRoleUserJComboBox1ItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectRoleUserJComboBox1.getSelectedItem();
        
//        SanchayUserDTO selectedUser = sanchaySpringRestClient.getUser(username);
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        fillUserRoleInfo(selectedUser);        
    }//GEN-LAST:event_selectRoleUserJComboBox1ItemStateChanged

    private void currentRoleJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_currentRoleJComboBoxItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectRoleUserJComboBox1.getSelectedItem();
        String rolename = (String) currentRoleJComboBox.getSelectedItem();
        
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        selectedUser.setDirty(true);
        
        selectedUser.setCurrentRoleName(rolename);        
    }//GEN-LAST:event_currentRoleJComboBoxItemStateChanged

    private void selectOrganisationUserJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectOrganisationUserJComboBoxItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectOrganisationUserJComboBox.getSelectedItem();
        
//        SanchayUserDTO selectedUser = sanchaySpringRestClient.getUser(username);
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        fillUserOrganisationInfo(selectedUser);
    }//GEN-LAST:event_selectOrganisationUserJComboBoxItemStateChanged

    private void currentUserOrganisationJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_currentUserOrganisationJComboBoxItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectOrganisationUserJComboBox.getSelectedItem();
        String name = (String) currentUserOrganisationJComboBox.getSelectedItem();
        
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        selectedUser.setCurrentOrganisationName(name);        
    }//GEN-LAST:event_currentUserOrganisationJComboBoxItemStateChanged

    private void selectLanguageUserJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectLanguageUserJComboBoxItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectLanguageUserJComboBox.getSelectedItem();
        
//        SanchayUserDTO selectedUser = sanchaySpringRestClient.getUser(username);
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        fillUserLangaugesInfo(selectedUser);
    }//GEN-LAST:event_selectLanguageUserJComboBoxItemStateChanged

    private void currentUserLanguageJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_currentUserLanguageJComboBoxItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectLanguageUserJComboBox.getSelectedItem();
        String name = (String) currentUserLanguageJComboBox.getSelectedItem();
        
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        selectedUser.setCurrentLanguageName(name);        
    }//GEN-LAST:event_currentUserLanguageJComboBoxItemStateChanged

    private void selectLevelUserJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectLevelUserJComboBoxItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectLevelUserJComboBox.getSelectedItem();
        
//        SanchayUserDTO selectedUser = sanchaySpringRestClient.getUser(username);
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        fillAnnotationLevelInfo(selectedUser);
    }//GEN-LAST:event_selectLevelUserJComboBoxItemStateChanged

    private void currentAnnotationLevelJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_currentAnnotationLevelJComboBoxItemStateChanged
        // TODO add your handling code here:
        String username = (String) selectLevelUserJComboBox.getSelectedItem();
        String name = (String) currentAnnotationLevelJComboBox.getSelectedItem();
        
        SanchayUserDTO selectedUser = annotationManagementUpdateInfo.getAllUsers().get(username);
        
        selectedUser.setCurrentAnnotationLevelName(name);        
    }//GEN-LAST:event_currentAnnotationLevelJComboBoxItemStateChanged

    private void createOrganisationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createOrganisationJButtonActionPerformed
        // TODO add your handling code here:
        String shortName = organisationShortNameJTextField.getText();
        String longName = organisationLongNameJTextField.getText();
        
        if(shortName != null && !shortName.equals("") && longName != null && !longName.equals(""))
        {
            String orgDetails = shortName + " <=> " + longName;
                        
            DefaultListModel listModel = ((DefaultListModel) organisationsListJList.getModel());
            
            if(!listModel.contains(orgDetails))
            {
                listModel.addElement(orgDetails);
                ((DefaultListModel) organisationsJList.getModel()).addElement(shortName + " <=> " + longName);

                SanchayOrganisationDTO organisationDTO = SanchayOrganisationDTO.builder().name(shortName).longName(longName).build();
                SanchayOrganisationSlimDTO organisationSlimDTO = SanchayOrganisationSlimDTO.builder().name(shortName).longName(longName).build();
//                organisationDTO.setSlimDTO(organisationSlimDTO);

                organisationDTO.setDirty(true);
                organisationDTO.setToBeAdded(true);
                organisationSlimDTO.setToBeAdded(true);

                annotationManagementUpdateInfo.getAllOrganisations().put(organisationDTO.getName(), organisationDTO);
                annotationManagementUpdateInfo.getAllSlimOrganisations().put(organisationDTO.getName(), organisationSlimDTO);
            }
        }
    }//GEN-LAST:event_createOrganisationJButtonActionPerformed

    private void deleteOrganisationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOrganisationJButtonActionPerformed
        // TODO add your handling code here:
        if(organisationsListJList.getSelectedValue() != null)
        {
            String orgDetails = organisationsListJList.getSelectedValue();

            ((DefaultListModel<String>) organisationsListJList.getModel()).removeElement(orgDetails);
            ((DefaultListModel<String>) organisationsJList.getModel()).removeElement(orgDetails);
            
            String shortName = orgDetails.split(" <=> ")[0];
            
            SanchayOrganisationDTO selectedOrganisation = annotationManagementUpdateInfo.getAllOrganisations().get(shortName);
            selectedOrganisation.setDirty(true);
            selectedOrganisation.setToBeDeleted(true);

            SanchayOrganisationSlimDTO selectedSlimOrganisation = annotationManagementUpdateInfo.getAllSlimOrganisations().get(shortName);
            selectedSlimOrganisation.setToBeDeleted(true);

//            annotationManagementUpdateInfo.getAllOrganisations().remove(shortName);
//            annotationManagementUpdateInfo.getAllSlimOrganisations().remove(shortName);
//            annotationManagementUpdateInfo.getAllOrganisations().remove(shortName, selectedOrganisation);
        }
    }//GEN-LAST:event_deleteOrganisationJButtonActionPerformed

    private void createLanguageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createLanguageJButtonActionPerformed
        // TODO add your handling code here:
        String name = languageNameJTextField.getText();
        
        if(name != null && !name.equals(""))
        {
            DefaultListModel<String> listModel = ((DefaultListModel<String>) languagesListJList.getModel());

            if(!listModel.contains(name))
            {
                listModel.addElement(name);
                ((DefaultListModel<String>) languagesJList.getModel()).addElement(name);

                SanchayResourceLanguageDTO languageDTO = SanchayResourceLanguageDTO.builder().name(name).build();
                SanchayResourceLanguageSlimDTO languageSlimDTO = SanchayResourceLanguageSlimDTO.builder().name(name).build();
//                languageDTO.setSlimDTO(languageSlimDTO);

                languageDTO.setDirty(true);
                languageDTO.setToBeAdded(true);
                languageSlimDTO.setToBeAdded(true);

                annotationManagementUpdateInfo.getAllLanguages().put(languageDTO.getName(), languageDTO);
                annotationManagementUpdateInfo.getAllSlimLanguages().put(languageDTO.getName(), languageSlimDTO);
            }
        }
    }//GEN-LAST:event_createLanguageJButtonActionPerformed

    private void deleteLanguageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLanguageJButtonActionPerformed
        // TODO add your handling code here:
        if(languagesListJList.getSelectedValue() != null)
        {
            String name = languagesListJList.getSelectedValue();

            ((DefaultListModel<String>) languagesListJList.getModel()).removeElement(name);
            ((DefaultListModel<String>) languagesListJList.getModel()).removeElement(name);
            
            SanchayResourceLanguageDTO languageDTO = annotationManagementUpdateInfo.getAllLanguages().get(name);
            SanchayResourceLanguageSlimDTO languageSlimDTO = annotationManagementUpdateInfo.getAllSlimLanguages().get(name);
            
            languageDTO.setDirty(true);
            languageDTO.setToBeDeleted(true);
            languageSlimDTO.setToBeDeleted(true);

//            annotationManagementUpdateInfo.getAllLanguages().remove(name);
//            annotationManagementUpdateInfo.getAllSlimLanguages().remove(name);
        }
    }//GEN-LAST:event_deleteLanguageJButtonActionPerformed
    
    private void deleteUserJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteUserJButtonActionPerformed
        // TODO add your handling code here:
        if(usersListJList.getSelectedValue() != null)
        {
            String username = usersListJList.getSelectedValue().split("; ")[0];

            if(username != null && username.equals("") == false)
            {
                ((DefaultListModel<String>) usersListJList.getModel()).removeElement(usersListJList.getSelectedValue());
                
                selectRoleUserJComboBox1.removeItem(username);
                selectOrganisationUserJComboBox.removeItem(username);
                selectLevelUserJComboBox.removeItem(username);

                SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);
                SanchayUserSlimDTO userSlimDTO = annotationManagementUpdateInfo.getAllSlimUsers().get(username);
                
                userDTO.setDirty(true);
                userDTO.setToBeDeleted(true);
                userSlimDTO.setToBeDeleted(true);

//                annotationManagementUpdateInfo.getAllUsers().remove(username);
//                annotationManagementUpdateInfo.getAllSlimUsers().remove(username);
            }
        }
    }//GEN-LAST:event_deleteUserJButtonActionPerformed

    private void userEnableJCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_userEnableJCheckBox1ItemStateChanged
        // TODO add your handling code here:
        if(usersListJList.getSelectedValue() != null)
        {
            String username = usersListJList.getSelectedValue().split("; ")[0];

            if(username != null && username.equals("") == false)
            {
                SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);
                SanchayUserSlimDTO userSlimDTO = annotationManagementUpdateInfo.getAllSlimUsers().get(username);
                
                userDTO.setEnabled(userEnableJCheckBox1.isSelected());
                userSlimDTO.setEnabled(userEnableJCheckBox1.isSelected());
            
                userDTO.setDirty(true);
            }
        }        
    }//GEN-LAST:event_userEnableJCheckBox1ItemStateChanged

    private void addRoleJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRoleJButtonActionPerformed
        // TODO add your handling code here:
        String name = rolesJList.getSelectedValue();
        
        if(name != null)
        {
            DefaultListModel<String> listModel = (DefaultListModel) rolesAssignedJList.getModel();
            
            if(!listModel.contains(name))
            {
                listModel.addElement(name);

                listModel = (DefaultListModel) rolesJList.getModel();
                listModel.removeElement(name);
                
                String username = (String) selectRoleUserJComboBox1.getSelectedItem();
                
                SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);
                
                currentRoleJComboBox.addItem(name);
                
                SanchayRoleDTO roleDTO = annotationManagementUpdateInfo.getAllRoles().get(name);
//                SanchayRoleSlimDTO roleSlimDTO = annotationManagementUpdateInfo.getAllSlimRoles().get(name);
                
                userDTO.addRole(roleDTO);
//                userDTO.addRole(roleSlimDTO);
//                userDTO.setDirty(true);
//                userDTO.addRole(sanchaySpringRestClient.getModelMapper().map(roleDTO, SanchayRoleSlimDTO.class));
//                userDTO.addRole(roleDTO);
            }
        }
    }//GEN-LAST:event_addRoleJButtonActionPerformed

    private void removeRoleJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRoleJButtonActionPerformed
        // TODO add your handling code here:
        String name = rolesAssignedJList.getSelectedValue();
        
        if(name != null)
        {
            DefaultListModel listModel = (DefaultListModel) rolesAssignedJList.getModel();
    
            listModel.removeElement(name);

            listModel = (DefaultListModel) rolesJList.getModel();

            if(!listModel.contains(name))
            {
                listModel.addElement(name);
            }

            String username = (String) selectRoleUserJComboBox1.getSelectedItem();

            SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);
            userDTO.setDirty(true);

            currentRoleJComboBox.removeItem(name);

            SanchayRoleDTO roleDTO = annotationManagementUpdateInfo.getAllRoles().get(name);
//            SanchayRoleSlimDTO roleSlimDTO = annotationManagementUpdateInfo.getAllSlimRoles().get(name);

            userDTO.removeRole(roleDTO);
//            userDTO.removeRole(roleSlimDTO);
//            userDTO.removeRole(sanchaySpringRestClient.getModelMapper().map(roleDTO, SanchayRoleSlimDTO.class));
        }        
    }//GEN-LAST:event_removeRoleJButtonActionPerformed

    private void addLanguageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLanguageJButtonActionPerformed
        // TODO add your handling code here:
        String name = languagesJList.getSelectedValue();
        
        if(name != null)
        {            
            DefaultListModel listModel = (DefaultListModel) languagesAssignedJList.getModel();

            if(!listModel.contains(name))
            {
                listModel.addElement(name);

                listModel = (DefaultListModel) languagesJList.getModel();
                listModel.removeElement(name);

                String username = (String) selectLanguageUserJComboBox.getSelectedItem();

                SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);
                
                currentUserLanguageJComboBox.addItem(name);
                
                SanchayResourceLanguageDTO languageDTO = annotationManagementUpdateInfo.getAllLanguages().get(name);
//                SanchayResourceLanguageSlimDTO languageSlimDTO = annotationManagementUpdateInfo.getAllSlimLanguages().get(name);
 
                userDTO.addLanguage(languageDTO);
//                userDTO.addLanguage(languageSlimDTO);
//                userDTO.addLanguage(sanchaySpringRestClient.getModelMapper().map(languageDTO, SanchayResourceLanguageSlimDTO.class));
            }
        }
    }//GEN-LAST:event_addLanguageJButtonActionPerformed

    private void removeLanguageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLanguageJButtonActionPerformed
        // TODO add your handling code here:
        String name = languagesAssignedJList.getSelectedValue();
        
        if(name != null)
        {
            DefaultListModel listModel = (DefaultListModel) languagesAssignedJList.getModel();
            listModel.removeElement(name);
            
            listModel = (DefaultListModel) languagesJList.getModel();

            if(!listModel.contains(name))
            {
                listModel.addElement(name);
            }

            String username = (String) selectLanguageUserJComboBox.getSelectedItem();

            SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);

            currentUserLanguageJComboBox.removeItem(name);

            SanchayResourceLanguageDTO languageDTO = annotationManagementUpdateInfo.getAllLanguages().get(name);
//            SanchayResourceLanguageSlimDTO languageSlimDTO = annotationManagementUpdateInfo.getAllSlimLanguages().get(name);

            userDTO.removeLanguage(languageDTO);
//            userDTO.removeLanguage(languageSlimDTO);
//            userDTO.removeLanguage(sanchaySpringRestClient.getModelMapper().map(languageDTO, SanchayResourceLanguageSlimDTO.class));
        }
    }//GEN-LAST:event_removeLanguageJButtonActionPerformed

    private void addOrganisationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOrganisationJButtonActionPerformed
        // TODO add your handling code here:
        String name = organisationsJList.getSelectedValue();
        
        if(name != null && name.equals("") == false)
        {
            DefaultListModel listModel = (DefaultListModel) organisationsAssignedJList.getModel();

            if(!listModel.contains(name))
            {
                listModel.addElement(name);

                listModel = (DefaultListModel) organisationsJList.getModel();
                listModel.removeElement(name);
                
                String username = (String) selectOrganisationUserJComboBox.getSelectedItem();
                
                SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);
                
                String shortName = name.split(" <=> ")[0];
                
                currentUserOrganisationJComboBox.addItem(shortName);
                
//                SanchayOrganisationSlimDTO organisationSlimDTO = annotationManagementUpdateInfo.getAllSlimOrganisations().get(shortName);
                SanchayOrganisationDTO organisationDTO = annotationManagementUpdateInfo.getAllOrganisations().get(shortName);

                userDTO.addOrganisation(organisationDTO);
//                userDTO.addOrganisation(organisationSlimDTO);
//                userDTO.addOrganisation(sanchaySpringRestClient.getModelMapper().map(organisationDTO, SanchayOrganisationSlimDTO.class));
            }
        }
    }//GEN-LAST:event_addOrganisationJButtonActionPerformed

    private void removeOrganisationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeOrganisationJButtonActionPerformed
        // TODO add your handling code here:
        String name = organisationsAssignedJList.getSelectedValue();
        
        if(name != null && name.equals("") == false)
        {
            DefaultListModel listModel = (DefaultListModel) organisationsAssignedJList.getModel();
            listModel.removeElement(name);
            
            listModel = (DefaultListModel) organisationsJList.getModel();

            if(!listModel.contains(name))
            {
                listModel.addElement(name);
            }

            String username = (String) selectOrganisationUserJComboBox.getSelectedItem();

            SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);

            String shortName = name.split(" <=> ")[0];

            currentUserOrganisationJComboBox.removeItem(shortName);

            SanchayOrganisationDTO organisationDTO = annotationManagementUpdateInfo.getAllOrganisations().get(shortName);
//            SanchayOrganisationSlimDTO organisationSlimDTO = annotationManagementUpdateInfo.getAllSlimOrganisations().get(shortName);

            userDTO.removeOrganisation(organisationDTO);
//            userDTO.removeOrganisation(organisationSlimDTO);
//            userDTO.removeOrganisation(sanchaySpringRestClient.getModelMapper().map(organisationDTO, SanchayOrganisationSlimDTO.class));
        }
    }//GEN-LAST:event_removeOrganisationJButtonActionPerformed

    private void addAnnotationLevelsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAnnotationLevelsJButtonActionPerformed
        // TODO add your handling code here:
        String name = annotationLevelsJList.getSelectedValue();
        
        if(name != null && name.equals("") == false)
        {            
            DefaultListModel listModel = (DefaultListModel) annotationLevelsJAssignedJList.getModel();

            if(!listModel.contains(name))
            {
                listModel.addElement(name);

                listModel = (DefaultListModel) annotationLevelsJList.getModel();
                listModel.removeElement(name);

                String username = (String) selectLevelUserJComboBox.getSelectedItem();

                SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);

                currentAnnotationLevelJComboBox.addItem(name);

                SanchayAnnotationLevelDTO levelDTO = annotationManagementUpdateInfo.getAllLevels().get(name);
//                SanchayAnnotationLevelSlimDTO levelSlimDTO = annotationManagementUpdateInfo.getAllSlimLevels().get(name);

                userDTO.addAnnotationLevel(levelDTO);
//                userDTO.addAnnotationLevel(levelSlimDTO);
//                userDTO.addAnnotationLevel(sanchaySpringRestClient.getModelMapper().map(levelDTO, SanchayAnnotationLevelSlimDTO.class));
            }
        }        
    }//GEN-LAST:event_addAnnotationLevelsJButtonActionPerformed

    private void removeAnnotationLevelsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAnnotationLevelsJButtonActionPerformed
        // TODO add your handling code here:
        String name = annotationLevelsJAssignedJList.getSelectedValue();
        
        if(name != null && name.equals("") == false)
        {
            DefaultListModel listModel = (DefaultListModel) annotationLevelsJAssignedJList.getModel();
            listModel.removeElement(name);
            
            listModel = (DefaultListModel) annotationLevelsJList.getModel();

            if(!listModel.contains(name))
            {
                listModel.addElement(name);
            }

            String username = (String) selectLevelUserJComboBox.getSelectedItem();

            SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);

            currentAnnotationLevelJComboBox.removeItem(name);

            SanchayAnnotationLevelDTO levelDTO = annotationManagementUpdateInfo.getAllLevels().get(name);
//            SanchayAnnotationLevelSlimDTO levelSlimDTO = annotationManagementUpdateInfo.getAllSlimLevels().get(name);

            userDTO.removeAnnotationLevel(levelDTO);
//            userDTO.removeAnnotationLevel(levelSlimDTO);
//            userDTO.removeAnnotationLevel(sanchaySpringRestClient.getModelMapper().map(levelDTO, SanchayAnnotationLevelSlimDTO.class));
        }        
    }//GEN-LAST:event_removeAnnotationLevelsJButtonActionPerformed

    private void saveAnnotationManagementInfoJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAnnotationManagementInfoJButtonActionPerformed
        try {
            // TODO add your handling code here:
            sanchaySpringRestClient.saveAnnotationManagementUpdateInfo(annotationManagementUpdateInfo);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveAnnotationManagementInfoJButtonActionPerformed

    private void createUserJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createUserJButtonActionPerformed
        // TODO add your handling code here:
        String username = usernameJTextField.getText();
        String password = String.valueOf(passwordJTextField1.getPassword());
        String email = emailJTextField.getText();
        String firstName = firstNameJTextField.getText();
        String lastName = lastNameJTextField.getText();
        
        if(username != null && password != null && email != null && firstName != null && lastName != null
            && !username.equals("") && !password.equals("") && !email.equals("") && !firstName.equals("")  && !lastName.equals(""))
        {
            SanchayUserDTO userDTO = SanchayUserDTO.builder().username(username)
                    .password(password)
                    .emailAddress(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            
            userDTO.setDirty(true);

            SanchayUserSlimDTO userSlimDTO = SanchayUserSlimDTO.builder().username(username)
                    .password(password)
                    .emailAddress(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            
//            userDTO.setSlimDTO(userSlimDTO);

            userDTO.setToBeAdded(true);
            userSlimDTO.setToBeAdded(true);

            annotationManagementUpdateInfo.getAllUsers().put(userDTO.getUsername(), userDTO);
            annotationManagementUpdateInfo.getAllSlimUsers().put(userDTO.getUsername(), userSlimDTO);
//            annotationManagementUpdateInfo.getAssignedUserRoles().put(userDTO.getUsername(), new LinkedHashMap<>());
//            annotationManagementUpdateInfo.getAssignedUserOrganisations().put(userDTO.getUsername(), new LinkedHashMap<>());
//            annotationManagementUpdateInfo.getAssignedUserLanguages().put(userDTO.getUsername(), new LinkedHashMap<>());
//            annotationManagementUpdateInfo.getAssignedUserLevels().put(userDTO.getUsername(), new LinkedHashMap<>());

            DefaultListModel listModel = (DefaultListModel) usersListJList.getModel();

            String value = username + "; "
                            + firstName + "; "
                            + lastName + "; ";
            
            if(!listModel.contains(value))
            {
                listModel.addElement(value);

                value = username;

                DefaultComboBoxModel<String> cbModel = (DefaultComboBoxModel<String>) selectRoleUserJComboBox1.getModel();

                cbModel.addElement(value);

                cbModel = (DefaultComboBoxModel<String>) selectLanguageUserJComboBox.getModel();

                cbModel.addElement(value);

                cbModel = (DefaultComboBoxModel<String>) selectOrganisationUserJComboBox.getModel();

                cbModel.addElement(value);

                cbModel = (DefaultComboBoxModel<String>) selectLevelUserJComboBox.getModel();

                cbModel.addElement(value);
            }
        }
        
    }//GEN-LAST:event_createUserJButtonActionPerformed

    private void updateOrganisationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateOrganisationJButtonActionPerformed
        // TODO add your handling code here:
        String shortName = organisationShortNameJTextField.getText();
        String longName = organisationLongNameJTextField.getText();
        
        if(shortName != null && !shortName.equals("") && longName != null && !longName.equals(""))
        {
            SanchayOrganisationDTO organisationDTO = annotationManagementUpdateInfo.getAllOrganisations().get(shortName);
            SanchayOrganisationSlimDTO organisationSlimDTO = annotationManagementUpdateInfo.getAllSlimOrganisations().get(shortName);
            
            organisationDTO.setName(shortName);
            organisationDTO.setLongName(longName);
            organisationSlimDTO.setName(shortName);
            organisationSlimDTO.setLongName(longName);

            organisationDTO.setDirty(true);
        }
    }//GEN-LAST:event_updateOrganisationJButtonActionPerformed

    private void updateLanguageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateLanguageJButtonActionPerformed
        // TODO add your handling code here:
        String name = languageNameJTextField.getText();
        
        if(name != null && !name.equals(""))
        {
            SanchayResourceLanguageDTO languageDTO = annotationManagementUpdateInfo.getAllLanguages().get(name);
            SanchayResourceLanguageSlimDTO languageSlimDTO = annotationManagementUpdateInfo.getAllSlimLanguages().get(name);

            languageDTO.setName(name);
            languageSlimDTO.setName(name);
            
            languageDTO.setDirty(true);
        }
    }//GEN-LAST:event_updateLanguageJButtonActionPerformed

    private void updateUserJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateUserJButtonActionPerformed
        // TODO add your handling code here:
        String username = usernameJTextField.getText();
        String password = String.valueOf(passwordJTextField1.getPassword());
        String email = emailJTextField.getText();
        String firstName = firstNameJTextField.getText();
        String lastName = lastNameJTextField.getText();

        if(username != null && password != null && email != null && firstName != null && lastName != null
                && !username.equals("") && !password.equals("") && !email.equals("") && !firstName.equals("")  && !lastName.equals(""))
        {
            SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);
            SanchayUserSlimDTO userSlimDTO = annotationManagementUpdateInfo.getAllSlimUsers().get(username);
            
            userDTO.setUsername(username);
            userDTO.setPassword(password);
            userDTO.setEmailAddress(email);
            userDTO.setFirstName(firstName);
            userDTO.setLastName(lastName);
            
            userDTO.setDirty(true);
            
            userSlimDTO.setUsername(username);
            userSlimDTO.setPassword(password);
            userSlimDTO.setEmailAddress(email);
            userSlimDTO.setFirstName(firstName);
            userSlimDTO.setLastName(lastName);
        }
    }//GEN-LAST:event_updateUserJButtonActionPerformed

    private void selectRoleUserJComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectRoleUserJComboBox1ActionPerformed
        // TODO add your handling code here:
        String username = usernameJTextField.getText();
        
        if(username != null && !username.equals(""))
        {
            SanchayUserDTO userDTO = annotationManagementUpdateInfo.getAllUsers().get(username);

            userDTO.setEnabled(userEnableJCheckBox1.isSelected());            
            userDTO.setDirty(true);
            
            SanchayUserSlimDTO userSlimDTO = annotationManagementUpdateInfo.getAllSlimUsers().get(username);

            userSlimDTO.setEnabled(userEnableJCheckBox1.isSelected());            
        }
        
    }//GEN-LAST:event_selectRoleUserJComboBox1ActionPerformed

    private void userEnableJCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userEnableJCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userEnableJCheckBox1ActionPerformed

    private void showAnnotationManagementTabs()
    {
        String sanchayRole = currentUser.getCurrentRoleName();
        
        if(sanchayRole.equals(SanchayRole.VIEWER) || sanchayRole.equals(SanchayRole.ANNOTATOR)
                || sanchayRole.equals(SanchayRole.VALIDATOR))
        {
            hideAnnotationManagementTabs();
        }
        else
        {
            rightJTabbedPane.add("Organisations", organisationsJPanel);
            rightJTabbedPane.add("Languages", languagesJPanel);
            rightJTabbedPane.add("Users", usersJPanel);
            rightJTabbedPane.add("User Roles", rolesJPanel);
            rightJTabbedPane.add("User Organisations", userOrganisationJPanel);
            rightJTabbedPane.add("User Languages", userLanguagesJPanel);
            rightJTabbedPane.add("Annotation Levels", annotationLevelsJPanel);

            fillUsersInfo();
            fillRolesInfo();
            fillOrganisationsInfo();
            fillLanguagesInfo();
            fillAnnotationLevelInfo();
            fillUserRoleInfo(currentUser);
            fillUserOrganisationInfo(currentUser);
            fillUserLangaugesInfo(currentUser);
            fillAnnotationLevelInfo(currentUser);

        }
    }

    private void fillOrganisationsInfo()
    {
        Map<String, SanchayOrganisationDTO> sanchayOrganisationMap = null;
//        try {
//            sanchayOrganisationMap = sanchaySpringRestClient.getAllOrganisations();
            sanchayOrganisationMap = annotationManagementUpdateInfo.getAllOrganisations();
//        }
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel1 = (DefaultListModel) organisationsListJList.getModel();
        DefaultListModel listModel2 = (DefaultListModel) organisationsJList.getModel();

        listModel1.removeAllElements();
        listModel2.removeAllElements();

        sanchayOrganisationMap.entrySet().forEach(
                entry ->
                {
                    String value = entry.getValue().getName() + " <=> " + entry.getValue().getLongName();
                    
                    listModel1.addElement(value);
                    listModel2.addElement(value);
                }
        );
    }

    private void fillLanguagesInfo()
    {
        Map<String, SanchayResourceLanguageDTO> languageMap = null;
        languageMap = annotationManagementUpdateInfo.getAllLanguages();

//        try {
//            languageMap = sanchaySpringRestClient.getAllLanguages();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel1 = (DefaultListModel) languagesListJList.getModel();
        DefaultListModel listModel2 = (DefaultListModel) languagesJList.getModel();

        listModel1.removeAllElements();
        listModel2.removeAllElements();

        languageMap.entrySet().forEach(
                entry -> 
                {
                    String value = entry.getValue().getName();

                    listModel1.addElement(value);
                    listModel2.addElement(value);
                }
        );
    }

    private void fillUsersInfo()
    {
        Map<String, SanchayUserDTO> userMap = null;
        
        userMap = annotationManagementUpdateInfo.getAllUsers();
        
//        try {
//            userMap = sanchaySpringRestClient.getAllUsers();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel = (DefaultListModel) usersListJList.getModel();

        listModel.removeAllElements();

        if(selectRoleUserJComboBox1.getItemCount() > 0) {
            selectRoleUserJComboBox1.removeAllItems();
        }

        if(selectLanguageUserJComboBox.getItemCount() > 0) {
            selectLanguageUserJComboBox.removeAllItems();
        }

        if(selectOrganisationUserJComboBox.getItemCount() > 0) {
            selectOrganisationUserJComboBox.removeAllItems();
        }

        if(selectLevelUserJComboBox.getItemCount() > 0) {
            selectLevelUserJComboBox.removeAllItems();
        }

        userMap.entrySet().forEach(
                entry ->
                {
                    if(!entry.getValue().isToBeDeleted())
                    {
                        String value = entry.getValue().getUsername() + "; "
                                + entry.getValue().getFirstName() + "; "
                                + entry.getValue().getLastName() + "; "
                                + entry.getValue().getCurrentOrganisationName();

                        listModel.addElement(value);

                        value = entry.getValue().getUsername();

                        DefaultComboBoxModel<String> cbModel = (DefaultComboBoxModel<String>) selectRoleUserJComboBox1.getModel();

                        cbModel.addElement(value);

                        cbModel = (DefaultComboBoxModel<String>) selectLanguageUserJComboBox.getModel();

                        cbModel.addElement(value);

                        cbModel = (DefaultComboBoxModel<String>) selectOrganisationUserJComboBox.getModel();

                        cbModel.addElement(value);

                        cbModel = (DefaultComboBoxModel<String>) selectLevelUserJComboBox.getModel();

                        cbModel.addElement(value);
                    }
                }
        );
                        
        selectRoleUserJComboBox1.setSelectedItem(currentUser.getUsername());
        selectLanguageUserJComboBox.setSelectedItem(currentUser.getUsername());
        selectOrganisationUserJComboBox.setSelectedItem(currentUser.getUsername());
        selectLevelUserJComboBox.setSelectedItem(currentUser.getUsername());
    }

    private void fillRolesInfo()
    {
        Map<String, SanchayRoleDTO> roleMap = null;

        roleMap = annotationManagementUpdateInfo.getAllRoles();

//        try {
//            roleMap = sanchaySpringRestClient.getAllRoles();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel = (DefaultListModel) rolesJList.getModel();

        listModel.removeAllElements();

        roleMap.entrySet().forEach(
                entry ->
                {
                    if(!entry.getValue().isToBeDeleted())
                    {
                        listModel.addElement(entry.getValue().getName());
                    }
                }
        );
    }

    private void fillAnnotationLevelInfo()
    {
        Map<String, SanchayAnnotationLevelDTO> annotationLevelMap = null;

        annotationLevelMap = annotationManagementUpdateInfo.getAllLevels();

//        try {
//            annotationLevelMap = sanchaySpringRestClient.getAllAnnotationLevels();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel = (DefaultListModel) annotationLevelsJList.getModel();

        listModel.removeAllElements();

        annotationLevelMap.entrySet().forEach(
                entry ->
                {
                    if(!entry.getValue().isToBeDeleted())
                    {
                        listModel.addElement(entry.getValue().getName());
                    }
                }
        );
    }

    private void fillUserRoleInfo(SanchayUserDTO selectedUser)
    {
        if(selectedUser == null)
            return;;

        Map<String, SanchayRoleSlimDTO> roleSlimMap = null;
//        Map<String, SanchayRoleDTO> roleMap = null;

        Map<String, SanchayRoleDTO> allRoleMap = annotationManagementUpdateInfo.getAllRoles();
        roleSlimMap = annotationManagementUpdateInfo.getAllUsers().get(selectedUser.getUsername()).getRoles();
//        roleMap = annotationManagementUpdateInfo.getAssignedUserRoles().get(selectedUser.getUsername());

//        try {
//            roleMap = sanchaySpringRestClient.getUserRoles(selectedUser.getUsername());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel1 = (DefaultListModel) rolesAssignedJList.getModel();
        DefaultListModel listModel2 = (DefaultListModel) rolesJList.getModel();

        listModel1.removeAllElements();
        listModel2.removeAllElements();

        if(currentRoleJComboBox.getItemCount() > 0) {
            currentRoleJComboBox.removeAllItems();
        }

        roleSlimMap.entrySet().forEach(
                entry -> 
                {
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        currentRoleJComboBox.addItem(entry.getValue().getName());
                        listModel1.addElement(entry.getValue().getName());
                    }
                }
        );

        allRoleMap.entrySet().forEach(
                entry -> 
                {
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        listModel2.addElement(entry.getValue().getName());
                    }
                }
        );

        if(selectedUser.getCurrentRole() != null)
        {
            currentRoleJComboBox.setSelectedItem(selectedUser.getCurrentRole());
        }
    }

    private void fillUserOrganisationInfo(SanchayUserDTO selectedUser)
    {
        if(selectedUser == null)
            return;;

        Map<String, SanchayOrganisationSlimDTO> sanchayOrganisationSlimMap = null;
//        Map<String, SanchayOrganisationDTO> sanchayOrganisationMap = null;

        Map<String, SanchayOrganisationDTO> allOrganisationMap = annotationManagementUpdateInfo.getAllOrganisations();
        sanchayOrganisationSlimMap = annotationManagementUpdateInfo.getAllUsers().get(selectedUser.getUsername()).getOrganisations();
//        sanchayOrganisationSlimMap = annotationManagementUpdateInfo.getAssignedUserOrganisations().get(selectedUser.getUsername());

//        try {
//            sanchayOrganisationMap = sanchaySpringRestClient.getUserOrganisations(selectedUser.getUsername());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel1 = (DefaultListModel) organisationsAssignedJList.getModel();
        DefaultListModel listModel2 = (DefaultListModel) organisationsJList.getModel();

        listModel1.removeAllElements();
        listModel2.removeAllElements();

        if(currentUserOrganisationJComboBox.getItemCount() > 0) {
            currentUserOrganisationJComboBox.removeAllItems();
        }

        sanchayOrganisationSlimMap.entrySet().forEach(
                entry -> 
                {  
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        String description = entry.getValue().getName() + " <=> " + entry.getValue().getLongName();
                        currentUserOrganisationJComboBox.addItem(entry.getValue().getName());
                        listModel1.addElement(description);
                    }
                }
        );

        allOrganisationMap.entrySet().forEach(
                entry -> 
                {  
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        String description = entry.getValue().getName() + " <=> " + entry.getValue().getLongName();
                        listModel2.addElement(description);
                    }
                }
        );

        if(selectedUser.getCurrentOrganisation()!= null)
        {
            currentUserOrganisationJComboBox.setSelectedItem(selectedUser.getCurrentOrganisationName());
        }
    }

    private void fillUserLangaugesInfo(SanchayUserDTO selectedUser)
    {
        if(selectedUser == null)
            return;;

        Map<String, SanchayResourceLanguageSlimDTO> sanchayLanguagesSlimMap = null;
//        Map<String, SanchayResourceLanguageDTO> sanchayLanguagesMap = null;

        Map<String, SanchayResourceLanguageDTO> allLanguagesMap = annotationManagementUpdateInfo.getAllLanguages();
        sanchayLanguagesSlimMap = annotationManagementUpdateInfo.getAllUsers().get(selectedUser.getUsername()).getLanguages();
//        sanchayLanguagesSlimMap = annotationManagementUpdateInfo.getAssignedUserLanguages().get(selectedUser.getUsername());

//        try {
//            sanchayLanguagesMap = sanchaySpringRestClient.getUserLanguages(selectedUser.getUsername());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel1 = (DefaultListModel) languagesAssignedJList.getModel();
        DefaultListModel listModel2 = (DefaultListModel) languagesJList.getModel();

        if(currentUserLanguageJComboBox.getItemCount() > 0) {
            currentUserLanguageJComboBox.removeAllItems();
        }

        listModel1.removeAllElements();
        listModel2.removeAllElements();

        sanchayLanguagesSlimMap.entrySet().forEach(
                entry -> 
                {
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        currentUserLanguageJComboBox.addItem(entry.getValue().getName());
                        listModel1.addElement(entry.getValue().getName());
                    }
                }
        );

        allLanguagesMap.entrySet().forEach(
                entry -> 
                {
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        listModel2.addElement(entry.getValue().getName());
                    }
                }
        );


        if(selectedUser.getCurrentLanguage() != null)
        {
            currentUserLanguageJComboBox.setSelectedItem(selectedUser.getCurrentLanguageName());
        }
    }
    private void fillAnnotationLevelInfo(SanchayUserDTO selectedUser)
    {
        if(selectedUser == null)
            return;;

        Map<String, SanchayAnnotationLevelSlimDTO> annotationLevelSlimMap = null;
//        Map<String, SanchayAnnotationLevelDTO> annotationLevelMap = null;

        Map<String, SanchayAnnotationLevelDTO> allLevelsMap = annotationManagementUpdateInfo.getAllLevels();
        annotationLevelSlimMap = annotationManagementUpdateInfo.getAllUsers().get(selectedUser.getUsername()).getAnnotationLevels();
//        annotationLevelSlimMap = annotationManagementUpdateInfo.getAssignedUserLevels().get(selectedUser.getUsername());

//        try {
//            annotationLevelMap = sanchaySpringRestClient.getUserAnnotationLevels(selectedUser.getUsername());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        DefaultListModel listModel1 = (DefaultListModel) annotationLevelsJAssignedJList.getModel();
        DefaultListModel listModel2 = (DefaultListModel) annotationLevelsJList.getModel();

        listModel1.removeAllElements();
        listModel2.removeAllElements();

        if(currentAnnotationLevelJComboBox.getItemCount() > 0) {
            currentAnnotationLevelJComboBox.removeAllItems();
        }

        annotationLevelSlimMap.entrySet().forEach(
                entry -> 
                {
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        currentAnnotationLevelJComboBox.addItem(entry.getValue().getName());
                        listModel1.addElement(entry.getValue().getName());
                    }
                }
        );

        allLevelsMap.entrySet().forEach(
                entry -> 
                {
                    if(entry.getValue() != null && !entry.getValue().isToBeDeleted())
                    {
                        listModel2.addElement(entry.getValue().getName());
                    }
                }
        );

        if(selectedUser.getCurrentAnnotationLevel() != null)
        {
            currentAnnotationLevelJComboBox.setSelectedItem(selectedUser.getCurrentAnnotationLevelName());
        }
    }
    
    private void hideAnnotationManagementTabs()
    {
        rightJTabbedPane.remove(organisationsJPanel);
        rightJTabbedPane.remove(languagesJPanel);
        rightJTabbedPane.remove(usersJPanel);
        rightJTabbedPane.remove(rolesJPanel);
        rightJTabbedPane.remove(userOrganisationJPanel);
        rightJTabbedPane.remove(userLanguagesJPanel);
        rightJTabbedPane.remove(annotationLevelsJPanel);        
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
    
    // Shift to a separate class later
    public void windowOpened(WindowEvent e) {
    }
    
    public void windowClosing(WindowEvent e) {
//        saveState(this);
//        closeFile(e);
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

    public void setParentComponent(Component parentComponent)
    {
        this.parentComponent = parentComponent;
    }
    
    public void setDialog(JDialog d) {
        dialog = d;
    }

    public void displayFile(String path, String charset, EventObject e) {
        if(path == null || path.equals(""))
            return;
        
        TextEditorJPanel textEditorJPanel = ((TextEditorJPanel) displayFileTextJPanel);
        
        textEditorJPanel.displayFile(new File(path), charset, e);        
//        displayFileInBackground(new File(path), charset, e);
    }
    
    public void displayFile(File file, String charset, EventObject e) {
       
        displayFile(file, charset, e);
        
//        if(file.isFile() == false || file.exists() == false)
//            return;
//
//        Cursor cursor = null;
//        
//        if(owner != null)
//        {
////            cursor = getParent().getCursor();
//            cursor = owner.getCursor();
////            owner.setCursor(Cursor.WAIT_CURSOR);
//            owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        }        
//        
//        try{
//            //	textJTextArea.setText("");
//            
//            initDocument();
//            
//            textFile = file.getAbsolutePath();
//            this.charset = charset;
//            
//            BufferedReader lnReader = null;
//            
//            try {
//                if(!charset.equals(""))
//                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), charset));
//                else
//                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), GlobalProperties.getIntlString("UTF-8")));
//                
//                sanchayBackup = new SanchayBackup();
//                FileMonitor.getInstance().addFileChangeListener(sanchayBackup, textFile, backupPeriod);
////                logJTextArea.setText("File " + textFile + " backed up.");
//                
//                String line = "";
//                
//                Element root = doc.getDefaultRootElement();
//                while((line = lnReader.readLine()) != null) {
//                    doc.insertString(root.getEndOffset() - 1, line + "\n", null);
//                    //textJTextArea.append(line + "\n");
//                }
//                
////		if(textJTextArea.getText().length() > 0)
//                if(doc.getLength() > 0)
//                    textJTextArea.setCaretPosition(0);
//            } catch (UnsupportedEncodingException ex) {
//                ex.printStackTrace();
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//            
//            dirty = false;
//            
//            undo.discardAllEdits();
//            undoAction.updateUndoState();
//            redoAction.updateRedoState();
//            
//            setTitle(textFile);
//        } catch(BadLocationException ex) {
//            ex.printStackTrace();
//        }
//
//        if(owner != null && cursor != null)
//        {
//            owner.setCursor(cursor);
//        }        
//        
//        saveState(this);
    }

    public void displayFile(EventObject e)
    {
        if(e instanceof DisplayEvent)
        {
            DisplayEvent de = (DisplayEvent) e;
            displayFile(de.getFilePath(), de.getCharset(), e);
        }
    }
    
    public boolean closeFile(EventObject e) {
//        if(dirty) {
//            int retVal = -1;
            
//            if(dialog != null)
//                retVal = JOptionPane.showConfirmDialog(dialog, "The file " + textFile + " has been modified.\n\nDo you want to save the file?", "Closing File", JOptionPane.YES_NO_OPTION);
//            else
//                retVal = JOptionPane.showConfirmDialog(parentComponent, "The file " + textFile + " has been modified.\n\nDo you want to save the file?", GlobalProperties.getIntlString("Closing_File"), JOptionPane.YES_NO_OPTION);
//            
//            if(retVal == JOptionPane.NO_OPTION) {
//                initDocument();
//                return false;
//            } else {
//                save(e);
//                initDocument();
//                return true;
//            }
//        } else
//            initDocument();
//        
//        if(sanchayBackup != null)
//            FileMonitor.getInstance().removeFileChangeListener(sanchayBackup, textFile);
        
        return true;
    }

    public ClientType getClientType()
    {
        return clientType;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String t) {
    }

    protected void createFileTree()
    {
        remoteFileExplorerJPanel = new RemoteFileExplorerJPanel(this, rootRemoteFileNode, connectionMode);

//        leftJTabbedPane.addTab(GlobalProperties.getIntlString("Folders"), remoteFileExplorerJPanel);
        foldersJPanel.add(remoteFileExplorerJPanel, BorderLayout.CENTER);
        foldersJPanel.setVisible(false);
        foldersJPanel.setVisible(false);
//    	mainJSplitPane.setLeftComponent(leftJTabbedPane);

//        mainJSplitPane.setLeftComponent(fileExplorerJPanel);
        remoteFileExplorerJPanel.createTree(".");        
    }
    
//    public static boolean connectRemote()
//    {
//        connectRemote()
//    }
    
    public boolean connectRemoteRMI()
    {
        server = serverJTextField.getText();
                
        try {        
            registry = LocateRegistry.getRegistry(server);
//            rmiFileSystem = (RMIFileSystemRI)registry.lookup("sanchayRMIProtocol");
            sanchayServerLauncher = (SanchayServerLauncherRI)registry.lookup(LAUNCHER_LOGIN);
            authenticationEntry = (AuthenticationEntryRI)registry.lookup(AUTH_LOGIN);

            sanchayLauncherSession = (SanchayLauncherSessionRI)registry.lookup(LAUNCHER_SESSION);
            authenticationSession = (AuthenticationSessionRI)registry.lookup(AUTH_SESSION);
        } catch (RemoteException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(registry != null)
        {        
            connected = true;

            SanchayMain.setConnected(connected);
            
            loggedIn = loginRemoteRMI();

            if(!loggedIn)
            {
                JOptionPane.showMessageDialog(this, "Unable to connect to the_server.\nPlease check connection." + getServer(), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }       
        
            initDownloadRMI();
            
            return connected;
        }
        
        return false;
    }

    public boolean loginRemoteRMI()
    {
        server = serverJTextField.getText();
        userName = userJTextField.getText();
        password= String.valueOf(passwordJTextField.getPassword());
        
        try {
            
            launcherSessionId = sanchayServerLauncher.login(userName, password.toCharArray());
            authenticationSessionId = authenticationEntry.login(userName, password.toCharArray());
            
            sanchayMainServer = sanchayLauncherSession.getSanchayMainServerInstance(launcherSessionId);
//            authenticationSever = authenticationSession.getAuthenticationSeverInstance(authenticationSessionId);
            rmiFileSystem = sanchayMainServer.getRMIFileSystem();
            userManagerRI = sanchayMainServer.getUserManager();
            resourceManagerRI = sanchayMainServer.getResourceManager();

//            sessionId = (UUID) authenticationSever.authenticateUser(userName, password);
            sessionId = authenticationEntry.authenticateUser(userName, password, authenticationSessionId);
            
            if(sessionId != null)
            {
                loggedIn = true;
            }
            
        } catch (AuthorizationException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SessionException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(!loggedIn)
        {
            JOptionPane.showMessageDialog(this, "Please check user name or password.", GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);            
        }
     
        SanchayMain.setLoggedIn(loggedIn);
        
        return loggedIn;
    }
    
    public boolean connectSpringWeb() throws JsonProcessingException {
        server = serverJTextField.getText();
        
        loggedIn = loginSpringWeb();

        if(!loggedIn)
        {
            JOptionPane.showMessageDialog(this, "Unable to connect to the_server.\nPlease check connection." + getServer(), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }       
        
        initDownloadSpringWeb();

        showAnnotationManagementTabs();

        connected = loggedIn;
        
        return connected;
    }

    public boolean loginSpringWeb() throws JsonProcessingException {
        server = serverJTextField.getText();
        userName = userJTextField.getText();
        password= String.valueOf(passwordJTextField.getPassword());

        sanchaySpringRestClient.authenticateUser(userName, password);
        currentUser = sanchaySpringRestClient.getCurrentUser();
        
        annotationManagementUpdateInfo = sanchaySpringRestClient.getAnnotationManagementUpdateInfo();
            
        if(currentUser != null)
        {
            loggedIn = true;
        }
                    
        if(!loggedIn)
        {
            JOptionPane.showMessageDialog(this, "Please check user name or password.", GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);            
        }
     
        SanchayMain.setLoggedIn(loggedIn);
        
        return loggedIn;
    }
    
    public boolean connectRemoteSftp(){
        
        boolean connectionSuccessful = false;

        server = serverJTextField.getText();
        userName = userJTextField.getText();
        password= String.valueOf(passwordJTextField.getPassword());
        
        String command1="ls -ltr";

        try{

            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            
            jsch = new JSch();
            session =jsch.getSession(userName, server, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

            System.out.println("Connected");

//            Channel channel=session.openChannel("exec");
//            ((ChannelExec)channel).setCommand(command1);
//            channel.setInputStream(null);
//            ((ChannelExec)channel).setErrStream(System.err);
//
//            InputStream in=channel.getInputStream();
//            channel.connect();
//            byte[] tmp=new byte[1024];
//            while(true){
//              while(in.available()>0){
//                int i=in.read(tmp, 0, 1024);
//                if(i<0)break;
//                System.out.print(new String(tmp, 0, i));
//              }
//              if(channel.isClosed()){
//                System.out.println("exit-status: "+channel.getExitStatus());
//                break;
//              }
//              try{Thread.sleep(1000);}catch(Exception ee){}
//            }
//            channel.disconnect();
//            System.out.println("Shell Example: DONE");

            Channel sftp = session.openChannel("sftp");

            int CHANNEL_TIMEOUT = 5000;
            // 5 seconds timeout
            sftp.connect(CHANNEL_TIMEOUT);
            
            connectionSuccessful = true;

            SanchayMain.setConnected(connectionSuccessful);
            SanchayMain.setLoggedIn(connectionSuccessful);

            if(!connectionSuccessful)
            {
                JOptionPane.showMessageDialog(this, GlobalProperties.getIntlString("Unable_to_connect_to_the_server.\nPlease check connection.") + getServer(), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }                    

            channelSftp = (ChannelSftp) sftp;            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        initDownloadSftp();
        
        connected = connectionSuccessful;
        loggedIn = connectionSuccessful;
        
        return connectionSuccessful;
    }
    
    public void initDownloadSftp(){
        
//        rootRemoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, null, null, RemoteFileNode.SFTP_MODE);
        rootRemoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, null, RemoteFileNode.SFTP_MODE);

        try{
            File homeDir = FileUtils.getUserDirectory();
            File workingDir = new File(homeDir, "annotation");

            if(!workingDir.exists())
            {
                try
                {
                    workingDir.mkdir();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            JSchSSHUtils.listDirectory(channelSftp, ".", rootRemoteFileNode);
//                JSchSSHUtils.downloadDir(workingDir.getPath(), workingDir.getPath(), channelSftp);
            RemoteFileNode annotationDirNode = JSchSSHUtils.getAnnotationDirNodeSftp(rootRemoteFileNode);
            JSchSSHUtils.cloneDirectory(annotationDirNode, channelSftp, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            
        createFileTree();
    }
    
    public void initDownloadRMI(){
        
//        rootRemoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, null, rmiFileSystem, RemoteFileNode.RMI_MODE);
        rootRemoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, null, RemoteFileNode.RMI_MODE);

        try{
            File homeDir = FileUtils.getUserDirectory();
            File workingDir = new File(homeDir, "annotation");

            if(!workingDir.exists())
            {
                try
                {
                    workingDir.mkdir();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            RMIUtils.listDirectories("", rootRemoteFileNode, rmiFileSystem);
            RemoteFileNode annotationDirNode = RMIUtils.getAnnotationDirNodeRMI(rootRemoteFileNode);
            RMIUtils.cloneDirectory(annotationDirNode, rmiFileSystem, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            
        createFileTree();
    }
    
    public void initDownloadSpringWeb(){
        
//        rootRemoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, null, rmiFileSystem, RemoteFileNode.RMI_MODE);
        rootRemoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, null, RemoteFileNode.RMI_MODE);

        try{
            File homeDir = FileUtils.getUserDirectory();
            File workingDir = new File(homeDir, "annotation");

            if(!workingDir.exists())
            {
                try
                {
                    workingDir.mkdir();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            sanchaySpringRestClient.listDirectories("", rootRemoteFileNode);
            RemoteFileNode annotationDirNode = RMIUtils.getAnnotationDirNodeRMI(rootRemoteFileNode);
            sanchaySpringRestClient.cloneDirectory(annotationDirNode, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            
        createFileTree();
    }
    
    public void remoteDisconnect()
    {
        if(session != null)
            session.disconnect();
    
        jsch = null;
        session = null;
        channelSftp = null;
        
        if(registry != null)
            try {
                registry.unbind("sanchayRMIProtocol");
        } catch (RemoteException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(SanchayRemoteWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        registry = null;
        rmiFileSystem = null;
    }
    
    public boolean downloadRemoteFile()
    {
        rootRemoteFileNode = remoteFileExplorerJPanel.getRootRemoteFileNode();

        boolean cloneSuccess = false;
        
        if(connectionMode == RemoteFileNode.RMI_MODE)
            cloneSuccess = RMIUtils.cloneFile(rootRemoteFileNode.getRemoteRMIFile(), rmiFileSystem, true);
        else if(connectionMode == RemoteFileNode.SPRING_MODE)
            cloneSuccess = sanchaySpringRestClient.cloneFile(rootRemoteFileNode.getRemoteRMIFile(), true);
        else if(connectionMode == RemoteFileNode.SFTP_MODE)
            cloneSuccess = JSchSSHUtils.cloneFile(rootRemoteFileNode.getRemoteSftpFile(), channelSftp, true);
        
        if(!cloneSuccess)
        {
            JOptionPane.showMessageDialog(this, "Please select a file by double clicking to open.", GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);            
            
            return false;
        }
        
        return true;
    }
    
    private void showWorkDialog()
    {
        taskKVP = SyntacticAnnotationTaskSetupJPanel.getDefaultTaskKVP();

        taskKVP.addProperty("TaskName", taskName);
        taskKVP.addProperty("SSFCorpusStoryFile", taskPath);
        taskKVP.addProperty("TaskPropFile", taskPath);            
        
        if(workJPanel != null)
        {
            workJPanel.init();
            workJPanel.setTaskProps(taskKVP);
            workJPanel.setOwner(this.getOwner());
            
            workJPanel.setTaskName(taskName);
            workJPanel.configure();
            
            dialog.setVisible(false);
            
            return;
        }
        
	workDialog = null;
	
	if(dialog != null)
	    workDialog = new JDialog(dialog, taskName, true);
	else
	    workDialog = new JDialog(owner, taskName, true);
        
//	SyntacticAnnotationWorkJPanel workJPanelLocal = new SyntacticAnnotationWorkJPanel(taskKVP);
	workJPanel = new SyntacticAnnotationWorkJPanel(taskKVP);

        workJPanel.setOwner(this.getOwner());
        workJPanel.setDialog(workDialog);
        workJPanel.setTaskName(taskName);
        workJPanel.configure();
        
        workDialog.add(workJPanel);
	
        int inset = 5;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        workDialog.setBounds(inset, inset,
		screenSize.width  - inset*2,
		screenSize.height - inset*9);

	workDialog.setVisible(true);
	
//	((AnnotationClient) owner).setWorkJPanel(workJPanel);
    }
    
    public Frame getOwner()
    {
        return owner;
    }
    
    public void setOwner(Frame f)
    {
        owner = (JFrame) f;
    }
    
    public boolean isLoggedIn()
    {
        return loggedIn;
    }
     
    public String getServer()
    {
        return server;
    }
     
    public void setServer(String s)
    {
        server = s;
    }
     
    public String getUserName()
    {
        return userName;
    }
     
    public void setUserName(String un)
    {
        userName = un;
    }
    
    public void setWorkJPanel(SyntacticAnnotationWorkJPanel wjPanel)
    {
        workJPanel = wjPanel;
    }
    
    public void uploadFile(String localPath, ActionEvent evt)
    {
        try {
            if(connectionMode == RemoteFileNode.RMI_MODE)
            {
                String absPathClient = rootRemoteFileNode.getRemoteRMIFile().getAbsolutePathOnClient();
                String absPathServer = rootRemoteFileNode.getRemoteRMIFile().getAbsolutePathOnServer();
                RMIUtils.uploadFile(absPathClient, absPathServer, rmiFileSystem);
            }
            else if(connectionMode == RemoteFileNode.SFTP_MODE)
            {
                RemoteSftpFile  remoteFile = rootRemoteFileNode.getRemoteSftpFile();
                JSchSSHUtils.uploadFile(remoteFile, channelSftp, true);
            }
            else if(connectionMode == RemoteFileNode.SPRING_MODE)
            {
                RemoteFile  remoteFile = rootRemoteFileNode.getRemoteRMIFile();
                sanchaySpringRestClient.uploadFile(currentUser.getUsername(), remoteFile, true);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }        
    }

    private static void createAndShowGUI() {
	
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame(GlobalProperties.getIntlString("Sanchay_Syntactic_Annotation"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	SanchayRemoteWorkJPanel newContentPane = new SanchayRemoteWorkJPanel();
        
        newContentPane.setOwner(frame);
	
//	newContentPane.owner = frame;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
//        frame.pack();
	
        int xinset = 280;
        int yinset = 140;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(xinset, yinset,
		screenSize.width  - xinset*2,
		screenSize.height - yinset*2);

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
    private javax.swing.JButton addAnnotationLevelsJButton;
    private javax.swing.JButton addLanguageJButton;
    private javax.swing.JPanel addLangugageJPanel;
    private javax.swing.JButton addOrganisationJButton;
    private javax.swing.JPanel addOrganisationJPanel;
    private javax.swing.JButton addRoleJButton;
    private javax.swing.JPanel addUserJPanel;
    private javax.swing.JScrollPane annotationLevelsAssignedJScrollPane;
    private javax.swing.JList<String> annotationLevelsJAssignedJList;
    private javax.swing.JPanel annotationLevelsJButtonsPanel;
    private javax.swing.JList<String> annotationLevelsJList;
    private javax.swing.JPanel annotationLevelsJPanel;
    private javax.swing.JScrollPane annotationLevelsScrollPane;
    private javax.swing.JCheckBox annotationManagementJCheckBox;
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton connectJButton;
    private javax.swing.JPanel connectJPanel;
    private javax.swing.JComboBox<String> connectionModeJComboBox;
    private javax.swing.JButton createLanguageJButton;
    private javax.swing.JButton createOrganisationJButton;
    private javax.swing.JButton createUserJButton;
    private javax.swing.JComboBox<String> currentAnnotationLevelJComboBox;
    private javax.swing.JLabel currentAnnotationLevelJLabel;
    private javax.swing.JPanel currentAnnotationLevelJPanel;
    private javax.swing.JComboBox<String> currentRoleJComboBox;
    private javax.swing.JLabel currentRoleJLabel;
    private javax.swing.JPanel currentRoleJPanel;
    private javax.swing.JPanel currentUserLangaugeJPanel;
    private javax.swing.JComboBox<String> currentUserLanguageJComboBox;
    private javax.swing.JLabel currentUserLanguageJLabel;
    private javax.swing.JComboBox<String> currentUserOrganisationJComboBox;
    private javax.swing.JLabel currentUserOrganisationJLabel;
    private javax.swing.JPanel currentUserOrganisationJPanel;
    private javax.swing.JButton deleteLanguageJButton;
    private javax.swing.JButton deleteOrganisationJButton;
    private javax.swing.JButton deleteUserJButton;
    private javax.swing.JPanel emailJPanel;
    private javax.swing.JTextField emailJTextField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JPanel fileDisplayJPanel;
    private javax.swing.JPanel firstNameJPanel;
    private javax.swing.JTextField firstNameJTextField;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JPanel foldersJPanel;
    private javax.swing.JPanel languageButtonJPanel;
    private javax.swing.JPanel languageButtonLayoutJPanel;
    private javax.swing.JPanel languageButtonLayoutJPanel1;
    private javax.swing.JPanel languageDetailsJPanel;
    private javax.swing.JScrollPane languageListJScrollPane;
    private javax.swing.JPanel languageNameJPanel;
    private javax.swing.JTextField languageNameJTextField;
    private javax.swing.JLabel languageNameLabel;
    private javax.swing.JList<String> languagesAssignedJList;
    private javax.swing.JPanel languagesAssignedJPanel;
    private javax.swing.JScrollPane languagesAssignedScrollPane;
    private javax.swing.JPanel languagesButtonsPanel;
    private javax.swing.JList<String> languagesJList;
    private javax.swing.JPanel languagesJPanel;
    private javax.swing.JList<String> languagesListJList;
    private javax.swing.JScrollPane languagesScrollPane;
    private javax.swing.JPanel lastNameJPanel;
    private javax.swing.JTextField lastNameJTextField;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JTabbedPane leftJTabbedPane;
    private javax.swing.JSplitPane mainJSplitPane;
    private javax.swing.ButtonGroup modeJButtonGroup;
    private javax.swing.JButton openRemoteJButton;
    private javax.swing.JPanel optionsJPanel;
    private javax.swing.JPanel organisationButtonJPanel;
    private javax.swing.JPanel organisationButtonLayoutJPanel;
    private javax.swing.JPanel organisationDetailsJPanel;
    private javax.swing.JScrollPane organisationListJScrollPane;
    private javax.swing.JTextField organisationLongNameJTextField;
    private javax.swing.JLabel organisationLongNameLabel;
    private javax.swing.JTextField organisationShortNameJTextField;
    private javax.swing.JLabel organisationShortNameLabel;
    private javax.swing.JList<String> organisationsAssignedJList;
    private javax.swing.JScrollPane organisationsAssignedScrollPane;
    private javax.swing.JList<String> organisationsJList;
    private javax.swing.JPanel organisationsJPanel;
    private javax.swing.JList<String> organisationsListJList;
    private javax.swing.JPanel organisationsLongNameJPanel;
    private javax.swing.JScrollPane organisationsScrollPane;
    private javax.swing.JPanel organisationsShortNameJPanel;
    private javax.swing.JLabel passwordJLabel;
    private javax.swing.JPanel passwordJPanel;
    private javax.swing.JPanel passwordJPanel1;
    private javax.swing.JPasswordField passwordJTextField;
    private javax.swing.JPasswordField passwordJTextField1;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JButton removeAnnotationLevelsJButton;
    private javax.swing.JButton removeLanguageJButton;
    private javax.swing.JButton removeOrganisationJButton;
    private javax.swing.JButton removeRoleJButton;
    private javax.swing.JTabbedPane rightJTabbedPane;
    private javax.swing.JList<String> rolesAssignedJList;
    private javax.swing.JScrollPane rolesAssignedScrollPane;
    private javax.swing.JPanel rolesButtonsPanel;
    private javax.swing.JList<String> rolesJList;
    private javax.swing.JPanel rolesJPanel;
    private javax.swing.JPanel rolesPanel;
    private javax.swing.JScrollPane rolesScrollPane;
    private javax.swing.JButton saveAnnotationManagementInfoJButton;
    private javax.swing.JComboBox<String> selectLanguageUserJComboBox;
    private javax.swing.JLabel selectLanguageUserJLabel;
    private javax.swing.JComboBox<String> selectLevelUserJComboBox;
    private javax.swing.JLabel selectLevelUserJLabel;
    private javax.swing.JComboBox<String> selectOrganisationUserJComboBox;
    private javax.swing.JLabel selectOrganisationUserJLabel;
    private javax.swing.JComboBox<String> selectRoleUserJComboBox1;
    private javax.swing.JLabel selectRoleUserJLabel;
    private javax.swing.JLabel serverJLabel;
    private javax.swing.JPanel serverJPanel;
    private javax.swing.JTextField serverJTextField;
    private javax.swing.JLabel titleJLabel;
    private javax.swing.JPanel titleJPanel;
    private javax.swing.JPanel topJPanel;
    private javax.swing.JButton updateLanguageJButton;
    private javax.swing.JButton updateOrganisationJButton;
    private javax.swing.JButton updateUserJButton;
    private javax.swing.JPanel userAddButtonsJPanel;
    private javax.swing.JPanel userAnnotationLevelsJPanel;
    private javax.swing.JPanel userButtonJPanel;
    private javax.swing.JPanel userDetailsJPanel;
    private javax.swing.JCheckBox userEnableJCheckBox1;
    private javax.swing.JLabel userJLabel;
    private javax.swing.JPanel userJPanel;
    private javax.swing.JTextField userJTextField;
    private javax.swing.JPanel userLanguagesJPanel;
    private javax.swing.JScrollPane userListJScrollPane;
    private javax.swing.JPanel userOrganisationJPanel;
    private javax.swing.JPanel userOrganisationsButtonsPanel;
    private javax.swing.JPanel userOrganisationsPanel;
    private javax.swing.JPanel usernameJPanel;
    private javax.swing.JTextField usernameJTextField;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JPanel usersJPanel;
    private javax.swing.JList<String> usersListJList;
    // End of variables declaration//GEN-END:variables
}
