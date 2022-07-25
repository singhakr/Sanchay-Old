/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.lexicon;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class DefaultLexicalFrame implements SanchayDOMElement, LexicalFrame {

    protected String stem;

    protected LinkedHashMap<String, LexicalSlot> slots;

    public DefaultLexicalFrame()
    {
        slots = new LinkedHashMap<String, LexicalSlot>();
    }

    /**
     * @return the stem
     */
    public String getStem()
    {
        return stem;
    }

    /**
     * @param stem the stem to set
     */
    public void setStem(String stem)
    {
        this.stem = stem;
    }

    public int countSlots()
    {
        return slots.size();
    }

    public Iterator getSlotKeys()
    {
        return slots.keySet().iterator();
    }

    public LexicalSlot getSlot(String k)
    {
        return slots.get(k);
    }

    public int addSlot(String k, LexicalSlot v)
    {
        slots.put(k, v);

        return slots.size();
    }

    public int addAllSlots(LexicalFrameInstance lf)
    {
        slots.putAll(lf.slots);

        return slots.size();
    }

    public LexicalSlot removeSlot(String k)
    {
        return slots.remove(k);
    }

    @Override
    public DOMElement getDOMElement()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getXML()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void readXML(Element domElement)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void printXML(PrintStream ps)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String makeString()
    {
        return "";
    }

    public boolean framesEqual(LexicalFrame lexicalFrame)
    {
        boolean equal = true;

        if(countSlots() != lexicalFrame.countSlots())
            return false;

        Iterator itr = getSlotKeys();

        while(itr.hasNext())
        {
            String key = (String) itr.next();

            LexicalSlot slot = getSlot(key);

            LexicalSlot slotOther = lexicalFrame.getSlot(key);

            if(slotOther == null)
                return false;

            if(slot.getFeatures().makeString().equals(slotOther.getFeatures().makeString()) == false)
                return false;
        }

        itr = lexicalFrame.getSlotKeys();

        while(itr.hasNext())
        {
            String key = (String) itr.next();

            LexicalSlot slot = lexicalFrame.getSlot(key);

            LexicalSlot slotOther = getSlot(key);

            if(slotOther == null)
                return false;

            if(slot.getFeatures().makeString().equals(slotOther.getFeatures().makeString()) == false)
                return false;
        }

        return equal;
    }

    public boolean stemFramesEqual(LexicalFrame lexicalFrame)
    {
        if(stem.equals(lexicalFrame.getStem()) == false)
            return false;

        if(framesEqual(lexicalFrame) == false)
            return false;

        return true;
    }
}
