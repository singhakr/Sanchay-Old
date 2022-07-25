/*
 * GenericQuery.java
 *
 * Created on 23 June 2008, 20:43
 */

/**
 *
 * @author  Nitesh Sood, NIT Trichy.
 */


package sanchay.xml.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;

public class GenericQuery extends javax.swing.JPanel {

    private static final int ELEMENT=1;
    private static final int ATTR=2;
    private static final int TEXT=3;
    private static final int COMMENT=8;
    private static final int DOCUMENT=9;
    private NodeList allElements=null;
    private XPathExpression expr=null;
    private Document doc=null;
    private XPath xpath=null;
    private List listOfItems;
    private boolean whereClauseEnabled=false;
    private boolean compoundQueryEnabled=false;
    private String att;
    private JTextField queryField;
    private JButton done;
    private JPanel panel;
    private String selectedFileName=null;
    
    public GenericQuery() throws ParserConfigurationException, SAXException, IOException {
        initComponents();
        initMyComponents();   //to edit some components after they've been created by NB
        //initFrame();   //uncomment to run GenericQuery in a new frame
    }
    
    public void setDoc(String selectedFileName) throws ParserConfigurationException, IOException, SAXException{
        this.selectedFileName=selectedFileName;
        DocumentBuilderFactory builderFactory= DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder=null;
        builder=builderFactory.newDocumentBuilder();
        doc=builder.parse(selectedFileName);
    }
    
    public String getDoc(){
        return this.selectedFileName;
    }

    public void initFrame(){
        
        JFrame frame=new JFrame(GlobalProperties.getIntlString("GenericQuery"));
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
    }
    
