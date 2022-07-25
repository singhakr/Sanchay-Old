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
public class FramesetExampleInflections extends FramesetAtom implements SanchayDOMElement {

    public FramesetExampleInflections()
    {
        super();

        leafNode = true;

        attributes.addAttribute(GlobalProperties.getIntlString("person"), GlobalProperties.getIntlString("ns"));
        attributes.addAttribute(GlobalProperties.getIntlString("tense"), GlobalProperties.getIntlString("ns"));
        attributes.addAttribute(GlobalProperties.getIntlString("aspect"), GlobalProperties.getIntlString("ns"));
        attributes.addAttribute(GlobalProperties.getIntlString("voice"), GlobalProperties.getIntlString("ns"));
        attributes.addAttribute(GlobalProperties.getIntlString("form"), GlobalProperties.getIntlString("ns"));
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("inflection"));

        return domElement;
    }
}
