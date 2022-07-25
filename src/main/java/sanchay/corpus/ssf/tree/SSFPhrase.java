package sanchay.corpus.ssf.tree;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import sanchay.corpus.ssf.SSFProperties;
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
import sanchay.corpus.xml.XMLProperties;
import sanchay.text.enc.conv.SanchayEncodingConverter;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.UtilityFunctions;
import sanchay.util.query.FindReplace;
import sanchay.util.query.FindReplaceOptions;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.GATEDOMElement;
import sanchay.xml.dom.SanchayDOMElement;
import sanchay.xml.dom.TypeCraftDOMElement;

public class SSFPhrase extends SSFNode
        implements MutableTreeNode, Alignable, Serializable, QueryValue,
        SanchayDOMElement, TypeCraftDOMElement, GATEDOMElement
{

    // Children could be of type SSFPhrase or SSFLexicalItem

    public SSFPhrase()
    {
        super();
    }

    public SSFPhrase(Object userObject)
    {
        super(userObject);
    }

    public SSFPhrase(Object userObject, boolean allowsChildren)
    {
        super(userObject, allowsChildren);
    }

    public SSFPhrase(String id, String lexdata, String name, String stringFS) throws Exception
    {
        super(id, lexdata, name, stringFS);
    }

    public SSFPhrase(String id, String lexdata, String name, FeatureStructures fs)
    {
        super(id, lexdata, name, fs);
    }

    public SSFPhrase(String id, String lexdata, String name, String stringFS, Object userObject) throws Exception
    {
        super(id, lexdata, name, stringFS, userObject);
    }

    public SSFPhrase(String id, String lexdata, String name, FeatureStructures fs, Object userObject)
    {
        super(id, lexdata, name, fs, userObject);
    }

    @Override
    public int countChildren()
    {
        return getChildCount();
    }

    public int addChild(SSFNode c)
    {
        add((SSFNode) c);
        return getChildCount();
    }

    public int addChildren(Collection c)
    {
        Object ca[] = c.toArray();

        for (int i = 0; i < ca.length; i++)
        {
            add((SSFNode) ca[i]);
        }

        return getChildCount();
    }

    public int addChildAt(SSFNode c, int index)
    {
        insert(c, index);
        return getChildCount();
    }

    public int addChildrenAt(Collection c, int index)
    {
        Object ca[] = c.toArray();

        for (int i = 0; i < ca.length; i++)
        {
            insert((SSFNode) ca[i], index + i);
        }

        return getChildCount();
    }

    public SSFNode getChild(int index)
    {
        return (SSFNode) getChildAt(index);
    }

    public int findChild(SSFNode aChild)
    {
        // Needed because getIndex relies on equals(), but we need to check references

        if (aChild == null)
        {
            throw new IllegalArgumentException(GlobalProperties.getIntlString("argument_is_null"));
        }

        if (!isNodeChild(aChild))
        {
            return -1;
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            if (getChild(i) == aChild)
            {
                return i;
            }
        }

        return -1;
    }

    public SSFNode findLeafByID(String id)
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int count = leaves.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) leaves.get(i);

            if(node.getId().equals(id)) {
                return node;
            }
        }

        return null;
    }

    public int findLeafIndexByID(String id)
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int count = leaves.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) leaves.get(i);

            if(node.getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public SSFNode findChildByID(String id)
    {
        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            if(node.getId().equals(id)) {
                return node;
            }
        }

        return null;
    }

    public int findChildIndexByID(String id)
    {
        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            if(node.getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public SSFNode findNodeByID(String id)
    {
        if(getId().equals(id)) {
            return this;
        }

        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            if(node instanceof SSFLexItem)
            {
                if(node.getId().equals(id)) {
                    return node;
                }
            }
            else if (node instanceof SSFPhrase) {
                return ((SSFPhrase) node).findNodeByID(id);
            }
        }

        return null;
    }

    public int findNodeIndexByID(String id)
    {
        if(getId().equals(id))
        {
            SSFPhrase prnt = (SSFPhrase) getParent();

            if(prnt == null) {
                return 0;
            }
            else {
                return prnt.findChild(this);
            }
        }

        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            if(node instanceof SSFLexItem)
            {
                if(node.getId().equals(id)) {
                    return i;
                }
            }
            else if (node instanceof SSFPhrase) {
                return ((SSFPhrase) node).findNodeIndexByID(id);
            }
        }

        return -1;
    }

    public SSFNode findLeafByName(String n)
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int count = leaves.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) leaves.get(i);

            String nodeName = node.getAttributeValue("name");

            if(nodeName != null && nodeName.equals(n)) {
                return node;
            }
        }

        return null;
    }

    public int findLeafIndexByName(String n)
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int count = leaves.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) leaves.get(i);

            String nodeName = node.getAttributeValue("name");

            if(nodeName != null && nodeName.equals(n)) {
                return i;
            }
        }

        return -1;
    }

    public SSFNode findChildByName(String n)
    {
        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            String nodeName = node.getAttributeValue("name");

            if(nodeName != null && nodeName.equals(n)) {
                return node;
            }
        }

        return null;
    }

    public int findChildIndexByName(String n)
    {
        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            String nodeName = node.getAttributeValue("name");

            if(nodeName != null && nodeName.equals(n)) {
                return i;
            }
        }

        return -1;
    }

    public SSFNode findNodeByName(String n)
    {
        String nodeName = getAttributeValue("name");

        if(nodeName != null && nodeName.equals(n)) {
            return this;
        }

        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            if(node instanceof SSFLexItem)
            {
                nodeName = node.getAttributeValue("name");

                if(nodeName != null && nodeName.equals(n)) {
                    return node;
                }
            }
            else if (node instanceof SSFPhrase) {
                return ((SSFPhrase) node).findNodeByName(n);
            }
        }

        return null;
    }

    public int findNodeIndexByName(String n)
    {
        String nodeName = getAttributeValue("name");

        if(nodeName != null && nodeName.equals(n))
        {
            SSFPhrase prnt = (SSFPhrase) getParent();

            if(prnt == null) {
                return 0;
            }
            else {
                return prnt.findChild(this);
            }
        }

        List<SSFNode> allChildren = getAllChildren();

        int count = allChildren.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = allChildren.get(i);

            if(node instanceof SSFLexItem)
            {
                nodeName = node.getAttributeValue("name");

                if(nodeName != null && nodeName.equals(n)) {
                    return i;
                }
            }
            else if (node instanceof SSFPhrase) {
                return ((SSFPhrase) node).findNodeIndexByName(n);
            }
        }

        return -1;
    }

    public List<SSFNode> getChildren(int from, int count)
    {
        List<SSFNode> ret = new ArrayList<SSFNode>(count);

        for (int i = 0; i < count; i++)
        {
            ret.add(getChild(from + i));
        }

        return ret;
    }

    public List<SSFNode> getAllChildren()
    {
        List<SSFNode> ret = new ArrayList<SSFNode>();

        ret.addAll(getChildren(0, countChildren()));
        
        return ret;
    }

    public void modifyChild(SSFNode c, int index)
    {
        insert(c, index);
        remove(index + 1);
    }

    public SSFNode removeChild(int index)
    {
        SSFNode rem = getChild(index);
        remove(index);

        return rem;
    }

    public void removeChildren(int index, int count)
    {
        for (int i = 0; i < count; i++)
        {
            removeChild(index);
        }
    }

    @Override
    public void removeAllChildren()
    {
        removeChildren(0, getChildCount());
    }

    @Override
    public void removeAttribute(String aname)
    {
        if(fs != null && fs.countAltFSValues() > 0)
        {
            FeatureStructure tfs = fs.getAltFSValue(0);
            tfs.removeAttribute(aname);
        }

        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            SSFNode child = getChild(i);
            child.removeAttribute(aname);
        }
    }

    @Override
    public void hideAttribute(String aname)
    {
        super.hideAttribute(aname);

        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            SSFNode child = getChild(i);
            child.hideAttribute(aname);
        }
    }

    @Override
    public void unhideAttribute(String aname)
    {
        super.unhideAttribute(aname);

        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            SSFNode child = getChild(i);
            child.unhideAttribute(aname);
        }
    }

    @Override
    public void removeEmptyPhrases()
    {
        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            if (node instanceof SSFPhrase)
            {
                if (node.countChildren() == 0)
                {
                    removeChild(i--);
                    count--;
                }

                node.removeEmptyPhrases();
            }
        }
    }

    @Override
    public void removeNonChunkPhrases()
    {
        if(isDSRedundantPhrase()) {
            removeLayer();
        }

        int count = countChildren();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) getChild(i);
            node.removeDSRedundantPhrases();
        }
    }

    @Override
    public boolean isNonChunkPhrase()
    {
        if(getDepth() > 1) {
            return true;
        }

        return false;
    }

    @Override
    public void removeDSRedundantPhrases()
    {
        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            if (node instanceof SSFPhrase)
            {
                if (node.isDSRedundantPhrase())
                {
                    removeChild(i--);
                    count--;
                }

                node.removeDSRedundantPhrases();
            }
        }
    }

    @Override
    public boolean isDSRedundantPhrase()
    {
        String drelAttribs[] = FSProperties.getDependencyTreeAttributes();

        boolean hasDSRedundantPhrases = true;

        int count = countChildren();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) getChild(i);

            if(node.getOneOfAttributeValues(drelAttribs) == null)
            {
                hasDSRedundantPhrases = false;
                break;
            }
        }

        return hasDSRedundantPhrases;
    }

    @Override
    public boolean isLeafNode()
    {
        return false;
    }

    public SSFNode getPrevious(SSFNode child)
    {
        return (SSFNode) getChildBefore(child);
    }

    public SSFNode getNext(SSFNode child)
    {
        return (SSFNode) getChildAfter(child);
    }

    public boolean hasLexItemChild()
    {
        int ccount = countChildren();

        for (int i = 0; i < ccount; i++)
        {
            if (getChild(i) instanceof SSFLexItem)
            {
                return true;
            }
        }

        return false;
    }

    public void concat(SSFPhrase ph)
    {
        int count = ph.countChildren();
        for (int i = 0; i < count; i++)
        {
            SSFNode n = ph.removeChild(0);
            addChild(n);
        }
//
//	Vector chvec = getAllChildren();
//	Vector nextChvec = ph.getAllChildren();
//
//	ph.removeAllChildren();
//	addChildren(nextChvec);
    }

    public SSFPhrase splitPhrase(int childIndex) throws Exception
    {
        SSFPhrase splitNode;
        
        splitNode = (SSFPhrase) getCopy();

        int count = getChildCount();
        for (int i = childIndex; i < count; i++)
        {
            removeChild(childIndex);
        }

        for (int i = 0; i < childIndex; i++)
        {
            splitNode.removeChild(0);
        }

        return splitNode;
    }

// Moved to SanchayMutableTreeNode
//    public Vector getAllLeaves() // &get_leaves( [$tree] )  -> @leaf_nodes;
    @Override
    public SSFNode getNodeForId(String id)
    {
        if (this.id.equalsIgnoreCase(id))
        {
            return this;
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            if (node.isLeafNode() && node.getId().equalsIgnoreCase(id))
            {
                return node;
            } else
            {
                SSFNode ret = ((SSFNode) getChild(i)).getNodeForId(id);

                if (ret != null)
                {
                    return ret;
                }
            }
        }

        return null;
    }

    public List<SSFNode> getNodesForName(String n)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        Pattern p = Pattern.compile(n);
//        Pattern p = Pattern.compile(n, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        Matcher m = p.matcher(getName());

        if (m.find()) {
            nodes.add(this);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);
    	    m = p.matcher(node.getName());

            if (m.find())
            {
                nodes.add(node);
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) getChild(i)).getNodesForName(n));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> getNodesForLexData(String ld)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        Pattern p = Pattern.compile(ld);
//        Pattern p = Pattern.compile(ld, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        Matcher m = p.matcher(getLexData());

        if (m.find()) {
            nodes.add(this);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

    	    m = p.matcher(node.getLexData());

            if (m.find())
            {
                nodes.add(node);
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) getChild(i)).getNodesForLexData(ld));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> getNodesForText(String ld)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

