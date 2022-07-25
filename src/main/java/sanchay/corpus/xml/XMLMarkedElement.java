/*
 * XMLMarkedElement.java
 *
 * Created on September 30, 2005, 12:13 AM
 */

package sanchay.corpus.xml;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public class XMLMarkedElement {
    
    private int startPosition;
    private int endPosition;

    private int marker;
    
    /** Creates a new instance of XMLMarkedElement */
    public XMLMarkedElement() {
    }
    
    public int getStartPosition()
    {
        return startPosition;
    }
    
    public void setStartPosition(int p)
    {
        startPosition = p;
    }
    
    public int getEndPosition()
    {
        return endPosition;
    }
    
    public void setEndPosition(int p)
    {
        endPosition = p;
    }
    
    public int getMarker()
    {
        return marker;
    }
    
    public void setMarker(int m)
    {
        marker = m;
    }
}
