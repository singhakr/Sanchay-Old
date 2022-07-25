/*
 * SyntacticAnnotationTask.java
 *
 * Created on November 1, 2005, 5:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.resources.aggregate;

import java.io.*;

/**
 *
 *  @author Anil Kumar Singh
 */
public class SyntacticAnnotationTask extends AggregateResourceImpl implements AggregateResource {
    
    /** Creates a new instance of SyntacticAnnotationTask */
    public SyntacticAnnotationTask(String taskFile, String taskCharset) {
    }
    
    public int read() throws FileNotFoundException, IOException
    {
        return -1;
    }

    public int read(String f, String charset) throws FileNotFoundException, IOException
    {
        return -1;
    }
   
    public int save() throws FileNotFoundException, UnsupportedEncodingException
    {
        return -1;
    }

    public int save(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        return -1;
    }
}
