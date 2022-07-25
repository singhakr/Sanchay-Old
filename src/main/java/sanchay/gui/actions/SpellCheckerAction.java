/*
 * SpellCheckerAction.java
 *
 * Created on April 1, 2006, 4:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.actions;

import sanchay.text.spell.gui.SpellCheckerJPanel;
import java.awt.event.*;
import javax.swing.*;

import sanchay.GlobalProperties;
import sanchay.gui.common.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class SpellCheckerAction extends AbstractAction {
    protected SpellCheckerJPanel currentSpellJPanel;
    
    public static final int CLOSE_ACTION = 0;
    public static final int IGNORE_ACTION = 1;
    public static final int IGNORE_ALL_ACTION = 2;
    public static final int REPLACE_ACTION = 3;
    public static final int REPLACE_ALL_ACTION = 4;
    public static final int ADD_TO_DICT_ACTION = 5;
    public static final int AUTO_CORRECT_ACTION = 6;
    public static final int SHOW_OPTIONS_ACTION = 7;
    public static final int SAVE_OUTPUT_ACTION = 8;
    public static final int DEFAULTS_ACTION = 9;
    public static final int _TOTAL_ACTIONS_ = 10;
    
    /** Creates a new instance of SpellCheckerAction */
    public SpellCheckerAction(SpellCheckerJPanel frJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);
        
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentSpellJPanel = frJPanel;
    }

    public SpellCheckerAction(SpellCheckerJPanel frJPanel, String text) {
        super(text);

        currentSpellJPanel = frJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
    
    }
    
    public static SpellCheckerAction createAction(SpellCheckerJPanel jpanel, int mode)
    {
	SpellCheckerAction act = null;
	String lbl = "";
	
	switch(mode)
	{
	    case CLOSE_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Close")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.close(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Close_the_window."));
		return act;

	    case IGNORE_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Ignore")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.ignore(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Ignore_the_word."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		return act;
		
	    case IGNORE_ALL_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Ignore_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.ignoreAll(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Ignore_all_occurrences_of_this_word."));
		return act;
		
	    case REPLACE_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Replace")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.replace(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_once."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;
		
	    case REPLACE_ALL_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Replace_All")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.replaceAll(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_all_occurrences_of_this_word."));
		return act;
		
	    case ADD_TO_DICT_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Add")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.addToDictionary(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_this_word_to_the_dictionary."));
		return act;
		
	    case AUTO_CORRECT_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Auto-Correct")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.autoCorrect(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Auto-Correct_all_words."));
		
		return act;
		
	    case SHOW_OPTIONS_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Hide_Options")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.showOptions(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_options."));
		return act;
		
	    case SAVE_OUTPUT_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Save_Output")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.saveOutput(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Auto-correct_in_all_documents_and_save_the_output."));
		return act;
		
	    case DEFAULTS_ACTION:
		act = new SpellCheckerAction(jpanel, GlobalProperties.getIntlString("Defaults")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentSpellJPanel.defaults(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Reset_defaults."));
		return act;
	}
	
	return act;
    }
}
