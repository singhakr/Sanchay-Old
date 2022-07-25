/*
 * Blog.java
 *
 * Created on December 5, 2007, 7:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.common.types.BlogType;
import sanchay.corpus.CorpusStatistics;
import sanchay.ontology.writing.Categories;
import sanchay.ontology.writing.Tag;
import sanchay.ontology.writing.Category;
import sanchay.ontology.writing.Tags;
import sanchay.properties.KeyValueProperties;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMDocument;

/**
 *
 * @author Anil Kumar Singh
 */
public class Blog implements SanchayDOMDocument {
    
    String name;
    BlogType blogType;
    
    boolean loadContent;
    
    KeyValueProperties blogProps;
        
    Category bestCategory;
    Categories categories;
    
    Tags tags;
    Tag bestTag;

    // Key is for the blog post (title)
    // Value is a reference to the BlogPost object
    LinkedHashMap blogPosts;
    
    /** Creates a new instance of Blog */
    public Blog() {
        blogPosts = new LinkedHashMap();
    }

    public Blog(KeyValueProperties props)
    {
        blogProps = props;
        blogPosts = new LinkedHashMap();
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        name = n;
    }            

    public BlogType getBlogType()
    {
        return blogType;
    }

    public void setBlogType(BlogType t)
    {
        blogType = t;
    }    
    
    public boolean getLoadContentInMemory()
    {
        return loadContent;
    }

    public void setLoadContentInMemory(boolean l)
    {
        loadContent = l;
    }            
	
    public int countPosts()
    {
            return blogPosts.size();
    }

    public Iterator getPostKeys()
    {
        return blogPosts.keySet().iterator();
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

    public KeyValueProperties getBogProps()
    {
        return blogProps;
    }

    public void setBogProps(KeyValueProperties p)
    {
        blogProps = p;
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

    public void readFilesHTML(String directoryPath) throws Exception, FileNotFoundException, IOException
    {
        System.err.println(GlobalProperties.getIntlString("Reading_directory:_") + directoryPath);
        File file = new File(directoryPath);
        
        if(file.exists() == false)
            return;
        
        File files[] = file.listFiles();
        
        for (int i = 0; i < files.length; i++)
        {
            KeyValueProperties bprops = new KeyValueProperties();
            
            BlogPage blogPage = BlogPage.getInstance(blogType, this);

            blogPage.readFileHTML(files[i].getAbsolutePath());
            
            Enumeration enm = blogPage.getPostKeys();
            
            while(enm.hasMoreElements())
            {
                String pkey = (String) enm.nextElement();
                BlogPost post = blogPage.getPost(pkey);
                
                addPost("" + (i + 1), post);
            }
        }        
    }

    public void htmlToXML(String directoryPath, String nm, BlogType tp,
            boolean inMemory, String outFilePath) throws Exception, FileNotFoundException, IOException
    {
        setName((new File(directoryPath)).getName());
        setBlogType(tp);
        setLoadContentInMemory(inMemory);

        readFilesHTML(directoryPath);
        
        XMLUtils.writeDOM4JXML(getDOMElement(), outFilePath + ".xml");        
    }
    
    public void print(PrintStream ps)
    {
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement("Blog");
        DOMAttribute attribName = new DOMAttribute(new org.dom4j.QName("name"), name);
        domElement.add(attribName);

        DOMAttribute attribType = new DOMAttribute(new org.dom4j.QName("type"), blogType.toString());
        domElement.add(attribType);
        
        if(blogProps != null)
        {
            DOMElement domElementProps = blogProps.getDOMElement();
            domElement.add(domElementProps);
        }
        
        if(bestCategory != null)
        {
            DOMElement domElementBestCat = bestCategory.getDOMElement();
            domElement.add(domElementBestCat);
        }

        if(categories != null)
        {
            DOMElement domElementCategories = categories.getDOMElement();
            domElement.add(domElementCategories);
        }
        
        if(bestTag != null)
        {
            DOMElement domElementBestTag = bestTag.getDOMElement();
            domElement.add(domElementBestTag);
        }

        if(tags != null)
        {
            DOMElement domElementTags = tags.getDOMElement();
            domElement.add(domElementTags);
        }
        
        Iterator itr = getPostKeys();

        String blogText = "";

        while(itr.hasNext())
        {
            String pkey = (String) itr.next();
            BlogPost post = getPost(pkey);

            DOMElement domElementPost = post.getDOMElement();
            domElement.add(domElementPost);

            blogText += domElementPost.getText().trim();
        }
        
        if(blogText.equals(""))
            System.out.println(name);
        
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
            System.err.println(GlobalProperties.getIntlString("Parsing:_") + path);
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
                BlogPost post = new BlogPost();
                
                post.readXMLFile(files[i].getAbsolutePath(), charset);
                
                addPost(post.getTitle().getTitle() + "::" + post.getTitle().getSubTitle(), post);
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
        
        elements = domElement.getElementsByTagName("Category");
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

        elements = domElement.getElementsByTagName("BlogPost");
        
        int count = elements.getLength();
        
        for (int i = 0; i < count; i++)
        {
            BlogPost post = new BlogPost();
            post.readXML((org.w3c.dom.Element) elements.item(i));
            
            if(post.getTitle() == null || post.getTitle().getTitle() == null)
                addPost(name + "-post-" + (i + 1), post);
            else
                addPost(post.getTitle().getTitle() + "::" + post.getTitle().getSubTitle(), post);
        }
    }
    
    public CorpusStatistics getStats()
    {
        CorpusStatistics stats = new CorpusStatistics();
        
        int parStat = 0;
        int senStat = 0;
        int wrdStat = 0;
        int charStat = 0;

        Iterator itr = getPostKeys();
        
        while(itr.hasNext())
        {
            String key = (String) itr.next();
            BlogPost post = (BlogPost) getPost(key);
            
            CorpusStatistics st = post.getStats();
            
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
    public void crop(long toSize, int type)
    {
        CorpusStatistics stats = getStats();
        
        long parStat = stats.getParagraphs();        
        long senStat = stats.getSentences();
        long wrdStat = stats.getWords();
        long charStat = stats.getCharacters();
                
        if(type == BlogCorpus.SIZE_WORDS && wrdStat > toSize)
        {
            int stat = 0;

            Iterator itr = getPostKeys();

            while(itr.hasNext())
            {
                String key = (String) itr.next();
                BlogPost post = (BlogPost) getPost(key);

                stat += post.getText().trim().split("[\\s;,\\.]+").length;
                
                if(stat > toSize)
                {
                    removePost(key);
                }
            }
        }
    }
}
