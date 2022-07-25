/*
 * BrowseFileAction.java
 *
 * Created on June 17, 2006, 3:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.text.JTextComponent;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class BrowseFileAction extends AbstractAction {
    FileFilter filters[], defaultFilter;
    int selectionMode, dialogType;
    String dialogTitle;
    
    public BrowseFileAction(){
	super(GlobalProperties.getIntlString("Browse..."));
    }
    
    public BrowseFileAction(FileFilter[] filters, FileFilter defaultFilter, int selectionMode, int dialogType, String dialogTitle){
	super(GlobalProperties.getIntlString("Browse..."));
	this.filters = filters;
	this.defaultFilter = defaultFilter;
	this.selectionMode = selectionMode;
	this.dialogType = dialogType;
	this.dialogTitle = dialogTitle;
    }
    
    public void actionPerformed(ActionEvent ae){
	JTextComponent tcomp = (JTextComponent)ae.getSource();
	JFileChooser chooser = new JFileChooser();
	String text = tcomp.getText().trim();
	if(text.length()>0){
	    File dir = new File(text).getParentFile();
	    if(dir!=null)
		chooser.setCurrentDirectory(dir);
	}
	if(filters!=null){
	    for(int i = 0; i<filters.length; i++)
		chooser.addChoosableFileFilter(filters[i]);
	}
	if(defaultFilter!=null)
	    chooser.setFileFilter(defaultFilter);
	chooser.setFileSelectionMode(selectionMode);
	chooser.setDialogType(dialogType);
	chooser.setDialogTitle(dialogTitle);
	chooser.setMultiSelectionEnabled(false);
	if(chooser.showDialog(tcomp, null)==JFileChooser.APPROVE_OPTION){
	    tcomp.setText(chooser.getSelectedFile().toString());
	    tcomp.setCaretPosition(0);
	}
    }
}
