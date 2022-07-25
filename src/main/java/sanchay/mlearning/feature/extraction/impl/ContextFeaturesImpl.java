/*
 * ContextFeaturesImpl.java
 *
 * Created on June 20, 2008, 5:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package sanchay.mlearning.feature.extraction.impl;

import java.util.ArrayList;
import java.util.List;
import sanchay.mlearning.feature.extraction.*;
import sanchay.corpus.ssf.*;
import sanchay.corpus.ssf.tree.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class ContextFeaturesImpl implements ContextFeatures {

    /**
     * Creates a new instance of ContextFeaturesImpl
     */
    public ContextFeaturesImpl() {
    }

    public List getwordContext(SSFSentence ssfSentence, int wordIndex, int windowSize, int minFreq, String str, int direction) {
        int i;
        int count;
        SSFNode rootNode;
        List contextString = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        count = childVector.size();


        if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if (wordIndex >= windowSize) {
                for (i = 1; i <= windowSize; i++) {
                    String feature = (String) childVector.get(wordIndex - i);
                    if (feature.equals(str)) {
                        contextString.add(feature);
                    }
                }
            } else {
                for (i = 1; i < wordIndex; i++) {
                    String feature = (String) childVector.get(wordIndex - i);
                    if (feature.equals(str)) {
                        contextString.add(feature);
                    }
                }
            }
        } else if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if (count - wordIndex >= windowSize) {
                for (i = 1; i <= windowSize; i++) {
                    String feature = (String) childVector.get(wordIndex + i);
                    if (feature.equals(str)) {
                        contextString.add(feature);
                    }

                }
            } else {
                for (i = 1; i <= count - wordIndex; i++) {
                    String feature = (String) childVector.get(wordIndex + i);
                    if (feature.equals(str)) {
                        contextString.add(feature);
                    }
                }
            }
        }

        return contextString;
    }

    public List gettagContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, int direction) {
        
        int i;
        int count;
        SSFNode rootNode;
        SSFNode ssfNode;
        List tagcontextString = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        count = childVector.size();


        if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if (wordIndex >= windowSize) {
                for (i = 1; i <= windowSize; i++) {
                    String feature = (String) ((SSFNode) childVector.get(wordIndex - i)).getName();
                    if (feature.equals(tag)) {
                        tagcontextString.add(feature);
                    }

                }
            } else {
                for (i = 1; i < wordIndex; i++) {

                    String feature = (String) ((SSFNode) childVector.get(wordIndex - i)).getName();
                    if (feature.equals(tag)) {
                        tagcontextString.add(feature);
                    }
                }
            }
        } else if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if (count - wordIndex >= windowSize) {
                for (i = 1; i <= windowSize; i++) {
                    String feature = (String) ((SSFNode) childVector.get(wordIndex + i)).getName();
                    if (feature.equals(tag)) {
                        tagcontextString.add(feature);
                    }
                }
            } else {

                for (i = 1; i <= count - wordIndex; i++) {
                    String feature = (String) ((SSFNode) childVector.get(wordIndex + i)).getName();
                    if (feature.equals(tag)) {
                        tagcontextString.add(feature);
                    }

                }
            }
        }
        return tagcontextString;

    }

    @Override
    public List getContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, int minfreq) {
        return null;

    }

    public List getwordtagContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, String str, int direction) {

        int i;
        int count;
        SSFNode rootNode;
        SSFNode ssfNode;
        List tagwordString = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        count = childVector.size();


        if (direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if (wordIndex >= windowSize) {
                for (i = 1; i <= windowSize; i++) {
                    String featureWord = (String) childVector.get(wordIndex - i);
                    String featureTag = (String) ((SSFNode) childVector.get(wordIndex - i)).getName();
                    if (featureWord.equals(str) && featureTag.equals(tag)) {
                        tagwordString.add(featureWord);
                        tagwordString.add(featureTag);
                    }

                }
            } else {
                for (i = 1; i < wordIndex; i++) {
                    String featureWord = (String) childVector.get(wordIndex - i);
                    String featureTag = (String) ((SSFNode) childVector.get(wordIndex - i)).getName();
                    if (featureWord.equals(str) && featureTag.equals(tag)) {
                        tagwordString.add(featureWord);
                        tagwordString.add(featureTag);
                    }
                }
            }
        } else if (direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if (count - wordIndex >= windowSize) {
                for (i = 1; i <= windowSize; i++) {
                    String featureWord = (String) childVector.get(wordIndex + i);
                    String featureTag = (String) ((SSFNode) childVector.get(wordIndex + i)).getName();
                    if (featureWord.equals(str) && featureTag.equals(tag)) {
                        tagwordString.add(featureWord);
                        tagwordString.add(featureTag);
                    }
                }
            } else {

                for (i = 1; i <= count - wordIndex; i++) {
                    String featureWord = (String) childVector.get(wordIndex + i);
                    String featureTag = (String) ((SSFNode) childVector.get(wordIndex + i)).getName();
                    if (featureWord.equals(str) && featureTag.equals(tag)) {
                        tagwordString.add(featureWord);
                        tagwordString.add(featureTag);
                    }

                }
            }
        }
        return tagwordString;



    }

    public List getContext(SSFSentence ssfSentence, int wordIndex, int windowSize, String tag, String str, int minFreq) {
        return null;

    }
    /*
     public boolean isFirstWordOfSentence(String str)
     {
    
     }
     public boolean isLastWordOfSentence(String str)
     {
    
     }
     public String DynamicLeftTagFeature(SSFNode ssfNode)
     {
    
     }
     public String DynamicRightTagFeature(SSFNode ssfNode)
     {
    
     }
     */
}
