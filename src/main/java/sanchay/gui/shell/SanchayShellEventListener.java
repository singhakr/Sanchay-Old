/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.shell;

import java.util.EventListener;

/**
 *
 * @author anil
 */
public interface SanchayShellEventListener extends EventListener {
    void handledShellEvent(SanchayShellEvent evt);
}
