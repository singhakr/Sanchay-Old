/*
 * Created on Jun 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package sanchay.corpus.ssf.features.impl;

import java.io.*;
import java.util.*;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sanchay.GlobalProperties;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.SSFCorpus;

import sanchay.tree.*;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.query.SSFQueryMatchNode;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.xml.XMLProperties;
import sanchay.util.UtilityFunctions;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class FeatureStructuresImpl extends SanchayMutableTreeNode
        implements FeatureStructures, SanchayDOMElement, Serializable
{
    private static FSProperties fsProps;

    // Children could be of type FeatureStructureImpl

    public FeatureStructuresImpl() {
        super();
    }

    public FeatureStructuresImpl(Object userObject) {
        super(userObject);
    }
    
    public FeatureStructuresImpl(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }
	
    @Override
    public int countAltFSValues() // get_num_fs($ref_to_array)
    {
        return getChildCount();
    }

    @Override
    public int addAltFSValue(FeatureStructure f)
    {
        add((FeatureStructureImpl) f);
        return getChildCount();
    }

    @Override
    public FeatureStructure getAltFSValue(int num) 
    {
        return (FeatureStructureImpl) getChildAt(num);
    }

    @Override
    public void modifyAltFSValue(FeatureStructure fs, int index)
    {
        insert((FeatureStructureImpl) fs, index);
        remove(index + 1);
    }

    @Override
    public int findAltFSValue(FeatureStructure fs) 
    {
        return getIndex((FeatureStructureImpl) fs);
    }

    @Override
    public FeatureStructure removeAltFSValue(int num) 
    {
        FeatureStructureImpl rem = (FeatureStructureImpl) getAltFSValue(num);
        remove(num);

        return rem;
    }

    public static FSProperties getFSProperties()
    {
	if(fsProps == null) {
            loadFSProperties();
        }

	return fsProps;
    }

    public static void loadFSProperties()
    {
	fsProps = new FSProperties();

	try {
	    fsProps.read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                "UTF-8");
	} catch (FileNotFoundException ex) {
		ex.printStackTrace();
	} catch (IOException ex) {
		ex.printStackTrace();
	}
    }

    public static void setFSProperties(FSProperties fsp)
    {
        fsProps = fsp;
    }

    // read_FS($string)
    @Override
    public int readString(String fs_str) throws Exception
    {
        clear();
        
        String fsOr = getFSProperties().getProperties().getPropertyValueForPrint("fsOR");
        String fsStrings[] = fs_str.split("[|]");
        
        for (int i = 0; i < fsStrings.length; i++)
        {
            if(fsStrings[i] != null && fsStrings[i].equals("") == false)
            {
                FeatureStructure fs = new FeatureStructureImpl();
                int pos = ((FeatureValue) fs).readString(fsStrings[i]);
                
                fs.checkAndSetHasMandatory();

                if(pos != -1) {
                    addAltFSValue(fs);
                }
            }
        }

//        int fslen = fs_str.length();
//        int pos = 0;
//        String str = "";
//
//        while(pos < fslen)
//        {
//            FeatureStructure fs = new FeatureStructureImpl();
//            pos = ((FeatureValue) fs).readString(fs_str);
//            
//            if ( pos == -1)
//            {
//                return -1;
//            }
//            else
//            {	
//                pos++;
//            }
//
//            addAltFSValue(fs);
//        }

        return 0;
    }

    // printFS_SSF($fs)

    @Override
    public void print(PrintStream ps)
    {
        ps.println(makeString());
    }

    @Override
    public String makeStringFV()
    {
        String fvStr = "";

        if(countAltFSValues() == 0) {
            return fvStr;
        }

        return getAltFSValue(0).makeStringFV();
    }

    @Override
    public String makeString()
    {
        int version = 2;

        if(getDepth() > 3) {
            version = 1;
        }
        
        String str = "";

        for (int i = 0; i < countAltFSValues(); i++)
        {
            FeatureStructureImpl fs = (FeatureStructureImpl) getAltFSValue(i);
            fs.setVersion(version);

            str += ((FeatureValue) getAltFSValue(i)).makeString();

            if(i < countAltFSValues() - 1)
            {
                str += getFSProperties().getProperties().getPropertyValueForPrint("fsOR");
            }
        }

        return str;
    }

    @Override
    public String makeStringForRendering()
    {
        int version = 2;

        if(getDepth() > 3) {
            version = 1;
        }

        String str = "";

        for (int i = 0; i < countAltFSValues(); i++)
        {
            FeatureStructureImpl fs = (FeatureStructureImpl) getAltFSValue(i);
            fs.setVersion(version);

            str += ((FeatureValue) getAltFSValue(i)).makeStringForRendering();

            if(i < countAltFSValues() - 1)
            {
                str += getFSProperties().getProperties().getPropertyValueForPrint("fsOR");
            }
        }

        return str;
    }

    @Override
    public SanchayMutableTreeNode getCopy() throws Exception
    {
        String str = makeString();
        
        FeatureStructuresImpl fss = new FeatureStructuresImpl();
        fss.readString(str);

        return fss;
    }

    @Override
    public List<String> getAttributeNames()
    {
        if(countAltFSValues() == 0) {
            return null;
        }

        return getAltFSValue(0).getAttributeNames();
    }

    @Override
    public List<String> getAttributeValues()
    {
        if(countAltFSValues() == 0) {
            return null;
        }

        return getAltFSValue(0).getAttributeValues();
    }

    @Override
    public List<String> getAttributeValuePairs()
    {
        if(countAltFSValues() == 0) {
            return null;
        }

        return getAltFSValue(0).getAttributeValuePairs();
    }

    @Override
    public String getAttributeValueString(String attibName)
    {
        if(countAltFSValues() == 0) {
            return null;
        }

        FeatureStructure fs = getAltFSValue(0);

        if(fs == null) {
            return null;
        }

        FeatureAttribute fa = fs.getAttribute(attibName);

        if(fa == null) {
            return null;
        }
        else if(fa.countAltValues() == 0) {
            return null;
        }
        else
        {
            FeatureValue fv = fa.getAltValue(0);

            if(fv == null) {
                return null;
            }
            else {
                return fv.makeString();
            }
        }
    }

    @Override
    public String[] getOneOfAttributeValues(String attibNames[])
    {
        if(countAltFSValues() == 0) {
            return null;
        }

        FeatureStructure fs = getAltFSValue(0);

        if(fs == null) {
            return null;
        }

        String ret[] = new String[2];

        for (int i = 0; i < attibNames.length; i++)
        {
            String attibName = attibNames[i];

            FeatureAttribute fa = fs.getAttribute(attibName);

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
    public void setAllAttributeValues(String attibName, String val)
    {
        int count  = countAltFSValues();
        
        if(count <= 0)
        {
            FeatureStructure fs = new FeatureStructureImpl();
            addAltFSValue(fs);
        }

        for (int i = 0; i < count; i++)
        {
            FeatureStructure fs = getAltFSValue(i);

            if(fs == null)
            {
                fs = new FeatureStructureImpl();
                addAltFSValue(fs);
            }

            FeatureAttribute fa = fs.getAttribute(attibName);

            if(fa == null)
            {
                fa = new FeatureAttributeImpl();
                fa.setName(attibName);
                fs.addAttribute(fa);
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

            fv.setValue(val);
        }
    }

    @Override
    public void setAttributeValue(String attibName, String val)
    {
        if(countAltFSValues() <= 0)
        {
            FeatureStructure fs = new FeatureStructureImpl();
            addAltFSValue(fs);
        }

        FeatureStructure fs = getAltFSValue(0);

        if(fs == null)
        {
            fs = new FeatureStructureImpl();
            addAltFSValue(fs);
        }
        
//        if( !(fs.hasMandatoryAttribs() == false && getFSProperties().isMandatory(attibName) == true) )
        if( fs.hasMandatoryAttribs() == false && getFSProperties().isMandatory(attibName) == true )
        {
            fs.addMandatoryAttributes();
        }

        FeatureAttribute fa = fs.getAttribute(attibName);

        if(fa == null)
        {
            fa = new FeatureAttributeImpl();
            fa.setName(attibName);
            fs.addAttribute(fa);
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
    public void concatenateAttributeValue(String attibName, String val, String sep)
    {
        val = SSFQueryMatchNode.stripQuotes(val);
        
        String oldVal = getAttributeValueString(attibName);

        if(oldVal == null)
        {
            oldVal = "";
        }

        if(oldVal.endsWith(sep) == false)
        {
            oldVal += sep;
        }

        if(countAltFSValues() <= 0)
        {
            FeatureStructure fs = new FeatureStructureImpl();
            addAltFSValue(fs);
        }

        FeatureStructure fs = getAltFSValue(0);

        if(fs == null)
        {
            fs = new FeatureStructureImpl();
            addAltFSValue(fs);
        }

//        if( !(fs.hasMandatoryAttribs() == false && getFSProperties().isMandatory(attibName) == true) )
        if( fs.hasMandatoryAttribs() == false && getFSProperties().isMandatory(attibName) == true )
        {
            fs.addMandatoryAttributes();
        }

        FeatureAttribute fa = fs.getAttribute(attibName);

        if(fa == null)
        {
            fa = new FeatureAttributeImpl();
            fa.setName(attibName);
            fs.addAttribute(fa);
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

        fv.setValue(oldVal + val + sep);
    }

    @Override
    public void hideAttribute(String aname)
    {
        int count  = countAltFSValues();

        for (int i = 0; i < count; i++)
        {
            FeatureStructure fs = getAltFSValue(i);

            if(fs != null) {
                fs.hideAttribute(aname);
            }
        }
    }

    @Override
    public void unhideAttribute(String aname)
    {
        int count  = countAltFSValues();

        for (int i = 0; i < count; i++)
        {
            FeatureStructure fs = getAltFSValue(i);

            if(fs != null) {
                fs.unhideAttribute(aname);
            }
        }
    }

    @Override
    public FeatureAttribute getAttribute(String attibName)
    {
        if(countAltFSValues() == 0) {
            return null;
        }

        FeatureStructure fs = getAltFSValue(0);

        if(fs == null) {
            return null;
        }

        FeatureAttribute fa = fs.getAttribute(attibName);
        
        return fa;
    }

    @Override
    public void clear()
    {
        removeAllChildren();
    }

    @Override
    public void setToEmpty()
    {
        int count = countAltFSValues();
        
        for(int i = 1; i < count; i++)
        {
            removeAltFSValue(i);
        }

	if(count >= 1) {
            getAltFSValue(0).setToEmpty();
        }
	else
	{
	    FeatureStructureImpl fs = new FeatureStructureImpl();
	    fs.setToEmpty();
	    addAltFSValue(fs);
	}
    }

    @Override
    public void clearAnnotation(long annoLevelFlags, SSFNode containingNode)
    {
        int count = countAltFSValues();

        if(UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.ALL_EXCEPT_THE_FIRST_FS))
        {
            for (int i = 1; i < count; i++) {
                removeAltFSValue(1);
            }
        }
        else if(UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.PRUNE_THE_FS))
        {
            for (int i = 0; i < count; i++)
            {
                FeatureStructure fs = getAltFSValue(i);
                FeatureAttribute fa = fs.getAttribute("cat");

                if(fa != null)
                {
                    FeatureValue fv = fa.getAltValue(0);
                    
                    if(fv != null && fv instanceof FeatureValueImpl
                            && ((String) fv.getValue()).equalsIgnoreCase(containingNode.getName()) == false
                            && ((String) fv.getValue()).equalsIgnoreCase("unk") == false
                            && count > 1)
                    {
                        removeAltFSValue(i);
                        count--;
                    }
                }
                
            }
        }

        count = countAltFSValues();
        
        for(int i = 0; i < count; i++) {
            getAltFSValue(i).clearAnnotation(annoLevelFlags, containingNode);
        }
    }
    
    @Override
    public boolean isDeep()
    {
	if(countAltFSValues() <= 0) {
            return false;
        }

	if(countAltFSValues() > 1) {
            return true;
        }

	FeatureStructure fs = getAltFSValue(0);
	
	int count = fs.countAttributes();
	
	for(int i = 0; i < count; i++)
	{
	    FeatureAttribute fa = fs.getAttribute(i);
	    
	    int vcount = fa.countAltValues();
	    
	    if(vcount > 1) {
                return true;
            }

	    if(fa.getAltValue(0).getClass().getName().endsWith("FeatureStructureImpl")) {
                return true;
            }
	}
	
	return false;
    }

    // Equal if all the children are equal.
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof FeatureStructuresImpl)) {
            return false;
        }
        
	if(obj == null) {
            return false;
        }
	
	FeatureStructures fssobj = (FeatureStructures) obj;

	int count = countAltFSValues();
	if(count != fssobj.countAltFSValues()) {
            return false;
        }

	for (int i = 0; i < count; i++)
	{
	    if(getAltFSValue(i).equals(fssobj.getAltFSValue(i)) == false) {
                return false;
            }
	}
	    
	return true;
    }

    @Override
    public String toString()
    {
        return makeString();
    }

    @Override
    public int readStringFV(String fs_str) throws Exception
    {
        clear();

        FeatureStructure fs = new FeatureStructureImpl();

        addAltFSValue(fs);

        fs.readStringFV(fs_str);

        return fs.countAttributes();
    }

    @Override
    public FeatureStructures getFeatureStructures(FeatureStructures fss, AlignmentUnit alignmentUnit)
    {
        if(fss == null)
        {
            fss = new FeatureStructuresImpl();

            FeatureStructure fs = new FeatureStructureImpl();
            
            fss.addAltFSValue(fs);
        }

        fss.getAltFSValue(0).setAlignmentUnit(alignmentUnit);

        return fss;
    }

    @Override
    public void setAlignmentUnit(AlignmentUnit alignmentUnit)
    {
        if(countAltFSValues() == 0)
        {
            FeatureStructure fs = new FeatureStructureImpl();

            addAltFSValue(fs);
        }

        getAltFSValue(0).setAlignmentUnit(alignmentUnit);
    }

    @Override
    public AlignmentUnit loadAlignmentUnit(Object srcAlignmentObject, Object srcAlignmentObjectContainer, Object tgtAlignmentObjectContainer, int parallelIndex)
    {
        if(countAltFSValues() == 0) {
            return null;
        }

        return getAltFSValue(0).loadAlignmentUnit(srcAlignmentObject, srcAlignmentObjectContainer, tgtAlignmentObjectContainer, parallelIndex);
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("fssTag"));

//        domElement.setName(GlobalProperties.getIntlString("predicate"));

        int count = countAltFSValues();

        for (int i = 0; i < count; i++)
        {
            FeatureStructureImpl fs = (FeatureStructureImpl) getAltFSValue(i);

            DOMElement idomElement = fs.getDOMElement();

            domElement.add(idomElement);
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
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("fsTag")))
                {
                    FeatureStructureImpl fs = new FeatureStructureImpl();
                    fs.readXML(element);
                    addAltFSValue(fs);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }

    public static void main(String[] args) {
        
        FeatureStructures fss = new FeatureStructuresImpl();
//        String test = "<af=nirupam,n,m,s,3,0,,/B1='pratap'/af1=ppratap/af2=chandan/af3=nirupam/af4=<pf1=aka/pf2=<nf1=aka3/nf3=aka5/nf4=<lp2=<pr2=pr4>>>>/af5=aka6/af6=aka8>";
//        String test = "<fs af='पाकिस्तान,n,m,sg,,d,,' dre=k1:2>|<fs af='पाकिस्तान,n,m,pl,,d,,' dre=k2:2 name='1'>|<fs af='पाकिस्तान,n,m,sg,,o,,'>|<fs af='पाकिस्तान,n,m,pl,,o,,' sem=NE>";
        String test = "<fs af='पाकिस्तान,n,m,sg,,d,,'/dre=k1:2/name=3>|<fs af='पाकिस्तान,n,m,pl,,d,,' dre=k2:2 name='1'>|<fs af='पाकिस्तान,n,m,sg,,o,,'>|<fs af='पाकिस्तान,n,m,pl,,o,,' sem=NE>";
//        String test = "<name='1'/drel='nmod_relc:6'>";
//        String test = "<fs af='का,n,m,sg,,o,,' poslcat='NM'>|<fs af='का,n,m,pl,,d,,s' poslcat='NM'>|<fs af='का,n,m,pl,,o,,' poslcat='NM'>";
//        String test = "<FeatureStructures><FeatureStructure lex=\"पाकिस्तान\" cat=\"n\" gend=\"m\" num=\"sg\" pers=\"\" case=\"d\" vib=\"\" tam=\"\" dre=\"k1:2\" name=\"3\"/><FeatureStructure lex=\"पाकिस्तान\" cat=\"n\" gend=\"m\" num=\"pl\" pers=\"\" case=\"d\" vib=\"\" tam=\"\" dre=\"k2:2\" name=\"1\"/><FeatureStructure lex=\"पाकिस्तान\" cat=\"n\" gend=\"m\" num=\"sg\" pers=\"\" case=\"o\" vib=\"\" tam=\"\"/><FeatureStructure lex=\"पाकिस्तान\" cat=\"n\" gend=\"m\" num=\"pl\" pers=\"\" case=\"o\" vib=\"\" tam=\"\" sem=\"NE\"/></FeatureStructures>";
        
        try
        {
            FSProperties fsp = new FSProperties();
            FeatureStructuresImpl.setFSProperties(fsp);
            
            fsp.read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                    GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                    GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
//          fss.read("< af=programme,n,m,p,3,0,,/drel=varg:watching/role=obj:watching/head=2>";
//            fss.readString(test);
//            ((SanchayDOMElement) fss).readXML(test);
            fss.print(System.out);
            
            test = fss.makeString();
            fss = new FeatureStructuresImpl();
            fss.readString(test);
            fss.print(System.out);
            
            test = fss.makeString();
            fss = new FeatureStructuresImpl();
            fss.readString(test);
            fss.print(System.out);

            ((SanchayDOMElement) fss).printXML(System.out);
            
//            fss.getAltFSValue(0).getAttribute("af4@0.pf2@0.nf4@0.lp2.pr2@0").print(System.out, false);
//          fss.getAltFSValue(0).getAttribute("af4.pf2.nf4").print(System.out, fsp, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        /*
        attrib.setName ("namef");
        attrib.addAltValue("asdasdad");
        attrib.addAltValue("asdad");
        attrib.addAltValue("sdfdghgd");
        System.out.println(attrib.getAltValue(2));
        */
    }
}
