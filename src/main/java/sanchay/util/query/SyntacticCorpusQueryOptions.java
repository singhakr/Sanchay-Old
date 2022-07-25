/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.util.query;

import java.util.SortedMap;
import java.util.Vector;

/**
 *
 * @author ambati
 */
public class SyntacticCorpusQueryOptions {
    
    public String nodeData;
    public boolean nodeDataB;
    public String nodeTag;
    public boolean nodeTagB;
    public String nodeDisWin;
    public boolean nodeDisWinB;
    public Vector nodeAttr;
    public Vector nodeVal;
    public Vector nodeAttrB;

    public String andOr1;
    public String tag;
    public String andOr2;
    public String feature;
    public String andOr3;
    public String featureValue;
    public boolean treeView;

    /** Creates a new instance of SyntacticCorpusQueryOptions */
    public SyntacticCorpusQueryOptions()
    {
        nodeData="[.]*";
        nodeDataB=true;
        nodeTag="[.]*";
        nodeTagB=true;
        nodeDisWin="[.]*";
        nodeDisWinB=true;
        nodeAttr = new Vector();
        nodeAttrB = new Vector();
        nodeVal = new Vector();
        
        andOr1 = "Or";
        tag = "";
        andOr2 = "Or";
        feature = "";
        andOr3 = "Or";
        featureValue = "";
        treeView = false;
    }
    
    public void setLexData(String data, boolean bool)
    {
        nodeData = data;
        nodeDataB = bool;
    }
    
    public void setTag(String tag, boolean bool)
    {
        nodeTag = tag;
        nodeTagB = bool;
    }

    public void setDisWin(String diswin, boolean bool)
    {
        nodeDisWin = diswin;
        nodeDisWinB = bool;
    }

    public void setAttrVal(String attr, String val, boolean bool)
    {
        int index = nodeAttr.indexOf(attr);
        if(index == -1)
        {
            nodeAttr.add(index,attr);
            nodeVal.add(index, val);
            nodeAttrB.add(index,bool);
        }
        else
        {
            String value = (String) nodeVal.get(index);
            nodeVal.add(value+"|"+val);
        }
        
    }
    
    public String getLexData()
    {
        return nodeData;
    }

    public String getTag()
    {
        return nodeTag;
    }

    public String getDisWin()
    {
        return nodeDisWin;
    }

    public boolean getLexDataB()
    {
        return nodeDataB;
    }

    public boolean getTagB()
    {
        return nodeTagB;
    }

    public boolean getDisWinB()
    {
        return nodeDisWinB;
    }

    public String getAttrValue(String attr)
    {
        int index = nodeAttr.indexOf(attr);
        if(index != -1)
        {
            return (String) nodeAttr.get(index);
        }
        
        else
        {
            return "ERR";
        }
    }

    public boolean getAttrValueB(String attr)
    {
        int index = nodeAttr.indexOf(attr);
        if(index != -1)
        {
            String bool = (String) nodeAttrB.get(index);
            if(bool.equals("true"))
                return true;
            else
                return false;
        }

        else
        {
            return false;
        }
    }
}
