/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import org.xml.sax.SAXException;
import sanchay.common.types.CorpusType;
import sanchay.corpus.Text;
import sanchay.corpus.parallel.Alignable;
import sanchay.corpus.ssf.impl.SSFTextImpl.SSFParagraph;
import sanchay.corpus.ssf.query.QueryValue;
import sanchay.corpus.ssf.query.SSFQuery;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.properties.KeyValueProperties;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.util.query.FindReplaceOptions;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SSFText extends Alignable {
    
    String getSSFFile();

    void setSSFFile(String ssfFile);

    String getCharset();

    void setCharset(String charset);

    String getMetaData();
    
    void setMetaData(String md);
    
    String getId() ;

    void setId(String i);

    int countParagraph();

    SSFText getParagraph(int index);
    
    SSFParagraph getParagraphForSentence(int index);

    int getParagraphIndexForSentence(int index);

    void addParagraphBoundaries(int startIndex, int endIndex);

    void addParagraphBoundaries(int startIndex, int endIndex, String paraAttribs, String paraMetaData);

    SSFParagraph removeParagraphBoundaries(int index);
	
    int countSentences();

    int countChunks();

    int countWords();
    
    double getAvgTokenLength();

    double getAvgSentenceLength();

    double getAvgPOSCount(String tag);

    double getAvgPOSCount(int category, int tagset);

    LinkedHashMap<String, Integer> getWordFreq();

    LinkedHashMap<String, Integer> getPOSTagFreq();

    LinkedHashMap<String, Integer> getGroupRelationFreq();

    LinkedHashMap<String, Integer> getWordTagPairFreq();

    LinkedHashMap<String, Integer> getChunkTagFreq();

    LinkedHashMap<String, Integer> getChunkRelationFreq();

    LinkedHashMap<String, Integer> getAttributeFreq();

    LinkedHashMap<String, Integer> getAttributeValueFreq();

    LinkedHashMap<String, Integer> getAttributeValuePairFreq();

    LinkedHashMap<String, Integer> getUntaggedWordFreq();

    LinkedHashMap<String, Integer> getUnchunkedWordFreq();

    int countCharacters();
	
    void addSentence(SSFSentence sentence);

    void addSentences(SSFStory story);

    void insertSentence(SSFSentence sentence, int index);

    void removeSentence(int index);

    void removeAttribute(String aname);

    void hideAttribute(String aname);
    void unhideAttribute(String aname);

    void removeEmptySentences();
	
    SSFSentence getSentence(int index);
	
    int findSentence(SSFSentence s);
    SSFSentence findSentence(String id);
    int findSentenceIndex(String id);
	
    void modifySentence(SSFSentence sentence, int index);
    
    SSFText getSSFText(int startSentenceNum, int windowSize);

    void makeTextFromRaw(String rawText) throws Exception;

    void makeTextFromPOSTagged(String posTagged) throws Exception;
	
    String makeString();

    String makePOSNolex();

    void convertToPOSNolex();
	
    String convertToBracketForm(int spaces);

    String convertToPOSTagged();

    String convertToPOSTagged(String sep);
    
    String convertToRawText();

    int readXML(String filePath, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException;

    int readPOSTagged(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception;

    int readHindenCorp(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception;

    int readVerticalPOSTagged(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception;

    int readBIFormat(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception;

    int readRaw(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception;

    int readChunked(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception;

    int readSSFFormat(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception;
    
    void print(PrintStream ps);

    void printLowerCase(PrintStream ps);

    void printLowerCaseRawText(PrintStream ps);
    
    void printXML(PrintStream ps);
    
    void printBracketForm(PrintStream ps, int spaces);

    void printPOSTagged(PrintStream ps);

    void printPOSNolex(PrintStream ps);

    void printRawText(PrintStream ps);

    void save(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException;

    void saveLowerCase(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException;

    void saveRawText(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException;

    int saveXML(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException;

    void saveBracketForm(String f, String charset, int spaces) throws FileNotFoundException, UnsupportedEncodingException;

    void savePOSTagged(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException;

    void savePOSNolex(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException;

    void saveLowerCaseRawText(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException;

    void saveAlignments();

    void loadAlignments(SSFText tgtText, int parallelIndex);

    void clearAlignments();

    Text getCopy();

    void clear();

    void reallocateSentenceIDs();
    
    void reallocateNodeIDs();
   
    void clearFeatureStructures();

    void clearAnnotation(long annoLevelFlags);

    int matchedSentenceCount(FindReplaceOptions findReplaceOptions);

    List<SSFNode> getAllOccurrences(String wrd);

    boolean matches(FindReplaceOptions findReplaceOptions);

    void setMorphTags(KeyValueProperties morphTags);

    int countPOSTags();

    int countChunkTags();

    int countAttributes();

    int countAttributeValues();

    int countAttributeValuePairs();

    int countUntaggedWords();

    int countUnchunkedWords();

    int countUntaggedChunks();

    int countWordsWithoutMorph();

    int countChunksWithoutMorph();

    int countDependencyRelations();

    LinkedHashMap<QueryValue, String> getMatchingValues(SSFQuery ssfQuery);

    void reallocatePositions(String positionAttribName, String nullWordString);

    void clearHighlights();

    void convertEncoding(SanchayEncodingConverter encodingConverter, String nullWordString);

    void makeLowerCase();

    boolean isTagged();
    
    void isTagged(boolean t);

}