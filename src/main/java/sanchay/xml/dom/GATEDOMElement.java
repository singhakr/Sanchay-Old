/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.xml.dom;

import java.io.PrintStream;

/**
 *
 * @author anil
 */
public interface GATEDOMElement {
    org.dom4j.dom.DOMElement getGATEDOMElement();
    String getGATEXML();
    void readGATEXML(org.w3c.dom.Element domElement);
    void printGATEXML(PrintStream ps);
}
