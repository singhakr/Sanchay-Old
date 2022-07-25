/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.resources.dsf;

import java.io.*;
import java.util.Vector;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Sense {
    
    //private config
    private FieldSet fieldset;

    /**
     * 
     */
    public Sense() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        //clear config;
        fieldset.clear();
    }
    
/*
 	public getConfig
 	public setConfig
*/
    
    public FieldSet getFieldSet()
    {
        return fieldset;
    }
    
    public void setFieldSet(FieldSet fs)
    {
        fieldset = fs;
    }
    
    public Vector matches(MatchConditions m)
    {
        Vector ret = new Vector();
        
        return ret;
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
