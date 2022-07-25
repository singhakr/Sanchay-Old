/*
 * BloggerBlog.java
 *
 * Created on December 10, 2007, 6:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.dom4j.dom.DOMElement;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.NodeList;
import sanchay.GlobalProperties;
import sanchay.ontology.writing.Category;
import sanchay.ontology.writing.SanchayURL;
import sanchay.ontology.writing.Title;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author Anil Kumar Singh
 */
public class BloggerBlogPage extends BlogPage implements SanchayDOMElement 
{    
    /** Creates a new instance of BloggerBlog */
    public BloggerBlogPage()
    {
    }

    public BloggerBlogPage(Blog b)
    {
        blog = b;
    }
        
    public void readFileXML(String htmlFilePath) throws Exception, FileNotFoundException, IOException
    {
//        htmlRootElement = XMLUtils.parseDomXML(htmlFilePath);
//        
//        Vector postElements = XMLUtils.getElementsByTagAndAttribValue(htmlRootElement, "div", "class", "post");
//
//        int pcount = postElements.size();
//        
//        for(int i = 0; i < pcount; i++)
//        {
//            BlogPost post = new BlogPost();
//            org.w3c.dom.Element postElement = (org.w3c.dom.Element) postElements.get(i);
//            
//            Vector innerElements = XMLUtils.getElementsByAttribValue(postElement, "class", "post");
//            post.setTitle( ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim());
//            
//            innerElements = XMLUtils.getElementsByAttribValue(postElement, "class", "date-header");
//
//            String dateStr = ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim();
////            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd @ hh:mm");
////            rptDate = sdf.format(date); System.out.println(rptDate+"\n");
//            Date date = new Date(dateStr);
//            post.setDate(date);
//
//            String text = "";
//            innerElements = XMLUtils.getElementsByAttribValue(postElement, "class", "post-body");
//            post.setText( ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim());
//            
//            innerElements = XMLUtils.getElementsByTagAndAttribValue(postElement, "a", "title", "permanent link");
//            post.setURL( ((org.w3c.dom.Element) innerElements.get(0)).getAttribute("title").trim());
//        }                
    }    

    public void readFileHTML(String htmlFilePath) throws Exception, FileNotFoundException, IOException
    {
        System.err.println(GlobalProperties.getIntlString("\tReading_file:_") + htmlFilePath);
        org.htmlparser.Parser parser = new org.htmlparser.Parser(htmlFilePath);
        
        TagNameFilter divTagFilter = new TagNameFilter("div");
        TagNameFilter aTagFilter = new TagNameFilter("a");
        TagNameFilter h2TagFilter = new TagNameFilter("h2");
 
        HasAttributeFilter bookmarkFilter = new HasAttributeFilter("rel", "bookmark");
        AndFilter titleFilter1 = new AndFilter(new NodeFilter[] {aTagFilter, bookmarkFilter} );
        
        HasParentFilter h2ParentFilter = new HasParentFilter(h2TagFilter);
        AndFilter titleFilter2 = new AndFilter(new NodeFilter[] {aTagFilter, h2ParentFilter} );

        OrFilter titleFilter = new OrFilter(new NodeFilter[] {titleFilter1, titleFilter2} );
        
        HasAttributeFilter categoryFilter = new HasAttributeFilter("rel", "category tag");
        HasAttributeFilter tagFilter = new HasAttributeFilter("rel", "tag");
        HasAttributeFilter textFilter1 = new HasAttributeFilter("class", "snap_preview");        
        HasAttributeFilter textFilter2 = new HasAttributeFilter("class", "entry snap_preview");        
        
        AndFilter aCategoriesFilter = new AndFilter(new NodeFilter[] {aTagFilter, categoryFilter} );
        AndFilter aTagsFilter = new AndFilter(new NodeFilter[] {aTagFilter, tagFilter} );

        HasChildFilter categoriesParentFilter = new HasChildFilter(aCategoriesFilter);
        HasChildFilter tagsParentFilter = new HasChildFilter(aTagsFilter);

        OrFilter textFilter = new OrFilter(new NodeFilter[] {textFilter1, textFilter2} );
        AndFilter divTextFilter = new AndFilter(new NodeFilter[] {divTagFilter, textFilter} );
        
        // Extract text
        NodeList extractedElements = parser.extractAllNodesThatMatch(divTextFilter);
              
        int count = extractedElements.size();        
        
        for(int i = 0; i < count; i++)
        {
            BlogPost post = new BlogPost();
            org.htmlparser.Node extractedElement = (org.htmlparser.Node) extractedElements.elementAt(i);

            if(extractedElement != null)
                post.setText( ((CompositeTag) extractedElement).toPlainTextString().trim());
            
            // Extract titles
            org.htmlparser.Node parentNode = extractedElement.getParent();
            NodeList innerElements = parentNode.getChildren().extractAllNodesThatMatch(titleFilter, true);

            if(innerElements==null || innerElements.size() != count || innerElements.elementAt(i) == null)
                ;
            else
                post.setTitle(new Title(innerElements.elementAt(0).toPlainTextString().trim() ) );
            
            if(innerElements==null || innerElements.elementAt(0) == null)
                ;
            else
                post.setURL(new SanchayURL( ((org.htmlparser.Tag) innerElements.elementAt(0)).getAttribute("href")) );

            // Extract categories and tags
            parentNode = extractedElement.getParent();
            innerElements = parentNode.getChildren().extractAllNodesThatMatch(aCategoriesFilter, true);

            int ccount = innerElements.size();            
            for (int j = 0; j < ccount; j++)
            {
                org.htmlparser.Node categoryElement = innerElements.elementAt(j);                
                String catText = categoryElement.toPlainTextString().trim();
                post.getCategories().addCategory(new Category(catText));
            }

            parentNode = extractedElement.getParent();
            innerElements = parentNode.getChildren().extractAllNodesThatMatch(aTagsFilter, true);

            int tcount = innerElements.size();            
            for (int j = 0; j < tcount; j++)
            {
                org.htmlparser.Node tagElement = innerElements.elementAt(j);                
                String tagText = tagElement.toPlainTextString().trim();
                post.getCategories().addCategory(new Category(tagText));
            }

            addPost( (""+ i), post);
        }        
    }

    public void readXMLFile(String path, String charset) {
    }

    public void readXML(DOMElement domElement) {
    }
}
