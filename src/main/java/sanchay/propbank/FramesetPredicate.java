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
public class FramesetPredicate extends FramesetAtom implements SanchayDOMElement {

    protected String note = "";

    protected Vector<FramesetRoleset> rolesets;

    public FramesetPredicate()
    {
        attributes = new FeatureStructureImpl();
        rolesets = new Vector<FramesetRoleset>();
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

    public int countRolesets()
    {
        return rolesets.size();
    }

    public FramesetRoleset getRoleset(int num)
    {
        return (FramesetRoleset) rolesets.get(num);
    }

    public int addRoleset(FramesetRoleset r)
    {
        rolesets.add(r);
        return rolesets.size();
    }

    public FramesetRoleset removeRoleset(int num)
    {
        return (FramesetRoleset) rolesets.remove(num);
    }

    public void removeRoleset(FramesetRoleset r)
    {
        int ind = rolesets.indexOf(r);

        if(ind != -1)
            rolesets.remove(ind);
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("predicate"));

        DOMElement idomElement = new DOMElement(GlobalProperties.getIntlString("note"));

        idomElement.setText(note);

        domElement.add(idomElement);

        int count = countRolesets();

        for (int i = 0; i < count; i++)
        {
            FramesetRoleset child = getRoleset(i);

            idomElement = child.getDOMElement();

            domElement.add(idomElement);
        }

        return domElement;
    }

    @Override
    public void readXML(Element domElement)
    {
        super.readXML(domElement);

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
                else if(element.getTagName().equals(GlobalProperties.getIntlString("roleset")))
                {
                    FramesetRoleset framesetRoleset = new FramesetRoleset();
                    framesetRoleset.readXML(element);
                    addRoleset(framesetRoleset);
                }
            }

            node = node.getNextSibling();
        }

        note = note.trim();
    }
}
