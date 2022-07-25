/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.text.enc.conv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import ml.options.Options;

/**
 *
 * @author anil
 */
public class EncodingConverter implements SanchayEncodingConverter {

    @Override
    public String convert(String in) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void convert(File inFile, File outFile) throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void convertBatch(File inFile, File outFile) throws FileNotFoundException, IOException {
                
        if(inFile.isFile() == true)
        {
            convert(inFile, outFile);
        }
        else
        {
            if(inFile.isDirectory() == true)
            {
                File files[] = inFile.listFiles();                

                for(int i = 0; i < files.length; i++)
                {
                    File ofile = new File(outFile, files[i].getName());
                    convert(files[i], ofile);
                }
            }
        }        
    }
    
//    protected static void runFromCommandLine(String args[])
//    {
//        Options opt = new Options(args, 2, 2);
//
//        opt.getSet().addOption("srcLanguage", "sl", Options.Separator.BLANK, Options.Multiplicity.ONCE);
//        opt.getSet().addOption("tgtLanguage", "tl", Options.Separator.BLANK, Options.Multiplicity.ONCE);
//
//        String srcLanguage = "UTF-8";
//        String tgtLanguage = "WX";
//        String infile;
//        String outfile;
//        
//        if (!opt.check()) {
//            System.out.println("EncodingConverter -sl srcLanguage -tl tgtLanguage inpath outpath");
//            System.out.println("\tExample:");
//            System.out.println("\tEncodingConverter -sl hin::utf8 -tl hin::wx inpath outpath");
//            System.out.println(opt.getCheckErrors());
//            System.exit(1);
//        }
//
//        if (opt.getSet().isSet("srcLanguage")) {
//            srcLanguage = opt.getSet().getOption("srcLanguage").getResultValue(0);
//            System.out.println("Source language-encoding: " + srcLanguage);
//        }
//
//        if (opt.getSet().isSet("tgtLanguage")) {
//            tgtLanguage = opt.getSet().getOption("tgtLanguage").getResultValue(0);
//            System.out.println("Target language-encoding: " + tgtLanguage);
//        }
//
//        infile = opt.getSet().getData().get(0);
//        outfile = opt.getSet().getData().get(1);
//
//        System.out.println("Input path: " + infile);
//        System.out.println("Output path: " + outfile);
//        
//        EncodingConverter encodingConverter;
//
//        try {
//            encodingConverter = (EncodingConverter) EncodingConverterUtils.createEncodingConverter(srcLanguage, tgtLanguage);
//            
//            encodingConverter.convertBatch(new File(infile), new File(outfile));
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(EncodingConverter.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(EncodingConverter.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(EncodingConverter.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(EncodingConverter.class.getName()).log(Level.SEVERE, null, ex);
//        }        
//    }

    public static void main(String args[])
    {
//        runFromCommandLine(args);
    }
}
