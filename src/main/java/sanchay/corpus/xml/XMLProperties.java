/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import java.io.Serializable;
import java.util.Iterator;
import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class XMLProperties implements Serializable {

    /**
     *
     */

    private KeyValueProperties properties;

    public XMLProperties() {
            super();
            // TODO Auto-generated constructor stub
            properties = new KeyValueProperties();
    }

    public XMLProperties(String pf /*props file*/, String charset)  throws FileNotFoundException, IOException {
            super();
            // TODO Auto-generated constructor stub
            properties = new KeyValueProperties(pf, charset);
    }

    public KeyValueProperties getProperties()
    {
            return properties;
    }

    public void setProperties(KeyValueProperties p)
    {
            properties = p;
    }

    public int readProperties(String f, String charset) throws FileNotFoundException, IOException
    // file with two columns
    {
        return properties.read(f, charset);
    }

    public int read(String pf /*props file*/, String charset) throws FileNotFoundException, IOException
    {
            readProperties(pf, charset);

            return 0;
    }

    public void print(PrintStream ps)
    {
            ps.println("#XML_properties");

            Iterator enm = properties.getPropertyKeys();

            while(enm.hasNext())
            {
                    String key = (String) enm.next();
                    ps.println(key + "\t" + properties.getPropertyValue(key));
            }
    }

    public Object clone() throws CloneNotSupportedException// copyFS($fs)
    {
        XMLProperties obj = (XMLProperties) super.clone();

        obj.properties = (KeyValueProperties) properties.clone();

        return obj;
    }

    public void clear()
    {
        properties.clear();
    }

    public static void main(String[] args)
    {
    }
}
