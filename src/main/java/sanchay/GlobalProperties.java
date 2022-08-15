/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author anil
 */
public class GlobalProperties implements Serializable {

    public static KeyValueProperties properties = new KeyValueProperties();
    public static KeyValueProperties clientModes = new KeyValueProperties();
//    public static ResourceBundle sanchayResourceBundle = ResourceBundle.getBundle("sanchay");
    public static ResourceBundle sanchayResourceBundle = ResourceBundle.getBundle("sanchay");

    protected static String SANCHAY_HOME;
    protected static String USER_HOME;

    public static KeyValueProperties getProperties()
    {
        if(properties.countProperties() == 0)
        {
            try
            {
                properties.read(GlobalProperties.getHomeDirectory() + "/" + "props/sanchay-props.txt", sanchayResourceBundle.getString("UTF-8"));
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(GlobalProperties.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(GlobalProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return properties;
    }

    public static void setProperties(KeyValueProperties props)
    {
        properties = props;
    }

    public static void readProperties(String path, String cs) throws FileNotFoundException, IOException
    {
        properties.read(path, cs);
    }

    public static KeyValueProperties getClientModes()
    {
        if(clientModes.countProperties() == 0)
        {
            try
            {
                clientModes.read(GlobalProperties.getHomeDirectory() + "/" + "props/client-modes.txt", sanchayResourceBundle.getString("UTF-8"));
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(GlobalProperties.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(GlobalProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return clientModes;
    }

    public static void setClientModes(KeyValueProperties props)
    {
        clientModes = props;
    }

    public static void readClientModes(String path, String cs) throws FileNotFoundException, IOException
    {
        clientModes.read(path, cs);
    }

    public static String getHomeDirectory()
    {
        Class myClass = SanchayMain.class;

        String className = SanchayMain.class.getName();

        className = className.replaceAll("sanchay.", "");

        URL url = myClass.getResource(className + ".class");

        String path = url.getPath();

//        System.out.println(path);

        if(path.contains(".jar"))
        {
            int ind = path.lastIndexOf("Sanchay.jar");

//            System.out.println(path.substring(0, ind - 5));
//            System.out.println(path.substring(0, ind));

            if(path.contains("dist/Sanchay.jar"))
                path = path.substring(0, ind - 5);
            else if(path.contains("lib/Sanchay.jar"))
                path = path.substring(0, ind - 4);
            else
                path = path.substring(0, ind);
        }
        else
        {
            int ind = path.lastIndexOf("Sanchay/");

             path = path.substring(0, ind + 7);

//             System.out.println(path);
        }

//        path = path.substring(0, path.length() - className.length() + 1);
        
        SANCHAY_HOME = path.replaceAll("file:", "");

//        System.out.println(SANCHAY_HOME);

//        KeyValueProperties props = getProperties();
//
//        return props.getPropertyValue(sanchayResourceBundle.getString("SANCHAY_HOME"));
        return SANCHAY_HOME;
    }

//    public static void setHomeDirectory(String d)
//    {
//        SANCHAY_HOME = d;
////        KeyValueProperties props = getProperties();
////
////        props.addProperty(sanchayResourceBundle.getString("SANCHAY_HOME"), d);
//    }

    public static String getWorkspaceDirectory()
    {
        KeyValueProperties props = getProperties();

        return props.getPropertyValue(sanchayResourceBundle.getString("WORKSPACE"));
    }

    public static void setWorkspaceDirectory(String d)
    {
        KeyValueProperties props = getProperties();

        props.addProperty(sanchayResourceBundle.getString("WORKSPACE"), d);
    }

    public static ResourceBundle getResourceBundle()
    {
        if(sanchayResourceBundle == null)
            sanchayResourceBundle = ResourceBundle.getBundle("sanchay");

        return sanchayResourceBundle;
    }

    public static String getIntlString(String key)
    {
        if(sanchayResourceBundle == null)
            sanchayResourceBundle = ResourceBundle.getBundle("sanchay");

        if(!sanchayResourceBundle.containsKey(key))
            return key;

        return sanchayResourceBundle.getString(key);
    }

    public static String resolveRelativePath(String path)
    {
        return resolveRelativePath(path, true);
    }

    public static String resolveRelativePath(String path, boolean writable)
    {
        if((new File(path)).isAbsolute())
            return path;

        USER_HOME = System.getProperty("user.home");

        File p = new File(USER_HOME, ".sanchay");

        if(!p.exists())
        {
            p.mkdir();
        }

        if(!p.isDirectory())
        {
            if(p.isFile())
                p.delete();
            
            p.mkdir();
        }

        File f = new File(p, path);

        if(writable && !f.canWrite())
            f.getParentFile().mkdirs();

        if((!writable && !f.canRead()) || (writable && !f.canWrite()))
        {
            p = new File(getHomeDirectory());
            f = new File(p, path);
        }

        if((!writable && !f.canRead()) || (writable && !f.canWrite()))
            return (new File(path)).getAbsolutePath();

//        System.out.println(f.getAbsolutePath());
        
        return f.getAbsolutePath();
    }

    public static boolean isRelativePath(String path)
    {
        File fpath = new File(path);

        return !fpath.getAbsolutePath().equals(path);
    }

    public static void main(String args[])
    {
        GlobalProperties.resolveRelativePath("props/fs-schema.txt");
    }
}
