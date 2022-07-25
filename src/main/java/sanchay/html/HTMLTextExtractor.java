/*
 * HTMLTextExtractor.java
 *
 * Created on February 4, 2007, 3:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import org.htmlparser.parserapplications.*;
import org.htmlparser.util.ParserException;

import sanchay.GlobalProperties;
import sanchay.util.FileFilterImpl;

/**
 *
 * @author Anil Kumar Singh
 */
public class HTMLTextExtractor {

    String url;
    StringExtractor extractor;
    String outputDir;
    String charset;
    
    /** Creates a new instance of HTMLTextExtractor */
    public HTMLTextExtractor(String url, String outDir, String cs)
    {
        this.url = url;
        outputDir = outDir;
        
        if (url != null)
        {
            extractor = new StringExtractor(url);
        }
        
        charset = cs;
    }
    
    public String extractStrings(boolean links) throws ParserException
    {
        return extractor.extractStrings(links);
    }
    
    public void writeExtractedStrings(boolean links)
            throws ParserException, FileNotFoundException, UnsupportedEncodingException, MalformedURLException
    {
        File odfile = new File(outputDir);
        
        URL u = new URL(url);
        
        String path = u.getPath();
        File pfile = new File(path);
        
        File ofile = new File(odfile.getPath(), pfile.getName());

        System.out.println(GlobalProperties.getIntlString("Writing_file_") + ofile.getAbsolutePath() + GlobalProperties.getIntlString("_with_") + charset + GlobalProperties.getIntlString("_as_the_charset."));
        
        PrintStream ps = null;
        
        if(charset == null)
            ps = new PrintStream(ofile);
        else
            ps = new PrintStream(ofile, charset);

        String exString = extractStrings(links);
//        exString = new String(exString.getBytes(charset));
        
        ps.println(exString);
        
        ps.close();
    }
        
    public static void writeExtractedStrings(String inURL, String outDir, String cs, boolean links)
            throws ParserException, FileNotFoundException, UnsupportedEncodingException, MalformedURLException
    {
        if(inURL != null)
        {
            HTMLTextExtractor extractor = new HTMLTextExtractor(inURL, outDir, cs);
            
            if(outDir == null)
            {
                try {
                    System.out.println(extractor.extractStrings(links));
                } catch (ParserException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    extractor.writeExtractedStrings(links);
                } catch (ParserException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        else
            System.out.println(GlobalProperties.getIntlString("Null_URL_for_string_extraction."));
    }
    
    public static void writeExtractedStringsBatch(String inURL, String outDir, String cs, boolean links, boolean filter)
            throws ParserException, FileNotFoundException, UnsupportedEncodingException, MalformedURLException
    {
        URL u = new URL(inURL);
        String protocol = u.getProtocol();
        String host = u.getHost();

        String path = u.getPath();
        File inFile = new File(path);
        
        File outFile = new File(outDir);
        
        String urlStr = null;
        
        if(inFile.isFile() == true)
        {
//            if((new File(outDir)).isDirectory())
//            {
//                File odir = new File(outDir);
//
//                if(odir.exists() == false)
//                {
//                    odir.mkdir();
//                }
//
//                odir = new File(odir, inFile.getName());
                
                writeExtractedStrings(inURL, outDir, cs, links);
//                writeExtractedStrings(inURL, odir.getAbsolutePath(), cs, links);
//            }
//            else
//            {
//                writeExtractedStrings(inURL, outDir, cs, links);
//            }
        }
        else
        {
            if(inFile.isDirectory() == true)
            {
                File files[] = null;
                
                if(filter)
                {
                    java.io.FileFilter fileFilter = new FileFilterImpl(new String[]{"htm", "html", "xhtml", "xml", "txt"}, GlobalProperties.getIntlString("Web_pages"));
                    files = inFile.listFiles(fileFilter);
                }
                else
                    files = inFile.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    urlStr = (new URL(protocol, host, files[i].getAbsolutePath())).toString();
                    writeExtractedStringsBatch(urlStr, outDir, cs, links, filter);
                }
            }
        }
    }
    
    public static void main(String[] args)
    {
        boolean links;
        boolean batch;
        String url;
        String outDir;
        String cs;
        
        links = false;
        batch = false;
        
        url = null;
        outDir = null;
        cs = null;

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equalsIgnoreCase("-links"))
                links = true;
            else if (args[i].equalsIgnoreCase("-b"))
                batch = true;
            else if (args[i].equalsIgnoreCase("-o"))
            {
                if(i < args.length - 1)
                {
                    outDir = args[i + 1];
                    i++;
                }
                else
                    System.out.println(GlobalProperties.getIntlString("Usage:_java_-cp_dist/Sanchay.jar_sanchay.html.HTMLTextExtractor") +
                            " [-links] [-e encoding] [-o output-directory] url");
            }
            else if (args[i].equalsIgnoreCase("-e"))
            {
                if(i < args.length - 1)
                {
                    cs = args[i + 1];
                    i++;
                }
                else
                    System.out.println(GlobalProperties.getIntlString("Usage:_java_-cp_dist/Sanchay.jar_sanchay.html.HTMLTextExtractor") +
                            " [-links] [-e encoding] [-o output-directory] url");
            }
            else
                url = args[i];
        }
        
        if(batch && url != null)
        {
            System.out.println(GlobalProperties.getIntlString("Batch_mode"));
            
            try {
                HTMLTextExtractor.writeExtractedStringsBatch(url, outDir, cs, links, true);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (ParserException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        else if(url != null)
        {
            HTMLTextExtractor extractor = new HTMLTextExtractor(url, outDir, cs);
            
            if(outDir == null)
            {
                try {
                    System.out.println(extractor.extractStrings(links));
                } catch (ParserException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    extractor.writeExtractedStrings(links);
                } catch (ParserException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        else
            System.out.println(GlobalProperties.getIntlString("Usage:_java_-cp_dist/Sanchay.jar_sanchay.html.HTMLTextExtractor") +
                    " [-links] [-e encoding] [-o output-directory] url");
    }    
}
