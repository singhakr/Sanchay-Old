/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.lexicon;

import java.util.Iterator;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class LexicalFrameType extends DefaultLexicalFrame implements SanchayDOMElement {

    protected int frequency = 1;

    public LexicalFrameType()
    {
        super();
    }

    public LexicalFrameType(LexicalFrameInstance lexicalFrameInstance)
    {
        super();
        
        stem = lexicalFrameInstance.stem;

        addAllSlots(lexicalFrameInstance);
    }

    /**
     * @return the frequency
     */
    public int getFrequency()
    {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }    

    @Override
    public String makeString()
    {
        String str = "";

        str += stem + ": ";

        Iterator itr = getSlotKeys();

        while(itr.hasNext())
        {
            String key = (String) itr.next();
            LexicalSlot slot = getSlot(key);

            str += slot.makeString() + " ";
        }

        str = str.trim() + " (" + frequency + ")";

        return str;
    }

    public String makeFrameString()
    {
        String str = "";

        Iterator itr = getSlotKeys();

        while(itr.hasNext())
        {
            String key = (String) itr.next();
            LexicalSlot slot = getSlot(key);

            str += slot.makeString() + " ";
        }

        str = str.trim();

        return str;
    }
}
