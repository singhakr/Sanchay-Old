/*
 * DialogFactory.java
 *
 * Created on May 20, 2006, 3:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import sanchay.corpus.ssf.gui.CorpusStatisticsJPanel;
import sanchay.corpus.ssf.gui.HierarchicalTagsJPanel;
import sanchay.corpus.ssf.gui.SSFAnnotationLevelsJPanel;
import sanchay.table.SanchayTableModel;
import sanchay.db.gui.ConnectToDBJPanel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.text.spell.gui.DictionaryFSTJPanel;

import sanchay.util.*;
import sanchay.util.gui.MNReadJPanel;
import sanchay.util.gui.RegexOptionsJPanel;

/**
 *
 * @author Anil Kumar Singh
 */
public class DialogFactory {
    
    /** Creates a new instance of DialogFactory */
    public static SanchayJDialog showDialog(JPanel panel, Frame owner, String title, boolean modal)
    {
	SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);

	dialog.pack();

	UtilityFunctions.centre(dialog);

	dialog.setVisible(true);

	return dialog;
    }

    public static SanchayJDialog showDialog(Class cl, Frame owner, String title, boolean modal)
    {
	JPanel panel = createJPanel(cl);
	SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);
	
	dialog.pack();
	
	UtilityFunctions.centre(dialog);

	dialog.setVisible(true);
	
	return dialog;
    }
    
    public static SanchayJDialog showDialog(Class cl, Dialog owner, String title, boolean modal)
    {
	JPanel panel = createJPanel(cl);
	SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);
	
	dialog.pack();
	
	UtilityFunctions.centre(dialog);

	dialog.setVisible(true);
	
	return dialog;
    }

    public static SanchayJDialog showDialog(Class cl, Frame owner, String title, boolean modal, String langEnc, String charset)
    {
	JPanel panel = createJPanel(cl, langEnc, charset);
	SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);

	dialog.pack();

	UtilityFunctions.centre(dialog);

	dialog.setVisible(true);

	return dialog;
    }

    public static SanchayJDialog showDialog(Class cl, Dialog owner, String title, boolean modal, String langEnc, String charset)
    {
	JPanel panel = createJPanel(cl, langEnc, charset);
	SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);

	dialog.pack();

	UtilityFunctions.centre(dialog);

	dialog.setVisible(true);

	return dialog;
    }

    public static SanchayJDialog showTableDialog(Dialog owner, String title, boolean modal, SanchayTableModel model, String langEnc, int mode)
    {
        SanchayJDialog diag = createTableDialog(owner, title, modal, model, langEnc, mode);
        diag.setVisible(true);
        return diag;
    }

    public static SanchayJDialog createTableDialog(Dialog owner, String title, boolean modal, SanchayTableModel model, String langEnc, int mode)
    {
	JPanel panel = new SanchayTableJPanel(model, true, null, mode, langEnc);
	SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);
        
        ((SanchayTableJPanel) panel).getJTable().setPreferredScrollableViewportSize(new Dimension(700, 220));
	
	dialog.pack();
	
	UtilityFunctions.centre(dialog);
	
	return dialog;
    }

    public static SanchayJDialog showTableDialog(Frame owner, String title, boolean modal, SanchayTableModel model, String langEnc, int mode)
    {
        SanchayJDialog diag = createTableDialog(owner, title, modal, model, langEnc, mode);
        diag.setVisible(true);
        return diag;
    }

    public static SanchayJDialog createTableDialog(Frame owner, String title, boolean modal, SanchayTableModel model, String langEnc, int mode)
    {
	JPanel panel = new SanchayTableJPanel(model, true, null, mode, langEnc);
	SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);
        
        ((SanchayTableJPanel) panel).getJTable().setPreferredScrollableViewportSize(new Dimension(700, 220));
	
	dialog.pack();
	
	UtilityFunctions.centre(dialog);
	
	return dialog;        
    }

    public static SanchayJDialog showFileSelectionDialog(Dialog owner, String title, boolean modal, File curDir)
    {
        FileSelectionJPanel panel = (FileSelectionJPanel) createJPanel(FileSelectionJPanel.class);

        panel.setCurrentDir(curDir);

        SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);

	dialog.pack();

	UtilityFunctions.centre(dialog);

	dialog.setVisible(true);

	return dialog;
    }

    public static SanchayJDialog showFileSelectionDialog(Frame owner, String title, boolean modal, File curDir)
    {
        FileSelectionJPanel panel = (FileSelectionJPanel) createJPanel(FileSelectionJPanel.class);

        panel.setCurrentDir(curDir);

        SanchayJDialog dialog = new SanchayJDialog(owner, title, modal, (JPanelDialog) panel);
        ((JPanelDialog) panel).setDialog(dialog);

	dialog.pack();

	UtilityFunctions.centre(dialog);

	dialog.setVisible(true);

	return dialog;
    }

    public static JPanel createJPanel(Class cl)
    {
	if(cl.equals(RegexOptionsJPanel.class))
	    return new RegexOptionsJPanel();
	else if(cl.equals(SSFAnnotationLevelsJPanel.class))
	    return new SSFAnnotationLevelsJPanel();
	else if(cl.equals(MNReadJPanel.class))
	    return new MNReadJPanel();
	else if(cl.equals(GenerateTasksJPanel.class))
	    return new GenerateTasksJPanel();
	else if(cl.equals(FileSelectionJPanel.class))
	    return new FileSelectionJPanel();
//	else if(cl.equals(BoardJPanel.class))
//	    return new BoardJPanel();
//	else if(cl.equals(UlatPalatKeJoRoCustomizeJPanel.class))
//	    return new UlatPalatKeJoRoCustomizeJPanel();
	else if(cl.equals(DictionaryFSTJPanel.class))
	    return new DictionaryFSTJPanel();
//	else if(cl.equals(PuzzleSelectionJPanel.class))
//	    return new PuzzleSelectionJPanel("hin:utf8", JoRoConstants.PROD_ULAT_PALAT_KE);
	else if(cl.equals(ConnectToDBJPanel.class))
	    return new ConnectToDBJPanel();
	else if(cl.equals(LocalKeyboardShorcutEditorJPanel.class))
	    return new LocalKeyboardShorcutEditorJPanel(new SanchayTableModel(0, 3));
	else if(cl.equals(KeystrokeEditorJPanel.class))
	    return new KeystrokeEditorJPanel();
	else if(cl.equals(CorpusStatisticsJPanel.class))
	    return new CorpusStatisticsJPanel();
	else if(cl.equals(HierarchicalTagsJPanel.class))
	    return new HierarchicalTagsJPanel("hin::utf8", "UTF-8", false);
	
	return null;
    }

    public static JPanel createJPanel(Class cl, String langEnc, String charset)
    {
	if(cl.equals(RegexOptionsJPanel.class))
	    return new RegexOptionsJPanel();
	else if(cl.equals(SSFAnnotationLevelsJPanel.class))
	    return new SSFAnnotationLevelsJPanel();
	else if(cl.equals(MNReadJPanel.class))
	    return new MNReadJPanel();
	else if(cl.equals(GenerateTasksJPanel.class))
	    return new GenerateTasksJPanel();
	else if(cl.equals(FileSelectionJPanel.class))
	    return new FileSelectionJPanel();
//	else if(cl.equals(BoardJPanel.class))
//	    return new BoardJPanel();
//	else if(cl.equals(UlatPalatKeJoRoCustomizeJPanel.class))
//	    return new UlatPalatKeJoRoCustomizeJPanel();
	else if(cl.equals(DictionaryFSTJPanel.class))
	    return new DictionaryFSTJPanel();
//	else if(cl.equals(PuzzleSelectionJPanel.class))
//	    return new PuzzleSelectionJPanel("hin:utf8", JoRoConstants.PROD_ULAT_PALAT_KE);
	else if(cl.equals(ConnectToDBJPanel.class))
	    return new ConnectToDBJPanel();
	else if(cl.equals(LocalKeyboardShorcutEditorJPanel.class))
	    return new LocalKeyboardShorcutEditorJPanel(new SanchayTableModel(0, 3));
	else if(cl.equals(KeystrokeEditorJPanel.class))
	    return new KeystrokeEditorJPanel();
	else if(cl.equals(CorpusStatisticsJPanel.class))
	    return new CorpusStatisticsJPanel();
	else if(cl.equals(HierarchicalTagsJPanel.class))
	    return new HierarchicalTagsJPanel(langEnc, charset, false);

	return null;
    }
}
