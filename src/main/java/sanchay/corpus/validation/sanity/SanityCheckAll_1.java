/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.validation.sanity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author ambati
 */
public class SanityCheckAll_1 {

    private File errFile;   // Input Directory
    private File usefulDir;  // Tools program Directory
    private String lang;
    private Vector POSTags;
    private SanityCheckFuncs_1 scf;

    public SanityCheckAll_1()
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

   
    public void setErrFile(String file, String lines) throws IOException
    {
        errFile = new File(file);
        if(errFile.exists())
        {
            errFile.delete();
        }
        
        BufferedWriter out = new BufferedWriter(new FileWriter(errFile,true));
        out.write(lines);
        out.close();
    }

    public void checkFuncsInits() throws FileNotFoundException, IOException
    {
        scf = new SanityCheckFuncs_1();
        scf.setLanguage(lang);
        scf.setToolsDir(usefulDir.getAbsolutePath());
        scf.inits();
    }

    public void checkErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        //System.out.println("Processing: "+ifile);
        
        checkFuncsInits();
        checkChunkErrors(ifile);
        checkMorphErrors(ifile);
    //    checkDependencyErrors(ifile);
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
        scf.chunkBoundaryCheck(ifile, errFile);
      
    }

    //Check Morph Errors
    public void checkMorphErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        scf.intMorphCheck();
//        scf.checkMorphErrors(ifile,errFile);
        scf.checkMorphATVErrors(ifile, errFile);
    }

    //Check Dependency Errors
    public void checkDependencyErrors(File ifile) throws IOException, InterruptedException, Exception
    {
//        scf.checkTreeErrors(ifile, errFile);
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
                if(!files[i].getName().endsWith(".bak") && !files[i].getName().endsWith(".comments"))
                {
                    processDir(files[i]);
         //           System.exit(0);
                }
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
        SanityCheckAll_1 sca = new SanityCheckAll_1();

        String input,output,lang,tools;

        if(args.length!=4)
        {
            //input = "C:\\Users\\Rahul\\Documents\\Academic\\Projects\\Treebank Validation Project\\Data\\mo.pos.chnk.prun.posn-set199";
            //input = "/home/ambati/ltrc/projects/treebanking/data/ToolsContest-2010/dataprep/hindi/testing/orig-ssf";
            //input = "/home/ambati/ltrc/projects/treebanking/data/hindi/second-25k-16-08-2010/data/2nd-half-aug-2010";
            //input = "/home/ambati/ltrc/projects/treebanking/tools/common/sample-data/fullnews_id_2504114_date_5_6_2004.utf8.cml.V.tkn.cml_updated.mo.pos.chnk.prun";
            //input = "/home/rahul/winC/Users/Rahul/Documents/Academic/Projects/Treebank Validation Project/Data/mo.pos.chnk.prun.posn-set199/fullnews_id_2583231_date_9_7_2004.utf8.cml.V.tkn.cml_updated.mo.pos.chnk.prun.posn";
            input = "/home/rahul/winC/Users/Rahul/Documents/Academic/Projects/Treebank Validation Project/Data/mo.pos.chnk.prun.posn-set199/fullnews_id_2583229_date_9_7_2004.utf8.cml.V.tkn.cml_updated.mo.pos.chnk.prun.posn";
            //output = "C:\\Users\\Rahul\\Documents\\Academic\\Projects\\Treebank Validation Project\\sanity_errors.txt";
            output = "/home/rahul/winC/Users/Rahul/Documents/Academic/Projects/Treebank Validation Project/sanity_errors.txt";
            lang = "hin";
            //tools = "/home/ambati/ltrc/tools/Sanchay/Sanchay-19-11-10/validation_tool/Sanity-Checks";
            //tools="C:\\Users\\Rahul\\Documents\\Academic\\Projects\\Treebank Validation\\ Project\\Sanchay\\Sanchay-19-11-10\\src\\sanchay\\corpus\\validation\\sanity";
            tools="/home/rahul/winC/Users/Rahul/Documents/Academic/Projects/Treebank Validation Project/Sanchay/Sanchay-19-11-10/src/sanchay/corpus/validation/sanity";
            
        }
        else
        {
            input=args[0];
            output=args[1];
            lang=args[2];
            tools=args[3];
        }
        
        sca.setErrFile(output,"");
        sca.setLanguage(lang);
        sca.setToolsDir(tools);
        File file = new File(input);
        sca.processDir(file);
    }
}