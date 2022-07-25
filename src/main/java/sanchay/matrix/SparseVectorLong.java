/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.matrix;

import java.util.LinkedHashMap;

/**
 *
 * @author anil
 */
public class SparseVectorLong extends SparseVector {
    private final int N;             // length
    private LinkedHashMap<Integer, Long> vectorMap;  // the vector, represented by index-value pairs

    private long defaultValue = 0;

    // initialize the all 0s vector of length N
    public SparseVectorLong(int N, long defaultValue) {
        this.N  = N;
        this.vectorMap = new LinkedHashMap<Integer, Long>();

        this.defaultValue = defaultValue;
    }

    protected LinkedHashMap getVectorMap()
    {
        return vectorMap;
    }

    protected void setVectorMap(LinkedHashMap vectorMap)
    {
        this.vectorMap = vectorMap;
    }

    public long getDefaultValue()
    {
        return defaultValue;
    }

    // put st[i] = value
    public void put(int i, long value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (value == defaultValue) vectorMap.remove(i);
        else              vectorMap.put(i, value);
    }

    // return st[i]
    public long get(int i) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");

        if (vectorMap.containsKey(i))
            return vectorMap.get(i);
        else
            return defaultValue;
    }

    // increment st[i] by value
    public void increment(int i, long value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");

        put(i, get(i) + value);
    }

    // return the size of the vector
    public int size() {
        return N;
    }

    // return the dot product of this vector a with b
    public long dot(SparseVectorLong b) {
        SparseVectorLong a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        if (a.getDefaultValue() != b.getDefaultValue()) throw new RuntimeException("Default values disagree");
        int sum = 0;

        // iterate over the vector with the fewest nonzeros
        if (a.vectorMap.size() <= b.vectorMap.size()) {
            for (int i : a.vectorMap.keySet())
                if (b.vectorMap.containsKey(i)) sum += a.get(i) * b.get(i);
        }
        else  {
            for (int i : b.vectorMap.keySet())
                if (a.vectorMap.containsKey(i)) sum += a.get(i) * b.get(i);
        }
        return sum;
    }

    // return the 2-norm
    public double norm() {
        SparseVectorLong a = this;
        return Math.sqrt(a.dot(a));
    }

    // return alpha * a
    public SparseVectorDouble scale(double alpha) {
        SparseVectorLong a = this;
        SparseVectorDouble c = new SparseVectorDouble(N, defaultValue);
        for (int i : a.vectorMap.keySet()) c.put(i, alpha * a.get(i));
        return c;
    }

    // return a + b
    public SparseVectorLong plus(SparseVectorLong b) {
        SparseVectorLong a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        if (a.getDefaultValue() != b.getDefaultValue()) throw new RuntimeException("Default values disagree");
        SparseVectorLong c = new SparseVectorLong(N, defaultValue);
        for (int i : a.vectorMap.keySet()) c.put(i, a.get(i));                // c = a
        for (int i : b.vectorMap.keySet()) c.put(i, b.get(i) + c.get(i));     // c = c + b
        return c;
    }

    public long sum() {

        long s = 0;

        for (int i : vectorMap.keySet()) s += get(i);

        return s;
    }
}
