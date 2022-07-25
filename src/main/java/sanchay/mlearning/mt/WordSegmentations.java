/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.mlearning.common.ModelScore;

/**
 *
 * @author anil
 */
public class WordSegmentations {
    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected String srcWrd;
    protected LinkedHashMap segmentations;

    int pruneTopN = 10;

    public WordSegmentations(String srcWrd, Collection partitions)
    {
        this.srcWrd = srcWrd;
        segmentations = new LinkedHashMap(0, 100);

        if(partitions != null)
        {
            Iterator itr = partitions.iterator();

            while(itr.hasNext())
            {
                String partition = (String) itr.next();
                WordSegmentationScores segmentationScores = new WordSegmentationScores();

                addSegmentationScores(partition, segmentationScores);
            }
        }
    }

    /**
     * @return the srcWrd
     */
    public String getSrcWrd() {
        return srcWrd;
    }

    /**
     * @param srcWrd the srcWrd to set
     */
    public void setSrcWrd(String srcWrd) {
        this.srcWrd = srcWrd;
    }

    public int countSegments()
    {
        if(segmentations == null)
            return 0;

        return segmentations.size();
    }

    public Iterator getSegmentations()
    {
        if(segmentations == null)
            return null;

        return segmentations.keySet().iterator();
    }

    public WordSegmentationScores getSegmentationScores(String segmentation)
    {
        if(segmentations == null)
            return null;

        return (WordSegmentationScores) segmentations.get(segmentation);
    }

    public void addSegmentationScores(String segmentation, WordSegmentationScores scores)
    {
        if(segmentations == null)
            segmentations = new LinkedHashMap(0, 100);

        segmentations.put(segmentation, scores);
    }

    public void removeSegmentationScores(String segmentation)
    {
        if(segmentations == null)
            return;

        segmentations.remove(segmentation);
    }

    public void readSegmentations(String path, String charset) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;

        if(charset != null && charset.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

        String line = "";

        while((line = inReader.readLine()) != null)
        {
            if(line.equals("") == false)
            {
                WordSegmentationScores segmentationScores = new WordSegmentationScores();

                addSegmentationScores(line, segmentationScores);
            }
        }
    }

    public void printSegmentations(PrintStream ps)
    {
        Iterator sgmItr = getSegmentations();

        while(sgmItr.hasNext())
        {
            String segmentation = (String) sgmItr.next();
            ps.println(segmentation);
        }
    }

    public Vector sortSegmentations(PhraseTranslationTable phraseTranslationTable)
    {
        Vector<ModelScore<String, Double>> sortedScores = new Vector<ModelScore<String, Double>>(100, 100);

        Iterator sgmItr = getSegmentations();

        while (sgmItr.hasNext()) {
            String segmentation = (String) sgmItr.next();
//            WordSegmentationScores segmentationScores = getSegmentationScores(segmentation);

            double s = 0.0;

            String segements[] = segmentation.split(GlobalProperties.getIntlString("_"));

            for (int i = 0; i < segements.length; i++)
            {
                s *= phraseTranslationTable.getPhraseTranslationScores(segements[i]);
            }

            ModelScore<String, Double> ms = new ModelScore<String, Double>(segmentation, new Double(s));
            sortedScores.add(ms);
        }

        Collections.sort(sortedScores, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) o2).compareTo((Comparable) o1);
            }
        });

        int count = Math.min(sortedScores.size(), pruneTopN);

        segmentations.clear();

        for (int i = 0; i < count; i++) {
            ModelScore<String, Double> ms = sortedScores.get(i);

            WordSegmentationScores s = new WordSegmentationScores();
            s.setSegmentationScore(ms.modelScore);

            addSegmentationScores((String) ms.modelKey, s);
        }

        return sortedScores;
    }

    public static void main(String args[])
    {
        WordSegmentations wordSegmentations = new WordSegmentations("", null);

        try {
            wordSegmentations.readSegmentations("/home/anil/tmp/feature_based_code/translit-test-data-hindi.txt-segment-unique.txt", GlobalProperties.getIntlString("UTF-8"));

            PrintStream ps = new PrintStream("/home/anil/tmp/feature_based_code/translit-test-data-hindi.txt-segment-formatted.txt");

            wordSegmentations.printSegmentations(ps);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordSegmentations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordSegmentations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
