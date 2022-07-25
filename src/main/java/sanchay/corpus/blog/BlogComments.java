/*
 * BlogComments.java
 *
 * Created on December 26, 2007, 5:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import sanchay.corpus.CorpusStatistics;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author Anil Kumar Singh
 */
public class BlogComments implements SanchayDOMElement {

    Vector comments;
    
    /** Creates a new instance of BlogComments */
    public BlogComments() {
        comments = new Vector(0, 10);
    }
    
    public int countComments() 

    {
        return comments.size();
    }

    public BlogComment getComment(int num) 
    {
        return (BlogComment) comments.get(num);
    }

    public int addComment(BlogComment c)
    {
        comments.add(c);
        return comments.size();
    }

    public BlogComment removeComment(int num) 
    {
        return (BlogComment) comments.remove(num);
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement("BlogComment");
        
        int ccount = comments.size();
        for (int i = 0; i < ccount; i++)
        {
            BlogComment cmt = getComment(i);
            domElement.add(cmt.getDOMElement());
        }
        
        return domElement;
    }

    public String getXML() {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void printXML(PrintStream ps) {
        ps.println(getXML());
    }
    
    public void readXML(org.w3c.dom.Element domElement) {        
        NodeList elements = domElement.getElementsByTagName("BlogComment");
        
        int count = elements.getLength();
        
        for (int i = 0; i < count; i++)
        {
            BlogComment cmt = new BlogComment();
            cmt.readXML((org.w3c.dom.Element) elements.item(i));
            addComment(cmt);
        }
    }
    
    public CorpusStatistics getStats()
    {
        CorpusStatistics stats = new CorpusStatistics();
        
        int parStat = 0;
        int senStat = 0;
        int wrdStat = 0;
        int charStat = 0;

        int count = countComments();
        
        for (int i = 0; i < count; i++)
        {
            BlogComment cmt = getComment(i);
            
            CorpusStatistics st = cmt.getStats();
            
            parStat += st.getParagraphs();
            senStat += st.getSentences();
            wrdStat += st.getWords();
            charStat += st.getCharacters();
        }

        stats.setSentences(parStat);
        stats.setSentences(senStat);
        stats.setWords(wrdStat);
        stats.setCharacters(charStat);
        
        return stats;
    }
}
