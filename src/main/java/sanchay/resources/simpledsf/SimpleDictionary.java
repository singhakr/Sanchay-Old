/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.resources.simpledsf;

import java.io.*;
import java.util.*;
import sanchay.properties.PropertiesManager;
import sanchay.resources.Resource;
import sanchay.resources.ResourceImpl;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleDictionary extends ResourceImpl implements Resource {

    //private config
    private PropertiesManager propman;
    
    private SimpleFieldSet fieldset;
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
    public SimpleDictionary()
    {
        super();
    }

    public SimpleDictionary(String fp, String cs)
    {
        super(fp, cs);
    }

    public SimpleDictionary(String fp, String cs, String lang, String nm)
    {
        super(fp, cs, lang, nm);
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
   
    public SimpleFieldSet getFieldSet()
    {
        return fieldset;
    }
    
    public void setFieldSet(SimpleFieldSet fs)
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
    
    public SimpleEntry getEntryByIndex(int ind)
    {
        return (SimpleEntry) entries.get(ind);
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
    
    public void modifyEntry(int ind, SimpleEntry e)
    {
        entries.setElementAt(e, ind);
    }
    
    public int addEntry(SimpleEntry e)
    {
        entries.add(e);
        return entries.size(); 
    }
    
    public SimpleEntry removeEntry(int ind)
    {
        return (SimpleEntry) entries.remove(ind);
    }
//    
//    public Vector getMatchingElements(MatchConditions m)
//    {
//        Vector ret = new Vector();
//        
//        return ret;
//    }

    public int read(String f, String charset) throws FileNotFoundException, IOException
    {
        return -1;
        
    }
    
    public int save(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        return -1;
    }
    
    public static void main(String[] args)
    {
    }
}
