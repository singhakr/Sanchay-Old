/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.simple.impl;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.common.types.*;
import sanchay.corpus.*;
import sanchay.corpus.simple.*;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.util.UtilityFunctions;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleStoryImpl extends Story implements SimpleStory {

    /**
     * 
     */
    public SimpleStoryImpl() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static CorpusType getCorpusType(String strings[])
    {
	CorpusType ctype = null;

        SSFProperties ssfp = SSFNode.getSSFProperties();
	String storyStart = ssfp.getProperties().getPropertyValue("storyStart");
	String sentenceStart = ssfp.getProperties().getPropertyValue("sentenceStart");

	String storyEnd = ssfp.getProperties().getPropertyValue("storyEnd");
	String sentenceEnd = ssfp.getProperties().getPropertyValue("sentenceEnd");

        String tcTextStart = ssfp.getProperties().getPropertyValue("tcTextStart");
	String tcPhraseStart = ssfp.getProperties().getPropertyValue("tcPhraseStart");

	String tcTextEnd = ssfp.getProperties().getPropertyValue("tcTextEnd");
	String tcPhraseEnd = ssfp.getProperties().getPropertyValue("tcPhraseEnd");

	String bracketFormStart = ssfp.getProperties().getPropertyValueForPrint("bracketFormStart");
	String bracketFormEnd = ssfp.getProperties().getPropertyValueForPrint("bracketFormEnd");

	String wordTagSeparator = ssfp.getProperties().getPropertyValueForPrint("wordTagSeparator");

	boolean storyStarted = false;
	boolean sentenceStarted = false;

        boolean tcTextStarted = false;
	boolean tcPhraseStarted = false;

	boolean postagged = true;

        boolean isXML = false;

	String line;

	for(int i = 0; i < strings.length; i++)
	{
	    line = (String) strings[i];
	    line = line.trim();

	    if(line.equals("") == false)
	    {
                if(line.contains("\t"))
                {
                    String parts[] = line.split("[\t]");
                    
                    if(parts.length == 3 && (parts[2].startsWith("B-") || parts[2].startsWith("I-")) ) {
                        return CorpusType.BI_FORMAT;
                    }

                    if(parts.length == 5 && parts[3].contains("|") && parts[4].contains("|") && parts[1].contains("-")) {
                        
                        String iparts[] = parts[1].split("-");
                        
                        if(UtilityFunctions.isInteger(iparts[0]) && UtilityFunctions.isInteger(iparts[1]))
                        {
                            return CorpusType.HINDENCORP_FORMAT;
                        }
                        
                        return CorpusType.BI_FORMAT;
                    }
//                    if(parts.length == 2)
//                        return CorpusType.VERTICAL_POS_TAGGED;
                }
                
                if(line.startsWith("<?xml version=")) {
                    isXML = true;
                }
//                    return CorpusType.XML_TAGGED;
//                    return CorpusType.XML_SSF_TAGGED;
                else if(!isXML && line.startsWith(storyStart)) {
                    storyStarted = true;
                }
		else if(!isXML && line.startsWith(sentenceStart)) {
                    sentenceStarted = true;
                }
		else if(!isXML && storyStarted && line.startsWith(storyEnd)) {
                    return CorpusType.SSF_FORMAT;
                }
		else if(!isXML && sentenceStarted && line.startsWith(sentenceEnd)) {
                    return CorpusType.SSF_FORMAT;
                }
                else if(isXML && line.startsWith(tcTextStart)) {
                    tcTextStarted = true;
                }
                else if(isXML && line.startsWith(tcPhraseStart)) {
                    tcPhraseStarted = true;
                }
		else if(tcTextStarted && line.startsWith(tcTextEnd)) {
                    return CorpusType.TYPECRAFT_FORMAT;
                }
		else if(tcPhraseStarted && line.startsWith(tcPhraseEnd)) {
                    return CorpusType.TYPECRAFT_FORMAT;
                }
		else if(storyStarted == false && sentenceStarted == false && line.startsWith(bracketFormStart) && line.contains(bracketFormEnd)) {
                    return CorpusType.CHUNKED;
                }
		else if(!isXML && postagged == true && storyStarted == false && sentenceStarted == false)
		{
		    String words[] = line.split("[ ]");
                    
                    int notag = 0;

		    for (int j = 0; j < words.length; j++)
		    {
			String wt[] = words[j].split("[" + wordTagSeparator + "]");

			if(wt.length != 2)
			{
                            notag += 1;
//			    j = words.length; // break
			}
		    }
                    
                    if(notag > 0 && (words.length - notag) <= notag) {
                        postagged = false;
                    }
		}
	    }
	}

        if(isXML) {
            return CorpusType.XML_FORMAT;
        }

	if(storyStarted == false && sentenceStarted == false)
	{
	    if(postagged) {
                return CorpusType.POS_TAGGED;
            }

	    return CorpusType.RAW;
	}
	
	return ctype;
    }
    
    public static CorpusType getCorpusType(File file, String cs)
    {
	CorpusType ctype = null;

	String line = "";
        Vector lines = new Vector(0, 5);

	try
	{
	    BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), cs));

	    while((line = lnReader.readLine()) != null)
	    {
		line.trim();

		if ((line.equals("") == false))
		{
		    lines.add(line);
		}
	    }
	    
	    String slines[] = new String[lines.size()];
	    lines.toArray(slines);
	    ctype = getCorpusType(slines);

	    lnReader.close();
	}
	catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	
	return ctype;
    }
    
    public static CorpusType getCorpusType(String path, String cs)
    {
	if(path == null || path.equals(""))
	    return null;

	if(cs == null | cs.equals(""))
	    return SimpleStoryImpl.getCorpusType(new File(path), "UTF-8");

	return SimpleStoryImpl.getCorpusType(new File(path), cs);
    }

    public static void main(String[] args) {
    }
}
