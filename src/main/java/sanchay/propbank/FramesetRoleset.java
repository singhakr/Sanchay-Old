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
public class FramesetRoleset extends FramesetAtom implements SanchayDOMElement {

    protected String note = "";
    protected String rolesNote = "";

    protected Vector<FramesetRole> roles;

    protected Vector<FramesetExample> examples;

    public FramesetRoleset()
    {
        attributes = new FeatureStructureImpl();
        roles = new Vector<FramesetRole>();
        examples = new Vector<FramesetExample>();
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
     * @return the rolesNote
     */
    public String getRolesNote()
    {
        return rolesNote;
    }

    /**
     * @param rolesNote the rolesNote to set
     */
    public void setRolesNote(String rolesNote)
    {
        this.rolesNote = rolesNote;
    }

    public int countRoles()
    {
        return roles.size();
    }

    public FramesetRole getRole(int num)
    {
        return (FramesetRole) roles.get(num);
    }

    public int addRole(FramesetRole r)
    {
        roles.add(r);
        return roles.size();
    }

    public FramesetRole removeRole(int num)
    {
        return (FramesetRole) roles.remove(num);
    }

    public void removeRole(FramesetRole r)
    {
        int ind = roles.indexOf(r);

        if(ind != -1)
            removeRole(ind);
    }

    public String getRolesetInfo()
    {
        String info = GlobalProperties.getIntlString("ID_=_") + getAttributes().getAttributeValue(GlobalProperties.getIntlString("id")) + "\n";
        info += GlobalProperties.getIntlString("Name_=_") + getAttributes().getAttributeValue(GlobalProperties.getIntlString("name")) + "\n";

        int count = countRoles();

        for (int i = 0; i < count; i++)
        {
            FramesetRole role = getRole(i);

            info += GlobalProperties.getIntlString("Arg") + i + " = " + role.getAttributes().getAttributeValue(GlobalProperties.getIntlString("descr")) + "\n";
        }

        return info;
    }

    public int countExamples()
    {
        return examples.size();
    }

    public FramesetExample getExample(int num)
    {
        return (FramesetExample) examples.get(num);
    }

    public int addExample(FramesetExample e)
    {
        examples.add(e);
        return examples.size();
    }

    public FramesetExample removeExample(int num)
    {
        return (FramesetExample) examples.remove(num);
    }

    public void removeExample(FramesetExample e)
    {
        int ind = examples.indexOf(e);

        if(ind != -1)
            examples.remove(ind);
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("roleset"));

        DOMElement idomElement = new DOMElement(GlobalProperties.getIntlString("note"));

        idomElement.setText(note.trim());

        domElement.add(idomElement);

        DOMElement rdomElement = new DOMElement(GlobalProperties.getIntlString("roles"));

        int count = countRoles();

        for (int i = 0; i < count; i++)
        {
            FramesetRole child = getRole(i);

            idomElement = child.getDOMElement();

            rdomElement.add(idomElement);
        }

        domElement.add(rdomElement);

        count = countExamples();

        for (int i = 0; i < count; i++)
        {
            FramesetExample child = getExample(i);

            idomElement = child.getDOMElement();

            domElement.add(idomElement);
        }

        idomElement = new DOMElement(GlobalProperties.getIntlString("note"));

        idomElement.setText(rolesNote.trim());

        domElement.add(idomElement);

        return domElement;
    }

    @Override
    public void readXML(Element domElement)
    {
        super.readXML(domElement);

        Node node = domElement.getFirstChild();

        note = "";
        rolesNote = "";

        boolean rolesNoteOn = false;

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals(GlobalProperties.getIntlString("note")))
                {
                    String n = element.getTextContent();

                    if(rolesNoteOn)
                    {
                        rolesNote += n + "\n";
                    }
                    else
                    {
                        note += n + "\n";
                        rolesNoteOn = true;
                    }

                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("roles")))
                {
                    Node inode = element.getFirstChild();

                    while(inode != null)
                    {
                        if(inode instanceof Element)
                        {
                            Element ielement = (Element) inode;

                            if(ielement.getTagName().equals(GlobalProperties.getIntlString("role")))
                            {
                                FramesetRole framesetRole = new FramesetRole();
                                framesetRole.readXML(ielement);
                                addRole(framesetRole);
                            }
                        }

                        inode = inode.getNextSibling();
                    }

                    rolesNoteOn = true;
                }
                else if(element.getTagName().equals(GlobalProperties.getIntlString("example")))
                {
                    FramesetExample framesetExample = new FramesetExample();
                    framesetExample.readXML(element);
                    addExample(framesetExample);

                    rolesNoteOn = true;
                }
            }

            node = node.getNextSibling();
        }
    }
}
