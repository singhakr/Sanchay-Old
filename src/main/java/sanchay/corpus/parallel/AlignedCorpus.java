/*
 * Created on Jul 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.parallel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import sanchay.corpus.Corpus;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface AlignedCorpus {
    public Hashtable getAlignments();

    public int getAlignment(int key);

    public void addAlignment(int key, int value);

    public void removeAlignments(int key);

    public void read(String file /* alignment file */)
            throws FileNotFoundException, IOException;

    public void print(Corpus srccorpus, Corpus tgtcorpus, APCData apcdata,
            PrintStream senps /* aligned setences */, PrintStream inps /* Mapping File */)
            throws FileNotFoundException, IOException;
}