/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.matrix;

import java.util.Iterator;
import java.util.LinkedHashMap;
import sanchay.mlearning.common.ModelScore;

/**
 *
 * @author anil
 */
public class SparseVector {

    protected LinkedHashMap getVectorMap()
    {
        return null;
    }

    protected void setVectorMap(LinkedHashMap vectorMap)
    {
    }

    // return the number of nonzero entries
    public int nnz() {
        return getVectorMap().size();
    }

    public int[] getSortedIndices(boolean ascending)
    {
        int sortedIndices[] = new int[getVectorMap().size()];

        LinkedHashMap sortedVectorMap = ModelScore.sortElementsByScores(getVectorMap(), ascending);

        Iterator<Integer> itr = sortedVectorMap.keySet().iterator();

        int i = 0;

        while(itr.hasNext())
        {
            Integer ind = itr.next();

            sortedIndices[i++] = ind.intValue();
        }

        return sortedIndices;
    }

    public int[] getTopNIndices(int n, boolean ascending)
    {
        int topN[] = new int[n];

        int sortedIndices[] = getSortedIndices(ascending);

        int count = Math.min(n, sortedIndices.length);

        for (int i = 0; i < count; i++)
        {
            topN[i] = sortedIndices[i];
        }

        return topN;
    }

    public Object[] getSortedValues(boolean ascending)
    {
        Object sortedValues[] = new Object[getVectorMap().size()];

        LinkedHashMap sortedVectorMap = ModelScore.sortElementsByScores(getVectorMap(), ascending);

        Iterator<Integer> itr = sortedVectorMap.keySet().iterator();

        int i = 0;

        while(itr.hasNext())
        {
            Integer ind = itr.next();

            Object val = getVectorMap().get(ind.intValue());
            
            sortedValues[i++] = val;
        }

        return sortedValues;
    }

    public Object[] getTopNValues(int n, boolean ascending)
    {
        Object topN[] = new Object[n];

        Object sortedValues[] = getSortedValues(ascending);

        int count = Math.min(n, sortedValues.length);

        for (int i = 0; i < count; i++)
        {
            topN[i] = sortedValues[i];
        }

        return topN;
    }

    public void pruneTopN(int n, boolean ascending)
    {
        setVectorMap(ModelScore.getTopN(getVectorMap(), n, ascending));
    }

    // return a string representation
    public String toString() {
        String s = "";

        Iterator itr = getVectorMap().keySet().iterator();

        while(itr.hasNext()) {
            Object key = itr.next();

            s += "(" + key + ", " + getVectorMap().get(key) + ") ";
        }
        return s;
    }
}
