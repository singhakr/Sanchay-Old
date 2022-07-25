/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.ontology.writing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.dom4j.dom.DOMElement;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMDocument;

/**
 *
 * @author anil
 */
public class Book implements SanchayDOMDocument {

    public Book()
    {
    }

    public Book(String n)
    {
    }

    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("Author"));

//        DOMAttribute attribUserId = new DOMAttribute(new org.dom4j.QName("userid"), userid);
//        DOMAttribute attribEmail = new DOMAttribute(new org.dom4j.QName("email"), email);
//
//        domElement.add(attribUserId);
//        domElement.add(attribEmail);

//        DOMElement domElementName = name.getDOMElement();
//        domElement.add(domElementName);

//        if(url != null)
//        {
//            DOMElement domElementURL = url.getDOMElement();
//            domElement.add(domElementURL);
//        }

        return domElement;
    }

    public String getXML()
    {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void printXML(PrintStream ps)
    {
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
//                BlogPost post = new BlogPost();
//
//                post.readXMLFile(files[i].getAbsolutePath(), charset);
//
//                addPost(post.getTitle().getTitle() + "::" + post.getTitle().getSubTitle(), post);
            }
        }
    }

    public void readXML(org.w3c.dom.Element domElement) {
//        name = domElement.getAttribute("name");
//        blogType = (BlogType) BlogType.findFromId(domElement.getAttribute("type"));
//
//        NodeList elements = domElement.getElementsByTagName("KeyValueProperties");
//        blogProps = new KeyValueProperties();
//
//        if(elements.item(0) != null)
//            blogProps.readXML((org.w3c.dom.Element) elements.item(0));
//
//        elements = domElement.getElementsByTagName("Category");
//        bestCategory = new Category();
//
//        if(elements.item(0) != null)
//            bestCategory.readXML((org.w3c.dom.Element) elements.item(0));
//
//        elements = domElement.getElementsByTagName("Categories");
//        categories = new Categories();
//
//        if(elements.item(0) != null)
//            categories.readXML((org.w3c.dom.Element) elements.item(0));
//
//        elements = domElement.getElementsByTagName("Tag");
//        bestTag = new Tag();
//
//        if(elements.item(0) != null)
//            bestTag.readXML((org.w3c.dom.Element) elements.item(0));
//
//        elements = domElement.getElementsByTagName("Tags");
//        tags = new Tags();
//
//        if(elements.item(0) != null)
//            tags.readXML((org.w3c.dom.Element) elements.item(0));
//
//        elements = domElement.getElementsByTagName("BlogPost");
//
//        int count = elements.getLength();
//
//        for (int i = 0; i < count; i++)
//        {
//            BlogPost post = new BlogPost();
//            post.readXML((org.w3c.dom.Element) elements.item(i));
//
//            if(post.getTitle() == null || post.getTitle().getTitle() == null)
//                addPost(name + "-post-" + (i + 1), post);
//            else
//                addPost(post.getTitle().getTitle() + "::" + post.getTitle().getSubTitle(), post);
//        }
    }
}
