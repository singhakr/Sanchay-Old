package sanchay.resources.bidict;

import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;

/**Documentation:BLDictEntry

<p>ABSTRACT:

<p>A dictionary can be seen as a list of entries. There is one entry for each
word. And each entry (or word) contains its tag, its meaning in the target language,
the different senses of the the word, example sentences, etc. This class represents
one such dictionary entry.

<p>DESCRIPTION:

<p>This class was written as part of an API for dictionaries which are used heavily
in natural language processing. It can also be useful for tasks like spell checking.
Currently, the implementation is restricted to the Shabdanjali English-Hindi dictionary,
which is based on the translex gram format, but it can be very easily adapted for
other dictionaries and languages.

<p>The BiDictEntry class was meant to be aggregated inside the BiDict class, though it
may also be used on its own for creating some other classes that you require.

	<p>SL (Source language): English
	<p>TL (Target language): Hindi or some other Indian language

<p>Note that the attributes are not meant to be used directly. You should use them through
the public methods. We have listed them above only to show what is contained in the class.


*/


public class BLDictEntry
{
	/**<p>The SL word*/
	protected String srcword;
	/**<p>The 'tag', or the grammatical category of the word*/
	protected String tag;
	/**<p>The vector of Sense objects (different senses of the word)*/
	protected Vector senses; // Senses
	/**<p>A reference pointing next entry with the same srcword.*/
	BLDictEntry prev;
	/**<p>A reference pointing previous entry.*/
	BLDictEntry next;

	/**<p>Default Constructor,creates a new senses vector, also assigns the references to NULL.*/
	public BLDictEntry()	//Default Constructor
	{
		senses = new Vector();
		prev = null;
		next = null;
	}
	
	/**<p>Paramatized constructor takes up source word and its tag, also assigns the references to NULL.*/
	public BLDictEntry(String sw /* source word */, String tg /* tag */)
	{
		srcword = sw;
		tag = tg;
		
		senses = new Vector();
		prev = null;
		next = null;
	}
	
	/**<p>Returns the srcword for the BLDictEntry object as String.*/
	public String getSrcWord() 
	{
		return srcword;
	}
	
	/**<p>Sets the srcword for the BLDictEntry object.*/
	public void setSrcWord(String sw) 
	{
		srcword = sw;
	}
	
	/**<p>Returns the grammatical category for the srcword as String.*/
	public String getTag() 
	{
		return tag;
	}
	
	/**<p>Sets the grammatical category for the srcword.*/
	public void setTag(String tg) 
	{
		tag = tg;
	}
	
	/**<p>Returns the size of the senses vector i.e., the no. of senses present in this BLDictEntry.*/
	public int countSenses()
	{
		return senses.size();
	}
	
	/**<p>Returns the reference to the Sense object corresponding to the index passed as argument.*/
	public Sense getSense(int num)           
	{
		if(countSenses() > 0)
			return (Sense) senses.get(num);
			
		else
			return null;

	}

	/**<p>Adds the sense to the senses vector and returns its index in the vector.*/
	public int addSense(Sense s) 
	{
		senses.add(s);
		return senses.size();
	}

	/**<p>Removes the sense from the senses vector by its index and returns its reference.*/
	public Sense removeSense(int num) 
	{
		return (Sense) senses.remove(num);
	}
	
	/**<p>Returns the "prev" reference(BLDictEntry).*/
	public BLDictEntry getPrevEntry()
	{
		return prev;
	}
	
	/**<p>Returns the "next" reference(BLDictEntry).*/
	public BLDictEntry getNextEntry()
	{
		return next;
	}
	
	/**<p>Sets the "next" reference(BLDictEntry).*/
	public void setNext(BLDictEntry bldict)
	{
		next = bldict;
	}

	/**<p>Sets the "prev" reference(BLDictEntry).*/
	public void setPrev(BLDictEntry bldict)
	{
		prev = bldict;
	}
	
	/**<p>Creates a linked list,adds the BLDictEntry object as the last node.*/
	public void addEntry(BLDictEntry bldictentry)
	{
		BLDictEntry bld = this;
		
		bldictentry.setNext(null);
		while(bld.getNextEntry() != null)
		{
			bld = bld.getNextEntry();
		}
		bld.setNext(bldictentry);
		//bldictentry.setPrev(bld);
	}
	
	/**<p>Returns the no. of BLDictEntry object present in the linked list.*/
	public int countEntries()
	{
		BLDictEntry bld = this;
		int c = 1;
		
		while(bld.getPrevEntry() != null)
		{
			bld = bld.getPrevEntry();
		}
		
		while(bld.getNextEntry() != null)
		{
			bld = bld.getNextEntry();
			c++;
		}
		
		return c;
	}
	
	
	/**<p>To print the contents of the BiDictEntry object.*/
	public int print(PrintStream ps)
	{
		int count = 0;
		
		ps.println("**********************************");
		ps.print(GlobalProperties.getIntlString("SID::"));
		ps.print(srcword);
		ps.print("%");
		ps.println(tag);
		
		count = countSenses();
		Sense sense = null;
		for(int i = 0; i < count; i++)
		{
			sense = getSense(i);
			ps.println(GlobalProperties.getIntlString("Headword::") + srcword);
			sense.print(ps);
		}
		
		return count;
	}
}
