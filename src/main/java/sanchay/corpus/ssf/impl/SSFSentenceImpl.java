package sanchay.corpus.ssf.impl;

import java.io.*;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;

import sanchay.common.types.SSFQueryOperatorType;
import sanchay.corpus.*;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.tree.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.corpus.ssf.query.QueryValue;
import sanchay.corpus.ssf.query.SSFQuery;
import sanchay.corpus.ssf.query.SSFQueryLexicalAnalyser;
import sanchay.corpus.xml.XMLProperties;
import sanchay.properties.KeyValueProperties;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.UtilityFunctions;
import sanchay.util.query.FindReplaceOptions;
import sanchay.util.query.SyntacticCorpusContextQueryOptions;
import sanchay.util.query.SyntacticCorpusQueryOptions;
import sanchay.xml.dom.GATEDOMElement;
import sanchay.xml.dom.SanchayDOMElement;
import sanchay.xml.dom.TypeCraftDOMElement;

public class SSFSentenceImpl extends Sentence
        implements Serializable, QueryValue, SSFSentence,
        SanchayDOMElement, TypeCraftDOMElement, GATEDOMElement
{
    protected String id;

    protected SSFPhrase root;

    protected FeatureStructure featureStructure;

    protected AlignmentUnit<SSFSentence> alignmentUnit;

    protected boolean emptyPhrasesAllowed;

    public SSFSentenceImpl()
    {
        alignmentUnit = new AlignmentUnit<SSFSentence>();
        featureStructure = new FeatureStructureImpl();
    }

    /**
     * @return the featureStructure
     */
    @Override
    public FeatureStructure getFeatureStructure()
    {
        return featureStructure;
    }

    /**
     * @param featureStructure the featureStructure to set
     */
    @Override
    public void setFeatureStructure(FeatureStructure featureStructure)
    {
        this.featureStructure = featureStructure;

        FeatureValue fvID = featureStructure.getAttributeValue("id");

        if(fvID != null)
        {
            setId((String) fvID.getValue());
        }
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String i)
    {
        id = i;

        featureStructure.addAttribute("id", id);
    }

    @Override
    public SSFPhrase getRoot()
    {
        return root;
    }

    @Override
    public void setRoot(SSFPhrase r)
    {
        root = r;
    }

    @Override
    public boolean emptyPhrasesAllowed()
    {
        return emptyPhrasesAllowed;
    }

    @Override
    public void emptyPhrasesAllowed(boolean e)
    {
        emptyPhrasesAllowed = e;
    }

    @Override
    public SSFNode findLeafByID(String id)
    {
        return getRoot().findLeafByID(id);
    }

    @Override
    public int findLeafIndexByID(String id)
    {
        return getRoot().findLeafIndexByID(id);
    }

    @Override
    public SSFNode findLeafByName(String id)
    {
        return getRoot().findLeafByName(id);
    }

    @Override
    public int findLeafIndexByName(String id)
    {
        return getRoot().findLeafIndexByName(id);
    }

    @Override
    public SSFNode findNodeByID(String id)
    {
        return getRoot().findNodeByID(id);
    }

    @Override
    public int findNodeIndexByID(String id)
    {
        return getRoot().findNodeIndexByID(id);
    }

    @Override
    public SSFNode findNodeByName(String id)
    {
        return getRoot().findNodeByName(id);
    }

    @Override
    public int findNodeIndexByName(String id)
    {
        return getRoot().findNodeIndexByName(id);
    }

    @Override
    public SSFNode findChildByID(String id)
    {
        return getRoot().findChildByID(id);
    }

    @Override
    public int findChildIndexByID(String id)
    {
        return getRoot().findChildIndexByID(id);
    }

    @Override
    public SSFNode findChildByName(String id)
    {
        return getRoot().findChildByName(id);
    }

    @Override
    public int findChildIndexByName(String id)
    {
        return getRoot().findChildIndexByName(id);
    }

    @Override
    public List<SSFNode> find(String nlabel, String ntext, String attrib, String val,
            String nlabelReplace, String ntextReplace, String attribReplace, String valReplace, boolean replace, boolean createAttrib, boolean exactMatch)
    {
//        System.out.println(nlabel+"\t"+ntext+"\t"+attrib+"\t"+val+"\t"+replace+"\t"+createAttrib);
        root = getRoot();
        //root.print(System.out);
        List<SSFNode> senNodes = null;
        List<SSFNode> senNodesForLabel;

        if(nlabel.equals("[.]*") == false)
        {
            senNodesForLabel = root.getNodesForName(nlabel);
            if(senNodesForLabel != null && senNodesForLabel.size() > 0) {
                senNodes = senNodesForLabel;
            }
        }

            List<SSFNode> senNodesForText;

            if(ntext.equals("[.]*") == false)
            {
//                senNodesForText = root.getNodesForLexData(ntext);
                senNodesForText = root.getNodesForText(ntext);


                if(senNodesForText != null)
                {
                    senNodes = (List) UtilityFunctions.getIntersection(senNodes, senNodesForText);
                }

                if(nlabel.equals("[.]*") == true && senNodes == null
                        && (senNodesForText != null && senNodesForText.size() > 0)) {
                    senNodes = senNodesForText;
                }
            }

            List<SSFNode> senNodesForAttrib;

            if(attrib.equals("[.]*") == false && val.equals("[.]*"))
            {
                senNodesForAttrib = root.getNodesForAttrib(attrib, exactMatch);

                if(senNodesForAttrib != null && createAttrib == false)
                {
                    senNodes = (List) UtilityFunctions.getIntersection(senNodes, senNodesForAttrib);
                }

                if((nlabel.equals("[.]*") == true && ntext.equals("[.]*") == true)
                        && senNodes == null && (senNodesForAttrib != null && senNodesForAttrib.size() > 0)) {
                    senNodes = senNodesForAttrib;
                }
            }

            List<SSFNode> senNodesForVal;

            if(val.equals("[.]*") == false)
            {
                senNodesForVal = root.getNodesForAttribVal(attrib, val, exactMatch);

                if(senNodesForVal != null)
                {
                    senNodes = (List) UtilityFunctions.getIntersection(senNodes, senNodesForVal);
                }

                if((nlabel.equals("[.]*") == true && ntext.equals("[.]*") == true && attrib.equals("[.]*") == true)
                        && senNodes == null && (senNodesForVal != null && senNodesForVal.size() > 0)) {
                    senNodes = senNodesForVal;
                }
            }

            if(senNodes != null)
            {
                if(replace)
                {
                    int repCount = senNodes.size();

                    for (int j = 0; j < repCount; j++)
                    {
                        SSFNode mnode = (SSFNode) senNodes.get(j);

                        if(UtilityFunctions.backFromExactMatchRegex(nlabel).equals(nlabelReplace) == false)
                        {
                            mnode.replaceNames(nlabel, nlabelReplace);
                        }

                        if(UtilityFunctions.backFromExactMatchRegex(ntext).equals(ntextReplace) == false)
                        {
                            mnode.replaceLexData(ntext, ntextReplace);
                        }

                        if(UtilityFunctions.backFromExactMatchRegex(attrib).equals(attribReplace) == false || val.equals(valReplace) == false)
                        {
                            mnode.replaceAttribVal(attrib, val, attribReplace, valReplace, createAttrib);
                        }
                    }
                }
            }
        return senNodes;
    }

    @Override
    public List<SSFNode> findContext(SyntacticCorpusContextQueryOptions contextOptions, boolean exactMatch)
    {
        List<SSFNode> senNodes;
        List<SSFNode> matchedNodes;

        SyntacticCorpusQueryOptions nodeOptions = contextOptions.getThisNodeOptions();
        String nlabel = "[.]*", ntext = "[.]*", attrib = "[.]*", val = "[.]*";
        String nlabelReplace="", ntextReplace="", attribReplace="", valReplace="";
        ntext = nodeOptions.getLexData();
        nlabel = nodeOptions.getTag();
        matchedNodes = find(nlabel, ntext, attrib, val, nlabelReplace, ntextReplace, attribReplace, valReplace, false, false, exactMatch);
        //root = getRoot();
        //matchedNodes = find3(root, nlabel, ntext, attrib, val, nlabelReplace, ntextReplace, attribReplace, valReplace, false,false);

        return matchedNodes;
    }

    // other methods

    @Override
    public void readFile(String fileName) throws Exception, FileNotFoundException, IOException //SSFSentence part
    {
        clear();

        List<SSFSentence> sentences = readSentencesFromFile(fileName);

        if(sentences == null || sentences.size() < 1) {
            throw new Exception();
        }

        SSFSentenceImpl sentence = (SSFSentenceImpl) sentences.get(0);
        id = sentence.getId();
        root = sentence.getRoot();
    }

    // Implementation parallel to the readString method
    public static boolean validateSSF(String string, List<String> errorLog /*Strings*/, int lineNum)
    {
	boolean validated = true;

        SSFProperties ssfp = SSFNode.getSSFProperties();

        String lines = "";
        String lineArray[] = string.split("\n");

        String sentenceStart = ssfp.getProperties().getPropertyValue("sentenceStart");
        String sentenceEnd = ssfp.getProperties().getPropertyValue("sentenceEnd");

        boolean start = false;

	int sentencesRead = 0;
        for(int i = 0; i < lineArray.length; i++)
        {
            lineArray[i] = lineArray[i].trim();

            if(lineArray[i].startsWith(sentenceStart))
//            if(lineArray[i].matches(sentenceStart))
            {
                start = true;
                lines = "";
            }
//            else if(lineArray[i].startsWith(sentenceEnd) && lines.equals("") == false)
            else if(lineArray[i].matches(sentenceEnd) && lines.equals("") == false)
            {
		validated = SSFPhrase.validateSSF(lines, errorLog, lineNum + i);

		if(validated) {
                    sentencesRead++;
                }
            }
            else if(start = true) {
                lines += lineArray[i] + "\n";
            }
        }

        if(sentencesRead < 1)
	{
	    validated = false;

	    if(errorLog != null)
	    {
		errorLog.add("************\n");
		errorLog.add(GlobalProperties.getIntlString("Empty_sentences_starting_from_line_") + lineNum + ": \n\n");
		errorLog.add(string);
		errorLog.add(GlobalProperties.getIntlString("************\n"));
	    }
	    else
	    {
		System.out.println("************");
		System.out.println(GlobalProperties.getIntlString("Empty_sentences_starting_from_line_") + lineNum + ": \n\n");
		System.out.println(string);
		System.out.println("************");
	    }
	}

	return validated;
    }

    @Override
    public void readString(String string, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    // reads SSF format, and makes a SSFTree of it
    {
        clear();

        List<SSFSentence> sentences = readSentencesFromString(string, errorLog, lineNum);

        if(sentences == null || sentences.size() < 1)
	{
	    if(errorLog != null)
	    {
		errorLog.add("************\n");
		errorLog.add(GlobalProperties.getIntlString("Empty_sentences_starting_from_line_") + lineNum + ": \n\n");
		errorLog.add(string);
		errorLog.add("************\n");
	    }
	    else
	    {
		System.out.println("************");
		System.out.println(GlobalProperties.getIntlString("Empty_sentences_starting_from_line_") + lineNum + ": \n\n");
		System.out.println(string);
		System.out.println("************");
//            throw new Exception();
	    }

	    SSFProperties ssfp = SSFNode.getSSFProperties();
            String rootName = ssfp.getProperties().getPropertyValue("rootName");

	    SSFPhrase rnode = new SSFPhrase("0", "", rootName, "");
	    setRoot(rnode);
	}
	else
	{
	    SSFSentenceImpl sentence = (SSFSentenceImpl) sentences.get(0);
	    id = sentence.getId();
            setFeatureStructure(sentence.getFeatureStructure());
	    root = sentence.getRoot();
	}
    }

    @Override
    public void readString(String string) throws Exception
    // reads SSF format, and makes a SSFTree of it
    {
	readString(string, null, 1);
    }

    public static List<SSFSentence> readSentencesFromFile(String fileName, List<String> errorLog /*Strings*/) throws Exception
    {
	SSFProperties ssfp = SSFNode.getSSFProperties();

        BufferedReader lnReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName),
                        ssfp.getProperties().getPropertyValue("encoding")));

        String line = "";
        String lines = "";

        while((line = lnReader.readLine()) != null)
        {
            if ((line.equals("") == false)) {
                lines += line + "\n";
            }
        }

        return readSentencesFromString(lines, errorLog, 1);
    }

    public static List<SSFSentence> readSentencesFromFile(String fileName) throws Exception
    {
	return readSentencesFromFile(fileName, null);
    }

    public static List<SSFSentence> readSentencesFromString(String string, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        List<SSFSentence> sentences = new ArrayList<SSFSentence>();

        String lines = "";
        String lineArray[] = string.split("\n");

        String sentenceStart = ssfp.getProperties().getPropertyValue("sentenceStart");
        String sentenceEnd = ssfp.getProperties().getPropertyValue("sentenceEnd");

        boolean start = false;

        SSFSentenceImpl sentence = null;

        for(int i = 0; i < lineArray.length; i++)
        {
            lineArray[i] = lineArray[i].trim();

            if(lineArray[i].startsWith(sentenceStart))
//            if(lineArray[i].matches(sentenceStart))
            {
                start = true;
                sentence = new SSFSentenceImpl();
                lines = "";

                Pattern p = Pattern.compile("\\s+[^=]+=[^=^>^ ]+");

                String fvString = "";

                Matcher m = p.matcher(lineArray[i]);

                while(m.find())
                {
                    fvString += m.group(0).trim() + " ";
                }

                sentence.featureStructure.readStringFV(fvString.replaceAll(">", "").trim());

                // Ad-hoc - change it to regex later
                String tmp[] = lineArray[i].split("id=");
                if(tmp.length == 2)
                {
                    tmp = tmp[1].split("\"");

                    if(tmp.length > 2) {
                        sentence.setId(tmp[1]);
                    }
                }
            }
            else if(lineArray[i].startsWith(sentenceEnd) && lines.equals("") == false)
//            else if(lineArray[i].matches(sentenceEnd) && lines.equals("") == false)
            {
                String chunkStart = ssfp.getProperties().getPropertyValue("chunkStart");
                String rootName = ssfp.getProperties().getPropertyValue("rootName");
//		rootName = new String(rootName.getBytes(), ssfp.getProperties().getPropertyValue("encoding"));

//		System.out.println(sentences.size());
//		System.out.println(lines);

		List<SSFNode> nodes = SSFPhrase.readNodesFromString(lines, errorLog, lineNum + i);

		if(nodes != null && nodes.size() > 0)
		{
		    if(nodes.size() == 1 && nodes.get(0).getClass().equals(SSFPhrase.class)
			    && ((SSFNode) nodes.get(0)).getName().equals(rootName))
		    {
			sentence.setRoot(((SSFPhrase) nodes.get(0)));
                        sentence.getRoot().removeEmptyPhrases();
		    }
		    else
		    {
			SSFPhrase rnode = new SSFPhrase(GlobalProperties.getIntlString("0"), "", rootName, "");

			for (int j = 0; j < nodes.size(); j++)
			{
			    rnode.addChild(((SSFNode) nodes.get(j)));
			}

//                        rnode.removeEmptyPhrases();
			sentence.setRoot(rnode);
		    }

		    sentences.add(sentence);
		}

                sentence = null;
//                System.out.println("One sentence read.");
            }
            else if(start = true) {
                lines += lineArray[i] + "\n";
            }
        }

        ((ArrayList) sentences).trimToSize();
        return sentences;
    }

    public static List<SSFSentence> readSentencesFromString(String string) throws Exception
    {
	return readSentencesFromString(string, null, 1);
    }

    @Override
    public void makeSentenceFromRaw(String rawSentence) throws Exception
    // takes a line of string, and makes an UNannotated and UNtagged SSF sentence
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        clear();

        String chunkStart = ssfp.getProperties().getPropertyValue("chunkStart");
        String rootName = ssfp.getProperties().getPropertyValue("rootName");
