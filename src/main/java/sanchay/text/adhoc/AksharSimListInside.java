package sanchay.text.adhoc;

import java.util.Hashtable;
import java.io.*;

public class AksharSimListInside implements Serializable 
{

	Hashtable listInside;
	Double maxValue;
	//Hashtable temp; // to manage inside list
	
	public AksharSimListInside (int max)
	{
		listInside = new Hashtable ();
		maxValue = new Double(max);
	}
	
	public void addAksharCost(String akshar, Double cost)
	{
		listInside.put((Object)akshar, (Object)cost);
	}
	
	public Double getCost (String akshar)
	{
		Object costObj  = listInside.get((Object) akshar);
		if (costObj == null)
		{
			return maxValue;
		}
		else
		{
			Double cost = (Double) costObj;
			return cost;
		}
	}
}
