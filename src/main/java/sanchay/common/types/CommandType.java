/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.common.types;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import jmathlib.toolbox.io.systemcommand;
import sanchay.GlobalProperties;
import sanchay.gui.shell.commands.engines.AccessResources;
import sanchay.gui.shell.commands.engines.AlignSentences;
import sanchay.gui.shell.commands.engines.AlignWords;
import sanchay.gui.shell.commands.engines.Annotate;
import sanchay.gui.shell.commands.engines.BuildWordList;
import sanchay.gui.shell.commands.engines.ChangeDirectory;
import sanchay.gui.shell.commands.engines.CleanHTML;
import sanchay.gui.shell.commands.engines.CloseTab;
import sanchay.gui.shell.commands.engines.CommandHelp;
import sanchay.gui.shell.commands.engines.CompileContextModels;
import sanchay.gui.shell.commands.engines.CompileNGramModel;
import sanchay.gui.shell.commands.engines.ConcatenateFiles;
import sanchay.gui.shell.commands.engines.ConvertEncoding;
import sanchay.gui.shell.commands.engines.ConvertFormat;
import sanchay.gui.shell.commands.engines.EstimateDeepSimilarity;
import sanchay.gui.shell.commands.engines.EstimateFunctionalSimilarity;
import sanchay.gui.shell.commands.engines.EstimateSurfaceSimilarity;
import sanchay.gui.shell.commands.engines.EvaluateExpression;
import sanchay.gui.shell.commands.engines.ExecuteJavaCode;
import sanchay.gui.shell.commands.engines.ExecutePythonCode;
import sanchay.gui.shell.commands.engines.ExecuteSystemCommand;
import sanchay.gui.shell.commands.engines.FileDifference;
import sanchay.gui.shell.commands.engines.GetCorpusStatistics;
import sanchay.gui.shell.commands.engines.GetScriptStatistics;
import sanchay.gui.shell.commands.engines.IdentifyLanguageEncoding;
import sanchay.gui.shell.commands.engines.IndexDocuments;
import sanchay.gui.shell.commands.engines.ListFiles;
import sanchay.gui.shell.commands.engines.NewTab;
import sanchay.gui.shell.commands.engines.NormalizeText;
import sanchay.gui.shell.commands.engines.PresentWorkingDirectory;
import sanchay.gui.shell.commands.engines.RetrieveDocuments;
import sanchay.gui.shell.commands.engines.SegmentMorphemes;
import sanchay.gui.shell.commands.engines.Shutdown;
import sanchay.gui.shell.commands.engines.SplitFiles;
import sanchay.gui.shell.commands.engines.Tokenize;
import sanchay.gui.shell.commands.engines.Translate;
import sanchay.gui.shell.commands.engines.TranslateNumberExpressions;
import sanchay.gui.shell.commands.engines.Transliterate;
import sanchay.gui.shell.commands.engines.WGet;

/**
 *
 * @author anil
 */
public class CommandType extends SanchayType implements Serializable {

    public final int ord;
    protected String longName;
    protected String description;
    protected Class commandEngineClass;
    private static Vector types = new Vector();

    /** Creates a new instance of CommandType */
    protected CommandType(Class commandEngineClass, String longName, String desc, String id, String pk) {
        super(id, pk);

        if (CommandType.last() != null) {
            this.prev = CommandType.last();
            this.longName = longName;
            this.description = desc;
            CommandType.last().next = this;
        }

        types.add(this);
	ord = types.size();
    }

    /**
     * @return the longName
     */
    public String getLongName()
    {
        return longName;
    }

