package sanchay.corpus.ssf.tree;

import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import java.awt.Color;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.MutableTreeNode;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import sanchay.GlobalProperties;
import sanchay.corpus.parallel.Alignable;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureAttributeImpl;
import sanchay.corpus.ssf.features.impl.FeatureStructureImpl;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.features.impl.FeatureValueImpl;
import sanchay.corpus.ssf.query.QueryValue;
import sanchay.corpus.ssf.query.SSFQuery;
import sanchay.corpus.xml.XMLProperties;
import sanchay.gui.common.SanchayLanguages;
import sanchay.properties.KeyValueProperties;
import sanchay.table.gui.SanchayJTable;
import sanchay.tree.SanchayEdge;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.UtilityFunctions;
import sanchay.util.query.FindReplace;
import sanchay.util.query.FindReplaceOptions;
import sanchay.xml.dom.GATEDOMElement;
import sanchay.xml.dom.SanchayDOMElement;
import sanchay.xml.dom.TypeCraftDOMElement;

public class SSFNode extends SanchayMutableTreeNode
        implements MutableTreeNode, Alignable, Serializable, QueryValue,
        SanchayDOMElement, TypeCraftDOMElement, GATEDOMElement
{
    private static SSFProperties ssfProps;
    private static XMLProperties xmlProps;

    protected String id; // e.g., 5.1
//    protected String lexdata; // e.g., fair
    protected List<Integer> tagIndices;
    protected List<Integer> lexIndices;

    protected List<Integer> commentIndices;

    protected FeatureStructures fs; // Corresponds to stringFS

    protected AlignmentUnit<SSFNode> alignmentUnit;
    
    static Index<String> vocabIndex = new HashIndex<String>();    
    static Index<String> tagIndex = new HashIndex<String>();    
    static Index<String> commentIndex = new HashIndex<String>();    
    
    public static String WORD_SEPARATOR = " ";
    public static String TAG_SEPARATOR = "__";
    public static String COMMENT_SEPARATOR = " ";

    protected boolean nestedFS;

    protected boolean isTriangle;

    protected long flags = 0;

    public static final int HIGHLIGHTED = 0x00000000001;

    public static final String HIGHLIGHT = "hlt";

    public SSFNode() {
        super();

        this.id = "";
        this.lexIndices = new ArrayList<Integer>();
        this.tagIndices = new ArrayList<Integer>();
        this.commentIndices = new ArrayList<Integer>();
        this.fs = null;
	
    	nestedFS = false;
        
        alignmentUnit = new AlignmentUnit<SSFNode>();
    }

    public SSFNode(Object userObject) {
        super(userObject);

        this.id = "";
        this.lexIndices = new ArrayList<Integer>();
        this.tagIndices = new ArrayList<Integer>();
        this.commentIndices = new ArrayList<Integer>();
        this.fs = null;
	
        nestedFS = false;
        
        alignmentUnit = new AlignmentUnit<SSFNode>();
    }
    
    public SSFNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);

        this.id = "";
        this.lexIndices = new ArrayList<Integer>();
        this.tagIndices = new ArrayList<Integer>();
        this.commentIndices = new ArrayList<Integer>();
        this.fs = null;
	
    	nestedFS = false;
        
        alignmentUnit = new AlignmentUnit<SSFNode>();
    }

    public SSFNode(String id, String lexdata, String name, String stringFS) throws Exception
    {
        super();

        this.id = id;
        this.lexIndices = getIndices(lexdata, vocabIndex, WORD_SEPARATOR, true);
        this.tagIndices = getIndices(name, tagIndex, TAG_SEPARATOR, true);
        this.commentIndices = new ArrayList<Integer>();
        this.fs = new FeatureStructuresImpl();

        fs.readString(stringFS);
	
    	nestedFS = false;
        
        alignmentUnit = new AlignmentUnit<SSFNode>();
    }

    public SSFNode(String id, String lexdata, String name, FeatureStructures fs)
    {
        super();

        this.id = id;
        this.lexIndices = getIndices(lexdata, vocabIndex, WORD_SEPARATOR, true);
        this.commentIndices = new ArrayList<Integer>();
        this.tagIndices = getIndices(name, tagIndex, TAG_SEPARATOR, true);
        this.fs = fs;
	
    	nestedFS = false;
        
        alignmentUnit = new AlignmentUnit<SSFNode>();
    }

    public SSFNode(String id, String lexdata, String name, String stringFS, Object userObject) throws Exception
    {
        super();

        this.id = id;
        this.lexIndices = getIndices(lexdata, vocabIndex, WORD_SEPARATOR, true);
        this.tagIndices = getIndices(name, tagIndex, TAG_SEPARATOR, true);
        this.commentIndices = new ArrayList<Integer>();
        this.fs = new FeatureStructuresImpl();
        fs.readString(stringFS);

    	nestedFS = false;

//        setUserObject(userObject);
        
        alignmentUnit = new AlignmentUnit<SSFNode>();
    }

    public SSFNode(String id, String lexdata, String name, FeatureStructures fs, Object userObject)
    {
        super();

        this.id = id;
        this.lexIndices = getIndices(lexdata, vocabIndex, WORD_SEPARATOR, true);
        this.tagIndices = getIndices(name, tagIndex, TAG_SEPARATOR, true);
        this.commentIndices = new ArrayList<Integer>();
        this.fs = fs;

    	nestedFS = false;

//        setUserObject(userObject);
        
        alignmentUnit = new AlignmentUnit<SSFNode>();
    }

    public String getId() 
    {
        return id;
    }

    public void setId(String i) 
    {
        id = i;
    }

    public String getLexData() 
    {
        return getString(lexIndices, vocabIndex, WORD_SEPARATOR);
    }

    public void setLexData(String ld) 
    {
        lexIndices = getIndices(ld, vocabIndex, WORD_SEPARATOR, true);
    }

    public String getName() 
    {
        return getString(tagIndices, tagIndex, TAG_SEPARATOR);
    }

    public void setName(String n) 
    {
        tagIndices = getIndices(n, tagIndex, TAG_SEPARATOR, true);
    }

    public String getComment() 
    {
        return getString(commentIndices, commentIndex, COMMENT_SEPARATOR);
    }

    public void setComment(String c) 
    {
        commentIndices = getIndices(c, commentIndex, COMMENT_SEPARATOR, true);
    }

    public long getVocabularySize()
    {
        long vocabularySize = vocabIndex.size();
        
        return vocabularySize;
    }

    public long getTagVocabularySize()
    {
        long vocabularySize = tagIndex.size();
        
        return vocabularySize;
    }

    public long getCommentVocabularySize()
    {
        long vocabularySize = commentIndex.size();
        
        return vocabularySize;
    }
    
    public static List<Integer> getIndices(String wds, Index<String> index, String sep, boolean add)
    {
        String parts[] = wds.split(sep);
        
        List<Integer> indices = new ArrayList<Integer>(parts.length);
        
        for (int i = 0; i < parts.length; i++) {
            int wi = index.indexOf(parts[i], add);
            
            indices.add(wi);
        }
        
        return indices;
    }

    public static String getString(List<Integer> wdIndices, Index<String> index, String sep)
    {
        String str = "";
        
        int i = 0;
        for (Integer wi : wdIndices) {
            if(i == 0)
            {
                str = index.get(wi);
            }
            else
            {
                str += sep + index.get(wi);
            }
            
            i++;
        }
        
        return str;
    }

    public boolean isTriangle()
    {
        return isTriangle;
    }

    public void isTriangle(boolean isTriangle)
    {
        this.isTriangle = isTriangle;
    }

    @Override
    public AlignmentUnit getAlignmentUnit()
    {
        return alignmentUnit;
    }

    @Override
    public void setAlignmentUnit(AlignmentUnit alignmentUnit)
    {
        alignmentUnit.setAlignmentObject(this);
        this.alignmentUnit = alignmentUnit;
    }

    @Override
    public SSFNode getAlignedObject(String alignmentKey)
    {
        return alignmentUnit.getAlignedObject(alignmentKey);
    }
    
    @Override
    public List<SSFNode> getAlignedObjects()
    {
        return alignmentUnit.getAlignedObjects();
    }

    @Override
    public SSFNode getFirstAlignedObject()
    {
        return alignmentUnit.getFirstAlignedObject();
    }

    @Override
    public SSFNode getAlignedObject(int i)
    {
        return alignmentUnit.getAlignedObject(i);
    }

    @Override
    public SSFNode getLastAlignedObject()
    {
        return alignmentUnit.getLastAlignedObject();
    }

    public void loadAlignments(SSFSentence srcSentence, SSFSentence tgtSentence, int parallelIndex)
    {
        if(fs == null)
        {
            fs = new FeatureStructuresImpl();
            FeatureStructure featureStructure = new FeatureStructureImpl();
            
            fs.addAltFSValue(featureStructure);
        }
        
        AlignmentUnit aunit = fs.loadAlignmentUnit(this, srcSentence, tgtSentence, parallelIndex);

        if(aunit != null) {
            alignmentUnit = aunit;
        }

        if(this instanceof SSFPhrase)
        {
            List<SSFNode> allChildren = ((SSFPhrase) this).getAllChildren();

            int count = allChildren.size();

            for (int i = 0; i < count; i++)
            {
                SSFNode node = allChildren.get(i);

                node.loadAlignments(srcSentence, tgtSentence, parallelIndex);
            }
        }
    }

    public void saveAlignments()
    {
        if(fs == null) {
            return;
        }
        
        fs.setAlignmentUnit(alignmentUnit);

        if(this instanceof SSFPhrase)
        {
            List<SSFNode> allChildren = ((SSFPhrase) this).getAllChildren();

            int count = allChildren.size();

            for (int i = 0; i < count; i++)
            {
                SSFNode node = allChildren.get(i);

                node.saveAlignments();
            }
        }
    }

    public boolean isHighlighted()
    {
        boolean hf = UtilityFunctions.flagOn(flags, HIGHLIGHTED);

        boolean ha = false;

        String hs = getAttributeValue(HIGHLIGHT);

        if(hs != null && hs.equals("true")) {
            ha = true;
        }

        return (hf || ha);
    }

    public void isHighlighted(boolean h)
    {
        if(h)
        {
            flags = UtilityFunctions.switchOnFlags(flags, HIGHLIGHTED);

            setAttributeValue(HIGHLIGHT, "true");

            return;
        }

        setAttributeValue(HIGHLIGHT, "false");

        flags = UtilityFunctions.switchOffFlags(flags, HIGHLIGHTED);
    }

    public void clearHighlights()
    {
        isHighlighted(false);

    	int count = countChildren();

        for (int i = 0; i < count; i++ )
        {
    		SSFNode child = (SSFNode) getChildAt(i);
            child.clearHighlights();
        }
    }

    public boolean hasLeaves()
    {
        return false;
    }

    public void setMorphTags(KeyValueProperties morphTags)
    {
        if(morphTags == null) {
            return;
        }

        if(this instanceof SSFPhrase)
        {
            int depth = getDepth();

            if(depth == 1)
            {
                List<SanchayMutableTreeNode> leaves = getAllLeaves();

                int count = leaves.size();

                for (int i = 0; i < count; i++)
                {
                    SSFNode leaf = (SSFNode) leaves.get(i);
                    String leafPOSTag = leaf.getName();

                    String mtag = morphTags.getPropertyValue(leafPOSTag);

                    FeatureStructures leafFss = leaf.getFeatureStructures();

                    if(leafFss != null)
                    {
                        if(mtag == null || mtag.equals(""))
                        {
                            SSFNode prevNode = leaf.getPrevious();

                            if(prevNode != null)
                            {
                                mtag = morphTags.getPropertyValue(prevNode.getName());

                                leafFss.setAllAttributeValues("cat", mtag);
                            }
                        }
                        else {
                            leafFss.setAllAttributeValues("cat", mtag);
                        }
                    }
                }
            }
            else if(depth > 1)
            {
                int ccount = countChildren();

                for (int i = 0; i < ccount; i++)
                {
                    SSFNode childNode = (SSFNode) getChildAt(i);
                    childNode.setMorphTags(morphTags);
                }
            }
        }
    }

    public FeatureStructures getFeatureStructures() 
    {
        return fs;
    }

    public String getStringFS() 
    {
        if(fs == null) {
            return "";
        }

        return fs.makeString();
    }

    public void setFeatureStructures(FeatureStructures f) 
    {
        fs = f;
    }

    public List<String> getAttributeNames()
    {
        if(fs == null) {
            return null;
        }

        return fs.getAttributeNames();
    }

    public String getAttributeValue(String attibName)
    {
        if(fs == null) {
            return null;
        }

        return fs.getAttributeValueString(attibName);
    }

    public List<String> getAttributeValues()
    {
        if(fs == null) {
            return null;
        }

        return fs.getAttributeValues();
    }

    public List<String> getAttributeValuePairs()
    {
        if(fs == null) {
            return null;
        }

        return fs.getAttributeValuePairs();
    }

    public String[] getOneOfAttributeValues(String attibNames[])
    {
        if(fs == null) {
            return null;
        }

        return fs.getOneOfAttributeValues(attibNames);
    }

    public void setAttributeValue(String attibName, String val)
    {
        if(fs == null) {
            fs = new FeatureStructuresImpl();
        }

        fs.setAttributeValue(attibName, val);
    }

    public void concatenateAttributeValue(String attibName, String val, String sep)
    {
        if(fs == null) {
            fs = new FeatureStructuresImpl();
        }

        fs.concatenateAttributeValue(attibName, val, sep);
    }

    public FeatureAttribute getAttribute(String attibName)
    {
        if(fs == null) {
            return null;
        }

        return fs.getAttribute(attibName);
    }

