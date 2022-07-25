/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.resources.dsf;

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
public class Entry extends SanchayMutableTreeNode
        implements MutableTreeNode, Serializable {

    //private config
    private FieldSet fieldset;
    private Vector senses;    

    /**
     * 
     */
    public Entry() {
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

    public FieldSet getFieldSet()
    {
        return fieldset;
    }
    
    public void setFieldSet(FieldSet fs)
    {
        fieldset = fs;
    }
    
    public int countSenses()
    {
        return senses.size();
    }
    
    public Sense getSense(int ind)
    {
        return (Sense) senses.get(ind);
    }
    
    public void modifySense(int ind, Sense s)
    {
        senses.setElementAt(s, ind);
    }
    
    public void addSense(Sense s)
    {
        senses.add(s);
    }
    
    public Sense removeSense(int ind)
    {
        return (Sense) senses.remove(ind);
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
