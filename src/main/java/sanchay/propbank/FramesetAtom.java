/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.propbank;

import java.io.PrintStream;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.FeatureAttribute;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.features.impl.FeatureStructureImpl;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class FramesetAtom implements SanchayDOMElement {

    protected boolean leafNode;

    protected String cdata = "";

    protected FeatureStructure attributes;

    public FramesetAtom()
    {
        attributes = new FeatureStructureImpl();
    }

    /**
     * @return the cdata
     */
    public String getCData()
    {
        return cdata;
    }

    /**
     * @param cdata the cdata to set
     */
    public void setCDataText(String text)
    {
        this.cdata = text;
    }

    public boolean leafNode()
    {
        return leafNode;
    }

    public void leafNode(boolean l)
    {
        leafNode = l;
    }

    /**
     * @return the attributes
     */
    public FeatureStructure getAttributes()
    {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(FeatureStructure attributes)
    {
        this.attributes = attributes;
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement(GlobalProperties.getIntlString("atom"));

        domElement.setText(cdata);

        FeatureStructure fs = getAttributes();

        if(fs != null)
        {
            int acount = fs.countAttributes();

            for (int i = 0; i < acount; i++)
            {
                FeatureAttribute fa = fs.getAttribute(i);
                FeatureValue fv = fa.getAltValue(0);

                String name = fa.getName();

                if(fv != null)
                {
                    String value = fv.getValue().toString();
                    DOMAttribute attribDOM = new DOMAttribute(new org.dom4j.QName(name), value);
                    domElement.add(attribDOM);
                }
            }
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
        if(leafNode)
        {
            cdata = domElement.getTextContent();
        }

        attributes = new FeatureStructureImpl();

        NamedNodeMap domAttribs = domElement.getAttributes();

        int acount = domAttribs.getLength();

        for (int i = 0; i < acount; i++)
        {
            Node node = domAttribs.item(i);
            String name = node.getNodeName();
            String value = node.getNodeValue();

            if(name != null)
            {
                if(value != null)
                    attributes.addAttribute(name, value);
                else
                    attributes.addAttribute(name, "");
            }
        }
    }

    @Override
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }
}
