/*
 * Created on Aug 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.features.impl;

import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.tree.SanchayMutableTreeNode;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FeatureValueImpl extends SanchayMutableTreeNode
        implements FeatureValue, Cloneable, Serializable
{
    transient java.util.ResourceBundle bundle = GlobalProperties.getResourceBundle(); // NOI18N

    /**
     * 
     */
    
    // Leaf node with a String (value) as the user object
    protected List<Integer> valueIndices;

    static Index<String> valueIndex = new HashIndex<String>();    

    public static String VALUE_SEPARATOR = "__";

    public FeatureValueImpl() {
        super();
    }

    public FeatureValueImpl(Object userObject) {
        super();
        valueIndices = getIndices((String) userObject, true);
    }
    
    public FeatureValueImpl(Object userObject, boolean allowsChildren) {
        super(null, allowsChildren);
        valueIndices = getIndices((String) userObject, true);
    }
    
    @Override
    public Object getValue()
    {
        return getString(valueIndices);
    }
    
    @Override
    public void setValue(Object v)
    {
        valueIndices = getIndices(v.toString(), true);
    }

    @Override
    public String getUserObject() 
    {
        return getString(valueIndices);
    }

    @Override
    public void setUserObject(Object userObject) 
    {
        valueIndices = getIndices(userObject.toString(), true);
    }

    public long getVocabularySize()
    {
        long vocabularySize = valueIndex.size();
        
        return vocabularySize;
    }
    
    public static List<Integer> getIndices(String wds, boolean add)
    {
        String parts[] = wds.split(VALUE_SEPARATOR);
        
        List<Integer> indices = new ArrayList<Integer>(parts.length);
        
        for (int i = 0; i < parts.length; i++) {
            int wi = valueIndex.indexOf(parts[i], add);
            
            indices.add(wi);
        }
        
        return indices;
    }

    public static String getString(List<Integer> wdIndices)
    {
        String str = "";
        
        int i = 0;
        for (Integer wi : wdIndices) {
            if(i == 0)
            {
                str = valueIndex.get(wi);
            }
            else
            {
                str += VALUE_SEPARATOR + valueIndex.get(wi);
            }
            
            i++;
        }
        
        return str;
    }

    @Override
    public boolean isFeatureStructure()
    {
	return false;
    }
    
    @Override
    public int readString(String str) throws Exception
    {
	setValue(str);
	return 0;
    }
	
    @Override
    public String makeString()
    {
        if(getUserObject() == null) {
            return null;
        }
        
        return getUserObject().toString();
    }

    @Override
    public String makeStringForRendering()
    {
        return makeString();
    }
	
    @Override
    public String toString()
    {
        return (String) makeString();
    }

    @Override
    public Object clone()
    {
        FeatureValueImpl obj = (FeatureValueImpl) super.clone();

        return obj;
    }

    @Override
    public void print(PrintStream ps)
    {
        ps.println(makeString());
    }

    @Override
    public void clear()
    {
        setUserObject("");
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof FeatureValueImpl)) {
            return false;
        }
        
	if(obj == null && getValue() == null) {
            return true;
        }

        if(obj == null || getValue() == null) {
            return false;
        }
	
	if(((String) getValue()).equalsIgnoreCase( (String) ((FeatureValue) obj).getValue())) {
            return true;
        }
	
	return false;
    }

    public static void main(String[] args) {
    }
}