//	rootName = new String(rootName.getBytes(), ssfp.getProperties().getPropertyValue("encoding"));
//        root = new SSFPhrase("0", chunkStart, rootName, "");
        root = new SSFPhrase("0", "", rootName, "");

        // Splitting is done here only on the basis of spaces
        // Other things are assumed to have been taken care of by the tokenizer
        String words[] = rawSentence.split(" ");
//        String punctuationSeparator[];
//        Vector nodes = new Vector();
//
//        for (int i = 0; i < words.length; i++)
//        {
//            // check for starting punctuations
//            words[i] = checkForStart(",", words[i], nodes);
//            words[i] = checkForStart(";", words[i], nodes);
//            words[i] = checkForStart(":", words[i], nodes);
//            words[i] = checkForStart("(", words[i], nodes);
//            words[i] = checkForStart("?", words[i], nodes);
//            words[i] = checkForStart(")", words[i], nodes);
//            words[i] = checkForStart("!", words[i], nodes);
//            words[i] = checkForStart("|", words[i], nodes);
//
//            //check for end puncutations and add the word in the list
//            while (words[i].equals("") == false)
//            {
//                String temp = words[i];
//                words[i] = checkIntermediate(",", words[i], nodes);
//                words[i] = checkIntermediate("!", words[i], nodes);
//                words[i] = checkIntermediate(")", words[i], nodes);
//                words[i] = checkIntermediate(":", words[i], nodes);
//                words[i] = checkIntermediate("(", words[i], nodes);
//                words[i] = checkIntermediate("?", words[i], nodes);
//                words[i] = checkIntermediate(";", words[i], nodes);
//                words[i] = checkIntermediate(".", words[i], nodes);
//                words[i] = checkIntermediate("|", words[i], nodes);
//                // if there is no change then add the word directly...
//                if (words[i].equals(temp) == true)
//                {
//                    nodes.add(words[i]);
//                    break;
//                }
//            }
//        }

