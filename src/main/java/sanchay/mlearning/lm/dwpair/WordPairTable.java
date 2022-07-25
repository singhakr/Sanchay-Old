package sanchay.mlearning.lm.dwpair;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import sanchay.GlobalProperties;
import sanchay.corpus.*;
import sanchay.corpus.parallel.APCData;
import sanchay.corpus.simple.data.*;
import sanchay.corpus.simple.data.impl.WordTypeExImpl;
import sanchay.corpus.simple.data.impl.WordTypeImpl;
import sanchay.corpus.simple.data.impl.WordTypeTableImpl;
import sanchay.corpus.simple.impl.SimpleCorpusImpl;
import sanchay.corpus.simple.impl.SimpleSentenceImpl;
import sanchay.mlearning.lm.ngram.*;

public class WordPairTable
{
	// index (source) -- "(source pair and distance) w1::w2::dist" as the key and vector index as value
	private Vector wordpairs;
	private Hashtable index;
	//private static APCData apcdata;
	private Corpus crp;
	//private static Corpus tgtcrp;
	private WordTypeTable wttbl;
	private String cutoff_file;

	public WordPairTable()
	{
		//alignedCorpus = ac;
		wordpairs = new Vector(1000, 300);
		index =  new Hashtable(1000, (float)0.3);
	}

	public WordPairTable(boolean isol)
	{
		wordpairs = new Vector(1000, 300);
		index =  new Hashtable(1000, (float)0.3);

		//apcdata = new APCData();
		crp = new SimpleCorpusImpl(1000);
		wttbl = new WordTypeTableImpl(1000);
	}

	public void setCutOffFile(String file)
	{
		cutoff_file = file;
	}

	public String getCutOffFile()
	{
		return cutoff_file;
	}

	public WordTypeTable getWtTbl()
	{
		return wttbl;
	}

	public void setWtTbl(WordTypeTable wtt)
	{
		wttbl = wtt;
	}

	public Corpus getCrp()
	{
		return crp;
	}

	public void setCrp(Corpus c)
	{
		crp = c;
	}

	/*public Corpus getTgtCrp()
	{
		return tgtcrp;
	}

	public void setTgtCrp(Corpus c)
	{
		tgtcrp = c;
	}*/

	public double getTotalFreq()
	{
		double total_freq = 0;
		Iterator itr = wordpairs.iterator();

		while( itr.hasNext() )
		{
			WordPair temp_wp = (WordPair)itr.next();
			total_freq +=  (double)temp_wp.getFreq();
		}

		return total_freq;
	}

	public int countWPs()
	{
		return wordpairs.size();
	}

	public WordPair getWP(int num)
	{
		return (WordPair) wordpairs.get(num);
	}

	public int addWP(WordPair wp)
	{
		int wi_1 = wp.getWord1();
		int wi_2 = wp.getWord2();
		int dist = wp.getDistance();
		String key = Integer.toString(wi_1) + "::" + Integer.toString(wi_2) + "::" + Integer.toString(dist);

		wordpairs.add(wp);

		int i = wordpairs.indexOf(wp);
		index.put(key,new Integer(i));

		return wordpairs.size();
	}

	public void resetHash()
	{
		for(int i = 0; i < countWPs(); i++)
		{
			WordPair wp = getWP(i);

			int wi_1 = wp.getWord1();
			int wi_2 = wp.getWord2();
			int dist = wp.getDistance();
			String key = Integer.toString(wi_1) + "::" + Integer.toString(wi_2) + "::" + Integer.toString(dist);

			int in = wordpairs.indexOf(wp);
			index.put(key,new Integer(in));
		}
	}

	public int findWP(String str)
	{
		Integer i = (Integer) index.get(str);

		if(i != null)
			return i.intValue();

		return -1;
	}

	public boolean containsWP(String str)
	{
		return wordpairs.contains(str);
	}

	public WordPair removeWP(int num)
	{
		return (WordPair) wordpairs.remove(num);
	}

