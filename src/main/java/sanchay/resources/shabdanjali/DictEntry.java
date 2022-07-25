/* DictEntry.java
 * Class which stores word,pos,meanings and examples of a word in dictionary.
 */

package sanchay.resources.shabdanjali;

import java.util.*;
import sanchay.GlobalProperties;

/**
 *
 *  @author Bharat Ram Ambati
 */

public class DictEntry {
	private String word, pos;
	private SortedMap<String, List<String>> meaningNex = new TreeMap<String, List<String>>();
	private List<String> exampleList;
	private List<String> meanings = new ArrayList<String>();
	
	public void addWord (String word) {
		this.word = word;
	}
	
	public void addPOS (String pos) { 
		this.pos = pos;
	}

	public void addMeaning (String meaning) {
//		System.out.println("AMB\n" + meaning);
		if (!(meaningNex.containsKey(meaning))) {
			meanings.add(meaning);
			meaningNex.put(meaning, exampleList = new ArrayList<String>());
		}
		else {
			System.err.println(GlobalProperties.getIntlString("Meaning_already_exists\n"));
		}
	}

	public void addExample (String sentence) {
		exampleList.add(sentence);
	}

	public void addExample (String meaning, String sentence) {
		if ((meaningNex.containsKey(meaning))) {
			exampleList = meaningNex.get(meaning);
			exampleList.add(sentence);
		}
		else {
			System.err.println(GlobalProperties.getIntlString("Can't_Add_example_sentence._Since_meaning_doesn't_exists\n"));
		}
	}

	public String getPOS () {
		return this.pos;
	}

	public String getWord () {
		return this.word;
	}

	public List<String> getMeanings() {
		return this.meanings;
	}

	public List<String> getExamples(String meaning) {
		return this.meaningNex.get(meaning);
	}

	public void addMeanExample(String meaning, String sentence) {
		if (!(meaningNex.containsKey(meaning))) {
			meanings.add(meaning);
			meaningNex.put(meaning, exampleList = new ArrayList<String>());
		}
		else {
			System.err.println(GlobalProperties.getIntlString("Meaning_already_exists\n"));
		}
		exampleList.add(sentence);
	}

	public void removeExample(String meaning, String sentence) {
		if ((meaningNex.containsKey(meaning))) {
			//			exampleList = new ArrayList<String>();
			exampleList = meaningNex.get(meaning);
			for (int i=0 ; i< exampleList.size() ; i++) {
				if (exampleList.get(i) == sentence) {
					exampleList.remove(i);
				}
			}
			System.out.println(sentence + exampleList.get(0));

		}
		else {
			System.err.println(GlobalProperties.getIntlString("Can't_remove_example_sentence._Since_meaning_doesn't_exists\n"));
		}

		//		exampleList = meaningNex.get(meaning);
	}

	public void removeMeaning(String meaning) {
		for (int i=0 ; i<meanings.size() ; i++) {
			if (meanings.get(i) == meaning) {
				meanings.remove(i);
				meaningNex.remove(meaning);
			}
		}
	}
	public void changeMeaning(String oldmean, String newmean) {
		for (int i=0 ; i<meanings.size() ; i++) {
			if (meanings.get(i) == oldmean) {
				meanings.set(i,newmean);
				meaningNex.remove(oldmean);
				if (!(meaningNex.containsKey(newmean))) {
					meaningNex.put(newmean, exampleList = new ArrayList<String>());
				}
				else {
					System.err.println(GlobalProperties.getIntlString("Meaning_already_exists\n"));
				}
			}
		}
	}
	public void changeExample(String meaning, String oldex, String newex) {
		if ((meaningNex.containsKey(meaning))) {
//			exampleList = new ArrayList<String>();
			exampleList = meaningNex.get(meaning);
			for (int i=0 ; i< exampleList.size() ; i++) {
				if (exampleList.get(i) == oldex) {
					exampleList.set(i,newex);
				}
			}
		}
		else {
			System.err.println(GlobalProperties.getIntlString("Can't_remove_example_sentence._Since_meaning_doesn't_exists\n"));
		}
	}
}
