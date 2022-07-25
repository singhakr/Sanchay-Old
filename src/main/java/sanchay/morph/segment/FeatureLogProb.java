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
public class FeatureLogProb {
    Hashtable flp =new Hashtable();
    Enumeration enumeration = null;
    boolean flag = false;

    Double get(String str) {
        if (!flp.containsKey(str)) {
            //return null;
            return 0.0;
        } else {
            return (Double) flp.get(str);

        }
    }

    public void put(String str, double d) {
        flp.put(str, d);
    }

    public boolean containsKey(Morph morph) {
        return flp.containsKey(morph.getString());
    }

    private void resetNext() {
        enumeration = flp.keys();
        if (flag) {
            System.err.println("size of morphAndDouble is " + flp.size());
        }
    }

    
    String next() {

        String feature =null;

        if (enumeration == null) {
            if (flag) {
                System.err.print("reseting ");
            }
            resetNext();
        }

        if (enumeration.hasMoreElements()) {
            feature = (String)enumeration.nextElement();
        }

        if(feature == null){
            enumeration = null;
        }

        return feature;
    }
}
