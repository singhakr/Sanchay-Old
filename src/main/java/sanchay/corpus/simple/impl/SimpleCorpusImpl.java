package sanchay.corpus.simple.impl;

import java.io.*;
import java.util.*;

import sanchay.GlobalProperties;
import sanchay.common.types.*;
import sanchay.corpus.*;
import sanchay.corpus.simple.*;

import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertiesManager;
import sanchay.properties.PropertiesTable;
import sanchay.table.*;

public class SimpleCorpusImpl extends Corpus implements SimpleCorpus
{
	protected Vector<Sentence> sentences; // Sentences
	protected SanchayTableModel wordTypeTable; //each row of the table represents wordtype of the corpus, the column
												//name of the table is provided by the schema which is a part 
												//of SanchayTableModel
	protected PropertiesManager propManager;	//will contain various properties(currently only KeyValueProperties)

    protected LinkedHashMap<String, Integer> words;

	public SimpleCorpusImpl(String propsfilename, String charset)
	{
		sentences = new Vector<Sentence>(10000, 10000);
		wordTypeTable = null;

        words = new LinkedHashMap<String, Integer>();
		
		try
		{
			propManager = new PropertiesManager(propsfilename, charset);
		}
		catch(IOException e)
		{
            e.printStackTrace();
			System.out.println(GlobalProperties.getIntlString("Could_not_find_the_keyvalue_property_file_in_SimpleCorpusImp..."));
			return;
		}
	}

	public SimpleCorpusImpl(int initial_capacity)
	{
		sentences = new Vector<Sentence>(initial_capacity, initial_capacity/3);
        words = new LinkedHashMap<String, Integer>();
		wordTypeTable = null;
		propManager = null;
	}
	
	public SanchayTableModel getWordTypeTable()
	{
		return wordTypeTable;
	}
	
	public void setWordTypeTable()
	{
		return;
	}
	
	public PropertiesManager getPropsManager()
	{
		return propManager;
	}
	
	public int setPropsManager(String propsfilename, String charset)
	{
		try
		{
			propManager = new PropertiesManager(propsfilename, charset);
		}
		catch(IOException e)
		{
            e.printStackTrace();
			System.out.println(GlobalProperties.getIntlString("Could_not_find_the_keyvalue_property_file_in_SimpleCorpusImp..."));
			return 1;
		}
		
		return 0;
	}

	public int countSentences()
	{
		return sentences.size();
	}

    public int countTokens(boolean recalculate)
    {
        if(recalculate == false)
            return tokenCount;

		Sentence sen = null;

		for(int i = 0; i < countSentences(); i++)
		{
			sen = getSentence(i);

            tokenCount +=  sen.countWords();
        }

        return tokenCount;
    }

	public Sentence getSentence(int num)
	{
		return sentences.get(num);
	}

	public int addSentence(Sentence s)
	{
		sentences.add(s);
		return sentences.size();
	}

	public int insertSentence(int index, Sentence s)
	{
		sentences.add(index, s);
		return sentences.size();
	}

	public Sentence removeSentence(int num)
	{
		return sentences.remove(num);
	}
	
	public int read(File file, String charset)throws FileNotFoundException, IOException
	{
		KeyValueProperties dataKVP = (KeyValueProperties) propManager.getPropertyContainer("DataProps", PropertyType.KEY_VALUE_PROPERTIES);
		PropertiesTable resourceTable = (PropertiesTable) propManager.getPropertyContainer("Resources", PropertyType.PROPERTY_TABLE);
		
		BufferedReader lnReader = null;
		
		if(file != null && charset != null)
		{
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		}
		else
		{
			lnReader = new BufferedReader(
		            new InputStreamReader(new FileInputStream(dataKVP.getPropertyValue("CorpusFile")), dataKVP.getPropertyValue("CorpusCharSet")));
		}
		
		String schemaFile = dataKVP.getPropertyValue("SchemaFile");
				
		SanchayTableModel schema = new SanchayTableModel(schemaFile, dataKVP.getPropertyValue("SchemaCharSet"));
		
		wordTypeTable = new SanchayTableModel(dataKVP.getPropertyValue("CorpusCharSet"), schema);
		wordTypeTable.setEditable(false);

		String line = "";
        String wrd = "";
		String splitstr[];
        int scount = 0;
        int wcount = 0;

        int wrdColIndex = wordTypeTable.getColumnIndex("WTString");
        int freqColIndex = wordTypeTable.getColumnIndex("WTFreq");

        int rowIndex = 0;

		while( (line = lnReader.readLine()) != null )
		{
			splitstr = line.split("[\\s+]");
			wcount = splitstr.length;

			if(scount > 0 && scount % 100 == 0)
			{
				System.out.println("Processed " + scount + " sentences.");
			}

			SimpleSentenceImpl sentence = new SimpleSentenceImpl(wcount);

			for(int i = 0; i < wcount; i++)
			{
                wrd = splitstr[i];
                Integer wrdIndexObj = words.get(wrd);

                if(wrdIndexObj == null)
				{                    
					wordTypeTable.addRow();
                    
					rowIndex = wordTypeTable.getRowCount() - 1;

                    wordTypeTable.setValueAt(wrd, rowIndex, wrdColIndex);

                    if(freqColIndex != -1)
                        wordTypeTable.setValueAt(new Long(1), rowIndex, freqColIndex);

                    words.put(wrd, new Integer(rowIndex));
					sentence.setWord(i, rowIndex);
				}
				else
				{
					rowIndex = wrdIndexObj;

                    if(freqColIndex != -1)
                    {
                        long freq = ((Long) wordTypeTable.getValueAt(rowIndex, freqColIndex)).longValue();

                        wordTypeTable.setValueAt(new Long(freq + 1), rowIndex, freqColIndex);
                    }

                    words.put(wrd, new Integer(rowIndex));
					sentence.setWord(i, rowIndex);
				}
			}

			scount++;

			addSentence(sentence);
		}

		return 0;
	}

