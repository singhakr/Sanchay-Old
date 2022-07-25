package sanchay.speech.common;

import sanchay.speech.common.*;

public class PathNode {
	private StringNode data;
	private TrellisNode model;
	private int modelIndex;

	public PathNode()
	{
		data = null;
		model = null;
		modelIndex = -1;
	}
	
	public PathNode(StringNode sn, TrellisNode tn, int mi)
	{
		data = sn;
		model = tn;
		modelIndex = mi;
	}

	public StringNode getDataNode()
	{
		return data;
	}

	public void setDataNode(StringNode n)
	{
		data = n;
	}

	public TrellisNode getModelNode()
	{
		return model;
	}

	public void setModelNode(TrellisNode n)
	{
		model = n;
	}

	public int getModelIndex()
	{
		return modelIndex;
	}

	public void setModelIndex(int i)
	{
		modelIndex = i;
	}
}
