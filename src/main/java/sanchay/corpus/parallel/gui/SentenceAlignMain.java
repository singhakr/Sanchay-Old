/*
 * SentenceAlignMain.java
 *
 * Created on November 16, 2005, 2:55 AM
 */

package sanchay.corpus.parallel.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.*;
import javax.swing.*;

import sanchay.GlobalProperties;
import sanchay.gui.clients.AnnotationClient;
import sanchay.gui.*;
import sanchay.properties.PropertiesManager;
import sanchay.properties.PropertiesTable;
import sanchay.common.types.ClientType;


/**
 *
 * @author  root
 */
public class SentenceAlignMain extends javax.swing.JFrame implements AnnotationClient {

    protected ClientType clientType = ClientType.SENTENCE_ALIGNMENT_INTERFACE;
    
       private JPanel tamMarkerJPanel;
    
    private String workspace;
    private PropertiesManager propman;
    
    protected String langEnc;
    private int currentPosition;
    
    /** Creates new form SentenceAlignMain */
    public SentenceAlignMain() {
        setTitle("GUI FOR SENTENCE ALIGNMENT");
        initComponents();
    
	tamMarkerJPanel = new SelectTaskJPanel(SelectTaskJPanel.SENTENCE_ALIGNMENT_TASK);
        tamMarkerJPanel.setBounds(0, 0, 550, 130);
        desktopPane.add(tamMarkerJPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        setBounds(250, 300, 558, 130);
         configure("workspace/tam-marker/server-props.txt", GlobalProperties.getIntlString("UTF-8"));
        ((SelectTaskJPanel) tamMarkerJPanel).setOwner(this);
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    private void initComponents() {                          
        desktopPane = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
     //aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(desktopPane, java.awt.BorderLayout.CENTER);

        fileMenu.setText(GlobalProperties.getIntlString("File"));
        openMenuItem.setText(GlobalProperties.getIntlString("Open"));
        fileMenu.add(openMenuItem);

        saveMenuItem.setText(GlobalProperties.getIntlString("Save"));
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText(GlobalProperties.getIntlString("Save_As_..."));
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setText(GlobalProperties.getIntlString("Exit"));
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
          fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText(GlobalProperties.getIntlString("Edit"));
        cutMenuItem.setText(GlobalProperties.getIntlString("Cut"));
        editMenu.add(cutMenuItem);

        copyMenuItem.setText(GlobalProperties.getIntlString("Copy"));
        editMenu.add(copyMenuItem);

        pasteMenuItem.setText(GlobalProperties.getIntlString("Paste"));
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setText(GlobalProperties.getIntlString("Delete"));
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setText(GlobalProperties.getIntlString("Help"));
        contentMenuItem.setText(GlobalProperties.getIntlString("Contents"));
        helpMenu.add(contentMenuItem);

        aboutMenuItem.setText(GlobalProperties.getIntlString("About"));
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }                       
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                             
        System.exit(0);
    }                                            
    
    public String getWorkspace()
      {
        return workspace;
    }
    
    public void setWorkspace(String p) throws Exception
    {
        workspace = p;
        File wsdir = new File(p);

        if(wsdir.exists() == false)
        {
            if(wsdir.mkdir() == false)
                throw new Exception();
        }
        else if(wsdir.isDirectory() == false)
        throw new Exception();
    }
	
    public PropertiesManager getPropertiesManager()
    {
        return propman;
    }
    
    public void configure(String pmPath, String charSet)
    {
        clear();
        
        try {
            propman = new PropertiesManager(pmPath, charSet);
            propman.print(System.out);
                
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void setCurrentPosition(int cp)
     {
        currentPosition = cp;
    }
    
    public void clear()
    {
        workspace = "/root/Project/Sanchay";
    }
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   /* private void initComponents() {
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(0, 400, Short.MAX_VALUE)
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(0, 300, Short.MAX_VALUE)
                );
        pack();
    }*/
    // </editor-fold>//GEN-END:initComponents

    public void setWorkJPanel(JPanel wjp)
    {
        
    }
  
    public PropertiesTable getTaskList()
    {
        return null;
    }
    
    public void setTaskList(PropertiesTable tl)
    {
        
    }

    public String getLangEnc()
    {
        return langEnc;
    }
    
    public Frame getOwner()
    {
        return this;
    }
    
    public void setOwner(Frame f)
    {
    }

    public void setParentComponent(Component parentComponent)
    {
    }

    public JMenuBar getJMenuBar() {
        throw new UnsupportedOperationException(GlobalProperties.getIntlString("Not_supported_yet."));
    }

    public JToolBar getJToolBar() {
        throw new UnsupportedOperationException(GlobalProperties.getIntlString("Not_supported_yet."));
    }

    public JPopupMenu getJPopupMenu()
    {
        throw new UnsupportedOperationException(GlobalProperties.getIntlString("Not_supported_yet."));
    }    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SentenceAlignMain().setVisible(true);
            }
        });
    } 
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem contentMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
