/*
 * Created on Aug 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.features;

import java.io.PrintStream;
import javax.swing.tree.*;


/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface FeatureAttribute extends MutableTreeNode {
    public static final int SORT_BY_NAME = 0;

    int addAltValue(FeatureValue v);

    void clear();

    int countAltValues();

    int findAltValue(FeatureValue v);

    FeatureValue getAltValue(int index);

    String getName();

    String makeString(boolean mandatory);

    void modifyAltValue(FeatureValue v, int index);

    void print(PrintStream ps, boolean mandatory);

    void removeAllAltValues();

    FeatureValue removeAltValue(int index);

    void hideAttribute();
    void unhideAttribute();
    boolean isHiddenAttribute();

    void setName(String n);

    // other methods
    public Object clone();

    @Override
    public boolean equals(Object obj);
}