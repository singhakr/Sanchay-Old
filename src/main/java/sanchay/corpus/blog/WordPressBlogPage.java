/*
 * WordPressBlog.java
 *
 * Created on December 10, 2007, 6:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
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
import sanchay.ontology.writing.Tag;
import sanchay.ontology.writing.Category;
import sanchay.ontology.writing.SanchayURL;
import sanchay.ontology.writing.Time;
import sanchay.ontology.writing.Title;
import sanchay.util.UtilityFunctions;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author Anil Kumar Singh
 */
public class WordPressBlogPage extends BlogPage implements SanchayDOMElement {
    
    /** Creates a new instance of WordPressBlog */
    public WordPressBlogPage()
    {
        super();
    }

    public WordPressBlogPage(Blog b)
    {
        super(b);
    }
    
    public void readFileXML(String htmlFilePath) throws Exception, FileNotFoundException, IOException
    {
        UtilityFunctions.replaceInFileInPlace("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd",
                "F:/software/loose.dtd", new File(htmlFilePath), "UTF-8");

        UtilityFunctions.replaceInFileInPlace("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd",
                "F:/software/loose.dtd", new File(htmlFilePath), "UTF-8");
        
        XMLUtils.correctlyEndTagsInPlace(new File(htmlFilePath), "UTF-8");
        
        htmlRootElement = XMLUtils.parseDomXML(htmlFilePath);
        
        Vector postElements = XMLUtils.getElementsByTagAndAttribValue(htmlRootElement, "div", "class", "post");

        int pcount = postElements.size();        
        for(int i = 0; i < pcount; i++)
        {
            BlogPost post = new BlogPost();
            org.w3c.dom.Element postElement = (org.w3c.dom.Element) postElements.get(i);
            
            Vector innerElements = XMLUtils.getElementsByTagAndAttribValue(postElement, "a", "rel", "bookmark");
            post.setTitle(new Title( ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim() ) );

            post.setURL(new SanchayURL(((org.w3c.dom.Element) innerElements.get(0)).getAttribute("href")) );
            
            innerElements = XMLUtils.getElementsByTagAndAttribValue(postElement, "div", "class", "cite");

            String citeText = "";
            
            if(innerElements.size() > 0)
            {
                citeText = ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim();

                Vector categoryElements = XMLUtils.getElementsByTagAndAttribValue(((org.w3c.dom.Element) innerElements.get(0)), "a", "rel", "category tag");

                int ccount = categoryElements.size();            
                for (int j = 0; j < ccount; j++)
                {
                    org.w3c.dom.Element categoryElement = (org.w3c.dom.Element) categoryElements.get(j);                
                    String catText = categoryElement.getTextContent().trim();
                    post.getCategories().addCategory(new Category(catText));

                    citeText = citeText.replaceAll(catText, "");
                }

                Vector tagElements = XMLUtils.getElementsByTagAndAttribValue(((org.w3c.dom.Element) innerElements.get(0)), "a", "rel", "tag");

                int tcount = tagElements.size();            
                for (int j = 0; j < tcount; j++)
                {
                    org.w3c.dom.Element tagElement = (org.w3c.dom.Element) tagElements.get(j);                
                    String tagText = tagElement.getTextContent().trim();
                    post.getTags().addCategory((new Tag(tagText)));

                    citeText = citeText.replaceAll(tagText, "");
                }

                citeText = citeText.replaceAll(GlobalProperties.getIntlString("Filed_under:_"), "");
                citeText = citeText.replaceAll("[\\s]*\\|[\\s]*Tags: ", "");
                citeText = citeText.replaceAll("[,]", " ");
                citeText = citeText.replaceAll("[\n]", " ");

//                Pattern p = Pattern.compile("(?dum)&#8212; ([^@^\\s]+) @");
//                Matcher m = p.matcher(citeText);
//
//                post.setAuthor(new Author(m.group(1)));
//
//                citeText = citeText.replaceAll(m.group(1), "");

    //            String time = ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim();
    //            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd @ hh:mm");
    //            rptDate = sdf.format(date); System.out.println(rptDate+"\n");
    //            Date date = new Date(dateStr);
                post.setTime(new Time( citeText.trim() ) ) ;
            }

            String text = "";
            innerElements = XMLUtils.getElementsByAttribValue(postElement, "class", "snap_preview");

            if(innerElements.size() > 0)
                post.setText( ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim());
            
            addPost( (""+ i), post);
        }                
    }    
    
