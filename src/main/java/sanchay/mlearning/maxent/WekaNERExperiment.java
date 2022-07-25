/*
 * WekaNERExperiment.java
 *
 * Created on June 30, 2008, 12:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.maxent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import sanchay.GlobalProperties;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author User
 */
public class WekaNERExperiment {
    
    String charset;
    String trainingFile;
    String testingFile;
    
    Instances trainInstances;
    Instances testInstances;
    
    StringToWordVector filter;

    Instances trainInstancesNew;
    Instances testInstancesNew;        
    
    Evaluation eval;
    Classifier classifier;
    
    /** Creates a new instance of WekaNERExperiment */
    public WekaNERExperiment(String trainingFile, String testingFile, String charset) {
        this.trainingFile = trainingFile;
        this.testingFile = testingFile;
        this.charset = charset;
    }

    public void initExperiment() throws IOException,  Exception 
    {
        BufferedReader trainReader = null;
        BufferedReader testReader = null;
//        PrintStream ps = new PrintStream(outFile, cs);
        
        if(charset != null && charset.equals("") == false) {
            trainReader = new BufferedReader(new InputStreamReader(new FileInputStream(trainingFile), charset));
            testReader = new BufferedReader(new InputStreamReader(new FileInputStream(testingFile), charset));
        } else {
            trainReader = new BufferedReader(new InputStreamReader(new FileInputStream(trainingFile)));
            testReader = new BufferedReader(new InputStreamReader(new FileInputStream(testingFile), charset));
        }
        
        trainInstances = new Instances(trainReader);
        trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
        
        testInstances = new Instances(testReader);
        testInstances.setClassIndex(testInstances.numAttributes() - 1);
        
        StringToWordVector filter = new StringToWordVector();
        
        filter.setInputFormat(trainInstances);
        
        trainInstancesNew = Filter.useFilter(trainInstances, filter);
        
//        trainInstancesNew.
        
        testInstancesNew = Filter.useFilter(testInstances, filter);        
    }
    
    
    public void initExperiment1() throws IOException,  Exception 
    {
        
        System.out.println(GlobalProperties.getIntlString("Initialising..."));
        BufferedReader trainReader = null;
        BufferedReader testReader = null;
//        PrintStream ps = new PrintStream(outFile, cs);
        
        if(charset != null && charset.equals("") == false) {
            trainReader = new BufferedReader(new InputStreamReader(new FileInputStream(trainingFile), charset));
            testReader = new BufferedReader(new InputStreamReader(new FileInputStream(testingFile), charset));
        } else {
            trainReader = new BufferedReader(new InputStreamReader(new FileInputStream(trainingFile)));
            testReader = new BufferedReader(new InputStreamReader(new FileInputStream(testingFile), charset));
        }
        
        trainInstances = new Instances(trainReader);
        trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
        
        testInstances = new Instances(testReader);
        testInstances.setClassIndex(testInstances.numAttributes() - 1);
        
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(trainInstances);
        
        trainInstancesNew = Filter.useFilter(trainInstances, filter);        
       
        
    }
    
    public void runExperiment(String classifierName, String[] options) throws Exception {
        
      
//        classifier = Classifier.forName(classifierName, options);
//        
//        classifier.buildClassifier(trainInstancesNew);
//      
//        
//        eval = new Evaluation(trainInstancesNew);
//        
//      eval.crossValidateModel(classifier, trainInstancesNew, 10, new Random(1));
//        
//        //eval.evaluateModel(classifier, testInstancesNew);   
//        
//        System.out.println(eval.toSummaryString(true));
//        System.out.println(eval.toClassDetailsString());
//        //result = eval.evaluateModelOnceAndRecordPrediction(classifier, testInstances);
//   //     Vector predict = eval.predictions();
//     //   for (int i = 0; i < predict.size(); i++) {
//            
//       //     System.out.println(predict.add(i));
//            
//      //  }
//       // System.out.println(eval.predictions());
    }

    public void runExperiment1(String classifierName, String[] options) throws Exception
    {
        
//        System.out.println(GlobalProperties.getIntlString("Classifying..."));
//        double clsLabel;
//        
//        // this is to create a copy
//        
//        Instances labeledTestInstances = new Instances(testInstances);
//
//        classifier = Classifier.forName(classifierName, options);
//        
//        classifier.buildClassifier(trainInstancesNew);
//
//        //this is to label instances
//        for (int i = 0; i < testInstances.numInstances(); i++) {
//            
//            clsLabel = classifier.classifyInstance(testInstances.instance(i));
//            labeledTestInstances.instance(i).setClassValue(clsLabel);
//        }
//        
//        BufferedWriter writer = new BufferedWriter(new FileWriter(testingFile + ".out.arff"));
//        writer.write(labeledTestInstances.toString());
//        writer.newLine();
//        writer.flush();
//        writer.close();        
    }
    
    protected String trainPath = new String("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/Blank/Chunking/output1.arff");
    protected String testPath = new String("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/Blank/Chunking/output2.arff");
    protected String wekaClassifier = new String();
    protected String [] options;    
    
    public void init(String weka_classifier, String[] options)
    {        
        wekaClassifier =  weka_classifier;        
        this.options = options;
    }
    
    
    public void runExperiment() throws Exception
    {
//      runExperiment1(wekaClassifier, options);   
    }
    
    
    public static void main(String[] args)
    {
        WekaNERExperiment wekaNERExperiment = new WekaNERExperiment("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/Blank/Chunking/output1.arff",
                "/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/Blank/Chunking/output2.arff", GlobalProperties.getIntlString("UTF-8"));

//        String classifiers[] = new String[]
//        {
//            "trees.ADTree", "", "", "", ""
//       };        
//       
//        String options[][] = new String[][]
//        {
//            new String[] {"-n", "", "", "", ""},
//            new String[] {"-n", "", "", "", ""},
//            new String[] {"-n", "", "", "", ""},
//            new String[] {"-n", "", "", "", ""},
//            new String[] {"-n", " ", "", "", ""}
//        };
        
        String classifiers[] = new String[] {"weka.classifiers.rules.OneR"};
        
        String options[][] = new String[][]
        {
          new String[] {"-n"}
        };

        for (int i = 0; i < classifiers.length; i++)
        {
            try {
                System.out.println(GlobalProperties.getIntlString("Initializing_the_instances..."));
                
//                wekaNERExperiment.initExperiment();
                wekaNERExperiment.initExperiment1();

                System.out.println(GlobalProperties.getIntlString("Classifying_the_instances..."));
                
              
//                wekaNERExperiment.runExperiment(classifiers[i], options[0]);
//                wekaNERExperiment.runExperiment(classifiers[i], null);
//                wekaNERExperiment.runExperiment1(classifiers[i], null);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getTrainPath() {
        return trainPath;
    }

    public void setTrainPath(String trainPath) {
        this.trainPath = trainPath;
    }

    public String getTestPath() {
        return testPath;
    }

    public void setTestPath(String testPath) {
        this.testPath = testPath;
    }

    public String getWekaClassifier() {
        return wekaClassifier;
    }

    public void setWekaClassifier(String wekaClassifier) {
        this.wekaClassifier = wekaClassifier;
    }
}
