/*
 * BlogEntry.java
 *
 * Created on December 26, 2007, 4:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import sanchay.GlobalProperties;
import sanchay.corpus.CorpusStatistics;
import sanchay.ontology.writing.Author;
import sanchay.ontology.writing.SanchayURL;
import sanchay.ontology.writing.Time;
import sanchay.ontology.writing.Title;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author Anil Kumar Singh
 */
public class BlogEntry  implements SanchayDOMElement {

    Title title;
    Author author;
    Time time;
    SanchayURL url;

    Vector segments;
    
    /** Creates a new instance of BlogEntry */
    public BlogEntry() {
        segments = new Vector(0, 10);
    }
    
    public Title getTitle()
    {
        return title;
    }

    public void setTitle(Title t)
    {
        title = t;
    }            
    
    public Author getAuthor()
    {
        return author;
    }

    public void setAuthor(Author a)
    {
        author = a;
    }            
    
    public Time getTime()
    {
        return time;
    }

    public void setTime(Time t)
    {
        time = t;
    }            
    
    public SanchayURL getURL()
    {
        return url;
    }

    public void setURL(SanchayURL u)
    {
        url = u;
    }                

    public int countSegments() 
    {
        return segments.size();
    }

    public DOMElement getSegment(int num) 
    {
        return (DOMElement) segments.get(num);
    }

    public int addSegment(DOMElement s)
    {
        segments.add(s);
        return segments.size();
    }

    public DOMElement removeSegments(int num) 
    {
        return (DOMElement) segments.remove(num);
    }
    
    public String getText()
    {
        String text = "";
        
        int count = segments.size();
        
        for (int i = 0; i < count; i++)
        {
            text += ((DOMElement) segments.get(i)).getText() + "\n";
        }
        
        return text;
    }

    public void setText(String t)
    {
        String paras[] = t.split("[\n]");
        
        segments = new Vector(paras.length);
        
        for (int i = 0; i < paras.length; i++)
        {
            DOMElement seg = new DOMElement("p");
            seg.setText(paras[i]);
            segments.add(seg);
        }
    }

    public void readFileHTML(String filePath) throws Exception, FileNotFoundException, IOException            
    {
        
    }

    public void readFileXML(String filePath) throws Exception, FileNotFoundException, IOException            
    {
        
    }
    
    public void print(PrintStream ps)
    {
        ps.println("<BlogEntry>\n");
        
//        categories
        
        int pcount = segments.size();
        for (int i = 0; i < pcount; i++)
        {
            
        }

        ps.println("\t\t</BlogEntry>\n");
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement("BlogEntry");

        if(title != null)
        {
            DOMElement domElementTitle = title.getDOMElement();
            domElement.add(domElementTitle);
        }

        if(author != null)
        {
            DOMElement domElementAuthor = author.getDOMElement();
            domElement.add(domElementAuthor);
        }

        if(time != null)
        {
            DOMElement domElementTime = time.getDOMElement();
            domElement.add(domElementTime);
        }

        if(url != null)
        {
            DOMElement domElementURL = url.getDOMElement();
            domElement.add(domElementURL);
        }

        if(segments != null)
        {
            int scount = segments.size();
            for (int i = 0; i < scount; i++)
            {
                DOMElement seg = (DOMElement) segments.get(i);
                domElement.add(seg);
            }
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
        NodeList elements = domElement.getElementsByTagName("Author");

        if(elements.item(0) != null)
        {
            author = new Author();
            author.readXML((org.w3c.dom.Element) elements.item(0));
        }

        elements = domElement.getElementsByTagName("Title");

        if(elements.item(0) != null)
        {
            title = new Title();
            title.readXML((org.w3c.dom.Element) elements.item(0));
        }

        elements = domElement.getElementsByTagName("Time");

        if(elements.item(0) != null)
        {
            time = new Time();
            time.readXML((org.w3c.dom.Element) elements.item(0));
        }

        elements = domElement.getElementsByTagName("URL");

        if(elements.item(0) != null)
        {
            url = new SanchayURL();
            url.readXML((org.w3c.dom.Element) elements.item(0));
        }

        elements = domElement.getElementsByTagName("p");
        
        int count = elements.getLength();
        
        for (int i = 0; i < count; i++)
        {
            org.w3c.dom.Element element = (org.w3c.dom.Element) elements.item(i);
            
            if(element != null)
                addSegment(XMLUtils.W3CDom2dom4jElement(element));
            else
                System.err.println(GlobalProperties.getIntlString("Null_paragraph_element_in:") + title.getTitle());
        }
    }
    
    public CorpusStatistics getStats()
    {
        CorpusStatistics stats = new CorpusStatistics();
        
        stats.setParagraphs(countSegments());
        
        int senStat = 0;
        int wrdStat = 0;
        int charStat = 0;

        int count = countSegments();
        
        for (int i = 0; i < count; i++)
        {
            senStat += getSegment(i).getTextTrim().split("[\\.!\\?\\|]").length;
            wrdStat += getSegment(i).getTextTrim().split("[\\s+\\n\\.;,]").length;
            charStat += getSegment(i).getTextTrim().length();
        }

        stats.setSentences(senStat);
        stats.setWords(wrdStat);
        stats.setCharacters(charStat);
        
        return stats;
    }
}
