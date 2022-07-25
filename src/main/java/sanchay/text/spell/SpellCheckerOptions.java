/*
 * SpellCheckerOptions.java
 *
 * Created on April 1, 2006, 3:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text.spell;


import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;

/**
 *
 * @author anil
 */
public class SpellCheckerOptions {
    public CorpusType readCorpusType;
    
    public String inDirectory;
    public String outDirectory;
    
    public String language;
    public String dictionary;

    public boolean standAlone;
    public boolean batchMode;

    public String charset;
    
    public boolean backward;
    public boolean recreateDirStr;
   
    /** Creates a new instance of SpellCheckerOptions */
    public SpellCheckerOptions() {
	setDefaults();
    }
    
    public void setDefaults()
    {
	standAlone = false;
	batchMode = false;

	readCorpusType = CorpusType.RAW;

	inDirectory = "";
	outDirectory = "";

	language = GlobalProperties.getIntlString("eng::utf8");
	dictionary = GlobalProperties.getIntlString("Standard");

	charset = GlobalProperties.getIntlString("UTF-8");

	backward = false;
	recreateDirStr = true;
    }
}
