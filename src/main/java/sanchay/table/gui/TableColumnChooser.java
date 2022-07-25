/*
 * TableColumnChooser.java
 *
 * Created on June 17, 2006, 3:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.table.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author anil
 */
public class TableColumnChooser extends MouseAdapter implements ActionListener{
    
    // client property used to specify fixed columns. value is of type int[] which is sorted
    // fixed column = columns that can't be toggled
    public static final String FIXED_COLUMNS = "FixedColumns"; //NOI18N
    
    // client property used to pass column index to actionlistener
    private static final String COLUMN_INDEX = "ColumnIndex";
    
    // client property used to pass jtable to actionlistener
    private static final String JTABLE = "JTable";
    
    private boolean isVisibleColumn(TableColumnModel model, int modelCol){
	for(int i=0; i<model.getColumnCount(); i++)
	    if(model.getColumn(i).getModelIndex()==modelCol)
		return true;
	return false;
    }
    
    /*-----------------------------------[ MouseListener ]------------------------------------------*/
    
    public void mouseReleased(MouseEvent me){
	if(!SwingUtilities.isRightMouseButton(me))
	    return;
	
	JTableHeader header = (JTableHeader)me.getComponent();
	TableModel tableModel = header.getTable().getModel();
	TableColumnModel columnModel = header.getTable().getColumnModel();
	
	int fixedColumns[] = (int[])header.getTable().getClientProperty(FIXED_COLUMNS);
	if(fixedColumns==null)
	    fixedColumns = new int[]{ 0 };
	    
	    JPopupMenu popup = new JPopupMenu();
	    for(int i = 0; i < tableModel.getColumnCount(); i++){
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(tableModel.getColumnName(i));
		item.setSelected(isVisibleColumn(columnModel, i));
		item.setEnabled(Arrays.binarySearch(fixedColumns, i)<0);
		item.putClientProperty(COLUMN_INDEX, new Integer(i));
		item.putClientProperty(JTABLE, header.getTable());
		item.addActionListener(this);
		popup.add(item);
	    }
	    
	    popup.show(header, me.getX(), me.getY());
    }
    
    /*------------------------------------[ ActionListener ]--------------------------------------*/
    
    public void actionPerformed(ActionEvent ae){
	JMenuItem item = (JMenuItem)ae.getSource();
	
	Integer columnIndex = (Integer)item.getClientProperty(COLUMN_INDEX);
	JTable table = (JTable)item.getClientProperty(JTABLE);
	TableColumnModel columnModel = table.getColumnModel();
	
	if(!item.isSelected()){
	    for(int i = 0; i < columnModel.getColumnCount(); i++){
		TableColumn col = columnModel.getColumn(i);
		if(col.getModelIndex() == columnIndex.intValue()){
		    columnModel.removeColumn(col);
		    return;
		}
	    }
	}else
	    table.addColumn(new TableColumn(columnIndex.intValue()));
    }
    
    /*-------------------------------------------------[ Singleton ]---------------------------------------------------*/
    
    private static WeakReference ref = null; // favour gc
    
    private TableColumnChooser(){}
    
    private static TableColumnChooser getInstance(){
	if(ref==null || ref.get()==null)
	    ref = new WeakReference(new TableColumnChooser());
	return (TableColumnChooser)ref.get();
    }
    
    /*-------------------------------------------------[ Usage ]---------------------------------------------------*/
    
    public static void install(JTable table){
	table.getTableHeader().addMouseListener(getInstance());
    }
    
    public static void uninstall(JTable table){
	table.getTableHeader().removeMouseListener(getInstance());
    }
    
    
    /*-------------------------------------------------[ Utilities ]---------------------------------------------------*/
    
    public static void hideColumns(TableColumnModel columnModel, int modelColumnIndexes[]){
	TableColumn column[] = new TableColumn[modelColumnIndexes.length];
	for(int i=0, j=0; i<columnModel.getColumnCount(); i++){
	    TableColumn col = columnModel.getColumn(i);
	    if(col.getModelIndex()==modelColumnIndexes[j]){
		column[j++] = col;
		if(j>=modelColumnIndexes.length)
		    break;
	    }
	}
	for(int i = 0; i<column.length; i++)
	    columnModel.removeColumn(column[i]);
    }
}