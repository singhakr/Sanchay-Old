/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.annotation;

import iitb.crf.DataIter;
import iitb.utils.Options;
import java.io.File;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class DefaultAnnotationMain implements SanchayAnnotationMain {
    protected int formatType = MLCorpusConverter.TAG_FORMAT;
//    protected int formatType = MLCorpusConverter.CHUNK_FORMAT;

    protected File trainDataPaths[];

    protected String trainDataPath = "data/automatic-annotation/pos-tagging/training/";
    protected String mlTrainPath = "data/automatic-annotation/pos-tagging/ml-format/training-ml.txt";
    protected String mlTestPath = "data/automatic-annotation/pos-tagging/ml-format/test-ml.txt";
    protected String mlTaggedPath = "data/automatic-annotation/pos-tagging/ml-format/tagged-ml.txt";
    protected String testDataPath = "data/automatic-annotation/pos-tagging/testing/testing-1-utf8-ssf.txt";
    protected String taggedDataPath = "data/automatic-annotation/pos-tagging/annotated/testing-1-utf8-ssf.txt";
    protected String baseDir = "data/automatic-annotation/pos-tagging/learntModels";

//    protected String trainDataPath = "data/automatic-annotation/chunking/training/";
//    protected String mlTrainPath = "data/automatic-annotation/chunking/ml-format/training-ml.txt";
//    protected String mlTestPath = "data/automatic-annotation/chunking/ml-format/test-ml.txt";
//    protected String mlTaggedPath = "data/automatic-annotation/chunking/ml-format/tagged-ml.txt";
//    protected String testDataPath = "data/automatic-annotation/chunking/testing/testing-1-utf8-ssf.txt";
//    protected String taggedDataPath = "data/automatic-annotation/chunking/annotated/testing-1-utf8-ssf.txt";
//    protected String baseDir = "data/automatic-annotation/chunking/learntModels";

//    protected String trainDataPath = "data/automatic-annotation/ner/training/";
//    protected String mlTrainPath = "data/automatic-annotation/ner/ml-format/training-ml.txt";
//    protected String mlTestPath = "data/automatic-annotation/ner/ml-format/test-ml.txt";
//    protected String mlTaggedPath = "data/automatic-annotation/ner/ml-format/tagged-ml.txt";
//    protected String testDataPath = "data/automatic-annotation/ner/testing/testing-1-utf8-ssf.txt";
//    protected String taggedDataPath = "data/automatic-annotation/ner/annotated/testing-1-utf8-ssf.txt";
//    protected String baseDir = "data/automatic-annotation/ner/learntModels";

    protected DataIter trainData;
    protected DataIter testData;

    protected DefaultMLCorpusConverter ssf2ML;
    protected DefaultMLCorpusConverter ml2SSF;
    protected DefaultMLCorpusConverter ssf2MLTest;

    protected String labelFeature = "ne";
    protected String charset = "UTF-8";

//    String outDir = "trial";
    protected int numLabels = 7;
    protected Options options;

    protected KeyValueProperties labels;

    /**
     * @return the formatType
     */
    public int getFormatType() {
        return formatType;
    }

    /**
     * @param formatType the formatType to set
     */
    public void setFormatType(int formatType) {
        this.formatType = formatType;
    }

    /**
     * @return the trainDataPath
     */
    public String getTrainDataPath() {
        return trainDataPath;
    }

    /**
     * @param trainDataPath the trainDataPath to set
     */
    public void setTrainDataPath(String trainDataPath) {
        this.trainDataPath = trainDataPath;
    }

    /**
     * @return the trainDataPath
     */
    public File[] getTrainDataPaths() {
        return trainDataPaths;
    }

    /**
     * @param trainDataPaths the trainDataPaths to set
     */
    public void setTrainDataPaths(File trainDataPaths[]) {
        this.trainDataPaths = trainDataPaths;
    }
    /**
     * @return the crfTrainPath
     */
    public String getMLTrainPath() {
        return mlTrainPath;
    }

    /**
     * @param crfTrainPath the crfTrainPath to set
     */
    public void setMLTrainPath(String mlTrainPath) {
        this.mlTrainPath = mlTrainPath;
    }

    /**
     * @return the crfTestPath
     */
    public String getMLTestPath() {
        return mlTestPath;
    }

    /**
     * @param crfTestPath the crfTestPath to set
     */
    public void setMLTestPath(String mlTestPath) {
        this.mlTestPath = mlTestPath;
    }

    /**
     * @return the crfTaggedPath
     */
    public String getMLTaggedPath() {
        return mlTaggedPath;
    }

    /**
     * @param crfTaggedPath the crfTaggedPath to set
     */
    public void setMLTaggedPath(String mlTaggedPath) {
        this.mlTaggedPath = mlTaggedPath;
    }

    /**
     * @return the testDataPath
     */
    public String getTestDataPath() {
        return testDataPath;
    }

    /**
     * @param testDataPath the testDataPath to set
     */
    public void setTestDataPath(String testDataPath) {
        this.testDataPath = testDataPath;
    }

    /**
     * @return the taggedDataPath
     */
    public String getTaggedDataPath() {
        return taggedDataPath;
    }

    /**
     * @param taggedDataPath the taggedDataPath to set
     */
    public void setTaggedDataPath(String taggedDataPath) {
        this.taggedDataPath = taggedDataPath;
    }

    /**
     * @return the baseDir
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir the baseDir to set
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
    /**
     * @return the trainData
     */
    public DataIter getTrainData() {
        return trainData;
    }

    /**
     * @param trainData the trainData to set
     */
    public void setTrainData(DataIter trainData) {
        this.trainData = trainData;
    }

    /**
     * @return the testData
     */
    public DataIter getTestData() {
        return testData;
    }

    /**
     * @param testData the testData to set
     */
    public void setTestData(DataIter testData) {
        this.testData = testData;
    }

    /**
     * @return the ssf2CRF
     */
    public DefaultMLCorpusConverter getSSF2ML() {
        return ssf2ML;
    }

    /**
     * @param ssf2CRF the ssf2CRF to set
     */
    public void setSSF2ML(DefaultMLCorpusConverter ssf2ML) {
        this.ssf2ML = ssf2ML;
    }

    /**
     * @return the crf2SSF
     */
    public DefaultMLCorpusConverter getML2SSF() {
        return ml2SSF;
    }

    /**
     * @param crf2SSF the crf2SSF to set
     */
    public void setML2SSF(DefaultMLCorpusConverter ml2SSF) {
        this.ml2SSF = ml2SSF;
    }

    /**
     * @return the ssf2CRFTest
     */
    public DefaultMLCorpusConverter getSSF2MLTest() {
        return ssf2MLTest;
    }

    /**
     * @param ssf2CRFTest the ssf2CRFTest to set
     */
    public void setSSF2MLTest(DefaultMLCorpusConverter ssf2MLTest) {
        this.ssf2MLTest = ssf2MLTest;
    }


    /**
     * @return the labelFeature
     */
    public String getLabelFeature() {
        return labelFeature;
    }

    /**
     * @param labelFeature the labelFeature to set
     */
    public void setLabelFeature(String labelFeature) {
        this.labelFeature = labelFeature;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return the numLabels
     */
    public int getNumLabels() {
        return numLabels;
    }

    /**
     * @param numLabels the numLabels to set
     */
    public void setNumLabels(int numLabels) {
        this.numLabels = numLabels;
    }

    /**
     * @return the options
     */
    public Options getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(Options options) {
        this.options = options;
    }

    public void annotate()
    {
        train();
        test();
    }

    public void train()
    {

    }

    public void test()
    {

    }

    public void initTrain() throws Exception {

    }

    public void load() throws Exception {

    }

    public void initTest() throws Exception {

    }
}
