/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.ssf;

import sanchay.properties.KeyValueProperties;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SSFCorpusCollection {
    public KeyValueProperties getCorporaPaths();

    public void setCorporaPaths(KeyValueProperties p);

    public KeyValueProperties getProperties();

    public void setProperties(KeyValueProperties p);
}