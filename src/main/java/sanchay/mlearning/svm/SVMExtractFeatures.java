/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.svm;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.feature.extraction.impl.OtherFeaturesImpl;
import sanchay.mlearning.feature.extraction.impl.WindowFeaturesImpl;
import sanchay.mlearning.feature.postagger.GetPOSTagImpl;
import sanchay.properties.KeyValueProperties;

/**
 *
 * @author anil
 */
/**
 * This class is for extracting freatures for libsvm input file.
 * A feature here is a "feature" being used for training ( for example,
 * --The present words gender feature
 * --The previous words POS tag. etc. )
 * Label refers to the number being assgined to a feature and this assignment is done by MLClassLabels object.
 */
public class SVMExtractFeatures
{
//protected PreviousWordsFeature prevWordFeature = new PreviousWordsFeature();
//protected ContextFeaturesImpl contextFeatures = new ContextFeaturesImpl();

    protected WindowFeaturesImpl windowFeature = new WindowFeaturesImpl();
    protected MLClassLabels labels = new MLClassLabels();
    protected MLClassLabels posLabels = new MLClassLabels();
    protected GetPOSTagImpl getPosTags = new GetPOSTagImpl();
    protected SSFStoryImpl ssfStory = new SSFStoryImpl();
    protected SSFSentenceImpl ssfSentence = new SSFSentenceImpl();
    protected SSFPhrase root = new SSFPhrase();
    protected SSFNode word = new SSFNode();
    protected List instance;
    //protected String svmInstance;
    private int WINDOW_SIZE;
    protected int MAX_POS_LABELS;
    private int TESTING_MODE = 0;
    
    public void init(SSFStoryImpl ssfStory, SSFSentenceImpl sentence, SSFNode currentWord, String cs, MLClassLabels MLLabel)
    {
        this.ssfStory = ssfStory;

        ssfSentence = sentence;

        root = sentence.getRoot();

        word = currentWord;

        instance = new ArrayList();
        //svmInstance = new String();

        //svmInstance = "";

        posLabels = MLLabel;

        setWINDOW_SIZE(3);

        MAX_POS_LABELS = 400;                                                   // Check this part-- need to find a better alternative

        labels.getLabel2IntMapping().addProperty("0", "blank");                      //0th PosTag initialised to "blank" because the SVM input labels
        labels.getInt2LabelMapping().addProperty("blank", "0");                    //start from 1.
    }    
    public void addSVMfeatures()
    {
        int i=0;
        addFeature( quantifierFeature(3.5)  );
        addFeature( symbolFeature(3.5)  );
        addFeature( wordLength(1)  );
        addFeature( suffixFeature(2.8) );
        addFeature( prefixFeature(2.7) );
        addFeature( FSFeature("cat", word) );
        addFeature( FSFeature("gend", word) );
        addFeature( FSFeature("num", word) );
        addFeature( FSFeature("pers", word) );
        addFeature( FSFeature("case", word) );
        addFeature( FSFeature("vib", word) );
        addFeature( FSFeature("tam", word) );
        addFeature( previousWordsFeature(1 /*getWINDOW_SIZE()*/,2.6)  );
        addFeature( nextWordsFeature(3 /*getWINDOW_SIZE()*/,2.6)  );
        addFeature( previousWordsPOSFeature(getWINDOW_SIZE()) );
        addFeature( currentWord(3.0)  );
       // if( TESTING_MODE == 0 )
       // {
        //    addFeature(PosProbabilities());
       // }
        //addFeature(rareWord());                                           // Not impleneted - need the "frequent words" list
        addFeature( PreviosWordsFSFeature(getWINDOW_SIZE(), "cat") );
        addFeature( PreviosWordsFSFeature(getWINDOW_SIZE(), "gend") );
        addFeature( PreviosWordsFSFeature(getWINDOW_SIZE(), "num") );
        addFeature( PreviosWordsFSFeature(getWINDOW_SIZE(), "pers") );
        addFeature( PreviosWordsFSFeature(getWINDOW_SIZE(), "case") );
        addFeature( PreviosWordsFSFeature(getWINDOW_SIZE(), "vib") );
        addFeature( PreviosWordsFSFeature(getWINDOW_SIZE(), "tam") );
     }

