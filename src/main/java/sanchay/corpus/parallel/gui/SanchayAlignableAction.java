/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import sanchay.table.gui.SanchayJTable;

/**
 *
 * @author anil
 */
public class SanchayAlignableAction extends AbstractAction {

    protected SanchayJTable currentJTable;

    public static final int TABLE_COPY = 0;
    public static final int TABLE_CUT = 1;
    public static final int TABLE_PASTE = 2;
    public static final int TABLE_SET_LABEL = 3;

    // Total number of actions available
    public static final int _TOTAL_ACTIONS_ = 4;

    public SanchayAlignableAction(SanchayJTable table, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentJTable = table;
    }

    public SanchayAlignableAction(SanchayJTable table, String text) {
        super(text);

        currentJTable = table;
    }

    public void actionPerformed(ActionEvent e) {

    }

    public static SanchayAlignableAction createAction(SanchayJTable jtable, int mode)
    {
	SanchayAlignableAction act = null;

	switch(mode)
	{
	    case TABLE_COPY:
		act = new SanchayAlignableAction(jtable, "Copy") {
		    public void actionPerformed(ActionEvent e) {
			this.currentJTable.copyTableData(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "Copy table data");
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		return act;

	    case TABLE_CUT:
		act = new SanchayAlignableAction(jtable, "Cut") {
		    public void actionPerformed(ActionEvent e) {
			this.currentJTable.cutTableData(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "Cut table data");
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
		return act;

	    case TABLE_PASTE:
		act = new SanchayAlignableAction(jtable, "Paste") {
		    public void actionPerformed(ActionEvent e) {
			this.currentJTable.pasteTableData(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "Paste table data");
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		return act;

	    case TABLE_SET_LABEL:
		act = new SanchayAlignableAction(jtable, "Set Label") {
		    public void actionPerformed(ActionEvent e) {
			this.currentJTable.setLabel(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, "Set label");
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		return act;
        }

        return act;
    }
}
