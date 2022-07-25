/*
 * TextEditorAction.java
 *
 * Created on February 26, 2007, 5:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package sanchay.text.editor.gui;

import java.awt.event.*;
import javax.swing.*;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class TextEditorAction extends AbstractAction
{

    protected TextEditorJPanel currentEditorJPanel;
    public static final int OPEN = 0;
    public static final int SAVE = 1;
    public static final int SAVE_AS = 2;
    public static final int NEW = 3;
    public static final int CLOSE = 4;
    public static final int UNDO = 5;
    public static final int REDO = 6;
    public static final int CUT = 7;
    public static final int COPY = 8;
    public static final int PASTE = 9;
    public static final int FIND = 10;
    public static final int REPLACE = 11;
    public static final int GOTO = 12;
    public static final int SELECT_FONT = 13;
    public static final int SPELL_CHECK = 14;
    public static final int SHOW_MORE_COMMANDS = 15;
    public static final int _BASIC_ACTIONS_ = 16;
    
    public static final int REVERT = 0;
    public static final int SELECT_LANGUAGE = 1;
    public static final int SELECT_ENCODING = 2;
    public static final int SELECT_INPUT_METHOD = 3;
    public static final int SWITCH_INPUT_METHOD = 4;
    public static final int SHOW_KB_MAP = 5;
    public static final int IDENTIFY_LANGUAGE_ENCODING = 6;
    public static final int EDIT_RESOURCE = 7;
    public static final int VALIDATE = 8;
    public static final int SHOW_COMMAND_BUTTONS = 9;
    public static final int PRINT = 10;
    public static final int TEXT_STATS = 11;
    public static final int TEXT_STATS_IN_FILES = 12;
    public static final int INCREASE_FONT_SIZE = 13;
    public static final int DECREASE_FONT_SIZE = 14;
    public static final int TO_CML = 15;
    public static final int MAKE_SEL_HEADING = 16;
    public static final int MAKE_SEL_PARAGRAPH = 17;
    public static final int MAKE_SEL_SENTENCE = 18;
    public static final int MAKE_SEL_SEGMENT = 19;
    public static final int MAKE_HEADING = 20;
    public static final int MAKE_PARAGRAPH = 21;
    public static final int MAKE_SENTENCE = 22;
    public static final int MAKE_SEGMENT = 23;
    public static final int CONVERT_ENCODING = 24;
    public static final int CONVERT_ENCODING_BATCH = 25;
    public static final int HTML_TO_TEXT = 26;
    public static final int SCRIPT_STATS = 27;

    // Total number of actions available
    public static final int _MORE_ACTIONS_ = 28;

    /** Creates a new instance of TextEditorAction */
    public TextEditorAction(TextEditorJPanel editorJPanel, String text, ImageIcon icon,
            String desc, Integer mnemonic, KeyStroke acclerator)
    {
        super(text, icon);

        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentEditorJPanel = editorJPanel;
    }

    public TextEditorAction(TextEditorJPanel editorJPanel, String text)
    {
        super(text);

        currentEditorJPanel = editorJPanel;
    }

    public void actionPerformed(ActionEvent e)
    {
    }

    public static Action createAction(TextEditorJPanel jpanel, int mode)
    {
        Action act = null;

        switch (mode)
        {
            case NEW:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("New"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.newFile(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Create_a_new_file."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
                return act;

            case OPEN:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Open"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.open(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Open_an_existing_file."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;

            case CLOSE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Close"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.closeFile(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Close_the_current_file."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
                return act;

            case SAVE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Save"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.save(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
                return act;

            case SAVE_AS:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Save_As"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.saveAs(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file_with_a_new_name."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
                return act;

            case UNDO:
                act = jpanel.getUndoAction();

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Undo_an_action."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
                return act;

            case REDO:
                act = jpanel.getRedoAction();

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Redo_an_action."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
                return act;

            case CUT:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Cut"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.cut(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Cut_the_selected_text."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
                return act;

            case COPY:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Copy"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.copy(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Copy_the_selected_text."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
                return act;

            case PASTE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Paste"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.paste(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Paste_the_text_from_clipboard."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
                return act;

            case FIND:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Find"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.find(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Find_some_text."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
                return act;

            case REPLACE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Replace"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.replace(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Replace_some_text."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
                return act;

            case GOTO:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Go_To"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.gotoLine(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Go_to_a_particular_line."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
                return act;

            case SELECT_FONT:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Font"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.selectFont(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_font."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case SPELL_CHECK:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Spell_Check"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.spellCheck(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Spell_check_the_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case SHOW_MORE_COMMANDS:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("More_Buttons"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.moreCommands(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_more_command_buttons."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
                return act;
        }

        return act;
    }

    public static Action createMoreAction(TextEditorJPanel jpanel, int mode)
    {
        Action act = null;

        switch (mode)
        {

            case REVERT:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Revert"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.revert(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Revert_to_the_previously_saved_version_of_the_current_file."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
                return act;

            case SELECT_LANGUAGE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Language"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.switchLanguage(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_language."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
                return act;

            case SELECT_ENCODING:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Encoding"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.switchEncoding(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_encoding."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
                return act;

            case SELECT_INPUT_METHOD:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Input_Method"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.selectInputMethod(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_input_method."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
                return act;

            case SWITCH_INPUT_METHOD:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Switch_Input_Method"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.switchInputMethod(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Switch_between_two_input_methods."));
                return act;

            case SHOW_KB_MAP:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Show_Keyboard"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.showKBMap(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_the_keyboard_map."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
                return act;

            case IDENTIFY_LANGUAGE_ENCODING:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Identify_LE"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.identifyLE(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Identify_the_language-encoding_of_the_document."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case EDIT_RESOURCE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Edit_Resource"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.editResource(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Edit_the_current_resource_with_the_associated_interface."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
                return act;

            case VALIDATE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Validate"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.validate(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Validate_the_format_of_the_current_resource."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
                return act;

            case SHOW_COMMAND_BUTTONS:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Show_Buttons"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.showCommandButtons(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Show_or_hide_the_command_buttons."));
                act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_W));
                return act;

            case PRINT:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Print"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.print(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Print_the_document."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
                return act;

            case TEXT_STATS:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Stats"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.textStats(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Get_statistics_about_the_text_in_the_document."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
                return act;

            case TEXT_STATS_IN_FILES:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Stats_(Files)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.textStatsInFiles(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Get_statistics_about_the_text_in_many_files."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
                return act;

            case INCREASE_FONT_SIZE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Zoom_In"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.increaseFontSize(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Increase_font_size."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
                return act;

            case DECREASE_FONT_SIZE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Zoom_Out"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.decreaseFontSize(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Decrease_font_size."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
                return act;

            case TO_CML:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("To_CML"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.toCML(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Convert_to_Corpus_XML."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_SEL_HEADING:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Heading_(Sel)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeSelTagged(e, "h");
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_selected_text_in_a_heading_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_SEL_PARAGRAPH:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Para_(Sel)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeSelTagged(e, "p");
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_selected_text_in_a_paragraph_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_SEL_SENTENCE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Sentence_(Sel)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeSelTagged(e, "s");
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_selected_text_in_a_sentence_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_SEL_SEGMENT:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Segment_(Sel)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeSelTagged(e, GlobalProperties.getIntlString("segment"));
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_selected_text_in_a_segment_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_HEADING:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Heading"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeTagged(e, "h");
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_current_line_in_a_heading_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_PARAGRAPH:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Para"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeTagged(e, "p");
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_current_line_in_a_paragraph_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_SENTENCE:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Sentence"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeTagged(e, GlobalProperties.getIntlString("sentence"));
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_current_line_in_a_sentence_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case MAKE_SEGMENT:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Segment"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.makeTagged(e, GlobalProperties.getIntlString("segment"));
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Wrap_the_current_line_in_a_segment_tag."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case CONVERT_ENCODING:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Convert_Encoding"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.convertEncoding(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Convert_the_encoding_of_the_current_text_or_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case CONVERT_ENCODING_BATCH:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Convert_Encoding_(Batch)"))
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        this.currentEditorJPanel.convertEncodingBatch(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Convert_the_encoding_of_a_batch_of_files."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case HTML_TO_TEXT:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("HTML_to_Text")) {
                    public void actionPerformed(ActionEvent e) {
                    this.currentEditorJPanel.htmlToText(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Convert_HTML_data_to_simple_text"));
//  		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

            case SCRIPT_STATS:
                act = new TextEditorAction(jpanel, GlobalProperties.getIntlString("Script_Stats")) {
                    public void actionPerformed(ActionEvent e) {
                    this.currentEditorJPanel.scriptStats(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Get_script_statistics_(distribution_of_letters_etc.)_for_the_selected_file."));
//  		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
                return act;

        }

        return act;
    }
}
