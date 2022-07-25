/*
 * Created on Jul 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.simple.data;

import java.io.PrintStream;

import sanchay.corpus.parallel.APCData;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface WordTypeEx {
    public int countHypernyms();

    public Integer getHypernym(int num);

    public int addHypernym(int h);

    public Integer removeHypernym(int num);

    public int countSynonyms();

    public Integer getSynonyms(int num);

    public int addSynonyms(int h);

    public Integer removeSynonyms(int num);

    public int getPhoneticMatch();

    public void setPhoneticMatch(int pm);

    public void populate(APCData apcdata, String tag, String type);

    public int print(PrintStream ps);
}