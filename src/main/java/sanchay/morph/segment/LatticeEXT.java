/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;

//import edu.cmu.sphinx.result.*;
//import java.util.Iterator;
import java.util.Vector;


/**
 *
 * @author ram
 */
//public class LatticeEXT extends Lattice{
public class LatticeEXT {
//    /**
//     * adds all the letters of the given string to lattice
//     * NOTE:it assumes that initialNode & terminalNode are set!!!
//     * @param str
//     */
//     public void addToken(String str){
//        Vector letters = new Vector();
//        int i;
//
//        for(i=0;i<str.length();i++){
//            String id = str+i;
//            String word = str.charAt(i)+"";
//            letters.add(addNode(id,word,0,0));
//        }
//        addEdge(getInitialNode(),(Node)letters.get(0),-1,0);
//        for(i=0;i<letters.size()-1;i++){
//            addEdge((Node)letters.get(i),(Node)letters.get(i+1), -1, 0);
//        }
//        addEdge((Node)letters.get(str.length()-1),getTerminalNode(),-1,0);
//    }
//
//    /**
//     *this is not for any particular use
//     */
//     public void printAllNodes(){
//        System.err.println("Lattice has " + getNodes().size() + " nodes and " + getEdges().size() + " edges");
//         for ( String s : nodes.keySet()){
//            System.out.println(s+" "+nodes.get(s));
//        }
//     }
//
//     /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        LatticeEXT lattice = new LatticeEXT();
//        Node initial = lattice.addNode("^","^",0,0);
//        Node terminal = lattice.addNode("$","$",0,0);
//        lattice.setInitialNode(initial);
//        lattice.setTerminalNode(terminal);
//
//        lattice.addToken("ram");
//        lattice.addToken("rama");
//        lattice.addToken("raman");
//
//
//        LatticeOptimizer lo = new LatticeOptimizer(lattice);
//        lo.optimize();
//
//        lattice.dumpAllPaths();
//    }
//
}