//    public SSFNode getParent() 
//    {
//        return parent;
//    }
//
//    public void setParent(SSFNode p) // #% &get_parent( $node , [$tree] ) -> $parent_node
//    {
//        parent = p;
//    }

    public int countChildren()
    {
//        if(lexdata.equals(""))
//            return getChildCount();

        return 0;
    }

    // other methods

    public boolean isLeafNode()
    {
        return isLeaf();
    }

    public int removeNode ()
    {
        SSFNode prnt = (SSFNode) getParent();
        int ind = ((SSFPhrase) prnt).findChild(this);
        ((SSFPhrase) prnt).removeChild(ind);

        return ind;
    }

    public void removeEmptyPhrases()
    {

    }

    public void removeNonChunkPhrases()
    {
        
    }

    public boolean isNonChunkPhrase()
    {
        return false;
    }

    public void removeDSRedundantPhrases()
    {

    }

    public boolean isDSRedundantPhrase()
    {
        return false;
    }

    public int removeLayer()
    {
        SSFNode prnt = (SSFNode) getParent();

        if(prnt == null) {
            return -1;
        }
	
        int ind = ((SSFPhrase) prnt).findChild(this);

        alignmentUnit.clearAlignments();

        if(isLeafNode()) {
            ((SSFPhrase) prnt).removeChild(ind);
        }
        else
        {
            ((SSFPhrase) prnt).addChildrenAt(((SSFPhrase) this).getAllChildren(), ind);
            this.removeNode();
        }

        return ind;
    }

    public void removeAttribute(String aname)
    {
        if(fs != null && fs.countAltFSValues() > 0)
        {
            FeatureStructure tfs = fs.getAltFSValue(0);
            tfs.removeAttribute(aname);
        }
    }

    public void hideAttribute(String aname)
    {
        if(fs != null && fs.countAltFSValues() > 0) {
            fs.hideAttribute(aname);
        }
    }

    public void unhideAttribute(String aname)
    {
        if(fs != null && fs.countAltFSValues() > 0) {
            fs.unhideAttribute(aname);
        }
    }

    public SSFNode getPrevious()
    {
//        return ((SSFPhrase) getParent()).getPrevious(this);
        return (SSFNode) getPreviousSibling();
    }

    public SSFNode getNext()
    {
//        return ((SSFPhrase) getParent()).getNext(this);
        return (SSFNode) getNextSibling();
    }

    public SSFNode getNodeForId(String id)
    {
        if(this.id.equalsIgnoreCase(id)) {
            return this;
        }
        
        return null;
    }

    public static SSFProperties getSSFProperties()
    {
	if(ssfProps == null) {
            loadSSFProperties();
        }
	
        return ssfProps;
    }

    public static void setSSFProperties(SSFProperties ssfp)
    {
        ssfProps = ssfp;
    }

    public static void loadSSFProperties()
    {
	ssfProps = new SSFProperties();

	try {
		ssfProps.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), GlobalProperties.getIntlString("UTF-8"));
	} catch (FileNotFoundException ex) {
		ex.printStackTrace();
	} catch (IOException ex) {
		ex.printStackTrace();
	}
    }

    public static XMLProperties getXMLProperties()
    {
	if(xmlProps == null) {
            loadXMLProperties();
        }

        return xmlProps;
    }

    public static void setXMLProperties(XMLProperties xmlp)
    {
        xmlProps = xmlp;
    }

    public static void loadXMLProperties()
    {
	xmlProps = new XMLProperties();

	try {
		xmlProps.read(GlobalProperties.resolveRelativePath("props/xml-props.txt"), GlobalProperties.getIntlString("UTF-8"));
	} catch (FileNotFoundException ex) {
		ex.printStackTrace();
	} catch (IOException ex) {
		ex.printStackTrace();
	}
    }

    public int readString(String s) throws Exception
    {
        String fieldSeparatorRegex = ssfProps.getProperties().getPropertyValue("fieldSeparatorRegex");
        String fields[] = s.split(fieldSeparatorRegex, 4);

        id = fields[0];
        lexIndices = getIndices(fields[1], vocabIndex, WORD_SEPARATOR, true);

	if(fields.length > 2) {
            tagIndices = getIndices(fields[2], tagIndex, TAG_SEPARATOR, true);
        }

        if(fields.length == 4 && fields[3].equals("") == false)
        {
            fs = new FeatureStructuresImpl();
            fs.readString(fields[3]);
        }

        return 0;
    }
    
    @Override
    public Object getUserObject()
    {
        if(this instanceof SSFPhrase) {
            return getName();
        }

        return getLexData();
    }

    public void print(PrintStream ps)
    {
        ps.print(makeString());
    }

    protected String makeTopString()
    {
        String toString;
//        String fieldSeparatorPrint = ssfProps.getProperties().getPropertyValueForPrint("fieldSeparatorPrint");
        String fieldSeparatorPrint = getSSFProperties().getProperties().getPropertyValueForPrint("fieldSeparatorPrint");

//        String chunkStart = ssfProps.getProperties().getPropertyValueForPrint("chunkStart");
        String chunkStart = getSSFProperties().getProperties().getPropertyValueForPrint("chunkStart");
        String ld = chunkStart;

        if(isLeafNode()) {
            ld = getLexData();
        }
        
        String name = getName();

        if (fs == null)
        {
            toString = (id + fieldSeparatorPrint + ld + fieldSeparatorPrint + name + fieldSeparatorPrint + "");
        }
        else
        {
            toString = (id + fieldSeparatorPrint + ld + fieldSeparatorPrint + name + fieldSeparatorPrint + fs.makeString());
        }

        return toString;
    }

    public String makeSummaryString()
    {
        String string = "";
        
        String lexdata = getLexData();

        if(lexdata.equals("") == false) {
            string = lexdata;
        }
        
        String name = getName();

        if(name.equals("") == false)
        {
            if(string.equals("") == false) {
                string += " : " + name;
            }
            else {
                string += name;
            }
        }

        if(fs != null && fs.makeString().equals("") == false)
        {
            if(string.equals("") == false) {
                string += " : " + fs.makeString();
            }
            else {
                string += fs.makeString();
            }
        }

        return string;
    }

    public void fillSSFData(SSFNode n)
    {
	id = n.id;
        setLexData(n.getLexData());
        setName(n.getName());
	fs = n.fs;
    }
    
    public void collapseLexicalItems()
    {
    }

    public void collapseLexicalItemsDeep()
    {
        
    }
    
    public String makeString()
    {
        return (makeTopString() + "\n");
    }
    
    public String makeRawSentence()
    {
	return getLexData();
    }

    public void convertToPOSNolex()
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

	String unknownTag = ssfp.getProperties().getPropertyValueForPrint("unknownTag");

        if(isLeafNode() == false)
	{
            List<SanchayMutableTreeNode> leaves = getAllLeaves();

            for(int i = 0; i < leaves.size(); i++)
            {
                SSFNode lv = (SSFNode) leaves.get(i);

                if(lv.getName().equals("") == false) {
                    lv.setLexData(lv.getName());
                }
                else {
                    lv.setLexData(unknownTag);
                }
                
                lv.setName("");
            }
	}
        else
        {
            if(getName().equals("") == false) {
                setLexData(getName());
            }
            else {
                setLexData(unknownTag);
            }
        }       
    }
    
    public String makePOSNolex()
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

	String unknownTag = ssfp.getProperties().getPropertyValueForPrint("unknownTag");

        String posNolex = "";
	
	if(isLeafNode() == false)
	{
            List<SanchayMutableTreeNode> leaves = getAllLeaves();

            for(int i = 0; i < leaves.size(); i++)
            {
                SSFNode lv = (SSFNode) leaves.get(i);

                if(lv.getName().equals("") == false) {
                    posNolex += lv.getName();
                }
                else {
                    posNolex += unknownTag;
                }

                if(i < leaves.size() - 1) {
                    posNolex += " ";
                }
                else {
                    posNolex += "\n";
                }
            }
	}
        else
        {
            if(getName().equals("") == false) {
                posNolex += getName();
            }
            else {
                posNolex += unknownTag;
            }
        }
	
	return posNolex;                        
    }
    
    public void convertToLowerCase()
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        for(int i = 0; i < leaves.size(); i++)
        {
            SSFNode lv = (SSFNode) leaves.get(i);
            
            lv.setLexData(lv.getLexData().toLowerCase());
        }        
    }

    public String convertToPOSTagged(String sep)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

	String wordTagSeparator = ssfp.getProperties().getPropertyValueForPrint("wordTagSeparator");
        
        if(sep != null)
        {
            wordTagSeparator = sep;
        }
        
	String unknownTag = ssfp.getProperties().getPropertyValueForPrint("unknownTag");

        String posTagged = "";
	
	if(isLeafNode() == false)
	{
            List<SanchayMutableTreeNode> leaves = getAllLeaves();

            for(int i = 0; i < leaves.size(); i++)
            {
                SSFNode lv = (SSFNode) leaves.get(i);

                if(lv.getName().equals("") == false) {
                    posTagged += lv.getLexData() + wordTagSeparator + lv.getName();
                }
                else {
                    posTagged += lv.getLexData() + wordTagSeparator + unknownTag;
                }

                if(i < leaves.size() - 1) {
                    posTagged += " ";
                }
                else {
                    posTagged += "\n";
                }
            }
	}
        else
        {
            if(getName().equals("") == false) {
                posTagged += getLexData() + wordTagSeparator + getName();
            }
            else {
                posTagged += getLexData() + wordTagSeparator + unknownTag;
            }
        }
	
	return posTagged;                
    }

    public String convertToPOSTagged()
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String wordTagSeparator = ssfp.getProperties().getPropertyValueForPrint("wordTagSeparator");
        
        return convertToPOSTagged(wordTagSeparator);
    }
    
    public String convertToBracketForm(int spaces)
    {
	String bracketForm = "";

	String rootName = ssfProps.getProperties().getPropertyValueForPrint("rootName");

	String bracketFormStart = ssfProps.getProperties().getPropertyValueForPrint("bracketFormStart");
	String bracketFormEnd = ssfProps.getProperties().getPropertyValueForPrint("bracketFormEnd");

	String wordTagSeparator = ssfProps.getProperties().getPropertyValueForPrint("wordTagSeparator");
	String unknownTag = ssfProps.getProperties().getPropertyValueForPrint("unknownTag");

//	try
//	{
//	    if(ssfProps != null)
//		rootName = new String(rootName.getBytes(), ssfProps.getProperties().getPropertyValue("encoding"));
//	} catch (UnsupportedEncodingException ex)
//	{
//	    ex.printStackTrace();
//	}
	    
	if(isLeafNode() == false)
	{
//	    if(getName().equals(rootName) == false)
	    bracketForm += bracketFormStart;

	    for(int j = 0; j < spaces; j++) {
                bracketForm += " ";
            }

	    int count = countChildren();
	    for (int i = 0; i < count; i++)
	    {
		SSFNode child = (SSFNode) getChildAt(i);
		
		bracketForm += child.convertToBracketForm(spaces);
		
		if(i < count - 1)
		{
		    for(int j = 0; j < spaces; j++) {
                        bracketForm += " ";
                    }
//		    bracketForm += " ";
		}
	    }

	    for(int j = 0; j < spaces; j++) {
                bracketForm += " ";
            }

//	    if(getName().equals(rootName) == false)
//	    {
	    if(getName().equals("") == false) {
                bracketForm += bracketFormEnd + wordTagSeparator + getName();
            }
//		bracketForm += bracketFormEnd + wordTagSeparator + getName() + " ";
	    else {
                bracketForm += bracketFormEnd + wordTagSeparator + unknownTag;
            }
//		bracketForm += bracketFormEnd + wordTagSeparator + unknownTag + " ";

//	    for(int i = 1; i < spaces; i++)
//		bracketForm += " ";
//	    }
	}
	else
	{
            if(getName().equals("")) {
                    bracketForm += getLexData();
                }
            else {
                    bracketForm += getLexData() + wordTagSeparator + getName();
                }
//	    bracketForm += getLexData();
	}
	
	return bracketForm;
    }
    
    public String convertToBracketFormHTML(int spaces)
    {
	String bracketFormHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><font color=#0000ff>"
		+ convertToBracketForm(spaces) + "</font></html>";
	
	return bracketFormHTML;
    }

    @Override
    public SanchayMutableTreeNode getCopy() throws Exception
    {
        String str = makeString();
        
        SSFNode ssfNode = new SSFNode();
        ssfNode.readString(str);

        ssfNode.flags = flags;

        return ssfNode;
    }

    public void copyExtraData(SSFNode node)
    {
        int count = countChildren();
        int ncount = node.countChildren();

        if(count != ncount) {
            return;
        }

        flags = node.flags;

        for (int i = 0; i < count; i++)
        {
            ((SSFNode) getChildAt(i)).copyExtraData((SSFNode) node.getChildAt(i));
        }
    }
    
    public boolean allowsNestedFS()
    {
	return nestedFS;
    }
    
    public void allowNestedFS(boolean b)
    {
	nestedFS = b;
    }

    public void clear()
    {
        id = "";
        lexIndices = new ArrayList<Integer>();
        tagIndices = new ArrayList<Integer>();
        fs = null;
    }

    // Add hoc
    public void addDefaultAttributes()
    {
	FeatureStructureImpl lfs = (FeatureStructureImpl) getFeatureStructures().getAltFSValue(0);
	
	if(getName().equals("VG"))
	{
	    FeatureAttributeImpl fa = new FeatureAttributeImpl();
	    fa.setName("name");
	    fa.addAltValue(new FeatureValueImpl(""));
	    lfs.addAttribute(fa);
	}
	else if(getName().equals("NP") || getName().equals("PP") || getName().equals("JJP"))
	{
	    FeatureAttributeImpl fa = new FeatureAttributeImpl();
	    fa.setName("drel");
	    fa.addAltValue(new FeatureValueImpl(""));
	    lfs.addAttribute(fa);
	}
    }

    public void clearFeatureStructures()
    {
	int count = countChildren();
	
        for (int i = 0; i < count; i++ )
        {
            SSFNode node = (SSFNode) getChildAt(i);
	    
	    FeatureStructures fss = node.getFeatureStructures();
	    
	    if(fss != null) {
                fss.setToEmpty();
            }
	    else
	    {
		fss = new FeatureStructuresImpl();
		fss.setToEmpty();
		node.setFeatureStructures(fss);
	    }
	    
	    node.clearFeatureStructures();
        }
    }

    public void clearAnnotation(long annoLevelFlags)
    {
	int count = countChildren();

	if(
	    ((this instanceof SSFLexItem) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.POS_TAGS))
		|| ((this instanceof SSFPhrase) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_NAMES))
	) {
            setName("");
        }

	if(getFeatureStructures() != null && fs.makeString().equals("") == false
	    && (
		((this instanceof SSFLexItem) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEXITEM_FEATURE_STRUCTURES))
		    || ((this instanceof SSFPhrase) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_FEATURE_STRUCTURES))
		    || ((this instanceof SSFLexItem) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_MANDATORY_ATTRIBUTES))
		    || ((this instanceof SSFPhrase) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_MANDATORY_ATTRIBUTES))
		    || ((this instanceof SSFLexItem) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_EXTRA_ATTRIBUTES))
		    || ((this instanceof SSFPhrase) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_EXTRA_ATTRIBUTES))
		    || ((this instanceof SSFNode) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.ALL_EXCEPT_THE_FIRST_FS))
		    || ((this instanceof SSFNode) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.PRUNE_THE_FS))
	    )
	)
    {
        if((this instanceof SSFLexItem) && (UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_MANDATORY_ATTRIBUTES))
                || (UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNK_EXTRA_ATTRIBUTES)) )
        {
            long modifiedAnnotationFlags = UtilityFunctions.switchOffFlags(annoLevelFlags, SSFCorpus.CHUNK_MANDATORY_ATTRIBUTES);
            modifiedAnnotationFlags = UtilityFunctions.switchOffFlags(modifiedAnnotationFlags, SSFCorpus.CHUNK_EXTRA_ATTRIBUTES);
    	    getFeatureStructures().clearAnnotation(modifiedAnnotationFlags, this);
        }
        else if((this instanceof SSFPhrase) && (UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_MANDATORY_ATTRIBUTES))
                || (UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.LEX_EXTRA_ATTRIBUTES)) )
        {
            long modifiedAnnotationFlags = UtilityFunctions.switchOffFlags(annoLevelFlags, SSFCorpus.LEX_MANDATORY_ATTRIBUTES);
            modifiedAnnotationFlags = UtilityFunctions.switchOffFlags(modifiedAnnotationFlags, SSFCorpus.LEX_EXTRA_ATTRIBUTES);
    	    getFeatureStructures().clearAnnotation(modifiedAnnotationFlags, this);
        }
        
        getFeatureStructures().clearAnnotation(annoLevelFlags, this);
    }

	if(UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.COMMENTS)) {
            setComment("");
        }
	
        for (int i = 0; i < count; i++ )
        {
            SSFNode node = (SSFNode) getChildAt(i);
	    node.clearAnnotation(annoLevelFlags);
        }

	// Inefficient, but will do for the time being
	if(
	    ((this instanceof SSFPhrase) && UtilityFunctions.flagOn(annoLevelFlags, SSFCorpus.CHUNKS))
		    && (getParent() == null || (getParent() != null && getParent().getParent() == null))
	) {
            ((SSFPhrase) this).flatten();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof SSFNode)) {
            return false;
        }
        
        if(obj == null) {
            return false;
        }
        
	SSFNode nobj = (SSFNode) obj;

	if(getId().equals(nobj.getId()) == false) {
            return false;
        }

	if(getLexData().equals(nobj.getLexData()) == false) {
            return false;
        }

	if(getName().equalsIgnoreCase(nobj.getName()) == false) {
            return false;
        }

	if(getFeatureStructures() == null && nobj.getFeatureStructures() == null) {
            return true;
        }

	if(getFeatureStructures() == null || nobj.getFeatureStructures() == null) {
            return false;
        }

	if(getFeatureStructures().equals(nobj.getFeatureStructures()) == false) {
            return false;
        }

	return true;
    }
    
    @Override
    public void setValuesInTable(DefaultTableModel tbl, int mode)
    {
	if(rowIndex == -1 || tbl.getRowCount() <= 0 || tbl.getColumnCount() <= 0) {
            return;
        }
	
	if(getName().equals("")) {
            tbl.setValueAt(getLexData(), rowIndex, columnIndex);
        }
	else if(getLexData().equals("")) {
            tbl.setValueAt(getName(), rowIndex, columnIndex);
        }
	else {
            tbl.setValueAt(getLexData(), rowIndex, columnIndex);
        }
//	    tbl.setValueAt(getLexData() + ":" + getName(), rowIndex, columnIndex);
    }

    @Override
    public String toString()
    {
        return makeRawSentence();
    }
    
    public boolean matches(FindReplaceOptions findReplaceOptions)
    {
        boolean match = false;
        
        Pattern pattern = FindReplace.compilePattern(findReplaceOptions.findText, findReplaceOptions);
        Matcher matcher = null;
        
        String text = getLexData();
        
        if(text != null && text.equals("") == false)
        {        
            matcher = pattern.matcher(text);

            if(matcher.find()) {
                match = true;
            }
        }
        else {
            match = false;
        }

//        if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.tag != null
//                && findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.tag.equals("") == false)
//        {
//            pattern = FindReplace.compilePattern(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.tag, findReplaceOptions);
//
//            String tag = getName();
//
//            if(tag != null && tag.equals("") == false)
//            {
//                matcher = pattern.matcher(tag);
//
//                if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr1.equals("And"))
//                    match = match && matcher.find();
//                else
//                    match = match || matcher.find();
//            }
//            else
//            {
//                if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr1.equals("And"))
//                    match = match && false;
//                else
//                    match = match || false;
//            }
//        }
//
//        if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature != null
//                && findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature.equals("") == false)
//        {
//            if(getAttribute(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature) != null)
//            {
//                if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr2.equals("And"))
//                    match = match && true;
//                else
//                    match = match || true;
//            }
//            else
//            {
//                if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr2.equals("And"))
//                    match = match && false;
//                else
//                    match = match || false;
//            }
//        }
//
//        if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.featureValue != null
//                && findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.featureValue.equals("") == false)
//        {
//            pattern = FindReplace.compilePattern(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.featureValue, findReplaceOptions);
//            String val = (String) getAttributeValueString(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature);
//
//            if(val != null && val.equals("") == false)
//            {
//                matcher = pattern.matcher(val);
//
//                if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr3.equals("And"))
//                    match = match && matcher.find();
//                else
//                    match = match || matcher.find();
//            }
//            else
//            {
//                if(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr3.equals("And"))
//                    match = match && false;
//                else
//                    match = match || false;
//            }
//        }

        if(match) {
            isHighlighted(true);
        }
        
        return match;
    }

    public List<SSFNode> replaceNames(String n, String replace)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        Pattern p = Pattern.compile(n);
