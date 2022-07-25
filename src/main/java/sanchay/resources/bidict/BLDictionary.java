package sanchay.resources.bidict;

import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;


/**Documentation:BLDictionary
<p>ABSTRACT

<p>A dictionary has source language words, with each word having many senses. The word
also has a grammatical category (tag). Each of it senses has meanings specified in
the target language, example sentences, etc. This class represents one such dictionary.

<p>DESCRIPTION:

This class was written as part of an API for dictionaries which are used heavily
in natural language processing. It can also be useful for tasks like spell checking.
Currently, the implementation is restricted to the Shabdanjali English-Hindi dictionary,
which is based on the translex gram format, but it can be very easily adapted for
other dictionaries and languages.

<p>The BiDict class, in addition to its own (scalar) attributes, is made up of objects of
BiDictEntry class (representing dictionary entries), which in turn is made up of objects
of the Sense class (representing a sense of a word).

	<p>SL (Source language): English
	<p>TL (Target language): Hindi or some other Indian language

<p>Note that the attributes are not meant to be used directly. You should use them through
the public methods. We have listed them above only to show what is contained in the class.
<p><p><p>(NOTE: BLDictEntry IS A LINKEDLIST, THEREFORE WHENEVER YOU GET
	A BLDictEntry REFERENCE IT IS THE FIRST NODE OF THE LIST.)<p><p><p>
*/

public class BLDictionary
{
	/**<p>The SL name*/
	protected String srclang;
	/**<p>The TL name*/
	protected String tgtlang;
	/**<p>HashTable consisting of BLDictEntry(key:srcword,tag)*/
	protected Hashtable srcentries;
	/**<p>HashTable consisting of BLDictEntry(key:tgtword,tag)*/
	protected Hashtable tgtentries;
	/**<p>Vector containing all the possible tags in the dictionary.*/
	protected Vector possible_tags;
	/**<p>Boolean value indicating wheather tgtentries are valid/formed or not.*/
	protected boolean indexed;

	/**<p>Contructor, assigns the source and the target language, creates the Hashtables and vector, assigns 
	the indexes flag to false.*/
	public BLDictionary(String sl /* source language */, String tl /* target language */)
	{
		srclang = sl;
		tgtlang = tl;

		srcentries = new Hashtable();
		tgtentries = new Hashtable();
		possible_tags = new Vector();
		
		indexed = false;
	}
	
	/**<p>Returns the source language as string.*/
	public String getSrcLang() 
	{
		return srclang;
	}
	
	/**<p>Sets srclang to the pass string argument.*/
	public void setSrcLang(String l) 
	{
		srclang = l;
	}
	
	/**<p>Returns the target language as string.*/
	public String getTgtLang() 
	{
		return tgtlang;
	}
	
	/**<p>Sets tgtlang to the pass string argument.*/
	public void setTgtLang(String l) 
	{
		tgtlang = l;
	}
	
	/**<p>Returns a vector of BLDictEntry containing sw with all possible tags.*/
	public Vector getEntries(String sw)
	{
		Vector bldictentry_vector = new Vector();
		String str = null;
		Iterator itr = possible_tags.iterator();

		BLDictEntry bldictentry = null;

		while(itr.hasNext())
		{
			str = sw + "::" + itr.next();
			bldictentry = getEntry(str);
			if(bldictentry != null)
			{
				bldictentry_vector.addElement(bldictentry);
			}
		}

		return bldictentry_vector;
	}
	
