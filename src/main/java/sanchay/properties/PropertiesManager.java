/*
 * Created on Aug 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.properties;

import java.io.*;
import java.util.*;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import sanchay.common.types.PropertyType;
import sanchay.resources.aggregate.AggregateResourceImpl;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertiesManager extends AggregateResourceImpl implements Serializable, SanchayProperties, SanchayDOMElement
{
    java.util.ResourceBundle bundle = sanchay.GlobalProperties.getResourceBundle(); // NOI18N

    /**
     * 
     */

    // Paths and other configuration information in taskProps
    
    // Properties objects corresponding to the configuration
    private Hashtable proptokens;
    private Hashtable multiproptokens;
    private Hashtable kvprops;
    private Hashtable multikvprops;
    private Hashtable proptables;
    private Hashtable multiproptables;
    private Hashtable propmans;
    private Hashtable multipropmans;
    
    public PropertiesManager() {
        super();
        // TODO Auto-generated constructor stub

        proptokens = new Hashtable(0, 5);
        multiproptokens = new Hashtable(0, 5);
        kvprops = new Hashtable(0, 5);
        multikvprops = new Hashtable(0, 5);
        proptables = new Hashtable(0, 5);
        multiproptables = new Hashtable(0, 5);
        propmans = new Hashtable(0, 5);
        multipropmans = new Hashtable(0, 5);

        taskProps = new PropertiesTable();
    }

    public PropertiesManager(String propFile, String cs) throws FileNotFoundException, IOException
    {
	super(propFile, cs);
        
        proptokens = new Hashtable(0, 5);
        multiproptokens = new Hashtable(0, 5);
        kvprops = new Hashtable(0, 5);
        multikvprops = new Hashtable(0, 5);
        proptables = new Hashtable(0, 5);
        multiproptables = new Hashtable(0, 5);
        propmans = new Hashtable(0, 5);
        multipropmans = new Hashtable(0, 5);

        taskProps = new PropertiesTable();

        read(propFile, cs);
    }
    
    private Hashtable getPCHashtable(PropertyType pt)
    {
        if(pt == PropertyType.PROPERTY_TOKENS)
            return proptokens;
	else if(pt == PropertyType.MULTI_PROPERTY_TOKENS)
            return multiproptokens;
        else if(pt == PropertyType.KEY_VALUE_PROPERTIES)
            return kvprops;
        else if(pt == PropertyType.MULTI_KEY_VALUE_PROPERTIES)
            return multikvprops;
        else if(pt == PropertyType.PROPERTY_TABLE)
            return proptables;
        else if(pt == PropertyType.MULTI_PROPERTY_TABLE)
            return multiproptables;
        else if(pt == PropertyType.MULTI_PROPERTIES_MANAGER)
            return multipropmans;
        else if(pt == PropertyType.PROPERTIES_MANAGER)
            return propmans;
        
        return null;
    }
    
    public int countPropertyContainers(PropertyType pt)
    {
        Hashtable ht = getPCHashtable(pt);
        
        if(ht != null)
            return ht.size();
        
        return -1;
    }
    
    public Enumeration getPropertyContainerKeys(PropertyType pt)
    {
        Hashtable ht = getPCHashtable(pt);
        
        if(ht != null)
            return ht.keys();
        
        return null;
    }
    
    public SanchayProperties getPropertyContainer(String p /* property key */, PropertyType pt)
    {
        Hashtable ht = getPCHashtable(pt);
        
        if(ht != null)
            return (SanchayProperties) ht.get(p);
        
        return null;
    }
    
    public int addPropertyContainer(String p /* property key */, SanchayProperties v /*property value */, PropertyType pt)
    {
        Hashtable ht = getPCHashtable(pt);
        
        if(ht != null)
        {
            ht.put(p, v);
            return ht.size();
        }

        return -1;
    }
    
    public int addPropertyContainers(Hashtable pcs, PropertyType pt)
    {
        Hashtable ht = getPCHashtable(pt);
        
        if(ht != null)
        {
            ht.putAll(pcs);
            return ht.size();
        }

        return -1;
    }
    
    public SanchayProperties removePropertyContainer(String p /* property key */, PropertyType pt)
    {
        Hashtable ht = getPCHashtable(pt);
        
        if(ht != null)
        {
            return (SanchayProperties) ht.remove(p);
        }

        return null;
    }
    
    public void savePropertyContainer(String p /* property key */, PropertyType pt) throws Exception
    {
        String colNames[] = {"PCType", "PCClass"};
        String vals[] = {pt.toString(), p};
        String outColNames[] = {"FilePath", "Charset"};
        
        Vector outVals = ((PropertiesTable) taskProps).getValuesAnd(colNames, vals, outColNames);
        
        if(outVals.size() != 1)
            throw new Exception();
        
        String path = (String) ((Vector) outVals.get(0)).get(0);
        String cs = (String) ((Vector) outVals.get(0)).get(1);
        
        SanchayProperties spc = getPropertyContainer(p, pt);
        spc.save(path, cs);
    }
    
    /**
     * Reads from a file with five columns, separated by a tab.
     * First column: PCType : property container type 
     * Second column: PCClass : property container class (key)
     * Third column: FilePath : file path
     * Fourth column: Charset : charset
     * Fifth column: ColNumber : number of columns for PropertyTable
     */
    public int read() throws FileNotFoundException, IOException
    {
	return read(filePath, charset);
    }
    
    public int read(String f, String cs) throws FileNotFoundException, IOException
    {
        clear();
        
        int ret = taskProps.read(f, cs);

        String pct = ""; // type
        String pcc = ""; // class/key
        PropertyType pt = null; 
        String path = "";
        String ccs = "";
        int cols = 0;
        SanchayProperties sp = null;
        
        for(int i = 0; i < ((PropertiesTable) taskProps).getRowCount(); i++)
        {
            pct = (String) ((PropertiesTable) taskProps).getValueAt(i, 0);
            pcc = (String) ((PropertiesTable) taskProps).getValueAt(i, 1);
            pt = (PropertyType) PropertyType.findFromId(pct);
            path = (String) ((PropertiesTable) taskProps).getValueAt(i, 2);
            ccs = (String) ((PropertiesTable) taskProps).getValueAt(i, 3);
            cols = Integer.parseInt((String) ((PropertiesTable) taskProps).getValueAt(i, 4));
            
            if(pt == PropertyType.PROPERTY_TOKENS)
                sp = new PropertyTokens(path, ccs);
            else if(pt == PropertyType.MULTI_PROPERTY_TOKENS)
                sp = new MultiPropertyTokens(path, ccs);
            else if(pt == PropertyType.KEY_VALUE_PROPERTIES)
                sp = new KeyValueProperties(path, ccs);
            else if(pt == PropertyType.MULTI_KEY_VALUE_PROPERTIES)
                sp = new MultiKeyValueProperties(path, ccs);
            else if(pt == PropertyType.PROPERTY_TABLE)
                sp = new PropertiesTable(path, ccs);
            else if(pt == PropertyType.MULTI_PROPERTY_TABLE)
                sp = new MultiPropertiesTable(path, ccs);
            else if(pt == PropertyType.PROPERTIES_MANAGER)
                sp = new PropertiesManager(path, ccs);
            else if(pt == PropertyType.MULTI_PROPERTIES_MANAGER)
                sp = new MultiPropertiesManager(path, ccs);

            addPropertyContainer(pcc, sp, pt);
        }
        
        return ret;
    }
    
    public static LinkedHashMap readMany(String f, String charset) throws FileNotFoundException, IOException
    {
        LinkedHashMap ht = new LinkedHashMap(0, 5);
        
        BufferedReader lnReader = null;
        
        if(!charset.equals(""))
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), sanchay.GlobalProperties.getIntlString("UTF-8")));

        String line;
        String splitstr[] = null;

        PropertiesManager pm = null;
        String pmName = "";

        while((line = lnReader.readLine()) != null)
        {
            if((!line.startsWith("#")) && line.equals("") == false)
            {
                if(line.startsWith(sanchay.GlobalProperties.getIntlString("PropertiesManagerBegin::")))
                {
                    splitstr = line.split("::");
                    pmName = splitstr[1];

                    pm = new PropertiesManager();
                }
                else if(line.startsWith(sanchay.GlobalProperties.getIntlString("PropertiesManagerEnd")))
                {
                    if(pmName.equals("") == false && pm != null)
                    {
                        ht.put(pmName, pm);

			pmName = "";
                        pm = null;
                    }
                    else
                        throw new IOException();
                }
                else
                {
                    splitstr = line.split("\t");
                }
            }
        }
        
        return ht;
    }

    public int save() throws FileNotFoundException, UnsupportedEncodingException
    {
	return save(filePath, charset);
    }

    public int save(String f, String cs) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, cs);
        print(ps);
	
	return 0;
    }

    public static void saveMany(LinkedHashMap propmans, String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printMany(propmans, ps);
    }
    
    public void print(PrintStream ps)
    {
        ps.println("Constants for Properties Manager:\n");
        taskProps.print(ps);
        ps.println("#############################################");
        
        Enumeration enm = PropertyType.elements();
        
        while(enm.hasMoreElements())
        {
            PropertyType pt = (PropertyType) enm.nextElement();

            Enumeration enm1 = getPropertyContainerKeys(pt);
            
            if(enm1 == null)
                continue;
            
            while(enm1.hasMoreElements())
            {
                String key = (String) enm1.nextElement();
                SanchayProperties sp = getPropertyContainer(key, pt);
                ps.println("#" + pt + "::" + key);
                sp.print(ps);
                ps.println("#############################################");
            }
        }
    }

    public static void printMany(LinkedHashMap propmans, PrintStream ps)
    {
        Vector kvec = new Vector(propmans.keySet());
        Collections.sort(kvec);
        
        int count = kvec.size();
        
        for(int i = 0; i < count; i++)
        {
            String tblName = (String) kvec.get(i);
            PropertiesManager pm = (PropertiesManager) propmans.get(tblName);
            
            ps.println(sanchay.GlobalProperties.getIntlString("PropertiesManagerBegin::") + tblName);
	    pm.print(ps);
            ps.println(sanchay.GlobalProperties.getIntlString("PropertiesManagerEnd"));
        }
    }

    public static void printManyXML(Hashtable propmans, PrintStream ps)
    {
        DOMElement domElementMany = new DOMElement(sanchay.GlobalProperties.getIntlString("MultiPropertiesManager"));

        Vector kvec = new Vector(propmans.keySet());
        Collections.sort(kvec);
        
        int count = kvec.size();
        
        for(int i = 0; i < count; i++)
        {
            String tblName = (String) kvec.get(i);
            PropertiesManager pm = (PropertiesManager) propmans.get(tblName);
            
            DOMElement domElement = pm.getDOMElement();
            DOMAttribute attribKVP = new DOMAttribute(domElement, new org.dom4j.QName(sanchay.GlobalProperties.getIntlString("name")), tblName);
            
            domElementMany.add(domElement);
        }
        
        ps.print(domElementMany.asXML());
    }

    public Object clone() throws CloneNotSupportedException// copyFS($fs)
    {
	PropertiesManager obj = null;

	String sfname = sanchay.GlobalProperties.getHomeDirectory() + "/" + "PropertiesManager-tmp.tmp";

	try {
	    FileOutputStream out = new FileOutputStream(sfname);
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(this);
	    os.flush();

	    FileInputStream in = new FileInputStream(sfname);
	    ObjectInputStream is = new ObjectInputStream(in);
	    obj = (PropertiesManager) is.readObject();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return obj;
    }

    public void clear()
    {
	proptokens.clear();
	kvprops.clear();
	proptables.clear();
	((PropertiesTable) taskProps).clear();
    }

//    public DOMElement getDOMElement() {
//        DOMElement domElement = new DOMElement("PropertiesManager");
//        
//        DOMElement domElementConst = new DOMElement("Constants");
//        domElementConst.add(((SanchayDOMElement) taskProps).getDOMElement());
//        
//        domElement.add(domElementConst);
//        
//        Enumeration enm = PropertyType.elements();
//        
//        while(enm.hasMoreElements())
//        {
//            PropertyType pt = (PropertyType) enm.nextElement();
//
//            Enumeration enm1 = getPropertyContainerKeys(pt);
//            
//            if(enm1 == null)
//                continue;
//            
//            while(enm1.hasMoreElements())
//            {
//                String key = (String) enm1.nextElement();
//                
////                DOMElement domElementProps = new DOMElement("SanchayProperties");
//                DOMElement domElementProps = new DOMElement(pt.toString());
////                DOMAttribute attribType = new DOMAttribute(domElementProps, new org.dom4j.QName("type"), pt.toString());
//                DOMAttribute attribKey = new DOMAttribute(domElementProps, new org.dom4j.QName("key"), key);
//                
//                SanchayProperties sp = getPropertyContainer(key, pt);
//                
//                domElementProps.add(((SanchayDOMElement) sp).getDOMElement());
//                domElement.add(domElementProps);
//            }            
//        }        
//        
//        return domElement;
//    }
    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement(sanchay.GlobalProperties.getIntlString("PropertiesManager"));
        
        DOMElement domElementConst = new DOMElement(sanchay.GlobalProperties.getIntlString("Constants"));
        
        domElementConst.add(((SanchayDOMElement) taskProps).getDOMElement());
        
        domElement.add(domElementConst);
        
        Enumeration enm = PropertyType.elements();
        
        while(enm.hasMoreElements())
        {
            PropertyType pt = (PropertyType) enm.nextElement();

            Enumeration enm1 = getPropertyContainerKeys(pt);
            
            if(enm1 == null)
                continue;
            
            while(enm1.hasMoreElements())
            {
                String key = (String) enm1.nextElement();
                

                DOMElement domElementProps = new DOMElement(pt.toString());

             //   DOMAttribute attribKey = new DOMAttribute(domElementProps, new org.dom4j.QName("key"), key);
               
                domElementProps.addAttribute(sanchay.GlobalProperties.getIntlString("key"), key);
                SanchayProperties sp = getPropertyContainer(key, pt);
                
                domElementProps.add(((SanchayDOMElement) sp).getDOMElement());
                domElement.add(domElementProps);
            }            
        }        
        
        return domElement;
    }

    public String getXML() {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void printXML(PrintStream ps) {
        ps.println(getXML());
    }

    public void readXML(Element domElement) {
    }

    public static void main(String[] args)
    {
        try {
            PropertiesManager pm = new PropertiesManager(sanchay.GlobalProperties.getHomeDirectory() + "/" + "workspace/server-props.txt", sanchay.GlobalProperties.getIntlString("UTF-8"));
//            pm.print(System.out);
//            PropertiesTable taskProps = (PropertiesTable) pm.getProperties();
            PropertiesTable users = (PropertiesTable) pm.getPropertyContainer(sanchay.GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
//			String cols[] = {"preeti", "user", "pr"};
//			users.addRow(cols);

//            String colNames[] = {"PCType", "PCClass"};
//            String vals[] = {"PropertyTable", "users"};
//            String outColNames[] = {"FilePath", "Charset"};

            String colNames[] = {sanchay.GlobalProperties.getIntlString("User"), "UserType"};
            String vals[] = {sanchay.GlobalProperties.getIntlString("anil"), sanchay.GlobalProperties.getIntlString("validator")};
            String outColNames[] = {"UserType", sanchay.GlobalProperties.getIntlString("Password")};
            String outVals[] = {sanchay.GlobalProperties.getIntlString("admin"), "a"};
            
//            Vector outVals = ((PropertiesTable) taskProps).getValuesAnd(colNames, vals, outColNames);
            Vector ret = users.removeRowsAnd(colNames, vals);
            
//            if(outVals.size() != 1)
//                throw new Exception();
//            
//            String path = (String) ((Vector) outVals.get(0)).get(0);
//            String cs = (String) ((Vector) outVals.get(0)).get(1);
            
//            users.save(path, cs);
            users.print(System.out);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
