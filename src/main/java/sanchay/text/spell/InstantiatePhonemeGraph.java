package sanchay.text.spell;

//import org.jgrapht.demo.JGraphAdapterDemo;
import org.jgrapht.*;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.Graphs;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.mlearning.lm.ngram.NGram;
import sanchay.mlearning.lm.ngram.NGramLM;
import sanchay.mlearning.lm.ngram.impl.NGramLMImpl;
import sanchay.properties.PropertyTokens;

public class InstantiatePhonemeGraph {

	String phoneFile;
	NGramLM nglm = null;
	NGramLM cpmsNglm = null;
	Vector nodeLabelList;
	String language;
	String charset;
	SimpleDirectedGraph dgraph;
	Vector phoneVtxObjectList;
	Hashtable phonemeNodes;
	boolean homogeneous;
	String propManager;

	public InstantiatePhonemeGraph(String lang,String cs,boolean homogns, String propMan)
	{
		this(lang,cs,homogns);
		propManager = propMan;
	}
	public InstantiatePhonemeGraph(String lang,String cs,boolean homogns)
	{
		language = lang;
		charset = cs;
		homogeneous = homogns;
		propManager = "";
	}
	public String getCharSet()
	{
		return charset;
	}
	public Vector createPhonemeSequence(String fstring)
	{
		Vector cpmsSequence = new Vector();

		nglm = new NGramLMImpl((File)null, GlobalProperties.getIntlString("uchar"), 3  , GlobalProperties.getIntlString("UTF-8"), language);
        nglm.makeNGramLM(fstring);
//		File cpmsFout = new File("/home/taraka/phoneme-similarity/tel-temp.cpms");
//		PrintStream cpmsPs = null;
//		try
//		{
//			nglm.makeNGramLM(fstring);
//			cpmsPs = new PrintStream(cpmsFout, "UTF8");
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//			System.out.println("IOException Exception!");
//		}
		createUnigramNodes();
		cpmsSequence = createSequence(fstring);

//		Enumeration keyEnum = cpmsSequence.elements();
//		while(keyEnum.hasMoreElements())
//		{
//		System.out.println(keyEnum.nextElement());
//		}
		return cpmsSequence;
	}

    public Vector createPhonemeSequence2(String fstring) {
        Vector cpmsSequence = new Vector();

        nglm = new NGramLMImpl((File) null, "uchar", 3, "UTF-8", language);
        nglm.makeNGramLM(fstring);
//		File cpmsFout = new File("/home/taraka/phoneme-similarity/tel-temp.cpms");
//		PrintStream cpmsPs = null;
//		try
//		{
//			nglm.makeNGramLM(fstring);
//			cpmsPs = new PrintStream(cpmsFout, "UTF8");
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//			System.out.println("IOException Exception!");
//		}
        createUnigramNodes();
        cpmsSequence = createSequence2(fstring);

//		Enumeration keyEnum = cpmsSequence.elements();
//		while(keyEnum.hasMoreElements())
//		{
//		System.out.println(keyEnum.nextElement());
//		}
        return cpmsSequence;
    }

