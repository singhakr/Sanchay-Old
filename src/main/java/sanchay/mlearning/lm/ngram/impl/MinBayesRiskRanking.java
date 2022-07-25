/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram.impl;

import java.io.*;
import java.util.regex.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.text.spell.InstantiatePhonemeGraph;
import sanchay.text.spell.PhoneticModelOfScripts;


public class MinBayesRiskRanking {

	Vector candScore = new Vector();
	Hashtable ht = new Hashtable();
	int maxLineNo = 0;

	public static final int EDIT = 1;
	public static final int LCSR = 2;
	public static final int DICE = 3;
	public static final int FNG = 4;
	public static final int FNGDICE = 5;
	public static final int CPMS = 6;

	public int scoreType;
	PrintStream ps;

	public MinBayesRiskRanking(int st, String fout)
	{
		scoreType = st;
		try {
			ps = new PrintStream(new FileOutputStream(fout));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public MinBayesRiskRanking(int st)
	{
		scoreType = st;
	}

	public void readFile(File f)
	{
		try {
				BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));
				String line = "";
				int lineNo = 0;
				Vector vec = new Vector();
				int temp = -1;

				while((line = inReader.readLine()) != null)
				{
//					System.out.println(line);
					Pattern delim = Pattern.compile("\\s\\|\\|\\|\\s");
					String wrds[] = line.split(delim.pattern());

					wrds[1] = wrds[1].replaceAll("\\s", "");
					lineNo = Integer.parseInt(wrds[0]);
//					System.out.println("----"+lineNo);
					if(lineNo != temp)
					{
						ht.put((Object)temp,vec);
						temp++;
						vec = new Vector();
					}
					else
						vec.add((Object)wrds[1]+"@#&"+wrds[3]);
//					ht.put(wrd, value)
				}
				maxLineNo = lineNo;
				ht.put((Object)temp,(Object)vec);
				System.out.println("---"+maxLineNo);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector vec = new Vector();

//			 for(int i = 0; i < maxLineNo+1;i++)
//			 {
//				 vec = (Vector)ht.get((Object)Integer.valueOf(i));
//				 for(int j = 0;j < vec.size();j++)
//					  System.out.println(vec.get(j));
//			 }
	}

	public double diceCoeff( String s1, String s2 )
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

	public double LevenshteinDistance(String s1, String s2)
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

	    return d[s1.length()][s2.length()]/am;
	}

	public double LCS(String s1, String s2)
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

	public double fng(String src,String tgt)
	{
		double score = 0.0;
		NGramLM trainingModel = null;
		NGramLM testModel = null;

		InstantiatePhonemeGraph ipg = new InstantiatePhonemeGraph(GlobalProperties.getIntlString("hin::utf8"),GlobalProperties.getIntlString("UTF-8"),false);
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

	public double calcLoss(String src, String tgt)
	{
		if(scoreType == EDIT)
			return Math.log(LevenshteinDistance(src, tgt));
		else if(scoreType == LCSR)
			return Math.log(1 - LCS(src,tgt));
		else if(scoreType == DICE)
			return Math.log(1 - diceCoeff(src,tgt));
		else if(scoreType == FNG)
			return fng(src,tgt);
		else if(scoreType == CPMS)
		{
			PhoneticModelOfScripts cpf = null;
			try {
				cpf = new PhoneticModelOfScripts(
						GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
						GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));

			} catch (Exception e) {
				// TODO: handle exception
			}
//			return Math.log(cpf.getStringDistance(src, tgt));
            return 0.0;
		}
		else
			return 0.0;
	}
	public void dombrRank()
	{
		System.out.println(maxLineNo);
		for(int i = 0; i <= maxLineNo ; i++)
		{

			double cumLoss = 0;
			int mbrLossIdx = -1;
			double minMBRLoss = 100000;
			double loss = 0.0;

			Vector vec = new Vector();
			vec = (Vector)ht.get((Object)Integer.valueOf(i));
			String bestTrans = "";

			for(int j = 0;j < vec.size();j++)
			{
				String strSrc[] = ((String)vec.get(j)).split("@#&");
				double srcScr = Double.parseDouble(strSrc[1]);
//				System.out.println("i ="+i+"src=="+strSrc[0]);

				for(int k = 0;k < vec.size();k++)
				{
					String strTgt[] = ((String)vec.get(j)).split("@#&");
					double tgtScr = Double.parseDouble(strTgt[1]);
					if(j != k && strSrc[0].length() > 0 && strTgt[0].length() > 0)
					{
						loss = calcLoss(strSrc[0], strTgt[0]);
						cumLoss += loss + tgtScr;
						if(cumLoss > minMBRLoss)
							break;
					}
				}
				if(cumLoss < minMBRLoss)
				{
					minMBRLoss = cumLoss;
					mbrLossIdx = j;
					bestTrans = strSrc[0];
				}
			}
			ps.println(bestTrans);
			System.out.println(bestTrans);
		}
	}

	public static void main(String[] args) {

		int st = CPMS;
		String fout = "/home/taraka/NEWS-sharedd-task/moses-exp/mbr/out-devel-100-cpms.utf";
		MinBayesRiskRanking mbrr = new MinBayesRiskRanking(st,fout);
//		mbrr.readFile(new File("/home/taraka/NEWS-sharedd-task/moses-exp/mbr/dev-100-list.utf"));
//		mbrr.dombrRank();
		System.out.println(GlobalProperties.getIntlString("score_is_=_")+mbrr.fng("बिजली","पिछली"));
//		System.out.println("score is = "+mbrr.fng("बिजली","निचली"));
//		System.out.println("score is = "+mbrr.fng("बिजली","बिछडा"));
	}

}
