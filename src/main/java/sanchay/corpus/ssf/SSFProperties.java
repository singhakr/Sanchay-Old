/*
 * Created on Aug 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;

/**
 * @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SSFProperties implements Serializable, Cloneable {

    /**
     *
     */
    private KeyValueProperties properties;

    public SSFProperties() {
        super();
        // TODO Auto-generated constructor stub
        properties = new KeyValueProperties();
    }

    public SSFProperties(String pf /*props file*/, String charset) throws FileNotFoundException, IOException {
        super();
        // TODO Auto-generated constructor stub
        properties = new KeyValueProperties(pf, charset);
    }

    public KeyValueProperties getProperties() {
        return properties;
    }

    public void setProperties(KeyValueProperties p) {
        properties = p;
    }

    public int readProperties(String f, String charset) throws FileNotFoundException, IOException // file with two columns
    {
        return properties.read(f, charset);
    }

    public int read(String pf /*props file*/, String charset) throws FileNotFoundException, IOException {
        readProperties(pf, charset);

        return 0;
    }

    public void print(PrintStream ps) {
        ps.println(GlobalProperties.getIntlString("#SSF_properties"));

        Iterator enm = properties.getPropertyKeys();

        while (enm.hasNext()) {
            String key = (String) enm.next();
            ps.println(key + "\t" + properties.getPropertyValue(key));
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException// copyFS($fs)
    {
        SSFProperties obj = (SSFProperties) super.clone();

        obj.properties = (KeyValueProperties) properties.clone();

        return obj;
    }

    public void clear() {
        properties.clear();
    }

    public static void main(String[] args) {
    }
}
