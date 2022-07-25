package sanchay.speech.common;

import java.util.*;
import java.io.*;
import sanchay.GlobalProperties;

public class TrellisString {
	private Vector<StringNode> stringNodes;

	public TrellisString()
	{
		stringNodes = new Vector<StringNode>();
	}

	public int countNodes()
	{
		return stringNodes.size();
	}

	public StringNode getNode(int num)
	{
		return stringNodes.get(num);
	}

	public int addNode(StringNode n)
	{
		stringNodes.add(n);
		return stringNodes.size();
	}

	public StringNode removeNode(int num)
	{
		return stringNodes.remove(num);
	}

	/*public int read(String filepath, int fmt) throws FileNotFoundException, IOException
	{
		// ignoring format for the time being, assuming lpcc

        BufferedReader lnReader = new BufferedReader(
        	new InputStreamReader(new FileInputStream(filepath), "UTF-8"));

		String line;
		String splitstr[];
		int format = -1, vecsize = 0, vecnum = 0;
		Feature feature = null;
		StringNode stringNode = null;

		if((line = lnReader.readLine()) != null)
			format = Integer.parseInt(line);

		if((line = lnReader.readLine()) != null)
		{
			splitstr = line.split("[\\t ]");
			vecnum = Integer.parseInt(splitstr[0]);
			vecsize = Integer.parseInt(splitstr[1]);
		}

		stringNodes.setSize(vecnum);

		int i = 0;
		while((line = lnReader.readLine()) != null)
		{
			splitstr = line.split("[\\t ]");
			if(splitstr.length == vecsize)
			{
				char features[] = new char[vecsize];
				for(int j = 0; j < vecsize; j++)
				{
					features[j] = Double.parseDouble(splitstr[j]);
				}

				feature = new Feature();
				feature.setFeatures(features);
				stringNode = new StringNode(feature);
				stringNode.setIndex(i);
				System.out.println(stringNode.getIndex());

				stringNodes.set(i++, stringNode);
			}
		}

		return vecnum;
	}*/

	public void clear()
	{
		stringNodes.clear();
	}

	public void write(PrintStream p)
	{
		if(p == null) p = System.out;

		p.println(GlobalProperties.getIntlString("String_size:_") + stringNodes.size() + GlobalProperties.getIntlString(",_Vector_size:_") + RecogProps.VECSIZE);

		for(int i = 0; i < countNodes(); i++)
			p.println(((StringNode) getNode(i)).getFeature());
	}

	public void normalize()
	{
		for(int i = 0; i < countNodes(); i++)
		{
			getNode(i).getFeature().normalize();
		}
	}
}