    private void addFeature(List features)
    {

        //System.out.println(" in SVMExtractFeature.addfeature");
        String index = new String();

        String featureValue = new String();

        SVMFeatureNode feature = new SVMFeatureNode();

        SVMFeatureNode temp = new SVMFeatureNode();

        int flag = 0;

        if (!features.isEmpty() )
        {

            for (int i = 0; i < features.size(); i++)
            {

                flag = 0;

                feature = (SVMFeatureNode) features.get(i);

                if (feature.getFeatureIndex() != -1) {
                    if (instance.size() == 0) {
                        instance.add(feature);
                        flag = 1;
                    } else {
                        for (int j = 0; j < instance.size(); j++) {
                            temp = (SVMFeatureNode) instance.get(j);

                            if (feature.getFeatureIndex() < temp.getFeatureIndex()) {
                                instance.add(j, feature);
                                flag = 1;
                                break;
                            }
                        }
                    }
                    if (flag == 0) {
                        instance.add(feature);
                    }
                //index = "" + feature.getFeatureIndex();

                //featureValue = "" + feature.getFeatureValue();

                //svmInstance = svmInstance + " " + index + ":" + featureValue;
                }
            }

        }
    }

    protected List previousWordsFeature(int windowSize,double wt)
    {
        List features = new ArrayList();

        int wordIndex;

        SSFNode ssfNode = new SSFNode();

        String currentWord = new String();

        int label = -1;

        List words = new ArrayList();


        words = root.getAllLeaves();
        wordIndex = words.indexOf(word);


        /************** Simple POS tag extracter of previos words *************/
        int count = WINDOW_SIZE;
        if (wordIndex < WINDOW_SIZE)
        {
            count = wordIndex;
        }

        //System.out.println("                 count ---->"+ count);
        for (int i = 0; i < count; i++)
        {
            ssfNode = (SSFNode) words.get(wordIndex - 1 - i);

            currentWord = ssfNode.getLexData();

            SVMFeatureNode svmNode = new SVMFeatureNode();

            label = getLabel("Prev_" + (i + 1) + "_WORD_" + currentWord);

            //System.out.println("                           word------------>"+currentWord );

            svmNode.setFeatureIndex(label);

            svmNode.setFeatureValueDouble(wt);

            features.add(svmNode);
        }
        /*************************************************************************/
        return features;
    }


    protected List nextWordsFeature(int windowSize,double wt)
    {
        List features = new ArrayList();

        int wordIndex;

        SSFNode ssfNode = new SSFNode();

        String nextWord = new String();

        int label = -1;

        List words = new ArrayList();


        words = root.getAllLeaves();
        wordIndex = words.indexOf(word);

        int totalWords = words.size();
        int count = totalWords - wordIndex - 1 ;

        if( count >= windowSize )
        {
         count = windowSize;
        }


        for (int i = 0; i < count; i++)
        {

            ssfNode = (SSFNode) words.get(wordIndex + 1 + i);

            nextWord = ssfNode.getLexData();

            SVMFeatureNode svmNode = new SVMFeatureNode();

            label = getLabel("Next_" + (i + 1) + "_WORD_" + nextWord);

            svmNode.setFeatureIndex(label);

            svmNode.setFeatureValueDouble(wt);

            features.add(svmNode);

        }

        return features;
    }


