/*
 * MLCorpusConverter.java
 *
 * Created on September 10, 2008, 7:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

import iitb.crf.DataIter;
import java.io.File;
import sanchay.corpus.ssf.SSFCorpus;

/**
 *
 * @author Anil Kumar Singh
 */
public interface MLCorpusConverter {

    /** Tag [and FS] given, tag to be learnt */
    public static final int TAG_FORMAT = 0;

    /** [Tags and FS given], chunk (boundary and name) to be learnt */
    public static final int CHUNK_FORMAT = 1;

    /** [Tags and FS given], chunk (boundary and/or chunk feature) to be learnt */
    public static final int CHUNK_FEATURE_FORMAT = 2;

    /** Tag may or may not be given, feature to be learnt */
    public static final int TAG_FEATURE_FORMAT = 3;

    /** [Tags, chunk boundaries and FS given], some chunk attribute to be learnt */
    public static final int CHUNK_ATTRIBUTE_FORMAT = 4;

    public static final int _TOTAL_FORMATS_ = 5;

    void init(String ssfPath, String mlPath, String cs, String opath, String labelFeature);

    void init(File ssfFiles[], String mlPath, String cs, String opath, String labelFeature);
    
    SSFCorpus getSSFCorpus();
    DataIter getMLCorpus();
    
    void convert(int format);

    MLClassLabels getLabels();
    void setLabels(MLClassLabels labels);
}
