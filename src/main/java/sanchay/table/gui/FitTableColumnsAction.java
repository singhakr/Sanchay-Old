/*
 * FitTableColumnsAction.java
 *
 * Created on June 19, 2006, 11:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.table.gui;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import sanchay.GlobalProperties;

/**
 * MySwing: Advanced Swing Utilites
 * Copyright (C) 2005 Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

public class FitTableColumnsAction extends AbstractAction{
    public FitTableColumnsAction(){
	super(GlobalProperties.getIntlString("Fit_Table_Columns"));
    }
    
    public void actionPerformed(ActionEvent ae){
	JTable table = (JTable)ae.getSource();
	JTableHeader header = table.getTableHeader();
	int rowCount = table.getRowCount();
	
	Enumeration columns = table.getColumnModel().getColumns();
	while(columns.hasMoreElements()){
	    TableColumn column = (TableColumn)columns.nextElement();
	    int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
	    int width = (int)table.getTableHeader().getDefaultRenderer()
	    .getTableCellRendererComponent(table, column.getIdentifier()
	    , false, false, -1, col).getPreferredSize().getWidth();
	    for(int row = 0; row<rowCount; row++){
		int preferedWidth = (int)table.getCellRenderer(row, col).getTableCellRendererComponent(table,
		table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
		width = Math.max(width, preferedWidth);
	    }
	    header.setResizingColumn(column); // this line is very important
	    column.setWidth(width+table.getIntercellSpacing().width);
	}
    }
}