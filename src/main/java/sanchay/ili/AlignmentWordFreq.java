package sanchay.ili;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sanchay.GlobalProperties;
import sanchay.text.AksharData;
import sanchay.text.DictionaryFST;
import sanchay.text.DictionaryFSTNode;
import sanchay.text.adhoc.AksharSimList;
import sanchay.text.adhoc.AksharSimilarity;
import sanchay.text.adhoc.PreProcessRules;

public class AlignmentWordFreq
{
	double cost, maxFinalCost;
	AksharSimList simList;
	Vector endNodeList;
	Vector costList;
	Vector modelStrList;
	Vector modelAksharCountList;
	Vector finalCostList;
	Vector indexCost;
	Hashtable engFreq;
	Hashtable hinFreq;
	String dataStr;
	
	int countWords, aksharCountData;
	double maxCost;
	
	private class WordSort 
	{
		String word;
		double cost;
	}
	
	public AlignmentWordFreq()
	{
		cost = 0;
		maxCost = 6.8;
		endNodeList = new Vector();
		costList = new Vector();
		modelStrList = new Vector();
		modelAksharCountList = new Vector();
		finalCostList = new Vector();
		countWords = 0;
		aksharCountData = 0;
		dataStr = "";
		maxFinalCost = 6.8;
		indexCost = new Vector ();
		engFreq = new Hashtable();
		hinFreq = new Hashtable();
	}
	
	public void initCost()
	{
		cost = 0;
	}
	
	public void initValues()
	{
		cost = 0;
		endNodeList = new Vector();
		costList = new Vector();
		modelStrList = new Vector();
		modelAksharCountList = new Vector();
		finalCostList = new Vector();
		countWords = 0;
		aksharCountData = 0;
		dataStr = "";
		indexCost = new Vector ();
	}
	
	public void getSimList (AksharSimilarity aks)
	{
		simList = aks.getSimList();
	}
	
	public String[] getAksharWord(String word, AksharData data)
	{
		String[] aksharArray = null;
		try
		{
			PreProcessRules aRule= new PreProcessRules();
			aRule.readRules(GlobalProperties.getHomeDirectory() + "/data/transliteration/preProcessAll");
    		word = aRule.transferString(word);
		
			aksharArray = new String[15];
			int i=0;
			dataStr = word;
			while (word.length() > 0 && i < 15 )
			{
				aksharArray[i] =  data.getAkshar(word);
				word = word.substring(aksharArray[i].length());
				i++;
			}
			aksharCountData = i;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return aksharArray;
	}
	
	public void readEngFreq(String file)
	{
		try
		{
		BufferedReader lnReader = null;
		lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String line;
	    String splitline[] = null;
	    String splitvalue[] = null;
	    int i=0;
	    while((line = lnReader.readLine()) != null)
	    {
	    	splitline = line.split(" ");
	    	if (splitline.length == 2)
	    	{
	    		engFreq.put(splitline[0].toUpperCase(), splitline[1]);
	    	}
	    }
	    
	    lnReader.close(); lnReader = null;
	    }
		
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	public void readHinFreq(String file)
	{
		try
		{
		BufferedReader lnReader = null;
		lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), GlobalProperties.getIntlString("UTF-8")));
	    String line;
	    String splitline[] = null;
	    String splitvalue[] = null;
	    int i=0;
	    while((line = lnReader.readLine()) != null)
	    {
	    	splitline = line.split("\t");
	    	if (splitline.length == 2)
	    	{
	    		hinFreq.put(splitline[0], splitline[1]);
	    	}
	    }
	    
	    lnReader.close(); lnReader = null;
	    }
		
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	
	public void genStrModel()
	{
		int i,j;
		String temp;
		DictionaryFSTNode node;
//		System.out.println("**** inside genStrModel()");
		for (i=0; i < countWords; i++)
		{
			temp = "";
			node = (DictionaryFSTNode) endNodeList.get(i);
			while (node!=null)
			{
				temp = node.getString() + temp;
				node = node.getParent();
			}
			//wordList[i].wordString = temp;
			
//			System.out.println(temp+ " " + costList.get(i));
			this.modelStrList.add(temp);
		}
	}
	