    /*
     * returns it as a vector of vectors
     */
    public Vector createSequence2(String fstring) {
        Vector cpmsVec = new Vector();
//		System.out.println("In readCharOthergrams");
//		PrintStream cpmsPs = null;
//		File f = new File("/home/taraka/phoneme-similarity/tel-temp.cpms");
        NGramLM cpmsFeaturesNGramLM = new NGramLMImpl((File) null, "uchar", 3);
//		try {
//			cpmsPs = new PrintStream("/home/taraka/phoneme-similarity/tel-temp-new.cpms", "UTF8");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        Hashtable phmHt = new Hashtable();
        SimpleDirectedGraph sdg = new SimpleDirectedGraph(DefaultEdge.class);
        for (int k = 0; k < fstring.length(); k++) {
            char ch = fstring.charAt(k);
            Vector tmp = PhonemeVertex.makeCpy((Vector) phonemeNodes.get((Object) ch));
//						System.out.println("Chracter "+ch+"\t"+tmp);
//						Vector tmp = (Vector)((Vector) phonemeNodes.get((Object)ch)).clone();
            phmHt.put(k, tmp);
        }

        for (int k = 0; k < fstring.length() - 1; k++) {
//						Vector src = (Vector) phmHt.get((Object)charStrs[k].charAt(0));
            Vector src = (Vector) phmHt.get(k);
//						Vector tgt = (Vector) phmHt.get((Object)charStrs[k+1].charAt(0));
            Vector tgt = (Vector) phmHt.get(k + 1);

            for (int m = 0; m < src.size(); m++) {
                sdg.addVertex(src.elementAt(m));
            }

            for (int n = 0; n < tgt.size(); n++) {
                sdg.addVertex(tgt.elementAt(n));
            }

            for (int m = 0; m < src.size(); m++) {
                for (int n = 0; n < tgt.size(); n++) {
                    Object srcObj = src.elementAt(m);
                    Object tgtObj = tgt.elementAt(n);

                    String srcLabel = ((PhonemeVertex) srcObj).getnodeLabel();
                    String tgtLabel = ((PhonemeVertex) tgtObj).getnodeLabel();

                    String srcfeatName;
                    String tgtfeatName;

                    if (srcObj.equals(tgtObj)) {
//									System.out.println("objects are equal");
                        continue;
                    }

                    if (homogeneous == true) {
                        srcfeatName = getFeatureName(srcLabel);
                        tgtfeatName = getFeatureName(tgtLabel);
                        if (sdg.containsEdge(srcObj, tgtObj) == false) {
                            sdg.addEdge(srcObj, tgtObj);

//										dgraph.setEdgeWeight(arg0, arg1)
                        }
                    } else {
                        if (sdg.containsEdge(srcObj, tgtObj) == false) {
                            sdg.addEdge(srcObj, tgtObj);
//										dgraph.setEdgeWeight(arg0, arg1)
                        }
                    }
                }
            }
        }
        //retrieving all the edges from the above graph
        char srcCh = fstring.charAt(0);
        char tgtCh = fstring.charAt(fstring.length() - 1);

        Vector src = (Vector) phmHt.get(0);
        Vector tgt = (Vector) phmHt.get(fstring.length() - 1);

        Set edgeSet = null;
        int maxPaths = 10000;

        for (int m = 0; m < src.size(); m++) {
            Object srcObj = src.elementAt(m);
            KShortestPaths ksp = new KShortestPaths(sdg, srcObj, maxPaths, fstring.length() - 1);

            for (int n = 0; n < tgt.size(); n++) {
                Object tgtObj = tgt.elementAt(n);
                List pathList = null;

                if (srcObj.equals(tgtObj) == false) {
//								System.out.println("Calculating path");
                    pathList = ksp.getPaths(tgtObj);
                } else {
                    continue;
                }
                ListIterator pathListIterator;
                if (pathList != null) {
                    pathListIterator = pathList.listIterator();
                } else {
                    continue;
                }

                while (pathListIterator.hasNext()) {
                    GraphPath gp = (GraphPath) pathListIterator.next();
                    List edgeList = gp.getEdgeList();
                    Graphs gs = null;

                    Object startVrtx = gp.getStartVertex();
                    Object endVrtx = gp.getEndVertex();
                    ListIterator edgeIterator = edgeList.listIterator();
                    Vector fngStr = new Vector();

                    while (edgeIterator.hasNext()) {
                        fngStr.add(((PhonemeVertex) startVrtx).getnodeLabel());
//									cpmsPs.print(((PhonemeVertex)startVrtx).getnodeLabel()+"@#&");
                        startVrtx = gs.getOppositeVertex(sdg, edgeIterator.next(), startVrtx);
                    }
//								cpmsPs.println(((PhonemeVertex)endVrtx).getnodeLabel());
                    fngStr.add(((PhonemeVertex) startVrtx).getnodeLabel());
                    cpmsVec.add(fngStr);
                }
//					cpmsFeaturesNGramLM.addNGram(fngStr, i);
            }

        }
        return cpmsVec;
    }

