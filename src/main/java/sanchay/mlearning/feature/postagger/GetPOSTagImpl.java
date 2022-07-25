/*
 * getPOSTagImpl.java
 *
 * Created on September 5, 2008, 12:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.feature.postagger;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class GetPOSTagImpl implements GetPOSTag {
    
    /** Creates a new instance of getPOSTagImpl *
     */
    
       public List getPOSTag(SSFSentence ssfSentence, int wordIndex, int windowSize, int direction ,int bool) {
        int i;
        int count;
        SSFNode rootNode;
        String NULL = "?";
        String posTag;
        List tagString = new ArrayList();
        rootNode = ssfSentence.getRoot();
        List childVector = new ArrayList();
        childVector = rootNode.getAllLeaves();
        count = childVector.size();
        
        
        if(direction == WINDOW_DIRECTION_LEFT || direction == WINDOW_DIRECTION_BOTH) {
            if(wordIndex >= windowSize) {
                for(i = 0; i < windowSize; i++) {
                    SSFNode feature = (SSFNode) childVector.get(wordIndex -  (i + 1));
                    posTag = feature.getName();
                    tagString.add(posTag);
                }
            } 
            else {
                for(i = 0; i < wordIndex; i++) {
                    SSFNode feature = (SSFNode) childVector.get(wordIndex - (i + 1));
                    posTag = feature.getName();
                    tagString.add(feature);
                }

                while(i < windowSize) {
                    SSFNode temp = new SSFNode() ;
                    temp.setLexData(NULL);
                    temp.setName(NULL);


                    tagString.add(temp);
                    i++;
                }
            }
        }
        
        if(direction == WINDOW_DIRECTION_RIGHT || direction == WINDOW_DIRECTION_BOTH) {
            if(count - (wordIndex + 1) > windowSize) {
                for(i = 0; i < windowSize; i++) {
                    SSFNode feature = (SSFNode) childVector.get( (wordIndex + 1) + i);
                    posTag = feature.getName();
                    tagString.add(posTag);
                    
                }
            } else {
                for(i = 0; i < count - (wordIndex + 1); i++) {
                    SSFNode feature = (SSFNode) childVector.get((wordIndex + 1) + i);
                    posTag = feature.getName();
                    tagString.add(posTag);
                }
                while(i < windowSize){
                    
                    SSFNode temp = new SSFNode() ;
                     temp.setLexData(NULL);
                     temp.setName(NULL);
               
                     tagString.add(temp);
                     i++;
              }
            }
        }
        
        return tagString;
    }
       
       
       public void printPOSTag( SSFStory ssfStory, PrintStream ps )
       {
           SSFSentence ssfSentence;
           int senCount = ssfStory.countSentences();
           for (int i = 0; i < senCount; i++) {
               ssfSentence = ssfStory.getSentence(i);
               List POSTag =  ssfSentence.getRoot().getAllChildren();
               int nodeCount = POSTag.size();
               for (int j = 0; j < nodeCount; j++) {
                   
                   List POSTagFeature = getPOSTag(ssfSentence, j, 3,  WINDOW_DIRECTION_BOTH , 1);
                   int count = POSTagFeature.size();
                   for (int k = 0; k < count ; k++) {
                       ps.print( POSTagFeature.get(k).toString() + " ");
                       
                   }
                   ps.println();
                   
                   
               }
                
               
               
               
           }
         
       
       
       }
       
       
       public static void main(String args[]) throws Exception{
           
           
           int senCount;
           GetPOSTagImpl obj = new GetPOSTagImpl();
           SSFStory ssfStory = new SSFStoryImpl();
           senCount = ssfStory.countSentences();
           
          PrintStream ps = new PrintStream("/home1/sanchay/automatic-annotation/file/try-output.txt");
           ssfStory.readFile("/home1/sanchay/automatic-annotation/file/story_43_1.final.mod.utf8-3");
           obj.printPOSTag(ssfStory, ps);
           
           
       
       
       
       }

    
    
}
