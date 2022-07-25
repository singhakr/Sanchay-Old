package sanchay.filters;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.gui.common.SanchayLanguages;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.MultiKeyValueProperties;

/**
 */
public class NumberFilter {
    
    private MultiKeyValueProperties numberMultiKVP;
    private MultiKeyValueProperties revNumberMultiKVP;

    //KeyValueProperties specialW = new KeyValueProperties();
    public NumberFilter()
    {
        super();
    }

    public NumberFilter(String langEnc, String charset)
    {
        super();

        String langCode = SanchayLanguages.getLanguageCodeFromLECode(langEnc);

        File mkvpFile = new File(GlobalProperties.resolveRelativePath("data/number-filter/number-words-" + langCode + ".txt"));

        configure(mkvpFile.getAbsolutePath(), charset);
    }

    public NumberFilter(File mkvpFile, String charset)
    {
        super();

        configure(mkvpFile.getAbsolutePath(), charset);
    }

    public MultiKeyValueProperties getMultiKeyValueProperties()
    {
        return numberMultiKVP;
    }

    public void setMultiKeyValueProperties(MultiKeyValueProperties pm)
    {
        numberMultiKVP = pm;
    }

    public void clear()
    {
        numberMultiKVP.clear();
        revNumberMultiKVP.clear();
    }