    protected List previousWordsPOSFeature(int windowSize)
    {
        List features = new ArrayList();

        int wordIndex;

        SSFNode ssfNode = new SSFNode();

        String PosTag = new String();

        int label = -1;

        List words = new ArrayList();


        words = root.getAllLeaves();
        wordIndex = words.indexOf(word);


        /************** Simple POS tag extracter of previos words *************/
        int count = windowSize;
        if (wordIndex < windowSize)
        {
            count = wordIndex;
        }
        for (int i = 0; i < count; i++)
        {
            ssfNode = (SSFNode) words.get(wordIndex - 1 - i);

            PosTag = ssfNode.getName();

            SVMFeatureNode svmNode = new SVMFeatureNode();

            label = getLabel("Prev_" + (i + 1) + "_POS_" + PosTag);

            svmNode.setFeatureIndex(label);

            svmNode.setFeatureValueInt(1);

            features.add(svmNode);
        }
        /*************************************************************************/
        return features;
    }

    private List PosProbabilities()
    {
        List probabilities = new ArrayList();

        List occurrences = new ArrayList();

        SSFNode ssfNode = new SSFNode();

        String posTag = new String();

        String posLabelStr = new String();

        int posLabel = 0, i, svmLabel;

        int count[] = new int[MAX_POS_LABELS];

        double probability;

        occurrences = ssfStory.getAllOccurrences(word.getLexData());

        for (i = 0; i < occurrences.size(); i++)
        {

            ssfNode = (SSFNode) occurrences.get(i);

            posTag = ssfNode.getName();

            posLabelStr = posLabels.getInt2LabelMapping().getPropertyValue(posTag);

            if (posLabelStr == null || posLabelStr.equals("null"))
            {
                posLabel = posLabels.getLabel2IntMapping().countProperties();

                posLabelStr = "" + posLabel;

                posLabels.getLabel2IntMapping().addProperty("" + posLabel, posTag);

                posLabels.getInt2LabelMapping().addProperty(posTag, "" + posLabel);
            }

            //System.out.println("POSLABEL------------->"+posLabel+"is for-->"+posTag+" lexData"+ssfNode.getLexData());
            posLabel = Integer.parseInt(posLabelStr);

            count[posLabel]++;
        }

        for (i = 0; i < count.length; i++)
        {
            if (count[i] != 0)
            {
                SVMFeatureNode svmNode = new SVMFeatureNode();

                posTag = posLabels.getLabel2IntMapping().getPropertyValue("" + i);

                svmLabel = getLabel("PROB_" + posTag);

                probability = ((double) count[i]) / ((double) occurrences.size());

                svmNode.setFeatureIndex(svmLabel);

                svmNode.setFeatureValueDouble(probability);

                probabilities.add(svmNode);

            }
        }
        return probabilities;
    }

    private List prefixFeature(double wt) {
       List feature = new ArrayList();

        SVMFeatureNode svmNode = new SVMFeatureNode();

        String prefix = new String();

        int label = -1;
        int endIndex = word.getLexData().length();

        if( word.getLexData().length() > 3 )
        {
            endIndex = 3 ;
        }

        prefix = word.getLexData().substring(0,endIndex);


        label = getLabel("prefix_"+prefix);

        svmNode.setFeatureIndex(label);

        svmNode.setFeatureValueDouble(wt);

        feature.add(svmNode);

        return feature;
    }

         private List suffixFeature(double wt) {

        List feature = new ArrayList();

        SVMFeatureNode svmNode = new SVMFeatureNode();

        String suffix = new String();

        int label = -1;
        int startIndex = 0 ;

        if( word.getLexData().length() > 4 )
        {
            startIndex = word.getLexData().length() - 4 ;
        }
        suffix = word.getLexData().substring(startIndex);


        label = getLabel("suffix_"+suffix);

        svmNode.setFeatureIndex(label);

        svmNode.setFeatureValueDouble(wt);

        feature.add(svmNode);

        return feature;
    }


    private List currentWord(double wt) {
      List feature = new ArrayList();

      String currentWord = word.getLexData();

      SVMFeatureNode svmNode = new SVMFeatureNode();

      int label = -1;

      label = getLabel("Current_WORD_"+currentWord);

      svmNode.setFeatureIndex(label);

      svmNode.setFeatureValueDouble(wt);

      feature.add(svmNode);


      return feature;
    }

