/*
 * Tokenizer.java
 *
 * Created on December 21, 2006, 7:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class Tokenizer {
    
    /** Creates a new instance of Tokenizer */
    public Tokenizer() {
    }

    public static void main(String[] args)
    {
        
	if(args == null || args.length != 5)
	{
	    System.out.println(GlobalProperties.getIntlString("USAGE:"));
	    System.out.println("\tjava sanchay.util.Tokenizer <input encoding> <output encoding> <language code> <input file> <output file>");
	    System.out.println(GlobalProperties.getIntlString("Example:"));
	    System.out.println("\tjava sanchay.util.Tokenizer UTF8 UTF8 hin::utf8 input.txt output.txt");
            
            System.exit(1);
	}
	
	String icharset = args[0];
	String ocharset = args[1];

	String lang = args[2];

	String ifile = args[3];
	String ofile = args[4];
//	String tfile = ofile + ".tmp";
	
	try {
//	    UtilityFunctions.naiiveTokenization(ifile, icharset, tfile, ocharset, lang);
	    UtilityFunctions.naiiveTokenization(ifile, icharset, ofile, ocharset, lang);
//	    UtilityFunctions.trimSpaces(tfile, icharset, ofile, "UTF-8");
//	    (new File(tfile)).delete();
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
}
