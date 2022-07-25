/*
 * Created on Jun 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.features.impl;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import sanchay.GlobalProperties;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.table.*;
import sanchay.tree.*;
import sanchay.util.*;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.query.SSFQueryMatchNode;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.corpus.xml.XMLProperties;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FeatureStructureImpl extends SanchayMutableTreeNode
        implements FeatureStructure, FeatureValue, SanchayDOMElement, Serializable
{

    /**
     * 
     */

    // Children could be of type FeatureAttributeImpl

    private String name;
    private boolean has_mandatory;
    private int version = 2;

    public FeatureStructureImpl() {
        super();
        name = "";
        has_mandatory = false;
    }

    public FeatureStructureImpl(Object userObject) {
        super(userObject);
        has_mandatory = false;
    }

    public FeatureStructureImpl(Object userObject, String n) {
        super(userObject);
        name = n;
        has_mandatory = false;
    }
    
    public FeatureStructureImpl(Object userObject, boolean allowsChildren) {
	super(userObject, allowsChildren);
        name = "";
        has_mandatory = false;
    }
    
    @Override
    public Object getValue()
    {
        return this;
    }
    
    @Override
    public void setValue(Object v)
    {
        setUserObject(this);
    }

    @Override
    public String getName() 
    {
        return name;
    }

    @Override
    public void setName(String n) 
    {
        name = n;
    }

    void setVersion(int v)
    {
        version = v;
    }

    @Override
    public boolean isFeatureStructure()
    {
	return true;
    }

    @Override
    public boolean hasMandatoryAttribs() 
    {
        return has_mandatory;
    }

    public void hasMandatoryAttribs(boolean m) 
    {
        has_mandatory = m;
    }

    @Override
    public void checkAndSetHasMandatory()
    {
        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();

        int count = countAttributes();
        int mcount = fsProperties.countMandatoryAttributes();

        if(count < mcount)
        {
            hasMandatoryAttribs(false);
            return;
        }
        else
        {
            for (int i = 0; i < mcount; i++)
            {
                FeatureAttribute fa = searchAttribute(fsProperties.getMandatoryAttribute(i), true);

                if(fa == null)
                {
                    hasMandatoryAttribs(false);
                    return;
                }
            }
        }
        
        hasMandatoryAttribs(true);
    }

    @Override
    public int countAttributes()
    {
        return getChildCount();
    }

    @Override
    public int addAttribute(FeatureAttribute a)
    {
        add((FeatureAttributeImpl) a);
        return getChildCount();
    }

    public int insertAttribute(FeatureAttribute a, int index)
    {
        insert((FeatureAttributeImpl) a, index);
        return getChildCount();
    }

    @Override
    public int addAttribute(FeatureAttribute a, String p /* path */) // add_attr_val($featurePath,$value,$FSReference)
    {
        FeatureValue fv = getAttributeValue(p);
        FeatureStructure fs = null;
        int ret = -1;
        
        if(fv.isFeatureStructure() == true)
        {
            
            fs = (FeatureStructureImpl) fv;
//            fs = (FeatureStructure) fv.getValue();
            ret = fs.addAttribute(a);
        }

        return ret;
    }

    @Override
    public int addAttribute(String name, String value)
    {
        FeatureAttribute fa = searchAttribute(name, true);

        int ret = 0;

        if(fa == null)
        {          
            fa = new FeatureAttributeImpl();
            fa.setName(name);

            addAttribute(fa);

            ret = 1;
        }

        FeatureValue fv = null;

        if(fa.countAltValues() > 0) {
            fv = fa.getAltValue(0);
        }

        if(fv == null)
        {
            fv = new FeatureValueImpl();
            fv.setValue(value);

            fa.addAltValue(fv);
        }
        else {
            fv.setValue(value);
        }

        return ret;
    }
    
    @Override
    public void addMandatoryAttributes()
    {
        int count = countAttributes();

	// Empty mandatory attributes values
	int mcount = FeatureStructuresImpl.getFSProperties().countMandatoryAttributes();
//	if(count == 0)
	if(hasMandatoryAttribs() == false)
	{
	    for(int i = 0; i < mcount; i++)
	    {
                FeatureAttribute fa = new FeatureAttributeImpl();
		fa.setName(FeatureStructuresImpl.getFSProperties().getMandatoryAttribute(i));
		fa.addAltValue(new FeatureValueImpl(""));
		insertAttribute(fa, i);
//		addAttribute(fa);
	    }
	}

        has_mandatory = true;
    }

    @Override
    public FeatureAttribute getAttribute(int index) 
    {
        return (FeatureAttribute) getChildAt(index);
    }

    @Override
    public int findAttribute(FeatureAttribute a) 
    {
        return getIndex((FeatureAttributeImpl) a);
    }

    @Override
    public FeatureAttribute getAttribute(String p)
    // Traverses to the FS as given by the String path and returns its reference
    // to be used in all modify/add/delete functions
    // The path will consist of FA names, separated by a period ('.')
    // SAMPLE PATH = 'af4.pf2.nf4'
    // af4 is an FA - part of this FS
    // pf2 is an FA - part of an FS which is the value of af4
    // nf4 is an FA - part of an FS which is the value of pf2

    // In the general case, FA name can be followed by the index of the alt value
    // (which should be an FS for non-leaf nodes) separated by '@'
    // SAMPLE PATH = 'af4@0.pf2@0.nf4@0'
    {
        String paths[];
        int i, k , j, count;//i used for looping all the paths,j used for traversing attribs
        paths = p.split("[.]");

        FeatureStructure node = this;
        FeatureAttribute pointer = null;
        FeatureValue fv = null;

        for(i = 0; i < paths.length; i++)
        {
//          System.out.println(paths[i]);

            String parts[] = paths[i].split("[@]");

            int altValIndex = 0;
            String attribName = paths[i];

            if(parts.length == 2)
            {
                attribName = parts[0];
                altValIndex = Integer.parseInt(parts[1]);
            }

            for (k = 0; k < node.countAttributes(); k++)
            {
                pointer = node.getAttribute(k);

                if(pointer.getName().equals(attribName))
                {
                    if(i == paths.length - 1) {
                        return pointer;
                    }

                    fv = pointer.getAltValue(altValIndex);

                    if(fv.isFeatureStructure() == true) {
                        node = (FeatureStructureImpl) fv;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public FeatureAttribute getOneOfAttributes(String names[])
    {
        for (int i = 0; i < names.length; i++)
        {
            String n = names[i];

            FeatureAttribute fa = getAttribute(n);

            if(fa != null) {
                return fa;
            }
        }

        return null;
    }

    @Override
    public void modifyAttribute(FeatureAttribute a, int index)
    {
        insert((FeatureAttributeImpl) a, index);
        remove(index + 1);
    }

    @Override
    public void modifyAttribute(FeatureAttribute a, int index, String p /* path */)
    {
        FeatureValue fv = getAttributeValue(p);
        FeatureStructure fs = null;

        if(fv.isFeatureStructure() == true)
        {
            fs = (FeatureStructureImpl) fv;
//            fs = (FeatureStructure) fv.getValue();
            fs.modifyAttribute(a, index);
        }
    }

    @Override
    public void modifyAttributeValue(FeatureValue fv, int attribIndex, int altValIndex)
    {
        FeatureAttribute fa = getAttribute(attribIndex);
        fa.modifyAltValue(fv, altValIndex);
    }

    @Override
    public void modifyAttributeValue(FeatureValue fv, String p /* path */) // update_attr_val($featurePath,$val,$FSReference)
    {
        FeatureAttribute attrib = getAttribute(p);
        String paths[] = p.split("[.]");
        String parts[] = paths[paths.length - 1].split("[@]");

        int altValIndex = 0;

        if(parts.length == 2) {
            altValIndex = Integer.parseInt(parts[1]);
        }

        attrib.modifyAltValue(fv, altValIndex);
    }

    @Override
    public FeatureAttribute removeAttribute(int index) 
    {
        FeatureAttribute rem = getAttribute(index);
        remove(index);
        
        return rem;
    }

    @Override
    public FeatureAttribute removeAttribute(String p) 
    {
        FeatureAttribute rem = getAttribute(p);
	
        if(rem != null)
        {
            if(FeatureStructuresImpl.getFSProperties().isMandatory(rem.getName()) == false) {
                remove(findAttribute(rem));
            }
            else
            {
                FeatureValue fv = rem.getAltValue(0);

                if(fv != null) {
                    fv.setValue("");
                }
            }
        }
        
        return rem;
    }

    @Override
    public void removeAllAttributes() 
    {
        while(countAttributes() > 0)
        {
    	    removeAttribute(0);
        }

        hasMandatoryAttribs(false);
    }

    @Override
    public void removeMandatoryAttributes()
    {
	if(hasMandatoryAttribs() == false) {
            return;
        }

	// Remove mandatory attributes
        int count = countAttributes();
	int mcount = FeatureStructuresImpl.getFSProperties().countMandatoryAttributes();

	if(mcount > 0 && count >= mcount)
	{
	    for(int i = 0; i < mcount; i++)
	    {
		String mAtrribName = FeatureStructuresImpl.getFSProperties().getMandatoryAttribute(i);
		FeatureAttribute fa = getAttribute(mAtrribName);
		
		if(fa != null) {
                    remove(fa);
                }
	    }
	}

	hasMandatoryAttribs(false);
    }

    @Override
    public void removeNonMandatoryAttributes()
    {
	if(hasMandatoryAttribs() == false)
	{
	    removeAllAttributes();
	    return;
	}

	// Remove non-mandatory attributes
        int count = countAttributes();

	for(int i = 0; i < count; i++)
	{
	    FeatureAttribute fa = getAttribute(i);
	    
	    if(FeatureStructuresImpl.getFSProperties().isMandatory(fa.getName()) == false)
	    {
		remove(fa);
		i--; count--;
	    }
	}
    }

    @Override
    public FeatureAttribute removeAttribute(int index, String p /* path */) // del_attr_val($featurePath,$FSReference)
    {
        FeatureValue fv = getAttributeValue(p);
        FeatureStructure fs = null;
        FeatureAttribute ret = null;

        if(fv.isFeatureStructure() == true)
        {
            fs = (FeatureStructureImpl) fv;
//            fs = (FeatureStructure) fv.getValue();
            ret = fs.removeAttribute(index);
        }

        return ret;
    }

    @Override
    public void hideAttribute(String aname)
    {
        FeatureAttribute hiddenAttr = getAttribute(aname);

        if(hiddenAttr != null)
        {
            hiddenAttr.hideAttribute();
        }
    }

    @Override
    public void unhideAttribute(String aname)
    {
        FeatureAttribute hiddenAttr = getAttribute(aname);

        if(hiddenAttr != null)
        {
            hiddenAttr.unhideAttribute();
        }
    }

    @Override
    public List<String> getAttributeNames() // get_attributes($FSReference)
    {
        // all the names of attributes are returned
        List<String> v = new ArrayList<String>();
        int k;
        
        for (k = 0; k < countAttributes(); k++)
        {
            v.add(getAttribute(k).getName());
        }

        return v;
    }

    @Override
    public List<String> getAttributeNames(String p /* path */)
    {
        FeatureValue fv = getAttributeValue(p);
        FeatureStructure fs = null;
        List<String> ret = null;

        if(fv.isFeatureStructure() == true)
        {
            fs = (FeatureStructureImpl) fv;
//            fs = (FeatureStructure) fv.getValue();
            ret = fs.getAttributeNames();
        }

        return ret;
    }

    @Override
    public List<String> getAttributeValues()
    {
        // all the values of top-level attibutes are returned
        // a Vector consisting of attribute values strings is returned
        List<String> v = new ArrayList<String>();

        for (int k = 0; k < countAttributes(); k++)
        {
            FeatureAttribute fa = getAttribute(k);

            String attribVal = getAttributeValue(fa.getName()).makeString();
            v.add(attribVal);
        }

        return v;
    }

    @Override
    public List<String> getAttributeValuePairs()
    {
        // a Vector consisting of attribute-value pair strings is returned
        List<String> v = new ArrayList<String>();

        for (int k = 0; k < countAttributes(); k++)
        {
            FeatureAttribute fa = getAttribute(k);
            
            String attribVal = fa.getName() + "=" + getAttributeValue(fa.getName()).makeString();
            v.add(attribVal);
        }

        return v;
    }

    @Override
    public FeatureValue getAttributeValueByIndex(String p /* path containing attribute index */)
    {
        FeatureAttribute attrib = getAttribute(p);
        String paths[] = p.split("[.]");
        String parts[] = paths[paths.length - 1].split("[@]");

        int altValIndex = 0;

        if(parts.length == 2) {
            altValIndex = Integer.parseInt(parts[1]);
        }

        return attrib.getAltValue(altValIndex);
    }

    @Override
    public FeatureValue getAttributeValue(String p)
    {
	// The first values of the attibute at the given path are returned
        List<FeatureValue> vals = getAttributeValues(p);
	
	if(vals == null || vals.size() <= 0) {
            return null;
        }

        return (FeatureValue) vals.get(0);
    }

    @Override
    public String getAttributeValueString(String p)
    {
	// The first values of the attibute at the given path are returned
        List<FeatureValue> vals = getAttributeValues(p);

	if(vals == null || vals.size() <= 0) {
            return null;
        }

        return ((FeatureValue) vals.get(0)).makeString();
    }

    @Override
    public void setAttributeValue(String attibName, String val)
    {
//        if( !(hasMandatoryAttribs() == false && getFSProperties().isMandatory(attibName) == true) )
        if(hasMandatoryAttribs() == false && FeatureStructuresImpl.getFSProperties().isMandatory(attibName) == true )
        {
            addMandatoryAttributes();
        }

        FeatureAttribute fa = getAttribute(attibName);

        if(fa == null)
        {
            fa = new FeatureAttributeImpl();
            fa.setName(attibName);
            addAttribute(fa);
        }

        FeatureValue fv = null;

        if(fa.countAltValues() == 0)
        {
            fv = new FeatureValueImpl();
            fa.addAltValue(fv);
        }
        else {
            fv = fa.getAltValue(0);
        }

        val = SSFQueryMatchNode.stripQuotes(val);

        fv.setValue(val);
    }

    @Override
    public List<FeatureValue> getAttributeValues(String p)
    {
        int count = countAttributes();

        List<FeatureValue> ret = new ArrayList<FeatureValue>();

        for (int i = 0; i < count; i++)
        {
            FeatureAttribute fa = getAttribute(p);

            if(fa != null && fa.getName().equals(p) && fa.countAltValues() > 0)
            {
                FeatureValue fv = fa.getAltValue(0);
                ret.add(fv);
            }
        }
//        FeatureValue fv = getAttributeValueString(p);
//        FeatureStructure fs = null;
//
//        if(fv.isFeatureStructure() == true)
//        {
//            fs = (FeatureStructureImpl) fv;
////            fs = (FeatureStructure) fv.getValue();
//            ret = fs.getAttributeValues();
//        }

        return ret;
    }

    @Override
    public List<FeatureAttribute> getPaths(String name) // get_path_values($attr,$fs)
    {
        // Search for an attribute name and return it's paths
        List<FeatureAttribute> v = new ArrayList<FeatureAttribute>();

        for (int i = 0; i < countAttributes(); i++)
        {
            FeatureAttribute fa = getAttribute(i);
            String path = fa.getName();

            if(fa.getName().equalsIgnoreCase(name)) {
                v.add(fa);
            }

            for (int j = 0; j < fa.countAltValues(); j++)
            {
                FeatureValue fv = fa.getAltValue(i);

                if(fv.isFeatureStructure() == true)
                {
                    FeatureStructureImpl fs = (FeatureStructureImpl) fv;
//                    FeatureStructureImpl fs = (FeatureStructureImpl) fv.getValue();
                    v.addAll(fs.getPaths(name, path));
                }
            }
        }

        return v;
    }

    private List<FeatureAttribute> getPaths(String name, String p) // get_path_values($attr,$fs)
    {
        List<FeatureAttribute> v = new ArrayList<FeatureAttribute>();

        for (int i = 0; i < countAttributes(); i++)
        {
            FeatureAttribute fa = getAttribute(i);
            String path = p + "." + fa.getName();

            if(fa.getName().equalsIgnoreCase(name)) {
                v.add(fa);
            }

            for (int j = 0; j < fa.countAltValues(); j++)
            {
                FeatureValue fv = fa.getAltValue(i);

                if(fv.isFeatureStructure() == true)
                {
                    FeatureStructureImpl fs = (FeatureStructureImpl) fv;
//                      FeatureStructureImpl fs = (FeatureStructureImpl) fv.getValue();
                    v.addAll(fs.getPaths(name, path));
                }
            }
        }

        return v;
    }

    @Override
    public FeatureAttribute searchOneOfAttributes(String names[], boolean exactMatch)
    {
        // Search for an attribute name and return first attribute which matches
        for (int i = 0; i < names.length; i++)
        {
            String name = names[i];

            List<FeatureAttribute> attribs = searchAttributes(name, exactMatch);

            if(attribs == null || attribs.size() <= 0) {
                continue;
            }

            return (FeatureAttribute) attribs.get(0);
        }

        return null;
    }

    @Override
    public FeatureAttribute searchAttribute(String name, boolean exactMatch)
    {
        // Search for an attribute name and return first attribute which matches
        List<FeatureAttribute> attribs = searchAttributes(name, exactMatch);

	if(attribs == null || attribs.size() <= 0) {
            return null;
        }

        return (FeatureAttribute) attribs.get(0);
    }

    @Override
    public List<FeatureAttribute> searchAttributes(String name, boolean exactMatch) // get_path_values($attr,$fs)
    {
        // Search for an attribute name and return attributes which match
        List<FeatureAttribute> v = new ArrayList<FeatureAttribute>();

        String lname = name;
        
        if(exactMatch) {
            lname = "^" + lname + "$";
        }

        Pattern pAttrib = Pattern.compile(lname);
//        Pattern pAttrib = Pattern.compile(name, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        for (int i = 0; i < countAttributes(); i++)
        {
            FeatureAttribute fa = getAttribute(i);

            Matcher m = pAttrib.matcher(fa.getName());

            if(m.find()) {
                v.add(fa);
            }

            for (int j = 0; j < fa.countAltValues(); j++)
            {
                FeatureValue fv = fa.getAltValue(j);

                if(fv.isFeatureStructure() == true)
                {
                    FeatureStructure fs = (FeatureStructure) fv;
//                      FeatureStructure fs = (FeatureStructure) fv.getValue();
                    v.addAll(fs.searchAttributes(name, exactMatch));
                }
            }
        }

        return v;
    }
    
    @Override
    public FeatureAttribute searchAttributeValue(String name, String val, boolean exactMatch)
    {
        // Search for the first attribute with the given name and value and return it
       List<FeatureAttribute> attribs = searchAttributeValues(name, val, exactMatch);
	
	if(attribs == null || attribs.size() <= 0) {
            return null;
        }

        return (FeatureAttribute) attribs.get(0);
    }
 
    @Override
    public List<FeatureAttribute> searchAttributeValues(String name, String val, boolean exactMatch)
    {
        // Search for attributes with the given name and value
        List<FeatureAttribute> v = new ArrayList<FeatureAttribute>();

        String lname = name;
        String lval = val;

        if(exactMatch)
        {
            lname = "^" + name + "$";
            lval = "^" + val + "$";
        }

        Pattern pAttrib = Pattern.compile(lname);
        Pattern pVal = Pattern.compile(lval);
//        Pattern pAttrib = Pattern.compile(name, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);
//        Pattern pVal = Pattern.compile(val, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        for (int i = 0; i < countAttributes(); i++)
        {
            FeatureAttribute fa = getAttribute(i);
//            boolean yes = fa.getName().equalsIgnoreCase(name);
            Matcher mAttrib = pAttrib.matcher(fa.getName());
            boolean yes = mAttrib.find();

            for (int j = 0; j < fa.countAltValues(); j++)
            {
                FeatureValue fv = fa.getAltValue(j);

                if(yes && fv.isFeatureStructure() == false)
                {
                    if(fv.getValue().getClass().equals(String.class))
                    {
                        String sfv = (String) fv.getValue();
        
                        String valParts[] = sfv.split("[:]");
                        
//                        if(valParts[0] != null && val.equals(valParts[0]) == true)
                        if(valParts[0] != null)
                        {
                            Matcher mVal = pVal.matcher(valParts[0]);

                            if(mVal.find()) {
                                v.add(fa);
                            }
                        }
                    }
                }

                if(fv.isFeatureStructure() == true)
                {
                    FeatureStructureImpl fs = (FeatureStructureImpl) fv;
//                    FeatureStructure fs = (FeatureStructure) fv.getValue();
                    v.addAll(fs.searchAttributeValues(name, val, exactMatch));
                }
            }
        }

        return v;
    }

    @Override
    public List<FeatureAttribute> replaceAttributeValues(String name, String val, String nameReplace, String valReplace)
    {
        // Search for attributes with the given name and value
        List<FeatureAttribute> v = new ArrayList<FeatureAttribute>();

        Pattern pAttrib = Pattern.compile(name);
        Pattern pVal = Pattern.compile(val);
//        Pattern pAttrib = Pattern.compile(name, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);
//        Pattern pVal = Pattern.compile(val, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        for (int i = 0; i < countAttributes(); i++)
        {
            FeatureAttribute fa = getAttribute(i);

            Matcher mAttrib = pAttrib.matcher(fa.getName());
            boolean yes = mAttrib.find();
//            boolean yes = fa.getName().equalsIgnoreCase(name);

            for (int j = 0; j < fa.countAltValues(); j++)
            {
                FeatureValue fv = fa.getAltValue(j);

                if(yes && fv.isFeatureStructure() == false)
                {
                    if(fv.getValue().getClass().equals(String.class))
                    {
                        String sfv = (String) fv.getValue();
        
                        String valParts[] = sfv.split("[:]");
                        
                        if(valParts[0] != null)
                        {
                            Matcher mVal = pVal.matcher(valParts[0]);

                            if(mVal.find())
//                            if(val.equals(valParts[0]) == true)
                            {
                                v.add(fa);
                                fa.setName(nameReplace);
                                fa.getAltValue(0).setValue(valReplace);
                            }
                        }
                    }
                }

                if(fv.isFeatureStructure() == true)
                {
                    FeatureStructureImpl fs = (FeatureStructureImpl) fv;
//                    FeatureStructure fs = (FeatureStructure) fv.getValue();
                    v.addAll(fs.replaceAttributeValues(name, val, nameReplace, valReplace));
                }
            }
        }

        return v;
    }

    /**
     * Not yet implemented.
     * @param f1
     * @param f2
     * @return
     */
    @Override
    public FeatureStructure unify(FeatureStructure f1, FeatureStructure f2) // unify($fs1,$fs2)
    {
            return null;
    }

    /**
     * Not yet implemented.
     * @param f1
     * @param f2
     * @return
     */
    @Override
    public FeatureStructure merge(FeatureStructure f1, FeatureStructure f2) // merge($fs1,$fs2)
    {
            return null;
    }

    /**
     * Not yet implemented.
     *
     */
    @Override
    public void prune()
    {
    }

    @Override
    public SanchayTableModel getFeatureTable()
    {
	int acount = countAttributes();
        SanchayTableModel table = new SanchayTableModel(acount, 2);
        table.setColumnIdentifiers(new String[]{GlobalProperties.getIntlString("Feature"), GlobalProperties.getIntlString("Value")});

	for(int i = 0; i < acount; i++)
	{
	    // Assuming only one value for an attribute
	    table.setValueAt(getAttribute(i).getName(), i, 0);
	    table.setValueAt(getAttribute(i).getAltValue(0).makeString(), i, 1);
	}
	
	return table;
    }

    @Override
    public void setFeatureTable(SanchayTableModel ft)
    {
	removeAllChildren();
	
	int count = ft.getRowCount();

	for(int i = 0; i < count; i++)
	{
	    FeatureAttribute fa = new FeatureAttributeImpl();
	    fa.setName((String) ft.getValueAt(i, 0));

	    FSProperties fsp = FeatureStructuresImpl.getFSProperties();

	    String nodeStart = fsp.getProperties().getPropertyValue("nodeStart");
	    String nodeEnd = fsp.getProperties().getPropertyValue("nodeEnd");
	    
	    String vstr = (String) ft.getValueAt(i, 1);
	    FeatureValue fv = null;
	    
	    if(vstr.startsWith(nodeStart) && vstr.endsWith(nodeEnd))
        {
    		fv = new FeatureStructureImpl();
            ((FeatureStructureImpl) fv).version = 1;
        }
	    else
        {
    		fv = new FeatureValueImpl();
        }
	    
	    try
	    {
		fv.readString(vstr);
	    } catch(Exception ex)
	    {
		ex.printStackTrace();
	    }

	    fa.addAltValue(fv);
	    addAttribute(fa);
	}
    }

    @Override
    public int readString(String fs_str) throws Exception
    {
        clear();
        
        Pattern fsPtn = Pattern.compile("^<\\s*fs", Pattern.CASE_INSENSITIVE);
//        Pattern fsPtn = Pattern.compile("^<\\s*fs", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = fsPtn.matcher(fs_str);
        
        if(m.find()) {
            return readStringV2(fs_str);
        }
        
        version = 1;
        fs_str = fs_str.replaceAll("'", "");

        FSProperties fsp = FeatureStructuresImpl.getFSProperties();

        String basicName = fsp.getProperties().getPropertyValue("basicName"); 
        String nodeStart = fsp.getProperties().getPropertyValue("nodeStart");
        String nodeEnd = fsp.getProperties().getPropertyValue("nodeEnd");
        String defAttribSeparator = fsp.getProperties().getPropertyValue("defAttribSeparator");
        String attribSeparatorV1 = fsp.getProperties().getPropertyValue("attribSeparatorV1");
        String fsOR = fsp.getProperties().getPropertyValue("fsOR");
        String attribOR = fsp.getProperties().getPropertyValue("attribOR");
        String attribEquate = fsp.getProperties().getPropertyValue("attribEquate");
	
	if(fs_str.equals(nodeStart) || fs_str.equals(nodeEnd) || fs_str.equals(nodeStart + nodeEnd)) {
            return readError(fs_str);
        }

        String str = "";
        int position = 0;

        // On every error condition in FS analysis readError(fs_str) is called
//		System.out.println("INPUT FS is - " + fs_str);
        String temp[], tstr;
        //int i; // any loop will always be called through this

        temp = fs_str.split(nodeStart + "{1}", 2);
        if (temp[0].equals("") == false)	{ return readError(fs_str); }
        position++;

        // how to use regex in java
        // pattern = Pattern.compile(");
        // matcher = pattern.matcher(wholeContent);

        tstr = temp[1];
        temp = tstr.split(attribEquate + "{1}", 2);
        //for (i = temp.length - 1; i >=0; i --) 	{System.out.println(temp[i] + i );} // this is temp test code
        if (temp[0].equals("") == true)	{ return readError(fs_str); }
        // this is to process the predifined FS part <af=.....
        if (temp[0].trim().equals(basicName) == true)
        {
            setName(basicName);
//            hasMandatoryAttribs(true);
            
            position += basicName.length() + 1;

            tstr = temp[1];
            temp = tstr.split(defAttribSeparator + "{1}");

	    // If the root word is comma
            if (temp.length == fsp.countMandatoryAttributes() + 1)
	    {
		String temp1[] = new String[fsp.countMandatoryAttributes()];
		
		for (int i = 0; i < fsp.countMandatoryAttributes(); i++)
		{
		    temp1[i] = temp[i + 1];
		}
		
		temp = temp1;
		position += defAttribSeparator.length();
	    }
	    
            if (temp.length != fsp.countMandatoryAttributes()) {
                return readError(fs_str);
            }
	    
            position += fsp.countMandatoryAttributes() - 1;

            //for (i = temp.length - 1; i >=0; i --) 	{System.out.println(temp[i] + i );} // this is temp test code
            // this printing part is for making purpose, not to be part of final API
            //			 THIS IS THE ERROR CHECKING CONDITION, HAS BEEN DISABLED AS OF NOW ....
            for(int i = 0; i < fsp.countMandatoryAttributes()-1; i++)
            {
//              THIS IS THE ERROR CHECKING COMDITION, HAS BEEN DISABLED AS OF NOW ....
               /*   if(temp[i].matches(fsp.getAttributeValueString(i)) == false)
                      return readError(fs_str); */
//			      {
//			          System.out.println(fsp.getAttributeValueString(i));
//			      }

                FeatureAttribute fa = new FeatureAttributeImpl();
                fa.setName(fsp.getMandatoryAttribute(i));

                FeatureValue fv = new FeatureValueImpl(temp[i]);
//                fv.setValue(temp[i]);
                fa.addAltValue(fv);
                this.addAttribute (fa);
                    
                position += temp[i].length();
            }

            int k = fsp.countMandatoryAttributes();
            FeatureAttribute fa = new FeatureAttributeImpl();
            fa.setName(fsp.getMandatoryAttribute(k-1));

            FeatureValue fv = new FeatureValueImpl("");
//            fv.setValue("");
            fa.addAltValue(fv);
            this.addAttribute (fa);
	    
	    boolean end = true;
	    
	    if(temp[k-1].indexOf(attribSeparatorV1) >= 0) {
                if(temp[k-1].indexOf(nodeEnd) > temp[k-1].indexOf(attribSeparatorV1)) {
                    end = false;
                }
            }

	    String temp1[] = null;
	    
	    if(end)
	    {
		temp1 = temp[k-1].split(nodeEnd, 2);
		temp[k-1] = nodeEnd;
	    }
	    else
	    {
		temp1 = temp[k-1].split(attribSeparatorV1, 2);
		temp[k-1] = attribSeparatorV1 + temp1[1];
	    }

	    String vl = temp1[0];
	    fv.setValue(vl);
	    
            position += temp1[0].length();

            /*System.out.println("Root "+temp[0]);
            System.out.println("Catg "+temp[1]);
            System.out.println("Gend "+temp[2]);
            System.out.println("Numb "+temp[3]);
            System.out.println("Pers "+temp[4]);
            System.out.println("Case "+temp[5]);
            System.out.println("Blnk "+temp[6]);
            System.out.println("rem  "+temp[7]);*/
            // if there is a `/` in temp[7] then there are additional attributes
            // if temp[7] = ">" then it is end of feature structure
            tstr = temp[k - 1];
            temp = tstr.split(attribSeparatorV1 + "{1}", 2);
            //for (i = temp.length - 1; i >=0; i --) 	{System.out.println(temp[i] + i );} // this is temp test code

            if (temp[0].equals(nodeEnd))
            {
                return (position += temp[0].length());
            }
            else if ((temp[0].equals("")) && (temp[1].equals("") == false))
            {
//              System.out.println("FS has additional attributes");
                tstr = temp[1];
		position++;
            }
            else {
                return readError(fs_str);
            }

            ReadRecurseArgs args = new ReadRecurseArgs(tstr, 0, 0, null);
            int ret = readRecurse(args);

            if(ret == -1) {
                return readError(fs_str);
            }
            else {
                return (position += ret);
            }
        }
        else
        {
            ReadRecurseArgs args = new ReadRecurseArgs(tstr, 0, 0, null);
            int ret = readRecurse(args);

            if(ret == -1)
            {
                return readError(fs_str);
            }
            else
            {
                checkAndSetHasMandatory();
                return (position += ret);
            }
        }
    }

    public int readStringV2(String fs_str) throws Exception
    {
        // Doesn't allow recursive FS
        version = 2;
        FSProperties fsp = FeatureStructuresImpl.getFSProperties();
        String basicName = fsp.getProperties().getPropertyValue("basicName"); 
        String attribSeparatorV2 = fsp.getProperties().getPropertyValue("attribSeparatorV2");
        
        int pos = fs_str.length();
        fs_str = fs_str.replaceAll(">", "");
        fs_str = fs_str.trim();
        
        String avpairs[] = fs_str.split(attribSeparatorV2);
        
        for(int i = 1; i < avpairs.length; i++)
        {
            String av[] = avpairs[i].split("[\\=]");

            if(av.length == 2)
            {
                av[0] = av[0].trim();
//                av[0] = av[0].replaceAll("[\\']", "");
//                av[0] = av[0].replaceAll("[\\\"]", "");
                av[0] = SSFQueryMatchNode.stripBoundingStrings(av[0], "'", "'");

                av[1] = av[1].trim();
//                av[1] = av[1].replaceAll("[\\']", "");
//                av[1] = av[1].replaceAll("[\\\"]", "");
                av[1] = SSFQueryMatchNode.stripBoundingStrings(av[1], "'", "'");

                if(av[0].equalsIgnoreCase(basicName))
                {
//                    hasMandatoryAttribs(true);
                    String ma[] = av[1].split("[,]");
                    
                    for (int j = 0; j < ma.length; j++)
                    {
                        FeatureAttribute a = new FeatureAttributeImpl();
                        a.setName(fsp.getMandatoryAttribute(j));

                        FeatureValue v = new FeatureValueImpl();
                        v.setValue(ma[j]);
                        
                        a.addAltValue(v);
                        addAttribute(a);
                    }

                    int count  = fsp.countMandatoryAttributes();
                    
                    if(ma.length < count)
                    {
                        for (int j = 0; j < count - ma.length; j++)
                        {
                            FeatureAttribute a = new FeatureAttributeImpl();
                            a.setName(fsp.getMandatoryAttribute(ma.length + j));

                            FeatureValue v = new FeatureValueImpl();
                            v.setValue("");

                            a.addAltValue(v);
                            addAttribute(a);
                        }
                    }
                }
                else
                {
                    FeatureAttribute a = new FeatureAttributeImpl();
                    a.setName(av[0]);

                    FeatureValue v = new FeatureValueImpl();
                    v.setValue(av[1]);

                    a.addAltValue(v);
                    addAttribute(a);
                }
            }
        }

        checkAndSetHasMandatory();
        
        return pos;
    }

    // Processing of non-Predefined / Additional attributes
    private int readRecurse(ReadRecurseArgs args) throws Exception // returns the current position of pointer
    {
        FSProperties fsp = FeatureStructuresImpl.getFSProperties();
        
        char pointer[] = new char[1];
        //Character 
        boolean end = false; 
        String pointStr;
        String attribName, attribValue;
//      System.out.println("\t Node Start");
        //pointer[0]=' ';
        while (end == false)
        {
            //first check for attribute name
            boolean isattrib = false;

            pointer[0] = args.str.charAt(args.position);
            pointStr = new String(pointer);
            attribName = "";
            //System.out.println("Attrib Start");//
            while(isattrib == false)
            {
                //System.out.println(pointStr);//
                args.position++;
                // checks wether its part of a word or has the word ended
                if (Pattern.matches ("[A-Za-z0-9_\\-]", pointStr) == true)
                {
                    attribName = attribName + pointStr;
                    //System.out.println("Part of value name");//
                    //System.out.println("AttribName - " + attribName + " Pointer - " +pointStr + position);//
                }
                else if (pointStr.equals(fsp.getProperties().getPropertyValue("attribEquate")))
                {
                    isattrib = true;
//                  System.out.print (attribName + " = ");
                }
                else if(pointStr.equals("\"") || pointStr.equals("`") || pointStr.equals("\'"))
		{
		    // Quote: ignore
		}
                else {
                    return readError(pointStr);
                }

                pointer[0] = args.str.charAt(args.position);
                pointStr = new String(pointer);
            }

/*          FeatureAttribute fa = null;
            if(args.fa == null) 
            {
                fa = new FeatureAttribute();
                fa.setName(attribName);
            }
            else
            {
                //args.fa.setName(attribName);
            }*/
            //System.out.println("out of attribvalue");//
            FeatureAttribute fa = new FeatureAttributeImpl();
            
            fa.setName(attribName);
            addAttribute (fa);

            attribValue = "";
            boolean isOR = true;
            
            while (isOR == true)
            {
                //System.out.println(pointStr+"#");//	

                if (pointStr.equals(fsp.getProperties().getPropertyValue("nodeStart")) == true)
                // presence of child node
                {
//                  System.out.println("Child node");
                    FeatureStructureImpl fs = new FeatureStructureImpl();
//                    FeatureValue fv = new FeatureValueImpl();
//                    fv.setValue(fs);
//                    fa.addAltValue(fv);
                    fa.addAltValue(fs);
                    fs.version = 1;
                    ReadRecurseArgs args1 = new ReadRecurseArgs(args.str, args.level+1, args.position+1,null);
                    args.position = fs.readRecurse(args1);
                }
                else
                // else take in the attrib value....
                {
                    boolean isvalue = false;
                    attribValue = "";
                    //System.out.println(pointStr+"#"+position);//	
                    while ( isvalue == false)
                    {
                        //System.out.println(pointStr+"#"+position);//	
                        if (Pattern.matches ("[A-Za-z0-9':,_ %\\-\u002E\u00C0-\u0F7D1]", pointStr) == true)
                        {
                            attribValue = attribValue + pointStr;
                            //System.out.println("Attribvalue - " + attribValue + " Pointer - " +pointStr);//
                        }
                        else if (pointStr.equals(fsp.getProperties().getPropertyValue("attribSeparatorV1")) == true) // so SET this as value and then restart from checking attrib name
                        {
//                          System.out.println(attribValue + " ");
                            //System.out.println("Next Attrib");
                            isOR = false;
                            isvalue = true;
                            args.position--;// done to settle the inconsistency in pointer in the remaining part of While loop

                            FeatureValue fv = new FeatureValueImpl(attribValue);
//                            fv.setValue(attribValue);
                            fa.addAltValue(fv);
                            //this.addAttribute(fa);
                        }
                        else if (pointStr.equals(fsp.getProperties().getPropertyValue("nodeEnd")) == true) // return, after setting the value
                        {
//                          System.out.print(attribValue + " ");
//                          System.out.println("\n\tNode End");//
                            isvalue = true;
                            end = true;

                            FeatureValue fv = new FeatureValueImpl(attribValue);
//                            fv.setValue(attribValue);
                            fa.addAltValue(fv);
                            return args.position;
                        }
                        else if (pointStr.equals(fsp.getProperties().getPropertyValue("attribOR")) == true)// read next value, after setting this value
                        {
//                          System.out.print(attribValue + " OR ");
                            isvalue = true;
                            //goto label:;

                            FeatureValue fv = new FeatureValueImpl(attribValue);
//                            fv.setValue(attribValue);
                            fa.addAltValue(fv);
                        }
			else if(pointStr.equals("\"") || pointStr.equals("`") || pointStr.equals("\'"))
			{
			    // Quote: ignore
			}
                        else {
                            return readError(pointStr);
                        }
                        
                        args.position++;
                        
//                        System.out.println(args.str);
                        pointer[0] = args.str.charAt(args.position);	
                        pointStr = new String(pointer);			
                    }
                    //System.out.println("Out of value");//
                }
                
                //to return of 'af1=sad>>>'
                args.position++;
                pointer[0] = args.str.charAt(args.position);	
                pointStr = new String(pointer);		
                //System.out.println(pointStr+"#"+position);//	

                if (pointStr.equals(fsp.getProperties().getPropertyValue("nodeEnd")) == true) // return, after setting the value
                {
//                  System.out.print(" ");
//                  System.out.println("\tOut of child in nested");//
                    end = true;
                    return args.position;
                }
                else if (pointStr.equals(fsp.getProperties().getPropertyValue("attribSeparatorV1")) == true) // to check for AND after >>>>>
                {
//                  System.out.print(attribValue + " ");
//                  System.out.println("\tNext Attrib in Parent");
                    isOR = false;
                    args.position++;
                }
                else if (pointStr.equals(fsp.getProperties().getPropertyValue("attribOR")) == true)// read next value, after setting this value
                {
//                  System.out.print("the childnode OR ");
                    isOR = true;
                    args.position++;
                    pointer[0] = args.str.charAt(args.position);	
                    pointStr = new String(pointer);			

                    //goto label:;
                }
            }

            //System.out.println("Out of OR");//
        }

        return (0);
    }

    private int readError(String fs_str)
    {
        System.out.println(GlobalProperties.getIntlString("Error_in_FS_input:") + fs_str);
        return -1;
    }

    @Override
    public void print(PrintStream ps)
    {
        ps.println(makeString());
    }

    @Override
    public String makeStringFV()
    {
        String fvStr = "";

        int fcount = countAttributes();

        for (int i = 0; i < fcount; i++)
        {
            FeatureAttribute fa = getAttribute(i);

            if(fa.countAltValues() == 0) {
                continue;
            }

            fvStr += fa.getName() + "='" + fa.getAltValue(0).getValue() + "'";

            if(i < fcount - 1) {
                fvStr += " ";
            }
        }

        return fvStr;
    }

    @Override
    public String makeString()
    {
        String str = "";
	int count = countAttributes();
	
	if(count == 0) {
            return "";
        }

        checkAndSetHasMandatory();

        FSProperties fsp = FeatureStructuresImpl.getFSProperties();

        if(version == 1) {
            str += fsp.getProperties().getPropertyValueForPrint("nodeStart");
        }
        else if(version == 2) {
            str += fsp.getProperties().getPropertyValueForPrint("nodeStart") + "fs ";
        }

        if(hasMandatoryAttribs() == true)
        {
            str += fsp.getProperties().getPropertyValueForPrint("basicName") + fsp.getProperties().getPropertyValueForPrint("attribEquate");
        }
        else
        {

        }
        
        int k = 0;
        boolean is_mand =false;
        
        if(hasMandatoryAttribs() == true && countAttributes() >= fsp.countMandatoryAttributes())
        {
            is_mand = true;
            
            for (int i = 0; i < fsp.countMandatoryAttributes(); i++)
            {
                if(i == 0 && str.endsWith("'") == false) {
                    str = str + "'";
                }
                
                str += ((FeatureAttribute) getAttribute(i)).makeString(is_mand);

                if ((i+1) < fsp.countMandatoryAttributes())
                {
                    str += fsp.getProperties().getPropertyValueForPrint("defAttribSeparator");
                }
                else 
                {
                    k=i+1;
                }

                if(i == fsp.countMandatoryAttributes() - 1 && str.endsWith("'") == false) {
                    str = str + "'";
                }
            }
        }
        else {
            hasMandatoryAttribs(false);
        }

        for (int i = k; i < count; i++)
        {
//            if(getAttribute(i).getName().equals(SSFNode.HIGHLIGHT))
//                continue;

            if (is_mand == true)
            {
                if(version == 1) {
                    str += fsp.getProperties().getPropertyValueForPrint("attribSeparatorV1");
                }
                else if(version == 2) {
                    str += fsp.getProperties().getPropertyValue("attribSeparatorForPrinting");
                }

                is_mand = false;
            }

            str += ((FeatureAttribute) getAttribute(i)).makeString(is_mand);

            if ((i+1) < count)
            {
                if(version == 1) {
                    str += fsp.getProperties().getPropertyValueForPrint("attribSeparatorV1");
                }
                else if(version == 2) {
                    str += fsp.getProperties().getPropertyValue("attribSeparatorForPrinting");
                }
            }
        }

        str += fsp.getProperties().getPropertyValueForPrint("nodeEnd");

        return str;
    }

    @Override
    public String toString()
    {
        return makeString();
    }

    @Override
    public String makeStringForRendering()
    {
        String str = "";
	int count = countAttributes();

	if(count == 0) {
            return "";
        }

        checkAndSetHasMandatory();

        FSProperties fsp = FeatureStructuresImpl.getFSProperties();

        if(version == 1) {
            str += fsp.getProperties().getPropertyValueForPrint("nodeStart");
        }
        else if(version == 2) {
            str += fsp.getProperties().getPropertyValueForPrint("nodeStart") + "fs ";
        }

        if(hasMandatoryAttribs() == true)
        {
            str += fsp.getProperties().getPropertyValueForPrint("basicName") + fsp.getProperties().getPropertyValueForPrint("attribEquate");
        }
        else
        {

        }

        int k = 0;
        boolean is_mand =false;

        if(hasMandatoryAttribs() == true && countAttributes() >= fsp.countMandatoryAttributes())
        {
            is_mand = true;

            for (int i = 0; i < fsp.countMandatoryAttributes(); i++)
            {
                if(i == 0 && str.endsWith("'") == false) {
                    str = str + "'";
                }

                FeatureAttribute fa = getAttribute(i);

                if(fa.isHiddenAttribute()) {
                    continue;
                }

                str += fa.makeString(is_mand);

                if ((i+1) < fsp.countMandatoryAttributes())
                {
                    str += fsp.getProperties().getPropertyValueForPrint("defAttribSeparator");
                }
                else
                {
                    k=i+1;
                }

                if(i == fsp.countMandatoryAttributes() - 1 && str.endsWith("'") == false) {
                    str = str + "'";
                }
            }
        }
        else {
            hasMandatoryAttribs(false);
        }

        for (int i = k; i < count; i++)
        {
//            if(getAttribute(i).getName().equals(SSFNode.HIGHLIGHT))
//                continue;

            if (is_mand == true)
            {
                if(version == 1) {
                    str += fsp.getProperties().getPropertyValueForPrint("attribSeparatorV1");
                }
                else if(version == 2) {
                    str += fsp.getProperties().getPropertyValue("attribSeparatorForPrinting");
                }

                is_mand = false;
            }

            FeatureAttribute fa = getAttribute(i);

            if(fa.isHiddenAttribute()) {
                continue;
            }

            str += fa.makeString(is_mand);

            if ((i+1) < count)
            {
                if(version == 1) {
                    str += fsp.getProperties().getPropertyValueForPrint("attribSeparatorV1");
                }
                else if(version == 2) {
                    str += fsp.getProperties().getPropertyValue("attribSeparatorForPrinting");
                }
            }
        }

        str += fsp.getProperties().getPropertyValueForPrint("nodeEnd");

        return str.trim();
    }
    
    @Override
    public void clear()
    {
        name = "";
//        has_mandatory = false;
//        removeAllChildren();
        removeAllAttributes();
    }

    @Override
    public void setToEmpty()
    {
	removeAllAttributes();
//	removeNonMandatoryAttributes();

//        getAltFSValue(0).setToEmpty();
    }

    @Override
    public void clearAnnotation(long annoLevelFlags, SSFNode containingNode)
    {
        int count = countAttributes();
	
	if((containingNode instanceof SSFLexItem && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_MANDATORY_ATTRIBUTES))
		|| (containingNode instanceof SSFPhrase && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_MANDATORY_ATTRIBUTES))) {
            removeMandatoryAttributes();
        }

	if((containingNode instanceof SSFLexItem && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_EXTRA_ATTRIBUTES))
		|| (containingNode instanceof SSFPhrase && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_EXTRA_ATTRIBUTES))) {
            removeNonMandatoryAttributes();
        }
        
//        for(int i = 0; i < count; i++)
//	    getAltFSValue(i).clearAnnotation(annoLevelFlags);
    }
    
    @Override
    public void sortAttributes(int sortType)
    {
	Collections.sort(children, FeatureAttributeImpl.getAttributeComparator(sortType));
    }

    @Override
    public int readStringFV(String fs_str) throws Exception
    {
        clear();

        String fvPairs[] = fs_str.split("[\\s]+");

        for (int i = 0; i < fvPairs.length; i++)
        {
            String fvPair = fvPairs[i];

            String fvparts[] = fvPair.split("=");

            if(fvparts.length != 2) {
                continue;
            }

            String f = fvparts[0];
            String v = fvparts[1];

//            f = f.replaceAll("\"", "");
//            f = f.replaceAll("'", "");

//            v = v.replaceAll("\"", "");
//            v = v.replaceAll("'", "");

            v = SSFQueryMatchNode.stripBoundingStrings(v, "'", "'");
            v = SSFQueryMatchNode.stripBoundingStrings(v, "\"", "\"");

            addAttribute(f, v);
        }

        return countAttributes();
    }

    @Override
    public void setAlignmentUnit(AlignmentUnit alignmentUnit)
    {
        Object alignmentObject = alignmentUnit.getAlignmentObject();

        String alignName = "alignedTo";
        String alignVal = "";

        if(alignmentObject instanceof SSFNode)
        {
            Iterator<String> itr = alignmentUnit.getAlignedUnitKeys();

            while(itr.hasNext())
            {
                String key = itr.next();

                AlignmentUnit alignedUnit = alignmentUnit.getAlignedUnit(key);
                SSFNode alignedNode = (SSFNode) alignedUnit.getAlignmentObject();
                alignVal += alignedNode.getAttributeValue("name") + ";";
            }
        }
        else if(alignmentObject instanceof SSFSentence)
        {
            Iterator<String> itr = alignmentUnit.getAlignedUnitKeys();

            while(itr.hasNext())
            {
                String key = itr.next();

                AlignmentUnit alignedUnit = alignmentUnit.getAlignedUnit(key);
                SSFSentence alignedSentence = (SSFSentence) alignedUnit.getAlignmentObject();
                alignVal += alignedSentence.getId() + ";";
            }
        }

        if(alignVal.equals("") == false) {
            int addAttribute = addAttribute(alignName, alignVal);
        }
        else {
            removeAttribute("alignedTo");
        }
    }

    @Override
    public AlignmentUnit loadAlignmentUnit(Object srcAlignmentObject, Object srcAlignmentObjectContainer, Object tgtAlignmentObjectContainer, int parallelIndex)
    {
        FeatureValue alignFV = getAttributeValue("alignedTo");

        if(alignFV == null) {
            return null;
        }

        String alignVal = (String) alignFV.getValue();

        alignVal = SSFQueryMatchNode.stripBoundingStrings(alignVal, "'", "'");

        if(alignVal.charAt(alignVal.length() - 1) == ';') {
            alignVal = alignVal.substring(0, alignVal.length());
        }

        String alignVals[] = alignVal.split(";");

        AlignmentUnit alignmentUnit = new AlignmentUnit();

        if(srcAlignmentObjectContainer instanceof SSFStory)
        {
            SSFStory srcDocument = (SSFStory) srcAlignmentObjectContainer;

            SSFSentence alignmentSentence = (SSFSentence) srcAlignmentObject;

            int alignmentIndex = srcDocument.findSentenceIndex(alignmentSentence.getId());

            if(alignmentIndex < 0) {
                return null;
            }

            alignmentUnit.setAlignmentObject(alignmentSentence);
            alignmentUnit.setParallelIndex(parallelIndex);
            alignmentUnit.setIndex(alignmentIndex);
        }
        else if(srcAlignmentObjectContainer instanceof SSFSentence)
        {
            SSFSentence srcSentence = (SSFSentence) srcAlignmentObjectContainer;

            SSFNode alignmentNode = (SSFNode) srcAlignmentObject;

            int alignmentIndex = srcSentence.findChildIndexByName(alignmentNode.getAttributeValue("name"));

            if(alignmentIndex < 0) {
                return null;
            }

            alignmentUnit.setAlignmentObject(alignmentNode);
            alignmentUnit.setParallelIndex(parallelIndex);
            alignmentUnit.setIndex(alignmentIndex);
        }

        for (int i = 0; i < alignVals.length; i++)
        {
            String string = alignVals[i];

            AlignmentUnit alignedUnit = new AlignmentUnit();

            if(tgtAlignmentObjectContainer instanceof SSFStory)
            {
                SSFStory tgtDocument = (SSFStory) tgtAlignmentObjectContainer;

                SSFSentence alignedSentence = tgtDocument.findSentence(string);

                if(alignedSentence != null)
                {
                    int alignedIndex = tgtDocument.findSentenceIndex(alignedSentence.getId());

                    alignedUnit.setAlignmentObject(alignedSentence);

                    if(parallelIndex == 0) {
                        alignedUnit.setParallelIndex(2);
                    }
                    else if(parallelIndex == 2) {
                        alignedUnit.setParallelIndex(0);
                    }

                    alignedUnit.setIndex(alignedIndex);

                    alignmentUnit.addAlignedUnit(alignedUnit);
                    alignedUnit.addAlignedUnit(alignmentUnit);
                }
            }
            else if(tgtAlignmentObjectContainer instanceof SSFSentence)
            {
                SSFSentence tgtSentence = (SSFSentence) tgtAlignmentObjectContainer;

                SSFNode alignedNode = tgtSentence.findChildByName(string);

                if(alignedNode != null)
                {
                    int alignedIndex = tgtSentence.findChildIndexByName(alignedNode.getAttributeValue("name"));

                    alignedUnit.setAlignmentObject(alignedNode);

                    if(parallelIndex == 0) {
                        alignedUnit.setParallelIndex(2);
                    }
                    else if(parallelIndex == 2) {
                        alignedUnit.setParallelIndex(0);
                    }

                    alignedUnit.setIndex(alignedIndex);

                    alignmentUnit.addAlignedUnit(alignedUnit);
                    alignedUnit.addAlignedUnit(alignmentUnit);
                }
            }
        }

        return alignmentUnit;
    }

    // Equals if the name as well as attributes are equal.
    @Override
    public boolean equals(Object obj)
    {
	if(obj == null) {
            return false;
        }
	
	FeatureStructure fsobj = (FeatureStructure) obj;

	if(getName().equalsIgnoreCase(fsobj.getName()) == false) {
            return false;
        }
	
	int count = countAttributes();
	if(count != fsobj.countAttributes()) {
            return false;
        }

	for (int i = 0; i < count; i++)
	{
	    if(getAttribute(i).equals(fsobj.getAttribute(i)) == false) {
                return false;
            }
	}
	    
	return true;
    }
    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("fsTag"));

        int acount = countAttributes();

        for (int i = 0; i < acount; i++)
        {
            FeatureAttribute fa = getAttribute(i);
            FeatureValue fv = fa.getAltValue(0);

            String aname = fa.getName();

            if(fv != null)
            {
                String value = fv.getValue().toString();
                DOMAttribute attribDOM = new DOMAttribute(new org.dom4j.QName(aname), value);
                domElement.add(attribDOM);
            }
        }

        return domElement;
    }

    @Override
    public String getXML()
    {
        String xmlString = "";

        org.dom4j.dom.DOMElement element = getDOMElement();
        xmlString = element.asXML();

        return "\n" + xmlString + "\n";
    }

    @Override
    public void readXML(Element domElement)
    {
        NamedNodeMap domAttribs = domElement.getAttributes();

        int acount = domAttribs.getLength();

        for (int i = 0; i < acount; i++)
        {
            Node node = domAttribs.item(i);
            String aname = node.getNodeName();
            String value = node.getNodeValue();

            if(aname != null)
            {
                if(value != null) {
                    addAttribute(aname, value);
                }
                else {
                    addAttribute(aname, "");
                }
            }
        }

        // Reorder to get the mandatory attributes in the beginning
        int count = FeatureStructuresImpl.getFSProperties().countMandatoryAttributes();

        for (int i = 0; i < count; i++)
        {
            String mname = FeatureStructuresImpl.getFSProperties().getMandatoryAttribute(i);

            FeatureAttribute fa = searchAttribute(mname, true);

            if(fa != null)
            {
                remove(fa);
                insertAttribute(fa, i);
            }
        }
    }

    @Override
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }

    class ReadRecurseArgs
    {
        String str;
        int position;
        int level;
        FeatureAttributeImpl fa;
        //FeatureStructure parentFS;

        ReadRecurseArgs(String s, int l, int p, FeatureAttributeImpl fa)//, FeatureStructure parent)
        {
            str  = s;
            level = l;
            position = p;
            this.fa =fa;
            // parentFS = parent;
        }
    }
}
