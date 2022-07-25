package sanchay.speech.decoder.isolated;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.speech.common.*;

public class IsolatedRecog {
	private TrellisString data;
	private Vector models; // TrellisString
	private Vector paths; // IsoTrellisPath, one path for each model

	public IsolatedRecog()
	{
		data = new TrellisString();
		models = new Vector();
		paths = new Vector();
	}

	public TrellisString getData()
	{
		return data;
	}

	public void setData(TrellisString d)
	{
		data = d;
	}

	public int countModels()
	{
		return models.size();
	}

	public TrellisString getModel(int num)
	{
		return (TrellisString) models.get(num);
	}

	public int addModel(TrellisString m)
	{
		models.add(m);
		return models.size();
	}

	public TrellisString removeModel(int num)
	{
		return (TrellisString) models.remove(num);
	}

	public int countPaths()
	{
		return paths.size();
	}

	public IsoTrellisPath getPath(int num)
	{
		return (IsoTrellisPath) paths.get(num);
	}

	public int addPath(IsoTrellisPath p)
	{
		paths.add(p);
		return paths.size();
	}

	public IsoTrellisPath removePath(int num)
	{
		return (IsoTrellisPath) paths.remove(num);
	}

	/*public int readData(String filepath, int fmt) throws FileNotFoundException, IOException
	{
		data.clear();
		return data.read(filepath, fmt);
	}*/

	// The list file will have one file name on each line
	// The first line will have the number of files
	/*public int readModels(String listfilepath, int fmt) throws FileNotFoundException, IOException
	{
        BufferedReader lnReader = new BufferedReader(
        	new InputStreamReader(new FileInputStream(listfilepath), "UTF-8"));

		String line;
		TrellisString trellisString = null;

		int i = 0;
		while((line = lnReader.readLine()) != null)
		{
			trellisString = new TrellisString();
			trellisString.read(line, fmt);
			addModel(trellisString);
			i++;
		}

		return i;
	}*/

	public void writeModels(PrintStream p)
	{
		for(int i = 0; i < models.size(); i++)
		{
			p.println(GlobalProperties.getIntlString("Model-") + i + ":");
			((TrellisString) models.get(i)).write(p);
		}
	}

	// Will align the data string with one model string
	public IsoTrellisPath alignString(TrellisString dat, TrellisString mdl)
	{
		if(dat == null || mdl == null)
			return null;

		int colnum = 0, i, j;
		double tempCost = RecogProps.INF_COST, minCost = RecogProps.INF_COST;

		IsoTrellisColumn prevCol = new IsoTrellisColumn();
		IsoTrellisColumn thisCol = new IsoTrellisColumn();
		IsoTrellisColumn tempCol = null;
		TrellisNode bestNode = null;
		IsoTrellisPath alignment = new IsoTrellisPath();
//		alignment->size = data->size;

		thisCol.setStringNode(dat.getNode(0));
		thisCol.fillColumn(mdl.countNodes());
		for(j = 0; j < mdl.countNodes(); j++)
		{
			thisCol.getNode(j).setStringNode(mdl.getNode(j));
			thisCol.getNode(j).setPathCost(RecogProps.INF_COST);
		}

		alignment.fillData(data.countNodes());
//		alignment->model = new stringNode*[data->size];

		for(i = 0; i < data.countNodes(); i++)
		{
			for(j = 0; j < (i+1)*2 && j < mdl.countNodes(); j++)
			{
				// For diagonal arc
				minCost = RecogProps.INF_COST;
				tempCost = RecogProps.INF_COST;
				if(j == 0 && i == 0)
					tempCost = thisCol.getStringNode().matchScore(thisCol.getNode(j).getStringNode());
				else if(j > 0 && i > 0)
				{
					tempCost = prevCol.getNode(j-1).getPathCost()
						+ thisCol.getStringNode().matchScore(thisCol.getNode(j).getStringNode());
				}
				if(tempCost < minCost)
					minCost = tempCost;

				// For double diagonal arc
				tempCost = RecogProps.INF_COST;
				if(j == 1 && i == 0)
					tempCost = thisCol.getStringNode().matchScore(thisCol.getNode(j).getStringNode());
				else if(j > 1 && i > 0)
				{
					tempCost = prevCol.getNode(j-2).getPathCost()
						+ thisCol.getStringNode().matchScore(thisCol.getNode(j).getStringNode()) + 1;
				}
				if(tempCost < minCost)
					minCost = tempCost;

				// For horizontal arc
				tempCost = RecogProps.INF_COST;
				if(i > 0)
				{
					tempCost = prevCol.getNode(j).getPathCost()
						+ thisCol.getStringNode().matchScore(thisCol.getNode(j).getStringNode()) + 1;
				}
				if(tempCost < minCost)
					minCost = tempCost;

				if(minCost < thisCol.getNode(j).getPathCost())
					thisCol.getNode(j).setPathCost(minCost);
			}

//			thisCol.write(System.out);

			bestNode = thisCol.getBestScore();

			alignment.getDataNode(i).setStringNode(thisCol.getStringNode());
			alignment.getDataNode(i).setPathCost(bestNode.getPathCost());
			alignment.addModelNode(bestNode.getStringNode());

//			if(prevCol) { delete prevCol; }
//			delete bestNode;

			if(i >= 0 && i < data.countNodes() - 1)
			{
				prevCol = thisCol;

				thisCol = new IsoTrellisColumn();
				thisCol.fillColumn(mdl.countNodes());

				thisCol.setStringNode(dat.getNode(i+1));
				for(j = 0; j < mdl.countNodes(); j++)
				{
					thisCol.getNode(j).setStringNode(mdl.getNode(j));
					thisCol.getNode(j).setPathCost(RecogProps.INF_COST);
				}
			}
		}

//		if(thisCol) { delete thisCol; };
		alignment.setCost(alignment.getDataNode(i-1).getPathCost());

//		alignment.write(System.out);

		return alignment;
	}

