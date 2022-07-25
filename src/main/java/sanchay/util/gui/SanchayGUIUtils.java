/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.util.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;

/**
 *
 * @author User
 */
public class SanchayGUIUtils {
    
    public static void maximizeDialog(JDialog dialog)
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = (int) (0.9 * (double) screenSize.height);
        int screenWidth = screenSize.width;

        dialog.setSize(screenWidth, screenHeight);        
    }
    
}
