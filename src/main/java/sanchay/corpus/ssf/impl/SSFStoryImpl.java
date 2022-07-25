package sanchay.corpus.ssf.impl;

import sanchay.corpus.ssf.SSFSentence;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.*;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.corpus.ssf.tree.*;
import sanchay.properties.KeyValueProperties;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.xml.dom.SanchayDOMElement;

public class SSFStoryImpl extends SSFTextImpl
        implements Serializable, SSFStory, SanchayDOMElement
{
    public SSFStoryImpl()
    {
        
    }

    public static SSFText getSSFText(String filePath, int startSentenceNum, int count)
            throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
        SSFStory s = new SSFStoryImpl();
        s.readFile(filePath);
        return s.getSSFText(startSentenceNum, count);
    }

    // Implementation parallel to the readFile method
    public static boolean validateSSF(String filePath, String cs, List<String> errorLog /*Strings*/)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String line = "";
        List<String> lines = new ArrayList<String>();

	String storyStart = ssfp.getProperties().getPropertyValue("storyStart");

	String storyEnd = ssfp.getProperties().getPropertyValue("storyEnd");

	boolean storyStarted = false;
	boolean storyEnded = false;

	boolean validated = true;

	try
	{
	    BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), cs));

	    while((line = lnReader.readLine()) != null)
	    {
		line = line.trim();

		if(line.startsWith(storyStart))
//		if(line.matches(storyStart))
		{
		    storyStarted = true;
		}
		else if(line.startsWith(storyEnd) && lines.isEmpty() == false)
//		else if(line.matches(storyEnd) && lines.equals("") == false)
		{
		    storyEnded = true;
//		    break;
		}
//		else if((storyStarted == true && storyEnded == false)
//			|| (storyStarted == false && storyEnded == false))
		    lines.add(line);
	    }

	    if(storyStarted == true && storyEnded == false)
	    {
		validated = false;
		    
		if(errorLog != null) {
                    errorLog.add(GlobalProperties.getIntlString("Error:_Story_tag_begun_but_not_ended.\n"));
                }
		else {
                    System.out.println(GlobalProperties.getIntlString("Error:_Story_tag_begun_but_not_ended.\n"));
                }
	    }
	    else if(storyStarted == false && storyEnded == true)
	    {
		validated = false;
		    
		if(errorLog != null) {
                    errorLog.add(GlobalProperties.getIntlString("Error:_Story_tag_ended_but_not_begun.\n"));
                }
		else {
                    System.out.println(GlobalProperties.getIntlString("Error:_Story_tag_ended_but_not_begun.\n"));
                }
	    }
	    
	    String slines[] = new String[lines.size()];
	    lines.toArray(slines);
            
	    if(validateSSF(slines, errorLog) == false) {
                validated = false;
            }

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

	if(validated == false) {
            errorLog.add(0, GlobalProperties.getIntlString("\nIncorrect_format:_Doesn't_seem_to_be_correct_SSF_format.\n"));
        }
	
	return validated;
    }

    // Implementation parallel to the readFile method
    public static boolean validateSSF(String strings[], List<String> errorLog /*Strings*/)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String line = "";
        String lines = "";

	String sentenceStart = ssfp.getProperties().getPropertyValue("sentenceStart");

	String sentenceEnd = ssfp.getProperties().getPropertyValue("sentenceEnd");

	int sentencesStarted = 0;
	int sentencesEnded = 0;

	boolean validated = true;
	
	LinkedHashMap<Integer, Boolean> sentenceHash = new LinkedHashMap<Integer, Boolean>(0, 5);

        int lineNum = 0;
        
	for(int i = 0; i < strings.length; i++)
	{
            lineNum = i + 1;
            
	    line = (String) strings[i];
	    line = line.trim();

	    if ((line.equals("") == false))
	    {
		if(line.startsWith(sentenceStart))
//		if(line.matches(sentenceStart))
		{
		    sentencesStarted++;
		    sentenceHash.put(new Integer(sentencesStarted), new Boolean(false));

		    lines = "";
		}
		else if(line.startsWith(sentenceEnd))
//		else if(line.matches(sentenceEnd))
		{
		    sentencesEnded++;
		    lines += line + "\n";

		    if(sentenceHash.get(new Integer(sentencesEnded)) != null) {
                        sentenceHash.put(new Integer(sentencesEnded), new Boolean(true));
                    }

		    if(SSFSentenceImpl.validateSSF(lines, errorLog, lineNum) == false)
		    {
			validated = false;
			errorLog.add(GlobalProperties.getIntlString("Couldn't_read_sentence_ending_at_line_") + lineNum + "\n");
		    }
		}

		if
		(
		    sentencesStarted > 0
			&& (sentencesStarted == sentencesEnded || sentencesStarted == sentencesEnded + 1)
		) {
                    lines += line + GlobalProperties.getIntlString("\n");
                }
	    }
            else {
                lineNum++;
            }
	}

	if(sentencesStarted != sentencesEnded)
	{
	    validated = false;

	    if(errorLog != null)
	    {
		errorLog.add("Sentences started:" + sentencesStarted + "\n");
		errorLog.add(GlobalProperties.getIntlString("Sentences_ended:") + sentencesEnded + "\n");
	    }
	    else
	    {
		System.out.println(GlobalProperties.getIntlString("Sentences_started:") + sentencesStarted);
		System.out.println(GlobalProperties.getIntlString("Sentences_ended:") + sentencesEnded);
	    }
	}

	if(validated == false) {
            errorLog.add(0, GlobalProperties.getIntlString("\nIncorrect_format:_Doesn't_seem_to_be_correct_SSF_format.\n"));
        }
	
	return validated;
   }
    
    @Override
    public void readFile(String filePath, String cs, CorpusType corpusType, List<String> errorLog /*Strings*/)
            throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
        if(corpusType.equals(CorpusType.XML_FORMAT))
        {
            readXML(filePath, cs);
            return;
        }
        else if(corpusType.equals(CorpusType.TYPECRAFT_FORMAT))
        {
            readTypeCraftXML(filePath, cs);
            return;
        }
        else if(corpusType.equals(CorpusType.RAW))
        {
            readRaw(filePath, cs, corpusType, errorLog);
            return;
        }
        else if(corpusType.equals(CorpusType.POS_TAGGED))
        {
            readPOSTagged(filePath, cs, corpusType, errorLog);
            return;
        }
        else if(corpusType.equals(CorpusType.HINDENCORP_FORMAT))
        {
            readHindenCorp(filePath, cs, corpusType, errorLog);
            return;
        }
