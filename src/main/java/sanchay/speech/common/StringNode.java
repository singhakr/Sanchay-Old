package sanchay.speech.common;

public class StringNode {
	private Feature feature;
	private int index;
	
	public StringNode(Feature f)
	{
		feature = f;
		index = -1;
	}

	public Feature getFeature()
	{
		return feature;
	}

	public void setFeature(Feature f)
	{
		feature = f;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int i)
	{
		index = i;
	}
	
	public double matchScore(StringNode n)
	{
		return RecogUtils.getDistance(feature, n.getFeature());
	}

	public double matchScore(StringNode n1, StringNode n2, boolean reverse)
	{
		return RecogUtils.getDistance(feature, n1.getFeature(), n2.getFeature(), reverse);
	}

	public double matchScore(StringNode m2, StringNode n1, StringNode n2)
	{
		return RecogUtils.getDistance(feature, m2.getFeature(), n1.getFeature(), n2.getFeature());
	}
}
