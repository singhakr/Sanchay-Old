/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.GlobalProperties;
import sanchay.gui.common.SanchayLanguages;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author anil
 */
public class TextNormalizer {

    protected String langEnc;
    protected String charset;
    protected String srcPath;
    protected String tgtPath;

    protected boolean redirection;

    protected AksharData aksharData;
    protected KeyValueProperties spellingNormalizationRules;

    public static final int LETTER = 0;
    public static final int VOWEL = 1;
    public static final int CONSONANT = 2;
    public static final int AKSHAR = 3;
    public static final int MAATRAA = 4;
    public static final int ARDHAAKSHAR = 5;
    public static final int OTTU = 6;
    public static final int MINIMAL_AKSHAR = 7;
    public static final int SANYUKTAAKSHAR = 8;
    public static final int TILE = 9;
    public static final int CELL = 10;

    public static final int REDIRECTION = 0;
    public static final int FILE = 1;
    public static final int STRING = 2;

    public static final int INITIAL = 0;
    public static final int MEDIAL = 1;
    public static final int FINAL = 2;

    public TextNormalizer(String langEnc, String charset, String srcPath, String tgtPath, AksharData aksharData, boolean redirection)
    {
        this(langEnc, charset, srcPath, tgtPath, redirection);

        this.aksharData = aksharData;
    }

