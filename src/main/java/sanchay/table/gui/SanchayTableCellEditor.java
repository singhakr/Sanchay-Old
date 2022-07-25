/*
 * SanchayTableCellEditor.java
 *
 * Created on September 28, 2008, 4:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.table.gui;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author eklavya
 */
public class SanchayTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    protected String langEnc;
    protected JTextField textField;
    protected JScrollPane scrollPane;
    protected JTextArea textArea;
    
    protected int mode = SINGLE_ROW;

    public static final int SINGLE_ROW = 0;
    public static final int MULTIPLE_ROW = 1;
   
    /** Creates a new instance of SanchayTableCellEditor */
    public SanchayTableCellEditor(String langEnc) {
        this(langEnc, SINGLE_ROW);
    }

    public SanchayTableCellEditor(String langEnc, int mode) {
        this.langEnc = langEnc;

        this.mode = mode;

        if(mode == SINGLE_ROW)
        {
            textField = new JTextField();
            UtilityFunctions.setComponentFont(textField, langEnc);
        }
        else if(mode == MULTIPLE_ROW)
        {
            scrollPane = new JScrollPane();
            textArea = new JTextArea();
            scrollPane.setViewportView(textArea);
            textArea.setRows(2);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            UtilityFunctions.setComponentFont(textArea, langEnc);
        }
        
    }

    public Object getCellEditorValue() {
        if(mode == MULTIPLE_ROW)
            return textArea.getText();

        return textField.getText();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if(mode == MULTIPLE_ROW)
        {
            if(value == null)
                textArea.setText("");
            else
                textArea.setText(value.toString());
            
            return scrollPane;
        }

        if(value == null)
            textField.setText("");
        else
            textField.setText(value.toString());

        return textField;
   }
    
}
