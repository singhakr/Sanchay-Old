/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package iitb.model;

/**
 *
 * @author eklavya
 */
public interface EdgeIterator {
    void start();
    boolean hasNext();
    Edge next();
    boolean nextIsOuter(); // returns true if the next edge it will return is outer
}
