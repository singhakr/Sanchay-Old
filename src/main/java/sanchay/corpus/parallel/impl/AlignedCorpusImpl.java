package sanchay.corpus.parallel.impl;

import java.io.*;
import java.util.*;

import sanchay.corpus.*;
import sanchay.corpus.parallel.APCData;
import sanchay.corpus.parallel.AlignedCorpus;
import sanchay.corpus.simple.data.*;

public class AlignedCorpusImpl implements AlignedCorpus
{
	private Hashtable alignments; // source index as key and target index as value

	public AlignedCorpusImpl()
	{
		alignments = new Hashtable();
	}

	public AlignedCorpusImpl(int initialize)
	{
		alignments = new Hashtable(initialize, (float)0.25);
	}

	public Hashtable getAlignments()
	{
		return (Hashtable) alignments;
	}
	
	public int getAlignment(int key)
	{
		return ((Integer) alignments.get(Integer.valueOf(key))).intValue();
	}
	
	public void addAlignment(int key, int value)
	{
		alignments.put(new Integer(key), new Integer(value));
	}
	
	public void removeAlignments(int key)
	{
		alignments.remove(Integer.valueOf(key));
	}

	public void read(String file /* alignment file */) throws FileNotFoundException, IOException
	{
	}

	public void print(Corpus srccorpus, Corpus tgtcorpus, APCData apcdata, PrintStream senps /* aligned setences */, PrintStream inps /* Mapping File */)
	throws FileNotFoundException, IOException
	{
		Sentence sentence;
		int key, value, j;
		WordType wrd;
		Set key_set = alignments.keySet();
		TreeSet tree_set = new TreeSet(key_set);
		Iterator itr = tree_set.iterator();
		String token_1;

		while( itr.hasNext() )
		{
			key = ((Integer) itr.next()).intValue();
			value = getAlignment(key);
			inps.println(key + " " + value);	//Mapping file(contains index pairs)

			/*senps.println("\n<Translation>");
			sentence = srccorpus.getSentence(key);

			senps.println();
			senps.print("<S_Sentence id = " + key + "> ");

			for(j = 0; j < sentence.getWordCount(); j++)
			{
				wrd = apcdata.getSrcWTTable().getWT(sentence.getWord(j));
				senps.print(wrd.getWord() + " ");
			}


			sentence = tgtcorpus.getSentence(value);

			senps.print("\n<T_Sentence id = " + value + "> ");
			for(j = 0; j < sentence.getWordCount(); j++)
			{
				wrd = apcdata.getTgtWTTable().getWT(sentence.getWord(j));
				senps.print(wrd.getWord() + " ");
			}

			senps.println("\n\n</Result = y>");
			senps.println("</Translation>");*/
		}
	}
}
