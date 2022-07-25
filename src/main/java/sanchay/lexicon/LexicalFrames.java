/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.lexicon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.simple.impl.SimpleStoryImpl;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.propbank.Frameset;
import sanchay.resources.Resource;
import sanchay.util.UtilityFunctions;
import sanchay.xml.XMLUtils;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class LexicalFrames implements Resource, SanchayDOMElement {

    protected String langEnc = GlobalProperties.getIntlString("hin::utf");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected String path = GlobalProperties.getHomeDirectory() + "/" + "data/lexical-frames/verbs";

    protected String frameTag = "^V[a-zA-Z]*";

    protected Vector<LexicalFrameInstance> frames;

    public LexicalFrames()
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
        frames = new Vector<LexicalFrameInstance>();
    }

    public int countFrames()
    {
        return frames.size();
    }

    public LexicalFrameInstance getFrame(int num)
    {
        return (LexicalFrameInstance) frames.get(num);
    }

    public int addFrame(LexicalFrameInstance p)
    {
        frames.add(p);
        return frames.size();
    }

    public LexicalFrameInstance removeFrame(int num)
    {
        return (LexicalFrameInstance) frames.remove(num);
    }

    public void removeFrame(LexicalFrameInstance p)
    {
        int ind = frames.indexOf(p);

        if(ind != -1)
            frames.remove(ind);
    }

    @Override
    public int read() throws FileNotFoundException, IOException
    {
        read(path, charset);

        return 0;
    }

    public void extractFramesFromSSFCorpus(SSFCorpus ssfCorpus)
    {
        Enumeration enm = ssfCorpus.getStoryKeys();

        while(enm.hasMoreElements())
        {
            String storyPath = (String) enm.nextElement();

            SSFStory ssfStory = new SSFStoryImpl();

            try {
                if(SimpleStoryImpl.getCorpusType(storyPath, charset) == CorpusType.SSF_FORMAT)
                {
                    ssfStory.readFile(storyPath, charset);

                    extractFramesFromSSFStory(ssfStory);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void extractFramesFromSSFStory(SSFStory ssfStory)
    {
        int scount = ssfStory.countSentences();

        for (int i = 0; i < scount; i++)
        {
            SSFSentence ssfSentence = ssfStory.getSentence(i);

            SSFPhrase root = ssfSentence.getRoot();

            try
            {
                LinkedHashMap cfgToMMTreeMapping = new LinkedHashMap(0, 10);
                SSFPhrase mmt = ((SSFPhrase) root).convertToGDepNode(cfgToMMTreeMapping);

                LinkedHashMap mmTreeToCFGMapping = (LinkedHashMap) UtilityFunctions.getReverseMap(cfgToMMTreeMapping);

                extractFramesFromSSFPhrase(mmt, mmTreeToCFGMapping);
            }
            catch(Exception ex)
            {
                System.err.println("MM Tree couldn't be formed for story " + ssfStory.getId()  + ", sentence " + (i + 1));
                ex.printStackTrace();
            }
        }
    }

    public void extractFramesFromSSFPhrase(SSFPhrase ssfPhrase, LinkedHashMap cfgToMMTreeMapping)
    {
        Pattern p = Pattern.compile(frameTag);

        String tag = ssfPhrase.getName();
        Matcher m = p.matcher(tag);

        boolean rightTag = false;

        if(m.find())
            rightTag = true;

        LexicalFrameInstance lexicalFrameInstance = new LexicalFrameInstance();

        SSFPhrase cfgNode = (SSFPhrase) cfgToMMTreeMapping.get(ssfPhrase);

        if(cfgNode != null)
        {
            String stem = cfgNode.getStemForTag("VM");

            if(stem != null && stem.equals("") == false)
                lexicalFrameInstance.setStem(stem);

            String lex = cfgNode.getLexDataForTag("VM");

            if(lex != null && lex.equals("") == false)
                lexicalFrameInstance.setLex(lex);
            
            String lexEx = cfgNode.getLexicalSequence(frameTag, "_");

            if(lexEx != null && lexEx.equals("") == false)
                lexicalFrameInstance.setLexEx(lexEx);
        }

        int scount = ssfPhrase.countChildren();

        for (int i = 0; i < scount; i++)
        {
            SSFNode ssfNode = ssfPhrase.getChild(i);

            if(ssfNode instanceof SSFPhrase)
            {
                if(rightTag)
                {
                    LexicalSlot lexicalSlot = new LexicalSlot();

                    FeatureStructure slotFeatures = lexicalSlot.getFeatures();

                    String rel = ssfNode.getAttributeValue("drel");

                    String parts[] = rel.split(":");

                    rel = parts[0];

                    String ctag = ssfNode.getName();

                    cfgNode = ((SSFPhrase) cfgToMMTreeMapping.get((SSFPhrase) ssfNode));

                    String vib = cfgNode.getLexicalSequence("PSP", "_");

                    if(rel != null && rel.equals("") == false
                            && ctag != null && ctag.equals("") == false)
                    {
                        if(vib != null && vib.equals("") == false)
                        {
                            slotFeatures.addAttribute("rel", rel);
                            slotFeatures.addAttribute("ctag", ctag);
                            slotFeatures.addAttribute("vib", vib);

                            lexicalFrameInstance.addSlot(ssfNode.getAttributeValue("name"), lexicalSlot);
                        }
                    }
                }

                extractFramesFromSSFPhrase((SSFPhrase) ssfNode, cfgToMMTreeMapping);
            }
        }

        if(rightTag && lexicalFrameInstance.countSlots() > 0)
            addFrame(lexicalFrameInstance);
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

        int count = countFrames();

        for (int i = 0; i < count; i++)
        {
            LexicalFrameInstance child = getFrame(i);

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
                    LexicalFrameInstance lexicalFrame = new LexicalFrameInstance();
                    lexicalFrame.readXML(element);
                    addFrame(lexicalFrame);
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

        int fcount = countFrames();

        for (int i = 0; i < fcount; i++)
        {
            LexicalFrameInstance lexicalFrame = getFrame(i);

            str += lexicalFrame.makeString() + "\n";
        }

        return str;
    }

    public void printFrames(PrintStream ps)
    {
        ps.println(makeString());
    }

    public LexicalFrameTypes getFramesTypes()
    {
        LexicalFrameTypes lexicalFrames = new LexicalFrameTypes();

        int count = countFrames();

        for (int i = 0; i < count; i++)
        {
            LexicalFrameInstance instance = getFrame(i);

            String frame = instance.makeFrameString();

            LexicalFrameType type = lexicalFrames.getFrameType(frame);

            if(type == null)
            {
                type = new LexicalFrameType(instance);
                lexicalFrames.addFrameType(frame, type);
            }
            else
            {
                type.setFrequency(type.getFrequency() + 1);
            }
        }

        return lexicalFrames;
    }

    public LexicalFrameTypes getStemFramesTypes()
    {
        LexicalFrameTypes lexicalFrames = new LexicalFrameTypes();

        int count = countFrames();

        for (int i = 0; i < count; i++)
        {
            LexicalFrameInstance instance = getFrame(i);
            
            String stem = instance.getStem();
            String frame = stem + "::" + instance.makeFrameString();

            LexicalFrameType type = lexicalFrames.getFrameType(frame);

            if(type == null)
            {
                type = new LexicalFrameType(instance);
                lexicalFrames.addFrameType(frame, type);
            }
            else
            {
                type.setFrequency(type.getFrequency() + 1);
            }
        }

        return lexicalFrames;
    }

    public static void main(String[] args)
    {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

        SSFStory story = new SSFStoryImpl();

        try {
            fsp.readDefaultProps();

            ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), GlobalProperties.getIntlString("UTF-8"));

            FeatureStructuresImpl.setFSProperties(fsp);
            SSFNode.setSSFProperties(ssfp);

            story.readFile("/home/anil/sanchay-debug-data/treebank-sample.txt");

            LexicalFrames lexicalFrames = new LexicalFrames();

            lexicalFrames.extractFramesFromSSFStory(story);

            lexicalFrames.printFrames(System.out);

            LexicalFrameTypes lexicalFrameTypes = lexicalFrames.getFramesTypes();

            lexicalFrameTypes.printFrames(System.out);

            lexicalFrameTypes = lexicalFrames.getStemFramesTypes();

            lexicalFrameTypes.printFrames(System.out);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
