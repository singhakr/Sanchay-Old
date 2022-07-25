/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.gui.common.SanchayLanguages;
import sanchay.properties.PropertyTokens;
import sanchay.text.TextNormalizer;

/**
 *
 * @author anil
 */
public class SMTTransliterator {

    protected String srcLangEnc = "eng::utf8";
    protected String tgtLangEnc = "hin::utf8";

    protected String srcCharset = "ISO-8859-1";
    protected String tgtCharset = "UTF-8";

    protected String outCharset = "UTF-8";

    protected String srcPathORString = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/translit-test-data-hindi.txt";
//    protected String tgtPath = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/translit-test-data-hindi-smt-results.xml";
    protected String tgtPath;

    protected TextNormalizer textNormalizer;

    protected PropertyTokens srcWords;

    protected String mode = "-s";
    protected boolean onlyTheFirst = true;
    protected boolean outputXML = false;
    protected TranslationData translationData;

    protected String configFile = "data/transliteration/eng-hin/phramer.fast.ini";
    protected PhramerWrapper phramerWrapper;

    public SMTTransliterator(String configFile)
    {
        this.configFile = configFile;

        init(configFile);
    }

    public SMTTransliterator(String mode, String srcLangEnc, String tgtLangEnc,
            String inPathORString, boolean onlyTheFirst, boolean outputXML, String outPath)
    {
        this.mode = mode;
        
        this.srcLangEnc = srcLangEnc;
        this.tgtLangEnc = tgtLangEnc;
        this.srcPathORString = inPathORString;
        this.onlyTheFirst = onlyTheFirst;
        this.outputXML = outputXML;
        this.tgtPath = outPath;

        String srcLang = SanchayLanguages.getLanguageCodeFromLECode(srcLangEnc);
        String tgtLang = SanchayLanguages.getLanguageCodeFromLECode(tgtLangEnc);

        if(!srcLang.equalsIgnoreCase("eng") && !srcLang.equalsIgnoreCase("hin"))
        {
            System.err.println("The source language not yet supported: try the with a custom config file, if available.");
        }

        if(!tgtLang.equalsIgnoreCase("eng") && !tgtLang.equalsIgnoreCase("hin"))
        {
            System.err.println("The target language not yet supported: try the with a custom config file, if available.");
        }

        configFile = GlobalProperties.getHomeDirectory() + "/data/transliteration/" + srcLang + "-" + tgtLang + "/phramer.fast.ini";

        init(configFile);
    }

    /**
     * @return the srcLangEnc
     */
    public String getSrcLangEnc()
    {
        return srcLangEnc;
    }

    /**
     * @param srcLangEnc the srcLangEnc to set
     */
    public void setSrcLangEnc(String srcLangEnc)
    {
        this.srcLangEnc = srcLangEnc;
    }

    /**
     * @return the tgtLangEnc
     */
    public String getTgtLangEnc()
    {
        return tgtLangEnc;
    }

    /**
     * @param tgtLangEnc the tgtLangEnc to set
     */
    public void setTgtLangEnc(String tgtLangEnc)
    {
        this.tgtLangEnc = tgtLangEnc;
    }

    /**
     * @return the srcCharset
     */
    public String getSrcCharset()
    {
        return srcCharset;
    }

    /**
     * @param srcCharset the srcCharset to set
     */
    public void setSrcCharset(String srcCharset)
    {
        this.srcCharset = srcCharset;
    }

    /**
     * @return the tgtCharset
     */
    public String getTgtCharset()
    {
        return tgtCharset;
    }

    /**
     * @param tgtCharset the tgtCharset to set
     */
    public void setTgtCharset(String tgtCharset)
    {
        this.tgtCharset = tgtCharset;
    }

    /**
     * @return the outCharset
     */
    public String getOutCharset()
    {
        return outCharset;
    }

    /**
     * @param outCharset the outCharset to set
     */
    public void setOutCharset(String outCharset)
    {
        this.outCharset = outCharset;
    }

    /**
     * @return the srcPathORString
     */
    public String getSrcPathORString()
    {
        return srcPathORString;
    }

    /**
     * @param srcPathORString the srcPathORString to set
     */
    public void setSrcPathORString(String srcPath)
    {
        this.srcPathORString = srcPath;
    }