	public int readSegments(File file, String charset, int segmentSize)throws FileNotFoundException, IOException
	{
		KeyValueProperties dataKVP = (KeyValueProperties) propManager.getPropertyContainer("DataProps", PropertyType.KEY_VALUE_PROPERTIES);
		PropertiesTable resourceTable = (PropertiesTable) propManager.getPropertyContainer("Resources", PropertyType.PROPERTY_TABLE);

		BufferedReader lnReader = null;

		if(file != null && charset != null)
		{
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		}
		else
		{
			lnReader = new BufferedReader(
		            new InputStreamReader(new FileInputStream(dataKVP.getPropertyValue("CorpusFile")), dataKVP.getPropertyValue("CorpusCharSet")));
		}

		String schemaFile = dataKVP.getPropertyValue("SchemaFile");

		SanchayTableModel schema = new SanchayTableModel(schemaFile, dataKVP.getPropertyValue("SchemaCharSet"));

		wordTypeTable = new SanchayTableModel(dataKVP.getPropertyValue("CorpusCharSet"), schema);
		wordTypeTable.setEditable(false);

		String line = "";
        String wrd = "";
		String splitstr[];
        int scount = 0;
        int wcount = 0;

        int wrdColIndex = wordTypeTable.getColumnIndex("WTString");
        int freqColIndex = wordTypeTable.getColumnIndex("WTFreq");

        int rowIndex = 0;
        int tokenCount = 0;

        SimpleSentenceImpl sentence = null;

        int wrdIndexInSentence = 0;

		while( (line = lnReader.readLine()) != null )
		{
			splitstr = line.split("[\\s+]");
			wcount = splitstr.length;

			if(scount > 0 && scount % 100 == 0)
			{
				System.out.println("Processed " + scount + " sentences.");
			}

			for(int i = 0; i < wcount; i++)
			{
                wrd = splitstr[i];
                Integer wrdIndexObj = words.get(wrd);

                if(tokenCount % segmentSize == 0)
                {
                    if(sentence != null)
                        addSentence(sentence);

                    sentence = new SimpleSentenceImpl(segmentSize);

                    wrdIndexInSentence = 0;
                }

                if(wrdIndexObj == null)
				{
					wordTypeTable.addRow();

					rowIndex = wordTypeTable.getRowCount() - 1;

                    wordTypeTable.setValueAt(wrd, rowIndex, wrdColIndex);

                    if(freqColIndex != -1)
                        wordTypeTable.setValueAt(new Long(1), rowIndex, freqColIndex);

                    words.put(wrd, new Integer(rowIndex));
					sentence.setWord(wrdIndexInSentence, rowIndex);
				}
				else
				{
					rowIndex = wrdIndexObj;

                    if(freqColIndex != -1)
                    {
                        long freq = ((Long) wordTypeTable.getValueAt(rowIndex, freqColIndex)).longValue();

                        wordTypeTable.setValueAt(new Long(freq + 1), rowIndex, freqColIndex);
                    }

                    words.put(wrd, new Integer(rowIndex));
					sentence.setWord(wrdIndexInSentence, rowIndex);
				}

                tokenCount++;
                wrdIndexInSentence++;
			}

			scount++;
		}

		return 0;
	}

