/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import sanchay.corpus.parallel.Alignable;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.properties.KeyValueProperties;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SSFCorpus extends Alignable {
    
    // Annotation levels
    public static final long NONE = 0x00000000;
    public static final long POS_TAGS = 0x00000001;
    public static final long CHUNK_NAMES = 0x00000002;
    public static final long TAGS = POS_TAGS | CHUNK_NAMES;
    public static final long CHUNKS = 0x00000004;
    public static final long LEX_MANDATORY_ATTRIBUTES = 0x00000008;
    public static final long LEX_EXTRA_ATTRIBUTES = 0x00000010;
    public static final long LEXITEM_FEATURE_STRUCTURES = LEX_MANDATORY_ATTRIBUTES | LEX_EXTRA_ATTRIBUTES;
    public static final long CHUNK_MANDATORY_ATTRIBUTES = 0x00000020;
    public static final long CHUNK_EXTRA_ATTRIBUTES = 0x00000040;
    public static final long CHUNK_FEATURE_STRUCTURES = CHUNK_MANDATORY_ATTRIBUTES | CHUNK_EXTRA_ATTRIBUTES;
    public static final long ALL_EXCEPT_THE_FIRST_FS = 0x00000080;
    public static final long PRUNE_THE_FS = 0x00000100;
    public static final long COMMENTS = 0x00000200;

    public static final long OVERALL_ANNOTATION = POS_TAGS | CHUNK_NAMES | CHUNKS
	| LEX_MANDATORY_ATTRIBUTES | LEX_EXTRA_ATTRIBUTES | LEXITEM_FEATURE_STRUCTURES
	| CHUNK_MANDATORY_ATTRIBUTES | CHUNK_EXTRA_ATTRIBUTES | CHUNK_FEATURE_STRUCTURES
	| ALL_EXCEPT_THE_FIRST_FS | COMMENTS;

    public KeyValueProperties getProperties();

    public void setProperties(KeyValueProperties p);

    public String getPath();

    public void setPath(String p);

    public String getCharset();

    public void setCharset(String cs);

    public int countStories();

    public Enumeration getStoryKeys();

    public SSFStory getStory(String p);

    public int addStory(String p, SSFStory s);

    public String removeStory(String p);

    public void read() throws FileNotFoundException, IOException;

    public void read(File f) throws FileNotFoundException, IOException;

    public void read(File f[]) throws FileNotFoundException, IOException;

    public void readStory(File f) throws Exception, FileNotFoundException, IOException;
}