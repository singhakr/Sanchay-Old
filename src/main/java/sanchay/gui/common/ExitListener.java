/*
 * ExitListener.java
 *
 * Created on February 2, 2006, 1:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.awt.event.*;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class ExitListener extends WindowAdapter
{
    java.util.ResourceBundle bundle = GlobalProperties.getResourceBundle(); // NOI18N

    public void windowClosing(WindowEvent event) {
        System.exit(0);
    }
}
