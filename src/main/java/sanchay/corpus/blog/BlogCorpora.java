/*
 * BlogCorpora.java
 *
 * Created on December 5, 2007, 7:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.PrintStream;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import sanchay.properties.KeyValueProperties;
import sanchay.xml.dom.SanchayDOMDocument;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author Anil Kumar Singh
 */
public class BlogCorpora implements SanchayDOMDocument {

    // For things like:
    // Workspace path (corpora path)
    // Path to the Corpora KeyValueProperties
    // Path to the Categories KeyValueProperties
    KeyValueProperties corporaProps;

    // Key is the blog corpus name (corresponding to the blog domain name)
    // Value is the path to the blog corpus key value properties file 
    KeyValueProperties corpora;
    
    /** Creates a new instance of BlogCorpora */
    public BlogCorpora() {
    }

    public KeyValueProperties getCorporaProps()
    {
        return corporaProps;
    }

    public void setCorporaProps(KeyValueProperties p)
    {
        corporaProps = p;
    }

    public KeyValueProperties getCorpora()
    {
        return corpora;
    }

    public void setCorpora(KeyValueProperties c)
    {
        corpora = c;
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement("BlogCorpora");
        
        DOMElement domElementProps = corporaProps.getDOMElement();
        DOMAttribute attribType = new DOMAttribute(new org.dom4j.QName("type"), "corporaProps");
        domElementProps.add(attribType);
        domElement.add(domElementProps);
        
        DOMElement domElementCorpora = corporaProps.getDOMElement();
        DOMAttribute attribCorpora = new DOMAttribute(new org.dom4j.QName("type"), "corpora");
        domElementCorpora.add(attribCorpora);
        domElement.add(domElementCorpora);

        return domElement;
    }

    public String getXML() {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void printXML(PrintStream ps) {
        ps.println(getXML());
    }

    public void readXMLFile(String path, String charset) {
    }

    public void readXML(org.w3c.dom.Element domElement) {
    }
}
