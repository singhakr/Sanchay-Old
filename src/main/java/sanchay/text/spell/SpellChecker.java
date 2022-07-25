/*
 * SpellChecker.java
 *
 * Created on March 30, 2006, 8:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text.spell;

import java.io.*;
import java.awt.*;
import java.util.regex.*;
import javax.swing.*;

import sanchay.GlobalProperties;
import sanchay.corpus.ssf.tree.*;
import sanchay.util.*;

/**
 *
 * @author anil
 */
public class SpellChecker {
    protected SpellCheckerOptions spellCheckerOptions;

    protected File inDirectoryFile;
    protected File outDirectoryFile;
    
    protected DefaultComboBoxModel fileList;
    
    protected JList fileJList;
    protected JPanel contentJPanel;

    // For batch mode
    protected File currentFile;

    // For Chunked and SSFTagged corpus
    protected int currentSentence;
    protected SSFNode currentParentNode;
    protected int currentChildIndex;
    
    protected Pattern pattern;
    protected Matcher matcher;

    protected Color highlightColor;
    
    public static final int NOT_FOUND = 0;
    
    /** Creates a new instance of SpellChecker */
    public SpellChecker(SpellCheckerOptions options, JPanel contentPanel)
            throws FileNotFoundException, IOException
    {
	super();

	spellCheckerOptions = options;
	spellCheckerOptions.standAlone = false;
	
	contentJPanel = contentPanel;

	prepare(options);
    }

    public SpellChecker(SpellCheckerOptions options, JList jlist, JPanel contentPanel)
            throws FileNotFoundException, IOException
    {
	super();
	
	spellCheckerOptions = options;
	spellCheckerOptions.standAlone = true;
	
	fileJList = jlist;
	contentJPanel = contentPanel;

	prepare(options);
   }
    
    private void prepare(SpellCheckerOptions options)
            throws FileNotFoundException, IOException
    {
	spellCheckerOptions = options;
	
	inDirectoryFile = new File(spellCheckerOptions.inDirectory);
	outDirectoryFile = new File(spellCheckerOptions.outDirectory);
	
	highlightColor = new Color(Color.YELLOW.getRGB());
	
	if(spellCheckerOptions.batchMode)
	{
	    fileList = new DefaultComboBoxModel();
	    fileJList.setModel(fileList);
	    
	    listAllBatch(inDirectoryFile);
	}
    }

    // Could be directory as well as file
    private File getOutputFile(File inDirFile) throws FileNotFoundException, IOException
    {
	if(spellCheckerOptions.outDirectory == null || spellCheckerOptions.outDirectory.equals(""))
	    return null;
	
        File odFile = null;
        
        if(spellCheckerOptions.recreateDirStr)
        {
            String topInPath = inDirectoryFile.getAbsolutePath();
            String inPath = inDirFile.getAbsolutePath();
            String inPathSuffix = inPath.replaceFirst(topInPath, "");

            odFile = new File(outDirectoryFile, inPathSuffix);
        }
        else if(inDirFile.isDirectory())
            odFile = outDirectoryFile;
        else
        {
            String topInPath = inDirectoryFile.getAbsolutePath();
            String inPath = inDirFile.getAbsolutePath();
            String inPathSuffix = inPath.replaceFirst(topInPath, "");
            inPathSuffix = inPathSuffix.replaceFirst(File.separator, "");
            inPathSuffix = inPathSuffix.replaceAll(File.separator, "-");

            odFile = new File(outDirectoryFile, inPathSuffix);
        }

        return odFile;
    }
    
    private File setupOutputDirectory(File outDirFile, boolean cln) throws FileNotFoundException, IOException
    {
	if(spellCheckerOptions.outDirectory == null || spellCheckerOptions.outDirectory.equals(""))
	    return null;

	if(outDirFile.exists() == true)
        {
            if(outDirFile.canWrite() == false)
                throw new FileNotFoundException(GlobalProperties.getIntlString("No_write_permission_for_directory:_")+ outDirFile.getAbsolutePath());
            else if(outDirFile.isDirectory() == false)
                throw new FileNotFoundException(GlobalProperties.getIntlString("Not_a_directory:_") + outDirFile.getAbsolutePath());
        }
        else
        {
            // Create directory:
            System.out.println(GlobalProperties.getIntlString("Creating_directory:_") + outDirFile.getAbsolutePath());
            
            outDirFile.mkdirs();
        }
        
        if(outDirFile.getAbsolutePath().equals(outDirectoryFile.getAbsolutePath()) && cln == true)
        {
            UtilityFunctions.removeDirectoryRecursive(outDirFile);
            outDirFile.mkdirs();
        }
        
        return outDirFile;
    }

    // For listing all the documents in the input directory
    protected int listAllBatch(File inDir) throws FileNotFoundException, IOException
    {
	int fileCount = NOT_FOUND;
	
        if(inDir.isFile() == true)
        {
	    fileList.addElement(inDir);

	    return 1;
        }
        else
        {
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    fileCount += listAllBatch(files[i]);
                }
            }
        }
        
	return fileCount;
    }    
    
    public int saveOutput(File inDir) throws FileNotFoundException, IOException
    {
	    
	return NOT_FOUND;
    }
}
