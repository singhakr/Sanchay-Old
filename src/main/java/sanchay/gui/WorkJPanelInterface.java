/*
 * WorkJPanelInterface.java
 *
 * Created on October 10, 2005, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui;

import java.io.*;
import javax.swing.*;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public interface WorkJPanelInterface extends DataStructureJPanelInterface {

    public void configure();

    public void convertToXML(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException;

    public String getXML(int pos);

    public void printXML(PrintStream ps);

    public void setTaskName(String tn);
    
}