	public int readOverlappingSegments(File file, String charset, int segmentSize)throws FileNotFoundException, IOException
	{
		KeyValueProperties dataKVP = (KeyValueProperties) propManager.getPropertyContainer("DataProps", PropertyType.KEY_VALUE_PROPERTIES);
		PropertiesTable resourceTable = (PropertiesTable) propManager.getPropertyContainer("Resources", PropertyType.PROPERTY_TABLE);

		BufferedReader lnReader = null;

		if(file != null && charset != null)
		{
			lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		}
		else
		{
			lnReader = new BufferedReader(
		            new InputStreamReader(new FileInputStream(dataKVP.getPropertyValue("CorpusFile")), dataKVP.getPropertyValue("CorpusCharSet")));
		}

		String schemaFile = dataKVP.getPropertyValue("SchemaFile");

		SanchayTableModel schema = new SanchayTableModel(schemaFile, dataKVP.getPropertyValue("SchemaCharSet"));

		wordTypeTable = new SanchayTableModel(dataKVP.getPropertyValue("CorpusCharSet"), schema);
		wordTypeTable.setEditable(false);

		String line = "";
        String wrd = "";
		String splitstr[];
        int scount = 0;
        int wcount = 0;

        int wrdColIndex = wordTypeTable.getColumnIndex("WTString");
        int freqColIndex = wordTypeTable.getColumnIndex("WTFreq");

        int rowIndex = 0;
        int tokenCount = 0;

        SimpleSentenceImpl sentence1 = null;
        SimpleSentenceImpl sentence2 = null;

        int wrdIndexInSentence1 = 0;
        int wrdIndexInSentence2 = 0;

		while( (line = lnReader.readLine()) != null )
		{
			splitstr = line.split("[\\s+]");
			wcount = splitstr.length;

			if(scount > 0 && scount % 100 == 0)
			{
				System.out.println("Processed " + scount + " sentences.");
			}

			for(int i = 0; i < wcount; i++)
			{
                wrd = splitstr[i];
                Integer wrdIndexObj = words.get(wrd);

                if(tokenCount % segmentSize == 0)
                {
                    if(sentence1 != null)
                        addSentence(sentence1);

                    sentence1 = new SimpleSentenceImpl(segmentSize);

                    wrdIndexInSentence1 = 0;
                }

                if(tokenCount % segmentSize == segmentSize/2)
                {
                    if(sentence2 != null)
                        addSentence(sentence2);

                    sentence2 = new SimpleSentenceImpl(segmentSize);

                    wrdIndexInSentence2 = 0;
                }

                if(wrdIndexObj == null)
				{
					wordTypeTable.addRow();

					rowIndex = wordTypeTable.getRowCount() - 1;

                    wordTypeTable.setValueAt(wrd, rowIndex, wrdColIndex);

                    if(freqColIndex != -1)
                        wordTypeTable.setValueAt(new Long(1), rowIndex, freqColIndex);

                    words.put(wrd, new Integer(rowIndex));
					sentence1.setWord(wrdIndexInSentence1, rowIndex);

                    if(sentence2 != null)
    					sentence2.setWord(wrdIndexInSentence2, rowIndex);
				}
				else
				{
					rowIndex = wrdIndexObj;

                    if(freqColIndex != -1)
                    {
                        long freq = ((Long) wordTypeTable.getValueAt(rowIndex, freqColIndex)).longValue();

                        wordTypeTable.setValueAt(new Long(freq + 1), rowIndex, freqColIndex);
                    }

                    words.put(wrd, new Integer(rowIndex));
					sentence1.setWord(wrdIndexInSentence1, rowIndex);
                    
                    if(sentence2 != null)
                        sentence2.setWord(wrdIndexInSentence2, rowIndex);
				}

                tokenCount++;
                wrdIndexInSentence1++;
                wrdIndexInSentence2++;
			}

			scount++;
		}

		return 0;
	}

	public void print(PrintStream ps)
	{
		Sentence sen = null;

		for(int i = 0; i < countSentences(); i++)
		{
			sen = getSentence(i);
            ps.println(((SimpleSentence) sen).getSentenceString(wordTypeTable));
		}
	}
	
	
	public static void main(String args[])
	{
		SimpleCorpusImpl simpleCorpus = new SimpleCorpusImpl("workspace/minimal_src_propertymanager.txt", GlobalProperties.getIntlString("UTF-8"));
		
		try
		{
//			simpleCorpus.read(new File(GlobalProperties.getHomeDirectory() + "/data/parallel-corpus/Eng-2000.txt"), "UTF-8");	//file and the charset will be read from the property file
			simpleCorpus.readOverlappingSegments(new File("/home/anil/corpora/gnc-books/sentence-split/HI-doc1"), "UTF-8", 25);

            simpleCorpus.print(System.out);
		}
		catch(IOException e)
		{
            e.printStackTrace();
		}
	}
}
