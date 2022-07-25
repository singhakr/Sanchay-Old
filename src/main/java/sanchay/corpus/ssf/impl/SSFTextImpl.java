package sanchay.corpus.ssf.impl;

import sanchay.corpus.ssf.SSFSentence;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.limsi.cm.util.SyntacticFeatureUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.*;
import sanchay.corpus.parallel.Alignable;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.query.QueryValue;
import sanchay.corpus.ssf.query.SSFQuery;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.corpus.xml.XMLProperties;
import sanchay.properties.KeyValueProperties;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.UtilityFunctions;
import sanchay.util.query.FindReplaceOptions;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.GATEDOMElement;
import sanchay.xml.dom.SanchayDOMElement;
import sanchay.xml.dom.TypeCraftDOMElement;

public class SSFTextImpl extends Text
        implements Serializable, SSFText, SanchayDOMElement,
        TypeCraftDOMElement, GATEDOMElement
{
    protected String ssfFile;
    protected String charset;

    protected String xmlDeclaration;
    protected String dtdDeclaration;
    public String metaData;
    protected String id;
    protected List<SSFSentence> sentences;
    protected List<SSFParagraph> paragraphs;

    protected AlignmentUnit<SSFText> alignmentUnit;
    
    protected boolean tagged;

    public SSFTextImpl()
    {
        metaData = "";

        paragraphs = new ArrayList<SSFParagraph>();
        sentences = new ArrayList<SSFSentence>();

        alignmentUnit = new AlignmentUnit();
    }

    /**
     * @return the ssfFile
     */
    @Override
    public String getSSFFile()
    {
        return ssfFile;
    }

    /**
     * @param ssfFile the ssfFile to set
     */
    @Override
    public void setSSFFile(String ssfFile)
    {
        this.ssfFile = ssfFile;
    }

    /**
     * @return the charset
     */
    @Override
    public String getCharset()
    {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    @Override
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    @Override
    public String getMetaData()
    {
        return metaData;
    }

    @Override
    public void setMetaData(String md)
    {
        metaData = md;
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
    }
    
    public boolean isTagged()
    {
        return tagged;
    }
    
    public void isTagged(boolean t)
    {
        tagged = t;
    }

    @Override
    public void addParagraphBoundaries(int startIndex, int endIndex)
    {
        SSFParagraph para = new SSFParagraph(startIndex, endIndex);

        paragraphs.add(para);
    }

    @Override
    public void addParagraphBoundaries(int startIndex, int endIndex, String paraAttribs, String paraMetaData)
    {
        SSFParagraph para = new SSFParagraph(startIndex, endIndex, paraAttribs, paraMetaData);

        paragraphs.add(para);
    }

    @Override
    public SSFParagraph removeParagraphBoundaries(int index)
    {
        return (SSFParagraph) paragraphs.remove(index);
    }

    @Override
    public int countParagraph()
    {
        return paragraphs.size();
    }

    @Override
    public SSFText getParagraph(int index)
    {
        SSFParagraph para = (SSFParagraph) paragraphs.get(index);

        return (SSFText) getSSFText(para.getStartSentence(), para.getEndSentence() - para.getStartSentence());
    }

    @Override
    public SSFParagraph getParagraphForSentence(int index)
    {
        int count = countParagraph();

        SSFParagraph para = null;

        for (int i = 0; i < count; i++)
        {
            para = (SSFParagraph) paragraphs.get(i);

            int fromIndex = para.getStartSentence();
            int toIndex = para.getEndSentence();

            if (index >= fromIndex && index <= toIndex)
            {
                return para;
            }
        }

        return para;
    }

    @Override
    public int getParagraphIndexForSentence(int index)
    {
        int count = countParagraph();

        SSFParagraph para = null;

        for (int i = 0; i < count; i++)
        {
            para = (SSFParagraph) paragraphs.get(i);

            int fromIndex = para.getStartSentence();
            int toIndex = para.getEndSentence();

            if (index >= fromIndex && index <= toIndex)
            {
                return i;
            }
        }

        return -1;
    }

    protected void insertSentenceInToParaAndReallocateIndices(int index)
    {
        int count = countParagraph();
        int paraIndex = getParagraphIndexForSentence(index);

        if (paraIndex == -1)
        {
            return;
        }

        for (int i = paraIndex; i < count; i++)
        {
            SSFParagraph para = (SSFParagraph) paragraphs.get(i);

            int fromIndex = para.getStartSentence();
            int toIndex = para.getEndSentence();

            if (i != paraIndex)
            {
                para.setStartSentence(fromIndex + 1);
            }

            para.setEndSentence(toIndex + 1);
        }
    }

    protected void removeSentenceFromParaAndReallocateIndices(int index)
    {
        int count = countParagraph();
        int paraIndex = getParagraphIndexForSentence(index);

        if (paraIndex == -1)
        {
            return;
        }

        for (int i = paraIndex; i < count; i++)
        {
            SSFParagraph para = (SSFParagraph) paragraphs.get(i);

            int fromIndex = para.getStartSentence();
            int toIndex = para.getEndSentence();

            if (i != paraIndex && fromIndex > 0)
            {
                para.setStartSentence(fromIndex - 1);
            }

            para.setEndSentence(toIndex - 1);
        }
    }

    // other methods
    @Override
    public int countSentences()
    {
        return sentences.size();
    }

    @Override
    public int countChunks()
    {
        int scount = countSentences();

        int count = 0;

        for (int i = 0; i < scount; i++)
        {
            count += getSentence(i).getRoot().countChildren();
        }

        return count;
    }

    /**
     *
     * @return
     */
    @Override
    public int countWords()
    {
        int scount = countSentences();

        int count = 0;

        for (int i = 0; i < scount; i++)
        {
            count += getSentence(i).getRoot().getAllLeaves().size();
        }

        return count;
    }

    @Override
    public int countCharacters()
    {
        int scount = countSentences();

        int count = 0;

        for (int i = 0; i < scount; i++)
        {
            List<SanchayMutableTreeNode> leaves = getSentence(i).getRoot().getAllLeaves();

            int lcount = leaves.size();

            for (int j = 0; j < lcount; j++)
            {
                count += ((SSFNode) leaves.get(j)).getLexData().length() + 1; // 1 for space
            }
        }

        return count;
    }

    @Override
    public double getAvgTokenLength()
    {
        double length = 0.0;
        
        int count = countSentences();
        int wcount = countWords();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = getSentence(i);
            
            List<SanchayMutableTreeNode> leaves = sen.getRoot().getAllLeaves();
            
            int lcount = leaves.size();
            
            for (int j = 0; j < lcount; j++) {

                length += ((SSFLexItem) leaves.get(j)).getLexData().length();
                
            }
        }
        
        length /= (double) wcount;
        
        return length;
    }

    @Override
    public double getAvgSentenceLength()
    {
        double length = 0.0;
        
        int count = countSentences();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = getSentence(i);
            length += sen.getRoot().getAllLeaves().size();
        }
        
        length /= (double) count;
        
        return length;        
    }

    @Override
    public double getAvgPOSCount(String tag)
    {
        double pcount = 0.0;
        
        int count = countSentences();
        int wcount = countWords();
        
        for (int i = 0; i < count; i++) {
            SSFSentence sen = getSentence(i);
            
            List<SanchayMutableTreeNode> leaves = sen.getRoot().getAllLeaves();
            
            int lcount = leaves.size();
            
            for (int j = 0; j < lcount; j++) {

                SSFLexItem lex = (SSFLexItem) leaves.get(i);
                
                if(lex.getName().equals(tag))
                {
                    pcount += 1;
                }
            }
        }
        
        pcount /= count;
        
        return pcount;        
    }

    @Override
    public double getAvgPOSCount(int category, int tagset)
    {
        double pcount = 0.0;
        
        int count = countSentences();
        
        for (int i = 0; i < count; i++) {
            
            SSFSentence sen = getSentence(i);

            pcount += SyntacticFeatureUtils.countSyntacticCategory(sen.getRoot(), category, tagset);
        }
        
        pcount /= (double) count;
        
        return pcount;                
    }

    @Override
    public void addSentence(SSFSentence sentence)
    {
        sentences.add(sentence);
//        insertSentenceInToParaAndReallocateIndices(sentences.size() - 1);
    }

    @Override
    public void addSentences(SSFStory s)
    {
        int scount = s.countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sen = s.getSentence(i);
            addSentence(sen);
        }
    }

    @Override
    public void insertSentence(SSFSentence sentence, int index)
    {
        sentences.add(index, sentence);

        insertSentenceInToParaAndReallocateIndices(index);
    }

    @Override
    public void removeSentence(int index)
    {
        sentences.remove(index);

        removeSentenceFromParaAndReallocateIndices(index);
    }

    @Override
    public void removeEmptySentences()
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            if (getSentence(i).getRoot() == null || getSentence(i).getRoot().countChildren() == 0)
            {
                removeSentence(i--);
                scount--;
            }
        }
    }

    @Override
    public void removeAttribute(String aname)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFPhrase root = getSentence(i).getRoot();

            if (root != null && root.countChildren() > 0)
            {
                root.removeAttribute(aname);
            }
        }
    }

    @Override
    public void hideAttribute(String aname)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFPhrase root = getSentence(i).getRoot();

            root.hideAttribute(aname);
        }
    }

    @Override
    public void unhideAttribute(String aname)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFPhrase root = getSentence(i).getRoot();

            root.hideAttribute(aname);
        }
    }

    @Override
    public SSFSentence getSentence(int index)
    {
        return (SSFSentence) sentences.get(index);
    }

    @Override
    public int findSentence(SSFSentence s)
    {
        return sentences.indexOf(s);
    }

    @Override
    public SSFSentence findSentence(String id)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sen = getSentence(i);

            if(sen.getId().equals(id)) {
                return sen;
            }
        }

        return null;
    }

    @Override
    public int findSentenceIndex(String id)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sen = getSentence(i);

            if(sen.getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void modifySentence(SSFSentence sentence, int index)
    {
//        sentences.add(index, sentence);
        sentences.set(index, sentence);
    }

    @Override
    public SSFText getSSFText(int startSentenceNum, int windowSize)
    {
        if (startSentenceNum < 0 || startSentenceNum >= sentences.size() || (startSentenceNum + windowSize) > sentences.size())
        {
            return null;
        }

        SSFText text = new SSFTextImpl();

        for (int i = 0; i < windowSize; i++)
        {
            text.addSentence(getSentence(startSentenceNum + i));
        }

        return text;
    }

    // For unannotated plain sentences.
    // Each sentence on a line of its own.
    @Override
    public void makeTextFromRaw(String rawText) throws Exception
    {
        clear();

        String lines[] = rawText.split("\n");

        for (int i = 0; i < lines.length; i++)
        {
            SSFSentence sentence = new SSFSentenceImpl();
            sentence.makeSentenceFromRaw(lines[i]);
            sentences.add(sentence);
        }
    }

    // For POS tagges sentences (in simple word/tag format)
    // Each sentence on a line of its own.
    @Override
    public void makeTextFromPOSTagged(String posTagged) throws Exception
    {
        clear();

        String lines[] = posTagged.split("\n");

        for (int i = 0; i < lines.length; i++)
        {
            SSFSentence sentence = new SSFSentenceImpl();
            sentence.makeSentenceFromPOSTagged(lines[i]);
            sentences.add(sentence);
        }
    }

    @Override
    public String makeString()
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String textStart = ssfp.getProperties().getPropertyValueForPrint("textStart");
        String textEnd = ssfp.getProperties().getPropertyValueForPrint("textEnd");

        if (textStart.startsWith("<"))
        {
            textStart += " id=\"" + id + "\">";
            textEnd += ">";
        } else
        {
            textStart += " " + id;
        }

        String SSF = textStart + "\n";

        if (metaData.equals("") == false)
        {
            SSF = metaData + "\n";
        }

        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentence SSFS = (SSFSentence) sentences.get(i);
            SSF += SSFS.makeString() + "\n";
        }

        SSF += textEnd + "\n";

        return SSF;
    }

    @Override
    public String convertToBracketForm(int spaces)
    {
//        SSFProperties ssfp = SSFNode.getSSFProperties();
//        String textStart = ssfp.getProperties().getPropertyValueForPrint("textStart");
//        String textEnd = ssfp.getProperties().getPropertyValueForPrint("textEnd");
//
//        if(textStart.startsWith("<"))
//        {
//            textStart += " id=\"" + id + "\">";
//            textEnd += ">";
//        }
//        else
//        {
//            textStart += " " + id;
//        }

//        String bracketForm = textStart + "\n\n";
        String bracketForm = "";

        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            bracketForm += SSFS.convertToBracketForm(spaces);
        }

