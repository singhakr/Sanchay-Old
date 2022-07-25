/*
 * UTF2WX.java
 *
 * Created on January 15, 2008, 2:25 AM
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
import sanchay.GlobalProperties;
import sanchay.table.SanchayTableModel;

/**
 *
 * @author anil
 */
public class UTF82WX extends EncodingConverter implements SanchayEncodingConverter {
    
    protected String langEnc;
    protected SanchayTableModel map;
    protected Hashtable hash;
    protected Hashtable lhash;
    
    /** Creates a new instance of UTF2WX */
    public UTF82WX(String langEnc) {
        this.langEnc = langEnc;
        
        try {
            map = new SanchayTableModel(GlobalProperties.resolveRelativePath("props/spell-checker/isc-utf8-wx-map.txt"),
                    GlobalProperties.getIntlString("UTF-8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        hash = new Hashtable(map.getRowCount());
        lhash = new Hashtable(map.getRowCount());
        
        init();
    }
        
    private void init()
    {
        int rcount = map.getRowCount();
        String utf = "";
        String wx = "";
        String ltype = "C";
        
        for (int i = 0; i < rcount; i++)
        {
            ltype = (String) map.getValueAt(i, 1);
            wx = (String) map.getValueAt(i, 2);
            utf = "";
            
            if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("hin::utf8")))
                utf = (String) map.getValueAt(i, 3);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("ben::utf8")))
                utf = (String) map.getValueAt(i, 4);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("gur::utf8")))
                utf = (String) map.getValueAt(i, 5);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("guj::utf8")))
                utf = (String) map.getValueAt(i, 6);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("ori::utf8")))
                utf = (String) map.getValueAt(i, 7);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("tam::utf8")))
                utf = (String) map.getValueAt(i, 8);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("tel::utf8")))
                utf = (String) map.getValueAt(i, 9);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("kan::utf8")))
                utf = (String) map.getValueAt(i, 10);
            else if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("mal::utf8")))
                utf = (String) map.getValueAt(i, 11);

            if(utf != null && utf.equals("") == false)
                hash.put(utf, new String[] {ltype, wx} );
            
            if(utf == null)
                utf = ltype + "::?";
            else
                utf = ltype + "::" + utf;

            if(utf != null && utf.equals("") == false)
                lhash.put(utf, new String[] {ltype, wx} );
        }

        ltype = "C";

        if(langEnc.equalsIgnoreCase(GlobalProperties.getIntlString("hin::utf8")))
        {
            utf = "ऩ";
            wx = "nZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "ऱ";
            wx = "rZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "ऴ";
            wx = "LZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "क़";
            wx = "kZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "ख़";
            wx = "KZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "ग़";
            wx = "gZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "ज़";
            wx = "jZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "ड़";
            wx = "dZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "ढ़";
            wx = "DZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "फ़";
            wx = "PZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );

            utf = "य़";
            wx = "yZ";
            hash.put(utf, new String[] {ltype, wx} );
            utf = ltype + "::" + utf;
            lhash.put(utf, new String[] {ltype, wx} );
        }
    }
    
    public String convert(String in)
    {
        String out = "";

        if(in == null)
            return null;

        if(in.equals(""))
            return "";
        
        int count = in.length();
        
        boolean romanWord = false;
        String prevLType = "";
        String ltype = "";
        String wx = "";
        String uwx = "";
        
        for (int i = 0; i < count; i++)
        {            
            String utf = new String(new char[] { in.charAt(i) } );
                
            ltype = "";
            wx = utf;
                        
            if(utf.matches("[a-zA-Z0-9]"))
            {
                if(romanWord == false)
                {
                    romanWord = true;
                    out += "@" + utf;
                }
                else
                    out += utf;
                
                prevLType = ltype;
                continue;
            }
            
            if(romanWord && utf.matches("[\\s]"))
            {
                out += utf;
                romanWord = false;
                prevLType = ltype;
                continue;                
            }
            else
                romanWord = false;
            
            if(romanWord == false)
            {
                if(utf.matches("[\\s]"))
                {
                    if(prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N"))
                        out += "a" + utf;
                    else
                        out += utf;
                        
                    prevLType = "";
                    continue;
                }

                if(hash.get(utf) != null)
                {
                    ltype = ((String[]) hash.get(utf))[0];
                    uwx = ((String[]) hash.get(utf))[1];
            
                    if(uwx.equals("M") || ltype.equals("N"))
                    {
                        if(prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N"))
                            out += "a" + uwx;
                        else
                            out += uwx;
                            
                        prevLType = ltype;
                        continue;
                    }
                    else if(ltype.equals("H"))
                    {
                        prevLType = ltype;
                        continue;
                    }
                    else if((prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N"))
                            && ltype.equals("M") == false)
                    {
                        out += "a" + uwx;
                        prevLType = ltype;
                        continue;
                    }

                    if(lhash.get(ltype + "::" + utf) != null)
                        wx = ((String[]) lhash.get(ltype + "::" + utf))[1];
                    else
                        wx = utf;
                }
            }
            
            if(ltype == null)
                ltype = "";
            
            if(hash.get(utf) != null)
            {
                if(wx != null && wx.equals("") == false)
                {
                    out += wx;
                }
                else
                    out += utf;                
            }
            else
                out += wx;                

            prevLType = ltype;
        }

        if(prevLType.equals("C") || prevLType.equals("A"))
            out += "a";
        
        return out;
    }
    
    public void convert(File inFile, File outFile) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), GlobalProperties.getIntlString("UTF-8")));
        PrintStream ps = new PrintStream(outFile, GlobalProperties.getIntlString("UTF-8"));
        
        String line = "";
        
        System.out.println("Writing file: " + outFile);
        
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

        String langEnc = GlobalProperties.getIntlString("hin::utf8");
        File inFile = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.utf8.txt");
        File outFile = new File(GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp.wx.txt");

        if(args.length > 0)
            langEnc = args[0];
        
        if(args.length > 1)
            inFile = new File(args[1]);

        if(args.length > 2)
            outFile = new File(args[2]);
        
        if(inFile.getParentFile().exists() == false)
            inFile.getParentFile().mkdir();
        
        UTF82WX converter = new UTF82WX(langEnc);
        
//        try {
//            converter.convert(inFile, outFile);
            System.out.println(converter.convert("चुन"));
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }    
}
