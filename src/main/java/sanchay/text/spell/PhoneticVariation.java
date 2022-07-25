/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.text.spell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import java.util.Vector;
import sanchay.GlobalProperties;
import sanchay.mlearning.common.impl.DefaultMLFreqProb;
import sanchay.mlearning.common.impl.TranslationListFreqProb;
import sanchay.properties.KeyValueProperties;
import sanchay.speech.common.*;
import sanchay.speech.decoder.isolated.*;

/**
 *
 * @author anil
 */
public class PhoneticVariation {

    java.util.ResourceBundle bundle = GlobalProperties.getResourceBundle(); // NOI18N

    protected String langEnc = "hin::utf8";

    protected KeyValueProperties wordListKVP;

    protected LinkedHashMap<String, Integer> fs_cl_cr;

    protected LinkedHashMap<String, TranslationListFreqProb> ft_Given_fs_cl_cr;
    
    protected PhoneticModelOfScripts phoneticModelOfScripts;

    public PhoneticVariation()
    {
        wordListKVP = new KeyValueProperties();

        fs_cl_cr = new LinkedHashMap<String, Integer>();

        ft_Given_fs_cl_cr = new LinkedHashMap<String, TranslationListFreqProb>();

        init();
    }

    public void init()
    {
        try {
            wordListKVP.read("/home/anil/corpora/awadhi-hindi-word-list.txt", "UTF-8");
            phoneticModelOfScripts = new PhoneticModelOfScripts(GlobalProperties.resolveRelativePath("props/spell-checker/spell-checker-propman.txt"),
                    GlobalProperties.getIntlString("UTF-8"), GlobalProperties.getIntlString("hin::utf8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void calculateCounts()
    {
        Iterator itr = wordListKVP.getPropertyKeys();

        while(itr.hasNext())
        {
            String srcWord = (String) itr.next();
            String tgtWord = (String) wordListKVP.getPropertyValue(srcWord);

            IsolatedRecog isolatedRecog = new IsolatedRecog();

            isolatedRecog.addModel(phoneticModelOfScripts.getTrellisString(tgtWord));

            isolatedRecog.setData(phoneticModelOfScripts.getTrellisString(srcWord));

            isolatedRecog.alignAll();

            IsoTrellisPath bestPaths[] = isolatedRecog.bestAlignments(1);

            IsoTrellisPath bestPath = bestPaths[0];

            int count = bestPath.countDataNodes();

            for (int i = 0; i < count; i++)
            {
                TrellisNode data = bestPath.getDataNode(i);
                StringNode model = bestPath.getModelNode(i);

                PhoneticCharacter dataPC = (PhoneticCharacter) data.getStringNode().getFeature().getFeatures();
                PhoneticCharacter modelPC = (PhoneticCharacter) model.getFeature().getFeatures();
                
                char dataC = dataPC.getCharacter().charValue();
                char modelC = modelPC.getCharacter().charValue();

                if(dataC == modelC)
                    continue;

                Vector<String> dataFVStrings = phoneticModelOfScripts.getFeatureValueStrings(dataC, langEnc);
                Vector<String> modelFVStrings = phoneticModelOfScripts.getFeatureValueStrings(modelC, langEnc);

                int dataContextCount = dataFVStrings.size();
                int modelContextCount = modelFVStrings.size();

                for (int j = 0; j < dataContextCount; j++)
                {
                    String dataContextString = "<" + dataFVStrings.get(j) + ">";

                    for (int k = 0; k < modelContextCount; k++)
                    {
                        String modelContextString = modelFVStrings.get(k);

                        if(PhoneticModelOfScripts.areFeaturesCompatible(dataContextString, modelContextString) == false
                                || PhoneticModelOfScripts.areFeaturesEquivalent(dataContextString, modelContextString))
                            continue;
                        
                        addContexts(dataContextString, modelContextString, dataC, modelC, srcWord, tgtWord);

                        if(i > 0)
                        {
                            TrellisNode dataLeftContext = bestPath.getDataNode(i - 1);

                            PhoneticCharacter dataPCLeftContext = (PhoneticCharacter) dataLeftContext.getStringNode().getFeature().getFeatures();

                            char dataCLeftContext = dataPCLeftContext.getCharacter().charValue();

                            Vector<String> dataFVStringsLeftContext = phoneticModelOfScripts.getFeatureValueStrings(dataCLeftContext, langEnc);

                            int leftContextCount = dataFVStringsLeftContext.size();

                            for (int l = 0; l < leftContextCount; l++)
                            {
                                String fvStringLeftContext = dataFVStringsLeftContext.get(l);

                                String srcContextString = fvStringLeftContext + ":" + dataContextString;

                                addContexts(srcContextString, modelContextString, dataC, modelC, srcWord, tgtWord);
                            }
                        }

                        if(i < count - 1)
                        {
                            TrellisNode dataRightContext = bestPath.getDataNode(i + 1);

                            PhoneticCharacter dataPCRightContext = (PhoneticCharacter) dataRightContext.getStringNode().getFeature().getFeatures();

                            char dataCRightContext = dataPCRightContext.getCharacter().charValue();

                            Vector<String> dataFVStringsRightContext = phoneticModelOfScripts.getFeatureValueStrings(dataCRightContext, langEnc);

                            int rightContextCount = dataFVStringsRightContext.size();

                            for (int l = 0; l < rightContextCount; l++)
                            {
                                String fvStringRightContext = dataFVStringsRightContext.get(l);

                                String srcContextString = dataContextString + ":" + fvStringRightContext;

                                addContexts(srcContextString, modelContextString, dataC, modelC, srcWord, tgtWord);
                            }
                        }

                        if(i > 0 && i < count - 1)
                        {
                            TrellisNode dataLeftContext = bestPath.getDataNode(i - 1);

                            PhoneticCharacter dataPCLeftContext = (PhoneticCharacter) dataLeftContext.getStringNode().getFeature().getFeatures();

                            char dataCLeftContext = dataPCLeftContext.getCharacter().charValue();

                            Vector<String> dataFVStringsLeftContext = phoneticModelOfScripts.getFeatureValueStrings(dataCLeftContext, langEnc);

                            int leftContextCount = dataFVStringsLeftContext.size();

                            for (int l = 0; l < leftContextCount; l++)
                            {
                                String fvStringLeftContext = dataFVStringsLeftContext.get(l);

                                TrellisNode dataRightContext = bestPath.getDataNode(i + 1);

                                PhoneticCharacter dataPCRightContext = (PhoneticCharacter) dataRightContext.getStringNode().getFeature().getFeatures();

                                char dataCRightContext = dataPCRightContext.getCharacter().charValue();

                                Vector<String> dataFVStringsRightContext = phoneticModelOfScripts.getFeatureValueStrings(dataCRightContext, langEnc);

                                int rightContextCount = dataFVStringsRightContext.size();

                                for (int m = 0; m < rightContextCount; m++)
                                {
                                    String fvStringRightContext = dataFVStringsRightContext.get(m);

                                    String srcContextString = fvStringLeftContext + ":" + dataContextString + ":" + fvStringRightContext;

                                    addContexts(srcContextString, modelContextString, dataC, modelC, srcWord, tgtWord);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void calculateProbs()
    {
        Iterator itrTS = ft_Given_fs_cl_cr.keySet().iterator();

        while(itrTS.hasNext())
        {
            String tgtContextString = (String) itrTS.next();

            System.out.println(tgtContextString);
            
            TranslationListFreqProb freqProb = ft_Given_fs_cl_cr.get(tgtContextString);

            System.out.println("\t" + freqProb.getFrequency());

            String parts[] = tgtContextString.split("\\|");

            String srcContextString = parts[1];

//            System.out.println("\t" + srcContextString);

            Integer srcContextCount = fs_cl_cr.get(srcContextString);

            if(srcContextCount != null)
            {
                freqProb.setProbability(((double) freqProb.getFrequency()) / ((double) srcContextCount.intValue()));
            }
            else
                freqProb.setProbability(0.0);

            System.out.println("\t" + freqProb.getProbability());

            System.out.println("\t" + freqProb.getObjectString());
      }
    }

    public void addContexts(String srcContextString, String tgtContextString, char srcChar, char tgtChar, String srcWord, String tgtWord)
    {
        String contextString = srcContextString;

        Integer countInt = fs_cl_cr.get(contextString);

        if(countInt == null)
            fs_cl_cr.put(contextString, new Integer(1));
        else
            fs_cl_cr.put(contextString, new Integer(countInt.intValue() + 1));

        contextString = tgtContextString + "|" + contextString;

        TranslationListFreqProb freqProb = ft_Given_fs_cl_cr.get(contextString);

        if(freqProb == null)
            ft_Given_fs_cl_cr.put(contextString, new TranslationListFreqProb(new Character(srcChar) + " in " + srcWord, new Character(tgtChar) + " in " + tgtWord, 1));
        else
        {
            freqProb.setFrequency(freqProb.getFrequency() + 1);
            freqProb.addObjects(new Character(srcChar) + " in " + srcWord, new Character(tgtChar) + " in " + tgtWord);
        }
    }

    public static void main(String args[])
    {
        PhoneticVariation phoneticVariation = new PhoneticVariation();
        phoneticVariation.calculateCounts();
        phoneticVariation.calculateProbs();
    }
}
