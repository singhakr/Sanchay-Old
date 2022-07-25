 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.weka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.feature.extraction.impl.OtherFeaturesImpl;
import sanchay.mlearning.feature.extraction.impl.WindowFeaturesImpl;

/**
 *
 * @author H Umesh
 */
public class ArffExtractFeatures {

    protected String charset;
    protected SSFSentenceImpl sen;
    protected SSFPhrase root;
    protected SSFNode word;
    protected LinkedHashMap labels;
    protected WindowFeaturesImpl windowFeature = new WindowFeaturesImpl();
    private int WINDOW_SIZE = 3;
    final int ATTR_TYPE_STRING = 0;
    final int ATTR_TYPE_NOMINAL_SPECIFICATION = 1;
    final int ATTR_TYPE_REAL = 2;
    final int ATTR_TYPE_INT = 3;
    final int ATTR_TYPE_NUMERIC = 4;
    private boolean TESTING_MODE;
    List<String> arffInstance;
    List<String> attributeNames;

    public ArffExtractFeatures() {
        
        labels = new LinkedHashMap();
        TESTING_MODE = false;        
        attributeNames = new ArrayList<String>();
    }
    
    
    
    
    public void init (SSFSentenceImpl sentence, SSFNode currentWord, String cs) {

        sen = sentence;
        root = sentence.getRoot(); 
        word = new SSFNode();
        word = currentWord;
        arffInstance = new ArrayList<String>();
        //System.out.println(" arffExtractFeature.init() Word- " +word.getLexData());
        charset = cs;
        
    }

    public void addArffFeatures() {
        //addFeature(getFeature("QFNUM",));
        addFeature( currentWord() );
        addFeature( contextWords() );
        addFeature( quantifierFeature());
        addFeature( symbolFeature() );
        addFeature( wordLength() );
        addFeature( suffixFeature() );
        addFeature( prevoiousWordPOSTag() );
    }
    
    
    private Object getLabel(String attributeName, int ATTR_TYPE) {
        
        if( labels.get(attributeName) == null ){
            //System.out.println(" getLabel( Adding new Label)-"+attributeName+"");
            return addLabel(attributeName, ATTR_TYPE);
        }else{
             return labels.get(attributeName);
        }
    }    
        
    private Object addLabel(String attributeName, int ATTR_TYPE) {

        if ( TESTING_MODE == false ) {
            
            switch (ATTR_TYPE) {
                
                case ATTR_TYPE_INT:
                            labels.put( attributeName, "integer" );
                            attributeNames.add(attributeName);
                            return labels.get( attributeName );                            
                case ATTR_TYPE_NOMINAL_SPECIFICATION:
                            MLClassLabels mlLabel  = new MLClassLabels();
                            labels.put( attributeName, mlLabel );
                            attributeNames.add(attributeName);
                            return labels.get( attributeName );
                case ATTR_TYPE_REAL:
                            labels.put( attributeName , "real" );
                            attributeNames.add(attributeName);
                            return labels.get( attributeName );                    
                case ATTR_TYPE_STRING:
                            labels.put( attributeName, "String" );
                            attributeNames.add(attributeName);
                            return labels.get( attributeName );
                case ATTR_TYPE_NUMERIC:
                            labels.put( attributeName, "numeric" );
                            attributeNames.add(attributeName);
                            return labels.get( attributeName );
                default:
                            return null;
                            
            }

        }
        return null;
    }
    
    
    
    protected void addFeature( List values ){
        if( values != null && !values.isEmpty())
        {
            for(int i = 0 ; i < values.size() ; i++ )
            {
                arffInstance.add( (String)values.get(i) );
            }
        }                
    }

    
    
    
    