    private void initMyComponents(){
        wList.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e){
            wListSelectionEventHandler(e);
        }
    });
    
    panel2.setVisible(false);
    compoundPanel.setVisible(false); 
    cancelButton1.setEnabled(false);
    cancelButton2.setEnabled(false);
    cancelButton3.setEnabled(false);
    cButton.setEnabled(false);
    xpathPanel.setVisible(false);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        wList = new javax.swing.JComboBox();
        relationList = new javax.swing.JComboBox();
        paraField = new javax.swing.JTextField();
        wButton = new javax.swing.JButton();
        panel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        fList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        sList = new javax.swing.JComboBox();
        okButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();
        compoundPanel = new javax.swing.JPanel();
        logicList = new javax.swing.JComboBox();
        textField = new javax.swing.JTextField();
        relationList2 = new javax.swing.JComboBox();
        paraField2 = new javax.swing.JTextField();
        cButton = new javax.swing.JButton();
        cancelButton1 = new javax.swing.JButton();
        cancelButton2 = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        xpathButton = new javax.swing.JButton();
        xpathPanel = new javax.swing.JPanel();
        xqueryField = new javax.swing.JTextField();
        doneButton = new javax.swing.JButton();
        cancelButton3 = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        jLabel3.setText(bundle.getString("WHERE")); // NOI18N

        relationList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "isEqualTo", "isNotEqualTo", "isGreaterThan", "isLessThan", "isGreaterThanOrEqualTo", "isLessThanOrEqualTo" }));

        paraField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(relationList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paraField, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(wList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(relationList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paraField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        wButton.setText(bundle.getString("Click_to_add_WHERE_clause")); // NOI18N
        wButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        wButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wButtonActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("SELECT")); // NOI18N

        jLabel2.setText(bundle.getString("FROM")); // NOI18N

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fList, sList});

        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(fList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        okButton.setText(bundle.getString("OK")); // NOI18N
        okButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        resultArea.setColumns(20);
        resultArea.setFont(new java.awt.Font("Verdana", 1, 14));
        resultArea.setRows(5);
        resultArea.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jScrollPane1.setViewportView(resultArea);

        logicList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "or", "and" }));

        textField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        relationList2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "isEqualTo", "isNotEqualTo", "isGreaterThan", "isLessThan", "isGreaterThanOrEqualTo", "isLessThanOrEqualTo" }));

        paraField2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout compoundPanelLayout = new javax.swing.GroupLayout(compoundPanel);
        compoundPanel.setLayout(compoundPanelLayout);
        compoundPanelLayout.setHorizontalGroup(
            compoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(compoundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logicList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(relationList2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paraField2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        compoundPanelLayout.setVerticalGroup(
            compoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(compoundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(compoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(logicList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(relationList2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paraField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        cButton.setText(bundle.getString("Click_to_create_compound_query")); // NOI18N
        cButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cButtonActionPerformed(evt);
            }
        });

        cancelButton1.setText(bundle.getString("Cancel")); // NOI18N
        cancelButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cancelButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButton1ActionPerformed(evt);
            }
        });

        cancelButton2.setText(bundle.getString("Cancel")); // NOI18N
        cancelButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cancelButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButton2ActionPerformed(evt);
            }
        });

        newButton.setText(bundle.getString("New")); // NOI18N
        newButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        xpathButton.setText(bundle.getString("Click_to_use_XPath")); // NOI18N
        xpathButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        xpathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xpathButtonActionPerformed(evt);
            }
        });

        xqueryField.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        doneButton.setText(bundle.getString("Done")); // NOI18N
        doneButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout xpathPanelLayout = new javax.swing.GroupLayout(xpathPanel);
        xpathPanel.setLayout(xpathPanelLayout);
        xpathPanelLayout.setHorizontalGroup(
            xpathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xpathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xqueryField, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        xpathPanelLayout.setVerticalGroup(
            xpathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xpathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xpathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xqueryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(doneButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelButton3.setText(bundle.getString("Cancel")); // NOI18N
        cancelButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cancelButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(compoundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(cButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cancelButton2))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(wButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cancelButton1))
                                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(xpathButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton3))
                            .addComponent(xpathPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(newButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(41, 41, 41))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {newButton, okButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {compoundPanel, xpathPanel});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wButton)
                    .addComponent(cancelButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cButton)
                    .addComponent(cancelButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(compoundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xpathButton)
                    .addComponent(cancelButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(xpathPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newButton)
                    .addComponent(okButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            queryAnalyzer();//GEN-LAST:event_okButtonActionPerformed
        } catch (XPathExpressionException ex) {
            Logger.getLogger(GenericQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
}                                        

private void wButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wButtonActionPerformed
    whereClauseEnabled=true;
    panel2.setVisible(true);
    cancelButton1.setEnabled(true);
    cButton.setEnabled(true);
}//GEN-LAST:event_wButtonActionPerformed

private void cButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cButtonActionPerformed
compoundQueryEnabled=true;
compoundPanel.setVisible(true);
textField.setText(att);
cancelButton2.setEnabled(true);
}//GEN-LAST:event_cButtonActionPerformed

private void cancelButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButton1ActionPerformed
paraField.setText("");
panel2.setVisible(false);
wList.setSelectedIndex(0);
relationList.setSelectedIndex(0);
whereClauseEnabled=false;
cancelButton1.setEnabled(false);
paraField2.setText("");
textField.setText("");
compoundPanel.setVisible(false);
logicList.setSelectedIndex(0);
relationList2.setSelectedIndex(0);
compoundQueryEnabled=false;
cancelButton2.setEnabled(false);
cButton.setEnabled(false);
}//GEN-LAST:event_cancelButton1ActionPerformed

private void cancelButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButton2ActionPerformed
paraField2.setText("");
textField.setText("");
compoundPanel.setVisible(false);
logicList.setSelectedIndex(0);
relationList2.setSelectedIndex(0);
compoundQueryEnabled=false;
cancelButton2.setEnabled(false);
}//GEN-LAST:event_cancelButton2ActionPerformed

private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
paraField.setText("");
panel2.setVisible(false);
sList.setSelectedIndex(0);
fList.setSelectedIndex(0);
wList.setSelectedIndex(0);
relationList.setSelectedIndex(0);
whereClauseEnabled=false;
cancelButton1.setEnabled(false);
paraField2.setText("");
textField.setText("");
compoundPanel.setVisible(false);
logicList.setSelectedIndex(0);
relationList2.setSelectedIndex(0);
compoundQueryEnabled=false;
cancelButton2.setEnabled(false);
cButton.setEnabled(false);
resultArea.setText("");
xpathPanel.setVisible(false);
}//GEN-LAST:event_newButtonActionPerformed

private void xpathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xpathButtonActionPerformed
xpathPanel.setVisible(true);
paraField.setText("");
panel2.setVisible(false);
sList.setSelectedIndex(0);
fList.setSelectedIndex(0);
wList.setSelectedIndex(0);
relationList.setSelectedIndex(0);
whereClauseEnabled = false;
cancelButton1.setEnabled(false);
paraField2.setText("");
textField.setText("");
compoundPanel.setVisible(false);
logicList.setSelectedIndex(0);
relationList2.setSelectedIndex(0);
compoundQueryEnabled = false;
cancelButton2.setEnabled(false);
cButton.setEnabled(false);
resultArea.setText("");
xpathButton.setEnabled(false);  
cancelButton3.setEnabled(true);
wButton.setEnabled(false);
sList.setEnabled(false);
fList.setEnabled(false);
}//GEN-LAST:event_xpathButtonActionPerformed

