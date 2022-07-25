/*
 * ArffToSSF.java
 *
 * Created on September 1, 2008, 3:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.extraction.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.features.impl.FeatureAttributeImpl;
import sanchay.corpus.ssf.features.impl.FeatureStructureImpl;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.features.impl.FeatureValueImpl;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;

/**
 *
 * @author Anil Kumar Singh
 */
public class YamchaToSSF {
    
    private Vector parse(String line)
    {
        StringTokenizer tkn = new StringTokenizer(line, "\t");
        
        String first = tkn.nextToken();
        String second = new String();
        while(tkn.hasMoreElements())
        {
            second = tkn.nextToken();
        }
        
        Vector v = new Vector();
        v.insertElementAt(first, 0);
        System.out.println(line);
        System.out.println(second);
        v.insertElementAt(second.substring(0, 1), 1);
        if(second.length() < 2)
            v.insertElementAt(GlobalProperties.getIntlString("NotPresentInFile"), 2);
        else
            v.insertElementAt(second.substring(2), 2);
        return v;
    }
    
    /** Creates a new instance of ArffToSSF */
    public static void main(String arg[]) throws IOException{
        YamchaToSSF ats = new YamchaToSSF();
        String source = new String("/home1/sanchay/automatic-annotation/results/crf/refinedData/with-dynamic-features/test-output");
        String destination = new String("/home1/sanchay/automatic-annotation/results/crf/refinedData/with-dynamic-features/test-output.ssf");
    //    PrintStream ps = new PrintStream(destination, "UTF-8");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));

//        String sourceSentenceCount = new String("/home1/sanchay/automatic-annotation/test-data--count-of-sentences");
//        BufferedReader readerSentenceCount = new BufferedReader(new InputStreamReader(new FileInputStream(sourceSentenceCount), "UTF-8"));
        String line = new String();
        int flagRead = 0;

        SSFStory ssfStory = new SSFStoryImpl();
        ssfStory.setId("1");
        SSFSentence ssfSentence = new SSFSentenceImpl();

        SSFPhrase root = null;

        SSFNode ssfNode = null;
        SSFPhrase ssfPhrase = null;
        String fsString = "";
        try {

//        String sentenceCountWordNumer = new String(readerSentenceCount.readLine());
//        int sentenceCount=1;
            root = new SSFPhrase("0", "((", "SSF", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        ssfSentence.setRoot(root);
        int i=1, sentenceId=1;
        while((line = reader.readLine()) != null) {
//            if(line.substring(0, 5).equalsIgnoreCase("@DATA")){
//                flagRead = 1;
//                continue;
//            }
//
//            if(flagRead == 0) continue;
            if(line.equalsIgnoreCase(""))
            {
                if(i != 1)
                {
                    //System.out.println("came here");
                    ssfSentence.setId(sentenceId+"");
                    sentenceId++;
                    ssfStory.addSentence(ssfSentence);
                }
                try {
                    ssfSentence = new SSFSentenceImpl();
                    root = new SSFPhrase("0", "((", "SSF", "");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                ssfSentence.setRoot(root);
                continue;
            }
            
            i=2;
            Vector list = ats.parse(line);
            if(list.elementAt(1).toString().equalsIgnoreCase("B"))
            {
                if(ssfNode != null)
                    root.addChild(ssfPhrase);

                try {
                    ssfPhrase = new SSFPhrase("0", "((", "NP", "");

                    FeatureStructures fss = new FeatureStructuresImpl();
                    FeatureStructure fs = new FeatureStructureImpl();
                    FeatureAttribute fa = new FeatureAttributeImpl();
                    FeatureValue fv = new FeatureValueImpl();

                    fa.setName("ne");
                    fv.setValue((String) list.elementAt(2));
                    fa.addAltValue(fv);
                    fs.addAttribute(fa);

                    fss.addAltFSValue(fs);

                    ssfPhrase.setFeatureStructures(fss);

                    ssfNode = new SSFLexItem("0", (String) list.elementAt(0), "", "");
                    ssfPhrase.addChild(ssfNode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
    //            ps.println("((\tNP\t<ne=" + list.elementAt(2)+">");
    //            ps.println(list.elementAt(0));
    //            ps.println("))");
            }
            if(list.elementAt(1).toString().equalsIgnoreCase("I"))
            {
                if(ssfPhrase == null)
                {
                    try {
                        ssfPhrase = new SSFPhrase("0", "((", "NP", "");

                        FeatureStructures fss = new FeatureStructuresImpl();
                        FeatureStructure fs = new FeatureStructureImpl();
                        FeatureAttribute fa = new FeatureAttributeImpl();
                        FeatureValue fv = new FeatureValueImpl();

                        fa.setName("ne");
                        fv.setValue((String) list.elementAt(2));
                        fa.addAltValue(fv);
                        fs.addAttribute(fa);

                        fss.addAltFSValue(fs);

                        ssfPhrase.setFeatureStructures(fss);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }                
                }

                try {
                    ssfNode = new SSFLexItem("0", (String) list.elementAt(0), "", "");
                    ssfPhrase.addChild(ssfNode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
    //           ps.println("((\tNP\t<ne=" + list.elementAt(2)+">");
    //           ps.println(list.elementAt(0));
    //           ps.println("))");
            }
            if(list.elementAt(1).toString().equalsIgnoreCase("O"))
            {
                if(ssfNode != null)
                    root.addChild(ssfPhrase);
    //            nodeEnd = false;
                try {
                    ssfNode = new SSFLexItem("0", (String) list.elementAt(0), "", "");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                root.addChild(ssfNode);
                ssfNode = null;
    //           ps.println(list.elementAt(0));
            }
        
            //System.err.println(line);
        }
        
        
        //ssfStory.addSentence(ssfSentence);
        
        //System.out.println(ssfStory.makeString());
        ssfStory.save(destination, GlobalProperties.getIntlString("UTF-8"));
        
        System.out.println(ssfStory.countSentences());
        
        System.out.println(ssfStory.getSentence(1).toString());
        reader.close();
    }
}
