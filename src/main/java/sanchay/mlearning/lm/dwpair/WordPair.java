package sanchay.mlearning.lm.dwpair;

import java.io.*;
import java.util.*;

import sanchay.corpus.parallel.aligner.*;
import sanchay.corpus.simple.data.*;

public class WordPair
{
	protected int word1; // index
	protected int word2; // index
	protected int distance;
	protected long freq;
	protected double mi;

	public WordPair()
	{
		distance = 0;
		freq = 0;
		mi = 0;
	}

	public int getWord1()
	{
		return word1;
	}

	public void setWord1(int s_index)
	{
		word1 = s_index;
	}

	public int getWord2()
	{
		return word2;
	}

	public void setWord2(int t_index)
	{
		word2 = t_index;
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int dist)
	{
		distance = dist;
	}

	public long getFreq()
	{
		return freq;
	}

	public void setFreq(long f)
	{
		freq = f;
	}

	public double getMI()
	{
		return mi;
	}

	public void setMI(double minfo)
	{
		mi = minfo;
	}

	public int print(PrintStream ps, WordTypeTable wttbl)//, String lang)
	{
		//ps.print("SrcWord: ");
		//if(getFreq() > 3)
		//{
			//if(lang == "h")
			//{
				ps.print(wttbl.getWT(getWord1()).getWord());

				ps.print("::");
				ps.print(wttbl.getWT(getWord2()).getWord());

				ps.print("::");
				ps.print(getDistance());

				ps.print(" ");
				ps.print(getFreq());

				ps.print(" ");
				ps.println(getMI());
			//}
			/*else
			{
				ps.print(wttbl.getWT(getWord1()).getWord());

				ps.print("::");
				ps.print(wttbl.getWT(getWord2()).getWord());

				ps.print("::");
				ps.print(getDistance());

				ps.print(" ");
				ps.print(getFreq());

				ps.print(" ");
				ps.println(getMI());
			}*/

			return 1;
		//}

		//return 0;
	}

}