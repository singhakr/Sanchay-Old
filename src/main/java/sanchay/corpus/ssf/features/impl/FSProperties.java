/*
 * Created on Jun 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.features.impl;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FSProperties implements Serializable{

    /**
     * The class for loading, accessing and printing featrure structure
     * properties. These properties are of two kinds:
     * - Names of mandatory attributes
     * - Key value pairs for other properties such as separators etc.
     */

    private Vector mandatoryAttributes; // mandatory attribute names
    private Vector mandatoryAttribvals; // valid attribute values (as regexes)

    private Vector nonMandatoryAttributes;
    private Vector nonMandatoryAttribvals;
    
    private KeyValueProperties properties;
    protected KeyValueProperties psAttributes;
    protected KeyValueProperties dependencyAttributes;
    protected KeyValueProperties semanticAttributes;

    public FSProperties() {
        super();
        // TODO Auto-generated constructor stub
        mandatoryAttributes = new Vector (0, 5);
        mandatoryAttribvals = new Vector (0, 5);
        nonMandatoryAttributes = new Vector (0, 5);
        nonMandatoryAttribvals = new Vector (0, 5);
        properties = new KeyValueProperties();
        psAttributes = new KeyValueProperties();
        dependencyAttributes = new KeyValueProperties();
        semanticAttributes = new KeyValueProperties();
        /*DEFAULT KEYS - nodeStart, nodeEnd, defAttribSeparator, attribSeparator, attribOR, attribEquate */
    }

    public FSProperties(String maf /*Mandatory attribs file*/, String nmaf /*Mandatory attribs file*/,
            String pf /*props file*/, String paf /*PS attribs file*/, String daf /*Dependency attribs file*/,
            String saf /*Semantic attribs file*/, String charset)  throws FileNotFoundException, IOException {
        super();
        // TODO Auto-generated constructor stub
        mandatoryAttributes = new Vector ();
        mandatoryAttribvals = new Vector ();
        nonMandatoryAttributes = new Vector (0, 5);
        nonMandatoryAttribvals = new Vector (0, 5);

        readMandatoryAttribs(maf, charset);
        readNonMandatoryAttribs(nmaf, charset);

        properties = new KeyValueProperties(pf, charset);
        psAttributes = new KeyValueProperties();
        dependencyAttributes = new KeyValueProperties();
        semanticAttributes = new KeyValueProperties();

        psAttributes.read(paf, charset);
        dependencyAttributes.read(daf, charset);
        semanticAttributes.read(saf, charset);
        /*DEFAULT KEYS - nodeStart, nodeEnd, defAttribSeparator, attribSeparator, attribOR, attribEquate */
    }

    public int countMandatoryAttributes()
    {
        return mandatoryAttributes.size();
    }

    public int addMandatoryAttribute(String a, String v)
    {
        mandatoryAttributes.add(a);
        mandatoryAttribvals.add(v);
        return mandatoryAttributes.size();
    }

    public String getMandatoryAttribute(int num)
    {
        return (String) mandatoryAttributes.get(num);
    }

    public String[] getMandatoryAttributes()
    {
        return (String[]) mandatoryAttributes.toArray(new String[mandatoryAttributes.size()]);
    }

    public String getMandatoryAttributeValue(int num)
    {
        return (String) mandatoryAttribvals.get(num);
    }

    public String[] getMandatoryAttributeValues(int num)
    {
        String valString = getMandatoryAttributeValue(num);

        String attribVals[] = valString.split("::");

        return attribVals;
    }

    public String[] getMandatoryAttributeValues(String attribName)
    {
        int count = countMandatoryAttributes();
        String valString = "";

        for (int i = 0; i < count; i++)
        {
            String str = getMandatoryAttribute(i);

            if(attribName.equalsIgnoreCase(str))
            {
                valString = getMandatoryAttributeValue(i);
                break;
            }
        }

        String attribVals[] = valString.split("::");

        return attribVals;
    }

    public void modifyMandatoryAttribute(String a, String v, int num)
    {
        mandatoryAttributes.setElementAt(a, num);
        mandatoryAttribvals.setElementAt(v, num);
    }

    public void modifyMandatoryAttributeValue(String v, int num)
    {
        mandatoryAttribvals.setElementAt(v, num);
    }

    public String removeMandatoryAttribute(int num)
    {
        mandatoryAttribvals.remove(num);
        return (String) mandatoryAttributes.remove(num);
    }

    public int countNonMandatoryAttributes()
    {
        return nonMandatoryAttributes.size();
    }

    public int addNonMandatoryAttribute(String a, String v)
    {
        nonMandatoryAttributes.add(a);
        nonMandatoryAttribvals.add(v);
        return nonMandatoryAttributes.size();
    }

    public String getNonMandatoryAttribute(int num)
    {
        return (String) nonMandatoryAttributes.get(num);
    }

    public String[] getNonMandatoryAttributes()
    {
        return (String[]) nonMandatoryAttributes.toArray(new String[nonMandatoryAttributes.size()]);
    }

    public String[] getAllAttributes()
    {
        Vector allAttribs = new Vector(mandatoryAttributes);
        allAttribs.addAll(nonMandatoryAttributes);

        return (String[]) allAttribs.toArray(new String[allAttribs.size()]);
    }

    public String getNonMandatoryAttributeValue(int num)
    {
        return (String) nonMandatoryAttribvals.get(num);
    }

    public String[] getNonMandatoryAttributeValues(int num)
    {
        String valString = getNonMandatoryAttributeValue(num);

        String attribVals[] = valString.split("::");

        return attribVals;
    }

    public String[] getNonMandatoryAttributeValues(String attribName)
    {
        int count = countNonMandatoryAttributes();
        String valString = "";

        for (int i = 0; i < count; i++)
        {
            String str = getNonMandatoryAttribute(i);

            if(attribName.equalsIgnoreCase(str))
            {
                valString = getNonMandatoryAttributeValue(i);
                break;
            }
        }
        
        String attribVals[] = valString.split("::");

        return attribVals;
    }

    public void modifyNonMandatoryAttribute(String a, String v, int num)
    {
        nonMandatoryAttributes.setElementAt(a, num);
        nonMandatoryAttribvals.setElementAt(v, num);
    }

    public void modifyNonMandatoryAttributeValue(String v, int num)
    {
        nonMandatoryAttribvals.setElementAt(v, num);
    }

    public String removeNonMandatoryAttribute(int num)
    {
        nonMandatoryAttribvals.remove(num);
        return (String) nonMandatoryAttributes.remove(num);
    }

    public KeyValueProperties getProperties()
    {
        return properties;
    }

    public void setProperties(KeyValueProperties p)
    {
        properties = p;
    }

    /**
     * @return the psAttributes
     */
    public KeyValueProperties getPSAttributesKVP() {
        return psAttributes;
    }

    /**
     * @param psAttributes the psAttributes to set
     */
    public void setPSAttributesKVP(KeyValueProperties psAttributes) {
        this.psAttributes = psAttributes;
    }

    /**
     * @return the dependencyAttributes
     */
    public KeyValueProperties getDependencyAttributesKVP() {
        return dependencyAttributes;
    }

    /**
     * @param dependencyAttributes the dependencyAttributes to set
     */
    public void setDependencyAttributesKVP(KeyValueProperties dependencyAttributes) {
        this.dependencyAttributes = dependencyAttributes;
    }

    /**
     * @return the psAttributes
     */
    public KeyValueProperties getSemanticAttributesKVP() {
        return semanticAttributes;
    }

    /**
     * @param psAttributes the psAttributes to set
     */
    public void setSemanticAttributesKVP(KeyValueProperties semanticAttributes) {
        this.semanticAttributes = semanticAttributes;
    }
    
    public boolean isMandatory(String attName)
    {
        if(mandatoryAttributes.indexOf(attName) == -1)
            return false;
        
        return true;
    }

    public void readDefaultProps() throws FileNotFoundException, IOException
    {
        read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
    }

    public int read(String maf /*Mandatory attribs file*/, String nmaf /*Mandatory attribs file*/,
                    String pf /*props file*/, String paf /*PS attribs file*/,
                    String daf /*Dependency attribs file*/, String saf /*Semantic attribs file*/,
                    String charset) throws FileNotFoundException, IOException
    {
        readMandatoryAttribs(maf, charset);
        readNonMandatoryAttribs(nmaf, charset);
        readProperties(pf, charset);
        psAttributes.read(paf, charset);
        dependencyAttributes.read(daf, charset);
        semanticAttributes.read(saf, charset);

        return 0;
    }

    public int readProperties(String f, String charset) throws FileNotFoundException, IOException
    // file with two columns
    {
        return properties.read(f, charset);
    }

    public int readMandatoryAttribs(String f, String charset) throws FileNotFoundException, IOException
    // file with only one column
    {
        BufferedReader lnReader = null;

        if(!charset.equals(""))
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

        String line;
        String splitstr[] = new String[2];

        while((line = lnReader.readLine()) != null)
        {
            if(line.startsWith("#") == false && line.equals("") == false)
            {
                splitstr = line.split("\t", 2);
                addMandatoryAttribute(splitstr[0], splitstr[1]);
            }
        }

        return 0;
    }

    public int readNonMandatoryAttribs(String f, String charset) throws FileNotFoundException, IOException
    // file with only one column
    {
        BufferedReader lnReader = null;

        if(!charset.equals(""))
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

        String line;
        String splitstr[] = new String[2];

        while((line = lnReader.readLine()) != null)
        {
            if(line.startsWith("#") == false && line.equals("") == false)
            {
                splitstr = line.split("\t", 2);
                addNonMandatoryAttribute(splitstr[0], splitstr[1]);
            }
        }

        return 0;
    }

    public void print(PrintStream ps)
    {
        ps.println(GlobalProperties.getIntlString("#Mandatory_attributes"));

        for (int i = 0; i < countMandatoryAttributes(); i++)
        {
            ps.println(getMandatoryAttribute(i) + "\t" + getMandatoryAttributeValue(i));
        }

        ps.println(GlobalProperties.getIntlString("#Non-Mandatory_attributes"));

        for (int i = 0; i < countNonMandatoryAttributes(); i++)
        {
            ps.println(getNonMandatoryAttribute(i) + "\t" + getNonMandatoryAttributeValue(i));
        }

        ps.println(GlobalProperties.getIntlString("#Feature_structure_properties"));

        Iterator enm = properties.getPropertyKeys();

        while(enm.hasNext())
        {
            String key = (String) enm.next();
            ps.println(key + "\t" + properties.getPropertyValue(key));
        }

        ps.println(GlobalProperties.getIntlString("#PS_attributes"));

        psAttributes.print(ps);

        ps.println(GlobalProperties.getIntlString("#Dependency_attributes"));

        dependencyAttributes.print(ps);
    }

    public Object clone() throws CloneNotSupportedException// copyFS($fs)
    {
        FSProperties obj = (FSProperties) super.clone();

        obj.mandatoryAttributes = new Vector(countMandatoryAttributes());
        obj.mandatoryAttribvals = new Vector(countMandatoryAttributes());

        obj.nonMandatoryAttributes = new Vector(countNonMandatoryAttributes());
        obj.nonMandatoryAttribvals = new Vector(countNonMandatoryAttributes());

        for (int i = 0; i < countMandatoryAttributes(); i++)
        {
            obj.addMandatoryAttribute(getMandatoryAttribute(i), getMandatoryAttributeValue(i));
        }

        for (int i = 0; i < countNonMandatoryAttributes(); i++)
        {
            obj.addNonMandatoryAttribute(getNonMandatoryAttribute(i), getNonMandatoryAttributeValue(i));
        }

        obj.properties = (KeyValueProperties) properties.clone();
        obj.psAttributes = (KeyValueProperties) psAttributes.clone();
        obj.dependencyAttributes = (KeyValueProperties) dependencyAttributes.clone();

        return obj;
    }

    public void clear()
    {
        mandatoryAttributes.removeAllElements();
        mandatoryAttribvals.removeAllElements();
        nonMandatoryAttributes.removeAllElements();
        nonMandatoryAttribvals.removeAllElements();
        properties.clear();
    }

    //////////////////// PS Attributes ////////////////
    public static boolean isPSAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isAttribute(a, fsProperties.getPSAttributesKVP());
    }

    public static String[] getPSAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getAttributes(attribsKV);
    }

    public static String[] getPSAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getAttributeProperties(attribsKV);
    }

    public static String[] getPSAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getAttributeProperties(name, attribsKV);
    }

    // Tree attributes
    public static boolean isPSTreeAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isTreeAttribute(a, fsProperties.getPSAttributesKVP());
    }

    public static String[] getPSTreeAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getTreeAttributes(attribsKV);
    }

    public static String[] getPSTreeAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getTreeAttributeProperties(attribsKV);
    }

    public static String[] getPSTreeAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getTreeAttributeProperties(name, attribsKV);
    }

    // Graph attributes
    public static boolean isPSGraphAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isGraphAttribute(a, fsProperties.getPSAttributesKVP());
    }

    public static String[] getPSGraphAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getGraphAttributes(attribsKV);
    }

    public static String[] getPSGraphAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getGraphAttributeProperties(attribsKV);
    }

    public static String[] getPSGraphAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getPSAttributesKVP();

        return getGraphAttributeProperties(name, attribsKV);
    }

    //////////////// Dependency Attributes ////////////////
    public static boolean isDependencyAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isAttribute(a, fsProperties.getDependencyAttributesKVP());
    }

    public static String[] getDependencyAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getAttributes(attribsKV);
    }

    public static String[] getDependencyAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getAttributeProperties(attribsKV);
    }

    public static String[] getDependencyAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getAttributeProperties(name, attribsKV);
    }

    // Tree attributes
    public static boolean isDependencyTreeAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isTreeAttribute(a, fsProperties.getDependencyAttributesKVP());
    }

    public static String[] getDependencyTreeAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getTreeAttributes(attribsKV);
    }

    public static String[] getDependencyTreeAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getTreeAttributeProperties(attribsKV);
    }

    public static String[] getDependencyTreeAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getTreeAttributeProperties(name, attribsKV);
    }

    // Graph attributes
    public static boolean isDependencyGraphAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isGraphAttribute(a, fsProperties.getDependencyAttributesKVP());
    }

    public static String[] getDependencyGraphAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getGraphAttributes(attribsKV);
    }

    public static String[] getDependencyGraphAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getGraphAttributeProperties(attribsKV);
    }

    public static String[] getDependencyGraphAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getDependencyAttributesKVP();

        return getGraphAttributeProperties(name, attribsKV);
    }

    //////////////////// Semantic Attributes ////////////////
    public static boolean isSemanticAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isAttribute(a, fsProperties.getSemanticAttributesKVP());
    }

    public static String[] getSemanticAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getAttributes(attribsKV);
    }

    public static String[] getSemanticAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getAttributeProperties(attribsKV);
    }

    public static String[] getSemanticAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getAttributeProperties(name, attribsKV);
    }

    // Tree attributes
    public static boolean isSemanticTreeAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isTreeAttribute(a, fsProperties.getSemanticAttributesKVP());
    }

    public static String[] getSemanticTreeAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getTreeAttributes(attribsKV);
    }

    public static String[] getSemanticTreeAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getTreeAttributeProperties(attribsKV);
    }

    public static String[] getSemanticTreeAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getTreeAttributeProperties(name, attribsKV);
    }

    // Graph attributes
    public static boolean isSemanticGraphAttribute(String a)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        return isGraphAttribute(a, fsProperties.getSemanticAttributesKVP());
    }

    public static String[] getSemanticGraphAttributes()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getGraphAttributes(attribsKV);
    }

    public static String[] getSemanticGraphAttributeProperties()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getGraphAttributeProperties(attribsKV);
    }

    public static String[] getSemanticGraphAttributeProperties(String name)
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        KeyValueProperties attribsKV = fsProperties.getSemanticAttributesKVP();

        return getGraphAttributeProperties(name, attribsKV);
    }

    /////////////////// Helper Functions ////////////////
    public static boolean isAttribute(String a, KeyValueProperties attribsKV)
    {
        if(attribsKV.getPropertyValue(a) == null)
            return false;

        return true;
    }

    public static String[] getAttributes(KeyValueProperties attribsKV)
    {
        int acount = attribsKV.countProperties();

        String attribs[] = new String[acount];

        Iterator enm = attribsKV.getPropertyKeys();

        int i = 0;
        while(enm.hasNext())
        {
            String at = (String) enm.next();
            attribs[i++] = at;
        }

        return attribs;
    }

    public static String[] getAttributeProperties(KeyValueProperties attribsKV)
    {
        int acount = attribsKV.countProperties();

        String attribsProps[] = new String[acount];

        Iterator enm = attribsKV.getPropertyKeys();

        int i = 0;
        while(enm.hasNext())
        {
            String at = (String) enm.next();
            attribsProps[i++] = attribsKV.getPropertyValue(at);
        }

        return attribsProps;
    }

    public static String[] getAttributeProperties(String name, KeyValueProperties attribsKV)
    {
        String props[] = attribsKV.getPropertyValue(name).split("::");

        return props;
    }

    // Tree attributes
    public static boolean isTreeAttribute(String a, KeyValueProperties attribsKV)
    {
        String val = attribsKV.getPropertyValue(a);

        if(val == null)
            return false;

        String parts[] = val.split("::");

        if( (parts.length == 3 && parts[2].equalsIgnoreCase("TREE")) == false )
            return false;

        return true;
    }

    public static String[] getTreeAttributes(KeyValueProperties attribsKV)
    {
        int acount = attribsKV.countProperties();

        Vector treeAttribs = new Vector(acount);

        Iterator enm = attribsKV.getPropertyKeys();

        while(enm.hasNext())
        {
            String at = (String) enm.next();

            String val = attribsKV.getPropertyValue(at);

            if(val == null)
                continue;

            String parts[] = val.split("::");

            if(parts.length == 3 && parts[2].equalsIgnoreCase("TREE"))
            {
                treeAttribs.add(at);
            }
        }

        return (String[]) treeAttribs.toArray(new String[treeAttribs.size()]);
    }

    public static String[] getTreeAttributeProperties(KeyValueProperties attribsKV)
    {
        int acount = attribsKV.countProperties();

        Vector treeAttribProps = new Vector(acount);

        Iterator enm = attribsKV.getPropertyKeys();

        while(enm.hasNext())
        {
            String at = (String) enm.next();

            String val = attribsKV.getPropertyValue(at);

            if(val == null)
                continue;

            String parts[] = val.split("::");

            if(parts.length == 3 && parts[2].equalsIgnoreCase("TREE"))
            {
                treeAttribProps.add(val);
            }
        }

        return (String[]) treeAttribProps.toArray(new String[treeAttribProps.size()]);
    }

    public static String[] getTreeAttributeProperties(String name, KeyValueProperties attribsKV)
    {
        String val = attribsKV.getPropertyValue(name);

        if(val == null)
            return null;

        String parts[] = val.split("::");

        if(parts.length == 3 && parts[2].equalsIgnoreCase("TREE"))
        {
            return parts;
        }

        return null;
    }

    // Graph attributes
    public static boolean isGraphAttribute(String a, KeyValueProperties attribsKV)
    {
        String val = attribsKV.getPropertyValue(a);

        if(val == null)
            return false;

        String parts[] = val.split("::");

        if( (parts.length == 3 && parts[2].equalsIgnoreCase("GRAPH")) == false )
            return false;

        return true;
    }

    public static String[] getGraphAttributes(KeyValueProperties attribsKV)
    {
        int acount = attribsKV.countProperties();

        Vector treeAttribs = new Vector(acount);

        Iterator enm = attribsKV.getPropertyKeys();

        while(enm.hasNext())
        {
            String at = (String) enm.next();

            String val = attribsKV.getPropertyValue(at);

            if(val == null)
                continue;

            String parts[] = val.split("::");

            if(parts.length == 3 && parts[2].equalsIgnoreCase("GRAPH"))
            {
                treeAttribs.add(at);
            }
        }

        return (String[]) treeAttribs.toArray(new String[treeAttribs.size()]);
    }

    public static String[] getGraphAttributeProperties(KeyValueProperties attribsKV)
    {
        int acount = attribsKV.countProperties();

        Vector treeAttribProps = new Vector(acount);

        Iterator enm = attribsKV.getPropertyKeys();

        while(enm.hasNext())
        {
            String at = (String) enm.next();

            String val = attribsKV.getPropertyValue(at);

            if(val == null)
                continue;

            String parts[] = val.split("::");

            if(parts.length == 3 && parts[2].equalsIgnoreCase("GRAPH"))
            {
                treeAttribProps.add(val);
            }
        }

        return (String[]) treeAttribProps.toArray(new String[treeAttribProps.size()]);
    }

    public static String[] getGraphAttributeProperties(String name, KeyValueProperties attribsKV)
    {
        String val = attribsKV.getPropertyValue(name);

        if(val == null)
            return null;

        String parts[] = val.split("::");

        if(parts.length == 3 && parts[2].equalsIgnoreCase("GRAPH"))
        {
            return parts;
        }

        return null;
    }
    /////////////////////////////////

    public static void main(String[] args) {
        try
        {
            FSProperties fsp = new FSProperties();
            fsp.read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                    GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                    GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;

            fsp.print(System.out);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
