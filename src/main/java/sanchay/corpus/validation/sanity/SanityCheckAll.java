/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.validation.sanity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author ambati
 */
public class SanityCheckAll {

    private File errFile;   // Input Directory
    private File usefulDir;  // Tools program Directory
    private String lang;
    private Vector POSTags;
    private SanityCheckFuncs scf;

    public SanityCheckAll()
    {
        POSTags = new Vector();
    }

    public void setLanguage(String language)
    {
        lang = language;
    }

    public void setToolsDir(String dir) throws FileNotFoundException, IOException
    {
        usefulDir = new File(dir);
    }

    public void fillPOSTags(File posFile) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = new BufferedReader(new FileReader(posFile));

        String line="";
        while ((line = inReader.readLine()) !=null)
        {
            line=line.trim();
            POSTags.add(line);
        }
    }

    public void setErrFile(String file)
    {
        errFile = new File(file);
        if(errFile.exists())
        {
            errFile.delete();
        }
    }

    public void checkFuncsInits() throws FileNotFoundException, IOException
    {
        scf = new SanityCheckFuncs();
        scf.setLanguage(lang);
        scf.setToolsDir(usefulDir.getAbsolutePath());
        scf.inits();
    }

    public void checkErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        //System.out.println("Processing: "+ifile);
        
        checkFuncsInits();
        checkPOSErrors(ifile);
        checkChunkErrors(ifile);
        checkMorphErrors(ifile);
        checkDependencyErrors(ifile);
    }

    //Check POS Errors
    public void checkPOSErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        scf.intPOSCheck();
        scf.checkPOSErrors(ifile,errFile);
    }

    //Check Chunk Errors
    public void checkChunkErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        scf.intChunkCheck();
        scf.chunkBoundaryCheck(ifile, errFile);
        scf.checkChunkErrors(ifile,errFile);
    }

    //Check Morph Errors
    public void checkMorphErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        scf.intMorphCheck();
        scf.checkMorphErrors(ifile,errFile);
        scf.checkMorphATVErrors(ifile, errFile);
    }

    //Check Dependency Errors
    public void checkDependencyErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        scf.checkTreeErrors(ifile, errFile);
    }

    //Gets individual files from the directory.
    public void processDir(File ifile) throws IOException, Exception
    {
        if(ifile.isDirectory() == true)
        {
            File files[] = ifile.listFiles();
            Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return f1.getName().compareTo(f2.getName());
            } });

            for(int i = 0; i < files.length; i++)
            {
                processDir(files[i]);
            }
        }
        else if(ifile.isFile() == true)
        {
            System.out.println("Processing: "+ifile.getAbsolutePath());
            checkErrors(ifile);
            /*
            //if(ifile.toString().endsWith(".pos"))
            if(ifile.toString().contains(".pos"))
            {
                checkPOSErrors(ifile);
            }
             * */
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        SanityCheckAll sca = new SanityCheckAll();

        String input,output,lang,tools;

        if(args.length!=4)
        {
            input = "/home/ambati/ltrc/projects/treebanking/data/hindi/PropBank/null-inserted/third-40k-Null-22ndOct-DSCorrected";
            //input = "/home/ambati/ltrc/projects/treebanking/data/ToolsContest-2010/dataprep/hindi/testing/orig-ssf";
            //input = "/home/ambati/ltrc/projects/treebanking/data/hindi/second-25k-16-08-2010/data/2nd-half-aug-2010";
            //input = "/home/ambati/ltrc/projects/treebanking/tools/common/sample-data/fullnews_id_2504114_date_5_6_2004.utf8.cml.V.tkn.cml_updated.mo.pos.chnk.prun";
            output = "/home/ambati/ltrc/projects/treebanking/data/hindi/DS/errors/third-40k-null-dscorrected-sanity.txt";
            lang = "hin";
            tools = "/home/ambati/ltrc/tools/Sanchay/Sanchay-19-11-10/validation_tool/Sanity-Checks";
            
        }
        else
        {
            input=args[0];
            output=args[1];
            lang=args[2];
            tools=args[3];
        }
        
        sca.setErrFile(output);
        sca.setLanguage(lang);
        sca.setToolsDir(tools);
        File file = new File(input);
        sca.processDir(file);
    }
}