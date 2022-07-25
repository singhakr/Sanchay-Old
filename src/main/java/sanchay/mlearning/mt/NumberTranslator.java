package sanchay.mlearning.mt;

import java.io.*;

import sanchay.GlobalProperties;
import sanchay.filters.*;
import sanchay.gui.common.SanchayLanguages;

public class NumberTranslator
{
    private NumberFilter srcFilter;
	private NumberFilter tgtFilter;

    public NumberTranslator(String srcLangEnc, String srcCharset, String tgtLangEnc, String tgtCharset)
    {
        super();

        String srcLangCode = SanchayLanguages.getLanguageCodeFromLECode(srcLangEnc);
        String tgtLangCode = SanchayLanguages.getLanguageCodeFromLECode(tgtLangEnc);

        File mkvpFileSrc = new File(GlobalProperties.getHomeDirectory() + "/data/number-filter/number-words-" + srcLangCode + ".txt");
        File mkvpFileTgt = new File(GlobalProperties.getHomeDirectory() + "/data/number-filter/number-words-" + tgtLangCode + ".txt");

		srcFilter = new NumberFilter(mkvpFileSrc, "UTF-8");
		tgtFilter = new NumberFilter(mkvpFileTgt, "UTF-8");
    }
	
	public NumberTranslator(String slfile, String tlfile) throws FileNotFoundException, IOException
	{
		srcFilter = new NumberFilter(slfile, "UTF-8");
		tgtFilter = new NumberFilter(tlfile, "UTF-8");
	}
	
	public NumberFilter getSrcFilter()
	{
		return srcFilter;
	}
	
	public NumberFilter getTgtFilter()
	{
		return tgtFilter;
	}
	
	public void clear()
	{
		srcFilter.clear();
		tgtFilter.clear();
	}
	
	public void configure(String slfile, String tlfile) throws FileNotFoundException, IOException
	{
		clear();
		
		srcFilter.configure(slfile, GlobalProperties.getIntlString("UTF-8"));
		tgtFilter.configure(tlfile, GlobalProperties.getIntlString("UTF-8"));
	}

	public String translate(String srcnum)
	{
		long sn = srcFilter.toInteger(srcnum);
		String tgtnum = tgtFilter.toWords(sn);
		
		return tgtnum;
//		return null;
	}

	public String markTranslatedNumbers(String srcsen)
	{
		String markedsen = srcFilter.markNumbers(srcsen, tgtFilter);

		return markedsen;
	}

	public void markBatch(String f, PrintStream ps) throws FileNotFoundException, IOException
	{
		BufferedReader lnReader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));
		
		String line;
		
		while((line = lnReader.readLine()) != null)
		{
			if(line.equals("") == false)
				ps.println(markTranslatedNumbers(line));
		}
	}

    public String translateNumberWord(String word)
    {
        String number = srcFilter.getNumberWordString(word);
        
        if(number != null)
            return "" + tgtFilter.getNumberWordString(Long.parseLong(number));

        return null;
    }

	public static void main(String[] args)
	{
		NumberTranslator t = null;
		
		try
		{
			t = new NumberTranslator(GlobalProperties.getHomeDirectory() + "/data/number-filter/number-words-eng.txt",
                    GlobalProperties.getHomeDirectory() + "/data/number-filter/number-words-hin.txt");
		}
		catch(IOException e) 
		{
			System.out.println("IOException!");
		}
		
		System.out.println(0 + " : " + t.translate("zero"));
		System.out.println(1 + " : " + t.translate("one"));
		System.out.println(16 + " : " + t.translate("sixteen"));
		System.out.println(100 + " : " + t.translate("one hundred"));
		System.out.println(118 + " : " + t.translate("one hundred eighteen"));
		System.out.println(200 + " : " + t.translate("two hundred"));
		System.out.println(219 + " : " + t.translate("two hundred nineteen"));
		System.out.println(800 + " : " + t.translate("eight hundred"));
		System.out.println(801 + " : " + t.translate("eight hundred one"));
		System.out.println(1316 + " : " + t.translate("one thousand three hundred sixteen"));
		System.out.println(1000000 + " : " + t.translate("one million"));
		System.out.println(2000000 + " : " + t.translate("two million"));
		System.out.println(3000200 + " : " + t.translate("three million two hundred"));
		System.out.println(700000 + " : " + t.translate("seven hundred thousand"));
		System.out.println(9000000 + " : " + t.translate("nine million"));
		System.out.println(123456789 + " : " + t.translate("one hundred twenty three million four hundred fifty six thousand seven hundred eighty nine"));
		System.out.println(-45 + " : " + t.translate("minus forty five"));
	
		System.out.println(4375 + " : " + t.translate("four thousand three hundred and seventy five"));
		System.out.println(4075 + " : " + t.translate("four thousand and seventy five"));
		System.out.println(4005 + " : " + t.translate("four thousand and five"));
		System.out.println(-4005 + " : " + t.translate("minus four thousand and five"));
		
		try
		{
			t.markBatch(GlobalProperties.getHomeDirectory() + "/data/number-filter/num_sentences.en", System.out);
		}
		catch(IOException e)
		{
			System.out.println("IOException!");
		}
	}
}
