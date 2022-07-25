package sanchay.ili;

import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;

public class AlignOutput 
{

	private class TranslitMaps
	{
		String wordTL, wordSL;
		double cost;
		double freq;
		double prob;
	}
	
	Vector listMaps;
	
	private class WordSort 
	{
		String word;
		double cost;
	}
	
	public void readMap(String inFile, String cs)
	{
		listMaps = new Vector();
		try 
		{
			BufferedReader lnReader = null;
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			String line;
		    while((line = lnReader.readLine()) != null)
		    {
		    	line = line.replace("\t", " ");
		    	String splitline[] = line.split(" ");
		    	
		    	TranslitMaps temp = new TranslitMaps();
		    	if (splitline.length == 4)
		    	{
		    		temp.wordSL = splitline[0].replaceAll("[0-9]", "");
		    		temp.wordTL = splitline[1];
		    		temp.cost = Double.parseDouble(splitline[2]);
		    		temp.freq = 1;
			    	listMaps.add(temp);
			    	//delDuplicates(100);
		    	}
		    	else if (splitline.length == 5)
		    	{
		    		temp.wordSL = splitline[0].replaceAll("[0-9]", "");
		    		temp.wordTL = splitline[1];
		    		temp.cost = Double.parseDouble(splitline[2]);
		    		temp.freq = 1;
		    		temp.prob = Double.parseDouble(splitline[4]);
			    	listMaps.add(temp);
			    	//delDuplicates(100);
		    	}
		    	

		    }

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void delDuplicates(int range)
	{
		int lastIndex = listMaps.size() - 1;
		//System.out.print("Last Index " + lastIndex);
		TranslitMaps temp = (TranslitMaps) listMaps.elementAt(lastIndex);
		int i;
		for (i = lastIndex - 1; (i > (lastIndex - range) && i >0); i--)
		{
			TranslitMaps temp1 = (TranslitMaps) listMaps.elementAt(i);
			if (temp.wordSL.equals(temp1.wordSL) && temp.wordTL.equals(temp1.wordTL) && temp.freq == temp1.freq)
			{
				if (temp.cost >= temp1.cost)
				{
					listMaps.remove(lastIndex);
					break;
				}
				else if (temp.cost < temp1.cost)
				{
					listMaps.remove(i);
					break;
				}
			}
		}
	}
	
	public void printFile(String outFile, String cs)
	{
		try
		{
	     PrintStream writer = new PrintStream( new File(outFile), cs);
	     int i;
	     for (i=0; i<listMaps.size(); i++)
	     {
	    	 TranslitMaps temp = (TranslitMaps) listMaps.elementAt(i);
	    	 String line = temp.wordSL + " " + temp.wordTL + "\t" + temp.cost + " " + temp.freq;
	    	 writer.println(line);
	     }
	     writer.close();
		}
		catch (Exception ex)
		{
			
		}
	}
	
	public void genRanks(String outFile, String cs)
	{
		try
		{
	     PrintStream writer = new PrintStream ( new File(outFile), cs);
	     int i;
    	 //TranslitMaps tempEx = (TranslitMaps) listMaps.elementAt(i);
	     String wordLeft = "";
	     Vector listSort = new Vector();

         TranslitMaps temp = null;

	     for (i=0; i<listMaps.size(); i++)
	     {
	    	 temp = (TranslitMaps) listMaps.elementAt(i);
	    	 //String line = temp.wordSL + " " + temp.wordTL + "\t" + temp.cost + " " + temp.freq + "\n";
	    	 //writer.write(line);
	    	 
	    	 if (wordLeft.equals(temp.wordSL))
	    	 {
	    		//Keep adding
				WordSort tempWord = new WordSort();
				tempWord.cost = temp.cost * 100;
				tempWord.word = temp.wordTL;
	    		 listSort.add(tempWord);
	    	 }
	    	 else
	    	 // SORT, WRITE INTO FILE, MAKE NEW TEMPWORD
	    	 {
				    Collections.sort(listSort, new Comparator() {
						public int compare(Object o1, Object o2) {
						    return ( (int) ((WordSort) o1).cost - (int) ((WordSort) o2).cost );
						}
					    });
				    
				    for (int p=0; p<listSort.size() && p < 7; p++)
					{
				    	String Source = ((WordSort)listSort.get(p)).word;
				    	if (((WordSort)listSort.get(p)).cost <= 65)
				    	{
				    		//if (temp.cost <=0.5)
				    		{
				    			String line = wordLeft + " " + ((WordSort)listSort.get(p)).word + " " + ((WordSort)listSort.get(p)).cost + " " + temp.freq + "\t" + (p+1);
				    			//String line = temp.wordTL + "\n";				    		System.out.println(line);
				    			writer.println(line);
				    		}
				    	}
					}
				    
				    wordLeft = temp.wordSL;
				    listSort = new Vector();
				    WordSort tempWord = new WordSort();
				    tempWord.cost = temp.cost;
					tempWord.word = temp.wordTL;
		    		listSort.add(tempWord);
	    	 }
	     }

         {
            Collections.sort(listSort, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ( (int) ((WordSort) o1).cost - (int) ((WordSort) o2).cost );
                }
                });

            for (int p=0; p<listSort.size() && p < 7; p++)
            {
                String Source = ((WordSort)listSort.get(p)).word;
                if (((WordSort)listSort.get(p)).cost <= 65)
                {
                    //if (temp.cost <=0.5)
                    {
                        String line = wordLeft + " " + ((WordSort)listSort.get(p)).word + " " + ((WordSort)listSort.get(p)).cost + " " + temp.freq + "\t" + (p+1);
                        //String line = temp.wordTL + "\n";				    		System.out.println(line);
                        writer.println(line);
                    }
                }
            }
         }
         
	     writer.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void printFileCond(String outFile, String cs)
	{
		try
		{
	     PrintStream writer = new PrintStream( new File(outFile), cs);
	     int i, size=0;
	     for (i=0; i<listMaps.size(); i++)
	     {
	    	 TranslitMaps temp = (TranslitMaps) listMaps.elementAt(i);
	    	 if (temp.freq > 29)
	    	 {
	    		 //String line = temp.wordSL + " " + temp.wordTL + "\t" + temp.cost + " " + temp.freq + "\t::\n";
	    		 String line = temp.wordTL;
	    	 	writer.println(line);
	    	 	size++;
	    	 }
	     }
	     writer.close();
	     System.out.println(size);
		}
		catch (Exception ex)
		{
			
		}
	}
	
	public static void main (String args[])
	{
		try
		{
			AlignOutput a = new AlignOutput();
			
			
			//a.readMap("/home/sanchay/tmp/TechTerms/mapNameLowThreshold.web_0");
			//a.printFileCond("/home/sanchay/tmp/Translit/Ind.hind.deleted");
			a.readMap(GlobalProperties.getHomeDirectory() + "/data/transliteration/HindiNames.FUZZY_0", GlobalProperties.getIntlString("UTF-8"));
			a.genRanks(GlobalProperties.getHomeDirectory() + "/data/transliteration/HindiNames.RANKS", GlobalProperties.getIntlString("UTF-8"));
			a.printFileCond(GlobalProperties.getHomeDirectory() + "/data/transliteration/eval/Eng.hind.test", GlobalProperties.getIntlString("UTF-8"));
/*
			a.readMap("/home/sanchay/tmp/Translit/mapListBNCLowThreshold.web.mar_0");
			a.printFileCond("/home/sanchay/tmp/Translit/Eng.mar.deleted");
			a.readMap("/home/sanchay/tmp/Translit/mapNameLowThreshold.web.mar_0");
			a.printFileCond("/home/sanchay/tmp/Translit/Ind.mar.deleted");
*/
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