    /*
     * During training- Checks if the ML Label has the "value" added into it, if not adds it. and returns the "value" back.
     * During testing-  If "value" exists in the ML Label returns the "value", else returns keySet "?"
     */
    private String getLabelValue(String attributeName ,MLClassLabels label, String value) {
            String featureValue = new String();
            
            String labelIndex = label.getLabel2IntMapping().getPropertyValue(value);
            
            if( (labelIndex == null || labelIndex.equals("null") ) )  
            {
                if (TESTING_MODE == false) {
                    int count = label.getInt2LabelMapping().countProperties();
                    label.getInt2LabelMapping().addProperty("" + count, value);
                    label.getLabel2IntMapping().addProperty(value, "" + count);
                    
                    
                    //System.out.print(" ATTRIBUTE-"+attributeName+ " LabelCount-"+count+" ValueAdded-"+value +" LabelIndex-"+labelIndex);
                    labelIndex = label.getLabel2IntMapping().getPropertyValue(value);
                    //System.out.println(" NewLabelIndex"+ labelIndex);
                    
                    
                    labels.put( attributeName , label);
                    featureValue = value;
                }else
                {
                    System.out.println(" In Else of getLabelValue");
                    featureValue = "?";
                }
            }else 
            {
                featureValue = value;
            }
            
            
            return featureValue;
    }
    
    protected List currentWord() {
        List feature = new ArrayList();

        //feature = null;

        String attributeName = "CURRENT_WORD";
        
        String label = (String) getLabel( attributeName , ATTR_TYPE_STRING);
        
        if( label != null && !label.equals("") && !label.equals("null") )
        {
            feature.add("'"+word.getLexData()+"'");
        }
        
        return feature;
        
    }

    
        protected List contextWords() {
        List feature = new ArrayList();
        
        //feature = null;
        
        String attributeName = "CONTEXT_WORDS"+" " + (2*WINDOW_SIZE);
        
        String label = (String) getLabel(attributeName, ATTR_TYPE_STRING);
        
        
        
        if (label != null && !label.equals("") && !label.equals("null") ) {

            List words = windowFeature.getWordWindow(sen, root.getAllLeaves().indexOf(word), WINDOW_SIZE, WindowFeaturesImpl.WINDOW_DIRECTION_LEFT, 1);

            for (int i = 0; i < words.size(); i++) {
                if( !(((SSFNode) words.get(i)).getLexData()).equals("?") ) {
                    feature.add("'"+((SSFNode) words.get(i)).getLexData()+"'");
                }
                else {
                    feature.add("?");
                }
            }
            if (words.size() < WINDOW_SIZE) {
                for (int i = 0; i < (WINDOW_SIZE - words.size()); i++) {
                    feature.add("?");
                }
            }
            
            words = windowFeature.getWordWindow(sen, root.getAllLeaves().indexOf(word), WINDOW_SIZE, WindowFeaturesImpl.WINDOW_DIRECTION_RIGHT, 1);
            
            for (int i = 0; i < words.size(); i++) {
                if( !(((SSFNode) words.get(i)).getLexData()).equals("?") ) {
                    feature.add( "'"+ ((SSFNode) words.get(i)).getLexData() + "'");
                }
                else {
                    feature.add("?");
                }
                    
            }
            if (words.size() < WINDOW_SIZE) {
                for (int i = 0; i < (WINDOW_SIZE - words.size()); i++) {
                    feature.add(feature.size(), "?");
                }
            }
        
        }

        return feature;
    }
        
        
    private List prevoiousWordPOSTag() {
        List features = new ArrayList();

        String attributeName = "PREVIOUS_WORDS_POS_TAG "+ WINDOW_SIZE;

        MLClassLabels label = (MLClassLabels) getLabel(attributeName, ATTR_TYPE_NOMINAL_SPECIFICATION);

        if (label != null ) {
            List words = windowFeature.getWordWindow(sen, root.getAllLeaves().indexOf(word), WINDOW_SIZE, WindowFeaturesImpl.WINDOW_DIRECTION_LEFT, 1);

            for (int i = 0; i < words.size(); i++) {
                if (!(((SSFNode) words.get(i)).getLexData()).equals("?")) {
                    String tag = getLabelValue(attributeName,label,((SSFNode) words.get(i)).getName());
                    features.add( tag );
                } else {
                    features.add("?");
                }
            }
            if (words.size() < WINDOW_SIZE) {
                for (int i = 0; i < (WINDOW_SIZE - words.size()); i++) {
                    features.add("?");
                }
            }                                   
        }               
        return features;
    }

    
    protected List quantifierFeature() {
        List feature = new ArrayList();

        //feature = null;
        
        String attributeName = "QFNUM";
        
        OtherFeaturesImpl wordFeature = new OtherFeaturesImpl();

        MLClassLabels label = new MLClassLabels();

        String quantifier = new String();

        label = (MLClassLabels) getLabel( attributeName , ATTR_TYPE_NOMINAL_SPECIFICATION);
        
        if (label != null) {
            if (wordFeature.isNumber(word)) {                
                quantifier = getLabelValue(attributeName,label,"TRUE"); //add the feature value if it does not exist beforehand or return the feature value(which is the argument string)
                                                        //in training phase, and return the feature value if exists or return '?' if not, during testing phase
            }else
            {
                quantifier = getLabelValue(attributeName,label,"FALSE");
            }
            
            feature.add(quantifier);
        }

        return feature;
    }

    
    protected  List symbolFeature()
    {
        List feature = new ArrayList();
        
        //feature = null;
        
        String attributeName = "IS_SPECIAL_SYMBOL";
        
        MLClassLabels label = new MLClassLabels();

        String isSymbol = new String();
        
        label = (MLClassLabels) getLabel(attributeName, ATTR_TYPE_NOMINAL_SPECIFICATION);
        
        if (label != null) {
            String lexData = new String();
            lexData = word.getLexData();
            isSymbol = getLabelValue(attributeName,label,"FALSE");
            for (int i = 0; i < lexData.length(); i++) {

                char ch = lexData.charAt(i);

                if (isSpecialSymbol(ch)) {
                    isSymbol = getLabelValue(attributeName,label,"TRUE");
                    break;
                }
            }            
            feature.add(isSymbol);
        }
        
        return feature;
    }

   
    
        
    private List wordLength() {
        List feature = new ArrayList();
        
        String attributeName = "WORD_LENGTH";
        
        MLClassLabels label = new MLClassLabels();
        
        label = (MLClassLabels) getLabel(attributeName, ATTR_TYPE_NOMINAL_SPECIFICATION);
        
        if( label != null )
        {
            String length = getWordLength(word.getLexData());
            String wordLength = getLabelValue(attributeName,label,length);
            
            feature.add(wordLength);
        }
        
        return feature;
    }

