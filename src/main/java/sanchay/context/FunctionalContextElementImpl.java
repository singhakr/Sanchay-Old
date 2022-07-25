/*
 * FunctionalContextElementImpl.java
 *
 * Created on January 18, 2009, 5:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.context;

import sanchay.context.impl.FunctionalContextElement;
import java.io.IOException;
import java.io.PrintStream;
import sanchay.text.DictionaryFSTNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class FunctionalContextElementImpl<E> extends ContextElementImpl<E> implements FunctionalContextElement<E> {
    
    protected short distance;
    
    /** Creates a new instance of FunctionalContextElementImpl */
    public FunctionalContextElementImpl() {
    }

    public short getDistance() {
        return distance;
    }

    public void setDistance(short distance) {
        this.distance = distance;
    }

    public void print(PrintStream ps) throws IOException, Exception
    {
        ps.println("\t" + contextElement.toString() + "\t" + distance + "\t" + freq);
    }
}
