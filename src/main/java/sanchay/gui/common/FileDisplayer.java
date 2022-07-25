/*
 * FileDisplayer.java
 *
 * Created on May 16, 2006, 6:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.io.*;
import java.util.EventListener;
import java.util.EventObject;

/**
 *
 * @author Anil Kumar Singh
 */
public interface FileDisplayer extends EventListener {
    boolean closeFile(EventObject e);
    void displayFile(EventObject e);
    void displayFile(String path, String charset, EventObject e);
    void displayFile(File file, String charset, EventObject e);
    String getDisplayedFile(EventObject e);
    String getCharset(EventObject e);
}
