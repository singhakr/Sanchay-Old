package sanchay.corpus.simple.data;

import java.io.*;
import java.util.*;

// compare() Inconsistent with equals()
public class ByWTTgtMng implements Comparator
{
	public int compare(Object one, Object two)
	{
		return -1; //( ((WordType) one).getMeaning().compareTo( ((WordType) two).getMeaning() ) );
	}
}