	public void calMI_applyCutOff(APCData apcdata, String lang) throws FileNotFoundException, IOException
	{
		int total = 0;
		int cnt = 0;
		int cnt2 = 0;

        BufferedReader lnReader = new BufferedReader(
            	new InputStreamReader(new FileInputStream(getCutOffFile()), GlobalProperties.getIntlString("UTF-8")));

		Hashtable cutoffhash = new Hashtable(10000, (float)0.5);
		String line = null;

		while( (line = lnReader.readLine()) != null )
		{
			String [] splitstr = line.split(" ");
			if(Double.parseDouble(splitstr[2]) >= 0.85)
			{
				cutoffhash.put(splitstr[0], splitstr[2]);
				//System.out.println(line);
			}
		}

		System.out.println(GlobalProperties.getIntlString("\nWordPairTable_Size_=_") + countWPs() + GlobalProperties.getIntlString("_Org_CutOff_size:_") + cutoffhash.size());

		//for(int i = 0; i < getWPCount(); i++)
		//{
			for(int j = 0; j < countWPs(); j++)
			{
				if(cnt%1000 == 0)
				{
					System.out.print(GlobalProperties.getIntlString("\rCalculated_cnt:_") + cnt);
				}
				cnt++;

				WordPair wp = getWP(j);
				double mi = wp.getMI();
				String key = null, key_hash = null;;
				if(lang == "e")
				{
					key = apcdata.getSrcWTTable().getWT(wp.getWord1()).getWord() + "::" + apcdata.getSrcWTTable().getWT(wp.getWord2()).getWord() + "::" + Integer.toString(wp.getDistance());
					key_hash = Integer.toString(wp.getWord1()) + "::" + Integer.toString(wp.getWord2()) + "::" + Integer.toString(wp.getDistance());
				}
				else
				{
					key = apcdata.getTgtWTTable().getWT(wp.getWord1()).getWord() + "::" + apcdata.getTgtWTTable().getWT(wp.getWord2()).getWord() + "::" + Integer.toString(wp.getDistance());
					key_hash = Integer.toString(wp.getWord1()) + "::" + Integer.toString(wp.getWord2()) + "::" + Integer.toString(wp.getDistance());
				}

				if(cutoffhash.containsKey(key) == true)
				{
					//if( Double.parseDouble( ((String)cutoffhash.get(key)) ) >= 0.9 )
					//{
					//System.out.println("key accepted " + key  + " " + cnt2);
					wp.setMI(Float.parseFloat( ((String)cutoffhash.get(key)) ));
					cnt2++;
				}
				else
				{
					//System.out.println("Removing key cnt.. " + key + " " + cnt);
					removeWP(j);
					//i = i - 1;
					j = j - 1;
					index.remove(key_hash);
					cnt++;
				}
				//}
				//else
				//	total++;
			}
		//}
		resetHash();
		//System.out.println(".............. " + total);
		System.out.println(GlobalProperties.getIntlString("\n_Total_WordPair_and_hash_Count_=_") + countWPs() + " " + index.size());
	}

