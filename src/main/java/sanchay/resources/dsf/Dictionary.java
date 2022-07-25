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
public class Dictionary {

    //private config
    private FieldSet fieldset;
    private Vector entries;
    
    private boolean is_sorted;
    private boolean asc_sort;
    private String sort_key;
    private String index_key;
    private boolean is_indexed;
    
    private Hashtable index;

    /**
     * 
     */
    public Dictionary() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void clear()
    {
        //clear config
        fieldset.clear();
        entries.removeAllElements();
        
        is_sorted = false;
        asc_sort = true;
        sort_key = null;
        index_key = null;
        is_indexed = false;
        
        index.clear();
    }
    
    public void configure(/*String data_file, String config_file, String mode (Simple or eXtended)*/)
    {
        
    }

    private void configureS(/*String data_file, String config_file*/)
    {
        
    }
    
    private Entry readEntryS(/*Vector lines, int linenum*/)
    {
        return null;
    }
    
    private Sense readSenseS(/*Vector lines, int linenum*/)
    {
        return null;
    }
    
    private FieldSet readFieldSetS(/*Vector lines, int linenum*/)
    {
        return null;
    }

    private void configureX(/*String data_file, String config_file*/)
    {
        
    }
    
    private Entry readEntryX(/*Vector lines, int linenum*/)
    {
        return null;
    }
    
    private Sense readSenseX(/*Vector lines, int linenum*/)
    {
        return null;
    }
    
    private FieldSet readFieldSetX(/*Vector lines, int linenum*/)
    {
        return null;
    }
    
    private String addEscapes(String ptn)
    {
        return ptn;
    }
    
//    private selectConfig
//    
//    private FieldSetConfig
//    
//    private FieldConfig
    
//	private SubFieldConfig
    
//    private AltValueConfig
    
//    private CWordConfig
    
//    private addInfoConfig

//    public getConfig
    
//  public setConfig
   
    public FieldSet getFieldSet()
    {
        return fieldset;
    }
    
    public void setFieldSet(FieldSet fs)
    {
        fieldset = fs;
    }
    
    public String getSortKey()
    {
        return sort_key;
    }
    
    public void setSortKey(String sk)
    {
        sort_key = sk;
    }
    
    public String getIndexKey()
    {
        return index_key;
    }
    
    public void setIndexKey(String sk)
    {
        index_key = sk;
    }
    
    public int countEntries()
    {
        return entries.size();
    }
    
    public Entry getEntryByIndex(int ind)
    {
        return (Entry) entries.get(ind);
    }
    
    public Enumeration getEntryKeys()
    {
        return index.keys();
    }

//	Returns an array of array of matching entries
//	A regular expression can be given instead of a specific key, with mode as regex
    public Vector getEntriesByKey(/*key, mode*/)
    {
        Vector ret = new Vector();
        
        return ret;
    }
    
    public void modifyEntry(int ind, Entry e)
    {
        entries.setElementAt(e, ind);
    }
    
    public int addEntry(Entry e)
    {
        entries.add(e);
        return entries.size(); 
    }
    
    public Entry removeEntry(int ind)
    {
        return (Entry) entries.remove(ind);
    }
    
    public Vector getMatchingElements(MatchConditions m)
    {
        Vector ret = new Vector();
        
        return ret;
    }
    
    public static void main(String[] args)
    {
    }
}
