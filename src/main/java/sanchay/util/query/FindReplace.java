/*
 * FindReplace.java
 *
 * Created on March 8, 2006, 7:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util.query;

import java.awt.Color;
import java.io.*;
//import java.awt.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jaxe.JaxeFrame;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sanchay.GlobalProperties;
import sanchay.corpus.manager.gui.NGramLMJPanel;
import sanchay.resources.shabdanjali.ShabdanjaliDict;
import sanchay.xml.gui.GenericQuery;
import sanchay.common.types.*;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.gui.SyntacticAnnotationWorkJPanel;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.*;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.mlearning.lm.ngram.impl.NGramLMImpl;
import sanchay.text.editor.gui.TextEditorJPanel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class FindReplace {
    
    protected FindReplaceOptions findReplaceOptions;

    protected File inDirectoryFile;
    protected File outDirectoryFile;
    
    protected DefaultComboBoxModel fileList;
    
    protected JList fileJList;
    protected JPanel contentJPanel;

    // For batch mode
    protected File currentFile;

    // For Chunked and SSFTagged corpus
    protected int currentSentence;
    protected SSFNode currentParentNode;
    protected int currentChildIndex;
    
    protected Pattern pattern;
    protected Matcher matcher;

    protected Color highlightColor;
    protected Color selectionColor;

    protected int oldStart;
    protected int oldEnd;
    
    public static final int NOT_FOUND = 0;
    
    /** Creates a new instance of FindReplace */
    public FindReplace(FindReplaceOptions options, JPanel contentPanel)
            throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, Exception
    {
	super();

	findReplaceOptions = options;
	findReplaceOptions.standAlone = false;
	
	contentJPanel = contentPanel;

	prepare(options);
   }

    public FindReplace(FindReplaceOptions options, JList jlist, JPanel contentPanel)
            throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, Exception
    {
	super();
	
	findReplaceOptions = options;
	findReplaceOptions.standAlone = true;
	
	fileJList = jlist;
	contentJPanel = contentPanel;

	prepare(options);
   }

    
    
    private void prepare(FindReplaceOptions options)
            throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, Exception
    {
	findReplaceOptions = options;
	
	inDirectoryFile = new File(findReplaceOptions.inDirectory);
	outDirectoryFile = new File(findReplaceOptions.outDirectory);
	
	highlightColor = new Color(Color.YELLOW.getRGB());
	selectionColor = new Color(Color.GRAY.getRGB());
	
	if(findReplaceOptions.batchMode)
        {
	    fileList = new DefaultComboBoxModel();
	    fileJList.setModel(fileList);
	    
	    findAllBatch(inDirectoryFile);
	}
    }

    // Could be directory as well as file
    private File getOutputFile(File inDirFile) throws FileNotFoundException, IOException
    {
	if(findReplaceOptions.outDirectory == null || findReplaceOptions.outDirectory.equals("")) {
            return null;
        }
	
        File odFile = null;
        
        if(findReplaceOptions.recreateDirStr)
        {
            String topInPath = inDirectoryFile.getAbsolutePath();
            String inPath = inDirFile.getAbsolutePath();
            String inPathSuffix = inPath.replaceFirst(topInPath, "");

            odFile = new File(outDirectoryFile, inPathSuffix);
        }
        else if(inDirFile.isDirectory()) {
            odFile = outDirectoryFile;
        }
        else
        {
            String topInPath = inDirectoryFile.getAbsolutePath();
            String inPath = inDirFile.getAbsolutePath();
            String inPathSuffix = inPath.replaceFirst(topInPath, "");
            inPathSuffix = inPathSuffix.replaceFirst(File.separator, "");
            inPathSuffix = inPathSuffix.replaceAll(File.separator, "-");

            odFile = new File(outDirectoryFile, inPathSuffix);
        }

        return odFile;
    }
    
    private File setupOutputDirectory(File outDirFile, boolean cln) throws FileNotFoundException, IOException
    {
	if(findReplaceOptions.outDirectory == null || findReplaceOptions.outDirectory.equals("")) {
            return null;
        }

	if(outDirFile.exists() == true)
        {
            if(outDirFile.canWrite() == false) {
                throw new FileNotFoundException(GlobalProperties.getIntlString("No_write_permission_for_directory:_")+ outDirFile.getAbsolutePath());
            }
            else if(outDirFile.isDirectory() == false) {
                throw new FileNotFoundException(GlobalProperties.getIntlString("Not_a_directory:_") + outDirFile.getAbsolutePath());
            }
        }
        else
        {
            // Create directory:
            System.out.println(GlobalProperties.getIntlString("Creating_directory:_") + outDirFile.getAbsolutePath());
            
            outDirFile.mkdirs();
        }
        
        if(outDirFile.getAbsolutePath().equals(outDirectoryFile.getAbsolutePath()) && cln == true)
        {
            UtilityFunctions.removeDirectoryRecursive(outDirFile);
            outDirFile.mkdirs();
        }
        
        return outDirFile;
    }

    // For listing all the matching documents
    protected int findAllBatch(File inDir) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, Exception
    {
	if(findReplaceOptions.readCorpusType == CorpusType.RAW
		|| findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
            return findAllRawBatch(inDir);
        }
	else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		|| findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
            return findAllSSFBatch(inDir);
        }
        else if(findReplaceOptions.readCorpusType == CorpusType.XML_FORMAT) {
            return findAllXMLBatch(inDir);
        }
        else if(findReplaceOptions.readCorpusType == CorpusType.NGRAM) {
            return findAllNGramBatch(inDir);
        }
        else if(findReplaceOptions.readCorpusType == CorpusType.DICTIONARY) {
            return findAllDictBatch(inDir);
        }
	    
	return NOT_FOUND;
    }
    
    public int saveOutput(File inDir) throws FileNotFoundException, IOException
    {
	if(findReplaceOptions.readCorpusType == CorpusType.RAW
		|| findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
            return saveOutputRaw(inDir);
        }
	else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		|| findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
            return saveOutputSSF(inDir);
        }
        else if(findReplaceOptions.readCorpusType == CorpusType.DICTIONARY) {
            return saveOutputDict(inDir);
        }
	    
	return NOT_FOUND;
    }

    // For finding all matches in one document
    // To be used only for highlighting
    // Will be called when:
    // In non-stand-alone mode: when the users presses Find button for the first time
    // In stand-alone mode: when the users selects one document from the list of matching documents
    // In extract mode, extractFrom methods will be called
    public int findAll(File inFile) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException, XPathExpressionException
    {
	currentFile = inFile;
	
	if(findReplaceOptions.extractionMode)
	{
	    if(findReplaceOptions.readCorpusType == CorpusType.RAW
		    || findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
                return extractFromRaw(inFile, false);
            }
	    else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		    || findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
                return extractFromSSF(inFile, false);
            }
            else if(findReplaceOptions.readCorpusType == CorpusType.XML_FORMAT) {
                return extractFromXML(inFile, false);
            }
            else if(findReplaceOptions.readCorpusType == CorpusType.NGRAM) {
                return extractFromNGram(inFile, false);
            }
            else if(findReplaceOptions.readCorpusType == CorpusType.DICTIONARY) {
                return extractFromDict(inFile, false);
            }
	}
	else
	{
	    if(findReplaceOptions.readCorpusType == CorpusType.RAW
		    || findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
                return findAllRaw(inFile);
            }
	    else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		    || findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
                return findAllSSF(inFile);
            }
            else if(findReplaceOptions.readCorpusType == CorpusType.XML_FORMAT) {
                return findAllXML(inFile);
            }
        //else if(findReplaceOptions.readCorpusType == CorpusType.NGRAM)
        //    return findAllNGram(inFile);
	}
	    
	return NOT_FOUND;
    }
    
    // For checking if a document matches
    public int matchDocument(File inFile) throws FileNotFoundException, IOException, Exception
    {
	if(findReplaceOptions.readCorpusType == CorpusType.RAW
		|| findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
            return matchDocumentRaw(inFile);
        }
	else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		|| findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
            return matchDocumentSSF(inFile);
        }

	return NOT_FOUND;
    }

    // For finding the next match in one document
    public int find()
    {
	if(findReplaceOptions.readCorpusType == CorpusType.RAW
		|| findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
            return findRaw();
        }
	else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		|| findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
            return findSSF();
        }

	return NOT_FOUND;
    }
    
    // For replacing the next match in one document
    public int replace()
    {
	if(findReplaceOptions.readCorpusType == CorpusType.RAW
		|| findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
            return replaceRaw();
        }
	else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		|| findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
            return replaceSSF();
        }

	return NOT_FOUND;
    }

    public int replaceAll(File inFile)
    {
	currentFile = inFile;
	
	if(findReplaceOptions.readCorpusType == CorpusType.RAW
		|| findReplaceOptions.readCorpusType == CorpusType.POS_TAGGED) {
            return replaceAllRaw(inFile);
        }
	else if(findReplaceOptions.readCorpusType == CorpusType.CHUNKED
		|| findReplaceOptions.readCorpusType == CorpusType.SSF_FORMAT) {
            return replaceAllSSF(inFile);
        }
	    
	return NOT_FOUND;
    }




    // For Raw and POSTagged corpus
    protected int findAllRawBatch(File inDir) throws FileNotFoundException, IOException // For batch mode
    {
	int findCount = NOT_FOUND;
	
        if(inDir.isFile() == true)
        {
	    findCount = matchDocumentRaw(inDir);
	    
	    if(findCount > 0)
            {
                    fileList.addElement(inDir);
            }

	    return findCount;
        }
        else
        {
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    findCount += findAllRawBatch(files[i]);
                }
            }
        }
	return findCount;
    }

    protected int findAllXMLBatch(File inDir) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException // For batch mode
    {
	int findCount = NOT_FOUND;

        if(inDir.isFile() == true)
        {
	    findCount = matchDocumentXML(inDir);

	    if(findCount > 0)
            {
                    fileList.addElement(inDir);
            }

	    return findCount;
        }
        else
        {
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    findCount += findAllXMLBatch(files[i]);
                }
            }
        }
	return findCount;
    }

    protected int findAllNGramBatch(File inDir) throws FileNotFoundException, IOException // For batch mode
    {
        int findCount = NOT_FOUND;

        if(inDir.isFile() == true)
        {
            findCount = matchDocumentNGram(inDir);

	    if(findCount > 0)
            {
                fileList.addElement(inDir);
            }

	    return findCount;
        }
        else
        {
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    findCount += findAllNGramBatch(files[i]);
                }
            }
        }
	return findCount;
    }

    protected int findAllDictBatch(File inDir) throws FileNotFoundException, IOException // For batch mode
    {
        int findCount = NOT_FOUND;

        if(inDir.isFile() == true)
        {
            findCount = matchDocumentDict(inDir);

	    if(findCount > 0)
        {
            fileList.addElement(inDir);
        }

	    return findCount;
        }
        else
        {
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    findCount += findAllDictBatch(files[i]);
                }
            }
        }
	return findCount;
    }

    protected int saveOutputRaw(File inDir) throws FileNotFoundException, IOException // For batch mode
    {
	int replaceCount = NOT_FOUND;

	if(inDir.isFile() == true)
        {
	    // Assuming that matching has already been done
	    File outFile = getOutputFile(inDir);
	    
	    int fileCount = fileList.getSize();
	    
	    int fileIndex = UtilityFunctions.findIndexOfEqualObject(fileList, inDir);
//        System.out.println("Saving--> "+inDir + "\t"+ outFile + fileIndex + findReplaceOptions.replaceMode + findReplaceOptions.extractionMode);

	    if(fileIndex >= 0 && fileIndex < fileCount)
	    {
            if(findReplaceOptions.extractionMode == true && findReplaceOptions.replaceMode == true)
            {
                replaceCount = extractReplaceFromRaw(inDir);
            }

            else if(findReplaceOptions.extractionMode)
		{
		    replaceCount = extractFromRaw(inDir, true);
		    //inDir = outFile;
		}

            else if(findReplaceOptions.replaceMode) {
                    replaceCount = replaceAllQuietRaw(inDir);
                }
		
            else if(findReplaceOptions.extractionMode == false && findReplaceOptions.replaceMode == false) {
                    UtilityFunctions.copyFile(inDir, outFile);
                }
	    }
        }

       else
        {
            File outDir = getOutputFile(inDir);
            setupOutputDirectory(outDir, findReplaceOptions.clean);
            
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    replaceCount += saveOutputRaw(files[i]);
                }
            }
        }
        
	return replaceCount;
    }

    protected int saveOutputDict(File inDir) throws FileNotFoundException, IOException // For batch mode
    {
	int replaceCount = NOT_FOUND;

	if(inDir.isFile() == true)
        {
	    // Assuming that matching has already been done
	    File outFile = getOutputFile(inDir);

	    int fileCount = fileList.getSize();

	    int fileIndex = UtilityFunctions.findIndexOfEqualObject(fileList, inDir);

        //System.out.println("Saving--> "+inDir + "\t"+ outFile + fileIndex + findReplaceOptions.replaceMode + findReplaceOptions.extractionMode);

	    if(fileIndex >= 0 && fileIndex < fileCount)
	    {
            if(findReplaceOptions.extractionMode == true && findReplaceOptions.replaceMode == true)
            {
            }
            else if(findReplaceOptions.extractionMode)
            {
                replaceCount = extractFromRaw(inDir, true);
                //inDir = outFile;
            }
            else if(findReplaceOptions.replaceMode)
            {
                //replaceCount = replaceAllQuietRaw(inDir);
            }
            else if(findReplaceOptions.extractionMode == false && findReplaceOptions.replaceMode == false)
            {
                UtilityFunctions.copyFile(inDir, outFile);
            }
	    }
        }
        else
        {
            File outDir = getOutputFile(inDir);
            setupOutputDirectory(outDir, findReplaceOptions.clean);

            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    replaceCount += saveOutputDict(files[i]);
                }
            }
        }

	return replaceCount;
    }

    public int matchDocumentRaw(File inFile) throws FileNotFoundException, IOException
    {
        if(inFile.isFile() == true)
        {
            BufferedReader lnReader;
            
            if(findReplaceOptions.charset.equals("") == false) {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), findReplaceOptions.charset));
            }
            else {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            }
            
            String line;
            
            while((line = lnReader.readLine()) != null)
            {
                if(line.equals("") == false && findReplaceOptions.findText.equals("") == false)
		{
		    pattern = compilePattern(findReplaceOptions.findText);
		    matcher = pattern.matcher(line);
		    
		    if(matcher.find()) {
                        return 1;
                    }
		}
	    }
	}
	
	return NOT_FOUND;
    }

    public int matchDocumentXML(File inFile) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException
    {
        if(inFile.isFile() == true)
        {
            Document doc;
            XPath xpath;
            DocumentBuilderFactory builderFactory= DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder=null;
            builder=builderFactory.newDocumentBuilder();
            doc=builder.parse(inFile.toString());
            XPathFactory xfact=XPathFactory.newInstance();
            xpath=xfact.newXPath();

            String query =GlobalProperties.getIntlString("//TITLE[contains(.,'Intro')]");

            NodeList result=(NodeList) xpath.evaluate(query,doc,XPathConstants.NODESET);

            if(result.getLength()==0) {
                return NOT_FOUND;
            }
            else {
                return 1;
            }
        }
        return NOT_FOUND;
	}


    public int matchDocumentNGram(File inFile) throws FileNotFoundException, IOException
    {
        if(inFile.isFile() == true)
        {
                NGramLM nglm = new NGramLMImpl(inFile, GlobalProperties.getIntlString("word"), 3);
                nglm.readNGramLM(inFile);

                String ngram = findReplaceOptions.findText;
                int order = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.order;
                int minFreq = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.minFreq;
                int maxFreq = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.maxFreq;
                //int minProb = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.minProb;
                //int maxProb = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.maxProb;
                /*
                String ngram="saB.*";
                int order=-1;
                int minFreq=2,maxFreq=-1;
                 * */
                if((nglm.fCheckNGramFile(ngram, order, minFreq, maxFreq))==true) {
                return 1;
            }
                else {
                return NOT_FOUND;
            }
            
        }
	return NOT_FOUND;
    }

    public int matchDocumentDict(File inFile) throws FileNotFoundException, IOException
    {
        if(inFile.isFile() == true)
        {
                ShabdanjaliDict dictionary = new ShabdanjaliDict();
                dictionary.prepareDict(inFile.toString());
                String query = findReplaceOptions.findText;
                if(dictionary.findEntry(query,findReplaceOptions.regex))
                {
                    return 1;
                }
                else {
                    return NOT_FOUND;
                }
        }
	return NOT_FOUND;
    }
