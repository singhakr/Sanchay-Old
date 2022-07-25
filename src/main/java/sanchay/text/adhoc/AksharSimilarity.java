package sanchay.text.adhoc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sanchay.GlobalProperties;
import sanchay.text.spell.PhoneticModelOfScripts;
import sanchay.text.AksharData;

@Deprecated
public class AksharSimilarity
{
	int max;
	AksharSimList simList;
	
	public AksharSimilarity(int max)
	{
		this.max = max;
	}
	
	public AksharSimList getSimList()
	{
		return simList;
	}
	
	public void generateSimList (AksharData data, PhoneticModelOfScripts phModel)
	{
		simList = new AksharSimList (max);
		int count;
		int i,j;
		double cost;
		String akshar1, akshar2;
		count = data.AksharList.size();
		for (i=0; i<count; i++)
		{
			akshar1 = (String) data.AksharList.get(i);
			for (j=0; j<count; j++)
			{
				akshar2 = (String) data.AksharList.get(j);
				cost = getAksharCost (akshar1, akshar2, phModel);
				if (cost < max)
				{
					simList.addAkshar(akshar1, akshar2, cost);
				}
			}
		}
	}
	
	public void generateSimTest (AksharData data, int aksharIndex, PhoneticModelOfScripts phModel)
	{
		int count;
		int i;
		double cost;
		String akshar = (String) data.AksharList.get(aksharIndex); 
		count = data.AksharList.size();
		for (i=0; i<count; i++)
		{
			cost = getAksharCost (akshar, (String) data.AksharList.get(i), phModel);
			if (cost < max)
			{
				System.out.println(akshar+" "+(String) data.AksharList.get(i)+" "+cost);
			}
		}
	}

	double getAksharCost(String aks1, String aks2, PhoneticModelOfScripts phModel)
	{
		int dataPos=0,modelPos=0; //data, model
		int a1Len, a2Len;
		int dataLen, modelLen;
		int dataX=0, modelX=0;
		double cost=0, tempCost, tempCost1;
		String data, model;
		a1Len = aks1.length();
		a2Len = aks2.length();
		char c1, c2;
		if (a1Len >= a2Len)
		{
			data = aks1;
			dataLen = a1Len;
			model = aks2;
			modelLen = a2Len;
		}
		else
		{
			data = aks2;
			dataLen = a2Len;
			model = aks1;
			modelLen = a1Len;
		}
		//dataPos = -1; modelPos =-1;
		// Diagoanal cost for 1st positions
		c1 = data.charAt(dataPos);
		c2 = model.charAt(modelPos);
		Character cha1 = new Character (c1);
		Character cha2 = new Character (c2);
		cost = phModel.getDistance(cha1, cha2);
		
		while (dataPos < (dataLen-1))
		{
			tempCost =500; tempCost1 =500;
			//Diaganol Cost
			if (dataPos < (dataLen-1) && modelPos < (modelLen-1))
			{
				c1 = data.charAt(dataPos+1);
				c2 = model.charAt(modelPos+1);
				Character ch1 = new Character (c1);
				Character ch2 = new Character (c2);
				tempCost = phModel.getDistance(ch1, ch2);
				tempCost1 = tempCost;
				dataX =1; modelX =1;
			}
			// Vertical 
			if (dataPos < (dataLen-1))
			{
				c1 = data.charAt(dataPos+1);
				c2 = model.charAt(modelPos);
				Character ch1 = new Character (c1);
				Character ch2 = new Character (c2);
				tempCost = phModel.getDistance(ch1, ch2);
				if (tempCost < tempCost1)
				{
					dataX =1; modelX =0;
					tempCost1 = tempCost;
				}	
			}
			// Horizontal
			if (modelPos < (modelLen-1))
			{
				c1 = data.charAt(dataPos);
				c2 = model.charAt(modelPos+1);
				Character ch1 = new Character (c1);
				Character ch2 = new Character (c2);
				tempCost = phModel.getDistance(ch1, ch2);
				if (tempCost < tempCost1)
				{
					dataX =0; modelX =1;
					tempCost1 = tempCost;
				}	
			}	
			cost = cost + tempCost1;
			dataPos = dataPos + dataX;
			modelPos = modelPos + modelX;
		}
		//System.out.println(aks1+" "+aks2+" "+cost);
		// HUERESTICS
		if (data.startsWith(model)) cost = cost *.25;
		//	PUNJABI HUERESTICS
		//if (data.endsWith("?")) cost = cost *.1;
		//	PUNJABI HUERESTICS
		
		//	ILI HUERESTICS
		if (data.endsWith("?") && data.startsWith(model)) cost = cost *.1;
		//	ILI HUERESTICS
		
		return cost;
	}
	
	public void writeSimilarityList(String file)
	{
		try 
		{
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
	        
        oos.writeObject(simList);
		oos.close();
		fos.close();
		}
		catch (Exception ex)
		{
			
		}
	}
	
	public void readSimilarityList(String file)
	{
		try 
		{
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);

	    simList = new AksharSimList(25);
		simList = (AksharSimList) ois.readObject();
		ois.close();	
		}
		catch (Exception ex)
		{
			
		}
	}
	
	public static void main (String[] args)
	{
		try
		{
			PhoneticModelOfScripts model = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman-hindi-hindi.txt"),
                    GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
			AksharSimilarity aks = new AksharSimilarity(25);
			aks.getAksharCost("", "", model);
			System.out.println(aks.getAksharCost("", "", model));
			AksharData a = new AksharData(GlobalProperties.getIntlString("hin::utf8"));
			a.readScript("/home/sanchay/tmp/devType.txt");
			a.makeGrammar();
			a.readDictionary("/home/sanchay/Hindi");
			//a.readDictionary("/home/sanchay/tmp/dictList");
			aks.generateSimTest(a, 0, model);
			aks.generateSimList(a,model);
			//aks.writeSimilarityList("/home/sanchay/tmp/AksharSimList");
			//aks.readSimilarityList("/home/sanchay/tmp/AksharSimListFinal");
			System.out.println(GlobalProperties.getIntlString("writen"));
			aks.writeSimilarityList("/home/sanchay/tmp/ILIEngSimList");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}	
}
