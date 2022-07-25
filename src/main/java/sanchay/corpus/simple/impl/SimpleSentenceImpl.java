package sanchay.corpus.simple.impl;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.*;
import sanchay.corpus.simple.*;
import sanchay.corpus.parallel.*;
import sanchay.corpus.simple.data.*;
import sanchay.corpus.simple.data.impl.*;
import sanchay.table.SanchayTableModel;

public class SimpleSentenceImpl extends Sentence implements SimpleSentence, SentenceFeatures
{
	protected int[] words; // indices
	protected int[] sbt_words; //indices of substituted word
	//protected Vector sbt_words_all; //indices of substituted words for each src word
	//protected int count;
	protected int sentence_length; //num. of chars in the sentence
	protected int signature; // sum of words' signatures
	protected int weighted_length;

	public SimpleSentenceImpl(int size)
	{
		words = new int[size];
		sbt_words = new int[size];
        sbt_words[0] = -1;
		//sbt_words_all = new Vector();
		//word_count = size;
	}

	public int countWords()
	{
		return words.length;	//word count
	}

	public int getWord(int num)
	{
		return words[num];
	}

	public int setWord(int ind /* sentence index */, int wd /* table index */)
	{
		return words[ind] = wd;
	}

    public void insertWord(int ind /* sentence index */, int wd /* table index */)
    {
        Vector<Integer> vec = new Vector<Integer>(words.length + 1);

        for (int i = 0; i < words.length; i++)
        {
            vec.add(words[i]);
        }

        vec.insertElementAt(wd, ind);

        int count = vec.size();
        words = new int[count];

        for (int i = 0; i < words.length; i++)
        {
            words[i] = vec.get(i).intValue();
        }
    }

	public int countSbtWords()
	{
		return sbt_words.length;
	}

	public int getWordSbt(int num)
	{
		return sbt_words[num];
	}

	public int setWordSbt(int ind , int wd )  // sentence index ,  table index
	{
		return sbt_words[ind] = wd;
	}

	/*public int getWordSbtAllCount()
	{
		return sbt_words_all.size();
	}

	public int getWordSbtAll(int num)
	{
		return ((Integer)sbt_words_all.get(num)).intValue();
	}

	public void setWordSbtAll(int wd )// table index
	{
		if(sbt_words_all.contains(Integer.valueOf(wd)) == false)
			sbt_words_all.add(Integer.valueOf(wd));
	}*/

    public void calculateSentenceLength(SanchayTableModel wttbl)
    {
        String str = getSentenceString(wttbl);

        sentence_length = str.length();
    }

    public void calculateSignature(SanchayTableModel wttbl)
    {
        String str = getSentenceString(wttbl);

        sentence_length = str.length();

        for (int i = 0; i < sentence_length; i++)
        {
            signature += (int) str.charAt(i);
        }
    }

	public int getSentenceLength()
	{
		return sentence_length;
	}

	public void setSentenceLength(int senlen)
	{
		sentence_length = senlen;
	}

	/*public int getWordCount()
	{
		return word_count;
	}

	public void setWordCount(int c)
	{
		word_count = c;
	}*/

	public int getSignature()
	{
		return signature;
	}

	public void setSignature(int sg)
	{
		signature = sg;
	}

	public void setWeightedLength(char type)
    {
        setWeightedLength(null, type);
    }

	public void setWeightedLength(APCProperties apcpro, char type)
	{
		if( type == 's' )
		{
			//weighted_length = words.length;
			weighted_length = (int) (
                (
				(
					apcpro.getSignatureWeight() * ( (double) signature / (double) apcpro.getSMaxSignature() ) +
					apcpro.getCharcntWeight() * ( (double) sentence_length / (double) apcpro.getSMaxCharcnt() ) +
					apcpro.getWrdcntWeight() * ( (double) words.length / (double) apcpro.getSMaxWrdcnt() )
				)
				* (double) apcpro.getSMaxWrdcnt() * (double) apcpro.getSMaxCharcnt()
                )
			);
		}

		if( type == 't' )
		{
			//weighted_length = words.length;
			weighted_length = (int) (
                (
				(
					apcpro.getSignatureWeight() * ( (double) signature / (double) apcpro.getTMaxSignature() ) +
					apcpro.getCharcntWeight() * ( (double) sentence_length / (double) apcpro.getTMaxCharcnt() ) +
					apcpro.getWrdcntWeight() * ( (double) words.length / (double) apcpro.getTMaxWrdcnt() )
				)
				* (double) apcpro.getTMaxWrdcnt() * (double) apcpro.getSMaxCharcnt()
                )
			);
		}
		//System.out.println("--------- " +  weighted_length);
		//System.out.println("+++++++" + words.length + " " + apcpro.getSMaxWrdcnt() + " " + apcpro.getWrdcntWeight() + " " + weighted_length );
	}

