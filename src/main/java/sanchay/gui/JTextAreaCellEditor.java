/*
 * TextPaneCellEditor.java
 *
 * Created on January 30, 2006, 11:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class JTextAreaCellEditor extends AbstractCellEditor
	 implements TableCellEditor, TreeCellEditor, InputMethodListener, Serializable
{
    String currentText;

    JScrollPane scrollPane;
    JTextArea textArea;
    
    /** Creates a new instance of JTextAreaCellEditor */
    public JTextAreaCellEditor()
    {
	scrollPane = new JScrollPane();
        textArea = new JTextArea();
	scrollPane.setViewportView(textArea);
	
	textArea.addInputMethodListener(this);
    }
    
    public void caretPositionChanged(InputMethodEvent event) {
	stopCellEditing();
    }

    public void inputMethodTextChanged(InputMethodEvent event) {
	stopCellEditing();
	currentText = textArea.getText();
    }
    
    public void setValue(Object value) {
	textArea.setText((value != null) ? value.toString() : "");
    }
    
    public Object getCellEditorValue() {
        return textArea.getText();
    }

    public Component getTableCellEditorComponent(JTable table,
	    Object value,
	    boolean isSelected,
	    int row,
	    int column) {
        currentText = (String) value;
	setValue(value);
        return scrollPane;
    }
    
    public Component getTreeCellEditorComponent(JTree tree,
	     Object value,
	     boolean isSelected,
	     boolean expanded,
	     boolean leaf,
	     int row)
    {
        currentText = (String) value;
	setValue(value);
        return scrollPane;
    }
    
    public void setFont(Font font)
    {
	textArea.setFont(font);
    }
    
    public Font getFont()
    {
	return textArea.getFont();
    }
}
