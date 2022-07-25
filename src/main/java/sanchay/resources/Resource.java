/*
 * Resource.java
 *
 * Created on November 3, 2005, 6:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.resources;

import java.io.*;

import sanchay.common.*;

/**
 *
 *  @author Anil Kumar Singh
 * Resources can be used locally as well as remotely. For remote use (say, for sharing),
 * the corresponding remote object (server) will be used. Otherwise a serializable
 * object of a class implementing this interface will be used.
 */

public interface Resource extends SanchaySerializable {
    
    public String getName();
    public void setName(String nm);

    public String getLangEnc();
    public void setLangEnc(String langEnc);

    public String getFilePath();
    public void setFilePath(String fp);
    public String getCharset();
    public void setCharset(String c);
    
    public int read() throws FileNotFoundException, IOException;
    public int read(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException;;
    public int save() throws FileNotFoundException, IOException;
    public int save(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException;
}
