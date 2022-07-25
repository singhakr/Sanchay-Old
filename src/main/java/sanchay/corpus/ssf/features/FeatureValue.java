/*
 * Created on Aug 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf.features;

import java.io.PrintStream;
import javax.swing.tree.*;

import sanchay.corpus.ssf.features.impl.FSProperties;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface FeatureValue extends MutableTreeNode {
    boolean isFeatureStructure();
    
    void clear();

    Object clone();

    Object getValue();

    String makeString();

    String makeStringForRendering();

    void print(PrintStream ps);

    int readString(String str) throws Exception;

    void setValue(Object v);

    public boolean equals(Object obj);
}