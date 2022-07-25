/*
 * SanchayDOMDocument.java
 *
 * Created on December 28, 2007, 7:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.xml.dom;

/**
 *
 * @author anil
 */
public interface SanchayDOMDocument extends SanchayDOMElement {
    void readXMLFile(String path, String charset);
}
