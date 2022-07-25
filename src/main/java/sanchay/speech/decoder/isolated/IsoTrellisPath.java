package sanchay.speech.decoder.isolated;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.speech.common.*;

public class IsoTrellisPath {
	private double cost;
	
	// The two vectors must be of the same size
	private Vector data; // array of trellis nodes
	private Vector model; // array of string node references
	
	private int modelIndex;

	public IsoTrellisPath()
	{
		data = new Vector();
		model = new Vector();
		cost = RecogProps.INF_COST;
		modelIndex = -1;
	}
	
	public double getCost()
	{
		return cost;
	}

	public void setCost(double c)
	{
		cost = c;
	}
	
	public int countDataNodes()
	{
		return data.size();
	}

	public TrellisNode getDataNode(int num) 
	{
		return (TrellisNode) data.get(num);
	}

	public int addDataNode(TrellisNode n) 
	{
		data.add(n);
		return data.size();
	}

	public TrellisNode removeDataNode(int num) 
	{
		return (TrellisNode) data.remove(num);
	}
	
	public void fillData(int num) 
	{
		clear();
		
		for(int i = 0; i < num; i++)
			addDataNode(new TrellisNode());
	}

	public int countModelNodes()
	{
		return model.size();
	}

	public StringNode getModelNode(int num) 
	{
		return (StringNode) model.get(num);
	}

	public int addModelNode(StringNode n) 
	{
		model.add(n);
		return model.size();
	}

	public StringNode removeModelNode(int num) 
	{
		return (StringNode) model.remove(num);
	}
	
	public int getModelIndex()
	{
		return modelIndex;
	}

	public void setModelIndex(int i)
	{
		modelIndex = i;
	}
	
	public void clear()
	{
		data.clear();
		model.clear();
	}

	public void write(PrintStream p) 
	{
		int i = 0;

		if(p == null) p = System.out;

		for(i = 0; i < countDataNodes(); i++)
		{
			p.println("((");
			p.println(((TrellisNode) data.get(i)).getStringNode().getFeature());
			p.println(((StringNode) model.get(i)).getFeature());
			
			p.println("#" + ((TrellisNode) data.get(i)).getStringNode().getIndex());
			p.println("@" + ((StringNode) model.get(i)).getIndex());
			
			p.println(GlobalProperties.getIntlString("Cost:_") + getCost() + GlobalProperties.getIntlString(",_model_index:_") + getModelIndex());
			p.println("))");
		}
	}
}
