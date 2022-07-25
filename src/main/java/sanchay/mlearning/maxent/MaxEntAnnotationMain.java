/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.maxent;

import sanchay.formats.converters.SSF2MaxEnt;
import sanchay.formats.converters.MaxEnt2SSF;
import iitb.utils.Options;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.chunker.ChunkSampleStream;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.model.BaseModel;
import sanchay.annotation.DefaultAnnotationMain;
import sanchay.annotation.SanchayAnnotationMain;
import sanchay.properties.KeyValueProperties;
import sanchay.corpus.ssf.impl.*;
import sanchay.mlearning.common.MLCorpusConverter;

/**
 *
 * @author Anil Kumar Singh
 */
public class MaxEntAnnotationMain extends DefaultAnnotationMain implements SanchayAnnotationMain {

    private String language;
    private String encoding;
    
    private BaseModel baseModel;
    private ObjectStream sampleStream;
    
    private POSTaggerME maxEntPOSTagger;
    private ChunkerME maxEntChunker;

    public MaxEntAnnotationMain() {

        labels = new KeyValueProperties(0, 10);

        ssf2ML = new SSF2MaxEnt();
        ml2SSF = new MaxEnt2SSF();
        ssf2MLTest = new SSF2MaxEnt();

        options = new Options();

        options.setProperty("MaxMemory", "10");
    }

