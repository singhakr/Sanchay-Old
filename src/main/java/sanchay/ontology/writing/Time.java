/*
 * Time.java
 *
 * Created on December 14, 2007, 5:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.ontology.writing;

import java.io.PrintStream;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import sanchay.GlobalProperties;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class Time implements SanchayDOMElement {

    String time;
    
    /** Creates a new instance of Time */
    public Time() {
    }

    public Time(String t) {
        time = t;
    }
    
    public String getTime()
    {
        return time;
    }

    public void setTime(String t)
    {
        time = t;
    }            
        
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("Time"));
        
        DOMAttribute attribTime = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("time")), time);
        domElement.add(attribTime);
        
        return domElement;
    }
    
    public String getXML()
    {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void printXML(PrintStream ps)
    {    
        ps.println(getXML());
    }

    public void readXML(org.w3c.dom.Element domElement) {
        time = domElement.getAttribute(GlobalProperties.getIntlString("time"));
    }
}
