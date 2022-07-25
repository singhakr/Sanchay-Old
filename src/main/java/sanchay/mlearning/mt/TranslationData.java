/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.gui.common.SanchayLanguages;
import sanchay.propbank.Frameset;
import sanchay.resources.Resource;
import sanchay.util.UtilityFunctions;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class TranslationData implements Resource, SanchayDOMElement {

    protected String srcLangEnc = "eng::utf8";
    protected String tgtLangEnc = "hin::utf8";

    protected String charset = "UTF-8";

    protected String name = "transliteration-data";

    protected String corpusType = "Training";

    protected String dataPath = GlobalProperties.getHomeDirectory() + "/" + "data/transliteration/translit-test-data-hindi.txt";

    protected LinkedHashMap translationCandidatesMap;

    public TranslationData()
    {
        translationCandidatesMap = new LinkedHashMap();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String nm)
    {
        name = nm;
    }

    @Override
    public String getLangEnc()
    {
        return srcLangEnc;
    }

    @Override
    public void setLangEnc(String langEnc)
    {
        this.srcLangEnc = langEnc;
    }

    @Override
    public String getFilePath()
    {
        return dataPath;
    }

    @Override
    public void setFilePath(String fp)
    {
        dataPath = fp;
    }

    /**
     * @return the srcLangEnc
     */
    public String getSrcLangEnc()
    {
        return srcLangEnc;
    }

    /**
     * @param srcLangEnc the srcLangEnc to set
     */
    public void setSrcLangEnc(String srcLangEnc)
    {
        this.srcLangEnc = srcLangEnc;
    }

    /**
     * @return the tgtLangEnc
     */
    public String getTgtLangEnc()
    {
        return tgtLangEnc;
    }

    /**
     * @param tgtLangEnc the tgtLangEnc to set
     */
    public void setTgtLangEnc(String tgtLangEnc)
    {
        this.tgtLangEnc = tgtLangEnc;
    }

    /**
     * @return the charset
     */
    public String getCharset()
    {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    /**
     * @return the dataPath
     */
    public String getDataPath()
    {
        return dataPath;
    }

    /**
     * @param dataPath the dataPath to set
     */
    public void setDataPath(String dataPath)
    {
        this.dataPath = dataPath;
    }
    
    public int countSrcTokens()
    {
        if(translationCandidatesMap == null)
            return 0;

        return translationCandidatesMap.size();
    }

    public Iterator getSrcTokenKeys()
    {
        if(translationCandidatesMap == null)
            return null;

        return translationCandidatesMap.keySet().iterator();
    }

    public TranslationCandidates getTranslationCandidates(String srcTkn)
    {
        if(translationCandidatesMap == null)
            return null;

        return (TranslationCandidates) translationCandidatesMap.get(srcTkn);
    }

    public void addSrcToken(String srcTkn, TranslationCandidates candidates)
    {
        if(translationCandidatesMap == null)
            translationCandidatesMap = new LinkedHashMap(0, 100);

        translationCandidatesMap.put(srcTkn, candidates);
    }

    public void removeTranslationCandidates(String srcTkn)
    {
        if(translationCandidatesMap == null)
            return;

        translationCandidatesMap.remove(srcTkn);
    }

    @Override
    public int read() throws FileNotFoundException, IOException
    {
        read(dataPath, charset);

        return 0;
    }

    @Override
    public int read(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        Element rootNode = null;

        try {
            rootNode = XMLUtils.parseW3CXML(f, charset, null, false);

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
        save(dataPath, charset);

        return 0;
    }

    @Override
    public int save(String f, String charset) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        PrintStream ps = null;

        ps = new PrintStream(f, charset);

        printXML(ps);

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
        DOMElement domElement = new DOMElement("TransliterationCorpus");

        DOMAttribute attribDOM = new DOMAttribute(new org.dom4j.QName("CorpusID"), "" + name);
        domElement.add(attribDOM);

        attribDOM = new DOMAttribute(new org.dom4j.QName("SourceLang"), "" + SanchayLanguages.getLanguageName(srcLangEnc));
        domElement.add(attribDOM);

        attribDOM = new DOMAttribute(new org.dom4j.QName("TargetLang"), "" + SanchayLanguages.getLanguageName(tgtLangEnc));
        domElement.add(attribDOM);

        attribDOM = new DOMAttribute(new org.dom4j.QName("CorpusType"), "" + corpusType);
        domElement.add(attribDOM);

        attribDOM = new DOMAttribute(new org.dom4j.QName("CorpusSize"), "" + countSrcTokens());
        domElement.add(attribDOM);

        attribDOM = new DOMAttribute(new org.dom4j.QName("CorpusFormat"), "" + charset);
        domElement.add(attribDOM);

        Iterator cndItr = getSrcTokenKeys();

        int id = 0;

        while(cndItr.hasNext())
        {
            String srcTkn = (String) cndItr.next();

            TranslationCandidates candidates = getTranslationCandidates(srcTkn);

            DOMElement idomElement = candidates.getDOMElement();

            attribDOM = new DOMAttribute(new org.dom4j.QName("ID"), "" + id);

            idomElement.add(attribDOM);

            domElement.add(idomElement);

            id++;
        }

        return domElement;
    }

    @Override
    public String getXML()
    {
        String xmlString = "<?xml version=1.0 encoding=\"UTF-8\"?>\n";
        
        org.dom4j.dom.DOMElement element = getDOMElement();
        xmlString += element.asXML();

        xmlString = xmlString.replaceAll("</Name>", "</Name>\n");

        return "\n" + xmlString + "\n";
    }

    @Override
    public void readXML(Element domElement)
    {
        translationCandidatesMap.clear();

        NamedNodeMap domAttribs = domElement.getAttributes();

        Node attribDOM = domAttribs.getNamedItem("CorpusID");

        if(attribDOM != null)
            name = attribDOM.getNodeValue();

        attribDOM = domAttribs.getNamedItem("CorpusFormat");

        if(attribDOM != null)
            charset = attribDOM.getNodeValue();

        attribDOM = domAttribs.getNamedItem("SourceLang");

        String le = SanchayLanguages.getLangEncCode(attribDOM.getNodeValue(), charset);

        if(attribDOM != null)
        {
            if(le != null)
                srcLangEnc = le;
            else
                srcLangEnc = "English";
        }

        attribDOM = domAttribs.getNamedItem("TargetLang");

        le = SanchayLanguages.getLangEncCode(attribDOM.getNodeValue(), charset);

        if(attribDOM != null)
        {
            if(le != null)
                tgtLangEnc = le;
            else
                tgtLangEnc = "Hindi";
        }

        attribDOM = domAttribs.getNamedItem("CorpusType");

        if(attribDOM != null)
            corpusType = attribDOM.getNodeValue();

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(GlobalProperties.getIntlString("Name")))
                {
                    TranslationCandidates candidates = new TranslationCandidates();
                    candidates.readXML(element);

                    addSrcToken(candidates.getSrcWrd(), candidates);
                }
            }

            node = node.getNextSibling();
        }
    }

    public void clear()
    {
        translationCandidatesMap.clear();
    }

    public void print(PrintStream ps)
    {
        Iterator cndItr = getSrcTokenKeys();


        while(cndItr.hasNext())
        {
            String srcTkn = (String) cndItr.next();

            boolean onlyTheFirst = true;

            TranslationCandidates candidates = getTranslationCandidates(srcTkn);

            if(candidates.countTranslationCandidates() > 1)
                onlyTheFirst = false;

            ps.println("#Transliteration of ({" + srcTkn + "})");

            candidates.printTranslationCandidates(ps, onlyTheFirst, false);
        }
    }

    @Override
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }

    public int saveParallelCorpus(String srcPath, String srcCharset, String tgtPath, String tgtCharset)
            throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        PrintStream srcPS = null;
        PrintStream tgtPS = null;

        srcPS = new PrintStream(srcPath, srcCharset);
        tgtPS = new PrintStream(tgtPath, tgtCharset);

        printParallelCorpus(srcPS, tgtPS);

        srcPS.close();
        tgtPS.close();

        return 0;
    }

    public void printParallelCorpus(PrintStream srcPS, PrintStream tgtPS)
    {
        Iterator srcItr = getSrcTokenKeys();

        while(srcItr.hasNext())
        {
            String srcTkn = (String) srcItr.next();

            TranslationCandidates candidates = getTranslationCandidates(srcTkn);

            Iterator tgtItr = candidates.getTranslationCandidates();

            while(tgtItr.hasNext())
            {
                String candidate = (String) tgtItr.next();

                splitAndPrint(srcTkn, candidate, srcPS, tgtPS);
            }
        }
    }

    protected void splitAndPrint(String srcString, String tgtString, PrintStream srcPS, PrintStream tgtPS)
    {
        String srcParts[] = srcString.split("[ ,-:_;()\\[\\]{}]+");
        String tgtParts[] = tgtString.split("[ ,-:_;()\\[\\]{}]+");

        if(srcParts.length == tgtParts.length)
        {
            for (int i = 0; i < tgtParts.length; i++)
            {
                String sstring = srcParts[i];
                String tstring = tgtParts[i];

                sstring = UtilityFunctions.getSpacedOutString(sstring);
                tstring = UtilityFunctions.getSpacedOutString(tstring);
                
                srcPS.println(sstring);
                tgtPS.println(tstring);
            }
        }
        else
        {
            System.err.println("Unmatching lengths of the source and target strings: ");
            System.err.println("\tSource string: " + srcString);
            System.err.println("\tTarget string: " + tgtString);
        }
    }

    public static void main(String args[])
    {
        TranslationData tdata = new TranslationData();
        
        try
        {
            tdata.read("/home/anil/corpora/ACL-NEWS-SHARED-TASK-09/NEWS09_dev_EnHi_974.xml/NEWS09_dev_EnHi_974.xml", "UTF-8");

            tdata.printXML(System.out);

//            tdata.saveParallelCorpus("/home/anil/corpora/ACL-NEWS-SHARED-TASK-09/NEWS09_dev_EnHi_974.xml/NEWS09_dev_EnHi_974.eng.xml", "UTF-8",
//                    "/home/anil/corpora/ACL-NEWS-SHARED-TASK-09/NEWS09_dev_EnHi_974.xml/NEWS09_dev_EnHi_974.hin.xml", "UTF-8");
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(TranslationData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(TranslationData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(TranslationData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
