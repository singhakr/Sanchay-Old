/*
 * DictionaryFSTNode.java
 *
 * Created on April 3, 2006, 7:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package sanchay.text;

import java.io.*;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JFrame;

import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.*;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.table.SanchayTableModel;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.tree.gui.SanchayTreeViewerJPanel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class DictionaryFSTNode implements Serializable
{

    protected String string;
    protected boolean isRev;
    protected FeatureStructure featureStructure;
    // Keys will starting character and the values the DictionaryFSTNode
    protected Hashtable fst;
    protected DictionaryFSTNode parent; // Not to be stored, rather to be assigned on loading
    protected long flags;
    protected double fuzzyMatchThreshold;
    // Surana - Changed from protected to public
//    public static final long EOAKSHAR = 0x00000001;
//    public static final long EOMORPHEME = 0x00000002;
//    public static final long EOWORD = 0x00000004;
    public static final long LETTER_BASED = 1;
    public static final long EOAKSHAR = 2;
    public static final long EOMORPHEME = 4;
    public static final long EOWORD = 8;

    /** Creates a new instance of DictionaryFSTNode */
    public DictionaryFSTNode()
    {
        super();

        isRev = false;
        featureStructure = new FeatureStructureImpl();
    }

    public DictionaryFSTNode(boolean rev)
    {
        super();

        isRev = rev;
        featureStructure = new FeatureStructureImpl();
    }

    public DictionaryFSTNode(boolean rev, boolean letterBased)
    {
        super();

        if (letterBased)
        {
            flags |= LETTER_BASED;
        }

        isRev = rev;
        featureStructure = new FeatureStructureImpl();
    }

    public FeatureStructure getFeatureStructure()
    {
        return featureStructure;
    }

    public String getStringFS()
    {
        return featureStructure.makeString();
    }

    public void setFeatureStructure(FeatureStructure f)
    {
        featureStructure = f;
    }

    public String getString()
    {
        return string;
    }

    public long getFlags()
    {
        return flags;
    }

    public void addFlag(long f)
    {
        flags |= f;
    }

    public DictionaryFSTNode getParent()
    {
        return this.parent;
    }

    public boolean isLeaf()
    {
        if (fst == null)
        {
            return true;
        } else
        {
            return fst.isEmpty();
        }
    }

    public boolean isWordEnd()
    {
        return UtilityFunctions.flagOn(flags, DictionaryFSTNode.EOWORD); 
    }

    public int getLevel()
    {
        int level = 1;

        DictionaryFSTNode prnt = parent;

        while (prnt != null)
        {
            level++;
            prnt = prnt.getParent();
        }

        return level;
    }

    public int countChildren()
    {
        if (fst == null)
        {
            return 0;
        }

        return fst.size();
    }

    public static int countDescendents(DictionaryFSTNode n)
    {
        int c = n.countChildren();

        Enumeration enm = n.getChildren();

        while (enm != null && enm.hasMoreElements())
        {
            DictionaryFSTNode ch = (DictionaryFSTNode) n.getChild((String) enm.nextElement());
            c += countDescendents(ch);
        }

        return c;
    }

    public Enumeration getChildren()
    {
        if (fst == null)
        {
            return null;
        }

        return fst.keys();
    }

    public DictionaryFSTNode getChild(String key)
    {
        if (fst == null)
        {
            return null;
        }

        return (DictionaryFSTNode) fst.get(key);
    }

    protected void setString(String s)
    {
        string = s;
    }

    public boolean isReverse()
    {
        return isRev;
    }

    public void compile(String word, DictionaryFSTNode parentNode, long freq, String fsStr)
    {
        if (word == null || word.length() == 0)
        {
            return;
        }

        char c = word.charAt(0);
        int len;

        if (fst == null)
        {
            fst = new Hashtable(0, 3);
        }

        String parentCharacter = new String(new char[]
                {
                    c
                });

        if (parentCharacter.equals(""))
        {
            parentCharacter = "?";
        }

        DictionaryFSTNode parent = (DictionaryFSTNode) fst.get(parentCharacter);

        if (parent == null)
        {
            parent = new DictionaryFSTNode(isRev, true);
            fst.put(parentCharacter, parent);
        }
        //fst.

        parent.setString(parentCharacter);
        //len =

        parent.parent = parentNode;

        if (word.length() > parentCharacter.length())
        {
            parent.compile(word.substring(parentCharacter.length()), parent, freq, fsStr);
        } else
        {
            //parent.compile(" ");
            parent.flags |= DictionaryFSTNode.EOWORD;

            if (freq > 0 || (fsStr != null && fsStr.equals("") == false))
            {
                try
                {
                    if (fsStr != null && fsStr.equals("") == false)
                    {
                        parent.featureStructure.readString(fsStr);
                    }

                    if (freq > 0)
                    {
                        FeatureAttribute fa = new FeatureAttributeImpl();
                        FeatureValue fv = new FeatureValueImpl();
                        fv.setValue(new Long(freq));
                        fa.setName("freq");
                        fa.addAltValue(fv);
                        parent.featureStructure.addAttribute(fa);
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*    public void compileAkshar(String word, AksharData data)
    {
    if(word == null || word.length() == 0)
    return;

    //char c = word.charAt(0);
    int len;

    if(fst == null)
    fst = new Hashtable(0, 3);

    String parentCharacter = data.getAkshar(word);

    DictionaryFSTNode parent = (DictionaryFSTNode) fst.get(parentCharacter);

    if(parent == null)
    {
    parent = new DictionaryFSTNode(isRev);
    fst.put(parentCharacter, parent);

    }
    //fst.

    parent.setString(parentCharacter);
    //len =

    if(word.length() > parentCharacter.length())
    parent.compileAkshar(word.substring(parentCharacter.length()), data);
    else
    //parent.compile(" ");
    parent.flags = parent.EOWORD;
    } */
    public void compileAlternate(String word, DictionaryFSTNode parentNode)
    {
        if (word == null || word.length() == 0)
        {
            return;
        }

        //char c = word.charAt(0);
        int len;

        if (fst == null)
        {
            fst = new Hashtable(0, 3);
        }

        String parentCharacter = word.substring(0, 1);

        if (parentCharacter.equals(""))
        {
            parentCharacter = "?";
        }
        DictionaryFSTNode parent = (DictionaryFSTNode) fst.get(parentCharacter);

        if (parent == null)
        {
            parent = new DictionaryFSTNode(isRev);
            fst.put(parentCharacter, parent);

        }
        //fst.

        parent.setString(parentCharacter);
        //len =

        parent.parent = parentNode;

        if (word.length() > parentCharacter.length())
        {
            parent.compileAlternate(word.substring(1), parent);
        } else
        //parent.compile(" ");
        {
            parent.flags = DictionaryFSTNode.EOWORD;
        }
    }

    public void compileAkshar(String word, AksharData data, DictionaryFSTNode parentNode)
    {
        if (word == null || word.length() == 0)
        {
            return;
        }

        //char c = word.charAt(0);
        int len;

        if (fst == null)
        {
            fst = new Hashtable(0, 3);
        }

        String parentCharacter = data.getAkshar(word);

        if (parentCharacter.equals(""))
        {
            parentCharacter = "?";
        }
        DictionaryFSTNode parent = (DictionaryFSTNode) fst.get(parentCharacter);

        if (parent == null)
        {
            parent = new DictionaryFSTNode(isRev);
            fst.put(parentCharacter, parent);

        }
        //fst.

        parent.setString(parentCharacter);
        //len =

        parent.parent = parentNode;

        if (word.length() > parentCharacter.length())
        {
            parent.compileAkshar(word.substring(parentCharacter.length()), data, parent);
        } else
        //parent.compile(" ");
        {
            parent.flags = DictionaryFSTNode.EOWORD;
        }
    }

    public void compileAkshar(String word, AksharData data, DictionaryFSTNode parentNode, long freq, String fsStr)
    {
        if (word == null || word.length() == 0)
        {
            return;
        }

        //char c = word.charAt(0);
        int len;

        if (fst == null)
        {
            fst = new Hashtable(0, 3);
        }

        String parentCharacter = data.getAkshar(word);

        if (parentCharacter.equals(""))
        {
            parentCharacter = "?";
        }

        DictionaryFSTNode parent = (DictionaryFSTNode) fst.get(parentCharacter);

        if (parent == null)
        {
            parent = new DictionaryFSTNode(isRev, false);
            fst.put(parentCharacter, parent);

        }
        //fst.

        parent.setString(parentCharacter);
        //len =

        parent.parent = parentNode;

        if (word.length() > parentCharacter.length())
        {
            parent.compileAkshar(word.substring(parentCharacter.length()), data, parent, freq, fsStr);
        } else
        {
            //parent.compile(" ");
            parent.flags |= DictionaryFSTNode.EOWORD;

            if (freq > 0 || (fsStr != null && fsStr.equals("") == false))
            {
                try
                {
                    if (fsStr != null && fsStr.equals("") == false)
                    {
                        featureStructure.readString(fsStr);
                    }

                    if (freq > 0)
                    {
                        FeatureAttribute fa = new FeatureAttributeImpl();
                        FeatureValue fv = new FeatureValueImpl();
                        fv.setValue(new Long(freq));
                        fa.setName("freq");
                        fa.addAltValue(fv);
                        featureStructure.addAttribute(fa);
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void compileFC(Vector vec, DictionaryFSTNode parentNode, String fsStr)
    {
        if (vec.isEmpty())
        {
            return;
        }

//	char c = word.charAt(0);
        int len;

        if (fst == null)
        {
            fst = new Hashtable(0, 3);
        }
        String parentCharacter = vec.elementAt(0).toString();

        if (parentCharacter.equals(""))
        {
            parentCharacter = "?";
        }

        DictionaryFSTNode parent = (DictionaryFSTNode) fst.get(parentCharacter);
        if (parent == null)
        {
            parent = new DictionaryFSTNode(isRev, true);
            fst.put(parentCharacter, parent);
        }
        //fst.

        parent.setString(parentCharacter);
        //len =

        parent.parent = parentNode;
        int parentSize = parentCharacter.split(" ").length;
        if (vec.size() > parentSize)
        {
            vec.remove(0);
            parent.compileFC(vec, parent, fsStr);
        } else
        {
            //parent.compile(" ");
            parent.flags |= DictionaryFSTNode.EOWORD;
            try
            {
                parent.featureStructure.readString(fsStr);

                FeatureAttribute fa = new FeatureAttributeImpl();
                FeatureValue fv = new FeatureValueImpl();
                fv.setValue(1.0);
                fa.setName("freq");
                fa.addAltValue(fv);
                parent.featureStructure.addAttribute(fa);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
    }

    public void printTable(PrintStream ps)
    {
        if (fst == null)
        {
            return;
        }

        if ((flags & EOWORD) > 0)
        {
            ps.print(getWordString() + "\t");

            if (featureStructure == null)
            {
                ps.println("0");
            } else
            {
                if (featureStructure.countAttributes() > 0)
                {
                    FeatureAttribute fa = featureStructure.getAttribute("freq");

                    if (fa != null && fa.countAltValues() > 0)
                    {
                        FeatureValue fv = fa.getAltValue(0);

                        if (fv.getValue().getClass().equals(Long.class))
                        {
                            ps.print(fv.toString());
                        } else
                        {
                            ps.print("0");
                        }
                    } else
                    {
                        ps.print("0");
                    }

                    ps.println("\t" + featureStructure.makeString());
                } else
                {
                    ps.println("0");
                }
            }
        }

        Enumeration enm = fst.keys();

        while (enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();
            DictionaryFSTNode child = (DictionaryFSTNode) fst.get(key);

            child.printTable(ps);
        }
    }

    public void print(PrintStream ps, int tabs)
    {
        if (fst == null)
        {
            return;
        }

        String tabStr = "";

        for (int i = 0; i < tabs; i++)
        {
            tabStr += "\t";
        }

        Enumeration enm = fst.keys();

        while (enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();
            DictionaryFSTNode child = (DictionaryFSTNode) fst.get(key);

//	    ps.print(tabStr + child.getString() + " ");
            ps.print(child.getString());
//	    featureStructure.print(ps);
            ps.print(";");

            child.print(ps, tabs + 1);
        }
    }

    public void compressStrings()
    {
        if (fst == null || fst.keys() == null)
        {
            return;
        }

        while (fst != null && fst.keys() != null && fst.size() == 1)
        {
            Enumeration enm = fst.keys();
            String key = (String) enm.nextElement();

            DictionaryFSTNode child = (DictionaryFSTNode) fst.get(key);

            string += child.string;
            fst = child.fst;
            featureStructure = child.featureStructure;
        }

        if (fst == null || fst.keys() == null)
        {
            return;
        }

        Enumeration enm = fst.keys();

        while (enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();
            DictionaryFSTNode child = (DictionaryFSTNode) fst.get(key);
            child.compressStrings();
        }
    }

    public void expandStrings()
    {
        if (fst == null || fst.keys() == null)
        {
            return;
        }

        Enumeration enm = fst.keys();
        Hashtable remove = new Hashtable();

        while (enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();

            if(remove.get(key) != null)
                continue;

            DictionaryFSTNode child = (DictionaryFSTNode) fst.get(key);

            if(key.length() > 1)
            {
                remove.put(key, "");

                int count = key.length();

                Hashtable expandedFST = new Hashtable(0, 3);
                
                for (int i = 0; i < count; i++)
                {
                    DictionaryFSTNode expandedChild = new DictionaryFSTNode(isRev);
                    String expandedKey = key.charAt(i) + "";
                    expandedChild.setString(expandedKey);
                    expandedFST.put(expandedKey, expandedChild);

                    if(expandedFST == null)
                        expandedFST = new Hashtable(0, 3);
                    
                    expandedChild.fst = expandedFST;

                    if(i == count - 1)
                    {
                        expandedChild.setFeatureStructure(child.getFeatureStructure());

                        if(child.fst != null)
                            expandedChild.fst.putAll(child.fst);
                        else
                            expandedChild.fst = null;

                        child.expandStrings();
                    }
                }
            }
            else
                child.expandStrings();
        }

        Enumeration rmEnm = remove.keys();

        while(rmEnm.hasMoreElements())
        {
            fst.remove(rmEnm.nextElement());
        }
    }

    public void storeObject(java.io.ObjectOutputStream out)
            throws IOException
    {
        if (string == null || string.equals("") || string.length() == 0)
        {
            out.writeUTF("");
        } else
        {
            out.writeUTF(string);
        }

        out.writeLong(flags);

        if (featureStructure != null && featureStructure.makeString() != null && featureStructure.makeString().equals("") == false && featureStructure.makeString().length() > 0)
        {
            String fsString = featureStructure.makeString();
            out.writeUTF(fsString);
        } else
        {
            out.writeUTF("");
        }

        if (fst == null || fst.size() == 0 || fst.keys() == null)
        {
            out.writeLong(0);
        } else
        {
            out.writeLong(fst.size());

            Enumeration enm = fst.keys();

            while (enm.hasMoreElements())
            {
                String key = (String) enm.nextElement();
                DictionaryFSTNode child = (DictionaryFSTNode) fst.get(key);
//		out.writeChars(key);
                child.storeObject(out);
//		out.writeObject(child);
            }
        }
    }

    public void loadObject(java.io.ObjectInputStream in, DictionaryFSTNode prnt)
            throws IOException, ClassNotFoundException
    {
        string = in.readUTF();
//	for (int i = 0; i < count; i++)
//	    string += (char) in.readByte();

//	count = in.readShort();

        flags = in.readLong();

        String fsString = "";
//	for (int i = 0; i < count; i++)
//	    fsString += (char) in.readByte();

        fsString = in.readUTF();

        if (fsString != null && fsString.equals("") == false && fsString.length() > 0)
        {
            featureStructure = new FeatureStructureImpl();
            try
            {
                featureStructure.readString(fsString);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        long count = in.readLong();
        fst = new Hashtable(0, 3);

        for (long i = 0; i < count; i++)
        {
            DictionaryFSTNode child = new DictionaryFSTNode(isRev);
            child.loadObject(in, this);
//	    DictionaryFSTNode child = (DictionaryFSTNode) in.readObject();
            fst.put(child.getString(), child);
        }

        parent = prnt;
    }

//    public LinkedHashMap getNearestWords(String str, int nearest)
//    {
//        LinkedHashMap nearestMatches = new LinkedHashMap(nearest, nearest);
//
//        SimilarityTraversalData similarityTraversalData = new SimilarityTraversalData(str, (short) nearest, nearestMatches, (short) 0, 0.0f);
//
//        getNearestWords(similarityTraversalData);
//
//        return similarityTraversalData.nearestMatches;
//    }
//
//    protected void getNearestWords(SimilarityTraversalData similarityTraversalData)
//    {
//        if (similarityTraversalData.level > 0)
//        {
//        }
//
////        if(similarityTraversalData.score <= fuzzyMatchThreshold
////                && (similarityTraversalData.str.equals("") || countChildren() == 0))
////            similarityTraversalData
//    }

    public void showTreeView(String langEnc, String rootStr)
    {
        JDialog realTreeDialog = null;

        JDialog dialog = null;
        JFrame owner = null;

        if (dialog != null)
        {
            realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Tree_Viewer"), true);
        } else
        {
            realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Tree_Viewer"), true);
        }

        DictionaryFSTMutableNode rootNode = new DictionaryFSTMutableNode(this, rootStr);
//        rootNode.setUserObject(rootStr);
//        rootNode.getDictionaryFSTNode().setString(rootStr);

        SanchayTreeViewerJPanel realTreeJPanel = null;

        if (isRev)
        {
            realTreeJPanel = new SanchayTreeViewerJPanel(rootNode, SanchayMutableTreeNode.DICT_FST_MODE, langEnc, true);
        } else
        {
            realTreeJPanel = new SanchayTreeViewerJPanel(rootNode, SanchayMutableTreeNode.DICT_FST_MODE, langEnc);
        }

        realTreeJPanel.setDialog(realTreeDialog);
        realTreeJPanel.setColumnClasses();

        SanchayTableModel model = new SanchayTableModel(new String[]
                {
                    GlobalProperties.getIntlString("Feature"), GlobalProperties.getIntlString("Value")
                }, 10);
//        SanchayTableCellEditor editor = new SanchayTableCellEditor(realTreeDialog, langEnc, "Affix Editor", SanchayTableCellEditor.DICTIONARY_FST_MODE);
//        realTreeJPanel.setDefaultNodeEditor(DictionaryFSTMutableNode.class, editor);

        realTreeDialog.add(realTreeJPanel);
        realTreeJPanel.sizeToFit();

        realTreeDialog.setVisible(true);
    }

    // Is it needed?
    public void showTreeJPanel(String langEnc, String rootStr)
    {
        JDialog realTreeDialog = null;

        JDialog dialog = null;
        JFrame owner = null;

        if (dialog != null)
        {
            realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Tree_Viewer"), true);
        } else
        {
            realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Tree_Viewer"), true);
        }

        DictionaryFSTMutableNode rootNode = new DictionaryFSTMutableNode(this, rootStr);
//        rootNode.setUserObject(rootStr);
//        rootNode.getDictionaryFSTNode().setString(rootStr);

        SanchayTreeJPanel realTreeJPanel = SanchayTreeJPanel.createDefaultTreeJPanel(rootNode, langEnc);

        realTreeJPanel.setDialog(realTreeDialog);

        realTreeDialog.add(realTreeJPanel);
        realTreeDialog.setBounds(80, 30, 900, 700);

        realTreeDialog.setVisible(true);
        realTreeJPanel.treeJTree.collapseRow(0);
    //realTreeJPanel.collapseAll(null);

    }

    public void getNodesForPrefix(String pre, Vector matches)
    {
        if (pre == null || pre.equals(""))
        {
            return;
        }

        if (countChildren() <= 0)
        {
            return;
        }

        Enumeration enm = getChildren();

        while (enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();
            DictionaryFSTNode ch = getChild(key);

            if (pre.equals(key))
            {
                matches.add(ch);
            } else if (pre.startsWith(key))
            {
                ch.getNodesForPrefix(pre.substring(key.length()), matches);
            }
        }
    }

    public String getWordString()
    {
        String wrd = getString();

        DictionaryFSTNode prnt = parent;

        while (prnt != null)
        {
            wrd = prnt.getString() + wrd;

            prnt = prnt.getParent();
        }

        return wrd;
    }

    public void getAllWords(Vector<DictionaryFSTNode> words)
    {
        Enumeration enm = getChildren();

        if (isWordEnd())
        {
            words.add(this);
        }

        while (enm != null && enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();
            DictionaryFSTNode ch = getChild(key);

            ch.getAllWords(words);
        }
    }

    public void getAllLeafNodes(Vector<DictionaryFSTNode> nodes)
    {
        if (isLeaf())
        {
            nodes.add(this);
            return;
        }

        Enumeration enm = getChildren();

        while (enm != null && enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();
            DictionaryFSTNode ch = getChild(key);

            ch.getAllLeafNodes(nodes);
        }
    }

    public String toString()
    {
        return getString();
    }

    public static void main(String[] args)
    {
        DictionaryFSTNode tree = new DictionaryFSTNode();

        tree.compileAlternate(GlobalProperties.getIntlString("one"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("two"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("three"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("four"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("five"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("six"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("seven"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("seventy"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("eight"), null);
        tree.compileAlternate(GlobalProperties.getIntlString("eighty"), null);

        Vector<DictionaryFSTNode> words = new Vector<DictionaryFSTNode>();
        tree.getAllWords(words);

        int count = words.size();

        for (int i = 0; i < count; i++)
        {
            System.out.println(words.get(i).getWordString() + " " + words.get(i).getLevel());
        }

//        UtilityFunctions.printVector(words, System.out);
    }
}
