/*
 * Created on Sep 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.corpus.xml.impl;

import sanchay.corpus.*;
import sanchay.corpus.xml.*;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLTextImpl extends Text implements XMLText {

    protected String inputPath;
    protected String outputPath;

    /**
     * 
     */
    public XMLTextImpl() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getInputPath()
    {
        return inputPath;
    }

    public void setInputPath(String p)
    {
        inputPath = p;
    }

    public String getOutputPath()
    {
        return outputPath;
    }

    public void setOutputPath(String p)
    {
        outputPath = p;
    }

    public static void main(String[] args) {
    }
}
