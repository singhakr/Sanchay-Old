/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.text.spell;

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *
 * @author anil
 */
public class SanchaySpellDictionaryHashMap extends SpellDictionaryHashMap {

    private final static int INITIAL_CAPACITY = 16 * 1024;

    private File dictFile = null;

    public SanchaySpellDictionaryHashMap(Reader wordList) throws IOException {
//        super.super((File) null);
        createDictionary(new BufferedReader(wordList));
    }

    public SanchaySpellDictionaryHashMap(File wordList, String charset) throws FileNotFoundException, IOException {
        this(new BufferedReader(new InputStreamReader(new FileInputStream(wordList), charset)));
        dictFile = wordList;
    }

    public SanchaySpellDictionaryHashMap(File wordList, File phonetic, String charset) throws FileNotFoundException, IOException {
        super(phonetic);
        dictFile = wordList;
        createDictionary(new BufferedReader(new InputStreamReader(new FileInputStream(wordList), charset)));
    }
}
