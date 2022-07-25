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
public class FramesetExampleArgument extends FramesetAtom implements SanchayDOMElement {

    public FramesetExampleArgument()
    {
        super();

        leafNode = true;

        attributes.addAttribute("\n", "");
        attributes.addAttribute(GlobalProperties.getIntlString("f"), "");
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("arg"));

        return domElement;
    }
}
