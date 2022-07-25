/*
 * BlogTags.java
 *
 * Created on December 9, 2007, 11:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.ontology.writing;

import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import sanchay.GlobalProperties;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class Tags extends Categories implements SanchayDOMElement {
    
    /** Creates a new instance of BlogTags */
    public Tags()
    {
        super();
    }

    public DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();
        DOMAttribute attribType = new DOMAttribute(new org.dom4j.QName(GlobalProperties.getIntlString("type")), GlobalProperties.getIntlString("tag"));
        domElement.add(attribType);
        
        return domElement;
    }
}