	/**<p>Returns a BLDictEntry reference.*/
	public BLDictEntry getEntries(String sw,String tag)
	{
		String str = null;
		BLDictEntry bldictentry = null;
		
		str = sw + "::" + tag;
		bldictentry = getEntry(str);
		return bldictentry;
	}

	
	/**<p>Returns a BLDictEntry vector.*/
	public Vector getEntriesByTW(String tw)
	{
		if(indexed == false) indexByTW();
		
		Vector bldictentry_vector = new Vector();
		Vector tmpvector = null;
		String str = null;
		Iterator itr = possible_tags.iterator();
		int count;

		BLDictEntry bldictentry = null;

		while(itr.hasNext())
		{
			str = tw + "::" + itr.next();
			tmpvector = (Vector) tgtentries.get(str);
			
			if(tmpvector != null)
			{
				count = tmpvector.size();
				for(int i = 0; i < count; i++)
				{
					bldictentry = (BLDictEntry) tmpvector.get(i);
					if(bldictentry != null)
					{
						bldictentry_vector.addElement(bldictentry);
					}
				}
			}
		}

		return bldictentry_vector;
	}
	
	/**<p>Returns a BLDictEntry vector.*/
	public Vector getEntriesByTW(String tw,String tag)
	{
		if(indexed == false) indexByTW();
		
		String str = tw + "::" + tag;
		return (Vector) tgtentries.get(str);
	}

	
	/**<p>Returns a String Vector containing all the possible tags present in the dictionary.*/
	public Vector getTags(String sw)
	{
		Vector str_vector = new Vector();
		String str = null;
		String tag = null;
		Iterator itr = possible_tags.iterator();


		BLDictEntry bldictentry = null;
		while(itr.hasNext())
		{
			tag = (String) itr.next();
			str = sw + "::" + tag;
			bldictentry = getEntry(str);
			if(bldictentry != null)
			{
				str_vector.addElement(tag);
			}
		}

		return str_vector;
	}


	/**<p>Returns a String Vector containing all the target examples for the source word.*/
	public Vector getTgtExamplesForSW(String sw)
	{
		Vector str_vector = new Vector();
		int count = 0;
		String str = null;
		Iterator itr = possible_tags.iterator();
		Sense sense = null;
		int k,j;


		BLDictEntry bldictentry = null;
		
		
		while(itr.hasNext())
		{
			str = sw + "::" + itr.next();
			bldictentry = getEntry(str);

			if( bldictentry != null )
			{
				count = bldictentry.countSenses();
				for( k = 0; k < count; k++)
				{
					sense = bldictentry.getSense(k);

					for( j = 0; j < sense.countTgtExamples(); j++)
					{
						str_vector.add(sense.getTgtExample(j));
					}
				}

				while(bldictentry.getNextEntry() != null )
				{
					bldictentry = bldictentry.getNextEntry();
					
					count = bldictentry.countSenses();
					for( k = 0; k < count; k++)
					{
						sense = bldictentry.getSense(k);

						for( j = 0; j < sense.countTgtExamples(); j++)
						{
							str_vector.add(sense.getTgtExample(j));
						}
					}
				}
			}

		}
		return str_vector;
	}
	
	/**<p>Returns a String Vector containing all the target examples for the target word.*/
	public Vector getTgtExamplesForTW(String tw)
	{
		if(indexed == false) indexByTW();
		
		Vector str_vector = new Vector();
		int count = 0;
		int count_sense = 0;
		String str = null;
		Iterator itr = possible_tags.iterator();
		Sense sense = null;
		Vector tmpvector = null;
		int l,j;

		BLDictEntry bldictentry = null;
		
		while(itr.hasNext())
		{
			str = tw + "::" + itr.next();
			tmpvector = (Vector) tgtentries.get(str);
			
			if(tmpvector != null)
			{
				count = tmpvector.size();
				for(int i = 0; i < count; i++)
				{
					bldictentry = (BLDictEntry) tmpvector.get(i);

					if(bldictentry != null)
					{
						count_sense = bldictentry.countSenses();
						for(l = 0; l < count_sense; l++)
						{
							sense = bldictentry.getSense(l);

							if( tw.equals(sense.getTgtWord()) )
							{
								for( j = 0; j < sense.countTgtExamples(); j++)
								{
									str_vector.add(sense.getTgtExample(j));
								}
							}
						}
						
						
						while(bldictentry.getNextEntry() != null)
						{
							bldictentry = bldictentry.getNextEntry();

							count_sense = bldictentry.countSenses();
							for(l = 0; l < count_sense; l++)
							{
								sense = bldictentry.getSense(l);

								if( tw.equals(sense.getTgtWord()) )
								{
									for(j = 0; j < sense.countTgtExamples(); j++)
									{
										str_vector.add(sense.getTgtExample(j));
									}
								}
							}
						}
					}
				}
			}
		}

		return str_vector;
	}
	