	public void genFinalCost()
	{
		int i, aksharLenDiff, modelLen;
		double lenScale, lenScaleRoot, rootLimit, costTrellis, costFinal;
		lenScale = 6.5;
		lenScaleRoot = .25;
		rootLimit = 7;
		for (i=0; i<countWords; i++)
		{
			Integer temp = (Integer) this.modelAksharCountList.get(i);
			modelLen = Integer.parseInt(temp.toString());
			aksharLenDiff = aksharCountData - modelLen;
			Double temp1 = (Double) this.costList.get(i); 
			costTrellis = Double.parseDouble(temp1.toString());
			costFinal = costTrellis;
			if (aksharLenDiff > 0)
			{
				if (costTrellis > rootLimit || modelLen < 6 ) // A word is not a root if its Akshar Length is only 1
				{
					costFinal = costTrellis + lenScale * aksharLenDiff;
				}
				else // A root
				{
					costFinal = costTrellis + lenScaleRoot * aksharLenDiff;
				}
			}
			Double costDoub = new Double (costFinal);
//			System.out.println("costDoub ::" + costDoub);
			this.finalCostList.add(costDoub);
		}
	}
	
	public double getMaxFinalCost(int akshar)
	{
        //Testing
        akshar++;
		if (akshar <=2)
			return (double)3.2;
		if (akshar > 2)
			return (double)(3.0+ (akshar - 2)* 4.2);
		
		return (double)7;
	}
	
	public void printFinalList(PrintStream ps)
	{
		ps.println(dataStr + ": " + this.aksharCountData);
		int i;
		for (i=0; i<countWords; i++)
		{
			ps.println(this.modelStrList.get(i) + "  " + this.modelAksharCountList.get(i) + "  " + this.costList.get(i) + "  " + this.finalCostList.get(i));
		}
	}

	public void printFinalListSorted(PrintStream ps)
	{
		ps.println(dataStr + ": " + this.aksharCountData);
		int i,j, index;
		double min;
		for (i=0; i<countWords; i++)
		{
			ps.println(this.modelStrList.get(i) + "  " + this.modelAksharCountList.get(i) + "  " + this.costList.get(i) + "  " + this.finalCostList.get(i));
		}
	}
	
