/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.annotation;

import iitb.crf.DataIter;
import sanchay.mlearning.crf.*;
import iitb.utils.Options;
import java.io.File;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;

/**
 *
 * @author Anil Kumar Singh
 */
public interface SanchayAnnotationMain {

    void annotate();

    /**
     * @return the baseDir
     */
    String getBaseDir();

    /**
     * @return the charset
     */
    String getCharset();

    /**
     * @return the formatType
     */
    int getFormatType();

    /**
     * @return the labelFeature
     */
    String getLabelFeature();

    /**
     * @return the numLabels
     */
    int getNumLabels();

    /**
     * @return the options
     */
    Options getOptions();

    /**
     * @return the taggedDataPath
     */
    String getTaggedDataPath();

    /**
     * @return the testData
     */
    DataIter getTestData();

    /**
     * @return the testDataPath
     */
    String getTestDataPath();

    /**
     * @return the trainData
     */
    DataIter getTrainData();

    /**
     * @return the trainDataPath
     */
    String getTrainDataPath();

    /**
     * @return the trainDataPath
     */
    File[] getTrainDataPaths();

    void initTest() throws Exception;

    void initTrain() throws Exception;

    void load() throws Exception;

    /**
     * @param baseDir the baseDir to set
     */
    void setBaseDir(String baseDir);

    /**
     * @param charset the charset to set
     */
    void setCharset(String charset);

    /**
     * @param formatType the formatType to set
     */
    void setFormatType(int formatType);

    /**
     * @param labelFeature the labelFeature to set
     */
    void setLabelFeature(String labelFeature);

    /**
     * @param numLabels the numLabels to set
     */
    void setNumLabels(int numLabels);

    /**
     * @param options the options to set
     */
    void setOptions(Options options);

    /**
     * @param taggedDataPath the taggedDataPath to set
     */
    void setTaggedDataPath(String taggedDataPath);

    /**
     * @param testData the testData to set
     */
    void setTestData(DataIter testData);

    /**
     * @param testDataPath the testDataPath to set
     */
    void setTestDataPath(String testDataPath);

    DefaultMLCorpusConverter getSSF2ML();

    void setSSF2ML(DefaultMLCorpusConverter ssf2ML);

    DefaultMLCorpusConverter getML2SSF();

    void setML2SSF(DefaultMLCorpusConverter ml2SSF);

    DefaultMLCorpusConverter getSSF2MLTest();

    void setSSF2MLTest(DefaultMLCorpusConverter ssf2MLTest);


    /**
     * @param trainData the trainData to set
     */
    void setTrainData(DataIter trainData);

    /**
     * @param trainDataPath the trainDataPath to set
     */
    void setTrainDataPath(String trainDataPath);

    /**
     * @param trainDataPaths the trainDataPaths to set
     */
    void setTrainDataPaths(File[] trainDataPaths);

    String getMLTrainPath();

    void setMLTrainPath(String mlTrainPath);

    String getMLTestPath();

    void setMLTestPath(String mlTestPath);

    String getMLTaggedPath();

    void setMLTaggedPath(String mlTaggedPath);

    void test();

    void train();

}