    private boolean isFrequentWord(SSFNode word)
    {

        int frequency;

        frequency = ssfStory.getAllOccurrences(word.getLexData()).size();

        if (frequency >= 4)
        {
            return true;
        }

        return false;

    }

    private List rareWord()
    {
        List feature = new ArrayList();

        return feature;
    }

    private List PreviosWordsFSFeature(int windowSize, String fSAttribute)
    {
        List features = new ArrayList();

        List previousWordsFeature = new ArrayList();

        List words = new ArrayList();

        int label = -1;

        String fSValue = new String();

        SVMFeatureNode svmNode = new SVMFeatureNode();

        words = root.getAllLeaves();

        int wordIndex = words.indexOf(word);

        try{
        previousWordsFeature = windowFeature.getWordFeatureWindow(ssfSentence, wordIndex, windowSize, WindowFeaturesImpl.WINDOW_DIRECTION_LEFT, fSAttribute);
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.print(".");
        }
        for (int i = 0; i < previousWordsFeature.size(); i++)
        {

            fSValue = (String) previousWordsFeature.get(i);

            if (!fSValue.isEmpty() && !fSValue.equals(""))
            {
                label = getLabel("Prev_" + (i + 1) + "_" + fSAttribute + "_" + fSValue);

                svmNode.setFeatureIndex(label);

                svmNode.setFeatureValueInt(1);

                features.add(svmNode);
            }
        }

        return features;
    }

    private void addLabel(String propName)
    {
        int labelIndex = 0;
        labelIndex = labels.getInt2LabelMapping().countProperties();
        labels.getLabel2IntMapping().addProperty("" + labelIndex, propName);
        labels.getInt2LabelMapping().addProperty(propName, "" + labelIndex);
    }

    private int getLabel(String propName)
    {
        int label = -1;


        String labelStr = new String();

        labelStr = labels.getInt2LabelMapping().getPropertyValue(propName);
        if (TESTING_MODE == 0) {
            if (labelStr == null || labelStr.equals("null")) {
                //System.out.println("                  new property added-"+propName);
                addLabel(propName);
            }
        }
            labelStr = labels.getInt2LabelMapping().getPropertyValue(propName);

            if( labelStr != null && !labelStr.equals("null"))
            {
                 label = Integer.parseInt(labelStr);
            }
        return label;
    }

    private List quantifierFeature(double wt)
    {
        List feature = new ArrayList();

        OtherFeaturesImpl wordFeature = new OtherFeaturesImpl();                 // from feature extraction API

        int label = getLabel("QFNUM");

        SVMFeatureNode svmNode = new SVMFeatureNode(label, wt);

        if (wordFeature.isNumber(word))
        {
            feature.add(svmNode);
            return feature;
        }

        return feature;

    }
    
    private List symbolFeature(double wt)
    {
        List feature = new ArrayList();

        String lexData = new String();

        int label = getLabel("IsSYM");

        lexData = word.getLexData();

        for (int i = 0; i < lexData.length(); i++)
        {

            char ch = lexData.charAt(i);

            if (isSpecialSymbol(ch))
            {

                SVMFeatureNode svmNode = new SVMFeatureNode(label,wt);

                feature.add(svmNode);

                return feature;

            }

        }

        return feature;
    }

    private List wordLength(double wt)
    {
        List feature = new ArrayList();

        OtherFeaturesImpl wordFeature = new OtherFeaturesImpl();                 // from Feature extraction API

        int label = getLabel("length_gt_3");

        SVMFeatureNode svmNode = new SVMFeatureNode(label, wt);

        if (wordFeature.characterCount(word) <= 3)
        {
            label = getLabel("length_le_3");
            svmNode.setFeatureIndex(label);
        }

        feature.add(svmNode);

        return feature;
    }

