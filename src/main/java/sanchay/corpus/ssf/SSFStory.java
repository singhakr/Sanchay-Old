/*
 * SSFStory.java
 *
 * Created on October 11, 2005, 4:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.ssf;

import java.io.*;
import java.util.*;
import sanchay.common.types.CorpusType;

/**
 *
 *  @author Anil Kumar Singh
 */
public interface SSFStory extends SSFText {

    void readFile(String filePath, String cs, CorpusType corpusType, List<String> errorLog /*Strings*/)
            throws Exception, FileNotFoundException, IOException;

    void readFile(String filePath, String cs, CorpusType corpusType) throws Exception, FileNotFoundException, IOException;

    void readFile(String filepath) throws Exception, FileNotFoundException, IOException;

    void readFile(String filePath, String cs) throws Exception, FileNotFoundException, IOException;

    void readString(String string) throws Exception, FileNotFoundException, IOException;

//    void readFileTreeForm(String filePath, String cs) throws Exception, FileNotFoundException, IOException;
//    void printTreeForm(String filePath, String cs, PrintStream ps) throws Exception, FileNotFoundException, IOException;
//    void saveTreeForm(String filePath, String cs) throws Exception, FileNotFoundException, IOException;
}
