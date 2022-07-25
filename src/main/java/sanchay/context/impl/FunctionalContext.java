/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.context.impl;

import java.util.Iterator;

/**
 *
 * @author anil
 */
public interface FunctionalContext<K, E, CE extends FunctionalContextElement<E>> extends Context<K, E, CE> {

    long countContextElementTypes(K key);

    long countContextElementTokens(K key);

    Iterator<Integer> getContextElementKeys(K key);

    CE getContextElement(K key, int distance);

    long addContextElement(K key, int distance, CE ce);

    CE removeContextElement(K key, int distance);
}
