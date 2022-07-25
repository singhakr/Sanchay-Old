/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.matrix;

/**
 *
 * @author anil
 */
public class SparseMatrixDouble3D extends SparseMatrix3D {
    private final int L;           // L-by-M-by-N matrix
    private final int M;
    private final int N;
    private SparseMatrixDouble[] rows;   // the rows, each row is a sparse matrix

    private double defaultValue = 0.0;

    // initialize an L-by-M-by-N matrix of all 0s
    public SparseMatrixDouble3D(int L, int M, int N, double defaultValue) {
        this.L  = L;
        this.M  = M;
        this.N  = N;

        rows = new SparseMatrixDouble[L];

        this.defaultValue = defaultValue;

        for (int i = 0; i < L; i++) rows[i] = new SparseMatrixDouble(M, N, defaultValue);
    }

    protected SparseMatrix[] getRows()
    {
        return rows;
    }

    public double getDefaultValue()
    {
        return defaultValue;
    }

    // put A[i][j][k] = value
    public void put(int i, int j, int k, double value) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index:" + i);
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index:" + j);
        if (k < 0 || k >= N) throw new RuntimeException("Illegal index:" + k);
        
        rows[i].put(j, k, value);
    }

    // return A[i][j][k]
    public double get(int i, int j, int k) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index: " + i);
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index: " + j);
        if (k < 0 || k >= N) throw new RuntimeException("Illegal index:" + k);

        return rows[i].get(j, k);
    }

    // increment st[i] by value
    public void increment(int i, int j, int k, double value) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index");
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index: " + j);
        if (k < 0 || k >= N) throw new RuntimeException("Illegal index:" + k);

        put(i, j, k, get(i, j, k) + value);
    }

    // return C = A + B
    public SparseMatrixDouble3D plus(SparseMatrixDouble3D B) {
        SparseMatrixDouble3D A = this;

        if (A.N != B.N) throw new RuntimeException("Dimensions disagree");
        if (A.getDefaultValue() != B.getDefaultValue()) throw new RuntimeException("Default values disagree");

        SparseMatrixDouble3D C = new SparseMatrixDouble3D(L, M, N, defaultValue);

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

    public double sum(int i) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index: "  + i);

        double s = 0.0f;

        for (int j = 0; j < L; j++) {
            s += sum(i, j);
        }

        return s;
    }

    public double sum(int i, int j) {
        if (i < 0 || i >= L) throw new RuntimeException("Illegal index: "  + i);
        if (j < 0 || j >= M) throw new RuntimeException("Illegal index: " + j);
 
        return rows[i].sum(j);
    }
}
