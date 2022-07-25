package sanchay.util;

import java.io.*;

import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;

public class FileSplitter {

    protected FileSplittingOptions fileSplittingOptions;

    protected File inDirectoryFile;
    protected File outDirectoryFile;

    protected long currentSize; // Current combined size of split files
    protected long currentTotalFileCount; // Current count of split files: the count of split chunks (output, not input)

    protected long progressUnit = 1000;

    public FileSplitter(FileSplittingOptions options)
            throws FileNotFoundException, IOException
    {
        super();

        // TODO Auto-generated constructor stub
        prepare(options);
    }
    
    private void prepare(FileSplittingOptions options)
            throws FileNotFoundException, IOException
    {
	fileSplittingOptions = options;
	
	currentSize = 0;
	currentTotalFileCount = 0;
	
	inDirectoryFile = new File(fileSplittingOptions.inDirectory);
	outDirectoryFile = new File(fileSplittingOptions.outDirectory);

	progressUnit = 1000;
    }

    // Could be directory as well as file
    private File getOutputFile(File inDirFile) throws FileNotFoundException, IOException
    {
        File odFile = null;
        
        if(fileSplittingOptions.recreateDirStr)
        {
            String topInPath = inDirectoryFile.getAbsolutePath();
            String inPath = inDirFile.getAbsolutePath();

            String inPathSuffix = inPath.substring(topInPath.length());

            odFile = new File(outDirectoryFile, inPathSuffix);
        }
        else if(inDirFile.isDirectory())
            odFile = outDirectoryFile;
        else
        {
            String topInPath = inDirectoryFile.getAbsolutePath();
            String inPath = inDirFile.getAbsolutePath();
            String inPathSuffix = inPath.substring(topInPath.length());
            inPathSuffix = inPathSuffix.replaceFirst(File.separator, "");
            inPathSuffix = inPathSuffix.replaceAll(File.separator, "-");

            odFile = new File(outDirectoryFile, inPathSuffix);
        }

        return odFile;
    }
    
    private File setupOutputDirectory(File outDirFile, boolean cln) throws FileNotFoundException, IOException
    {
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
    
    // prepare() must be called before any split operation
    public void splitFilesBatch() throws FileNotFoundException,
    IOException
    {
        switch(fileSplittingOptions.unit)
        {
            case FileSplittingOptions.BY_CHAR:
                splitFilesBatchByChars(inDirectoryFile);
                break;
            case FileSplittingOptions.BY_WORD:
                splitFilesBatchByWords(inDirectoryFile);
                break;
            case FileSplittingOptions.BY_SENTENCE:
                splitFilesBatchBySentences(inDirectoryFile);
                break;
            case FileSplittingOptions.BY_PARAGRAPH:
                splitFilesBatchByParagraphs(inDirectoryFile);
                break;
            default:
                splitFilesBatchByParagraphs(inDirectoryFile);
                break;
        }
	
	System.out.println(GlobalProperties.getIntlString("Total_output_files:_") + currentTotalFileCount);
	System.out.println(GlobalProperties.getIntlString("Total_output_size:_") + currentSize);
    }
    
    public int splitFilesBatchByChars(File inDir) throws FileNotFoundException,
    IOException
    {
        if(inDir.isFile() == true)
        {
            if(splitFileByChars(inDir) == -1)
                return -1;
        }
        else
        {
            File outDir = getOutputFile(inDir);
            setupOutputDirectory(outDir, fileSplittingOptions.clean);
            
            if(inDir.isDirectory() == true && outDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    if(splitFilesBatchByChars(files[i]) == -1)
                        return -1;
                }

            }
        }
        
        return 0;
    }
    
    public int splitFileByChars(File inFile) throws FileNotFoundException,
    IOException
    {
        return splitFile(inFile, FileSplittingOptions.BY_CHAR);
    }
    
    public int splitFilesBatchByWords(File inDir) throws FileNotFoundException,
    IOException
    {
        if(inDir.isFile() == true)
        {
            if(splitFileByWords(inDir) == -1)
                return -1;
        }
        else
        {
            File outDir = getOutputFile(inDir);
            setupOutputDirectory(outDir, fileSplittingOptions.clean);
            
            if(inDir.isDirectory() == true && outDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    if(splitFilesBatchByWords(files[i]) == -1)
                        return -1;
                }

            }
        }
        