//        bracketForm += textEnd + "\n";

        return bracketForm;
    }

    @Override
    public String makePOSNolex()
    {
        String posNolex = "";

        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            posNolex += SSFS.makePOSNolex();
        }

        return posNolex;        
    }

    @Override
    public void convertToPOSNolex()
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            SSFS.convertToPOSNolex();
        }
    }

    public void makeLowerCase()
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            SSFS.convertToLowerCase();
        }
    }

    @Override
    public String convertToPOSTagged(String sep)
    {
        String posTagged = "";

        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            posTagged += SSFS.convertToPOSTagged(sep);
        }

        return posTagged;        
    }

    @Override
    public String convertToPOSTagged()
    {
//        SSFProperties ssfp = SSFNode.getSSFProperties();
//        String textStart = ssfp.getProperties().getPropertyValueForPrint("textStart");
//        String textEnd = ssfp.getProperties().getPropertyValueForPrint("textEnd");
//
//        if(textStart.startsWith("<"))
//        {
//            textStart += " id=\"" + id + "\">";
//            textEnd += ">";
//        }
//        else
//        {
//            textStart += " " + id;
//        }
//
//        String posTagged = textStart + "\n\n";
        String posTagged = "";

        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            posTagged += SSFS.convertToPOSTagged();
        }

