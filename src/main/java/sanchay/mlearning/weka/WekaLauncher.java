/*
 * WekaLauncher.java
 *
 * Created on June 24, 2008, 5:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.weka;

import weka.gui.GUIChooser;
import weka.gui.LookAndFeel;
import weka.gui.arffviewer.ArffViewer;

/**
 *
 * @author User
 */
public class WekaLauncher {
    
    /** Creates a new instance of WekaLauncher */
    public WekaLauncher() {
    }
    
    public static void main(String [] args) {
        
        LookAndFeel.setLookAndFeel();
        
        try {
            
            // uncomment to disable the memory management:
            //m_Memory.setEnabled(false);
            
            GUIChooser m_chooser = new GUIChooser();
            m_chooser.setVisible(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
    }
}
