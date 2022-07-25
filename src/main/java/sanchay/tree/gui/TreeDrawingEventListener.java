/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.tree.gui;

import java.util.EventListener;

/**
 *
 * @author anil
 */
public interface TreeDrawingEventListener extends EventListener {
    void treeChanged(TreeViewerEvent evt);
}
