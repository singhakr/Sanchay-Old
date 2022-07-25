/*
 * AnnotationClient.java
 *
 * Created on October 9, 2005, 11:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.clients;

import javax.swing.*;

import sanchay.properties.*;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public interface AnnotationClient extends SanchayClient {
    void clear();

    void configure(String pmPath, String charSet);

    PropertiesManager getPropertiesManager();

    String getWorkspace();
    
    PropertiesTable getTaskList();
    
    void setTaskList(PropertiesTable tl);

    void setCurrentPosition(int cp);

    void setWorkspace(String p) throws Exception;
    
    void setWorkJPanel(JPanel wjp);
}