//        posTagged += textEnd + "\n";

        return posTagged;
    }

    @Override
    public String convertToRawText()
    {
//        SSFProperties ssfp = SSFNode.getSSFProperties();
//        String textStart = ssfp.getProperties().getPropertyValueForPrint("textStart");
//        String textEnd = ssfp.getProperties().getPropertyValueForPrint("textEnd");
//
//        if(textStart.startsWith("<"))
//        {
//            textStart += " id=\"" + id + "\">";
//            textEnd += ">";
//        }
//        else
//        {
//            textStart += " " + id;
//        }
//
//        String rawText = textStart + "\n\n";
        String rawText = "";

        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            rawText += SSFS.convertToRawText();
        }

//        rawText += textEnd + "\n";

        return rawText;
    }

    @Override
    public void print(PrintStream ps)
    {
        removeAttribute(SSFNode.HIGHLIGHT);
        ps.print(makeString());
    }

    @Override
    public void printLowerCase(PrintStream ps)
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = null;

            try {
                SSFS = (SSFSentenceImpl) sentences.get(i).getCopy();
            } catch (Exception ex) {
                Logger.getLogger(SSFTextImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ((SSFSentenceImpl) SSFS).convertToLowerCase();
            
            ps.print(SSFS.makeString());
        }
    }

    @Override
    public void printLowerCaseRawText(PrintStream ps)
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            ps.print(SSFS.convertToRawText().trim().toLowerCase());
        }
    }

    @Override
    public void printBracketForm(PrintStream ps, int spaces)
    {
        ps.print(convertToBracketForm(spaces).trim());
    }

    @Override
    public void printPOSTagged(PrintStream ps)
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            
            ps.println(SSFS.convertToPOSTagged().trim());
        }
    }

    @Override
    public void printPOSNolex(PrintStream ps)
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            
            ps.println(SSFS.makePOSNolex().trim());
        }
    }

    @Override
    public void printRawText(PrintStream ps)
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentenceImpl SSFS = (SSFSentenceImpl) sentences.get(i);
            
            ps.println(SSFS.convertToRawText().trim());
        }
    }

    @Override
    public void save(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        ssfFile = f;
        this.charset = charset;
//	clearFeatureStructures();
        PrintStream ps = new PrintStream(f, charset);
        print(ps);
    }

    @Override
    public void saveLowerCase(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printLowerCase(ps);        
    }    

    @Override
    public void saveLowerCaseRawText(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printLowerCaseRawText(ps);        
    }    

    @Override
    public void saveBracketForm(String f, String charset, int spaces) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printBracketForm(ps, spaces);
    }

    @Override
    public void savePOSTagged(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printPOSTagged(ps);
    }

    @Override
    public void saveRawText(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printRawText(ps);
    }

    @Override
    public void savePOSNolex(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printPOSNolex(ps);        
    }

    @Override
    public void saveAlignments()
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            FeatureStructure fs = sentence.getFeatureStructure();

            fs.setAlignmentUnit(sentence.getAlignmentUnit());
        }
    }

    @Override
    public void loadAlignments(SSFText tgtText, int parallelIndex)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            FeatureStructure fs = sentence.getFeatureStructure();

            AlignmentUnit aunit = fs.loadAlignmentUnit(sentence, this, tgtText, parallelIndex);

            if(aunit != null) {
                sentence.setAlignmentUnit(aunit);
            }
        }
    }

    @Override
    public void clearAlignments()
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);
            sentence.getAlignmentUnit().clearAlignments();
        }
    }

    @Override
    public Text getCopy()
    {
        return null;
    }

    @Override
    public void clear()
    {
        id = "";
        metaData = "";
        sentences.clear();
        paragraphs.clear();
    }

    @Override
    public List<SSFNode> getAllOccurrences(String word)
    {
        List<SSFNode> occurrences = new ArrayList<SSFNode>();

        List<SanchayMutableTreeNode> leaves = new ArrayList<SanchayMutableTreeNode>();

        SSFSentenceImpl sen = new SSFSentenceImpl();

        SSFPhrase root = new SSFPhrase();

        SSFNode ssfNode = new SSFNode();

        String lexData = new String();

        for ( int i=0 ; i < countSentences() ; i++ )
        {
            sen = (SSFSentenceImpl) getSentence(i);

            root = sen.getRoot();

            leaves = root.getAllLeaves();

            for ( int j=0 ;  j < leaves.size() ; j++ )
            {
                ssfNode = ( SSFNode ) leaves.get(j);

                lexData = ssfNode.getLexData();

                if(  lexData.equals( word )  )
                {
                    occurrences.add( ssfNode );
                }
            }
        }

        return occurrences;
    }

    @Override
    public void clearFeatureStructures()
    {
        int count = countSentences();

        for (int i = 0; i < count; i++)
        {
            SSFSentence ssfs = (SSFSentence) sentences.get(i);
            ssfs.clearFeatureStructures();
        }
    }

    @Override
    public void clearAnnotation(long annoLevelFlags)
    {
        int count = countSentences();

        for (int i = 0; i < count; i++)
        {
            SSFSentence ssfs = (SSFSentence) sentences.get(i);
            ssfs.clearAnnotation(annoLevelFlags);
        }
    }

    @Override
    public void reallocateSentenceIDs()
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentence SSFS = (SSFSentence) sentences.get(i);
            SSFS.setId(Integer.toString((i + 1)));
        }
    }

    @Override
    public void reallocateNodeIDs()
    {
        for (int i = 0; i < sentences.size(); i++)
        {
            SSFSentence SSFS = (SSFSentence) sentences.get(i);

            if (SSFS.getRoot() != null)
            {
                SSFS.getRoot().reallocateId("");
            }
        }
    }

    public static void transferTags(SSFText tgtText, SSFText srcText)
    {
        int scount = srcText.countSentences();

        if (scount != tgtText.countSentences())
        {
            System.err.println(GlobalProperties.getIntlString("Error:_The_number_of_sentences_is_different_in_the_source_and_target_files."));
        }

        for (int i = 0; i < scount; i++)
        {
            SSFSentence srcSentence = srcText.getSentence(i);
            SSFSentence tgtSentence = tgtText.getSentence(i);

            SSFPhrase srcRoot = srcSentence.getRoot();
            SSFPhrase tgtRoot = tgtSentence.getRoot();

            List<SanchayMutableTreeNode> srcWords = srcRoot.getAllLeaves();
            List<SanchayMutableTreeNode> tgtWords = tgtRoot.getAllLeaves();

            int wcount = srcWords.size();

            if (wcount != tgtWords.size())
            {
                System.err.println(GlobalProperties.getIntlString("Error:_The_number_of_words_is_different_in_the_source_and_target_sentence:_") + (i + 1));
                System.err.println(GlobalProperties.getIntlString("Source_sentence:"));
                System.err.println("\t" + srcRoot.toString());
                System.err.println(GlobalProperties.getIntlString("Target_sentence:"));
                System.err.println("\t" + tgtRoot.toString());
            }

            for (int j = 0; j < wcount; j++)
            {
                SSFNode srcWord = (SSFNode) srcWords.get(j);
                SSFNode tgtWord = (SSFNode) tgtWords.get(j);

                tgtWord.setName(srcWord.getName());
            }
        }
    }

    @Override
    public int matchedSentenceCount(FindReplaceOptions findReplaceOptions)
    {
        int findCount = 0;

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            if (sentence.matches(findReplaceOptions))
            {
                findCount++;
            }
        }

        return findCount;
    }

    @Override
    public boolean matches(FindReplaceOptions findReplaceOptions)
    {
        boolean match = false;

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);
            match = match || sentence.matches(findReplaceOptions);
        }

        return match;
    }

    @Override
    public void setMorphTags(KeyValueProperties morphTags)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);
            sentence.setMorphTags(morphTags);
        }
    }

    @Override
    public LinkedHashMap<String, Integer> getWordFreq()
    {
        LinkedHashMap<String, Integer> allWords = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> tags = sentence.getRoot().getWordFreq();

            UtilityFunctions.mergeMap(allWords, tags);
        }

        return allWords;
    }

    @Override
    public LinkedHashMap<String, Integer> getPOSTagFreq()
    {
        LinkedHashMap<String, Integer> allTags = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> tags = sentence.getRoot().getPOSTagFreq();

            UtilityFunctions.mergeMap(allTags, tags);
        }

        return allTags;
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
        LinkedHashMap<String, Integer> allWords = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> tags = sentence.getRoot().getWordTagPairFreq();

            UtilityFunctions.mergeMap(allWords, tags);
        }

        return allWords;
    }

    public int countWordTagPairFreq()
    {
        int count = 0;

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> tags = sentence.getRoot().getWordTagPairFreq();

            count += tags.size();
        }

        return count;
    }

    @Override
    public LinkedHashMap<String, Integer> getChunkTagFreq()
    {
        LinkedHashMap<String, Integer> allTags = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> tags = sentence.getRoot().getChunkTagFreq();

            UtilityFunctions.mergeMap(allTags, tags);
        }

        return allTags;
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
        LinkedHashMap<String, Integer> allRels = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> tags = sentence.getRoot().getGroupRelationFreq();

            UtilityFunctions.mergeMap(allRels, tags);
        }

        return allRels;
    }

    public int countGroupRelations()
    {
        LinkedHashMap<String, Integer> rels = getGroupRelationFreq();

        return rels.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getChunkRelationFreq()
    {
        LinkedHashMap<String, Integer> allRels = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> tags = sentence.getRoot().getChunkRelationFreq();

            UtilityFunctions.mergeMap(allRels, tags);
        }

        return allRels;
    }

    public int countChunkRelations()
    {
        LinkedHashMap<String, Integer> rels = getChunkRelationFreq();

        return rels.size();
    }

    @Override
    public LinkedHashMap<String, Integer> getAttributeFreq()
    {
        LinkedHashMap<String, Integer> allAttribs = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> attribs = sentence.getRoot().getAttributeFreq();

            UtilityFunctions.mergeMap(allAttribs, attribs);
        }

        return allAttribs;
    }

    /**
     *
     * @return
     */
    @Override
    public int countAttributes()
    {
        LinkedHashMap<String, Integer> attribs = getAttributeFreq();

        return attribs.size();
    }

    /**
     *
     * @return
     */
    @Override
    public LinkedHashMap<String, Integer> getAttributeValueFreq()
    {
        LinkedHashMap<String, Integer> allAttribs = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> attribs = sentence.getRoot().getAttributeValueFreq();

            UtilityFunctions.mergeMap(allAttribs, attribs);
        }

        return allAttribs;
    }

    /**
     *
     * @return
     */
    @Override
    public int countAttributeValues()
    {
        LinkedHashMap<String, Integer> attribs = getAttributeValueFreq();

        return attribs.size();
    }

    public LinkedHashMap<String, Integer> getAttributeValuePairFreq()
    {
        LinkedHashMap<String, Integer> allAttribs = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> attribs = sentence.getRoot().getAttributeValuePairFreq();

            UtilityFunctions.mergeMap(allAttribs, attribs);
        }

        return allAttribs;
    }

    /**
     *
     * @return
     */
    @Override
    public int countAttributeValuePairs()
    {
        LinkedHashMap<String, Integer> attribs = getAttributeValuePairFreq();

        return attribs.size();
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
    @Override
    public int countUntaggedWords()
    {
        int count = 0;

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            List<SSFNode> nodes = sentence.getRoot().getNodesForName("");

            int ncount = nodes.size();

            for (int j = 0; j < ncount; j++)
            {
                SSFNode node = nodes.get(i);

                if(node instanceof SSFLexItem) {
                    count++;
                }
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
        LinkedHashMap<String, Integer> allWords = new LinkedHashMap();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> words = sentence.getRoot().getUnchunkedWordFreq();

            UtilityFunctions.mergeMap(allWords, words);
        }

        return allWords;
    }

    /**
     *
     * @return
     */
    @Override
    public int countUnchunkedWords()
    {
        int count = 0;

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<String, Integer> words = sentence.getRoot().getUnchunkedWordFreq();

            count += words.size();
        }

        return count;
    }

    @Override
    public int countUntaggedChunks()
    {
        int count = 0;

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            List<SSFNode> nodes = sentence.getRoot().getNodesForName("");

            int ncount = nodes.size();

            for (int j = 0; j < ncount; j++)
            {
                SSFNode node = nodes.get(i);

                if(node instanceof SSFPhrase) {
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    public int countWordsWithoutMorph()
    {
        int count = 0;

        return count;
    }

    @Override
    public int countChunksWithoutMorph()
    {
        int count = 0;

        return count;
    }

    @Override
    public int countDependencyRelations()
    {
        int count = 0;

        return count;
    }

    @Override
    public AlignmentUnit getAlignmentUnit()
    {
        return alignmentUnit;
    }

    @Override
    public void setAlignmentUnit(AlignmentUnit alignmentUnit)
    {
        this.alignmentUnit = alignmentUnit;
    }

    @Override
    public SSFText getAlignedObject(String alignmentKey)
    {
        return alignmentUnit.getAlignedObject(alignmentKey);
    }
    
    @Override
    public List<SSFText> getAlignedObjects()
    {
        return alignmentUnit.getAlignedObjects();
    }

    @Override
    public SSFText getFirstAlignedObject()
    {
        return alignmentUnit.getFirstAlignedObject();
    }

    @Override
    public SSFText getAlignedObject(int i)
    {
        return alignmentUnit.getAlignedObject(i);
    }

    @Override
    public SSFText getLastAlignedObject()
    {
        return alignmentUnit.getLastAlignedObject();
    }

    @Override
    public LinkedHashMap<QueryValue, String> getMatchingValues(SSFQuery ssfQuery)
    {
        clearHighlights();

        LinkedHashMap<QueryValue, String> matches = new LinkedHashMap<QueryValue, String>();

        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);

            LinkedHashMap<QueryValue, String> qmatches = sentence.getMatchingValues(ssfQuery);

            int mcount = qmatches.size();

            Iterator<QueryValue> itr = qmatches.keySet().iterator();

            while(itr.hasNext())
            {
                QueryValue value = itr.next();

                matches.put(value, (i + 1) + "::" + ((SSFNode) value).getId() + "::" + qmatches.get(value));
            }
        }

        return matches;
    }

    @Override
    public void reallocatePositions(String positionAttribName, String nullWordString)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);
            sentence.reallocatePositions(positionAttribName, nullWordString);
        }
    }

    @Override
    public void clearHighlights()
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);
            sentence.clearHighlights();
        }
    }

    @Override
    public void convertEncoding(SanchayEncodingConverter encodingConverter, String nullWordString)
    {
        int scount = countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence sentence = getSentence(i);
            sentence.convertEncoding(encodingConverter, nullWordString);
        }
    }

    public org.dom4j.dom.DOMDocument getDOMDocument()
    {
        DOMDocument domDocument = new DOMDocument(getDOMElement());

        return domDocument;
    }

    public org.dom4j.dom.DOMDocument getTypeCraftDOMDocument()
    {
        DOMDocument domDocument = new DOMDocument(getTypeCraftDOMElement());

        return domDocument;
    }

    public org.dom4j.dom.DOMDocument getGATEDOMDocument()
    {
        DOMDocument domDocument = new DOMDocument(getGATEDOMElement());

        return domDocument;
    }

    @Override
    public int readXML(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException
    {
        Element rootNode = null;

        rootNode = XMLUtils.parseW3CXML(f, charset, false);
//        rootNode = XMLUtils.parseDomXML(f);

        if(rootNode != null)
        {
            readXML(rootNode);
        }

        return 0;
    }

    public int readTypeCraftXML(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException
    {
        Element rootNode = null;

        rootNode = XMLUtils.parseW3CXML(f, charset, false);

        if(rootNode != null)
        {
            readTypeCraftXML(rootNode);
        }

        return 0;
    }

    public int readGATEXML(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException
    {
        Element rootNode = null;

        rootNode = XMLUtils.parseW3CXML(f, charset, false);

        if(rootNode != null)
        {
            readGATEXML(rootNode);
        }

        return 0;
    }

    @Override
    public int readPOSTagged(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception
    {
        BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));

        String line = "";        
	int lineNum = 0;

        SSFSentenceImpl sen = null;

        while((line = lnReader.readLine()) != null)
        {
            line = line.trim();
            
            if(line.equals("") == false)
            {
                sen = new SSFSentenceImpl();
                sen.makeSentenceFromPOSTagged(line, errorLog, lineNum);
                addSentence(sen);            
            }
        }

        return 0;
    }

    @Override
    public int readHindenCorp(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception
    {
        BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));

        String line = "";        
	int lineNum = 0;

        SSFSentenceImpl sen = null;

        while((line = lnReader.readLine()) != null)
        {
            line = line.trim();
            
            if(line.equals("") == false)
            {
                sen = new SSFSentenceImpl();
                sen.makeSentenceFromPOSTagged(line, errorLog, lineNum);
                addSentence(sen);            
            }
        }

        return 0;
    }

    @Override
    public int readVerticalPOSTagged(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception
    {

        return 0;
    }

    @Override
    public int readBIFormat(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception
    {

        return 0;
    }

    @Override
    public int readRaw(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception
    {
        BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));

        String line = "";        

        SSFSentenceImpl sen = null;

        while((line = lnReader.readLine()) != null)
        {
            line = line.trim();
            
            if(line.equals("") == false)
            {
                sen = new SSFSentenceImpl();
                sen.makeSentenceFromRaw(line);
                addSentence(sen);            
            }
        }

        return 0;
    }

    @Override
    public int readChunked(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception
    {
        BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));

        String line = "";        
	int lineNum = 0;

        SSFSentenceImpl sen = null;

        while((line = lnReader.readLine()) != null)
        {
            line = line.trim();
            
            if(line.equals("") == false)
            {
                sen = new SSFSentenceImpl();
                sen.makeSentenceFromChunked(line, errorLog, lineNum);
                addSentence(sen);            
            }
        }

        return 0;
    }

    @Override
    public int readSSFFormat(String filePath, String charset, CorpusType corpusType, List<String> errorLog /*Strings*/) throws FileNotFoundException, IOException, UnsupportedEncodingException, SAXException, Exception
    {

        return 0;
    }

    @Override
    public int saveXML(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        PrintStream ps = null;

        try
        {
            ps = new PrintStream(f, charset);

            OutputFormat outputFormat = OutputFormat.createPrettyPrint();

            XMLWriter writer = new XMLWriter(ps, outputFormat);

            writer.write(getDOMDocument());

            writer.close();


//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//
//            Transformer serializer = transformerFactory.newTransformer();
//
//            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//            serializer.setOutputProperty("{http;//xml.apache.org/xslt}indent-amount", "2");
//
//            serializer.transform(new DOMSource(getDOMElement()), new StreamResult(ps));

//            ps.println("<!DOCTYPE frameset PUBLIC \"-//Sanchay//Frameset//EN\" SYSTEM \"" + "./data/propbank/resource/frameset/frameset.dtd\">");

//            printXML(ps);

        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SSFText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SSFText.class.getName()).log(Level.SEVERE, null, ex);
        }

        ps.close();

        return 0;
    }

    public int saveTypeCraftXML(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        PrintStream ps = null;

        try
        {
            ps = new PrintStream(f, charset);

            OutputFormat outputFormat = OutputFormat.createPrettyPrint();

            XMLWriter writer = new XMLWriter(ps, outputFormat);

            writer.write(getTypeCraftDOMDocument());

            writer.close();

        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SSFText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SSFText.class.getName()).log(Level.SEVERE, null, ex);
        }

        ps.close();

        return 0;
    }

    public int saveGATEXML(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        PrintStream ps = null;

        try
        {
            ps = new PrintStream(f, charset);

            OutputFormat outputFormat = OutputFormat.createPrettyPrint();

            XMLWriter writer = new XMLWriter(ps, outputFormat);

            writer.write(getGATEDOMDocument());

            writer.close();

        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SSFText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SSFText.class.getName()).log(Level.SEVERE, null, ex);
        }

        ps.close();

        return 0;
    }

    @Override
    public DOMElement getDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("documentTag"));

        int count = countParagraph();

        if(count >= 1)
        {
            for (int i = 0; i < count; i++)
            {
                SSFText paragraph = getParagraph(i);

                DOMElement pdomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("paragraphTag"));

                int scount = paragraph.countSentences();

                for (int j = 0; j < scount; j++)
                {
                    SSFSentenceImpl sentence = (SSFSentenceImpl) getSentence(j);

                    DOMElement sdomElement = ((SanchayDOMElement) sentence).getDOMElement();

                    pdomElement.add(sdomElement);
                }

                domElement.add(pdomElement);
            }
        }
        else
        {
            DOMElement pdomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("paragraphTag"));

            int scount = countSentences();

            for (int j = 0; j < scount; j++)
            {
                SSFSentenceImpl sentence = (SSFSentenceImpl) getSentence(j);

                DOMElement sdomElement = ((SanchayDOMElement) sentence).getDOMElement();

                pdomElement.add(sdomElement);
            }

            domElement.add(pdomElement);
        }

        return domElement;
    }

    @Override
    public DOMElement getTypeCraftDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcPhrasesTag"));

        int scount = countSentences();

        for (int j = 0; j < scount; j++)
        {
            SSFSentenceImpl sentence = (SSFSentenceImpl) getSentence(j);

            DOMElement sdomElement = ((TypeCraftDOMElement) sentence).getTypeCraftDOMElement();

            domElement.add(sdomElement);
        }

        return domElement;
    }

    @Override
    public DOMElement getGATEDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcPhrasesTag"));

        int scount = countSentences();

        for (int j = 0; j < scount; j++)
        {
            SSFSentenceImpl sentence = (SSFSentenceImpl) getSentence(j);

            DOMElement sdomElement = ((GATEDOMElement) sentence).getGATEDOMElement();

            domElement.add(sdomElement);
        }

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

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("metaDataTag")))
                {
//                        ((SanchayDOMElement) featureStructure).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("paragraphTag")))
                {
//                    SSFParagraph paragraph = new SSFParagraph();
//                    paragraph.readXML(element);

                    int startIndex = (countSentences() - 1);
                    int endIndex = startIndex;

                    if(startIndex == -1) {
                        startIndex = 0;
                    }

                    Node snode = element.getFirstChild();

                    while(snode != null)
                    {
                        if(snode instanceof Element)
                        {
                            Element selement = (Element) snode;

                            if(selement.getTagName().equals(xmlProperties.getProperties().getPropertyValue("metaDataTag")))
                            {
        //                        ((SanchayDOMElement) featureStructure).readXML(element);
                            }
                            else if(selement.getTagName().equals(xmlProperties.getProperties().getPropertyValue("sentenceTag")))
                            {
                                SSFSentenceImpl sentence = new SSFSentenceImpl();
                                sentence.readXML(selement);
                                addSentence(sentence);

                                endIndex++;
                            }
                        }

                        snode = snode.getNextSibling();
                    }

                    addParagraphBoundaries(startIndex, endIndex);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readTypeCraftXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("metaDataTag")))
                {
//                        ((SanchayDOMElement) featureStructure).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("tcPhraseTag")))
                {
                    SSFSentenceImpl sentence = new SSFSentenceImpl();
                    sentence.readTypeCraftXML(element);
                    addSentence(sentence);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readGATEXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("metaDataTag")))
                {
//                        ((SanchayDOMElement) featureStructure).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("tcPhraseTag")))
                {
                    SSFSentenceImpl sentence = new SSFSentenceImpl();
                    sentence.readGATEXML(element);
                    addSentence(sentence);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void printXML(PrintStream ps) {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();

        XMLWriter writer = null;

        try {
            writer = new XMLWriter(ps, outputFormat);

            writer.write(getDOMDocument());

            writer.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSFTextImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSFTextImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

//        ps.println(getXML());
    }

    @Override
    public void printTypeCraftXML(PrintStream ps) {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();

        XMLWriter writer = null;

        try {
            writer = new XMLWriter(ps, outputFormat);

            writer.write(getTypeCraftDOMDocument());

            writer.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSFTextImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSFTextImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

//        ps.println(getXML());
    }

    @Override
    public void printGATEXML(PrintStream ps) {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();

        XMLWriter writer = null;

        try {
            writer = new XMLWriter(ps, outputFormat);

            writer.write(getGATEDOMDocument());

            writer.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSFTextImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSFTextImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

//        ps.println(getXML());
    }

    public class SSFParagraph implements Alignable, SanchayDOMElement
    {

        protected int startSentence;
        protected int endSentence;
        protected String paraAttribs;
        protected String paraMetaData;

        protected AlignmentUnit<SSFParagraph> alignmentUnit;

        public SSFParagraph()
        {
        }

        public SSFParagraph(int startIndex, int endIndex)
        {
            this.startSentence = startIndex;
            this.endSentence = endIndex;
        }

        public SSFParagraph(int startIndex, int endIndex, String paraAttribs, String paraMetaData)
        {
            this.startSentence = startIndex;
            this.endSentence = endIndex;

            this.paraAttribs = paraAttribs;
            this.paraMetaData = paraMetaData;
        }

        public int getStartSentence()
        {
            return startSentence;
        }

        public void setStartSentence(int s)
        {
            startSentence = s;
        }

        public int getEndSentence()
        {
            return endSentence;
        }

        public void setEndSentence(int e)
        {
            endSentence = e;
        }

        public String getAttribsString()
        {
            return paraAttribs;
        }

        public void setAttribsString(String a)
        {
            paraAttribs = a;
        }

        public String getMetaData()
        {
            return paraMetaData;
        }

        public void setMetaData(String m)
        {
            paraMetaData = m;
        }

        public void addMetaData(String m)
        {
            paraMetaData += m;
        }

        @Override
        public AlignmentUnit getAlignmentUnit()
        {
            return alignmentUnit;
        }

        @Override
        public void setAlignmentUnit(AlignmentUnit alignmentUnit)
        {
            this.alignmentUnit = alignmentUnit;
        }

        @Override
        public SSFParagraph getAlignedObject(String alignmentKey)
        {
            return alignmentUnit.getAlignedObject(alignmentKey);
        }

        @Override
        public List<SSFParagraph> getAlignedObjects()
        {
            return alignmentUnit.getAlignedObjects();
        }

        @Override
        public SSFParagraph getFirstAlignedObject()
        {
            return alignmentUnit.getFirstAlignedObject();
        }

        @Override
        public SSFParagraph getAlignedObject(int i)
        {
            return alignmentUnit.getAlignedObject(i);
        }

        @Override
        public SSFParagraph getLastAlignedObject()
        {
            return alignmentUnit.getLastAlignedObject();
        }

        @Override
        public DOMElement getDOMElement() {
            XMLProperties xmlProperties = SSFNode.getXMLProperties();

            DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("paragraphTag"));

            int count = countSentences();

            for (int i = 0; i < count; i++)
            {
                SSFSentence sentence = getSentence(i);

                DOMElement idomElement = ((SanchayDOMElement) sentence).getDOMElement();

                domElement.add(idomElement);
            }

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
        public void readXML(Element domElement) {
            XMLProperties xmlProperties = SSFNode.getXMLProperties();

            Node node = domElement.getFirstChild();

            while(node != null)
            {
                if(node instanceof Element)
                {
                    Element element = (Element) node;

                    if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("metaDataTag")))
                    {
//                        ((SanchayDOMElement) featureStructure).readXML(element);
                    }
                    else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("sentenceTag")))
                    {
                        SSFSentenceImpl sentence = new SSFSentenceImpl();
                        sentence.readXML(element);
                    }
                }

                node = node.getNextSibling();
            }
        }

        @Override
        public void printXML(PrintStream ps) {
            ps.println(getXML());
        }
    }

    public static void main(String[] args)
    {
//        SSFSentence stc = new SSFSentenceImpl();
//        try
//        {
//                stc.read("ssfTree2.txt"); //throws java.io.FileNotFoundException;
//        }
//        catch(Exception e)
//        {
//                System.out.println("Exception!");
//                e.printStackTrace();
//        }
//        stc.root.print(System.out);
//        SSFPhrase test = (SSFPhrase)stc.root.getChild(4);
//        test.removeLayer();
//        System.out.println("/////////////deleting layer 5////////////////////////");
//        stc.root.reallocateID("");
//        stc.root.print(System.out);
//        stc.root.addChild(new SSFPhraseImpl("3,4,3","harshit","NP","",stc.root));
//        System.out.println("/////////////ADDING TO ROOT////////////////////////");
//        stc.root.reallocateID("");
//        stc.root.print(System.out);
    }
}
