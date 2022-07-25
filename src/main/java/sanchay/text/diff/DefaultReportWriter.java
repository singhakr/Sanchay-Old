package sanchay.text.diff;

import java.io.PrintStream;

/**
 * Implements IREportWriter to generate a human-friendly report to any
 * PrintStream.
 */
public class DefaultReportWriter implements IReportWriter
{
    private PrintStream printStream;
    private String lineNumPad = "    "; // Line numbers will be padded to this

    public DefaultReportWriter()
    {
    }

    public DefaultReportWriter(PrintStream aStream)
    {
        printStream = aStream;
    }

    public String makeString(EditCommand command)
    {
        String reportStr = "";

        reportStr += command.command + " ";
        reportStr += makeString(command.oldLines, "Old" );
        reportStr += makeString(command.newLines, "New" );
        reportStr += " " + "\n";

        return reportStr;
    }

    public void report(EditCommand command)
    {
        printStream.print(makeString(command));
//        printStream.print( command.command + " " );
//        print( command.oldLines, "Old" );
//        print( command.newLines, "New" );
//        printStream.println( " " );
    }

    private void print(LineBlock lineBlock, String fileDescription)
    {
        printStream.println( makeString(lineBlock, fileDescription) );
//        if (null != lineBlock)
//        {
//            printStream.println( fileDescription + " line(s) " + (lineBlock.fromLineNum + 1) + "-"
//                    + (lineBlock.thruLineNum + 1) + " " );
//            if (lineBlock.reportable)
//            {
//                int lineNum = lineBlock.fromLineNum + 1;
//                for (int i = 0; i < lineBlock.lines.length; i++)
//                {
//                    printStream.println( pad( lineNum++ ) + ": " + lineBlock.lines[i] );
//                }
//            }
//        }
    }

    public String makeString(LineBlock lineBlock, String fileDescription)
    {
        String reportStr = "";

        if (null != lineBlock)
        {
            reportStr += fileDescription + " line(s) " + (lineBlock.fromLineNum + 1) + "-"
                    + (lineBlock.thruLineNum + 1) + " " + "\n";

            if (lineBlock.reportable)
            {
                int lineNum = lineBlock.fromLineNum + 1;
                for (int i = 0; i < lineBlock.lines.length; i++)
                {
                    reportStr +=  pad( lineNum++ ) + ": " + lineBlock.lines[i] + "\n";
                }
            }
        }

        return reportStr;
    }

    private String pad(int lineNum)
    {
        String paddedNum = "" + lineNum;
        if (paddedNum.length() < lineNumPad.length())
            return (lineNumPad + paddedNum).substring( paddedNum.length() );
        else
            return paddedNum;
    }
}