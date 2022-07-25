/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel.gui;

import java.util.EventListener;

/**
 *
 * @author anil
 */
public interface AlignmentEventListener extends EventListener {
    void alignmentChanged(AlignmentEvent evt);
}