    public void configure(String mkvpFile, String charset)
    {
        try
        {
            numberMultiKVP = new MultiKeyValueProperties(mkvpFile, charset);
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        revNumberMultiKVP = new MultiKeyValueProperties();

        Iterator itr = numberMultiKVP.getPropertiesKeys();

        while (itr.hasNext())
        {
            String kvpKey = (String) itr.next();
            KeyValueProperties kvp = (KeyValueProperties) numberMultiKVP.getPropertiesValue(kvpKey);

            KeyValueProperties revkvp = kvp.getReverse();

            revNumberMultiKVP.addProperties(kvpKey, revkvp);
        }
    }

    protected String convertLessThanOneThousand(long number)
    {
        KeyValueProperties minorW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("MinorWords");
        KeyValueProperties specialW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("SpecialWords");

        String soFar;
	
		soFar = (String) minorW.getPropertyValue("" + new Long(number % 100L));
		number /= 100L;
	
		if(soFar == null)
		    soFar = "";
	
		if (number == 0)
		    return soFar;
	
		String mw = (String) minorW.getPropertyValue("" + new Long(number));
	
		if(mw == null)
		    mw = "";
		else
		    mw = mw + " ";
	
		if(soFar.equals(""))
		    return mw + (String) specialW.getPropertyValue("100");
		else
		    return mw + " " + (String) specialW.getPropertyValue("100") + " " + soFar;
    }
	
    public String toWords(long number)
    {
        KeyValueProperties specialW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("SpecialWords");
        KeyValueProperties majorW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("MajorWords");
        KeyValueProperties numProps = (KeyValueProperties) numberMultiKVP.getPropertiesValue("NumberProperties");

        // special case
        if (number == 0)
        {
            return specialW.getPropertyValue("0");
        }

        String prefix = "";

        if (number < 0) {
            number = -1 * number;
            prefix = specialW.getPropertyValue("-") + " ";
        }

        String soFar = "";
        long place = 0;
        long factor = Long.parseLong(numProps.getPropertyValue("Major Factor"));

        do
        {
            if(place == 0)
                factor = 1000;
            else if(place > 0)
                factor = Long.parseLong(numProps.getPropertyValue("Major Factor"));


            long n = number  % factor;

            if (n != 0)
            {
                    String s = convertLessThanOneThousand(n);
                    String mn = "";

                    if(place > 0)
                    {
                        mn = majorW.getPropertyValue("" + (1000 * ((long) Math.pow((double) factor, (double) (place - 1)))));
                    }

                    soFar = s + " " + mn + " " + soFar;
            }

            place++;
            number /= factor;
        }
        while (number > 0);

        String ret = prefix + soFar;

        ret = ret.trim();

        return ret;
    }

    public String getNumberWordString(long number)
    {
        KeyValueProperties specialW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("SpecialWords");
        KeyValueProperties majorW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("MajorWords");
        KeyValueProperties minorW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("MinorWords");

        String nwd = null;

        if(number == 0 || number == 100)
        {
            nwd = specialW.getPropertyValue("" + number);
            return nwd;
        }

        nwd = majorW.getPropertyValue("" + number);

        if(nwd != null)
            return nwd;

        nwd = minorW.getPropertyValue("" + number);

        if(nwd != null)
            return nwd;

        return nwd;
    }

    public String getNumberWordString(String wd)
    {
        KeyValueProperties revSpecialW = (KeyValueProperties) revNumberMultiKVP.getPropertiesValue("SpecialWords");
        KeyValueProperties revMajorW = (KeyValueProperties) revNumberMultiKVP.getPropertiesValue("MajorWords");
        KeyValueProperties revMinorW = (KeyValueProperties) revNumberMultiKVP.getPropertiesValue("MinorWords");

        String nwd = revSpecialW.getPropertyValue(wd);

        if(nwd != null && (nwd.equals("0") || nwd.equals("100")))
            return nwd;

        nwd = revMajorW.getPropertyValue(wd);

        if(nwd != null)
            return nwd;

        nwd = revMinorW.getPropertyValue(wd);

        if(nwd != null)
            return nwd;

        return null;
    }

    protected long getNumberFromWord(String wd) throws NumberFormatException
    {
        KeyValueProperties specialW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("SpecialWords");
        KeyValueProperties revMajorW = (KeyValueProperties) revNumberMultiKVP.getPropertiesValue("MajorWords");
        KeyValueProperties revMinorW = (KeyValueProperties) revNumberMultiKVP.getPropertiesValue("MinorWords");

        String specialWords_hundred = (String) specialW.getPropertyValue("100");
        String specialWords_zero = (String) specialW.getPropertyValue("0");

        if (wd.equals(specialWords_hundred) == true)
        {
            return 100;
        }

        if (wd.equals(specialWords_zero) == true)
        {
            return 0;
        }

//                Long n = Long.parseLong(revMajorW.getPropertyValue(wd)) ;



        String str1 = revMajorW.getPropertyValue(wd);

        String str2 = revMinorW.getPropertyValue(wd);

//                System.out.println("revMajorW (wd) = "+str1);
//                System.out.println("revMinorW (wd) = "+str2);

        if (str1 != null)
        {
            return Long.parseLong(str1);
        }

//		n = Long.parseLong(revMinorW.getPropertyValue(wd));

        if (str2 != null)
        {
            return (Long.parseLong(str2));
        }

        throw new NumberFormatException("NaN: " + wd);

    //       return -1;
    }

    protected long formNumber(long n[])
    {
        int maxMajorWordIndex = findMaxMajorWordIndex(n);

        if (maxMajorWordIndex == -1)
        {
            return formNumberLessThanThousand(n);
        } else
        {
            long ret = -1;
            long left[] = new long[maxMajorWordIndex];
            long right[] = new long[n.length - maxMajorWordIndex - 1];

            for (int i = 0; i < maxMajorWordIndex; i++)
            {
                left[i] = n[i];
            }

            for (int i = 1; i <= right.length; i++)
            {
                right[i - 1] = n[maxMajorWordIndex + i];
            }

            long l = formNumber(left);
            long r = formNumber(right);

            ret = (l * n[maxMajorWordIndex]) + r;

            return ret;
        }
    }

    protected long formNumberLessThanThousand(long n[])
    {
        if (n.length == 1)
        {
            return n[0];
        }

        long ret = 0;
        long prev = 0;

        for (int i = 0; i < n.length; i++)
        {
            if (i == 0)
            {
                ret = n[i];
            } else if (n[i] < prev)
            {
                ret += n[i];
            } else if (n[i] > prev)
            {
                ret *= n[i];
            }

            prev = n[i];
        }

        return ret;
    }

    // Return index
    protected int findMaxMajorWordIndex(long n[])
    {
        long max = -1;
        int maxIndex = -1;
        String num = new String();
        KeyValueProperties majorWords = (KeyValueProperties) numberMultiKVP.getPropertiesValue("MajorWords");

        for (int i = 0; i < n.length; i++)
        {
            num = (new Long(n[i])).toString();
            if (majorWords.getPropertyValue(num) != null)
            {
                if (max < n[i])
                {
                    max = n[i];
                    maxIndex = i;
                }
            }
        }

        return maxIndex;
    }

    public long toInteger(String words) throws NumberFormatException
    {
        KeyValueProperties specialW = (KeyValueProperties) numberMultiKVP.getPropertiesValue("SpecialWords");

        if (words == null || words.equals(""))
        {
            throw new NumberFormatException(GlobalProperties.getIntlString("NaN:_empty_string."));
        }

        String specialWords_ampersand = (String) specialW.getPropertyValue("&");
        String specialWords_plus = (String) specialW.getPropertyValue("+");
        String specialWords_minus = (String) specialW.getPropertyValue("-");


//                System.out.println("ampersand = "+specialWords_ampersand);
//                System.out.println("plus = "+specialWords_plus);
//                System.out.println("minus = "+specialWords_minus);

        if (words.equals(specialWords_ampersand) || words.equals(specialWords_plus) || words.equals(specialWords_minus))
        {
            throw new NumberFormatException("NaN: " + words);
        }

        String[] wds = words.split("[ ]");


//                for (int  i=0 ; i<wds.length; i++ )
//                {
//                    System.out.println("First wds[i] :"+wds[i]);
//                }


        //

        long ret = 0;
        long sign = 1;
        long n = -1;

        Vector longs = new Vector(wds.length);
        //              System.out.println("wds length = "+wds.length);
        //              System.out.println("longs length = "+longs.size());


        for (int i = 0; i < wds.length; i++)
        {
            wds[i] = wds[i].trim();

            //                   System.out.println("Second wds[i] :"+wds[i]);

            if (i == 0)
            {
                if (wds[i].equals(specialWords_minus) == true)
                {
                    sign = -1;
                    continue;
                } else if (wds[i].equals(specialWords_plus) == true)
                {
                    sign = 1;
                    continue;
                }
            }

            if (wds[i].equals(specialWords_ampersand) != true)
            {
                try
                {
//                                    System.out.println(wds[i]);
                    n = getNumberFromWord(wds[i]);
//                                    System.out.println(n);
                    longs.add(new Long(n));
                } catch (NumberFormatException e)
                {
                    throw e;
                }
            }
        }

        long parts[] = new long[longs.size()];

        for (int i = 0; i < longs.size(); i++)
        {
            parts[i] = ((Long) longs.get(i)).longValue();
        //                   System.out.println("parts[i] = "+parts[i]);
        }

        ret = formNumber(parts);
        ret *= sign;

        //               System.out.println("ret = "+ret);

        return ret;
    }

    public String markNumbers(String srcsen, NumberFilter tgtfilter)
    {
        KeyValueProperties numProps = (KeyValueProperties) numberMultiKVP.getPropertiesValue("NumberProperties");

        String xmlTag = numProps.getPropertyValue("XMLTag");
        String xmlVal = numProps.getPropertyValue("XMLValAttr");
        String xmlTransVal = numProps.getPropertyValue("XMLTransAttr");

        srcsen = srcsen.trim();

        String[] wds = srcsen.split("[ ]");

        Vector wdsvec = new Vector(wds.length);
        Vector mweflags = new Vector(wds.length);
        Vector mwevals = new Vector(wds.length);

        String markedsen = "";

        if (wds.length <= 0)
        {
            return null;
        }

        for (int i = 0; i < wds.length; i++)
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

        while (j < wdsvec.size()) // - 1)
        {
            nan = true;
            int i = wdsvec.size();

            while (j <= wdsvec.size() && nan == true && i >= 0 && j < i)
            {
                token = "";

                for (int k = j; k < i; k++)
                {
                    token += (String) wdsvec.get(k) + " ";
                }

                i--;

                token = token.trim();

                try
                {
                    mwe = toInteger(token);
                    nan = false;
                } catch (NumberFormatException e)
                {
                    //throw e; // Ignore
                }
            }

            // Found a number expression
            if (nan == false)
            {
                for (int k = j; k <= i; k++)
                {
                    wdsvec.remove(j);
                    mweflags.remove(j);
                    mwevals.remove(j);
                }

                wdsvec.insertElementAt(token, j);
                mweflags.insertElementAt("true", j);
                String mwestr = "" + mwe;
                mwevals.insertElementAt(mwestr, j);

                if (j == i)
                {
                    j++;
                } else
                {
                    j = i;
                }
            } else
            {
                j++;
            }
        }

        for (int i = 0; i < wdsvec.size(); i++)
        {
            token = (String) wdsvec.get(i);
            String ismwe = (String) mweflags.get(i);
            String val = (String) mwevals.get(i);
            String transVal = "";

            if (ismwe.equals("true"))
            {
				if(tgtfilter != null)
				{
				    transVal = tgtfilter.toWords(Long.parseLong(val));
    				markedsen += " <" + xmlTag + " " + xmlVal + "=\"" + val
                             + " " + xmlTransVal + "=\"" + transVal + "\">" + token + "</" + xmlTag + ">";
				}
                else
    				markedsen += " <" + xmlTag + " " + xmlVal + "=\"" + val + "\">" + token + "</" + xmlTag + ">";
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

        while ((line = lnReader.readLine()) != null)
        {
            if (line.equals("") == false)
            {
                ps.println(markNumbers(line, null));
            }
        }
    }

    public static void main(String[] args)
    {
        NumberFilter f = null;

        f = new NumberFilter(new File(GlobalProperties.resolveRelativePath("data/number-filter/number-words-hin.txt")), "UTF-8");

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

        try
		{
			f.markBatch(GlobalProperties.resolveRelativePath("data/number-filter/num_sentences.en"), System.out);
		}
		catch(IOException e)
		{
			System.out.println("IOException!");
		}

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

//        System.out.println(0 + " : " + f.toInteger("zero"));
//        System.out.println(1 + " : " + f.toInteger("one"));
//        System.out.println(16 + " : " + f.toInteger("sixteen"));
//        System.out.println(100 + " : " + f.toInteger("one hundred"));
//        System.out.println(118 + " : " + f.toInteger("one hundred eighteen"));
//        System.out.println(200 + " : " + f.toInteger("two hundred"));
//        System.out.println(219 + " : " + f.toInteger("two hundred nineteen"));
//        System.out.println(800 + " : " + f.toInteger("eight hundred"));
//        System.out.println(801 + " : " + f.toInteger("eight hundred one"));
//        System.out.println(1316 + " : " + f.toInteger("one thousand three hundred sixteen"));
//        System.out.println(1000000 + " : " + f.toInteger("one million"));
//        System.out.println(2000000 + " : " + f.toInteger("two million"));
//        System.out.println(3000200 + " : " + f.toInteger("three million two hundred"));
//        System.out.println(700000 + " : " + f.toInteger("seven hundred thousand"));
//        System.out.println(9000000 + " : " + f.toInteger("nine million"));
//        System.out.println(123456789 + " : " + f.toInteger("one hundred twenty three million four hundred fifty six thousand seven hundred eighty nine"));
//        System.out.println(-45 + " : " + f.toInteger("minus forty five"));
//
//        System.out.println(4375 + " : " + f.toInteger("four thousand three hundred and seventy five"));
//        System.out.println(4075 + " : " + f.toInteger("four thousand and seventy five"));
//        System.out.println(4005 + " : " + f.toInteger("four thousand and five"));
//        System.out.println(-4005 + " : " + f.toInteger("minus four thousand and five"));
    }
}
