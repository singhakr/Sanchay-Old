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
public class FramesetRole extends FramesetAtom implements SanchayDOMElement {
    
    protected Vector<FramesetVNRole> vnroles;

    public FramesetRole()
    {
        attributes = new FeatureStructureImpl();
        vnroles = new Vector<FramesetVNRole>();

        attributes.addAttribute("\n", "");
        attributes.addAttribute(GlobalProperties.getIntlString("f"), "");
        attributes.addAttribute(GlobalProperties.getIntlString("descr"), "");
    }

    public int countVNRoles()
    {
        return vnroles.size();
    }

    public FramesetVNRole getVNRole(int num)
    {
        return (FramesetVNRole) vnroles.get(num);
    }

    public int addVNRole(FramesetVNRole r)
    {
        vnroles.add(r);
        return vnroles.size();
    }

    public FramesetVNRole removeVNRole(int num)
    {
        return (FramesetVNRole) vnroles.remove(num);
    }

    public void removeVNRole(FramesetVNRole v)
    {
        int ind = vnroles.indexOf(v);

        if(ind != -1)
            removeVNRole(ind);
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("role"));

        int count = countVNRoles();

        for (int i = 0; i < count; i++)
        {
            FramesetVNRole child = getVNRole(i);

            DOMElement idomElement = child.getDOMElement();

            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public void readXML(Element domElement)
    {
        super.readXML(domElement);

        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(GlobalProperties.getIntlString("vnrole")))
                {
                    FramesetVNRole framesetVNRole = new FramesetVNRole();
                    framesetVNRole.readXML(element);
                    addVNRole(framesetVNRole);
                }
            }

            node = node.getNextSibling();
        }
    }
}