	/**<p>Returns a String Vector containing all the source examples for the source word.*/
	public Vector getSrcExamplesForSW(String sw)
	{
		Vector str_vector = new Vector();
		int count = 0;
		String str = null;
		Iterator itr = possible_tags.iterator();
		Sense sense = null;
		int k,j;

		BLDictEntry bldictentry = null;
		
		while(itr.hasNext())
		{
			str = sw + "::" + itr.next();
			bldictentry = getEntry(str);

			if(bldictentry != null)
			{
				count = bldictentry.countSenses();
				for( k = 0; k < count; k++)
				{
					sense = bldictentry.getSense(k);
					sense.print(System.out);
					
					for( j = 0; j < sense.countSrcExamples(); j++)
					{
						str_vector.add(sense.getSrcExample(j));
					}
				}
				
				while(bldictentry.getNextEntry() != null)
				{
					bldictentry = bldictentry.getNextEntry();
					
					count = bldictentry.countSenses();
					for( k = 0; k < count; k++)
					{
						sense = bldictentry.getSense(k);
						sense.print(System.out);

						for( j = 0; j < sense.countSrcExamples(); j++)
						{
							str_vector.add(sense.getSrcExample(j));
						}
					}
				}
			}

		}

		return str_vector;
	}

	/**<p>Returns a String Vector containing all the source examples for the target word.*/
	public Vector getSrcExamplesForTW(String tw)
	{
		if(indexed == false) indexByTW();
		
		Vector str_vector = new Vector();
		int count = 0;
		int count_sense = 0;
		String str = null;
		Iterator itr = possible_tags.iterator();
		Sense sense = null;
		Vector tmpvector = null;
		int l,j,i;

		BLDictEntry bldictentry = null;
		
		while(itr.hasNext())
		{
			str = tw + "::" + itr.next();
			tmpvector = (Vector) tgtentries.get(str);

			if(tmpvector != null)
			{
				count = tmpvector.size();

				for(i = 0; i < count; i++)
				{
					bldictentry = (BLDictEntry) tmpvector.get(i);
					
					if(bldictentry != null)
					{
						count_sense = bldictentry.countSenses();
						for(l = 0; l < count_sense; l++)
						{
							sense = bldictentry.getSense(l);
							if( tw.equals(sense.getTgtWord()) )
							{
								for(j = 0; j < sense.countSrcExamples(); j++)
								{
									str_vector.add(sense.getSrcExample(j));
								}
							}
						}
						
						
						while(bldictentry.getNextEntry() != null)
						{
							bldictentry = bldictentry.getNextEntry();
							count_sense = bldictentry.countSenses();
							for(l = 0; l < count_sense; l++)
							{
								sense = bldictentry.getSense(l);
								if( tw.equals(sense.getTgtWord()) )
								{
									for(j = 0; j < sense.countSrcExamples(); j++)
									{
										str_vector.add(sense.getSrcExample(j));
									}
								}
							}
						}
					}
				}
			}
		}
		return str_vector;
	}

	
	/**<p>Returns the size of the srcentries(hashtable).*/
	public int countEntries()
	{
		return srcentries.size();
	}
	
	/**<p>Returns Enumeration to all the keys in the srcentries(hashtable).*/
	public Enumeration getEntryKeys()
	{
		return srcentries.keys();
	}
	
	/**<p>Returns a reference to BLDictEntry in the srcentries(hashtable) for the key passed.*/
	public BLDictEntry getEntry(String key)
	{
		return (BLDictEntry) srcentries.get(key);
	}
	