	public NGramLM createPhonemeUnigrams(String fstring)
	{
		NGramLM cpmsNGram = null;

		nglm = new NGramLMImpl((File)null, GlobalProperties.getIntlString("uchar"), 3  , GlobalProperties.getIntlString("UTF-8"), language);
        nglm.makeNGramLM(fstring);
//		File cpmsFout = new File("/home/taraka/phoneme-similarity/tel-temp.cpms");
//		PrintStream cpmsPs = null;
//		try
//		{
//			cpmsPs = new PrintStream(cpmsFout, "UTF8");
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//			System.out.println("IOException Exception!");
//		}
		createUnigramNodes();
		cpmsNGram = readCharOthergrams((File)null);
		return cpmsNGram;
	}
	public NGramLM createPhonemeUnigrams(File fout)
	{
//		if(fname == "")
//		phoneFile = "/home/taraka/phoneme-similarity/tel-temp.cpms";
//		else
//		phoneFile = fname;
//		File fout = new File(fname);

//		File fout = new File("/home/taraka/phoneme-similarity/temp");
		NGramLM cpmsNGram = null;

		nglm = new NGramLMImpl(fout, GlobalProperties.getIntlString("uchar"), 3 , GlobalProperties.getIntlString("UTF-8"), language);
//		File cpmsFout = new File("/home/taraka/phoneme-similarity/tel-temp.cpms");
//		PrintStream cpmsPs = null;
		try
		{
			nglm.makeNGramLM(fout);
//			cpmsPs = new PrintStream(cpmsFout, "UTF8");
//			nglm.printNGrams(2, System.out );
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
		}
//		cpmsNglm = nglm.getCPMSFeaturesNGramLM(fout);
//		cpmsNglm.saveNGramLMBinary(cpmsPs);



		//intiate the file name

//		File phoneFileIn = new File(phoneFile);
//		readPhoneUnigrams(cpmsFout);

		createUnigramNodes();
		cpmsNGram = readCharOthergrams(fout);
		return cpmsNGram;
	}
	public void createUnigramNodes()
	{
//		System.out.println("createUnigramNodes");
		phonemeNodes = new Hashtable();

		Iterator<List<Integer>> itr = nglm.getNGramKeys(1);

		while(itr.hasNext())
		{
			List<Integer> key = itr.next();
//			System.out.println("In Enum loop key"+key);
			NGram ng = (NGram) nglm.getNGram(key, 1);

			String ngStr = ng.getString(nglm);

//			nodeLabelList.addElement((Object)ngStr);
//			System.out.println("HI"+ngStr);
			PhoneticModelOfScripts cpms = null;

			try {
				if(propManager.length() > 0)
					cpms = new PhoneticModelOfScripts(propManager, charset, language);
				else
					cpms = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
                            charset, language);

                                cpms.setNgramLM(cpmsNglm);
                                
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			PropertyTokens featureList = cpms.getFeatureList();

			int fcount = featureList.countTokens();
			//creating the non-NULL nodes for a bigram. Also checking for "~" and "^" character
			int flag = 0;
			char cch = ngStr.charAt(0);
//			System.out.println(cch);
			Vector temp = new Vector();

			for (int j = 0; j < fcount; j++)
			{
				String feature = featureList.getToken(j);

//				System.out.println(feature);

				String fngStr = "";

//				System.out.println(chStr);

				String fvStr = GlobalProperties.getIntlString("NULL");
				FeatureStructure fs = cpms.getFeatureStructure(cch, language);

				if(fs != null)
				{
//					System.out.println(fs.makeString());

					FeatureAttribute fa = fs.getAttribute(feature);

					FeatureValue fv = null;

					if(fa != null)
					{
						fv = fa.getAltValue(0);

						if(fv != null)
						{
							fvStr = (String) fv.getValue();
//							System.out.println("----"+fvStr);
						}
					}
				}
//				fvStr = feature + "=" + fvStr;
				if(cch == '~')
				{
					temp.add((Object)feature + GlobalProperties.getIntlString("=START"));
//					System.out.print(feature+ "=START ");
				}
//				((Vector)featureNodes.elementAt(i)).add((Object)feature + "=START");
				else if(cch == '^')
				{
					temp.add((Object)feature + GlobalProperties.getIntlString("=END"));
//					System.out.print(feature+ "=END ");
				}
//				((Vector)featureNodes.elementAt(i)).add((Object)feature + "=END");
				else if(fvStr.equals(GlobalProperties.getIntlString("NULL")) == false && cch != '~' && cch != '^')
				{
					temp.add((Object)feature + "=" + fvStr);
//					((Vector)featureNodes.elementAt(i)).add((Object)feature + "=" + fvStr);
//					System.out.print(feature+ "="+fvStr+" ");
				}
			}
//			System.out.println();
			Vector addToHash = createNodes(temp);
			phonemeNodes.put((Object)cch, (Object)addToHash);
		}
	}