	public int getWeightedLength()
	{
		return weighted_length;
	}

	public Vector getWords(SanchayTableModel wttbl)
	{
		int c = countWords();
		Vector ret = new Vector(c, c/2);

		for(int i = 0; i < c; i++)
		{
			int ind = getWord(i);

            Vector row = wttbl.getRow(ind);
			String wrd = (String) row.get(0);

			ret.add( wrd );
		}

		return ret;
	}

	public String getSentenceString(SanchayTableModel wttbl)
	{
		String ret = "";
		int c = countWords();

		for(int i = 0; i < c; i++)
		{
			int ind = getWord(i);

            Vector row = wttbl.getRow(ind);
			String wrd = (String) row.get(0);

			ret += wrd + " ";
		}

		ret = ret.trim();

		return ret;
	}

	public String getWordString(int i, SanchayTableModel wttbl)
	{
        String wrd = (String) wttbl.getValueAt(i, 0);

		return wrd;
	}

	public double getCommonWords(Sentence sen, APCData apcdata)
	{
		int count, i, j, index;
		double common;
		WordTypeExImpl wrdtypex = null;
		//int sbsind[] = new int[getWordSbtCount()];
		//int sbsind[] =
		//System.out.println("..........");
		Integer sbsind[] = new Integer[1];
		Vector temp = new Vector();

		for(i = 0; i < countWords(); i++)
		{
			index = getWord(i);
			wrdtypex = (WordTypeExImpl) apcdata.getSrcWTTable().getWT(index);

			temp.add( Integer.valueOf(wrdtypex.getEqWord()) );

			/*for(j = 0; j < wrdtypex.getWordSbtAllCount(); j++)
			{
				temp.add( Integer.valueOf(wrdtypex.getWordSbtAll(j)) );
			}*/
		}

		sbsind = (Integer [])temp.toArray(sbsind);
		int hndind[] = new int[sen.countWords()];

		//for(i = 0; i < getWordSbtCount(); i++)
		//	sbsind[i] = getWordSbt(i);

		for(i = 0; i < sen.countWords(); i++)
			hndind[i] = sen.getWord(i);


		Arrays.sort(sbsind);
		Arrays.sort(hndind);

		count = 0;
		common = 0.0;
		if(sbsind.length > 0)
		{
			for(i = 0; i < sbsind.length; i++)
			{
				if((sbsind[i]).intValue() == -1)
					continue;

				for(j = count; j < hndind.length; j++)
				{
					if(hndind[j] == -1)
					{
						count++;
						continue;
					}

					if((sbsind[i]).intValue() < hndind[j])
						break;

					if((sbsind[i]).intValue() == hndind[j])
					{
						count++;
						common++;
						break;
					}
					count++;
				}
			}

			common = (double)common / (double)countWords();
			return common;
		}
		else
			return 0.0;
	}

