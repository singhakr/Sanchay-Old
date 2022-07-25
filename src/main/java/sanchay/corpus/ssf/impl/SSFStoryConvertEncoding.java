/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.text.enc.conv.EncodingConverterUtils;
import sanchay.text.enc.conv.SanchayEncodingConverter;

/**
 *
 * @author anil
 */
public class SSFStoryConvertEncoding {

    protected SanchayEncodingConverter encodingConverter;

    public void init(String srcLangEnc, String tgtLangEnc)
    {
        encodingConverter = EncodingConverterUtils.createEncodingConverter(srcLangEnc, tgtLangEnc);

        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

//        SSFText text = null;

        try {
            fsp.read(GlobalProperties.resolveRelativePath("props/fs-mandatory-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-other-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/fs-props.txt"),
                    GlobalProperties.resolveRelativePath("props/ps-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/dep-attribs.txt"),
                    GlobalProperties.resolveRelativePath("props/sem-attribs.txt"),
                    GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;

            ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), GlobalProperties.getIntlString("UTF-8")); //throws java.io.FileNotFoundException;
            FeatureStructuresImpl.setFSProperties(fsp);
            SSFNode.setSSFProperties(ssfp);
        }  catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void convertEncoding(String srcFilePath, String srcCharset, String tgtFilePath,
            String tgtCharset) throws FileNotFoundException, IOException, Exception
    {
        System.out.println("Converting: " + srcFilePath);

        SSFStory story = new SSFStoryImpl();
        
        story.readFile(srcFilePath, srcCharset); //throws java.io.FileNotFoundException;

        story.convertEncoding(encodingConverter, "NULL");

        story.save(tgtFilePath, tgtCharset); //throws java.io.FileNotFoundException;
    }

    public void convertEncoding(SSFStory story, String nullNodeString)
    {
        story.convertEncoding(encodingConverter, nullNodeString);
    }

    public static void main(String[] args)
    {
        SSFStoryConvertEncoding  ssfStoryConvertEncoding = new  SSFStoryConvertEncoding();

        String srcLangEnc = "hin::utf8";
        String tgtLangEnc = "hin::wx";

        ssfStoryConvertEncoding.init(srcLangEnc, tgtLangEnc);

//        String srcFilePath = "/home/anil/sanchay-debug-data/poslcat-single-fs.txt";
//        String tgtFilePath = "/home/anil/sanchay-debug-data/poslcat-single-fs.wx.txt";

        String srcFilePath = "/home/anil/sanchay-debug-data/validation-test";
        String tgtFilePath = "/home/anil/sanchay-debug-data/validation-test-wx";

        File srcFile = new File(srcFilePath);
        File tgtFile = new File(tgtFilePath);

        tgtFile.mkdirs();

        String srcCharset = "UTF-8";
        String tgtCharset = "UTF-8";

        try {
            if(srcFile.isFile() && tgtFile.isFile())
                ssfStoryConvertEncoding.convertEncoding(srcFilePath, srcCharset, tgtFilePath, tgtCharset);
            else if(srcFile.isDirectory() && tgtFile.isDirectory())
            {
                File[] files = srcFile.listFiles();

                for (int i = 0; i < files.length; i++) {
                    File tgtFl = new File(tgtFile, files[i].getName());

                    ssfStoryConvertEncoding.convertEncoding(files[i].getAbsolutePath(), srcCharset,
                            tgtFl.getAbsolutePath(), tgtCharset);
                }
            }

            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