private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        try {
            queryDirectly();
        } catch (XPathExpressionException ex) {
            Logger.getLogger(GenericQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_doneButtonActionPerformed

private void cancelButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButton3ActionPerformed
    
newButtonActionPerformed(evt);
sList.setEnabled(true);
fList.setEnabled(true);
xpathButton.setEnabled(true);
cancelButton3.setEnabled(false);
wButton.setEnabled(true);
}//GEN-LAST:event_cancelButton3ActionPerformed
                                        
private void wListSelectionEventHandler(ActionEvent e){
    XMLQueryListItem temp=null;
    temp=(XMLQueryListItem)wList.getSelectedItem();
    att=temp.getItemName();
}

public void fillLists() throws ParserConfigurationException, SAXException, IOException {

    XPathFactory xfact=XPathFactory.newInstance();
    xpath=xfact.newXPath();
    
    allElements=doc.getDocumentElement().getElementsByTagName("*");
    
    //DefaultComboBoxModel sModel=new DefaultComboBoxModel();
    XMLQueryComboBoxModel sModel=createGeneralListModel(allElements);
    sList.setModel(sModel);
    sList.setRenderer(new MyListCellRenderer());
    sList.setSelectedIndex(0);
    
    //DefaultComboBoxModel fModel=new DefaultComboBoxModel();
    XMLQueryComboBoxModel fModel=createGeneralListModel(allElements);
    fModel=createGeneralListModel(allElements);
    fList.setModel(fModel);
    fList.setRenderer(new MyListCellRenderer());
    fList.setSelectedIndex(0);
    
    //DefaultComboBoxModel wModel=new DefaultComboBoxModel();
    XMLQueryComboBoxModel wModel=createGeneralListModel(allElements);
    wList.setModel(wModel);
    wList.setRenderer(new MyListCellRenderer());
    wList.setSelectedIndex(0);
}

private XMLQueryComboBoxModel createGeneralListModel(NodeList allElements){
       
    boolean elementInList=false;
    String temp=null;
    NamedNodeMap possibleAtts=null;
    listOfItems=new ArrayList(); 
    XMLQueryListItem t=null;
    Attr a=null;
    
    Element root=doc.getDocumentElement();
    listOfItems.add(new XMLQueryListItem(root.getTagName(),DOCUMENT,null,null));
    
    for (int i=0;i<allElements.getLength();i++) {
       elementInList=false;
       temp=allElements.item(i).getNodeName();
       for(int k=0;k<listOfItems.size();k++) {
            t=(XMLQueryListItem) listOfItems.get(k);
            if((t.getItemName().compareTo(temp))==0){ 
                elementInList=true;
                break;
            }
       }
       if(!elementInList){
           if (allElements.item(i).getNodeType()!=TEXT || 
                    allElements.item(i).getNodeType()!=COMMENT)
                    {    listOfItems.add(new XMLQueryListItem(allElements.item(i).getNodeName(),ELEMENT,
                        allElements.item(i).getParentNode().getNodeName(),
                        null));
                    }
           if(allElements.item(i).hasAttributes()){
                possibleAtts=allElements.item(i).getAttributes();
                for(int j=0;j<possibleAtts.getLength();j++){
                   temp=possibleAtts.item(j).getNodeName();
                   a=(Attr) possibleAtts.item(j);
                   listOfItems.add(new XMLQueryListItem(temp,ATTR,null,a.getOwnerElement()
                           .getNodeName()));
                }
            }
        }
       }
    
    XMLQueryComboBoxModel model=new XMLQueryComboBoxModel(listOfItems);
    return model;
}

public void queryAnalyzer() throws XPathExpressionException{
      
    //item selected from the WHERE combobox
    XMLQueryListItem selectedItemFromWhereList=(XMLQueryListItem) wList.getSelectedItem();
    //item selected from the SELECT combobox
    XMLQueryListItem selectedItemFromSelectList=(XMLQueryListItem) sList.getSelectedItem();
    //string representation of selectedItemFromWhereList
    String stringRep= selectedItemFromWhereList.getItemName();
    String query=null;
    
    if(!whereClauseEnabled) {
        query=simpleQueryAnalyzer(selectedItemFromSelectList);
    }
    else{
        if(selectedItemFromSelectList.getItemType()==ATTR && 
                selectedItemFromWhereList.getItemType()==ELEMENT) {
            query=funcOne(selectedItemFromSelectList,selectedItemFromWhereList);
        }
        else if(selectedItemFromSelectList.getItemType()==ELEMENT && 
               selectedItemFromWhereList.getItemType()==ATTR) {
            query=funcTwo(selectedItemFromSelectList,selectedItemFromWhereList,stringRep);
        }
        else if(selectedItemFromSelectList.getItemType()==ELEMENT &&
               selectedItemFromWhereList.getItemType()==ELEMENT) {
            query=funcThree(selectedItemFromSelectList,selectedItemFromWhereList);
        }
        else if(selectedItemFromSelectList.getItemType()==ATTR &&
                 selectedItemFromWhereList.getItemType()==ATTR)
        {  query=funcFour(selectedItemFromSelectList,selectedItemFromWhereList);System.out.println(GlobalProperties.getIntlString("hi"));}
    }
    
        Object resultObject=xpath.evaluate(query,doc,XPathConstants.NODESET);
        NodeList result=(NodeList) resultObject;

        fillResultArea(result);
}

private String simpleQueryAnalyzer(XMLQueryListItem a) throws XPathExpressionException{
       
    XMLQueryListItem b=(XMLQueryListItem) fList.getSelectedItem();
    
    // a is the selected item in the SELECT list
    // b is the selected item in the FROM list
    
    String query="//"+b.getItemName()
            +"//"+a.getItemName();
    
    if(a.getItemType()==ELEMENT) {
        query+=GlobalProperties.getIntlString("/text()");
    }
    else if(a.getItemType()==ATTR){
        int index=query.lastIndexOf("/");    
        String sub="@";
        query=subStringInserter(query,sub,index);
    }

    //System.out.println("AMB-->\t"+query);
            
    return query;
  
}

private String compoundQueryAnalyzer(String query){
    
    query+=GlobalProperties.getIntlString("_or_@")+att+relation2sign((String) relationList2.getSelectedItem())
            +"'"+paraField2.getText()+"'";
    
    return query;
}

private String relation2sign(String relation) {
    
    if(relation.compareTo(GlobalProperties.getIntlString("isEqualTo"))==0) {
        return "=";
    }
    else if(relation.compareTo(GlobalProperties.getIntlString("isNotEqualTo"))==0) {
        return "!=";
    }
    else if(relation.compareTo(GlobalProperties.getIntlString("isGreaterThan"))==0) {
        return ">";
    }
    else if(relation.compareTo(GlobalProperties.getIntlString("isLessThan"))==0) {
        return "<";
    }
    else if(relation.compareTo(GlobalProperties.getIntlString("isGreaterThanOrEqualTo"))==0) {
        return ">=";
    }
    else if(relation.compareTo(GlobalProperties.getIntlString("isLessThanOrEqualTo"))==0) {
        return "<=";
    }
    else {
        return null;
    }
}

private String funcOne(XMLQueryListItem a,XMLQueryListItem b) throws XPathExpressionException{
    
    // a=selectedItemFromSelectList
    // b=selectedItemFromWhereList
    
    String query="/"+doc.getDocumentElement().getTagName()+"//"+b.getParentOfItem()+
            "["+b.getItemName()+relation2sign((String)relationList.getSelectedItem())+
            "'"+paraField.getText()+"']//"+a.getOwnerOfItem()+"/@"+a.getItemName();
 
    return query;
}

private String funcTwo (XMLQueryListItem a,XMLQueryListItem b,
        String stringRep) throws XPathExpressionException{
    
    //a=selectedItemFromSelectList
    //b=selectedItemFromWhereList
    
    Attr selectedAttr=null;     //selected attribute from wList
    Element selectedAttrOwner=null;     //owner element of the selected attribute from wList
    NamedNodeMap atts=null;
    
    for (int i=0;i<allElements.getLength();i++) {
        if(allElements.item(i).hasAttributes()) {
            atts=allElements.item(i).getAttributes();
            for(int j=0;j<atts.getLength();j++){
                if(atts.item(j).getNodeName().compareTo(stringRep)==0) {
                    selectedAttr=(Attr) atts.item(j);
                    selectedAttrOwner= selectedAttr.getOwnerElement();
                    break;
                }
            }
        }
    }
    
    String query="/"+doc.getDocumentElement().getTagName()+"//"+selectedAttrOwner.getParentNode()
            .getNodeName()+"["+selectedAttrOwner.getNodeName()+"[@"+selectedAttr.getNodeName()+
            relation2sign((String)relationList.getSelectedItem())+"'"+paraField.getText()+"'";
    
    if(compoundQueryEnabled){
        query=compoundQueryAnalyzer(query);
    }
    
    query+="]]//"+a.getItemName();
            
    if(a.getItemType()==ELEMENT) {
        query+=GlobalProperties.getIntlString("/text()");
    }
              
    return query;
}

private String funcThree(XMLQueryListItem a,XMLQueryListItem b){
    //a=selectedItemFromSelectList
    //b=selectedItemFromWhereList
    
    String query="/"+doc.getDocumentElement().getTagName()+"//"+b.getParentOfItem()
            +"["+b.getItemName()+relation2sign((String)relationList.getSelectedItem())+
            "'"+paraField.getText()+"']//"+a.getItemName()+GlobalProperties.getIntlString("/text()");
    
    if(compoundQueryEnabled){
        String sub=" "+(String)logicList.getSelectedItem()+" "+textField.getText()+
                relation2sign((String)relationList2.getSelectedItem())+"'"+paraField2.getText()+
                "'";
        int index=query.lastIndexOf("'");
        query=subStringInserter(query,sub,index);
    }
    return query;
}

private String funcFour(XMLQueryListItem a,XMLQueryListItem b){
    //a=selectedItemFromSelectList
    //b=selectedItemFromWhereList
    
    String query="/"+doc.getDocumentElement().getTagName()+"//"+b.getOwnerOfItem()+
            "[@"+b.getItemName()+relation2sign((String)relationList.getSelectedItem())+"'"
            +paraField.getText()+"']/@"+a.getItemName();
    
    if(compoundQueryEnabled){
        String sub=" "+(String)logicList.getSelectedItem()+" @"+b.getItemName()
                +relation2sign((String)relationList2.getSelectedItem())+"'"+paraField2.getText()+"'";
        int index=query.lastIndexOf("'");
        query=subStringInserter(query,sub,index);
    }
    return query;
}

private String subStringInserter(String aString,String aSubString,int index){
    String prefix=aString.substring(0,index+1);
    String suffix=aString.substring(index+1);
    
    return prefix+aSubString+suffix;
}

//uncomment the following code to allow user to enter an xpath language query
// in a new frame

/*
private void directQuery() throws XPathExpressionException{
    initDQFrame();
} 

  private void initDQFrame(){
         
        done=new JButton("Done");
        done.setBorder(javax.swing.BorderFactory
                .createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        done.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                try {
                    queryDirectly();
                } catch (XPathExpressionException ex) {
                    Logger.getLogger(GenericQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        queryField=new JTextField("",20);
        queryField.setBorder(javax.swing.BorderFactory
                .createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        panel=new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(queryField);
        panel.add(done);
        
        JFrame frame=new JFrame("XPath");
        
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
}
*/

public void queryDirectly() throws XPathExpressionException{
 
    String str=xqueryField.getText();
    queryDirectly(str);
}

public void queryDirectly(String str) throws XPathExpressionException
{
    XPathFactory xfact=XPathFactory.newInstance();
    xpath=xfact.newXPath();
    NodeList result = (NodeList)xpath.evaluate(str,doc,XPathConstants.NODESET);
    fillResultArea(result);
}

public NodeList getQueryResult(String str) throws XPathExpressionException
{
    XPathFactory xfact=XPathFactory.newInstance();
    xpath=xfact.newXPath();
    NodeList result = (NodeList)xpath.evaluate(str,doc,XPathConstants.NODESET);
    return result;
}

public void fillResultArea(NodeList result)
{
    if(result.getLength()==0)
    {
        resultArea.append(GlobalProperties.getIntlString("\n_Sorry,_no_items_were_found."));
        resultArea.append(GlobalProperties.getIntlString("\n\n_Note:_You_can_try_submitting_the_query_using_XPath."));
    }
    for(int i=0;i<result.getLength();i++){
        resultArea.append(result.item(i).getNodeValue()+"\n");
    }
}

public JTextArea getResultArea()
{
    return resultArea;
}

/*
public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException{
    GenericQuery gq=new GenericQuery();
}*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cButton;
    private javax.swing.JButton cancelButton1;
    private javax.swing.JButton cancelButton2;
    private javax.swing.JButton cancelButton3;
    private javax.swing.JPanel compoundPanel;
    private javax.swing.JButton doneButton;
    private javax.swing.JComboBox fList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox logicList;
    private javax.swing.JButton newButton;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JTextField paraField;
    private javax.swing.JTextField paraField2;
    private javax.swing.JComboBox relationList;
    private javax.swing.JComboBox relationList2;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JComboBox sList;
    private javax.swing.JTextField textField;
    private javax.swing.JButton wButton;
    private javax.swing.JComboBox wList;
    private javax.swing.JButton xpathButton;
    private javax.swing.JPanel xpathPanel;
    private javax.swing.JTextField xqueryField;
    // End of variables declaration//GEN-END:variables

}
