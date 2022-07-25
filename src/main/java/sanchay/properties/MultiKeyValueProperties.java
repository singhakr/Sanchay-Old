/*
 * MultiKeyValueProperties.java
 *
 * Created on April 2, 2006, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.properties;

import java.io.*;
import java.util.*;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sanchay.resources.*;
import sanchay.xml.dom.SanchayDOMElement;


/**
 *
 * @author anil
 */
public class MultiKeyValueProperties extends ResourceImpl implements Serializable, SanchayProperties, SanchayDOMElement
{
    protected LinkedHashMap multiprops;
    
    /** Creates a new instance of MultiKeyValueProperties */
    public MultiKeyValueProperties()
    {
	multiprops = new LinkedHashMap(0, 3);
    }

    public MultiKeyValueProperties(String propFile, String cs) throws FileNotFoundException, IOException
    {
	read(propFile, cs);
    }

    public MultiKeyValueProperties(LinkedHashMap multiprops)
    {
	this.multiprops = multiprops;
    }

    public void print(java.io.PrintStream ps)
    {
	KeyValueProperties.printMany(multiprops, ps);
    }

    public int read(String f, String charset) throws FileNotFoundException, IOException
    {
	multiprops = KeyValueProperties.readMany(f, charset);

	return multiprops.size();
    }
    
    public int save() throws FileNotFoundException, IOException
    {
        KeyValueProperties.saveMany(multiprops, filePath, charset);
        
        return 0;
    }

    public int save(String f, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        KeyValueProperties.saveMany(multiprops, f, cs);
        
        return 0;
    }
    
    public LinkedHashMap getMultiPropertiesMap()
    {
	return multiprops;
    }
    
    public int countProperties()
    {
        return multiprops.size();   
    }

    public Iterator getPropertiesKeys()
    {
        return multiprops.keySet().iterator();
    }

    public KeyValueProperties getPropertiesValue(String p /* Properties key */)
    {
        return (KeyValueProperties) multiprops.get(p);   
    }

    public int addProperties(String p /* properties key */, KeyValueProperties v /*properties value */)
    {
        multiprops.put(p, v);
        
        return multiprops.size();   
    }

    public KeyValueProperties removeProperties(String p /* properties key */)
    {
        return (KeyValueProperties) multiprops.remove(p);   
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement(sanchay.GlobalProperties.getIntlString("MultiKeyValueProperties"));
        Iterator itr = multiprops.keySet().iterator();

        while(itr.hasNext())
        {
            String key = (String) itr.next();
            KeyValueProperties val = (KeyValueProperties) multiprops.get(key);
            
            DOMElement kvpElement = val.getDOMElement();
            DOMAttribute attribName = new DOMAttribute(kvpElement, new org.dom4j.QName(sanchay.GlobalProperties.getIntlString("name")), key);
            domElement.add(kvpElement);
        }
        
        return domElement;
    }

    public String getXML() {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void printXML(PrintStream ps) {
        ps.println(getXML());
    }

    public void readXML(Element domElement) {
        NodeList elements = domElement.getElementsByTagName(sanchay.GlobalProperties.getIntlString("KeyValueProperties"));

        int count = elements.getLength();
        
        for (int i = 0; i < count; i++)
        {
            org.w3c.dom.Element propsElement = (org.w3c.dom.Element) elements.item(i);
            String key = propsElement.getAttribute(sanchay.GlobalProperties.getIntlString("name"));
            
            KeyValueProperties props = new KeyValueProperties();
            props.readXML(propsElement);

            multiprops.put(key, props);
        }        
    }

    public void clear()
    {
        multiprops.clear();
    }
}
