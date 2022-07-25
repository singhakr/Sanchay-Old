/*
 * FileSplittingOptions.java
 *
 * Created on February 20, 2006, 1:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util;


import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.ssf.SSFCorpus;

/**
 *
 * @author anil
 */
public class FileSplittingOptions
{
    public CorpusType readCorpusType;
    public CorpusType writeCorpusType;
    
    public String inDirectory;
    public String outDirectory;
    
    public String charset;
    
    public int unit;
    public long splitSize;
    public long maxSize; // Max combined size of split files in the relevant unit (char, word, etc.)

    public boolean allowLastSmaller;
    public boolean clean;
    public boolean recreateDirStr;
    public boolean splitSizePerDir;
    public boolean exactSize;
    
    public boolean reallocateStoryIDs;
    public boolean reallocateSentenceIDs;
    public boolean reallocateNodeIDs;
    
    public long clearAnnotationLevelsFlag;
    
    public static final int BY_CHAR = 0; 
    public static final int BY_WORD = 1; 
    public static final int BY_SENTENCE = 2; 
    public static final int BY_PARAGRAPH = 3;
    
    /** Creates a new instance of FileSplittingOptions */
    public FileSplittingOptions()
    {
	setDefaults();
    }
    
    public void setDefaults()
    {
	readCorpusType = CorpusType.RAW;
	writeCorpusType = CorpusType.RAW;

	inDirectory = "";
	outDirectory = "";

	charset = GlobalProperties.getIntlString("UTF-8");

	unit = BY_SENTENCE;
	splitSize = 30;
	maxSize = 10000; // Max combined size of split files in the relevant unit (char, word, etc.)

	allowLastSmaller = true;
	clean = true;
	recreateDirStr = true;
        splitSizePerDir = false;
	exactSize = false;

	reallocateStoryIDs = true;
	reallocateSentenceIDs = true;
	reallocateNodeIDs = true;
	clearAnnotationLevelsFlag = SSFCorpus.NONE;
    }
}
