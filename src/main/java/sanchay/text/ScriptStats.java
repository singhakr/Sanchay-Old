/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.mlearning.common.ModelScore;
import sanchay.properties.PropertyTokens;
import sanchay.util.UtilityFunctions;


/**
 *
 * @author anil
 */
public class ScriptStats {

    PropertyTokens filePT;
    TextNormalizer textNormalizer;

    Hashtable lettersMap;
    Hashtable tilesMap;
    Hashtable ottuTilesMap;
    Hashtable cellsMap;
    Hashtable wordLengthsLetterMap;
    Hashtable wordLengthsTileMap;
    Hashtable wordLengthsOttuTileMap;
    Hashtable wordLengthsCellMap;

    LinkedHashMap<String, Integer> wordList;

    public ScriptStats(String f, String langEnc, String cs)
    {
        lettersMap = new Hashtable();
        tilesMap = new Hashtable();
        ottuTilesMap = new Hashtable();
        cellsMap = new Hashtable();
        wordLengthsLetterMap = new Hashtable();
        wordLengthsTileMap = new Hashtable();
        wordLengthsOttuTileMap = new Hashtable();
        wordLengthsCellMap = new Hashtable();

        wordList = new LinkedHashMap<String, Integer>();

        filePT = new PropertyTokens();

        textNormalizer = new TextNormalizer(langEnc, cs, "", "", false);

        try
        {
            filePT.read(f, cs);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(ScriptStats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(ScriptStats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PropertyTokens getFilePT()
    {
        return filePT;
    }

    public void computeStats()
    {
        int count = filePT.countTokens();

        for (int i = 0; i < count; i++)
        {
            String line = filePT.getToken(i);

            String words[] = line.split("[\\s]+");

            for (int j = 0; j < words.length; j++)
            {
                String word = words[j];

                if(textNormalizer.isPossiblyValidWord(word))
                {
                    Pattern halantEnding = Pattern.compile("(?dum)\u0C4D$");

                    Matcher m = halantEnding.matcher(word);

                    if(m.find())
                    {
                        continue;
//                        System.out.println("Halant ending word: " + word);
                    }

                    Integer countInt = (Integer) wordList.get(word);

                    if(countInt == null)
                    {
                        wordList.put(word, new Integer(1));
                    }
                    else
                    {
                        wordList.put(word, new Integer(countInt.intValue() + 1));
                    }

                    String letters[] = word.split("");

                    for (int k = 0; k < letters.length; k++)
                    {
                        String letter = letters[k];

                        countInt = (Integer) lettersMap.get(letter);

                        if(countInt == null)
                            lettersMap.put(letter, new Integer(1));
                        else
                            lettersMap.put(letter, new Integer(countInt.intValue() + 1));
                    }

                    String tiles[] = textNormalizer.getTiles(word, false, false);

                    for (int k = 0; k < tiles.length; k++)
                    {
                        String tile = tiles[k];

                        countInt = (Integer) tilesMap.get(tile);

                        if(countInt == null)
                            tilesMap.put(tile, new Integer(1));
                        else
                            tilesMap.put(tile, new Integer(countInt.intValue() + 1));
                    }

//                    Pattern halantEnding = Pattern.compile("(?dum)\u0C4D$");
//
//                    Matcher m = halantEnding.matcher(word);
//
//                    if(m.find())
//                    {
//                        System.out.println("Halant ending word: " + word);
//                    }
//
//                    else
//                    {
//                        Pattern halantFound = Pattern.compile("(?dum)\u0C4D");
//
//                        Matcher mh = halantFound.matcher(word);
//
//                        if(mh.find())
//                        {
//                            System.out.println("Halant found: " + word);
//                        }
//                    }

                    String ottuTiles[] = textNormalizer.getTiles(word, false, true);

                    for (int k = 0; k < ottuTiles.length; k++)
                    {
                        String tile = ottuTiles[k];

                        countInt = (Integer) ottuTilesMap.get(tile);

                        if(countInt == null)
                            ottuTilesMap.put(tile, new Integer(1));
                        else
                            ottuTilesMap.put(tile, new Integer(countInt.intValue() + 1));
                    }

                    String akshars[] = textNormalizer.getAkshars(word);

                    for (int k = 0; k < akshars.length; k++)
                    {
                        String sanyuktaakshar = akshars[k];

                        countInt = (Integer) cellsMap.get(sanyuktaakshar);

                        if(countInt == null)
                            cellsMap.put(sanyuktaakshar, new Integer(1));
                        else
                            cellsMap.put(sanyuktaakshar, new Integer(countInt.intValue() + 1));
                    }

                    countInt = (Integer) wordLengthsLetterMap.get(new Integer(letters.length));

                    if(countInt == null)
                        wordLengthsLetterMap.put(new Integer(letters.length), new Integer(1));
                    else
                        wordLengthsLetterMap.put(new Integer(letters.length), new Integer(countInt.intValue() + 1));

                    countInt = (Integer) wordLengthsTileMap.get(new Integer(tiles.length));

                    if(countInt == null)
                        wordLengthsTileMap.put(new Integer(tiles.length), new Integer(1));
                    else
                        wordLengthsTileMap.put(new Integer(tiles.length), new Integer(countInt.intValue() + 1));

                    countInt = (Integer) wordLengthsOttuTileMap.get(new Integer(ottuTiles.length));

                    if(countInt == null)
                        wordLengthsOttuTileMap.put(new Integer(ottuTiles.length), new Integer(1));
                    else
                        wordLengthsOttuTileMap.put(new Integer(ottuTiles.length), new Integer(countInt.intValue() + 1));

                    countInt = (Integer) wordLengthsCellMap.get(new Integer(akshars.length));

                    if(tiles.length != ottuTiles.length)
                    {
                        if(tiles.length < ottuTiles.length)
                            System.out.println("Different lengths (ottu):\t" + word + "\t" + tiles.length + "\t" + ottuTiles.length);
                        else
                            System.out.println("Different lengths (ottu):\t" + word + "\t" + tiles.length + "\t" + ottuTiles.length + "\t***");

                        System.out.println("\tTiles:\t" + word + "\t");
                        UtilityFunctions.printArray(tiles, System.out, "\t");
                        System.out.println("");
                        System.out.println("\tOttu Based Tiles:\t" + word + "\t");
                        UtilityFunctions.printArray(ottuTiles, System.out, "\t");
                        System.out.println("");
                    }

                    if(countInt == null)
                        wordLengthsCellMap.put(new Integer(akshars.length), new Integer(1));
                    else
                        wordLengthsCellMap.put(new Integer(akshars.length), new Integer(countInt.intValue() + 1));
                }
            }
        }

        wordList = ModelScore.sortElementsByScores(wordList, false);
    }

    public int getCountInOneCellWords(String str)
    {
        Iterator itr = wordList.keySet().iterator();

        Pattern p = Pattern.compile(str, Pattern.UNICODE_CASE | Pattern.CANON_EQ);

        int count = 0;

        while(itr.hasNext())
        {
            String word = (String) itr.next();
            String sanyuktaakshars[] = textNormalizer.getAkshars(word);

            if(sanyuktaakshars.length == 1)
            {
                Integer intCount = (Integer) wordList.get(word);

                Matcher m = p.matcher(word);

//                if(word.contains(str))
                if(m.find())
                    count += intCount;
            }
        }

        return count;
    }

    public String printStats(PrintStream ps)
    {
        if(ps == null)
            ps = System.out;

        String statString = "";

        ps.println("Letters (Characters):");
        statString += "Letters (Characters):\n";

        Enumeration enm = lettersMap.keys();

        while(enm.hasMoreElements())
        {
            String letter = (String) enm.nextElement();
            Integer intCount = (Integer) lettersMap.get(letter);

            ps.println(letter + "\t" + intCount);
            statString += letter + "\t" + intCount + "\n";
        }

//        ps.println("\nTiles:");
//        statString += "\nTiles:\n";
//
//        enm = tilesMap.keys();
//
//        while(enm.hasMoreElements())
//        {
//            String tile = (String) enm.nextElement();
//            Integer intCount = (Integer) tilesMap.get(tile);
//
//            ps.println(tile + "\t" + intCount);
//            statString += tile + "\t" + intCount + "\n";
//        }
//
//        ps.println("\nCentral Tiles:");
//        statString += "\nCentral Tiles:\n";
//
//        enm = tilesMap.keys();
//
//        while(enm.hasMoreElements())
//        {
//            String akshar = (String) enm.nextElement();
//
//            if(textNormalizer.getTileType(akshar) == TextNormalizer.AKSHAR)
//            {
//                Integer intCount = (Integer) tilesMap.get(akshar);
//
//                ps.println(akshar + "\t" + intCount);
//                statString += akshar + "\t" + intCount + "\n";
//            }
//        }
//
//        ps.println("\nOttu Based Central Tiles:");
//        statString += "\nOttu Based Central Tiles:\n";
//
//        enm = ottuTilesMap.keys();
//
//        while(enm.hasMoreElements())
//        {
//            String akshar = (String) enm.nextElement();
//
//            if(textNormalizer.getTileType(akshar) == TextNormalizer.AKSHAR)
//            {
//                Integer intCount = (Integer) ottuTilesMap.get(akshar);
//
//                ps.println(akshar + "\t" + intCount);
//                statString += akshar + "\t" + intCount + "\n";
//            }
//        }
//
//        ps.println("\nMaatraa Tiles:");
//        statString += "\nMaatraa Tiles:\n";
//
//        enm = tilesMap.keys();
//
//        while(enm.hasMoreElements())
//        {
//            String akshar = (String) enm.nextElement();
//
//            if(textNormalizer.getTileType(akshar) == TextNormalizer.MAATRAA)
//            {
//                Integer intCount = (Integer) tilesMap.get(akshar);
//
//                ps.println(akshar + "\t" + intCount);
//                statString += akshar + "\t" + intCount + "\n";
//            }
//        }
//
//        ps.println("\nArdhaakshar Tiles:");
//        statString += "\nArdhaakshar Tiles:\n";
//
//        enm = tilesMap.keys();
//
//        while(enm.hasMoreElements())
//        {
//            String akshar = (String) enm.nextElement();
//
//            if(textNormalizer.getTileType(akshar) == TextNormalizer.ARDHAAKSHAR)
//            {
//                Integer intCount = (Integer) tilesMap.get(akshar);
//
//                ps.println(akshar + "\t" + intCount);
//                statString += akshar + "\t" + intCount + "\n";
//            }
//        }
//
//        ps.println("\nOttu Tiles:");
//        statString += "\nOttu Tiles:\n";
//
//        enm = ottuTilesMap.keys();
//
//        while(enm.hasMoreElements())
//        {
//            String akshar = (String) enm.nextElement();
//
//            if(textNormalizer.getTileType(akshar) == TextNormalizer.OTTU)
//            {
//                Integer intCount = (Integer) ottuTilesMap.get(akshar);
//
//                ps.println(akshar + "\t" + intCount);
//                statString += akshar + "\t" + intCount + "\n";
//            }
//        }

        // Discounted for one cell words

        ps.println("\nCentral Tiles (Discounted for one cell words):");
        statString += "\nCentral Tiles (Discounted for one cell words):\n";

        enm = tilesMap.keys();

        while(enm.hasMoreElements())
        {
            String akshar = (String) enm.nextElement();

            if(textNormalizer.getTileType(akshar, false) == TextNormalizer.AKSHAR);
            {
                Integer intCount = (Integer) tilesMap.get(akshar);

                intCount -= getCountInOneCellWords(akshar);

                ps.println(akshar + "\t" + intCount);
                statString += akshar + "\t" + intCount + "\n";
            }
        }

        ps.println("\nOttu Based Central Tiles (Discounted for one cell words):");
        statString += "\nOttu Based Central Tiles (Discounted for one cell words):\n";

        enm = ottuTilesMap.keys();

        while(enm.hasMoreElements())
        {
            String akshar = (String) enm.nextElement();

            if(textNormalizer.getTileType(akshar, true) == TextNormalizer.AKSHAR)
            {
                Integer intCount = (Integer) ottuTilesMap.get(akshar);

                intCount -= getCountInOneCellWords(akshar);

                ps.println(akshar + "\t" + intCount);
                statString += akshar + "\t" + intCount + "\n";
            }
        }

        ps.println("\nMaatraa Tiles (Discounted for one cell words):");
        statString += "\nMaatraa Tiles (Discounted for one cell words):\n";

        enm = tilesMap.keys();

        while(enm.hasMoreElements())
        {
            String akshar = (String) enm.nextElement();

            if(textNormalizer.getTileType(akshar, false) == TextNormalizer.MAATRAA)
            {
                Integer intCount = (Integer) tilesMap.get(akshar);

                intCount -= getCountInOneCellWords(akshar);

                ps.println(akshar + "\t" + intCount);
                statString += akshar + "\t" + intCount + "\n";
            }
        }

        ps.println("\nArdhaakshar Tiles (Discounted for one cellwords):");
        statString += "\nArdhaakshar Tiles (Discounted for one cell words):\n";

        enm = tilesMap.keys();

        while(enm.hasMoreElements())
        {
            String akshar = (String) enm.nextElement();

            if(textNormalizer.getTileType(akshar, false) == TextNormalizer.ARDHAAKSHAR)
            {
                Integer intCount = (Integer) tilesMap.get(akshar);

                intCount -= getCountInOneCellWords(akshar);

                ps.println(akshar + "\t" + intCount);
                statString += akshar + "\t" + intCount + "\n";
            }
        }

        ps.println("\nOttu Tiles (Discounted for one cellwords):");
        statString += "\nOttu Tiles (Discounted for one cell words):\n";

        enm = ottuTilesMap.keys();

        while(enm.hasMoreElements())
        {
            String akshar = (String) enm.nextElement();

            if(textNormalizer.getTileType(akshar, true) == TextNormalizer.OTTU)
            {
                Integer intCount = (Integer) ottuTilesMap.get(akshar);

                intCount -= getCountInOneCellWords(akshar);

                ps.println(akshar + "\t" + intCount);
                statString += akshar + "\t" + intCount + "\n";
            }
        }

        // Cells

        ps.println("\nSanyuktaakshars (Cells):");
        statString += "\nSanyuktaakshars (Cells):\n";

        enm = cellsMap.keys();

        while(enm.hasMoreElements())
        {
            String sanyuktaakshar = (String) enm.nextElement();
            Integer intCount = (Integer) cellsMap.get(sanyuktaakshar);

            ps.println(sanyuktaakshar + "\t" + intCount);
            statString += sanyuktaakshar + "\t" + intCount + "\n";
        }

        ps.println("\nWord Lengths (Letters/Characters):");
        statString += "\nWord Lengths (Letters/Characters):\n";

        enm = wordLengthsLetterMap.keys();

        while(enm.hasMoreElements())
        {
            Integer length = (Integer) enm.nextElement();
            Integer intCount = (Integer) wordLengthsLetterMap.get(length);

            ps.println(length + "\t" + intCount);
            statString += length + "\t" + intCount + "\n";
        }

        ps.println("\nWord Lengths (Tiles):");
        statString += "\nWord Lengths (Tiles):\n";

        enm = wordLengthsTileMap.keys();

        while(enm.hasMoreElements())
        {
            Integer length = (Integer) enm.nextElement();
            Integer intCount = (Integer) wordLengthsTileMap.get(length);

            ps.println(length + "\t" + intCount);
            statString += length + "\t" + intCount + "\n";
        }

        ps.println("\nWord Lengths (Ottu Based Tiles):");
        statString += "\nWord Lengths (Ottu Based Tiles):\n";

        enm = wordLengthsOttuTileMap.keys();

        while(enm.hasMoreElements())
        {
            Integer length = (Integer) enm.nextElement();
            Integer intCount = (Integer) wordLengthsOttuTileMap.get(length);

            ps.println(length + "\t" + intCount);
            statString += length + "\t" + intCount + "\n";
        }

        ps.println("\nWord Lengths (Cells):");
        statString += "\nWord Lengths (Cells):\n";

        enm = wordLengthsCellMap.keys();

        while(enm.hasMoreElements())
        {
            Integer length = (Integer) enm.nextElement();
            Integer intCount = (Integer) wordLengthsCellMap.get(length);

            ps.println(length + "\t" + intCount);
            statString += length + "\t" + intCount + "\n";
        }

//        ps.println("\nSingle-Cell-Word_Frequencies:");
//        statString += "\nSingle-Cell-Word_Frequencies:\n";
//
//        Iterator itr = wordList.keySet().iterator();
//
//        while(itr.hasNext())
//        {
//            String word = (String) itr.next();
//            String sanyuktaakshars[] = textNormalizer.getAkshars(word);
//
//            if(sanyuktaakshars.length == 1)
//            {
//                Integer intCount = (Integer) wordList.get(word);
//
//                ps.println(word + "\t" + intCount);
//                statString += word + "\t" + intCount + "\n";
//            }
//        }

//        ps.println("\nWord Frequencies:");
//
//        itr = wordList.keySet().iterator();
//
//        while(itr.hasNext())
//        {
//            String word = (String) itr.next();
//            Integer intCount = (Integer) wordList.get(word);
//
//            ps.println(word + "\t" + intCount);
//        }

        return statString;
    }

    public String getMatchingWords(String regex, PrintStream ps)
    {
        String statString = "";
        
//        String regex = "(?dum)యీ";

        ps.println("\nWord containing " + regex + ":\n");

        regex = "(?dum)" + regex;

        Pattern p = Pattern.compile(regex);

        Iterator itr = wordList.keySet().iterator();

        while(itr.hasNext())
        {
            String word = (String) itr.next();
            String sanyuktaakshars[] = textNormalizer.getAkshars(word);

            Matcher m = p.matcher(word);

            if(m.find() && sanyuktaakshars.length > 1)
            {
                Integer intCount = (Integer) wordList.get(word);

                ps.println(word + "\t" + intCount);
            }
        }

        return statString;
    }

    public void saveStats(String path, String cs)
    {
        try {
            PrintStream ps = new PrintStream(path, cs);

            printStats(ps);

            ps.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptStats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ScriptStats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveMatchingWords(String regex, String path, String cs)
    {
        try {
            PrintStream ps = new PrintStream(path, cs);

            getMatchingWords(regex, ps);

            ps.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptStats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ScriptStats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args)
    {
        ScriptStats scriptStats = new ScriptStats("/home/anil/joro/data/script-stats/Translated-Material.txt", "tel::utf8", "UTF-8");

        scriptStats.computeStats();
//        scriptStats.printStats(System.out);
        scriptStats.saveMatchingWords("్ఖ", "/home/anil/joro/data/script-stats/script-stats-telugu-ottu/Translated-Material-script-kha-ottu.txt", "UTF-8");
        scriptStats.saveMatchingWords("్ఝ", "/home/anil/joro/data/script-stats/script-stats-telugu-ottu/Translated-Material-script-jha-ottu.txt", "UTF-8");
        scriptStats.saveMatchingWords("్ఙ", "/home/anil/joro/data/script-stats/script-stats-telugu-ottu/Translated-Material-script-nga-ottu.txt", "UTF-8");
//        scriptStats.saveStats("/home/anil/joro/data/script-stats/script-stats-telugu-ottu/Social-Sciences-script-stats-no-word-list.txt", "UTF-8");
    }
}
