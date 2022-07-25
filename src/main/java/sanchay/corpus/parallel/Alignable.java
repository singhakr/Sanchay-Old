/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author anil
 */
public interface Alignable<T> extends Serializable {

    AlignmentUnit<T> getAlignmentUnit();
    void setAlignmentUnit(AlignmentUnit<T> alignmentUnit);

    T getAlignedObject(String alignmentKey);

    List<T> getAlignedObjects();

    T getFirstAlignedObject();
    T getAlignedObject(int i);
    T getLastAlignedObject();
}
