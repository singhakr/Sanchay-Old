/*
 * FunctionalContextElement.java
 *
 * Created on January 18, 2009, 5:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.context.impl;

import sanchay.context.impl.ContextElement;

/**
 *
 * @author Anil Kumar Singh
 */
public interface FunctionalContextElement<E> extends ContextElement<E> {

    short getDistance();
    
    void setDistance(short distance);
}
