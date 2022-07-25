/*
 * ListFindAction.java
 *
 * Created on June 17, 2006, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.list.gui.actions;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.text.Position;
import sanchay.gui.actions.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class ListFindAction extends ListModelFindAction{
    
    protected boolean changed(JComponent comp, String searchString, Position.Bias bias){
	JList list = (JList)comp;
	boolean startingFromSelection = true;
	int max = list.getModel().getSize();
	int increment = 0;
	if(bias!=null)
	    increment = (bias==Position.Bias.Forward) ? 1 : -1;
	int startingRow = (list.getLeadSelectionIndex()+increment+max)%max;
	if(startingRow<0 || startingRow>= list.getModel().getSize()){
	    startingFromSelection = false;
	    startingRow = 0;
	}
	
	int index = getNextMatch(list.getModel(), searchString, startingRow, bias);
	if(index!=-1){
	    changeSelection(list, index);
	    return true;
	} else if(startingFromSelection){
	    index = getNextMatch(list.getModel(), searchString, 0, bias);
	    if(index!=-1){
		changeSelection(list, index);
		return true;
	    }
	}
	return false;
    }
    
    protected void changeSelection(JList list, int index){
	if(controlDown)
	    list.addSelectionInterval(index, index);
	else
	    list.setSelectedIndex(index);
	list.ensureIndexIsVisible(index);
    }
}