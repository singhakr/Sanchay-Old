/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.corpus.ssf.features.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.query.SSFQueryMatchNode;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.table.SanchayTableModel;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.Pair;
import sanchay.util.UtilityFunctions;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class SimpleSingleFeatureStructure
        implements FeatureStructures, FeatureStructure, SanchayDOMElement {

    protected List<Pair<Integer, Integer>> featureValues;
    
    protected boolean has_mandatory;
    protected int version = 2;
    
    public SimpleSingleFeatureStructure()
    {
         featureValues = new ArrayList<Pair<Integer, Integer>>();
    }

    @Override
    public int addAltFSValue(FeatureStructure f) {
        copy((SimpleSingleFeatureStructure) f);
        
        return 1;
    }

    @Override
    public void clear() {
        featureValues.clear();
    }

    @Override
    public int countAltFSValues() {
        return 1;
    }

    @Override
    public int findAltFSValue(FeatureStructure fs) {
        if(!equals(fs)) {
            return -1;
        }
            
        return 0;
    }

    @Override
    public FeatureStructure getAltFSValue(int num) {
        if(num == 0) {
            return this;
        }
        
        return null;
    }

    @Override
    public SanchayMutableTreeNode getCopy() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDeep() {
        return false;
    }

    @Override
    public String makeStringFV()
    {
        String fvStr = "";

        int fcount = featureValues.size();

        for (int i = 0; i < fcount; i++)
        {
            Pair<Integer, Integer> fv = featureValues.get(i);

            fvStr += SimpleFeatureAttribute.getFeatureString(fv.first)
                    + "='" + SimpleFeatureAttribute.getValueString(fv.second) + "'";

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
    public void modifyAltFSValue(FeatureStructure fs, int index) {
        if(index == 0)
        {
            copy((SimpleSingleFeatureStructure) fs);
        }
    }

    @Override
    public void print(PrintStream ps) {
        ps.println(makeString());
    }

    @Override
    public int readString(String fs_str) throws Exception {
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
                        SimpleFeatureAttribute a = new SimpleFeatureAttribute();
                        a.setName(fsp.getMandatoryAttribute(j));

                        a.setValue(ma[j]);
                        
                        addAttribute(a);
                    }

                    int count  = fsp.countMandatoryAttributes();
                    
                    if(ma.length < count)
                    {
                        for (int j = 0; j < count - ma.length; j++)
                        {
                            SimpleFeatureAttribute a = new SimpleFeatureAttribute();
                            a.setName(fsp.getMandatoryAttribute(ma.length + j));

                            a.setValue("");
                            addAttribute(a);
                        }
                    }
                }
                else
                {
                    SimpleFeatureAttribute a = new SimpleFeatureAttribute();
                    a.setName(av[0]);

                    a.setValue(av[1]);

                    addAttribute(a);
                }
            }
        }

        checkAndSetHasMandatory();
        
        return pos;
    }

    @Override
    public FeatureStructure removeAltFSValue(int num) {
        if(num == 0)
        {
            FeatureStructure fs = (FeatureStructure) clone();
            clear();
            
            return fs;
        }
        
        return null;
    }

    @Override
    public void clearAnnotation(long annoLevelFlags, SSFNode containingNode)
    {
	if((containingNode instanceof SSFLexItem && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_MANDATORY_ATTRIBUTES))
		|| (containingNode instanceof SSFPhrase && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_MANDATORY_ATTRIBUTES))) {
            removeMandatoryAttributes();
        }

	if((containingNode instanceof SSFLexItem && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_EXTRA_ATTRIBUTES))
		|| (containingNode instanceof SSFPhrase && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_EXTRA_ATTRIBUTES))) {
            removeNonMandatoryAttributes();
        }
    }

    @Override
    public void setToEmpty() {
        clear();
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
    public String[] getOneOfAttributeValues(String attibNames[])
    {
        String ret[] = new String[2];

        for (int i = 0; i < attibNames.length; i++)
        {
            String attibName = attibNames[i];

            FeatureAttribute fa = getAttribute(attibName);

            if(fa == null) {
                continue;
            }
            else if(fa.countAltValues() == 0) {
                continue;
            }
            else
            {
                FeatureValue fv = fa.getAltValue(0);

                if(fv == null) {
                    continue;
                }
                else
                {
                    ret[0] = attibName;
                    ret[1] = fv.makeString();
                    return ret;
                }
            }
        }

        return null;
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
            fa = new SimpleFeatureAttribute(attibName, val);
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
    public void concatenateAttributeValue(String attibName, String val, String sep) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAllAttributeValues(String attibName, String val) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void hideAttribute(String aname) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unhideAttribute(String aname) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAttribute getAttribute(String attibName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureStructures getFeatureStructures(FeatureStructures fss, AlignmentUnit alignmentUnit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAlignmentUnit(AlignmentUnit alignmentUnit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AlignmentUnit loadAlignmentUnit(Object srcAlignmentObject, Object srcAlignmentObjectContainer, Object tgtAlignmentObjectContainer, int parallelIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(MutableTreeNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setUserObject(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeFromParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TreeNode getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getIndex(TreeNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowsChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int addAttribute(FeatureAttribute a) {
        SimpleFeatureAttribute sfa = (SimpleFeatureAttribute) a;
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(sfa.getFeatureIndex(), sfa.getValueIndex());
        featureValues.add(pair);
        
        return featureValues.size();
    }

    @Override
    public int addAttribute(FeatureAttribute a, String p) {
        return addAttribute(a);
    }

    @Override
    public int addAttribute(String name, String value) {
        Pair<Integer, Integer> pair
                = new Pair<Integer, Integer>(SimpleFeatureAttribute.getFeatureIndex(name, true),
                SimpleFeatureAttribute.getValueIndex(value, true));
        featureValues.add(pair);
        
        return featureValues.size();
    }

    @Override
    public void addMandatoryAttributes()
    {
	// Empty mandatory attributes values
	int mcount = FeatureStructuresImpl.getFSProperties().countMandatoryAttributes();
//	if(count == 0)
	if(hasMandatoryAttribs() == false)
	{
	    for(int i = 0; i < mcount; i++)
	    {
                String name = FeatureStructuresImpl.getFSProperties().getMandatoryAttribute(i);
                
                Pair<Integer, Integer> pair
                        = new Pair<Integer, Integer>(SimpleFeatureAttribute.getFeatureIndex(name, true),
                        SimpleFeatureAttribute.getValueIndex("", true));

                featureValues.add(i, pair);
	    }
	}

        has_mandatory = true;
    }

    @Override
    public int countAttributes() {
        return featureValues.size();
    }

    @Override
    public int findAttribute(FeatureAttribute a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAttribute getOneOfAttributes(String[] names) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAttribute getAttribute(int index) {
        Pair<Integer, Integer> attrib = featureValues.get(index);
        SimpleFeatureAttribute f = new SimpleFeatureAttribute(attrib.first, attrib.second);
        
        return f;
    }

    @Override
    public List<String> getAttributeNames(String p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureValue getAttributeValueByIndex(String p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FeatureValue> getAttributeValues(String p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SanchayTableModel getFeatureTable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FeatureAttribute> getPaths(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasMandatoryAttribs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void hasMandatoryAttribs(boolean m) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public FeatureStructure merge(FeatureStructure f1, FeatureStructure f2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void modifyAttribute(FeatureAttribute a, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void modifyAttribute(FeatureAttribute a, int index, String p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void modifyAttributeValue(FeatureValue fv, String p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void modifyAttributeValue(FeatureValue fv, int attribIndex, int altValIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void prune() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAllAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAttribute removeAttribute(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAttribute removeAttribute(String p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAttribute removeAttribute(int index, String p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeMandatoryAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeNonMandatoryAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public List<FeatureAttribute> searchAttributes(String name, boolean exactMatch)
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

            FeatureValue fv = fa.getAltValue(0);

            if(yes)
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

    @Override
    public void setFeatureTable(SanchayTableModel ft) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setName(String n) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Object v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureStructure unify(FeatureStructure f1, FeatureStructure f2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sortAttributes(int sortType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DOMElement getDOMElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getXML() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void readXML(Element domElement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void printXML(PrintStream ps) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
    
    @Override
    public Object clone()
    {
        SimpleSingleFeatureStructure obj = null;
        
        try {
            obj = (SimpleSingleFeatureStructure) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(SimpleSingleFeatureStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        obj.featureValues = (List<Pair<Integer, Integer>>) ((ArrayList<Pair<Integer, Integer>>) featureValues).clone();
        obj.has_mandatory = has_mandatory;
        
        return obj;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof SimpleSingleFeatureStructure)) {
            return false;
        }
        
        if(has_mandatory != ((SimpleSingleFeatureStructure) obj).has_mandatory) {
            return false;
        }

        if(featureValues.equals(((SimpleSingleFeatureStructure) obj).featureValues)) {
            return false;
        }

        return true;
    }
    
    public void copy(SimpleSingleFeatureStructure ssfs)
    {
        has_mandatory = ssfs.has_mandatory;
        featureValues.clear();
        featureValues.addAll(ssfs.featureValues);
    }

    @Override
    public int readStringFV(String fs_str) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getAttributeValues() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAttribute searchOneOfAttributes(String[] names, boolean exactMatch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
