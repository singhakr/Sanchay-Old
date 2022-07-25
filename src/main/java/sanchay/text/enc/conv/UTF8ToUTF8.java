/*
 * UTF82UTF8.java
 *
 * Created on January 16, 2008, 8:32 PM
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
import java.util.Hashtable;
import java.util.Iterator;
import sanchay.GlobalProperties;
import sanchay.table.SanchayTableModel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class UTF8ToUTF8 extends EncodingConverter implements SanchayEncodingConverter {

    String srcLangEnc;
    String tgtLangEnc;
    
    protected SanchayTableModel map;
    protected Hashtable hash;
    protected Hashtable lhash;
    
    /** Creates a new instance of UTF82UTF8 */
    public UTF8ToUTF8(String srcLE, String tgtLE) {
        srcLangEnc = srcLE;
        tgtLangEnc = tgtLE;
        
        if
        (
            ! (
            (srcLE.equals(GlobalProperties.getIntlString("kas::utf8")) && tgtLE.equals(GlobalProperties.getIntlString("kas::roman")))
            || (srcLE.equals(GlobalProperties.getIntlString("kas::roman")) && tgtLE.equals(GlobalProperties.getIntlString("kas::utf8")))
            )
        )
        {
            try {
                map = new SanchayTableModel(GlobalProperties.resolveRelativePath("props/spell-checker/isc-utf8-wx-map.txt"), GlobalProperties.getIntlString("UTF-8"));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            hash = new Hashtable(map.getRowCount());
            lhash = new Hashtable(map.getRowCount());

            init();
        }
    }

    private void init()
    {
        int rcount = map.getRowCount();
        
        for (int i = 0; i < rcount; i++)
        {
            String ltype = (String) map.getValueAt(i, 1);
            String srcUTF = "";
            String tgtUTF = "";
            
            if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("hin::utf8")))
                srcUTF = (String) map.getValueAt(i, 3);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("ben::utf8")))
                srcUTF = (String) map.getValueAt(i, 4);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("gur::utf8")))
                srcUTF = (String) map.getValueAt(i, 5);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("guj::utf8")))
                srcUTF = (String) map.getValueAt(i, 6);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("ori::utf8")))
                srcUTF = (String) map.getValueAt(i, 7);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("tam::utf8")))
                srcUTF = (String) map.getValueAt(i, 8);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("tel::utf8")))
                srcUTF = (String) map.getValueAt(i, 9);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("kan::utf8")))
                srcUTF = (String) map.getValueAt(i, 10);
            else if(srcLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("mal::utf8")))
                srcUTF = (String) map.getValueAt(i, 11);
            
            if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("hin::utf8")))
                tgtUTF = (String) map.getValueAt(i, 3);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("ben::utf8")))
                tgtUTF = (String) map.getValueAt(i, 4);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("gur::utf8")))
                tgtUTF = (String) map.getValueAt(i, 5);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("guj::utf8")))
                tgtUTF = (String) map.getValueAt(i, 6);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("ori::utf8")))
                tgtUTF = (String) map.getValueAt(i, 7);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("tam::utf8")))
                tgtUTF = (String) map.getValueAt(i, 8);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("tel::utf8")))
                tgtUTF = (String) map.getValueAt(i, 9);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("kan::utf8")))
                tgtUTF = (String) map.getValueAt(i, 10);
            else if(tgtLangEnc.equalsIgnoreCase(GlobalProperties.getIntlString("mal::utf8")))
                tgtUTF = (String) map.getValueAt(i, 11);
            
            if(srcUTF != null && srcUTF.equals("") == false)
                hash.put(srcUTF, new String[] {ltype, tgtUTF} );
            
            if(srcUTF == null)
                srcUTF = ltype + "::?";
            else
                srcUTF = ltype + "::" + srcUTF;

            if(srcUTF != null && srcUTF.equals("") == false)
                lhash.put(srcUTF, new String[] {ltype, tgtUTF} );
        }
    }
    
    public String convert(String in)
    {        
        if(srcLangEnc.equals(GlobalProperties.getIntlString("kas::utf8")) && tgtLangEnc.equals(GlobalProperties.getIntlString("kas::roman")))
        {
            return UtilityFunctions.kashmiriToRomanString(in);
        }
        else if(srcLangEnc.equals(GlobalProperties.getIntlString("kas::roman")) && tgtLangEnc.equals(GlobalProperties.getIntlString("kas::utf8")))
        {
            return UtilityFunctions.romanToKashmiriString(in);            
        }

        String out = in;

        Iterator itr = hash.keySet().iterator();

        while(itr.hasNext())
        {
            String key = (String) itr.next();
            String val[] = (String[]) hash.get(key);

            if(val != null && val.length == 2 && val[1] != null)
            {
                key = "(?dum)" + key;

                out = out.replaceAll(key, val[1]);
            }
        }
        
//        int count = in.length();
//
//        boolean romanWord = false;
//        String prevLType = "";
//        String ltype = "";
//        String wx = "";
//        String uwx = "";
//
//        for (int i = 0; i < count; i++)
//        {
//            String utf = new String(new char[] { in.charAt(i) } );
//
//            ltype = "";
//            wx = utf;
//
//            if(utf.matches("[a-zA-Z0-9]"))
//            {
//                if(romanWord == false)
//                {
//                    romanWord = true;
//                    out += "@" + utf;
//                }
//                else
//                    out += utf;
//
//                prevLType = ltype;
//                continue;
//            }
//
//            if(romanWord && utf.matches("[\\s]"))
//            {
//                out += utf;
//                romanWord = false;
//                prevLType = ltype;
//                continue;
//            }
//            else
//                romanWord = false;
//
//            if(romanWord == false)
//            {
//                if(utf.matches("[\\s]"))
//                {
//                    if(prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N"))
//                        out += "a" + utf;
//                    else
//                        out += utf;
//
//                    prevLType = "";
//                    continue;
//                }
//
//                if(hash.get(utf) != null)
//                {
//                    ltype = ((String[]) hash.get(utf))[0];
//                    uwx = ((String[]) hash.get(utf))[1];
//
//                    if(uwx == null)
//                        continue;
//
//                    if(uwx.equals("M") || ltype.equals("N"))
//                    {
//                        if(prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N"))
//                            out += "a" + uwx;
//                        else
//                            out += uwx;
//
//                        prevLType = ltype;
//                        continue;
//                    }
//                    else if(ltype.equals("H"))
//                    {
//                        prevLType = ltype;
//                        continue;
//                    }
//                    else if((prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N"))
//                            && ltype.equals("M") == false)
//                    {
//                        out += "a" + uwx;
//                        prevLType = ltype;
//                        continue;
//                    }
//
//                    if(lhash.get(ltype + "::" + utf) != null)
//                        wx = ((String[]) lhash.get(ltype + "::" + utf))[1];
//                    else
//                        wx = utf;
//                }
//            }
//
//            if(ltype == null)
//                ltype = "";
//
//            if(hash.get(utf) != null)
//            {
//                if(wx != null && wx.equals("") == false)
//                {
//                    out += wx;
//                }
//                else
//                    out += utf;
//            }
//            else
//                out += wx;
//
//            prevLType = ltype;
//        }
        
        return out;
    }
    
    public void convert(File inFile, File outFile) throws FileNotFoundException, IOException
    {
        if(srcLangEnc.equals(GlobalProperties.getIntlString("kas::utf8")) && tgtLangEnc.equals(GlobalProperties.getIntlString("kas::roman")))
        {
            UtilityFunctions.kashmiriToRoman(inFile.getAbsolutePath(), outFile.getAbsolutePath());
        }
        else if(srcLangEnc.equals(GlobalProperties.getIntlString("kas::roman")) && tgtLangEnc.equals(GlobalProperties.getIntlString("kas::utf8")))
        {
            UtilityFunctions.romanToKashmiri(inFile.getAbsolutePath(), outFile.getAbsolutePath());            
        }
        else
        {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), GlobalProperties.getIntlString("UTF-8")));
            PrintStream ps = new PrintStream(outFile, GlobalProperties.getIntlString("UTF-8"));

            String line = "";

            System.out.println(GlobalProperties.getIntlString("Writing_file_") + outFile);

            while((line = inReader.readLine()) != null) {
                line = convert(line);
                ps.println(line);
            }        
        }
    }
    
