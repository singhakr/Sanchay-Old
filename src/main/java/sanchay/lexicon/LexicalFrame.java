/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.lexicon;

import java.util.Iterator;

/**
 *
 * @author anil
 */
public interface LexicalFrame {

    int addAllSlots(LexicalFrameInstance lf);

    int addSlot(String k, LexicalSlot v);

    int countSlots();

    LexicalSlot getSlot(String k);

    Iterator getSlotKeys();

    /**
     * @return the stem
     */
    String getStem();

    String makeString();

    LexicalSlot removeSlot(String k);

    /**
     * @param stem the stem to set
     */
    void setStem(String stem);

}