	// Will align the data with all the models and store the paths
	public int alignAll()
	{
		paths.clear();
		paths.setSize(models.size());

		for(int i = 0; i < models.size(); i++)
		{
			IsoTrellisPath p = alignString(data, (TrellisString) models.get(i));
			p.setModelIndex(i);
			paths.set(i, p);
		}

		return 0;
	}

	// Will return the index of the model best aligned
	public int bestAlignment()
	{
		int i, b = -1;
		double bestcost = RecogProps.INF_COST;

		for(i = 0; i < paths.size(); i++)
		{
			if(((IsoTrellisPath) paths.get(i)).getCost() < bestcost)
			{
				bestcost = ((IsoTrellisPath) paths.get(i)).getCost();
				b = i;
			}
		}

		return b;
	}

	public IsoTrellisPath[] bestAlignments(int best)
	{
	    IsoTrellisPath bestAls[] = new IsoTrellisPath[best];
	    
	    Object ps[] = paths.toArray();
	    
	    Comparator cmp = new Comparator() {
		public int compare(Object o1, Object o2) {
		    return (int) ( ((IsoTrellisPath) o1).getCost() - ((IsoTrellisPath) o2).getCost() );
		}
	    };
	    
	    Arrays.sort(ps, cmp);
	    
	    for(int i = 0; i < best; i++)
		bestAls[i] = (IsoTrellisPath) ps[i];

	    return bestAls;
	}

	public void clear()
	{
		data.clear();
		models.clear();
		paths.clear();
	}

	public static void main(String args[])
	{
		IsolatedRecog theApp = new IsolatedRecog();

		int modelnum = 0, bestalign = -1;
		String filepath;

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.print(GlobalProperties.getIntlString("Enter_the_data_file_path:_"));
		try
		{
		    filepath = stdin.readLine();
			//theApp.readData(filepath, 0);
			System.out.println(GlobalProperties.getIntlString("Data:"));
			//((TrellisString) theApp.getData()).write(System.out);

			System.out.print(GlobalProperties.getIntlString("Enter_the_model_list_file_path:_"));
			filepath = stdin.readLine();
			//int mdlcount = theApp.readModels(filepath, 0);
			//System.out.println("Number of models: " + mdlcount);
			//theApp.writeModels(System.out);

			theApp.alignAll();

			System.out.println(GlobalProperties.getIntlString("Number_of_paths:_") + theApp.countPaths());
			for(int i = 0; i < theApp.countPaths(); i++)
			{
				System.out.println(((IsoTrellisPath) theApp.getPath(i)).getCost());
				System.out.println(((IsoTrellisPath) theApp.getPath(i)).getModelIndex());
			}

			bestalign = theApp.bestAlignment();

			System.out.println(GlobalProperties.getIntlString("The_best_model_is:_") + bestalign);
		}
		catch(IOException e)
		{
			System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
		}
	}
}