	public double getCommonHypernyms(Sentence sen, APCData apcdata)
	{
		int count, i, j, index, engindex;
		double common;
		Vector engind = new Vector();
		Vector sbsind = new Vector();
		Object [] engindarr;
		Object [] sbsindarr;
		WordTypeEx wrdtypex = null;
		WordType wrdtyp = null;

		for(i = 0; i < countWords(); i++)
		{
			index = getWord(i);
			wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(index);
			for(j = 0; j < wrdtypex.countHypernyms(); j++)
			{
				engind.add(wrdtypex.getHypernym(j));
			}
		}

		for(i = 0; i < sen.countWords(); i++)
		{
			index = sen.getWord(i);
			wrdtyp = apcdata.getTgtWTTable().getWT(index);
			engindex = wrdtyp.getEqWord();

			if ( engindex != -1)
			{
				wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(engindex);
				for(j = 0; j < wrdtypex.countHypernyms(); j++)
				{
					sbsind.add(wrdtypex.getHypernym(j));
				}
			}
		}

		engindarr = engind.toArray();
		sbsindarr = sbsind.toArray();

		Arrays.sort(engindarr);
		Arrays.sort(sbsindarr);

		count = 0;
		common = 0.0;
		for(i = 0; i < engindarr.length; i++)
		{
			if( Integer.parseInt(engindarr[i].toString()) == -1)
				continue;

			for(j = count; j < sbsindarr.length; j++)
			{
				if(Integer.parseInt(sbsindarr[j].toString()) == -1)
				{
					count++;
					continue;
				}

				if(Integer.parseInt(engindarr[i].toString()) < Integer.parseInt(sbsindarr[j].toString()))
					break;

				if(Integer.parseInt(engindarr[i].toString()) == Integer.parseInt(sbsindarr[j].toString()))
				{
					count++;
					common++;
					break;
				}
				count++;
			}
		}

		common = (double)common / (double)countWords();
		//System.out.println("In comm hypernyms:" + common);
		return common;
	}

	public double get_Phntc_Num_Match(Sentence sen, APCData apcdata)
	{
		Vector src_phnt_in = new Vector(500, 50);
		//Vector src_num = new Vector(500, 50);
		double common_num = 0.0;
		double common_phnt = 0.0;
		double common_wrd = 0.0;

		boolean found = false;


		for(int i = 0; i < countWords(); i++)
		{
			int wrdind = getWord(i);
			WordTypeExImpl wtx = (WordTypeExImpl) apcdata.getSrcWTTable().getWT(wrdind);
			if(wtx.getIsPhonetic() == true)
			{
				src_phnt_in.add( Integer.valueOf(wtx.getEqWord()) );
			}
		}

		for(int i = 0; i < sen.countWords(); i++)
		{
			int wrdind = sen.getWord(i);
			WordType wt = apcdata.getTgtWTTable().getWT(wrdind);
			String wrd = wt.getWord();
			String strip_hnd = removeVowels(wrd, "hnd");

			if(found == true)
				break;

			for(int j = 0; j < countWords(); j++)
			{
				String src_wrd = ((WordTypeExImpl) apcdata.getSrcWTTable().getWT(getWord(j))).getWord();
				if(src_wrd.equals(wrd) == true)
				{
					//System.out.println(src_wrd + " " + wrd);
					//found = true;
					common_num++;
					break;
				}
				/*else if( ( ((WordTypeEx) apcdata.getSrcWTTable().getWT(getWord(j))).getTag() ) != null &&
					  ( ((WordTypeEx) apcdata.getSrcWTTable().getWT(getWord(j))).getTag() ).equals("N") ==
					    false)
				{
					if( (stripVowel(src_wrd, "eng")).equals(strip_hnd) == true)
					{
						common_wrd++;
						break;
					}
				}*/
			}

			if(src_phnt_in != null)
			{
				for(int j = 0; j < src_phnt_in.size(); j++)
				{
					if(wrdind == ((Integer)src_phnt_in.get(j)).intValue())
					{
						//found = true;
						common_phnt++;
						break;
					}
				}
			}
		}

		//common = (double)common / (double)getWordCount();
		if(common_phnt >= 2.0 || common_num >= 3.0)///TEMP//////common_phnt >= 3.0)//common_phnt >= 2.0 )//|| common_num >= 2.0)//found == true)common_wrd >= 5.0
			return 1.0;
		else
			return 0.0;
	}

