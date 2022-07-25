package sanchay.speech.common;

public class TrellisNode {
    java.util.ResourceBundle bundle = sanchay.GlobalProperties.getResourceBundle(); // NOI18N

    private StringNode stringNode;
	private double pathCost;
	
	public TrellisNode()
	{
		stringNode = null;
		pathCost = 0.0;
	}
	
	public TrellisNode(StringNode n, double c)
	{
		stringNode = n;
		pathCost = c;
	}

	public StringNode getStringNode()
	{
		return stringNode;
	}

	public void setStringNode(StringNode n)
	{
		stringNode = n;
	}

	public double getPathCost()
	{
		return pathCost;
	}

	public void setPathCost(double c)
	{
		pathCost = c;
	}
	
	public void copy(TrellisNode n)
	{
		if(n == null)
			return;
	
		setStringNode(n.getStringNode());
		setPathCost(n.getPathCost());
	}
}