	public Vector createSequence(String fstring)
	{
		Vector cpmsVec = new Vector();
//		System.out.println("In readCharOthergrams");
//		PrintStream cpmsPs = null;
//		File f = new File("/home/taraka/phoneme-similarity/tel-temp.cpms");
		NGramLM cpmsFeaturesNGramLM = new NGramLMImpl((File)null, GlobalProperties.getIntlString("uchar"), 3);
//		try {
//			cpmsPs = new PrintStream("/home/taraka/phoneme-similarity/tel-temp-new.cpms", "UTF8");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
					Hashtable phmHt = new Hashtable();
					SimpleDirectedGraph sdg = new SimpleDirectedGraph(DefaultEdge.class);
					for(int k = 0;k < fstring.length();k++)
					{
						char ch = fstring.charAt(k);
						Vector tmp = PhonemeVertex.makeCpy((Vector) phonemeNodes.get((Object)ch));
//						System.out.println("Chracter "+ch+"\t"+tmp);
//						Vector tmp = (Vector)((Vector) phonemeNodes.get((Object)ch)).clone();
						phmHt.put(k, tmp);
					}

					for(int k = 0;k < fstring.length() - 1;k++)
					{
//						Vector src = (Vector) phmHt.get((Object)charStrs[k].charAt(0));
						Vector src = (Vector) phmHt.get(k);
//						Vector tgt = (Vector) phmHt.get((Object)charStrs[k+1].charAt(0));
						Vector tgt = (Vector) phmHt.get(k+1);

						for(int m = 0;m < src.size();m++)
							sdg.addVertex(src.elementAt(m));

						for(int n = 0;n < tgt.size();n++)
							sdg.addVertex(tgt.elementAt(n));

						for(int m = 0;m < src.size();m++)
						{
							for(int n = 0;n < tgt.size();n++)
							{
								Object srcObj = src.elementAt(m);
								Object tgtObj = tgt.elementAt(n);

								String srcLabel = ((PhonemeVertex)srcObj).getnodeLabel();
								String tgtLabel = ((PhonemeVertex)tgtObj).getnodeLabel();

								String srcfeatName;
								String tgtfeatName;

								if(srcObj.equals(tgtObj))
								{
//									System.out.println("objects are equal");
									continue;
								}

								if(homogeneous == true)
								{
									srcfeatName = getFeatureName(srcLabel);
									tgtfeatName = getFeatureName(tgtLabel);
									if(sdg.containsEdge(srcObj,tgtObj) == false)
									{
										sdg.addEdge(srcObj,tgtObj);

//										dgraph.setEdgeWeight(arg0, arg1)
									}
								}
								else
								{
									if(sdg.containsEdge(srcObj,tgtObj) == false)
									{
										sdg.addEdge(srcObj,tgtObj);
//										dgraph.setEdgeWeight(arg0, arg1)
									}
								}
							}
						}
					}
					//retrieving all the edges from the above graph
					char srcCh = fstring.charAt(0);
					char tgtCh = fstring.charAt(fstring.length() - 1);

					Vector src = (Vector) phmHt.get(0);
					Vector tgt = (Vector) phmHt.get(fstring.length() - 1);

					Set edgeSet = null;
					int maxPaths = 10000;

					for(int m = 0;m < src.size();m++)
					{
						Object srcObj = src.elementAt(m);
						KShortestPaths ksp = new KShortestPaths(sdg,srcObj,maxPaths,fstring.length()-1);

						for(int n = 0;n < tgt.size();n++)
						{
							Object tgtObj = tgt.elementAt(n);
							List pathList = null;

							if(srcObj.equals(tgtObj) == false){
//								System.out.println("Calculating path");
								pathList = ksp.getPaths(tgtObj);
							}
							else
								continue;
							ListIterator pathListIterator;
							if(pathList != null)
								pathListIterator = pathList.listIterator();
							else
								continue;

							while(pathListIterator.hasNext())
							{
								GraphPath gp = (GraphPath)pathListIterator.next();
								List edgeList = gp.getEdgeList();
								Graphs gs = null;

								Object startVrtx = gp.getStartVertex();
								Object endVrtx = gp.getEndVertex();
								ListIterator edgeIterator = edgeList.listIterator();
								String fngStr = "";

								while(edgeIterator.hasNext())
								{
									fngStr += ((PhonemeVertex)startVrtx).getnodeLabel() + GlobalProperties.getIntlString("_");
//									cpmsPs.print(((PhonemeVertex)startVrtx).getnodeLabel()+"@#&");
									startVrtx = gs.getOppositeVertex(sdg, edgeIterator.next(), startVrtx);
								}
//								cpmsPs.println(((PhonemeVertex)endVrtx).getnodeLabel());
								fngStr += ((PhonemeVertex)startVrtx).getnodeLabel();
								cpmsVec.add(fngStr);
							}
//					cpmsFeaturesNGramLM.addNGram(fngStr, i);
				}

			}
		return cpmsVec;
	}
    
