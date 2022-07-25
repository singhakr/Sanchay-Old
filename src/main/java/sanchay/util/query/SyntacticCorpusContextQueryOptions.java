/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.util.query;

import java.util.Vector;

/**
 *
 * @author ambati
 */
public class SyntacticCorpusContextQueryOptions {
    
    public SyntacticCorpusQueryOptions thisNodeOptions;

    public Vector prevNodeOptions;
    public Vector nextNodeOptions;
    public Vector parentNodeOptions;
    public Vector childNodeOptions;

    public SyntacticCorpusContextQueryOptions()
    {
        thisNodeOptions = new SyntacticCorpusQueryOptions();
        prevNodeOptions = new Vector();
        nextNodeOptions = new Vector();
        parentNodeOptions = new Vector();
        childNodeOptions = new Vector();
    }

    public SyntacticCorpusQueryOptions getThisNodeOptions()
    {
        return thisNodeOptions;
    }

    public Vector getPrevNodeOptions()
    {
        return prevNodeOptions;
    }

    public Vector getNextNodeOptions()
    {
        return nextNodeOptions;
    }
    
    public Vector getParentNodeOptions()
    {
        return parentNodeOptions;
    }

    public Vector getChildNodeOptions()
    {
        return childNodeOptions;
    }
}
