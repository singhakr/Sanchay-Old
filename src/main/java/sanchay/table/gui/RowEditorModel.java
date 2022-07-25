/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.table.gui;

import java.util.Hashtable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author anil
 */
public class RowEditorModel
{
    private Hashtable data;

    public RowEditorModel()
    {
        data = new Hashtable();
    }

    public void addEditorForRow(int row, TableCellEditor e)
    {
        data.put(new Integer(row), e);
    }

    public void removeEditorForRow(int row)
    {
        data.remove(new Integer(row));
    }

    public TableCellEditor getEditor(int row)
    {
        return (TableCellEditor) data.get(new Integer(row));
    }
}
