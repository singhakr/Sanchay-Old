/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package sanchay.gui.clients;

/**
 *
 * @author User
 */
public class UserDetailsJPanel extends javax.swing.JPanel {

    /**
     * Creates new form UserDetailsJPanel
     */
    public UserDetailsJPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainJPanel = new javax.swing.JPanel();
        usersJPanel = new javax.swing.JPanel();
        selectUserJPanel = new javax.swing.JPanel();
        selectUserJLabel = new javax.swing.JLabel();
        selectUserJComboBox = new javax.swing.JComboBox<>();
        resourcesJPanel = new javax.swing.JPanel();
        selectResourceJPanel = new javax.swing.JPanel();
        selectResourceJLabel = new javax.swing.JLabel();
        selectResourceJComboBox = new javax.swing.JComboBox<>();
        organisationsJPanel = new javax.swing.JPanel();
        selectOrganisationJPanel = new javax.swing.JPanel();
        selectOrganisationJLabel = new javax.swing.JLabel();
        selectOrganisationJComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        mainJPanel.setLayout(new java.awt.GridLayout(1, 3));

        usersJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Users"));
        usersJPanel.setLayout(new java.awt.BorderLayout());

        selectUserJLabel.setText("User: ");
        selectUserJPanel.add(selectUserJLabel);

        selectUserJComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
        selectUserJPanel.add(selectUserJComboBox);

        usersJPanel.add(selectUserJPanel, java.awt.BorderLayout.NORTH);

        mainJPanel.add(usersJPanel);

        resourcesJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Resources"));
        resourcesJPanel.setLayout(new java.awt.BorderLayout());

        selectResourceJLabel.setText("Resource: ");
        selectResourceJPanel.add(selectResourceJLabel);

        selectResourceJComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
        selectResourceJPanel.add(selectResourceJComboBox);

        resourcesJPanel.add(selectResourceJPanel, java.awt.BorderLayout.NORTH);

        mainJPanel.add(resourcesJPanel);

        organisationsJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Organisation"));
        organisationsJPanel.setLayout(new java.awt.BorderLayout());

        selectOrganisationJLabel.setText("Organisation: ");
        selectOrganisationJPanel.add(selectOrganisationJLabel);

        selectOrganisationJComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
        selectOrganisationJPanel.add(selectOrganisationJComboBox);

        organisationsJPanel.add(selectOrganisationJPanel, java.awt.BorderLayout.NORTH);

        mainJPanel.add(organisationsJPanel);

        add(mainJPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainJPanel;
    private javax.swing.JPanel organisationsJPanel;
    private javax.swing.JPanel resourcesJPanel;
    private javax.swing.JComboBox<String> selectOrganisationJComboBox;
    private javax.swing.JLabel selectOrganisationJLabel;
    private javax.swing.JPanel selectOrganisationJPanel;
    private javax.swing.JComboBox<String> selectResourceJComboBox;
    private javax.swing.JLabel selectResourceJLabel;
    private javax.swing.JPanel selectResourceJPanel;
    private javax.swing.JComboBox<String> selectUserJComboBox;
    private javax.swing.JLabel selectUserJLabel;
    private javax.swing.JPanel selectUserJPanel;
    private javax.swing.JPanel usersJPanel;
    // End of variables declaration//GEN-END:variables
}
