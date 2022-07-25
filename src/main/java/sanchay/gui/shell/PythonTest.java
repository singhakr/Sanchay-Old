/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.gui.shell;

import org.python.core.CompileMode;
import org.python.core.CompilerFlags;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.InteractiveInterpreter;

/**
 *
 * @author anil
 */
public class PythonTest {
//    protected PythonInterpreter pythonInterpreter = new PythonInterpreter();
    protected InteractiveInterpreter pythonInterpreter = new InteractiveInterpreter();

    public PythonTest()
    {
        PySystemState.initialize();

        pythonInterpreter.setIn(System.in);
        pythonInterpreter.setOut(System.out);
        pythonInterpreter.setErr(System.err);
    }

    public void eval(String cmd)
    {
//        PyObject outputObj = pythonInterpreter.eval("2 + 2");
//        pythonInterpreter.runsource("a = 2\nb = 3\na * b\n");
//        PyObject code = new PyObject();
//        code._
//        pythonInterpreter.runsource("a = 2\nb = 3\na * b\n");

//        String source = "a = 2\nb = 3\n(a * b) * \"Hello!\"\n";
        String source = "a = 2\n";
        PyObject code;
        try {
            code = Py.compile_command_flags(source, "<input>", CompileMode.exec, new CompilerFlags(), true);
//            pythonInterpreter.runcode(code);
            pythonInterpreter.exec(code);
            source = "b = 3\n";
            code = Py.compile_command_flags(source, "<input>", CompileMode.exec, new CompilerFlags(), true);
            pythonInterpreter.exec(code);
            source = "(a * b) * \'Hello\'\n";
            code = Py.compile_command_flags(source, "<input>", CompileMode.exec, new CompilerFlags(), true);
            pythonInterpreter.exec(code);
        } catch (PyException exc) {
            if (exc.match(Py.SyntaxError)) {
                // Case 1
//                showexception(exc);
//                return false;
            } else if (exc.match(Py.ValueError) || exc.match(Py.OverflowError)) {
                // Should not print the stack trace, just the error.
//                showexception(exc);
//                return false;
            } else {
                throw exc;
            }
        }

//        PyObject outputObj = pythonInterpreter.eval("a = 2\nb = 3\n");

//        String output = "";
//
//        if(outputObj != null)
//            output = outputObj.toString() + "\n";
//
//        System.out.println(output);
    }

    public static void main(String[] args)
    {
        PythonTest pythonTest = new PythonTest();

        pythonTest.eval("2 + 2");
    }
}
