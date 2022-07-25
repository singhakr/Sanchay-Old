/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.text.spell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import sanchay.GlobalProperties;
import sanchay.properties.PropertyTokens;
import sanchay.text.DictionaryFSTNode;

/**
 *
 * @author anil
 */
public class CPMSExample {

    public static void getHindiWNWords(String inpath, String outpath) throws FileNotFoundException, IOException
    {
//        JHWNL.initialize("../properties/HindiWN.properties"); 
        
        PropertyTokens inPT = new PropertyTokens(inpath, "UTF-8");

        PrintStream ps = new PrintStream(outpath, "UTF-8");
        
        int count = inPT.countTokens();
        
        for (int i = 0; i < count; i++) {
            String inLine = inPT.getToken(i);
            
            String parts[] = inLine.split(" ");
            
            ps.println(parts[0]);
        }
        
        ps.close();
    }
    
    
    public static void main(String[] args) {
        try {
            
//            CPMSExample.getHindiWNWords("/extra/hindi-wordnet/HindiWN_1_2/database/index_txt", "/extra/hindi-wordnet/HindiWN_1_2/wn-wordlist.txt");
            
//            PhoneticModelOfScripts cpf = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
//                    "UTF-8", "hin::utf8");
////hindi-wordnet/HindiWN_1_2/wn-wordlist-uniq.txt
//            cpf.matchWords("/extra/cpms-example/mistaken_words.txt", "UTF-8",
//                    "/extra/cpms-example/cpms-matched-words.txt", "UTF-8");

            DictionaryFSTExt dictionaryFST = new DictionaryFSTExt("/extra/hindi-wordnet/HindiWN_1_2/wn-wordlist-uniq.txt",
                    "UTF-8", "tmp/tmp.forward", "tmp/tmp.backward", false, 0);

            dictionaryFST.loadPhoneticModelOfScripts();

            PropertyTokens inPT = new PropertyTokens("/extra/cpms-example/mistaken_words.txt", "UTF-8");

            PrintStream ps = new PrintStream("/extra/cpms-example/cpms-matched-words-akshar.txt", "UTF-8");

            int count = inPT.countTokens();

            for (int i = 0; i < count; i++) {
                String wrd = inPT.getToken(i);
                
                LinkedHashMap<DictionaryFSTNode, Double> matchedWords = dictionaryFST.getNearestWords(wrd, 5, false);

                Iterator itr = matchedWords.keySet().iterator();

                ps.println(wrd);

                while(itr.hasNext())
                {
                    Object fstNode = itr.next();
                    Double d = matchedWords.get(fstNode);

                    String cstr = ((DictionaryFSTNode) fstNode).getWordString();

                    ps.println("\t" + cstr + "\t" + d);
                }
            }
            
////	    Character a1 = new Character('à¤¬');
////	    Character a2 = new Character('à¤µ');
////	    System.out.println (cpf.getDistance( a1 , a2 ));
//
//            String wrd1 = "राजस्थान";
//            String wrd2 = "राजिस्थान";
//
//            double ssim = cpf.getSurfaceSimilarity(wrd1, wrd2);
//            
//            System.out.println("SSim: " + wrd1 + " : " + wrd2 + " = " + ssim);
//
//            wrd1 = "सजाना";
//            wrd2 = "नवाना";
//
//            ssim = cpf.getSurfaceSimilarity(wrd1, wrd2);
//
//            System.out.println("SSim: " + wrd1 + " : " + wrd2 + " = " + ssim);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
