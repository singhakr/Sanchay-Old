/*
 * ListAutoCompleter.java
 *
 * Created on June 17, 2006, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.list.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import sanchay.gui.common.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class ListAutoCompleter extends AutoCompleter {
    
    private List completionList;
    private boolean ignoreCase;
    
    public ListAutoCompleter(JTextComponent comp, List completionList, boolean ignoreCase){
	super(comp);
	this.completionList = completionList;
	this.ignoreCase = ignoreCase;
    }
    
    // update classes model depending on the data in textfield
    protected boolean updateListData(){
	String value = textComp.getText();
	
	int substringLen = value.length();
	
	List possibleStrings = new ArrayList();
	Iterator iter = completionList.iterator();
	while(iter.hasNext()){
	    String listEntry = (String)iter.next();
	    if(substringLen>=listEntry.length())
		continue;
	    
	    if(ignoreCase){
		if(value.equalsIgnoreCase(listEntry.substring(0, substringLen)))
		    possibleStrings.add(listEntry);
	    }else if(listEntry.startsWith(value))
		possibleStrings.add(listEntry);
	}
	
	list.setListData(possibleStrings.toArray());
	return true;
    }
    
    // user has selected some item in the classes. update textfield accordingly...
    protected void acceptedListItem(String selected){
	if(selected==null)
	    return;
	
	int prefixlen = textComp.getDocument().getLength();
	
	try{
	    textComp.getDocument().insertString(textComp.getCaretPosition(), selected.substring(prefixlen), null);
	} catch(BadLocationException e){
	    e.printStackTrace();
	}
	
	popup.setVisible(false);
    }
}