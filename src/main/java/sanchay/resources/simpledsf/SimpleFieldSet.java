/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.resources.simpledsf;

import java.io.*;
import java.util.*;
import javax.swing.table.TableModel;
import sanchay.table.SanchayTableModel;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleFieldSet extends SanchayTableModel {

    /**
     * 
     */
    public SimpleFieldSet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void readAndAddField(String str)
    {
        
    }
   
    public String makeString()
    {
        String sval = "";
        
        return sval;
    }
    
    public void print(PrintStream ps)
    {
        ps.print(makeString());
    }
    
//    public Vector matches(MatchConditions m)
//    {
//        Vector ret = new Vector();
//        
//        return ret;
//    }    
}
