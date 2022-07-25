/*
 * Suman2UTF8.java
 *
 * Created on September 8, 2008, 7:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text.enc.conv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author eklavya
 */
public class Suman2UTF8 extends EncodingConverter implements SanchayEncodingConverter  {
    
    public Suman2UTF8() {
    }
    
    protected String langEnc;
    protected KeyValueProperties map;
    
    /** Creates a new instance of Suman2UTF8 */
    public Suman2UTF8(String langEnc) {
        this.langEnc = langEnc;
        
        try {
            map = new KeyValueProperties(GlobalProperties.resolveRelativePath("props/spell-checker/suman-utf8-map.txt"),
                    GlobalProperties.getIntlString("UTF-8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String convert(String in)
    {
        String out = in;
        
        Iterator enm = map.getPropertyKeys();
        
        String sum = "";
        String utf = "";
        
        while(enm.hasNext())
        {
            sum = (String) enm.next();
            utf = map.getPropertyValue(sum);
            
            out = out.replaceAll(sum, utf);
        }
        
        out = out.replaceAll("\u094D\u093E", "");        
        
        return out;
    }
    
    public void convert(File inFile, File outFile) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), GlobalProperties.getIntlString("ISO-8859-1")));
        PrintStream ps = new PrintStream(outFile, GlobalProperties.getIntlString("UTF-8"));
        
        String line = "";
        
        System.out.println(GlobalProperties.getIntlString("Writing_file_") + outFile);
        
        while((line = inReader.readLine()) != null) {
            line = convert(line);
            ps.println(line);
        }        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
//        if(args.length != 3)
//        {
//            System.out.println("USAGE:");
//            System.out.println("java -cp dist/Sanchay.jar sanchay.text.enc.conv.WX2UTF8 langEnc inFilePath outFilePath");
//            System.out.println("\tFile path could be of a file or directory.");
////            System.out.println("\tConversion will be performed recursively.");
//            System.exit(1);
//        }

        String langEnc = GlobalProperties.getIntlString("hin::suman");
        File inFile = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.wx.txt");
        File outFile = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.utf8.txt");

        if(args.length > 0)
            langEnc = args[0];
        
        if(args.length > 1)
            inFile = new File(args[1]);

        if(args.length > 2)
            outFile = new File(args[2]);
        
        if(inFile.getParentFile().exists() == false)
            inFile.getParentFile().mkdir();
        
        Suman2UTF8 converter = new Suman2UTF8(langEnc);
        
        try {
            converter.convert(inFile, outFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }        
}
