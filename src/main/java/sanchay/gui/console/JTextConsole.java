/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.console;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

/**
 *
 * @author anil
 */
public class JTextConsole extends JTextPane {

    protected AbstractDocument doc;

    /**
     * Creates a new <code>JTextPane</code>.  A new instance of
     * <code>StyledEditorKit</code> is
     * created and set, and the document model set to <code>null</code>.
     */
    public JTextConsole() {
        super();

        doc = new DefaultStyledDocument();
        setDocument(doc);
        setEditable(false);
    }

    /**
     * Creates a new <code>JTextPane</code>, with a specified document model.
     * A new instance of <code>javax.swing.text.StyledEditorKit</code>
     *  is created and set.
     *
     * @param doc the document model
     */
    public JTextConsole(StyledDocument doc) {
        this();
        setStyledDocument(doc);
    }

	/**
	 * Rendering method. Overrides the paint() method
	 * on the superclass to add antialiasing to the output
	 * screen.
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g);
	}

	/**
	 * Clear the screen
	 */
	public void clear() {

        try
        {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex)
        {
            Logger.getLogger(JTextConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

    protected boolean isValidPosition(int pos)
    {
        return true;
    }

	/**
	 * Key processing function. Certain key events
	 * are delegated to the superclass. Others submit
	 * the user input to the calling object or react to the input
	 * internally.
	 */
	protected void processKeyEvent(KeyEvent e) {

        char keyChar = e.getKeyChar();
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_L && e.isControlDown()) {
            clear();
		}
        else
			super.processKeyEvent(e);
	}

    public void append(String string)
    {
        Element root = doc.getDefaultRootElement();
        
        try
        {
            doc.insertString(root.getEndOffset() - 1, string, null);
        } catch (BadLocationException ex)
        {
            Logger.getLogger(JTextConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
