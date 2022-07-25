/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.stats;

/**
 *
 * @author anil
 */
public class CorpusStatisticsFactory {

    protected static CorpusStatistics corpusStatistics;

    public static CorpusStatistics getGlobalCorpusStatisticsReference()
    {
        if(corpusStatistics == null)
        {
            corpusStatistics = new CorpusStatistics(true);
            corpusStatistics.loadGlobalData();
        }

        return corpusStatistics;
    }
}