    /**
     * @return the tgtPath
     */
    public String getTgtPath()
    {
        return tgtPath;
    }

    /**
     * @param tgtPath the tgtPath to set
     */
    public void setTgtPath(String tgtPath)
    {
        this.tgtPath = tgtPath;
    }

    /**
     * @return the textNormalizer
     */
    public TextNormalizer getTextNormalizer()
    {
        return textNormalizer;
    }

    /**
     * @param textNormalizer the textNormalizer to set
     */
    public void setTextNormalizer(TextNormalizer textNormalizer)
    {
        this.textNormalizer = textNormalizer;
    }

    /**
     * @return the srcWords
     */
    public PropertyTokens getSrcWords()
    {
        return srcWords;
    }

    /**
     * @param srcWords the srcWords to set
     */
    public void setSrcWords(PropertyTokens srcWords)
    {
        this.srcWords = srcWords;
    }

    /**
     * @return the configFile
     */
    public String getConfigFile()
    {
        return configFile;
    }

    /**
     * @param configFile the configFile to set
     */
    public void setConfigFile(String configFile)
    {
        this.configFile = configFile;
    }

    /**
     * @return the phramerWrapper
     */
    public PhramerWrapper getPhramerWrapper()
    {
        return phramerWrapper;
    }

    /**
     * @param phramerWrapper the phramerWrapper to set
     */
    public void setPhramerWrapper(PhramerWrapper phramerWrapper)
    {
        this.phramerWrapper = phramerWrapper;
    }

