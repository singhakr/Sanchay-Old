/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.propbank;

import org.dom4j.dom.DOMElement;
import sanchay.GlobalProperties;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class FramesetExampleRelation extends FramesetAtom implements SanchayDOMElement {

    public FramesetExampleRelation()
    {
        super();

        leafNode = true;

        attributes.addAttribute(GlobalProperties.getIntlString("f"), "");
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("rel"));

        return domElement;
    }
}
