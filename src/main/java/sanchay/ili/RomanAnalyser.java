package sanchay.ili;

import java.io.*;
import java.util.Vector;
import sanchay.GlobalProperties;

public class RomanAnalyser 
{

	Vector typeGrammar;
	Vector consGrammar;
	Vector matraGrammar;
	Vector vowelGrammar;
	Vector ambiGrammar;
	String halant;

	private class GrammarRule 
	{
		String word;
		Vector rules;
	}

	private class WordType 
	{
		String word;
		String type;
	}
	
	public RomanAnalyser()
	{
		typeGrammar = new Vector();
		consGrammar = new Vector();
		matraGrammar = new Vector();
		vowelGrammar = new Vector();
		ambiGrammar = new Vector();
		halant = "?";
	}
	
	public void readType(String file) 
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
	    	WordType temp = new WordType();
	    	temp.word = splitline[0];
	    	temp.type = splitline[1];
	    	typeGrammar.add(temp);
	    }
	    }
		
		catch(Exception ex)
		{}
	}
	
	public void readCons(String file) 
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
	    	GrammarRule temp = new GrammarRule();
	    	temp.word = splitline[0];
	    	splitvalue = null;
	    	splitvalue = splitline[1].split("[/|]");
	    	temp.rules = new Vector();
	    	for (i=0; i<splitvalue.length; i++)
	    	{ 
	    		if ((splitvalue[i].equals("")==false))
	    		temp.rules.add(splitvalue[i]);
	    	}
	    	consGrammar.add(temp);
	    }
	    }
		
		catch(Exception ex)
		{}
		
	}
	
	public void readMatra(String file) 
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
	    	GrammarRule temp = new GrammarRule();
	    	temp.word = splitline[0];
	    	splitvalue = null;
	    	splitvalue = splitline[1].split("[/|]");
	    	temp.rules = new Vector();
	    	for (i=0; i<splitvalue.length; i++)
	    	{ 
	    		if ((splitvalue[i].equals("x")==false)) // to handle 'a' case
	    		{
	    			temp.rules.add(splitvalue[i]);
	    		}
	    		else
	    		{
	    			temp.rules.add("");
	    		}
	    	}
	    	matraGrammar.add(temp);
	    }
	    }
		
		catch(Exception ex)
		{}
		
	}

	public void readVowel(String file) 
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
	    	GrammarRule temp = new GrammarRule();
	    	temp.word = splitline[0];
	    	splitvalue = null;
	    	splitvalue = splitline[1].split("[/|]");
	    	temp.rules = new Vector();
	    	for (i=0; i<splitvalue.length; i++)
	    	{ 
	    		if ((splitvalue[i].equals("")==false))
	    		temp.rules.add(splitvalue[i]);
	    	}
	    	vowelGrammar.add(temp);
	    }
	    }
		
		catch(Exception ex)
		{}
		
	}

	public void readAmbi(String file ) 
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
	    	GrammarRule temp = new GrammarRule();
	    	temp.word = splitline[0];
	    	splitvalue = null;
	    	splitvalue = splitline[1].split("[/|]");
	    	temp.rules = new Vector();
	    	for (i=0; i<splitvalue.length; i++)
	    	{ 
	    		if ((splitvalue[i].equals("")==false))
	    		temp.rules.add(splitvalue[i]);
	    	}
	    	ambiGrammar.add(temp);
	    }
	    }
		
		catch(Exception ex)
		{}
		
	}
	
	public String preProcess(String word)
	{
		String wordProcess;
		wordProcess = word.replaceAll("[0-9]", "");
		wordProcess = wordProcess.toLowerCase();
		wordProcess = wordProcess.trim();
		return wordProcess;
	}
	
	public String getType(String phonemes)
	{
		String splitline[] = phonemes.split(" ");
		int i,j;
		String typeStr = "";
		for (i=0 ; i<splitline.length; i++)
		{
			for (j=0; j<typeGrammar.size(); j++)
			{
				WordType temp = (WordType) typeGrammar.get(j);
				if (splitline[i].equals(temp.word))
				{
					typeStr = typeStr + temp.type + " ";
				}
			}
		}
		typeStr = typeStr.trim();
		return typeStr;
	}
	
	public Vector ambiAnlyz(String word)
	{
		Vector ambi = new Vector();
		ambi.add("");
		int listSize;
		while (word.length()!=0)
		{
			listSize = ambi.size();
			for (int i=0; i<listSize; i++)
			{
				boolean grammarFound = false;
				String wordList = ambi.get(i).toString();
				for (int j=0; j<ambiGrammar.size(); j++)
				{
					GrammarRule temp = (GrammarRule) ambiGrammar.get(j);
					String test = temp.word;
					if (word.startsWith(test))
					{
						grammarFound = true;
						for (int k=0; k<temp.rules.size(); k++)
						{
							String wordNew = wordList + temp.rules.get(k);
							ambi.add(wordNew);
						}
						int ambiWordLen = test.length();
						 //word = word.substring(ambiWordLen);
					}
				}
				if (grammarFound==false)
				{
					String wordAdd = word.substring(0,1);
					String wordNew = wordList + wordAdd;
					ambi.add(wordNew);
				}
			}
			
			if (word.length() == 1)
			{
				word = "";
			}
			else
			{
				word = word.substring(1);
			}
			
			for (int i=0; i<listSize; i++)
			{
				ambi.remove(0);
			}
		}
		return ambi;
	}
	
	public Vector aksharAnlyz(String word)
	{
		Vector akshar = new Vector();
		akshar.add("");
		int listSize;
		int ambiWordLen=0;
		while (word.length()!=0)
		{
			listSize = akshar.size();
			for (int i=0; i<listSize; i++)
			{
				String wordList = akshar.get(i).toString();
				for (int j=0; j<typeGrammar.size(); j++)
				{
					WordType temp = (WordType) typeGrammar.get(j);
					String test = temp.word;
					if (word.startsWith(test))
					{
						if (temp.type.equals("C"))
						{
							String wordNew = wordList + " " + test;
							akshar.add(wordNew);
						}
						if (temp.type.equals("V") && test.length() == 1)
						{
							String wordNew = wordList + " " + test;
							akshar.add(wordNew);
						}
						if (temp.type.equals("V") && test.length() == 2)
						{
							String wordNew = wordList + " " + test;
							akshar.add(wordNew);
							if (test.substring(0,1).equals(test.substring(1,2)) == false)
							{
								wordNew = wordList + " " + test.substring(0,1) + " " + test.substring(1,2);
								akshar.add(wordNew);
							}
						}
						ambiWordLen = test.length();
						break;
						 //word = word.substring(ambiWordLen);
					}
				}
				//word = word.substring(ambiWordLen);
			}
			
			if (word.length() == 1)
			{
				word = "";
			}
			else
			{
				word = word.substring(ambiWordLen);
			}
			
			for (int i=0; i<listSize; i++)
			{
				akshar.remove(0);
			}
		}
		return akshar;
	}
	
	public Vector getMapping(String phonemes)
	{
		
		String typeStr = getType(phonemes);
		String phone[] = phonemes.split(" ");
		String type[] = typeStr.split(" ");
		Vector maps = new Vector();
		maps.add("");
		
		for (int i=0; i<phone.length; i++)
		{
			if (type[i].equals("C"))
			{
				String extra;
				Vector options = new Vector();
				int countInit = maps.size();
				if (i==(phone.length-1))
				{
					extra = "";
				}
				else if (type[i+1].equals("V"))
				{
					extra = "";
				}
				else
				{
					extra = halant;
				}
				for (int j=0; j<consGrammar.size(); j++)
				{
					GrammarRule temp = (GrammarRule) consGrammar.get(j);
					String test = temp.word;
					if (phone[i].equals(test))
						{
							options = temp.rules;
							break;
						}
				}
				for (int j=0; j<countInit; j++)
				{
					//System.out.println(maps.size());
					for (int k=0; k<options.size(); k++)
					{
						String map = maps.get(j).toString();
						map = map + options.get(k).toString() + extra;
						maps.add(map);
					//	System.out.println(j + " " + k);
					}
				}
				for (int j= 0; j<countInit; j++)
				{
					maps.remove(0);
				}
				
			}
			else if (type[i].equals("V"))
			{
				Vector options = new Vector();
				int countInit = maps.size();
				Vector vGrammar = new Vector();
				// vGrammar is the Grammar to be followed, depending on what grammar to be followed 
				if (i==0)
				{
					vGrammar = vowelGrammar;
				}
				else if (type[i-1].equals("V"))
				{
					vGrammar = vowelGrammar;
				}
				else
				{
					vGrammar = matraGrammar;
				}
				for (int j=0; j<vGrammar.size(); j++)
				{
					GrammarRule temp = (GrammarRule) vGrammar.get(j);
					String test = temp.word;
					if (phone[i].equals(test))
						{
							options = temp.rules;
							break;
						}
				}
				for (int j=0; j<countInit; j++)
				{
					//System.out.println(maps.size());
					for (int k=0; k<options.size(); k++)
					{
						String map = maps.get(j).toString();
						map = map + options.get(k).toString();
						maps.add(map);
					//	System.out.println(j + " " + k);
					}
				}
				for (int j= 0; j<countInit; j++)
				{
					maps.remove(0);
				}
				
			}
		}
		return maps;
	}
	
	public Vector romanAnlyz( String word )
	{
		word = preProcess(word);
		Vector ambiList = new Vector();
		ambiList = ambiAnlyz(word);
		System.out.println("AMBI - ");
		Vector aksharList = new Vector();
		for (int i=0; i<ambiList.size(); i++)
		{
			System.out.println(ambiList.get(i));
			Vector temp = new Vector();
			temp = aksharAnlyz(ambiList.get(i).toString());
			for (int k=0; k<temp.size(); k++)
			{
				String aks = temp.get(k).toString().trim();
				if (aksharList.contains(aks) == false)
				{
					aksharList.add(aks);
					System.out.println(aks);
				}
			}
		}
		
		Vector mapList = new Vector();
		for (int i=0 ; i<aksharList.size(); i++)
		{
			Vector temp = new Vector();
			temp = getMapping(aksharList.get(i).toString());
			for (int k =0; k<temp.size(); k++)
			{
				String map = temp.get(k).toString().trim();
				if (mapList.contains(map) == false)
				{
					mapList.add(map);
					System.out.println(map);
				}
			}
		}
		
		return mapList;
	}
	
	public void genMapList(String inFile, String outFile)
	{
		try
		{
			BufferedReader lnReader = null;
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
		    String line;
		    String splitline[] = null;
		    String splitvalue[] = null;
		    int i=0;
		    //FileOutputStream writer = null;
		    //writer = new FileOutputStream( new File(outFile) , "UTF-8");
//			FileOutputStream fos = new FileOutputStream(outFile);
//			OutputStreamWriter write = OutputStreamWriter(fos);  
			PrintStream ps = new PrintStream(outFile, GlobalProperties.getIntlString("UTF-8"));

		    int count=0;
		    while((line = lnReader.readLine()) != null)
		    {
		    	splitline = line.split(" ");
		    	count++;
		    	//if (splitline.length == 1)
		    	{
		    		//System.out.println(splitline[0]);
		    		Vector maps = romanAnlyz(splitline[0].trim());
		    		for (i=0; i<maps.size(); i++)
		    		{ 
		    			ps.println(splitline[0].toUpperCase() + "\t" + maps.get(i).toString() + "\n");
		    		}
		    		//System.out.println(line);
		    	}
		    	/*else
		    	{
		    		System.out.println(line);
		    	}*/
		    }
//		    writer.close();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	public static void main (String args[])
	{
		RomanAnalyser a = new RomanAnalyser();
		a.readType(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/GrammarTypeRoman");
		a.readCons(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/ConsantMapRomanDevProperNames");
		a.readMatra(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/MatraMapRomanDev");
		a.readVowel(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/VowelMapRomanDev");
		a.readAmbi(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/AmbiGrammarRoman");
		a.romanAnlyz("sa");
		a.romanAnlyz("si");
		a.romanAnlyz("varma");
		a.genMapList(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/HindiNames", GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/HindiNames.MAP");
	}
}
