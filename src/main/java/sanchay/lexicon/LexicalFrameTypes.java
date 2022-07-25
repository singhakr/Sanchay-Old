/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.lexicon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.propbank.Frameset;
import sanchay.resources.Resource;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class LexicalFrameTypes implements Resource, SanchayDOMElement {

    protected String langEnc = GlobalProperties.getIntlString("hin::utf");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected String path = GlobalProperties.getHomeDirectory() + "/" + "data/lexical-frames/verbs";

    protected String frameTag = "^V[a-zA-Z]*";

    protected LinkedHashMap<String, LexicalFrameType> frameTypes;

    public LexicalFrameTypes()
    {
        init();
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

    public void init()
    {
        frameTypes = new LinkedHashMap<String, LexicalFrameType>();
    }


    public int countSlots()
    {
        return frameTypes.size();
    }

    public Iterator getFrameTypeKeys()
    {
        return frameTypes.keySet().iterator();
    }

    public LexicalFrameType getFrameType(String k)
    {
        return frameTypes.get(k);
    }

    public int addFrameType(String k, LexicalFrameType v)
    {
        frameTypes.put(k, v);

        return frameTypes.size();
    }

    public int addAllFrameType(LexicalFrameTypes lf)
    {
        frameTypes.putAll(lf.frameTypes);

        return frameTypes.size();
    }

    public LexicalFrameType removeFrameType(String k)
    {
        return frameTypes.remove(k);
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

    @Override
    public int save() throws FileNotFoundException, IOException
    {
        save(path, charset);

        return 0;
    }

    @Override
    public int save(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        PrintStream ps = null;
        try
        {
            ps = new PrintStream(f, charset);

            ps.println("<!DOCTYPE frameset SYSTEM \"" + GlobalProperties.getHomeDirectory() + "/" + "data/propbank/resource/frameset/frameset.dtd\">");

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

//        idomElement.setText(note);

        domElement.add(idomElement);

//        int count = countSlots();
//
//        for (int i = 0; i < count; i++)
//        {
//            LexicalFrameInstance child = getFrame(i);
//
//            idomElement = child.getDOMElement();
//
//            domElement.add(idomElement);
//        }

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

    @Override
    public void readXML(Element domElement)
    {
        init();

        Node node = domElement.getFirstChild();

//        note = "";

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(GlobalProperties.getIntlString("note")))
                {
                    String n = element.getTextContent();

//                    note += n.trim() + "\n";
                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("predicate")))
                {
                    LexicalFrameType lexicalFrame = new LexicalFrameType();
                    lexicalFrame.readXML(element);
//                    addFrameType(lexicalFrame);
                }
            }

            node = node.getNextSibling();
        }

//        note = note.trim();
    }

    @Override
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }

    public String makeString()
    {
        String str = "";

        Iterator itr = getFrameTypeKeys();
        
        while(itr.hasNext())
        {
            String key = (String) itr.next();

            LexicalFrameType lexicalFrame = getFrameType(key);

            str += lexicalFrame.makeString() + "\n";
        }

        return str;
    }

    public void printFrames(PrintStream ps)
    {
        ps.println(makeString());
    }
}
