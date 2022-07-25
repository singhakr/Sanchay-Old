/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.morph.segment;

import java.io.Serializable;
import javax.swing.tree.MutableTreeNode;
import sanchay.corpus.ssf.tree.SSFLexItem;

/**
 *
 * @author ram
 */
public class Morph extends SSFLexItem implements MutableTreeNode, Serializable {

    String morph;
    
    /**
     * dummy constructor
     */
    Morph(){}
    
    /**
     * constructor that creates morph from string
     * @param w
     */
    Morph(String str){
        fromString(str);
    }

    /**
     * create morph from string
     * @param str 
     */
    public void fromString(String str) {
        morph = str;
    }

    /**
     * get back string from morph
     * @return string form of morph
     */
    public String getString() {
        return morph;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString(){
        return getString();
    }

    /**
     * gets the prefix part
     * @param beginIndex
     * @param endIndex
     * @return
     */
    public String split(int beginIndex, int endIndex) {
        if(morph.length() < endIndex)
        {
            System.err.println("Error:"+morph+" "+morph.length()+" "+endIndex);
            int a=9/0;
        }
        return morph.substring(beginIndex,endIndex);
    }

    /**
     * gets the suffix part
     * @param beginIndex
     * @return
     */
    public String split(int beginIndex) {
        return morph.substring(beginIndex);
    }

    public int length(){
        return morph.length();
    }

    /**
     *
     * @param a
     * @return
     */
    public boolean equals(Morph a){
       return a.getString().equals(this.getString());
    }
}
