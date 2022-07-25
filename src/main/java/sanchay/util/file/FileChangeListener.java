/*
 * FileChangeListener.java
 *
 * Created on May 22, 2007, 8:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util.file;

/**
 *
 * @author anil
 */

public interface FileChangeListener {
    /** Invoked when a file changes.   
     * @param fileName name of changed file.
     */
    public void fileChanged(String fileName);
}
