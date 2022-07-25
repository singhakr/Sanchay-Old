/*
 * TableAction.java
 *
 * Created on October 22, 2005, 6:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.table.gui;

import java.awt.event.*;
import javax.swing.*;

import sanchay.GlobalProperties;

/**
 *
 *  @author Anil Kumar Singh
 */
public class SanchayTableAction extends AbstractAction {

    protected SanchayTableJPanel currentTableJPanel;
    
    public static final int EDIT_TABLE = 0;

    public static final int CONNECT_TO_DB = 1;
    public static final int OPEN_TABLE = 2;

    public static final int SET_SORTABLE = 3;
    
    public static final int SAVE_TABLE = 4;
    public static final int SAVE_TABLE_AS = 5;
    public static final int MARK_FOR_SAVING = 6;
    
    public static final int RESET_ALL = 7;
    public static final int CLEAR_ALL = 8;
    
    public static final int ADD_ROW = 9;
    public static final int DEL_ROW = 10;
    public static final int INSERT_ROW = 11;
    public static final int CLEAR_ROW = 12;
    
    public static final int ADD_COL = 13;
    public static final int INSERT_COL = 14;
    public static final int RENAME_COL = 15;
    public static final int DEL_COL = 16;
    public static final int CLEAR_COL = 17;

    public static final int TABLE_COPY = 18;
    public static final int TABLE_CUT = 19;
    public static final int TABLE_PASTE = 20;
    
    public static final int PRINT_TABLE = 21;
    public static final int SET_TABLE_SEL_MODE = 22;

    public static final int SELECT_LANGUAGE = 23;
    public static final int SELECT_ENCODING = 24;
    
    public static final int SELECT_INPUT_METHOD = 25;
    public static final int SHOW_KB_MAP = 26;

    public static final int INCREASE_FONT_SIZE = 27;
    public static final int DECREASE_FONT_SIZE = 28;

    public static final int INCREASE_ROW_SIZE = 29;
    public static final int DECREASE_ROW_SIZE = 30;
    
    public static final int SHOW_COMMAND_BUTTONS = 31;
    
    // Total number of actions available
    public static final int _TOTAL_ACTIONS_ = 31;
   
    /** Creates a new instance of TableAction */
    public SanchayTableAction(SanchayTableJPanel tableJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);
        
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentTableJPanel = tableJPanel;
    }

    public SanchayTableAction(SanchayTableJPanel tableJPanel, String text) {
        super(text);

        currentTableJPanel = tableJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
    
    }
    
    public static SanchayTableAction createAction(SanchayTableJPanel jpanel, int mode)
    {
	SanchayTableAction act = null;
	
	switch(mode)
	{
	    case CONNECT_TO_DB:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Connect_to_DB")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.connectToDB(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Connect_to_a_database."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;
                
	    case OPEN_TABLE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Open")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.openTable(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Open_a_table."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;
                
	    case SET_SORTABLE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Sortable")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.setSortable(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Make_the_table_sortable_(toggle)."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

            case EDIT_TABLE:
		String lbl = "";
		if(jpanel.getModel().getEditable())
		    lbl = GlobalProperties.getIntlString("Edit_Off");
		else
		    lbl = GlobalProperties.getIntlString("Edit_On");

		act = new SanchayTableAction(jpanel, lbl) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.editTable(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Toggle_edit_mode."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;
		
	    case SAVE_TABLE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Save_Table")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.saveTable(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_table."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		return act;
		
	    case SAVE_TABLE_AS:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Save_Table_As...")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.saveTableAs(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_table_as..."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
		return act;
		
	    case MARK_FOR_SAVING:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Mark_for_Saving")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.markForSaving(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Mark_the_table_for_saving."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		return act;
		
	    case RESET_ALL:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Reset_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.tableResetAll(e);
		    }
		};
		
	        act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Reset_everything."));
		return act;
		
	    case CLEAR_ALL:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Clear_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.tableClearAll(e);
		    }
		};
		
	        act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Clear_everything."));
		return act;
		
	    case ADD_ROW:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Add_Row")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.addTableRow(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_a_row_at_the_end_of_the_table."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		return act;
		
	    case DEL_ROW:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Delete_Row")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.deleteTableRow(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Delete_selected_rows."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		return act;
		
	    case INSERT_ROW:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Insert_Row")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.insertTableRow(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Insert_a_row_before_the_selected_row."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		return act;
		
	    case CLEAR_ROW:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Clear_Row")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.tableRowClear(e);
		    }
		};
		
	        act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Clear_values_in_the_selected_row."));
		return act;

	    case ADD_COL:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Add_Column")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.addTableColumn(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_a_column_at_the_right_end_of_the_table."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
		return act;

	    case INSERT_COL:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Insert_Column")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.insertTableColumn(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Insert_a_column_before_the_selected_column."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

	    case RENAME_COL:
		act = new SanchayTableAction(jpanel, "Rename Column") {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.renameTableColumn(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Insert_a_column_before_the_selected_column."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

	    case DEL_COL:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Delete_Column")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.deleteTableColumn(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Delete_the_selected_columns."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
		return act;

	    case CLEAR_COL:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Clear_Column")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.tableColumnClear(e);
		    }
		};
		
	        act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Clear_values_in_the_selected_column."));
		return act;

	    case TABLE_COPY:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Copy")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.copyTableData(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Copy_table_data."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		return act;

	    case TABLE_CUT:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Cut")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.cutTableData(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Cut_table_data."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
		return act;

	    case TABLE_PASTE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Paste")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.pasteTableData(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Paste_table_data."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		return act;

	    case PRINT_TABLE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Print")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.printTable(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Print_the_table."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;

	    case SET_TABLE_SEL_MODE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Selection_Mode")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.setTableSelectionMode(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_a_row_at_the_end_of_the_table."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		return act;

	    case SELECT_LANGUAGE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Language")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.selectLanguage(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_language."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

	    case SELECT_ENCODING:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Encoding")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.selectEncoding(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_encoding."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

	    case SELECT_INPUT_METHOD:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Input_Method")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.selectInputMethod(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_input_method."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

	    case SHOW_KB_MAP:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Show_Keyboard")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.showKBMap(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_the_keyboard_map."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
		return act;

	    case INCREASE_FONT_SIZE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Zoom_In")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.increaseFontSize(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Increase_font_size."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;

	    case DECREASE_FONT_SIZE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Zoom_Out")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.decreaseFontSize(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Decrease_font_size."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;

	    case INCREASE_ROW_SIZE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Row++")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.increaseRowSize(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Increase_row_size."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;

	    case DECREASE_ROW_SIZE:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Row--")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.decreaseRowSize(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Decrease_font_size."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;

	    case SHOW_COMMAND_BUTTONS:
		act = new SanchayTableAction(jpanel, GlobalProperties.getIntlString("Show_Buttons")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentTableJPanel.showCommandButtons(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_the_command_buttons."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
		return act;
	}
	
	return act;
    }
}
