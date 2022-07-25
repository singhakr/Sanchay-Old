/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.xml.validation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author anil
 */
public class SanchayEntityResolver implements EntityResolver
{
    protected String charset = "UTF-8";
    
    protected KeyValueProperties entities = new KeyValueProperties();

    protected KeyValueProperties entitiesSystemIds = new KeyValueProperties();

    public SanchayEntityResolver(String cs)
    {
        this();

        charset = cs;
    }

    public SanchayEntityResolver()
    {
        addMapping("-//Sanchay//Frameset//EN", "./data/propbank/resource/frameset/frameset.dtd",
                GlobalProperties.getHomeDirectory() + "/data/propbank/resource/frameset/frameset.dtd");
    }

    private void addMapping(String publicID, String systemID, String URL)
    {
        entities.addProperty(publicID, URL);
        entitiesSystemIds.addProperty(systemID, URL);
    }

    @Override
    public InputSource resolveEntity(String publicID, String systemID) throws SAXException, UnsupportedEncodingException, FileNotFoundException
    {
        if (entities.getPropertyValue(publicID) != null)
        {
            String url = (String) entities.getPropertyValue(publicID);

            BufferedReader reader = null;

            if(charset != null && charset.equals("") == false)
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(url), charset));
            else
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(url)));

            InputSource local = new InputSource(reader);
//            InputSource local = new InputSource(url);
            return local;
        }
        else if (entitiesSystemIds.getPropertyValue(systemID) != null)
        {
            String url = (String) entitiesSystemIds.getPropertyValue(systemID);

            BufferedReader reader = null;

            if(charset != null && charset.equals("") == false)
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(url), charset));
            else
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(url)));

            InputSource local = new InputSource(reader);
//            InputSource local = new InputSource(url);
            return local;
        }
        else
        {
            return null;
        }
    }
}
