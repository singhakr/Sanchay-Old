/*
 * PreprocessRawText.java
 *
 * Created on April 26, 2006, 7:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util;

import java.io.*;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class PreprocessRawText {
    
    /** Creates a new instance of PreprocessRawText */
    public PreprocessRawText() {
    }
    
    public static void main(String[] args)
    {
	if(args == null || args.length != 5)
	{
	    System.out.println(GlobalProperties.getIntlString("USAGE:"));
	    System.out.println("\tjava sanchay.util.PreprocessRawText <input encoding> <output encoding> <language code> <input file> <output file>");
	    System.out.println(GlobalProperties.getIntlString("Example:"));
	    System.out.println("\tjava sanchay.util.PreprocessRawText UTF8 UTF8 hin::utf8 input.txt output.txt");
            
            System.exit(1);
	}
	
	String icharset = args[0];
	String ocharset = args[1];

	String lang = args[2];

	String ifile = args[3];
	String ofile = args[4];
	String tfile = ofile + ".tmp";
	
	try {
	    UtilityFunctions.naiivePreprocessing(ifile, icharset, tfile, ocharset, lang);
	    UtilityFunctions.trimSpaces(tfile, icharset, ofile, GlobalProperties.getIntlString("UTF-8"));
	    (new File(tfile)).delete();
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }    
}
