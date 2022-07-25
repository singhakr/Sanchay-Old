package sanchay.ili;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.speech.freetts.lexicon.LetterToSound;
import com.sun.speech.freetts.lexicon.LetterToSoundImpl;
import sanchay.GlobalProperties;

public class PhonemeMapping 
{
	Vector typeGrammar;
	Vector consGrammar;
	Vector matraGrammar;
	Vector vowelGrammar;
	String halant;
	Hashtable CMUSpeechDictionary;

	// CMU LTS dictioanry filepath
	final String CMU_LTS_RULES_FILEPATH = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/cmulex_lts_unix.txt";
	
	//FreeTTS' LetterToSound class object
	LetterToSound letterToSound = null;
	
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
	
	public PhonemeMapping()
	{
		typeGrammar = new Vector();
		consGrammar = new Vector();
		matraGrammar = new Vector();
		vowelGrammar = new Vector();
		halant = "\u094D";
		CMUSpeechDictionary = new Hashtable();

		//Letter to Phoneme generation class' Instantiation
		File file = new File(CMU_LTS_RULES_FILEPATH);
		try {
                        URL url = file.toURI().toURL();
//                        URL url = new URL("file://" + file.getParentFile().getAbsolutePath() + "/" + file.getName());
			letterToSound = new LetterToSoundImpl(url, false);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void readType(String file) 
	{
		try
		{
		BufferedReader lnReader = null;
		lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),GlobalProperties.getIntlString("UTF-8")));
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
	    
	    lnReader.close(); lnReader = null;
	    }catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void readCons(String file) 
	{
		try
		{
		BufferedReader lnReader = null;
		lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),GlobalProperties.getIntlString("UTF-8")));
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
	    lnReader.close(); lnReader = null;
	    
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void readMatra(String file) 
	{
		try
		{
		BufferedReader lnReader = null;
		lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),GlobalProperties.getIntlString("UTF-8")));
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
	    		//if ((splitvalue[i].equals("")==false))
	    		temp.rules.add(splitvalue[i]);
	    	}
	    	matraGrammar.add(temp);
	    }
	    
	    lnReader.close(); lnReader = null;
	    
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	public void readVowel(String file) 
	{
		try
		{
		BufferedReader lnReader = null;
		lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),GlobalProperties.getIntlString("UTF-8")));
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
	    lnReader.close(); lnReader = null;
	    
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public String preProcess(String phonemes)
	{
		String word = phonemes;
		word = word.replaceAll("[0-9]", "");
		word = word.trim();
		return word;
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
	
	public Vector getMapping(String phonemes)
	{
		phonemes = preProcess(phonemes);
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
					String test = temp.word.trim();
					if (phone[i].trim().equals(test))
						{
							options = temp.rules;
							break;
						}
				}
				for (int j=0; j<countInit; j++)
				{
					System.out.println(i + " :: " + phone[i]);
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
					String test = temp.word.trim();
					if (phone[i].trim().equals(test))
						{
							options = temp.rules;
							break;
						}
				}
				for (int j=0; j<countInit; j++)
				{
					System.out.println(i + " :: " + phone[i]);
					for (int k=0; k<options.size(); k++)
					{
						String map = maps.get(j).toString();
						map = map + options.get(k).toString();
						maps.add(map);
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
		    FileWriter writer = null;
		    writer = new FileWriter ( new File(outFile));
		    while((line = lnReader.readLine()) != null)
		    {
		    	splitline = line.split("  ");
		    	if (splitline.length == 2)
		    	{
		    		Vector maps = getMapping(splitline[1]);
		    		for (i=0; i<maps.size(); i++)
		    		{ 
		    			writer.write(splitline[0] + "\t" + maps.get(i).toString() + "\n");
		    		}
		    		//System.out.println(line);
		    	}
		    	else
		    	{
		    		System.out.println(line);
		    	}
		    }
		    writer.close(); writer = null;
		    lnReader.close(); lnReader = null;
		
		}catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Hashtable getCMUSpeechDictionary()
	{
		return CMUSpeechDictionary;
	}
	
	/**
	 * Method to read the CMU Speech Dictionary and load the contents in the CMUSpeechDictionary vector
	 * @param fileName
	 */
	public void readCmuSpeech(String fileName)
	{
		BufferedReader lnReader = null;
		String line,word;
		String[] linesplit = null;
		//Regular Expression to check if the word starts with alphabets(A-Z)
		String REGEX = "^[A-Z].*$";
		
		try 
		{
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		    while((line = lnReader.readLine()) != null)
		    {
		    	line = line.trim();
		    	if(line.matches(REGEX))
		    	{
		    		linesplit = line.split("  ");
		    		word = linesplit[0].replaceAll("[0-9\\(\\)]", "");
		    	
		    		if (!CMUSpeechDictionary.containsKey(word))
		    		{
		    			CMUSpeechDictionary.put(word,linesplit[1].trim());
		    		}
		    	}
		    }
		    
		   lnReader.close(); lnReader = null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Method to get the pronunciation using the FreeTTS engine when the particular English
	 * origin word is not found in the CMU Speech dictionary
	 * @param inputStr
	 * @return
	 */
	public String getPhonemes(String inputStr){
		StringBuffer sb = new StringBuffer("");
		
		try {
			//Lowercase the inputString as the rules of the CMU LTS dictionary is in lowercase
			inputStr = inputStr.toLowerCase();
		
			String[] phonemes = letterToSound.getPhones(inputStr,null);
			
			for (int i = 0; i < phonemes.length; i++) {
				sb.append(phonemes[i].trim().toUpperCase());
				sb.append(" ");
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		
		return sb.toString().trim();
    } 
	
	public static void main (String args[])
	{
		PhonemeMapping a = new PhonemeMapping();
		
		a.readType(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/ILI_Eng/CMU_Grammar_Type");
		a.readCons(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/ILI_Eng/CMU_Grammar_Cons1");
		a.readMatra(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/ILI_Eng/CMU_Grammar_Matra");
		a.readVowel(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/ILI_Eng/CMU_Grammar_Vowel");
//		String pre = a.preProcess("AE D V AH K EY T IH NG");
//		System.out.println(pre);
//		System.out.println(a.getType(pre));
		Vector x = a.getMapping("K AA1 NG G R AH0 S");
		for (int i=0; i<x.size(); i++)
		{
			System.out.println(x.get(i).toString());
		}
		//a.genMapList(GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/Transfer/TelCMU", GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/Transfer/TelCMU.Mapped");
	}
}