	public void readCorpus(String crpfile, String lang) throws FileNotFoundException, IOException
	{
        BufferedReader lnReader = new BufferedReader(
            	new InputStreamReader(new FileInputStream(crpfile), GlobalProperties.getIntlString("UTF-8")));

		String line;
		String [] splitstr;
		//wttbl = new WordTypeTable();
		//crp = new Corpus();
		WordTypeExImpl wpx = null;
		WordType wp = null;
		int sen_num = 0;
		Pattern p = Pattern.compile("[\\s(]|[(]|[_]|[-]");
		//Pattern p = Pattern.compile("[\\p{Blank}]+|[\\p{Punct}]+");

		while( (line = lnReader.readLine()) != null )          //Source Corpus Parsing
		{
			sen_num++;
			//if(sen_num%500 == 0)
			//{
			//	sen_num = 0;
				System.out.print(GlobalProperties.getIntlString("\rReading_Corpus...") + sen_num);
			//}

			//splitstr = line.split(" ");
			splitstr = p.split(line);          //Regular Expression for spaces between the words##
			//count = splitstr.length;

			int count = splitstr.length;
			Sentence sentence = new SimpleSentenceImpl(count);

			for(int i = 0; i < count; i++)
			{
				int contains_src = -1;

				//if(lang == "e")
					contains_src = getWtTbl().findWT(splitstr[i]);
				//else
				//	contains_src = apcdata.getTgtWTTable().findWT(splitstr[i]);

				if(contains_src == -1)
				{
					int index_src = -1;

					if(lang == "e")
					{
						wpx = new WordTypeExImpl();
						wpx.setWord(splitstr[i]);
						wpx.setFreq(1);
						wpx.setIsCorpusWord(true);
						index_src = getWtTbl().addWT(wpx) - 1;
					}
					else
					{
						wp = new WordTypeImpl();
						wp.setWord(splitstr[i]);
						wp.setFreq(1);
						wp.setIsCorpusWord(true);
						index_src = getWtTbl().addWT(wp) - 1;
					}

					sentence.setWord(i, index_src);
				}
				else
				{
					if(lang == "e")
					{
						wpx = (WordTypeExImpl) getWtTbl().getWT(contains_src);
						wpx.setFreq(wpx.getFreq()+1);
					}
					else
					{
						wp = getWtTbl().getWT(contains_src);
						wp.setFreq(wp.getFreq()+1);
					}
					sentence.setWord(i, contains_src);
				}

			}

			//if(lang == "e")
				crp.addSentence(sentence);
			//else
			//	tgtcrp.addSentence(sentence);

			int threshold = 3;	//window size

			//WordTypeTable wttbl = null;

			//if(lang == "e")
			//	wttbl = apcdata.getSrcWTTable();
			//else
			//	wttbl = apcdata.getTgtWTTable();

			for(int i = 0; i < count ; i++)
			{
				int contains1 = wttbl.findWT(splitstr[i]);
				if(contains1 == -1)
					System.out.println("---------"  + splitstr[i]);

				for(int i_thrh = i + 1; (i_thrh < i + threshold && i_thrh < count); i_thrh++)
				{
					int contains2 = wttbl.findWT(splitstr[i_thrh]);
					if(contains2 == -1)
						System.out.println("==========="  + splitstr[i_thrh]);

					int tm = i_thrh - i;

					String key = Integer.toString(contains1) + "::" + Integer.toString(contains2) + "::" + Integer.toString(tm);
					int present = findWP(key);


					if(present != -1)
					{
						WordPair temp_wp = getWP(present);
						long temp = temp_wp.getFreq();
						temp_wp.setFreq(temp+1);
					}
					else
					{
						WordPair wordpair = new WordPair();
						wordpair.setWord1(contains1);
						wordpair.setWord2(contains2);
						wordpair.setDistance(i_thrh - i);
						wordpair.setFreq(1);

						addWP(wordpair);
					}
				}
			}
		}
	}

	public float sentenceProb(Corpus corpus, int index, NGramLM ngramlm)
	{
		Sentence sen = corpus.getSentence(index);
		int n = sen.countWords();
		int i, wrd_ind;
		float total_prob = 0;
		String wrd = null;
		int ws = 6;	//window size
		String key = null;
		double total_freq = getTotalFreq();
		int max;

		for( i = 0; i < n; i++ )
		{
			wrd_ind = sen.getWord(i);
			WordType wt = wttbl.getWT(wrd_ind);
			wrd = wt.getWord();
			double prob = ((NGram) ngramlm.getNGram(wrd, 1)).getProb();
			total_prob += prob;
		}

		for( i = n; i <= 2; i--)
		{
			if( (i - ws) < 1 )
				max = 1;
			else
				max = i - ws;

			for(int j = i-1; j <= max; j--)
			{
				wrd_ind = sen.getWord(i);
				int wrd_ind_prev = sen.getWord(j);

				int dist = i - j + 1;
				key = Integer.toString(wrd_ind_prev) + "::" + Integer.toString(wrd_ind) + "::" + Integer.toString(dist);

				int present = findWP(key);
				if( present != -1 )
				{
					WordPair temp_wp = getWP(present);
					long freq = temp_wp.getFreq();

					double prob_a_b = (double)freq / (double)total_freq;

					WordType wt = wttbl.getWT(wrd_ind_prev);
					wrd = wt.getWord();
					double prob_a = ((NGram) ngramlm.getNGram(wrd, 1)).getProb();

					wt = wttbl.getWT(wrd_ind);
					wrd = wt.getWord();
					double prob_b = ((NGram) ngramlm.getNGram(wrd, 1)).getProb();

					double mi_a_b = Math.log( prob_a_b / (prob_a * prob_b) );

					total_prob += mi_a_b;
				}
			}
		}

		return total_prob;
	}

