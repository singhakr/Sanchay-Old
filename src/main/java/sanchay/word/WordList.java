/*
 * WordList.java
 *
 * Created on July 17, 2008, 10:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.word;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.SanchayProperties;
import sanchay.resources.ResourceImpl;
import sanchay.table.SanchayTableModel;
import sanchay.table.TableSorter;
import sanchay.text.TextNormalizer;
import sanchay.text.spell.DictionaryFSTExt;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author eklavya
 */
public class WordList  extends ResourceImpl implements SanchayProperties, SanchayDOMElement {
    
    public static final int ONLY_WORD_TYPE = 0;
    public static final int WORD_FREQ_TYPE = 1;
    public static final int RAW_CORPUS_TYPE = 2;
//    public static final int POS_TAGGED_CORPUS_TYPE = 3;
//    public static final int CHUNKED_CORPUS_TYPE = 4;
//    public static final int SSF_CORPUS_TYPE = 5;
    public static final int _TOTAL_SOURCE_TYPES_ = 3;

    protected TextNormalizer textNormalizer;
    
    protected KeyValueProperties wordListKVP;
    protected DictionaryFSTExt wordListFST;
    
    /** Creates a new instance of WordList */
    public WordList() {
        textNormalizer = new TextNormalizer(sanchay.GlobalProperties.getIntlString("hin::utf8"), sanchay.GlobalProperties.getIntlString("UTF-8"), "", "", false);
        clear();
    }
    
    public static String getWordListSourceTypeString(int srcType)
    {
        switch(srcType)
        {
            case ONLY_WORD_TYPE:
                return sanchay.GlobalProperties.getIntlString("Only_Words");
            case WORD_FREQ_TYPE:
                return sanchay.GlobalProperties.getIntlString("Word_and_Frequency");
            case RAW_CORPUS_TYPE:
                return sanchay.GlobalProperties.getIntlString("Raw_Corpus");
//            case POS_TAGGED_CORPUS_TYPE:
//                return "POS Tagged Corpus";
//            case CHUNKED_CORPUS_TYPE:
//                return "Chunked Corpus";
//            case SSF_CORPUS_TYPE:
//                return "SSF Corpus";
            default:
                return sanchay.GlobalProperties.getIntlString("Not_Found");
        }        
    }
    
    public static int getWordListSourceTypeString(String  srcType)
    {
        if(srcType.equals(sanchay.GlobalProperties.getIntlString("Only_Words")))
            return ONLY_WORD_TYPE;
        else if(srcType.equals(sanchay.GlobalProperties.getIntlString("Word_and_Frequency")))
            return WORD_FREQ_TYPE;
        else if(srcType.equals(sanchay.GlobalProperties.getIntlString("Raw_Corpus")))
            return RAW_CORPUS_TYPE;
//        else if(srcType.equals("POS Tagged Corpus"))
//            return POS_TAGGED_CORPUS_TYPE;
//        else if(srcType.equals("Chunked Corpus"))
//            return CHUNKED_CORPUS_TYPE;
//        else if(srcType.equals("SSF Corpus"))
//            return SSF_CORPUS_TYPE;
                
        return -1;
    }
    
    public static String[] getWordListSourceTypeStrings()
    {
        String srcTypes[] = new String[_TOTAL_SOURCE_TYPES_];
        
        for (int i = 0; i < _TOTAL_SOURCE_TYPES_; i++)
        {
            srcTypes[i] = getWordListSourceTypeString(i);
        }
        
        return srcTypes;
    }

    public void readFiles(String[] paths, String cs, int[] fileTypes, boolean clear) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        if(paths.length != fileTypes.length)
            System.err.println(sanchay.GlobalProperties.getIntlString("Error:_The_number_of_paths_is_different_from_the_number_of_file_types."));
        
        if(clear)
            clear();
        
