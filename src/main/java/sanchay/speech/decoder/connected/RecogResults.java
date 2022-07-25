package sanchay.speech.decoder.connected;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;

public class RecogResults {
	private Vector modelPaths; // paths (ConTrellisPath) -- one for each model
	ConTrellisPath endPath; // best ending nodes

	public RecogResults()
	{
		modelPaths = new Vector();
		endPath = null;
	}

	public int countModelPaths()
	{
		return modelPaths.size();
	}

	public ConTrellisPath getModelPath(int num) 
	{
		return (ConTrellisPath) modelPaths.get(num);
	}

	public int addModelPath(ConTrellisPath p) 
	{
		modelPaths.add(p);
		return modelPaths.size();
	}

	public ConTrellisPath removeModelPath(int num) 
	{
		return (ConTrellisPath) modelPaths.remove(num);
	}
	public ConTrellisPath getEndPath()
	{
		return endPath;
	}

	public void setEndPath(ConTrellisPath p)
	{
		endPath = p;
	}
	
	public void write(PrintStream p) 
	{
		int i = 0;

		if(p == null) p = System.out;

		for(i = 0; i < modelPaths.size(); i++)
		{
			p.println(GlobalProperties.getIntlString("Path-") + i + ":");
			((ConTrellisPath) modelPaths.get(i)).write(p);
			p.println(GlobalProperties.getIntlString("End_Path:"));
			endPath.write(p);
		}
	}
}
