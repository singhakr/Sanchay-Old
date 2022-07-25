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
public class SparseMatrix {

    protected SparseVector[] getRows()
    {
        return null;
    }

    public int rowSize() {
        return -1;
    }

    public int columnSize() {
        return -1;
    }

    public int nnz() {
        int sum = 0;

        SparseVector[] rows = getRows();

        for (int i = 0; i < rows.length; i++)
            sum += rows[i].nnz();
        return sum;
    }

    public int nnz(int row) {
        SparseVector[] rows = getRows();

        return rows[row].nnz();
    }

    public int[] getSortedIndices(int row, boolean ascending)
    {
        SparseVector[] rows = getRows();

        return rows[row].getSortedIndices(ascending);
    }

    public int[] getTopNIndices(int row, int n, boolean ascending)
    {
        SparseVector[] rows = getRows();

        return rows[row].getTopNIndices(n, ascending);
    }

    public Object[] getSortedValues(int row, boolean ascending)
    {
        SparseVector[] rows = getRows();

        return rows[row].getSortedValues(ascending);
    }

    public Object[] getTopNValues(int row, int n, boolean ascending)
    {
        SparseVector[] rows = getRows();

        return rows[row].getTopNValues(n, ascending);
    }

    public void pruneTopN(int row, int n, boolean ascending)
    {
        SparseVector[] rows = getRows();

        rows[row].pruneTopN(n, ascending);
    }

    // return a string representation
    public String toString() {
        SparseVector[] rows = getRows();

        int M = rows.length;
        int N = columnSize();

        String s = "M = " + M + ", N = " + N + ", nonzeros = " + nnz() + "\n";
        for (int i = 0; i < M; i++) {
            s += i + ": " + rows[i] + "\n";
        }
        return s;
    }

}
