/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.propbank.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class FramesetExampleAction extends AbstractAction {

    protected FramesetExamplesJPanel currentEditorJPanel;

    public static final int ADD_EXAMPLE = 0;
    public static final int REMOVE_EXAMPLE = 1;
    
    public static final int ADD_ARGUMENT = 2;
    public static final int ADD_RELATION = 3;

    public static final int _ACTIONS_ = 4;

    public FramesetExampleAction(FramesetExamplesJPanel editorJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentEditorJPanel = editorJPanel;
    }

    public FramesetExampleAction(FramesetExamplesJPanel editorJPanel, String text) {
        super(text);

        currentEditorJPanel = editorJPanel;
    }

    public void actionPerformed(ActionEvent e) {

    }

    public static Action createAction(FramesetExamplesJPanel jpanel, int mode)
    {
        Action act = null;

        switch(mode)
        {
            case ADD_EXAMPLE:
                act = new FramesetExampleAction(jpanel, GlobalProperties.getIntlString("Add_Example")) {
                        public void actionPerformed(ActionEvent e) {
                        this.currentEditorJPanel.addExample(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_an_example."));
    //            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
                return act;

            case REMOVE_EXAMPLE:
                act = new FramesetExampleAction(jpanel, GlobalProperties.getIntlString("Remove_Example")) {
                        public void actionPerformed(ActionEvent e) {
                        this.currentEditorJPanel.removeExample(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_an_example."));
    //            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
                return act;

            case ADD_ARGUMENT:
                act = new FramesetExampleAction(jpanel, GlobalProperties.getIntlString("Add_Argument")) {
                        public void actionPerformed(ActionEvent e) {
                        this.currentEditorJPanel.addArgument(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_an_argument."));
    //            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
                return act;

            case ADD_RELATION:
                act = new FramesetExampleAction(jpanel, GlobalProperties.getIntlString("Add_Relation")) {
                        public void actionPerformed(ActionEvent e) {
                        this.currentEditorJPanel.addRelation(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_a_relation."));
    //            act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
                return act;
        }

        return act;
    }
}
