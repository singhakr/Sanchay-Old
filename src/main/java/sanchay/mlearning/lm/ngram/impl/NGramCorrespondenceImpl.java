package sanchay.mlearning.lm.ngram.impl;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.parallel.impl.AlignedCorpusImpl;
import sanchay.mlearning.lm.ngram.NGramCorrespondence;

public class NGramCorrespondenceImpl implements NGramCorrespondence
{
	private int nGramOrder;
	
	// pairs (source-target) -- "ngram-source::ngram-target" as key and frequency as value
	private Hashtable oneGrams;
	private Hashtable twoGrams;
	private Hashtable threeGrams;
	private Hashtable fourGrams;
	
	public NGramCorrespondenceImpl(int order)
	{
		nGramOrder = order;
//		alignedCorpus = ac;
		
		oneGrams =  new Hashtable();
		twoGrams =  new Hashtable();
		threeGrams =  new Hashtable();
		fourGrams =  new Hashtable();
	}
	
	public int getNGramOrder()
	{
		return nGramOrder;
	}
	
	public void setNGramOrder(int o)
	{
		nGramOrder = o;
	}
	
	public long countNGrams(short whichGram)
	{
		switch(whichGram)
		{
			case 1:
				return oneGrams.size();
			case 2:
				return twoGrams.size();
			case 3:
				return threeGrams.size();
			case 4:
				return fourGrams.size();
		}
		
		return 0;
	}

	public Enumeration getNGramKeys(int whichGram)
	{
		switch(whichGram)
		{
			case 1:
				return oneGrams.keys();
			case 2:
				return twoGrams.keys();
			case 3:
				return threeGrams.keys();
			case 4:
				return fourGrams.keys();
		}
		
		return null;
	}
	
	public long getNGramFreq(String wds, int whichGram)
	{
/*		switch(whichGram)
		{
			case 1:
				return (long) oneGrams.get(wds);
			case 2:
				return (long) twoGrams.get(wds);
			case 3:
				return (long) threeGrams.get(wds);
			case 4:
				return (long) fourGrams.get(wds);
		}
*/		
		return -1;
	}
	
	public long addNGram(String wds, long ngf, int whichGram)
	{
		Hashtable ht = null;
		long oldngf = -1;
		
		switch(whichGram)
		{
			case 1:
				ht = oneGrams;
				break;
			case 2:
				ht = twoGrams;
				break;
			case 3:
				ht = threeGrams;
				break;
			case 4:
				ht = fourGrams;
				break;
		}
		
		if(ht != null)
		{
/*			if( ht.get(wds) == null )
				ht.put(wds, (long) 1);
			else
			{
				oldngf = (long) ht.get(wds);
				ht.put(wds, (long) (oldngf + 1) );
			}
*/			
			return ht.size();
		}
		
		return 0;
	}
	
	public long removeNGram(String wds, int whichGram)
	{
/*		switch(whichGram)
		{
			case 1:
				return (long) oneGrams.remove(wds);
			case 2:
				return (long) twoGrams.remove(wds);
			case 3:
				return (long) threeGrams.remove(wds);
			case 4:
				return (long) fourGrams.remove(wds);
		}
*/		
		return -1;
	}
	
	public int train(AlignedCorpusImpl ac)
	{
		clear();
		return 0;
	}

	public void clear()
	{
		oneGrams.clear();
		twoGrams.clear();
		threeGrams.clear();
		fourGrams.clear();
	}
	
	public void print(int whichGram, PrintStream ps)
	{
		System.out.println(whichGram);
		Enumeration enm = getNGramKeys(whichGram);
		
		while(enm.hasMoreElements())
		{
			String key = (String) enm.nextElement();
//			printNGram(key, whichGram, ps);
		}
	}
	
	public void print(PrintStream ps)
	{
		for(int i = 1; i <= nGramOrder; i++)
		{
			print(i, ps);
		}
	}

	public static void main(String args[])
	{
		NGramCorrespondence ngcor = new NGramCorrespondenceImpl(2);
		AlignedCorpusImpl alcorpus = new AlignedCorpusImpl();
		
		try
		{
			alcorpus.read("");
		}
		catch(IOException e)
		{
			System.out.println(GlobalProperties.getIntlString("Input_file_not_found"));
			return;
		}
		
		ngcor.train(alcorpus);
		
		PrintStream ps = null;
		try
		{
			ps = new PrintStream("");
		}
		catch(FileNotFoundException e) 
		{
			System.out.println(GlobalProperties.getIntlString("FileNotFoundException_Exception!"));
		}
		
		ngcor.print(ps);
	}
}
