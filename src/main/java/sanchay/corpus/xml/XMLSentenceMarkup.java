/*
 * XMLSentenceMarkuo.java
 *
 * Created on September 30, 2005, 12:21 AM
 */

package sanchay.corpus.xml;

import java.util.*;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public class XMLSentenceMarkup {
    
    private Vector markedElements;
    
    /** Creates a new instance of XMLSentenceMarkup */
    public XMLSentenceMarkup() {
        markedElements = new Vector(0, 3);
    }
    
    public int countMarkedElements()
    {
        return markedElements.size();
    }

    public void addMarkedElement(XMLMarkedElement m)
    {
        markedElements.add(m);
    }

    public void addMarkedElements(Collection c)
    {
        markedElements.addAll(c);
    }

    public XMLMarkedElement removeMarkedElement(int index)
    {
        return (XMLMarkedElement) markedElements.remove(index);
    }

    public XMLMarkedElement getMarkedElement(int index)
    {
            return (XMLMarkedElement) markedElements.get(index);
    }

    public Vector getCopyOfMarkedElements()
    {
            return new Vector(markedElements);
    }

    public int findMarkedElement(XMLMarkedElement m)
    {
            return markedElements.indexOf(m);
    }

    public void modifyMarkedElement(XMLMarkedElement m, int index)
    {
        markedElements.setElementAt(m, index);
    }
}
