/*
 * ModelScore.java
 *
 * Created on January 25, 2009, 7:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Anil Kumar Singh
 */
public class ModelScore<K, S extends Comparable> implements Comparable {
    
    /** Creates a new instance of ModelScore */
    public K modelKey;
    public S modelScore;

    public ModelScore(K modelKey, S modelScore)
    {
        this.modelKey = modelKey;
        this.modelScore = modelScore;
    }    

    public static <K extends Object, S extends Comparable>
            LinkedHashMap<K, S> sortElementsByScores(LinkedHashMap<K,S> elements, boolean ascending)
    {
        LinkedHashMap<K, S> sortedElements = new LinkedHashMap<K, S>(elements.size());
    	List<ModelScore> sortedScores = new Vector<ModelScore>(elements.size());

        Iterator<K> itr = elements.keySet().iterator();

        while(itr.hasNext())
        {
            K key = itr.next();

            S score = elements.get(key);
            ModelScore<K, S> ms = new ModelScore<K, S>(key, score);
            sortedScores.add(ms);
        }

        if(!ascending)
            Collections.sort(sortedScores, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) o2).compareTo((Comparable) o1);
            }
            });
        else
            Collections.sort(sortedScores, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) o1).compareTo((Comparable) o2);
            }
            });

        for (int i = 0; i < sortedScores.size(); i++)
        {
            ModelScore<K,S> ms = (ModelScore<K,S>) sortedScores.get(i);
            sortedElements.put(ms.modelKey, ms.modelScore);
        }

        return sortedElements;
   }

    public static <K extends Object, S extends Comparable>
            LinkedHashMap<K, S> getTopN(LinkedHashMap<K,S> elements, int N, boolean ascending)
    {
        LinkedHashMap<K, S> sortedMap = sortElementsByScores(elements, ascending);

        LinkedHashMap<K, S> topN = new LinkedHashMap<K, S>(N);

        Iterator<K> itr = sortedMap.keySet().iterator();

        int i = 0;

        while(itr.hasNext() && i < N)
        {
            K k = itr.next();

            S s = sortedMap.get(k);

            topN.put(k, s);

            i++;
        }

        return topN;
    }

    @Override
    public int compareTo(Object o)
    {
        return modelScore.compareTo(((ModelScore) o).modelScore);
    }

    public static void main(String args[])
    {
        LinkedHashMap<String, Integer> scores =  new LinkedHashMap<String, Integer>();

        scores.put("a", new Integer(1));
        scores.put("b", new Integer(3));
        scores.put("c", new Integer(5));

        LinkedHashMap<String, Integer> sorted =  ModelScore.sortElementsByScores(scores, true);
//        LinkedHashMap<String, Integer> sorted = ModelScore.getTopN(scores, 2, false);

        Iterator<String> itr = sorted.keySet().iterator();

        while(itr.hasNext())
        {
            String k = itr.next();

            Integer s = sorted.get(k);

            System.out.println(k + "\t" + s);
        }
    }
}
