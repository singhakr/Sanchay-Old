/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author ram
 */
public class MorphInfoCollectionUsingHashV2 implements MorphInfoCollectionInterface,Serializable {
    Hashtable morphCollection;
    Enumeration enumeration;
    boolean flag;

    MorphInfoCollectionUsingHashV2(){
        enumeration = null;
        morphCollection = new Hashtable();
    }

    /**
     * @param argv
     */
    public static void main(String[] argv) {
        MorphInfoCollectionInterface mc = new MorphInfoCollectionUsingHashV2();
        Morph morph = null,tempMorph = null;
        MorphInfoInterface morphInfo = null;

        for (int i = 1; i <= 5; i++) {
            //System.err.println(i);
            morph = mc.getMorphFromString(Integer.toString(i));
            if(i==3)
                tempMorph = morph;
            //System.err.print(morph.getString() +"\n\n");
            morphInfo = new MorphInfo(i, i, i);
            mc.addMorph(morph, morphInfo);
        }

            morph = mc.getMorphFromString(Integer.toString(3));
            morphInfo = mc.getInfo(morph);
            morphInfo.setAll(100, 100, 100);


        while ((morph = mc.next()) != null) {
            morphInfo = mc.getInfo(morph);
            System.out.print(morph.getString() + " ");
            System.out.println(morphInfo.getMorphCount());
        }
    }

    public void addMorph(Morph morph, MorphInfoInterface morphInfo) {
        morphCollection.put(morph.getString(), morphInfo);
    }

    public void removeMorph(Morph morph) {
        morphCollection.remove(morph.getString());
    }

    public MorphInfoInterface getInfo(Morph morph) {
        if (!morphCollection.containsKey(morph.getString())) {
            return null;
        } else {
            return (MorphInfoInterface) morphCollection.get(morph.getString());
        }
    }

    public Morph next() {

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

    public void resetNext() {
        enumeration = morphCollection.keys();
        if (flag) {
            System.err.println("size of morphCollection is " + morphCollection.size());
        }
    }

    public Morph getMorphFromString(String str) {
        return new Morph(str);
    }

    public int size() {
        return morphCollection.size();
    }

    private Enumeration keys() {
        return morphCollection.keys();
    }
}
