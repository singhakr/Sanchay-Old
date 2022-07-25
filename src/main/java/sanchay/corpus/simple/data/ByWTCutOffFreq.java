package sanchay.corpus.simple.data;

import java.io.*;
import java.util.*;

// compare() Inconsistent with equals()
public class ByWTCutOffFreq implements Comparator
{
	public int compare(Object one, Object two)
	{
		return ( (int) ((WordType) two).getCutOffFreq() - (int) ((WordType) one).getCutOffFreq() );
	}
}
