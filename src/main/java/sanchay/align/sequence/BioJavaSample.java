/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.align.sequence;

/**
 *
 * @author anil
 */
public class BioJavaSample {

   /**
    * @param args
    * @throws Exception
    */
//   public static void main(String[] args) throws Exception {
//      // The alphabet of the sequences. For this example DNA is chosen.
//      FiniteAlphabet alphabet = (FiniteAlphabet) AlphabetManager
//            .alphabetForName("DNA");
//      // Use a substitution matrix with equal scores for every match and every
//      // replace.
//      int match = 1;
//      int replace = -1;
//      SubstitutionMatrix matrix = new SubstitutionMatrix(alphabet, match,
//            replace);
//      // Firstly, define the expenses (penalties) for every single operation.
//      int insert = 2;
//      int delete = 2;
//      int gapExtend = 2;
//      // Global alignment.
//      SequenceAlignment aligner = new NeedlemanWunsch(match, replace, insert,
//            delete, gapExtend, matrix);
//      Sequence query = DNATools.createDNASequence("GCCCTAGCG", "query");
//      Sequence target = DNATools.createDNASequence("GCGCAATG", "target");
//      // Perform an alignment and save the results.
//      aligner.pairwiseAlignment(query, // first sequence
//            target // second one
//            );
//      // Print the alignment to the screen
//      System.out.println("Global alignment with Needleman-Wunsch:\n"
//            + aligner.getAlignmentString());
//
//      // Perform a local alignment from the sequences with Smith-Waterman.
//      aligner = new SmithWaterman(match, replace, insert, delete, gapExtend,
//            matrix);
//      // Perform the local alignment.
//      aligner.pairwiseAlignment(query, target);
//      System.out.println("\nLocal alignment with Smith-Waterman:\n"
//            + aligner.getAlignmentString());
//   }
}
