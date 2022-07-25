package sanchay.filters;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import sanchay.GlobalProperties;

/**
*/
public class QuantityFilter
{
	private String language; // en/hn
	private String language_file;
	private String xmltag;
	private String xmlvalattr;
	
	private long major_factor;

	private Hashtable specialWords;
	private Hashtable majorWords;
	private Hashtable minorWords;
	
	private Hashtable revSpecialWords;
	private Hashtable revMajorWords;
	private Hashtable revMinorWords;
	
	public QuantityFilter(String lfile) throws FileNotFoundException, IOException
	{
		xmltag = "nlp_number";
		xmlvalattr = "val";
		major_factor = 1000;
		
		specialWords = new Hashtable(5);
		majorWords = new Hashtable(6);
		minorWords = new Hashtable(19);
		
		revSpecialWords = new Hashtable(5);
		revMajorWords = new Hashtable(6);
		revMinorWords = new Hashtable(19);
		
		configure(lfile);
	}
	
	public String getLanguage()
	{
		return language;
	}
	
	public void setLanguage(String lang)
	{
		language = lang;
	}

	public String getXMLTag()
	{
		return xmltag;
	}

	public void setXMLTag(String t)
	{
		xmltag = t;
	}

	public String getXMLValAttr()
	{
		return xmlvalattr;
	}

	public void setXMLValAttr(String t)
	{
		xmlvalattr = t;
	}
	
	public void clear()
	{
		specialWords.clear();
		majorWords.clear();
		minorWords.clear();

		revSpecialWords.clear();
		revMajorWords.clear();
		revMinorWords.clear();
	}
	
	public void configure(String lfile) throws FileNotFoundException, IOException
	{
		if(lfile != null && lfile != "")
			language_file = lfile;
			
		clear();
		
        BufferedReader lnReader = new BufferedReader(
            	new InputStreamReader(new FileInputStream(language_file), GlobalProperties.getIntlString("UTF-8")));
		
		String line;
		String splitstr[];
		Hashtable hashref = null;
		Hashtable revHashref = null;
		
		String REGEX = "\t";
		Pattern p = Pattern.compile(REGEX);
		
		while((line = lnReader.readLine()) != null)
		{
			Matcher m = p.matcher(line);
			
			splitstr = p.split(line);

			if(m.find())
			{
				if(splitstr[0] != null && splitstr[0].startsWith("#Language:"))
				{
					language = splitstr[1];
				}
				else if(splitstr[0] != null && splitstr[0].startsWith("XMLTag"))
				{
					xmltag = splitstr[1];
				}
				else if(splitstr[0] != null && splitstr[0].startsWith("XMLValAttr"))
				{
					xmlvalattr = splitstr[1];
				}
				else if(splitstr[0] != null && splitstr[0].startsWith("Major Factor"))
				{
					major_factor = Long.parseLong(splitstr[1]);
				}
				else if(splitstr[0] != null && splitstr[1] != null && !splitstr[0].startsWith("#"))
				{
						if(hashref == specialWords)
					{
						hashref.put(splitstr[0], splitstr[1]);
						revHashref.put(splitstr[1], splitstr[0]);
					}
					else
					{
						long ln = Long.parseLong(splitstr[0]);
						Long lnum = new Long(ln);
						hashref.put(lnum, splitstr[1]);
						revHashref.put(splitstr[1], lnum);
					}
				}
			}
			else
			{
				if(line.startsWith("#"))
				{
					if(line.startsWith("#Special"))
					{
						hashref = specialWords;
						revHashref = revSpecialWords;
					}
					else if(line.startsWith("#Major"))
					{
						hashref = majorWords;
						revHashref = revMajorWords;
					}
					else if(line.startsWith("#Minor"))
					{
						hashref = minorWords;
						revHashref = revMinorWords;
					}
				}
			}
		}
	}

