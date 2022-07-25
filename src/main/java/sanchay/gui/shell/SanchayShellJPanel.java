/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SanchayShellJPanel.java
 *
 * Created on 26 Oct, 2009, 2:08:51 PM
 */

package sanchay.gui.shell;

import bsh.Interpreter;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import org.incava.diffj.DiffJ;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import sanchay.SanchayMain;
import sanchay.common.types.ClientType;
import sanchay.gui.clients.SanchayClient;
import sanchay.gui.common.JPanelDialog;
import sanchay.gui.shell.commands.MathEvaluator;
import sanchay.java.JavaDiff;
import sanchay.text.diff.Report;
import sanchay.text.diff.TextDiff;
import sanchay.util.UtilityFunctions;
import sanchay.xml.diff.XMLDiff;

/**
 *
 * @author anil
 */
public class SanchayShellJPanel extends javax.swing.JPanel
        implements JPanelDialog, sanchay.gui.clients.SanchayClient, SanchayShellEventListener {

    protected ClientType clientType = ClientType.SANCHAY_SHELL;

    protected JFrame owner;
    protected JDialog dialog;
    protected Component parentComponent;

    protected String langEnc = "hin::utf8";
    protected String charset = "UTF-8";

    protected String title = "";

    protected String input = "";
    protected String curDir = System.getProperty("user.home");
    public static String promptSep1 = ":";
    public static String promptSep2 = "$";

    protected javax.swing.JScrollPane stdOutJScrollPane;
    protected javax.swing.JScrollPane stdErrJScrollPane;

    protected JTextShell stdOutJTextShell;
    protected JTextShell stdErrJTextShell;

//    protected PipedInputStream piIn;
//    protected PipedOutputStream poIn;

    protected PipedInputStream piOut;
    protected PipedInputStream piErr;
    protected PipedOutputStream poOut;
    protected PipedOutputStream poErr;

    protected OutputStream oiIn;
    protected PrintStream psOut;
    protected PrintStream psErr;

    protected Process process;

//    protected SanchayConsoleReader consoleReader;

    protected bsh.Interpreter bshInterpeter = new bsh.Interpreter();
    protected PythonInterpreter pythonInterpreter = new PythonInterpreter();

    protected TextDiff textDiff = new TextDiff();
//    protected XMLDiff xmlDiff = new XMLDiff();
    protected JavaDiff javaDiff;
    
    protected MathEvaluator mathEvaluator = new MathEvaluator();
//    protected Matlab matlab;
//    protected Octave octave;
    protected jmathlib.core.interpreter.Interpreter jMathLibInterpreter;
//    protected JDMP

    protected CommandHistory commandHistory;

    protected int mode;

    public static final int DEFAULT_MODE = 0;
    public static final int BEAN_SHELL_MODE = 1;
    public static final int MATH_MODE = 2;
    public static final int MATLAB_MODE = 3;
    public static final int OCTAVE_MODE = 4;
    public static final int JMATHLIB_MODE = 5;
    public static final int JDMP_MODE = 6;
    public static final int PYTHON_MODE = 7;

    /** Creates new form SanchayShellJPanel */
    public SanchayShellJPanel() {
        initComponents();

        stdOutJTextShell = new JTextShell();
        stdErrJTextShell = new JTextShell();

        stdOutJScrollPane = new javax.swing.JScrollPane();
        stdOutJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Output"));

        stdErrJScrollPane = new javax.swing.JScrollPane();
        stdErrJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Errors"));

        stdOutJScrollPane.setViewportView(stdOutJTextShell);
        stdErrJScrollPane.setViewportView(stdErrJTextShell);

        consoleJSplitPane.setTopComponent(stdOutJScrollPane);
        consoleJSplitPane.setBottomComponent(stdErrJScrollPane);

//        piIn = new PipedInputStream();
        piOut = new PipedInputStream();
        piErr = new PipedInputStream();

        try
        {
//            poIn = new PipedOutputStream(piIn);
//            oiIn = new PrintStream(poIn, true, charset);

            poOut = new PipedOutputStream(piOut);
//            System.setOut(new PrintStream(poOut, true, charset));
            psOut = new PrintStream(poOut, true, charset);

            poErr = new PipedOutputStream(piErr);
//            System.setErr(new PrintStream(poErr, true, charset));
            psErr = new PrintStream(poErr, true, charset);

//            consoleReader = new SanchayConsoleReader();
//            consoleReader = new SanchayConsoleReader(piIn,
//                    new PrintWriter(
//                            new OutputStreamWriter(psOut)));

        } catch (IOException ex)
        {
            Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        stdOutJTextShell.requestFocusInWindow();

        UtilityFunctions.setComponentFont(stdOutJTextShell, langEnc);
        UtilityFunctions.setComponentFont(stdErrJTextShell, langEnc);

        // Create reader threads
//        new SanchayConsoleReaderThread(consoleReader, stdOutJTextShell, this).start();
        new SanchayShellReaderThread(piOut, stdOutJTextShell, this).start();
        new SanchayShellReaderThread(piErr, stdErrJTextShell, this).start();

        try
        {
//            if(Matlab.isAvailable())
//                matlab = Matlab.getInstance();
//
//            if(Octave.isAvailable())
//                octave = Octave.getInstance();
            
//            jMathLibInterpreter = new jmathlib.core.jMathLibInterpreter.Interpreter(false);

            bshInterpeter.setOut(psOut);
            bshInterpeter.setErr(psErr);
            
            PySystemState.initialize();

            pythonInterpreter.setOut(poOut);
            pythonInterpreter.setErr(poErr);
        } catch (Exception ex)
        {
            Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        commandHistory = new CommandHistory();

        stdOutJTextShell.setCommandHistory(commandHistory);

        stdOutJTextShell.addSanchayShellEventListener(this);
        
        showPrompt();

        stdOutJTextShell.requestFocusInWindow();
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    public void setMode(int m)
    {
        mode = m;
    }

    protected String runCommand(String cmd)
    {
        cmd = cmd.trim();

        if(cmd.equals("") == false)
            commandHistory.addToHistory(cmd);

        String output = "";

        if(mode == DEFAULT_MODE)
        {
            if(cmd.equals("help"))
            {
                output += "Sancahy Shell: The following commands are available:\n";
                output += "\t" + "help" + "\n";
                output += "\t" + "pwd" + "\n";
                output += "\t" + "ls" + "\n";
                output += "\t" + "ll" + "\n";
                output += "\t" + "cd" + "\n";
                output += "\t" + "cat" + "\n";
                output += "\t" + "diff" + "\n";
                output += "\t" + "diffx" + "\n";
                output += "\t" + "edit" + "\n";
                output += "\t" + "exec" + "\n";
                output += "\t" + "=\n\tTo evaluate the following expression" + "\n";
                output += "\t" + "x = 1\n\tTo set the value (1) of a variable (x)" + "\n";
                output += "\t" + "clear" + "\n";
                output += "\t" + "new" + "\n";
                output += "\t" + "exit" + "\n";
            }
            else if(cmd.equals("pwd"))
            {
                File curFile = new File(curDir);
                output = curFile.getAbsolutePath() + "\n";
            }
            else if(cmd.equals("ls"))
            {
                File curFile = new File(curDir);

                File files[] = curFile.listFiles();

                for (int i = 0; i < files.length; i++)
                {
                    File file = files[i];

                    if(i % 3 == 0)
                        output += file.getName() + "\t";
                    else
                        output += file.getName() + "\n";
                }
            }
            else if(cmd.equals("ll"))
            {
                File curFile = new File(curDir);

                File files[] = curFile.listFiles();

                for (int i = 0; i < files.length; i++)
                {
                    File file = files[i];

                    Date date = new Date(file.lastModified());

                    output += file.length() + "\t" + DateFormat.getDateInstance().format(date)
                             + "\t" + DateFormat.getTimeInstance().format(date)
                           +"\t" + file.getName() + "\n";
                }
            }
            else if(cmd.startsWith("cd "))
            {
                String parts[] = cmd.split("[\\s+]");

                if(parts.length == 1)
                {
                    curDir = System.getProperty("user.home");

                    File curFile = new File(curDir);
                    output = curFile.getAbsolutePath() + "\n";
                }
                else if(parts.length == 2)
                {
                    String newDir = parts[1];

                    File curFile = new File(curDir);

                    File newFile = new File(curFile, newDir);

                    if(newFile.exists())
                    {
                        try
                        {
                            curDir = newFile.getCanonicalPath();
                        } catch (IOException ex)
                        {
                            Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

    //                    curFile = new File(curDir);
    //                    output += curFile.getAbsolutePath() + "\n";
                        output = curDir + "\n";
                    }
                }
            }
            else if(cmd.startsWith("cat "))
            {
                String parts[] = cmd.split("[\\s+]");

                if(parts.length == 2)
                {
                    String file = parts[1];
                    try
                    {
                        output = UtilityFunctions.getTextFromFile(file, charset) + "\n";
                    } catch (FileNotFoundException ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else if(cmd.startsWith("diff "))
            {
                String parts[] = cmd.split("[\\s+]");

                if(parts.length == 3)
                {
                    String file1 = parts[1];
                    String file2 = parts[2];

                    try
                    {
                        Report diffReport =  textDiff.compare(file1, file2, charset);
                        output = diffReport.getReportDefaultFormat() + "\n";
                    } catch (FileNotFoundException ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else if(cmd.startsWith("diffx "))
            {
                String parts[] = cmd.split("[\\s+]");

                if(parts.length == 3)
                {
                    String file1 = parts[1];
                    String file2 = parts[2];

                    BufferedReader inReader1 = null;
                    BufferedReader inReader2 = null;

                    PrintWriter writer = new PrintWriter(psOut);

                    try
                    {
                        inReader1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1), charset));
                        inReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2), charset));

                        XMLDiff.diff(inReader1, inReader2, writer);
                        Report diffReport =  textDiff.compare(file1, file2, charset);
                        output = diffReport.getReportDefaultFormat() + "\n";
                    } catch (FileNotFoundException ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex)
                    {
                        Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else if(cmd.startsWith("diffj "))
            {
                String parts[] = cmd.split("[\\s+]");

                if(parts.length == 3)
                {
                    String file1 = parts[1];
                    String file2 = parts[2];

                    javaDiff = new JavaDiff(new String[]{file1, file2}, psOut);
                }
            }
            else if(cmd.startsWith("edit"))
            {
                ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_EDITOR);
            }
            else if(cmd.startsWith("exec "))
            {
                cmd = cmd.replace("exec", "");

                cmd = cmd.trim();

                String parts[] = cmd.split("\\s+");
                Vector cmdList = new Vector(parts.length);

                for (int i = 0; i < parts.length; i++)
                {
                    String string = parts[i];
                    cmdList.add(string);
                }

                try
                {
                    ProcessBuilder pb = new ProcessBuilder(cmdList);

                    pb.directory(new File(curDir));

                    process = pb.start();

                    new ProcessReaderThread(process.getInputStream(), stdOutJTextShell, this).start();
                    new ProcessReaderThread(process.getErrorStream(), stdErrJTextShell, this).start();

    //                process = null;
                } catch (IOException ex)
                {
                    Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(cmd.startsWith("="))
            {
                String expr = cmd.replace("=", "");

                expr = expr.trim();

                if(expr.equals(""))
                {
                    output = "Nothing to evaluate";
                }
                else
                {
                    try
                    {
                        mathEvaluator.setExpression(expr);
                        output = mathEvaluator.getValue().toString() + "\n";
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else if(cmd.contains("="))
            {
                String parts[] = cmd.split("=");

                if(parts == null || parts.length != 2)
                {
                    output = "Incorrect expression";
                }
                else if(parts.length == 2)
                {
                    String var = parts[0];
                    String val = parts[1];

                    mathEvaluator.addVariable(var, Double.parseDouble(val));
                }
            }
            else if(cmd.equals("clear"))
            {
                stdOutJTextShell.clear();
            }
            else if(cmd.equals("new") || cmd.equals("shell"))
            {
                ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
            }
            else if(cmd.startsWith("new "))
            {
                String parts[] = cmd.split("\\s+");

                if(parts == null || parts.length != 2)
                {
                    ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
                }
                else if(parts.length == 2)
                {
                    String modeStr = parts[1];

                    int m = DEFAULT_MODE;

                    if(modeStr.equals("math"))
                    {
                        m = MATH_MODE;

                        SanchayClient client = ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
                        ((SanchayShellJPanel) client).setMode(m);
                    }
//                    else if(modeStr.equals("matlab") && Matlab.isAvailable())
//                    {
//                        m = MATLAB_MODE;
//
//                        SanchayClient client = ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
//                        ((SanchayShellJPanel) client).setMode(m);
//                    }
//                    else if(modeStr.equals("octave") && Octave.isAvailable())
//                    {
//                        m = OCTAVE_MODE;
//
//                        SanchayClient client = ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
//                        ((SanchayShellJPanel) client).setMode(m);
//                    }
                    else if(modeStr.equals("jmathlib"))
                    {
//                        JMathLib.showGUI();
//                        JFrame.setDefaultLookAndFeelDecorated(true);
//                        JDialog.setDefaultLookAndFeelDecorated(true);
                        new jmathlib.ui.swing.SwingGUI(new String[]{"-width", "800"});
//                        m = JMATHLIB_MODE;
//
//                        SanchayClient client = ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
//                        ((SanchayShellJPanel) client).setMode(m);
                    }
                    else if(modeStr.equals("bsh"))
                    {
                        m = BEAN_SHELL_MODE;

                        SanchayClient client = ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
                        ((SanchayShellJPanel) client).setMode(m);
                    }
                    else if(modeStr.equals("python"))
                    {
                        m = PYTHON_MODE;

                        SanchayClient client = ((SanchayMain) owner).createNewApplication(ClientType.SANCHAY_SHELL);
                        ((SanchayShellJPanel) client).setMode(m);
                    }
                }
            }
            else if(cmd.equals("exit"))
            {
                ((SanchayMain) owner).stopApplication();
            }
        }
        else if(mode == MATH_MODE)
        {
            try
            {
                mathEvaluator.setExpression(cmd);
                output = mathEvaluator.getValue().toString() + "\n";
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(mode == MATLAB_MODE)
        {
            try
            {
//                output = matlab.execute(cmd);
            } catch (Exception ex)
            {
                Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(mode == JMATHLIB_MODE)
        {
            try
            {
                jMathLibInterpreter.executeExpression(cmd);
            } catch (Exception ex)
            {
                Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(mode == BEAN_SHELL_MODE)
        {
            try
            {
                Object outputObj = bshInterpeter.eval(cmd);

                if(outputObj != null)
                    output = outputObj.toString();
            } catch (Exception ex)
            {
                Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(mode == PYTHON_MODE)
        {
            try
            {
//                pythonInterpreter.exec(cmd);
//                PyObject cmdObj = new PyObject();
//                cmdObj.__tojava__(String.class) = cmd;
                PyObject outputObj = pythonInterpreter.eval(cmd + "\n\n\n");

                if(outputObj != null)
                    output = outputObj.toString() + "\n";
            } catch (Exception ex)
            {
                Logger.getLogger(SanchayShellJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
//        if(output.equals(""))
//            showPrompt();

        return output;
    }

    public void showPrompt()
    {
        String prompt = constructPrompt();
        
        stdOutJTextShell.append(prompt);

        stdOutJTextShell.setPromt(prompt);

        stdOutJTextShell.setCaretPosition(stdOutJTextShell.getText().length());

        stdOutJTextShell.requestFocusInWindow();
    }

    public String constructPrompt()
    {
        String prompt = "";

        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getByName("127.0.0.1"); //

//            // Get hostname by a byte array containing the IP address
//            byte[] ipAddr = new byte[]{127, 0, 0, 1};
//            addr = InetAddress.getByAddress(ipAddr);

            // Get the host name from the address
            String hostname = addr.getHostName();

            // Get canonical host name
            String hostnameCanonical = addr.getCanonicalHostName();

            String user = System.getProperty("user.name");

            File curFile = new File(curDir);

            prompt = user + "@" +hostname + promptSep1 + curFile.getName() + promptSep2;
        }
        catch (UnknownHostException e) {
        // handle exception
        }

        return prompt;
    }
    
    public String getLangEnc()
    {
        return langEnc;
    }

    public Frame getOwner() {
        return owner;
    }

    public void setOwner(Frame frame) {
        owner = (JFrame) frame;
    }

    public void setParentComponent(Component parentComponent)
    {
        this.parentComponent = parentComponent;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public String getTitle() {
        return title;
    }

    public JMenuBar getJMenuBar() {
        return null;
    }

    public JPopupMenu getJPopupMenu() {
        return null;
    }

    public JToolBar getJToolBar() {
        return null;
    }

    @Override
    public void handledShellEvent(SanchayShellEvent evt)
    {
        int id = evt.getEventID();

        if(id == SanchayShellEvent.SHELL_COMMAND_EVENT)
        {
            String output = runCommand(stdOutJTextShell.getInput());

            stdOutJTextShell.append("\n" + output);

            showPrompt();

            stdOutJTextShell.setInput("");
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Sanchay Shell");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
    	SanchayShellJPanel newContentPane = new SanchayShellJPanel();
        newContentPane.setOwner(frame);

        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();

        int inset = 35;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset,
		screenSize.width  - inset*2,
		screenSize.height - inset*5);

	frame.setVisible(true);

        newContentPane.requestFocusInWindow();
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        consoleJSplitPane = new javax.swing.JSplitPane();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        consoleJSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        consoleJSplitPane.setResizeWeight(0.8);
        consoleJSplitPane.setOneTouchExpandable(true);
        add(consoleJSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_formFocusGained
    {//GEN-HEADEREND:event_formFocusGained
        // TODO add your handling code here:
        stdOutJTextShell.requestFocusInWindow();
    }//GEN-LAST:event_formFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane consoleJSplitPane;
    // End of variables declaration//GEN-END:variables

}
