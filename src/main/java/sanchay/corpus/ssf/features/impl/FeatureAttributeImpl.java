/*
 * Created on Jun 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.features.impl;

import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Comparator;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.tree.SanchayMutableTreeNode;


/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FeatureAttributeImpl extends SanchayMutableTreeNode
        implements FeatureAttribute, Serializable {

    /**
     * 
     */

    // Children could be FeatureValueImpl or FeatureStructureImpl
    private boolean hide = false;

    private int nameIndex;

    static Index<String> namesIndex = new HashIndex<String>();    

    public FeatureAttributeImpl() {
        super();
    }

    public FeatureAttributeImpl(Object userObject) {
        super();
        nameIndex = getIndex((String) userObject, true);
    }
    
    public FeatureAttributeImpl(Object userObject, boolean allowsChildren) {
        super(null, allowsChildren);
        nameIndex = getIndex((String) userObject, true);
    }

    @Override
    public String getName() 
    {
        return getString(nameIndex);
    }

    @Override
    public void setName(String n) 
    {
        nameIndex = getIndex(n, true);
    }

    public long getNameVocabularySize()
    {
        long vocabularySize = namesIndex.size();
        
        return vocabularySize;
    }
    
    public static int getIndex(String wd, boolean add)
    {
        int wi = namesIndex.indexOf(wd, add);
        
        return wi;
    }

    public static String getString(Integer wdIndex)
    {
        return namesIndex.get(wdIndex);
    }

    @Override
    public Object getUserObject()
    {
        return getName();
    }

    @Override
    public int countAltValues()
    {
        return getChildCount();
    }

    @Override
    public int addAltValue(FeatureValue v)
    {
        add((SanchayMutableTreeNode) v);
        return getChildCount();
    }

    @Override
    public int findAltValue(FeatureValue v) 
    {
        return getIndex((FeatureValueImpl) v);
    }

    @Override
    public FeatureValue getAltValue(int index) 
    {
        return (FeatureValue) getChildAt(index);
    }

    @Override
    public void modifyAltValue(FeatureValue v, int index)
    {
        insert((FeatureValueImpl) v, index);
        remove(index + 1);
    }

    @Override
    public FeatureValue removeAltValue(int index) 
    {
        FeatureValue rem = getAltValue(index);
        remove(index);
        
        return rem;
    }

    @Override
    public void removeAllAltValues() 
    {
        int count = countAltValues();
        
        for (int i = 0; i < count; i++) {
            removeAltValue(i);
        }
    }

    @Override
    public void hideAttribute()
    {
        hide = true;
    }

    @Override
    public void unhideAttribute()
    {
        hide = false;
    }

    @Override
    public boolean isHiddenAttribute()
    {
        return hide;
    }

    @Override
    public String makeString(boolean mandatory)
    {
        String str = "";
        FSProperties fsp = FeatureStructuresImpl.getFSProperties();

        if(mandatory == true)
        {
            str += getAltValue(0).makeString();

            for(int i = 1; i < countAltValues(); i++) {
                str += fsp.getProperties().getPropertyValueForPrint("attribOR") + getAltValue(i).makeString();
            }

            return str;
        }
        else
        {
            String valStr = getAltValue(0).makeString();

            if(valStr == null) {
                valStr = "";
            }
            
            if(valStr.equals("'") || valStr.equals("''") || (valStr.startsWith("'") == false || valStr.endsWith("'") == false)) {
                str +=  getName() + fsp.getProperties().getPropertyValueForPrint("attribEquate") + "'" + valStr + "'";
            }
            else {
                str +=  getName() + fsp.getProperties().getPropertyValueForPrint("attribEquate") + valStr;
            }

            for(int i = 1; i < countAltValues(); i++)
            {
                valStr = getAltValue(i).makeString();
                
                if(valStr.equals("'") || valStr.equals("''") || (valStr.startsWith("'") == false || valStr.endsWith("'") == false)) {
                    str += fsp.getProperties().getPropertyValueForPrint("attribOR") + "'" + valStr + "'";
                }
                else {
                    str += fsp.getProperties().getPropertyValueForPrint("attribOR") + valStr;
                }
            }
        }
        return str;
    }

    // other methods

    @Override
    public void print(PrintStream ps, boolean mandatory)
    {
        ps.println(makeString(mandatory));
    }

    @Override
    public void clear()
    {
        nameIndex = -1;
        removeAllChildren();
    }
    
    public static Comparator getAttributeComparator(int sortType)
    {
	switch(sortType)
	{
	    case SORT_BY_NAME:
		return new Comparator()
		{
                    @Override
		    public int compare(Object one, Object two)
		    {
			return ( ((FeatureAttribute) one).getName().compareToIgnoreCase( ((FeatureAttribute) two).getName() ) );
		    }
		};
	}
	
	return null;
    }

    // Equal if the name as well as all the alt values are equal.
    @Override
    public boolean equals(Object obj)
    {
	if(obj == null || !(obj instanceof FeatureAttributeImpl)) {
            return false;
        }
	
	FeatureAttribute aobj = (FeatureAttribute) obj;
	
	if(getName().equalsIgnoreCase(aobj.getName()) == false) {
            return false;
        }
	
	int count = countAltValues();

        if(count != aobj.countAltValues()) {
            return false;
        }

	for (int i = 0; i < count; i++)
	{
	    if(getAltValue(i).equals(aobj.getAltValue(i)) == false) {
                return false;
            }
	}
	
	return true;
    }

    public static void main(String[] args) {
        FeatureAttribute attrib = new FeatureAttributeImpl();
    }
}
