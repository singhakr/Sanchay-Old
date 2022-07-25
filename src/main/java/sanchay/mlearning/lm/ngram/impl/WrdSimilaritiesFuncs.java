package sanchay.mlearning.lm.ngram.impl;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Iterator;
import java.util.List;

import sanchay.GlobalProperties;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.text.spell.InstantiatePhonemeGraph;

public class WrdSimilaritiesFuncs {
	public static double diceCoeff( String s1, String s2 )
	{
//		System.out.println("String 1="+s1+"String 2="+s2);
		String [] s1_bi = new String[s1.length()-1];
		String [] s2_bi = new String[s2.length()-1];

		int overlap=0, i, j;

		for (i=0; i<s1.length()-1;i++)
		{
			s1_bi[i] = s1.substring(i,i+2);
//			System.out.println(s1.substring(0,2));
//			System.out.println(i+"\t"+s1_bi[i]);
		}

		for (j=0; j<s2.length()-1;j++)
			s2_bi[j] = s2.substring(j,j+2);

		for ( i=0; i<s1_bi.length;i++)
			for (j=0; j<s2_bi.length;j++)
				if(s1_bi[i].equals(s2_bi[j]))
					overlap = overlap + 1;

		double am = (s1.length()+s2.length())/2;
		double gm = Math.sqrt(s1.length()*s2.length());
		double max = Math.max(s1.length(),s2.length());
//		return overlap;
		double total = s1_bi.length+s2_bi.length;
		return (2 * overlap) / total;
	}

	public static double LevenshteinDistance(String s1, String s2)
	{
		if ( s1 == null || s2 == null || s1 == "" || s2 == "")
			return 0;

	   // d is a table with m+1 rows and n+1 columns
	   int d[][] = new int[s1.length()+1][s2.length()+1];

	   for(int i = 0; i<=s1.length(); i++ )
	       d[i][0] = i;
	   for(int j = 0; j<=s2.length(); j++ )
	       d[0][j] = j;

	   int cost = 0;

	   for(int i = 1; i<=s1.length(); i++ )
	   {
		   for(int j = 1; j<=s2.length(); j++ )
	       {
	           if (s1.charAt(i-1) == s2.charAt(j-1))
	        	   cost = 0;
	           else
	        	   cost = 1;

	           d[i][j] = Math.min(Math.min(d[i-1][j] + 1, d[i][j-1] + 1),d[i-1][j-1] + cost);
	       }
	   }
	   	double am = (s1.length()+s2.length())/2;
		double gm = Math.sqrt(s1.length()*s2.length());
		double max = Math.max(s1.length(),s2.length());
//		return d[s1.length()][s2.length()];
	    return (2.0*d[s1.length()][s2.length()])/(s1.length()+s2.length());
	}

	public static double LCS(String s1, String s2)
	{
		//if either string is empty, the length must be 0
		if ( s1 == null || s2 == null || s1 == "" || s2 == "")
			return 0;

		int num[][] = new int[s1.length()][ s2.length()];  //2D array
		char letter1;
		char letter2;

		//Actual algorithm
		for(int i = 0; i < s1.length(); i++)
		{
			for(int j = 0; j < s2.length(); j++)
			{
				letter1 = s1.charAt(i);
				letter2 = s2.charAt(j);

				if(letter1 == letter2)
				{
					if((i == 0) || (j == 0))
						num[i][j] = 1;
					else
						num[i][j] = 1 + num[i-1][j-1];
				}
				else
				{
					if ((i == 0) && (j == 0))
						num[i][j] = 0;
					else if ((i == 0) && !(j == 0))   //First ith element
						num[i][j] = Math.max(0, num[i][j - 1]);
					else if (!(i == 0) && (j == 0))   //First jth element
						num[i][j] = Math.max(num[i - 1][j], 0);
					else if (!(i == 0) && !(j == 0))
						num[i][j] = Math.max(num[i - 1][j], num[i][j - 1]);
				}
			}//end j
		}//end i
		double am = (s1.length()+s2.length())/2;
		double gm = Math.sqrt(s1.length()*s2.length());
		double max = Math.max(s1.length(),s2.length());
		return num[s1.length()-1][s2.length()-1]/am;
	} //end LongestCommonSubsequence
	public static double fng(String src,String tgt)
	{
		double score = 0.0;
		NGramLM trainingModel = null;
		NGramLM testModel = null;

		InstantiatePhonemeGraph ipg = new InstantiatePhonemeGraph(GlobalProperties.getIntlString("ded::utf8"),GlobalProperties.getIntlString("UTF-8"),false);
		trainingModel = ipg.createPhonemeUnigrams(src);
		testModel = ipg.createPhonemeUnigrams(tgt);

		for(int j = 1; j <= trainingModel.getNGramOrder(); j++)
		{
			Iterator<List<Integer>> testItr = testModel.getNGramKeys(j);

			while(testItr.hasNext())
			{
				List<Integer> testNGram = testItr.next();
				NGram testNg = (NGram) testModel.getNGram(testNGram, j);
				NGram trainNg = (NGram) trainingModel.getNGram(testNGram, j);

				if(trainNg != null)
				{
					// System.out.println("Matched NGram: " + ((NGram) sortedNGrams.get(i)).getString() + "\t" + ((NGram) sortedTestNGrams.get(k)).getString());
					score += testNg.getProb() * Math.log(trainNg.getProb());
					score += trainNg.getProb() * Math.log(testNg.getProb());
				}
			}
		}
		return score;
	}

}
