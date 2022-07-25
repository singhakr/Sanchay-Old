/*
 * SanchayJDialog.java
 *
 * Created on May 20, 2006, 5:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.awt.*;
import javax.swing.*;
import java.util.regex.*;
import sanchay.corpus.ssf.gui.SSFAnnotationLevelsJPanel;

import sanchay.util.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class SanchayJDialog extends JDialog
{
    protected JPanel panel;

    public SanchayJDialog()
    {
	super();
    }

    public SanchayJDialog(Dialog owner, String title, boolean modal, JPanelDialog pnl)
    {
	super(owner, title, modal);
	pnl.setDialog(this);
	panel = (JPanel) pnl;
	add(panel);
    }

    public SanchayJDialog(Frame owner, String title, boolean modal, JPanelDialog pnl)
    {
	super(owner, title, modal);
	pnl.setDialog(this);
	panel = (JPanel) pnl;
	add(panel);
    }

    public JPanel getJPanel()
    {
	return panel;
    }
};
