/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.shell;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.IOException;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.plaf.UIResource;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import sanchay.gui.console.JTextConsole;

/**
 *
 * @author anil
 */
public class JTextShell extends JTextPane {

    protected AbstractDocument doc;

    protected String input = "";
    protected String prompt = "";

    protected CommandHistory commandHistory;

    /**
     * TransferHandler used if one hasn't been supplied by the UI.
     */
    private static DefaultTransferHandler defaultTransferHandler;
    
	/**
	 * The list of keys that shoould be processed
	 * by the JTextPane superclass.
	 */
	int[] processable = new int[] {
//			KeyEvent.VK_UP,
//			KeyEvent.VK_DOWN,
			KeyEvent.VK_ALT,
			KeyEvent.VK_C,
			KeyEvent.VK_V,
			KeyEvent.VK_COPY,
			KeyEvent.VK_PASTE,
			KeyEvent.VK_LEFT,
			KeyEvent.VK_RIGHT,
			KeyEvent.VK_HOME,
			KeyEvent.VK_END };

    /**
     * Creates a new <code>JTextPane</code>.  A new instance of
     * <code>StyledEditorKit</code> is
     * created and set, and the document model set to <code>null</code>.
     */
    public JTextShell() {
        super();

        setRequestFocusEnabled(true);
        
        doc = new DefaultStyledDocument();
        setDocument(doc);
    }

    /**
     * Creates a new <code>JTextPane</code>, with a specified document model.
     * A new instance of <code>javax.swing.text.StyledEditorKit</code>
     *  is created and set.
     *
     * @param doc the document model
     */
    public JTextShell(StyledDocument doc) {
        this();
        setStyledDocument(doc);
    }

    /**
     * @return the commandHistory
     */
    public CommandHistory getCommandHistory()
    {
        return commandHistory;
    }

    /**
     * @param commandHistory the commandHistory to set
     */
    public void setCommandHistory(CommandHistory commandHistory)
    {
        this.commandHistory = commandHistory;
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
        Document doc = getDocument();
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
        StyledDocument doc = (StyledDocument) getDocument();

        Element root = doc.getDefaultRootElement();
        
        int lcount = root.getElementCount();

        Element lastLine = root.getElement(lcount - 1);

        if(pos < lastLine.getStartOffset() + prompt.length())
            return false;

//        if(pos < lastLine.getEndOffset() - 1 && pos < prompt.length())
//        if(pos < prompt.length())
//            return false;

        return true;
    }

