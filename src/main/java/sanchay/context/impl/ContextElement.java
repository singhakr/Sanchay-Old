/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.context.impl;

import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author anil
 */
public interface ContextElement<E> {

    E getContextElement();

    void setContextElement(E contextElement);

    long getFreq();

    void setFreq(long freq);

    void print(PrintStream ps) throws IOException, Exception;

}