	/**<p>Adds a new BLDictEntry "entry" to the hashtable if the entry does not exist, else becomes a next 
	node to the existing list.*/
	public void addEntry(BLDictEntry entry)
	{
		String key = entry.getSrcWord() + "::" + entry.getTag();
		BLDictEntry bldentry = getEntry(key);
		if(bldentry == null)
		{
			srcentries.put(key,entry);
		}
		else
		{
			bldentry.addEntry(entry);
		}
		
		indexed = false;
	}
	
	/*public BLDictEntry removeEntry(String key, BLDictEntry bld)	// broken
	{
		if(key == null) return null;
		if(bld == null) return (BLDictEntry) srcentries.remove(key);
		
		BLDictEntry bldentry = (BLDictEntry) srcentries.get(key);
		//if(bldentry != null) return (BLDictEntry) bldentry.removeEntry(key, bld); //broken...
		
		indexed = false;
	}*/
	
	/**<p>Populates tgtentries(hashtable), each element contains vector of BLDictEntry. Should always be true
	any function working with tgtword, ex: getEntriesByTW,getTgtExamplesForTW,getSrcExamplesForTW.*/
	public void indexByTW()
	{
		tgtentries.clear();
		Enumeration enumt;
		int count = 0;
		String str = null;
		String key = null;
		Vector vector = null;
		Sense sense = null;
		int k = 0;

		BLDictEntry bldictentry = null;
		enumt = getEntryKeys();
		
		while(enumt.hasMoreElements())
		{
			key = (String) enumt.nextElement();
			bldictentry = getEntry(key);
			
			count = bldictentry.countSenses();
			for(k = 0; k < count; k++)
			{
				sense = bldictentry.getSense(k);
				str = sense.getTgtWord() + "::" + sense.getTag();
				vector = (Vector) tgtentries.get(str);
				if(vector == null)
				{
					Vector newvector = new Vector();
					newvector.add(bldictentry);
					tgtentries.put(str,newvector);	//append into hashtable
				}
				else
				{
					vector.add(bldictentry);	//append into existing vector
				}
			}
			
			
			
			
			while(bldictentry.getNextEntry() != null )
			{
				bldictentry = bldictentry.getNextEntry();	//parse to next node	

				count = bldictentry.countSenses();
				for( k = 0; k < count; k++)
				{
					sense = bldictentry.getSense(k);
					str = sense.getTgtWord() + "::" + sense.getTag();
					vector = (Vector) tgtentries.get(str);
					if(vector == null)
					{
						Vector newvector = new Vector();
						newvector.add(bldictentry);
						tgtentries.put(str,newvector);	//append into hashtable
					}
					else
					{
						vector.add(bldictentry);	//append into existing vector
					}
					
				}
			}
		}
		indexed = true;
	}
	