	public void genSimilarity (String fileList, int interval, int number,  String outFilePrefix, DictionaryFST dict)
	{
		try 
		{
			int count = 0;
			int version=0;
			BufferedReader lnReader = null;
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileList), GlobalProperties.getIntlString("UTF-8")));
			Vector wordFinalSort = new Vector(); 
			//FileWriter writer = null;
			String outFileName = outFilePrefix + "_" + version;
			//writer = new FileWriter ( new File(outFileName));
			PrintStream ps = new PrintStream(outFileName, GlobalProperties.getIntlString("UTF-8"));
		    String line;
		    String splitline[] = null;
		    String outLine;
		    int total=0, totalSingle=0, totalModel=0;
                    String cognates = "";

                    while((line = lnReader.readLine()) != null && version <= number)
		    {
		    	splitline = line.split("\t");
		    	initValues ();
		    	//ps.print(line);
//		    	System.out.println("AA " + line);
		 
		    	if (splitline.length>1)
		    	{
		    	splitline[1] = splitline[1].trim();
		    	String[] akshar = getAksharWord(splitline[1], dict.data);
				traverseTree(dict.getRoot(), 0, akshar, 0, 0);
				genStrModel();
				genFinalCost();
				//writer.write(dataStr + ": " + this.aksharCountData + " freq: " +splitline[1] +  "\n");
				for (int i=0; i<countWords; i++)
				{
					double costTemp = Double.parseDouble(finalCostList.get(i).toString());
					int modelAksharCount = Integer.parseInt(modelAksharCountList.get(i).toString());
                    
//                    System.out.println(modelStrList.get(i).toString() + " : " + costTemp + " : " + getMaxFinalCost(modelAksharCount));

					if (costTemp <= getMaxFinalCost(modelAksharCount))
					{
						WordSort temp = new WordSort();
						temp.cost = costTemp;
						temp.word = modelStrList.get(i).toString();
						
						if (aksharCountData > 2 && Math.abs(aksharCountData-modelAksharCount) <=1)
						{							
							//outLine = dataStr +" " + this.modelStrList.get(i) + "  " + this.modelAksharCountList.get(i) + "  " + this.costList.get(i) + "  " + this.finalCostList.get(i) + "\t\n";
							//writer.write(outLine);
							//total++;
							wordFinalSort.add(temp);
						}
						else if ((aksharCountData == 2) && aksharCountData == modelAksharCount)
						{
							//outLine = dataStr +" " + this.modelStrList.get(i) + "  " + this.modelAksharCountList.get(i) + "  " + this.costList.get(i) + "  " + this.finalCostList.get(i) + "\t\n";
							//writer.write(outLine);
							//total++;
							wordFinalSort.add(temp);
						}
					}
				}
				
				// SORTING IN THE COMPARATOR
			    Collections.sort(wordFinalSort, new Comparator() {
					public int compare(Object o1, Object o2) {
					    return ( (int) ((WordSort) o1).cost - (int) ((WordSort) o2).cost );
					}
				    });
				
			    for (int i=0; i<wordFinalSort.size() && i < 3; i++)
				{
			    	if (i==0) 
			    		{
			    		totalSingle++;
			    		ps.print("\n");
			    		}
			    	if (hinFreq.get(((WordSort)wordFinalSort.get(i)).word )!=null)
			    	{
			    		outLine = splitline[0] + " " + ((WordSort)wordFinalSort.get(i)).word + "\t" + ((WordSort)wordFinalSort.get(i)).cost + " " + hinFreq.get(((WordSort)wordFinalSort.get(i)).word)  + "\n" ;
			    	}
			    	else
			    	{
			    		outLine = splitline[0] + " " + ((WordSort)wordFinalSort.get(i)).word + "\t" + ((WordSort)wordFinalSort.get(i)).cost + " " + 1  + "\n" ;
			    	}
			    	ps.print(outLine);
			    	total ++;
                                cognates += ((WordSort)wordFinalSort.get(i)).word + "; ";
				}
				count ++;
				wordFinalSort = new Vector();
				//writer.write("\n");
		    	}
				notvalid:
				if (count == interval)
				{
					version ++;
					//writer.close();
//					System.out.println("Writing " + outFileName);
					outFileName = outFilePrefix + "_" + version;
					//writer = new FileWriter ( new File(outFileName));
					count = 0;
				}
		    	
		    	if (count%1000 == 100)
		    	{
		    		System.out.println(count);
		    	}
		    }
			System.out.println(total + GlobalProperties.getIntlString("_cognate_pairs_found:_") + cognates + "\n");
			System.out.println(totalSingle + GlobalProperties.getIntlString("_out_of_") + count + GlobalProperties.getIntlString("_cognates_found!"));
		    //writer.close();
			
			lnReader.close(); lnReader = null;
			ps.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/*
	public swapVectorObj (int i)
	
	public void sortItOut()
	{
		int i,j;
		int indexCost;
		double leastCost, upperCost;
		for (i=0; i<)
			finalCostList.
	
	}
	*/
	
	public void traverseTree (DictionaryFSTNode node, int depth, String akshar[], int dataPos, double cost)
	{
		DictionaryFSTNode tempNode;
		double costTemp=0;
		if (depth == 0)
		{
			Enumeration childList = node.getChildren();
			
			if(childList != null){
				while (childList.hasMoreElements())
				{
					tempNode = node.getChild((String) childList.nextElement());
					costTemp = simList.getCost(akshar[dataPos], tempNode.getString());
					//depth = 1;
					traverseTree (tempNode, 1, akshar, dataPos+1, costTemp);
				}
			}
		}
		else
		{
			if (cost > maxCost)
			{
				return ;
			}

			Enumeration childList = node.getChildren();
			double temp;
			int dataX=0, modelX=0;
//			if (childList != null)
			if (node.countChildren() > 0)
			{
				while (childList.hasMoreElements())
				{
					tempNode = node.getChild((String) childList.nextElement());
//					 Diagonal Cost
					if (akshar[dataPos] != null)
					{
						costTemp = simList.getCost(akshar[dataPos], tempNode.getString());
						dataX = 1; modelX = 1;
					}
//					Horizontal Cost
					if (akshar[dataPos-1] != null && depth > 1)
					{
						temp = simList.getCost(akshar[dataPos-1], tempNode.getString()) * 1.5 + 1;
						if (temp<costTemp)
						{
							dataX = 0; modelX = 1;
							costTemp = temp;
						}
					}
					
					
					//Vertical Cost
					///*
					if (akshar[dataPos]!=null)
					{
						temp = simList.getCost(akshar[dataPos], node.getString()) *1.5 + 1 ;
						if (temp<costTemp)
						{
							dataX = 1; modelX = 0;
							costTemp = temp;
						}
					}//*/
					if (dataX==1 && modelX==1) //Diag
					{
						traverseTree (tempNode, depth + 1, akshar, dataPos+1, cost + costTemp);
					}
					else if (dataX==0 && modelX==1)//Horz
					{
						traverseTree (tempNode, depth + 1, akshar, dataPos, cost + costTemp);
					}
					else if (dataX==1 && modelX==0)//Vert
					{
						traverseTree (node, depth, akshar, dataPos+1, cost + costTemp);
					}
					//else return;
					
				}
			}
			if (node.getFlags() == node.EOWORD)
			{
				if (endNodeList.contains(node) == false)
				{
					endNodeList.add(node);
					Double costDoub = new Double(cost);
					costList.add(costDoub);
					Integer depthInt = new Integer (depth);
					modelAksharCountList.add(depthInt);
					
//					System.out.println(node.getString() + " " + cost + " " + depthInt);
					countWords++;
//					System.out.print(" " + countWords);
				}
				return;
			}
		}
	}
	
	public void PrintWordList ()
	{
		int i,j;
		String temp;
		DictionaryFSTNode node;
		for (i=0; i < countWords; i++)
		{
			temp = "";
			node = (DictionaryFSTNode) endNodeList.get(i);
			while (node!=null)
			{
				temp = node.getString() + temp;
				node = node.getParent();
			}
			//wordList[i].wordString = temp;
			System.out.println(temp+ " " + costList.get(i));
		}
	}
	
	public static void main (String args[])
	{
	
		try
		{
			DictionaryFST dict = new DictionaryFST(GlobalProperties.getHomeDirectory() + "/data/transliteration/wordList", GlobalProperties.getIntlString("UTF-8"), "/home/sanchay/tmp/spell-checker-dict.forward", "/home/sanchay/tmp/spell-checker-dict.reverse");
			AlignmentWordFreq tree = new AlignmentWordFreq();
			AksharSimilarity aks = new AksharSimilarity(25);
			aks.readSimilarityList(GlobalProperties.getHomeDirectory() + "/data/transliteration/ILIEngSimList.sim");
			tree.getSimList(aks);
			//String[] akshar = tree.getAksharWord("संगमररमर", dict.data);
			//String[] akshar = tree.getAksharWord("बिराज", dict.data);
			//System.out.println("Search Starts");
			//tree.traverseTree(dict.getRoot(), 0, akshar, 0, 0);
			//tree.genStrModel();
			//tree.genFinalCost();
			//tree.printFinalList(System.out);
			tree.readHinFreq(GlobalProperties.getHomeDirectory() + "/data/transliteration/wordList");
			System.out.println("READ");
			tree.genSimilarity(GlobalProperties.getHomeDirectory() + "/data/transliteration/Transfer/HinCMU.Mapped", 7500000, 1, GlobalProperties.getHomeDirectory() + "/data/transliteration/Transfer/HinCMU.Mapped.CAND", dict );
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
