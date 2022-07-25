/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.lm.ngram.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.io.*;
import java.util.Arrays;


import java.util.Iterator;
import java.util.LinkedHashMap;
import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;

public class CalcCognSim {

	LinkedHashMap htKnd;
	LinkedHashMap htTel;
	LinkedHashMap htTam;
	LinkedHashMap htMal;

	public int scoreType;
	KeyValueProperties trainingDataPaths;
	LinkedHashMap wrdLists;
	LinkedHashMap testLists;
	LinkedHashMap cognateCnt;

	public CalcCognSim(String trainPaths,int st) throws IOException
	{
		trainingDataPaths = new KeyValueProperties();
        trainingDataPaths.read(trainPaths, GlobalProperties.getIntlString("UTF-8"));
        scoreType = st;
	}

	public Vector readFile(File f)
	{
//		Hashtable ht = new Hashtable();
		Vector vec = new Vector();
		try {
			BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));
			String line = "";


			while((line = inReader.readLine()) != null)
			{
//				System.out.println(line);
				vec.add(line);
			}

		}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return vec;
	}
	public KeyValueProperties getTrainingDataPaths() {
        return trainingDataPaths;
    }
    
	public Iterator getLangListKeys()
    {
        return getTrainingDataPaths().getPropertyKeys();
    }
	public void readWrdLists()
	{
		Iterator enm = getTrainingDataPaths().getPropertyKeys();
		wrdLists = new LinkedHashMap(getTrainingDataPaths().countProperties());
		while(enm.hasNext())
		{
			String k = (String) enm.next();
			String fName = getTrainingDataPaths().getPropertyValue(k);
//			System.out.println(fName);
			wrdLists.put(k,readFile(new File(fName)));
		}
	}
	public Vector calcDistVector(Vector langSrc, Vector langTgt)
	{
		Vector results = new Vector();
		for(int i = 0;i < langSrc.size();i++)
		{
			String splitStrSrc[] = ((String)langSrc.get(i)).split("\\t");
			for(int j = 0;j < langTgt.size();j++)
			{
				String splitStrTgt[] = ((String)langTgt.get(j)).split("\\t");
//				System.out.println(splitStrSrc[1]+"---"+splitStrTgt[1]);
				if(scoreType == MinBayesRiskRanking.EDIT)
					results.add(splitStrSrc[0]+"@#&"+splitStrTgt[0]+"\t"+WrdSimilaritiesFuncs.LevenshteinDistance(splitStrSrc[1], splitStrTgt[1]));
				else if(scoreType == MinBayesRiskRanking.FNG)
					results.add(splitStrSrc[0]+"@#&"+splitStrTgt[0]+"\t"+WrdSimilaritiesFuncs.fng(splitStrSrc[1], splitStrTgt[1]));
				else if(scoreType == MinBayesRiskRanking.LCSR)
					results.add(splitStrSrc[0]+"@#&"+splitStrTgt[0]+"\t"+WrdSimilaritiesFuncs.LCS(splitStrSrc[1], splitStrTgt[1]));
				else if(scoreType == MinBayesRiskRanking.DICE)
					results.add(splitStrSrc[0]+"@#&"+splitStrTgt[0]+"\t"+WrdSimilaritiesFuncs.diceCoeff(splitStrSrc[1], splitStrTgt[1]));
//					results.add(splitStrSrc[0]+"\t"+splitStrTgt[0]+"\t"+WrdSimilaritiesFuncs.fng(splitStrSrc[1], splitStrTgt[1]));


//				if(((((i+1) * (j+1))%100) == 0) == true)
//					System.out.println("taking 100 as factor the iteration is "+(((i+1) * (j+1))/100+"time taken "+System.currentTimeMillis()));
			}
		}
		return results;
	}
	public void calListDist(Vector langSrc,String srcKey)
	{
		Iterator enm = getLangListKeys();
		String tgtKey = "";
		Vector langTgt;

		while(enm.hasNext())
		{
			tgtKey = (String)enm.next();
			langTgt = (Vector)wrdLists.get(tgtKey);
			String langMixedKeys1 = srcKey+"\t"+tgtKey;
			String langMixedKeys2 = tgtKey+"\t"+srcKey;
//			System.out.println("Source---"+srcKey+"Target---"+tgtKey);
			if(testLists.containsKey(langMixedKeys1) == false && testLists.containsKey(langMixedKeys2) == false && srcKey.equals(tgtKey) == false)
				testLists.put((Object)langMixedKeys1,(Object)calcDistVector(langSrc,langTgt));
		}
	}
	public void calcScores()
	{
		Iterator enm = getLangListKeys();
		String langKey = "";

		testLists = new LinkedHashMap(5,5);
		while(enm.hasNext())
		{
			langKey = (String)enm.next();
			calListDist((Vector)wrdLists.get(langKey),langKey);
		}
	}
	public void printScores()
	{
		Iterator enm = testLists.keySet().iterator();
		while(enm.hasNext())
		{
			String key = (String)enm.next();
			Vector vec = (Vector)testLists.get(key);
			System.out.println(key+"::::");
			for(int i = 0;i < vec.size();i++)
				System.out.println("\t"+vec.elementAt(i));
		}
	}
	public void calcCognateCount()
	{
		Iterator enm = testLists.keySet().iterator();
		cognateCnt = new LinkedHashMap();
		while(enm.hasNext())
		{
			String key = (String)enm.next();
			Vector vec = (Vector)testLists.get(key);
//			System.out.println(key);
			Enumeration elEnm = vec.elements();
			int cogCnt = 0;
			while(elEnm.hasMoreElements())
			{
				String cand = (String)elEnm.nextElement();
//				System.out.println("Cand="+cand);
				String cands[] = cand.split("\\t");
				String indexNums[] = cands[0].split("@#&");
				if(indexNums[0].equals(indexNums[1]))
					cogCnt++;
			}
			System.out.println(key+"\t"+cogCnt);
			cognateCnt.put((Object)key, cogCnt);
		}
//		System.out.println("Counting Cognates");
		Iterator cgEnm = cognateCnt.keySet().iterator();
		System.out.println(GlobalProperties.getIntlString("Cognatecount_hash_size")+cognateCnt.size());
//		while(cgEnm.hasMoreElements())
//		{
//			String key = (String)enm.nextElement();
//			int count = Integer.parseInt((String)cognateCnt.get(key));
//			System.out.println(key+"\t"+cognateCnt.get(key));
//		}
	}
	public void sortScores()
	{
		System.out.println(GlobalProperties.getIntlString("In_sorted_scores"));
		Iterator enm = testLists.keySet().iterator();
//		cognateCnt = new Hashtable(testLists.size());
		while(enm.hasNext())
		{
			String key = (String)enm.next();
			Vector vec = (Vector)testLists.get(key);

			Enumeration enm1 = vec.elements();
			Vector sortedScores = new Vector(vec.size());
			int pairCnt = Integer.parseInt(String.valueOf(cognateCnt.get(key)));
			System.out.println(key+GlobalProperties.getIntlString("--paircount--")+pairCnt+GlobalProperties.getIntlString("--size_of_score--")+vec.size());
			while(enm1.hasMoreElements())
			{
				String cand = (String)enm1.nextElement();
				String cands[] = cand.split("\\t");
//				System.out.println("Cand="+cand);
				CognateScore cs = new CognateScore(cands[0],Double.parseDouble(cands[1]));
				sortedScores.add(cs);
			}
//			CognateScore srtdArr[] = (CognateScore [])sortedScores.toArray();
			//Sorting items
			if( scoreType == MinBayesRiskRanking.DICE || scoreType == MinBayesRiskRanking.LCSR )
				Collections.sort(sortedScores, new Comparator() {
					public int compare(Object o1, Object o2) {
						double cs1 = ((CognateScore)o1).modelScore;
						double cs2 = ((CognateScore)o2).modelScore;
						if(cs1 > cs2)
							return -1;
						else if(cs1 < cs2)
							return 1;
						else
							return 0;
//						return ( (int) ((CognateScore) o2).modelScore.doubleValue() - (int) ((CognateScore) o1).modelScore.doubleValue() );
					}
				});
			else if( scoreType == MinBayesRiskRanking.FNG || scoreType == MinBayesRiskRanking.EDIT)
				Collections.sort(sortedScores, new Comparator() {
					public int compare(Object o1, Object o2) {
						double cs1 = ((CognateScore)o1).modelScore;
						double cs2 = ((CognateScore)o2).modelScore;
						if(cs1 > cs2)
							return 1;
						else if(cs1 < cs2)
							return -1;
						else
							return 0;
					}
				});
			System.out.println(GlobalProperties.getIntlString("Size_of_sorted_scores_=_")+sortedScores.size());
			for(int l = 0;l < sortedScores.size();l++)
				System.out.println(((CognateScore)sortedScores.get(l)).modelKey+"---"+((CognateScore)sortedScores.get(l)).modelScore);
			calcElvnPntAvgPrecs(sortedScores, pairCnt);
		}
	}
	public void calcElvnPntAvgPrecs(Vector srtdScrs, int pairCnt)
	{
//		double[] precArr = new double[];
		int strtPnt = 0;
		double[] precArr = new double[10];
		int[] rclArr = new int[10];
		int crctCnt = 0;
		double totPrec = 1.0;
		precArr[0] = 0.0;

		for(int i = 1; i < 10;i++)
		{
			rclArr[i] = Math.round((i * pairCnt)/10);
		}
		for(int l = 1;l < 10;l++)
			System.out.println(l+"="+rclArr[l]);
		for(int j = 0;j < srtdScrs.size();j++)
		{
			CognateScore cs = (CognateScore)srtdScrs.get(j);
			String idx = cs.modelKey;
			double score = cs.modelScore;
			String idxs[] = idx.split("@#&");
			if(idxs[1].equalsIgnoreCase(idxs[0]))
			{
//				System.out.println()
				++crctCnt;
				int index = Arrays.binarySearch(rclArr, crctCnt);
				if(index >= 0)
				{
					System.out.println(GlobalProperties.getIntlString("Recall_point=")+rclArr[index] +GlobalProperties.getIntlString("_reached_at_=")+index+GlobalProperties.getIntlString("_in_scores_array_at=")+j+GlobalProperties.getIntlString("_indexes_=")+idx);
					precArr[index] = (crctCnt*1.0)/(j+1);
				}
			}
		}
//
		int k = precArr.length - 1;
		System.out.println(GlobalProperties.getIntlString("Size_of_precision_array_is_=")+precArr.length);
		for(int l = 0;l < k+1;l++)
			System.out.println(l+GlobalProperties.getIntlString("=")+precArr[l]);
		double val = 0.0;
		int n = k;
		val = precArr[n];
		while(k >= 1)
		{
			if(precArr[k-1] <= precArr[k])
			{
				totPrec += val;
				k--;
			}
			else
			{
				totPrec += val;
				val = precArr[k-1];
				k--;
			}
		}
		totPrec = totPrec / 11;
		System.out.println(totPrec);
	}
	public static void main(String[] args) throws IOException
	{

		String trainingPaths = "/home/anil/training.txt";
		int scoreType = MinBayesRiskRanking.DICE;
		CalcCognSim ccs = new CalcCognSim(trainingPaths,scoreType);
		ccs.readWrdLists();
		ccs.calcScores();
		System.out.println(GlobalProperties.getIntlString("calculated_scores"));
//		ccs.printScores();
		ccs.calcCognateCount();
		System.out.println(GlobalProperties.getIntlString("calculated_cognate_count"));
		ccs.sortScores();
		System.out.println(GlobalProperties.getIntlString("sorted_scores"));

//		ccs.htKnd = ccs.readFile(new File("/home/taraka/data/progs/Ka.txt"));
//		ccs.htTam = ccs.readFile(new File("/home/taraka/data/progs/Ta.txt"));
//		ccs.htTel =	ccs.readFile(new File("/home/taraka/data/progs/Te.txt"));
//		ccs.htMal =	ccs.readFile(new File("/home/taraka/data/progs/Ma.txt"));
//
//		MinBayesRiskRanking mbrr = null;
//		int st =  mbrr.EDIT;
//
//		ccs.calcDistance(ccs.htKnd,ccs.htTam,st);
//		ccs.calcDistance(ccs.htKnd,ccs.htTel,st);
//		ccs.calcDistance(ccs.htKnd,ccs.htMal,st);
//		ccs.calcDistance(ccs.htTam,ccs.htTel,st);
//		ccs.calcDistance(ccs.htTam,ccs.htMal,st);
//		ccs.calcDistance(ccs.htTel,ccs.htMal,st);
	}

	class CognateScore
    {
	public String modelKey;
	public double modelScore;

	public CognateScore(String modelKey, double modelScore)
	{
	    this.modelKey = modelKey;
	    this.modelScore = modelScore;
	}
    }
}
