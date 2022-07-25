package sanchay.text;

import java.io.*;
import java.util.Vector;
import sanchay.GlobalProperties;
import sanchay.speech.decoder.isolated.*;
import sanchay.speech.common.TrellisString;
import sanchay.text.spell.PhoneticModelOfScripts;
import sanchay.text.adhoc.*;

public class AksharData {

	Vector ScriptType;
	Vector ScriptUTF;
	public Vector AksharList;
	int AksharCount[];
	Vector Grammar;
	public SimilarityList list;
	
	protected String langEnc;
        
	public AksharData (String langEnc)
	{
            this.langEnc = langEnc;
            
            ScriptType = new Vector();
            ScriptUTF = new Vector();
            AksharList = new Vector();
            //AksharCount = new Vector();
            Grammar = new Vector();
            AksharCount = new int[5000];
            initArray();
	}
	
	public void initArray()
	{
		int i;
		for (i=0; i<2000; i++)
		{
			AksharCount[i] = 0;
		}
	}
	
	public void readScript(String file) 
	{
		try
		{
                    BufferedReader lnReader = null;
                    lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), GlobalProperties.getIntlString("UTF-8")));

                    String line;
                    String splitline[] = null;
                    while((line = lnReader.readLine()) != null)
                    {
                        splitline = line.split("\t");
                        ScriptUTF.add(splitline[1]);
                        ScriptType.add(splitline[2]);
                    }

                }
		catch(Exception ex)
		{
                    ex.printStackTrace();
                }
		
	}
	
        public void makeGrammarJodo ()
	{
		//Grammar.add("vo"); // Q
//		Grammar.add("chch");
            
//            Grammar.add("ck");
            
            if(langEnc.startsWith(GlobalProperties.getIntlString("tam::")))
		Grammar.add("h");
            else
		Grammar.add("ch");
            
            Grammar.add("v");
            Grammar.add("o");
            Grammar.add("c");
            Grammar.add("m");
            //Grammar.add("ckmo");
            //Grammar.add("cmo");
            //Grammar.add("ckm");
            //Grammar.add("cm");
            //Grammar.add("co");
//		Grammar.add("chckmo");
//		Grammar.add("chcmo");
//		Grammar.add("chckm");
//		Grammar.add("chcm");
//		Grammar.add("chco");
//		Grammar.add("chchckmo");
//		Grammar.add("chchcmo");
//		Grammar.add("chchckm");
//		Grammar.add("chchcm");
//		Grammar.add("chchc");
//		Grammar.add("chc");
	}

        
	public void makeGrammar ()
	{
		Grammar.add("vo"); // Q
		Grammar.add("v");
		Grammar.add("ckmo");
		Grammar.add("cmo");
		Grammar.add("ckm");
		Grammar.add("cm");
		Grammar.add("co");
//		Grammar.add("chckmo");
//		Grammar.add("chcmo");
//		Grammar.add("chckm");
//		Grammar.add("chcm");
//		Grammar.add("chco");
//		Grammar.add("chchckmo");
//		Grammar.add("chchcmo");
//		Grammar.add("chchckm");
//		Grammar.add("chchcm");
//		Grammar.add("chchc");
//		Grammar.add("chc");
		Grammar.add("ch");
		Grammar.add("c");
	}
	
	public void makeGrammarAkshar ()
	{
		Grammar.add("vo"); // Q
		Grammar.add("v");
		Grammar.add("ckmo");
		Grammar.add("cmo");
		Grammar.add("ckm");
		Grammar.add("cm");
		Grammar.add("co");
		Grammar.add("chckmo");
		Grammar.add("chcmo");
		Grammar.add("chckm");
		Grammar.add("chcm");
		Grammar.add("chco");
		Grammar.add("chchckmo");
		Grammar.add("chchcmo");
		Grammar.add("chchckm");
		Grammar.add("chchcm");
		Grammar.add("chchc");
		Grammar.add("chc");
		Grammar.add("ch");
		Grammar.add("c");
	}
	
	public void makeGrammarRoman ()
	{
		Grammar.add("v");
		//Grammar.add("ckmo");
		//Grammar.add("cmo");
		//Grammar.add("ckm");
		//Grammar.add("cm");
		//Grammar.add("chckmo");
		//Grammar.add("chcmo");
		//Grammar.add("chckm");
		//Grammar.add("chcm");
		Grammar.add("chc");
		Grammar.add("ch");
		Grammar.add("c");
	}
	
	public String getType (String s)
	{
		int i;
		for (i=0; i< ScriptType.size(); i++)
		{
			String ch = (String) ScriptUTF.get(i);
			if (ch.equals(s))
			{
				return (String) ScriptType.get(i);
			}
		}
		
		if (s.equals("")) //s.length()==0)
		{
			return "";
		}
		else
		{
			return "x";
		}
	}

	public String convertType (String s)
	{
		int i;
		String conv, tmp;
		conv = "";
		char temp[] = {' '};
		for (i=0; i< s.length(); i++)
		{
//			temp[0] = s.charAt(i);
//    		tmp = new String(temp);
			
			tmp = s.substring(i, i + 1);

				conv = conv + getType (tmp);
		}
		return conv;
	}
	
	public void addAkshar (String s)
	{
		int i,k;
		String tmp;
		if (AksharList.contains((Object)s)==false)
		{
			AksharList.add(s);
		}
		for (i=0; i<AksharList.size(); i++)
		{
			tmp = (String) AksharList.get(i);
			if (tmp.equals(s))
			{
				k = AksharCount [i];
				AksharCount [i] = k+1;
				break;
			}
		}
	}

	
	public void readDictionary(String file) 
	{
		try
		{
		BufferedReader lnReader = null;
		lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String line, word, type, akshar, tmp, typeStr, typeStr1, grammar;
	    PreProcessRules rule = new PreProcessRules();
	    rule.readRules(GlobalProperties.getHomeDirectory() + "/" + "tmp/preProcessAll");
	    boolean gotGrammar = false; 
	    char temp[] = {' '};
	    String splitline[] = null;
	    int time = 0;
	    int i,len;
	    tmp = " ";
	    grammar = "";
	    while((line = lnReader.readLine()) != null)
	    {
	    	splitline = line.split(" ");
	    	word = splitline[0];
	    	word = word.replaceAll("[a-z]|[A-Z]|[0-9]|-|\'|'.'", "");
	    	word = rule.transferString(word); 
	    	typeStr = convertType (word);
	    	typeStr1 = typeStr;
	    	while (word.length()>0)
	    	{
	    		akshar = "";
	    		for (i=0; i< Grammar.size(); i++)
	    		{
	    			grammar = (String) Grammar.get(i);
	    			word = word.trim(); // Q
	    			typeStr = typeStr.trim();
		    		if (typeStr.startsWith(grammar))
		    		{
		    			akshar = word.substring(0,grammar.length());
		    			addAkshar(akshar);
		    			word = word.substring(grammar.length());
		    			typeStr = typeStr.substring(grammar.length());
		    			gotGrammar = true;
		    			break;
		    		}
	    		}
	    		if (gotGrammar==false)
		    		{
		    			System.out.println(word + " "+ typeStr);
		    			typeStr = typeStr.substring(1);
		    			word = word.substring(1);
		    		}
	    		gotGrammar = false;
	    	}
	    }
	    	time++;
	    	
	    }
		catch(Exception ex)
		{}
		
	}
	
	public String getAkshar(String wordIn) 
	{

	    String line, word, type, akshar, tmp, typeStr, typeStr1, grammar;
	    boolean gotGrammar = false; 
	    char temp[] = {' '};
	    String splitline[] = null;
	    int time = 0;
	    int i,len;
	    akshar = "";
	    tmp = " ";
	    grammar = "";
            word = wordIn;
            word = word.replaceAll("[a-z]|[A-Z]|[0-9]|-|\'|'.'", "");
            typeStr = convertType (word);
            typeStr1 = typeStr;
            
            while (word.length() > 0)
            {
                    akshar = "";
                    for (i=0; i< Grammar.size(); i++)
                    {
                            grammar = (String) Grammar.get(i);
                            word = word.trim(); // Q
                            typeStr = typeStr.trim();
                            if (typeStr.startsWith(grammar))
                            {
                                    akshar = word.substring(0,grammar.length());
                                    addAkshar(akshar);
                                    word = word.substring(grammar.length());
                                    typeStr = typeStr.substring(grammar.length());
                                    gotGrammar = true;
                                    return akshar;
                            }
                    }
                    
                    if (gotGrammar==false)
                    {
                        //System.out.println(wordIn + "# #" +  word + " "+ typeStr);
                        typeStr = typeStr.substring(1);
                        word = word.substring(1);
                    }
                    
                    gotGrammar = false;
            }

            return akshar;
	}
	
	public void getAksharSimilarityList()
	{
		list = new SimilarityList();
		try
		{
		PhoneticModelOfScripts model = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
                GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
		
		String data, modl;
		String aksharPath;
		TrellisString dataStr, modelStr;
		int i,j,k;
		IsoTrellisPath path;
		double cost;
		for (i=0; i<AksharList.size(); i++)
		{
			IsolatedRecog recog = new IsolatedRecog ();
			AksharSimilarity aksharSim = new AksharSimilarity ();
			data = (String)AksharList.get(i);
			dataStr = model.getTrellisString(data);
			recog.setData(dataStr);
			aksharSim.akshar = data;
			for (j=0; j<AksharList.size(); j++)
			{
				modl = (String)AksharList.get(j);
				modelStr = model.getTrellisString(modl);
				//path = recog.alignString(dataStr, modelStr);
				//cost = path.getCost();
				//System.out.println(data + " - "+ modl + "= " + cost);
				recog.addModel(modelStr);
			}
			recog.alignAll();			
			IsoTrellisPath[] paths = recog.bestAlignments(50);
			for (j=0; j<paths.length; j++)
			{
				
				k = paths[j].getModelIndex();
				aksharPath = (String)AksharList.get(k);
				//System.out.println(data + " " + aksharPath + " = " + paths[j].getCost());
				aksharSim.similarityString[j] = aksharPath;
				aksharSim.similarityCost [j] = paths[j].getCost();
			}
			list.list.put(data, aksharSim);
		}
		
		}
		catch (Exception e)
		{
			//System.out.println(e.)
			e.printStackTrace();
		}
	}
	
	public void writeSimilarityList(String file)
	{
		try 
		{
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
	        
        oos.writeObject(list);
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

	    list = new SimilarityList();
		list = (SimilarityList) ois.readObject();
		ois.close();	
		}
		catch (Exception ex)
		{
			
		}
	}
		
	public static void main (String[] args)
	{
		AksharData a = new AksharData(GlobalProperties.getIntlString("hin::utf8"));
		a.readScript("/home/sanchay/tmp/devType.txt");
		a.makeGrammarAkshar();
		a.readDictionary("/home/sanchay/tmp/hindiweb.utf.ngramlm");
		System.out.println("adfffffffffffff" + a.AksharList.size());
		int i;
		for (i=0; i<a.AksharList.size(); i++)
			System.out.println(a.AksharList.get(i).toString() + " "+ a.AksharCount[i]);
	}
	
}
