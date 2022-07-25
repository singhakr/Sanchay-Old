/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.matrix;

/**
 *
 * @author anil
 */

public class SparseMatrixDouble extends SparseMatrix {
    private final int M;           // M-by-N matrix
    private final int N;
    private SparseVectorDouble[] rows;   // the rows, each row is a sparse vector

    private double defaultValue = 0.0;

    // initialize an M-by-N matrix of all 0s
    public SparseMatrixDouble(int M, int N, double defaultValue) {
        this.M  = M;
        this.N  = N;
        rows = new SparseVectorDouble[M];

        this.defaultValue = defaultValue;

        for (int i = 0; i < M; i++) rows[i] = new SparseVectorDouble(N, defaultValue);
    }

    protected SparseVector[] getRows()
    {
        return rows;
    }

    public double getDefaultValue()
    {
        return defaultValue;
    }

    // put A[i][j] = value
    public void put(int i, int j, double value) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index:" + i);
        if (j < 0 || j >= N) throw new RuntimeException("Illegal index:" + j);
        rows[i].put(j, value);
    }

    // return A[i][j]
    public double get(int i, int j) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index: " + i);
        if (j < 0 || j >= N) throw new RuntimeException("Illegal index:" + j);
        return rows[i].get(j);
    }

    // increment st[i] by value
    public void increment(int i, int j, double value) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index: " + i);
        if (j < 0 || j >= N) throw new RuntimeException("Illegal index:" + j);

        put(i, j, get(i, j) + value);
    }

    // return the matrix-vector product b = Ax
    public SparseVectorDouble times(SparseVectorDouble x) {
        SparseMatrixDouble A = this;
        if (M != x.size()) throw new RuntimeException("Dimensions disagree");
        if (getDefaultValue() != x.getDefaultValue()) throw new RuntimeException("Default values disagree");
        SparseVectorDouble b = new SparseVectorDouble(M, defaultValue);
        for (int i = 0; i < M; i++)
            b.put(i, A.rows[i].dot(x));
        return b;
    }

    // return C = A + B
    public SparseMatrixDouble plus(SparseMatrixDouble B) {
        SparseMatrixDouble A = this;
        if (A.M != B.M) throw new RuntimeException("Dimensions disagree");
        if (A.getDefaultValue() != B.getDefaultValue()) throw new RuntimeException("Default values disagree");
        SparseMatrixDouble C = new SparseMatrixDouble(M, N, getDefaultValue());
        for (int i = 0; i < M; i++)
            C.rows[i] = A.rows[i].plus(B.rows[i]);
        return C;
    }

    public int rowSize() {
        return M;
    }

    public int columnSize() {
        return N;
    }

    public double sum(int i) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index:" + i);

        return rows[i].sum();
    }

    // test client
    public static void main(String[] args) {
        SparseMatrixDouble A = new SparseMatrixDouble(5, 5, 0.0);
        SparseVectorDouble x = new SparseVectorDouble(5, 0.0);
        A.put(0, 0, 1.0);
        A.put(1, 1, 1.0);
        A.put(2, 2, 1.0);
        A.put(3, 3, 1.0);
        A.put(4, 4, 1.0);
        A.put(2, 4, 0.3);
        x.put(0, 0.75);
        x.put(2, 0.11);
        System.out.println("x     : " + x);
        System.out.println("A     : " + A);
        System.out.println("Ax    : " + A.times(x));
        System.out.println("A + A : " + A.plus(A));
    }
}
