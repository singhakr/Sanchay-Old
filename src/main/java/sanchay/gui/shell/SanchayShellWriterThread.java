/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.shell;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.swing.JTextArea;

/**
 *
 * @author anil
 */
public class SanchayShellWriterThread extends Thread {

    protected PipedInputStream pi;
    protected JTextArea textArea;

    public SanchayShellWriterThread(PipedInputStream po, JTextArea textArea)
    {
        this.pi = po;
        this.textArea = textArea;
    }
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
					textArea.append(input);
				}
//				if (quit) return;
			}

		} catch (Exception e)
		{
			textArea.append("\nConsole reports an Internal error.");
			textArea.append("The error is: "+e);
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