//        Pattern p = Pattern.compile(n, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        Matcher m = p.matcher(getName());

        if(m.find())
        {
            setName(replace);
            nodes.add(this);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) getChildAt(i);
    	    m = p.matcher(node.getName());

            if (m.find())
            {
                node.setName(replace);
                nodes.add(node);
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) getChildAt(i)).replaceNames(n, replace));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> replaceLexData(String ld, String replace)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        Pattern p = Pattern.compile(ld);
//        Pattern p = Pattern.compile(ld, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        Matcher m = p.matcher(getLexData());

        if(m.find())
        {
            setLexData(replace);
            nodes.add(this);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) getChildAt(i);

    	    m = p.matcher(node.getLexData());

            if (m.find())
            {
                node.setLexData(replace);
                nodes.add(node);
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) getChildAt(i)).replaceLexData(ld, replace));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public static List<SSFNode> replaceAttribVal(SSFNode node, String attrib, String val, String attribReplace, String valReplace, boolean createAttrib)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();
        
        FeatureStructures fss = node.getFeatureStructures();

        if (fss != null && fss.countAltFSValues() > 0)
        {
            FeatureStructure ifs = fss.getAltFSValue(0);

//            FeatureAttribute fa = ifs.getAttribute(attrib);

            FeatureAttribute fa = ifs.searchAttributeValue(attrib, val, false);

            if(fa != null)
            {
                if(FeatureStructuresImpl.getFSProperties().isMandatory(UtilityFunctions.backFromExactMatchRegex(attrib)) == false)
                {
                    if(valReplace.equals("")) {
                        ifs.removeAttribute(UtilityFunctions.backFromExactMatchRegex(attrib));
                    }
                }
            }
            else if(createAttrib || fa == null)
            {
                if(FeatureStructuresImpl.getFSProperties().isMandatory(UtilityFunctions.backFromExactMatchRegex(attrib)))
                {
                    if(ifs.hasMandatoryAttribs() == false) {
                        ifs.addMandatoryAttributes();
                    }
                }
                else
                {
                    fa = new FeatureAttributeImpl();
                    fa.setName(attribReplace);

                    FeatureValue fv = new FeatureValueImpl();
                    fv.setValue("");

                    fa.addAltValue(fv);

                    ifs.addAttribute(fa);
                }
            }

            ifs.replaceAttributeValues(attrib, val, attribReplace, valReplace);
            nodes.add(node);
        }
        else if(createAttrib)
        {
            fss = new FeatureStructuresImpl();
            FeatureStructure ifs = new FeatureStructureImpl();

            FeatureAttribute fa = new FeatureAttributeImpl();
            fa.setName(attribReplace);

            FeatureValue fv = new FeatureValueImpl();
            fv.setValue(valReplace);

            fa.addAltValue(fv);

            ifs.addAttribute(fa);

            fss.addAltFSValue(ifs);

            node.setFeatureStructures(fss);
            nodes.add(node);
        }

