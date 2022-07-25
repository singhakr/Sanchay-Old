/*
 * Created on Aug 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.properties;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sanchay.resources.*;
import sanchay.table.SanchayTableModel;
import sanchay.text.TextNormalizer;
import sanchay.util.UtilityFunctions;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertyTokens extends ResourceImpl implements Serializable, SanchayProperties, SanchayDOMElement, Cloneable
{

    private String propertyName;

    /**
     * 
     */
    
    private Vector tokens;
    
    public PropertyTokens() {
        super();
        // TODO Auto-generated constructor stub
        tokens = new Vector(0, 5);
        
        setPropertyName("PropertyTokens");
    }
    
    public PropertyTokens(int size) {
        super();
        // TODO Auto-generated constructor stub
        tokens = new Vector(size);
        
        setPropertyName("PropertyTokens");
    }
    
    public PropertyTokens(int size, int growSize) {
        super();
        // TODO Auto-generated constructor stub
        tokens = new Vector(size, growSize);
        
        setPropertyName("PropertyTokens");
    }
    
    public void makeUnique()
    {
        Hashtable unique = new Hashtable(countTokens()/2, 10);
        
        int count = countTokens();
        
        for (int i = 0; i < count; i++)
        {
            unique.put(getToken(i), new Boolean(true));
        }
        
        removeAllTokens();
        
        Enumeration enm = unique.keys();
        
        while(enm.hasMoreElements())
        {
            String k = (String) enm.nextElement();
            addToken(k);
        }
    }

    public PropertyTokens(String propFile, String cs) throws FileNotFoundException, IOException
    {
	super(propFile, cs);
	
        tokens = new Vector(0, 5);
        
        setPropertyName("PropertyTokens");

        read(propFile, cs);
    }
    
    public String getPropertyName()
    {
        return propertyName;
    }
    
    public void setPropertyName(String n)
    {
        propertyName = n;
    }

    public int countTokens()
    {
        return tokens.size();
    }

    public void addToken(String t)
    {
        tokens.add(t);
    }

    public void insertToken(String t, int index)
    {
        tokens.insertElementAt(t, index);
    }

    public void addTokens(Collection c)
    {
        tokens.addAll(c);
    }
    
    public int addAllProperties(PropertyTokens pt)
    {
        tokens.addAll(pt.tokens);
        return tokens.size();
    }

    public void removeToken(int index)
    {
        tokens.remove(index);
    }

    public void removeToken(String k) {
        int count = tokens.size();
        
        for(int i = 0; i < count; i++)
        {
            if(getToken(i).equals(k))
            {
                removeToken(i);
                break;
            }
        }
    }

    public void removeAllTokens()
    {
        tokens.removeAllElements();
    }

    public String getToken(int index)
    {
        return (String) tokens.get(index);
    }

    public Vector getCopyOfTokens()
    {
        return new Vector(tokens);
    }

    public Vector getTypes()
    {
        Hashtable types = new Hashtable(0, 5);
        int count = tokens.size();
        
        for(int i = 0; i < count; i++)
            types.put(getToken(i), new Integer(1));
        
        Set kset = types.keySet();
        Vector ret = new Vector(kset);
        
        return ret;
    }

    public int findToken(String t)
    {
        return tokens.indexOf(t);
    }

    public void modifyToken(String t, int index)
    {
        tokens.setElementAt(t,index);
    }

    public void fill(int size, Object obj)
    {
        tokens.setSize(size);
        Collections.fill(tokens, obj);
    }

    /**
     * Reads from a file with one token on a line.
     */
    public int read() throws FileNotFoundException, IOException
    {
	return read(filePath, charset);
    }
    
    public int read(String f, String cs) throws FileNotFoundException, IOException
    {
        clear();

        BufferedReader lnReader = null;
        
        if(!cs.equals(""))
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), cs));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), sanchay.GlobalProperties.getIntlString("UTF-8")));

        String line;
        String splitstr[] = new String[2];
        
        while((line = lnReader.readLine()) != null)
        {
//            if((!line.startsWith("#")) && line.equals("") == false)
            if((!line.startsWith("#")) && line != "")
            {
                if(line.startsWith(sanchay.GlobalProperties.getIntlString("Column_Names::")))
                {
                    splitstr = line.split("::", 2);

                    if(splitstr.length == 2 && splitstr[1].equals("") == false)
                        setPropertyName(splitstr[1]);
                }
                else if(line.startsWith(sanchay.GlobalProperties.getIntlString("Column_Count::")) == false)
                    addToken(line);
            }
        }
        
        return 0;
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

        PropertyTokens ptkn = null;
        String ptName = "";

        while((line = lnReader.readLine()) != null)
        {
            if((!line.startsWith("#")) && line.equals("") == false)
            {
                if(line.startsWith(sanchay.GlobalProperties.getIntlString("PropertyTokensBegin::")))
                {
                    splitstr = line.split("::");
                    ptName = splitstr[1];

                    ptkn = new PropertyTokens();
                }
                else if(line.startsWith(sanchay.GlobalProperties.getIntlString("PropertyTokensEnd")))
                {
                    if(ptName.equals("") == false && ptkn != null)
                    {
                        ht.put(ptName, ptkn);
        
                        ptName = "";
                        ptkn = null;
                    }
                    else
                        throw new IOException();
                }
                else if(line.startsWith(sanchay.GlobalProperties.getIntlString("Column_Names::")))
                {
		    ptkn.propertyName = line;
		}
                else
                {
                    ptkn.addToken(line);
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

    public static void saveMany(LinkedHashMap ptokens, String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printMany(ptokens, ps);
    }
    
    public static PropertyTokens getPropertyTokens(DefaultComboBoxModel cbm)
    {
        PropertyTokens pt = new PropertyTokens();
        
        int count = cbm.getSize();
        
        for(int i = 0; i < count; i++)
        {
            pt.addToken((String) cbm.getElementAt(i));
        }
        
        return pt;
    }

    public static DefaultComboBoxModel getListModel(PropertyTokens pt)
    {
        DefaultComboBoxModel cbm = new DefaultComboBoxModel();

        int count = pt.countTokens();

        for(int i = 0; i < count; i++)
        {
            cbm.addElement(pt.getToken(i));
        }

        return cbm;
    }
  
    public void print(PrintStream ps)
    {
	if(getPropertyName().equals("") == false)
	    ps.println(sanchay.GlobalProperties.getIntlString("Column_Names::") + getPropertyName());

        for(int i = 0; i < countTokens(); i++)
            ps.println(getToken(i));
    }

    public static void printMany(LinkedHashMap ptokens, PrintStream ps)
    {
        Vector kvec = new Vector(ptokens.keySet());
        Collections.sort(kvec);
        
        int count = kvec.size();
        
        for(int i = 0; i < count; i++)
        {
            String ptName = (String) kvec.get(i);
            PropertyTokens ptoken = (PropertyTokens) ptokens.get(ptName);
            
            ps.println(sanchay.GlobalProperties.getIntlString("PropertyTokensBegin::") + ptName);
	    ptoken.print(ps);
            ps.println(sanchay.GlobalProperties.getIntlString("PropertyTokensEnd"));
        }
    }

    public static void printManyXML(LinkedHashMap ptokens, PrintStream ps)
    {
        Vector kvec = new Vector(ptokens.keySet());
        Collections.sort(kvec);

        DOMElement domElementMany = new DOMElement(sanchay.GlobalProperties.getIntlString("PropertyTokensMany"));
        
        int count = kvec.size();
        
        for(int i = 0; i < count; i++)
        {
            String ptName = (String) kvec.get(i);
            PropertyTokens ptoken = (PropertyTokens) ptokens.get(ptName);
            
            DOMElement domElement = ptoken.getDOMElement();
            DOMAttribute attribPT = new DOMAttribute(domElement, new org.dom4j.QName(sanchay.GlobalProperties.getIntlString("name")), ptName);
            
            domElementMany.add(domElement);
        }
        
        ps.print(domElementMany.asXML());
    }

    public static KeyValueProperties convertToKVP(PropertyTokens pt)
    {
        int tcount = pt.countTokens();
        
        KeyValueProperties kvp = new KeyValueProperties(tcount);

        for (int i = 0; i < tcount; i++)
        {
            kvp.addProperty(pt.getToken(i), "");
        }
        
        return kvp;
    }

    public static SanchayTableModel convertToSanchayTableModel(PropertyTokens pt)
    {
        int tcount = pt.countTokens();
        
        SanchayTableModel table = new SanchayTableModel(tcount, 1);

        for (int i = 0; i < tcount; i++)
        {
            table.setValueAt(pt.getToken(i), i, 0);
        }
        
        return table;
    }

    public static PropertyTokens convertToPropertyTokens(SanchayTableModel stm)
    {
        PropertyTokens pt = new PropertyTokens();
        pt.setCharset(stm.getCharset());
        
        int rcount = stm.getRowCount();
        
        for (int i = 0; i < rcount; i++)
        {
            pt.addToken((String) stm.getValueAt(i, 0));
        }
        
        return pt;
    }

    public Object clone() throws CloneNotSupportedException// copyFS($fs)
    {
        PropertyTokens obj = (PropertyTokens) super.clone();
		
	obj.tokens = new Vector(countTokens());
		
        for(int i = 0; i < countTokens(); i++)
            obj.tokens.add(getToken(i));

	return obj;
    }
	
    public void clear()
    {
    	tokens.removeAllElements();
    }

    // For words to be transliterated
    public PropertyTokens getSpacedOutStrings(PropertyTokens inPT)
    {
        PropertyTokens outPT = new PropertyTokens();

        int count = inPT.countTokens();

        for (int i = 0; i < count; i++)
        {
            outPT.addToken(UtilityFunctions.getSpacedOutString(inPT.getToken(i)));
        }

        return outPT;
    }

    public static int getIntersectionSize(PropertyTokens pt1, PropertyTokens pt2)
    {
        return getIntersection(pt1, pt2).countTokens();
    }

    public static PropertyTokens getIntersection(PropertyTokens pt1, PropertyTokens pt2)
    {
        KeyValueProperties listKVP1 = convertToKVP(pt1);
        KeyValueProperties listKVP2 = convertToKVP(pt2);

        KeyValueProperties intersectionKVP = KeyValueProperties.getIntersectionOfKeys(listKVP1, listKVP2);

        PropertyTokens intersectionPT = new PropertyTokens();

        Iterator itr = intersectionKVP.getPropertyKeys();

        while(itr.hasNext())
        {
            intersectionPT.addToken((String) itr.next());
        }

        return intersectionPT;
    }

//    public DOMElement getDOMElement() {
//        DOMElement domElement = new DOMElement("PropertyTokens");
//
//        DOMAttribute attribPropName = new DOMAttribute(domElement, new org.dom4j.QName("propertyName"), propertyName);
//        
//        int tcount = tokens.size();
//        for (int i = 0; i < tcount; i++)
//        {
//            String token = getToken(i);
//            DOMElement tokenElement = new DOMElement("PropertyToken");
//            DOMAttribute attribToken = new DOMAttribute(tokenElement, new org.dom4j.QName("token"), token);
//            domElement.add(tokenElement);
//        }
//        
//        return domElement;
//    }
    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement(sanchay.GlobalProperties.getIntlString("PropertyTokens"));
        
        
        

        //DOMAttribute attribPropName = new DOMAttribute(domElement, new org.dom4j.QName("propertyName"), propertyName);
        
        int tcount = tokens.size();
        for (int i = 0; i < tcount; i++)
        {
            String token = getToken(i);
            DOMElement tokenElement = new DOMElement(sanchay.GlobalProperties.getIntlString("PropertyToken"));
           // DOMAttribute attribToken = new DOMAttribute(tokenElement, new org.dom4j.QName("token"), token);
            tokenElement.addAttribute(sanchay.GlobalProperties.getIntlString("token"), token);
            domElement.add(tokenElement);
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

//    public void readXML(Element domElement) {
//        propertyName = domElement.getAttribute("propertyName");
//        
//        NodeList elements = domElement.getElementsByTagName("PropertyToken");
//        int count = elements.getLength();
//        
//        for (int i = 0; i < count; i++)
//        {
//            org.w3c.dom.Element kvElement = (org.w3c.dom.Element) elements.item(i);
//            String token = kvElement.getAttribute("token");
//            
//            addToken(token);
//        }        
//    }
    public void readXML(Element domElement) {
        //propertyName = domElement.getAttribute("propertyName");
        
        NodeList elements = domElement.getElementsByTagName(sanchay.GlobalProperties.getIntlString("PropertyToken"));
        int count = elements.getLength();
     //   System.out.println("I am adding tokens here: ");
        for (int i = 0; i < count; i++)
        {
            org.w3c.dom.Element kvElement = (org.w3c.dom.Element) elements.item(i);
            String token = kvElement.getAttribute(sanchay.GlobalProperties.getIntlString("token"));
       //     System.out.println(token);
            
            addToken(token);
        }        
    }

    public static void main(String[] args) {
        PropertyTokens pt = new PropertyTokens();
//        TextNormalizer textNormalizer = new TextNormalizer("tel::utf8", "UTF-8", "", "", false);
//
        PropertyTokens ptOut = new PropertyTokens();

        KeyValueProperties kvp = new KeyValueProperties();

        try
        {
            pt.read("/home/anil/sanchay/Sanchay/data/transliteration/translit-test-data-hindi.txt", "UTF-8");

            ptOut = pt.getSpacedOutStrings(pt);

            ptOut.save("/home/anil/sanchay/Sanchay/data/transliteration/translit-test-data-hindi-spaced-out.txt", "UTF-8");
//            kvp.read("/home/anil/joro/data/two-telugu-books-1016-1018-word-list-3.txt", sanchay.GlobalProperties.getIntlString("UTF-8"));

//            int count = pt.countTokens();
//
//            for (int i = 0; i < count; i++)
//            {
//                String string = pt.getToken(i);
//
//                if(textNormalizer.isPossiblyValidWord(string))
//                {
//                    ptOut.addToken(string);
//                }
//            }

            kvp.save("/home/anil/joro/data/two-telugu-books-1016-1018-word-list-4.txt", sanchay.GlobalProperties.getIntlString("UTF-8"));

        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(PropertyTokens.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(PropertyTokens.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