//    public void convertBatch(File inFile, File outFile) throws FileNotFoundException, IOException {
//        if(inFile.isFile() == true)
//        {
//            if(outFile.isDirectory())
//            {
//                File odir = outFile;
//
//                if(odir.exists() == false)
//                {
//                    odir.mkdir();
//                }
//
//                odir = new File(odir, inFile.getParentFile().getName() + "-" + inFile.getName());
//
//                System.out.println(GlobalProperties.getIntlString("Converting_file_") + inFile.getAbsolutePath());
//                convert(inFile, odir);
//            }
//            else
//            {
//                System.out.println(GlobalProperties.getIntlString("Converting_file_") + inFile.getAbsolutePath());
//                convert(inFile, outFile);
//            }
//        }
//        else
//        {
//            if(inFile.isDirectory() == true)
//            {
//                File files[] = inFile.listFiles();
//
//                for(int i = 0; i < files.length; i++)
//                {
//                    convertBatch(files[i], outFile);
//                }
//            }
//        }
//    }
    
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

        String srcLangEnc = GlobalProperties.getIntlString("hin::utf8");
        String tgtLangEnc = GlobalProperties.getIntlString("tel::utf8");
        
        File inFile = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.utf8.txt");
        File outFile = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.tel.utf8.txt");

        if(args.length > 0)
            srcLangEnc = args[0];

        if(args.length > 1)
            tgtLangEnc = args[1];
        
        if(args.length > 2)
            inFile = new File(args[2]);

        if(args.length > 3)
            outFile = new File(args[3]);
        
        if(inFile.getParentFile().exists() == false)
            inFile.getParentFile().mkdir();
        
        UTF8ToUTF8 converter = new UTF8ToUTF8(srcLangEnc, tgtLangEnc);
        
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
