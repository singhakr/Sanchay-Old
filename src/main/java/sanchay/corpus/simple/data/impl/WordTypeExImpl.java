package sanchay.corpus.simple.data.impl;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.parallel.APCData;
import sanchay.corpus.simple.data.WordTypeEx;

public class WordTypeExImpl extends WordTypeImpl implements WordTypeEx
{
	protected Vector hypernyms; // ints
	protected Vector synonyms; // ints
	protected int phonetic_match;

	public WordTypeExImpl()
	{
		hypernyms = new Vector();
		synonyms = new Vector();
		phonetic_match = -1;
	}

	public WordTypeExImpl(int initial_capacity)
	{
		hypernyms = new Vector(initial_capacity, initial_capacity/2);
		synonyms = new Vector(initial_capacity, initial_capacity/2);
		phonetic_match = -1;
	}

	public int countHypernyms()
	{
		return hypernyms.size();
	}

	public Integer getHypernym(int num)
	{
		return (Integer) hypernyms.get(num);
	}

	public int addHypernym(int h)
	{
		hypernyms.add(Integer.valueOf(h));
		return hypernyms.size();
	}

	public Integer removeHypernym(int num)
	{
		return (Integer) hypernyms.remove(num);
	}

	public int countSynonyms()
	{
		return synonyms.size();
	}

	public Integer getSynonyms(int num)
	{
		return (Integer) synonyms.get(num);
	}

	public int addSynonyms(int h)
	{
		synonyms.add(Integer.valueOf(h));
		return synonyms.size();
	}

	public Integer removeSynonyms(int num)
	{
		return (Integer) synonyms.remove(num);
	}


	public int getPhoneticMatch()
	{
		return phonetic_match;
	}

	public void setPhoneticMatch(int pm)
	{
		phonetic_match = pm;
	}

