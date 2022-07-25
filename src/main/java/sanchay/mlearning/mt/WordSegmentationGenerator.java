/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

/**
 *
 * @author anil
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

public class WordSegmentationGenerator {

	Vector vec;
	String outPath;

    public WordSegmentationGenerator()
	{
		vec = new Vector();
    }
    
	public WordSegmentationGenerator(String Fname)
	{
		vec = new Vector();
		outPath = Fname + "-segment.txt";
		readFile(Fname);
	}

    public void clear()
    {
        vec.clear();
    }

	public void readFile(String path)
	{
        if(outPath == null)
            outPath = path + "-segment.txt";

		Vector words = new Vector();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));

			String line;
			while ((line = in.readLine()) != null) {
				words.add(line);

			}
//			System.out.println(words.size());
			Iterator iter = words.iterator();
			while (iter.hasNext()) {
				String word = (String)iter.next();
				vec.add(word);
				partition("", word);
				printVector();
				vec.removeAllElements();
				Runtime r = Runtime.getRuntime();
				r.gc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Vector partition(String prv,String wrd)
	{
        
		int length = wrd.length();
		if(prv.length() == 0)
			prv = new String("");

		if(length == 1)
		{
//			System.out.println(prv+wrd);
			vec.add(prv+wrd);
			return vec;
		}
		else
		{
			for(int i=0;i<length;i++)
			{
				String left = prv + wrd.substring(0,i+1)+" ";
				String rem = wrd.substring(i+1);
				if(rem.length() > 1)
				{
//					System.out.println(left+rem);
					vec.add(left+rem);
				}
				if(rem.length() == 0)
					return vec;
				partition(left,rem);
			}
		}

        return vec;
	}
	public void printVector()
	{
		try
		{
		int index=0;
		boolean flag;
		BufferedWriter bw = new BufferedWriter(new FileWriter(outPath, true));

		while(index < vec.size())
		{
			flag = false;
			String elem = (String)vec.elementAt(index++);
			String[] str = elem.split(" ");
			if(str.length == 1)
				bw.write(elem+"\n");
			else
			{
				for(int i = 0;i<str.length;i++)
				{
					if(str[i].length()>5)
					{
						flag = true;
						break;
					}
				}
				if(flag)
					continue;
//				System.out.println(elem);
				bw.write(elem+"\n");
			}
		}
		bw.close();
		}catch (IOException e) {
		}
	}
	public static void main(String[] args) {

//		for(int i=0;i<10;i++)
//		{
		String filename = "/home/anil/tmp/feature_based_code/translit-test-data-hindi.txt";
		WordSegmentationGenerator pw = new WordSegmentationGenerator(filename);
//		}

	}
}

