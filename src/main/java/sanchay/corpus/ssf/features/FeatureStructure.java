/*
 * Created on Aug 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.features;

import java.io.PrintStream;
import java.util.*;
import javax.swing.tree.*;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.table.*;
/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface FeatureStructure extends MutableTreeNode {
    
    int addAttribute(FeatureAttribute a);

    int addAttribute(FeatureAttribute a, String p);

    int addAttribute(String name, String value);

    void addMandatoryAttributes();

    void clear();

    int countAttributes();

    int findAttribute(FeatureAttribute a);

    FeatureAttribute getAttribute(String p);

    FeatureAttribute getOneOfAttributes(String names[]);

    FeatureAttribute getAttribute(int index);

    List<String> getAttributeNames();

    List<String> getAttributeNames(String p);

    FeatureValue getAttributeValue(String p);

    String getAttributeValueString(String p);

    void setAttributeValue(String attibName, String val);

    FeatureValue getAttributeValueByIndex(String p);

    List<String> getAttributeValues();

    List<String> getAttributeValuePairs();

    List<FeatureValue> getAttributeValues(String p);

    SanchayTableModel getFeatureTable();

    String getName();

    List<FeatureAttribute> getPaths(String name);

    Object getValue();

    void clearAnnotation(long annoLevelFlags, SSFNode containingNode);

    boolean hasMandatoryAttribs();

    void hasMandatoryAttribs(boolean m);

    void checkAndSetHasMandatory();

    String makeStringForRendering();

    String makeString();

    String makeStringFV();

    /**
     * Not yet implemented.
     * 
     * @param f1
     * @param f2
     * @return 
     */
    FeatureStructure merge(FeatureStructure f1, FeatureStructure f2);

    void modifyAttribute(FeatureAttribute a, int index);

    void modifyAttribute(FeatureAttribute a, int index, String p);

    void modifyAttributeValue(FeatureValue fv, String p);

    void modifyAttributeValue(FeatureValue fv, int attribIndex, int altValIndex);

    void print(PrintStream ps);

    /**
     * Not yet implemented.
     */
    void prune();

    int readString(String fs_str) throws Exception;

    int readStringFV(String fs_str) throws Exception;

    void removeAllAttributes();

    FeatureAttribute removeAttribute(int index);

    FeatureAttribute removeAttribute(String p);

    FeatureAttribute removeAttribute(int index, String p);

    void removeMandatoryAttributes();

    void removeNonMandatoryAttributes();

    void hideAttribute(String aname);
    void unhideAttribute(String aname);

    FeatureAttribute searchAttribute(String name, boolean exactMatch);

    FeatureAttribute searchOneOfAttributes(String names[], boolean exactMatch);

    FeatureAttribute searchAttributeValue(String name, String val, boolean exactMatch);

    List<FeatureAttribute> searchAttributeValues(String name, String val, boolean exactMatch);

    List<FeatureAttribute> replaceAttributeValues(String name, String val, String nameReplace, String valReplace);

    List<FeatureAttribute> searchAttributes(String name, boolean exactMatch);

    void setFeatureTable(SanchayTableModel ft);

    void setName(String n);

    void setToEmpty();

    void setValue(Object v);

    /**
     * Not yet implemented.
     * 
     * @param f1
     * @param f2
     * @return 
     */
    FeatureStructure unify(FeatureStructure f1, FeatureStructure f2);

    void setAlignmentUnit(AlignmentUnit alignmentUnit);

    AlignmentUnit loadAlignmentUnit(Object srcAlignmentObject, Object srcAlignmentObjectContainer, Object tgtAlignmentObjectContainer, int parallelIndex);

    public Object clone();

    public void sortAttributes(int sortType);

    @Override
    public boolean equals(Object fa);
}