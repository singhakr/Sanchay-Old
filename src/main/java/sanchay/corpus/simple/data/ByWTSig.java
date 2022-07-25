package sanchay.corpus.simple.data;

import java.io.*;
import java.util.*;

// compare() Inconsistent with equals()
public class ByWTSig implements Comparator
{
	public int compare(Object one, Object two)
	{
		return ( ((WordType) one).getWordSig() - ((WordType) two).getWordSig());
	}
}
