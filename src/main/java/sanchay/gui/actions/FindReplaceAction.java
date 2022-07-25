/*
 * FindReplaceAction.java
 *
 * Created on March 15, 2006, 8:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.actions;

import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import javax.xml.parsers.ParserConfigurationException;
import sanchay.GlobalProperties;
import sanchay.gui.common.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class FindReplaceAction extends AbstractAction {
    protected IntegratedResourceAccessorJPanel currentFRJPanel;
    
    public static final int CLOSE_ACTION = 0;
    public static final int SAVE_OUTPUT_ACTION = 1;
    public static final int REPLACE_ALL_ACTION = 2;
    public static final int REPLACE_ACTION = 3;
    public static final int DEFAULTS_ACTION = 4;
    public static final int SHOW_OPTIONS_ACTION = 5;
    public static final int FIND_EXTRACT_ACTION = 6;
    public static final int KWIK_VIEW_ACTION = 7;
//    public static final int SIMILAR_DOC_ACTION = 8;
    public static final int _TOTAL_ACTIONS_ = 8;
    
    /** Creates a new instance of FindReplaceAction */
    public FindReplaceAction(IntegratedResourceAccessorJPanel frJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);
        
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentFRJPanel = frJPanel;
    }

    public FindReplaceAction(IntegratedResourceAccessorJPanel frJPanel, String text) {
        super(text);

        currentFRJPanel = frJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
    
    }
    
    public static FindReplaceAction createAction(IntegratedResourceAccessorJPanel jpanel, int mode)
    {
	FindReplaceAction act = null;
	String lbl = "";
	
	switch(mode)
	{
	    case CLOSE_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Close")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentFRJPanel.close(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Close_the_window."));
		return act;

	    case SAVE_OUTPUT_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Save_Output")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentFRJPanel.saveOutput(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Extract/replace_in_all_matched_documents_and_save_the_output."));
		return act;
		
	    case REPLACE_ALL_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Replace_All")) {
		    public void actionPerformed(ActionEvent e) {
                try {
                    this.currentFRJPanel.replaceAll(e);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(FindReplaceAction.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(FindReplaceAction.class.getName()).log(Level.SEVERE, null, ex);
                }
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_all_in_the_selected_document."));
		return act;
		
	    case REPLACE_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Replace")) {
		    public void actionPerformed(ActionEvent e) {
                try {
                    this.currentFRJPanel.replace(e);
                } catch (Exception ex) {
                    Logger.getLogger(FindReplaceAction.class.getName()).log(Level.SEVERE, null, ex);
                }
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_once."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;
		
	    case DEFAULTS_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Defaults")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentFRJPanel.defaults(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Reset_defaults."));
		return act;
		
	    case SHOW_OPTIONS_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Hide_Options")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentFRJPanel.showOptions(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_options."));
		return act;
		
	    case FIND_EXTRACT_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Find")) {
		    public void actionPerformed(ActionEvent e) {
                try {
                    this.currentFRJPanel.findExtract(e);
                } catch (Exception ex) {
                    Logger.getLogger(FindReplaceAction.class.getName()).log(Level.SEVERE, null, ex);
                }
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Find/extract_matches"));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
		
		return act;

	    case KWIK_VIEW_ACTION:
		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Show_KWIC_View")) {
		    public void actionPerformed(ActionEvent e) {
                try {
                    this.currentFRJPanel.showKWIKView(e);
                } catch (Exception ex) {
                    Logger.getLogger(FindReplaceAction.class.getName()).log(Level.SEVERE, null, ex);
                }
		    }
		};

		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("See_the_results_in_KWIC_view"));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_K));

		return act;

//	    case SIMILAR_DOC_ACTION:
//		act = new FindReplaceAction(jpanel, GlobalProperties.getIntlString("Similar_Documents")) {
//		    public void actionPerformed(ActionEvent e) {
//                try {
//                    this.currentFRJPanel.findSimilarDocs(e);
//                } catch (Exception ex) {
//                    Logger.getLogger(FindReplaceAction.class.getName()).log(Level.SEVERE, null, ex);
//                }
//		    }
//		};
//
//		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Find_documents_similar_to_the_one_selected."));
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_K));
//
//		return act;
	}
	
	return act;
    }
}