    public TextNormalizer(String langEnc, String charset, String srcPath, String tgtPath, boolean redirection)
    {
        super();
        
        this.langEnc = langEnc;
        this.charset = charset;
        this.srcPath = srcPath;
        this.tgtPath = tgtPath;

        this.redirection = redirection;

        aksharData = new AksharData(langEnc);
        aksharData.readScript(GlobalProperties.getHomeDirectory() + "/" + "data/text/devType-" + SanchayLanguages.getLanguageCodeFromLECode(langEnc) + ".txt");
        aksharData.makeGrammarAkshar();

        spellingNormalizationRules = new KeyValueProperties();
        
        try {
            spellingNormalizationRules.read(GlobalProperties.getHomeDirectory() + "/" + "data/normalization/spelling-norm-" + SanchayLanguages.getLanguageCodeFromLECode(langEnc) + ".txt", charset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextNormalizer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextNormalizer.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the srcPath
     */
    public String getSrcPath() {
        return srcPath;
    }

    /**
     * @param srcPath the srcPath to set
     */
    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    /**
     * @return the tgtPath
     */
    public String getTgtPath() {
        return tgtPath;
    }

    /**
     * @param tgtPath the tgtPath to set
     */
    public void setTgtPath(String tgtPath) {
        this.tgtPath = tgtPath;
    }

    /**
     * @return the aksharData
     */
    public AksharData getAksharData() {
        return aksharData;
    }

    /**
     * @param aksharData the aksharData to set
     */
    public void setAksharData(AksharData aksharData) {
        this.aksharData = aksharData;
    }

    public void normalizeDocument() throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;
        InputStream is = null;
        PrintStream ps = null;
        
        if(redirection)
        {
            is = System.in;
            ps = System.out;

            if(charset != null && charset.equals("") == false)
                inReader = new BufferedReader(new InputStreamReader(is, charset));
            else
                inReader = new BufferedReader(new InputStreamReader(is));
        }
        else
        {
            if(charset != null && charset.equals("") == false)
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), charset));
            else
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath)));

            ps = new PrintStream(tgtPath, charset);
        }

        String srcLine = "";

        while((srcLine = inReader.readLine()) != null)
        {
            String tgtLine = normalizeString(srcLine);
            ps.println(tgtLine);
        }
    }

    public void processDocument(String langEnc, String charset, String srcPath, String tgtPath) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;

        if(charset != null && charset.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath), charset));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath)));

        String srcLine = "";

        PrintStream ps = new PrintStream(tgtPath, charset);

        while((srcLine = inReader.readLine()) != null)
        {
            String tgtLine = processString(srcLine);            
            ps.println(tgtLine);
        }
    }

    public void processDocument() throws FileNotFoundException, IOException
    {
        processDocument(langEnc, charset, srcPath, tgtPath);
    }

    public String processString(String srcString)
    {
        String tgtString = "";
        String srcWords[] = srcString.split("[\\s+]");

        for (int i = 0; i < srcWords.length; i++)
        {
            String tgtWord = processWord(srcWords[i]);

            if(i < srcWords.length - 1)
                tgtString += tgtWord + " ";
            else
                tgtString += tgtWord;
        }

        return tgtString;
    }

    public String processWord(String srcWrd)
    {
        String tgtWrd = "";

        if(isPossiblyValidWord(srcWrd) == false)
            tgtWrd = normalizeWord(srcWrd);
        else
            tgtWrd = srcWrd;

        return tgtWrd;
    }

    public String normalizeString(String srcString)
    {
        String tgtString = "";
        String srcWords[] = srcString.split("[\\s+]");

        for (int i = 0; i < srcWords.length; i++)
        {
            String tgtWord = normalizeWord(srcWords[i]);

            if(i < srcWords.length - 1)
                tgtString += tgtWord + " ";
            else
                tgtString += tgtWord;
        }

        return tgtString;
    }

    public String normalizeWord(String srcWrd)
    {
        String lcode = SanchayLanguages.getLanguageCodeFromLECode(langEnc);
        boolean iswx = lcode.equalsIgnoreCase("wx");

        String tgtWrd = srcWrd;

        Iterator enm = spellingNormalizationRules.getPropertyKeys();

        while(enm.hasNext())
        {
            String find = (String) enm.next();
            String replace = spellingNormalizationRules.getPropertyValue(find);

            tgtWrd = tgtWrd.replaceAll(find, replace);

//            Pattern p = Pattern.compile(find, Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES | Pattern.MULTILINE);
//            Matcher m = p.matcher(srcWrd);
//
//            while(m.find())
//            {
//
//            }
        }

        String typeStr = aksharData.convertType(tgtWrd);

        int pos = getNuktaPos(typeStr, iswx);

        while(pos != -1)
        {
            if(tgtWrd.charAt(pos + 1) == '\u0921' || tgtWrd.charAt(pos + 1) == '\u0922')
            {
                tgtWrd = tgtWrd.substring(0, pos + 2) + "\u093C" + tgtWrd.substring(pos + 2, tgtWrd.length());
            }
            else if(tgtWrd.charAt(pos + 1) == 'd' || tgtWrd.charAt(pos + 1) == 'D')
            {
                tgtWrd = tgtWrd.substring(0, pos + 2) + "Z" + tgtWrd.substring(pos + 2, tgtWrd.length());
            }

            pos = getNuktaPos(typeStr, iswx, pos + 3);
        }

        if(lcode.equalsIgnoreCase("wx"))
        {
            for (int i = 1; i < tgtWrd.length() - 1; i++)
            {
                typeStr = aksharData.convertType(tgtWrd);
                
                if((tgtWrd.charAt(i) == 'm' || tgtWrd.charAt(i) == 'n' || tgtWrd.charAt(i) == 'F' || tgtWrd.charAt(i) == 'N')
                        && typeStr.charAt(i + 1) == 'c')
                    tgtWrd = tgtWrd.substring(0, i) + "M" + tgtWrd.substring(i + 1, tgtWrd.length());
            }
        }

        return tgtWrd;
    }

    private int getNuktaPos(String typeStr, boolean iswx)
    {
        int pos = typeStr.indexOf("vcv");

        if(iswx == false)
        {
            if(pos == -1)
            {
                pos = typeStr.indexOf("vcm");

                if(pos == -1)
                {
                    pos = typeStr.indexOf("mcv");

                    if(pos == -1)
                    {
                        pos = typeStr.indexOf("mcm");

                        if(pos == -1)
                        {
                            pos = typeStr.indexOf("ccv");

                            if(pos == -1)
                            {
                                pos = typeStr.indexOf("ccm");
                            }
                        }
                    }
                }
            }
        }

        return pos;
    }

    private int getNuktaPos(String typeStr, boolean iswx, int start)
    {
        int pos = typeStr.indexOf("vcv", start);

        if(iswx == false)
        {
            if(pos == -1)
            {
                pos = typeStr.indexOf("vcm", start);

                if(pos == -1)
                {
                    pos = typeStr.indexOf("mcv", start);

                    if(pos == -1)
                    {
                        pos = typeStr.indexOf("mcm", start);

                        if(pos == -1)
                        {
                            pos = typeStr.indexOf("ccv", start);

                            if(pos == -1)
                            {
                                pos = typeStr.indexOf("ccm", start);
                            }
                        }
                    }
                }
            }
        }

        return pos;
    }

    public boolean isPossiblyValidWordNaiive(String wrd)
    {
        String typWrd = aksharData.convertType(wrd);

        int tcount = typWrd.length();

        String typ = "";
        String prevTyp = "";

        // For words only with maatraas or others
        Pattern p1 = Pattern.compile("^[\\s]*[mohk]+[\\s]*$");
        Pattern p2 = Pattern.compile("^[\\s]*[mohk]");
        Pattern p3 = Pattern.compile("[mohk][mohk]");
        Pattern p3a = Pattern.compile("[m][o]");
        Pattern p4 = Pattern.compile("[v][mhk]");
        Pattern p5 = Pattern.compile("^[\\s]*[c][h][c][\\s]*$");
        Pattern p6 = Pattern.compile("^[\\s]*[c][ohk][\\s]*$");

//        System.out.println(typWrd);

        Matcher m1 = p1.matcher(typWrd);
        Matcher m2 = p2.matcher(typWrd);
        Matcher m3 = p3.matcher(typWrd);
        Matcher m3a = p3a.matcher(typWrd);
        Matcher m4 = p4.matcher(typWrd);
        Matcher m5 = p5.matcher(typWrd);
        Matcher m6 = p6.matcher(typWrd);

        if(m1.find() || m2.find() || ( m3.find() && m3a.find() == false) || m4.find() || m5.find() || m6.find())
        {
//            System.out.println("Invalid: " + typWrd + " : " + wrd);
            return false;
        }

        return true;
    }

    public boolean isPossiblyValidWord(String wrd)
    {
        if(wrd.trim().equals(""))
            return false;

        String typeStr = aksharData.convertType(wrd);

        String typeAkshars[] = getAksharsFromTypeString(typeStr);

        for (int i = 0; i < typeAkshars.length; i++)
        {
            if(isValidAkshar(typeAkshars[i]) == false)
                return false;
        }

        return true;
    }

    public boolean isPossiblyValidWordStart(String wrd)
    {
        if(wrd.trim().equals(""))
            return false;

        String typeStr = aksharData.convertType(wrd);

        String typeAkshars[] = getAksharsFromTypeString(typeStr);

        for (int i = 0; i < typeAkshars.length - 1; i++)
        {
            if(isValidAkshar(typeAkshars[i]) == false)
                return false;
        }

        return true;
    }
    public boolean isPossiblyValidCombination(String[] tiles)
    {
        if(tiles.length > 10)
            return false;

        int tileType;

        double aksharCount = 0;
        double nonAksharCount = 0;

        for (int i = 0; i < tiles.length; i++) {
            String string = tiles[i];
            tileType = getTileType(string, false);

            if(tileType == AKSHAR )
                aksharCount++;
            else
                nonAksharCount++;
        }

        if(aksharCount == 0.0 || aksharCount/nonAksharCount < 0.4 || aksharCount > 6 || nonAksharCount > 4)
            return false;

        return true;

//        String combination = "";
//
//        for (int i = 0; i < tiles.length; i++) {
//            String string = tiles[i];
//            combination += string;
//        }
//
//        String typeStr = aksharData.convertType(combination);
//
//        int count = typeStr.length();
//        int charType = 's';
//
//        for (int i = 0; i < count; i++)
//        {
//            charType = typeStr.charAt(i);
//
//            if(charType == 'c')
//                return true;
//        }

//        return false;
    }
    
    public boolean isValidAkshar(String typeStr)
    {
        if(typeStr.trim().equals(""))
            return false;

        int charType = 's';
        int prevCharType = 's';
        int nextCharType = 'e';

        int count = typeStr.length();

        for (int i = 0; i < count; i++)
        {
            charType = typeStr.charAt(i);

            if(i == count - 1)
                nextCharType = 'e';
            else
                nextCharType = typeStr.charAt(i + 1);
            
            if(charType == 'c') // consonant
            {
                if(!(prevCharType == 's' || prevCharType == 'h')
                        && (nextCharType == 'e' || nextCharType == 'k' || nextCharType == 'm' || nextCharType == 'h'))
                    return false;
            }
            else if(charType == 'v') // vowel
            {
                if((prevCharType != 's')
                        && (nextCharType == 'e' || nextCharType == 'o'))
                    return false;
            }
            else if(charType == 'k') // nukta
            {
                if((prevCharType != 'c')
                        && (nextCharType == 'e' || nextCharType == 'h' || nextCharType == 'm'))
                    return false;
            }
            else if(charType == 'h') // halant
            {
                if(!(prevCharType == 'c' || prevCharType == 'k')
                        && (nextCharType == 'e' || nextCharType == 'c'))
                    return false;
            }
            else if(charType == 'm') // maatraa
            {
                if(!(prevCharType == 'c' || prevCharType == 'k')
                        && (nextCharType == 'e' || nextCharType == 'o'))
                    return false;
            }
            else if(charType == 'o') // vowel modifier
            {
                if(!(prevCharType == 'v' || prevCharType == 'm')
                        && nextCharType == 'e')
                    return false;
            }
            else
                return false;

            prevCharType = charType;
        }

        return true;
    }

    public boolean isValidTile(String typeStr, boolean consonantClusters, boolean ottuBased, int curPos)
    {
        if(typeStr.trim().equals(""))
            return false;

        int count = typeStr.length();

        if(count == 1)
        {
            if(typeStr.charAt(0) == 'v' || typeStr.charAt(0) == 'm'|| typeStr.charAt(0) == 'o')
                return true;
        }

        if(count == 2)
        {
            if(ottuBased)
            {
                if(curPos != FINAL)
                {
                    if((typeStr.charAt(0) == 'h' && typeStr.charAt(1) == 'c')
                        || (typeStr.charAt(0) == 'c' && typeStr.charAt(1) == 'k'))
                        return true;
                }
                else
                {
                    if((typeStr.charAt(0) == 'c' && typeStr.charAt(1) == 'h')
                        || (typeStr.charAt(0) == 'h' && typeStr.charAt(1) == 'c')
                        || (typeStr.charAt(0) == 'c' && typeStr.charAt(1) == 'k'))
                        return true;
                }
            }
            else
            {
                if((typeStr.charAt(0) == 'c' && typeStr.charAt(1) == 'h')
                    || (typeStr.charAt(0) == 'c' && typeStr.charAt(1) == 'k'))
                    return true;
            }
        }

        if(consonantClusters)
        {
            if(count == 3)
            {
                if(typeStr.charAt(0) == 'c' && typeStr.charAt(1) == 'h' && typeStr.charAt(2) == 'c')
                    return true;
            }
        }

        return false;
    }

    public int getTileType(String tile, boolean ottuBased)
    {
        if(tile.trim().equals(""))
            return -1;

        tile = aksharData.convertType(tile);

        int count = tile.length();

        if(count == 1)
        {
            if(tile.charAt(0) == 'm'|| tile.charAt(0) == 'o')
                return MAATRAA;
            if(tile.charAt(0) == 'v' || tile.charAt(0) == 'c')
                return AKSHAR;
        }

        if(count == 2)
        {
            if(ottuBased)
            {
                if(tile.charAt(0) == 'c' && tile.charAt(1) == 'h')
                    return AKSHAR;

                if(tile.charAt(0) == 'h' && tile.charAt(1) == 'c')
                    return OTTU;
            }
            else
            {
                if(tile.charAt(0) == 'c' && tile.charAt(1) == 'h')
                    return ARDHAAKSHAR;
            }
        }

        if(count == 3)
        {
            if(ottuBased)
            {
                if(tile.charAt(0) == 'h' && tile.charAt(1) == 'c' && tile.charAt(1) == 'h')
                    return OTTU;
            }
        }

        return -1;
    }

    public String getFirstAkshar(String typeStr)
    {
        String firstAkshar = null;

        int count = typeStr.length();

        for (int i = count; i > 0; i--)
        {
            firstAkshar = typeStr.substring(0, i);

            if(isValidAkshar(firstAkshar))
                return firstAkshar;
        }

        return firstAkshar;
    }

    public String getFirstTile(String typeStr, boolean consonantClusters, boolean ottuBased, int curPos)
    {
        String firstTile = null;

        int count = typeStr.length();

        for (int i = count; i > 0; i--)
        {
            firstTile = typeStr.substring(0, i);

            if(isValidTile(firstTile, consonantClusters, ottuBased, curPos))
                return firstTile;
        }

        return firstTile;
    }

    public String[] getAkshars(String word)
    {
        String typeStr = aksharData.convertType(word);

        String typeAkshars[] = getAksharsFromTypeString(typeStr);

        String akshars[] = new String[typeAkshars.length];

        int beg = 0;

        for (int i = 0; i < akshars.length; i++)
        {
            int end = beg + typeAkshars[i].length();
            akshars[i] = word.substring(beg, end);
            
            beg += typeAkshars[i].length();
        }

        return akshars;
    }

    public String[] getTiles(String word, boolean consonantClusters, boolean ottuBased)
    {
        String typeStr = aksharData.convertType(word);

        String typeTiles[] = getTilesFromTypeString(typeStr, consonantClusters, ottuBased);

        String tiles[] = new String[typeTiles.length];

        int beg = 0;

        for (int i = 0; i < tiles.length; i++)
        {
            int end = beg + typeTiles[i].length();
            tiles[i] = word.substring(beg, end);

            beg += typeTiles[i].length();
        }

        for (int i = 0; i < tiles.length; i++)
        {
            String tile = tiles[i];
            String tileTypeStr = aksharData.convertType(tile);

            // Ignore nukta
            if(tileTypeStr.length() == 2
                    && (tileTypeStr.charAt(0) == 'c' && tileTypeStr.charAt(0) == 'k'))
                tiles[i] = tiles[i].charAt(0) + "";
        }

        return tiles;
    }

    public String[] getAksharsFromTypeString(String typeStr)
    {
        Vector aksharVector = new Vector(0, 5);

        while(typeStr.length() > 0)
        {
            String akshar = getFirstAkshar(typeStr);

            aksharVector.add(akshar);

            typeStr = typeStr.substring(akshar.length(), typeStr.length());
        }

        return (String[]) aksharVector.toArray(new String[aksharVector.size()]);
    }

    public String[] getTilesFromTypeString(String typeStr, boolean consonantClusters, boolean ottuBased)
    {
        Vector tileVector = new Vector(0, 5);

        String tile = "";
        int i = 0;
        int len = typeStr.length();

        try
        {
            if(len == 3 && ottuBased && typeStr.endsWith("ch"))
            {
                tileVector.add("" + typeStr.charAt(0));
                tileVector.add("ch");

                return (String[]) tileVector.toArray(new String[tileVector.size()]);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        while(typeStr.length() > 0)
        {
            tile = "hch";

            if(typeStr.length() == 3 && ottuBased && typeStr.equals(tile))
            {
                tileVector.add(tile);
            }
            else
            {
                if(i < 2 && len > 2)
                    tile = getFirstTile(typeStr, consonantClusters, ottuBased, INITIAL);
                else if(typeStr.length() <= 2 && i >= len - 2)
                    tile = getFirstTile(typeStr, consonantClusters, ottuBased, FINAL);
                else
                    tile = getFirstTile(typeStr, consonantClusters, ottuBased, MEDIAL);

                tileVector.add(tile);
            }

            i += tile.length();

            typeStr = typeStr.substring(tile.length(), typeStr.length());
        }

        return (String[]) tileVector.toArray(new String[tileVector.size()]);
    }

    public static void main(String args[])
    {
        String rORfORs = "-f";

        if(args.length > 0)
            rORfORs = args[0];

        int mode = FILE;

        if(rORfORs.equals("-r"))
            mode = REDIRECTION;
        else if(rORfORs.equals("-s"))
            mode = STRING;

        String langEnc = "hin::utf8";
        
        if(args.length > 1)
            langEnc = args[1];

        String srcPath = "/home/anil/sanchay-debug-data/normalization-sample-wx.txt";

        if(args.length > 2)
            srcPath = args[2];

        String tgtPath = "/home/anil/sanchay-debug-data/normalization-sample-wx-out.txt";

//        String hdir = GlobalProperties.getHomeDirectory();
//
//        System.out.println(hdir);

        if(args.length > 3)
            tgtPath = args[3];

        TextNormalizer textNormalizer = new TextNormalizer(langEnc, "UTF-8",
                srcPath, tgtPath, mode == REDIRECTION ? true: false);
//        Class myClass = TextNormalizer.class;
//        URL url = myClass.getResource("TextNormalizer.class");
//
//        String path = url.getPath();
//
//        int ind = path.lastIndexOf("Sanchay/");
//
//        path = path.substring(0, ind - 1);
//
//        System.out.println(path);
//
//        path = path.replaceAll("file:", "");
//
//        System.out.println(path);
        
        try {

            if(mode == REDIRECTION || mode == FILE)
                textNormalizer.normalizeDocument();
            else if (mode == STRING)
                System.out.println(textNormalizer.normalizeString(srcPath));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextNormalizer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextNormalizer.class.getName()).log(Level.SEVERE, null, ex);
        }

//        String word = "मारवsdfाड़ी";
//        String word = "पढाई";
//
//        System.out.println(GlobalProperties.getIntlString("Is_the_word_") + word + GlobalProperties.getIntlString("_valid_start:_") + textNormalizer.isPossiblyValidWordStart(word));
//
//        String akshars[] = textNormalizer.getAkshars(word);
//
//        for (int i = 0; i < akshars.length; i++)
//        {
//            System.out.println(akshars[i]);
//        }
//
//        System.out.println("Is the word " + word + " valid: " + textNormalizer.isPossiblyValidWord(word));

//        word = "मन्दिर";
//        word = "cAhiye";
//        word = "badA";
//        word = "बडा";
//
//        String normWord = textNormalizer.normalizeWord(word);
//
//        System.out.println("The word " + word + " normalized as: " + normWord);

//        akshars = textNormalizer.getAkshars("सामाऩ्यत:");
//
//        for (int i = 0; i < akshars.length; i++)
//        {
//            System.out.println(akshars[i]);
//        }
//
//        akshars = textNormalizer.getAkshars("प्राकृति");
//
//        for (int i = 0; i < akshars.length; i++)
//        {
//            System.out.println(akshars[i]);
//        }
    }
}