	public void PrintConfiguration(PrintStream ps)
	{
		Hashtable href = null;

		href = specialWords;
		for(Enumeration e = href.elements() ; e.hasMoreElements() ;)
			ps.println("#Special Words:\n" + (String) e.nextElement() + "\t" + href.get(e.nextElement()) + "\n");

		href = majorWords;
		for(Enumeration e = href.elements() ; e.hasMoreElements() ;)
			ps.println("#Major Words:\n" + (String) e.nextElement() + "\t" + href.get(e.nextElement()) + "\n");

		href = minorWords;
		for(Enumeration e = href.elements() ; e.hasMoreElements() ;)
			ps.println("#Minor Words:\n" + (String) e.nextElement() + "\t" + href.get(e.nextElement()) + "\n");

		href = revSpecialWords;
		for(Enumeration e = href.elements() ; e.hasMoreElements() ;)
			ps.println("#Reverse Special Words:\n" + (String) e.nextElement() + "\t" + href.get(e.nextElement()) + "\n");

		href = revMajorWords;
		for(Enumeration e = href.elements() ; e.hasMoreElements() ;)
			ps.println("#Reverse Major Words:\n" + (String) e.nextElement() + "\t" + href.get(e.nextElement()) + "\n");

		href = revMinorWords;
		for(Enumeration e = href.elements() ; e.hasMoreElements() ;)
			ps.println("#Reverse Minor Words:\n" + (String) e.nextElement() + "\t" + href.get(e.nextElement()) + "\n");
	}

	public String toWords(long number)
	{
		return null;
	}
	
	protected Unit getUnitFromWord(String wd) throws NumberFormatException
	{
		return null;
	}
	
	public long toInteger(String words) throws NumberFormatException
	{
		return 0;
	}

	public String markQuantities(String srcsen, QuantityFilter tgtfilter)
	{
		srcsen = srcsen.trim();

		String[] wds = srcsen.split("[ ]");

		Vector wdsvec = new Vector(wds.length);
		Vector mweflags = new Vector(wds.length);
		Vector mwevals = new Vector(wds.length);
		
		String markedsen = "";

		if(wds.length <= 0) { return null; }

		for(int i = 0; i < wds.length; i++)
		{
			wdsvec.add(wds[i]);
			mweflags.add("false");
			mwevals.add("NaN");
		}

		//Finding the number. Starting with the whole sentence and going left.
		int j = 0;
		String token = srcsen;
		boolean nan = true;
		long mwe = -1;

		while(j < wdsvec.size()) // - 1)
		{
			nan = true;
			int i = wdsvec.size();

			while(j <= wdsvec.size() && nan == true && i >= 0 && j < i)
			{
				token = "";

				for(int k = j; k < i; k++)
					token += (String) wdsvec.get(k) + " ";

				i--;

				token = token.trim();

				try
				{
					mwe = toInteger(token);
					nan = false;
				}
				catch(NumberFormatException e)
				{
					//throw e; // Ignore
				}
				
			}

			// Found a number expression
			if(nan == false)
			{
				for(int k = j; k <= i; k++)
				{
					wdsvec.remove(j);
					mweflags.remove(j);
					mwevals.remove(j);
				}

				wdsvec.insertElementAt(token, j);
				mweflags.insertElementAt("true", j);
				String mwestr = "" + mwe;
				mwevals.insertElementAt(mwestr, j);

				if(j == i)
					j++;
				else
					j = i;
			}
			else
			{
				j++;
			}
		}

		for(int i = 0; i < wdsvec.size(); i++)
		{
			token = (String) wdsvec.get(i);
			String ismwe = (String) mweflags.get(i);
			String val = (String) mwevals.get(i);

			if(ismwe == "true")
			{
				if(tgtfilter != null)
				{
					val = tgtfilter.toWords(Long.parseLong(val));
				}

				markedsen += " <" + getXMLTag() + " " + getXMLValAttr() + "=\"" + val + "\">" + token + "</" + getXMLTag() + ">";
			}
			else
			{
				markedsen += " " + token;
			}
		}

		markedsen = markedsen.trim();

		return markedsen;
	}

