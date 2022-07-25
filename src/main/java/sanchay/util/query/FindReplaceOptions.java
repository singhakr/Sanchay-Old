/*
 * FindReplaceOptions.java
 *
 * Created on March 8, 2006, 7:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util.query;

import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;

/**
 *
 * @author anil
 */
public class FindReplaceOptions {

    public CorpusType readCorpusType;
    public CorpusType writeCorpusType;

    public String inDirectory;
    public String outDirectory;

    public String language;
    public String charset;

    public String findText;
    public String replaceWith;

    public boolean standAlone;
    public boolean batchMode;
    public boolean replaceMode;
    public boolean extractionMode;

    public boolean clean;
    public boolean recreateDirStr;

    public boolean searchBackwards;
    public boolean highlightResults;

    public boolean regex;

    // Regex specific options
    public boolean canonicalEquivalence;
    public boolean caseInsensitive;
    public boolean multiline;
    public boolean unicodeCase;
    public boolean unixLines;

    public ResourceQueryOptions resourceQueryOptions;

    /** Creates a new instance of FindReplaceOptions */
    public FindReplaceOptions() {
	setDefaults();
    }

    public void setDefaults()
    {
	standAlone = false;
	batchMode = false;

	replaceMode = false;
	extractionMode = false;

	readCorpusType = CorpusType.RAW;
	writeCorpusType = CorpusType.RAW;

	inDirectory = "";
	outDirectory = "";

	language = GlobalProperties.getIntlString("eng::utf8");
	charset = GlobalProperties.getIntlString("UTF-8");

	clean = true;
	recreateDirStr = true;

	searchBackwards = false;
	highlightResults = true;

	regex = false;

	canonicalEquivalence = false;
	caseInsensitive = true;
	multiline = false;
	unicodeCase = false;
	unixLines = false;

        resourceQueryOptions = new ResourceQueryOptions();
    }
}
