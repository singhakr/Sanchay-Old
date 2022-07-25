package sanchay.text;

import java.io.*;

public class AksharSimilarity implements Serializable
{
	public String akshar;
	public String similarityString[];
	public double similarityCost[];
	
	public AksharSimilarity()
	{
		akshar = "";
		similarityString = new String [50];
		similarityCost = new double [50];
	}
	
}
