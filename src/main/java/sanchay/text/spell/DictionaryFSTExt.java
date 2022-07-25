/*
 * DictionaryFSTExt.java
 *
 * Created on April 16, 2008, 3:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text.spell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;
import sanchay.table.SanchayTableModel;
import sanchay.text.DictionaryFST;
import sanchay.text.DictionaryFSTNode;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class DictionaryFSTExt extends DictionaryFST implements Serializable {
    
    /** Creates a new instance of DictionaryFSTExt */
    public DictionaryFSTExt()
    throws FileNotFoundException, IOException, ClassNotFoundException {
        super();
    }

    public DictionaryFSTExt(String inFile, String cs, String ffile, String bfile)
    throws FileNotFoundException, IOException {
        super(inFile, cs, ffile, bfile);
    }

    public DictionaryFSTExt(String inFile, String cs, String ffile, String bfile, boolean letterBased)
    throws FileNotFoundException, IOException {
        super(inFile, cs, ffile, bfile, letterBased);
    }

    public DictionaryFSTExt(String inFile, String cs, String ffile, String bfile, boolean letterBased, int minFreq)
    throws FileNotFoundException, IOException {
        super(inFile, cs, ffile, bfile, letterBased, minFreq);
    }
    
    public DictionaryFSTExt(String ffile, String bfile)
    throws FileNotFoundException, IOException, ClassNotFoundException {
        super(ffile, bfile);
    }
        
    public SanchayTableModel getLetterTable()
    {
        SanchayTableModel table = new SanchayTableModel(new String[]{GlobalProperties.getIntlString("Letter"), GlobalProperties.getIntlString("Frequency")}, 0);

        Hashtable list = getLetterList(forwardFST, null);
        
        Enumeration enm = list.keys();
        
        while(enm.hasMoreElements())
        {
            String letter = (String) enm.nextElement();
            Long freqLong = (Long) list.get(letter);
            
            Vector dat = new Vector(2);
            dat.add(letter);
            dat.add(freqLong);
            
            table.addRow(dat);
        }
        
        return table;
    }
    
    public SanchayTableModel getAksharTable()
    {
        SanchayTableModel table = new SanchayTableModel(new String[]{GlobalProperties.getIntlString("Akshar"), GlobalProperties.getIntlString("Frequency")}, 0);

        Hashtable list = getAksharList(forwardFST, null);
        
        if(list == null)
            return table;
        
        Enumeration enm = list.keys();
        
        while(enm.hasMoreElements())
        {
            String akshar = (String) enm.nextElement();
            Long freqLong = (Long) list.get(akshar);
            
            Vector dat = new Vector(2);
            dat.add(akshar);
            dat.add(freqLong);
            
            table.addRow(dat);
        }
        
        return table;
    }

    public SanchayTableModel getLetterAffixTable(boolean reverse, int depth, int fr)
    {
        SanchayTableModel table = new SanchayTableModel(new String[]{GlobalProperties.getIntlString("Letter_Affix"), GlobalProperties.getIntlString("Frequency")}, 0);

        Hashtable list = null;
        
        if(reverse)
            list = getLetterAffixList(backwardFST, null, depth, fr, null);
        else
            list = getLetterAffixList(forwardFST, null, depth, fr, null);

        Enumeration enm = list.keys();
        
        while(enm.hasMoreElements())
        {
            String rt = (String) enm.nextElement();
            Long freqLong = (Long) list.get(rt);
            
            Vector dat = new Vector(2);
            dat.add(rt);
            dat.add(freqLong);
            
            table.addRow(dat);
        }
        
        return table;                        
    }

    public SanchayTableModel getAksharAffixTable(boolean reverse, int depth, int fr)
    {
        SanchayTableModel table = new SanchayTableModel(new String[]{GlobalProperties.getIntlString("Akshar_Affix"), GlobalProperties.getIntlString("Frequency")}, 0);

        Hashtable list = null;
        
        if(reverse)
            list = getAksharAffixList(backwardFST, null, depth, fr, null);
        else
            list = getAksharAffixList(forwardFST, null, depth, fr, null);
        
        if(list == null)
            return table;

        Enumeration enm = list.keys();
        
        while(enm.hasMoreElements())
        {
            String rt = (String) enm.nextElement();
            Long freqLong = (Long) list.get(rt);
            
            Vector dat = new Vector(2);
            dat.add(rt);
            dat.add(freqLong);
            
            table.addRow(dat);
        }
        
        return table;        
    }

    public SanchayTableModel getRootTable()
    {
        SanchayTableModel table = new SanchayTableModel(new String[]{GlobalProperties.getIntlString("Root"), GlobalProperties.getIntlString("Frequency")}, 0);

        Hashtable list = getRootList(forwardFST, null, null);
        
        Enumeration enm = list.keys();
        
        while(enm.hasMoreElements())
        {
            String rt = (String) enm.nextElement();
            Long freqLong = (Long) list.get(rt);
            
            Vector dat = new Vector(2);
            dat.add(rt);
            dat.add(freqLong);
            
            table.addRow(dat);
        }
        
        return table;
    }

    public SanchayTableModel getMorphemeTable()
    {
        SanchayTableModel table = new SanchayTableModel(new String[]{GlobalProperties.getIntlString("Morpheme"), GlobalProperties.getIntlString("Frequency")}, 0);
        
        Hashtable list = getMorphemeList(forwardFST, null, null);

        return table;                
    }

    public static Hashtable getLetterList(DictionaryFSTNode node, Hashtable list)
    {      
        if((node.getFlags() & DictionaryFSTNode.LETTER_BASED) > 0)
        {
            if(list == null)
            {
                list = new Hashtable(0, 20);
            }

            long freq = 0;

            String letter = node.getString();

            if(letter == null && node.countChildren() == 0)
                return list;
            
            if(letter != null)               
            {
                Long freqLong = (Long) list.get(letter);

                if(freqLong != null)
                    freq = freqLong.longValue();

                freq++;

                list.put(letter, new Long(freq));
            }

            Enumeration enm = node.getChildren();

            while(enm != null && enm.hasMoreElements())
            {
                getLetterList((DictionaryFSTNode) node.getChild((String) enm.nextElement()), list);
            }
        }
    
        return list;
    }
    
    public static Hashtable getAksharList(DictionaryFSTNode node, Hashtable list)
    {
        if((node.getFlags() & DictionaryFSTNode.LETTER_BASED) > 0)
        {
            return null;
        }

        if(list == null)
        {
            list = new Hashtable(0, 20);
        }
        
        long freq = 0;
        
        String akshar = node.getString();
        
        if(akshar == null)
            return list;
        
        Long freqLong = (Long) list.get(akshar);
        
        if(freqLong != null)
            freq = freqLong.longValue();
        
        freq++;
        
        list.put(akshar, new Long(freq));
        
        Enumeration enm = node.getChildren();
        
        while(enm.hasMoreElements())
        {
            getAksharList((DictionaryFSTNode) node.getChild((String) enm.nextElement()), list);
        }
        
        return list;
    }

    public static Hashtable getLetterAffixList(DictionaryFSTNode node, Hashtable list, int depth, int fr, String af)
    {
        if(list == null)
        {
            list = new Hashtable(0, 20);
        }
        
        long freq = 0;
        
        String letter = node.getString();
        
        if(letter == null && node.countChildren() == 0)
            return list;
        
        if(letter != null)
        {
            if(af == null)
                af = letter;
            else
                af += letter;

            if(depth > 0)
            {
                Long freqLong = (Long) list.get(af);

                if(freqLong != null)
                    freq = freqLong.longValue();

                freq += DictionaryFSTNode.countDescendents(node) + 1;

                if(node.isReverse())
                    list.put(UtilityFunctions.reverseString(af), new Long(freq));
                else
                    list.put(af, new Long(freq));
            }
        }
        
        Enumeration enm = node.getChildren();
        
        while(enm != null && enm.hasMoreElements())
        {
            getLetterAffixList((DictionaryFSTNode) node.getChild((String) enm.nextElement()), list, depth - 1, fr, af);
        }
        
        return list;
    }

    public static Hashtable getAksharAffixList(DictionaryFSTNode node, Hashtable list, int depth, int fr, String af)
    {
        if((node.getFlags() & DictionaryFSTNode.LETTER_BASED) > 0)
        {
            return null;
        }
        
        if(list == null)
        {
            list = new Hashtable(0, 20);
        }
        
        long freq = 0;
        
        String akshar = node.getString();
        
        if(akshar == null)
            return list;
        
        if(af == null)
            af = akshar;
        else
            af += akshar;

        if(depth > 0)
        {
            Long freqLong = (Long) list.get(af);

            if(freqLong != null)
                freq = freqLong.longValue();

            freq += DictionaryFSTNode.countDescendents(node) + 1;

            if(node.isReverse())
                list.put(UtilityFunctions.reverseString(af), new Long(freq));
            else
                list.put(af, new Long(freq));
        }
        
        Enumeration enm = node.getChildren();
        
        while(enm.hasMoreElements())
        {
            getAksharAffixList((DictionaryFSTNode) node.getChild((String) enm.nextElement()), list, depth - 1, fr, af);
        }
        
        return list;
    }

    public static Hashtable getRootList(DictionaryFSTNode node, Hashtable list, String rt)
    {
        if(list == null)
        {
            list = new Hashtable(0, 20);
        }
        
        long freq = 0;
        
        String akshar = node.getString();
        
        if(akshar == null)
            return list;
        
        if(rt == null)
            rt = akshar;
        else
            rt += akshar;
        
        if((node.getFlags() & DictionaryFSTNode.EOWORD) > 0)
        {
            Long freqLong = (Long) list.get(rt);

            if(freqLong != null)
                freq = freqLong.longValue();

            freq += DictionaryFSTNode.countDescendents(node) + 1;

            list.put(rt, new Long(freq));
        }
        
        Enumeration enm = node.getChildren();
        
        while(enm.hasMoreElements())
        {
            getRootList((DictionaryFSTNode) node.getChild((String) enm.nextElement()), list, rt);
        }
        
        return list;
    }

    public static Hashtable getMorphemeList(DictionaryFSTNode node, Hashtable list, String mph)
    {
        return null;
    }

    public void getFeatureComparison(KeyValueProperties wordList)
    {
        Iterator enm = wordList.getPropertyKeys();

        try
        {
            PrintStream ps = new PrintStream(GlobalProperties.getHomeDirectory() + "/" + "tmp/wordlist_features",GlobalProperties.getIntlString("UTF-8"));
            for(int i = 0;i<wordList.countProperties();i++)
            {
                String word1 = enm.next().toString();
                String word2 = wordList.getPropertyValue(word1);
                if(word1.length()<2||word2.length()<2)
                    continue;
                ps.print(word1+"\t"+word2+"\t");
                InstantiatePhonemeGraph ipg = new InstantiatePhonemeGraph(GlobalProperties.getIntlString("hin::utf8"),GlobalProperties.getIntlString("UTF-8"),false);
                Vector phnSeq1 = ipg.createPhonemeSequence(word1);
                Vector phnSeq2 = ipg.createPhonemeSequence(word2);
//                System.out.println(phnSeq1.size()+" "+phnSeq1.toString());
//                System.out.println(phnSeq2.size()+" "+phnSeq2.toString());
//                String iFile = GlobalProperties.getHomeDirectory() + "/data/transliteration/forwardFile";
//                String bFile = GlobalProperties.getHomeDirectory() + "/data/transliteration/backwardFile";
                DictionaryFSTNode forFST = new DictionaryFSTNode(false, true);
                DictionaryFSTNode backFST = new DictionaryFSTNode(true, true);

                 compileFC(phnSeq1,forFST,backFST,"<l=a>");
                 leaves = 0;
                 featureList.clear();
                 traverseTree(backFST);
                 int initSize = featureList.size();
                 compareFC(phnSeq2,forFST,backFST,"<l=h>");
                 leaves = 0;
                 featureList.clear();
                 traverseTree(backFST);
                 int finSize = featureList.size();
                 ps.println(initSize+"\t"+finSize);


            }
        }

        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
	try {
	    DictionaryFST dictionaryFST = new DictionaryFST(GlobalProperties.getHomeDirectory() + "/data/transliteration/Transfer/hq/test.Hin1", GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getHomeDirectory() + "/" + "tmp/spell-checker-dict.forward", GlobalProperties.getHomeDirectory() + "/" + "tmp/spell-checker-dict.reverse");
//		DictionaryFST dictionaryFST = new DictionaryFST("/home/sanchay/tmp/FST/hindi.for", "/home/sanchay/tmp/FST/hindi.rev");
	    System.out.println(GlobalProperties.getIntlString("Dictionary_loaded."));
	    
//	    dictionaryFST.write();
	    //dictionaryFST.read();
	    System.out.println(GlobalProperties.getIntlString("Dictionary_Read."));
//	    dictionaryFST.showTreeView();
	    dictionaryFST.showTreeJPanel(false);
	    dictionaryFST.print(System.out);
	    
//        KeyValueProperties keyVal = new KeyValueProperties("/home/prad/Desktop/awadhi-hindi-word-list.txt","UTF-8");
//        dict.getFeatureComparison(keyVal);

//	    PrintStream ps = new PrintStream("/home/anil/tmp/spell-checker-dict.forward.txt", "UTF-8");
//	    dictionaryFST.printForwardFST(ps);

//	    ps = new PrintStream("/home/anil/tmp/spell-checker-dict.reverse.txt", "UTF-8");
//	    dictionaryFST.printBackwardFST(ps);
	}
//	catch (ClassNotFoundException ex)
//	{
//	    ex.printStackTrace();
//	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	}
    }
}
