/*
 * TreeViewerEventListener.java
 *
 * Created on 10 November, 2008, 8:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree;

import java.util.EventListener;
import sanchay.tree.gui.TreeViewerEvent;

/**
 *
 * @author ayush
 */
public interface TreeViewerEventListener extends EventListener {
    void treeChanged(TreeViewerEvent evt);
}
