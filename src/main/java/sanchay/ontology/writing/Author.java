/*
 * BlogAuthor.java
 *
 * Created on December 9, 2007, 11:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.ontology.writing;

import java.io.PrintStream;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import sanchay.GlobalProperties;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class Author implements SanchayDOMElement {
    
    Name name;
    String userid;
    String email;
    SanchayURL url;
    
    /** Creates a new instance of BlogAuthor */
    public Author()
    {
    }    

    public Author(String n)
    {
        name = new Name(n);
    }    
    
    public Name getName()
    {
        return name;
    }

    public void setName(Name n)
    {
        name = n;
    }            
    
    public String getUserId()
    {
        return userid;
    }

    public void setUserId(String u)
    {
        userid = u;
    }            
    
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String e)
    {
        email = e;
    }            
    
    public SanchayURL getURL()
    {
        return url;
    }

    public void setURL(SanchayURL u)
    {
        url = u;
    }            

    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("Author"));
        
        DOMAttribute attribUserId = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("userid")), userid);
        DOMAttribute attribEmail = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("email")), email);
        
        domElement.add(attribUserId);
        domElement.add(attribEmail);

        DOMElement domElementName = name.getDOMElement();
        domElement.add(domElementName);

        if(url != null)
        {
            DOMElement domElementURL = url.getDOMElement();
            domElement.add(domElementURL);
        }
        
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
        NodeList elements = domElement.getElementsByTagName(GlobalProperties.getIntlString("Name"));

        if(elements.item(0) != null)
        {
            name = new Name();
            name.readXML((org.w3c.dom.Element) elements.item(0));
        }
        
        userid = domElement.getAttribute(GlobalProperties.getIntlString("userid"));
        email = domElement.getAttribute(GlobalProperties.getIntlString("email"));

        elements = domElement.getElementsByTagName(GlobalProperties.getIntlString("URL"));
        
        if(elements.item(0) != null)
        {
            url = new SanchayURL();
            url.readXML((org.w3c.dom.Element) elements.item(0));
        }
    }
}
