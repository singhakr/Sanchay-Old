/*
 * HuffingtonPostBlog.java
 *
 * Created on December 10, 2007, 6:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.dom4j.dom.DOMElement;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.LinkStringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.NodeList;
import sanchay.GlobalProperties;
import sanchay.ontology.writing.Author;
import sanchay.ontology.writing.Category;
import sanchay.ontology.writing.Time;
import sanchay.ontology.writing.Title;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author Anil Kumar Singh
 */
public class HuffingtonPostBlogPage extends BlogPage implements SanchayDOMElement {
    
    /** Creates a new instance of HuffingtonPostBlog */
    public HuffingtonPostBlogPage()
    {
    }    

    public HuffingtonPostBlogPage(Blog b)
    {
        blog = b;
    }
    
    public void readFileXML() throws Exception, FileNotFoundException, IOException
    {
        
    }
    
    public void readFileHTML(String htmlFilePath) throws Exception, FileNotFoundException, IOException
    {
        System.err.println(GlobalProperties.getIntlString("\tReading_file:_") + htmlFilePath);
        org.htmlparser.Parser parser = new org.htmlparser.Parser(htmlFilePath);
        
        TagNameFilter divTagFilter = new TagNameFilter("div");
        TagNameFilter aTagFilter = new TagNameFilter("a");
        TagNameFilter metaTagFilter = new TagNameFilter("meta");
        TagNameFilter bodyTagFilter = new TagNameFilter("body");
 
        HasAttributeFilter authorAttribFilter = new HasAttributeFilter("name", "author");
        HasAttributeFilter timeAttribFilter = new HasAttributeFilter("name", "publish_date");
        HasAttributeFilter titleAttribFilter = new HasAttributeFilter("id", "title_permalink");

        HasAttributeFilter contentAttribFilter = new HasAttributeFilter("class", "blog_content");
        HasAttributeFilter entryBodyAttribFilter = new HasAttributeFilter("id", "entry_body");

//        HasAttributeFilter commentsAttribFilter = new HasAttributeFilter("id", "comments");
        HasAttributeFilter commentAttribFilter = new HasAttributeFilter("class", "cmt_txt_wrap");
        LinkStringFilter commentAuthorStringFilter = new LinkStringFilter("/users/profile/");

        AndFilter authorFilter = new AndFilter(new NodeFilter[] {metaTagFilter, authorAttribFilter} );
        AndFilter timeFilter = new AndFilter(new NodeFilter[] {metaTagFilter, timeAttribFilter} );
        AndFilter titleFilter = new AndFilter(new NodeFilter[] {aTagFilter, titleAttribFilter} );
        AndFilter contentFilter = new AndFilter(new NodeFilter[] {divTagFilter, contentAttribFilter, entryBodyAttribFilter} );

//        AndFilter commentsFilter = new AndFilter(new NodeFilter[] {divTagFilter, commentsAttribFilter} );
        AndFilter commentFilter = new AndFilter(new NodeFilter[] {divTagFilter, commentAttribFilter} );
        AndFilter commentAuthorFilter = new AndFilter(new NodeFilter[] {aTagFilter, commentAuthorStringFilter} );

        BlogPost post = new BlogPost();
        addPost("1", post);
        
        // Extract categories
        NodeList bodyElements = parser.extractAllNodesThatMatch(bodyTagFilter);
        org.htmlparser.Tag bodyTag = (org.htmlparser.Tag) bodyElements.elementAt(0);

        String cat = bodyTag.getAttribute("id");

        if(cat != null && cat.equals("") == false)
            post.getCategories().addCategory(new Category(cat));

        cat = bodyTag.getAttribute("class");

        if(cat != null && cat.equals("") == false)
            post.getCategories().addCategory(new Category(cat));
        
        // Extracting meta info
        parser = new org.htmlparser.Parser(htmlFilePath);

        NodeList extractedElements = parser.extractAllNodesThatMatch(metaTagFilter);
        
        // Extracting author name
        NodeList metaElements = extractedElements.extractAllNodesThatMatch(authorFilter, true);
        
        org.htmlparser.Tag metaTag = (org.htmlparser.Tag) metaElements.elementAt(0);
        
        if(metaTag != null)
            post.setAuthor(new Author(metaTag.getAttribute("content")));
        
        // Extracting time
        metaElements = extractedElements.extractAllNodesThatMatch(timeFilter, true);
        
        metaTag = (org.htmlparser.Tag) metaElements.elementAt(0);

        if(metaTag != null)
            post.setTime(new Time(metaTag.getAttribute("content")));
        
        // Extracting title
        extractedElements = bodyElements.extractAllNodesThatMatch(titleFilter, true);
        
        org.htmlparser.Tag extractedTag = (org.htmlparser.Tag) extractedElements.elementAt(0);
        post.setTitle(new Title(extractedTag.toPlainTextString().trim()));
        
        // Extracting post content
        extractedElements = bodyElements.extractAllNodesThatMatch(contentFilter, true);
        
        extractedTag = (org.htmlparser.Tag) extractedElements.elementAt(0);
        post.setText(extractedTag.toPlainTextString().trim());
        
        // Extracting comments
        extractedElements = bodyElements.extractAllNodesThatMatch(commentFilter, true);

        int count = extractedElements.size();        
        
        for(int i = 0; i < count; i++)
        {
            org.htmlparser.Node extractedElement = (org.htmlparser.Node) extractedElements.elementAt(i);

            // Extracting comment text
            if(extractedElement != null)
            {
                BlogComment cmt = new BlogComment();
                cmt.setText( ((CompositeTag) extractedElement).toPlainTextString().trim());
                post.getComments().addComment(cmt);
                
                org.htmlparser.Node prevElement = (org.htmlparser.Node) extractedElement.getPreviousSibling().getPreviousSibling();

                if(prevElement != null)
                {
                    NodeList innerElements = prevElement.getChildren().extractAllNodesThatMatch(commentAuthorFilter);        
                    org.htmlparser.Tag innerTag = (org.htmlparser.Tag) innerElements.elementAt(0);
                    
                    if(innerTag != null)
                       cmt.setAuthor(new Author(innerTag.toPlainTextString().trim()));
                }
            }
        }
    }    

    public void readXMLFile(String path, String charset) {
    }

    public void readXML(DOMElement domElement) {
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Properties systemSettings = System.getProperties();
        systemSettings.put("http.proxyHost", "192.168.36.204");
        systemSettings.put("http.proxyPort", "8080");
        System.setProperties(systemSettings);        

        HuffingtonPostBlogPage page = new HuffingtonPostBlogPage();
        try {
            page.readFileHTML("E:\\blog-corpus\\huffingtonpost-blogs-rearranged\\al-giordano\\what-if-bill-clinton-had-_b_66524.html-index.html");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        String xmlURL = "F:\\Temp\\tmp.xml";
        XMLUtils.writeDOM4JXML(page.getDOMElement(), xmlURL);        
    }
}