//        Pattern p = Pattern.compile(ld, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);
        Pattern p = Pattern.compile(ld);

        Matcher m = p.matcher(makeRawSentence());

        if (m.find()) {
            nodes.add(this);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

    	    m = p.matcher(node.makeRawSentence());

            if (m.find())
            {
                nodes.add(node);
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) getChild(i)).getNodesForText(ld));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List replaceLabelForText(String ld, String replace)
    {
        List nodes = new ArrayList<SSFNode>();

        Pattern p = Pattern.compile(ld);
//        Pattern p = Pattern.compile(ld, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES);

        Matcher m = p.matcher(makeRawSentence());

        if (m.find())
        {
            setName(replace);
            nodes.add(this);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

    	    m = p.matcher(node.makeRawSentence());

            if (m.find())
            {
                node.setName(replace);
                nodes.add(node);
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) getChild(i)).replaceLabelForText(ld, replace));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> getNodesForFS(String fss)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            if (node.getFeatureStructures().makeString().equalsIgnoreCase(fss))
            {
                nodes.add(node);
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) getChild(i)).getNodesForFS(fss));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> getNodesForAttrib(String attrib, boolean exactMatch)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        FeatureStructures fss = getFeatureStructures();

        if (fss != null && fss.countAltFSValues() > 0)
        {
            if (fss.getAltFSValue(0).searchAttribute(attrib, exactMatch) != null)
            {
                nodes.add(this);
            }
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            fss = node.getFeatureStructures();

            if (fss != null && fss.countAltFSValues() > 0)
            {
                if (fss.getAltFSValue(0).searchAttribute(attrib, exactMatch) != null)
                {
                    nodes.add(node);
                }
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) node).getNodesForAttrib(attrib, exactMatch));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public SSFNode getNodeForAttribVal(String attrib, String val, boolean exactMatch)
    {
        List nodes = getNodesForAttribVal(attrib, val, exactMatch);

        if (nodes == null || nodes.size() <= 0)
        {
            return null;
        }

        return (SSFNode) nodes.get(0);
    }

    public List<SSFNode> getNodesForAttribVal(String attrib, String val, boolean exactMatch)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        FeatureStructures fss = getFeatureStructures();

        if (fss != null && fss.countAltFSValues() > 0)
        {
            if (fss.getAltFSValue(0).searchAttributeValue(attrib, val, exactMatch) != null)
            {
                nodes.add(this);
            }
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            fss = node.getFeatureStructures();

            if (fss != null && fss.countAltFSValues() > 0)
            {
                if (fss.getAltFSValue(0).searchAttributeValue(attrib, val, exactMatch) != null)
                {
                    nodes.add(node);
                }
            }

            if (node.isLeafNode() == false)
            {
                nodes.addAll(((SSFPhrase) node).getNodesForAttribVal(attrib, val, exactMatch));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> replaceAttribValForText(String attrib, String val, String ntext, String attribReplace, String valReplace)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            if (node.makeRawSentence().equalsIgnoreCase(ntext))
            {
                FeatureStructures fss = node.getFeatureStructures();

                if (fss != null && fss.countAltFSValues() > 0)
                {
                    FeatureStructure ifs = fss.getAltFSValue(0);

                    ifs.replaceAttributeValues(attrib, val, attribReplace, valReplace);
                    nodes.add(node);
                } else
                {
                    fss = new FeatureStructuresImpl();
                    FeatureStructure ifs = new FeatureStructureImpl();

                    FeatureAttribute fa = new FeatureAttributeImpl();
                    fa.setName(attribReplace);

                    FeatureValue fv = new FeatureValueImpl();
                    fv.setValue(valReplace);

                    fss.addAltFSValue(ifs);
                }

            }

            if (node instanceof SSFPhrase)
            {
                nodes.addAll(((SSFPhrase) node).replaceAttribValForText(attrib, val, ntext, attribReplace, valReplace));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> replaceAttribValForLabel(String attrib, String val, String nlabel, String attribReplace, String valReplace, boolean createAttrib)
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            if (node.getName().equalsIgnoreCase(nlabel))
            {
                FeatureStructures fss = node.getFeatureStructures();

                if (fss != null && fss.countAltFSValues() > 0)
                {
                    FeatureStructure ifs = fss.getAltFSValue(0);

                    FeatureAttribute fa = ifs.getAttribute(attrib);

                    if(createAttrib && fa == null)
                    {
                        if(FeatureStructuresImpl.getFSProperties().isMandatory(attrib))
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
            }

            if (node instanceof SSFPhrase)
            {
                nodes.addAll(((SSFPhrase) node).replaceAttribValForLabel(attrib, val, nlabel, attribReplace, valReplace, createAttrib));
            }
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public List<SSFNode> getNodesForValue(int fieldnumber/* 1 to 4 */, String val)
    /* for the 4th field, otherwise null */ // &get_nodes( $fieldnumber , $value , [$tree] ) -> @required_nodes
    {
        List<SSFNode> nodes = new ArrayList<SSFNode>();

        switch (fieldnumber)
        {
            case 1:
                nodes.add(getNodeForId(val));
            case 2:
                nodes.addAll(getNodesForName(val));
            case 3:
                nodes.addAll(getNodesForLexData(val));
            case 4:
                nodes.addAll(getNodesForFS(val));
            default:
                nodes.add(getNodeForId(val));
        }

        nodes = (List) UtilityFunctions.getUnique(nodes);

        return nodes;
    }

    public int formPhrase(int fromChild, int count) throws Exception
    {
        int ret = -1;

        int childCount = getChildCount();

        if (fromChild >= 0 && fromChild < childCount && (fromChild + count) <= childCount)
        {
            SSFProperties ssfp = SSFNode.getSSFProperties();
            String chunkStart = ssfp.getProperties().getPropertyValue("chunkStart");

//            SSFPhrase ssfph = new SSFPhrase("0", chunkStart, "NP", "");
            SSFPhrase ssfph = new SSFPhrase("0", "", "NP", "");

            List<SSFNode> ch = getChildren(fromChild, count);
            insert(ssfph, fromChild);
            removeChildren(fromChild + 1, count);
            ssfph.addChildren(ch);

            ssfph.reallocateId(getId());

            ssfph.getFeatureStructures().setToEmpty();

            clearAlignments(fromChild, count);
        } else
        {
            return ret;
        }

        return ret;
    }

    public void clearAlignments(int fromChild, int count)
    {
        int childCount = getChildCount();

        if (fromChild >= 0 && fromChild < childCount && (fromChild + count) <= childCount)
        {
            for (int i = fromChild; i < fromChild + count; i++)
            {
                SSFNode node = getChild(i);

                AlignmentUnit aunit = node.getAlignmentUnit();

                if(aunit != null) {
                    node.getAlignmentUnit().clearAlignments();
                }
            }
        }
    }

    public void readFile(String f, String charset) throws Exception
    {
        // Perhaps not needed. Use the method in SSFSentenceImpl
//        BufferedReader lnReader = null;
//
//        if(!charset.equals(""))
//            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
//        else
//            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
//
//        String line = "";
//        String lines = "";
//
//        while((line = lnReader.readLine()) != null)
//        {
//            if((!line.startsWith("#")) && line.equals("") == false)
//            {
//                lines += line + "\n";
//            }
//        }
//
//        readString(lines);
    }

    public int readString(String string, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        List<SSFNode> nodes = SSFPhrase.readNodesFromString(string, errorLog, lineNum);

        if (nodes == null || nodes.size() < 1 || nodes.get(0) == null)
        {
            throw new Exception();
        }

        if (nodes != null && nodes.size() > 0)
        {
            if (nodes.size() == 1 && nodes.get(0).getClass().equals(SSFPhrase.class) && ((SSFNode) nodes.get(0)).getName().equals(rootName))
            {
                fillSSFData((SSFNode) nodes.get(0));
            } else
            {
                for (int j = 0; j < nodes.size(); j++)
                {
                    addChild(((SSFNode) nodes.get(j)));
                }
            }
        }

        removeEmptyPhrases();

        return getChildCount();
    }

    @Override
    public int readString(String string) throws Exception
    {
        return readString(string, null, 1);
    }

    public int readChunkedString(String string, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        List<SSFNode> nodes = SSFPhrase.readNodesFromChunked(string, errorLog, lineNum);

        if (nodes == null || nodes.size() < 1 || nodes.get(0) == null)
        {
            throw new Exception();
        }

        if (nodes != null && nodes.size() > 0)
        {
            if (nodes.size() == 1 && nodes.get(0).getClass().equals(SSFPhrase.class) && ((SSFNode) nodes.get(0)).getName().equals(rootName))
            {
                fillSSFData((SSFNode) nodes.get(0));
            } else
            {
                for (int j = 0; j < nodes.size(); j++)
                {
                    addChild(((SSFNode) nodes.get(j)));
                }
            }
        }

        removeEmptyPhrases();

        return getChildCount();
    }

    public int readChunkedString(String string) throws Exception
    {
        return readChunkedString(string, null, 1);
    }

    @Override
    public void fillSSFData(SSFNode n)
    {
        if (n.isLeaf())
        {
            System.out.println("Error in SSFPhrase.fillSSFData: Leaf node given as arguement.");
            return;
        }
        if (n == null)
        {
            System.out.println("Error in SSFPhrase.fillSSFData: Null node given as arguement.");
            return;
        }

        super.fillSSFData(n);

        int count = n.getChildCount();
        while (count > 0)
        {
            add((SSFNode) n.getChildAt(0));
            count--;
        }
    }

    // Implementation parallel to the readNodesFromString method
    public static boolean validateSSF(String string, List<String> errorLog /*Strings*/, int lineNum)
    {
        boolean validated = true;

        SSFProperties ssfp = SSFNode.getSSFProperties();

        String fieldSeparatorRegex = ssfp.getProperties().getPropertyValue("fieldSeparatorRegex");
        String chunkStart = ssfp.getProperties().getPropertyValue("chunkStart");
        String chunkEnd = ssfp.getProperties().getPropertyValue("chunkEnd");
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        List<SSFNode> nodes = new ArrayList();

        String lineArray[] = string.split("\n");

        // The first level to be treated differently
        int level = 0;

        List<SSFNode> phraseStack = new ArrayList<SSFNode>();

        SSFNode parent = null;
        SSFNode node = null;

        for (int i = 0; i < lineArray.length; i++)
        {
            if (lineArray[i].equals("") == false)
            {
                if (lineArray[i].contains(chunkEnd) == false)
                {
                    lineArray[i] = lineArray[i].trim();
                }

                String fields[] = lineArray[i].split(fieldSeparatorRegex, 4);

//		if(fields.length <= 1 || fields[1] == null)
                if (lineArray[i] != null && lineArray[i].equals("") == false && (fields == null || fields.length <= 1))
                {
                    validated = false;

                    if (errorLog != null)
                    {
                        errorLog.add(string + "\n");
                        errorLog.add("\nError in line " + (lineNum + i) + ":\n");
                        errorLog.add("********************\n");
                        errorLog.add(lineArray[i]);
                        errorLog.add("********************\n");
                        errorLog.add("Error:_Second_SSF_field_null.\n");
                    } else
                    {
                        System.out.println(string + "\n");
                        System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                        System.out.println("********************");
                        System.out.println(lineArray[i]);
                        System.out.println("********************");
                    }
                }

                if (lineArray[i] == null || lineArray[i].equals("") || fields == null || fields.length <= 1)
                {
                    lineNum++;
                } else if (fields[1].equals(chunkStart) == true)
                {
                    level++;

                    if (level == 1)
                    {
                        parent = null;
                    } else
                    {
                        if (phraseStack.isEmpty())
                        {
                            validated = false;

                            if (errorLog != null)
                            {
                                errorLog.add(string + "\n");
                                errorLog.add("\nError_in_line:_" + (lineNum + i) + "\n");
                                errorLog.add("********************\n");
                                errorLog.add(lineArray[i]);
                                errorLog.add("********************\n");
                                errorLog.add("Error:_Null_parent_for_SSFPhrase._Incorrect_format.\n");
                            } else
                            {
                                System.out.println(string + "\n");
                                System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                                System.out.println("********************");
                                System.out.println(lineArray[i]);
                                System.out.println("********************");
                            }
                        } else
                        {
                            parent = (SSFPhrase) phraseStack.get(phraseStack.size() - 1);
                        }
                    }

                    try
                    {
                        node = new SSFPhrase("0", "", rootName, "");
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    phraseStack.add(node);

                    node.setId(fields[0]);
                    node.setLexData("");

                    if (fields.length > 2)
                    {
                        node.setName(fields[2]);
                    }

                    if (fields.length == 4 && fields[3].equals("") == false)
                    {
                        FeatureStructures fss = new FeatureStructuresImpl();

                        try
                        {
                            fss.readString(fields[3]);
                        } catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }

                        node.setFeatureStructures(fss);
                    }

                    if (level == 1)
                    {
                        nodes.add(node);
                    } else
                    {
                        ((SSFPhrase) parent).addChild(node);
                    }
                } else if (fields[1].equals(chunkEnd))
                {
                    level--;

                    if (phraseStack.isEmpty())
                    {
                        validated = false;

                        if (errorLog != null)
                        {
                            errorLog.add(string + "\n");
                            errorLog.add("\nError_in_line_" + (lineNum + i) + ":\n");
                            errorLog.add("********************");
                            errorLog.add(lineArray[i]);
                            errorLog.add("********************\n");
                            errorLog.add("Error:_Unmatching_ending_bracket._Incorrect_format.\n");
                        } else
                        {
                            System.out.println(string + "\n");
                            System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                            System.out.println("********************");
                            System.out.println(lineArray[i]);
                            System.out.println("********************");
                        }
                    } else
                    {
                        phraseStack.remove(phraseStack.size() - 1);
                    }
                } else if (fields.length > 1 && lineArray[i].equals("") == false && fields[1].equals(chunkStart) == false && fields[1].equals(chunkEnd) == false) // lexical item
                {
                    if (level == 0)
                    {
                        parent = null;
                    } else
                    {
                        if (phraseStack.isEmpty())
                        {
                            validated = false;

                            if (errorLog != null)
                            {
                                errorLog.add(string + "\n");
                                errorLog.add("\nError_in_line_" + (lineNum + i) + ":\n");
                                errorLog.add("********************\n");
                                errorLog.add(lineArray[i]);
                                errorLog.add("********************\n");
                                errorLog.add("Error:_Null_parent_for_a_LexicalItem._Incorrect_format.\n");
                            } else
                            {
                                System.out.println(string + "\n");
                                System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                                System.out.println("********************");
                                System.out.println(lineArray[i]);
                                System.out.println("********************");
                            }
                        } else
                        {
                            parent = (SSFPhrase) phraseStack.get(phraseStack.size() - 1);
                        }
                    }

                    node = new SSFLexItem();

                    try
                    {
                        node.readString(lineArray[i]);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    // If more than one words are joined in one lexical item, separate them
                    String ld = node.getLexData();
                    String ldwrds[] = ld.split("[ ]");

                    if (ldwrds.length > 1)
                    {
                        for (int j = 0; j < ldwrds.length; j++)
                        {
                            SSFNode node1 = null;

                            try
                            {
                                if (node.getFeatureStructures() != null)
                                {
                                    node1 = new SSFLexItem(node.getId(), ldwrds[j], node.getName(), node.getFeatureStructures().makeString());
                                } else
                                {
                                    node1 = new SSFLexItem(node.getId(), ldwrds[j], node.getName(), "");
                                }
                            } catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }

                            if (level == 0)
                            {
                                nodes.add(node1);
                            } else
                            {
                                ((SSFPhrase) parent).addChild(node1);
                            }
                        }
                    } else
                    {
                        if (level == 0)
                        {
                            nodes.add(node);
                        } else
                        {
                            ((SSFPhrase) parent).addChild(node);
                        }
                    }
                }
            } else
            {
                lineNum++;
            }
        }

        if (phraseStack.size() > 0)
        {
            validated = false;

            if (errorLog != null)
            {
                errorLog.add("...Sentence_string:\n");
                errorLog.add(string + "\n");
                errorLog.add("Error:_Wrong_format._Unmatching_brackets.\n");
            } else
            {
                System.out.println("...Sentence_string:");
                System.out.println(string + "\n");
            }
        }

        return validated;
    }

    public static List<SSFNode> readNodesFromString(String string, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String fieldSeparatorRegex = ssfp.getProperties().getPropertyValue("fieldSeparatorRegex");
        String chunkStart = ssfp.getProperties().getPropertyValue("chunkStart");
        String chunkEnd = ssfp.getProperties().getPropertyValue("chunkEnd");
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        List<SSFNode> nodes = new ArrayList<SSFNode>();

        String lineArray[] = string.split("\n");

        // The first level to be treated differently
        int level = 0;

        List<SSFNode> phraseStack = new ArrayList<SSFNode>();

        SSFNode parent = null;
        SSFNode node = null;

        for (int i = 0; i < lineArray.length; i++)
        {
            if (lineArray[i].equals("") == false)
            {
                lineArray[i] = lineArray[i].trim();

                String fields[] = lineArray[i].split(fieldSeparatorRegex, 4);

//		if(fields.length <= 1 || fields[1] == null)
                if (lineArray[i].contains(chunkEnd) == false
                        && (fields == null || fields.length <= 1))
                {
                    if (errorLog != null)
                    {
                        errorLog.add(string + "\n");
                        errorLog.add("\nError_in_line_" + (lineNum + i) + ":\n");
                        errorLog.add("********************\n");
                        errorLog.add(lineArray[i]);
                        errorLog.add("********************\n");
                        errorLog.add("Error:_Second_SSF_field_null.\n");
                    } else
                    {
                        System.out.println(string + "\n");
                        System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                        System.out.println("********************");
                        System.out.println(lineArray[i]);
                        System.out.println("********************");

                        throw new Exception("Error:_Second_SSF_field_null.");
                    }
                }

                if (fields.length > 1 && fields[1].equals(chunkStart) == true)
                {
                    level++;

                    if (level == 1)
                    {
                        parent = null;
                    }
                    else
                    {
                        if (phraseStack.isEmpty())
                        {
                            if (errorLog != null)
                            {
                                errorLog.add(string + "\n");
                                errorLog.add("\nError_in_line:_" + (lineNum + i) + "\n");
                                errorLog.add("********************\n");
                                errorLog.add(lineArray[i]);
                                errorLog.add("********************\n");
                                errorLog.add("Error:_Null_parent_for_SSFPhrase._Incorrect_format.\n");
                            } else
                            {
                                System.out.println(string + "\n");
                                System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                                System.out.println("********************");
                                System.out.println(lineArray[i]);
                                System.out.println("********************");

                                throw new Exception(GlobalProperties.getIntlString("Error:_Null_parent_for_SSFPhrase._Incorrect_format."));
                            }
                        } else
                        {
                            parent = (SSFPhrase) phraseStack.get(phraseStack.size() - 1);
                        }
                    }

                    node = new SSFPhrase("0", "", rootName, "");
                    phraseStack.add(node);

                    node.setId(fields[0]);
                    node.setLexData("");

                    if (fields.length > 2)
                    {
                        node.setName(fields[2]);
                    }

                    if (fields.length == 4 && fields[3].equals("") == false)
                    {
                        FeatureStructures fss = new FeatureStructuresImpl();
                        fss.readString(fields[3]);
                        node.setFeatureStructures(fss);
                    }

                    if (level == 1)
                    {
                        nodes.add(node);
                    } else
                    {
                        ((SSFPhrase) parent).addChild(node);
                    }
                } else if (fields[0].equals(chunkEnd))
                {
                    level--;

                    if (phraseStack.isEmpty())
                    {
                        if (errorLog != null)
                        {
                            errorLog.add(string + "\n");
                            errorLog.add("\nError_in_line_" + (lineNum + i) + ":\n");
                            errorLog.add("********************");
                            errorLog.add(lineArray[i]);
                            errorLog.add("********************\n");
                            errorLog.add("Error:_Unmatching_ending_bracket._Incorrect_format.\n");
                        } else
                        {
                            System.out.println(string + "\n");
                            System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                            System.out.println("********************");
                            System.out.println(lineArray[i]);
                            System.out.println("********************");

                            throw new Exception(GlobalProperties.getIntlString("Error:_Unmatching_ending_bracket._Incorrect_format."));
                        }
                    } else
                    {
                        phraseStack.remove(phraseStack.size() - 1);
                    }
                } else if (fields.length > 1 && lineArray[i].equals("") == false && fields[1].equals(chunkStart) == false && fields[0].equals(chunkEnd) == false) // lexical item
                {
                    if (level == 0)
                    {
                        parent = null;
                    } else
                    {
                        if (phraseStack.isEmpty())
                        {
                            if (errorLog != null)
                            {
                                errorLog.add(string + "\n");
                                errorLog.add("\nError_in_line_" + (lineNum + i) + ":\n");
                                errorLog.add("********************\n");
                                errorLog.add(lineArray[i]);
                                errorLog.add("********************\n");
                                errorLog.add("Error:_Null_parent_for_a_LexicalItem._Incorrect_format.\n");
                            } else
                            {
                                System.out.println(string + "\n");
                                System.out.println("\nError_in_line_" + (lineNum + i) + ":");
                                System.out.println("********************");
                                System.out.println(lineArray[i]);
                                System.out.println("********************");

                                throw new Exception("Error:_Null_parent_for_a_LexicalItem._Incorrect_format.");
                            }
                        } else
                        {
                            parent = (SSFPhrase) phraseStack.get(phraseStack.size() - 1);
                        }
                    }

                    node = new SSFLexItem();
                    node.readString(lineArray[i]);

                    // If more than one words are joined in one lexical item, separate them
                    String ld = node.getLexData();
                    String ldwrds[] = ld.split("[ ]");

                    if (ldwrds.length > 1)
                    {
                        for (int j = 0; j < ldwrds.length; j++)
                        {
                            SSFNode node1;

                            if (node.getFeatureStructures() != null)
                            {
                                node1 = new SSFLexItem(node.getId(), ldwrds[j], node.getName(), node.getFeatureStructures().makeString());
                            } else
                            {
                                node1 = new SSFLexItem(node.getId(), ldwrds[j], node.getName(), "");
                            }

                            if (level == 0)
                            {
                                nodes.add(node1);
                            } else
                            {
                                ((SSFPhrase) parent).addChild(node1);
                            }
                        }
                    } else
                    {
                        if (level == 0)
                        {
                            nodes.add(node);
                        } else
                        {
                            ((SSFPhrase) parent).addChild(node);
                        }
                    }
                }
            } else
            {
                lineNum++;
            }
        }

        if (phraseStack.size() > 0)
        {
            if (errorLog != null)
            {
                errorLog.add("...Sentence_string:\n");
                errorLog.add(string + "\n");
                errorLog.add("Error:_Wrong_format._Unmatching_brackets.\n");
            } else
            {
                System.out.println("...Sentence_string:");
                System.out.println(string + "\n");

                throw new Exception(GlobalProperties.getIntlString("Error:_Wrong_format._Unmatching_brackets."));
            }
        }

        ((ArrayList) nodes).trimToSize();
        return nodes;
    }

    public static List<SSFNode> readNodesFromString(String string) throws Exception
    {
        return readNodesFromString(string, null, 1);
    }

    public static List<SSFNode> readNodesFromChunked(String string) throws Exception
    {
        return readNodesFromChunked(string, null, 1);
    }

    public static List<SSFNode> readNodesFromChunked(String string, List<String> errorLog /*Strings*/, int lineNum) throws Exception
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String bracketFormStart = ssfp.getProperties().getPropertyValue("bracketFormStart");
        String bracketFormEnd = ssfp.getProperties().getPropertyValue("bracketFormEnd");
        String wordTagSeparator = ssfp.getProperties().getPropertyValue("wordTagSeparator");
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        List<SSFNode> nodes = new ArrayList<SSFNode>();

        if (string.contains(bracketFormEnd + wordTagSeparator + rootName))
        {
            string = string.replaceAll(bracketFormEnd + wordTagSeparator + rootName, "");
            string = string.substring(bracketFormStart.length());
            string = string.trim();
        }

        String parts[] = string.split(" ");

        // The first level to be treated differently
        int level = 0;

        List<SSFNode> phraseStack = new ArrayList<SSFNode>();

        SSFNode parent = null;
        SSFNode node;

        for (int i = 0; i < parts.length; i++)
        {
            if (parts[i].equals("") == false)
            {
                if (parts[i].equals(bracketFormStart) == true)
                {
                    level++;

                    if (level == 1)
                    {
                        parent = null;
                    } else
                    {
                        if (phraseStack.isEmpty())
                        {
                            if (errorLog != null)
                            {
                                errorLog.add(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                            } else
                            {
                                System.err.println(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                            }
                        } else
                        {
                            parent = (SSFPhrase) phraseStack.get(phraseStack.size() - 1);
                        }
                    }

                    node = new SSFPhrase("0", "", rootName, "");
                    phraseStack.add(node);

                    node.setId("0");
                    node.setLexData("");

                    if (level == 1)
                    {
                        nodes.add(node);
                    } else
                    {
                        ((SSFPhrase) parent).addChild(node);
                    }
                } else if (parts[i].startsWith(bracketFormEnd))
                {
                    level--;

                    if (phraseStack.isEmpty())
                    {
                        if (errorLog != null)
                        {
                            errorLog.add(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                        } else
                        {
                            System.err.println(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                        }
                    } else
                    {
                        String pparts[] = parts[i].split(wordTagSeparator);

                        if (pparts.length != 2)
                        {
                            if (errorLog != null)
                            {
                                errorLog.add(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                            } else
                            {
                                System.err.println(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                            }
                        } else
                        {
                            if (parent != null)
                            {
                                parent.setName(pparts[1]);
                                phraseStack.remove(phraseStack.size() - 1);
                            } else
                            {
                                if (errorLog != null)
                                {
                                    errorLog.add(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                                } else
                                {
                                    System.err.println(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                                }

                                phraseStack.remove(phraseStack.size() - 1);
                            }
                        }
                    }
                } else if (parts[i].contains(wordTagSeparator)) // lexical item
                {
                    if (level == 0)
                    {
                        parent = null;
                    } else
                    {
                        if (phraseStack.isEmpty())
                        {
                            if (errorLog != null)
                            {
                                errorLog.add(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                            } else
                            {
                                System.err.println(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                            }
                        } else
                        {
                            parent = (SSFPhrase) phraseStack.get(phraseStack.size() - 1);
                        }
                    }

                    String pparts[] = parts[i].split(wordTagSeparator);

                    if (pparts.length != 2)
                    {
                        if (errorLog != null)
                        {
                            errorLog.add(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                        } else
                        {
                            System.err.println(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                        }
                    } else
                    {
                        node = new SSFLexItem(GlobalProperties.getIntlString("0"), pparts[0], pparts[1], "");

                        if (level == 0)
                        {
                            nodes.add(node);
                        } else
                        {
                            ((SSFPhrase) parent).addChild(node);
                        }
                    }
                }
            }
        }

        if (phraseStack.size() > 0)
        {
            if (errorLog != null)
            {
                errorLog.add(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                errorLog.add(GlobalProperties.getIntlString("Error:_Wrong_format._Unmatching_brackets.\n"));
            } else
            {
                System.err.println(GlobalProperties.getIntlString("Error_in_the_bracket_form_file_(line-") + lineNum + "): " + string);
                System.out.println(string + "\n");
            }
        }

        ((ArrayList) nodes).trimToSize();
        return nodes;
    }

    @Override
    public String makeString()
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String fieldSeparatorPrint = ssfp.getProperties().getPropertyValueForPrint("fieldSeparatorPrint");
        String chunkEnd = ssfp.getProperties().getPropertyValueForPrint("chunkEnd");
    	String rootName = ssfp.getProperties().getPropertyValueForPrint("rootName");

        String string = "";

        if(getName().equals(rootName) == false) {
            string = makeTopString() + "\n";
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            string += getChild(i).makeString();
        }

        if(getName().equals(rootName) == false) {
            string += fieldSeparatorPrint + chunkEnd + "\n";
        }

        return string;
    }

    @Override
    public String makeRawSentence()
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();
        String rawsen = getLexData();

//        if(lexdata.equals("") == false)
//            lexdata += " > ";

        int count = leaves.size();
        for (int i = 0; i < count; i++)
        {
            if (i == count - 1)
            {
                rawsen += ((SSFNode) leaves.get(i)).getLexData();
            } else
            {
                rawsen += ((SSFNode) leaves.get(i)).getLexData() + " ";
            }
        }

        return rawsen;
    }

    public void reallocateId(String parentId)
    {
        int i;

        int count = getChildCount();
        for (i = 0; i < count; i++)
        {
            SSFNode node = this.getChild(i);

            if (getChild(i).isLeafNode())
            {
                node.id = parentId + (i + 1);
            } else
            {
                // it is a phrase so need to recursively go inside
                node.id = parentId + (i + 1);
                ((SSFPhrase) node).reallocateId(node.id + ".");
            }
        }
    }

    @Override
    public SanchayMutableTreeNode getCopy() throws Exception
    {
        String str = makeString();

        SSFNode ssfNode = new SSFPhrase();
        ssfNode.readString(str);

        ssfNode.flags = flags;

        return ssfNode;
    }

    // Join child nodes
    public boolean joinNodes(int from, int count)
    {
        boolean joinable = true;

        List<SSFNode> chvec = getAllChildren();

        for (int i = from; i < from + count; i++)
        {
            if ((getChild(i) instanceof SSFLexItem) == false)
            {
                joinable = false;
            }
        }

        if (joinable)
        {
            SSFLexItem joinedNode = new SSFLexItem();
            joinedNode.setName(getChild(from).getName());
            joinedNode.setFeatureStructures(getChild(from).getFeatureStructures());

            String ld = "";

            for (int i = from; i < from + count; i++)
            {
//		if(i < from + count - 1)
//		    ld += getChild(i).getLexData() + " ";
//		else
                ld += getChild(i).getLexData();
            }

            for (int i = 0; i < count; i++)
            {
                remove(from);
            }

            joinedNode.setLexData(ld);

            addChildAt(joinedNode, from);
        }

        return joinable;
    }

    public boolean splitLexItem(int index)
    {
        boolean splittable = (getChild(index) instanceof SSFLexItem);

        if (splittable)
        {
            SSFLexItem node = (SSFLexItem) getChild(index);

            SSFLexItem node1 = new SSFLexItem();
            node1.setName(node.getName());
            node1.setFeatureStructures(node.getFeatureStructures());

            SSFLexItem node2 = null;

            if(node.getFeatureStructures() != null)
            {
                try {
                    node2 = new SSFLexItem(node.getId(), node.getLexData(), node.getName(), node.getFeatureStructures().makeString());
                } catch (Exception ex) {
                    Logger.getLogger(SSFPhrase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                node2 = new SSFLexItem();
                node2.setLexData(node.getLexData());
                node2.setName(node.getName());
            }

            String ld = node.getLexData();

            String lds[] = ld.split("[\\-]", 2);

            if (lds != null && lds.length == 2)
            {
                node1.setLexData(lds[0]);
                node2.setLexData(lds[1]);
            } else
            {
                node1.setLexData(ld);
                node2.setLexData(ld);
            }

            remove(index);

            addChildAt(node2, index);
            addChildAt(node1, index);

            reallocateId(getId());
        }

        return splittable;
    }

    @Override
    public void clear()
    {
        super.clear();

        removeAllChildren();
    }

    public void flatten()
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();
        removeAllChildren();

        int count = leaves.size();
        for (int i = 0; i < count; i++)
        {
            addChild((SSFNode) leaves.get(i));
        }
    }

    public boolean flattenChunks()
    {
        boolean flattened = false;

        if(getParent() !=null && getLexData().equals(""))
        {
            flattened = true;
            removeLayer();
        }

        for (int i = 0; i < getChildCount(); i++)
        {
            SSFNode cnode = getChild(i);

            if(cnode instanceof SSFPhrase)
            {
                ((SSFPhrase) cnode).flattenChunks();
            }
        }

        return flattened;
    }

//    public void copyAttributesMM2Chunk(SSFPhrase mmRoot)
//    {
//
// //        System.out.println("Converting");
////        if(mmRoot == null || mmRoot.countChildren() == 0)
////            return;
//
////        String attribVal = mmRoot.getAttributeValueString(attribName);
//
//    //    SSFPhrase node = (SSFPhrase) ((SSFPhrase) getRoot()).getNodeForId(mmRoot.getId());
//
//    //       SSFNode node = ((SSFPhrase) getRoot()).getNodeForId(mmRoot.getId());
//
////         SSFNode node = getNodeForId(mmRoot.getId());
//
////        if(node != null)
////        {
////            node.setAttributeValue(attribName, attribVal);
////        }
//
//     //   int count = countChildren();
//
////           int count = mmRoot.getChildCount();
//
////        for (int i = 0; i < count; i++)
////        {
//           // SSFPhrase mmChild = (SSFPhrase) mmRoot.getChild(i);
////            SSFNode mmChild = mmRoot.getChild(i);
//
////            if(mmChild instanceof SSFPhrase)
////            {
//           // attribVal = mmChild.getAttributeValueString(attribName);
//
//           // node = ((SSFPhrase) getRoot()).getNodeForId(mmChild.getId());
//
//           // if(node != null)
//           // {
//           //     node.setAttributeValue(attribName, attribVal);
//           // }
//
////            copyAttributesMM2Chunk((SSFPhrase) mmChild, attribName);
////           }
////        }
//
//   /*     if(value==0)
//        {
//             value=1;
//            convertMM2Chunk((SSFPhrase) mmRoot.getChild(0));
//        }
//    */
//        int count=mmRoot.getChildCount();
//
//        SSFNode rootChild=((SSFPhrase) getRoot()).getNodeForId(mmRoot.getId());
//
////        if(count!=0){
////        rootChild.setAttributeValue("name",mmRoot.getAttributeValueString("name"));
////        }
////       value++;
////       int temp=value-1;
//
//        int check=0;
//
//        for(int i=0;i<count;i++)
//        {
//           SSFNode temp = ((SSFPhrase) mmRoot).getChild(i);
//            if(temp instanceof SSFPhrase)
//                check++;
//        }
//
//        String depAttribs[] = FSProperties.getAnnCorraDependencyAttributes();
//
////        String drelAttribVal = mmRoot.getAttributeValueString("drel");
//        String drelAttribVal[] = mmRoot.getOneOfAttributeValues(depAttribs);
//
//        if(drelAttribVal !=null && !drelAttribVal.equals(""))
//        {
//            drelAttribVal[1].replaceAll("[']", "");
//            drelAttribVal[1].replaceAll("[`]", "");
//            drelAttribVal[1].replaceAll("[\"]", "");
//
////            rootChild.setAttributeValue("drel", drelAttribVal);
//            rootChild.setAttributeValue(drelAttribVal[0], drelAttribVal[1]);
//        }
//
//        String nameAttribVal = mmRoot.getAttributeValueString("name");
//
//        if(nameAttribVal != null && nameAttribVal.equals("") == false)
//        {
//            nameAttribVal.replaceAll("[']", "");
//            nameAttribVal.replaceAll("[`]", "");
//            nameAttribVal.replaceAll("[\"]", "");
//
//            if (check!=0)
//                rootChild.setAttributeValue("name", nameAttribVal);
//            else
//            {
//                return;
//            }
//        }
//
//        for (int i=0;i<count;i++)
//        {
//            SSFNode mmChild= mmRoot.getChild(i);
//
//            if(mmChild instanceof SSFPhrase)
//            {
////                rootChild=((SSFPhrase) getRoot()).getNodeForId(mmChild.getId());
//
////                rootChild.setAttributeValue("drel",mmChild.getAttributeValueString("drel"));
//
//                copyAttributesMM2Chunk((SSFPhrase)mmChild);
//            }
//        }
//    }
    public void copyAttributesDep2Chunk(SSFPhrase mmRoot, LinkedHashMap cfgToMMTreeMapping)
    {
//        Iterator itr = cfgToMMTreeMapping.keySet().iterator();
//        String depAttribs[] = FSProperties.getAnnCorraDependencyAttributes();
//
//        while(itr.hasNext())
//        {
//            SSFNode cfgNode = (SSFNode) itr.next();
//
//            SSFNode mmtNode = (SSFNode) cfgToMMTreeMapping.get(cfgNode);
//
//            if((cfgNode instanceof SSFPhrase && mmtNode instanceof SSFPhrase)
//                && (cfgNode != null && mmtNode != null))
//            {
//                String refAtVal[] = mmtNode.getOneOfAttributeValues(depAttribs);
//
//                if(refAtVal != null && refAtVal.length == 2)
//                    cfgNode.setAttributeValue(refAtVal[0], refAtVal[1]);
//            }
//        }

//        removeDSRedundantPhrases();

        String depAttribs[] = FSProperties.getDependencyAttributes();

        int mcount = mmRoot.getChildCount();

        for (int i = 0; i < mcount; i++)
        {
            SSFNode mnode = mmRoot.getChild(i);

            if (mnode instanceof SSFPhrase)
            {
                String refAtVal[] = mnode.getOneOfAttributeValues(depAttribs);

                if (refAtVal == null)
                {
                    copyAttributesDep2Chunk((SSFPhrase) mnode, cfgToMMTreeMapping);
                    continue;
                }

                String refVal = refAtVal[1];

                if (refVal == null)
                {
                    copyAttributesDep2Chunk((SSFPhrase) mnode, cfgToMMTreeMapping);
                    continue;
                }

//                String parts[] = refVal.split(":");
//
//                if (parts[1] == null)
//                {
//                    continue;
//                }

                String mnodeName = mnode.getAttributeValue("name");

                SSFNode cnode = getNodeForAttribVal("name", mnodeName, true);

                if(cnode != null)
                    cnode.setAttributeValue(refAtVal[0], refAtVal[1]);

                copyAttributesDep2Chunk((SSFPhrase) mnode, cfgToMMTreeMapping);
            }
        }
    }

    public void copyDataDep2Chunk(SSFNode mmRoot, LinkedHashMap cfgToMMTreeMapping, boolean leafDependencies)
    {
        String depAttribs[] = FSProperties.getDependencyAttributes();

        int mcount = mmRoot.getChildCount();

        for (int i = 0; i < mcount; i++)
        {
            SSFNode mnode = (SSFNode) mmRoot.getChildAt(i);

            if ((mnode instanceof SSFPhrase && leafDependencies == false)
                    || (leafDependencies == true))
            {
                String refAtVal[] = mnode.getOneOfAttributeValues(depAttribs);

                if (refAtVal == null)
                {
                    copyDataDep2Chunk(mnode, cfgToMMTreeMapping, leafDependencies);
                    continue;
                }

                String refVal = refAtVal[1];

                if (refVal == null)
                {
                    copyDataDep2Chunk(mnode, cfgToMMTreeMapping, leafDependencies);
                    continue;
                }

                String mnodeName = mnode.getAttributeValue("name");

                SSFNode cnode = getNodeForAttribVal("name", mnodeName, true);

                if(cnode != null)
                {
//                    cnode.setAttributeValue(refAtVal[0], refAtVal[1]);
                    FeatureStructures mfss = mnode.getFeatureStructures();

                    if(mfss != null) {
                        cnode.setFeatureStructures(mfss);
                    }

                    cnode.setName(mnode.getName());
                }

                copyDataDep2Chunk(mnode, cfgToMMTreeMapping, leafDependencies);
            }
        }
    }

    public void copyAttributesPS2Chunk(SSFPhrase psRoot, LinkedHashMap cfgToPSTreeMapping)
    {
        String psAttribs[] = FSProperties.getPSAttributes();

        int pcount = psRoot.getChildCount();

        for (int i = 0; i < pcount; i++)
        {
            SSFNode pnode = psRoot.getChild(i);

//            if (pnode instanceof SSFPhrase)
//            {
                String refAtVal[] = pnode.getOneOfAttributeValues(psAttribs);

                if (refAtVal == null)
                {
                    continue;
                }

                String refVal = refAtVal[1];

                if (refVal == null)
                {
                    continue;
                }

//                String parts[] = refVal.split(":");
//
//                if (parts[1] == null)
//                {
//                    continue;
//                }

                String pnodeName = pnode.getAttributeValue("name");

                SSFNode cnode = getNodeForAttribVal("name", pnodeName, true);

                cnode.setAttributeValue(refAtVal[0], refAtVal[1]);

                copyAttributesPS2Chunk((SSFPhrase) pnode, cfgToPSTreeMapping);
//            }
        }
    }

    // Recursive
    public void reallocateNames(LinkedHashMap tags, LinkedHashMap words /* both null at the top level call */)
    {
        reallocateNames(tags, words, 0);
    }

    public void reallocateNames(LinkedHashMap tags, LinkedHashMap words /* both null at the top level call */, int fromChild)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String chunkStart = ssfp.getProperties().getPropertyValue("chunkStart");

        int i;

        if(tags == null)
        {
            tags = new LinkedHashMap(0, 10);
            words = new LinkedHashMap(0, 10);
        }

        int count = getChildCount();

        for (i = fromChild; i < count; i++)
        {
            SSFNode node = this.getChild(i);

//            if(getChild(i) instanceof SSFPhrase)
//            {
            String tag = node.getName();
            String word = node.getLexData();

            if (tag == null || tag.equals("")) {
                tag = "XP";
            }

            if(node instanceof SSFPhrase && word.equals("") == false) {
                tag = node.getLexData();
            }

            if (word == null || word.equals("")) {
                word = "NULL";
            }

            if (word.equals(":")) {
                word = "symColon";
            }

            String oldName = node.getAttributeValue("name");
            String newName = "";

            if(node instanceof SSFPhrase)
            {
                Integer prevTagNum = (Integer) tags.get(tag);

                if (prevTagNum == null)
                {
                    newName = tag;
                    tags.put(tag, new Integer(1));
                } else
                {
                    newName = tag + (prevTagNum.intValue() + 1);
                    tags.put(tag, new Integer(prevTagNum.intValue() + 1));
                }
            }
            else
            {
                Integer prevTagNum = (Integer) words.get(word);

                if (prevTagNum == null)
                {
                    newName = word;
                    words.put(word, new Integer(1));
                } else
                {
                    newName = word + (prevTagNum.intValue() + 1);
                    words.put(word, new Integer(prevTagNum.intValue() + 1));
                }
            }

//            SSFPhrase rootNode = (SSFPhrase) getRoot();

//            Vector refNodes = rootNode.getReferringNodes(node, PHRASE_STRUCTURE_MODE);
            List<SSFNode> refNodes = getReferringNodes(node, PHRASE_STRUCTURE_MODE);

            int rcount = refNodes.size();

            for (int j = 0; j < rcount; j++)
            {
                SSFPhrase rnode = (SSFPhrase) refNodes.get(j);

                if (rnode != null)
                {
                    rnode.setReferredName(newName, PHRASE_STRUCTURE_MODE);
                }
            }

//            refNodes = rootNode.getReferringNodes(node, DEPENDENCY_STRUCTURE_MODE);
            refNodes = getReferringNodes(node, DEPENDENCY_RELATIONS_MODE);

            rcount = refNodes.size();

            for (int j = 0; j < rcount; j++)
            {
//                SSFPhrase rnode = (SSFPhrase) refNodes.get(j);
                SSFNode rnode = refNodes.get(j);

                if (rnode != null)
                {
                    rnode.setReferredName(newName, DEPENDENCY_RELATIONS_MODE);
                }
            }

            node.setAttributeValue("name", newName);
//            }

            if(node instanceof SSFPhrase) {
                ((SSFPhrase) node).reallocateNames(tags, words);
            }
        }
    }

    public static void getMapping(SSFNode node1, SSFNode node2, LinkedHashMap mapping)
    {
        if ((node1 instanceof SSFLexItem && node2 instanceof SSFLexItem) || (node1 instanceof SSFPhrase && node2 instanceof SSFPhrase))
        {
            if (node1.getName().equals(node2.getName()) && node1.countChildren() == node2.countChildren())
            {
                mapping.put(node1, node2);
            }

            if (node1 instanceof SSFPhrase)
            {
                int ccount = node1.countChildren();

                for (int i = 0; i < ccount; i++)
                {
                    SSFNode cnode1 = ((SSFPhrase) node1).getChild(i);
                    SSFNode cnode2 = ((SSFPhrase) node2).getChild(i);

                    mapping.put(cnode1, cnode2);

                    getMapping(cnode1, cnode2, mapping);
                }
            }
        }
    }

    public void expandMMTree(LinkedHashMap cfgToMMTreeMapping)
    {
        LinkedHashMap mmtToCFGMapping = (LinkedHashMap) UtilityFunctions.getReverseMap(cfgToMMTreeMapping);

        int ccount = countChildren();

        for (int i = 0; i < ccount; i++)
        {
            SSFNode mmtNode = getChild(i);
            SSFNode cfgNode = (SSFNode) mmtToCFGMapping.get(mmtNode);

            if (cfgNode == null)
            {
                if (mmtNode instanceof SSFPhrase)
                {
                    ((SSFPhrase) mmtNode).expandMMTree(cfgToMMTreeMapping);
                }

                continue;
            }

            if (cfgNode instanceof SSFPhrase && ((SSFPhrase) mmtNode).hasLexItemChild() == false)
            {
                mmtNode.setLexData("");

                List<SanchayMutableTreeNode> leaves = cfgNode.getAllLeaves();

                int iccount = leaves.size();

                for (int j = 0; j < iccount; j++)
                {
                    SSFNode cnode = (SSFNode) leaves.get(j);

                    SSFNode mnode = null;

                    try
                    {
                        mnode = (SSFNode) cnode.getCopy();
                    } catch (Exception ex)
                    {
                        Logger.getLogger(SSFPhrase.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try
                    {
                        mmtNode.add(mnode);
                        cfgToMMTreeMapping.put(cnode, mnode);
                    } catch (Exception ex)
                    {
                        Logger.getLogger(SSFPhrase.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (mmtNode instanceof SSFPhrase)
            {
                ((SSFPhrase) mmtNode).expandMMTree(cfgToMMTreeMapping);
            }
        }
    }

    public SSFPhrase convertToGDepNode(LinkedHashMap cfgToMMTreeMapping)
    {
        return convertToGDepNode(cfgToMMTreeMapping, true);
    }

    public SSFPhrase convertToGDepNode(LinkedHashMap cfgToMMTreeMapping, boolean collapse)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        if (getName().equals(rootName) || getParent() == null)
        {
            reallocateNames(null, null);
        }

        SSFPhrase mmRoot = null;

        try
        {
            mmRoot = (SSFPhrase) getCopy();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (cfgToMMTreeMapping != null)
        {
            getMapping(this, mmRoot, cfgToMMTreeMapping);
        }

//        mmRoot.removeNonChunkPhrases();

        String depAttribs[] = FSProperties.getDependencyTreeAttributes();

//	Vector drelNodes = mmRoot.getNodesForAttrib("drel");
        List<SSFNode> drelNodes = mmRoot.getNodesForOneOfAttribs(depAttribs, true);
        List<SSFNode> namedNodesVec = mmRoot.getNodesForAttrib("name", true);

        if (drelNodes.size() <= 0 || namedNodesVec.size() <= 0)
        {
            return null;
        }

        LinkedHashMap<String, SSFNode> namedNodes = new LinkedHashMap<String, SSFNode>();

        int count = namedNodesVec.size();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) namedNodesVec.get(i);

            if (node instanceof SSFPhrase)
            {
                String nm = (String) node.getAttributeValue("name");
                namedNodes.put(nm, node);

//            node.collapseLexicalItems();
            }
        }

        count = drelNodes.size();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) drelNodes.get(i);

            if (node.isLeafNode())
            {
//                System.out.println(GlobalProperties.getIntlString("Wrong_input_node:_Only_chunks_can_be_part_of_the_dependency_tree"));
//                return null;
            } else
            {
                // It is a chunk, so check whether it has a drel attribute.
                // If it does, than find the chunk to which it is related and make that its parent.

//		String drel = (String) node.getFeatureStructures().getAltFSValue(0).getAttribute("drel").getAltValue(0).getValue();
                String drel = (String) node.getFeatureStructures().getAltFSValue(0).getOneOfAttributes(depAttribs).getAltValue(0).getValue();

                String atval[] = drel.split("[:]");

//                if (atval.length != 2 || atval[1].equals("") == true)
//                {
//                    System.out.println("Wrong value of attribute: " + drel);
//                    return null;
//                }

                String rel = atval[0];
                String nm = "";

                if(atval.length == 1)
                {
                    nm = atval[0];
                    rel = "";
                }
                else {
                    nm = atval[1];
                }

                String chunk = ((SSFPhrase) node).makeRawSentence();

                SSFNode mmParent = (SSFNode) namedNodes.get(nm);

                if (mmParent == null)
                {
                    continue;
                }

                // New
//                mmParent.collapseLexicalItems();
                if(collapse) {
                    mmParent.collapseLexicalItemsDeep();
                }

                mmParent.add(node);

//                node.collapseLexicalItems();
                if(collapse) {
                    node.collapseLexicalItemsDeep();
                }
            }
        }

        if (mmRoot.getChildCount() == 0)
        {
            return null;
        }

//        if(mmRoot != null)
//            mmRoot.removeDSRedundantPhrases();
        if(mmRoot.getChildCount() == 1 && collapse) {
            mmRoot.collapseLexicalItemsDeep();
        }

        return mmRoot;
    }

    public SSFPhrase convertToLDepNode(LinkedHashMap cfgToMMTreeMapping)
    {
        return convertToLDepNode(cfgToMMTreeMapping, true);
    }

    public SSFPhrase convertToLDepNode(LinkedHashMap cfgToDepTreeMapping, boolean collapse)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        if (getName().equals(rootName) || getParent() == null)
        {
            reallocateNames(null, null);
        }

        SSFPhrase mmRoot = null;

        try
        {
            mmRoot = (SSFPhrase) getCopy();

            mmRoot.flattenChunks();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (cfgToDepTreeMapping != null)
        {
            getMapping(this, mmRoot, cfgToDepTreeMapping);
        }

//        mmRoot.removeNonChunkPhrases();

        String depAttribs[] = FSProperties.getDependencyTreeAttributes();

//	Vector drelNodes = mmRoot.getNodesForAttrib("drel");
        List<SSFNode> drelNodes = mmRoot.getNodesForOneOfAttribs(depAttribs, true);
        List<SSFNode> namedNodesVec = mmRoot.getNodesForAttrib("name", true);

        if (drelNodes.size() <= 0 || namedNodesVec.size() <= 0)
        {
            return mmRoot;
        }

        LinkedHashMap<String, SSFNode> namedNodes = new LinkedHashMap();

        int count = namedNodesVec.size();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) namedNodesVec.get(i);

//            if (node instanceof SSFPhrase)
//            if (node instanceof SSFLexItem)
//            {
                String nm = (String) node.getAttributeValue("name");
                namedNodes.put(nm, node);

//            node.collapseLexicalItems();
//            }
        }

        count = drelNodes.size();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) drelNodes.get(i);

            if (node instanceof SSFPhrase && node.getLexData().equals(""))
            {
//                node.removeLayer();
            }
            else
            {
                // It is a chunk, so check whether it has a drel attribute.
                // If it does, than find the chunk to which it is related and make that its parent.

                SSFNode namedNode = namedNodes.get(node.getAttributeValue("name"));

//		String drel = (String) node.getFeatureStructures().getAltFSValue(0).getAttribute("drel").getAltValue(0).getValue();
                String drel = (String) namedNode.getFeatureStructures().getAltFSValue(0).getOneOfAttributes(depAttribs).getAltValue(0).getValue();

                String atval[] = drel.split("[:]");

//                if (atval.length != 2 || atval[1].equals("") == true)
//                {
//                    System.out.println("Wrong value of attribute: " + drel);
//                    return null;
//                }

                String rel = atval[0];
                String nm = "";

                if(atval.length == 1)
                {
                    nm = atval[0];
                    rel = "";
                }
                else {
                    nm = atval[1];
                }

//                String chunk = ((SSFPhrase) node).makeRawSentence();

                SSFNode mmParent = (SSFNode) namedNodes.get(nm);

                SSFNode mmGrandParent = (SSFNode) mmParent.getParent();

//                if (mmParent == null || mmGrandParent == null)
                if (mmParent == null)
                {
                    continue;
                }

                // New
//                mmParent.collapseLexicalItems();
//                if(collapse)
//                    mmParent.collapseLexicalItemsDeep();

                if(mmParent instanceof SSFLexItem)
                {
                    SSFPhrase mmParentPhrase = new SSFPhrase(mmParent.getId(),
                            mmParent.getLexData(), mmParent.getName(), mmParent.getFeatureStructures());

                    int mmParentIndex = mmGrandParent.getIndex((SSFNode) mmParent);

                    mmGrandParent.remove((SSFNode) mmParent);

                    mmGrandParent.insert(mmParentPhrase, mmParentIndex);

                    LinkedHashMap d2cTreeMap = (LinkedHashMap) UtilityFunctions.getReverseMap(cfgToDepTreeMapping);

                    SSFNode cfgNode = (SSFNode) d2cTreeMap.get(mmParent);

                    cfgToDepTreeMapping.put(cfgNode, mmParentPhrase);

                    namedNodes.put(mmParent.getAttributeValue("name"), mmParentPhrase);

    //                int ind = drelNodes.indexOf(mmParent);
    //
    //                if(ind != -1)
    //                    drelNodes.setElementAt(mmParentPhrase, ind);

                    mmParentPhrase.add(namedNode);
                }
                else {
                    mmParent.add(namedNode);
                }

//                node.collapseLexicalItems();
//                if(collapse)
//                    node.collapseLexicalItemsDeep();
            }
        }

        if (mmRoot.getChildCount() == 0)
        {
            return null;
        }

//        if(mmRoot != null)
//            mmRoot.removeDSRedundantPhrases();
//        if(mmRoot.getChildCount() == 1 && collapse)
//            mmRoot.collapseLexicalItemsDeep();

        return mmRoot;
    }

    public SSFPhrase convertToPSNode(LinkedHashMap cfgToPSTreeMapping)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();
        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        if (getName().equals(rootName) || getParent() == null)
        {
            reallocateNames(null, null);
        }


        SSFPhrase psRoot = null;

        try
        {
            psRoot = (SSFPhrase) getCopy();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (cfgToPSTreeMapping != null)
        {
            getMapping(this, psRoot, cfgToPSTreeMapping);
        }

        String psAttribs[] = FSProperties.getPSTreeAttributes();

//	Vector drelNodes = mmRoot.getNodesForAttrib("drel");
        List<SSFNode> psrelNodes = psRoot.getNodesForOneOfAttribs(psAttribs, true);
        List<SSFNode> namedNodesVec = psRoot.getNodesForAttrib("name", true);

        if (psrelNodes.size() <= 0 || namedNodesVec.size() <= 0)
        {
            return null;
        }

        LinkedHashMap<String, SSFNode> namedNodes = new LinkedHashMap<String, SSFNode>();

        int count = namedNodesVec.size();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) namedNodesVec.get(i);

//            if (node instanceof SSFPhrase)
//            {
                String nm = (String) node.getFeatureStructures().getAltFSValue(0).getAttribute("name").getAltValue(0).getValue();
                namedNodes.put(nm, node);

//            node.collapseLexicalItems();
//            }
        }

        count = psrelNodes.size();
        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) psrelNodes.get(i);

//            if (node.isLeafNode())
//            {
//                System.out.println("Wrong input node: Only chunks can be part of the MM tree");
//                return null;
//            } else
//            {
                // It is a chunk, so check whether it has a drel attribute.
                // If it does, than find the chunk to which it is related and make that its parent.

//		String drel = (String) node.getFeatureStructures().getAltFSValue(0).getAttribute("drel").getAltValue(0).getValue();
                String psrel = (String) node.getFeatureStructures().getAltFSValue(0).getOneOfAttributes(psAttribs).getAltValue(0).getValue();

                String atval[] = psrel.split("[:]");

                if (atval.length != 2 || atval[1].equals("") == true)
                {
                    System.out.println(GlobalProperties.getIntlString("Wrong_value_of_attribute:_") + psrel);
                    return null;
                }

                String rel = atval[0];
                String nm = atval[1];

//                String chunk = ((SSFPhrase) node).makeRawSentence();

                SSFNode psParent = (SSFNode) namedNodes.get(nm);

                if (psParent == null)
                {
                    continue;
                }

                // New
//                psParent.collapseLexicalItems();

                psParent.add(node);

//                node.collapseLexicalItems();
//            }
        }

        if (psRoot.getChildCount() == 0)
        {
            return null;
        }

        return psRoot;
    }

    public SSFPhrase convertToPennDepNode()
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String rootName = ssfp.getProperties().getPropertyValue("rootName");

        SSFPhrase rootCopy = null;
        SSFPhrase pdRoot = null;

        try
        {
            rootCopy = (SSFPhrase) getCopy();
        } catch (Exception ex)
        {
            System.out.println(GlobalProperties.getIntlString("Error_in_node_copying"));
            ex.printStackTrace();
        }

        List<SSFNode> pdNodes = rootCopy.getNodesForAttrib("penndep", true);
        List<SSFNode> namedNodesVec = rootCopy.getNodesForAttrib("name", true);

        if (pdNodes.size() <= 0 || namedNodesVec.size() <= 0)
        {
            return null;
        }

        LinkedHashMap<String, SSFNode> namedNodes = new LinkedHashMap<String, SSFNode>();
        LinkedHashMap<String, SSFNode> parentNodes = new LinkedHashMap<String, SSFNode>();
        LinkedHashMap<String, SSFNode> namedPhraseNodes = new LinkedHashMap<String, SSFNode>();

        int count = namedNodesVec.size();

        List<SSFNode> namedPhraseNodesVec = new ArrayList<SSFNode>(count);

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) namedNodesVec.get(i);

            String nm = (String) node.getFeatureStructures().getAltFSValue(0).getAttribute("name").getAltValue(0).getValue();
            namedNodes.put(nm, node);

            SSFPhrase phraseNode = null;

            try
            {
                phraseNode = new SSFPhrase(node.getId(), node.getLexData(), node.getName(), node.getFeatureStructures());
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            namedPhraseNodesVec.add(phraseNode);
            namedPhraseNodes.put(nm, phraseNode);
        }

        count = pdNodes.size();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = (SSFNode) pdNodes.get(i);

            String penndep = (String) node.getFeatureStructures().getAltFSValue(0).getAttribute("penndep").getAltValue(0).getValue();

            String atval[] = penndep.split("[:]");

            if (atval.length != 2 || atval[1].equals("") == true)
            {
                System.out.println(GlobalProperties.getIntlString("Wrong_value_of_penndep_attribute:_") + penndep);
                return null;
            }

            String rel = atval[0];
            String nm = atval[1];

            SSFPhrase phraseNode = (SSFPhrase) namedPhraseNodesVec.get(i);

            SSFNode prnt = (SSFNode) namedNodes.get(nm);

            SSFPhrase phraseParent = (SSFPhrase) namedPhraseNodes.get(nm);

            if (rel.equalsIgnoreCase("ROOT"))
            {
//                phraseParent = new SSFPhrase("0", "", rootName, (FeatureStructures) null);
                pdRoot = phraseNode;
            } else
            {
                phraseParent.add(phraseNode);
            }
        }

        return pdRoot;
    }

    @Override
    public void collapseLexicalItems()
    {
        boolean hasOnlyLeaves = true;

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            if (getChild(i).getClass().equals(SSFPhrase.class))
            {
                hasOnlyLeaves = false;
                i = count;
            }
        }

        if (hasOnlyLeaves && count > 0)
        {
            String rawString = makeRawSentence();
            setLexData(rawString);
            removeAllChildren();
        }
    }

    @Override
    public void collapseLexicalItemsDeep()
    {
        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            SSFNode node = getChild(i);

            if (node instanceof SSFPhrase)
            {
                if (((SSFPhrase) node).hasLexItemChild())
                {
                    String rawString = "";

                    int ccount = ((SSFPhrase) node).countChildren();

                    for (int j = 0; j < ccount; j++)
                    {
                        SSFNode cnode = ((SSFPhrase) node).getChild(j);

                        if (cnode instanceof SSFLexItem)
                        {
                            if (j == ccount - 1)
                            {
                                rawString += cnode.getLexData();
                            } else
                            {
                                rawString += cnode.getLexData() + " ";
                            }

                            ((SSFPhrase) node).removeChild(j--);
                            ccount--;
                        }
                    }

                    node.setLexData(rawString);
                }

                ((SSFPhrase) node).collapseLexicalItemsDeep();
            }
        }
    }

    public boolean isTaggingSame(SSFPhrase ch)
    {
        if (ch == null)
        {
            return false;
        }

        if (getDifferentPOSTags(ch) != null)
        {
            return false;
        }

        return true;
    }

    // Return indices of leaves for which the POS tags are different from those of the arguement.
    public int[] getDifferentPOSTags(SSFPhrase ch)
    {
        int diff[];
        List<SanchayMutableTreeNode> lvs = getAllLeaves();

        int count = lvs.size();

        if (ch == null)
        {
            diff = new int[count];

            for (int i = 0; i < count; i++)
            {
                diff[i] = i;
            }

            return diff;
        }

        List<SanchayMutableTreeNode> chlvs = ch.getAllLeaves();

        List<Integer> dvec = new ArrayList<Integer>();

        for (int i = 0; i < count && i < chlvs.size(); i++)
        {
            if (((SSFLexItem) lvs.get(i)).getName().equalsIgnoreCase(((SSFLexItem) chlvs.get(i)).getName()) == false)
            {
                dvec.add(new Integer(i));
            }
        }

        count = dvec.size();

        if (count <= 0)
        {
            return null;
        }

        diff = new int[count];

        for (int i = 0; i < count; i++)
        {
            diff[i] = ((Integer) dvec.get(i)).intValue();
        }

        return diff;
    }

    public boolean areLexItemFeaturesSame(SSFPhrase ch)
    {
        if (ch == null)
        {
            return false;
        }

        if (getDifferentLexItemFeatures(ch) != null)
        {
            return false;
        }

        return true;
    }

    // Return indices of leaves for which the Features are different from those of the arguement.
    public int[] getDifferentLexItemFeatures(SSFPhrase ch)
    {
        int diff[];
        List<SanchayMutableTreeNode> lvs = getAllLeaves();

        int count = lvs.size();

        if (ch == null)
        {
            diff = new int[count];

            for (int i = 0; i < count; i++)
            {
                diff[i] = i;
            }

            return diff;
        }

        List<SanchayMutableTreeNode> chlvs = ch.getAllLeaves();

        List<Integer> dvec = new ArrayList<Integer>();

        for (int i = 0; i < count && i < chlvs.size(); i++)
        {
            FeatureStructures fstrs = ((SSFLexItem) lvs.get(i)).getFeatureStructures();
            FeatureStructures chunk_fstrs = ((SSFLexItem) chlvs.get(i)).getFeatureStructures();

            if ((fstrs != null && fstrs.equals(chunk_fstrs) == false) || (fstrs == null && chunk_fstrs != null))
            {
                dvec.add(new Integer(i));
            }
        }

        count = dvec.size();

        if (count <= 0)
        {
            return null;
        }

        diff = new int[count];

        for (int i = 0; i < count; i++)
        {
            diff[i] = ((Integer) dvec.get(i)).intValue();
        }

        return diff;
    }

    public boolean areChunkFeaturesSame(SSFPhrase ch)
    {
        if (ch == null)
        {
            return false;
        }

        if (getDifferentChunkFeatures(ch) != null)
        {
            return false;
        }

        return true;
    }

    // Return indices of chunks for which the Features are different from those of the arguement.
    public int[] getDifferentChunkFeatures(SSFPhrase ch)
    {
        int diff[];

        int count = countChildren();

        if (ch == null || isChunkingSame(ch) == false)
        {
            diff = new int[count];

            for (int i = 0; i < count; i++)
            {
                diff[i] = i;
            }

            return diff;
        }

        List<Integer> dvec = new ArrayList<Integer>();

        for (int i = 0; i < count; i++)
        {
            FeatureStructures fstrs = getChild(i).getFeatureStructures();
            FeatureStructures chunk_fstrs = ch.getChild(i).getFeatureStructures();

            if ((fstrs != null && fstrs.equals(chunk_fstrs) == false) || (fstrs == null && chunk_fstrs != null))
            {
                dvec.add(new Integer(i));
            }
        }

        count = dvec.size();

        if (count <= 0)
        {
            return null;
        }

        diff = new int[count];

        for (int i = 0; i < count; i++)
        {
            diff[i] = ((Integer) dvec.get(i)).intValue();
        }

        return diff;
    }

    /*
     * Check whether the chunk has the same lexical items, irrespective of their POS tags.
     */
    public boolean isChunkingSame(SSFPhrase ch)
    {
        if (ch == null)
        {
            return false;
        }

        if (getLexData().equals(ch.getLexData()) == false)
        {
            return false;
        }

        if (getName().equals(ch.getName()) == false)
        {
            return false;
        }

        if (countChildren() != ch.countChildren())
        {
            return false;
        }

        int count = countChildren();

        for (int i = 0; i < count; i++)
        {
            if ((getChild(i).isLeafNode() == false && ch.getChild(i).isLeafNode() == true) || (getChild(i).isLeafNode() == true && ch.getChild(i).isLeafNode() == false))
            {
                return false;
            } else if (getChild(i).isLeafNode() == true && ch.getChild(i).isLeafNode() == true)
            {
                if (((SSFNode) getChild(i)).getLexData().equals(((SSFNode) ch.getChild(i)).getLexData()) == false)
                {
                    return false;
                }
            } else if (getChild(i).isLeafNode() == false && ch.getChild(i).isLeafNode() == false)
            {
                return ((SSFPhrase) getChild(i)).isChunkingSame(((SSFPhrase) ch.getChild(i)));
            }
        }

        return true;
    }

    @Override
    public void setValuesInTable(DefaultTableModel tbl, int mode)
    {
//	if(requiredColumnCount == -1 || rowIndex == -1 || columnIndex == -1)
//	    return;

        super.setValuesInTable(tbl, mode);

        if (isLeaf() == false)
        {
            int chcount = getChildCount();

            for (int i = 0; i < chcount; i++)
            {
                SSFNode child = (SSFNode) getChildAt(i);
                child.setValuesInTable(tbl, mode);
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof SSFPhrase)) {
            return false;
        }
        
        if (super.equals(obj) == false)
        {
            return false;
        }

        SSFPhrase pobj = (SSFPhrase) obj;

        int count = countChildren();
        if (count != pobj.countChildren())
        {
            return false;
        }

        for (int i = 0; i < count; i++)
        {
            if (getChild(i).equals(pobj.getChild(i)) == false)
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean matches(FindReplaceOptions findReplaceOptions)
    {
        boolean match = false;

        Pattern pattern = FindReplace.compilePattern(findReplaceOptions.findText, findReplaceOptions);
        Matcher matcher = null;

        String text = makeRawSentence();

        if (text != null && text.equals("") == false)
        {
            matcher = pattern.matcher(text);

            if (matcher.find())
            {
                match = true;
            }
        } else
        {
            match = false;
        }

//        if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.tag != null && findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.tag.equals("") == false)
//        {
//            pattern = FindReplace.compilePattern(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.tag, findReplaceOptions);
//
//            String tag = getName();
//
//            if (tag != null && tag.equals("") == false)
//            {
//                matcher = pattern.matcher(tag);
//
//                if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr1.equals("And"))
//                {
//                    match = match && matcher.find();
//                } else
//                {
//                    match = match || matcher.find();
//                }
//            } else
//            {
//                if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr1.equals("And"))
//                {
//                    match = match && false;
//                } else
//                {
//                    match = match || false;
//                }
//            }
//        }
//
//        if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature != null && findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature.equals("") == false)
//        {
//            if (getAttribute(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature) != null)
//            {
//                if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr2.equals("And"))
//                {
//                    match = match && true;
//                } else
//                {
//                    match = match || true;
//                }
//            } else
//            {
//                if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr2.equals("And"))
//                {
//                    match = match && false;
//                } else
//                {
//                    match = match || false;
//                }
//            }
//        }
//
//        if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.featureValue != null && findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.featureValue.equals("") == false)
//        {
//            pattern = FindReplace.compilePattern(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.featureValue, findReplaceOptions);
//            String val = (String) getAttributeValueString(findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.feature);
//
//            if (val != null && val.equals("") == false)
//            {
//                matcher = pattern.matcher(val);
//
//                if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr3.equals("And"))
//                {
//                    match = match && matcher.find();
//                } else
//                {
//                    match = match || matcher.find();
//                }
//            } else
//            {
//                if (findReplaceOptions.resourceQueryOptions.syntacticCorpusQueryOptions.andOr3.equals("And"))
//                {
//                    match = match && false;
//                } else
//                {
//                    match = match || false;
//                }
//            }
//        }

        int count = countChildren();

        for (int i = 0; i < count; i++)
        {
            SSFNode child = getChild(i);
            match = match || child.matches(findReplaceOptions);
        }

        if(match) {
            isHighlighted(true);
        }

        return match;
    }

    public String getLexicalSequence(String tag, String compoundTag)
    {
        String lexSeq = "";

        Pattern tp = Pattern.compile(tag);
        Pattern ctp = Pattern.compile(compoundTag);

        boolean goOn = false;

        int scount = countChildren();

        for (int i = 0; i < scount; i++)
        {
            SSFNode ssfNode = getChild(i);

            if(ssfNode instanceof SSFLexItem)
            {
                Matcher tm = tp.matcher(ssfNode.getName());
                Matcher ctm = ctp.matcher(ssfNode.getName());

                if(tm.find() || ctm.find())
                {
                    if(goOn) {
                        lexSeq += "_" + ssfNode.getLexData();
                    }
                    else {
                        lexSeq = ssfNode.getLexData();
                    }

                    goOn = true;
                }
                else
                {
                    goOn = false;

                    if(lexSeq.equals("") == false) {
                        return lexSeq;
                    }
                }
            }
        }

        return lexSeq;
    }

    public String getLexDataForTag(String tag)
    {
        int scount = countChildren();

        for (int i = 0; i < scount; i++)
        {
            SSFNode ssfNode = getChild(i);

            if(ssfNode instanceof SSFLexItem && ssfNode.getName().equalsIgnoreCase(tag)) {
                return ssfNode.getLexData();
            }
        }

        return "";
    }

    public String getStemForTag(String tag)
    {
        int scount = countChildren();

        for (int i = 0; i < scount; i++)
        {
            SSFNode ssfNode = getChild(i);

            if(ssfNode instanceof SSFLexItem && ssfNode.getName().equalsIgnoreCase(tag)) {
                return ssfNode.getAttributeValue("lex");
            }
        }

        return "";
    }

    public LinkedHashMap<String, Integer> getWordFreq()
    {
        LinkedHashMap<String, Integer> words = new LinkedHashMap();

        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int lcount = leaves.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode leafNode = (SSFNode) leaves.get(j);

            if(leafNode instanceof SSFLexItem)
            {
                String lexData = leafNode.getLexData();

                if(words.get(lexData) == null) {
                    words.put(lexData, 1);
                }
                else {
                    words.put(lexData, words.get(lexData) + 1);
                }
            }
        }

        return words;
    }

    public LinkedHashMap<String, Integer> getPOSTagFreq()
    {
        LinkedHashMap<String, Integer> tags = new LinkedHashMap();

        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int lcount = leaves.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode leafNode = (SSFNode) leaves.get(j);

            if(leafNode instanceof SSFLexItem)
            {
                String tag = leafNode.getName();

                if(tags.get(tag) == null) {
                    tags.put(tag, 1);
                }
                else {
                    tags.put(tag, tags.get(tag) + 1);
                }
            }
        }

        return tags;
    }

    public LinkedHashMap<String, Integer> getWordTagPairFreq()
    {
        LinkedHashMap<String, Integer> words = new LinkedHashMap();

        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int lcount = leaves.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode leafNode = (SSFNode) leaves.get(j);

            if(leafNode instanceof SSFLexItem)
            {
                String lexData = leafNode.getLexData();
                String tag = leafNode.getName();

                String wordTagPair = lexData + "/" + tag;

                if(words.get(wordTagPair) == null) {
                    words.put(wordTagPair, 1);
                }
                else {
                    words.put(wordTagPair, words.get(wordTagPair) + 1);
                }
            }
        }

        return words;
    }

    public LinkedHashMap<String, Integer> getChunkTagFreq()
    {
        LinkedHashMap<String, Integer> tags = new LinkedHashMap();

        List<SSFNode> allChildren = getAllChildren();

        int lcount = allChildren.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode childNode = allChildren.get(j);

            if(childNode instanceof SSFPhrase)
            {
                String tag = childNode.getName();

                if(tags.get(tag) == null) {
                    tags.put(tag, 1);
                }
                else {
                    tags.put(tag, tags.get(tag) + 1);
                }

                tags.putAll(((SSFPhrase) childNode).getChunkTagFreq());
            }
        }

        return tags;
    }

    public LinkedHashMap<String, Integer> getGroupRelationFreq()
    {
        LinkedHashMap<String, Integer> rels = new LinkedHashMap();

        List<SSFNode> allChildren = getAllChildren();

        String depAttribs[] = FSProperties.getDependencyAttributes();

        int lcount = allChildren.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode childNode = allChildren.get(j);

            if(childNode instanceof SSFPhrase)
            {
                String refAtVal[] = childNode.getOneOfAttributeValues(depAttribs);

                if(refAtVal == null) {
                    continue;
                }

                String attrib = refAtVal[0];
                String val = refAtVal[1];

                String parts[] = val.split(":");

                String attribVal = attrib + "=" + parts[0];

                if(rels.get(attribVal) == null)
                {
                    rels.put(attribVal, 1);
                }
                else {
                    rels.put(attribVal, rels.get(attribVal) + 1);
                }

                rels.putAll(((SSFPhrase) childNode).getGroupRelationFreq());
            }
        }

        return rels;
    }

    public LinkedHashMap<String, Integer> getChunkRelationFreq()
    {
        LinkedHashMap<String, Integer> rels = new LinkedHashMap();

        List<SSFNode> allChildren = getAllChildren();

        String depAttribs[] = FSProperties.getDependencyAttributes();

        int lcount = allChildren.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode childNode = allChildren.get(j);

            if(childNode instanceof SSFPhrase)
            {
                String refAtVal[] = childNode.getOneOfAttributeValues(depAttribs);

                if(refAtVal == null) {
                    continue;
                }

                String chunk = childNode.makeRawSentence();

                String attrib = refAtVal[0];
                String val = refAtVal[1];

                String parts[] = val.split(":");

                String attribVal = chunk + "::" + attrib + "=" + parts[0];

                if(rels.get(attribVal) == null)
                {
                    rels.put(attribVal, 1);
                }
                else {
                    rels.put(attribVal, rels.get(attribVal) + 1);
                }

                rels.putAll(((SSFPhrase) childNode).getChunkRelationFreq());
            }
        }

        return rels;
    }

    public LinkedHashMap<String, Integer> getAttributeFreq()
    {
        LinkedHashMap<String, Integer> attribs = new LinkedHashMap();

        List<SSFNode> allChildren = getAllChildren();

        int lcount = allChildren.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode childNode = allChildren.get(j);

            List<String> attribNames = childNode.getAttributeNames();

            if(attribNames == null) {
                continue;
            }

            int acount = attribNames.size();

            for (int i = 0; i < acount; i++)
            {
                String attrib = attribNames.get(i);

                if(attribs.get(attrib) == null) {
                    attribs.put(attrib, 1);
                }
                else {
                    attribs.put(attrib, attribs.get(attrib) + 1);
                }
            }

            if(childNode instanceof SSFPhrase)
            {
                attribs.putAll(((SSFPhrase) childNode).getAttributeFreq());
            }
        }

        return attribs;
    }

    public LinkedHashMap<String, Integer> getAttributeValueFreq()
    {
        LinkedHashMap<String, Integer> attribs = new LinkedHashMap();

        List<SSFNode> allChildren = getAllChildren();

        int lcount = allChildren.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode childNode = allChildren.get(j);

            List<String> attribVals = childNode.getAttributeValues();

            if(attribVals == null) {
                continue;
            }

            int acount = attribVals.size();

            for (int i = 0; i < acount; i++)
            {
                String attrib = attribVals.get(i);

                if(attribs.get(attrib) == null) {
                    attribs.put(attrib, 1);
                }
                else {
                    attribs.put(attrib, attribs.get(attrib) + 1);
                }
            }

            if(childNode instanceof SSFPhrase)
            {
                attribs.putAll(((SSFPhrase) childNode).getAttributeValueFreq());
            }
        }

        return attribs;
    }

    public LinkedHashMap<String, Integer> getAttributeValuePairFreq()
    {
        LinkedHashMap<String, Integer> attribs = new LinkedHashMap();

        List<SSFNode> allChildren = getAllChildren();

        int lcount = allChildren.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode childNode = allChildren.get(j);

            List<String> attribVals = childNode.getAttributeValuePairs();

            if(attribVals == null) {
                continue;
            }

            int acount = attribVals.size();

            for (int i = 0; i < acount; i++)
            {
                String attrib = attribVals.get(i);

                if(attribs.get(attrib) == null) {
                    attribs.put(attrib, 1);
                }
                else {
                    attribs.put(attrib, attribs.get(attrib) + 1);
                }
            }

            if(childNode instanceof SSFPhrase)
            {
                attribs.putAll(((SSFPhrase) childNode).getAttributeValuePairFreq());
            }
        }

        return attribs;
    }

    public LinkedHashMap<String, Integer> getUnchunkedWordFreq()
    {
        LinkedHashMap<String, Integer> tags = new LinkedHashMap();

        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int lcount = leaves.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode leafNode = (SSFNode) leaves.get(j);

            if(leafNode instanceof SSFLexItem)
            {
                String lexData = leafNode.getLexData();

                if(leafNode.getParent() != null && leafNode.getParent().getParent() == null)
                {
                    if(tags.get(lexData) == null) {
                        tags.put(lexData, 1);
                    }
                    else {
                        tags.put(lexData, tags.get(lexData) + 1);
                    }
                }
            }
        }

        return tags;
    }

    public void reallocatePositions(String positionAttribName, String nullWordString)
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int lcount = leaves.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode leafNode = (SSFNode) leaves.get(j);

