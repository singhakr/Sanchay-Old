/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.shell;

import sanchay.gui.common.SanchayEvent;

/**
 *
 * @author anil
 */
public class SanchayShellEvent extends SanchayEvent {

    public static final int SHELL_COMMAND_EVENT = 0;
    public static final int HISTORY_NEXT_EVENT = 1;
    public static final int HISTORY_PREVIOUS_EVENT = 2;
    public static final int AUTO_COMPLETION_EVENT = 3;

    /** Creates a new instance of TreeViewerEvent */
    public SanchayShellEvent(Object source, int id) {
        super(source, id);
    }
}
