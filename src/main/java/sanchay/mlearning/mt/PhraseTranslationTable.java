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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.GlobalProperties;
import sanchay.mlearning.common.ModelScore;
import sanchay.mlearning.common.ModelScoreEx;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class PhraseTranslationTable {

//    Hash of Hash
    String langEnc = GlobalProperties.getIntlString("hin::utf8");
    String charset = GlobalProperties.getIntlString("UTF-8");

    LinkedHashMap phraseTranslationScores;
    LinkedHashMap maxPhraseTranslationScores;

    double minScore = 0.003;
//    double minScore = 0.0001;

    public PhraseTranslationTable()
    {
        phraseTranslationScores = new LinkedHashMap(0, 100);
        maxPhraseTranslationScores = new LinkedHashMap(0, 100);
    }

    public int countSrcPhrases()
    {
        if(phraseTranslationScores == null)
            return 0;

        return phraseTranslationScores.size();
    }

    public int countTgtPhrases(String srcPhrase)
    {
        if(phraseTranslationScores == null)
            return 0;

        LinkedHashMap tgtScores = (LinkedHashMap) phraseTranslationScores.get(srcPhrase);

        if(tgtScores == null)
            return 0;

        return tgtScores.size();
    }

    public Iterator getSrcPhrases()
    {
        if(phraseTranslationScores == null)
            return null;

        return phraseTranslationScores.keySet().iterator();
    }

    public Iterator getTgtPhrases(String srcPhrase)
    {
        if(phraseTranslationScores == null)
            return null;

        LinkedHashMap tgtScores = (LinkedHashMap) phraseTranslationScores.get(srcPhrase);

        if(tgtScores == null)
            return null;

        return tgtScores.keySet().iterator();
    }

    public PhraseTranslationScores getPhraseTranslationScores(String srcPhrase, String tgtPhrase)
    {
        LinkedHashMap tgtScores = (LinkedHashMap) phraseTranslationScores.get(srcPhrase);

        if(tgtScores == null)
            return null;

        return (PhraseTranslationScores) tgtScores.get(tgtPhrase);
    }
    
    public double getPhraseTranslationScores(String srcPhrase)
    {
        LinkedHashMap tgtScores = (LinkedHashMap) phraseTranslationScores.get(srcPhrase);

        if(tgtScores == null)
            return 0.0;

        Double ds = (Double) maxPhraseTranslationScores.get(srcPhrase);

        return ds.doubleValue();
    }

    public void addPhraseTranslationScores(String srcPhrase, String tgtPhrase, PhraseTranslationScores scores)
    {
        LinkedHashMap tgtScores = (LinkedHashMap) phraseTranslationScores.get(srcPhrase);

        if(tgtScores == null)
        {
            tgtScores = new LinkedHashMap(0, 100);

            phraseTranslationScores.put(srcPhrase, tgtScores);
        }

        tgtScores.put(tgtPhrase, scores);
    }

    public void removePhraseTranslationScores(String srcPhrase)
    {
        LinkedHashMap tgtScores = (LinkedHashMap) phraseTranslationScores.get(srcPhrase);

        if(tgtScores == null)
            return;

        phraseTranslationScores.remove(srcPhrase);
    }

    public void readTranslationTable(String path, String charset) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;

        if(charset != null && charset.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

        String line = "";

        while((line = inReader.readLine()) != null)
        {
            if(line.equals(""))
                continue;

            String parts[] = line.split("\\|\\|\\|");

            String srcPhrase = parts[0].trim();
            String tgtPhrase = parts[1].trim();

            String alignment = parts[2].trim();
            String revAlignment = parts[3].trim();

            String scoresStr = parts[4].trim();

            String scores[] = scoresStr.split(" ");

            String translationScoresStr = scores[0].trim();
            String revTranslationScoresStr = scores[1].trim();

            double tscore = Double.parseDouble(translationScoresStr);
            double rtscore = Double.parseDouble(revTranslationScoresStr);

            String srcPhraseNoSpace = srcPhrase.replaceAll(" ", "");

            if(((double) srcPhraseNoSpace.length() / (double) srcPhraseNoSpace.length()) > 2.0)
                    continue;
            else if(((double) srcPhraseNoSpace.length() / (double) srcPhraseNoSpace.length()) > 2.0)
                    continue;

            Pattern p = Pattern.compile("[a-zA-Z ]+");
            Matcher m = p.matcher(tgtPhrase);

            if(m.find() == false)
                continue;

            if(srcPhraseNoSpace.length() > 2)
                continue;
            else if(srcPhraseNoSpace.length() == 1)
            {
                if(tscore < minScore * 100.0 || rtscore < minScore * 100.0 || (tscore * rtscore) < Math.pow(minScore * 100.0, 2.0))
//                if(tscore < minScore * 100.0 || rtscore < minScore * 100.0)
                    continue;
            }
            else if(srcPhraseNoSpace.length() == 2)
            {
                if(UtilityFunctions.isASCIIVowel(srcPhraseNoSpace.charAt(0)) == true
                        && UtilityFunctions.isASCIIVowel(srcPhraseNoSpace.charAt(1)) == false)
                    continue;
                
                if(tscore < minScore * 30.0 || rtscore < minScore * 30.0 || (tscore * rtscore) < Math.pow(minScore * 30.0, 2.0))
//                if(tscore < minScore * 50.0 || rtscore < minScore * 50.0)
                    continue;
            }
//            else
//            {
//                if(!(UtilityFunctions.isASCIIVowel(srcPhraseNoSpace.charAt(0)) == false
//                        && UtilityFunctions.isASCIIVowel(srcPhraseNoSpace.charAt(0)) == false
//                        && srcPhraseNoSpace.charAt(0) == 'a'))
//                    continue;
//
//                char c2 = srcPhraseNoSpace.charAt(1);
//
//                if(UtilityFunctions.isASCIIVowel(c2))
//                    continue;
//
//                if(UtilityFunctions.isASCIIVowel(srcPhraseNoSpace.charAt(0)) == true
//                        && UtilityFunctions.isASCIIVowel(c2) == false
//                        && UtilityFunctions.isASCIIVowel(srcPhraseNoSpace.charAt(2)) == true)
//                    continue;
//
//                if(UtilityFunctions.isASCIIVowel(srcPhraseNoSpace.charAt(0)) == false
//                        && UtilityFunctions.isASCIIVowel(c2) == false
//                        && srcPhraseNoSpace.charAt(2) != 'a')
//                    continue;
//
//                if(tscore < minScore || rtscore < minScore || (tscore * rtscore) < Math.pow(minScore, 2.0))
//                    continue;
////                if(tscore < minScore || rtscore < minScore)
//                    continue;
//            }

            PhraseTranslationScores translationScores = new PhraseTranslationScores();

            translationScores.setTranlsationScore(Double.parseDouble(translationScoresStr));
            translationScores.setRevTranlsationScore(Double.parseDouble(revTranslationScoresStr));

            addPhraseTranslationScores(srcPhrase, tgtPhrase, translationScores);
        }

//        pruneAndSortPhraseTranslationScores(3);
//        calcMaxPhraseTranslationScores();
    }

    public void pruneAndSortPhraseTranslationScores(int pruneTopN)
    {
        Iterator srcItr = getSrcPhrases();

        int scount = countSrcPhrases();

        Vector srcVec = new Vector(scount);

        while(srcItr.hasNext())
        {
            String srcPhrase = (String) srcItr.next();
            srcVec.add(srcPhrase);
        }

        for (int i = 0; i < scount; i++)
            pruneAndSortPhraseTranslationScores((String) srcVec.get(i), pruneTopN);
    }

    public Vector pruneAndSortPhraseTranslationScores(String srcPhrase, int pruneTopN)
    {
        Vector<ModelScoreEx<String, Double, PhraseTranslationScores>> sortedScores
                = new Vector<ModelScoreEx<String, Double, PhraseTranslationScores>>(100, 100);

        Iterator tgtItr = getTgtPhrases(srcPhrase);

        while (tgtItr.hasNext()) {
            String tgtPhrase = (String) tgtItr.next();
            PhraseTranslationScores translationScores = getPhraseTranslationScores(srcPhrase, tgtPhrase);

            double s = translationScores.getTranlsationScore() * translationScores.getRevTranlsationScore();

            ModelScoreEx<String, Double, PhraseTranslationScores> ms = new ModelScoreEx<String, Double, PhraseTranslationScores>(tgtPhrase, new Double(s), translationScores);
            sortedScores.add(ms);
        }

        Collections.sort(sortedScores, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) o2).compareTo((Comparable) o1);
            }
        });

        int count = Math.min(sortedScores.size(), pruneTopN);

        removePhraseTranslationScores(srcPhrase);

        for (int i = 0; i < count; i++) {
            ModelScoreEx<String, Double, PhraseTranslationScores> ms = sortedScores.get(i);

            addPhraseTranslationScores(srcPhrase, (String) ms.modelKey, (PhraseTranslationScores) ms.modelObject);
        }

        return sortedScores;

    }

    protected void calcMaxPhraseTranslationScores()
    {
        Iterator srcItr = getSrcPhrases();

        while(srcItr.hasNext())
        {
            String srcPhrase = (String) srcItr.next();

            Iterator tgtItr = getTgtPhrases(srcPhrase);

            double s = 0.0;
            double max = 0.0;

            while(tgtItr.hasNext())
            {
                String tgtPhrase = (String) tgtItr.next();

                PhraseTranslationScores scores = getPhraseTranslationScores(srcPhrase, tgtPhrase);

                s = scores.getTranlsationScore() * scores.getRevTranlsationScore();

                if(max < s)
                {
                    max = s;
                }
            }

            maxPhraseTranslationScores.put(srcPhrase, new Double(max));
        }
    }


    public void printTranslationTable(PrintStream ps) throws FileNotFoundException, IOException
    {
        Iterator srcItr = getSrcPhrases();

        while(srcItr.hasNext())
        {
            String srcPhrase = (String) srcItr.next();

            Iterator tgtItr = getTgtPhrases(srcPhrase);

            while(tgtItr.hasNext())
            {
                String tgtPhrase = (String) tgtItr.next();

                PhraseTranslationScores scores = getPhraseTranslationScores(srcPhrase, tgtPhrase);

                ps.println(srcPhrase + " ||| " + tgtPhrase + " ||| " + scores);
            }
        }
    }

    public void printAllSrcPhrases(PrintStream ps) throws FileNotFoundException, IOException
    {
        Iterator srcItr = getSrcPhrases();

        while(srcItr.hasNext())
        {
            String srcPhrase = (String) srcItr.next();
            ps.println(srcPhrase);
        }
    }

    public void printAllTgtPhrases(PrintStream ps) throws FileNotFoundException, IOException
    {
        Iterator srcItr = getSrcPhrases();

        while(srcItr.hasNext())
        {
            String srcPhrase = (String) srcItr.next();

            Iterator tgtItr = getTgtPhrases(srcPhrase);

            while(tgtItr.hasNext())
            {
                String tgtPhrase = (String) tgtItr.next();
                ps.println(tgtPhrase);
            }
        }
    }

    public static void main(String args[])
    {
        PhraseTranslationTable ptable = new PhraseTranslationTable();

        try {
            ptable.readTranslationTable("/home/anil/tmp/feature_based_code/phrase-table.0-0", GlobalProperties.getIntlString("UTF-8"));
//            ptable.printTranslationTable(System.out);

            PrintStream ps = new PrintStream("/home/anil/tmp/feature_based_code/src-phrases.txt");
            
            ptable.printAllSrcPhrases(ps);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PhraseTranslationTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PhraseTranslationTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
