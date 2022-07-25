/*
 * SSFNodeComparison.java
 *
 * Created on January 26, 2006, 10:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.impl;

import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.ssf.tree.*;

import sanchay.table.*;
import sanchay.tree.SanchayMutableTreeNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class SSFCorpusAnalyzer
{
    // The first one will be taken as the reference, against which the others will be compared.
    private SSFPhrase nodes[];
    private String taskNames[];
    private String comments[];

    private SSFPhrase[] mmTrees;
    private LinkedHashMap cfgToMMTreeMappings[];

    private SanchayTableModel summaryComparison;

    private int[] overallComparison;
    private SanchayTableModel posTagComparison;
    private SanchayTableModel chunkComparison;
    private int[] mmTreeComparison;
    private SanchayTableModel lexitemFTComparison;
    private SanchayTableModel chunkFTComparison;
    private SanchayTableModel commentComparison;
    
    private boolean different[][];
    private boolean compared[];
    
    // Comparison types:
    public static final int OVERALL_ANNOTATION = 0;
    public static final int POS_TAGS = 1;
    public static final int CHUNKS = 2;
    public static final int MM_TREES = 3;
    public static final int LEXITEM_FEATURE_TABLE = 4;
    public static final int CHUNK_FEATURE_TABLE = 5;
    public static final int COMMENTS = 6;
    public static final int _COMPARISON_LAST_ = 7;
    
    /** Creates a new instance of SSFNodeComparison */
    public SSFCorpusAnalyzer(SSFPhrase nodes[], String taskNames[])
    {
	different = new boolean[nodes.length][_COMPARISON_LAST_];
	compared = new boolean[_COMPARISON_LAST_];

        cfgToMMTreeMappings = new LinkedHashMap[_COMPARISON_LAST_];
	
	this.nodes = nodes;
	this.taskNames = taskNames;
	
	comments = new String[nodes.length];
	
	for (int i = 0; i < taskNames.length; i++) {
            comments[i] = "";
        }
    }

    public SSFCorpusAnalyzer(SSFPhrase nodes[], String taskNames[], String comments[])
    {
	this(nodes, taskNames);
	
	this.comments = comments;
    }
    
    public int countNodes()
    {
	return nodes.length;
    }
    
    public SSFPhrase[] getNodes()
    {
	return nodes;
    }
    
    public String[] getComments()
    {
	return comments;
    }
    
    public SSFPhrase getNode(int i)
    {
	if(i < 0 || i >= countNodes()) {
            return null;
        }

	return nodes[i];
    }
    
    public SSFPhrase[] getMMTrees()
    {
	return mmTrees;
    }
    
    public SSFPhrase getMMTree(int i)
    {
	if(i < 0 || i >= countNodes())
	    return null;

	return mmTrees[i];
    }

    public LinkedHashMap[] getCFGToMMTreeMappings()
    {
        return cfgToMMTreeMappings;
    }

    public LinkedHashMap getCFGToMMTreeMapping(int i)
    {
	if(i < 0 || i >= countNodes()) {
            return null;
        }

	return cfgToMMTreeMappings[i];
    }
    
    private void compare(int comparisonType)
    {
	switch(comparisonType)
	{
	    case OVERALL_ANNOTATION:
		if(compared[OVERALL_ANNOTATION] == false)
		{
		    compareOverall();
		    compared[OVERALL_ANNOTATION] = true;
		}
		break;
	    case POS_TAGS:
		if(compared[POS_TAGS] == false)
		{
		    comparePOSTags();
		    compared[POS_TAGS] = true;
		}
		break;
	    case CHUNKS:
		if(compared[CHUNKS] == false)
		{
		    compareChunks();
		    compared[OVERALL_ANNOTATION] = true;
		}
		break;
	    case MM_TREES:
		if(compared[MM_TREES] == false)
		{
		    compareMMTrees();
		    compared[MM_TREES] = true;
		}
		break;
	    case LEXITEM_FEATURE_TABLE:
		if(compared[LEXITEM_FEATURE_TABLE] == false)
		{
		    compareLexItemFeatures();
		    compared[LEXITEM_FEATURE_TABLE] = true;
		}
		break;
	    case CHUNK_FEATURE_TABLE:
		if(compared[CHUNK_FEATURE_TABLE] == false)
		{
		    compareChunkFeatures();
		    compared[CHUNK_FEATURE_TABLE] = true;
		}
		break;
	    case COMMENTS:
		if(compared[COMMENTS] == false)
		{
		    compareComments();
		    compared[COMMENTS] = true;
		}
		break;
	}
    }
    
    public boolean areDifferent(int comparisonType)
    {
	if(nodes != null || nodes.length == 1) {
            return false;
        }
	
	compare(comparisonType);

	for (int i = 1; i < nodes.length; i++)
	{
	    if(isDifferent(i, comparisonType) == true) {
                return true;
            }
	}

	return false;
    }
    
    public boolean isDifferent(int nodeIndex /* except the first, reference node */, int comparisonType)
    {
	compare(comparisonType);
	
	return different[nodeIndex][comparisonType];
    }
    
    public SanchayTableModel comparisonSummary()
    {
	if(summaryComparison != null) {
            return summaryComparison;
        }

	compare(OVERALL_ANNOTATION);

	summaryComparison = newComparisonTableModel();
	
	summaryComparison.setColumnIdentifier(0, GlobalProperties.getIntlString("Comparison_Type"));
	
	for(int i = 0; i < _COMPARISON_LAST_; i++)
	{
	    summaryComparison.addRow();

	    for(int j = 1; j < nodes.length; j++)
	    {
		if(different[j][i] == false) {
                    summaryComparison.setValueAt(GlobalProperties.getIntlString("Same"), i, j + 1);
                }
		else {
                    summaryComparison.setValueAt(GlobalProperties.getIntlString("Different"), i, j + 1);
                }
	    }
	}

	summaryComparison.setValueAt(GlobalProperties.getIntlString("Overall_annotation"), OVERALL_ANNOTATION, 0);
	summaryComparison.setValueAt(GlobalProperties.getIntlString("POS_tags"), POS_TAGS, 0);
	summaryComparison.setValueAt(GlobalProperties.getIntlString("Chunks"), CHUNKS, 0);
	summaryComparison.setValueAt(GlobalProperties.getIntlString("Modifier_trees"), MM_TREES, 0);
	summaryComparison.setValueAt(GlobalProperties.getIntlString("Features_of_lexical_items_(words)"), LEXITEM_FEATURE_TABLE, 0);
	summaryComparison.setValueAt(GlobalProperties.getIntlString("Features_of_chunks"), CHUNK_FEATURE_TABLE, 0);
	summaryComparison.setValueAt(GlobalProperties.getIntlString("Comments"), COMMENTS, 0);
	
	if(nodes.length >= 1) {
            summaryComparison.removeColumn(1);
        }
	
	return summaryComparison;
    }
    
    public int[] compareOverall()
    {
	if(compared[OVERALL_ANNOTATION] == true) {
            return overallComparison;
        }

	for(int i = 1; i < nodes.length; i++)
	{
	    boolean diff = false;
	    for(int j = 1; j < _COMPARISON_LAST_; j++)
	    {
		compare(j);

		if(isDifferent(i, j) == true) {
                    diff = true;
                }
	    }

	    if(diff == true) {
                different[i][OVERALL_ANNOTATION] = true;
            }
	}

	if(nodes.length == 1) {
            overallComparison = new int[]{0};
        }
	else
	{
	    List<Integer> ocvec = new ArrayList<Integer>();
	    
	    ocvec.add(new Integer(0));
	    
	    for (int i = 1; i < nodes.length; i++)
	    {
		if(nodes[0].equals(nodes[i]) == false)
		{
		    ocvec.add(new Integer(i));
		    different[i][OVERALL_ANNOTATION] = true;
		}
	    }
	    
	    if(ocvec.size() == 1) {
                overallComparison = null;
            }
	    else
	    {
		overallComparison = new int[ocvec.size()];

		for (int i = 0; i < ocvec.size(); i++) {
                    overallComparison[i] = ((Integer) ocvec.get(i)).intValue();
                }
	    }
	}
	
	compared[OVERALL_ANNOTATION] = true;
	return overallComparison;
    }
    
    private SanchayTableModel newComparisonTableModel()
    {
	SanchayTableModel sanchayTableModel = new SanchayTableModel(0, nodes.length + 1);
	sanchayTableModel.setEditable(false);

	String colNames[] = new String[nodes.length + 1];
	colNames[0] = GlobalProperties.getIntlString("Annotation");
	
	if(taskNames == null || taskNames.length != nodes.length)
	{
	    taskNames = new String[nodes.length];
	    for(int i = 0; i < nodes.length; i++)
	    {
		taskNames[i] = GlobalProperties.getIntlString("Task-") + (i + 1);
	    }
	}
        
        System.arraycopy(taskNames, 0, colNames, 1, nodes.length);
	
	sanchayTableModel.setColumnIdentifiers(colNames);
	
	return sanchayTableModel;
    }
    
    public SanchayTableModel comparePOSTags()
    {
	if(compared[POS_TAGS] == true) {
            return posTagComparison;
        }
	
	posTagComparison = newComparisonTableModel();
	posTagComparison.setColumnIdentifier(0, GlobalProperties.getIntlString("Lexical_Item"));

	List<SanchayMutableTreeNode> lvs = null;
	int count = 0;
	
	if(nodes.length >= 1)
	{
	    lvs = nodes[0].getAllLeaves();
	    count = lvs.size();
	    
	    for (int i = 0; i < count; i++)
	    {
		posTagComparison.addRow();
		SSFLexItem sli = (SSFLexItem) lvs.get(i);
		posTagComparison.setValueAt(sli.getLexData(), i, 0);
		posTagComparison.setValueAt(sli.getName(), i, 1);
	    }
	}

	int rem = 0;
	for (int i = 1; i < nodes.length; i++)
	{
	    int tagdiff[] = nodes[0].getDifferentPOSTags(nodes[i]);

	    if(tagdiff != null)
	    {
		List<SanchayMutableTreeNode> lvsi = nodes[i].getAllLeaves();

		for (int j = 0; j < tagdiff.length; j++)
		{
		    if(tagdiff[j] >= lvsi.size()) {
                        posTagComparison.setValueAt("", tagdiff[j], i - rem + 1);
                    }
		    else
		    {
			SSFLexItem sli = (SSFLexItem) lvsi.get(tagdiff[j]);
			posTagComparison.setValueAt(sli.getName(), tagdiff[j], i - rem + 1);
		    }
		}
		
		different[i][POS_TAGS] = true;
	    }
	    else {
                posTagComparison.removeColumn(i - rem++);
            }
	}
	
	if(nodes.length > 1 && posTagComparison.getColumnCount() == 2)
	{
	    posTagComparison.removeColumn(0);
	    posTagComparison.removeColumn(0);
	}
	
	compared[POS_TAGS] = true;
	return posTagComparison;
    }
    
    public SanchayTableModel compareChunks()
    {
	if(compared[CHUNKS] == true) {
            return chunkComparison;
        }
	
	chunkComparison = newComparisonTableModel();
	chunkComparison.removeColumn(0);
	chunkComparison.setEditable(false);

	int max = 0;
	
	for(int i = 0; i < nodes.length; i++)
	{
	    if(nodes[0].countChildren() < nodes[i].countChildren()) {
                max = i;
            }
	}

	int maxcount = nodes[max].countChildren();
	for (int i = 0; i < maxcount; i++) {
            chunkComparison.addRow();
        }

	for (int j = 0; j < nodes[0].countChildren(); j++)
	{
	    SSFNode ch = nodes[0].getChild(j);
	    String chstr = "[[" + ch.makeRawSentence() + "]]" + ch.getName();
	    chunkComparison.setValueAt(chstr, j, 0);
	}
	
	int rem = 0;
	for(int i = 1; i < nodes.length; i++)
	{
	    if(nodes[0].isChunkingSame(nodes[i]) == false)
	    {
		for (int j = 0; j < nodes[i].countChildren(); j++)
		{
		    SSFNode ch = nodes[i].getChild(j);
		    String chstr = "[[" + ch.makeRawSentence() + "]]" + ch.getName();
		    chunkComparison.setValueAt(chstr, j, i - rem);
		}
		
		different[i][CHUNKS] = true;
	    }
	    else {
                chunkComparison.removeColumn(i - rem++);
            }
	}
	
	if(nodes.length > 1 && chunkComparison.getColumnCount() == 1)
	{
	    chunkComparison.removeColumn(0);
	}
	
	compared[CHUNKS] = true;
	return chunkComparison;
    }
    
    public int[] compareMMTrees()
    {
	if(compared[MM_TREES] == true) {
            return mmTreeComparison;
        }

	mmTrees = new SSFPhrase[nodes.length];

	for(int i = 0; i < nodes.length; i++)
	{
            cfgToMMTreeMappings[i] = new LinkedHashMap(0, 10);
        
	    mmTrees[i] = nodes[i].convertToGDepNode(cfgToMMTreeMappings[i]);
	}

	if(nodes.length == 1) {
            mmTreeComparison = new int[]{0};
        }
	else
	{
	    List<Integer> mcvec = new ArrayList<Integer>();
	    
	    mcvec.add(new Integer(0));
	    
	    for (int i = 1; i < nodes.length; i++)
	    {
		if(mmTrees[0] == null && mmTrees[i] == null) {
                    continue;
                }
		
		if(
		    (mmTrees[0] == null || mmTrees[i] == null)
		    || (mmTrees[0].equals(mmTrees[i]) == false)
		)
		{
		    mcvec.add(new Integer(i));
		    different[i][MM_TREES] = true;
		}
	    }
	    
	    if(mcvec.size() == 1) {
                mmTreeComparison = null;
            }
	    else
	    {
		mmTreeComparison = new int[mcvec.size()];

		for (int i = 0; i < mcvec.size(); i++) {
                    mmTreeComparison[i] = ((Integer) mcvec.get(i)).intValue();
                }
	    }
	}
	
	compared[MM_TREES] = true;
	return mmTreeComparison;
    }
    
    public SanchayTableModel compareLexItemFeatures()
    {
	if(compared[LEXITEM_FEATURE_TABLE] == true) {
            return lexitemFTComparison;
        }
	
	lexitemFTComparison = newComparisonTableModel();
	lexitemFTComparison.setColumnIdentifier(0, GlobalProperties.getIntlString("Lexical_Item"));

	List<SanchayMutableTreeNode> lvs = null;
	int count = 0;
	
	if(nodes.length >= 1)
	{
	    lvs = nodes[0].getAllLeaves();
	    count = lvs.size();
	    
	    for (int i = 0; i < count; i++)
	    {
		lexitemFTComparison.addRow();
		SSFLexItem sli = (SSFLexItem) lvs.get(i);
		lexitemFTComparison.setValueAt(sli.getLexData(), i, 0);
		
		if(sli.getFeatureStructures() != null) {
                    lexitemFTComparison.setValueAt(sli.getFeatureStructures().makeString(), i, 1);
                }
		else {
                    lexitemFTComparison.setValueAt("", i, 1);
                }
	    }
	}

	int rem = 0;
	for (int i = 1; i < nodes.length; i++)
	{
	    int ftdiff[] = nodes[0].getDifferentLexItemFeatures(nodes[i]);

	    if(ftdiff != null)
	    {
		List<SanchayMutableTreeNode> lvsi = nodes[i].getAllLeaves();

		for (int j = 0; j < ftdiff.length; j++)
		{
		    if(ftdiff[j] >= lvsi.size()) {
                        lexitemFTComparison.setValueAt("", ftdiff[j], i - rem + 1);
                    }
		    else
		    {
			SSFLexItem sli = (SSFLexItem) lvsi.get(ftdiff[j]);

			if(sli.getFeatureStructures() != null) {
                            lexitemFTComparison.setValueAt(sli.getFeatureStructures().makeString(), ftdiff[j], i - rem + 1);
                        }
			else {
                            lexitemFTComparison.setValueAt("", ftdiff[j], i - rem + 1);
                        }
		    }
		}
		
		different[i][LEXITEM_FEATURE_TABLE] = true;
	    }
	    else {
                lexitemFTComparison.removeColumn(i - rem++);
            }
	}
	
	if(nodes.length > 1 && lexitemFTComparison.getColumnCount() == 2)
	{
	    lexitemFTComparison.removeColumn(0);
	    lexitemFTComparison.removeColumn(0);
	}
	
	compared[LEXITEM_FEATURE_TABLE] = true;
	return lexitemFTComparison;
    }

    // Find out the common chunks. Then if their features are different, add them to the table.
    public SanchayTableModel compareChunkFeatures()
    {
	if(compared[CHUNK_FEATURE_TABLE] == true) {
            return chunkFTComparison;
        }

	chunkFTComparison = newComparisonTableModel();
	chunkFTComparison.setColumnIdentifier(0, GlobalProperties.getIntlString("Chunk"));

	List<SSFNode> lvs = null;
	int count = 0;
	
	if(nodes.length >= 1)
	{
	    lvs = nodes[0].getAllChildren();
	    count = lvs.size();
	    
	    for (int i = 0; i < count; i++)
	    {
		chunkFTComparison.addRow();
		SSFNode sli = (SSFNode) lvs.get(i);
		String chstr = "[[" + sli.makeRawSentence() + "]]" + sli.getName();
		chunkFTComparison.setValueAt(chstr, i, 0);

		if(sli.getFeatureStructures() != null) {
                    chunkFTComparison.setValueAt(sli.getFeatureStructures().makeString(), i, 1);
                }
		else {
                    chunkFTComparison.setValueAt("", i, 1);
                }
	    }
	}

	int rem = 0;
	for (int i = 1; i < nodes.length; i++)
	{
	    int ftdiff[] = nodes[0].getDifferentChunkFeatures(nodes[i]);

	    if(ftdiff != null)
	    {
		List<SSFNode> lvsi = nodes[i].getAllChildren();

		for (int j = 0; j < ftdiff.length; j++)
		{
		    if(ftdiff[j] >= lvsi.size()) {
                        chunkFTComparison.setValueAt("", ftdiff[j], i - rem + 1);
                    }
		    else
		    {
			SSFNode sli = (SSFNode) lvsi.get(ftdiff[j]);
			
			if(sli.getFeatureStructures() != null) {
                            chunkFTComparison.setValueAt(sli.getFeatureStructures().makeString(), ftdiff[j], i - rem + 1);
                        }
			else {
                            chunkFTComparison.setValueAt("", ftdiff[j], i - rem + 1);
                        }
		    }
		}
		
		different[i][CHUNK_FEATURE_TABLE] = true;
	    }
	    else {
                chunkFTComparison.removeColumn(i - rem++);
            }
	}
	
	if(nodes.length > 1 && chunkFTComparison.getColumnCount() == 2)
	{
	    chunkFTComparison.removeColumn(0);
	    chunkFTComparison.removeColumn(0);
	}
	
	compared[CHUNK_FEATURE_TABLE] = true;
	return chunkFTComparison;
    }
    
    public SanchayTableModel compareComments()
    {
	if(compared[COMMENTS] == true) {
            return commentComparison;
        }

	commentComparison = new SanchayTableModel(0, nodes.length + 1);
	commentComparison.setEditable(false);
	
	commentComparison.setColumnIdentifiers(new String[]{GlobalProperties.getIntlString("Task_Name"), GlobalProperties.getIntlString("Comment")});

	for(int i = 0; i < nodes.length; i++)
	{
	    commentComparison.addRow(new String[]{taskNames[i], comments[i]});
	}

	int rem = 0;
	for(int i = 1; i < nodes.length; i++)
	{
	    if(comments[0].equalsIgnoreCase(comments[i]) == false) {
                different[i][COMMENTS] = true;
            }
	    else {
                commentComparison.removeRow(i - rem++);
            }
	}
	
	if(nodes.length > 1 && commentComparison.getRowCount() == 1)
	{
	    commentComparison.removeRow(0);
	}
	
	compared[COMMENTS] = true;
	return commentComparison;
    }
}
