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
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author ambati
 */
public class SanityCheckPOS {

    private File errFile;   // Input Directory
    private File usefulDir;  // Tools program Directory
    private String lang;
    private Vector POSTags;

    public SanityCheckPOS()
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
    }

    //Check POS Errors
    public void checkPOSErrors(File ifile) throws IOException, InterruptedException, Exception
    {
        System.out.println("Processing: "+ifile);
        SanityCheckFuncs scf = new SanityCheckFuncs();
        scf.setLanguage(lang);
        scf.setToolsDir(usefulDir.getAbsolutePath());
        scf.inits();
        scf.intPOSCheck();
        scf.checkPOSErrors(ifile,errFile);
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
            //if(ifile.toString().endsWith(".pos"))
            if(ifile.toString().contains(".pos"))
            {
                checkPOSErrors(ifile);
            }
        }
    }

    public void inits()
    {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

        try {
            fsp.read("useful/data/props/fs-mandatory-attribs.txt",
                    "useful/data/props/fs-other-attribs.txt",
                    "useful/data/props/fs-props.txt",
                    "useful/data/props/ps-attribs.txt",
                    "useful/data/props/dep-attribs.txt",
                    "useful/data/props/sem-attribs.txt",
                    "UTF-8"); //throws java.io.FileNotFoundException;
            ssfp.read("useful/data/props/ssf-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;

            FeatureStructuresImpl.setFSProperties(fsp);
            SSFNode.setSSFProperties(ssfp);


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

    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        SanityCheckPOS scp = new SanityCheckPOS();

        String input,output,lang,tools;

        if(args.length!=4)
        {
            input = "/home/ambati/Download/mo.pos.chnk-set172/home/ambati/ltrc/projects/treebanking/data/hindi/40k-validated/1st-part";
            output = "/home/ambati/ltrc/projects/treebanking/data/hindi/errors/40k-1stpart-pos-sanity.txt";
            lang = "hin";
            tools = "/home/ambati/ltrc/projects/treebanking/tools/common/useful/";
        }
        else
        {
            input=args[0];
            output=args[1];
            lang=args[2];
            tools=args[3];
        }
        
        scp.setErrFile(output);
        scp.setLanguage(lang);
        scp.setToolsDir(tools);
        scp.inits();
        File file = new File(input);
        scp.processDir(file);
    }
}