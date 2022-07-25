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
public class AltValue {
    
    private Vector cwords;
    private Hashtable addinfo;
    
    /**
     * 
     */
    public AltValue() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        cwords.removeAllElements();
        addinfo.clear();
    }
    
    public int countCWords()
    {
        return 0;
    }
    
    public CWord getCWord(int ind)
    {
        return (CWord) cwords.get(ind);
    }
    
    public void modifyCWord(int ind, CWord cw)
    {
        cwords.setElementAt(cw, ind);
    }
    
    public void addCWord(CWord cw)
    {
        cwords.add(cw);
    }
    
    public CWord removeCWord(int ind)
    {
        return (CWord) cwords.remove(ind);
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
