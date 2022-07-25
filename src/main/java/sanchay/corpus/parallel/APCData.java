package sanchay.corpus.parallel;

import java.io.*;
import java.util.*;

import sanchay.corpus.simple.data.*;
import sanchay.corpus.simple.data.impl.WordTypeTableImpl;

public class APCData
{
	protected WordTypeTable srctable; // WordTypeExs
	protected WordTypeTable tgttable; // WordTypes

	public APCData()
	{
		srctable = new WordTypeTableImpl();
		tgttable = new WordTypeTableImpl();
	}

	public APCData(int src_initial_capacity, int tgt_initial_capacity)
	{
		srctable = new WordTypeTableImpl(src_initial_capacity);
		tgttable = new WordTypeTableImpl(tgt_initial_capacity);
	}
	
	public WordTypeTable getSrcWTTable()
	{
		return srctable;
	}

	public void setSrcWTTable(WordTypeTable t)
	{
		srctable = t;
	}
	
	public WordTypeTable getTgtWTTable()
	{
		return tgttable;
	}

	public void setTgtWTTable(WordTypeTable t)
	{
		tgttable = t;
	}
	
	public int print(PrintStream ps)
	{
		WordTypeTable wordtypetable = null;
		
		wordtypetable = getSrcWTTable();
		wordtypetable.print(ps);
		
		wordtypetable = getTgtWTTable();
		wordtypetable.print(ps);
		
		return 0;
	}
}
