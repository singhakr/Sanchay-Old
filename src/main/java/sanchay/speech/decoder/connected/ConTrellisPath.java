package sanchay.speech.decoder.connected;

import java.io.*;
import java.util.*;

import sanchay.speech.common.*;

public class ConTrellisPath {
	private double cost;
	private Vector pathNodes; // array of trellis nodes

	public ConTrellisPath()
	{
		pathNodes = new Vector();
		cost = RecogProps.INF_COST;
	}
	
	public double getCost()
	{
		return cost;
	}

	public void setCost(double c)
	{
		cost = c;
	}
	
	public int countPathNodes()
	{
		return pathNodes.size();
	}

	public PathNode getPathNode(int num) 
	{
		return (PathNode) pathNodes.get(num);
	}

	public int addPathNode(PathNode n) 
	{
		pathNodes.add(n);
		return pathNodes.size();
	}

	public PathNode removePathNode(int num) 
	{
		return (PathNode) pathNodes.remove(num);
	}
	
	public void write(PrintStream p) 
	{
		int i = 0;

		if(p == null) p = System.out;

		for(i = 0; i < pathNodes.size(); i++)
		{
			p.println("((");
			p.println(((PathNode) pathNodes.get(i)).getDataNode().getFeature());
			p.println(((PathNode) pathNodes.get(i)).getModelNode().getStringNode().getFeature());
			p.println(((PathNode) pathNodes.get(i)).getModelNode().getPathCost());
			p.println("))");
		}
	}
}
