/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.shell;

import java.io.IOException;
import java.io.InputStream;
import javax.swing.JTextArea;
import jline.ConsoleOperations;
import jline.Terminal;

/**
 *
 * @author anil
 */
public class SanchayTerminal extends Terminal implements ConsoleOperations {

    private static Terminal term;

    protected boolean echoEnabled;

    @Override
    public void initializeTerminal() throws Exception
    {
    }

    @Override
    public int getTerminalWidth()
    {
        return 0;
    }

    @Override
    public int getTerminalHeight()
    {
        return 0;
    }

    @Override
    public boolean isSupported()
    {
        return true;
    }

    @Override
    public boolean getEcho()
    {
        return false;
    }

    @Override
    public boolean isEchoEnabled()
    {
        return echoEnabled;
    }

    @Override
    public void enableEcho()
    {
    }

    @Override
    public void disableEcho()
    {
    }

    public void beforeReadLine(SanchayConsoleReader reader, String prompt,
                               Character mask) {
    }

    public void afterReadLine(SanchayConsoleReader reader, String prompt,
                              Character mask) {
    }

    /**
     *  Read a single character from the input stream. This might
     *  enable a terminal implementation to better handle nuances of
     *  the console.
     */
    public int readCharacter(final JTextArea in) throws IOException {
//        return in.read();
        return 0;
    }

    /**
     *  Reads a virtual key from the console. Typically, this will
     *  just be the raw character that was entered, but in some cases,
     *  multiple input keys will need to be translated into a single
     *  virtual key.
     *
     *  @param  in  the JTextArea to read from
     *  @return  the virtual key (e.g., {@link ConsoleOperations#VK_UP})
     */
    public int readVirtualKey(JTextArea in) throws IOException {
//        return readCharacter(in);
        return 0;
    }
}
