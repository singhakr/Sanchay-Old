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
public class Field {
    
    private String name;
    private Hashtable subfields;
    private Vector seq;

    /**
     * 
     */
    public Field() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        //clear config
        name = null;
        subfields.clear();
        seq.removeAllElements();
    }

//  public getConfig
  
//public setConfig
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String n)
    {
        name = n;
    }
    
    public int countSubFields()
    {
        return subfields.size();
    }
    
    public Enumeration getSubFieldKeys()
    {
        return subfields.keys();
    }
    
    public SubField getSubField(String key)
    {
        return (SubField) subfields.get(key);
    }
    
    public SubField getSubField(int ind)
    {
        return null;
    }
    
//	Returns the SubField key at the given index    
    public String getSubFieldKey(int ind)
    {
        return null;
    }

//  Returns the index of the given SubField key
    public int GetSubFieldIndex(String key)
    {
        return -1;
    }
    
    public void modifySubField(String key, SubField val)
    {
        subfields.put(key, val);
    }
    
    public void modifySubField(int ind, SubField val)
    {
    }
    
    public int addSubField(String key, SubField val)
    {
        return -1;
    }
    
    public int insertSubField(int ind, String key, SubField val)
    {
        return -1;
    }
    
    public SubField removeSubField(String key)
    {
        return (SubField) subfields.remove(key);
    }
    
    public SubField removeSubField(int ind)
    {
        return null;
    }
    
    public String makeString(/*config, global-separators*/)
    {
        String sval = "";
        
        return sval;
    }
    
    public Vector matches(MatchConditions m)
    {
        Vector ret = new Vector();
        
        return ret;
    }
    
    public void print(PrintStream ps/*, config, global-separators*/)
    {
        
    }

    public static void main(String[] args) {
    }
}
