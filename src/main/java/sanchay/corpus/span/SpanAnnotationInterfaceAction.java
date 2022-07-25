/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.span;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class SpanAnnotationInterfaceAction extends AbstractAction {
    protected SpanArgOptionalJPanel currentAnnotationJPanel;

    public static final int FREEZE =0;
    public static final int SELECT_LANGUAGE = 1;
    public static final int SELECT_INPUT_METHOD = 2;
    public static final int NEW_ANNOTATED = 3;
    public static final int OPEN_ANNOTATED = 4;
    public static final int CLOSE_ANNOTATED = 5;
    public static final int SAVE_ANNOTATED = 6;
    public static final int SAVE_AS_ANNOTATED = 7;
    public static final int _BASIC_ACTIONS_=8;
    
    
    public SpanAnnotationInterfaceAction(SpanArgOptionalJPanel editorJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);
        
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentAnnotationJPanel = editorJPanel;
    }

    public SpanAnnotationInterfaceAction(SpanArgOptionalJPanel editorJPanel, String text) {
        super(text);

        currentAnnotationJPanel = editorJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
    
    }
    
    public static Action createAction(SpanArgOptionalJPanel jpanel, int mode)
    {
	Action act = null;
        String selection;
	
	switch(mode)
	{
	    case FREEZE:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Freeze")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.freeze(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Freeze_the_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

            case SELECT_LANGUAGE:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Switch_Language")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.switchLanguage(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_language."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

	    case SELECT_INPUT_METHOD:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Input_Method")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.selectInputMethod(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_input_method."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
		return act;

            case NEW_ANNOTATED:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("New")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.newFile(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Create_a_new_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

            case OPEN_ANNOTATED:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Open")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.open(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Open_an_existing_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

            case CLOSE_ANNOTATED:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Close")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.closeFile(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Close_the_current_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		return act;

            case SAVE_ANNOTATED:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Save")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.save(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		return act;

            case SAVE_AS_ANNOTATED:
		act = new SpanAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Save_As")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.saveAs(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file_with_a_new_name."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		return act;
         
        }
        
        return act;
    }   
}