	public double getCommonSynonyms(Sentence sen, APCData apcdata)
	{
		int count, i, j, index, engindex;
		double common;
		Vector engind = new Vector();
		Vector sbsind = new Vector();
		Object [] engindarr;
		Object [] sbsindarr;
		WordTypeEx wrdtypex = null;
		WordType wrdtyp = null;

		for(i = 0; i < countWords(); i++)
		{
			index = getWord(i);
			wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(index);
			for(j = 0; j < wrdtypex.countSynonyms(); j++)
			{
				engind.add(wrdtypex.getSynonyms(j));
			}
		}

		for(i = 0; i < sen.countWords(); i++)
		{
			index = sen.getWord(i);
			wrdtyp = apcdata.getTgtWTTable().getWT(index);
			engindex = wrdtyp.getEqWord();

			if ( engindex != -1)
			{
				wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(engindex);
				for(j = 0; j < wrdtypex.countSynonyms(); j++)
				{
					sbsind.add(wrdtypex.getSynonyms(j));
				}
			}
		}

		engindarr = engind.toArray();
		sbsindarr = sbsind.toArray();

		Arrays.sort(engindarr);
		Arrays.sort(sbsindarr);

		count = 0;
		common = 0.0;
		for(i = 0; i < engindarr.length; i++)
		{
			if( Integer.parseInt(engindarr[i].toString()) == -1)
				continue;

			for(j = count; j < sbsindarr.length; j++)
			{
				if(Integer.parseInt(sbsindarr[j].toString()) == -1)
				{
					count++;
					continue;
				}

				if(Integer.parseInt(engindarr[i].toString()) < Integer.parseInt(sbsindarr[j].toString()))
					break;

				if(Integer.parseInt(engindarr[i].toString()) == Integer.parseInt(sbsindarr[j].toString()))
				{
					count++;
					common++;
					break;
				}
				count++;
			}
		}

		common = (double)common / (double)countWords();
		//System.out.println("In comm synonyms:" + common);
		return common;
	}

	public String removeVowels(String word, String lang)
	{
		String trim = "";

		if(lang.equals("eng") == true)
		{
			char buf_src[] = new char[word.length()];
			word.getChars(0,word.length(),buf_src,0);

			for(int j = 0; j < buf_src.length; j++)
			{
				if(buf_src[j] == 'a' || buf_src[j] == 'e' || buf_src[j] == 'i' || buf_src[j] == 'o' || buf_src[j] == 'u')
				{}
				else
					trim = trim + buf_src[j];
			}
		}
		else
		{
			char buf_tgt[] = new char[word.length()];
			word.getChars(0,word.length(),buf_tgt,0);

			for(int k = 0; k < buf_tgt.length; k++)
			{
				if(buf_tgt[k] == 'a' || buf_tgt[k] == 'A' || buf_tgt[k] == 'e' || buf_tgt[k] == 'E' || buf_tgt[k] == 'i' || buf_tgt[k] == 'I' || buf_tgt[k] == 'o' || buf_tgt[k] == 'O' || buf_tgt[k] == 'u' || buf_tgt[k] == 'U' )
				{
				}
				else
				{
					if(buf_tgt[k] == 'w' || buf_tgt[k] == 'W')
					{
						if(buf_tgt[k] == 'W')
						{
							trim = trim + "th";
						}
						else
							trim = trim + "t";
					}
					else if(buf_tgt[k] == 'x' || buf_tgt[k] == 'X')
					{
						if(buf_tgt[k] == 'X')
						{
							trim = trim + "dh";
						}
						else
							trim = trim + "d";
					}
					else if(buf_tgt[k] == 'c' || buf_tgt[k] == 'C')
					{
						if(buf_tgt[k] == 'C')
						{
							trim = trim + "chh";
						}
						else
							trim = trim + "ch";
					}
					else if(buf_tgt[k] == 'M')
					{
						trim = trim + "n";
					}
					else if(buf_tgt[k] == 'z')
					{
						trim = trim + "n";
					}
					else if(buf_tgt[k] == 'q')
					{
						trim = trim + "r";
					}
					else if(buf_tgt[k] == 'R')
					{
						trim = trim + "sh";
					}
					else if(Character.isUpperCase(buf_tgt[k]) == true)
					{
						trim = trim + Character.toString(Character.toLowerCase(buf_tgt[k])) + "h";
					}
					else
					{
						trim = trim + buf_tgt[k];
					}
				}
			}
		}

		return trim;
	}

