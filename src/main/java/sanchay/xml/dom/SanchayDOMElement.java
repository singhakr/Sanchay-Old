/*
 * SanchayDOMElement.java
 *
 * Created on December 13, 2007, 9:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.xml.dom;

import java.io.PrintStream;

/**
 *
 * @author anil
 */
public interface SanchayDOMElement {
    org.dom4j.dom.DOMElement getDOMElement();    
    String getXML();
    void readXML(org.w3c.dom.Element domElement);    
    void printXML(PrintStream ps);    
}