//        if (node.isLeafNode() == false)
//        {
//            nodes.addAll(((SSFPhrase) node).replaceAttribVal(attrib, val, attribReplace, valReplace, createAttrib));
//        }

//        nodes = (Vector) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> replaceAttribVal(String attrib, String val, String attribReplace, String valReplace, boolean createAttrib)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        FeatureStructures fss = getFeatureStructures();
        FeatureStructure lfs = null;

        if (createAttrib)
        {
            if(fss == null)
            {
                fss = new FeatureStructuresImpl();
                setFeatureStructures(fss);
            }

            if(fss.countAltFSValues() == 0)
            {
                lfs = new FeatureStructureImpl();
                fss.addAltFSValue(lfs);
            }
        }

        if(fss != null && fss.countAltFSValues() > 0)
        {
            lfs = fss.getAltFSValue(0);

            if(createAttrib || lfs.searchAttributeValue(attrib, val, false) != null)
            {
                replaceAttribVal(this, attrib, val, attribReplace, valReplace, createAttrib);
                nodes.add(this);
            }
        }

//        int count = getChildCount();
//        for (int i = 0; i < count; i++)
//        {
//            SSFNode node = (SSFNode) getChildAt(i);
//
//            nodes.addAll(replaceAttribVal(node, attrib, val, attribReplace, valReplace, createAttrib));
//        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public LinkedHashMap<QueryValue, String> getMatchingValues(SSFQuery ssfQuery)
    {
        LinkedHashMap<QueryValue, String> matches = new LinkedHashMap<QueryValue, String>();

        try
        {
            LinkedHashMap<SSFNode, String> qmatches = ssfQuery.executeQuery(this);

            if(qmatches != null && qmatches.size() > 0)
            {
                matches.putAll(qmatches);
            }
        } catch (Exception ex)
        {
//            System.err.println("Error in processing the SSF query.");
//            Logger.getLogger(SSFNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(this instanceof SSFPhrase)
        {
            SSFPhrase phrase = (SSFPhrase) this;

            List<SSFNode> allChildren = phrase.getAllChildren();

            int lcount = allChildren.size();

            for (int j = 0; j < lcount; j++)
            {
                SSFNode childNode = allChildren.get(j);

                matches.putAll(childNode.getMatchingValues(ssfQuery));
            }
        }

        return matches;
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

    public static String getPOSTagsPath(String dirPath, String langEnc)
    {
        return getPropertiesPath("POSTags", dirPath, langEnc);
    }

    public static String getPhraseNamesPath(String dirPath, String langEnc)
    {
        return getPropertiesPath("PhraseNames", dirPath, langEnc);
    }

    public static String getPOSTagLevelsMapPath(String dirPath, String langEnc)
    {
        return getPropertiesPath("POSTagsLevels", dirPath, langEnc);
    }

    public static String getPhraseNamesLevelsMapPath(String dirPath, String langEnc)
    {
        return getPropertiesPath("PhraseNamesLevels", dirPath, langEnc);
    }

    public static String getPropertiesPath(String ptype, String dirPath, String langEnc)
    {
        boolean isRelativePath = GlobalProperties.isRelativePath(dirPath);

        File dpath = new File(dirPath);

        String pathPrefix = dpath.getAbsolutePath().substring(0, (int) (dpath.getAbsolutePath().length() - dirPath.length()));

        String fname = "pos-tags";

        if(ptype.equals("POSTags"))
        {
            fname = "pos-tags";
        }
        else if(ptype.equals("PhraseNames"))
        {
            fname = "phrase-names";
        }
        else if(ptype.equals("POSTagsLevels"))
        {
            fname = "pos-tags-levels";
        }
        else if(ptype.equals("PhraseNamesLevels"))
        {
            fname = "phrase-names-levels";
        }

        String ext = ".txt";
        String lang = SanchayLanguages.getLanguageCodeFromLECode(langEnc);

        File pfile = new File(dirPath, fname + "-" + lang + ext);

        String rpath = "";

        if(pfile.canRead())
        {
            rpath = pfile.getAbsolutePath();
        }
        else
        {
            pfile = new File(dirPath, fname + ext);
            rpath = pfile.getAbsolutePath();
        }

        if(isRelativePath)
        {
            rpath = rpath.substring(pathPrefix.length(), rpath.length());
        }

        return rpath;
    }

    public static void init()
    {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

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
        }  catch (FileNotFoundException e) {
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

    @Override
    public DOMElement getDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();
        
        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("nodeTag"));

        DOMElement idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("nodeIDTag"));
        idomElement.setText(id);
        domElement.add(idomElement);

        idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("nameTag"));
        idomElement.setText(getName());
        domElement.add(idomElement);

        idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("lexDataTag"));
        idomElement.setText(getLexData());
        domElement.add(idomElement);

        if(fs != null)
        {
            idomElement = ((SanchayDOMElement) fs).getDOMElement();
            domElement.add(idomElement);
        }

        if(getComment() != null)
        {
            idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("commentTag"));
            idomElement.setText(getComment());
            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public DOMElement getTypeCraftDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcMorphemeTag"));

        DOMElement idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcGlossTag"));
        idomElement.setText(getLexData());
        domElement.add(idomElement);

        if(fs != null)
        {
            idomElement = ((SanchayDOMElement) fs).getDOMElement();
            domElement.add(idomElement);
        }

        if(getComment() != null)
        {
            idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("commentTag"));
            idomElement.setText(getComment());
            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public DOMElement getGATEDOMElement() {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        DOMElement domElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcMorphemeTag"));

        DOMElement idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("tcGlossTag"));
        idomElement.setText(getLexData());
        domElement.add(idomElement);

        if(fs != null)
        {
            idomElement = ((SanchayDOMElement) fs).getDOMElement();
            domElement.add(idomElement);
        }

        if(getComment() != null)
        {
            idomElement = new DOMElement(xmlProperties.getProperties().getPropertyValue("commentTag"));
            idomElement.setText(getComment());
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

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("nodeIDTag")))
                {
                    id = element.getTextContent();
                    id = id.trim();
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("lexDataTag")))
                {
                    String lexdata = element.getTextContent();
                    lexdata = lexdata.trim();
                    setLexData(lexdata);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("nameTag")))
                {
                    String name = element.getTextContent();
                    name = name.trim();
                    setName(name);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("fssTag")))
                {
                    fs = new FeatureStructuresImpl();
                    ((SanchayDOMElement) fs).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("commentTag")))
                {
                    String comment = element.getTextContent();
                    comment = comment.trim();
                    setComment(comment);
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readTypeCraftXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        NamedNodeMap domAttribs = domElement.getAttributes();

        int acount = domAttribs.getLength();

        for (int i = 0; i < acount; i++)
        {
            Node node = domAttribs.item(i);
            String name = node.getNodeName();
            String value = node.getNodeValue();

            if(name != null)
            {
                if(name.equals("text"))
                {
                    setLexData(value);
                }
                else if(value != null) {
                    setAttributeValue(name, value);
                }
                else {
                    setAttributeValue(name, "");
                }
            }
        }

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("tcGlossTag")))
                {
                    String name = element.getTextContent();
                    name = name.trim();
                    setName(name);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("lexDataTag")))
                {
                    String lexdata = element.getTextContent();
                    lexdata = lexdata.trim();
                    setLexData(lexdata);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("nameTag")))
                {
                    String name = element.getTextContent();
                    name = name.trim();
                    setName(name);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("fssTag")))
                {
                    fs = new FeatureStructuresImpl();
                    ((SanchayDOMElement) fs).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("commentTag")))
                {
                    String comment = element.getTextContent();
                    comment = comment.trim();
                    setComment(comment);
                }
            }

            String name = getName();
            
            if(name == null || name.equals("")) {
                setName("NO_GLOSS");
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readGATEXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        NamedNodeMap domAttribs = domElement.getAttributes();

        int acount = domAttribs.getLength();

        for (int i = 0; i < acount; i++)
        {
            Node node = domAttribs.item(i);
            String name = node.getNodeName();
            String value = node.getNodeValue();

            if(name != null)
            {
                if(name.equals("text"))
                {
                    setLexData(value);
                }
                else if(value != null) {
                    setAttributeValue(name, value);
                }
                else {
                    setAttributeValue(name, "");
                }
            }
        }

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("tcGlossTag")))
                {
                    String name = element.getTextContent();
                    name = name.trim();
                    setName(name);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("lexDataTag")))
                {
                    String lexdata = element.getTextContent();
                    lexdata = lexdata.trim();
                    setLexData(lexdata);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("nameTag")))
                {
                    String name = element.getTextContent();
                    name = name.trim();
                    setName(name);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("fssTag")))
                {
                    fs = new FeatureStructuresImpl();
                    ((SanchayDOMElement) fs).readXML(element);
                }
                else if(element.getTagName().equals(xmlProperties.getProperties().getPropertyValue("commentTag")))
                {
                    String comment = element.getTextContent();
                    comment = comment.trim();
                    setComment(comment);
                }
            }

            String name = getName();
            
            if(name == null || name.equals("")) {
                setName("NO_GLOSS");
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void printXML(PrintStream ps) {
        ps.print(getXML());
    }

    @Override
    public void printTypeCraftXML(PrintStream ps) {
        ps.print(getTypeCraftXML());
    }

    @Override
    public void printGATEXML(PrintStream ps) {
        ps.print(getGATEXML());
    }

    public boolean hasLexicalDependencies(String relAttrib)
    {
        String relVal = getAttributeValue(relAttrib);

        if(relVal != null && relVal.equals("") == false)
        {
            String parts[] = relVal.split(":");

            if(parts.length == 2) {
                return true;
            }
        }

        int ccount = countChildren();

        for (int i = 0; i < ccount; i++) {
            SSFNode cnode = (SSFNode) getChildAt(i);

            if(cnode.hasLexicalDependencies(relAttrib) == true) {
                return true;
            }
        }

        return false;
    }

    public boolean hasContituentDependencies(String relAttrib)
    {
        if(this instanceof SSFLexItem) {
            return false;
        }

        String relVal = getAttributeValue(relAttrib);

        if(relVal == null || relVal.equals("")) {
            return false;
        }

        String parts[] = relVal.split(":");

        if(parts.length == 2) {
            return true;
        }

        int ccount = countChildren();

        for (int i = 0; i < ccount; i++) {
            SSFNode cnode = (SSFNode) getChildAt(i);

            if(cnode.hasContituentDependencies(relAttrib) == true) {
                return true;
            }
        }

        return false;
    }

    public List<SSFNode> getReferringNodes(SSFNode referredNode, int mode)
    {
        String attribs[] = null;

        if (mode == PHRASE_STRUCTURE_MODE)
        {
            attribs = FSProperties.getPSAttributes();
        } else if (mode == DEPENDENCY_RELATIONS_MODE)
        {
            attribs = FSProperties.getDependencyAttributes();
        }

        String nm = referredNode.getAttributeValue("name");

        List<SSFNode> refNodes = getNodesForOneOfAttribs(attribs, true);
        List<SSFNode> referringNodes = new ArrayList<SSFNode>();

        int count = refNodes.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode refNode = (SSFNode) refNodes.get(i);

            String refAtVal[] = refNode.getOneOfAttributeValues(attribs);

            if (refAtVal == null)
            {
                continue;
            }

            String refVal = refAtVal[1];

            if (refVal == null)
            {
                continue;
            }

            String parts[] = refVal.split(":");

//            if (parts.length == 1)
//            {
//                continue;
//            }

            if ((parts.length == 2 && parts[1].equals(nm)) || (parts.length == 1 && parts[0].equals(nm)))
            {
                referringNodes.add(refNode);
            }
        }

        return referringNodes;
    }

    // For dependency
    public void setReferredName(String name, int mode)
    {
        String attribs[] = null;

        if (mode == PHRASE_STRUCTURE_MODE)
        {
            attribs = FSProperties.getPSAttributes();
        } else if (mode == DEPENDENCY_RELATIONS_MODE)
        {
            attribs = FSProperties.getDependencyAttributes();
        }

        String refAtVal[] = getOneOfAttributeValues(attribs);

        if(refAtVal == null) {
            return;
        }

        String refVal = refAtVal[1];

        String parts[] = refVal.split(":");

        if(parts.length == 1) {
            setAttributeValue(refAtVal[0], name);
        }
        else {
            setAttributeValue(refAtVal[0], parts[0] + ":" + name);
        }
    }

    public SSFNode getReferredNode(String attribName)
    {
        String attribVal = getAttributeValue(attribName);

        if(attribVal == null || !attribVal.contains(":")) {
            return null;
        }

        String attribValParts[] = attribVal.split(":");

        String referredName = attribValParts[1];

        return ((SSFPhrase) getRoot()).getNodeForAttribVal("name", referredName, true);
    }

    public List<SSFNode> getNodesForOneOfAttribs(String attribs[], boolean exactMatch)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        FeatureStructures fss = getFeatureStructures();

        if (fss != null && fss.countAltFSValues() > 0)
        {
            if (fss.getAltFSValue(0).searchOneOfAttributes(attribs, exactMatch) != null)
            {
                nodes.add(this);
            }
        }

        int count = getChildCount();
        
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) getChildAt(i);

            fss = node.getFeatureStructures();

            if (fss != null && fss.countAltFSValues() > 0)
            {
                if (fss.getAltFSValue(0).searchOneOfAttributes(attribs, exactMatch) != null)
                {
                    nodes.add(node);
                }
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) node).getNodesForOneOfAttribs(attribs, exactMatch));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    @Override
    public void fillTreeEdges(SanchayJTable jtbl, int mode)
    {
//	if(requiredColumnCount == -1 || rowIndex == -1 || columnIndex == -1)
//	    return;

        String atrrNames[] = null;

        switch (mode)
        {
            case PHRASE_STRUCTURE_MODE:
                if(getParent() == null) {
                    jtbl.setCellObject(getRowIndex(), getColumnIndex(), this);
                }

                atrrNames = FSProperties.getPSTreeAttributes();
                int chcount = getChildCount();

                for (int i = 0; i < chcount; i++)
                {
                    SSFNode child = (SSFNode) getChildAt(i);

                    // Skipping the root SSF node
                    if (rowIndex >= 0)
                    {
                        SanchayEdge edge = new SanchayEdge(this, rowIndex, columnIndex, child, child.getRowIndex(), child.getColumnIndex());

                        edge.isTriangle(child.isTriangle);

                        if (child.getFeatureStructures() != null && child.getFeatureStructures().countAltFSValues() > 0 && child.getFeatureStructures().getAltFSValue(0) != null)
                        {
                            FeatureAttribute fa = child.getFeatureStructures().getAltFSValue(0).getOneOfAttributes(atrrNames);

                            if (fa != null)
                            {
                                String prel = (String) child.getFeatureStructures().getAltFSValue(0).getOneOfAttributes(atrrNames).getAltValue(0).getValue();
                                prel = prel.split("[:]")[0];
                                edge.setLabel(prel.toUpperCase());
                                Color color = UtilityFunctions.getColor(FSProperties.getPSTreeAttributeProperties(fa.getName())[0]);
                                edge.setColor(color);
                                Stroke stroke = UtilityFunctions.getStroke(FSProperties.getPSTreeAttributeProperties(fa.getName())[1]);
                                edge.setStroke(stroke);
                            }
                        }

                        jtbl.addEdge(edge);
                    }

                    jtbl.setCellObject(child.getRowIndex(), child.getColumnIndex(), child);
                    child.fillTreeEdges(jtbl, mode);
                }

                break;
            case DEPENDENCY_RELATIONS_MODE:
                atrrNames = FSProperties.getDependencyTreeAttributes();

                if (isLeaf() == false || jtbl.allowsLeafDependencies())
                {
                    chcount = getChildCount();

                    for (int i = 0; i < chcount; i++)
                    {
                        SSFNode child = (SSFNode) getChildAt(i);

                        // Skipping the root SSF node
                        if (rowIndex >= 0)
                        {
                            SanchayEdge edge = new SanchayEdge(this, rowIndex, columnIndex, child, child.getRowIndex(), child.getColumnIndex());

                            if (child.getFeatureStructures() != null && child.getFeatureStructures().countAltFSValues() > 0 && child.getFeatureStructures().getAltFSValue(0) != null)
                            {
                                FeatureAttribute fa = child.getFeatureStructures().getAltFSValue(0).getOneOfAttributes(atrrNames);

                                if (fa != null)
                                {
                                    String drel = (String) child.getFeatureStructures().getAltFSValue(0).getOneOfAttributes(atrrNames).getAltValue(0).getValue();
                                    drel = drel.split("[:]")[0];
                                    edge.setLabel(drel.toUpperCase());
                                    Color color = UtilityFunctions.getColor(FSProperties.getDependencyTreeAttributeProperties(fa.getName())[0]);
                                    edge.setColor(color);
                                    Stroke stroke = UtilityFunctions.getStroke(FSProperties.getDependencyTreeAttributeProperties(fa.getName())[1]);
                                    edge.setStroke(stroke);
                                }
                            }

                            jtbl.addEdge(edge);
                        }

                        jtbl.setCellObject(child.getRowIndex(), child.getColumnIndex(), child);
                        child.fillTreeEdges(jtbl, mode);
                    }
                }

                break;
            default:
                super.fillTreeEdges(jtbl, mode);
        }

    }

    @Override
    public void fillGraphEdges(SanchayJTable jtbl, int mode)
    {
        String atrrNames[] = null;

        switch (mode)
        {
            case PHRASE_STRUCTURE_MODE:
                atrrNames = FSProperties.getPSGraphAttributes();

                int mcount = getChildCount();

                for (int i = 0; i < mcount; i++)
                {
                    SSFNode mnode = (SSFNode) getChildAt(i);

                    mnode.fillGraphEdges(jtbl, mode);

                    String refAtVal[] = mnode.getOneOfAttributeValues(atrrNames);

                    if (refAtVal == null)
                    {
                        continue;
                    }

                    String refVal = refAtVal[1];

                    if (refVal == null)
                    {
                        continue;
                    }

                    String parts[] = refVal.split(":");

//                    if (parts[1] == null)
//                    {
//                        continue;
//                    }

                    String rel = "";
                    String nm = "";

                    if(parts.length == 1)
                    {
                        nm = parts[0];
                    }
                    else if(parts.length == 1)
                    {
                        rel = parts[0];
                        nm = parts[1];
                    }

                    SSFNode referredNode = ((SSFPhrase) getRoot()).getNodeForAttribVal("name", nm, true);

                    if (referredNode != null && referredNode.getRowIndex() >= 0)
                    {
                        SanchayEdge edge = new SanchayEdge(this, referredNode.getRowIndex(), referredNode.getColumnIndex(), mnode, mnode.getRowIndex(), mnode.getColumnIndex());

                        String prel = rel;
                        edge.setLabel(prel.toUpperCase());
                        Color color = UtilityFunctions.getColor(FSProperties.getPSGraphAttributeProperties(refAtVal[0])[0]);
                        edge.setColor(color);
                        Stroke stroke = UtilityFunctions.getStroke(FSProperties.getPSGraphAttributeProperties(refAtVal[0])[1]);
                        edge.setStroke(stroke);
                        edge.isCurved(true);

                        jtbl.addEdge(edge);
                    }

                    jtbl.setCellObject(mnode.getRowIndex(), mnode.getColumnIndex(), mnode);
                }

                break;
            case DEPENDENCY_RELATIONS_MODE:

                atrrNames = FSProperties.getDependencyGraphAttributes();

                if (isLeaf() == false || jtbl.allowsLeafDependencies())
                {
                    mcount = getChildCount();

                    for (int i = 0; i < mcount; i++)
                    {
                        SSFNode mnode = (SSFNode) getChildAt(i);

                        if (mnode instanceof SSFPhrase || jtbl.allowsLeafDependencies())
                        {
                            mnode.fillGraphEdges(jtbl, mode);

                            String refAtVal[] = mnode.getOneOfAttributeValues(atrrNames);

                            if (refAtVal == null)
                            {
                                continue;
                            }

                            String refVal = refAtVal[1];

                            if (refVal == null)
                            {
                                continue;
                            }

                            String parts[] = refVal.split(":");

                            String rel = "";
                            String nm = "";

                            if (parts.length == 1)
                            {
                                nm = parts[0];
                            }
                            else if(parts.length == 2)
                            {
                                rel = parts[0];
                                nm = parts[1];
                            }

                            SSFNode referredNode = ((SSFPhrase) getRoot()).getNodeForAttribVal("name", nm, true);

                            if (referredNode != null && referredNode.getRowIndex() >= 0)
                            {
                                SanchayEdge edge = new SanchayEdge(this, referredNode.getRowIndex(), referredNode.getColumnIndex(), mnode, mnode.getRowIndex(), mnode.getColumnIndex());

                                String drel = rel;
                                edge.setLabel(drel.toUpperCase());
                                Color color = UtilityFunctions.getColor(FSProperties.getDependencyGraphAttributeProperties(refAtVal[0])[0]);
                                edge.setColor(color);
                                Stroke stroke = UtilityFunctions.getStroke(FSProperties.getDependencyGraphAttributeProperties(refAtVal[0])[1]);
                                edge.setStroke(stroke);
                                edge.isCurved(true);

                                jtbl.addEdge(edge);
                            }

                            jtbl.setCellObject(mnode.getRowIndex(), mnode.getColumnIndex(), mnode);
                        }
                    }
                }

                break;

            default:
                super.fillGraphEdges(jtbl, mode);
        }
    }
    
    public static Index getCurrentVocabulary()
    {
        return UtilityFunctions.getCopy(vocabIndex);
    }
    
    public static Index getCurrentTagVocabulary()
    {
        return UtilityFunctions.getCopy(tagIndex);
    }
}
