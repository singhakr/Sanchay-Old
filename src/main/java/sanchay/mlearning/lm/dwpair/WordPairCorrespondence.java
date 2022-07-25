package sanchay.mlearning.lm.dwpair;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.*;
import sanchay.corpus.parallel.APCData;
import sanchay.corpus.simple.data.*;

public class WordPairCorrespondence
{
	// pairs (source-target) -- word_s_wd1::word_s_wd2::freq#word_t_wd1::word_t_wd2::freq as the key and frequency as value
	protected Hashtable pairs;
	protected int threshold;
	protected double max_mi;
	protected double min_mi;

	public WordPairCorrespondence(int initial)
	{
		//alignedCorpus = ac;

		pairs =  new Hashtable(initial, (float)0.40);
		threshold = 3;
		max_mi = -9999;
		min_mi = 9999;
	}

	public int countPairs()
	{
		return pairs.size();
	}

	public Enumeration getKeys()
	{
		return pairs.keys();
	}

	public Vector getEntry(String key)
	{
		return (Vector) pairs.get(key);
	}

	public void addEntry(String key)
	{
		Vector vect = getEntry(key);
		int temp;

		if(vect == null)
		{
			//pairs.put(key, new Integer(1));
			Vector tempv = new Vector(2);
			tempv.add(new Integer(1));
			pairs.put(key, tempv);
		}
		else
		{
			Vector tempv = new Vector(2);
			int freq = ((Integer) vect.get(0)).intValue();
			temp = freq + 1;
			tempv.add(new Integer(temp));
			pairs.put(key, tempv);
		}

	}

	public void addMI(String key, double mi)
	{
		Vector vect = getEntry(key);

		Vector tempv = new Vector(2);
		int freq = ((Integer) vect.get(0)).intValue();
		tempv.add(new Integer(freq));
		tempv.add(new Double(mi));
		pairs.put(key, tempv);
	}

	public void removeEntry(String key)
	{
		Vector vect = getEntry(key);

		if(vect != null)
		{
			pairs.remove(key);
		}
	}

	public double getTotalFreq()
	{
		double total_freq = 0;

		Enumeration enm = getKeys();

		while( enm.hasMoreElements() )
		{
			String key = (String) enm.nextElement();
			total_freq =+ (double) (( (Integer)(getEntry(key).get(0))).intValue());
		}

		return total_freq;
	}

