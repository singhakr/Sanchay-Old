/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.svm;

import sanchay.formats.converters.SVM2SSF;
import sanchay.formats.converters.SSF2SVM;
import iitb.utils.Options;
//import libsvm.SVMTrain;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import libsvm.SVMPredict;
import sanchay.annotation.DefaultAnnotationMain;
import sanchay.annotation.SanchayAnnotationMain;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author anil
 */
public class SVMAnnotationMain extends DefaultAnnotationMain implements SanchayAnnotationMain {

    public SVMAnnotationMain() {

        labels = new KeyValueProperties(0, 10);

        ssf2ML = new SSF2SVM();
        ml2SSF = new SVM2SSF();
        ssf2MLTest = new SSF2SVM();

        options = new Options();

        options.setProperty("MaxMemory", "10");
    }

    public SVMAnnotationMain(int type) {
        this();

        formatType = type;
    }

    public void train() {
        if (getTrainDataPaths() != null) {
            getSSF2ML().init(getTrainDataPaths(), getBaseDir() + "/svm-train.txt", getCharset(), getBaseDir(), getLabelFeature());
        } else {
            getSSF2ML().init(getTrainDataPath(), getBaseDir() + "/svm-train.txt", getCharset(), getBaseDir(), getLabelFeature());
        }
        //getSSF2ML().convert(getFormatType());
        getSSF2ML().convert(SSF2SVM.TAG_FORMAT);
        getSSF2ML().saveFeatureLabels();

        try {
            getSSF2ML().getLabels().getInt2LabelMapping().save(getBaseDir() + "/labels", getCharset());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        setTrainData(getSSF2ML().getMLCorpus());

        try {

            initTrain();
        } catch (Exception ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void test2() {
        SSFStoryImpl ssfStory = new SSFStoryImpl();

        SSFStoryImpl finalStory = new SSFStoryImpl();

        try {
            ssfStory.readFile(getTestDataPath(), getCharset());

            SSFSentenceImpl ssfSentence = new SSFSentenceImpl();

            int scount = ssfStory.countSentences();

            for (int i = 0; i < 5; i++) {

                SSFStoryImpl smallStory = new SSFStoryImpl();

                ssfSentence = (SSFSentenceImpl) ssfStory.getSentence(i);

                smallStory.addSentence(ssfSentence);

                int wcount = ssfSentence.getRoot().getAllLeaves().size();

                System.out.println(" Word Count-" + wcount);

                for (int j = 0; j < wcount + 2; j++) {

                    smallStory.save(getBaseDir() + "/smallStory", getCharset());
                    //if( j==0 )
                    getSSF2MLTest().init(getBaseDir() + "/smallStory", getBaseDir() + "/smallStoryTagged", getCharset(), getBaseDir(), getLabelFeature());
                    //else
                    //    getSSF2MLTest().init(getBaseDir()+"/tempStory", getBaseDir()+"/tempStoryTagged", getCharset(), getBaseDir(), getLabelFeature());
                    getSSF2MLTest().setFeatureLabels();
                    getSSF2MLTest().setLabels(getSSF2ML().getLabels());
                    getSSF2MLTest().convert(getFormatType());

                    initTest();

                    getML2SSF().setLabels(getSSF2ML().getLabels());
                    //getML2SSF().init(getBaseDir() + "/smallStory", getBaseDir() + "/output", getCharset(), getBaseDir() + "/smallStory", getLabelFeature());
                    getML2SSF().init(getBaseDir() + "/smallStory", getBaseDir() + "/output", getCharset(), getBaseDir() + "smallStory", getLabelFeature());
                    getML2SSF().convert(getFormatType());

                    smallStory = new SSFStoryImpl();
                    smallStory.readFile(getBaseDir() + "/smallStory", getCharset());
                //smallStory.readFile(getBaseDir() + "\\smallStory", getCharset());
                }

                ssfSentence = (SSFSentenceImpl) smallStory.getSentence(0);
                finalStory.addSentence(ssfSentence);

            }

            finalStory.save(getTestDataPath() + "out.txt", getCharset());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    /*
    
    
    //if( j == 0 )
    
    //else
    //    getML2SSF().init(getBaseDir()+"/tempStory", getBaseDir()+"/output", getCharset(), getBaseDir()+"/tempStory", getLabelFeature()); 
    
    
    try {
    smallStory = new SSFStoryImpl();
    smallStory.readFile(getBaseDir() + "/smallStory", getCharset());
    } catch (FileNotFoundException ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    }
    
    try {
    
    smallStory.readFile(getBaseDir() + "/smallStory", getCharset());
    } catch (FileNotFoundException ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    }catch (Exception ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    } 
    
    taggedSsfStory.addSentence(smallStory.getSentence(0));
    
    }
    
    try {
    taggedSsfStory.save(getTestDataPath() + ".out.txt", getCharset());
    } catch (FileNotFoundException ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
    Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
    }*/

    }

    public void test() {
        try {

            load();
        } catch (Exception ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.err.println("-----------------------> Calling ssf2svm().init()");
        getSSF2MLTest().init(getTestDataPath(), getBaseDir() + "/smallStoryTagged", getCharset(), getBaseDir(), getLabelFeature());
        
        getSSF2MLTest().setLabels(getSSF2ML().getLabels());
        getSSF2MLTest().setFeatureLabels();
        //getSSF2MLTest().convert(getFormatType());
        System.err.println("-----------------------> Calling ssf2svm().convert()");
        getSSF2MLTest().convert(DefaultMLCorpusConverter.TAG_FORMAT);
        try {
            getSSF2MLTest().getLabels().getInt2LabelMapping().save(getBaseDir() + "/testLabels", getCharset());
            initTest();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SVMAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        }

//        getML2SSF().getLabels().setLabels(getSSF2ML().getLabels().getInt2LabelMapping());
//        getML2SSF().getLabels().setRevLabels(getSSF2ML().getLabels().getLabel2IntMapping());
        getML2SSF().getLabels().setLabels(getSSF2ML().getLabels().getLabel2IntMapping());
        getML2SSF().getLabels().setRevLabels(getSSF2ML().getLabels().getInt2LabelMapping());
        getML2SSF().init(getTestDataPath(), getBaseDir() + "/output", getCharset(), getTestDataPath() + ".out.txt", getLabelFeature());
        //getML2SSF().convert(getFormatType());
        getML2SSF().convert(DefaultMLCorpusConverter.TAG_FORMAT);
    }

    public void initTrain() throws Exception {
        String args[] = new String[8];
        args[0] = "-s";
        args[1] = "0";
        args[2] = "-t";
        args[3] = "0";
        args[4] = "-v";
        args[5] = "10";
        args[6] = getBaseDir() + "/svm-train.txt";
        args[7] = getBaseDir() + "/svm-model";
//        SVMTrain svmTrainOb = new SVMTrain();
//        svmTrainOb.main(args);
    }

    public void load() throws Exception {
        labels.read(getBaseDir() + "/labels", getCharset());
        getSSF2ML().getLabels().setLabels(labels.getReverse());
        getSSF2ML().getLabels().setRevLabels(labels);

    }

    public void initTest() throws Exception {

        String args[] = new String[3];

        args[0] = getBaseDir() + "/smallStoryTagged";
        args[1] = getBaseDir() + "/svm-model";
        args[2] = getBaseDir() + "/output";
//        SVMPredict predictOb = new SVMPredict();
        System.out.println(" Classifying...");        
//        predictOb.main(args);
    }

    public static void main(String argv[]) throws Exception {

        SVMAnnotationMain svmAnnotation = new SVMAnnotationMain();
//        svmAnnotation.trainDataPaths = new File[1];
//        svmAnnotation.trainDataPaths[0] = new File("/home/anil/projects/sanchay/sanchay-release/public/git/Sanchay/data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.out.txt");
//        svmAnnotation.trainDataPaths[0] = new File("data/automatic-annotation/pos-tagging/training/240k-hindi-wx.ssf.clean-1-bform.isc.utf8");
//        svmAnnotation.trainDataPaths[1] = new File("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/shallow-parsing-training-data/240k-hindi-wx.ssf.clean-2-bform.isc.utf8");
//        svmAnnotation.trainDataPaths[2] = new File("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/shallow-parsing-training-data/240k-hindi-wx.ssf.clean-1-bform.isc.utf8");
//        svmAnnotation.trainDataPaths[3] = new File("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/shallow-parsing-training-data/240k-hindi-wx.ssf.clean-5-bform.isc.utf8");
//        svmAnnotation.trainDataPaths[4] = new File("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/shallow-parsing-training-data/240k-hindi-wx.ssf.clean-6-bform.isc.utf8");
//        svmAnnotation.trainDataPaths[5] = new File("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/shallow-parsing-training-data/240k-hindi-wx.ssf.clean-7-bform.isc.utf8");
//        svmAnnotation.trainDataPaths[6] = new File("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/shallow-parsing-training-data/240k-hindi-wx.ssf.clean-8-bform.isc.utf8");
        
//        svmAnnotation.trainDataPaths = new File[]{
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Aparna/bhojpuri_data aparna rechecked.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Archana/bhojpuri new.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Archana/bhojpuri 2.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Archana/bhjpuri5.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Archana/bhjpuri4 (1).txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Archana/bhjpuri3 (1).txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/Bhoj 7 Khushboo.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/file1.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/file2.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/file3.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/file4.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/file5.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/file6.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Chandani and Khushboo/file7.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Deepak/Bhoj 5/deepak.txt.bak"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Deepak/Bhoj 5/to send.txt"),
//            new File("/home/anil/projects/nlp-tools/data/checked/bhojpuri/Deepak/Bhoj 15.txt")
//        };

        svmAnnotation.trainDataPaths = new File[]{
            new File("/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/navya/Magahi 2.txt"),
            new File("/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/navya/magahi.txt"),
            new File("/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/navya/magahi1"),
            new File("/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/rasmi/rasmi magahi .txt"),
            new File("/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/Shubhra/Magahi Data 2-Shubhra.txt.tmp"),
            new File("/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/swati/swati magahi.txt"),
            new File("/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/Vandana/Vandana.html")
        };
        
        //svmAnnotation.annotate();
        svmAnnotation.train();
        //svmAnnotation.initTrain();
//        svmAnnotation.load();
//        
//        svmAnnotation.testDataPath = "/home/anil/projects/nlp-tools/data/Magahi tagged Data Rechecked/Vandana/Vandana.html";
//        
//        svmAnnotation.test();
    }
}
