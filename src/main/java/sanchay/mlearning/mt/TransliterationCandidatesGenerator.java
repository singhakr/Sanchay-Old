/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.gui.common.SanchayLanguages;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.MultiKeyValueProperties;

/**
 *
 * @author anil
 */
public class TransliterationCandidatesGenerator {

    protected MultiKeyValueProperties mappingsMKVP;

    public TransliterationCandidatesGenerator()
    {
        mappingsMKVP = new MultiKeyValueProperties();
    }

    public TransliterationCandidatesGenerator(String mapCharset)
    {
        loadData(mapCharset);
    }

    public void loadData(String mapCharset)
    {
        String mappingPath = GlobalProperties.getHomeDirectory() + "/data/transliteration/transliteration-mappings.txt";

        try
        {
            mappingsMKVP = new MultiKeyValueProperties(mappingPath, mapCharset);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(TransliterationCandidatesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(TransliterationCandidatesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TranslationCandidates getTransliterationCandidates(String srcLangEnc, String tgtLangEnc, String srcWrd, boolean useSpeechDict)
    {
        TranslationCandidates translationCandidates = new TransliterationCandidates();

        String srcLang = SanchayLanguages.getLanguageName(srcLangEnc);
        String tgtLang = SanchayLanguages.getLanguageName(tgtLangEnc);

        KeyValueProperties mapKVP = mappingsMKVP.getPropertiesValue(srcLang + "-" + tgtLang);

        

        return translationCandidates;
    }    
}