	public void train(Vector sp_backtrace_list, Corpus srccorpus, Corpus tgtcorpus/*, APCData apcdata*/, WordPairTable wordpairtable_s, WordPairTable wordpairtable_t) //throws FileNotFoundException, IOException
	{
		/*
        BufferedReader lnReader = new BufferedReader(
        	new InputStreamReader(new FileInputStream(f), "UTF-8"));
		
		String line, line_t;*/
		Sentence srcsen,tgtsen;
		String[] splitstr;
		String[] splitstr_t;
		String key,key_t,master_key;
		int count,count_t;
		int contains1,contains2,contains1_t,contains2_t;
		Iterator itr = sp_backtrace_list.iterator();
		String bead_line = "";

		WordPair temp_wp = null;
		int cnt = 0;

		//for(int k = 0; k < srccorpus.getSentenceCount(); k++)
		while(itr.hasNext())
		{
			bead_line = (String) itr.next();

			String[] bead_line_elements = bead_line.split(":");

			if(cnt%100 == 0)
			{
				System.out.print(GlobalProperties.getIntlString("\rComputed:_") + cnt);
			}
			cnt++;

			int bead_pos_1 = Integer.parseInt(bead_line_elements[0]);
			int bead_pos_2 = Integer.parseInt(bead_line_elements[1]);

			if(bead_line_elements[2].equals(GlobalProperties.getIntlString("match")) == true)
			{
				srcsen = srccorpus.getSentence(bead_pos_1);
				count = srcsen.countWords();

				tgtsen = tgtcorpus.getSentence(bead_pos_2);
				count_t = tgtsen.countWords();

				for(int i = 0; i < count - 1; i++)
				{
					contains1 = srcsen.getWord(i);
					//contains1 = apcdata.getSrcWTTable().findWT(splitstr[i]);
					//if(contains1 == -1)
					//	System.out.println("---------"  + splitstr[i]);

					for(int i_thrh = i + 1; (i_thrh < i + threshold && i_thrh < count); i_thrh++)
					{
						contains2 = srcsen.getWord(i_thrh);
						//contains2 = apcdata.getSrcWTTable().findWT(splitstr[i_thrh]);
						//if(contains2 == -1)
						//	System.out.println("==========="  + splitstr[i_thrh]);

						int tmp = i_thrh - i;

						key = Integer.toString(contains1) + "::" + Integer.toString(contains2) + "::" + Integer.toString(tmp);
						int present = wordpairtable_s.findWP(key);

						if(present == -1)
						{
							//temp_wp = wordpairtable_s.getWP(present);
							//long temp = temp_wp.getFreq();
							//if( temp < 1 )
							//{
							//	wordpairtable_s.removeWP(present);
							//	continue;
							//}
							continue;
						}
						//else
						//	continue;
						/*else
						{
							WordPair wordpair_s = new WordPair();
							wordpair_s.setWord1(contains1);
							wordpair_s.setWord2(contains2);
							wordpair_s.setDistance(i_thrh - i);
							wordpair_s.setFreq(1);

							wordpairtable_s.addWP(wordpair_s);
						}*/

						for(int j = 0; j < count_t; j++)
						{
							contains1_t = tgtsen.getWord(j);
							//contains1_t = apcdata.getTgtWTTable().findWT(splitstr_t[j]);
							//if(contains1_t == -1)
							//	System.out.println("**********"  + splitstr_t[j]);

							for(int j_thrh = j + 1; (j_thrh < j + threshold && j_thrh < count_t - 1); j_thrh++)
							{
								contains2_t = tgtsen.getWord(j_thrh);
								//contains2_t = apcdata.getTgtWTTable().findWT(splitstr_t[j_thrh]);
								//if(contains2_t == -1)
								//	System.out.println("..........."  + splitstr_t[j_thrh]);

								//System.out.println(".........");
								int tmp2 = j_thrh - j;

								key_t = Integer.toString(contains1_t) + "::" + Integer.toString(contains2_t) + "::" + Integer.toString(tmp2);
								present = wordpairtable_t.findWP(key_t);

								if(present == -1)
								{
								//	System.out.println("XXX");
									//temp_wp = wordpairtable_t.getWP(present);
									//long temp = temp_wp.getFreq();
									//if(temp < 1)
									//{
									//	wordpairtable_t.removeWP(present);
									//	continue;
									//}
									//temp_wp.setFreq(temp+1);
									continue;
								}
								//else
								//	continue;
								/*else
								{
									WordPair wordpair_t = new WordPair();
									wordpair_t.setWord1(contains1_t);
									wordpair_t.setWord2(contains2_t);
									wordpair_t.setDistance(j_thrh - j);
									wordpair_t.setFreq(1);

									wordpairtable_t.addWP(wordpair_t);
								}*/
								String masterkey = key + "#" + key_t;
								addEntry(masterkey);
								//System.out.println("\nAdding as bi-pair " + masterkey);
							}
						}
					}
				}
			}
		}

		System.out.println(GlobalProperties.getIntlString("\nWord_Bi-Pair_Size:_") + countPairs());
		calculateMI(wordpairtable_s, wordpairtable_t);
	}

	public void calculateMI(WordPairTable wordpairtable_s, WordPairTable wordpairtable_t)
	{
		Enumeration enm = getKeys();
		double total_freq = getTotalFreq();
		int cnt = 0;
		System.out.println(GlobalProperties.getIntlString("\nCalculating_MI_for_bi-pair"));

		while( enm.hasMoreElements() )
		{
			if(cnt%100 == 0)
			{
				System.out.print(GlobalProperties.getIntlString("\rComputed_MI:_") + cnt);
			}
			cnt++;

			String masterkey = (String) enm.nextElement();
			String splitkey[] = masterkey.split("#");
			//System.out.println("\nmasterkey splitkey[]" + masterkey + " " + splitkey[0] + " " + splitkey[1]);
			//total_freq =+ (double) (( (Integer)(getEntry(key).get(0))).intValue());

			double freq = (double)((Integer)getEntry(masterkey).get(0)).intValue();

			double prob_a_b = (double)freq / (double)total_freq;
			double prob_b = 0, prob_a = 0;

			int present = wordpairtable_s.findWP(splitkey[0]);
			WordPair wp = (WordPair) wordpairtable_s.getWP(present);
			long freq_a = wp.getFreq();
			prob_a = (double) freq_a / (double)wordpairtable_s.getTotalFreq();

			present = wordpairtable_t.findWP(splitkey[1]);
			wp = (WordPair) wordpairtable_t.getWP(present);
			long freq_b = wp.getFreq();
			prob_b = (double) freq_b / (double)wordpairtable_t.getTotalFreq();

			double mi_a_b = Math.log( prob_a_b / (prob_a * prob_b) );
			addMI(masterkey, mi_a_b);
			//System.out.println("\nMIs.......prob_a_b prob_a prob_b" + mi_a_b + " " + prob_a_b + " " + prob_a + " " + prob_b);

			if(mi_a_b < min_mi)
			{
				min_mi = mi_a_b;
			}

			if(mi_a_b > max_mi)
			{
				max_mi = mi_a_b;
			}
		}

		normalizeMI();
	}

