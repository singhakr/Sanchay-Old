/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.matrix;

/**
 *
 * @author anil
 */
public class SparseMatrixFloat3D extends SparseMatrix3D {
    private final int L;           // L-by-M-by-N matrix
    private final int M;
    private final int N;
    private SparseMatrixFloat[] rows;   // the rows, each row is a sparse matrix

    private float defaultValue = 0.0f;

    // initialize an L-by-M-by-N matrix of all 0s
    public SparseMatrixFloat3D(int L, int M, int N, float defaultValue) {
        this.L  = L;
        this.M  = M;
        this.N  = N;

        this.defaultValue = defaultValue;

        rows = new SparseMatrixFloat[L];

        for (int i = 0; i < L; i++) rows[i] = new SparseMatrixFloat(M, N, defaultValue);
    }

    protected SparseMatrix[] getRows()
    {
        return rows;
    }

    public float getDefaultValue()
    {
        return defaultValue;
    }

    // put A[i][j][k] = value
    public void put(int i, int j, int k, float value) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index:" + i);
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index:" + j);
        if (k < 0 || k >= N) throw new RuntimeException("Illegal index:" + k);

        rows[i].put(j, k, value);
    }

    // return A[i][j][k]
    public float get(int i, int j, int k) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index:" + i);
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index:" + j);
        if (k < 0 || k >= N) throw new RuntimeException("Illegal index:" + k);

        return rows[i].get(j, k);
    }

    // increment st[i] by value
    public void increment(int i, int j, int k, float value) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index:" + i);
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index:" + j);
        if (k < 0 || k >= N) throw new RuntimeException("Illegal index");

        put(i, j, k, get(i, j, k) + value);
    }

    // return C = A + B
    public SparseMatrixFloat3D plus(SparseMatrixFloat3D B) {
        SparseMatrixFloat3D A = this;

        if (A.N != B.N) throw new RuntimeException("Dimensions disagree");
        if (A.getDefaultValue() != B.getDefaultValue()) throw new RuntimeException("Default values disagree");

        SparseMatrixFloat3D C = new SparseMatrixFloat3D(L, M, N, defaultValue);

        for (int i = 0; i < L; i++)
            C.rows[i] = A.rows[i].plus(B.rows[i]);

        return C;
    }

    public int rowSize() {
        return L;
    }

    public int columnSize() {
        return M;
    }

    public int stackSize() {
        return N;
    }

    public float sum(int i) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index:" + i);

        float s = 0.0f;

        for (int j = 0; j < L; j++) {
            s += sum(i, j);
        }

        return s;
    }

    public float sum(int i, int j) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index: " + i);
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index: " + j);

        return rows[i].sum(j);
    }
}
