/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.table.gui;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class TreeDrawingTableEditor extends AbstractCellEditor implements TableCellEditor {

    protected String langEnc;
    protected JTextField textField;

    protected Object cellObject;

    public TreeDrawingTableEditor(String langEnc) {
        this.langEnc = langEnc;

        textField = new JTextField();

        UtilityFunctions.setComponentFont(textField, langEnc);
    }

    public boolean isCellEditable(EventObject e) {

        SanchayJTable table = (SanchayJTable) e.getSource();

        int r = table.getSelectedRow();
        int c = table.getSelectedColumn();

        if(r == -1 || c == -1)
            return false;

        cellObject = table.getCellObject(r, c);

        if(cellObject instanceof SSFLexItem)
            return true;
        
        return false;
    }

    public boolean stopCellEditing() {
        
        String text = textField.getText();
        
        if(cellObject instanceof SSFLexItem)
            ((SSFLexItem) cellObject).setLexData(text);

        fireEditingStopped();
        return true;
    }

    public Object getCellEditorValue() {

        String text = textField.getText();

//        if(cellObject instanceof SSFLexItem)
//            ((SSFLexItem) cellObject).setLexData(text);
    
        return text;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        cellObject = ((SanchayJTable) table).getCellObject(row, column);

        if(cellObject != null && cellObject instanceof SSFLexItem)
        {
            ((SSFLexItem) cellObject).setLexData(textField.getText());
            textField.setText(((SSFLexItem) cellObject).getLexData());

            return textField;
        }

        return null;
   }

}
