package sanchay.corpus.simple.data.impl;

import java.io.*;
import java.util.*;
import sanchay.data.attrib.impl.DefaultTypeTable;
import sanchay.corpus.simple.data.*;

public class WordTypeTableImpl extends DefaultTypeTable implements WordTypeTable
{
	protected Vector wordtypes; // WordTypes for target and WordTypeExs for source
	protected Hashtable word_index;

	public WordTypeTableImpl()
	{
		wordtypes = new Vector();
		word_index = new Hashtable();
	}

	public WordTypeTableImpl(int initial_capacity)
	{
		wordtypes = new Vector(initial_capacity, initial_capacity/2);
		word_index = new Hashtable(initial_capacity, (float)0.40);
	}

	// Change wtflags to BitSet
	public int countWTs(short wtflags /* one or more WordType flags - ANY_WORD will override others */)
	{
		if(wtflags == WordType.ANY_WORD)
			return wordtypes.size();

		int ret = 0;
		WordType wt = null;

		for(int i = 0;  i < wordtypes.size(); i++)
		{
			wt = (WordType) wordtypes.get(i);

			if(wtflags == WordType.CORPUS_WORD)
			{
				if(wt.getIsCorpusWord())
					ret++;
			}
			else if(wtflags == WordType.SUBSTITUTE_WORD)
			{
				if(wt.getIsSubstituteWord())
					ret++;
			}
			else
			{
				if(wtflags == WordType.HYPERNYM_WORD && wt.getIsHypernymWord())
					ret++;
			}
		}

		return ret;
	}

	// Change wtflags to BitSet
	public long countTokens(short wtflags /* one or more WordType flags - ANY_WORD will override others */)
	{
		long ret = 0;

		WordType wt = null;

		for(int i = 0;  i < wordtypes.size(); i++)
		{
			wt = (WordType) wordtypes.get(i);

			if(wtflags == WordType.ANY_WORD)
				ret += wt.getFreq();
			else if(wtflags == WordType.CORPUS_WORD)
			{
				if(wt.getIsCorpusWord())
					ret += wt.getFreq();
			}
			else if(wtflags == WordType.CUT_OFF_WORD)
			{
				if(wt.getIsCorpusWord())
					ret += wt.getCutOffFreq();
			}
			else if(wtflags == WordType.SUBSTITUTE_WORD)
			{
				if(wt.getIsSubstituteWord())
					ret += wt.getFreq();
			}
			else
			{
				if(wtflags == WordType.HYPERNYM_WORD && wt.getIsHypernymWord())
					ret += wt.getFreq();
			}
		}

		return ret;
	}

	public WordType getWT(int num)
	{
		return (WordType) wordtypes.get(num);
	}

	public WordType getWT(String swrd)
	{
		WordType x = null;
		int i = findWT(swrd);
		if( i != -1)
			return getWT(i);
		else
			return x;
	}

	public int addWT(WordType wt)
	{
  		String key = wt.getWord();

		wordtypes.add(wt);

		int i = wordtypes.indexOf(wt);
		word_index.put(key, new Integer(i));

		return wordtypes.size();
	}

	public int findWT(String str)
	{
		Integer i;

		if(str == null)
		{
			return -1;
		}

		if( word_index.containsKey(str) == true)
		{
			i = (Integer)word_index.get(str);
			return i.intValue();
		}
		else
			return -1;


		/*for(i = 0; i < getWTCount(); i++)
		{
			if(getWT(i).getWord().compareTo(str) == 0)
				return wordtypes.indexOf(getWT(i));
		}*/


	}

	public boolean containsWT(String str)
	{
		return wordtypes.contains(str);
	}

	public WordType removeWT(int num)
	{
		return (WordType) wordtypes.remove(num);
	}

	public WordType removeWT(String swrd)
	{
		int i = findWT(swrd);
		return (WordType) wordtypes.remove(i);
	}

	public Vector sort(int order)
	{
		Vector sorted = new Vector(wordtypes);

		switch(order)
		{
			case WordType.SORT_BY_SRC_WRD:
				Collections.sort(sorted, new ByWTSrcWrd());
				break;
			case WordType.SORT_BY_TGT_MNG:
				Collections.sort(sorted, new ByWTTgtMng());
				break;
			case WordType.SORT_BY_TAG:
				Collections.sort(sorted, new ByWTTag());
				break;
			case WordType.SORT_BY_EQWRD:
				Collections.sort(sorted, new ByWTEqWrd());
				break;
			case WordType.SORT_BY_FREQ:
				Collections.sort(sorted, new ByWTFreq());
				break;
			case WordType.SORT_BY_REVERSE_FREQ:
				Collections.sort(sorted, new ByWTReverseFreq());
				break;
			case WordType.SORT_BY_SIG:
				Collections.sort(sorted, new ByWTSig());
				break;
			case WordType.SORT_BY_CUT_OFF_FREQ:
				Collections.sort(sorted, new ByWTCutOffFreq());
				break;
			default:
				Collections.sort(sorted, new ByWTSrcWrd());
		}

		//tree_set.addAll(wordtypes); // sorted
		/*Iterator itr_token_1_cnt = sorted.iterator();
		WordType token_wt = null;

		while(itr_token_1_cnt.hasNext())
		{
			token_wt = (WordType) itr_token_1_cnt.next();
			System.out.println(">>>>>>>>>>>>>>> " + (String)token_wt.getWord() + " " + token_wt.getFreq());
		}*/

		//System.out.println("$$$$$$$$$ " + wordtypes.size() + " $$$$$$$$$ " + tree_set.size());
		return sorted;
	}

	public WordTypeTable getShallowCopy()
	{
		WordTypeTableImpl retwtbl = new WordTypeTableImpl();

		Vector rwordtypes = (Vector) wordtypes.clone();
		retwtbl.wordtypes = rwordtypes;

		Hashtable rword_index = (Hashtable) word_index.clone();
		retwtbl.word_index = rword_index;

		return retwtbl;
	}

	public int print(PrintStream ps)
	{
		WordType wordtype = null;
		int count = 0;

		count = countWTs(WordType.ANY_WORD);
		for(int i = 0; i < count; i++)
		{
			ps.println(i);
			wordtype = getWT(i);
			wordtype.print(ps);
			ps.print("\n\n\n");
		}

		return countWTs(WordType.ANY_WORD);
	}
}
