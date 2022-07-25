/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.corpus.ssf.SSFStory;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.MultiKeyValueProperties;

/**
 *
 * @author anil
 */
public class CorpusStatistics {

    protected String langEnc;

    protected String statsPath = "workspace/syn-annotation/global-statistics.txt";
    protected String charset = "UTF-8";

    protected SSFStory ssfStory;

    protected LinkedHashMap<String, Integer> wordFreq;
    protected LinkedHashMap<String, Integer> posTagFreq;
    protected LinkedHashMap<String, Integer> grelFreq;
    protected LinkedHashMap<String, Integer> wordTagFreq;
    protected LinkedHashMap<String, Integer> chunkTagFreq;
    protected LinkedHashMap<String, Integer> unchunkedWordFreq;
    protected LinkedHashMap<String, Integer> chunkGRelFreq;

    protected MultiKeyValueProperties globalStatistics;

    /** Creates new form CorpusStatisticsJPanel */
    public CorpusStatistics(boolean global) {

        wordFreq = new LinkedHashMap<String, Integer>();
        posTagFreq = new LinkedHashMap<String, Integer>();
        grelFreq = new LinkedHashMap<String, Integer>();
        wordTagFreq = new LinkedHashMap<String, Integer>();
        chunkTagFreq = new LinkedHashMap<String, Integer>();
        chunkGRelFreq = new LinkedHashMap<String, Integer>();
        unchunkedWordFreq = new LinkedHashMap<String, Integer>();

        globalStatistics = new MultiKeyValueProperties();

        if(global)
        {
            File sf = new File(statsPath);

            try {
                if(sf.exists())
                {
                    globalStatistics.read(statsPath, charset);
                }
                else
                {
                    globalStatistics.save(statsPath, charset);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void save()
    {
        globalStatistics.getPropertiesValue("WordFrequency").loadMap(getWordFreq());

        globalStatistics.getPropertiesValue("POSTagFrequency").loadMap(getPOSTagFreq());

        globalStatistics.getPropertiesValue("GroupRelationFrequency").loadMap(getGRelFreq());

        globalStatistics.getPropertiesValue("WordPOSTagFrequency").loadMap(getWordTagFreq());

        globalStatistics.getPropertiesValue("ChunkTagFrequency").loadMap(getChunkTagFreq());

        globalStatistics.getPropertiesValue("ChunkGroupRelationFrequency").loadMap(getChunkGRelFreq());

        globalStatistics.getPropertiesValue("UnchunkedWordFrequency").loadMap(getChunkGRelFreq());

        try {
            // TODO add your handling code here:
            globalStatistics.save(getStatsPath(), getCharset());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clear()
    {

        File sf = new File(getStatsPath());

        try {

            if(sf.exists())
            {
                sf.delete();
            }

            globalStatistics.save(getStatsPath(), getCharset());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initStats(String langEnc, SSFStory ssfStory)
    {
        this.setLangEnc(langEnc);
        this.setSsfStory(ssfStory);

        loadData(ssfStory);
    }

    public void initStats(String langEnc, LinkedHashMap<File, SSFStory> selStories)
    {
        this.setLangEnc(langEnc);

        loadData(selStories);
    }

    public void loadData(SSFStory localSSFStory)
    {
        if(localSSFStory == null)
            return;

//        loadGlobalData();

        if(getWordFreq() == null)
            wordFreq = localSSFStory.getWordFreq();
        else
            getWordFreq().putAll(localSSFStory.getWordFreq());

        if(getPOSTagFreq() == null)
            posTagFreq = localSSFStory.getPOSTagFreq();
        else
            getPOSTagFreq().putAll(localSSFStory.getPOSTagFreq());

        if(getGRelFreq() == null)
            grelFreq = localSSFStory.getGroupRelationFreq();
        else
            getGRelFreq().putAll(localSSFStory.getGroupRelationFreq());

        if(getWordTagFreq() == null)
            wordTagFreq = localSSFStory.getWordTagPairFreq();
        else
            getWordTagFreq().putAll(localSSFStory.getWordTagPairFreq());

        if(getChunkTagFreq() == null)
            chunkTagFreq = localSSFStory.getChunkTagFreq();
        else
            getChunkTagFreq().putAll(localSSFStory.getChunkTagFreq());

        if(getChunkGRelFreq() == null)
            chunkGRelFreq = localSSFStory.getChunkRelationFreq();
        else
            getChunkGRelFreq().putAll(localSSFStory.getChunkRelationFreq());

        if(getUnchunkedWordFreq() == null)
            unchunkedWordFreq = localSSFStory.getUnchunkedWordFreq();
        else
            getUnchunkedWordFreq().putAll(localSSFStory.getUnchunkedWordFreq());
    }

    public void loadGlobalData()
    {
        if(globalStatistics.getPropertiesValue("WordFrequency") != null)
            globalStatistics.getPropertiesValue("WordFrequency").fillMap(getWordFreq());
        else
            globalStatistics.addProperties("WordFrequency", new KeyValueProperties());

        if(globalStatistics.getPropertiesValue("POSTagFrequency") != null)
            globalStatistics.getPropertiesValue("POSTagFrequency").fillMap(getPOSTagFreq());
        else
            globalStatistics.addProperties("POSTagFrequency", new KeyValueProperties());

        if(globalStatistics.getPropertiesValue("GroupRelationFrequency") != null)
            globalStatistics.getPropertiesValue("GroupRelationFrequency").fillMap(getGRelFreq());
        else
            globalStatistics.addProperties("GroupRelationFrequency", new KeyValueProperties());

        if(globalStatistics.getPropertiesValue("WordPOSTagFrequency") != null)
            globalStatistics.getPropertiesValue("WordPOSTagFrequency").fillMap(getWordTagFreq());
        else
            globalStatistics.addProperties("WordPOSTagFrequency", new KeyValueProperties());

        if(globalStatistics.getPropertiesValue("ChunkTagFrequency") != null)
            globalStatistics.getPropertiesValue("ChunkTagFrequency").fillMap(getChunkTagFreq());
        else
            globalStatistics.addProperties("ChunkTagFrequency", new KeyValueProperties());

        if(globalStatistics.getPropertiesValue("ChunkGroupRelationFrequency") != null)
            globalStatistics.getPropertiesValue("ChunkGroupRelationFrequency").fillMap(getChunkGRelFreq());
        else
            globalStatistics.addProperties("ChunkGroupRelationFrequency", new KeyValueProperties());

        if(globalStatistics.getPropertiesValue("UnchunkedWordFrequency") != null)
            globalStatistics.getPropertiesValue("UnchunkedWordFrequency").fillMap(getUnchunkedWordFreq());
        else
            globalStatistics.addProperties("UnchunkedWordFrequency", new KeyValueProperties());
    }

    public void loadData(LinkedHashMap<File, SSFStory> selStories)
    {
        if(selStories == null || selStories.size() == 0)
            return;

        loadGlobalData();

        Iterator<File> sitr = selStories.keySet().iterator();

        while(sitr.hasNext())
        {
            SSFStory localSSFStory = selStories.get(sitr.next());

            // Change
            setSsfStory(localSSFStory);

            if(getWordFreq() == null)
                wordFreq = localSSFStory.getWordFreq();
            else
                getWordFreq().putAll(localSSFStory.getWordFreq());

            if(getPOSTagFreq() == null)
                posTagFreq = localSSFStory.getPOSTagFreq();
            else
                getPOSTagFreq().putAll(localSSFStory.getPOSTagFreq());

            if(getGRelFreq() == null)
                grelFreq = localSSFStory.getGroupRelationFreq();
            else
                getGRelFreq().putAll(localSSFStory.getGroupRelationFreq());

            if(getWordTagFreq() == null)
                wordTagFreq = localSSFStory.getWordTagPairFreq();
            else
                getWordTagFreq().putAll(localSSFStory.getWordTagPairFreq());

            if(getChunkTagFreq() == null)
                chunkTagFreq = localSSFStory.getChunkTagFreq();
            else
                getChunkTagFreq().putAll(localSSFStory.getChunkTagFreq());

            if(getChunkGRelFreq() == null)
                chunkGRelFreq = localSSFStory.getChunkRelationFreq();
            else
                getChunkGRelFreq().putAll(localSSFStory.getChunkRelationFreq());

            if(getUnchunkedWordFreq() == null)
                unchunkedWordFreq = localSSFStory.getUnchunkedWordFreq();
            else
                getUnchunkedWordFreq().putAll(localSSFStory.getUnchunkedWordFreq());
        }
    }

    /**
     * @return the langEnc
     */
    public String getLangEnc() {
        return langEnc;
    }

    /**
     * @param langEnc the langEnc to set
     */
    public void setLangEnc(String langEnc) {
        this.langEnc = langEnc;
    }

    /**
     * @return the statsPath
     */
    public String getStatsPath() {
        return statsPath;
    }

    /**
     * @param statsPath the statsPath to set
     */
    public void setStatsPath(String statsPath) {
        this.statsPath = statsPath;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return the ssfStory
     */
    public SSFStory getSsfStory() {
        return ssfStory;
    }

    /**
     * @param ssfStory the ssfStory to set
     */
    public void setSsfStory(SSFStory ssfStory) {
        this.ssfStory = ssfStory;
    }

    /**
     * @return the wordFreq
     */
    public LinkedHashMap<String, Integer> getWordFreq() {
        return wordFreq;
    }

    /**
     * @return the posTagFreq
     */
    public LinkedHashMap<String, Integer> getPOSTagFreq() {
        return posTagFreq;
    }

    /**
     * @return the grelFreq
     */
    public LinkedHashMap<String, Integer> getGRelFreq() {
        return grelFreq;
    }

    /**
     * @return the wordTagFreq
     */
    public LinkedHashMap<String, Integer> getWordTagFreq() {
        return wordTagFreq;
    }

    /**
     * @return the chunkTagFreq
     */
    public LinkedHashMap<String, Integer> getChunkTagFreq() {
        return chunkTagFreq;
    }

    /**
     * @return the unchunkedWordFreq
     */
    public LinkedHashMap<String, Integer> getUnchunkedWordFreq() {
        return unchunkedWordFreq;
    }

    /**
     * @return the chunkGRelFreq
     */
    public LinkedHashMap<String, Integer> getChunkGRelFreq() {
        return chunkGRelFreq;
    }

    public void copyDataFrom(CorpusStatistics corpusStats)
    {
        wordFreq.putAll(corpusStats.wordFreq);
        posTagFreq.putAll(corpusStats.posTagFreq);
        grelFreq.putAll(corpusStats.grelFreq);
        wordTagFreq.putAll(corpusStats.wordTagFreq);
        chunkTagFreq.putAll(corpusStats.chunkTagFreq);
        unchunkedWordFreq.putAll(corpusStats.unchunkedWordFreq);
        chunkGRelFreq.putAll(corpusStats.chunkGRelFreq);     
    }

    public String getTagsForWord(String word)
    {
        String tags = null;

        Iterator<String> itr = getWordTagFreq().keySet().iterator();

        while(itr.hasNext())
        {
            String wrdTag = itr.next();

            String parts[] = wrdTag.split("/");

            if(parts.length == 2 && parts[0].equals(word))
            {
                String tag = parts[1];

                if(tags == null)
                    tags = tag;
                else
                    tags += ", " + tag;
            }
        }

        return tags;
    }

    public String getWordsForTag(String tag)
    {
        String words = null;

        Iterator<String> itr = getWordTagFreq().keySet().iterator();

        while(itr.hasNext())
        {
            String wrdTag = itr.next();

            String parts[] = wrdTag.split("/");

            if(parts.length == 2 && parts[1].equals(tag))
            {
                String word = parts[0];

                if(words == null)
                    words = word;
                else
                    words += ", " + word;
            }
        }

        return words;
    }

    public String getChunksForRelation(String rel)
    {
        String chunks = null;

        Iterator<String> itr = getChunkGRelFreq().keySet().iterator();

        while(itr.hasNext())
        {
            String chunkRel = itr.next();

            String parts[] = chunkRel.split("::");

            if(parts.length == 2 && parts[1].equals(rel))
            {
                String chunk = parts[0];

                if(chunks == null)
                    chunks = chunk;
                else
                    chunks += ", " + chunk;
            }
        }

        return chunks;
    }

    public String getRelationsForChunk(String chunk)
    {
        String rels = null;

        Iterator<String> itr = getChunkGRelFreq().keySet().iterator();

        while(itr.hasNext())
        {
            String chunkRel = itr.next();

            String parts[] = chunkRel.split("::");

            if(parts.length == 2 && parts[0].equals(chunk))
            {
                String rel = parts[1];

                if(rels == null)
                    rels = rel;
                else
                    rels += ", " + rel;
            }
        }

        return rels;
    }
}