    public MaxEntAnnotationMain(int type) {
        this();

        formatType = type;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return the baseModel
     */
    public BaseModel getBaseModel() {
        return baseModel;
    }

    /**
     * @param baseModel the baseModel to set
     */
    public void setBaseModel(BaseModel baseModel) {
        this.baseModel = baseModel;
    }

    /**
     * @return the sampleStream
     */
    public ObjectStream getSampleStream() {
        return sampleStream;
    }

    /**
     * @param sampleStream the sampleStream to set
     */
    public void setSampleStream(ObjectStream sampleStream) {
        this.sampleStream = sampleStream;
    }

    /**
     * @return the maxEntPOSTagger
     */
    public POSTaggerME getMaxEntPOSTagger() {
        return maxEntPOSTagger;
    }

    /**
     * @param maxEntPOSTagger the maxEntPOSTagger to set
     */
    public void setMaxEntPOSTagger(POSTaggerME maxEntPOSTagger) {
        this.maxEntPOSTagger = maxEntPOSTagger;
    }

    /**
     * @return the maxEntChunker
     */
    public ChunkerME getMaxEntChunker() {
        return maxEntChunker;
    }

    /**
     * @param maxEntChunker the maxEntChunker to set
     */
    public void setMaxEntChunker(ChunkerME maxEntChunker) {
        this.maxEntChunker = maxEntChunker;
    }

    public void annotate() {
        try {
            train();
            test();
        } catch (Exception ex) {
            Logger.getLogger(MaxEntAnnotationMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void train() {
        if (getTrainDataPaths() != null) {
            getSSF2ML().init(getTrainDataPaths(), getMLTrainPath(), "UTF-8", getMLTrainPath(), getLabelFeature());
        } else {
            getSSF2ML().init(getTrainDataPath(), getMLTrainPath(), "UTF-8", getMLTrainPath(), getLabelFeature());
        }

        getSSF2ML().convert(getFormatType());
        getSSF2ML().saveFeatureLabels();

        setTrainData(getSSF2ML().getMLCorpus());

        setNumLabels(getSSF2ML().getLabels().getLabel2IntMapping().countProperties());

        try {
            /*
             * There are mainly two phases for a learning application: Training and Testing.
             * Implement two routines for each of the phases and call them appropriately here.
             */
            initTrain();
            load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void test() {
        try {
            initTest();

            getML2SSF().init(getTaggedDataPath(), getMLTaggedPath(), "UTF-8", getTaggedDataPath(), getLabelFeature());

            DefaultMaxEntData mlCorpus = new DefaultMaxEntData();

            if (getFormatType() == MLCorpusConverter.TAG_FORMAT) {
                ((DefaultMaxEntData) mlCorpus).readTagged(getMLTaggedPath(), "UTF-8");
            } else if (getFormatType() == MLCorpusConverter.CHUNK_FORMAT) {
                ((DefaultMaxEntData) mlCorpus).readSSFChunk(getMLTaggedPath(), "UTF-8");
            } else if (getFormatType() == MLCorpusConverter.CHUNK_FEATURE_FORMAT) {
                ((DefaultMaxEntData) mlCorpus).readSSFFeature(getMLTaggedPath(), "UTF-8", labelFeature);
            }

            getML2SSF().setMLCorpus(mlCorpus);

            getML2SSF().setLabels(getSSF2ML().getLabels());

            getML2SSF().convert(getFormatType());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initTrain() throws Exception {
        if (getFormatType() == MLCorpusConverter.TAG_FORMAT) {
            
            sampleStream = null;
//            sampleStream = new WordTagSampleStream((new InputStreamReader(new FileInputStream(getMLTrainPath()), "UTF-8")));
            
            setBaseModel(POSTaggerME.train(language, sampleStream, null, null));

        } else if (getFormatType() == MLCorpusConverter.CHUNK_FORMAT) {

            sampleStream = null;
//            sampleStream = new ChunkSampleStream(new PlainTextByLineStream((new InputStreamReader(new FileInputStream(getMLTrainPath())))));
            
//            setBaseModel(ChunkerME.train(language, sampleStream, null, null));

            maxEntPOSTagger = new POSTaggerME((POSModel) baseModel);
        }

        labels = getSSF2ML().getLabels().getLabel2IntMapping();

        labels.save(getBaseDir() + "/labels", getCharset());

        File outputFile = new File(getBaseDir() + "/MaxEnt");

//        SuffixSensitiveGISModelWriter writer = new SuffixSensitiveGISModelWriter(getGISModel(), outputFile);
//        writer.persist();
    }

    public void load() throws Exception {

//        setGISModel(new SuffixSensitiveGISModelReader(new File(getBaseDir() + "/MaxEnt")).getModel());

    }

    public void initTest() throws Exception {

        SSFStoryImpl ssfStory = new SSFStoryImpl();

        ssfStory.readFile(getTestDataPath());

        PrintStream resultOP = new PrintStream(getMLTaggedPath());
//        PrintStream resultOP1 = new PrintStream("C:\\Users\\Sourabh\\Desktop\\Sanchay data\\sourabh.out.utf8");

        String line = new String();

        /*Code for tagging*/
        if (getFormatType() == MLCorpusConverter.TAG_FORMAT) {

            ssfStory.saveRawText(getMLTestPath(), "UTF-8");

            maxEntPOSTagger = new POSTaggerME((POSModel) baseModel);

        } else if (getFormatType() == 1) {

            ssfStory.savePOSTagged(getMLTestPath(), "UTF-8");

            FileInputStream testMLfile = new FileInputStream(getMLTestPath());

            BufferedReader inreader = new BufferedReader(new InputStreamReader(testMLfile));

//            ChunkFeatureGenerator dcg = new ChunkFeatureGenerator();

//            ChunkFeatureGeneratorConfigFile dcg = new ChunkFeatureGeneratorConfigFile();

//            ChunkerME treebankChunker = new ChunkerME(getGISModel(), dcg);
            //PrintStream faltu = new PrintStream("C:\\Users\\Sourabh\\Desktop\\Sanchay data\\faltu.utf8");

            while ((line = inreader.readLine()) != null) {
                if (line.equals("")) {

                    resultOP.println();

                } else {

                    String[] tts = line.split(" ");

                    String[] tokens = new String[tts.length];

                    String[] tags = new String[tts.length];

                    for (int ti = 0, tn = tts.length; ti < tn; ti++) {

                        int si = tts[ti].lastIndexOf("_");

                        tokens[ti] = tts[ti].substring(0, si);

                        tags[ti] = tts[ti].substring(si + 1);
                    }

//                    String[] chunks = treebankChunker.chunk(tokens, tags);
//                    ChunkerME chunker = new ChunkerME(getGISModel(),dcg);
//                    String[] chunks = chunker.chunk(tokens,tags);

                    resultOP.print("[[ ");

                    int chunkBegin = 0;
                    String chunkName = new String();

//                    for (int ci = 0, cn = chunks.length; ci < cn; ci++) {
////                        chunkName = chunks[ci];
////                        resultOP1.println(" "+tokens[ci]+"_"+tags[ci]+"_"+chunkName);
//                        if (ci > 0 && !chunks[ci].startsWith("I-") && !chunks[ci - 1].startsWith("O-")) {
//
//                            if (chunkBegin == 1) {
//                                resultOP.print(" ]]_" + chunkName);
//                                chunkBegin = 0;
//                            }
//                        }
//
//                        if (chunks[ci].startsWith("B-")) {
//                            //System.out.println("here in begining");
//                            chunkBegin = 1;
//                            chunkName = chunks[ci].substring(2);
//
//                            resultOP.print(" [[");
//                        }
//
//                        resultOP.print(" " + tokens[ci] + "_" + tags[ci]);
//                        //faltu.println(chunks[ci]);
//                    }
//
//                    if (!chunks[chunks.length - 1].startsWith("O-")) {
//
//                        if (chunkBegin == 1) {
//
//                            chunkBegin = 0;
//                            resultOP.print(" ]]_" + chunkName);
//                        }
//                    }
//
//                    resultOP.println(" ]]_SSF");
                }

            }

        }

    }

    public static void main(String argv[]) throws Exception {
        MaxEntAnnotationMain maxEntAnnotation = new MaxEntAnnotationMain();
        maxEntAnnotation.train();
//        maxEntAnnotation.annotate();
//        maxEntAnnotation.load();
        maxEntAnnotation.test();
    }
}
