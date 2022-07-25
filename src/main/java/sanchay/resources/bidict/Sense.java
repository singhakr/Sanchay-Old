package sanchay.resources.bidict;

import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;

/** Documentation: Sense.java

<p>ABSTRACT:
<p>A word in a natural language can have more than one senses. The dictionary
entry for that word includes at least some of these senses. This class represents
one sense of a word.

<p>DESCRIPTION:

<p><p>This class was written as part of an API for dictionaries which are used heavily
in natural language processing. It can also be useful for tasks like spell checking.
Currently, the implementation is restricted to the Shabdanjali English-Hindi dictionary,
which is based on the translex gram format, but it can be very easily adapted for
other dictionaries and languages.

<p><p>The Sense class was meant to be aggregated inside the BiDictEntry class, though it
may also be used on its own for things like word sense disambiguation. Or you might
make it a part of some other classes that you require.

	<p>SL (Source language): English
	<p>TL (Target language): Hindi or some other Indian language

<p><p>Note that the attributes are not meant to be used directly. You should use them through
the public methods. We have listed them above only to show what is contained in the class.
	

*/

public class Sense
{
	/**<p>Specific sense in the TL(Hindi)*/
	protected String tgtword;
	/**<p>The 'tag' or the grammatical category*/
	protected String tag;
	/**<p>The sense id*/
	protected int id;
	/**<p>meaning target*/
	protected String mngtgt;
	/**<p>Information pertaining only for verbs*/
	protected String eframe; // only for verbs
	/**<p>Information pertaining only for verbs*/
	protected String iframe; // only for verbs
	/**<p>The array of example sentences in SL*/
	protected Vector src_examples;
	/**<p>The array of example sentences in TL*/
	protected Vector tgt_examples;

	/**<p>Default Constructor,creates the two example vectors.*/
	public Sense()		//Default Constructor	
	{
		src_examples = new Vector();
		tgt_examples = new Vector();
	}
	
	/** <p>Paramatized Constructor,takes in target word, its id and the tag.Also creates the example vectors.*/
	public Sense(String tw /* target word */, int i, String tg /* tag */)
	{
		tgtword = tw;
		id = i;
		tag = tg;
		
		src_examples = new Vector();
		tgt_examples = new Vector();
	}
	
	/**<p>Returns the target word for this sense object as a string.*/
	public String getTgtWord() 
	{
		return tgtword;
	}
	
	/**<p> Set the tgtword.*/
	public void setTgtWord(String tw) 
	{
		tgtword = tw;
	}
	
	/** <p>Get the id for the sense, retuns integer.*/
	public int getID() 
	{
		return id;
	}
	
	/** <p> Sets the id for the sense.*/
	public void setID(int i) 
	{
		id = i;
	}
	
	public String getMeaningTarget() 
	{
		return mngtgt;
	}
	
	public void setMeaningTarget(String w) 
	{
		mngtgt = w;
	}
	
	public String getEFrame() 
	{
		return eframe;
	}
	
	public void setEFrame(String f) 
	{
		eframe = f;
	}
	
	public String getIFrame() 
	{
		return iframe;
	}
	
	public void setIFrame(String f) 
	{
		iframe = f;
	}
	
	/** <p>Get the grammatical category for the sense as string.*/
	public String getTag() 
	{
		return tag;
	}
	
	/** <p>Set the grammatical category for the sense.*/
	public void setTag(String tg) 
	{
		tag = tg;
	}
	
	/** <p> To get the count of examples in the vector.Use the count to retrieve examples(specific or all).*/
	public int countSrcExamples()
	{
		return src_examples.size();
	}
	
	/** <p>To get an example sentence in SL. It returns a strings.*/
	public String getSrcExample(int num) 
	{
		return (String) src_examples.get(num);
	}

	/**<p>To add user defined example sentence(SL) for a sense.*/
	public int addSrcExample(String se) 
	{
		src_examples.add(se);
		return src_examples.size();
	}

	/** <p> To remove example sentence(SL) for a sense specified by an index.*/
	public String removeSrcExample(int num) 
	{
		return (String) src_examples.remove(num);
	}
	
	/**<p>To get the count of examples in the vector.Use the count to retrieve examples(specific or all).*/
	public int countTgtExamples()
	{
		return tgt_examples.size();
	}
	
	/**<p>To get an example sentence in TL. It returns a string.*/
	public String getTgtExample(int num) 
	{
		return (String) tgt_examples.get(num);
	}

	/** <p>To add user defined example sentence(TL) for a sense.*/
	public int addTgtExample(String te) 
	{
		tgt_examples.add(te);
		return tgt_examples.size();
	}

	/**<p>To remove example sentence(TL) for a sense specified by an index.*/
	public String removeTgtExample(int num) 
	{
		return (String) tgt_examples.remove(num);
	}
	
	/**<p>To print the contents of the Sense object.*/
	public int print(PrintStream ps)
	{
		int count = 0;
		boolean verb = false;
		String str = null;
		
		ps.print(GlobalProperties.getIntlString("Meaning_Hindi::"));
		ps.print(id);
		ps.print("::");
		ps.print(tgtword);
		ps.print("::");
		ps.println(tag);
		
		ps.println(GlobalProperties.getIntlString("Lex_Cat::") + tag);
		
		if(tag != null && tag.charAt(0) == 'V')
		{
			verb = true;
		}
		
		ps.println(GlobalProperties.getIntlString("Meaning_Target::") + mngtgt);

		count = countSrcExamples();
		
		for(int i = 0; i<count; i++)
		{
			str = getSrcExample(i);
			ps.print(GlobalProperties.getIntlString("Eng_Example::"));
			ps.println(str);
		}
		
		if(count == 0) ps.println(GlobalProperties.getIntlString("Eng_Example::"));
		
		count = countTgtExamples();
		
		for(int i = 0; i<count; i++)
		{
			str = getTgtExample(i); 
			ps.print(GlobalProperties.getIntlString("Trans_Nat::"));
			ps.println(str);
		}
		
		if(count == 0) ps.println(GlobalProperties.getIntlString("Trans_Nat::"));
		
		if(verb == true)
		{
			ps.print(GlobalProperties.getIntlString("Frame_E::"));
			if(eframe != null)
				ps.println(eframe);
			else
				ps.println("");
			
			ps.print(GlobalProperties.getIntlString("Frame_I::"));
			if(iframe != null)
				ps.println(iframe);
			else
				ps.println("");
		}
		
		ps.println("");
		
		return 0;
	}
}
