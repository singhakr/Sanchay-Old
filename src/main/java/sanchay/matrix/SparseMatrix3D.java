/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.matrix;

/**
 *
 * @author anil
 */
public class SparseMatrix3D {

    protected SparseMatrix[] getRows()
    {
        return null;
    }

    public int rowSize() {
        return -1;
    }

    public int columnSize() {
        return -1;
    }

    public int stackSize() {
        return -1;
    }

    // return the number of nonzero entries (not the most efficient implementation)
    public int nnz() {
        int sum = 0;

        SparseMatrix[] rows = getRows();

        for (int i = 0; i < rows.length; i++)
            sum += rows[i].nnz();
        return sum;
    }

    public int nnz(int row) {
        SparseMatrix[] rows = getRows();

        return rows[row].nnz();
    }

    public int nnz(int row, int col) {
        SparseMatrix[] rows = getRows();

        return rows[row].nnz(col);
    }

    public int[] getSortedIndices(int row, boolean ascending)
    {
        SparseMatrix[] rows = getRows();

        return rows[row].getSortedIndices(row, ascending);
    }

    public int[] getTopNIndices(int row, int n, boolean ascending)
    {
        SparseMatrix[] rows = getRows();

        return rows[row].getTopNIndices(row, n, ascending);
    }

    public Object[] getSortedValues(int row, boolean ascending)
    {
        SparseMatrix[] rows = getRows();

        return rows[row].getSortedValues(row, ascending);
    }

    public Object[] getTopNValues(int row, int n, boolean ascending)
    {
        SparseMatrix[] rows = getRows();

        return rows[row].getTopNValues(row, n, ascending);
    }

    public void pruneTopN(int row, int n, boolean ascending)
    {
        SparseMatrix[] rows = getRows();

        rows[row].pruneTopN(row, n, ascending);
    }

    // return a string representation
    public String toString() {
        SparseMatrix[] rows = getRows();

        int L = rowSize();
        int M = columnSize();
        int N = stackSize();

        String s = "L = " + L + ", M = " + M + ", N = " + N + ", nonzeros = " + nnz() + "\n";
        for (int i = 0; i < M; i++) {
            s += i + ": " + rows[i] + "\n";
        }
        return s;
    }
}
