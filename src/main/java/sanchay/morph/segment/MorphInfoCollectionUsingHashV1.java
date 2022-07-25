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
public class MorphInfoCollectionUsingHashV1 implements MorphInfoCollectionInterface {

    Hashtable morphCollection;
    Enumeration enumeration;
    boolean flag;

    public MorphInfoCollectionUsingHashV1() {
        flag = false;

        morphCollection = new Hashtable();
        enumeration = null;
    }

    public void addMorph(Morph morph, MorphInfoInterface morphInfo) {
        morphCollection.put(morph, morphInfo);
    }

    public MorphInfoInterface getInfo(Morph morph) {
        if (!morphCollection.containsKey(morph)) {
            return null;
        } else {
            return (MorphInfoInterface) morphCollection.get(morph);
        }
    }

    private Enumeration keys() {
        return morphCollection.keys();
    }

    public int size() {
        return morphCollection.size();
    }

    public void removeMorph(Morph morph) {
        morphCollection.remove(morph);
    }

    public void resetNext() {
        enumeration = morphCollection.keys();
        if (flag) {
            System.err.println("size of morphCollection is " + morphCollection.size());
        }
    }

    public static void main(String[] argv) {
        MorphInfoCollectionInterface mc = new MorphInfoCollectionUsingHashV1();
        Morph morph = null,tempMorph = null;
        MorphInfoInterface morphInfo = null;

        for (int i = 1; i <= 5; i++) {
            morph = mc.getMorphFromString(Integer.toString(i));
            if(i==3)
                tempMorph = morph;
            morphInfo = new MorphInfo();
            morphInfo.setAll(i, i, i);
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

    public Morph next(boolean reset) {
        if (reset || enumeration == null) {
            if (flag) {
                System.err.print("reseting ");
            }
            resetNext();
        }
        Morph skipNull = null;

        while (enumeration.hasMoreElements()) {
            skipNull = (Morph) enumeration.nextElement();
            if(skipNull != null){
                break;
            }
        }

        if(skipNull == null){
            enumeration = null;
        }
        
        return skipNull;

    }

    public Morph next() {
        return next(false);
    }

    /**
     *create if necessary
     * @param str
     * @return
     */
    public Morph getMorphFromString(String str) {

        Morph morph = null;

        //TODO how will i reduce complexity here?
        
        resetNext();
        while ((morph = next()) != null) {
            if (morph.getString().equals(str)) {
                break;
            }
        }
        
        if (morph == null) {
            morph = new Morph(str);
        }        
        resetNext();

        return morph;
    }
}
