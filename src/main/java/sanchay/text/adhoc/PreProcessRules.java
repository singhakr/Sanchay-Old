package sanchay.text.adhoc;

import java.util.*;
import java.io.*;
import sanchay.GlobalProperties;

public class PreProcessRules 
{
	Hashtable Rules;
	Vector RuleNodeList;
	
	public PreProcessRules ()
	{
		Rules = new Hashtable();
		RuleNodeList = new Vector();
	}
	
	public String transferString(String word)
	{
		int i;
		String transferWord = word;
		for (i=0; i<RuleNodeList.size(); i++)
		{
			String ruleWordNode = RuleNodeList.get(i).toString();
			String ruleWordSubst = Rules.get(ruleWordNode).toString(); 
			if (ruleWordSubst!= null)
			{
				if(ruleWordSubst.equals(GlobalProperties.getIntlString("null")))
				{
					transferWord = transferWord.replaceAll(ruleWordNode, "");
				}
				else
				{
					transferWord = transferWord.replaceAll(ruleWordNode, ruleWordSubst);
				}
			}
		}
		String transferWord1 = "";
		for (i=0; i<transferWord.length(); i++)
		{
			
			if (Character.isIdentifierIgnorable(transferWord.charAt(i)) == false)
			transferWord1 = transferWord1 + transferWord.charAt(i);
		}
		transferWord = transferWord1;
		return transferWord;
	}
	
	public void readRules(String fileName)
	{
		try
		{
			BufferedReader lnReader = null;
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		    String line;
		    String splitline[] = null;
		    while((line = lnReader.readLine()) != null)
		    {
		    	splitline = line.split("\t");
		    	if(splitline.length == 2)
		    	{
		    		RuleNodeList.add(splitline[0]);
		    		Rules.put(splitline[0], splitline[1]);
		    	}
		    }
		    lnReader.close();
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void main (String args[])
	{
		try
		{
			PreProcessRules a= new PreProcessRules();
			a.readRules("/home/sanchay/tmp/preProcessAll");
			System.out.println(a.transferString("'ज़ज़ढढफ़फफ़फज़ज़ज़ज़'"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
