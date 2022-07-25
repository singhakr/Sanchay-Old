/*
 * BlogPost.java
 *
 * Created on December 5, 2007, 7:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import sanchay.corpus.CorpusStatistics;
import sanchay.ontology.writing.Tag;
import sanchay.ontology.writing.Categories;
import sanchay.ontology.writing.Category;
import sanchay.ontology.writing.Tags;
import sanchay.xml.dom.SanchayDOMDocument;

/**
 *
 * @author Anil Kumar Singh
 */
public class BlogPost extends BlogEntry implements SanchayDOMDocument {
    
    Categories categories;
    Category bestCategory;
    
    Tags tags;
    Tag bestTag;
    
    BlogComments blogComments;

    /** Creates a new instance of BlogPost */
    public BlogPost() {
        super();

        categories = new Categories();
        tags = new Tags();
        blogComments = new BlogComments();
    }

    public BlogComments getComments()
    {
        return blogComments;
    }

    public void setComments(BlogComments cm)
    {
        blogComments = cm;
    }        

    public Categories getCategories()
    {
        return categories;
    }

    public void setCategories(Categories cs)
    {
        categories = cs;
    }        
    
    public Category getBestCategory()
    {
        return bestCategory;
    }

    public void setBestCategory(Category c)
    {
        bestCategory = c;
    }            
    
    public Category selectBestCategory()
    {
        if(categories == null || categories.countCategories() == 0)
            return null;
        
        // Till an algorithm is designed
        bestCategory = (Category) categories.getCategory(0);
        
        return bestCategory;
    }

    public Tags getTags()
    {
        return tags;
    }

    public void setTags(Tags ts)
    {
        tags = ts;
    }        
    
    public Tag getBestTag()
    {
        return bestTag;
    }

    public void setBestTag(Tag t)
    {
        bestTag = t;
    }            
    
    public Tag selectBestTag()
    {
        if(tags == null || tags.countCategories() == 0)
            return null;
        
        // Till an algorithm is designed
        bestTag = (Tag) tags.getCategory(0);
        
        return bestTag;
    }

    public void readFileHTML(String filePath) throws Exception, FileNotFoundException, IOException            
    {
        
    }

    public void readFileXML(String filePath) throws Exception, FileNotFoundException, IOException            
    {
        
    }
    
    public void print(PrintStream ps)
    {
        ps.println("<BlogPost>\n");
        
//        categories
        
        int pcount = segments.size();
        for (int i = 0; i < pcount; i++)
        {
            
        }

        ps.println("\t\t</BlogPost>\n");
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = (DOMElement) super.getDOMElement();
        
        domElement.setName("BlogPost");

        if(bestCategory != null)
        {
            DOMElement domElementBestCat = bestCategory.getDOMElement();
            domElement.add(domElementBestCat);
        }

        if(categories != null && categories.countCategories() > 0)
        {
            DOMElement domElementCategories = categories.getDOMElement();
            domElement.add(domElementCategories);
        }

        if(bestTag != null)
        {
            DOMElement domElementBestTag = bestTag.getDOMElement();
            domElement.add(domElementBestTag);
        }

        if(tags != null && tags.countCategories() > 0)
        {
            DOMElement domElementTags = tags.getDOMElement();
            domElement.add(domElementTags);
        }

        if(blogComments != null && blogComments.countComments() > 0)
        {
            DOMElement domElementTags = blogComments.getDOMElement();
            domElement.add(domElementTags);
        }

        return domElement;
    }

    public void readXMLFile(String path, String charset) {
    }
    
    public void readXML(org.w3c.dom.Element domElement) {
        super.readXML(domElement);
        
        NodeList elements = domElement.getElementsByTagName("Category");
        bestCategory = new Category();
        
        if(elements.item(0) != null)
            bestCategory.readXML((org.w3c.dom.Element) elements.item(0));
        
        elements = domElement.getElementsByTagName("Categories");
        categories = new Categories();
        
        if(elements.item(0) != null)
            categories.readXML((org.w3c.dom.Element) elements.item(0));
        
        elements = domElement.getElementsByTagName("Tag");
        bestTag = new Tag();
        
        if(elements.item(0) != null)
            bestTag.readXML((org.w3c.dom.Element) elements.item(0));
        
        elements = domElement.getElementsByTagName("Tags");
        tags = new Tags();
        
        if(elements.item(0) != null)
            tags.readXML((org.w3c.dom.Element) elements.item(0));

        elements = domElement.getElementsByTagName("BlogComments");
        
        if(elements.item(0) != null)
            blogComments.readXML((org.w3c.dom.Element) elements.item(0));
    }    

    public CorpusStatistics getStats()
    {
        CorpusStatistics stats = super.getStats();
        
        long parStat = stats.getParagraphs();
        long senStat = stats.getSentences();
        long wrdStat = stats.getWords();
        long charStat = stats.getCharacters();
        
        if(blogComments != null && blogComments.countComments() > 1)
        {
            parStat += blogComments.getStats().getParagraphs();
            senStat += blogComments.getStats().getSentences();
            wrdStat += blogComments.getStats().getWords();
            charStat += blogComments.getStats().getCharacters();
        }
        
        stats.setParagraphs(parStat);
        stats.setSentences(senStat);
        stats.setWords(wrdStat);
        stats.setCharacters(charStat);
        
        return stats;
    }

    // For now, only size in words is implemented
    public void crop(long toSize, int type)
    {
        CorpusStatistics stats = super.getStats();
        
        long parStat = stats.getParagraphs();        
        long senStat = stats.getSentences();
        long wrdStat = stats.getWords();
        long charStat = stats.getCharacters();
                
        if(type == BlogCorpus.SIZE_WORDS && wrdStat > toSize)
        {
            int count = countSegments();
            int stat = 0;
            
            for (int i = 0; i < count; i++)
            {
                stat += getSegment(i).getTextTrim().split("[\\s;,\\.]+").length;
                
                if(stat > toSize)
                {
                    for (int j = i; j < count; j++)
                    {
                        removeSegments(j);
                    }
                    
                    break;
                }
            }
        }
        
        if(blogComments != null && blogComments.countComments() > 1)
        {
            parStat += blogComments.getStats().getParagraphs();
            senStat += blogComments.getStats().getSentences();
            wrdStat += blogComments.getStats().getWords();
            charStat += blogComments.getStats().getCharacters();
            
            if(type == BlogCorpus.SIZE_WORDS && wrdStat > toSize)
            {
                int count = blogComments.countComments();
                int stat = 0;

                for (int i = 0; i < count; i++)
                {
                    stat += blogComments.getComment(i).getText().trim().split("[\\s;,\\.]+").length;

                    if(stat > toSize)
                    {
                        for (int j = i; j < count; j++)
                        {
                            blogComments.removeComment(j);
                        }

                        break;
                    }
                }
            }            
        }
    }
}
