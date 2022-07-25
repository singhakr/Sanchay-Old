/*
 * WebCorpusFile.java
 *
 * Created on November 10, 2007, 2:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import sanchay.common.types.BlogType;
import sanchay.xml.dom.SanchayDOMDocument;

/**
 *
 * @author Anil Kumar Singh
 */
public class BlogPage implements SanchayDOMDocument {

    // Key is for the blog post (title)
    // Value is a reference to the BlogPost object
    Hashtable blogPosts;
    
    org.w3c.dom.Element htmlRootElement;
      
    Blog blog;
  
    /** Creates a new instance of WebCorpusFile */
    public BlogPage()
    {
        blogPosts = new Hashtable();
    }

    public BlogPage(Blog b)
    {
        blogPosts = new Hashtable();
        blog = b;
    }
    
    public Blog getBlog()
    {
        return blog;
    }
    
    public void setBlog(Blog b)
    {
        blog = b;
    }
	
    public int countPosts()
    {
            return blogPosts.size();
    }

    public Enumeration getPostKeys()
    {
            return blogPosts.keys();
    }

    public BlogPost getPost(String p /* Post key */)
    {
            return (BlogPost) blogPosts.get(p);
    }

    public int addPost(String k /* Post key */, BlogPost p /* property value */)
    {
            blogPosts.put(k, p);
            return blogPosts.size();
    }

    public BlogPost removePost(String p /* Post key */)
    {
            return (BlogPost) blogPosts.remove(p);
    }
    
    public void readFileXML(String htmlFilePath) throws Exception, FileNotFoundException, IOException
    {
    }

    public void readFileHTML(String htmlFilePath) throws Exception, FileNotFoundException, IOException
    {
        
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement("BlogPage");
//        DOMAttribute attribBlog = new DOMAttribute(domElement, new org.dom4j.QName("blog"), blog.getName());

        Enumeration enm = blogPosts.keys();
        
        while(enm.hasMoreElements())
        {
            String key = (String) enm.nextElement();
            BlogPost post = (BlogPost) blogPosts.get(key);
            
            domElement.add(post.getDOMElement());
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
    
    public static BlogPage getInstance(BlogType type, Blog b)
    {
        if(type == BlogType.WORD_PRESS)
            return new WordPressBlogPage(b);
        else if(type == BlogType.BLOGGER)
            return new BloggerBlogPage(b);
        else if(type == BlogType.HUFFINGTON_POST)
            return new HuffingtonPostBlogPage(b);
        
        return null;
    }

    public void readXMLFile(String path, String charset) {
    }

    public void readXML(org.w3c.dom.Element domElement) {
        NodeList elements = domElement.getElementsByTagName("BlogPost");
        
        int count = elements.getLength();
        
        for (int i = 0; i < count; i++)
        {
            BlogPost post = new BlogPost();
            post.readXML((org.w3c.dom.Element) elements.item(i));
            addPost(post.getTitle().getTitle() + "::" + post.getTitle().getSubTitle(), post);
        }
    }
}
