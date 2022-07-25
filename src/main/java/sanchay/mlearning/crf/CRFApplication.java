/*
 * CRFApplication.java
 *
 * Created on September 7, 2008, 11:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import iitb.crf.CRF;
import iitb.crf.DataSequence;
import iitb.model.*;

import java.util.Properties;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class CRFApplication {
    Properties options;
    CRF crfModel;
    FeatureGenImpl featureGen;
    
//    String trainDataPath = "data/automatic-annotation/pos-tagging/training/";
//    String testDataPath = "data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5";
//    String taggedDataPath = "data/automatic-annotation/pos-tagging/testing/story_27_1.final.mod.utf8-5.tagged";

    String trainDataPath = "/home/anil/projects/nlp-tools/pos-tagger/crf/training/crf-ml.txt";
    String testDataPath = "/home/anil/projects/nlp-tools/pos-tagger/crf/testing/";
    String taggedDataPath = "/home/anil/projects/nlp-tools/pos-tagger/crf/testing/.tagged";
    
    DefaultCRFData trainData;
    DefaultCRFData testData;
    
    String charset = GlobalProperties.getIntlString("UTF-8");
    
//    String baseDir = "data/automatic-annotation/pos-tagging";
    String baseDir = "/home/anil/projects/nlp-tools/pos-tagger/crf";
    String outDir = "sample";
    
    int numLabels = 42;
    
    public CRFApplication()
    {
        trainData = new DefaultCRFData();
        testData = new DefaultCRFData();        
    }

    public static void main(String argv[]) throws Exception {
        /* 
         * Initialization:
         * Get the required arguements for the application here.
         * Also, you will need to create a Properties object for arguements to be 
         * passed to the CRF. You do not need to worry about this object, 
         * because there are default values for all the parameters in the CRF package.
         * You may need to pass your own parameters values for tuning the application 
         * performance.
         */
        
        CRFApplication crfApplication = new CRFApplication();

        /*
         * There are mainly two phases for a learning application: Training and Testing.
         * Implement two routines for each of the phases and call them appropriately here.
         */
        crfApplication.train();
//        crfApplication.test();
    }

    public void train() throws Exception {
        /*
         * Read the training dataset into an object which implements DataIter 
         * interface(trainData). Each of the training instance is encapsulated in the 
         * object which provides DataSequence interface. The DataIter interface
         * returns object of DataSequence (training instance) in next() routine.
         */

         trainData.readTagged(trainDataPath, charset);

        /*
         * Once you have loaded the training dataset, you need to allocate objects 
         * for the model to be learned. allocmodel() method does that allocation.
         */
        allocModel();
	
        /*
         * You may need to train some of the feature types class. This training is 
         * needed for features which need to learn from the training data for instance
         * dictionary features build generated from the training set.
         */
        featureGen.train(trainData);

        /*
         * Call train routine of the CRF model to train the model using the 
         * train data. This routine returns the learned weight for the features.
         */
        double featureWts[] = crfModel.train(trainData);

        /*
         * You can store the learned model for later use into disk.
         * For this you will have to store features as well as their 
         * corresponding weights.
         */
        crfModel.write(baseDir+"/learntModels/"+outDir+"/crf");
        featureGen.write(baseDir+"/learntModels/"+outDir+"/features");

    }

    public void test() throws Exception {
        /*
         * Read the test dataset. Each of the test instance is encapsulated in the 
         * object which provides DataSequence interface. 
         */

        testData.readRaw(testDataPath, charset);

        /*
         * Once you have loaded the test dataset, you need to allocate objects 
         * for the model to be learned. allocmodel() method does that allocation.
         * Also, you need to read learned parameters from the disk stored after
         * training. If the model is already available in the memory, then you do 
         * not need to reallocate the model i.e. you can skip the next step in that
         * case.
         */
        allocModel();
        featureGen.read(baseDir+"/learntModels/"+outDir+"/features");
        crfModel.read(baseDir+"/learntModels/"+outDir+"/crf");

        /*
         * Iterate over test data set and apply the crf model to each test instance.
         */
            
        DataSequence testRecord = null;

        testData.startScan();
       
        while(testData.hasNext())
        {
            testRecord = testData.next();
            /*
             * Now apply CRF model to each test instance.
             */
            crfModel.apply(testRecord);

            /*
             * The labeled instance have value of the states as labels. 
             * These state values are not labels as supplied during training.
             * To map this state to one of the labels you need to call following
             * method on the labled testRecord.
             */
            featureGen.mapStatesToLabels(testRecord);
        }        
        
        testData.saveTagged(taggedDataPath, charset);
    }

    void  allocModel() throws Exception {
        /*
         * A CRF model consists of features and corresponding weights.
         * The features are stored in FeatureGenImpl and weights and other
         * CRF parameters are encapsulated in CRF object.
         *
         * Here, you will call appropriate constructor for a feature generator 
         * and a CRF model. You can use feature generator available in the 
         * package or use your own implemented feature generator.
         *
         * There are two CRF model classes: CRF and NestedCRF. The CRF class is
         * flat CRF model while NestedCRF is a segment(semi-)CRF model.
         */ 
        featureGen = new FeatureGenImpl("naive", numLabels);
        crfModel=new CRF(numLabels, featureGen, "");
    } 
};

