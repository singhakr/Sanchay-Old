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
public class SubField {
    
    private Vector altvals;

    /**
     * 
     */
    public SubField() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        altvals.removeAllElements();
    }

    public int countAltVals()
    {
        return 0;
    }
    
    public AltValue getAltVal(int ind)
    {
        return (AltValue) altvals.get(ind);
    }
    
    public void modifyAltVal(int ind, AltValue av)
    {
        altvals.setElementAt(av, ind);
    }
    
    public void addAltVal(AltValue av)
    {
        altvals.add(av);
    }
    
    public AltValue removeAltVal(int ind)
    {
        return (AltValue) altvals.remove(ind);
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
