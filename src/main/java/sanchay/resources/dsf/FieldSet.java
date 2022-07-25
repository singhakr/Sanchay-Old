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
public class FieldSet {

    private Hashtable fields;

    /**
     * 
     */
    public FieldSet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        //clear config;
        fields.clear();
    }
    
    public int countFields()
    {
        return fields.size();
    }
    
    public Enumeration getFieldKeys()
    {
        return fields.keys();
    }
    
    public Field getField(String key)
    {
        return (Field) fields.get(key);
    }
    
    public void setField(String key, Field val)
    {
        fields.put(key, val);
    }
    
    public Field removeField(String key)
    {
        return (Field) fields.remove(key);
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
