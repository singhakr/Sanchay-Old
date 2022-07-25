/*
 * MLClassLabels.java
 *
 * Created on September 10, 2008, 2:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.common;

import sanchay.properties.KeyValueProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class MLClassLabels {

    protected KeyValueProperties labels;
    protected KeyValueProperties revLabels;
    
    public static final int OUTSIDE = 0;
    public static final int BEGIN = 1;
    public static final int INTERMEDIATE = 3;
    public static final int END = 4;
    public static final int SINGLE = 5;

    public static final String UNDEFINED_LABEL = "UNK";

    /** Creates a new instance of MLClassLabels */
    public MLClassLabels() {
        labels = new KeyValueProperties(5, 5);
        revLabels = new KeyValueProperties(5, 5);
    }
    
    public int label2Int(String label)
    {
        return Integer.parseInt(labels.getPropertyValue("" + label));
    }
    
    public String int2Label(int index)
    {
        return revLabels.getPropertyValue("" + index);
    }
    
    public KeyValueProperties getLabel2IntMapping()
    {
        return labels;
    }
    
    public KeyValueProperties getInt2LabelMapping()
    {
        return revLabels;
    }    
    
    public void setLabels(KeyValueProperties labels)
    {
        this.labels = labels;
        revLabels = labels.getReverse();        
    }
    
    public void setRevLabels(KeyValueProperties rlabels)
    {
        revLabels = rlabels;
        labels = revLabels.getReverse();        
    }
    
    public boolean isOutside(String l)
    {
        if(l.startsWith("O-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isBeginning(String l)
    {
        if(l.startsWith("B-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isIntermediate(String l)
    {
        if(l.startsWith("I-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isEnd(String l)
    {
        if(l.startsWith("E-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isSingle(String l)
    {
        if(l.startsWith("S-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isOutside(int l)
    {
        String lbl = revLabels.getPropertyValue("" + l);
        
        if(lbl.equals("O")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isBeginning(int l)
    {
        String lbl = revLabels.getPropertyValue("" + l);
        
        if(lbl.startsWith("B-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isIntermediate(int l)
    {
        String lbl = revLabels.getPropertyValue("" + l);
        
        if(lbl.startsWith("I-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isEnd(int l)
    {
        String lbl = revLabels.getPropertyValue("" + l);
        
        if(lbl.startsWith("E-")) {
            return true;
        }
        
        return false;
    }
    
    public boolean isSingle(int l)
    {
        String lbl = revLabels.getPropertyValue("" + l);
        
        if(lbl.startsWith("S-")) {
            return true;
        }
        
        return false;
    }
    
    public int getBoundaryType(String l)
    {
        if(isOutside(l)) {
            return OUTSIDE;
        }
        
        if(isBeginning(l)) {
            return BEGIN;
        }
        
        if(isIntermediate(l)) {
            return INTERMEDIATE;
        }
        
        if(isEnd(l)) {
            return END;
        }
        
        return SINGLE;
    }
    
    public int getBoundaryType(int l)
    {
        if(isOutside(l)) {
            return OUTSIDE;
        }
        
        if(isBeginning(l)) {
            return BEGIN;
        }
        
        if(isIntermediate(l)) {
            return INTERMEDIATE;
        }
        
        if(isEnd(l)) {
            return END;
        }
        
        return SINGLE;
    }
}
