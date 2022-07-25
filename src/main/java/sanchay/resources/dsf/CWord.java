/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.resources.dsf;

import java.io.*;
import java.util.*;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CWord {
    
    private String word;
    private Hashtable addinfo;

    /**
     * 
     */
    public CWord() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        word = null;
        addinfo.clear();
    }
    
    public String getWord()
    {
        return word;
    }
    
    public void getWord(String w)
    {
        word = w;
    }
    
    public int countAddInfos()
    {
        return addinfo.size();
    }
    
    public Enumeration getAddInfoKeys()
    {
        return addinfo.keys();
    }
    
    public String getAddInfo(String key)
    {
        return (String) addinfo.get(key);
    }
    
    public void setAddInfo(String key, String val)
    {
        addinfo.put(key, val);
    }
    
    public String removeAddInfo(String key)
    {
        return (String) addinfo.remove(key);
    }
    
    public String makeString(/*config, global-separators*/)
    {
        String sval = "";
        
        return sval;
    }
    
    public void print(PrintStream ps/*, config, global-separators*/)
    {
        
    }

    public static void main(String[] args) {
    }
}
