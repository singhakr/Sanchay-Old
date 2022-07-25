package sanchay.text.spell;

import java.io.*;
import java.util.*;

public class PhonemeVertex {

	String nodeLabel;

public PhonemeVertex(String label)
{
	nodeLabel = label;
}
public String getnodeLabel()
{
	return nodeLabel;
}
public static Vector makeCpy(Vector orgVec)
{
	Vector dupVec = new Vector();

	for(int i = 0;i < orgVec.size();i++)
	{
		PhonemeVertex pv = new PhonemeVertex(((PhonemeVertex)orgVec.elementAt(i)).getnodeLabel());
		dupVec.add((Object)pv);
	}

	return dupVec;
}
}