//        else if(corpusType.equals(CorpusType.VERTICAL_POS_TAGGED))
//        {
//            readVerticalPOSTagged(filePath, cs, corpusType, errorLog);
//            return;
//        }
//        else if(corpusType.equals(CorpusType.BI_FORMAT))
//        {
//            readBIFormat(filePath, cs, corpusType, errorLog);
//            return;
//        }
        else if(corpusType.equals(CorpusType.CHUNKED))
        {
            readChunked(filePath, cs, corpusType, errorLog);
            return;
        }
//        else if(corpusType.equals(CorpusType.SSF_FORMAT))
//        {
//            readSSFFormat(filePath, cs, corpusType, errorLog);
//            return;
//        }

        SSFProperties ssfp = SSFNode.getSSFProperties();

        clear();

        String line = "";
        String lines = "";

	String metaDataStart = ssfp.getProperties().getPropertyValue("metaDataStart");
	String storyStart = ssfp.getProperties().getPropertyValue("storyStart");
	String bodyStart = ssfp.getProperties().getPropertyValue("bodyStart");
	String paragraphStart = ssfp.getProperties().getPropertyValue("paragraphStart");
	String sentenceStart = ssfp.getProperties().getPropertyValue("sentenceStart");

        String paragraphMetaDataStart = ssfp.getProperties().getPropertyValue("paragraphMetaDataStart");

	String metaDataEnd = ssfp.getProperties().getPropertyValue("metaDataEnd");
	String storyEnd = ssfp.getProperties().getPropertyValue("storyEnd");
	String bodyEnd = ssfp.getProperties().getPropertyValue("bodyEnd");
	String paragraphEnd = ssfp.getProperties().getPropertyValue("paragraphEnd");
	String sentenceEnd = ssfp.getProperties().getPropertyValue("sentenceEnd");

	boolean metaDataStarted = false;
	boolean storyStarted = false;
	boolean bodyStarted = false;
	int sentencesStarted = 0;
	int sentencesEnded = 0;
        
        int paraBeginIndex = 0;
	
	LinkedHashMap<Integer, Boolean> sentenceHash = new LinkedHashMap<Integer, Boolean>(0, 5);

        BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), cs));

	int lineNum = 0;
        
        String paraAttribs = "";
        String paraMetaData = "";
        
        SSFSentenceImpl sen = null;
        SSFPhrase chunk = null;
        
        while((line = lnReader.readLine()) != null)
        {
	    lineNum++;
	    
//	    line = line.trim();
            if(corpusType == CorpusType.VERTICAL_POS_TAGGED)
            {                
                if(line.trim().equals(""))
                {
                    if(sen != null) {
                        addSentence(sen);
                    }
                        
                    sen = new SSFSentenceImpl();
                    sen.setRoot(new SSFPhrase("0", "((", "SSF", ""));
                }
                else
                {
                    if(sen == null)
                    {
                        sen = new SSFSentenceImpl();
                        sen.setRoot(new SSFPhrase("0", "((", "SSF", ""));
                    }

                    String parts[] = line.split("[\\s]+");
                    
                    if(parts.length == 2)
                    {
                        SSFLexItem word = new SSFLexItem("0", parts[0], parts[1], "");
                        sen.getRoot().addChild(word);                    
                    }
                }                
            }
            else if(corpusType == CorpusType.BI_FORMAT)
            {                
                if(line.trim().equals(""))
                {
                    if(sen != null)
                    {
                        if(chunk != null) {
                            sen.getRoot().addChild(chunk);
                        }
                        
                        addSentence(sen);
                    }
                        
                    sen = new SSFSentenceImpl();
                    sen.setRoot(new SSFPhrase("0", "((", "SSF", ""));
                }
                else
                {
                    if(sen == null)
                    {
                        sen = new SSFSentenceImpl();
                        sen.setRoot(new SSFPhrase("0", "((", "SSF", ""));
                    }

                    String parts[] = line.split("[\\s]+");

                    if(parts.length == 3)
                    {
                        SSFLexItem word = new SSFLexItem("0", parts[0], parts[1], "");

                        if(parts[2].startsWith("B-"))
                        {
                            if(chunk != null) {
                                sen.getRoot().addChild(chunk);
                            }

                            chunk = new SSFPhrase("0", "((", parts[2].split("-")[1], "");
                        }

                        if(chunk != null) {
                            chunk.addChild(word);
                        }
                    }
                }
            }
            else if ((line.equals("") == false))
	    {
                if(corpusType == CorpusType.SSF_FORMAT)
		{
		    if(line.startsWith(sentenceStart))
//		    if(line.matches(sentenceStart))
		    {
			sentencesStarted++;
			sentenceHash.put(new Integer(sentencesStarted), new Boolean(false));

			lines = "";
		    }
		    else if(line.startsWith(sentenceEnd))
//		    else if(line.matches(sentenceEnd))
		    {
			sentencesEnded++;
			lines += line + "\n";

			if(sentenceHash.get(new Integer(sentencesEnded)) != null) {
                            sentenceHash.put(new Integer(sentencesEnded), new Boolean(true));
                        }

			sen = new SSFSentenceImpl();
			sen.readString(lines, errorLog, lineNum);
			addSentence(sen);
		    }

		    if(line.startsWith(metaDataStart))
//		    if(line.matches(metaDataStart))
		    {
			metaDataStarted = true;
                    }
                    else if(line.startsWith(metaDataEnd))
//                    else if(line.matches(metaDataEnd))
		    {
			metaDataStarted = false;
                    }
                    else if(line.startsWith(paragraphStart))///
		    {
                        line = line.replaceAll(paragraphStart, "");
                        line = line.replaceAll("<", "");
                        line = line.replaceAll(">", "");
                        
                        paraAttribs = line;
                        
                        paraBeginIndex = sentencesEnded;
                        paraMetaData = "";
                    }
                    else if(line.startsWith(paragraphMetaDataStart))
                    {
                        paraMetaData = line;
                    }
                    else if(line.startsWith(paragraphEnd))
		    {
                        addParagraphBoundaries(paraBeginIndex, sentencesEnded, paraAttribs, paraMetaData);
                    }
                    else if(line.startsWith(bodyStart))
                    {
                        bodyStarted = true;
                    }
                    else if(line.startsWith(storyStart))
//                    else if(line.matches(storyStart))
		    {
			storyStarted = true;

			// Ad-hoc - change it to regex later
			String tmp[] = line.split("id=");
			if(tmp.length == 2)
			{
			    tmp = tmp[1].split("\"");

			    if(tmp.length > 2) {
                                setId(tmp[1]);
                            }
			}
		    }
		    else if(line.startsWith(storyEnd) && lines.equals("") == false)
//		    else if(line.matches(storyEnd) && lines.equals("") == false)
		    {
			break;
		    }
                    else if(metaDataStarted == true && storyStarted == true && bodyStarted == false)
                    {
			metaData += line + "\n";
                    }
		    else if
		    (
			storyStarted == true
				||
				(
				    sentencesStarted > 0
					&& (sentencesStarted == sentencesEnded || sentencesStarted == sentencesEnded + 1)
				)
		    ) {
                        lines += line + "\n";
                    }
		}
		else if(corpusType == CorpusType.RAW)
		{
                    line = line.trim();
		    sen = new SSFSentenceImpl();
		    sen.makeSentenceFromRaw(line);
		    addSentence(sen);
		}
		else if(corpusType == CorpusType.POS_TAGGED)
		{
                    line = line.trim();
		    sen = new SSFSentenceImpl();
		    sen.makeSentenceFromPOSTagged(line, errorLog, lineNum);
		    addSentence(sen);
		}
		else if(corpusType == CorpusType.CHUNKED)
		{
                    line = line.trim();
		    sen = new SSFSentenceImpl();
		    sen.makeSentenceFromChunked(line, errorLog, lineNum);
		    addSentence(sen);
		}
	    }
        }
	
	if(corpusType == CorpusType.SSF_FORMAT && storyStarted == false)
	{
	    if(sentencesStarted != sentencesEnded)
	    {
		if(errorLog != null)
		{
		    errorLog.add(GlobalProperties.getIntlString("Sentences_started:") + sentencesStarted + "\n");
		    errorLog.add(GlobalProperties.getIntlString("Sentences_ended:") + sentencesEnded + "\n");
		}
		else
		{
		    System.out.println(GlobalProperties.getIntlString("Sentences_started:") + sentencesStarted);
		    System.out.println(GlobalProperties.getIntlString("Sentences_ended:") + sentencesEnded);
		}
		
		Iterator<Integer> itr = sentenceHash.keySet().iterator();
		
		while(itr.hasNext())
		{
		    Integer key = itr.next();
		    Boolean val = sentenceHash.get(key);
		    
		    if(val.booleanValue() == false)
		    {
			if(errorLog != null) {
                            errorLog.add("\tSentence_not_ended:" + key.toString() + "\n");
                        }
			else {
                            System.out.println("\tSentence_not_ended:" + key.toString());
                        }
		    }
		}
		
		if(errorLog != null) {
                    errorLog.add("\nIncorrect_format:_Doesn't_seem_to_be_correct_SSF_format.\n");
                }
		else {
                    throw new Exception("\nIncorrect_format:_Doesn't_seem_to_be_correct_SSF_format.\n");
                }
	    }
	}
	
        lnReader.close();

        removeEmptySentences();
    }

    @Override
    public void readFile(String filePath, String cs, CorpusType corpusType)
            throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
	readFile(filePath, cs, corpusType, null);
    }

    @Override
    public void readFile(String filePath, String cs)
            throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
	CorpusType corpusType = SimpleStoryImpl.getCorpusType(filePath, cs);
	readFile(filePath, cs, corpusType);
    }

    @Override
    public void readFile(String filePath)
            throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

	readFile(filePath, ssfp.getProperties().getPropertyValue("encoding"));
    }

    public void readString(String string, List<String> errorLog /*Strings*/) throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        clear();

        String lines = "";
        String lineArray[] = string.split("\n");

	String metaDataStart = ssfp.getProperties().getPropertyValue("metaDataStart");
	String storyStart = ssfp.getProperties().getPropertyValue("storyStart");
	String bodyStart = ssfp.getProperties().getPropertyValue("bodyStart");
	String paragraphStart = ssfp.getProperties().getPropertyValue("paragraphStart");
	String sentenceStart = ssfp.getProperties().getPropertyValue("sentenceStart");

        String paragraphMetaDataStart = ssfp.getProperties().getPropertyValue("paragraphMetaDataStart");

	String metaDataEnd = ssfp.getProperties().getPropertyValue("metaDataEnd");
	String storyEnd = ssfp.getProperties().getPropertyValue("storyEnd");
	String bodyEnd = ssfp.getProperties().getPropertyValue("bodyEnd");
	String paragraphEnd = ssfp.getProperties().getPropertyValue("paragraphEnd");
	String sentenceEnd = ssfp.getProperties().getPropertyValue("sentenceEnd");

	boolean metaDataStarted = false;
	boolean storyStarted = false;
	boolean bodyStarted = false;
	int sentencesStarted = 0;
	int sentencesEnded = 0;
        
        int paraBeginIndex = 0;
        
        String paraAttribs = "";
        String paraMetaData = "";
	
	LinkedHashMap<Integer, Boolean> sentenceHash = new LinkedHashMap<Integer, Boolean>(0, 5);

        for(int i = 0; i < lineArray.length; i++)
        {
            lineArray[i] = lineArray[i].trim();

	    if(lineArray[i].startsWith(sentenceStart))
//	    if(lineArray[i].matches(sentenceStart))
	    {
		sentencesStarted++;
		sentenceHash.put(new Integer(sentencesStarted), false);
	    }
	    else if(lineArray[i].startsWith(sentenceEnd))
//	    else if(lineArray[i].matches(sentenceEnd))
	    {
		sentencesEnded++;
		
		if(sentenceHash.get(new Integer(sentencesEnded)) != null) {
                    sentenceHash.put(new Integer(sentencesEnded), true);
                }
	    }

            if(lineArray[i].startsWith(metaDataStart))
//            if(lineArray[i].matches(metaDataStart))
            {
                metaDataStarted = true;
            }
            else if(lineArray[i].startsWith(metaDataEnd))
//            else if(lineArray[i].matches(metaDataEnd))
            {
                metaDataStarted = false;
            }
            else if(lineArray[i].startsWith(paragraphStart))
            {
                lineArray[i] = lineArray[i].replaceAll(paragraphStart, "");
                lineArray[i] = lineArray[i].replaceAll("<", "");
                lineArray[i] = lineArray[i].replaceAll(">", "");

                paraAttribs = lineArray[i];
                
                paraBeginIndex = sentencesEnded;
                paraMetaData = "";
            }
            else if(lineArray[i].startsWith(paragraphMetaDataStart))
            {
                paraMetaData = lineArray[i];
            }
            else if(lineArray[i].startsWith(paragraphEnd))
            {
                addParagraphBoundaries(paraBeginIndex, sentencesEnded, paraAttribs, paraMetaData);
            }
            else if(lineArray[i].startsWith(bodyStart))
            {
                bodyStarted = true;
            }
            else if(lineArray[i].startsWith(storyStart))
//            else if(lineArray[i].matches(storyStart))
            {
                storyStarted = true;

                // Ad-hoc - change it to regex later
                String tmp[] = lineArray[i].split("id=");
                if(tmp.length == 2)
                {
                    tmp = tmp[1].split("\"");

                    if(tmp.length > 2) {
                        setId(tmp[1]);
                    }
                }
            }
            else if(lineArray[i].startsWith(storyEnd) && lines.equals("") == false)
//            else if(lineArray[i].matches(storyEnd) && lines.equals("") == false)
            {
                List<SSFSentence> sents = SSFSentenceImpl.readSentencesFromString(lines);
                sentences.addAll(sents);

                i = lineArray.length; // break
            }
            else if(metaDataStarted == true && storyStarted == true && bodyStarted == false)
            {
                metaData += lineArray[i] + "\n";
            }
            else if
	    (
		storyStarted == true
			||
			(
			    sentencesStarted > 0
				&& (sentencesStarted == sentencesEnded || sentencesStarted == sentencesEnded + 1)
			)
	    ) {
                lines += lineArray[i] + "\n";
            }
        }
	
	if(storyStarted == false)
	{
	    if(sentencesStarted == sentencesEnded)
	    {
		List<SSFSentence> sents = SSFSentenceImpl.readSentencesFromString(lines);
		sentences.addAll(sents);
	    }
	    else
	    {
		System.out.println(GlobalProperties.getIntlString("Sentences_started:") + sentencesStarted);
		System.out.println(GlobalProperties.getIntlString("Sentences_ended:") + sentencesEnded);
		
		Iterator<Integer> itr = sentenceHash.keySet().iterator();
		
		while(itr.hasNext())
		{
		    Integer key = itr.next();
		    Boolean val = sentenceHash.get(key);
		    
		    if(val.booleanValue() == false) {
                        System.out.println(GlobalProperties.getIntlString("\tSentence_not_ended:") + key.toString());
                    }
		}
		
		if(errorLog != null) {
                    errorLog.add(GlobalProperties.getIntlString("\nIncorrect_format:_Doesn't_seem_to_be_correct_SSF_format.\n"));
                }
		else {
                    throw new Exception(GlobalProperties.getIntlString("\nIncorrect_format:_Doesn't_seem_to_be_correct_SSF_format.\n"));
                }
	    }
	}

        removeEmptySentences();
    }

    @Override
    public void readString(String string) throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
	readString(string, null);
    }

    @Override
    public String makeString()
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String SSF = "";
        
	String metaDataStart = ssfp.getProperties().getPropertyValue("metaDataStart");
	String metaDataEnd = ssfp.getProperties().getPropertyValue("metaDataEnd");
        String storyStart = ssfp.getProperties().getPropertyValueForPrint("storyStart");
        String storyEnd = ssfp.getProperties().getPropertyValueForPrint("storyEnd");
        String bodyStart = ssfp.getProperties().getPropertyValueForPrint("bodyStart");
        String bodyEnd = ssfp.getProperties().getPropertyValueForPrint("bodyEnd");
	String paragraphStart = ssfp.getProperties().getPropertyValue("paragraphStart");
	String paragraphEnd = ssfp.getProperties().getPropertyValue("paragraphEnd");

	String paragraphTextStart = ssfp.getProperties().getPropertyValue("paragraphTextStart");
	String paragraphTextEnd = ssfp.getProperties().getPropertyValue("paragraphTextEnd");

        if(storyStart.startsWith("<"))
        {
            storyStart += " id=\"" + id + "\">";
            storyEnd += ">";
        }
        else
        {
            storyStart += " " + id;
        }

        SSF += storyStart + "\n";

        if(metaDataStart != null && metaDataStart.equals("") == false)
        {
            if(xmlDeclaration != null && xmlDeclaration.equals("") == false) {
                SSF += xmlDeclaration + "\n\n";
            }

            if(dtdDeclaration != null && dtdDeclaration.equals("") == false) {
                SSF += dtdDeclaration + "\n\n";
            }

            SSF += metaDataStart + ">\n";
            SSF += metaData + "\n";
            SSF += metaDataEnd + ">\n";
        }
        
        boolean paragraphPresent = false;
        
        if(paragraphStart != null && paragraphStart.equals("") == false && paragraphs.size() > 0)
        {
            paragraphPresent = true;
        }

        if(paragraphPresent)
        {
            SSF += bodyStart + ">\n";
            
            for(int i = 0; i < paragraphs.size(); i++ )
            {
                SSFParagraph para = (SSFParagraph) paragraphs.get(i);

                if(para.getAttribsString() != null) {
                    SSF += paragraphStart + " " + para.getAttribsString().trim() + ">\n";
                }
                
                SSF += para.getMetaData() + "\n";
                SSF += paragraphTextStart + ">\n";
                
                for(int j = para.getStartSentence(); j < para.getEndSentence(); j++ )
                {
                    SSFSentence SSFS = (SSFSentenceImpl) sentences.get(j);
                    SSF += SSFS.makeString() + "\n";
                }

                SSF += paragraphTextEnd + ">\n";
                SSF += paragraphEnd + ">\n";
            }

            SSF += bodyEnd + ">\n";
        }
        else
        {
            for(int i = 0; i < sentences.size(); i++ )
            {
                SSFSentence SSFS = (SSFSentenceImpl) sentences.get(i);
                SSF += SSFS.makeString() + "\n";
            }
        }

        SSF += storyEnd + "\n";

        return SSF;
    }
    
    public static boolean clearAnnotationRecursive(File dir, String cs, long annoLevelFlags) throws FileNotFoundException, IOException
    {
	if(dir.exists() == false)
	{
	    System.out.println(GlobalProperties.getIntlString("File_doesn't_exist:_") + dir.getAbsolutePath());
	    return false;
	}

	if(dir.canWrite() == false)
	{
	    System.out.println(GlobalProperties.getIntlString("No_write_permission:_") + dir.getAbsolutePath());
	    return false;
	}
	
        if(dir.isFile())
	{
            if(SimpleStoryImpl.getCorpusType(dir, cs) == CorpusType.SSF_FORMAT)
	    {
		SSFStory ssfStory = new SSFStoryImpl();
		
		try {
		    ssfStory.readFile(dir.getAbsolutePath(), cs, CorpusType.SSF_FORMAT);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		ssfStory.clearAnnotation(annoLevelFlags);
		ssfStory.save(dir.getAbsolutePath(), cs);
		return true;
	    }
	    else {
                return false;
            }
	}
        
        boolean success = true;

        File files[] = dir.listFiles();
	
	if(files == null) {
            return success;
        }

        for(int i = 0; i < files.length; i++)
        {
	    return clearAnnotationRecursive(files[i], cs, annoLevelFlags);
        }
        
        return success;
    }
    
    public static void replaceWordsNotInListWithTags(SSFStory story, KeyValueProperties pruneWordList)
    {
        int scount = story.countSentences();
        
        if(pruneWordList == null || pruneWordList.countProperties() == 0) {
            return;
        }
        
        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = story.getSentence(i);
            
            List<SanchayMutableTreeNode> words = sentence.getRoot().getAllLeaves();
            
            int wcount = words.size();
            
            for (int j = 0; j < wcount; j++)
            {
                SSFNode word = (SSFNode) words.get(j);
                
                if(pruneWordList.getPropertyValue(word.getLexData()) == null)
                {
                    String tag = word.getName();
                    
                    if(tag.equals("") == false) {
                        word.setLexData(tag);
                    }
                }
            }
        }
    }

    public static LinkedHashMap<File, SSFStory> readStories(File[] selFiles, String cs)
    {
        LinkedHashMap<File, SSFStory> selStories = new LinkedHashMap<File, SSFStory>();

        if(selFiles != null && selFiles.length > 1)
        {
            for (int i = 0; i < selFiles.length; i++) {
                File file = (File) selFiles[i];

                SSFStory story = new SSFStoryImpl();

                selStories.put(file, story);
                story.setSSFFile(file.getAbsolutePath());

                try {
                    story.readFile(file.getAbsolutePath(), cs);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return selStories;
    }

    public static void loadGIZAData(String gizaFilePath, String cs, SSFStory srcSSFStory, SSFStory tgtSSFStory)
    {
        BufferedReader inReader = null;

        try {
            if (cs != null && cs.equals("") == false) {
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(gizaFilePath), cs));
            } else {
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(gizaFilePath)));
            }

            String line = "";

            SSFProperties ssfp = SSFNode.getSSFProperties();
            String rootName = ssfp.getProperties().getPropertyValue("rootName");

            SSFSentence srcSen = null;
            SSFSentence tgtSen = null;
            SSFPhrase rnode = null;
            SSFLexItem lexItem = null;

            LinkedHashMap tags = null;
            LinkedHashMap words = null;

            boolean start = false;

            while ((line = inReader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("# Sentence pair")) {
                    start = true;
                } else if (start) {
                    srcSen = new SSFSentenceImpl();

                    rnode = new SSFPhrase("0", "", rootName, "");
                    srcSen.setRoot(rnode);

                    String wrds[] = line.split(" ");

                    for (int i = 0; i < wrds.length; i++) {
                        lexItem = new SSFLexItem("0", wrds[i], "", "");
                        srcSen.getRoot().addChild(lexItem);

//                        ((Alignable) lexItem).getAlignmentUnit().setIndex(i);
                    }

                    srcSen.getRoot().reallocateNames(null, null);

                    srcSSFStory.addSentence(srcSen);

                    start = false;
                } else if (line.startsWith("NULL")) {
                    tgtSen = new SSFSentenceImpl();

                    rnode = new SSFPhrase("0", "", rootName, "");
                    tgtSen.setRoot(rnode);

                    String wrds[] = line.split(" ");

                    boolean inAlign = false;
                    boolean nullNode = false;

                    List<Integer> alignIndices = new ArrayList<Integer>();

                    tags = new LinkedHashMap(0, 10);
                    words = new LinkedHashMap(0, 10);

                    for (int i = 0; i < wrds.length; i++) {
                        String str = wrds[i];
                        str = str.trim();

//                        if(str.equals(""))
//                            continue;

                        if (str.equals("NULL")) {
                            nullNode = true;
                        } else {
                            nullNode = false;
                        }

                        if (inAlign == false && !str.equals("({") && !str.equals("})") && !nullNode) {
                            lexItem = new SSFLexItem("0", wrds[i], "", "");
                            tgtSen.getRoot().addChild(lexItem);

//                            ((Alignable) lexItem).getAlignmentUnit().setIndex(i);
                        } else if (str.equals("({")) {
                            inAlign = true;
                        } else if (inAlign == true && !str.equals("})") && !nullNode) {
                            alignIndices.add(new Integer(Integer.parseInt(str) - 1));
                        } else if (str.equals("})") && !nullNode) {
                            int acount = alignIndices.size();
                            int ccount = tgtSen.getRoot().countChildren();

                            if (ccount > 0) {
                                tgtSen.getRoot().reallocateNames(tags, words, ccount - 1);
                            }

                            String alignName = "alignedTo";
                            String alignSep = ";";

                            for (int j = 0; j < acount; j++) {
                                SSFNode alignedSrcNode = srcSen.getRoot().getChild(alignIndices.get(j));

//                                lexItem.getAlignmentUnit().addAlignedUnit(alignedNode.getAlignmentUnit());
//                                alignedNode.getAlignmentUnit().addAlignedUnit(lexItem.getAlignmentUnit());

                                alignedSrcNode.concatenateAttributeValue(alignName, lexItem.getAttributeValue("name"), alignSep);
                                lexItem.concatenateAttributeValue(alignName, alignedSrcNode.getAttributeValue("name"), alignSep);
                            }

                            alignIndices.clear();

                            inAlign = false;
                        }
                    }

//                    srcSen.saveAlignments();
//                    tgtSen.saveAlignments();

                    tgtSSFStory.addSentence(tgtSen);

                    start = true;
                }
            }

            srcSSFStory.reallocateSentenceIDs();
            srcSSFStory.reallocateNodeIDs();
            tgtSSFStory.reallocateSentenceIDs();
            tgtSSFStory.reallocateNodeIDs();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSFStoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSFStoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSFStoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SSFStoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void readFileTreeForm(String filePath, String cs) throws Exception, FileNotFoundException, IOException
//    {
//        Element rootNode = null;
//
//        try {
//            rootNode = XMLUtils.parseW3CXML(filePath, cs);
//
//            if(rootNode != null)
//            {
//               readTreeFormXML(rootNode);
//            }
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (SAXException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    protected void readTreeFormXML(Element domElement)
//    {
//        Node node = domElement.getFirstChild();
//
//        while(node != null)
//        {
//            if(node instanceof Element)
//            {
//                Element element = (Element) node;
//
//                if(element.getTagName().equals("sentence"))
//                {
//                    SSFSentence sentence = readTreeFormSentence(element);
//                    addSentence(sentence);
//                }
//            }
//
//            node = node.getNextSibling();
//        }
//    }
//
//    protected SSFSentence readTreeFormSentence(Element domElement)
//    {
//        SSFSentence sentence = new SSFSentenceImpl();
//
//        Node node = domElement.getFirstChild();
//
//        while(node != null)
//        {
//            if(node instanceof Element)
//            {
//                Element element = (Element) node;
//
//                if(element.getTagName().equals("syntacticstructure"))
//                {
//                    readTreeFormSyntacticStructure(sentence, element);
//                }
//            }
//
//            node = node.getNextSibling();
//        }
//
//        return sentence;
//    }
//
//    protected void readTreeFormSyntacticStructure(SSFSentence sentence, Element domElement)
//    {
//
//    }
//
//    public void printTreeForm(String filePath, String cs, PrintStream ps) throws Exception, FileNotFoundException, IOException
//    {
//    }
//
//    public void saveTreeForm(String filePath, String cs) throws Exception, FileNotFoundException, IOException
//    {
//        PrintStream ps = new PrintStream(filePath, cs);
//
//        printTreeForm(filePath, cs, ps);
//    }

    public static void main(String[] args)
    {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

        SSFStory story = new SSFStoryImpl();
//        SSFText text = null;

        try {
            fsp.read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                    GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                    GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
            ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
            FeatureStructuresImpl.setFSProperties(fsp);
            SSFNode.setSSFProperties(ssfp);
//            story.readFile("/extra/wmt12/training_set/target_system.spa.postaggedUS", "UTF-8"); //throws java.io.FileNotFoundException;
//            story.readFile("/extra/work/questimate/tmp/source.eng", "UTF-8"); //throws java.io.FileNotFoundException;
            story.readFile("/home/anil/sanchay-debug-data/poslcat-single-fs.txt", "UTF-8"); //throws java.io.FileNotFoundException;

            System.out.println("Sentences read: " + story.countSentences());
            
//            story.convertToPOSNolex();

            System.out.println(story.convertToPOSTagged("/"));
            
//            story.print(System.out);
//            story.readFile("/home/anil/docs/resources/CIIL-FORMALISED/Hindi/tagged-ciil-ssf-nlpai6.utf8/story_52_1c.final.mod.utf8"); //throws java.io.FileNotFoundException;
//            story.readFile("/home/anil/sanchay-debug-data/poslcat-single-fs.txt"); //throws java.io.FileNotFoundException;
//            story.readFile("/home/anil/myproj/sanchay/eclipse/Sanchay/workspace/syn-annotation/delhi-tasks/story_14_1a.final.mod"); //throws java.io.FileNotFoundException;
//            story.print(System.out);
//            ((SSFStoryImpl) story).readXML("tmp/ssf-xml-sample.xml.xml", "UTF-8");
//            ((SSFStoryImpl) story).printXML(System.out);
//            ((SSFStoryImpl) story).saveXML("tmp/ssf-xml-sample.xml", "UTF-8");
//            ((SSFStoryImpl) story).reallocateSentenceIDs();
//	    story.save("/home/anil/tmp/Corpus1/story_26_1.final.txt", "UTF-8");
//           SSFStory.getSSFText("ABC-5.txt", 3, 4).print(System.out);
	    
//	    SSFStoryImpl.clearAnnotationRecursive(
//		    new File("/home/anil/myproj/sanchay/eclipse/Sanchay/workspace/syn-annotation/preeti-tmp"),
//		    GlobalProperties.getIntlString("UTF-8"),
//		    SSFCorpus.LEX_MANDATORY_ATTRIBUTES | SSFCorpus.CHUNK_MANDATORY_ATTRIBUTES
//	    );

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
