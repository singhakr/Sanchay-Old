/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.matrix;

/**
 *
 * @author anil
 */
public class SparseMatrixFloat extends SparseMatrix {
    private final int M;           // M-by-N matrix
    private final int N;
    private SparseVectorFloat[] rows;   // the rows, each row is a sparse vector

    private float defaultValue = 0.0f;

    // initialize an M-by-N matrix of all 0s
    public SparseMatrixFloat(int M, int N, float defaultValue) {
        this.M  = M;
        this.N  = N;
        rows = new SparseVectorFloat[M];

        this.defaultValue = defaultValue;

        for (int i = 0; i < M; i++) rows[i] = new SparseVectorFloat(N, defaultValue);
    }

    protected SparseVector[] getRows()
    {
        return rows;
    }

    public float getDefaultValue()
    {
        return defaultValue;
    }

    // put A[i][j] = value
    public void put(int i, int j, float value) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index:" + i);
        if (j < 0 || j >= N) throw new RuntimeException("Illegal index:" + j);
        rows[i].put(j, value);
    }

    // return A[i][j]
    public float get(int i, int j) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index: " + i);
        if (j < 0 || j >= N) throw new RuntimeException("Illegal index:" + j);
        return rows[i].get(j);
    }

    // increment st[i] by value
    public void increment(int i, int j, float value) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index: " + i);
        if (j < 0 || j >= N) throw new RuntimeException("Illegal index:" + j);

        put(i, j, get(i, j) + value);
    }

    // return the matrix-vector product b = Ax
    public SparseVectorFloat times(SparseVectorFloat x) {
        SparseMatrixFloat A = this;
        if (M != x.size()) throw new RuntimeException("Dimensions disagree");
        if (getDefaultValue() != x.getDefaultValue()) throw new RuntimeException("Default values disagree");
        SparseVectorFloat b = new SparseVectorFloat(M, defaultValue);
        for (int i = 0; i < M; i++)
            b.put(i, A.rows[i].dot(x));
        return b;
    }

    // return C = A + B
    public SparseMatrixFloat plus(SparseMatrixFloat B) {
        SparseMatrixFloat A = this;
        if (A.M != B.M) throw new RuntimeException("Dimensions disagree");
        if (A.getDefaultValue() != B.getDefaultValue()) throw new RuntimeException("Default values disagree");
        SparseMatrixFloat C = new SparseMatrixFloat(M, N, defaultValue);
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

    public float sum(int i) {
        if (i < 0 || i >= M) throw new RuntimeException("Illegal index:" + i);

        return rows[i].sum();
    }

    // test client
    public static void main(String[] args) {
        SparseMatrixFloat A = new SparseMatrixFloat(5, 5, 0.0f);
        SparseVectorFloat x = new SparseVectorFloat(5, 0.0f);
        A.put(0, 0, 1.0f);
        A.put(1, 1, 1.0f);
        A.put(2, 2, 1.0f);
        A.put(3, 3, 1.0f);
        A.put(4, 4, 1.0f);
        A.put(2, 4, 0.3f);
        x.put(0, 0.75f);
        x.put(2, 0.11f);
        System.out.println("x     : " + x);
        System.out.println("A     : " + A);
        System.out.println("Ax    : " + A.times(x));
        System.out.println("A + A : " + A.plus(A));
    }
}
