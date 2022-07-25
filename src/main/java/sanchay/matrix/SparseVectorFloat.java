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
public class SparseVectorFloat extends SparseVector {
    private final int N;             // length
    private LinkedHashMap<Integer, Float> vectorMap;  // the vector, represented by index-value pairs

    private float defaultValue = 0.0f;

    // initialize the all 0s vector of length N
    public SparseVectorFloat(int N, float defaultValue) {
        this.N  = N;
        this.vectorMap = new LinkedHashMap<Integer, Float>();

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

    public float getDefaultValue()
    {
        return defaultValue;
    }

    // put st[i] = value
    public void put(int i, float value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (value == defaultValue) vectorMap.remove(i);
        else              vectorMap.put(i, value);
    }

    // return st[i]
    public float get(int i) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (vectorMap.containsKey(i)) return vectorMap.get(i);
        else                return (float) defaultValue;
    }

    // increment st[i] by value
    public void increment(int i, float value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");

        put(i, get(i) + value);
    }

    // return the size of the vector
    public int size() {
        return N;
    }

    // return the dot product of this vector a with b
    public float dot(SparseVectorFloat b) {
        SparseVectorFloat a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        if (a.getDefaultValue() != b.getDefaultValue()) throw new RuntimeException("Default values disagree");
        float sum = 0.0f;

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
        SparseVectorFloat a = this;
        return Math.sqrt(a.dot(a));
    }

    // return alpha * a
    public SparseVectorFloat scale(float alpha) {
        SparseVectorFloat a = this;
        SparseVectorFloat c = new SparseVectorFloat(N, defaultValue);
        for (int i : a.vectorMap.keySet()) c.put(i, alpha * a.get(i));
        return c;
    }

    // return a + b
    public SparseVectorFloat plus(SparseVectorFloat b) {
        SparseVectorFloat a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        if (a.getDefaultValue() != b.getDefaultValue()) throw new RuntimeException("Default values disagree");
        SparseVectorFloat c = new SparseVectorFloat(N, defaultValue);
        for (int i : a.vectorMap.keySet()) c.put(i, a.get(i));                // c = a
        for (int i : b.vectorMap.keySet()) c.put(i, b.get(i) + c.get(i));     // c = c + b
        return c;
    }

    public float sum() {

        float s = 0.0f;

        for (int i : vectorMap.keySet()) s += get(i);

        return s;
    }

    // test client
    public static void main(String[] args) {
        SparseVectorFloat a = new SparseVectorFloat(10, 0.0f);
        SparseVectorFloat b = new SparseVectorFloat(10, 0.0f);
        a.put(3, 0.50f);
        a.put(9, 0.75f);
        a.put(6, 0.11f);
        a.put(6, 0.00f);
        b.put(3, 0.60f);
        b.put(4, 0.90f);
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("a dot b = " + a.dot(b));
        System.out.println("a + b   = " + a.plus(b));

        SparseVectorFloat c = a.plus(b);

        Object vals[] = c.getSortedValues(false);

        System.out.println("Sorted values: ");

        for (int i = 0; i < vals.length; i++)
        {
            Object object = vals[i];
            System.out.println("\t" + object);
        }

        vals = c.getTopNValues(2, false);

        System.out.println("Pruned values: ");

        for (int i = 0; i < vals.length; i++)
        {
            Object object = vals[i];
            System.out.println("\t" + object);
        }
    }
}
