/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FramesetVNRoleJPanel.java
 *
 * Created on 16 Sep, 2009, 3:45:08 AM
 */

package sanchay.propbank.gui;

import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.propbank.FramesetVNRole;

/**
 *
 * @author anil
 */
public class FramesetVNRoleJPanel extends javax.swing.JPanel {

    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");

    protected FramesetVNRole vnrole;

    protected FramesetRoleJPanel framesetRoleJPanel;

    /** Creates new form FramesetVNRoleJPanel */
    public FramesetVNRoleJPanel(String langEnc) {
       this.langEnc = langEnc;

       initComponents();

       vnrole = new FramesetVNRole();
    }

    public void init()
    {
        FeatureStructure fs = vnrole.getAttributes();

        int acount = fs.countAttributes();

        for (int i = 0; i < acount; i++)
        {
            FeatureAttribute fa = fs.getAttribute(i);
            FeatureValue fv = fa.getAltValue(0);

            String val = "";

            if(fv != null)
                val = fv.getValue().toString();

            AttributeValueJPanel framesetVNRoleAttribJPanel = new AttributeValueJPanel(fa.getName(), val, langEnc);
            framesetVNRoleAttribJPanel.setFeatureStructure(fs);
            attribsJPanel.add(framesetVNRoleAttribJPanel);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        attribsJPanel = new javax.swing.JPanel();
        commandsJPanel = new javax.swing.JPanel();
        removeJButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "VN Role"));
        setLayout(new java.awt.BorderLayout());

        attribsJPanel.setLayout(new java.awt.GridLayout(0, 3, 5, 5));
        add(attribsJPanel, java.awt.BorderLayout.CENTER);

        commandsJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        removeJButton.setText(bundle.getString("Remove")); // NOI18N
        removeJButton.setToolTipText(bundle.getString("Remove_VNRole")); // NOI18N
        removeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeJButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(removeJButton);

        add(commandsJPanel, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void removeJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removeJButtonActionPerformed
    {//GEN-HEADEREND:event_removeJButtonActionPerformed
        // TODO add your handling code here:
        getFramesetRoleJPanel().removeVNRole(this);
    }//GEN-LAST:event_removeJButtonActionPerformed

    public FramesetRoleJPanel getFramesetRoleJPanel()
    {
        return framesetRoleJPanel;
    }

    public void setFramesetRoleJPanel(FramesetRoleJPanel f)
    {
        framesetRoleJPanel = f;
    }

    /**
     * @return the vnrole
     */
    public FramesetVNRole getVNRole()
    {
        return vnrole;
    }

    /**
     * @param vnrole the vnrole to set
     */
    public void setVNRole(FramesetVNRole vnrole)
    {
        this.vnrole = vnrole;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel attribsJPanel;
    private javax.swing.JPanel commandsJPanel;
    private javax.swing.JButton removeJButton;
    // End of variables declaration//GEN-END:variables

}