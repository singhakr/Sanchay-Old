/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;
import sanchay.GlobalProperties;
import sanchay.table.SanchayTableModel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class KeystrokeCellEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

    protected Component owner;
    protected String langEnc;

    protected KeystrokeEditorJPanel keystrokeEditorJPanel;
    protected JButton button;

    protected KeyStroke currentValue;
    protected String currentLabel;
    protected SanchayTableModel currentTable;

    protected int row;
    protected int col;

    protected static final String EDIT = GlobalProperties.getIntlString("edit");

    public KeystrokeCellEditor(Component owner, String langEnc) {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        this.owner = owner;
        this.langEnc = langEnc;

        UtilityFunctions.setComponentFont(button, langEnc);
    }
    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand()) && currentTable != null) {
            //The user has clicked the cell, so
            //bring up the dialog.
//            button.setText(currentLabel);
            button.setText(getKeyStrokeLabel(currentValue));

            keystrokeEditorJPanel = new KeystrokeEditorJPanel();

            keystrokeEditorJPanel.addActionListener(this);

            keystrokeEditorJPanel.setKeyStroke(currentValue);

            SanchayJDialog dlg = new SanchayJDialog((Frame) owner, "Edit Keystroke", true, keystrokeEditorJPanel);
            dlg.pack();
//            dlg.setBounds(0, 0, 300, 140);
//            dlg.addWindowListener(this);

            UtilityFunctions.centre(dlg);
            dlg.setVisible(true);

            //Make the renderer reappear.
            fireEditingStopped();

        } else { //User pressed dialog's "OK" button.
            currentValue = keystrokeEditorJPanel.getKeyStroke();

            currentTable.setValueAt(currentValue, row, col);

            button.setText(getKeyStrokeLabel(currentValue));

//            if(currentValue != null && keystrokeEditorJPanel != null)
//            {
//                currentValue = keystrokeEditorJPanel.getKeyStroke();
//            }
        }
    }

    public static String getKeyStrokeLabel(KeyStroke keyStroke)
    {
        String keyStr = "";

        if(UtilityFunctions.flagOn(keyStroke.getModifiers(), InputEvent.SHIFT_DOWN_MASK))
        {
            keyStr = "Shift + ";
        }

        if(UtilityFunctions.flagOn(keyStroke.getModifiers(), InputEvent.CTRL_DOWN_MASK))
        {
            keyStr += "Control + ";
        }

        if(UtilityFunctions.flagOn(keyStroke.getModifiers(), InputEvent.ALT_DOWN_MASK))
        {
            keyStr += "Alt + ";
        }

        if(Character.isDefined(keyStroke.getKeyChar()))
            keyStr += keyStroke.getKeyChar();
        else
            keyStr += (char) keyStroke.getKeyCode();

        return keyStr;
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
//        return editor.getModel().getName();
//        currentLabel = button.getText();
//
//        currentValue = KeyStroke.getKeyStroke(currentLabel);

        button.setText(getKeyStrokeLabel(currentValue));

        return currentValue;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if(column == 2)
        {
            Object keyStrokeObj = table.getModel().getValueAt(row, column);
            KeyStroke keyStroke = null;

            if(keyStrokeObj instanceof String)
                keyStroke = KeyStroke.getKeyStroke((String) keyStrokeObj);
            else
                keyStroke = (KeyStroke) keyStrokeObj;

            if(Character.isDefined((char) keyStroke.getKeyCode()))
                button.setText(KeystrokeCellEditor.getKeyStrokeLabel(keyStroke));
            else
                button.setText("");

            currentValue = keyStroke;
        }
        else
            button.setText((String) value);

//        button.setText(value.toString());
//        button.setText(value.toString());
//        button.setText(getKeyStrokeLabel(currentValue));

        this.row = row;
        this.col = column;

        currentTable = (SanchayTableModel) table.getModel();

//        Object val = table.getValueAt(row, column);
//
//        if(currentValue != null)
//        {
//            keyStroke = KeyStroke.getKeyStroke(currentValue);
//        }

        return button;
    }
}
