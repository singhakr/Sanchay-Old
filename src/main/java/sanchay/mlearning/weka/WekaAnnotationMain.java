/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.weka;

import sanchay.formats.converters.Arff2SSF;
import sanchay.formats.converters.SSF2Arff;
import sanchay.mlearning.maxent.WekaNERExperiment;
import iitb.utils.Options;
import sanchay.annotation.DefaultAnnotationMain;
import sanchay.annotation.SanchayAnnotationMain;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author anil
 */
public class WekaAnnotationMain extends DefaultAnnotationMain implements SanchayAnnotationMain {

    protected  String classifier;

    final int LAZY_IBK = 0;
    final int TREES_J48 = 1;
    final int NAIVE_BAYES = 2;
    final int ONE_R = 3;
    final int SMO = 4;
    final int LOGISTIC = 5;
    final int ADA_BOOST = 6;
    final int LOGIT_BOOST = 7;
    final int DECISION_STUMPS = 8;
    final int RULES_PART = 9;
    
    public WekaAnnotationMain() {

        labels = new KeyValueProperties(0, 10);

    	options = new Options();
        
        ssf2ML = new SSF2Arff();
        ml2SSF = new Arff2SSF();
        ssf2MLTest = new SSF2Arff();

        options.setProperty("MaxMemory", "10");
        
        classifier = new String();
        setClassifier(DECISION_STUMPS);
    }

    public WekaAnnotationMain(int type) {
        this();

        formatType = type;
    }
    
    public void annotate()
    {
        train();
        test();
    }

    public void train()
    {
        if(getTrainDataPaths() != null)
            getSSF2ML().init(getTrainDataPaths(), getMLTrainPath(), getCharset(), getMLTrainPath(), getLabelFeature());
        else
            getSSF2ML().init(getTrainDataPath(), getMLTrainPath(), getCharset(), getMLTrainPath(), getLabelFeature());

        getSSF2ML().convert(getFormatType());
        
        setTrainData(getSSF2ML().getMLCorpus());
    }

    public void test()
    {

        try {
            getSSF2MLTest().init(getTestDataPath(), getMLTestPath(),getCharset(), getMLTestPath(), getLabelFeature());
            //getSSF2MLTest().setLabels(getSSF2ML().getLabels());
            getSSF2MLTest().convert(getFormatType());
            
            setTestData(getSSF2MLTest().getMLCorpus());
                    
            initTest();           
            
            //getML2SSF().setMLCorpus(getTestData());
            getML2SSF().init( getTestDataPath(),getMLTestPath()+".out.arff", getCharset(), getTestDataPath()+".out.utf8", getLabelFeature());
            
            //getML2SSF().setLabels(getSSF2ML().getLabels());

            getML2SSF().convert(getFormatType());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }                
    }

    public void initTrain() throws Exception {

    }

    public void load() throws Exception {

    }

    public void initTest() throws Exception {
                
        WekaNERExperiment wekaNERExperiment = new WekaNERExperiment(getMLTrainPath(),getMLTestPath(), getCharset() );
        wekaNERExperiment.init( getClassifier(), getWekaOptions());
        wekaNERExperiment.initExperiment1();
        wekaNERExperiment.runExperiment();
        
    }

    public String[] getWekaOptions()
    {
        String Options[];
        
        return null;
    }
    
    public String getClassifier()
    {
        return classifier;
    }
    
    public void setClassifier( int classifier )
    {
        switch( classifier ){
            case ADA_BOOST:
                this.classifier = "weka.classifiers.meta.AdaBoostM1";
                break;
            case DECISION_STUMPS:
                this.classifier = "weka.classifiers.trees.DecisionStump";
                break;
            case LAZY_IBK:
                this.classifier = "weka.classifiers.lazy.IBk";
                break;
            case LOGISTIC:
                this.classifier = "weka.classifiers.functions.Logistic";
                break;
            case LOGIT_BOOST:
                this.classifier = "weka.classifiers.meta.LogitBoost";
                break;
            case NAIVE_BAYES:
                this.classifier = "weka.classifiers.bayes.NaiveBayes";
                break;
            case ONE_R:
                this.classifier = "weka.classifiers.rules.OneR";
                break;
            case SMO:
                this.classifier = "weka.classifiers.functions.SMO";
                break;
            case TREES_J48:
                this.classifier = "weka.classifiers.trees.J48";
                break;
            case RULES_PART:
                this.classifier = "weka.classifiers.rules.PART";
                break;
        }
        
    }
    public static void main(String argv[]) throws Exception {
        WekaAnnotationMain wekaAnnotation = new WekaAnnotationMain();
        wekaAnnotation.annotate();
        //wekaAnnotation.train();
        //wekaAnnotation.test();
    }
}
