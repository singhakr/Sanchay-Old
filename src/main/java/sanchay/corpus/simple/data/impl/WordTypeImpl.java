package sanchay.corpus.simple.data.impl;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.simple.data.WordType;

public class WordTypeImpl implements WordType
{
	protected String word;
	protected String tag;
	//protected String meaning;
	protected int eqword; // equivalent word index
	protected Vector sbt_words_all; //indices of substituted words for each src word
	protected int wordsig; // signature of the word
	protected long freq;
	protected long cutoffreq;
	protected BitSet wtflags; // One or more WordType flags (bitmap)

	public WordTypeImpl()
	{
		tag = null;
		//meaning = null;
		eqword = -1;
		sbt_words_all = new Vector();
		wordsig = 0;
		freq = 0;
		cutoffreq = 0;
		wtflags = new BitSet(5);
	}

	public String getWord()
	{
		return word;
	}

	public void setWord(String w)
	{
		word = w;
		try
		{
			int i = Integer.parseInt(word);
			setIsNumeric(true);
		}
		catch(NumberFormatException nfb)
		{
		}
	}

	public String getTag()
	{
		return tag;
	}

	public void setTag(String t)
	{
		tag = t;
	}

	/*public String getMeaning()
	{
		return meaning;
	}

	public void setMeaning(String w)
	{
		meaning = w;
	}*/

	public int getEqWord()
	{
		return eqword;
	}

	public void setEqWord(int w)
	{
		eqword = w;
	}

	public int countAllSbtWords()
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
	}

	public int getWordSig()
	{
		return wordsig;
	}

	public void setWordSig(int s)
	{
		wordsig = s;
	}

	public long getFreq()
	{
		return freq;
	}

	public void setFreq(long f)
	{
		freq = f;
	}

	public long getCutOffFreq()
	{
		return cutoffreq;
	}

	public void setCutOffFreq(long f)
	{
		cutoffreq = f;
	}

	public boolean getIsCorpusWord()
	{
		if(wtflags.get(CORPUS_WORD) == true)
			return true;

		return false;
	}

	public void setIsCorpusWord(boolean flag)
	{
		if(flag)
			wtflags.set(CORPUS_WORD);
		//else
		//	wtflags = ;
	}

	public boolean getIsSubstituteWord()
	{
		if(wtflags.get(SUBSTITUTE_WORD) == true)
			return true;

		return false;
	}

	public void setIsSubstituteWord(boolean flag)
	{
		if(flag)
			wtflags.set(SUBSTITUTE_WORD);
		//else
		//	wtflags &= ~SUBSTITUTE_WORD;
	}

	public boolean getIsHypernymWord()
	{
		if(wtflags.get(HYPERNYM_WORD) == true)
			return true;

		return false;
	}

	public void setIsHypernymWord(boolean flag)
	{
		if(flag)
			wtflags.set(HYPERNYM_WORD);
		//else
		//	wtflags &= ~HYPERNYM_WORD;
	}

	public boolean getIsNumeric()
	{
		if(wtflags.get(NUMERIC) == true)
			return true;

		return false;
	}

	public void setIsNumeric(boolean flag)
	{
		if(flag)
			wtflags.set(NUMERIC);
		//else
		//	wtflags &= ~HYPERNYM_WORD;
	}

	public boolean getIsPhonetic()
	{
		if(wtflags.get(PHONETIC) == true)
			return true;

		return false;
	}

	public void setIsPhonetic(boolean flag)
	{
		if(flag)
			wtflags.set(PHONETIC);
		//else
		//	wtflags &= ~HYPERNYM_WORD;
	}

	public int print(PrintStream ps)
	{
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

		return 0;
	}
}