    public void init(String configFile)
    {
        if(tgtLangEnc.startsWith("hin::"))
            textNormalizer = new TextNormalizer(getTgtLangEnc(), getTgtCharset(), "", "", false);
        
        setPhramerWrapper(new PhramerWrapper(configFile));
        
        if(mode.equals("-f"))
        {
            try
            {
                srcWords = new PropertyTokens(srcPathORString, tgtCharset);
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(SMTTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(SMTTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        translationData = new TranslationData();
    }

    public String transliterate(String wordString)
    {
        return phramerWrapper.transliterate(wordString);
    }

    public String[] transliterateNBest(String wordString)
    {
        return phramerWrapper.transliterateNBest(wordString);
    }

    public void transliterate(String srcWrd, boolean onlyTheFirst)
    {
        if(!onlyTheFirst)
        {
            String candidates[] = transliterateNBest(srcWrd);

            TransliterationCandidates transliterationCandidates = new TransliterationCandidates();

            transliterationCandidates.setSrcWrd(srcWrd);

            for (int j = 0; j < candidates.length; j++)
            {
                String string = candidates[j];

                transliterationCandidates.addTranslationCandidateScores(string, new TranslationCandidateScores());
            }

            translationData.addSrcToken(srcWrd, transliterationCandidates);
        }
        else
        {
            String candidate = transliterate(srcWrd);

            TransliterationCandidates transliterationCandidates = new TransliterationCandidates();

            transliterationCandidates.setSrcWrd(srcWrd);

            transliterationCandidates.addTranslationCandidateScores(candidate, new TranslationCandidateScores());

            translationData.addSrcToken(srcWrd, transliterationCandidates);
        }
    }

    public void transliterate()
    {
        if(mode.equals("-f"))
        {
            int scount = srcWords.countTokens();

            for (int i = 1; i <= scount; i++)
            {
                String srcWrd = srcWords.getToken(i - 1);
                transliterate(srcWrd, onlyTheFirst);
            }
        }
        else if(mode.equals("-s"))
        {
            translationData.clear();
            transliterate(srcPathORString, onlyTheFirst);
        }
    }

    public void transliterate(boolean onlyTheFirst)
    {
        this.onlyTheFirst = onlyTheFirst;

        transliterate();
    }

    public void printOutput()
    {
        PrintStream ps = null;

        if(tgtPath != null)
        {
            try {
                ps = new PrintStream(tgtPath, tgtCharset);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MLTransliterator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            ps = System.out;

        printOutput(ps);
    }

    public void printOutput(PrintStream ps)
    {
        if(outputXML)
            translationData.printXML(ps);
        else
            translationData.print(ps);
    }

    public void saveOutput()
    {
        try
        {
            translationData.save(tgtPath, tgtCharset);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SMTTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SMTTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(SMTTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readMosesOutput()
    {
        PropertyTokens tgtSMTWords = null;
        try
        {
            tgtSMTWords = new PropertyTokens("/home/anil/tmp/feature_based_code/NEWS-smt-test-10-best.utf.txt", "UTF-8");
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SMTTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(SMTTransliterator.class.getName()).log(Level.SEVERE, null, ex);
        }

        for(int l = 0;l < tgtSMTWords.countTokens(); l++)
        {
            String line = tgtSMTWords.getToken(l);
            String srcTkns[] = line.split("\\s\\|\\|\\|\\s");

            String sword = srcWords.getToken(Integer.parseInt(srcTkns[0]));

            TransliterationCandidates smtCandidates = (TransliterationCandidates) translationData.getTranslationCandidates(sword);

            if(smtCandidates == null)
            {
                smtCandidates = new TransliterationCandidates();
                smtCandidates.setSrcWrd(sword);
                translationData.addSrcToken(sword, smtCandidates);
            }

            smtCandidates.addTranslationCandidateScores(srcTkns[1], new TranslationCandidateScores());
        }
    }

    public static void main(String args[])
    {
//        args = new String[]{"-s", "eng::utf8", "hin::utf8", "kandarp"};
//        args = new String[]{"-s", "hin::utf8", "eng::utf8", "कंदर्प", "false", "true"};

//        SMTTransliterator transliterator = new SMTTransliterator(GlobalProperties.getHomeDirectory() + "/data/transliteration/en-hi-utf8/phramer.fast.ini");

        if(args.length < 4)
        {
            System.out.println("USAGE:");
            System.out.println("\tjava -cp ... sanchay.mlearning.mt.SMTTransliterator mode srcLangEnc tgtLangEnc inputStringORFile  [onlyTheFirst] [[xml] [outFilePath]]]");
            System.out.println("Examples:");
            System.out.println("\tjava -cp ... sanchay.mlearning.mt.SMTTransliterator -s eng::utf8 hin::utf8 kandarp");
            System.out.println("\tjava -cp ... sanchay.mlearning.mt.SMTTransliterator -s eng::utf8 hin::utf8 kandarp false false out.xml");
            System.out.println("\tjava -cp ... sanchay.mlearning.mt.SMTTransliterator -s eng::utf8 hin::utf8 kandarp false true out.xml");
            System.out.println("\tjava -cp ... sanchay.mlearning.mt.SMTTransliterator -f eng::utf8 hin::utf8 in.txt");
            System.out.println("\tjava -cp ... sanchay.mlearning.mt.SMTTransliterator -f eng::utf8 hin::utf8 in.txt false true out.xml");
            System.out.println("\tjava -cp ... sanchay.mlearning.mt.SMTTransliterator -f eng::utf8 hin::utf8 in.txt false false out.xml");
            System.out.println("\t-s: the string mode");
            System.out.println("\t-s: the file mode");
            System.out.println("\tThe input file can have a list of strings to be transliterated, one per line.");
            System.out.println("\tIf the outFilePath is not given, the output will on the terminal.");
            System.out.println("");
            System.out.println("**For running from the shell script, replace the 'java -cp ...' part with 'sh transliteration-smt.sh'**");
            System.out.println("");

            System.exit(1);
        }

        SMTTransliterator transliterator = null;

        if(args.length == 7)
            transliterator = new SMTTransliterator(args[0], args[1], args[2], args[3], Boolean.parseBoolean(args[4]), Boolean.parseBoolean(args[5]), args[6]);
        else if(args.length == 6)
            transliterator = new SMTTransliterator(args[0], args[1], args[2], args[3], Boolean.parseBoolean(args[4]), Boolean.parseBoolean(args[5]), null);
        else if(args.length == 5)
            transliterator = new SMTTransliterator(args[0], args[1], args[2], args[3], Boolean.parseBoolean(args[4]), false, null);
        else
            transliterator = new SMTTransliterator(args[0], args[1], args[2], args[3], true, false, null);

//        transliterator.transliterate(false);

//        transliterator.readMosesOutput();

//        transliterator.saveOutput();

        transliterator.transliterate();
        transliterator.printOutput();
    }
}
