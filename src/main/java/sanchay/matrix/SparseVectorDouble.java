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

public class SparseVectorDouble extends SparseVector {
    private final int N;             // length
    private LinkedHashMap<Integer, Double> vectorMap;  // the vector, represented by index-value pairs

    private double defaultValue = 0.0;

    // initialize the all 0s vector of length N
    public SparseVectorDouble(int N, double defaultValue) {
        this.N  = N;
        this.vectorMap = new LinkedHashMap<Integer, Double>();

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

    public double getDefaultValue()
    {
        return defaultValue;
    }

    // put st[i] = value
    public void put(int i, double value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (value == defaultValue) vectorMap.remove(i);
        else              vectorMap.put(i, value);
    }

    // return st[i]
    public double get(int i) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (vectorMap.containsKey(i)) return vectorMap.get(i);
        else                return defaultValue;
    }

    // increment st[i] by value
    public void increment(int i, double value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");

        put(i, get(i) + value);
    }

    // return the size of the vector
    public int size() {
        return N;
    }

    // return the dot product of this vector a with b
    public double dot(SparseVectorDouble b) {
        SparseVectorDouble a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        if (a.getDefaultValue() != b.getDefaultValue()) throw new RuntimeException("Default values disagree");
        double sum = 0.0;

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
        SparseVectorDouble a = this;
        return Math.sqrt(a.dot(a));
    }

    // return alpha * a
    public SparseVectorDouble scale(double alpha) {
        SparseVectorDouble a = this;
        SparseVectorDouble c = new SparseVectorDouble(N, defaultValue);
        for (int i : a.vectorMap.keySet()) c.put(i, alpha * a.get(i));
        return c;
    }

    // return a + b
    public SparseVectorDouble plus(SparseVectorDouble b) {
        SparseVectorDouble a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        if (a.getDefaultValue() != b.getDefaultValue()) throw new RuntimeException("Default values disagree");
        SparseVectorDouble c = new SparseVectorDouble(N, defaultValue);
        for (int i : a.vectorMap.keySet()) c.put(i, a.get(i));                // c = a
        for (int i : b.vectorMap.keySet()) c.put(i, b.get(i) + c.get(i));     // c = c + b
        return c;
    }

    public double sum() {

        double s = 0.0f;

        for (int i : vectorMap.keySet()) s += get(i);

        return s;
    }

    // test client
    public static void main(String[] args) {
        SparseVectorDouble a = new SparseVectorDouble(10, 0);
        SparseVectorDouble b = new SparseVectorDouble(10, 0);
        a.put(3, 0.50);
        a.put(9, 0.75);
        a.put(6, 0.11);
        a.put(6, 0.00);
        b.put(3, 0.60);
        b.put(4, 0.90);
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("a dot b = " + a.dot(b));
        System.out.println("a + b   = " + a.plus(b));

        SparseVectorDouble c = a.plus(b);

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