//        for (int i = 0; i < nodes.size(); i++)
        for (int i = 0; i < words.length; i++)
        {
//          System.out.println((String)nodes.get(i));

            if(words[i].equals("") == false) {
                root.addChild(new SSFLexItem("", words[i], "", ""));
            }
//            root.addChild(new SSFLexItem("", (String) nodes.get(i), "", ""));
        }

        root.reallocateId("");
    }

    @Override
    public void makeSentenceFromPOSTagged(String posTagged, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    // takes a line POS tagged of string, and makes a POS tagged SSF sentence
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        clear();

        String rootName = ssfp.getProperties().getPropertyValue("rootName");

	String wordTagSeparator = ssfp.getProperties().getPropertyValueForPrint("wordTagSeparator");

	root = new SSFPhrase("0", "", rootName, "");

        String words[] = posTagged.split(" ");

        for (int i = 0; i < words.length; i++)
        {
	    String wt[] = words[i].split("[" + wordTagSeparator + "]");
            
            if(wt.length == 0)
            {
                continue;
            }

	    if(wt.length != 2)
	    {
		if(errorLog == null) {
                    System.out.println("Wrong_format:_Data_is_not_in_simple_word/tag_format.");
                }
		else {
                    errorLog.add("Wrong_format:_Data_is_not_in_simple_word/tag_format.\n");
                }
                root.addChild(new SSFLexItem("", wt[0], "", ""));
	    }
            else
                root.addChild(new SSFLexItem("", wt[0], wt[1], ""));
        }

        root.reallocateId("");
    }

    @Override
    public void makeSentenceFromPOSTagged(String posTagged) throws Exception
    // takes a line POS tagged of string, and makes a POS tagged SSF sentence
    {
	makeSentenceFromPOSTagged(posTagged, null, 1);
    }

    @Override
    public void makeSentenceFromChunked(String chunked, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    // takes a line chunked (bracket form) of string, and makes a chunked SSF sentence
    {
	SSFProperties ssfp = SSFNode.getSSFProperties();

        clear();

        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        List<SSFNode> nodes = SSFPhrase.readNodesFromChunked(chunked, errorLog, lineNum);

        if(nodes != null && nodes.size() > 0)
        {
            if(nodes.size() == 1 && nodes.get(0).getClass().equals(SSFPhrase.class)
                    && ((SSFNode) nodes.get(0)).getName().equals(rootName))
            {
                setRoot(((SSFPhrase) nodes.get(0)));
                getRoot().removeEmptyPhrases();
        }
            else
            {
                SSFPhrase rnode = new SSFPhrase("0", "", rootName, "");

                for (int j = 0; j < nodes.size(); j++)
                {
                    rnode.addChild(((SSFNode) nodes.get(j)));
                }

//                rnode.removeEmptyPhrases();
                setRoot(rnode);
            }
        }
    }

    @Override
    public void makeSentenceFromChunked(String chunked) throws Exception
    // takes a line chunked (bracket form) of string, and makes a chunked SSF sentence
    {
	// TODO
	makeSentenceFromChunked(chunked, null, 1);
    }

    private String checkForStart(String punct,String word, List<String> node)
    {
        if (word.startsWith(punct)==true)
        {
            String punctWord[] = word.split("[" + punct + "]", 2);
            node.add(punct);
            return punctWord[1];
        }
        return word;
    }

    private String checkForEnd(String punct, String word, List<String> node)
    {
        if (word.endsWith(punct) == true)
        {
            String punctWord[] = word.split(punct, 2);
            //node.add()
            node.add(punct);
            return punctWord[1];
        }

        return word;
    }

    private String checkIntermediate(String punct, String word, List<String> node)
    {
        if (word.equals("") == false)
        {
            if ((word.contains(punct)) == true)
            {
                String punctWord[] = word.split("[" + punct + "]", 2);
                node.add(punctWord[0]);
                node.add(punct);

                if ((punctWord.length == 2) && (punctWord[1].equals("") == false)) {
                    return punctWord[1];
                }
                else {
                    return "" ;
                }
            }
            else {
                return word;
            }//"";
        }

        return "";
    }

    @Override
    public void print(PrintStream ps)
    {
        ps.print(makeString());
    }

    @Override
    public void print(PrintStream ps, CorpusType corpusType)
    {
        if(corpusType == CorpusType.SSF_FORMAT) {
            print(ps);
        }
        else if(corpusType == CorpusType.CHUNKED) {
            ps.print(convertToBracketForm(1));
        }
        else if(corpusType == CorpusType.POS_TAGGED) {
            ps.print(convertToPOSTagged());
        }
        else if(corpusType == CorpusType.RAW && getRoot() != null) {
            ps.print(convertToRawText());
        }
//	    ps.print(getRoot().makeRawSentence());
    }

    @Override
    public void save(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        print(ps);
    }

    @Override
    public String toString()
    {
        return convertToBracketForm(1);
    }

    @Override
    public String makeString()
    {
        root.reallocateId("");

        SSFProperties ssfp = SSFNode.getSSFProperties();

        String sentenceStart = ssfp.getProperties().getPropertyValueForPrint("sentenceStart");
        String sentenceEnd = ssfp.getProperties().getPropertyValueForPrint("sentenceEnd");

//        sentenceStart = sentenceStart.substring(1);
//        sentenceEnd = sentenceEnd.substring(1);
//
//        String sParts[] = sentenceStart.split("[\\|]");
//        String eParts[] = sentenceEnd.split("[\\|]");
//
//        sentenceStart = sParts[0];
//        sentenceEnd = eParts[0];
//
//        sentenceStart.replaceAll("[\\]", "");
//        sentenceEnd.replaceAll("[\\]", "");

        if(sentenceStart.startsWith("<"))
        {
            if(featureStructure.countAttributes() == 0) {
                sentenceStart += " id=\"" + id + "\">";
            }
            else {
                sentenceStart += " " + featureStructure.makeStringFV() + ">";
            }

            sentenceEnd += ">";
        }
        else
        {
            sentenceStart += " " + id;
        }

        String SSF = "";

        SSF = SSF + sentenceStart + "\n";

        String rootString = root.makeString();

        if(rootString.equals("")) {
            System.out.println(GlobalProperties.getIntlString("Empty_sentence:_id=") + id);
        }

        SSF = SSF + rootString;
        SSF = SSF + sentenceEnd + "\n";

        return SSF;
    }

    @Override
    public String convertToBracketForm(int spaces)
    {
//	String bracketForm = id + ". ";
	String bracketForm = "";

	bracketForm += getRoot().convertToBracketForm(spaces) + "\n";

	return bracketForm;
    }
    
    @Override
    public String makePOSNolex()
    {
        return root.makePOSNolex();
    }

    @Override
    public void convertToLowerCase()
    {
        root.convertToLowerCase();        
    }

    @Override
    public void convertToPOSNolex()
    {
        root.convertToPOSNolex();
    }

    @Override
    public String convertToPOSTagged(String sep)
    {
        return root.convertToPOSTagged(sep);
    }

    @Override
    public String convertToPOSTagged()
    {
        return root.convertToPOSTagged();
    }

    @Override
    public String convertToRawText()
    {
	String rawText = "";

        List<SanchayMutableTreeNode> leaves = root.getAllLeaves();

        for(int i = 0; i < leaves.size(); i++)
	{
	    SSFNode lv = (SSFNode) leaves.get(i);

	    rawText += lv.getLexData();

	    if(i < leaves.size() - 1) {
                rawText += " ";
            }
	    else {
                rawText += "\n";
            }
	}

	return rawText;
    }

    public static String convertSentenceString(String sentenceString, CorpusType inCorpusType, CorpusType outCorpusType)
    {
	String convertedString = "";

	SSFSentenceImpl sen = new SSFSentenceImpl();

	try {
	    if(inCorpusType == CorpusType.SSF_FORMAT) {
                sen.readString(sentenceString);
            }
	    else if(inCorpusType == CorpusType.CHUNKED) {
                sen.makeSentenceFromPOSTagged(sentenceString);
            }
	    else if(inCorpusType == CorpusType.POS_TAGGED) {
                sen.makeSentenceFromChunked(sentenceString);
            }
	    else if(inCorpusType == CorpusType.RAW && sen.getRoot() != null) {
                sen.makeSentenceFromRaw(sentenceString);
            }

	    if(outCorpusType == CorpusType.SSF_FORMAT) {
                convertedString = sen.makeString();
            }
	    else if(outCorpusType == CorpusType.CHUNKED) {
                convertedString = sen.convertToPOSTagged();
            }
	    else if(outCorpusType == CorpusType.POS_TAGGED) {
                convertedString = sen.convertToBracketForm(1);
            }
	    else if(outCorpusType == CorpusType.RAW && sen.getRoot() != null) {
                convertedString = sen.convertToRawText();
            }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return convertedString;
    }

    @Override
    public String getUnannotated()
    {
        String sen = "";

        List<SanchayMutableTreeNode> leaves = root.getAllLeaves();

        for(int i = 0; i < leaves.size(); i++) {
            sen += ((SSFNode) leaves.get(i)).getLexData();
        }

        return sen;
    }

    @Override
    public Sentence getCopy() throws Exception
    {
        String str = makeString();

        SSFSentenceImpl sen = new SSFSentenceImpl();
        sen.readString(str);

    	sen.getRoot().allowNestedFS(getRoot().allowsNestedFS());

        sen.getRoot().copyExtraData(getRoot());
        
        sen.alignmentUnit = (AlignmentUnit<SSFSentence>) alignmentUnit.clone();

        return sen;
    }

    @Override
    public void clear()
    {
        id = "";
        root = null;
    }

    @Override
    public void clearFeatureStructures()
    {
	getRoot().clearFeatureStructures();

	int count = getRoot().countChildren();

	for(int i = 0; i < count; i++)
	{
	    // Add hoc
	    getRoot().getChild(i).addDefaultAttributes();
	}
    }

    @Override
    public void clearAnnotation(long annoLevelFlags)
    {
	getRoot().clearAnnotation(annoLevelFlags);
    }

    @Override
    public boolean matches(FindReplaceOptions findReplaceOptions)
    {
        if(root == null) {
            return false;
        }

        return root.matches(findReplaceOptions);
    }

    @Override
    public void setMorphTags(KeyValueProperties morphTags)
    {
        root.setMorphTags(morphTags);
    }

    @Override
    public void clearHighlights()
    {
        getRoot().clearHighlights();
    }

    @Override
    public SSFSentence getAlignedObject(String alignmentKey)
    {
        return alignmentUnit.getAlignedObject(alignmentKey);
    }
    
    @Override
    public List<SSFSentence> getAlignedObjects()
    {
        return alignmentUnit.getAlignedObjects();
    }
    

    @Override
    public SSFSentence getFirstAlignedObject()
    {
        return alignmentUnit.getFirstAlignedObject();
    }

    @Override
    public SSFSentence getAlignedObject(int i)
    {
        return alignmentUnit.getAlignedObject(i);
    }

    @Override
    public SSFSentence getLastAlignedObject()
    {
        return alignmentUnit.getLastAlignedObject();
    }

    @Override
    public AlignmentUnit<SSFSentence> getAlignmentUnit()
    {
        return alignmentUnit;
    }

    @Override
    public void setAlignmentUnit(AlignmentUnit alignmentUnit)
    {
        this.alignmentUnit = alignmentUnit;
    }

    @Override
    public void loadAlignments(SSFSentence tgtSentence, int parallelIndex)
    {
        if(getRoot() == null) {
            return;
        }

        getRoot().loadAlignments(this, tgtSentence, parallelIndex);
    }

    @Override
    public void saveAlignments()
    {
        if(getRoot() == null) {
            return;
        }

        getRoot().saveAlignments();
    }

    @Override
    public void clearAlignments()
    {
        if(getRoot() == null) {
            return;
        }

        getRoot().clearAlignments(0, getRoot().countChildren());
    }

    @Override
    public Object getQueryReturnValue()
    {
        return this;
    }

    @Override
    public void setQueryReturnValue(Object rv)
    {

    }

    @Override
    public Object getQueryReturnObject()
    {
        return this;
    }

    @Override
    public void setQueryReturnObject(Object rv)
    {

    }

    @Override
    public void setAttributeValue(String attibName, String val)
    {
        if(featureStructure == null) {
            featureStructure = new FeatureStructureImpl();
        }

        featureStructure.setAttributeValue(attibName, val);
    }

    @Override
    public LinkedHashMap<QueryValue, String> getMatchingValues(SSFQuery ssfQuery)
    {
        SSFPhrase rNode = getRoot();

        LinkedHashMap<QueryValue, String> matches = new LinkedHashMap<QueryValue, String>();

        if(ssfQuery.getRootMatchNode().getOperator().equals(SSFQueryOperatorType.ON_DS))
        {
            LinkedHashMap cfgToMMTreeMapping = new LinkedHashMap(0, 10);

            SSFPhrase mmNode = ((SSFPhrase) rNode).convertToGDepNode(cfgToMMTreeMapping, false);

            if(mmNode == null) {
                return matches;
            }

//            mmNode.expandMMTree(cfgToMMTreeMapping);

            matches = mmNode.getMatchingValues(ssfQuery);
        }
        else if(ssfQuery.getRootMatchNode().getOperator().equals(SSFQueryOperatorType.COMMAND))
        {
            String commandString = (String) ssfQuery.getRootMatchNode().getUserObject();

            Matcher cmdReallocateIDsMatcher = SSFQueryLexicalAnalyser.cmdReallocateIDs.matcher(commandString);
            Matcher cmdReallocateNamesMatcher = SSFQueryLexicalAnalyser.cmdReallocateNames.matcher(commandString);
            Matcher cmdReallocatePosMatcher = SSFQueryLexicalAnalyser.cmdReallocatePosn.matcher(commandString);

            if(cmdReallocateIDsMatcher.find())
            {
                getRoot().reallocateId("");
            }
            else if(cmdReallocateNamesMatcher.find())
            {
                getRoot().reallocateNames(null, null);
            }
            else if(cmdReallocatePosMatcher.find())
            {
                getRoot().reallocatePositions("posn", "null");
            }
        }
        else {
            matches = rNode.getMatchingValues(ssfQuery);
        }

        return matches;
    }

    @Override
    public void reallocatePositions(String positionAttribName, String nullWordString)
    {
        getRoot().reallocatePositions(positionAttribName, nullWordString);
    }

    @Override
    public void convertEncoding(SanchayEncodingConverter encodingConverter, String nullWordString)
    {
        getRoot().convertEncoding(encodingConverter, nullWordString);
    }

    @Override
    public DOMElement getDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("sentenceTag"));

        DOMAttribute attribDOM = new DOMAttribute(new org.dom4j.QName("id"), id);
        domElement.add(attribDOM);

        DOMElement idomElement = ((SanchayDOMElement)featureStructure).getDOMElement();
        domElement.add(idomElement);

        idomElement = root.getDOMElement();
        domElement.add(idomElement);

        return domElement;
    }

    @Override
    public DOMElement getTypeCraftDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcPhraseTag"));

        DOMAttribute attribDOM = new DOMAttribute(new org.dom4j.QName("id"), id);
        domElement.add(attribDOM);

        DOMElement idomElement = ((SanchayDOMElement)featureStructure).getDOMElement();
        domElement.add(idomElement);

        idomElement = root.getDOMElement();
        domElement.add(idomElement);

        return domElement;
    }

    @Override
    public DOMElement getGATEDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcPhraseTag"));

        DOMAttribute attribDOM = new DOMAttribute(new org.dom4j.QName("id"), id);
        domElement.add(attribDOM);

        DOMElement idomElement = ((SanchayDOMElement)featureStructure).getDOMElement();
        domElement.add(idomElement);

        idomElement = root.getDOMElement();
        domElement.add(idomElement);

        return domElement;
    }

    @Override
    public String getXML() {
        String xmlString = "";

        org.dom4j.dom.DOMElement element = getDOMElement();
        xmlString = element.asXML();

        return "\n" + xmlString + "\n";
    }

    @Override
    public String getTypeCraftXML() {
        String xmlString = "";

        org.dom4j.dom.DOMElement element = getTypeCraftDOMElement();
        xmlString = element.asXML();

        return "\n" + xmlString + "\n";
    }

    @Override
    public String getGATEXML() {
        String xmlString = "";

        org.dom4j.dom.DOMElement element = getGATEDOMElement();
        xmlString = element.asXML();

        return "\n" + xmlString + "\n";
    }

    @Override
    public void readXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("fsTag")))
                {
                    ((SanchayDOMElement) featureStructure).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("nodeTag")))
                {
                    try {
                        root = new SSFPhrase("0", "", rootName, "");
                    } catch (Exception ex) {
                        Logger.getLogger(SSFSentenceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    root.readXML(element);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readTypeCraftXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        try {
            root = new SSFPhrase("0", "", rootName, "");
        } catch (Exception ex) {
            Logger.getLogger(SSFSentenceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        root.setLexData(xmlProperties.getProperties().getPropertyValue("tcPhraseTag"));

        SSFPhrase word = null;

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("fsTag")))
                {
                    ((SanchayDOMElement) featureStructure).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("tcWordTag")))
                {
                    try {
                        word = new SSFPhrase("0", "", "word", "");
                    } catch (Exception ex) {
                        Logger.getLogger(SSFSentenceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    word.readTypeCraftXML(element);
                    root.addChild(word);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readGATEXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        try {
            root = new SSFPhrase("0", "", rootName, "");
        } catch (Exception ex) {
            Logger.getLogger(SSFSentenceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        root.setLexData(xmlProperties.getProperties().getPropertyValue("tcPhraseTag"));

        SSFPhrase word = null;

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("fsTag")))
                {
                    ((SanchayDOMElement) featureStructure).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("tcWordTag")))
                {
                    try {
                        word = new SSFPhrase("0", "", "word", "");
                    } catch (Exception ex) {
                        Logger.getLogger(SSFSentenceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    word.readGATEXML(element);
                    root.addChild(word);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void printXML(PrintStream ps) {
        ps.println(getXML());
    }

    @Override
    public void printTypeCraftXML(PrintStream ps) {
        ps.println(getTypeCraftXML());
    }

    @Override
    public void printGATEXML(PrintStream ps) {
        ps.println(getGATEXML());
    }

    @Override
    public LinkedHashMap<String, Integer> getWordFreq()
    {
        return getRoot().getWordFreq();
    }

    @Override
    public LinkedHashMap<String, Integer> getPOSTagFreq()
    {
        return getRoot().getPOSTagFreq();
    }

    @Override
    public int countWords()
    {
        return getRoot().getAllLeaves().size();
    }

    @Override
    public int countPOSTags()
    {
        LinkedHashMap<String, Integer> tags = getPOSTagFreq();

        return tags.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getWordTagPairFreq()
    {
        return getRoot().getWordTagPairFreq();
    }

    @Override
    public int countWordTagPairs()
    {
        return getRoot().getWordTagPairFreq().size();
    }

    @Override
    public LinkedHashMap<String, Integer> getChunkTagFreq()
    {
        return getRoot().getChunkTagFreq();
    }

    @Override
    public int countChunkTags()
    {
        LinkedHashMap<String, Integer> tags = getChunkTagFreq();

        return tags.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getGroupRelationFreq()
    {
        return getRoot().getGroupRelationFreq();
    }

    @Override
    public int countGroupRelations()
    {
        LinkedHashMap<String, Integer> rels = getGroupRelationFreq();

        return rels.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getChunkRelationFreq()
    {
        return getRoot().getChunkRelationFreq();
    }

    @Override
    public int countChunkRelations()
    {
        LinkedHashMap<String, Integer> rels = getChunkRelationFreq();

        return rels.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getAttributeFreq()
    {
        return getRoot().getAttributeFreq();
    }

    @Override
    public int countAttributes()
    {
        LinkedHashMap<String, Integer> attribs = getAttributeFreq();

        return attribs.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getAttributeValueFreq()
    {
        return getRoot().getAttributeValueFreq();
    }

    @Override
    public int countAttributeValues()
    {
        LinkedHashMap<String, Integer> attribs = getAttributeValueFreq();

        return attribs.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getAttributeValuePairFreq()
    {
        return getRoot().getAttributeValuePairFreq();
    }

    @Override
    public int countAttributeValuePairs()
    {
        LinkedHashMap<String, Integer> attribs = getAttributeValuePairFreq();

        return attribs.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getUntaggedWordFreq()
    {
        LinkedHashMap<String, Integer> allWords = new LinkedHashMap();
//
//        int scount = countSentences();
//
//        for (int i = 0; i < scount; i++)
//        {
//            SSFSentence sentence = getSentence(i);
//
//            Vector<SSFNode> nodes = sentence.getRoot().getNodesForName("");
//
//            int ncount = nodes.size();
//
//            for (int j = 0; j < ncount; j++)
//            {
//                SSFNode node = nodes.get(i);
//
//                if(node instanceof SSFLexItem)
//                    count++;
//            }
//        }

        return allWords;
    }

    @Override
    public int countUntaggedWords()
    {
        int count = 0;

        List<SSFNode> nodes = getRoot().getNodesForName("");

        int ncount = nodes.size();

        for (int i = 0; i < ncount; i++)
        {
            SSFNode node = nodes.get(i);

            if(node instanceof SSFLexItem) {
                count++;
            }
        }

        return count;
    }

    /**
     *
     * @return
     */
    @Override
    public LinkedHashMap<String, Integer> getUnchunkedWordFreq()
    {
        return getRoot().getUnchunkedWordFreq();
    }

    @Override
    public int countUnchunkedWords()
    {
        return getRoot().getUnchunkedWordFreq().size();
    }

    @Override
    public int countUntaggedChunks()
    {
        int count = 0;

        List<SSFNode> nodes = getRoot().getNodesForName("");

        int ncount = nodes.size();

        for (int i = 0; i < ncount; i++)
        {
            SSFNode node = nodes.get(i);

            if(node instanceof SSFPhrase) {
                count++;
            }
        }

        return count;
    }

    @Override
    public double countCharacters()
    {
        List<SanchayMutableTreeNode> leaves = getRoot().getAllLeaves();

        int lcount = leaves.size();
        
        double length = 0.0;

        for (int j = 0; j < lcount; j++) {

            length += ((SSFLexItem) leaves.get(j)).getLexData().length();

        }        
        
        return length;
    }

    @Override
    public double getEntropyLexical()
    {
        double entropy = 0.0;
        
        LinkedHashMap<String, Integer> wordFreq = getWordFreq();
        
        int wcount = UtilityFunctions.getTotalValue(wordFreq);
        
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            String word = entry.getKey();
            Integer freq = entry.getValue();
            double prob =  (double) freq / (double) wcount;
            
            entropy += prob * Math.log(prob);
        }
        
        return -1 * entropy;
    }

    @Override
    public double getEntropyPOS()
    {
        double entropy = 0.0;
        
        LinkedHashMap<String, Integer> posTagFreq = getPOSTagFreq();

        int pcount = UtilityFunctions.getTotalValue(posTagFreq);
        
        for (Map.Entry<String, Integer> entry : posTagFreq.entrySet()) {
            String pos = entry.getKey();
            Integer freq = entry.getValue();
            double prob =  (double) freq / (double) pcount;
            
            entropy += prob * Math.log(prob);
        }
        
        return -1 * entropy;
    }

    @Override
    public double getEntropyLexicalPOS()
    {
        double entropy = 0.0;
        
        LinkedHashMap<String, Integer> wordTagFreq = getWordTagPairFreq();
        
        int wtcount = UtilityFunctions.getTotalValue(wordTagFreq);
        
        for (Map.Entry<String, Integer> entry : wordTagFreq.entrySet()) {
            String wordTag = entry.getKey();
            Integer freq = entry.getValue();
            double prob =  (double) freq / (double) wtcount;
            
            entropy += prob * Math.log(prob);
        }
        
        return -1 * entropy;
    }

    public static void main(String[] args)
    {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();
        SSFSentenceImpl stc = new SSFSentenceImpl();

        try {
            fsp.read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                    GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                    GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
            ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
            SSFNode.setSSFProperties(ssfp);
            FeatureStructuresImpl.setFSProperties(fsp);

	    stc.readFile("/home/anil/tmp/ssf-sentence-1.txt"); //throws java.io.FileNotFoundException;
            stc.print(System.out);

//	    System.out.println("/////////////////////////////////////");
//
//	    SSFSentenceImpl stcCopy = new SSFSentenceImpl();
//	    stcCopy.readString(stc.makeString());
//            stcCopy.print(System.out);

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
//
//        try {
//            //Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
//            stc.print(System.out);
//            System.out.println("/////////////////////////////////////");
////
////            SSFPhrase test = (SSFPhrase) stc.getRoot().getChild(3); // fourth child
////            test.print(System.out);
////		    System.out.println("/////////////////////////////////////");
//
////          test.removeLayer();
////		    stc.getRoot().reallocateId("");
////		    stc.print(System.out);
////		    System.out.println("/////////////////////////////////////");
//
////            stc.root.addChild(new SSFPhrase("", "", "NP", "", stc.root));
////		    stc.getRoot().reallocateId("");
////		    stc.print(System.out);
////		    System.out.println("/////////////////////////////////////");
//
////		    stc.root.addChild(new SSFLexicalItem("", "intently", "RB", "", stc.root));
////		    stc.getRoot().reallocateId("");
////		    stc.print(System.out);
////		    System.out.println("/////////////////////////////////////");
//
////		    stc.root.addChildAt(new SSFLexicalItem("", "The", "DT", "", stc.root), 0);
////		    stc.getRoot().reallocateId("");
////		    stc.print(System.out);
////		    System.out.println("/////////////////////////////////////");
//
////		    stc.makeSentenceFromRaw("Hindi competition would start at four thirty in CR 104.");
////		    stc.print(System.out);
////		    System.out.println("/////////////////////////////////////");
//
//        } catch (Exception e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
    }
}
