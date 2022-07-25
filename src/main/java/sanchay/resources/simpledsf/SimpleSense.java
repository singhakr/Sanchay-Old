/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.resources.simpledsf;

import java.io.*;
import java.util.Vector;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleSense {
    
    //private config
    private SimpleFieldSet fieldset;

    /**
     * 
     */
    public SimpleSense() {
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
    
    public SimpleFieldSet getFieldSet()
    {
        return fieldset;
    }
    
    public void setFieldSet(SimpleFieldSet fs)
    {
        fieldset = fs;
    }
    
//    public Vector matches(MatchConditions m)
//    {
//        Vector ret = new Vector();
//        
//        return ret;
//    }
   
    public String makeString()
    {
        String sval = "";
        
        return sval;
    }
    
    public void print(PrintStream ps)
    {
        ps.print(makeString());
    }
}