	public void markBatch(String f, PrintStream ps) throws FileNotFoundException, IOException
	{
        BufferedReader lnReader = new BufferedReader(
            	new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));
		
		String line;
		
		while((line = lnReader.readLine()) != null)
		{
//			if(line != "")
//				ps.println(markNumbers(line, null));
		}
	}

	public static void main(String[] args)
	{
		QuantityFilter f = null;
		
		try
		{
			f = new QuantityFilter(GlobalProperties.resolveRelativePath("data/number_words.en"));
		}
		catch(IOException e) 
		{
			System.out.println(GlobalProperties.getIntlString("IOException!"));
		}
		
		System.out.println(0 + " : " + f.toWords(0));
		System.out.println(1 + " : " + f.toWords(1));
		System.out.println(16 + " : " + f.toWords(16));
		System.out.println(100 + " : " + f.toWords(100));
		System.out.println(118 + " : " + f.toWords(118));
		System.out.println(200 + " : " + f.toWords(200));
		System.out.println(219 + " : " + f.toWords(219));
		System.out.println(800 + " : " + f.toWords(800));
		System.out.println(801 + " : " + f.toWords(801));
		System.out.println(1316 + " : " + f.toWords(1316));
		System.out.println(1000000 + " : " + f.toWords(1000000));
		System.out.println(2000000 + " : " + f.toWords(2000000));
		System.out.println(3000200 + " : " + f.toWords(3000200));
		System.out.println(700000 + " : " + f.toWords(700000));
		System.out.println(9000000 + " : " + f.toWords(9000000));
		System.out.println(123456789 + " : " + f.toWords(123456789));
		System.out.println(-45 + " : " + f.toWords(-45));
		
		/*
		*** zero
		*** one
		*** sixteen
		*** one hundred
		*** one hundred eighteen
		*** two hundred
		*** two hundred nineteen
		*** eight hundred
		*** eight hundred one
		*** one thousand three hundred sixteen
		*** one million
		*** two million
		*** three million two hundred
		*** seven hundred thousand
		*** nine million
		*** one hundred twenty three million four hundred fifty six thousand seven hundred eighty nine
		*** minus fourty five
		*/
		
		System.out.println(0 + " : " + f.toInteger("zero"));
		System.out.println(1 + " : " + f.toInteger("one"));
		System.out.println(16 + " : " + f.toInteger("sixteen"));
		System.out.println(100 + " : " + f.toInteger("one hundred"));
		System.out.println(118 + " : " + f.toInteger("one hundred eighteen"));
		System.out.println(200 + " : " + f.toInteger("two hundred"));
		System.out.println(219 + " : " + f.toInteger("two hundred nineteen"));
		System.out.println(800 + " : " + f.toInteger("eight hundred"));
		System.out.println(801 + " : " + f.toInteger("eight hundred one"));
		System.out.println(1316 + " : " + f.toInteger("one thousand three hundred sixteen"));
		System.out.println(1000000 + " : " + f.toInteger("one million"));
		System.out.println(2000000 + " : " + f.toInteger("two million"));
		System.out.println(3000200 + " : " + f.toInteger("three million two hundred"));
		System.out.println(700000 + " : " + f.toInteger("seven hundred thousand"));
		System.out.println(9000000 + " : " + f.toInteger("nine million"));
		System.out.println(123456789 + " : " + f.toInteger("one hundred twenty three million four hundred fifty six thousand seven hundred eighty nine"));
		System.out.println(-45 + " : " + f.toInteger("minus forty five"));
	
		System.out.println(4375 + " : " + f.toInteger("four thousand three hundred and seventy five"));
		System.out.println(4075 + " : " + f.toInteger("four thousand and seventy five"));
		System.out.println(4005 + " : " + f.toInteger("four thousand and five"));
		System.out.println(-4005 + " : " + f.toInteger("minus four thousand and five"));
	}
}
