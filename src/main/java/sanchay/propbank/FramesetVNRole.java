/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.propbank;

import org.dom4j.dom.DOMElement;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructureImpl;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class FramesetVNRole extends FramesetAtom implements SanchayDOMElement {

    public FramesetVNRole()
    {
        leafNode = true;
        
        attributes = new FeatureStructureImpl();

        attributes.addAttribute(GlobalProperties.getIntlString("vncls"), "");
        attributes.addAttribute(GlobalProperties.getIntlString("vntheta"), "");
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = super.getDOMElement();

        domElement.setName(GlobalProperties.getIntlString("vnrole"));

        return domElement;
    }
}
