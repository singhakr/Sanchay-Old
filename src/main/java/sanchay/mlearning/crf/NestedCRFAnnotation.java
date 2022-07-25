/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import iitb.crf.FeatureGeneratorNested;
import iitb.crf.NestedCRF;
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
public class NestedCRFAnnotation extends NestedCRF {

    public NestedCRFAnnotation(int numLabels, FeatureGeneratorNested fgen, String arg) {
	super(numLabels, fgen, arg);
    }

    public NestedCRFAnnotation(int numLabels, FeatureGeneratorNested fgen, java.util.Properties configOptions) {
	super(numLabels, fgen, configOptions);
    }

    public void write(String fileName, String cs)  throws IOException {
       PrintWriter out=new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), cs));
        out.println(lambda.length);
        for (int i = 0; i < lambda.length; i++)
            out.println(lambda[i]);
        out.close();
    }

    /**
     * read the parameters of the CRF from a file
     */
    public void read(String fileName, String cs) throws IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(fileName), cs));
        int numF = Integer.parseInt(in.readLine());
        lambda = new double[numF];
        int pos = 0;
        String line;
        while((line=in.readLine())!=null) {
            lambda[pos++] = Double.parseDouble(line);
        }
    }
}
