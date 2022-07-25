/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import sanchay.table.SanchayTableModel;



/**
 *
 * @author ram
 */
public class TableTester {

    /**
     * @param args the command line arguments
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        SanchayTableModel m;
        m = new SanchayTableModel("data/SanchayTableModel/sampledata","UTF-8");
        System.err.println("*"+m.getColumnName(0)+"*");
        System.err.println("*"+m.getColumnName(1)+"*");
        System.err.println(m.getColumnName(2));
        System.err.println("rowcount "+m.getRowCount());
        System.err.println("colcount "+m.getColumnCount());
        System.err.println("element "+m.getValueAt(0, 0));

        m.print(System.err);

        System.out.println(m.getColumn("age"));
        System.out.println(m.getColumn("a"));
    }

}

  /*  private Vector viterbiSegmentWord(String word) {

    int T = word.length();
    double badLikelihood = (T+1)*logNMorphTokens;
    double pseudoInfiniteCost = (T+1)*badLikelihood;
    int t, l;
    Morph morph;
    double logp;

    double delta[], psi[], bestDelta, bestl, currentDelta;

    // Viterbi segmentation
    delta[0] = 0;
    psi[0] = 0;
    foreach t (1 .. T) {
	bestDelta = pseudoInfiniteCost;
	bestl = 0;
      L_LOOP:
	foreach l (1 .. t) {
	    morph = substr(word, t - l, l);
	    if (defined(morphLogProb{morph})) {
		logp = morphLogProb{morph};
	    }
	    elsif (l == 1) {

            logp = badLikelihood;
	    }
	    else {
		// The morph was not defined: Don't accept!
		next L_LOOP;
	    }
	    currentDelta = delta[t - l] + logp;
	    if (currentDelta < bestDelta) {
		bestDelta = currentDelta;
		bestl = l;
	    }
	}
	delta[t] = bestDelta;
	psi[t] = bestl;
    }

    // Trace back
    my(@morphseq) = ();
    t = T;
    while (psi[t] != 0) {
	unshift @morphseq, substr(word, t - psi[t], psi[t]);
	t -= psi[t];
    }
    return @morphseq;
    }
}

*/
