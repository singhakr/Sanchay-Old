/*
 * ListModelFindAction.java
 *
 * Created on June 17, 2006, 2:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.list.gui.actions;

import javax.swing.ListModel;
import javax.swing.text.Position;
import sanchay.gui.actions.*;

/**
 *
 * @author Anil Kumar Singh
 */
public abstract class ListModelFindAction extends FindAction{
    
    public int getNextMatch(ListModel model, String prefix, int startIndex, Position.Bias bias){
	int max = model.getSize();
	if(prefix==null){
	    throw new IllegalArgumentException();
	}
	if(startIndex<0 || startIndex>= max){
	    throw new IllegalArgumentException();
	}
	
	if(ignoreCase)
	    prefix = prefix.toUpperCase();
	
	// start search from the next element after the selected element
	int increment = (bias==null || bias == Position.Bias.Forward) ? 1 : -1;
	int index = startIndex;
	do{
	    Object item = model.getElementAt(index);
	    
	    if(item!=null){
		String text = item.toString();
		if(ignoreCase)
		    text = text.toUpperCase();
		
		if(text!=null && text.startsWith(prefix)){
		    return index;
		}
	    }
	    index = (index+increment+max)%max;
	} while(index!=startIndex);
	return -1;
    }
}
