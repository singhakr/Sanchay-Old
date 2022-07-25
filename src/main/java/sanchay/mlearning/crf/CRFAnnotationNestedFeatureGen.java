/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import iitb.crf.FeatureGeneratorNested;
import iitb.model.ConcatRegexFeatures;
import iitb.model.FeatureTypesMulti;
import iitb.model.NestedFeatureGenImpl;
import iitb.model.WindowFeatures;
import iitb.model.WordsInTrainExt;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 *
 * @author Anil Kumar Singh
 */
public class CRFAnnotationNestedFeatureGen extends NestedFeatureGenImpl implements FeatureGeneratorNested {

    public CRFAnnotationNestedFeatureGen(String modelSpecs, int numLabels) throws Exception {
        super(modelSpecs, numLabels, true);
    }

    public CRFAnnotationNestedFeatureGen(int numLabels,java.util.Properties options, boolean addFeatureNow) throws Exception {
        super(numLabels, options, addFeatureNow);
    }

    public CRFAnnotationNestedFeatureGen(int numLabels,java.util.Properties options) throws Exception {
        super(numLabels, options);
    }

    /**
     * @param modelSpecs
     * @param numLabels
     * @param addFeatureNow
     */
    public CRFAnnotationNestedFeatureGen(String modelSpecs, int numLabels, boolean addFeatureNow) throws Exception {
        super(modelSpecs, numLabels, addFeatureNow);
    }

    protected void addFeatures()
    {
        super.addFeatures();

        WindowFeatures.Window windows[] = new WindowFeatures.Window[] {
                new WindowFeatures.Window(0,true,0,true,"start"),
                new WindowFeatures.Window(0,false,0,false,"end"),
                new WindowFeatures.Window(1,true,-1,false,"continue"),
                new WindowFeatures.Window(-1,true,-1,true,"left-1"),
                new WindowFeatures.Window(1,false,1,false,"right+1"),
        };

        addFeature(new WindowFeatures(windows, new FeatureTypesMulti(new ConcatRegexFeatures(this,0,0))));

        addFeature(new CRFPOSTagFeatures(this, dict));
    }

    public void read(String fileName, String cs) throws IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(fileName), cs));
        WordsInTrainExt dict = getDict();
        if (dict != null) dict.read(in, model.numStates());
        totalFeatures = featureMap.read(in);
    }

    public void write(String fileName, String cs) throws IOException {
       PrintWriter out=new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), cs));
        WordsInTrainExt dict = getDict();
        if (dict != null) dict.write(out);
        featureMap.write(out);
        out.close();
    }
}