	public void normalizeMI()
	{
		Enumeration enm = getKeys();
		int cnt = 0;
		System.out.println("\nNormalizing bi-pair MI");
		while( enm.hasMoreElements() )
		{
			if(cnt%1000 == 0)
			{
				System.out.print("\rComputed: " + cnt);
			}
			cnt++;

			String masterkey = (String) enm.nextElement();

			double mi = ((Double)getEntry(masterkey).get(1)).doubleValue();
			mi = (mi - min_mi) / (max_mi - min_mi);
			//System.out.println("\nNormalize MIs.......min max " + mi + " " + min_mi + " " + max_mi);

			addMI(masterkey, mi);
		}
	}


	public int commonCorr(Sentence srcsen, Sentence tgtsen)//, WordPairTable wordpairtable_s, WordPairTable wordpairtable_t)
	{
		String key, key_t, master_key;
		int count, count_t;
		int contains1, contains2, contains1_t, contains2_t;
		int common = 0;
		double mi_a_b = 0.0;

		WordPair temp_wp = null;

		count = srcsen.countWords();
		count_t = tgtsen.countWords();

		for(int i = 0; i < count - 1; i++)
		{
			contains1 = srcsen.getWord(i);

			for(int i_thrh = i + 1; (i_thrh < i + threshold && i_thrh < count); i_thrh++)
			{
				contains2 = srcsen.getWord(i_thrh);

				int tmp = i_thrh - i;

				key = Integer.toString(contains1) + "::" + Integer.toString(contains2) + "::" + Integer.toString(tmp);

				for(int j = 0; j < count_t; j++)
				{
					contains1_t = tgtsen.getWord(j);

					for(int j_thrh = j + 1; (j_thrh < j + threshold && j_thrh < count_t - 1); j_thrh++)
					{
						contains2_t = tgtsen.getWord(j_thrh);

						int tmp2 = j_thrh - j;

						key_t = Integer.toString(contains1_t) + "::" + Integer.toString(contains2_t) + "::" + Integer.toString(tmp2);

						String masterkey = key + "#" + key_t;
						if(pairs.containsKey(masterkey) == true)
						{
						//	common++;
							/*double total_freq = getTotalFreq();

							double freq = (double)((Integer)getEntry(masterkey).get(0)).intValue();

							double prob_a_b = (double)freq / (double)total_freq;
							double prob_b = 0, prob_a = 0;

							int present = wordpairtable_s.findWP(key);
							WordPair wp = (WordPair) wordpairtable_s.getWP(present);
							long freq_a = wp.getFreq();
							prob_a = (double) freq_a / (double)wordpairtable_s.getTotalFreq();

							present = wordpairtable_t.findWP(key_t);
							wp = (WordPair) wordpairtable_t.getWP(present);
							long freq_b = wp.getFreq();
							prob_b = (double) freq_b / (double)wordpairtable_t.getTotalFreq();

							mi_a_b = Math.log( prob_a_b / (prob_a * prob_b) );
							mi_a_b = (mi_a_b - min_mi) / (max_mi - min_mi);*/
							mi_a_b = ((Double)(getEntry(masterkey).get(1))).doubleValue();

							if(mi_a_b >= 0.85)
							{
								//return mi_a_b;
								common++;
							}
						}
					}
				}
			}
		}
		return common;
		//return mi_a_b;
	}


	public void print(PrintStream ps, APCData apcdata)
	{
		Enumeration enm = this.getKeys();

		ps.println(GlobalProperties.getIntlString("SIZE:_") + pairs.size());
		while( enm.hasMoreElements() )
		{
			String key = (String)enm.nextElement();
			String [] splitstr = key.split("#");
			String [] splitstr2 = splitstr[0].split("::");
			WordType wrd = apcdata.getSrcWTTable().getWT(Integer.parseInt(splitstr2[0]));
			ps.print(wrd.getWord() + "::");
			wrd = apcdata.getSrcWTTable().getWT(Integer.parseInt(splitstr2[1]));
			ps.print(wrd.getWord() + "::");
			ps.print(splitstr2[2]);

			ps.print("#");

			splitstr2 = splitstr[1].split("::");
			wrd = apcdata.getTgtWTTable().getWT(Integer.parseInt(splitstr2[0]));
			ps.print(wrd.getWord() + "::");
			wrd = apcdata.getTgtWTTable().getWT(Integer.parseInt(splitstr2[1]));
			ps.print(wrd.getWord() + "::");
			ps.print(splitstr2[2]);

			int freq = ((Integer)getEntry(key).get(0)).intValue();
			double mi = ((Double)getEntry(key).get(1)).doubleValue();
			ps.println(GlobalProperties.getIntlString("___Key_") + key + GlobalProperties.getIntlString("_Freq_") + freq + GlobalProperties.getIntlString("_MI_") + mi);
		}
	}
}
