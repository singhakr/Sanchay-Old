/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFCorpusImpl;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.common.MLCorpusConverter;
import sanchay.mlearning.common.impl.DefaultMLCorpusConverter;
import sanchay.mlearning.crf.ChunkedDataSequence;
import sanchay.mlearning.crf.DefaultDataSequence;

/**
 *
 * @author H Umesh
 */
public class SSF2Arff extends DefaultMLCorpusConverter implements MLCorpusConverter {

    protected String outputPrefix = ".out.txt";
    protected ArffExtractFeatures arffExtractFeatures;

    public SSF2Arff() {
        super();
    }

    public void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature) {
        super.init(ssfPath, mlPath, cs, opath, labelFeature);

        ssfCorpus = new SSFCorpusImpl(charset);

        try {
            ssfCorpus.read(new File(ssfPath));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mlCorpus = new DefaultArffData();

        arffExtractFeatures = new ArffExtractFeatures();
    }

    public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature) {
        super.init(ssfFiles, mlPath, cs, opath, labelFeature);

        ssfCorpus = new SSFCorpusImpl(charset);

        try {
            ssfCorpus.read(ssfFiles);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mlCorpus = new DefaultArffData();

        arffExtractFeatures = new ArffExtractFeatures();
    }

    protected void convertToTagFormat(boolean featureType) {
        Enumeration enm = ssfCorpus.getStoryKeys();

        int docIndex = 0;

        while (enm.hasMoreElements()) {
            String storyPath = (String) enm.nextElement();

            SSFStory ssfStory = new SSFStoryImpl();

            try {
                ssfStory.readFile(storyPath, charset);

                if (SimpleStoryImpl.getCorpusType(storyPath, charset) != CorpusType.RAW) {
                    String tmpFile = "tmp/tmp-tmp-tmp.abc.xyz";

                    ssfStory.save(tmpFile, charset);

                    ssfStory = new SSFStoryImpl();

                    ssfStory.readFile(tmpFile, charset);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            SSFStory ssfStory = ssfCorpus.getStory(storyPath);

            int scount = ssfStory.countSentences();

            for (int i = 0; i < scount; i++) {
                SSFSentence ssfSentence = ssfStory.getSentence(i);

                DefaultDataSequence mlSentence = new DefaultDataSequence();

                SSFPhrase root = ssfSentence.getRoot();

                List words = root.getAllLeaves();

                int wcount = words.size();

                for (int j = 0; j < wcount; j++) {
                    SSFNode word = (SSFNode) words.get(j);
                    String label = word.getName();

                    if (featureType) {
                        label = word.getAttributeValue(labelFeature);

                        if (label == null) {
                            label = MLClassLabels.UNDEFINED_LABEL;
                        }
                    }

                    String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);

                    int labelIndex = -1;

                    if (labelIndexStr == null) {
                        labelIndex = labels.getLabel2IntMapping().countProperties();
                        labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                        labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
                    } else {
                        labelIndex = Integer.parseInt(labelIndexStr);
                    }

                    if (word.getLexData().length() > 1) {
                        word.setLexData(word.getLexData().replaceAll(":", ""));                    //System.out.println(" Word-"+word.getLexData());
                    }
                    String arffInstance = new String();
                    arffExtractFeatures.init((SSFSentenceImpl) ssfSentence, word, charset);
                    arffExtractFeatures.addArffFeatures();
                    arffInstance = arffExtractFeatures.getArffInstance() + label;
                    System.out.println(arffInstance);
                    //arffInstance;

                    mlSentence.add_x(arffInstance);
                    mlSentence.set_y(j, labelIndex);
                }

                ((DefaultArffData) mlCorpus).add(mlSentence);
            }

            ((DefaultArffData) mlCorpus).addDocumentBoundaries(docIndex, docIndex + scount, storyPath, storyPath + outputPrefix);

            docIndex += scount;
        }

        HashMap attributes = new HashMap();

        String keys[] = new String[arffExtractFeatures.getAttributeNames().length + 1];

        for (int k = 0; k < keys.length - 1; k++) {
            keys[k] = arffExtractFeatures.getAttributeNames()[k];
        }
        keys[arffExtractFeatures.getAttributeNames().length] = "class";

        attributes = arffExtractFeatures.getAttributes();

        attributes.put("class", labels);


        try {
            ((DefaultArffData) mlCorpus).saveArffTagged(outputPath, charset, keys, attributes);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries) {

        Enumeration enm = ssfCorpus.getStoryKeys();

        int docIndex = 0;

        while (enm.hasMoreElements()) {
            String storyPath = (String) enm.nextElement();

            SSFStory ssfStory = new SSFStoryImpl();

            try {
                ssfStory.readFile(storyPath, charset);

                if (SimpleStoryImpl.getCorpusType(storyPath, charset) != CorpusType.RAW) {
                    String tmpFile = "tmp/tmp-tmp-tmp.abc.xyz";

                    ssfStory.save(tmpFile, charset);

                    ssfStory = new SSFStoryImpl();

                    ssfStory.readFile(tmpFile, charset);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            int scount = ssfStory.countSentences();

            for (int i = 0; i < scount; i++) {
                
                SSFSentence ssfSentence = ssfStory.getSentence(i);

                ChunkedDataSequence mlSentence = new ChunkedDataSequence();

                SSFPhrase root = ssfSentence.getRoot();

                int ccount = root.countChildren();

                int wrdPos = 0;
                
                int prevWrdPos = 0;

                for (int j = 0; j < ccount; j++) {
                
                    SSFNode node = root.getChild(j);
                    
                    String label = node.getName();
                    
                    if (featureType) {
                    
                        label = node.getAttributeValue(labelFeature);

                        if (label == null) {
                            label = MLClassLabels.UNDEFINED_LABEL;
                        }
                    }

                    if (preserveChunkBoundaries) {
                        
                     /*   String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);

                        int labelIndex = -1;

                        if (labelIndexStr == null) {
                            labelIndex = labels.getLabel2IntMapping().countProperties();
                            labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                            labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
                        } else {
                            labelIndex = Integer.parseInt(labelIndexStr);
                        }

                        if (node.getLexData().length() > 1) {
                            node.setLexData(node.getLexData().replaceAll(":", ""));
                        }
                        mlSentence.add_x(node);
                        mlSentence.set_y(j, labelIndex);*/
                    }else
                    {
                        int wcount = node.countChildren();
                        int labelIndex = -1;

                        if(wcount <= 0)
                        {
                            label = "O";

                            String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(label);

                            labelIndex = -1;

                            if(labelIndexStr == null)
                            {
                                labelIndex = labels.getLabel2IntMapping().countProperties();
                                labels.getLabel2IntMapping().addProperty(label, "" + labelIndex);
                                labels.getInt2LabelMapping().addProperty("" + labelIndex, label);
                            }
                            else
                            {
                                labelIndex = Integer.parseInt(labelIndexStr);
                            }
                    
                            if(node.getLexData().length() > 1)
                                node.setLexData(node.getLexData().replaceAll(":", ""));
                           
                            String arffInstance = new String();
                            arffExtractFeatures.init((SSFSentenceImpl) ssfSentence, node, charset);
                            arffExtractFeatures.addArffFeatures();
                            arffInstance = arffExtractFeatures.getArffInstance() + label;
                            System.out.println(arffInstance);
                            
                            
                            
                            mlSentence.add_x(arffInstance);
                            mlSentence.set_y(wrdPos, labelIndex);

                            mlSentence.addChunkBoundaries(wrdPos, wrdPos + 1, node);
                            mlSentence.setSegment(wrdPos, wrdPos++, labelIndex);
                    }else {
                            for (int k = 0; k < wcount; k++)
                            {
                                String innerLabel = label;
                                SSFNode innerNode = ((SSFPhrase) node).getChild(k);
    
                                if(k == 0)
                                {
                                    innerLabel = "B-" + innerLabel;
                                } else
                                    innerLabel = "I-" + innerLabel;
                                
                                 String labelIndexStr = labels.getLabel2IntMapping().getPropertyValue(innerLabel);

                                labelIndex = -1;

                                if(labelIndexStr == null)
                                {
                                    labelIndex = labels.getLabel2IntMapping().countProperties();
                                    labels.getLabel2IntMapping().addProperty(innerLabel, "" + labelIndex);
                                    labels.getInt2LabelMapping().addProperty("" + labelIndex, innerLabel);
                                }
                                else
                                {
                                    labelIndex = Integer.parseInt(labelIndexStr);
                                }
                                 if(innerNode.getLexData().length() > 1)
                                    innerNode.setLexData(innerNode.getLexData().replaceAll(":", ""));

                                
                                String arffInstance = new String();
                                arffExtractFeatures.init((SSFSentenceImpl) ssfSentence, innerNode, charset);
                                arffExtractFeatures.addArffFeatures();
                                arffInstance = arffExtractFeatures.getArffInstance() + innerLabel;
                                System.out.println(arffInstance);

                                mlSentence.add_x(arffInstance);
                                mlSentence.set_y(wrdPos++, labelIndex);                                
                                
                            }
                             mlSentence.addChunkBoundaries(prevWrdPos, wrdPos, node);
                             prevWrdPos = wrdPos;                            
                    }
                }
            }
                 ((DefaultArffData) mlCorpus).add(mlSentence);
        }
               ((DefaultArffData) mlCorpus).addDocumentBoundaries(docIndex, docIndex + scount, storyPath, storyPath + outputPrefix);
            docIndex += scount;
    }
        
                HashMap attributes = new HashMap();

        String keys[] = new String[arffExtractFeatures.getAttributeNames().length + 1];

        for (int k = 0; k < keys.length - 1; k++) {
            keys[k] = arffExtractFeatures.getAttributeNames()[k];
        }
        keys[arffExtractFeatures.getAttributeNames().length] = "class";

        attributes = arffExtractFeatures.getAttributes();

        attributes.put("class", labels);
        
        try {
            ((DefaultArffData) mlCorpus).saveArffTagged(outputPath, charset, keys, attributes);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String args[]) {
        SSF2Arff ssf2arff = new SSF2Arff();
        ssf2arff.init("/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/shallow-parsing-annotated-data/SmallFile2.txt.out.txt",
                "/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/Blank/Chunking/output.arff",
                "UTF-8",
                "/media/disk/Users/53590/Desktop/IIIT_H/CORPUS/Blank/Chunking/output3.arff",
                "ne");
        ssf2arff.convert(CHUNK_FORMAT);
    }
}

