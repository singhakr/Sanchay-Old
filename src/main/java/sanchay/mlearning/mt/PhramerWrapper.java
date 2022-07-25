/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.phramer.PhramerException;
import org.phramer.v1.decoder.Phramer;
import org.phramer.v1.decoder.PhramerConfig;
import org.phramer.v1.decoder.loader.ConcurrentObjectsLoader;
import org.phramer.v1.decoder.loader.PhramerHelperCustom;
import org.phramer.v1.decoder.loader.PhramerHelperIf;
import org.phramer.v1.decoder.loader.PhramerHelperSimpleImpl;
import org.phramer.v1.decoder.loader.custom.AllLoader;
import org.phramer.v1.decoder.token.TokenBuilderWordOnly;
import sanchay.GlobalProperties;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class PhramerWrapper {

    protected PhramerConfig phramerConfig;
    protected Phramer phramer;
    protected PhramerHelperIf helper;

    protected int nbest = 10;

    public PhramerWrapper(String configFile)
    {
        AllLoader loader = new ConcurrentObjectsLoader();
        try
        {
            String args = "-config " + configFile;
            helper = new PhramerHelperCustom(loader, new TokenBuilderWordOnly(PhramerHelperSimpleImpl.getFilter((String) null)), loader, loader, loader);
            phramerConfig = new PhramerConfig(args, helper, null);

        } catch (IOException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }  catch (PhramerException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        phramer = new Phramer(phramerConfig);
    }

    public String[] translateNBest(String sentence)
    {
        String sentenceStrings[] = new String[nbest];

        try
        {
            sentenceStrings = phramer.translateNBest(sentence, false, false, nbest);
        } catch (PhramerException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sentenceStrings;
    }

    public String translate(String sentence)
    {
        try
        {
            return phramer.translate(sentence, 0, false, false);
        } catch (PhramerException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Unknown";
    }

    public String transliterate(String wordString)
    {
        wordString = UtilityFunctions.getSpacedOutString(wordString);

        try
        {
            wordString = phramer.translate(wordString, 0, false, false);
        } catch (PhramerException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        wordString = wordString.replaceAll(" ", "");

        return wordString;
    }

    public String[] transliterateNBest(String wordString)
    {
        wordString = UtilityFunctions.getSpacedOutString(wordString);

        String wordStrings[] = new String[nbest];

        try
        {
            wordStrings = phramer.translateNBest(wordString, false, false, nbest);

            for (int i = 0; i < wordStrings.length; i++)
            {
                String string = wordStrings[i];
                string = string.replaceAll(" ", "");
                wordStrings[i] = string;
            }
        } catch (PhramerException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(PhramerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }


        return wordStrings;
    }

    public static void main(String[] args)
    {
        PhramerWrapper phramerWrapper = new PhramerWrapper("data/transliteration/en-hi-utf8/phramer.fast.ini");

        String srcString = "america";

        String tgtString = phramerWrapper.transliterate(srcString);

        System.out.println(tgtString);
    }
}
