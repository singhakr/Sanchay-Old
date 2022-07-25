/* Dictionary.java
 * Class which reads the file and creates dictionary out of it.
 *
 * */

package sanchay.resources.shabdanjali;

import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;

/**
 *
 *  @author Bharat Ram Ambati
 */

public class Dictionary {
	private BufferedReader inputStream = null;
	private String file;
	private SortedMap<String, List<DictEntry>> dict = new TreeMap<String, List<DictEntry>>();
	
	public void makeDictionary(String filename) throws IOException {

		this.file=filename;

		try {
			inputStream = new BufferedReader(new FileReader(this.file));
			String line, wordData;
			wordData="";
			while ((line = inputStream.readLine()) != null ) {
				if (line.length() == 0) {
					if (wordData != "") {
//						System.out.println("wordData is " + wordData + "wordDataend");
						makeEntry(wordData);
						wordData="";
					}
				}
				else {
					wordData+=line+"\n";
				}
			}
			if (wordData != "") {
//				System.out.println("wordData is " + wordData + "wordDataend");
				makeEntry(wordData);
				wordData="";
			}
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	private void makeEntry (String wordData) {
		
		String word,pos,meaning,exsentence;
		List<DictEntry> entryList = new ArrayList<DictEntry>();
		DictEntry dictEntry = new DictEntry();
		
		exsentence="";
		String[] lines;
		lines = wordData.split("\n");
		lines[0]=lines[0].replaceAll("\"","");
		String[] items = lines[0].split(",");

		if (items.length ==3) {
			word = items[0];
			pos = items[1];
			meaning = items[2];

			if (dict.get(word)  == null) {
				dict.put(word, entryList);
			}
			else {
				entryList = dict.get(word);
			}

			dictEntry.addWord(word);
			dictEntry.addPOS(pos);
			dictEntry.addMeaning(meaning);
//			System.out.println("Word pos meaning = " + word + pos + meaning);
		}
		else {
			System.err.println(GlobalProperties.getIntlString("Error_in_format_<word,pos,meaning>") + lines[0]);
			System.err.println(GlobalProperties.getIntlString("Data_is_") + wordData);
			return;
		}

		for (int i=1 ; i < lines.length ; i++) {
			if (lines[i].startsWith("--\"")) {
				meaning = lines[i].substring(3,lines[i].length() - 1);
				dictEntry.addMeaning(meaning);
				exsentence="";
//				System.out.println("Meaning is " + meaning);
			}
			else if (lines[i].endsWith(".") || lines[i].indexOf(word) != -1) {
				exsentence += lines[i];
				dictEntry.addExample(exsentence);
				exsentence="";
//				System.out.println("Exsentence is " + exsentence);
			}
			else {
				exsentence += lines[i];
			}

		}
		entryList.add(dictEntry);
/*
		System.out.println("Word and Examples " + dictEntry.getWord() + dictEntry.getMeanings());
		for (String key : dictEntry.getMeanings()) {
			System.out.println("meaning and Examples " + key + dictEntry.getExamples(key));
		}
*/
		
	}
	
	public void removeEntry(String word) {
		dict.remove(word);
	}

	public void addEntry(String word,List<DictEntry> entryList) {
		dict.put(word,entryList);
	}
	
	public void printDictionary() {
		List<DictEntry> entryList = new ArrayList<DictEntry>();
		DictEntry dictEntry = new DictEntry();
		for (String word : dict.keySet()) {
			entryList = dict.get(word);
			for (int i = 0 ; i< entryList.size() ; i++) {
				dictEntry = entryList.get(i);
				System.out.println(GlobalProperties.getIntlString("Word_is_") + dictEntry.getWord());
				System.out.println(GlobalProperties.getIntlString("Pos_is_") + dictEntry.getPOS());
				for (String mean : dictEntry.getMeanings()) {
					System.out.println(GlobalProperties.getIntlString("Meaning_") + mean);
					System.out.println(GlobalProperties.getIntlString("Examples_") + dictEntry.getExamples(mean));
				}
			}
			System.out.println(GlobalProperties.getIntlString("\nDetails_of_one_word_completed\n\n"));
		}
	}

    public void writeFile() {
        writeFile("shabdanjali.unicode");
    }

    public void writeEntry(String key, File fout)
    {
            PrintStream ps = null;

            try
            {
                ps = new PrintStream(fout, GlobalProperties.getIntlString("UTF8"));
                String entry="";
                String word,pos,meaning;
                List<DictEntry> entryList = new ArrayList<DictEntry>();
                List<String> meanlist = new ArrayList<String>();
                DictEntry dictEntry = new DictEntry();
                entryList = dict.get(key);
                for (int i = 0 ; i< entryList.size() ; i++) {
                    dictEntry = entryList.get(i);
                    word=key;
                    pos=dictEntry.getPOS();
                    entry="\""+key+"\",\""+pos+"\"";
                    meanlist=dictEntry.getMeanings();
                    meaning=meanlist.get(0);
                    entry=entry+",\""+meaning+"\"\n";
                    for (String example1 : dictEntry.getExamples(meaning)){
                        entry=entry+example1+"\n";
                    }
                    for (int j=1; j<meanlist.size(); j++){
                        meaning=meanlist.get(j);
                        entry=entry+"--\""+meaning+"\"\n";
                        for (String example : dictEntry.getExamples(meaning)){
                            entry=entry+example+"\n";
                        }
                    }
                }
                ps.println(entry);
                entry="";
		}
            catch(IOException e)
            {
                e.printStackTrace();
                System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
            }
    }

	public void writeFile(String outFile) {
            //File fout = new File("shabdanjali.unicode");
            File fout = new File(outFile);
            PrintStream ps = null;
            
            try
            {
                ps = new PrintStream(fout, GlobalProperties.getIntlString("UTF8"));
                
                String entry="";
                String word,pos,meaning;
                List<DictEntry> entryList = new ArrayList<DictEntry>();
                List<String> meanlist = new ArrayList<String>();
            DictEntry dictEntry = new DictEntry();
		for (String key : dict.keySet()) {
			entryList = dict.get(key);
			for (int i = 0 ; i< entryList.size() ; i++) {
				dictEntry = entryList.get(i);
                                word=key;
                                pos=dictEntry.getPOS();
                                entry="\""+key+"\",\""+pos+"\"";
                                meanlist=dictEntry.getMeanings();
                                meaning=meanlist.get(0);
                                entry=entry+",\""+meaning+"\"\n";
                                for (String example1 : dictEntry.getExamples(meaning)){
                                    entry=entry+example1+"\n";
                                }
                                for (int j=1; j<meanlist.size(); j++){
                                    meaning=meanlist.get(j);
                                    entry=entry+"--\""+meaning+"\"\n";
                                    for (String example : dictEntry.getExamples(meaning)){
                                    entry=entry+example+"\n";
                                }
                                }
				
			}
                        ps.println(entry);
                        entry="";
		}
            }
            catch(IOException e) 
            {
		e.printStackTrace();
                System.out.println(GlobalProperties.getIntlString("IOException_Exception!"));
            }

		
	}

    public SortedMap getDictionary()
    {
        return dict;
    }

	public void printWordDetails(String word) {
		List<DictEntry> entryList = new ArrayList<DictEntry>();
		DictEntry dictEntry = new DictEntry();
		entryList = dict.get(word);
 if (entryList == null) {
                        System.out.println(GlobalProperties.getIntlString("No_entries_in_the_dictionary"));
                }
                else {

			for (int i = 0 ; i< entryList.size() ; i++) {
				dictEntry = entryList.get(i);
				System.out.println(GlobalProperties.getIntlString("Word_is_") + dictEntry.getWord());
				System.out.println(GlobalProperties.getIntlString("Pos_is_") + dictEntry.getPOS());
				for (String mean : dictEntry.getMeanings()) {
					System.out.println(GlobalProperties.getIntlString("Meaning_") + mean);
					System.out.println(GlobalProperties.getIntlString("Examples_") + dictEntry.getExamples(mean));
				}
			}
}
	}

	public List<DictEntry> getDictionaryitem(String word) {
		return dict.get(word);
	}
/*
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Incorrect input\nFormat is java Dictionary <dictionary-file>");
			return;
		}
		Dictionary newDict = new Dictionary();
		newDict.makeDictionary(args[0]);
		newDict.printDictionary();
//		newDict.printWordDetails("appraise");
//		newDict.printDictionary();
//		System.out.println("Num of args = "+ args.length);
	}
*/
}
