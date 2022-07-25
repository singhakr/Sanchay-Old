/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author ram
 */
public class MorphAndDouble {
    Hashtable mnd =new Hashtable();
    Enumeration enumeration = null;
    boolean flag = false;

    boolean containsKey(Morph morph) {
        return mnd.containsKey(morph.getString());
    }

    Double get(Morph morph) {
        if (!mnd.containsKey(morph.getString())) {
            //return null;
            return 0.0;
        } else {
            return (Double) mnd.get(morph.getString());
        
        }
    }

    Morph next() {

        Morph morph =null;

        if (enumeration == null) {
            if (flag) {
                System.err.print("reseting ");
            }
            resetNext();
        }

        if (enumeration.hasMoreElements()) {
            morph = new Morph((String)enumeration.nextElement());
        }

        if(morph == null){
            enumeration = null;
        }

        return morph;
    }

    void put(Morph morph, double d) {
       mnd.put(morph.getString(),new Double(d));
    }

    private void resetNext() {
        enumeration = mnd.keys();
        if (flag) {
            System.err.println("size of morphAndDouble is " + mnd.size());
        }
    }

    public int size() {
        return mnd.size();
    }

    public static void main(String[] argv) {
        MorphAndDouble mnd = new MorphAndDouble();
        double d=1;
        Morph morph = new Morph("ram");
        mnd.put(morph, d);
        d=2;
        morph.fromString("ankur");
        mnd.put(morph, d);

        System.out.println(mnd.get(morph));
        morph.fromString("ram");
        System.out.println(mnd.get(morph));
    }

}