/*
    public int matchDocumentDict(File inFile) throws FileNotFoundException, IOException
    {
        if(inFile.isFile() == true)
        {
            BufferedReader lnReader = null;

            if(findReplaceOptions.charset.equals("") == false)
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), findReplaceOptions.charset));
            else
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

            String line;

            while((line = lnReader.readLine()) != null)
            {
                if(line.equals("") == false && findReplaceOptions.findText.equals("") == false)
		{
		    pattern = compilePattern(findReplaceOptions.findText);
		    matcher = pattern.matcher(line);

		    if(matcher.find())
			return 1;
		}
	    }
	}

	return NOT_FOUND;
    }
*/


    private int extractFromXML(File inFile, boolean save) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException, XPathExpressionException
    {
        int findCount = NOT_FOUND;

        if(findReplaceOptions.batchMode == false) {
            return findCount;
        }

        if(inFile.isFile() == true)
        {
            File outFile = getOutputFile(inFile);
            PrintStream ps = null;
            TextEditorJPanel editTextJPanel = null;
            String query = GlobalProperties.getIntlString("//TUTORIAL//TITLE/text()");
            GenericQuery qPanel = new GenericQuery();
            qPanel.setDoc(inFile.toString());
            NodeList result = qPanel.getQueryResult(query);
            editTextJPanel = new TextEditorJPanel(findReplaceOptions.language, findReplaceOptions.charset, null, null, TextEditorJPanel.MINIMAL_MODE);
            editTextJPanel.setVisible(true);
            if(contentJPanel.getComponentCount() > 0) {
                contentJPanel.removeAll();
            }

            contentJPanel.add(editTextJPanel, java.awt.BorderLayout.CENTER);
            contentJPanel.setVisible(false);
            contentJPanel.setVisible(true);

            if(save)
            {
                ps = new PrintStream(outFile, findReplaceOptions.charset);
                for(int i=0;i<result.getLength();i++){
                    ps.println(result.item(i).getNodeValue()+"\n");
                }
            }
            else
            {
                if(result.getLength()==0)
                {
                    editTextJPanel.textJTextArea.append(GlobalProperties.getIntlString("\n_Sorry,_no_items_were_found."));
                    editTextJPanel.textJTextArea.append(GlobalProperties.getIntlString("\n\n_Note:_You_can_try_submitting_the_query_using_XPath."));
                }
                for(int i=0;i<result.getLength();i++){
                    editTextJPanel.textJTextArea.append(result.item(i).getNodeValue()+"\n");
                }
            }

			return result.getLength();
        }
        return findCount;
    }

    private int extractFromNGram(File inFile, boolean save) throws FileNotFoundException, IOException
    {
        int findCount = NOT_FOUND;

        if(findReplaceOptions.batchMode == false) {
            return findCount;
        }

        if(inFile.isFile() == true)
        {
            File outFile = getOutputFile(inFile);

	    if(save)
            {

            }
	    else
	    {
                NGramLM nglm = new NGramLMImpl(inFile, GlobalProperties.getIntlString("word"), 3);
                nglm.readNGramLM(inFile);
                
                String ngram = findReplaceOptions.findText;
                int order = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.order;
                int minFreq = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.minFreq;
                int maxFreq = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.maxFreq;
                //int minProb = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.minProb;
                //int maxProb = findReplaceOptions.resourceQueryOptions.nGramLMQueryOptions.maxProb;
                /*
                String ngram="saB.*";
                int order=-1;
                int minFreq=2,maxFreq=-1;
                 * */
                LinkedHashMap<Integer, LinkedHashMap<List<Integer>, NGram>> matchNgrams;
                matchNgrams = nglm.findNGramFile(ngram, order, minFreq, maxFreq);
                NGramLMJPanel nGramPanel = new NGramLMJPanel();
                nGramPanel.ngramQFillTable(matchNgrams);
                nGramPanel.setVisible(true);

                if(contentJPanel.getComponentCount() > 0) {
                    contentJPanel.removeAll();
                }

		contentJPanel.add(nGramPanel.qOutputTJPanel, java.awt.BorderLayout.CENTER);
		contentJPanel.setVisible(false);
		contentJPanel.setVisible(true);
	    }
	}
	return findCount;
    }

    private int extractFromDict(File inFile, boolean save) throws FileNotFoundException, IOException
    {
        int findCount = NOT_FOUND;

        if(findReplaceOptions.batchMode == false) {
            return findCount;
        }
        
        File outFile = getOutputFile(inFile);
        ShabdanjaliDict dictionary = new ShabdanjaliDict(true);
        String word = findReplaceOptions.findText;
        if(save)
        {
            dictionary.writeEntryToFile(word,outFile);
        }
        else
        {
            dictionary.prepareDict(inFile.toString());
            if(findReplaceOptions.regex)
            {
                dictionary.retriveButtonClicked(word);
            }
            else
            {
                dictionary.populateTree(word);
            }

            if(contentJPanel.getComponentCount() > 0) {
                contentJPanel.removeAll();
            }

            contentJPanel.add(dictionary, java.awt.BorderLayout.CENTER);
            contentJPanel.setVisible(false);
            contentJPanel.setVisible(true);
        }
	
        return findCount;
    }

    public int extractFromRaw(File inFile, boolean save) throws FileNotFoundException, IOException
    {
	int findCount = NOT_FOUND;

	if(findReplaceOptions.batchMode == false) {
            return findCount;
        }
	
        if(inFile.isFile() == true)
        {
            File outFile = getOutputFile(inFile);
            PrintStream ps = null;

            BufferedReader lnReader;
            
            if(findReplaceOptions.charset.equals("") == false) {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), findReplaceOptions.charset));
            }
            else {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            }
            
            String line;
	    TextEditorJPanel editTextJPanel = null;
	    
	    if(save) {
                ps = new PrintStream(outFile, findReplaceOptions.charset);
            }
	    else
	    {
            //editTextJPanel = new TextEditorJPanel(inFile.getAbsolutePath(), findReplaceOptions.language, findReplaceOptions.charset, null, null, TextEditorJPanel.MINIMAL_MODE);
            editTextJPanel = new TextEditorJPanel(findReplaceOptions.language, findReplaceOptions.charset, null, null, TextEditorJPanel.MINIMAL_MODE);
//        editTextJPanel = new TextEditorJPanel(findReplaceOptions.language, findReplaceOptions.charset);
//		editTextJPanel.setMode(TextEditorJPanel.MINIMAL_MODE);
		editTextJPanel.setVisible(true);
        //System.out.println("Extract -->"+inFile);
		if(contentJPanel.getComponentCount() > 0)
		    contentJPanel.removeAll();

		contentJPanel.add(editTextJPanel, java.awt.BorderLayout.CENTER);
		contentJPanel.setVisible(false);
		contentJPanel.setVisible(true);
	    }
            
            while((line = lnReader.readLine()) != null)
            {
                if(line.equals("") == false && findReplaceOptions.findText.equals("") == false)
		{
		    pattern = compilePattern(findReplaceOptions.findText);
		    matcher = pattern.matcher(line);
		    
		    if(matcher.find())
		    {
			if(save)
			{
			    ps.println(line);
			}
			else
			{
			    editTextJPanel.textJTextArea.append(line + "\n");
			}
			
			findCount++;
		    }
		}
	    }
	    
	    if(save == false) {
                highlightAllRaw(editTextJPanel.textJTextArea, highlightColor);
            }
	}
	
	return findCount;
    }

    public int extractReplaceFromRaw(File inFile) throws FileNotFoundException, IOException
    {
	int findCount = NOT_FOUND;

	if(findReplaceOptions.batchMode == false) {
            return findCount;
        }

        if(inFile.isFile() == true)
        {
            File outFile = getOutputFile(inFile);
            PrintStream ps;

            BufferedReader lnReader;

            if(findReplaceOptions.charset.equals("") == false) {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), findReplaceOptions.charset));
            }
            else {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            }

            String line,newLine;
	    TextEditorJPanel editTextJPanel = null;

            ps = new PrintStream(outFile, findReplaceOptions.charset);
        
            while((line = lnReader.readLine()) != null)
            {
                if(line.equals("") == false && findReplaceOptions.findText.equals("") == false)
		{
		    pattern = compilePattern(findReplaceOptions.findText);
		    matcher = pattern.matcher(line);

		    if(matcher.find())
		    {
                        newLine = matcher.replaceAll(findReplaceOptions.replaceWith);
                        ps.println(newLine);
                        findCount++;
		    }
		}
	    }
	}

	return findCount;
    }

    protected int findAllXML(File inFile) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
    {

        int findCount = NOT_FOUND;

        if(findReplaceOptions.batchMode)
        {
            JaxeFrame jaxeFrame = new JaxeFrame();
            jaxeFrame.ouvrir(inFile);
            jaxeFrame.setVisible(false);
            /*
            JaxeTextPane textPane;
            JScrollPane paneScrollPane;
            JTabbedPane sidepane = null;
            JaxeDocument doc;
            JFrame frame=null;

            doc = new JaxeDocument();
            URL u ;
            String sec=null;
            u = inFile.toURI().toURL();
            doc.lire(u,sec);

            textPane = new JaxeTextPane(doc, frame);
            doc.textPane = textPane;
            //textPane.setStyledDocument(doc);
            paneScrollPane = new JScrollPane(textPane);
            paneScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            paneScrollPane.setPreferredSize(new Dimension(500, 400));
            paneScrollPane.setMinimumSize(new Dimension(100, 50));

                sidepane = new JTabbedPane();
                ArbreXML arbrexml;
                arbrexml = new ArbreXML(doc) ;
                ResourceBundle rb = JaxeResourceBundle.getRB();
                textPane.ajouterEcouteurArbre(arbrexml);
                sidepane.addTab(rb.getString("tabs.arbre"), arbrexml);


            JSplitPane split;
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            split.setLeftComponent(sidepane);
            split.setRightComponent(paneScrollPane);

             * */
            //contentJPanel.add(split, java.awt.BorderLayout.CENTER);
            contentJPanel.add(jaxeFrame.getContentPane(), java.awt.BorderLayout.CENTER);
            contentJPanel.setVisible(false);
            contentJPanel.setVisible(true);
        }
        else
        {
            //String query = "//TUTORIAL//TITLE/text()";
            //GenericQuery qPanel = new GenericQuery();
            //qPanel.setDoc(inFile.toString());
            //NodeList result = qPanel.getQueryResult(query);
            
            
        }

        return findCount;
    }

    protected int findAllRaw(File inFile)
    {
        int findCount = NOT_FOUND;

        TextEditorJPanel editTextJPanel = null;
	
	if(findReplaceOptions.batchMode)
	{
        editTextJPanel = new TextEditorJPanel(inFile.getAbsolutePath(), findReplaceOptions.language, findReplaceOptions.charset, null, null, TextEditorJPanel.MINIMAL_MODE);
	    //editTextJPanel = new TextEditorJPanel(inFile.getAbsolutePath(), findReplaceOptions.language, findReplaceOptions.charset);
	    //editTextJPanel.setMode(TextEditorJPanel.MINIMAL_MODE);
	    editTextJPanel.setVisible(true);
        System.out.println(GlobalProperties.getIntlString("Find_-->")+inFile);
	    
	    if(contentJPanel.getComponentCount() > 0) {
                contentJPanel.removeAll();
            }
	    
	    contentJPanel.add(editTextJPanel, java.awt.BorderLayout.CENTER);
	    contentJPanel.setVisible(false);
	    contentJPanel.setVisible(true);
	}
	else
	{
	    editTextJPanel = (TextEditorJPanel) contentJPanel;
	}
	    
	String text = editTextJPanel.textJTextArea.getText();

	pattern = compilePattern(findReplaceOptions.findText);
	matcher = pattern.matcher(text);

	if(findReplaceOptions.highlightResults) {
            removeAllHighlightsRaw(editTextJPanel.textJTextArea);
        }
	
	while(matcher.find())
	{
	    int currentTextStartPos = matcher.start();
	    int currentTextEndPos = matcher.end();

	    if(findReplaceOptions.highlightResults)
	    {
		highlightRaw(currentTextStartPos, currentTextEndPos, editTextJPanel.textJTextArea, highlightColor);
	    }

	    findCount++;
	}
	
	matcher = pattern.matcher(text);
	
	return findCount;
    }

    protected int findRaw()
    {
	TextEditorJPanel editTextJPanel = null;
	
	if(findReplaceOptions.batchMode)
	{
	    if(contentJPanel.getComponentCount() > 0) {
                editTextJPanel = (TextEditorJPanel) contentJPanel.getComponent(0);
            }
	    else {
                return NOT_FOUND;
            }
	}
	else
	{
	    editTextJPanel = (TextEditorJPanel) contentJPanel;
	}
	
	if(editTextJPanel == null) {
            return NOT_FOUND;
        }
	
	String text = editTextJPanel.textJTextArea.getText();
	
	int caretPos = editTextJPanel.textJTextArea.getCaretPosition();
	
	if(findReplaceOptions.searchBackwards)
	{
	    caretPos = getPreviousPos(text, caretPos);
	    
	    if(text.indexOf(findReplaceOptions.findText, caretPos) == -1) {
                caretPos = getPreviousPos(text, caretPos);
            }
	}
	
	if(pattern == null) {
            pattern = compilePattern(findReplaceOptions.findText);
        }
	
	if(matcher == null) {
            matcher = pattern.matcher(text);
        }
	
	boolean matched;

	if(matcher.hitEnd()) {
            matched = matcher.find(0);
        }
	else {
            matched = matcher.find(caretPos);
        }

	if(matched)
	{
	    int currentTextStartPos = matcher.start();
	    int currentTextEndPos = matcher.end();
	    
	    if(findReplaceOptions.highlightResults)
	    {
		if(oldStart >= 0 && oldEnd > oldStart && (oldEnd - oldStart) <= text.length() ) {
                    highlightRaw(oldStart, oldEnd, editTextJPanel.textJTextArea, highlightColor);
                }

		removeHighlightRaw(currentTextStartPos, currentTextEndPos, editTextJPanel.textJTextArea);
	    }

	    editTextJPanel.textJTextArea.requestFocus();

	    editTextJPanel.textJTextArea.setCaretPosition(currentTextStartPos);
	    oldStart = currentTextStartPos;
	    
	    editTextJPanel.textJTextArea.moveCaretPosition(currentTextEndPos);
	    oldEnd = currentTextEndPos;
	    
	    return 1;
	}
	else
	{
	    oldStart = 0;
	    oldEnd = 0;
	    matcher.reset();
	}
	
	return NOT_FOUND;
    }
    
    protected int replaceRaw()
    {
	TextEditorJPanel editTextJPanel = null;
	
	if(findReplaceOptions.batchMode)
	{
	    if(contentJPanel.getComponentCount() > 0) {
                editTextJPanel = (TextEditorJPanel) contentJPanel.getComponent(0);
            }
	    else {
                return NOT_FOUND;
            }
	}
	else
	{
	    editTextJPanel = (TextEditorJPanel) contentJPanel;
	}
	
	if(editTextJPanel == null) {
            return NOT_FOUND;
        }

	int oldStart = editTextJPanel.textJTextArea.getSelectionStart();
	int oldEnd = editTextJPanel.textJTextArea.getSelectionEnd();
	
	int caretPos = editTextJPanel.textJTextArea.getCaretPosition();
	
	String text = editTextJPanel.textJTextArea.getText();
	
	if(findReplaceOptions.searchBackwards)
	{
	    caretPos = getPreviousPos(text, caretPos);
	    
	    if(text.indexOf(findReplaceOptions.findText, caretPos) == -1) {
                caretPos = getPreviousPos(text, caretPos);
            }
	}
	
	if(pattern == null) {
            pattern = compilePattern(findReplaceOptions.findText);
        }

        matcher = pattern.matcher(text);

	boolean matched = matcher.find(caretPos);
	
	if(matched == false) {
            matched = matcher.find(0);
        }

	if(matched)
	{
	    StringBuffer sb = new StringBuffer();
	    matcher.appendReplacement(sb, findReplaceOptions.replaceWith);
	    matcher.appendTail(sb);

	    editTextJPanel.textJTextArea.setText(sb.toString());

	    int currentTextStartPos = matcher.start();
	    int currentTextEndPos = matcher.end();
	    
	    if(findReplaceOptions.highlightResults)
	    {
		removeAllHighlightsRaw(editTextJPanel.textJTextArea);
		highlightAllRaw(editTextJPanel.textJTextArea, highlightColor);

		if(oldStart >= 0 && oldEnd > oldStart && (oldEnd - oldStart) <= text.length() ) {
                    highlightRaw(oldStart, oldEnd, editTextJPanel.textJTextArea, highlightColor);
                }

		removeHighlightRaw(currentTextStartPos, currentTextEndPos, editTextJPanel.textJTextArea);
	    }

	    editTextJPanel.textJTextArea.requestFocus();
	    
	    editTextJPanel.textJTextArea.setCaretPosition(currentTextStartPos);
	    oldStart = currentTextStartPos;
	    
	    editTextJPanel.textJTextArea.moveCaretPosition(currentTextEndPos);
	    oldEnd = currentTextEndPos;
	    
	    return 1;
	}
	else
	{
	    oldStart = 0;
	    oldEnd = 0;
	}
	
	return NOT_FOUND;
    }
    
    protected int replaceAllRaw(File inFile)
    {
	if(findReplaceOptions.replaceMode == false) {
            return NOT_FOUND;
        }
	    
	if(findReplaceOptions.replaceWith == null) {
            findReplaceOptions.replaceWith = "";
        }
	
	int findCount = NOT_FOUND;

	TextEditorJPanel editTextJPanel = null;
	
	if(findReplaceOptions.batchMode)
	{
	    if(contentJPanel.getComponentCount() > 0) {
                editTextJPanel = (TextEditorJPanel) contentJPanel.getComponent(0);
            }
	    else {
                return NOT_FOUND;
            }
	}
	else
	{
	    editTextJPanel = (TextEditorJPanel) contentJPanel;
	}
	
	if(editTextJPanel == null) {
            return NOT_FOUND;
        }
	    
	String text = editTextJPanel.textJTextArea.getText();

	pattern = compilePattern(findReplaceOptions.findText);
	matcher = pattern.matcher(text);
	
	text = matcher.replaceAll(findReplaceOptions.replaceWith);
	editTextJPanel.textJTextArea.setText(text);

	pattern = compilePattern(findReplaceOptions.replaceWith);
	matcher = pattern.matcher(text);
	
	while(matcher.find())
	{
	    int currentTextStartPos = matcher.start();
	    int currentTextEndPos = matcher.end();

	    if(findReplaceOptions.highlightResults)
	    {
		highlightRaw(currentTextStartPos, currentTextEndPos, editTextJPanel.textJTextArea, highlightColor);
	    }

	    findCount++;
	}

	pattern = compilePattern(findReplaceOptions.findText);
	matcher = pattern.matcher(text);
	
	return findCount;
    }
    
    protected int replaceAllQuietRaw(File inFile) throws FileNotFoundException, IOException
    {
	int findCount = NOT_FOUND;

	if(findReplaceOptions.batchMode == false) {
            return findCount;
        }
	
        if(inFile.isFile() == true)
        {
            File outFile = getOutputFile(inFile);
            PrintStream ps = new PrintStream(outFile, findReplaceOptions.charset);

            BufferedReader lnReader;
            
            if(findReplaceOptions.charset.equals("") == false) {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), findReplaceOptions.charset));
            }
            else {
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            }
            
            String line, newline;
            
            while((line = lnReader.readLine()) != null)
            {
                if(line.equals("") == false && findReplaceOptions.findText.equals("") == false)
		{
		    pattern = compilePattern(findReplaceOptions.findText);
		    matcher = pattern.matcher(line);
		    
		    if(matcher.find() || findReplaceOptions.extractionMode == false)
		    {
			newline = matcher.replaceAll(findReplaceOptions.replaceWith);
			ps.println(newline);
			findCount++;
		    }
		}
	    }
	}
	
	return findCount;
    }
    
    // For Chunked and SSFTagged corpus
    protected int findAllSSFBatch(File inDir) throws FileNotFoundException, IOException, Exception // For batch mode
    {
	int findCount = NOT_FOUND;
        
	if(inDir.isFile() == true)
        {
	    findCount = matchDocumentSSF(inDir);
	    
	    if(findCount > 0) {
                fileList.addElement(inDir);
            }

	    return findCount;
        }
        else
        {
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    findCount += findAllSSFBatch(files[i]);
                }
            }
        }
        
	return findCount;
    }

    protected int saveOutputSSF(File inDir) throws FileNotFoundException, IOException // For batch mode
    {
	int replaceCount = NOT_FOUND;

        if(inDir.isFile() == true)
        {
            return replaceAllQuietSSF(inDir);
        }
        else
        {
            File outDir = getOutputFile(inDir);
            setupOutputDirectory(outDir, findReplaceOptions.clean);
            
            if(inDir != null && inDir.isDirectory() == true && outDir != null && outDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    replaceCount += saveOutputSSF(files[i]);
                }
            }
        }
        
	return replaceCount;
    }

    public int matchDocumentSSF(File inFile) throws FileNotFoundException, IOException, Exception
    {
        int findCount = NOT_FOUND;
        
        SSFStory ssfStory = new SSFStoryImpl();
        
        ssfStory.readFile(inFile.getAbsolutePath(), findReplaceOptions.charset);
                
        findCount = ssfStory.matchedSentenceCount(findReplaceOptions);
        
        return findCount;
    }

    public int extractFromSSF(File inFile, boolean save) throws FileNotFoundException, IOException
    {
	return NOT_FOUND;	
    }

    protected int findAllSSF(File inFile)
    {
	int findCount = NOT_FOUND;
        
//        if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.treeView == false)
//        {
//            TextEditorJPanel editTextJPanel = null;
//
//            if(findReplaceOptions.batchMode)
//            {
//                editTextJPanel = new TextEditorJPanel(inFile.getAbsolutePath(), findReplaceOptions.language, findReplaceOptions.charset, null, null, TextEditorJPanel.MINIMAL_MODE);
//    //	    editTextJPanel = new TextEditorJPanel(inFile.getAbsolutePath(), findReplaceOptions.language, findReplaceOptions.charset);
//    //	    editTextJPanel.setMode(TextEditorJPanel.MINIMAL_MODE);
//                editTextJPanel.setVisible(true);
//
//                if(contentJPanel.getComponentCount() > 0)
//                    contentJPanel.removeAll();
//
//                contentJPanel.add(editTextJPanel, java.awt.BorderLayout.CENTER);
//                contentJPanel.setVisible(false);
//                contentJPanel.setVisible(true);
//            }
//            else
//            {
//                editTextJPanel = (TextEditorJPanel) contentJPanel;
//            }
//
//            String text = editTextJPanel.textJTextArea.getText();
//
//            pattern = compilePattern(findReplaceOptions.findText);
//            matcher = pattern.matcher(text);
//
//            if(findReplaceOptions.highlightResults)
//                removeAllHighlightsRaw(editTextJPanel.textJTextArea);
//
//            while(matcher.find())
//            {
//                int currentTextStartPos = matcher.start();
//                int currentTextEndPos = matcher.end();
//
//                if(findReplaceOptions.highlightResults)
//                {
//                    highlightRaw(currentTextStartPos, currentTextEndPos, editTextJPanel.textJTextArea, highlightColor);
//                }
//
//                findCount++;
//            }
//
//            matcher = pattern.matcher(text);
//        }
//        else
        {
            SyntacticAnnotationWorkJPanel syntacticAnnotationWorkJPanel = null;

            if(findReplaceOptions.batchMode)
            {
                syntacticAnnotationWorkJPanel = new SyntacticAnnotationWorkJPanel();
                syntacticAnnotationWorkJPanel.openFile(inFile.getAbsolutePath(), findReplaceOptions.language, findReplaceOptions.charset);
                syntacticAnnotationWorkJPanel.setVisible(true);

                if(contentJPanel.getComponentCount() > 0) {
                    contentJPanel.removeAll();
                }

                contentJPanel.add(syntacticAnnotationWorkJPanel, java.awt.BorderLayout.CENTER);
                contentJPanel.setVisible(false);
                contentJPanel.setVisible(true);
                
                currentSentence = 0;
            }
            else
            {
                syntacticAnnotationWorkJPanel = (SyntacticAnnotationWorkJPanel) contentJPanel;
            } 
            
            findCount = 1;
        }
	
	return findCount;
    }

    protected int findSSF()
    {
	TextEditorJPanel editTextJPanel = null;
	
//        if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.treeView == false)
//        {
//            if(findReplaceOptions.batchMode)
//            {
//                if(contentJPanel.getComponentCount() > 0)
//                    editTextJPanel = (TextEditorJPanel) contentJPanel.getComponent(0);
//                else
//                    return NOT_FOUND;
//            }
//            else
//            {
//                editTextJPanel = (TextEditorJPanel) contentJPanel;
//            }
//
//            if(editTextJPanel == null)
//                return NOT_FOUND;
//
//            String text = editTextJPanel.textJTextArea.getText();
//
//            int caretPos = editTextJPanel.textJTextArea.getCaretPosition();
//
//            if(findReplaceOptions.searchBackwards)
//            {
//                caretPos = getPreviousPos(text, caretPos);
//
//                if(text.indexOf(findReplaceOptions.findText, caretPos) == -1)
//                    caretPos = getPreviousPos(text, caretPos);
//            }
//
//            if(pattern == null)
//                pattern = compilePattern(findReplaceOptions.findText);
//
//            if(matcher == null)
//                matcher = pattern.matcher(text);
//
//            boolean matched = false;
//
//            if(matcher.hitEnd())
//                matched = matcher.find(0);
//            else
//                matched = matcher.find(caretPos);
//
//            if(matched)
//            {
//                int currentTextStartPos = matcher.start();
//                int currentTextEndPos = matcher.end();
//
//    //	    if(findReplaceOptions.highlightResults)
//                {
//                    if(oldStart >= 0 && oldEnd > oldStart && (oldEnd - oldStart) <= text.length() )
//                        highlightRaw(oldStart, oldEnd, editTextJPanel.textJTextArea, highlightColor);
//
//                    removeHighlightRaw(currentTextStartPos, currentTextEndPos, editTextJPanel.textJTextArea);
//                }
//
//                editTextJPanel.textJTextArea.requestFocus();
//
//                editTextJPanel.textJTextArea.setCaretPosition(currentTextStartPos);
//                oldStart = currentTextStartPos;
//
//                editTextJPanel.textJTextArea.moveCaretPosition(currentTextEndPos);
//                oldEnd = currentTextEndPos;
//
//                return 1;
//            }
//            else
//            {
//                oldStart = 0;
//                oldEnd = 0;
//                matcher.reset();
//            }
//        }
//        else
        {
            SyntacticAnnotationWorkJPanel syntacticAnnotationWorkJPanel = null;
            
            if(findReplaceOptions.batchMode)
            {
                if(contentJPanel.getComponentCount() > 0) {
                    syntacticAnnotationWorkJPanel = (SyntacticAnnotationWorkJPanel) contentJPanel.getComponent(0);
                }
                else {
                    return NOT_FOUND;
                }
            }
            else
            {
                syntacticAnnotationWorkJPanel = (SyntacticAnnotationWorkJPanel) contentJPanel;
            }

            if(syntacticAnnotationWorkJPanel == null) {
                return NOT_FOUND;
            }
           
            SSFStory ssfStory = syntacticAnnotationWorkJPanel.getSSFStory();
            
            int scount = ssfStory.countSentences();
            
            for(int i = currentSentence; i < scount; i++)
            {
                SSFSentence ssfSentence = ssfStory.getSentence(i);

                ssfSentence.clearHighlights();

                if(ssfSentence.matches(findReplaceOptions))
                {
                    syntacticAnnotationWorkJPanel.setCurrentPosition(i);
                    currentSentence = ++i;
                    return 1;
               }
            }
            
            currentSentence = 0;
            syntacticAnnotationWorkJPanel.setCurrentPosition(0);
        }
	
	return NOT_FOUND;
    }
    
    protected int replaceSSF()
    {
	return NOT_FOUND;
    }
    
    protected int replaceAllSSF(File inFile)
    {
	return NOT_FOUND;
    }
    
    protected int replaceAllQuietSSF(File inFile)
    {
	return NOT_FOUND;
    }
    
    protected void highlightAllRaw(JComponent contentComp, Color color)
    {
	String text = null;
	if(contentComp instanceof JTextArea)
	{
	    text = ((JTextArea) contentComp).getText();
	}

	pattern = compilePattern(findReplaceOptions.findText);
	matcher = pattern.matcher(text);
	
	while(matcher.find())
	{
	    int currentTextStartPos = matcher.start();
	    int currentTextEndPos = matcher.end();

	    if(findReplaceOptions.highlightResults)
	    {
		highlightRaw(currentTextStartPos, currentTextEndPos, contentComp, highlightColor);
	    }
	}
	
	matcher.reset();
    }
    
    protected void highlightRaw(int start, int end, JComponent contentComp, Color color)
    {
	try
	{
	    Highlighter hilite = null;
	    Document doc = null;

	    if(contentComp instanceof JTextArea)
	    {
		hilite = ((JTextArea) contentComp).getHighlighter();
	    }
	    else if(contentComp instanceof JTextPane)
	    {
		hilite = ((JTextPane) contentComp).getHighlighter();
	    }

	    FindHighlightPainter hp = new FindHighlightPainter(color);
	    Object tag = hilite.addHighlight(start, end, hp);
	}
	catch (BadLocationException e) {
	    e.printStackTrace();
	}
    }
    
    protected void removeAllHighlightsRaw(JComponent contentComp)
    {
	Highlighter hilite = null;
	Document doc = null;

	if(contentComp instanceof JTextArea)
	{
	    hilite = ((JTextArea) contentComp).getHighlighter();
	}
	else if(contentComp instanceof JTextPane)
	{
	    hilite = ((JTextPane) contentComp).getHighlighter();
	}
	
	hilite.removeAllHighlights();
    }
    
    protected void removeHighlightRaw(int start, int end, JComponent contentComp)
    {
	Highlighter hilite = null;
	Document doc = null;

	if(contentComp instanceof JTextArea)
	{
	    hilite = ((JTextArea) contentComp).getHighlighter();
	}
	else if(contentComp instanceof JTextPane)
	{
	    hilite = ((JTextPane) contentComp).getHighlighter();
	}
	
	Highlighter.Highlight hls[] = hilite.getHighlights();
	
	for (int i = 0; i < hls.length; i++)
	{
	    if(hls[i].getStartOffset() == start && hls[i].getEndOffset() == end)
	    {
		hilite.removeHighlight(hls[i]);
	    }
	}
    }
    
    protected void highlightSSF(int start, int end, JComponent contentComp)
    {
    }
    
    private class FindHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public FindHighlightPainter(Color color) {
            super(color);
        }
    }
    
    protected Pattern compilePattern(String regex)
    {
	Pattern p = null;
	
	if(findReplaceOptions.regex == false) {
            p = Pattern.compile(regex, Pattern.LITERAL);
        }
	else
	{
	    int flags = Pattern.CANON_EQ;
	    
	    if(findReplaceOptions.canonicalEquivalence) {
                flags &= Pattern.CANON_EQ;
            }

	    if(findReplaceOptions.caseInsensitive) {
                flags |= Pattern.CASE_INSENSITIVE;
            }

	    if(findReplaceOptions.multiline) {
                flags |= Pattern.MULTILINE;
            }

	    if(findReplaceOptions.unicodeCase) {
                flags |= Pattern.UNICODE_CASE;
            }

	    if(findReplaceOptions.unixLines) {
                flags |= Pattern.UNIX_LINES;
            }
	    
	    p = Pattern.compile(regex, flags);
	}
	
	return p;
    }
    
    public static Pattern compilePattern(String regex, FindReplaceOptions findReplaceOptions)
    {
	Pattern p = null;
	
	if(findReplaceOptions.regex == false) {
            p = Pattern.compile(regex, Pattern.LITERAL);
        }
	else
	{
	    int flags = Pattern.CANON_EQ;
	    
	    if(findReplaceOptions.canonicalEquivalence) {
                flags &= Pattern.CANON_EQ;
            }

	    if(findReplaceOptions.caseInsensitive) {
                flags |= Pattern.CASE_INSENSITIVE;
            }

	    if(findReplaceOptions.multiline) {
                flags |= Pattern.MULTILINE;
            }

	    if(findReplaceOptions.unicodeCase) {
                flags |= Pattern.UNICODE_CASE;
            }

	    if(findReplaceOptions.unixLines) {
                flags |= Pattern.UNIX_LINES;
            }
	    
	    p = Pattern.compile(regex, flags);
	}
	
	return p;
    }
    
    private int getPreviousPos(String txt, int presentPos)
    {
	int prevPos = presentPos - findReplaceOptions.findText.length() - 1;

	if(prevPos < 0) {
            prevPos = txt.length() - 1;
        }
	else {
            prevPos = txt.lastIndexOf(findReplaceOptions.findText, prevPos);
        }

	if(prevPos == -1) {
            prevPos = txt.length() - 1;
        }
	
	return prevPos;
    }
}
