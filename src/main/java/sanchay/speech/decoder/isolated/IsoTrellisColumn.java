package sanchay.speech.decoder.isolated;

import java.io.*;
import java.util.*;

import sanchay.speech.common.*;

public class IsoTrellisColumn {
	private StringNode stringNode; // data
	private Vector trellisNodes; // model
	
	public IsoTrellisColumn()
	{
		stringNode = null;
		trellisNodes = new Vector();
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

	public void clear() 
	{
		trellisNodes.clear();
	}
	
	public TrellisNode getBestScore()
	{
		int i;
		double cst = RecogProps.INF_COST;
		TrellisNode best = new TrellisNode();

		for(i = 0; i < countNodes(); i++)
		{
			if(getNode(i).getPathCost() <= cst)
			{
				cst = getNode(i).getPathCost();
				best.copy(getNode(i));
			}
		}

		return best;
	}

	public void write(PrintStream p) 
	{
		int i;

		if(p == null) p = System.out;

		p.println("((\n" + (Feature) stringNode.getFeature() + "))\n");
		for(i = 0; i < trellisNodes.size(); i++)
		{
			p.println((Feature)  ((TrellisNode) trellisNodes.get(i) ).getStringNode().getFeature());
			p.println(((TrellisNode) trellisNodes.get(i) ).getPathCost());
		}
	}
}
