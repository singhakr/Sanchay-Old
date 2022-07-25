/*
 * CRFAnnotation.java
 *
 * Created on September 10, 2008, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import sanchay.formats.converters.SSF2CRF;
import sanchay.formats.converters.CRF2SSF;
import sanchay.annotation.SanchayAnnotationMain;
import iitb.crf.CRF;
import iitb.crf.DataSequence;
import iitb.crf.FeatureGenerator;
import iitb.crf.FeatureGeneratorNested;
import iitb.model.FeatureGenImpl;
import iitb.model.NestedFeatureGenImpl;
import iitb.utils.Options;
import sanchay.GlobalProperties;
import sanchay.annotation.DefaultAnnotationMain;
import sanchay.properties.KeyValueProperties;
import sanchay.formats.converters.CRF2SSF;

/**
 *
 * @author Anil Kumar Singh
 */
public class CRFAnnotationMain extends DefaultAnnotationMain implements SanchayAnnotationMain {
    protected CRF crfModel;
    protected FeatureGenerator featureGen;
    private int maxIter;
    /** Creates a new instance of CRFAnnotation */
    public CRFAnnotationMain() {
        labels = new KeyValueProperties(0, 10);
        
        ssf2ML = new SSF2CRF();
        ml2SSF = new CRF2SSF();
        ssf2MLTest = new SSF2CRF();
    	options = new Options();

        options.setProperty("MaxMemory", "10");
//        options.setProperty("modelGraph", "noEdge");
    }

    public CRFAnnotationMain(int type) {
        this();

        formatType = type;
    }

    /**
     * @return the crfModel
     */
    public CRF getCrfModel() {
        return crfModel;
    }

    /**
     * @param crfModel the crfModel to set
     */
    public void setCrfModel(CRF crfModel) {
        this.crfModel = crfModel;
    }

    /**
     * @return the featureGen
     */
    public FeatureGenerator getFeatureGen() {
        return featureGen;
    }

    /**
     * @param featureGen the featureGen to set
     */
    public void setFeatureGen(FeatureGenerator featureGen) {
        this.featureGen = featureGen;
    }
   

    public void annotate()
    {
        train();
        test();
    }
    
