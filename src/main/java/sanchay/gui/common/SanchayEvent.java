/*
 * SanchayEvent.java
 *
 * Created on 10 November, 2008, 8:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.util.EventObject;

/**
 *
 * @author Anil Kumar Singh
 */
public class SanchayEvent extends EventObject {
    
    protected int id;
    
    /** Creates a new instance of SanchayEvent */
    public SanchayEvent(Object source) {
        super(source);
    }
    
    public SanchayEvent(Object source, int id) {
        super(source);
        
        this.id = id;
    }

    public int getEventID()
    {
        return id;
    }
}