	public NGramLM readCharOthergrams(File f)
	{
//		System.out.println("In readCharOthergrams");
//		PrintStream cpmsPs = null;
//		File f = new File("/home/taraka/phoneme-similarity/tel-temp.cpms");
		NGramLM cpmsFeaturesNGramLM = new NGramLMImpl(f, GlobalProperties.getIntlString("uchar"), 3);
//		try {
//			cpmsPs = new PrintStream("/home/taraka/phoneme-similarity/tel-temp-new.cpms", "UTF8");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		for(int i = 1; i <= nglm.getNGramOrder(); i++)
//			for(int i = 1; i <= 3; i++)
		{
			Iterator<List<Integer>> itr = nglm.getNGramKeys(i);
//			System.out.println("No of character ngrams of type "+i+" are "+nglm.countTypes(i));
			while(itr.hasNext())
			{
				List<Integer> key = itr.next();
//				System.out.println("In Enum loop key"+key);
				NGram ng = (NGram) nglm.getNGram(key, i);

				String ngStr = ng.getString(nglm);
//				nodeLabelList.addElement((Object)ngStr);
//				System.out.println("HI"+ngStr+" freq="+ng.getFreq());
				if(i == 1)
				{
					Vector src = (Vector) phonemeNodes.get((Object)ngStr.charAt(0));
					for(int l = 0;l < src.size();l++)
					{
						cpmsFeaturesNGramLM.addNGram(((PhonemeVertex)src.elementAt(l)).getnodeLabel(), ng, i);
//						cpmsFeaturesNGramLM.addNGram(((PhonemeVertex)src.elementAt(l)).getnodeLabel(), i);
					}
				}
				else
				{
					String charStrs[] = ngStr.split("@#&");
					SimpleDirectedGraph sdg = new SimpleDirectedGraph(DefaultEdge.class);
//					checking for some ngrams which are bad
//					for(int k = 0;k < charStrs.length - 1;k++)
//					{
//					if((charStrs[k].equalsIgnoreCase("~") || charStrs[k].equalsIgnoreCase("^")) && (charStrs[k+1].equalsIgnoreCase("~") || charStrs[k+1].equalsIgnoreCase("^")))
//					{
////					System.out.println("removed bad ngram");
//					continue;
//					}
//					}
					Hashtable phmHt = new Hashtable();
					for(int k = 0;k < charStrs.length;k++)
					{
						char ch = charStrs[k].charAt(0);
						Vector tmp = PhonemeVertex.makeCpy((Vector) phonemeNodes.get((Object)ch));
//						System.out.println("Chracter "+ch+"\t"+tmp);
//						Vector tmp = (Vector)((Vector) phonemeNodes.get((Object)ch)).clone();
						phmHt.put(k, tmp);
					}

					for(int k = 0;k < charStrs.length - 1;k++)
					{
//						Vector src = (Vector) phmHt.get((Object)charStrs[k].charAt(0));
						Vector src = (Vector) phmHt.get(k);
//						Vector tgt = (Vector) phmHt.get((Object)charStrs[k+1].charAt(0));
						Vector tgt = (Vector) phmHt.get(k+1);

						for(int m = 0;m < src.size();m++)
							sdg.addVertex(src.elementAt(m));

						for(int n = 0;n < tgt.size();n++)
							sdg.addVertex(tgt.elementAt(n));

						for(int m = 0;m < src.size();m++)
						{
							for(int n = 0;n < tgt.size();n++)
							{
								Object srcObj = src.elementAt(m);
								Object tgtObj = tgt.elementAt(n);

								String srcLabel = ((PhonemeVertex)srcObj).getnodeLabel();
								String tgtLabel = ((PhonemeVertex)tgtObj).getnodeLabel();

								String srcfeatName;
								String tgtfeatName;

								if(srcObj.equals(tgtObj))
								{
//									System.out.println("objects are equal");
									continue;
								}

								if(homogeneous == true)
								{
									srcfeatName = getFeatureName(srcLabel);
									tgtfeatName = getFeatureName(tgtLabel);
									if(sdg.containsEdge(srcObj,tgtObj) == false)
									{
										sdg.addEdge(srcObj,tgtObj);

//										dgraph.setEdgeWeight(arg0, arg1)
									}
								}
								else
								{
									if(sdg.containsEdge(srcObj,tgtObj) == false)
									{
										sdg.addEdge(srcObj,tgtObj);
//										dgraph.setEdgeWeight(arg0, arg1)
									}
								}
							}
						}
					}
					//retrieving all the edges from the above graph
					char srcCh = charStrs[0].charAt(0);
					char tgtCh = charStrs[charStrs.length - 1].charAt(0);

					Vector src = (Vector) phmHt.get(0);
					Vector tgt = (Vector) phmHt.get(charStrs.length - 1);

					Set edgeSet = null;
					int maxPaths = 10000;

					for(int m = 0;m < src.size();m++)
					{
						Object srcObj = src.elementAt(m);
						KShortestPaths ksp = new KShortestPaths(sdg,srcObj,maxPaths,i-1);

						for(int n = 0;n < tgt.size();n++)
						{
							Object tgtObj = tgt.elementAt(n);
							List pathList = null;

							if(srcObj.equals(tgtObj) == false){
//								System.out.println("Calculating path");
								pathList = ksp.getPaths(tgtObj);
							}
							else
								continue;
							ListIterator pathListIterator;
							if(pathList != null)
								pathListIterator = pathList.listIterator();
							else
								continue;

							while(pathListIterator.hasNext())
							{
								GraphPath gp = (GraphPath)pathListIterator.next();
								List edgeList = gp.getEdgeList();
								Graphs gs = null;

								Object startVrtx = gp.getStartVertex();
								Object endVrtx = gp.getEndVertex();
								ListIterator edgeIterator = edgeList.listIterator();
								String fngStr = "";

								while(edgeIterator.hasNext())
								{
									fngStr += ((PhonemeVertex)startVrtx).getnodeLabel() + "@#&";
//									cpmsPs.print(((PhonemeVertex)startVrtx).getnodeLabel()+"@#&");
									startVrtx = gs.getOppositeVertex(sdg, edgeIterator.next(), startVrtx);
								}
//								cpmsPs.println(((PhonemeVertex)endVrtx).getnodeLabel());
								fngStr += ((PhonemeVertex)startVrtx).getnodeLabel();
//								System.out.println("path string is "+fngStr);
								if(fngStr.equals("") == false)
									cpmsFeaturesNGramLM.addNGram(fngStr, ng, i);
//								cpmsFeaturesNGramLM.addNGram(fngStr, i);
							}

						}
					}

				}
			}
		}
        
//		cpmsFeaturesNGramLM.calcProbsNSmooth();
//		cpmsFeaturesNGramLM.saveNGramLMBinary(cpmsPs);
//		cpmsFeaturesNGramLM.saveNGramLMBinary(System.out);
		cpmsFeaturesNGramLM.calcSimpleProbs();
		return cpmsFeaturesNGramLM;


	}
    
