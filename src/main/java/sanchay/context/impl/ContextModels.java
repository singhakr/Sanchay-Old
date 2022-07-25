/*
 * ContextModels.java
 *
 * Created on January 18, 2009, 5:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.context.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

/**
 *
 * @author Anil Kumar Singh
 */
public interface ContextModels<K, M extends Context> {

    int countContextModels();
    
    Iterator<K> getContextModelKeys();

    M getContextModel(K key);
    
    int addContextModel(K key, M cm);
    
    M removeContextModel(K key);

    void readModels(String file, String cs) throws FileNotFoundException, IOException, Exception;
    
    void printModels(PrintStream ps) throws IOException, Exception;
    
    void saveModels(String file, String cs) throws FileNotFoundException, IOException, Exception;
    
    void pruneTopN(int n, boolean ascending);
    
    long countContextElementTypes(boolean recount);
    
    long countContextElementTokens(boolean recount);
}
