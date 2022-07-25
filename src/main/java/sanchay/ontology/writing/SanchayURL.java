/*
 * SanchayURL.java
 *
 * Created on December 14, 2007, 4:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.ontology.writing;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import sanchay.GlobalProperties;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class SanchayURL implements SanchayDOMElement {
    URL url;
    String urlString;

    /** Creates a new instance of SanchayURL */
    public SanchayURL() {
    }

    public SanchayURL(String u) {
        urlString = u;
    }
    
    public URL getURL()
    {
        return url;
    }

    public void setURL(URL u)
    {
        url = u;
    }            
    
    public String getURLString()
    {
        return urlString;
    }

    public void setURLString(String s)
    {
        urlString = s;
    }            
    
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("URL"));
        
        DOMAttribute attribURL = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("url")), urlString);
        domElement.add(attribURL);
        
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
        urlString = domElement.getAttribute(GlobalProperties.getIntlString("url"));
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
}
