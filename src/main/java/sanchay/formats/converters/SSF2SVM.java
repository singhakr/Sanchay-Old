/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.formats.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFCorpusImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.mlearning.crf.DefaultDataSequence;
import sanchay.mlearning.svm.SVMExtractFeatures;
import sanchay.properties.KeyValueProperties;
import sanchay.tree.SanchayMutableTreeNode;

/**
 *
 * @author anil
 */
public class SSF2SVM extends DefaultMLCorpusConverter implements MLCorpusConverter {

    PrintStream ps;
    protected SVMExtractFeatures svmExtractFeatures;
    String baseDir = new String();
    private int TESTING_MODE = 0;

    public SSF2SVM() {
        super();
    }

    @Override
    public void init(String SSFPath, String mlPath, String cs, String baseDirPath, String labelFeature) {
        System.out.println(GlobalProperties.getIntlString("_Initialising..."));

        super.init(SSFPath, mlPath, cs, baseDirPath + "/featureLabels", labelFeature);
        svmExtractFeatures = new SVMExtractFeatures();
        //setFeatureLabels();

        ssfCorpus = new SSFCorpusImpl(charset);

        baseDir = baseDirPath;

        try {
            ssfCorpus.read(new File(ssfPath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            ps = new PrintStream(mlPath, cs);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void init(File ssfFiles[], String mlPath, String cs, String baseDirPath, String labelFeature) {
        System.out.println(GlobalProperties.getIntlString("Initialising...."));

        super.init(ssfFiles, mlPath, cs, baseDirPath + "/featureLabels", labelFeature);
        svmExtractFeatures = new SVMExtractFeatures();
        //setFeatureLabels();

        ssfCorpus = new SSFCorpusImpl(charset);

        baseDir = baseDirPath;

        try {
            ssfCorpus.read(ssfFiles);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
        
            ps = new PrintStream(mlPath, cs);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void convertToTagFormat(boolean featureType) {

        List words = new ArrayList();

        SSFSentenceImpl ssfSentence;

        SSFPhrase root = new SSFPhrase();

        SSFNode word = new SSFNode();

        String svmInstance = new String();

        String label = new String();

        SSFStoryImpl ssfStory = new SSFStoryImpl();

        Enumeration enu = ssfCorpus.getStoryKeys();


        while (enu.hasMoreElements()) {
            try {
                String p = (String) enu.nextElement();
                //System.out.println("SSFCORPUS ENU----------------------->"+p);
                //String p = ssfPath;
                //System.out.println("Path "+p);
                ssfStory.readFile(p, charset); // get this using ssfCorpus.getStoryKeys(); later
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println(ssfStory.countSentences());
            int scount = ssfStory.countSentences();

            for (int i = 0; i < scount; i++) {

                ssfSentence = (SSFSentenceImpl) ssfStory.getSentence(i);

                root = ssfSentence.getRoot();

                words = root.getAllLeaves();

                for (int j = 0; j < words.size(); j++) {
                    String labelIndexStr = new String();

                    word = (SSFNode) words.get(j);

                    label = word.getName();

                    labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);

                    int labelIndex = -1;

//                    if ((labelIndexStr == null || labelIndexStr.equals("null")) && TESTING_MODE == 0) {
                    if (labelIndexStr == null || labelIndexStr.equals("null")) {
                        labelIndex = labels.getLabel2IntMapping().countProperties();

                        labelIndexStr = "" + labelIndex;

                        labels.getInt2LabelMapping().addProperty("" + labelIndex, label);

                        labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                    } else {
                        try {
                            labelIndex = Integer.parseInt(labelIndexStr);
                        } catch (NumberFormatException ex) {
                            System.err.println(GlobalProperties.getIntlString("_Number_Format_Error:_Tag_without_Number-") + label);
                            labelIndexStr = labels.getLabel2IntMapping().getPropertyValue("NN");
                            labelIndex = Integer.parseInt(labelIndexStr);
                        }
                    }

                    svmExtractFeatures.init(ssfStory, (SSFSentenceImpl) ssfSentence, word, charset, labels);

                    svmExtractFeatures.addSVMfeatures();

                    svmInstance = "" + labelIndexStr;

                    svmInstance = svmInstance + svmExtractFeatures.getInstance();

                    System.out.println(svmInstance);

                    ps.println(svmInstance);

                }
            }
        }
        
        labelSaveTemp();
    }

    /*private void finish() throws FileNotFoundException, UnsupportedEncodingException
     {
     labels.getInt2LabelMapping().save(getBaseDIRPath() + "POSlabels", charset);
     }*/
    @Override
    protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries) {

        Enumeration enm = ssfCorpus.getStoryKeys();

        int docIndex = 0;

        while (enm.hasMoreElements()) {
            String storyPath = (String) enm.nextElement();

            SSFStory ssfStory = new SSFStoryImpl();

            try {
                ssfStory.readFile(storyPath, charset);

                if (SimpleStoryImpl.getCorpusType(storyPath, charset) != CorpusType.RAW) {
                    String tmpFile = GlobalProperties.getHomeDirectory() + "/" + "tmp/tmp-tmp-tmp.abc.xyz";

                    ssfStory.save(tmpFile, charset);

                    ssfStory = new SSFStoryImpl();

                    ssfStory.readFile(tmpFile, charset);
                }
            } catch (Exception ex) {
                Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
            }

            int scount = ssfStory.countSentences();

            for (int i = 0; i < scount; i++) {

                SSFSentence ssfSentence = ssfStory.getSentence(i);

                DefaultDataSequence mlSentence = new DefaultDataSequence();

                SSFPhrase root = ssfSentence.getRoot();

                List<SanchayMutableTreeNode> words = root.getAllLeaves();

                int wcount = words.size();

                for (int j = 0; j < wcount; j++) {
                    SSFNode word = (SSFNode) words.get(j);

                    String label = new String();
                    /* if(featureType)
                     {
                     label = word.getAttributeValue(labelFeature);
                    
                     if(label == null)
                     label = MLClassLabels.UNDEFINED_LABEL;
                     }*/
                    label = "" + SVMExtractFeatures.getBIOTag(root, true).get(j);


                    String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);

                    int labelIndex = -1;


//                    if ((labelIndexStr == null || labelIndexStr.equals("null")) && TESTING_MODE == 0) {
                    if (labelIndexStr == null || labelIndexStr.equals("null")) {
                        labelIndex = labels.getLabel2IntMapping().countProperties();

                        labelIndexStr = "" + labelIndex;

                        labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
                        labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                    } else {
                        try {
                            labelIndex = Integer.parseInt(labelIndexStr);
                        } catch (NumberFormatException ex) {
                            System.err.println(GlobalProperties.getIntlString("_Number_Format_Error:_Tag_without_Number-") + label);
                            labelIndexStr = labels.getLabel2IntMapping().getPropertyValue("I-NP");
                            labelIndex = Integer.parseInt(labelIndexStr);
                        }
                    }

                    System.out.print(label + " ");


                    svmExtractFeatures.init((SSFStoryImpl) ssfStory, (SSFSentenceImpl) ssfSentence, word, charset, labels);

                    svmExtractFeatures.addSVMfeatures();

                    String svmInstance = "" + labelIndex;

                    svmInstance = svmInstance + svmExtractFeatures.getInstance();

                    System.out.println(svmInstance);

                    ps.println(svmInstance);
                }
            }
        }
        
        labelSaveTemp();
    }

    @Override
    public void setLabels(MLClassLabels mlLabels) {
//        TESTING_MODE = 1;
        labels = mlLabels;
    }

    @Override
    public void setFeatureLabels() {
        KeyValueProperties featureLabels = new KeyValueProperties();
        try {
            featureLabels.read(baseDir + "/featureLabels", charset);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        }
        svmExtractFeatures.setLabels(featureLabels.getReverse());
    }

    @Override
    public void saveFeatureLabels() {
        try {

            svmExtractFeatures.saveLabels(outputPath, charset);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void labelSaveTemp() {
        try {

            svmExtractFeatures.saveLabels(baseDir + "/testFeatureLabels", charset);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2SVM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * ********************************* main() not needed *******************************************
     */
    public static void main(String args[]) throws FileNotFoundException, IOException {
//        SSF2SVM ssf2svm = new SSF2SVM();
//        ssf2svm.init("/media/disk/Users/53590/Desktop/IIIT H/CORPUS/shallow-parsing-test-data/240k-hindi-wx.ssf.clean-5-bform.isc.utf8",
//                "/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/SVM_Pos-tagging/svm-train.txt",
//                GlobalProperties.getIntlString("UTF-8"),
//                "/media/disk/Users/53590/Desktop/IIIT H/test/svm/learnt model/potagging_linear_kernel",
//                "ne");
//        /*ssf2svm.labels.getInt2LabelMapping().read("/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/pos-tagging/learntModels/labels",
//         "UTF-8");*/
//        ssf2svm.convert(CHUNK_FORMAT);
//        ssf2svm.labels.getInt2LabelMapping().save("/media/disk/Users/53590/Desktop/IIIT H/Sanchay/Sanchay new version/Sanchay-17-05-09/data/automatic-annotation/SVM_Pos-tagging/17-06-labels.txt", ssf2svm.charset);

        SSF2SVM converter = new SSF2SVM();
        
//        converter.init("data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.out.txt",
//                "tmp/svm-ml.txt", "UTF-8", "tmp/svm", "ne");
        converter.init("data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.out.txt",
                "tmp/svm-ml-test.txt", "UTF-8", "tmp/svm", "ne");
        
        converter.convertToTagFormat(false);
        
        try {
            converter.getLabels().getInt2LabelMapping().save("tmp/int2labels.txt", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSF2CRF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SSF2CRF.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}