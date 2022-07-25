/*
 * ResourceQueryOptions.java
 *
 * Created on October 2, 2008, 2:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util.query;

/**
 *
 * @author eklavya
 */
public class ResourceQueryOptions {
    
    public RawCorpusQueryOptions rawCorpusQueryOptions;
    public NGramLMQueryOptions nGramLMQueryOptions;
    public LexiconQueryOptions lexiconQueryOptions;
    public SyntacticCorpusContextQueryOptions syntacticCorpusContextQueryOptions;
    public ParallelCorpusQueryOptions parallelCorpusQueryOptions;
    public DiscourseCorpusQueryOptions discourseCorpusQueryOptions;
    public XMLCorpusQueryOptions xmlCorpusQueryOptions;
    
    /** Creates a new instance of ResourceQueryOptions */
    public ResourceQueryOptions() {
        nGramLMQueryOptions = new NGramLMQueryOptions();
        syntacticCorpusContextQueryOptions = new SyntacticCorpusContextQueryOptions();
        xmlCorpusQueryOptions = new XMLCorpusQueryOptions();
    }
}