    private String getWordLength(String lexData) {
        String wordLength = new String();
        
        if( lexData.length() <= 4 )
        {
            wordLength = "eq_"+lexData.length() + "";
        }else{
            wordLength = "gt_4";
        }
        
        return wordLength;
    }
    
    private List suffixFeature() {
        List feature = new ArrayList();
        
        String attributeName = "SUFFIX";
        
        String label = (String) getLabel( attributeName , ATTR_TYPE_STRING);
        
        if( label != null && !label.equals("") && !label.equals("null") )
        {
            int startIndex = 0;

            if (word.getLexData().length() > 4) {
                startIndex = word.getLexData().length() - 4;
            }
            String suffix = word.getLexData().substring(startIndex);
            feature.add("'"+suffix+"'");

            
            
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

    
    
    public void setWINDOW_SIZE(int WINDOW_SIZE) {
        this.WINDOW_SIZE = WINDOW_SIZE;
    }

    
    public String getArffInstance()
    {
        String instance = new String();
        
        for( int i = 0 ; i < ( arffInstance.size() ) ; i++ ) 
        {
            String value = (String) arffInstance.get(i);
            if( !value.equals("null") &&  !value.equals("") && value.equals("'null'") == false)
            {   
                if( !value.equals("'''")) {
                    instance = instance + value + ",";
                }            
                else {
                    instance = instance + "''"+",";
                }
                //System.out.print(" In IF of getArffInstance");
            }else{
                //System.out.println(" In Else of getArffInstance");
                instance = instance + "??,";                
            }
        }
                
        return instance;
                
    }
  
    public LinkedHashMap getAttributes(){
        return labels;
    }
    
    public String[] getAttributeNames(){

        String keys[] = new String[attributeNames.size()];
        
        for (int i = 0; i < attributeNames.size(); i++) {
            keys[i] = (String) attributeNames.get(i);
            //System.out.print(keys[i]+" ");
        }

        return keys;
    }

   
}
