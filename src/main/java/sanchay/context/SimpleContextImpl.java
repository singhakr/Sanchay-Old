/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.context;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import sanchay.context.impl.ContextElement;
import sanchay.context.impl.SimpleContext;
import sanchay.mlearning.common.ModelScore;

/**
 *
 * @author anil
 */
public class SimpleContextImpl<K, E, CE extends ContextElementImpl<E>>
        implements SimpleContext<K, E, CE> {

    protected LinkedHashMap<K, CE> contextElements;

    protected long contextElementTypeCount;
    protected long contextElementTokenCount;

    protected int pruneTopN = 100;

    /** Creates a new instance of FunctionalContextImpl */
    public SimpleContextImpl() {
        contextElements = new LinkedHashMap<K, CE>(0, 5);
    }

    @Override
    public Iterator<K> getContextElementKeys()
    {
        return contextElements.keySet().iterator();
    }

    @Override
    public CE getContextElement(K key)
    {
        return contextElements.get(key);
    }

    @Override
    public long addContextElement(K key, CE ce)
    {
        contextElements.put(key, ce);

        contextElementTypeCount = contextElements.size();

        return contextElementTypeCount;
    }

    @Override
    public CE removeContextElement(K key)
    {
        CE ce = contextElements.remove(key);

        contextElementTypeCount = contextElements.size();

        return ce;
    }

    public long countContextElementTypes()
    {
        contextElementTypeCount = contextElements.size();

        return contextElementTypeCount;
    }

    @Override
    public long countContextElementTokens(boolean recount)
    {
        if(recount || contextElementTokenCount == 0)
        {
            contextElementTokenCount = 0;

            Iterator<K> itr = getContextElementKeys();

            if(itr == null)
                return 0;

            while(itr.hasNext())
            {
                K key = itr.next();

                ContextElement<E> element = contextElements.get(key);

                contextElementTokenCount += element.getFreq();
            }
        }

        return contextElementTokenCount;
    }

    @Override
    public void print(PrintStream ps) throws IOException, Exception
    {
        Iterator<K> itr = getContextElementKeys();

        if(itr == null)
            return;

        while(itr.hasNext())
        {
            K key = itr.next();

            ContextElement<E> element = contextElements.get(key);
            
            element.print(ps);
        }
    }

    @Override
    public void pruneTopN(int n, boolean ascending)
    {
        pruneTopN = n;
        contextElements = ModelScore.getTopN(contextElements, pruneTopN, ascending);
    }
}
