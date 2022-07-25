/*
 * WebCorpus.java
 *
 * Created on November 10, 2007, 2:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sanchay.common.types.BlogType;
import sanchay.corpus.CorpusStatistics;
import sanchay.properties.KeyValueProperties;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMDocument;

/**
 *
 * @author Anil Kumar Singh
 */
public class BlogCorpus implements SanchayDOMDocument {
    
    String name;
    BlogType blogType;
    
    boolean loadContent;
    
    // Key is the blogname (as in the domain name)
    // Value is the path to the blog key value properties file 
    KeyValueProperties blogProps;

    // Key is for the blog (title)
    // Value is a reference to the Blog object
    LinkedHashMap blogs;
    
    public static final int SIZE_PARAGRAPHS = 1;
    public static final int SIZE_SENTENCES = 2;
    public static final int SIZE_WORDS = 3;
    public static final int SIZE_CHARACTERS = 4;
    
    /** Creates a new instance of WebCorpus */
    public BlogCorpus() {
        blogProps = new KeyValueProperties();
        blogs = new LinkedHashMap();
    }

    public KeyValueProperties getBogProperties()
    {
        return blogProps;
    }

    public void setBlogProperties(KeyValueProperties p)
    {
        blogProps = p;
    }    

    public BlogType getBlogType()
    {
        return blogType;
    }

