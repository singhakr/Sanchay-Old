/*
 * CorpusSetupJPanel.java
 *
 * Created on October 27, 2005, 4:44 PM
 */

package sanchay.corpus.manager.gui;

/**
 *
 * @author  anil
 */
public class CorpusSetupJPanel extends javax.swing.JPanel {
    
    /** Creates new form CorpusSetupJPanel */
    public CorpusSetupJPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        taskJPanel = new javax.swing.JPanel();
        taskNameJLabel = new javax.swing.JLabel();
        taskNameJTextField = new javax.swing.JTextField();
        propJPanel = new javax.swing.JPanel();
        propJLabel = new javax.swing.JLabel();
        propJTextField = new javax.swing.JTextField();
        propertiesJButton = new javax.swing.JButton();
        srcCorpusJPanel = new javax.swing.JPanel();
        srcCorpusJLabel = new javax.swing.JLabel();
        srcCorpusJTextField = new javax.swing.JTextField();
        srcCorpusJButton = new javax.swing.JButton();
        tgtCorpusJPanel = new javax.swing.JPanel();
        tgtCorpusJLabel = new javax.swing.JLabel();
        tgtCorpusJTextField = new javax.swing.JTextField();
        tgtCorpusJButton = new javax.swing.JButton();
        tgtCorpusUTF8JPanel = new javax.swing.JPanel();
        tgtCorpusUTF8JLabel = new javax.swing.JLabel();
        tgtCorpusUTF8JTextField = new javax.swing.JTextField();
        tgtCorpusUTF8JButton = new javax.swing.JButton();
        srcTMListJPanel = new javax.swing.JPanel();
        srcTMListJLabel = new javax.swing.JLabel();
        srcTMListJTextField = new javax.swing.JTextField();
        srcTMListJButton = new javax.swing.JButton();
        tgtTMListJPanel = new javax.swing.JPanel();
        tgtTMListJLabel = new javax.swing.JLabel();
        tgtTMListJTextField = new javax.swing.JTextField();
        tgtTMListJButton = new javax.swing.JButton();
        markerDictJPanel = new javax.swing.JPanel();
        markerDictJLabel = new javax.swing.JLabel();
        markerDictJTextField = new javax.swing.JTextField();
        markerDictJButton = new javax.swing.JButton();
        commandsJPanel = new javax.swing.JPanel();
        OKJButton = new javax.swing.JButton();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        taskJPanel.setLayout(new java.awt.BorderLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        taskNameJLabel.setText(bundle.getString("Task_name:")); // NOI18N
        taskJPanel.add(taskNameJLabel, java.awt.BorderLayout.NORTH);

        taskNameJTextField.setText(bundle.getString("Task_name")); // NOI18N
        taskJPanel.add(taskNameJTextField, java.awt.BorderLayout.CENTER);

        add(taskJPanel);

        propJPanel.setLayout(new java.awt.BorderLayout());

        propJLabel.setText(bundle.getString("Task_properties_file:")); // NOI18N
        propJPanel.add(propJLabel, java.awt.BorderLayout.NORTH);

        propJTextField.setText(bundle.getString("Task_properties_file")); // NOI18N
        propJPanel.add(propJTextField, java.awt.BorderLayout.CENTER);

        propertiesJButton.setText(bundle.getString("Browse")); // NOI18N
        propertiesJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertiesJButtonActionPerformed(evt);
            }
        });
        propJPanel.add(propertiesJButton, java.awt.BorderLayout.EAST);

        add(propJPanel);

        srcCorpusJPanel.setLayout(new java.awt.BorderLayout());

        srcCorpusJLabel.setText(bundle.getString("Source_corpus_file:")); // NOI18N
        srcCorpusJPanel.add(srcCorpusJLabel, java.awt.BorderLayout.NORTH);

        srcCorpusJTextField.setText(bundle.getString("Source_corpus_file")); // NOI18N
        srcCorpusJPanel.add(srcCorpusJTextField, java.awt.BorderLayout.CENTER);

        srcCorpusJButton.setText(bundle.getString("Browse")); // NOI18N
        srcCorpusJPanel.add(srcCorpusJButton, java.awt.BorderLayout.EAST);

        add(srcCorpusJPanel);

        tgtCorpusJPanel.setLayout(new java.awt.BorderLayout());

        tgtCorpusJLabel.setText(bundle.getString("Target_corpus_file:")); // NOI18N
        tgtCorpusJPanel.add(tgtCorpusJLabel, java.awt.BorderLayout.NORTH);

        tgtCorpusJTextField.setText(bundle.getString("Target_corpus_file")); // NOI18N
        tgtCorpusJPanel.add(tgtCorpusJTextField, java.awt.BorderLayout.CENTER);

        tgtCorpusJButton.setText(bundle.getString("Browse")); // NOI18N
        tgtCorpusJPanel.add(tgtCorpusJButton, java.awt.BorderLayout.EAST);

        add(tgtCorpusJPanel);

        tgtCorpusUTF8JPanel.setLayout(new java.awt.BorderLayout());

        tgtCorpusUTF8JLabel.setText(bundle.getString("Target_corpus_file_(UTF8):")); // NOI18N
        tgtCorpusUTF8JPanel.add(tgtCorpusUTF8JLabel, java.awt.BorderLayout.NORTH);

        tgtCorpusUTF8JTextField.setText(bundle.getString("Target_corpus_file_(UTF8)")); // NOI18N
        tgtCorpusUTF8JPanel.add(tgtCorpusUTF8JTextField, java.awt.BorderLayout.CENTER);

        tgtCorpusUTF8JButton.setText(bundle.getString("Browse")); // NOI18N
        tgtCorpusUTF8JPanel.add(tgtCorpusUTF8JButton, java.awt.BorderLayout.EAST);

        add(tgtCorpusUTF8JPanel);

        srcTMListJPanel.setLayout(new java.awt.BorderLayout());

        srcTMListJLabel.setText(bundle.getString("Source_language_marker_file:")); // NOI18N
        srcTMListJPanel.add(srcTMListJLabel, java.awt.BorderLayout.NORTH);

        srcTMListJTextField.setText(bundle.getString("Source_language_marker_file")); // NOI18N
        srcTMListJPanel.add(srcTMListJTextField, java.awt.BorderLayout.CENTER);

        srcTMListJButton.setText(bundle.getString("Browse")); // NOI18N
        srcTMListJPanel.add(srcTMListJButton, java.awt.BorderLayout.EAST);

        add(srcTMListJPanel);

        tgtTMListJPanel.setLayout(new java.awt.BorderLayout());

        tgtTMListJLabel.setText(bundle.getString("Target_language_marker_file:")); // NOI18N
        tgtTMListJPanel.add(tgtTMListJLabel, java.awt.BorderLayout.NORTH);

        tgtTMListJTextField.setText(bundle.getString("Target_language_marker_file")); // NOI18N
        tgtTMListJPanel.add(tgtTMListJTextField, java.awt.BorderLayout.CENTER);

        tgtTMListJButton.setText(bundle.getString("Browse")); // NOI18N
        tgtTMListJPanel.add(tgtTMListJButton, java.awt.BorderLayout.EAST);

        add(tgtTMListJPanel);

        markerDictJPanel.setLayout(new java.awt.BorderLayout());

        markerDictJLabel.setText(bundle.getString("Marker_dictionary_file:")); // NOI18N
        markerDictJPanel.add(markerDictJLabel, java.awt.BorderLayout.NORTH);

        markerDictJTextField.setText(bundle.getString("Marker_dictionary_file")); // NOI18N
        markerDictJPanel.add(markerDictJTextField, java.awt.BorderLayout.CENTER);

        markerDictJButton.setText(bundle.getString("Browse")); // NOI18N
        markerDictJPanel.add(markerDictJButton, java.awt.BorderLayout.EAST);

        add(markerDictJPanel);

        OKJButton.setText(bundle.getString("OK")); // NOI18N
        commandsJPanel.add(OKJButton);

        add(commandsJPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void propertiesJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertiesJButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_propertiesJButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton OKJButton;
    public javax.swing.JPanel commandsJPanel;
    public javax.swing.JButton markerDictJButton;
    public javax.swing.JLabel markerDictJLabel;
    public javax.swing.JPanel markerDictJPanel;
    public javax.swing.JTextField markerDictJTextField;
    public javax.swing.JLabel propJLabel;
    public javax.swing.JPanel propJPanel;
    public javax.swing.JTextField propJTextField;
    public javax.swing.JButton propertiesJButton;
    public javax.swing.JButton srcCorpusJButton;
    public javax.swing.JLabel srcCorpusJLabel;
    public javax.swing.JPanel srcCorpusJPanel;
    public javax.swing.JTextField srcCorpusJTextField;
    public javax.swing.JButton srcTMListJButton;
    public javax.swing.JLabel srcTMListJLabel;
    public javax.swing.JPanel srcTMListJPanel;
    public javax.swing.JTextField srcTMListJTextField;
    public javax.swing.JPanel taskJPanel;
    public javax.swing.JLabel taskNameJLabel;
    public javax.swing.JTextField taskNameJTextField;
    public javax.swing.JButton tgtCorpusJButton;
    public javax.swing.JLabel tgtCorpusJLabel;
    public javax.swing.JPanel tgtCorpusJPanel;
    public javax.swing.JTextField tgtCorpusJTextField;
    public javax.swing.JButton tgtCorpusUTF8JButton;
    public javax.swing.JLabel tgtCorpusUTF8JLabel;
    public javax.swing.JPanel tgtCorpusUTF8JPanel;
    public javax.swing.JTextField tgtCorpusUTF8JTextField;
    public javax.swing.JButton tgtTMListJButton;
    public javax.swing.JLabel tgtTMListJLabel;
    public javax.swing.JPanel tgtTMListJPanel;
    public javax.swing.JTextField tgtTMListJTextField;
    // End of variables declaration//GEN-END:variables
    
}
