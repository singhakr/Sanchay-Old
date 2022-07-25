/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.propbank.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class FramesetAction extends AbstractAction {
    protected FramesetJPanel currentEditorJPanel;

    public static final int OPEN = 0;

    public static final int SAVE = 1;
    public static final int SAVE_AS = 2;

    public static final int NEW = 3;
    public static final int CLOSE = 4;

    public static final int ADD_PREDICATE = 5;
    public static final int EDIT_PREDICATE = 6;
    public static final int REMOVE_PREDICATE = 7;

    public static final int ADD_ROLESET = 8;
    public static final int EDIT_ROLESET = 9;
    public static final int REMOVE_ROLESET = 10;

    public static final int ADD_ROLE = 11;
    public static final int EDIT_EXAMPLES = 12;

    public static final int SELECT_LANGUAGE = 13;
    public static final int SELECT_ENCODING = 14;
    public static final int SELECT_INPUT_METHOD = 15;
    public static final int SHOW_KB_MAP = 16;

    // Total number of actions available
    public static final int _ACTIONS_ = 17;

    public FramesetAction(FramesetJPanel editorJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentEditorJPanel = editorJPanel;
    }

    public FramesetAction(FramesetJPanel editorJPanel, String text) {
        super(text);

        currentEditorJPanel = editorJPanel;
    }

    public void actionPerformed(ActionEvent e) {

    }

    public static Action createAction(FramesetJPanel jpanel, int mode)
    {
        Action act = null;

        switch(mode)
        {
            case NEW:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("New")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.newFile(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Create_a_new_file."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
            return act;

                case OPEN:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Open")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.open(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Open_an_existing_file."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
            return act;

        case CLOSE:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Close")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.closeFile(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Close_the_current_file."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case ADD_PREDICATE:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Add_Predicate")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.addPredicate(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_Predicate."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case EDIT_PREDICATE:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Edit_Predicate")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.editPredicate(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_Predicate."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case REMOVE_PREDICATE:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Remove_Predicate")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.removePredicate(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_Predicate."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case ADD_ROLESET:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Add_Roleset")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.addRoleset(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_Roleset."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case EDIT_ROLESET:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Edit_Roleset")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.editRoleset(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_Roleset."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case REMOVE_ROLESET:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Remove_Roleset")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.removeRoleset(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_Roleset."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case ADD_ROLE:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Add_Role")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.addRole(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_Role."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case EDIT_EXAMPLES:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Edit_Examples")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.editExamples(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_Examples."));
//            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
            return act;

        case SAVE:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Save")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.save(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
            return act;

                case SAVE_AS:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Save_As")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.saveAs(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file_with_a_new_name."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
            return act;
        
        case SELECT_LANGUAGE:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Language")) {

                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.switchLanguage(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_language."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
            return act;

        case SELECT_ENCODING:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Encoding")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.switchEncoding(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_encoding."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
            return act;

	    case SELECT_INPUT_METHOD:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Input_Method")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.selectInputMethod(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_input_method."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
		return act;

	    case SHOW_KB_MAP:
            act = new FramesetAction(jpanel, GlobalProperties.getIntlString("Show_Keyboard")) {
                public void actionPerformed(ActionEvent e) {
                this.currentEditorJPanel.showKBMap(e);
                }
            };

            act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_the_keyboard_map."));
            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
            return act;
        }

        return act;
    }
}
