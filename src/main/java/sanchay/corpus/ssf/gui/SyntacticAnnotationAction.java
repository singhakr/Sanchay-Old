/*
 * SyntacticAnnotationAction.java
 *
 * Created on March 16, 2009, 9:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package sanchay.corpus.ssf.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class SyntacticAnnotationAction extends AbstractAction
{

    protected SyntacticAnnotationWorkJPanel annotationWorkJPanel;
    public static final int SAVE = 0;
    public static final int SAVE_AS = 1;
    public static final int EDIT_AS_TEXT = 2;
    public static final int STATISTICS = 3;
    public static final int STATISTICS_FILES = 4;
    public static final int SHOW_COMMENTS = 5;
    public static final int JOIN_SENTENCE = 6;
    public static final int SPLIT_SENTENCE = 7;
    public static final int JOIN_FILES = 8;
    public static final int TRANSFER_TAGS = 9;
    public static final int SHOW_MORE_BUTTONS = 10;
    public static final int CLEAR = 11;
    public static final int CLEAR_ALL = 12;
    public static final int RESET = 13;
    public static final int RESET_ALL = 14;
    public static final int _TOTAL_MAIN_ACTIONS_ = 15;
    public static final int FIND = 0;
    public static final int FIND_IN_FILES = 1;
    public static final int REPLACE = 2;
    public static final int REPLACE_IN_FILES = 3;
    public static final int BATCH_REPLACE = 4;
    public static final int BATCH_REPLACE_IN_FILES = 5;
    public static final int _TOTAL_FIND_ACTIONS_ = 6;

    /** Creates a new instance of SyntacticAnnotationAction */
    public SyntacticAnnotationAction(SyntacticAnnotationWorkJPanel workJPanel, String text, ImageIcon icon,
            String desc, Integer mnemonic, KeyStroke acclerator)
    {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        annotationWorkJPanel = workJPanel;
    }

    public SyntacticAnnotationAction(SyntacticAnnotationWorkJPanel workJPanel, String text)
    {
        super(text);

        annotationWorkJPanel = workJPanel;
    }

    public void actionPerformed(ActionEvent e)
    {
    }

    public static SyntacticAnnotationAction createMainAction(SyntacticAnnotationWorkJPanel jpanel, int mode)
    {
        SyntacticAnnotationAction act = null;

        switch (mode)
        {
            case SAVE:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Save"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.save(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case SAVE_AS:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Save_As"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.saveAs(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_file_with_a_different_name."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case EDIT_AS_TEXT:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Edit_SSF"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.editSSFText(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_SSF_as_text."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case STATISTICS:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Statistics"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.showStatistics(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("See_the_statistics_for_the_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case STATISTICS_FILES:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Statistics_(File)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.showStatisticsInFiles(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("See_the_statistics_for_a_group_of_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case SHOW_COMMENTS:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Show_Comments"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.showComments(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_comments."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case JOIN_SENTENCE:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Join_Sentence"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.joinSentence(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Join_the_current_sentence_with_the_next."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case SPLIT_SENTENCE:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Split_Sentence"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.splitSentence(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Split_the_sentence_at_the_selected_node."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case JOIN_FILES:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Join_Files"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.joinFiles(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Join_two_or_more_files."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case TRANSFER_TAGS:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Transfer_Tags"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.transferTags(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Transfer_the_tags_from_one_file_to_another."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case SHOW_MORE_BUTTONS:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Show_More_Buttons"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.showMoreButtons(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_more_Buttons."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case CLEAR:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Clear"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.clear(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Clear_Annotation."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case CLEAR_ALL:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Clear_some_or_all_of_the_annotation"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.clearAll(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Toggle_edit_mode."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case RESET:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Reset"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.reset(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Reload_the_sentence_from_the_saved_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case RESET_ALL:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Reset_All"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.resetAll(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Reload_all_the_sentence_from_the_saved_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;
        }

        return act;
    }

    public static SyntacticAnnotationAction createFindAction(SyntacticAnnotationWorkJPanel jpanel, int mode)
    {
        SyntacticAnnotationAction act = null;

        switch (mode)
        {
            case FIND:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Find"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.find(e, false);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Find_something_in_the_current_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case FIND_IN_FILES:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Find_(Files)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.find(e, true);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Find_something_in_many_files."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case REPLACE:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Replace"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.replace(e, false);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_something_in_the_current_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case REPLACE_IN_FILES:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Replace_(Files)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.replace(e, true);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_something_in_the_current_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case BATCH_REPLACE:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Batch_Replace"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.replaceBatch(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_many_things_in_the_current_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;

            case BATCH_REPLACE_IN_FILES:
                act = new SyntacticAnnotationAction(jpanel, GlobalProperties.getIntlString("Batch_Replace_(Files)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.annotationWorkJPanel.replaceBatchInFiles(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_many_things_in_many_files."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_0));
                return act;
        }

        return act;
    }
}