	/*public double getCommonSynonyms(Sentence sen, APCData apcdata)
	{
		int i, index, engindex;
		double count;
		Hashtable engsyn = new Hashtable(50, (float)0.25);
		Hashtable hndsyn = new Hashtable(50, (float)0.25);

		WordTypeEx wrdtypex = null;
		WordType wrdtyp = null;
		PrintStream ps = null;
		String s = null;

		for(i = 0; i < getWordCount(); i++)
		{
			index = getWord(i);
			wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(index);
			String wrd = wrdtypex.getWord();
			String tag = wrdtypex.getTag();

			if(wrd == null || tag == null)
			{
				continue;
			}
			//System.out.println(wrd + " " + tag + " " + wrdtypex);
			if(tag.equals("Adv") == true)
			{
				tag = "a";
			}
			String file = "/home/samar/sanchay/java/output/synonyms_tmp.txt";
			String cmd = "/usr/local/wordnet1.7/bin/linux/wn " + wrd + " -syns" + tag.toLowerCase();

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
				//else {
				//BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				//while ((s = stdErr.readLine()) != null) {
				//System.out.println(s);
				//}
				//}
			}
			catch (Exception e) {
				System.out.println(e);
			}
			ps.close();



			try
			{
				BufferedReader lnReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "UTF-8"));
					
				String line;
				String splitstr1[],splitstr2[],tempstr[];
				//Pattern p = Pattern.compile("[\\s]?");
				//Matcher mat;
				//boolean bool = false;


				while( (line = lnReader.readLine()) != null )
				{
					if(line.contains("Sense "))        // all lines containing Sense (number)
					{
						line = lnReader.readLine();	//read next line for hypernyms
						splitstr1 = line.split(", ");	//split them if more than one
						if(splitstr1.length > 1)
						{
							for(int j = 0; j < splitstr1.length; j++)
							{
								//System.out.println(splitstr1[i]);
								splitstr1[j] = splitstr1[j].toLowerCase();
								engsyn.put(splitstr1[j], new Integer(1));
							}
						}
						else			//only one hypernym
						{
							//System.out.println(line);
							line = line.toLowerCase();
							engsyn.put(line, new Integer(1));
						}
					}

					if(line.contains("=>"))
					{
						splitstr2 = line.split("=>");
						//mat = pat.matcher(patrnstr[z]);
						//bool = mat.matches();
						//System.out.println(line + "..." + splitstr2);
						tempstr = splitstr2[1].split(", ");
						if(tempstr.length > 1)
						{
							for(int j = 0; j < tempstr.length; j++)
							{
								//System.out.println(tempstr[i]);
								tempstr[j] = tempstr[j].toLowerCase();
								engsyn.put(tempstr[j], new Integer(1));
							}
						}
						else
						{
							//System.out.println(splitstr2[1]);
							splitstr2[1] = splitstr2[1].toLowerCase();
							engsyn.put(splitstr2[1], new Integer(1));
						}
					}
				}
			}
			catch(IOException e)
			{
				System.out.println("An error : e");
			}
		}


//////////////////////////////////////
		for(i = 0; i < sen.getWordCount(); i++)
		{
			index = sen.getWord(i);
			wrdtyp = apcdata.getTgtWTTable().getWT(index);
			engindex = wrdtyp.getEqWord();

			if ( engindex != -1)
			{
				wrdtypex = (WordTypeEx) apcdata.getSrcWTTable().getWT(engindex);

				String wrd = wrdtypex.getWord();
				String tag = wrdtypex.getTag();

				if(wrd == null || tag == null)
				{
					continue;
				}

				String file = "/home/samar/sanchay/java/output/synonyms_tmp.txt";
				String cmd = "/usr/local/wordnet1.7/bin/linux/wn " + wrd + " -syns" + tag.toLowerCase();

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
					//else {
					//BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					// read the output from the command
					//while ((s = stdErr.readLine()) != null) {
					//System.out.println(s);
					//}
					//}
				}
				catch (Exception e) {
					System.out.println(e);
				}
				ps.close();



				try
				{
					BufferedReader lnReader = new BufferedReader(
					            new InputStreamReader(new FileInputStream(file), "UTF-8"));

					String line;
					String splitstr1[],splitstr2[],tempstr[];

					while( (line = lnReader.readLine()) != null )
					{
						if(line.contains("Sense "))        // all lines containing Sense (number)
						{
							line = lnReader.readLine();	//read next line for hypernyms
							splitstr1 = line.split(", ");	//split them if more than one
							if(splitstr1.length > 1)
							{
								for(int j = 0; j < splitstr1.length; j++)
								{
									//System.out.println(splitstr1[i]);
									splitstr1[j] = splitstr1[j].toLowerCase();
									hndsyn.put(splitstr1[j], new Integer(1));
								}
							}
							else			//only one hypernym
							{
								//System.out.println(line);
								line = line.toLowerCase();
								hndsyn.put(line, new Integer(1));
							}
						}

						if(line.contains("=>"))
						{
							splitstr2 = line.split("=>");
							tempstr = splitstr2[1].split(", ");
							if(tempstr.length > 1)
							{
								for(int j = 0; j < tempstr.length; j++)
								{
									//System.out.println(tempstr[i]);
									tempstr[j] = tempstr[j].toLowerCase();
									hndsyn.put(tempstr[j], new Integer(1));
								}
							}
							else
							{
								//System.out.println(splitstr2[1]);
								splitstr2[1] = splitstr2[1].toLowerCase();
								hndsyn.put(splitstr2[1], new Integer(1));
							}
						}
					}
				}
				catch(IOException e)
				{
					System.out.println("An error : e");
				}
			}
		}

		count = 0.0;
		Enumeration enm = (Enumeration) engsyn.keys();

		while( enm.hasMoreElements() )
		{
			String key = (String)enm.nextElement();
			if(hndsyn.containsKey(key) == true)
			{
				count++;
			}
		}

		count = (double)count / (double)getWordCount();
		engsyn.clear();
		hndsyn.clear();

		return count;
	}*/

