/*
 * ResourceImpl.java
 *
 * Created on November 4, 2005, 5:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.resources;

import java.io.*;

/**
 *
 *  @author Anil Kumar Singh
 */
public class ResourceImpl implements Resource, Cloneable
{
    protected String name;
    protected String langEnc;
    
    protected String filePath;
    protected String charset;
    
    /** Creates a new instance of ResourceImpl */
    public ResourceImpl() {
    }

    public ResourceImpl(String fp, String cs) {
	filePath = fp;
	charset = cs;
    }

    public ResourceImpl(String fp, String cs, String lang, String nm) {
	filePath = fp;
	charset = cs;
	
	langEnc = lang;
	name = nm;
    }
    
    public String getName()
    {
	return name;
    }
    
    public void setName(String nm)
    {
	name = nm;
    }
    
    public String getLangEnc()
    {
	return langEnc;
    }
    
    public void setLangEnc(String langEnc)
    {
    	this.langEnc = langEnc;
    }

    public String getFilePath()
    {
        return filePath;
    }
    
    public void setFilePath(String fp)
    {
        filePath = fp;
    }

    public String getCharset()
    {
        return charset;
    }

    public void setCharset(String cs)
    {
        charset = cs;
    }
    
    public int read() throws FileNotFoundException, IOException
    {
	if((new File(filePath).exists()))
	{
	    read(filePath, charset);
	    return 0;
	}
	
        return -1;
    }

    public int read(String f, String charset) throws FileNotFoundException, IOException
    {
        return -1;
    }
   
    public int save() throws FileNotFoundException, IOException
    {
	if((new File(filePath).exists()))
	{
	    save(filePath, charset);
	    return 0;
	}

	return -1;
    }

    public int save(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        return -1;
    }

    public Object clone() throws CloneNotSupportedException
    {
        ResourceImpl obj = (ResourceImpl) super.clone();

        obj.name = name;
        obj.langEnc = langEnc;
        obj.filePath = filePath;
        obj.charset = charset;
        
        return obj;
    }
}
