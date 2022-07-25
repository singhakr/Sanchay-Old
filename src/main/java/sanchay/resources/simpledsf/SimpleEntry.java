/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.resources.simpledsf;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;

import sanchay.tree.*;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleEntry extends SanchayMutableTreeNode
        implements MutableTreeNode, Serializable {

    //private config
    private SimpleFieldSet fieldset;
    private Vector senses;    

    /**
     * 
     */
    public SimpleEntry() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        //clear config
        fieldset.clear();
        senses.removeAllElements();
    }

//  public getConfig
  
//public setConfig

    public SimpleFieldSet getFieldSet()
    {
        return fieldset;
    }
    
    public void setFieldSet(SimpleFieldSet fs)
    {
        fieldset = fs;
    }
    
    public int countSenses()
    {
        return senses.size();
    }
    
    public SimpleSense getSense(int ind)
    {
        return (SimpleSense) senses.get(ind);
    }
    
    public void modifySense(int ind, SimpleSense s)
    {
        senses.setElementAt(s, ind);
    }
    
    public void addSense(SimpleSense s)
    {
        senses.add(s);
    }
    
    public SimpleSense removeSense(int ind)
    {
        return (SimpleSense) senses.remove(ind);
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
    
    public static void main(String[] args) {
    }
}
