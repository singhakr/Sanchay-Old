/*
 * RawCorpusPreprocessor.java
 *
 * Created on February 27, 2006, 2:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class RawCorpusPreprocessor {
    
    protected String inFilePath;
    protected String inCharset;
    protected String outFilePath;
    protected String outCharset;

    protected String currentParagraph;
    
    protected BufferedReader inReader;
    protected PrintStream outStream;
    
    protected static int CHAR_WINDOW = 5;

    protected static int DEFAULT_PARAGRAPH_SIZE = 5;
    protected static int DEFAULT_SENTENCE_SIZE = 5;
    protected static int DEFAULT_WORD_SIZE = 5;

    protected static final int PARAGRAPH = 0;
    protected static final int SENTENCE = 1;
    protected static final int WORD = 2;
    
    /** Creates a new instance of RawCorpusPreprocessor */
    public RawCorpusPreprocessor() {
    }
    
    public void init(String inFile, String ics) throws FileNotFoundException, IOException
    {
	inFilePath = inFile;
	inCharset = ics;

	if(inCharset.equals("") == false)
	    inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), inCharset));
	else
	    inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
    }
    
    public boolean hasMore() throws FileNotFoundException, IOException
    {
	if(inReader == null || inReader.ready() == false)
	    return false;
	
	return true;
    }
    
    public Object nextSegments(int mode) throws FileNotFoundException, IOException
    {
	switch(mode)
	{
	    case PARAGRAPH:
		return nextParagraph();
		
	    case SENTENCE:
		return nextSentence();

	    case WORD:
		return nextWord();
	}
	
	return null;
    }
    
    protected String nextParagraph() throws FileNotFoundException, IOException
    {
	if(inReader == null || inReader.ready() == false)
	    return null;

	LinkedList list = new LinkedList();
	
	char c = 0;
	currentParagraph = "";
	
	while((c = (char) inReader.read()) != -1)
	{
//	    if(c == '\n' && list.)
//	    {
//		break;
//	    }
//	    else
//	    {
//		currentParagraph += c;
//	    }
//
//	    if(list.size() < CHAR_WINDOW)
//		list.add(new Character(c));
//	    else
//	    {
//		list.removeFirst();
//		list.add(new Character(c));
//	    }
	}
	
	if(c == -1)
	{
	    inReader.close();
	    inReader = null;
	}
	
	String para = currentParagraph;
	
    return para;
    }
    
    public String nextSentence() throws FileNotFoundException, IOException
    {
	return null;
    }
    
    public String nextWord() throws FileNotFoundException, IOException
    {
	return null;
    }
    
    public static Vector segmentSentences(String paragraph)
    {
	Vector sentences = new Vector(DEFAULT_PARAGRAPH_SIZE, DEFAULT_PARAGRAPH_SIZE);
	
	String EOL = "__EOL__";

	Pattern p = Pattern.compile("[\n]");
	Matcher m = p.matcher(paragraph);
//	paragraph = 
	
//	Pattern p = Pattern.compile("([|\\?!])");
//	Matcher m = p.matcher(paragraph);
//
//	if(m.)
//	sentences.add(m.replaceAll(m.group(1) + "\n"));

	String senStrings[] = paragraph.split("[\n]");
	
	for (int i = 0; i < senStrings.length; i++)
	{
	    sentences.add(senStrings[i]);
	}
	
	return sentences;
    }
    
    public static Vector segmentWords(String sentence)
    {
	Vector words = new Vector(DEFAULT_SENTENCE_SIZE, DEFAULT_SENTENCE_SIZE);
	
	Pattern p = Pattern.compile("([\\(\\),;\\/])");
	Matcher m = p.matcher(sentence);

	words.add(m.replaceAll(" " + m.group(1) + " "));

	String wrdStrings[] = sentence.split(" ");
	
	for (int i = 0; i < wrdStrings.length; i++)
	{
	    words.add(wrdStrings[i].trim());
	}
	
	return words;
    }
    
    public void preprocess(String inFile, String ics, String outFile, String ocs, int mode, String langEnc) throws FileNotFoundException, IOException
    {
	init(inFile, ics);
	
	outStream = new PrintStream(outFile, ocs);

	while(hasMore())
	{
	    switch(mode)
	    {
		case PARAGRAPH:
		    outStream.println(nextParagraph());

		case SENTENCE:
		    String sentence = nextSentence();

//		    int scount = sentences.size();
//
//		    for (int i = 0; i < scount; i++)
//			outStream.println((String) sentences.get(i));

		case WORD:
		    String words = nextWord();

//		    int wcount = words.size();
//
//		    for (int i = 0; i < wcount; i++)
//			outStream.println((String) words.get(i));
	    }
	}
	
	outStream.close();
	outStream = null;
    }
    
    public static void main(String[] args)
    {
	RawCorpusPreprocessor rcp = new RawCorpusPreprocessor();
	try {
	    rcp.preprocess("/home/anil/NEWUTF-split/hindi/allutf/101-utf-1", GlobalProperties.getIntlString("UTF-8"),
		    "/home/anil/tmp/101-utf-1.preprocessed", GlobalProperties.getIntlString("UTF-8"), RawCorpusPreprocessor.PARAGRAPH, GlobalProperties.getIntlString("hin::utf8"));
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	
//	rcp.init("/home/anil/NEWUTF-split/hindi/allutf/101-utf-1", "UTF-8");
//	while(rcp.hasMore())
//	{
//	    
//	}
    }
}
