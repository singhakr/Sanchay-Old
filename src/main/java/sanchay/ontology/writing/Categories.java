/*
 * WebCorpusCategories.java
 *
 * Created on December 5, 2007, 7:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.ontology.writing;

import java.io.PrintStream;
import java.util.Vector;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import sanchay.GlobalProperties;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class Categories implements SanchayDOMElement {
    
    Vector categories;
    
    /** Creates a new instance of WebCorpusCategories */
    public Categories()
    {
        categories = new Vector(0, 10);
    }
    
    public String getType()
    {
        return GlobalProperties.getIntlString("category");
    }

    public int countCategories() 
    {
        return categories.size();
    }

    public Category getCategory(int num) 
    {
        return (Category) categories.get(num);
    }

    public int addCategory(Category c)
    {
        categories.add(c);
        return categories.size();
    }

    public Category removeCategory(int num) 
    {
        return (Category) categories.remove(num);
    }
    
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }    

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("Categories"));
        
        int ccount = categories.size();
        for (int i = 0; i < ccount; i++)
        {
            Category cat = getCategory(i);
            domElement.add(cat.getDOMElement());
        }
        
        return domElement;
    }

    public String getXML() {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void readXML(org.w3c.dom.Element domElement) {
        NodeList elements = domElement.getElementsByTagName(GlobalProperties.getIntlString("Category"));
        
        int count = elements.getLength();
        
        for (int i = 0; i < count; i++)
        {
            Category cat = new Category();
            cat.readXML((org.w3c.dom.Element) elements.item(i));
            addCategory(cat);
        }
    }
}
