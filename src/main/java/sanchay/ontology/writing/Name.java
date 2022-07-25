/*
 * Name.java
 *
 * Created on December 14, 2007, 4:42 AM
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
public class Name implements SanchayDOMElement {
    
    String title;
    String fname;
    String mname;
    String lname;
    
    /** Creates a new instance of Name */
    public Name() {
    }

    public Name(String n) {
        setName(n);
    }
    
    public String getName()
    {
        String name = title + " " + fname + " " + lname + " " + lname;
        
        name = name.replaceAll("  ", " ");
        name = name.trim();
        
        return name;
    }

    public void setName(String n)
    {
        fname = n;
    }

    public void setName(String n, boolean hasTitle, boolean hasMiddleName)
    {
        String parts[] = n.split("[\\s+]");
        
        if(hasTitle)
            title = parts[0];
        
        if(hasTitle && hasMiddleName)
        {
            fname = parts[1];
            mname = parts[2];
            
            if(parts.length == 4)
                lname = parts[3];
        }
        else if(hasMiddleName)
        {
            fname = parts[0];
            mname = parts[1];

            if(parts.length == 3)
                lname = parts[2];
        }
    }            
    
    public String getFirstName()
    {
        return fname;
    }

    public void setFirstName(String n)
    {
        fname = n;
    }                
    
    public String getMiddleName()
    {
        return mname;
    }

    public void setMiddleName(String n)
    {
        mname = n;
    }                
    
    public String getLastName()
    {
        return lname;
    }

    public void setLastName(String n)
    {
        lname = n;
    }                

    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("Name"));
        
        DOMAttribute attribTitle = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("title")), title);
        DOMAttribute attribFName = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("fname")), fname);
        DOMAttribute attribMName = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("mname")), mname);
        DOMAttribute attribLName = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("lname")), lname);
        
        domElement.add(attribTitle);
        domElement.add(attribFName);
        domElement.add(attribMName);
        domElement.add(attribLName);
        
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
        fname = domElement.getAttribute(GlobalProperties.getIntlString("fname"));
        mname = domElement.getAttribute(GlobalProperties.getIntlString("mname"));
        lname = domElement.getAttribute(GlobalProperties.getIntlString("lname"));
    }
}
