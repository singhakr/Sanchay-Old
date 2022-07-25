package sanchay.speech.decoder.connected;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.speech.common.*;

public class ConTrellisColumn {
	private StringNode stringNode; // data
	private Vector trellisNodes; // model
	private PathNode bestEndNode;
	
	public ConTrellisColumn()
	{
		trellisNodes = new Vector();
		stringNode = null;
		bestEndNode = null;
	}

	public StringNode getStringNode()
	{
		return stringNode;
	}

	public void setStringNode(StringNode n)
	{
		stringNode = n;
	}

	public int countNodes()
	{
		return trellisNodes.size();
	}

	public TrellisNode getNode(int num) 
	{
		return (TrellisNode) trellisNodes.get(num);
	}

	public int addNode(TrellisNode n) 
	{
		trellisNodes.add(n);
		return trellisNodes.size();
	}

	public TrellisNode removeNode(int num) 
	{
		return (TrellisNode) trellisNodes.remove(num);
	}
	
	public void fillColumn(int num) 
	{
		clear();
		
		for(int i = 0; i < num; i++)
			addNode(new TrellisNode());
	}

	public PathNode getBestEndNode()
	{
		return bestEndNode;
	}

	public void setBestEndNode(PathNode n)
	{
		bestEndNode = n;
	}

	public void clear() 
	{
		trellisNodes.clear();
	}

	public void write(PrintStream p) 
	{
		int i;

		if(p == null) p = System.out;

		p.println("((\n" + (Feature) stringNode.getFeature() + "))\n");
		
		for(i = 0; i < trellisNodes.size(); i++)
		{
			p.println((Feature) ((TrellisNode) trellisNodes.get(i)).getStringNode().getFeature());
			p.println(((TrellisNode) trellisNodes.get(i)).getPathCost());
		}
		
		p.println(GlobalProperties.getIntlString("Best_End_Node:_[Data_Feature:_") + bestEndNode.getDataNode().getFeature()
			+ GlobalProperties.getIntlString(",_Model_Feature:_") + bestEndNode.getModelNode().getStringNode().getFeature()
			+ GlobalProperties.getIntlString(",_Cost:_") + bestEndNode.getModelNode().getPathCost()
			+ GlobalProperties.getIntlString(",_Model:_") + bestEndNode.getModelIndex() + "]\n");
	}
}
