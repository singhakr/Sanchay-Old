/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.gui.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import javax.swing.JTextArea;

/**
 *
 * @author anil
 */
public class SanchayShellReaderThread extends Thread
{

    protected PipedInputStream pi;
    protected JTextShell textShell;
    protected SanchayShellJPanel sanchayShellJPanel;

    public SanchayShellReaderThread(PipedInputStream pi, JTextShell textShell, SanchayShellJPanel sanchayShellJPanel)
    {
        this.pi = pi;
        this.textShell = textShell;
        this.sanchayShellJPanel = sanchayShellJPanel;
    }

//    public synchronized void run()
//    {
////        final byte[] buf = new byte[1024];
//        final byte[] buf = new byte[131072];
//
//        try
//        {
//            while (true)
//            {
//                final int len = pi.read(buf);
//                if (len == -1)
//                {
//                    break;
//                }
//
//                SwingUtilities.invokeLater(new Runnable()
//                {
//
//                    @Override
//                    public synchronized void run()
//                    {
//                        textShell.append(new String(buf, 0, len));
//
//                        // Make sure the last line is always visible
//                        textShell.setCaretPosition(textShell.getDocument().getLength());
//
//                        // Keep the text Shell down to a certain character size
//                        int idealSize = 100000;
//                        int maxExcess = 50000;
//                        int excess = textShell.getDocument().getLength() - idealSize;
//                        if (excess >= maxExcess)
//                        {
//                            textShell.replaceRange("", 0, excess);
//                        }
//                    }
//                });
//            }
//        } catch (IOException e)
//        {
//        }
//    }

	public synchronized void run()
	{
		try
		{
			while (Thread.currentThread() == this)
			{
				try { this.wait(100);}catch(InterruptedException ie) {}
				if (pi.available()!=0)
				{
					String input=this.readLine(pi);
					textShell.append(input);
                    sanchayShellJPanel.showPrompt();
//                    textShell.setCaretPosition(textShell.getText().length());
                    
				}
//				if (quit) return;
			}

		} catch (Exception e)
		{
			textShell.append("\nConsole reports an Internal error.");
			textShell.append("The error is: "+e);
		}

		// just for testing (Throw a Nullpointer after 1 second)
//		if (Thread.currentThread()==errorThrower)
//		{
//			try { this.wait(1000); }catch(InterruptedException ie){}
//			throw new NullPointerException("Application test: throwing an NullPointerException It should arrive at the console");
//		}
	}
    
	public synchronized String readLine(PipedInputStream in) throws IOException
	{
		String input="";
		do
		{
			int available=in.available();
			if (available==0) break;
			byte b[]=new byte[available];
			in.read(b);
			input=input+new String(b,0,b.length);
//		} while( !input.endsWith("\n") &&  !input.endsWith("\r\n") && !quit);
		} while( !input.endsWith("\n") &&  !input.endsWith("\r\n"));
		return input;
	}
}
