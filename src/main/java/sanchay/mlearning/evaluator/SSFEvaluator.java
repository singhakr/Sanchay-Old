package sanchay.mlearning.evaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;

/*
 * nerEval.java
 *
 * Created on October 3, 2008, 10:39 AM
 */
/**
 *
 * @author Anil Kumar Singh
 */
public class SSFEvaluator {

    /**
     * Creates a new instance of nerEval
     */
    public SSFEvaluator() {
    }
//        try {

//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace();
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
    public static void main(String args[]) {
//        if (args.length < 2)
//        {
//            System.out.println("USAGE: perl ne-eval.pl YOUR-DATA-FILE REF-DATA-FILE");
//        }
//
        try {
            Process cmd = Runtime.getRuntime().exec("rm *.ref");
            cmd = Runtime.getRuntime().exec("rm *.your");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String yourDataPath = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Enter_the_path_of_your_data_file"));
        String refDataPath = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Enter_the_path_of_your_reference_file"));

        String senStart = "<Sentence";
        String senEnd = "</Sentence";

        String nodeStartRegex = "((";
        String nodeEndRegex = "))";

        try {

            FileOutputStream detailsFile = new FileOutputStream("ne-eval.detail");
            FileOutputStream logFile = new FileOutputStream("ne-eval.log");






            OutputStreamWriter details = new OutputStreamWriter(detailsFile, GlobalProperties.getIntlString("UTF-8"));


            //my ($pm, $cm, $rm, $tm, $Fm, $pn, $cn, $rn, $tn, $Fn, $pl, $cl, $rl, $tl, $Fl);
            //my (%pm, %cm, %rm, %tm, %Fm, %pn, %cn, %rn, %tn, %Fn, %pl, %cl, %rl, %tl, %Fl);

            int pm = 0, cm = 0, rm = 0, tm = 0, Fm = 0, pn = 0, cn = 0, rn = 0, tn = 0, Fn = 0, pl = 0, cl = 0, rl = 0, tl = 0, Fl = 0;

            String tags[] = new String[]{"NEP", "NED", "NEO", "NEA", "NEB", "NETP", "NETO", "NEL", "NETI", "NEN", "NEM", "NETE"};

            SSFEvaluator nerEvalObj = new SSFEvaluator();

            nerEvalObj.evalNE(yourDataPath, refDataPath, logFile);



            List vpm = new ArrayList();
            List vcm = new ArrayList();
            List vrm = new ArrayList();
            List vtm = new ArrayList();
            List vFm = new ArrayList();
            List vpn = new ArrayList();
            List vcn = new ArrayList();
            List vrn = new ArrayList();
            List vtn = new ArrayList();
            List vFn = new ArrayList();
            List vpl = new ArrayList();
            List vcl = new ArrayList();
            List vrl = new ArrayList();
            List vtl = new ArrayList();
            List vFl = new ArrayList();

            int allCalc[] = nerEvalObj.calcMeasures();

            nerEvalObj.printResults(allCalc[2], allCalc[0], allCalc[1], "Maximal");

            nerEvalObj.printResults(allCalc[5], allCalc[3], allCalc[4], "Nested");

            nerEvalObj.printResults(allCalc[8], allCalc[6], allCalc[7], "Lexical");

            for (int i = 0; i < tags.length; i++) {
                System.out.println("For " + tags[i] + ":");
                //nerEvalObj.printResults()
//            nerEvalObj.printResults((Integer)vpm.get(i), (Integer)vcm.get(i), (Integer)vrm.get(i), (Integer)vtm.get(i), (Integer)vFm.get(i), "Maximal ("+tags[i]+")" );
//            System.out.println();
//            nerEvalObj.printResults((Integer)vpm.get(i), (Integer)vcm.get(i), (Integer)vrm.get(i), (Integer)vtm.get(i), (Integer)vFm.get(i), "Nested ("+tags[i]+")" );
//            System.out.println();
//            nerEvalObj.printResults((Integer)vpm.get(i), (Integer)vcm.get(i), (Integer)vrm.get(i), (Integer)vtm.get(i), (Integer)vFm.get(i), "Lexical ("+tags[i]+")" );            System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int[] calcMeasures() {
        int mCalc[] = new int[3];
        int lCalc[] = new int[3];
        int nCalc[] = new int[3];
        // int allCalc[] = new int[9];

        try {
            FileInputStream yourMaxNEFile = new FileInputStream("ne-list-max.your");
            FileInputStream yourNesNEFile = new FileInputStream("ne-list-nes.your");
            FileInputStream yourLexNEFile = new FileInputStream("ne-list-lex.your");
            FileInputStream refMaxNEFile = new FileInputStream("ne-list-max.ref");
            FileInputStream refNesNEFile = new FileInputStream("ne-list-nes.ref");
            FileInputStream refLexNEFile = new FileInputStream("ne-list-lex.ref");

            InputStreamReader yourMaxNE = new InputStreamReader(yourMaxNEFile, GlobalProperties.getIntlString("UTF-8"));
            InputStreamReader yourNesNE = new InputStreamReader(yourNesNEFile, GlobalProperties.getIntlString("UTF-8"));
            InputStreamReader yourLexNE = new InputStreamReader(yourLexNEFile, GlobalProperties.getIntlString("UTF-8"));
            InputStreamReader refMaxNE = new InputStreamReader(refMaxNEFile, GlobalProperties.getIntlString("UTF-8"));
            InputStreamReader refNesNE = new InputStreamReader(refNesNEFile, GlobalProperties.getIntlString("UTF-8"));
            InputStreamReader refLexNE = new InputStreamReader(refLexNEFile, GlobalProperties.getIntlString("UTF-8"));

            mCalc = compare(yourMaxNE, refMaxNE, "m");
            nCalc = compare(yourNesNE, refNesNE, "n");
            lCalc = compare(yourLexNE, refLexNE, "l");



            yourMaxNEFile.close();
            yourNesNEFile.close();
            yourLexNEFile.close();
            refMaxNEFile.close();
            refNesNEFile.close();
            refLexNEFile.close();

            //return allCalc;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        int allCalc[] = {mCalc[0], mCalc[1], mCalc[2], nCalc[0], nCalc[1], nCalc[2], lCalc[0], lCalc[1], lCalc[2]};
        return allCalc;

    }

    int[] compare(InputStreamReader yourFile, InputStreamReader refFile, String mORnORl) {
        List your = new ArrayList();
        List ref = new ArrayList();
        int retCount = 0;
        int totCount = 0;
        int cm = 0, cn = 0, cl = 0;
        int arr[] = new int[3];
        int i = 0;
        BufferedReader yourInput = new BufferedReader(yourFile);
        BufferedReader refInput = new BufferedReader(refFile);
        try {
            while (yourInput.readLine() != null) {
                try {
                    your.add(yourInput.readLine());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            while (refInput.readLine() != null) {
                try {
                    ref.add(refInput.readLine());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }



        for (i = 0; i < your.size(); i++) {
            System.out.println(GlobalProperties.getIntlString("Inside_for_loop_for_comparison"));
            System.out.println("your.size = " + your.size());
            System.out.println("your.get(i) = " + your.get(i) + "  ref.get(i) = " + ref.get(i));
            if (your.get(i) != null && ref.contains(your.get(i)))// != null && (your.get(i) == ref.get(i)))
            {
                System.out.println("your.get(i) = " + your.get(i));
                if ("m".equals(mORnORl)) {
                    System.out.println("cm = " + cm);
                    cm++;
                }
                if ("l".equals(mORnORl)) {
                    System.out.println("cl = " + cl);
                    cl++;
                }
                if ("n".equals(mORnORl)) {
                    System.out.println("cn = " + cn);
                    cn++;
                }
            }
        }

        retCount = your.size();
        totCount = ref.size();

        if ("m".equals(mORnORl)) {

            arr[0] = retCount;
            arr[1] = totCount;
            arr[2] = cm;
        }


        if ("l".equals(mORnORl)) {

            arr[0] = retCount;
            arr[1] = totCount;
            arr[2] = cl;
        }


        if ("n".equals(mORnORl)) {

            arr[0] = retCount;
            arr[1] = totCount;
            arr[2] = cn;
        }
        System.out.println("cm = " + cm + "    cn = " + cn + "    cl = " + cl);
        return arr;
    }

    void printResults(float c, float r, float t, String type) {
        float p = 0;
        float F = 0;
        System.out.println(type + GlobalProperties.getIntlString("_Retrieved_Count:_") + r);
        System.out.println(type + GlobalProperties.getIntlString("_Correct_Count:_") + c);
        System.out.println(type + GlobalProperties.getIntlString("_Total_Count:_") + t);
        if (r > 0 && t > 0) {
            p = c / r;
            r = c / t;

            System.out.println(type + GlobalProperties.getIntlString("_Precision:_") + p);

            System.out.println(type + GlobalProperties.getIntlString("_Recall:_") + r);
        } else {
            System.err.println(GlobalProperties.getIntlString("Division_by_zero_or_nothing_found."));
        }

        if ((p + r) > 0) {
            F = (2 * p * r) / (p + r);
            System.out.println(type + GlobalProperties.getIntlString("_F-measure:_") + F);
        } else {
            System.err.println(GlobalProperties.getIntlString("Division_by_zero_or_nothing_found."));
        }
    }

    void evalNE(String yourData, String refData, FileOutputStream logFile) {
        try {
            OutputStreamWriter log = new OutputStreamWriter(logFile);
            System.out.println(GlobalProperties.getIntlString("reached_evalNE"));
            log.write(GlobalProperties.getIntlString("Processing_file_") + refData + "\n");
            System.out.println(GlobalProperties.getIntlString("after_log.write"));
            File yData = new File(yourData);
            System.out.println(GlobalProperties.getIntlString("after_log.write1"));
            File rData = new File(refData);
            System.out.println(GlobalProperties.getIntlString("after_log.write2"));
            if (yData.exists() && rData.exists()) {
                log.write(GlobalProperties.getIntlString("Checked_file_") + refData + "\n");
                System.out.println(GlobalProperties.getIntlString("after_log.write3"));
                evalFileNE(yourData, refData, log);
                // log.close();
            } else {
                System.err.println(GlobalProperties.getIntlString("Something_wrong_with_files_or_path."));
            }
            log.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void evalFileNE(String yourData, String refData, OutputStreamWriter logFile) {
        try {

            FileOutputStream yourMaxNEFile = new FileOutputStream("ne-list-max.your");
            FileOutputStream yourNesNEFile = new FileOutputStream("ne-list-nes.your");
            FileOutputStream yourLexNEFile = new FileOutputStream("ne-list-lex.your");
            FileOutputStream refMaxNEFile = new FileOutputStream("ne-list-max.ref");
            FileOutputStream refNesNEFile = new FileOutputStream("ne-list-nes.ref");
            FileOutputStream refLexNEFile = new FileOutputStream("ne-list-lex.ref");

            OutputStreamWriter yourMaxNE = new OutputStreamWriter(yourMaxNEFile, GlobalProperties.getIntlString("UTF-8"));
            OutputStreamWriter yourNesNE = new OutputStreamWriter(yourNesNEFile, GlobalProperties.getIntlString("UTF-8"));
            OutputStreamWriter yourLexNE = new OutputStreamWriter(yourLexNEFile, GlobalProperties.getIntlString("UTF-8"));
            OutputStreamWriter refMaxNE = new OutputStreamWriter(refMaxNEFile, GlobalProperties.getIntlString("UTF-8"));
            OutputStreamWriter refNesNE = new OutputStreamWriter(refNesNEFile, GlobalProperties.getIntlString("UTF-8"));
            OutputStreamWriter refLexNE = new OutputStreamWriter(refLexNEFile, GlobalProperties.getIntlString("UTF-8"));

            logFile.write(GlobalProperties.getIntlString("Evaluating_file_") + refData + "\n");
            List yourSentences = new ArrayList();
            List refSentences = new ArrayList();
            yourSentences = getSentences(yourData, logFile);
            refSentences = getSentences(refData, logFile);
            System.out.println(yourSentences.size() + "  " + refSentences.size());
            int senId = 0;
            if (yourSentences.size() != refSentences.size()) {
                System.err.println(GlobalProperties.getIntlString("Number_of_test_and_reference_sentences_are_not_same."));
            }
            for (int i = 0; i < yourSentences.size(); i++) {
                senId = i + 1;
                evalSentenceNE((SSFSentence) yourSentences.get(i), yourData, yourMaxNE, yourNesNE, yourLexNE, refMaxNE, refLexNE, refNesNE, "y");
                evalSentenceNE((SSFSentence) refSentences.get(i), refData, yourMaxNE, yourNesNE, yourLexNE, refMaxNE, refLexNE, refNesNE, "r");
            }

            try {

                yourMaxNEFile.close();
                yourNesNEFile.close();
                yourLexNEFile.close();
                refMaxNEFile.close();
                refNesNEFile.close();
                refLexNEFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    List getSentences(String file, OutputStreamWriter logFile) {
        try {
            logFile.write(GlobalProperties.getIntlString("Getting_sentences_from_file_") + file + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        List sentences = new ArrayList();

        SSFStory story = new SSFStoryImpl();
        try {
            story.readFile(file, GlobalProperties.getIntlString("UTF-8"));
            int senCount = story.countSentences();
            System.out.println(GlobalProperties.getIntlString("no._of_sentences_in_file_=") + senCount);
            for (int i = 0; i < senCount; i++) {
                sentences.add(i, story.getSentence(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return sentences;


    }

    void evalSentenceNE(SSFSentence sent, String file, OutputStreamWriter yourMaxNE, OutputStreamWriter yourNesNE, OutputStreamWriter yourLexNE, OutputStreamWriter refMaxNE, OutputStreamWriter refLexNE, OutputStreamWriter refNesNE, String yr) {



        List words = ((SSFPhrase) sent.getRoot()).getAllLeaves();
        System.out.println(GlobalProperties.getIntlString("no._of_words_=_") + words.size());
        for (int i = 0; i < words.size(); i++) {
            System.out.println(GlobalProperties.getIntlString("word_=_") + ((SSFNode) words.get(i)).toString());
            SSFNode parent = (SSFNode) ((SSFNode) words.get(i)).getParent();
            System.out.println(parent.toString());
            FeatureStructures fs = parent.getFeatureStructures();
            if (fs != null && fs.countAltFSValues() > 0) {
                System.out.println(GlobalProperties.getIntlString("Inside_Feature_Str"));
                FeatureStructure fs1 = fs.getAltFSValue(0);
                FeatureAttribute fa = fs1.getAttribute("ne");
                if (fa != null && fa.countAltValues() > 0) {
                    String wordFeature = (String) fa.getAltValue(0).getValue();
                    System.out.println(GlobalProperties.getIntlString("word_Feature_=_") + wordFeature);

                    if (wordFeature != null) {
                        try {
                            if ("y".equals(yr)) {
                                System.out.println(sent.getId() + " " + parent.getId() + " " + parent.getNext().getId() + " " + wordFeature);
                                yourMaxNE.write(sent.getId() + "::" + parent.getId() + "::" + parent.getNext().getId() + "::" + wordFeature + "\n");

                            } else if ("r".equals(yr)) {

                                refMaxNE.write(sent.getId() + "::" + parent.getId() + "::" + parent.getNext().getId() + "::" + wordFeature + "\n");

                            }

                            if ("y".equals(yr)) {

                                yourNesNE.write(sent.getId() + "::" + parent.getId() + "::" + parent.getNext().getId() + "::" + wordFeature + "\n");

                            } else if ("r".equals(yr)) {

                                refNesNE.write(sent.getId() + "::" + parent.getId() + "::" + parent.getNext().getId() + "::" + wordFeature + "\n");

                            }
                            if ("y".equals(yr)) {

                                yourLexNE.write(sent.getId() + "::" + parent.getId() + "::" + parent.getNext().getId() + "::" + wordFeature + "\n");

                            } else if ("r".equals(yr)) {

                                refLexNE.write(sent.getId() + "::" + parent.getId() + "::" + parent.getNext().getId() + "::" + wordFeature + "\n");

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
        }
    }
}