    public void readFileHTML(String htmlFilePath) throws Exception, FileNotFoundException, IOException
    {
        System.err.println(GlobalProperties.getIntlString("\tReading_file:_") + htmlFilePath);
        org.htmlparser.Parser parser = new org.htmlparser.Parser(htmlFilePath);
        
        TagNameFilter divTagFilter = new TagNameFilter("div");
        TagNameFilter aTagFilter = new TagNameFilter("a");
 
        HasAttributeFilter idFilter = new HasAttributeFilter("class", "id");
        HasAttributeFilter postFilter = new HasAttributeFilter("class", "post");
        HasAttributeFilter titleFilter = new HasAttributeFilter("rel", "bookmark");
        HasAttributeFilter citeFilter = new HasAttributeFilter("class", "cite");
        HasAttributeFilter categoryFilter = new HasAttributeFilter("rel", "category tag");
        HasAttributeFilter tagFilter = new HasAttributeFilter("rel", "tag");
        HasAttributeFilter textFilter1 = new HasAttributeFilter("class", "snap_preview");        
        HasAttributeFilter textFilter2 = new HasAttributeFilter("class", "entry snap_preview");        
        
        HasAttributeFilter entryFilter = new HasAttributeFilter("class", "entry");
        
        HasAttributeFilter entryHeadFilter = new HasAttributeFilter("class", "entry-head");
        HasAttributeFilter entryContentFilter = new HasAttributeFilter("class", "entry-content");

        AndFilter divEntryFilter = new AndFilter(new NodeFilter[] {divTagFilter, entryFilter} );

        AndFilter hasHeadNContentFilter = new AndFilter(new NodeFilter[] {entryHeadFilter, entryContentFilter} );
        HasChildFilter hasEntryFilter = new HasChildFilter(hasHeadNContentFilter, true);
        
//        Vector postElements = XMLUtils.getElementsByTagAndAttribValue(htmlRootElement, "div", "class", "post");
        NodeFilter divPostFilter = new AndFilter(new NodeFilter[] {divTagFilter, postFilter} );
        
        OrFilter mainPostFilter = new OrFilter(new NodeFilter[] {hasEntryFilter, divPostFilter, divEntryFilter} );

        NodeList postElements = null;
        
        try
        {
            postElements = parser.extractAllNodesThatMatch(mainPostFilter);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        int pcount = postElements.size();        
        for(int i = 0; i < pcount; i++)
        {
            BlogPost post = new BlogPost();
            org.htmlparser.Node postElement = (org.htmlparser.Node) postElements.elementAt(i);
            
//            Vector innerElements = XMLUtils.getElementsByTagAndAttribValue(postElement, "a", "rel", "bookmark");
            NodeFilter aTitleFilter = new AndFilter(new NodeFilter[] {aTagFilter, titleFilter} );
            NodeList innerElements = postElement.getChildren().extractAllNodesThatMatch(aTitleFilter, true);
            
            if(innerElements==null || innerElements.elementAt(0) == null)
                ;
            else
                post.setTitle(new Title(innerElements.elementAt(0).toPlainTextString().trim() ) );
            
            if(innerElements==null || innerElements.elementAt(0) == null)
                ;
            else
                post.setURL(new SanchayURL( ((org.htmlparser.Tag) innerElements.elementAt(0)).getAttribute("href")) );
            
//            innerElements = XMLUtils.getElementsByTagAndAttribValue(postElement, "div", "class", "cite");
//            NodeFilter divCiteFilter = new AndFilter(new NodeFilter[] {divTagFilter, citeFilter} );
//            innerElements = postElement.getChildren().extractAllNodesThatMatch(divCiteFilter, true);

//            String citeText = "";
            
//            if(innerElements.size() > 0)
//            {
//                org.htmlparser.Node innerElement = innerElements.elementAt(0);
//                citeText = innerElement.toPlainTextString().trim();
//                citeText = postElement.toPlainTextString().trim();

//                Vector categoryElements = XMLUtils.getElementsByTagAndAttribValue(((org.w3c.dom.Element) innerElements.get(0)), "a", "rel", "category tag");
                NodeFilter categoriesFilter = new AndFilter(new NodeFilter[] {aTagFilter, categoryFilter} );
//                NodeList categoryElements = innerElement.getChildren().extractAllNodesThatMatch(categoriesFilter, true);
                NodeList categoryElements = postElement.getChildren().extractAllNodesThatMatch(categoriesFilter, true);

                int ccount = categoryElements.size();            
                for (int j = 0; j < ccount; j++)
                {
                    org.htmlparser.Node categoryElement = categoryElements.elementAt(j);                
                    String catText = categoryElement.toPlainTextString().trim();
                    post.getCategories().addCategory(new Category(catText));

//                    citeText = citeText.replaceAll(catText, "");
                }

//                Vector tagElements = XMLUtils.getElementsByTagAndAttribValue(((org.w3c.dom.Element) innerElements.get(0)), "a", "rel", "tag");
                NodeFilter tagsFilter = new AndFilter(new NodeFilter[] {aTagFilter, tagFilter} );
//                NodeList tagElements = innerElement.getChildren().extractAllNodesThatMatch(tagsFilter, true);
                NodeList tagElements = postElement.getChildren().extractAllNodesThatMatch(tagsFilter, true);

                int tcount = tagElements.size();            
                for (int j = 0; j < tcount; j++)
                {
                    org.htmlparser.Node tagElement = tagElements.elementAt(j);                
                    String tagText = tagElement.toPlainTextString().trim();
                    post.getTags().addCategory((new Tag(tagText)));

//                    citeText = citeText.replaceAll(tagText, "");
                }

//                citeText = citeText.replaceAll("Filed under: ", "");
//                citeText = citeText.replaceAll("Filed under: ", "");
//                citeText = citeText.replaceAll("[\\s]*\\|[\\s]*Tags: ", "");
//                citeText = citeText.replaceAll("[,]", " ");
//                citeText = citeText.replaceAll("[\n]", " ");

//                Pattern p = Pattern.compile("(?dum)&#8212; ([^@^\\s]+) @");
//                Matcher m = p.matcher(citeText);
//
//                post.setAuthor(new Author(m.group(1)));
//
//                citeText = citeText.replaceAll(m.group(1), "");

    //            String time = ((org.w3c.dom.Element) innerElements.get(0)).getTextContent().trim();
    //            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd @ hh:mm");
    //            rptDate = sdf.format(date); System.out.println(rptDate+"\n");
    //            Date date = new Date(dateStr);
//                post.setTime(new Time( citeText.trim() ) ) ;
//            }

            String text = "";
            //innerElements = XMLUtils.getElementsByAttribValue(postElement, "class", "snap_preview");
            OrFilter textFilter = new OrFilter(new NodeFilter[] {textFilter1, textFilter2} );
            innerElements = postElement.getChildren().extractAllNodesThatMatch(textFilter, true);

            if(innerElements.size() > 0)
                post.setText( ((CompositeTag) innerElements.elementAt(0)).toPlainTextString().trim());
            
            addPost( (""+ i), post);
        }                
    }    
    
    public void readFileHTMLNew(String htmlFilePath) throws Exception, FileNotFoundException, IOException
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

    public static void main(String[] args) {
        // TODO code application logic here
        Properties systemSettings = System.getProperties();
        systemSettings.put("http.proxyHost", "192.168.36.204");
        systemSettings.put("http.proxyPort", "8080");
        System.setProperties(systemSettings);        

        WordPressBlogPage page = new WordPressBlogPage();
        try {
            page.readFileHTML("E:\\blog-corpus\\wordpress-blogs-done\\2ohreally\\1-index.html");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        String xmlURL = "F:\\Temp\\tmp.xml";
        XMLUtils.writeDOM4JXML(page.getDOMElement(), xmlURL);        
    }
}