    private List FSFeature(String propName, SSFNode ssfWord)
    {
        List feature = new ArrayList();

        int label = -1;

        SVMFeatureNode svmNode = new SVMFeatureNode();

        String PropValue = new String();                                         //property value, for example- sg, pl for num FS property.

        PropValue = ssfWord.getAttributeValue(propName);

        svmNode.setFeatureValueInt(1);

        if ((PropValue != null) && !PropValue.isEmpty())
        {
            label = getLabel(propName + "_" + PropValue);

            svmNode.setFeatureIndex(label);

            feature.add(svmNode);
        }
        return feature;
    }




    private boolean isSpecialSymbol(char ch)
    {
        if ((ch > 32 && ch < 48) || (ch > 57 && ch < 65) || (ch > 90 && ch < 97) || (ch > 122 && ch < 127))
        {
            return true;
        }
        return false;
    }

    public static List getBIEOTag ( SSFPhrase SentenceRoot , boolean includeChunkTag )
    {
        SSFNode child   =  new SSFNode ( ) ;

        List sentenceTags = new ArrayList ( ) ;

        List childTags  =  new ArrayList ( ) ;

        int level = 0 ;

        int  i , j ;
        int childrenCount=SentenceRoot.countChildren();

        //System.out.println("LexData-"+SentenceRoot.getLexData()+" "+SentenceRoot.getName()/*+" ID-"+child.getId()+" Level-"+child.getLevel()*/);
        String parentTag = new String();
        if( ! SentenceRoot.getName().equals("SSF"))
        {
            parentTag = SentenceRoot.getName();
        }



        for ( i = 0 ; i< childrenCount ; i++ )
        {
            child  =  SentenceRoot.getChild ( i ) ;

            level  =  child.getLevel ( ) ;

            String chunkTag = child.getName();

            String wordTag = new String();

            if (     !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   && !child.getLexData().equals("[[") &&   level  !=  1 )
            {
                if (i == 0) {

                    if ( childrenCount == 1 ){
                        wordTag = "" + 'S';
                        if( includeChunkTag == true )
                        {
                            wordTag = wordTag + "-" + parentTag;
                        }

                        sentenceTags.add(wordTag);

                    }else {
                        wordTag = "" + 'B' ;

                        if( includeChunkTag == true )
                        {
                            wordTag = wordTag + "-" + parentTag;
                        }
                        sentenceTags.add(wordTag);
                    }

                } else {
                    if (i == (childrenCount - 1) ) {

                        wordTag = "" + 'E';

                        if( includeChunkTag == true )
                        {
                            wordTag = wordTag + "-" + parentTag;
                        }
                        sentenceTags.add(wordTag);

                    } else {

                        wordTag = "" + 'I' ;

                        if( includeChunkTag == true )
                        {
                            wordTag = wordTag + "-" + parentTag;
                        }
                        sentenceTags.add(wordTag);

                    }
                }
            }

            if(      !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   && !child.getLexData().equals("[[") &&  level ==  1  )
            {
                wordTag = 'O' + "";

                /*if( includeChunkTag == true )
                        {
                            wordTag = wordTag + "-" + parentTag;
                        }*/
                sentenceTags.add(wordTag);
            }



            /*********************Recursive call in case of nested chunking**********************/
            if(   child.getLexData().equals("")    ||    child.getLexData().equals("((")  || child.getLexData().equals("[[") )
            {
                childTags   =  getBIEOTag((SSFPhrase)child , includeChunkTag )  ;

                for( j = 0 ;  j<  childTags.size ( ) ;  j++  )
                {
                    sentenceTags.add(childTags.get ( j ) );
                }

            }
        }

       return sentenceTags;
    }

