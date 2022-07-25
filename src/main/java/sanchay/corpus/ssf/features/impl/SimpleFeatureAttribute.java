/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.corpus.ssf.features.impl;

import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureValue;

/**
 *
 * @author anil
 */
public class SimpleFeatureAttribute implements FeatureAttribute, FeatureValue {
    
    protected int feature;
    protected int value;

    protected boolean hide = false;
    
    static Index<String> featureIndex = new HashIndex<String>();
    static Index<String> valueIndex = new HashIndex<String>();
    
    public SimpleFeatureAttribute()
    {        
    }
    
    public SimpleFeatureAttribute(int f, int v)
    {        
        feature = f;
        value = v;
    }
    
    public SimpleFeatureAttribute(String f, String v)
    {        
        feature = getFeatureIndex(v, true);
        value = getValueIndex(v, true);
    }
    
    public int getFeatureIndex()
    {
        return feature;
    }
    
    public int getValueIndex()
    {
        return value;
    }
    
    public static int getFeatureIndex(String wd, boolean add)
    {
        int fi = featureIndex.indexOf(wd, add);
        
        return fi;
    }

    public static String getFeatureString(Integer fIndex)
    {
        return featureIndex.get(fIndex);
    }
    
    public static int getValueIndex(String wd, boolean add)
    {
        int vi = valueIndex.indexOf(wd, add);
        
        return vi;
    }

    public static String getValueString(Integer vIndex)
    {
        return valueIndex.get(vIndex);
    }
    
    @Override
    public int addAltValue(FeatureValue v) {
        value = getValueIndex((String) v.getValue(), true);
        
        return 1;
    }

    @Override
    public void clear() {
        value = -1;
    }

    @Override
    public int countAltValues() {
        return 1;
    }

    @Override
    public int findAltValue(FeatureValue v) {
        return 0;
    }

    @Override
    public FeatureValue getAltValue(int index) {
        if(index == 0) {
            return this;
        }
        
        return null;
    }

    @Override
    public String getName() {
        return getFeatureString(feature);
    }

    @Override
    public String makeString(boolean mandatory)
    {
        String str = "";
        FSProperties fsp = FeatureStructuresImpl.getFSProperties();

        if(mandatory == true)
        {
            return getValueString(value);
        }
        else
        {
            String valStr = getValueString(value);
            
            if(valStr.equals("'") || valStr.equals("''") || (valStr.startsWith("'") == false || valStr.endsWith("'") == false)) {
                str +=  getName() + fsp.getProperties().getPropertyValueForPrint("attribEquate") + "'" + valStr + "'";
            }
            else {
                str +=  getName() + fsp.getProperties().getPropertyValueForPrint("attribEquate") + valStr;
            }
        }

        return str;
    }

    @Override
    public void modifyAltValue(FeatureValue v, int index) {
        if(index == 0)
        {
            setValue(v);
        }
    }

    @Override
    public void print(PrintStream ps, boolean mandatory) {
        ps.print(makeString(mandatory));
    }

    @Override
    public void removeAllAltValues() {
        feature = -1;
        value = -1;
    }

    @Override
    public FeatureValue removeAltValue(int index) {
        if(index == 0)
        {
            feature = -1;
            value = -1;
            
            return this;
        }
        
        return null;
    }

    @Override
    public void hideAttribute() {
        hide = true;
    }

    @Override
    public void unhideAttribute() {
        hide = false;
    }

    @Override
    public boolean isHiddenAttribute() {
        return hide;
    }

    @Override
    public void setName(String n) {
        feature = getFeatureIndex(n, true);
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(int index) {
        if(index == 0)
        {
            feature = -1;
            value = -1;            
        }
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
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return null;
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
    public Object clone()
    {
        SimpleFeatureAttribute obj = null;
        
        try {
            obj = (SimpleFeatureAttribute) super.clone();
            
            obj.feature = feature;
            obj.value = value;
            
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(SimpleFeatureAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return obj;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof SimpleFeatureAttribute))
        {
            return false;
        }
        
        if(((SimpleFeatureAttribute) obj).feature != feature)
        {
            return false;
        }
        
        if(((SimpleFeatureAttribute) obj).value != value)
        {
            return false;
        }
        
        return true;
    }

    @Override
    public boolean isFeatureStructure() {
        return false;
    }

    @Override
    public Object getValue() {
        return getValueString(value);
    }

    @Override
    public String makeString() {
        return makeString(false);
    }

    @Override
    public String makeStringForRendering() {
        return makeString();
    }

    @Override
    public void print(PrintStream ps) {
        ps.print(makeString());
    }

    @Override
    public int readString(String str) throws Exception {
        String fv[] = str.split("=", 2);
        
        if(fv.length == 1)
        {
            value = getValueIndex(str, true);
        }
        else if(fv.length == 2)
        {
            feature = getFeatureIndex(fv[0], true);
            value = getValueIndex(fv[0], true);
        }
        
        return 0;
    }

    @Override
    public void setValue(Object v) {
        value = getValueIndex((String) v, true);
    }
}
