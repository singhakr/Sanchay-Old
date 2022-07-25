/*
 * Created on Jul 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.simple.data;

import java.io.PrintStream;
import java.util.Vector;
import sanchay.data.attrib.TypeTable;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface WordTypeTable extends TypeTable {
    // Change wtflags to BitSet
    public int countWTs(short wtflags /* one or more WordType flags - ANY_WORD will override others */);

    // Change wtflags to BitSet
    public long countTokens(short wtflags /* one or more WordType flags - ANY_WORD will override others */);

    public WordType getWT(int num);

    public WordType getWT(String swrd);

    public int addWT(WordType wt);

    public int findWT(String str);

    public boolean containsWT(String str);

    public WordType removeWT(int num);

    public WordType removeWT(String swrd);

    public Vector sort(int order);

    public WordTypeTable getShallowCopy();

    public int print(PrintStream ps);
}