    protected boolean changeInput(String toThis)
    {
        StyledDocument doc = (StyledDocument) getDocument();

        Element root = doc.getDefaultRootElement();

        int lcount = root.getElementCount();

        Element lastLine = root.getElement(lcount - 1);

        int begOffset = lastLine.getStartOffset() + prompt.length();
        int endOffset = lastLine.getEndOffset();

        try
        {
            doc.remove(begOffset, endOffset - begOffset - 1);
            append(toThis);
            input = toThis;

            setCaretPosition(doc.getLength());
        } catch (BadLocationException ex)
        {
            Logger.getLogger(JTextShell.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    protected void readInput()
    {
        StyledDocument docLocal = (StyledDocument) getDocument();

        Element root = docLocal.getDefaultRootElement();

        int lcount = root.getElementCount();

        Element lastLine = root.getElement(lcount - 1);

        int begOffset = lastLine.getStartOffset() + prompt.length();
        int endOffset = lastLine.getEndOffset();

        if(endOffset - begOffset - 1 < 0)
            return;

        try
        {
            input = docLocal.getText(begOffset, endOffset - begOffset - 1);
        } catch (BadLocationException ex)
        {
            Logger.getLogger(JTextShell.class.getName()).log(Level.SEVERE, null, ex);
        }
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

		if (keyCode == KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED) {
            fireSanchayShellEvent(new SanchayShellEvent(this, SanchayShellEvent.SHELL_COMMAND_EVENT));
            
//			finished = true;
		}

        boolean needProcess = false;

        for (int i = 0; i < processable.length; i++) {
			if (processable[i] == keyCode) {
				needProcess = true;
			}
		}

		if (!needProcess) {
			int caretPosition = getCaretPosition();

            int checkPosition = caretPosition;

            if (keyCode == KeyEvent.VK_BACK_SPACE && e.getID() == KeyEvent.KEY_PRESSED) {
                checkPosition = caretPosition - 1;

                if(isValidPosition(checkPosition)){
           			super.processKeyEvent(e);

                    readInput();
                }
            }
            else if (keyCode == KeyEvent.VK_DELETE && e.getID() == KeyEvent.KEY_PRESSED) {
//                checkPosition = caretPosition + 1;

                readInput();

                if(isValidPosition(checkPosition))
                {
           			super.processKeyEvent(e);

                    readInput();
                }
            }

            if (Character.isISOControl(keyChar) == false && isValidPosition(checkPosition)
                    && Character.isDefined(keyChar))
            {
                super.processKeyEvent(e);
                
                if(e.getID() == KeyEvent.KEY_PRESSED)
                {
                    int docLength = getDocument().getLength();

                    if(checkPosition == docLength)
                        input += keyChar;
                    else if(checkPosition < docLength)
                    {
                        int offsetFromEnd = docLength - checkPosition;

                        String before = input.substring(0, input.length() - offsetFromEnd);
                        String after = input.substring(input.length() - offsetFromEnd, input.length());

                        input = before + keyChar + after;
                    }
                }

//                if (e.getID() == KeyEvent.KEY_PRESSED) {
//
//                    if (Character.isLetterOrDigit(keyChar)) {
//                        write("" + keyChar);
//                    }
//                    else if (keyCode == KeyEvent.VK_SPACE) {
//                        write(" ");
//                    }
//                    else if (keyCode == KeyEvent.VK_DELETE)
//                    {
//                        write(" ");
//                    }
//                    else if (keyCode == KeyEvent.VK_BACK_SPACE)
//                    {
//                        setCaretPosition(getCaretPosition() - 1);
//                        write(" ");
//                        setCaretPosition(getCaretPosition() - 1);
//                    }
//                }
            }

            if ((keyCode == KeyEvent.VK_TAB)
					&& (e.getID() == KeyEvent.KEY_PRESSED))
            {
                autoComplete();

//				boolean found = false;
//
//				if (e.isShiftDown())
//                {
//				}
			}

            if ((keyCode == KeyEvent.VK_UP)
					&& (e.getID() == KeyEvent.KEY_PRESSED))
            {
                moveToPreviousInHistory();
            }

            if ((keyCode == KeyEvent.VK_DOWN)
					&& (e.getID() == KeyEvent.KEY_PRESSED))
            {
                moveToNextInHistory();
            }
		}

		if (needProcess) {
			super.processKeyEvent(e);
		}
	}

    public void autoComplete()
    {

    }

    public void moveToPreviousInHistory()
    {
        commandHistory.previous();
        String cmd = commandHistory.current();

        changeInput(cmd);
    }

    public void moveToNextInHistory()
    {
        commandHistory.next();
        String cmd = commandHistory.current();

        changeInput(cmd);
    }

	/**
	 * Write text to screen with current foregound color
	 */
	public void write(String string) {
		int caretPosition = getCaretPosition();

        int start = caretPosition;

        int end = caretPosition + string.length();

        setSelectionStart(start);
		setSelectionEnd(end);
		replaceSelection(string);
		setSelectionStart(getCaretPosition());
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

    // This methods allows classes to register for MyEvents
    public void addSanchayShellEventListener(EventListener listener)
    {
        listenerList.add(EventListener.class, listener);
    }

    // This methods allows classes to unregister for MyEvents
    public void removeSanchayShellEventListener(EventListener listener)
    {
        listenerList.remove(EventListener.class, listener);
    }

    // This private class is used to fire SanchayShellEvent
    public void fireSanchayShellEvent(SanchayShellEvent evt)
    {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] instanceof SanchayShellEventListener)
            {
                ((SanchayShellEventListener) listeners[i]).handledShellEvent((SanchayShellEvent) evt);
            }
        }
    }

    public void paste() {
        int caretPosition = getCaretPosition();

        if (isEditable() && isEnabled() && isValidPosition(caretPosition)) {
            String actName = "paste";
            ActionMap map = getActionMap();
            Action action = null;

            if (map != null) {
                action = map.get(actName);
            }
            if (action == null) {
                installDefaultTransferHandlerIfNecessary();
                action = TransferHandler.getPasteAction();
            }
            action.actionPerformed(new ActionEvent(this,
                                   ActionEvent.ACTION_PERFORMED, (String)action.
                                   getValue(Action.NAME),
                                   EventQueue.getMostRecentEventTime(),
                                   getCurrentEventModifiers()));

            readInput();
        }
    }

    /**
     * If the current <code>TransferHandler</code> is null, this will
     * install a new one.
     */
    private void installDefaultTransferHandlerIfNecessary() {
        if (getTransferHandler() == null) {
            if (defaultTransferHandler == null) {
                defaultTransferHandler = new DefaultTransferHandler();
            }
            setTransferHandler(defaultTransferHandler);
        }
    }

    private int getCurrentEventModifiers() {
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent)currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent)currentEvent).getModifiers();
        }
        return modifiers;
    }

    public String getInput()
    {
        return input;
    }

    public void setInput(String in)
    {
        input = in;
    }

    public void setPromt(String p)
    {
        prompt = p;
    }
    /**
     * A Simple TransferHandler that exports the data as a String, and
     * imports the data from the String clipboard.  This is only used
     * if the UI hasn't supplied one, which would only happen if someone
     * hasn't subclassed Basic.
     */
    static class DefaultTransferHandler extends TransferHandler implements
                                        UIResource {
        public void exportToClipboard(JComponent comp, Clipboard clipboard,
                                      int action) throws IllegalStateException {
            if (comp instanceof JTextComponent) {
                JTextComponent text = (JTextComponent)comp;
                int p0 = text.getSelectionStart();
                int p1 = text.getSelectionEnd();
                if (p0 != p1) {
                    try {
                        Document doc = text.getDocument();
                        String srcData = doc.getText(p0, p1 - p0);
                        StringSelection contents =new StringSelection(srcData);

                        // this may throw an IllegalStateException,
                        // but it will be caught and handled in the
                        // action that invoked this method
                        clipboard.setContents(contents, null);

                        if (action == TransferHandler.MOVE) {
                            doc.remove(p0, p1 - p0);
                        }
                    } catch (BadLocationException ble) {}
                }
            }
        }
        public boolean importData(JComponent comp, Transferable t) {
            if (comp instanceof JTextComponent) {
                DataFlavor flavor = getFlavor(t.getTransferDataFlavors());

                if (flavor != null) {
		    InputContext ic = comp.getInputContext();
		    if (ic != null) {
			ic.endComposition();
		    }
                    try {
                        String data = (String)t.getTransferData(flavor);

                        ((JTextComponent)comp).replaceSelection(data);
                        return true;
                    } catch (UnsupportedFlavorException ufe) {
                    } catch (IOException ioe) {
                    }
                }
            }
            return false;
        }
        public boolean canImport(JComponent comp,
                                 DataFlavor[] transferFlavors) {
            JTextComponent c = (JTextComponent)comp;
            if (!(c.isEditable() && c.isEnabled())) {
                return false;
            }
            return (getFlavor(transferFlavors) != null);
        }
        public int getSourceActions(JComponent c) {
            return NONE;
        }
        private DataFlavor getFlavor(DataFlavor[] flavors) {
            if (flavors != null) {
                for (int counter = 0; counter < flavors.length; counter++) {
                    if (flavors[counter].equals(DataFlavor.stringFlavor)) {
                        return flavors[counter];
                    }
                }
            }
            return null;
        }
    }
}
