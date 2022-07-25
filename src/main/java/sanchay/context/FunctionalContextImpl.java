/*
 * FunctionalContextImpl.java
 *
 * Created on October 12, 2008, 7:53 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.context;

import sanchay.context.impl.FunctionalContextElement;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import sanchay.context.impl.FunctionalContext;
import sanchay.mlearning.common.ModelScoreEx;

/**
 *
 * @author Anil Kumar Singh
 */
public class FunctionalContextImpl<K, E, CE extends FunctionalContextElement<E>> implements FunctionalContext<K, E, CE> {
    
    protected LinkedHashMap<K, LinkedHashMap<Integer, CE>> contextElements;
    protected long contextElementTypeCount;
    protected long contextElementTokenCount;
    
    protected int pruneTopN = 100;
    
    /** Creates a new instance of FunctionalContextImpl */
    public FunctionalContextImpl() {
        contextElements = new LinkedHashMap<K, LinkedHashMap<Integer, CE>>(0, 5);
    }

    public long countContextElementTypes(boolean recount)
    {
        if(recount || contextElementTypeCount == 0)
        {
            contextElementTypeCount = 0;
            
            Iterator<K> itr = getContextElementKeys();
            
            if(itr == null)
                return 0;
            
            while(itr.hasNext())
            {
                K key = itr.next();
                LinkedHashMap<Integer, FunctionalContextElement<E>> elements = (LinkedHashMap<Integer, FunctionalContextElement<E>>) contextElements.get(key);
                
                contextElementTypeCount += elements.size();
            }
        }
        
        return contextElementTypeCount;
    }

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
                
                Iterator<Integer> enm = getContextElementKeys(key);
                
                while(enm.hasNext())
                {
                    Integer distanceInt = (Integer) enm.next();

                    FunctionalContextElement<E> contextElement = (FunctionalContextElement) getContextElement(key, distanceInt.intValue());
                
                    contextElementTokenCount += contextElement.getFreq();
                }
            }
        }

        return contextElementTokenCount;        
    }
    
    public long countContextElementTypes(K key)
    {
        LinkedHashMap<Integer, CE> elements = contextElements.get(key);
        
        if(elements == null)
            return 0;
        
        return elements.size();        
    }

    public long countContextElementTokens(K key)
    {
        LinkedHashMap<Integer, CE> elements = contextElements.get(key);
        
        if(elements == null)
            return 0;
        
        long tokenCount = 0;
        
        Iterator<Integer> enm = getContextElementKeys(key);

        while(enm.hasNext())
        {
            Integer distanceInt = (Integer) enm.next();

            FunctionalContextElement<E> contextElement = getContextElement(key, distanceInt.intValue());

            tokenCount += contextElement.getFreq();
        }
                
        return tokenCount;        
    }

    public Iterator<K> getContextElementKeys()
    {
        return contextElements.keySet().iterator();
    }

    public Iterator<Integer> getContextElementKeys(K key)
    {
        LinkedHashMap<Integer, CE> elements = contextElements.get(key);
        
        if(elements == null)
            return null;
        
        return contextElements.get(key).keySet().iterator();
    }

    public CE getContextElement(K key, int distance)
    {
        LinkedHashMap<Integer, CE> elements = contextElements.get(key);
        
        if(elements == null)
            return null;
        
        return elements.get(new Integer(distance));        
    }

    public long addContextElement(K key, int distance, CE ce)
    {
        LinkedHashMap<Integer, CE> elements = contextElements.get(key);
        
        if(elements == null)
        {
            elements = new LinkedHashMap(1, 5);
            contextElements.put(key, elements);            
        }
        
        elements.put(new Integer(distance), ce);        
        
        contextElementTokenCount++;

        return contextElementTypeCount++;
    }

    public CE removeContextElement(K key, int distance)
    {
        LinkedHashMap<Integer, CE> elements = contextElements.get(key);
        
        if(elements == null)
            return null;
        
        CE contextElement = elements.remove(new Integer(distance));
        
        if(contextElement != null)
            contextElementTypeCount--;
        
        return contextElement;        
    }

    public void print(PrintStream ps) throws IOException, Exception
    {
        Iterator<K> itr = getContextElementKeys();
        
        while(itr.hasNext())
        {
            K key = itr.next();

            Iterator<Integer> enm = getContextElementKeys(key);

            while(enm.hasNext())
            {
                Integer distanceInt = (Integer) enm.next();
                
                CE contextElement = getContextElement(key, distanceInt.intValue());
                contextElement.print(ps);
            }
        }
    }
    
    public void pruneTopN(int n, boolean ascending)
    {
        pruneTopN = n;
        
        Vector<ModelScoreEx> sortedScores = new Vector<ModelScoreEx>(contextElements.size(), contextElements.size());

        Iterator<K> itr = getContextElementKeys();

        while (itr.hasNext()) {
            K key = itr.next();

            Iterator<Integer> enm = getContextElementKeys(key);

            while (enm.hasNext()) {
                Integer distanceInt = (Integer) enm.next();

                CE contextElement = getContextElement(key, distanceInt.intValue());

                ModelScoreEx<String, Double, CE> ms = new ModelScoreEx<String, Double, CE>(key + "\t" + distanceInt.intValue(), (double) contextElement.getFreq(), contextElement);
                
                sortedScores.add(ms);
            }
        }

        if(ascending)
        {
            Collections.sort(sortedScores, new Comparator() {

                public int compare(Object o1, Object o2) {
                    return ((Comparable) o1).compareTo((Comparable) o2);
                }
            }); 
        }
        else
        {
            Collections.sort(sortedScores, new Comparator() {

                public int compare(Object o1, Object o2) {
                    return ((Comparable) o2).compareTo((Comparable) o1);
                }
            });
        }

        int count = Math.min(sortedScores.size(), pruneTopN);

        contextElements.clear();

        for (int i = 0; i < count; i++) {
            ModelScoreEx<String, Double, CE> ms = sortedScores.get(i);

            String parts[] = ((String) ms.modelKey).split("[\t]");
            String key = parts[0];
            int dist = Integer.parseInt(parts[1]);
            long freq = (long) ms.modelScore.doubleValue();

            addContextElement((K) key, dist, ms.modelObject);
        }
    }
}
