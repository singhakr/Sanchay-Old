/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.gui;

import sanchay.gui.common.SanchayEvent;

/**
 *
 * @author Dave
 */
public class NavigatetoValidateEvent extends SanchayEvent {

    public static final int NAVIGATE_EVENT = 100;
    protected String sentenceID;
    protected String nodeID;
    protected String fileID;

    public NavigatetoValidateEvent(Object source, int id, String nodeID, String sentenceID, String fileID) {
        super(source, id);
        this.nodeID = nodeID;
        this.sentenceID = sentenceID;        
        this.fileID = fileID;
    }

    public String[] getLocationStringArray()
    {
        return new String[] {this.nodeID, this.sentenceID, this.fileID};
    }

}
