/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import iitb.crf.CRF;
import iitb.crf.FeatureGenerator;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 *
 * @author Anil Kumar Singh
 */
public class CRFAnnotation extends CRF implements Serializable {

    public CRFAnnotation(int numLabels, FeatureGenerator fgen, String arg) {
        super(numLabels, fgen, arg);
    }

    public CRFAnnotation(int numLabels, FeatureGenerator fgen, java.util.Properties configOptions) {
        super(numLabels, 1, fgen, configOptions);
    }

    public CRFAnnotation(int numLabels, int histsize, FeatureGenerator fgen, java.util.Properties configOptions) {
        super(numLabels, histsize, fgen, configOptions);
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
