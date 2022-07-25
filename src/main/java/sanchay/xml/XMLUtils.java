/*
 * XMLUtils.java
 *
 * Created on May 21, 2006, 3:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.xml;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import org.apache.xml.serialize.OutputFormat;
import org.htmlparser.tags.CompositeTag;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.jdom.*;
import org.w3c.dom.Element;
import sanchay.GlobalProperties;
import sanchay.util.UtilityFunctions;
import sanchay.xml.validation.SanchayEntityResolver;
import sanchay.xml.validation.SaxErrorHandler;

/**
 *
 * @author anil
 */
public class XMLUtils {
    
    /** Creates a new instance of XMLUtils */
	
    public static org.jdom.Element parseJDOMXML(String xmlURL, String charset) throws JDOMException, IOException
    {
	org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
        
//        builder.setValidation(false);
	
	org.jdom.Document xmlDoc = builder.build(xmlURL);
	
	org.jdom.Element rootElement = xmlDoc.getRootElement();
	
	return rootElement;
    }

    public static org.w3c.dom.Element parseW3CXML(String xmlURL, String charset, SaxErrorHandler errorHandler, boolean validate) throws FileNotFoundException, SAXException, IOException
    {
        BufferedReader xmlReader = null;

        if(charset != null && charset.equals("") == false)
            xmlReader = new BufferedReader(new InputStreamReader(new FileInputStream(xmlURL), charset));
        else
            xmlReader = new BufferedReader(new InputStreamReader(new FileInputStream(xmlURL)));

        File xmlFile = new File(xmlURL);
    //	FileReader xmlReader = new FileReader(xmlFile);

        InputSource inputSource = new InputSource(xmlReader);

//        inputSource.setSystemId(systemId);

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    //	dbf.setSchema("/home/anil/exec/xxe-std-24p1/config/lmodel/lmodel.xsd");
        dbf.setValidating(validate);
        org.w3c.dom.Element rootElement = null;

        try
        {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(new SanchayEntityResolver(charset));

            //parse using builder to get DOM representation of the XML file
            org.w3c.dom.Document xmlDoc = db.parse(inputSource);

            if(errorHandler != null)
                db.setErrorHandler(errorHandler);

    //		Document xmlDoc = db.parse(xmlFilePath);
            rootElement = xmlDoc.getDocumentElement();

        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch(SAXException se) {
            se.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        xmlReader.close();

        return rootElement;
    }

    public static org.w3c.dom.Element parseW3CXML(String xmlURL, String charset, SaxErrorHandler errorHandler) throws FileNotFoundException, SAXException, IOException
    {
        return parseW3CXML(xmlURL, charset, errorHandler, true);
    }

    public static org.w3c.dom.Element parseW3CXML(String xmlURL, String charset, boolean validate) throws FileNotFoundException, SAXException, IOException
    {
        return parseW3CXML(xmlURL, charset, null, validate);
    }

    public static org.w3c.dom.Element parseW3CXML(String xmlURL, String charset) throws FileNotFoundException, SAXException, IOException
    {
        return parseW3CXML(xmlURL, charset, null);
    }
	
    public static org.w3c.dom.Element parseW3CXML(String xmlURL) throws FileNotFoundException, SAXException, IOException
    {
	return parseW3CXML(xmlURL, GlobalProperties.getIntlString("UTF-8"));
    }
	
    public static org.jdom.Element parseJDOMXML(String xmlURL) throws JDOMException, IOException
    {
	return parseJDOMXML(xmlURL, GlobalProperties.getIntlString("UTF-8"));
    }
    
//    public static String getSchemaURL(org.jdom.Document xmlDoc)
//    {
//	xmlDoc.getRootElement().
//    }
//    
//    public static String getSchemaURL(org.w3c.dom.Document xmlDoc, org.jdom.Namespace namespace)
//    {
//	xmlDoc.getElementsByTagNameNS(namespace, "");
//    }

    public static org.w3c.dom.Document createDocument(InputSource is) throws Exception{ 
        SAXParserFactory saxFactory = SAXParserFactory.newInstance(); 
	saxFactory.setNamespaceAware(true);

	javax.xml.parsers.SAXParser parser = saxFactory.newSAXParser(); 
        XMLReader reader = new XMLTrimFilter(parser.getXMLReader()); 
 
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(); 
        transformer.setOutputProperty(OutputKeys.INDENT, GlobalProperties.getIntlString("no"));
        DOMResult result = new DOMResult(); 
        transformer.transform(new SAXSource(reader, is), result); 
        return (org.w3c.dom.Document) result.getNode(); 
    } 
	
    public static org.w3c.dom.Element parseDomXML(String path) throws FileNotFoundException, SAXException, IOException
    {
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        InputSource inputSource = new InputSource(fileReader);
        org.apache.xerces.parsers.DOMParser xmlParser = new org.apache.xerces.parsers.DOMParser();
        xmlParser.setFeature("http://xml.org/sax/features/validation", false);

        try
        {
            xmlParser.parse(inputSource);
        }
        catch(Exception e)
        {
            System.out.println(GlobalProperties.getIntlString("Exception_while_parsing:_") + path);
            e.printStackTrace();
        }
        
        org.w3c.dom.Document xmlDoc = xmlParser.getDocument();
        org.w3c.dom.Element rootElement = xmlDoc.getDocumentElement();
        fileReader.close();

        return rootElement;
    }

    public static Vector getElementsByTagAndAttribValue(org.w3c.dom.Element element,
            String tag, String attrib, String val)
    {
        NodeList desElements = element.getElementsByTagName(tag);
        Vector selElements = new Vector(desElements.getLength() / 10, 10);

        for(int i = 0; i < desElements.getLength(); i++)
        {
            org.w3c.dom.Node desElement = desElements.item(i);

            if(desElement.getNodeType() == org.w3c.dom.Element.ELEMENT_NODE)
            {
		NamedNodeMap attributeNodes = desElement.getAttributes();
		
		org.w3c.dom.Node selAttribNode = attributeNodes.getNamedItem(attrib);
		
		if(selAttribNode != null && selAttribNode.getNodeValue().equalsIgnoreCase(val))
                {
                    selElements.add(desElement);
                }
            }
        }
    
        return selElements;
    }

    public static Vector getElementsByAttribValue(org.w3c.dom.Element element, String attrib, String val)
    {
        NodeList desElements = element.getElementsByTagName("*");
        Vector selElements = new Vector(desElements.getLength() / 10, 10);

        for(int i = 0; i < desElements.getLength(); i++)
        {
            org.w3c.dom.Node desElement = desElements.item(i);

            if(desElement.getNodeType() == org.w3c.dom.Element.ELEMENT_NODE)
            {
		NamedNodeMap attributeNodes = desElement.getAttributes();
		
		org.w3c.dom.Node selAttribNode = attributeNodes.getNamedItem(attrib);
		
		if(selAttribNode != null && selAttribNode.getNodeValue().equalsIgnoreCase(val))
                {
                    selElements.add(desElement);
                }
            }
        }
    
        return selElements;
    }

    public static void writeXercesDOMXML(org.w3c.dom.Element rootElement, String filePath, String charset)
    {
        org.apache.xerces.dom.DocumentImpl xmlDoc = new org.apache.xerces.dom.DocumentImpl();
        
        //rootElement.
        xmlDoc.appendChild(rootElement);
        
        OutputFormat of = new OutputFormat(filePath, charset, true);
        
        of.setIndent(1);
        of.setIndenting(true);
        of.setDoctype(null, null);
        
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            org.apache.xml.serialize.XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer(fos, of);

            // As a DOM Serializer
            serializer.asDOMSerializer();

            serializer.serialize( xmlDoc.getDocumentElement() );
            fos.close();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeDOM4JXML(org.dom4j.dom.DOMElement rootElement, String filePath)
    {
        org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
        document.add(rootElement);

        try
        {
            PrintStream ps = new PrintStream(filePath);

            org.dom4j.io.OutputFormat format = org.dom4j.io.OutputFormat.createPrettyPrint();
            org.dom4j.io.XMLWriter writer = new org.dom4j.io.XMLWriter( ps, format );
            writer.write( document );
            writer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // Not working
    public static void correctlyEndTags(File inFile, File outFile, String cs)  throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;
        PrintStream ps = new PrintStream(outFile, cs);
        
        if(cs != null && cs.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
        
        String line = "";
        
        System.out.println(GlobalProperties.getIntlString("Writing_file_") + outFile);
        
        while((line = inReader.readLine()) != null) {
            
            if(line.contains("<"))
            {
                String patternStr = "<(img|IMG|br|BR|em|EM)\\s*([^<>]*?)>";
                String replaceStr = "<$1 $2 />";
                
                Pattern pattern = Pattern.compile(patternStr);

                CharSequence inputStr = line;
                Matcher matcher = pattern.matcher(inputStr);

                while(matcher.find())
                {
                    System.out.println(line);
                    line = matcher.replaceAll(replaceStr);
//                    line = line.replaceAll("//>>", "/>");
                    System.out.println(line);
                }
                
                patternStr = "/\\s*/\\s*>";
                replaceStr = "/>";
                pattern = Pattern.compile(patternStr);

                inputStr = line;
                matcher = pattern.matcher(inputStr);

                while(matcher.find())
                {
                    System.out.println(line);
                    line = matcher.replaceAll(replaceStr);
//                    line = line.replaceAll("//>>", "/>");
                    System.out.println(line);
                }
            }
            
            ps.println(line);
        }
        
        inReader.close();
        ps.close();
    }
    
    // Not working
    public static void correctlyEndTagsInPlace(File inFile, String cs)  throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;
        
        File outFile = new File(inFile.getAbsolutePath() + ".tmp.tmp");
        
        correctlyEndTags(inFile, outFile, cs);
        inFile.delete();
        
        UtilityFunctions.copyFile(outFile, inFile);
        outFile.delete();
    }
    
    public static org.w3c.dom.Element dom4j2W3CDomElement(org.dom4j.dom.DOMElement dom4jElement)
    {
//        org.w3c.dom.Element domElement = new org.jdom.Element(dom4jElement.getNodeName(),
//                dom4jElement.getNamespaceURI());        
        return null;
    }
    
    public static org.dom4j.dom.DOMElement W3CDom2dom4jElement(org.w3c.dom.Element domElement)
    {        
        NamedNodeMap attribs = domElement.getAttributes();
        
        org.dom4j.dom.DOMElement dom4jElement = new org.dom4j.dom.DOMElement(new org.dom4j.QName(domElement.getNodeName()), attribs.getLength());
        
        int count = attribs.getLength();

        for (int i = 0; i < count; i++)
        {
            org.w3c.dom.Attr attrib = (org.w3c.dom.Attr) attribs.item(i);
            org.dom4j.dom.DOMAttribute attrib4j = new org.dom4j.dom.DOMAttribute(new org.dom4j.QName(attrib.getNodeName()), attrib.getNodeValue());
            dom4jElement.add(attrib4j);            
        }
        
        NodeList childNodes = domElement.getChildNodes();

        count = childNodes.getLength();

        for (int i = 0; i < count; i++)
        {            
            if(childNodes.item(i).getNodeType() == org.w3c.dom.Element.ELEMENT_NODE)
            {
                org.w3c.dom.Element childNode = (org.w3c.dom.Element) childNodes.item(i);
                org.dom4j.dom.DOMElement dom4jChildNode = W3CDom2dom4jElement(childNode);

                dom4jElement.add(dom4jChildNode);            
            }
            else if(childNodes.item(i).getNodeType() == org.w3c.dom.Element.TEXT_NODE)
            {
                org.w3c.dom.Node childNode = childNodes.item(i);
                dom4jElement.setText(childNode.getTextContent());            
            }
        }
        
        return dom4jElement;
    }
    
    public static org.dom4j.dom.DOMElement mergeDom4jElements(org.dom4j.dom.DOMElement dom4jElement1,
            org.dom4j.dom.DOMElement dom4jElement2)
    {
        if(dom4jElement1.getNodeType() != dom4jElement1.getNodeType())
            return null;
        
        if(dom4jElement1.getNodeName().equals(dom4jElement1.getNodeName()))
            return null;        
            
        org.dom4j.dom.DOMElement dom4jElement = new org.dom4j.dom.DOMElement(dom4jElement1.getNodeName());

        // Merging attributes
        NamedNodeMap attribs1 = dom4jElement1.getAttributes();
        NamedNodeMap attribs2 = dom4jElement2.getAttributes();

        int count = attribs1.getLength();
        
        Hashtable attribHash = new Hashtable(count);
        
        for (int i = 0; i < count; i++)
        {
            org.dom4j.dom.DOMAttribute attrib1 = (org.dom4j.dom.DOMAttribute) attribs1.item(i);
            
            String akey = attrib1.getNodeName();
            
            org.dom4j.dom.DOMAttribute attrib2 = (org.dom4j.dom.DOMAttribute) attribs1.getNamedItem(akey);
            
            if(attrib2 == null)
                attribHash.put(akey, attrib1);
            else if(attrib2.getNodeValue().equals(""))
                attribHash.put(akey, attrib1);
            else if(attrib1.getNodeValue().equals(""))
                attribHash.put(akey, attrib2);
            else
                attribHash.put(akey, attrib1);
        }

        count = attribs2.getLength();

        for (int i = 0; i < count; i++)
        {
            org.dom4j.dom.DOMAttribute attrib2 = (org.dom4j.dom.DOMAttribute) attribs2.item(i);

            String akey = attrib2.getNodeName();
            
            if(attribHash.get(akey) == null)
                attribHash.put(akey, attrib2);
        }
        
        Enumeration enm = attribHash.keys();
        
        while(enm.hasMoreElements())
        {
            String k = (String) enm.nextElement();
            org.dom4j.dom.DOMAttribute attrib = (org.dom4j.dom.DOMAttribute) attribHash.get(k);
            
            dom4jElement.add(attrib);
        }
        
        // Merging child nodes
        org.w3c.dom.NodeList childNodes1 = dom4jElement1.getChildNodes();
        org.w3c.dom.NodeList childNodes2 = dom4jElement2.getChildNodes();

        count = childNodes1.getLength();
        
        Vector childNodesVector = new Vector(count);
        
        for (int i = 0; i < count; i++)
        {
            org.dom4j.dom.DOMElement childNode1 = (org.dom4j.dom.DOMElement) childNodes1.item(i);

            int jcount = childNodes2.getLength();
            
            for (int j = 0; j < jcount; j++)
            {
                org.dom4j.dom.DOMElement childNode2 = (org.dom4j.dom.DOMElement) childNodes2.item(j);

                if( ((CompositeTag) (Node) childNode1).toPlainTextString().trim().equals(((CompositeTag) (Node) childNode2).toPlainTextString().trim()) == false)
                    childNodesVector.add(childNode2);
            }

            childNodesVector.add(childNode1);                    
        }
        
        count  = childNodesVector.size();
        
        for (int i = 0; i < count; i++)
        {
            dom4jElement.add((org.dom4j.dom.DOMElement) childNodesVector.get(i));
        }

        return dom4jElement;
    }

    public static boolean hasChileNode(Element domElement, String tag)
    { 
        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(tag))
                {
                    return true;
                }
            }

            node = node.getNextSibling();
        }

        return false;
    }
}
