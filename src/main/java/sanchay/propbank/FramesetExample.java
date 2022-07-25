/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.propbank;

import java.util.Vector;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructureImpl;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class FramesetExample extends FramesetAtom implements SanchayDOMElement {

    protected String note = "";

    protected String text = "";

    protected FramesetExampleInflections inflections;

    protected Vector<FramesetExampleArgument> arguments;

    protected Vector<FramesetExampleRelation> relations;

    public FramesetExample()
    {
        init();
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

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    public FramesetExampleInflections getInflections()
    {
        return inflections;
    }

    public void setInflections(FramesetExampleInflections i)
    {
        inflections = i;
    }

    public int countArguments()
    {
        return arguments.size();
    }

    public FramesetExampleArgument getArgument(int num)
    {
        return (FramesetExampleArgument) arguments.get(num);
    }

    public int addArgument(FramesetExampleArgument a)
    {
        arguments.add(a);
        return arguments.size();
    }

    public FramesetExampleArgument removeArgument(int num)
    {
        return (FramesetExampleArgument) arguments.remove(num);
    }

    public void removeArgument(FramesetExampleArgument a)
    {
        int ind = arguments.indexOf(a);
        arguments.remove(ind);
    }

    public int countRelations()
    {
        return relations.size();
    }

    public FramesetExampleRelation getRelation(int num)
    {
        return (FramesetExampleRelation) relations.get(num);
    }

    public int addRelation(FramesetExampleRelation r)
    {
        relations.add(r);
        return relations.size();
    }

    public FramesetExampleRelation removeRelation(int num)
    {
        return (FramesetExampleRelation) relations.remove(num);
    }

    public void removeRelation(FramesetExampleRelation r)
    {
        int ind = relations.indexOf(r);
        relations.remove(ind);
    }

    public void init()
    {
        note = "";

        text = "";

        attributes = new FeatureStructureImpl();
        inflections = new FramesetExampleInflections();
        arguments = new Vector<FramesetExampleArgument>();
        relations = new Vector<FramesetExampleRelation>();
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("example"));

        FramesetExampleInflections child = getInflections();

        DOMElement idomElement = child.getDOMElement();

        domElement.add(idomElement);

        if(note != null && note.trim().equals("") == false)
        {
            idomElement = new DOMElement(GlobalProperties.getIntlString("note"));

            idomElement.setText(note);

            domElement.add(idomElement);
        }

        if(getText() != null && getText().trim().equals("") == false)
        {
            idomElement = new DOMElement(GlobalProperties.getIntlString("text"));

            idomElement.setText(getText());

            domElement.add(idomElement);
        }

        int count = countArguments();

        for (int i = 0; i < count; i++)
        {
            FramesetExampleArgument argument = getArgument(i);

            idomElement = argument.getDOMElement();

            domElement.add(idomElement);
        }

        count = countRelations();

        for (int i = 0; i < count; i++)
        {
            FramesetExampleRelation relation = getRelation(i);

            idomElement = relation.getDOMElement();

            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public void readXML(Element domElement)
    {
        init();
        
        super.readXML(domElement);

        Node node = domElement.getFirstChild();

        note = "";
        setText("");

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(GlobalProperties.getIntlString("note")))
                {
                    String n = element.getTextContent();

                    note += n + "\n";
                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("text")))
                {
                    String t = element.getTextContent();

                    setText(getText() + t + "\n");
                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("inflection")))
                {
                    FramesetExampleInflections framesetExampleInflection = new FramesetExampleInflections();
                    framesetExampleInflection.readXML(element);
                    setInflections(framesetExampleInflection);
                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("arg")))
                {
                    FramesetExampleArgument framesetExampleArgument = new FramesetExampleArgument();
                    framesetExampleArgument.readXML(element);
                    addArgument(framesetExampleArgument);
                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("rel")))
                {
                    FramesetExampleRelation framesetExampleRelation = new FramesetExampleRelation();
                    framesetExampleRelation.readXML(element);
                    addRelation(framesetExampleRelation);
                }
            }

            node = node.getNextSibling();
        }
    }
}
