package sanchay.corpus.simple.data;

import java.io.*;
import java.util.*;

// compare() Inconsistent with equals()
public class ByWTFreq implements Comparator
{
	public int compare(Object one, Object two)
	{
		return ( (int) ((WordType) one).getFreq() - (int) ((WordType) two).getFreq() );
	}
}
