/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay;

import java.util.EventListener;

/**
 *
 * @author anil
 */
public interface SanchayMainEventListener extends EventListener {
    void openTab(SanchayMainEvent evt);
    void displayFile(SanchayMainEvent evt);
}
