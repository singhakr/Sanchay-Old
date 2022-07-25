/*
 * MLCorpusConverter.java
 *
 * Created on September 10, 2008, 7:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common.impl;

import iitb.crf.DataIter;
import java.io.File;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.mlearning.common.MLClassLabels;
import sanchay.mlearning.common.MLCorpusConverter;

/**
 *
 * @author Anil Kumar Singh
 */
public class DefaultMLCorpusConverter implements MLCorpusConverter {
    java.util.ResourceBundle bundle = GlobalProperties.getResourceBundle(); // NOI18N
    
    protected String ssfPath;
    protected File ssfFiles[];

    protected String mlPath;

    protected String labelFeature;

    protected String outputPath;

    protected String charset;

    protected SSFCorpus ssfCorpus;
    protected DataIter mlCorpus;

    protected MLClassLabels labels;

    public DefaultMLCorpusConverter() {
        labels = new MLClassLabels();
    }

    public void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature)
    {
        this.ssfPath = ssfPath;
        this.mlPath = mlPath;

        this.labelFeature = labelFeature;

        charset = cs;

        outputPath = opath;
    }

    public void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature)
    {
        this.ssfFiles = ssfFiles;
        this.mlPath = mlPath;

        this.labelFeature = labelFeature;

        charset = cs;

        outputPath = opath;
    }

    public SSFCorpus getSSFCorpus()
    {
        return ssfCorpus;
    }



    public void setSSFCorpus(SSFCorpus c)
    {
        ssfCorpus = c;
    }

    public DataIter getMLCorpus()
    {
        return mlCorpus;
    }

    public void setMLCorpus(DataIter c)
    {
        mlCorpus = c;
    }

    public void convert(int format)
    {
        switch(format)
        {
            case TAG_FORMAT:
                convertToTagFormat(false);
                break;
            case TAG_FEATURE_FORMAT:
                convertToTagFormat(true);
                break;
            case CHUNK_FORMAT:
                convertToChunkFormat(false, false);
                break;
            case CHUNK_FEATURE_FORMAT:
                convertToChunkFormat(true, false);
                break;
            case CHUNK_ATTRIBUTE_FORMAT:
                convertToChunkFormat(true, true);
                break;
        }
    }

    protected void convertToTagFormat(boolean featureType)
    {
    }

    protected void convertToChunkFormat(boolean featureType, boolean preserveChunkBoundaries)
    {

    }

    public MLClassLabels getLabels()
    {
        return labels;
    }

    public void setLabels(MLClassLabels labels)
    {
        this.labels = labels;
    }

    public void saveFeatureLabels()
    {

    }

    public void setFeatureLabels() {

    }
}
