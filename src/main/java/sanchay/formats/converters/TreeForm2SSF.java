/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.formats.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import sanchay.corpus.Corpus;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFCorpusImpl;
import sanchay.corpus.xml.XMLStory;
import sanchay.corpus.xml.impl.XMLCorpusImpl;

/**
 *
 * @author anil
 */
public class TreeForm2SSF {

    protected String srcPath;
    protected File srcFiles[];

    protected String tgtPath;

    protected String outputPath;

    protected String charset;

    protected Corpus srcCorpus;
    protected Corpus tgtCorpus;

    public TreeForm2SSF()
    {
    }

    public void init(String srcPath, String tgtPath, String cs, String opath)
    {
        this.srcPath = srcPath;
        this.tgtPath = tgtPath;

        charset = cs;

        outputPath = opath;

        tgtCorpus = new XMLCorpusImpl();

//         try {
//            ((XMLCorpus) tgtCorpus).read(tgtPath, charset);
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        srcCorpus = new SSFCorpusImpl(charset);
    }

    public void init(File srcFiles[], String tgtPath, String cs, String opath)
    {
        this.srcFiles = srcFiles;
        this.tgtPath = tgtPath;

        charset = cs;

        outputPath = opath;

        tgtCorpus = new XMLCorpusImpl();

//         try {
//            ((XMLCorpus) tgtCorpus).read(tgtPath, charset);
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        srcCorpus = new SSFCorpusImpl(charset);
    }

    protected void addStory(XMLStory tgtDoc, SSFStory srcDoc)
    {
        String opath = tgtDoc.getOutputPath();

        try {

            File ofile = new File(opath);
            File outputFile = new File(outputPath);

            if(outputFile.exists() && outputFile.isDirectory())
            {
                outputFile = new File(outputFile, ofile.getName());

                opath = outputFile.getAbsolutePath();
            }

            srcDoc.save(opath, charset);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ((SSFCorpus) srcCorpus).addStory(outputPath, null);
    }

    public static void main(String argv[])
    {
//        CRF2SSF ml2SSF = new CRF2SSF();
//        crf2SSF.init("D:/automatic-annotation/data/pos-tagging/ml-format/crf/pos-crf.txt", crfPath, "UTF-8", taggedDataPath);
//
//            ml2SSF.convert(MLCorpusConverter.POS_FORMAT);
//        crfAnnotation.annotate();
    }
}
