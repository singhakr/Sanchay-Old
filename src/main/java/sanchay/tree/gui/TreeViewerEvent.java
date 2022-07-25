/*
 * TreeViewerEvent.java
 *
 * Created on 10 November, 2008, 8:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree.gui;

import sanchay.gui.common.SanchayEvent;

/**
 *
 * @author ayush
 */
public class TreeViewerEvent extends SanchayEvent {
        
    public static final int TREE_CHANGED_EVENT = 0;

    
    /** Creates a new instance of TreeViewerEvent */
    public TreeViewerEvent(Object source, int id) {
        super(source, id);
    }

}
