/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.clients;

import java.awt.Component;
import java.awt.Frame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import sanchay.common.types.ClientType;

/**
 *
 * @author Anil Kumar Singh
 */
public interface SanchayClient {

    String getLangEnc();

    Frame getOwner();

    void setOwner(Frame frame);

    void setParentComponent(Component parentComponent);
    
    String getTitle();
    
    JMenuBar getJMenuBar();

    JPopupMenu getJPopupMenu();
    
    JToolBar getJToolBar();

    ClientType getClientType();
}
