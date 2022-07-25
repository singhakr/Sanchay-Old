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
public interface TypeCraftDOMElement {
    org.dom4j.dom.DOMElement getTypeCraftDOMElement();
    String getTypeCraftXML();
    void readTypeCraftXML(org.w3c.dom.Element domElement);
    void printTypeCraftXML(PrintStream ps);
}