//            if(leafNode instanceof SSFLexItem && leafNode.getLexData().equalsIgnoreCase(nullWordString))
//            {
                leafNode.setAttributeValue(positionAttribName, "" + (j + 1) * 10);
//            }
        }
    }

    public void convertEncoding(SanchayEncodingConverter encodingConverter, String nullWordString)
    {
        List<SanchayMutableTreeNode> leaves = getAllLeaves();

        int lcount = leaves.size();

        for (int j = 0; j < lcount; j++)
        {
            SSFNode leafNode = (SSFNode) leaves.get(j);

            if(leafNode instanceof SSFLexItem && leafNode.getLexData().equalsIgnoreCase(nullWordString) == false)
            {
                String convertedLexData = encodingConverter.convert(leafNode.getLexData());
                leafNode.setLexData(convertedLexData);

                String convertedLex = encodingConverter.convert(leafNode.getAttributeValue("lex"));

                if(convertedLex != null && convertedLex.equals("") == false) {
                    leafNode.setAttributeValue("lex", convertedLex);
                }

                String convertedTAM = encodingConverter.convert(leafNode.getAttributeValue("tam"));

                if(convertedTAM != null && convertedTAM.equals("") == false) {
                    leafNode.setAttributeValue("tam", convertedTAM);
                }

                String convertedVib = encodingConverter.convert(leafNode.getAttributeValue("vib"));

                if(convertedVib != null && convertedVib.equals("") == false) {
                    leafNode.setAttributeValue("vib", convertedVib);
                }

                String convertedName = encodingConverter.convert(leafNode.getAttributeValue("name"));

                if(convertedName != null && convertedName.equals("") == false) {
                    leafNode.setAttributeValue("name", convertedName);
                }
            }
        }
    }

    @Override
    public DOMElement getDOMElement() {
        DOMElement domElement = super.getDOMElement();

        int count = countChildren();

        for (int i = 0; i < count; i++)
        {
            SSFNode child = getChild(i);

            DOMElement idomElement = child.getDOMElement();

            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public DOMElement getTypeCraftDOMElement() {
        DOMElement domElement = super.getTypeCraftDOMElement();

        int count = countChildren();

        for (int i = 0; i < count; i++)
        {
            SSFNode child = getChild(i);

            DOMElement idomElement = child.getTypeCraftDOMElement();

            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public DOMElement getGATEDOMElement() {
        DOMElement domElement = super.getGATEDOMElement();

        int count = countChildren();

        for (int i = 0; i < count; i++)
        {
            SSFNode child = getChild(i);

            DOMElement idomElement = child.getGATEDOMElement();

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

        super.readXML(domElement);

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                String tag = xmlProperties.getProperties().getPropertyValue("nodeTag");

                if(element.getTagName().equals(tag))
                {
                    SSFNode child = null;

                    if(XMLUtils.hasChileNode(element, tag))
                    {
                        try {
                            child = new SSFPhrase("0", "", "NP", "");

                            child.readXML(element);

                            addChild(child);
                        } catch (Exception ex) {
                            Logger.getLogger(SSFPhrase.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else
                    {
                        try {
                            child = new SSFLexItem("0", "", "NN", "");

                            child.readXML(element);

                            addChild(child);
                        } catch (Exception ex) {
                            Logger.getLogger(SSFLexItem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readTypeCraftXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        super.readTypeCraftXML(domElement);

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

                String tag = xmlProperties.getProperties().getPropertyValue("tcMorphemeTag");

                if(element.getTagName().equals(tag))
                {
                    SSFNode child;

                    try {
                        child = new SSFLexItem("0", "", "NN", "");

                        child.readTypeCraftXML(element);

                        addChild(child);
                    } catch (Exception ex) {
                        Logger.getLogger(SSFLexItem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                tag = xmlProperties.getProperties().getPropertyValue("tcPOSTag");

                if(element.getTagName().equals(tag))
                {
                    setName(element.getTextContent().trim());
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public void readGATEXML(Element domElement) {
        XMLProperties xmlProperties = SSFNode.getXMLProperties();

        super.readGATEXML(domElement);

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

                String tag = xmlProperties.getProperties().getPropertyValue("tcMorphemeTag");

                if(element.getTagName().equals(tag))
                {
                    SSFNode child;

                    try {
                        child = new SSFLexItem("0", "", "NN", "");

                        child.readGATEXML(element);

                        addChild(child);
                    } catch (Exception ex) {
                        Logger.getLogger(SSFLexItem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                tag = xmlProperties.getProperties().getPropertyValue("tcPOSTag");

                if(element.getTagName().equals(tag))
                {
                    setName(element.getTextContent().trim());
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

    public static void main(String[] args)
    {
        SSFPhrase node = new SSFPhrase();
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

        FeatureStructuresImpl.setFSProperties(fsp);
        SSFNode.setSSFProperties(ssfp);

        System.out.println(GlobalProperties.getIntlString("Testing_SSFPhrase..."));

        try
        {
            fsp.read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                    GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                    GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
            ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
            node.readFile("/home/anil/tmp/ssf-sentence-1.txt", GlobalProperties.getIntlString("UTF-8"));
            System.out.println(node.makeString());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
