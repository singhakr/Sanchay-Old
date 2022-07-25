/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.xml.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class SaxErrorHandler implements ErrorHandler {
    
    public void warning(SAXParseException exception) throws SAXException {
        String msg = GlobalProperties.getIntlString("XML_Parser_Warning:_") + GlobalProperties.getIntlString("Line:_") + exception.getLineNumber()
                + "\n" + GlobalProperties.getIntlString("URI:_") + exception.getSystemId() + "\n"
                + GlobalProperties.getIntlString("Message:_") + exception.getMessage();
        
        throw new SAXException(msg);
    }

    public void error(SAXParseException exception) throws SAXException {
        String msg = GlobalProperties.getIntlString("XML_Parser_Error:_") + GlobalProperties.getIntlString("Line:_") + exception.getLineNumber()
                + "\n" + GlobalProperties.getIntlString("URI:_") + exception.getSystemId() + "\n"
                + GlobalProperties.getIntlString("Message:_") + exception.getMessage();

        throw new SAXException(msg);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        String msg = GlobalProperties.getIntlString("XML_Parser_Fatal_Error:_") + GlobalProperties.getIntlString("Line:_") + exception.getLineNumber()
                + "\n" + GlobalProperties.getIntlString("URI:_") + exception.getSystemId() + "\n"
                + GlobalProperties.getIntlString("Message:_") + exception.getMessage();

        throw new SAXException(msg);
    }
}