	public void populate(APCData apcdata, String tag, String type)
	{
		String s = null;
		String file = null, file2 = null;
		String cmd = null;
		boolean notag = false;
		PrintStream ps = null;

		if(tag != null)
		{
			notag = false;
			if(type.equals("hyp") == true)
			{
				file = "/home/samar/sanchay/java/output/hyp_tmp.txt";
				cmd = "/usr/local/wordnet1.7/bin/linux/wn " + word + " -hype" + tag.toLowerCase();
				//System.out.println("^^^^^^^^^In populate: "+ type);
			}
			else
			{
				if(tag.equals("Adv") == true)
				{
					tag = "a";
				}
				file = "/home/samar/sanchay/java/output/synonyms_tmp.txt";
				cmd = "/usr/local/wordnet1.7/bin/linux/wn " + word + " -syns" + tag.toLowerCase();
				//System.out.println("^^^^^^^^^In populate: "+ type);
			}

			try
			{
				ps = new PrintStream(file);

				Process p = Runtime.getRuntime().exec(cmd);
				//int i = p.waitFor();
				//System.out.println(i);
				//if (i == 0){
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((s = stdInput.readLine()) != null) {
				ps.println(s);
			}
			//}
			/*else {
			BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			// read the output from the command
			while ((s = stdErr.readLine()) != null) {
			System.out.println(s);
			}
			}*/
			}
			catch (Exception e) {
				System.out.println(e);
			}
			ps.close();
		}
		else
		{
			notag = true;

			for(int twice = 0; twice < 2; twice++)
			{
				try
				{
				if(twice == 0)
				{
					if(type.equals("hyp") == true)
					{
						cmd = "/usr/local/wordnet1.7/bin/linux/wn " + word + " -hypen";
						file = "/home/samar/sanchay/java/output/hyn_tmp.txt";
					}
					else
					{
						cmd = "/usr/local/wordnet1.7/bin/linux/wn " + word + " -synsn";
						file = "/home/samar/sanchay/java/output/synonyms_tmp.txt";
					}

					ps = new PrintStream(file);
				}
				else
				{
					if(type.equals("hyp") == true)
					{
						cmd = "/usr/local/wordnet1.7/bin/linux/wn " + word + " -hypev";
						file2 = "/home/samar/sanchay/java/output/hyn_tmp2.txt";
					}
					else
					{
						cmd = "/usr/local/wordnet1.7/bin/linux/wn " + word + " -synsv";
						file2 = "/home/samar/sanchay/java/output/synonyms_tmp2.txt";
					}
					ps = new PrintStream(file2);
				}

				Process p = Runtime.getRuntime().exec(cmd);
				//int i = p.waitFor();
				//System.out.println(i);
				//if (i == 0){
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((s = stdInput.readLine()) != null) {
				ps.println(s);
				}
				//}
				/*else {
				BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				while ((s = stdErr.readLine()) != null) {
				System.out.println(s);
				}
				}*/
				}
				catch (Exception e) {
				System.out.println(e);
				}
				ps.close();
			}
		}



//reading each hypernym from from the file created above and
//placing them into scrtable(WordtypeTable)

		int contains = 0;
		int level = 0;

		if(notag == false)
		{
			try{
			BufferedReader lnReader = new BufferedReader(
		            new InputStreamReader(new FileInputStream(file), GlobalProperties.getIntlString("UTF-8")));

			String line;
			String splitstr1[],splitstr2[],tempstr[];
			level = 0;


				while( (line = lnReader.readLine()) != null )
				{
					if(line.contains("Sense "))        // all lines containing Sense (number)
					{
						line = lnReader.readLine();	//read next line for hypernyms
						splitstr1 = line.split(", ");	//split them if more than one
	
						//level++;	//read the upper part
						level = 0;
						if(splitstr1.length > 1)
						{
							for(int i = 0; i < splitstr1.length; i++)
							{
								//System.out.println(splitstr1[i]);
								splitstr1[i] = splitstr1[i].toLowerCase();
								contains = apcdata.getSrcWTTable().findWT(splitstr1[i]);
	
								if(contains == -1)
								{
									WordTypeExImpl wdtyex = new WordTypeExImpl();
	
									wdtyex.setWord(splitstr1[i]);
									//wdtyex.setFreq(1);
									wdtyex.setIsCorpusWord(false);
	
									int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
									if(type.equals("hyp") == true)
									{
										addHypernym(index);
										//System.out.println(".." + index);
									}
									else
									{
										addSynonyms(index);
										//System.out.println("..syn" + index);
									}
								}
								else
								{
									WordTypeEx wrdtypex = null;
									wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
									//wrdtypex.setFreq(wrdtypex.getFreq()+1);
									if(type.equals("hyp") == true)
									{
										addHypernym(contains);
										//System.out.println(".." + contains);
									}
									else
									{
										addSynonyms(contains);
										//System.out.println("..syn" + contains);
									}
								}
							}
						}
						else			//only one hypernym
						{
							//System.out.println(line);
							line = line.toLowerCase();
							contains = apcdata.getSrcWTTable().findWT(line);
	
							if(contains == -1)
							{
								WordTypeExImpl wdtyex = new WordTypeExImpl();
	
								wdtyex.setWord(line);
								//wdtyex.setFreq(1);
								wdtyex.setIsCorpusWord(false);
	
								int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
								if(type.equals("hyp") == true)
								{
									addHypernym(index);
									//System.out.println(".." + index);
								}
								else
								{
									addSynonyms(index);
									//System.out.println("..syn" + index);
								}
							}
							else
							{
								WordTypeEx wrdtypex = null;
								wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
								//wrdtypex.setFreq(wrdtypex.getFreq()+1);
								//addHypernym(contains);
								if(type.equals("hyp") == true)
								{
									addHypernym(contains);
									//System.out.println(".." + contains);
								}
								else
								{
									addSynonyms(contains);
									//System.out.println("..syn" + contains);
								}
							}
						}
					}
	
					if(line.contains("=>") == true && level < 3)
					{
						splitstr2 = line.split("=>");
						tempstr = splitstr2[1].split(", ");
						level++;
						//System.out.println("......." + level);
						if(tempstr.length > 1)
						{
							for(int i = 0; i < tempstr.length; i++)
							{
								//System.out.println(tempstr[i]);
								tempstr[i] = tempstr[i].toLowerCase();
								contains = apcdata.getSrcWTTable().findWT(tempstr[i]);
	
								if(contains == -1)
								{
									WordTypeExImpl wdtyex = new WordTypeExImpl();
	
									wdtyex.setWord(tempstr[i]);
									//wdtyex.setFreq(1);
									wdtyex.setIsCorpusWord(false);
	
									int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
									//addHypernym(index);
									if(type.equals("hyp") == true)
									{
										addHypernym(index);
										//System.out.println(".." + index);
									}
									else
									{
										addSynonyms(index);
										//System.out.println("..syn" + index);
									}
								}
								else
								{
									WordTypeEx wrdtypex = null;
									wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
									//wrdtypex.setFreq(wrdtypex.getFreq()+1);
									//addHypernym(contains);
									if(type.equals("hyp") == true)
									{
										addHypernym(contains);
										//System.out.println(".." + contains);
									}
									else
									{
										addSynonyms(contains);
										//System.out.println("..syn" + contains);
									}
								}
							}
						}
						else
						{
							//System.out.println(splitstr2[1]);
							splitstr2[1] = splitstr2[1].toLowerCase();
							contains = apcdata.getSrcWTTable().findWT(splitstr2[1]);
	
							if(contains == -1)
							{
								WordTypeExImpl wdtyex = new WordTypeExImpl();
	
								wdtyex.setWord(splitstr2[1]);
								//wdtyex.setFreq(1);
								wdtyex.setIsCorpusWord(false);
	
								int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
								//addHypernym(index);
								if(type.equals("hyp") == true)
								{
									addHypernym(index);
									//System.out.println(".." + index);
								}
								else
								{
									addSynonyms(index);
									//System.out.println("..syn" + index);
								}
							}
							else
							{
								WordTypeEx wrdtypex = null;
								wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
								//wrdtypex.setFreq(wrdtypex.getFreq()+1);
								//addHypernym(contains);
								if(type.equals("hyp") == true)
								{
									addHypernym(contains);
									//System.out.println(".." + contains);
								}
								else
								{
									addSynonyms(contains);
									//System.out.println("..syn" + contains);
								}
							}
						}
					}
					//if(level > 3)/////////TEMP
						//break;
				}
			}
			catch(IOException e)
			{
				System.out.println(GlobalProperties.getIntlString("An_error_:_e"));
			}
		}
		else
		{
			for(int k = 0; k < 2; k++)
			{
				try{
				    BufferedReader lnReader = null;
				    
					if(k == 0)
					    lnReader = new BufferedReader(
					            new InputStreamReader(new FileInputStream(file), GlobalProperties.getIntlString("UTF-8")));
					else
					    lnReader = new BufferedReader(
					            new InputStreamReader(new FileInputStream(file2), GlobalProperties.getIntlString("UTF-8")));
	
					String line;
					String splitstr1[],splitstr2[],tempstr[];
					level = 0;
	
	
					while( (line = lnReader.readLine()) != null )
					{
						if(line.contains("Sense "))        // all lines containing Sense (number)
						{
							line = lnReader.readLine();	//read next line for hypernyms
							splitstr1 = line.split(", ");	//split them if more than one
	
							//level++;
							level = 0;
							if(splitstr1.length > 1)
							{
								for(int i = 0; i < splitstr1.length; i++)
								{
									//System.out.println(splitstr1[i]);
									splitstr1[i] = splitstr1[i].toLowerCase();
									contains = apcdata.getSrcWTTable().findWT(splitstr1[i]);
	
									if(contains == -1)
									{
										WordTypeExImpl wdtyex = new WordTypeExImpl();
	
										wdtyex.setWord(splitstr1[i]);
										//wdtyex.setFreq(1);
										wdtyex.setIsCorpusWord(false);
	
										int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
										//addHypernym(index);
										if(type.equals("hyp") == true)
										{
											addHypernym(index);
											//System.out.println(".." + index);
										}
										else
										{
											addSynonyms(index);
											//System.out.println("..syn" + index);
										}
									}
									else
									{
										WordTypeEx wrdtypex = null;
										wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
										//wrdtypex.setFreq(wrdtypex.getFreq()+1);
										//addHypernym(contains);
										if(type.equals("hyp") == true)
										{
											addHypernym(contains);
											//System.out.println(".." + contains);
										}
										else
										{
											addSynonyms(contains);
											//System.out.println("..syn" + contains);
										}
									}
								}
							}
							else			//only one hypernym
							{
								//System.out.println(line);
								line = line.toLowerCase();
								contains = apcdata.getSrcWTTable().findWT(line);
	
								if(contains == -1)
								{
									WordTypeExImpl wdtyex = new WordTypeExImpl();
	
									wdtyex.setWord(line);
									//wdtyex.setFreq(1);
									wdtyex.setIsCorpusWord(false);
	
									int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
									//addHypernym(index);
									if(type.equals("hyp") == true)
									{
										addHypernym(index);
										//System.out.println(".." + index);
									}
									else
									{
										addSynonyms(index);
										//System.out.println("..syn" + index);
									}
								}
								else
								{
									WordTypeEx wrdtypex = null;
									wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
									//wrdtypex.setFreq(wrdtypex.getFreq()+1);
									//addHypernym(contains);
									if(type.equals("hyp") == true)
									{
										addHypernym(contains);
										//System.out.println(".." + contains);
									}
									else
									{
										addSynonyms(contains);
										//System.out.println("..syn" + contains);
									}
								}
							}
						}
	
						if(line.contains("=>") == true && level < 3)
						{
							splitstr2 = line.split("=>");
							tempstr = splitstr2[1].split(", ");
	
							level++;
							//System.out.println("......." + level);
							if(tempstr.length > 1)
							{
								for(int i = 0; i < tempstr.length; i++)
								{
									//System.out.println(tempstr[i]);
									tempstr[i] = tempstr[i].toLowerCase();
									contains = apcdata.getSrcWTTable().findWT(tempstr[i]);
	
									if(contains == -1)
									{
										WordTypeExImpl wdtyex = new WordTypeExImpl();
	
										wdtyex.setWord(tempstr[i]);
									//	wdtyex.setFreq(1);
										wdtyex.setIsCorpusWord(false);
	
										int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
										//addHypernym(index);
										if(type.equals("hyp") == true)
										{
											addHypernym(index);
											//System.out.println(".." + index);
										}
										else
										{
											addSynonyms(index);
											//System.out.println("..syn" + index);
										}
									}
									else
									{
										WordTypeEx wrdtypex = null;
										wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
									//	wrdtypex.setFreq(wrdtypex.getFreq()+1);
										//addHypernym(contains);
										if(type.equals("hyp") == true)
										{
											addHypernym(contains);
											//System.out.println(".." + contains);
										}
										else
										{
											addSynonyms(contains);
											//System.out.println("..syn" + contains);
										}
									}
								}
							}
							else
							{
								//System.out.println(splitstr2[1]);
								splitstr2[1] = splitstr2[1].toLowerCase();
								contains = apcdata.getSrcWTTable().findWT(splitstr2[1]);
	
								if(contains == -1)
								{
									WordTypeExImpl wdtyex = new WordTypeExImpl();
	
									wdtyex.setWord(splitstr2[1]);
									//wdtyex.setFreq(1);
									wdtyex.setIsCorpusWord(false);
	
									int index = apcdata.getSrcWTTable().addWT(wdtyex) - 1;
									//addHypernym(index);
									if(type.equals("hyp") == true)
									{
										addHypernym(index);
										//System.out.println(".." + index);
									}
									else
									{
										addSynonyms(index);
										//System.out.println("..syn" + index);
									}
								}
								else
								{
									WordTypeEx wrdtypex = null;
									wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(contains);
									//wrdtypex.setFreq(wrdtypex.getFreq()+1);
									//addHypernym(contains);
									if(type.equals("hyp") == true)
									{
										addHypernym(contains);
										//System.out.println(".." + contains);
									}
									else
									{
										addSynonyms(contains);
										//System.out.println("..syn" + contains);
									}
								}
							}
						}
	
						//if(level > 3)////////TEMP
						//{
							//break;
						//}
					}
				}
				catch(IOException e)
				{
					System.out.println(GlobalProperties.getIntlString("An_error_:_e"));
				}
			}
		}
	}

	public int print(PrintStream ps)
	{
		Integer index;
		int count = 0;

		ps.print(GlobalProperties.getIntlString("Word:_"));
		ps.println(getWord());

		/*ps.print("Meaning: ");
		ps.println(getMeaning());*/

		ps.print(GlobalProperties.getIntlString("Equivalent_Word_Index:_"));
		ps.println(getEqWord());

		ps.print(GlobalProperties.getIntlString("Word_Signature:_"));
		ps.println(getWordSig());

		ps.print(GlobalProperties.getIntlString("Word_Frequency:_"));
		ps.println(getFreq());

		ps.print(GlobalProperties.getIntlString("Is_Corpus_Word:_"));
		ps.println(getIsCorpusWord());

		ps.print(GlobalProperties.getIntlString("Is_Substitute_Word:_"));
		ps.println(getIsSubstituteWord());

		count = countHypernyms();
		ps.print(GlobalProperties.getIntlString("Hypernyms_index:_"));
		for(int i = 0; i < count; i++)
		{
			//ps.println(i);
			index = getHypernym(i);
			ps.print(index.intValue());
			ps.print(" ");
		}

		ps.print("\n");
		return countHypernyms();
	}
}