	public void print(PrintStream ps)
	{
		ps.print(GlobalProperties.getIntlString("Index's:____"));
		for(int i = 0; i < countWords(); i++)
		{
			ps.print(words[i] + " ");
		}

		/*ps.print("\nTarget Index's:   ");
		for(int i = 0; i < getWordCount(); i++)
		{
			ps.print(sbt_words[i] + " ");
		}

		ps.print("\nAll Meanings:    ");
		for(int i = 0; i < sbt_words_all.size(); i++)
		{
			ps.print(sbt_words_all.get(i) + " ");
		}*/

		ps.println(GlobalProperties.getIntlString("\nTotal_Word_Count:_") + countWords());
		ps.println(GlobalProperties.getIntlString("Sentence_Length:_") + sentence_length);
		ps.println(GlobalProperties.getIntlString("Signature:_") + signature);
	}

    public void printCounts(SanchayTableModel wtTable, PrintStream ps)
    {
        ps.println("Sentence: " + getSentenceString(wtTable));
        ps.println("\t Character count: " + getSentenceLength());
        ps.println("\t Word count: " + countWords());
        ps.println("\t Signature: " + getSignature());
        ps.println("\t Weighted Sentence Length: " + getWeightedLength());
    }
}
    /*if( type == 's' )
		{
			//weighted_length = words.length;
			weighted_length = (int) (
				(
					Double.valueOf(apcpro.get("signature_weight")).doubleValue() * ( (double) signature / Double.valueOf(apcpro.get("smax_signature")).doubleValue() ) + 
					Double.valueOf(apcpro.get("charcnt_weight")).doubleValue() * ( (double) sentence_length / Double.valueOf(apcpro.get("smax_charcnt")).doubleValue() ) +
					Double.valueOf(apcpro.get("wrdcnt_weight")).doubleValue() * ( (double) words.length / Double.valueOf(apcpro.get("smax_wrdcnt")).doubleValue() )
				)
				* Double.valueOf(apcpro.get("smax_wrdcnt")).doubleValue()
			);
		}
		
		if( type == 't' )
		{
			//weighted_length = words.length;
			weighted_length = (int) (
				(
					Double.valueOf(apcpro.get("signature_weight")).doubleValue() * ( (double) signature / Double.valueOf(apcpro.get("tmax_signature")).doubleValue() ) + 
					Double.valueOf(apcpro.get("charcnt_weight")).doubleValue() * ( (double) sentence_length / Double.valueOf(apcpro.get("tmax_charcnt")).doubleValue() ) +
					Double.valueOf(apcpro.get("wrdcnt_weight")).doubleValue() * ( (double) words.length / Double.valueOf(apcpro.get("tmax_wrdcnt")).doubleValue() )
				)
				* Double.valueOf(apcpro.get("smax_wrdcnt")).doubleValue()
			);
		}*/