	/**<p>Reads the dictionary file and populates all the objects.*/
	public int read(String f /* dictionary file */) throws FileNotFoundException, IOException
	{
		BufferedReader lnReader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));
		
		String line;
		Sense sense = null;
		BLDictEntry bldictentry = new BLDictEntry();
		String splitstr[];
		String splitstr1[];
		String str = null;
		int id = 0;
		boolean verb = false;

		while((line = lnReader.readLine()) != null)
		{
			if(line.contains("***"))
			{
				if(bldictentry != null)
				{
					if(sense != null)
					{
						bldictentry.addSense(sense);
						sense = null;
					}
					addEntry(bldictentry);
				}
				
				bldictentry = new BLDictEntry();
				id = 0;
			}
			else if(line.length() > 1)
			{
				splitstr = line.split("::");
				if(splitstr[0].equals(GlobalProperties.getIntlString("SID")))
				{
					if(sense != null)
					{
						bldictentry.addSense(sense);
					}
					
					sense = new Sense();
					splitstr1 = splitstr[1].split("%");
					String str_lower = splitstr1[0].toLowerCase();
					bldictentry.setSrcWord(str_lower);	
					bldictentry.setTag(splitstr1[1]);
				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Headword")))
				{
					//do nothing
				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Lex_Cat")))
				{
					str = splitstr[1];
				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Meaning_Hindi")))
				{
					++id;
					if(splitstr.length > 2)
					{
						sense.setTgtWord(splitstr[2]);
						sense.setID(id) ;
						sense.setTag(str);
						if(possible_tags.contains(str) == false) 
						{
							possible_tags.addElement(str);
							//System.out.println(str);
						}
					}
					
					if(str.charAt(0) == 'V')
					{
						verb = true;
					}

				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Meaning_Target")))
				{
					if(splitstr.length > 1)
					{
						sense.setMeaningTarget(splitstr[1]);
					}
				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Eng_Example")))
				{
					if(splitstr.length > 1)
					{
						sense.addSrcExample(splitstr[1]);
					}
				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Trans_Nat")))
				{
					//do nothing
				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Frame_E"))  && verb == true)
				{
					if(splitstr.length > 1)
					{
						sense.setEFrame(splitstr[1]);
					}
				}
				else if(splitstr[0].equals(GlobalProperties.getIntlString("Frame_I"))  && verb == true)
				{
					if(splitstr.length > 1)
					{
						sense.setIFrame(splitstr[1]);
					}
				}
			}
		}

		return 0;
	}

	/**<p>To print the contents of the BiDict object.*/
	public int print(PrintStream ps)
	{
		int count = 0;
		//Enumeration enumt;
		Iterator itr = sort().iterator();
		BLDictEntry bldictentry = null;
		String str = null;
		
		//enumt = getEntryKeys();
		
		while(itr.hasNext())
		{
			str = (String) itr.next();
			bldictentry = getEntry(str);
			bldictentry.print(ps);
			
			while(bldictentry.getNextEntry() != null )
			{
				bldictentry = bldictentry.getNextEntry();	//parse to next node
				bldictentry.print(ps);
			}
		}
		return countEntries();
	}

	/**<p>Sorts the keys, returns a treeset.*/
	public TreeSet sort()
	{
		Set key_set = srcentries.keySet();
		TreeSet tree_set = new TreeSet(key_set);
		return tree_set;
	}
	
	public static void main(String args[])
	{
		BLDictionary bldictionary = new BLDictionary(GlobalProperties.getIntlString("English"),GlobalProperties.getIntlString("Hindi"));
		BLDictEntry bldictentry = null;
		String str = null;
		Vector generic = null;
		int count = 0;
		Sense sense = null;
		int count_sense = 0;
		int exit = 0;
		
		int i,j;
		try
		{
			i = bldictionary.read("dict.dat");
		}
		catch(IOException e)
		{
			System.out.println(GlobalProperties.getIntlString("Input_file_not_found"));
			return;
		}
		
		
		//generic = bldictionary.getEntriesByTW("eka");
		//generic = bldictionary.getSrcExamplesForTW("ke_bAre_meM");
		//count = generic.size();
		bldictionary.print(System.out);
		
		/*for(int k = 0; k < count; k++)
		{
			//bldictentry = (BLDictEntry) generic.get(k); 
			//bldictentry.print(System.out);
			str = (String) generic.get(k);
			System.out.println(str);
			/*while(bldictentry.getNextEntry() != null)
			{
				//str = (String) generic.elementAt(i);
				//System.out.println(str);
				//bldictentry = (BLDictEntry) generic.get(i);
				bldictentry = bldictentry.getNextEntry();
				bldictentry.print(System.out);
				//count_sense = bldictentry.size();
				//for(j = 0; j < count_sense; j++)
				//{
	//				str = bldictentry.getSrcWord();
					//str = sense.getTgtWord();	
//					System.out.println(str);
				//}
				
			}
		
		}*/
	}
}