    /**
     * @param longName the longName to set
     */
    public void setLongName(String longName)
    {
        this.longName = longName;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the commandEngineClass
     */
    public Class getCommandEngineClass()
    {
        return commandEngineClass;
    }

    public static int size()
    {
        return types.size();
    }

    public static SanchayType first()
    {
        return (SanchayType) types.get(0);
    }

    public static SanchayType last()
    {
        if(types.size() > 0)
            return (SanchayType) types.get(types.size() - 1);

        return null;
    }

    public static SanchayType getType(int i)
    {
        if(i >=0 && i < types.size())
            return (SanchayType) types.get(i);

        return null;
    }

    public static Enumeration elements()
    {
        return new TypeEnumerator(CommandType.first());
    }


    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = CommandType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = CommandType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public String toString() {
        return id;
    }

    public static final CommandType NEW_TAB = new CommandType(NewTab.class, "New Tab", "Open a new tab based on the arguments given", "new", "null");
    public static final CommandType PWD = new CommandType(PresentWorkingDirectory.class, "Present Working Directory", "Print the present working directory", "pwd", "null");
    public static final CommandType LIST_FILES = new CommandType(ListFiles.class, "List Files", "List files", "ls", "null");
    public static final CommandType CD = new CommandType(ChangeDirectory.class, "Change Directory", "Change to a different directory", "cd", "null");
    public static final CommandType CAT = new CommandType(ConcatenateFiles.class, "Display and Concatenate", "Display and concatenate files", "cat", "null");
    public static final CommandType EXEC = new CommandType(ExecuteSystemCommand.class, "System Command", "Execute a system command", "exec", "null");
    public static final CommandType WGET = new CommandType(WGet.class, "Download Web Data", "Download web data from the given URL", "wget", "null");
    public static final CommandType CLEAN_HTML = new CommandType(CleanHTML.class, "Clean HTML", "Clean HTML files for corpus creation", "cleanh", "null");
    public static final CommandType EVAL = new CommandType(EvaluateExpression.class, "Evaluate", "Evaluate the given expression", "eval", "null");
    public static final CommandType DIFF = new CommandType(FileDifference.class, "File Difference", "View the file difference based on the given arguments", "eval", "null");
    public static final CommandType SPLIT = new CommandType(SplitFiles.class, "File Split", "Split files based on the given arguments", "split", "null");
    public static final CommandType ACCESS = new CommandType(AccessResources.class, "Access Resource", "Access a resource (query and transform)", "eval", "null");
    public static final CommandType INDEX = new CommandType(IndexDocuments.class, "Index Documents", "Index documents using Lucene", "index", "null");
    public static final CommandType RETRIEVE = new CommandType(RetrieveDocuments.class, "Retrieve Documents", "Retrieve documents using Lucene", "retrieve", "null");
    public static final CommandType IDENTIFY_LE = new CommandType(IdentifyLanguageEncoding.class, "Identify Language and Encoding", "Identify the language and encoding of the given text or documents", "identifyle", "null");
    public static final CommandType TOKENIZE = new CommandType(Tokenize.class, "Tokenize", "Tokenize the text according to the arguments given", "tokenize", "null");
    public static final CommandType WORD_LIST = new CommandType(BuildWordList.class, "Build Word List", "Build a word list from the given sources", "wlist", "null");
    public static final CommandType NGRAM = new CommandType(CompileNGramModel.class, "NGram Model", "Build an n-gram model of a certain kind from the given sources", "ngram", "null");
    public static final CommandType CONTEXT_MODEL = new CommandType(CompileContextModels.class, "Build Context Models", "Build certain kinds of context models from the given sources", "contextm", "null");
    public static final CommandType TEXT_NORMALIZE = new CommandType(NormalizeText.class, "Normalize Text", "Normalizing the spelling of text or documents and performing some other operations", "textnorm", "null");
    public static final CommandType ANNOTATE = new CommandType(Annotate.class, "Automatically Annotate", "Automatically annotate text using the specified method and configuration", "annotate", "null");
    public static final CommandType SCRIPT_STATS = new CommandType(GetScriptStatistics.class, "Script Statistics", "Get statistics about a script from the corpus specified", "stats", "null");
    public static final CommandType CORPUS_STATS = new CommandType(GetCorpusStatistics.class, "Corpus Statistics", "Get statistics about the given corpus", "statc", "null");
    public static final CommandType SENTENCE_ALIGN = new CommandType(AlignSentences.class, "Align Sentences", "Align sentences for the given parallel corpus", "senalign", "null");
    public static final CommandType WORD_ALIGN = new CommandType(AlignWords.class, "Align Words", "Align words for the given sentence aligned parallel corpus", "wordalign", "null");
    public static final CommandType CONVERT_ENCODING = new CommandType(ConvertEncoding.class, "Convert Encoding", "Convert the encoding of text or documents", "encconv", "null");
    public static final CommandType CONVERT_FORMAT = new CommandType(ConvertFormat.class, "Convert Format", "Convert the format of text or documents", "fmtconv", "null");
    public static final CommandType TRANSLATE = new CommandType(Translate.class, "Translate", "Get machine translation of text or documents", "translate", "null");
    public static final CommandType NTRANSLATE = new CommandType(TranslateNumberExpressions.class, "Translate Number Expressions", "Translate multi word number expressions", "ntranslate", "null");
    public static final CommandType TRANSLITERATE = new CommandType(Transliterate.class, "Transliterate", "Get machine transliteration of text or documents", "translit", "null");
    public static final CommandType SURFACE_SIMILARITY = new CommandType(EstimateSurfaceSimilarity.class, "Surface Similarity", "Get an estimate of the surface similarity", "ssim", "null");
    public static final CommandType FUNCTIONAL_SIMILARITY = new CommandType(EstimateFunctionalSimilarity.class, "Functional Similarity", "Get an estimate of the functional similarity", "fsim", "null");
    public static final CommandType DEEP_SIMILARITY = new CommandType(EstimateDeepSimilarity.class, "Deep Similarity", "Get an estimate of the deep similarity", "dsim", "null");
    public static final CommandType MORPH_SEGMENT = new CommandType(SegmentMorphemes.class, "Morph Segment", "Get morph segmentation of the given word(s)", "msegment", "null");
    public static final CommandType START_PYTHON = new CommandType(ExecutePythonCode.class, "Start Python", "Start a block of python code", "py", "null");
    public static final CommandType END_PYTHON = new CommandType(ExecutePythonCode.class, "End Python", "End a block of python code", "yp", "null");
    public static final CommandType START_JAVA = new CommandType(ExecuteJavaCode.class, "Start Java", "Start a block of Java (Bean Shell) code", "ja", "null");
    public static final CommandType END_JAVA = new CommandType(ExecuteJavaCode.class, "End Java", "End a block of Java (Bean Shell) code", "aj", "null");
    public static final CommandType EXIT_TAB = new CommandType(CloseTab.class, "Close Tab", "Close the current tab", "exit", "null");
    public static final CommandType SHUTDOWN = new CommandType(Shutdown.class, "Shutdown", "Close all tabs and exit Sanchay", "shutdown", "null");
    public static final CommandType HELP = new CommandType(CommandHelp.class, "Help", "Help about the Sanchay Shell", "help", "null");
}
