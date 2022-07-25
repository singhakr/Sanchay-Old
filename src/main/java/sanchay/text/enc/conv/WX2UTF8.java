/*
 * WX2UTF8.java
 *
 * Created on January 15, 2008, 2:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text.enc.conv;

import java.io.BufferedReader;
import java.util.Hashtable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import sanchay.GlobalProperties;
import sanchay.table.SanchayTableModel;

/**
 *
 * @author anil
 */
public class WX2UTF8 extends EncodingConverter implements SanchayEncodingConverter {
    
    protected String langEnc;
    protected SanchayTableModel map;
    protected Hashtable hash;
    protected Hashtable lhash;
    
    /** Creates a new instance of WX2UTF8 */
    public WX2UTF8(String langEnc) {
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
        
        for (int i = 0; i < rcount; i++)
        {
            String ltype = (String) map.getValueAt(i, 1);
            String wx = (String) map.getValueAt(i, 2);
            String utf = "";
            
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

            if(wx != null && wx.equals("") == false)
                hash.put(wx, new String[] {ltype, utf} );

            if(wx != null && wx.equals("") == false)
                lhash.put(ltype + "::" + wx, new String[] {ltype, utf} );
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
        String prevWX = "";
        String ltype = "";
        String utf = "";
        
        for (int i = 0; i < count; i++)
        {            
            String wx = new String(new char[] { in.charAt(i) } );
                
            ltype = "";
            utf = wx;
            
            if(romanWord == false)
            {
                if(wx.matches("[\\s]"))
                {
                    out += wx;
                    prevWX = wx;
                    prevLType = "";
                    continue;
                }

                if(hash.get(wx) != null)
                {
                    ltype = ((String[]) hash.get(wx))[0];
            
                    if((prevWX.matches("[\\s]") && prevWX.equals("a")) && (ltype.equals("V") || ltype.equals("M")))
                    {
                        String svar = ((String[]) lhash.get("V::" + wx))[1];
                        out += svar;
                        prevWX = wx;
                        prevLType = "V";
                        continue;
                    }
                    if(wx.equals("M"))
                    {
                        String anu = ((String[]) lhash.get("D::M"))[1];
                        out += anu;
                        prevWX = wx;
                        prevLType = "D";
                        continue;
                    }
                    else if((prevLType.equals("C") || prevLType.equals("A")) && ltype.equals("N"))
                    {
                        String nuk = ((String[]) lhash.get("N::Z"))[1];
                        out += nuk;
                        prevWX = wx;
                        prevLType = ltype;
                        continue;
                    }
                    else if((prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N")) && wx.equals("a"))
                    {
                        prevWX = wx;
                        prevLType = ltype;
                        continue;                        
                    }
                    else if(prevLType.equals("") && ltype.equals("M"))
                        ltype = "V";
                    else if((prevLType.equals("M") || prevLType.equals("D") || prevLType.equals("V")) && ltype.equals("M"))
                        ltype = "V";

                    if(lhash.get(ltype + "::" + wx) != null)
                        utf = ((String[]) lhash.get(ltype + "::" + wx))[1];
                    else
                        utf = wx;
                }
            }
            
            if(ltype == null)
                ltype = "";
                        
            if(wx.equals("@"))
            {
                romanWord = true;
                prevWX = wx;
                prevLType = ltype;
                continue;
            }
            
            if(romanWord && wx.matches("[\\s]") == false)
            {
                out += utf;
                prevWX = wx;
                prevLType = ltype;
                continue;
            }
            else if(romanWord && wx.matches("[\\s]"))
            {
                out += utf;
                romanWord = false;
                prevWX = wx;
                prevLType = ltype;
                continue;                
            }

            if(hash.get(wx) != null)
            {
                if(utf != null && utf.equals("") == false)
                {
                    out += utf;
                }
                else
                    out += wx;                
            }
            else
                out += utf;                

            if(out.length() > 1
                    && (prevLType.equals("C") || prevLType.equals("A") || prevLType.equals("N"))
                    && (ltype.equals("M") == false && ltype.equals("V") == false))
            {
                String hal = ((String[]) lhash.get("H::?"))[1];
                out = out.substring(0, out.length() - 1) + hal + out.substring(out.length() - 1);
            }            
            else if(out.length() > 2 && prevLType.equals("A") && (ltype.equals("C") || ltype.equals("A")))
            {
                if(i == in.length() - 1 ||  i < in.length() - 1)
                {
                    if(in.charAt(i - 1) != 'm' && in.charAt(i - 1) != 'n' && in.charAt(i - 1) != 'N')
                    {
                        String anu = ((String[]) lhash.get("D::M"))[1];
                        out = out.substring(0, out.length() - 2) + anu + out.substring(out.length() - 1);
                    }
                }
            }
            else if(out.length() > 1 && prevLType.equals("M") && ltype.equals("A"))
            {
                if(i == in.length() - 1 ||  (i < in.length() - 1 && in.charAt(i + 1) != 'a'))
                {
                    if(wx.equals("m") == false && wx.equals("n") == false && wx.equals("N") == false)
                    {
                        String anu = ((String[]) lhash.get("D::M"))[1];
                        out = out.substring(0, out.length() - 1) + anu;
                        ltype = "A";
                    }
                }
            }            
            
            prevWX = wx;
            prevLType = ltype;
        }
        
        return out;
    }
    
    public void convert(File inFile, File outFile) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), GlobalProperties.getIntlString("ASCII")));
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

        String langEnc = GlobalProperties.getIntlString("hin::utf8");
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
        
        WX2UTF8 converter = new WX2UTF8(langEnc);
        
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