     public static List getBIOTag ( SSFPhrase SentenceRoot , boolean includeChunkTag )
    {
        SSFNode child   =  new SSFNode ( ) ;

        List sentenceTags = new ArrayList ( ) ;

        List childTags  =  new ArrayList ( ) ;

        int level = 0 ;

        int  i , j ;
        int childrenCount=SentenceRoot.countChildren();

        //System.out.println("LexData-"+SentenceRoot.getLexData()+" "+SentenceRoot.getName()/*+" ID-"+child.getId()+" Level-"+child.getLevel()*/);
        String parentTag = new String();
        if( ! SentenceRoot.getName().equals("SSF"))
        {
            parentTag = SentenceRoot.getName();
        }



        for ( i = 0 ; i< childrenCount ; i++ )
        {
            child  =  SentenceRoot.getChild ( i ) ;

            level  =  child.getLevel ( ) ;

            String chunkTag = child.getName();

            String wordTag = new String();

            if (     !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   && !child.getLexData().equals("[[") &&   level  !=  1 )
            {
                if (i == 0) {

                        wordTag = "" + 'B' ;

                        if( includeChunkTag == true )
                        {
                            wordTag = wordTag + "-" + parentTag;
                        }
                        sentenceTags.add(wordTag);

                } else {

                        wordTag = "" + 'I' ;

                        if( includeChunkTag == true )
                        {
                            wordTag = wordTag + "-" + parentTag;
                        }
                        sentenceTags.add(wordTag);

                }
            }

            if(      !child.getLexData().equals("")    &&   !child.getLexData().equals("((")   && !child.getLexData().equals("[[") &&  level ==  1  )
            {
                wordTag = 'O' + "";

                sentenceTags.add(wordTag);
            }



            /*********************Recursive call in case of nested chunking**********************/
            if(   child.getLexData().equals("")    ||    child.getLexData().equals("((")  || child.getLexData().equals("[[") )
            {
                childTags   =  getBIOTag((SSFPhrase)child , includeChunkTag )  ;

                for( j = 0 ;  j<  childTags.size ( ) ;  j++  )
                {
                    sentenceTags.add(childTags.get ( j ) );
                }

            }
        }

       return sentenceTags;
    }


    public int getWINDOW_SIZE()
    {
        return WINDOW_SIZE;
    }

    public void setWINDOW_SIZE(int WINDOW_SIZE)
    {
        this.WINDOW_SIZE = WINDOW_SIZE;
    }

    public void saveLabels(String path, String cs) throws UnsupportedEncodingException, FileNotFoundException
    {
        labels.getInt2LabelMapping().save(path, cs);
    }

    public void setLabels(KeyValueProperties featureLabels)
    {
     labels.setLabels(featureLabels);
     labels.setRevLabels(featureLabels.getReverse());
     TESTING_MODE = 1;
     //for(int i = 0 ; i< labels.getInt2LabelMapping().countProperties() ; i++ )
     //{
         //System.out.println(i+"--"+labels.getLabel2IntMapping().getPropertyValue(""+i));
     //}
    }

    public String getInstance()
    {

        //System.out.println(" In SVMEXtractFeatures.getInstance instance size ="+ instance.size());
        SVMFeatureNode feature1 = new SVMFeatureNode();
        //SVMFeatureNode feature2 = new SVMFeatureNode();
        //SVMFeatureNode temp = new SVMFeatureNode();
       /* for( int i = 0 ; i < instance.size() ; i++)
        {
            feature1 = (SVMFeatureNode) instance.elementAt(i);
            for(int j = 0; j < instance.size() ; j++ )
            {
                feature2 = (SVMFeatureNode) instance.elementAt(j);

                if( feature1.getFeatureIndex() > feature2.getFeatureIndex() )
                {
                    temp = feature2;
                    instance.setElementAt(feature1, j);
                    instance.setElementAt(temp, i);

                }
            }
        } */

        String svmInstance = new String("");

        for(int i=0 ;i < instance.size() ; i++)
        {
            feature1 = (SVMFeatureNode) instance.get(i);
            svmInstance= svmInstance + " " + feature1.getFeatureIndex()+":"+feature1.getFeatureValue();
        }
        return svmInstance;
    }

}
