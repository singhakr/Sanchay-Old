/*
 * PopupListener.java
 *
 * Created on October 5, 2005, 10:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.awt.event.*;
import javax.swing.JPopupMenu;
import sanchay.GlobalProperties;

/**
 *
 *  @author Anil Kumar Singh
 */
public class PopupListener extends MouseAdapter {

    java.util.ResourceBundle bundle = GlobalProperties.getResourceBundle(); // NOI18N
    
    JPopupMenu popup;
    
    /** Creates a new instance of PopupListener */
    public PopupListener(JPopupMenu pm) {
        popup = pm;
    }

    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    private void showPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
        popup.show(e.getComponent(),
               e.getX(), e.getY());
        }
    }
}