        for (int i = 0; i < paths.length; i++) {
            readFile(paths[i], cs, fileTypes[i], false);
        }
    }

    public void readFiles(String[] paths, String cs, int fileType, boolean clear) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        if(clear)
            clear();
        
        for (int i = 0; i < paths.length; i++) {
            readFile(paths[i], cs, fileType, false);
        }
    }
    
    public void readFile(String path, String cs, int fileType, boolean clear) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        if(clear)
            clear();
        
        System.out.println(sanchay.GlobalProperties.getIntlString("Reading_file:_") + path);
        
        switch(fileType)
        {
            case ONLY_WORD_TYPE:
                readFileOnlyWord(path, cs);
                break;
            case WORD_FREQ_TYPE:
                readFileWordFreq(path, cs);
                break;
            case RAW_CORPUS_TYPE:
                readFileRawCorpus(path, cs);
                break;
//            case POS_TAGGED_CORPUS_TYPE:
//                readFilePOSTaggedCorpus(path, cs);
//                break;
//            case CHUNKED_CORPUS_TYPE:
//                readFileChunkedCorpus(path, cs);
//                break;
//            case SSF_CORPUS_TYPE:
//                readFileSSFCorpus(path, cs);
        }
    }

    public void readFileOnlyWord(String path, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        BufferedReader lnReader = null;
        
        if(!cs.equals(""))
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), sanchay.GlobalProperties.getIntlString("UTF-8")));

        String line;
        String splitstr[] = new String[2];
        
        System.out.println(sanchay.GlobalProperties.getIntlString("Compiling_file_") + path);
        
        while((line = lnReader.readLine()) != null)
        {
//            if((!line.startsWith("#")) && line.equals("") == false)
            if((!line.startsWith("#")) && line.equals(""))
            {
                if(line.startsWith(sanchay.GlobalProperties.getIntlString("Column_Names::")))
                {
                    splitstr = line.split("::", 2);

                    if(splitstr.length == 2 && splitstr[1].equals("") == false)
                    {
                        wordListKVP.setKeyName(splitstr[1]);
                        wordListKVP.setValueName(sanchay.GlobalProperties.getIntlString("Frequency"));
                    }
                }
                else if(line.startsWith(sanchay.GlobalProperties.getIntlString("Column_Count::")) == false)
                {
                    String freqStr = wordListKVP.getPropertyValue(line);

                    if(textNormalizer.isPossiblyValidWord(line) == false)
                        continue;
                    
                    if(freqStr == null || freqStr.equals(""))
                    {
                        wordListKVP.addProperty(line, "1");
                    }
                    else
                    {
                        Integer freqInt = Integer.parseInt(freqStr);
                        
                        int newFreq = freqInt.intValue() + 1;
                        
                        wordListKVP.addProperty(line, "" + new Integer(newFreq));
                    }
                }
            }
        }
        
        lnReader.close();
    }

    public void readFileWordFreq(String path, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        BufferedReader lnReader = null;

        if(!cs.equals(""))       
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), sanchay.GlobalProperties.getIntlString("UTF-8")));

        String line;
        
        String splitstr[] = new String[2];
        
        System.out.println(sanchay.GlobalProperties.getIntlString("Compiling_file_") + path);

        while((line = lnReader.readLine()) != null)
        {
            if((!line.startsWith("#")) && line != "")       
            {
                if(line.startsWith(sanchay.GlobalProperties.getIntlString("Column_Names::")))
                {
                    splitstr = line.split("::", 2);   
                    splitstr = splitstr[1].split("\t");

                    if(splitstr.length == 2 && splitstr[0].equals("") == false
                            && splitstr[1].equals("") == false)
                    {
                        wordListKVP.setKeyName(splitstr[0]);   
                        wordListKVP.setValueName(splitstr[1]);
                    }
                }
                else   
                {
                    splitstr = line.split("\t", 2);
                    
                    if(splitstr.length == 2)       
                    {
                        if(textNormalizer.isPossiblyValidWord(splitstr[0]) == false)
                            continue;
                        
                        String freqStr = wordListKVP.getPropertyValue(splitstr[0]);
                        
                        if(freqStr == null || freqStr.equals(""))
                        {
                            wordListKVP.addProperty(splitstr[0], splitstr[1]);
                        }
                        else
                        {
                            Integer freqInt = Integer.parseInt(freqStr);

                            int newFreq = freqInt.intValue() + Integer.parseInt(splitstr[1]);

                            wordListKVP.addProperty(line, "" + newFreq);
                        }
                    }
                }
            }
        }        
        
        lnReader.close();
    }

    public void readFileRawCorpus(String path, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        BufferedReader lnReader = null;

        if(!cs.equals(""))       
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), cs));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), sanchay.GlobalProperties.getIntlString("UTF-8")));

        String line;
        
        String splitstr[] = new String[2];
        
        System.out.println(sanchay.GlobalProperties.getIntlString("Compiling_file_") + path);

        while((line = lnReader.readLine()) != null)
        {
            if(line.equals("") == false)       
            {
                splitstr = line.split("[\\s]+");

                for (int i = 0; i < splitstr.length; i++)
                {
                    String freqStr = wordListKVP.getPropertyValue(splitstr[i]);

                    if(textNormalizer.isPossiblyValidWord(splitstr[i]) == false)
                        continue;

                    if(freqStr == null || freqStr.equals(""))
                    {
                        wordListKVP.addProperty(splitstr[i], "1");
                    }
                    else
                    {
                        Integer freqInt = Integer.parseInt(freqStr);

                        int newFreq = freqInt.intValue() + 1;

                        wordListKVP.addProperty(splitstr[i], "" + newFreq);
                    }
                }
            }
        }
        
        lnReader.close();
    }

    public void readFilePOSTaggedCorpus(String path, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        
    }

    public void readFileChunkedCorpus(String path, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        
    }

    public void readFileSSFCorpus(String path, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        
    }
    
    public void clear()
    {
        wordListKVP = new KeyValueProperties(0, 10000);
    }

    public int save(String f, String cs) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        System.out.println(sanchay.GlobalProperties.getIntlString("Writing_file:_") + f);

        filePath = f;
        charset = cs;
        
        PrintStream ps = new PrintStream(f, cs);
        
        print(ps);

        return 0;   
    }

    public void print(PrintStream ps)
    {
        try {
            printSorted(ps);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printSorted(PrintStream ps) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        System.out.println(sanchay.GlobalProperties.getIntlString("Converting_to_table..."));

        SanchayTableModel word_list_table = KeyValueProperties.convertToSanchayTableModel(wordListKVP);
        TableSorter sorter = new TableSorter(word_list_table);

        System.out.println(sanchay.GlobalProperties.getIntlString("Sorting_the_table..."));

        sorter.setColumnComparator(String.class, TableSorter.STRING_INTEGER_COMPARATOR);
        sorter.setSortingStatus(1, TableSorter.DESCENDING);

        int rcount = word_list_table.getRowCount();            
        int ccount = word_list_table.getColumnCount();

        SanchayTableModel sorted_word_list_table = new SanchayTableModel(rcount, ccount);

        for (int i = 0; i < rcount; i++)
        {
            for (int j = 0; j < ccount; j++)       
            {
                sorted_word_list_table.setValueAt(sorter.getValueAt(i, j), i, j);   
            }
        }

        sorted_word_list_table.setColumnIdentifiers(new String[]{sanchay.GlobalProperties.getIntlString("Word"), sanchay.GlobalProperties.getIntlString("Frequency")});

        System.out.println(sanchay.GlobalProperties.getIntlString("Saving_the_word_list..."));
        
        sorted_word_list_table.save(filePath, charset);            
    }

    public DOMElement getDOMElement() {
        return null;
    }

    public String getXML() {
        return null;
    }

    public void readXML(Element domElement) {
    }

    public void printXML(PrintStream ps)
    {
    }    
}
