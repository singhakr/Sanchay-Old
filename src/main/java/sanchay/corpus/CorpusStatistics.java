/*
 * CorpusStatistics.java
 *
 * Created on January 21, 2008, 9:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus;

import java.io.PrintStream;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class CorpusStatistics {
    
    protected long paragraphs;
    protected long sentences;
    protected long words;
    protected long characters;
    
    /** Creates a new instance of CorpusStatistics */
    public CorpusStatistics() {
    }

    public long getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(long paragraphs) {
        this.paragraphs = paragraphs;
    }

    public long getSentences() {
        return sentences;
    }

    public void setSentences(long sentences) {
        this.sentences = sentences;
    }

    public long getWords() {
        return words;
    }

    public void setWords(long words) {
        this.words = words;
    }

    public long getCharacters() {
        return characters;
    }

    public void setCharacters(long characters) {
        this.characters = characters;
    }
    
    public void print(PrintStream ps)
    {
        ps.println(GlobalProperties.getIntlString("Paragraphs:_") + paragraphs);
        ps.println(GlobalProperties.getIntlString("Sentences:_") + sentences);
        ps.println(GlobalProperties.getIntlString("Words:_") + words);
        ps.println(GlobalProperties.getIntlString("Characters:_") + characters);
    }
    
}