    public void setBlogType(BlogType t)
    {
        blogType = t;
    }    
    
    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        name = n;
    }            
    
    public boolean getLoadContentInMemory()
    {
        return loadContent;
    }

    public void setLoadContentInMemory(boolean l)
    {
        loadContent = l;
    }            
	
    public int countBlogs()
    {
            return blogs.size();
    }

    public Iterator getBlogKeys()
    {
            return blogs.keySet().iterator();
    }

    public Blog getBlog(String p /* Post key */)
    {
            return (Blog) blogs.get(p);
    }

    public int addBlog(String k /* Post key */, Blog p /* property value */)
    {
            blogs.put(k, p);
            return blogs.size();
    }

    public Blog removeBlog(String p /* Post key */)
    {
            return (Blog) blogs.remove(p);
    }

    public void readFilesHTML(String directoryPath) throws Exception, FileNotFoundException, IOException
    {
        File file = new File(directoryPath);
        
        if(file.exists() == false)
            return;
        
        File files[] = file.listFiles();
        
        for (int i = 0; i < files.length; i++)
        {
            KeyValueProperties bprops = new KeyValueProperties();
            Blog blog = new Blog(bprops);
            
            blog.setName(files[i].getName());
            blog.setBlogType(blogType);
            blog.setLoadContentInMemory(loadContent);
            blog.readFilesHTML(files[i].getAbsolutePath());
            
            addBlog(files[i].getName(), blog);
        }
    }

    public void htmlToXMLInOneGo(String directoryPath, String nm, BlogType tp,
            boolean inMemory, String outFilePath) throws Exception, FileNotFoundException, IOException
    {
        setName(nm);
        setBlogType(tp);
        setLoadContentInMemory(inMemory);

        readFilesHTML(directoryPath);
        
        XMLUtils.writeDOM4JXML(getDOMElement(), outFilePath);        
    }

    public void htmlToXML(String directoryPath, String nm, BlogType tp,
            boolean inMemory, String outDirPath) throws Exception, FileNotFoundException, IOException
    {
        File file = new File(directoryPath);
        File outFile = new File(outDirPath);
        
        if(file.exists() == false)
            return;

        if(outFile.exists() == false)
            outFile.mkdirs();
        
        File files[] = file.listFiles();
        
        for (int i = 0; i < files.length; i++)
        {
            setName(nm);
            setBlogType(tp);
            setLoadContentInMemory(inMemory);

            Blog blog = new Blog();
            
            File blogOutFile = new File(outFile, files[i].getName());
            
            blog.htmlToXML(files[i].getAbsolutePath(), name, blogType, loadContent, blogOutFile.getAbsolutePath());
        }
    }
    
    public void print(PrintStream ps)
    {
//        Enumeration enm = blogs.getPropertyKeys();
//        
//        ps.println("<BlogCorpus type=\"" + blogType + "\">\n");
//        
//        while(enm.hasMoreElements())
//        {
//            String bkey = (String) enm.nextElement();
//            String bval = blogs.getPropertyValue(bkey);
//
//            SanchayTableModel blogProps = null;
//            
//            try {
//                blogProps = new SanchayTableModel(bval, "UTF-8");
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//           
//            Blog blog = new Blog(blogProps);
//            
//            blog.print(ps);
//        }
//
//        ps.println("</BlogCorpus>\n");
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement("BlogCorpus");
        
        DOMAttribute attribType = new DOMAttribute(new org.dom4j.QName("type"), blogType.toString());
        DOMAttribute attribName = new DOMAttribute(new org.dom4j.QName("name"), name);

        domElement.add(attribType);
        domElement.add(attribName);
        
        DOMElement domElementBlogProperties = blogProps.getDOMElement();
        domElement.add(domElementBlogProperties);
        
        Iterator itr = blogs.keySet().iterator();
        
        while(itr.hasNext())
        {
            String bkey = (String) itr.next();
            Blog blog = (Blog) blogs.get(bkey);

            DOMElement domElementBlog = blog.getDOMElement();                    
            domElement.add(domElementBlog);
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

    public void readXMLFile(String path, String charset) {
        File inFile = new File(path);
        
        if(inFile.isFile())
        {
            try {
                System.err.println("Parsing: " + path);
                org.w3c.dom.Element rootElement = XMLUtils.parseDomXML(path);
                readXML(rootElement);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            }            
        }
        else if(inFile.isDirectory())
        {
            File files[] = inFile.listFiles();
            
            for (int i = 0; i < files.length; i++)
            {
                Blog blog = new Blog();
                
                blog.readXMLFile(files[i].getAbsolutePath(), charset);
                
                addBlog(blog.getName(), blog);
            }
        }
    }

    public void readXML(org.w3c.dom.Element domElement) {
        name = domElement.getAttribute("name");
        blogType = (BlogType) BlogType.findFromId(domElement.getAttribute("type"));

        NodeList elements = domElement.getElementsByTagName("KeyValueProperties");
        blogProps = new KeyValueProperties();
        
        if(elements.item(0) != null)
            blogProps.readXML((org.w3c.dom.Element) elements.item(0));

        elements = domElement.getElementsByTagName("Blog");
        
        int count = elements.getLength();
        
        for (int i = 0; i < count; i++)
        {
            Blog blog = new Blog();
            blog.readXML((org.w3c.dom.Element) elements.item(i));
            addBlog(blog.getName(), blog);
        }        
    }
    
    public CorpusStatistics getStats()
    {
        CorpusStatistics stats = new CorpusStatistics();
        
        int parStat = 0;
        int senStat = 0;
        int wrdStat = 0;
        int charStat = 0;

        Iterator itr = getBlogKeys();
        
        while(itr.hasNext())
        {
            String key = (String) itr.next();
            Blog blog = (Blog) getBlog(key);
            
            CorpusStatistics st = blog.getStats();
            
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

    // For now, only size in words is implemented
    public void crop(long toCorpusSize, long toBlogSize, int type)
    {
        Iterator itr = getBlogKeys();
        
        while(itr.hasNext())
        {
            String key = (String) itr.next();
            Blog blog = (Blog) getBlog(key);
            blog.crop(toBlogSize, type);
        }
        
        CorpusStatistics stats = getStats();
        
        long parStat = stats.getParagraphs();        
        long senStat = stats.getSentences();
        long wrdStat = stats.getWords();
        long charStat = stats.getCharacters();
                
        if(type == BlogCorpus.SIZE_WORDS && wrdStat > toCorpusSize)
        {
            int stat = 0;

            itr = getBlogKeys();

            while(itr.hasNext())
            {
                String key = (String) itr.next();
                Blog blog = (Blog) getBlog(key);

                stat += blog.getStats().getWords();
                
                if(stat > toCorpusSize)
                {
                    removeBlog(key);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
//        BlogCorpus blogCorpus = new BlogCorpus();
//        blogCorpus.setName("WordPress");
//        blogCorpus.setBlogType(BlogType.WORD_PRESS);
//        blogCorpus.setLoadContentInMemory(true);
//        
//        try {
//            blogCorpus.readFilesHTML("F:\\docs\\corpus-building\\lexmasterclass\\sanchay-site-capture\\wordpress-blogs");
//            } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        
//        String xmlURL = "F:\\Temp\\word-press-blog-corpus.xml";
//        XMLUtils.writeDOM4JXML(blogCorpus.getDOMElement(), xmlURL);        

//        BlogCorpus blogCorpus = new BlogCorpus();
//
//        String name = "HuffingtonPost";
//        BlogType type = BlogType.HUFFINGTON_POST;
//        boolean inMemory = true;
//        String corpusPath = "E:\\blog-corpus\\huffingtonpost-blogs-rearranged";
//        String outPath = "F:\\Temp\\huffingtonpost-blog-corpus";
//        
//        try {
//            blogCorpus.htmlToXML(corpusPath, name, type, inMemory, outPath);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        BlogCorpus blogCorpus = new BlogCorpus();
        
//        String name = "WordPress";
//        BlogType type = BlogType.WORD_PRESS;
//        boolean inMemory = true;
//        String corpusPath = "E:\\blog-corpus\\wordpress-blogs-new";
//        String outPath = "F:\\Temp\\word-press-blog-corpus-2a";
        
        try {
//            blogCorpus.htmlToXML(corpusPath, name, type, inMemory, outPath);
            blogCorpus.readXMLFile("E:\\blog-corpus\\word-press-blog-corpus", "UTF-8");
            CorpusStatistics stats = blogCorpus.getStats();
            
            stats.print(System.out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
