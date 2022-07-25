package sanchay.text.adhoc;

import java.util.Hashtable;
import java.io.*;

@Deprecated
public class AksharSimList implements Serializable 
{

	Hashtable list;
	//Hashtable temp; // to manage inside list
	int max;
	
	public AksharSimList (int max)
	{
		list = new Hashtable ();
		this.max = max;
	}
	
	public void addAkshar (String aks1, String aks2, double cost)
	{
		Object o = list.get((Object)aks1);
		AksharSimListInside listIn;
		if (o == null) // make a new instance of AksharSimListInside
		{
			listIn = new AksharSimListInside(max); 
			list.put((Object) aks1, (Object)listIn);
		}
		else
		{
			listIn = (AksharSimListInside) o;
		}
		Double costDouble = new Double (cost);
		listIn.addAksharCost(aks2, costDouble);
	}
	
	public double getCost(String aks1, String aks2)
	{
		if (aks1==null || aks2==null)
		{
			return (double)max;
		}
		
		Object o = list.get((Object)aks1);
		if (o == null)
		{
			return (double)max; 
		}
		else
		{
			AksharSimListInside listIn = (AksharSimListInside) o;
			return listIn.getCost(aks2).doubleValue();
		}
	}
	
}
