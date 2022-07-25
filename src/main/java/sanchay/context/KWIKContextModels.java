/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.context;

import sanchay.context.impl.ContextModels;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.util.query.FindReplace;
import sanchay.util.query.FindReplaceOptions;

/**
 *
 * @author Anil Kumar Singh
 */
public class KWIKContextModels extends DefaultContextModels implements ContextModels {

    @Override
    public void readModels(String file, String cs) throws FileNotFoundException, IOException, Exception
    {

    }

    @Override
    public void printModels(PrintStream ps) throws IOException, Exception
    {
    }

    public void fillModels(String[] lines, FindReplaceOptions findReplaceOptions)
    {
        Pattern p = FindReplace.compilePattern(findReplaceOptions.findText, findReplaceOptions);
        
        Matcher m = null;

        int kwordCount = 0;

        for (int i = 0; i < lines.length; i++)
        {
            String string = lines[i];
            m = p.matcher(string);

            while(m.find())
            {
                int start = m.start();
                int end = m.end();

                kwordCount++;

                String lcontext = string.substring(0, start);
                String kword = string.substring(start, end);
                String rcontext = string.substring(end - 1, string.length());

                KWIKContext kwikContext = new KWIKContext();

                KWIKContextElement leftKWIKContextElement = new KWIKContextElement();
                KWIKContextElement kwordKWIKContextElement = new KWIKContextElement();
                KWIKContextElement rightKWIKContextElement = new KWIKContextElement();

                leftKWIKContextElement.setContextElement(lcontext);
                kwordKWIKContextElement.setContextElement(kword);
                rightKWIKContextElement.setContextElement(rcontext);

                kwikContext.setLeftContext(leftKWIKContextElement);
                kwikContext.setKeyword(kwordKWIKContextElement);
                kwikContext.setRightContext(rightKWIKContextElement);

                addContextModel(kword + Integer.toString(kwordCount), kwikContext);
            }
        }
    }
}