        return 0;
    }
    
    public int splitFileByWords(File inFile) throws FileNotFoundException,
    IOException
    {
        return splitFile(inFile, FileSplittingOptions.BY_WORD);
    }
    
    public int splitFilesBatchBySentences(File inDir) throws FileNotFoundException,
    IOException
    {
        if(inDir.isFile() == true)
        {
            if(splitFileBySentences(inDir) == -1)
                return -1;
        }
        else
        {
            File outDir = getOutputFile(inDir);
            setupOutputDirectory(outDir, fileSplittingOptions.clean);
            
            if(inDir.isDirectory() == true && outDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    if(splitFilesBatchBySentences(files[i]) == -1)
                        return -1;
                }

            }
        }
        
        return 0;
    }
    
    public int splitFileBySentences(File inFile) throws FileNotFoundException,
    IOException
    {
        return splitFile(inFile, FileSplittingOptions.BY_SENTENCE);
    }
    
    public int splitFilesBatchByParagraphs(File inDir) throws FileNotFoundException,
    IOException
    {
        if(inDir.isFile() == true)
        {
            if(splitFileByParagraphs(inDir) == -1)
                return -1;
        }
        else
        {
            File outDir = getOutputFile(inDir);
            setupOutputDirectory(outDir, fileSplittingOptions.clean);
            
            if(inDir.isDirectory() == true && outDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
                    if(splitFilesBatchByParagraphs(files[i]) == -1)
                        return -1;
                }

            }
        }
        
        return 0;
    }
    
    public int splitFileByParagraphs(File inFile) throws FileNotFoundException,
    IOException
    {
        return splitFile(inFile, FileSplittingOptions.BY_PARAGRAPH);
        
//        if(inFile.isFile() == true)
//        {
//            File outFile = getOutputFile(inFile);
//            PrintStream ps = null;
//
//            BufferedReader lnReader = null;
//            
//            if(charset.equals("") == false)
//                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), charset));
//            else
//                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
//            
//            int progressUnit = 1000;
//
//            long progressUnits;
//            long i = 0; // Current position in the input file
//            long j = 0; // Current position in the output file
//            long filecount = 0;
//
//            String fpath = "";
//            String line;
//            
//            while((line = lnReader.readLine()) != null)
//            {
//                if(line.equals("") == false)
//                {
//                    j++;
//                    currentSize++;
//                    
//                    if(i++ % splitSize == 0)
//                    {
//                        fpath = outFile.getAbsolutePath() + "-" + (++filecount);
//                        ps = new PrintStream(fpath, charset);
//                        
//                        System.out.println("Processed " + filecount + " parts of " + inFile.getAbsolutePath() + ".\n");
//                        
//                        if(j > 0)
//                            j = 1;
//                    }
//
//                    if(currentSize > maxSize)
//                    {
//                        File oof = new File(fpath);
//                        
//                        if(allowLastSmaller == false && j > 0 && j < splitSize)
//                        {
//                            oof.delete();
//                            currentSize -= j;
//                            return -2;
//                        }
//                        else if(oof.length() == 0)
//                            oof.delete();
//                            
//                        return -1;
//                    }
//                        
//                    ps.println(line);
//
//                    if(currentSize % progressUnit == 0)
//                    {
//                        progressUnits = currentSize / progressUnit;
//
//                        if(progressUnits > 0)
//                        {
//                            System.out.println("Processed " + progressUnits + " thousand paragraphs.\n");
//                        }
//                    }
//                }
//            }
//
//            if(allowLastSmaller == false && j > 0 && j < splitSize)
//            {
//                File oof = new File(fpath);
//                oof.delete();
//                currentSize -= j;
//
//                return -2;
//            }
//        }
//        
//        return 0;
    }

    // The new current size when a new paragaph is added (total, for the input file, or for the output file)
    private long getNewSize(long curSz, String newParagraph, int unit)
    {
        long sz = curSz;
        
        switch(unit)
        {
            case FileSplittingOptions.BY_CHAR:
                sz += newParagraph.length();
                break;
            case FileSplittingOptions.BY_WORD:
                sz += newParagraph.split("[ ]").length;
                break;
            case FileSplittingOptions.BY_SENTENCE:
                sz++;
                break;
            case FileSplittingOptions.BY_PARAGRAPH:
                sz++;
                break;
            default:
                sz++;
                break;
        }
        
        return sz;
    }

    // The new current size when a new SSFSentence added (total, for the input file, or for the output file)
    private long getNewSize(long curSz, SSFSentence newSSFSen, int unit)
    {
        long sz = curSz;
        
        switch(unit)
        {
            case FileSplittingOptions.BY_CHAR:
                sz += newSSFSen.getRoot().makeRawSentence().length();
                break;
            case FileSplittingOptions.BY_WORD:
                sz += newSSFSen.getRoot().getAllLeaves().size();
                break;
            case FileSplittingOptions.BY_SENTENCE:
                sz++;
                break;
            case FileSplittingOptions.BY_PARAGRAPH:
                sz++;
                break;
            default:
                sz++;
                break;
        }
        
        return sz;
    }
    
    private String getUnitName(int unit)
    {
        switch(unit)
        {
            case FileSplittingOptions.BY_CHAR:
                return GlobalProperties.getIntlString("characters");
            case FileSplittingOptions.BY_WORD:
                return GlobalProperties.getIntlString("words");
            case FileSplittingOptions.BY_SENTENCE:
                return GlobalProperties.getIntlString("sentences");
            case FileSplittingOptions.BY_PARAGRAPH:
                return GlobalProperties.getIntlString("paragraphs");
        }
        
        return "";
    }
    
    public static int getUnitFromString(String unitName)
    {
	if(unitName.equalsIgnoreCase(GlobalProperties.getIntlString("character")))
	    return FileSplittingOptions.BY_CHAR;
	else if(unitName.equalsIgnoreCase(GlobalProperties.getIntlString("word")))
	    return FileSplittingOptions.BY_WORD;
	else if(unitName.equalsIgnoreCase(GlobalProperties.getIntlString("sentence")))
	    return FileSplittingOptions.BY_SENTENCE;
	else if(unitName.equalsIgnoreCase(GlobalProperties.getIntlString("paragraph")))
	    return FileSplittingOptions.BY_PARAGRAPH;

        return -1;
    }
    
    private int splitFile(File inFile, int unit) throws FileNotFoundException, IOException
    {
	if(fileSplittingOptions.readCorpusType == CorpusType.RAW)
	    return splitFileRaw(inFile, unit);
	else if(fileSplittingOptions.readCorpusType == CorpusType.POS_TAGGED
		|| fileSplittingOptions.readCorpusType == CorpusType.CHUNKED
		|| fileSplittingOptions.readCorpusType == CorpusType.SSF_FORMAT)
	    return splitFileSSF(inFile, unit);
	else if(fileSplittingOptions.readCorpusType == CorpusType.XML_FORMAT)
	    return splitFileXML(inFile, unit);

        return -1;
    }
    
    private int splitFileRaw(File inFile, int unit) throws FileNotFoundException, IOException
    {
        if(inFile.isFile() == true)
        {
            File outFile = getOutputFile(inFile);
            PrintStream ps = null;

            BufferedReader lnReader = null;
            
            if(fileSplittingOptions.charset.equals("") == false)
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), fileSplittingOptions.charset));
            else
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            
	    SplitLineArgs splitLineArgs = new SplitLineArgs();
	    
	    splitLineArgs.unit = unit;
	    splitLineArgs.inFile = inFile;
	    splitLineArgs.outFile = outFile;
	    splitLineArgs.ps = ps;
            splitLineArgs.i = 0; // Current position in the input file
            splitLineArgs.j = 0; // Current position in the output file
            splitLineArgs.oldj = 0;
            splitLineArgs.filecount = 0;

            splitLineArgs.fpath = "";
	    
	    String line = "";
            
            while((line = lnReader.readLine()) != null)
            {
                if(line.equals("") == false)
                {
		    int ret = 0;
		    
		    if(fileSplittingOptions.exactSize == false || unit == FileSplittingOptions.BY_PARAGRAPH)
		    {
			splitLineArgs.line = line;
			ret = splitLineRaw(splitLineArgs);
			
			if(ret != 0) return ret;
		    }
		    
		    if(fileSplittingOptions.exactSize)
		    {
			if(unit == FileSplittingOptions.BY_SENTENCE)
			{
			    splitLineArgs.line = line;
			    ret = splitLineRaw(splitLineArgs);

			    if(ret != 0) return ret;
			}
			else if(unit == FileSplittingOptions.BY_WORD)
			{
			    String words[] = line.split("[ ]");

			    for (int i = 0; i < words.length; i++)
			    {
				splitLineArgs.line = words[i];
				ret = splitLineRaw(splitLineArgs);

				if(ret != 0) return ret;

				splitLineArgs.ps.print(" ");
			    }
			}
			else if(unit == FileSplittingOptions.BY_CHAR)
			{
			    for (int i = 0; i < line.length(); i++)
			    {
				char ch = line.charAt(i);
				splitLineArgs.line = new String(new char[]{ch});
				ret = splitLineRaw(splitLineArgs);

				if(ret != 0) return ret;
			    }
			}
		    }
		    
		    splitLineArgs.ps.print("\n");
                }
            }

            if(fileSplittingOptions.allowLastSmaller == false && splitLineArgs.j > 0 && splitLineArgs.j < fileSplittingOptions.splitSize)
            {
                File oof = new File(splitLineArgs.fpath);
                oof.delete();
                currentSize -= splitLineArgs.j;
		currentTotalFileCount--;

                return -2;
            }
        }
        
        return 0;
    }
    
    private int splitLineRaw(SplitLineArgs splitLineArgs) throws FileNotFoundException, IOException
    {
	splitLineArgs.oldj = splitLineArgs.j;
	splitLineArgs.j = getNewSize(splitLineArgs.j, splitLineArgs.line, splitLineArgs.unit);
	currentSize = getNewSize(currentSize, splitLineArgs.line, splitLineArgs.unit);

	if( splitLineArgs.i == 0 || splitLineArgs.oldj >= fileSplittingOptions.splitSize)
	{
	    splitLineArgs.fpath = splitLineArgs.outFile.getAbsolutePath() + "-" + (++splitLineArgs.filecount);
	    splitLineArgs.ps = new PrintStream(splitLineArgs.fpath, fileSplittingOptions.charset);
	    currentTotalFileCount++;

	    System.out.println(GlobalProperties.getIntlString("Processed_") + splitLineArgs.filecount + GlobalProperties.getIntlString("_parts_of_") + splitLineArgs.inFile.getAbsolutePath() + ".\n");

	    if(splitLineArgs.j > 0)
	    {
		splitLineArgs.j = getNewSize(0, splitLineArgs.line, splitLineArgs.unit);
		splitLineArgs.oldj = 0;
	    }
	}

	splitLineArgs.i = getNewSize(splitLineArgs.i, splitLineArgs.line, splitLineArgs.unit);

	if(fileSplittingOptions.readCorpusType == CorpusType.RAW && fileSplittingOptions.writeCorpusType == CorpusType.RAW)
	    splitLineArgs.ps.print(splitLineArgs.line);
	else
	{
	    splitLineArgs.ps.print(SSFSentenceImpl.convertSentenceString(splitLineArgs.line, fileSplittingOptions.readCorpusType, fileSplittingOptions.writeCorpusType));
	}

	if(currentSize > fileSplittingOptions.maxSize)
	{
	    File oof = new File(splitLineArgs.fpath);

	    if(fileSplittingOptions.allowLastSmaller == false && splitLineArgs.j > 0 && splitLineArgs.j < fileSplittingOptions.splitSize)
	    {
		oof.delete();
		currentSize -= splitLineArgs.j;
		currentTotalFileCount--;
		return -2;
	    }
	    else if(oof.length() == 0)
	    {
		currentTotalFileCount--;
		oof.delete();
	    }

	    return -1;
	}

	if(currentSize % progressUnit == 0)
	{
	    splitLineArgs.progressUnits = currentSize / progressUnit;

	    if(splitLineArgs.progressUnits > 0)
	    {
		System.out.println(GlobalProperties.getIntlString("Processed_") + splitLineArgs.progressUnits + GlobalProperties.getIntlString("_thousand_") + getUnitName(splitLineArgs.unit) + ".\n");
	    }
	}
	
	return 0;
    }
    
    private int splitFileSSF(File inFile, int unit) throws FileNotFoundException, IOException
    {
        if(inFile.isFile() == true)
        {
            File outFile = getOutputFile(inFile);

	    SSFStory story = new SSFStoryImpl();

	    try {

		story.readFile(inFile.getAbsolutePath(), fileSplittingOptions.charset, fileSplittingOptions.readCorpusType);
		
		if(fileSplittingOptions.reallocateSentenceIDs)
		    story.reallocateSentenceIDs();

		if(fileSplittingOptions.reallocateNodeIDs)
		    story.reallocateNodeIDs();
		
		if(fileSplittingOptions.clearAnnotationLevelsFlag != SSFCorpus.NONE)
		    story.clearAnnotation(fileSplittingOptions.clearAnnotationLevelsFlag);
		
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }

	    PrintStream ps = null;

	    long progressUnits;
	    long i = 0; // Current position in the input file
	    long j = 0; // Current position in the output file
	    long oldj = 0;
	    long filecount = 0;

	    String fpath = "";

	    int count = story.countSentences();

	    for (int s = 0; s < count; s++)
	    {
		SSFSentence sentence = story.getSentence(s);

		if(sentence != null & sentence.getRoot() != null && sentence.getRoot().countChildren() > 0)
		{
		    oldj = j;
		    j = getNewSize(j, sentence, unit);
		    currentSize = getNewSize(currentSize, sentence, unit);

		    if( i == 0 || oldj >= fileSplittingOptions.splitSize)
		    {
			fpath = outFile.getAbsolutePath() + "-" + (++filecount);
			ps = new PrintStream(fpath, fileSplittingOptions.charset);
			currentTotalFileCount++;

			System.out.println(GlobalProperties.getIntlString("Processed_") + filecount + GlobalProperties.getIntlString("_parts_of_") + inFile.getAbsolutePath() + ".\n");

			if(j > 0)
			{
			    j = getNewSize(0, sentence, unit);
			    oldj = 0;
			}
		    }

		    i = getNewSize(i, sentence, unit);

		    sentence.print(ps, fileSplittingOptions.writeCorpusType);

		    if(currentSize > fileSplittingOptions.maxSize)
		    {
			File oof = new File(fpath);

			if(fileSplittingOptions.allowLastSmaller == false && j > 0 && j < fileSplittingOptions.splitSize)
			{
			    oof.delete();
			    currentSize -= j;
			    currentTotalFileCount--;
			    return -2;
			}
			else if(oof.length() == 0)
			{
			    currentTotalFileCount--;
			    oof.delete();
			}

			return -1;
		    }

		    if(currentSize % progressUnit == 0)
		    {
			progressUnits = currentSize / progressUnit;

			if(progressUnits > 0)
			{
			    System.out.println(GlobalProperties.getIntlString("Processed_") + progressUnits + GlobalProperties.getIntlString("_thousand_") + getUnitName(unit) + ".\n");
			}
		    }
		}
	    }

	    if(fileSplittingOptions.allowLastSmaller == false && j > 0 && j < fileSplittingOptions.splitSize)
	    {
		File oof = new File(fpath);
		oof.delete();
		currentSize -= j;
		currentTotalFileCount--;

		return -2;
	    }
	}
        
        return 0;
    }
    
    private int splitFileXML(File inFile, int unit) throws FileNotFoundException, IOException
    {
	return -1;
    }
    
    public void cleanup() throws FileNotFoundException, IOException
    {
        UtilityFunctions.removeEmptyDirectoryRecursive(outDirectoryFile);
    }
    
    public static boolean validateCorpusType(File inDir, String charset, CorpusType ctype)
    {
        if(inDir.isFile() == true)
        {
	    if(ctype == CorpusType.RAW)
		return true;
	    else if(SimpleStoryImpl.getCorpusType(inDir, charset) != ctype)
                return false;
        }
        else
        {
            if(inDir.isDirectory() == true)
            {
                File files[] = inDir.listFiles();

                for(int i = 0; i < files.length; i++)
                {
		    if(FileSplitter.validateCorpusType(files[i], charset, ctype) == false)
		    {
			System.out.println(GlobalProperties.getIntlString("File_") + files[i] + GlobalProperties.getIntlString("_not_of_corpus_type_") + ctype.toString());
			return false;
		    }
                }

            }
        }

	return true;
    }
    
    public static boolean validateCorpusType(String inPath, String charset, CorpusType ctype)
    {
	if(inPath == null || inPath.equals(""))
	    return false;

	if(charset == null | charset.equals(""))
	    return validateCorpusType(new File(inPath), GlobalProperties.getIntlString("UTF-8"), ctype);

	return validateCorpusType(new File(inPath), charset, ctype);
    }
    
    class SplitLineArgs
    {
	public int unit;
	public File inFile;
	public File outFile;
	public PrintStream ps;
	public long progressUnits;
	public long i = 0; // Current position in the input file
	public long j = 0; // Current position in the output file
	public long oldj = 0;
	public long filecount = 0;

	public String fpath = "";
	public String line;
   }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
	
	FileSplittingOptions options = new FileSplittingOptions();

	options.inDirectory = "/home/anil/tmp/fileSplitTrial/indir"; /*inDir*/
	options.outDirectory = "/home/anil/tmp/fileSplitTrial/outdir"; /*outDir*/

	options.charset = GlobalProperties.getIntlString("UTF-8"); /*charset*/
	options.splitSize = 4; /*splitSize*/
	options.allowLastSmaller = false; /*allowLastSmaller*/
	options.clean = true; /*clean*/
	options.recreateDirStr = true ; /*recreateDirStr*/
	options.maxSize = 40; /*maxSize*/

        try {
	    FileSplitter fileSplitter = new FileSplitter(options);
            
            fileSplitter.splitFilesBatch();
            fileSplitter.cleanup();
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