    public void train()
    {
        /* 
         * Initialization:
         * Get the required arguements for the application here.
         * Also, you will need to create a Properties object for arguements to be 
         * passed to the CRF. You do not need to worry about this object, 
         * because there are default values for all the parameters in the CRF package.
         * You may need to pass your own parameters values for tuning the application 
         * performance.
         */

        if(getTrainDataPaths() != null)
            getSSF2ML().init(getTrainDataPaths(), getMLTrainPath(), GlobalProperties.getIntlString("UTF-8"), getMLTrainPath(), getLabelFeature());
        else
            getSSF2ML().init(getTrainDataPath(), getMLTrainPath(), GlobalProperties.getIntlString("UTF-8"), getMLTrainPath(), getLabelFeature());

        getSSF2ML().convert(getFormatType());
        
        setTrainData(getSSF2ML().getMLCorpus());
        
        setNumLabels(getSSF2ML().getLabels().getLabel2IntMapping().countProperties());
        
        try {
            /*
             * There are mainly two phases for a learning application: Training and Testing.
             * Implement two routines for each of the phases and call them appropriately here.
             */
            initTrain();
//            load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void test()
    {
       try {
            getSSF2MLTest().init(getTestDataPath(), getMLTestPath(), GlobalProperties.getIntlString("UTF-8"), getMLTestPath(), getLabelFeature());
            getSSF2MLTest().setLabels(getSSF2ML().getLabels());
            getSSF2MLTest().convert(getFormatType());

            setTestData(getSSF2MLTest().getMLCorpus());
                    
            initTest();

            getML2SSF().init(getTaggedDataPath(), getMLTaggedPath(), GlobalProperties.getIntlString("UTF-8"), getTaggedDataPath(), getLabelFeature());
             /*Removed By Pankaj Soni*/
            //getML2SSF().setMLCorpus(getTestData());
            getML2SSF().setLabels(getSSF2ML().getLabels());

            getML2SSF().convert(getFormatType());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }                
    }

    public void initTrain() throws Exception {
        /*
         * Read the training dataset into an object which implements DataIter 
         * interface(trainData). Each of the training instance is encapsulated in the 
         * object which provides DataSequence interface. The DataIter interface
         * returns object of DataSequence (training instance) in next() routine.
         */

//         trainData.readTagged(trainDataPath, charset);

        /*
         * Once you have loaded the training dataset, you need to allocate objects 
         * for the model to be learned. allocmodel() method does that allocation.
         */
        allocModel();
	
        /*
         * You may need to initTrain some of the feature types class. This training is
         * needed for features which need to learn from the training data for instance
         * dictionary features build generated from the training set.
         */
        ((FeatureGenImpl) getFeatureGen()).train(getTrainData());

        /*
         * Call initTrain routine of the CRF model to initTrain the model using the
         * initTrain data. This routine returns the learned weight for the features.
         */
        double featureWts[] = getCrfModel().train(getTrainData());

        /*
         * You can store the learned model for later use into disk.
         * For this you will have to store features as well as their 
         * corresponding weights.
         */
//        crfModel.write(baseDir+"/learntModels/"+outDir+"/crf", charset);
//        ((FeatureGenImpl) featureGen).write(baseDir+"/learntModels/"+outDir+"/features", charset);

        labels = getSSF2ML().getLabels().getLabel2IntMapping();
        
        labels.save(getBaseDir() + "/labels",getCharset());
        getCrfModel().write(getBaseDir() + "/crf",getCharset());
       ((FeatureGenImpl) getFeatureGen()).write(getBaseDir() +"/features",getCharset());
    }

    public void load() throws Exception {

        labels.read(getBaseDir() + "/labels",getCharset());

        setNumLabels(labels.countProperties());
       
        getSSF2ML().getLabels().setLabels(labels);
        getSSF2ML().getLabels().setRevLabels(labels.getReverse());
        
        allocModel();

        getCrfModel().read(getBaseDir() + "/crf", getCharset());
        ((FeatureGenImpl) getFeatureGen()).read(getBaseDir() + "/features", getCharset());
//        crfModel.read(baseDir+"/learntModels/"+outDir+"/crf", charset);
//        ((FeatureGenImpl) featureGen).read(baseDir+"/learntModels/"+outDir+"/features", charset);
    }

    public void initTest() throws Exception {
        /*
         * Read the initTest dataset. Each of the initTest instance is encapsulated in the
         * object which provides DataSequence interface. 
         */

//        testData = new DefaultCRFData();
//        testData.readSSFPOS(testDataPath, charset);

        /*
         * Once you have loaded the initTest dataset, you need to allocate objects
         * for the model to be learned. allocmodel() method does that allocation.
         * Also, you need to read learned parameters from the disk stored after
         * training. If the model is already available in the memory, then you do 
         * not need to reallocate the model i.e. you can skip the next step in that
         * case.
         */
//        allocModel();
//        featureGen.read(baseDir + "/features", charset);
//        crfModel.read(baseDir + "/crf", charset);
//        featureGen.read(baseDir+"/learntModels/"+outDir+"/features", charset);
//        crfModel.read(baseDir+"/learntModels/"+outDir+"/crf", charset);

        /*
         * Iterate over initTest data set and apply the crf model to each initTest instance.
         */
            
        DataSequence testRecord = null;

        getTestData().startScan();
       
        while(getTestData().hasNext())
        {
            testRecord = getTestData().next();
            /*
             * Now apply CRF model to each initTest instance.
             */
            getCrfModel().apply(testRecord);

            /*
             * The labeled instance have value of the states as labels. 
             * These state values are not labels as supplied during training.
             * To map this state to one of the labels you need to call following
             * method on the labled testRecord.
             */
            /*Removed by Pankaj Soni*/
           // ((FeatureGenImpl) getFeatureGen()).mapStatesToLabels(testRecord);
            /*Added by Pankaj Soni*/
            ((NestedFeatureGenImpl) getFeatureGen()).mapStatesToLabels(testRecord);
        }        
        
        ((DefaultCRFData) getTestData()).saveTagged(getMLTaggedPath(), getCharset());
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
//        if(formatType == MLCorpusConverter.CHUNK_FORMAT || formatType == MLCorpusConverter.CHUNK_FEATURE_FORMAT)
//        {
//	    featureGen = new CRFAnnotationNestedFeatureGen(numLabels, options);
//	    crfModel = new NestedCRFAnnotation(((FeatureGenImpl) featureGen).numStates(), ((FeatureGeneratorNested) featureGen), options);
//        }
//        else
//        {
//            setFeatureGen(new CRFAnnotationFeatureGenerator("noEdge,4-chain,4-parallel,boundary", getNumLabels()));
            setFeatureGen(new CRFAnnotationNestedFeatureGen("noEdge,4-parallel*", getNumLabels()));
//            setFeatureGen(new CRFAnnotationFeatureGenerator("semi-markov", getNumLabels()));
//            setCrfModel(new CRFAnnotation(getNumLabels(), getFeatureGen(), ""));
            /*Removed By Pankaj Soni*/
	    //crfModel = new NestedCRFAnnotation(((FeatureGenImpl) featureGen).numStates(), ((FeatureGeneratorNested) featureGen), options);
            /*Added by Pankaj Soni*/
            setCrfModel(new NestedCRFAnnotation(((FeatureGenImpl) featureGen).numStates(), ((FeatureGeneratorNested) featureGen), options));
    }     

    public static void main(String argv[]) throws Exception {
        CRFAnnotationMain crfAnnotation = new CRFAnnotationMain();
      //crfAnnotation.annotate();
       crfAnnotation.train();

//        crfAnnotation.load();
//        crfAnnotation.test();
    }
}
