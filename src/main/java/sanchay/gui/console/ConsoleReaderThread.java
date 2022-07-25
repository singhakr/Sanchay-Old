/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.gui.console;

import java.io.IOException;
import java.io.PipedInputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author anil
 */
public class ConsoleReaderThread extends Thread
{

    protected PipedInputStream pi;
    protected JTextConsole textConsole;

    public ConsoleReaderThread(PipedInputStream pi, JTextConsole textConsole)
    {
        this.pi = pi;
        this.textConsole = textConsole;
    }

    @Override
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
//                        textConsole.append(new String(buf, 0, len));
//
//                        // Make sure the last line is always visible
//                        textConsole.setCaretPosition(textConsole.getDocument().getLength());
//
//                        // Keep the text area down to a certain character size
//                        int idealSize = 100000;
//                        int maxExcess = 50000;
//                        int excess = textConsole.getDocument().getLength() - idealSize;
//                        if (excess >= maxExcess)
//                        {
//                            textConsole.replaceRange("", 0, excess);
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
					textConsole.append(input);
				}
//				if (quit) return;
			}

		} catch (Exception e)
		{
			textConsole.append("\nConsole reports an Internal error.");
			textConsole.append("The error is: "+e);
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
