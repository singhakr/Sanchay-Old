/*
 * DefaultContextModels.java
 *
 * Created on January 18, 2009, 5:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.context;

import sanchay.context.impl.ContextModels;
import sanchay.context.impl.Context;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author Anil Kumar Singh
 */
public class DefaultContextModels<K, M extends Context> implements ContextModels<K, M> {
    
    protected LinkedHashMap<K,M> contextModels;
    protected int windowSize;

    protected long contextElementTypeCount;
    protected long contextElementTokenCount;
    
    /** Creates a new instance of DefaultContextModels */
    public DefaultContextModels() {
        contextModels = new LinkedHashMap<K,M>(0, 5);
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
    
    public int countContextModels() {
        return contextModels.size();
    }
    
    public Iterator<K> getContextModelKeys()
    {
        return contextModels.keySet().iterator();
    }
    
    public M getContextModel(K key) {
        return contextModels.get(key);
    }
    
    public int addContextModel(K key, M cm) {
        contextModels.put(key, cm);
        
        return contextModels.size();
    }
    
    public M removeContextModel(K key) {
        return contextModels.remove(key);
    }

    public void readModels(String file, String cs) throws FileNotFoundException, IOException, Exception
    {
        
    }
    
    public void printModels(PrintStream ps) throws IOException, Exception
    {
        
    }

    public void saveModels(String file, String cs) throws FileNotFoundException, IOException, Exception {
        PrintStream ps = new PrintStream(file, cs);

        printModels(ps);
    }
    
    public void pruneTopN(int n, boolean ascending)
    {        
        Iterator<K> itr = getContextModelKeys();
        
        while(itr.hasNext()) {
            K key = itr.next();
            
            M context = getContextModel(key);
            context.pruneTopN(n, ascending);
        }
    }
    
    public long countContextElementTypes(boolean recount)
    {
        if(!recount)
            return contextElementTypeCount;

        Iterator<K> itr = getContextModelKeys();
        
        while(itr.hasNext()) {
            K key = itr.next();

            M context = getContextModel(key);

            if(context instanceof SimpleContextImpl)
                contextElementTypeCount += ((SimpleContextImpl) context).countContextElementTypes();
            else if(context instanceof FunctionalContextImpl)
                contextElementTypeCount += context.countContextElementTokens(recount);
        }

        return contextElementTypeCount;
    }
    
    public long countContextElementTokens(boolean recount)
    {        
        if(!recount)
            return contextElementTokenCount;

        Iterator<K> itr = getContextModelKeys();

        while(itr.hasNext()) {
            K key = itr.next();

            M context = getContextModel(key);

            contextElementTokenCount += context.countContextElementTokens(recount);
        }

        return contextElementTokenCount;
    }
}
