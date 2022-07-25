/*
 * ComboFindAction.java
 *
 * Created on June 17, 2006, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.actions;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.Position;
import sanchay.list.gui.actions.ListModelFindAction;

/**
 *
 * @author Anil Kumar Singh
 */
public class ComboFindAction extends ListModelFindAction{
    
    protected boolean changed(JComponent comp, String searchString, Position.Bias bias){
	JComboBox combo = (JComboBox)comp;
	boolean startingFromSelection = true;
	int max = combo.getModel().getSize();
	int increment = 0;
	if(bias!=null)
	    increment = (bias == Position.Bias.Forward) ? 1 : -1;
	int startingRow = (combo.getSelectedIndex() + increment + max) % max;
	if (startingRow < 0 || startingRow >= combo.getModel().getSize()) {
	    startingFromSelection = false;
	    startingRow = 0;
	}
	
	int index = getNextMatch(combo.getModel(), searchString, startingRow, bias);
	if (index != -1) {
	    combo.setSelectedIndex(index);
	    return true;
	} else if (startingFromSelection) {
	    index = getNextMatch(combo.getModel(), searchString, 0, bias);
	    if (index != -1) {
		combo.setSelectedIndex(index);
		return true;
	    }
	}
	return false;
    }
}