	public void calculateMI(String lang)
	{
		int i, wrd_ind;
		double total_freq = getTotalFreq();
		WordTypeEx wtx = null;
		WordType wt = null;
		int count = 0;

		System.out.println("WordPair Table Size = " + countWPs());

		for(i = 0; i < countWPs(); i++)
		{
			count++;
			//if(count%10000 == 0)
			//{
				System.out.print("\rCalculating MI... " + count);
			//	count = 0;
			//}
			WordPair wp = (WordPair) getWP(i);

			double mi_a_b = 0;
			wp.setMI(mi_a_b);


			/*long freq = wp.getFreq();

			double prob_a_b = (double)freq / (double)total_freq;
			double prob_b = 0, prob_a = 0;
			//System.out.println((double)freq + " " + (double)total_freq);

			if(lang == "e")
			{
				wtx = (WordTypeEx) getWtTbl().getWT(wp.getWord1());
				long wrd_freq = wtx.getFreq();
				prob_a = (double) wrd_freq / (double) getWtTbl().getWTCount(WordType.CORPUS_WORD);

				wtx = (WordTypeEx) getWtTbl().getWT(wp.getWord2());
				wrd_freq = wtx.getFreq();
				prob_b = (double) wrd_freq / (double) getWtTbl().getWTCount(WordType.CORPUS_WORD);
			}
			else
			{
				wt = getWtTbl().getWT(wp.getWord1());
				long wrd_freq = wt.getFreq();
				prob_a = (double) wrd_freq / (double) getWtTbl().getWTCount(WordType.CORPUS_WORD);

				wt = getWtTbl().getWT(wp.getWord2());
				wrd_freq = wt.getFreq();
				prob_b = (double) wrd_freq / (double) getWtTbl().getWTCount(WordType.CORPUS_WORD);
			}

			double mi_a_b = Math.log( prob_a_b / (prob_a * prob_b) );
			//System.out.println(prob_a + " " + prob_b + " " + prob_a_b + " " + mi_a_b);
			wp.setMI(mi_a_b);*/
		}
	}

	public int print(PrintStream ps)
	{
		WordPair wordpair = null;
		int count = 0;
		int num = 0, total = 0;

		count = countWPs();

		for(int i = 0; i < count; i++)
		{
			//ps.println(i);
			wordpair = getWP(i);
			num = wordpair.print(ps, wttbl);
			if( num == 1)
				total++;
			//ps.print("\n\n\n");
		}
		//ps.println("Total: " + total);
		return countWPs();
	}

	/*public static void main(String args[])
	{
		System.out.println("...............");

		WordPairTable src_wpt = new WordPairTable(true);
		WordPairTable tgt_wpt = new WordPairTable(true);

		//apcdata = new APCData();
		//srccrp = new Corpus();
		//tgtcrp = new Corpus();

		String file_src = args[0];
		String file_tgt = args[1];

		try
		{
			System.out.println("Reading the SRC corpus....");
			src_wpt.readCorpus(file_src, "e");
			System.out.println("Reading the TGT corpus....");
			tgt_wpt.readCorpus(file_tgt, "h");
		}
		catch(IOException e)
		{
			System.out.println("Input file not found");
			return;
		}

		System.out.println("Calculating the SRC MI...");
		src_wpt.calculateMI("e");
		System.out.println("Calculating the TGT MI...");
		tgt_wpt.calculateMI("h");

		System.out.println("Printing....");
		try
		{
			PrintStream ps = new PrintStream("src.mi");
			src_wpt.print(ps, "e");

			ps = new PrintStream("tgt.mi");
			tgt_wpt.print(ps, "h");
		}
		catch(IOException e)
		{
			System.out.println("Input file not found");
			return;
		}
	}*/
}