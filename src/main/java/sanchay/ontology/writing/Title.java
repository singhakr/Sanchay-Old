/*
 * Title.java
 *
 * Created on December 14, 2007, 4:58 AM
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
public class Title implements SanchayDOMElement {
    
    String title;
    String subTitle;
    
    /** Creates a new instance of Title */
    public Title() {
    }

    public Title(String t) {
        title = t;
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String t)
    {
        title = t;
    }            
    
    public String getSubTitle()
    {
        return subTitle;
    }

    public void setSubTitle(String t)
    {
        subTitle = t;
    }            
    
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("Title"));
        
        DOMAttribute attribTitle = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("title")), title);
        DOMAttribute attribSubTitle = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("subTitle")), subTitle);
        
        domElement.add(attribTitle);
        domElement.add(attribSubTitle);
        
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
        title = domElement.getAttribute(GlobalProperties.getIntlString("title"));
        subTitle = domElement.getAttribute(GlobalProperties.getIntlString("subTitle"));
    }
}