	public String getFeatureName(String str)
	{
		String splitstr [] = str.split("\\=");
		return splitstr[0];
	}
	public Vector createNodes(Vector nodeList)
	{
		Vector temp = new Vector();

		for(int i = 0;i < nodeList.size();i++)
		{
			PhonemeVertex pv = new PhonemeVertex((String)nodeList.elementAt(i));
//			dgraph.addVertex((Object)pv);
			temp.add((Object)pv);
		}
		return temp;
	}

	public static void main(String[] args) {
		InstantiatePhonemeGraph ipg = new InstantiatePhonemeGraph(GlobalProperties.getIntlString("hin::utf8"),GlobalProperties.getIntlString("UTF-8"),false);
//		File f = new File("/home/taraka/phoneme-similarity/temp");
		String cs = ipg.getCharSet();
//		SimpleDirectedGraph dummy = null;
//		try {
//		ipg.readFile(f, cs);
		Vector vec ;
//		NGramLM ng = ipg.createPhonemeUnigrams("माला");
//		ng.saveNGramLMBinary(System.out);
		vec = ipg.createPhonemeSequence("कानदारप");
		for(int i = 0;i < vec.size();i++)
    		System.out.println(GlobalProperties.getIntlString("Element_is_")+vec.elementAt(i));
//		}
//        catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		}
//		String fname = "";
//		ipg.createPhonemeUnigrams(fname);
	}
}