/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.propbank;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.properties.MultiPropertyTokens;
import sanchay.resources.Resource;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMElement;
import sanchay.xml.validation.SaxErrorHandler;

/**
 *
 * @author anil
 */
public class Frameset implements Resource, SanchayDOMElement {

    protected String langEnc = GlobalProperties.getIntlString("hin::utf");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected String path = GlobalProperties.getHomeDirectory() + "/" + "data/propbank/resource/frameset/verb.xml";

    protected String note = "";

    protected Vector<FramesetPredicate> predicates;

    protected static DTD dtd;
    protected static MultiPropertyTokens attributeValues;

    public Frameset()
    {
        init();

        try
        {
            read();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        }

        path = GlobalProperties.getIntlString("Untitled");
    }

    /**
     * @return the note
     */
    public String getNote()
    {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note)
    {
        this.note = note;
    }

    public int countPredicates()
    {
        return predicates.size();
    }

    public FramesetPredicate getPredicate(int num)
    {
        return (FramesetPredicate) predicates.get(num);
    }

    public int addPredicate(FramesetPredicate p)
    {
        predicates.add(p);
        return predicates.size();
    }

    public FramesetPredicate removePredicate(int num)
    {
        return (FramesetPredicate) predicates.remove(num);
    }

    public void removePredicate(FramesetPredicate p)
    {
        int ind = predicates.indexOf(p);

        if(ind != -1)
            predicates.remove(ind);
    }

    @Override
    public String getName()
    {
        throw new UnsupportedOperationException(GlobalProperties.getIntlString("Not_supported_yet."));
    }

    @Override
    public void setName(String nm)
    {
        throw new UnsupportedOperationException(GlobalProperties.getIntlString("Not_supported_yet."));
    }

    @Override
    public String getLangEnc()
    {
        return langEnc;
    }

    @Override
    public void setLangEnc(String langEnc)
    {
        this.langEnc = langEnc;
    }

    @Override
    public String getFilePath()
    {
        return path;
    }

    @Override
    public void setFilePath(String fp)
    {
        path = fp;
    }

    @Override
    public String getCharset()
    {
        return charset;
    }

    @Override
    public void setCharset(String c)
    {
        charset = c;
    }

    /**
     * @return the dtd
     */
    public static DTD getDTD()
    {
        return dtd;
    }

    /**
     * @param dtd the dtd to set
     */
    public static void setDTD(DTD d)
    {
        dtd = d;
    }

    public static MultiPropertyTokens getAttributeValues()
    {
        return attributeValues;
    }

    public static void setAttributeValues(MultiPropertyTokens m)
    {
        attributeValues = m;
    }

    public void init()
    {
        langEnc = GlobalProperties.getIntlString("hin::utf");
        charset = GlobalProperties.getIntlString("UTF-8");

        path = GlobalProperties.getHomeDirectory() + "/" + "data/propbank/resource/frameset/verb.xml";

        note = "";

        predicates = new Vector<FramesetPredicate>();
    }

    @Override
    public int read() throws FileNotFoundException, IOException
    {
        read(path, charset);

        return 0;
    }

    @Override
    public int read(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        Element rootNode = null;

        try {
            rootNode = XMLUtils.parseW3CXML(f, charset);

            if(rootNode != null)
            {
                readXML(rootNode);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public String validate(String f, String charset)
    {
        String msg = null;

        File tmpFile = new File(f + ".tmp.tmp");

        Element rootNode = null;

        try {
            save(tmpFile.getAbsolutePath(), charset);
            
            rootNode = XMLUtils.parseW3CXML(tmpFile.getAbsolutePath(), charset, new SaxErrorHandler());

            tmpFile.delete();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {

            msg = ex.getMessage();

            return msg;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return msg;
    }

    @Override
    public int save() throws FileNotFoundException, IOException
    {
        save(path, charset);

        return 0;
    }

    @Override
    public int save(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
//        org.dom4j.dom.DOMDocument document = getDOMDocument();
//
//    	BufferedWriter writer = null;
//
//        if(charset != null && charset.equals("") == false)
//            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), charset));
//        else
//            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
//
//        document.write(writer);

        PrintStream ps = null;
        try
        {
            ps = new PrintStream(f, charset);

            ps.println("<!DOCTYPE frameset PUBLIC \"-//Sanchay//Frameset//EN\" SYSTEM \"" + "./data/propbank/resource/frameset/frameset.dtd\">");

            printXML(ps);

        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        }

        ps.close();

        return 0;
    }

    public org.dom4j.dom.DOMDocument getDOMDocument()
    {
        DOMDocument domDocument = new DOMDocument(getDOMElement());

        return domDocument;
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("frameset"));

        DOMElement idomElement = new DOMElement(GlobalProperties.getIntlString("note"));

        idomElement.setText(note);

        domElement.add(idomElement);

        int count = countPredicates();

        for (int i = 0; i < count; i++)
        {
            FramesetPredicate child = getPredicate(i);

            idomElement = child.getDOMElement();

            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public String getXML()
    {
        String xmlString = "";
        org.dom4j.dom.DOMElement element = getDOMElement();
        xmlString = element.asXML();

        return "\n" + xmlString + "\n";
    }

    public static void readDTD(String path, String charset)
    {
        BufferedReader inReader = null;

        try
        {
            if (charset != null && charset.equals("") == false)
            {
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
            } else
            {
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            }

            DTDParser dtdParser = new DTDParser(inReader);
            dtd = dtdParser.parse();

            inReader.close();

        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readAttributeValues(String path, String charset)
    {
        attributeValues = new MultiPropertyTokens();
        
        try
        {
            attributeValues.read(path, charset);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Frameset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void readXML(Element domElement)
    {
        init();

        Node node = domElement.getFirstChild();

        note = "";

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(GlobalProperties.getIntlString("note")))
                {
                    String n = element.getTextContent();

                    note += n.trim() + "\n";
                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("predicate")))
                {
                    FramesetPredicate framesetPredicate = new FramesetPredicate();
                    framesetPredicate.readXML(element);
                    addPredicate(framesetPredicate);
                }
            }
            
            node = node.getNextSibling();
        }

        note = note.trim();
    }

    @Override
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }
}
